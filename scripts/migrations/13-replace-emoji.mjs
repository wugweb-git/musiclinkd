#!/usr/bin/env node
/**
 * 13-replace-emoji.mjs — retire the 289 text glyphs standing in for icons.
 *
 *   node 13-replace-emoji.mjs <flows-dir> [--write]
 *
 * Two separate problems wearing one costume:
 *
 *   262 x  ★ / ☆   the entire rating system, as text characters. They cannot
 *                  half-fill, cannot be sized to the icon grid, render
 *                  differently on every OS, and a screen reader announces
 *                  "black star black star black star black star white star".
 *                  Replaced with a real component that solves all four.
 *
 *    27 x  🎸🎹🥁… instrument and category pictographs. Two guitars share one
 *                  glyph and "Electric Guitar" is a lightning bolt. These need
 *                  drawing, so they are REPORTED, not silently swapped — a
 *                  wrong icon is worse than an obviously missing one.
 */
import fs from "node:fs";
import path from "node:path";
const FLOWS = process.argv[2], WRITE = process.argv.includes("--write");

const PICTO = /[\u{1F300}-\u{1FAFF}\u{2600}-\u{26FF}\u{2700}-\u{27BF}]/gu;

const files = [];
(function walk(d) { for (const e of fs.readdirSync(d, { withFileTypes: true })) {
  const p = path.join(d, e.name);
  if (e.isDirectory()) walk(p); else if (e.name === "index.html") files.push(p);
}})(FLOWS);

let ratings = 0, starsGone = 0, pictos = [];

for (const f of files) {
  let h = fs.readFileSync(f, "utf8"); const before = h;
  const route = path.relative(FLOWS, path.dirname(f)).split(path.sep).join("/");

  /* ── ratings ──────────────────────────────────────────────────────────────
     Any run of stars becomes one component carrying the value as a number, so
     the number is the source of truth and the stars are a rendering of it.
     Half values work because the fill is a width, not a character. */
  h = h.replace(/>([\s]*[★☆]{2,}[\s]*)</g, (m, run) => {
    const filled = (run.match(/★/g) || []).length;
    const total  = (run.match(/[★☆]/g) || []).length;
    starsGone += total; ratings++;
    const pct = Math.round((filled / total) * 100);
    return `><span class="ml-rating" role="img" aria-label="${filled} out of ${total} stars"`
         + ` style="--ml-rating:${pct}%"><i aria-hidden="true"></i></span><`;
  });

  /* A lone ★ beside a number is a label, not a rating — swap for the icon.
     It appears on both sides: "★ 4.9" and "4.9 ★". */
  h = h.replace(/(?<=[\d.]\s{0,3})★|★(?=\s{0,3}[\d.])/g, () => {
    starsGone++;
    return `<svg class="ml-icon ml-icon--1" aria-hidden="true"><use href="/guide/system/ml-icons.svg#i-star"/></svg>`;
  });

  /* ── pictographs — report only ───────────────────────────────────────────── */
  for (const m of h.matchAll(PICTO)) {
    const ctx = h.slice(Math.max(0, m.index - 90), m.index + 40).replace(/<[^>]+>/g, " ").replace(/\s+/g, " ").trim();
    pictos.push({ route, glyph: m[0], ctx: ctx.slice(-52) });
  }

  if (h !== before && WRITE) fs.writeFileSync(f, h);
}

console.log(`rating components created   ${ratings}`);
console.log(`star characters removed     ${starsGone}`);
console.log(`pictographs still present   ${pictos.length}  (need drawing, see below)\n`);

const byGlyph = new Map();
for (const p of pictos) { if (!byGlyph.has(p.glyph)) byGlyph.set(p.glyph, []); byGlyph.get(p.glyph).push(p); }
console.log("glyphs to draw into the icon set:");
for (const [g, list] of [...byGlyph].sort((a, b) => b[1].length - a[1].length))
  console.log(`   ${g}  ${String(list.length).padStart(2)} uses   e.g. "${list[0].ctx}"`);

console.log(`\nCSS for the rating component (add to ml-core.css):

.ml-rating {
  --ml-rating: 0%;
  display: inline-block; position: relative;
  width: calc(var(--icon-1) * 5); height: var(--icon-1);
  color: var(--color-line-strong);              /* the empty track */
}
.ml-rating::before,
.ml-rating > i {
  content: ""; position: absolute; inset: 0;
  background: currentColor;
  -webkit-mask: url("/guide/system/ml-icons.svg#i-star") repeat-x left center / var(--icon-1) var(--icon-1);
          mask: url("/guide/system/ml-icons.svg#i-star") repeat-x left center / var(--icon-1) var(--icon-1);
}
.ml-rating > i {                                 /* the filled portion */
  color: var(--color-ink);
  width: var(--ml-rating);                       /* any value, including halves */
}

Why this shape: the value lives in one custom property, so a 3.5 renders as 70%
and needs no half-star character. The accessible name is written once on the
wrapper, so a screen reader says "4 out of 5 stars" instead of naming five
separate glyphs.`);
