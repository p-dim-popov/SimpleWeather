
package com.example.simpleweather.models

import android.util.Log
import androidx.lifecycle.*
import com.example.simpleweather.network.LocationDefinition
import com.example.simpleweather.network.WeatherApi
import com.example.simpleweather.network.getCurrentWeather
import kotlinx.coroutines.launch

class LocationWeatherViewModel : ViewModel() {
    companion object { val initialState = LocationWeatherViewModel() }

    private val _locationName = MutableLiveData("Loading...") // Varna,BG
    val locationName: LiveData<String> = _locationName
    fun setLocationName(locationName: String) {
        _locationName.value = locationName

        if (locationName != initialState.locationName.value) fetchTemperature()
    }

    private val _temperature = MutableLiveData("N/a")
    val temperature: LiveData<String> = Transformations.map(_temperature) { "$it °C" }

    private val _notifications = MutableLiveData<Boolean>(false)
    val notifications: LiveData<Boolean> = _notifications

    private val _possibleLocations = MutableLiveData<List<LocationDefinition>>()
    val possibleLocations: LiveData<List<LocationDefinition>> = _possibleLocations

    fun autoSetupLocationName(lon: String, lat: String) {
        viewModelScope.launch {
            try {
                val possibleLocations = WeatherApi.retrofitService.getLocationsFromCoordinates(lon, lat)
                _possibleLocations.value = possibleLocations

                val locationInfo = possibleLocations.first()
                setLocationName("${locationInfo.name},${locationInfo.country}")
            } catch (e: Exception) {
                _locationName.value = "Error! Boo! $e"
            }
        }
    }

    fun updatePossibleLocations(lon: String, lat: String) {
        viewModelScope.launch {
            try {
                val possibleLocations = WeatherApi.retrofitService.getLocationsFromCoordinates(lon, lat)
                _possibleLocations.value = possibleLocations
            } catch (e: Exception) {
                Log.e(LocationWeatherViewModel::class.java.toString(), e.toString())
            }
        }
    }

    fun fetchTemperature() {
        viewModelScope.launch {
            _locationName.value?.apply {
                val (city, countryCode) = split(",")
                val temp = WeatherApi.retrofitService.getCurrentWeather(city, countryCode)
                _temperature.value = temp.toString()
            }
        }
    }

    fun toggleNotifications() {
        _notifications.value = _notifications.value?.not()
    }
}