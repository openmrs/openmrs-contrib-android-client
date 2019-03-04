package org.openmrs.mobile.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.openmrs.mobile.models.*

@Database(
        version = 7, entities = [
    Patient::class,
    Concept::class,
    Visit::class,
    Encounter::class,
    Observation::class,
    Location::class
], exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app-database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
        }
    }

}