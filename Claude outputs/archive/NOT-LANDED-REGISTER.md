---
doc: NOT-LANDED-REGISTER
version: 1.0
status: SUPERSEDED
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
folder: archive
originally: NOT-LANDED-REGISTER.md
---

> **SUPERSEDED** · v1.0 · 2026-09-13. Superseded same day by state/BUILD-STATE-2026-09-13.md. Kept for the correction trail.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

# MusicLinkd — what is worked on but NOT in sync / NOT live

Verified **13 Sep 2026** against `origin/main` = `359a485` (fetched 04:55 UTC today) and the Lovable
project record for `ace967d9-a62e-48bb-b076-c3b63b11b185`.

---

## 0. Two corrections to what I told you an hour ago

I was working from a clone snapshot taken before today's fetch. Both of these were wrong:

| I said | Actually |
|---|---|
| "The services / service-builder / availability screens do not exist in any branch." | **They exist and are on `main`.** Eleven route files, committed 1–9 Sep. I missed them because they were never in a PR — see §1. |
| "`origin/sync` CI is unmerged; `main` has no `.github` directory." | **Merged today.** `9f1d876` → `359a485`. `.github/workflows/ci.yml` is on `main`. |

---

## 1. Why the screens looked missing — the real structural finding

Every one of those screens was committed by **`gpt-engineer-app[bot]`**, Lovable's sync bot,
with the commit message `Changes` or `Work in progress`:

```
d6a8e15  09 Sep  Changes   src/routes/services.conflicts.tsx
cf96048  08 Sep  Changes   src/routes/services.$id.enquiry.tsx, services.$id.manage.tsx
cf3f93b  08 Sep  Changes   src/routes/business.$id.availability.tsx, business.$id.services.tsx
0b631c3  04 Sep  Changes   src/routes/services.availability.tsx
6949afb  04 Sep  Changes   src/routes/services.new.tsx
f77acf2  04 Sep  Changes   src/routes/services.$id.tsx
5224160  04 Sep  Changes   src/routes/services.index.tsx
5e65567  01 Sep  Changes   src/routes/provider.availability.tsx
5870f08  01 Sep  Work in progress  src/routes/booking.$id.service.tsx
```

**There are two writers on `main` and only one of them uses PRs.** Claude Code branches, opens a
PR, writes a real commit message, gets a review gate. Lovable's agent commits straight to `main`
with a two-word message. Nothing about the second path is reviewable, greppable by intent, or
CI-gated — and now that CI exists, it still won't gate these, because they land as direct pushes
to `main`, not as PRs.

**This is the thing to decide.** Either Lovable's agent gets its own branch and a PR, or you accept
that roughly half the codebase arrives unreviewed and untraceable.

### Attribution consequence

Your rule is: every commit's `Co-Authored-By` names Wugweb only, `admin@wugweb.com`, nothing else.

- Claude Code's commits comply — `9f1d876` and `359a485` both carry `Wugweb <admin@wugweb.com>`.
- **Lovable's bot commits do not.** `08ca2b3`, `f7ceab6`, `08ed071` all carry
  `Co-authored-by: wugweb-git <132406863+wugweb-git@users.noreply.github.com>`; `e2861bc` carries
  no trailer at all. Lovable writes these itself and they cannot be fixed after the fact without
  rewriting published history, which is forbidden.

So the rule is currently unenforceable on ~half of `main`. Branch-and-PR for Lovable would fix it
going forward.

---

## 2. Landed today, on `main`, not yet confirmed on the public site

`6a11ca0 .. 359a485` — 969 insertions, 10 files:

```
.github/workflows/ci.yml                          +35
src/routes/search.history.tsx                    +126   NEW
src/routes/search.saved.tsx                      +127   NEW
src/routes/search.why.$id.tsx                    +122   NEW
src/data/announcements.ts                        +121   NEW
src/data/search-meta.ts                          +123   NEW
src/data/monetization.ts                          +25   NEW
src/integrations/supabase/types.ts               ±366
src/integrations/supabase/previewAuthStorage.ts   ±66
src/routeTree.gen.ts                              +82
```

Lovable's record: `latest_commit_sha 08ca2b3`, preview built at `359a485`, `is_published: true`,
`updated_at 2026-09-13T04:57`. **I could not verify the published HTML** — WebFetch returns
`PROVENANCE_REQUIRED` for `musiclinkd.lovable.app`. I am reporting the build record, not the
rendered page.

---

## 3. Specified, agreed, and still not in the code

Each verified by reading the file at `main` today — not carried over from an older note.

### 3.1 Typography — 3 of 4 edits done, 2 remain

| # | item | state |
|---|---|---|
| 1 | `--font-display` → Inter | **done** |
| 2 | drop Inter Tight from the font link | **done** |
| 3 | **weight 800 never added** — link is `Inter:wght@400;500;600;700` | **open** |
| 4 | **`letter-spacing: -0.02em` still on `h1..h4`** in `styles.css` `@layer base` | **open** |
| 5 | two `tracking-tight` in `ml/auth.tsx` (Brand wordmark, AuthHeading) | not re-checked today |

(3) and (4) are the same bug: both were Inter Tight compensation. Inter is not condensed, so the
tracking is now wrong, and headings ask for a weight the browser never downloads — they render at
700 and synthesise, or fall back.

### 3.2 `styles.css` header comment contradicts its own file

