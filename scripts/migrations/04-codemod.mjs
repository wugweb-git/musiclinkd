#!/usr/bin/env node
/**
 * 04-codemod.mjs — the 205-screen sweep.
 *
 *   node 04-codemod.mjs <flows-dir> [--dry]
 *
 * Every transform is idempotent: running it twice changes nothing the second
 * time. Each one reports a count so the diff can be checked against the audit.
 */
import fs from "node:fs";
import path from "node:path";

const root = process.argv[2];
const DRY = process.argv.includes("--dry");
if (!root) { console.error("usage: node 04-codemod.mjs <flows-dir> [--dry]"); process.exit(1); }

const files = [];
(function walk(d) {
  for (const e of fs.readdirSync(d, { withFileTypes: true })) {
    const p = path.join(d, e.name);
    if (e.isDirectory()) walk(p);
    else if (e.name === "index.html") files.push(p);
  }
})(root);

const n = Object.create(null);
const bump = (k, c = 1) => { n[k] = (n[k] || 0) + c; };

for (const f of files) {
  let s = fs.readFileSync(f, "utf8");
  const before = s;

  // 1 — viewport: maximum-scale=1 disables pinch-zoom. WCAG 2.1 AA 1.4.4.
  s = s.replace(/(<meta name="viewport"[^>]*?)\s*,?\s*maximum-scale=1(?=[",])/g,
    (m, a) => { bump("viewport"); return a; });

  // 2 — app bar: strip the inline override so the grid rule in ml-fixes.css can
  //     apply, which also restores the --safe-top the inline padding was killing.
  s = s.replace(/(<header class="ml-appbar")\s+style="[^"]*"/g,
    (m, a) => { bump("appbar-inline"); return a; });

  // 3 — tabs: aria-selected is invalid without role="tab". Add the roles rather
  //     than removing the attribute, since these really are tabs.
  s = s.replace(/<nav class="ml-tabs([^"]*)"((?:(?!role=)[^>])*?)>/g, (m, mod, rest) => {
    bump("tablist"); return `<nav class="ml-tabs${mod}"${rest} role="tablist">`;
  });
  s = s.replace(/<(a|span|button) class="ml-tabs__tab([^"]*)"((?:(?!role=)[^>])*?)>/g,
    (m, tag, mod, rest) => { bump("tab-role"); return `<${tag} class="ml-tabs__tab${mod}"${rest} role="tab">`; });

  // 4 — type scale renumber: DROPPED. It existed only to make room for a new
  //     14px step, and build-tokens.mjs caps every scale at 4 steps. Renumbering
  //     without the new step is churn with no destination.

  // 5 — outcome banners: a full refund is the good outcome and a declined offer
  //     is neutral. Neither is an error.
  s = s.replace(/(class="ml-banner ml-banner--)error("[^>]*>\s*(?:<[^>]+>\s*)?(?:Cancelled — refunded in full))/g,
    (m, a, b) => { bump("banner-success"); return a + "success" + b; });
  s = s.replace(/(class="ml-banner ml-banner--)error("[^>]*>\s*(?:<[^>]+>\s*)?(?:Offer declined))/g,
    (m, a, b) => { bump("banner-neutral"); return a + "neutral" + b; });

  // 6 — verb consistency. Four ways to say retry, two to say book.
  const verbs = [
    [/>\s*Try again\s*</g, ">Retry<", "verb-retry"],
    [/>\s*Scan again\s*</g, ">Scan another<", "verb-scan"],
    [/>\s*Book now\s*</g, ">Book<", "verb-book"],
  ];
  for (const [re, to, key] of verbs) {
    const c = (s.match(re) || []).length;
    if (c) { s = s.replace(re, to); bump(key, c); }
  }

  // 7 — load the fix layer.
  if (!s.includes("ml-fixes.css")) {
    s = s.replace(/(<link rel="stylesheet" href="\/preview\/shared\/app-shell\.css[^>]*>)/,
      (m) => { bump("fixes-css"); return `<link rel="stylesheet" href="/guide/system/ml-fixes.css" />\n${m}`; });
  }

  if (s !== before) { bump("files-changed"); if (!DRY) fs.writeFileSync(f, s); }
}

console.log(DRY ? "DRY RUN — nothing written\n" : "APPLIED\n");
console.log(`files scanned            ${files.length}`);
for (const k of Object.keys(n).sort()) console.log(`  ${k.padEnd(22)} ${n[k]}`);
