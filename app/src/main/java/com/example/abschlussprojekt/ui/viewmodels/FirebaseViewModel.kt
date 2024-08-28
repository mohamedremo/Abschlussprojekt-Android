package com.example.abschlussprojekt.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abschlussprojekt.data.FirebaseRepository
import kotlinx.coroutines.launch

private const val TAG = "FirebaseViewModel"

class FirebaseViewModel(): ViewModel() {

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
}