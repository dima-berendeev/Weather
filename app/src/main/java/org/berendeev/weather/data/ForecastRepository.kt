@file:OptIn(ExperimentalCoroutinesApi::class)

package org.berendeev.weather.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.berendeev.weather.data.model.ForecastData
import org.berendeev.weather.models.Coordinates
import javax.inject.Inject
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
    /**
     *
     * @return cashed according the rules
     */
    fun observe(coordinates: Coordinates): Flow<State>

    suspend fun refresh()

    data class State(
        val forecast: ForecastData?,
        val loadingFailed: Boolean,
    ) {
        val isInitialising get() = forecast == null && !loadingFailed
        val isStale get() = loadingFailed
    }
}

class FakeForecastRepository @Inject constructor() : ForecastRepository {
    override fun observe(coordinates: Coordinates): Flow<ForecastRepository.State> {
        return flow {
            emit(ForecastRepository.State(null, false))
            delay(4.seconds)
            emit(ForecastRepository.State(forecast = ForecastData(10.0f), false))
            delay(4.seconds)
            emit(ForecastRepository.State(forecast = ForecastData(10.0f), true))
            delay(4.seconds)
        }
    }

    override suspend fun refresh() {
        TODO("Not yet implemented")
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ForecastRepositoryRepoModule {
    @Binds
    abstract fun binds(impl: FakeForecastRepository): ForecastRepository
}
