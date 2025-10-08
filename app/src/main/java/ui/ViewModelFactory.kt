package com.emergency.mesh.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emergency.mesh.p2p.MeshManager

class ViewModelFactory(private val meshManager: MeshManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            return MessageViewModel(meshManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
