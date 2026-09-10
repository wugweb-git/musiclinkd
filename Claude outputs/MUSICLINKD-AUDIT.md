# MusicLinkd — Consolidated Audit

**Date:** 10 September 2026
**Supersedes:** `musiclinkd-audit-2026-09-10.md` (Part 1), `…part2-governance-and-code…` (Part 2),
`…part3-chat-history…` (Part 3) and its addendum. Those four can be deleted.

**Audited:** Lovable project `ace967d9…` at commit `d10513fd`, its full 110-message chat and all
46 commits; the local repo `~/Documents/GitHub/musiclinkd` at `f1419f2`; the Notion MusicLinkd
workspace.

---

## 1. Verdict

**205 routes. 2 database tables. Three products sharing one name.**

The Lovable app has more screens than the Android app it was modelled on, and less persistence.
Two flows of twenty are real; eighteen are drawings that discard everything on reload. This was
not drift — every plan document since 2 September declares itself design-only. The wave plan that
would have fixed it was written, started, and silently abandoned after Wave 1.

Three decisions have been open since the first week of September and block everything downstream.
None are technical.

---

## 2. Method and coverage — read this before trusting a number

| Source | Coverage | Confidence |
|---|---|---|
| Lovable chat | **110 of 110 messages** retrieved; ~2.64M chars | Structure: complete. **Verbatim: ~0.8%** |
| Your prompts | 38 non-empty, 363,948 chars — all constraint lines extracted programmatically; 6 read in full | High on instructions |
| Your prompts, unreadable | **18 of 56 messages return empty** (attachment-only: all of Sep 4, 5, 6, 8, 9, 10) | **Zero** |
| Lovable's replies | 2.28M chars, 2,144 embedded tool-call blocks | Pattern-matched, not read |
| Commits | 46 of 46 metadata; **2 diffs read in full** | High on metadata |
| Local repo `.md` | 63 project files found; **~23 read**, ~40 unread | Partial |
| Component code | `ui.tsx`, `kit.tsx`, `cards.tsx`, `styles.css`, `profile-map.ts`, `types.ts`, 3 migrations | High on those files only |
| Route quality | **Not assessed.** Route *existence* verified from the file listing | None |

**What this means:** the timeline, the instructions you gave, the schema, and the design-system
defects are solid. Anything depending on the interior of a long Lovable reply, or on the contents
of a route file, is not.

**Largest single unread object:** one assistant message of 274,433 characters (Sep 2 18:06).

---

## 3. Three products, one name

| # | Product | Stack | Documented in | Status |
|---|---|---|---|---|
| 1 | **Android app** | Kotlin/Compose + Firebase `musiclinkd-7ae30` | `MASTER-REGISTRY.md` — 24 flows, 188 screens, 563 states | **Alive** (confirmed) |
| 2 | **Admin + website + guide** | Next.js admin, static site, Gestalt tokens | `admin/docs/architecture/*` (10 docs) | Admin CMS real and working |
| 3 | **Lovable web app** | TanStack Start + Supabase `gsqeymhtyzepwcnorjun` | `.lovable/plan/*` only | 205 routes, 2 tables |

**The Notion workspace documents products 1 and 2 exclusively.** Product Spec, Master Registry,
Architecture & Mapping, Deployment Plan — none describe the Lovable app. Nothing in the Lovable
repo references Firebase, the registry, or `governance.md`.

**Unanswered and now the top question:** what *is* the relationship? Prototype feeding Android,
parallel web client, or replacement in progress? It decides whether the Lovable schema mirrors the
Firestore model or deliberately diverges — and nothing in either repo says.

---

## 4. What is genuinely built

Verified from the route listing at `d10513fd`.

**Persisted (real):** Flow 01 Auth · Flow 02 Onboarding · Flow 03 Portfolio.
Real migrations, RLS, owner policies, signup trigger, server functions. `profile-map.ts` is
careful work — `draftToProfile` / `profileToDraft` / `profileGaps`, jsonb draft blob as recovery.
**This part is good.**

**Design-only (mock):** Flows 04, 05, 06, 07, 08, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22,
23, 24. Largest surfaces: Collaboration (30 routes), Marketplace + Rentals (21), Booking (19),
Business (14), QR/Identity (13), Opportunities (13).

