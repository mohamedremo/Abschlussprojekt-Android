package com.example.abschlussprojekt.ui.Task.viewmodel

import android.app.Application
import android.location.Address
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.abschlussprojekt.data.FirebaseRepository
import com.example.abschlussprojekt.data.model.Task
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch

private const val TAG = "TaskViewModel"

class TaskViewModel(application: Application): AndroidViewModel(application) {

    private val repository = FirebaseRepository()

    val currentUser = repository.currentUser
    val profile = repository.profile
    val tasks = repository.tasks
    val selectedTask = repository.selectedTask
    val myTasks = repository.myTasks

    init {
        fetchTasks()
        getMyTasks()
    }

    private val _selectedAdress = MutableLiveData<Address>()
    val selectedAdress: LiveData<Address>
        get() = _selectedAdress

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String>
        get() = _selectedCategory

    fun fetchTasks() {
        viewModelScope.launch {
            repository.fetchTasks()
        }
    }

    fun setSelectedAdress(address: Address) {
        _selectedAdress.value = address
    }

    fun saveTaskInFireStore(newTask: Task) {
        repository.saveTaskInFireStore(newTask)
    }

    fun getMyTasks() {
        repository.getMyTasks()
    }

    fun uploadImage(imageUri: Uri) {
        repository.uploadImage(imageUri)
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateLastLocation(location: GeoPoint) {
        repository.updateLastLocation(location)
    }

    fun setSelectedTask(task: Task) {
        repository.setSelectedTask(task)
    }
}