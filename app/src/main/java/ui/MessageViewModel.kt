package com.emergency.mesh.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emergency.mesh.p2p.MeshManager
import kotlinx.coroutines.launch

class MessageViewModel(private val meshManager: MeshManager) : ViewModel() {

    // Send SOS message (goes red on map)
    fun sendSOS(latitude: Double, longitude: Double) {
        val message = "SOS|$latitude|$longitude"
        sendMessage(message)
    }

    // Send “I am safe” message (goes green on map)
    fun sendSafe(latitude: Double, longitude: Double) {
        val message = "SAFE|$latitude|$longitude"
        sendMessage(message)
    }

    // Broadcast message via MeshManager
    private fun sendMessage(message: String) {
        viewModelScope.launch {
            meshManager.broadcastMessage(message)
        }
    }

    // Listen for messages from peers
    fun listenForMessages(onMessageReceived: (String) -> Unit) {
        meshManager.onMessageReceived = onMessageReceived
    }
}
