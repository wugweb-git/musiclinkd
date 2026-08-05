# Architecture & System Design Document

## Architecture Overview
MusicLinkd adheres to Modern Android MVVM (Model-View-ViewModel) and Clean Architecture principles with Jetpack Compose for declarative UI rendering.

```
[ UI Layer (Compose) ] <---> [ ViewModel / StateFlow ] <---> [ Repository Layer ] <---> [ Data Sources (Room / Firebase / Gemini API) ]
```

## Key Technical Specifications
- **Language:** Kotlin 2.0+
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **State Management:** `StateFlow` and `collectAsStateWithLifecycle`
- **Asynchronous Operations:** Kotlin Coroutines & Flow
- **Image Loading:** Coil `AsyncImage` with automatic placeholder & fallback handling
- **Audio Playback:** Android Media3 / ExoPlayer wrapper
- **Local Database:** Room Database with KSP symbol processing
- **Cloud Backend:** Firebase Auth, Firestore, Firebase Storage, FCM
- **AI Intelligence:** Gemini 1.5 Flash REST API
