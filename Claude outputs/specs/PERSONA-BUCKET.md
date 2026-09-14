---
doc: PERSONA-BUCKET
version: 0.1
status: DRAFT — for Vedanshu's review
updated: 2026-09-14
verified_against: musiclinkd-9e6ca875 supabase/migrations/20260914120000_taxonomy_persona_bucket.sql (generated from the same data)
folder: specs
originally: new
---

> **DRAFT** · 14 Sep 2026. The options a profile can pick, given one primary person type, up to two
> secondary person types, and its languages. Generated together with the migration, so the tables
> below and the SQL cannot disagree. Rows marked *added* were not in either source list.

# Persona bucket

**Instruments** = union of the instruments allowed for the primary and each secondary person type.
**Genres** = every *universal* genre, plus each *regional* genre linked to any selected language.

Call: `select * from persona_bucket('singer', array['lyricist','composer'], array['hi','en']);`

## Languages (12)

| Slug | Name |
|---|---|
| `hi` | हिंदी |
| `pa` | ਪੰਜਾਬੀ |
| `ta` | தமிழ் |
| `te` | తెలుగు |
| `bn` | বাংলা |
| `mr` | मराठी |
| `gu` | ગુજરાતી |
| `kn` | ಕನ್ನಡ |
| `ml` | മലയാളം |
| `bho` | भोजपुरी |
| `as` | অসমীয়া |
| `en` | English |

## Business types (9)

| Slug | Label |
|---|---|
| `studio` | Studio |
| `label` | Label |
| `agency` | Agency |
| `venue` | Venue |
| `dealer` | Dealer |
| `rental_provider` | Rental Provider |
| `music_school` | Music School |
| `production_house` | Production House |
| `other` | Other |

## Instruments (34)

| Family | Instruments |
|---|---|
| Guitar | Acoustic Guitar · Electric Guitar · Bass Guitar · Ukulele *(added)* · Mandolin *(added)* |
| Keys | Keyboard / Synthesizer · Piano · Harmonium |
| Percussion | Drums / Percussion · Tabla · Dholak · Mridangam · Ghatam *(added)* · Kanjira *(added)* · Cajón *(added)* |
| Indian String | Sitar · Sarod · Santoor · Veena · Tanpura |
| Bowed | Violin · Viola *(added)* · Cello · Double Bass *(added)* · Sarangi *(added)* |
| Wind | Flute (Bansuri) · Saxophone · Trumpet · Clarinet *(added)* · Shehnai · Nadaswaram *(added)* · Harmonica *(added)* |
| Electronic | DJ Controller / Turntables *(added)* · Drum Machine / Sampler *(added)* |

## Person types (33) → instruments

| Group | Slug | Label | Source | Instruments | Basis |
|---|---|---|---|---|---|
| Vocal | `singer` | Singer / Vocalist | both | **Guitar** · **Keys** | Vedanshu, 14 Sep |
| Vocal | `rapper` | Rapper / MC | firestore | — | none |
| Vocal | `folk_vocalist` | Folk Vocalist | firestore | Acoustic Guitar · Harmonium · Dholak | proposed |
| Vocal | `voice_artist` | Voice Artist | app | — | app: not asked |
| Instrumentalist | `musician` | Musician | app | all | app: asked |
| Instrumentalist | `guitarist` | Guitarist | firestore | **Guitar** | label |
| Instrumentalist | `bassist` | Bassist | added | Bass Guitar · Double Bass | label |
| Instrumentalist | `keyboardist` | Keyboardist / Pianist | firestore | **Keys** | label |
| Instrumentalist | `drummer` | Drummer / Percussionist | firestore | **Percussion** · Drum Machine / Sampler | label |
| Instrumentalist | `sitarist` | Sitarist / Flutist | firestore | **Indian String** · Flute (Bansuri) | label |
| Instrumentalist | `violinist` | Violinist / String Player | added | **Bowed** | label |
| Instrumentalist | `wind_player` | Flautist / Wind Player | added | **Wind** | label |
| Creator | `composer` | Composer | both | all | app: asked |
| Creator | `lyricist` | Lyricist | both | — | app: not asked |
| Creator | `arranger` | Vocal Arranger | firestore | **Keys** | proposed |
| Creator | `music_director` | Music Director | app | — | app: not asked |
| Creator | `producer` | Music Producer | both | all | app: asked |
| Creator | `beatmaker` | Beatmaker | added | Keyboard / Synthesizer · Drum Machine / Sampler | proposed |
| Technical | `sound_engineer` | Sound Engineer | both | — | app: not asked |
| Technical | `mixing_engineer` | Mixing Engineer | added | — | none |
| Technical | `mastering_engineer` | Mastering Engineer | added | — | none |
| Technical | `sound_designer` | Sound Designer | firestore | Keyboard / Synthesizer · Drum Machine / Sampler | proposed |
| Media | `dj` | DJ | app | **Electronic** | proposed (no acoustic instruments) |
| Media | `rj` | RJ | app | — | app: not asked |
| Media | `podcaster` | Podcaster | app | — | app: not asked |
| Education | `music_teacher` | Music Teacher | app | all | app: asked |
| Business | `artist_manager` | Artist Manager | firestore | — | business |
| Business | `a_and_r` | A&R | added | — | business |
| Business | `music_label` | Music Label | firestore | — | business |
| Business | `music_publisher` | Music Publisher | firestore | — | business |
| Business | `studio` | Recording Studio | firestore | — | business |
| Business | `event_organizer` | Event Organizer | firestore | — | business |
| Other | `other` | Other | app | — | app: not asked |

