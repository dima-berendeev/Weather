package org.berendeev.weather.selectplace

import org.berendeev.weather.models.Latitude
import org.berendeev.weather.models.Longitude

data class Place(val name: String, val latitude: Latitude, val longitude: Longitude)
