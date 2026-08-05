# MusicLinkd — AI Agent Memory & Operational Rules

This document serves as persistent memory and instructions for AI Studio Agents working on the **MusicLinkd** project ecosystem.

---

## 🌐 Product Topology & Target Hosts

MusicLinkd is operated across three isolated target environments and one primary email contact:

1. **Public Website (`musiclinkd.com`):** Editorial marketing landing pages, SEO catalog, pricing tiers, and public artist/studio discovery previews.
2. **Application Target (`app.musiclinkd.com`):** High-contrast light Jetpack Compose Android app for musicians, vocalists, producers, audio engineers, and studio bookings.
3. **Admin Portal (`admin.musiclinkd.com`):** Enterprise control center for moderation, verification queue, flagged content, user management, and system analytics.
4. **Official Contact Email:** `hello@musiclinkd.com`
5. **GitHub Repository:** `https://github.com/wugweb-git/musiclinkd`

---

## 📜 Mandatory Engineering Rule (Definition of Done)

> **"No implementation, bug fix, refactor, feature, UI update or documentation update is complete until `update.md`, `CHANGELOG.md` and all relevant documentation have been updated."**

---

## 🎨 Design System Principles & Constraints

- **Base Container Radius:** `24.dp` on all cards, modals, sheets, and dialogs.
- **Card Styling:** White canvas (`#FFFFFF`) with a `1.dp` border stroke in `BrandBorder` (`#ECECF1`). Drop shadows are strictly avoided.
- **Brand Primary:** `#EA4C89` (Dribbble Pink / Active Accents).
- **Brand Dark:** `#0D0B26` (Midnight Navy / Primary Typography).
- **Brand Background:** `#FCFCFD` (Soft Off-White Canvas Canvas).
- **Touch Targets:** Minimum dimensions of `48.dp x 48.dp`.
- **Reusable Components:** All standard cards, buttons, chips, headers, error cards, and slot pickers MUST be sourced directly from `com.example.onboarding.ui.components.UnifiedComponents`.

---

## 📁 Source Code & Module Layout

- `app/src/main/java/com/example/onboarding/`
  - `ai/`: Gemini API 1.5 Flash synergy matchmaker service.
  - `api/`: REST utilities & Unsplash image helper.
  - `audio/`: Waveform audio player and Media3 / ExoPlayer state handling.
  - `database/`: Room SQLite database (`AppDatabase`) and Firebase Firestore manager (`FirebaseManager`).
  - `messaging/`: FCM push notification receiver (`AppNotificationManager`).
  - `models/`: Domain entities (`DemoArtist`, `DemoStudio`, `DemoTrack`, `DemoFeedPost`, `MediaAttachmentItem`).
  - `ui/`:
    - `components/`: Standardized design system components (`UnifiedComponents.kt`).
    - `screens/`: Screen composables grouped by feature module.
  - `util/`: Centralized `DemoDataProvider` seed data engine.
  - `viewmodel/`: `AuthViewModel`, `DiscoverViewModel`, `MessagingViewModel`, `PortfolioViewModel`.

---

## 🛠 Local Memory Key Terms & Seed Rules

- `DemoDataProvider`: The single source of truth for test/mock state. Never create inline hardcoded mock arrays inside screen composables.
- `Resource<T>` State Pattern: All async data flows MUST expose `Loading`, `Success`, `Empty`, or `Error` states.
