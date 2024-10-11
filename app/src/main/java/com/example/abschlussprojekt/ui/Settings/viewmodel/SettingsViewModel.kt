package com.example.abschlussprojekt.ui.Settings.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.example.abschlussprojekt.data.FirebaseRepository

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseRepository()
    val profile = repository.profile

    fun updateProfile(firstName: String, surName: String, birthDate: String) {
        repository.updateProfile(firstName, surName, birthDate)
    }

    fun logOut() {
        repository.logOut()
    }

    fun uploadImage(uri: Uri) {
        repository.uploadImage(uri)
    }
}