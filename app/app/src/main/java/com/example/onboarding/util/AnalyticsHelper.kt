package com.example.onboarding.util

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Helper object to initialize Firebase Analytics and track screen transitions and user interactions
 * across the MusicLinkd app, routing telemetry to the single primary 'musiclinkd-7ae30' project.
 */
object AnalyticsHelper {

    private const val TAG = "MusicLinkd_Analytics"
    const val PRIMARY_PROJECT_ID = "musiclinkd-7ae30"

    private var firebaseAnalytics: FirebaseAnalytics? = null

    /**
     * Initialize Firebase Analytics with the application context.
     */
    fun initialize(context: Context) {
        try {
            val isFirebaseReady = try { com.google.firebase.FirebaseApp.getInstance() != null } catch (e: Throwable) { false }
            if (isFirebaseReady) {
                try {
                    val analytics = FirebaseAnalytics.getInstance(context.applicationContext)
                    firebaseAnalytics = analytics
                    analytics.setAnalyticsCollectionEnabled(false)
                    analytics.setUserProperty("musiclinkd_project_id", PRIMARY_PROJECT_ID)
                    analytics.setUserProperty("app_target", "app.musiclinkd.com")
                } catch (e: Throwable) {
                    Log.w(TAG, "Firebase Analytics init skipped: ${e.message}")
                }

                try {
                    val crashlytics = FirebaseCrashlytics.getInstance()
                    crashlytics.setCustomKey("musiclinkd_project_id", PRIMARY_PROJECT_ID)
                    crashlytics.setCustomKey("app_target", "app.musiclinkd.com")
                    crashlytics.setCrashlyticsCollectionEnabled(false)
                } catch (e: Throwable) {
                    Log.w(TAG, "Firebase Crashlytics init skipped: ${e.message}")
                }

                Log.i(TAG, "Firebase Analytics & Crashlytics initialized for project: $PRIMARY_PROJECT_ID")
            } else {
                Log.w(TAG, "FirebaseApp is not active; telemetry recorded to local diagnostic buffers.")
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to initialize Firebase Analytics/Crashlytics: ${e.message}")
        }
    }

    /**
     * Track screen transitions across the navigation graph.
     */
    fun trackScreenView(screenName: String, screenClass: String = screenName) {
        Log.d(TAG, "Track Screen View: $screenName ($screenClass)")
        ErrorReportingService.logBreadcrumb("Navigated to screen: $screenName")

        try {
            firebaseAnalytics?.let { analytics ->
                val bundle = Bundle().apply {
                    putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                    putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
                    putString("project_id", PRIMARY_PROJECT_ID)
                }
                analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error tracking screen view: ${e.message}")
        }
    }

    /**
     * Track user interactions, feature usage, and button clicks.
     */
    fun trackUserInteraction(eventName: String, params: Map<String, Any> = emptyMap()) {
        Log.d(TAG, "Track Event: $eventName -> $params")
        try {
            firebaseAnalytics?.let { analytics ->
                val bundle = Bundle().apply {
                    putString("project_id", PRIMARY_PROJECT_ID)
                    params.forEach { (key, value) ->
                        when (value) {
                            is String -> putString(key, value)
                            is Int -> putInt(key, value)
                            is Long -> putLong(key, value)
                            is Double -> putDouble(key, value)
                            is Boolean -> putBoolean(key, value)
                            else -> putString(key, value.toString())
                        }
                    }
                }
                analytics.logEvent(eventName, bundle)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error tracking user interaction: ${e.message}")
        }
    }

    /**
     * Identify the active user for analytics and crash logging.
     */
    fun setUserContext(userId: String, email: String? = null, role: String? = null) {
        try {
            firebaseAnalytics?.setUserId(userId)
            role?.let { firebaseAnalytics?.setUserProperty("user_role", it) }

            ErrorReportingService.setUserContext(userId, email, role)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting user context in Analytics: ${e.message}")
        }
    }

    // --- TYPED CUJ EVENT TRACKERS ---

    fun trackAppOpen() = trackUserInteraction("app_open")
    fun trackLogin(method: String) = trackUserInteraction("login", mapOf("method" to method))
    fun trackSignup(method: String, role: String) = trackUserInteraction("signup", mapOf("method" to method, "role" to role))
    fun trackSearch(query: String, category: String) = trackUserInteraction("search", mapOf("query" to query, "category" to category))
    fun trackProfileView(targetUserId: String, role: String) = trackUserInteraction("profile_view", mapOf("target_user_id" to targetUserId, "role" to role))
    fun trackTrackPlay(trackId: String, title: String) = trackUserInteraction("track_play", mapOf("track_id" to trackId, "title" to title))
    fun trackTrackUpload(trackId: String, format: String) = trackUserInteraction("track_upload", mapOf("track_id" to trackId, "format" to format))
    fun trackConnectionRequest(recipientId: String) = trackUserInteraction("connection_request", mapOf("recipient_id" to recipientId))
    fun trackMessageSent(threadId: String, hasAudio: Boolean) = trackUserInteraction("message_sent", mapOf("thread_id" to threadId, "has_audio" to hasAudio))
    fun trackBookingCreated(bookingId: String, studioId: String) = trackUserInteraction("booking_created", mapOf("booking_id" to bookingId, "studio_id" to studioId))
    fun trackBookingCompleted(bookingId: String, amount: Double) = trackUserInteraction("booking_completed", mapOf("booking_id" to bookingId, "amount" to amount))
    fun trackMarketplaceListing(listingId: String, category: String) = trackUserInteraction("marketplace_listing", mapOf("listing_id" to listingId, "category" to category))
    fun trackPurchase(itemId: String, amount: Double) = trackUserInteraction("purchase", mapOf("item_id" to itemId, "amount" to amount))
    fun trackAiRecommendation(query: String, resultCount: Int) = trackUserInteraction("ai_recommendation", mapOf("query" to query, "result_count" to resultCount))
    fun trackStudioBooking(studioId: String, durationHours: Int) = trackUserInteraction("studio_booking", mapOf("studio_id" to studioId, "duration_hours" to durationHours))
    fun trackNotificationOpen(notificationId: String, channel: String) = trackUserInteraction("notification_open", mapOf("notification_id" to notificationId, "channel" to channel))
    fun trackErrorEvent(errorType: String, message: String) = trackUserInteraction("error_event", mapOf("error_type" to errorType, "message" to message))

    /**
     * Explicitly record a non-fatal crash or exception in Crashlytics.
     */
    fun recordCrashlyticsException(throwable: Throwable, tag: String = "AppException", message: String? = null) {
        ErrorReportingService.logException(
            throwable = throwable,
            tag = tag,
            message = message ?: throwable.localizedMessage ?: "Unexpected Exception",
            customKeys = mapOf("project_id" to PRIMARY_PROJECT_ID)
        )
    }
}
