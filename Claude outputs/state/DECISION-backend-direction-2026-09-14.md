---
doc: DECISION-backend-direction-2026-09-14
version: 1.0
status: CURRENT
updated: 2026-09-14
verified_against: Firestore musiclinkd-7ae30 live read 14 Sep · musiclinkd-9e6ca875@4d402b6
folder: state
originally: new
---

> **CURRENT** · 14 Sep 2026. Decision record: what happens to the Firebase/Firestore backend now
> that the Lovable app is the product. Answers the question "map Firebase to it, or migrate Firebase
> into Supabase?"
> Ground truth lives in `state/CURRENT.md`.

# Backend direction — neither map nor migrate

## The question

> Should I map the Firebase DB to the Lovable app now, or migrate the Firebase data into Supabase
> instead?

## The answer

**Neither.** Both options assume a problem that does not exist, and one of them makes things
materially worse.

---

## 1. There is no data to migrate

Read live from Firestore `musiclinkd-7ae30` on **14 Sep 2026**, via the public web API key:

| Collection | Docs | Real records |
|---|---|---|
| `users` | **92** | **0** — every id is `mock-` prefixed |
| `studios` | 43 | 0 |
| `taxonomy_categories` | 29 | **29 — real** |
| `reviews` | 11 | 0 |
| `projects` | 8 | 0 |
| `marketplace_items` | 8 | 0 |

It has grown since the 10 Sep audit — users 83 → 92, studios 11 → 43 — and **every one of them is
still mock**. Vedanshu has confirmed the seed data is intentional ("seed is good"); zero real users
is not a problem to solve.

So "migrate the Firebase data" is a non-task. There is no production data. What looks like a
migration is schema-design work wearing a migration's clothes, and it should be scheduled and judged
as design work.

The one genuine exception is **`taxonomy_categories` — 29 real, curated categories.** That is the
only content in Firestore worth moving, and §4 says when.

---

## 2. Mapping the app to Firestore is the worst of the three options

Identity already lives in Supabase: `auth.users`, `profiles`, `portfolio_items`, the
`profiles.visibility` RLS applied 13 Sep, and the 11 seeded linked-profile rows.

Pointing the Lovable app at Firestore for everything else puts **users in Postgres and their content
in Firestore, joined in application code**. That is not one backend with a bridge. It is two
backends, two auth models, two consistency stories, and a class of bug that only appears under
concurrency.

It also means every new feature has to decide which database it belongs to — a decision that will be
made inconsistently, under time pressure, by whoever is writing that feature.

---

## 3. Two properties of the current Firestore that bear on any plan

Neither is a reason to act today. Both are reasons not to grow the user store there.

**`/users` is world-readable at document level.** The rule is `allow read: if true`, and Firestore
has **no field-level read control** — so the whole document is published, including `email`, which
is present on all 92 user docs. The schema also declares `mobileNumber`, `whatsappNumber`, `gstin`,
`fcmToken` and `geo` on the same document, and its own comment says `mobileNumber` is *"already
written by the real onboarding flow (OnboardingViewModel.kt)"*.

**The first real OTP signup publishes that person's phone number**, and no rule change can prevent
it. The fix is either a split public-profile document or a database with column-level grants.

**The project is on the Spark plan with no Cloud Functions deployed.** Two consequences already live
in the code:

- `profile_analytics` counters are **written by the viewing client**, so anyone can inflate their own
  profile views.
- `page_content` is a CMS that **writes to nothing** — `frontend/website/` is static HTML and never
  reads the collection, so an admin can save a page, see it succeed, and change nothing.

Both disappear in Postgres, where a trigger or a server function does that work.

---

## 4. What to do instead

### Step 1 — Taxonomy into Supabase, now

29 categories, 95 options needing real slugs (22 categories already have them). `languages` maps to
ISO 639-1 so the identity is never the display string.

