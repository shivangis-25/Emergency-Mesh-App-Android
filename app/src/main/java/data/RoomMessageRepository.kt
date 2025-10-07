package com.emergency.mesh.data

import com.emergency.mesh.db.MessageDao
import com.emergency.mesh.db.MessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of MessageRepository2 using Room.
 * Added support for senderNumber field.
 */
class RoomMessageRepository(
    private val messageDao: MessageDao,
    private val senderNumber: String // ✅ Added parameter to fix compile errors
) : MessageRepository2 {

    override suspend fun saveMessage(message: Message) {
        val entity = MessageEntity(
            id = message.id,
            text = message.text,
            latitude = message.latitude,
            longitude = message.longitude,
            timestamp = message.timestamp,
            isSynced = message.isSynced,
            messageType = message.messageType,
            senderNumber = message.senderNumber // ✅ Store sender’s number in DB
        )
        messageDao.insert(entity)
    }

    override suspend fun getUnsyncedMessages(): List<Message> =
        messageDao.getUnsyncedMessages().map { it.toMessage() }

    override suspend fun markAsSynced(messageId: String) =
        messageDao.markAsSynced(messageId)

    override fun getAllMessages(): Flow<List<Message>> =
        messageDao.getAllMessages().map { list -> list.map { it.toMessage() } }

    // Extension function to convert Room entity to domain model
    private fun MessageEntity.toMessage(): Message =
        Message(
            id = id,
            text = text,
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp,
            isSynced = isSynced,
            messageType = messageType,
            senderNumber = senderNumber // ✅ Pass senderNumber to model
        )
}
