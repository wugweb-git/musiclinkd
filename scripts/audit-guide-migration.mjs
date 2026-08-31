/**
 * audit-guide-migration.mjs — what the old styleguide has that the new guide doesn't.
 *
 * The old /guide/styleguide.html was superseded by the generated
 * /guide/index.html (ml-tokens + ml-core + ml-widgets). The migration was
 * claimed complete more than once and was not. This script exists so the
 * claim is never a claim again: it reads both files and reports the delta.
 *
 * It does NOT judge fidelity — a section that exists in both may still be a
 * shabby port. It only answers "does a home for this exist at all yet?".
 * Fidelity is tracked by hand in docs/guide-migration.md, keyed on these ids.
 *
 * Usage: node scripts/audit-guide-migration.mjs [--json]
 */

import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = join(dirname(fileURLToPath(import.meta.url)), "..");
const OLD = join(ROOT, "frontend/guide/styleguide.html");
const NEW = join(ROOT, "frontend/guide/index.html");

/** Every `id="..."` on a heading/section/article, with its visible label. */
function sections(html) {
  const out = new Map();
  const re = /<(section|article|div|h[1-6])\b([^>]*\bid="([A-Za-z0-9_-]+)"[^>]*)>/g;
  let m;
  while ((m = re.exec(html))) {
    const id = m[3];
    if (out.has(id)) continue;
    // Nearest heading text after the tag, for a human-readable label.
    const after = html.slice(m.index, m.index + 1200);
    const h = after.match(/<h[1-6][^>]*>([\s\S]{0,120}?)<\/h[1-6]>/);
    const label = h ? h[1].replace(/<[^>]*>/g, "").replace(/\s+/g, " ").trim() : "";
    out.set(id, label);
  }
  return out;
}

/** Class names actually used for demos, e.g. ml-chip, sg-card — the real surface. */
function classes(html, prefix) {
  const out = new Set();
  const re = new RegExp(`\\b${prefix}[a-z0-9-]*(?:__[a-z0-9-]+)?`, "g");
  for (const c of html.match(re) || []) out.add(c.split("--")[0]);
  return out;
}

const oldHtml = readFileSync(OLD, "utf8");
const newHtml = readFileSync(NEW, "utf8");

const oldSec = sections(oldHtml);
const newSec = sections(newHtml);

// Old ids are prefixed by area (f- foundations, c- components, na- native,
// vd- visual design, p- pages, ml- musiclinkd blocks…). New ids are c-*.
// Match on the normalised tail so `na-otp` finds `c-otp`.
const norm = (id) => id.replace(/^(f|c|c2|na|vd|ml|p|p2|pat|bl|adv|util|sc|cd|pr|db|mp|fm|lay|sr|cr)-/, "").replace(/-/g, "");
const newByNorm = new Map([...newSec.keys()].map((id) => [norm(id), id]));

const missing = [];
const present = [];
for (const [id, label] of oldSec) {
  // Skip runtime/demo plumbing ids — they're wiring, not catalogue entries.
  if (/^(toast|sb|ov|content|next-btn|prev-btn|current-section-name|global-search|mob|bs|sideSheet|deckW|tr1|tr2|chatDrawer|dropdownDemo|credAct|bf)/i.test(id)) continue;
  const hit = newByNorm.get(norm(id));
  (hit ? present : missing).push({ id, label, mappedTo: hit || null });
}

const oldClasses = classes(oldHtml, "sg-");
const newClasses = classes(newHtml, "ml-");

const report = {
  old: { file: "frontend/guide/styleguide.html", sections: oldSec.size, sgClasses: oldClasses.size },
  new: { file: "frontend/guide/index.html", sections: newSec.size, mlClasses: newClasses.size },
  migrated: present.length,
  missing: missing.length,
  missingSections: missing.sort((a, b) => a.id.localeCompare(b.id)),
  // Stock imagery must not come across; local /assets/img must.
  stockRefsInOld: (oldHtml.match(/https:\/\/[^"']*(unsplash|pexels|placehold|picsum|randomuser|flaticon)[^"']*/g) || []).length,
  stockRefsInNew: (newHtml.match(/https:\/\/[^"']*(unsplash|pexels|placehold|picsum|randomuser|flaticon)[^"']*/g) || []).length,
  localImagesInOld: [...new Set(oldHtml.match(/\/assets\/img\/[^"')\s]+/g) || [])],
  localImagesInNew: [...new Set(newHtml.match(/\/assets\/img\/[^"')\s]+/g) || [])],
};
report.localImagesDroppedInMigration = report.localImagesInOld.filter((p) => !report.localImagesInNew.includes(p));

if (process.argv.includes("--json")) {
  console.log(JSON.stringify(report, null, 2));
} else {
  console.log(`old styleguide : ${report.old.sections} sections`);
  console.log(`new guide      : ${report.new.sections} sections`);
  console.log(`migrated       : ${report.migrated}`);
  console.log(`MISSING        : ${report.missing}\n`);
  for (const m of report.missingSections) console.log(`  ${m.id.padEnd(28)} ${m.label}`);
  console.log(`\nstock image refs   old=${report.stockRefsInOld}  new=${report.stockRefsInNew}`);
  console.log(`local /assets/img  old=${report.localImagesInOld.length}  new=${report.localImagesInNew.length}`);
  if (report.localImagesDroppedInMigration.length) {
    console.log(`\nlocal images dropped in migration (${report.localImagesDroppedInMigration.length}):`);
    for (const p of report.localImagesDroppedInMigration) console.log(`  ${p}`);
  }
}
