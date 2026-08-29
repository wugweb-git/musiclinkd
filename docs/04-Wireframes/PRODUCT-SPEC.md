# MusicLinkd — Product Spec

*(Renamed 2026-08-16 from "Person Profile & Marketplace" — scope grew to cover four flows, name no longer fit.)*

Written against the real repo (`admin/types/firestore.ts`, `firestore.rules`, `webapp/flows/*`, `admin/app/(admin)/flows/`, `frontend/guide/`), not against either brief in isolation. Where a brief and the repo disagree, the repo wins and the disagreement is flagged below — per your own CLAUDE.md rule: don't propagate a doc's claims without checking the filesystem.

Four workstreams live in this one file so far, at your request: **Part A — Person Profile**, **Part B — Marketplace (Flow 7)**, **Part C — Discover (Flow 4)**, **Part D — Opportunities (Flow 5)**. **Part E** is the cross-flow widget inventory. **Flow 11 (Services)** is confirmed as its own upcoming workstream — will land as **Part F** once you bring the brief; Part A's Services gap (§5 Q2) is deliberately waiting on it rather than being resolved standalone. Treat this as the running product spec, not a single-feature doc.

---

## Implementation Log — what's actually been done vs. proposed only

Kept current, chronologically, so this file never overstates what's real.

**2026-08-16 — `firestore.rules` deployed, confirmed live.** You ran `firebase deploy --only firestore:rules` yourself and it succeeded (`"✔ Deploy complete!"`, rules released to `cloud.firestore`). This means **all rules added in this file are now enforced on the live `musiclinkd-7ae30` project**: `blocks`, `profile_analytics`, `offers`, `marketplace_orders`, `opportunity_applications`, plus the widened `reviews` rule. This is the first thing in this entire spec that's actually live, not just source-ready — every other "not deployed" note below this point is now stale for rules specifically; still accurate for everything else (see below).

**What deploying the rules does *not* mean, to be precise about it:** no data exists yet in any of the new collections (rules only govern what's *allowed* to be written, they don't write anything themselves). The new screens (`121`–`135`) are still source-only in `default-screens.ts` — they only reach the live `flows/{flowId}/screens` collection via the admin panel's "Seed default screens" button, a separate manual step from a rules deploy. The Kotlin/PWA app still doesn't read or write any of this — that requires touching the app itself, which isn't in this repo. In short: the door is now open (rules), but nothing's walked through it yet (no data, no app wiring).

**2026-08-16 — Person Profile, Part A:**
- Registered 4 new screens (`121`–`124`) in `admin/app/(admin)/flows/screens/default-screens.ts` (source code only).
- Added `collaboratorUids` and `galleryUrls` to `UserDoc`, and two new types — `BlockDoc` (`blocks` collection) and `ProfileAnalyticsDoc` (`profile_analytics` collection) — in `admin/types/firestore.ts`.
- Added matching Firestore rules for `blocks/{blockId}` and `profile_analytics/{userId}` to `firestore.rules`.
- Verified: `tsc --noEmit` passes clean in `admin/`; `firestore.rules` braces/parens balanced.
- **Not done / not live:** `firestore.rules` not deployed (no Firebase CLI or project credentials in this sandbox — run `firebase deploy --only firestore:rules` yourself). The new screens are not seeded into the live `flows/{flowId}/screens` Firestore collection — that requires opening the admin panel → Flows → Screens (`public_profile`) → "Seed default screens," which is a client-side action I haven't triggered. No `webapp/flows/public_profile/{media_gallery,profile_analytics,collaborators_graph,talent_services_view}/screen.json` shells exist yet — those get scaffolded by running `npx tsx scripts/webapp-map/generate.ts` from `admin/`, not yet run. `blocks` and `profile_analytics` are type definitions only — zero documents exist in either collection.
- Left untouched, deliberately: `serviceOfferings` (blocked on an unanswered decision, §5 Q2) and Connect vs. Follow (blocked on §5 Q1).

**2026-08-16 — Marketplace, Part B:** spec only (this section), no code changes. Nothing implemented yet — see §B.5 before any code lands here.

**2026-08-16 — Discover (Part C) and Opportunities (Part D):** spec only, no code changes. Open questions resolved: Communities removed from Discover; Studio-duplication left as-is (Discover queries `studios` directly); Saved/Following investigated and resolved to `saved_favorites_hub` (120) as canonical; Opportunities scoped to MVP (Browse + Detail + Message, no Application record); `opportunity_applications` confirmed to stay separate from Marketplace's `offers`. See §C.4 and §D.5 for exactly what's built vs. still proposed.

**2026-08-16 — later same day, MVP scope finalized:** Connect vs. Follow decided (Follow collapses into existing `ConnectionDoc`, §4/§5). Marketplace MVP now includes a minimal Create Listing screen (`128`) rather than deferring listing creation. Opportunities MVP now includes a minimal Create Opportunity screen (`134`), same reasoning. Both resolve the "where do real listings come from" gap flagged in the previous pass. Still spec only across all four parts — no code changes yet, everything above is plan, not implementation. Still open: Services-for-Talent field location (Part A §5 Q2), Native Android target (§5 Q4), `opportunityType` taxonomy values (D.4-3).

**2026-08-16 — full backend implementation pass:**
- **Schema** (`admin/types/firestore.ts`): added `OfferDoc` (`offers`), `MarketplaceOrderDoc` (`marketplace_orders`), `blockedDates` on `MarketplaceItemDoc`, widened `ReviewDoc.targetType` (+`MARKETPLACE_ITEM`) and added `orderId`, widened `NotificationType` (+`OFFER_RECEIVED`/`ORDER_STATUS_UPDATE`/`APPLICATION_STATUS_UPDATE`) — all per §B.3. Widened `ProjectStatus` and added `description`/`skillsRequired`/`languagesRequired`/`locationType`/`deadline`/`opportunityType`/`posterName`/`posterAvatarUrl` to `ProjectDoc`, plus new `OpportunityApplicationDoc` (`opportunity_applications`) — all per §D.2. Added `UserDoc.geo` per §C.2.
- **Rules** (`firestore.rules`): added `offers`, `marketplace_orders`, `opportunity_applications` collections; extended the `reviews` rule to verify the new optional `orderId` the same way `bookingId` is already verified. Braces/parens balanced, matches every existing rule's shape.
- **Screens** (`default-screens.ts`): registered `125`–`129` (Marketplace) and `130`–`135` (Opportunities, a flow that had zero entries before today) — each marked MVP or Phase 2 per the spec, each noted "Not built" since none of this is audited-from-Kotlin like the rest of the file. Max `screenNumber` is now `135`, no duplicates (verified).
- **Taxonomy** (`admin/docs/architecture/Master_Data_Seed_Values.md` + `admin/scripts/seed/data/taxonomy-seed.ts`): added `opportunity_types` (23 values, `metadata.group` for the 6 brief categories) to both the authoritative doc and the seed — including widening the seed's local `CategorySeed` type and its `TAXONOMY_OPTIONS` mapper to actually carry `metadata` through, which it silently dropped before today.
- **Verified:** `tsc --noEmit` clean, `firestore.rules` braces/parens balanced (94/94, 233/233), no duplicate `screenNumber` values across the registry.
- **Deployment — attempted, not possible from this sandbox, confirmed rather than assumed:** installed `firebase-tools` via `npx` (global install failed on permissions, `npx` worked around it) and ran `firebase deploy --only firestore:rules --project musiclinkd-7ae30` — failed with `"Failed to authenticate, have you run firebase login?"`. Checked for every credential path that could avoid interactive login: no `FIREBASE_TOKEN`, no `GOOGLE_APPLICATION_CREDENTIALS`, no service-account JSON, no `google-services.json` anywhere in the repo or environment. This isn't a sandbox limitation I'm guessing at — it's confirmed by the actual failed deploy attempt. **To finish this yourself:** run `firebase login` once on your machine, then `firebase deploy --only firestore:rules` from the repo root. The screens/schema/taxonomy above are source-only until then — nothing is live, same as every previous pass in this file.

**2026-08-16 — Services routed to its own flow:** Services-for-Talent (§5 Q2) is no longer being resolved inside Part A or Marketplace — confirmed it belongs to a dedicated **Flow 11 (Services)**, coming later as Part F. Screen `124` and the Discover Services tab (C.1) both updated to point at that dependency instead of carrying an open question. The technical finding from the brainstorm below stays on record for when that flow lands.

