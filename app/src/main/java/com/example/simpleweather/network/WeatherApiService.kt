package com.example.simpleweather.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY="fceac126c2f6bbe42d54d1d0893c2c6d"

private const val BASE_URL =
    "https://api.openweathermap.org"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface WeatherApiService {
    @GET("geo/1.0/reverse")
    suspend fun getLocationsFromCoordinates(
        @Query("lon") lon: String,
        @Query("lat") lat: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String = API_KEY
    ): List<LocationDefinition>

    @GET("data/2.5/weather")
    suspend fun _getCurrentWeather(
        @Query("q") q: String,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = "metric",
    ): LocationInfo

    @GET("geo/1.0/direct")
    suspend fun getLocationsFromQuery(
        @Query("q") q: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String = API_KEY,
    ): List<LocationDefinition>
}

suspend fun WeatherApiService.getCurrentWeather(city: String, countryCode: String): Double =
    this._getCurrentWeather("$city,$countryCode").mainWeather.temp

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}