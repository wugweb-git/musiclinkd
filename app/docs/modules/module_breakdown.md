# Module Breakdown — MusicLinkd

## Domain Package Structure (`com.example.onboarding`)

- `ai/`: Gemini 1.5 Flash synergy matchmaker & recommendation algorithms.
- `api/`: Gemini REST client and image generator utilities.
- `audio/`: Media3 / ExoPlayer wrapper for waveform audio preview playback.
- `database/`: Room Local SQLite DB & `FirebaseManager` (Auth, Firestore, Storage).
- `messaging/`: `AppNotificationManager` and `AppFirebaseMessagingService`.
- `models/`: Immutable domain models, sealed UI states (`Resource<T>`), and request DTOs.
- `ui/components/`: Unified design primitives (`UnifiedComponents.kt`).
- `ui/screens/`: Domain feature composable screens.
- `util/`: `DemoDataProvider`, `AppStrings`, `ErrorReportingService`, `DynamicLinkManager`.
- `viewmodel/`: ViewModel state managers using Kotlin `StateFlow`.
