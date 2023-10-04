package org.berendeev.weather.models

sealed interface WeatherLocation {
    data class Fixed(val name: String, val coordinates: Coordinates) : WeatherLocation
    object Current : WeatherLocation
}
