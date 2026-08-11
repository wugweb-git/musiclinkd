/**
 * Mirrors app/docs/firestore/Firestore_Schema_Index_Rules.md exactly.
 * Keep both in sync by hand when the schema changes.
 */
import type { Timestamp } from "firebase/firestore";

export type AdminRole = "ROLE_ADMIN" | "ROLE_MODERATOR" | "ROLE_SUPPORT";

/**
 * Replaced the old hardcoded 5-value UserRole enum (ARTIST/PRODUCER/
 * VOCALIST/STUDIO/LABEL) — the app's real onboarding collects one of 17
 * granular roles (see the "person_types" master-data category), as
 * primaryRole (required) + optional secondaryRole, matching what
 * OnboardingViewModel.kt actually writes today. Values are dynamic
 * (admin-managed master data), not a fixed union — see Users_And_Masters
 * notes in admin/docs/architecture/ for the seed list.
 */
/** { name, role, isLeader } — one entry per band-profile lineup member. */
export interface BandLineupMember {
  name: string;
  role: string;
  isLeader: boolean;
}

export type ProfileType = "TALENT" | "BAND" | "BUSINESS" | "VENUE";
export type BusinessType =
  | "Studio"
  | "Label"
  | "School"
  | "Dealer"
  | "Production House"
  | "Booking Agency"
  | "Artist Manager"
  | "Rental Company";

/** One entry on a Band's shows list — upcoming or past, distinguished by `isPast`. */
export interface BandShow {
  title: string;
  date: string;
  venueName?: string;
  ticketUrl?: string;
  isPast: boolean;
}

/** One staff member on a Business profile's "Team & Works" tab. */
export interface BusinessTeamMember {
  name: string;
  role: string;
  experience?: string;
  bio?: string;
  avatarUrl?: string;
}

/** One priced service on a Business profile's service menu. */
export interface BusinessServiceOffering {
  title: string;
  description?: string;
  price?: string;
  duration?: string;
}

/** Social/streaming platform links, all optional — matches Kotlin's ExternalLinksSchema. */
export interface ExternalLinks {
  spotify?: string;
  instagram?: string;
  youtube?: string;
  linkedin?: string;
  website?: string;
  twitter?: string;
  soundcloud?: string;
  appleMusic?: string;
  bandcamp?: string;
  /** DJ module — missing from AI Studio's original ExternalLinksSchema despite the DJ profile flow requiring it. */
  mixcloud?: string;
}

export interface UserDoc {
  displayName: string;
  email: string;
  primaryRole: string;
  secondaryRole: string;
  /**
   * Proposed replacement for secondaryRole (max 2) — additive, NOT yet
   * confirmed by AI Studio (see Prompt F §11) and no app-side writer exists.
   * secondaryRole stays live and untouched since it's a real, actively-used
   * field; this is forward-compat scaffolding, not a migration.
   */
  supportingRoles?: string[];
  isVerified: boolean;
  isBanned: boolean;
  isAvailable: boolean;
  hourlyRate: string;
  /** Coarse price-tier indicator (₹/₹₹/₹₹₹) for browse/filter display — separate from hourlyRate's exact figure. Taxonomy value from "budget_type". */
  budgetTier?: string;
  location: string;
  bio: string;
  /**
   * Real field, already written by the app's onboarding flow — was missing
   * from this type entirely until this pass (a genuine gap, not a proposal).
   */
  gender?: string;
  genres: string[];
  languages: string[];
  instruments: string[];
  personaBadges: string[];
  /** Admin-granted trust/tier badges (e.g. "Verified Artist") — distinct from personaBadges, which is self-selected. Never writable by the owner themselves; see firestore.rules. */
  trustBadges: string[];
  avatarUrl: string;
  createdAt: Timestamp;
  updatedAt?: Timestamp;
  /**
   * 4-family profile classification. Optional for now — existing documents
   * predate this field, and no app-side writer sets it yet (unverified
   * as of this session's audits; see admin/docs/architecture/ plan notes).
   * Once set, never writable by the owner themselves — see firestore.rules.
   */
  profileType?: ProfileType;

