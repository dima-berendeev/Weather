@file:OptIn(FlowPreview::class)

package org.berendeev.weather.selectplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import org.berendeev.weather.models.Latitude
import org.berendeev.weather.models.Longitude
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SelectPlaceViewModel @Inject constructor(private val placesRepository: PlacesRepository) : ViewModel() {
    val queryStateFlow = MutableStateFlow("")

    val uiStateFlow: StateFlow<SelectPlaceUiState?> =
        merge(
            queryStateFlow.take(1),
            queryStateFlow.drop(1).debounce(2.seconds)
        )
            .map { query ->
                SelectPlaceUiState(
                    query = query,
                    variants = placesRepository.fetchVariants(query)
                        .map {
                            SelectPlaceUiState.PlaceVariant(it.name, it.latitude, it.longitude)
                        }
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

}

data class SelectPlaceUiState(val query: String, val variants: List<PlaceVariant>) {
    data class PlaceVariant(val name: String, val latitude: Latitude, val longitude: Longitude)
}
