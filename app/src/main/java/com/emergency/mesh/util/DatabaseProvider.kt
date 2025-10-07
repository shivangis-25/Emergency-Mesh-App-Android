package com.emergency.mesh.util

import android.content.Context
import androidx.room.Room
import com.emergency.mesh.data.AppDatabase

object DatabaseProvider {
    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "emergency_mesh_database"
            )
                .fallbackToDestructiveMigration() // For development - removes this in production
                .build()
            database = instance
            instance
        }
    }
}