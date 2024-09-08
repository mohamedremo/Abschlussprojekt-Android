package com.example.abschlussprojekt.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.abschlussprojekt.data.model.Profile
import com.example.abschlussprojekt.data.model.Task

@Dao
interface MyButlerDao {
    @Query("SELECT * FROM profile_table")
    fun getProfile(): Profile

    @Query("SELECT * FROM task_table WHERE category = :category")
    fun getTasksByCategory(category: String): List<Task>

    @Insert
    fun insertProfile(profile: Profile)

    @Insert
    fun insertTask(task: Task)

    @Insert
    fun insertAllTasks(tasks: List<Task>)

    @Query("DELETE FROM profile_table")
    fun deleteProfile()

    @Query("DELETE FROM task_table")
    fun deleteTasks()



}