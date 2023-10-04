package org.berendeev.weather.selectplace

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.berendeev.weather.network.GeoCodingDataSource
import org.berendeev.weather.models.Coordinates
import javax.inject.Inject

@ViewModelScoped
class SuggestionsRepository @Inject constructor(
    private val geoCodingDataSource: GeoCodingDataSource,

    ) {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    suspend fun fetchVariants(query: String): List<Suggestion> {
        return withContext(ioDispatcher) {
            val apiModel = geoCodingDataSource.fetch(query)
            apiModel.results.map { createPlaceVariant(it) }
        }
    }

    private fun createPlaceVariant(it: GeoCodingDataSource.ApiModel.Result): Suggestion {
        return Suggestion(
            name = it.name,
            coordinates = Coordinates(it.latitude, it.longitude)
        )
    }

    data class Suggestion(val name: String, val coordinates: Coordinates)
}
