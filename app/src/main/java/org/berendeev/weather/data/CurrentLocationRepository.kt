package org.berendeev.weather.data

import kotlinx.coroutines.flow.StateFlow
import org.berendeev.weather.models.Coordinates
import java.time.LocalDateTime

interface CurrentLocationRepository {
    val state: StateFlow<ForecastRepository.Result>
    fun refresh()

    sealed interface State {
        //todo outdated location.
        data class Success(val time: LocalDateTime, val coordinates: Coordinates) : State
        object NoPermission : State
        object Error : State
    }
}
