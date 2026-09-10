#!/usr/bin/env node
/**
 * 15-triage-states.mjs — which single-state screens actually need states.
 *
 *   node 15-triage-states.mjs <flows-dir> <flows-map.json>
 *
 * "76 screens have no loading, empty or error state" is true but not actionable:
 * a static legal page genuinely needs none. The question is whether the screen
 * READS REMOTE DATA. Those need loading + empty + error before they can be wired
 * to Firestore; the rest are fine as they are.
 *
 * The signal is the screen's own contract — collectionsTouched — plus whether the
 * markup renders a list or a card grid, which is what has an empty state.
 */
import fs from "node:fs";
import path from "node:path";
const FLOWS = process.argv[2];
const map = JSON.parse(fs.readFileSync(process.argv[3], "utf8"));

const need = [], fine = [], partial = [];
for (const s of map.screens) {
  const route = `${s.flowKey}/${s.screenKey}`;
  const dir = path.join(FLOWS, route);
  const cj = path.join(dir, "screen.json");
  if (!fs.existsSync(cj)) continue;
  const contract = JSON.parse(fs.readFileSync(cj, "utf8"));
  const states = new Set(contract.states || []);
  if (states.size > 1) continue;

  const html = fs.existsSync(path.join(dir, "index.html")) ? fs.readFileSync(path.join(dir, "index.html"), "utf8") : "";
  const collections = (contract.collectionsTouched || s.collectionsTouched || []);
  const listish = /ml-(result|person-row|market-card|artist-card|studio-card|media-card|review-card|booking-card|opportunity-card|rail|feed)\b/.test(html);
  const formish = /ml-field|<input|<textarea|<select/.test(html);

  const missing = [];
  if (collections.length || listish) { missing.push("loading"); missing.push("error"); }
  if (listish) missing.push("empty");
  if (formish && !collections.length) missing.push("error");

  const row = { route, collections, listish, formish, missing: [...new Set(missing)] };
  if (!row.missing.length) fine.push(row);
  else if (collections.length) need.push(row);
  else partial.push(row);
}

console.log(`single-state screens: ${need.length + partial.length + fine.length}\n`);
console.log(`BLOCKING — reads remote data, needs states before Firestore wiring: ${need.length}`);
for (const r of need) console.log(`   ${r.route.padEnd(46)} + ${r.missing.join(", ").padEnd(22)} [${r.collections.join(", ")}]`);
console.log(`\nSHOULD HAVE — renders a list or a form but touches no collection yet: ${partial.length}`);
for (const r of partial) console.log(`   ${r.route.padEnd(46)} + ${r.missing.join(", ")}`);
console.log(`\nFINE AS IS — static, no remote read, nothing to fail: ${fine.length}`);
for (const r of fine) console.log(`   ${r.route}`);
