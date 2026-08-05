package com.example.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Centralized Icon Library mapping labeled domain icons to Material Icons.
 */
object IconLibrary {

    data class IconItem(
        val key: String,
        val label: String,
        val category: String,
        val icon: ImageVector
    )

    val Navigation = listOf(
        IconItem("back", "Back Arrow", "Navigation", Icons.AutoMirrored.Filled.ArrowBack),
        IconItem("search", "Search", "Navigation", Icons.Default.Search),
        IconItem("close", "Close", "Navigation", Icons.Default.Close),
        IconItem("more", "More Options", "Navigation", Icons.Default.MoreVert),
        IconItem("check", "Checkmark", "Navigation", Icons.Default.Check)
    )

    val MainTabs = listOf(
        IconItem("discover", "Discover Feed", "MainTabs", Icons.Default.Home),
        IconItem("marketplace", "Marketplace & Gear", "MainTabs", Icons.Default.ShoppingBag),
        IconItem("connections", "Connections & Network", "MainTabs", Icons.Default.People),
        IconItem("messages", "Direct Messages", "MainTabs", Icons.Default.Email),
        IconItem("profile", "Artist Profile", "MainTabs", Icons.Default.Person)
    )

    val AudioAndMedia = listOf(
        IconItem("play", "Play Audio", "Audio", Icons.Default.PlayArrow),
        IconItem("pause", "Pause Audio", "Audio", Icons.Default.Pause),
        IconItem("music", "Music Track", "Audio", Icons.Default.MusicNote),
        IconItem("mic", "Microphone Vocal", "Audio", Icons.Default.Mic),
        IconItem("headset", "Headphones / Monitor", "Audio", Icons.Default.Headset),
        IconItem("send_audio", "Send Track", "Audio", Icons.AutoMirrored.Filled.Send)
    )

    val BusinessAndBooking = listOf(
        IconItem("studio", "Recording Studio", "Business", Icons.Default.Storefront),
        IconItem("calendar", "Booking Calendar", "Business", Icons.Default.DateRange),
        IconItem("star", "Reputation & Rating", "Business", Icons.Default.Star),
        IconItem("verified", "Verified Badge", "Business", Icons.Default.Verified)
    )

    val AllIcons: List<IconItem> = Navigation + MainTabs + AudioAndMedia + BusinessAndBooking

    fun getIcon(key: String): ImageVector {
        return AllIcons.find { it.key == key }?.icon ?: Icons.Default.MusicNote
    }
}
