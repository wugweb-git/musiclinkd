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
      "Maps to the existing projects collection (gig/collaboration postings — someone looking for a collaborator), per the round-2 reconciliation — folded in rather than a separate gig_opportunities collection. Distinct from the \"Add Portfolio\" flow (finished creative work someone already made). No confirmed real app-side writer.",
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
    title: "Marketplace (Buy/Sell)",
    status: "app-mocked",
    description:
      "100% mocked, not wired to Firestore. admin's MarketplaceCategory (GEAR/STEMS/SERVICES) doesn't match the app's real categories (sale/rental/mixing/beats/services) — never reconciled; low urgency until real write code exists.",
  },
  {
    key: "booking",
    order: 8,
    title: "Booking",
    status: "partial",
    description:
      "Currently studio-only (StudioBookingDoc) — not conceptually limited to studios long-term, the primary bookable entity type today is just Studio. Real writer exists but uses the wrong field names (artistUserId/totalCostAmount/statusKey instead of the schema's userId/totalAmount/bookingStatus) — confirmed still broken as of this session's last verification pass, despite two rounds of AI Studio claiming it was fixed.",
  },
  {
    key: "equipment_rental",
    order: 9,
    title: "Equipment Rental",
    status: "missing",
    description:
      "No dedicated schema. StudioDoc.equipment (added this session) covers gear display for the booking flow; no rental-specific transaction/listing model exists.",
  },
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
    status: "missing",
    description: "notifications/{notificationId} typed and rules-ready this session. Zero confirmed app-side writer or reader.",
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
      "UserDoc Band extension (bandName/lineupMembers/techRiderUrl/stagePlotUrl) modeled. \"Shows\" (upcoming/past performances) not modeled — flagged and deferred, needs a shape decision (flat array vs. its own collection).",
  },
  {
    key: "verifications",
    order: 16,
    title: "Verifications",
    status: "missing",
    description:
      "verification_requests collection and the admin Verification page are both real and working. No confirmed app-side writer for the actual document-submission flow.",
  },
  {
    key: "reviews",
    order: 17,
    title: "Reviews",
    status: "partial",
    description:
      "reviews collection and admin module both real and working. No confirmed app-side writer — the bookingId \"verified purchase\" linkage exists in rules but wasn't verified end-to-end against a real review submission.",
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
    status: "missing",
    description:
      "No dedicated search schema or index strategy audited this session — likely rides on Discover's data model, which is itself 100% mocked.",
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
];
