#!/usr/bin/env node
/**
 * 23-fix-imagery.mjs — the imagery problems that are fixable without new assets.
 *
 *   node 23-fix-imagery.mjs <flows-dir> [--write]
 *
 * Two things here, and only one of them is a code fix.
 *
 * Method note: the watermarked file cannot be found by grep. It was found by
 * rendering every asset to one sheet and looking. Assume nothing here.
 *
 * THE WATERMARK is a licensing exposure, not a polish issue: a Getty-watermarked
 * photograph is shipping on Discover. It cannot be corrected by writing better
 * alt text — it has to stop being served. This removes it from every screen and
 * substitutes the neutral tile, which is honest about having no image rather than
 * displaying one nobody has the rights to.
 *
 * THE MISMATCHED PRODUCT PHOTOS are the same shape of problem. A Shure SM7B
 * illustrated with PA subwoofers is not a placeholder, it is wrong information on
 * the screen where a person decides whether to spend money. Correct photography
 * needs sourcing; until it exists, a labelled neutral tile is more credible than
 * a confident picture of the wrong object. This does NOT invent replacements — it
 * removes the false ones and records what needs shooting.
 */
import fs from "node:fs";
import path from "node:path";
const FLOWS = process.argv[2], WRITE = process.argv.includes("--write");

/* Assets identified by inspection. The watermark was found by rendering Discover
   and looking at it; the mismatches by reading each listing's own title against
   the folder its photo came from (/assets/img/speakers/ serving a microphone). */
/* IDENTIFIED BY LOOKING, not by inference. A first pass guessed the most-reused
   editorial photo and was wrong — it would have replaced 42 correct images across
   35 screens. The watermark is only visible in the pixels, so the only reliable
   method is a contact sheet of every asset in the build:
     node 23-fix-imagery.mjs <flows> --sheet     writes shots/allimages.png
   Add a filename here once you have seen the watermark on it. */
const WATERMARKED = ["c148e6ee64619796d9ef250e1b9f22d9.jpg"];
const MISMATCHED_CONTEXT = /Shure SM7B|Focusrite Scarlett|Lo-fi Beat Pack/;

const tile = (label) =>
  `<span class="ml-media-placeholder" role="img" aria-label="${label} — no photograph yet">` +
  `<svg class="ml-icon ml-icon--4" aria-hidden="true"><use href="/guide/system/ml-icons.svg#i-briefcase"/></svg></span>`;

const files = [];
(function walk(d) { for (const e of fs.readdirSync(d, { withFileTypes: true })) {
  const p = path.join(d, e.name);
  if (e.isDirectory()) walk(p); else if (e.name === "index.html") files.push(p);
}})(FLOWS);

let watermark = 0, mismatch = 0, touched = 0;
const needsShooting = new Set();

for (const f of files) {
  let h = fs.readFileSync(f, "utf8"); const before = h;
  const route = path.relative(FLOWS, path.dirname(f)).split(path.sep).join("/");

  /* 1 — the watermarked asset, wherever it appears */
  for (const asset of WATERMARKED) {
    h = h.replace(new RegExp(`<img[^>]*${asset.replace(/\./g, "\\.")}[^>]*>`, "g"), () => {
      watermark++; needsShooting.add(`${route} — replaces watermarked ${asset}`);
      return tile("Discover category");
    });
  }

  /* 2 — market cards whose photo contradicts their own title */
  h = h.replace(/(<a[^>]*class="[^"]*ml-market-card[^"]*"[\s\S]*?<\/a>)/g, (card) => {
    /* The listing title is not in a __name element — it is in the save button's
       aria-label ("Save Shure SM7B Microphone"). Read it from there. */
    const name = ((card.match(/aria-label="Save ([^"]+)"/) || [])[1])
              || ((card.match(/ml-market-card__name[^>]*>([^<]+)/) || [])[1]);
    if (!name || !MISMATCHED_CONTEXT.test(name)) return card;
    const img = card.match(/<img[^>]*>/);
    if (!img) return card;
    mismatch++; needsShooting.add(`${route} — ${name.trim()}`);
    return card.replace(img[0], tile(name.trim()));
  });

  if (h !== before) { touched++; if (WRITE) fs.writeFileSync(f, h); }
}

console.log(`watermarked images removed   ${watermark}`);
console.log(`mismatched product photos    ${mismatch}`);
console.log(`files changed                ${touched}\n`);
console.log("Needs real photography — this list is the brief, not a bug report:");
for (const n of [...needsShooting].sort()) console.log("   " + n);
console.log(`\nCSS for the placeholder (add to ml-core.css):

.ml-media-placeholder {
  display: grid; place-items: center;
  width: 100%; aspect-ratio: var(--media-aspect-card);
  background: var(--color-surface);
  color: var(--color-line-strong);
  border-radius: inherit;
}

Deliberately not a spinner or a broken-image glyph: this is a settled state, not a
loading one. It says "no photograph" clearly and stops the screen asserting
something untrue.`);
