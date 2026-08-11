# MusicLinkd Design System — Foundations (`docs/styleguide/Foundations.md`)

This is the single authoritative source of truth for the **MusicLinkd Design System Foundations (v4.0)** across the public website (`musiclinkd.com`), the mobile and web application (`app.musiclinkd.com`), and the administration control center (`admin.musiclinkd.com`). It maps all visual, structural, and interactive dimensions including colors, typography, spacing, gridding, elevation, borders, z-index overlays, corner radii, vector icons, tap/click/hover/scroll animations, and accessibility standards.

---

## 🎨 1. Design System Foundations & Brand Philosophy

### Visual Design Philosophy
MusicLinkd is designed around an editorial, high-contrast light aesthetic. It lets artist media, audio waveforms, portfolio assets, and profile photography lead the visual experience. Layouts emphasize structured grids, clean dividers, generous white space, and bold typography. Heavy drop shadows are strictly avoided in favor of crisp 1.dp structural borders (`#ECECF1`) and subtle tonal surface contrast.

### Brand Topology & Target Hosts
1. **Public Website (`musiclinkd.com`):** Editorial marketing landing pages, SEO catalogs, pricing tiers, and public discovery previews. Built with Next.js/Web.
2. **Application Target (`app.musiclinkd.com`):** High-contrast light Jetpack Compose Android app for musicians, vocalists, producers, audio engineers, and studio bookings.
3. **Admin Portal (`admin.musiclinkd.com`):** Enterprise Control Center for moderation, user verification queues, flagged content review, and system analytics. Built with React.

---

## 📐 2. The 8-Layer Architectural Hierarchy Model

The MusicLinkd user experience is built upon eight nested structural layers. Every visible element on any screen maps to exactly one layer in this hierarchy:

```
+-----------------------------------------------------------------+
|                         8. SCREENS (Routes)                     |
+-----------------------------------------------------------------+
                               ^
+-----------------------------------------------------------------+
|                     7. MODULE UI / SUB-VIEWS                    |
+-----------------------------------------------------------------+
                               ^
+-----------------------------------------------------------------+
|                       6. STATE WIDGETS                          |
+-----------------------------------------------------------------+
                               ^
+-----------------------------------------------------------------+
|                      5. REUSABLE BLOCKS                         |
+-----------------------------------------------------------------+
                               ^
+-----------------------------------------------------------------+
|                     4. COMPOUND COMPONENTS                      |
+-----------------------------------------------------------------+
                               ^
+-----------------------------------------------------------------+
|                      3. ATOMIC PRIMITIVES                       |
+-----------------------------------------------------------------+
                               ^
+-----------------------------------------------------------------+
|                     2. BRAND DESIGN TOKENS                      |
+-----------------------------------------------------------------+
                               ^
+-----------------------------------------------------------------+
|                    1. CORE SYSTEM TAXONOMIES                    |
+-----------------------------------------------------------------+
```

### Layer Breakdown Summary
1. **Level 1: Core System Taxonomies:** 11 master taxonomy categories (`person_types`, `languages`, `genres`, `instruments`, `persona_badges`, `project_types`, `project_roles`, `booking_statuses`, `studio_amenities`, `collaboration_types`, `trust_badges`) & `modules/{moduleKey}` flags.
2. **Level 2: Brand Design Tokens:** Stable v4.0 colors, shapes, typography, and spacing grids.
3. **Level 3: Atomic Primitives:** Stateless chips, input fields, primary pill buttons, status badges, and star ratings.
4. **Level 4: Compound Components:** Search bars, tab rows, bottom navigation bars, cover headers, and error cards.
5. **Level 5: Reusable Blocks:** Artist cards, studio cards, track/project cards, booking summary cards, and review cards.
6. **Level 6: State Widgets:** Global audio playback bar, illustrated empty states, shimmer skeleton loaders, and networking feeds.
7. **Level 7: Module UI / Sub-Views:** Auth forms, onboarding wizards, profile settings, project creation steppers, and studio booking calendar grids.
8. **Level 8: Screens:** Top-level route composables (`WELCOME`, `ROLE_SELECT`, `LOCATION_SELECT`, `METADATA_SYNC`, `DASHBOARD`, `DISCOVER`, `PORTFOLIO`, `STUDIO_BOOKINGS`, `MESSAGING`, `CONNECTIONS`, `REPUTATION`, `SUPPORT_DESK`).

---

## 🎨 3. Color Palette Tokens & Semantic Mapping (Stable v4.0)

