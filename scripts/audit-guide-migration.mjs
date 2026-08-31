/**
 * audit-guide-migration.mjs — what the old styleguide has that the new guide doesn't.
 *
 * The old /guide/styleguide.html was superseded by the generated
 * /guide/index.html (ml-tokens + ml-core + ml-widgets). The migration was
 * claimed complete more than once and was not. This script exists so the
 * claim is never a claim again: it reads both files and reports the delta.
 *
 * It does NOT judge fidelity — a section that exists in both may still be a
 * shabby port. It only answers "does a home for this exist at all yet?".
 * Fidelity is tracked by hand in docs/guide-migration.md, keyed on these ids.
 *
 * Usage: node scripts/audit-guide-migration.mjs [--json]
 */

import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = join(dirname(fileURLToPath(import.meta.url)), "..");
const OLD = join(ROOT, "frontend/guide/styleguide.html");
const NEW = join(ROOT, "frontend/guide/index.html");

/** Every `id="..."` on a heading/section/article, with its visible label. */
function sections(html) {
  const out = new Map();
  const re = /<(section|article|div|h[1-6])\b([^>]*\bid="([A-Za-z0-9_-]+)"[^>]*)>/g;
  let m;
  while ((m = re.exec(html))) {
    const id = m[3];
    if (out.has(id)) continue;
    // Nearest heading text after the tag, for a human-readable label.
    const after = html.slice(m.index, m.index + 1200);
    const h = after.match(/<h[1-6][^>]*>([\s\S]{0,120}?)<\/h[1-6]>/);
    const label = h ? h[1].replace(/<[^>]*>/g, "").replace(/\s+/g, " ").trim() : "";
    out.set(id, label);
  }
  return out;
}

/** Class names actually used for demos, e.g. ml-chip, sg-card — the real surface. */
function classes(html, prefix) {
  const out = new Set();
  const re = new RegExp(`\\b${prefix}[a-z0-9-]*(?:__[a-z0-9-]+)?`, "g");
  for (const c of html.match(re) || []) out.add(c.split("--")[0]);
  return out;
}

const oldHtml = readFileSync(OLD, "utf8");
const newHtml = readFileSync(NEW, "utf8");

const oldSec = sections(oldHtml);
const newSec = sections(newHtml);

// Old ids are prefixed by area (f- foundations, c- components, na- native,
// vd- visual design, p- pages, ml- musiclinkd blocks…). New ids are c-*.
// Match on the normalised tail so `na-otp` finds `c-otp`.
const norm = (id) => id.replace(/^(f|c|c2|na|vd|ml|p|p2|pat|bl|adv|util|sc|cd|pr|db|mp|fm|lay|sr|cr)-/, "").replace(/-/g, "");
const newByNorm = new Map([...newSec.keys()].map((id) => [norm(id), id]));

