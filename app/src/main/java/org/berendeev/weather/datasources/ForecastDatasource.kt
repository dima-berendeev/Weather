package org.berendeev.weather.datasources

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import javax.inject.Inject

class ForecastDatasource @Inject constructor(private val httpClient: HttpClient) {

    suspend fun fetchForecast(): ApiModel {
        // todo map exceptions
        return httpClient.get(
            "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41" +
                    "&hourly=temperature_2m" +
                    "&current_weather=true" +
                    "&forecast_days=1"
        ).body()
    }

    @Serializable
    class ApiModel(
        val current_weather: CurrentWeather
    ) {
        @Serializable
        class CurrentWeather(
            val time: String,
            val temperature: Float,
            val weathercode: Int,
            val windspeed: Float,
            val winddirection: Int,
        )
    }
}
