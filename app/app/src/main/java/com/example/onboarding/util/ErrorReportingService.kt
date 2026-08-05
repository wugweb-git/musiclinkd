package com.example.onboarding.util

import android.util.Log
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
 * Logs exceptions to Firebase Crashlytics when available at runtime,
 * while maintaining a structured in-memory diagnostic buffer and local logcat reports.
 */
object ErrorReportingService {

    private const val TAG = "MusicLinkd_ErrorReporting"
    
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
            val crashlyticsClass = Class.forName("com.google.firebase.crashlytics.FirebaseCrashlytics")
            val getInstanceMethod = crashlyticsClass.getMethod("getInstance")
            val instance = getInstanceMethod.invoke(null)
            
            val setUserIdMethod = crashlyticsClass.getMethod("setUserId", String::class.java)
            setUserIdMethod.invoke(instance, userId)
            
            val setCustomKeyMethod = crashlyticsClass.getMethod("setCustomKey", String::class.java, String::class.java)
            email?.let { setCustomKeyMethod.invoke(instance, "user_email", it) }
            role?.let { setCustomKeyMethod.invoke(instance, "user_role", it) }
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
            val crashlyticsClass = Class.forName("com.google.firebase.crashlytics.FirebaseCrashlytics")
            val getInstanceMethod = crashlyticsClass.getMethod("getInstance")
            val instance = getInstanceMethod.invoke(null)
            val logMethod = crashlyticsClass.getMethod("log", String::class.java)
            logMethod.invoke(instance, message)
        } catch (e: Throwable) {
            Log.d(TAG, "Breadcrumb recorded locally: $message")
        }
    }

    /**
     * Primary exception reporting method.
     * Captures stacktrace, routes to Firebase Crashlytics if available, and records in local diagnostic memory.
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
            val crashlyticsClass = Class.forName("com.google.firebase.crashlytics.FirebaseCrashlytics")
            val getInstanceMethod = crashlyticsClass.getMethod("getInstance")
            val instance = getInstanceMethod.invoke(null)
            
            val setCustomKeyMethod = crashlyticsClass.getMethod("setCustomKey", String::class.java, String::class.java)
            customKeys.forEach { (k, v) ->
                setCustomKeyMethod.invoke(instance, k, v.toString())
            }
            setCustomKeyMethod.invoke(instance, "error_severity", severity.name)
            setCustomKeyMethod.invoke(instance, "error_tag", tag)
            
            val recordExceptionMethod = crashlyticsClass.getMethod("recordException", Throwable::class.java)
            recordExceptionMethod.invoke(instance, throwable)
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
