#!/usr/bin/env node
/**
 * 19-fix-severed-links.mjs — reconnect the five journeys.
 *
 *   node 19-fix-severed-links.mjs <flows-dir> [--write]
 *
 * 16-journeys.mjs found that 5 of 6 core journeys cannot be walked from page
 * content. Not because screens are missing — every destination exists and is
 * finished — but because five links were never added. Each fix below is one
 * anchor placed where that screen's own structure says it belongs.
 *
 * The worst is booking/availability_calendar: it has NO action control at all,
 * only a back arrow. A user can find a studio, open its calendar, and go no
 * further. The second half of the booking flow is built and unreachable.
 *
 * Idempotent — each insert is guarded by the href it adds.
 */
import fs from "node:fs";
import path from "node:path";
const FLOWS = process.argv[2], WRITE = process.argv.includes("--write");
const P = (r) => path.join(FLOWS, r, "index.html");
const applied = [], skipped = [];

function edit(route, why, fn) {
  const p = P(route);
  if (!fs.existsSync(p)) { skipped.push(`${route} — file not found`); return; }
  const before = fs.readFileSync(p, "utf8");
  const after = fn(before);
  if (after === before) { skipped.push(`${route} — already linked or anchor not found`); return; }
  applied.push(`${route}  ${why}`);
  if (WRITE) fs.writeFileSync(p, after);
}

/* 1 — the booking cul-de-sac. No CTA existed; add the one the flow needs. */
edit("booking/availability_calendar", "+ Continue -> configure_session", (h) => {
  if (h.includes("booking/configure_session")) return h;
  const bar = `
      <div class="ml-action-bar ml-action-bar--sticky">
        <a class="ml-btn ml-btn--secondary" href="/preview/flows/booking/studio_profile/index.html">Back to studio</a>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/booking/configure_session/index.html">Continue</a>
      </div>
`;
  return h.replace(/(\s*<\/main>)/, `${bar}$1`);
});

/* 2 — a completed booking is the only honest place to ask for a review, and it
       was the one place with no route to one. The cards are anchors, so the
       action cannot nest inside them — it goes alongside, in a wrapper. */
edit("booking/my_bookings", "+ Leave a review beside each past booking", (h) => {
  if (h.includes("reviews/write_review")) return h;
  return h.replace(/(<a class="ml-booking-card" data-state="past"[\s\S]*?<\/a>)/g,
    (card) => `<div class="ml-stack ml-stack--2" data-state="past">\n      ${card.replace(' data-state="past"', '')}\n      <a class="ml-btn ml-btn--secondary ml-btn--sm" href="/preview/flows/reviews/write_review/index.html">Leave a review</a>\n    </div>`);
});

/* 3 — you could find a person in Discover and never reach their profile. */
edit("discover/discover_profile_detail", "+ View full profile -> public_profile_view_talent", (h) => {
  if (h.includes("public_profile/public_profile_view_talent")) return h;
  return h.replace(/(<a[^>]*class="[^"]*ml-btn[^"]*"[^>]*>\s*Hire\s*<\/a>)/,
    `$1\n        <a class="ml-btn ml-btn--ghost" href="/preview/flows/public_profile/public_profile_view_talent/index.html">View full profile</a>`);
});

/* 4 — publishing a listing should land on the list it joined. */
edit("marketplace/marketplace_create_listing", "+ View my listings after publish", (h) => {
  if (/data-state="[^"]*published[^"]*"[\s\S]*marketplace_my_listings/.test(h)) return h;
  return h.replace(/(<a[^>]*class="[^"]*ml-btn[^"]*"[^>]*>\s*Publish listing\s*<\/a>)/,
    `$1\n        <a class="ml-btn ml-btn--secondary" href="/preview/flows/marketplace/marketplace_my_listings/index.html">View my listings</a>`);
});

/* 5 — the point of onboarding is the profile; it never showed you one. */
edit("onboarding/submit_publish", "+ View your profile alongside Continue", (h) => {
  if (h.includes("public_profile/public_profile_view_talent")) return h;
  return h.replace(/(<a[^>]*class="[^"]*ml-btn[^"]*"[^>]*>\s*Continue\s*<\/a>)/,
    `<a class="ml-btn ml-btn--secondary" href="/preview/flows/public_profile/public_profile_view_talent/index.html">View your profile</a>\n        $1`);
});

console.log(`applied ${applied.length}`);
for (const a of applied) console.log("   " + a);
if (skipped.length) { console.log(`\nskipped ${skipped.length}`); for (const s of skipped) console.log("   " + s); }
console.log(WRITE ? "\nwritten — re-run 16-journeys.mjs to confirm" : "\nDRY RUN — pass --write to apply");