**No canonical flow is missing.** All 23 live flows have surface. The Lovable app has *out-built*
the Android app — it designed flows 18, 20, 21 and 23 that the registry lists as `missing`.

---

## 5. The central failure

`musiclinkd-deepen-flows-01-14-to-full-spec-2026-09-02.md` named fourteen tables:

> `user_roles`, `saved_items`, `follows`, `opportunities`, `opportunity_applications`,
> `services`, `service_packages`, `availability_rules`, `availability_exceptions`,
> `bookings`, `booking_events`, `reviews`, `businesses`, `business_members`

**None exist.** Live `public` schema: `profiles`, `portfolio_items`. Nothing else.

The plan's own rule — *"Wave 1 first, since every later wave reads from the identity record"* —
was followed for Wave 1 and then abandoned. Waves 3–7 shipped screens, no data.

**The Master Registry describes the Android app in the same words:** `app-mocked` is defined there
as *"UI exists, zero Firestore wiring."* Swap Firestore for Supabase and that is the Lovable app
across eighteen flows.

**The same failure has been reproduced in a second stack, and the audit describing it was already
in the other repo.**

---

## 6. Instructions you gave that were not carried out

From the exhaustive constraint extraction across all 38 prompts.

### Asked three times, still violated

> *"Do not create duplicate versions of components that already exist"* (Aug 12)
> *"Do not duplicate existing MusicLinkd components"* (Aug 14)
> *"Do not create duplicate components that already exist in Gestalt"* (Aug 17)

Shipped anyway: `AvailabilityBadge`/`StatusPill` (identical markup, parallel tone maps — and
`kit.tsx` comments *"One status system across every flow"* directly above the second one);
`Chip`/`Segmented` (byte-identical class strings); `Sheet`/`Dialog` (both hand-rolled). Plus **45
Radix primitives installed and unused**, including four accessible modal implementations while the
two hand-rolled ones have no focus trap, no Escape handler, no scroll lock.

### Never acted on

| Instruction | Date | State |
|---|---|---|
| *"Do not create custom loaders"* | Aug 17 | `kit.tsx` `Loading` is custom; `ui/skeleton.tsx` unused |
| *"Do not introduce Inter Tight, Inter, or another font merely because previous concept prompts used it"* | Aug 12 | `styles.css` uses both. **But `governance.md` §4 specifies Inter/Inter Tight** — your prompt and your governance file contradict each other and nobody reconciled them |
| *"Do not hardcode the colours from older prompts"* | Aug 12 | `README.md` still ships `#5B4BFF` |
| *"you can tilt towards nothing phone ui"* | Aug 12 | Never mentioned again by either side |
| *"ui reference were ignored.? why?"* | Aug 14 | Complaint logged once, never resolved |

### Screens specified and never built

- **SCREEN 08 — MAKE OFFER** and **JOURNEY 08 — OFFER** (Aug 12). No offer route exists anywhere.
  An entire specified journey is absent.
- **Saved Searches / Saved Sellers / Saved Services / Saved Studios** (Aug 12 §25). Only `/saved`
  and `/marketplace/saved` exist. Saved searches remains on the Sep 7 pending list — **26 days**.
- **Report Listing / Report Seller / Block Seller / Report Scam** (Aug 12 §33). No marketplace
  moderation routes. `reviews.$id.report` exists; marketplace has nothing.
- **Marketplace messaging with listing and offer cards in-thread** (Aug 12 §18). `/messages` is a
  single route with no thread context.

### Delivered, but very late

**SCREEN 17 — RECENTLY VIEWED**, asked 12 August. Shipped **10 September**. Twenty-nine days.

### Correctly honoured

*"remove (AI-ASSISTED DISCOVERY, MARKETPLACE ANALYTICS — SELLER)"* (Aug 14) — neither exists.
Retraction respected, and re-respected in the Sep 2 person-profile plan.

### Unverifiable without reading route bodies

*"Do not imply payment if payment infrastructure is not implemented"* · *"Do not imply payment has
occurred"* · *"Do not allow false confirmation"* · *"Do not fabricate availability"*.
`/booking/$id/pay` and `/plans/checkout` exist. Whether they imply completion is unknown — I did
not open them.

---

## 7. `governance.md` — authoritative, and violated

The file's own header: *"Authoritative Rules for Android Jetpack Compose, **Web**, and Admin
Portal."* Nothing in the Lovable repo references it. The Android app is alive, so this is live law.

