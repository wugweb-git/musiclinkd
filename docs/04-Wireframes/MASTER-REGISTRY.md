# MusicLinkd — Master Registry

**Notion is the source of truth for this document, per your instruction on 2026-08-29.** This repo file is a mirror — edit Notion first, then re-sync here, not the other way around. (This is the opposite direction from `PRODUCT-SPEC.md`, which stays repo-primary — see that file's own §7 for why the two docs have different sync directions.)

This is the full inventory of what's registered in the app's admin backend — every flow, every screen, every schema type, every widget, every taxonomy category — pulled directly from the source files on 2026-08-29. It is **not** the same document as `PRODUCT-SPEC.md`. That file is a narrative product spec covering 4 of the app's 24 flows in depth (decisions, open questions, phasing). This file is the raw registry: all 24 flows, all 135 screens, all 34 schema types, all 27 widgets, all taxonomy categories — real and audited, proposed and unbuilt, side by side, clearly marked which is which.

**Correction on the way in:** an earlier audit this session stated there were "11 taxonomy categories." That was wrong — it came from a grep pattern that only matched `##`-level category headers and missed the `###`-level ones. The real count, verified by reading the full file, is **29 categories** (11 original + 18 added across later passes), plus one referenced-but-not-yet-seeded (`studio_addons`). Corrected here, flagged plainly rather than quietly fixed.

- `FLOW-SPECS.md` — the **verbatim product briefs** for Flows 1–24, the
  Home/Dashboard IA and the Person Profile, as handed over in chat. Recovered
  from session transcripts and committed on 2026-08-31: until then they existed
  only in `~/.claude/projects/` and were repeatedly lost to context compaction.
  That file is the brief; this file is what got built against it.

Sources, exactly as they exist in the repo on 2026-08-29:
- Flows: `admin/app/(admin)/flows/default-flows.ts`
- Screens: `admin/app/(admin)/flows/screens/default-screens.ts`
- Schema: `admin/types/firestore.ts`
- Widgets: `admin/docs/architecture/Widget_Directory.md`
- Taxonomy: `admin/docs/architecture/Master_Data_Seed_Values.md`

None of this is a proposal layer — where something is proposed/unbuilt rather than audited from the real Kotlin app, the source file says so explicitly and this doc repeats that flag rather than smoothing it over.

---

## 1. Flows (24)

`status` legend, from `default-flows.ts`'s own header: **documented** = real, working, audited. **partial** = some real code but broken/incomplete/inconsistent. **app-mocked** = UI exists, zero Firestore wiring. **missing** = no schema, no UI, or neither confirmed.

| # | Key | Title | Status | Description |
|---|---|---|---|---|
| 1 | `auth` | User Auth | partial | Email/password calls the real Firebase Auth SDK but fails server-side (fabricated API key). OTP is 100% fake. Google Sign-In's native flow reliably fails and falls back to a hardcoded fake profile. Most real registrations land under a `local_uid_${email.hashCode()}` fallback with no real Firebase Auth account behind it. |
| 2 | `onboarding` | User Onboarding | partial | 13-screen real NavHost-driven flow, writes to `users/{uid}` on completion. `instrumentProficiency`, `roleExperience`, `networkingLinks`, `profilePicturePath`, `coverPicturePath` are collected but silently dropped from the final write. |
| 3 | `portfolio` | Add Portfolio | app-mocked | 11-screen publish flow for finished creative work — distinct from Opportunities' `projects` collection (gig postings). Schema matches exactly. Zero confirmed app-side writer — still 100% in-memory with a hardcoded author name. |
| 4 | `discover` | Discover | app-mocked | 100% mock data — zero Firestore references, filters a hardcoded 12-profile list. |
| 5 | `opportunities` | Opportunities | missing | Maps to the existing `projects` collection. 100% missing, not just unconfirmed — zero `.collection("projects")` calls anywhere; the only browse/apply UI is commented-out or unreachable dead code whose own copy admits "Opportunities are coming soon." |
| 6 | `communication` | Communication & Collaboration | partial | `connections` has two inconsistent writers (different document shapes for the same pair). `chat_threads`/`messages` exist in rules but weren't independently re-audited. |
| 7 | `marketplace` | Marketplace — Buy/Sell/Rent | app-mocked | 100% mocked, not wired to Firestore. One real, dashboard-wired browse screen with 6 hardcoded category tabs including "Gear Rentals"; an in-dialog item detail (not a screen); a Saved/Favorites tab. No create-listing, cart, checkout, or seller-messaging screens exist. `equipment_rental` was merged in here 2026-08-13, not kept separate. |
| 8 | `booking` | Booking | partial | Currently studio-only. Real writer exists but uses wrong field names (`artistUserId`/`totalCostAmount`/`statusKey` instead of the schema's `userId`/`totalAmount`/`bookingStatus`) — confirmed still broken. |
| 9 | *(retired)* | *(equipment_rental — removed 2026-08-13)* | — | Merged into `marketplace`. Order 9 deliberately left unassigned to avoid renumbering stale cross-references elsewhere. |
| 10 | `profile_management` | Profile Management | partial | `profileType` + Business/Band/Venue/Talent extension fields are real schema. The app previously had three disconnected profileType-guessing UI implementations that should converge on the real field. Distinct from Flow 21 (app preferences). |
| 11 | `services_availability` | Services, Availability | partial | `sessionAvailability` (Talent), `StudioDoc.services`, `serviceOfferings` (Business) all modeled. No single unified "availability" concept spans all profile types. |
| 12 | `business_profiles` | Business Profiles (Studios, Labels) | partial | Business extension fields modeled, matching the app's mock "Octave Sound Labs" tabs. `businessType` covers only 4 of ~8 planned sub-types (Studio/Label/School/Dealer — missing Production House/Booking Agency/Artist Manager/Rental Company). |
| 13 | `notifications` | Notifications | app-mocked | `notifications/{id}` typed and rules-ready. A real, feature-rich reader screen exists but is 100% local mock state — zero Firestore calls despite UI copy falsely claiming "Firestore Real-Time Sync." Three separate, mutually-inconsistent preference surfaces exist, none persisted. |
| 14 | `public_profile` | Public Profile | partial | Depends on Profile Management's template rendering. The public-facing read path wasn't separately audited. |
| 15 | `band` | Band | partial | Band extension fields modeled (`bandName`/`lineupMembers`/`techRiderUrl`/`stagePlotUrl`). `BandShow[]` (shows, flat array not a subcollection) closed 2026-08-12. |
| 16 | `verifications` | Verifications | app-mocked | `verification_requests` and the admin page are real and working. The app screen is fully simulated — upload boxes hardcode fake filenames, never write to Firestore, no status-tracking screen exists. |
| 17 | `reviews` | Reviews | app-mocked | `reviews` collection and admin module are real and working. The app's Reputation Hub has 4 real write screens but none write to the real `reviews` collection — some persist to local Room, the rest are no-op UI. |
| 18 | `subscription` | Subscription | missing | Not modeled at all — no subscription/plan/tier collection exists anywhere. |
| 19 | `search` | Search | app-mocked | `UniversalSearchFlow` is a real, standalone, reachable screen (corrected 2026-08-13 from an earlier wrong "duplicate" classification). 4-state, fully mocked — hardcoded 10-item list, zero Firestore reads. |
| 20 | `recommendation_engine` | Recommendation Engine | missing | Explicitly deprioritized by the user ("ignore"). `GeminiRecommendationEngine.kt` is real but neutered by a placeholder-API-key bug. |
| 21 | `user_settings` | Settings & Support | missing | App-level settings, Help & Support, legal pages, app version. Distinct from Flow 1 (Auth) and Flow 10 (Profile Management). Types are rules-ready but the app has zero real persistence anywhere for any of it. |
| 22 | `cms_pages` | CMS Pages | partial | `page_content` and the admin Pages module are real and working, but `frontend/website/` is 100% static HTML that never reads Firestore at runtime — edits here don't reach the live site. |
| 23 | `qr_share` | QR Code & Profile Sharing | missing | Added 2026-08-13. 3-state screen generating a "Premium Comp Card" with a simulated PDF download (fake progress bar, no real file). No schema modeled at all. |
| 24 | `saved_favorites` | Saved & Favorites | app-mocked | Added 2026-08-13. Real 4-tab hub (Artists/Bands/Studios/Marketplace). All four tabs are 100% local mock data — zero Firestore reads or writes, no persisted "saved" relationship on any real document. |

---

## 2. Screens (135)

Grouped by flow, in flow order. Screen numbers are globally unique and sequential across the whole app (1–135, no gaps, no duplicates — verified). Screens `121`–`135` are the one exception: proposed by `PRODUCT-SPEC.md`, not audited from Kotlin source like every other entry in this section — each says so in its own notes.

### Flow 1 — Auth (screens 1–7)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 1 | `splash` | Splash | loading, resuming_local_session | — | Session "persistence" is local Room state resumption, not a Firebase Auth session check. |
| 2 | `welcome` | Welcome (choose auth method) | default | — | Entry point: Email, Mobile/OTP, Google Sign-In. |
| 3 | `email_auth` | Email Auth (sign up / sign in) | signup, signin, loading, error | — | Real Firebase Auth SDK calls, but the fabricated API key means these fail server-side — the one honest failure mode in this flow. |
| 4 | `mobile_auth` | Mobile Number Entry | default, error | — | |
| 5 | `otp_verification` | OTP Verification | entering, verifying, error, success | — | 100% fake — any 6-digit string passes. No PhoneAuthProvider exists anywhere. |
| 6 | `google_signin` | Google Sign-In | account_picker, success, fallback_fake | — | Fabricated OAuth client ID means the real intent reliably fails; falls back to a hardcoded fake profile. |
| 7 | `registration_write` | Registration write (implicit) | success, fallback_uid | `users` | Most real signups land under `local_uid_${email.hashCode()}` with no real Firebase Auth account. |

### Flow 2 — Onboarding (screens 8–20)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 8 | `primary_role` | Primary Role Selection | default | `taxonomy_categories` | Reads `person_types` options. |
| 9 | `secondary_role` | Secondary Role (optional) | default, skipped | `taxonomy_categories` | |
| 10 | `genres` | Genres (pick up to 3) | default | `taxonomy_categories` | |
| 11 | `languages` | Languages | default | `taxonomy_categories` | |
| 12 | `instruments` | Instruments | default, skipped_for_non_musician | `taxonomy_categories` | |
| 13 | `instrument_proficiency` | Instrument Proficiency | default | — | Collected but confirmed dropped from the final write. |
| 14 | `location` | Location | default | — | Written at final submit; country has no Room column so resets on a resumed session. |
| 15 | `profile_creation` | Profile Creation | default | — | `profilePicturePath`/`coverPicturePath` collected but dropped from the final write. |
| 16 | `networking_links` | Networking Links | default | — | Collected but dropped from the final write and from Room. |
| 17 | `persona_badges` | Persona Badges | default | `taxonomy_categories` | |
| 18 | `review_confirm` | Review / Confirm Profile | default | — | |
| 19 | `submit_publish` | Submit / Publish Profile | submitting, success, error | `users` | Real write, confirmed — but missing `instrumentProficiency`/`roleExperience`/`networkingLinks`/`profilePicturePath`/`coverPicturePath`. |
| 20 | `dashboard_handoff` | Success → Dashboard Handoff | default | — | |

### Flow 3 — Add Portfolio (screens 21–31)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 21 | `portfolio_dashboard` | Portfolio Dashboard | empty, populated | `portfolio_projects` | |
| 22 | `choose_project_type` | Choose Project Type | default | `taxonomy_categories` | `project_types` options. |
| 23 | `project_information` | Project Information | default | — | |
| 24 | `streaming_links` | Add Streaming Links | default | — | |
| 25 | `select_role` | Select Your Role | default | `taxonomy_categories` | `project_roles` options. |
| 26 | `add_collaborators` | Add Collaborators | searching, selected | `users` | |
| 27 | `credit_attribution` | Credit Attribution | default | — | |
| 28 | `media_preview` | Project Media Preview | default | — | "Artwork" is a palette-theme keyword, not an uploaded image — no real upload UI exists. |
| 29 | `publish_project` | Publish Project | ready, publishing, error | `portfolio_projects` | Not confirmed real — still 100% in-memory. |
| 30 | `success` | Success State | default | — | |
| 31 | `profile_project_view` | Published Project — Profile View | default | `portfolio_projects` | |

### Flow 10 — Profile Management (screens 32–41)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 32 | `profile_overview` | Profile Overview / Dashboard | default | `users` | |
| 33 | `edit_identity` | Edit Profile | editing, saving, saved, error | `users` | Real, confirmed write (`SetOptions.merge()`). |
| 34 | `profile_type_selection` | Profile Type Selection | default | `users` | `profileType` — write-locked after creation. |
| 35 | `business_profile_editor` | Business Profile Editor | visible_if_business | `users` | |
| 36 | `band_profile_editor` | Band Profile Editor | visible_if_band | `users` | |
| 37 | `venue_profile_editor` | Venue Profile Editor | visible_if_venue | `users` | |
| 38 | `talent_profile_editor` | Talent Profile Editor | visible_if_talent | `users`, `role_profiles` | |
| 39 | `external_links_editor` | External Links Editor | default | `users` | |
| 40 | `public_profile_preview` | Public Profile Preview | talent/band/business/venue_template | `users`, `portfolio_projects`, `reviews` | Previously driven by 3 disconnected string-matching heuristics instead of the real field. |
| 41 | `profile_completion` | Profile Completion Indicator | incomplete, complete | `users` | Derived client-side, not stored. Was hidden/commented-out in source. |

### Flow 11 — Services, Availability (screens 42–46)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 42 | `availability_toggle` | Availability Toggle (Talent) | available_now, open_for_gigs, booked_soon, on_tour, unavailable | `users` | `sessionAvailability` + coarser `isAvailable` boolean. |
| 43 | `service_offerings_editor` | Service Offerings Editor (Business) | default | `users` | `serviceOfferings` (priced menu) vs. `services` (filter tags) — two different fields. |
| 44 | `studio_services_amenities` | Studio Services & Amenities | default | `studios`, `taxonomy_categories` | `studio_services` (offered) vs. `studio_amenities` (physical) — distinct categories. |
| 45 | `rate_pricing_editor` | Rate / Pricing Editor | default | `users`, `studios` | `hourlyRate` migrated to a real number 2026-08-12. |
| 46 | `session_configuration` | Session Configuration | default | `studio_bookings`, `taxonomy_categories` | Full `StudioBookingDoc` fields now editable via the Bookings admin editor sheet. |

### Flow 21 — Settings & Support (screens 47–59)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 47 | `account_settings` | Account Settings | default | `users` | Overlaps with Flow 10's Edit Profile in the real app. |
| 48 | `notification_settings` | Notification Settings | default | `user_settings` | No real persistence in the app yet. |
| 49 | `privacy_settings` | Privacy Settings | default | `user_settings` | Includes a "Download My Data" action, UI-only today. |
| 50 | `security_settings` | Security Settings | default, two_factor_setup | — | Password reset lives here in the real app today, conceptually belongs in Auth. |
| 51 | `appearance_settings` | Appearance Settings | default | `user_settings` | Theme + accent color — only `darkModeEnabled` is typed. |
| 52 | `help_center` | Help Center | default | — | Landing/menu screen. |
| 53 | `faq` | FAQ | default, searching | — | Static hardcoded 4-item list. |
| 54 | `raise_ticket` | Contact Support / Raise Ticket | default, submitting, success | `support_tickets` | Real collection exists; app currently fabricates a fake ticket ID instead of writing one. |
| 55 | `report_user` | Report User | default, submitting, success | `moderation_flags` | Real collection + admin queue exist; app just shows a snackbar and discards input. |
| 56 | `report_listing` | Report Listing | default, submitting, success | `moderation_flags` | Same gap as Report User. |
| 57 | `feedback` | Feedback | default, submitting, success | — | No Firestore write, no external link — input discarded. |
| 58 | `legal_pages` | Legal Pages | default | `page_content` | Lives under Flow 22's `page_content` collection. |
| 59 | `app_version` | App Version / About | default | — | Static build info. |

### Flow 8 — Booking (screens 60–70)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 60 | `studio_discovery` | Studio Discovery | default | `studios` | Featured/Nearby/Popular can't be real filters yet — no such flags on `StudioDoc`. |
| 61 | `filters` | Filters (bottom sheet) | default | `taxonomy_categories` | Distance needs `StudioDoc.geo`; price sort unblocked schema-side, app-side UI unconfirmed. |
| 62 | `search_results` | Search Results | default, empty | `studios` | |
| 63 | `studio_profile` | Studio Profile | default | `studios`, `reviews` | Engineers-as-bookable-staff deliberately deferred. |
| 64 | `equipment_screen` | Equipment | default | `studios` | `StudioDoc.equipment` not yet confirmed as app-written. |
| 65 | `availability_calendar` | Availability Calendar | monthly, weekly, timeline | `studio_bookings` | Slot status derives from querying existing bookings. |
| 66 | `configure_session` | Configure Session | default | `taxonomy_categories` | All fields typed, none confirmed app-wired. |
| 67 | `booking_summary` | Booking Summary | default | `studio_bookings` | `taxAmount`/`depositAmount`/`discountAmount`/`promoCode` typed, none confirmed app-wired. |
| 68 | `payment_screen` | Payment | default, processing, error, success | `studio_bookings` | |
| 69 | `booking_success` | Booking Success | default | `studio_bookings` | Real write confirmed BROKEN — wrong field names as of last verification. |
| 70 | `my_bookings` | My Bookings | upcoming, past, cancelled | `studio_bookings` | |

### Flow 12 — Business Profiles (screens 71–77)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 71 | `business_overview` | Overview | default | `users` | |
| 72 | `business_services_tab` | Services Tab | default | `users` | `serviceOfferings` (priced menu). |
| 73 | `business_equipment_tab` | Equipment Tab | default | `users`, `studios` | `equipmentList` or `StudioDoc.equipment` depending on `businessType`. |
| 74 | `business_team_tab` | Team & Works Tab | default | `users` | `teamMembers` — matches the app's mock "Octave Sound Labs" content exactly. |
| 75 | `business_reviews_gallery_tab` | Reviews & Gallery Tab | default | `reviews`, `users` | |
| 76 | `business_book_now` | Book Now (CTA) | default | — | Routes into Booking (Flow 8) — no schema of its own. |
| 77 | `business_owner_insights` | Owner Mode / Business Insights | visible_if_owner | — | 100% mocked, no real data source. |

### Flow 6 — Communication & Collaboration (screens 78–84)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 78 | `connection_requests_tab` | Connection Requests | default | `connections` | Two writers disagree on document shape. |
| 79 | `collaborators_tab` | Collaborators | default | `connections` | |
| 80 | `ai_matchmaker_tab` | AI Matchmaker Tab | unreachable | — | Exists in code but missing from the visible tab list — unreachable, not just hidden. |
| 81 | `chat_thread_list` | Chat Thread List | default, empty | `chat_threads` | |
| 82 | `chat_conversation` | Chat Conversation | default, sending, error | `chat_threads` | `messages` subcollection under `chat_threads/{id}`. |
| 83 | `networking_feed` | Networking Feed (post / view) | default, posting | `feed_posts` | Real read/write exists but write side uses `content` not the admin schema's `text`, plus a `mediaUrl` mismatch. |
| 84 | `networking_feed_artist_profile` | Artist Profile (from Networking Feed) | default | — | A 4th disconnected profile-view implementation on top of 3 already flagged elsewhere. |

### Flow 4 — Discover (screens 85–89)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 85 | `discover_feed` | Discover Feed / Home | default | — | 100% mocked — zero Firestore references, hardcoded 12-profile list. |
| 86 | `advanced_filters` | Advanced Filters (bottom sheet) | default | — | Every filter runs against mock data. |
| 87 | `discover_profile_detail` | Profile Detail View | default | — | Reads the same mock list, not `users`. |
| 88 | `saved_profiles` | Saved Profiles | empty, populated | `user_bookmarks` | Real, rules-ready collection — screen is 100% unwired to it. See §C.3 of `PRODUCT-SPEC.md` — likely superseded by `saved_favorites_hub` (120), not confirmed dead. |
| 89 | `trending_recommended_sections` | Trending / Recommended | hidden | — | Composables exist, call sites commented out — hidden, not deleted. |

### Flow 15 — Band (screens 90–92)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 90 | `band_profile_layout` | Band Profile (public view) | default | `users` | Renders off `profileType === "BAND"` now. |
| 91 | `ensemble_lineup` | Ensemble Lineup Display | default | `users` | `lineupMembers` — matches the app's existing carousel UI shape. |
| 92 | `upcoming_shows` | Upcoming Shows List | hardcoded | — | "Shows" wasn't modeled when this was audited — real UI showed a hardcoded list. (`BandShow[]` has since been added to schema, per Flow 15's description above — not yet re-confirmed as wired to this screen.) |

### Flow 22 — CMS Pages (screens 93–97)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 93 | `marketing_homepage` | Marketing Homepage | default | — | Static HTML, never reads Firestore at runtime. |
| 94 | `about_pricing_careers` | About / Pricing / Careers | default | — | Static HTML pages. |
| 95 | `legal_pages_site` | Legal Pages (site) | default | — | Static HTML today. |
| 96 | `blog_changelog` | Blog / Changelog | default | — | Generated from `CHANGELOG.md` at build time — not Firestore-backed. |
| 97 | `admin_pages_editor` | Admin Pages Editor | default | `page_content` | Real, working admin CRUD — but the live site never reads it. Core documented gap for this flow. |

### Flow 14 — Public Profile (screen 98, + variants 98-1..98-4, + proposed 121–124)

| # | Key | Title | Collections | Notes |
|---|---|---|---|---|
| 98 | `public_profile_view` | Public Profile (cross-reference) | `users`, `portfolio_projects`, `reviews` | Same screen as Flow 10's "Public Profile Preview" — this entry exists so Flow 14 isn't empty. Forks into 4 profileType-driven templates (genuine schema fork, not just a UI state). |
| 98-1 | `public_profile_view_talent` | Public Profile — Talent template | `users`, `portfolio_projects`, `reviews` | `profileType === "TALENT"` — base `UserDoc` fields, no extension block. |
| 98-2 | `public_profile_view_band` | Public Profile — Band template | `users`, `portfolio_projects`, `reviews` | `profileType === "BAND"` — `bandName`/`lineupMembers`/`techRiderUrl`/`stagePlotUrl`/`shows`. |
| 98-3 | `public_profile_view_business` | Public Profile — Business template | `users`, `portfolio_projects`, `reviews` | `profileType === "BUSINESS"` — full Business extension. |
| 98-4 | `public_profile_view_venue` | Public Profile — Venue template | `users`, `portfolio_projects`, `reviews` | `profileType === "VENUE"` — Venue extension fields. |
| 121 | `media_gallery` | Media Gallery (photo/video viewer) | `users` | **Proposed 2026-08-16**, per `PRODUCT-SPEC.md` Part A. Reads `UserDoc.galleryUrls`. Not built. |
| 122 | `profile_analytics` | Profile Analytics / Insights | `profile_analytics` | **Proposed.** New collection, owner-only read. Not built. |
| 123 | `collaborators_graph` | Collaborators (cross-project graph) | `users`, `portfolio_projects` | **Proposed.** Reads `UserDoc.collaboratorUids`. Not built. |
| 124 | `talent_services_view` | Services (public view, Talent/Band) | `users` | **Proposed, blocked** on an unresolved product decision — deferred to Flow 11 (Services), not this pass. Slot reserved, UI not started. |

### Flow 7 — Marketplace (screens 99–101, + proposed 125–129)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 99 | `marketplace_browse` | Creator Marketplace (browse) | default, empty, sale, rental, mixing, beats, services, search_filtered | — | Wired live into the dashboard's Marketplace tab. Backed entirely by a hardcoded 8-item list — zero `marketplace_items` references anywhere. |
| 100 | `marketplace_item_detail` | Listing Detail | default | — | Rendered as an `AlertDialog`, not a separate screen. Confirm buttons set a fake local success string — no network/DB call. |
| 101 | `marketplace_saved_bookmarks` | Saved & Favorites — Marketplace tab | grid, list, empty | — | A thin wrapper opening the Saved/Favorites hub (120), Marketplace sub-tab — not its own feature. |
| 125 | `marketplace_search_filters` | Market Search / Category / Filters / Sort | default, empty | `marketplace_items` | **Proposed, MVP.** Not built. |
| 126 | `marketplace_make_offer` | Make Offer / Negotiate | default, countered, accepted, declined, expired | `offers` | **Proposed, Phase 2.** Needs the `offers` collection. Not built. |
| 127 | `marketplace_order_status` | Transaction / Order Status | pending, paid, active, returned, completed, cancelled | `marketplace_orders` | **Proposed, Phase 2.** Covers both Buy and Rent lifecycles in one screen. Not built. |
| 128 | `marketplace_create_listing` | Create Listing (Sell + Rent Out) | default, publishing, error | `marketplace_items` | **Proposed, MVP, shipped minimal.** Title/category/condition/price/1 photo/description/publish. Not built. |
| 129 | `marketplace_my_listings` | My Listings / Purchases / Rentals | listings, purchases, rentals, empty | `marketplace_items`, `marketplace_orders` | **Proposed, Phase 2.** Not built. |

### Flow 5 — Opportunities (screens 130–135, all proposed)

Flow 5 had **zero** real screens before 2026-08-16 — confirmed 100% missing in the app. All 6 screens below are proposed by `PRODUCT-SPEC.md` Part D, not audited from Kotlin source.

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 130 | `opportunities_home` | Opportunities Home/Search/Categories/Results/Filters | default, empty | `projects` | MVP. Reads `projects` where `status == "OPEN"`. Not built. |
| 131 | `opportunity_detail` | Opportunity Detail | default | `projects` | MVP. Needs `ProjectDoc.description`. Not built. |
| 132 | `opportunity_apply` | Apply / Submit Portfolio / Submitted | default, submitting, submitted, error | `opportunity_applications` | Phase 2. MVP substitutes "Message the poster" instead. Not built. |
| 133 | `my_applications` | My Applications / Application Status | submitted, viewed, shortlisted, accepted, rejected, empty | `opportunity_applications` | Phase 2. Not built. |
| 134 | `create_opportunity` | Create Opportunity / Edit Opportunity | default, publishing, error | `projects` | MVP, shipped minimal. Not built. |
| 135 | `manage_applicants` | Manage Applicants | default, empty | `opportunity_applications` | Phase 2. Not built. |

### Flow 13 — Notifications (screens 102–105)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 102 | `notifications_center` | Notifications Center | loading, empty, populated, search_filtered, category_filtered, muted_banner | — | Feature-rich but 100% local mock state, zero Firestore calls — UI falsely claims "Firestore Real-Time Sync." |
| 103 | `notification_settings` | Notification Settings (in Notif. Center) | default | — | Local-only, not persisted anywhere. One of 3 inconsistent preference surfaces. |
| 104 | `notification_preferences_onboarding` | Notification Preferences (onboarding step) | default | — | Separate, unconnected screen. Its "test notification" is the only genuinely functional piece across all 3 preference surfaces (device-level OS API, not Firestore). |
| 105 | `notification_feeds_settings` | Notification Feeds (general Settings) | default | — | A 3rd separate preferences surface. "Save" shows a success toast with no visible persistence. |

### Flow 16 — Verifications (screen 106)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 106 | `verification_screen` | Verify Professional Status | id_type_selector, govt_id_upload, gst_upload, badge_timeline | — | Upload boxes hardcode a fake filename on tap — no file picker, no Storage call, no write to `verification_requests`. No status-tracking screen exists. |

### Flow 17 — Reviews (screens 107–117)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 107 | `reputation_dashboard` | Reputation Hub Dashboard | default | — | Entry point of a standalone module, reached from the dashboard, not a post-booking prompt. |
| 108 | `review_list` | Review List | default, empty | — | Reads the module's local Room table, not the real `reviews` collection. |
| 109 | `review_detail` | Review Detail | default | — | Same local-Room-backed module. |
| 110 | `write_review` | Write Review | default, star_selected, subcategory_ratings, comment_entered, submitted | — | Persists to local Room, not `reviews`. |
| 111 | `rating_breakdown` | Rating Breakdown | default | — | Same local-Room-backed data as Review List. |
| 112 | `completed_project_review` | Completed Project Review | default, submitted | — | Fully no-op — snackbar only, no persistence. |
| 113 | `marketplace_review_screen` | Marketplace Gear Review | default, submitted | — | Fully no-op UI. |
| 114 | `studio_facility_review` | Studio Facility Review | default, submitted | — | Distinct from Business Profile's embedded tab. Fully no-op. |
| 115 | `review_business_response` | Business Response to Review | default | — | Writes to the same local Room table, not Firestore. |
| 116 | `review_report` | Report Review | default | — | Fully no-op UI. |
| 117 | `public_reputation` | Public Reputation (cross-reference) | default | `reviews` | The one read-facing screen in this flow that touches the real collection. |

### Flow 19 — Search (screen 118)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 118 | `universal_search` | Universal Search | home, live_typing, results, empty | — | Reached from the dashboard's top-bar Search icon — genuinely separate from Discover's/Booking's internal search states. Hardcoded 10-item list, zero Firestore reads. |

### Flow 23 — QR Code & Profile Sharing (screen 119)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 119 | `qr_share_flow` | QR Code & Comp Card Sharing | home, options, preview | — | "Preview" generates a simulated PDF (fake progress bar, no real file, no real QR payload). No schema exists for this at all. |

### Flow 24 — Saved & Favorites (screen 120)

| # | Key | Title | States | Collections | Notes |
|---|---|---|---|---|---|
| 120 | `saved_favorites_hub` | Saved & Favorites Hub | artists, bands, studios, marketplace, empty, grid_view, list_view | — | The real hub, reached from the dashboard's Saved shortcut. Marketplace tab = screen 101. All 4 tabs are local mock data, zero Firestore reads/writes. Canonical over `saved_profiles` (88) per `PRODUCT-SPEC.md` §C.3. |

---

## 3. Schema — Firestore Document Types (34)

Every `export interface *Doc` in `admin/types/firestore.ts`, in file order. `?` marks optional fields. This is the actual master schema — narrower than this list are the 9 `RoleProfileDoc` variants and several non-`Doc` helper types (listed at the end of this section).

| Interface | Likely collection | Fields | Notes |
|---|---|---|---|
| `UserDoc` | `users` | `displayName`, `email`, `primaryRole`, `secondaryRole`, `supportingRoles?`, `isVerified`, `isBanned`, `isAvailable`, `username?`, `vanityUrl?`, `qrCodeUrl?`, `coverImageUrl?`, `hourlyRate`, `budgetTier?`, `location`, `bio`, `gender?`, `genres[]`, `languages[]`, `instruments[]`, `personaBadges[]`, `trustBadges[]`, `avatarUrl`, `createdAt`, `updatedAt?`, `profileType?`, `mobileNumber?`, `whatsappNumber?`, `fcmToken?`, `externalLinks?`, `instrumentProficiency?`, `roleExperience?`, `collaboratorUids?`, `geo?`, `galleryUrls?`, + Talent extension (`vocalRange?`, `sessionAvailability?`, `dawStack?`, `performanceHistory?`, `writingSamples?`), + Business extension (`businessName?`...`talentRoster?`, ~13 fields), + Band extension (`bandName?`...`shows?`, 5 fields), + Venue extension (`venueName?`...`eventCalendar?`, 4 fields) | The single largest document type — one flat interface with 4 role-gated extension blocks, not a discriminated union. |
| `AdminRoleDoc` | `admin_roles` (by uid) | `role`, `grantedBy`, `grantedAt` | |
| `ConnectionDoc` | `connections` | `senderUid`, `targetUserId`, `status` | Two inconsistent app-side writers confirmed (Flow 6). |
| `BlockDoc` | `blocks` | `blockerUid`, `blockedUid`, `createdAt` | Added 2026-08-16. Doc ID `{blockerUid}_{blockedUid}`. Rules live, no data yet. |
| `FeedPostDoc` | `feed_posts` | `authorUid`, `postType`, `audioUrl?`, `text?`, `likesCount`, `timestamp`, `authorName?`, `authorRole?`, `authorAvatarUrl?`, `mediaUrls?`, `commentsCount?` | App writes `content` not `text` — confirmed field-name mismatch. |
| `StudioBookingDoc` | `studio_bookings` | `studioId`, `userId`, `date`, `slotText`, `totalAmount`, `bookedHourSlots?`, `bookingStatus?`, `paymentStatus?`, `paymentTransactionId?`, `createdAt`, `sessionType?`, `durationTier?`, `engineerRequested?`, `selectedEquipment?`, `addons?`, `notes?`, `taxAmount?`, `depositAmount?`, `discountAmount?`, `promoCode?`, `paymentMethod?` | App writer confirmed broken (wrong field names). |
| `ChatThreadDoc` | `chat_threads` | `memberUids[]` | |
| `ChatMessageDoc` | `chat_threads/{id}/messages` | `senderUid`, `text`, `sentAt` | |
| `StudioDoc` | `studios` | `ownerUid`, `name`, `location`, `rating`, `reviewCount`, `hourlyRate`, `verifiedGold`, `amenities[]`, `consoleType`, `imageUrl`, `createdAt?`, `description?`, `phone?`, `images?`, `services?`, `geo?`, `equipment?` (`StudioEquipmentItem[]`), `rooms?` (`StudioRoom[]`), `operatingHours?`, `cancellationPolicy?` | |
| `ProjectDoc` | `projects` | `creatorUid`, `title`, `roleRequired`, `budget`, `status`, `genres[]`, `location?`, `applicantCount?`, `collaborationType?`, `createdAt`, `description?`, `skillsRequired?`, `languagesRequired?`, `locationType?`, `deadline?`, `opportunityType?`, `posterName?`, `posterAvatarUrl?` | Also backs Flow 5 Opportunities — deliberate reuse, not a new collection. |
| `OpportunityApplicationDoc` | `opportunity_applications` | `opportunityId`, `applicantUid`, `attachedPortfolioProjectIds?`, `message?`, `status`, `createdAt`, `updatedAt` | Added 2026-08-16. Rules live, no data yet. |
| `MarketplaceItemDoc` | `marketplace_items` | `sellerUid`, `title`, `price`, `category`, `condition`, `imageUrl`, `itemCategory?`, `quantity?`, `blockedDates?`, `createdAt` | Zero real documents — app is 100% hardcoded. |
| `OfferDoc` | `offers` | `listingId`, `buyerUid`, `sellerUid`, `amount`, `message?`, `status`, `counterAmount?`, `createdAt`, `updatedAt` | Added 2026-08-16. Rules live, no data yet. |
| `MarketplaceOrderDoc` | `marketplace_orders` | `listingId`, `buyerUid`, `sellerUid`, `amount`, `status`, `paymentMethod?`, `paymentStatus?`, `paymentTransactionId?`, `startDate?`, `endDate?`, `deliveryMethod?`, `createdAt` | Added 2026-08-16. Rules live, no data yet. |
| `ReviewDoc` | `reviews` | `targetType`, `targetId`, `authorUid`, `rating`, `text`, `bookingId?`, `orderId?`, `createdAt` | `targetType` widened to include `MARKETPLACE_ITEM` 2026-08-16. |
| `VerificationRequestDoc` | `verification_requests` | `subjectType`, `subjectId`, `submittedBy`, `status`, `documentPaths[]`, `verificationIdType?`, `submittedAt`, `reviewedBy?`, `reviewedAt?`, `notes?` | |
| `ModerationFlagDoc` | `moderation_flags` | `targetType`, `targetId`, `reportedBy`, `reason`, `status`, `createdAt`, `reviewedBy?`, `reviewedAt?` | |
| `SupportTicketDoc` | `support_tickets` | `requesterUid`, `requesterEmail`, `subject`, `message`, `status`, `assignedTo?`, `createdAt`, `updatedAt` | |
| `BroadcastMessageDoc` | `broadcast_messages` | `title`, `body`, `audience`, `sentBy`, `sentAt`, `status` | |
| `AuditLogDoc` | `audit_logs` | `actorUid`, `action`, `targetPath`, `timestamp`, `metadata?` | |
| `PageContentDoc` | `page_content` | `route`, `title`, `status`, `content`, `seoDescription`, `updatedBy`, `updatedAt` | Real admin CRUD, live site never reads it. |
| `TaxonomyCategoryDoc` | `taxonomy_categories` | `key`, `label`, `description`, `isActive`, `createdAt`, `updatedAt`, `updatedBy` | |
| `TaxonomyOptionDoc` | `taxonomy_categories/{id}/options` | `label`, `value`, `sortOrder`, `isActive`, `metadata?`, `createdAt`, `updatedAt` | `metadata` is the escape hatch used for `opportunity_types.group` etc. |
| `FlowDoc` | `flows` | `key`, `order`, `title`, `description`, `status`, `createdAt`, `updatedAt` | Backs §1 of this document. |
| `FlowScreenDoc` | `flows/{id}/screens` | `key`, `order`, `screenNumber?`, `variantOf?`, `variantLabel?`, `title`, `states?`, `collectionsTouched?`, `notes?`, `createdAt`, `updatedAt` | Backs §2 of this document. |
| `CapabilityMatrixDoc` | `capabilities` | `roleKey`, `label`, `canCreateProfile`, `canPostOpportunities`, `canSellEquipment`, `canCreateEvents`, `crmAccess`, `updatedBy`, `updatedAt`, `createdAt` | |
| `ModuleRegistryDoc` | `module_registry` | `label`, `description`, `enabled`, `navModuleKey`, `remoteConfigKey`, `updatedBy`, `updatedAt`, `createdAt` | |
| `BookmarkDoc` | `user_bookmarks` | `ownerUid`, `savedUserId`, `createdAt` | Corresponds to `saved_profiles` (88), currently 100% unwired app-side. |
| `ProfileAnalyticsDoc` | `profile_analytics` (by uid) | `profileViews`, `portfolioViews`, `audioPlays`, `videoPlays`, `searchAppearances`, `shareCount`, `connectionRequestsReceived`, `updatedAt` | Added 2026-08-16. Rules live, no data yet. |
| `PortfolioProjectDoc` | `portfolio_projects` | `ownerUid`, `name`, `description`, `type`, `releaseDate`, `status`, `visibility`, `spotifyUrl?`...`instagramReelUrl?` (6 platform links), `ownerRoles[]`, `collaborators[]` (`ProjectCollaborator[]`), `credits[]` (`CreditItem[]`), `mediaUrls?`, `artworkTheme`, `projectViews`, `likes`, `shares`, `createdAt`, `lastUpdatedAt` | Rules deployed, no confirmed app-side writer. |
| `NotificationDoc` | `notifications` | `userId`, `title`, `body`, `type`, `isRead`, `targetUrl?`, `createdAt` | `type` widened 2026-08-16 (+`OFFER_RECEIVED`/`ORDER_STATUS_UPDATE`/`APPLICATION_STATUS_UPDATE`). No confirmed app-side reader/writer. |
| `UserSettingsDoc` | `user_settings` (by uid) | `pushNotificationsEnabled`, `emailNotificationsEnabled`, `profileVisibility`, `darkModeEnabled`, `currency`, `language`, `updatedAt` | `SettingsFlow` confirmed to have zero real persistence app-side. |
| `BrandAssetsDoc` | `brand_assets` | `ownerUid`, `logoUrl?`, `iconUrl?`, `thumbnailUrl?`, `heroBannerUrl?`, `darkVariantUrl?`, `lightVariantUrl?`, `updatedAt` | No confirmed app-side writer. |
| `WidgetDoc` | `widgets` | `key`, `order`, `name`, `category`, `purpose`, `contentComposition`, `codeMapping`, `isVisible?`, `notes?`, `relatedScreens?`, `createdAt`, `updatedAt` | Backs §4 of this document. |

**Not counted in the 34 above, but part of the same schema file:**
- `RoleProfileDoc` — a **union type** (not an interface) over 9 role-specific interfaces, all backing the same `role_profiles/{ownerUid_roleKey}` collection (one doc per role a multi-role person plays): `SingerRoleProfile`, `MusicianRoleProfile`, `LyricistRoleProfile`, `MusicDirectorRoleProfile`, `ComposerRoleProfile`, `SoundEngineerRoleProfile`, `DjRoleProfile`, `RjRoleProfile`, `PodcasterRoleProfile`. Each has `ownerUid`/`createdAt`/`updatedAt` plus 4–6 role-specific fields (vocal range, DAW stack, specializations, etc. — see `firestore.ts` for exact fields per role). No confirmed app-side writer for any of them.
- Helper types (not their own collections, embedded on the interfaces above): `BandLineupMember`, `BandShow`, `BusinessTeamMember`, `BusinessServiceOffering`, `ExternalLinks`, `StudioEquipmentItem`, `StudioRoom`, `ProjectCollaborator`, `CreditItem`, `WithId<T>`.

---

## 4. Widgets (27)

From `admin/docs/architecture/Widget_Directory.md` — pasted verbatim by the user 2026-08-11, transcribed verbatim into `default-widgets.ts`. Every `Code Implementation Mapping` below points at a real, named Kotlin class — this is an audited catalog, not a proposal list. **Many of these are currently hidden from the app's UI on purpose** — hidden/visible state isn't tracked per-widget below (the source doc flags this as a known gap, not resolved).

### 1. Discovery & Collaboration

| Widget | Purpose | Composition | Code Mapping |
|---|---|---|---|
| ArtistCard | Talent discovery and artist showcase | Avatar, name, role/instrument, location, rating, verification, connect CTA | `UnifiedArtistCard` / `ArtistCard` |
| RecommendedCollaboratorCard | AI-powered collaborator matching card | Cover/avatar, match %, synergy reasons, skills, collaboration actions | `RecommendedCollaboratorCard` |
| DiscoverySearchResultCard | Global discovery and search result item | Profile/entity identity, category, genres, location, match data, preview action | `DiscoverySearchResultCard` |
| ProfilePreviewSheet | Quick profile inspection modal sheet | Profile summary, bio, top tracks, gear/skills, full-profile CTA | `ProfilePreviewSheet` |
| NetworkingFeed | Social/network activity feed list widget | User header, text, media/audio attachment preview, likes, comments, sharing | `NetworkingFeed` |
| BandMembersWidget | Band & performing crew roster display | Member avatars, instrument role tags, lead-member designation | `BandMembersWidget` |
| CollaboratorMatchWidget | Collection of AI-recommended collaborators | Horizontal/vertical cards, match scores, synergy info, actions | `RecommendedCollaboratorsSection` |

### 2. Studios & Venues

| Widget | Purpose | Composition | Code Mapping |
|---|---|---|---|
| StudioCard | Standard studio discovery and booking card | Studio imagery, room name, location, Dolby Atmos badge, rating, hourly rate | `UnifiedStudioCard` |
| StudioVenueListItem | Compact studio/venue directory list item | Thumbnail, venue name, location, rating, pricing, Atmos badge | `StudioVenueListItem` |
| FeaturedStudioCard | Hero studio promotion banner card | Large photography, studio/room identity, Atmos spec, pricing, booking CTA | `FeaturedStudioCard` |
| StudioSpecWidget | Detailed studio technical specs widget | Outboard gear, console models (SSL 9000J), monitors, mic inventory | `StudioSpecWidget` |

### 3. Marketplace & Opportunities

| Widget | Purpose | Composition | Code Mapping |
|---|---|---|---|
| MarketplaceCard | Equipment rental and service discovery card | Product/gear photo, name, rental rate, seller rating, availability tag | `UnifiedMarketplaceCard` |
| OpportunityCard | Gigs, jobs, auditions & collaboration card | Project title, creator/role, budget tag, required skills, applicant count, deadline | `UnifiedOpportunityCard` |

### 4. Portfolio & Projects

| Widget | Purpose | Composition | Code Mapping |
|---|---|---|---|
| ProjectCard | Public portfolio & release presentation card | Cover artwork, title, release date, status badge, collaborators & credits | `UnifiedProjectCard` |
| RecentProjectCard | Compact dashboard portfolio card | Artwork, track/project name, role, release year, streaming links, edit action | `RecentProjectCard` |

### 5. Media

| Widget | Purpose | Composition | Code Mapping |
|---|---|---|---|
| TrackCard | Music & track release discovery item | Artwork thumbnail, title, duration, platform badges, play count | `UnifiedTrackCard` |
| AudioPlayerWidget | Dedicated embedded audio playback widget | Waveform visualizer, play/pause, timeline slider, duration counter | `WaveformAudioPlayer` |
| PersistentAudioPlayer | Global sticky audio playback bar | Mini artwork, track title, play/pause, progress bar, close trigger | `GlobalPersistentAudioPlayerBar` — **verify this file still exists**, it showed as deleted (`D`) in `git status` at the start of an earlier session |
| AudioPodcastDemoWidget | In-line audio demo player in dashboards | Audio identity, waveform controls, duration display, playback state | `AudioPodcastDemoWidget` |

### 6. Bookings & Hiring

| Widget | Purpose | Composition | Code Mapping |
|---|---|---|---|
| BookingCard | Studio session booking record card | Studio name, date/time slot, room type, total price, status tag | `UnifiedBookingCard` |
| HireContactModal | Direct hiring & booking inquiry modal | Project scope options, proposed date picker, budget field, submit CTA | `HireContactModal` |

### 7. Reputation & Activity

| Widget | Purpose | Composition | Code Mapping |
|---|---|---|---|
| PublicReputationWidget | Public trust and endorsement presentation | Aggregated rating, verified credits count, client reviews, response rate | `PublicReputationWidget` |
| ReviewCard | Individual client review/endorsement card | Reviewer identity, organization, star row, quote, timestamp | `UnifiedReviewCard` |
| NotificationCard | Notification and activity item card | Actor avatar, event description, event type icon, timestamp | `UnifiedNotificationCard` |

### 8. Search, Filters & Sharing

| Widget | Purpose | Composition | Code Mapping |
|---|---|---|---|
| AdvancedFiltersBottomSheet | Advanced discovery filtering bottom sheet | Distance/budget sliders, category toggles, Dolby Atmos switch, apply CTA | `AdvancedFiltersBottomSheet` — confirmed filtering 100% mock data (Discover audit) |
| WhatsAppShareSheet | External profile & project sharing sheet | Share preview card, pre-populated message text, WhatsApp share trigger | `WhatsAppShareSheet` — **flagged possibly fake**, likely the same "Options" screen buttons found to be UI-only (`showSnackbar` only, no real WhatsApp intent) in the QR/Share audit |

### 9. Home & Featured Content

| Widget | Purpose | Composition | Code Mapping |
|---|---|---|---|
| HomeFeatureCard | Hero featured content on main feed | Featured artist, studio, track, playlist or promotion with imagery and CTA | `HomeFeatureCard` |

**5 proposed widgets exist outside this real registry** (`MediaUploadGrid`, `OfferThreadCard`, `TransactionStatusCard`, `ConversationContextHeader`, `ApplicationStatusCard`) — deliberately kept out of `Widget_Directory.md`/`default-widgets.ts` since those files are verbatim-audited-from-Kotlin and these aren't. Full detail in `PRODUCT-SPEC.md` Part E.2.

---

## 5. Master Data / Taxonomy (29 categories + 1 pending)

From `admin/docs/architecture/Master_Data_Seed_Values.md`. `value` is what's actually stored on the document field named in "Feeds"; `label` is the admin/user-facing text.

| Category | Options | Feeds | Status |
|---|---|---|---|
| `genres` | 17 (Bollywood/Filmi, Indie Pop, Hindustani Classical, Carnatic Classical, Punjabi Folk/Bhangra, Rajasthani Folk, Sufi Devotional, Ghazal, Qawwali, Rock/Metal, Hip Hop/Trap, Lofi/Chill Beats, EDM/Synthwave, Acoustic/Unplugged, R&B/Soul, Jazz/Blues, Reggae/Dub) | `UserDoc.genres` | Already seeded, matches app's real hardcoded list |
| `person_types` | 18 (Singer/Vocalist, Rapper/MC, Folk Vocalist, Guitarist, Keyboardist/Pianist, Drummer/Percussionist, Sitarist/Flutist, Composer, Lyricist, Vocal Arranger, Music Producer, Sound Engineer, Sound Designer, Artist Manager, Music Label, Music Publisher, Recording Studio, Event Organizer) | `UserDoc.primaryRole`/`secondaryRole` | Seeded, `value` = app's real `RoleItem` key slugs |
| `languages` | 19 (English, Hindi, Punjabi, Tamil, Telugu, Marathi, Gujarati, Kannada, Malayalam, Bengali, Assamese, Odia, Urdu, Sanskrit, Spanish, French, German, Japanese, Korean) | `UserDoc.languages` | Seeded, matches app |
| `instruments` | 21 (Acoustic Guitar, Electric Guitar, Bass Guitar, Keyboard/Synth, Piano, Drums/Percussion, Tabla, Sitar, Flute/Bansuri, Harmonium, Dholak, Violin, Saxophone, Trumpet, Sarod, Shehnai, Mridangam, Santoor, Veena, Tanpura, Cello) | `UserDoc.instruments`, doubles as a Marketplace dealer's "what I sell" | Seeded |
| `persona_badges` | 7 (Available for Collab, Session Ready, Touring, Home Studio Owner, Full-Time, Weekend Warrior, Open to Remote Work) | `UserDoc.personaBadges` | **Proposed starter set, not grounded in app data** — nothing in the app defines this list today |
| `project_types` | 10 (Song, Album, EP, Podcast, DJ Mix, Film Score, Commercial Jingle, Live Performance, Music Video, Custom Project) | `PortfolioProjectDoc.type` | Seeded, matches app |
| `project_roles` | 11 (Singer, Composer, Music Director, Producer, Lyricist, Session Musician, DJ, Mix Engineer, Mastering Engineer, Sound Designer, Band Member) | `PortfolioProjectDoc.ownerRoles` | Seeded, matches app — deliberately separate from `person_types` (per-project vs. platform-wide role) |
| `booking_statuses` | 5 (Requested, Confirmed, Payment Pending, Cancelled, Completed) | `StudioBookingDoc.bookingStatus` | Seeded |
| `studio_amenities` | 16 (Dolby Atmos, Vocal Booth, Live Room, Valet Parking, Resident Engineers, Private Lounge, 24x7 Access, Rehearsal Room, Chill Lounge, Drums Included, Parking Space, Instruments Support, Acoustics Optimized, Fiber Internet, Stage Monitors, Bar & Diner Access) | `StudioDoc.amenities` | Seeded, matches app's mock studio data |
| `collaboration_types` | 4 (Work for Hire, Royalties Split, Co-Write, Session Payment) | `ProjectDoc.collaborationType` | Seeded |
| `trust_badges` | 4 (Verified Artist, Top Host, Rising Talent, Studio Partner) | `UserDoc.trustBadges` | **Admin-granted only** — rules block a user from setting this on their own doc |
| `vocal_range` | 6 (Soprano, Mezzo-Soprano, Alto, Tenor, Baritone, Bass) | `UserDoc.vocalRange`, `SingerRoleProfile.vocalRange` | Proposed, industry-standard, not matched against an app list |
| `daw_tools` | 7 (Logic Pro, Pro Tools, Ableton Live, FL Studio, Cubase, Studio One, Reaper) | `UserDoc.dawStack`, `SoundEngineerRoleProfile.dawStack` | Proposed |
| `session_types` | 5 (Recording, Mixing, Mastering, Podcast, Jam Session) | `StudioBookingDoc.sessionType` | Proposed, matches the booking flow's real chip labels |
| `session_duration_tiers` | 3 (Hourly, Half Day, Full Day) | `StudioBookingDoc.durationTier` | Proposed |
| `studio_services` | 3 (Recording, Mixing, Mastering) | `StudioDoc.services` | Proposed — distinct from `studio_amenities` (offers vs. has) |
| `singing_styles` | 9 (Playback, Classical, Ghazal, Sufi, Indie, Pop, Rock, Rap, Bhajan) | `SingerRoleProfile.singingStyles` | Proposed |
| `lyricist_specializations` | 6 (Romantic, Commercial, Devotional, Rap, Film Songs, Independent Music) | `LyricistRoleProfile.specializations` | Proposed, confirmed against user-resupplied list |
| `director_specializations` | 4 (Film Music, OTT, Advertising, Independent) | `MusicDirectorRoleProfile.specializations` | Proposed, confirmed |
| `composition_styles` | 5 (Orchestral, Electronic, Background Score, Commercial Jingles, Film Score) | `ComposerRoleProfile.compositionStyles` | Proposed, confirmed |
| `engineer_specialties` | 4 (Recording, Mixing, Mastering, Live Sound) | `SoundEngineerRoleProfile.specialty` | Proposed, confirmed. Deliberate value-overlap with `opportunity_types` (see below) |
| `dj_types` | 5 (Club, Wedding, Radio, Festival, Bollywood) | `DjRoleProfile.djType` | Proposed, not confirmed |
| `verification_id_type` | 4 (Government ID/Aadhaar, Passport, Driving License, Company PAN/GSTIN) | `VerificationRequestDoc.verificationIdType` | Proposed |
| `availability_status` | 5 (Available Now, Open for Gigs, Booked Soon, On Tour, Unavailable) | `UserDoc.sessionAvailability` | Seeded — canonical over an earlier, rougher 4-value list, which was never a second real source |
| `podcast_categories` | 6 (Music, Interview, Comedy, Culture, Business, Education) | `PodcasterRoleProfile.categories` | Proposed, not confirmed |
| `marketplace_categories` | 6 (Gear Sale, Gear Rental, Beats Licensing, Stems, Mixing & Mastering, Creative Services) | `MarketplaceItemDoc.category` | Seeded — replaced an earlier fixed union that never matched the app's real categories |
| `equipment_categories` | 7 (Microphones, Audio Interfaces, Studio Monitors, Mixers, Preamps, Headphones, Controllers) | `StudioEquipmentItem.category`, `MarketplaceItemDoc.itemCategory` | Seeded |
| `budget_type` | 3 (₹ Budget, ₹₹ Mid-range, ₹₹₹ Premium) | `UserDoc.budgetTier` | Seeded |
| `opportunity_types` | 23, grouped via `metadata.group` into 6 brief-defined groups (Collaboration, Professional Work, Performance, Production, Studio/Space, Equipment) | `ProjectDoc.opportunityType` | **Added 2026-08-16, Phase 2, seeded in source, not run against live Firestore.** Deliberate value-overlap with `engineer_specialties`/`studio_services` on `recording`/`mixing`/`mastering` — different axis, same values, for future matching |
| `studio_addons` | *(not seeded — no value list exists yet)* | `StudioBookingDoc.addons` | **Pending.** Referenced by schema, flagged as needing a real value list, never seeded |

---

## Cross-reference notes (from the widget audit, worth keeping visible)

A few widgets and screens were independently touched by different audit passes and are worth reading together rather than in isolation:
- `NetworkingFeed` widget ↔ screen `83` (`networking_feed`) — same `content`/`text` field-name mismatch flagged from both directions.
- `RecommendedCollaboratorsSection`/`CollaboratorMatchWidget` — confirmed hidden (call site commented out) in the Discover audit.
- `AdvancedFiltersBottomSheet` widget ↔ screen `86` — same finding, filtering 100% mock data.
- `GlobalPersistentAudioPlayerBar` — the underlying `.kt` file showed as deleted in `git status` at an earlier audit; not re-confirmed since.
- `WhatsAppShareSheet` — likely the same UI-only fake buttons found in the QR/Share screen (`119`) audit; not confirmed either way.

---

*This document is generated from the live registry source files, not hand-maintained prose — if the underlying `.ts`/`.md` files change, re-generate rather than hand-editing drift into this copy. Companion document: `PRODUCT-SPEC.md` (the narrative product spec for 4 of these 24 flows).*
