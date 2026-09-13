---
doc: TAILWIND-DS-AUDIT
version: 1.1
status: PARTLY STALE
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
folder: audits
originally: TAILWIND-DS-AUDIT.md
---

> **PARTLY STALE** · v1.1 · 2026-09-13. Both sides of the design system. Section B is now overtaken: the ceiling was raised and the scale is 7 steps. Section A still holds.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

# Tailwind design-system audit — Lovable app + the guide

Two systems, one design. Audited against the Lovable repo at HEAD `315564c5` and
`wugweb-git/musiclinkd` on the Mac. **The 4-steps-per-scale cap is dead**, which changes what the
right answer is — most of what follows only became possible yesterday.

---

## A. The Lovable app (Tailwind v4, CSS-first)

### A1. The biggest unlock from lifting the cap: `--radius-xl/2xl/3xl` are fake

```css
--radius-sm: 8px;   --radius-md: 16px;  --radius-lg: 32px;
--radius-xl: 32px;  --radius-2xl: 32px; --radius-3xl: 32px;   /* ← all aliases of lg */
```

The file explains why: *"retained as aliases of 32px so the ~450 existing utility usages keep
rendering on-scale instead of silently falling back to Tailwind's defaults (which would collapse
2xl from 40px to 16px)."*

That shim existed **only because the scale was capped at four steps.** `rounded-2xl` has meant 40px
in this codebase's history; today it silently means 32. Three utility names collapse to one value,
so the design can't express a difference it used to have.

With the cap gone these become real steps. **This is the single cheapest win available** — it
restores expressiveness to ~450 existing call sites without touching one of them.

### A2. Two spacing systems, coexisting, acknowledged in a comment

```css
--spacing-xs: 4px;  --spacing-sm: 8px;  --spacing-md: 16px;  --spacing-lg: 32px;
--spacing-nav-clear: 96px;
/* "Spacing steps. Numeric utilities (p-1, gap-4 …) are untouched." */
```

In Tailwind v4, `--spacing` is a **base unit** that generates the whole numeric scale
(`p-1` … `p-96`). Declaring `--spacing-xs` doesn't change that scale — it *adds* `p-xs` alongside
it. So the app has `p-xs` **and** `p-1`, both 4px, both valid, neither preferred.

They happen to line up (4/8/16/32 = `p-1`/`p-2`/`p-4`/`p-8`), which is why nothing has broken. It
also means a linter can never tell a mistake from a choice.

**Pick one.** Either set `--spacing: 4px` and use the numeric scale, or keep named steps and ban
numeric utilities. Right now you have both and no rule.

### A3. Dark mode is declared and does not exist

```css
@custom-variant dark (&:is(.dark *));
```

Line 5 of `styles.css`. **There is no `.dark { }` block anywhere in the file.** Every `dark:`
utility in the codebase is dead — it compiles, it never matches. Either build the dark palette or
delete the variant, because right now `dark:bg-card` looks like it works.

### A4. Every token exists twice

`:root` holds shadcn semantic names (`--background`, `--primary`, `--muted-foreground`).
`@theme inline` re-exports them into Tailwind's namespace (`--color-background`, `--color-primary`).
That indirection is correct and standard — but it means **adding one token is two edits**, and with
an uncapped scale you'll be adding them often.

`--text-1..5` → `--type-1..5` has the same shape. Five steps today; the moment there are nine,
that's eighteen lines maintained by hand.

**This is the argument for generating the block** — see §C.

### A5. Dead and stale entries

| token | state |
|---|---|
| `--color-navy` / `--navy` | **retired** in `tokens.json`; kept as a deliberate shim for 17 route files |
| `--gradient-navy` | built on the retired colour |
| `--secondary-foreground` | still `oklch(0.1713 0.0539 282.44)` — that *is* navy, unflagged |
| `--text-secondary` | now identical to `--muted-foreground` (`#66666F`); the file says merging is deferred because `text-text2` is on ~100 call sites |
| header comment | still claims *"Type is four fluid ramps"* while the block below documents five |

### A6. The type scale vs. what routes actually use

Measured across three route files I read at HEAD — `feed.tsx`, `index.tsx`, `onboarding/done.tsx`:

**11 distinct arbitrary sizes**: 10, 11, 12, 13, 14, 15, 16, 17, 24, 28, 30 px.

Against the five steps as they resolve at the 412px target:

```
--type-1  12.0px      --type-2  16.1px      --type-3  22.3px
--type-4  32.6px      --type-5  40.8px
```

Nearest-step mapping, and the error:

