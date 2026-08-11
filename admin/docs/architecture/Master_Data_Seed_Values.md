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