| Clause | Rule | State |
|---|---|---|
| **§3** | *"Never use emoji as icons… **Lucide only. One color per icon. Period.**"* | `⌕ 🎙 ← ✓ 📍 ♡` shipped as text glyphs — **this is why your live app renders empty boxes.** `lucide-react` is installed and used elsewhere in the same files. Still happening in Sep 10 code (`discover.music.tsx` renders `workType(t.kind).emoji`) |
| **§6.4, §9.3** | Focused state = `2px solid var(--pink)`; visible focus ring on all controls | **Zero `focus-visible:` rules** across `ui.tsx`, `kit.tsx`, `cards.tsx` |
| **§4** | 8-property type scale, `displayLarge` 57sp → `labelSmall` 11sp | Eight arbitrary sizes: `text-[10px]…[22px]`. No token, no scale |
| **§1.2** | 48dp minimum touch target | `TalentRow` link and save button = 44px; `Chip`/`Segmented` ≈ 32px |
| **§8** | CHANGELOG entry mandatory per commit, *"applies to all repos"* | Lovable repo has **no CHANGELOG.md**. ~70 routes in 8 days, zero entries |
| **§2** | Brand palette `#EA4C89`, `#0D0B26`, `#FCFCFD`, `#ECECF1` | ✅ `styles.css` implements exactly this |

Radius tokens `--radius-sm/md/lg/xl/2xl` (8/16/24/32/40) are defined and then bypassed with
`rounded-[24px]`, `rounded-[20px]` (**not on the scale at all**), `rounded-t-[32px]`, `rounded-3xl`.

---

## 8. Code defects

**Bottom-nav highlighting is broken on ~200 of 205 routes.**

```js
const active = path === item.to;
```

Exact match only. `/marketplace/search`, `/profile/rahul`, `/messages/…` — none highlight.

**Typed routing disabled at the highest-traffic component.** `Row` casts `to` and `params` to
`any` behind an eslint-disable. TanStack Router's compile-time route checking — the main reason to
use it — is off for every `Row` in the app. With 205 routes, that is where dead links live.

**Display copy used as a style key.** `AvailabilityBadge` does `map[status] ?? map["Booked"]`,
keyed on user-visible text. Reword or translate it and every badge silently turns grey.

**Dead controls ship as working ones.** `recently-viewed.tsx` "Clear history" and `cards.tsx`
"Message" — both `<Btn>` with no `onClick`. Both from commits in the last 48 hours.

**Deprecated route wired into brand-new code.** `discover.nearby.tsx` and `recently-viewed.tsx`
(Sep 10) both link `/talent/$id`, which `roadmap.md` has scheduled for redirect since Sep 2.

**Two styling mechanisms interleaved.** Tailwind classes for most things, inline
`style={{ background: "var(--…)" }}` for `BottomNav`, `AvailabilityBadge`, `StatusPill`, `Meter`,
`TalentRow`. Inline styles can't be overridden by class or linted for token compliance.

**Smaller:** `Meter` pct unclamped (overflows above `max`) · `Stars` rounds 4.4 to 4 stars beside
the label "4.4" · `Btn` types variants as `Record<string,string>`, discarding the union ·
`SearchBar` hardcodes Discover copy as a generic component's default and has no input label ·
`max-w-[430px]` repeated in four components.

---

## 9. The type-error thread — 24 days, four attempts, worse than at the start

| When | Event |
|---|---|
| **Aug 17** | Lovable: *"blocked on three type errors that were reported just as I stopped"* |
| **Sep 1 06:46** | You paste them: `profile.tsx(75,10)`, `(185,12)`, `(810,9)` |
| **Sep 1 07:30** | You paste **the identical blocked-state text again** |
| **Sep 2 17:33** | You paste it **a third time** |
| **Sep 2 18:27** | You paste 6 × `error TS` — now `identity.ts` too |
| **Sep 10** | You paste `person-profile.tsx` — **10 × `exactOptionalPropertyTypes`** |

Four "fix" commits landed in between. **It started as 3 errors and is now 10.**

