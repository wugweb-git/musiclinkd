# Token map — MusicLinkd app

Every design token the app defines, what it is worth, how often it is used, and every place the code
sets the same kind of value by hand instead. Written to be swept: each drift group ends in a
conversion table (hand-set value → token, call sites, how the pixel changes), and the last two
sections give the sweep order and the choices only the owner can make.

**Measured** 16 Sep 2026 from the read-only worktree at `post-migrations`, commit `14e13ab`
("feat(db): The Kite Notes and Static Bloom get members from real profiles", 2026-09-16 09:41 +0530),
which is `origin/main`. Tailwind CSS 4.3.3. No app code was changed to produce this.

**What is mapped.** `src/styles.css` (the `@theme inline` block, `:root`, `@layer base`, every
`@utility` and keyframe), and every class token in `src/**/*.{ts,tsx}`. Tailwind's own theme defaults
(`node_modules/tailwindcss/theme.css`) are quoted where a utility resolves to them rather than to a
token.

**What is not mapped.** There is no Tailwind config file — the theme lives entirely in
`src/styles.css` (`@import "tailwindcss" source(none)` + `@source "../src"`), so there is nothing else
to read. `tokens.json` and the generated `ml-tokens.css` that the CSS comments cite are **not in this
repo**; they live in the guide repo, so this map is measured from the CSS, not from the generator.
`src/routeTree.gen.ts` is generated and excluded. Storybook stories and `src/lab/` are excluded from
the headline counts and reported separately as `lab`.

**Buckets.** 380 files were scanned. Counts are split because two thirds of the off-token values sit
in code the app never reaches:

| Bucket | Files | What it is |
| --- | ---: | --- |
| `product` | 314 | `src/routes`, `src/components/ml`, `src/lib`, `src/hooks`, `src/data`, `src/integrations`, root files |
| `vendor` | 56 | `src/components/ui` (shadcn), `src/components/kibo-ui`, `src/components/waves-cn` |
| `lab` | 7 | `*.stories.tsx`, `src/lab/` |
| `docs` | 1 | `src/registry/buttons-and-links.mdx` |
| `css` | 2 | `src/styles.css`, `src/styles/liquefy-ui.css` |
| generated | 1 | `src/routeTree.gen.ts` |

Only three vendor files are reachable from the product: `ui/avatar` (from `ml/viewer.tsx`),
`ui/switch` (from `ml/work-form.tsx`), `ui/sonner` (from `routes/__root.tsx`). Everything else under
`src/components/ui`, `src/components/kibo-ui` and `src/components/waves-cn` is imported only by
stories, or by nothing.

**Unless a table says otherwise, every number is the `product` bucket.**

## How the numbers were counted

1. Every string literal in `src/**/*.{ts,tsx}` (double, single and backtick) is extracted.
2. Each literal is split on whitespace and on `${` / `}` template boundaries. Each fragment is a
   candidate class token. Tailwind arbitrary values cannot contain a space, so this does not split
   `shadow-[0_1px_3px_...]`.
3. Variant prefixes are stripped at bracket depth zero (`hover:`, `md:`, `data-[state=open]:`), as
   are a leading `!` or `-`. What remains is matched against the group's pattern.
4. **Counts are occurrences of the class token, not lines and not files.** A className that says
   `text-[13px]` twice counts twice. File counts are given alongside.
5. `var(--token)` counts are plain `grep -o` over `src/**/*.{ts,tsx,css}`, reported separately for
   `src/styles.css` and for everything else.
6. Element attribution for type sizes ("what the element is") is the nearest preceding `<Tag` before
   the match. It is an approximation and is only used to describe shape, never as a count of
   anything else.
7. Raw counts: `token_map.json`, written beside this document in the session scratchpad. Re-derivable by re-running the same
   extraction against the same commit.

Two independent cross-checks against `grep -c` agreed exactly: `border-border` 483 in all of
`src/**/*.{ts,tsx}` (481 product + 1 vendor + 1 lab), `text-muted-foreground` 1,012 (968 + 37 + 7).

---

## 1. Font family

| Token | Value | Tailwind utility | Utility uses | `var()` uses |
| --- | --- | --- | ---: | ---: |
| `--font-sans` | `"Inter", ui-sans-serif, system-ui, sans-serif` | `font-sans` | 0 | 1 (`styles.css`, `body`) |
| `--font-display` | `"Inter", ui-sans-serif, system-ui, sans-serif` | `font-display` | 0 | 1 (`styles.css`, `h1–h4`) |

Both tokens hold the same stack. Neither utility is written anywhere; the family is applied once in
`@layer base` on `body` and once on `h1–h4`.

**Drift:** `font-mono` × 1, vendor only (`ui/chart.tsx`). Zero `font-[...]` arbitrary families.
Zero hand-set `font-family` in any `style={{}}` in product code.

---

## 2. Type scale (size) — the largest drift group in the app

Six steps, fluid between 360px and 1440px. The design target is 412px.

| Token | Value | Tailwind utility | At 412px | At 1440px | Line height it carries | Uses | Files |
| --- | --- | --- | ---: | ---: | ---: | ---: | ---: |
| `--type-1` → `--text-1` | `clamp(12px, 12px + 1×(100vw−360px)/1080, 13px)` | `text-1` | 12.05px | 13px | 1.45 | 340 | 41 |
| `--type-sm` → `--text-sm` | `clamp(14px, … +1 …, 15px)` | `text-sm` | 14.05px | 15px | 1.43 | 267 | 99 |
| `--type-2` → `--text-2` | `clamp(16px, … +2 …, 18px)` | `text-2` | 16.10px | 18px | 1.5 | 77 | 26 |
| `--type-3` → `--text-3` | `clamp(22px, … +6 …, 28px)` | `text-3` | 22.29px | 28px | 1.25 | 17 | 14 |
| `--type-4` → `--text-4` | `clamp(32px, … +12 …, 44px)` | `text-4` | 32.58px | 44px | 1.1 | 1 | 1 |
| `--type-5` → `--text-5` | `clamp(40px, … +16 …, 56px)` | `text-5` | 40.77px | 56px | 1.04 | 1 | 1 |

`text-sm` keeps Tailwind's name but resolves to `--type-sm`, so it is a token, not a default.
Vendor adds 63 more token-size uses (62 of them `text-sm`).

`text-4` is used once (`routes/onboarding/index.tsx`) and `text-5` once (`routes/welcome.tsx`),
which is what the CSS comment asks for on `--text-5` but not on `--text-4` — screen titles are
supposed to sit on `text-4`.

### 2a. Drift: arbitrary pixel sizes

**998 occurrences across 17 distinct sizes in 156 product files.** (1,004 across all of `src`; 6 are
vendor, all `text-[0.8rem]` in `ui/calendar.tsx`. The registry MDX quotes 50 more inside code
samples — documentation, not drift.) One further `text-[…]` is not a size at all:
`text-[color:var(--success)]` in `ml/collab.tsx`.

AGENTS.md states the rule this breaks: *"Type: `text-1`, `text-sm`, `text-2`, `text-3`, `text-4`,
`text-5` (hero). No `text-xs/base/lg`."*

| Size | Uses | Files | In a template literal | Has `leading-*` in the same class string | Mostly on |
| --- | ---: | ---: | ---: | ---: | --- |
| `text-[12px]` | 241 | 98 | 3 | 3 | `p` 172, `span` 20, `button` 17 |
| `text-[13px]` | 230 | 98 | 0 | 18 | `p` 171, `span` 20, `div` 8 |
| `text-[11px]` | 214 | 93 | 3 | 1 | `p` 112, `span` 49, `button` 19, `Price` 14 |
| `text-[15px]` | 84 | 40 | 0 | 4 | `h2` 59, `p` 9, `h3` 9 |
| `text-[17px]` | 51 | 29 | 0 | 9 | `h2` 37, `h3` 6 |
| `text-[14px]` | 47 | 27 | 1 | 2 | `p` 16, `h3` 13, `input` 6 |
| `text-[10px]` | 43 | 21 | 0 | 0 | `p` 15, `span` 15, `Price` 8 |
| `text-[16px]` | 38 | 15 | 0 | 4 | `h2` 25, `h1` 11 |
| `text-[19px]` | 15 | 13 | 0 | 15 | `h2` 13 |
| `text-[20px]` | 10 | 9 | 0 | 5 | `h2` 5, `h1` 2, `p` 2 |
| `text-[18px]` | 8 | 7 | 0 | 2 | `h1` 2, `h2` 2 |
| `text-[22px]` | 6 | 6 | 0 | 3 | `h1` 4 |
| `text-[24px]` | 5 | 5 | 0 | 5 | `h1` 3 |
| `text-[28px]` | 2 | 2 | 0 | 2 | `h1` 2 |
| `text-[34px]` | 2 | 2 | 0 | 2 | `p` 2 |
| `text-[30px]` | 1 | 1 | 0 | 1 | `h1` 1 |
| `text-[26px]` | 1 | 1 | 0 | 1 | `h1` 1 |

Top files: `routes/verification.$type.tsx` 57 · `ml/business-profile.tsx` 23 ·
`routes/discover.index.tsx` 22 · `routes/opportunities.new.tsx` 22 ·
`routes/marketplace/rent/e.$id.tsx` 22 · `routes/marketplace/rent/list.tsx` 22 ·
`routes/opportunities.$id.apply.tsx` 21 · `routes/services.new.tsx` 20 ·
`routes/services.$id.index.tsx` 19 · `routes/business.new.tsx` 18.

The shape is consistent: **11–13px on `p`/`span` body and caption text** (685 of 998, 69%), and
**15–19px on `h2`/`h3` section headings** (150 of 998, 15%).

### 2b. Drift: Tailwind's own size utilities

**222 occurrences, 6 distinct sizes, 72 files** (vendor adds 28). These are not tokens: they resolve
to `node_modules/tailwindcss/theme.css`.

| Utility | Tailwind value | Line height it carries | Uses | Files |
| --- | --- | ---: | ---: | ---: |
| `text-xs` | 0.75rem = 12px | 1.333 | 175 | 64 |
| `text-xl` | 1.25rem = 20px | 1.4 | 19 | 11 |
| `text-lg` | 1.125rem = 18px | 1.556 | 14 | 10 |
| `text-2xl` | 1.5rem = 24px | 1.333 | 10 | 9 |
| `text-base` | 1rem = 16px | 1.5 | 3 | 2 |
| `text-7xl` | 4.5rem = 72px | 1 | 1 | 1 |

