package com.example.abschlussprojekt.data

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.abschlussprojekt.MainActivity
import com.example.abschlussprojekt.data.model.Profile
import com.example.abschlussprojekt.isValidEmail
import com.example.abschlussprojekt.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

private const val TAG = "FirebaseRepository"

class FirebaseRepository {

    //Firebase Authentifizierung
    private val auth = FirebaseAuth.getInstance()
    //Firebase Firestore
    private val firestore = FirebaseFirestore.getInstance()
    //Firebase Storage
    private val storage = FirebaseStorage.getInstance()

    private val storageRef = storage.reference
    private var profileRef: DocumentReference? = null


    private val _currentUser = MutableLiveData<FirebaseUser?>(auth.currentUser)
    val currentUser: MutableLiveData<FirebaseUser?>
        get() = _currentUser

    private val _downloading = MutableLiveData(false)
    val downloading: LiveData<Boolean>
        get() = _downloading

    private val _profile = MutableLiveData<DocumentReference?>()
    val profile: LiveData<DocumentReference?>
        get() = _profile

    //Initialisierung der Authentifizierung, wenn ein User eingeloggt ist.
    init {
        auth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
            profileRef = auth.currentUser?.uid?.let {
                firestore.collection("profiles")
                    .document(it)
            }
                _profile.postValue(profileRef) // Aktuellen Benutzer in Live Data aktualisieren
        }
    }

    //Registrierung eines neuen Users
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
        try {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                //Wenn Registrierung erfolgreich
                if (task.isSuccessful) {
                    logIn(email,password)
                    _currentUser.postValue(auth.currentUser) // Aktuellen Benutzer in Live Data aktualisieren
                    //Speichern der Daten in Firestore
                    saveInFirestore(firstName, surName, email, birthDate, driverLicense, readyForWork, profilePicture)
                    Log.d(TAG, "registerNewUser(if): ${task.result}")
                }
            }
        } catch (e: Exception) {
            _currentUser.value = null
            Log.d(TAG, "registerNewUser(TryCatch): ${e.message}")
        }
    }

    //Login eines Users
    fun logIn(email: String, password: String) {
        if (isValidEmail(email)){
            try {
                //Login mit Email und Passwort
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _currentUser.value = auth.currentUser
                        Log.d(TAG, "LogIn Success: ${task.result}")
                    }
                }
            } catch (e: Exception) {
                _currentUser.value = null
                Log.e(TAG, "Fehler im logIn() TryCatch Block: ${e.message}")
            }
        } else {
            Log.d(TAG, "Ungültige E-Mail")
            return // Abbruch der Funktion, wenn die E-Mail ungültig ist
        }
    }

    //Passwort vergessen Funktion
    fun forgotPassword(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            Log.d(TAG, "forgotPassword: ${task.result}")
        }
    }

    //Ausloggen Funktion
    fun logOut() {
        auth.signOut()
        _currentUser.value = null // Setze den aktuellen Benutzer auf null um den Vorgang zu beenden.
        Log.d(TAG, "logOut Successful")
    }

    //Speichern der Daten in Firestore
    fun saveInFirestore(
        firstName: String,
        surName: String,
        email: String,
        birthDate: String,
        driverLicense: Boolean,
        readyForWork: Boolean,
        profilePicture: String
    ) {
        auth.currentUser?.let { user ->
            //Referenz zum Dokument im Firestore
            val documentRef = firestore
                .collection("profiles")
                .document(user.uid)

            //Daten in ein Profile Objekt packen
            val userData = Profile(
                firstName,
                surName,
                email,
                birthDate,
                driverLicense = driverLicense,
                readyForWork = readyForWork,
                profilePicture = profilePicture
            )
            //Speichern der Daten
            documentRef.set(userData)
                .addOnSuccessListener {
                    Log.d(TAG, "User Profil wurder Erfolgreich gespeichert")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Beim Speichern des User-Profiles ist ein Fehler aufgetreten", e)
                }
        }
    }

    //Abrufen der Daten aus Firestore
    fun getUserProfile(onResult: (Map<String, Any>?) -> Unit) {
        auth.currentUser?.let { user ->
            //Referenz zum Dokument im Firestore
            val documentRef = firestore.collection("profiles").document(user.uid)

            documentRef.get().addOnSuccessListener { document -> //Erfolgreich -> widr aufgerufen wenn das Dokument erfolgreich abgerufen wurde
                    if (document.exists()) {
                        // Daten abrufen und an die Callback-Funktion übergeben
                        val userData = document.data
                        onResult(userData)
                    } else {
                        Log.d(TAG, "Dokument existiert nicht")
                        onResult(null)
                    }
                }.addOnFailureListener { e -> //Fail -> wird aufgerufen wenn ein Fehler beim Abrufen des Dokuments auftritt
                    Log.w(TAG, "Beim Fetchen der Profil-Daten ist ein Fehler aufgetreten", e)
                    onResult(null)
                }
        }
    }

    //Abrufen der Daten aus Firestore
    suspend fun getValueFromDocument(collection: String, documentId: String, field: String): Double? {
        return try {
            val documentSnapshot = firestore.collection(collection).document(documentId).get().await()
            documentSnapshot.getDouble(field) // Hier wird das Feld als Double abgefragt
        } catch (e: Exception) {
            e.printStackTrace()
            null // Wenn ein Fehler auftritt, null zurückgeben
        }
    }

    //Speichern des Bildes in Firebase Storage
    fun uploadImage(imageUri: Uri) {
        auth.currentUser?.let { user ->
            //Referenz zum Speicherort im Firebase Storage
            var storageRef = storageRef.child("images/${user.uid}/profilePicture")

            //Speichern des Bildes
            storageRef.putFile(imageUri).addOnSuccessListener { //Erfolgreich -> Bild wird hochgeladen
                Log.d(TAG, "Bild wurde erfolgreich hochgeladen")
                _downloading.value = true
                //Aktualisieren der Profilbild URL in der Firestore Datenbank
                storageRef.downloadUrl.addOnSuccessListener {
                    (profileRef?.update("profilePicture", it.toString()))
                }
            }.addOnFailureListener { //Fail -> Bild konnte nicht hochgeladen werden
                _downloading.value = false
                Log.d(TAG, "Bild konnte nicht hochgeladen werden")
            }
        }
    }
}