| used | nearest | delta | |
|---|---|---|---|
| 10px | type-1 | −2.0 | no step within 1.5px |
| 11px | type-1 | −1.0 | |
| 12px | type-1 | 0.0 | |
| 13px | type-1 | +1.0 | |
| **14px** | type-1 | **+2.0** | **no step within 1.5px** |
| 15px | type-2 | −1.1 | |
| 16px | type-2 | −0.1 | |
| 17px | type-2 | +0.9 | |
| **24px** | type-3 | **+1.7** | **no step within 1.5px** |
| **28px** | type-4 | **−4.6** | **no step within 1.5px** |
| **30px** | type-4 | **−2.6** | **no step within 1.5px** |

Three files, and there is **a 10px gap between type-3 (22.3) and type-4 (32.6)** with 24, 28 and
30px all living in it. That gap is where the arbitrary values come from — the scale genuinely
cannot express what the screens need.

That was unfixable under the cap. It is now a decision: **add a step between 22 and 32**, and one
below 12 if 10/11px are deliberate. Those are yours to make; the measurement is the input.

Caveat: three files, not 204. The full count is Phase 7. Expect the list to grow, not shrink.

---

## B. The guide's token system

### B1. It is at 108 of 108 tokens, with zero headroom

`tokens.json` → `meta.tokenBudget.ceiling: 108`. The generated `ml-tokens.css` emits **exactly
108**. `build-tokens.mjs` exits 1 above it.

**The Lovable app already ships `--type-5`. The guide cannot generate it today.** The number has to
move before anything else does.

### B2. Two different rules were welded into one number

The ceiling note conflates them:

- **The anti-duplication budget** — real. The old system reached **248 tokens** because the same
  8px shipped under six names. Keep this. It is the ceiling doing its actual job.
- **The 4-steps-per-scale rule** — a convention. Now dead.

They are not the same constraint, and only the first deserves a number.

### B3. Where the dead rule is still written down

14 files. Three are **generated** — fix the source, not the output.

**Edit:** `tokens.json` (ceiling note, `space`/`icon`/`size` descriptions) ·
`scripts/build-guide.mjs` (4 places — it generates `index.html`) · `scripts/build-tokens.mjs`
(comments only) · `frontend/guide/system/ml-fixes.css` · `docs/09-Roadmap/…`

**Do not hand-edit:** `frontend/guide/index.html` · `frontend/guide/system/ml-tokens.css` ·
`webapp/shared/ml-tokens.css`

### B4. One real validator will reject a fifth step

`scripts/build-handoff.mjs:95` —

```js
/** A type step's weight must BE one of the four steps, never a stray value. */
```

Everything else about "four" is comments. **This one is code**, and it fails on `--type-5`.

### B5. Every scale is still exactly four

```
type 4 · space 4 · radius 4 · size 4 · weight 4 · opacity 4
elevation 4 · icon 4 · avatar 4 · z 4 · duration 4 · border 2
```

Not one of these was sized by need. They were sized by a rule that no longer exists.

---

## C. The merge, and why it is smaller than it looks

`scripts/build-tokens.mjs` already emits **one source to two destinations**:

```js
const OUTS = [
  resolve(ROOT, "frontend/guide/system/ml-tokens.css"),
  resolve(ROOT, "webapp/shared/ml-tokens.css"),
];
```

Its header: *"makes that class of drift impossible: ml-tokens.css is never hand-edited."*

**The Lovable app is the one consumer still hand-transcribed** — and every drift found this week
(wrong type scale, navy, paper, hairline) came from exactly that.

The merge is a second emitter, not a rewrite:

1. `tokens.json` — raise the ceiling, delete the step rule from the notes.
2. `build-tokens.mjs` — add `emitTailwind()` writing `@theme inline` + `:root` shape. Same data,
   different syntax. The generator already holds every value.
3. Lovable repo — commit the output as `src/tokens.generated.css` with a provenance header naming
   the `tokens.json` sha. `styles.css` imports it and declares **no token by hand**, keeping only
   the semantic aliases.
4. Regenerate. Drift becomes structurally impossible in both directions.

This also fixes §A4 for free: adding a step becomes one edit in `tokens.json` instead of two by
hand in `styles.css`.

---

## D. Order

1. **Raise the ceiling in `tokens.json`** — nothing else can move until this does.
2. **Fix `build-handoff.mjs:95`** — the only real code enforcing "four".
3. **Make `--radius-xl/2xl/3xl` real steps** — ~450 call sites gain expressiveness, zero edits.
4. **Decide the spacing system** — named or numeric, not both.
5. **Dark mode: build it or delete the variant.**
6. **Add the type step(s) the 22→32 gap needs** — after Phase 7 gives the full count, not before.
7. **Then the second emitter**, and the hand-maintained token block goes away.

1–3 and 5 are mechanical and independent. 6 is yours. 7 is the one that stops this recurring.
