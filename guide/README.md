# MusicLinkd Design System

This directory is the single source of truth for MusicLinkd's UI and brand decisions.

**Interactive reference:** Open `guide/index.html` in any browser — no build step, no dependencies beyond Google Fonts.

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
- Spacing scale: 4, 8, 16, 24, 32, 48, 64, 96, 128 px (8px-based rhythm).
- Radius: 12, 16, 24, 32 px; pill (9999px) for buttons, chips, and compact controls.
- Stroke: 1px solid `#ECECF1` for containers; 2px `#EA4C89` for active focus.
- Motion: 150ms, 200ms, 300ms, and 500ms using `cubic-bezier(0.4, 0, 0.2, 1)`.

## Boundaries

Do not introduce Material-design layouts, purple brand tokens, glassmorphism, neumorphism, heavy gradients, mixed icon families, or competing design-system files. The `index.html` is the interactive reference; this README and `tokens/tokens.json` govern new work.

## Directory ownership

| Path | Purpose |
|---|---|
| `index.html` | Interactive living design system (sidebar nav, live previews, AI prompt library) |
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