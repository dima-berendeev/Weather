@file:OptIn(ExperimentalCoroutinesApi::class)

package org.berendeev.weather.currentweather

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import org.berendeev.weather.datasources.ForecastDatasource
import org.berendeev.weather.selectplace.Place
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentWeatherRepository @Inject constructor(private val forecastDatasource: ForecastDatasource) {
    private val weatherLocationState = MutableStateFlow<WeatherLocation>(WeatherLocation.Current)

    val state: Flow<State> = weatherLocationState.flatMapLatest { weatherLocation ->
        flow {
            val initialState = State(
                weatherLocation = weatherLocation,
                weatherInfo = null
            )
            emit(initialState)
            when (weatherLocation) {
                WeatherLocation.Current -> {

                }

                is WeatherLocation.Fixed -> {
                    val apiModel = forecastDatasource.fetchForecast(
                        weatherLocation.place.latitude,
                        weatherLocation.place.longitude
                    )
                    emit(
                        initialState.copy(
                            weatherInfo = WeatherInfo(
                                apiModel.current_weather.temperature
                            )
                        )
                    )
                }
            }
        }

    }

    fun usePlace(place: Place) {
        weatherLocationState.value = WeatherLocation.Fixed(place)
    }

    fun useCurrentLocation() {
        weatherLocationState.value = WeatherLocation.Current
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
