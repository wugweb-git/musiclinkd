package com.example.onboarding.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val timestamp: String,
    val category: String,
    val isRead: Boolean,
    val sourceName: String,
    val iconEmoji: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = true,
    val expiresAt: Long = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000) // 7-day expiration policy TTL
)
