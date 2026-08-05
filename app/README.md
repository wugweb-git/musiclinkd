# MusicLinkd — Professional Music Collaboration Platform & Ecosystem

> **Repository Status:** `Release Candidate (v1.5.0-RC)` — Final frozen baseline for production deployment across `musiclinkd.com`, `app.musiclinkd.com`, and `admin.musiclinkd.com`.

Welcome to **MusicLinkd**, the premier Android application and platform connecting independent musicians, session vocalists, instrumentalists, audio engineers, recording studios, and indie record labels.

---

## 🌐 Product Ecosystem Topology

MusicLinkd is operated across three distinct environment targets:

- **Public Website & Portal:** [`musiclinkd.com`](https://musiclinkd.com)
- **Mobile Web & Application Target:** [`app.musiclinkd.com`](https://app.musiclinkd.com)
- **Admin & Enterprise Management:** [`admin.musiclinkd.com`](https://admin.musiclinkd.com)
- **Official Contact Email:** [`hello@musiclinkd.com`](mailto:hello@musiclinkd.com)
- **GitHub Repository:** [`https://github.com/wugweb-git/musiclinkd`](https://github.com/wugweb-git/musiclinkd)

---

## 🎼 Ecosystem Capabilities & Catalog

- **Supported Languages:** English, Hindi, Punjabi, Tamil, Telugu, Bengali, Marathi, Spanish, French, Portuguese (`docs/catalog/Instruments_Languages_Platforms.md`).
- **Musical Instruments:** String & fretted (Sitar, Guitar, Bass), Percussion (Tabla, Drum Kits, Cajón), Keyboards (Piano, Harmonium, Synths), Brass/Wind (Bansuri Flute, Saxophone), Vocals (Carnatic, Hindustani, Western, Rap).
- **Supporting Streaming Integrations:** Spotify, Apple Music, YouTube Music, SoundCloud, Tidal, Audiomack, Bandcamp, Instagram, YouTube.

---

## 📚 Architectural & Engineering Documentation

- **Architecture Decision Records (ADR):** `docs/adr/` (State-driven navigation, Firebase-Room hybrid persistence, Seeded relational data).
- **Domain Data Models:** `docs/models/Data_Models_Catalog.md` (Artist, Project, Track, Review, Studio, Booking, Conversation, MarketplaceItem, Notification).
- **Firestore Schema & Rules:** `docs/firestore/Firestore_Schema_Index_Rules.md`.
- **Feature & Permissions Matrix:** `docs/features/Feature_Access_Matrix.md` and `docs/permissions/Permissions_Matrix.md`.
- **Analytics & Errors:** `docs/analytics/Analytics_Event_Taxonomy.md` and `docs/errors/Error_Catalogue.md`.
- **Single Source of Truth Styleguide:** `styleguide/` (Tokens, components, motion, accessibility, patterns).

---

## 🚀 Key Features

- **Centralized Demo Data & Relational Stems:** High-resolution Unsplash artist portfolios, studio equipment lists, and audio waveform preview tracks.
- **AI Matchmaking & Synergy Engine:** Powered by Gemini 1.5 Flash to evaluate creative collaboration compatibility.
- **Firebase Auth & Real-Time Sync:** Support for Google One-Tap, Email/Password sign-in, Firestore live updates, and FCM push notifications.
- **Studio Session Booking:** Hourly studio reservation system with equipment selection and verified client reviews.
- **Local Caching & Resilience:** Room SQLite Database for offline usability and fault-tolerant local state fallback.

---

## 🛠 Tech Stack

- **Language:** Kotlin 2.0+
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **State Management:** `ViewModel` with Kotlin `StateFlow` and UDF (Unidirectional Data Flow)
- **Asynchronous Operations:** Kotlin Coroutines & Flow
- **Local Persistence:** Room Database with KSP processing
- **Backend Integrations:** Firebase Auth, Cloud Firestore, Firebase Storage, FCM
- **Build System:** Gradle Kotlin DSL (`build.gradle.kts`)

---

## 📁 Repository Structure

```
.
├── app/                        # Android application source code
│   └── src/main/java/com/example/onboarding/
│       ├── ai/                 # Gemini AI synergy engine
│       ├── api/                # REST clients & image utilities
│       ├── audio/              # ExoPlayer & audio waveform player
│       ├── database/           # Room DB & FirebaseManager
│       ├── messaging/          # FCM Push Notification Service
│       ├── models/             # Domain data models & UI states
│       ├── ui/                 # Jetpack Compose UI (components & screens)
│       └── util/               # Centralized DemoDataProvider & helpers
├── docs/                       # Complete engineering documentation suite
├── styleguide/                 # Design System & UI specification
├── .env.development            # Development environment configuration
├── .env.staging                # Staging environment configuration
├── .env.production             # Production environment configuration
├── governance.md               # Mandatory engineering rules & Definition of Done
├── update.md                   # Real-time engineering updates log
└── CHANGELOG.md                # Release version history
```

---

## 📄 License & Governance

MusicLinkd is distributed under the MIT License. Refer to `LICENSE` and `governance.md` for contribution guidelines and mandatory engineering standards.
