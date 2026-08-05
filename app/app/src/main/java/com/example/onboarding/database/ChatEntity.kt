package com.example.onboarding.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConvoEntity(
    @PrimaryKey val id: String,
    val name: String,
    val role: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val isVerified: Boolean,
    val context: String, // Mapped from ChatContext enum name
    val contextStatus: String,
    val isPinned: Boolean,
    val avatarUrl: String
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val convoId: String,
    val sender: String,
    val text: String,
    val time: String,
    val isMe: Boolean,
    val type: String,
    val metaTitle: String,
    val metaDesc: String,
    val metaPrice: String,
    val metaStatus: String,
    val timestampMs: Long = System.currentTimeMillis()
)
