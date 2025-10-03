package com.emergency.mesh.data

class MessageRepository(private val dao: MessageDao) {

    suspend fun saveMessage(message: MessageEntity) {
        dao.insertMessage(message)
    }

    fun getAllMessages() = dao.getAllMessages()
}
