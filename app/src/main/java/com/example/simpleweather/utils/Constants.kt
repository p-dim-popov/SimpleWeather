package com.example.simpleweather.utils

import android.Manifest

object Constants {
    const val SharedPreferences_location = "locationName"
    const val SharedPreferences_notifications = "notifications"

    val RequiredPermissions_location = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    const val WorkName_currentWeather = "8c303c0a-e1e6-4307-a162-617f0e56abb2"

    const val NotificationChannel_weatherUpdate = "weather_update"
}