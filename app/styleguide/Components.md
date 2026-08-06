# MusicLinkd Design System — Components & Layouts (`styleguide/Components.md`)

This is the single authoritative source of truth for the **MusicLinkd Components, Widgets, Layout Templates, and Motion/Transitions Specifications** across the public website (`musiclinkd.com`), the mobile and web application (`app.musiclinkd.com`), and the administration control center (`admin.musiclinkd.com`).

---

## 🧩 1. Reusable Components Catalog (`UnifiedComponents.kt`)

All layouts across the application must draw components directly from the unified styleguide catalog to guarantee strict design parity:

### 1. `UnifiedBaseCard`
- **Specification:** Clean, structural border container.
- **Visual Styles:** Pure white background canvas (`#FFFFFF`), a mandatory solid `1.dp` border stroke in `BrandBorder` (`#ECECF1`), and a uniform corner radius of `24.dp`. Drop shadows are completely suppressed.
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

---

## 📱 2. Specialized Functional Widgets & Structural Blocks

### Functional Widgets Specification

1. **`AudioWaveformPlayerWidget`:** Animated audio stem preview player displaying custom real-time audio visualization bars driven by `rememberInfiniteTransition()`. Integrates play/pause triggers, track timeline sliders, and duration.
2. **`StudioSlotPickerWidget`:** Hourly schedule matrix helping users review, select, and book Recording Studio blocks with touch selections.
3. **`ArtistSynergyScoreWidget`:** Circular progress arc depicting the Gemini AI Matchmaking score badge and summarized matching metrics.
4. **`VerifiedBadgeWidget`:** Verified Industry Passport checkmark emblem to display on authenticated creative accounts.
5. **`SearchAndFilterHeaderWidget`:** Full-width filled search bar with clear buttons and a horizontally scrollable row of genre category chips.

### Structural Layout Blocks

1. **`ArtistProfileHeroBlock`:** Prominent profile workspace grouping circular user avatars, verified badges, locations, role sub-headers, and core quick action targets (Connect / Message).
2. **`StudioSuiteSpecBlock`:** Structured studio specs card highlighting spatial dimensions, equipment configurations (SSL consoles, classic microphones), and hourly rates.
3. **`AudioStemShowcaseBlock`:** Segmented container listing available STEM tracks with integrated audio previews.
4. **`MediaGalleryGridBlock`:** Horizontally scrollable row containing portfolio images with rounded corners (`12.dp`).

### Layout Templates

1. **Form Screen Template:** Standard multi-step inputs grouping clear heading labels, filled input fields with system-guided borders, and primary CTA buttons pinned to the screen bottom.
2. **Modal Sheet Template:** Rounded bottom sheet featuring a top drag indicator handle, close button, nested scrollable content, and primary actions.
3. **Dashboard Tab Screen Template:** `Scaffold` container containing a top search workspace, scrollable `LazyColumn` for content, a persistent player container, and a bottom navigation bar.
4. **Full-Page Portfolio Template:** Cover hero art, overlay avatar, action rows, and nested tab structures (Bio, STEMs, Gear, Reviews).

---

## 🎬 3. Motion, Transitions, & Animations Architecture

- **Visual Transitions:** All screen changes within the navigation graph use custom slide-in horizontal actions paired with quick fades.
- **Waveform Visualization:** Equalizer bar animations in `AudioWaveformPlayer` animate heights dynamically using `rememberInfiniteTransition()`.
- **Micro-interactions:** Toggling connection status triggers scale bounces (1.0 to 1.15 to 1.0) on button shapes to establish tactile feedback.
