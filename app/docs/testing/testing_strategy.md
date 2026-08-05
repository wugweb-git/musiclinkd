# Testing Strategy & Automated QA — MusicLinkd

## Testing Strategy

- **Local Unit Tests:** JVM unit testing for ViewModels and Data Providers (`gradle :app:testDebugUnitTest`).
- **Robolectric & Roborazzi:** Screenshot verification and CUJ UI flow tests without emulators.
- **Manual QA Matrix:** End-to-end user flow verification across `app.musiclinkd.com`.
