package com.example.data.models

import com.google.firebase.Timestamp

data class Artist(
    val id: String = "",
    val name: String = "",
    val primaryRole: String = "",
    val secondaryRole: String = "",
    val genres: List<String> = emptyList(),
    val instruments: List<String> = emptyList(),
    val bio: String = "",
    val location: String = "",
    val avatarUrl: String = "",
    val portfolioTrackUrls: List<String> = emptyList(),
    val rating: Double = 5.0,
    val reviewCount: Int = 0,
    val isVerified: Boolean = false,
    val projectId: String = "musiclinkd-7ae30",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)
