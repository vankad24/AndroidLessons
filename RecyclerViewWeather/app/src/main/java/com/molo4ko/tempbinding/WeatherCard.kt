package com.molo4ko.tempbinding

data class WeatherCard(
    val city: String,
    val temp: Double,
    val humidity: Int,
    val weatherIcon: Int,
    val windIcon: Int
)