---
doc: WIDGET-ADOPTION-SPEC
version: 1.0
status: AWAITING DECISION
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
folder: specs
originally: WIDGET-ADOPTION-SPEC.md
---

> **AWAITING DECISION** · v1.0 · 2026-09-13. 116 widget families. Blocked on one decision: import the CSS, port to React, or tokens-only.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

# Adopting `ml-widgets.css` in the Lovable app

Verified against `wugweb-git/musiclinkd` on the Mac and the Lovable repo at HEAD `315564c5`.

---

## 1. The headline: you don't port these, you import them

| file | size | what |
|---|---|---|
| `ml-tokens.css` | 200 lines · 9 KB | the foundation variables |
| `ml-core.css` | 66 KB · 50 class families | `.ml-media` `.ml-chip` `.ml-btn` `.ml-avatar` — the primitives the widgets build on |
| `ml-widgets.css` | 2,635 lines · 152 KB · **116 widget families** | the widgets |
| `ml-fixes.css` | 21 KB | fix layer — **load last** |

Load order is documented in `ml-fixes.css` itself: *"Load AFTER ml-tokens.css, ml-core.css and
ml-widgets.css."*

**`ml-widgets.css` references 67 CSS variables. 66 of them are defined in `ml-tokens.css`.** The
only exception is `--ml-cal-cell`, which is local to the calendar widget. There are no `@import`s
and no external dependencies. The system is self-contained.

So the Lovable app has been hand-rolling a component kit next to a finished, documented,
state-covered, recycle-counted library of 116 widgets. **That is the largest duplication in this
project** — not `AvailabilityBadge`/`StatusPill`. Two entire component systems.

---

## 2. The one thing that makes this cheap or expensive: the name collision

The app and the guide use **different names for the same values**.

| concept | Lovable `styles.css` | guide `ml-tokens.css` |
|---|---|---|
| page ground | `--background` | `--color-paper` |
| card fill | `--card` | `--color-white` |
| sunken surface | `--surface-subtle` | `--color-surface` |
| hairline | `--border` | `--color-line` |
| secondary text | `--muted-foreground` | `--color-muted` |
| accent | `--primary` | `--color-pink` |
| accent text | `--pink-strong` | `--color-pink-pressed` |
| accent tint | `--pink-container` | `--color-pink-container` |
| easing | `--easing-standard` | `--ease-standard` |

Already identical, no work: `--type-1..4`, `--elevation-1..4`, `--duration-1..4`, `--font-display`.

**Entirely absent from the app, arriving free with `ml-tokens.css`:**
`--space-1..4` · `--weight-1..4` · `--radius-1..4` · `--size-1..4` · `--icon-1..3` ·
`--avatar-1..4` · `--z-1..4` · `--border-1/2` · `--media-aspect-*` · `--safe-*` · `--status-*`

Usage is dominated by spacing: `--space-2` appears **136 times**, `--space-3` **123 times**,
`--color-white` 99, `--type-1` 95, `--color-muted` 86.

**The decision — yours, and it decides everything downstream:** import `ml-tokens.css` as the
single foundation and alias the app's semantic names onto it, or keep two palettes in sync by
hand. Aliasing is one block of nine lines. Two palettes is forever.

```css
/* if aliasing: the whole bridge */
:root {
  --background:       var(--color-paper);
  --card:             var(--color-white);
  --surface-subtle:   var(--color-surface);
  --border:           var(--color-line);
  --muted-foreground: var(--color-muted);
  --primary:          var(--color-pink);
  --pink-strong:      var(--color-pink-pressed);
  --pink-container:   var(--color-pink-container);
  --easing-standard:  var(--ease-standard);
}
```

Note that this would move the page ground back to `--color-paper` `#FCFCFD`. The app deliberately
uses `#ECECF1` because `#FCFCFD` measured 1.025:1 against the white card. **`tokens.json` records
that change in `--color-paper`'s description but never applied it to the value.** Fix it upstream
before aliasing, or the contrast work from PR #8 is undone by the import.

---

## 3. Where each widget lands

| widget | reuse in the system | where it belongs in the Lovable app | status today |
|---|---|---|---|
| **profile hero** | **51 screens · 20 flows** | `/profile/:username`, `/me`, `ProfilePreviewSheet`, `ml/person-profile.tsx` | hand-rolled |
| **media card** | 10 screens · 5 flows | `index.tsx` "Trending this week" | hand-rolled **and uses retired `from-navy/85`** |
| **audio player** | 7 screens · 3 flows | `ml/portfolio.tsx` | bare `<audio>` / `<video>` with scoped a11y disables |
| **surface effects** `.ml-glass` `.ml-grain` `.ml-lift` | 6 screens · 2 flows | public profile views | absent |
| **inset card** | 5 screens · 4 flows | marketplace cards, `/plans` | hand-rolled |
| **dimension grid** | 1 screen · 1 flow | `index.tsx` category rail, `/categories` | hand-rolled |
| **lede** | 1 screen · 1 flow | `/legal/*`, `/help/:slug` | absent |
| **featured studio card** | **0 — built, never used** | `/studio`, Discover "Nearby" | absent both sides |
| tooltip · popover · help button · sticker | **0 — built, never used** | — | absent both sides |

**Start with profile hero.** 51 screens across 20 flows is more reuse than the next four combined,
and it is the widget the product is actually about.

**The media card is the cleanest single swap.** The guide's version is eight selectors:

```css
.ml-media-card::after        /* the scrim */
.ml-media-card__tag          /* pill, top-left */
.ml-media-card__body         /* title block, bottom */
.ml-media-card__title        /* --font-display, --type-3, --weight-4 */
.ml-media-card__sub          /* --type-1, white at 85% */
.ml-media-card:active        /* scale(0.98) */
```

`index.tsx` currently reimplements exactly this with `bg-gradient-to-t from-navy/85`, `text-[15px]`,
`text-[11px]` and `rounded-3xl` — a hand-rolled copy that also reaches for a retired colour.

---

## 4. What this does NOT solve

These widgets are **CSS classes**. The Lovable app is **Tailwind utility classes on React
components**. Adopting them means those components render `className="ml-media-card"` instead of a
utility string — which is a different styling mechanism from the one the project knowledge
mandates (*"One styling mechanism: Tailwind + semantic tokens"*).

That rule now has to change or the widgets can't be used. Three honest options:

1. **Import the CSS, use the classes.** Cheapest, fastest, and the widgets stay in sync with the
   guide because they *are* the guide. Costs: two styling mechanisms in one app.
2. **Port each widget to a React component** with Tailwind. Keeps one mechanism. Costs: 116
   widgets re-expressed by hand, and they drift from the guide the day after.
3. **Import the tokens only**, keep building Tailwind components against them. No widgets adopted,
   but the two systems finally share a foundation.

I am not choosing. But note that (2) is the status quo that produced the duplication, and the
recycle counts — 51 screens, 10 screens, 7 screens — are evidence that the guide's versions are
the more exercised ones.

---

## 5. Before anything

**Pin a commit.** `ml-fixes.css` was modified **10 Sep 20:13** — the same evening as the Lovable
work. The guide is moving. Adopting a moving target without a pinned ref is how `tokens.json` and
`ml-tokens.css` ended up disagreeing about `--color-paper` in the first place.

**Confirm `.ml-value`.** It is referenced by `ml-inset__foot` and defined in `ml-widgets.css`, not
`ml-core.css` — so the inset card is self-contained, but check the same for any other widget you
adopt piecemeal. `.ml-media`, `.ml-chip`, `.ml-btn` and `.ml-avatar` all live in `ml-core.css`,
which means **core is not optional** for most widgets.
