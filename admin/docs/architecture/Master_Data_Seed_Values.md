# Master Data Seed Values

Exact label/value pairs to enter under **Master Data** in the admin panel
for each category. `label` is what admins/users see; `value` is what
actually gets stored on `users/{uid}` — see `AI_Studio_Sync_Prompts.md` for
why these specific `value`s were chosen (matching what the app already
writes, so no app-side reformatting is needed once it's reading from
Firestore instead of its own hardcoded lists).

## Category: `genres` (already seeded — repeated here for completeness)

`value` = the exact display string (not slugified), matching what the app
already stores in `selectedGenres` today.

| label | value |
|---|---|
| Bollywood / Filmi | Bollywood / Filmi |
| Indie Pop | Indie Pop |
| Hindustani Classical | Hindustani Classical |
| Carnatic Classical | Carnatic Classical |
| Punjabi Folk / Bhangra | Punjabi Folk / Bhangra |
| Rajasthani Folk | Rajasthani Folk |
| Sufi Devotional | Sufi Devotional |
| Ghazal | Ghazal |
| Qawwali | Qawwali |
| Rock / Metal | Rock / Metal |
| Hip Hop / Trap | Hip Hop / Trap |
| Lofi / Chill Beats | Lofi / Chill Beats |
| EDM / Synthwave | EDM / Synthwave |
| Acoustic / Unplugged | Acoustic / Unplugged |
| R&B / Soul | R&B / Soul |
| Jazz / Blues | Jazz / Blues |
| Reggae / Dub | Reggae / Dub |

## Category: `person_types`

`value` = the app's existing `RoleItem` key slug (`OnboardingData.kt`'s
`availableRoles`) — **not** slugified from the label, use these exact
strings so `primaryRole`/`secondaryRole` match once the app writes slugs
instead of display text (Prompt B).

| label | value |
|---|---|
| Singer / Vocalist | singer |
| Rapper / MC | rapper |
| Folk Vocalist | folk_vocalist |
| Guitarist | guitarist |
| Keyboardist / Pianist | keyboardist |
| Drummer / Percussionist | drummer |
| Sitarist / Flutist | sitarist |
| Composer | composer |
| Lyricist | lyricist |
| Vocal Arranger | arranger |
| Music Producer | producer |
| Sound Engineer | sound_engineer |
| Sound Designer | sound_designer |
| Artist Manager | artist_manager |
| Music Label | music_label |
| Music Publisher | music_publisher |
| Recording Studio | studio |
| Event Organizer | event_organizer |

## Category: `languages`

`value` = the exact display string, matching what the app already stores.

| label | value |
|---|---|
| English | English |
| Hindi (हिंदी) | Hindi (हिंदी) |
| Punjabi (ਪੰਜਾਬੀ) | Punjabi (ਪੰਜਾਬੀ) |
| Tamil (தமிழ்) | Tamil (தமிழ்) |
| Telugu (తెలుగు) | Telugu (తెలుగు) |
| Marathi (मराठी) | Marathi (मराठी) |
| Gujarati (ગુજરાતી) | Gujarati (ગુજરાતી) |
| Kannada (ಕನ್ನಡ) | Kannada (ಕನ್ನಡ) |
| Malayalam (മലയാളം) | Malayalam (മലയാളം) |
| Bengali (বাংলা) | Bengali (বাংলা) |
| Assamese (অসমীয়া) | Assamese (অসমীয়া) |
| Odia (ଓଡ଼ିଆ) | Odia (ଓଡ଼ିଆ) |
| Urdu (اردو) | Urdu (اردو) |
| Sanskrit (संस्कृत) | Sanskrit (संस्कृत) |
| Spanish (Español) | Spanish (Español) |
| French (Français) | French (Français) |
| German (Deutsch) | German (Deutsch) |
| Japanese (日本語) | Japanese (日本語) |
| Korean (한국어) | Korean (한국어) |

## Category: `instruments`

`value` = the exact display string (emoji dropped — not part of the
underlying data, purely a UI decoration in the app).

| label | value |
|---|---|
| Acoustic Guitar | Acoustic Guitar |
| Electric Guitar | Electric Guitar |
| Bass Guitar | Bass Guitar |
| Keyboard / Synthesizer | Keyboard / Synthesizer |
| Piano | Piano |
| Drums / Percussion | Drums / Percussion |
| Tabla | Tabla |
| Sitar | Sitar |
| Flute (Bansuri) | Flute (Bansuri) |
| Harmonium | Harmonium |
| Dholak | Dholak |
| Violin | Violin |
| Saxophone | Saxophone |
| Trumpet | Trumpet |
| Sarod | Sarod |
| Shehnai | Shehnai |
| Mridangam | Mridangam |
| Santoor | Santoor |
| Veena | Veena |
| Tanpura | Tanpura |
| Cello | Cello |

