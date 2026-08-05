# MUSICLINKD ARCHITECTURE & CODING CONVENTIONS

This document specifies the system-level navigation, responsive layout systems, spacing scales, micro-interactions, and JVM testing patterns.

---

## 📱 1. JETPACK COMPOSE LAYOUT & SYSTEM DESIGN

### Edge-to-Edge Execution
-   Always call `enableEdgeToEdge()` inside the main `Activity`.
-   Handle `WindowInsets` correctly using modern Scaffold integrations:
    `Scaffold(contentWindowInsets = WindowInsets.systemBars) { ... }`
-   Apply `Modifier.windowInsetsPadding` or status bar padding helpers to prevent UI items from slipping beneath system bars.

### Spacing System (8.dp Grid)
Containers are positioned using clean spacing classes to maintain consistent density:
-   `4.dp` — Status badges, avatar padding.
-   `8.dp` — Spacing inside small cards and related fields.
-   `16.dp` — Standard padding on page boundaries, lists, and form screens.
-   `24.dp` — Block-to-block margin spacing.
-   `32.dp` — Editorial, airy vertical space.

### Responsive Window Size Classes (Compact, Medium, Expanded)
We design mobile-first but adapt dynamically to larger screen formats (foldables, tablets, ChromeOS):
-   **Fluid Widths & Centered Containers**: Avoid hardcoding static horizontal sizes (e.g., `width(360.dp)`). Always wrap top-level screens in `BoxWithConstraints` and apply `.widthIn(max = 840.dp)` with `Alignment.TopCenter` on wide screens (>840.dp) to keep displays looking beautifully centered.
-   **Container Scaling**: Leverage `BoxWithConstraints` when child components must calculate sizing thresholds on the fly.
-   **Touch Target Standard**: All interactive elements maintain a minimum size of `48.dp x 48.dp` (`minimumInteractiveComponentSize()`) for accessibility compliance.

---

## 📷 2. CAMERAX & HEADSHOT CAPTURE SUBSYSTEM

MusicLinkd features a native camera headshot capture system for musicians, producers, engineers, and studios:
-   **TakePicturePreview Contract**: Integrates `ActivityResultContracts.TakePicturePreview()` and `ActivityResultContracts.RequestPermission()` for runtime `CAMERA` permission handling.
-   **Local Storage**: Captured headshots are compressed into high-quality JPEG files and persisted locally in `context.cacheDir` under `headshot_<timestamp>.jpg`.
-   **Live Preview & Gallery Pick**: Dual-mode selection allows instant camera capture or picking existing high-res press photos from the device gallery.

---

## 🗄️ 3. DECOUPLED REPOSITORY LAYER & ROOM PERSISTENCE

The data access layer separates UI/ViewModels from data sources via clean Kotlin interfaces:
-   **`UserProfileRepository`**: Defines standard CRUD operations for user profiles, headshot updates, and professional details.
-   **`RoomUserProfileRepository`**: Production Room implementation using `UserProfileDao` for reactive `Flow<UserProfileEntity?>` emission.
-   **`StubUserProfileRepository`**: In-memory repository implementation facilitating fast offline testing, UI previews, and isolated JVM unit tests.
-   **`RepositoryProvider`**: Centralized singleton providing lazy instances of repositories across the app.

---

## 🏃 4. MOTION & MICRO-INTERACTIONS

All interactive elements feature spring-physics scales or precise easing curves:

-   **Standard Easing**: `cubic-bezier(0.4, 0, 0.2, 1)` (duration: `200ms` to `300ms`) for sliding transitions and alpha fades.
-   **Spring Scaling**: Use a spring-based scale animation (`120ms` scale down to `0.98x` or spring scale up to `1.04x` for active chips) to provide elastic, physical feedback on click.
-   **Ripple Indications**: Every clickable element must wrap its click event with a Material Ripple to register clear user intent.

---

## 🧪 3. JVM & ROBOLECTRIC TESTING CONVENTIONS

We maintain highly reliable, fast, local JVM Robolectric tests to verify core repository logic, database interactions, and state flows without requiring an emulator.

### Test Guidelines
-   **In-Memory Room Instances**: Always configure testing suites to instantiate an in-memory database to prevent write contamination and keep suites isolated:
    ```kotlin
    Room.inMemoryDatabaseBuilder(context, MusicLinkdDatabase::class.java)
        .allowMainThreadQueries()
        .build()
    ```
-   **Standard Assertion**: Use Robolectric `ShadowLog` to intercept and check local debugger logs, and leverage coroutine test dispatchers to test asynchronous state flows in view models.
-   **Location**: `/app/src/test/java/com/example/ExampleRobolectricTest.kt`
-   **Run Command**: Execute `gradle :app:testDebugUnitTest` to compile and execute all tests.
