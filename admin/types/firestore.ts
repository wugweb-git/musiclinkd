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
  /** Module A profile-shell fields (PRD) — vanity handle, shareable URL, generated QR data, and a cover image distinct from avatarUrl. Not confirmed as app-written. */
  username?: string;
  vanityUrl?: string;
  qrCodeUrl?: string;
  coverImageUrl?: string;
  /** Per-hour INR equivalent. 0 means unset/not hourly-priced (e.g. a label with no personal booking rate) — render as "—"/"Under Discussion" rather than "₹0". Day/session/track rates are stored as their hourly-equivalent guess; migrated from a free-text display string (see Prompt F). */
  hourlyRate: number;
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
  /**
   * Denormalized fan-out of every collaborator UID this person has been
   * tagged as (across all of their portfolio_projects). Firestore has no
   * server-side joins, so without this, rendering "who has this person
   * worked with" on a profile would require reading every project they
   * appear on. Written client-side whenever a project's `collaborators`
   * list changes — no confirmed app-side writer yet. Proposed 2026-08-16
   * per the Person Profile spec (docs/04-Wireframes/PERSON-PROFILE-SPEC.md
   * §4) to back the new Collaborators screen (screenNumber 123).
   */
  collaboratorUids?: string[];
  /**
   * Coordinates for distance-based "Nearby" sort in Discover — mirrors
   * StudioDoc.geo's existing shape exactly. Studios already have this;
   * People didn't, so Discover's Nearby tab only worked for Studios.
   * Proposed 2026-08-16 per docs/04-Wireframes/PRODUCT-SPEC.md §C.2.
   * No confirmed app-side writer yet.
   */
  geo?: { lat: number; lng: number };
  /**
   * Performance/studio photography gallery — shared across any profileType,
   * not Business-only (moved out of the Business-extension block below,
   * where it previously lived scoped to BusinessProfileScreens.kt's
   * "Reviews & Gallery" tab). Talent and Band profiles need this too (brief
   * §16). No confirmed app-side writer yet. Distinct from portfolio_projects
   * (music projects, not photos) and mediaUrls on PortfolioProjectDoc
   * (per-project media, not a profile-level gallery). Proposed 2026-08-16
   * per docs/04-Wireframes/PERSON-PROFILE-SPEC.md §4 (new screen 121).
   */
  galleryUrls?: string[];

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
  /**
   * The priced service menu shown on the profile page — richer than the
   * flat `services` filter-tag list above. NOTE (2026-08-16): Talent
   * professionals (mixing/mastering/session engineers etc.) also need a
   * priced service menu per the Person Profile spec (§4/§5 open question 2:
   * docs/04-Wireframes/PERSON-PROFILE-SPEC.md), but whether that means
   * widening this field to the base UserDoc or adding a separate
   * Talent-scoped field is an unanswered product decision — not resolved
   * here, deliberately left Business-scoped until that's decided.
   */
  serviceOfferings?: BusinessServiceOffering[];
  /** businessType === "Label" — names or user UIDs of signed artists. Simple string list, not a structured roster — matches "not detailed schema, just entities" scoping. */
  artistRoster?: string[];
  /** businessType === "School" — course names offered. */
  courses?: string[];
  /** businessType === "Booking Agency" / "Artist Manager" — names or user UIDs of represented talent. */
  talentRoster?: string[];

  // --- Band extension (profileType === "BAND") ---
  bandName?: string;
  lineupMembers?: BandLineupMember[];
  techRiderUrl?: string;
  stagePlotUrl?: string;
  /** Upcoming/past performances — not modeled until now; distinguished by BandShow.isPast rather than two separate arrays. */
  shows?: BandShow[];

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

