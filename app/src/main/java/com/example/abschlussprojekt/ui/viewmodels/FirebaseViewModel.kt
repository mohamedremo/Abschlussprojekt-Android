package com.example.abschlussprojekt.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abschlussprojekt.data.FirebaseRepository
import kotlinx.coroutines.launch

private const val TAG = "FirebaseViewModel"

class FirebaseViewModel(application: Application): AndroidViewModel(application) {

    private val repository = FirebaseRepository()
    val currentUser = repository.currentUser

    fun registerNewUser(email: String, password: String) {
        viewModelScope.launch {
            repository.registerNewUser(email, password)
        }
    }

    fun logIn(email: String, password: String) {
        viewModelScope.launch {
            repository.logIn(email, password)
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            repository.forgotPassword(email)
        }
    }

    fun logOut() {
        viewModelScope.launch {
            repository.logOut()
        }
    }

    fun saveInFirestore(firstName: String, surName: String, email: String, birthDate: String, driverLicense: Boolean, wantDeliver: Boolean, phoneNumber: String) {
        repository.saveInFirestore(firstName, surName, email, birthDate, driverLicense, wantDeliver, phoneNumber)
    }

    fun getUserProfile(onResult: (Map<String, Any>?) -> Unit) {
        repository.getUserProfile(onResult)
    }
}