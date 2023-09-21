package org.berendeev.weather.currentweather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun CurrentWeatherScreen(
    onCurrentCityClick: () -> Unit,
    viewModel: CurrentWeatherViewModel,
    modifier: Modifier = Modifier,
) {
    viewModel.currentCity
    Column(modifier = modifier) {
        CurrentCity(name = viewModel.currentCity, onCurrentCityClick, Modifier.fillMaxWidth())
    }
}

@Composable
fun CurrentCity(name: String?, onCurrentCityClick: () -> Unit, modifier: Modifier) {
    Text(
        text = name ?: "Selected city",
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onCurrentCityClick()
            }
    )
}