// The v9 rebuild renamed as it went, so a tail match alone reports renames as
// gaps — it claimed 254 missing when most of those had a home under a new
// name. Every entry below was checked by opening both sections. A rename is
// NOT a migration: it only means a home exists. Fidelity is a separate column
// in docs/guide-migration.md.
//
// The value is the new id, or one of:
//   "#<section>" — folded into a prose section rather than its own component
//   null         — deliberately dropped, with the reason in DROPPED
const ALIAS = {
  // components renamed in the rebuild
  "c-btns": "c-button", "c-btn-families": "c-button", "anatomy-btn": "c-artist-card",
  "c-inp": "c-field", "c-srch": "c-search", "c-chips": "c-chip",
  "c-badges": "c-badge", "c-audio": "c-audio-player", "c-empty": "c-empty-state",
  "c-dialogs": "c-dialog", "c-nav": "c-nav-app-bar",
  "c2-dropdown": "c-combobox", "c2-sidesheet": "c-drawer",
  "c2-skeleton": "c-loading", "c2-spinner": "c-loading",
  "c2-tabs": "c-scrollable-category-tabs", "c2-supporting": "c-field",
  "c2-multichip": "c-chip", "c2-m3navbar": "c-bottom-tab-bar",
  "fm-checkbox": "c-checkbox-radio", "fm-radio": "c-checkbox-radio",
  "fm-textarea": "c-field", "fm-toggle": "c-switch", "fm-upload": "c-media-upload-grid",
  "na-snackbar": "c-toast", "na-fab": "c-floating-action-button",
  "na-segmented": "c-segmented-control", "na-topbar": "c-top-app-bar",
  "na-tabs": "c-scrollable-category-tabs", "na-datepicker": "c-calendar",
  "na-rail": "c-section-header-rail", "na-progress": "c-step-progress",
  "na-camera": "c-camera-picker", "na-audio-rec": "c-audio-recorder",
  "na-permission": "c-permission-dialog", "na-filter-sheet": "c-bottom-sheet",
  "na-scrubber": "c-audio-player", "na-search-suggest": "c-recent-suggested-search-rows",
  "nav-bar": "c-bottom-tab-bar", "nav-bar-alt": "c-nav-app-bar",
  "nav-breadcrumb": "c-breadcrumb", "nav-tabs2": "c-scrollable-category-tabs",
  "nav-switch": "c-switch",
  "sr-global": "c-search", "sr-filters": "c-bottom-sheet", "sr-pills": "c-chip",
  "cd-talent": "c-artist-card", "cd-album": "c-ambient-album-card",
  "sc-feed": "c-feed-post", "sc-comment": "c-chat", "sc-testimonial": "c-testimonial",
  "mixer": "c-stem-mixer", "cr-drop": "c-stem-dropzone", "cr-edit": "c-inline-editor",
  "swipe": "c-swipe-deck", "pattern-swipe-deck": "c-swipe-deck",
  "trust": "c-trust-verification", "bl-trust": "c-trust-verification",
  "pf-header": "c-profile-hero", "pf-avatar": "c-avatar", "pf-stats": "c-stat-grid",
  "db-kpi": "c-metrics", "db-progress": "c-progress-card", "db-charts": "c-trends-table",
  "mp-credits": "c-credit-row", "mp-req": "c-application-card", "mp-status": "c-status",
  "ml-band": "c-band-card", "ml-studio": "c-studio-card", "ml-opp": "c-opportunity-card",
  "ml-application": "c-application-card", "ml-review": "c-rating",
  "ml-marketplace": "c-marketplace-card", "ml-rental": "c-marketplace-card",
  "ml-profile": "c-profile-hero", "ml-msg": "c-chat", "ml-notif": "c-booking-review-notification-rows",
  "ml-calendar": "c-calendar", "ml-booking": "c-order-summary", "ml-audio": "c-audio-player",
  "ml-badges": "c-badge", "ml-cards": "c-card", "ml-project": "c-progress-card",
  "adv-masonry": "c-masonry", "adv-pinterest-masonry": "c-trends-table",
  "adv-blog": "c-article-grid", "adv-features": "c-feature-grid",
  "adv-supercharge-bento": "c-bento", "adv-supercharge-dark": "c-bento",
  "adv-supercharge-split": "c-split-editorial", "adv-dualhero": "c-editorial-layouts",
  "util-skeleton": "c-loading", "util-shimmer": "c-loading", "util-empty": "c-empty-state",
  "util-error": "c-error-boundary", "util-offline": "c-offline-adaptive-banners",
  "util-adaptive": "c-offline-adaptive-banners", "util-qr": "c-qr-share",
  "util-success": "c-animated-success", "util-persistent-audio": "c-audio-player",
  "util-textures": "c-surface-effects",
  "vd-pinterest": "c-masonry", "vd-pinterest-hero": "c-editorial-layouts",
  "vd-snapchat": "c-media-card", "vd-apple-music": "c-artist-card",
  "vd-hero-gradient": "c-media-card", "vd-avatar-stack": "c-avatar",
  "vd-effects": "c-surface-effects", "vd-pricing": "c-pricing-tiers",
  "vd-trust-block": "c-trust-verification", "vd-genz": "c-collage",
  "vd-equalizer": "c-equalizer", "vd-track-card": "c-media-card",
  "vd-chat-list": "c-chat", "vd-msg": "c-chat", "vd-typing": "c-chat",
  "vd-attachments": "c-attachment", "vd-notif": "c-booking-review-notification-rows",
  "vd-booking-summary": "c-order-summary", "vd-time-slot": "c-calendar",
  "vd-success": "c-animated-success", "vd-reviews": "c-rating",
  "vd-write-review": "c-rating", "vd-scorecard": "c-match-score",
  "vd-recent-project": "c-progress-card", "vd-workspace": "c-progress-card",
  "vd-featured-studio": "c-studio-card", "vd-featured-artists": "c-artist-card",
  "vd-categories": "c-category-tile", "vd-quick-actions": "c-what-are-you-looking-for-grid",
  "vd-row-card": "c-list-row", "vd-continue": "c-progress-card",
  "vd-greeting": "c-lede", "vd-insights": "c-metrics", "vd-analytics": "c-trends-table",
  "vd-equipment": "c-marketplace-card", "vd-footer-cta": "c-footer-cta",
  "vd-recommended": "c-artist-card", "vd-more-inspired": "c-surface-effects",
  "vd-blocks": "c-bento", "vd-cards": "c-card", "vd-audio": "c-audio-player",
  "vd-persistent-audio": "c-audio-player", "vd-notif-toggle": "c-switch",
  "bl-audio": "c-audio-player", "bl-stats": "c-stat-grid", "bl-hero": "c-editorial-layouts",
  "bl-profile": "c-profile-hero", "bl-marketplace": "c-marketplace-card",
  "bl-messaging": "c-chat", "bl-booking": "c-order-summary",
  "bl-discovery": "c-artist-card", "bl-dashboard": "c-metrics",
  "gestures-native": "c-native-keyboard-ime", "state-demos": "#foundations",
  "icon-states": "#foundations", "icons-gestalt": "#icons",
  "gestalt-icon-grid": "#icons", "f-icons-ext": "#icons",
  "widget-registry": "#gaps", "widget-registry-list": "#gaps", "not-built": "#gaps",

  // foundations folded into the single #foundations section's tables
  "f-colors": "#foundations", "f-semantic": "#foundations", "f-typo": "#foundations",
  "f-typo-ext": "#foundations", "f-spacing": "#foundations", "f-radius": "#foundations",
  "f-sizing": "#foundations", "f-opacity": "#foundations", "f-elevation": "#foundations",
  "f-elev-levels": "#foundations", "f-border-tk": "#foundations",
  "f-states": "#foundations", "f-zindex": "#foundations", "f-4tier": "#foundations",
  "f-breakpoints": "#gaps", "f-motion-tk": "#motion", "f-motion-principles": "#motion",
  "motion-spec": "#motion", "f-voice": "#brand", "palette": "#foundations",
  "palette-2": "#foundations", "brand-colors": "#foundations",
  "brand-identity": "#brand", "tokens": "#foundations",

  // taxonomy
  "p-catalog": "#taxonomy", "p-personas": "#taxonomy", "p-modules": "#taxonomy",
  "pr-talent": "#taxonomy", "pr-band": "#taxonomy", "pr-biz": "#taxonomy", "pr-venue": "#taxonomy",

  // patterns
  "pat-auth": "#patterns", "pat-onboard": "#patterns", "pat-marketplace": "#patterns",
  "p-onboard": "#patterns", "p-splash": "#patterns", "p-public": "#patterns",
  "p-disc": "#patterns", "p-discover": "#patterns", "p-srch": "#patterns",
  "p-mkt": "#patterns", "p-artist": "#patterns", "p-studio": "#patterns",
  "p-journeys": "#patterns",
};

