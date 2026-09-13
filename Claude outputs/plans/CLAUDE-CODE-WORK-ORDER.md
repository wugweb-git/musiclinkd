---
doc: CLAUDE-CODE-WORK-ORDER
version: 1.1
status: PARTLY STALE
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
folder: plans
originally: CLAUDE-CODE-WORK-ORDER.md
---

> **PARTLY STALE** · v1.1 · 2026-09-13. Executable phase plan. Appendix A was a second copy of AGENTS.md and is deleted. Phases 1, 2 (ml/ only) and 7 are DONE. Phase 8 is priced at ~97h.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

# MusicLinkd — Claude Code Work Order

**Repo:** `git@github.com:wugweb-git/musiclinkd-9e6ca875.git`
**Baseline commit:** `d10513fd955d6593466c383f2b9020bd5ac509f3`
**Prepared:** 10 September 2026 · derived from `MUSICLINKD-LOVABLE-AUDIT.md`

---

## 0. Read this first

This repo is a Lovable project with **two-way GitHub sync**. Commits pushed to the tracked branch
appear in the Lovable editor and rebuild the live preview. That makes ordinary git mistakes
expensive in ways they normally aren't.

**Non-negotiable:**

- **Never force-push. Never rebase, amend or squash anything already pushed.** It rewrites history
  on Lovable's side and can destroy the project's history.
- **Lovable tracks one branch only** (default `main`). Work on a feature branch is invisible to
  the editor until merged.
- **One writer at a time.** Confirm the Lovable agent is idle before pushing. Concurrent writes
  create a `lovable-sync` branch holding your rejected changes — silently.
- After every push to `main`: `git fetch origin && git branch -r | grep lovable-sync`. If it
  exists, merge it before continuing.
- Every commit ends with `Co-Authored-By: Wugweb <admin@wugweb.com>` and updates `CHANGELOG.md`.

**Context you need:** this app is the *only* MusicLinkd frontend — it replaces a Kotlin/Compose
Android app being retired. Nothing here is a prototype. 207 routes exist; only 2 database tables
do (`profiles`, `portfolio_items`). Auth, onboarding and portfolio persist; every other flow is
seeded mock data **by decision** — do not "fix" that, it is scheduled after the refactor.

**The core finding driving this work order:** the app was built against a *superseded* version of
the design system. The system was reconciled to v9 on 31 August; Lovable resumed work on
1 September and has never seen it. Most defects below are the previous generation of a rule,
correctly implemented.

---

## 1. Ground rules for every phase

Each phase is one branch, one PR, independently verifiable. **Do not batch phases.**

Exit gate for every phase — run them, and state plainly in the PR if you did not:

```sh
bun install
bunx tsc --noEmit
bun run lint
bun run build
```

Then a render pass at **412px** on every touched screen. Every screen must keep its loading,
empty, error and offline states.

**Do not** change behaviour while refactoring. **Do not** add features. **Do not** touch the
database. **Do not** edit `src/routeTree.gen.ts` (generated). **Do not** create `src/pages/` or
`app/layout.tsx` — this is TanStack Start, flat dot-named routes only.

If something in this order turns out to be wrong, **stop and say so** rather than working around
it. Silent workarounds are how this codebase got here.

---

## PHASE 1 — Rules and docs land first

**Branch:** `chore/agents-and-docs` · **Risk:** none · **Est:** 30 min

The rules must exist in the repo before the code they govern changes.

### 1.1 Create `AGENTS.md`

Full content: see `AGENTS.md` at the repo root (mirror in `reference/AGENTS.md`). **Preserve the existing `<!-- LOVABLE:BEGIN -->…<!-- LOVABLE:END -->`
block byte-for-byte at the top** — Lovable manages it and may rewrite it.

### 1.2 Create `CHANGELOG.md`

It does not exist. Governance requires an entry per commit. Seed it:

```md
# Changelog

All notable changes to this project are documented here.
Format: [Keep a Changelog](https://keepachangelog.com/). 

## [Unreleased]
### Added
- `AGENTS.md` — GitHub↔Lovable sync protocol and code rules
- `CHANGELOG.md`
```

### 1.3 Fix `README.md`

It currently ships the **superseded** brief and is the first file any agent reads. Remove the
pasted prompt block wholesale — specifically every occurrence of:

- `Primary: #5B4BFF` and `Secondary: #8B5CF6`
- `Radius: 16px`
- `Typography: Google Sans Roboto`
- `Material Design 3`

Replace with a short factual header: what the app is, the stack, where the live preview is, a
pointer to `AGENTS.md` for rules, and the current palette (`#EA4C89` brand, `#0D0B26` navy).
Keep Lovable's own "Build with Lovable" section.

**Acceptance:** `grep -rn "5B4BFF\|Google Sans" README.md` returns nothing.

---