  /** OTP-auth contact number — already written by the real onboarding flow (OnboardingViewModel.kt), added here to match. */
  mobileNumber?: string;
  whatsappNumber?: string;
  /** Push-notification device token, if/when FCM registration is wired up app-side. */
  fcmToken?: string;
  externalLinks?: ExternalLinks;
  /** Simple per-instrument label list (e.g. "Guitar (Expert)") — a flatter shape than the app's in-memory Map<instrument, SkillProficiency>; reconcile if/when that gets persisted. */
  instrumentProficiency?: string[];
  /** role -> experience label, e.g. { "Producer": "5 years" }. */
  roleExperience?: Record<string, string>;

  // --- Talent extension (profileType === "TALENT") — role-specific dynamic
  // module fields, gated on primaryRole rather than a sub-type enum since
  // Talent doesn't have Business's businessType-style discriminator. All
  // optional; a given user only fills in the ones relevant to their role. ---
  /** Singer module. Taxonomy value from "vocal_range" (Soprano/Alto/Tenor/Bass etc.). */
  vocalRange?: string;
  /** Musician module — distinct from the coarse isAvailable boolean; taxonomy value from "availability_status". */
  sessionAvailability?: string;
  /** Engineer module — taxonomy values from "daw_tools" (Logic/Pro Tools/Ableton/Cubase/FL Studio). */
  dawStack?: string[];
  /** DJ module — free-text/link summary of past gigs; no structured shape confirmed yet. */
  performanceHistory?: string;
  /** Lyricist module — links to writing sample documents. */
  writingSamples?: string[];

  // --- Business extension (profileType === "BUSINESS") ---
  businessName?: string;
  businessType?: BusinessType;
  businessAddress?: string;
  tagline?: string;
  operatingHours?: string;
  gstin?: string;
  /** Taxonomy-backed filter tags (Recording/Mixing/Mastering) — used by Discover's filter chips, distinct from serviceOfferings below. */
  services?: string[];
  equipmentList?: string[];
  /** Staff roster — BusinessProfileScreens.kt's "Team & Works" tab has zero schema backing until this. */
  teamMembers?: BusinessTeamMember[];
  /** The priced service menu shown on the profile page — richer than the flat `services` filter-tag list above. */
  serviceOfferings?: BusinessServiceOffering[];
  /** Photo gallery — BusinessProfileScreens.kt's "Reviews & Gallery" tab; distinct from portfolio_projects (music projects, not photos). */
  galleryUrls?: string[];

  // --- Band extension (profileType === "BAND") ---
  bandName?: string;
  lineupMembers?: BandLineupMember[];
  techRiderUrl?: string;
  stagePlotUrl?: string;

  // --- Venue extension (profileType === "VENUE") ---
  venueName?: string;
  capacity?: number;
  venueFeatures?: string[];
  /** Scheduled event links/IDs — a flat string list for v1, not a subcollection. */
  eventCalendar?: string[];
}

export interface AdminRoleDoc {
  role: AdminRole;
  grantedBy: string;
  grantedAt: Timestamp;
}

export interface ConnectionDoc {
  senderUid: string;
  targetUserId: string;
  status: "PENDING" | "ACCEPTED" | "DECLINED";
}

export interface FeedPostDoc {
  authorUid: string;
  postType: string;
  audioUrl?: string;
  text?: string;
  likesCount: number;
  timestamp: Timestamp;
  /** Denormalized so the feed can render without a join against users/{uid} — additive, unverified as app-written this session. */
  authorName?: string;
  authorRole?: string;
  authorAvatarUrl?: string;
  /** Generalized media attachments (images/audio) — audioUrl is kept for the existing single-audio shape. */
  mediaUrls?: string[];
  commentsCount?: number;
}

export interface StudioBookingDoc {
  studioId: string;
  userId: string;
  date: string;
  slotText: string;
  totalAmount: number;
  bookedHourSlots?: string[];
  /** Taxonomy value from "booking_statuses" (requested/confirmed/payment_pending/cancelled/completed). */
  bookingStatus?: string;
  paymentStatus?: "PENDING" | "PAID" | "REFUNDED" | "FAILED";
  paymentTransactionId?: string;
  createdAt: Timestamp;

