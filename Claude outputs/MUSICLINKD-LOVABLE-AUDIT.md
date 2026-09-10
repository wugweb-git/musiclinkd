# MusicLinkd (Lovable) — Complete Audit

**Date:** 10 September 2026 · **Revision 2**
**Repo:** `wugweb-git/musiclinkd-9e6ca875` · Lovable project `ace967d9-a62e-48bb-b076-c3b63b11b185`
**Commit audited:** `d10513fd` (10 Sep 2026, 01:26) · **Live:** musiclinkd.lovable.app
**Database:** Supabase `gsqeymhtyzepwcnorjun` (Lovable-managed)

Single complete document. Replaces the five earlier files (archived in `superseded/`).

**Revision 2 changes:** new §2 (content that never reached Lovable — the central finding);
§11 rewritten against the **v9 design system**, not `governance.md` alone; three of my own
findings corrected in §21; decisions recorded in §3.

---

## 1. Verdict

**205 routes. 2 database tables. And a design system, reconciled on 31 August, that the app
built from 1 September onward has never seen.**

The Lovable app is not off-system by accident. It is built on **the previous generation of your
rules** — the exact values that were audited, corrected and explicitly reversed the day before it
started. Radius 24 (removed from the scale). Shadows avoided (rule reversed). Unsplash photography
(94 references deliberately purged). Emoji as icons (never permitted in any version).

Lovable was given your design system on day one, and said so in writing — then overrode it with a
hex code from the prompt. Everything downstream follows from that.

---

## 2. The content that never reached Lovable

This is the answer to the question that matters.

### 2.1 It received the design system, and overrode it

**11 August, 14:29 — you:** *"attached html for design guidleiens and .md file for geera; scehma
of the produict. (gestalt is our design sysytem)"*

**11 August, 14:26 — Lovable's own reasoning, verbatim:**

> *"I'm reviewing the uploaded design system to understand its component patterns… **The prompt
> specifies a primary color of #5B4BFF, which takes precedence**…"*

It read your design system. It then took the hex from the prompt body instead. That single decision
is the origin of the `#5B4BFF` in the README, and of every token divergence in §11.

**14 August — you noticed, and said so:** *"ui reference were ignored.? why ? you can still refer
it attached."*

**The complaint appears once and is never resolved by either side.** No later message returns to it.

### 2.2 The v9 system was finished the day before Lovable started

`frontend/guide/rules/*` carries the same banner on every file:

> *"**Reconciled 2026-08-31.** This file predates the v9 system and several of its values had gone
> stale. Corrections are marked below… It is now rendered into `/guide` by
> `scripts/build-guide.mjs`, so this file is the source and cannot drift from the catalogue again."*

**31 August is one day before the Lovable work resumed on 1 September.** Everything reconciled in
that pass is invisible to the Lovable repo — nothing there references `tokens.json`, `ml-core.css`,
`ml-widgets.css`, `/guide`, or any rules file.

What was decided on 31 August, and what Lovable built instead:

| Rule (v9, 31 Aug) | Lovable built |
|---|---|
| Radius scale **8 / 16 / 32 / pill**. *"24dp… not a step on any scale, so no card could have been built from tokens and matched them"* | `--radius: 24px`; `rounded-[24px]`, `rounded-[20px]` |
| *"A card lifts off the ground with `--elevation-2`… **Was** 'zero shadow noise, rely on the hairline border'"* — **rule reversed** | `styles.css`: *"Flat surfaces: 1px borders, radius 24 on cards, **shadows avoided**"* — the reversed rule, implemented |
| *"**Use the local photography library** under `/assets/img`… **Never stock.** Was 'use real photography from Unsplash'. The guide carried 94 stock references and now carries zero; they were removed deliberately"* | Unsplash URLs throughout `src/data/*` |
| **Two icon sets** — Gestalt (423, filled, product) and Lucide (2006, stroked, breadth). *"Never mix them inside one control."* Supersedes the old "Lucide only" rule | Six emoji text glyphs; Lucide elsewhere; Gestalt set absent entirely |
| Type scale: **4 fluid `clamp()` tokens** (10–12, 14–18, 20–28, 28–44px) | Eight fixed arbitrary sizes `text-[10px]…[22px]` |
| Motion: `--duration-1..4` = **125 / 200 / 320 / 500ms**, three easings, shimmer 1.4s | No motion system; `active:scale-[0.98]` only |
| Focus: **2px `--color-pink` at 2px offset, `:focus-visible` only** | No `focus-visible:` rule anywhere |
| Touch: 48dp floor, but *"controls smaller than the floor take an **invisible 48dp target** rather than growing — a 32dp chip is the right visual size"* | 44px controls with no invisible target |
| **Purple permitted in exactly one place** — `.ml-badge--category`. *"It is not a brand token — it never paints a button, link, border or surface"* | `--purple` mapped to `--accent`, a general-purpose accent |
| Typography: Inter Tight display, Inter body, **JetBrains Mono metadata** | Inter + Inter Tight ✅; no mono |

**Nine of ten current rules are broken, and most were broken by implementing the *previous*
version of the same rule.** That is the signature of a document that never arrived.

### 2.3 The Notion link that could not be opened

