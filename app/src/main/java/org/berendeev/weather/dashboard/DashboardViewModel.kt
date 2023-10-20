package org.berendeev.weather.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.berendeev.weather.data.ForecastRepository
import org.berendeev.weather.data.LocationModeRepository
import org.berendeev.weather.data.model.LocationMode
import org.berendeev.weather.models.Coordinates
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
                            forecastUiStateFlow(mode.coordinates).map {
                                DashboardUiState(locationMode = mode, forecastUiState = it)
                            }
                        )
                    }

                    null -> {}
                }
            }
        }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    private fun forecastUiStateFlow(coordinates: Coordinates): Flow<ForecastUiState?> {
        val updatingState = MutableStateFlow<Boolean>(false)
        val mutex = Mutex()
        val request = ForecastRepository.Request(coordinates)
        val refresh: () -> Unit = {
            viewModelScope.launch {
                mutex.withLock {
                    updatingState.value = true
                    request.update()
                    updatingState.value = false
                }
            }
        }
        return combine(
            updatingState,
            forecastRepository.observe(request)
        ) { updating, state: ForecastRepository.Result? ->
            when (state) {
                is ForecastRepository.Result.Error -> {
                    ForecastUiState.Error(updating, refresh)
                }

                is ForecastRepository.Result.Success -> {
                    ForecastUiState.Success(
                        state.forecast,
                        updating, refresh,
                        state.lastUpdateFailed
                    )
                }

                null -> null
            }
        }
    }
}