// Deliberately not carried over, each with the reason. Listing them is the
// point: a dropped section that nobody wrote a reason for is an accident.
const DROPPED = {
  "ai-prompts-library": "Gemini/Midjourney prompt copy — authoring aid, not a system surface",
  prompts: "same prompt library",
  "prompt-artist-card": "same prompt library",
  "prompt-marketplace": "same prompt library",
  "prompt-portfolio": "same prompt library",
  "prompt-studio-booking": "same prompt library",
  "product-vision": "product narrative; belongs in docs/, not the component catalogue",
  "product-overview": "as above",
  strategy: "as above",
  brief: "as above",
  "dev-docs": "duplicated README.md and infrastructure/",
  "util-flow": "Firebase project facts; duplicated infrastructure/firebase-hosting.md",
  "util-workspace": "as above",
  a11y: "folded into per-component prose plus #foundations → Component states",
  "f-a11y": "as above",
  "f-philosophy": "folded into the page's own preamble",
  "f-principles": "as above",
  "f-comp-rules": "as above",
  "f-grid": "superseded by --layout-max / --layout-measure",
  "f-layout": "as above",
  "f-responsive": "superseded by --bp-medium / --bp-expanded",
  more: "nav plumbing, not a section",
};

const missing = [];
const present = [];
const dropped = [];
for (const [id, label] of oldSec) {
  // Skip runtime/demo plumbing ids — they're wiring, not catalogue entries.
  if (/^(toast|sb|ov|content|next-btn|prev-btn|current-section-name|global-search|mob|bs|sideSheet|deckW|tr1|tr2|chatDrawer|dropdownDemo|credAct|bf|wf|sg-)/i.test(id)) continue;
  if (id in DROPPED) { dropped.push({ id, label, why: DROPPED[id] }); continue; }
  const alias = ALIAS[id];
  const hit = alias || newByNorm.get(norm(id));
  // An alias to "#section" is a home, not a component; still counts as landed.
  const landed = hit && (hit.startsWith("#") || newSec.has(hit));
  if (alias && !landed) {
    missing.push({ id, label, mappedTo: null, note: `alias -> ${alias} does not exist in the new guide` });
  } else {
    (landed ? present : missing).push({ id, label, mappedTo: hit || null });
  }
}

