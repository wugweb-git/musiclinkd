# MusicLinkd Design System — Components & Layouts (`styleguide/Components.md`)

This is the single authoritative source of truth for the **MusicLinkd Components, Widgets, Layout Templates, and Motion/Transitions Specifications** across the public website (`musiclinkd.com`), the mobile and web application (`app.musiclinkd.com`), and the administration control center (`admin.musiclinkd.com`).

---

## 🧩 1. Reusable Components Catalog (`UnifiedComponents.kt`)

All layouts across the application must draw components directly from the unified styleguide catalog to guarantee strict design parity:

### 1. `UnifiedBaseCard`
- **Specification:** Clean, structural border container.
- **Visual Styles:** Pure white background canvas (`#FFFFFF`), a mandatory solid `1.dp` border stroke in `BrandBorder` (`#ECECF1`), and a uniform corner radius of `24.dp`. Drop shadows are suppressed.
- **Height Constraints:** Min 120dp. Max per breakpoint: Mobile 480dp, Tablet 520dp, Desktop 600dp, Wide 680dp.
- **Compose API:**
  ```kotlin
  @Composable
  fun UnifiedBaseCard(
      modifier: Modifier = Modifier,
      onClick: (() -> Unit)? = null,
      content: @Composable ColumnScope.() -> Unit
  )
  ```

### 2. `UnifiedPrimaryButton`
- **Specification:** Main call-to-action button with loading and disabled states.
- **Visual Styles:** Pinned pill-shaped layout (`RoundedCornerShape(999.dp)`), solid `BrandPrimary` pink background, white bold typography, and a guaranteed touch target height of **`48.dp`** or higher.
- **Compose API:**
  ```kotlin
  @Composable
  fun UnifiedPrimaryButton(
      text: String,
      onClick: () -> Unit,
      modifier: Modifier = Modifier,
      isLoading: Boolean = false,
      enabled: Boolean = true
  )
  ```

### 3. `UnifiedConnectButton`
- **Specification:** Stateful button coordinating peer collaboration requests.
- **Functional States:**
  - `IDLE` ("Connect"): Solid `BrandPrimary` pink background with white text.
  - `PENDING` ("Requested"): Soft gray background with a checkmark icon, signaling a waiting double opt-in action.
  - `CONNECTED` ("Connected"): Muted emerald background highlighting mutual verification.
- **Compose API:**
  ```kotlin
  @Composable
  fun UnifiedConnectButton(
      state: ConnectState,
      onClick: () -> Unit,
      modifier: Modifier = Modifier
  )
  ```

### 4. `UnifiedErrorCard`
- **Specification:** Warning card for empty, failed, or offline states.
- **Visual Styles:** Subtle light red border stroke (`1.dp`), integrated exclamation icon, error message block, and a clear retry action button.
- **Compose API:**
  ```kotlin
  @Composable
  fun UnifiedErrorCard(
      errorMessage: String,
      onRetry: () -> Unit,
      modifier: Modifier = Modifier
  )
  ```

### 5. `MasonryDiscoveryGrid`
- **Specification:** Variable-height card grid with hover overlays, save buttons, and action rows. Extracted from Pinterest Fluid UI design patterns.
- **Visual Styles:** CSS column-count responsive (4 cols desktop → 2 cols mobile). Cards have 24dp radius, 1dp border, hover lift (-4px translateY), image scale (1.06x) on hover, gradient overlay with save/share action buttons.
- **Height Constraints:** Masonry items max-height per breakpoint: Mobile 420dp, Tablet 460dp, Desktop 520dp, Wide 580dp.
- **Compose API:**
  ```kotlin
  @Composable
  fun MasonryDiscoveryGrid(
      items: List<DiscoveryCardData>,
      onSave: (String) -> Unit,
      onShare: (String) -> Unit,
      modifier: Modifier = Modifier
  )
  ```

### 6. `UnifiedModal`
- **Specification:** Modal dialog system with three variants: Create Board, Login, and Business Promo.
- **Visual Styles:** Backdrop blur (4px), spring entrance animation (cubic-bezier 0.34,1.56,0.64,1), 32dp border radius, max-width 480dp (680dp for business variant with split layout).
- **Variants:**
  - **Create Board:** Form input, toggle switch, collaborator invite row.
  - **Login:** Email/password fields, Google SSO button, QR code section.
  - **Business Promo:** Split layout (navy gradient left + content right), feature checklist.
- **Compose API:**
  ```kotlin
  @Composable
  fun UnifiedModal(
      variant: ModalVariant,
      isOpen: Boolean,
      onDismiss: () -> Unit,
      modifier: Modifier = Modifier
  )
  ```

