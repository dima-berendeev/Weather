package org.berendeev.weather

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.berendeev.weather.currentweather.CurrentWeatherScreen
import org.berendeev.weather.selectplace.SelectPlaceScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home-screen") {
        fun pop() {
            navController.popBackStack()
        }
        composable("home-screen") {
            CurrentWeatherScreen(
                onCurrentCityClick = {
                    navController.navigate("select-city-screen")
                },
                viewModel = hiltViewModel()
            )
        }
        composable("select-city-screen") {
            SelectPlaceScreen(
                onPlaceSelected = { pop() },
                onClose = { pop() },
                onCurrentLocationSelected = { pop() },
                viewModel = hiltViewModel()
            )
        }
    }
}
