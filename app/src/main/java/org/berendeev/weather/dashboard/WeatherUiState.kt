package org.berendeev.weather.dashboard

import org.berendeev.weather.data.model.ForecastData
import org.berendeev.weather.data.model.LocationMode

data class WeatherUiState(
    val locationMode: LocationMode? = null,
    val forecastUiState: ForecastUiState = ForecastUiState.Loading,
    val requireLocationPermission: Boolean = false,
    val hasNetworkIssue: Boolean = false,
)

sealed interface ForecastUiState {
    object Loading : ForecastUiState
    data class Error(val error: Throwable, val update: () -> Unit) : ForecastUiState
    data class Stale(val forecastData: ForecastData, val error: Throwable?, val update: () -> Unit) : ForecastUiState
    data class Success(val forecastData: ForecastData, val update: () -> Unit) : ForecastUiState
}
