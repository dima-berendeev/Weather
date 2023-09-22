package org.berendeev.weather.selectplace

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SelectPlaceScreen(
    onPlaceSelected: (Place) -> Unit,
    onClose: () -> Unit,
    viewModel: SelectPlaceViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        val uiState by viewModel.uiStateFlow.collectAsState()
//        val variants: List<SelectPlaceViewModel.PlaceVariant> = (0..10).map {
//            SelectPlaceViewModel.PlaceVariant(it.toString(), Latitude(0.0), Longitude(0.0))
//        }
        val variants: List<SelectPlaceViewModel.PlaceVariant> = uiState?.variants ?: emptyList()

        val query = viewModel.queryStateFlow.collectAsState().value

        Row() {
            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = { onClose() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Localized description"
                )
            }

            val focusManager = LocalFocusManager.current
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.queryStateFlow.value = it },
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .fillMaxWidth(),
                label = { "Search" },
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

        LazyColumn(
            modifier = Modifier

                .weight(1.0f)
                .fillMaxWidth()
                .imePadding()
        ) {
            items(variants) { variant ->
                ListItem(headlineText = {
                    Text(
                        text = variant.name,
                        modifier = Modifier.fillMaxWidth(),
                    )
                })

            }
        }
    }
}
