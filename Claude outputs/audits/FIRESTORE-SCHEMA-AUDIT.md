---
doc: FIRESTORE-SCHEMA-AUDIT
version: 1.0
status: CURRENT
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
folder: audits
originally: FIRESTORE-SCHEMA-AUDIT.md
---

> **CURRENT** · v1.0 · 2026-09-13. 1063-line schema audited against live Firestore. Unaffected by frontend changes.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

# Firestore schema audit — `admin/types/firestore.ts` (1063 lines) vs. live `musiclinkd-7ae30`

Read in full, then checked against the live database. Every count below came from the live
Firestore REST API on 10 Sep, not from the file's comments — the file is honest about its own
uncertainty (it says "no confirmed app-side writer" 14 times), so the database is the arbiter.

**The Kotlin app is not in this repo**, so I could not verify writers against app source. Where
the file says "unverified", I have left it unverified rather than resolving it by assumption.

---

## 1. Exposure — read with a public API key and nothing else

I enumerated every collection using only the web API key. **13 collections are world-readable:**

| Collection | Docs | |
|---|---|---|
| `users` | 83 | all `mock-` |
| `taxonomy_categories` | 29 | real |
| `modules` | 25 | real |
| `portfolio_projects` | 20 | all `mock-` |
| `studios` | 11 | all `mock-` |
| `reviews` | 11 | all `mock-` |
| `page_content` | 9 | real |
| `projects` | 8 | all `mock-` |
| `marketplace_items` | 8 | all `mock-` |
| `role_profiles` | 6 | all `mock-` |
| `feed_posts` | 5 | all `mock-` |
| `capabilities` | 5 | real |
| `brand_assets` | 2 | all `mock-` |

Denied by rules: `admin_roles`, `connections`, `studio_bookings`, `chat_threads`,
`verification_requests`, `moderation_flags`, `support_tickets`, `broadcast_messages`,
`audit_logs`, `notifications`, `user_settings`, `blocks`, `offers`, `marketplace_orders`,
`opportunity_applications`, `profile_analytics`, `user_bookmarks`, `flows`, `widgets`.

**The rules are not uniformly broken.** Private things are genuinely locked; public directory
content is genuinely public. That is a defensible design — with one hole.

### The hole: `UserDoc` carries PII and Firestore has no field-level rules

`allow read: if true` on `/users/{userId}` publishes the **entire document**. `UserDoc` declares:

```
email            mobileNumber     whatsappNumber
gstin            fcmToken         geo
```

Verified live: **`email` is present on all 83 publicly-readable user documents.** `gstin` on one.
`mobileNumber`, `whatsappNumber` and `fcmToken` are absent from seed data — but the schema comment
states mobileNumber is *"already written by the real onboarding flow (OnboardingViewModel.kt)"*.

**So the first real OTP signup publishes that person's phone number to anyone with the project ID.**
Not a hypothetical: the writer exists, only the users don't yet.

Firestore cannot fix this with rules — it has no field-level read control. The fix is either a
split public-profile document, or Postgres, where a view or column-level grant handles it directly.
**This is a concrete argument for the Supabase move that I had not made before.**

Note this is the *third* instance of the same class of bug: Supabase `profiles` RLS `USING (true)`,
Firestore `/users` `allow read: if true`, and now field-level PII inside the latter.

---

## 2. Most of this schema is scaffolding, not a live system

This is the dominant structural fact and it changes what "migrate" means.

**Collections with zero documents — declared, ruled, typed, never written:**
`blocks` · `offers` · `marketplace_orders` · `opportunity_applications` · `profile_analytics` ·
`notifications` · `user_settings` · `user_bookmarks`

(Those return DENIED rather than empty, so "zero" is inferred from the file's own
"no confirmed app-side writer yet" plus the absence of any seed data for them. Confirm with
admin credentials before acting.)

**Fields explicitly marked as having no writer:** `supportingRoles`, `username`, `vanityUrl`,
`qrCodeUrl`, `coverImageUrl`, `profileType`, `collaboratorUids`, `geo`, `galleryUrls`,
`blockedDates`, and every Phase-2 field on `ProjectDoc` (`skillsRequired`, `languagesRequired`,
`locationType`, `deadline`, `opportunityType`, `posterName`, `posterAvatarUrl`).

**`role_profiles` holds 6 documents against 9 declared role types.**

**Consequence for the migration:** you are not porting a working system. You are porting
*one working core* (users, studios, bookings, projects, marketplace, reviews, taxonomy, the admin
registries) plus *a large body of design intent that has never run*. Port the first faithfully.
Treat the second as greenfield Postgres design — because several of those shapes exist only to
work around Firestore, and reproducing them in Postgres would be copying the workaround without
the constraint.

---

## 3. Firestore-shaped decisions that should not survive the move

**`RoleProfileDoc` — a 9-way discriminated union across 9 document types.** This exists because
Firestore has no joins and no table inheritance. In Postgres this is one `role_profiles` table
with `(owner_uid, role_key)` and a `jsonb attrs`, or nine narrow tables — a real choice, but the
union type itself is a Firestore artifact.

**`UserDoc` is a god-document.** TALENT, BUSINESS, BAND and VENUE extensions all live in one
type — roughly 60 fields where a given row uses a fraction. `businessName`, `bandName`,
`venueName`, `lineupMembers`, `capacity`, `artistRoster`, `courses`, `talentRoster`… In Postgres
this is a `profiles` core plus per-type tables. Porting it as one wide table imports the smell.

