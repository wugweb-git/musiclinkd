---
doc: DESIGN-WORK-STATE
version: 1.0
status: SUPERSEDED
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
folder: archive
originally: DESIGN-WORK-STATE.md
---

> **SUPERSEDED** · v1.0 · 2026-09-13. 10 Sep landed/not-landed list. Superseded by state/CURRENT.md.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

# MusicLinkd (Lovable app) — design work: what has landed, what has not

Verified against `wugweb-git/musiclinkd-9e6ca875` at **HEAD `384af26f`** (PR #10), 10 Sep 2026.
Every "landed" line was read from the file at HEAD, not from a commit message.

**Claude (Cowork) cannot push, commit or merge this repo** — it is not mounted on the Mac and the
Lovable connector is read-only (no `write_file`). This document is the handover for Claude Code.

---

## A. Landed and merged — nothing to do

| PR | Merged | What |
|---|---|---|
| #3 | 13:37 | **Phase 2** — design tokens reconciled to v9; `src/components/ml/` migrated off arbitrary values |
| #4 | 13:37 | AGENTS.md single-author commit rule made explicit |
| #5 | 13:37 | Findings recorded (Lovable metadata drift, js-yaml chain) |
| #6 | 14:09 | **jsx-a11y wired**, 15 WCAG errors fixed; `--muted-foreground` 4.04:1 → 4.66:1 |
| #7 | 14:10 | `profiles.visibility` RLS migration **merged — see §C, NOT applied** |
| #8 | 14:16 | Page ground `#FCFCFD` → `#ECECF1`; card step 1.025:1 → 1.177:1 |
| #9 | 14:22 | Grey ladder — `--border` → `#D5D5DE` (1.147:1 → 1.458:1); four near-identical greys → `#F8F8FB` |
| #10 | 14:31 | `rail` utility — scroll-snap on all 65 horizontal rails |

Build `ready`, `error: null`, no `lovable-sync` branch. Published build deployed 15:29 UTC and
current with this HEAD.

**Phase 2 spot-check (live `ml/auth.tsx`):** `text-[17px]` → `text-2`, `text-[26px]` → `text-3`,
`text-[11px]` → `text-1`, `max-w-[430px]` → `max-w-app`. The sweep is real.

---

## B. Specified and NOT landed — the actual work list

### B1. Typography — Inter only *(marked "done" 10 Sep ~19:40; nothing at HEAD)*

Four edits. All four verified still un-made:

1. `src/routes/__root.tsx` — font link is still
   `family=Inter:wght@400;500;600;700&family=Inter+Tight:wght@600;700;800`.
   Should be `family=Inter:wght@400;500;600;700;800` (800 must be requested — Inter currently
   loads only to 700, and display needs it).
2. `src/styles.css` — `--font-display` is still `"Inter Tight", "Inter", …`. Should be Inter.
   **Keep the token name**; `font-display` utilities may be used across the 205 routes.
3. `src/styles.css` `@layer base` — `letter-spacing: -0.02em` still on the `h1..h4` block.
   Remove: it was tuned for Inter Tight, which is condensed. Inter is not.
4. `src/components/ml/auth.tsx` — two `tracking-tight` still present: the `Brand` wordmark span
   and the `AuthHeading` h1. Same compensation, same removal.

Measured consequences: delivered fonts **412.3 KB → 293.8 KB**; the wordmark widens to **105.3%**
of Inter Tight 800 at the same px, so check fixed-width slots (nav, `Brand`) for clipping.

**Not part of this change, flagged only:** 13 `tracking-wide` on `text-1 font-bold uppercase`
micro-labels across `auth.tsx`, `kit.tsx`, `ui.tsx`, `collab-extra.tsx`, `booking.tsx`, plus
`tracking-[0.18em]` on the eyebrow in `index.tsx`. Positive tracking on all-caps is a legibility
convention, not an Inter Tight artefact. Leave unless Vedanshu says otherwise.

### B2. Stale comments in `styles.css` — the authoritative file is now wrong twice

- Header: *"Type is four fluid ramps; never an arbitrary px size"* and the type block's *"Four
  steps, not five… every other foundation group is likewise exactly four."* **The 4-step cap is
  lifted.** Scales may carry as many steps as needed; what stays banned is the arbitrary value.
- Header: *"hairline #ECECF1"*. `#ECECF1` is the page ground. The hairline is `--border #D5D5DE`.

Both matter because this file is what the next reader trusts — the same class of error that
produced the wrong type scale.

### B3. Ships-wrong-today, independent of any phase

- **`src/routes/index.tsx` renders `src={talents[1]!.photo}` under `alt="Your profile"`** — a seed
  record's face presented as the viewer's own, to every user, right now. No dependency on the
  entry gate or Phase 8. Fix as a standalone. What replaces it is Vedanshu's call.
- **`__root.tsx` `theme-color` is still `#FCFCFD`.** PR #8 changed the ground to `#ECECF1`. Mobile
  browser chrome is painting the old colour. One line.
- **`--muted-foreground` is 3.22:1 on the `#ECECF1` ground.** Fine inside cards (4.66:1), fails AA
  for secondary text placed directly on the page ground. `--text-secondary` does not exist yet.

### B4. Phase 2 leftovers in `ml/`

- `text-[12.5px]` survives the Phase 2 sweep — twice in `auth.tsx` (`AuthNotice` body,
  `PasswordRequirements` list items).
- Inline `style={{ background: … }}` still in `auth.tsx` (Brand span, AuthNotice, strength bars,
  check circles) — Phase 6.2's job, not done.

### B5. Phases not started

**2b** motion (sheets/dialogs have no enter or exit, route hard-cuts, no list entry, no sticky-header
scroll binding, hard-cut state changes) · **3** optional-prop convention · **4** emoji → Lucide ·
**5** nav longest-prefix + dead controls + `AvailabilityBadge` union + touch targets ·
**6** de-duplication + Radix swap · **7** inventory of 205 routes · **9** first-run (gated on 8,
and on a cookie-auth migration — see §C).

Phase 4 is now broader than "emoji": `→ ★ ✓` are **absent from Inter's served Google Fonts
subsets**, so those text glyphs already fall back to a system font alongside the emoji.

---

## C. Blocked or not-a-code-change

- **`profiles.visibility` RLS.** PR #7 merged the migration; its own commit message says
  `NOT APPLIED`, and the live database confirms it — the SELECT policy is still `USING (true)`.
  All 3 profiles are `visibility='public'`, so nothing is exposed yet. Applying it is a production
  data-access change and Vedanshu's call, not Claude Code's.
- **Phase 9 first-run** needs a cookie-based auth migration (`@supabase/ssr`, `createServerClient`)
  that is in no phase. Today the session is `localStorage`-only and `requireSupabaseAuth` is
  *function* middleware reading a Bearer header — `beforeLoad` on an SSR document render sees
  nothing.
- **Upstream `tokens.json`** (other repo): `--color-paper`'s description narrates a change never
  applied to its value, and wrongly claims `#ECECF1` equals `--color-line` (`#D5D5DE`). Also
  `ml-tokens.css` still declares `--font-eyebrow: Unica One`, now retired.

---

## D. Suggested branch order for Claude Code

```
1  fix/avatar-your-profile        §B3 first bullet   — ships wrong today
2  fix/theme-color                §B3 second         — one line, same PR is fine
3  feat/inter-only                §B1 + §B2          — 4 edits + 2 comment fixes
4  fix/text-secondary-on-ground   §B3 third
5  fix/phase2-leftovers           §B4
```

One branch, one PR, exit gate each. Do not batch 1–3 into one PR: the avatar fix should be
reviewable on its own because it changes what every user sees.

Every commit ends with, and only with:

```
Co-Authored-By: Wugweb <admin@wugweb.com>
```

Never force-push, rebase, amend or squash anything already pushed — it corrupts Lovable's sync.
