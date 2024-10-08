package com.example.abschlussprojekt.ui.Task.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.abschlussprojekt.data.FirebaseRepository
import com.example.abschlussprojekt.data.model.Task
import kotlinx.coroutines.launch

private const val TAG = "TaskViewModel"

class TaskViewModel(application: Application): AndroidViewModel(application) {

    private val repository = FirebaseRepository()

    val currentUser = repository.currentUser
    val profile = repository.profile
    val tasks = repository.tasks
    val selectedTask = repository.selectedTask

    fun fetchTasks() {
        viewModelScope.launch {
            repository.fetchTasks()
        }
    }

    fun saveTaskInFireStore(newTask: Task) {
        repository.saveTaskInFireStore(newTask)
    }

    fun getMyTasks(onResult: (Map<String, Any>?) -> Unit) {
        repository.getMyTasks(onResult)
    }

    fun uploadImage(imageUri: Uri) {
        repository.uploadImage(imageUri)
    }

}