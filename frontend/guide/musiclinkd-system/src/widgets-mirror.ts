// Read-only mirror of admin/app/(admin)/widgets/default-widgets.ts, itself
// transcribed verbatim from admin/docs/architecture/Widget_Directory.md —
// the home/dashboard content-composition widgets used by the admin panel's
// widget editor (WidgetDoc in admin/types/firestore.ts, widgetsRepo, the
// /widgets/{widgetId} Firestore rules block).
//
// This is a REFERENCE, not the source of truth: edit widget behavior in the
// admin app, then re-transcribe this file to match — don't edit behavior
// here. Kept verbatim (purpose/contentComposition/codeMapping text is not
// paraphrased) per the source doc's own instruction to itself.
//
// Last transcribed: 2026-08-16, from the admin app as it existed then.

export type WidgetEntry = {
  key: string;
  order: number;
  name: string;
  category: string;
  purpose: string;
  contentComposition: string;
  codeMapping: string;
  isVisible?: boolean; // undefined = not confirmed either way in the source doc
  notes?: string;
};

export const WIDGET_CATEGORIES_ORDER = [
  "Discovery & Collaboration",
  "Studios & Venues",
  "Marketplace & Opportunities",
  "Portfolio & Projects",
  "Media",
  "Bookings & Hiring",
  "Reputation & Activity",
  "Search, Filters & Sharing",
  "Home & Featured Content",
];

