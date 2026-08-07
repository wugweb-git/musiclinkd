# MusicLinkd Android App — Design System & Codebase Audit

> **Source audited:** `/app` (Native Android · Kotlin · Jetpack Compose · Firebase · Room · Gemini AI)
> **Purpose:** Single source inventory for styleguide sync + database mapping.
> **Generated:** 2026-07-08

---

## 0. Executive Summary (Counts)

| Category | Count | Notes |
|---|---|---|
| Personas / Professional Roles | **13** | 7 individual + 6 business |
| Instruments (taxonomy) | **9+** | core demo set |
| Languages | **10** | onboarding catalog |
| Vocal Styles / Registers | **6** | |
| Genres (demo) | **8+** | |
| Primitive components | **8** | buttons, chips, inputs, tabs |
| Badge components | **7** | |
| Card components | **14** | |
| Audio widgets | **5** | |
| Skeleton loaders | **5** | |
| Feedback/Empty/Error components | **7** | |
| Modals / Bottom sheets | **4** | |
| Navigation components | **4** | |
| Feature areas (screens) | **9** | discover, profile, booking, messaging, reputation, portfolio, auth/onboarding, supporting, dashboard |
| Room entities | **7** | + DAO + Repository each |
| Feature modules | **6** | auth, discover, marketplace, messaging, profile, studios |
| AI engines | **2** | GeminiRecommendationEngine, GeminiImageGenerator |
| Notification channels | **1** service | FCM + local manager |

---

## 1. Design Tokens

> ⚠️ **Note:** The app code contains per-file constant drift (`Constants_0.kt`, `Constants_37.kt`). Canonical tokens live in `ui/theme/Color.kt` + `Type.kt`. The **HTML design-system tokens are the source of truth** going forward; app values noted here for mapping.

### 1.1 Color Tokens

| Token | App Value (Compose) | Styleguide Value | Role |
|---|---|---|---|
| BrandPrimary / Pink500 | `#EA4C89` | `#EA4C89` | Primary CTA, active |
| Pink600 | `#D93C77` | `#D93C77` | Hover/pressed |
| Pink700 | `#C72F66` | — | Deep pressed |
| NavyDark / BrandDark | `#0D0B26` | `#0D0B26` | Headers, dark |
| BrandAccent (Purple) | `#7C3AED` | `#8B5CF6` | AI + Business badge only |
| Success500 | `#22C55E` | `#22C55E` | Confirmed/available |
| Warning500 | `#F59E0B` | `#F59E0B` | Pending/caution |
| Error500 | `#DC2626` | `#DC2626` | Rejected/critical |
| Info | `#2563EB` | `#2563EB` | "On Tour" badge |

**Neutral scale (13):** `#FFFFFF` `#FCFCFD` `#F8F8FB` `#F3F3F6` `#ECECF1` `#DDDEE5` `#B8BAC7` `#8E91A1` `#676B7B` `#4A4D5C` `#2C2F3A` `#181A22` `#0D0F14`
**Brand pink scale (9):** `#FFF1F6` `#FFE1ED` `#FFC2DA` `#FFA3C7` `#F472A6` `#EA4C89` `#D93C77` `#C72F66` `#B41E53` `#8D0F3C`

### 1.2 Typography Tokens

| Token | Size/Weight | Family | Usage |
|---|---|---|---|
| Display | 56sp / 800 | Inter Tight | Hero, splash |
| H1 | 28sp / 700 | Inter Tight | Section titles |
| H2 | 20sp / 600 | Inter Tight | Card titles |
| H3 | 16sp / 600 | Inter Tight | Dialog headers |
| Body | 14sp / 400 | Inter | Paragraphs |
| Caption | 11sp / 400 | Inter | Tags, micro-text |
| Overline | 10sp / 700 +0.1em | Inter | Eyebrow labels |

### 1.3 Spacing / Radius / Elevation

- **Spacing:** 4, 8, 12, 16, 24, 32, 48 dp
- **Radius:** card 24dp · media 16dp · pill 999dp · avatar 50% · toast 16dp
- **Elevation:** Level 0–3, modal, bottom-sheet, FAB
- **Touch target:** 48dp minimum (all interactive)

