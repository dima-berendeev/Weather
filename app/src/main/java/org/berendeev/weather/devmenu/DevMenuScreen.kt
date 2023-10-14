package org.berendeev.weather.devmenu

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DevMenuRouter() {
    Column {
        Text("Developer menu")
        val vm: DevMenuViewModel = hiltViewModel()
        val uiState: DevMenuUiState = vm.state.collectAsStateWithLifecycle().value ?: return
        Checkbox(checked = uiState.useFakeForecastRepository, onCheckedChange = { uiState.update(it) })
    }
}
