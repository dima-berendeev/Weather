package org.berendeev.weather.data.model

import org.berendeev.weather.models.Coordinates

sealed interface LocationMode {
    data class Fixed(val name: String, val coordinates: Coordinates) : LocationMode
    object Current : LocationMode
}
