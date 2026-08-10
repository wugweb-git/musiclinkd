# Screens Map

**Audited:** August 2026, against `app/app/src/main/java/com/example/` (137 `.kt`
files read in full). This is a dated snapshot, not a live source — nothing
keeps it in sync if the Android app changes afterward (it's built and
published separately via AI Studio, not from this repo). Re-verify against
the actual source before relying on specifics here for anything load-bearing.

## Headline finding

The seven top-level packages under `com.example` (`auth`, `components`,
`data`, `features`, `navigation`, `onboarding`, `ui`) are **not equally
real**. Verified by grepping for call sites of every public symbol:

- `com/example/auth/FirebaseAuthManager.kt` — dead code, zero references.
- `com/example/components/IconLibrary.kt` — dead code, zero references.
- `com/example/data/` (`models/Artist.kt`, `Venue.kt`, `Event.kt`,
  `repository/FirestoreRepository.kt`) — dead code, a self-contained CRUD
  layer for `artists`/`venues`/`events` that nothing calls.
- `com/example/navigation/Routes.kt` — dead code, never referenced.
- `com/example/features/{auth,discover,marketplace,messaging,profile,studios}/*Feature.kt`
  — dead code. Each is a thin pass-through wrapper around a real
  `onboarding/ui/screens` composable, but no wrapper is ever called.
- `com/example/features/navigation/AppNavigationGraph.kt` — the one live
  part of `features/` (rendered by `MainActivity`), but see "Navigation
  graph" below: most of its routes are onboarding-only, three are dead.

**Practical implication:** almost the entire real, running app lives under
`com/example/onboarding/` (screens in `onboarding/ui/screens/`, reusable
pieces in `onboarding/ui/components/`, state in `onboarding/viewmodel/` and
`onboarding/models/`), plus `com/example/ui/theme/` for styling. Treat the
other packages as orphaned scaffolding, not the basis for anything.

## Screen inventory

No single flat "screen" abstraction exists — three different mechanisms
render screens (see "Navigation graph"). Grouped by the sub-system that
actually renders them:

### A. Onboarding / auth flow
Driven by `OnboardingViewModel` + `OnboardingScreen` enum
(`onboarding/models/OnboardingData.kt:8`). All in
`onboarding/ui/screens/{WelcomeAndAuthScreens,RoleAndInterestsScreens,LocationAndProfileScreens}.kt`:

| Screen | File | Purpose |
|---|---|---|
| SplashScreen | WelcomeAndAuthScreens.kt:1181 | Loading splash, auto-dismisses |
| WelcomeScreen | WelcomeAndAuthScreens.kt:93 | Landing — Google/Mobile/Email sign-in |
| EmailAuthScreen | WelcomeAndAuthScreens.kt:453 | Email + password form |
| MobileAuthScreen | WelcomeAndAuthScreens.kt:718 | Phone number entry |
| OtpVerificationScreen | WelcomeAndAuthScreens.kt:910 | 6-digit OTP entry |
| PrimaryRoleScreen | RoleAndInterestsScreens.kt:38 | Grid of 17 role cards (searchable) |
| SecondaryRoleScreen | RoleAndInterestsScreens.kt:265 | Optional second role, skippable |
| LanguagesScreen | RoleAndInterestsScreens.kt:554 | Multi-select language chips |
| GenresScreen | RoleAndInterestsScreens.kt:650 | Multi-select genre chips, capped at 3 |
| InstrumentsScreen | RoleAndInterestsScreens.kt:746 | Instrument + proficiency (role-gated) |
| LocationScreen | LocationAndProfileScreens.kt:51 | Country/state/city/area |
| ProfileCreationScreen | LocationAndProfileScreens.kt:252 | Name, bio, avatar, links |
| SuccessOnboardingScreen | SuccessAndDashboardScreens.kt:75 | Completion screen |

Only `InstrumentsScreen` is role-conditional (gated by
`canRolePlayInstruments`); everything else runs uniformly for all roles.

