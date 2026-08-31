#!/usr/bin/env node
/**
 * build-handoff.mjs — emits the NATIVE handoff from tokens.json.
 *
 *   frontend/guide/tokens/tokens.json   (source of truth, hand-edited)
 *        └─→ docs/handoff/MusicLinkdTokens.kt     Compose tokens
 *        └─→ docs/handoff/MusicLinkdTokens.swift  SwiftUI tokens
 *        └─→ docs/handoff/README.md               how to consume them
 *
 * Why this exists: tokens.json has carried explicit $android AND $ios forms on
 * every token since v9 — sp ramps, Dynamic Type styles, spring physics, Dp and
 * CGFloat values — and nothing consumed them. Compose got its generator on
 * 2026-08-30; SwiftUI was still unread on 2026-08-31, so 63 $ios entries were
 * authored, reviewed and shipped to no consumer at all. Both are emitted here. Dual authoring that no build reads is just a longer JSON file. This
 * is the consumer, and it is generated for the same reason ml-tokens.css is:
 * a hand-copied Kotlin file drifts from the CSS within one sprint, and the
 * whole point of the dual authoring was that web and native cannot disagree.
 *
 * What it deliberately does NOT emit:
 *   - zIndex     — Compose uses composition order, not a z scale
 *   - layout.max — a web page max-width; on Android that is a WindowSizeClass
 *   - chrome.scrollbar — browser chrome
 *   - the brand* colours — expression only, never rendered by the app
 *
 * Usage:  node scripts/build-handoff.mjs [--check]
 */

