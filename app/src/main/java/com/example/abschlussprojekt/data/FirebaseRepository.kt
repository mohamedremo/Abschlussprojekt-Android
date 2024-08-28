package com.example.abschlussprojekt.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

private const val TAG = "FirebaseRepository"

class FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()

    private val _currentUser = MutableLiveData<FirebaseUser?>(auth.currentUser)
    val currentUser: MutableLiveData<FirebaseUser?>
        get() = _currentUser

    suspend fun registerNewUser(email: String, password: String) {
        try {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _currentUser.value = auth.currentUser
                    Log.d(TAG, "registerNewUser(if): ${task.result}")
                } else {
                    _currentUser.value = null
                    Log.d(TAG, "registerNewUser(else): ${task.result}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun logIn(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _currentUser.value = auth.currentUser
                    Log.d(TAG, "logIn(if): ${task.result}")

                } else {
                    _currentUser.value = null
                    Log.d(TAG, "logIn(else): ${task.result}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun forgotPassword(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            Log.d(TAG, "forgotPassword: ${task.result}")
        }
    }

    suspend fun logOut() {
        auth.signOut()
        _currentUser.value = null
        Log.d(TAG, "logOut Successful")
    }

}