const oldClasses = classes(oldHtml, "sg-");
const newClasses = classes(newHtml, "ml-");

const report = {
  old: { file: "frontend/guide/styleguide.html", sections: oldSec.size, sgClasses: oldClasses.size },
  new: { file: "frontend/guide/index.html", sections: newSec.size, mlClasses: newClasses.size },
  migrated: present.length,
  droppedOnPurpose: dropped.length,
  missing: missing.length,
  missingSections: missing.sort((a, b) => a.id.localeCompare(b.id)),
  droppedSections: dropped.sort((a, b) => a.id.localeCompare(b.id)),
  // Stock imagery must not come across; local /assets/img must.
  stockRefsInOld: (oldHtml.match(/https:\/\/[^"']*(unsplash|pexels|placehold|picsum|randomuser|flaticon)[^"']*/g) || []).length,
  stockRefsInNew: (newHtml.match(/https:\/\/[^"']*(unsplash|pexels|placehold|picsum|randomuser|flaticon)[^"']*/g) || []).length,
  localImagesInOld: [...new Set(oldHtml.match(/\/assets\/img\/[^"')\s]+/g) || [])],
  localImagesInNew: [...new Set(newHtml.match(/\/assets\/img\/[^"')\s]+/g) || [])],
};
report.localImagesDroppedInMigration = report.localImagesInOld.filter((p) => !report.localImagesInNew.includes(p));

if (process.argv.includes("--json")) {
  console.log(JSON.stringify(report, null, 2));
} else {
  console.log(`old styleguide : ${report.old.sections} sections`);
  console.log(`new guide      : ${report.new.sections} sections`);
  console.log(`migrated       : ${report.migrated}  (has a home in the new guide)`);
  console.log(`dropped w/ why : ${report.droppedOnPurpose}`);
  console.log(`MISSING        : ${report.missing}\n`);
  for (const m of report.missingSections)
    console.log(`  ${m.id.padEnd(28)} ${m.label || ""}${m.note ? `  [${m.note}]` : ""}`);
  console.log(`\nstock image refs   old=${report.stockRefsInOld}  new=${report.stockRefsInNew}`);
  console.log(`local /assets/img  old=${report.localImagesInOld.length}  new=${report.localImagesInNew.length}`);
  if (report.localImagesDroppedInMigration.length) {
    console.log(`\nlocal images dropped in migration (${report.localImagesDroppedInMigration.length}):`);
    for (const p of report.localImagesDroppedInMigration) console.log(`  ${p}`);
  }
}