| Token Name | Hex Value | Color Name | Usage Context |
| :--- | :--- | :--- | :--- |
| **White** | `#FFFFFF` | Pure White | Card canvas background, dialog surfaces, button text |
| **Bg** | `#FCFCFD` | Soft Off-White | General application screen backdrop |
| **Surface-50** | `#F8F8FB` | Extra Light Gray | Secondary card background, subtle section tint |
| **Surface-100** | `#F3F3F6` | Light Neutral Gray | Input fill backgrounds, tag pill backgrounds |
| **Border** | `#ECECF1` | Subtle Silver | 1.dp structural dividers and container borders |
| **Muted** | `#8E91A1` | Muted Gray | Helper text, caption icons, placeholder text |
| **Text-2** | `#676B7B` | Secondary Text | Sub-headers, metadata labels, secondary info |
| **Text** | `#181A22` | Primary Text | Headings, title typography, high-contrast text |
| **Black** | `#0D0D0D` | Midnight Black | Primary dark accent, contrast callouts |
| **Pink** | `#EA4C89` | Brand Pink | Primary brand accent, active selection, CTAs |
| **Green** | `#16A34A` | Success Green | Verified badges, active online dots, connected states |
| **Error** | `#DC2626` | Alert Red | Form validation errors, destructive actions |

---

## 📐 4. Outlines, Shadows, Elevation & Opacity Tokens

### Border & Outline System
- **Default Structural Outline:** Solid `1.dp` border in `Border` (`#ECECF1`). Applied to all cards, chips, input fields, and bottom sheets.
- **Active / Focused Outline:** Solid `2.dp` border in `Pink` (`#EA4C89`). Applied to active selection cards, focused text inputs, and highlighted filters.
- **Success Outline:** Solid `1.dp` border in `Green` (`#16A34A`). Applied to connected profile badges and confirmed booking cards.

### Shadow & Elevation Standards
- **Flat Canvas Philosophy:** Heavy drop shadows are suppressed (`shadow = 0.dp`) to keep layouts crisp and modern.
- **Tonal Surface Elevation:** Depth hierarchy is created using surface color steps (`White` -> `Surface-50` -> `Surface-100`) rather than drop shadows.
- **Floating Decks & Modals:** Floating action bars, audio player decks, and dialogs use a subtle `2.dp` to `4.dp` elevation tint combined with a `1.dp` `Border` stroke.

### Opacity & Alpha Overlays
- **Modal Scrim Overlay:** `0.40f` alpha `Black` (`#0D0D0D`) backdrop scrim on dialogs and bottom sheet popups.
- **Subtle Surface Tint:** `0.06f` alpha `Pink` (`#EA4C89`) background fill for active selected states.
- **Disabled Element Opacity:** `0.38f` alpha applied across text and buttons in disabled states.
- **Secondary Caption Muting:** `0.65f` to `0.85f` alpha for helper text and secondary labels.

---

## 🏗️ 5. Z-Index Layering & Stack Architecture

| Layer ID | Z-Index | Component Type | Stacking Description |
| :--- | :--- | :--- | :--- |
| **Canvas** | `z = 0` | Base Screen Canvas | Off-white background (`#FCFCFD`), lazy scroll content |
| **Header** | `z = 1` | Sticky Top Bar / App Bar | Top search header, sticky section headers |
| **Navigation** | `z = 2` | Bottom Navigation Bar | Fixed bottom navigation bar with active pills |
| **Audio Deck** | `z = 3` | Global Audio Player Deck | Pinned persistent audio player bar |
| **Scrim** | `z = 10` | Overlay Dimming Scrim | Backdrop overlay (`0.40f` `Black`) for modals |
| **Modal** | `z = 11` | Bottom Sheet / Dialog | Active dialog surfaces, picker menus (`24.dp` radius) |
| **Toast / Alert** | `z = 20` | Notifications & Toasts | High-priority snackbars and system floating alerts |

---

## ⭕ 6. Corner Radii & Curves System

- **`RadiusHero` (`32.dp`):** Hero splash cards, major modal bottom sheets, onboarding card containers (`RoundedCornerShape(32.dp)`).
- **`RadiusBase` (`24.dp`):** Mandatory standard corner radius for all cards, dialogs, media panels, and search containers (`RoundedCornerShape(24.dp)`).
- **`RadiusInner` (`12.dp`):** Interior elements including input fields, thumbnail previews, gear spec cards, and audio waveform containers (`RoundedCornerShape(12.dp)`).
- **`RadiusPill` (`999.dp`):** Main primary CTA buttons, filter category chips, status badges, and active pill indicators (`RoundedCornerShape(999.dp)`).

---

## 📏 7. Spacing, Padding, & 8.dp Grid System

All layouts enforce an **8.dp baseline grid** to maintain horizontal and vertical rhythm:

