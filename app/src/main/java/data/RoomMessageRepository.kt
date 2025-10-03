package com.emergency.mesh.data

import com.emergency.mesh.db.MessageDao
import com.emergency.mesh.db.MessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Implementation of MessageRepository that uses Room for local storage.
class RoomMessageRepository(private val messageDao: MessageDao) : MessageRepository {

    override suspend fun saveMessage(message: Message) {
        val entity = MessageEntity(
            id = message.id,
            text = message.text,
            latitude = message.latitude,
            longitude = message.longitude,
            timestamp = message.timestamp,
            isSynced = message.isSynced,
            messageType = message.messageType
        )
        messageDao.insert(entity)
    }

    override suspend fun getUnsyncedMessages(): List<Message> {
        return messageDao.getUnsyncedMessages().map { it.toMessage() }
    }

    override suspend fun markAsSynced(messageId: String) {
        messageDao.markAsSynced(messageId)
    }

    override fun getAllMessages(): Flow<List<Message>> {
        return messageDao.getAllMessages().map { entities ->
            entities.map { it.toMessage() }
        }
    }

    private fun MessageEntity.toMessage(): Message {
        return Message(
            id = this.id,
            text = this.text,
            latitude = this.latitude,
            longitude = this.longitude,
            timestamp = this.timestamp,
            isSynced = this.isSynced,
            messageType = this.messageType
        )
    }
}
