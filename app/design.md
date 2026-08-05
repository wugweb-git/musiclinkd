# MUSICLINKD DEFINITIVE DESIGN SYSTEM & UI TOKENS

This document serves as the single source of truth for the MusicLinkd visual language, design tokens, typography scales, and structural layouts. It defines the premium, editorial aesthetic.

---

## 🎨 1. COLOR DESIGN TOKENS

Our color system is optimized for editorial high-contrast presentation.

### Primary brand primitives
*   `brand-primary`: `#EA4C89` (Vibrant Pink Accent) — Used for core interactive elements, CTA buttons, active states, progress indicators, and highlight badges.
*   `brand-dark`: `#0D0B26` (Midnight Navy) — Used for headings, major titles, prominent texts, and bold high-contrast structures.
*   `brand-background`: `#FCFCFD` (Editorial Soft Off-White) — The clean canvas background that makes images and text breathe.
*   `brand-card`: `#FFFFFF` (Pristine White) — Used for container surfaces and cards to elevate content above the background.
*   `brand-border`: `#ECECF1` (Subtle Silver) — Used for thin `1.dp` borders to partition sections cleanly instead of heavy shadows.
*   `brand-text-primary`: `#0D0B26` (Midnight Navy) — Primary high-contrast typography.
*   `brand-text-secondary`: `#7D7D8E` (Muted Steel Gray) — Secondary support, details, and metadata texts.
*   `brand-success`: `#10B981` or `#16A34A` (Emerald Green) — Used for currency rates, approved statuses, and secure booking confirmations.

---

## 📐 2. RADIUS & STRUCTURAL TOKENS

We utilize soft, rounded corners to maintain a friendly yet professional high-fashion aesthetic.

### Border Radius System
*   `radius-none`: `0.dp`
*   `radius-xs`: `8.dp` (e.g., status tags, tiny indicators)
*   `radius-sm`: `12.dp` (e.g., album art, input boxes, small cards)
*   `radius-md`: `16.dp` (e.g., standard inner panels, review cards)
*   `radius-lg`: `24.dp` (e.g., standard discovery cards, artist showcases)
*   `radius-xl` / `container-outer`: `32.dp` (e.g., outer display panels, main modal sheets, onboarding sheets)
*   `radius-pill`: `999.dp` (e.g., pills, search bars, CTA buttons, status badges)

### Border Stroke Weight System
*   `stroke-subtle`: `1.dp` — Standard divider and container border weight.
*   `stroke-heavy`: `2.dp` — Input fields active focus outlines.

---

## 💬 3. SPACING SCALE

We employ an 8.dp grid system with a heavy emphasis on **large negative space** for a spacious, editorial breathing feel.

*   `space-xs` (`4.dp`): Status badges, avatar padding.
*   `space-sm` (`8.dp`): Small gaps between related items, internal chip card content.
*   `space-md` (`16.dp`): Standard padding for containers, cards, and screen boundaries.
*   `space-lg` (`24.dp`): Spacing between major, distinct design blocks.
*   `space-xl` (`32.dp` to `48.dp`): Premium editorial whitespace to isolate sections and create elegant visual focus.

---

## ✍️ 4. TYPOGRAPHY SYSTEM

Typography on MusicLinkd is expressive, clean, and carries professional weight.

### Font Families
*   **Display / Headings**: Inter Tight (or standard Bold/Black sans-serif) — Used for expressive headlines and numbers.
*   **Body / Support**: Inter (or standard medium/regular sans-serif) — Used for paragraphs, forms, and secondary text.
*   **Monospace / Metadata**: JetBrains Mono (or standard monospace) — Used for numerical counts, stats, status codes, and tags.

### Typography Scale
*   **Display / Title Large**: `28.sp` to `32.sp` (Bold/Black) — Used for main screen welcome titles and main prices.
*   **Heading Medium**: `20.sp` to `24.sp` (Bold) — Used for category sections, names, and card headers.
*   **Body Large / Subhead**: `16.sp` (Medium) — High-priority info, body labels, buttons.
*   **Body Medium**: `14.sp` (Regular) — Descriptions, chat messages, input values.
*   **Caption / Micro-text**: `11.sp` to `12.sp` (Regular/Medium) — Timestamp, location, category subhead tags.

---

## 🧬 5. DESIGN PRINCIPLES & DNA

1.  **Editorial Minimalism**: Flat design featuring thin, clean gray borders (`1.dp`) and high-contrast typography instead of heavy multi-layered shadows, glassmorphism, or complex background gradients.
2.  **Interactive Card Discovery**: Content-driven layouts that prioritize swipeability, rich visual card profiles, and immediate interactive engagement.
3.  **Audio-First Integration**: Previews, waveforms, and visual volume states are treated as premium UI elements, giving music creators immediate feedback.
4.  **Massive Negative Space**: Prioritize spacious paddings over dense clusters of information. Let individual portfolio assets speak for themselves.
5.  **No Visual Noise**: Say NO to heavy color gradients, 3D buttons, or neumorphic elements. Rely on pristine flat white surfaces against soft off-white backgrounds, with MusicLinkd Pink used exclusively for CTAs and highlights.