  // --- Session configuration ("Configure Session" screen) ---
  /** Taxonomy value from new "session_types" category (Recording/Mixing/Mastering/Podcast/Jam Session). */
  sessionType?: string;
  /** Taxonomy value from new "session_duration_tiers" category (Hourly/Half Day/Full Day). */
  durationTier?: string;
  engineerRequested?: boolean;
  /** Which of the studio's StudioDoc.equipment items were included in this booking. */
  selectedEquipment?: string[];
  /** Taxonomy values from new "studio_addons" category (Food/Parking/Extra Hours etc.). */
  addons?: string[];
  /**
   * Maps to the app's actual (differently-named) `artistBookingNotes` field
   * found in StudioBookingScreens.kt — this is the corrected name, not yet
   * confirmed as what the app will write; flag in the next AI Studio prompt.
   */
  notes?: string;

  // --- Booking Summary / Payment screens ---
  taxAmount?: number;
  depositAmount?: number;
  discountAmount?: number;
  promoCode?: string;
  paymentMethod?: "UPI" | "Card" | "Net Banking" | "Wallet";
}

export interface ChatThreadDoc {
  memberUids: string[];
}

export interface ChatMessageDoc {
  senderUid: string;
  text: string;
  sentAt: Timestamp;
}

/** One physical piece of gear — the "Equipment" screen's card shape. */
export interface StudioEquipmentItem {
  name: string;
  category?: string;
  imageUrl?: string;
  quantity?: number;
  condition?: string;
  included: boolean;
}

export interface StudioDoc {
  ownerUid: string;
  name: string;
  location: string;
  rating: number;
  reviewCount: number;
  hourlyRate: string;
  verifiedGold: boolean;
  /** Taxonomy value(s) from "studio_amenities" — physical features (Dolby Atmos, Parking, 24x7 Access). */
  amenities: string[];
  consoleType: string;
  imageUrl: string;
  createdAt?: Timestamp;
  description?: string;
  /** For the Studio Profile screen's "Call" button. */
  phone?: string;
  /** Gallery — additive alongside the single imageUrl (used as the hero/card thumbnail). */
  images?: string[];
  /** Taxonomy values from new "studio_services" category (Recording/Mixing/Mastering/Podcast/Jam Session) — what the studio offers, distinct from amenities (what it physically has). */
  services?: string[];
  /** For distance-based "Nearby" sort and the profile's location map — additive alongside the display-string `location`. */
  geo?: { lat: number; lng: number };
  /** Structured gear list — consoleType stays as a simple free-text fallback for records that don't have this yet. */
  equipment?: StudioEquipmentItem[];
  operatingHours?: string;
  cancellationPolicy?: string;
}

export type ProjectStatus = "OPEN" | "IN_PROGRESS" | "COMPLETED";

export interface ProjectDoc {
  creatorUid: string;
  title: string;
  roleRequired: string;
  budget: string;
  status: ProjectStatus;
  genres: string[];
  location?: string;
  applicantCount?: number;
  /** Taxonomy value from "collaboration_types" (work_for_hire/royalties_split/co_write/session_payment). */
  collaborationType?: string;
  createdAt: Timestamp;
}

export type MarketplaceCondition = "NEW" | "LIKE_NEW" | "USED";

export interface MarketplaceItemDoc {
  sellerUid: string;
  title: string;
  price: string;
  /**
   * Was a fixed "GEAR"|"STEMS"|"SERVICES" union that never matched the
   * app's real categories (sale/rental/mixing/beats/services) — reconciled
   * this round to a taxonomy-backed string sourced from the new
   * "marketplace_categories" category (gear_sale/gear_rental/
   * beats_licensing/stems/mixing_mastering/creative_services).
   */
  category: string;
  condition: MarketplaceCondition;
  imageUrl: string;
  /** Which taxonomy category applies for this listing's specific item — "instruments" or the new "equipment_categories", depending on what's being sold. */
  itemCategory?: string;
  createdAt: Timestamp;
}

