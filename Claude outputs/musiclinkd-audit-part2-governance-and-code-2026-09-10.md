# MusicLinkd — Audit Part 2: The Backup Repo, Governance Compliance, and Code Review

**Date:** 10 September 2026
**Supersedes nothing in Part 1 — this is the layer Part 1 was missing.**

Part 1 audited the Lovable repo's *plans*. It never opened a component file, and it never
opened the local repo. Both of those gaps are closed here.

---

## 0. Direct answer

**No — I had not read the backup.** Part 1 read the 13 `.lovable/plan/*.md` files in the Lovable
repo and nothing else. The local folder `~/Documents/GitHub/musiclinkd` was connected on Sep 3 and
never opened.

It has now been read.

| | Count |
|---|---|
| `.md` files, total | 1,943 |
| …vendored (`node_modules`, Pinterest Gestalt clone) | ~1,880 |
| **Project-owned `.md` files** | **63** |
| Largest | `docs/04-Wireframes/FLOW-SPECS.md` — **14,439 lines** |

**Local repo state:** branch `design-system/audit-fix-set`, last commit `f1419f2`,
**dated 2026-09-01** — nine days stale. Clean tree.

**And: `.lovable/plan/` does not exist in the backup.** No `.lovable` directory anywhere in the
local repo. The 13 planning documents exist in exactly one place — the Lovable repo — and nothing
in the local repo mirrors them. See §6.

---

## 1. There are three MusicLinkds, not two

Part 1 said two. That was wrong — it was reasoning from Notion page titles, not from the filesystem.

| # | Product | Stack | Where documented | State |
|---|---|---|---|---|
| 1 | **Android app** | Kotlin / Jetpack Compose + Firebase `musiclinkd-7ae30` | `docs/04-Wireframes/MASTER-REGISTRY.md` — 24 flows, 188 screens, 563 states | The audited product. Registry says most flows are `app-mocked` or `missing` |
| 2 | **Admin + website + guide** | Next.js admin, static site, Gestalt design system | `admin/docs/architecture/*` (10 docs), `frontend/guide/*` | Admin CMS is real and working; static site never reads Firestore |
| 3 | **Lovable web app** | TanStack Start + Supabase `gsqeymhtyzepwcnorjun` | `.lovable/plan/*` only | 205 routes, 2 tables |

**Product 3 is a re-implementation of the same 24-flow brief in a different stack, and it does not
know products 1 and 2 exist.** No file in the Lovable repo references Firebase, the Master
Registry, `governance.md`, or the Kotlin app.

---

## 2. The canonical spec was in the backup the whole time

`docs/04-Wireframes/MASTER-REGISTRY.md` is the authoritative inventory:

> *"all 24 flows, 188 screens, all 34 schema types, all 27 widgets… real and audited, proposed
> and unbuilt, side by side, clearly marked which is which."*

It also settles the count dispute directly:

> *"The screen list below was written when there were 135 screens. There are now **188**, across
> the same 24 flows… The registry grew 135 → 188 screens and 286 → 563 declared states."*

**So: 188 screens / 563 states is the Android registry. 205 routes is the Lovable app.** They are
not two counts of the same thing and should never have been compared. Different products.

The registry names its own currency rule, which is worth keeping: *"The live count is whatever
`node scripts/audit-screens.mjs` prints. If it disagrees with this file, the audit is right and
this file is behind."*

### Flow coverage: Lovable vs. the 24-flow canon

| # | Canonical flow | Registry status (Android) | In Lovable? |
|---|---|---|---|
| 1 | Auth | partial | ✅ |
| 2 | Onboarding | partial | ✅ **persisted** |
| 3 | Add Portfolio | app-mocked | ✅ **persisted** |
| 4 | Discover | app-mocked | ✅ |
| 5 | Opportunities | **missing** | ✅ (mock) |
| 6 | Communication & Collaboration | partial | ✅ (mock, 30 routes) |
| 7 | Marketplace | app-mocked | ✅ (mock) |
| 8 | Booking | partial | ✅ (mock) |
| 9 | *(retired — merged into 7)* | — | n/a |
| 10 | Profile Management | partial | ✅ |
| 11 | Services & Availability | partial | ✅ (mock) |
| 12 | Business Profiles | partial | ✅ (mock) |
| 13 | Notifications | app-mocked | ✅ (mock) |
| 14 | Public Profile | partial | ✅ `/profile/$username` |
| 15 | Band | partial | ✅ (mock) |
| 16 | Verifications | app-mocked | ✅ (mock) |
| 17 | Reviews | app-mocked | ✅ (mock) |
| 18 | Subscription | **missing** | ✅ (mock) |
| 19 | Search | app-mocked | ✅ `/search` |
| 20 | Recommendation Engine | **missing** | ✅ (mock) |
| 21 | Settings & Support | **missing** | ✅ (mock) |
| 22 | CMS Pages | partial | ✅ (mock) |
| 23 | QR Code & Sharing | **missing** | ✅ (mock, 13 routes) |
| 24 | Saved & Favorites | app-mocked | ✅ `/saved` |

