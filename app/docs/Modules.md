# Application Modules Specification

The codebase is organized into domain-specific, modular feature directories:

- `com.example.onboarding.ai` — Gemini AI matchmaker & collaborator synergy engine.
- `com.example.onboarding.api` — Gemini REST API and image generation client.
- `com.example.onboarding.audio` — Media3 / ExoPlayer wrapper for audio STEM previews and animated waveforms.
- `com.example.onboarding.database` — Local Room SQLite database & FirebaseManager (Auth, Firestore, Storage).
- `com.example.onboarding.messaging` — FCM push notification service & `AppNotificationManager`.
- `com.example.onboarding.models` — Domain data models, UI states, and sealed classes.
- `com.example.onboarding.ui.components` — Centralized `UnifiedComponents.kt` recycling design primitives.
- `com.example.onboarding.ui.screens` — Modular feature screens (Auth, Discover, Messaging, Profiles, Studios).
- `com.example.onboarding.util` — AppStrings, DemoDataProvider, ErrorReportingService, DynamicLinkManager.
- `com.example.onboarding.viewmodel` — ViewModel state containers and StateFlow flows.
