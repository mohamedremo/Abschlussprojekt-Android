package com.example.abschlussprojekt.ui.Home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.abschlussprojekt.data.FirebaseRepository
import com.example.abschlussprojekt.data.WeatherRepository
import com.example.abschlussprojekt.data.remote.WeatherApi

private const val TAG = "WeatherViewModel"

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseRepository()

    val profile = repository.profile
    val myTasks = repository.myTasks

    init {
        getMyTasks()
    }

    fun getMyTasks() {
        repository.getMyTasks()
    }

}