# MusicLinkd — Hardcoded Content Refactor

## Scope

This refactor separates three different classes of content:

1. **UI copy** — labels, headings, button text, placeholders, accessibility text → Android `strings.xml`.
2. **Seed/demo content** — artists, studios, tracks, feed posts, search results, categories → `DemoDataProvider` / dedicated seed providers.
3. **Runtime content** — profile names, roles, locations, counts, messages, notifications, bookings, etc. → application state/repository/database; never fallback to invented user data in a production screen.

## Already centralized

- Shared navigation labels and accessibility strings are now in `app/app/src/main/res/values/strings.xml`.
- Bottom-navigation badge default moved out of the UI component into `UiDemoDefaults.kt`.
- Existing `DemoDataProvider` remains the intended home for seeded/demo entities and relationships.

## Identified hardcoded-content hotspots

### P0 — remove from production composition

- `SuccessAndDashboardScreens.kt`
  - Contains `SuccessOnboardingScreen_OLD_UNUSED` with a large independent editorial/demo experience.
  - Contains named people, subscriber counts, editorial posts, comments, simulated audio state, and fallback profile identity.
  - This should be deleted from the production composition, not migrated as runtime data.

### P1 — move to seed/content providers

- `DiscoverData.kt`
  - `defaultCategories`
  - `sampleDiscoverProfiles`
  - embedded projects, credits, reviews, members, locations, pricing, bios and image URLs.
- `SupportingScreens.kt`
  - recent searches
  - trending searches
  - `mockAllResults`
  - suggested categories
  - supporting-flow demo entities
- Other feature screens containing literal demo entities should consume `DemoDataProvider` or a feature-specific seed provider.

### P1 — move UI copy to resources

Known examples include:

- `DiscoverHomeScreen.kt`
- `SupportingScreens.kt`
- `SuccessAndDashboardScreens.kt`
- `MessagingScreens.kt`
- `PortfolioScreens.kt`
- `ProfileManagementScreens.kt`
- `ReputationAndReviewsScreens.kt`
- `StudioBookingScreens.kt`
- `BandProfileScreen.kt`
- `BusinessProfileScreens.kt`
- `FullTalentProfileScreen.kt`
- reusable component files under `ui/components/`

## Rule for future code

Do not add user-visible literals directly inside Compose UI:

```kotlin
Text("Discover")
Text("Search artists...")
placeholder = { Text("Search...") }
contentDescription = "Back"
```

Use resources:

```kotlin
Text(stringResource(R.string.nav_discover))
Text(stringResource(R.string.search_artists_studios_instruments))
contentDescription = stringResource(R.string.action_back)
```

Do not move real runtime values into resources. Runtime values belong to models/state/repositories.

## Current branch status

This branch establishes the resource layer and centralization boundary. The remaining hotspots above require source-level migration and should be completed before merging into `main`.
