# Comprehensive Navigation Architecture & Graph — MusicLinkd

## Navigation Graph Architecture

```
Splash (com.example.onboarding.ui.screens.WelcomeAndAuthScreens)
  │
  ├──► Welcome Screen
  │      │
  │      ├──► Auth Flow (Email / Google Sign-In)
  │      │      │
  │      │      └──► Role & Interests Onboarding
  │      │             │
  │      │             └──► Dashboard Root
  │      │
  └──► Dashboard Root (com.example.onboarding.ui.screens.SuccessAndDashboardScreens)
         │
         ├── Tab 1: Home / Discover
         │      ├── Universal Search TopBar
         │      ├── Networking Feed
         │      ├── AI Synergy Matchmaker
         │      └── Category Filters
         │
         ├── Tab 2: Discover & Marketplace
         │      ├── Artist Grid
         │      ├── Studio Listings
         │      ├── Track STEM Previews
         │      └── Gear Marketplace
         │
         ├── Tab 3: Supporting Modules
         │      ├── Connections Management
         │      ├── Networking Feed (Expanded)
         │      ├── Artist Profile Screen
         │      ├── QR Code Share
         │      ├── Saved Favorites
         │      ├── Studio Booking Modal
         │      └── Analytics
         │
         ├── Tab 4: Direct Messaging
         │      ├── Chat Threads List
         │      ├── Active Conversation Window
         │      └── Session Proposals
         │
         └── Tab 5: Profile & Settings
                ├── User Profile Editor
                ├── Portfolio Management
                ├── Audio STEM Upload
                └── Settings & Preferences
```

## Route Specifications & Deep Linking
- `https://app.musiclinkd.com/artist/{id}` -> Launches `ArtistProfileScreen`
- `https://app.musiclinkd.com/studio/{id}` -> Launches `StudioBookingScreen`
- `https://app.musiclinkd.com/connect/{id}` -> Launches `ConnectionRequestModal`
