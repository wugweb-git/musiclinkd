# Error Catalogue & Resilience Reference — MusicLinkd

## Standardized Error Codes & UI Fallbacks

All runtime exceptions across `app.musiclinkd.com` map to standardized codes displayed in `UnifiedErrorCard`:

| Error Code | Category | User-Facing Message | Automatic Resolution Strategy |
| :--- | :--- | :--- | :--- |
| `AUTH_FAILED` | Authentication | "Sign-in failed. Please check credentials or retry." | Offers retry button; preserves guest session state. |
| `SNAPSHOT_CREATION_FAILED` | User Sync | "Profile snapshot sync failed. Local profile loaded." | Fallback to `DemoDataProvider` local user snapshot. |
| `UPLOAD_FAILED` | Firebase Storage | "Media upload interrupted. Please check network." | Prompts user to retry upload; retains local file URI. |
| `BOOKING_FAILED` | Studio | "Selected studio slot unavailable. Please choose another slot." | Refreshes live slot availability from repository. |
| `GEMINI_API_TIMEOUT` | AI Engine | "AI synergy analysis temporarily offline. Showing default score." | Calculates fallback heuristic synergy score. |
| `OFFLINE_NO_NETWORK` | Connectivity | "You are currently offline. Viewing cached profiles." | Switches to Room SQLite offline cached mode. |
