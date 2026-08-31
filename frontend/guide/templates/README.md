# Templates

Canonical layout families. A new page should start from one of these templates.



> **Reconciled 2026-08-31.** Rendered into `/guide` by
> `scripts/build-guide.mjs`, so this file is the source and cannot drift
> from the catalogue. Its interactive reference used to be
> `styleguide.html`, which now 301-redirects to `/guide`.

> The nine families below predate the flow model. `/guide` → Patterns is the
> live list — **24 flows** generated from `webapp/flows/`. These remain the
> layout archetypes those flows are built from.

## Template families

| Template | Purpose | Key sections |
|---|---|---|
| Splash | App launch | Centered logo on dark background, fade-in |
| Login | Authentication | Email/password, social auth, brand mark |
| Onboarding | Profile setup | Progress indicator, role selection, photo upload, genre/skills, availability |
| Home | Personalized dashboard | Greeting, quick stats, activity feed, recommendations, upcoming bookings |
| Discover | Card-driven discovery | Search bar, filter chips, card grid, skeleton loading, empty state |
| Artist Profile | Talent showcase | Hero, About, Audio, Projects, Credits, Reviews, Gallery, Services, sticky Hire CTA |
| Studio Profile | Business showcase | Identity header, equipment, rooms, calendar, reviews, Booking CTA |
| Marketplace | Gear & instrument listings | Category tabs, search, filter bar, listing grid, sorting |
| Settings | Account management | Profile, Account, Notifications, Privacy, Payments, Help, Log out |

## Rules

- Start every new screen from one of these template families.
- Reuse approved cards, components, and patterns from the design system.
- Do not create a wholly separate layout when a template family fits.
- Use `tokens/tokens.json` for all spacing, radius, color, and motion values.