**No flow is missed.** All 23 live flows have Lovable surface. Lovable has, in screen terms,
*out-built* the Android app — it designed flows 18, 20, 21 and 23 that the registry lists as
`missing` entirely.

**But the registry's status column and my Part 1 findings are the same sentence twice.**
`app-mocked` is defined there as *"UI exists, zero Firestore wiring."* Substitute Supabase for
Firestore and that is precisely the Lovable app's condition across 18 of 20 flows.

**The same failure has now been reproduced in a second stack.** That is the finding that matters
more than any individual bug in this document.

---

## 3. `governance.md` exists, is authoritative, and the Lovable app violates it

`governance.md` — *"Authoritative Rules for Android Jetpack Compose, **Web**, and Admin Portal"* —
is explicitly cross-platform. Nothing in the Lovable repo references it.

### Violation 1 — emoji as icons (§3, the most explicit rule in the file)

> **Prohibition**: Never use emoji as icons, colorful junkie icons, FontAwesome multi-color icons,
> or mixed icon sets across any component or screen. **Lucide only. One color per icon. Period.**

`src/components/ml/ui.tsx` and `cards.tsx`:

| Glyph | Component | Should be |
|---|---|---|
| `⌕` | `SearchBar` | `<Search />` |
| `🎙` | `SearchBar` | `<Mic />` |
| `←` | `TopBar` back | `<ArrowLeft />` |
| `✓` | `VerifiedBadge` | `<BadgeCheck />` |
| `📍` | `TalentRow` | `<MapPin />` |
| `♡` | `TalentRow` save | `<Heart />` |

**This is the cause of the empty boxes in your live app.** The Lovable project screenshot renders
tofu where the search icon, mic and category icons should be. `lucide-react` is already a
dependency and already used for `BottomNav`, `Row`, `Stars`. These six just don't use it.

CLAUDE.md flags this same disease in the *other* repo: *"the original `styleguide.html` (old, has
known issues — duplicate token systems, **unenforced emoji-vs-icon rule**, duplicated sections)."*
**The rule is unenforced in both codebases.**

### Violation 2 — no focus states (§6.4 and §9.3)

> §6: Every component must define 6 explicit interaction states… **4. Focused → `2px solid var(--pink)`**
> §9.3: All interactive controls must render a visible `2px` focus ring on keyboard tab navigation.

Zero `focus-visible:` rules across `ui.tsx`, `kit.tsx`, `cards.tsx`. `Btn` implements Default,
Pressed (`active:scale-[0.98]`) and Disabled — and skips Focused and Error entirely. `Chip`,
`Segmented`, `Row`, `SearchBar`, `BottomNav` links: nothing.

Minor sub-violation: §6.5 specifies disabled `opacity: 0.4`; `Btn` uses `disabled:opacity-60`.

### Violation 3 — typography scale ignored (§4)

Governance defines a full 8-property Compose scale (`displayLarge` 57sp → `labelSmall` 11sp,
Inter Tight / Inter). `styles.css` correctly sets `--font-sans: Inter` and
`--font-display: Inter Tight`. Then every size is an arbitrary value:

```
text-[10px] text-[11px] text-[12px] text-[13px] text-[15px] text-[17px] text-[19px] text-[22px]
```

Eight ad-hoc sizes across three files. No token, no scale, no mapping to the governed ramp.

> This is the **same font-sizing problem** already logged against the design-system repo. It has
> been carried into the new stack unchanged.

### Violation 4 — radius tokens defined then bypassed

`styles.css` defines `--radius-sm/md/lg/xl/2xl` = 8/16/24/32/40px. The components use
`rounded-[24px]`, `rounded-[20px]`, `rounded-t-[32px]`, `rounded-3xl`. **`20px` is not on the
scale at all.** Four mechanisms, one of them off-system.

### Violation 5 — touch targets below 48dp (§1.2)

> All interactive elements MUST satisfy a minimum touch target of **48dp × 48dp**.

- `Btn` — `min-h-12` = 48px ✅
- `TalentRow` "View profile" link — `min-h-11` = **44px** ❌
- `TalentRow` save button — `h-11 w-11` = **44 × 44px** ❌
- `Chip` / `Segmented` — `py-2` + `text-xs` ≈ **32px tall** ❌

