package com.emergency.mesh.util

import android.content.Context
import androidx.room.Room
import com.emergency.mesh.data.AppDatabase

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "mesh_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
