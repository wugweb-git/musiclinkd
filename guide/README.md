# MusicLinkd Design System

This directory is the single source of truth for MusicLinkd's UI and brand decisions.

**Interactive reference:** Open `guide/styleguide.html` in any browser — no build step, no dependencies beyond Google Fonts.

## Product expression

- Premium, editorial, airy, audio-first, people-first marketplace.
- Prioritise discovery, hiring, booking, collaboration, buying, and renting over generic social engagement.
- Use realistic human photography for people-facing content. Do not use initials, emoji, cartoon avatars, invented statistics, or unapproved product features as placeholders.
- Use one icon family only: Lucide, outlined at a 1.75px stroke.
- Reuse approved components and templates; do not create a visual variant when an existing component satisfies the need.

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
- Every numeric token category is exactly 4 values on an exponential scale — no exceptions, no per-breakpoint variants:
  - Spacing: 4, 8, 16, 32px (×2)
  - Radius: 8, 16, 32px (×2) + pill (9999px, functional full-round, not a scale step)
  - Opacity: 0.08, 0.16, 0.32, 0.64 (×2)
  - Motion duration: 125, 200, 320, 500ms (×~1.6) using `cubic-bezier(0.4, 0, 0.2, 1)`
  - Type scale: 4 fluid tokens, each a CSS `clamp()` scaling continuously between 360px and 1440px viewports — 10–12px, 14–18px, 20–28px, 28–44px
- Stroke: 1px solid `#ECECF1` for containers; 2px `#EA4C89` for active focus.

## Boundaries

Do not introduce Material-design layouts, purple brand tokens, glassmorphism, neumorphism, heavy gradients, mixed icon families, or competing design-system files. The `index.html` is the interactive reference; this README and `tokens/tokens.json` govern new work.

## Directory ownership

| Path | Purpose |
|---|---|
| `styleguide.html` | Canonical living Enterprise Design System & Styleguide reference (296 consolidated sections & screens) |
| `tokens/tokens.json` | Machine-readable design tokens (colors, spacing, radius, typography, motion, stroke) |
| `components/` | Controls and composite UI primitives |
| `cards/` | Reusable discovery and transaction cards |
| `profile-types/` | Role-specific module composition |
| `templates/` | Reusable page layouts |
| `motion/` | Motion and loading rules |
| `accessibility/` | Inclusive design requirements |
| `icons/` | Approved icon assets (Lucide family) |
| `illustrations/` | Custom illustrations |
| `assets/` | Brand assets (logos, photos, mockups) |
| `prompts/` | Versioned AI Studio prompts |