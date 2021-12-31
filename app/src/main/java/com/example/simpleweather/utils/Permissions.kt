package com.example.simpleweather.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat

fun List<Int>.allPermissionsGranted(): Boolean = this
    .all { it == PackageManager.PERMISSION_GRANTED }

fun Activity.arePermissionsGranted(permissions: Array<String>): Boolean =
    permissions
        .map { ActivityCompat.checkSelfPermission(this, it) }
        .allPermissionsGranted()

interface LocationListenerWithLocationManager: LocationListener {
    var locationManager: LocationManager
}

@SuppressLint("MissingPermission")
fun LocationListenerWithLocationManager.requestLocationUpdates(providers: Array<String> = arrayOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)) {
    providers.forEach {
        this.locationManager.requestLocationUpdates(it, 2_000L, 0f, this)
    }
}
