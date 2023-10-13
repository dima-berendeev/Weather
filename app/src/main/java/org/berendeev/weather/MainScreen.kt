package org.berendeev.weather

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.berendeev.weather.dashboard.DashboardRoute
import org.berendeev.weather.data.model.LocationMode
import org.berendeev.weather.devmenu.devMenuScreen
import org.berendeev.weather.devmenu.navigateToDevMenu
import org.berendeev.weather.selectplace.SelectPlaceRoute
import org.berendeev.weather.selectplace.SelectedPlace

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomAppBar {
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("dashboard") },
                    icon = {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigateToDevMenu() },
                    icon = {
                        Icon(Icons.Default.Build, contentDescription = "Developer menu")
                    }
                )
            }

        }
    ) { paddings ->
        Box(modifier = Modifier.padding(paddings)) {
            NavigationContainer(navController)
        }
    }

}

@Composable
private fun NavigationContainer(navController: NavHostController) {

    val mainViewModel: MainViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "dashboard") {
        fun pop() {
            navController.popBackStack()
        }
        composable("dashboard") {
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
        devMenuScreen()
    }
}
