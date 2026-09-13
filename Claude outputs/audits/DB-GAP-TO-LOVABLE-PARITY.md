---
doc: DB-GAP-TO-LOVABLE-PARITY
version: 1.0
status: CURRENT
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42 (audits/FIRESTORE-SCHEMA-AUDIT.md, specs/TAXONOMY-MIGRATION-SPEC.md)
folder: audits
originally: new
---

# How much database is missing to reach parity, now that Lovable is the direction

Straight answer: **the live Supabase database has 2 tables. The intended product needs roughly
34 document shapes' worth of data.** That gap was already fully mapped by two existing documents
in this folder — `FIRESTORE-SCHEMA-AUDIT.md` and `TAXONOMY-MIGRATION-SPEC.md` — both dated today.
This page is the executive summary of those two; read them for field-by-field detail.

## Where things stand right now

| | |
|---|---|
| Live tables in the Lovable app's database (Supabase `gsqeymhtyzepwcnorjun`) | **2**: `profiles`, `portfolio_items` |
| What those 2 tables cover | Auth, onboarding, portfolio items only |
| Everything else in the product (booking, marketplace, opportunities, reviews, bands, business, messaging, notifications, verification) | **Seeded mock data, by decision** — not backed by any table |
| The full intended data model, already designed | 16 Firestore collections / **34 document types**, in `admin/types/firestore.ts`, backing the Firebase admin panel |
| Real production data in that Firestore model | **Zero.** All 83 user documents are `mock-` prefixed. Nothing to migrate, nothing to backfill — this is schema design work, not a data migration. |

Since the decision is to take the Lovable/Supabase app forward as the real product, the direction
is: **design Postgres tables from the Firestore schema's intent, not port Firestore's shape
literally.** The audit already worked out which parts of that intent are worth carrying over as-is
and which are Firestore-specific workarounds that shouldn't survive the move.

## The gap, split by what's worth porting vs. redesigning

**Port faithfully — these have real shape and real (if mock) data behind them today:**
`users` (core fields only, not the 4 profile-type extensions) · `studios` · `studio_bookings` ·
`connections` · `feed_posts` · `chat_threads` · `projects` · `marketplace_items` · `reviews` ·
`verification_requests` · `moderation_flags` · `support_tickets` · `broadcast_messages` ·
`audit_logs` · `taxonomy_categories` · `modules` · `capabilities` · `flows` · `widgets` ·
`page_content` — **20 collections.**

**Redesign in Postgres — no live data, no confirmed writer even in the old app, or exists only to
work around Firestore's lack of joins/inheritance:**
`role_profiles` (9-way union → normalize to one table + `jsonb`, or 9 narrow tables) · `blocks` ·
`offers` · `marketplace_orders` · `opportunity_applications` · `profile_analytics` ·
`notifications` · `user_settings` · `user_bookmarks` · `brand_assets` · the 4 `UserDoc`
profile-type extension blocks (Talent/Business/Band/Venue — currently ~60 fields flattened into
one god-document) · every denormalized fan-out field (`collaboratorUids`, `authorName` on posts,
`posterName` on projects — these exist only because Firestore can't join; in Postgres they're just
joins) — **10 collections' worth of intent, redesigned rather than copied.**

## What has to happen *before* either of those

**Taxonomy, first.** Roughly 20+ fields across the whole schema are typed as plain `string`
specifically because their real values live in taxonomy tables, not in code — genres, instruments,
languages, session types, booking statuses, and more. `TAXONOMY-MIGRATION-SPEC.md` already has the
full mapping ready to execute: 29 categories, 95 options needing real slugs (22 categories already
have them), `languages` mapped to ISO 639-1 codes specifically so the identity isn't the display
string. This is the cheapest it will ever be to fix, because there are zero real users yet.

## Real defects to fix in the port, not carry across

- **Money stored as strings** in some places (`MarketplaceItemDoc.price`), numbers in others
  (`StudioDoc.hourlyRate`) — standardize to integer minor units or `numeric`.
- **Dates stored as strings** (`BandShow.date`, booking dates, order dates) — should be
  `date`/`timestamptz`.
- **Structured data flattened into display strings** — e.g. `instrumentProficiency: string[]` holds
  `"Guitar (Expert)"` instead of a real `(profile_id, instrument, proficiency)` join table.
- **Polymorphic references with no real foreign key** — `ReviewDoc.targetType` + bare `targetId`
  needs either three FK columns with a check constraint, or a trigger-validated design.

## Two security bugs worth knowing about regardless of DB choice

- Firestore's live `/users` rule (`allow read: if true`) publishes the **entire** document —
  including `email`, present on all 83 documents today. Firestore has no field-level read control;
  Postgres/Supabase fixes this trivially with a view or column-level grant. This is itself an
  argument for the Supabase direction, not just a note.
- Supabase's own `profiles` table currently has RLS `USING (true)` too (per `CURRENT.md`, this was
  fixed 13 Sep — worth re-confirming it's still applied before treating it as closed).

## Bottom line

This isn't "add a few tables." It's roughly **30 table-equivalents of schema design work**
(20 ported + 10 redesigned), gated on a taxonomy pass that's already spec'd and ready to execute,
plus a handful of data-shape fixes that are cheap now and expensive after real users exist. None of
it is blocked on missing information — the design intent already exists in the Firestore schema and
in Notion's product docs; what's missing is the Postgres implementation.

**Not yet decided, flagging rather than assuming:** exact effort/timeline estimate for the ~30-table
build-out, and whether it's sequenced as one migration or phased alongside the Lovable app's own
Phase 8 route sweep (`~97 hours` per `state/CURRENT.md`). Worth a explicit scoping pass before
committing to a timeline.
