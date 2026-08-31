# Native handoff

**Generated.** Source: `frontend/guide/tokens/tokens.json` (v9.0.0).
Regenerate with `node scripts/build-handoff.mjs`. Do not hand-edit
`MusicLinkdTokens.kt` — the web preview at `/guide` is generated from the
same file, and patching one side is how the two drift apart.

## What is here

| File | Contents |
|---|---|
| `MusicLinkdTokens.kt` | Compose `Color`, `Dp`, `TextStyle`, `FontWeight`, spring specs and shape tokens |
| `MusicLinkdTokens.swift` | SwiftUI `Color`, `CGFloat`, `Font`, `Font.Weight`, `Animation.spring` and shadow tokens |

## The two are not translations of each other

Four values genuinely differ by platform, and the generator emits each from its
own `$android` / `$ios` form rather than converting one into the other:

| | Compose | SwiftUI |
|---|---|---|
| `size.3` — touch minimum | **48**dp (Material) | **44**pt (Apple HIG) |
| `radius.4` | `9999.dp` | `Capsule()` |
| motion | `dampingRatio` / `stiffness` | `response` / `dampingFraction` |
| type | `sp` ramp + `FontWeight(n)` | Dynamic Type `textStyle` + named weight |

Copying the Android numbers into Swift would have produced a file that looks
right and is wrong in four places — most damagingly the touch target, which
would be 4pt over Apple's minimum on every control in the app.

## Before you build

Three families need bundling as font resources. The file currently points all
three at `FontFamily.Default` so it compiles on its own:

| Token | Family | Weights |
|---|---|---|
| `Inter` | Inter | 400 · 500 · 600 · 700 |
| `InterTight` | Inter Tight | 600 · 700 · 800 |
| `UnicaOne` | Unica One | **400 only** |

Unica One ships a single weight. Requesting 500 resolves down to 400 and is
safe; anything at 600 or above will be synthesised into a fake bold, which on
strokes this fine reads as a rendering fault rather than as emphasis.

## Scales

Every scale is capped at 4 steps and the build fails if one grows past it.

| Scale | Steps | Values (dp) |
|---|---|---|
| `space` | 4 | 4 · 8 · 16 · 32 |
| `radius` | 4 | 8 · 16 · 32 · 50 |
| `size` | 4 | 32 · 40 · 48 · 56 |
| `icon` | 4 | 16 · 20 · 24 · 32 |
| `avatar` | 4 | 24 · 32 · 48 · 64 |
| `border` | 2 | 1 · 2 |
| `elevation` | 4 | 1 · 3 · 8 · 12 |

Two scales do not pass a strict 1.5&times;-per-step contrast test, and both are
constrained by something outside taste rather than by oversight:

- **`size`** 32/40/48/56 — 48dp is the Material touch minimum and 44pt the iOS
  one, so an exponential ramp would put half the scale under the accessibility
  floor. It is a constant set, not a scale.
- **`type`** 12/16/22/32sp — the perfect fourth, the standard modular ratio for
  text. A 2&times; ramp is 12/24/48/96 with nothing usable between caption and
  headline.

## What is deliberately absent

| Absent | Why |
|---|---|
| Dark mode | Not built. Light-only is a position, not a gap. The dark elevation rule has never been decided — see `meta.darkMode`. |
| `zIndex` | Compose uses composition order. |
| Brand expression colours | Marketing only. A screen never renders them. |
| Safe-area values | Read `WindowInsets` from the framework; the px numbers in the source are browser fallbacks. |
| Pixel breakpoints | Branch on `WindowWidthSizeClass` — Medium ≥ 600dp, Expanded ≥ 840dp. |

## Roles

Semantic role names (`actionPrimary`, `surfaceCanvas`) are documented in
`semantic` in the source but emit no CSS on web, because there they would be a
second name for the same value. On Android, map them in your `ColorScheme`:

| Role | Token | Note |
|---|---|---|
| primary | `MLColor.Ink` | The CTA colour is **black**, not pink. |
| onPrimary | `MLColor.White` | |
| secondary / accent | `MLColor.Pink` | Focus, selection, active tab. Never a CTA fill. |
| background | `MLColor.Paper` | |
| surface | `MLColor.White` | |
| outline | `MLColor.Line` | |
| error | `MLColor.Error` | |

One rule worth carrying over verbatim: **pink fills use `Pink`; pink text and
anything reversed out of pink uses `PinkPressed`.** `Pink` measures 3.56:1
against white and fails AA as text; `PinkPressed` measures 5.37:1. The accent
button therefore fills with `PinkPressed` so its white label passes.
