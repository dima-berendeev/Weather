package org.berendeev.weather.currentweather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    currentWeatherRepository: CurrentWeatherRepository
) : ViewModel() {

    val currentWeatherUiState: StateFlow<CurrentWeatherUiState?> = currentWeatherRepository.state.map {
        CurrentWeatherUiState(weatherLocation = it.weatherLocation, temperature = it.weatherInfo?.temperature)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

}

data class CurrentWeatherUiState(val weatherLocation: WeatherLocation, val temperature: Float?){

}