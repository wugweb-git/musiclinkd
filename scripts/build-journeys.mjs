#!/usr/bin/env node
/**
 * 16-journeys.mjs — build layer 4 and validate it against the real nav graph.
 *
 *   node 16-journeys.mjs <flows-dir> [--write]
 *
 * A flow groups screens by feature area. A journey is what a person is trying to
 * do, and it crosses flows. Nothing in the registry models one, which is why a
 * severed booking path and an unreachable review form went unnoticed.
 *
 * Each step names a route. The script walks the CONTENT links only — global
 * chrome excluded, because "reachable via the bottom nav" is not a journey — and
 * reports where the path breaks.
 */
import fs from "node:fs";
import path from "node:path";
const FLOWS = process.argv[2], WRITE = process.argv.includes("--write");

const JOURNEYS = [
  { id: "hire-talent", name: "Hire a session musician", actor: "Hirer",
    trigger: "Needs a player for a session", outcome: "Booking confirmed and paid",
    steps: ["discover/people_talent","discover/discover_profile_detail","public_profile/public_profile_view_talent",
            "public_profile/talent_services_view","booking/configure_session","booking/booking_summary",
            "booking/payment_screen","booking/booking_success"] },
  { id: "book-studio", name: "Book a studio", actor: "Hirer",
    trigger: "Needs a room for a date", outcome: "Booking confirmed and paid",
    steps: ["discover/studios_spaces","discover/studio_profile_discover","booking/availability_calendar",
            "booking/configure_session","booking/booking_summary","booking/payment_screen","booking/booking_success"] },
  { id: "review-after-booking", name: "Review after a session", actor: "Hirer",
    trigger: "Session finished", outcome: "Review published, reputation updated",
    steps: ["booking/booking_success","booking/my_bookings","reviews/write_review","reviews/review_list"] },
  { id: "sell-gear", name: "Sell a piece of gear", actor: "Talent / Studio",
    trigger: "Has gear to sell", outcome: "Sold and paid out",
    steps: ["marketplace/marketplace_create_listing","marketplace/marketplace_my_listings",
            "marketplace/marketplace_make_offer","marketplace/marketplace_order_status"] },
  { id: "get-hired", name: "Get hired for an opportunity", actor: "Talent",
    trigger: "Looking for work", outcome: "Application accepted",
    steps: ["opportunities/opportunities_home","opportunities/opportunity_detail",
            "opportunities/opportunity_apply","opportunities/my_applications"] },
  { id: "onboard", name: "Sign up to a live public profile", actor: "New user",
    trigger: "First open", outcome: "Public profile discoverable",
    steps: ["auth/welcome","auth/email_auth","onboarding/primary_role","onboarding/genres",
            "onboarding/profile_creation","onboarding/review_confirm","onboarding/submit_publish",
            "public_profile/public_profile_view_talent"] },
];

/* content-only edges */
const edges = new Map();
(function walk(d) { for (const e of fs.readdirSync(d, { withFileTypes: true })) {
  const p = path.join(d, e.name);
  if (e.isDirectory()) walk(p);
  else if (e.name === "index.html") {
    const route = path.relative(FLOWS, path.dirname(p)).split(path.sep).join("/");
    const html = fs.readFileSync(p, "utf8");
    const chrome = (html.match(/<(?:nav|header)[^>]*class="[^"]*ml-(?:tabbar|tabs|appbar|navbar)[^"]*"[\s\S]*?<\/(?:nav|header)>/g) || []).join("");
    const body = html.split(chrome).join("");
    edges.set(route, new Set([...body.matchAll(/href="\/preview\/flows\/([^"?#]+?)(?:\/index\.html)?(?:[?#][^"]*)?"/g)].map(m => m[1].replace(/\/$/, ""))));
  }
}})(FLOWS);

/* A journey lists milestones, not every screen, so "not directly linked" is not
   the same as "unreachable". BFS distinguishes the two: a step reachable in a few
   hops is fine; a step reachable in none is a severed path. */
function hops(a, b, max = 4) {
  if (!edges.has(a)) return -1;
  let frontier = new Set([a]), seen = new Set([a]);
  for (let d = 1; d <= max; d++) {
    const next = new Set();
    for (const n of frontier) for (const m of edges.get(n) || []) {
      if (m === b) return d;
      if (!seen.has(m)) { seen.add(m); next.add(m); }
    }
    frontier = next; if (!frontier.size) break;
  }
  return -1;
}

let broken = 0;
for (const j of JOURNEYS) {
  const severed = [], indirect = [];
  for (let i = 0; i < j.steps.length - 1; i++) {
    const a = j.steps[i], b = j.steps[i + 1];
    if (!edges.has(a)) { severed.push(`${a} does not exist`); continue; }
    if (edges.get(a).has(b)) continue;
    const d = hops(a, b);
    if (d > 0) indirect.push(`${a}  ->  ${b}  (${d} hops)`);
    else severed.push(`${a}  ->  ${b}`);
  }
  j.gaps = severed; if (severed.length) broken++;
  const tag = severed.length ? "SEVERED" : indirect.length ? "walks* " : "walks  ";
  console.log(`${tag} ${j.name.padEnd(38)} ${j.steps.length} steps, ${new Set(j.steps.map(s=>s.split("/")[0])).size} flows`);
  for (const g of severed)  console.log(`          NO PATH:        ${g}`);
  for (const g of indirect) console.log(`          via other screens: ${g}`);
}
console.log(`\n${JOURNEYS.length - broken} of ${JOURNEYS.length} journeys are walkable.  * = reachable, not directly linked.`);
if (WRITE) {
  fs.mkdirSync("data", { recursive: true });
  fs.writeFileSync("data/journeys.json", JSON.stringify({
    _note: "Layer 4. A journey crosses flows; validate with 16-journeys.mjs and 11-audit.mjs.",
    journeys: JOURNEYS.map(({ gaps, ...j }) => ({ ...j, steps: j.steps.map(route => ({ route })) })) }, null, 2));
  console.log("wrote data/journeys.json");
}
