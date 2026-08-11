# AI Studio Prompts — Master Data Sync & Other CRUD

Copy-paste prompts for AI Studio's chat, one at a time. Each is written as
an instruction to AI Studio, not a description for you. Written August
2026, against the current admin schema (`admin/types/firestore.ts`,
`firestore.rules`) — if either changes later, these need a re-check.

**Read this first — the one detail that makes or breaks the sync:** every
master-data option has both a `label` (what's shown on screen) and a
`value` (what's actually stored on the user's record). The app must store
`value`, never `label`. For **person_types**, `value` is the existing
stable slug already in `OnboardingData.kt`'s `RoleItem` (`"singer"`,
`"producer"`, `"guitarist"`, etc.) — the app already has these, it just
needs to start writing them instead of the display string. For **genres**,
**languages**, **instruments**, and **persona_badges**, there's no existing
slug in the app, so the admin master-data seed will set `value` equal to
the exact display string already used today (e.g. `"Bollywood / Filmi"`,
not a slugified version) — specifically so the app's current string values
keep matching with zero app-side reformatting needed.

---

## Prompt A — Fetch master data from Firestore instead of hardcoded lists

```
In OnboardingData.kt, the following are hardcoded local lists: availableRoles,
availableLanguages, availableGenres, availableInstruments. Replace them with
data fetched from Firestore instead of hardcoded Kotlin lists, while keeping
the exact same UI (RoleItem cards, language/genre/instrument chips) working
unchanged.

Firestore structure (project musiclinkd-7ae30):
  taxonomy_categories/{categoryId}                      — category metadata
  taxonomy_categories/{categoryId}/options/{optionId}    — the actual list

Document shape for each option:
  { label: string, value: string, sortOrder: number, isActive: boolean }

Category IDs to fetch:
  - "person_types"    → replaces availableRoles (map each option to a RoleItem;
                         option.value is the existing RoleItem key slug, e.g. "singer")
  - "languages"        → replaces availableLanguages
  - "genres"            → replaces availableGenres
  - "instruments"       → replaces availableInstruments (note: proficiency
                         level/years stays local per-selection, this only
                         replaces the list of available instrument names)

Requirements:
1. Fetch each category's active options (isActive == true), ordered by sortOrder,
   at app startup or when the relevant onboarding screen first loads.
2. Cache the result in Room so onboarding still works offline after the first
   successful fetch.
3. If the fetch fails (no network, category not yet seeded in Firestore, etc.),
   fall back to the existing hardcoded lists currently in OnboardingData.kt —
   do NOT show an empty list or crash. This mirrors how the admin panel itself
   falls back to freeform input when a master-data category isn't seeded yet.
4. Keep using option.value (not option.label) as the identifier stored anywhere
   selections are tracked — label is display-only.
```

---

## Prompt B — Sync selections back to `/users/{uid}`

```
When onboarding completes (or a profile is edited later), the user's selections
need to sync to Firestore's users/{uid} document using these exact field names —
this is what the admin panel (admin.musiclinkd.com/users) reads and edits:

  primaryRole: string       — was previously a coarse "role" enum; now the
                               RoleItem.key slug of the selected primary role
                               (e.g. "producer", not "Music Producer")
  secondaryRole: string     — same, for the optional secondary role; empty
                               string "" if none selected (not null, not omitted)
  genres: string[]          — array of selected genre option.value strings
  languages: string[]       — array of selected language option.value strings
  instruments: string[]     — array of selected instrument option.value strings
  personaBadges: string[]   — array of selected persona badge option.value strings

Also required on every users/{uid} document (already existing fields, just
listed for completeness — don't change these): displayName, email, isVerified,
isBanned, isAvailable, hourlyRate, location, bio, avatarUrl, createdAt.

Important: the Firestore security rules only allow a user to create their own
document if primaryRole is a string and isVerified/isBanned are both false —
and once created, a user can never change isVerified, isBanned, primaryRole,
or secondaryRole on their own document again (only an admin can, via the admin
panel). So primaryRole/secondaryRole should be set correctly at creation time
and not need to change after — if there's a "change your primary role" flow
in Settings, it will fail against these rules by design; that's expected, flag
it if you find one so we can decide whether that flow should even exist.

Do not touch the "musicians" collection anywhere — that's a separate, stray
collection from a different part of this codebase (ProfileManagementScreens.kt's
EditProfileScreen) that should be pointed at this same users/{uid} document
instead, not synced independently. See Prompt C below.

Note: users/{uid} also has a trustBadges: string[] field — admin-granted
trust indicators, distinct from personaBadges. Never write this field from
the app; security rules block a user from ever setting it on their own
document, same as isVerified/isBanned/primaryRole/secondaryRole.
```

