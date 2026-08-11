package com.example.onboarding

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.os.Bundle
import android.util.Log
import com.example.onboarding.database.RepositoryProvider
import com.example.onboarding.util.AnalyticsHelper
import com.example.onboarding.util.ErrorReportingService
import com.google.firebase.FirebaseApp
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy

class MusicLinkdApplication : Application(), ImageLoaderFactory, Application.ActivityLifecycleCallbacks {

    private var activeActivityCount = 0
    private var isAppInForeground = false

    override fun onCreate() {
        super.onCreate()

        // Ensure Firebase core is initialized with musiclinkd-7ae30 primary configuration
        try {
            val defaultApiKey = "AIzaSyMusicLinkd7ae30DefaultApiKey"
            val defaultAppId = "1:846160129456:android:b204e38af5c6891a"

            val apiKey = try { com.example.BuildConfig.FIREBASE_API_KEY.ifEmpty { defaultApiKey } } catch (e: Throwable) { defaultApiKey }
            val appId = try { com.example.BuildConfig.FIREBASE_APP_ID.ifEmpty { defaultAppId } } catch (e: Throwable) { defaultAppId }

            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setProjectId(AnalyticsHelper.PRIMARY_PROJECT_ID)
                    .setApplicationId(appId)
                    .setApiKey(apiKey)
                    .setGcmSenderId("846160129456")
                    .setStorageBucket("musiclinkd-7ae30.firebasestorage.app")
                    .build()

                try {
                    val app = FirebaseApp.initializeApp(this)
                    if (app == null && FirebaseApp.getApps(this).isEmpty()) {
                        FirebaseApp.initializeApp(this, options)
                    }
                } catch (e: Throwable) {
                    if (FirebaseApp.getApps(this).isEmpty()) {
                        FirebaseApp.initializeApp(this, options)
                    }
                }
            }
            Log.i("MusicLinkdApplication", "FirebaseApp initialized for project: ${AnalyticsHelper.PRIMARY_PROJECT_ID}")
        } catch (e: Throwable) {
            Log.w("MusicLinkdApplication", "FirebaseApp setup completed or handled: ${e.message}")
        }

        AnalyticsHelper.initialize(this)
        ErrorReportingService.logBreadcrumb("MusicLinkdApplication initialized for primary project ${AnalyticsHelper.PRIMARY_PROJECT_ID}")

        registerActivityLifecycleCallbacks(this)
        RepositoryProvider.initialize(this)
    }

    // Activity Lifecycle Monitoring & Application State Tracking
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ErrorReportingService.logBreadcrumb("Activity Created: ${activity.javaClass.simpleName}")
    }

    override fun onActivityStarted(activity: Activity) {
        activeActivityCount++
        if (activeActivityCount == 1 && !isAppInForeground) {
            isAppInForeground = true
            Log.d("MusicLinkdApplication", "App entered FOREGROUND [Project: ${AnalyticsHelper.PRIMARY_PROJECT_ID}]")
            AnalyticsHelper.trackUserInteraction("app_foreground", mapOf("project_id" to AnalyticsHelper.PRIMARY_PROJECT_ID))
        }
    }

    override fun onActivityResumed(activity: Activity) {
        AnalyticsHelper.trackScreenView(
            screenName = activity.javaClass.simpleName,
            screenClass = activity.javaClass.name
        )
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        activeActivityCount--
        if (activeActivityCount <= 0) {
            activeActivityCount = 0
            isAppInForeground = false
            Log.d("MusicLinkdApplication", "App entered BACKGROUND [Project: ${AnalyticsHelper.PRIMARY_PROJECT_ID}]")
            AnalyticsHelper.trackUserInteraction("app_background", mapOf("project_id" to AnalyticsHelper.PRIMARY_PROJECT_ID))
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        ErrorReportingService.logBreadcrumb("Activity Destroyed: ${activity.javaClass.simpleName}")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            Log.w("MusicLinkdApplication", "High memory pressure detected (TrimLevel: $level), trimming media cache")
            ErrorReportingService.logBreadcrumb("TrimMemory triggered: level=$level")
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("musiclinkd_media_disk_cache"))
                    .maxSizeBytes(150 * 1024 * 1024) // 150 MB disk cache capacity for cover art & photos
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .build()
    }
}