export interface ReviewDoc {
  targetType: "STUDIO" | "USER";
  targetId: string;
  authorUid: string;
  rating: number;
  text: string;
  /** Links to studio_bookings/{bookingId} for "verified purchase" — enforced by firestore.rules. */
  bookingId?: string;
  createdAt: Timestamp;
}

export type VerificationStatus = "PENDING" | "APPROVED" | "REJECTED";

export interface VerificationRequestDoc {
  subjectType: "USER" | "STUDIO";
  subjectId: string;
  submittedBy: string;
  status: VerificationStatus;
  documentPaths: string[];
  /** Taxonomy value from "verification_id_type" (Govt ID/Passport/Driving License/Company PAN-GSTIN) — field was missing despite the category already being cataloged (Part L.3). */
  verificationIdType?: string;
  submittedAt: Timestamp;
  reviewedBy?: string;
  reviewedAt?: Timestamp;
  notes?: string;
}

export type ModerationTargetType = "POST" | "AUDIO" | "REVIEW" | "MARKETPLACE_ITEM" | "USER";
export type ModerationStatus = "PENDING" | "ACTIONED" | "DISMISSED";

export interface ModerationFlagDoc {
  targetType: ModerationTargetType;
  targetId: string;
  reportedBy: string;
  reason: string;
  status: ModerationStatus;
  createdAt: Timestamp;
  reviewedBy?: string;
  reviewedAt?: Timestamp;
}

export type SupportTicketStatus = "OPEN" | "IN_PROGRESS" | "RESOLVED";

export interface SupportTicketDoc {
  requesterUid: string;
  requesterEmail: string;
  subject: string;
  message: string;
  status: SupportTicketStatus;
  assignedTo?: string;
  createdAt: Timestamp;
  updatedAt: Timestamp;
}

export type BroadcastStatus = "QUEUED" | "SENT" | "FAILED";

export interface BroadcastMessageDoc {
  title: string;
  body: string;
  audience: string;
  sentBy: string;
  sentAt: Timestamp;
  status: BroadcastStatus;
}

export interface AuditLogDoc {
  actorUid: string;
  action: string;
  targetPath: string;
  timestamp: Timestamp;
  metadata?: Record<string, unknown>;
}

export type PageStatus = "DRAFT" | "PUBLISHED";

/**
 * Known gap, not silently dropped: frontend/website/ is static HTML and
 * never reads this collection at runtime, so a save here doesn't change
 * what's live. The real fix is a build-time SSG pull of PUBLISHED docs into
 * scripts/build-hosting.sh's frontend/website/ step — a separate, larger
 * workstream, not attempted yet. See admin/docs/architecture/CMS_Content_Mapping.md.
 */
export interface PageContentDoc {
  /** URL path on musiclinkd.com, e.g. "/", "/about", "/legal/privacy". */
  route: string;
  title: string;
  status: PageStatus;
  /** Plain text/markdown body for v1 — no block-based page builder. */
  content: string;
  seoDescription: string;
  updatedBy: string;
  updatedAt: Timestamp;
}

export interface TaxonomyCategoryDoc {
  key: string;
  label: string;
  description: string;
  isActive: boolean;
  createdAt: Timestamp;
  updatedAt: Timestamp;
  updatedBy: string;
}

export interface TaxonomyOptionDoc {
  label: string;
  value: string;
  sortOrder: number;
  isActive: boolean;
  /** Optional escape hatch for category-specific extra data (e.g. an instrument's isAcoustic flag, a booking status's indicatorColorHex) — not editable from the admin UI yet, set via Firestore console/script until a concrete category needs it. */
  metadata?: Record<string, unknown>;
  createdAt: Timestamp;
  updatedAt: Timestamp;
}

export type FlowStatus = "documented" | "partial" | "missing" | "app-mocked";