## Genres (32) → languages

| Slug | Label | Scope | Languages |
|---|---|---|---|
| `indie_pop` | Indie Pop | universal | all |
| `rock_metal` | Rock / Metal | universal | all |
| `hiphop_trap` | Hip Hop / Trap | universal | all |
| `lofi_chill` | Lofi / Chill Beats | universal | all |
| `edm_synthwave` | EDM / Synthwave | universal | all |
| `acoustic_unplugged` | Acoustic / Unplugged | universal | all |
| `rnb_soul` | R&B / Soul | universal | all |
| `jazz_blues` | Jazz / Blues | universal | all |
| `reggae_dub` | Reggae / Dub | universal | all |
| `fusion` | Fusion *(added)* | universal | all |
| `bollywood_filmi` | Bollywood / Filmi | regional | हिंदी |
| `hindustani_classical` | Hindustani Classical | regional | हिंदी · ਪੰਜਾਬੀ · বাংলা · मराठी · ગુજરાતી · অসমীয়া · भोजपुरी |
| `carnatic_classical` | Carnatic Classical | regional | தமிழ் · తెలుగు · ಕನ್ನಡ · മലയാളം |
| `sufi_devotional` | Sufi Devotional | regional | हिंदी · ਪੰਜਾਬੀ |
| `ghazal` | Ghazal | regional | हिंदी |
| `qawwali` | Qawwali | regional | हिंदी · ਪੰਜਾਬੀ |
| `bhajan_kirtan` | Bhajan / Kirtan *(added)* | regional | all Indian |
| `punjabi_folk_bhangra` | Punjabi Folk / Bhangra | regional | ਪੰਜਾਬੀ |
| `punjabi_pop` | Punjabi Pop *(added)* | regional | ਪੰਜਾਬੀ |
| `rajasthani_folk` | Rajasthani Folk | regional | हिंदी |
| `bhojpuri_folk` | Bhojpuri Folk *(added)* | regional | भोजपुरी |
| `kollywood` | Tamil Film (Kollywood) *(added)* | regional | தமிழ் |
| `gaana` | Gaana *(added)* | regional | தமிழ் |
| `tollywood` | Telugu Film (Tollywood) *(added)* | regional | తెలుగు |
| `sandalwood` | Kannada Film (Sandalwood) *(added)* | regional | ಕನ್ನಡ |
| `mollywood` | Malayalam Film (Mollywood) *(added)* | regional | മലയാളം |
| `rabindra_sangeet` | Rabindra Sangeet *(added)* | regional | বাংলা |
| `baul` | Baul *(added)* | regional | বাংলা |
| `lavani` | Lavani *(added)* | regional | मराठी |
| `natya_sangeet` | Natya Sangeet *(added)* | regional | मराठी |
| `garba_dandiya` | Garba / Dandiya *(added)* | regional | ગુજરાતી |
| `bihu` | Bihu *(added)* | regional | অসমীয়া |

## Worked examples

**singer + lyricist, composer · languages हिंदी · English**

- Instruments (34): all
- Genres (17): 10 universal + Bollywood / Filmi, Hindustani Classical, Sufi Devotional, Ghazal, Qawwali, Bhajan / Kirtan, Rajasthani Folk

**singer + lyricist, dj · languages தமிழ்**

- Instruments (10): Acoustic Guitar, Electric Guitar, Bass Guitar, Ukulele, Mandolin, Keyboard / Synthesizer, Piano, Harmonium, DJ Controller / Turntables, Drum Machine / Sampler
- Genres (14): 10 universal + Carnatic Classical, Bhajan / Kirtan, Tamil Film (Kollywood), Gaana

**drummer + folk_vocalist · languages ਪੰਜਾਬੀ · भोजपुरी**

- Instruments (10): Acoustic Guitar, Harmonium, Drums / Percussion, Tabla, Dholak, Mridangam, Ghatam, Kanjira, Cajón, Drum Machine / Sampler
- Genres (17): 10 universal + Hindustani Classical, Sufi Devotional, Qawwali, Bhajan / Kirtan, Punjabi Folk / Bhangra, Punjabi Pop, Bhojpuri Folk

## Not decided here

- Which person types, instruments and genres ship — every *added* row and every *proposed* basis.
- `profiles.roles`, `.instruments`, `.genres`, `.languages` still store the app's English strings; this
  data is not wired to onboarding or to `profiles` yet.
- Firestore `person_types` kept its combined label `Sitarist / Flutist`; `wind_player` now also covers flute.