| Spacing Token | Value | Applied Context |
| :--- | :--- | :--- |
| **`SpacingMicro`** | `4.dp` | Micro-gaps between inline icons and text labels |
| **`SpacingCompact`** | `8.dp` | Compact padding inside category chips, tag rows, and small buttons |
| **`SpacingMedium`** | `12.dp` | Gaps between internal card elements, input field vertical padding |
| **`SpacingBase`** | `16.dp` | Standard screen edge margin, content row spacing, list item gaps |
| **`SpacingLarge`** | `24.dp` | Outer card padding, section headers, container separations |
| **`SpacingHero`** | `32.dp` | Screen top hero margins, splash banner padding |
| **`TouchTarget`** | `48.dp` | Minimum touch target height and width for interactive elements |

---

## ✍️ 8. Typography Scale & Font Weights

MusicLinkd pairs Plus Jakarta Sans with standard system typography:

| Token Name | Font Family | Size / Line Height | Weight | Color Token | Primary Usage Context |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **`DisplayTitle`** | Plus Jakarta Sans | 28.sp / 36.sp | ExtraBold | `Text` (`#181A22`) | Screen hero headers, splash greetings |
| **`HeaderMedium`** | Plus Jakarta Sans | 20.sp / 26.sp | Bold | `Text` (`#181A22`) | Section titles, artist card names |
| **`BodyLarge`** | Plus Jakarta Sans | 15.sp / 22.sp | Medium | `Text` (`#181A22`) | Bio text, feed content, messages |
| **`BodySmall`** | Plus Jakarta Sans | 13.sp / 18.sp | Normal | `Text-2` (`#676B7B`) | Location details, sub-labels, gear specs |
| **`Caption`** | System Sans | 11.sp / 14.sp | SemiBold | `Pink` (`#EA4C89`) | Genre chips, verified labels, time codes |

---

## 🖼️ 9. Iconography Extended & Lucide Vector System (v4.0)

### Icon Scale Guidelines
| Size Token | Usage Context | Type |
| :--- | :--- | :--- |
| **16dp** | Micro badges, status indicators, inline tags | Outlined / Filled |
| **20dp** | Action chips, search input leads, sub-button icons | Outlined |
| **24dp** | Standard navigation items, primary actions, tab icons | Outlined (`1.75dp` stroke) |
| **32dp** | Hero workspace headers, empty state illustrations | Outlined / Filled |

### Icon Color State Mapping
- **`Active`:** `Pink` (`#EA4C89`) for active selections and highlighted navigation.
- **`Inactive`:** `IconInactive` (`#7D7D8E`) for unselected states.
- **`Disabled`:** `IconDisabled` (`#9CA3AF`) for locked or disabled actions.

### Vector Specification
- **Library Standard:** Lucide vector family, outlined at **`1.75dp` stroke width**, **`24.dp` default bounding box**.

---

## ⚡ 10. Interactive Micro-Interactions, Animations & Motion Specifications

### Tap, Click & Press Micro-Interactions
- **Scale Bounce Press Effect:** Interactive cards and buttons scale smoothly down to `0.96f` on press and bounce back to `1.0f` on release using spring physics (`spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)`).
- **Tactile Ripple Feedback:** All clickable surfaces supply Material 3 ripples (`rememberRipple()`) to guarantee visual feedback.

### Hover, Focus & State Transitions
- **Focus Transition:** Text inputs and interactive surfaces transition border stroke from `Border` (`#ECECF1`) to `Pink` (`#EA4C89`) over `150ms` (`tween(150, easing = FastOutSlowInEasing)`).
- **Selection State Transition:** Card background colors transition seamlessly between `White` (`#FFFFFF`) and `Surface-50` (`#F8F8FB`) upon selection.

### Scroll Animations & Behavior
- **Dynamic Header Elevation:** Sticky Top AppBars compute scroll offset from `LazyListState` to adjust background opacity and border visibility on scroll.
- **Floating Audio Deck Motion:** Bottom audio player deck slides smoothly out of sight when scrolling down, and reappears on upward scroll.

### Screen Entry & Transition Physics
- **Horizontal Slide & Fade:** Navigation transitions utilize a `300ms` horizontal slide combined with an alpha fade (`tween(300)`).
- **Equalizer Waveform Motion:** Audio equalizer visualization bars animate height infinitely using `rememberInfiniteTransition()`.

---

## ♿ 11. Accessibility & Android UI Standards

1. **Edge-to-Edge Design:** Mandatory invocation of `enableEdgeToEdge()` in `MainActivity` combined with proper `WindowInsets` handling (`Scaffold(contentWindowInsets = ...)`).
2. **Touch Target Sizing:** All touch target elements maintain a minimum clickable dimension of **`48.dp x 48.dp`**.
3. **Fluid Typography Scaling:** All typography declarations utilize scale-independent pixels (`sp`).
4. **Screen Reader Semantics:** Purely decorative illustrations pass `null` as their `contentDescription`. All interactive elements supply descriptive labels.
5. **Adaptive Screen Widths:** Layout containers avoid hardcoded absolute widths, using dynamic percentages (`fillMaxWidth()`) with max width constraints (`Modifier.widthIn(max = 600.dp)`).


