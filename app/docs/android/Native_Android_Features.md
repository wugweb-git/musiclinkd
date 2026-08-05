# Native Android Capabilities & Features Reference — `app.musiclinkd.com`

## Native Android Integration Specification

`app.musiclinkd.com` leverages deep Android OS integrations to deliver an uncompromised native experience for musicians and recording studios.

---

## 🎧 1. Media3 & ExoPlayer Audio STEM Engine (`com.example.onboarding.audio`)
- **Background Playback Service:** Audio STEM preview tracks continue playing seamlessly when the user switches apps or locks their screen.
- **Media Notification Controls:** Native Android lockscreen and status bar notification controls (Play/Pause, Skip, Waveform scrubber).
- **Audio Focus Management:** Gracefully pauses audio previews when an incoming phone call or external audio event occurs (`AudioManager.AUDIOFOCUS_GAIN`).

---

## 📱 2. WindowInsets & Edge-to-Edge Display
- **`enableEdgeToEdge()`:** Draws transparent system bars (status bar and navigation bar) for a immersive visual layout.
- **Dynamic Inset Padding:** `Modifier.windowInsetsPadding(WindowInsets.statusBars)` and `WindowInsets.navigationBars` ensure no UI elements collide with camera cutouts or gesture navigation pill bars.

---

## 🔔 3. Firebase Cloud Messaging (FCM) & Push Channels (`AppNotificationManager`)
- **Notification Channels (`res/values/strings.xml`):**
  - `channel_messages`: High priority channel for direct chat messages with custom vibration pattern.
  - `channel_bookings`: Critical priority channel for studio session reservation confirmations.
  - `channel_social`: Normal priority channel for connection requests and collaboration matches.

---

## 📳 4. Haptic Feedback & Tactile Precision
- **`LocalHapticFeedback.current`:** Delivers crisp tactile haptic feedback (`HapticFeedbackType.LongPress` and `TextHandleMove`) when scrubbing audio waveforms or confirming studio booking reservations.

---

## 🎨 5. Material You Dynamic Color & Light Mode Scheme
- **System Theme Sync:** Automatically adapts accent primary hues on Android 12+ (API level 31+) while guaranteeing high-contrast readability against the `#FCFCFD` canvas.
