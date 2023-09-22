package org.berendeev.weather.datasources

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import javax.inject.Inject

class GeoCodingDataSource @Inject constructor(private val httpClient: HttpClient) {
    suspend fun fetch(name: String): ApiModel {
        if(name.isBlank()){
            return ApiModel()
        }
        return httpClient.get("https://geocoding-api.open-meteo.com/v1/search?count=10&language=en&format=json") {
            url {
                parameters.apply {
                        append("name", name)
                }
            }
        }.body()
    }

    @Serializable
    data class ApiModel(val results: List<Result> = emptyList()) {

        @Serializable
        data class Result(val name: String, val latitude: Double, val longitude: Double)
    }
}
