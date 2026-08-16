// Regenerates the "Logos" section of index.html from a real read of
// frontend/guide/brand/assets/ — not a hand-typed inventory. Run this
// whenever assets/ changes: `node generate.mjs`.
//
// Classification into "Logo" vs "App icon" sets is filename-based (the two
// mark-only files vs the six wordmark/lockup/badge files) — confirmed
// directly with the repo owner on 2026-08-16 rather than guessed, since
// nothing in brand/README.md documents this split. If more assets are added
// later, update APP_ICON_FILES below and re-run.
//
// Light/dark staging per card and which-color-is-real are NOT hand-typed —
// both are detected from each file's actual <path fill="..."> value(s).

import fs from "fs";
import path from "path";

const ASSETS_DIR = path.resolve("../brand/assets");
const APP_ICON_FILES = new Set(["favicon-mark.svg", "mark-grey.svg"]);

const files = fs.readdirSync(ASSETS_DIR).filter((f) => f.endsWith(".svg")).sort();

function analyzeFills(svgText) {
  // Real data pulled from the file: every fill value used, and how much
  // path data (d-attribute length, a rough proxy for visible area) is drawn
  // in each. Reported as-is rather than collapsed into a single guess.
  const matches = [...svgText.matchAll(/<path[^>]*\sd="([^"]*)"[^>]*\sfill="([^"]*)"/g)];
  const byFill = {};
  for (const [, d, fill] of matches) {
    if (fill === "none") continue;
    byFill[fill] = (byFill[fill] || 0) + d.length;
  }
  return byFill;
}

const cards = files.map((filename) => {
  const full = fs.readFileSync(path.join(ASSETS_DIR, filename), "utf8");
  const fillBreakdown = analyzeFills(full);
  const set = APP_ICON_FILES.has(filename) ? "App icon" : "Logo";
  return { filename, fillBreakdown, set, raw: full };
});

const logoCards = cards.filter((c) => c.set === "Logo");
const appIconCards = cards.filter((c) => c.set === "App icon");

// Multi-path lockups mix fill colors across individual letterforms/shapes in
// ways that can't be resolved to "this file is X color" by counting alone —
// checked directly (see logo-lockup-black.svg: 11 of 12 paths are white,
// only the largest single path is #24211C, and the filename still says
// "black" — fill counts contradict the filename, so guessing from either
// would risk being wrong). Shown on BOTH backgrounds side by side instead of
// asserting one "correct" stage, since only rendering it settles that, not
// static analysis.
function renderBothStagesCard(c) {
  const fillList = Object.entries(c.fillBreakdown)
    .sort((a, b) => b[1] - a[1])
    .map(([fill]) => fill)
    .join(", ");
  return `      <div class="logo-card logo-card-dual">
        <div class="dual-stage">
          <div class="stage light"><img src="../brand/assets/${c.filename}" alt="${c.filename} on light" /></div>
          <div class="stage dark"><img src="../brand/assets/${c.filename}" alt="${c.filename} on dark" /></div>
        </div>
        <div class="caption"><div class="name">${c.filename}</div><div class="use">Fill colors in source: <code>${fillList}</code> — shown on both, since multi-path fill mixes don't resolve to a single "correct" background from static analysis.</div></div>
      </div>`;
}

// The two App icon files are single-path, single-fill marks (verified: each
// has exactly one non-"none" fill value across its one <path>) — so unlike
// the Logo set above, there's no ambiguity here about what color the file
// "is." Inlined directly (not <img src>) with that real fill swapped for
// currentColor, so the live color picker can actually recolor them. Staged
// on whichever background its real original fill would need (white fill →
// dark stage so it's visible by default; dark fill → light stage) before
// the user drags the picker.
function renderRecolorableCard(c) {
  const fills = Object.keys(c.fillBreakdown);
  const originalFill = fills.sort((a, b) => c.fillBreakdown[b] - c.fillBreakdown[a])[0];
  const stage = /^white$|^#f/i.test(originalFill) ? "dark" : "light";
  const inlined = c.raw
    .replace(/<\?xml[^>]*\?>/, "")
    .replace(/fill="(white|#[0-9a-fA-F]{6})"/g, 'fill="currentColor"')
    .replace("<svg ", '<svg class="ml-recolor-svg" ');
  return `      <div class="logo-card">
        <div class="stage ${stage} recolor-stage">${inlined}</div>
        <div class="caption"><div class="name">${c.filename}</div><div class="use">Single fill in source (<code>${originalFill}</code>) — recolorable below, live.</div></div>
      </div>`;
}

const logoSectionHtml = logoCards.map(renderBothStagesCard).join("\n");
const appIconSectionHtml = appIconCards.map(renderRecolorableCard).join("\n");

const indexPath = "index.html";
let html = fs.readFileSync(indexPath, "utf8");

html = html.replace(
  /<!-- LOGO_SET_START -->[\s\S]*<!-- LOGO_SET_END -->/,
  `<!-- LOGO_SET_START -->\n${logoSectionHtml}\n      <!-- LOGO_SET_END -->`
);
html = html.replace(
  /<!-- APP_ICON_SET_START -->[\s\S]*<!-- APP_ICON_SET_END -->/,
  `<!-- APP_ICON_SET_START -->\n${appIconSectionHtml}\n      <!-- APP_ICON_SET_END -->`
);

fs.writeFileSync(indexPath, html);
console.log(`Regenerated Logos section from ${files.length} real files in brand/assets/:`);
console.log(`  Logo set (${logoCards.length}):`, logoCards.map((c) => c.filename).join(", "));
console.log(`  App icon set (${appIconCards.length}):`, appIconCards.map((c) => c.filename).join(", "));
