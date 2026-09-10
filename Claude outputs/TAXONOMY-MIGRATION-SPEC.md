# MusicLinkd — taxonomy migration spec

**Target:** Supabase project `grufbwlmxhzpocryrhho` (owner: personal ID — Vedanshu's call, settled).
**Source of truth:** `admin/docs/architecture/Master_Data_Seed_Values.md`, as transcribed in
`admin/scripts/seed/data/taxonomy-seed.ts`.
**NOT a source:** `admin/scripts/seed/data/taxonomy.ts` — the seed file explicitly warns it is
*"filler-generation convenience, not a second source of truth."*

**Execution owner:** Claude Code. Claude (Cowork) cannot reach this project — it sits outside
the `wugweb` Supabase org, so the connector does not see it. Everything below is written to be
executed without me in the loop.

---

## 1. Why now

Verified against live Firestore (`musiclinkd-7ae30`) on 10 Sep:

- **83 user documents, all `mock-`. Zero real users.** There is no production data to migrate
  and no backfill to run. Re-seeding is happening anyway.
- The bad keys therefore **never have to be migrated at all**. Define the taxonomy correctly in
  Postgres, seed from it, done.

This is the cheapest this will ever be. It stops being cheap the day someone signs up.

*(Unverified: whether Firebase Auth holds accounts with no matching `users` document. Needs
admin credentials. Confirm before concluding nobody has registered.)*

---

## 2. What is actually broken

Of 29 taxonomy categories, **22 already carry proper slugs** — `person_types` is the model:
`{ label: "Singer / Vocalist", value: "singer" }`.

**7 categories have `value === label` for every option** — the display string is the key:

| Category | Options |
|---|---|
| `genres` | 17 |
| `languages` | 19 |
| `instruments` | 21 |
| `persona_badges` | 7 |
| `project_roles` | 11 |
| `studio_amenities` | 16 |
| `trust_badges` | 4 |

**95 options total.** No mixed categories — the split is clean, which makes this mechanical.

### The proof it fails

Live Firestore already contains values that are not in the taxonomy:

```
"Kannada (ಕನ್ನಡ)"  12x     "Kannada"  3x
"Tamil (தமிழ்)"    11x     "Tamil"     1x
```

Four documents — `mock-band-the-bangalore-blues-project`, `mock-business-nisha-rao-events`,
`mock-business-resonance-acoustic-suites`, `mock-talent-siddharth-swar`. The team's own
generator drifted from the team's own taxonomy, and nothing caught it, because nothing can.
Filtering by the canonical value silently misses those records.

`languages` is the worst shape of the seven: the key carries native script, so the identity
*is* the localisation. Real localisation is structurally impossible while that holds.

---

## 3. Slug rules

- **`languages` → ISO 639-1.** A real standard, stable, and it decouples identity from display.
  All 19 have codes.
- **Everything else → slugified label**: NFKD, strip diacritics, lowercase, `&` → `and`,
  non-alphanumeric → `_`, collapse and trim.
- Overrides applied where slugify produced something unreadable (`r_b_soul` → `rnb_soul`).
  Listed explicitly below so they can be vetoed rather than discovered.

### Two identifier cautions

**`24x7_access` starts with a digit.** Harmless as a data value; breaks the moment anyone uses
slugs as SQL identifiers, TS enum members, or CSS classes. **Recommend `access_24x7`.**

**`or` (Odia) and `as` (Assamese) are SQL keywords.** Harmless as data values in a `text[]`.
Only a hazard if someone generates identifiers from them. Noted, not changed — ISO codes are
worth more than the edge case.

---

## 4. The mapping

### genres (17)

```
bollywood_filmi        | Bollywood / Filmi
indie_pop              | Indie Pop
hindustani_classical   | Hindustani Classical
carnatic_classical     | Carnatic Classical
punjabi_folk_bhangra   | Punjabi Folk / Bhangra
rajasthani_folk        | Rajasthani Folk
sufi_devotional        | Sufi Devotional
ghazal                 | Ghazal
qawwali                | Qawwali
rock_metal             | Rock / Metal
hiphop_trap            | Hip Hop / Trap
lofi_chill             | Lofi / Chill Beats
edm_synthwave          | EDM / Synthwave
acoustic_unplugged     | Acoustic / Unplugged
rnb_soul               | R&B / Soul
jazz_blues             | Jazz / Blues
reggae_dub             | Reggae / Dub
```

Overrides: `rock_metal`, `hiphop_trap`, `lofi_chill`, `edm_synthwave`, `acoustic_unplugged`,
`rnb_soul`, `jazz_blues`, `reggae_dub`, `bollywood_filmi`, `punjabi_folk_bhangra`.

### languages (19) — ISO 639-1

```
en | English            hi | Hindi (हिंदी)         pa | Punjabi (ਪੰਜਾਬੀ)
ta | Tamil (தமிழ்)      te | Telugu (తెలుగు)      mr | Marathi (मराठी)
gu | Gujarati (ગુજરાતી)  kn | Kannada (ಕನ್ನಡ)      ml | Malayalam (മലയാളം)
bn | Bengali (বাংলা)     as | Assamese (অসমীয়া)   or | Odia (ଓଡ଼ିଆ)
ur | Urdu (اردو)        sa | Sanskrit (संस्कृत)    es | Spanish (Español)
fr | French (Français)  de | German (Deutsch)     ja | Japanese (日本語)
ko | Korean (한국어)
```

The parenthetical native script stays in `label` — it is display, and it is good display.
It must never again be part of the key.

### instruments (21)

```
acoustic_guitar   electric_guitar   bass_guitar    keyboard_synthesizer
piano             drums_percussion  tabla          sitar
flute_bansuri     harmonium         dholak         violin
saxophone         trumpet           sarod          shehnai
mridangam         santoor           veena          tanpura
cello
```

Labels are the existing seed labels verbatim — `Keyboard / Synthesizer`, `Flute (Bansuri)`,
`Drums / Percussion`, the rest unchanged.

### persona_badges (7)

```
available_for_collab   | Available for Collab
session_ready          | Session Ready
touring                | Touring
home_studio_owner      | Home Studio Owner
full_time              | Full-Time
weekend_warrior        | Weekend Warrior
open_to_remote_work    | Open to Remote Work
```

### project_roles (11)

```
singer          composer            music_director   producer
lyricist        session_musician    dj               mix_engineer
mastering_engineer   sound_designer      band_member
```

**Check for overlap with `person_types` before seeding.** `singer`, `composer`, `lyricist`,
`producer`, `sound_designer` appear in both categories with the same slug. That may be correct
(a person's role vs. their credited role on a project) or it may be two names for one concept.
Report which; do not merge on your own judgement.

### studio_amenities (16)

```
dolby_atmos        vocal_booth          live_room           valet_parking
resident_engineers private_lounge       access_24x7 *       rehearsal_room
chill_lounge       drums_included       parking_space       instruments_support
acoustics_optimized fiber_internet      stage_monitors      bar_and_diner_access
```

\* renamed from the mechanical `24x7_access` — see §3.

### trust_badges (4)

```
verified_artist  |  top_host  |  rising_talent  |  studio_partner
```

Note: `trust_badges` is admin-granted and **never owner-writable** (`firestore.rules`). Whatever
enforces that in Firestore must have an equivalent in Postgres RLS, or the rule is lost in the move.

---

## 5. Schema

```sql
create table taxonomy_categories (
  key         text primary key,
  label       text not null,
  description text not null default '',
  is_active   boolean not null default true,
  created_at  timestamptz not null default now(),
  updated_at  timestamptz not null default now()
);

create table taxonomy_options (
  category_key text not null references taxonomy_categories(key) on delete cascade,
  slug         text not null,
  label        text not null,
  sort_order   int  not null default 0,
  is_active    boolean not null default true,
  metadata     jsonb,
  created_at   timestamptz not null default now(),
  updated_at   timestamptz not null default now(),
  primary key (category_key, slug)
);

create index on taxonomy_options (category_key, sort_order) where is_active;
```

`metadata jsonb` maps `TaxonomyOptionDoc.metadata?: Record<string, unknown>` directly —
the instrument `isAcoustic` flag, the booking-status `indicatorColorHex`, and whatever
`opportunity_types` uses it for.

### Profile columns

Keep `text[]`, storing **slugs**:

```sql
create index on profiles using gin (genres);
create index on profiles using gin (instruments);
create index on profiles using gin (languages);
```

A join table is not warranted. The dominant read is multi-attribute filtered discovery
(`genres && '{indie_pop}' and languages && '{ta}' and city = …`), which `text[]` + GIN answers
in one index. A join table earns its place only when you need attributes *on the relationship* —
and note `UserDoc.instrumentProficiency?: string[]` currently does exactly that, badly, as
`"Guitar (Expert)"` strings. **That one is a genuine future join table.** Not this pass.

### RLS

`taxonomy_categories` and `taxonomy_options`: **public SELECT, no write from the client.**
Mirrors `firestore.rules` (`allow read: if true; allow write: if isAdmin();`) and matches the
fact that taxonomy is public reference data needing no session.

---

## 6. Referential integrity

Postgres cannot enforce that a `text[]` element exists in `taxonomy_options` — array elements
take no foreign key. Three options, pick one deliberately:

1. **Trigger-based validation** on insert/update of `profiles` — rejects unknown slugs. Real
   integrity, some write cost.
2. **App-level validation** in the server function. Cheap, and exactly what failed in Firestore.
3. **Accept drift**, reconcile with a periodic report.

**Recommend 1.** The entire reason this document exists is that option 2 was the status quo and
it produced `"Kannada"` alongside `"Kannada (ಕನ್ನಡ)"` in a dataset nobody had even shipped yet.

---

## 7. Acceptance

- 29 categories and every option present in `taxonomy_options`, counts matching the seed file.
- No row anywhere has `slug = label` in the seven fixed categories.
- Every value in `profiles.genres`, `.instruments`, `.languages` resolves to a live
  `taxonomy_options.slug`. Zero orphans — assert it with a query, not by eye.
- `"Kannada"` and `"Tamil"` bare forms do not exist anywhere.
- A filtered discovery query across all three arrays uses the GIN indexes (`explain analyze`).
- Public SELECT works unauthenticated; client write is refused.
- `trust_badges` cannot be set by the profile owner.

---

## 8. Still open

- **Firebase Auth accounts without `users` documents** — unchecked, needs admin credentials.
- **`UserDoc.genres` vs `selectedGenres`** — the seed comment says the category *"feeds
  UserDoc.selectedGenres"*, but `UserDoc` declares `genres: string[]`. One is wrong. Confirm
  which the Kotlin app writes before mapping.
- **`project_roles` vs `person_types` overlap** — five identical slugs, see §4.
- **What happens to the Firestore taxonomy** once Postgres holds it. Two live copies is the
  problem this was meant to end. Decide: retire it, or make one a read-only mirror.
