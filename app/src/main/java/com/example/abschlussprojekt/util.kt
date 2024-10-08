package com.example.abschlussprojekt
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import coil.load
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.abschlussprojekt.data.model.Profile
import com.example.abschlussprojekt.data.model.Task
import com.example.abschlussprojekt.ui.LoginAndRegister.viewmodel.FirebaseViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.abschlussprojekt.ui.MainMenu.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.GeoPoint
import com.google.android.material.dialog.MaterialAlertDialogBuilder


//Checken ob Email korrekt Formatiert ist.
fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
        .matches()
}

fun toast(text: String, context: Context) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT)
        .show()
}

//Entfernung zwischen zwei Punkten berechnen
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371e3 // Radius der Erde in Metern

    val lat1Rad = Math.toRadians(lat1)
    val lat2Rad = Math.toRadians(lat2)
    val deltaLat = Math.toRadians(lat2 - lat1)
    val deltaLon = Math.toRadians(lon2 - lon1)

    val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
            cos(lat1Rad) * cos(lat2Rad) *
            sin(deltaLon / 2) * sin(deltaLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    val distance = R * c // Entfernung in Metern
    return distance / 1000 // Entfernung in Kilometern
}

//Lottie Animation anhand des im Profil gespeicherten Levels
fun setLottieByLevel(level: Int): String {
    val lottieFiles = listOf(
        "one.json", "two.json", "three.json",
        "four.json", "five.json", "six.json",
        "seven.json", "eight.json", "nine.json", "ten.json"
    )
    return if (level in 1..10) lottieFiles[level - 1] else lottieFiles.last()
}

//Ein Funktion die es ermöglicht eine View zu fokussieren um somit klicks ausserhalb dieser View zu erkennen
// (Wird meistens für Schwebende UI Elemente benutzt.
fun outsideTouch(view: View, event: MotionEvent): Boolean {
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    val x = event.rawX
    val y = event.rawY

    return x < location[0] || x > location[0] + view.width ||
            y < location[1] || y > location[1] + view.height
}

//Funktion zum anzeigen von Datum und Uhrzeit Picker
fun showDateTimePicker(
    context: Context,         // Übergib den Context als Parameter
    onDateTimeSelected: (String) -> Unit   // Callback für das ausgewählte Datum und Uhrzeit
) {
    val calendar = Calendar.getInstance()

    // DatePicker anzeigen
    val dialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Nachdem das Datum ausgewählt wurde wird es in der Calendar-Instanz gespeichert
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Nachdem das Datum ausgewählt wurde wird ein TimePickerDialog angezeigt
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    // Wenn die Uhrzeit ausgewählt wurde wird diese auch in der Calendar-Instanz gespeichert
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)

                    // Hier wird das Datum und die Uhrzeit in die gewünschten Formatierung formatiert
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    val formattedDateTime = dateFormat.format(calendar.time)

                    // Callback aufrufen mit dem ausgewählten Datum und Uhrzeit
                    onDateTimeSelected(formattedDateTime)

                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // Hier wird die Uhrzeit in 24-Stunden-Formatierung angezeigt
            ).show()

        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    dialog.show()
}

fun showDatePicker(
    context: Context,         // Übergib den Context als Parameter
    onDateSelected: (String) -> Unit   // Callback für das ausgewählte Datum
) {
    val calendar = Calendar.getInstance()

    // DatePicker anzeigen
    val dialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Nachdem das Datum ausgewählt wurde wird es in der Calendar-Instanz gespeichert
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Hier wird das Datum in die gewünschte Formatierung formatiert
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(calendar.time)

            // Callback aufrufen mit dem ausgewählten Datum
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    dialog.show()
}

//Funktion zum Formatieren von Double-Zahlen auf 2 Stellen nach dem Komma und im Euro Format.
fun formatToEuro(value: Double?): String {
    return String.format(Locale.GERMANY, "%.2f €", value ?: 0.0)
}

fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    return dateFormat.format(Date()) // Hier wird das aktuelle Datum formatiert und zurückgegeben
}

