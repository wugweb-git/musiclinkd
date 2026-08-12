# MusicLinkd — Official Audited Widget Directory

Pasted verbatim by the user (2026-08-11). Built into the admin panel as of
2026-08-12: `admin/app/(admin)/widgets/` (list page, editor sheet,
`DEFAULT_WIDGETS` seed transcribed verbatim from this table), `WidgetDoc`
in `admin/types/firestore.ts`, `widgetsRepo` in
`admin/services/repositories/index.ts`, `COLLECTIONS.widgets` +
permissions in `admin/config/constants.ts`, the nav item in
`admin/config/navigation.ts`, and the `/widgets/{widgetId}` match block in
`firestore.rules` (moderator-read, admin-write — same shape as `/flows`).
Preserve this table exactly; don't paraphrase when editing the real
registry from it.

The user notes: **many of these widgets are currently hidden from the
app's UI on purpose**, and wants that hidden/visible state tracked in the
admin panel alongside the rest of this directory.

## 1. Discovery & Collaboration

| Widget Name | Purpose / Use Case | Content / Composition | Code Implementation Mapping |
|---|---|---|---|
| ArtistCard | Talent discovery and artist showcase | Avatar, name, role/instrument, location, rating, verification, connect CTA | UnifiedArtistCard / ArtistCard |
| RecommendedCollaboratorCard | AI-powered collaborator matching card | Cover/avatar, match %, synergy reasons, skills, collaboration actions | RecommendedCollaboratorCard |
| DiscoverySearchResultCard | Global discovery and search result item | Profile/entity identity, category, genres, location, match data, preview action | DiscoverySearchResultCard |
| ProfilePreviewSheet | Quick profile inspection modal sheet | Profile summary, bio, top tracks, gear/skills, full-profile CTA button | ProfilePreviewSheet |
| NetworkingFeed | Social/network activity feed list widget | User header, text, media/audio attachment preview, likes, comments, sharing | NetworkingFeed |
| BandMembersWidget | Band & performing crew roster display | Member avatars, instrument role tags, lead-member designation | BandMembersWidget |
| CollaboratorMatchWidget | Collection of AI-recommended collaborators | Horizontal/vertical cards, match scores, synergy information, actions | RecommendedCollaboratorsSection |

## 2. Studios & Venues

| Widget Name | Purpose / Use Case | Content / Composition | Code Implementation Mapping |
|---|---|---|---|
| StudioCard | Standard studio discovery and booking card | Studio imagery, room name, location, Dolby Atmos badge, rating, hourly rate | UnifiedStudioCard |
| StudioVenueListItem | Compact studio/venue directory list item | Thumbnail, venue name, location, rating, pricing, Atmos indicator badge | StudioVenueListItem |
| FeaturedStudioCard | Hero studio promotion banner card | Large photography, studio/room identity, Atmos spec, pricing, booking CTA | FeaturedStudioCard |
| StudioSpecWidget | Detailed studio technical specs widget | Outboard gear, console models (SSL 9000J), monitors, microphone inventory | StudioSpecWidget |

## 3. Marketplace & Opportunities

| Widget Name | Purpose / Use Case | Content / Composition | Code Implementation Mapping |
|---|---|---|---|
| MarketplaceCard | Equipment rental and service discovery card | Product/gear photo, name, rental rate, seller rating, availability tag | UnifiedMarketplaceCard |
| OpportunityCard | Gigs, jobs, auditions & collaboration card | Project title, creator/role, budget tag, required skills, applicant count, deadline | UnifiedOpportunityCard |

## 4. Portfolio & Projects

| Widget Name | Purpose / Use Case | Content / Composition | Code Implementation Mapping |
|---|---|---|---|
| ProjectCard | Public portfolio & release presentation card | Cover artwork, project title, release date, status badge, collaborators & credits | UnifiedProjectCard |
| RecentProjectCard | Compact dashboard portfolio card | Artwork, track/project name, role, release year, streaming links, edit action | RecentProjectCard |

## 5. Media

