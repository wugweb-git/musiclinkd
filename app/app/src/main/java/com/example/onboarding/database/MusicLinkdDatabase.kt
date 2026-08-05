package com.example.onboarding.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ReviewEntity::class,
        ConvoEntity::class,
        MessageEntity::class,
        OnboardingEntity::class,
        UserProfileEntity::class,
        NotificationEntity::class,
        DiscoverFeedEntity::class,
        SearchHistoryEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class MusicLinkdDatabase : RoomDatabase() {
    abstract fun reviewDao(): ReviewDao
    abstract fun chatDao(): ChatDao
    abstract fun onboardingDao(): OnboardingDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun notificationDao(): NotificationDao
    abstract fun discoverFeedDao(): DiscoverFeedDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: MusicLinkdDatabase? = null

        fun getDatabase(context: Context): MusicLinkdDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicLinkdDatabase::class.java,
                    "musiclinkd_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
