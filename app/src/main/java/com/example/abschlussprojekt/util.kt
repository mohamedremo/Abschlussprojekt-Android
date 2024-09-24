package com.example.abschlussprojekt

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


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

fun setLottieByLevel(level: Int): String {
    val lottieFiles = listOf(
        "one.json", "two.json", "three.json",
        "four.json", "five.json", "six.json",
        "seven.json", "eight.json", "nine.json", "ten.json"
    )
    return if (level in 1..10) lottieFiles[level - 1] else lottieFiles.last()
}

fun outsideTouch(view: View, event: MotionEvent): Boolean {
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    val x = event.rawX
    val y = event.rawY

    return x < location[0] || x > location[0] + view.width ||
            y < location[1] || y > location[1] + view.height
}


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

                    // Hier wird das Datum und die Uhrzeit in der gewünschten Formatierung formatiert
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    val formattedDateTime = dateFormat.format(calendar.time)

                    // Callback aufrufen mit dem ausgewählten Datum und Uhrzeit
                    onDateTimeSelected(formattedDateTime)

                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // Hie
            ).show()

        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    dialog.show()
}