/**
 * flows/{flowId} — the product's flow map, as an admin-editable registry
 * instead of only living in markdown docs. Flow is the authoritative unit;
 * screens (subcollection below) belong to a flow, and each screen can carry
 * multiple named states (e.g. per-profileType variants, loading/error
 * states) — "a screen has states, but flow prevails," per the user's own
 * framing.
 */
export interface FlowDoc {
  key: string;
  order: number;
  title: string;
  description: string;
  status: FlowStatus;
  createdAt: Timestamp;
  updatedAt: Timestamp;
}

/**
 * flows/{flowId}/screens/{screenId} — one screen within a flow.
 * `collectionsTouched` is the Flow/Screen ↔ schema mapping the user asked
 * for: which Firestore collections this screen actually reads or writes.
 */
export interface FlowScreenDoc {
  key: string;
  order: number;
  title: string;
  /** Named states this screen can render as — per-role variants, loading/error/empty, etc. */
  states?: string[];
  /** Firestore collection names this screen reads or writes. */
  collectionsTouched?: string[];
  notes?: string;
  createdAt: Timestamp;
  updatedAt: Timestamp;
}

export type CrmAccessLevel = "none" | "partial" | "full";

/**
 * capabilities/{roleKey} — what a profile type/role is permitted to do.
 * roleKey spans both profileType values ("talent") and businessType
 * sub-types ("studio"/"label"/"agency") plus "admin" — a flatter key space
 * than a strict profileType/businessType split, matching how the user
 * described the matrix. Public read (the app needs this to gate UI
 * client-side), admin-only write — same pattern as ModuleRegistryDoc.
 */
export interface CapabilityMatrixDoc {
  roleKey: string;
  label: string;
  canCreateProfile: boolean;
  canPostOpportunities: boolean;
  canSellEquipment: boolean;
  canCreateEvents: boolean;
  crmAccess: CrmAccessLevel;
  updatedBy: string;
  updatedAt: Timestamp;
  createdAt: Timestamp;
}

export interface ModuleRegistryDoc {
  label: string;
  description: string;
  enabled: boolean;
  /** Links to a real admin nav item/route (see config/constants.ts ModuleKey), or null if this module has no admin-panel page. */
  navModuleKey: string | null;
  /** Remote Config parameter this toggle mirrors, or null if it's admin-only. */
  remoteConfigKey: string | null;
  updatedBy: string;
  updatedAt: Timestamp;
  createdAt: Timestamp;
}

/**
 * Corresponds to the app's SavedProfilesScreen (currently 100% unwired to
 * Firestore) — admin type only for now, no admin UI built yet.
 */
export interface BookmarkDoc {
  ownerUid: string;
  savedUserId: string;
  createdAt: Timestamp;
}

export type PortfolioProjectStatus = "Released" | "Upcoming" | "In Production" | "Draft";
export type PortfolioVisibility = "Public" | "Connections Only" | "Private Draft";

/** A tagged, taggable collaborator on a project — the "Add Collaborators" step. */
export interface ProjectCollaborator {
  id: string;
  name: string;
  role: string;
  avatarEmoji: string;
  isVerified: boolean;
  imageUrl?: string;
}

/** The "Credit Attribution" step's output — one entry per collaborator's assigned credit. */
export interface CreditItem {
  collaboratorName: string;
  role: string;
  avatarEmoji: string;
  imageUrl?: string;
}

/**
 * portfolio_projects/{projectId} — rules already deployed (firestore.rules),
 * owner-uid pattern matching studios/projects (field name is `ownerUid`,
 * matching the deployed rule — NOT `userId`, an earlier mismatch corrected
 * here). No confirmed app-side writer as of this session's audits — admin
 * type + rules land ahead of that so the collection is ready the moment the
 * app starts writing to it.
 *
 * Field-by-field maps to the 11-screen "Add Project" publish flow:
 * Screen 2 (type) -> type; Screen 3 -> name/description/releaseDate/status;
 * Screen 4 (streaming links) -> spotifyUrl..instagramReelUrl; Screen 5
 * (role multi-select) -> ownerRoles; Screen 6 (tag collaborators) ->
 * collaborators; Screen 7 (credit attribution) -> credits; Screen 8
 * (artwork preview) -> artworkTheme (a palette-keyword string, e.g.
 * "Acoustic"/"Techno" — the real PortfolioScreens.kt artwork picker is a
 * color-theme chooser, NOT a file upload; don't treat this as an image URL
 * until real upload UI exists); Screen 9 -> visibility; Screen 11 (profile
 * view stats) -> projectViews/likes/shares.
 */
