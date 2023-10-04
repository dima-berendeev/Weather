package org.berendeev.weather

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.berendeev.weather.data.CurrentWeatherRepository
import org.berendeev.weather.currentweather.WeatherLocation
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val currentWeatherRepository: CurrentWeatherRepository) : ViewModel() {
    fun setWeatherLocation(weatherLocation: WeatherLocation) {
        currentWeatherRepository.setWeatherLocation(weatherLocation)
    }
}
