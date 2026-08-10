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