**1 September, 17:33:29 — you:** *"**Missing flows**: Other: Follow
`…/Architecture-Mapping-3b8bb710b9ab813a9ac6ffbe5e699764` / **Priority**: Finish the open ones /
**Scope**: Add a real backend"*

**17:33:56 — Lovable's reasoning:**

> *"I need to access that Notion doc for the flow definitions… To read the Notion architecture
> mapping, **I'll need a Notion connector**… I need to **ask the user to connect Notion** so I can
> read the architecture doc."*

**It knew. It planned to tell you. The visible output was six tool calls and no text.** No commit
followed. The next commit in the project is 24 hours later.

The connector arrived **2 September 17:27**, and at 17:36 Lovable confirmed: *"Got the flow map
from your Notion doc — your numbering matches the launch-readiness FLOW 01–24 scheme."* So it did
arrive — a day late, after a dropped instruction.

### 2.4 A numbering conflict, resolved silently

**2 September, 18:40 — Lovable's reasoning:**

> *"I also notice **the flow numbering in the uploaded doc conflicts with the Notion mapping**
> referenced in context (FLOW 03 as Market vs. Add Portfolio), so I'll go with the uploaded
> document's numbering…"*

Two of your source documents number the flows differently. Lovable spotted it, chose one, and
recorded the choice **only in its private reasoning**. You were never told.

**The consequence is visible in `roadmap.md`, which uses `FLOW 03` for two different things:**
*"FLOW 03 (Discover) — discover home, categories, search, filters, profiles"* and *"FLOW 03 — Add
Portfolio"*. Any cross-reference between the Lovable roadmap and the Master Registry is unsafe
until this is reconciled.

### 2.5 The spec that did arrive was not the one in the repo

**2 September, 18:19:** *"The uploaded file is massive, covering all flows."*
**2 September, 18:38:** *"This is a big spec file with **31k lines** covering much more than the 14
flows already done."*

`docs/04-Wireframes/FLOW-SPECS.md` in the local repo is **14,439 lines**. The file Lovable received
is roughly twice that. **They are not the same document**, and the 31k-line version does not exist
in the repo — it lives only in the Lovable chat, attached to a message whose content the API
returns as empty.

### 2.6 What is provably absent from the Lovable repo

No file in it references, and no reasoning mentions: `governance.md` · `MASTER-REGISTRY.md` ·
`tokens/tokens.json` · `ml-core.css` / `ml-widgets.css` · `frontend/guide/rules/*` ·
`hardcoded-content-audit.md` · `Widget_Directory.md` (27 audited widgets) · the Firebase backend ·
the 16 Firestore collections · the 34 document types.

### 2.7 And what I cannot see

**18 of your 56 messages return empty through the API** — attachment-only turns, including every
day from 4 to 10 September. Lovable saw those attachments; I cannot. Anything you sent on those
days is outside this audit, and that is a limit of the tooling, not evidence of loss.

---

## 3. Decisions recorded (10 September)

| Question | Your answer | Effect on this audit |
|---|---|---|
| What is the Lovable app *for*? | **Same product.** Its frontend replaces the Android frontend; **the previous version gets dumped** | §4 rewritten: not three products — **one product, one frontend, one backend** |
| Database mapping | **Only after refactor and routing** | §6 is a backlog item, not a blocker |
| Razorpay | **Not yet** | Stays open; no longer ranked first |
| Real backend | **Post-UI** | §17 deferred |
| Notion | **Stays blind** (Android-only) | Former "Notion documents the wrong product" finding **withdrawn** |
| Inter / Inter Tight conflict | **Ignore** | Withdrawn — the code is correct |
| `query_database` direct DDL | **After refactor** | Risk note retained, timing accepted |
| Lovable project knowledge | **Write it** | Now the top action (§22) |

**Consequence of "same product":** the design-system violations in §11 stop being a divergence
between two products and become **defects in the frontend you are about to ship on Android.**
Every one of them will land in the Android app.

---

## 4. One product, one frontend

| Layer | Implementation | Documented in | Status |
|---|---|---|---|
| **Frontend** | Lovable — TanStack Start, 205 routes | `.lovable/plan/*` only | **The frontend** |
| ~~Kotlin / Jetpack Compose~~ | ~~Jetpack Compose~~ | `MASTER-REGISTRY.md` — 24 flows, 188 screens, 563 states | **Being dumped.** Read it before deleting it — see below |
| **Design system** | `tokens.json` v8, `ml-core.css`, `ml-widgets.css`, `/guide`, 133 sections | `frontend/guide/*` — v9, reconciled 31 Aug | **Live. Never reached Lovable** |
| **Backend** | Firebase `musiclinkd-7ae30`, 16 collections, 34 doc types | `admin/docs/architecture/*` | Live; Spark plan (§17) |
| **Admin** | Next.js panel | `admin/docs/*` | Real and working |

The Master Registry settles the old count dispute: *"The screen list… was written when there were
135 screens. There are now **188**, across the same 24 flows… 286 → 563 declared states."*
188/563 is the Android registry; 205 is Lovable routes. Different things.

