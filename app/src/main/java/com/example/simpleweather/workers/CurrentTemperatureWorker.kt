package com.example.simpleweather.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.simpleweather.MainActivity
import com.example.simpleweather.R
import com.example.simpleweather.models.AppViewModel
import com.example.simpleweather.network.WeatherApi
import com.example.simpleweather.network.getCurrentWeather
import com.example.simpleweather.utils.Constants

class CurrentTemperatureWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    private val notificationManager = getSystemService(applicationContext, NotificationManager::class.java)!!
    private val baseNotification
        get() = NotificationCompat.Builder(applicationContext, Constants.NotificationChannel_weatherUpdate)
            .setSmallIcon(R.drawable.clouds)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    init { createNotificationChannel(applicationContext) }

    override suspend fun doWork(): Result {
        val serializedLocation = applicationContext
            .getSharedPreferences(MainActivity::class.simpleName, Context.MODE_PRIVATE)
            .getString(Constants.SharedPreferences_location, null)

        return when(val location = AppViewModel.Location.parse(serializedLocation)) {
            null -> notificationManager.notify(0, baseNotification
                .setContentTitle("Location unknown or not set")
                .build())
                .let { Result.failure() }
            else -> with(location) {
                when(val temp = kotlin.runCatching { WeatherApi.retrofitService.getCurrentWeather(city, countryCode) }.getOrNull()) {
                    null -> notificationManager.notify(0, baseNotification
                        .setContentTitle("Could not fetch weather info...")
                        .build())
                        .let { Result.failure() }
                    else -> notificationManager.notify(0, baseNotification
                        .setContentTitle("${temp.let { "$it °C" }} in $city")
                        .build())
                        .let { Result.success() }
                }
            }
        }
    }

    companion object {
        fun createNotificationChannel(ctx: Context) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Weather Update"
                val descriptionText = name
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(Constants.NotificationChannel_weatherUpdate, name, importance).apply {
                    description = descriptionText
                    setSound(null, null)
                }
                // Register the channel with the system
                val notificationManager = getSystemService(ctx, NotificationManager::class.java)
                notificationManager?.createNotificationChannel(channel)
            }
        }
    }
}