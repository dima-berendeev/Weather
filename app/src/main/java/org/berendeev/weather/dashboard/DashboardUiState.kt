package org.berendeev.weather.dashboard

import org.berendeev.weather.data.model.ForecastData
import org.berendeev.weather.data.model.LocationMode

data class DashboardUiState(
    val locationMode: LocationMode? = null,
    val forecastUiState: ForecastUiState? = null,
    val requireLocationPermission: Boolean = false,
)

sealed interface ForecastUiState {
    data class Success(
        val forecastData: ForecastData,
        val refreshing: Boolean,
        val refresh: (() -> Unit),
        val lastLoadFailed: Boolean
    ) : ForecastUiState

    data class Error(
        val updating: Boolean,
        val update: () -> Unit
    ) : ForecastUiState
}
