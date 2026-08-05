# Widget Catalogue — MusicLinkd Specialized Composables

## Specialized Widgets Specification

### 1. `AudioWaveformPlayer`
- **Purpose:** Interactive audio player widget displaying animated equalizer bars and progress scrubbing.
- **Props:** `track: DemoTrack`, `isPlaying: Boolean`, `onTogglePlay: () -> Unit`

### 2. `StudioSlotPickerWidget`
- **Purpose:** Hourly recording studio slot selector widget.
- **Props:** `selectedDate: String`, `availableSlots: List<String>`, `onSlotSelected: (String) -> Unit`

### 3. `ArtistSynergyScoreWidget`
- **Purpose:** Gemini AI collaboration compatibility score badge with circular progress indicator.
- **Props:** `synergyScore: Int`, `synergyReason: String`

### 4. `VerifiedBadgeWidget`
- **Purpose:** Verified artist and gold studio status badge with checkmark vector.
- **Props:** `isGold: Boolean = false`