### Violation 6 — CHANGELOG discipline (§8)

> Every commit MUST include a corresponding entry in `CHANGELOG.md`… applies to all repos.

The local repo has `CHANGELOG.md` (63 lines). **The Lovable repo has none.** Every one of the ~70
routes added in the last eight days landed without a changelog entry.

### What governance vindicates

**The pink is correct.** §2 names `#EA4C89`, `#0D0B26`, `#FCFCFD`, `#ECECF1` as the brand palette —
exactly what `styles.css` implements. My Part 1 note that `#EA4C89` "is widely recognised as
Dribbble's brand pink" and might be an inherited default was wrong to imply doubt: it is the
governed brand colour, documented in the authoritative rules file.

**Which sharpens Part 1's real finding.** The Lovable `README.md` still ships the `#5B4BFF` /
16px / Google Sans brief. That brief contradicts the project's own governance. It is not a
harmless stale file — it is the first thing an agent reads in that repo, and it points away from
the governed system.

---

## 4. Code review — `ui.tsx`, `kit.tsx`, `cards.tsx`

Three files read in full. Everything below is a defect, not a preference.

### 4.1 Bottom-nav active state is broken on ~200 of 205 routes

```js
const active = path === item.to;
```

Exact string match. `/marketplace/search`, `/marketplace/rent/index`, `/profile/rahul`,
`/messages/anything` — none highlight their tab. Only the five exact paths ever light up.

**Fix:** prefix match, longest-wins, with `/` special-cased so it doesn't match everything.

### 4.2 Three duplicate component pairs

| Pair | Files | Evidence |
|---|---|---|
| `AvailabilityBadge` / `StatusPill` | `ui.tsx` / `kit.tsx` | Identical markup, parallel tone maps. `kit.tsx` comments **"One status system across every flow"** directly above the second one |
| `Chip` / `Segmented` | `ui.tsx` / `kit.tsx` | Byte-identical active/inactive class strings |
| `Sheet` / `Dialog` | `ui.tsx` / `kit.tsx` | Both hand-rolled, both `if (!open) return null`, both `bg-navy/50 backdrop-blur-[2px]` |

### 4.3 Four accessible modal implementations ship unused

`Sheet` and `Dialog` are hand-built with **no focus trap, no Escape handler, no scroll lock, no
`aria-labelledby`**. Meanwhile `components/ui/dialog.tsx`, `sheet.tsx`, `drawer.tsx` and
`alert-dialog.tsx` — Radix, accessible, already installed — sit unused in the repo.

### 4.4 Typed routing disabled at the highest-traffic link component

```js
// eslint-disable-next-line @typescript-eslint/no-explicit-any
<Link to={to as any} {...(params ? { params: params as any } : {})} />
```

`Row` is the standard navigation primitive. TanStack Router's compile-time route checking — the
main reason to use it — is switched off for every `Row` in the app. With 205 routes, this is
exactly where dead links survive undetected.

### 4.5 Display copy used as a style lookup key

```js
const map = { Available: {...}, Selective: {...}, Booked: {...} };
const c = map[status] ?? map["Booked"]!;
```

`AvailabilityBadge` keys styling off user-visible text. Change the copy, or translate it, and every
badge silently falls back to "Booked" grey. Should key off a union (`"available" | "selective" |
"booked"`) with the label as a separate display concern.

### 4.6 Two styling mechanisms interleaved

`className` for most things, then inline `style={{ background: "var(--…)" }}` for
`BottomNav` active pill, `AvailabilityBadge`, `StatusPill`, `Meter`, `TalentRow` language chip.
Inline styles can't be overridden by class, don't participate in the cascade, and can't be linted
for token compliance. Pick one.

### 4.7 The 430px shell is a magic number in three places

`max-w-[430px]` is repeated in `Screen`, `BottomNav`, `Sheet`, `Dialog`. Paired with `pb-24`
clearance against an `h-16` nav plus safe-area inset — coupled constants that will drift.

Note: governance §1.1 sets the baseline at **360dp**, and §1.3 requires desktop layouts to wrap
into a responsive grid rather than a single fixed column. The Lovable app pins one 430px column at
every width. Whether that's acceptable is a product decision, but it is a documented divergence.

### 4.8 Smaller items

- `Meter` — `pct` is unclamped; `value > max` overflows the bar.
- `Stars` — `Math.round(4.4)` renders 4 stars beside the label "4.4", and there is no half-star.
- `Btn` — `styles: Record<string, string>` discards the variant union; `styles[variant]` is
  `string | undefined` at type level.
