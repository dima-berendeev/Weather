package org.berendeev.weather.models

data class Coordinates(val latitude: Latitude, val longitude: Longitude)

fun Coordinates(latitude: Double, longitude: Double) = Coordinates(Latitude(latitude), Longitude(longitude))
