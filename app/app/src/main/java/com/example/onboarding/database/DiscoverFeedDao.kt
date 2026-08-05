package com.example.onboarding.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiscoverFeedDao {

    @Query("SELECT * FROM discover_feed WHERE expiresAt > :currentTime ORDER BY matchScore DESC")
    fun getValidFeedItemsFlow(currentTime: Long = System.currentTimeMillis()): Flow<List<DiscoverFeedEntity>>

    @Query("SELECT * FROM discover_feed WHERE expiresAt > :currentTime ORDER BY matchScore DESC")
    suspend fun getValidFeedItemsSync(currentTime: Long = System.currentTimeMillis()): List<DiscoverFeedEntity>

    @Query("SELECT * FROM discover_feed WHERE isSynced = 0")
    suspend fun getUnsyncedFeedItems(): List<DiscoverFeedEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<DiscoverFeedEntity>)

    @Query("UPDATE discover_feed SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markItemsSynced(ids: List<String>)

    @Query("DELETE FROM discover_feed WHERE expiresAt <= :currentTime")
    suspend fun deleteExpiredItems(currentTime: Long = System.currentTimeMillis()): Int

    @Query("DELETE FROM discover_feed")
    suspend fun clearAll()
}
