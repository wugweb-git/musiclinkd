# MusicLinkd Design System

This directory is the single source of truth for MusicLinkd's UI and brand decisions.

**Interactive reference:** Open `guide/styleguide.html` in any browser — no build step, no dependencies beyond Google Fonts.

## Product expression

- Premium, editorial, airy, audio-first, people-first marketplace.
- Prioritise discovery, hiring, booking, collaboration, buying, and renting over generic social engagement.
- Use realistic human photography for people-facing content. Do not use initials, emoji, cartoon avatars, invented statistics, or unapproved product features as placeholders.
- Icon family: **Gestalt icons** (Pinterest's open-source set, `icons/`, Apache-2.0) are canonical for all new work, replacing the earlier Lucide-only rule. Existing Lucide icons already placed across shipped screens in `styleguide.html`/`merge3.html` have not been swapped in this pass (tracked separately — see `icons/README.md`); do not mix families within a single screen while that migration is pending.
- Reuse approved components and templates; do not create a visual variant when an existing component satisfies the need.
- Component and visual language are informed by [Gestalt](https://gestalt.pinterest.systems/) (Pinterest's design system, [github.com/pinterest/gestalt](https://github.com/pinterest/gestalt), Apache-2.0) — see "Gestalt lineage" below.

## Foundations

- Primary: `#EA4C89`
- Primary dark / headline: `#0D0B26`
- Surface: `#FFFFFF`
- Background: `#FCFCFD`
- Border: `#ECECF1`
- Secondary text: `#7D7D8E`
- Success: `#22C55E`
- Warning: `#F59E0B`
- Error: `#DC2626`
- Typography: Inter Tight for display/headings; Inter for body; JetBrains Mono for metadata.
- The v6.0 core numeric scale (spacing/radius/opacity/motion/type, still exactly 4 values each) is unchanged and remains the default for existing work:
  - Spacing: 4, 8, 16, 32px (×2)
  - Radius: 8, 16, 32px (×2) + pill (9999px, functional full-round, not a scale step)
  - Opacity: 0.08, 0.16, 0.32, 0.64 (×2)
  - Motion duration: 125, 200, 320, 500ms (×~1.6) using `cubic-bezier(0.4, 0, 0.2, 1)`
  - Type scale: 4 fluid tokens, each a CSS `clamp()` scaling continuously between 360px and 1440px viewports — 10–12px, 14–18px, 20–28px, 28–44px
- Stroke: 1px solid `#ECECF1` for containers; 2px `#EA4C89` for active focus.

## Gestalt lineage

MusicLinkd's component and interaction patterns are informed by [Gestalt](https://gestalt.pinterest.systems/), Pinterest's open-source design system ([github.com/pinterest/gestalt](https://github.com/pinterest/gestalt), Apache-2.0). Gestalt is a React library with no native mobile SDK, so it is never installed as a dependency here — it's a design-language reference, reimplemented natively per platform (vanilla HTML/CSS on web, Jetpack Compose on Android), both driven by `tokens/tokens.json`.

- `foundation.spacing`/`radius`/`opacity`/`motion`/`type` in tokens.json (the "4-token core") is MusicLinkd's own constraint and stays in place — confirmed to already land exactly on Gestalt's real spacing (100/200/400/800 → 4/8/16/32px) and rounding (200/400/800/pill) steps, so it is a validated subsample rather than a divergence.
- `foundation.gestalt.*` in tokens.json carries Gestalt's full-fidelity scales verbatim (16-step spacing, 8-step rounding, full opacity/elevation/motion/type-size scales) for component work that needs more granularity than the 4-token core. Brand hue palettes (Gestalt's red/pink/blue/etc. ramps) are intentionally excluded — see Boundaries.
- Component anatomy, states, and behavior in the "Gestalt-Inspired" sections of `styleguide.html`/`merge3.html` are hand-built to match Gestalt's real specs, in MusicLinkd's own brand colors.

## Boundaries

Do not introduce Material-design layouts, glassmorphism, neumorphism, heavy gradients, or competing design-system files. Do not adopt Gestalt's Pinterest-red/pink brand hues or its `gestalt` npm package as a runtime dependency — it is a reference only.

**Purple** — amended 2026-08-31 on the brand owner's instruction. This section previously read "no purple brand tokens", and `meta.palette.removed.purple` in tokens.json cites that line as the reason `#8B5CF6` was deleted in v9. Purple is now permitted in exactly one place: `--status-category-bg` / `--status-category-text`, consumed only by `.ml-badge--category`. It restores styleguide.html v4's rule *"Pink = Brand. Black = Primary. Purple = Badges only."* It is **not** a brand token — it never paints a button, link, border or surface, and it carries no state meaning. It marks a badge as a *category* (genre, service, role) rather than a state, which is the one job no existing colour did. Anything beyond that single modifier is still forbidden.

`/guide` (generated from `index.html`) is the live reference. `styleguide.html` is the superseded v4 catalogue, kept for recovery only and 301-redirected to `/guide`. This README and `tokens/tokens.json` govern new work.

## Icon sets

Two, and they are not interchangeable.

| Set | Count | Form | Role |
|---|---|---|---|
| Gestalt (`musiclinkd-system/icons/`) | 423 | Filled paths, 16- and 24-unit grids | **Product set.** Screens inline these at build time via `icon()`, which fails the build on an unknown name. |
| Lucide (`icons-lucide/`) | 2006 | Stroked outlines, 24 grid, 2px round caps | **Breadth set.** Reach for it when the product set has no glyph. Vendored from `lucide-react` (ISC) by `scripts/build-icons-lucide.mjs`. |

Never mix them inside one control or one toolbar: at the same size a filled glyph beside a stroked one reads as two different weights. Choose a set per surface.

## Directory ownership

| Path | Purpose |
|---|---|
| `styleguide.html` | Canonical living Enterprise Design System & Styleguide reference (296 consolidated sections & screens). Moved here from `frontend/website/` 2026-08-12 — deploys to `/guide/styleguide.html` via `scripts/build-hosting.sh`. |
| `merge3.html` | Working consolidated draft/reference, includes the "Gestalt-Inspired" component section. Moved here alongside styleguide.html; deploys to `/guide/merge3.html`. |
| `tokens/tokens.json` | Machine-readable design tokens (colors, spacing, radius, typography, motion, stroke) |
| `components/` | Controls and composite UI primitives |
| `cards/` | Reusable discovery and transaction cards |
| `profile-types/` | Role-specific module composition |
| `templates/` | Reusable page layouts |
| `motion/` | Motion and loading rules |
| `accessibility/` | Inclusive design requirements |
| `icons/` | Approved icon assets (Gestalt family, Apache-2.0, see `icons/README.md`) |
| `illustrations/` | Custom illustrations |
| `assets/` | Brand assets (logos, photos, mockups) |
| `prompts/` | Versioned AI Studio prompts |