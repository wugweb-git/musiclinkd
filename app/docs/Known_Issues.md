# Known Issues & Technical Debt Register

## Known Issues Log

1. **Simulated Audio Streaming in Local Emulator Mode:**
   - *Behavior:* When testing without external network access, ExoPlayer plays local sample audio files (`SoundHelix`).
   - *Impact:* Low. Live production streaming activates automatically when valid audio URLs are passed.

2. **Google OAuth Client Credentials:**
   - *Behavior:* Requires production Cloud Project Client ID configuration in AI Studio Secrets panel for production deployment.
   - *Impact:* Medium. Offline local authentication fallback is active for local testing.

## Technical Debt Roadmap
- Refactor remaining large screen files into modular smaller Composables (< 500 lines).
- Expand Roborazzi screenshot coverage for studio booking screens.
