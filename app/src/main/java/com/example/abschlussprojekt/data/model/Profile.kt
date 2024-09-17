package com.example.abschlussprojekt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

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
    var lastLocation: Location = Location(53.0793,8.8017)
){
    constructor() : this("", "", "", "", false, false, "", "", 0, 0, Location(53.0793,8.8017))
}

data class Profiles(
    val profiles: List<Profile>
)