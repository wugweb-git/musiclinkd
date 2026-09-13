#!/usr/bin/env node
/**
 * 24-type-renumber.mjs — the type renumber `05-codemod-css.mjs` was held for.
 *
 *   node 24-type-renumber.mjs <flows-dir> [--write]
 *
 * README.md's "Not applied, and why" called this "churn with no destination"
 * while the 4-step ceiling stood. On 2026-09-11 the brand owner lifted it and
 * `foundation.type` grew from 4 steps to 7 (12/14/16/22/28/32/40px). Steps 2,
 * 3 and 4 moved to make room: old `--type-2` (16-18px body) is now step 3,
 * old `--type-3` (22-28px section headings) is now step 4, and old `--type-4`
 * (32-44px display) is now step 6. New steps 2 (14px), 5 (28-36px) and 7
 * (40-56px hero) fill in around them — see tokens.json foundation.type.
 *
 * This renumbers every existing `--type-N` / `ml-type-N` reference so each
 * one keeps pointing at the SAME rendered pixel size it always did. It does
 * not introduce any use of the three new steps — that is a design decision
 * for whoever wants 14px, 28px or the hero size on a specific screen, made
 * screen by screen, not by a codemod.
 *
 * Renumbering 2→3→4→6 the naive way (three sequential find/replace passes)
 * corrupts itself: pass one's output (`--type-3`) is pass two's input. This
 * remaps all three in one regex pass instead, so no value is ever renumbered
 * twice. A marker comment records that a file has been migrated, because
 * unlike this repo's other codemods, running this fix twice is NOT
 * detectably a no-op from the bytes alone — after migration, a file's own
 * `--type-3` is indistinguishable from an original `--type-3` that a second,
 * unmarked run would wrongly bump to `--type-4`. The marker is what makes
 * re-running safe.
 */
import fs from "node:fs";
import path from "node:path";

const FLOWS = process.argv[2], WRITE = process.argv.includes("--write");
if (!FLOWS) {
  console.error("usage: node 24-type-renumber.mjs <flows-dir> [--write]");
  process.exit(1);
}

const MARKER = "<!-- 24-type-renumber: applied 2026-09-11 -->";
const MAP = { 2: "3", 3: "4", 4: "6" };
const PATTERN = /(--type-|ml-type-)([234])(?!\d)/g;

const files = [];
(function walk(d) {
  for (const e of fs.readdirSync(d, { withFileTypes: true })) {
    const p = path.join(d, e.name);
    if (e.isDirectory()) walk(p);
    else if (e.name === "index.html") files.push(p);
  }
})(FLOWS);

let touched = 0, replacements = 0, alreadyDone = 0;
const perFile = [];

for (const f of files) {
  const before = fs.readFileSync(f, "utf8");
  if (before.includes(MARKER)) { alreadyDone++; continue; }

  let n = 0;
  const after = before.replace(PATTERN, (_, prefix, digit) => {
    n++;
    return prefix + MAP[digit];
  });
  if (n === 0) continue;

  touched++;
  replacements += n;
  perFile.push(`  ${n.toString().padStart(3)}  ${path.relative(FLOWS, f)}`);

  if (WRITE) {
    const stamped = after.includes("<!DOCTYPE")
      ? after.replace(/(<!DOCTYPE[^>]*>)/i, `$1\n${MARKER}`)
      : `${MARKER}\n${after}`;
    fs.writeFileSync(f, stamped);
  }
}

console.log(`files scanned            ${files.length}`);
console.log(`already migrated (skip)  ${alreadyDone}`);
console.log(`files changed            ${touched}`);
console.log(`--type-N / ml-type-N replaced   ${replacements}\n`);
console.log(perFile.join("\n"));
console.log(WRITE ? "\nwritten" : "\nDRY RUN — pass --write to apply");
