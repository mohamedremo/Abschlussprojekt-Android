package com.example.abschlussprojekt.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.abschlussprojekt.data.Repository
import com.example.abschlussprojekt.data.local.MyButlerDatabase
import com.example.abschlussprojekt.data.remote.ButlerApi
import com.example.abschlussprojekt.data.remote.MyButlerApiService
import com.example.abschlussprojekt.data.remote.WeatherApi

import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"

class MainViewModel(application: Application): AndroidViewModel(application) {


    private val repository = Repository(WeatherApi, ButlerApi)

    val lastWeather = repository.lastWeather

    val tasks = repository.tasks


    fun getWeatherByLocation(longitude: Double, latitude: Double) {
        viewModelScope.launch {
            repository.getWeatherByLocation(longitude, latitude)
        }
    }

    fun getProfiles() {
        viewModelScope.launch {
            repository.getProfiles()
        }
    }

    fun getTasks() {
        viewModelScope.launch {
            repository.getTasks()
        }
    }



}