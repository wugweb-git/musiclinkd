# Modules Map

**Audited:** August 2026, against `app/app/src/main/java/com/example/`. Dated
snapshot — not synced to the live Android app, which is built and published
separately via AI Studio. Re-verify before relying on specifics.

This is the completeness inventory that seeded Part C's module registry
(`modules/{moduleKey}` in Firestore, admin panel `/modules` page) — each row
below maps to a registry seed row where one exists.

## Per-module completeness

| Module | Entry point | Backend wiring | Evidence |
|---|---|---|---|
| **Onboarding/Auth** | `WelcomeAndAuthScreens.kt` + `OnboardingViewModel` | Real — Firebase Auth (email/password, Google, phone OTP); profile written to Firestore `users` collection | `FirebaseManager.saveUserProfileToFirestore`, `OnboardingViewModel.kt:676-695` |
| **Networking Feed** | `NetworkingFeed.kt` (`SupportingModule.NETWORKING_FEED`) | Real — reads/writes Firestore `feed_posts` | `NetworkingFeed.kt:104,180` via `FirebaseManager.fetchNetworkingFeed`/`createFeedPost` |
| **Connections** | `ConnectionsScreen.kt` / `ConnectionsFlow` | Real — writes directly to Firestore `connections` | `ConnectionsScreen.kt:127`; also `FirebaseManager.kt:353` |
| **Profile edit** | `EditProfileScreen` (inside `ProfileManagementMainFlowContainer`) | Partially real, but fragmented: writes to a Firestore collection called **`musicians`** (not `users`), doc ID a slugified display name — not the Firebase Auth UID | `ProfileManagementScreens.kt:342-344` |
| **Discover** (talent/band/business/venue browsing) | `DiscoverMainFlowContainer.kt` | UI-only mockup — filters an in-memory `sampleDiscoverProfiles` list; zero Firestore/Room calls | `DiscoverViewModel.kt:339` |
| **Studio Booking** | `StudioBookingScreens.kt` | UI-only mockup — local `StudioSpace`/`StudioBooking` data classes, hardcoded sample data, zero Firestore references | full 2462-line file, see Service_Flow.md for the trace |
| **Marketplace** | `MarketplaceBetaScreen` | UI-only mockup — inline hardcoded listing data, explicitly labelled "Beta" | `SuccessAndDashboardScreens.kt:2647` |
| **Portfolio** (project publishing) | `PortfolioViewModel`/`PortfolioScreens.kt` | UI-only mockup — "publish" updates local state + Toast, zero Firestore calls | |
| **Reputation/Reviews** | `ReputationAndReviewsScreens.kt` | Local-only — Room (`ReviewRepository(database.reviewDao())`), per-device, not shared across users | |
| **Messaging** | `MessagingViewModel` / `Flow5MessagingWorkspace` | Local-only (Room: `ChatEntity`/`ConvoEntity`/`MessageEntity`) **and unreachable** — no live entry point from dashboard tabs (see Screens_Map.md navigation graph) | |
| **Notifications** | Room `NotificationEntity`/`NotificationDao` | Local-only — push arrives via FCM but the in-app list is device-local, not Firestore-synced | |
| **Verification / Moderation / Support tickets / Broadcast messages / Audit logs / Admin roles / Page content** | — | **Not present at all** — no screens, ViewModels, or Firestore references anywhere in the app | `HelpSubPage.REPORT_USER`/`REPORT_LISTING` exists as UI but no confirmed Firestore write behind it |

## Bottom line

Of the 16 canonical Firestore collections the admin panel documents, the
Android app touches only **`users`, `connections`, `feed_posts`** in a
schema-consistent way, plus an extra, ad-hoc **`musicians`** collection not
in that list. `studios`, `projects`, `marketplace_items`, `reviews`,
`studio_bookings`, `chat_threads` are never written or read by the app
despite full-featured screens existing for all of them.

## Module registry seed mapping (Part C)

Reconciling this table against `admin/config/constants.ts`'s
`MOCK_ONLY_MODULES` and `.env.example`'s never-wired `ENABLE_*` flags — see
`admin/README` / the admin `Settings` → `Modules` page for the live version
of this table. Static-config snapshot at time of writing:

| Registry docId | Source signal |
|---|---|
| `marketplace` | `.env.example` `ENABLE_MARKETPLACE` — matches this doc's "UI-only mockup" finding |
| `bookings` | `.env.example` `ENABLE_BOOKINGS` — matches "Studio Booking: UI-only mockup" |
| `analytics` | `MOCK_ONLY_MODULES` + `.env.example` `ENABLE_ANALYTICS` (admin-side; no Android analytics module exists to compare against) |
| `tracks` | `MOCK_ONLY_MODULES` (admin-side; no direct Android equivalent) |
| `ai` | `.env.example` `ENABLE_AI` — Gemini integration exists but is neutered by a placeholder API key, see Edge_Functions_And_Errors.md |
| `business` | `.env.example` `ENABLE_BUSINESS` — corresponds loosely to `BusinessProfileWorkspace` |
