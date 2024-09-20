package com.example.abschlussprojekt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.GeoPoint
import java.time.LocalDate
import java.util.UUID

@Entity("profile_table")
data class Profile(
    @PrimaryKey()
    val firstName: String,
    val surName: String,
    val email: String,
    val birthDate: String,
    val driverLicense: Boolean,
    val readyForWork: Boolean,
    val profilePicture: String,
    val role: String = "",
    val level: Int = 0,
    val points: Int = 0,
    var lastLocation: GeoPoint = GeoPoint(53.0793,8.8017)
){
    constructor() : this("", "", "", "", false, false, "", "", 0, 0, GeoPoint(53.0793,8.8017))
}

data class Profiles(
    val profiles: List<Profile>
)