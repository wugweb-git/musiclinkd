# MUSICLINKD USER FLOWS & DATABASE ARCHITECTURE

This document establishes all supported user journeys, interactive states, local database structures (Room), and background responder simulators.

---

## 🧭 1. CORE USER JOURNEYS

### Onboarding & Sign-Up Flow
-   **Identity Split**: On entry, users select their primary purpose: **"Publish My Talent Portfolio"** (Talent) or **"Discover & Hire Talent"** (Client).
-   **Mapped User Journeys by Professional Role**:
    1.  **Musician / Vocalist Journey**:
        - Step 1: Select Primary Instrument & Vocal Range (e.g. Lead Singer, Session Guitarist, Tabla Virtuoso).
        - Step 2: Choose Genre Specializations (e.g., Bollywood, Classical, Pop, Rock).
        - Step 3: Media & Showreel Upload (Vocal stems, track samples, live performance clips).
        - Step 4: Capture Professional Headshot (Camera headshot or photo picker).
        - Step 5: Configure Performance Rates (Daily gig rates, session hourly rates).
    2.  **Music Producer Journey**:
        - Step 1: DAW Suite Stack (Logic Pro, Ableton Live, Pro Tools, FL Studio).
        - Step 2: Beatmaking & Genre Focus (EDM, Hip-Hop, Cinematic Score, Fusion).
        - Step 3: Discography & Published Track Links (Spotify, SoundCloud, Apple Music).
        - Step 4: Capture Studio / Headshot Photo.
        - Step 5: Define Project Rates & Stems Delivery Terms (Per track / per album pricing).
    3.  **Sound / Mix / Master Engineer Journey**:
        - Step 1: Core Engineering Specialization (Tracking, Mixing, Analog Mastering, Spatial Audio).
        - Step 2: Outboard Gear & Acoustics Ledger (SSL, Neve, Universal Audio, Neumann).
        - Step 3: A/B Portfolio Comparison Tracks (Raw Multitrack vs Analog Mastered Audio).
        - Step 4: Capture Studio / Headshot Photo.
        - Step 5: Set Studio Hourly & Mix Revision Rates.
    4.  **Recording Studio Journey**:
        - Step 1: Live Room & Booth Specs (Dimensions, Treatment, Mic Locker).
        - Step 2: Console & Hardware Rack Listing.
        - Step 3: Interactive Slots & Engineer Availability Calendar.
        - Step 4: Capture High-Res Room Photos & Location Pins.
    5.  **Venue & Event Organizer Journey**:
        - Step 1: Audience Standing / Seating Capacity & Stage Dimensions.
        - Step 2: Backline Equipment & PA System Specs.
        - Step 3: Event Calendar & Booking Terms.
        - Step 4: Capture Venue Photos & Address Pin.

-   **Client Flow**: Need Definition → Music Genre selection → Budget Bracket selection → Match Recommendations (Interactive match deck).

### Direct Messaging & Negotiation Flow
-   **Chat Entry**: Clients trigger a chat from a Talent, Studio, or Gear profile.
-   **Secure Booking Offer Creation**: Inside chat, clients can press `"Create Contract Offer"`, opening a specialized form (Select service, rate, booking amount, milestones, timeline).
-   **Active State Negotiation**: The offer is inserted as an interactive custom card in the chat thread. The recipient sees `"Accept & Pay Deposit"` and `"Decline"` controls.
-   **Verification Logs**: Clicking Accept or Decline triggers immediate, system-verified message posts in the chat log (e.g., `"Contract Approved — Secure Deposit of ₹25,000 Verified"`).

---

## 🗄️ 2. LOCAL DATABASE INTEGRATION (ROOM)

MusicLinkd runs a robust, write-through, stream-based offline-first database. All data mutations update local tables and push updates reactively to the ViewModels via Kotlin Flows.

### Master Database Structure (`MusicLinkdDatabase`)
*   **OnboardingEntity**: Stores local user setup states (mobile number, selected roles, genres, rate tiers, avatar index, profile status).
*   **ConvoEntity**: Conversation headers (UUID `convoId`, contact name, last message snippet, timestamp, unread count, active context like `STUDIO`, and contract offer status).
*   **MessageEntity**: Message bubbles (UUID `messageId`, parent `convoId`, sender name, body text, timestamp, checkmark read states, and custom attachment flags like `CONTRACT_OFFER`).
*   **ReviewEntity**: Customer review records (reviewer name, rating stars, review text, date, upvote counts).

---

## 🤖 3. SMART BACKGROUND RESPONDERS

To simulate live, engaging interactions without a network server, MusicLinkd includes intelligent, context-aware automated responders within the `ChatRepository` layer:

-   **Realistic Typing Delays**: Simulates physical typing behavior by pausing for 1.5 to 3 seconds before inserting a response.
-   **Contextual Dialogue Scripts**: Responses are generated based on the conversation's active topic context:
    -   `STUDIO`: Auto-responds regarding hardware consoles, mic availability, acoustic treatments, and engineering support.
    -   `MARKETPLACE`: Auto-responds about audio condition, shipping, local trial setups, and warranty periods.
    -   `PROJECT`: Auto-responds about vocal tracking, mix timelines, and mastering revisions.
-   **System Push Alerts**: When a smart responder fires, a local system alert is immediately dispatched using Android `NotificationManager` over the `musiclinkd_chat` channel, notifying users of new replies even if the app is minimized.