**It was never eleven bugs — it is one convention error at every call site.**
`exactOptionalPropertyTypes: true` rejects an explicitly-passed `undefined`. Every primitive in
`ui.tsx`/`kit.tsx` declares `onClick?`, `tone?`, `cta?`, `back?`, `onMore?` the narrow way; every
caller spreads possibly-undefined values in. Fixing call sites guarantees recurrence with the next
batch of routes.

**Fix at source:** declare `onClick?: (() => void) | undefined` on the primitives. Two files.
`roadmap.md` still lists this under **Next**, correctly — the commit log does not.

---

## 10. Commit messages do not describe their commits

Two of two sampled diffs were wrong — not vague, **wrong**.

| Commit | Message | Actual contents |
|---|---|---|
| `5129664` Sep 8 | **"Added hidden footer icon"** | 10 new business/services routes. No footer, no icon, nothing hidden |
| `d10513f` Sep 10 | **"Added audio autoplay"** | 5 new discover routes. No audio, no autoplay |

Also in the log: `Update plan` ×8, `Added the requested changes`, `Updated goal label in DB`, and
**`Fixed all bugs and errors`** (Sep 2 18:17) — eleven days before you pasted ten unfixed errors
from that codebase.

**On your clandestine-edit question:** I read both diffs. Nothing hidden was inserted. But `git log`
is not a usable record of this repo — anyone auditing by reading it will be misled.

---

## 11. Three decisions that were dropped, with timestamps

### Razorpay — 1 September, 07:14

Lovable answered correctly and completely: Stripe unavailable for Indian sellers, Paddle
ineligible, Razorpay recommended, *"Which would you like to go with?"*
**Your next message, one minute later, was the FLOW 02 onboarding prompt.** Never answered.
Blocks four plan documents and the entire booking/monetisation spine.

### "Add a real backend" — 1 September, 17:33

You wrote: *"**Missing flows**: Other: Follow [Notion Architecture & Mapping link] / **Priority**:
Finish the open ones / **Scope**: Add a real backend."*

Lovable made six tool calls, produced **no text and no commit**, and never said the link was
unreadable. **The Notion connector did not exist until Sep 2 17:27** — a day later. A day was lost
and the instruction went with it, until you re-litigated it on Sep 2 with *"flow 1-14 you did were
justr a tip on the ice."*

**And per your own correction: the real backend already exists at admin.musiclinkd.com.** So the
instruction never meant "design a schema" — it meant "connect to what we have." Which nobody did,
and which reframes §5 entirely (see §12).

### The wave plan — 2 September, 19:09

Wave 1 landed with real migrations at 19:05. At **19:09** you wrote *"now continue with remainign
screens and flows."* Every subsequent plan is design-only. Nobody flagged that the wave plan had
been superseded — including the agent that wrote it 30 minutes earlier.

---

## 12. The backend that already exists — and why it isn't reachable

From `admin/docs/architecture/`:

- **16 canonical Firestore collections**, with a working Next.js admin panel. `verification_requests`,
  `reviews` and `page_content` plus their admin modules are real and working.
- **The Android app touches 3 of them consistently** — `users`, `connections`, `feed_posts` — plus
  an ad-hoc `musicians` collection keyed by *slugified display name, not the Auth UID*.
- `studios`, `projects`, `marketplace_items`, `reviews`, `studio_bookings`, `chat_threads` are
  *"never written or read by the app despite full-featured screens existing for all of them."*

**The blocker is specific and has nothing to do with Lovable:**

- `musiclinkd-7ae30` is on Firebase's free **Spark plan**. Cloud Functions cannot deploy at all —
  a live `firebase deploy --only functions` was attempted and failed before deploying anything.
- `backend/{functions,api,webhooks,auth,database,storage}/` held only `.gitkeep`. **No server-side
  trust boundary exists anywhere** — booking amounts, conflicts and payment authorisation all ran
  client-side.
- **A free-tier Supabase project is already registered in your `wugweb` org** as an Edge Functions
  bridge (credentials in `admin/.env.local`), deferred because it needs a hand-generated Firebase
  service-account key. That is **not** the Lovable Supabase project.

**So the schema question is really an integration question:** does the Lovable app read Firestore
directly under rules, or go through the half-provisioned Supabase bridge? Either way,
**Blaze-vs-Spark is the gate**, and it has been sitting under this since August.

**Building 14 Postgres tables would fork the domain model across two live products.** The registry
already has 34 document types modelled for these same flows.

---

