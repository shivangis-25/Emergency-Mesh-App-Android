package com.example.emergnecymesh.model


import java.util.UUID

/**
 * Message model for P2P communication in the Emergency Mesh network
 */
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val ttl: Int = 10, // Time-to-live for mesh routing
    val hopCount: Int = 0 // Number of hops in mesh network
) {
    /**
     * Create a copy of this message with incremented hop count
     * Used for mesh routing
     */
    fun incrementHop(): Message {
        return copy(hopCount = hopCount + 1, ttl = ttl - 1)
    }

    /**
     * Check if message should still be forwarded
     */
    fun isValid(): Boolean {
        return ttl > 0
    }
}