---

## Prompt C — Other CRUD to map (priority order, from this session's full app audit)

These aren't written as single copy-paste prompts — each is a bigger unit of
work than A/B above, worth doing one at a time with its own verification.
Priority order, most impactful first:

1. **Fix the `musicians` vs `users` collection split.** `ProfileManagementScreens.kt`'s
   `EditProfileScreen` (line ~342) writes profile edits to a `musicians`
   collection using a slugified-display-name as the document ID — a
   completely different collection and ID scheme than the `users/{uid}`
   document onboarding creates. Right now, any profile edit a user makes
   after onboarding is invisible in the admin panel. Prompt: *"Point
   EditProfileScreen's save at users/{current Firebase Auth uid} instead of
   creating/updating a document in the musicians collection — use
   updateDoc, not a new document, and use the signed-in user's actual UID,
   not a slugified name."*

2. **Wire Studio Booking to Firestore.** Currently 100% mocked — no
   `studio_bookings` document is ever written (see
   `admin/docs/architecture/Service_Flow.md` for the full trace). This is
   the single most "looks done, isn't" flow in the app. Needs: a real
   `studios` collection read (currently `mockStudios` hardcoded), and a
   real `studio_bookings` write on booking confirmation, matching the
   shape in `admin/types/firestore.ts`'s `StudioBookingDoc`.

3. **Wire Portfolio (project publishing) to Firestore.** Also 100% mocked.
   This is bigger than A/B above and genuinely needs its own schema
   design first — you flagged this as a next thing to design (project
   creation, project detail page, external links, project types). That's
   real, separate scope from this doc; happy to design it once you're
   ready to move to it specifically, rather than fold it in here.

4. **Marketplace and Reviews** — same pattern as Studio Booking (mocked
   list, no Firestore writes on create). Lower priority than 1–3 since
   they're less central to the core flows.

5. **Fix the Gemini API key placeholder mismatch** — small, independent,
   already documented in `Edge_Functions_And_Errors.md` — `apiKey ==
   "YOUR_GEMINI_API_KEY"` should be `apiKey == "MY_GEMINI_API_KEY"` in
   `GeminiRecommendationEngine.kt`.

---

## Prompt D — Corrections to AI Studio's own `BackendSchemas.kt` proposal

AI Studio independently designed a parallel schema (`BackendSchemas.kt`,
`BACKEND_SCHEMA_SPEC.md` — never synced to this repo) for master data +
Portfolio, instead of following Prompts A/B/C above. This corrects it
against what's actually deployed, rather than starting over.

