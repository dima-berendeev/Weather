package org.berendeev.weather.selectplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.berendeev.weather.models.Latitude
import org.berendeev.weather.models.Longitude
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SelectPlaceViewModel @Inject constructor(private val placesRepository: PlacesRepository) : ViewModel() {
    val queryStateFlow = MutableStateFlow("")

    val uiStateFlow: StateFlow<UiState?> = queryStateFlow
        .debounce(2.seconds)
        .map { query ->
        UiState(
            query = query,
            variants = placesRepository.fetchVariants(query)
                .map {
                    PlaceVariant(it.name, it.latitude, it.longitude)
                }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    data class UiState(val query: String, val variants: List<PlaceVariant>)

    data class PlaceVariant(val name: String, val latitude: Latitude, val longitude: Longitude)
}