export interface PortfolioProjectDoc {
  ownerUid: string;
  name: string;
  description: string;
  /** Taxonomy value from "project_types" (song/album/ep/podcast/dj_mix/film_score/commercial_jingle/live_performance/music_video/custom_project). */
  type: string;
  releaseDate: string;
  status: PortfolioProjectStatus;
  visibility: PortfolioVisibility;
  spotifyUrl?: string;
  appleMusicUrl?: string;
  youtubeUrl?: string;
  soundcloudUrl?: string;
  mixcloudUrl?: string;
  instagramReelUrl?: string;
  /** Taxonomy values from "project_roles" (Singer/Composer/Music Director/Producer/Lyricist/Session Musician/DJ/Mix Engineer/Mastering Engineer/Sound Designer/Band Member) — multi-select, the project-scoped role(s) the owner played. */
  ownerRoles: string[];
  collaborators: ProjectCollaborator[];
  credits: CreditItem[];
  /** Generic visual media (cover art image, screenshots) — distinct from artworkTheme below, which is a palette keyword, not a real file. Dropped from the original Part F correction; re-added since AI Studio's spec's mediaUrls concept has no other home. */
  mediaUrls?: string[];
  artworkTheme: string;
  projectViews: number;
  likes: number;
  shares: number;
  createdAt: Timestamp;
  lastUpdatedAt: Timestamp;
}

export type NotificationType =
  | "CONNECTION_REQUEST"
  | "BOOKING_UPDATE"
  | "REVIEW_RECEIVED"
  | "SYSTEM_ALERT"
  | "GIG_MATCH";

/**
 * notifications/{notificationId} — no confirmed app-side writer or reader
 * as of this session's audits. Admin type + rules only for now.
 */
export interface NotificationDoc {
  userId: string;
  title: string;
  body: string;
  type: NotificationType;
  isRead: boolean;
  targetUrl?: string;
  createdAt: Timestamp;
}

export type ProfileVisibility = "PUBLIC" | "CONNECTIONS_ONLY" | "PRIVATE";

/**
 * user_settings/{userId} — doc ID = the user's own UID. No confirmed
 * app-side writer as of this session's audits (SettingsFlow was found to
 * have zero persistence anywhere). Admin type + rules only for now.
 */
export interface UserSettingsDoc {
  pushNotificationsEnabled: boolean;
  emailNotificationsEnabled: boolean;
  profileVisibility: ProfileVisibility;
  darkModeEnabled: boolean;
  currency: string;
  language: string;
  updatedAt: Timestamp;
}

/**
 * brand_assets/{assetId} — owner-uid pattern, for Business-family profiles'
 * logo/identity assets. No confirmed app-side writer as of this session's
 * audits. Admin type + rules only for now.
 */
export interface BrandAssetsDoc {
  ownerUid: string;
  logoUrl?: string;
  iconUrl?: string;
  thumbnailUrl?: string;
  heroBannerUrl?: string;
  darkVariantUrl?: string;
  lightVariantUrl?: string;
  updatedAt: Timestamp;
}

export type RoleProfileKey =
  | "singer"
  | "musician"
  | "lyricist"
  | "music_director"
  | "composer"
  | "sound_engineer"
  | "dj"
  | "rj"
  | "podcaster";

interface RoleProfileBase {
  ownerUid: string;
  createdAt: Timestamp;
  updatedAt: Timestamp;
}

