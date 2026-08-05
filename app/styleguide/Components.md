# MusicLinkd Styleguide — Components & Widgets

## Recycled Design Primitives in `UnifiedComponents.kt`

### 1. Unified Base Card Container
- **Shape:** `RoundedCornerShape(24.dp)`
- **Border:** `BorderStroke(1.dp, BrandBorder)`
- **Background:** `Color.White`
- **Usage:** Artist cards, studio cards, track cards, feed post cards, review cards.

### 2. Unified Primary Button
- **Shape:** Pill shape (`RoundedCornerShape(999.dp)` or `RoundedCornerShape(16.dp)`)
- **Background:** `BrandPrimary` (`#EA4C89`)
- **Text Color:** `Color.White` with `FontWeight.Bold`
- **Touch Target:** Minimum height `48.dp`

### 3. Unified Connect Button (`UnifiedConnectButton`)
- **States:**
  - `IDLE`: Solid Pink background (`"Connect"` / `"Audition"`)
  - `PENDING`: Soft grey background with checkmark icon (`"Requested"`)
  - `CONNECTED`: Light green background (`"Connected"`)

### 4. Error Handling Boundary Card (`UnifiedErrorCard`)
- **Shape:** `RoundedCornerShape(24.dp)`
- **Border:** `BorderStroke(1.dp, Color(0xFFEF4444))`
- **Icon:** Warning circle icon with retry button.
