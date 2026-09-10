# MusicLinkd — Phase specs 2b → 7

Companion to `PHASE-2-TOKEN-SPEC.md` (Phase 2) and `CLAUDE-CODE-WORK-ORDER.md`.
Each phase is one branch, one PR, exit gate on each. Do not batch.

---

# PHASE 2b — Motion and scroll

**Branch:** `feat/motion-choreography` · **Risk:** medium (visual, everywhere) · **Est:** 1 day
**Depends on:** Phase 2 (needs `--duration-*` and `--ease-*`)

## The problem, stated

The app has **feedback** motion and no **choreography**. The only movement in the entire codebase
is `active:scale-[0.98]` on `Btn`. Everything else is a hard cut: routes swap instantly, sheets
appear fully-formed, lists paint in one frame, sticky headers snap.

The design system names this as its own known gap — *"0 entry animations, 0 view transitions, 0
scroll-driven motion, hard-cut state switching across 518 states."* So this is not a new opinion;
it is a documented debt.

**Ground rule:** motion here is orientation, not decoration. Every animation below answers "where
did this come from / where did it go". Nothing pulses, bounces or draws attention to itself.

## 2b.1 Sheets and dialogs — the worst offender

`Sheet` and `Dialog` both do `if (!open) return null`. There is no enter, no exit, no backdrop
fade. A bottom sheet that teleports reads as a bug.

**Note the ordering conflict:** Phase 6 replaces both with Radix primitives, which bring their own
`data-[state=open]` animation hooks. **Do the Radix swap first if you can** — otherwise this work
gets written twice. If Phase 6 slips, implement here and port the values across.

Target, once on Radix:

```css
/* backdrop */
[data-state="open"]   → opacity 0 → 1,  --duration-2, --ease-enter
[data-state="closed"] → opacity 1 → 0,  --duration-1   /* ~60% of enter */

/* sheet panel — slide from bottom */
[data-state="open"]   → translateY(100%) → 0, --duration-3, --ease-enter
[data-state="closed"] → translateY(0) → 100%, --duration-2, --ease-standard

/* dialog — fade + scale, no slide */
[data-state="open"]   → opacity 0→1, scale .96→1, --duration-3, --ease-enter
[data-state="closed"] → opacity 1→0, scale 1→.98, --duration-2
```

Exit at **60–70% of enter** — that ratio is in `motion.md` and it is what makes dismissal feel
responsive rather than sluggish.

## 2b.2 Route transitions

TanStack Router has no transition today; screens hard-cut. Add a shared shell transition in
`__root.tsx` — a short cross-fade with a few pixels of vertical travel. Keep it under
`--duration-2`; anything longer and navigation feels laggy on a phone.

Do **not** animate the bottom nav between routes. It is a fixed frame; moving it breaks the
illusion that it is the app chrome.

## 2b.3 List and card entry

Discover rails, search results, collab lists, marketplace grids all paint in one frame.

Stagger the first screenful only:

```
per-item delay: 20–30ms, capped at ~8 items
transform:      translateY(8px) → 0
opacity:        0 → 1
duration:       --duration-2
easing:         --ease-enter
```

**Only on first mount.** Not on filter changes, not on re-render, never on scroll-back. A list
that re-animates every time you return to it is the single most irritating pattern in mobile UI.

## 2b.4 Scroll behaviour

Currently: no scroll-linked motion at all, and horizontal rails are free-scrolling.

**Sticky header.** `TopBar` is `sticky top-0` with a permanent border. Bind the border and a
subtle `--elevation-1` to scroll position — flat at rest, hairline plus shadow once content moves
under it. That is the only scroll-driven effect worth having; it tells you the page has moved.

**Horizontal rails.** The carousels (`Featured talent`, `Trending`) scroll freely and stop
anywhere, which is why cards get clipped mid-item.

```css
scroll-snap-type: x mandatory;   /* on the rail */
scroll-snap-align: start;        /* on each card */
scroll-padding-left: var(--space-3);
```

This also fixes the clipped-card-at-the-edge problem visible in the live app.

**Smooth scrolling** for in-page anchors only — `scroll-behavior: smooth` on the container, not
globally, and gated behind `prefers-reduced-motion`.

**Do not** add parallax, scroll-triggered reveals below the fold, or scroll-jacking. On a 430px
shell they cost more than they give.

## 2b.5 State transitions

`AvailabilityBadge` / `StatusPill` colour changes, `Meter` fill, `Chip` and `Segmented` active
state, skeleton → content — all hard cuts today.