This goes first because **roughly 20+ columns across the entire intended schema are typed as plain
`string` purely because their real values belong in taxonomy tables** — `vocalRange`,
`sessionAvailability`, `budgetTier`, `bookingStatus`, `sessionType`, `durationTier`, `specialty`,
`djType`, `collaborationType`, `itemCategory`, `services`, `amenities`, `dawStack`, `singingStyles`
and more. Nothing downstream can be modelled correctly until the thing they reference exists.

With zero real users, **this is the cheapest it will ever be.** Spec is written and ready:
`specs/TAXONOMY-MIGRATION-SPEC.md`.

### Step 2 — Grow Supabase tables as features need them

Not 34 at once. The Firestore schema is a **design document, not a template**:

- 8 collections have zero documents and no confirmed writer even in the old app — `blocks`,
  `offers`, `marketplace_orders`, `opportunity_applications`, `profile_analytics`, `notifications`,
  `user_settings`, `user_bookmarks`.
- Several shapes exist only because Firestore cannot join or inherit. `RoleProfileDoc` is a 9-way
  discriminated union across 9 document types. `UserDoc` is a ~60-field god-document with TALENT,
  BUSINESS, BAND and VENUE extensions in one type. `collaboratorUids` exists, per its own comment,
  because *"Firestore has no server-side joins."* `FeedPostDoc.authorName`/`authorRole`/
  `authorAvatarUrl` and `ProjectDoc.posterName`/`posterAvatarUrl` are the same denormalisation.

**Porting those into Postgres imports the workaround without the constraint.** In Postgres they are
joins, a `profiles` core plus per-type tables, and one `role_profiles` table with `(owner_uid,
role_key)` and a `jsonb attrs`.

Also fix in the port rather than carry across: money as strings (`MarketplaceItemDoc.price: string`
while `StudioDoc.hourlyRate: number`) → integer minor units; dates as strings → `date` /
`timestamptz`; `instrumentProficiency: string[]` holding `"Guitar (Expert)"` → a join table
`(profile_id, instrument_slug, proficiency)`.

Full field-by-field detail: `audits/FIRESTORE-SCHEMA-AUDIT.md` and
`audits/DB-GAP-TO-LOVABLE-PARITY.md`.

### Step 3 — Leave Firestore alone

It backs a working admin panel at `admin.musiclinkd.com` — 16 collections, 34 document types, real
module and capability registries. **That panel is the actual asset in there**, not the mock records.
It keeps working while the Lovable app grows its own tables.

---

## 5. The decision that is actually open

**Not the data. The admin panel.**

The frontend is settled — Lovable is the host, Supabase is its database. What is not settled is
whether the admin eventually moves to Postgres too, or stays on Firebase reading a second database.

That decision is worth taking deliberately, later, once the Lovable app has enough tables that the
admin has something real to administer. Taking it now would be guessing.

Three facts that should be on the table whenever it is taken:

1. Two databases means the admin and the app can disagree about the same record.
2. The Firestore PII exposure in §3 is unfixable with rules, so any plan keeping user records there
   needs a split public-profile document.
3. Spark-plan constraints mean anything needing a server-side trust boundary — counters,
   moderation, CMS publishing — cannot be built on that project without moving to Blaze, which is
   itself a cost decision that stops mattering if the move to Postgres happens.

---

## 6. What was rejected, and why

| Option | Rejected because |
|---|---|
| **Map the Lovable app to Firestore** | Identity is already in Supabase. Splitting users from their content across two databases, joined in app code, is a permanent consistency problem and a per-feature decision nobody will make consistently. |
| **Migrate Firestore data into Supabase** | There is no data. 92 user docs, 0 real. This is schema design, and calling it a migration hides that. |
| **Port all 34 document types at once** | 8 have no writer; several are Firestore workarounds. Porting the shape imports the smell without the constraint. |
| **Decide the admin panel's fate now** | Nothing forces it today, and the right answer depends on how many tables the app grows. |
