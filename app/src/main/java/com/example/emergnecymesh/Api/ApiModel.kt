package com.example.emergnecymesh.api



import com.google.gson.annotations.SerializedName

/**
 * Request/Response models for API communication
 */

// Emergency Alert Request
data class EmergencyAlertRequest(
    @SerializedName("device_id")
    val deviceId: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("message")
    val message: String,

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("phone_number")
    val phoneNumber: String? = null
)

// Mesh Message Request
data class MeshMessageRequest(
    @SerializedName("message_id")
    val messageId: String,

    @SerializedName("sender_id")
    val senderId: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("hop_count")
    val hopCount: Int,

    @SerializedName("ttl")
    val ttl: Int
)

// Generic API Response
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T? = null
)

// Sync Status
data class SyncStatus(
    @SerializedName("synced_count")
    val syncedCount: Int,

    @SerializedName("failed_count")
    val failedCount: Int,

    @SerializedName("last_sync")
    val lastSync: Long
)