### B. Post-onboarding hub — `DashboardPreviewScreen`
`SuccessAndDashboardScreens.kt:1349`. A `Scaffold` with local `activeTab:
String` state (not NavHost-driven). Bottom nav exposes 5 tabs: **Home,
Discover, Connections, Marketplace, Profile**. Reached via top-bar
icons/shortcut chips: StudioSpaces, Search, QRShare, Saved, Settings, Help,
Reputation, Notifications, Portfolio.

| Tab / destination | Composable | File |
|---|---|---|
| Home | inline | SuccessAndDashboardScreens.kt |
| Profile | ProfileManagementMainFlowContainer | ProfileManagementScreens.kt:102 |
| Portfolio | PortfolioMainFlowContainer | PortfolioScreens.kt:54 |
| StudioSpaces | StudioBookingMainFlowContainer | StudioBookingScreens.kt:106 |
| Discover | DiscoverMainFlowContainer | DiscoverMainFlowContainer.kt:57 |
| Marketplace | MarketplaceBetaScreen | SuccessAndDashboardScreens.kt:2647 |
| Connections | ConnectionsFlow | ConnectionsScreen.kt:67 |
| Search | UniversalSearchFlow | SupportingScreens.kt:280 |
| QRShare | QrAndShareFlow | SupportingScreens.kt:653 |
| Saved | SavedAndFavoritesFlow | SupportingScreens.kt:1021 |
| Settings | SettingsFlow | SupportingScreens.kt:1474 |
| Help | HelpAndSupportFlow | SupportingScreens.kt:2133 |
| Reputation | ReputationAndReviewsScreenContainer | ReputationAndReviewsScreens.kt:76 |
| Notifications | NotificationsCenterScreen (+ NotificationSettingsScreen) | SuccessAndDashboardScreens.kt:3136, 3687 |

Two more `SupportingScreensHub` states reachable only from within Discover:
`NETWORKING_FEED` (Firestore-backed, see Service_Flow.md) and
`ARTIST_PROFILE` (`onboarding/ui/components/ArtistProfileScreen.kt:51`).

### C. Discover sub-screens
`DiscoverScreen` enum (`onboarding/models/DiscoverData.kt:5`), rendered
inside `DiscoverMainFlowContainer.kt`: HOME, CATEGORY_DIRECTORY,
SEARCH_RESULTS, SINGER_DIRECTORY, FULL_PROFILE, BAND_PROFILE,
BUSINESS_PROFILE, VENUE_PROFILE, SAVED_PROFILES, RECOMMENDED_MATCHES — 10
screens, each its own file (`*.kt:57`).

### D. Studio Booking sub-screens
`StudioBookingScreen` enum (StudioBookingScreens.kt:47), 11 states:
DISCOVERY, SEARCH_RESULTS, STUDIO_PROFILE, EQUIPMENT_DETAILS,
CALENDAR_SLOTS, CONFIGURE_SESSION, BOOKING_SUMMARY, PAYMENT_GATEWAY,
CONFIRMATION_SUCCESS, MY_BOOKINGS, OWNER_DASHBOARD. Only 5 were confirmed as
independently named composables (StudioDiscoveryScreen,
StudioSearchResultsScreen, StudioProfileScreen,
StudioEquipmentDetailsScreen, StudioAvailabilityCalendarScreen) — the rest
are handled inline in `StudioBookingMainFlowContainer`'s `when` block.

### E. Portfolio (project publishing) sub-screens
`PortfolioScreen` enum (`onboarding/models/PortfolioData.kt:8`), 11 states:
PORTFOLIO_DASHBOARD, CHOOSE_PROJECT_TYPE, PROJECT_INFORMATION,
ADD_STREAMING_LINKS, SELECT_YOUR_ROLE, ADD_COLLABORATORS,
CREDIT_ATTRIBUTION, PROJECT_MEDIA_PREVIEW, PUBLISH_PROJECT, SUCCESS_STATE,
PROFILE_PROJECT_VIEW — in a single 2683-line `PortfolioScreens.kt`.

