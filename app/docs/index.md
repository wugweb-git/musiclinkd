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

### 🏛️ 1. Master System & Product Specifications
* 🛠️ **[Master System Architecture & Product Specifications (Architecture.md)](Architecture.md)** - Authoritative guide containing:
  - **Product Roadmap & PRD:** Supported roles, user personas, and core functional modules.
  - **Clean Architecture & MVVM:** Package layout, sealed navigation routing states, and system specifications.
  - **Data Schemas & Firebase:** Room SQLite tables, remote Firestore NoSQL collections, and GMS integration parameters.
  - **Peer Handshake Sync:** Double-opt-in connection request states and resilient message synchronization queues.
  - **Notifications & Motion:** FCM JSON payloads, horizontal page transitions, and frequency waveform visualization physics.
  - **Security, Native UI, & Release:** Alphanumeric form sanitization, edge-to-edge layouts, minimum touch targets, dynamic analytics taxonomy, local JVM test verification commands, and release build guidelines.

### 🎨 2. Unified Brand & Design Specifications
* 🎨 **[Design System Foundations (../styleguide/Foundations.md)](../styleguide/Foundations.md)** - Visual philosophy, color palette, typography hierarchy, spacing grids, icons, and native accessibility.
* 🧩 **[Design System Components (../styleguide/Components.md)](../styleguide/Components.md)** - Unified components, interactive screens, layout templates, specialized widgets, and micro-animations.

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
