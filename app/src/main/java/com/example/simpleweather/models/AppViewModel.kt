
package com.example.simpleweather.models

import android.util.Log
import androidx.lifecycle.*
import com.example.simpleweather.network.LocationDefinition
import com.example.simpleweather.network.WeatherApi
import com.example.simpleweather.network.getCurrentWeather
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    companion object { val initialState = AppViewModel() }

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

    private val _locationName = MutableLiveData<Location>(null) // Varna,BG
    val locationName: LiveData<Location> = _locationName
    fun setLocationName(location: Location) {
        _locationName.value = location
        fetchTemperature()
    }

    private val _temperature = MutableLiveData("N/a")
    val temperature: LiveData<String> = Transformations.map(_temperature) { "$it °C" }

    private val _notifications = MutableLiveData<Boolean>(false)
    val notifications: LiveData<Boolean> = _notifications

    private val _possibleLocations = MutableLiveData<List<LocationDefinition>>()
    val possibleLocations: LiveData<List<LocationDefinition>> = _possibleLocations

    fun autoSetupLocationName(lon: String, lat: String) {
        viewModelScope.launch {
            val possibleLocations = WeatherApi.retrofitService.getLocationsFromCoordinates(lon, lat)
            _possibleLocations.value = possibleLocations

            val locationInfo = possibleLocations.first()
            setLocationName(Location(locationInfo.name, locationInfo.country))
        }
    }

    fun updatePossibleLocations(lon: String, lat: String) {
        viewModelScope.launch {
            try {
                val possibleLocations = WeatherApi.retrofitService.getLocationsFromCoordinates(lon, lat)
                _possibleLocations.value = possibleLocations
            } catch (e: Exception) {
                Log.e(AppViewModel::class.java.toString(), e.toString())
            }
        }
    }

    fun fetchTemperature() {
        viewModelScope.launch {
            _locationName.value?.apply {
                val temp = WeatherApi.retrofitService.getCurrentWeather(city, countryCode)
                _temperature.value = temp.toString()
            }
        }
    }

    fun toggleNotifications() {
        _notifications.value = _notifications.value?.not()
    }
}