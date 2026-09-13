---
doc: PHASE-2-TOKEN-SPEC
version: 1.0
status: SUPERSEDED
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
folder: archive
originally: PHASE-2-TOKEN-SPEC.md
---

> **SUPERSEDED** · v1.0 · 2026-09-13. Phase 2 shipped. The file's own banner disowns its type scale, and its 'four steps' argument is the rule that is now dead. Sections 4 and 5 should be lifted into Phase 8.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

> # ⚠️ SUPERSEDED — DO NOT USE THE TYPE SCALE IN THIS FILE
>
> The type scale below (10–12 / 14–18 / 20–28 / 28–44) is **wrong**. I sourced it from
> `frontend/guide/README.md`, which is prose and pre-v9. The generated
> `frontend/guide/system/ml-tokens.css` is the truth:
>
> ```css
> --type-1: clamp(12px, calc(12px + 1  * (100vw - 360px) / 1080), 13px);
> --type-2: clamp(16px, calc(16px + 2  * (100vw - 360px) / 1080), 18px);
> --type-3: clamp(22px, calc(22px + 6  * (100vw - 360px) / 1080), 28px);
> --type-4: clamp(32px, calc(32px + 12 * (100vw - 360px) / 1080), 44px);
> ```
>
> `tokens.json` states the rule: **every web minimum equals its Android sp value — 12/16/22/32.**
> Web minimums below that make the preview render smaller text than the app ever can. That exact
> bug existed before 2026-08-29 as *"web 10/14/20/28 against native 12/16/22/32; the caption
> measured 10.03px"* — and my clamp for `--text-1` computed to **10.1px at 412px**. I reproduced
> the defect the token file was written to prevent.
>
> Also wrong here: **JetBrains Mono is retired** (never loaded, silently fell back to generic
> monospace). **Navy `#0D0B26` is retired** — use `ink #0D0D0D`. **`line` is `#D5D5DE`, not
> `#ECECF1`; `muted` is `#66666F`, not `#7D7D8E`.** And there is a **third font family I omitted —
> Unica One**, for eyebrows/kickers only, single weight 400.
>
> The `text-[15px]`/`text-[17px]` mapping table below is invalid — it was computed against the
> wrong scale. Against the real scale, `15px` and `17px` both sit between type-1 (12–13) and
> type-2 (16–18), and `17px` is closest to type-2. **Re-derive before applying.**
>
> Radius, spacing, opacity and motion durations in this file are correct. Elevation values were
> invented by me — the real ones are in `tokens.json`.

# Phase 2 — `src/styles.css` token spec

Exact values for the design-token refactor. Written so Phase 2 is transcription, not interpretation.

Derived from `frontend/guide/README.md` (v9 core scales) and `frontend/guide/rules/*`
(reconciled 31 Aug 2026).

---

## 1. What replaces what

| Token group | Current in `styles.css` | v9 |
|---|---|---|
| Radius | `--radius: 24px`; scale 8/16/**24**/32/**40** | **8 / 16 / 32 / pill** |
| Type | *(none — 8 arbitrary sizes downstream)* | 4 fluid `clamp()` tokens |
| Motion | *(none)* | 125 / 200 / 320 / 500ms + 3 easings |
| Elevation | *"shadows avoided"*, no tokens | `--elevation-1..3` |
| Spacing | Tailwind default | 4 / 8 / 16 / 32 |
| Opacity | *(none)* | 0.08 / 0.16 / 0.32 / 0.64 |
| Purple | `--accent` (general purpose) | Category badge only |
| Focus | *(none)* | 2px pink @ 2px offset |
| Mono | *(none)* | JetBrains Mono |

Keep unchanged: the oklch palette, `#EA4C89` primary, `--font-sans: Inter`,
`--font-display: Inter Tight`, `color-mix` containers, all semantic token names.

---

## 2. The block

```css
@theme inline {
  /* ---- Type: 4 fluid tokens, 360px → 1440px ---- */
  --text-1: clamp(0.625rem, 0.5833rem + 0.1852vw, 0.75rem);   /* 10 → 12px */
  --text-2: clamp(0.875rem, 0.7917rem + 0.3704vw, 1.125rem);  /* 14 → 18px */
  --text-3: clamp(1.25rem,  1.0833rem + 0.7407vw, 1.75rem);   /* 20 → 28px */
  --text-4: clamp(1.75rem,  1.4167rem + 1.4815vw, 2.75rem);   /* 28 → 44px */

  --font-sans:    "Inter", ui-sans-serif, system-ui, sans-serif;
  --font-display: "Inter Tight", "Inter", ui-sans-serif, system-ui, sans-serif;
  --font-mono:    "JetBrains Mono", ui-monospace, SFMono-Regular, monospace;

  /* ---- Radius: 8 / 16 / 32 / pill ---- */
  --radius-1: 8px;
  --radius-2: 16px;
  --radius-3: 32px;
  --radius-pill: 9999px;

  /* ---- Spacing ---- */
  --space-1: 4px;
  --space-2: 8px;
  --space-3: 16px;
  --space-4: 32px;

  /* ---- Opacity ---- */
  --opacity-1: 0.08;
  --opacity-2: 0.16;
  --opacity-3: 0.32;
  --opacity-4: 0.64;

  /* ---- Motion ---- */
  --duration-1: 125ms;  /* button press, chip toggle */
  --duration-2: 200ms;  /* hover, icon/colour change, dismissal */
  --duration-3: 320ms;  /* sheet reveal, card lift, shared-element */
  --duration-4: 500ms;  /* modal fade+scale, splash */

  --ease-standard: cubic-bezier(0.4, 0, 0.2, 1);
  --ease-enter:    cubic-bezier(0, 0, 0.2, 1);
  --ease-spring:   cubic-bezier(0.34, 1.56, 0.64, 1);
}

