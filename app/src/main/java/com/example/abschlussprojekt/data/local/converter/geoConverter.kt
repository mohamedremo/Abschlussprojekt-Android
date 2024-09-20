package com.example.abschlussprojekt.data.local.converter

import androidx.room.TypeConverter
import com.google.firebase.firestore.GeoPoint


class geoConverter {
    @TypeConverter
    fun fromGeoPoint(geoPoint: GeoPoint): String {
        return "${geoPoint.latitude},${geoPoint.longitude}"
    }

    @TypeConverter
    fun toGeoPoint(value: String): GeoPoint {
        val parts = value.split(",")
        val latitude = parts[0].toDouble()
        val longitude = parts[1].toDouble()
        return GeoPoint(latitude, longitude)
    }

    @TypeConverter
    fun fromStringList(list: List<String>) : String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String) : List<String> {
        return value.split(",")
    }

}