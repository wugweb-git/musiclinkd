#!/usr/bin/env node
/**
 * 18-counters.mjs — Phase 11. Six numbers, none of them called "screens".
 *
 *   node 18-counters.mjs --flows webapp/flows --css guide/system --data data
 *
 * The point is that "205 screens" was never one number. It was a route count
 * being read as a product-surface count, which inflated the design surface by
 * roughly 2x and the template surface by 20x. Each figure below reads from the
 * layer that owns it.
 */
import fs from "node:fs";
import path from "node:path";
const arg = (k, d) => { const i = process.argv.indexOf(k); return i > -1 ? process.argv[i + 1] : d; };
const FLOWS = arg("--flows", "webapp/flows"), CSS = arg("--css", "guide/system"), DATA = arg("--data", "data");
const read = (p, d = null) => fs.existsSync(p) ? JSON.parse(fs.readFileSync(p, "utf8")) : d;

const routes = [];
(function walk(dir) { for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
  const p = path.join(dir, e.name);
  if (e.isDirectory()) walk(p);
  else if (e.name === "index.html") routes.push(path.relative(FLOWS, path.dirname(p)).split(path.sep).join("/"));
}})(FLOWS);

const tf = read(path.join(DATA, "template-families.json"), { families: [], proposedFamilies: [], routes: {} });
const journeys = read(path.join(DATA, "journeys.json"), { journeys: [] });
const cls = read(path.join(DATA, "classification.json"), null);

let states = 0, multi = 0;
for (const r of routes) {
  const c = read(path.join(FLOWS, r, "screen.json"));
  if (!c) continue;
  const n = (c.states || []).length; states += n; if (n > 1) multi++;
}

const cssSrc = ["ml-core.css", "ml-widgets.css"].map(f => path.join(CSS, f)).filter(fs.existsSync)
  .map(f => fs.readFileSync(f, "utf8").replace(/\/\*[\s\S]*?\*\//g, "")).join("\n");
const blocks = new Set([...cssSrc.matchAll(/\.(ml-[a-z0-9-]+)/g)].map(m => m[1]).filter(c => !c.includes("__") && !c.includes("--")));
const html = routes.map(r => fs.readFileSync(path.join(FLOWS, r, "index.html"), "utf8")).join("\n");
const usedBlocks = [...blocks].filter(b => new RegExp(`class="[^"]*\\b${b}\\b`).test(html));

const sprite = path.join(CSS, "ml-icons.svg");
const icons = fs.existsSync(sprite) ? (fs.readFileSync(sprite, "utf8").match(/<symbol /g) || []).length : 0;

const famCount = new Set(Object.values(tf.routes)).size;
const canonical = cls ? Object.values(cls).filter(v => v.class === "REAL_SCREEN").length : null;

const row = (label, value, note) =>
  console.log(`  ${label.padEnd(30)} ${String(value).padStart(6)}   ${note}`);

console.log("\nMusicLinkd — what there actually is\n");
row("Template families", famCount, `${tf.families.length} adopted + ${(tf.proposedFamilies || []).length} proposed`);
row("Canonical product screens", canonical ?? "—", canonical ? "destinations a person navigates to" : "run the classifier to populate");
row("Implementation entries", routes.length, "routes on disk — NOT the product-screen count");
row("States", states, `${multi} routes carry more than one`);
row("Components in use", usedBlocks.length, `of ${blocks.size} defined · ${blocks.size - usedBlocks.length} in the recycling backlog`);
row("Icons", icons, "one sprite, referenced");
row("Journeys", (journeys.journeys || []).length, "actor → intent → route → outcome");

console.log(`\n  Read it as: ${famCount} templates, ${canonical ?? "?"} product screens, ${routes.length} routes, ${states} states.`);
console.log(`  Never as "${routes.length} screens" — that is the route count, and quoting it as`);
console.log(`  product surface overstates the design work by roughly 2x and the template work by 15x.\n`);
