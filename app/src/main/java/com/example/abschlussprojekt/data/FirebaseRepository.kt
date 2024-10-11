package com.example.abschlussprojekt.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.abschlussprojekt.data.model.Order
import com.example.abschlussprojekt.data.model.Product
import com.example.abschlussprojekt.data.model.Profile
import com.example.abschlussprojekt.data.model.Task
import com.example.abschlussprojekt.isValidEmail
import com.example.abschlussprojekt.randomCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.type.LatLng
import kotlinx.coroutines.tasks.await
import java.io.File

private const val TAG = "FirebaseRepository"

class FirebaseRepository {

    //Firebase Authentifizierung
    private val auth = FirebaseAuth.getInstance()

    //Firebase Firestore
    private val firestore = FirebaseFirestore.getInstance()

    //Firebase Storage
    private val storage = FirebaseStorage.getInstance()

    //Eine Referenz zum Speicherort im Firebase Storage
    private val storageRef = storage.reference

    //Der aktuelle Benutzer
    private val _currentUser = MutableLiveData<FirebaseUser?>(auth.currentUser)
    val currentUser: MutableLiveData<FirebaseUser?>
        get() = _currentUser

    //Das aktuelle Profil
    private val _profile = MutableLiveData<Profile?>()
    val profile: LiveData<Profile?>
        get() = _profile

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: MutableLiveData<List<Task>>
        get() = _tasks

    private val _selectedTask = MutableLiveData<Task>()
    val selectedTask: LiveData<Task>
        get() = _selectedTask

    private val _myTasks = MutableLiveData<Task?>()
    val myTasks: LiveData<Task?>
        get() = _myTasks

    //Initialisierung der Authentifizierung, wenn ein User eingeloggt ist.
    init {
        auth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
            auth.currentUser?.uid?.let {
                val ref = firestore.collection("profiles")
                    .document(it)
                ref.addSnapshotListener { value, error ->
                    if (error == null) {
                        val profile = value?.toObject(Profile::class.java)
                        _profile.postValue(profile)
                    }
                }
            }
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
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    //Wenn Registrierung erfolgreich
                    if (task.isSuccessful) {
                        _currentUser.postValue(task.result.user) // Aktuellen Benutzer in Live Data aktualisieren
                        saveProfileInFireStore(
                            firstName,
                            surName,
                            email,
                            birthDate,
                            driverLicense,
                            readyForWork,
                            profilePicture
                        ) //Firestore Speichern
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
        if (isValidEmail(email)) {
            try {
                //Login mit Email und Passwort
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
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

    //Ausloggen Funktion
    fun logOut() {
        _profile.postValue(null) // Profilreferenz zurücksetzen
        _currentUser.value =
            null // Setze den aktuellen Benutzer auf null um den Vorgang zu beenden.
        auth.signOut()
        Log.d(TAG, "logOut Successful")
    }

    //Passwort vergessen Funktion
    fun forgotPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                Log.d(TAG, "forgotPassword: ${task.result}")
            }
    }

    //Speichern eines Profile im Firestore mit der dazugehörigen UUID
    fun saveProfileInFireStore(
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
                driverLicense,
                readyForWork,
                profilePicture
            )
            //Speichern der Daten
            documentRef.set(userData)
                .addOnSuccessListener {
                    Log.d(TAG, "User Profil wurde Erfolgreich hochgeladen")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Beim Speichern des User-Profiles ist ein Fehler aufgetreten", e)
                }
        }
    }

    //Speichern einer Liste von Profile im Firestore
    fun saveAllProfiles(profiles: List<Profile>) {
        profiles.forEach {
            firestore.collection("profiles")
                .document()
                .set(it)
        }
        Log.d(TAG, "Alle Profile wurden erfolgreich gespeichert")
    }

