package com.example.onboarding.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val reviewerName: String,
    val reviewerRole: String,
    val isVerified: Boolean,
    val ratingStars: Int,
    val reviewText: String,
    val projectContext: String,
    val date: String,
    val helpfulCount: Int,
    val hasAudioSample: Boolean,
    val audioTitle: String,
    val hasImages: Boolean,
    val businessReplyText: String
)