export interface SingerRoleProfile extends RoleProfileBase {
  roleKey: "singer";
  /** Taxonomy value from "vocal_range" (Soprano/Alto/Tenor/Bass etc.) — same category the Talent-extension field reuses. */
  vocalRange?: string;
  /** Taxonomy values from new "singing_styles" category (Playback/Classical/Ghazal/Sufi/Indie/Pop/Rock/Rap/Bhajan). */
  singingStyles?: string[];
  originalSongsCount?: number;
  coversCount?: number;
  audioSampleUrls?: string[];
  videoSampleUrls?: string[];
}

export interface MusicianRoleProfile extends RoleProfileBase {
  roleKey: "musician";
  /** Taxonomy value from "instruments" (already exists — same category the flat UserDoc.instruments[] uses). */
  primaryInstrument?: string;
  secondaryInstruments?: string[];
  yearsOfExperience?: number;
  performanceVideoUrls?: string[];
  sessionRecordingUrls?: string[];
}

export interface LyricistRoleProfile extends RoleProfileBase {
  roleKey: "lyricist";
  /** Taxonomy values from new "lyricist_specializations" category (Romantic/Commercial/Devotional/Rap/Film Songs/Independent). */
  specializations?: string[];
  publishedWorksCount?: number;
  writingSampleUrls?: string[];
  portfolioPdfUrls?: string[];
}

export interface MusicDirectorRoleProfile extends RoleProfileBase {
  roleKey: "music_director";
  /** Taxonomy values from new "director_specializations" category (Film Music/OTT/Advertising/Independent). */
  specializations?: string[];
  filmography?: string[];
  discography?: string[];
  showcaseAudioUrls?: string[];
  showcaseVideoUrls?: string[];
}

export interface ComposerRoleProfile extends RoleProfileBase {
  roleKey: "composer";
  /** Taxonomy values from new "composition_styles" category (Orchestral/Electronic/Background Score/Commercial Jingles/Film Score). */
  compositionStyles?: string[];
  showcaseAudioUrls?: string[];
}

export interface SoundEngineerRoleProfile extends RoleProfileBase {
  roleKey: "sound_engineer";
  /** Taxonomy value from new "engineer_specialties" category (Recording/Mixing/Mastering/Live Sound). */
  specialty?: string;
  /** Taxonomy values from "daw_tools" (already exists — same category the Talent-extension dawStack field reuses). */
  dawStack?: string[];
  ownedEquipment?: string[];
  pastProjectUrls?: string[];
}

export interface DjRoleProfile extends RoleProfileBase {
  roleKey: "dj";
  /** Taxonomy value from new "dj_types" category — proposed, not confirmed (Club/Wedding/Radio/Festival/Bollywood). */
  djType?: string;
  eventExperienceYears?: number;
  mixUrls?: string[];
  eventVideoUrls?: string[];
}

export interface RjRoleProfile extends RoleProfileBase {
  roleKey: "rj";
  showsHosted?: string[];
  experienceYears?: number;
  audioSampleUrls?: string[];
  showClipUrls?: string[];
}

export interface PodcasterRoleProfile extends RoleProfileBase {
  roleKey: "podcaster";
  podcastName?: string;
  /** Taxonomy values from new "podcast_categories" category — proposed, not confirmed. */
  categories?: string[];
  listenerBase?: number;
  episodeUrls?: string[];
  clipUrls?: string[];
}

/**
 * role_profiles/{ownerUid_roleKey} — one document per role a user plays,
 * so a genuinely multi-role person (e.g. Singer AND Sound Engineer) has two
 * independent documents rather than one UserDoc bloated with every role's
 * fields. Chosen over a flat UserDoc extension or a generic
 * Record<string, unknown> bag specifically for this scalability, per the
 * user's explicit choice — see the plan's Part U. No confirmed app-side
 * writer yet.
 */
export type RoleProfileDoc =
  | SingerRoleProfile
  | MusicianRoleProfile
  | LyricistRoleProfile
  | MusicDirectorRoleProfile
  | ComposerRoleProfile
  | SoundEngineerRoleProfile
  | DjRoleProfile
  | RjRoleProfile
  | PodcasterRoleProfile;

export interface WithId<T> {
  id: string;
  data: T;
}
