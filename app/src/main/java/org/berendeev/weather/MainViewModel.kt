package org.berendeev.weather

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.berendeev.weather.data.ForecastRepository
import org.berendeev.weather.models.WeatherLocation
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val currentWeatherRepository: ForecastRepository) : ViewModel() {
    fun setWeatherLocation(weatherLocation: WeatherLocation) {
//        currentWeatherRepository.setWeatherLocation(weatherLocation)
    }
}
