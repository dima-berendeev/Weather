package org.berendeev.weather

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.berendeev.weather.dashboard.CurrentWeatherRoute
import org.berendeev.weather.models.WeatherLocation
import org.berendeev.weather.selectplace.SelectPlaceRoute
import org.berendeev.weather.selectplace.SelectedPlace

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = "current-weather-screen") {
        fun pop() {
            navController.popBackStack()
        }
        composable("current-weather-screen") {
            CurrentWeatherRoute(
                onCurrentCityClick = {
                    navController.navigate("select-city-screen")
                }
            )
        }
        composable("select-city-screen") {
            SelectPlaceRoute(
                onPlaceSelected = { place: SelectedPlace ->
                    mainViewModel.setWeatherLocation(
                        when (place) {
                            SelectedPlace.CurrentLocation -> WeatherLocation.Current
                            is SelectedPlace.FromSuggestions -> WeatherLocation.Fixed(place.name, place.coordinates)
                        }
                    )
                    pop()
                },
                onClose = {
                    pop()
                }
            )
        }
    }
}