fun generateUUID(): String {
    return UUID.randomUUID()
        .toString()
}

/* Diese Funktion wird aufgerufen um die Tasks auf der Karte anzuzeigen und
 * diesen dann ein Custom Icon zuzuweisen + Description hinzufügen.*/
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

fun stringToCelsius(string: String): String {
    return string + " °C"
}

fun welcomeMessage(name: String): String {
    return "Welcome $name"
}

fun setProfilePic(TAG: String, imageView: ImageView, pic: Uri?) {
    try {
        imageView.load(pic) {
            crossfade(true)
            crossfade(500)
        }
        Log.d(TAG, "Profil Pic setted")
    } catch (e: Exception) {
        Log.d(TAG, "Cannot set Profil Pic!")
        e.printStackTrace()
    }
}

fun setOnlineStatus(lottie: LottieAnimationView, readyForWork: Boolean) {
    if (readyForWork) {
        lottie.repeatMode = LottieDrawable.RESTART
        lottie.setAnimation("online.json")
        lottie.playAnimation()
        lottie.loop(true)
    } else {
        lottie.repeatMode = LottieDrawable.REVERSE
        lottie.setAnimation("offline.json")
        lottie.playAnimation()
        lottie.setRepeatCount(0)
    }
}

fun initTasksFromJsonToFirebase(
    TAG: String,
    fireViewModel: FirebaseViewModel,
    assets: AssetManager
) {
    val json = assets.open("tasks.json")
        .bufferedReader()
        .use {
            it.readText()
        }
    Log.e(TAG, json)
    val listToSave = Gson().fromJson(json, Array<Task>::class.java).toList()
    fireViewModel.saveAllTasks(listToSave)
    Log.d(TAG, "Anzahl der geladenen Tasks: ${listToSave.size}")
}


fun initProfilesFromJsonToFirebase(
    TAG: String,
    fireViewModel: FirebaseViewModel,
    assets: AssetManager
) {
    val json = assets.open("profiles.json")
        .bufferedReader()
        .use {
            it.readText()
        }
    Log.e(TAG, json)
    val listToSave = Gson().fromJson(json, Array<Profile>::class.java).toList()
    fireViewModel.saveAllProfiles(listToSave)
    Log.d(TAG, "Anzahl der geladenen Tasks: ${listToSave.size}")
}



object LocationUtil {

    fun requestInitialPermissions(
        context: Context,
        requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
        activity: MainActivity
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            showRationaleDialog(context) {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    fun getLastLocation(
        context: Context,
        fusedLocationClient: FusedLocationProviderClient,
        fireViewModel: FirebaseViewModel,
        viewModel: WeatherViewModel,
        onSuccess: (GeoPoint) -> Unit,
        onFailure: () -> Unit
    ) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    fireViewModel.profile.value?.apply {
                        lastLocation = GeoPoint(latitude, longitude)
                    }
                    viewModel.getWeatherByLocation(latitude, longitude)
                    onSuccess(GeoPoint(latitude, longitude))
                } else {
                    onFailure()
                }
            }
        } else {
            onFailure() // Fehler-Callback aufrufen
        }
    }

    private fun showRationaleDialog(context: Context, onOk: () -> Unit) {
        MaterialAlertDialogBuilder(context).setTitle("Benötigt Berechtigung")
            .setMessage("Die App benötigt die folgende Berechtigung, um deine Aktivität zu tracken.")
            .setPositiveButton("OK") { _, _ -> onOk() }
            .show()
    }

    fun showSettingsDialog(context: Context) {
        MaterialAlertDialogBuilder(context).setTitle("Berechtigungen dauerhaft abgelehnt")
            .setMessage("Die Standortberechtigungen wurden dauerhaft abgelehnt. Du musst sie in den App-Einstellungen manuell aktivieren.")
            .setPositiveButton("Zu den Einstellungen") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
            .setNegativeButton("Abbrechen", null)
            .show()
    }
}


