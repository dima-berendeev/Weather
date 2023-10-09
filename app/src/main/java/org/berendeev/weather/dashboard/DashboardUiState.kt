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
    val isUpdating: Boolean = false,
    val updateFailed: Boolean = false,
    val update: (() -> Unit)? = null
)
