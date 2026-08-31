# Motion

Use purposeful motion only: pressed state, gentle card lift, sheet/modal fade
and scale, swipeable discovery, audio waveform feedback, and skeleton loading.
Respect reduced-motion preferences.

> **Reconciled 2026-08-31.** This file predates the v9 system and several of
> its values had gone stale. Corrections are marked below; the rules that
> survived are unchanged. It is now rendered into `/guide` by
> `scripts/build-guide.mjs`, so this file is the source and cannot drift
> from the catalogue again.

## Timing

Web durations are the four `--duration-*` tokens. Native does **not** take a
duration — it takes a spring, and `MLMotion.spring1..4` are emitted to both
Compose and SwiftUI from the same source.

| Token | Web | Usage |
|---|---|---|
| `--duration-1` | 125ms | Button press, chip toggle, ripple |
| `--duration-2` | 200ms | Hover, icon change, colour change, dismissal |
| `--duration-3` | 320ms | Sheet reveal, card lift, shared-element transform |
| `--duration-4` | 500ms | Modal fade+scale, splash |

*Was 150/200/300/500. Three of the four disagreed with the shipped tokens.*

## Rules

- **Three easings, not one:** `--ease-standard` for most motion,
  `--ease-enter` for arrivals, `--ease-spring` for overshoot.
  *Was "use one easing curve for all motion". A single curve cannot express
  arrival and departure, and native uses springs regardless.*
- Pressed state: `scale(0.97)`. *(unchanged — matches `.ml-btn:active`)*
- Hover: lift 2–4px with minimal shadow. Pointer only; never the sole signal.
- Skeleton shimmer: **1.4s** loop. *Was 1.5s.*
- Audio waveform: CSS keyframe bounce, staggered delays.
- Always honour `prefers-reduced-motion`.
- Exit runs at roughly 60–70% of enter.

## Known gap

The system has feedback motion but no choreography: **0** entry animations,
**0** view transitions, **0** scroll-driven motion, and state switching is a
hard cut across 518 states. See `docs/09-Roadmap/motion-plan.md`.
