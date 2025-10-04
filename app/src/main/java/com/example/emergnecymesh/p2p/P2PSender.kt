package com.example.emergnecymesh.p2p



import com.example.emergnecymesh.model.Message

/**
 * Interface for sending P2P messages
 * Implemented by P2PManager and can be injected into mesh layer
 */
interface P2PSender {
    /**
     * Send a message to all connected peers
     */
    fun sendMessage(message: Message)
}