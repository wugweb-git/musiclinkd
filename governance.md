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
- **Prohibition**: Never use emoji as icons, colorful junkie icons, or mixed icon sets across any component or screen.

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

## 7. ♿ ARIA, WCAG 2.1 AA & Accessibility Standards

1. **Color Contrast**: Text must achieve a contrast ratio of at least **4.5:1** against its background (`#0D0B26` on `#FFFFFF` / `#FCFCFD` = `16.8:1`).
2. **Screen Reader Labels**: Every interactive icon button must include `aria-label` or `contentDescription`.
3. **Keyboard Focus**: All interactive controls must render a visible `2px` focus ring on keyboard tab navigation.
