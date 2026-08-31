# Motion plan — why the product looks dead, and the order to fix it

**Drafted 2026-08-31.** Not started. Sits alongside
`design-system-rebuild-plan.md` and `guide-design-system-fix-plan.md`.

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

CSS-only via `animation-timeline: view()`. No `IntersectionObserver`, no JS, no
scroll listener — so it costs nothing on the main thread and cannot jank.

- One reveal per section, not per element
- Must degrade to fully-visible where unsupported — content is readable first,
  animated second, per the Motion Sensitivity rule
- **Explicitly excluded:** parallax and scroll-jacking. The rules name both as
  disorienting, and the palette rule ("media carries the colour") means the
  photography is already doing the work parallax would attempt.

## Phase 4 — Screen-to-screen transitions

View Transition API for `/preview` navigation, which is currently a full reload.

- Forward navigates left/up, back navigates right/down — `navigation-direction`
- Shared-element continuity for card → detail, where the artwork is the anchor
- Native equivalents already exist (Compose shared-element, SwiftUI
  `matchedGeometryEffect`) and map onto `MLMotion.spring3`, the token whose
  description already reads *"sheet reveal, card lift, shared-element /
  container transform"*

## Phase 5 — Loading, which is the real gap

Measured across all 184 screens:

| State | Declared | Rendered | Both |
|---|---|---|---|
| empty | 27 | 61 (`.ml-empty`) | 25 |
| **loading** | **5** | **1** (`.ml-skeleton`) | **1** |

Empty states are real. **Loading is not** — `auth/splash`, `auth/email_auth`,
`verifications/verification_screen` and `notifications/notifications_center` all
declare a loading state that renders nothing.

This is a *motion* problem only in part; mostly it is a missing state. The
Loading States rule is High severity, and the guide's own state map already
predicts this by showing `Loading` as *composed* rather than owned by anything.

**This phase is the one with product impact rather than polish**, and arguably
belongs before Phase 3.

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
