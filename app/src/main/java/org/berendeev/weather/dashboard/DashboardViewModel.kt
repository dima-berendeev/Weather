package org.berendeev.weather.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import org.berendeev.weather.data.ForecastRepository
import org.berendeev.weather.data.LocationModeRepository
import org.berendeev.weather.data.model.LocationMode
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val modeRepository: LocationModeRepository,
    private val forecastRepository: ForecastRepository,
//    private val locationRepository: CurrentLocationRepository,
) : ViewModel() {

    val currentWeatherUiState: StateFlow<DashboardUiState> = modeRepository.state
        .flatMapLatest { mode ->
            flow {
                emit(DashboardUiState(locationMode = mode))
                when (mode) {
                    LocationMode.Current -> TODO()
                    is LocationMode.Fixed -> {
                        emitAll(
                            forecastUiStateFlow().map {
                                DashboardUiState(locationMode = mode, forecastUiState = it)
                            }
                        )
                    }

                    null -> {}
                }
            }
        }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    private fun forecastUiStateFlow(): Flow<ForecastUiState?> {
        val refreshFlow = Channel<Unit>(capacity = 1) {
            Log.e("WeatherInfoViewModel", "update event was not delivered")
        }

        val refresh: () -> Unit = { refreshFlow.trySend(Unit) }

        val refreshingFlow = refreshFlow
            .receiveAsFlow()
            .flatMapConcat {
                flow {
                    emit(true)
                    forecastRepository.refresh()
                    emit(false)
                }
            }.onStart { emit(false) }

        return combine(
            refreshingFlow,
            forecastRepository.state
        ) { refreshing, forecastState ->
            when (forecastState) {
                is ForecastRepository.State.Success -> {
                    ForecastUiState.Success(
                        forecastData = forecastState.forecast,
                        lastLoadFailed = forecastState.lastUpdateFailed,
                        refresh = refresh,
                        refreshing = refreshing
                    )
                }

                ForecastRepository.State.Error -> {
                    ForecastUiState.Error(refreshing, refresh)
                }

                null -> null
            }
        }
    }
}
