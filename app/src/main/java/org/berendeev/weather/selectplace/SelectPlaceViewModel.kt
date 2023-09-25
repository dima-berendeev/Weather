@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package org.berendeev.weather.selectplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import org.berendeev.weather.models.Coordinates
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SelectPlaceViewModel @Inject constructor(private val placesRepository: SuggestionsRepository) : ViewModel() {
    val queryStateFlow = MutableStateFlow(createInitQuery())

    private fun createInitQuery(): Query {
        return Query("") { text ->
            queryStateFlow.update { it.copy(text = text) }
        }
    }

    val selectedPlaceStateFlow: MutableStateFlow<SelectedPlace?> = MutableStateFlow(null)

    val uiStateFlow: StateFlow<SelectPlaceUiState?> =
        merge(
            queryStateFlow.map { it.text }.take(1),
            queryStateFlow.map { it.text }.drop(1).debounce(2.seconds)
        )
            .mapLatest { query ->
                SelectPlaceUiState(
                    query = query,
                    variants = createVariants(placesRepository.fetchVariants(query))
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private fun createVariants(repoSuggestions: List<SuggestionsRepository.Suggestion>): List<SelectPlaceUiState.Place> {
        val suggestionsUiState = repoSuggestions.map {
            SelectPlaceUiState.Place.FromSuggestions(
                it.name,
                it.coordinates
            ) {
                selectedPlaceStateFlow.value = SelectedPlace.FromSuggestions(it.name, it.coordinates)
            }
        }
        return buildList {
            add(
                SelectPlaceUiState.Place.CurrentLocation {
                    selectedPlaceStateFlow.value = SelectedPlace.CurrentLocation
                }
            )
            addAll(suggestionsUiState)
        }
    }
}

data class Query(val text: String, val onChanged: (String) -> Unit)

data class SelectPlaceUiState(val query: String, val variants: List<Place>, val selectedPlace: SelectedPlace? = null) {
    sealed interface Place {
        data class FromSuggestions(val name: String, val coordinates: Coordinates, val onClick: () -> Unit) : Place
        data class CurrentLocation(val onClick: () -> Unit) : Place
    }
}
