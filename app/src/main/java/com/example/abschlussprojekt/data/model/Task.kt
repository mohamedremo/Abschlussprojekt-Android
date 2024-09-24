package com.example.abschlussprojekt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.GeoPoint


@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey
    val taskName: String,
    val description: String?,
    val createdFrom: String,
    val due: String,
    val expire: String,
    val category: Category,
    val butlePoints: Int,
    val location: GeoPoint,
    val isFinished: Boolean,
)
enum class Category(var displayName: String) {
    DIENSTLEISTUNG("Dienstleistung"),
    FAHRT("Fahrt"),
    PRODUKT("Produkt"),
    SONSTIGES("Sonstiges")
}