| Widget Name | Purpose / Use Case | Content / Composition | Code Implementation Mapping |
|---|---|---|---|
| TrackCard | Music & track release discovery item | Artwork thumbnail, title, duration, platform badges (Spotify/YouTube), play count | UnifiedTrackCard |
| AudioPlayerWidget | Dedicated embedded audio playback widget | Waveform visualizer, play/pause, timeline slider, duration counter | WaveformAudioPlayer |
| PersistentAudioPlayer | Global sticky audio playback bar | Mini artwork, track title, play/pause controls, progress bar, close trigger | GlobalPersistentAudioPlayerBar |
| AudioPodcastDemoWidget | In-line audio demo player in dashboards | Audio identity, waveform controls, duration display, playback state | AudioPodcastDemoWidget |

Audio architecture unification, per the user's own note: `AudioPlayerWidget`
maps directly to `WaveformAudioPlayer` as its implementation, and
`PersistentAudioPlayer` maps directly to `GlobalPersistentAudioPlayerBar`.

## 6. Bookings & Hiring

| Widget Name | Purpose / Use Case | Content / Composition | Code Implementation Mapping |
|---|---|---|---|
| BookingCard | Studio session booking record card | Studio name, date/time slot, room type, total price, booking status tag | UnifiedBookingCard |
| HireContactModal | Direct hiring & booking inquiry modal | Project scope options, proposed date picker, budget field, inquiry submission CTA | HireContactModal |

## 7. Reputation & Activity

| Widget Name | Purpose / Use Case | Content / Composition | Code Implementation Mapping |
|---|---|---|---|
| PublicReputationWidget | Public trust and endorsement presentation | Aggregated rating score, verified credits count, client reviews, response rate | PublicReputationWidget |
| ReviewCard | Individual client review/endorsement card | Reviewer identity, organization, rating star row, review quote, timestamp | UnifiedReviewCard |
| NotificationCard | Notification and activity item card | Actor avatar, event description, event type icon, timestamp | UnifiedNotificationCard |

## 8. Search, Filters & Sharing

| Widget Name | Purpose / Use Case | Content / Composition | Code Implementation Mapping |
|---|---|---|---|
| AdvancedFiltersBottomSheet | Advanced discovery filtering bottom sheet | Distance/budget sliders, category toggles, Dolby Atmos switch, apply CTA | AdvancedFiltersBottomSheet |
| WhatsAppShareSheet | External profile & project sharing sheet | Share preview card, pre-populated message text, WhatsApp share action trigger | WhatsAppShareSheet |

## 9. Home & Featured Content

| Widget Name | Purpose / Use Case | Content / Composition | Code Implementation Mapping |
|---|---|---|---|
| HomeFeatureCard | Hero featured content on main feed | Featured artist, studio, track, playlist or promotion with imagery and CTA | HomeFeatureCard |

## Cross-reference to this session's earlier audits

Several of these code-mapping names were already touched independently
this session, under different framing — worth reconciling when the real
`widgets` collection gets built, not just appending blindly:
- `NetworkingFeed` — audited in depth already (Flow 6 "Communication",
  screen `networking_feed`) — real Firestore read/write exists but with
  field-name divergence from the admin schema (`content` vs `text`,
  `mediaUrl` mismatch). This widget's "Code Implementation Mapping" is the
  same file already flagged in Prompt F §5.
- `RecommendedCollaboratorsSection` / `CollaboratorMatchWidget` — already
  found hidden (call site commented out) in the Discover audit (Flow 4).
- `AdvancedFiltersBottomSheet` — already documented in the Discover audit
  (Flow 4, `advanced_filters` screen) as filtering 100% mock data.
- `GlobalPersistentAudioPlayerBar` — named in this session's earlier dead-
  code audit (`onboarding/ui/components/GlobalPersistentAudioPlayerBar.kt`)
  as one of the files deleted per `git status` at the start of this
  session (shows as `D` in the repo's working tree) — confirm this file
  still exists before assuming the widget is live; it may have been
  removed already.
- `WhatsAppShareSheet` — likely relates to the "Options" screen's per-
  channel share buttons found to be UI-only fakes in an earlier QR/Share
  audit (`SupportingScreens.kt:924-969`, `showSnackbar` only, no real
  WhatsApp intent). Verify before assuming this one is real and just
  hidden, vs. fake and hidden.
