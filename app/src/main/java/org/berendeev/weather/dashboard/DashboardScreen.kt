@file:OptIn(ExperimentalMaterialApi::class)

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
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

    Box(
        modifier = modifier
            .fillMaxSize()

    ) {
        Column(Modifier.fillMaxSize()) {
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
                Modifier.fillMaxSize()
            )
        }
    }
}


@Composable
private fun SearchBar(locationMode: LocationMode, onCurrentCityClick: () -> Unit, modifier: Modifier) {
    val locationName = when (locationMode) {
        LocationMode.Current -> "Current"
        is LocationMode.Fixed -> locationMode.name
    }

    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp)
            .heightIn(min = 56.dp)
            .background(MaterialTheme.colors.secondary, RoundedCornerShape(CornerSize(50)))
            .clickable {
                onCurrentCityClick()
            }
            .wrapContentSize(Alignment.Center),
        text = locationName,
        style = MaterialTheme.typography.h4,

        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.onSecondary
    )
}

@Composable
private fun Forecast(forecastUiState: ForecastUiState?, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        when (forecastUiState) {
            is ForecastUiState.Success -> {
                ForecastInformation(
                    forecastUiState.forecastData,
                    forecastUiState.refreshing,
                    forecastUiState.refresh,
                    modifier
                )
            }

            is ForecastUiState.Error -> {
                Text(
                    "Error",
                    modifier = modifier
                        .wrapContentSize(Alignment.Center)
                )
            }

            null -> ForecastInitialisation(modifier.wrapContentSize(Alignment.Center))
        }
    }
}

@Composable
private fun ForecastInitialisation(modifier: Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
    )
}

@ExperimentalMaterialApi
@Composable
private fun ForecastInformation(forecastData: ForecastData, refreshing: Boolean, refresh: () -> Unit, modifier: Modifier) {
    val pullRefreshState = rememberPullRefreshState(refreshing, refresh)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(state = pullRefreshState)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = forecastData.temperature.toString(),
            style = MaterialTheme.typography.h3,
            textAlign = TextAlign.Center,
            modifier = modifier
                .padding(100.dp)
                .wrapContentSize(Alignment.Center)
        )
        PullRefreshIndicator(
            scale = true,
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview(device = Devices.NEXUS_6)
@Composable
private fun SuccessPreview() {
    DashboardScreen(
        uiState = DashboardUiState(
            LocationMode.Current,
            ForecastUiState.Success(
                forecastData = ForecastData(10f),
                refreshing = false,
                refresh = {},
                lastLoadFailed = false
            )
        ),
        onCurrentCityClick = { /*TODO*/ }
    )
}

@Preview(device = Devices.NEXUS_6)
@Composable
private fun ForecastInitializingPreview() {
    DashboardScreen(
        uiState = DashboardUiState(
            LocationMode.Current,
            null
        ),
        onCurrentCityClick = { /*TODO*/ }
    )
}

@Preview(device = Devices.NEXUS_6)
@Composable
private fun ForecastErrorPreview() {
    DashboardScreen(
        uiState = DashboardUiState(
            LocationMode.Current,
            ForecastUiState.Error(false, {})
        ),
        onCurrentCityClick = { /*TODO*/ }
    )
}
