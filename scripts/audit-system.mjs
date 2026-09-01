#!/usr/bin/env node
/**
 * 11-audit.mjs — the build gate.
 *
 *   node 11-audit.mjs --flows webapp/flows --css guide/system --map data/flows-map.json
 *
 * Exits non-zero on any FAIL. Warnings are printed and tolerated so the gate can
 * be turned on before the migration finishes.
 */
import fs from "node:fs";
import path from "node:path";
const arg = (k, d) => { const i = process.argv.indexOf(k); return i > -1 ? process.argv[i + 1] : d; };
const FLOWS = arg("--flows", "webapp/flows"), CSS = arg("--css", "guide/system"), MAP = arg("--map", "data/flows-map.json");

const fails = [], warns = [];
const fail = (rule, msg) => fails.push(`${rule}: ${msg}`);
const warn = (rule, msg) => warns.push(`${rule}: ${msg}`);

const screens = [];
(function walk(d){ for (const e of fs.readdirSync(d,{withFileTypes:true})) {
  const p = path.join(d,e.name);
  if (e.isDirectory()) walk(p); else if (e.name==="index.html") screens.push(p);
}})(FLOWS);

const routes = new Set(screens.map(f => path.relative(FLOWS, path.dirname(f)).split(path.sep).join("/")));
const cssSrc = ["ml-core.css","ml-widgets.css","ml-tokens.css","ml-fixes.css"]
  .map(f => path.join(CSS,f)).filter(fs.existsSync).map(f => fs.readFileSync(f,"utf8")).join("\n");
const defined = new Set([...cssSrc.matchAll(/\.(ml-[a-zA-Z0-9_-]+)/g)].map(m => m[1]));
const tokens  = new Set([...cssSrc.matchAll(/(--[a-z0-9-]+)\s*:/g)].map(m => m[1]));

/* 7 — token ceiling */
const emitted = (fs.existsSync(path.join(CSS,"ml-tokens.css"))
  ? (fs.readFileSync(path.join(CSS,"ml-tokens.css"),"utf8").match(/^\s*--[a-z0-9-]+:/gm)||[]).length : 0);
if (emitted > 108) fail("token-ceiling", `${emitted} emitted, ceiling is 108`);
else console.log(`  token budget            ${emitted}/108`);

for (const f of screens) {
  const route = path.relative(FLOWS, path.dirname(f)).split(path.sep).join("/");
  const html = fs.readFileSync(f, "utf8");
  const dir = path.dirname(f);

  /* 2 — viewport */
  if (/maximum-scale=/.test(html)) fail("viewport", `${route} disables pinch-zoom`);

  /* 7 — screen contract */
  const cj = path.join(dir, "screen.json");
  if (!fs.existsSync(cj)) { fail("missing-contract", `${route} has no screen.json`); continue; }
  let contract; try { contract = JSON.parse(fs.readFileSync(cj,"utf8")); }
  catch { fail("bad-contract", `${route} screen.json is not valid JSON`); continue; }

  /* 8 — declared states vs markup, both directions */
  const declared = new Set(contract.states || []);
  const inMarkup = new Set([...html.matchAll(/data-state="([^"]+)"/g)].flatMap(m => m[1].split(/\s+/)));
  for (const s of inMarkup) if (!declared.has(s)) fail("undeclared-state", `${route} markup uses "${s}", contract does not declare it`);
  if (declared.size > 1) for (const s of declared) if (!inMarkup.has(s)) warn("unimplemented-state", `${route} declares "${s}" with no markup`);
  if (declared.size === 1) warn("single-state", `${route} declares only "${[...declared][0]}"`);

  /* 9 — component references resolve */
  for (const m of html.matchAll(/class="([^"]*)"/g))
    for (const c of m[1].split(/\s+/))
      if (c.startsWith("ml-") && !defined.has(c)) fail("unknown-class", `${route} uses .${c}, not defined in the system`);

  /* token references resolve */
  for (const m of html.matchAll(/var\((--[a-z0-9-]+)/g))
    if (!tokens.has(m[1])) fail("unknown-token", `${route} references ${m[1]}, not defined`);

  /* 10 — internal links resolve */
  for (const m of html.matchAll(/href="\/preview\/flows\/([^"?#]+?)(?:\/index\.html)?(?:[?#][^"]*)?"/g))
    if (!routes.has(m[1].replace(/\/$/,""))) fail("broken-link", `${route} links to ${m[1]}, which does not exist`);

  /* a11y floors that are checkable statically */
  if (!/<main\b/.test(html)) fail("no-main", `${route} has no <main>`);
  if (!/<h1\b/.test(html))   fail("no-h1", `${route} has no <h1>`);
  for (const m of html.matchAll(/<a\b((?:(?!role=)[^>])*?)aria-pressed=/g)) fail("invalid-aria", `${route} has aria-pressed on an <a>`);
  if (/[\u{1F300}-\u{1FAFF}\u{2600}-\u{27BF}]|★|☆/u.test(html.replace(/<(script|style)[\s\S]*?<\/\1>/g,"")))
    warn("emoji-as-icon", `${route} contains emoji or star characters where an icon belongs`);
}

/* 11 — journeys reference real routes */
const jp = path.join(path.dirname(MAP), "journeys.json");
if (fs.existsSync(jp)) {
  const js = JSON.parse(fs.readFileSync(jp, "utf8"));
  for (const j of js.journeys || [])
    for (const step of j.steps || [])
      if (step.route && !routes.has(step.route)) fail("broken-journey", `journey "${j.name}" references ${step.route}, which does not exist`);
} else warn("no-journeys", "data/journeys.json does not exist — layer 4 is unmodelled");

console.log(`  screens audited         ${screens.length}`);
console.log(`\n  WARN ${warns.length}`);
for (const w of warns.slice(0, 12)) console.log("    " + w);
if (warns.length > 12) console.log(`    ... and ${warns.length - 12} more`);
console.log(`\n  FAIL ${fails.length}`);
for (const f of fails.slice(0, 20)) console.log("    " + f);
if (fails.length > 20) console.log(`    ... and ${fails.length - 20} more`);
process.exit(fails.length ? 1 : 0);