### F. Profile Management sub-screens
`ProfileSubScreen` enum (ProfileManagementScreens.kt:64), 15 states:
DASHBOARD, EDIT_PROFILE, ROLE_MANAGEMENT, AVAILABILITY,
PROJECT_MANAGEMENT, SERVICES_PRICING, VERIFICATION, ANALYTICS, PRIVACY,
NOTIFICATIONS, SUBSCRIPTION, BUSINESS_PROFILE, PROFILE_SHARE,
PUBLIC_PREVIEW, BUSINESS_STOREFRONT_FLOW_11. `BUSINESS_PROFILE` routes to
`BusinessProfileWorkspace` (BusinessProfileScreens.kt:110, 1858 lines, its
own Overview/Services/Equipment sub-tabs) — the studio/label/business
variant of the profile screen.

### G. Reputation & Reviews sub-screens
`ReputationSubScreen` enum (ReputationAndReviewsScreens.kt:44), 11 states:
DASHBOARD, REVIEW_LIST, REVIEW_DETAIL, WRITE_REVIEW, RATING_BREAKDOWN,
COMPLETED_PROJECT_REVIEW, MARKETPLACE_REVIEW, STUDIO_REVIEW,
BUSINESS_RESPONSE, REPORT_REVIEW, PUBLIC_REPUTATION.

### H. Messaging sub-screens — currently unreachable
`ChatContext` enum (MessagingScreens.kt:54) → `Flow5MessagingWorkspace` →
`MessagesHomeScreen` → `ChatScreen` (2179-line file total, includes an AI
assist tray and message bubbles). Fully built, but not reachable from any
live navigation path — see "Navigation graph."

## Navigation graph — actual structure

Three independent, non-interoperating mechanisms:

**(a) `com.example.navigation.Routes`** — sealed class of route strings.
Dead: zero references anywhere.

**(b) `com.example.features.navigation.AppNavigationGraph`** — the real
`NavHost`, rendered by `MainActivity.kt:137`. Declares 16 routes: `splash`,
`welcome`, `email_auth`, `mobile_auth`, `otp_verify`, `role_primary`,
`role_secondary`, `languages`, `genres`, `instruments`, `location`,
`profile_creation`, `success`, `dashboard`, `artist/{artistId}`,
`studio/{studioId}`, `messages/{threadId}`.

- `startDestination` is recomputed each recomposition from
  `OnboardingViewModel.state.currentScreen` — screen movement in onboarding
  is driven by ViewModel state, not `navController.navigate()`.
- **There is no call to `navController.navigate(...)` anywhere in the
  codebase.** `popBackStack()` is used in exactly 3 places, all "back"
  buttons for the routes below.
- Consequence: `artist/{artistId}`, `studio/{studioId}`, `messages/{threadId}`
  are declared but **unreachable** — nothing ever navigates to them.
- `dashboard` renders `DashboardPreviewScreen`, where mechanism (c) takes
  over.

**(c) Local `activeTab: String` state inside `DashboardPreviewScreen`** —
the real, live post-onboarding navigation system. A manual `when(activeTab)`
switch, no NavHost, no back stack, no deep-link support. Repeated at every
nested level (Discover, Portfolio, Studio Booking, Profile Management,
Reputation, Supporting Hub each have their own internal enum + local switch,
same pattern).

**Deep links:** `AndroidManifest.xml` declares `autoVerify` intent-filters
for `musiclinkd.page.link` / `musiclinkd.com` / `app.musiclinkd.com` /
`musiclinkd.app`. `onboarding/util/DynamicLinkManager.kt` *constructs*
shareable links for profiles/projects — but **`MainActivity.onCreate` never
reads `intent.data`**, so there is no inbound deep-link handling at all.
Outbound-only.

**Analytics:** `AppNavigationGraph.kt:71-85` tracks screen views via
back-stack changes — this stops working once the user reaches `dashboard`,
since real navigation from there on uses mechanism (c), which the NavHost
never sees.

## Bottom line

There is no single authoritative route table. The onboarding flow (13
screens) has real string routes. Everything past onboarding — the bulk of
the app, 60+ screens — has **no route/URL identity at all**; it's addressed
only by nested enum/string tab state, not deep-linkable or externally
referenceable without new plumbing.