/**
 * blocks/{blockerUid_blockedUid} — one-directional block, distinct from
 * ModerationFlagDoc (a one-time report). Doc ID convention matches
 * user_bookmarks ("{ownerUid}_{targetId}" style): "{blockerUid}_{blockedUid}".
 * Only the blocker (and moderators) can read/delete their own block docs —
 * the blocked user has no visibility into who has blocked them, same
 * expectation as most social products. Proposed 2026-08-16 per
 * docs/04-Wireframes/PERSON-PROFILE-SPEC.md §4. No confirmed app-side
 * writer yet — admin type + rules only for now, same posture as
 * NotificationDoc/UserSettingsDoc when they were first added.
 */
export interface BlockDoc {
  blockerUid: string;
  blockedUid: string;
  createdAt: Timestamp;
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

/**
 * One bookable room within a studio — a studio can have several (e.g. a
 * live tracking room and a separate vocal booth), each with its own rate
 * and gear. Distinct from the studio-level `equipment` list, which today
 * represents gear the studio as a whole advertises rather than per-room.
 */
export interface StudioRoom {
  name: string;
  capacity?: number;
  /** Per-hour INR equivalent — same convention as StudioDoc.hourlyRate. */
  hourlyRate: number;
  amenities?: string[];
  imageUrl?: string;
}

export interface StudioDoc {
  ownerUid: string;
  name: string;
  location: string;
  rating: number;
  reviewCount: number;
  /** Per-hour INR equivalent — see UserDoc.hourlyRate for the same 0-means-unset convention. */
  hourlyRate: number;
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
  /** Individually bookable rooms within this studio, if it has more than one. Absent/empty means the studio is booked as a single space (the common case today). */
  rooms?: StudioRoom[];
  operatingHours?: string;
  cancellationPolicy?: string;
}

/**
 * Widened 2026-08-16 (additive — existing OPEN/IN_PROGRESS/COMPLETED values
 * stay valid) so a poster can pause or close a listing without deleting it.
 * Per docs/04-Wireframes/PRODUCT-SPEC.md §D.2. Phase 2 — MVP only writes
 * OPEN/IN_PROGRESS/COMPLETED, same as before.
 */
export type ProjectStatus = "DRAFT" | "OPEN" | "PAUSED" | "IN_PROGRESS" | "CLOSED" | "COMPLETED";

/**
 * projects/{projectId} — also the backing collection for Flow 5
 * (Opportunities), per a prior reconciliation decision (see
 * webapp/flows/opportunities/_flow.json): "folded in rather than a
 * separate gig_opportunities collection." Fields below `collaborationType`
 * were added 2026-08-16 per docs/04-Wireframes/PRODUCT-SPEC.md §D.2 to
 * actually support an Opportunity Detail screen — `description` is the one
 * MVP-required addition (an Opportunity Detail screen with no body text
 * isn't a detail screen); the rest are Phase 2.
 */
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

  // --- Opportunities fields, added 2026-08-16 (PRODUCT-SPEC.md §D.2) ---
  /** MVP-required — an Opportunity Detail screen with no body text isn't a detail screen. */
  description?: string;
  /** Multi-select, additive alongside the single roleRequired. Phase 2. */
  skillsRequired?: string[];
  /** Phase 2. */
  languagesRequired?: string[];
  /** Phase 2 — the brief separates "Location" from "Remote/On-site" as two different questions. */
  locationType?: "remote" | "on_site" | "hybrid";
  /** Phase 2. */
  deadline?: Timestamp;
  /**
   * Taxonomy value from the new "opportunity_types" category — a real,
   * separate axis from collaborationType (which is about compensation
   * structure, not what kind of opportunity this is). Phase 2 — taxonomy
   * drafted in PRODUCT-SPEC.md §D.6, not yet seeded.
   */
  opportunityType?: string;
  /**
   * Denormalized poster identity — same justification already accepted for
   * FeedPostDoc's authorName/authorAvatarUrl: render a list of opportunity
   * cards without one Firestore read per poster. Phase 2 (MVP can do one
   * read per row at small scale).
   */
  posterName?: string;
  posterAvatarUrl?: string;
}