Top files: `ml/booking.tsx` 24 · `ml/market.tsx` 10 · `routes/marketplace/rent/active.$id.tsx` 9 ·
`routes/marketplace/rent/list.tsx` 7.

### 2c. Type group totals

| | Occurrences | Share |
| --- | ---: | ---: |
| On a token (`text-1/sm/2/3/4/5`) | 703 | 36.5% |
| Arbitrary px/rem | 998 | 51.9% |
| Tailwind default step | 222 | 11.5% |
| Non-size `text-[…]` | 1 | — |
| **All font-size utilities (product)** | **1,924** | |

### 2d. Conversion table — arbitrary px → token

Deltas are at the 412px design target. A fixed px value does not grow with the viewport; the token
does, so the second delta (at 1440px) is given where it matters. "Same class string already sets
`leading-*`" means the line height will **not** move on that site.

| Hand-set | Token | Sites | Font-size delta @412 | Delta @1440 | Line-height effect | Fidelity |
| --- | --- | ---: | ---: | ---: | --- | --- |
| `text-[12px]` | `text-1` | 241 | +0.05px | +1px | 1.5 (inherited) → 1.45, −0.6px | **identical** at phone |
| `text-[13px]` | `text-1` | 230 | −0.95px | 0 | 1.5 → 1.45, −1.7px (18 sites already pin `leading-*`) | small shift |
| `text-[11px]` | — | 214 | — | — | — | **no step** (see 2e) |
| `text-[15px]` | `text-sm` | 84 | −0.95px | 0 | 1.5 → 1.43, −2.4px | small shift |
| `text-[17px]` | `text-2` | 51 | −0.90px | +1px | 1.5 → 1.5, ≈0 | small shift |
| `text-[14px]` | `text-sm` | 47 | +0.05px | +1px | 1.5 → 1.43, −0.9px | **identical** at phone |
| `text-[10px]` | — | 43 | — | — | — | **no step** (see 2e) |
| `text-[16px]` | `text-2` | 38 | +0.10px | +2px | 1.5 → 1.5, ≈0 | **identical** at phone |
| `text-[19px]` | — | 15 | — | — | 15 of 15 already pin `leading-*` | **no step** (see 2e) |
| `text-[20px]` | — | 10 | — | — | — | **no step** (see 2e) |
| `text-[18px]` | `text-2` | 8 | −1.90px | 0 | 1.5 → 1.5, ≈0 | judgement call |
| `text-[22px]` | `text-3` | 6 | +0.29px | +6px | 1.5 → 1.25, −4.6px | **identical** at phone, large at desktop |
| `text-[24px]` | `text-3` | 5 | −1.71px | +4px | 5 of 5 already pin `leading-*` | judgement call |
| `text-[28px]` | `text-3` | 2 | −5.71px | 0 | both already pin `leading-*` | judgement call |
| `text-[34px]` | `text-4` | 2 | −1.42px | +10px | both already pin `leading-*` | judgement call |
| `text-[30px]` | `text-3` or `text-4` | 1 | −7.71px / +2.58px | −2px / +14px | already pins `leading-*` | judgement call |
| `text-[26px]` | `text-3` | 1 | −3.71px | +2px | already pins `leading-*` | judgement call |
| `text-[0.8rem]` (12.8px) | `text-1` | 6 (vendor) | −0.75px | +0.2px | 1.5 → 1.45 | small shift |

Mechanical, visually identical at 412px: **332 sites** (12px, 14px, 16px, 22px).
Small shift under 1px: **365 sites** (13px, 15px, 17px).
Judgement call: **19 sites** (18px and everything ≥ 24px).
No step: **282 sites** (10px, 11px, 19px, 20px) — below.

### 2e. The four sizes with no step

Every option, with the pixel delta each way. No recommendation.

**`text-[10px]` — 43 sites, 21 files.** Mostly `span`/`p` micro-labels and 8 `Price` unit lines.

| Option | Result @412 | Result @1440 | Notes |
| --- | --- | --- | --- |
| Round up to `text-1` | +2.05px (10 → 12.05) | +3px | 20% jump; the biggest single relative change in the sweep |
| Add a `--type-0` step at `clamp(10px, …, 11px)` | 0 | +1px | Makes the scale seven steps; the CSS comment says every foundation group other than type is "exactly four" |
| Leave as is | 0 | 0 | 43 sites stay off-token |

**`text-[11px]` — 214 sites, 93 files.** This is also exactly the `pill` utility's own font size
(`font-size: 11px` in `@utility pill`), so the value is already in the system — as a utility, not as
a scale step.

| Option | Result @412 | Result @1440 | Notes |
| --- | --- | --- | --- |
| Round up to `text-1` | +1.05px | +2px | 214 sites grow ~9%; affects 93 files |
| Add an eyebrow step (`--type-0` / `--text-eyebrow` = 11px flat) | 0 | 0 | Matches `pill`; 52 of these 214 are already eyebrow labels (§4b) |
| Move the eyebrow sites onto `pill` or a new eyebrow utility, round the rest | mixed | mixed | Splits the 214 into ~52 label + ~162 body/caption |
| Leave as is | 0 | 0 | |

**`text-[19px]` — 15 sites, 13 files.** All but two are `h2`. All 15 already set `leading-*`, so only
font-size would move.

| Option | Result @412 | Result @1440 |
| --- | --- | --- |
| Down to `text-2` | −2.90px | −1px |
| Up to `text-3` | +3.29px | +9px |
| Add a step between 18 and 22 | 0 | — |

**`text-[20px]` — 10 sites, 9 files.** `h2` 5, `h1` 2, `p` 2, `span` 1. 5 of 10 pin `leading-*`.

| Option | Result @412 | Result @1440 |
| --- | --- | --- |
| Down to `text-2` | −3.90px | −2px |
| Up to `text-3` | +2.29px | +8px |
| Add a step | 0 | — |

**`text-xl` (20px, 19 sites) falls on the same gap** and would take the same answer.

### 2f. Conversion table — Tailwind default → token

| Hand-set | Token | Sites | Delta @412 | Delta @1440 | Line height | Fidelity |
| --- | --- | ---: | ---: | ---: | --- | --- |
| `text-xs` (12px) | `text-1` | 175 | +0.05px | +1px | 1.333 → 1.45, +1.4px | **identical** at phone |
| `text-base` (16px) | `text-2` | 3 | +0.10px | +2px | 1.5 → 1.5 | **identical** at phone |
| `text-lg` (18px) | `text-2` | 14 | −1.90px | 0 | 1.556 → 1.5 | judgement call |
| `text-2xl` (24px) | `text-3` | 10 | −1.71px | +4px | 1.333 → 1.25 | judgement call |
| `text-xl` (20px) | — | 19 | — | — | — | **no step** — same gap as `text-[20px]` |
| `text-7xl` (72px) | — | 1 | — | — | — | **no step** — above `text-5` (40.8–56px). `routes/__root.tsx`, the 404 numeral |

---

## 3. Weight

There are **no weight tokens in the CSS**. Every weight utility resolves to Tailwind's
`--font-weight-*` defaults. `@layer base` sets `h1–h4 { font-weight: 700 }`.

| Utility | Resolves to | Uses | Files |
| --- | ---: | ---: | ---: |
| `font-semibold` | 600 | 546 | 179 |
| `font-bold` | 700 | 175 | 74 |
| `font-medium` | 500 | 34 | 16 |
| `font-normal` | 400 | 3 | 2 |
| **Total** | | **758** | **188** |

Vendor adds 46. Zero `font-[500]`-style arbitrary weights anywhere.

**Drift within the group.** 53 headings restate a weight the base layer already sets or override it:
`h1 font-bold` 17, `h2 font-bold` 12, `h2 font-semibold` 8, `h3 font-bold` 7, `h3 font-semibold` 5,
`h1 font-semibold` 3, `h4 font-semibold` 1. The 17 `h1 font-bold` and 12 `h2 font-bold` are
redundant; the 17 `font-semibold` on headings are live overrides that pull 700 down to 600.

**Conversion table**

| Hand-set | Token | Sites | Change | Fidelity |
| --- | --- | ---: | --- | --- |
| `font-bold` on `h1`–`h4` | delete (base layer already gives 700) | 36 | none | **identical** |
| `font-semibold` on `h1`–`h4` | keep as an override, or delete to inherit 700 | 17 | 600 → 700 if deleted | judgement call |
| every other weight | no token exists | 705 | — | nothing to swap until weight tokens exist |

---

## 4. Style — italic, letter spacing, case

### 4a. Italic

**Zero.** No `italic` or `not-italic` anywhere in `src`, product or vendor. No `font-style` in any
inline style. Nothing to map and nothing to fix.

### 4b. Letter spacing

No tracking tokens in the CSS. The only spacing the system defines is inside `@utility pill`
(`letter-spacing: 0.08em`).

| Utility | Value | Uses | Files |
| --- | --- | ---: | ---: |
| `tracking-wide` | 0.025em (Tailwind default) | 78 | 39 |
| `tracking-[0.14em]` | 0.14em | 6 | 5 |
| `tracking-[0.18em]` | 0.18em | 2 | 2 |
| `tracking-widest` | 0.1em (Tailwind default) | 1 | 1 |
| **Total** | | **87** | **46** |

Vendor adds 8 (`tracking-tight` 4, `tracking-widest` 4). So the app uses **four** letter-spacing
values and the system defines a fifth (0.08em, inside `pill`) that no class can reach.

### 4c. Case

| Utility | Uses | Files |
| --- | ---: | ---: |
| `uppercase` | 101 | 51 |
| `lowercase` | 3 | 2 |
| `capitalize` | 2 | 2 |
| **Total** | **106** | **54** |

### 4d. Drift: labels built by hand instead of `pill`

A class string that carries both `uppercase` and `tracking-*` and does **not** use `pill`:
**87 occurrences in 46 files** (vendor 0). Against `pill` + `pill-glass` used **25 times in 14 files**.

