@file:OptIn(ExperimentalMaterial3Api::class)

package org.berendeev.weather.dashboard

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.berendeev.weather.data.model.ForecastData
import org.berendeev.weather.data.model.LocationMode

@Composable
fun DashboardRoute(
    viewModel: DashboardViewModel = hiltViewModel(),
    onCurrentCityClick: () -> Unit
) {
    val uiState = viewModel.currentWeatherUiState.collectAsState().value
    DashboardScreen(uiState, onCurrentCityClick = onCurrentCityClick)
}

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onCurrentCityClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(modifier = modifier.fillMaxSize()) {
        val locationMode = uiState.locationMode ?: return // show empty

        SearchBar(locationMode, onCurrentCityClick, Modifier.fillMaxWidth())
//        val permissionResultLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { it: Boolean ->
//            it
//        }
//        ContextCompat.checkSelfPermission(LocalContext.current, Manifest.permission.ACCESS_COARSE_LOCATION)
//
//        Button(onClick = {
//            permissionResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
//        }) {
//            Text(text = "Give location permission")
//        }

        Forecast(
            uiState.forecastUiState,
            Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
private fun Forecast(uiState: ForecastUiState, modifier: Modifier = Modifier) {
    when (uiState) {
        ForecastUiState.Loading -> {
            CircularProgressIndicator(modifier.wrapContentSize(Alignment.Center))
        }

        is ForecastUiState.Error -> {
            Text("Error")
        }

        is ForecastUiState.Stale -> Forecast(uiState.forecastData, uiState.update, modifier)
        is ForecastUiState.Success -> Forecast(uiState.forecastData, uiState.update, modifier)
    }
}

@Composable
private fun Forecast(forecastData: ForecastData, update: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = forecastData.temperature.toString(),
        style = MaterialTheme.typography.displayLarge,
        textAlign = TextAlign.Center,
        modifier = modifier
            .wrapContentSize(Alignment.Center)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(locationMode: LocationMode, onCurrentCityClick: () -> Unit, modifier: Modifier) {

    val locationName = when (locationMode) {
        LocationMode.Current -> "Current"
        is LocationMode.Fixed -> locationMode.name
    }

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
                    text = locationName,
                    style = MaterialTheme.typography.headlineLarge,

                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    )
}
