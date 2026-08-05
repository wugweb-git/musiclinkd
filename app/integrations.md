# MusicLinkd 3rd Party & Backend Integrations

## 1. Integrations Overview

| Integration | Technology / Provider | Purpose | Configuration / Environment Key |
| :--- | :--- | :--- | :--- |
| **Authentication** | Firebase Auth | User signup, login, session token validation | Automatic via Firebase SDK |
| **Cloud Database** | Cloud Firestore | Connections, networking feed posts, user profiles | Collections: `connections`, `feed_posts`, `profiles` |
| **AI Intelligence** | Gemini 1.5 Flash API | AI matchmaker, collaborator synergy scoring, artwork gen | `GEMINI_API_KEY` in `.env` / Secrets |
| **Deep Linking** | Firebase Dynamic Links / App Links | Profile sharing & project stem invite deep links | Domain: `musiclinkd.page.link`, `app.musiclinkd.com` |
| **Cloud Messaging** | Firebase Cloud Messaging (FCM) | Collaboration requests & session booking push alerts | Service: `AppFirebaseMessagingService` |
| **Audio Engine** | Android Media3 / ExoPlayer | STEM playback, waveform preview, persistent audio player | Local Audio Manager |
| **Local Persistence**| Room Database (SQLite) | Offline search history, cached chat threads, feed cache | `MusicLinkdDatabase` |

## 2. Environment Variables (`.env.example`)

```env
# GEMINI_API_KEY: Required for Gemini AI recommendation & image generation services.
# Configured via AI Studio Secrets panel.
GEMINI_API_KEY=your_gemini_api_key_here

# FIREBASE_PROJECT_ID: Firebase project identifier for Firestore & FCM
FIREBASE_PROJECT_ID=musiclinkd-prod

# DEEP_LINK_HOST: Domain host for shareable dynamic links
DEEP_LINK_HOST=app.musiclinkd.com
```

## 3. Deep Link & Mobile Browser Support
MusicLinkd supports universal URL routing across desktop and mobile web browsers at:
- `https://app.musiclinkd.com`
- `https://musiclinkd.com`
- `https://musiclinkd.page.link`
