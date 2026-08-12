import type { FlowDoc } from "@/types/firestore";

/**
 * Seed content for the "Seed defaults" button on the Flows list page.
 * Descriptions are grounded in this session's audits of the real /app
 * source — not guessed. Status reflects what was actually verified:
 * "documented" = real, working, audited; "partial" = some real code but
 * broken/incomplete/inconsistent; "app-mocked" = UI exists, zero Firestore
 * wiring; "missing" = no schema, no UI, or neither confirmed.
 */
export const DEFAULT_FLOWS: Omit<FlowDoc, "createdAt" | "updatedAt">[] = [
  {
    key: "auth",
    order: 1,
    title: "User Auth",
    status: "partial",
    description:
      "Email/password calls the real Firebase Auth SDK but fails server-side (fabricated API key). OTP is 100% fake — verifyOtp()'s check is effectively \"any 6-digit string passes.\" Google Sign-In's native flow reliably fails (fabricated OAuth client) and falls back to a fully hardcoded fake profile. Most real registrations land under a local_uid_${email.hashCode()} fallback with no corresponding Firebase Auth account.",
  },
  {
    key: "onboarding",
    order: 2,
    title: "User Onboarding",
    status: "partial",
    description:
      "13-screen real NavHost-driven flow, writes to users/{uid} on completion. instrumentProficiency, roleExperience, networkingLinks, profilePicturePath, and coverPicturePath are all collected but silently dropped from the final Firestore write.",
  },
  {
    key: "portfolio",
    order: 3,
    title: "Add Portfolio",
    status: "app-mocked",
    description:
      "11-screen publish flow for a piece of finished creative work (song/album/EP/etc.) — distinct from the separate \"Opportunities\" flow's projects collection (gig/collaboration postings). Schema (portfolio_projects) corrected this session to match it exactly — ownerUid, 6 named streaming links, ownerRoles, collaborators/credits, status/visibility, artworkTheme. Zero confirmed app-side writer — PortfolioViewModel.publishProject() is still 100% in-memory with a hardcoded author name.",
  },
  {
    key: "discover",
    order: 4,
    title: "Discover",
    status: "app-mocked",
    description:
      "100% mock data — DiscoverViewModel has zero Firestore references, filters a hardcoded 12-profile list. Every filterable field (experienceLevel, availabilityStatus, rating, etc.) would need to be sourced from real users/studios documents to go live.",
  },
  {
    key: "opportunities",
    order: 5,
    title: "Opportunities",
    status: "missing",
    description:
      "Maps to the existing projects collection (gig/collaboration postings — someone looking for a collaborator), per the round-2 reconciliation — folded in rather than a separate gig_opportunities collection. Distinct from the \"Add Portfolio\" flow (finished creative work someone already made). CONFIRMED 2026-08-12 (grounded Explore audit): 100% missing, not just unconfirmed. Zero `.collection(\"projects\")` calls anywhere in the app; `ProjectSchema` (BackendSchemas.kt:112) is defined but never instantiated. The only browse/apply UI (SuccessAndDashboardScreens.kt:2174-2435) is commented-out or unreachable dead code whose own copy admits \"Opportunities are coming soon.\" `UnifiedOpportunityCard` is defined but never called. A \"Live Opportunities Feed\" nav item exists but routes to an unrelated business-storefront screen. `ChatContext.OPPORTUNITY` is just a messaging-inbox filter tab around one hardcoded demo message, not a posting/browsing flow.",
  },
  {
    key: "communication",
    order: 6,
    title: "Communication & Collaboration",
    status: "partial",
    description:
      "connections collection has two inconsistent writers: FirebaseManager.sendConnectionRequest is correct (senderUid/targetUserId/status, composite doc ID); ConnectionsScreen.kt's second writer still writes a different, non-composite document for the same pair. chat_threads/messages exist in firestore.rules but weren't independently re-audited this session.",
  },
  {
    key: "marketplace",
    order: 7,
    title: "Marketplace — Buy/Sell/Rent",
    status: "app-mocked",
    description:
      "100% mocked, not wired to Firestore — reconciled: MarketplaceItemDoc.category is now taxonomy-backed (marketplace_categories), matching the app's real sale/rental/mixing/beats/services split. SCREEN-MAPPED 2026-08-12 (grounded Explore audit): one real, dashboard-wired browse screen (MarketplaceBetaScreen) with 6 category tabs including \"Gear Rentals\", an in-dialog item detail (not a separate screen), and a Saved/Favorites bookmarks tab — all backed by hardcoded lists, zero `marketplace_items` references anywhere. No create-listing, cart, checkout, or seller-messaging screens exist at all — confirmed absent by exhaustive grep, not just unaudited. MERGED 2026-08-13 (user call): the standalone \"Equipment Rental\" flow entry was folded in here rather than kept separate — rental was never anything more than one of this screen's own category tabs; see the equipment_rental key's former slot (order 9, now retired, not reassigned) for the original grounded finding this merge is based on.",
  },
  {
    key: "booking",
    order: 8,
    title: "Booking",
    status: "partial",
    description:
      "Currently studio-only (StudioBookingDoc) — not conceptually limited to studios long-term, the primary bookable entity type today is just Studio. Real writer exists but uses the wrong field names (artistUserId/totalCostAmount/statusKey instead of the schema's userId/totalAmount/bookingStatus) — confirmed still broken as of this session's last verification pass, despite two rounds of AI Studio claiming it was fixed.",
  },
  // key: "equipment_rental" (formerly order 9) — REMOVED 2026-08-13 (user
  // call): merged into `marketplace` above rather than kept as its own
  // entry. Order 9 is deliberately left unassigned, not reused by the next
  // flow — several other flows' descriptions reference each other by
  // number in prose (e.g. "distinct from Flow 21"), and renumbering all of
  // them to close the gap risked introducing new stale cross-references
  // for a purely cosmetic gain. See `marketplace`'s description for the
  // grounded finding this merge is based on.
  {
    key: "profile_management",
    order: 10,
    title: "Profile Management",
    status: "partial",
    description:
      "profileType + Business/Band/Venue/Talent extension fields are now real schema (this session). The app previously had three disconnected profileType-guessing UI implementations (dashboard badge substring match, PublicProfilePreviewScreen headline match, Discover's mock-only enum) that should converge on the real field once wired — not independently reverified against the new field this session. IMPORTANT: this is PROFILE settings (identity, roles, links, public-facing fields) — explicitly distinct from Flow 21 \"User Settings\" (app preferences: notifications, privacy, dark mode, language, currency). Do not conflate the two when adding screens here.",
  },
  {
    key: "services_availability",
    order: 11,
    title: "Services, Availability",
    status: "partial",
    description:
      "sessionAvailability (Talent), StudioDoc.services (taxonomy tags), and serviceOfferings (priced menu, Business) now modeled. No single unified \"availability\" concept spans all profile types yet — Talent/Band/Business/Venue each have their own shape.",
  },
  {
    key: "business_profiles",
    order: 12,
    title: "Business Profiles (Studios, Labels)",
    status: "partial",
    description:
      "UserDoc Business extension (businessName/Type/tagline/hours/GSTIN/services/equipment/teamMembers/serviceOfferings/gallery) modeled this session, matching BusinessProfileScreens.kt's mock \"Octave Sound Labs\" tabs. businessType only covers 4 of the PRD's ~8 sub-types (Studio/Label/School/Dealer — missing Production House/Booking Agency/Artist Manager/Rental Company).",
  },
  {
    key: "notifications",
    order: 13,
    title: "Notifications",
    status: "app-mocked",
    description:
      "notifications/{notificationId} typed and rules-ready this session. SCREEN-MAPPED 2026-08-12 (grounded Explore audit, upgraded from \"missing\" to \"app-mocked\" — a real reader screen does exist): NotificationsCenterScreen is a genuinely reachable, feature-rich screen (search/category filters, swipe-to-dismiss, pull-to-refresh) but 100% local mock state — zero `.collection(\"notifications\")` calls anywhere, despite UI copy that falsely claims \"Firestore Real-Time Sync.\" Three separate, mutually-inconsistent preferences surfaces also exist (Notifications Center settings, an onboarding notification-permissions step, and a general Settings \"Notification Feeds\" panel) — none persist anywhere beyond local state.",
  },
  {
    key: "public_profile",
    order: 14,
    title: "Public Profile",
    status: "partial",
    description:
      "Depends on Profile Management's template rendering — same profileType-driven templates. The public-facing read path wasn't separately audited this session.",
  },
  {
    key: "band",
    order: 15,
    title: "Band",
    status: "partial",
    description:
      "UserDoc Band extension (bandName/lineupMembers/techRiderUrl/stagePlotUrl) modeled. \"Shows\" (upcoming/past performances) closed 2026-08-12: `BandShow { title, date, venueName?, ticketUrl?, isPast }` + `UserDoc.shows?: BandShow[]` — a flat array, not a separate collection.",
  },
  {
    key: "verifications",
    order: 16,
    title: "Verifications",
    status: "app-mocked",
    description:
      "verification_requests collection and the admin Verification page are both real and working. SCREEN-MAPPED 2026-08-12 (grounded Explore audit, upgraded from \"missing\" to \"app-mocked\"): one real, reachable screen (VerificationScreen) exists off the Profile tab, but it's fully simulated — upload boxes hardcode literal filenames into local state (no file picker, no FirebaseStorage call) and never write to `verification_requests`. No status-tracking (pending/approved/rejected) screen exists; verified status is three local booleans driving badge colors. `VerificationRequestSchema` is defined in BackendSchemas.kt but never instantiated.",
  },
  {
    key: "reviews",
    order: 17,
    title: "Reviews",
    status: "app-mocked",
    description:
      "reviews collection and admin module both real and working. SCREEN-MAPPED 2026-08-12 (grounded Explore audit, upgraded from \"partial\" to \"app-mocked\" — the write side is now confirmed non-functional, not just unverified): a standalone Reputation Hub module (ReputationAndReviewsScreens.kt, reached from the dashboard, not from any post-booking prompt) has 4 real write screens — Write Review, Completed Project Review, Marketplace Gear Review, Studio Facility Review — plus business-response and report-review screens. None write to the real `reviews` Firestore collection: some persist to a local Room table, the rest are pure no-op UI. `ReviewSchema` (matching admin's ReviewDoc) exists in BackendSchemas.kt but is never referenced. The Business Profile \"Reviews & Gallery\" tab (already screen-mapped under business_profiles) remains a separate, read-only display of hardcoded data.",
  },
  {
    key: "subscription",
    order: 18,
    title: "Subscription",
    status: "missing",
    description: "Not modeled at all — no subscription/plan/tier collection exists anywhere in this schema yet.",
  },
  {
    key: "search",
    order: 19,
    title: "Search",
    status: "app-mocked",
    description:
      "CORRECTED 2026-08-13 (direct source read, SupportingScreens.kt): the 2026-08-12 audit was wrong — `UniversalSearchFlow` (SupportingScreens.kt:281, reached via the dashboard's top-bar Search icon, `SupportingModule.UNIVERSAL_SEARCH`) is a real, standalone, reachable screen, not a duplicate of Discover's or Booking's internal SEARCH_RESULTS states (those remain separately covered under `discover`/`booking`). 4-state (`SearchScreenState`: HOME/LIVE/RESULTS/EMPTY), fully mocked — a hardcoded 10-item `mockAllResults` list, zero Firestore reads. `DemoDataProvider.kt`'s `searchFilterCategories` list is still confirmed dead code, unrelated to this screen.",
  },
  {
    key: "recommendation_engine",
    order: 20,
    title: "Recommendation Engine",
    status: "missing",
    description:
      "Explicitly deprioritized by the user (\"ignore\"). For reference: GeminiRecommendationEngine.kt is real app-side code but neutered by a placeholder-API-key guard-check bug (checks for the wrong placeholder string, so it always falls through to hardcoded fallback data).",
  },
  {
    key: "user_settings",
    order: 21,
    title: "Settings & Support",
    status: "missing",
    description:
      "App-level settings (notifications/privacy/security/appearance), Help & Support (FAQ, raise a ticket, report user/listing, feedback), legal pages, and app version — explicitly distinct from Flow 1 Auth (password change lives there, not here) and Flow 10 Profile Management (identity/public-profile fields, not app preferences). user_settings/{userId} and support_tickets/moderation_flags are typed and rules-ready, but SettingsFlow and HelpAndSupportFlow app-side have zero real persistence anywhere — confirmed this session. Founded, ready-to-wire gaps, not speculative.",
  },
  {
    key: "cms_pages",
    order: 22,
    title: "CMS Pages",
    status: "partial",
    description:
      "page_content collection and the admin Pages module are both real and working, but frontend/website/ is 100% static HTML that never reads Firestore at runtime — edits made here don't reach the live site. Documented as a known gap (CMS_Content_Mapping.md), not silently dropped.",
  },
  {
    key: "qr_share",
    order: 23,
    title: "QR Code & Profile Sharing",
    status: "missing",
    description:
      "ADDED 2026-08-13 (direct source read, previously completely absent from this registry). `QrAndShareFlow` (SupportingScreens.kt:655, reached from the dashboard's QRShare shortcut) — 3-state screen (Home/Options/Preview, plain string state, not an enum) generating a shareable \"Premium Comp Card\" with a simulated PDF download (fake progress bar via `delay(150)` loop, no real file generation). No schema modeled for this at all — no shareable-link record, no QR payload, no download/share analytics. `DynamicLinkManager.kt` constructs outbound share links elsewhere in the app but this screen doesn't call it — confirmed no cross-wiring.",
  },
  {
    key: "saved_favorites",
    order: 24,
    title: "Saved & Favorites",
    status: "app-mocked",
    description:
      "ADDED 2026-08-13 (direct source read, previously only 1 of its 4 real tabs was documented — as `marketplace_saved_bookmarks` under the `marketplace` flow, which remains accurate for that one tab). `SavedAndFavoritesFlow` (SupportingScreens.kt:1023, reached from the dashboard's Saved shortcut) is the actual hub — `SavedTab` enum: ARTISTS, BANDS, STUDIOS, MARKETPLACE (+ EMPTY state), grid/list view toggle, per-tab remove action. All four tabs are 100% local `mutableStateListOf` mock data (hardcoded artists/studios/marketplace items) — zero Firestore reads or writes, no persisted \"saved\" relationship on any real document.",
  },
];