- Colour and background: `transition: colors --duration-2 --ease-standard`
- `Meter` fill width: `--duration-3 --ease-standard`
- Skeleton → content: cross-fade at `--duration-2`, never a pop
- Skeleton shimmer: **1.4s** loop (`motion.md` — currently 1.5s in Tailwind's default pulse)

## 2b.6 Reduced motion

The block in the Phase 2 token spec kills durations globally under
`prefers-reduced-motion: reduce`. Verify it actually takes: staggered lists and scroll-snap must
still *work*, just without the animation. Snap is not motion — keep it.

## Acceptance

Record a screen capture at 412px of: open a sheet, dismiss it, navigate between two tabs, scroll a
horizontal rail to its end, pull a list into view. Compare against the same five before the branch.
**Green build tells you nothing here.**

Then: enable "reduce motion" in the OS and repeat. Nothing should animate; everything should still
function.

---

# PHASE 3 — Optional-prop convention

**Branch:** `fix/optional-prop-types` · **Risk:** low · **Est:** 30 min

Full detail in the work order §Phase 3. Summary: `exactOptionalPropertyTypes: true` rejects an
explicitly-passed `undefined`. Declare optional props on shared primitives as
`prop?: T | undefined` **at the component definition**.

**Acceptance is unusual and non-negotiable: zero `TS2375` errors, with no call site modified.**
If a call site still errors, the primitive is still wrong. Four previous attempts patched callers
and the error count went 3 → 10.

Also fix `Btn`'s `Record<string, string>` variant map, which discards the union and makes
`styles[variant]` possibly-undefined at type level.

---

# PHASE 4 — Icons

**Branch:** `fix/icons-no-emoji` · **Risk:** low · **Est:** 1 hour

Six text glyphs render as empty boxes on real devices. `lucide-react` is installed and already
used for `BottomNav`, `Row`, `Stars`.

| Glyph | File | Replace |
|---|---|---|
| `⌕` | `ml/ui.tsx` `SearchBar` | `<Search />` |
| `🎙` | `ml/ui.tsx` `SearchBar` | `<Mic />` |
| `←` | `ml/ui.tsx` `TopBar` | `<ArrowLeft />` |
| `✓` | `ml/ui.tsx` `VerifiedBadge` | `<BadgeCheck />` |
| `📍` | `ml/cards.tsx` `TalentRow` | `<MapPin />` |
| `♡` | `ml/cards.tsx` `TalentRow` | `<Heart />` |

Then sweep `src/data/*` for emoji-as-data — **`src/data/portfolio.ts` exposes `workType().emoji`,
consumed by `src/routes/discover.music.tsx`.** Replace with an icon key.

Keep `aria-label="Back"` on `TopBar` when swapping the glyph. `SearchBar`'s `<input>` has no
label — add one while you are in the file.

**Acceptance:** no emoji in `src/` outside comments.

---

# PHASE 5 — Navigation and dead controls

**Branch:** `fix/nav-and-dead-controls` · **Risk:** low · **Est:** 1 hour

**5.1** `const active = path === item.to` in `BottomNav` — exact match, broken on ~200 of 205
routes. Longest-prefix match, `/` special-cased (it is Discover and would otherwise match
everything).

**5.2** Dead controls — `recently-viewed.tsx` "Clear history", `cards.tsx` `TalentRow` "Message".
Sweep for more. Where the action is not built: `disabled` plus a reason.

**5.3** `AvailabilityBadge` keys styling off display copy (`map[status] ?? map["Booked"]`). Key
off a union; label separately.

**5.4** Touch targets — `TalentRow`'s link (`min-h-11`) and save button (`h-11 w-11`) are 44px
against a 48dp floor. **Do not enlarge chips**: v9 says a 32px chip is correct and keeps an
invisible 48dp hit area instead.

**5.5** `/talent/$id` — new 10 Sep screens link a route scheduled for redirect since 2 Sep. Land
the redirect or stop wiring to it. Flag which.

---

# PHASE 6 — De-duplication

**Branch:** `refactor/dedupe-components` · **Risk:** highest · **Est:** 1 day

You asked for this three times: 12, 14 and 17 August.

| Duplicate | Action |
|---|---|
| `AvailabilityBadge` / `StatusPill` | Keep `StatusPill` — `kit.tsx` already claims "One status system across every flow" above it |
| `Chip` / `Segmented` | Byte-identical class strings; keep one |
| `Sheet` / `Dialog` | **Replace with Radix** `ui/sheet.tsx`, `ui/dialog.tsx` |
| `Loading` | Replace with `ui/skeleton.tsx` |

Hand-rolled modals have no focus trap, no Escape, no scroll lock, no `aria-labelledby`. 45 Radix
primitives ship unused.

**6.1** Type `Row` properly and delete the `as any` + eslint-disable. **This will surface real
broken links across 205 routes.** That is the point — they exist now and are hidden. Enumerate
them in the PR; do not cast them away.

**6.2** Move inline `style={{ background: "var(--…)" }}` (in `BottomNav`, `AvailabilityBadge`,
`StatusPill`, `Meter`, `TalentRow`) to Tailwind classes on the Phase 2 tokens.

**Do this phase when you can watch it.** Swapping four modals changes focus, Escape and scroll
behaviour on every sheet in the app.

---

# PHASE 7 — Inventory (no commit)

**Est:** 2 hours · **Output:** a table, not a branch

**205 route bodies and 21 of 22 `src/data` modules have never been opened by anyone.** Count per
file:

| Signal | Pattern |
|---|---|
| Arbitrary radius | `rounded-[` |
| Arbitrary type | `text-[` |
| Stock imagery | `unsplash`, `images.` |
| Emoji | non-ASCII glyph ranges |
| Dead controls | `<Btn` / `<Link` with no `onClick`/`to` |
| Deprecated route | `/talent/$id` |
| Inline design values | `style={{` |
| Relative-time strings | `"… ago"`, `"in … days"` |
| Fabricated state | hardcoded ratings, counts, availability |

**Output columns:** file · route · counts per signal · est. effort · notes.

**This number decides whether Phase 8 is one PR or three weeks.** The last plan written without it
collapsed at Wave 2. Do not skip it, and do not let it become a commit — it is a document.

---

## Sequencing note

Phase 2 → **2b** → 3 → 4 → 5 → 6 → 7 is the written order, but **6 before 2b is better if the
schedule allows**: Radix brings its own animation hooks, so doing motion first means writing sheet
choreography twice.

Phases 3, 4, 5 are independent of everything and can run in any order, or in parallel by a second
pair of hands. Phase 7 can run at any time — it touches nothing.
