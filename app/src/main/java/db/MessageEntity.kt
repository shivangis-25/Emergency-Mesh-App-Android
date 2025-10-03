package com.emergency.mesh.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// Represents a message stored in the local Room database.
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val text: String,
    val latitude: Double,
    val longitude: Double,
    val locationName: String? = null, // e.g., "Bengaluru, Karnataka, India"
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val messageType: String // "SOS" or "SAFE"
)