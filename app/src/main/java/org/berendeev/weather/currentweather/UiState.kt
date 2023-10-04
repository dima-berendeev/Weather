package org.berendeev.weather.currentweather

import org.berendeev.weather.models.WeatherLocation

data class CurrentWeatherUiState(val weatherLocation: WeatherLocation? = null, val temperature: Float? = null)
