package com.emergency.mesh.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import androidx.annotation.Keep

/**
 * Represents a message stored in the local Room database.
 */
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val senderNumber: String,  // 👈 Add this too
    val text: String,
    val messageType: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val isSynced: Boolean
)

