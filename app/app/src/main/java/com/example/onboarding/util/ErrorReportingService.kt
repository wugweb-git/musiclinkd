package com.example.onboarding.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.io.PrintWriter
import java.io.StringWriter

enum class ErrorSeverity {
    FATAL,
    NON_FATAL,
    WARNING,
    INFO
}

data class LoggedErrorEvent(
    val id: String = System.currentTimeMillis().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String,
    val message: String,
    val severity: ErrorSeverity,
    val throwableMessage: String?,
    val stackTraceSnippet: String?
)

/**
 * Centralized Error Reporting Service for MusicLinkd Application.
 * Logs exceptions and breadcrumbs directly to Firebase Crashlytics for project musiclinkd-7ae30,
 * while maintaining a structured in-memory diagnostic buffer and local logcat reports.
 */
object ErrorReportingService {

    private const val TAG = "MusicLinkd_ErrorReporting"
    private const val PRIMARY_PROJECT_ID = "musiclinkd-7ae30"
    
    private val _recentErrorEvents = mutableListOf<LoggedErrorEvent>()
    val recentErrorEvents: List<LoggedErrorEvent> get() = _recentErrorEvents.toList()

    private val _breadcrumbs = mutableListOf<String>()
    val breadcrumbs: List<String> get() = _breadcrumbs.toList()

    /**
     * Set global user context identifiers for Crashlytics tracking.
     */
    fun setUserContext(userId: String, email: String? = null, role: String? = null) {
        logBreadcrumb("User Context Set: ID=$userId, Email=$email, Role=$role")
        try {
            val isFirebaseReady = try { com.google.firebase.FirebaseApp.getInstance(); true } catch (e: Throwable) { false }
            if (isFirebaseReady) {
                val crashlytics = FirebaseCrashlytics.getInstance()
                crashlytics.setUserId(userId)
                crashlytics.setCustomKey("musiclinkd_project_id", PRIMARY_PROJECT_ID)
                email?.let { crashlytics.setCustomKey("user_email", it) }
                role?.let { crashlytics.setCustomKey("user_role", it) }
            }
        } catch (e: Throwable) {
            Log.d(TAG, "Crashlytics not available for setUserContext: ${e.message}")
        }
    }

    /**
     * Add a diagnostic breadcrumb event leading up to potential exceptions.
     */
    fun logBreadcrumb(message: String) {
        val entry = "[${System.currentTimeMillis()}] $message"
        synchronized(_breadcrumbs) {
            if (_breadcrumbs.size >= 50) _breadcrumbs.removeAt(0)
            _breadcrumbs.add(entry)
        }
        try {
            val isFirebaseReady = try { com.google.firebase.FirebaseApp.getInstance(); true } catch (e: Throwable) { false }
            if (isFirebaseReady) {
                FirebaseCrashlytics.getInstance().log(message)
            }
        } catch (e: Throwable) {
            Log.d(TAG, "Breadcrumb recorded locally: $message")
        }
    }

    /**
     * Primary exception reporting method.
     * Captures stacktrace, routes to Firebase Crashlytics, and records in local diagnostic memory.
     */
    fun logException(
        throwable: Throwable,
        tag: String = "AppException",
        message: String = throwable.localizedMessage ?: "An unexpected exception occurred",
        severity: ErrorSeverity = ErrorSeverity.NON_FATAL,
        customKeys: Map<String, Any> = emptyMap()
    ) {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        val fullStackTrace = sw.toString()
        val stackSnippet = fullStackTrace.lines().take(6).joinToString("\n")

        val event = LoggedErrorEvent(
            tag = tag,
            message = message,
            severity = severity,
            throwableMessage = throwable.message,
            stackTraceSnippet = stackSnippet
        )

        synchronized(_recentErrorEvents) {
            if (_recentErrorEvents.size >= 30) _recentErrorEvents.removeAt(0)
            _recentErrorEvents.add(event)
        }

        Log.e(TAG, "[$severity] [$tag] $message\n$stackSnippet")

        try {
            val isFirebaseReady = try { com.google.firebase.FirebaseApp.getInstance(); true } catch (e: Throwable) { false }
            if (isFirebaseReady) {
                val crashlytics = FirebaseCrashlytics.getInstance()
                crashlytics.setCustomKey("firebase_project_id", PRIMARY_PROJECT_ID)
                customKeys.forEach { (k, v) ->
                    crashlytics.setCustomKey(k, v.toString())
                }
                crashlytics.setCustomKey("error_severity", severity.name)
                crashlytics.setCustomKey("error_tag", tag)
                crashlytics.recordException(throwable)
            }
        } catch (e: Throwable) {
            Log.d(TAG, "Firebase Crashlytics not active during logException: ${e.message}")
        }
    }

    /**
     * Report a non-fatal warning or message without a throwable object.
     */
    fun logWarning(tag: String, message: String, customKeys: Map<String, Any> = emptyMap()) {
        logException(
            throwable = Exception(message),
            tag = tag,
            message = message,
            severity = ErrorSeverity.WARNING,
            customKeys = customKeys
        )
    }

    /**
     * Clear recorded diagnostics.
     */
    fun clearLogs() {
        synchronized(_recentErrorEvents) { _recentErrorEvents.clear() }
        synchronized(_breadcrumbs) { _breadcrumbs.clear() }
    }
}