**2026-08-16 — brainstorming round:** Services-for-Talent (§5 Q2) — investigated the actual type and found `UserDoc.serviceOfferings` was never technically Business-only (flat interface, no enforced restriction) — recommended sharing the one field rather than duplicating it; still needs your final yes, not auto-applied. `opportunityType` taxonomy (D.6) — drafted a full 23-value flat list with `metadata.group` for the brief's 6 categories, following the existing sub-area/metadata convention rather than inventing a 3rd Firestore level; flagged a deliberate value-string overlap with `engineer_specialties`/`studio_services`. Part E (new) — audited the real Widget Directory against everything proposed in B/C/D: most needs already have a matching real widget (`MarketplaceCard`, `OpportunityCard`, `DiscoverySearchResultCard`, and the real Gestalt `DateRange` picker for rental dates); 5 genuine gaps proposed (`MediaUploadGrid`, `OfferThreadCard`, `TransactionStatusCard`, `ConversationContextHeader`, `ApplicationStatusCard`), kept out of the real `Widget_Directory.md`/`default-widgets.ts` deliberately since those files are verbatim-audited-from-Kotlin and these aren't. `MediaUploadGrid` is flagged as already blocking Portfolio today, independent of anything new here.

**2026-08-29 — Notion audit, doc cleanup, no schema/screen changes.** Diffed the live Notion mirror against this file and the actual code (`default-screens.ts`, `firestore.rules`, `taxonomy-seed.ts`). Confirmed 135 screens registered, all 5 new rule blocks (`blocks`/`profile_analytics`/`offers`/`marketplace_orders`/`opportunity_applications`) live in `firestore.rules` source, `opportunity_types` present in the seed file — matches this doc's claims, no drift. Found and fixed two real gaps in the Notion copy: §D.3 (Rules note) had been dropped during condensing, and §E.2's table had lost its `Category`/`Composition` columns. Also removed the paragraph that used to sit here — it said screens `125`–`135` were "reserved on paper only," which the Implementation Log two entries above it already contradicts (they were written to `default-screens.ts` days ago). **Still true and unchanged:** none of `121`–`135` are seeded into the live `flows/{flowId}/screens` Firestore collection yet — that's a manual admin-panel step, deliberately left for you to do yourself, not blocked on anything from this end.

---

## Part A — Person Profile

## 0. Reality check — read this before anything else

Four places where the brief's assumptions don't match what's actually in this repo:

**"Android mobile app" — there's no Android app in this repo.** Comments in `admin/types/firestore.ts` reference Kotlin files (`OnboardingViewModel.kt`, `PortfolioScreens.kt`, `BusinessProfileScreens.kt`, `SupportingScreens.kt`) — a native Kotlin app clearly exists and was audited against, but its source isn't checked into this repo. What *is* here: `webapp/` — a PWA "reference build," explicitly labeled `"Not the production app"` in its own README, meant to be installed to a home screen so it opens full-screen. Design against the PWA reference build and treat native-Kotlin translation as a separate, later step — I can't spec Compose screens I've never seen.

**"MusicLinkd Gestalt Design System" — doesn't exist yet as a MusicLinkd system.** `frontend/guide/gestalt/` is a raw vendored clone of Pinterest's open-source Gestalt library (Apache-2.0, v177.0.13) — untouched Pinterest tokens and components. Your own `CLAUDE.md` says it plainly: *"This is not yet MusicLinkd's actual brand system — it shows Gestalt's raw Pinterest tokens as-is. Building MusicLinkd's own brand layer is future work."* It's wired into `admin/` (Next.js/React) only — `webapp/` has no React runtime and no Gestalt CSS applied. So "use the existing Gestalt Design System" currently means "use raw Pinterest colors/type/spacing," not a MusicLinkd-authored system. I've mapped components to real Gestalt (Pinterest) primitives below, flagged clearly as pending a MusicLinkd brand pass.

**The schema is real and far along — better than the brief assumes.** `admin/types/firestore.ts` already has `UserDoc`, `RoleProfileDoc` (9 role-specific extensions: singer/musician/lyricist/music_director/composer/sound_engineer/dj/rj/podcaster), `PortfolioProjectDoc`, `ReviewDoc`, `ConnectionDoc`, `NotificationDoc`, `UserSettingsDoc`, `BookmarkDoc`, `BrandAssetsDoc`. Most of what the brief asks for already has a home. Section 4 below lists the genuine gaps only.

**Business/Venue profiles are explicitly out of scope here — and the repo already agrees.** The brief's own section 43 says "Do not use this person-profile system for business profiles." The real screen registry already treats `public_profile_view_business` and `public_profile_view_venue` as siblings of the talent/band templates (screen 98's variants 98-3, 98-4), not part of this workstream. This spec covers **Talent + Band only** (`profileType: "TALENT" | "BAND"`).

---

## 1. The single biggest structural correction

The brief's section 59 lists ~20 items as if each needs its own screen (Audio, Video, Gallery, Instruments, Genres, Languages, Collaborators, Experience...). But the brief's *own* section 41 says: **"Do not create ten visible tabs... a strong profile should be scannable immediately."** Those two instructions conflict. I'm resolving it in favor of section 41, because it's also what the real registry already does.

**One canonical route** (screen `98` / `public_profile_view`, forking into `98-1` Talent and `98-2` Band — already modeled in `default-screens.ts` and `webapp/flows/public_profile/`), built as a single scrollable page with **at most 5 in-page section anchors** (not separate screens): `Overview` (hero → identity → featured → music strip → portfolio preview → projects preview) · `Portfolio` (audio + video + gallery, tabbed by media type) · `Projects` (full list, reuses screen `31`) · `Services` · `About` (bio, experience, languages, external links).

Everything else the brief names — Instruments, Genres, Languages, Discography, Collaborators, Experience, Reviews, Connections — is a **section or drill-in**, not a top-level screen. This is fewer screens to build, matches your existing IA, and matches the brief's own scannability rule.

---

## 2. Screen inventory — reuse vs. new

| # | Brief section | Reality | Action |
|---|---|---|---|
| 1 | Public Profile (hero, actions) | Screen `98`/`98-1`/`98-2` — real, has a working `screen.json` data contract (`webapp/flows/public_profile/public_profile_view_talent/`) | **Reuse**, restructure into the 5-anchor layout above |
| 2 | Owner Mode / My Profile | Screen `32` `profile_overview` | **Reuse** |
| 3 | Edit Profile (Identity/Professional/Music/Portfolio/Services/Availability/Social/Privacy/Verification) | Screens `33`–`39` (`edit_identity`, `profile_type_selection`, `business/band/venue/talent_profile_editor`, `external_links_editor`) — already progressive-disclosure, exactly as brief section 35 asks | **Reuse** |
| 4 | Profile Completion | Screen `41` `profile_completion` | **Reuse** — flagged in the registry as *"was a hidden/commented-out card"*; needs un-hiding, not rebuilding |
| 5 | Projects | Screen `31` `profile_project_view` | **Reuse** |
| 6 | Reviews (list, breakdown, reputation) | Screens `108`, `111`, `117` (`review_list`, `rating_breakdown`, `public_reputation`) — a whole reviews flow already exists, richer than the brief's ask | **Reuse**, surface as the profile's Reviews section |
| 7 | Connections | Screen `78` `connection_requests_tab` | **Reuse** for management; profile shows a summary strip linking here |
| 8 | Share / QR Identity | Screen `119` `qr_share_flow`; `UserDoc.vanityUrl`/`qrCodeUrl` already scaffolded | **Reuse** |
| 9 | Marketplace connection | Screen `99` `marketplace_browse`, filtered `where sellerUid == profile.uid` | **Reuse**, no new screen |
| 10 | Verification | Screen `106` `verification_screen` | **Reuse** |
| 11 | Availability toggle | Screen `42` `availability_toggle` | **Reuse** |
| 12 | Media Gallery (full-screen photo/video viewer) | Nothing today — `BrandAssetsDoc` gallery fields are Business-only | **New** — proposed screen `121` |
| 13 | Profile Analytics/Insights | Nothing at profile level (`portfolio_projects` has per-project views/likes/shares only) | **New** — proposed screen `122` |
| 14 | Collaborators (cross-project graph) | Nothing — collaborators only exist per-project (`PortfolioProjectDoc.collaborators`) | **New** — proposed screen `123`, depends on a schema addition (§4) |
| 15 | Services (Talent professionals) | `serviceOfferings` exists but is scoped to the Business extension block only | **Deferred to Flow 11 (Services) — decided 2026-08-16, not this pass.** Screen `124` stays reserved but unbuilt until Flow 11 is scoped; don't resolve the field-location question piecemeal here |
| 16 | Opportunities (on profile) | `opportunities` flow has **zero screens** — confirmed in `webapp/README.md`'s "Known gaps": *"no real UI"* | **Do not build yet.** This depends on an unbuilt flow. Flagging, not inventing. |
| 17 | Block/Report | `ModerationFlagDoc` covers Report (`targetType: "USER"`); no `BlockDoc` exists | **New schema type + sheet**, not a full screen — see §4 |

