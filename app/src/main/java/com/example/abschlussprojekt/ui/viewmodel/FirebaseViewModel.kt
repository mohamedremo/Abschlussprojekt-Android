package com.example.abschlussprojekt.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.abschlussprojekt.data.FirebaseRepository
import kotlinx.coroutines.launch

private const val TAG = "FirebaseViewModel"

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseRepository()
    val currentUser = repository.currentUser
    val profile = repository.profile

    fun registerNewUser(
        email: String,
        password: String,
        firstName: String,
        surName: String,
        birthDate: String,
        driverLicense: Boolean,
        readyForWork: Boolean,
        profilePicture: String
    ) {
        viewModelScope.launch {
            repository.registerNewUser(
                email,
                password,
                firstName,
                surName,
                birthDate,
                driverLicense,
                readyForWork,
                profilePicture
            )
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

    fun saveInFirestore(
        firstName: String,
        surName: String,
        email: String,
        birthDate: String,
        driverLicense: Boolean,
        readyForWork: Boolean,
        profilePicture: String
    ) {
        repository.saveInFirestore(firstName, surName, email, birthDate, driverLicense, readyForWork,profilePicture)
    }

    fun getUserProfile(onResult: (Map<String, Any>?) -> Unit) {
        repository.getUserProfile(onResult)
    }

    fun uploadImage(imageUri: Uri) {
        viewModelScope.launch {
            repository.uploadImage(imageUri)
        }
    }

//    fun getValueFromDocument(collection: String, documentId: String, field: String) {
//        viewModelScope.launch {
//            repository.getValueFromDocument(collection, documentId, field )
//        }
//    }
}