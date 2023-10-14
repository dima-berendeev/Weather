package org.berendeev.weather.devmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.berendeev.weather.data.DevSettingsRepo
import javax.inject.Inject

@HiltViewModel
class DevMenuViewModel @Inject constructor(private val devSettingsRepo: DevSettingsRepo) : ViewModel() {
    val state: StateFlow<DevMenuUiState?> = devSettingsRepo.state.filterNotNull().map {
        DevMenuUiState(
            useFakeForecastRepository = it.useFakeForecastRepo,
            update = { checked ->
                devSettingsRepo.updateUseFakeForecastRepo(checked)
            }
        )
    }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5_000), null)

}

data class DevMenuUiState(val useFakeForecastRepository: Boolean, val update: (Boolean) -> Unit)