export const WIDGETS: WidgetEntry[] = [
  // 1. Discovery & Collaboration
  {
    key: "artist_card",
    order: 1,
    name: "ArtistCard",
    category: "Discovery & Collaboration",
    purpose: "Talent discovery and artist showcase",
    contentComposition: "Avatar, name, role/instrument, location, rating, verification, connect CTA",
    codeMapping: "UnifiedArtistCard / ArtistCard",
  },
  {
    key: "recommended_collaborator_card",
    order: 2,
    name: "RecommendedCollaboratorCard",
    category: "Discovery & Collaboration",
    purpose: "AI-powered collaborator matching card",
    contentComposition: "Cover/avatar, match %, synergy reasons, skills, collaboration actions",
    codeMapping: "RecommendedCollaboratorCard",
  },
  {
    key: "discovery_search_result_card",
    order: 3,
    name: "DiscoverySearchResultCard",
    category: "Discovery & Collaboration",
    purpose: "Global discovery and search result item",
    contentComposition: "Profile/entity identity, category, genres, location, match data, preview action",
    codeMapping: "DiscoverySearchResultCard",
  },
  {
    key: "profile_preview_sheet",
    order: 4,
    name: "ProfilePreviewSheet",
    category: "Discovery & Collaboration",
    purpose: "Quick profile inspection modal sheet",
    contentComposition: "Profile summary, bio, top tracks, gear/skills, full-profile CTA button",
    codeMapping: "ProfilePreviewSheet",
  },
  {
    key: "networking_feed",
    order: 5,
    name: "NetworkingFeed",
    category: "Discovery & Collaboration",
    purpose: "Social/network activity feed list widget",
    contentComposition: "User header, text, media/audio attachment preview, likes, comments, sharing",
    codeMapping: "NetworkingFeed",
    notes:
      'Already audited (Flow 6 "Communication", screen networking_feed) — real Firestore read/write exists but with field-name divergence from the admin schema (content vs text, mediaUrl mismatch). Not a visibility question so much as a data-contract bug; isVisible intentionally left unset.',
  },
  {
    key: "band_members_widget",
    order: 6,
    name: "BandMembersWidget",
    category: "Discovery & Collaboration",
    purpose: "Band & performing crew roster display",
    contentComposition: "Member avatars, instrument role tags, lead-member designation",
    codeMapping: "BandMembersWidget",
  },
  {
    key: "collaborator_match_widget",
    order: 7,
    name: "CollaboratorMatchWidget",
    category: "Discovery & Collaboration",
    purpose: "Collection of AI-recommended collaborators",
    contentComposition: "Horizontal/vertical cards, match scores, synergy information, actions",
    codeMapping: "RecommendedCollaboratorsSection",
    isVisible: false,
    notes: "Confirmed hidden in the Discover audit (Flow 4) — call site commented out.",
  },

  // 2. Studios & Venues
  {
    key: "studio_card",
    order: 8,
    name: "StudioCard",
    category: "Studios & Venues",
    purpose: "Standard studio discovery and booking card",
    contentComposition: "Studio imagery, room name, location, Dolby Atmos badge, rating, hourly rate",
    codeMapping: "UnifiedStudioCard",
  },
  {
    key: "studio_venue_list_item",
    order: 9,
    name: "StudioVenueListItem",
    category: "Studios & Venues",
    purpose: "Compact studio/venue directory list item",
    contentComposition: "Thumbnail, venue name, location, rating, pricing, Atmos indicator badge",
    codeMapping: "StudioVenueListItem",
  },
  {
    key: "featured_studio_card",
    order: 10,
    name: "FeaturedStudioCard",
    category: "Studios & Venues",
    purpose: "Hero studio promotion banner card",
    contentComposition: "Large photography, studio/room identity, Atmos spec, pricing, booking CTA",
    codeMapping: "FeaturedStudioCard",
  },
  {
    key: "studio_spec_widget",
    order: 11,
    name: "StudioSpecWidget",
    category: "Studios & Venues",
    purpose: "Detailed studio technical specs widget",
    contentComposition: "Outboard gear, console models (SSL 9000J), monitors, microphone inventory",
    codeMapping: "StudioSpecWidget",
  },

  // 3. Marketplace & Opportunities
  {
    key: "marketplace_card",
    order: 12,
    name: "MarketplaceCard",
    category: "Marketplace & Opportunities",
    purpose: "Equipment rental and service discovery card",
    contentComposition: "Product/gear photo, name, rental rate, seller rating, availability tag",
    codeMapping: "UnifiedMarketplaceCard",
  },
  {
    key: "opportunity_card",
    order: 13,
    name: "OpportunityCard",
    category: "Marketplace & Opportunities",
    purpose: "Gigs, jobs, auditions & collaboration card",
    contentComposition: "Project title, creator/role, budget tag, required skills, applicant count, deadline",
    codeMapping: "UnifiedOpportunityCard",
  },

  // 4. Portfolio & Projects
  {
    key: "project_card",
    order: 14,
    name: "ProjectCard",
    category: "Portfolio & Projects",
    purpose: "Public portfolio & release presentation card",
    contentComposition: "Cover artwork, project title, release date, status badge, collaborators & credits",
    codeMapping: "UnifiedProjectCard",
  },
  {
    key: "recent_project_card",
    order: 15,
    name: "RecentProjectCard",
    category: "Portfolio & Projects",
    purpose: "Compact dashboard portfolio card",
    contentComposition: "Artwork, track/project name, role, release year, streaming links, edit action",
    codeMapping: "RecentProjectCard",
  },

  // 5. Media
  {
    key: "track_card",
    order: 16,
    name: "TrackCard",
    category: "Media",
    purpose: "Music & track release discovery item",
    contentComposition: "Artwork thumbnail, title, duration, platform badges (Spotify/YouTube), play count",
    codeMapping: "UnifiedTrackCard",
  },
  {
    key: "audio_player_widget",
    order: 17,
    name: "AudioPlayerWidget",
    category: "Media",
    purpose: "Dedicated embedded audio playback widget",
    contentComposition: "Waveform visualizer, play/pause, timeline slider, duration counter",
    codeMapping: "WaveformAudioPlayer",
    notes: "Maps directly to WaveformAudioPlayer as its implementation (per the source doc's audio-architecture unification note).",
  },
  {
    key: "persistent_audio_player",
    order: 18,
    name: "PersistentAudioPlayer",
    category: "Media",
    purpose: "Global sticky audio playback bar",
    contentComposition: "Mini artwork, track title, play/pause controls, progress bar, close trigger",
    codeMapping: "GlobalPersistentAudioPlayerBar",
    notes:
      "Maps directly to GlobalPersistentAudioPlayerBar. That file was named in an earlier dead-code audit as one shown 'D' (deleted) in git status at that session's start — confirm the file still exists before assuming this widget is live; isVisible intentionally left unset pending that check.",
  },
  {
    key: "audio_podcast_demo_widget",
    order: 19,
    name: "AudioPodcastDemoWidget",
    category: "Media",
    purpose: "In-line audio demo player in dashboards",
    contentComposition: "Audio identity, waveform controls, duration display, playback state",
    codeMapping: "AudioPodcastDemoWidget",
  },

  // 6. Bookings & Hiring
  {
    key: "booking_card",
    order: 20,
    name: "BookingCard",
    category: "Bookings & Hiring",
    purpose: "Studio session booking record card",
    contentComposition: "Studio name, date/time slot, room type, total price, booking status tag",
    codeMapping: "UnifiedBookingCard",
  },
  {
    key: "hire_contact_modal",
    order: 21,
    name: "HireContactModal",
    category: "Bookings & Hiring",
    purpose: "Direct hiring & booking inquiry modal",
    contentComposition: "Project scope options, proposed date picker, budget field, inquiry submission CTA",
    codeMapping: "HireContactModal",
  },

  // 7. Reputation & Activity
  {
    key: "public_reputation_widget",
    order: 22,
    name: "PublicReputationWidget",
    category: "Reputation & Activity",
    purpose: "Public trust and endorsement presentation",
    contentComposition: "Aggregated rating score, verified credits count, client reviews, response rate",
    codeMapping: "PublicReputationWidget",
  },
  {
    key: "review_card",
    order: 23,
    name: "ReviewCard",
    category: "Reputation & Activity",
    purpose: "Individual client review/endorsement card",
    contentComposition: "Reviewer identity, organization, rating star row, review quote, timestamp",
    codeMapping: "UnifiedReviewCard",
  },
  {
    key: "notification_card",
    order: 24,
    name: "NotificationCard",
    category: "Reputation & Activity",
    purpose: "Notification and activity item card",
    contentComposition: "Actor avatar, event description, event type icon, timestamp",
    codeMapping: "UnifiedNotificationCard",
  },

  // 8. Search, Filters & Sharing
  {
    key: "advanced_filters_bottom_sheet",
    order: 25,
    name: "AdvancedFiltersBottomSheet",
    category: "Search, Filters & Sharing",
    purpose: "Advanced discovery filtering bottom sheet",
    contentComposition: "Distance/budget sliders, category toggles, Dolby Atmos switch, apply CTA",
    codeMapping: "AdvancedFiltersBottomSheet",
    notes: "Documented in the Discover audit (Flow 4, advanced_filters screen) as filtering 100% mock data — a data-wiring gap, not a visibility question; isVisible intentionally left unset.",
  },
  {
    key: "whatsapp_share_sheet",
    order: 26,
    name: "WhatsAppShareSheet",
    category: "Search, Filters & Sharing",
    purpose: "External profile & project sharing sheet",
    contentComposition: "Share preview card, pre-populated message text, WhatsApp share action trigger",
    codeMapping: "WhatsAppShareSheet",
    notes:
      "Likely relates to the Options screen's per-channel share buttons found to be UI-only fakes (SupportingScreens.kt:924-969, showSnackbar only, no real WhatsApp intent) — unverified whether this specific widget is the same code path. isVisible intentionally left unset.",
  },

  // 9. Home & Featured Content
  {
    key: "home_feature_card",
    order: 27,
    name: "HomeFeatureCard",
    category: "Home & Featured Content",
    purpose: "Hero featured content on main feed",
    contentComposition: "Featured artist, studio, track, playlist or promotion with imagery and CTA",
    codeMapping: "HomeFeatureCard",
  },
];
