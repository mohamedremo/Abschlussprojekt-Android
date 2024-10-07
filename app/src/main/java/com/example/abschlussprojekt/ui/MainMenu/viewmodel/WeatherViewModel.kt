package com.example.abschlussprojekt.ui.MainMenu.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.abschlussprojekt.data.Repository
import com.example.abschlussprojekt.data.remote.WeatherApi

import kotlinx.coroutines.launch

private const val TAG = "WeatherViewModel"

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(WeatherApi)

    val lastWeather = repository.lastWeather

    fun getWeatherByLocation(longitude: Double, latitude: Double) {
        viewModelScope.launch {
            repository.getWeatherByLocation(longitude, latitude)
            Log.d(TAG, "getWeatherByLocation: $lastWeather")
        }
    }
}