The recipes:

| Recipe | Sites |
| --- | ---: |
| `text-[11px] font-bold uppercase tracking-wide text-muted-foreground` | 41 |
| `text-1 font-bold uppercase tracking-wide text-muted-foreground` | 21 |
| `text-[11px] font-bold uppercase tracking-[0.14em] text-muted-foreground` | 5 |
| `text-[11px] uppercase tracking-wide text-muted-foreground` | 4 |
| `text-[13px] font-bold uppercase tracking-wide text-muted-foreground` | 4 |
| `text-1 font-bold uppercase tracking-wide` (no colour) | 2 |
| `text-[11px] font-bold uppercase tracking-wide` (no colour) | 2 |
| 8 further recipes, 1 site each (`text-[10px]`, `text-[12px]`, `text-xs`, `tracking-[0.18em]`, `tracking-widest`, `font-semibold`) | 8 |

**15 distinct recipes for one visual object.**

Top files: `routes/marketplace/rent/list.tsx` 8 · `routes/services.new.tsx` 6 ·
`routes/marketplace/rent/search.tsx` 4 · `ml/profile.tsx` 4 · `routes/portfolio.settings.tsx` 3 ·
`routes/verification.choose.tsx` 3 · `routes/marketplace/rent/active.$id.tsx` 3 · `ml/booking.tsx` 3.

**These are not pills.** `pill` is a badge: inline-flex, `gap`, `border-radius: 9999px`,
`padding: 0.125rem 0.5rem`, 11px / 500 / 0.08em, and it expects a background from the caller.
The 87 hand-built ones are **section eyebrows** — a bare caption above a block, no background, no
padding, 11–13px / 700 / 0.025em. AGENTS.md distinguishes Chip from Pill but has no name for this
third thing.

**Conversion table**

| Hand-set | Target | Sites | Change if swapped to `pill` as it stands | Fidelity |
| --- | --- | ---: | --- | --- |
| `text-[11px] … tracking-wide` eyebrow | `pill` | 41 | weight 700 → 500, tracking 0.025em → 0.08em, gains 2px/8px padding and a pill radius | **not a swap** — needs a new eyebrow utility, or `pill` gains a variant |
| `text-1 … tracking-wide` eyebrow | `pill` | 21 | size 12.05px → 11px, plus the above | **not a swap** — same |
| `tracking-[0.14em]` / `[0.18em]` eyebrow | `pill` (0.08em) | 9 | tracking roughly halves | judgement call |
| remainder | — | 16 | — | one-offs, decide per site |

---

## 5. Colour

37 colour tokens are exposed to Tailwind through `--color-*` in `@theme inline`; each one is an
alias of a raw custom property in `:root`. Counts below are every colour-carrying utility for that
token (`bg-`, `text-`, `border-`, `ring-`, `fill-`, `stroke-`, `from-`, `via-`, `to-`, `divide-`,
`outline-`, `decoration-`, `placeholder-`, `caret-`, `accent-`, `shadow-`), alpha modifiers included.

| Token | Value | Product uses | Files | Vendor | Busiest utility |
| --- | --- | ---: | ---: | ---: | --- |
| `muted-foreground` | `#66666F` | 982 | 198 | 37 | `text-muted-foreground` 968 |
| `border` | `#D5D5DE` | 547 | 142 | 12 | `border-border` 481, `divide-border` 44, `bg-border` 19 |
| `card` | `oklch(1 0 89.88)` = white | 369 | 117 | 2 | `bg-card` 348 |
| `subtle` | `#F8F8FB` (`--surface-subtle`) | 226 | 85 | 1 | `bg-subtle` 225 |
| `primary` | `#0D0D0D` | 190 | 87 | 18 | `bg-primary` 78, `border-primary` 56, `text-primary` 50 |
| `foreground` | `oklch(0.1591 0 89.88)` | 173 | 77 | 19 | `text-foreground` 153 |
| `background` | `#ECECF1` | 132 | 76 | 16 | `bg-background` 64, `text-background` 34 |
| `primary-foreground` | `oklch(1 0 89.88)` = white | 40 | 29 | 11 | `text-primary-foreground` 38 |
| `ink` | `#0D0D0D` | 35 | 20 | 1 | `bg-ink` 23 |
| `destructive` | `oklch(0.5771 0.2152 27.33)` | 33 | 11 | 11 | `text-destructive` 22 |
| `success` | `oklch(0.7227 0.192 149.58)` | 15 | 10 | 1 | `bg-success` 9, `text-success` 6 |
| `warn` | `oklch(0.7686 0.1647 70.08)` | 15 | 10 | 0 | `text-warn` 9, `bg-warn` 6 |
| `success-container` | `color-mix(success 12%, transparent)` | 12 | 10 | 0 | `bg-success-container` 12 |
| `warn-container` | `color-mix(warn 14%, transparent)` | 10 | 6 | 0 | `bg-warn-container` 10 |
| `success-strong` | `color-mix(success 52%, black)` | 9 | 7 | 0 | `text-success-strong` 9 |
| `price` | `#C7295F` (alias of `--pink-strong`) | 8 | 4 | 0 | `text-price` 8 |
| `warn-strong` | `color-mix(warn 48%, black)` | 7 | 5 | 0 | `text-warn-strong` 7 |
| `selected-text` | `#FFFFFF` | 6 | 5 | 0 | `text-selected-text` 6 |
| `danger-container` | `color-mix(danger 12%, transparent)` | 5 | 4 | 0 | `bg-danger-container` 5 |
| `status-category-bg` | `color-mix(purple 12%, #fff)` | 5 | 4 | 0 | `bg-status-category-bg` 5 |
| `status-category-text` | `color-mix(purple 72%, black)` | 5 | 4 | 0 | `text-status-category-text` 5 |
| `selected-bg` | `#0D0D0D` | 5 | 5 | 0 | `bg-selected-bg` 5 |
| `brand` | `#EA4C89` | 4 | 4 | 0 | `text-brand` 2, `bg-brand` 2 |
| `accent` | `#F8F8FB` | 1 | 1 | 33 | `bg-accent` 1 |
| `input` | `#D5D5DE` | 1 | 1 | 8 | `border-input` 1 |
| `ring` | `#0D0D0D` | 1 | 1 | 21 | `ring-ring` 1 |
| `pink-container` | `#FCE7F3` | 1 | 1 | 0 | `bg-pink-container` 1 |
| `pink-strong` | `#C7295F` | 1 | 1 | 0 | `text-pink-strong` 1 |
| `purple` | `oklch(0.6056 0.2189 292.72)` | 0 | 0 | 0 | — reached only through the two `status-category-*` mixes |
| `card-foreground` | white-on-card | 0 | 0 | 1 | — |
| `popover` | `oklch(1 0 89.88)` | 0 | 0 | 12 | — vendor only |
| `popover-foreground` | ink | 0 | 0 | 11 | — vendor only |
| `secondary` | `#F8F8FB` | 0 | 0 | 5 | — vendor only |
| `secondary-foreground` | `oklch(0.1713 0.0539 282.44)` | 0 | 0 | 2 | — vendor only |
| `muted` | `#F8F8FB` | 0 | 0 | 14 | — vendor only |
| `accent-foreground` | ink | 0 | 0 | 26 | — vendor only |
| `destructive-foreground` | white | 0 | 0 | 2 | — vendor only |

Defined in `:root` with **no `--color-*` alias**, so no utility can reach them:
`--purple-container`, `--danger`, `--gradient-brand`, `--gradient-ink` (the last two are reached
through the `brand-gradient` / `ink-gradient` utilities).

Four tokens hold the same value: `--primary`, `--ink`, `--ring`, `--selected-bg` are all `#0D0D0D`.
Three hold `#F8F8FB`: `--secondary`, `--muted`, `--accent`, plus `--surface-subtle`.
`--input` equals `--border`. `--color-price` is a second name for `--pink-strong`.

### 5a. Drift: hard-coded colour

**Zero hex inside any Tailwind arbitrary value.** The owner's measurement is confirmed: a grep for
`*-[#rrggbb]` across `src/**/*.{ts,tsx}` returns nothing. Also zero `rgb()`, `hsl()` or `oklch()`
inside an arbitrary value in product code.

Raw colour literals that do exist (37 hex occurrences in `src/**/*.{ts,tsx}`), none of them a UI
surface colour set by hand:

| Where | What | Occurrences |
| --- | --- | ---: |
| `ml/auth.tsx` | Google `G` mark and the Indian flag, as SVG `fill=` | 10 |
| `data/identity.ts` | QR / identity-card palette pairs (`dark` / `light` per style) | 8 |
| `lib/error-page.ts` | the standalone server error page, plain HTML/CSS, outside React | 6 |
| `ui/chart.tsx` | recharts attribute selectors `[stroke='#ccc']` — matchers, not paint | 4 |
| `ui/glasscn/liquid-glass.tsx` | mask gradients `linear-gradient(#000 0 0)` | 4 |
| `ml/qr.tsx` | one radial-gradient backdrop `#374151` | 1 |
| `lib/provider.tsx` | one `tint: "#8eb9ff"` | 1 |
| `routes/__root.tsx` | `<meta name="theme-color" content="#ECECF1">` | 1 |
| `routes/identity.*` | remainder | 2 |

`rgba()`: **38 occurrences, all in two files** — `lib/glass-variants.ts` (30) and
`ui/glasscn/liquid-glass.tsx` (8). `hsl()`: 2, both in `lib/wave-cn.tsx`, one of which wraps
`var(--muted)`. Zero `oklch()` outside `styles.css`.

Named / palette colours through utilities: **70 occurrences, 32 distinct, 9 files** —
`kibo-ui/reel/reel-controlled.tsx` 25, `kibo-ui/reel/index.tsx` 18, `lib/glass-variants.ts` 16,
`kibo-ui/stories/index.tsx` 6, then one each in five vendor dialogs. `text-white` 19, `bg-blue-500`
6, `bg-blue-600` 4, `text-gray-600` 3, `bg-gray-200/300/100` 4, the rest `white/black` with alpha.
**`lib/glass-variants.ts` is the only product-bucket file in this list** (16 occurrences); every
other one is vendor.

### 5b. Drift: alpha modifiers