Line 20: *"Type is four fluid ramps; never an arbitrary px size."*
Sixty lines below, the type block correctly documents **five** steps and defines `--type-5`.

This is the exact class of error that produced the wrong type scale in the first place: the next
reader trusts the header. One-line fix.

### 3.3 `--radius-xl / 2xl / 3xl` are still aliases of 32px

```css
--radius-lg: 32px;  --radius-xl: 32px;  --radius-2xl: 32px;  --radius-3xl: 32px;
```

Three utility names collapse to one value across ~450 call sites. The file's own comment says the
shim exists because the scale was capped at four steps. **The cap is lifted.** Making these real
steps is the cheapest available win — zero call-site edits.

### 3.4 Dark mode is declared and does not exist

`@custom-variant dark (&:is(.dark *));` on line 5, and **no `.dark { }` block anywhere.** Every
`dark:` utility in the codebase compiles and never matches. Build it or delete the variant.

### 3.5 Two spacing systems, no rule

`--spacing-xs/sm/md/lg` (4/8/16/32) coexist with Tailwind's numeric `p-1/p-2/p-4/p-8` — identical
values, both valid, neither preferred. A linter can never tell a mistake from a choice. Pick one.

### 3.6 Retired colour still defined and still used

`--navy`, `--color-navy` and `--gradient-navy` are live in `styles.css`, and `src/routes/index.tsx`
uses `bg-navy` and `from-navy/85` today. Documented as a deliberate shim for 17 route files.

### 3.7 `--text-secondary` is now byte-identical to `--muted-foreground`

Both `#66666f`. The two-tier grey existed to clear AA; it no longer has a job. Kept only because
`text-text2` is on ~100 call sites.

### 3.8 Phase 2's token sweep never reached `src/routes/`

`index.tsx` at `main` today, one file:

- arbitrary sizes: `text-[30px] [15px] [14px] [13px] [11px] [10px]`, plus `text-xs`
- inline `style={{ background: … }}` twice
- emoji and text glyphs as icons: `🔔` `✨` `→` `★`
- `rounded-3xl` / `rounded-2xl` — the fake radius steps from §3.3

PR #3 migrated `src/components/ml/`. **The 204 route files were never swept.** That is Phase 8, and
the Phase 7 inventory prices it at **≈97 hours / 16 working days / 8 PRs**, dominated by a 29.3h
412px render pass and a 40.5h `src/data` split.

---

## 4. Not started at all

| Phase | Scope |
|---|---|
| 2b | Motion — sheets and dialogs have no enter/exit, route changes hard-cut, no list entry, no sticky-header scroll binding |
| 3 | `exactOptionalPropertyTypes` convention at component definitions |
| 4 | Emoji → Lucide. Broader than it sounds: `→ ★ ✓` are absent from Inter's served Google subsets and already fall back to a system font |
| 5 | Nav longest-prefix matching, dead controls, `AvailabilityBadge`/`StatusPill` union, 48dp touch floor |
| 6 | De-duplication, Radix swap (hand-rolled modals have no focus trap, Escape or scroll lock) |
| 8 | The 204-route sweep — ≈97h |
| 9 | First-run / splash / onboarding entry. **Blocked** — needs a cookie-based auth migration (`@supabase/ssr`, `createServerClient`) that is in no phase. Today the session is `localStorage`-only and `requireSupabaseAuth` is *function* middleware reading a Bearer header, so `beforeLoad` on an SSR document render sees nothing |

---

## 5. Specified elsewhere, zero code written

| Spec | Status |
|---|---|
| `TAXONOMY-MIGRATION-SPEC.md` — 95 slugs across 7 broken categories where `value === label` | no migration file exists |
| `WIDGET-ADOPTION-SPEC.md` — 116 widget families in `ml-widgets.css`, currently hand-rolled twice | **awaiting your decision**: import the CSS, port to React, or tokens-only |
| `TAILWIND-DS-AUDIT.md` §C — second emitter in `build-tokens.mjs` so the Lovable app stops being hand-transcribed | not started |
| Capacitor / Android shell | not started. `__root.tsx` still has no `viewport-fit=cover` |
| `tokens.json` ceiling | **108 of 108, zero headroom.** `build-handoff.mjs:95` will reject `--type-5`, which the app already ships |

---

## 6. Open decisions that are yours, not Claude Code's

1. **Lovable's agent: branch + PR, or keep direct-to-`main`?** (§1 — biggest one)
2. **`profiles.visibility` RLS** — PR #7 merged the migration, it is still **not applied**; the live
   SELECT policy is `USING (true)`. All 3 profiles are `public`, so nothing is exposed yet.
3. **Firestore `/users` PII** — `allow read: if true` publishes whole documents. `email` is on all
   83 world-readable user docs today. Firestore has no field-level read control, so the first real
   OTP signup publishes that person's phone number. Fixed by a split public-profile doc, or by the
   Postgres move.
4. **The type-scale gap** — 24, 28 and 30px all live in the 10px hole between `--type-3` (22.3) and
   `--type-4` (32.6). Add a step, or keep forcing arbitrary values. Your call; the measurement is
   the input.
5. **The Lovable project `description` field still specifies the superseded system** — `#5B4BFF`
   primary, `#8B5CF6` secondary, `#FAFAFA` ground, Google Sans. Project knowledge says `#5B4BFF`
   "is superseded and always a bug." The description is what the agent reads alongside knowledge.
   Worth rewriting.
