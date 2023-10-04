@file:OptIn(ExperimentalMaterial3Api::class)

package org.berendeev.weather.currentweather

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import org.berendeev.weather.models.WeatherLocation

@Composable
fun CurrentWeatherRoute(
    viewModel: CurrentWeatherViewModel = hiltViewModel(),
    onCurrentCityClick: () -> Unit
) {
    val uiState = viewModel.currentWeatherUiState.collectAsState().value
    CurrentWeatherScreen(uiState, onCurrentCityClick = onCurrentCityClick)
}

@Composable
fun CurrentWeatherScreen(
    uiState: CurrentWeatherUiState,
    onCurrentCityClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val weatherLocation = uiState.weatherLocation
    val temperature = uiState.temperature

    Column(modifier = modifier.fillMaxSize()) {
        val weatherLocationName = when (weatherLocation) {
            WeatherLocation.Current -> "Current"
            is WeatherLocation.Fixed -> weatherLocation.name
            null -> "???"
        }

        SearchBar(name = weatherLocationName, onCurrentCityClick, Modifier.fillMaxWidth())
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(name: String?, onCurrentCityClick: () -> Unit, modifier: Modifier) {
    TopAppBar(
        title = {
            Box(
                modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp)
                    .heightIn(min = 56.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(CornerSize(50)))
                    .clickable {
                        onCurrentCityClick()
                    }) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = name ?: "Selected city",
                    style = MaterialTheme.typography.headlineLarge,

                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

        }
    )
}