    //Abrufen des aktuellen Users
    fun getUserProfile(onResult: (Map<String, Any>?) -> Unit) {
        auth.currentUser?.let { user ->
            //Referenz zum Dokument im Firestore
            val profileRef = firestore.collection("profiles")
                .document(user.uid)

            profileRef.get()
                .addOnSuccessListener { document -> //Erfolgreich -> widr aufgerufen wenn das Dokument erfolgreich abgerufen wurde
                    if (document.exists()) {
                        // Daten abrufen und an die Callback-Funktion übergeben
                        val userData = document.data
                        onResult(userData)
                    } else {
                        Log.d(TAG, "Dokument existiert nicht")
                        onResult(null)
                    }
                }
                .addOnFailureListener { e -> //Fail -> wird aufgerufen wenn ein Fehler beim Abrufen des Dokuments auftritt
                    Log.w(TAG, "Beim Fetchen der Profil-Daten ist ein Fehler aufgetreten", e)
                    onResult(null)
                }
        }
    }

    //Speichern EINER Task in Firestore
    fun saveTaskInFireStore(newTask: Task) {
        auth.currentUser?.let { user ->
            //Referenz zum Dokument im Firestore
            val documentRef = firestore.collection("tasks")
                .document(user.email.toString())

            //Speichern der Daten
            documentRef.set(newTask)
                .addOnSuccessListener {
                    Log.d(TAG, "Task wurde Erfolgreich hochgeladen und angelegt.")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Beim Speichern des Tasks ist ein Fehler aufgetreten", e)
                }
        }
    }

    //Abrufen des zuletzt erstellenten Tasks
    fun getMyTasks() {
        auth.currentUser?.let { user ->
            //Referenz zum Dokument im Firestore
            val taskRef = firestore.collection("tasks")
                .document(user.email.toString())

            taskRef.get()
                .addOnSuccessListener { document -> //Erfolgreich -> widr aufgerufen wenn das Dokument erfolgreich abgerufen wurde
                    if (document.exists()) {
                        // Daten abrufen und an die Callback-Funktion übergeben
                        val taskData = document.toObject(Task::class.java)
                        _myTasks.postValue(taskData)
                    } else {
                        Log.d(TAG, "Dokument existiert nicht")
                    }
                }
                .addOnFailureListener { e -> //Fail -> wird aufgerufen wenn ein Fehler beim Abrufen des Dokuments auftritt
                    Log.w(TAG, "Beim Fetchen der Tasks ist ein Fehler aufgetreten", e)
                }
        }
    }

    suspend fun fetchTasks() {
        val taskList = firestore.collection("tasks")
            .get()
            .await().documents.map { document ->
                document.toObject(Task::class.java)
                    ?.copy(
                        location = document.getGeoPoint("location") ?: GeoPoint(0.0, 0.0)
                    ) ?: Task(
                    "",
                    "",
                    "",
                    "",
                    "",
                    randomCategory(), // Hier werden zufällig die werte für die Task erstellt
                    0,
                    GeoPoint(0.0, 0.0),
                    false
                )
            }
        _tasks.postValue(taskList)
    }

    //Speichern einer Liste von Produkten im Firestore
    fun saveAllProducts(products: List<Product>) {
        products.forEach {
            firestore.collection("products")
                .document(it.name)
                .set(it)
        }
    }

    //Speichern einer Liste von Tasks im Firestore
    fun saveAllTasks(tasks: List<Task>) {
        tasks.forEach {
            firestore.collection("tasks")
                .document(it.createdFrom)
                .set(it)
        }
    }

    //Alle Produkte aus Firestore abrufen
    fun getAllProductsFromFirestore(onResult: (List<Product>) -> Unit) {

        val products = mutableListOf<Product>()

        firestore.collection("products")
            .get()
            .addOnSuccessListener { collection ->
                for (document in collection) {
                    val product = document.toObject(Product::class.java)
                    products.add(product)
                }
                onResult(products)

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Beim Abrufen der Produkte ist ein Fehler aufgetreten", e)
                onResult(emptyList())
            }
    }

