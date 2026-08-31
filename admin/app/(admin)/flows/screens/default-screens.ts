import type { FlowScreenDoc } from "@/types/firestore";

type ScreenSeed = Omit<FlowScreenDoc, "createdAt" | "updatedAt">;

/**
 * Seed content for the "Seed default screens" button on a flow's detail
 * page. Keyed by flow key (matches default-flows.ts). Grounded in this
 * session's audits of the real /app source — not guessed.
 *
 * Populated with real, audited screen breakdowns: auth, onboarding,
 * portfolio, profile_management, services_availability, user_settings,
 * booking, business_profiles, communication, discover, band, cms_pages,
 * public_profile (cross-reference only, see its entry), marketplace,
 * notifications, verifications, reviews, search, qr_share, saved_favorites.
 *
 * CORRECTED 2026-08-13: search was previously (wrongly) left unpopulated —
 * `UniversalSearchFlow` is a real, standalone screen, not a duplicate of
 * discover's/booking's internal search states. qr_share and
 * saved_favorites were previously entirely absent from default-flows.ts
 * too (saved_favorites' Marketplace tab was documented, but 3 of its 4
 * real tabs — Artists/Bands/Studios — were not). See each flow's
 * default-flows.ts description for the direct-source-read evidence.
 *
 * Deliberately NOT populated — equipment_rental, subscription,
 * recommendation_engine — confirmed to have zero dedicated screens (see
 * their default-flows.ts descriptions for the grounded evidence:
 * equipment_rental is folded into marketplace, subscription has no schema
 * or screens at all, recommendation_engine was explicitly deprioritized).
 * Seeding placeholder rows for these would misrepresent absence as
 * presence — do a proper audit pass first if that changes.
 *
 * UPDATED 2026-08-16: `opportunities` is now populated, but NOT because
 * real UI was found (it still has none — see
 * webapp/flows/opportunities/_flow.json's "100% missing... commented-out
 * or unreachable dead code"). These are screens 130–135, PROPOSED by
 * docs/04-Wireframes/PRODUCT-SPEC.md §D after MVP scope was confirmed —
 * unlike every other entry in this file, they are not grounded in a
 * Kotlin-source audit. Each entry's own notes say "Not built." Marketplace
 * also gained 5 proposed screens (125–129) the same way, appended after
 * its 3 real (if fully mocked) screens.
 */
