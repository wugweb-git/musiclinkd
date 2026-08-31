/**
 * audit-tokens.mjs — the other half of "define and map the tokens".
 *
 * build-tokens.mjs proves every token is DEFINED once and stays inside the
 * budget. It says nothing about whether anything USES them, or whether the
 * stylesheets still carry literals a token already exists for. Both were true
 * and invisible: --media-aspect-card was defined and referenced nowhere while
 * .ml-media offered only --square and --wide, and ml-widgets.css still held
 * 140 literal px values.
 *
 * Reports three things, none of them hand-maintained:
 *   orphans    a token nothing references — either wire it up or delete it
 *   literals   a hardcoded value in the stylesheets that a token already names
 *   offPalette a colour that is not in the palette at all
 *
 * Usage: node scripts/audit-tokens.mjs [--strict]
 */

import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = join(dirname(fileURLToPath(import.meta.url)), "..");
const SYS = join(ROOT, "frontend/guide/system");
const STRICT = process.argv.includes("--strict");

const tokenCss = readFileSync(join(SYS, "ml-tokens.css"), "utf8");

/** name -> value, for every token the generator emits. */
const TOKENS = new Map(
  [...tokenCss.matchAll(/^\s*(--[a-z0-9-]+):\s*([^;]+);/gm)].map((m) => [m[1], m[2].trim()])
);

/* The files that consume tokens. The guide generator counts: its inline
   styles are shipped markup, not build scaffolding.

   The NATIVE consumer is not optional here. This system targets Compose and
   SwiftUI, so a whole family can be legitimately unused on web and fully used
   on Android — --gesture-*, --component-*, --chrome-* and --bp-* are exactly
   that. Auditing web files alone reported 36 dead tokens when most were doing
   their job in MusicLinkdTokens.kt; splitting the buckets is the difference
   between "delete this" and "this is native-only, as designed". */
const WEB_CONSUMERS = ["frontend/guide/system/ml-core.css",
                       "frontend/guide/system/ml-widgets.css",
                       "scripts/build-guide.mjs"];
const NATIVE_CONSUMERS = ["docs/handoff/MusicLinkdTokens.kt"];

const read = (f) => { try { return readFileSync(join(ROOT, f), "utf8"); } catch { return ""; } };
const webSrc = WEB_CONSUMERS.map(read).join("\n") + "\n" + tokenCss;
const nativeSrc = NATIVE_CONSUMERS.map(read).join("\n");

/* ── orphans ───────────────────────────────────────────────────────────── */
// A token may also be referenced by another token (--color-scrim inside a
// gradient), which still counts as used.
const usedOnWeb = (t) => new RegExp(`var\\(\\s*${t}\\b`).test(webSrc);
/* Kotlin drops the family prefix and PascalCases the tail:
     --gesture-swipe-threshold -> MLGesture.SwipeThreshold
     --component-appbar-search -> MLComponent.AppbarSearch
     --color-brand-white       -> MLColor.BrandWhite
   Numbered scales get a letter instead, one per family:
     --icon-3 -> MLIcon.I3   --avatar-2 -> MLAvatar.A2
     --border-1 -> MLBorder.B1   --elevation-4 -> MLElevation.E4
   Matching the whole CSS name against the Kotlin found nothing, which is what
   made this report 36 orphans on its first run. */
const SCALE_LETTER = { icon: "I", avatar: "A", border: "B", elevation: "E",
                       space: "S", radius: "R", size: "Size", type: "T",
                       opacity: "O", duration: "D", weight: "W", z: "Z" };
const pascal = (parts) => parts.map((w) => w[0].toUpperCase() + w.slice(1)).join("");

function nativeNames(t) {
  const parts = t.replace(/^--/, "").split("-");
  const fam = parts[0];
  const tail = parts.slice(1);
  const out = [pascal(parts), pascal(tail)];
  if (tail.length === 1 && /^\d+$/.test(tail[0]) && SCALE_LETTER[fam])
    out.push(SCALE_LETTER[fam] + tail[0]);
  return out.filter(Boolean);
}
/* Word-boundary match, so `Ink` does not also satisfy `InkPressed`. */
const usedOnNative = (t) =>
  nativeNames(t).some((n) => new RegExp(`\\b${n}\\b`).test(nativeSrc));

