@file:OptIn(ExperimentalCoroutinesApi::class)

package org.berendeev.weather.currentweather

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import org.berendeev.weather.LocationProvider
import org.berendeev.weather.network.ForecastDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentWeatherRepository @Inject constructor(
    private val forecastDatasource: ForecastDatasource,
    private val locationProvider: LocationProvider,
) {
    private val weatherLocationState = MutableStateFlow<WeatherLocation>(WeatherLocation.Current)

    val state: Flow<State> = weatherLocationState.flatMapLatest { weatherLocation ->
        flow {
            val initialState = State(
                weatherLocation = weatherLocation,
                weatherInfo = null
            )
            emit(initialState)
            val coordinates = when (weatherLocation) {
                WeatherLocation.Current -> {
                    locationProvider.getCurrentLocation().coordinates
                }

                is WeatherLocation.Fixed -> {
                    weatherLocation.coordinates
                }
            }

            val apiModel = forecastDatasource.fetchForecast(coordinates)
            emit(
                initialState.copy(
                    weatherInfo = WeatherInfo(
                        apiModel.current_weather.temperature
                    )
                )
            )
        }
    }

    fun setWeatherLocation(weatherLocation: WeatherLocation) {
        weatherLocationState.value = weatherLocation
    }

    fun update() {

    }

    data class State(
        val weatherLocation: WeatherLocation,
        val weatherInfo: WeatherInfo?,
    )

    data class WeatherInfo(
        val temperature: Float
    )
}
