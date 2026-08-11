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

---

## Prompt F — Round 3: still-open corrections, profile-type design, and a verification note

**Before sending, read this:** a verification pass (2026-08-11) checked
every claim in your `v1.9.1` changelog entry against the live source, item
by item. All 11 substantive claims — including several that overlap with
Prompts D/E below — were false when checked against the actual code, not
partially confirmed. The strongest signal: `FirebaseManager.kt`, central to
4 of those claims, has had **zero changes — committed or uncommitted —
since the initial commit**, despite three separate changelog entries across
different dates claiming functions were added to it. Please paste the
actual diff of the changed file for every section below, not just an
updated schema doc or changelog entry — that's the only way we can confirm
a fix landed instead of just being described.

```
This is a third correction pass. Two things up front, because the same
pattern has happened twice now: both times I sent field-name corrections
for studio_bookings and connections, you updated BackendSchemas.kt /
BACKEND_SCHEMA_SPEC.md to show the corrected names, but StudioBookingScreens.kt
and ConnectionsScreen.kt were never actually changed — the real write code
still uses the old field names today. For every section below, please paste
back the actual diff of the screen/viewmodel file you changed, not just the
updated schema doc — a schema-doc-only change will not be treated as done.

=== 1. Studio Bookings — this is still broken, please fix the actual write code this time ===

StudioBookingScreens.kt's booking-confirmation write (around where
db.collection("studio_bookings").document(...).set(...) is called) still
writes:
  "artistUserId" (should be "userId")
  "totalCostAmount" (should be "totalAmount")
  "statusKey" (should be "bookingStatus")
and never writes "slotText" or "paymentStatus" at all, despite both being
in the schema you already wrote. Please fix the actual hashMapOf(...) /
data-class construction in the write function, the .whereEqualTo(...) query
that reads these fields back, and the cancellation-update call — all three
places currently use the old names. Paste the diff of the actual changed
lines in StudioBookingScreens.kt when done.

=== 2. Connections — two different writers disagree with each other, not just with the schema ===

There are two separate places that write to the "connections" collection:
FirebaseManager.kt's sendConnectionRequest (this one is already correct —
senderUid/targetUserId/status, correct doc ID) and a second function in
ConnectionsScreen.kt (used for accept/reject/request-sent) that writes
connectionId/targetName instead, with a plain local ID as the doc ID
instead of "${senderUid}_${targetUserId}". Please point the second writer
at the same document shape and ID scheme as the first, so both paths agree.
Paste the diff of the changed function in ConnectionsScreen.kt.

=== 3. Profile-type schema — new work, please confirm the design before building ===

The app has three separate, disconnected ways of guessing whether a
profile is Talent/Band/Business/Venue (a dashboard-badge substring match
on primaryRole, a headline substring match in PublicProfilePreviewScreen,
and DiscoverData.kt's ProfileType enum used only by mock data) — none of
them write or read a real field. Please add:

  users/{uid}.profileType: string   — REQUIRED, set once at profile
                                       creation, values from a new
                                       taxonomy_categories/profile_types
                                       category: "talent", "band",
                                       "business", "venue_event"
                                       (matching your existing ProfileType
                                       enum's casing).

For the Business family (Studio/Label/School/Dealer), extend the EXISTING
"studios" collection rather than creating a new one — add:
  businessType: string   — from a new "business_types" taxonomy category:
                            "studio", "label", "school", "dealer"
  businessName: string
  tagline: string
  operatingHours: string
  gstin: string
  services: string[]

For Band, a NEW top-level "band_profiles" collection, doc ID = the owning
user's Firebase Auth UID:
  ownerUid: string
  members: array of { name: string, instrument: string, role: string }
  technicalRider: string
  stagePlotUrl: string

For Venue, a NEW top-level "venue_profiles" collection, same doc-ID scheme:
  ownerUid: string
  capacity: number
  features: string[]

Before writing any of this, confirm back: (a) does StudioDoc.amenities
already cover what Label/School/Dealer would use for "equipment", or do
they need a separate field? (b) should the existing BandProfileLayout /
VenueProfileLayout hardcoded UI (in ProfileManagementScreens.kt) be wired
to read/write these new fields as part of this change, or is that a
separate follow-up? Don't build the UI wiring until this is confirmed.

=== 4. Portfolio projects — still schema-only, please confirm your Prompt D/E work actually shipped ===

Per the standing instruction at the top of this prompt: please paste the
actual diff showing portfolio_projects being written to Firestore from
PortfolioViewModel.kt's publishProject() function, with the corrected field
names from the previous prompt (ownerUid, artworkTheme, projectViews,
likes, shares, ownerRoles, etc.) — if this hasn't been done yet, please do
it now rather than reporting it as already complete.

=== 5. feed_posts — field names need reconciling WITH a migration plan, this collection has live data ===

Unlike the other items in this prompt, feed_posts already has real posts
being written today, so this isn't a simple rename. Current write code
(FirebaseManager.kt's createFeedPost) writes "content" (should read as
"text" to match the admin schema), and adds "authorName"/"authorRole"
fields directly onto each post (denormalized, not in the documented
schema) plus a "mediaUrl" field whose meaning your own read code
(NetworkingFeed's post-mapping function) doesn't agree with (it currently
reads mediaUrl back as an avatar image, not media content — please check
whether this is intentional or a bug on your end first).

Please: (a) confirm whether "authorName"/"authorRole" denormalization is
intentional (avoids a join against "users" when rendering the feed) — if
so, that's fine, just confirm it so the admin schema doc can be updated to
match reality instead of the other way around. (b) either rename "content"
to "text" with a one-time migration/backfill for existing documents, or
tell me how many real feed_posts documents currently exist so we can decide
whether a backfill is worth writing. (c) fix the mediaUrl read/write
mismatch — decide whether it's for audio (as documented) or avatar images
(as currently read), and align both sides.

=== 6. Small bug, unrelated to schema, found in the same audit ===

NativeQRCodeCanvas (in your Compose UI components file) takes a
"contentUrl" parameter but never actually reads it inside the Canvas
drawing code — it always renders the same fixed pattern no matter what URL
is passed in. This means the QR code shown to users doesn't actually encode
their profile link. Please fix this so the canvas actually encodes
contentUrl (a real QR encoding library, or at minimum a URL-dependent
pattern) rather than a static decorative grid.

=== 7. Vocabularies to confirm before I create taxonomy categories for them ===

I found these controlled-vocabulary-shaped fields in your app but don't
have your source's exact literal option lists — please send me the full
value lists (in the same style as your earlier Genres/Instruments lists)
for: vocal range, gender, session/booking duration tiers, session mode,
studio add-ons (confirm: is this the same list as your existing
"studio_amenities", or genuinely different?), the 5-state availability
status (I only have partial values from a UI screenshot), verification ID
type, subscription plan tier, and service rate type. I'll create matching
taxonomy_categories entries once I have the real lists — please don't
invent new ones on your end in the meantime.

=== 8. Experience-seniority reconciliation — please don't add a 4th vocabulary ===

Your app currently has THREE separate "how experienced is this person"
vocabularies with no shared mapping: SkillProficiency.yearsOfExperience
("1-2 Years"/"3-5 Years"/"5-10 Years"/"10+ Years"), .proficiencyLevel
("Beginner"/"Intermediate"/"Advanced"/"Expert"), and Discover's
experienceLevel field ("Rising Star (1-2 yrs)"/"Established (3-5
yrs)"/"Pro (5+ years)"/"Legend (10+ yrs)"). Before any more schema work
touches "experience," please tell me: is Discover's version meant to be a
computed/derived display label from the other two, or is it a genuinely
separate user-facing "seniority tier" concept? I don't want to add a 4th
vocabulary on top of three that already don't agree with each other.
Separately: experience should be tracked at TWO levels, both need to
persist — a per-instrument map (instrumentProficiency) AND a single
per-primary-role value (roleExperience). Confirm your persistence fix
keeps both, not collapsed into one field.

=== 9. Unpersisted onboarding data ===

instrumentProficiency, roleExperience, and networkingLinks are all
collected during onboarding but never make it into your Firestore write
call (OnboardingViewModel's confirmProfile() / saveUserProfileToFirestore)
or into Room. Same for profilePicturePath and coverPicturePath — collected
via updateProfile() but absent from the final write payload. Please add
all five to the write payload — instrumentProficiency and roleExperience
as nested maps/fields on users/{uid}, networkingLinks as part of the
social_links collection from Prompt D section 1 (one document per
platform, not a map field) — and confirm which approach you used.

=== 10. Settings and moderation — please wire these to real writes ===

Two flows currently show a success snackbar and discard the input instead
of writing anything: (a) SettingsFlow's Account/Privacy/Notifications/
Security/Appearance screens — please write these to a new "user_settings"
collection (doc ID = user's UID) once I confirm its field shape with you
next round; hold off on this one specifically until I send the shape. (b)
HelpAndSupportFlow's Report User, Report Listing, and Feedback actions —
please wire these NOW to write real documents to the already-deployed
"moderation_flags" collection (targetType, targetId, reportedBy, reason,
status — same shape the admin panel already has full CRUD for) and
"support_tickets" collection, instead of just showing a snackbar.

=== 11. Category/instrument selection must respect a single profileType — please confirm this design before building ===

A user has exactly one profileType (never two profile views at once), so
primaryRole, supportingRoles, and instrument selections all need to stay
consistent with whichever one is chosen. Proposed approach — please confirm
or counter-propose before implementing:

  - Change UserDoc.secondaryRole (single string) to
    UserDoc.supportingRoles: string[] (max 2 entries) — primaryRole plus up
    to 2 supportingRoles, all validated against the same profileType.
  - Tag each option in the "person_types" taxonomy category with a new
    metadata field: applicableProfileTypes: string[] (using the
    TaxonomyOptionDoc.metadata field the admin panel already supports) —
    e.g. "Vocalist" -> ["talent"], "Recording Studio" -> ["business"],
    "Concert Hall" -> ["venue_event"], "Ensemble/Band Manager" -> ["band"].
    Once profileType is set during onboarding, filter the role-picker UI to
    only show person_types options whose applicableProfileTypes includes
    it — this prevents the impossible combination from being selectable at
    all, rather than validating it after the fact.
  - Only show the instrument-proficiency onboarding step when profileType
    is "talent" or "band" — Business/Venue profiles use equipmentList /
    venueFeatures instead, which don't need a per-instrument mapping table.

Please confirm this approach (or propose an alternative) before building
it — this changes an existing field's type (secondaryRole ->
supportingRoles), so I want to agree on the shape first.

=== 12. Auth — this is a product decision, not a code request. Please answer these questions, don't fix anything yet ===

A deeper audit found the registration flow itself has real problems, not
just field-name mismatches:
  - verifyOtp()'s check is effectively "any 6-digit string passes" (the
    `|| enteredOtp.length == 6` makes the intended "123456" check dead) —
    no real SMS/OTP verification exists.
  - Google Sign-In's native flow reliably fails (fabricated OAuth client
    ID) and falls back to a fully hardcoded fake profile with zero network
    calls.
  - The registration write's UID fallback
    (`?: "local_uid_${email.hashCode()}"`) means most real signups today
    write to Firestore under a hash-of-email document ID with no
    corresponding Firebase Auth account at all.

Before we prompt any code change here: (a) should OTP become real (needs a
real SMS gateway or Firebase Phone Auth — a cost/infra decision on our
side, not something to build speculatively), (b) should Google Sign-In get
a real OAuth client (blocked on us registering a real applicationId +
google-services.json first), (c) is the local_uid_${email.hashCode()}
fallback acceptable as a permanent pseudo-identity, or should registration
hard-block without a real Firebase Auth UID? Please just answer these
three, don't change any auth code until we've decided.

Please confirm each of these 12 sections back to me one at a time, WITH the
actual code diff for any section that touches existing write code
(sections 1, 2, 4, 5, 6, 9, 10b) — not just a schema doc update. Section 12
specifically needs answers, not code.
```
