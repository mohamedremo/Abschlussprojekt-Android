package com.example.abschlussprojekt.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.abschlussprojekt.data.model.WeatherResponse
import com.example.abschlussprojekt.data.remote.WeatherApi

private const val TAG = "WeatherRepository"

class WeatherRepository(
    private val weatherApi: WeatherApi,
) {
    private val _lastWeather = MutableLiveData<WeatherResponse>()
    val lastWeather: LiveData<WeatherResponse>
        get() = _lastWeather

    // Wetterdaten anhand von Koordinaten bei Weather API (openMeteo) abrufen
    suspend fun getWeatherByLocation(longitude: Double, latitude: Double) {
        try {
            val result = weatherApi.retrofitService.getCurrentWeather(longitude, latitude)
            _lastWeather.postValue(result)
            Log.d(TAG, "getWeatherByLocation: $result")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "getWeatherByLocation: ${e.message}")
        }
    }
}