## PHASE 2 — Design tokens (the keystone)

**Branch:** `refactor/design-tokens` · **Risk:** medium — visual · **Est:** half a day

**Do this before anything else touches a component.** Every arbitrary value downstream exists
because these tokens are missing. Fix it last and every route gets edited twice.

### 2.1 `src/styles.css` — what is wrong

| Current | Required (v9) |
|---|---|
| `--radius: 24px` | On-scale value; cards use **32px** |
| `--radius-sm/md/lg/xl/2xl` = 8/16/**24**/32/**40** | Scale is **8 / 16 / 32 / pill**. 24 and 40 are not steps |
| Header comment *"shadows avoided"*; **no elevation tokens** | Cards lift with `--elevation-2`. Border is the *fallback* for cards on white |
| **No type scale at all** | Four fluid `clamp()` tokens: 10–12px, 14–18px, 20–28px, 28–44px |
| **No motion tokens** | `--duration-1..4` = **125 / 200 / 320 / 500ms**; three easings — standard, enter, spring |
| `--accent` mapped to purple | **Purple is badge-only.** Introduce `--status-category-bg` / `--status-category-text`; remove `--accent`'s purple role |
| No focus-ring token | `--focus-ring: 2px solid var(--primary)` at 2px offset |
| No mono family | `--font-mono: "JetBrains Mono"` for metadata |
| Spacing implicit (Tailwind default) | Declare **4 / 8 / 16 / 32**; opacity **0.08 / 0.16 / 0.32 / 0.64** |

Keep what is already correct: the oklch palette, `#EA4C89` primary, `--font-sans: Inter`,
`--font-display: Inter Tight`, `color-mix` containers, the semantic token names.

### 2.2 Migration mapping for downstream files

Apply mechanically once tokens exist:

```
rounded-[24px]   → rounded-lg      (32px card radius)
rounded-[20px]   → rounded-md      (16px) — off-scale, choose 16 not 32
rounded-t-[32px] → rounded-lg
rounded-3xl      → rounded-lg
text-[10px]      → type token 1
text-[11px]      → type token 1
text-[12px]      → type token 2
text-[13px]      → type token 2
text-[15px]      → type token 2
text-[17px]      → type token 3
text-[19px]      → type token 3
text-[22px]      → type token 4
```

**Do not guess where the mapping is ambiguous.** `text-[15px]` and `text-[17px]` straddle
token 2/3 — list them in the PR and ask rather than choosing silently.

### 2.3 Constants

`max-w-[430px]` is repeated in `Screen`, `BottomNav`, `Sheet`, `Dialog`, paired with `pb-24`
clearance against an `h-16` nav plus safe-area inset. Extract to one shared constant so they
cannot drift.

**Acceptance:** build clean; a visual diff at 412px on `/`, `/feed`, `/me`, `/marketplace`,
`/bands`; no `rounded-[` or `text-[` remaining in `src/components/ml/`.

---

## PHASE 3 — Kill the build errors at source

**Branch:** `fix/optional-prop-types` · **Risk:** low · **Est:** 30 min

**This is the highest-value change in the order.** The same error has been reported four times
across 24 days — 17 Aug (3 errors), 1 Sep ×2, 2 Sep, and 10 Sep (**10 errors**). Four "fix"
commits landed. It got worse each time because every fix patched call sites.

**Root cause:** `exactOptionalPropertyTypes: true` means `prop?: () => void` will **not** accept
an explicitly-passed `undefined`. Every shared primitive declares optional props the narrow way;
every caller spreads possibly-undefined values in. **One convention error at every call site.**

### 3.1 The change

In `src/components/ml/ui.tsx` and `src/components/ml/kit.tsx`, widen every optional prop on every
exported component:

```diff
- onClick?: () => void;
+ onClick?: (() => void) | undefined;
```

Apply to at minimum: `Btn` (`onClick`, `className`, `type`, `disabled`), `TopBar` (`subtitle`,
`back`, `right`), `SearchBar` (`value`, `onChange`, `placeholder`, `onFocus`), `Chip`
(`active`, `onClick`), `Screen` (`bottomNav`), `Sheet` (`title`), `Card` (`className`),
`Row` (`icon`, `subtitle`, `right`, `to`, `params`, `onClick`), `EmptyState` (`children`),
`Dialog` (`body`, `children`), `Meter` (`hint`), `StatusPill` (`tone`, `icon`),
`Loading` (`rows`), `Stars` (`size`), `VerifiedBadge` (`size`).

Then the same pass on every component in `src/components/ml/*.tsx` that other files import —
including `profile.tsx` and `person-profile.tsx`, which carry the currently-reported errors
(`TagChip.tone`, `EmptyState.cta`, `onMore`, `back`).

### 3.2 Also fix `Btn`'s discarded union

```diff
- const styles: Record<string, string> = { … }
+ const styles: Record<NonNullable<BtnProps["variant"]>, string> = { … }
```

`Record<string, string>` makes `styles[variant]` `string | undefined` at type level.

**Acceptance:** `bunx tsc --noEmit` returns **zero** `TS2375` errors. Do not fix any call site to
achieve this — if a call site still errors, the primitive is still wrong.

---

## PHASE 4 — Icons

**Branch:** `fix/icons-no-emoji` · **Risk:** low · **Est:** 1 hour

Emoji are shipped as text glyphs and **render as empty boxes on real devices** — this is the
visible defect on the live app right now. `lucide-react` is already a dependency and already used
for `BottomNav`, `Row` and `Stars`.

| Glyph | File | Component | Replace with |
|---|---|---|---|
| `⌕` | `ml/ui.tsx` | `SearchBar` | `<Search />` |
| `🎙` | `ml/ui.tsx` | `SearchBar` | `<Mic />` |
| `←` | `ml/ui.tsx` | `TopBar` back | `<ArrowLeft />` |
| `✓` | `ml/ui.tsx` | `VerifiedBadge` | `<BadgeCheck />` or `<Check />` |
| `📍` | `ml/cards.tsx` | `TalentRow` | `<MapPin />` |
| `♡` | `ml/cards.tsx` | `TalentRow` save | `<Heart />` |

Then sweep `src/data/*` for emoji used as data — **`src/data/portfolio.ts` has `workType().emoji`,
rendered by `src/routes/discover.music.tsx`** (added 10 Sep). Replace with an icon key.

**Also:** `SearchBar`'s `<input>` has no accessible label, and `TopBar`'s back link is a `<Link>`
with `aria-label="Back"` — keep the label when swapping in the icon.

**Acceptance:** `grep -rnP "[\x{1F300}-\x{1FAFF}\x{2190}-\x{21FF}\x{2300}-\x{23FF}\x{2600}-\x{27BF}]" src/`
returns nothing outside comments.

---

## PHASE 5 — Navigation and dead controls

**Branch:** `fix/nav-and-dead-controls` · **Risk:** low · **Est:** 1 hour

### 5.1 Bottom-nav active state

`src/components/ml/ui.tsx`:

```js
const active = path === item.to;   // exact match — broken on ~200 of 207 routes
```

`/marketplace/search`, `/marketplace/rent/…`, `/profile/rahul`, `/messages/…` never highlight.
Replace with **longest-prefix match, `/` special-cased** (it is Discover, and would otherwise
match everything).

### 5.2 Dead controls

A `<Btn>` with no `onClick` and no `to` renders as a working control and does nothing. Known:

- `src/routes/recently-viewed.tsx` — "Clear history"
- `src/components/ml/cards.tsx` — `TalentRow` "Message"

Sweep for others. Where the action is not built: `disabled` plus a reason, not a live-looking
button.

### 5.3 Display copy used as a style key

`AvailabilityBadge` in `ml/ui.tsx`:

```js
const map = { Available: {…}, Selective: {…}, Booked: {…} };
const c = map[status] ?? map["Booked"]!;
```

Keyed on user-visible text — reword or translate it and every badge silently turns grey. Key off
a union (`"available" | "selective" | "booked"`), label as a separate display concern.

### 5.4 Touch targets

`TalentRow`'s "View profile" link (`min-h-11`) and save button (`h-11 w-11`) are **44px**. Floor
is **48dp**. Note the v9 rule: a control that should *look* smaller — a 32px chip — keeps an
**invisible 48dp hit area** rather than growing. Do not enlarge chips.

### 5.5 Deprecated route in new code

`src/routes/discover.nearby.tsx` and `src/routes/recently-viewed.tsx` (both 10 Sep) link
`/talent/$id`. `roadmap.md` has had *"redirect `/talent/$id` → `/profile/$username`"* open since
2 Sep. Either land the redirect or stop wiring new screens to it — flag which in the PR.

**Acceptance:** every bottom-nav tab highlights on its nested routes; no `<Btn>` without a handler
or destination; `grep -rn "min-h-11\|h-11 w-11" src/` reviewed.

---

## PHASE 6 — Component de-duplication

**Branch:** `refactor/dedupe-components` · **Risk:** medium — wide import churn · **Est:** 1 day

You instructed this three times — 12, 14 and 17 August. It was not done.

| Duplicate | Files | Action |
|---|---|---|
| `AvailabilityBadge` / `StatusPill` | `ui.tsx` / `kit.tsx` | Keep `StatusPill`. `kit.tsx` already comments *"One status system across every flow"* directly above it |
| `Chip` / `Segmented` | `ui.tsx` / `kit.tsx` | Byte-identical active/inactive class strings — keep one |
| `Sheet` / `Dialog` (hand-rolled) | `ui.tsx` / `kit.tsx` | **Replace both** with `src/components/ui/sheet.tsx` and `dialog.tsx` (Radix, installed, unused) |
| `Loading` (custom) | `kit.tsx` | Replace with `src/components/ui/skeleton.tsx` |

The hand-rolled modals have **no focus trap, no Escape handler, no scroll lock, no
`aria-labelledby`**. `src/components/ui/` holds **45 installed Radix primitives, all unused**.

### 6.1 Also: typed routing

`Row` in `kit.tsx`:

```js
// eslint-disable-next-line @typescript-eslint/no-explicit-any
<Link to={to as any} {...(params ? { params: params as any } : {})} />
```

`Row` is the standard navigation primitive; this disables TanStack Router's compile-time route
checking for **every** `Row` in the app. With 207 routes that is where dead links survive. Type it
properly. **Expect this to surface real broken links** — list them in the PR rather than casting
them away.

### 6.2 Styling mechanism

Inline `style={{ background: "var(--…)" }}` appears in `BottomNav`, `AvailabilityBadge`,
`StatusPill`, `Meter`, `TalentRow`. Inline styles cannot be overridden by class or linted for
token compliance. Move to Tailwind classes bound to the Phase 2 tokens.

**Acceptance:** one status component, one chip, one sheet, one dialog, one skeleton; zero
`as any` in `kit.tsx`; broken links enumerated.

---

## PHASE 7 — Inventory before the big retrofit

**Branch:** none — this produces a document, not a commit · **Est:** 2 hours

**Nobody has opened a single one of the 207 route bodies, or 21 of the 22 `src/data` modules.**
Before touching them, produce a per-file count of:

- arbitrary radius (`rounded-[`) and type (`text-[`) values
- Unsplash / stock image URLs
- emoji glyphs
- `<Btn>`/`<Link>` with no handler or destination
- `/talent/$id` links
- inline `style={{` with design values
- relative time stored as a string (`"3 weeks ago"` and similar)

**Output:** a table — file, counts, estimated effort. That number decides whether Phase 8 is one
PR or a staged migration. The last plan written without this number collapsed at Wave 2.

---

## PHASE 8 — Retrofit routes and data

**Branch:** staged per the Phase 7 inventory · **Est:** unknown until Phase 7

Mechanical application of the Phase 2 token mapping across routes, plus the content-layer split
in `src/data/*`:

Keep three kinds of content in **separate modules** — **UI copy** (`reviewPolicy`, `flagReasons`),
**seed/demo content** (`receivedReviews`), **runtime data** (`ratingSummary`). Today they share
files and screens read all three identically, so **there is no seam along which to replace the
runtime third with real queries.** That seam is the entire point of this phase — the database work
scheduled after routing depends on it.

Replace relative-time strings with real timestamps formatted at render. Replace Unsplash URLs per
the v9 rule (local photography library, never stock).

**The taxonomy for this already exists** in the other repo: `guide/docs/hardcoded-content-audit.md`.
Read it before starting.

---

## Do not do these

- **Do not add database tables.** Two exist by decision; DB work is scheduled after routing. A
  live Firebase backend already models this domain — 16 collections, 34 document types. New tables
  would fork the model.
- **Do not apply DDL directly.** All schema changes go through `supabase/migrations/`.
- **Do not wire payments.** Stripe and Paddle are unavailable (Indian seller); Razorpay is likely
  but unapproved.
- **Do not build media upload.** Link-based by decision.
- **Do not "fix" the mock data by making flows persist.** That is scheduled, not forgotten.
- **Do not renumber flows.** Two source documents disagree (FLOW 03 is "Market" in one and "Add
  Portfolio" in another). Surface it; do not resolve it.

---

## Open unknowns — flag, do not guess

1. **207 route bodies unread.** Phase 7 exists to close this.
2. **Four constraints unverifiable** until routes are read: whether `/booking/$id/pay` and
   `/plans/checkout` imply a payment succeeded, a booking is confirmed, or availability is real.
   The rule is: never imply a state the system cannot deliver.
3. **`text-[15px]` / `text-[17px]`** straddle type tokens 2 and 3 — ask, don't choose.
4. **`/talent/$id` redirect** — open since 2 Sep, still being wired into new screens.
5. **~40 of 63 `.md` files in the sibling repo unread.** The six already read overturned two
   audit findings; the rest may overturn more.

---

# Appendix A — `AGENTS.md`

**Deleted 13 Sep 2026.** This appendix held a second, condensed copy of `AGENTS.md`. Two copies of
the governing rules is exactly how the `#5B4BFF` palette drifted in the first place, and the copy
here had already fallen behind on the type scale and the retired fonts.

The single source is **`AGENTS.md` at the root of `musiclinkd-9e6ca875`**. A mirror of the current
proposed version lives at `reference/AGENTS.md` in this folder; the repo copy is what agents
actually read, so changes go there first.
