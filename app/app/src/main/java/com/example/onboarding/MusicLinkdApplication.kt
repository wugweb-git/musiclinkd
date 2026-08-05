package com.example.onboarding

import android.app.Application
import com.example.onboarding.database.RepositoryProvider
import com.example.onboarding.util.ErrorReportingService
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy

class MusicLinkdApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        ErrorReportingService.logBreadcrumb("MusicLinkdApplication initialized")
        RepositoryProvider.initialize(this)
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