A colour modifier (`bg-ink/80`) is the system's own escape hatch, but the opacity steps
(`--opacity-1..4` = .08 / .16 / .32 / .64) are not reachable from one.

**127 occurrences, 64 distinct combinations, 60 files** (product 88, vendor 39).

| Alpha | Uses | Matches an opacity token? |
| --- | ---: | --- |
| `/95` | 23 | no step |
| `/80` | 16 | no step |
| `/90` | 16 | no step |
| `/50` | 14 | no step |
| `/10` | 11 | no step (nearest `--opacity-1` = .08) |
| `/40` | 8 | no step |
| `/60` | 6 | no step |
| `/70`, `/20`, `/85`, `/15`, `/25`, `/30` | 21 | `/15` and `/16`-adjacent: `--opacity-2` = .16 |
| bracketed `[0.04]`…`[0.55]` | 12 | no step; all vendor or `glass-variants.ts` |

Busiest: `bg-background/95` 12, `bg-card/95` 11, `bg-background/90` 6, `bg-card/90` 6 — these are the
sticky headers and the bottom nav. **None of the 26 distinct alpha values lands on an opacity step.**

### 5c. Conversion table — colour

| Hand-set | Token | Sites | Fidelity |
| --- | --- | ---: | --- |
| `text-white` | `text-background` (#ECECF1) or `text-selected-text` (#FFFFFF) | 19 | `selected-text` is **identical**; `background` is a small shift. All 19 are vendor |
| `bg-white`, `bg-black` | `bg-card` / `bg-ink` | 2 | small shift (`card` is pure white, `ink` is #0D0D0D not #000) — vendor |
| `bg-gray-100/200/300`, `text-gray-600` | `bg-subtle` / `text-muted-foreground` | 7 | judgement call — vendor |
| `bg-blue-500`, `bg-blue-600` | no blue in the palette | 10 | **no token** — vendor (`kibo-ui`) |
| `rgba(...)` in `glass-variants.ts` | no glass tokens exist | 30 | **no token** — decide whether glass stays |
| `border border-border` | `hairline` | 413 | **identical** (see §10) |
| alpha modifiers | no opacity token is reachable from a modifier | 88 | **no token** |

---

## 6. Radius

Material 3 shape scale, seven steps, all overriding Tailwind's defaults.

| Token | Value | Tailwind default it replaces | Utility | Uses | Files | Stated role |
| --- | --- | --- | --- | ---: | ---: | --- |
| `--radius-xs` | 4px | 2px | `rounded-xs` | 1 | 1 | — |
| `--radius-sm` | 8px | 4px | `rounded-sm` | 1 | 1 | chips, small controls |
| `--radius-md` | 12px | 6px | `rounded-md` | 71 | 36 | inputs, thumbnails, small tiles |
| `--radius-lg` | 16px | 8px | `rounded-lg` | 377 | 105 | **cards** and list containers |
| `--radius-xl` | 20px | 12px | `rounded-xl` | 0 | 0 | large tiles |
| `--radius-2xl` | 28px | 16px | `rounded-2xl` | 1 | 1 | sheets and dialogs |
| `--radius-3xl` | 32px | 24px | `rounded-3xl` | 0 | 0 | hero / full-bleed |
| (Tailwind) | 9999px | — | `rounded-full` | 345 | 115 | buttons and pills |

Side-specific variants in product: `rounded-t-lg` 1, `rounded-t-2xl` 1, and the four corners of one
QR frame in `ml/qr.tsx` (`rounded-tl-lg`/`tr`/`bl`/`br`, 1 each).

`--radius: 16px` in `:root` is **dead**: it is not in the `--radius-*` Tailwind namespace, no
component reads `var(--radius)`, and nothing in `styles.css` references it. It is shadcn's base
variable, left over.

Vendor adds 110 radius utilities, including the only uses of bare `rounded` (6, = Tailwind's
0.25rem, not a token), `rounded-none` 4, `rounded-xl` 3, `rounded-[2px]` 2, `rounded-[inherit]` 2.

### 6a. Drift: arbitrary radius

**53 occurrences, 6 distinct values, 30 files.** AGENTS.md: *"No arbitrary `rounded-[…]`."*

Top files: `routes/opportunities.new.tsx` 7 · `routes/verification.$type.tsx` 6 ·
`routes/identity.customize.tsx` 3 · `routes/identity.print.tsx` 3 ·
`routes/opportunities.$id.apply.tsx` 3 · `routes/services.$id.enquiry.tsx` 3.

**Conversion table**

| Hand-set | Token | Sites | Delta | Fidelity |
| --- | --- | ---: | ---: | --- |
| `rounded-[16px]` | `rounded-lg` | 24 | 0 | **identical** |
| `rounded-[20px]` | `rounded-xl` | 14 | 0 | **identical** — would give `rounded-xl` its first 14 uses |
| `rounded-[12px]` | `rounded-md` | 3 | 0 | **identical** |
| `rounded-[32px]` | `rounded-3xl` | 1 | 0 | **identical** — would give `rounded-3xl` its first use |
| `rounded-[24px]` | `rounded-xl` 20px / `rounded-2xl` 28px | 6 | −4px / +4px | **no step** |
| `rounded-[14px]` | `rounded-md` 12px / `rounded-lg` 16px | 5 | −2px / +2px | **no step** |

42 of 53 are exact; 11 fall between steps.

---

## 7. Spacing and layout size

Spacing has **one** token and uses Tailwind's numeric scale for everything else — this is deliberate
(`styles.css`: *"The named duplicates `--spacing-xs/sm/md/lg` had zero uses and were removed 13 Sep
so there is one spacing system, not two."*).

| Token | Value | Utility | Uses | Files |
| --- | --- | --- | ---: | ---: |
| `--spacing-nav-clear` | 96px | `pb-nav-clear` (any spacing utility) | 1 | 1 (`ml/ui.tsx`, inside `Screen`) |
| `--container-app` | 430px | `max-w-app` | 27 | 19 |

**Numeric scale usage: 4,517 occurrences across 23 steps** (padding, margin, gap, space only).
The scale is used as intended — 3,697 of 4,517 (82%) sit on steps 1–6:

| Step | px | Uses | | Step | px | Uses |
| ---: | ---: | ---: | --- | ---: | ---: | ---: |
| 3 | 12 | 997 | | 2.5 | 10 | 45 |
| 2 | 8 | 937 | | 0 | 0 | 32 |
| 4 | 16 | 781 | | 3.5 | 14 | 21 |
| 5 | 20 | 584 | | 10 | 40 | 21 |
| 1 | 4 | 398 | | 32 | 128 | 14 |
| 6 | 24 | 266 | | 7 | 28 | 10 |
| 1.5 | 6 | 196 | | 28 | 112 | 7 |
| 0.5 | 2 | 93 | | 40 | 160 | 6 |
| 8 | 32 | 91 | | 12, 16 | 48, 64 | 5 each |
| | | | | 24, 36, 14, 9 | | 3, 3, 1, 1 |

Sizing utilities (`w-`, `h-`, `size-`, `min-*`, `max-*`) on the numeric scale: **1,195 occurrences,
75 distinct**. Busiest: `min-w-0` 244, `h-10` 69, `w-10` 60, `h-12` 50, `w-12` 40, `h-4` 39,
`h-14` 39, `w-11` 39, `h-11` 34.

### 7a. Drift: arbitrary spacing and size

**132 occurrences, 73 distinct values, 68 files** (vendor adds 38, almost all Radix
`--radix-*` plumbing that has to stay arbitrary).

| Value | Uses | Files | Maps to |
| --- | ---: | ---: | --- |
| `min-h-[100dvh]` | 34 | 32 | **no token.** `Screen` in `ml/ui.tsx` uses `min-h-screen` instead, so these 34 also bypass the shell component |
| `max-w-[430px]` | 27 | 25 | `max-w-app` — **identical**. The CSS comment says this was "previously repeated in 55 places"; 27 were migrated, 27 were not |
| `pb-[max(16px,env(safe-area-inset-bottom))]` | 8 | 8 | **no token.** Safe-area padding, three spellings in total |
| `pb-[max(20px,env(safe-area-inset-bottom))]` | 5 | 5 | ditto |
| `max-w-[280px]` | 6 | 6 | **no token** |
| `max-w-[260px]`, `max-w-[60%]`, `h-[124px]` | 3 each | 2 each | **no token** |
| 66 further one-off values | 41 | | mostly fixed card/media heights and widths |

Safe-area spellings in product: `pb-[max(16px,…)]` 8, `pb-[max(20px,…)]` 5,
`pb-[max(14px,…)]` 1, `pb-[env(…)]` 1, `pt-[max(1.5rem,…)]` 1, `pt-[max(0.75rem,…)]` 1,
`pt-[calc(env(…)+16px)]` 1, `pb-[calc(env(…)+24px)]` 1 — **eight different spellings of one idea**.

**Conversion table**

| Hand-set | Token | Sites | Fidelity |
| --- | --- | ---: | --- |
| `max-w-[430px]` | `max-w-app` | 27 | **identical** |
| `min-h-[100dvh]` | none; or adopt `Screen`; or add a token | 34 | swapping to `min-h-screen` is a behaviour change on mobile (`100vh` vs `100dvh`) — **judgement call** |
| safe-area paddings | none; would need a `--spacing-safe-*` token or a utility | 18 | **no token** |
| everything else | none | 53 | **no token** — one-off layout numbers |

---

## 8. Z-index

**No z-index token exists in the CSS.** The `styles.css` comment lists `zIndex` as one of the
foundation groups that is "exactly four", but no `--z-*` is declared and no `z-*` utility is themed,
so every use resolves to Tailwind's default scale.

| Utility | Uses | Files | Vendor |
| --- | ---: | ---: | ---: |
| `z-30` | 21 | 20 | 1 |
| `z-40` | 4 | 4 | 2 |
| `z-50` | 2 | 2 | 18 |
| `z-10` | 1 | 1 | 9 |
| `z-20` | 1 | 1 | 4 |
| **Total** | **30** (5 distinct) | **26** | **35** |

Zero `z-[…]` arbitrary values in product (`z-[1]` × 1 in vendor). The layering in product is
coherent — `z-30` sticky headers, `z-40` bottom nav and FABs, `z-50` two overlays — it just has no
names. `lib/styles-prop.ts` sets `z-index` through an inline style once.

**Conversion table:** nothing to swap. 30 sites are on 5 unnamed layers; naming them is an
addition to the CSS, not an edit to the app (see Decisions).

---

## 9. Opacity

Four steps are declared in `:root` but **none of them is in `@theme`**, so no `opacity-1..4` utility
exists. `--opacity-1` is read exactly once, by `state-layer` in `styles.css`. `--opacity-2/3/4` are
read nowhere.

| Token | Value | Reachable as a utility? | `var()` uses |
| --- | --- | --- | ---: |
| `--opacity-1` | 0.08 | no | 1 (`state-layer` hover) |
| `--opacity-2` | 0.16 | no | 0 (the `ripple` keyframe hard-codes `0.16`) |
| `--opacity-3` | 0.32 | no | 0 |
| `--opacity-4` | 0.64 | no | 0 |

**Drift: 37 occurrences, 11 distinct values, 12 files** (vendor adds 50).

| Utility | Uses | Nearest step | Delta |
| --- | ---: | --- | ---: |
| `opacity-50` | 11 | `--opacity-3` 0.32 / `--opacity-4` 0.64 | −0.18 / +0.14 — **no step** |
| `opacity-60` | 6 | `--opacity-4` 0.64 | +0.04 — **no step** |
| `opacity-0` | 4 | — | fine, it is zero |
| `opacity-70` | 3 | `--opacity-4` | −0.06 — **no step** |
| `opacity-80` | 3 | — | **no step** |
| `opacity-40` | 3 | `--opacity-3` 0.32 | −0.08 — **no step** |
| `opacity-85` | 3 | — | **no step** |
| `opacity-75` | 2 | — | **no step** |
| `opacity-64` | 1 | `--opacity-4` 0.64 | **exact** — the only site on a step |
| `opacity-35` | 1 | `--opacity-3` 0.32 | −0.03 — **no step** |

Plus `state-layer`'s own `:active` rule hard-codes `opacity: 0.12` where `--opacity-1` (0.08) and
`--opacity-2` (0.16) exist, and `link-action`'s `:active` hard-codes `opacity: 0.6`.

**Conversion table**

| Hand-set | Token | Sites | Fidelity |
| --- | --- | ---: | --- |
| `opacity-64` | `--opacity-4` (needs a `@theme` entry first) | 1 | **identical** |
| every other `opacity-*` | — | 36 | **no step** — 10 distinct values, none within 0.03 of a step |
| `state-layer` `:active 0.12` | between `--opacity-1` and `--opacity-2` | 1 (CSS) | **no step** |
| `link-action` `:active 0.6` | `--opacity-4` 0.64 | 1 (CSS) | small shift |

---

## 10. Border and stroke width

**No border-width token exists.** The system's hairline is expressed two ways: the `hairline`
utility (`border: 1px solid var(--color-border)`) and the base-layer rule `* { border-color:
var(--color-border) }`, which already makes `border` alone produce a hairline in the token colour.

| Utility | Width | Uses | Files |
| --- | --- | ---: | ---: |
| `border` | 1px | 456 | 161 |
| `border-t` | 1px | 41 | 33 |
| `border-b` | 1px | 26 | 20 |
| `border-0` | 0 | 22 | 16 |
| `ring-1` | 1px | 17 | 13 |
| `ring-2` | 2px | 14 | 8 |
| `border-2` | 2px | 13 | 8 |
| `border-l` / `border-r` / `border-y` | 1px | 11 | 10 |
| `border-4` and the four `border-{t,r,b,l}-4` | 4px | 13 | 4 |
| `border-[1.5px]`, `ring-[3px]`, `ring-0` | — | 4 | vendor |
| **Total** | | **618** (19 distinct) | **179** |

`--focus-ring: 2px solid var(--primary)` with `--focus-ring-offset: 2px` is applied once, globally,
on `:focus-visible`. No utility backs it; nothing overrides it.

### 10a. Drift: `border border-border` written out instead of `hairline`

**413 class strings in 133 product files** contain both `border` and `border-border`. The `hairline`
utility that does exactly this is used **zero times**, anywhere.

Top files: `ml/booking.tsx` 21 · `ml/portfolio.tsx` 19 · `ml/profile.tsx` 15 · `ml/market.tsx` 13 ·
`ml/rental.tsx` 12 · `routes/discover.index.tsx` 9 · `routes/marketplace/rent/active.$id.tsx` 9 ·
`ml/person-profile.tsx` 9 · `ml/collab.tsx` 8 · `routes/feed.tsx` 8 · `routes/marketplace/sell.tsx` 8
· `routes/marketplace/rent/request.$id.tsx` 8.

Note that `border-border` is itself redundant in most of these: `@layer base` already sets
`border-color: var(--color-border)` on `*`, so `border` alone renders the same hairline. There are
three overlapping ways to say one thing.

**Conversion table**

| Hand-set | Token | Sites | Fidelity |
| --- | --- | ---: | --- |
| `border border-border` | `hairline` | 413 | **identical** — same declaration, one class instead of two |
| `border border-border` | just `border` | 413 | **identical** — base layer supplies the colour |
| `border-t border-border` and the other sides | no utility covers a single side | ~60 | **no token** — `hairline` sets all four sides |
| `border-2`, `border-4`, `ring-2` | no width token | 40 | **no token** |

---

## 11. Shadow and elevation

Four elevation tokens, aliased into `--shadow-*` so `shadow-elevation-N` exists.

| Token | Value | Utility | Uses |
| --- | --- | --- | ---: |
| `--elevation-1` → `--shadow-elevation-1` | `0 1px 3px rgba(13,11,38,0.08)` | `shadow-elevation-1` | 0 |
| `--elevation-2` → `--shadow-elevation-2` | `0 4px 12px rgba(13,11,38,0.05)` | `shadow-elevation-2` | 0 |
| `--elevation-3` → `--shadow-elevation-3` | `0 8px 24px rgba(13,11,38,0.08)` | `shadow-elevation-3` | **1** (vendor, `ui/sonner.tsx`) |
| `--elevation-4` → `--shadow-elevation-4` | `0 12px 32px rgba(13,11,38,0.12)` | `shadow-elevation-4` | 0 |

The elevation scale is effectively unused. `styles.css` says *"Cards lift with `--elevation-2`; the
1px border is the fallback for cards on white"* — in practice every card uses the border and none
uses the shadow.

**Drift: 52 shadow utilities in total, of which 9 are in the product bucket and 8 of those 9 are in
one file** (`lib/glass-variants.ts`).

| Utility | Product | Vendor |
| --- | ---: | ---: |
| `shadow-sm` | 2 | 10 |
| `shadow` (Tailwind default) | 0 | 11 |
| `shadow-lg` | 0 | 8 |
| `shadow-md` | 0 | 7 |
| `shadow-none` | 1 | 2 |
| `shadow-xs`, `shadow-xl` | 0 | 2 |
| `shadow-[…]` arbitrary | 6 (all `glass-variants.ts`) | 2 (`ui/sidebar.tsx`) |
| `shadow-elevation-3` | 0 | 1 |

The six arbitrary shadows in `glass-variants.ts` include two six-part inset stacks of ~300 characters
each.

**Conversion table**

| Hand-set | Token | Sites | Delta | Fidelity |
| --- | --- | ---: | --- | --- |
| `shadow-sm` (`0 1px 3px rgb(0 0 0/.1), 0 1px 2px -1px rgb(0 0 0/.1)`) | `shadow-elevation-1` (`0 1px 3px rgba(13,11,38,.08)`) | 12 | loses the second layer, alpha .1 → .08, black → ink-blue | small shift |
| `shadow-md` | `shadow-elevation-2` or `-3` | 7 | different blur and spread | judgement call — vendor only |
| `shadow-lg` | `shadow-elevation-3` or `-4` | 8 | different blur and spread | judgement call — vendor only |
| `shadow-[…]` glass stacks | none | 6 | — | **no token** — decide whether glass stays |

---

## 12. Icon size

**No icon-size token exists.** Icons come from `lucide-react` (AGENTS.md: *"Icons from `lucide-react`
… Never emoji"*), and every one is sized by a number literal.

**476 `size={N}` props across `src`** (447 of them in the product bucket, 123 files in all, 116 of
them product), of which 323 are on a lucide element directly; the rest are on components that take a
`size` number (`Stars`, `VerifiedBadge`, `IconBtn` icons, brand icons).

| `size={N}` | On lucide | All sites |
| ---: | ---: | ---: |
| 16 | 87 | 145 |
| 18 | 69 | 79 |
| 14 | 42 | 65 |
| 15 | 25 | 37 |
| 13 | 30 | 36 |
| 17 | 9 | 34 |
| 12 | 22 | 23 |
| 22 | 6 | 9 |
| 20 | 6 | 7 |
| 40 | 5 | 7 |
| 11 | 3 | 6 |
| 26 | 5 | 5 |
| 24 | 4 | 4 |
| 28 | 4 | 4 |
| 44 | 4 | 4 |
| 19 | 0 | 4 |
| 32 | 0 | 2 |
| 10 | 2 | 2 |
| 30 | 0 | 1 |
| 96 | 0 | 1 |
| 300 | 0 | 1 |

**16 distinct sizes on lucide icons, 21 across all `size=` props** — the last two (96, 300) are a QR
/ canvas dimension rather than an icon.

A second, smaller spelling exists: **33 lucide icons are sized by className instead** — `h-4 w-4` 22
(12 files), `size-4` 6 (1 file), and one each of `h-2 w-2`, `h-3 w-3`, `h-3.5 w-3.5`, `h-2.5 w-2.5`,
`h-5 w-5`.

`strokeWidth` is set 21 times across 7 distinct values: `1.75` ×7, `1.8` ×5, `3` ×4, `2` ×2, and one
each of `0`, `1.4`, `2.2`. Lucide's default is 2.

**Conversion table:** nothing to swap — there is no target. Adding an icon scale is an addition to
the CSS plus a sweep (see Decisions). If four steps were chosen as 12 / 16 / 20 / 24, the 476 sites
would land:

| Bucket | Sites | Move |
| --- | ---: | --- |
| already exactly 12, 16, 20 or 24 | 179 | 0 |
| 13, 14, 15 → 16 | 138 | +1 to +3px |
| 17, 18, 19 → 16 or 20 | 117 | −3 to +2px — **judgement call**, 18 is the second-commonest size in the app |
| 22 → 20 or 24 | 9 | ±2px |
| 10, 11 → 12 | 8 | +1 to +2px |
| 26, 28, 30, 32, 40, 44, 96, 300 | 25 | above the scale |

---

## 13. Avatar and control size

**No avatar-size token exists.** Avatars, dots and round controls are sized with the numeric scale.
Counting round product elements (`rounded-full` with an explicit width and height): **135 sites**,
of which **101 are square across 17 distinct sizes** and 34 are not square (switches, meters, bars).

| Size | px | Sites | Typically |
| --- | ---: | ---: | --- |
| `h-10 w-10` | 40 | 16 | list-row avatar, round icon button |
| `h-5 w-5` | 20 | 14 | inline avatar, small dot |
| `h-1.5 w-1.5` | 6 | 11 | status dot (matches `StatusPill`'s own dot) |
| `h-11 w-11` | 44 | 11 | round action button |
| `h-12 w-12` | 48 | 9 | header avatar |
| `h-16 w-16` | 64 | 8 | profile avatar |
| `h-14 w-14` | 56 | 6 | |
| `h-8 w-8` | 32 | 4 | chip media (`Chip` in `ml/ui.tsx`) |
| `h-6 w-6` 24px | | 4 | |
| `h-4 w-4` 16, `h-7 w-7` 28, `h-2 w-2` 8, `h-9 w-9` 36, `h-2.5 w-2.5` 10, `h-1 w-1` 4, `h-20 w-20` 80, `h-24 w-24` 96 | | 19 | |
| non-square (`h-7 w-12` toggle, `h-6 w-11` switch, progress bars) | | 34 | not avatars |

Seventeen square sizes are in use: 4, 6, 8, 10, 16, 20, 24, 28, 32, 36, 40, 44, 48, 56, 64, 80, 96 px.

**Conversion table:** nothing to swap; there is no token. The values are all on the Tailwind numeric
scale already, so an avatar token would be a naming exercise, not a pixel change.

---

## 14. Duration, easing and animation

| Token | Value | Utility | Uses | Files |
| --- | --- | --- | ---: | ---: |
| `--duration-1` | 125ms | **none** (not in `@theme`) | — | — |
| `--duration-2` | 200ms | none | — | — |
| `--duration-3` | 320ms | none | — | — |
| `--duration-4` | 500ms | none | — | — |
| `--easing-standard` → `--ease-standard` | `cubic-bezier(0.4, 0, 0.2, 1)` | `ease-standard` | 33 | 14 |
| `--easing-enter` → `--ease-enter` | `cubic-bezier(0.05, 0.7, 0.1, 1)` | `ease-enter` | 1 | 1 |
| `--easing-spring` → `--ease-spring` | `cubic-bezier(0, 0.35, 0, 1.25)` | `ease-spring` | **0** | 0 |

`--duration-*` is declared in `:root` only and read 3 times inside `styles.css`
(`state-layer` transition, `link-action` transition × 2). It is **not aliased into `@theme`**, so
there is no `duration-1` utility — the app writes the number.

| Utility | Uses | Files | Equals |
| --- | ---: | ---: | --- |
| `duration-125` | 20 | 11 | `--duration-1` exactly |
| `duration-200` | 16 | 9 | `--duration-2` exactly |
| `duration-300` | 1 | 1 | 20ms short of `--duration-3` |
| `duration-500` / `duration-1000` | 0 / 0 (2 in vendor) | | `--duration-4` / no step |

**36 of 37 product duration uses are already on a token value under a non-token name.**

Animations (all tokens, all in `@theme`):

| Token | Value | Utility uses |
| --- | --- | ---: |
| `--animate-message` | `message 200ms var(--easing-enter) both` | 10 |
| `--animate-pop` | `pop 320ms var(--easing-spring) both` | 6 |
| `--animate-rise` | `rise 480ms var(--easing-enter) both` | 5 |
| `--animate-shake` | `shake 320ms var(--easing-standard)` | 3 |
| `--animate-sound-check` | `sound-check 500ms var(--easing-enter) both` | 1 |
| `--animate-meter` | `meter 360ms var(--easing-standard)` | 1 |
| `--animate-caret` | `caret 1s steps(1) infinite` | 1 |
| `--animate-select` | `select 320ms var(--easing-spring)` | 1 |
| `--animate-marquee` | `marquee 60s linear infinite` | 1 |
| `--animate-fill` | `fill 500ms var(--easing-enter) both` | 1 |

`--animate-rise` is 480ms, which is not a duration step (`--duration-3` 320, `--duration-4` 500).
`--animate-meter` is 360ms, likewise. The view-transition rules in `styles.css` hard-code
`200ms`/`320ms`/`500ms`/`60ms` rather than reading `--duration-*`.

Non-token animation utilities: `animate-pulse` 27, `animate-in` 22, `animate-out` 18 (these three
come from `tw-animate-css`), `animate-spin` 7, plus 5 vendor one-offs.

`transition-*`: 92 occurrences, 15 distinct. `transition-colors` 31, `transition-transform` 23,
`transition-all` 14, and 10 bracketed property lists including
`transition-[transform,background-color,border-color,color,opacity]`.

**Conversion table**

| Hand-set | Token | Sites | Delta | Fidelity |
| --- | --- | ---: | ---: | --- |
| `duration-125` | `duration-1` (needs a `@theme` entry) | 20 | 0 | **identical** |
| `duration-200` | `duration-2` (needs a `@theme` entry) | 16 | 0 | **identical** |
| `duration-300` | `duration-3` (320ms) | 1 | +20ms | small shift |
| `ease-linear`, `ease-in-out` | `ease-standard` | 5 (vendor) | curve changes | judgement call |
| `--animate-rise` 480ms, `--animate-meter` 360ms | `--duration-4` / `--duration-3` | 2 (CSS) | +20ms / −40ms | small shift |
| view-transition hard-coded ms | `--duration-*` | 8 (CSS) | 0 | **identical** |

---

## 15. Breakpoints and container

The app is a single-width mobile shell. There is **no breakpoint token** and almost no responsive
code.

| Variant | Uses | Product uses |
| --- | ---: | ---: |
| `sm:` | 20 | **0** |
| `md:` | 19 | 2 files |
| `lg:`, `xl:`, `2xl:`, `max-*`, container queries | 0 | 0 |

All 20 `sm:` uses are in vendor dialogs and sheets. The only width contract is:

| Token | Value | Utility | Uses |
| --- | --- | --- | ---: |
| `--container-app` | 430px | `max-w-app` | 27 (19 files) |

with 27 more sites still writing `max-w-[430px]`, and the fluid type ramps interpolating between
360px and 1440px. The stated design target is 412px (AGENTS.md checks: *"412px screenshots"*).

**Conversion table:** see §7a — `max-w-[430px]` → `max-w-app`, 27 sites, identical.

---

## 16. Utilities

Custom utilities declared in `src/styles.css` with `@utility`, and the view-transition classes.
Counts are class-token occurrences; `files` lists every bucket.

| Utility | What it does | Product | Vendor | Files |
| --- | --- | ---: | ---: | ---: |
| `rail` | horizontal rail: `scroll-snap-type: x mandatory`, `scroll-padding-left: 20px`, `overscroll-behavior-x: contain`, children `scroll-snap-align: start` | 65 | 0 | 45 |
| `no-scrollbar` | hides the scrollbar (`scrollbar-width: none` + `::-webkit-scrollbar { display:none }`) | 65 | 0 | 45 |
| `pill` | status/tag label: inline-flex, gap 0.25rem, radius 9999px, padding 2px/8px, **11px / 500 / 0.08em / uppercase / nowrap**, line-height 1rem. Tone comes from the caller | 20 | 0 | 11 |
| `state-layer` | Material 3 state layer: a `currentColor` veil under the content — 8% on hover (pointer devices), **12% hard-coded** on `:active`, fading over `--duration-1` | 20 | 0 | 13 |
| `hit-area` | 48dp touch floor via an `::after` of `max(100%, 48px)` square, centred, without growing the control | 16 | 0 | 10 |
| `pill-glass` | pill over a photo: `color-mix(white 72%)`, `backdrop-filter: blur(16px) saturate(180%)`, 0.5px inset rim, 320ms `pill-in` fade-and-settle | 5 | 0 | 5 |
| `link-action` | text link / text button: underline appears on hover, **`opacity: 0.6`** while pressed, colour unchanged | 3 | 0 | 2 |
| `ripple` | the touch ripple itself: `scale(0)` → `ripple 550ms var(--easing-standard) forwards` | 2 | 0 | 1 |
| `ripple-clip` | the ripple's clipping box (`overflow: hidden; border-radius: inherit`) so the button stays unclipped for `hit-area` | 1 | 0 | 1 |
| `ink-gradient` | `background-image: var(--gradient-ink)` (`linear-gradient(150deg, #0d0d0d, #2b2b2b)`) | 1 | 0 | 1 |
| `hairline` | `border: 1px solid var(--color-border)` | **0** | 0 | 0 |
| `brand-gradient` | `background-image: var(--gradient-brand)` (`linear-gradient(135deg, brand, purple)`) | **0** | 0 | 0 |
| `vt-brand` | `view-transition-name: brand` — the splash logo gliding to Welcome's corner | 2 | 0 | 2 |
| `vt-flow-header` / `vt-flow-footer` / `vt-flow-progress` | view-transition names that hold the registration chrome still while the question slides | 3 | 0 | 1 |

`rail` and `no-scrollbar` are always paired: 65 uses each, the same 45 files.
`ripple` is applied through JS in `ml/ui.tsx`, which is why it is 2 and 1.

Also in `styles.css` but not `@utility`: nine `@keyframes` (`rise`, `select`, `meter`, `shake`,
`pop`, `message`, `fill`, `caret`, `sound-check`, `marquee`, `pill-in`, `ripple`), the Material 3
shared-axis view-transition rules, and a `prefers-reduced-motion` block that flattens both animations
and view transitions.

### 16a. A second, unreachable token system

`src/styles/liquefy-ui.css` is **3,575 lines** with **153 `--lq-*` declarations over 55 distinct
property names** and 520 mentions in total — a complete parallel design system (glass fills, rims,
tints, control lines, easings, radii, spacing), themed per component and per state.
**No file imports it.** The components that read those properties (`components/ui/glasscn/`,
`components/kibo-ui/`, `components/waves-cn/`) are imported only by `src/lab/*.stories.tsx`.
`lib/glass-variants.ts` (in the product bucket, 30 `rgba()` literals and 6 arbitrary shadows) is the
bridge between the two systems.

---

## 17. Token counts at a glance

| | Count |
| --- | ---: |
| Declarations in `@theme inline` | 77 (71 tokens + 6 `--text-*--line-height` pairs) |
| Declarations in `:root` | 64 |
| Colour tokens exposed as utilities | 37 |
| Colour tokens with zero product uses | 9 |
| Radius steps | 7 (+ `rounded-full`) |
| Type steps | 6 |
| Elevation steps | 4 (utility uses: 1, in vendor) |
| Duration steps | 4 (no utility) |
| Opacity steps | 4 (no utility) |
| Easing curves | 3 (`ease-spring` unused) |
| Named animations | 10 |
| Spacing tokens | 1 (`--spacing-nav-clear`, 1 use) |
| Container tokens | 1 (`--container-app`, 27 uses) |
| Dead declarations | `--radius`, `--opacity-2/3/4`, `--purple-container`, `--danger` (reached only via `--danger-container`) |
| Foundation groups with **no** token at all | z-index, icon size, avatar size, border width, font weight, letter spacing, line height, breakpoint |

---

## 18. Drift at a glance

Product bucket. "No step" counts the occurrences whose value falls between existing tokens.

| Group | Off-token occurrences | Distinct values | Files | Maps exactly | No step | Worst file |
| --- | ---: | ---: | ---: | ---: | ---: | --- |
| Type — arbitrary px | 998 | 17 | 156 | 327 | 282 | `routes/verification.$type.tsx` (57) |
| Type — Tailwind steps | 222 | 6 | 72 | 178 | 20 | `ml/booking.tsx` (24) |
| Hairline written out | 413 | 1 | 133 | 413 | 0 | `ml/booking.tsx` (21) |
| Hand-built eyebrows | 87 | 15 recipes | 46 | 0 | 87 | `routes/marketplace/rent/list.tsx` (8) |
| Radius — arbitrary | 53 | 6 | 30 | 42 | 11 | `routes/opportunities.new.tsx` (7) |
| Spacing/size — arbitrary | 132 | 73 | 68 | 27 | 105 | 32 files share `min-h-[100dvh]` |
| Opacity | 37 | 11 | 12 | 1 | 36 | `ml/profile.tsx` (6) |
| Duration | 37 | 3 | 15 | 36 | 1 | `ml/onboarding.tsx` (8) |
| Icon size | 447 (476 incl. vendor) | 21 | 116 | n/a — no token | all | `ml/booking.tsx` |
| Colour — named palette | 16 | ~8 | 1 | 0 | 16 | `lib/glass-variants.ts` (16) |
| Colour — alpha modifiers | 88 | 26 | ~50 | 0 | 88 | `ml/ui.tsx` |
| Shadow | 9 | 8 | 2 | 2 | 7 | `lib/glass-variants.ts` (8) |
| Z-index | 30 | 5 | 26 | n/a — no token | 30 | `kibo-ui/reel` (vendor) |
| Hex / rgb / hsl in a class | **0** | 0 | 0 | — | — | — |
| **Total off-token occurrences** | **2,569** | | **196** | | | |

Where the drift is worst, in one line: **type** (1,220 of 2,569 off-token occurrences, 47.5%) and
**the hairline** (413, 16.1%) — together 64% of everything recyclable, and both are almost entirely
mechanical. Icon size is the third largest (447) but has no token to move to.

---

## 19. Sites where the swap is not mechanical

| Kind | Sites | Where | Why |
| --- | ---: | --- | --- |
| Size inside a template literal / conditional class | 7 | `text-[12px]` ×3, `text-[11px]` ×3, `text-[14px]` ×1 | the class is built with `${…}`; the string must be edited inside the expression, and formatting may re-wrap the whole template |
| Size passed as a component prop | 44 | `Price amountClassName="…"` and `unitClassName` in `ml/kit.tsx` callers | the class never appears on the element it styles; `Price` also forces `font-normal leading-tight` on the unit line, so a token's own line height is overridden there but not on the amount |
| Size on an element that already pins `leading-*` | 77 of 998 | mostly 19px (15/15), 24px (5/5), 13px (18/230) | font-size moves, line height does not — these are the *safest* sites, but they must be recognised so the `leading-*` is not "cleaned up" at the same time |
| Size on an element with **no** `leading-*` | 921 of 998 | everywhere else | the element currently inherits `line-height: 1.5` from Tailwind Preflight (`html { line-height: 1.5 }`); every token carries its own (1.04–1.5), so **every one of these 921 swaps changes line height as well as size** |
| `text-xs` → `text-1` | 175 | 64 files | line height 1.333 → 1.45: rows get ~1.4px taller each. Dense lists will reflow |
| `min-h-[100dvh]` → `min-h-screen` | 34 | 32 files | `100dvh` and `100vh` differ on mobile with a collapsing URL bar; this is a behaviour change, not a token swap |
| Icon `size={N}` | 476 (447 product) | 116 product files | numbers in JSX expressions, not class strings — a different edit shape, and `VerifiedBadge` computes `Math.round(size * 0.65)` from its prop |
| `strokeWidth` | 21 | 7 values | same |
| `lib/styles-prop.ts` | 1 | sets `z-index` through an inline style | not a class at all |
| `lib/error-page.ts` | 6 hex | standalone HTML string served outside React | Tailwind never runs on it; tokens are not available |
| `ml/auth.tsx` brand SVGs | 10 hex | Google `G`, Indian flag | third-party marks — must not be tokenised |
| `data/identity.ts` | 8 hex | QR / identity-card palettes | product data, not UI chrome |
| `lib/glass-variants.ts` | 30 `rgba()` + 6 shadows + 16 named colours | the bridge to the unreachable `--lq-*` system | no target token exists; the file's fate is a decision, not a swap |

---

## 20. Sweep order, biggest win first

Per-file totals across the five mechanical groups (arbitrary type, Tailwind type, arbitrary radius,
hairline pairs, eyebrows) plus opacity/duration and named colours. **196 product files carry drift;
the top 20 files carry 626 of 1,863 hits (34%), the top 30 carry 812 (44%).**

| # | File | Total | txt-arb | txt-TW | radius | hairline | eyebrow | op/dur | named |
| ---: | --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| 1 | `src/routes/verification.$type.tsx` | 69 | 57 | 0 | 6 | 6 | 0 | 0 | 0 |
| 2 | `src/components/ml/booking.tsx` | 50 | 0 | 24 | 0 | 21 | 3 | 2 | 0 |
| 3 | `src/routes/marketplace/rent/list.tsx` | 44 | 22 | 7 | 0 | 7 | 8 | 0 | 0 |
| 4 | `src/routes/discover.index.tsx` | 38 | 22 | 4 | 0 | 9 | 1 | 2 | 0 |
| 5 | `src/routes/marketplace/rent/active.$id.tsx` | 37 | 16 | 9 | 0 | 9 | 3 | 0 | 0 |
| 6 | `src/routes/opportunities.new.tsx` | 36 | 22 | 0 | 7 | 7 | 0 | 0 | 0 |
| 7 | `src/routes/marketplace/rent/e.$id.tsx` | 33 | 22 | 4 | 0 | 7 | 0 | 0 | 0 |
| 8 | `src/routes/services.new.tsx` | 29 | 20 | 0 | 0 | 3 | 6 | 0 | 0 |
| 9 | `src/routes/opportunities.$id.apply.tsx` | 28 | 21 | 1 | 3 | 3 | 0 | 0 | 0 |
| 10 | `src/components/ml/portfolio.tsx` | 28 | 0 | 0 | 0 | 19 | 2 | 7 | 0 |
| 11 | `src/components/ml/business-profile.tsx` | 27 | 23 | 3 | 0 | 1 | 0 | 0 | 0 |
| 12 | `src/routes/marketplace/rent/request.$id.tsx` | 27 | 12 | 6 | 0 | 8 | 1 | 0 | 0 |
| 13 | `src/components/ml/profile.tsx` | 27 | 0 | 0 | 0 | 15 | 4 | 8 | 0 |
| 14 | `src/components/ml/market.tsx` | 26 | 0 | 10 | 0 | 13 | 3 | 0 | 0 |
| 15 | `src/routes/studio.tsx` | 23 | 14 | 4 | 0 | 5 | 0 | 0 | 0 |
| 16 | `src/routes/business.new.tsx` | 22 | 18 | 0 | 0 | 2 | 2 | 0 | 0 |
| 17 | `src/routes/bands.$id.index.tsx` | 21 | 13 | 6 | 1 | 1 | 0 | 0 | 0 |
| 18 | `src/components/ml/ui.tsx` | 21 | 0 | 2 | 0 | 6 | 0 | 13 | 0 |
| 19 | `src/routes/profile.edit.tsx` | 20 | 10 | 3 | 0 | 5 | 2 | 0 | 0 |
| 20 | `src/routes/search.index.tsx` | 20 | 12 | 3 | 0 | 4 | 0 | 1 | 0 |
| 21 | `src/routes/marketplace/l.$id.tsx` | 20 | 12 | 3 | 0 | 5 | 0 | 0 | 0 |
| 22 | `src/components/ml/rental.tsx` | 20 | 0 | 5 | 0 | 12 | 2 | 1 | 0 |
| 23 | `src/routes/provider.bookings.calendar.tsx` | 19 | 13 | 1 | 0 | 3 | 2 | 0 | 0 |
| 24 | `src/routes/saved.tsx` | 19 | 10 | 4 | 0 | 4 | 1 | 0 | 0 |
| 25 | `src/routes/services.$id.index.tsx` | 19 | 19 | 0 | 0 | 0 | 0 | 0 | 0 |
| 26 | `src/routes/marketplace/sell.tsx` | 19 | 6 | 4 | 0 | 8 | 1 | 0 | 0 |
| 27 | `src/routes/marketplace/rent/provider.$id.tsx` | 18 | 13 | 2 | 0 | 3 | 0 | 0 | 0 |
| 28 | `src/components/ml/onboarding.tsx` | 18 | 0 | 0 | 0 | 7 | 0 | 11 | 0 |
| 29 | `src/routes/opportunities.$id.index.tsx` | 17 | 13 | 0 | 1 | 2 | 1 | 0 | 0 |
| 30 | `src/routes/recommendations.swipe.tsx` | 17 | 5 | 4 | 1 | 3 | 0 | 4 | 0 |

The remaining 166 files hold 1,051 hits, a long tail of 1–17 each. Full per-file data is in
`token_map.json` under `per_file_drift`.

Two orderings are available and they differ:

- **By file** (the table above): finishes screens one at a time, each file touched once.
- **By group** (`hairline` 413 across 133 files, then `text-[12px]` 241 across 98, then
  `text-[13px]` 230 across 98…): one decision applied everywhere, but most files get touched four or
  five times.

`src/components/ml/*` carries **307 hits across 23 files** and is where a single edit changes many
screens; `src/routes/*` carries **1,540 across 172 files**, one screen per edit; `src/lib/*` carries
16 (all in `glass-variants.ts`).

---

## Decisions for the owner

Facts and options only.

**Type scale**

1. **`text-[11px]` — 214 sites.** It is the `pill` font size but not a scale step. Options: add an
   eyebrow/step-0 token at 11px (0 delta, scale becomes seven steps); round all 214 up to `text-1`
   (+1.05px at 412, +2px at 1440); split them — the ~52 eyebrow labels onto a label utility, the
   ~162 body uses rounded up; leave them.
2. **`text-[10px]` — 43 sites.** Round up to `text-1` (+2.05px, a 20% jump) or add a step at 10px or
   leave them.
3. **`text-[19px]` — 15 sites, and `text-[20px]` + `text-xl` — 29 sites.** They sit in the gap
   between `--type-2` (16–18) and `--type-3` (22–28). Down to `text-2` (−2.9px / −3.9px at 412), up
   to `text-3` (+3.3px / +2.3px at 412, +9px / +8px at 1440), or add a step between 18 and 22.
4. **Everything ≥ 24px that is not 34px — 10 sites.** 24, 26, 28, 30 all land between `--type-3`
   (max 28) and `--type-4` (min 32). Round down to `text-3`, up to `text-4`, or leave.
5. **`text-7xl` (72px), one site**, the 404 numeral in `__root.tsx`. Above `text-5` (max 56). Leave
   it, or make the 404 use `text-5`.
6. **Line height.** 921 of the 998 arbitrary-size sites currently inherit 1.5 from Tailwind
   Preflight. Every token carries its own (1.04–1.5). Accept the reflow, or pin `leading-*` at each
   site during the sweep to freeze current line heights.
7. **`text-4` is used once and `text-5` once.** The CSS says screen titles belong on `text-4`. Either
   the titles (currently 15–19px) move up to `text-4` (32.6px at 412 — a large change), or the
   comment is wrong about what a screen title is.
8. **Does `text-sm` keep Tailwind's name?** It is a token now (`--type-sm`) but reads like a default,
   next to `text-1`/`text-2`. Rename to `text-1.5`/`text-2`-renumbering, or keep the name and accept
   the ambiguity.

**Labels**

9. **The eyebrow.** 87 hand-built `uppercase` + `tracking-*` labels against 20 `pill` uses. They are
   a different object from `pill` (no background, no padding, 700 weight, 0.025em). Options: add an
   `eyebrow` utility and sweep 87 sites; widen `pill` with a "bare" variant; declare the eyebrow a
   normal caption and drop `uppercase`/`tracking` from all 87.
10. **Letter spacing.** Four values in use (0.025, 0.08 inside `pill`, 0.14, 0.18) and none is a
    token. Pick the set, or leave tracking untokenised.

**Hairline and borders**

11. **413 sites say `border border-border`; `hairline` has zero uses; and `@layer base` already
    colours every border.** Three spellings for one thing. Options: sweep all 413 to `hairline`;
    sweep to bare `border` and delete `hairline`; leave and delete `hairline` as unused.
12. **Single-side hairlines** (`border-t border-border` and friends, ~60 sites) have no utility.
    Add `hairline-t/b/l/r`, or leave them as classes.
13. **Border width has no token** (1px, 2px, 4px, `ring-1/2` in use, 40 sites off the hairline).
    Add a width scale, or accept that 1px is the only width the system names.

**Radius**

14. **11 arbitrary radii fall between steps**: `rounded-[24px]` ×6 (20 or 28, ±4px) and
    `rounded-[14px]` ×5 (12 or 16, ±2px). Round, or add steps.
15. **`--radius: 16px` in `:root` is dead** (nothing reads it). Delete, or keep for shadcn parity.
16. **`rounded-xl` (20px) and `rounded-3xl` (32px) have zero uses**, yet 15 sites write those exact
    pixel values by hand. Sweeping fixes it; the alternative is deleting two unused steps.

**Elevation and shadow**

17. **The elevation scale is unused** (1 utility use, in vendor), while the CSS says cards lift with
    `--elevation-2`. Either cards start using it, or the comment is corrected and the border remains
    the only card edge, or the four tokens are deleted.
18. **`shadow-sm` → `shadow-elevation-1` is not identical** (Tailwind's is two layers of black at
    10%; the token is one layer of ink-blue at 8%). Accept the shift on 12 sites, or leave them.

**Opacity**

19. **`--opacity-1..4` are not in `@theme`**, so no `opacity-*` utility can reach them, and 36 of 37
    uses are off-step anyway. Options: alias them into `@theme` and sweep (10 distinct values would
    have to move); delete `--opacity-2/3/4` as unused; leave both sides as they are.
20. **`state-layer` hard-codes `:active { opacity: 0.12 }`** between steps 1 (.08) and 2 (.16), and
    `link-action` hard-codes `0.6` next to step 4 (.64). Move them onto steps, or record 0.12 and 0.6
    as intended.
21. **26 distinct alpha modifier values, none on a step** (`/95` ×23, `/90` ×16, `/80` ×16 …).
    Standardise the sticky-header alpha, or leave modifiers outside the token system.

**Motion**

22. **`--duration-1..4` are not in `@theme`**, so the app writes `duration-125` / `duration-200`
    (36 of 37 uses are already exactly on a step). Aliasing them makes 36 sites a zero-delta rename.
23. **`--animate-rise` is 480ms and `--animate-meter` 360ms**, neither a duration step. Snap to 500
    and 320, or record them as intentional.
24. **The view-transition CSS hard-codes 200/320/500/60ms** instead of `var(--duration-*)`. Swap
    (zero delta) or leave.
25. **`ease-spring` has zero uses** though two animations use `--easing-spring` directly. Keep the
    alias or drop it.

**Colour**

26. **Four tokens are the same black** (`--primary`, `--ink`, `--ring`, `--selected-bg` = `#0D0D0D`)
    and **four are the same off-white** (`--secondary`, `--muted`, `--accent`, `--surface-subtle` =
    `#F8F8FB`). Collapse, or keep the semantic names.
27. **Nine colour tokens have zero product uses** (`purple`, `card-foreground`, `popover`,
    `popover-foreground`, `secondary`, `secondary-foreground`, `muted`, `accent-foreground`,
    `destructive-foreground`) and exist only for shadcn components the app does not import.
    Keep as shadcn contract, or prune.
28. **`--color-price` is an alias of `--pink-strong`** and both are used (8 and 1). Keep two names
    or one.
29. **`--purple-container` and `--danger` are declared with no `--color-*` alias**, so no utility
    exists for them. Alias, or delete.

**Groups with no tokens at all**

30. **Icon size** — 476 hand-set numbers (447 in product), 21 distinct (10–300). The CSS comment
    claims `icon` is a four-step foundation; no such token exists here. Add a scale (12/16/20/24
    would leave 272 sites to move by 1–3px, 117 of them the 17/18/19 judgement call), or accept
    per-site numbers.
31. **Avatar size** — 135 round elements, 101 of them square across 17 sizes (4–96px), all already
    on the Tailwind numeric scale. Name a subset as avatar steps, or leave them.
32. **Z-index** — 30 product uses on 5 unnamed Tailwind layers, consistently applied. Name the layers
    (sticky header / bottom nav / overlay), or leave.
33. **Font weight** — 758 uses, 4 values, no token. 36 heading weights restate what `@layer base`
    already sets; 17 override 700 down to 600. Decide whether headings may carry their own weight.

**Scope of the cleanup**

34. **`src/styles/liquefy-ui.css` is 3,575 lines and 55 `--lq-*` properties (153 declarations) that nothing imports**,
    and `components/ui/glasscn`, `components/kibo-ui`, `components/waves-cn` are reachable only from
    `src/lab` stories. Delete the second system, keep it as a lab, or adopt it. Most of the
    vendor-bucket drift in this document disappears with it.
35. **`src/components/ui` is 52 shadcn files of which 3 are reached** (`avatar`, `switch`, `sonner`).
    Prune to the three, or keep the set for future use. This decides whether the ~300 vendor-bucket
    off-token occurrences counted here are worth sweeping at all.
36. **`lib/glass-variants.ts`** is in the product bucket and holds 30 `rgba()` literals, 6 arbitrary
    shadows and 16 named colours — the only product file with hard-coded colour. Its fate follows
    decision 34.
37. **The sweep order**: by file (28% of the work is in 20 files, each touched once) or by group
    (one decision applied everywhere, most files touched four or five times).
38. **`tokens.json` and `ml-tokens.css` are not in this repo.** If the cleanup adds or moves a step,
    decide whether the generator in the guide repo is the source of truth or whether `styles.css`
    now is. The CSS already records three places where the two disagree (`--color-paper` value,
    the `--color-line` claim, and `--type-sm` missing from the generated file).