:root {
  --radius: var(--radius-3);            /* cards */

  /* ---- Elevation. Cards lift; the border is the fallback on white ---- */
  --elevation-1: 0 1px 2px rgb(13 11 38 / 0.04);
  --elevation-2: 0 2px 8px rgb(13 11 38 / 0.06), 0 1px 2px rgb(13 11 38 / 0.04);
  --elevation-3: 0 8px 24px rgb(13 11 38 / 0.08), 0 2px 6px rgb(13 11 38 / 0.05);

  /* ---- Focus ---- */
  --focus-ring: 2px solid var(--primary);
  --focus-offset: 2px;

  /* ---- Purple: category badge ONLY ---- */
  --status-category-bg:   color-mix(in oklab, var(--purple) 10%, transparent);
  --status-category-text: var(--purple);
}

@layer base {
  :where(a, button, input, select, textarea, [tabindex]):focus-visible {
    outline: var(--focus-ring);
    outline-offset: var(--focus-offset);
  }
  @media (prefers-reduced-motion: reduce) {
    *, *::before, *::after {
      animation-duration: 0.01ms !important;
      transition-duration: 0.01ms !important;
    }
  }
}
```

**Delete** the `"shadows avoided"` line from the header comment — that rule was reversed on 31 Aug.
**Remove** `--accent`'s purple role. Audit every `--accent` consumer: anything that is not a
category badge needs a different token.

---

## 3. The type-scale decision you flagged

At the 412px target viewport the four tokens compute to **≈10 / 14 / 20 / 29px**. Every current
arbitrary size, mapped to its nearest token:

| Current | → | At 412px | Delta |
|---|---|---|---|
| `text-[10px]` | `--text-1` | 10.1px | +0.1 |
| `text-[11px]` | `--text-1` | 10.1px | −0.9 |
| `text-[12px]` | `--text-1` | 10.1px | −1.9 |
| `text-[13px]` | `--text-2` | 14.2px | +1.2 |
| `text-[15px]` | `--text-2` | 14.2px | −0.8 |
| `text-[17px]` | `--text-2` | 14.2px | **−2.8** |
| `text-[19px]` | `--text-3` | 20.4px | +1.4 |
| `text-[22px]` | `--text-3` | 20.4px | −1.6 |

**So both `15px` and `17px` map to `--text-2`.** That was the open question; it is now answered by
arithmetic rather than taste.

### But read this before applying it

**`text-[17px]` → 14.2px is a 2.8px drop, and it is used on `SectionHeader` and `EmptyState`
titles.** Collapsing 8 sizes into 4 flattens the hierarchy — 17px headers and 15px body would
become the same size. That is a visible design change, not a refactor.

The design system anticipates exactly this. `frontend/guide/README.md`:

> *"`foundation.gestalt.*` in tokens.json carries Gestalt's full-fidelity scales verbatim (16-step
> spacing, 8-step rounding, full opacity/elevation/motion/type-size scales) **for component work
> that needs more granularity than the 4-token core**."*

**Two options — pick one before Phase 2 starts:**

**A. Strict 4-token core.** Everything maps per the table. Cleanest system, and section headers
lose their step above body text. You would restore hierarchy through weight and colour instead of
size — which is arguably better typography, but it is a redesign of every screen's heading rhythm.

**B. Core plus one.** Use the four tokens for 90% and add a single `--text-2-lg` (≈17px, fluid
16→20) for section headers, citing the documented granularity escape hatch. Preserves the current
visual hierarchy exactly. Costs one non-core token.

**My read: B.** The 4-token core is a constraint on *arbitrary* values, not a ban on a fifth
deliberate step, and the system explicitly provides for it. A is defensible but turns a mechanical
phase into a design review of 205 screens — which is not what Phase 2 is for.

**This is your call, not Claude Code's.** Decide before the branch opens.

---

## 4. Radius mapping

```
rounded-[24px]   → rounded-3   (32px)   ← cards
rounded-[20px]   → rounded-2   (16px)   ← off-scale, round down
rounded-t-[32px] → rounded-3   (32px)
rounded-3xl      → rounded-3   (32px)
rounded-2xl      → rounded-2   (16px)
rounded-xl       → rounded-2   (16px)
rounded-full     → rounded-pill
```

`20px → 16px` is the only judgement call and it is small. Rounding *down* keeps it distinct from
the 32px card radius, which is the shape doing the work.

---

## 5. Shared constants

`max-w-[430px]` appears in `Screen`, `BottomNav`, `Sheet` and `Dialog`, paired with `pb-24`
clearance against an `h-16` nav plus safe-area inset. Extract:

```ts
export const SHELL_MAX_W = "max-w-[430px]";
export const NAV_H = 64;                    // h-16
export const NAV_CLEARANCE = "pb-24";       // must exceed NAV_H + safe-area
```

Four hardcoded copies that must agree is three chances to drift.

---

## 6. Acceptance

- `bunx tsc --noEmit` · `bun run lint` · `bun run build` all clean
- `grep -rn "rounded-\[" src/` → nothing
- `grep -rn "text-\[" src/` → nothing
- `grep -n "shadows avoided" src/styles.css` → nothing
- Visual diff at 412px on `/`, `/feed`, `/me`, `/marketplace`, `/bands`, `/reviews`
- Keyboard-tab any screen: focus ring visible on every control
- Every `--accent` consumer reviewed; purple appears only on category badges

**Screenshot before and after at 412px.** This is the one phase where the diff being green tells
you nothing about whether it is right.