---

## 2. Primitives

| ID | Component | File | Spec | States |
|---|---|---|---|---|
| prim-primary-btn | `UnifiedPrimaryButton` | UnifiedPrimaryButton.kt | 50dp pill, pink bg, white 15sp bold, optional icon | enabled / disabled |
| prim-secondary-btn | `UnifiedSecondaryButton` | UnifiedSecondaryButton.kt | 50dp pill, 1dp `#ECECF1` border | enabled / disabled |
| prim-text-btn | `UnifiedTextButton` | UnifiedTextButton.kt | pink bold ghost text | enabled / disabled |
| prim-connect-btn | `UnifiedConnectButton` | UnifiedConnectButton.kt | connect ↔ connected toggle | default / connected |
| prim-chip | `UnifiedChip` | UnifiedChip.kt | 999dp pill, 16×10dp pad; selected = pink 8% bg + 2dp border + ✓; spring scale 1.0→1.04 | selected / unselected |
| prim-input | `UnifiedInputField` | UnifiedInputField.kt | 999dp OutlinedTextField, 56dp height, icons, keyboard actions | default / focus / error |
| prim-search | `UnifiedSearchBar` | UnifiedSearchBar.kt | pill search w/ leading icon | default / focus |
| prim-tabrow | `UnifiedTabRow` | UnifiedTabRow.kt | tab strip | selected / unselected |

**Native primitives in use:** Switch · Slider · DatePicker · OTP boxes · Radio · Checkbox · FAB · Snackbar · TopAppBar · NavigationBar · AlertDialog · Permission dialog.

---

## 3. Badges & Status

| ID | Component | Variants |
|---|---|---|
| badge-status | `UnifiedStatusBadge` | Available (green) · In Session (amber) · On Tour (blue) |
| badge-verify | `UnifiedVerificationBadge` | Pro Verified (pink) · Business Studio (purple) |
| badge-glow | `PulsingGlowBadge` | animated pulseGlow |
| badge-persona | `PersonaTypeBadge` | persona role tag |
| badge-stage | `StagePositionPill` | stage/level |
| badge-social | `SocialIntegrationBadge` | social platform |
| badge-rating | `UnifiedStarRating` | 0–5 stars |

---

## 4. Cards (14)

| ID | Component | Purpose |
|---|---|---|
| card-artist | `UnifiedArtistCard` | Talent discovery: avatar, waveform, genre chips, stats, Message+Hire |
| card-studio | `UnifiedStudioCard` | Studio: photo, gear chips, ₹/hr, rating, Book |
| card-booking | `UnifiedBookingCard` | Session: room, datetime, price, Reschedule/Cancel |
| card-marketplace | `UnifiedMarketplaceCard` | Gear sale: photo, price, Message Seller |
| card-opportunity | `UnifiedOpportunityCard` | Casting brief: budget, applications, Apply |
| card-project | `UnifiedProjectCard` | Project credits: cover, collaborators, verify |
| card-review | `UnifiedReviewCard` | Review: avatar, stars, text |
| card-notification | `UnifiedNotificationCard` | Notification row: unread dot, timestamp |
| card-track | `UnifiedTrackCard` | Audio track: waveform, play |
| card-error | `UnifiedErrorCard` | Error state + retry |
| card-artist-alt | `ArtistCard` | Discover variant |
| card-search-result | `DiscoverySearchResultCard` | Search result |
| card-collaborator | `RecommendedCollaboratorCard` | Recommended match |
| card-venue-item | `StudioVenueListItem` | Venue/studio row |

---

## 5. UI Blocks / Widgets / Skeletons / Feedback

