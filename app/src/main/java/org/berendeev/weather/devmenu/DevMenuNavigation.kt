package org.berendeev.weather.devmenu

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val devMenuRoute = "dev_menu_route"

fun NavController.navigateToDevMenu(navOptions: NavOptions? = null) {
    this.navigate(devMenuRoute, navOptions)
}

fun NavGraphBuilder.devMenuScreen(
) {
    composable(route = devMenuRoute) {
        DevMenuRouter()
    }
}
