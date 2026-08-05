# Known Issues & Technical Debt Register — MusicLinkd

## Known Issues

1. **ExoPlayer Offline Sample Playback:**
   - *Behavior:* Uses local `SoundHelix` MP3 links when external network streams are unreachable.
   - *Status:* Intended fallback behavior.

2. **Google OAuth Client Credentials:**
   - *Behavior:* OAuth client ID requires configuration in AI Studio Secrets panel for production deployment.
   - *Status:* Graceful offline fallback enabled.
