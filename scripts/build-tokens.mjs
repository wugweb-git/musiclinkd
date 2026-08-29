#!/usr/bin/env node
/**
 * build-tokens.mjs — the single generator for MusicLinkd's design tokens.
 *
 *   frontend/guide/tokens/tokens.json   (source of truth, hand-edited)
 *        │
 *        ├─→ frontend/guide/system/ml-tokens.css   web preview, light only
 *        └─→ MusicLinkdTokens.kt / .swift          phase 5 / 6, not yet emitted
 *
 * Why this exists: before v9 the "tokens" lived in three places at once —
 * tokens.json, styleguide.html's :root, and webapp/shared/tokens.css — and
 * they had drifted apart (tokens.json still pointed the primary button at
 * pink months after the CSS had been corrected to black). Generating the CSS
 * makes that class of drift impossible: ml-tokens.css is never hand-edited.
 *
 * The build FAILS rather than warns on two conditions, both of which are the
 * failure modes this system actually had:
 *   1. token count over meta.tokenBudget.ceiling  (it had reached 248)
 *   2. any scale grown past 4 steps               (radius had reached 26)
 *
 * Usage:  node scripts/build-tokens.mjs [--check]
 *         --check verifies the committed CSS is current without writing.
 */

import { readFileSync, writeFileSync, mkdirSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const SRC = resolve(ROOT, "frontend/guide/tokens/tokens.json");
// Two destinations, one source. webapp/ is served from a different root
// (/preview) and cannot reach frontend/guide/system/ at runtime, so it gets
// its own generated copy rather than a hand-maintained near-duplicate — which
// is exactly what webapp/shared/tokens.css used to be.
const OUTS = [
  resolve(ROOT, "frontend/guide/system/ml-tokens.css"),
  resolve(ROOT, "webapp/shared/ml-tokens.css"),
];
const OUT = OUTS[0];
const CHECK = process.argv.includes("--check");

const T = JSON.parse(readFileSync(SRC, "utf8"));
const CEILING = T.meta.tokenBudget.ceiling;

/* ── helpers ───────────────────────────────────────────────────────────── */

const lines = [];
const names = new Set();
const errors = [];

function emit(name, value, comment) {
  if (value === undefined || value === null || value === "") {
    errors.push(`token --${name} would emit an empty value`);
    return;
  }
  if (names.has(name)) {
    errors.push(`duplicate token --${name}`);
    return;
  }
  names.add(name);
  lines.push(`  --${name}: ${value};${comment ? ` /* ${comment} */` : ""}`);
}

function section(title) {
  lines.push("", `  /* ${title} */`);
}

/** A scale may never exceed 4 steps — this is the rule v8 lost. */
function scale(group, label) {
  const steps = Object.keys(group).filter((k) => /^\d+$/.test(k));
  if (steps.length > 4) {
    errors.push(`scale "${label}" has ${steps.length} steps; the maximum is 4`);
  }
  return steps;
}

/** Resolve a "{foundation.color.ink}" alias to its var() reference. */
const ALIAS_MAP = {
  "foundation.color.ink": "color-ink",
  "foundation.color.paper": "color-paper",
  "foundation.color.white": "color-white",
  "foundation.color.surface": "color-surface",
  "foundation.color.line": "color-line",
  "foundation.color.muted": "color-muted",
  "foundation.color.pink": "color-pink",
};
function deref(v) {
  const m = typeof v === "string" && v.match(/^\{(.+)\}$/);
  if (!m) return v;
  const target = ALIAS_MAP[m[1]];
  if (!target) {
    errors.push(`unresolvable alias {${m[1]}}`);
    return v;
  }
  return `var(--${target})`;
}

/* ── emit ──────────────────────────────────────────────────────────────── */

const f = T.foundation;

section("Colour — monochrome chrome; media carries all saturation");
for (const [key, tok] of Object.entries(f.color)) {
  if (key.startsWith("$")) continue;
  emit(`color-${kebab(key)}`, tok.$value, short(tok.$description));
}

const productColourCount = names.size;   // colours emitted so far
section("Brand expression — marketing only, NOT the UI palette");
for (const [key, tok] of Object.entries(f.brandColor)) {
  if (key.startsWith("$")) continue;
  emit(`brand-${kebab(key)}`, tok.$value, short(tok.$description));
}
const brandColourCount = names.size - productColourCount;

section("Spacing — 4-step exponential (r=2)");
for (const k of scale(f.space, "space")) emit(`space-${k}`, f.space[k].$web);

section("Radius");
for (const k of scale(f.radius, "radius")) emit(`radius-${k}`, f.radius[k].$web);

section("Size — control heights; 3 = 48dp touch minimum, 4 = 56dp comfortable");
for (const k of scale(f.size, "size")) emit(`size-${k}`, f.size[k].$web);

section("Opacity");
for (const k of scale(f.opacity, "opacity")) emit(`opacity-${k}`, f.opacity[k].$value);

section("Type — fluid clamp() on web; native uses the sp/Dynamic Type ramp in tokens.json");
const typeClasses = [];
for (const k of scale(f.type, "type")) {
  const w = f.type[k].$web;
  const min = parseFloat(w.min);
  const max = parseFloat(w.max);
  // scales continuously between a 360px and 1440px viewport (1080px span)
  emit(
    `type-${k}`,
    `clamp(${min}px, calc(${min}px + ${max - min} * (100vw - 360px) / 1080), ${max}px)`
  );
  // The other three axes are NOT separate custom properties — that would be
  // 16 tokens for a 4-step scale. They ship as utility classes generated from
  // the same source, so nothing can drift and nothing is hand-typed.
  typeClasses.push(
    `.ml-type-${k} {\n` +
      `  font-family: var(--font-${w.family});\n` +
      `  font-size: var(--type-${k});\n` +
      `  font-weight: ${w.weight};\n` +
      `  line-height: ${w.lineHeight};\n` +
      `  letter-spacing: ${w.letterSpacing === "0" ? "0" : w.letterSpacing};\n` +
      `}`
  );
}

section("Font families");
for (const [key, tok] of Object.entries(f.font)) {
  if (key.startsWith("$")) continue;
  emit(`font-${key}`, tok.$value);
}

section("Elevation — 2 levels only; prefer a hairline border to a shadow");
for (const k of scale(f.elevation, "elevation")) emit(`elevation-${k}`, f.elevation[k].$value);

section("Motion — web duration + easing; native springs live in tokens.json");
for (const k of scale(f.motion, "motion")) emit(`duration-${k}`, f.motion[k].$web.duration);
// Easings are a small named set, not a 4-step scale: steps 1 and 2 share the
// same curve, so emitting one per step would duplicate a value under two names.
const EASE_NAMES = { 1: "standard", 2: "standard", 3: "enter", 4: "spring" };
const seenEase = new Set();
for (const k of scale(f.motion, "motion")) {
  const name = EASE_NAMES[k];
  if (seenEase.has(name)) continue;
  seenEase.add(name);
  emit(`ease-${name}`, f.motion[k].$web.easing);
}

section("Z-index — web only");
for (const k of scale(f.zIndex, "zIndex")) emit(`z-${k}`, f.zIndex[k].$value);

section("Gesture thresholds");
for (const [key, tok] of Object.entries(T.interaction)) {
  if (key.startsWith("$")) continue;
  emit(`gesture-${kebab(key)}`, tok.$web);
}

// T.semantic.action/surface/text intentionally emit NOTHING — see the
// $emitCss:false note in tokens.json. They are 1:1 aliases of foundation
// colours and exist for the Compose/SwiftUI role names only. On web,
// --color-ink IS the primary action and the primary text.
if (T.semantic.$emitCss !== false) {
  errors.push("semantic block is marked emitting; that reintroduces alias tokens");
}

section("Media — the palette rule, made operational");
emit("media-scrim", T.semantic.media.scrim.$value);
emit("media-aspect-square", T.semantic.media.aspectSquare.$value);
emit("media-aspect-wide", T.semantic.media.aspectWide.$value);

section("Safe area — edge-to-edge is mandatory, not optional");
for (const [key, tok] of Object.entries(T.platform.safeArea)) {
  if (key.startsWith("$")) continue;
  emit(`safe-${key}`, tok.$web);
}

/* ── enforce ───────────────────────────────────────────────────────────── */

if (names.size > CEILING) {
  errors.push(
    `token count ${names.size} exceeds the ceiling of ${CEILING}. ` +
      `Adding a token means removing one — see meta.note.`
  );
}
if (errors.length) {
  console.error("build-tokens: FAILED\n");
  for (const e of errors) console.error("  ✗ " + e);
  process.exit(1);
}

/* ── write ─────────────────────────────────────────────────────────────── */

const css = `/**
 * ml-tokens.css — GENERATED. Do not edit.
 *
 * Source:    frontend/guide/tokens/tokens.json  (v${T.meta.version})
 * Regenerate: node scripts/build-tokens.mjs
 *
 * ${names.size} tokens, ceiling ${CEILING}. The previous hand-maintained system
 * had reached 248 custom properties in a single :root, of which every value
 * past the 4-step core was an exact duplicate under a second name.
 *
 * Palette rule: the chrome is monochrome, the media carries all the colour.
 * Light only — dark mode is deliberately not built.
 */
:root {
${lines.join("\n")}
}

${typeClasses.join("\n")}

/* Media at or above ${T.platform.windowSize.medium.$web} is Material's Medium window size class;
   ${T.platform.windowSize.expanded.$web} is Expanded. These are the only two breakpoints —
   they match what Compose branches on, so web and native stay in agreement. */
`;

if (CHECK) {
  let stale = false;
  for (const out of OUTS) {
    let current = "";
    try { current = readFileSync(out, "utf8"); } catch { current = ""; }
    if (current !== css) {
      console.error(`build-tokens: stale -> ${out.replace(ROOT + "/", "")}`);
      stale = true;
    }
  }
  if (stale) {
    console.error("Run: node scripts/build-tokens.mjs");
    process.exit(1);
  }
  console.log(`build-tokens: all outputs current (${names.size} tokens)`);
} else {
  for (const out of OUTS) {
    mkdirSync(dirname(out), { recursive: true });
    writeFileSync(out, css);
    console.log(`build-tokens: ${String(names.size).padStart(3)} tokens -> ${out.replace(ROOT + "/", "")}`);
  }
  console.log(`build-tokens: ${names.size} tokens (ceiling ${CEILING}) — ` +
    `${productColourCount} product colours, ${brandColourCount} brand-expression colours`);
}

/* ── util ──────────────────────────────────────────────────────────────── */
function kebab(s) {
  return s.replace(/([a-z0-9])([A-Z])/g, "$1-$2").toLowerCase();
}
function short(d) {
  if (!d) return "";
  const first = d.split(/(?<=\.)\s/)[0];
  return first.length > 72 ? "" : first.replace(/\.$/, "");
}
