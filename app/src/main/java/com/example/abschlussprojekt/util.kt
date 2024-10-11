package com.example.abschlussprojekt

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.airbnb.lottie.LottieAnimationView
import com.example.abschlussprojekt.data.model.Profile
import com.example.abschlussprojekt.data.model.Task
import com.example.abschlussprojekt.ui.LoginAndRegister.viewmodel.FirebaseViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Funktionen für Validierung, Toast, Berechnungen

/** Checkt, ob eine E-Mail korrekt formatiert ist */
fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
        .matches()
}

/** Zeigt eine Toast-Nachricht an */
fun toast(text: String, context: Context) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT)
        .show()
}

/** Berechnet die Entfernung zwischen zwei Punkten (in Kilometern) */
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371e3 // Radius der Erde in Metern
    val lat1Rad = Math.toRadians(lat1)
    val lat2Rad = Math.toRadians(lat2)
    val deltaLat = Math.toRadians(lat2 - lat1)
    val deltaLon = Math.toRadians(lon2 - lon1)

    val a =
        sin(deltaLat / 2) * sin(deltaLat / 2) + cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2) * sin(
            deltaLon / 2
        )
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return (R * c) / 1000 // Entfernung in Kilometern
}

// Funktionen für UI und Animationen

/** Setzt Lottie-Animationen basierend auf dem Level */
fun setLottieByLevel(level: Int): String {
    val lottieFiles = listOf(
        "one.json",
        "two.json",
        "three.json",
        "four.json",
        "five.json",
        "six.json",
        "seven.json",
        "eight.json",
        "nine.json",
        "ten.json"
    )
    return if (level in 1..10) lottieFiles[level - 1] else lottieFiles.last()
}

/** Handhabt Klicks außerhalb einer View */
fun outsideTouch(view: View, event: MotionEvent): Boolean {
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    val x = event.rawX
    val y = event.rawY
    return x < location[0] || x > location[0] + view.width || y < location[1] || y > location[1] + view.height
}

// Datum- und Zeit-Picker Funktionen

/** Zeigt einen Date- und Time-Picker an */
fun showDateTimePicker(context: Context, onDateTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()

    // DatePicker anzeigen
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)

            // TimePicker anzeigen
            TimePickerDialog(context, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                onDateTimeSelected(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()

        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

/** Zeigt nur einen Date-Picker an */
fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()

    // DatePicker anzeigen
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            onDateSelected(dateFormat.format(calendar.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

// Formatierungs- und Hilfsfunktionen

/** Formatiert einen Double-Wert ins Euro-Format */
fun formatToEuro(value: Double?): String {
    return String.format(Locale.GERMANY, "%.2f €", value ?: 0.0)
}

/** Erzeugt eine zufällige UUID */
fun generateUUID(): String {
    return UUID.randomUUID()
        .toString()
}

// Firebase-Interaktionen

/** Lädt Tasks auf der Karte an und zeigt sie mit einem Custom-Icon an */
fun displayTasksOnMap(googleMap: GoogleMap, tasks: List<Task>) {
    for (task in tasks) {
        val position = LatLng(task.location.latitude, task.location.longitude)
        val icon = BitmapDescriptorFactory.fromResource(R.drawable.mapsmarker)
        googleMap.addMarker(
            MarkerOptions()
                .position(position)
                .title(task.taskName)
                .icon(icon)
                .snippet("Beschreibung: ${task.description}")
                .anchor(0.5f, 0.5f)
        )
    }
}

// Funktionen für Weather und Status

/** Fügt einen °C Suffix zu einem String hinzu */
fun stringToCelsius(string: String): String {
    return "$string °C"
}

/** Gibt eine Begrüßungsnachricht zurück */
fun welcomeMessage(name: String): String {
    return "Welcome $name"
}

/** Setzt den Online-Status basierend auf der Lottie-Animation */
fun setOnlineStatus(lottie: LottieAnimationView, readyForWork: Boolean) {
    if (readyForWork) {
        lottie.setAnimation("online.json")
        lottie.loop(true)
        lottie.playAnimation()
    } else {
        lottie.setAnimation("offline.json")
        lottie.loop(false)
        lottie.playAnimation()
    }
}

// Mock-Daten Initialisierung

/** Lädt Task-Mockdaten aus einer JSON-Datei in Firebase */
fun initTasksFromJsonToFirebase(
    TAG: String,
    fireViewModel: FirebaseViewModel,
    assets: AssetManager
) {
    val json = assets.open("tasks.json")
        .bufferedReader()
        .use { it.readText() }
    Log.e(TAG, json)

    val listToSave = Gson().fromJson(json, Array<Task>::class.java)
        .toList()
    fireViewModel.saveAllTasks(listToSave)
    Log.d(TAG, "Anzahl der geladenen Tasks: ${listToSave.size}")
}

/** Lädt Profil-Mockdaten aus einer JSON-Datei in Firebase */
fun initProfilesFromJsonToFirebase(
    TAG: String,
    fireViewModel: FirebaseViewModel,
    assets: AssetManager
) {
    val json = assets.open("profiles.json")
        .bufferedReader()
        .use { it.readText() }
    Log.e(TAG, json)

    val listToSave = Gson().fromJson(json, Array<Profile>::class.java)
        .toList()
    fireViewModel.saveAllProfiles(listToSave)
    Log.d(TAG, "Anzahl der geladenen Profile: ${listToSave.size}")
}

fun randomCategory(): String {
    val listOfCategories = listOf(
        "Handwerker",
        "Wunsch",
        "Etwas ausleihen",
        "Etwas Abholen"
    )
    return listOfCategories.random()
}

fun pickImage(activityResultLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "image/*"
    }
    activityResultLauncher.launch(intent)
}


