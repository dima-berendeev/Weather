package org.berendeev.weather.currentweather

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CurrentWeatherViewModel @Inject constructor() : ViewModel() {
    val currentCity = "Amsterdam"

}