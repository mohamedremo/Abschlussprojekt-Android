package com.example.abschlussprojekt.ui.Home.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.abschlussprojekt.data.FirebaseRepository
import com.example.abschlussprojekt.data.WeatherRepository
import com.example.abschlussprojekt.data.model.Task
import com.example.abschlussprojekt.data.remote.WeatherApi
import com.google.firebase.firestore.GeoPoint

import kotlinx.coroutines.launch

private const val TAG = "WeatherViewModel"

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val weatherRepository = WeatherRepository(WeatherApi)
    private val repository = FirebaseRepository()
    val profile = repository.profile
    val tasks = repository.tasks
    val currentUser = repository.currentUser

    val lastWeather = weatherRepository.lastWeather

    init {
        fetchTasks()
    }

    fun getWeatherByLocation(longitude: Double, latitude: Double) {
        viewModelScope.launch {
            weatherRepository.getWeatherByLocation(longitude, latitude)
            Log.d(TAG, "getWeatherByLocation: $lastWeather")
        }
    }

    fun fetchTasks() {
        viewModelScope.launch {
            repository.fetchTasks()
        }
    }

    fun updateLastLocation(location: GeoPoint) {
        repository.updateLastLocation(location)
    }

    fun setOnlineStatus(readyForWork: Boolean) {
        repository.setOnlineStatus(readyForWork)
    }

    fun setSelectedTask(taskName: Task) {
        repository.setSelectedTask(taskName)
    }

    fun uploadImage(uri: Uri) {
        repository.uploadImage(uri)
    }
}