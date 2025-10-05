package com.emergency.mesh.data

import kotlinx.coroutines.flow.Flow

// Interface defining the contract for message data operations
interface MessageRepository2 {
    suspend fun saveMessage(message: Message)
    suspend fun getUnsyncedMessages(): List<Message>
    suspend fun markAsSynced(messageId: String)
    fun getAllMessages(): Flow<List<Message>>
}