4 new additions (2026-08-11): Santoor, Veena, Tanpura, Cello — the rest of
the "Indian Instruments"/"Western Instruments" list the user supplied this
round were already present under slightly different labels (e.g. "Flute
(Bansuri)" already covers "Bansuri", "Drums / Percussion" already covers
"Drums"). Reused this single category for both a musician's "what I play"
and a dealer's "what I sell" rather than creating a duplicate — same
instrument names either way.

## Category: `persona_badges` — proposed starter set, not grounded in app data

Unlike the four categories above, **nothing in the app or schema currently
defines this** — there's no existing hardcoded list to source from. This is
a proposed starting set for self-selected profile tags (per your answer
that this is self-service, not admin-granted); edit or replace freely via
the Master Data UI, this is a starting point, not a finding.

| label | value |
|---|---|
| Available for Collab | Available for Collab |
| Session Ready | Session Ready |
| Touring | Touring |
| Home Studio Owner | Home Studio Owner |
| Full-Time | Full-Time |
| Weekend Warrior | Weekend Warrior |
| Open to Remote Work | Open to Remote Work |

## Category: `project_types`

`value` = a stable slug, matching `availableProjectTypes` in `PortfolioData.kt`.

| label | value |
|---|---|
| Song | song |
| Album | album |
| EP | ep |
| Podcast | podcast |
| DJ Mix | dj_mix |
| Film Score | film_score |
| Commercial Jingle | commercial_jingle |
| Live Performance | live_performance |
| Music Video | music_video |
| Custom Project | custom_project |

## Category: `project_roles`

`value` = the exact display string, matching `availableProjectRoles` in
`PortfolioData.kt`. Deliberately separate from `person_types` — this is the
role a person plays on *one project*, not their platform-wide primary role.

| label | value |
|---|---|
| Singer | Singer |
| Composer | Composer |
| Music Director | Music Director |
| Producer | Producer |
| Lyricist | Lyricist |
| Session Musician | Session Musician |
| DJ | DJ |
| Mix Engineer | Mix Engineer |
| Mastering Engineer | Mastering Engineer |
| Sound Designer | Sound Designer |
| Band Member | Band Member |

## Category: `booking_statuses`

`value` = a stable slug. Feeds `StudioBookingDoc.bookingStatus`.

| label | value |
|---|---|
| Requested | requested |
| Confirmed | confirmed |
| Payment Pending | payment_pending |
| Cancelled | cancelled |
| Completed | completed |

## Category: `studio_amenities`

`value` = the exact display string, matching the real hardcoded amenity
tags already used in `StudioBookingScreens.kt`'s mock studio data. Feeds
the already-deployed `StudioDoc.amenities: string[]`.

| label | value |
|---|---|
| Dolby Atmos | Dolby Atmos |
| Vocal Booth | Vocal Booth |
| Live Room | Live Room |
| Valet Parking | Valet Parking |
| Resident Engineers | Resident Engineers |
| Private Lounge | Private Lounge |
| 24x7 Access | 24x7 Access |
| Rehearsal Room | Rehearsal Room |
| Chill Lounge | Chill Lounge |
| Drums Included | Drums Included |
| Parking Space | Parking Space |
| Instruments Support | Instruments Support |
| Acoustics Optimized | Acoustics Optimized |
| Fiber Internet | Fiber Internet |
| Stage Monitors | Stage Monitors |
| Bar & Diner Access | Bar & Diner Access |

## Category: `collaboration_types`

`value` = a stable slug. Feeds `ProjectDoc.collaborationType`.

| label | value |
|---|---|
| Work for Hire | work_for_hire |
| Royalties Split | royalties_split |
| Co-Write | co_write |
| Session Payment | session_payment |

## Category: `trust_badges` — proposed starter set, admin-granted

Distinct from `persona_badges` (self-selected). Feeds `UserDoc.trustBadges`,
which security rules block a user from ever setting on their own document —
these are assigned by an admin via the Users editor sheet.

| label | value |
|---|---|
| Verified Artist | Verified Artist |
| Top Host | Top Host |
| Rising Talent | Rising Talent |
| Studio Partner | Studio Partner |

## New categories — from the profile-type and studio-booking flow gap analysis (2026-08-11)

These 5 don't yet exist in Firestore. Values are proposed (industry-standard
vocabularies, not matched against an existing app hardcoded list like the
categories above) — confirm before seeding if a stricter source-of-truth
list exists on the app side.