- `SearchBar` — Discover-specific copy hardcoded as the default placeholder of a generic
  component; the `<input>` has no associated label.
- `TalentCard` — `w-[172px]` and `h-[150px]` hardcoded.

---

## 5. What the backup already knew

`guide/docs/hardcoded-content-audit.md` — written for the Kotlin app — sets out the exact
three-way split this project keeps failing to make:

> 1. **UI copy** — labels, headings, button text → `strings.xml`
> 2. **Seed/demo content** — artists, studios, tracks, feed posts → `DemoDataProvider`
> 3. **Runtime content** — profile names, counts, bookings → application state/repository;
>    **never fallback to invented user data in a production screen**

Its closing rule: *"Do not add user-visible literals directly inside Compose UI."*

**The Lovable app collapses all three categories into one.** `src/data/*.ts` mixes UI copy
(`reviewPolicy`, `flagReasons`), seed content (`receivedReviews`) and would-be runtime data
(`ratingSummary`) in single modules — and screens read all of it identically, so there is no seam
along which to replace the runtime third with real queries.

**The taxonomy for fixing this was written weeks ago and is sitting in the other repo.**

---

## 6. Risk: the plan history has no second copy

`FLOW-SPECS.md` opens with a warning learned the hard way:

> *"Recovered from session transcripts and committed on 2026-08-31: until then they existed only
> in `~/.claude/projects/` and were repeatedly lost to context compaction."*

The 13 `.lovable/plan/*.md` documents are in the identical position now: they exist **only** in the
Lovable repo, mirrored nowhere, and they are the sole written record of why 18 flows were built
design-only. They should be copied into `docs/` in the local repo, exactly as `FLOW-SPECS.md` was.

---

## 7. What Part 1 got wrong

1. **"Two MusicLinkds."** Three. Part 1 inferred from Notion titles instead of reading the repo.
2. **Doubt about the pink.** It is the governed brand colour (`governance.md` §2). Part 1 raised
   it as a possible inherited default. It isn't.
3. **Part 1 had no code in it.** It audited plans and route filenames and presented that as an
   audit. §3 and §4 here are the part that was missing.

---

## 8. Revised recommendations

Replacing Part 1 §11, in order:

1. **Point the Lovable repo at `governance.md`.** It is authoritative for web by its own header,
   and the app has never seen it. Copy it in, or reference it from project knowledge.
2. **Replace six emoji with six Lucide imports.** One file, thirty minutes, fixes the visible
   defect on your live app and closes the oldest unenforced rule in the project.
3. **Fix the bottom-nav prefix match.** Four lines. Currently broken on ~200 routes.
4. **Fix `README.md`** in the Lovable repo — it contradicts `governance.md` and is read first.
5. **Mirror `.lovable/plan/` into `docs/`.** §6.
6. **Decide Razorpay.** Unchanged from Part 1 — still the oldest open decision, still blocking.
7. **Then** schema. And when it happens, `MASTER-REGISTRY.md` §3 already lists 34 Firestore
   document types that were modelled for these same flows. Read that before designing Postgres
   tables from scratch — the domain modelling is done, only the dialect is different.

---

## 9. Open questions — unchanged, still unanswered

1. **Which flows ship first?** Asked three times. Nothing else can be sequenced without it.
2. **Razorpay: yes or no?**
3. **Is the Kotlin/Firebase app still alive**, or has the Lovable app replaced it? The local repo's
   last commit is Sep 1; Lovable's is Sep 10. If product 1 is dead, `governance.md`, the Master
   Registry and 34 modelled schema types should be migrated, not abandoned.
4. **Which product do the Notion pages describe going forward?** They currently document product 1.

---

### Method note

Read in full: `governance.md`, `CLAUDE.md`, `guide/docs/hardcoded-content-audit.md`,
`MASTER-REGISTRY.md` §§1–2, `src/components/ml/ui.tsx`, `kit.tsx`, `cards.tsx`.
Structure extracted (not read in full): `FLOW-SPECS.md` (14,439 lines — a verbatim dump of two
chat sessions, not a structured spec).
Not read: the 10 `admin/docs/architecture/*` documents, `docs/09-Roadmap/*` (4 plans),
`frontend/guide/rules/*` (6 rule files), `PRODUCT-SPEC.md`, `webapp/README.md`.
**Roughly 40 of the 63 project `.md` files remain unread.** Say the word and I'll finish them —
`frontend/guide/rules/*` and `docs/09-Roadmap/*` are the two sets most likely to contain further
binding rules the Lovable app is violating.
