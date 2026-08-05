package com.example.onboarding.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM conversations ORDER BY isPinned DESC, id DESC")
    fun getAllConversations(): Flow<List<ConvoEntity>>

    @Query("SELECT * FROM conversations")
    suspend fun getAllConversationsSync(): List<ConvoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(convos: List<ConvoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(convo: ConvoEntity)

    @Query("SELECT * FROM messages WHERE convoId = :convoId ORDER BY timestampMs ASC")
    fun getMessagesForConversation(convoId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM conversations WHERE id = :convoId")
    suspend fun deleteConversation(convoId: String)

    @Query("DELETE FROM messages WHERE convoId = :convoId")
    suspend fun deleteMessagesForConversation(convoId: String)

    @Query("DELETE FROM conversations")
    suspend fun deleteAllConversations()

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}
