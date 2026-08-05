package com.example.navigation

/**
 * Sealed class hierarchy representing type-safe screen routes for Jetpack Navigation Compose.
 */
sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Welcome : Routes("welcome")
    object EmailAuth : Routes("email_auth")
    object MobileAuth : Routes("mobile_auth")
    object OtpVerify : Routes("otp_verify")
    object RolePrimary : Routes("role_primary")
    object RoleSecondary : Routes("role_secondary")
    object Languages : Routes("languages")
    object Genres : Routes("genres")
    object Instruments : Routes("instruments")
    object Location : Routes("location")
    object ProfileCreation : Routes("profile_creation")
    object Success : Routes("success")
    object Dashboard : Routes("dashboard")

    object ArtistDetail : Routes("artist/{artistId}") {
        fun createRoute(artistId: String) = "artist/$artistId"
    }

    object StudioDetail : Routes("studio/{studioId}") {
        fun createRoute(studioId: String) = "studio/$studioId"
    }

    object ChatThread : Routes("messages/{threadId}") {
        fun createRoute(threadId: String) = "messages/$threadId"
    }
}
