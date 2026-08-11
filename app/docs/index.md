# MusicLinkd Repository Master Index (`docs/index.md`)

> **Repository Status:** `Release Candidate (v1.5.0-RC)` — Frozen production baseline. All ongoing changes across `musiclinkd.com`, `app.musiclinkd.com`, and `admin.musiclinkd.com` must strictly maintain this governance framework.

Welcome to the master index of the **MusicLinkd** repository. To prevent documentation decay, fragmentation, and duplication, all technical, design, and product specifications are consolidated into three single-source-of-truth master guides:

---

## 🌐 Products Topology

| Product Target | Description | Target URL | Primary Tech Stack |
| :--- | :--- | :--- | :--- |
| **Public Website** | Editorial landing pages, SEO catalog, pricing tiers & download links | `musiclinkd.com` | Next.js / Web |
| **Application Target** | M3 Jetpack Compose Android app for musicians, vocalists & studios | `app.musiclinkd.com` | Kotlin, Jetpack Compose, Room, Firebase |
| **Admin Portal** | Enterprise control center for moderation, verification & system logs | `admin.musiclinkd.com` | React / Admin Dashboard |
| **Official Email** | Official contact, support, and security disclosures | `hello@musiclinkd.com` | Security / Support Channel |

---

## 📁 Ecosystem Single-Source-of-Truth Specifications

### 🏛️ 1. Master System & Architecture Specifications
* 🛠️ **[Master System Architecture & Product Specifications (Architecture.md)](Architecture.md)** — Clean architecture, PRD, supported roles, package layout, state-driven navigation, local Room caching, and FCM push notifications.
* 🗄️ **[Backend Schemas & Live Firestore Sync (BACKEND_SCHEMA_SPEC.md)](BACKEND_SCHEMA_SPEC.md)** — Live backend inventory for `musiclinkd-7ae30`: `studio_bookings`, `reviews`, `verification_requests`, `connections`, `projects`, `user_bookmarks`, `modules` remote feature flags, and all 11 dynamic master taxonomy categories.

### 🎨 2. Unified Brand, Design Systems & Master Component Catalog
* 📐 **[Design System Foundations (styleguide/Foundations.md)](styleguide/Foundations.md)** — Consolidated 8-layer architectural model, v4.0 color palette, typography hierarchy, spacing grids, Lucide iconography standards, and native accessibility.
* 🧩 **[Master Component Catalog (styleguide/Components.md)](styleguide/Components.md)** — Master catalog of all components, widgets, base cards, structural UI blocks, layout templates, and motion transitions.

---

## 💻 Kotlin Package Architecture (`app/src/main/java/com/example/onboarding/`)

- `ai/`: Gemini API 1.5 Flash AI Synergy Matchmaker.
- `audio/`: Media3 / ExoPlayer waveform audio preview engine.
- `database/`: Room SQLite (`AppDatabase`) & Firebase Firestore manager (`FirebaseManager`).
- `messaging/`: FCM push notification handler (`AppNotificationManager`).
- `models/`: Domain entities (`DemoArtist`, `DemoStudio`, `DemoTrack`, `DemoFeedPost`).
- `ui/components/`: Standardized design tokens & components (`UnifiedComponents.kt`).
- `ui/screens/`: Feature screens (`DiscoverScreens`, `PortfolioScreens`, `MessagingScreens`, `StudioBookingScreens`).
- `util/`: `DemoDataProvider` central seed engine.
- `viewmodel/`: StateFlow ViewModels (`DiscoverViewModel`, `MessagingViewModel`, `PortfolioViewModel`).
