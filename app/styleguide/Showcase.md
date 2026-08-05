# MusicLinkd Design System — Complete Interactive Showcase & Specification

Welcome to the single source of truth for the **MusicLinkd Design System** across `musiclinkd.com`, `app.musiclinkd.com`, and `admin.musiclinkd.com`.

---

## 🎨 Design System Foundations

### 1. Brand Palette & Color Tokens (`styleguide/colors/color_palette.md`)
- **Brand Primary (`#EA4C89`):** Accent pink used for primary call-to-action pill buttons, active tab indicators, and verified highlight badges.
- **Brand Dark (`#0D0B26`):** Midnight navy used for high-contrast primary typography and main headers.
- **Brand Background (`#FCFCFD`):** Clean off-white canvas ensuring low eye-strain.
- **Brand Border (`#ECECF1`):** Subdued 1.dp border stroke for card containers. Drop shadows are strictly avoided.
- **Brand Success (`#10B981`):** Emerald green for verified artist checkmarks and connected states.

### 2. Spatial Grid & Corner Radius (`styleguide/radius/corner_radius.md` & `spacing/`)
- **Container Corner Radius:** Mandatory `24.dp` (`RoundedCornerShape(24.dp)`) across all cards, dialogs, and sheets.
- **Pill Button Radius:** `999.dp` (`RoundedCornerShape(999.dp)`).
- **Spatial Grid:** 8.dp baseline grid (`4.dp`, `8.dp`, `12.dp`, `16.dp`, `24.dp`, `32.dp`).

---

## 🧩 Reusable Component Catalog

### `UnifiedBaseCard`
- **Description:** Clean, white background card container with mandatory `24.dp` corner radius and `1.dp` border.
- **Usage Do:** Use for all list items, artist profile cards, studio suite cards, and marketplace gear items.
- **Usage Don't:** Do NOT add elevation or drop shadows. Do NOT use sharp 0.dp corners.

### `UnifiedPrimaryButton`
- **Description:** High-contrast pill-shaped action button (`#EA4C89`).
- **Usage Do:** Use for primary screen actions ("Connect", "Book Studio Slot", "Save Changes").
- **Usage Don't:** Do NOT use square corners. Minimum touch target height must be `48.dp`.

---

## 📱 Screen & Template Showcase

- **Discover Dashboard:** `com.example.onboarding.ui.screens.DiscoverScreens`
- **Artist Portfolio:** `com.example.onboarding.ui.screens.PortfolioScreens`
- **Studio Reservation Sheet:** `com.example.onboarding.ui.screens.StudioBookingScreens`
- **Direct Messaging Thread:** `com.example.onboarding.ui.screens.MessagingScreens`
