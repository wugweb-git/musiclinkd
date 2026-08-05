# ADR 003: Centralized Relational Seeded Data Provider Architecture

## Status
Accepted

## Context
During development, testing, and offline preview states, UI screens (Discover, Profiles, Messaging, Marketplace, Studio Bookings) require consistent entity references so that an artist seen in the Discover grid appears with the exact same name, avatar, tracks, and reviews across all screens.

## Decision
We maintain a single, immutable, centralized data provider (`DemoDataProvider.kt`) as the single source of relational test data.

## Rationale
1. **Relational Integrity:** Rohan Sharma (`artist_1`) links to the exact same audio STEM tracks (`track_1`), studio reviews (`review_1`), and chat threads across the entire app.
2. **Zero Duplicate Seed Data:** Avoids fragmented inline mock lists across screen composables.
3. **Production Fallback:** When live Firestore queries return empty results or encounter network timeouts, `DemoDataProvider` seamlessly provides high-resolution realistic preview states.