## 13. Corrections — what earlier drafts of this audit got wrong

1. **"Two MusicLinkds."** Three. I inferred from Notion page titles instead of reading the repo.
2. **"The pink drifted accidentally."** No — `styles.css` documents it and `governance.md` §2
   mandates it. I raised doubt where there was none.
3. **"I can apply the schema directly via `query_database`, no agent needed."** Technically true,
   practically wrong: `supabase/migrations/` is tracked in the repo, so direct DDL would silently
   desync the live schema from the migration history. I missed the folder because it sits past the
   third page of the file listing.
4. **"Design 14 Postgres tables."** Wrong — a live backend and domain model already exist (§12).
5. **"Real backend was decided Sep 2 18:38."** It was asked for **Sep 1 17:33** and dropped (§11).
6. **Three separate audit documents.** Wrong shape; each of your requests widened scope and I wrote
   a new file instead of revising. This document replaces all of them.

---

## 14. Do these, in this order

1. **Answer Razorpay.** One word, open since 1 September, gates the revenue spine.
2. **Decide what the Lovable app *is*** relative to the Android app (§3). It determines whether the
   next step is a schema or an integration — and no schema work should start before it.
3. **Fix the optional-prop convention at source.** Two files, ten minutes, ends a 24-day recurring
   failure permanently (§9).
4. **Replace six emoji with six Lucide imports.** One file. Fixes the visible defect on your live
   app and closes the oldest unenforced rule in the project (§7).
5. **Fix the bottom-nav prefix match.** Four lines; currently broken on ~200 routes (§8).
6. **Write Lovable project knowledge — it is empty**, and so is the workspace-skills list. That
   absence is why every flow since Sep 2 was built design-only without anyone re-deciding it. Bind
   `governance.md`, ban emoji glyphs and `onClick`-less buttons, require `| undefined` on optional
   props.
7. **Fix `README.md`** in the Lovable repo — it contradicts `governance.md` and is the first file
   an agent reads there.
8. **Mirror `.lovable/plan/` into `docs/`.** Those 13 documents exist in one place only.
   `FLOW-SPECS.md` opens by describing how this class of file was already lost once:
   *"until then they existed only in `~/.claude/projects/` and were repeatedly lost to context
   compaction."*
9. **Rewrite `roadmap.md`** so checkboxes match §4; remove duplicated flow blocks (FLOW 11/12/13/21
   appear once unchecked and again immediately below, checked); mark the wave-era backend section
   superseded.
10. **Then** the §6 missing screens and the Sep 7 pending list.

---

## 15. Still open

1. What is the Lovable app *for*, relative to the live Android app? **(blocks everything)**
2. Razorpay: yes or no?
3. Blaze plan: upgrade, or route through the already-registered Supabase bridge?
4. Which two or three flows ship first? *Asked four times, still unanswered.*
5. Do the Notion pages start covering product 3, or stay Android-only?

---

## Appendix — actual database schema

Live `public` schema. Two tables, RLS on both.

**`profiles`** — PK `id` → `auth.users(id)` CASCADE. `username` (unique), `display_name`,
`headline`, `bio`, `city`, `state`, `country`, `roles[]`, `instruments[]`, `genres[]`,
`languages[]`, `specialisations[]`, `work_modes[]`, `intents[]`, `contact_channels[]`,
`experience_level`, `avatar_url`, `availability`, `visibility` (default `public`), `links` jsonb,
`discovery_prefs` jsonb, `onboarding_step`, `onboarding_draft` jsonb, `onboarding_complete`,
`created_at`, `updated_at`.
Policies: public SELECT; owner INSERT/UPDATE on `auth.uid() = id`.

**`portfolio_items`** — PK `id` uuid, `user_id` → `auth.users(id)` CASCADE. `title`, `kind`
(default `track`), `description`, `role`, `collaborators[]`, `cover_url`, `media_url`,
`release_year`, `published`, `sort_order`, `created_at`, `updated_at`.
Index `(user_id, sort_order)`. Policies: public SELECT where `published = true`; owner full CRUD.

**Functions/triggers:** `update_updated_at_column()` on both; `handle_new_user()` SECURITY DEFINER
on `auth.users` INSERT, seeding a profile row; EXECUTE revoked from `anon`/`authenticated`/`PUBLIC`.

**Absent:** all fourteen tables listed in §5.
