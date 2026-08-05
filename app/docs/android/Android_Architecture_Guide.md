# Android Architecture & Jetpack Compose Best Practices — MusicLinkd

## Architecture Principles

1. **Jetpack Compose UI:** 100% declarative UI built with Material Design 3 (M3).
2. **State Management:** `ViewModel` exposing read-only `StateFlow<UiState>` to Composables.
3. **State Hoisting & Preservation:**
   - Use `remember { mutableStateOf(...) }` for volatile, ephemeral UI state (e.g., active ripple, expanded card state).
   - Use `rememberSaveable { mutableStateOf(...) }` for UI state that must survive configuration and screen orientation changes (e.g., search text input, filter tab index).
4. **Lifecycle-Aware Collection:**
   - Always collect state in Composables using `collectAsStateWithLifecycle()` from `androidx.lifecycle.compose`.
5. **Coroutines & Asynchronous Processing:**
   - Execute asynchronous operations inside `viewModelScope.launch { ... }` with proper exception handling.
6. **State Navigation Rationale:**
   - Single Activity (`MainActivity`) utilizing sealed `AppTabScreen` states to manage tab transitions cleanly without route-serialization crashes.
