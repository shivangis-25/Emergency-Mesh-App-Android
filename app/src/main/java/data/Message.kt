package com.emergency.mesh.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize // Add this annotation
data class Message(
    val id: String,
    val text: String,
    val messageType: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val isSynced: Boolean
) : Parcelable // Add this interface to make it "parcelable"
