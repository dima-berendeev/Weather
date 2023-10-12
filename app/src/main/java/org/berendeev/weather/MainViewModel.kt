package org.berendeev.weather

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.berendeev.weather.data.LocationModeRepository
import org.berendeev.weather.data.model.LocationMode
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val locationModeRepository: LocationModeRepository) : ViewModel() {
    fun locationMode(locationMode: LocationMode) {
        locationModeRepository.setMode(locationMode)
    }
}
