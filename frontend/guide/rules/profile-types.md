# Profile types

Use shared modules — identity, hero, audio, projects, credits, gallery, services, equipment, availability, reviews, contact, and business information — to compose profiles. Do not create a wholly separate layout for every role.



> **Reconciled 2026-08-31.** Rendered into `/guide` by
> `scripts/build-guide.mjs`, so this file is the source and cannot drift
> from the catalogue. Its interactive reference used to be
> `styleguide.html`, which now 301-redirects to `/guide`.

> **Still unbuilt in the design layer.** `role_profiles` is a live Firestore
> collection with an admin route, and none of the four families or their
> module lists appear in `/guide`. This file is currently the only place the
> composition rules exist — see the Gaps ledger, "Live data the design layer
> cannot see".

## Primary template families

| Family | Roles | Key modules |
|---|---|---|
| Talent | Singer, Instrumentalist, Producer/Engineer, DJ/Performer, Composer, Lyricist | Hero + photo capture, verification badge, nav tabs (About, Projects, Credits, Reviews), audio waveform, Connect/Hire CTA |
| Band | Ensembles, groups, duos | Banner + logo, lineup carousel (member cards), technical rider & stage plot, media reel, Booking CTA |
| Business | Recording Studio, Record Label, Music School, Equipment Dealer | Identity header, equipment checklist, room photos, availability calendar, reviews, Booking CTA (studio) / Course catalog (school) / Artist grid (label) / Product catalog (dealer) |
| Venue | Concert hall, club, amphitheater, festival | Hero imagery + address, capacity & features ledger, event calendar, Booking CTA for tour managers |

## Category-specific modules (Talent)

| Role variant | Modules |
|---|---|
| Singer / Vocalist | Vocal register, performance languages, microphone specs |
| Instrumentalist | Core instrument registry, touring setup, session availability |
| Producer / Engineer | DAW stack, outboard gear ledger, A/B mixing deck |
| DJ / Performer | Deck setup, residency timeline, festival logs |
| Composer | Filmography, original scores carousel |
| Lyricist | Signature lyric cards, IPRS verification, writing languages |

## Rules

- Compose profiles from shared modules — do not create separate layouts per role.
- Use `tokens/tokens.json` for all spacing, radius, color, and typography values.
- Verification badges follow the approved component spec (see `components/README.md`).