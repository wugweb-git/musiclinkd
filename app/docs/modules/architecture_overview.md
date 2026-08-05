# Feature-Based Modularization Architecture Overview — MusicLinkd

## Feature-Based Modularization Strategy

MusicLinkd is structured around decoupled, feature-centric sub-packages within `com.example.onboarding.ui` to enforce clean architecture, single responsibility, and maintainability.

---

## Core Feature Modules & Responsibilities

### 1. Discover Module (`com.example.onboarding.ui.discover`)
- **Responsibilities:**
  - Directory search and category filtering across artists, producers, and session vocalists.
  - Social feed rendering (`CollabCall`, `Release`, `StudioUpdate`).
  - Gemini AI Synergy Matchmaker compatibility scoring.
- **Key Components:** `DiscoverScreen`, `DiscoverHeader`, `DiscoverFeed`, `DiscoverCards`, `DiscoverState`.

### 2. Profile Module (`com.example.onboarding.ui.profile`)
- **Responsibilities:**
  - Displaying rich artist portfolio headers, verified badges, bio, and genre chips.
  - Audio STEM preview player integration (`AudioWaveformPlayer`).
  - Firebase Storage media attachment uploader and portfolio showcase gallery.
- **Key Components:** `ArtistProfileScreen`, `PortfolioHeader`, `StemShowcase`, `MediaUploaderSheet`.

### 3. Messaging Module (`com.example.onboarding.ui.messaging`)
- **Responsibilities:**
  - Real-time peer-to-peer direct messaging threads.
  - FCM push notification triggers upon receiving new chat messages.
  - Offline message caching via local Room SQLite Database.
- **Key Components:** `MessagingScreen`, `ChatThreadView`, `MessageInputField`, `DirectMessageItem`.

### 4. Marketplace Module (`com.example.onboarding.ui.marketplace`)
- **Responsibilities:**
  - Audio gear sales, vintage synthesizer listings, and STEM pack distribution.
  - Seller inquiry messaging triggers and condition tags.
- **Key Components:** `MarketplaceScreen`, `MarketplaceItemCard`, `GearCategoryFilter`.

### 5. Booking Module (`com.example.onboarding.ui.studios`)
- **Responsibilities:**
  - Recording studio directory search and Dolby Atmos suite inspection.
  - Date and hourly slot picker (`StudioSlotPickerWidget`).
  - Session reservation confirmation and booking receipt persistence.
- **Key Components:** `StudioBookingScreen`, `StudioSuiteCard`, `SlotPickerSheet`, `BookingConfirmationCard`.
