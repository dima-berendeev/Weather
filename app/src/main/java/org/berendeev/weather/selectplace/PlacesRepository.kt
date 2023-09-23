package org.berendeev.weather.selectplace

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.berendeev.weather.datasources.GeoCodingDataSource
import org.berendeev.weather.models.Latitude
import org.berendeev.weather.models.Longitude
import javax.inject.Inject

@ViewModelScoped
class PlacesRepository @Inject constructor(
    private val geoCodingDataSource: GeoCodingDataSource,

    ) {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    suspend fun fetchVariants(query: String): List<PlaceVariant> {
        return withContext(ioDispatcher) {
            val apiModel = geoCodingDataSource.fetch(query)
            apiModel.results.map { createPlaceVariant(it) }
        }
    }

    private fun createPlaceVariant(it: GeoCodingDataSource.ApiModel.Result) =
        PlaceVariant(name = it.name, latitude = Latitude(it.latitude), longitude = Longitude(it.longitude))


    data class PlaceVariant(val name: String, val latitude: Latitude, val longitude: Longitude)
}
