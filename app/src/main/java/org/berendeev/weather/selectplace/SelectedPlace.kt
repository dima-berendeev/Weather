package org.berendeev.weather.selectplace

import org.berendeev.weather.models.Coordinates

sealed interface SelectedPlace {
    data class FromSuggestions(val name: String, val coordinates: Coordinates) : SelectedPlace
    object CurrentLocation : SelectedPlace
}
