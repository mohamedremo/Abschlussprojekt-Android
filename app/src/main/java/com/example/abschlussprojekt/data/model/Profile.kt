package com.example.abschlussprojekt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("profile_table")
data class Profile(
    @PrimaryKey()
    val firstName: String,
    val surName: String,
    val email: String,
    val birthDate: String,
    val role: String = "",
    val level: Int = 0,
    val points: Int = 0,
    val lastLongitude: Double = 0.0,
    val lastLatitude: Double = 0.0,
    val driverLicense: Boolean,
    val readyForWork: Boolean,
    val profilePicture: String
)

data class Profiles(
    val profiles: List<Profile>
)