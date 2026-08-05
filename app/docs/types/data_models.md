# Data Models & Types — MusicLinkd

## Data Models (`com.example.onboarding.util.DemoDataProvider` & `models`)

### `DemoArtist`
- `id`: String
- `name`: String
- `role`: String
- `location`: String
- `isVerified`: Boolean
- `isAvailable`: Boolean
- `rating`: Double
- `reviewsCount`: Int
- `hourlyRate`: String
- `genres`: List<String>
- `avatarUrl`: String
- `bio`: String
- `primaryInstrument`: String

### `DemoStudio`
- `id`: String
- `name`: String
- `location`: String
- `rating`: Double
- `reviewCount`: Int
- `hourlyRate`: String
- `verifiedGold`: Boolean
- `amenities`: List<String>
- `imageUrl`: String
- `consoleType`: String

### `MediaAttachmentItem`
- `id`: String
- `fileName`: String
- `type`: String ("AUDIO" or "IMAGE")
- `url`: String
- `sizeOrDuration`: String
