# Motion plan — why the product looks dead, and the order to fix it

**Drafted 2026-08-31.** All five phases landed 2026-09-01 (order 1 → 5 → 2 →
3 → 4, per the plan below) except Phase 4's directional + shared-element
tier, deliberately scoped out — see that section. Sits alongside
`design-system-rebuild-plan.md` and `guide-design-system-fix-plan.md`. None
of this has been visually verified yet — every phase is a local file edit;
nothing has been watched render in a real browser. See each phase's section
for what was corrected from the original diagnosis along the way (Phase 5's
"renders nothing" claim was wrong for all 4 named screens; Phase 3 needed a
new wrapper element the original plan didn't anticipate).

## The diagnosis, measured

| Signal | Measured |
|---|---|
| Animation code in `app-shell.js` | **0 lines** |
| States switched by `el.hidden = …` | **518**, across 184 screens |
| Guide sections with any `transition`/`animation` | **33 of 133 (25%)** |
| `@starting-style` (entry) | **0** |
| `view-transition` (screen change) | **0** |
| `animation-timeline` / `scroll-timeline` | **0** |
| `IntersectionObserver` | **0** |
| `:active` press states | 31 |
| `:hover` | 21 |

**The system has feedback but no choreography.** Poke it and it answers — 31
press states, a ripple, a scale. Leave it alone and it is a photograph. Nothing
arrives, nothing leaves, nothing responds to scroll position.

The single largest cause is one line in `webapp/shared/app-shell.js`:

```js
el.hidden = !states.includes(state);
```

State switching **is** the interaction model here — 518 of them — and every one
is an instant blink.

**The tokens already exist and are unused.** `--duration-1..4`
(125/200/320/500ms), `--ease-standard`, `--ease-enter`, `--ease-spring`, plus
four spring specs already emitted to Compose (`MLMotion.spring1..4`) and SwiftUI
(`Animation.spring(response:dampingFraction:)`). This is not missing design
work. It is 75% of components never referencing the vocabulary that was built
for them — the same pattern as the weight tokens and the dead tokens.

## Governing constraints

From `ui-ux-pro-max`, the three High-severity rules that shape everything below:

- **Excessive Motion** — *animate 1–2 key elements per view maximum.* This plan
  is therefore deliberately small. The failure mode of a motion pass is
  animating everything, which reads as cheap rather than alive.
- **Reduced Motion** — honour `prefers-reduced-motion`. `ml-core.css` already
  has the block; every phase must land inside it.
- **Cancellable State Transitions** — *never depend on `transitionend` for state
  correctness.* Rapid state changes must cancel and set the final state
  directly. This is the rule that makes Phase 1 CSS-only rather than JS-driven.

Supporting rules applied throughout: exit at 60–70% of enter duration; stagger
30–50ms per item; `transform`/`opacity` only (never width/height/top/left);
enter-from-below means deeper, exit-upward means back; animations stay
interruptible and never block input.

## Platform reality

Three targets, one token source. Motion must be authored so each consumes its
own form — as `tokens.json` already does for every other axis.

| | Web / PWA | Android (Compose) | iOS (SwiftUI) |
|---|---|---|---|
| Expression | `transition` + `@starting-style` | `spring(dampingRatio, stiffness)` | `Animation.spring(response, dampingFraction)` |
| Already emitted? | ✅ `--duration-*`, `--ease-*` | ✅ `MLMotion.spring1..4` | ✅ `MLMotion.spring1..4` |
| Reduced motion | `prefers-reduced-motion` | `ANIMATOR_DURATION_SCALE` | `UIAccessibility.isReduceMotionEnabled` |
| Touch floor | 48dp (enforced) | 48dp | **44pt** |

A duration is not translatable to a spring, which `tokens.json` already records.
No phase below invents a native value; each maps onto an existing spring step.

**Device factors that gate the work:** safe areas (`--safe-top` /
`--safe-bottom`) must not be animated — a moving safe inset causes the layout
shift the rules forbid. Window classes `--bp-medium` (600) and `--bp-expanded`
(840) are the only breakpoints; motion must not introduce a third. Frame budget
is ~16ms, so every phase is `transform`/`opacity` and composited.

## Phase 1 — Animate the state switch

**The whole product, one rule.** 518 transitions currently hard-cut.

Modern CSS does this without touching `app-shell.js`, which matters for two
reasons: the concurrent session owns that file, and the Cancellable rule says
state correctness must not depend on an animation event. `hidden` stays the
source of truth; the browser animates the discrete change.

```css
[data-state] {
  transition:
    opacity var(--duration-2) var(--ease-standard),
    transform var(--duration-2) var(--ease-standard),
    overlay var(--duration-2) allow-discrete,
    display var(--duration-2) allow-discrete;
}
@starting-style { [data-state]:not([hidden]) { opacity: 0; transform: translateY(var(--space-2)); } }
[data-state][hidden] { opacity: 0; transform: translateY(calc(var(--space-2) * -1)); }
```

Enter rises from below (deeper), exit lifts away (back) — the hierarchy rule.
Exit uses `--duration-1` (125ms) against enter's `--duration-2` (200ms), which
is the 60–70% ratio.

- **Effort:** one rule, no markup change, no JS change
- **Risk:** low — additive; `hidden` semantics unchanged, so nothing regresses
  if the browser lacks `allow-discrete`
- **Verify:** every state on a screen with 5+ states (`search`, at 11.5
  states/screen) transitions rather than blinks; screen reader still sees
  `hidden`; rapid state spamming leaves the correct final state

## Phase 2 — Entry choreography for collections

Cards, rails and lists. **Not every element** — the Excessive Motion rule caps
this at the primary collection per screen.

- Stagger 40ms per item, capped at ~8 items (beyond that it reads as slow, not
  choreographed)
- `--duration-2` in, `--ease-enter` (decelerate on arrival)
- `transform: translateY()` + `opacity` only
- Applies to `.ml-artist-card`, `.ml-studio-card`, `.ml-market-card`,
  `.ml-track`, `.ml-collab-rail` — the recycled collection components

**Precondition:** Phase 1 must land first, or entry animations fire on every
state switch and the screen becomes noisy.

## Phase 3 — Scroll-driven reveal

**Done, 2026-09-01, with a scope correction found along the way.**
`.ml-section` (the heading row: `<div class="ml-section"><h2>…</h2></div>`) is
not a section container — it's just the label. The actual section content
(cards, lists) sits as separate flat siblings after it, not nested inside it.
Animating `.ml-section` alone would only move the heading; the content below
would still pop in instantly, a weaker and arguably worse-looking effect than
doing nothing.

Fixed by introducing `.ml-section-block`, a wrapper element (`<section
class="ml-section-block">`) that groups each `.ml-section` heading with every
sibling that follows it up to the next `.ml-section` (or end of parent), so
one reveal covers heading + content together. Rolled out via a script that
inserts only the wrapper open/close tags into the original file text at exact
byte offsets — verified byte-identical everywhere else across all 80 files
(diffed against a pre-change backup) and tag-balance-checked, rather than
round-tripped through an HTML serializer, which was tried first and rejected
after it silently lowercased SVG `viewBox` to `viewbox` (would have broken
every icon's viewBox — SVG attribute names are case-sensitive, HTML parsers
are not) and reformatted every line of every file.

**Scope: 80 of the 101 `.ml-section` usages, not all of them.** A repo-wide
scan split the 101 into three groups:
- **69 files** — 2+ `.ml-section` under the same parent, on a long
  independently-scrollable page (`home_dashboard`, `discover_feed`,
  `public_profile_view`, etc.). Wrapped.
- **11 files** — exactly one `.ml-section` on a scrollable `main`, no
  `[data-state]` ancestor. Wrapped.
- **21 files** — exactly one `.ml-section`, and it's the first thing inside a
  `[data-state]` wizard-step div (`verification_screen` and 20 others). **Not
  wrapped, deliberately** — that content already animates in via the Phase 1
  state-entry rule when its step becomes active. Also wrapping it for scroll
  would stack two animation triggers on the same element, which the
  Excessive Motion rule (1-2 key elements per view) rules out.

Also preserved: one file (`portfolio/media_preview`) had its `.ml-section`'s
parent `<main>` carrying an explicit flex `gap`, which used to space the
heading from its content as flat flex siblings. Wrapping them would have
silently dropped that gap (down to the heading's own smaller bottom margin).
The wrapper for that one file replicates the same gap value internally
(`display:flex;flex-direction:column;gap:var(--space-3)`) so spacing is
unchanged; the other 79 didn't need this — their parents have no explicit
gap, so spacing comes from `.ml-section`'s own margin and normal block-margin
collapsing through the (unstyled, `display:block`) wrapper, which is
transparent to it.

CSS lives in `ml-widgets.css`, guarded by `@supports (animation-timeline:
view())` — unsupported browsers get the plain unanimated layout, never an
invisible one, since there is no unguarded `opacity: 0` baseline outside the
`@supports` block.

- One reveal per section (heading + content together), not per element
- Degrades to fully-visible where unsupported — content is readable first,
  animated second, per the Motion Sensitivity rule
- **Explicitly excluded:** parallax and scroll-jacking. The rules name both as
  disorienting, and the palette rule ("media carries the colour") means the
  photography is already doing the work parallax would attempt.

**Not yet done:** visual verification. Same caveat as every other phase —
these are local file edits; the hosted `/preview` serves a deployed build,
so nothing here has actually been watched render.

## Phase 4 — Screen-to-screen transitions

**Base tier done, 2026-09-01; directional + shared-element tier deliberately
not started.** `/preview` screens are separate static HTML files navigated
via full `<a href>` page loads, not a single-page app — so this uses the
**cross-document** View Transition API (`@view-transition { navigation:
auto; }` in `ml-core.css`), not `document.startViewTransition()`. Zero JS:
CSS's forward-compatible parsing means a browser that doesn't recognise the
at-rule just ignores it, so unsupported browsers keep the normal instant
navigation they already had — nothing to guard, nothing that can render
broken. Every screen-to-screen click across all ~184 screens now gets a
default crossfade (styled to `--duration-3` / `--ease-standard` instead of
the browser's own default timing) instead of an instant swap.

Not done, and scoped separately rather than folded into this pass:

- **Directional detection** (forward slides left/up, back slides right/down)
  needs a `pageswap` listener calling
  `event.viewTransition.types.add('forward'|'back')` so CSS can select on
  `:active-view-transition-type()` — a new small script, not a CSS-only
  change like everything else in this plan.
- **Shared-element continuity** (card → detail, artwork as the anchor) needs
  `view-transition-name` assigned consistently across every matched
  card/detail screen pair — real per-screen work across the ~184-screen
  corpus, not a global rule.

Both map onto `MLMotion.spring3`, the token whose description already reads
*"sheet reveal, card lift, shared-element / container transform"*, so the
native (Compose/SwiftUI) side has an equivalent waiting whenever this gets
scoped.

## Phase 5 — Loading, which is the real gap

**Correction, 2026-09-01: the diagnosis below was wrong on the specific
screens.** Read all four named files directly rather than trusting this
table. Findings:

- `auth/splash` — loading state renders real text plus a spinner. Not empty.
- `auth/email_auth` — `data-state="loading"` wraps a real `.ml-spinner` in the
  footer CTA. Not empty.
- `verifications/verification_screen` — has **no** `data-state="loading"`
  anywhere in the file. It doesn't declare a loading state at all, contrary
  to this table.
- `notifications/notifications_center` — had a spinner + "Loading…" text.
  Not empty, but this was the one real problem: a content list using a
  spinner instead of the skeleton pattern this same file's own comment
  prescribes ("Skeletons, not spinners, for content"). **Fixed 2026-09-01** —
  now three `.ml-skeleton` rows shaped like real notification rows, matching
  `home_dashboard`'s existing correct pattern.

A repo-wide grep (`data-state="[^"]*\bloading\b[^"]*"` across
`webapp/flows/**/index.html`) found loading states declared in exactly these
four files — no fifth, no hidden empty ones. So "Declared: 5, Rendered: 1
(`.ml-skeleton`), Both: 1" was very likely counting `.ml-skeleton` usage
specifically (only `home_dashboard` uses it) rather than "renders nothing" —
the original phrasing overstated the problem. The real, narrower finding:
loading treatment was inconsistent (skeleton in one place, spinner in two,
plain text in one), not absent. That inconsistency is now reduced by one
(`notifications_center` fixed); `email_auth`'s button spinner is arguably
correct as-is (a button awaiting a network response is exactly what a
spinner is for, not a skeleton), and `splash`'s plain-text treatment is a
judgment call, not a defect, for a resume screen with no content to shape a
skeleton around.

This is a *motion* problem only in part; mostly it was a consistency
question, not a missing-state one as originally written. The Loading States
rule is still High severity and worth keeping in mind for any future screen
that declares loading with no visual treatment — none currently do.

## What this plan does not do

- **No new tokens.** The budget is at its ceiling (108/108); every phase
  consumes existing `--duration-*` / `--ease-*`.
- **No JS animation library.** GSAP and equivalents are out — this is a
  vanilla-CSS system with no runtime, and adding one contradicts the Boundaries
  section of `frontend/guide/README.md`.
- **No parallax, no scroll-jacking, no autoplay expansion.** One existing
  autoplay video in the guide already needs a pause control and an off-screen
  stop under the Auto-Play rule; that is a fix, not an addition.
- **No motion on safe-area insets or layout properties.**

## Verification, per phase

Run against a real viewport — a hidden pane collapses to 0×0 and silently
invalidates every geometry reading.

```bash
node scripts/build-guide.mjs && node scripts/audit-screens.mjs
```

In the browser, with served bytes confirmed equal to on-disk bytes:

1. `prefers-reduced-motion: reduce` — every phase renders its final state, no
   motion, no missing content
2. Rapid state switching — final state correct, no stuck intermediate
3. Frame budget — no animation of `width`/`height`/`top`/`left`; confirm
   `transform`/`opacity` only
4. Touch targets unchanged — 0 of 383 below 48dp is the current baseline
5. 375px width and landscape

## Order

**1 → 5 → 2 → 3 → 4.** Phase 1 is the largest visible change for the smallest
diff and unblocks the rest. Phase 5 jumps the queue because a declared-but-absent
loading state is a defect, not polish. Phases 3 and 4 are the least essential and
the easiest to overdo.
