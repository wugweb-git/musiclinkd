# Analytics Event Taxonomy — MusicLinkd

## Telemetry & Analytics Taxonomy

All analytics events on `app.musiclinkd.com` follow the structured schema `{category}_{action}`:

| Event Name | Category | Parameters Captured | User Trigger |
| :--- | :--- | :--- | :--- |
| `auth_sign_in_success` | Auth | `method` ("GOOGLE", "EMAIL"), `user_id` | User completes sign-in |
| `onboarding_role_selected` | Onboarding | `role` ("ARTIST", "PRODUCER", "STUDIO") | Role survey completed |
| `discover_search_executed` | Discovery | `query`, `genre_filter`, `results_count` | User types in search bar |
| `artist_profile_viewed` | Portfolio | `artist_id`, `artist_name`, `source` | Clicked artist card |
| `stem_audio_play` | Audio | `track_id`, `track_title`, `artist_id` | Clicked play on waveform |
| `connection_request_sent` | Social | `sender_id`, `target_user_id` | Clicked "Connect" button |
| `studio_booking_created` | Marketplace | `studio_id`, `slot_time`, `amount` | Submitted booking request |
| `media_attachment_uploaded` | Portfolio | `file_type` ("AUDIO", "IMAGE"), `size_kb` | File upload completed |
| `error_boundary_triggered` | Resiliency | `error_code`, `screen_name` | Error UI card shown |
