package com.emergency.mesh.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emergency.mesh.data.MessageRepository2
import com.emergency.mesh.p2p.MeshManager

class ViewModelFactory(
    private val repository: MessageRepository2,
    private val meshManager: MeshManager,
    private val senderNumber: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageViewModel(repository, meshManager, senderNumber) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

