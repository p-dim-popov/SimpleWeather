
package com.example.simpleweather.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import com.example.simpleweather.network.LocationDefinition
import com.example.simpleweather.network.WeatherApi
import com.example.simpleweather.network.getCurrentWeather
import com.example.simpleweather.utils.Constants
import com.example.simpleweather.workers.CurrentTemperatureWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AppViewModel(application: Application) : ViewModel() {
    class Location(
        val city: String,
        val countryCode: String,
    ) {
        override fun equals(other: Any?): Boolean = other != null && other is Location && with(other) { this.city == this@Location.city && this.countryCode == this@Location.countryCode }
        override fun hashCode(): Int {
            var result = city.hashCode()
            result = 31 * result + countryCode.hashCode()
            return result
        }

        override fun toString(): String {
            return "${city},${countryCode}"
        }

        companion object {
            fun parse(string: String?): Location? {
                 string?.apply {
                     val (city, countryCode) = split(",")
                     return Location(city, countryCode)
                 }
                return null
            }

            fun from(location: LocationDefinition): Location {
                return Location(location.name, location.country)
            }
        }
    }

    private val workManager = WorkManager.getInstance(application)

    private val _location = MutableLiveData<Location>(null)
    val location: LiveData<Location> = _location
    fun setLocation(location: Location) {
        _location.value = location
        fetchTemperature()
    }

    private val _temperature = MutableLiveData("N/a")
    val temperature: LiveData<String> = Transformations.map(_temperature) { "$it °C" }

    private val _notifications = MutableLiveData<Boolean>(null)
    val notifications: LiveData<Boolean> = _notifications
    fun setNotifications(value: Boolean) {
        _notifications.value = value
    }

    private val _possibleLocations = MutableLiveData<List<LocationDefinition>>()
    val possibleLocations: LiveData<List<LocationDefinition>> = _possibleLocations

    fun autoSetupLocationName(lon: String, lat: String) {
        viewModelScope.launch {
            val possibleLocations = WeatherApi.retrofitService.getLocationsFromCoordinates(lon, lat)
            _possibleLocations.value = possibleLocations

            val locationInfo = possibleLocations.first()
            setLocation(Location(locationInfo.name, locationInfo.country))
        }
    }

    fun updatePossibleLocations(lon: String, lat: String) = viewModelScope.launch {
        try {
            val possibleLocations = WeatherApi.retrofitService.getLocationsFromCoordinates(lon, lat)
            _possibleLocations.value = possibleLocations
        } catch (e: Exception) {
            Log.e(AppViewModel::class.java.toString(), e.toString())
        }
    }

    fun fetchTemperature() = viewModelScope.launch {
        _location.value?.apply {
            val temp = WeatherApi.retrofitService.getCurrentWeather(city, countryCode)
            _temperature.value = temp.toString()
        }
    }

    fun toggleNotifications() {
        setNotifications((_notifications.value ?: false).not())
        when (_notifications.value) {
            true -> workManager
                .enqueueUniquePeriodicWork(
                    Constants.WorkName_currentWeather,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    PeriodicWorkRequestBuilder<CurrentTemperatureWorker>(1, TimeUnit.HOURS)
                        .setInputData(with(Data.Builder()) {
                            putString("location", location.value.toString())
                            build()
                        })
                        .build()
                )
            else -> workManager.cancelUniqueWork(Constants.WorkName_currentWeather)
        }
    }
}

class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            AppViewModel(application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}