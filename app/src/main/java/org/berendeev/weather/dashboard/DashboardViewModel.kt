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
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
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

    val currentWeatherUiState: StateFlow<DashboardUiState> = modeRepository.state.flatMapLatest { mode ->
        flow {
            emit(DashboardUiState(locationMode = mode))
            when (mode) {
                LocationMode.Current -> TODO()
                is LocationMode.Fixed -> {
                    emitAll(
                        getWeatherDetailsUiStateForLocation(mode.coordinates)
                            .map { DashboardUiState(mode, it) }
                    )
                }
            }

        }
    }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    private fun getWeatherDetailsUiStateForLocation(coordinates: Coordinates): Flow<ForecastUiState> {
        val forceUpdateFlow = Channel<Boolean>() {
            Log.e("WeatherInfoViewModel", "update event was not delivered")
        }
        val update: () -> Unit = { forceUpdateFlow.trySend(true) }
        return forceUpdateFlow.consumeAsFlow()
            .onStart { emit(false) }
            .flatMapLatest { forceUpdate ->
                forecastRepository.observe(coordinates, forceUpdate)
            }
            .mapForecastStateToUi(update)
    }

    private fun Flow<ForecastRepository.State>.mapForecastStateToUi(update: () -> Unit): Flow<ForecastUiState> {
        return map { repoState: ForecastRepository.State ->
            when (repoState) {
                is ForecastRepository.State.Empty -> ForecastUiState.Loading
                is ForecastRepository.State.Success -> ForecastUiState.Success(repoState.forecast, update)
                is ForecastRepository.State.Error -> ForecastUiState.Error(repoState.error, update)
                is ForecastRepository.State.Stale -> ForecastUiState.Stale(repoState.forecast, repoState.error, update)
            }
        }
    }
}


