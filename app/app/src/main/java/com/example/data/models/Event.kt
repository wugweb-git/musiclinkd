package com.example.data.models

import com.google.firebase.Timestamp

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val venueId: String = "",
    val venueName: String = "",
    val organizerUid: String = "",
    val organizerName: String = "",
    val eventDate: Timestamp? = null,
    val genres: List<String> = emptyList(),
    val imageUrl: String = "",
    val ticketPrice: Double = 0.0,
    val performerUids: List<String> = emptyList(),
    val status: String = "UPCOMING",
    val projectId: String = "musiclinkd-7ae30",
    val createdAt: Timestamp? = null
)
