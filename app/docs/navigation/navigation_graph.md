# Navigation Architecture & Deep Links — MusicLinkd

## 🌐 Products Navigation Targets
- **Public Portal:** `https://musiclinkd.com`
- **Application App Links:** `https://app.musiclinkd.com/artist/{id}`, `https://app.musiclinkd.com/studio/{id}`
- **Admin Portal:** `https://admin.musiclinkd.com`

---

## 🧭 Jetpack Navigation Compose Sealed Route Hierarchy (`Routes.kt`)

Implemented in `com.example.navigation.Routes` and `com.example.features.navigation.AppNavigationGraph`.

| Route | Composable Destination | Description |
| :--- | :--- | :--- |
| `splash` | `SplashScreen` | Animated brand splash screen with auto-dismiss transition. |
| `welcome` | `WelcomeScreen` | Main authentication entry point (Google, Mobile, Email). |
| `email_auth` | `EmailAuthScreen` | Firebase Email & Password authentication gateway. |
| `mobile_auth` | `MobileAuthScreen` | Mobile number entry with auto-formatting. |
| `otp_verify` | `OtpVerificationScreen` | 6-digit OTP verification with countdown timer. |
| `role_primary` | `PrimaryRoleScreen` | Primary musician/producer role selection screen. |
| `role_secondary` | `SecondaryRoleScreen` | Secondary role selection screen. |
| `languages` | `LanguagesScreen` | Vocal/lyrical language preference selection. |
| `genres` | `GenresScreen` | Musical genre preference selection. |
| `instruments` | `InstrumentsScreen` | Instrument proficiency setup screen. |
| `location` | `LocationScreen` | Geographic studio & artist discovery location setup. |
| `profile_creation` | `ProfileCreationScreen` | Full name, handle, bio, and avatar customization. |
| `success` | `SuccessOnboardingScreen` | Verified Artist Passport summary screen. |
| `dashboard` | `DashboardPreviewScreen` | Main 5-tab application root with global persistent player. |
| `artist/{artistId}` | `PortfolioScreens` | Detailed artist portfolio, audio STEM previews, and synergy score. |
| `studio/{studioId}` | `StudioBookingScreens` | Recording studio suite details and hourly slot reservation sheet. |
| `messages/{threadId}` | `MessagingScreens` | Direct messaging thread with real-time audio attachment support. |

---

## 📐 Navigation Flow Graph

```
Splash (`splash`) 
   │
   ▼
Welcome (`welcome`) ──┬── Email Auth (`email_auth`)
                      ├── Mobile Auth (`mobile_auth`) ──► OTP Verify (`otp_verify`)
                      └── Google Auth Direct
                           │
                           ▼
                  Primary Role (`role_primary`)
                           │
                           ▼
                  Secondary Role (`role_secondary`)
                           │
                           ▼
                  Languages (`languages`)
                           │
                           ▼
                  Genres (`genres`)
                           │
                           ▼
                  Instruments (`instruments`)
                           │
                           ▼
                  Location (`location`)
                           │
                           ▼
                  Profile Creation (`profile_creation`)
                           │
                           ▼
                  Success Passport (`success`)
                           │
                           ▼
                  Dashboard Root (`dashboard`)
                  ├── Tab 0: Discover & Synergy Matchmaker
                  ├── Tab 1: Discovery Grid & Marketplace (`gear`, `stems`)
                  ├── Tab 2: Connections & Social Streams
                  ├── Tab 3: Direct Messaging (`messages/{threadId}`)
                  └── Tab 4: Profile & Studio Reservations (`studio/{studioId}`)
```

