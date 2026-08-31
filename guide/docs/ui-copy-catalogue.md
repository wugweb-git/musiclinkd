# UI copy catalogue — rescued from `refactor/hardcoded-content`

**Rescued 2026-08-31**, immediately before that branch was deleted. It is the
last thing on it that this repository did not already have.

## Why this file exists

`refactor/hardcoded-content` was a stale branch: 6 commits, 115 behind `main`,
last touched 2026-08-12, and every code change it carried edited
`app/app/src/main/java/…` — an Android tree removed from this repository in
`1620e46`. Its audit document was already rescued onto `main` via
`recover/hardcoded-content-audit`. Deleting the branch was correct; deleting
**this** would have thrown away real work.

`strings.xml` held **72 strings**, extracted here as 71.
One is deliberately omitted: `default_web_client_id` is an OAuth client
identifier, not UI copy, and does not belong in a documentation file.

## Still hardcoded in `webapp/` today — 23

Each of these appears verbatim in a built screen right now. They are the
concrete case for a copy layer: the same words are typed into generated HTML
and were, separately, centralised on a branch nobody merged.

| Key | String |
|---|---|
| `app_name` | MusicLinkd |
| `nav_home` | Home |
| `nav_discover` | Discover |
| `nav_connections` | Connections |
| `nav_market` | Market |
| `nav_messages` | Messages |
| `nav_studios` | Studios |
| `nav_portfolio` | Portfolio |
| `nav_profile` | Profile |
| `action_search` | Search |
| `action_clear` | Clear |
| `action_share` | Share |
| `action_back` | Back |
| `action_remove` | Remove |
| `action_save` | Save |
| `action_retry` | Retry |
| `discover_recommendations` | Recommendations |
| `discover_search_results` | Search Results |
| `hub_connections` | Connections |
| `hub_feed` | Feed |
| `hub_search` | Search |
| `hub_saved` | Saved |
| `hub_settings` | Settings |

## Not currently in `webapp/` — 48

Copy from the Android app for screens the PWA has not built, or has worded
differently. Kept because it is the product’s own voice, decided once and
otherwise lost with the branch.

| Key | String |
|---|---|
| `nav_tab_selected` | %1$s navigation tab, selected |
| `nav_tab_unselected` | %1$s navigation tab, unselected |
| `action_clear_all` | Clear All |
| `discover_subtitle` | Connect with India’s Music Grid |
| `discover_search_placeholder` | Search talent, studios, bands... |
| `discover_hot_match` | Swipe n' Match |
| `discover_hot_match_description` | Discover collaborators game-show style! |
| `discover_hot_match_action` | Hot-Match |
| `discover_quick_categories` | Quick Categories |
| `discover_see_all` | See All |
| `discover_featured_professionals` | Featured Professionals |
| `discover_trending_bands` | Trending Independent Bands |
| `discover_nearby_studios` | Nearby Studios & Venues |
| `discover_saved_profiles` | Saved Profiles |
| `search_artists_studios_instruments` | Search artists, studios, instruments... |
| `search_suggested_categories` | Suggested Categories |
| `search_recent` | Recent Searches |
| `search_trending` | Trending Searches |
| `success_welcome` | Welcome to the Grid. |
| `success_description` | Your digital profile is live on India’s premier music professional registry. |
| `verified_professional` | Verified Professional |
| `success_scan_record` | Scan to view digital registry record |
| `success_launch_dashboard` | Launch My Registry Dashboard |
| `success_share_pass` | Share My Digital Pass |
| `default_profile_name` | Your MusicLinkd Profile |
| `default_profile_role` | Music Professional |
| `hub_title` | MusicLinkd Hub |
| `hub_supporting_flows` | SUPPORTING FLOWS |
| `hub_artist_profile` | Artist Profile |
| `hub_qr_share` | QR & Share |
| `hub_help_support` | Help & Support |
| `role_musician` | Musician / Singer |
| `role_producer` | Music Producer |
| `role_engineer` | Sound Engineer |
| `role_band` | Band / Group |
| `role_studio` | Recording Studio |
| `role_venue` | Venue / Organizer |
| `headshot_title` | Update Profile Headshot |
| `headshot_desc` | Capture a professional headshot with camera or pick from gallery. |
| `action_take_photo` | Take Photo |
| `action_pick_gallery` | Pick Gallery |
| `camera_permission_required` | Camera permission is required to capture headshot. |
| `headshot_success` | Headshot captured and set successfully! |
| `btn_book_now` | Book / Hire Talent |
| `btn_reserve_studio` | Reserve Studio Slot |
| `btn_book_venue` | Book Venue Space |
| `btn_send_message` | Send Message |
| `btn_save_profile` | Save Profile |

## What this is not

**Not infrastructure.** Nothing consumes this file, deliberately. Building a
copy module belongs to the deferred screen-contract pass recorded in
`docs/09-Roadmap/screen-contract-chain.md` — which also notes that
`screen.json` carries no `actions` or `permissions` field yet. Creating an
unused catalogue now would repeat the pattern this repository has been removing
all week: a vocabulary defined with no consumers.

Two entries carry a format argument — `nav_tab_selected` and
`nav_tab_unselected`, both `%1$s` — and are state-aware accessibility copy
("Home navigation tab, selected"). The web screens announce tab state through
`aria-selected` instead, so these are a reference for the native apps rather
than something to port.
