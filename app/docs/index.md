# MusicLinkd Repository Master Index (`docs/index.md`)

> **Repository Status:** `Release Candidate (v1.5.0-RC)` — Frozen production baseline. All ongoing changes across `musiclinkd.com`, `app.musiclinkd.com`, and `admin.musiclinkd.com` must strictly maintain this governance framework.

Welcome to the master index of the **MusicLinkd** repository. This document provides direct links and structural navigation across all product targets, engineering modules, design system tokens, and architecture specifications.

---

## 🌐 Products Topology

| Product Target | Description | Target URL | Primary Tech Stack |
| :--- | :--- | :--- | :--- |
| **Public Website** | Editorial landing pages, SEO catalog, pricing tiers & download links | `musiclinkd.com` | Next.js / Web |
| **Application Target** | M3 Jetpack Compose Android app for musicians, vocalists & studios | `app.musiclinkd.com` | Kotlin, Jetpack Compose, Room, Firebase |
| **Admin Portal** | Enterprise control center for moderation, verification & system logs | `admin.musiclinkd.com` | React / Admin Dashboard |
| **Official Email** | Official contact, support, and security disclosures | `hello@musiclinkd.com` | Security / Support Channel |

---

## 📁 Repository Documentation Index

### 1. Architecture & System Design
- [Application Architecture](docs/Architecture.md)
- [Visual Application Architecture Diagram](docs/architecture/Visual_Application_Architecture.md)
- [Module Architecture Overview](docs/modules/architecture_overview.md)
- [Visual Module Dependencies Diagram](docs/modules/Visual_Module_Dependencies.md)
- [Repository Architecture Diagram](docs/repositories/Visual_Repository_Dependencies.md)
- [Database Schema & Firestore Collections](docs/database/schema_definition.md)
- [Visual Firestore ERD Diagram](docs/database/Visual_Firestore_ERD.md)
- [Architecture Decision Records (ADRs)](docs/adr/)

### 2. User Flows & Visual Diagrams
- [User Journeys Catalog](docs/journeys/)
- [Navigation Graph & Route Registry](docs/navigation/navigation_graph.md)
- [Visual Navigation Graph](docs/navigation/Visual_Navigation_Graph.md)
- [Core User Flow Sequence Diagrams](docs/flows/Core_Flow_Diagrams.md)

### 3. Product Features & Governance Matrices
- [Platform Roles Matrix](docs/roles/platform_roles.md)
- [Feature Access Matrix](docs/features/feature_matrix.md)
- [Cross-Channel Notification Matrix](docs/notifications/Notification_Matrix.md)
- [Analytics Event Taxonomy](docs/analytics/Analytics_Event_Taxonomy.md)
- [Error Catalogue](docs/errors/Error_Catalogue.md)

### 4. Products & Future Integrations
- [Website Architecture & SEO (`musiclinkd.com`)](docs/website/Website_Architecture_And_SEO.md)
- [Admin Portal Readiness (`admin.musiclinkd.com`)](docs/admin/Admin_Readiness_Architecture.md)
- [API Endpoint Specifications](docs/api/API_Endpoint_Specifications.md)

### 5. Design System & UI Catalogues
- [Design System Showcase](styleguide/Showcase.md)
- [Icon Library Catalog](docs/branding/icon_library.md)
- [GSAP Web & Compose Animations Specification](docs/animations/gsap_and_compose_animations.md)
- [Native Android Capabilities Specification](docs/android/Native_Android_Features.md)
- [Component Catalogue](docs/components/Component_Catalogue.md)
- [Widget Catalogue](docs/widgets/Widget_Catalogue.md)
- [Block Catalogue](docs/blocks/Block_Catalogue.md)
- [Accessibility Implementation Checklist](styleguide/Accessibility.md)
- [Motion & Gestures Guide](styleguide/motion.md)

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
