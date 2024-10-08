package com.example.abschlussprojekt.ui.LoginAndRegister.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.abschlussprojekt.data.FirebaseRepository
import com.example.abschlussprojekt.data.model.Product
import com.example.abschlussprojekt.data.model.Profile
import com.example.abschlussprojekt.data.model.Task

import kotlinx.coroutines.launch

private const val TAG = "FirebaseViewModel"

class FirebaseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseRepository()
    val currentUser = repository.currentUser
    val profile = repository.profile
    val tasks = repository.tasks
    val selectedTask = repository.selectedTask

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

    fun saveProfileInFireStore(
        firstName: String,
        surName: String,
        email: String,
        birthDate: String,
        driverLicense: Boolean,
        readyForWork: Boolean,
        profilePicture: String
    ) {
        repository.saveProfileInFireStore(
            firstName,
            surName,
            email,
            birthDate,
            driverLicense,
            readyForWork,
            profilePicture
        )
    }

    // Die 3 nachfolgenden Funktionen sind Funktionen zum Initialisieren von Testdaten im Firestore.

    fun saveAllProfiles(profiles: List<Profile>) {
        repository.saveAllProfiles(profiles)
    }

    fun saveAllProducts(products: List<Product>) {
        repository.saveAllProducts(products)
    }

    fun saveAllTasks(tasks: List<Task>) {
        repository.saveAllTasks(tasks)
    }

    fun getUserProfile(onResult: (Map<String, Any>?) -> Unit) {
        repository.getUserProfile(onResult)
    }

    fun uploadImage(imageUri: Uri) {
        viewModelScope.launch {
            repository.uploadImage(imageUri)
        }
    }

    fun saveTaskInFireStore(newTask: Task) {
        viewModelScope.launch {
            repository.saveTaskInFireStore(newTask)
        }

    }

    fun fetchTasks() {
        viewModelScope.launch {
            repository.fetchTasks()
        }
    }

    fun setOnlineStatus(status: Boolean) {
        repository.setOnlineStatus(status)
    }

    fun getMyTasks(onResult: (Map<String, Any>?) -> Unit) {
        repository.getMyTasks(onResult)
    }

    fun updateProfile(firstName: String, surName: String, birthDate: String) {
        repository.updateProfile(firstName, surName, birthDate)
    }

    fun setSelectedTask(task: Task) {
        repository.setSelectedTask(task)
    }


//    fun getValueFromDocument(collection: String, documentId: String, field: String) {
//        viewModelScope.launch {
//            repository.getValueFromDocument(collection, documentId, field )
//        }
//    }
}