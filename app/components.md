# MUSICLINKD REUSABLE COMPONENT CATALOG

This document lists all verified, high-fidelity reusable Jetpack Compose components designed and built for the MusicLinkd application. These components are centralized inside `UnifiedComponents.kt` and are recycled across all screens using parameterized states to enforce a consistent, cohesive visual identity.

---

## 🎨 1. CORE COMPONENT STANDARDS & RECYCLING RULES

Every reusable component is engineered to comply with the brand's premium, editorial style:
- **Base Container Specification**: Every card utilizes the same base container component with a uniform **24.dp corner radius** (`RoundedCornerShape(24.dp)`), **1.dp solid border** using `BrandBorder` (`#ECECF1`), and flat white background (`#FFFFFF`).
- **State Recycling Pattern**: Instead of creating ad-hoc custom cards, components accept parameter flags (`status`, `isPlaying`, `selected`, `isLoading`, `isVerified`, `initialStatus`) to render various UI states seamlessly.
- **Surface Elevation**: Flat containers with clean negative space, avoiding cluttered shadow noise.
- **Interactive State**: Built with standard Material Ripple indication and maintaining accessible touch targets of at least **48.dp x 48.dp**.
- **Human-First Photography**: All user avatars, recommended connections, and profiles load **real-life professional photographs** via Unsplash (using the Coil `AsyncImage` loader) with graceful fallback icons.

---

## 🏛️ 2. CONSOLIDATED COMPONENT SYSTEM

All master components are defined in:
`app/src/main/java/com/example/onboarding/ui/components/UnifiedComponents.kt`

### 1. Unified Chip (`UnifiedChip`)
- **Purpose**: Creative tags, performance instruments, genre selectors, and filter pills.
- **Anatomy**: Pill-shaped container (`999.dp` corner size) with custom checking indicator (`✓`) displayed on active selection, paired with spring-physics scaling transitions.

### 2. Unified Input Field (`UnifiedInputField`)
- **Purpose**: Form fields, onboarding inputs, credentials, and message writing boxes.
- **Anatomy**: Filled white background with rounded outline, dedicated label header, support for leading/trailing action icons, and automated focus borders.

### 3. Unified Button Systems
- **`UnifiedPrimaryButton`**: Vibrant MusicLinkd Pink background (`#EA4C89`), bold centered white typography, rounded pill shape (`999.dp`), and leading icon capability.
- **`UnifiedSecondaryButton`**: Transparent background with light silver border outlines (`#ECECF1`) and high-contrast navy text.
- **`UnifiedTextButton`**: Transparent and borderless for passive selections or back triggers.
- **`UnifiedConnectButton`**: Interactive connection button with live state handling (`IDLE`, `PENDING`, `CONNECTED`) and direct Cloud Firestore mutation logic.

### 4. Unified Search Bar & TopBar (`UnifiedSearchBar` & `GlobalSearchTopBar`)
- **Purpose**: Directory search, marketplace listings, and talent search inputs with category filters (Artists, Tracks, Studios, Projects).
- **Anatomy**: Spacious search bar with `16.dp` rounded corners, leading Magnifying Glass icon, trailing clear (`X`) button, and integrated category chips.

### 5. Unified Bottom Navigation (`UnifiedBottomNavigation`)
- **Purpose**: Master application tab switching.
- **Anatomy**: Solid white base spanning screen width, rendering master hubs (*Home*, *Discover*, *Marketplace*, *Messages*, *Profile*) with active Pink indicator highlights.

### 6. Unified Profile Header & Artist Profile (`UnifiedProfileHeader` & `ArtistProfileScreen`)
- **Purpose**: Profile summaries, portfolio sheets, and full artist showcase screen with Firebase Auth email binding.
- **Anatomy**: High-resolution avatar portrait, verified artist check badge, location metadata, availability state, audio waveform player, and gear lists.

### 7. Unified Artist Card (`UnifiedArtistCard`)
- **Purpose**: Main grid or list item showcasing creative talent and music producers.
- **Anatomy**: `24.dp` rounded card layout, high-res portrait, verified check, availability pulse, location/genre indicators, and direct connect action.

### 8. Unified Studio Card (`UnifiedStudioCard` & `UnifiedBusinessHeader`)
- **Purpose**: Recording venues, rehearsal slots, and acoustic spaces.
- **Anatomy**: Studio imagery, Gold verification badge, community star rating (`★`), equipment lists, hourly rates, and booking trigger.

### 9. Unified Track Card (`UnifiedTrackCard` & `WaveformAudioPlayer`)
- **Purpose**: STEM audio preview, portfolio tracks, and player controls.
- **Anatomy**: Album art cover, title, artist name, duration, animated waveform audio spectrum bar, and toggle play/pause state.

### 10. Unified Project & Opportunity Cards (`UnifiedProjectCard` & `UnifiedOpportunityCard`)
- **Purpose**: Collaboration sessions, open audition calls, and freelance gigs.
- **Anatomy**: Project title, creator name, seeking roles, genre chips, and session application triggers.

### 11. Unified Booking Card (`UnifiedBookingCard`)
- **Purpose**: Live studio reservations and booking schedule cards.
- **Anatomy**: Booking title, date/time slot, status badge (`LIVE`, `PENDING`, `CONFIRMED`), and price summary.

### 12. Unified Notification Card (`UnifiedNotificationCard`)
- **Purpose**: Push notification center and activity alerts.
- **Anatomy**: Title, description, time ago text, unread indicator dot, and click handler.

### 13. Networking Feed (`NetworkingFeed`)
- **Purpose**: Professional social updates feed for artists, producers, and studios.
- **Anatomy**: Integrated post composer card, Firestore stream data reader, filter chips (`COLLAB_CALL`, `RELEASE`, `STUDIO_UPDATE`), stem audio player, and connect buttons.

### 14. Centralized Error Handling & Error Boundary (`UnifiedErrorCard` & `ErrorBoundary`)
- **Purpose**: Centralized UI error wrapper and card for catching network failures, timeouts, and failed data queries.
- **Anatomy**: Base 24.dp container card with warning icon, error title, descriptive message, and retry button (`UnifiedPrimaryButton`).

### 15. Media Attachments & Cloud Storage (`MediaAttachmentItem`)
- **Purpose**: Audio STEM snippets and gallery photo attachments embedded in Artist Profiles.
- **Anatomy**: Integrated Firebase Storage media uploader button, waveform audio player cards, and behind-the-scenes artwork gallery grid within 24.dp base containers.

---

## 📂 3. COMPONENT FILE STRUCTURE

- **`UnifiedComponents.kt`** (`com/example/onboarding/ui/components/UnifiedComponents.kt`): Holds master consolidated UI components.
- **`OnboardingUIComponents.kt`** (`com/example/onboarding/ui/components/OnboardingUIComponents.kt`): Layout headers, indicators, and `NativeQRCodeCanvas` drawing logic.
- **`EmptyState.kt`** (`com/example/onboarding/ui/components/EmptyState.kt`): Universal template for empty lists, search queries, and empty channels.
