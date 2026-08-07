# MusicLinkd Engineering Governance & Mobile-First Standards (v2026.1)

> **Authoritative Rules for Android Jetpack Compose, Web, and Admin Portal**

---

## 1. 📱 Mobile-First Priority & Viewport Baseline

1. **360dp Baseline**: Every interface component, card, button, and screen MUST be designed first for a **360dp mobile viewport reference frame** before scaling to tablet (768dp) or desktop (1024dp+).
2. **Touch Targets**: All interactive elements (buttons, chips, icon buttons, list items, checkboxes, toggles) MUST satisfy a minimum touch target size of **48dp × 48dp** (`min-height: 48px; min-width: 48px;`).
3. **No Full-Width Stretch**: On desktop viewports, cards and form controls MUST NOT stretch across the entire screen. Enforce compact card wrappers (`max-width: 360dp` to `480dp`) that wrap into a **2nd row** or responsive grid columns (`repeat(auto-fit, minmax(280dp, 1fr))`).

---

## 2. 🎨 Color Swatch Rule

- **Strict Directive**: Every single color hex code (`#EA4C89`, `#0D0B26`, `#FCFCFD`, `#ECECF1`, `#7D7D8E`, `#22C55E`, `#F59E0B`, `#DC2626`) documented in code, tables, or documentation MUST be accompanied by a visual inline color swatch box (`<span class="ts" style="background:#HEX"></span>`).

---

## 3. 🖼️ Icon Language Standard

- **Icon Family**: Use **Lucide Outlined Icons** exclusively (outlined at `1.75px` stroke in monochrome `#0D0B26` / `#7D7D8E` or brand pink `#EA4C89`).
- **No Multi-Colored Default Icons**: Every icon in its default/resting state MUST be a single solid color. No gradients, no duo-tone, no multi-color fills. The ONLY permitted icon colors are:
  - `var(--black)` / `#0D0D0D` — default, inactive
  - `var(--muted)` / `#7D7D8E` — secondary, disabled
  - `var(--pink)` / `#EA4C89` — active, selected, brand accent
- **Prohibition**: Never use emoji as icons, colorful junkie icons, FontAwesome multi-color icons, or mixed icon sets across any component or screen. Lucide only. One color per icon. Period.

---

## 4. 🔤 Native Android Compose Typography Scale

Every typography token MUST define all 8 composite properties: Font Family, Size (`sp`), Line Height, Letter Spacing, Font Weight, Color Token, Padding, and Jetpack Compose `@Composable` snippet:

| Compose Token | Font Family | Size / LineHeight | Weight | Color Token | Padding |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `displayLarge` | Inter Tight | 57sp / 64sp | 800 (ExtraBold) | `var(--navy)` (`#0D0B26`) | 0 0 16dp |
| `displayMedium` | Inter Tight | 45sp / 52sp | 700 (Bold) | `var(--navy)` (`#0D0B26`) | 0 0 12dp |
| `headlineLarge` | Inter Tight | 32sp / 40sp | 700 (Bold) | `var(--navy)` (`#0D0B26`) | 0 0 12dp |
| `headlineMedium` | Inter Tight | 28sp / 36sp | 700 (Bold) | `var(--navy)` (`#0D0B26`) | 0 0 8dp |
| `titleLarge` | Inter | 22sp / 28sp | 600 (SemiBold) | `var(--navy)` (`#0D0B26`) | 0 0 8dp |
| `bodyLarge` | Inter | 16sp / 24sp | 400 (Regular) | `var(--navy)` (`#0D0B26`) | 0 0 8dp |
| `bodyMedium` | Inter | 14sp / 20sp | 400 (Regular) | `var(--muted)` (`#7D7D8E`) | 0 0 6dp |
| `labelLarge` | Inter | 14sp / 20sp | 600 (SemiBold) | `var(--pink)` (`#EA4C89`) | 0 0 4dp |
| `labelSmall` | Inter | 11sp / 16sp | 500 (Medium) | `var(--muted)` (`#7D7D8E`) | 0 0 4dp |

---

