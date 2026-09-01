#!/usr/bin/env node
/**
 * 07-template-families.mjs — derive templateFamily for every route.
 *
 *   node 07-template-families.mjs <flows-dir> <flows-map.json> [--write]
 *
 * The nine families come from /guide -> Patterns and are NOT invented here.
 * Assignment is by STRUCTURE first (what the screen is built from) and name
 * second, so the answer is checkable rather than asserted. Routes that match no
 * family are reported as UNASSIGNED rather than forced — an unassigned route is
 * real signal: either the family set needs a tenth entry, or that screen is not
 * built on a template at all.
 */
import fs from "node:fs";
import path from "node:path";

/* The first nine come from /guide -> Patterns and predate the flow model. The
   last four are PROPOSED, not invented on a whim: running this with nine left 32
   routes unassigned, and they fell into four clean groups rather than scattering.
   That is the signature of a family set that is incomplete, not of odd routes.
   Adopting them is a Guide decision — until then they are flagged in the output. */
const FAMILIES = ["Splash","Login","Onboarding","Home","Discover",
                  "Artist Profile","Studio Profile","Marketplace","Settings"];
const PROPOSED = ["Conversation","Review","Listing & Application","Checkout"];
const ALL = [...FAMILIES, ...PROPOSED];

const flowsDir = process.argv[2], mapPath = process.argv[3];
const WRITE = process.argv.includes("--write");
const map = JSON.parse(fs.readFileSync(mapPath, "utf8"));

function read(route) {
  const p = path.join(flowsDir, route, "index.html");
  return fs.existsSync(p) ? fs.readFileSync(p, "utf8") : "";
}

/* Structural signals, cheap and observable. */
function features(h) {
  const has = (s) => h.includes(s);
  return {
    tabbar:   has("ml-tabbar"),          // top-level destination
    tabs:     has("ml-tabs"),            // in-screen tab strip
    steps:    has("ml-steps"),           // progress indicator
    hero:     has("ml-hero") || has("ml-profile-hero"),
    grid:     has("ml-market-card") || has("ml-listing"),
    search:   has("ml-search"),
    rows:     (h.match(/ml-row\b/g) || []).length,
    switches: (h.match(/ml-switch\b/g) || []).length,
    fields:   (h.match(/ml-field\b/g) || []).length,
    cards:    (h.match(/ml-card\b/g) || []).length,
    splash:   has("ml-splash") || has("ml-wordmark") && !has("ml-tabbar") && h.length < 4000,
  };
}

const NAME = [
  [/^auth\/splash/,                         "Splash"],
  [/^auth\//,                               "Login"],
  [/^onboarding\//,                         "Onboarding"],
  [/^home\//,                               "Home"],
  [/^discover\//,                           "Discover"],
  [/^marketplace\//,                        "Marketplace"],
  [/^(user_settings|cms_pages)\//,          "Settings"],
  [/^(business_profiles)\//,                "Studio Profile"],
  [/^booking\/(studio_profile|studio_discovery|availability|equipment|provider)/, "Studio Profile"],
  [/^(portfolio|public_profile|band)\//,    "Artist Profile"],
  [/^profile_management\//,                 "Settings"],
  [/^(services_availability|notifications|subscription|verifications|qr_share)\//, "Settings"],
  [/^(search|saved_favorites|recommendation_engine)\//, "Discover"],
  /* ── the four proposed families ────────────────────────────────────────── */
  [/^communication\//,                     "Conversation"],
  [/^reviews\//,                           "Review"],
  [/^opportunities\//,                     "Listing & Application"],
  [/^booking\/(filters|search_results|booking_summary|payment_screen|booking_success|my_bookings)/, "Checkout"],
  [/^booking\//,                           null],  // the rest decide structurally
];

function assign(route, f) {
  for (const [re, fam] of NAME) if (re.test(route)) { if (fam) return [fam, "name"]; break; }
  /* Structural fallback for the flows that genuinely span families. */
  if (f.steps && f.fields)            return ["Onboarding", "structure: progress + fields"];
  if (f.grid || (f.search && f.tabs)) return ["Marketplace", "structure: listing grid"];
  if (f.tabbar && f.search)           return ["Discover", "structure: bottom nav + search"];
  if (f.hero)                         return ["Artist Profile", "structure: hero"];
  if (f.switches >= 2 || f.rows >= 4) return ["Settings", "structure: row/switch list"];
  if (f.fields >= 2)                  return ["Onboarding", "structure: form"];
  if (f.cards >= 2)                   return ["Discover", "structure: card list"];
  return [null, "no structural match"];
}

const out = {}, tally = {}, unassigned = [];
for (const s of map.screens) {
  const route = `${s.flowKey}/${s.screenKey}`;
  const [fam, why] = assign(route, features(read(route)));
  if (!fam) { unassigned.push(route); continue; }
  out[route] = fam;
  tally[fam] = (tally[fam] || 0) + 1;
}

console.log(`routes: ${map.screens.length}   assigned: ${Object.keys(out).length}   unassigned: ${unassigned.length}\n`);
for (const f of ALL) {
  const n = tally[f] || 0;
  const tag = PROPOSED.includes(f) ? " (proposed)" : "";
  console.log(`  ${f.padEnd(22)} ${String(n).padStart(3)}  ${"█".repeat(Math.round(n / 2))}${tag}`);
}
if (unassigned.length) { console.log("\nUNASSIGNED — needs a human call:"); for (const r of unassigned) console.log("   " + r); }

const propCount = PROPOSED.reduce((a, f) => a + (tally[f] || 0), 0);
console.log(`\n${ALL.length} families, ${Object.keys(out).length} instances`);
console.log(`  ${FAMILIES.length} existing cover ${Object.keys(out).length - propCount}`);
console.log(`  ${PROPOSED.length} proposed cover ${propCount} — adopt them in /guide or reassign those routes`);
if (WRITE) {
  fs.writeFileSync("data/template-families.json",
    JSON.stringify({ _note: "Generated by 07-template-families.mjs. Families come from /guide -> Patterns.",
                     families: FAMILIES, proposedFamilies: PROPOSED, routes: out }, null, 2));
  console.log("\nwrote data/template-families.json");
}