**`UserDoc.collaboratorUids`** — the comment states its purpose outright: *"Firestore has no
server-side joins, so without this, rendering 'who has this person worked with' would require
reading every project."* **That is a denormalisation with no reason to exist in Postgres.** Delete
it in the port; it becomes a join. Same question for `FeedPostDoc.authorName` / `authorRole` /
`authorAvatarUrl` and `ProjectDoc.posterName` / `posterAvatarUrl`.

**`ProjectDoc` doubles as Opportunities.** One collection, two concepts, merged by a prior
reconciliation. Fine in a document store; in Postgres decide deliberately whether it is one table
with a discriminator or two.

**`ReviewDoc` is polymorphic** — `targetType: "STUDIO" | "USER" | "MARKETPLACE_ITEM"` plus a bare
`targetId`. No foreign key is possible against that shape. Either three FK columns with a check
constraint, or accept it and validate in a trigger.

---

## 4. Data-shape defects to fix in the port, not carry across

**Money as strings.**
`MarketplaceItemDoc.price: string` — while `StudioDoc.hourlyRate: number` and
`OfferDoc.amount: number`. `BusinessServiceOffering.price?: string`. These should all be integer
minor units (paise) or `numeric`. `UserDoc.hourlyRate` already carries a scar from this:
*"migrated from a free-text display string."*

**Dates as strings.** `BandShow.date`, `PortfolioProjectDoc.releaseDate`,
`StudioBookingDoc.date`, `MarketplaceOrderDoc.startDate`/`endDate`. → `date` / `timestamptz`.

**Structured data flattened into display strings.**
- `instrumentProficiency?: string[]` — `"Guitar (Expert)"`. The file itself flags this as
  *"a flatter shape than the app's in-memory Map<instrument, SkillProficiency>."* This is the
  genuine case for a join table: `(profile_id, instrument_slug, proficiency)`.
- `roleExperience?: Record<string, string>` — `{ "Producer": "5 years" }`. Free-text duration
  keyed by free-text role. Both halves should be typed.
- `BusinessServiceOffering.duration?: string`.

**`artworkTheme`** is a palette keyword, not an image. The file warns: *"don't treat this as an
image URL until real upload UI exists."* Name it so in Postgres.

**Taxonomy-backed fields still typed `string`.** `vocalRange`, `sessionAvailability`, `budgetTier`,
`bookingStatus`, `sessionType`, `durationTier`, `specialty`, `djType`, `collaborationType`,
`category`, `itemCategory`, `verificationIdType`, `type`, `services`, `amenities`, `dawStack`,
`ownerRoles`, `singingStyles`, `compositionStyles`, `categories`. Every one of these should
reference `taxonomy_options(category_key, slug)`. This is the same problem as genres/languages/
instruments, just spread across the rest of the schema — and it is the argument for doing the
taxonomy work *before* the rest of the port, not after.

---

## 5. Two live defects worth naming separately

**`page_content` is a CMS that writes to nothing.** 9 real documents. The file states it plainly:
*"frontend/website/ is static HTML and never reads this collection at runtime, so a save here
doesn't change what's live."* Someone can edit a page in the admin, see it save, and change
nothing. That is worse than no CMS. Either wire the build-time pull or disable the module.

**`profile_analytics` counters are written by the viewing client.** The file explains why: the
project is on the **Spark plan with no Cloud Functions deployed**, so *"counter increments are
written directly by the viewing client."* Anyone can inflate their own profile views by writing
to the endpoint. In Postgres this is a server function or a trigger, and the problem disappears.

The Spark constraint shows up twice more — `ModuleRegistryDoc` toggles are written client-side
for the same reason, and the Remote Config mirror is deferred. **Blaze vs. not is still the open
question it was a week ago**, and it stops mattering entirely if the move to Postgres happens.

---

## 6. Port / redesign split

**Port faithfully** (real data, real writers, working admin):
`users` core · `studios` · `studio_bookings` · `connections` · `feed_posts` · `chat_threads` ·
`projects` · `marketplace_items` · `reviews` · `verification_requests` · `moderation_flags` ·
`support_tickets` · `broadcast_messages` · `audit_logs` · `taxonomy_categories` · `modules` ·
`capabilities` · `flows` · `widgets` · `page_content`

**Redesign in Postgres** (no live data, no confirmed writer, or a Firestore-shaped workaround):
`role_profiles` · `blocks` · `offers` · `marketplace_orders` · `opportunity_applications` ·
`profile_analytics` · `notifications` · `user_settings` · `user_bookmarks` · `brand_assets` ·
the four `UserDoc` profile-type extensions · every denormalised fan-out field

**Do first, before either:** the taxonomy tables from `TAXONOMY-MIGRATION-SPEC.md`. Twenty-plus
columns across the rest of this schema are taxonomy references waiting for something to reference.

---

## 7. Open, unverified

- **Firebase Auth accounts with no `users` document** — needs admin credentials.
- **Whether the eight zero-document collections are truly empty** — inferred, not measured.
- **`UserDoc.genres` vs `selectedGenres`** — the taxonomy seed comment names a field the type
  does not declare. One is wrong.
- **`project_roles` vs `person_types`** — five identical slugs across two categories.
- **`BusinessServiceOffering` for Talent** — the file flags this as an unresolved product
  decision (widen to base `UserDoc`, or a Talent-scoped field). Still unresolved.
- **`app/docs/firestore/Firestore_Schema_Index_Rules.md`** — the file's header says it mirrors
  that document *"exactly"* and must be kept in sync **by hand**. That doc is in the app repo,
  which I cannot see. If the two have drifted, this audit inherits the drift.
