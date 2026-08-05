package com.example.onboarding.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discover_feed")
data class DiscoverFeedEntity(
    @PrimaryKey val id: String,
    val title: String,
    val creatorName: String,
    val creatorRole: String,
    val coverImageUrl: String,
    val genre: String,
    val matchScore: Int,
    val seekingRoles: String, // Comma separated list of open roles
    val description: String,
    val audioSnippetTitle: String,
    val listensCount: String,
    val isSynced: Boolean = true, // Partial sync status indicator
    val fetchedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // Default 24-hour TTL expiration policy
)
