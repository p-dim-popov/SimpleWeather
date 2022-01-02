package com.example.simpleweather.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.simpleweather.R
import com.example.simpleweather.models.AppViewModel
import com.example.simpleweather.network.WeatherApi
import com.example.simpleweather.network.getCurrentWeather
import com.example.simpleweather.utils.Constants
import java.lang.Exception

class CurrentTemperatureWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val appContext = applicationContext

        AppViewModel.Location.parse(inputData.getString("location"))?.apply {

            val temp = try {
                WeatherApi.retrofitService.getCurrentWeather(city, countryCode).let { "$it °C" }
            } catch (e: Exception) { return Result.failure() }

            val notification = NotificationCompat.Builder(appContext, "")
                .setSmallIcon(R.drawable.clouds)
                .setContentTitle("$city - $temp")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(Constants.NotificationChannel_weatherUpdate)
                .build()

            val notificationManager = getSystemService(appContext, NotificationManager::class.java)

            createNotificationChannel(applicationContext)
            notificationManager?.notify(0, notification)
            return Result.success()
        }

        return Result.failure()
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