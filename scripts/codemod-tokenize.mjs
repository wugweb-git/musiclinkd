/**
 * codemod-tokenize.mjs — replace literals in the stylesheets with the token
 * that already names them.
 *
 * audit-tokens.mjs found 156 such literals and, as a direct consequence,
 * --border-1 and --border-2 sitting unreferenced on web while ml-core.css
 * wrote `1px` seventy-odd times. The token existed; nothing used it.
 *
 * This is deliberately PROPERTY-AWARE rather than a value substitution. A
 * blind s/2px/var(--border-2)/ would rewrite `outline-offset: 2px` (an offset,
 * not a width) and `padding: 2px var(--space-2)` (padding), both of which are
 * in ml-core.css today. Only the rules below run, and each says why it is safe.
 *
 * Usage: node scripts/codemod-tokenize.mjs [--write]
 *        without --write it prints the diff it would make and changes nothing.
 */

import { readFileSync, writeFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = join(dirname(fileURLToPath(import.meta.url)), "..");
const SYS = join(ROOT, "frontend/guide/system");
const WRITE = process.argv.includes("--write");
const FILES = ["ml-core.css", "ml-widgets.css"];

const BORDER_TOKEN = { 1: "--border-1", 2: "--border-2" };
const ICON_TOKEN = { 16: "--icon-1", 20: "--icon-2", 24: "--icon-3", 32: "--icon-4" };

const RULES = [
  {
    name: "border width",
    /* `border: 1px solid X`, `border-top: 2px dashed X`, `border-width: 1px`.
       The width is always the FIRST length in the shorthand, and a border
       width is the only thing --border-* names, so this is unambiguous.
       Excludes border-radius, which is a --radius-* concern, by requiring the
       property to be border / border-<side> / border-width exactly. */
    re: /\b(border(?:-(?:top|right|bottom|left|width|inline|block))?)\s*:\s*(\d+)px\b/g,
    apply: (m, prop, n) => BORDER_TOKEN[n] ? `${prop}: var(${BORDER_TOKEN[n]})` : m,
  },
  {
    name: "icon glyph size",
    /* Only on a declaration that sets width and height to the SAME value on
       one line — the shape every icon rule in these files takes:
         .ml-icon-btn svg { width: 20px; height: 20px; display: block; }
       A single width, or a width and height that differ, is a layout size and
       is left alone. Restricted further to selectors mentioning svg or icon,
       so a 24x24 avatar or badge is not miscast as an icon. */
    line: /(^|\s)(svg|[\w.-]*icon[\w.-]*)\b/i,
    re: /\bwidth:\s*(\d+)px;\s*height:\s*\1px\b/g,
    apply: (m, n) => ICON_TOKEN[n]
      ? `width: var(${ICON_TOKEN[n]}); height: var(${ICON_TOKEN[n]})`
      : m,
  },
];

let total = 0;
for (const file of FILES) {
  const path = join(SYS, file);
  const src = readFileSync(path, "utf8");
  const out = [];
  let changed = 0;

  /* Track whether we are inside a block comment, so prose that happens to
     contain "border: 1px" is never rewritten. ml-core.css's comments discuss
     exactly these values, at length. */
  let inComment = false;
  let selector = "";

  for (const raw of src.split("\n")) {
    let line = raw;
    const opens = (line.match(/\/\*/g) || []).length;
    const closes = (line.match(/\*\//g) || []).length;
    const wasInComment = inComment;
    if (opens > closes) inComment = true;
    else if (closes > opens) inComment = false;

    if (!wasInComment && !/^\s*(\/\*|\*)/.test(line)) {
      if (/\{/.test(line)) selector = line.split("{")[0];
      for (const rule of RULES) {
        if (rule.line && !rule.line.test(selector) && !rule.line.test(line)) continue;
        const before = line;
        line = line.replace(rule.re, rule.apply);
        if (line !== before) {
          changed++;
          if (!WRITE) console.log(`  ${file}\n    - ${before.trim()}\n    + ${line.trim()}`);
        }
      }
    }
    out.push(line);
  }

  if (changed) {
    total += changed;
    if (WRITE) writeFileSync(path, out.join("\n"));
    console.log(`${file}: ${changed} declaration${changed === 1 ? "" : "s"} tokenized${WRITE ? "" : " (dry run)"}`);
  }
}
console.log(`\n${total} total${WRITE ? " — written" : " — dry run, pass --write to apply"}`);
