package com.example.onboarding.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE expiresAt > :currentTime ORDER BY createdAt DESC")
    fun getAllNotificationsFlow(currentTime: Long = System.currentTimeMillis()): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE isSynced = 0")
    suspend fun getUnsyncedNotifications(): List<NotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1, isSynced = 0 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markNotificationsSynced(ids: List<String>)

    @Query("DELETE FROM notifications WHERE expiresAt <= :currentTime")
    suspend fun deleteExpiredNotifications(currentTime: Long = System.currentTimeMillis()): Int

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}