const orphans = [];       // dead everywhere
const nativeOnly = [];    // unused on web, alive on native — by design
for (const t of TOKENS.keys()) {
  if (usedOnWeb(t)) continue;
  (usedOnNative(t) ? nativeOnly : orphans).push(t);
}

/* ── literals a token already names ────────────────────────────────────── */
// Only scalar, unambiguous families. Type sizes are clamp() and spacing is
// contextual, so matching those on value alone would cry wolf.
const SCALAR_FAMILIES = ["space", "radius", "size", "icon", "avatar", "border"];
const byValue = new Map();
for (const [name, value] of TOKENS) {
  const fam = name.replace(/^--/, "").split("-")[0];
  if (!SCALAR_FAMILIES.includes(fam)) continue;
  if (!/^\d+px$/.test(value)) continue;
  if (!byValue.has(value)) byValue.set(value, []);
  byValue.get(value).push(name);
}

const PALETTE = new Set([...TOKENS.entries()]
  .filter(([n]) => n.startsWith("--color-"))
  .map(([, v]) => v.toLowerCase()));

const literals = [];
const offPalette = [];
for (const f of ["frontend/guide/system/ml-core.css", "frontend/guide/system/ml-widgets.css"]) {
  const src = readFileSync(join(ROOT, f), "utf8");
  /* Block-comment state has to be tracked across lines, not sniffed per line.
     ml-core.css documents the very values this audit looks for — the status
     colours are explained in prose with their hexes inline — so a per-line
     "starts with *" test reported all 8 of its own comments as violations. */
  let inComment = false;
  src.split("\n").forEach((line, i) => {
    const opens = (line.match(/\/\*/g) || []).length;
    const closes = (line.match(/\*\//g) || []).length;
    const wasInComment = inComment;
    if (opens > closes) inComment = true;
    else if (closes > opens) inComment = false;
    if (wasInComment || /^\s*(\*|\/\*|\/\/)/.test(line)) return;
    // strip any single-line comment before scanning the declaration
    line = line.replace(/\/\*[\s\S]*?\*\//g, "");
    for (const m of line.matchAll(/:\s*[^;]*?(?<![\w.-])(\d+px)\b/g)) {
      const hit = byValue.get(m[1]);
      if (hit) literals.push({ file: f, line: i + 1, value: m[1], token: hit[0], src: line.trim().slice(0, 90) });
    }
    for (const m of line.matchAll(/#[0-9a-fA-F]{3,8}\b/g)) {
      if (!PALETTE.has(m[0].toLowerCase()))
        offPalette.push({ file: f, line: i + 1, value: m[0], src: line.trim().slice(0, 90) });
    }
  });
}

/* ── report ────────────────────────────────────────────────────────────── */
console.log(`tokens defined : ${TOKENS.size}`);
console.log(`native-only    : ${nativeOnly.length}  (unused on web, live in MusicLinkdTokens.kt — by design)`);
for (const o of nativeOnly) console.log(`  ${o.padEnd(30)} = ${TOKENS.get(o)}`);
console.log(`\nDEAD everywhere : ${orphans.length}`);
for (const o of orphans) console.log(`  ${o.padEnd(30)} = ${TOKENS.get(o)}`);
console.log(`\nliterals a token already names : ${literals.length}`);
for (const l of literals.slice(0, 40))
  console.log(`  ${l.file.split("/").pop()}:${String(l.line).padEnd(5)} ${l.value} -> var(${l.token})   ${l.src}`);
if (literals.length > 40) console.log(`  … ${literals.length - 40} more`);
console.log(`\noff-palette colours : ${offPalette.length}`);
for (const c of offPalette) console.log(`  ${c.file.split("/").pop()}:${c.line} ${c.value}   ${c.src}`);

if (STRICT && (orphans.length || literals.length || offPalette.length)) process.exit(1);