### Category: `vocal_range` — Singer module (Talent profile)

| label | value |
|---|---|
| Soprano | soprano |
| Mezzo-Soprano | mezzo_soprano |
| Alto | alto |
| Tenor | tenor |
| Baritone | baritone |
| Bass | bass |

### Category: `daw_tools` — Engineer module (Talent profile)

| label | value |
|---|---|
| Logic Pro | logic_pro |
| Pro Tools | pro_tools |
| Ableton Live | ableton_live |
| FL Studio | fl_studio |
| Cubase | cubase |
| Studio One | studio_one |
| Reaper | reaper |

### Category: `session_types` — Studio Booking "Configure Session" screen

Feeds `StudioBookingDoc.sessionType`. Values match the exact chip labels
from the booking flow.

| label | value |
|---|---|
| Recording | recording |
| Mixing | mixing |
| Mastering | mastering |
| Podcast | podcast |
| Jam Session | jam_session |

### Category: `session_duration_tiers` — Studio Booking "Configure Session" screen

Feeds `StudioBookingDoc.durationTier`.

| label | value |
|---|---|
| Hourly | hourly |
| Half Day | half_day |
| Full Day | full_day |

### Category: `studio_services` — Studio Discovery filter chips

Feeds `StudioDoc.services` — what a studio offers, distinct from
`studio_amenities` (what it physically has).

| label | value |
|---|---|
| Recording | recording |
| Mixing | mixing |
| Mastering | mastering |

### `studio_addons` — still pending, not proposed here

Referenced by `StudioBookingDoc.addons` (Food/Parking/Extra Hours from the
"Configure Session" screen) — already flagged in Prompt F §7 as needing
AI Studio's real value list rather than a guessed one. Not seeded yet.

## New categories — for `role_profiles` (Module B role-specific fields, 2026-08-11)

Proposed values (not matched against an app-side hardcoded list — confirm
before seeding).

### Category: `singing_styles` — Singer role profile

| label | value |
|---|---|
| Playback | playback |
| Classical | classical |
| Ghazal | ghazal |
| Sufi | sufi |
| Indie | indie |
| Pop | pop |
| Rock | rock |
| Rap | rap |
| Bhajan | bhajan |

### Category: `lyricist_specializations` — Lyricist role profile

| label | value |
|---|---|
| Romantic | romantic |
| Commercial | commercial |
| Devotional | devotional |
| Rap | rap |
| Film Songs | film_songs |
| Independent Music | independent_music |

### Category: `director_specializations` — Music Director role profile

| label | value |
|---|---|
| Film Music | film_music |
| OTT | ott |
| Advertising | advertising |
| Independent | independent |

### Category: `composition_styles` — Composer role profile

| label | value |
|---|---|
| Orchestral | orchestral |
| Electronic | electronic |
| Background Score | background_score |
| Commercial Jingles | commercial_jingles |
| Film Score | film_score |

### Category: `engineer_specialties` — Sound Engineer role profile

| label | value |
|---|---|
| Recording | recording |
| Mixing | mixing |
| Mastering | mastering |
| Live Sound | live_sound |

### Category: `dj_types` — DJ role profile

| label | value |
|---|---|
| Club | club |
| Wedding | wedding |
| Radio | radio |
| Festival | festival |
| Bollywood | bollywood |

### Category: `verification_id_type` — Verification Queue

Feeds `VerificationRequestDoc.verificationIdType` (field added this round —
was missing despite this category being cataloged earlier).

| label | value |
|---|---|
| Government ID (Aadhaar) | govt_id_aadhaar |
| Passport | passport |
| Driving License | driving_license |
| Company PAN / GSTIN | company_pan_gstin |

### Category: `availability_status` — Talent availability

Feeds `UserDoc.sessionAvailability`. These 5 states were already used as
literal strings in the Flows registry's "Services, Availability" screen
seed this session — formalizing them here closes that inconsistency.

| label | value |
|---|---|
| Available Now | available_now |
| Open for Gigs | open_for_gigs |
| Booked Soon | booked_soon |
| On Tour | on_tour |
| Unavailable | unavailable |

**Reconciliation note (2026-08-11):** an earlier, coarser pasted filter
list ("Available / Busy / Touring / Studio Work" — 4 values) also exists
in this session's history for the same concept. This 5-value list is the
canonical one — it matches the more detailed source (AI Studio's own
MasterVocabularies.AvailabilityStatuses) and is already wired into
`UserDoc.sessionAvailability` and the Flows registry. The 4-value list was
a rougher early mention, not a second real source — don't seed both.

### Category: `podcast_categories` — Podcaster role profile

