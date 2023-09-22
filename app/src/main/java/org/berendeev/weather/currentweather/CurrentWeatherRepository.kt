package org.berendeev.weather.currentweather

import android.location.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.berendeev.weather.datasources.ForecastDatasource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentWeatherRepository @Inject constructor(private val forecastDatasource: ForecastDatasource) {

    val state: Flow<CurrentWeather> = flow {
        emit(forecastDatasource.fetchForecast().mapModel())
    }

    fun setLocation(location: Location) {

    }

    fun update() {

    }
}

private fun ForecastDatasource.ApiModel.mapModel(): CurrentWeather {
    return CurrentWeather(
        temperature = this.current_weather.temperature
    )
}