**Audio widgets (5):** `WaveformAudioPlayer` · `AnimatedWaveform` · `AnimatedEqualizerWave` · `GlobalPersistentAudioPlayerBar` · `AudioPlayerManager`
**Skeleton loaders (5):** `DiscoverFeedSkeletonLoader` · `ChatFeedSkeletonLoader` · `NotificationListSkeletonLoader` · `PortfolioGridSkeletonLoader` · `ProfileSkeletonLoader`
**Feedback/Empty/Error (7):** `EmptyState` · `IllustratedEmptyStateView` · `ErrorBoundary` · `ScreenErrorBoundary` · `FallbackErrorScreen` · `MicroCheckmarkSuccessAnimation` · `InteractiveLikeButton`
**Modals/Sheets (4):** `HireContactModal` · `AdvancedFiltersBottomSheet` · `ProfilePreviewSheet` · `WhatsAppShareSheet`
**Navigation (4):** `UnifiedBottomNavigation` · `GlobalSearchTopBar` · `UnifiedProfileHeader` · `UnifiedBusinessHeader`
**Content blocks:** `NetworkingFeed` · `TrendingMusicProjectsSection` · `RecommendedCollaboratorsSection` · `HomeSectionHeader` · `FilterSectionHeader` · `DiscoverFlowRow` · `FilterChipCustom`

---

## 6. Icon Library

- **`components/IconLibrary.kt`** — central Compose icon set (music + UI, Lucide-style 1.75dp stroke)
- **Vector drawables:** `ic_musiclinkd_logo` · `ic_musiclinkd_connections` · `ic_musiclinkd_studio` · `ic_musiclinkd_verified` · launcher set
- **Inline icons across UI:** search, play, message, share, heart, clock, calendar, bookmark, edit, check, settings

---

## 7. PERSONAS — 13 Professional Roles  *(DB-mappable)*

| # | ID | Name | Type | Profile Card | Job-to-be-done |
|---|---|---|---|---|---|
| 1 | persona-vocalist | Vocalist | individual | talent | Playback & session vocals |
| 2 | persona-instrumentalist | Instrumentalist | individual | talent | Sitar, Tabla, Drums, Bass sessions |
| 3 | persona-beatmaker | Producer / Beatmaker | individual | talent | Beat leases & stem licensing |
| 4 | persona-composer | Composer | individual | talent | OSTs & project briefs |
| 5 | persona-mix-engineer | Mix Engineer | individual | talent | Mixing & verified credits |
| 6 | persona-mastering | Mastering Engineer | individual | talent | Lossless stem delivery |
| 7 | persona-voice-artist | Voice Artist | individual | talent | Dubbing & VO samples |
| 8 | persona-studio | Recording Studio | business | studio | Rooms, gear, calendar, reviews |
| 9 | persona-label | Record Label / Management | business | business | Roster, briefs, contracts, royalties |
| 10 | persona-school | Music School / Educator | business | business | Courses, admissions, student network |
| 11 | persona-venue | Venue / Event Organiser | business | venue | Calendar, bookings, event listings |
| 12 | persona-rental | Gear Rental Provider | business | rental | Inventory, daily rates, listings |
| 13 | persona-manager | Manager / Business | business | business | Rosters, bookings, procurement |

*(Also referenced: Music Director, Podcaster, Teacher as sub-roles.)*

---

## 8. TAXONOMY  *(DB-mappable)*

### 8.1 Languages (10)
| ID | Language |
|---|---|
| lang-en | English |
| lang-hi | Hindi (हिंदी) |
| lang-pa | Punjabi (ਪੰਜਾਬੀ) |
| lang-ta | Tamil (தமிழ்) |
| lang-te | Telugu (తెలుగు) |
| lang-bn | Bengali (বাংলা) |
| lang-mr | Marathi (मराठी) |
| lang-es | Spanish |
| lang-fr | French |
| lang-other | Other |

### 8.2 Instruments (9 core)
| ID | Instrument |
|---|---|
| inst-sitar | Sitar |
| inst-tabla | Tabla |
| inst-harmonium | Harmonium |
| inst-bansuri | Bansuri Flute |
| inst-guitar | Acoustic Guitar |
| inst-bass | Bass |
| inst-cajon | Cajón |
| inst-synth | Synths |
| inst-violin | Violin |

