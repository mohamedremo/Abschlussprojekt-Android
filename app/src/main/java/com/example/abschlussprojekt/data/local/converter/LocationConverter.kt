package com.example.abschlussprojekt.data.local.converter

import androidx.room.TypeConverter
import com.example.abschlussprojekt.data.model.Location

class LocationConverter {
    @TypeConverter
    fun fromLocation(location: Location): String {
        return "${location.latitude},${location.longitude}"
    }

    @TypeConverter
    fun toLocation(locationString: String): Location {
        val parts = locationString.split(",")
        return Location(parts[0].toDouble(), parts[1].toDouble())
    }
}