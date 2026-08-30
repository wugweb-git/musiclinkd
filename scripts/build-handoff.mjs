#!/usr/bin/env node
/**
 * build-handoff.mjs — emits the Android handoff from tokens.json.
 *
 *   frontend/guide/tokens/tokens.json   (source of truth, hand-edited)
 *        └─→ docs/handoff/MusicLinkdTokens.kt    Compose tokens
 *        └─→ docs/handoff/README.md              how to consume them
 *
 * Why this exists: tokens.json has carried explicit $android forms on every
 * token since v9 — sp ramps, spring physics, Dp values — and nothing consumed
 * them. Dual authoring that no build reads is just a longer JSON file. This
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

const typeLines = steps(f.type).map((k) => {
  const a = f.type[k].$android;
  const doc = f.type[k].$description;
  return `    /** ${doc} — Material ${a.style} */
    val Type${k} = TextStyle(
        fontFamily = ${a.family === "InterTight" ? "InterTight" : a.family === "UnicaOne" ? "UnicaOne" : "Inter"},
        fontSize = ${sp(a.size)}.sp,
        lineHeight = ${sp(a.lineHeight)}.sp,
        fontWeight = FontWeight(${a.weight}),
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

const md = `# Android handoff

**Generated.** Source: \`frontend/guide/tokens/tokens.json\` (v${T.meta.version}).
Regenerate with \`node scripts/build-handoff.mjs\`. Do not hand-edit
\`MusicLinkdTokens.kt\` — the web preview at \`/guide\` is generated from the
same file, and patching one side is how the two drift apart.

## What is here

| File | Contents |
|---|---|
| \`MusicLinkdTokens.kt\` | Compose \`Color\`, \`Dp\`, \`TextStyle\`, spring specs and shape tokens |

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

const outputs = [[OUT_KT, kt], [OUT_MD, md]];

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
