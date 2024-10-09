package com.example.abschlussprojekt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName

data class Task(
    val taskName: String,
    val description: String?,
    val createdFrom: String,
    val created: String,
    val expire: String,
    val category: String,
    val butlePoints: Int,
    val location: GeoPoint,
    @PropertyName("finished") //Der Name wie er in Firestore gespeichert wird
    val isFinished: Boolean,
){
    constructor() : this("", "", "", "", "", "", 0, GeoPoint(0.0, 0.0), false)
}