/**
 * opportunity_applications/{applicationId} — the single biggest gap
 * identified in the Opportunities flow: `ProjectDoc.applicantCount` was a
 * bare counter, nothing tracked *who* applied, with *what*, or their
 * status. Deliberately kept separate from Marketplace's `offers` — gigs/
 * collaboration (Shortlist → Accept on a person) and commercial
 * negotiation (Counter → Accept on a price) are different domains with
 * different state machines, confirmed 2026-08-16 (PRODUCT-SPEC.md §D.2/
 * §D.4-2). Phase 2 — not built for the Opportunities MVP, which uses
 * "Message the poster" instead. No confirmed app-side writer yet.
 */
export interface OpportunityApplicationDoc {
  /** → projects/{projectId} doc ID. */
  opportunityId: string;
  applicantUid: string;
  attachedPortfolioProjectIds?: string[];
  message?: string;
  status: "SUBMITTED" | "VIEWED" | "SHORTLISTED" | "ACCEPTED" | "REJECTED";
  createdAt: Timestamp;
  updatedAt: Timestamp;
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
  /** Units available for this listing. Undefined/1 = single item (the common case); >1 for a seller listing multiple identical units (e.g. a beat-pack license count, bulk cable stock). */
  quantity?: number;
  /**
   * Dates already booked for `category === "gear_rental"` listings — a
   * simple blocked-dates list, not a full calendar engine. Proposed
   * 2026-08-16 per docs/04-Wireframes/PRODUCT-SPEC.md §B.3. No confirmed
   * app-side writer yet.
   */
  blockedDates?: string[];
  createdAt: Timestamp;
}

/**
 * offers/{offerId} — the shared Make Offer / Negotiate / Accept primitive
 * for Marketplace, covering both `sale` and `rental` listings (rent stayed
 * merged into Marketplace as a listing category, not a separate flow — see
 * PRODUCT-SPEC.md §B.1). Deliberately NOT reused by Opportunities'
 * application flow — different domain, different state machine (Shortlist
 * → Accept on a person, vs. Counter → Accept on a price), confirmed
 * 2026-08-16 per §D.2. No confirmed app-side writer yet.
 */
export interface OfferDoc {
  listingId: string;
  buyerUid: string;
  sellerUid: string;
  amount: number;
  message?: string;
  status: "PENDING" | "COUNTERED" | "ACCEPTED" | "DECLINED" | "EXPIRED";
  counterAmount?: number;
  createdAt: Timestamp;
  updatedAt: Timestamp;
}

/**
 * marketplace_orders/{orderId} — one order/transaction record for both a
 * straight Buy purchase and a Rent booking (rent didn't get its own
 * booking collection — see PRODUCT-SPEC.md §B.1/§B.3). Payment fields
 * deliberately mirror StudioBookingDoc's existing shape verbatim (decided
 * 2026-08-16, §B.4) rather than inventing a new payments model. Proposed
 * 2026-08-16, no confirmed app-side writer yet.
 */
export interface MarketplaceOrderDoc {
  listingId: string;
  buyerUid: string;
  sellerUid: string;
  amount: number;
  status: "PENDING" | "PAID" | "COMPLETED" | "CANCELLED" | "ACTIVE" | "RETURNED";
  /** Same shape as StudioBookingDoc.paymentMethod. */
  paymentMethod?: "UPI" | "Card" | "Net Banking" | "Wallet";
  /** Same shape as StudioBookingDoc.paymentStatus. */
  paymentStatus?: "PENDING" | "PAID" | "REFUNDED" | "FAILED";
  paymentTransactionId?: string;
  /** Only set when the listing's category is a rental (`gear_rental`). */
  startDate?: string;
  endDate?: string;
  deliveryMethod?: "pickup" | "delivery";
  createdAt: Timestamp;
}

