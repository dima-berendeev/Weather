package org.berendeev.weather.currentweather

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(val currentWeatherRepository: CurrentWeatherRepository) : ViewModel() {

    val temperature: Float?
        @Composable
        get() = currentWeatherRepository.state.collectAsState(initial = null).value?.temperature

}