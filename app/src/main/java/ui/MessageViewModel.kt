package com.emergency.mesh.ui

import androidx.lifecycle.*
import com.emergency.mesh.data.Message
import com.emergency.mesh.data.MessageRepository2
import com.emergency.mesh.p2p.MeshManager
import kotlinx.coroutines.launch
import java.util.UUID

class MessageViewModel(
    private val repository: MessageRepository2,
    private val meshManager: MeshManager,
    private val senderNumber: String // ✅ Added parameter
) : ViewModel() {

    val allMessages: LiveData<List<Message>> = repository.getAllMessages().asLiveData()

    fun sendMessage(text: String, messageType: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val message = Message(
                id = UUID.randomUUID().toString(),
                text = text,
                messageType = messageType,
                latitude = latitude,
                longitude = longitude,
                timestamp = System.currentTimeMillis(),
                senderNumber = senderNumber, // ✅ Added this line
                isSynced = false
            )

            repository.saveMessage(message)
            meshManager.sendMessage(message)
        }
    }
}
