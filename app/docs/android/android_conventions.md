# Android Development Conventions — MusicLinkd

## Android Architecture Guidelines

1. **Jetpack Compose UI:** 100% declarative UI code written in Kotlin.
2. **Material Design 3 (M3):** System colors, dynamic light color scheme, typography tokens.
3. **Edge-to-Edge:** Native `enableEdgeToEdge()` execution with system bar inset handling.
4. **State Management:** `StateFlow` collected via `collectAsStateWithLifecycle()`.
5. **Touch Targets:** Minimum 48.dp x 48.dp for accessibility compliance.
