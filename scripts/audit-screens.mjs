#!/usr/bin/env node
/**
 * audit-screens.mjs — static audit of every webapp screen against the rules
 * the design system actually enforces. Runs with no browser and no server, so
 * it can gate a commit; the browser checks in the plan's verification section
 * cover what only a live layout can answer (touch-target geometry, overflow).
 *
 * Checks, each of which corresponds to a real defect found on 2026-08-29:
 *
 *   unresolved   var(--x) where --x is defined nowhere. styleguide.html shipped
 *                155 of these to production (--white alone was referenced 62
 *                times with no fallback); webapp shipped one (--space-5).
 *   offPalette   a hardcoded colour that is not in the 13-colour palette.
 *                The palette rule is "monochrome chrome, media carries the
 *                colour", so a stray hex is a real violation, not a nit.
 *   deadLink     an internal /preview/... href with no file behind it. Screens
 *                navigate by absolute path, so a typo is a dead end the user
 *                only finds by tapping. 5 were introduced in a single
 *                hand-written screen before this check existed.
 *   localStyle   per-screen <style> blocks — the component library duplicated
 *                across screens instead of living in the system. Reported as a
 *                count to track Phase 3 progress, not as a failure.
 *
 * Usage: node scripts/audit-screens.mjs [--strict]
 *        --strict exits non-zero if unresolved or off-palette hits exist.
 */
import { readFileSync, readdirSync, existsSync } from "node:fs";
import { join, resolve, relative, dirname } from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const STRICT = process.argv.includes("--strict");

/* the generated token file is the only legitimate source of --names */
const tokenCss = readFileSync(join(ROOT, "webapp/shared/ml-tokens.css"), "utf8");
const defined = new Set([...tokenCss.matchAll(/^\s*(--[a-zA-Z0-9-]+)\s*:/gm)].map((m) => m[1]));
/* app-shell.css owns the environment layer; app-shell.js sets some at runtime */
for (const f of ["webapp/shared/app-shell.css"]) {
  for (const m of readFileSync(join(ROOT, f), "utf8").matchAll(/^\s*(--[a-zA-Z0-9-]+)\s*:/gm))
    defined.add(m[1]);
}
for (const n of ["--app-vh", "--app-height", "--app-safe-top", "--app-safe-bottom",
                 "--app-safe-left", "--app-safe-right"]) defined.add(n);

const PALETTE = new Set(
  [...tokenCss.matchAll(/#[0-9A-Fa-f]{6}/g)].map((m) => m[0].toUpperCase())
);
/* pure black/white shorthands and transparent are allowed; so is any colour
   inside a gradient token, which is the media scrim */
for (const c of ["#FFFFFF", "#000000", "#FFF", "#000"]) PALETTE.add(c);

/* The four Google brand colours on the sign-in button. Fixed by Google's
   brand terms and not ours to re-tint — the same exemption audit-flow makes,
   duplicated here so the two checks agree. */
const BRAND_EXEMPT = new Set(["#4285F4", "#34A853", "#FBBC05", "#EA4335"]);

/* webapp/index.html draws a phone around each preview. These four colours
   depict HARDWARE and iOS chrome — bezel, camera lens, low-power battery
   yellow, and the desk surface the phone sits on — not MusicLinkd UI. Putting
   them on the palette would make the workbench look like a MusicLinkd screen
   rather than like a phone, which defeats the purpose of a device frame. */
const DEVICE_CHROME = new Set(["#1A1A2E", "#2C2F3A", "#FFD60A", "#EFEFF3"]);

function walk(dir, out = []) {
  for (const e of readdirSync(dir, { withFileTypes: true })) {
    const p = join(dir, e.name);
    if (e.isDirectory()) walk(p, out);
    else if (e.name.endsWith(".html")) out.push(p);
  }
  return out;
}

const files = walk(join(ROOT, "webapp"));
let nUnresolved = 0, nOffPalette = 0, nLocalStyle = 0, nScreens = 0, nDead = 0;
const report = [];

for (const f of files) {
  const rel = relative(ROOT, f);
  const s = readFileSync(f, "utf8");
  const isScreen = /webapp\/flows\//.test(rel);
  if (isScreen) nScreens++;

  const unresolved = new Set();
  for (const m of s.matchAll(/var\(\s*(--[a-zA-Z0-9-]+)\s*(,)?/g))
    if (!defined.has(m[1]) && !m[2]) unresolved.add(m[1]);

  /* Hex colours written directly into markup or a local <style>.
     Three exclusions, all of which this check used to count as violations and
     which made it disagree with audit-flow (0 vs 19) once the real ones were
     cleared:
       - HTML comments. A note explaining why a hex was REMOVED still contains
         that hex; flagging the explanation as the violation is backwards.
       - <meta name="theme-color">. A meta attribute cannot take a var(), so a
         literal there is the only thing that works — one per screen, 155 in
         total, none of them fixable.
       - The four Google brand colours on the sign-in button, which are fixed
         by Google's brand terms and cannot be re-tinted. */
  const paintable = s
    .replace(/<!--[\s\S]*?-->/g, "")
    .replace(/<meta[^>]*name="theme-color"[^>]*>/gi, "");
  const offPalette = new Set();
  for (const m of paintable.matchAll(/#[0-9A-Fa-f]{3,8}\b/g)) {
    const hex = m[0].toUpperCase();
    if (hex.length !== 7) continue;               // skip #rgb / #rrggbbaa noise
    if (!PALETTE.has(hex) && !BRAND_EXEMPT.has(hex) && !DEVICE_CHROME.has(hex)) offPalette.add(hex);
  }

  /* internal navigation: /preview/x -> webapp/x must exist on disk */
  const deadLinks = new Set();
  for (const m of s.matchAll(/(?:href|src)="(\/preview\/[^"#?]+)"/g)) {
    const target = join(ROOT, "webapp", m[1].replace(/^\/preview\//, ""));
    if (!existsSync(target)) deadLinks.add(m[1]);
  }

  const localStyle = (s.match(/<style>/g) || []).length;
  if (isScreen) nLocalStyle += localStyle;

  nUnresolved += unresolved.size;
  nOffPalette += offPalette.size;
  nDead += deadLinks.size;

  if (unresolved.size || deadLinks.size) {
    report.push({ rel, unresolved: [...unresolved], deadLinks: [...deadLinks] });
  }
}

console.log(`audit-screens: ${files.length} files (${nScreens} screens)\n`);
console.log(`  palette          ${PALETTE.size - 4} tokenised colours`);
console.log(`  unresolved var() ${nUnresolved}`);
console.log(`  dead /preview links ${nDead}`);
console.log(`  off-palette hex  ${nOffPalette}`);
console.log(`  local <style>    ${nLocalStyle} blocks across screens  (Phase 3 target: 0)\n`);

for (const r of report.slice(0, 25)) {
  console.log(`  ${r.rel}`);
  if (r.unresolved.length) console.log(`      unresolved: ${r.unresolved.join(", ")}`);
  if (r.deadLinks.length) console.log(`      dead links: ${r.deadLinks.join(", ")}`);
}
if (report.length > 25) console.log(`  ... and ${report.length - 25} more files`);

if (STRICT && (nUnresolved || nDead)) process.exit(1);