### 8.3 Vocal Styles (6)
| ID | Style |
|---|---|
| vocal-hindustani | Hindustani Classical |
| vocal-carnatic | Carnatic |
| vocal-pop | Western Pop |
| vocal-rap | Rap / Hip-Hop |
| vocal-sufi | Sufi |
| vocal-playback | Playback |

### 8.4 Genres (demo set)
Punjabi Folk · Indie Rock · Bollywood Pop · Sufi · Bollywood · Classical · Jazz · Electronic · Rock · Hindi Folk · Pop Vocals · Synthwave

---

## 9. Screens Inventory (by feature)

| Area | Key Screens |
|---|---|
| Auth/Onboarding | Splash (2.8s) · Welcome · Login/OTP · Role · Photo (1:1) · Genres · Languages · Instruments · Location · Portfolio · Success→Dashboard |
| Discover | DiscoverHomeScreen · DiscoverMainFlowContainer · SearchResults · RecommendedMatches · SavedProfiles · Connections · CategoryDirectory · SingerDirectory · FullTalentProfile |
| Profiles | Artist · Band · Business (×2) · Venue · FullTalent · ProfileManagement · ProfilePreview |
| Booking | StudioBooking (detail→calendar→slots→configure→payment→success) |
| Messaging | chat list · conversation · bubbles · typing · attachments |
| Reputation | rating breakdown · write review |
| Portfolio | grid · upload |
| Supporting/Dashboard | SupportingScreens · SuccessAndDashboardScreens |

---

## 10. Motion & Animation

- **Library:** `GsapComposeAnimations.kt` (GSAP-style Compose)
- **Spring:** DampingRatioMediumBouncy · Damping 0.8 / Stiffness 300
- **Keyframes:** `eqWave` (equalizer) · `pulseGlow` · `floatPhone` · `wfb` (waveform bars)
- **Durations:** 100 fast · 150 default · 200 medium · 300 slow · 500 entrance (ms)
- **Micro:** MicroCheckmarkSuccess · card-lift translateY(-4px) · like pop · chip spring

---

## 11. Architecture / Navigation / Data / AI

- **Pattern:** MVVM + Repository · StateFlow · `collectAsStateWithLifecycle`
- **DI:** none — manual `RepositoryProvider` (⚠️ double-init in Application + MainActivity)
- **Navigation:** `AppNavigationGraph.kt` + `Routes.kt` (typed routes)
- **Database:** Room `MusicLinkdDatabase` — entities: Onboarding · UserProfile · DiscoverFeed · Chat · Review · Notification · SearchHistory
- **Firebase:** Auth · Firestore · FCM (`AppFirebaseMessagingService`, `AppNotificationManager`) · Dynamic Links · Crashlytics · Analytics (`AnalyticsHelper`)
- **AI:** `GeminiRecommendationEngine` (match scoring) · `GeminiImageGenerator` (REST)
- **Models:** `DiscoverData` · `OnboardingData` · `PortfolioData` · `DemoDataProvider`

---

## 12. Accessibility & Android Standards

- `contentDescription` on icons · Compose semantics · 48dp touch targets · focus ring · TalkBack support · skeleton shimmer
- Jetpack Compose BOM · Material3 · Coil · Room · Firebase suite
- `backup_rules.xml` · `data_extraction_rules.xml` · ProGuard · Crashlytics mapping

---

## 13. Key Findings / Action Items

| # | Finding | Recommendation |
|---|---|---|
| 1 | Single god-ViewModel (`OnboardingViewModel`) | Split per feature |
| 2 | No DI framework; RepositoryProvider double-init | Introduce Hilt or fix init |
| 3 | Per-file constant drift (`Constants_0`, `Constants_37`) | Unify to `ui/theme` tokens |
| 4 | Doc drift in Architecture.md | Realign doc to code |
| 5 | Token parity with HTML DS is close (pink/navy match) | Use HTML tokens as canonical |

---

*End of audit. For DB sync, use tables in §7 (Personas) and §8 (Taxonomy) — each row has a stable `ID` primary key.*