```
Please make the following corrections to the backend schema design you proposed
(BackendSchemas.kt / BACKEND_SCHEMA_SPEC.md). These are corrections to align
with the real, already-deployed Firestore structure in this project
(musiclinkd-7ae30) — not a request to redesign from scratch.

=== 1. Stop using /musicians/{userId}/... anywhere ===

"musicians" is a stray, buggy collection from an unrelated bug in
ProfileManagementScreens.kt's EditProfileScreen (writes to
musicians/{slugified-display-name} instead of users/{firebaseUid}) — it is
being fixed separately, not extended. Replace every user-scoped path as follows:

  - Social links: use a NEW top-level collection "social_links", one document
    per (user, platform) pair, document ID = "{firebaseUid}_{platformKey}"
    (matches the existing id-composition pattern used by the "reviews"
    collection in this project). Each document must have an "ownerUid: string"
    field equal to the signed-in user's Firebase Auth UID — do NOT nest under
    /users/{uid}/ or /musicians/{uid}/.

  - Instrument proficiencies: do NOT create a new collection. Store the flat
    list of instrument names the user plays in the existing field
    users/{uid}.instruments (string array, already deployed). If you need
    richer per-instrument data (years active, experience level, primary flag)
    beyond a flat list, STOP and ask first rather than inventing a new
    collection or a musicians subcollection — this needs a real schema
    decision, not an assumption.

  - Portfolio projects: use a NEW top-level collection "portfolio_projects",
    one document per project, with an "ownerUid: string" field equal to the
    creating user's Firebase Auth UID. Do NOT nest under /users/{uid}/ or
    /musicians/{uid}/.

This matches every other user-owned collection already in this project's
firestore.rules (studios.ownerUid, projects.creatorUid,
marketplace_items.sellerUid, studio_bookings.userId) — all top-level
collections with an owner-uid field, never subcollections under /users/{uid}.

=== 2. Point the 5 "Metadata Sync" categories at the existing taxonomy system ===

Your MetadataSyncManagerScreen's 5 tabs — User Roles, Languages, Genres &
Styles, Instruments & Gear, Persona Badges — must NOT create new
metadata_profile_types / metadata_instruments / etc. collections. Point them
at the collection structure that's already live in this Firestore project:

  taxonomy_categories/{categoryId}                      — category metadata
  taxonomy_categories/{categoryId}/options/{optionId}    — the actual list

Document shape for every option (exact field names, all required):
  { label: string, value: string, sortOrder: number, isActive: boolean }

The 5 category IDs, which must match exactly (the admin panel at
admin.musiclinkd.com/master-data already expects these exact keys — do not
invent new ones):
  - "person_types"   (your "User Roles" tab)
  - "languages"       (your "Languages" tab)
  - "genres"           (your "Genres & Styles" tab)
  - "instruments"      (your "Instruments & Gear" tab)
  - "persona_badges"   (your "Persona Badges" tab)

Rules: read is public (allow read: if true already set on
taxonomy_categories and its options subcollection); write is admin-only —
your MetadataSyncManagerScreen's pull direction (Firestore → app) is fine for
any signed-in user, but its push direction (app → Firestore) for these 5
categories should be REMOVED from the debug screen entirely — regular users
and this debug overlay must not be able to write taxonomy option values,
only admins via the web admin panel can. Drop any "push" button for these 5
tabs; keep pull/read only.

Delete your CustomMasterEntity/CustomMasterDao Room schema bump (v7→v8) —
instead, cache pulled taxonomy_categories/{cat}/options rows in Room using
whatever entity shape best fits {label, value, sortOrder, isActive}, no new
DB version bump needed if you can fit it into the existing schema, otherwise
keep the version bump but simplify the entity to those 4 fields only.

=== 3. Net-new master-data categories ===

Two more categories should ALSO go through the same taxonomy_categories
structure (same shape, same rules, created via the existing admin panel, not
via new bespoke collections):

  - "project_types" — values: song, album, ep, podcast, dj_mix, film_score,
    commercial_jingle, live_performance, music_video, custom_project
    (these must match the app's existing availableProjectTypes exactly)
  - "project_roles" — values: Singer, Composer, Music Director, Producer,
    Lyricist, Session Musician, DJ, Mix Engineer, Mastering Engineer, Sound
    Designer, Band Member (matching availableProjectRoles exactly) — this is
    the project-scoped role a person plays on ONE project, distinct from
    person_types (their platform-wide primary/secondary role)

Do NOT build metadata_studio_amenities, metadata_booking_statuses, or
metadata_collab_types yet — those flows (Studio Booking, Marketplace
collaboration) aren't wired to Firestore at all yet (still 100% mocked), so
there's nothing to seed master data for. Skip these three for now.

Do NOT build a generic metadata_external_links / platform-type category for
streaming links — the real PortfolioProject model uses named fields
(spotifyUrl, appleMusicUrl, youtubeUrl, soundcloudUrl, mixcloudUrl,
instagramReelUrl), not a dynamic list. Keep those as named fields in the
portfolio_projects schema (see section 5 below), do not generalize this.

=== 4. Actually wire OnboardingData.kt to read from Firestore ===

Separately from the schema fixes above, OnboardingData.kt's availableRoles,
availableLanguages, availableGenres, availableInstruments are STILL
hardcoded local Kotlin lists — none of your new schema work is actually
wired into the real onboarding screens yet. Please now also:

1. Fetch each of the 5 categories' active options (isActive == true), ordered
   by sortOrder, at app startup or when the relevant onboarding screen first
   loads, from taxonomy_categories/{categoryId}/options.
2. Cache results in Room so onboarding works offline after first fetch.
3. If fetch fails (no network, category not seeded yet), fall back to the
   existing hardcoded lists in OnboardingData.kt — never show an empty list
   or crash.
4. Use option.value (never option.label) as the identifier stored anywhere a
   selection is tracked. For person_types, option.value is the existing
   RoleItem key slug (e.g. "singer", "producer") already used in
   OnboardingData.kt. For genres/languages/instruments/persona_badges,
   option.value equals the exact display string already used today (e.g.
   "Bollywood / Filmi") — no reformatting needed on the app side.
5. When onboarding completes or a profile is edited, sync selections to
   users/{firebaseUid} using these exact field names: primaryRole (string,
   required), secondaryRole (string, "" if none — not null/omitted), genres
   (string[]), languages (string[]), instruments (string[]), personaBadges
   (string[]). Note: the security rules block a user from ever changing
   primaryRole/secondaryRole/isVerified/isBanned on their own document after
   creation — only an admin can. Set them correctly at creation time.

=== 5. Fix the musicians/users bug (do this first, it's the highest-impact fix) ===

In ProfileManagementScreens.kt's EditProfileScreen (around line 342), change:

    db.collection("musicians").document(docId)

to update the signed-in user's own document instead:

    db.collection("users").document(<current Firebase Auth uid>)

using updateDoc/set-with-merge on users/{firebaseUid}, not a new document
keyed by a slugified display name. Keep the same field data being saved
(displayName, bio, location, languages, genres, skills, social links, etc.)
but map field names to the users/{uid} schema (see users/{uid} field list in
section 4 above) rather than the ad hoc field names currently used
(professionalName, headline, skills, linkWebsite, linkInstagram, linkSpotify,
linkYouTube aren't fields on users/{uid} — for now, drop or hold these until
a follow-up decision on social-link storage; do not invent new users/{uid}
fields for them without confirming first).

=== 6. Corrected PortfolioProjectSchema ===

Revise PortfolioProjectSchema (to be written to portfolio_projects/{projectId},
per section 1) to match what the real Portfolio screens actually collect:

  projectId: String
  ownerUid: String                          // NOT ownerUserId, and NOT nested under musicians/users
  name: String
  description: String
  type: String                              // value from "project_types" category
  releaseDate: String
  status: String = "Released"               // "Released" | "Upcoming" | "In Production" | "Draft"
  visibility: String = "Public"             // "Public" | "Connections Only" | "Private Draft"
  spotifyUrl: String = ""
  appleMusicUrl: String = ""
  youtubeUrl: String = ""
  soundcloudUrl: String = ""
  mixcloudUrl: String = ""
  instagramReelUrl: String = ""
  ownerRoles: List<String> = emptyList()    // NEW — was missing from your schema; from SELECT_YOUR_ROLE screen; values from "project_roles" category
  collaborators: List<ProjectCollaborator> = emptyList()
  credits: List<CreditItem> = emptyList()
  artworkTheme: String = "Default"          // RENAMED from artworkUrl — this is currently a palette
                                              // keyword (e.g. "Acoustic", "Techno", "Folk"), not a real
                                              // uploaded file URL. There is no real image-upload UI in
                                              // PROJECT_MEDIA_PREVIEW today. Do NOT imply a Storage URL
                                              // here until real upload is built as separate scope.
  projectViews: Int = 0                     // RENAMED from viewCount to match app's actual field name
  likes: Int = 0                            // RENAMED from likeCount
  shares: Int = 0                           // RENAMED from shareCount
  createdAt: Long = System.currentTimeMillis()
  lastUpdatedAt: Long = System.currentTimeMillis()

Drop commentCount (no comment feature exists), drop bannerMediaUrl (no real
upload UI exists — same reasoning as artworkTheme above), drop durationSeconds
(no UI field collects this) — these can be re-added later if/when the
corresponding UI is actually built. Keep ProjectCollaborator/CreditItem
roughly as your original design but make sure their field names match
ProjectCollaborator/CreditItem in PortfolioData.kt exactly (id, name, role,
avatarEmoji, isVerified, imageUrl for collaborators; collaboratorName, role,
avatarEmoji, imageUrl for credits) rather than inventing new field names.

Please confirm each of these 6 sections back to me one at a time so we can
verify before you regenerate the full BackendSchemas.kt / spec doc.
```

