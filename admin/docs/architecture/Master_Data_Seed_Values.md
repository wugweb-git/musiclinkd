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
