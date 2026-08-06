package com.example.data.models

import com.google.firebase.Timestamp

data class Venue(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val city: String = "",
    val capacity: Int = 0,
    val description: String = "",
    val imageUrl: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val amenities: List<String> = emptyList(),
    val hourlyRate: Double = 0.0,
    val rating: Double = 5.0,
    val isAvailable: Boolean = true,
    val projectId: String = "musiclinkd-7ae30",
    val createdAt: Timestamp? = null
)
