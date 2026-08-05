# API Contracts & Service Specifications — MusicLinkd

## Service Interfaces

### 1. Gemini AI Synergy Service Contract
```kotlin
interface GeminiMatchmakerService {
    suspend fun calculateSynergyScore(
        artistA: UserProfile,
        artistB: UserProfile
    ): SynergyResult
}
```

### 2. Firebase Storage Media Service Contract
```kotlin
interface MediaStorageService {
    suspend fun uploadMedia(
        fileName: String,
        bytes: ByteArray,
        mimeType: String
    ): String
}
```
