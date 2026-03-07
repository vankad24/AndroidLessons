package com.molo4ko.tempbinding

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class Weather(
    val main: String
)

data class Wind(
    val deg: Double
)