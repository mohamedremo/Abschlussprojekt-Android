package com.example.abschlussprojekt.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.abschlussprojekt.data.local.converter.geoConverter
import com.example.abschlussprojekt.data.model.Profile
import com.example.abschlussprojekt.data.model.Task

@Database(entities = [Profile::class, Task::class], version = 2, exportSchema = false)
@TypeConverters(geoConverter::class)
abstract class MyButlerDatabase : RoomDatabase() {
    abstract val dao: MyButlerDao

    //Datenbank erstellen
    companion object {
        @Volatile
        private lateinit var INSTANCE: MyButlerDatabase

        //Datenbank initialisieren wenn sie noch nicht existiert
        fun getDatabase(context: Context): MyButlerDatabase {
            synchronized(MyButlerDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        MyButlerDatabase::class.java,
                        "butler_database" //Datenbank Name
                    )
                        .build()
                }
                return INSTANCE
            }
        }
    }
}