export interface ReviewDoc {
  targetType: "STUDIO" | "USER" | "MARKETPLACE_ITEM";
  targetId: string;
  authorUid: string;
  rating: number;
  text: string;
  /** Links to studio_bookings/{bookingId} for "verified purchase" — enforced by firestore.rules. */
  bookingId?: string;
  /** Links to marketplace_orders/{orderId} for a Marketplace "verified purchase" — same idea as bookingId, added 2026-08-16 alongside the MARKETPLACE_ITEM targetType per PRODUCT-SPEC.md §B.3. */
  orderId?: string;
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
  /** Flow number — doubles as sort order and the stable "Flow N" label shown in the UI. 1-24, no gaps. */
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
  /** Position within this flow only (not globally unique) — drives the in-flow "#" column. */
  order: number;
  /**
   * Globally unique screen ID across every flow's screens, assigned once and
   * never reused — the stable identifier for cross-referencing a screen from
   * widgets, schema docs, or anywhere else. Absent on variant entries (see
   * `variantOf`), which reference a base screen's number instead of owning
   * their own.
   */
  screenNumber?: number;
  /**
   * Set when this screen is a schema-driven fork of another screen (e.g. a
   * per-profileType template) rather than a standalone screen — the
   * `screenNumber` of the base screen it forks from. Rendered in the UI as
   * `{variantOf}-{n}` (n = 1-based position among that base screen's variants).
   */
  variantOf?: number;
  /** Short label for what this variant is, e.g. "Business template", "Band template". */
  variantLabel?: string;
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

/**
 * profile_analytics/{uid} — doc ID = the profile owner's UID, same
 * convention as UserSettingsDoc. Nothing modeled profile-level analytics
 * before this (PortfolioProjectDoc only has per-project projectViews/
 * likes/shares) — proposed 2026-08-16 per
 * docs/04-Wireframes/PERSON-PROFILE-SPEC.md §4 to back the new Profile
 * Analytics screen (screenNumber 122). Counters, not derived client-side —
 * this project stays on the Spark plan with no Cloud Functions deployed
 * (see the /notifications rule's own note), so counter increments are
 * written directly by the viewing client, same pattern as everything else
 * here. Private to the owner (and admins), not public — see firestore.rules.
 */
export interface ProfileAnalyticsDoc {
  profileViews: number;
  portfolioViews: number;
  audioPlays: number;
  videoPlays: number;
  searchAppearances: number;
  shareCount: number;
  connectionRequestsReceived: number;
  updatedAt: Timestamp;
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
  | "GIG_MATCH"
  /** Marketplace — added 2026-08-16 per PRODUCT-SPEC.md §B.3, Phase 2 (not MVP). */
  | "OFFER_RECEIVED"
  | "ORDER_STATUS_UPDATE"
  /** Opportunities — added 2026-08-16 per PRODUCT-SPEC.md §D.2, Phase 2 (not MVP; opportunity_applications isn't built yet either). */
  | "APPLICATION_STATUS_UPDATE";

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

/**
 * widgets/{widgetId} — the app's UI-component catalog as data, same
 * pattern as flows/capabilities: internal engineering documentation, not
 * user-facing content. Sourced from admin/docs/architecture/Widget_Directory.md.
 * `isVisible` is left undefined until someone actually confirms whether a
 * given widget's call site is live or commented out in the app — several
 * entries were only "likely hidden" per the source audit, not confirmed;
 * don't default this to true or false, that would fabricate a fact this
 * registry exists specifically to get right.
 */
export interface WidgetDoc {
  key: string;
  /** Widget number — doubles as sort order and the stable "Widget N" label shown in the UI. */
  order: number;
  name: string;
  category: string;
  purpose: string;
  contentComposition: string;
  codeMapping: string;
  isVisible?: boolean;
  notes?: string;
  /**
   * screenNumbers (FlowScreenDoc.screenNumber) this widget actually renders
   * on — many-to-many, since one widget (e.g. ArtistCard) typically appears
   * across several screens/flows. Added 2026-08-13; empty/absent on
   * existing seed data until each widget's real screens are confirmed
   * against source — not populated speculatively.
   */
  relatedScreens?: number[];
  createdAt: Timestamp;
  updatedAt: Timestamp;
}

export interface WithId<T> {
  id: string;
  data: T;
}
