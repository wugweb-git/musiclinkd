package com.example.onboarding.database

import com.example.onboarding.ui.screens.ChatContext
import com.example.onboarding.ui.screens.ConvoItem
import com.example.onboarding.ui.screens.MessageBubbleItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(private val chatDao: ChatDao) {

    val allConversations: Flow<List<ConvoItem>> = chatDao.getAllConversations().map { entities ->
        entities.map { entity ->
            ConvoItem(
                id = entity.id,
                name = entity.name,
                role = entity.role,
                lastMessage = entity.lastMessage,
                timestamp = entity.timestamp,
                unreadCount = entity.unreadCount,
                isVerified = entity.isVerified,
                context = try { ChatContext.valueOf(entity.context) } catch (e: Exception) { ChatContext.STUDIO },
                contextStatus = entity.contextStatus,
                isPinned = entity.isPinned,
                avatarUrl = entity.avatarUrl
            )
        }
    }

    fun getMessages(convoId: String): Flow<List<MessageBubbleItem>> {
        return chatDao.getMessagesForConversation(convoId).map { entities ->
            entities.map { entity ->
                MessageBubbleItem(
                    id = entity.id,
                    sender = entity.sender,
                    text = entity.text,
                    time = entity.time,
                    isMe = entity.isMe,
                    type = entity.type,
                    metaTitle = entity.metaTitle,
                    metaDesc = entity.metaDesc,
                    metaPrice = entity.metaPrice,
                    metaStatus = entity.metaStatus
                )
            }
        }
    }

    suspend fun insertConversations(items: List<ConvoItem>) {
        chatDao.insertConversations(items.map { item ->
            ConvoEntity(
                id = item.id,
                name = item.name,
                role = item.role,
                lastMessage = item.lastMessage,
                timestamp = item.timestamp,
                unreadCount = item.unreadCount,
                isVerified = item.isVerified,
                context = item.context.name,
                contextStatus = item.contextStatus,
                isPinned = item.isPinned,
                avatarUrl = item.avatarUrl
            )
        })
    }

    suspend fun insertConversation(item: ConvoItem) {
        chatDao.insertConversation(
            ConvoEntity(
                id = item.id,
                name = item.name,
                role = item.role,
                lastMessage = item.lastMessage,
                timestamp = item.timestamp,
                unreadCount = item.unreadCount,
                isVerified = item.isVerified,
                context = item.context.name,
                contextStatus = item.contextStatus,
                isPinned = item.isPinned,
                avatarUrl = item.avatarUrl
            )
        )
    }

    suspend fun insertMessage(convoId: String, item: MessageBubbleItem) {
        chatDao.insertMessage(
            MessageEntity(
                id = item.id,
                convoId = convoId,
                sender = item.sender,
                text = item.text,
                time = item.time,
                isMe = item.isMe,
                type = item.type,
                metaTitle = item.metaTitle,
                metaDesc = item.metaDesc,
                metaPrice = item.metaPrice,
                metaStatus = item.metaStatus
            )
        )
    }

    suspend fun deleteConversation(convoId: String) {
        chatDao.deleteConversation(convoId)
        chatDao.deleteMessagesForConversation(convoId)
    }

    suspend fun clearAll() {
        chatDao.deleteAllConversations()
        chatDao.deleteAllMessages()
    }
}
