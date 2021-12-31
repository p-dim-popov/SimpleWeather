package com.example.simpleweather.network

import com.squareup.moshi.Json

data class LocationInfo (
    @Json(name = "coord")
    val coordinates: Coordinates?,
    val weather: List<Weather>?,
    val base: String?,
    @Json(name = "main")
    val mainWeather: MainWeather,
    val visibility: Long?,
    val wind: Wind?,
    val clouds: Clouds?,
    val dt: Long?,
    val sys: Sys?,
    val id: Long?,
    val name: String,
    val cod: Long?,
)

data class Clouds (
    val all: Long,
)

data class Coordinates (
    val lon: Double,
    val lat: Double,
)

data class MainWeather (
    val temp: Double,
    val pressure: Long?,
    val humidity: Long?,
    val tempMin: Double?,
    val tempMax: Double?,
)

data class Sys (
    val type: Long?,
    val id: Long?,
    val message: Double?,
    val country: String?,
    val sunrise: Long?,
    val sunset: Long?,
)

data class Weather (
    val id: Long?,
    val main: String?,
    val description: String?,
    val icon: String?,
)

data class Wind (
    val speed: Double?,
    val deg: Long?,
)
