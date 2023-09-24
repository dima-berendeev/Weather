package org.berendeev.weather.currentweather

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat

@Composable
fun CurrentWeatherScreen(
    onCurrentCityClick: () -> Unit,
    viewModel: CurrentWeatherViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState = viewModel.currentWeatherUiState.collectAsState().value ?: return
    val weatherLocation = uiState.weatherLocation
    val temperature = uiState.temperature

    Column(modifier = modifier.fillMaxSize()) {
        val weatherLocationName = when (weatherLocation) {
            WeatherLocation.Current -> "Current"
            is WeatherLocation.Fixed -> weatherLocation.name
        }

        CurrentCity(name = weatherLocationName, onCurrentCityClick, Modifier.fillMaxWidth())
        val permissionResultLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { it: Boolean ->
            it
        }
        ContextCompat.checkSelfPermission(LocalContext.current, Manifest.permission.ACCESS_COARSE_LOCATION)

        Button(onClick = {
            permissionResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }) {
            Text(text = "Give location permission")
        }

        Text(
            text = temperature?.toString() ?: "?????",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
            modifier = modifier
                .wrapContentSize()
                .clickable {
                    onCurrentCityClick()
                }
        )
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
