package com.emergency.mesh.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val text: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val ttl: Int
)
