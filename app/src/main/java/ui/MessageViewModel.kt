package com.emergency.mesh.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emergency.mesh.p2p.MeshManager
import kotlinx.coroutines.launch

class MessageViewModel(private val meshManager: MeshManager) : ViewModel() {

    fun sendSOS(latitude: Double, longitude: Double) {
        val message = "🚨 SOS ALERT | Lat: $latitude | Lon: $longitude"
        viewModelScope.launch {
            meshManager.sendMessage(message)
        }
    }

    fun sendSafe(latitude: Double, longitude: Double) {
        val message = "✅ I'M SAFE | Lat: $latitude | Lon: $longitude"
        viewModelScope.launch {
            meshManager.sendMessage(message)
        }
    }

    fun listenForMessages(onMessageReceived: (String) -> Unit) {
        meshManager.onMessageReceived = { message ->
            onMessageReceived(message)
        }
    }
}