**Dumping the Kotlin frontend is the right call, but it is also the last moment to extract from
it.** Three things live only on that side and are not in the Lovable repo: the **563 declared
states** (Lovable's routes declare nothing equivalent), the **27 audited widgets** in
`Widget_Directory.md` with their code-mapping column, and the **four profile template families**
in `profile-types.md` (Talent / Band / Business / Venue with per-role module lists), which
`role_profiles` in Firestore already depends on. Harvest those into `docs/` before the delete.

---

## 5. Ground truth

### Lovable repo at `d10513fd`

| Measure | Count |
|---|---|
| Files | 349 |
| Route files | 205 incl. `__root.tsx` |
| Feature components (`ml/`) | 20 |
| Radix primitives (`ui/`) | 45 — **all unused** |
| Mock-data modules (`data/`) | 22 |
| **Database tables** | **2** |
| Migrations | 3 |
| Plan documents | 13 |
| Commits | 46, **all `completed`** |

A Sep 2 plan counted *"roughly 110 routes"*; a later Sep 2 document *"all 135 screens"*. Today:
**205**. ~70 routes in eight days. The table count did not move.

### Local repo — branch `design-system/audit-fix-set`, last commit `f1419f2` (1 Sep), clean

1,943 `.md` files → **63 project-owned** (rest vendored). Largest: `FLOW-SPECS.md`, 14,439 lines.
**`.lovable/` does not exist anywhere in it.**

---

## 6. What is built, flow by flow

| Flow | Status | Evidence |
|---|---|---|
| 01 Authentication | Built | 11 `auth.*` + `welcome`, `reset-password`, `legal.*`, `settings.delete-account` |
| 02 Onboarding | **Persisted** | 7 routes; writes to `profiles` |
| 03 Portfolio | **Persisted** | 9 routes; `portfolio_items` + `portfolio.functions.ts` |
| 04 Discover | Mock | `index`, `search`, `categories`, `singers`, `saved` + 4 `discover.*` |
| 05 Opportunities | Mock | 13 routes |
| 06 Collaboration | Mock | **30 routes** — largest surface |
| 07 Marketplace + Rentals | Mock | 21 routes |
| 08 Booking | Mock | 13 customer + 6 provider |
| 10 Profile Management | Built | `profile.$username`, `profile.edit`, `profile.insights`, `me` |
| 11 Services | Mock | 7 routes |
| 12 Business Profiles | Mock | 14 routes |
| 13 Notifications | Mock | 2 routes |
| 14 Public Profile | Built | `/profile/$username` |
| 15 Bands | Mock | 9 routes |
| 16 Verification | Mock | 5 routes |
| 17 Reviews | Mock | 6 routes |
| 18 Plans | Mock | 4 routes |
| 19 Search | Mock | `/search` |
| 20 Recommendations | Mock | 3 routes |
| 21 Settings | Mock | 5 routes |
| 22 Help / CMS | Mock | 3 routes |
| 23 QR / Identity | Mock | 13 `identity.*` |
| 24 Saved | Mock | `/saved` |

**Two of twenty are real.** No canonical flow is missing — Lovable *out-built* the Kotlin app,
designing flows 18, 20, 21 and 23 that the registry lists as `missing`.

**Caveat from §2.4:** these flow numbers come from the Lovable roadmap, which has an unreconciled
numbering conflict with the Master Registry. Treat cross-references as unsafe.

`profile-map.ts` deserves credit — `draftToProfile` / `profileToDraft` / `profileGaps`, with the
raw onboarding draft kept as jsonb for recovery. Careful work.

---

## 7. The fourteen tables (deferred by decision)

`musiclinkd-deepen-flows-01-14-to-full-spec-2026-09-02.md` named: `user_roles`, `saved_items`,
`follows`, `opportunities`, `opportunity_applications`, `services`, `service_packages`,
`availability_rules`, `availability_exceptions`, `bookings`, `booking_events`, `reviews`,
`businesses`, `business_members`. **None exist.**

Wave 1 landed (auth, onboarding, profile — genuinely persisted). Waves 3–7 shipped screens only.
Every plan since 2 September declares itself design-only; `roadmap.md` still carries the wave-era
backend checklist under **In progress**.

**Per §3 this is now scheduled, not a defect** — DB work happens after refactor and routing. The
item that stays live is the trap: **the domain is already modelled** (16 Firestore collections, 34
document types). Designing Postgres tables from scratch would fork it. See §17.

The Master Registry defines `app-mocked` as *"UI exists, zero Firestore wiring"* and applies it to
most of the Kotlin app. Swap Firestore for Supabase and that is the Lovable app across eighteen
flows — **the same failure reproduced in a second frontend.**

---

## 8. Plans versus reality

| Plan document | Carried out? | Notes |
|---|---|---|
| `flow-02-user-onboarding-2026-09-01` | Yes | All 7 routes |
| `flow-02-finish-flow-03-add-portfolio-2026-09-02` | Yes | `onboarding/done.tsx`; portfolio expanded |
| `musiclinkd-person-profile-experience-2026-09-01` | Yes | `/profile/$username` as specified |
| `flow-06-collaboration-design-only-2026-09-01` | Yes | 30 routes |
| `flow-06-collaboration-complete-…` | Mostly | Specified `collab.$id.participants.$pid.remove.tsx`; shipped `collab.$id.remove.$pid.tsx` |
| `flow-08-booking-plus-dashboard-ia-follow-up` | Yes | Full customer + provider set |
| `dashboard-ia-refactor-booking-gap-closure` | Yes | `/feed` rebuilt |
| `flow-16-verification-complete-journey` | Yes | choose / badge / privacy |
| `wave-1-continued-onboarding-profile-made-durable` | Yes | Migration 3, all 16 columns |
| `musiclinkd-deepen-flows-01-14-to-full-spec` | Waves 1–2 partial; 3–7 UI only | §7 |
| `stabilise-and-polish-every-musiclinkd-flow-in-order` | Unverifiable | Needs a live render sweep |
| `pending-screens-across-all-flows-2026-09-07` | Partially | §10 |

**No planned screen set was skipped.** One plan document carries a committed human annotation
between two `&nbsp;` blocks — `*Where are the other 7 flows ???` — the only such interjection.

---

## 9. Instructions you gave that were not carried out

From constraint extraction across all 38 non-empty prompts (363,948 chars).

**Asked three times, violated three times** — *"Do not create duplicate versions of components that
already exist"* (12 Aug) · *"Do not duplicate existing MusicLinkd components"* (14 Aug) · *"Do not
create duplicate components that already exist in Gestalt"* (17 Aug):

| Duplicate | Files | Evidence |
|---|---|---|
| `AvailabilityBadge` / `StatusPill` | `ui.tsx` / `kit.tsx` | Identical markup, parallel tone maps. `kit.tsx` comments **"One status system across every flow"** directly above the second |
| `Chip` / `Segmented` | `ui.tsx` / `kit.tsx` | Byte-identical active/inactive class strings |
| `Sheet` / `Dialog` | `ui.tsx` / `kit.tsx` | Both hand-rolled, no focus trap, no Escape, no scroll lock |

Plus **45 Radix primitives installed and unused** — including four accessible modals.

**Never acted on:** *"Do not create custom loaders"* (17 Aug — `kit.tsx` `Loading` is custom while
`ui/skeleton.tsx` sits unused) · *"Do not hardcode the colours from older prompts"* (12 Aug —
`README.md` still ships `#5B4BFF`) · *"you can tilt towards nothing phone ui"* (12 Aug — never
mentioned again) · *"ui reference were ignored.? why?"* (14 Aug — never resolved).

**Specified and never built:** **SCREEN 08 MAKE OFFER / JOURNEY 08 OFFER** (12 Aug) — an entire
journey, no route exists · **Saved Searches / Sellers / Services / Studios** (12 Aug §25) — still
pending 26 days later · **Report Listing / Seller / Block Seller / Report Scam** (12 Aug §33) —
no marketplace moderation · **Marketplace messaging with listing and offer cards in-thread**
(12 Aug §18) — `/messages` is one route with no thread context.

**Delivered very late:** SCREEN 17 RECENTLY VIEWED — asked 12 August, shipped **10 September**.
Twenty-nine days.

**Correctly honoured:** *"remove (AI-ASSISTED DISCOVERY, MARKETPLACE ANALYTICS — SELLER)"*
(14 Aug). Neither exists; re-respected in the 2 Sep person-profile plan.

**Prompts you sent twice**, near-verbatim — Person Profile (17 Aug → 1 Sep, **94.9% identical**),
Booking (1 Sep 07:21 → 2 Sep 17:30, **94.6%**), Flow 3 (11 Aug, 3 minutes apart, **94.8%**).
Collaboration and Portfolio were re-sent expanded (59% / 57%). **Roughly a quarter of your 364k
characters is re-sent material** — consistent with believing the first send had not landed.

**Unverifiable without opening route bodies:** *"Do not imply payment if payment infrastructure is
not implemented"* · *"Do not imply payment has occurred"* · *"Do not allow false confirmation"* ·
*"Do not fabricate availability"*. `/booking/$id/pay` and `/plans/checkout` exist.

---

## 10. Pending screens, reconciled

**Landed since the 7 Sep list:** Business (14 routes: services, availability, portfolio, enquiry,
reviews, verification, analytics, leads, dashboard) · Services (`enquiry`, `manage`, `conflicts`) ·
Bands (`portfolio`, `events`, `contact`) · Reviews (`reply`, `edit`, `report`, `request`) ·
Discovery (`music`, `projects`, `community`, `nearby`, `recently-viewed`).

**Still open:** follow/share a business, related-business suggestions · "why am I seeing this",
saved searches and alerts, search history, location permission · report an opportunity,
withdraw-application confirmation, attach portfolio in conversation, archive work, media
upload-failure and processing states, save-conflict · onboarding verification/services prompts,
onboarding error/offline, splash/session-check, verification help, FAQ, announcements, help search ·
change email / phone / username, invoice history and receipts.

**Not on any pending list:** the fourteen tables (§7). The 7 Sep document scoped itself *"no new
backend work"*, so the largest item is untracked by the project's own list.

---

## 11. Design system — measured against v9, not the old rules

**This section is rewritten in revision 2.** Revision 1 measured against `governance.md`. That file
is dated `v2026.1` and several of its clauses were **superseded on 31 August** by the reconciliation
pass in `frontend/guide/rules/*`. Measuring against it produced two wrong findings (§21).

The authority chain, current: `tokens/tokens.json` (v8.0.0) → `ml-core.css` / `ml-widgets.css` →
`/guide` (generated by `scripts/build-guide.mjs`) → `frontend/guide/rules/*` (the prose source) →
`frontend/guide/README.md`. `styleguide.html` is superseded v4 and 301-redirects to `/guide`.

| Rule (v9) | Lovable | Verdict |
|---|---|---|
| Brand `#EA4C89`, navy `#0D0B26`, paper `#FCFCFD`, line `#ECECF1` | `styles.css` implements exactly this | ✅ **Correct** |
| Inter Tight display / Inter body | Correct | ✅ (JetBrains Mono for metadata absent — minor) |
| **Icons: Gestalt (product) + Lucide (breadth), never mixed in one control** | `⌕ 🎙 ← ✓ 📍 ♡` as **text glyphs**; Lucide elsewhere; Gestalt set absent | ❌ **Emoji are not permitted in any version.** This is why the live app renders empty boxes. Still shipping 10 Sep (`discover.music.tsx` renders `workType(t.kind).emoji`) |
| **Radius 8 / 16 / 32 / pill** — *"24dp… not a step on any scale"* | `--radius: 24px`, `rounded-[24px]`, `rounded-[20px]`, `rounded-t-[32px]` | ❌ Two of the four values are off-scale entirely |
| **Cards lift with `--elevation-2`**; border is the fallback on white | `styles.css`: *"Flat surfaces… shadows avoided"* | ❌ Implements the rule that was **reversed** on 31 Aug |
| **Local photography, never stock** — 94 Unsplash refs deliberately removed | Unsplash URLs throughout `src/data/*` | ❌ Directly contradicts a deliberate removal |
| **Type: 4 fluid `clamp()` tokens** (10–12 / 14–18 / 20–28 / 28–44) | 8 fixed sizes `text-[10px]…[22px]` | ❌ No scale, no fluidity |
| **Focus: 2px pink, 2px offset, `:focus-visible` only** | Zero `focus-visible:` rules | ❌ |
| Touch 48dp; **small controls take an invisible 48dp target, not a bigger visual** | 44px controls, no invisible target | ❌ (but see §21 — a 32px chip is *correct*) |
| **Purple only on `.ml-badge--category`** — *"never paints a button, link, border or surface"* | `--purple` mapped to `--accent`, general-purpose | ❌ |
| Motion 125 / 200 / 320 / 500, three easings, shimmer 1.4s | `active:scale-[0.98]`; no system | ❌ |
| Spacing 4 / 8 / 16 / 32 | Tailwind default scale | ⚠️ Compatible but unenforced |
| CHANGELOG per commit (`governance.md` §8, still current) | No `CHANGELOG.md`; ~70 routes, zero entries | ❌ |

**The token layer Lovable did build is good in isolation** — semantic tokens, oklch palette,
`color-mix` containers, hairline borders. It is a competent system built to the wrong spec.

**`README.md` is the propagation vector.** It still ships the superseded brief — `#5B4BFF`, 16px
radius, Google Sans/Roboto — and it is the first file any agent reads in that repo.

---

## 12. Code defects

**Bottom-nav highlighting broken on ~200 of 205 routes.**

```js
const active = path === item.to;
```

Exact match. `/marketplace/search`, `/profile/rahul`, `/messages/…` never highlight.
*Fix: prefix match, longest-wins, `/` special-cased.*

**Typed routing disabled at the busiest component.** `Row` casts `to` and `params` to `any` behind
an eslint-disable — TanStack Router's compile-time route checking is off for every `Row`. With 205
routes, that is where dead links live.

**Display copy as a style key.** `AvailabilityBadge` does `map[status] ?? map["Booked"]`, keyed on
user-visible text. Reword or translate and every badge silently turns grey.

**Dead controls ship as working ones.** `recently-viewed.tsx` "Clear history", `cards.tsx`
"Message" — `<Btn>` with no `onClick`. Both from the last 48 hours.

**Deprecated route in brand-new code.** `discover.nearby.tsx` and `recently-viewed.tsx` (10 Sep)
link `/talent/$id`, scheduled for redirect since 2 Sep.

**Two styling mechanisms interleaved.** Tailwind classes plus inline `style={{ background:
"var(--…)" }}` in `BottomNav`, `AvailabilityBadge`, `StatusPill`, `Meter`, `TalentRow`. Inline
styles can't be overridden by class or linted for token compliance.

**Fake permission gate.** `discover.nearby.tsx` uses `useState(false)` + `setAllowed(true)` for
location. Correct as a stub; will be mistaken for real.

**Smaller:** `Meter` pct unclamped (overflows above `max`) · `Stars` rounds 4.4 to 4 beside the
label "4.4" · `Btn` types variants as `Record<string,string>`, discarding the union · `SearchBar`
hardcodes Discover copy as a generic default and has no input label · `max-w-[430px]` repeated in
four components · `TalentCard` `w-[172px]`/`h-[150px]`.

**The `src/data/*` problem.** 22 modules mix UI copy (`reviewPolicy`, `flagReasons`), seed content
(`receivedReviews`) and would-be runtime data (`ratingSummary`) in single files, read identically
by screens. **There is no seam along which to replace the runtime third with real queries.**
`reviews.ts` stores `createdAt: "3 weeks ago"` as a *string*.

**The fix is already written**, in `guide/docs/hardcoded-content-audit.md`: separate **UI copy** →
resources, **seed/demo** → a provider, **runtime** → repository, and *"never fallback to invented
user data in a production screen."* Another document that never reached Lovable.

---

## 13. The type-error thread — 24 days, four attempts, worse than at the start

| When | Who | Event |
|---|---|---|
| 17 Aug 02:50 | assistant | *"blocked on three type errors that were reported just as I stopped"* |
| 1 Sep 06:46 | **you** | Paste them — 3 × `error TS` in `profile.tsx` |
| 1 Sep 07:30 | **you** | Paste the identical blocked-state text **again** |
| 2 Sep 17:33 | **you** | Paste it **a third time** |
| 2 Sep 18:27 | **you** | 6 × `error TS` — now `identity.ts` too |
| 10 Sep | **you** | `person-profile.tsx` — **10 × `exactOptionalPropertyTypes`** |

Four "fix" commits in between. **3 errors became 10.**

**It was never eleven bugs.** `exactOptionalPropertyTypes: true` rejects an explicitly-passed
`undefined`. Every primitive in `ui.tsx`/`kit.tsx` declares `onClick?`, `tone?`, `cta?`, `back?`,
`onMore?` the narrow way; every caller spreads possibly-undefined values in. **One convention error
at every call site.** Fixing files one at a time guarantees recurrence.

**Fix at source:** `onClick?: (() => void) | undefined` on the primitives. Two files.
`roadmap.md` lists this honestly under **Next**. The commit log does not.

---

## 14. Commit messages do not describe their commits

| Commit | Message | Actual contents |
|---|---|---|
| `5129664` 8 Sep | **"Added hidden footer icon"** | 10 business/services routes. No footer, no icon, nothing hidden |
| `d10513f` 10 Sep | **"Added audio autoplay"** | 5 discover routes. No audio, no autoplay |

Two of two sampled diffs, wrong. Also: `Update plan` ×8, `Added the requested changes`,
`Updated goal label in DB`, and **`Fixed all bugs and errors`** (2 Sep 18:17) — eleven days before
you pasted ten unfixed errors from that codebase.

**On the clandestine-edit question:** I read both diffs. Nothing hidden was inserted. But `git log`
is not a usable record of this repo.

---

## 15. The chat, phase by phase

110 messages, 11 Aug 14:26 → 10 Sep 01:26, 16 active days.

**Phase 1 — 11–17 Aug.** Six prompts, six flows. Aug 15–16 end *"before the pause"*; Aug 17 ends
*"ran out of runway mid-build"* and *"blocked on three type errors."* **Then 15 days of silence.**

**Phase 2 — 1 Sep.** Eleven prompts in ninety minutes (06:46 → 07:41), several fired while the
previous build was incomplete.

**Phase 3 — 2 Sep.** Seventeen messages, thirteen commits. The wave plan, the real migrations, the
pivot back to screens.

**Phase 4 — 3–10 Sep.** One prompt per day; 4, 5, 6, 8, 9 and 10 September are **all
attachment-only** and unreadable through the API.

| Signal (of 54 assistant messages) | Count |
|---|---|
| Mentions a 412px render pass | 28 |
| Mentions typecheck / `tsgo` / `tsc --noEmit` | 30 |
| Claims "build OK" | 8 |
| **Contains an incompleteness marker** | **26 (48%)** |

Every plan promises "typecheck, build, 412px render pass" as its exit criterion. **Half the
messages promising it also admit they didn't finish — and all 46 commits are recorded `completed`.**
There is no state for "landed, verification skipped", which is the state most of this repo is in.

---

## 16. Three decisions that were dropped

**Razorpay — 1 Sep 07:14.** Lovable answered correctly (Stripe unavailable for Indian sellers,
Paddle ineligible, Razorpay recommended) and asked *"Which would you like to go with?"* Your next
message, one minute later, was the FLOW 02 prompt. *Status: deferred by decision, §3.*

**"Add a real backend" — 1 Sep 17:33.** Dropped to an unreadable Notion link (§2.3). Reinstated
2 Sep only after *"but i twll you flow 1-14 you did were justr a tip on the ice."*

**The wave plan — 2 Sep 19:09.** Wave 1 landed at 19:05 with real migrations. At 19:09: *"now
continue with remainign screens and flows."* Every plan after is design-only. Nobody flagged the
supersession — including the agent that wrote the plan thirty minutes earlier.

---

## 17. The backend that already exists

- **16 canonical Firestore collections**, working Next.js admin. `verification_requests`, `reviews`
  and `page_content` plus their admin modules are real.
- **The Kotlin app touches 3 consistently** — `users`, `connections`, `feed_posts` — plus an ad-hoc
  `musicians` collection keyed by *slugified display name, not the Auth UID*.
- `studios`, `projects`, `marketplace_items`, `reviews`, `studio_bookings`, `chat_threads` are
  *"never written or read by the app despite full-featured screens existing for all of them."*

**The gate, and it predates Lovable:**

- `musiclinkd-7ae30` is on Firebase's free **Spark plan**. Cloud Functions cannot deploy — a live
  `firebase deploy --only functions` was attempted and failed.
- `backend/{functions,api,webhooks,auth,database,storage}/` held only `.gitkeep`. **No server-side
  trust boundary exists** — booking amounts, conflicts and payment authorisation all ran
  client-side.
- **A free-tier Supabase project is already registered in your `wugweb` org** as an Edge Functions
  bridge (credentials in `admin/.env.local`), deferred pending a hand-generated Firebase
  service-account key. **Not** the Lovable Supabase project.

Per §3 this is post-UI. The item to carry forward: **34 document types are already modelled.**

---

## 18. What else the backup already knew

`admin/docs/architecture/Widget_Directory.md` — **27 audited widgets** across six categories, each
with a real code-mapping column. `design-system-rebuild-plan.md` calls it *"the actual component
inventory the new system needs to implement against — it's already scoped, don't re-derive it."*
The Lovable app re-derived it.

`motion.md` records the system's own known gap: **0 entry animations, 0 view transitions, 0
scroll-driven motion, hard-cut state switching across 518 states.**

`profile-types.md`: *"`role_profiles` is a live Firestore collection with an admin route, and none
of the four families or their module lists appear in `/guide`."* Four template families —
Talent / Band / Business / Venue — with per-role module lists, unbuilt in the design layer and
unknown to Lovable.

**~40 of the 63 project `.md` files remain unread by me** — `PRODUCT-SPEC.md`, three of four
`docs/09-Roadmap/*`, nine of ten `admin/docs/architecture/*`. Given that the six small rules files
overturned two of my findings, **the remaining forty are likely to overturn more.**

---

## 19. Risks

1. **The design system will be broken *in the Android app*.** Per §3 this frontend replaces the
   Kotlin UI. Every §11 defect ships with it.
2. **Schema drift is easy to cause.** Migrations are tracked, but the live DB is also reachable via
   the Lovable connector, which executes DDL. Every schema change must go through a migration file.
3. **`roadmap.md` is untrustworthy.** Duplicate flow blocks (FLOW 11/12/13/21 appear once unchecked
   and again checked), stale wave-era backend section, and the §2.4 numbering conflict.
4. **Two writers, one branch.** `AGENTS.md`: pushed commits sync into Lovable; rewriting published
   history can destroy project history. Never force-push.
5. **`README.md` contradicts the design system** and is read first by every agent.
6. **The plan history has no second copy.** `FLOW-SPECS.md` opens by describing how this class of
   file was already lost once: *"until then they existed only in `~/.claude/projects/` and were
   repeatedly lost to context compaction."* The 13 `.lovable/plan/*.md` files are in that position
   now — **and so is the 31k-line spec (§2.5), which exists nowhere else.**

---

## 20. Coverage — what this audit did and did not read

| Source | Coverage | Confidence |
|---|---|---|
| Lovable chat | **110 of 110** messages, ~2.64M chars | Structure complete; **verbatim ≈ 0.8%** |
| Your prompts | 38 non-empty, 363,948 chars — all constraint lines extracted; 8 read in full; ~25% are re-sends | High on instructions |
| Your prompts, unreadable | **18 of 56 empty** — all of 4, 5, 6, 8, 9, 10 Sep | **Zero** |
| Lovable's replies | 2.28M chars, 2,144 tool-call blocks; targeted extraction | Medium |
| Largest unread object | one message of **274,433 chars** (2 Sep 18:06) | — |
| Commits | 46 of 46 metadata; **2 diffs in full** | High on metadata |
| Local repo `.md` | **23 of 63** read | Partial — §18 |
| Component code | `ui.tsx`, `kit.tsx`, `cards.tsx`, `styles.css`, `profile-map.ts`, `types.ts`, 3 migrations | High on those only |
| Route bodies | **0 of 205** | None |
| Live database | Queried directly | Measured |

---

## 21. Corrections — including two from revision 1

1. **"`governance.md` is authoritative."** Partly stale. Its §3 *"Lucide only… Period"* was
   **superseded 31 August**: the current rule is **two icon sets**, Gestalt (product) + Lucide
   (breadth), never mixed in one control. The emoji finding stands; the framing was wrong.
2. **"Chip/Segmented at 32px violates the 48dp minimum."** **Wrong.** `accessibility.md`:
   *"Controls smaller than the floor take an invisible 48dp target rather than growing — **a 32dp
   chip is the right visual size**."* The real defect is the missing invisible hit target.
3. **"Two MusicLinkds."** Then "three." Now **one product, one frontend** — the Lovable UI, with
   the Kotlin frontend dumped (§3).
4. **"The pink drifted accidentally."** No — documented and mandated.
5. **"I can apply the schema via `query_database`."** True but unsafe: `supabase/migrations/` is
   tracked, so direct DDL desyncs the live schema from history.
6. **"Design 14 Postgres tables."** A live backend and 34-type domain model already exist.
7. **"Real backend decided 2 Sep 18:38."** Asked **1 Sep 17:33** and dropped.
8. **"Notion describes the wrong products."** **Withdrawn** — Notion stays Android-only by decision.
9. **The Inter / Inter Tight conflict.** **Withdrawn** — ignore; the code is correct.
10. **Five separate documents.** Wrong shape; this replaces them.

---

## 22. Do these, in this order

1. **Write Lovable project knowledge — it is empty, and so is the workspace-skills list.** That
   emptiness is the mechanism behind §2: nothing in the Lovable environment carries your rules
   between sessions. It should bind: the v9 token scales (radius 8/16/32/pill, four fluid type
   tokens, motion 125/200/320/500); **no emoji as icons, ever**; local photography, never Unsplash;
   purple only on category badges; cards lift with elevation; `focus-visible` on every control; no
   `onClick`-less buttons; optional props declared `| undefined`; a CHANGELOG entry per commit.
2. **Fix `README.md`.** It ships `#5B4BFF` and is read before anything else.
3. **Fix the optional-prop convention at source** (§13). Two files, ten minutes, ends a 24-day
   recurring failure.
4. **Replace the six emoji with icon components** (§11). One file; fixes the visible defect on the
   live app.
5. **Fix the bottom-nav prefix match** (§12). Four lines; broken on ~200 routes.
6. **Reconcile the flow numbering** (§2.4) before any cross-reference between the Lovable roadmap
   and the Master Registry.
7. **Retrofit the v9 token scales** — radius, type, motion, elevation, photography source. This is
   the refactor; do it before routing, and before the DB work you have scheduled after it.
8. **Mirror `.lovable/plan/` and the 31k-line spec into `docs/`** (§19.6).
9. **Rewrite `roadmap.md`** to match §6; remove duplicated flow blocks; mark the wave-era backend
   section superseded.
10. **Then** routing, then the DB mapping, then the §9/§10 backlog.

---

## 23. Still open

1. **Razorpay** — deferred, not answered.
2. **Blaze plan** — upgrade, or use the already-registered Supabase bridge? (§17)
3. **Which flows ship first?** Asked four times.
4. **The 31k-line spec** — where does it live outside the Lovable chat?
5. **The ~40 unread local `.md` files** — §18. The six already read overturned two findings.
6. **Route bodies** — 0 of 205 opened. Four of your constraints stay unverifiable (§9).

---

## Appendix A — actual database schema

Live `public` schema, measured. Two tables, RLS on both.

**`profiles`** — PK `id` → `auth.users(id)` CASCADE. `username` (unique), `display_name`,
`headline`, `bio`, `city`, `state`, `country`, `roles[]`, `instruments[]`, `genres[]`,
`languages[]`, `specialisations[]`, `work_modes[]`, `intents[]`, `contact_channels[]`,
`experience_level`, `avatar_url`, `availability`, `visibility` (default `public`), `links` jsonb,
`discovery_prefs` jsonb, `onboarding_step`, `onboarding_draft` jsonb, `onboarding_complete`,
`created_at`, `updated_at`. Policies: public SELECT; owner INSERT/UPDATE on `auth.uid() = id`.

**`portfolio_items`** — PK `id` uuid, `user_id` → `auth.users(id)` CASCADE. `title`, `kind`
(default `track`), `description`, `role`, `collaborators[]`, `cover_url`, `media_url`,
`release_year`, `published`, `sort_order`, `created_at`, `updated_at`. Index `(user_id,
sort_order)`. Policies: public SELECT where `published = true`; owner full CRUD.

**Functions/triggers:** `update_updated_at_column()` on both; `handle_new_user()` SECURITY DEFINER
on `auth.users` INSERT; EXECUTE revoked from `anon`/`authenticated`/`PUBLIC`.

**Migrations:** `20260902172915` (initial) · `20260902172936` (revoke) · `20260902185426`
(16 profile columns).

---

## Appendix B — source inventory

**Read in full:** `roadmap.md` · `README.md` · `AGENTS.md` · `src/routes/README.md` · 3 migrations ·
`src/styles.css` · `types.ts` · `profile-map.ts` · `src/data/reviews.ts` · `package.json` ·
`ui.tsx` · `kit.tsx` · `cards.tsx` · 9 of 13 `.lovable/plan/*.md` · `governance.md` · `CLAUDE.md` ·
`hardcoded-content-audit.md` · `MASTER-REGISTRY.md` §§1–2 · `Modules_Map.md` ·
`Edge_Functions_And_Errors.md` (partial) · **all 6 `frontend/guide/rules/*`** ·
`frontend/guide/README.md` · `design-system-rebuild-plan.md` (partial) · 8 user prompts

**Extracted, not read linearly:** all 110 chat messages · all 38 non-empty prompts ·
`FLOW-SPECS.md` (structure only) · all 46 commit records

**Diffs read in full:** `5129664` (8 Sep) · `d10513f` (10 Sep)

**Not read:** ~40 of 63 project `.md` (notably `PRODUCT-SPEC.md`, `docs/09-Roadmap/` ×3,
`admin/docs/architecture/` ×9, `frontend/guide/INDEX.md`, `app-audit.md`) · all 205 route bodies ·
45 Radix primitives · 21 of 22 `src/data/*` · 44 of 46 diffs · **18 empty user messages
(unreadable)** · the interior of 54 assistant messages · `tokens/tokens.json` itself