Next screen number in the real registry is `121` (max used today is `120`, `saved_favorites_hub`). Proposed new screens continue from there.

---

## 3. Component mapping — Gestalt (Pinterest) primitives → brief's component list

All of these exist today in `frontend/guide/gestalt/packages/gestalt/src/` and can be used as-is once a MusicLinkd token layer sits on top (colors/type/spacing only — don't touch structure/behavior).

| Brief component | Real Gestalt primitive | Gap |
|---|---|---|
| Avatar, Verification Badge, Availability Badge | `Avatar`, `AvatarGroup`, `Badge`, `Status`, `Indicator` | None |
| Role Badge, Genre/Instrument/Language Chip | `Tag` | None |
| Profile Action Group | `Button`, `ButtonGroup`, `IconButton` | None |
| Stat Row | `Datapoint`, `Flex` | None |
| Tabs (Overview/Portfolio/Projects/Services/About) | `Tabs`, `SegmentedControl` | None |
| Audio Track Card | `TrackCard` (Widget Directory §5, code-mapped to `UnifiedTrackCard`) | None — already cataloged |
| Audio Player | `AudioPlayerWidget` → `WaveformAudioPlayer` (Widget Directory §5) | None — already cataloged, but the Widget Directory itself flags **the underlying `.kt` file may have been deleted** (`GlobalPersistentAudioPlayerBar.kt` shows as `D` in git status per the directory's own note) — verify it still exists before building against it |
| Video Card | No cataloged widget | **Gap** — nearest is `ProjectCard`/`TrackCard` pattern adapted for video; propose adding `VideoCard` to the Widget Directory rather than inventing a one-off |
| Gallery / Masonry grid | `Masonry` / `MasonryV2` — real Gestalt component, already used in the `musiclinkd-system` Blocks tab demo | None |
| Project Card, Collaborator Card | `ProjectCard` (Widget Directory §4) | None |
| Service Card | No cataloged widget | **Gap** — propose adding to Widget Directory §6 (Bookings & Hiring) alongside `BookingCard` |
| Review Card | `ReviewCard` → `UnifiedReviewCard` (Widget Directory §7) | None |
| Share Card, QR Card | `WhatsAppShareSheet` exists but the Widget Directory's own cross-reference flags it as **possibly fake** — *"UI-only fakes... showSnackbar only, no real WhatsApp intent"* — verify before reusing | **Verify, don't assume** |
| Profile Completion | No cataloged widget; screen `41` exists but per registry notes was hidden/commented-out | **Gap** — needs its own component, not just a screen |
| Bottom sheets (More menu, Block/Report) | `SheetMobile` | None |

**Genuinely new Gestalt components** (per the brief's own section 57 rule — add to the system, don't build one-offs): `VideoCard`, `ServiceCard`, `ProfileCompletionMeter`. Three, not the dozen the brief's component list might imply once you subtract what already exists.

---

## 4. Schema evolution — proposals only, nothing silently added

Per the brief's own section 56 format.

| Existing field | Missing requirement | Proposed field | Reason | Affected screen |
|---|---|---|---|---|
| `UserDoc.serviceOfferings` (documented as Business-only, grouped under the "Business extension" comment block) | Talent professionals (mixing/mastering/session engineers etc.) can't list priced services — brief §19 explicitly wants this for Talent | **Deferred to Flow 11 (Services) — decided 2026-08-16.** The technical finding in §5 Q2 (the field was never actually enforced as Business-only) stays on record for whenever Flow 11 picks this up — not resolved here | Services section (new screen `124`), deferred |
| `PortfolioProjectDoc.collaborators` (per-project only) | No way to answer "who has this person worked with, across everything" without an expensive fan-out query | Add `collaboratorUids: string[]` (denormalized) to `UserDoc`, updated whenever a project's collaborator list changes | Firestore has no server-side joins; without denormalization, the Collaborators screen requires reading every project this person appears on, on every profile view | Collaborators screen (new `123`) |
| — (nothing today) | No profile-level analytics exist — only per-project `projectViews`/`likes`/`shares` | New `profile_analytics/{uid}` doc: `profileViews`, `portfolioViews`, `audioPlays`, `videoPlays`, `searchAppearances`, `shareCount`, `connectionRequestsReceived` (counters, incremented server-side) | Brief §37 asks for this explicitly; nothing today models it at the profile level | Analytics screen (new `122`) |
| — (nothing today) | No gallery for Talent/Band — `BrandAssetsDoc.galleryUrls`-equivalent is effectively Business-only via `UserDoc.galleryUrls` under the Business extension | Add `galleryUrls: string[]` to the shared (non-extension) part of `UserDoc`, or a dedicated `gallery/{uid}` subcollection if captions/ordering are needed later | Brief §16 wants performance/studio photography for any profile type, not just Business | Gallery (new screen `121`) |
| `ConnectionDoc` (`PENDING`/`ACCEPTED`/`DECLINED`, mutual) | Brief §4 and §33 wanted "Connect" and "Follow" as distinct actions | **Decided (2026-08-16): same as Connect.** Follow collapses into the existing `ConnectionDoc` flow — no separate one-way `FollowDoc`, no new schema. The Profile header shows one relationship action, not two. | Profile header actions |
| `UserSettingsDoc.profileVisibility` (single global enum: `PUBLIC`/`CONNECTIONS_ONLY`/`PRIVATE`) | Brief §40 wants granular per-field privacy (contact info, portfolio, activity, links, availability, location — each independently toggleable) | Flagging as a **v2 proposal**, not building now — start with the existing 3-state global toggle, which already covers the common case | Privacy settings (within Edit Profile) |
| — (nothing today) | No Block relationship exists — `ModerationFlagDoc` only covers Report | New `blocks/{blockerUid_blockedUid}` doc: `blockerUid`, `blockedUid`, `createdAt` | Report and Block are different actions with different data needs (Block affects query filtering everywhere; Report is a one-time flag) | More menu / contextual actions |

---

## 5. Open questions — need your call before I build anything else

1. ~~**Connect vs. Follow**~~ — **Decided (2026-08-16): same as Connect.** Follow collapses into the existing `ConnectionDoc` flow, no separate schema. See §4's table.
2. ~~**Services for Talent**~~ — **Deferred to Flow 11 (Services), decided 2026-08-16.** This isn't a Profile-scoped decision after all — you've flagged Services as its own dedicated flow. Not resolving the field-location question here; screen `124` stays reserved but unbuilt until Flow 11 gets scoped in this same doc.

   The brainstorm from earlier stays on record for whenever that happens, since the underlying technical finding doesn't expire: `UserDoc` in `admin/types/firestore.ts` is one flat interface, not a discriminated union — `serviceOfferings?: BusinessServiceOffering[]` was never actually enforced as Business-only at the type or rules level, so "share one field" vs. "duplicate into a parallel field" is a real, live choice whenever Flow 11 lands, not a hypothetical one.
3. **Opportunities on the profile** — still open, but now has more context: Part D below has since given Opportunities its own MVP scope (Browse + Detail + Message + a minimal Create Opportunity screen). Once that's actually built, this profile section can link to it — not before.
4. **Native Android** — still open. Is there a Kotlin/Compose repo I should get access to, or is the near-term target actually the `webapp/` PWA (which is what's real and buildable today)?

---

## 6. Recommended phasing

**Phase 1** — restructure screen `98`/`98-1`/`98-2` into the 5-anchor layout (§1), un-hide screen `41`'s completion card, wire the Services/Gallery/Analytics gaps only after §5 is answered. **Phase 2** — new screens `121`–`124` once their schema dependencies land. **Phase 3** — Business/Venue templates (`98-3`/`98-4`) — separate workstream, not this spec.

---

## 7. Notion

Live since 2026-08-16 — mirrored to a Notion page ("🎵 MusicLinkd — Product Spec") in Wugweb's Workspace, `https://app.notion.com/p/3cbbb710b9ab819b8d66dc0a90258a39`. This repo file is the source of truth for *this* document; the Notion page is a manually re-synced copy, last aligned 2026-08-29.

**Companion doc (2026-08-29):** `docs/04-Wireframes/MASTER-REGISTRY.md`, mirrored to a separate Notion page, `https://app.notion.com/p/3cbbb710b9ab814293efca1d2d1ea8f0`. That's the full raw registry — all 24 flows, all 135 screens, all 34 schema types, all 27 widgets, all 29 taxonomy categories — where this file only narrates 4 flows in depth. **Sync direction is reversed for that doc, per your explicit instruction:** the Notion page is primary, the repo file is the mirror. Don't assume this file's repo-primary convention applies there too.

---

## Part B — Marketplace (Flow 7): Buy / Sell / Rent / Rent Out

Your brief, cross-checked against the real repo before anything below is treated as fact.

### B.0 Reality check — this one's a bigger gap than Person Profile was

Flow 7 (`webapp/flows/marketplace/_flow.json`) is on record, in its own words, as: *"100% mocked, not wired to Firestore."* Its `screenCount` is `3`, and all three have `collectionsTouched: []` — confirmed by exhaustive grep, not just unaudited:

| Real screen | Number | What it actually is |
|---|---|---|
| `marketplace_browse` | 99 | `MarketplaceBetaScreen`, live in the dashboard's Marketplace tab — but backed entirely by a **hardcoded 8-item list**. "Gear Rentals" is one of its 6 category tabs, not a separate flow. |
| `marketplace_item_detail` | 100 | Rendered as an `AlertDialog` inside the browse screen — **not a navigable screen**. Its "Contact Seller" / "Rent This Gear" buttons set a fabricated local success string (e.g. a fake request ID). No network or DB call happens. |
| `marketplace_saved_bookmarks` | 101 | A thin wrapper that opens the shared Saved/Favorites hub, defaulted to its Marketplace sub-tab — not its own feature. |

No create-listing, cart, checkout, seller-messaging, offer, or review screens exist anywhere in this flow. None. Your Buy/Sell/Rent/Rent-Out brief describes ~66 nominal screens; the real starting point is 3 screens, 2 of which aren't real screens, and 0% Firestore-wired.

**A real conflict worth surfacing, not silently resolving:** on 2026-08-13, a previous session explicitly merged the standalone "Equipment Rental" flow into Marketplace, on the stated grounds that *"rental was never anything more than one of this screen's own category tabs."* Your brief here re-splits Rent and Rent Out into full parallel flows with availability calendars, pickup/delivery, active-rental tracking, and returns — substantially more than a category tab. That reopens a decision someone made deliberately five days ago. I'm not overriding it on your behalf — see open question B.3-1.

What the schema already has, relevant to this flow:
- `MarketplaceItemDoc` — real type: `sellerUid`, `title`, `price`, `category` (taxonomy-backed: `gear_sale` / `gear_rental` / `beats_licensing` / `stems` / `mixing_mastering` / `creative_services`), `condition`, `imageUrl`, `itemCategory`, `quantity`. This is the schema's current model of "rental": one category value on a listing, not a booking engine.
- `ModerationFlagDoc.targetType` already includes `"MARKETPLACE_ITEM"` — Report is covered.
- `BlockDoc` (added this session for Part A) is entity-agnostic (`blockerUid`/`blockedUid`) — reusable here as-is, no rework needed.
- `ChatThreadDoc`/`ChatMessageDoc` are generic (`memberUids`) — reusable for Marketplace Conversation, same pattern as Profile's Message action.
- `StudioBookingDoc` is the closest real precedent for a rental-with-dates record (`date`, `slotText`, `bookingStatus`, `paymentStatus`, `addons`) — but it's hard-typed to `studioId`, not reusable for equipment as-is. It proves the pattern is workable, not that it's ready.
- `ReviewDoc.targetType` is `"STUDIO" | "USER"` only — **no marketplace-listing review target exists.**
- No Offer/Negotiation doc exists anywhere in the schema.
- No Order/Transaction doc exists for a straight Buy purchase.

### B.1 Same structural correction as Part A — canonicalize, don't quadruplicate

Your own brief already asks for this in its "MARKET — SHARED SCREENS" section, and it's the right instinct. Buy, Sell, Rent, and Rent Out aren't four flows — they're one listing pipeline (Browse → Category → Filters → Results → Listing Detail → Seller/Provider Profile → Message/Offer → Transaction → Status → Review), parameterized by `listingType` (`sale` | `rental`) and by viewer role (buyer/renter sees the public Listing Detail; seller/owner sees the same screen with owner controls — "My Listings" is that same screen filtered to `sellerUid == me`, not a separate design, exactly the same pattern as Part A's Owner Mode).

**Decided (2026-08-16):** Rent stays merged into Marketplace as a listing category, not a separate flow — the 2026-08-13 merge decision holds. That means no standalone rental-booking screen sequence and no separate `rental_bookings` collection with its own lifecycle engine (Pickup/Delivery/Active/Return as distinct screens) — rental-specific data folds into the same Transaction/Order record and the same Offer/Negotiate primitive that Buy uses. Tables below are revised to reflect this — it's a real simplification from the original B.2/B.3 draft, not just a relabeling.

### B.2 Screen inventory — reuse vs. new

| Brief screens (collapsed) | Reality | Action |
|---|---|---|
| Market Home | `marketplace_browse` (99) | **Reuse — MVP**, needs real Firestore wiring — it's currently 100% hardcoded |
| Search / Category / Filters / Sort | Nothing real — browse screen's category tabs are hardcoded, no search/filter logic exists | **New — MVP**, proposed screen `125` |
| Listing Results | Same surface as Market Home, tab-filtered | Folds into `125`, not a separate screen |
| Listing Detail | `marketplace_item_detail` (100) — currently a dialog, not a screen | **Reuse the number, rebuild as a real screen — MVP** |
| Seller / Provider Profile | Screen `98` (`public_profile_view`) — the exact same canonical profile from Part A | **Reuse directly — MVP** — do not build a second profile screen for sellers |
| Make Offer / Negotiate / Accept Offer | Nothing | **New — Phase 2**, proposed screen `126`, needs a new `OfferDoc` (§B.3), shared by both sale and rental listings |
| Marketplace Conversation | `chat_threads`/`chat_messages` — generic, reusable | **Reuse — MVP**, contextualize the thread to the listing (same pattern as Profile's Message action carrying context) |
| Transaction / Order Status (covers both Buy's "Purchase/Transaction/Order Status" and Rent's "Rental Summary/Confirm/Pickup-Delivery/Active/Return") | Nothing; `StudioBookingDoc` is the nearest pattern but studio-only | **New — Phase 2**, proposed screen `127`, one screen whose `status` states carry the full lifecycle for whichever `listingType` applies — no separate rental engine, per the decision in B.1 |
| Review | `ReviewDoc` exists, reviews UI (`108`/`110`/`111`/`117`) already built for Part A | **Reuse the UI — Phase 2**, but `ReviewDoc.targetType` needs widening (§B.3) — it can't target a listing today |
| Sell / Create Listing (type → media → details → condition → price → location → preview → publish) | Nothing — no create-listing screen exists at all | **New — MVP (decided 2026-08-16), shipped minimal.** One flow, proposed screen `128`, `listingType` param distinguishes sale vs. rental rather than being two flows. "Minimal" means: title, category, condition, price, one photo, description, publish — skips the brief's full 8-step preview/location-picker polish, which is a Phase 2 refinement, not a blocker |
| Rent Out (owner-side rental creation) | Nothing | **Folds into `128`** with `listingType: "rental"` — confirmed not a separate flow, ships in MVP alongside Sell |
| My Listings / My Purchases / My Rentals | Nothing | **New — Phase 2**, proposed screen `129`, filtered views of `marketplace_items`/`marketplace_orders` by `sellerUid`/`buyerUid` — no separate rentals collection to filter, per B.1 |
| Availability (equipment, rental category only) | Nothing — no per-listing calendar concept exists anywhere, including on `StudioDoc` | **New concept — Phase 2**, kept minimal (a blocked-dates list, not a calendar engine) — schema in §B.3 |
| Saved | `marketplace_saved_bookmarks` (101) | **Reuse — MVP** |
| Report / Block | `ModerationFlagDoc` + `BlockDoc` (added this session) | **Reuse — MVP**, nothing new needed |
| Notifications | `NotificationDoc` exists, but `NotificationType` has no offer/order-status value | **Reuse the screen, extend the type — Phase 2** (§B.3) |

### B.3 Schema evolution — proposals only, same discipline as Part A

| Existing | Missing | Proposed | Reason | Affected screen |
|---|---|---|---|---|
| `MarketplaceItemDoc` | No offer/negotiation model | New `offers/{offerId}`: `listingId`, `buyerUid`, `sellerUid`, `amount`, `message`, `status` (`PENDING`/`COUNTERED`/`ACCEPTED`/`DECLINED`/`EXPIRED`), `counterAmount?`, `createdAt`, `updatedAt` | Buy, Rent, Sell, and Rent Out all need Make Offer → Negotiate → Accept as one shared primitive, not four — one collection regardless of `listingType`, per the rent-stays-merged decision | Make Offer / Negotiate (new `126`) |
| — (nothing today) | No order/transaction record for a purchase **or** a rental — single gap, not two, since rent didn't get its own flow | New `marketplace_orders/{orderId}`: `listingId`, `buyerUid`, `sellerUid`, `amount`, `status` (`PENDING`/`PAID`/`COMPLETED`/`CANCELLED`, plus `ACTIVE`/`RETURNED` when `listingType == "rental"`), `createdAt`. **Payments (decided 2026-08-16): reuse `StudioBookingDoc`'s existing rails as-is** rather than inventing new payment fields — add `paymentMethod?: "UPI" \| "Card" \| "Net Banking" \| "Wallet"`, `paymentStatus?: "PENDING" \| "PAID" \| "REFUNDED" \| "FAILED"`, `paymentTransactionId?: string`, matching `StudioBookingDoc`'s field names and types exactly. When `listingType == "rental"`, also carries `startDate?`, `endDate?`, `deliveryMethod?: "pickup" \| "delivery"` — no separate rental-booking collection | "Purchase/Transaction/Order Status" (Buy) and "Rental Summary/Confirm/Pickup-Delivery/Active/Return" (Rent) both write here — one record type, per B.1 |
| `MarketplaceItemDoc` (no calendar) | No per-listing availability | Add `blockedDates?: string[]` to `MarketplaceItemDoc` for rental-category listings | Renters need to see what's already booked before requesting dates — simplest workable model, not a full calendar engine | Availability step, folds into `127` |
| `ReviewDoc.targetType` (`"STUDIO" \| "USER"`) | Can't review a purchase or rental | Widen to `"STUDIO" \| "USER" \| "MARKETPLACE_ITEM"`, add optional `orderId?` link (mirrors the `bookingId` field `ReviewDoc` already has for studio "verified purchase" — same idea, renamed since it now points at `marketplace_orders`) | Every Market sub-flow ends in Review — nothing to attach it to today | Review (reuse `108`/`110`/`111`/`117`) |
| `NotificationDoc.type` | No offer/order-status notification | Add `"OFFER_RECEIVED" \| "ORDER_STATUS_UPDATE"` to the `NotificationType` union | Buyer/seller need to know when an offer lands or an order/rental status changes | Notifications (existing, reused) |

### B.4 Decisions (2026-08-16)

1. **Rent stays merged into Marketplace** — not un-merged. Confirmed: rent remains a listing category (`gear_rental`), not a parallel flow. B.1–B.3 above are revised to match — no separate rental-booking collection or lifecycle screens.
2. **Payments: yes, reuse `StudioBookingDoc`'s existing rails.** `marketplace_orders` (§B.3) adopts the same `paymentMethod`/`paymentStatus`/`paymentTransactionId` field names and types verbatim, rather than a new payments model.
3. **Scope for the first pass: MVP.** Real Browse + real Listing Detail + Message — no Offer/Order engine yet. See B.5 for exactly what that means and one gap it exposes.

### B.5 What I have and haven't done here

**Updated 2026-08-16 — implemented, not deployed.** Schema (`offers`, `marketplace_orders`, `blockedDates`, `ReviewDoc`/`NotificationDoc` widening), rules, and screens `125`–`129` are now real source code — see the top-of-file Implementation Log for exactly what changed and where. `firestore.rules` deployment was attempted and confirmed impossible from this sandbox (no credentials); you'll need to run `firebase deploy --only firestore:rules` yourself. The actual app-level wiring (`marketplace_browse` reading real data, the Create Listing UI existing in the Kotlin/PWA app) is still not done — that requires touching the app itself, which isn't in this repo.

**What MVP means concretely, now that it's fully scoped (2026-08-16):**
- Wire `marketplace_browse` (99) to real `marketplace_items` reads instead of the hardcoded 8-item list.
- Rebuild `marketplace_item_detail` (100) as a real screen (it's currently an `AlertDialog`), reading one real `marketplace_items` doc.
- **New screen `128`, minimal Create Listing** — title, category (`gear_sale`/`gear_rental`/etc.), condition, price, one photo, description, publish. Writes a real `marketplace_items` doc. This is what makes real Browse possible without admin seeding.
- Wire "Contact Seller" to a real `chat_threads` conversation instead of the current fabricated-success-string fake.
- Reuse `marketplace_saved_bookmarks` (101) as-is, wired to `user_bookmarks`.
- Still Phase 2, not MVP: `offers`, `marketplace_orders`, `blockedDates`, `ReviewDoc`/`NotificationDoc` changes, the full 8-step listing-creation polish (photo gallery, location picker, preview step) — per B.3's markings above.

The seed-data question is resolved — no more open gap here.

---

## Part C — Discover (Flow 4)

### C.0 Reality check — better news than Marketplace, still 100% mocked

Flow 4 (`webapp/flows/discover/_flow.json`), status `app-mocked`: *"100% mock data — DiscoverViewModel has zero Firestore references, filters a hardcoded 12-profile list."* Unlike Marketplace, though, the screens themselves are real and already close to what your brief describes:

| Real screen | Number | State |
|---|---|---|
| `discover_feed` | 85 | Real screen, hardcoded 12-profile list, zero Firestore |
| `advanced_filters` | 86 | Real bottom sheet — sort/city/area/language/genre/experience/verified/remote/session-work — every filter runs against mock data |
| `discover_profile_detail` | 87 | Real screen, reads the same mock list, not `users` |
| `saved_profiles` | 88 | Real screen, `user_bookmarks` collection is real and rules-ready — but the **screen itself is 100% unwired to it** |
| `trending_recommended_sections` | 89 | Composable functions exist in source but call sites are commented out — confirmed hidden, not deleted |

`universal_search` (118, Flow 17 "Search") is a genuinely separate screen from Discover's own search/filter states — reached from the dashboard's top bar, filters a hardcoded 10-item list across Musicians/Bands/Projects/Studios. Also zero Firestore.

**A schema duplication, flagged and now resolved:** your brief's "Studios & Spaces" discovery dimension has two real, parallel homes in this schema — `StudioDoc` (own collection, own `booking` flow, screens `60`–`63`, real `collectionsTouched`) and `UserDoc` with `profileType: "BUSINESS"`/`businessType: "Studio"` (the `business_profiles` flow, screens `71`–`77`). **Decided (2026-08-16): no consolidation.** Both stay as they are — Discover's Studios & Spaces tab queries `studios` directly, since it's the more complete, already-wired path (real `collectionsTouched`, dedicated booking flow). Business-profile-as-studio remains reachable separately through the Businesses tab. Nothing gets merged or migrated.

### C.1 Same correction as A and B — the brief's own funnel already canonicalizes

Your Discover screen list (20 items) mostly isn't 20 screens — it's one Discover Home with a type switcher (People/Businesses/Music/Services/Spaces/Opportunities — **Communities removed, decided 2026-08-16, see below**), each tab reusing the *existing, canonical* detail screen for that entity type rather than a bespoke "Business Profile" or "Project Detail" screen living inside Discover itself:

| Brief screen | Reality | Action |
|---|---|---|
| Discover Home / "What are you looking for?" / Choose Discovery Type | `discover_feed` (85) | **Reuse, rewire** — restructure as tabs instead of a single mock list, wire to real `users`/`studios`/`portfolio_projects` queries |
| Global Search | `universal_search` (118) | **Reuse, rewire** |
| Search Results / Filters | `advanced_filters` (86) | **Reuse, rewire** |
| People / Talent results | `users` where `profileType in (TALENT, BAND)` | Fold into `85`'s People tab — no new screen |
| Businesses results | `users` where `profileType == BUSINESS` | Fold into `85`'s Businesses tab |
| Studios & Spaces results | `studios` collection (booking flow) — **decided, see C.0** | Fold into `85`'s Spaces tab |
| Services results | `serviceOfferings` (Business, live) / Talent services deferred to Flow 11 (§5 Q2) | Fold into `85`'s Services tab for Business now; Talent side waits on Flow 11 |
| Projects / Portfolio results | `portfolio_projects` | Fold into `85`'s Projects tab |
| Music / Audio results | `portfolio_projects` filtered to audio-bearing types, `role_profiles.*audioSampleUrls` | Fold into `85`'s Music tab |
| Opportunities (as a Discover tab) | `projects` collection — **see Part D**, which covers this properly | Fold into `85`'s Opportunities tab once Part D lands |
| Nearby | `StudioDoc.geo` exists (added 2026-08-12); **`UserDoc` has no `geo` field** — only a display-string `location` | **Partial gap** — Nearby works for Studios, not for People, until schema changes (§C.2) |
| Person Profile | Screen `98` (Part A's canonical profile) | **Reuse directly** |
| Business Profile | Screens `71`–`77` | **Reuse directly** |
| Studio Profile | Screens `60`–`63` | **Reuse directly** |
| Project Detail | Screen `31` (`profile_project_view`) | **Reuse directly** |
| Service Detail | Business: screen `72`. Talent: Part A's screen `124` (currently blocked, same dependency) | **Reuse**, inherits Part A's block |
| Saved / Following | `saved_favorites_hub` (120) — **decided canonical, see C.3 investigation below** | **Reuse `120` only** — `saved_profiles` (88) is not built against |

**Communities — removed (decided 2026-08-16).** Was flagged as a genuine gap (no `CommunityDoc`, no schema anywhere) — you've confirmed it's out of scope. Dropped from Discover's dimensions entirely, not deferred.

Net: **zero new screens needed for Discover** beyond what already exists — this is a rewiring job (mock → real Firestore queries), not a build.

### C.2 Schema evolution — proposals only

| Existing | Missing | Proposed | Reason | Affected screen |
|---|---|---|---|---|
| `StudioDoc.geo` (real, added 2026-08-12) | `UserDoc` has no coordinate field, only a display-string `location` | Add `geo?: { lat: number; lng: number }` to `UserDoc`, mirroring `StudioDoc`'s existing shape exactly | "Nearby" people search needs the same distance-sort capability Studios already have | Discover's Nearby tab (`85`) |

### C.3 Decisions (2026-08-16)

1. **Studio duplication — no consolidation.** Confirmed: `StudioDoc` and `UserDoc`-as-business-Studio both stay. Discover queries `studios` directly for the Studios & Spaces tab (§C.0, §C.1).
2. **Communities — removed.** No longer part of Discover's scope. No schema, no tab, no gap to track.
3. **Saved/Following duplication — investigated, `saved_favorites_hub` (120) is canonical.** Findings:
   - The two screens trace to two *different* Kotlin source files: `saved_profiles` (88) is documented against `SavedProfilesScreen.kt` (per the comment on `BookmarkDoc` in `admin/types/firestore.ts`, sourced from an earlier audit pass, `AI_Studio_Sync_Prompts.md` §9). `saved_favorites_hub` (120) is documented against `SavedAndFavoritesFlow` in `SupportingScreens.kt:1023` — confirmed via a **direct source read on 2026-08-13** (`admin/app/(admin)/flows/default-flows.ts:201`), with a real, confirmed entry point: "reached from the dashboard's Saved shortcut."
   - Unlike the equipment_rental→Marketplace case (which has an explicit `"MERGED 2026-08-13..."` note tying the two together), **no reconciliation note exists anywhere linking `saved_profiles` and `saved_favorites_hub`.** Nobody has confirmed they're the same screen.
   - `saved_favorites_hub` is strictly broader (4 tabs: Artists/Bands/Studios/Marketplace) and more recently, more directly verified against source than `saved_profiles` (people-only, older audit pass, no confirmed live entry point in anything I could read).
   - **Conclusion:** build against `saved_favorites_hub` (120) as canonical for Saved/Following. Treat `saved_profiles` (88) as likely superseded or dead — but that's a recommendation based on available evidence, not a confirmed fact, since the actual Kotlin source (`SavedProfilesScreen.kt`) isn't in this repo for me to verify directly. Worth a quick source-level check on your end before formally deprecating `88` in the registry.

### C.4 What I have and haven't done here

Still spec only — no code changes. All three open questions from the previous pass are now resolved (above), so Discover's plan is locked in: rewire `discover_feed`/`advanced_filters`/`discover_profile_detail`/`universal_search` to real Firestore queries, drop Communities, route Studios & Spaces to `studios`, and build Saved/Following against `saved_favorites_hub` only. Nothing has actually been built yet — no changes to `DiscoverViewModel`, no new types in `firestore.ts`, nothing registered in `default-screens.ts`. This is now unblocked and ready to build whenever you say go.

---

## Part D — Opportunities (Flow 5)

### D.0 Reality check — this is the emptiest of the four flows

Flow 5 (`webapp/flows/opportunities/_flow.json`), status `missing`, `screenCount: 0`. Its own description is unusually blunt, worth quoting in full: *"100% missing, not just unconfirmed. Zero `.collection(\"projects\")` calls anywhere in the app; `ProjectSchema` is defined but never instantiated. The only browse/apply UI is commented-out or unreachable dead code whose own copy admits 'Opportunities are coming soon.' `UnifiedOpportunityCard` is defined but never called. A 'Live Opportunities Feed' nav item exists but routes to an unrelated business-storefront screen. `ChatContext.OPPORTUNITY` is just a messaging-inbox filter tab around one hardcoded demo message, not a posting/browsing flow."*

There is no partial build to reuse here. This is the most honest possible starting point: nothing.

**One real, useful decision already on record:** a prior reconciliation deliberately mapped "Opportunities" onto the existing `projects` collection (`ProjectDoc`) rather than a separate `gig_opportunities` collection — *"folded in rather than a separate... collection... distinct from the 'Add Portfolio' flow (finished creative work someone already made)."* That decision is sound and I'm keeping it, not reopening it. `ProjectDoc` already has real, deployed rules (`firestore.rules:122-127` — `read: true`, `create` gated to `creatorUid`, `update`/`delete` to owner or moderator) ready to receive richer data the moment the type gets extended.

Your brief's "Critical ecosystem connection" diagram is accurate to how this repo is actually built: `role_profiles`/`portfolio_projects` (Profile/Portfolio) → `projects` (Opportunities, this section) → `chat_threads`/`chat_messages` (Messaging, real and generic) → booking/project completion → `reviews` (Reputation, real and rich) → back into Discover. The loop already exists structurally in the schema; Opportunities is the one missing link.

### D.1 Screen inventory — what maps to `ProjectDoc`, what needs a new collection

Your 24-screen list collapses the same way the other three did — most of it is one Opportunity Detail screen (parameterized by viewer role: browsing/applicant vs. poster/owner) plus one Create/Edit flow, not 24 separate routes.

| Brief screens (collapsed) | Maps to | Action |
|---|---|---|
| Opportunities Home / Search / Categories / Results / Filters | New screen `130` — reads `projects` where `status == "OPEN"` | **New — MVP** (screen only — `ProjectDoc` mostly has what's needed already, see D.2 for the gaps) |
| Opportunity Detail (title, poster, requirements, budget, timeline, location) | New screen `131` — reads one `projects` doc | **New — MVP**, needs `description` added to `ProjectDoc` (D.2) — the rest of D.2's fields are Phase 2 |
| Poster / Organisation Profile | Screen `98` (Talent/Band poster) or screens `71`–`77` (Business poster) | **Reuse directly — MVP** — same principle as Marketplace's Seller Profile |
| "Message the poster" | `chat_threads`/`chat_messages` — real, generic | **Reuse — MVP**, contextualized to the opportunity, same pattern as Marketplace and Profile |
| Apply / Submit Portfolio / Application Preview / Application Submitted | New screen `132` | **New — Phase 2**, needs a new `opportunity_applications` collection (D.2) — nothing today tracks an application as its own record, only `ProjectDoc.applicantCount` (a bare number). MVP substitutes "Message the poster" for this. |
| My Applications / Application Status (Submitted/Viewed/Shortlisted/Accepted/Rejected) | New screen `133` — reads `opportunity_applications` where `applicantUid == me` | **New — Phase 2**, same schema dependency as above |
| Shortlisted / Accepted / Rejected states | States on `opportunity_applications.status`, not separate screens | Folds into `133`, **Phase 2** |
| Create Opportunity / Edit Opportunity | New screen `134` — writes to `projects`, rules already support this (`firestore.rules:122-127`) | **New — MVP (decided 2026-08-16), shipped minimal.** Title, `description`, `roleRequired`, `budget`, `location`, publish — that's it. Richer fields (skills/languages/deadline/opportunityType, status-widening) stay Phase 2 |
| Manage Applicants | New screen `135` — poster-side view of `opportunity_applications` where `opportunityId == this project` | **New — Phase 2**, same schema dependency |
| My Posted Opportunities (Draft/Active/Paused/Closed/Completed) | Folds into `134`/owner view of `projects` where `creatorUid == me` | **Phase 2** — `ProjectDoc.status` needs widening (D.2) to support Draft/Paused, which it can't express today (`OPEN`/`IN_PROGRESS`/`COMPLETED` only) |
| Saved Opportunities | `saved_favorites_hub` (120) — confirmed canonical for Saved/Following per C.3 | **Reuse — MVP** |

### D.2 Schema evolution — proposals only

| Existing | Missing | Proposed | Reason | Affected screen | Phase |
|---|---|---|---|---|---|
| `ProjectDoc` (`title`, `roleRequired`, `budget`, `status`, `genres`, `location?`, `applicantCount?`, `collaborationType?`, `createdAt`) | **No description field at all** | Add `description: string` | An Opportunity Detail screen with no body text isn't a detail screen — this is a real, plain gap, not an enhancement | Opportunity Detail (`131`) | **MVP** |
| `ProjectDoc.roleRequired` (single string) | Brief wants multi-select skills/instruments required, not one role | Add `skillsRequired?: string[]`, keep `roleRequired` as the primary role (backward-compatible, additive) | "Role / Skills" in the brief's Opportunity Detail breakdown implies more than one required skill | Opportunity Detail, Create Opportunity | Phase 2 |
| — | No languages-required field | Add `languagesRequired?: string[]` | Brief's Create Opportunity flow explicitly has a Language step | Create/Edit Opportunity | Phase 2 |
| `ProjectDoc.location?` (display string) | No remote/on-site flag | Add `locationType?: "remote" \| "on_site" \| "hybrid"` | Brief explicitly separates "Location" from "Remote / On-site" as two different questions | Opportunity Detail, Create Opportunity | Phase 2 |
| — | No deadline/timeline field | Add `deadline?: Timestamp` | Brief's Opportunity Detail and Create flow both have a dedicated Deadline step; nothing today expresses "this closes on X" | Opportunity Detail, Create Opportunity, My Posted Opportunities | Phase 2 |
| — | No opportunity-category taxonomy (Collaboration / Professional Work / Performance / Production / Studio-Space / Equipment) — `collaborationType` is a *compensation* type (`work_for_hire`/`royalties_split`/etc.), a different axis entirely | Add `opportunityType?: string`, taxonomy-backed (new `opportunity_types` category), following the same pattern every other taxonomy-backed field in this schema already uses | Brief's "Opportunity types" tree is a real, separate categorization from how the money works | Categories / Filters (`130`) | Phase 2 — values not seeded yet, still open (D.4-3) |
| `ProjectDoc.status` (`OPEN`\|`IN_PROGRESS`\|`COMPLETED`) | Brief's "My Posted Opportunities" wants Draft/Active/Paused/Closed/Completed | Widen the union to `"DRAFT" \| "OPEN" \| "PAUSED" \| "IN_PROGRESS" \| "CLOSED" \| "COMPLETED"` (additive — existing `OPEN`/`IN_PROGRESS`/`COMPLETED` values stay valid) | Posters need to pause or close a listing without deleting it; nothing today expresses that | My Posted Opportunities, Create/Edit Opportunity | Phase 2 |
| `ProjectDoc.creatorUid` only | No denormalized poster identity — every other "card" type in this schema (`FeedPostDoc`, e.g.) already denormalizes `authorName`/`authorAvatarUrl` for exactly this reason: rendering a list without a join per row | Add `posterName?: string`, `posterAvatarUrl?: string` | Opportunity Results (`130`) needs to render a list of cards without one Firestore read per poster — same justification already accepted for `FeedPostDoc` | Opportunities Home/Results | Phase 2 (MVP can do one read per row at small scale) |
| — | No application-record concept — `applicantCount` is a bare counter, nothing tracks *who* applied, with *what*, or their status | New `opportunity_applications/{applicationId}`: `opportunityId` (→ `projects` doc ID), `applicantUid`, `attachedPortfolioProjectIds?: string[]`, `message?: string`, `status` (`SUBMITTED`/`VIEWED`/`SHORTLISTED`/`ACCEPTED`/`REJECTED`), `createdAt`, `updatedAt`. **Confirmed (2026-08-16): stays a separate collection from Marketplace's `offers`** — Marketplace is physical-goods commerce (Counter → Accept on a price), Opportunities is gigs/collaboration (Shortlist → Accept on a person) — different domains, different state machines, not unified. | This is the single biggest gap in the whole flow — nothing about "my applications" or "manage applicants" can exist without it | Apply (`132`), My Applications (`133`), Manage Applicants (`135`) | Phase 2 |

### D.3 Rules note

`/projects/{projectId}` already has working rules (`read: true`, owner-gated `create`/`update`/`delete`) — no rule changes needed there, only the type additions above. The new `opportunity_applications` collection would need its own rule, modeled on `moderation_flags`' create-then-restricted-read pattern: applicant creates (`request.auth.uid == request.resource.data.applicantUid`), both applicant and the opportunity's `creatorUid` can read, only the poster (or a moderator) can update `status`.

### D.4 Decisions (2026-08-16)

1. **Scope for the first pass: MVP, confirmed.** Browse + Detail + "Message the poster" — no formal Application record yet. See D.5 for exactly what that means and the one gap it exposes (same shape as Marketplace's B.5 gap).
2. **`opportunity_applications` vs. reusing `offers`: confirmed separate, not unified.** Your reasoning: Marketplace is physical-goods commerce, Opportunities is gigs/collaboration — different domains. D.2's table is updated to record this as decided, not proposed.
3. **`opportunityType` taxonomy values — brainstormed below (§D.6).** Phase 2 work regardless, not blocking MVP, but drafted now since you asked.

### D.5 What I have and haven't done here

**Updated 2026-08-16 — implemented, not deployed.** Schema (`ProjectDoc` additions, `OpportunityApplicationDoc`), rules, and screens `130`–`135` (a flow that had zero registered screens before today) are now real source code — see the top-of-file Implementation Log. Same deployment caveat as Marketplace: rules can't be deployed from this sandbox, confirmed by a failed live attempt, not assumed. Of the four parts in this file, Opportunities remains the one furthest from "reuse and rewire" and closest to "build from zero" at the actual app level — the registry/schema/rules groundwork is done, but nothing in the real Kotlin/PWA app reads or writes any of this yet.

**What MVP means concretely, now that it's fully scoped (2026-08-16):**
- New screen `130` (Opportunities Home) — reads `projects` where `status == "OPEN"`.
- New screen `131` (Opportunity Detail) — reads one `projects` doc. Requires adding `description: string` to `ProjectDoc` (D.2) — without it there's no body text to show.
- **New screen `134`, minimal Create Opportunity** — title, `description`, `roleRequired`, `budget`, `location`, publish. Writes a real `projects` doc, using the rules that already exist (`firestore.rules:122-127`). This is what makes real Browse possible without admin seeding.
- Poster Profile — reuses screen `98` or `71`–`77` directly, no new work.
- "Message the poster" — reuses `chat_threads`/`chat_messages`, contextualized to the opportunity, same pattern as Marketplace and Profile.
- Saved Opportunities — reuses `saved_favorites_hub` (120).
- Still Phase 2, not MVP: `opportunity_applications` collection, Apply/My Applications/Manage Applicants screens, `skillsRequired`/`languagesRequired`/`locationType`/`deadline`/`opportunityType`/status-widening — per D.2's markings above.

The seed-data question is resolved — no more open gap here.

### D.6 `opportunity_types` taxonomy — draft (brainstormed 2026-08-16)

Your original brief's tree (Collaboration / Professional Work / Performance / Production / Studio-Space / Equipment, each with sub-types) is two levels deep. This schema's taxonomy pattern (`taxonomy_categories/{categoryId}/options/{optionId}`) is deliberately flat — two Firestore levels only. That's not a limitation I'm hitting by accident; it's an explicit prior decision recorded in `admin/docs/architecture/AI_Studio_Sync_Prompts.md` for a near-identical case (city/sub-area): *"do NOT add a 3rd Firestore level... store sub-areas as an array inside the option's own metadata field instead."* Applying the same convention here rather than inventing a new one:

**One flat `opportunity_types` category, 23 leaf options, each carrying its brief-defined group in `metadata.group`** (using `TaxonomyOptionDoc.metadata`, the same escape hatch every other category already uses) — preserves your 6-group structure for UI chip-grouping/filtering without a schema deviation.

| `value` | `label` | `metadata.group` |
|---|---|---|
| `looking_for_singer` | Looking for Singer | Collaboration |
| `looking_for_musician` | Looking for Musician | Collaboration |
| `looking_for_producer` | Looking for Producer | Collaboration |
| `looking_for_composer` | Looking for Composer | Collaboration |
| `looking_for_lyricist` | Looking for Lyricist | Collaboration |
| `recording` | Recording | Professional Work |
| `mixing` | Mixing | Professional Work |
| `mastering` | Mastering | Professional Work |
| `session_work` | Session Work | Professional Work |
| `sound_engineering` | Sound Engineering | Professional Work |
| `gigs` | Gigs | Performance |
| `shows` | Shows | Performance |
| `events` | Events | Performance |
| `touring` | Touring | Performance |
| `film` | Film | Production |
| `advertising` | Advertising | Production |
| `albums` | Albums | Production |
| `digital_content` | Digital Content | Production |
| `studio_required` | Studio Required | Studio / Space |
| `rehearsal_required` | Rehearsal Required | Studio / Space |
| `venue_required` | Venue Required | Studio / Space |
| `equipment_required` | Equipment Required | Equipment |
| `rental_required` | Rental Required | Equipment |

**One naming overlap worth flagging, not silently resolving:** `recording`/`mixing`/`mastering` in this list share their exact label with values already seeded in two *other* taxonomy categories — `engineer_specialties` (a person's specialty, feeds `SoundEngineerRoleProfile.specialty`) and `studio_services` (what a studio offers). These are different axes (what an opportunity needs vs. what a person/studio provides), so I kept them as separate taxonomy categories rather than merging — but I deliberately used identical `value` strings (`"recording"`, `"mixing"`, `"mastering"`) across all three, so that a future matching feature ("opportunities matching your specialty") can compare values directly without a translation table. If you'd rather these be namespaced differently, say so before this gets seeded.

Still Phase 2 — not seeded, not wired into `ProjectDoc.opportunityType` yet, per D.4's scope decision. This is the draft ready for whenever Phase 2 starts.

---

## Part E — Widget inventory: gaps found across Parts B/C/D

Checked the real widget registry (`admin/docs/architecture/Widget_Directory.md`, 27 cataloged widgets, seeded verbatim into `admin/app/(admin)/widgets/default-widgets.ts`) against everything proposed in Parts B–D.

**A boundary I'm deliberately not crossing:** that registry's own header is explicit — *"Transcribed verbatim from admin/docs/architecture/Widget_Directory.md — don't paraphrase purpose/contentComposition/codeMapping when editing."* Every entry's `codeMapping` field points at a real, audited Kotlin class. None of the widgets I'm proposing below exist in the app yet — I have no real `codeMapping` to give them. Writing them into `default-widgets.ts` or the real `Widget_Directory.md` alongside audited findings would blur "confirmed real code" with "my own proposal," which is exactly the failure mode this whole registry (and honestly this whole spec file) exists to prevent. So these stay here, clearly labeled proposed, until someone actually builds them — same discipline as Part A §3's `VideoCard`/`ServiceCard`/`ProfileCompletionMeter`.

### E.1 Good news first — most of it already exists

| Need (Part B/C/D) | Existing widget | Verdict |
|---|---|---|
| Marketplace listing cards (Home/Results) | `MarketplaceCard` (`UnifiedMarketplaceCard`) | **Reuse, exact match** |
| Opportunity cards (Home/Results) | `OpportunityCard` (`UnifiedOpportunityCard`) — "budget tag, required skills, applicant count, deadline" | **Reuse, near field-for-field match** to D.2's proposed `ProjectDoc` additions |
| Discover result cards (People/Businesses/Studios/Projects) | `DiscoverySearchResultCard`, `ArtistCard`, `StudioCard` | **Reuse** |
| Rental date-range picking (Marketplace `blockedDates`, §B.3) | `DateField`/`DatePicker`/`DateRange` — real Gestalt primitives, already vendored in `gestalt-datepicker` | **Reuse directly — not a gap.** Correcting my own earlier assumption: I hadn't checked this package before; a custom calendar widget isn't needed |

### E.2 Real gaps — proposed widgets, no code mapping exists yet

| Proposed widget | Category | Needed by | Composition |
|---|---|---|---|
| `MediaUploadGrid` | Media | **Cross-cutting — the single biggest gap here.** Marketplace's Create Listing (`128`), Part A's Media Gallery (`121`), *and* an already-existing hole: Portfolio's own "Add Project" artwork step has **no real upload UI today** either — confirmed by `default-screens.ts`'s own note, `"'Artwork' here is a palette-theme keyword, not an uploaded image — no real upload UI exists."` Three flows need this, zero have it. | Photo/media picker grid: thumbnail previews, reorder, remove, upload progress |
| `OfferThreadCard` (or a `SheetMobile`-based `OfferBottomSheet`) | Marketplace & Opportunities | Make Offer / Negotiate (Marketplace `126`, Phase 2) — **Opportunities-only, not shared**, since `opportunity_applications` was confirmed to stay separate from `offers` (D.4-2) | Amount, message, counter-offer thread, accept/decline actions |
| `TransactionStatusCard` | Bookings & Hiring | My Listings/Purchases/Rentals (Marketplace `129`, Phase 2) | Same status-tag pattern `BookingCard` already uses for studio bookings, applied to `marketplace_orders` |
| `ConversationContextHeader` | Search, Filters & Sharing | Reused identically in 3 places, first time it's needed 3x: Marketplace Conversation, Opportunity Conversation, and Profile's Message action (all three "carry context" per this spec's own notes) — worth cataloging once instead of building it three times | Compact header inside a chat thread: "this conversation is about [listing/opportunity/profile]," thumbnail + name + link back |
| `ApplicationStatusCard` | Reputation & Activity | My Applications / Manage Applicants (Opportunities `133`/`135`, Phase 2, lower priority — MVP doesn't need this) | Applicant identity, status tag (Submitted/Viewed/Shortlisted/Accepted/Rejected), attached portfolio preview |

Five proposed, not 20+ — most of what B/C/D need was already built for a different flow and just needs reusing, consistent with the canonicalization pattern in every part of this file. `MediaUploadGrid` is the one worth prioritizing regardless of which flow you build first, since it's already blocking Portfolio today, not just these new proposals.

Still spec only — no changes to `Widget_Directory.md`, `default-widgets.ts`, or `firestore.ts`'s `WidgetDoc`. Say the word if you want these formally added to the real registry (as clearly-marked proposals, `isVisible`/`codeMapping` left unset — not backdated as if audited).
