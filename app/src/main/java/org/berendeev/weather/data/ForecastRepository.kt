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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.berendeev.weather.common.ApplicationCoroutineScope
import org.berendeev.weather.data.model.ForecastData
import org.berendeev.weather.models.Coordinates
import org.berendeev.weather.network.ForecastDatasource
import javax.inject.Inject
import javax.inject.Singleton

interface ForecastRepository {
    fun observe(coordinates: Coordinates): Flow<State?>

    sealed interface State {
        data class Error(val update: suspend () -> Unit) : State
        data class Success(val forecast: ForecastData, val lastUpdateFailed: Boolean, val update: suspend () -> Unit) : State
    }
}

@Singleton
class ForecastRepositoryImpl @Inject constructor(
    private val forecastDatasource: ForecastDatasource,
    @ApplicationCoroutineScope private val coroutineScope: CoroutineScope,
) : ForecastRepository {

    override fun observe(coordinates: Coordinates): Flow<ForecastRepository.State?> {
        val state = MutableStateFlow<ForecastRepository.State?>(null)
        val mutex = Mutex()

        suspend fun load() {
            if (mutex.isLocked) return
            mutex.withLock {
                try {
                    val apiModel: ForecastDatasource.ApiModel = withContext(Dispatchers.IO) {
                        forecastDatasource.fetchForecast(coordinates)
                    }
                    state.value = ForecastRepository.State.Success(ForecastData(apiModel.current_weather.temperature), false) {
                        load()
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Throwable) {
                    val prevValue = state.value
                    state.value = when (prevValue) {
                        is ForecastRepository.State.Error, null -> ForecastRepository.State.Error {
                            load()
                        }

                        is ForecastRepository.State.Success -> prevValue.copy(lastUpdateFailed = true)
                    }
                }
            }
        }
        coroutineScope.launch {
            load()
        }

        return state
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ForecastRepositoryRepoModule {
    @Binds
    @Singleton
    abstract fun binds(impl: ForecastRepositoryImpl): ForecastRepository
}