---

## Prompt E — Corrections to AI Studio's round-2, 9-module inventory

A third, larger AI Studio round proposed 9 modules, several of which
directly collide with already-deployed collections and security rules
(`studio_bookings`, `reviews`, `verification_requests`, `connections`), and
Module 9 independently re-invents the already-shipped `modules` registry.
This corrects that round. See `reactive-scribbling-giraffe.md`'s Part I for
the full reasoning behind each correction below.

```
Please make the following corrections to your third-round module inventory
(Modules 6–9, plus the auth/sessions/geolocation/settings/bookmarks items).
These are corrections against the real, already-deployed Firestore structure
in this project (musiclinkd-7ae30) — not a request to redesign from scratch.

=== 1. Stop building BackendFeatureFlag — use the already-shipped `modules` collection ===

A remote feature-flag system already exists and is already deployed:
`modules/{moduleKey}` documents with this exact shape:
  { label: string, description: string, enabled: boolean,
    navModuleKey: string | null, remoteConfigKey: string | null,
    updatedBy: string, updatedAt: Timestamp, createdAt: Timestamp }

It's toggled today from the web admin panel (admin.musiclinkd.com/modules) via
a direct, isAdmin()-gated Firestore write — there is no Cloud Function behind
it (this project runs on Firebase's free Spark plan; Cloud Functions require
Blaze, which isn't provisioned).

Important limitation of the CURRENT v1 design: `enabled` only gates the ADMIN
PANEL's own nav item and route right now (see ModuleRegistryContext in the
admin app) — nothing in the Android app reads this collection today, and the
`remoteConfigKey` field is a documented placeholder for a future Remote
Config mirror that is NOT currently written by anything. Do NOT build your
own reader against Remote Config for this — nothing is populating it.

If you want the Android app to render/hide a whole section (Marketplace,
Studio Booking tab, etc.) based on an admin toggle, read `modules/{key}`
DIRECTLY via the Firestore client SDK (same project, already public-read —
`allow read: if true` — no new rules needed). Suggested keys to read:
"marketplace", "studioBooking" (confirm these match the moduleKey document
IDs actually seeded — check with us before assuming, don't invent new keys).
When `enabled == false`, show your graceful offline placeholder; when true or
the doc doesn't exist yet, render normally (fail open, matching how the admin
panel itself treats a missing registry row).

Delete BackendFeatureFlag and any associated Room entity entirely — there is
nothing for it to do that `modules/{moduleKey}.enabled` doesn't already cover.

=== 2. studio_bookings — exact field-name corrections (this breaks security rules if wrong) ===

The deployed security rules read these EXACT field names off the document —
using different names means bookings silently fail to create or become
unreadable by their own creator:

  allow create: if signedIn() && request.auth.uid == request.resource.data.userId;
  allow read, update: if signedIn()
    && (request.auth.uid == resource.data.userId
        || request.auth.uid == get(.../studios/$(resource.data.studioId)).data.ownerUid
        || isModerator());

Required field names for the studio_bookings document:
  studioId: string           // required, exact name, references the studios doc
  userId: string             // REQUIRED name — NOT "artistUserId"
  date: string                // already deployed, keep
  slotText: string            // already deployed, keep — the admin Bookings
                               // page displays this column today; don't drop it.
                               // Write it as a human-readable summary of the
                               // selected hours, e.g. "2:00 PM – 4:00 PM".
  bookedHourSlots: string[]   // NEW, additive — the granular structured slot list
  totalAmount: number         // REQUIRED name — NOT "totalCostAmount"
                               // (matches admin's StudioBookingDoc.totalAmount)
  bookingStatus: string       // NEW — value from a new "booking_statuses"
                               // taxonomy category: requested, confirmed,
                               // payment_pending, cancelled, completed
  paymentStatus: string       // NEW, optional — "PENDING" | "PAID" | "REFUNDED" | "FAILED"
  paymentTransactionId: string // NEW, optional — your payment gateway's transaction id
  createdAt: Timestamp        // NEW — every other collection in this schema has
                               // this, studio_bookings was missing it, add it

Also: only the studio's owner or a moderator can update bookingStatus/
paymentStatus once created — a booker (the userId on the doc) will only be
able to set bookingStatus to "cancelled" and nothing else, once the
corresponding rules tightening (see admin-side changes) ships. Don't build a
flow that assumes a booker can mark their own booking "completed" or "paid".

=== 3. reviews — exact field-name corrections + booking linkage ===

  allow create, update: if signedIn()
    && request.auth.uid == request.resource.data.authorUid
    && reviewId == request.auth.uid + '_' + request.resource.data.targetId;

Required field names:
  authorUid: string      // REQUIRED name — NOT "reviewerId"/"userId"
  targetType: string     // "STUDIO" | "USER" — REQUIRED name — NOT "entityId"'s type
  targetId: string        // REQUIRED name — NOT "subjectId"
  rating: number
  text: string
  createdAt: Timestamp
  bookingId: string        // NEW, optional — links to studio_bookings/{bookingId}
                            // for "verified purchase". Leave "" if not linked.

Document ID MUST be composed client-side as `${authorUid}_${targetId}` —
NOT a random auto-ID, NOT booking-based. This is how the "one review per
author per target" constraint is enforced by the existing rules.

=== 4. admin_verification_queue — do not build this, it's the same collection ===

verification_requests already exists, already has a `status` field
("PENDING" | "APPROVED" | "REJECTED"), and the web admin panel's Verification
page already filters status == "PENDING" for its approve/reject actions —
that IS the moderation queue. Do not create a second collection. If you need
an in-app "pending verifications" queue view, query
verification_requests where status == "PENDING", ordered by submittedAt.

=== 5. connections — your description doesn't match what's actually deployed ===

The connections collection already exists and is already being written to by
FirebaseManager.kt's sendConnectionRequest — do NOT redesign its ID scheme.
Required, exact, already-live shape:
  senderUid: string        // REQUIRED name
  targetUserId: string     // REQUIRED name
  status: string           // "PENDING" | "ACCEPTED" | "DECLINED" only — if you
                            // need more states (e.g. blocking), stop and ask
                            // first rather than adding new values silently
  document ID = "${senderUid}_${targetUserId}"   // sender-first, NOT
                            // alphabetically sorted — this is the live scheme,
                            // changing it breaks every connection already sent

=== 6. gig_opportunities — fold into the existing "projects" collection, don't create a new one ===

We compared your gig_opportunities shape (role keys, budget amounts, location
limits, applicant counts) against the two candidates already deployed:
marketplace_items (items-for-sale: sellerUid/title/price/category/condition —
wrong shape entirely, don't use this) and projects (creatorUid/title/
roleRequired/budget/status/genres — same concept, already deployed, and the
admin panel already describes this collection as "Open collaboration calls
and project opportunities"). Use "projects", with these additive field
changes (all optional, backward compatible):

  location: string             // NEW, optional — "location limits"
  applicantCount: number = 0   // NEW, optional — "applicant counts"
  collaborationType: string    // NEW, optional — value from a new
                                 // "collaboration_types" taxonomy category:
                                 // work_for_hire, royalties_split, co_write,
                                 // session_payment

Keep roleRequired as a single string and budget as a single string for v1 —
matches what's already deployed. If your gig-posting design genuinely needs
multiple roles per posting or numeric/currency-safe budgets, stop and ask
first; don't silently pluralize or retype these fields.

=== 7. metadata_badges vs UserDoc.personaBadges — different concepts, name them so they're not confused ===

UserDoc.personaBadges (already deployed, self-selected, taxonomy-backed via
the "persona_badges" category from the prior sync round) is NOT the same
thing as your "visual tier badges e.g. Verified Artist, Top Host" — those are
admin-granted trust indicators, not self-selected. Use a NEW field:

  UserDoc.trustBadges: string[]   // NEW — values from a new "trust_badges"
                                   // taxonomy category, ADMIN-GRANTED ONLY.
                                   // A user can never write this field
                                   // themselves (security rules enforce this,
                                   // same as isVerified/isBanned).

Do not repurpose personaBadges for this — keep both fields, they mean
different things.

=== 8. Items you should drop or defer entirely — do not build these now ===

  - metadata_auth_providers: DROP. Firebase Auth's enabled sign-in providers
    are Firebase Console configuration, not app data. Nothing for a
    collection to do here.
  - user_sessions: DEFER. No FCM-token-to-Firestore write exists anywhere
    today and there's no admin-side send infrastructure to target individual
    sessions (no Cloud Functions — Spark plan). Revisit only if a real
    multi-device "manage my sessions" Settings screen gets designed.
  - metadata_cities / sub_areas: DEFER. The real LocationScreen today is
    plain text fields (country/state/city/area), not a picker — there's no
    UI consuming a city taxonomy yet. If built later, do NOT add a 3rd
    Firestore level under taxonomy_categories/{id}/options/{id} (that
    structure is exactly 2 levels) — store sub-areas as an array inside the
    city option's own metadata field instead:
    metadata: { subAreas: ["Bandra", "Andheri West", "Juhu"] }
  - onboarding_progress: DEFER Firestore sync. Keep this local-only (Room or
    DataStore) — there's no cross-device-resume requirement identified
    anywhere in the app today. Simpler is correct here.
  - user_settings: DEFER. No Settings screen with privacy/notification/
    locale preferences exists in the app yet. Design this once that screen
    is actually being built, not speculatively now.

=== 9. user_bookmarks — this one IS legitimate, build it ===

SavedProfilesScreen.kt already exists in the app and is currently 100%
unwired to Firestore (same pattern as Studio Booking before this fix). Build
a new top-level "user_bookmarks" collection:

  ownerUid: string       // the user who saved the bookmark
  savedUserId: string    // the user being bookmarked
  createdAt: Timestamp

Document ID = "${ownerUid}_${savedUserId}" (same composite-ID pattern as
reviews/connections — makes duplicate bookmarks structurally impossible).

=== 10. Two new taxonomy categories, same mechanism as before ===

Create these via taxonomy_categories/{categoryId}/options/{optionId} (same
{label, value, sortOrder, isActive} shape as every other category, admin-
write, public-read) — do NOT create bespoke metadata_* collections for
these:

  - "booking_statuses" — requested, confirmed, payment_pending, cancelled,
    completed (feeds studio_bookings.bookingStatus, see section 2)
  - "studio_amenities" — seed from your own StudioBookingScreens.kt mock
    list (Dolby Atmos, Vocal Booth, Live Room, Valet Parking, Resident
    Engineers, Private Lounge, 24x7 Access, Rehearsal Room, Chill Lounge,
    Drums Included, Parking Space, Instruments Support, Acoustics
    Optimized, Fiber Internet, Stage Monitors, Bar & Diner Access) —
    de-duplicated. Feeds the already-deployed StudioDoc.amenities: string[]
    — no new field on StudioDoc needed.
  - "collaboration_types" — work_for_hire, royalties_split, co_write,
    session_payment (feeds projects.collaborationType, see section 6)
  - "trust_badges" — Verified Artist, Top Host, etc. (feeds
    UserDoc.trustBadges, see section 7)

Please confirm each of these 10 sections back to me one at a time, same as
last round — don't batch-confirm.
```
