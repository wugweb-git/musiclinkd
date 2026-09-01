#!/usr/bin/env node
/**
 * 21-state-default-hidden.mjs — the flash-of-all-states fix, reconciled.
 *
 *   node 21-state-default-hidden.mjs <flows-dir> [--write]
 *
 * SUPERSEDES section 2 of 02-ml-fixes.css. Delete that block before applying.
 *
 * The problem: applyState() runs after DOMContentLoaded AND after an async
 * screen.json fetch. Until then every state paints at once; if the fetch 404s it
 * stays that way permanently and the only signal is a console.warn.
 *
 * The first fix set solved it in CSS — `[data-state] { display: none }` plus a
 * reveal class. That cannot ship, because a concurrent motion pass now animates
 * the same selector with `transition: display allow-discrete` and an
 * `@starting-style` keyed on `[hidden]` being removed. A display:none default
 * fights both.
 *
 * This solves it in the markup instead, which collides with nothing:
 *
 *   every [data-state] element whose state is NOT states[0] ships with `hidden`
 *
 * so the served HTML already shows exactly one coherent state. No JS, no fetch,
 * no CSS involved. app-shell's `el.hidden = !states.includes(state)` still owns
 * the attribute at runtime, `[hidden]{display:none!important}` still does the
 * hiding, and the motion pass's @starting-style still fires on the removal —
 * every mechanism keeps its existing owner.
 *
 * Currently 5 of 651 such elements carry `hidden`. The rest are the bug.
 */
import fs from "node:fs";
import path from "node:path";
const FLOWS = process.argv[2], WRITE = process.argv.includes("--write");

const files = [];
(function walk(d) { for (const e of fs.readdirSync(d, { withFileTypes: true })) {
  const p = path.join(d, e.name);
  if (e.isDirectory()) walk(p); else if (e.name === "index.html") files.push(p);
}})(FLOWS);

let stamped = 0, alreadyOk = 0, defaults = 0, touched = 0, noContract = 0;
for (const f of files) {
  const dir = path.dirname(f);
  const cj = path.join(dir, "screen.json");
  if (!fs.existsSync(cj)) { noContract++; continue; }
  const states = JSON.parse(fs.readFileSync(cj, "utf8")).states || [];
  if (states.length < 2) continue;            // single-state screens have nothing to hide
  const first = states[0];

  let h = fs.readFileSync(f, "utf8"); const before = h;
  h = h.replace(/<([a-z][a-z0-9]*)((?:[^>]*?\s)?data-state="([^"]+)"[^>]*?)>/g, (m, tag, attrs, val) => {
    const list = val.split(/\s+/).filter(Boolean);
    if (list.includes(first)) {               // belongs to the default state — stays visible
      if (/\bhidden\b/.test(attrs)) { /* leave a deliberate hidden alone */ }
      alreadyOk++;
      // Mark it. app-shell's fallbackState() reads this when screen.json
      // cannot be fetched, so the default state is declared in the markup
      // rather than inferred from whichever [data-state] element happens to
      // come first in document order.
      if (/\bdata-state-default\b/.test(attrs)) return m;
      defaults++;
      return `<${tag}${attrs} data-state-default>`;
    }
    if (/(^|\s)hidden(\s|=|$)/.test(attrs)) { alreadyOk++; return m; }
    stamped++;
    return `<${tag}${attrs} hidden>`;
  });
  if (h !== before) { touched++; if (WRITE) fs.writeFileSync(f, h); }
}

console.log(`screens scanned          ${files.length}`);
console.log(`  without a contract     ${noContract}`);
console.log(`files changed            ${touched}`);
console.log(`elements stamped hidden  ${stamped}`);
console.log(`already correct          ${alreadyOk}`);
console.log(`default-state marked     ${defaults}`);
console.log(WRITE ? "\nwritten" : "\nDRY RUN — pass --write to apply");
console.log(`\nRemember: delete section 2 from 02-ml-fixes.css. Both fixes solve the same
problem and only this one is compatible with the motion pass.`);