import { readFileSync, writeFileSync, mkdirSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const SRC = resolve(ROOT, "frontend/guide/tokens/tokens.json");
const OUT_KT = resolve(ROOT, "docs/handoff/MusicLinkdTokens.kt");
const OUT_SWIFT = resolve(ROOT, "docs/handoff/MusicLinkdTokens.swift");
const OUT_MD = resolve(ROOT, "docs/handoff/README.md");
const CHECK = process.argv.includes("--check");

const T = JSON.parse(readFileSync(SRC, "utf8"));
const f = T.foundation;

/* ── helpers ───────────────────────────────────────────────────────────── */

const pascal = (s) => s.replace(/(^|[-_])([a-z])/g, (_, __, c) => c.toUpperCase());
/** #EA4C89 -> 0xFFEA4C89 ; rgba(13,13,13,0.32) -> 0x520D0D0D */
function composeColor(v) {
  const hex = v.match(/^#([0-9A-Fa-f]{6})$/);
  if (hex) return `0xFF${hex[1].toUpperCase()}`;
  const rgba = v.match(/^rgba?\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*(?:,\s*([\d.]+)\s*)?\)$/);
  if (rgba) {
    const [, r, g, b, a] = rgba;
    const alpha = Math.round((a === undefined ? 1 : parseFloat(a)) * 255);
    const h = (n) => Number(n).toString(16).padStart(2, "0").toUpperCase();
    return `0x${h(alpha)}${h(r)}${h(g)}${h(b)}`;
  }
  throw new Error(`cannot express "${v}" as a Compose Color`);
}
/** "16.dp" -> 16 ; "50" -> 50 */
const dpNum = (v) => String(v).replace(/\.dp$/, "");
const sp = (v) => String(v).replace(/sp$/, "");
/** "-0.01em" at a given sp size -> Compose sp letterSpacing */
const trackSp = (em, sizeSp) => (parseFloat(em) * parseFloat(sizeSp)).toFixed(2);

const isNum = (k) => /^\d+$/.test(k);
const steps = (g) => Object.keys(g).filter(isNum);

/* ── Kotlin ────────────────────────────────────────────────────────────── */

const colourLines = Object.entries(f.color)
  .filter(([k]) => !k.startsWith("$") && !k.startsWith("brand"))
  .map(([k, v]) => {
    const doc = (v.$description || "").split(".")[0];
    return `    /** ${doc || k} */\n    val ${pascal(k)} = Color(${composeColor(v.$value)})`;
  }).join("\n");

const dimGroup = (name, group, unit = "dp") =>
  steps(group).map((k) => `    val ${name}${k} = ${dpNum(group[k].$android)}.${unit}`).join("\n");

const componentLines = Object.entries(f.component)
  .filter(([k]) => !k.startsWith("$"))
  .map(([k, v]) => `    /** ${(v.$description || "").split(".")[0]} */\n    val ${pascal(k)} = ${dpNum(v.$android)}.dp`)
  .join("\n");

/* Weight is a named scale on Compose too, for the same reason it became one on
   web: it was written as a bare FontWeight(600) inside each TextStyle, so the
   700 -> 600 change of 2026-08-31 reached the CSS in one edit and reached
   Compose only because these four styles happened to be regenerated. Nothing
   named the scale, so nothing outside MLType could reference it — a Composable
   wanting a semibold label had to type the number, which is exactly the state
   the web side was just dug out of (114 literal declarations). */
const weightLines = steps(f.weight)
  .map((k) => `    /** ${(f.weight[k].$description || "").split(".")[0]} */\n    val W${k} = FontWeight(${f.weight[k].$value})`)
  .join("\n");

/** A type step's weight must BE one of the four steps, never a stray value. */
const WEIGHT_TOKEN = new Map(steps(f.weight).map((k) => [f.weight[k].$value, `MLWeight.W${k}`]));
function weightRef(value, where) {
  const tok = WEIGHT_TOKEN.get(value);
  if (!tok) {
    throw new Error(
      `${where} uses fontWeight ${value}, which is not a step in foundation.weight ` +
        `(${[...WEIGHT_TOKEN.keys()].join(", ")}). Add the step or change the type ramp — ` +
        `do not emit a weight Compose cannot name.`
    );
  }
  return tok;
}

const typeLines = steps(f.type).map((k) => {
  const a = f.type[k].$android;
  const doc = f.type[k].$description;
  return `    /** ${doc} — Material ${a.style} */
    val Type${k} = TextStyle(
        fontFamily = ${a.family === "InterTight" ? "InterTight" : a.family === "UnicaOne" ? "UnicaOne" : "Inter"},
        fontSize = ${sp(a.size)}.sp,
        lineHeight = ${sp(a.lineHeight)}.sp,
        fontWeight = ${weightRef(a.weight, `type.${k}.$android`)},
        letterSpacing = ${trackSp(a.letterSpacing, a.size)}.sp,
    )`;
}).join("\n");

const motionLines = steps(f.motion).map((k) => {
  const a = f.motion[k].$android;
  return `    /** ${f.motion[k].$description}${a.note ? ` — ${a.note}` : ""} */
    fun <T> spring${k}() = spring<T>(dampingRatio = ${a.dampingRatio}f, stiffness = ${a.stiffness}f)`;
}).join("\n");

const opacityLines = steps(f.opacity)
  .map((k) => `    /** ${f.opacity[k].$description} */\n    const val Opacity${k} = ${f.opacity[k].$value}f`)
  .join("\n");

const gestureLines = Object.entries(T.interaction)
  .filter(([k]) => !k.startsWith("$") && T.interaction[k].$android !== "—")
  .map(([k, v]) => {
    const a = String(v.$android);
    // Every value in this group already arrives as a valid Kotlin literal —
    // "80.dp", "500L", "0.5f" — because tokens.json authors the Android form
    // directly rather than a bare number. So nothing is appended. An earlier
    // version added an f to anything not ending .dp or L and emitted "0.5ff",
    // which would not have compiled.
    const lit = a;
    return `    /** ${(v.$description || "").split(".")[0]} */\n    val ${pascal(k)} = ${lit}`;
  }).join("\n");


/* ── SwiftUI ───────────────────────────────────────────────────────────────
   tokens.json has carried $ios forms on 63 entries since v9 and NOTHING read
   them — the whole iOS half of the dual authoring was writing to a file no
   build consumed. Compose got its generator on 2026-08-30; this is the same
   thing for SwiftUI, added 2026-08-31.

   iOS is not Android with different syntax, and the values differ where the
   platforms genuinely differ:
     · size.3 is 44, not 48 — Apple's touch minimum, not Material's
     · radius.4 is Capsule(), not a 9999 number
     · motion is response/dampingFraction, not dampingRatio/stiffness
     · type maps onto Dynamic Type text styles so it scales with the user's
       accessibility setting, exactly as the sp ramp does on Android
   Copying the Android numbers across would have produced a plausible file
   that was wrong in four places. */

/** #EA4C89 -> Color(red:green:blue:) ; rgba(...) -> with opacity */
function swiftColor(v) {
  const hex = v.match(/^#([0-9A-Fa-f]{6})$/);
  const c = (n) => (parseInt(n, 16) / 255).toFixed(4);
  if (hex) {
    const h = hex[1];
    return `Color(red: ${c(h.slice(0,2))}, green: ${c(h.slice(2,4))}, blue: ${c(h.slice(4,6))})`;
  }
  const rgba = v.match(/^rgba?\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*(?:,\s*([\d.]+)\s*)?\)$/);
  if (rgba) {
    const [, r, g, b, a] = rgba;
    const f = (n) => (Number(n) / 255).toFixed(4);
    const op = a === undefined ? "" : `.opacity(${a})`;
    return `Color(red: ${f(r)}, green: ${f(g)}, blue: ${f(b)})${op}`;
  }
  throw new Error(`cannot express "${v}" as a SwiftUI Color`);
}

/* Apple names weights; it does not take numbers. The map is explicit rather
   than arithmetic so an unmappable weight FAILS instead of silently rounding
   to something close. */
const SWIFT_WEIGHT = { 400: ".regular", 500: ".medium", 600: ".semibold", 700: ".bold", 800: ".heavy" };
function swiftWeight(value, where) {
  const w = SWIFT_WEIGHT[value];
  if (!w) throw new Error(`${where}: no SwiftUI Font.Weight for ${value}`);
  return w;
}

const sColor = Object.entries(f.color)
  .filter(([k]) => !k.startsWith("$") && !k.startsWith("brand"))
  .map(([k, v]) => `    /// ${(v.$description || "").split(".")[0]}\n    public static let ${k} = ${swiftColor(v.$value)}`)
  .join("\n");

const sScale = (group, prefix) => steps(f[group])
  .filter((k) => f[group][k].$ios !== "capsule")
  .map((k) => `    /// ${(f[group][k].$description || "").split(".")[0]}\n    public static let ${prefix}${k}: CGFloat = ${f[group][k].$ios}`)
  .join("\n");

const sWeight = steps(f.weight)
  .map((k) => `    /// ${(f.weight[k].$description || "").split(".")[0]}\n    public static let w${k}: Font.Weight = ${swiftWeight(f.weight[k].$value, `weight.${k}`)}`)
  .join("\n");

const sType = steps(f.type).map((k) => {
  const a = f.type[k].$ios;
  const doc = f.type[k].$description;
  return `    /// ${doc}
    public static let type${k} = Font.system(.${a.textStyle}, design: .default).weight(.${a.weight})`;
}).join("\n");

const sMotion = steps(f.motion).map((k) => {
  const a = f.motion[k].$ios;
  return `    /// ${f.motion[k].$description}
    public static let spring${k} = Animation.spring(response: ${a.response}, dampingFraction: ${a.dampingFraction})`;
}).join("\n");

const sElevation = steps(f.elevation).map((k) => {
  const a = f.elevation[k].$ios;
  return `    /// ${(f.elevation[k].$description || "").split(".")[0]}
    public static let e${k} = Shadow(radius: ${a.radius}, y: ${a.y}, opacity: ${a.opacity})`;
}).join("\n");

const sOpacity = steps(f.opacity)
  .map((k) => `    /// ${f.opacity[k].$description}\n    public static let o${k}: Double = ${f.opacity[k].$value}`)
  .join("\n");

const sComponent = Object.entries(f.component)
  .filter(([k]) => !k.startsWith("$"))
  .map(([k, v]) => `    /// ${(v.$description || "").split(".")[0]}\n    public static let ${k}: CGFloat = ${v.$ios}`)
  .join("\n");

/* The Swift type comes from the token's own $type, not from a blanket CGFloat.
   longPress is a duration in seconds — typing it CGFloat compiles but is wrong
   at every call site that passes it to a timer, and a pinch scale is a bare
   number rather than a length. Getting this from the data means a new
   interaction token cannot be mistyped by omission. */
const SWIFT_TYPE = { dimension: "CGFloat", duration: "TimeInterval", number: "CGFloat" };
const sGesture = Object.entries(T.interaction)
  .filter(([k, v]) => !k.startsWith("$") && v.$ios !== "—")
  .map(([k, v]) => {
    const ty = SWIFT_TYPE[v.$type];
    if (!ty) throw new Error(`interaction.${k}: no Swift type for $type "${v.$type}"`);
    return `    /// ${(v.$description || "").split(".")[0]}\n    public static let ${k}: ${ty} = ${v.$ios}`;
  })
  .join("\n");

/* Haptics are generators you instantiate, not values you store, so they are
   emitted as documentation rather than as constants — a `let` here would be a
   token that no call site could actually use. */
const sHaptic = Object.entries(T.semantic.haptic || {})
  .filter(([k]) => !k.startsWith("$"))
  .map(([k, v]) => `//   ${k.padEnd(10)} ${v.$ios}`)
  .join("\n");

const swift = `//
//  MusicLinkdTokens.swift — GENERATED. Do not edit.
//
//  Source:     frontend/guide/tokens/tokens.json  (v${T.meta.version})
//  Regenerate: node scripts/build-handoff.mjs
//
//  The web preview at /guide and MusicLinkdTokens.kt are generated from this
//  same file, so all three cannot disagree. If a value here looks wrong,
//  change tokens.json and regenerate — do not patch this file.
//
//  Deliberately NOT emitted, for the same reasons as the Compose file:
//    - zIndex            SwiftUI uses view order and .zIndex(), not a scale
//    - layout.max        a web page max-width; on iOS that is a size class
//    - chrome.scrollbar  browser chrome
//    - the brand* colours expression only, never rendered by the app
//
//  Insets are NOT tokens here — read safeAreaInsets from the framework.
//

import SwiftUI

public enum MLColor {
${sColor}
}

public enum MLSpace {
${sScale("space", "s")}
}

/// Radius. Step 4 is Capsule(), not a number — a 9999 corner radius is a web
/// idiom; SwiftUI has a shape for it and clipping to it is exact at any size.
public enum MLRadius {
${sScale("radius", "r")}
    public static let capsule = Capsule()
}

/// Control heights. Step 3 is **44**, not Android's 48 — that is Apple's
/// touch-target minimum, and using Material's number here would be wrong on
/// both platforms at once.
public enum MLSize {
${sScale("size", "s")}
}

public enum MLIcon {
${sScale("icon", "i")}
}

public enum MLAvatar {
${sScale("avatar", "a")}
}

public enum MLBorder {
${sScale("border", "b")}
}

public enum MLOpacity {
${sOpacity}
}

/// Font weight — 400/500/600/800 as Apple's named weights. There is
/// deliberately no .bold step: the heavy UI weight was cut from 700 to 600 on
/// 2026-08-31 and leaving one would let it creep back.
public enum MLWeight {
${sWeight}
}

/// Type. Built on Dynamic Type text styles so every step scales with the
/// user's accessibility setting — the same reason Android authors an sp ramp
/// rather than reusing the web clamp().
public enum MLType {
${sType}
}

/// Motion — response/dampingFraction, which is how SwiftUI expresses a spring.
/// Not a duration, and not Compose's dampingRatio/stiffness pair.
public enum MLMotion {
${sMotion}
}

public struct Shadow {
    public let radius: CGFloat
    public let y: CGFloat
    public let opacity: Double
}

/// Elevation. Prefer a hairline border to a shadow; these exist for the cases
/// that genuinely lift off the surface.
public enum MLElevation {
${sElevation}
}

public enum MLGesture {
${sGesture}
}

/// Dimensions fixed by a platform contract rather than chosen by us.
public enum MLComponent {
${sComponent}
}

/// Haptics are UIKit generators, not values:
${sHaptic}
`;

const kt = `/*
 * MusicLinkdTokens.kt — GENERATED. Do not edit.
 *
 * Source:     frontend/guide/tokens/tokens.json  (v${T.meta.version})
 * Regenerate: node scripts/build-handoff.mjs
 *
 * The web preview at /guide is generated from this same file, so the two
 * cannot disagree. If a value here looks wrong, change tokens.json and
 * regenerate both — do not patch this file.
 *
 * Palette rule: the chrome is monochrome, the media carries all the colour.
 * Light only — dark mode is deliberately not built. See meta.darkMode in the
 * source for the decision that has to be made first if that changes.
 */
package com.musiclinkd.design

import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* The three families. Unica One ships ONE weight (400); requesting anything
   heavier makes the platform synthesise a fake bold, which on strokes this
   fine reads as a rendering fault. */
val Inter = FontFamily.Default      // replace with the bundled Inter resource
val InterTight = FontFamily.Default // replace with the bundled Inter Tight resource
val UnicaOne = FontFamily.Default   // replace with the bundled Unica One resource

/** Colour. Product palette only — the brand* expression swatches are marketing
 *  assets and are deliberately absent from the app. */
object MLColor {
${colourLines}
}

/** Spacing — 4-step exponential (r=2). */
object MLSpace {
${dimGroup("S", f.space)}
}

/** Corner radius. Radius4 is a pill and is expressed as a shape, not a Dp. */
object MLRadius {
${steps(f.radius).filter((k) => k !== "4").map((k) => `    val R${k} = ${dpNum(f.radius[k].$android)}.dp`).join("\n")}
    val Pill = RoundedCornerShape(percent = 50)
}

/** Control heights. Size3 IS the 48dp touch-target minimum and Size4 the 56dp
 *  comfortable target — there is no separate touch-target token because that
 *  would be the same number under a second name. */
object MLSize {
${dimGroup("S", f.size)}
}

/** Icon sizes. 16 and 24 are the two grids the library is actually drawn on;
 *  the others scale. */
object MLIcon {
${dimGroup("I", f.icon)}
}

/** Avatar sizes. NOT MLSize — an avatar is not a control and must not inherit
 *  a touch-target floor. */
object MLAvatar {
${dimGroup("A", f.avatar)}
}

/** Border widths. Zero needs no token. */
object MLBorder {
${dimGroup("B", f.border)}
}

/** Elevation. Prefer a hairline MLColor.Line border before reaching for a
 *  shadow — the reference apps separate surfaces with borders, not stacks. */
object MLElevation {
${dimGroup("E", f.elevation)}
}

/** Opacity. */
object MLOpacity {
${opacityLines}
}

/** Font weight — 400/500/600/800. There is deliberately no 700 step: the heavy
 *  UI weight was cut from 700 to 600 on 2026-08-31, and leaving a 700 here
 *  would let it creep back one Composable at a time. Reference these rather
 *  than writing FontWeight(600) inline. */
object MLWeight {
${weightLines}
}

/** Type. Sizes are sp and scale with the user's OS font-size setting; that is
 *  the whole reason these are authored separately from the web clamp(). */
object MLType {
${typeLines}
}

/** Motion — springs, not durations. A duration is not translatable to a spring
 *  and pretending otherwise was the web-shaped assumption this replaced. */
object MLMotion {
${motionLines}
}

/** Gesture thresholds. */
object MLGesture {
${gestureLines}
}

/** Dimensions fixed by a platform contract rather than chosen by us. */
object MLComponent {
${componentLines}
}

/* Insets are NOT tokens on Android — read them from the framework:
 *   WindowInsets.statusBars      (top)
 *   WindowInsets.navigationBars  (bottom)
 * The px fallbacks in tokens.json exist only for browsers without env().
 *
 * Window size classes: Medium >= ${f.layout ? T.platform.windowSize.medium.$value : "600"}dp, Expanded >= ${T.platform.windowSize.expanded.$value}dp.
 * Branch on WindowWidthSizeClass, not on a pixel breakpoint.
 */
`;

/* ── README ────────────────────────────────────────────────────────────── */

const scaleTable = ["space", "radius", "size", "icon", "avatar", "border", "elevation"]
  .map((g) => {
    const grp = f[g];
    const vals = steps(grp).map((k) => dpNum(grp[k].$android)).join(" · ");
    return `| \`${g}\` | ${steps(grp).length} | ${vals} |`;
  }).join("\n");

const md = `# Native handoff

**Generated.** Source: \`frontend/guide/tokens/tokens.json\` (v${T.meta.version}).
Regenerate with \`node scripts/build-handoff.mjs\`. Do not hand-edit
\`MusicLinkdTokens.kt\` — the web preview at \`/guide\` is generated from the
same file, and patching one side is how the two drift apart.

## What is here

| File | Contents |
|---|---|
| \`MusicLinkdTokens.kt\` | Compose \`Color\`, \`Dp\`, \`TextStyle\`, \`FontWeight\`, spring specs and shape tokens |
| \`MusicLinkdTokens.swift\` | SwiftUI \`Color\`, \`CGFloat\`, \`Font\`, \`Font.Weight\`, \`Animation.spring\` and shadow tokens |

## The two are not translations of each other

Four values genuinely differ by platform, and the generator emits each from its
own \`$android\` / \`$ios\` form rather than converting one into the other:

| | Compose | SwiftUI |
|---|---|---|
| \`size.3\` — touch minimum | **48**dp (Material) | **44**pt (Apple HIG) |
| \`radius.4\` | \`9999.dp\` | \`Capsule()\` |
| motion | \`dampingRatio\` / \`stiffness\` | \`response\` / \`dampingFraction\` |
| type | \`sp\` ramp + \`FontWeight(n)\` | Dynamic Type \`textStyle\` + named weight |

Copying the Android numbers into Swift would have produced a file that looks
right and is wrong in four places — most damagingly the touch target, which
would be 4pt over Apple's minimum on every control in the app.

## Before you build

Three families need bundling as font resources. The file currently points all
three at \`FontFamily.Default\` so it compiles on its own:

| Token | Family | Weights |
|---|---|---|
| \`Inter\` | Inter | 400 · 500 · 600 · 700 |
| \`InterTight\` | Inter Tight | 600 · 700 · 800 |
| \`UnicaOne\` | Unica One | **400 only** |

Unica One ships a single weight. Requesting 500 resolves down to 400 and is
safe; anything at 600 or above will be synthesised into a fake bold, which on
strokes this fine reads as a rendering fault rather than as emphasis.

## Scales

Every scale is capped at 4 steps and the build fails if one grows past it.

| Scale | Steps | Values (dp) |
|---|---|---|
${scaleTable}

Two scales do not pass a strict 1.5&times;-per-step contrast test, and both are
constrained by something outside taste rather than by oversight:

- **\`size\`** 32/40/48/56 — 48dp is the Material touch minimum and 44pt the iOS
  one, so an exponential ramp would put half the scale under the accessibility
  floor. It is a constant set, not a scale.
- **\`type\`** 12/16/22/32sp — the perfect fourth, the standard modular ratio for
  text. A 2&times; ramp is 12/24/48/96 with nothing usable between caption and
  headline.

## What is deliberately absent

| Absent | Why |
|---|---|
| Dark mode | Not built. Light-only is a position, not a gap. The dark elevation rule has never been decided — see \`meta.darkMode\`. |
| \`zIndex\` | Compose uses composition order. |
| Brand expression colours | Marketing only. A screen never renders them. |
| Safe-area values | Read \`WindowInsets\` from the framework; the px numbers in the source are browser fallbacks. |
| Pixel breakpoints | Branch on \`WindowWidthSizeClass\` — Medium ≥ ${T.platform.windowSize.medium.$value}dp, Expanded ≥ ${T.platform.windowSize.expanded.$value}dp. |

## Roles

Semantic role names (\`actionPrimary\`, \`surfaceCanvas\`) are documented in
\`semantic\` in the source but emit no CSS on web, because there they would be a
second name for the same value. On Android, map them in your \`ColorScheme\`:

| Role | Token | Note |
|---|---|---|
| primary | \`MLColor.Ink\` | The CTA colour is **black**, not pink. |
| onPrimary | \`MLColor.White\` | |
| secondary / accent | \`MLColor.Pink\` | Focus, selection, active tab. Never a CTA fill. |
| background | \`MLColor.Paper\` | |
| surface | \`MLColor.White\` | |
| outline | \`MLColor.Line\` | |
| error | \`MLColor.Error\` | |

One rule worth carrying over verbatim: **pink fills use \`Pink\`; pink text and
anything reversed out of pink uses \`PinkPressed\`.** \`Pink\` measures 3.56:1
against white and fails AA as text; \`PinkPressed\` measures 5.37:1. The accent
button therefore fills with \`PinkPressed\` so its white label passes.
`;

/* ── write ─────────────────────────────────────────────────────────────── */

const outputs = [[OUT_KT, kt], [OUT_SWIFT, swift], [OUT_MD, md]];

if (CHECK) {
  let stale = false;
  for (const [p, content] of outputs) {
    let current = "";
    try { current = readFileSync(p, "utf8"); } catch { current = ""; }
    if (current !== content) {
      console.error(`build-handoff: stale -> ${p.replace(ROOT + "/", "")}`);
      stale = true;
    }
  }
  if (stale) { console.error("Run: node scripts/build-handoff.mjs"); process.exit(1); }
  console.log("build-handoff: all outputs current");
} else {
  for (const [p, content] of outputs) {
    mkdirSync(dirname(p), { recursive: true });
    writeFileSync(p, content);
    console.log(`build-handoff: -> ${p.replace(ROOT + "/", "")}`);
  }
  const n = Object.keys(f.color).filter((k) => !k.startsWith("$") && !k.startsWith("brand")).length;
  console.log(`build-handoff: ${n} colours, ${steps(f.type).length} text styles, ${steps(f.motion).length} springs`);
}
