package org.berendeev.weather

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.berendeev.weather.dashboard.DashboardRoute
import org.berendeev.weather.data.model.LocationMode
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
            DashboardRoute(
                onCurrentCityClick = {
                    navController.navigate("select-city-screen")
                }
            )
        }
        composable("select-city-screen") {
            SelectPlaceRoute(
                onPlaceSelected = { place: SelectedPlace ->
                    mainViewModel.locationMode(
                        when (place) {
                            SelectedPlace.CurrentLocation -> LocationMode.Current
                            is SelectedPlace.FromSuggestions -> LocationMode.Fixed(place.name, place.coordinates)
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