    //Speichern der Bestellung in Firestore
    fun saveOrderInFireStore(order: Order) {
        auth.currentUser?.let { user ->
            //Referenz zum Dokument im Firestore
            val documentRef = firestore
                .collection("orders")
                .document(order.orderId)
                .set(order)
                .addOnSuccessListener {
                    Log.d(TAG, "Order wurde Erfolgreich hochgeladen und angelegt.")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Beim Speichern des Orders ist ein Fehler aufgetreten", e)
                }
        }
    }

    //Speichern einer PDF in Firebase Storage
    fun uploadPdfToFirebaseStorage(filePath: String, orderId: String) {
        val file = File(filePath)
        val storageRef = FirebaseStorage.getInstance()
            .getReference("orders/$orderId.pdf")

        val uploadTask = storageRef.putFile(Uri.fromFile(file))
        uploadTask.addOnSuccessListener {
            Log.d("Firebase", "PDF erfolgreich hochgeladen.")
        }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Fehler beim Hochladen des PDFs: ${e.message}")
            }
    }

    //Speichern eines Profil-Bildes in Firebase Storage
    fun uploadImage(imageUri: Uri) {
        auth.currentUser?.let { user ->
            //Referenz zum Speicherort im Firebase Storage
            var storageRef = storageRef.child("images/${user.uid}/profilePicture")

            //Speichern des Bildes
            storageRef.putFile(imageUri)
                .addOnSuccessListener { //Erfolgreich -> Bild wird hochgeladen
                    Log.d(TAG, "Bild wurde erfolgreich auf Firestore hochgeladen")
                    //Aktualisieren der Profilbild URL in der Firestore Datenbank
                    storageRef.downloadUrl.addOnSuccessListener {
                        //Downloading State wird auf false gesetzt
                        Log.d(TAG, "Bild wurde erfolgreich vom Firestore heruntergeladen")
                        _profile.value = _profile.value?.copy(profilePicture = it.toString())
                        //Referenz zum Dokument im Firestore
                        val profileRef = firestore.collection("profiles")
                            .document(user.uid)
                        //Aktualisieren der Daten im Firestore
                        profileRef.update("profilePicture", it.toString())
                            .addOnSuccessListener {
                                Log.d(
                                    TAG,
                                    "Profilbild URL wurde erfolgreich im Firestore aktualisiert"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w(
                                    TAG,
                                    "Beim Aktualisieren der Profilbild URL im Firestore ist ein Fehler aufgetreten",
                                    e
                                )
                            }
                    }
                        .addOnFailureListener { //Fail -> Bild konnte nicht hochgeladen werden

                            Log.d(TAG, "Bild konnte nicht hochgeladen werden")
                        }
                }
        }
    }

    fun updateProfile(firstName: String, surName: String, birthDate: String) {
        auth.currentUser?.let { user ->
            val profileRef = firestore.collection("profiles")
                .document(user.uid)
            profileRef.update("firstName", firstName)
            profileRef.update("surName", surName)
            profileRef.update("birthDate", birthDate)
                .addOnSuccessListener {
                    Log.d(TAG, "Profile erfolgreich aktualisiert")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Beim Aktualisieren des Profiles ist ein Fehler aufgetreten", e)
                }
        }
    }

    fun setOnlineStatus(status: Boolean) {
        auth.currentUser?.let { user ->
            val profileRef = firestore.collection("profiles")
                .document(user.uid)
            profileRef.update("readyForWork", status)
                .addOnSuccessListener {
                    Log.d(TAG, "Status erfolgreich aktualisiert")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Beim Aktualisieren des Status ist ein Fehler aufgetreten", e)
                }
        }
    }

    fun setSelectedTask(task: Task) {
        _selectedTask.value = task
    }

    fun updateLastLocation(location: GeoPoint) {
        auth.currentUser?.let { user ->
            val profileRef = firestore.collection("profiles")
                .document(currentUser.value?.uid!!)
            profileRef.update("lastLocation", location)
        }
    }
}

