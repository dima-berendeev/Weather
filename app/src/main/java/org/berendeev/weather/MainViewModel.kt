package org.berendeev.weather

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.berendeev.weather.common.ApplicationCoroutineScope
import org.berendeev.weather.data.ForecastRepository
import org.berendeev.weather.data.LocationModeRepository
import org.berendeev.weather.data.model.LocationMode
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val locationModeRepository: LocationModeRepository,
    private val forecastRepository: ForecastRepository,
    @ApplicationCoroutineScope private val applicationCoroutineScope: CoroutineScope
) : ViewModel() {
    fun locationMode(locationMode: LocationMode) {
        applicationCoroutineScope.launch {
            locationModeRepository.setMode(locationMode)
            when (locationMode) {
                LocationMode.Current -> {}
                is LocationMode.Fixed -> forecastRepository.setCoordinates(locationMode.coordinates)
            }
        }
    }
}
