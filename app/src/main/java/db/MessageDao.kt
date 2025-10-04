package com.emergency.mesh.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Data Access Object (DAO) for the messages table.
@Dao
interface MessageDao {

    // Inserts a message into the table. If the message already exists, it's replaced.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    // Retrieves all messages from the table, ordered by timestamp descending.
    // Returns a Flow, so the UI can observe changes automatically.
    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    // Fetches all messages that have not yet been synced.
    @Query("SELECT * FROM messages WHERE isSynced = 0")
    suspend fun getUnsyncedMessages(): List<MessageEntity>

    // Marks a specific message as synced.
    @Query("UPDATE messages SET isSynced = 1 WHERE id = :messageId")
    suspend fun markAsSynced(messageId: String)
}