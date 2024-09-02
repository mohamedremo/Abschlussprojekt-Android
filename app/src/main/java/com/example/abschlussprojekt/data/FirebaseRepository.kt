package com.example.abschlussprojekt.data

import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.abschlussprojekt.data.model.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

private const val TAG = "FirebaseRepository"

class FirebaseRepository {


    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val storageRef = storage.reference
    private lateinit var profileRef: DocumentReference

    private val _currentUser = MutableLiveData<FirebaseUser?>(auth.currentUser)
    val currentUser: MutableLiveData<FirebaseUser?>
        get() = _currentUser

    private val _downloading = MutableLiveData(false)
    val downloading: LiveData<Boolean>
        get() = _downloading

    init {
        if (auth.currentUser != null) {
            _currentUser.value = auth.currentUser
            profileRef = firestore.collection("profiles").document(auth.currentUser!!.uid)
        }
    }

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

    fun logOut() {
        auth.signOut()
        _currentUser.value = null
        Log.d(TAG, "logOut Successful")
    }

    fun saveInFirestore(
        firstName: String,
        surName: String,
        email: String,
        birthDate: String,
        driverLicense: Boolean,
        wantDeliver: Boolean,
    ) {
        auth.currentUser?.let { user ->
            val documentRef = firestore
                .collection("profiles")
                .document(user.uid)

            val userData = Profile(
                firstName,
                surName,
                email,
                birthDate,
                driverLicense,
                wantDeliver,
                profilePicture = ""
            )

            documentRef.set(userData)
                .addOnSuccessListener {
                    Log.d(TAG, "User Profil wurder Erfolgreich gespeichert")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Beim Speichern des User-Profiles ist ein Fehler aufgetreten", e)
                }
        }
    }

    suspend fun getUserProfile(onResult: (Map<String, Any>?) -> Unit) {
        auth.currentUser?.let { user ->
            val documentRef = firestore
                .collection("profiles")
                .document(user.uid)

            documentRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Daten abrufen und an die Callback-Funktion übergeben
                        val userData = document.data
                        onResult(userData)
                    } else {
                        Log.d(TAG, "Dokument existiert nicht")
                        onResult(null)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Beim Fetchen der Profil-Daten ist ein Fehler aufgetreten", e)
                    onResult(null)
                }
        }
    }

    fun uploadImage(imageUri: Uri) {
        auth.currentUser?.let { user ->
            var storageRef = storageRef.child("images/${user.uid}/profilePicture")

            storageRef.putFile(imageUri).addOnSuccessListener {
                Log.d(TAG, "Bild wurde erfolgreich hochgeladen")
                storageRef.downloadUrl.addOnSuccessListener {
                    (profileRef.update("profilePicture", it.toString()))
                }
            }
                .addOnFailureListener {
                    Log.d(TAG, "Bild konnte nicht hochgeladen werden")
                }

        }
    }
}