| label | value |
|---|---|
| Music | music |
| Interview | interview |
| Comedy | comedy |
| Culture | culture |
| Business | business |
| Education | education |

## Specialization / skills categories — confirmation, not new work (2026-08-11)

The user re-supplied the Lyricist/Music Director/Composer/Sound Engineer
specialization and skills lists this round, asking whether they'd been
mapped. Checked against what's already documented above (role_profiles
round, same session) — **already done, values match exactly**:
`lyricist_specializations` (romantic/commercial/devotional/rap/
film_songs/independent_music), `director_specializations` (film_music/ott/
advertising/independent), `composition_styles` (orchestral/electronic/
background_score/commercial_jingles/film_score), `engineer_specialties`
(recording/mixing/mastering/live_sound), `daw_tools` (already a superset —
includes Pro Tools/Logic/Cubase/Ableton/FL Studio plus Studio One/Reaper).
No new category needed here.

## New categories — Marketplace/Budget reconciliation (2026-08-11)

### Category: `marketplace_categories`

Feeds the corrected `MarketplaceItemDoc.category` (was a fixed "GEAR"|
"STEMS"|"SERVICES" union that never matched the app's real values —
reconciled to this taxonomy-backed set, matching AI Studio's own
"reconciled" proposal since Marketplace has zero real Firestore wiring to
conflict with yet).

| label | value |
|---|---|
| Gear Sale | gear_sale |
| Gear Rental | gear_rental |
| Beats Licensing | beats_licensing |
| Stems | stems |
| Mixing & Mastering | mixing_mastering |
| Creative Services | creative_services |

### Category: `equipment_categories`

Feeds `StudioEquipmentItem.category` (Studio Booking's Equipment screen)
and `MarketplaceItemDoc.itemCategory` for gear listings — distinct from
`instruments` (playable instruments) and `studio_amenities` (physical room
features).

| label | value |
|---|---|
| Microphones | microphones |
| Audio Interfaces | audio_interfaces |
| Studio Monitors | studio_monitors |
| Mixers | mixers |
| Preamps | preamps |
| Headphones | headphones |
| Controllers | controllers |

### Category: `budget_type`

Feeds `UserDoc.budgetTier` — a coarse price-tier indicator for browse/
filter display, separate from the exact `hourlyRate` figure.

| label | value |
|---|---|
| ₹ (Budget) | budget |
| ₹₹ (Mid-range) | mid_range |
| ₹₹₹ (Premium) | premium |

## New category — Opportunities/Flow 5 (2026-08-16, proposed, not grounded in app data)

### Category: `opportunity_types`

Feeds `ProjectDoc.opportunityType` — a separate axis from `collaboration_types`
(which is about compensation structure, not what kind of opportunity this
is). Two-level in the source brief (6 groups, each with sub-types); this
schema's taxonomy pattern is deliberately flat (2 Firestore levels only,
per the existing `metadata`-escape-hatch convention used for city
sub-areas — see `TaxonomyOptionDoc.metadata`), so the group is carried in
each option's `metadata.group` instead of a 3rd Firestore level. Phase 2 —
not yet wired into any screen. Drafted in
`docs/04-Wireframes/PRODUCT-SPEC.md` §D.6.

**Naming overlap, deliberate, not an error:** `recording`/`mixing`/`mastering`
below share their exact `value` string with entries already seeded in
`engineer_specialties` and `studio_services` — different axis (what an
opportunity needs vs. what a person/studio provides), kept as separate
categories, but intentionally value-aligned so a future matching feature
can compare them directly without a translation table.

| label | value | metadata.group |
|---|---|---|
| Looking for Singer | looking_for_singer | Collaboration |
| Looking for Musician | looking_for_musician | Collaboration |
| Looking for Producer | looking_for_producer | Collaboration |
| Looking for Composer | looking_for_composer | Collaboration |
| Looking for Lyricist | looking_for_lyricist | Collaboration |
| Recording | recording | Professional Work |
| Mixing | mixing | Professional Work |
| Mastering | mastering | Professional Work |
| Session Work | session_work | Professional Work |
| Sound Engineering | sound_engineering | Professional Work |
| Gigs | gigs | Performance |
| Shows | shows | Performance |
| Events | events | Performance |
| Touring | touring | Performance |
| Film | film | Production |
| Advertising | advertising | Production |
| Albums | albums | Production |
| Digital Content | digital_content | Production |
| Studio Required | studio_required | Studio / Space |
| Rehearsal Required | rehearsal_required | Studio / Space |
| Venue Required | venue_required | Studio / Space |
| Equipment Required | equipment_required | Equipment |
| Rental Required | rental_required | Equipment |
| ₹₹₹ (Premium) | premium |
