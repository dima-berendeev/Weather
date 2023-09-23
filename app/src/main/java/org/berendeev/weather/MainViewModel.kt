package org.berendeev.weather

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.berendeev.weather.currentweather.CurrentWeatherRepository
import org.berendeev.weather.selectplace.Place
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val currentWeatherRepository: CurrentWeatherRepository) : ViewModel() {
    fun usePlace(place: Place) {
        currentWeatherRepository.usePlace(place)
    }

    fun useCurrentLocation() {
        currentWeatherRepository.useCurrentLocation()
    }
}
