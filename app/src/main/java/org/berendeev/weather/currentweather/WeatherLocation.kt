package org.berendeev.weather.currentweather

import org.berendeev.weather.selectplace.Place

sealed interface WeatherLocation {
    data class Fixed(val place: Place) : WeatherLocation
    object Current : WeatherLocation
}
