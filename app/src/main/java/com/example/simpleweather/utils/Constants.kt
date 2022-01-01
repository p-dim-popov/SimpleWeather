package com.example.simpleweather.utils

import android.Manifest

object Constants {
    const val SharedPreferences_location = "locationName"
    val RequiredPermissions_location = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )
}