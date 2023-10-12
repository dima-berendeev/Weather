@file:OptIn(ExperimentalMaterialApi::class)

package org.berendeev.weather.selectplace

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SelectPlaceRoute(
    onPlaceSelected: (SelectedPlace) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SelectPlaceViewModel = hiltViewModel()
) {
    val selectedPlace = viewModel.selectedPlaceStateFlow.collectAsStateWithLifecycle().value
    LaunchedEffect(key1 = selectedPlace) {
        selectedPlace?.run { onPlaceSelected(selectedPlace) }
    }

    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val query = viewModel.queryStateFlow.collectAsStateWithLifecycle().value
    SelectPlaceScreen(uiState, query, onClose, modifier)
}

@Composable
fun SelectPlaceScreen(
    uiState: SelectPlaceUiState?,
    query: Query,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        TopBar(
            query = query,
            onNavIconClicked = { onClose() }
        )

        val variants: List<PlaceVariantUiState>? = uiState?.variants

        if (variants != null) {
            Places(
                variants,
                Modifier
                    .weight(1.0f)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun TopBar(query: Query, onNavIconClicked: () -> Unit) {
    Row {
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = { onNavIconClicked() }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Localized description"
            )
        }

        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            value = query.text,
            onValueChange = { query.onChanged(it) },
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Clear,
                    contentDescription = "Localized description"
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() })
        )
    }
}

@Composable
private fun Places(
    variants: List<PlaceVariantUiState>,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier
            .imePadding()
    ) {
        items(variants) { variant ->
            when (variant) {
                is PlaceVariantUiState.CurrentLocation -> CurrentLocation(variant)
                is PlaceVariantUiState.Geo -> Suggestion(variant)
            }
        }
    }
}

@Composable
private fun CurrentLocation(variant: PlaceVariantUiState.CurrentLocation) {
    ListItem(
        modifier = Modifier.clickable {
            variant.onClick()
        },
        text = {
            Text(
                text = "Current location",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    )
}

@Composable
private fun Suggestion(variant: PlaceVariantUiState.Geo) {
    ListItem(
        modifier = Modifier.clickable { variant.onClick() },
        text = {
            Text(
                text = variant.name,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    )
}
