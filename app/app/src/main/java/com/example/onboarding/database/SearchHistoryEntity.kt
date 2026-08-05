package com.example.onboarding.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val query: String,
    val category: String = "Artist",
    val resultCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
