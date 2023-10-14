package org.berendeev.weather.data

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DevSettingsRepo @Inject constructor() {

    val state: MutableStateFlow<State?> = MutableStateFlow(State(true))

    fun updateUseFakeForecastRepo(enabled:Boolean) {
        state.value = state.value?.copy(useFakeForecastRepo = enabled)
    }

    data class State(val useFakeForecastRepo: Boolean)
}
