# MusicLinkd Design System — Foundations (`styleguide/Foundations.md`)

This is the single authoritative source of truth for the **MusicLinkd Design System Foundations** across the public website (`musiclinkd.com`), the mobile and web application (`app.musiclinkd.com`), and the administration control center (`admin.musiclinkd.com`). It covers visual design philosophy, low-level primitives, color tokens, typography scales, spacing grids, icons, and accessibility standards.

---

## 🎨 1. Design System Foundations & Brand Philosophy

### Visual Design Philosophy
MusicLinkd is designed around an editorial, high-contrast light aesthetic. It lets artist media, audio waveforms, portfolio assets, and profile photography lead the visual experience. Layouts emphasize structured grids, clean dividers, generous white space, and bold typography. Drop shadows are strictly avoided to prevent visual noise.

### Brand Topology & Environments
1. **Public Website (`musiclinkd.com`):** Editorial marketing landing pages, SEO catalogs, pricing tiers, and public discovery previews. Built with Next.js/Web.
2. **Application Target (`app.musiclinkd.com`):** M3 Jetpack Compose Android app for musicians, vocalists, producers, and recording studio bookings.
3. **Admin Portal (`admin.musiclinkd.com`):** Enterprise Control Center for moderation, user verification queues, flagged content review, and system analytics. Built with React.

---

## 📐 2. Color Palette & Primitive Tokens (`com.example.ui.theme`)

| Token Name | Hex Value | Color Name | Role & Usage Context |
| :--- | :--- | :--- | :--- |
| `BrandPrimary` | `#EA4C89` | Dribbble Pink | Primary brand accent, active selection chips, CTA buttons, and highlighted states. |
| `BrandDark` | `#0D0B26` | Midnight Navy | Primary text color, headers, title labels, and structural typography. |
| `BrandBackground`| `#FCFCFD` | Soft Off-White | General application background canvas (low eye-strain contrast). |
| `BrandBorder` | `#ECECF1` | Subtle Silver | 1.dp structural dividers, borders on cards, and container separators. No drop shadows. |
| `BrandTextSecondary`| `#7D7D8E`| Muted Slate | Subtitles, helper text, caption labels, and inactive state placeholders. |
| `BrandSuccess` | `#10B981` | Emerald Green | Verified artist checkmarks, success indicators, and connected state badges. |

### Border Radius Primitives
- **`RadiusBase` (`24.dp`):** Mandatory container corner radius for all cards, modal bottom sheets, pop-up dialog overlays, and main panels (`RoundedCornerShape(24.dp)`).
- **`RadiusPill` (`999.dp`):** Used for all main primary action buttons and state-switching chips (`RoundedCornerShape(999.dp)`).
- **`RadiusSmall` (`12.dp`):** Reserved for interior elements, such as inline search inputs and media/track thumbnail previews.

### Spacing Grid Primitives
Enforced strictly on an 8.dp baseline grid to establish a consistent vertical and horizontal rhythm:
- **`4.dp`:** Micro-gaps between icons and text or adjacent chips.
- **`8.dp`:** Compact padding inside buttons, category chips, and list headers.
- **`12.dp`:** Spacing between internal card elements or adjacent text rows.
- **`16.dp`:** Standard screen edge padding on compact/mobile displays.
- **`24.dp`:** Large section separators, outer card margins, and primary container padding.
- **`32.dp`:** Large hero workspace margins and upper display banners.

---

## ✍️ 3. Typography Hierarchy Scale

MusicLinkd pairs Plus Jakarta Sans with standard system display typography to ensure excellent legibility across all environments:

| Token Name | Font Family | Size / Line Height | Weight | Color Token | Primary Usage Context |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **`DisplayTitle`** | Plus Jakarta Sans | 28.sp / 36.sp | ExtraBold | `BrandDark` | Screen hero headers, splash greetings |
| **`HeaderMedium`** | Plus Jakarta Sans | 20.sp / 26.sp | Bold | `BrandDark` | Section titles, artist card names |
| **`BodyLarge`** | Plus Jakarta Sans | 15.sp / 22.sp | Medium | `BrandDark` | Bio text, feed content, messages |
| **`BodySmall`** | Plus Jakarta Sans | 13.sp / 18.sp | Normal | `BrandTextSecondary` | Location details, sub-labels, gear specs |
| **`Caption`** | System Sans | 11.sp / 14.sp | SemiBold | `BrandPrimary` | Genre chips, verified labels, time codes |

---

## 📦 4. Icon Library Specification (`IconLibrary.kt`)

MusicLinkd declares clean Material Symbols to maintain layout parity across tabs and controls:

| Icon Key | Icon Name | Category | Material Symbol / Path Reference | Usage |
| :--- | :--- | :--- | :--- | :--- |
| `discover` | Discover | Navigation | `Icons.Default.Home` | Main workspace feed and AI matchmaker tab |
| `marketplace` | Marketplace | Navigation | `Icons.Default.ShoppingBag` | Gear and STEM marketplace tab |
| `connections` | Network | Navigation | `Icons.Default.People` | Connections and active chat directory tab |
| `messages` | Chat Threads | Navigation | `Icons.Default.Email` | Message and vocal feedback history tab |
| `profile` | My Portfolio | Navigation | `Icons.Default.Person` | User creative portfolio workspace tab |
| `play` | Play Trigger | Audio | `Icons.Default.PlayArrow` | Audio track play action |
| `pause` | Pause Trigger | Audio | `Icons.Default.Pause` | Audio track pause action |
| `mic` | Vocal Micro | Genres | `Icons.Default.Mic` | Vocalist role badge & audio recording |
| `verified` | Verified | Badges | `Icons.Default.Verified` | Certified profile checkmark emblem |

---

## ♿ 5. Accessibility & Android UI Standards

### Native Android Standards
1. **Edge-to-Edge Design:** Mandatory invocation of `enableEdgeToEdge()` in `MainActivity` combined with proper `WindowInsets` handling (e.g., `Scaffold(contentWindowInsets = ...)`).
2. **Touch Target Sizing:** All touch target targets must maintain a minimum clickable dimension of **`48.dp x 48.dp`** to support touch ergonomics and accessibility.
3. **Fluid Typography Scaling:** All typography declarations must utilize scale-independent pixels (`sp`) to allow clean wrapping and respect system text sizes.
4. **Screen Reader Semantics:** Purely decorative illustrations or icons must pass `null` as their `contentDescription`. Non-decorative buttons, images, and dynamic indicators must supply meaningful description labels.
5. **Adaptive Screen Widths:** Layout containers must avoid hardcoded absolute widths. Use dynamic percentages (`fillMaxWidth()`) with maximum width bounding constraints (`Modifier.widthIn(max = 600.dp)`) to render beautifully on foldables, tablets, and wide landscape displays.
