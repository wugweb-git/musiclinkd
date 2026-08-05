# Domain Models Specification — MusicLinkd

## Core Data Models (`com.example.onboarding.models` & `DemoDataProvider`)

- `DemoArtist`: Immutable artist profile containing avatar URL, primary instrument, rating, hourly rate, and bio.
- `DemoStudio`: Recording studio representation with console type, Dolby Atmos status, and hourly rates.
- `DemoTrack`: Audio track STEM item with audio sample URL, duration text, and cover thumbnail.
- `DemoFeedPost`: Social update post item containing post type (`COLLAB_CALL`, `RELEASE`, `STUDIO_UPDATE`), content, timestamp, and likes count.
- `MediaAttachmentItem`: Audio or image attachment item uploaded via Firebase Storage.
