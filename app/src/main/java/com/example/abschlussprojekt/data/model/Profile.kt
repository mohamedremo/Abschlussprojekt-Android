package com.example.abschlussprojekt.data.model

data class Profile(
    val firstName: String,
    val surName: String,
    val email: String,
    val birthDate: String,
    val driverLicense: Boolean,
    val wantDeliver: Boolean,
    val phoneNumber: String,
    val profilePicture: String
)
