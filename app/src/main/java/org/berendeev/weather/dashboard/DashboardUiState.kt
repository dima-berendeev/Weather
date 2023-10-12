package org.berendeev.weather.dashboard

import org.berendeev.weather.data.model.ForecastData
import org.berendeev.weather.data.model.LocationMode

data class DashboardUiState(
    val locationMode: LocationMode? = null,
    val forecastUiState: ForecastUiState = ForecastUiState(),
    val requireLocationPermission: Boolean = false,
)

data class ForecastUiState(
    val forecastData: ForecastData? = null,
    val lastLoadFailed: Boolean = false,
    val refresh: (() -> Unit)? = null,
    val refreshing: Boolean = false,
)
