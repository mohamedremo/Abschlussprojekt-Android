package com.example.abschlussprojekt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Entity(tableName = "task_table")
data class Task(
    val createdAt: String,
    val taskName: String,
    val client: String,
    val description: String,
    val expireDate: String,
    val butlePoints: Int,
    val longitude: Double,
    val latitude: Double,
    val isFinished: Boolean,
//    val category: Category,
    @PrimaryKey
    val id: String
)
//enum class Category(var displayName: String) {
//    DIENSTLEISTUNG("Dienstleistung"),
//    FAHRT("Fahrt"),
//    PRODUKT("Produkt"),
//    SONSTIGES("Sonstiges")
//}
