package com.example.abschlussprojekt

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


//Checken ob Email korrekt Formatiert ist.
fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun toast(text: String,context: Context?) = Toast.makeText(context, text, Toast.LENGTH_LONG)
    .show()

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