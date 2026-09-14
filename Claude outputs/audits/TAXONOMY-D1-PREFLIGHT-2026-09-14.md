---
doc: TAXONOMY-D1-PREFLIGHT-2026-09-14
version: 1.1
status: CURRENT
updated: 2026-09-14
verified_against: musiclinkd-9e6ca875@1937767 · Lovable Cloud DB live read 14 Sep (read-only)
folder: audits
originally: new
---

> **CURRENT** · 14 Sep 2026. Pre-flight check for Track D step D1 (taxonomy into Supabase), run
> before writing any SQL. Finds two blockers in `specs/TAXONOMY-MIGRATION-SPEC.md` that the
> decision record does not name. Ground truth lives in `state/CURRENT.md`.

# D1 pre-flight — taxonomy into Supabase

**Result: do not execute the spec as written.** Two decisions are open, and D3 (seed marking) is
not one of them. D3 does not block D1.

---

## Blocker 1 — the spec targets the wrong project

| | Project |
|---|---|
| Spec §0 target | `grufbwlmxhzpocryrhho` |
| The app's database | `gsqeymhtyzepwcnorjun` (`supabase/config.toml`, Lovable Cloud) |

`CURRENT.md` already records these as different projects. Seeding taxonomy into the spec's target:

- recreates the two-database split that `DECISION-backend-direction-2026-09-14` §2 rejects;
- cannot satisfy the spec itself — §5's GIN indexes and §6's validation trigger are on `profiles`,
  which exists only in the app's project.

**Recommendation:** the app's project. A migration committed to `supabase/migrations/` lands there.

---

## Blocker 2 — the Lovable app never adopted this taxonomy

The Firestore taxonomy is the vocabulary of the retired Kotlin app and the admin panel's Master Data
(`admin/docs/architecture/DATABASE-MAP.md`). The Lovable app has its own lists in
`src/data/onboarding.ts:141-189`, and **every value in the live database comes from those.**

Live read, 14 profiles (11 seeded, 3 real):

| Field | App list | Stored today | Taxonomy |
|---|---|---|---|
| genres | 14 | `Indie` 8 · `Jazz` 5 · `Bollywood` 5 · `Electronic` 5 · `Fusion` 3 · `Pop` 3 · `Rock` 3 · `Classical` 2 · `Regional` 1 · `Hip-Hop` 1 | 17, finer: `indie_pop`, `bollywood_filmi`, `hindustani_classical` / `carnatic_classical` … |
| instruments | 14 | `Keyboard` 4 · `Piano` 4 · `Guitar` 2 · `Bass` 2 · `Drums` 1 | 21, finer: `acoustic_guitar` / `electric_guitar`, `drums_percussion` … |
| languages | 13, incl. `Bhojpuri`, `Other` | bare names: `English` 11 · `Hindi` 9 · `Marathi` 3 … | 19, no Bhojpuri; adds Odia, Urdu, Sanskrit, 5 foreign |
| roles | 13 kebab keys | `musician`, `sound-engineer` … plus business **labels**: `A&R`, `Independent label`, `Rehearsal space`, `Venue`, `Production house` … | `person_types`, 18 snake keys: `sound_engineer`, `rapper`, `music_label`, `studio` … |

Zero exact genre matches. Importing the taxonomy "straight across" and adding the §6 trigger would
reject every onboarding save.

### Consequences either way

- **Neither direction is lossless.** The Firestore seeds store taxonomy labels (`"Jazz / Blues"`,
  `"Rock / Metal"`, `"Hindi (हिंदी)"` — `audits/FIRESTORE-SCHEMA-WITH-CONTENT.md`), so importing
  them under option A means mapping taxonomy → app lists. Most options have a home; these do not:

  | Field | No home in the app list (would become `other` or be dropped) | Needs a call |
  |---|---|---|
  | genres (5 of 17) | Ghazal · Lofi / Chill Beats · Acoustic / Unplugged · R&B / Soul · Reggae / Dub | Qawwali → Sufi? · Indie Pop → Indie, Pop, or both? · Blues half of Jazz / Blues lost |
  | instruments (7 of 21) | Trumpet · Sarod · Shehnai · Mridangam · Veena · Tanpura · Cello | Dholak → Percussion? |
  | languages (8 of 19) | Odia · Urdu · Sanskrit · Spanish · French · German · Japanese · Korean | — |

  Under option B the loss runs the other way: a real user's `Classical` cannot be split into
  Hindustani or Carnatic without asking them, and `Fusion`, `Regional`, `Bhojpuri` have no
  taxonomy home.
- **Bhojpuri** has no ISO 639-1 code (639-3 `bho`). The spec's "ISO 639-1" rule needs a fallback —
  BCP 47 (`bho`) is the standard answer and keeps two-letter codes where they exist.
- **Business roles** (`A&R`, `Publishing`, `Rehearsal space`, `Live music bookings` …) are in no
  taxonomy category. They need a category of their own whichever vocabulary wins.
- **The admin panel.** It stays on Firestore (decision record §5) and its Master Data page edits the
  Firestore vocabulary. If Supabase adopts the app's lists, that page edits values nothing reads —
  the "two live copies" problem in spec §8, sharpened.

### The choice (product call — Vedanshu's)

| Option | Data work | UI work | Seed import (D3) |
|---|---|---|---|
| **A. App lists become the taxonomy** | slugify 14 rows, mechanical | none — save path stores slugs, labels unchanged | lossy on the table above; ~a dozen mapping calls |
| **B. Adopt the Firestore taxonomy** | remap 14 rows, 3 real users need guesses or re-asking | onboarding pickers change → Lovable prompt | seeds import directly |
| **C. Union — app lists plus the missing taxonomy options** | slugify 14 rows | pickers grow by ~20 options → Lovable prompt | seeds import with no loss |

Claude Code recommends **A for D1**: the loss in A lands on mock seed data, the loss in B lands on
real users. **C** is the right answer if the missing genres, instruments and languages are wanted in
the product — that is a content decision, and it can follow A without a second data migration
(adding options never invalidates stored slugs).

---

## D3 note — the seed counts disagree

`DECISION-backend-direction-2026-09-14` says `users` 92 and `studios` 43, and that the REST page
capped at 83. `audits/FIRESTORE-SCHEMA-WITH-CONTENT.md`, same date, says `users` 83 and `studios` 11,
"nothing has changed since" 10 Sep. The `profileType` split in the decision record (62 + 19 + 1 + 1)
sums to **83**, not 92 — so 9 user docs are uncounted by type, and the studios figure is a genuine
conflict, not a paging artefact. Re-count with pagination before D3 sizes anything.

## D3 note — belongs in D2

The two paths in the decision record are not exclusive: `is_seed` is one boolean, and importing
BUSINESS + VENUE first works with it. Add the flag either way; mark the existing 11 rows.

`profiles.id` references `auth.users(id)`, so every imported seed needs a sign-in-less auth user
(`20260913090949_seed_linked_profiles.sql` made 11). At 103 that is a lot of fake identities, and
studios, labels and venues are exactly the rows a real owner may later claim. That argues for a
`profiles` primary key of its own with a nullable `owner_uid` — a D2 design point, cheapest now.

---

## Unblocks when answered

1. Target project — the app's (`gsqeymhtyzepwcnorjun`)?
2. Vocabulary — A, B or C?

Then: D1 migration, backfill the 14 rows to slugs, save path writes slugs, validation trigger, spec
§7 acceptance checks run as queries.
