@file:OptIn(ExperimentalCoroutinesApi::class)

package org.berendeev.weather.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.berendeev.weather.common.ApplicationCoroutineScope
import org.berendeev.weather.data.model.ForecastData
import org.berendeev.weather.models.Coordinates
import org.berendeev.weather.network.ForecastDatasource
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

interface ForecastRepository {
    /**
     * state returns null if repo is not initialize
     * state launch initialization after first subscription is registered
     */
    val state: Flow<State?>

    suspend fun setCoordinates(coordinates: Coordinates)

    suspend fun refresh()

    sealed interface State {
        object Error : State
        data class Success(val forecast: ForecastData, val lastUpdateFailed: Boolean) : State
    }
}

@Singleton
class ForecastRepositoryImpl @Inject constructor(
    private val forecastDatasource: ForecastDatasource,
    @ApplicationCoroutineScope private val coroutineScope: CoroutineScope,
) : ForecastRepository {
    override val state: MutableStateFlow<ForecastRepository.State?> = MutableStateFlow(null)

    private val mutex = Mutex()
    private var initialized = AtomicBoolean(false)
    private var coordinates: Coordinates? = null

    init {
        state
            .onSubscription {
                ensureInitialization()
            }.launchIn(coroutineScope)
    }

    private suspend fun ensureInitialization() {
        if (!initialized.getAndSet(true)) {
            coroutineScope.launch {
                refresh()
            }
        }
    }

    override suspend fun setCoordinates(coordinates: Coordinates) {
        mutex.withLock {
            this.coordinates = coordinates
            state.value = null
            loadNewValues()
        }
    }

    override suspend fun refresh() {
        mutex.withLock {
            loadNewValues()
        }
    }

    private suspend fun loadNewValues() {
        val coordinates = coordinates ?: return

        try {
            val apiModel = withContext(Dispatchers.Default) {
                forecastDatasource.fetchForecast(coordinates)
            }
            state.value = ForecastRepository.State.Success(ForecastData(apiModel.current_weather.temperature), false)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            val prevValue = state.value
            state.value = when (prevValue) {
                ForecastRepository.State.Error -> ForecastRepository.State.Error
                is ForecastRepository.State.Success -> prevValue.copy(lastUpdateFailed = true)
                null -> ForecastRepository.State.Error
            }
        }
    }
}

class ForecastRepositoryProxy @Inject constructor(
    private val forecastRepository: ForecastRepositoryImpl,
    private val fakeForecastRepository: FakeForecastRepository,
    private val settingsRepo: DevSettingsRepo
) : ForecastRepository {
    override val state: Flow<ForecastRepository.State?> = settingsRepo.state.filterNotNull().flatMapLatest {
        if (it.useFakeForecastRepo) {
            fakeForecastRepository.state
        } else {
            forecastRepository.state
        }
    }

    override suspend fun setCoordinates(coordinates: Coordinates) {
        if (useFakeForecastRepo()) {
            fakeForecastRepository.setCoordinates(coordinates)
        } else {
            forecastRepository.setCoordinates(coordinates)
        }
    }

    private suspend fun useFakeForecastRepo() = settingsRepo.state.filterNotNull().first().useFakeForecastRepo

    override suspend fun refresh() {
        if (useFakeForecastRepo()) {
            fakeForecastRepository.refresh()
        } else {
            forecastRepository.refresh()
        }
    }
}

class FakeForecastRepository @Inject constructor(@ApplicationCoroutineScope private val coroutineScope: CoroutineScope) : ForecastRepository {

    private val rnd = Random(System.currentTimeMillis())
    private var temperature = 0.0


    private val mutex = Mutex()
    private var initialized = AtomicBoolean(false)
    override val state: MutableStateFlow<ForecastRepository.State?> = MutableStateFlow(null)

    init {
        state
            .onSubscription {
                ensureInitializing()
            }.launchIn(coroutineScope)
    }

    private suspend fun ensureInitializing() {
        mutex.withLock {
            if (!initialized.getAndSet(true)) {
                coroutineScope.launch {
                    refresh()
                }
            }
        }
    }

    private fun updateTemperature() {
        temperature = rnd.nextDouble(0.0, 20.0).roundToInt().toDouble()
    }

    override suspend fun setCoordinates(coordinates: Coordinates) {
        mutex.withLock {
            state.value = null
            loadNewValues()
        }
    }

    override suspend fun refresh() {
        mutex.withLock {
            loadNewValues()
        }
    }

    private suspend fun loadNewValues() {
        withContext(Dispatchers.Default) {
            delay(2.seconds)
            updateTemperature()
            state.value = ForecastRepository.State.Success(ForecastData(temperature.toFloat()), false)
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ForecastRepositoryRepoModule {
    @Binds
    abstract fun binds(impl: ForecastRepositoryProxy): ForecastRepository
}
