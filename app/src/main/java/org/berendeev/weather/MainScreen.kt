package org.berendeev.weather

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.berendeev.weather.currentweather.CurrentWeatherScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home-screen") {
        composable("home-screen") {
            CurrentWeatherScreen(
                onCurrentCityClick = {
                    navController.navigate("select-city-screen")
                },
                viewModel = hiltViewModel()
            )
        }
        composable("select-city-screen") {
            SelectCityScreen()
        }
    }
}
