package com.example.abschlussprojekt.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.abschlussprojekt.data.model.Category
import com.example.abschlussprojekt.data.model.WeatherResponse
import com.example.abschlussprojekt.data.remote.WeatherApi

private const val TAG = "Repository"

class Repository(
    private val weatherApi: WeatherApi,
) {

    private val _lastWeather = MutableLiveData<WeatherResponse>()
    val lastWeather: LiveData<WeatherResponse>
        get() = _lastWeather

    private val _selectedCategory = MutableLiveData<Category>()
    val selectedCategory: LiveData<Category>
        get() = _selectedCategory

    // Wetterdaten anhand von Koordinaten bei Weather API (openMeteo) abrufen
    suspend fun getWeatherByLocation(longitude: Double, latitude: Double) {
        try {
            val result = WeatherApi.retrofitService.getCurrentWeather(longitude, latitude)
            _lastWeather.postValue(result)
            Log.d(TAG, "getWeatherByLocation: $result")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "getWeatherByLocation: ${e.message}")
        }
    }

    suspend fun setSelectedCategory(category: Category) {
        _selectedCategory.value = category
    }

}