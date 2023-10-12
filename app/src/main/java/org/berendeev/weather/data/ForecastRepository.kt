@file:OptIn(ExperimentalCoroutinesApi::class)

package org.berendeev.weather.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.berendeev.weather.common.ApplicationCoroutineScope
import org.berendeev.weather.data.model.ForecastData
import org.berendeev.weather.models.Coordinates
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

//@Singleton
//class ForecastRepository @Inject constructor(
//    private val forecastDatasource: ForecastDatasource,
//    private val locationProvider: LocationProvider,
//) {
//    private val weatherLocationState = MutableStateFlow<WeatherLocation>(WeatherLocation.Current)
//
//    val state: Flow<State> = weatherLocationState.flatMapLatest { weatherLocation ->
//        flow {
//            val initialState = State(
//                weatherLocation = weatherLocation,
//                weatherInfo = null
//            )
//            emit(initialState)
//            val coordinates = when (weatherLocation) {
//                WeatherLocation.Current -> {
//                    locationProvider.getCurrentLocation().coordinates
//                }
//
//                is WeatherLocation.Fixed -> {
//                    weatherLocation.coordinates
//                }
//            }
//
//            val apiModel = forecastDatasource.fetchForecast(coordinates)
//            emit(
//                initialState.copy(
//                    weatherInfo = WeatherInfo(
//                        apiModel.current_weather.temperature
//                    )
//                )
//            )
//        }
//    }
//
//    fun setWeatherLocation(weatherLocation: WeatherLocation) {
//        weatherLocationState.value = weatherLocation
//    }
//
//    fun update() {
//
//    }
//
//    data class State(
//        val weatherLocation: WeatherLocation,
//        val weatherInfo: WeatherInfo?,
//    )
//
//    data class WeatherInfo(
//        val temperature: Float
//    )
//}

interface ForecastRepository {
    val state: Flow<State>

    suspend fun setCoordinates(coordinates: Coordinates)

    suspend fun refresh()

    data class State(
        val forecast: ForecastData?,
        val loadingFailed: Boolean,
    ) {
        val isInitialising get() = forecast == null && !loadingFailed
        val isStale get() = loadingFailed
    }
}

class FakeForecastRepository @Inject constructor(@ApplicationCoroutineScope private val coroutineScope: CoroutineScope) : ForecastRepository {

    private val rnd = Random(System.currentTimeMillis())
    private var temperature = 0.0


    private val mutex = Mutex()
    private var initialized = false
    override val state: MutableStateFlow<ForecastRepository.State> =
        MutableStateFlow(ForecastRepository.State(null, false))

    init {
        state
            .onSubscription {
                ensureInitializing()
            }.launchIn(coroutineScope)
    }

    private suspend fun ensureInitializing() {
        withContext(Dispatchers.Default) {
            mutex.withLock {
                if (!initialized) {
                    initialized = true
                    coroutineScope.launch {
                        updateTemperature()
                        delay(2.seconds)
                        state.value = ForecastRepository.State(forecast = ForecastData(temperature.toFloat()), false)
                    }
                }
            }
        }
    }

    private fun updateTemperature() {
        temperature = rnd.nextDouble(0.0, 20.0).roundToInt().toDouble()
    }

    override suspend fun setCoordinates(coordinates: Coordinates) {
        TODO("Not yet implemented")
    }

    override suspend fun refresh() {
        withContext(Dispatchers.Default) {
            mutex.withLock {
                updateTemperature()
                delay(2.seconds)
                state.value = ForecastRepository.State(forecast = ForecastData(temperature.toFloat()), false)
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ForecastRepositoryRepoModule {
    @Binds
    abstract fun binds(impl: FakeForecastRepository): ForecastRepository
}