export const DEFAULT_SCREENS: Record<string, ScreenSeed[]> = {
  home: [
    {
      key: "home_dashboard",
      order: 1,
      screenNumber: 1,
      title: "Home",
      states: ["default", "loading", "empty", "offline", "error"],
      collectionsTouched: ["users", "connections", "opportunities", "studioBookings", "feedPosts"],
      notes: "Returning-user dashboard. Every count and activity row is seeded sample data — there is no backend query behind them yet.",
    },
  ],

  auth: [
    {
      key: "email_verification",
      order: 90,
      screenNumber: 198,
      title: "Verify your email",
      states: ["sent", "verifying", "verified", "expired", "error"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. The approved plan's Auth +3. A link was sent and nothing has changed until it is followed — the copy must not claim otherwise.",
    },
    {
      key: "forgot_password",
      order: 91,
      screenNumber: 199,
      title: "Forgot password",
      states: ["default", "sent", "error"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. Ask for a reset. Deliberately says the same thing whether or not the address exists, so it cannot be used to discover accounts.",
    },
    {
      key: "reset_password",
      order: 92,
      screenNumber: 200,
      title: "Set a new password",
      states: ["default", "weak", "saved", "expired"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. Reached from the emailed link. An expired link is a state with a way to ask for a new one.",
    },
    {
      key: "splash",
      order: 1,
      screenNumber: 1,
      title: "Splash",
      states: ["loading", "resuming_local_session"],
      collectionsTouched: [],
      notes: "Session 'persistence' is local Room state resumption, not a Firebase Auth session check.",
    },
    {
      key: "welcome",
      order: 2,
      screenNumber: 2,
      title: "Welcome (choose auth method)",
      states: ["default"],
      collectionsTouched: [],
      notes: "Entry point offering Email, Mobile/OTP, and Google Sign-In.",
    },
    {
      key: "email_auth",
      order: 3,
      screenNumber: 3,
      title: "Email Auth (sign up / sign in)",
      states: ["signup", "signin", "loading", "error"],
      collectionsTouched: [],
      notes:
        "Real Firebase Auth SDK calls (createUserWithEmailAndPassword/signInWithEmailAndPassword) — but the project's fabricated API key means these fail server-side with a real error. The one honest failure mode in this flow.",
    },
    {
      key: "mobile_auth",
      order: 4,
      screenNumber: 4,
      title: "Mobile Number Entry",
      states: ["default", "error"],
      collectionsTouched: [],
    },
    {
      key: "otp_verification",
      order: 5,
      screenNumber: 5,
      title: "OTP Verification",
      states: ["entering", "verifying", "error", "success"],
      collectionsTouched: [],
      notes:
        "100% fake — verifyOtp()'s check is effectively \"any 6-digit string passes.\" No PhoneAuthProvider exists anywhere in the app.",
    },
    {
      key: "google_signin",
      order: 6,
      screenNumber: 6,
      title: "Google Sign-In",
      states: ["account_picker", "success", "fallback_fake"],
      collectionsTouched: [],
      notes:
        "The fabricated OAuth client ID means the real intent reliably fails; every failure path falls back to a fully hardcoded fake profile with zero network calls.",
    },
    {
      key: "registration_write",
      order: 7,
      screenNumber: 7,
      title: "Registration write (implicit, end of onboarding)",
      states: ["success", "fallback_uid"],
      collectionsTouched: ["users"],
      notes:
        "userId = FirebaseAuth.currentUser?.uid ?: \"local_uid_${email.hashCode()}\" — given the states above, most real signups today write under the fallback hash-of-email ID with no real Firebase Auth account behind it.",
    },
  ],
  onboarding: [
    {
      key: "specialisation",
      order: 90,
      screenNumber: 201,
      title: "Your specialisation",
      states: ["singer", "musician", "producer", "composer", "engineer", "dj"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. The approved plan's Onboarding +3, and the clearest recycling case in that column: SIX role-specific screens are six states of ONE, each asking what that role actually needs. Backed by seeded taxonomy — vocal_range, daw_tools, engineer_specialties, dj_types, composition_styles.",
    },
    {
      key: "work_preferences",
      order: 91,
      screenNumber: 202,
      title: "How you work",
      states: ["default"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. Remote or in person, travel, typical notice. PROPOSED fields workMode and lookingFor are marked as such in UserDoc rather than invented.",
    },
    {
      key: "privacy_preferences",
      order: 92,
      screenNumber: 203,
      title: "Privacy to start with",
      states: ["default"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. The privacy decisions worth making before a profile goes live, mirroring FLOW 21's controls rather than duplicating them.",
    },
    {
      key: "primary_role",
      order: 1,
      screenNumber: 8,
      title: "Primary Role Selection",
      states: ["default"],
      collectionsTouched: ["taxonomy_categories"],
      notes: "Reads person_types options.",
    },
    {
      key: "secondary_role",
      order: 2,
      screenNumber: 9,
      title: "Secondary Role (optional)",
      states: ["default", "skipped"],
      collectionsTouched: ["taxonomy_categories"],
    },
    {
      key: "genres",
      order: 3,
      screenNumber: 10,
      title: "Genres (pick up to 3)",
      states: ["default"],
      collectionsTouched: ["taxonomy_categories"],
    },
    {
      key: "languages",
      order: 4,
      screenNumber: 11,
      title: "Languages",
      states: ["default"],
      collectionsTouched: ["taxonomy_categories"],
    },
    {
      key: "instruments",
      order: 5,
      screenNumber: 12,
      title: "Instruments",
      states: ["default", "skipped_for_non_musician"],
      collectionsTouched: ["taxonomy_categories"],
    },
    {
      key: "instrument_proficiency",
      order: 6,
      screenNumber: 13,
      title: "Instrument Proficiency (per-instrument years/level)",
      states: ["default"],
      collectionsTouched: [],
      notes: "Collected in local state but confirmed dropped from the final Firestore write and from Room.",
    },
    {
      key: "location",
      order: 7,
      screenNumber: 14,
      title: "Location (country/state/city/area)",
      states: ["default"],
      collectionsTouched: [],
      notes: "Written at final submit; country has no Room column so it resets to the default on a resumed session.",
    },
    {
      key: "profile_creation",
      order: 8,
      screenNumber: 15,
      title: "Profile Creation (name/bio/photo/cover)",
      states: ["default"],
      collectionsTouched: [],
      notes: "profilePicturePath/coverPicturePath collected but confirmed dropped from the final write.",
    },
    {
      key: "networking_links",
      order: 9,
      screenNumber: 16,
      title: "Networking Links (Spotify/YouTube/SoundCloud/LinkedIn)",
      states: ["default"],
      collectionsTouched: [],
      notes: "Collected but confirmed dropped from the final write and from Room.",
    },
    {
      key: "persona_badges",
      order: 10,
      screenNumber: 17,
      title: "Persona Badges",
      states: ["default"],
      collectionsTouched: ["taxonomy_categories"],
    },
    {
      key: "review_confirm",
      order: 11,
      screenNumber: 18,
      title: "Review / Confirm Profile",
      states: ["default"],
      collectionsTouched: [],
    },
    {
      key: "submit_publish",
      order: 12,
      screenNumber: 19,
      title: "Submit / Publish Profile",
      states: ["submitting", "success", "error"],
      collectionsTouched: ["users"],
      notes:
        "Real write, confirmed — but missing instrumentProficiency, roleExperience, networkingLinks, profilePicturePath, coverPicturePath.",
    },
    {
      key: "dashboard_handoff",
      order: 13,
      screenNumber: 20,
      title: "Success → Dashboard Handoff",
      states: ["default"],
      collectionsTouched: [],
    },
  ],
  portfolio: [
    {
      key: "portfolio_settings",
      order: 90,
      screenNumber: 204,
      title: "Portfolio settings",
      states: ["default", "reorder", "archived"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. Ordering, visibility and archiving for the portfolio as a whole. The eleven existing screens publish ONE piece; this governs the set.",
    },
    {
      key: "portfolio_dashboard",
      order: 1,
      screenNumber: 21,
      title: "Portfolio Dashboard",
      states: ["empty", "populated"],
      collectionsTouched: ["portfolio_projects"],
    },
    {
      key: "choose_project_type",
      order: 2,
      screenNumber: 22,
      title: "Choose Project Type",
      states: ["default"],
      collectionsTouched: ["taxonomy_categories"],
      notes: "project_types options.",
    },
    {
      key: "project_information",
      order: 3,
      screenNumber: 23,
      title: "Project Information",
      states: ["default"],
      collectionsTouched: [],
    },
    {
      key: "streaming_links",
      order: 4,
      screenNumber: 24,
      title: "Add Streaming Links",
      states: ["default"],
      collectionsTouched: [],
    },
    {
      key: "select_role",
      order: 5,
      screenNumber: 25,
      title: "Select Your Role",
      states: ["default"],
      collectionsTouched: ["taxonomy_categories"],
      notes: "project_roles options.",
    },
    {
      key: "add_collaborators",
      order: 6,
      screenNumber: 26,
      title: "Add Collaborators",
      states: ["searching", "selected"],
      collectionsTouched: ["users"],
    },
    {
      key: "credit_attribution",
      order: 7,
      screenNumber: 27,
      title: "Credit Attribution",
      states: ["default"],
      collectionsTouched: [],
    },
    {
      key: "media_preview",
      order: 8,
      screenNumber: 28,
      title: "Project Media Preview",
      states: ["default"],
      collectionsTouched: [],
      notes: "\"Artwork\" here is a palette-theme keyword, not an uploaded image — no real upload UI exists.",
    },
    {
      key: "publish_project",
      order: 9,
      screenNumber: 29,
      title: "Publish Project",
      states: ["ready", "publishing", "error"],
      collectionsTouched: ["portfolio_projects"],
      notes: "Not confirmed real — PortfolioViewModel.publishProject() is still 100% in-memory.",
    },
    {
      key: "success",
      order: 10,
      screenNumber: 30,
      title: "Success State",
      states: ["default"],
      collectionsTouched: [],
    },
    {
      key: "profile_project_view",
      order: 11,
      screenNumber: 31,
      title: "Published Project — Profile View",
      states: ["default"],
      collectionsTouched: ["portfolio_projects"],
    },
  ],
  profile_management: [
    {
      key: "profile_overview",
      order: 1,
      screenNumber: 32,
      title: "Profile Overview / Dashboard",
      states: ["default"],
      collectionsTouched: ["users"],
    },
    {
      key: "edit_identity",
      order: 2,
      screenNumber: 33,
      title: "Edit Profile (name/bio/location/roles/genres/languages)",
      states: ["editing", "saving", "saved", "error"],
      collectionsTouched: ["users"],
      notes: "Real, confirmed write (SetOptions.merge()) — this is the fix that landed correctly earlier this session.",
    },
    {
      key: "profile_type_selection",
      order: 3,
      screenNumber: 34,
      title: "Profile Type Selection (Talent/Band/Business/Venue)",
      states: ["default"],
      collectionsTouched: ["users"],
      notes: "profileType — write-locked after creation, cannot be self-changed once set.",
    },
    {
      key: "business_profile_editor",
      order: 4,
      screenNumber: 35,
      title: "Business Profile Editor",
      states: ["visible_if_business"],
      collectionsTouched: ["users"],
    },
    {
      key: "band_profile_editor",
      order: 5,
      screenNumber: 36,
      title: "Band Profile Editor",
      states: ["visible_if_band"],
      collectionsTouched: ["users"],
    },
    {
      key: "venue_profile_editor",
      order: 6,
      screenNumber: 37,
      title: "Venue Profile Editor",
      states: ["visible_if_venue"],
      collectionsTouched: ["users"],
    },
    {
      key: "talent_profile_editor",
      order: 7,
      screenNumber: 38,
      title: "Talent Profile Editor",
      states: ["visible_if_talent"],
      collectionsTouched: ["users", "role_profiles"],
    },
    {
      key: "external_links_editor",
      order: 8,
      screenNumber: 39,
      title: "External Links Editor",
      states: ["default"],
      collectionsTouched: ["users"],
    },
    {
      key: "public_profile_preview",
      order: 9,
      screenNumber: 40,
      title: "Public Profile Preview",
      states: ["talent_template", "band_template", "business_template", "venue_template"],
      collectionsTouched: ["users", "portfolio_projects", "reviews"],
      notes:
        "Previously driven by 3 disconnected string-matching heuristics instead of a real field — should now key off the real profileType.",
    },
    {
      key: "profile_completion",
      order: 10,
      screenNumber: 41,
      title: "Profile Completion Indicator",
      states: ["incomplete", "complete"],
      collectionsTouched: ["users"],
      notes: "Derived client-side, not stored. Was a hidden/commented-out card in the real source as of this session's audit.",
    },
  ],
  services_availability: [
    {
      key: "my_services",
      order: 90,
      screenNumber: 196,
      title: "My services",
      states: ["default", "paused", "empty"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. A provider's own list with publication state. service_offerings_editor edits one; this lists them all.",
    },
    {
      key: "availability_toggle",
      order: 1,
      screenNumber: 42,
      title: "Availability Toggle (Talent)",
      states: ["available_now", "open_for_gigs", "booked_soon", "on_tour", "unavailable"],
      collectionsTouched: ["users"],
      notes: "sessionAvailability + the coarser isAvailable boolean.",
    },
    {
      key: "service_offerings_editor",
      order: 2,
      screenNumber: 43,
      title: "Service Offerings Editor (Business)",
      states: ["default"],
      collectionsTouched: ["users"],
      notes: "serviceOfferings (priced menu) vs. services (taxonomy filter tags) — two different fields, added this session.",
    },
    {
      key: "studio_services_amenities",
      order: 3,
      screenNumber: 44,
      title: "Studio Services & Amenities",
      states: ["default"],
      collectionsTouched: ["studios", "taxonomy_categories"],
      notes: "studio_services (offered) vs. studio_amenities (physical features) — distinct taxonomy categories.",
    },
    {
      key: "rate_pricing_editor",
      order: 4,
      screenNumber: 45,
      title: "Rate / Pricing Editor",
      states: ["default"],
      collectionsTouched: ["users", "studios"],
      notes: "hourlyRate migrated to a real number (per-hour INR equivalent, 0 = unset) — 2026-08-12. Editor sheets now use a numeric input; list columns format it via lib/utils.ts's formatHourlyRate().",
    },
    {
      key: "session_configuration",
      order: 5,
      screenNumber: 46,
      title: "Session Configuration (booking-side service selection)",
      states: ["default"],
      collectionsTouched: ["studio_bookings", "taxonomy_categories"],
      notes: "sessionType/durationTier and the rest of StudioBookingDoc (engineerRequested/selectedEquipment/addons/taxAmount/depositAmount/discountAmount/promoCode/paymentMethod/notes) are now editable via the Bookings admin module's row-click editor sheet (closed 2026-08-13) — previously view-only with status-transition buttons only.",
    },
  ],
  user_settings: [
    {
      key: "account_settings",
      order: 1,
      screenNumber: 47,
      title: "Account Settings",
      states: ["default", "editing", "saving", "saved", "error"],
      collectionsTouched: ["users"],
      notes:
        "SettingsSubPage.ACCOUNT — edits name/email/phone/primaryRole/genres. Overlaps with Flow 10's Edit Profile screen in the real app; worth reconciling into one screen if the app ever gets rebuilt, not something to silently merge here.",
    },
    {
      key: "notification_settings",
      order: 2,
      screenNumber: 48,
      title: "Notification Settings",
      states: ["default"],
      collectionsTouched: ["user_settings"],
      notes: "pushNotify/emailNotify/smsNotify/marketplaceAlerts/bookingsAlerts — no real persistence in the app yet.",
    },
    {
      key: "privacy_settings",
      order: 3,
      screenNumber: 49,
      title: "Privacy Settings",
      states: ["default", "profile_visibility", "contact_privacy", "online_status", "search_visibility"],
      collectionsTouched: ["user_settings"],
      notes: "profileVisible/hideContactDetails/hideFollowersList + a \"Download My Data\" action (UI-only today).",
    },
    {
      key: "security_settings",
      order: 4,
      screenNumber: 50,
      title: "Security Settings",
      states: ["default", "two_factor_setup", "two_factor_on", "connected_accounts", "security_alert"],
      collectionsTouched: [],
      notes:
        "Two-factor toggle + a \"Reset Access Password\" button live here in the real app today — conceptually that action belongs to Flow 1 (Auth), per this flow's scope note. Flag to AI Studio if you want it physically moved, not just re-labeled.",
    },
    {
      key: "appearance_settings",
      order: 5,
      screenNumber: 51,
      title: "Appearance Settings",
      states: ["default", "language", "accessibility"],
      collectionsTouched: ["user_settings"],
      notes: "Theme (Light/Dark/Cosmic) + accent color — darkModeEnabled is the only piece of this in the typed schema.",
    },
    {
      key: "help_center",
      order: 6,
      screenNumber: 52,
      title: "Help Center",
      states: ["default", "searching", "no_results"],
      collectionsTouched: [],
      notes: "Landing/menu screen for the sub-flows below.",
    },
    {
      key: "faq",
      order: 7,
      screenNumber: 53,
      title: "FAQ",
      states: ["default", "searching"],
      collectionsTouched: [],
      notes: "Static hardcoded 4-item list — not Firestore-backed.",
    },
    {
      key: "raise_ticket",
      order: 8,
      screenNumber: 54,
      title: "Contact Support / Raise Ticket",
      states: ["default", "submitting", "success"],
      collectionsTouched: ["support_tickets"],
      notes: "Real collection exists and is rules-ready; the app currently fabricates a fake client-side ticket ID instead of writing one.",
    },
    {
      key: "report_user",
      order: 9,
      screenNumber: 55,
      title: "Report User",
      states: ["default", "submitting", "success"],
      collectionsTouched: ["moderation_flags"],
      notes: "Real collection + admin moderation queue exist; the app currently just shows a snackbar and discards the input.",
    },
    {
      key: "report_listing",
      order: 10,
      screenNumber: 56,
      title: "Report Listing",
      states: ["default", "submitting", "success"],
      collectionsTouched: ["moderation_flags"],
      notes: "Same gap as Report User.",
    },
    {
      key: "feedback",
      order: 11,
      screenNumber: 57,
      title: "Feedback",
      states: ["default", "submitting", "success"],
      collectionsTouched: [],
      notes: "No Firestore write and no external mailto/link — input is entirely discarded today.",
    },
    {
      key: "legal_pages",
      order: 12,
      screenNumber: 58,
      title: "Legal Pages (Privacy Policy, Terms)",
      states: ["default"],
      collectionsTouched: ["page_content"],
      notes:
        "Lives under Flow 22 CMS Pages' page_content collection (route \"/legal/privacy\", \"/legal/terms\") — surfaced here as an entry point since Settings is where a user would look for it.",
    },
    {
      key: "app_version",
      order: 13,
      screenNumber: 59,
      title: "App Version / About",
      states: ["default"],
      collectionsTouched: [],
      notes: "Static build info (version name/code) — no Firestore involvement expected.",
    },
    {
      key: "username",
      order: 14,
      screenNumber: 159,
      title: "Username",
      states: ["default", "checking", "taken", "saved"],
      collectionsTouched: ["users"],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-21 brief. Owns the handle and the musiclinkd.com/<username> preview. Availability checking is a state, not a screen. Must never expose an internal document id.",
    },
    {
      key: "change_email_phone",
      order: 15,
      screenNumber: 160,
      title: "Change email or phone",
      states: ["email", "phone", "verifying", "verified", "error"],
      collectionsTouched: ["users"],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-21 brief, which is explicit that the change must not be claimed as done before verification succeeds. Email and phone are two states of one screen: same shape, same verification step. OTP UI only — no authentication logic.",
    },
    {
      key: "active_sessions",
      order: 16,
      screenNumber: 161,
      title: "Signed-in devices",
      states: ["default", "current_only"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-21 brief. Device, platform, approximate location, last active, and which one you are on. Deliberately does not show precise location or full device fingerprints — the brief says not to expose excessive device data.",
    },
    {
      key: "blocked_accounts",
      order: 17,
      screenNumber: 162,
      title: "Blocked accounts",
      states: ["default", "empty"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-21 brief. Blocked people and businesses in one list, with unblock. Blocking is immediate and separate from reporting, which the copy states.",
    },
    {
      key: "data_privacy",
      order: 18,
      screenNumber: 163,
      title: "Data & privacy",
      states: ["default", "export_requested", "export_processing", "export_ready", "export_expired", "personalisation"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-21 brief. The export lifecycle is four states of one screen rather than four screens, and the copy never claims immediate availability. Personalisation (personalised / limited / off) is a state here and must not imply that turning it off disables core search.",
    },
    {
      key: "delete_account",
      order: 19,
      screenNumber: 164,
      title: "Deactivate or delete",
      states: ["choose", "deactivate_confirm", "delete_consequences", "delete_confirm", "processing"],
      collectionsTouched: ["users"],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-21 brief. Deactivate and delete share one danger screen and progress through states, so a destructive action is never one tap: consequences are shown before the confirmation, and the confirmation needs a typed phrase. Deletion is described as requested-and-processing, not instant.",
    },
  ],
  booking: [
    {
      key: "provider_dashboard",
      order: 90,
      screenNumber: 192,
      title: "Provider dashboard",
      states: ["default", "quiet"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. The approved plan's Booking +3, provider side. What needs answering today, then the rooms. Deliberately not an analytics page.",
    },
    {
      key: "provider_requests",
      order: 91,
      screenNumber: 193,
      title: "Booking requests",
      states: ["pending", "accepted", "declined", "empty"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. Incoming requests with the four outcomes as states of one list.",
    },
    {
      key: "provider_availability",
      order: 92,
      screenNumber: 194,
      title: "Manage availability",
      states: ["default", "blocked", "hours"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. The provider's own calendar. Reuses .ml-calendar and hands off to FLOW 11 for rates, which owns them.",
    },
    {
      key: "studio_discovery",
      order: 1,
      screenNumber: 60,
      title: "Studio Discovery",
      states: ["default"],
      collectionsTouched: ["studios"],
      notes: "Featured/Nearby/Popular sections — StudioDoc has no featured/popular flags yet, so these can't be real filters today.",
    },
    {
      key: "filters",
      order: 2,
      screenNumber: 61,
      title: "Filters (bottom sheet)",
      states: ["default"],
      collectionsTouched: ["taxonomy_categories"],
      notes: "City/distance/price + amenity chips — distance needs StudioDoc.geo (added this session, unconfirmed as app-used); hourlyRate is now a real number (migrated 2026-08-12), so price sort is unblocked on the schema side — app-side filter UI still unconfirmed.",
    },
    {
      key: "search_results",
      order: 3,
      screenNumber: 62,
      title: "Search Results",
      states: ["default", "empty"],
      collectionsTouched: ["studios"],
    },
    {
      key: "studio_profile",
      order: 4,
      screenNumber: 63,
      title: "Studio Profile",
      states: ["default"],
      collectionsTouched: ["studios", "reviews"],
      notes: "About/Rooms sections now have schema (StudioDoc.rooms, added 2026-08-12). Engineers-as-bookable-staff stays deliberately deferred — user's explicit call, not re-opened.",
    },
    {
      key: "equipment_screen",
      order: 5,
      screenNumber: 64,
      title: "Equipment",
      states: ["default"],
      collectionsTouched: ["studios"],
      notes: "StudioDoc.equipment (structured, added this session) — not yet confirmed as app-written.",
    },
    {
      key: "availability_calendar",
      order: 6,
      screenNumber: 65,
      title: "Availability Calendar",
      states: ["monthly", "weekly", "timeline"],
      collectionsTouched: ["studio_bookings"],
      notes: "Slot available/booked/pending status derives from querying existing bookings for the date range.",
    },
    {
      key: "configure_session",
      order: 7,
      screenNumber: 66,
      title: "Configure Session",
      states: ["default"],
      collectionsTouched: ["taxonomy_categories"],
      notes: "sessionType/durationTier/engineerRequested/selectedEquipment/addons — all typed this session, none confirmed app-wired.",
    },
    {
      key: "booking_summary",
      order: 8,
      screenNumber: 67,
      title: "Booking Summary",
      states: ["default"],
      collectionsTouched: ["studio_bookings"],
      notes: "taxAmount/depositAmount/discountAmount/promoCode — typed this session, none confirmed app-wired.",
    },
    {
      key: "payment_screen",
      order: 9,
      screenNumber: 68,
      title: "Payment",
      states: ["default", "processing", "error", "success"],
      collectionsTouched: ["studio_bookings"],
      notes: "paymentMethod/paymentStatus/paymentTransactionId.",
    },
    {
      key: "booking_success",
      order: 10,
      screenNumber: 69,
      title: "Booking Success",
      states: ["default"],
      collectionsTouched: ["studio_bookings"],
      notes:
        "The real write here is confirmed BROKEN as of this session's last verification pass — still uses artistUserId/totalCostAmount/statusKey instead of the schema's userId/totalAmount/bookingStatus.",
    },
    {
      key: "my_bookings",
      order: 11,
      screenNumber: 70,
      title: "My Bookings",
      states: ["upcoming", "past", "cancelled"],
      collectionsTouched: ["studio_bookings"],
    },
  ],
  business_profiles: [
    {
      key: "business_gallery",
      order: 90,
      screenNumber: 197,
      title: "Business gallery",
      states: ["default", "empty"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. The room and equipment photography as its own route, so it can be linked and shared. The tab shows a sample; this is all of it.",
    },
    {
      key: "business_overview",
      order: 1,
      screenNumber: 71,
      title: "Overview",
      states: [
        "default",
        // FLOW-12 brief, 2026-08-31. Draft/unpublished/suspended/not-found are
        // what this same profile looks like in each lifecycle state — one
        // screen, not four. Empty sections are states too: the brief says an
        // owner and a visitor should see different copy for "no services yet".
        "draft", "unpublished", "suspended", "not_found",
        "no_services", "no_portfolio", "no_team", "no_reviews", "no_availability",
      ],
      collectionsTouched: ["users"],
    },
    {
      key: "create_business",
      order: 8,
      screenNumber: 171,
      title: "Create a business",
      states: ["basics", "identity", "location", "hours", "preview", "published"],
      collectionsTouched: ["businesses"],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-12 brief, which asks for lightweight creation and explicitly warns against a huge form. Six steps as STATES of one screen: you can see how far along you are and go back without losing a back-stack entry per field. Identity, logo, cover, description, location and hours are those steps, not six screens.",
    },
    {
      key: "business_dashboard",
      order: 9,
      screenNumber: 172,
      title: "Business dashboard",
      states: ["default", "quiet"],
      collectionsTouched: ["businesses", "studio_bookings"],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-12 brief. The owner's home: today's activity, pending requests, upcoming bookings, and quick actions. The brief warns against turning this into a generic analytics dashboard, so the numbers are few and each one links to the thing it counts. \"quiet\" is the honest state for a business with nothing happening today.",
    },
    {
      key: "business_services_tab",
      order: 2,
      screenNumber: 72,
      title: "Services Tab",
      states: ["default", "owner"],
      collectionsTouched: ["users"],
      notes: "serviceOfferings (priced menu) — added this session.",
    },
    {
      key: "business_equipment_tab",
      order: 3,
      screenNumber: 73,
      title: "Equipment Tab",
      states: ["default"],
      collectionsTouched: ["users", "studios"],
      notes: "UserDoc.equipmentList for a general Business profile, or StudioDoc.equipment if businessType is Studio.",
    },
    {
      key: "business_team_tab",
      order: 4,
      screenNumber: 74,
      title: "Team & Works Tab",
      states: ["default", "owner", "invite"],
      collectionsTouched: ["users"],
      notes: "teamMembers — added this session, matches BusinessProfileScreens.kt's mock \"Octave Sound Labs\" content exactly.",
    },
    {
      key: "business_reviews_gallery_tab",
      order: 5,
      screenNumber: 75,
      title: "Reviews & Gallery Tab",
      states: ["default"],
      collectionsTouched: ["reviews", "users"],
      notes: "galleryUrls added this session; reviews collection is real and separate.",
    },
    {
      key: "business_book_now",
      order: 6,
      screenNumber: 76,
      title: "Book Now (CTA)",
      states: ["default"],
      collectionsTouched: [],
      notes: "Routes into the Booking flow (Flow 8) — no schema of its own.",
    },
    {
      key: "business_owner_insights",
      order: 7,
      screenNumber: 77,
      title: "Owner Mode / Business Insights",
      states: ["visible_if_owner", "leads", "analytics"],
      collectionsTouched: [],
      notes: "100% mocked as of this session's audit — BusinessOwnerInsightsScreen has no real data source.",
    },
  ],
  communication: [
    {
      key: "connection_requests_tab",
      order: 1,
      screenNumber: 78,
      title: "Connection Requests",
      states: ["default"],
      collectionsTouched: ["connections"],
      notes: "Two writers exist for this collection and disagree on document shape — see Flow-level notes / Prompt F §2.",
    },
    {
      key: "collaborators_tab",
      order: 2,
      screenNumber: 79,
      title: "Collaborators",
      states: ["default"],
      collectionsTouched: ["connections"],
    },
    {
      key: "ai_matchmaker_tab",
      order: 3,
      screenNumber: 80,
      title: "AI Matchmaker Tab",
      states: ["unreachable"],
      collectionsTouched: [],
      notes: "Tab exists in the underlying code but is missing from the visible tab list — unreachable through the real UI, not just hidden by a flag.",
    },
    {
      key: "chat_thread_list",
      order: 4,
      screenNumber: 81,
      title: "Chat Thread List",
      states: ["default", "empty"],
      collectionsTouched: ["chat_threads"],
    },
    {
      key: "chat_conversation",
      order: 5,
      screenNumber: 82,
      title: "Chat Conversation",
      states: ["default", "sending", "error"],
      collectionsTouched: ["chat_threads"],
      notes: "messages subcollection under chat_threads/{threadId}.",
    },
    {
      key: "networking_feed",
      order: 6,
      screenNumber: 83,
      title: "Networking Feed (post / view)",
      states: ["default", "posting"],
      collectionsTouched: ["feed_posts"],
      notes: "Real read/write exists, but write side uses \"content\" not the admin schema's \"text\", plus a mediaUrl read/write mismatch — flagged, needs a migration-aware fix (Prompt F §5).",
    },
    {
      key: "collaborations",
      order: 8,
      screenNumber: 184,
      title: "Collaborations",
      states: ["active", "pending", "completed", "drafts", "empty", "no_invitations"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-06 Collaboration brief. Messaging answers \"can we talk?\"; Collaboration answers \"are we working together, on what, and what happens next?\". This is the list of that second thing. Four tabs are four states of one list. BETA.",
    },
    {
      key: "collaboration_project",
      order: 9,
      screenNumber: 185,
      title: "Project",
      states: [
        "overview", "participants", "files", "timeline", "activity", "notes",
        "on_hold", "completed", "cancelled", "archived",
        "no_files", "no_milestones", "no_activity", "permission_denied", "deleted",
      ],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from FLOW 06. The workspace. Its areas (overview, participants, files, timeline, activity, notes) are states of one screen rather than six, and the lifecycle (on hold, completed, cancelled, archived) is a state of the same screen. The brief warns against building Jira: milestones exist, tasks deliberately do not. BETA.",
    },
    {
      key: "collaboration_proposal",
      order: 10,
      screenNumber: 186,
      title: "Proposal",
      states: ["draft", "received", "counter", "accepted", "declined", "invitation"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from FLOW 06. Scope, deliverables, timeline and compensation. Sending, receiving, countering and the outcome are one negotiation, so they are states of one screen. The brief says not to invent legal terms, so it records what was agreed without pretending to be a contract. BETA.",
    },
    {
      key: "collaboration_files",
      order: 11,
      screenNumber: 187,
      title: "Project files",
      states: ["all", "audio", "video", "images", "documents", "versions", "uploading", "upload_error", "access", "unavailable"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from FLOW 06, which is explicit that audio is a first-class collaboration object and must not look like a generic attachment. Waveform, duration, contributor and version history. Filters are states. BETA.",
    },
    {
      key: "networking_feed_artist_profile",
      order: 7,
      screenNumber: 84,
      title: "Artist Profile (from Networking Feed)",
      states: ["default"],
      collectionsTouched: [],
      notes:
        "ADDED 2026-08-13 (direct source read). SupportingModule.ARTIST_PROFILE, renders ArtistProfileScreen (UnifiedComponents.kt:2870) — reached only by tapping a post in Networking Feed. Confirmed distinct from Discover's FULL_PROFILE, which renders a different composable (FullTalentProfileScreen, DiscoverScreens.kt) — a 4th disconnected profile-view implementation on top of the 3 already flagged under profile_management's flow-level note (dashboard badge substring match, PublicProfilePreviewScreen headline match, Discover's mock-only enum). Not independently audited for its own data-source/persistence behavior this pass.",
    },
  ],
  discover: [
    {
      key: "discover_feed",
      order: 1,
      screenNumber: 85,
      title: "Discover Feed / Home",
      states: ["default"],
      collectionsTouched: [],
      notes: "100% mocked — DiscoverViewModel has zero Firestore references, filters a hardcoded 12-profile list.",
    },
    {
      key: "advanced_filters",
      order: 2,
      screenNumber: 86,
      title: "Advanced Filters (bottom sheet)",
      states: ["default"],
      collectionsTouched: [],
      notes: "Sort/city/area/language/genre/experience-seniority/verified/remote/session-work — all filter mock data, not real fields.",
    },
    {
      key: "discover_profile_detail",
      order: 3,
      screenNumber: 87,
      title: "Profile Detail View",
      states: ["default"],
      collectionsTouched: [],
      notes: "Reads from the same mock DiscoverProfile list, not users.",
    },
    {
      key: "saved_profiles",
      order: 4,
      screenNumber: 88,
      title: "Saved Profiles",
      states: ["empty", "populated"],
      collectionsTouched: ["user_bookmarks"],
      notes: "Screen exists in the real app; user_bookmarks collection is real and rules-ready, but the screen is 100% unwired to it.",
    },
    {
      key: "trending_recommended_sections",
      order: 5,
      screenNumber: 89,
      title: "Trending Projects / Recommended Collaborators",
      states: ["hidden"],
      collectionsTouched: [],
      notes: "Composable functions still exist in source but their call sites are commented out — confirmed hidden, not deleted.",
    },
    /* Registered 2026-08-30. These 16 screens already existed on disk under
       webapp/flows/discover/ WITH correct screen.json metadata and finished
       designs, but were never added here — so the generated flows-map.json
       omitted them and all 16 were invisible in the workbench index. Titles,
       numbers, states and notes below are read from each screen's own
       screen.json, not re-invented. */
    {
      key: "global_search",
      order: 6,
      screenNumber: 140,
      title: "Global Search",
      states: ["idle", "typing", "no_results"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "search_results",
      order: 7,
      screenNumber: 141,
      title: "Search Results",
      states: ["default", "no_results"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "people_talent",
      order: 8,
      screenNumber: 142,
      title: "People / Talent",
      states: ["default"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "businesses",
      order: 9,
      screenNumber: 143,
      title: "Businesses",
      states: ["default"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "business_profile",
      order: 10,
      screenNumber: 151,
      title: "Business Profile",
      states: ["default"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "studios_spaces",
      order: 11,
      screenNumber: 144,
      title: "Studios & Spaces",
      states: ["default"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "studio_profile_discover",
      order: 12,
      screenNumber: 152,
      title: "Studio Profile",
      states: ["default"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "services",
      order: 13,
      screenNumber: 145,
      title: "Services",
      states: ["default"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "service_detail",
      order: 14,
      screenNumber: 154,
      title: "Service Detail",
      states: ["default"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "projects",
      order: 15,
      screenNumber: 146,
      title: "Projects / Portfolio",
      states: ["default"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "project_detail",
      order: 16,
      screenNumber: 153,
      title: "Project Detail",
      states: ["default"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "music_audio",
      order: 17,
      screenNumber: 147,
      title: "Music / Audio",
      states: ["default", "playing"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "communities",
      order: 18,
      screenNumber: 148,
      title: "Communities",
      states: ["default"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "community_detail",
      order: 19,
      screenNumber: 155,
      title: "Community Detail",
      states: ["default", "joined"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "nearby",
      order: 20,
      screenNumber: 150,
      title: "Nearby",
      states: ["default", "permission_denied"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
    {
      key: "discover_opportunities",
      order: 21,
      screenNumber: 149,
      title: "Opportunities",
      states: ["default"],
      collectionsTouched: [],
      notes: "Designed 2026-08-29 against the Flow 4 Discover spec. Discover is find-someone/something inside the ecosystem; Market (flow 7) is buy/sell/rent — the two must not converge. No backend: DiscoverViewModel has zero Firestore references today.",
    },
  ],
  band: [
    {
      key: "band_profile_layout",
      order: 1,
      screenNumber: 90,
      title: "Band Profile (public view)",
      states: ["default", "owner", "private", "members_only", "no_portfolio", "no_events", "no_followers"],
      collectionsTouched: ["users"],
      notes: "Renders off profileType === \"BAND\" now; previously one of three disconnected template-selection heuristics.",
    },
    {
      key: "band_create",
      order: 4,
      screenNumber: 174,
      title: "Create a band",
      states: ["identity", "members", "invitation", "published"],
      collectionsTouched: ["users"],
      notes:
        "PROPOSED 2026-08-31 from the deferred-flows brief, FLOW 15. Identity, members and the invitation a member receives are steps of one creation, so they are states. A band is a collective professional entity, not a group chat — the copy says so and the fields (type, formation year, languages) follow the brief. BETA.",
    },
    {
      key: "band_settings",
      order: 5,
      screenNumber: 175,
      title: "Band settings",
      states: ["default", "privacy", "leave", "remove_member", "delete"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from FLOW 15. Leave, remove-member and delete are the brief's three destructive actions; each is a state that explains consequences before confirming, never a one-tap row. BETA.",
    },
    {
      key: "ensemble_lineup",
      order: 2,
      screenNumber: 91,
      title: "Ensemble Lineup Display",
      states: ["default", "owner", "pending", "roles", "remove_member", "empty"],
      collectionsTouched: ["users"],
      notes: "lineupMembers — real field, confirmed matches the app's existing lineup-carousel UI shape.",
    },
    {
      key: "upcoming_shows",
      order: 3,
      screenNumber: 92,
      title: "Upcoming Shows List",
      states: ["hardcoded", "past", "empty"],
      collectionsTouched: [],
      notes: "\"Shows\" is not modeled anywhere — the real UI shows a hardcoded list. Flagged, deferred pending a shape decision (flat array vs. own collection).",
    },
  ],
  cms_pages: [
    {
      key: "marketing_homepage",
      order: 1,
      screenNumber: 93,
      title: "Marketing Homepage",
      states: ["default"],
      collectionsTouched: [],
      notes: "Static HTML, never reads Firestore at runtime.",
    },
    {
      key: "about_pricing_careers",
      order: 2,
      screenNumber: 94,
      title: "About / Pricing / Careers",
      states: ["default", "pricing", "careers"],
      collectionsTouched: [],
      notes: "Static HTML pages under frontend/website/.",
    },
    {
      key: "legal_pages_site",
      order: 3,
      screenNumber: 95,
      title: "Legal Pages (site)",
      states: ["default", "policy", "restricted"],
      collectionsTouched: [],
      notes: "Static HTML today — cross-reference Flow 21's \"Legal Pages\" entry point, and the admin's page_content collection below.",
    },
    {
      key: "blog_changelog",
      order: 4,
      screenNumber: 96,
      title: "Blog / Changelog",
      states: ["default", "article", "announcement", "archived", "not_found"],
      collectionsTouched: [],
      notes: "Generated from CHANGELOG.md at build time via scripts/generate-changelog.mjs — not Firestore-backed.",
    },
    {
      key: "content_home",
      order: 6,
      screenNumber: 176,
      title: "Guides & help",
      states: ["default", "category", "searching", "no_results", "offline"],
      collectionsTouched: ["page_content"],
      notes:
        "PROPOSED 2026-08-31 from FLOW 22. The READER's journey — the brief is explicit that authoring belongs to Admin, not here. Featured, latest, categories and search in one screen; the category being browsed is a state. BETA.",
    },
    {
      key: "content_article",
      order: 7,
      screenNumber: 177,
      title: "Article",
      states: ["default", "faq", "policy", "help", "archived", "restricted", "not_found", "offline"],
      collectionsTouched: ["page_content"],
      notes:
        "PROPOSED 2026-08-31 from FLOW 22. One editorial reader for every content type — article, FAQ, policy, help — because they differ in content, not in shape. Archived, restricted and not-found are states of the same reader. Editorial typography, generous measure; the brief warns against dense documentation pages. BETA.",
    },
    {
      key: "admin_pages_editor",
      order: 5,
      screenNumber: 97,
      title: "Admin Pages Editor",
      states: ["default"],
      collectionsTouched: ["page_content"],
      notes: "Real, working admin CRUD — but frontend/website/ never reads this collection at runtime, so edits here don't reach the live site. The core documented gap for this flow.",
    },
  ],
  public_profile: [
    {
      key: "public_profile_view",
      order: 1,
      screenNumber: 98,
      title: "Public Profile (cross-reference)",
      collectionsTouched: ["users", "portfolio_projects", "reviews"],
      notes:
        "Same screen as Flow 10's \"Public Profile Preview\" — this entry exists so Flow 14 isn't empty in the registry, not because there's a second, separate implementation. See Flow 10 for the full note. Forks into 4 profileType-driven templates below (98-1..98-4) — this is a genuine schema fork (different UserDoc extension fields render per template), not just a UI state, so it's modeled with variantOf rather than a plain states[] entry.",
    },
    {
      key: "public_profile_view_talent",
      order: 2,
      variantOf: 98,
      variantLabel: "Talent template",
      title: "Public Profile — Talent template",
      collectionsTouched: ["users", "portfolio_projects", "reviews"],
      notes: "profileType === \"TALENT\" — renders base UserDoc fields (no extension block).",
    },
    {
      key: "public_profile_view_band",
      order: 3,
      variantOf: 98,
      variantLabel: "Band template",
      title: "Public Profile — Band template",
      collectionsTouched: ["users", "portfolio_projects", "reviews"],
      notes: "profileType === \"BAND\" — renders bandName/lineupMembers/techRiderUrl/stagePlotUrl/shows.",
    },
    {
      key: "public_profile_view_business",
      order: 4,
      variantOf: 98,
      variantLabel: "Business template",
      title: "Public Profile — Business template",
      collectionsTouched: ["users", "portfolio_projects", "reviews"],
      notes: "profileType === \"BUSINESS\" — renders the Business extension (businessName/Type/services/equipment/teamMembers/gallery, plus Label/School/Agency sub-fields).",
    },
    {
      key: "public_profile_view_venue",
      order: 5,
      variantOf: 98,
      variantLabel: "Venue template",
      title: "Public Profile — Venue template",
      collectionsTouched: ["users", "portfolio_projects", "reviews"],
      notes: "profileType === \"VENUE\" — renders the Venue extension fields.",
    },
    // --- 2026-08-16: new screens proposed by the Person Profile spec
    // (docs/04-Wireframes/PERSON-PROFILE-SPEC.md §2/§4) — Talent/Band scope
    // only, not built yet. Each depends on a schema addition landed the
    // same session (admin/types/firestore.ts) — see each entry's notes.
    {
      key: "media_gallery",
      order: 6,
      screenNumber: 121,
      title: "Media Gallery (photo/video full-screen viewer)",
      states: ["default", "empty"],
      collectionsTouched: ["users"],
      notes:
        "Talent/Band only — reads UserDoc.galleryUrls (new field, proposed 2026-08-16, no confirmed app-side writer yet). Not built.",
    },
    {
      key: "profile_analytics",
      order: 7,
      screenNumber: 122,
      title: "Profile Analytics / Insights",
      states: ["default", "empty"],
      collectionsTouched: ["profile_analytics"],
      notes:
        "New collection (proposed 2026-08-16) — profileViews/portfolioViews/audioPlays/videoPlays/searchAppearances/shareCount/connectionRequestsReceived, doc ID = owner UID. Owner-only read. Not built.",
    },
    {
      key: "collaborators_graph",
      order: 8,
      screenNumber: 123,
      title: "Collaborators (cross-project graph)",
      states: ["default", "empty"],
      collectionsTouched: ["users", "portfolio_projects"],
      notes:
        "Reads UserDoc.collaboratorUids (new denormalized field, proposed 2026-08-16, no confirmed app-side writer yet) to avoid fanning out across every portfolio_projects doc on each profile view. Not built.",
    },
    {
      key: "talent_services_view",
      order: 9,
      screenNumber: 124,
      title: "Services (public view, Talent/Band)",
      states: ["default", "empty"],
      collectionsTouched: ["users"],
      notes:
        "BLOCKED on an unanswered product decision (see PERSON-PROFILE-SPEC.md §5, open question 2): whether UserDoc.serviceOfferings moves out of the Business-extension block or gets a separate Talent-scoped field. Screen slot registered; UI not started.",
    },
  ],

  // --- 2026-08-12: grounded Explore-agent audit pass, per Part W's pending tracker.
  // opportunities and equipment_rental deliberately get NO entry here —
  // confirmed to have zero dedicated screens (see their updated default-flows.ts
  // descriptions for the grounded evidence), so seeding placeholder rows here
  // would misrepresent absence as presence.
  marketplace: [
    {
      key: "marketplace_nearby",
      order: 90,
      screenNumber: 188,
      title: "Nearby listings",
      states: ["default", "empty", "permission_denied"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. From the approved plan's Market +4. Distance-sorted listings around the city on your profile. Location permission is a state, and the copy must not imply the app can override the OS.",
    },
    {
      key: "marketplace_availability",
      order: 91,
      screenNumber: 189,
      title: "Rental availability",
      states: ["default", "unavailable"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. Rental availability for one item, reusing .ml-calendar — the same component booking uses, which is why it lives in the system rather than in either flow.",
    },
    {
      key: "marketplace_rental_summary",
      order: 92,
      screenNumber: 190,
      title: "Rental summary",
      states: ["default", "returned", "overdue"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. Dates, daily rate, deposit and what happens on return. Overdue is a state, not a separate screen.",
    },
    {
      key: "marketplace_seller_profile",
      order: 93,
      screenNumber: 191,
      title: "Seller",
      states: ["default", "business"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. A seller's marketplace-facing view: what they have listed, their rating, response time. Distinct from FLOW 14's public profile, which is the person; this is the trading identity.",
    },
    {
      key: "marketplace_browse",
      order: 1,
      screenNumber: 99,
      title: "Creator Marketplace (browse)",
      states: ["default", "empty", "sale", "rental", "mixing", "beats", "services", "search_filtered"],
      collectionsTouched: [],
      notes:
        "MarketplaceBetaScreen, wired live into the dashboard's Marketplace tab. Backed entirely by a hardcoded 8-item list — zero `marketplace_items` references anywhere in the file. \"Gear Rentals\" is just one of its 6 category tabs, not a separate flow (see equipment_rental note in default-flows.ts).",
    },
    {
      key: "marketplace_item_detail",
      order: 2,
      screenNumber: 100,
      title: "Listing Detail",
      states: ["default"],
      collectionsTouched: [],
      notes:
        "Rendered as an AlertDialog inside the browse screen, not a separate navigable screen. \"Contact Seller\" / \"Rent This Gear\" confirm buttons only set a local fake-success string (e.g. a fabricated request ID) — no network or DB call.",
    },
    {
      key: "marketplace_saved_bookmarks",
      order: 3,
      screenNumber: 101,
      title: "Saved & Favorites — Marketplace tab",
      states: ["grid", "list", "empty"],
      collectionsTouched: [],
      notes:
        "The screen MarketplaceFeature.kt actually routes to (a thin wrapper opening the Saved/Favorites hub defaulted to its Marketplace sub-tab) — not a browse/listing feature itself. Backed by a hardcoded item list, no Firestore.",
    },
    // --- 2026-08-16: new screens proposed by the Product Spec
    // (docs/04-Wireframes/PRODUCT-SPEC.md §B.2/§B.3) — Buy/Sell/Rent,
    // rent kept merged into Marketplace as a listing category (not a
    // separate flow), MVP scope confirmed. NOT audited from Kotlin source
    // like the three screens above — proposed and not built. See each
    // entry's notes for MVP vs. Phase 2.
    {
      key: "marketplace_search_filters",
      order: 4,
      screenNumber: 125,
      title: "Market Search / Category / Filters / Sort",
      states: ["default", "empty"],
      collectionsTouched: ["marketplace_items"],
      notes: "MVP. Not built — proposed screen, no Kotlin/webapp implementation exists yet.",
    },
    {
      key: "marketplace_make_offer",
      order: 5,
      screenNumber: 126,
      title: "Make Offer / Negotiate",
      states: ["default", "countered", "accepted", "declined", "expired"],
      collectionsTouched: ["offers"],
      notes: "Phase 2, not MVP. Needs the new offers collection (schema landed 2026-08-16). Not built.",
    },
    {
      key: "marketplace_order_status",
      order: 6,
      screenNumber: 127,
      title: "Transaction / Order Status (covers both Buy and Rent lifecycles)",
      states: ["pending", "paid", "active", "returned", "completed", "cancelled"],
      collectionsTouched: ["marketplace_orders"],
      notes:
        "Phase 2, not MVP. One screen for both Buy's Purchase/Transaction/Order Status and Rent's Summary/Confirm/Pickup-Delivery/Active/Return — no separate rental-booking screen sequence, per the rent-stays-merged decision. Needs the new marketplace_orders collection. Not built.",
    },
    {
      key: "marketplace_create_listing",
      order: 7,
      screenNumber: 128,
      title: "Create Listing (Sell + Rent Out, shared, listingType param)",
      states: ["default", "publishing", "error"],
      collectionsTouched: ["marketplace_items"],
      notes:
        "MVP, shipped minimal (decided 2026-08-16): title, category, condition, price, one photo, description, publish. Full 8-step brief polish (gallery, location picker, preview step) is Phase 2. Rent Out is listingType: \"rental\" on this same screen, not a separate flow. Not built.",
    },
    {
      key: "marketplace_my_listings",
      order: 8,
      screenNumber: 129,
      title: "My Listings / My Purchases / My Rentals",
      states: ["listings", "purchases", "rentals", "empty"],
      collectionsTouched: ["marketplace_items", "marketplace_orders"],
      notes: "Phase 2, not MVP. Not built.",
    },
  ],
  opportunities: [
    {
      key: "my_opportunities",
      order: 90,
      screenNumber: 195,
      title: "My opportunities",
      states: ["posted", "drafts", "closed", "empty"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31. The poster's side: what you have posted, drafted and closed. Tabs are states. Distinct from my_applications, which is the applicant's side.",
    },
    // --- 2026-08-16: Flow 5 was previously deliberately left unpopulated
    // (see this file's header) — confirmed zero real UI exists in the app
    // (webapp/flows/opportunities/_flow.json: "100% missing... commented-out
    // or unreachable dead code"). These are new screens PROPOSED by the
    // Product Spec (docs/04-Wireframes/PRODUCT-SPEC.md §D.1/§D.2), not
    // audited from Kotlin source — this is a real build from zero, not a
    // rewiring job like Discover. MVP scope confirmed 2026-08-16. Reuses
    // the existing `projects` collection per a prior reconciliation
    // decision (not a new gig_opportunities collection).
    {
      key: "opportunities_home",
      order: 1,
      screenNumber: 130,
      title: "Opportunities Home / Search / Categories / Results / Filters",
      states: ["default", "empty"],
      collectionsTouched: ["projects"],
      notes: "MVP. Reads projects where status == \"OPEN\". Not built.",
    },
    {
      key: "opportunity_detail",
      order: 2,
      screenNumber: 131,
      title: "Opportunity Detail",
      states: ["default"],
      collectionsTouched: ["projects"],
      notes: "MVP. Needs ProjectDoc.description (schema landed 2026-08-16). Not built.",
    },
    {
      key: "opportunity_apply",
      order: 3,
      screenNumber: 132,
      title: "Apply / Submit Portfolio / Application Preview / Submitted",
      states: ["default", "submitting", "submitted", "error"],
      collectionsTouched: ["opportunity_applications"],
      notes:
        "Phase 2, not MVP — MVP substitutes \"Message the poster\" (reuses chat_threads) for a formal application. Needs the new opportunity_applications collection. Not built.",
    },
    {
      key: "my_applications",
      order: 4,
      screenNumber: 133,
      title: "My Applications / Application Status",
      states: ["submitted", "viewed", "shortlisted", "accepted", "rejected", "empty"],
      collectionsTouched: ["opportunity_applications"],
      notes: "Phase 2, not MVP. Not built.",
    },
    {
      key: "create_opportunity",
      order: 5,
      screenNumber: 134,
      title: "Create Opportunity / Edit Opportunity",
      states: ["default", "publishing", "error"],
      collectionsTouched: ["projects"],
      notes:
        "MVP, shipped minimal (decided 2026-08-16): title, description, roleRequired, budget, location, publish — writes to projects using rules that already exist. Richer fields (skillsRequired/languagesRequired/locationType/deadline/opportunityType, status-widening beyond OPEN/IN_PROGRESS/COMPLETED) are Phase 2. Not built.",
    },
    {
      key: "manage_applicants",
      order: 6,
      screenNumber: 135,
      title: "Manage Applicants",
      states: ["default", "empty"],
      collectionsTouched: ["opportunity_applications"],
      notes: "Phase 2, not MVP. Poster-side view of opportunity_applications where opportunityId == this project. Not built.",
    },
  ],
  notifications: [
    {
      key: "notifications_center",
      order: 1,
      screenNumber: 102,
      title: "Notifications Center",
      states: [
        "loading", "empty", "populated", "search_filtered", "category_filtered", "muted_banner",
        // FLOW-13 brief, 2026-08-31. SCREEN 06-16 are eleven notification
        // TYPES — message, collaboration, opportunity, marketplace, booking,
        // payment, service, business, follow, portfolio, system. They are one
        // list filtered by category, so they are states. Priority is a state
        // too: "high" is the same list showing only what cannot wait.
        "high_priority", "grouped", "push_denied", "offline", "error",
      ],
      collectionsTouched: [],
      notes:
        "Reachable, feature-rich screen (swipe-to-dismiss, mark read/unread, pull-to-refresh) but 100% local mock state seeded with 5 hardcoded items — zero `.collection(\"notifications\")` calls, despite UI copy claiming \"Firestore Real-Time Sync\" (a commented-out banner, dead code). No tap-through/detail screen; no `targetUrl` navigation.",
    },
    {
      key: "notification_detail",
      order: 5,
      screenNumber: 169,
      title: "Notification",
      states: [
        "booking", "collaboration", "opportunity", "marketplace", "payment",
        "expired", "entity_deleted", "permission_denied",
      ],
      collectionsTouched: ["notifications"],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-13 brief. Most notifications go straight to the flow that owns them and need no detail screen. This one exists for the handful that carry a DECISION — a booking request, a collaboration invitation — where accepting or declining from the list would be too easy to do by accident. It shows the context and hands off; it deliberately does not reproduce the booking page.",
    },
    {
      key: "notification_categories",
      order: 6,
      screenNumber: 170,
      title: "Notification categories",
      states: ["default", "messages", "bookings", "marketplace", "opportunities", "system"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-13 brief's SCREEN 26. Per-category control (which events, and on which channel) as one screen whose state is the category being configured — the brief's own list of categories, not one screen each.",
    },
    {
      key: "notification_settings",
      order: 2,
      screenNumber: 103,
      title: "Notification Settings (in Notifications Center)",
      states: ["default"],
      collectionsTouched: [],
      notes:
        "Local-only preferences (push toggle, per-category toggles, quiet hours) — not persisted to Firestore, DataStore, or SharedPreferences. One of three separate, mutually-inconsistent \"notification preferences\" surfaces found (see the other two below) — not a single source of truth.",
    },
    {
      key: "notification_preferences_onboarding",
      order: 3,
      screenNumber: 104,
      title: "Notification Preferences (onboarding step)",
      states: ["default", "asking", "denied"],
      collectionsTouched: [],
      notes:
        "A separate, unconnected screen from notification_settings above, reached during profile onboarding. Its \"test notification\" action does exercise the real Android OS notification-channel API (device-level, not Firestore) — the only genuinely functional piece across all three preference surfaces.",
    },
    {
      key: "notification_feeds_settings",
      order: 4,
      screenNumber: 105,
      title: "Notification Feeds (general Settings tab)",
      states: ["default"],
      collectionsTouched: [],
      notes: "A third, separate preferences surface in the main Settings flow. \"Save\" shows a success Snackbar with no visible persistence call.",
    },
  ],
  verifications: [
    {
      key: "verification_screen",
      order: 1,
      screenNumber: 106,
      title: "Verify Professional Status",
      states: [
        "id_type_selector", "govt_id_upload", "gst_upload", "badge_timeline",
        // FLOW-16 brief, 2026-08-31. The three levels (basic / professional /
        // business) differ in what evidence they ask for, so they are states
        // of one submission rather than three screens.
        "level_basic", "level_professional", "level_business", "review", "consent",
      ],
      collectionsTouched: [],
      notes:
        "Reachable from the Profile tab. Upload boxes hardcode a literal filename into local state on tap (no file picker, no FirebaseStorage call) — the exact same fake filenames (Aadhaar_Front_SwarSandhya.pdf, GST_Verification_VedanshuMusic.pdf) referenced in this session's mock-data seeding. No write to verification_requests; no status-tracking (pending/approved/rejected) screen exists anywhere — verified status is three local booleans.",
    },
    {
      key: "verification_status",
      order: 2,
      screenNumber: 173,
      title: "Verification status",
      states: ["not_started", "pending", "verified", "rejected", "expired", "help"],
      collectionsTouched: ["verification_requests"],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-16 brief. The lifecycle lives here as five states of ONE screen, because a person checking their verification is doing the same thing whatever the answer is. The brief's rule that verification must not become a vanity badge shapes the copy: it says what was checked, not that the person is endorsed. Rejection gives a reason and a route back in without restarting. BETA.",
    },
  ],
  reviews: [
    {
      key: "reputation_dashboard",
      order: 1,
      screenNumber: 107,
      title: "Reputation Hub Dashboard",
      states: ["default"],
      collectionsTouched: [],
      notes:
        "ReputationSubScreen.DASHBOARD — entry point of the standalone Reputation Hub module (ReputationAndReviewsScreens.kt), reached from the main dashboard, not from any post-booking prompt. Enum state confirmed via Screens_Map.md's full-source audit; not independently re-verified for persistence behavior beyond what's already established for the module as a whole (see the write screens below — all local Room or no-op, zero real `reviews` writes).",
    },
    {
      key: "review_list",
      order: 2,
      screenNumber: 108,
      title: "Review List",
      states: ["default", "empty", "pending_moderation", "request", "reminder"],
      collectionsTouched: [],
      notes:
        "ReputationSubScreen.REVIEW_LIST. Reads whatever the module's local Room table holds (the same store write_review/etc. write to below) — not the real `reviews` Firestore collection the admin ReviewDoc schema targets.",
    },
    {
      key: "review_detail",
      order: 3,
      screenNumber: 109,
      title: "Review Detail",
      states: ["default", "edit", "delete", "reported"],
      collectionsTouched: [],
      notes: "ReputationSubScreen.REVIEW_DETAIL. Single-review expanded view within the same local-Room-backed module.",
    },
    {
      key: "write_review",
      order: 4,
      screenNumber: 110,
      title: "Write Review",
      states: ["default", "star_selected", "subcategory_ratings", "comment_entered", "submitted"],
      collectionsTouched: [],
      notes: "Part of a standalone Reputation Hub module, reached from the dashboard — not triggered by booking completion. Persists to a local Room table, not the real `reviews` Firestore collection.",
    },
    {
      key: "rating_breakdown",
      order: 5,
      screenNumber: 111,
      title: "Rating Breakdown",
      states: ["default"],
      collectionsTouched: [],
      notes: "ReputationSubScreen.RATING_BREAKDOWN — aggregate score/category breakdown display, same local-Room-backed data as review_list.",
    },
    {
      key: "completed_project_review",
      order: 6,
      screenNumber: 112,
      title: "Completed Project Review",
      states: ["default", "submitted"],
      collectionsTouched: [],
      notes: "Milestone-evaluation variant in the same Reputation Hub module. Fully no-op — submit only shows a snackbar and navigates back, no persistence at all.",
    },
    {
      key: "marketplace_review_screen",
      order: 7,
      screenNumber: 113,
      title: "Marketplace Gear Review",
      states: ["default", "submitted"],
      collectionsTouched: [],
      notes: "Post-purchase gear rating in the Reputation Hub module. Fully no-op UI, no persistence.",
    },
    {
      key: "studio_facility_review",
      order: 8,
      screenNumber: 114,
      title: "Studio Facility Review",
      states: ["default", "submitted"],
      collectionsTouched: [],
      notes: "Facility rating (acoustics/cleanliness), distinct from Business Profile's embedded Reviews & Gallery tab. Fully no-op UI, no persistence.",
    },
    {
      key: "review_business_response",
      order: 9,
      screenNumber: 115,
      title: "Business Response to Review",
      states: ["default", "posted"],
      collectionsTouched: [],
      notes: "Business owner replies to a review. Writes to the same local Room table as write_review, not Firestore.",
    },
    {
      key: "review_report",
      order: 10,
      screenNumber: 116,
      title: "Report Review",
      states: ["default", "submitted"],
      collectionsTouched: [],
      notes: "Dispute/report a review. Fully no-op UI, no persistence.",
    },
    {
      key: "public_reputation",
      order: 11,
      screenNumber: 117,
      title: "Public Reputation (cross-reference)",
      states: ["default"],
      collectionsTouched: ["reviews"],
      notes:
        "ReputationSubScreen.PUBLIC_REPUTATION — the read-facing summary shown on a public profile (aggregate rating, verified-credits count). Related to but distinct from Flow 14's public_profile_view cross-reference entry; listed here since it's driven by this module's own enum, not the profile template.",
    },
  ],

  // --- 2026-08-13: direct source read (SupportingScreens.kt) — corrects the
  // 2026-08-12 audit's search entry and adds qr_share/saved_favorites, both
  // previously entirely absent from this registry. See default-flows.ts.
  search: [
    {
      key: "universal_search",
      order: 1,
      screenNumber: 118,
      title: "Universal Search",
      states: [
        "home", "live_typing", "results", "empty",
        // FLOW-19 brief, 2026-08-31. The seven "RESULTS" sections are one
        // result list filtered by entity type, so they are states. Partial
        // results, typo recovery, permission and offline likewise: the query
        // has already run, only what comes back differs.
        "people", "businesses", "services", "marketplace_results",
        "opportunities_results", "portfolio_results", "groups",
        "partial", "typo", "permission", "offline", "error",
      ],
      collectionsTouched: [],
      notes:
        "UniversalSearchFlow (SupportingScreens.kt:281), SearchScreenState enum (HOME/LIVE/RESULTS/EMPTY). Reached from the dashboard's top-bar Search icon (SupportingModule.UNIVERSAL_SEARCH) — a genuinely separate screen from discover's/booking's own internal search-results states, not a duplicate. Filters a hardcoded 10-item mockAllResults list across Musicians/Bands/Projects/Studios categories; zero Firestore reads.",
    },
    {
      key: "search_filters",
      order: 2,
      screenNumber: 168,
      title: "Search filters",
      states: ["people", "businesses", "services", "marketplace_results", "opportunities_results", "portfolio_results", "active"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-19 brief. Filters change per entity type, and the brief says not to show irrelevant ones — so the filter set is a state keyed to the entity being filtered, in ONE sheet-shaped screen rather than six. Sort lives here too. Active filters appear as chips back on the results.",
    },
  ],
  qr_share: [
    {
      key: "qr_share_flow",
      order: 1,
      screenNumber: 119,
      title: "QR Code & Comp Card Sharing",
      states: [
        "home", "options", "preview",
        // FLOW-23 brief, 2026-08-31: the identity card is the same card for a
        // person and a business, and print formats are previews of it, so they
        // are states of this screen rather than five more screens.
        "business", "share_card", "print_card", "print_poster", "share_error",
      ],
      collectionsTouched: [],
      notes:
        "QrAndShareFlow (SupportingScreens.kt:655), reached from the dashboard's QRShare shortcut. State is a plain string, not an enum. \"Preview\" generates a \"Premium Comp Card\" with a simulated PDF download (fake progress via a delay(150) loop, no real file, no real QR payload). No schema exists for this anywhere — no shareable-link record, no download/share analytics. Does not call DynamicLinkManager.kt, despite that existing elsewhere in the app for outbound share links.",
    },
    {
      key: "qr_scanner",
      order: 2,
      screenNumber: 165,
      title: "Scan QR",
      states: [
        "scanning", "resolved_profile", "resolved_business", "resolved_service",
        "resolved_listing", "resolved_portfolio",
        "invalid", "expired", "private", "deleted", "deactivated",
        "no_camera", "camera_denied", "image_scan", "offline",
      ],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-23 brief. A screen, not a state, because it owns the camera and a route. Every scan outcome is a state of it: five resolved entity types and six failure modes. The failure copy must not reveal private information and must not imply the app can override an OS camera permission.",
    },
    {
      key: "digital_business_card",
      order: 3,
      screenNumber: 166,
      title: "Digital business card",
      states: ["default", "save_contact", "restricted"],
      collectionsTouched: ["users"],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-23 brief. Shows only the fields the owner has made public — the restricted state is what a card looks like when phone and email are private. Save Contact previews exactly what would be written and never includes a hidden field.",
    },
    {
      key: "qr_settings",
      order: 4,
      screenNumber: 167,
      title: "QR settings",
      states: ["default", "disabled", "analytics"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-23 brief. Enable/disable, destination, and scan analytics. The brief is explicit that changing a profile must NOT require reprinting a QR — the code resolves through the canonical route, so there is deliberately no regenerate action for ordinary edits.",
    },
  ],
  saved_favorites: [
    {
      key: "saved_favorites_hub",
      order: 1,
      screenNumber: 120,
      title: "Saved & Favorites Hub",
      states: [
        "artists", "bands", "studios", "marketplace", "empty", "grid_view", "list_view",
        // FLOW-24 spec, added 2026-08-31. Every "SCREEN NN — ITEM X" section in
        // that brief is a STATE of a saved row, not a screen: a saved item that
        // has been sold, expired, made private or deleted still occupies one
        // row in this list and only its resolution differs. Favourites is a
        // filter over the same list, not a second collection.
        "favorites", "collections", "bulk_select",
        "item_sold", "item_expired", "item_private", "item_unavailable",
      ],
      collectionsTouched: [],
      notes:
        "SavedAndFavoritesFlow (SupportingScreens.kt:1023), reached from the dashboard's Saved shortcut. SavedTab enum: ARTISTS, BANDS, STUDIOS, MARKETPLACE (+ EMPTY). The Marketplace tab is the same screen already documented as marketplace_saved_bookmarks under the marketplace flow (MarketplaceFeature.kt routes here, defaulted to that tab) — listed here too since it's one tab of this single real hub, not a separate implementation. All four tabs are local mutableStateListOf mock data (hardcoded artists/studios/marketplace items); zero Firestore reads or writes, no persisted \"saved\" relationship on any real document.",
    },
    {
      key: "collection_detail",
      order: 2,
      screenNumber: 156,
      title: "Collection",
      states: ["default", "empty", "shared", "private", "editing"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-24 brief. A collection needs its own route and deep link — it is shareable and can be opened directly — so it is a screen rather than a state of the hub. Rename/description/privacy/delete are a sheet and a dialog on this screen, not four further screens. Deleting a collection must not delete the saved items, and the copy says so.",
    },
    {
      key: "saved_searches",
      order: 3,
      screenNumber: 157,
      title: "Saved searches",
      states: ["default", "empty", "alerts_on", "editing"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-24 brief. A saved search is a query plus filters plus an alert toggle; running one hands off to FLOW 19 Search and alert delivery belongs to FLOW 13. This screen owns the list and the alert switch only.",
    },
    {
      key: "saved_item_detail",
      order: 4,
      screenNumber: 158,
      title: "Saved item",
      states: ["default", "unavailable"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from the FLOW-24 brief, which is explicit that Saved must never become a duplicate detail page. This shows the ORIGINAL entity preview plus the saved-specific facts (saved date, favourite state, which collection) and a single Open Original action into the canonical flow.",
    },
  ],

  // --- 2026-08-31: FLOW 18 and FLOW 20 had NO screens at all. Both are
  // PROPOSED from the deferred-flows brief and marked BETA in the UI: neither
  // has an Android implementation, and subscription has no schema either
  // (MASTER-REGISTRY records "not modelled at all — no subscription/plan/tier
  // collection exists anywhere"). Designing them does not change that; the
  // screens say Beta so nobody reads them as shipped.
  subscription: [
    {
      key: "plans",
      order: 1,
      screenNumber: 178,
      title: "Plans",
      states: ["default", "free", "pro", "business", "trial", "promotion", "feature_limit"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from FLOW 18. Plan comparison and each plan's detail are one screen: the plan being examined is the state. feature_limit is the same screen reached after hitting a limit, which is where the brief says to explain WHICH limit and what lifts it. No fabricated prices beyond the placeholder tiers already used on the marketing page. BETA.",
    },
    {
      key: "checkout",
      order: 2,
      screenNumber: 179,
      title: "Checkout",
      states: ["review", "processing", "success", "failure"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from FLOW 18. Plan, price, tax, billing period and terms before paying. Payment credentials are never shown or collected in this design layer — the method is named, never entered. processing blocks a second submission. BETA.",
    },
    {
      key: "manage_subscription",
      order: 3,
      screenNumber: 180,
      title: "Manage subscription",
      states: ["active", "upgrade", "downgrade", "cancel", "cancelled", "past_due"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from FLOW 18. Upgrade, downgrade and cancel are states of one screen, each showing what changes and when it takes effect. The brief forbids manipulative retention patterns, so cancelling is a plain path with no interstitial offer. BETA.",
    },
    {
      key: "invoices",
      order: 4,
      screenNumber: 181,
      title: "Invoices",
      states: ["default", "empty", "receipt"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from FLOW 18. Invoice list and a single receipt, which is a state rather than a screen. BETA.",
    },
  ],

  recommendation_engine: [
    {
      key: "for_you",
      order: 1,
      screenNumber: 182,
      title: "For you",
      states: [
        "default", "people", "businesses", "services", "opportunities",
        "marketplace", "collaborators", "nearby", "why", "dismissed", "empty",
      ],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from FLOW 20. The brief's six recommendation TYPES are one list filtered by what is being recommended, so they are states. Every card carries its reason in words — the brief demands transparency over mystery and forbids fabricated reasons, so a card with no honest reason is not shown. \"why\" expands the reason; \"dismissed\" is what Not Interested leaves behind. BETA.",
    },
    {
      key: "recommendation_preferences",
      order: 2,
      screenNumber: 183,
      title: "Recommendation settings",
      states: ["default", "off"],
      collectionsTouched: [],
      notes:
        "PROPOSED 2026-08-31 from FLOW 20. Which signals may be used, and an off switch. The brief: turning personalisation off must not imply core search stops working, and the copy says exactly that. Mirrors the Personalisation control in Settings rather than replacing it. BETA.",
    },
  ],
};
