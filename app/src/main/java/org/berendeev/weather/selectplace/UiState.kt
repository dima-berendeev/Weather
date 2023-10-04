package org.berendeev.weather.selectplace

import org.berendeev.weather.models.Coordinates

sealed interface PlaceVariantUiState {
    data class Geo(val name: String, val coordinates: Coordinates, val onClick: () -> Unit) : PlaceVariantUiState
    data class CurrentLocation(val onClick: () -> Unit) : PlaceVariantUiState
}

data class SelectPlaceUiState(val query: String, val variants: List<PlaceVariantUiState>, val selectedPlace: SelectedPlace? = null)