## 5. 🔗 Global Token Mapping Hierarchy

All UI elements must follow the 4-tier token mapping hierarchy:
```
Global Tokens (Colors, Spacing, Radius, Typography, Motion)
  └─► Primitive Components (Buttons, Inputs, Chips, Badges, Icons)
        └─► Composite Components (Artist Card, Studio Card, Waveform Player, Chat Drawer)
              └─► Page Templates (Discover, Profile, Studio Booking, Marketplace)
```

---

## 6. ⚡ 6-State Interactive Matrix

Every component must define 6 explicit interaction states:
1. **Default** (`enabled = true`)
2. **Hover** (`collectIsHoveredAsState()`)
3. **Pressed** (`collectIsPressedAsState()` -> `scale(0.98)`)
4. **Focused** (`collectIsFocusedAsState()` -> `2px solid var(--pink)`)
5. **Disabled** (`enabled = false` -> `opacity: 0.4`, `cursor: not-allowed`)
6. **Error** (`isError = true` -> `border: 1.5px solid var(--err)`)

---

## 7. 📐 Card & Block Height Constraints (Breakpoint-Mapped)

Every card, block, and container MUST respect min/max height boundaries mapped to the 4-breakpoint grid. This prevents content overflow, maintains visual rhythm, and ensures consistent scrolling behavior across all viewport sizes.

| Component | Min Height | Max Height (Mobile 0-767px) | Max Height (Tablet 768-1023px) | Max Height (Desktop 1024-1439px) | Max Height (Wide 1440px+) | Overflow |
|-----------|-----------|---------------------------|-------------------------------|--------------------------------|--------------------------|----------|
| Card (.card-s) | 120dp | 480dp | 520dp | 600dp | 680dp | hidden |
| Masonry Item | auto | 420dp | 460dp | 520dp | 580dp | hidden |
| Spec Card | 100dp | 360dp | 400dp | 480dp | 560dp | scroll |
| Visual Demo | 80dp | 600dp | none | none | none | auto |
| Phone Frame Screen | 500dp | 700dp | 700dp | 700dp | 700dp | scroll |

**Breakpoint Reference:**
- **Mobile:** 0–767px (360dp viewport, 2-col grid)
- **Tablet:** 768–1023px (iPad, foldables, 4-col grid)
- **Desktop:** 1024–1439px (Laptops, 8-col grid)
- **Wide:** 1440px+ (Large monitors, 12-col grid)

**CSS Enforcement:**
```css
.card-s { min-height: 120px; overflow: hidden; }
@media (max-width: 767px)  { .card-s { max-height: 480px; } }
@media (min-width: 768px) and (max-width: 1023px) { .card-s { max-height: 520px; } }
@media (min-width: 1024px) and (max-width: 1439px) { .card-s { max-height: 600px; } }
@media (min-width: 1440px) { .card-s { max-height: 680px; } }
```

---

## 8. 📝 Changelog Discipline

- **Mandatory Rule**: Every commit MUST include a corresponding entry in `CHANGELOG.md` before `git commit`.
- **Format**: Each entry must follow [Keep a Changelog](https://keepachangelog.com/) conventions:
  ```
  ## [Unreleased]
  ### Added / Changed / Fixed / Removed
  - <concise description of what changed and why>
  ```
- **Enforcement**: A commit without a `CHANGELOG.md` update is considered incomplete. CI should fail the build if `CHANGELOG.md` is not modified in the same commit as code changes.
- **Scope**: This applies to all repos: `app/` (Android), `admin/` (Next.js), `website/` (static), `guide/` (design system).

---

## 9. ♿ ARIA, WCAG 2.1 AA & Accessibility Standards

1. **Color Contrast**: Text must achieve a contrast ratio of at least **4.5:1** against its background (`#0D0B26` on `#FFFFFF` / `#FCFCFD` = `16.8:1`).
2. **Screen Reader Labels**: Every interactive icon button must include `aria-label` or `contentDescription`.
3. **Keyboard Focus**: All interactive controls must render a visible `2px` focus ring on keyboard tab navigation.
