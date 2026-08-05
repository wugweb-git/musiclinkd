package com.example.onboarding.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String = "current_user",
    val displayName: String = "Vedanshu Srivastava",
    val professionalName: String = "Vedanshu",
    val headline: String = "Music Producer & Composer",
    val bio: String = "Passionate sound architect based in Mumbai. Specializing in cinematic fusion, synth layering, and spatial audio production.",
    val location: String = "Mumbai, Maharashtra",
    val languages: String = "English, Hindi, Punjabi",
    val genres: String = "Indie, Cinematic, Synthwave, Electronic",
    val skills: String = "Music Producer, Sound Engineer, Synthesizer",
    val website: String = "https://vedanshumaestro.music",
    val instagram: String = "@vedanshu_music",
    val spotify: String = "spotify:artist:vedanshu",
    val youtube: String = "https://youtube.com/@vedanshu_music",
    val profilePicturePath: String? = null,
    val hourlyRate: String = "₹8,000 / session",
    val lastUpdated: Long = System.currentTimeMillis()
)