### 7. `UnifiedToast`
- **Specification:** Spring-animated bottom-left toast notification.
- **Visual Styles:** Black background (#0D0D0D), white text, 16dp border radius, pink dot icon, spring entrance (cubic-bezier 0.175,0.885,0.32,1.275), 2.5s auto-dismiss.
- **Compose API:**
  ```kotlin
  @Composable
  fun UnifiedToast(
      message: String,
      type: ToastType = ToastType.INFO,
      isVisible: Boolean,
      onDismiss: () -> Unit
  )
  ```

### 8. `TrendsTable`
- **Specification:** Search trends analytics table with sparkline charts and volume bars.
- **Visual Styles:** Rounded table (16dp), uppercase header labels, sparkline SVG charts (pink stroke, 2px), volume bar fills (pink), growth percentage (green).
- **Compose API:**
  ```kotlin
  @Composable
  fun TrendsTable(
      trends: List<TrendData>,
      modifier: Modifier = Modifier
  )
  ```

### 9. `CollageCard`
- **Specification:** Image grid card with floating sticker overlay.
- **Visual Styles:** 1.2fr/1fr image grid, 220dp height, floating pink sticker (rotated -4deg), bold title, muted description.
- **Compose API:**
  ```kotlin
  @Composable
  fun CollageCard(
      mainImage: ImageBitmap,
      subImages: List<ImageBitmap>,
      stickerText: String,
      title: String,
      description: String,
      modifier: Modifier = Modifier
  )
  ```

### 10. `BlogHero`
- **Specification:** Blog/news hero section with floating graphic overlays and parallax hover.
- **Visual Styles:** Light pastel blue background (#E0F2FE), 32dp border radius, 48px bold title, floating graphics (headphones, watch) with rotate transforms, hover parallax effect.
- **Compose API:**
  ```kotlin
  @Composable
  fun BlogHero(
      tag: String,
      title: String,
      graphics: List<ImageBitmap>,
      modifier: Modifier = Modifier
  )
  ```

### 11. `AboutHero`
- **Specification:** Landing page hero with massive typography and inline pill badges.
- **Visual Styles:** 52px clamp font size, 900 weight, -0.04em letter-spacing, inline badges (pill-shaped, 15px font, pink icon variant), centered CTA row.
- **Compose API:**
  ```kotlin
  @Composable
  fun AboutHero(
      heading: String,
      badges: List<HeadingBadge>,
      primaryCta: String,
      secondaryCta: String,
      modifier: Modifier = Modifier
  )
  ```

---

## 📱 2. Specialized Functional Widgets & Structural Blocks

### Functional Widgets Specification

1. **`AudioWaveformPlayerWidget`:** Animated audio stem preview player displaying custom real-time audio visualisation bars driven by `rememberInfiniteTransition()`. Integrates play/pause triggers, track timeline sliders, and duration.
2. **`StudioSlotPickerWidget`:** Hourly schedule matrix helping users review, select, and book Recording Studio blocks with touch selections.
3. **`ArtistSynergyScoreWidget`:** Circular progress arc depicting the Gemini AI Matchmaking score badge and summarised matching metrics.
4. **`VerifiedBadgeWidget`:** Verified Industry Passport checkmark emblem to display on authenticated creative accounts.
5. **`SearchAndFilterHeaderWidget`:** Full-width filled search bar with clear buttons and a horizontally scrollable row of genre category chips.
6. **`MasonrySaveOverlayWidget`:** Hover-activated overlay with Save button, action row (play preview, share), and gradient wash. Extracted from Pinterest Fluid UI.
7. **`ToastNotificationWidget`:** Spring-animated bottom-left toast with pink dot icon, black background, auto-dismiss. Extracted from Pinterest Fluid UI.
8. **`ModalSystemWidget`:** Multi-variant modal (Create Board, Login, Business Promo) with backdrop blur, spring entrance, and variant-specific layouts. Extracted from Pinterest Fluid UI.

### Structural Layout Blocks

1. **`ArtistProfileHeroBlock`:** Prominent profile workspace grouping circular user avatars, verified badges, locations, role sub-headers, and core quick action targets (Connect / Message).
2. **`StudioSuiteSpecBlock`:** Structured studio specs card highlighting spatial dimensions, equipment configurations (SSL consoles, classic microphones), and hourly rates.
3. **`AudioStemShowcaseBlock`:** Segmented container listing available STEM tracks with integrated audio previews.
4. **`MediaGalleryGridBlock`:** Horizontally scrollable row containing portfolio images with rounded corners (`12.dp`).
5. **`BlogHeroBlock`:** Pastel blue hero with floating graphic overlays, parallax hover, and bold typography. Extracted from Pinterest Fluid UI.
6. **`AboutLandingBlock`:** Massive heading with inline pill badges, centered CTAs. Extracted from Pinterest Fluid UI.
7. **`TrendsAnalyticsBlock`:** Search trends table with sparkline charts, volume bars, and growth percentages. Extracted from Pinterest Fluid UI.
8. **`CollageCardBlock`:** Image grid with floating sticker overlay, bold title, and description. Extracted from Pinterest Fluid UI.

### Layout Templates

1. **Form Screen Template:** Standard multi-step inputs grouping clear heading labels, filled input fields with system-guided borders, and primary CTA buttons pinned to the screen bottom.
2. **Modal Sheet Template:** Rounded bottom sheet featuring a top drag indicator handle, close button, nested scrollable content, and primary actions.
3. **Dashboard Tab Screen Template:** `Scaffold` container containing a top search workspace, scrollable `LazyColumn` for content, a persistent player container, and a bottom navigation bar.
4. **Full-Page Portfolio Template:** Cover hero art, overlay avatar, action rows, and nested tab structures (Bio, STEMs, Gear, Reviews).
5. **Masonry Discovery Template:** Variable-height card grid with hover overlays, responsive column count (4→3→2), save/share actions. Extracted from Pinterest Fluid UI.
6. **Modal System Template:** Backdrop blur overlay with spring-animated card, close button, variant-specific content (Create/Login/Business). Extracted from Pinterest Fluid UI.

---

## 🎬 3. Motion, Transitions, & Animations Architecture

- **Visual Transitions:** All screen changes within the navigation graph use custom slide-in horizontal actions paired with quick fades.
- **Waveform Visualization:** Equalizer bar animations in `AudioWaveformPlayer` animate heights dynamically using `rememberInfiniteTransition()`.
- **Micro-interactions:** Toggling connection status triggers scale bounces (1.0 to 1.15 to 1.0) on button shapes to establish tactile feedback.
- **Masonry Hover:** Cards lift -4px translateY with 200ms cubic-bezier(0.4,0,0.2,1), images scale 1.06x over 600ms.
- **Modal Entrance:** Backdrop fades in 300ms, card scales from 0.9 to 1.0 with spring physics (cubic-bezier 0.34,1.56,0.64,1).
- **Toast Entrance:** Slides up from translateY(100px) with spring easing (cubic-bezier 0.175,0.885,0.32,1.275), auto-dismisses after 2.5s.
- **Blog Hero Parallax:** Floating graphics rotate and scale on parent hover (0.5s ease transition).
- **Float Animation:** Elements bob translateY(-8px) on 3s infinite ease-in-out cycle with staggered delays.

---

## 📐 4. Card & Block Height Constraints (Breakpoint-Mapped)

All cards and blocks follow these min/max height rules mapped to the 4-breakpoint grid:

| Component | Min Height | Max Height (Mobile) | Max Height (Tablet) | Max Height (Desktop) | Max Height (Wide) | Overflow |
|-----------|-----------|---------------------|---------------------|----------------------|-------------------|----------|
| Card (.card-s) | 120dp | 480dp | 520dp | 600dp | 680dp | hidden |
| Masonry Item | auto | 420dp | 460dp | 520dp | 580dp | hidden |
| Spec Card | 100dp | 360dp | 400dp | 480dp | 560dp | scroll |
| Visual Demo | 80dp | 600dp | none | none | none | auto |
| Phone Frame Screen | 500dp | 700dp | 700dp | 700dp | 700dp | scroll |

**Breakpoint Reference:**
- Mobile: 0–767px (360dp viewport)
- Tablet: 768–1023px (iPad, foldables)
- Desktop: 1024–1439px (Laptops)
- Wide: 1440px+ (Large monitors)

---

## 🎨 5. Visual Effects & Texture Utilities

CSS utility classes available for all components (extracted from Pinterest Fluid UI and enhanced):

| Class | Effect | Usage |
|-------|--------|-------|
| `.glass` | White glassmorphism (backdrop-blur 16px) | Modal backgrounds, overlay cards |
| `.glass-dark` | Dark glassmorphism (backdrop-blur 16px) | Dark theme overlays |
| `.glow-pink` | Pink glow shadow (40px + 80px) | Featured cards, CTAs |
| `.glow-black` | Black glow shadow (40px + 80px) | Dark cards |
| `.gradient-text` | Pink-to-black gradient text clip | Hero headlines |
| `.gradient-card` | Pink-to-black gradient background | Feature cards |
| `.gradient-card-purple` | Purple-to-black gradient background | Pro/business cards |
| `.gradient-card-green` | Green-to-black gradient background | Success/verified cards |
| `.bg-dots` | Dot matrix pattern (16px) | Subtle surface texture |
| `.bg-grid` | Grid mesh pattern (24px) | Technical/studio surfaces |
| `.bg-noise` | Fractal noise overlay (3% opacity) | Organic texture |
| `.texture-paper` | Paper texture SVG pattern | Editorial surfaces |
| `.ring-pink` | Pink ring shadow (4px) | Selected/focused states |
| `.ring-black` | Black ring shadow (4px) | Dark selected states |
| `.float` | Float animation (3s infinite) | Decorative elements |
| `.pulse-ring` | Pulsing ring animation (2s) | Live/active indicators |
| `.shimmer-text` | Shimmer gradient text (3s) | Loading/featured text |