# MusicLinkd Architecture & Engineering Governance

> **Repository Baseline:** `Release Candidate (v1.5.0-RC)` — Frozen production baseline. All ongoing changes across `musiclinkd.com`, `app.musiclinkd.com`, and `admin.musiclinkd.com` must strictly maintain this governance framework.

## 1. Product Distinction & Ecosystem Topology
MusicLinkd consists of three distinct product environments and a centralized version control repository:
- **Public Website / Portal:** `musiclinkd.com`
- **Mobile Web & Application Platform:** `app.musiclinkd.com`
- **Admin & Enterprise Management Portal:** `admin.musiclinkd.com`
- **GitHub Repository:** `https://github.com/wugweb-git/musiclinkd`

All code, documentation, deep links, and API targets must respect this explicit product separation.

## 2. Core Principles & Philosophy
- **Component Recycling Over Recreation:**
  - Never duplicate card or input implementations. Always extend state parameters on base unified components (`UnifiedComponents.kt`).
  - Base cards MUST strictly maintain the **24dp corner radius** (`RoundedCornerShape(24.dp)`), **1dp border stroke** (`BrandBorder`), and **high-contrast white canvas** styling.
- **Single Source of Truth for Design System:**
  - Standardized color tokens are declared in `com.example.ui.theme.Color`:
    - `BrandPrimary` (`#EA4C89`)
    - `BrandDark` (`#0D0B26`)
    - `BrandBackground` (`#FCFCFD`)
    - `BrandBorder` (`#ECECF1`)
    - `BrandTextSecondary` (`#7D7D8E`)
    - `BrandSuccess` (`#10B981`)
- **State-Driven Modular Screens:**
  - All interactive components accept explicit parameters (`isLoading`, `isEnabled`, `status`, `isSelected`, `onClick`) and callbacks.
  - Asynchronous Firestore and Firebase operations handle offline gracefully with deterministic fallback mock state.

## 2. Directory Structure & Modular Layout
```
app/src/main/java/com/example/
├── MainActivity.kt                      # Main Activity launcher & Edge-to-Edge root
├── ui/
│   ├── theme/                           # Centralized M3 Theme, Color, Type
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
└── onboarding/
    ├── ai/                              # Gemini Recommendation Engine
    ├── api/                             # Gemini Image Generator & REST client
    ├── audio/                           # Audio Player Manager (ExoPlayer wrapper)
    ├── database/                        # Room Database & Firebase Manager
    ├── messaging/                       # Firebase Cloud Messaging Service
    ├── models/                          # Data Models & Sealed UI States
    ├── ui/
    │   ├── components/                  # Consolidated UI Component Library
    │   │   ├── UnifiedComponents.kt
    │   │   ├── EmptyState.kt
    │   │   └── OnboardingUIComponents.kt
    │   └── screens/                     # Domain Feature Screens
    │       ├── BusinessProfileScreens.kt
    │       ├── ConnectionsScreen.kt
    │       ├── DiscoverScreens.kt
    │       ├── LocationAndProfileScreens.kt
    │       ├── MessagingScreens.kt
    │       ├── PortfolioScreens.kt
    │       ├── ProfileManagementScreens.kt
    │       ├── ReputationAndReviewsScreens.kt
    │       ├── RoleAndInterestsScreens.kt
    │       ├── StudioBookingScreens.kt
    │       ├── SuccessAndDashboardScreens.kt
    │       ├── SupportingScreens.kt
    │       └── WelcomeAndAuthScreens.kt
    ├── util/                            # Application Strings, Error Reporting & Dynamic Links
    └── viewmodel/                       # ViewModels and StateFlow State Containers
```

## 3. Error Handling & Edge Case Guidelines
- **Graceful Fallbacks:** Every image loading block with `AsyncImage` MUST supply an icon fallback if loading fails or URL is null.
- **Offline Resiliency:** Firestore operations fallback to local memory state when network is unavailable.
- **Form Input Validation:** Input fields MUST sanitize string lengths and validate regex formats (email, URL, phone numbers) before emitting state changes.

## 4. Component Recycling Mandate
- Components in `UnifiedComponents.kt` MUST NOT be duplicated across screen files.
- Buttons, chips, cards, headers, status badges, and top-bar search controls MUST be called directly from `UnifiedComponents.kt`.

## 5. Mandatory Engineering & Documentation Rule (Definition of Done)
"No implementation, bug fix, refactor, feature, UI update or documentation update is complete until update.md, CHANGELOG.md and all relevant documentation have been updated."

Every completed task MUST update:
- `update.md`
- `CHANGELOG.md`
- relevant technical documentation
- navigation documentation
- API documentation
- module documentation
- integration documentation
- screen documentation
- architecture documentation

**Documentation is strictly part of the Definition of Done.** No pull request or feature task is complete without corresponding documentation synchronization.

