package org.berendeev.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.berendeev.weather.ui.theme.WeatherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home-screen") {
        composable("home-screen") {
            Home(
                onCurrentCityClick = { navController.navigate("select-city-screen") }
            )
        }
        composable("select-city-screen") {
            SelectCityScreen()
        }
    }
}

@Composable
fun CurrentCity(onCurrentCityClick: () -> Unit, modifier: Modifier) {
    Text(
        text = "Selected city",
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onCurrentCityClick()
            }
    )
}

@Composable
fun Home(onCurrentCityClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        CurrentCity(onCurrentCityClick, Modifier.fillMaxWidth())
    }
}

@Composable
fun SelectCityScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        repeat(10) {
            Text(
                text = "City",
                modifier = modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}
