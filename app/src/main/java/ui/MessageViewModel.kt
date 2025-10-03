package com.emergency.mesh.ui

import androidx.lifecycle.*
import com.emergency.mesh.data.Message
import com.emergency.mesh.data.MessageRepository
import com.emergency.mesh.p2p.MeshManager
import kotlinx.coroutines.launch
import java.util.UUID

class MessageViewModel(
    private val repository: MessageRepository,
    private val meshManager: MeshManager
) : ViewModel() {

    // A flow of all messages from the database, collected as LiveData
    val allMessages: LiveData<List<Message>> = repository.getAllMessages().asLiveData()

    /**
     * Creates and saves a new message, then passes it to the MeshManager to be broadcast.
     *
     * @param text The content of the message (e.g., "SOS" or "I'm Safe").
     * @param messageType The type of the message ("SOS" or "SAFE").
     * @param latitude The user's current latitude.
     * @param longitude The user's current longitude.
     */
    fun sendMessage(text: String, messageType: String, latitude: Double, longitude: Double) {
        // Use viewModelScope to launch a coroutine that is automatically cancelled when the ViewModel is cleared.
        viewModelScope.launch {
            val message = Message(
                id = UUID.randomUUID().toString(),
                text = text,
                messageType = messageType,
                latitude = latitude,
                longitude = longitude,
                timestamp = System.currentTimeMillis(),
                isSynced = false // New messages are always initially unsynced
            )
            // Save the message locally first
            repository.saveMessage(message)
            // Then, try to send it over the mesh network
            meshManager.sendMessage(message)
        }
    }
}

