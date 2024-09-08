package com.example.abschlussprojekt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    val createdAt: String,
    val name: String,
    val client: String,
    val description: String,
    val expireDate: String,
    val butlePoints: Int,
    val longitude: Double,
    val latitude: Double,
    val isFinished: Boolean,
    val category: String,
    @PrimaryKey
    val id: String
)
