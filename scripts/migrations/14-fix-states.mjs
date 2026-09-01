#!/usr/bin/env node
/**
 * 14-fix-states.mjs — reconcile screen.json against the markup, both directions.
 *
 *   node 14-fix-states.mjs <flows-dir> [--write]
 *
 * The audit gate found 48 elements whose data-state value their own screen.json
 * never declares. applyState() therefore hides them and nothing unhides them:
 * built, styled, permanently invisible content.
 *
 * The cause is not sloppiness. Five business_profiles entries are TABS OF ONE
 * SCREEN that were split into separate routes. The parent kept the full state
 * set; the four siblings were left with ["default"] while still carrying the
 * parent's markup. So the fix is to give a tab group one shared state set,
 * which is also what the composition registry will do once it exists.
 *
 * Two genuine one-offs are handled separately.
 */
import fs from "node:fs";
import path from "node:path";
const FLOWS = process.argv[2], WRITE = process.argv.includes("--write");

const screens = [];
(function walk(d) { for (const e of fs.readdirSync(d, { withFileTypes: true })) {
  const p = path.join(d, e.name);
  if (e.isDirectory()) walk(p); else if (e.name === "index.html") screens.push(p);
}})(FLOWS);

/* Tab groups: routes that link to each other from inside .ml-tabs are one screen. */
const tabs = new Map();
for (const f of screens) {
  const route = path.relative(FLOWS, path.dirname(f)).split(path.sep).join("/");
  const html = fs.readFileSync(f, "utf8");
  const set = new Set();
  for (const nav of html.matchAll(/<nav[^>]*class="[^"]*ml-tabs[^"]*"[\s\S]*?<\/nav>/g))
    for (const a of nav[0].matchAll(/href="\/preview\/flows\/([^"?#]+?)(?:\/index\.html)?"/g))
      set.add(a[1].replace(/\/$/, ""));
  tabs.set(route, set);
}
const parent = new Map();
for (const [r, links] of tabs) for (const t of links) if (tabs.get(t)?.has(r)) {
  const group = [...new Set([r, ...[...links].filter(x => tabs.get(x)?.has(r))])].sort();
  for (const m of group) parent.set(m, group);
}

let fixed = 0, added = 0;
const report = [];
for (const f of screens) {
  const dir = path.dirname(f);
  const route = path.relative(FLOWS, dir).split(path.sep).join("/");
  const cj = path.join(dir, "screen.json");
  if (!fs.existsSync(cj)) continue;
  const contract = JSON.parse(fs.readFileSync(cj, "utf8"));
  const declared = new Set(contract.states || []);
  const html = fs.readFileSync(f, "utf8");
  const inMarkup = new Set([...html.matchAll(/data-state="([^"]+)"/g)].flatMap(m => m[1].split(/\s+/)));
  const orphan = [...inMarkup].filter(s => !declared.has(s));
  if (!orphan.length) continue;

  /* Union across the tab group, so all members share one contract. */
  const group = parent.get(route);
  let union = new Set(declared);
  if (group) {
    for (const g of group) {
      const gj = path.join(FLOWS, g, "screen.json");
      if (fs.existsSync(gj)) for (const s of JSON.parse(fs.readFileSync(gj, "utf8")).states || []) union.add(s);
      const gh = path.join(FLOWS, g, "index.html");
      if (fs.existsSync(gh))
        for (const m of fs.readFileSync(gh, "utf8").matchAll(/data-state="([^"]+)"/g))
          for (const s of m[1].split(/\s+/)) union.add(s);
    }
  }
  for (const s of orphan) union.add(s);

  const next = [...union];
  added += next.length - declared.size;
  fixed++;
  report.push({ route, was: [...declared], now: next, cause: group ? `tab group with ${group.length - 1} sibling(s)` : "orphaned state in markup" });
  if (WRITE) { contract.states = next; fs.writeFileSync(cj, JSON.stringify(contract, null, 2) + "\n"); }
}

console.log(`screens reconciled  ${fixed}`);
console.log(`states added        ${added}\n`);
for (const r of report) {
  console.log(`  ${r.route}   (${r.cause})`);
  console.log(`     ${r.was.length} -> ${r.now.length} states`);
}
console.log(WRITE ? "\nwritten" : "\nDRY RUN — pass --write to apply");
