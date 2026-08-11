package com.example.navigation

import androidx.navigation.NavController

/**
 * AppNavigation centralizes all navigation routes, parameter types, and type-safe builders 
 * for the MusicLinkd ecosystem (including profiles, marketplace, and messaging).
 */
object AppNavigation {

    // Primary route constants
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val EMAIL_AUTH = "email_auth"
    const val MOBILE_AUTH = "mobile_auth"
    const val OTP_VERIFY = "otp_verify"
    const val ROLE_PRIMARY = "role_primary"
    const val ROLE_SECONDARY = "role_secondary"
    const val LANGUAGES = "languages"
    const val GENRES = "genres"
    const val INSTRUMENTS = "instruments"
    const val LOCATION = "location"
    const val PROFILE_CREATION = "profile_creation"
    const val SUCCESS = "success"
    const val DASHBOARD = "dashboard"

    // Parametrized Route Patterns
    const val ARTIST_DETAIL_PATTERN = "artist/{artistId}"
    const val STUDIO_DETAIL_PATTERN = "studio/{studioId}"
    const val CHAT_THREAD_PATTERN = "messages/{threadId}"

    // Route Argument Keys
    const val ARG_ARTIST_ID = "artistId"
    const val ARG_STUDIO_ID = "studioId"
    const val ARG_THREAD_ID = "threadId"

    /**
     * Helper functions to build type-safe destination routes with arguments.
     */
    fun artistDetailRoute(artistId: String): String = "artist/$artistId"
    fun studioDetailRoute(studioId: String): String = "studio/$studioId"
    fun chatThreadRoute(threadId: String): String = "messages/$threadId"

    /**
     * Core type-safe navigation functions for direct invocation.
     */
    fun navigateToArtistProfile(navController: NavController, artistId: String) {
        navController.navigate(artistDetailRoute(artistId))
    }

    fun navigateToStudioDetail(navController: NavController, studioId: String) {
        navController.navigate(studioDetailRoute(studioId))
    }

    fun navigateToChatThread(navController: NavController, threadId: String) {
        navController.navigate(chatThreadRoute(threadId))
    }

    fun navigateToDashboard(navController: NavController) {
        navController.navigate(DASHBOARD) {
            popUpTo(SPLASH) { inclusive = true }
        }
    }
}
