#!/usr/bin/env node
/**
 * build-icons-lucide.mjs — vendor the Lucide icon set as plain SVG.
 *
 * MusicLinkd ships TWO icon sets, on the brand owner's instruction (2026-08-31):
 *
 *   gestalt  423 icons, FILLED paths, drawn on 16- and 24-unit grids.
 *            The product set — what the app screens use.
 *   lucide   2007 icons, STROKED outlines on a 24 grid, 2px round caps.
 *            The breadth set — everything the product set has no glyph for.
 *
 * They are not interchangeable and must never be mixed inside one control:
 * a filled glyph beside a stroked one at the same size reads as two different
 * weights, which is the exact instability the icon-scale cleanup fixed for the
 * 16-vs-24 grids. Pick a set per surface, not per icon.
 *
 * Lucide was already an admin dependency (lucide-react, ISC) and styleguide.html
 * specified it — "Lucide icons, 1.75px stroke" — before the v9 rebuild replaced
 * it wholesale with Gestalt. This restores it as a second set rather than a
 * replacement.
 *
 * Reads the installed package rather than the network, so the build is
 * reproducible and offline. Source of truth is admin/node_modules/lucide-react;
 * re-run after upgrading it.
 *
 * Usage: node scripts/build-icons-lucide.mjs
 */

import { readFileSync, readdirSync, writeFileSync, mkdirSync, rmSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = join(dirname(fileURLToPath(import.meta.url)), "..");
const SRC = join(ROOT, "admin/node_modules/lucide-react/dist/esm/icons");
const OUT = join(ROOT, "frontend/guide/icons-lucide");

/* Lucide stores each icon as [tag, attrs] pairs rather than a path string,
   because several are built from rect/circle/line primitives, not just <path>.
   Flattening them to a path would change the geometry, so the primitives are
   emitted as-is. */
function toSvg(nodes) {
  return nodes
    .map(([tag, attrs]) => {
      const a = Object.entries(attrs)
        .filter(([k]) => k !== "key") // React reconciliation key, meaningless in SVG
        .map(([k, v]) => `${k.replace(/[A-Z]/g, (c) => "-" + c.toLowerCase())}="${v}"`)
        .join(" ");
      return `<${tag} ${a}/>`;
    })
    .join("");
}

/** Parse the __iconNode array out of the module without executing it.
 *
 *  Two shapes have to be handled, and missing either silently drops icons —
 *  the first version of this parser wrote 1653 of 2007 and skipped 354 (18%)
 *  without failing, which is worse than not running at all:
 *
 *  1. Prettier wraps entries with a long `d` across multiple lines, so the
 *     tag and its attribute block are not on one line. `[\s\S]` inside the
 *     object, not `[^}]` on a single line.
 *  2. ~200 files are pure re-exports — `export { default } from
 *     './alarm-clock-check.mjs'` — which are ALIAS NAMES for another icon,
 *     not icons of their own. Those are followed, so the alias name resolves
 *     to the same artwork rather than vanishing.
 */
function parseIconNode(src, resolve, depth = 0) {
  const alias = src.match(/export \{ default \} from '\.\/([\w-]+)\.mjs'/);
  if (alias) {
    if (depth > 4) return null; // cycle guard
    const target = resolve(alias[1]);
    return target ? parseIconNode(target, resolve, depth + 1) : null;
  }
  const m = src.match(/const __iconNode = \[([\s\S]*?)\n\];/);
  if (!m) return null;
  const nodes = [];
  for (const entry of m[1].matchAll(/\[\s*"(\w+)"\s*,\s*\{([\s\S]*?)\}\s*\]/g)) {
    const attrs = {};
    for (const kv of entry[2].matchAll(/([A-Za-z0-9-]+):\s*"([^"]*)"/g)) attrs[kv[1]] = kv[2];
    nodes.push([entry[1], attrs]);
  }
  return nodes.length ? nodes : null;
}

let license = "";
try {
  license = readFileSync(join(ROOT, "admin/node_modules/lucide-react/LICENSE"), "utf8");
} catch {
  console.error("build-icons-lucide: LICENSE not found — refusing to vendor without it");
  process.exit(1);
}

const version = JSON.parse(
  readFileSync(join(ROOT, "admin/node_modules/lucide-react/package.json"), "utf8")
).version;

rmSync(OUT, { recursive: true, force: true });
mkdirSync(OUT, { recursive: true });

const files = readdirSync(SRC).filter((f) => f.endsWith(".mjs") && !f.endsWith(".mjs.map"));
let written = 0;
const skipped = [];

const readIcon = (n) => { try { return readFileSync(join(SRC, `${n}.mjs`), "utf8"); } catch { return null; } };

for (const f of files) {
  const name = f.replace(/\.mjs$/, "");
  const nodes = parseIconNode(readFileSync(join(SRC, f), "utf8"), readIcon);
  if (!nodes) { skipped.push(name); continue; }
  /* width/height are omitted deliberately: every consumer sets its own size
     from --icon-*, and a hardcoded width with no viewBox is what silently
     CROPPED the Gestalt glyphs when they were resized. The viewBox is always
     present here, so scaling is safe at any size. */
  const svg =
    `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" ` +
    `stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">` +
    toSvg(nodes) +
    `</svg>\n`;
  writeFileSync(join(OUT, `${name}.svg`), svg);
  written++;
}

writeFileSync(join(OUT, "LICENSE"), license);
writeFileSync(
  join(OUT, "README.md"),
  `# Lucide — vendored icon set\n\n` +
    `GENERATED by \`scripts/build-icons-lucide.mjs\`. Do not hand-edit.\n\n` +
    `- **${written} icons**, extracted from \`lucide-react@${version}\` (ISC — see LICENSE).\n` +
    `- Stroked outlines on a 24 grid, \`stroke-width="2"\`, round caps and joins.\n` +
    `- \`width\`/\`height\` are omitted on purpose; the consumer sets size from \`--icon-*\`.\n` +
    `  The \`viewBox\` is always present, so scaling never crops.\n\n` +
    `## The two sets\n\n` +
    `This is the **breadth** set. \`musiclinkd-system/icons/\` is the **product**\n` +
    `set — 423 filled Gestalt glyphs, which is what app screens use.\n\n` +
    `Do not mix them inside one control or one toolbar. A filled glyph next to a\n` +
    `stroked one at the same size reads as two different weights.\n`
);

console.log(`build-icons-lucide: ${written} icons -> frontend/guide/icons-lucide (lucide-react@${version}, ISC)`);
/* FAIL rather than warn. A silent skip is how the first run shipped 1653 of
   2007 and reported success; a set with 18% missing looks identical to a
   complete one until someone reaches for a glyph that is not there. */
if (skipped.length) {
  console.error(`build-icons-lucide: FAILED — ${skipped.length} of ${files.length} did not parse`);
  console.error(`  ${skipped.slice(0, 12).join(", ")}${skipped.length > 12 ? " …" : ""}`);
  process.exit(1);
}
