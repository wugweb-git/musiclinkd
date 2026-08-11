# Personas, User Journeys, Navigation & Platform Modules

Complete operational blueprint mapping who MusicLinkd is built for, their step-by-step user journeys, navigation architecture, and platform modules.

---

## 1. Who, What, When, Where, Why & How

- **WHO**: 13 Verified Music Industry Roles (Vocalists, Instrumentalists, Beatmakers, Composers, Mix Engineers, Mastering Engineers, Podcasters, Voice Artists, Bands, Music Educators, Rental Providers, Record Labels, Managers).
- **WHAT**: India's unified professional music ecosystem for discovery, collaboration, studio booking, gear marketplace, and verified credits.
- **WHEN**: Available 24/7 with real-time studio booking availability and instant FCM push notifications.
- **WHERE**: Native Android App (`app.musiclinkd.com`), Public Portal (`musiclinkd.com`), Admin Platform (`admin.musiclinkd.com`).
- **WHY**: Resolves scattered music workflows across WhatsApp, Instagram, Google Drive, and cash transactions with one verified professional identity.
- **HOW**: Built on Native Android Jetpack Compose, Room DB, Firebase Auth/Firestore, and Gemini 2.5 Flash AI.

---

## 2. All 13 Persona Types

| Persona Role | Primary Goal | Core Feature Focus |
| :--- | :--- | :--- |
| **Vocalist** | Get hired for playback, session vocals, and singles | Audio Portfolio, Waveform Previews, Verified Credits |
| **Instrumentalist** | Showcase session skills (Sitar, Tabla, Drums, Bass) | Multi-Genre Tags, Availability Calendar |
| **Beatmaker** | License stems and beat leases | Marketplace Module, Royalty Splits |
| **Composer** | Find vocalists and mix engineers for OSTs | Brief Posting, AI Matchmaker |
| **Mix Engineer** | Build reputation and secure mixing gigs | Credits Ledger, Verified Reviews |
| **Mastering Engineer** | Deliver lossless mastering stems | High-Res File Dropzone, Escrow |
| **Podcaster** | Hire voice artists and book podcast rooms | Studio Booking, Hourly Reservation |
| **Voice Artist** | Land commercial dubbing and VO gigs | Sample Player, Instant Booking |
| **Band** | Find gig venues and rental sound gear | Venue Directory, Gear Marketplace |
| **Music Teacher** | Connect students to studios and session pros | Education Hub, Studio Network |
| **Rental Provider** | Rent out sound gear, mics, and monitors | Gear Listing, Escrow Deposit |
| **Record Label** | Post briefs and hire talent side-by-side | Business Portal, Bulk Hiring |
| **Manager** | Oversee artist calendars, bookings, and income | Management Dashboard, Analytics |

---

## 3. User Journeys (Step-by-Step)

### A. Artist Journey (From Unknown to In-Demand)
1. **Create Profile**: Select primary role, avatar photo, location, and genres.
2. **Upload Stems**: Add audio preview tracks and videos to portfolio.
3. **Get Discovered**: Appear in search results and AI synergy recommendations.
4. **Connect**: Send connection requests and build professional network.
5. **Collaborate**: Work with verified pros on real releases.
6. **Book Studio**: Reserve recording rooms at verified hourly rates.
7. **Receive Reviews**: Earn multi-party verified reviews.
8. **Grow Career**: Leverage verified discography to land major projects.

### B. Studio Journey (From Empty Rooms to Full Calendar)
1. **Register Studio**: Add room specs, equipment list, pricing, and photos.
2. **Get Verified**: Complete business verification for the Business Badge.
3. **Sync Calendar**: Connect real-time availability.
4. **Receive Bookings**: Get instant push notifications on new reservations.
5. **Collect Reviews**: Earn verified reviews from clients.

---

## 4. Navigation Architecture & Rules

- **Max Tap Depth**: Maximum **3 taps** to reach any key feature from the home screen.
- **Deep Linking Intents**:
  - `musiclinkd://artist/{artist_id}`
  - `musiclinkd://studio/{studio_id}`
  - `musiclinkd://booking/{booking_id}`
  - `musiclinkd://marketplace/{item_id}`
- **Modal vs Sheet Rules**:
  - *Primary Workflows* (Booking, Filters, Search) surface in **360dp Bottom Sheets**.
  - *Secondary Dialogs* (Confirmations, Alerts) use centered Modal Dialogs.
