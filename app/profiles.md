# MUSICLINKD PROFILE ARCHITECTURE & TEMPLATES

This document specifies the core profile templates of the MusicLinkd platform and the modular subsystems engineered for each creative persona.

---

## 🏛️ Template 1: Talent Profile (Solo Artists & Session Pros)

The Talent Profile is built with a modular specifications architecture. A single layout renders dynamic content blocks depending on the user's micro-category or instrument.

### Shared Hero Structure (Editorial Profile Layout)
*   **Aesthetic**: Editorial-style layout with massive negative space, clean typography, and flat outlines.
*   **Hero Components**: Large User Avatar loaded from Unsplash or captured live via CameraX, pink verification check badge, location details, dynamic availability dot, rate labels, and follower statistics.
*   **Headshot Capture Subsystem**: Live camera shutter capture with runtime camera permission launcher (`TakePicturePreview`), automatically compressing and updating local `UserProfileRepository`.
*   **Navigation Bar**: Single-tap tab controls to transition smoothly between *About*, *Projects*, *Credits*, and *Reviews*.

### Category-Specific Modules
1.  **Singer / Vocalist Module**:
    *   Vocal Register tracking (e.g., Mezzo-Soprano, Soprano).
    *   Preferred performance languages and classical/pop genres.
    *   Microphone specifications (e.g., Neumann U87 Ai).
2.  **Instrumentalist / Session Musician Module**:
    *   Core instrument registry and touring setups (e.g., Fender Stratocaster, Kemper Profiler).
    *   Session availability indicators (e.g., Remote stem delivery, Tour booking).
3.  **Sound Engineer / Producer Module (Interactive A/B Mixing Deck)**:
    *   DAW preference stack (e.g., Pro Tools Ultimate, LUNA).
    *   Outboard gear hardware ledger (e.g., Tube-Tech CL 1B).
    *   **Interactive A/B Mastering Comparison Slider**: Allows prospective clients to toggle seamlessly between "Dry Raw Vocal Stem" (Channel A) and "SSL Analog Master" (Channel B) with real-time waveform updates and audio descriptions.
4.  **DJ / Live Performer Module**:
    *   Deck setup specification (e.g., Pioneer CDJ-3000 rig).
    *   Residency listing timeline and past festival logs.
5.  **Lyricist Module**:
    *   Italicized typography quotes displaying signature lyric cards.
    *   IPRS registry verification checks and writing languages.
6.  **Composer Module**:
    *   Filmography listings and original scores carousel.

---

## 🎸 Template 2: Band Profile

Designed for groups and music ensembles requiring logistics, member tracking, and rider coordination.

*   **Header Banner**: Wide-aspect band photo, logo, genres, location, and touring status.
*   **Ensemble Lineup Carousel**: Swipeable profile card carousel showing band members' avatars, names, and instruments, linking to their individual Talent Profiles.
*   **Technical Rider & Stage Plot**: Certified backline equipment demands (e.g., drum specifications, microphone layout, keyboard DIs). Houses a visual, container-based stage plot representing the physical rig locations on stage.
*   **Media Reel**: Live performance videos and past show history list.

---

## 🏢 Template 3: Business Profile

Designed for recording studios, record labels, academies, and equipment sellers.

*   **Identity Header**: High-contrast cover, business logo, verification badge, rating, and address.
*   **Recording Studio Module**:
    *   Certified equipment checklist (e.g., SSL Duality Console, Dolby Atmos Certification).
    *   High-fidelity photos of individual rooms (e.g., Live Room A, Vocal Booth B).
    *   **Interactive Availability Calendar**: Grid rendering booked and open slots to facilitate direct hourly bookings.
*   **Record Label Module**: Signed artist grid, showcasing active rosters and direct links to talent profiles.
*   **Music School Module**: Catalog of courses, syllabus outlines, durations, and tuition pricing.
*   **Equipment Dealer Module**: Product catalog grids detailing live gear inventories and purchase rates.

---

## 🎪 Template 4: Venue & Event Profile

Designed for music halls, clubs, amphitheaters, and festival organizers.

*   **Venue Header**: Representative hero imagery, venue name, address, and verification level.
*   **Capacity & Features Ledger**: Stands, standing capacity counts (e.g., 500 cap), and structural attributes (e.g., Acoustic tuning panels, loading bay dimensions).
*   **Scheduled Event Calendars**: Chronological lists of upcoming live festivals, supporting acts, and date stamps.
*   **Interactive Booking CTA**: Dedicated reservation form for tour managers looking to book open concert dates.
