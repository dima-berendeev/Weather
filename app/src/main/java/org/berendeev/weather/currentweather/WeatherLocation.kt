package org.berendeev.weather.currentweather

import org.berendeev.weather.models.Coordinates

sealed interface WeatherLocation {
    data class Fixed(val name: String, val coordinates: Coordinates) : WeatherLocation
    object Current : WeatherLocation
}
