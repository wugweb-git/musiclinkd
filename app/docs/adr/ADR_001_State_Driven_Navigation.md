# ADR 001: Sealed State-Driven Single-Activity Navigation Architecture

## Status
Accepted

## Context
MusicLinkd requires seamless tab switching, instant deep-link routing (`app.musiclinkd.com/artist/{id}`), modal sheet overlays, and reliable backstack restoration without route-parser serialization overhead or screen flicker.

## Decision
We utilize a single `MainActivity` root managing primary top-level tabs via sealed navigation states (`AppTabScreen`) coupled with explicit state hoisting (`currentTab`, `selectedArtistId`, `selectedStudioId`).

## Rationale
1. **Zero Route Serialization Overhead:** Complex domain models (`DemoArtist`, `DemoStudio`) are passed directly via memory or lookup IDs from `DemoDataProvider`.
2. **Deterministic Backstack Handling:** Back button presses decrement state layers gracefully (Modal -> Detail Screen -> Main Tab -> Exit confirmation).
3. **Deep Link Handling:** Android App Links (`app.musiclinkd.com`) populate target state IDs (`selectedArtistId`) directly into the ViewModel upon activity intent handling.

## Consequences
- Clean, fast UI response across low-end and high-end Android devices.
- High component reusability across `app.musiclinkd.com`.
