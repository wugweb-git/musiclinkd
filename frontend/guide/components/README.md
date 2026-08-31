# Components

Approved components are shared primitives, controls, and composable blocks.
Create an implementation and a usage specification together.

> **Reconciled 2026-08-31.** This file predates the v9 system and several of
> its values had gone stale. Corrections are marked below; the rules that
> survived are unchanged. It is now rendered into `/guide` by
> `scripts/build-guide.mjs`, so this file is the source and cannot drift
> from the catalogue again.

## Implementation rules

- **Base container:** `--radius-3` (32px) corner, `--border-1` solid
  `--color-line`, `--color-paper` background.
  *Was "24dp radius, 1px #ECECF1" — #ECECF1 is exactly `--color-line`, but 24dp
  is not a step on the radius scale (8/16/32/pill), so it could never have been
  built from tokens.*
- **All buttons:** pill shape (`--radius-4`), minimum `--size-3` (48dp) in both
  axes.
  *Was "min 44px". 44pt is the Apple floor and is correct in
  `MusicLinkdTokens.swift`; the web and Compose floor is Material's 48dp.
  Controls smaller than that carry an invisible 48dp target — see `.ml-hit`.*
- **Icons:** two sets, never mixed inside one control. Gestalt (423, filled,
  16/24 grid) is the product set that screens inline at build time; Lucide
  (2006, 2px stroked) is the breadth set.
  *Was "Lucide only, 1.75px stroke" — Lucide was replaced wholesale in v9 and
  reinstated 2026-08-31 as the second set, at its native 2px stroke.*
- Maintain one canonical implementation per component.
- Do not create a visual variant when an existing component satisfies the need.

## Canonical component list

The ten below were the original core. The system now documents **133** sections
across `ml-core.css` and `ml-widgets.css`; `/guide` is the live list and this
table is kept as the historical minimum, not the ceiling.

| Component | Purpose |
|---|---|
| Button (primary, secondary, ghost) | CTAs, actions, passive triggers |
| Input Field | Form fields, onboarding, credentials, messaging |
| Chip | Genre/role/filter selectors (pill, toggle) |
| Search Bar | Directory search with category filters |
| Bottom Navigation | Master app tab switching |
| Audio Player / Waveform | Playable audio moments with animated waveform |
| Bottom Sheet | Modal overlay for confirmations, filters, forms |
| Empty State | Universal template for empty lists and no results |
| Verification Badge | Trust signal for verified artists and studios |
| Status Badge | Availability, confirmation, urgency indicators |
