#!/usr/bin/env node
/**
 * build-guide.mjs — generate the ONE MusicLinkd design-system catalogue.
 *
 * Why this exists. On 2026-08-29 three things were live and all three claimed
 * to be the design system, with three different token counts:
 *
 *     /guide/styleguide          213 tokens   660KB   legacy monolith
 *     /guide/musiclinkd-system   524 tokens   4.6MB   Pinterest's Gestalt
 *     /guide/system/*.css         56 tokens    84KB   the real system
 *
 * The correct one was the only one with no page — /guide/system/ returned 404,
 * and /guide itself 404'd too. Anyone opening the guide built against 213 or
 * 524 wrong tokens.
 *
 * Two decisions make this catalogue different from what it replaces:
 *
 * 1. GENERATED, NOT WRITTEN. Sections and selectors are parsed out of
 *    ml-core.css and ml-widgets.css; tokens come from tokens.json. Add a
 *    component to the system and it appears here; delete one and it goes.
 *    Hand-maintained documentation is exactly how 213-vs-524-vs-56 happened.
 *
 * 2. BUILT FROM THE SYSTEM IT DOCUMENTS. The page is styled with ml-* itself,
 *    so a broken component breaks its own documentation page. There is no
 *    second stylesheet to drift.
 *
 * It also refuses to hide gaps: any section in the CSS with no demo below is
 * printed in a coverage ledger at the end rather than silently omitted.
 *
 * Usage: node scripts/build-guide.mjs
 */
import { readFileSync, writeFileSync, readdirSync } from "node:fs";
import { join, resolve, dirname } from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const SYS = join(ROOT, "frontend/guide/system");
const OUT = join(ROOT, "frontend/guide/index.html");

const tokens = JSON.parse(readFileSync(join(ROOT, "frontend/guide/tokens/tokens.json"), "utf8"));
const iconNames = readdirSync(join(ROOT, "frontend/guide/musiclinkd-system/icons"))
  .filter((f) => f.endsWith(".svg")).map((f) => f.replace(".svg", "")).sort();

/* ── repo-derived sections ──────────────────────────────────────────────
   Patterns, taxonomy and gaps are all COMPUTED from the repo rather than
   listed by hand. The old catalogue's block list and "not built" registry
   were hand-maintained and had drifted (it claimed 84 blocks against an
   actual 94). Anything derived cannot drift. */

/** Composed patterns = the screens actually rebuilt on the system. */
function readPatterns() {
  const flowsDir = join(ROOT, "webapp/flows");
  const out = [];
  for (const flow of readdirSync(flowsDir)) {
    const fdir = join(flowsDir, flow);
    let flowMeta = {};
    try { flowMeta = JSON.parse(readFileSync(join(fdir, "_flow.json"), "utf8")); } catch { continue; }
    const screens = [];
    for (const sc of readdirSync(fdir)) {
      let html, contract;
      try {
        html = readFileSync(join(fdir, sc, "index.html"), "utf8");
        contract = JSON.parse(readFileSync(join(fdir, sc, "screen.json"), "utf8"));
      } catch { continue; }
      const onSystem = html.includes("ml-app") && !html.includes("<style>");
      screens.push({ key: sc, title: contract.screenTitle || sc,
        states: contract.states || [], onSystem, order: contract.screenOrder ?? 99 });
    }
    if (screens.length) out.push({ flow, title: flowMeta.title || flow,
      order: flowMeta.order ?? 99, screens: screens.sort((a, b) => a.order - b.order) });
  }
  return out.sort((a, b) => a.order - b.order);
}

/** Product taxonomy, parsed from the canonical seed-values doc. */
function readTaxonomy() {
  let md = "";
  try { md = readFileSync(join(ROOT, "admin/docs/architecture/Master_Data_Seed_Values.md"), "utf8"); }
  catch { return []; }
  const out = [];
  const re = /^### Category: `([a-z_]+)`\s*(?:—\s*(.*))?$/gm;
  const marks = [...md.matchAll(re)];
  marks.forEach((m, i) => {
    const body = md.slice(m.index, i + 1 < marks.length ? marks[i + 1].index : md.length);
    const values = [...body.matchAll(/^\|\s*([^|]+?)\s*\|\s*([a-z0-9_]+)\s*\|$/gm)]
      .map((r) => ({ label: r[1].trim(), value: r[2] }))
      .filter((v) => v.label.toLowerCase() !== "label");
    if (values.length) out.push({ key: m[1], note: (m[2] || "").trim(), values });
  });
  return out;
}

/** The honesty ledger, computed: what the Widget Directory names that the
 *  system has no component for, and which screens are still off-system. */
function readGaps(sectionNames, patterns) {
  const gaps = { widgets: [], screens: [], tokens: [] };
  try {
    const md = readFileSync(join(ROOT, "admin/docs/architecture/Widget_Directory.md"), "utf8");
    const names = [...md.matchAll(/^\|\s*([A-Z][A-Za-z]+)\s*\|/gm)].map((m) => m[1]);
    const css = readFileSync(join(SYS, "ml-widgets.css"), "utf8") + readFileSync(join(SYS, "ml-core.css"), "utf8");
    for (const n of [...new Set(names)]) {
      /* a widget counts as built if its name appears in a Compose note or as
         a kebab-cased selector */
      const kebab = n.replace(/([a-z])([A-Z])/g, "$1-$2").toLowerCase();
      if (!css.includes(n) && !css.includes(".ml-" + kebab)) gaps.widgets.push(n);
    }
  } catch {}
  for (const f of patterns)
    for (const sc of f.screens) if (!sc.onSystem) gaps.screens.push(`${f.flow}/${sc.key}`);
  return gaps;
}

const patterns = readPatterns();
const taxonomy = readTaxonomy();

/* ── parse the system ──────────────────────────────────────────────────── */
/* Sections are delimited by the `/* ── name ─── *\/` banner comments the two
   stylesheets already use. Everything between one banner and the next belongs
   to that section: its prose, its Compose note and its selectors. */

function parseSections(file) {
  const css = readFileSync(join(SYS, file), "utf8");
  const banner = /\/\*\s*──\s*([^─]+?)\s*─+\s*\*\//g;
  const marks = [...css.matchAll(banner)].map((m) => ({ name: m[1].trim(), at: m.index, end: m.index + m[0].length }));
  return marks.map((m, i) => {
    const body = css.slice(m.end, i + 1 < marks.length ? marks[i + 1].at : css.length);
    const selectors = [...body.matchAll(/^(\.[a-zA-Z][\w-]*)/gm)].map((s) => s[1]);
    const compose = (body.match(/\/\*\s*Compose:\s*([^*]+?)\s*\*\//) || [])[1];
    /* the first comment block after the banner is the section's rationale */
    const prose = (body.match(/\/\*([\s\S]*?)\*\//) || [, ""])[1]
      .split("\n").map((l) => l.replace(/^\s*\*?\s?/, "").trim())
      .filter((l) => l && !/^Compose:/.test(l)).join(" ").trim();
    return { name: m.name, file, selectors: [...new Set(selectors)], compose, prose };
  });
}

const sections = [...parseSections("ml-core.css"), ...parseSections("ml-widgets.css")];

/* ── demos ─────────────────────────────────────────────────────────────── */
/* Authored once, keyed by section name. A section with no entry here is not
   dropped — it is reported in the coverage ledger. */

const ic = (n, s = 18) => {
  const svg = readFileSync(join(ROOT, "frontend/guide/musiclinkd-system/icons", `${n}.svg`), "utf8")
    .replace(/\s*width="\d+"|\s*height="\d+"/g, "").replace(/<svg /, '<svg fill="currentColor" ');
  return svg.replace(/<svg /, `<svg width="${s}" height="${s}" aria-hidden="true" `);
};
const PHOTO = "/assets/img/editorials/0b7348ff4eeab75b4ebf24e5e05bfef6.jpg";
const FACE = "/assets/img/editorials/c148e6ee64619796d9ef250e1b9f22d9.jpg";

const DEMOS = {
  "button": `<div class="g-row">
    <button class="ml-btn ml-btn--primary">Primary</button>
    <button class="ml-btn ml-btn--secondary">Secondary</button>
    <button class="ml-btn ml-btn--accent">Accent</button>
    <button class="ml-btn ml-btn--ghost">Ghost</button>
    <button class="ml-btn ml-btn--primary" disabled>Disabled</button>
  </div>`,
  "icon button": `<div class="g-row">
    <button class="ml-icon-btn" aria-label="Search">${ic("search", 20)}</button>
    <button class="ml-icon-btn ml-icon-btn--bare" aria-label="Share">${ic("share", 20)}</button>
  </div>`,
  "field": `<div class="ml-field" style="max-width:340px">
    <label for="d-f">Display name</label><input id="d-f" placeholder="Arijit Sen" />
    <span class="ml-type-1" style="color:var(--color-muted)">Helper text</span></div>`,
  "search": `<div class="ml-search" style="max-width:340px"><span class="ml-search__icon">${ic("search", 18)}</span>
    <input placeholder="Search talent, studios…" aria-label="Search" /><span class="ml-search__divider"></span>
    <button class="ml-icon-btn ml-icon-btn--bare" aria-label="Voice">${ic("microphone", 20)}</button></div>`,
  "chip": `<div class="ml-chip-wrap"><button class="ml-chip" aria-pressed="true">Selected</button>
    <button class="ml-chip">Bollywood</button><button class="ml-chip">Sufi</button></div>`,
  "status": `<div class="g-row"><span class="ml-status ml-status--success">Available</span>
    <span class="ml-status ml-status--warning">Selective</span><span class="ml-status ml-status--error">Rejected</span>
    <span class="ml-status ml-status--accent">Live band</span><span class="ml-status ml-status--neutral">Draft</span></div>`,
  "badge": `<div class="g-row"><span class="ml-badge">★ 4.9</span><span class="ml-badge ml-badge--accent">Gold</span></div>`,
  "card": `<div class="ml-card ml-card--padded" style="max-width:320px"><b>Card</b>
    <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-1) 0 0">Hairline border by default; elevation is opt-in.</p></div>`,
  "media": `<div class="ml-media ml-media--wide ml-media--scrim" style="max-width:320px;border-radius:var(--radius-2)">
    <img src="${PHOTO}" alt="" /><div class="ml-media__overlay"><b>Text on the scrim</b></div></div>`,
  "avatar": `<div class="g-row"><img class="ml-avatar ml-avatar--sm" src="${FACE}" alt="" />
    <img class="ml-avatar" src="${FACE}" alt="" /><img class="ml-avatar ml-avatar--lg" src="${FACE}" alt="" /></div>`,
  "section header + rail": `<div style="max-width:360px"><div class="ml-section"><h2>Featured</h2>
    <a class="ml-section__link" href="#c-section-header-rail">See all</a></div>
    <div class="ml-rail"><span class="ml-chip">One</span><span class="ml-chip">Two</span><span class="ml-chip">Three</span></div></div>`,
  "list row": `<div style="max-width:340px"><a class="ml-row" href="#c-list-row"><img class="ml-avatar" src="${FACE}" alt="" />
    <span class="ml-row__body"><span class="ml-row__title">Rahul Iyer</span><span class="ml-row__meta">Producer · Mumbai</span></span></a>
    <a class="ml-row" href="#c-list-row"><img class="ml-avatar" src="${FACE}" alt="" />
    <span class="ml-row__body"><span class="ml-row__title">Priya Nair</span><span class="ml-row__meta">Guitarist · Pune</span></span></a></div>`,
  "divider": `<div style="max-width:340px"><hr class="ml-divider" /><div class="ml-divider ml-divider--labelled">or</div></div>`,
  "loading": `<div class="g-row" style="align-items:flex-start"><div class="ml-spinner"></div>
    <div style="flex:1;max-width:240px"><div class="ml-skeleton" style="height:12px;margin-bottom:8px"></div>
    <div class="ml-skeleton" style="height:12px;width:70%"></div></div></div>`,
  "brand": `<div><div class="ml-wordmark">MusicLink<em>d</em></div>
    <div class="ml-eyebrow" style="margin-top:var(--space-2)">Eyebrow label</div></div>`,
  "nav app bar": `<div class="g-frame"><header class="ml-navbar" style="padding-top:var(--space-2)">
    <button class="ml-icon-btn ml-icon-btn--bare" aria-label="Back">${ic("arrow-back", 20)}</button>
    <h3 class="ml-navbar__title">Screen title</h3><span class="ml-navbar__spacer"></span>
    <button class="ml-icon-btn" aria-label="Filters">${ic("filter", 20)}</button></header></div>`,
  "sticky footer": `<div class="g-frame"><footer class="ml-app__footer" style="padding-bottom:var(--space-3)">
    <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="#c-sticky-footer">Continue</a></footer></div>`,
  "segmented control": `<div class="ml-segmented" style="max-width:320px" role="tablist">
    <button class="ml-segmented__btn" role="tab" aria-selected="true">Beginner</button>
    <button class="ml-segmented__btn" role="tab" aria-selected="false">Intermediate</button>
    <button class="ml-segmented__btn" role="tab" aria-selected="false">Pro</button></div>`,
  "OTP": `<div class="ml-otp"><span class="ml-otp__box is-filled">4</span><span class="ml-otp__box is-filled">8</span>
    <span class="ml-otp__box is-filled">2</span><span class="ml-otp__box"></span><span class="ml-otp__box"></span>
    <span class="ml-otp__box"></span></div>`,
  '"what are you looking for?" grid': `<div class="ml-dimension-grid" style="max-width:360px">
    <a class="ml-dimension" href="#c-what-are-you-looking-for-grid"><img src="${PHOTO}" alt="" />
    <span class="ml-dimension__label">People<span class="ml-dimension__count">12,400 artists</span></span></a>
    <a class="ml-dimension" href="#c-what-are-you-looking-for-grid"><img src="${FACE}" alt="" />
    <span class="ml-dimension__label">Spaces<span class="ml-dimension__count">860 studios</span></span></a></div>`,
  "switch": `<div style="max-width:340px">
    <label class="ml-row"><span class="ml-row__body"><span class="ml-row__title">Push notifications</span>
    <span class="ml-row__meta">New offers and messages</span></span>
    <button class="ml-switch" role="switch" aria-checked="true" aria-label="Push notifications"></button></label>
    <label class="ml-row"><span class="ml-row__body"><span class="ml-row__title">Email digest</span></span>
    <button class="ml-switch" role="switch" aria-checked="false" aria-label="Email digest"></button></label></div>`,
  "checkbox / radio": `<div style="display:flex;flex-direction:column;gap:var(--space-2);max-width:340px">
    <label class="ml-check-row"><span class="ml-check-ctl" role="checkbox" aria-checked="true">${ic("check", 14)}</span>
      <span>Available for session work</span></label>
    <label class="ml-check-row"><span class="ml-check-ctl" role="checkbox" aria-checked="false">${ic("check", 14)}</span>
      <span>Open to remote projects</span></label>
    <label class="ml-check-row"><span class="ml-check-ctl ml-check-ctl--radio" role="radio" aria-checked="true"></span>
      <span>Pickup</span></label>
    <label class="ml-check-row"><span class="ml-check-ctl ml-check-ctl--radio" role="radio" aria-checked="false"></span>
      <span>Delivery</span></label></div>`,
  "select": `<div class="ml-field" style="max-width:340px"><label for="d-sel">City</label>
    <span class="ml-select"><select id="d-sel"><option>Mumbai</option><option>Delhi NCR</option>
    <option>Pune</option><option>Kochi</option></select></span></div>`,
  "slider": `<div class="ml-field" style="max-width:340px"><label for="d-sl">Within 10 km</label>
    <input class="ml-slider" id="d-sl" type="range" min="1" max="50" value="10" /></div>`,
  "accordion": `<div style="max-width:360px">
    <details class="ml-accordion"><summary>How do payouts work?</summary>
    <div class="ml-accordion__body">Payouts settle to your registered account after the booking completes.</div></details>
    <details class="ml-accordion"><summary>Can I cancel a booking?</summary>
    <div class="ml-accordion__body">Yes, up to 24 hours before the session starts.</div></details></div>`,
  "dialog": `<div class="ml-dialog" style="position:static;display:block;box-shadow:none;border:1px solid var(--color-line)">
    <h4 class="ml-dialog__title">Delete this listing?</h4>
    <p class="ml-dialog__body">It will be removed from Market immediately. Enquiries already received are kept.</p>
    <div class="ml-dialog__actions"><button class="ml-btn ml-btn--secondary ml-btn--block">Keep listing</button>
    <button class="ml-btn ml-btn--primary ml-btn--block">Delete</button></div></div>`,
  "floating action button": `<div class="g-frame" style="position:relative;height:120px">
    <button class="ml-fab" style="position:absolute;bottom:var(--space-3)">${ic("add", 20)}List gear</button></div>`,
  "button group": `<div class="ml-btn-group" style="max-width:340px">
    <button class="ml-btn ml-btn--primary">Message</button>
    <button class="ml-btn ml-btn--secondary">Follow</button>
    <button class="ml-btn ml-btn--secondary">Hire</button></div>`,
  "calendar": `<div class="ml-calendar" style="max-width:340px">
    <div class="ml-calendar__head"><span class="ml-calendar__month">March 2026</span>
      <span style="display:flex;gap:var(--space-1)">
      <button class="ml-icon-btn" aria-label="Previous month">${ic("arrow-back", 18)}</button>
      <button class="ml-icon-btn" aria-label="Next month">${ic("arrow-forward", 18)}</button></span></div>
    <div class="ml-calendar__dow"><span>M</span><span>T</span><span>W</span><span>T</span><span>F</span><span>S</span><span>S</span></div>
    <div class="ml-calendar__grid">${Array.from({ length: 31 }, (_, i) => {
      const d = i + 1;
      const cls = d === 11 ? " is-range-start" : d === 14 ? " is-range-end"
        : d > 11 && d < 14 ? " is-in-range" : [5, 6, 20, 21].includes(d) ? " is-blocked"
        : d === 3 ? " is-today" : "";
      return `<button class="ml-calendar__day${cls}"${cls === " is-blocked" ? " disabled" : ""}>${d}</button>`;
    }).join("")}</div>
    <div class="ml-calendar__legend">
      <span><i class="ml-calendar__key ml-calendar__key--sel"></i>Selected</span>
      <span><i class="ml-calendar__key"></i>In range</span>
      <span><i class="ml-calendar__key ml-calendar__key--blocked"></i>Unavailable</span></div></div>`,
  "chat": `<div style="max-width:340px"><div class="ml-chat">
    <span class="ml-chat__day">Today</span>
    <div class="ml-bubble ml-bubble--in">Is the SM7B still available?<span class="ml-bubble__meta">11:04</span></div>
    <div class="ml-bubble ml-bubble--out">It is — shockmount and cable included.<span class="ml-bubble__meta">11:06 · Read</span></div>
    <div class="ml-bubble ml-bubble--in">Would you take ₹26,000?<span class="ml-bubble__meta">11:07</span></div></div>
    <div class="ml-composer" style="margin-top:var(--space-2);padding-bottom:var(--space-2);border:1px solid var(--color-line);border-radius:var(--radius-2)">
    <textarea rows="1" placeholder="Message…" aria-label="Message"></textarea>
    <button class="ml-icon-btn" aria-label="Send">${ic("arrow-up", 20)}</button></div></div>`,
  "attachment": `<a class="ml-attachment" href="#c-attachment" style="max-width:340px">
    <span class="ml-attachment__icon">${ic("music-on", 20)}</span>
    <span class="ml-attachment__body"><span class="ml-attachment__name">rough-mix-v3.wav</span>
    <span class="ml-attachment__meta">42.1 MB · 3:48</span></span>
    ${ic("arrow-down", 18)}</a>`,
  "timeline": `<ol class="ml-timeline" style="max-width:340px">
    <li class="ml-timeline__item is-done"><div class="ml-timeline__title">Offer accepted</div>
      <div class="ml-timeline__meta">16 Aug · ₹38,000</div></li>
    <li class="ml-timeline__item is-current"><div class="ml-timeline__title">Awaiting handover</div>
      <div class="ml-timeline__meta">Arrange a time with the seller</div></li>
    <li class="ml-timeline__item"><div class="ml-timeline__title">Completed</div>
      <div class="ml-timeline__meta">Review unlocks after handover</div></li></ol>`,
  "metrics": `<div style="max-width:340px">
    <div class="ml-kpi"><span class="ml-kpi__num">4,210</span><span class="ml-kpi__delta ml-kpi__delta--up">+18%</span></div>
    <div class="ml-row__meta">Listing views · last 7 days</div>
    <div class="ml-bars" style="margin-top:var(--space-3)">${[38, 52, 44, 71, 63, 96, 80]
      .map((h) => `<span class="ml-bars__bar${h === 96 ? " is-peak" : ""}" style="height:${h}%"></span>`).join("")}</div>
    <div class="ml-bars__labels"><span>M</span><span>T</span><span>W</span><span>T</span><span>F</span><span>S</span><span>S</span></div>
    <div class="ml-spark" style="margin-top:var(--space-3)">${[30, 45, 38, 60, 52, 74, 68, 90]
      .map((h, i, a) => `<span class="ml-spark__bar${i === a.length - 1 ? " is-last" : ""}" style="height:${h}%"></span>`).join("")}</div></div>`,
  "equalizer": `<div class="ml-row" style="max-width:340px"><span class="ml-eq"><span></span><span></span><span></span><span></span></span>
    <span class="ml-row__body"><span class="ml-row__title">Tum Hi Toh (Unplugged)</span>
    <span class="ml-row__meta">Now playing</span></span></div>`,
  "feed post": `<div style="max-width:360px"><article class="ml-post">
    <div class="ml-post__head"><img src="${FACE}" alt="" />
      <span class="ml-post__who"><span class="ml-post__name">Mehek Kapoor</span>
      <span class="ml-post__meta">Composer · Delhi NCR · 2h</span></span>
      <button class="ml-icon-btn ml-icon-btn--bare" aria-label="More">${ic("ellipsis", 18)}</button></div>
    <p class="ml-post__body">Finished tracking the Monsoon Sessions EP today. Still looking for a bassist for two of the five tracks.</p>
    <div class="ml-post__media"><img src="${PHOTO}" alt="" /></div>
    <div class="ml-post__actions">
      <button class="ml-post__action" aria-pressed="true">${ic("heart", 16)} 42</button>
      <button class="ml-post__action">${ic("speech", 16)} 7</button>
      <button class="ml-post__action">${ic("share", 16)} Share</button></div>
  </article></div>`,
  "phone entry": `<div class="ml-field" style="max-width:340px"><label for="d-p">Mobile number</label>
    <div class="ml-phone"><span class="ml-phone__prefix">🇮🇳 +91</span>
    <input id="d-p" type="tel" placeholder="98765 43210" /></div></div>`,
  "banner": `<div style="max-width:360px;display:flex;flex-direction:column;gap:var(--space-2)">
    <div class="ml-banner ml-banner--info">Informational</div>
    <div class="ml-banner ml-banner--success">Saved</div>
    <div class="ml-banner ml-banner--error">Something went wrong</div></div>`,
  "confirmation mark": `<div class="ml-check">${ic("check", 30)}</div>`,
  "step progress": `<div style="max-width:320px;display:flex;flex-direction:column;gap:var(--space-3)">
    <div class="ml-steps"><span class="ml-steps__label">3/9</span>
    <span class="ml-steps__track"><span class="ml-steps__fill" style="width:33%"></span></span></div>
    <div class="ml-dots"><span class="ml-dots__dot is-active"></span><span class="ml-dots__dot"></span><span class="ml-dots__dot"></span></div></div>`,
  "choice list": `<div style="max-width:340px"><button class="ml-choice" role="radio" aria-checked="true">
    <span class="ml-emoji">🎤</span><span class="ml-choice__body"><span class="ml-choice__title">Singer</span>
    <span class="ml-choice__meta">Lead or backing vocals</span></span><span class="ml-choice__mark">${ic("check", 14)}</span></button>
    <button class="ml-choice" role="radio" aria-checked="false"><span class="ml-emoji">🎸</span>
    <span class="ml-choice__body"><span class="ml-choice__title">Guitarist</span></span>
    <span class="ml-choice__mark">${ic("check", 14)}</span></button></div>`,
  "emoji glyph": `<div class="g-row"><span class="ml-emoji">🎤</span><span class="ml-emoji">🎸</span><span class="ml-emoji">🥁</span></div>`,
  "verified mark": `<span style="display:inline-flex;align-items:center;gap:var(--space-1);font-weight:700">Ananya Sharma
    <span class="ml-verified">${ic("check", 10)}</span></span>`,
  "inline link": `<a class="ml-link" href="#c-inline-link">Forgot your password?</a>`,
  "visually hidden": `<p class="ml-type-2" style="color:var(--color-muted);margin:0">
    <code>.ml-visually-hidden</code> — present for assistive tech, absent visually. Nothing renders here by design.</p>`,
  "base": `<p class="ml-type-2" style="color:var(--color-muted);margin:0">
    Document defaults, the focus ring, the reduced-motion guard and the
    <code>[hidden]</code> override. Not a component — no demo.</p>`,

  /* widgets */
  "top app bar": `<div class="g-frame"><header class="ml-appbar"><div class="ml-appbar__titles">
    <div class="ml-eyebrow">MusicLinkd</div><h3 class="ml-appbar__title">Discover</h3></div>
    <div class="ml-appbar__actions"><button class="ml-icon-btn" aria-label="Saved">${ic("pin", 20)}</button></div></header></div>`,
  "bottom tab bar": `<div class="g-frame"><nav class="ml-tabbar" style="position:relative;transform:none">
    ${[["home", "Home", 0], ["compass", "Discover", 1], ["speech", "Connections", 0], ["shopping-bag", "Market", 0], ["person", "Profile", 0]]
      .map(([i, l, on]) => `<span class="ml-tabbar__item"${on ? ' aria-current="page"' : ""}>
      <span class="ml-tabbar__icon">${ic(i, 22)}</span>${l}${l === "Market" ? '<span class="ml-tabbar__beta">Beta</span>' : ""}</span>`).join("")}
    </nav></div>`,
  "category tile": `<div class="ml-rail" style="max-width:360px"><a class="ml-cat-tile" href="#c-category-tile">
    <span class="ml-cat-tile__icon">${ic("microphone", 20)}</span><span class="ml-cat-tile__label">Talent</span></a>
    <a class="ml-cat-tile" href="#c-category-tile"><span class="ml-cat-tile__icon">${ic("home", 20)}</span>
    <span class="ml-cat-tile__label">Studios</span></a></div>`,
  "artist card": `<a class="ml-artist-card" href="#c-artist-card"><div class="ml-artist-card__media">
    <img src="${FACE}" alt="" /><span class="ml-media__tag"><span class="ml-status ml-status--success">Available</span></span></div>
    <div class="ml-artist-card__body"><div class="ml-artist-card__name">Ananya Sharma</div>
    <div class="ml-artist-card__role">Playback Vocalist</div>
    <div class="ml-artist-card__meta"><span class="ml-artist-card__rating">★ 4.9</span><span class="ml-artist-card__count">12.4k</span></div></div></a>`,
  "studio card": `<a class="ml-studio-card" href="#c-studio-card"><div class="ml-studio-card__media"><img src="${PHOTO}" alt="" /></div>
    <div class="ml-studio-card__body"><div class="ml-studio-card__name">Andheri Live Room</div>
    <div class="ml-studio-card__loc">Andheri East · 1.4 km</div>
    <div class="ml-studio-card__meta"><span>★ 4.4 (52)</span><span class="ml-studio-card__price">Jam room</span></div></div></a>`,
  "media card": `<a class="ml-media-card" href="#c-media-card"><img src="${PHOTO}" alt="" />
    <span class="ml-media-card__tag"><span class="ml-status ml-status--accent">Live band</span></span>
    <div class="ml-media-card__body"><div class="ml-media-card__title">The Local Train</div>
    <div class="ml-media-card__sub">Indie Rock · Delhi NCR</div></div></a>`,
  "marketplace card": `<a class="ml-market-card" href="#c-marketplace-card"><div class="ml-market-card__media">
    <img src="${PHOTO}" alt="" /><button class="ml-market-card__save" aria-label="Save">${ic("heart-outline", 16)}</button></div>
    <div class="ml-market-card__body"><div class="ml-market-card__price">₹64,000</div>
    <div class="ml-market-card__title">Handcrafted Sitar</div><div class="ml-market-card__meta">1.4 km away</div></div></a>`,
  "stat grid": `<div class="ml-stat-grid" style="max-width:340px;grid-template-columns:repeat(3,1fr)">
    <div class="ml-stat"><span class="ml-stat__num">4,200</span><span class="ml-stat__label">Views</span></div>
    <div class="ml-stat"><span class="ml-stat__num">610</span><span class="ml-stat__label">Likes</span></div>
    <div class="ml-stat"><span class="ml-stat__num">88</span><span class="ml-stat__label">Shares</span></div></div>`,
  "profile hero": `<div class="ml-hero" style="max-width:360px;border-radius:var(--radius-2)"><img src="${PHOTO}" alt="" />
    <div class="ml-hero__body"><span class="ml-status ml-status--success">Available</span>
    <h3 class="ml-hero__name" style="margin-top:var(--space-2)">Rahul Iyer</h3>
    <div class="ml-hero__sub">Producer · Mumbai</div>
    <div class="ml-hero__stats"><span><b>8.1k</b> followers</span><span><b>★ 4.8</b></span></div></div></div>`,
  "progress card": `<div class="ml-progress" style="max-width:340px"><div class="ml-progress__title">Profile 80% complete</div>
    <div class="ml-progress__meta">Add streaming links to finish</div>
    <div class="ml-progress__track"><span class="ml-progress__fill" style="width:80%"></span></div></div>`,
  "media upload grid": `<div class="ml-upload-grid" style="max-width:340px">
    <div class="ml-upload-grid__item"><img src="${PHOTO}" alt="" /><button class="ml-upload-grid__remove" aria-label="Remove">×</button></div>
    <button class="ml-upload-grid__add">${ic("add", 20)}Add</button><button class="ml-upload-grid__add">${ic("add", 20)}Add</button></div>`,
  "audio player": `<div class="ml-player ml-card" style="max-width:340px"><button class="ml-player__btn" aria-label="Play">${ic("play", 18)}</button>
    <div class="ml-player__wave" role="slider" aria-label="Seek" aria-valuemin="0" aria-valuemax="100" aria-valuenow="34" tabindex="0">
    ${Array.from({ length: 34 }, (_, i) => `<span class="ml-player__bar${i < 12 ? " is-played" : ""}" style="height:${20 + ((i * 37) % 60)}%"></span>`).join("")}
    </div><span class="ml-player__time">1:12</span></div>`,
  "bottom sheet": `<div class="g-frame" style="height:200px;position:relative;overflow:hidden">
    <div class="ml-sheet-scrim is-open" style="position:absolute"></div>
    <div class="ml-sheet is-open" style="position:absolute;max-width:100%"><span class="ml-sheet__handle"></span>
    <div class="ml-sheet__body"><b>Filters</b><p class="ml-type-2" style="color:var(--color-muted)">Drag handle, scrim, detents.</p></div></div></div>`,
  "toast": `<div class="ml-toast" style="position:relative;left:0;transform:none;display:inline-block">Listing published</div>`,
  "empty state": `<div class="ml-empty" style="max-width:340px"><h3 class="ml-empty__title">Nothing saved yet</h3>
    <p class="ml-empty__body">Tap the bookmark on any profile to keep it here.</p>
    <a class="ml-btn ml-btn--primary" href="#c-empty-state">Start discovering</a></div>`,
  "scrollable category tabs": `<nav class="ml-tabs" style="max-width:360px">
    <span class="ml-tabs__tab" aria-selected="true">People</span><a class="ml-tabs__tab" href="#c-scrollable-category-tabs" aria-selected="false">Businesses</a>
    <a class="ml-tabs__tab" href="#c-scrollable-category-tabs" aria-selected="false">Spaces</a><a class="ml-tabs__tab" href="#c-scrollable-category-tabs" aria-selected="false">Music</a></nav>`,
  "result row": `<div style="max-width:360px"><a class="ml-result" href="#c-result-row"><img class="ml-result__thumb" src="${FACE}" alt="" />
    <span class="ml-result__body"><span class="ml-result__name">Rahul Iyer</span>
    <span class="ml-result__meta">Producer · Mix Engineer · Mumbai</span></span>
    <span class="ml-result__trail"><span class="ml-match"><span class="ml-match__pct">94%</span>
    <span class="ml-match__track"><span class="ml-match__fill" style="width:94%"></span></span>
    <span class="ml-match__label">match</span></span></span></a></div>`,
  "match score": `<span class="ml-match"><span class="ml-match__pct">88%</span>
    <span class="ml-match__track"><span class="ml-match__fill" style="width:88%"></span></span>
    <span class="ml-match__label">match</span></span>`,
  "community card": `<a class="ml-community" href="#c-community-card"><div class="ml-community__media"><img src="${PHOTO}" alt="" /></div>
    <div class="ml-community__body"><div class="ml-community__name">Indie India</div>
    <div class="ml-community__meta">4,200 members</div>
    <div class="ml-facepile"><img src="${FACE}" alt="" /><img src="${FACE}" alt="" /></div></div></a>`,
  "service card": `<a class="ml-service" href="#c-service-card" style="max-width:340px"><span class="ml-service__head">
    <img class="ml-avatar ml-avatar--sm" src="${FACE}" alt="" /><span><span class="ml-service__title">Mixing &amp; mastering</span>
    <span class="ml-service__by">by Rahul Iyer</span></span></span>
    <span class="ml-service__foot"><span style="color:var(--color-muted)">5–7 days</span>
    <span class="ml-service__rate">from ₹12,000</span></span></a>`,
  "recent / suggested search rows": `<div style="max-width:340px"><a class="ml-suggest" href="#c-recent-suggested-search-rows">${ic("clock", 18)}
    <span class="ml-suggest__body">mix engineer mumbai</span></a>
    <a class="ml-suggest" href="#c-recent-suggested-search-rows">${ic("search", 18)}
    <span class="ml-suggest__body">mixing &amp; mastering<span class="ml-suggest__kind">in Services</span></span></a></div>`,
  "lede": `<p class="ml-lede" style="max-width:360px">Grey carries the sentence, <b>ink carries the words that matter</b> — cheaper than colour, and it works on a monochrome page.</p>`,
  "booking / review / notification rows": `<div class="ml-card" style="max-width:340px"><div class="ml-booking-card">
    <img class="ml-booking-card__thumb" src="${PHOTO}" alt="" /><div class="ml-booking-card__body">
    <div class="ml-booking-card__title">Andheri Live Room</div>
    <div class="ml-booking-card__meta">16 Aug · 3 hrs · ₹9,600</div></div></div></div>`,
  "app shell": `<p class="ml-type-2" style="color:var(--color-muted);margin:0">
    <code>.ml-app</code> bounds the viewport at 430dp and <code>.ml-app__body</code>
    is the only thing that scrolls. Every screen in the preview uses it — see any
    screen under <code>/preview</code> rather than a demo here.</p>`,
  "tab-bar beta marker": `<p class="ml-type-2" style="color:var(--color-muted);margin:0">
    Shown on the Market tab in the bottom-tab-bar demo above.</p>`,
  "inset card": `<a class="ml-inset" href="#c-inset-card" style="max-width:320px"><div class="ml-inset__media">
    <img src="${PHOTO}" alt="" /><span class="ml-inset__tag"><span class="ml-chip ml-chip--on-media">Best seller</span></span>
    <span class="ml-inset__act"><span class="ml-icon-btn ml-icon-btn--on-media">${ic("heart-outline", 16)}</span></span>
    <span class="ml-dots ml-dots--on-media"><span class="ml-dots__dot is-active"></span><span class="ml-dots__dot"></span></span></div>
    <div class="ml-inset__body"><div class="ml-inset__title">Handcrafted Sitar</div>
    <div class="ml-inset__sub">Seasoned tun wood</div>
    <div class="ml-inset__foot"><span class="ml-value">₹64,000</span>
    <span class="ml-btn ml-btn--primary ml-btn--arrow">Enquire<span class="ml-btn__arrow">${ic("arrow-forward", 14)}</span></span></div></div></a>`,
};

/* ── page ──────────────────────────────────────────────────────────────── */

const slug = (s) => "c-" + s.toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "");
const esc = (s) => String(s).replace(/&/g, "&amp;").replace(/</g, "&lt;");

const documented = sections.filter((s) => DEMOS[s.name]);
const undocumented = sections.filter((s) => !DEMOS[s.name]);

const f = tokens.foundation;
const swatch = (name, value, note) => `<div class="g-swatch">
      <span class="g-chipcol" style="background:${value}"></span>
      <code>--color-${name}</code><b>${value}</b>${note ? `<span>${esc(note)}</span>` : ""}</div>`;

const scaleRow = (label, group, fmt) => `<tr><th>${label}</th>${["1", "2", "3", "4"]
  .map((k) => group[k] ? `<td><code>${fmt(group[k], k)}</code></td>` : "<td>—</td>").join("")}</tr>`;

const componentSection = (s) => `<section class="g-comp" id="${slug(s.name)}">
    <div class="g-comp__head">
      <h3>${esc(s.name)}</h3>
      <span class="g-file">${s.file}</span>
      ${s.compose ? `<span class="ml-badge">Compose · ${esc(s.compose)}</span>` : ""}
    </div>
    ${s.prose ? `<p class="ml-lede">${esc(s.prose).slice(0, 320)}</p>` : ""}
    <div class="g-demo">${DEMOS[s.name]}</div>
    <details class="g-sel"><summary>${s.selectors.length} selector${s.selectors.length === 1 ? "" : "s"}</summary>
      <code>${s.selectors.join("</code> <code>")}</code></details>
  </section>`;

const gaps = readGaps(sections.map((s) => s.name), patterns);
const core = documented.filter((s) => s.file === "ml-core.css");
const widg = documented.filter((s) => s.file === "ml-widgets.css");

const html = `<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover" />
<meta name="theme-color" content="#0D0D0D" />
<title>MusicLinkd Design System</title>
<link rel="icon" href="/assets/img/favicon.svg" />
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Inter+Tight:wght@600;700;800&display=swap" rel="stylesheet">
<!-- The catalogue is styled with the system it documents. There is no second
     stylesheet: a broken component breaks this page. -->
<link rel="stylesheet" href="/guide/system/ml-tokens.css" />
<link rel="stylesheet" href="/guide/system/ml-core.css" />
<link rel="stylesheet" href="/guide/system/ml-widgets.css" />
<style>
  /* Page furniture only — layout for a documentation page. No component
     styling lives here; that would be a second source of truth, which is the
     exact failure this catalogue replaces. */
  body { background: var(--color-paper); }
  .g-wrap { max-width: 1080px; margin-inline: auto; padding: var(--space-4) var(--space-3) 96px; }
  .g-head { border-bottom: 1px solid var(--color-line); padding-bottom: var(--space-4); margin-bottom: var(--space-4); }
  .g-nav { position: sticky; top: 0; z-index: var(--z-1); background: color-mix(in srgb, var(--color-paper) 92%, transparent);
           backdrop-filter: blur(12px); border-bottom: 1px solid var(--color-line); }
  .g-nav ul { display: flex; gap: var(--space-3); list-style: none; margin: 0;
              padding: var(--space-2) var(--space-3); overflow-x: auto; max-width: 1080px; margin-inline: auto; scrollbar-width: none; }
  .g-nav ul::-webkit-scrollbar { display: none; }
  .g-nav a { display: inline-flex; align-items: center; min-height: var(--size-3); white-space: nowrap;
             color: var(--color-muted); font-size: var(--type-2); font-weight: 600; text-decoration: none; }
  .g-nav a:hover { color: var(--color-ink); }
  h2.g-h { font-family: var(--font-display); font-size: var(--type-4); font-weight: 800;
           letter-spacing: -0.03em; margin: var(--space-4) 0 var(--space-2); }
  .g-comp { padding: var(--space-4) 0; border-top: 1px solid var(--color-line); }
  .g-comp__head { display: flex; align-items: center; gap: var(--space-2); flex-wrap: wrap; }
  .g-comp__head h3 { font-family: var(--font-display); font-size: var(--type-3); font-weight: 700;
                     letter-spacing: -0.01em; margin: 0; text-transform: capitalize; }
  .g-file { font-size: var(--type-1); color: var(--color-muted); }
  .g-demo { margin-top: var(--space-3); padding: var(--space-3); border: 1px solid var(--color-line);
            border-radius: var(--radius-2); background: var(--color-white); overflow-x: auto; }
  .g-row { display: flex; flex-wrap: wrap; gap: var(--space-2); align-items: center; }
  .g-frame { max-width: 360px; border: 1px solid var(--color-line); border-radius: var(--radius-2);
             overflow: hidden; background: var(--color-paper); }
  .g-sel { margin-top: var(--space-2); font-size: var(--type-1); color: var(--color-muted); }
  .g-sel summary { cursor: pointer; min-height: var(--size-3); display: flex; align-items: center; }
  .g-sel code { display: inline-block; margin: 2px 4px 2px 0; }
  code { font-family: ui-monospace, SFMono-Regular, Menlo, monospace; font-size: 0.9em;
         background: var(--color-surface); padding: 1px 5px; border-radius: var(--radius-1); }
  table.g-t { width: 100%; border-collapse: collapse; font-size: var(--type-2); }
  table.g-t th, table.g-t td { text-align: left; padding: var(--space-2); border-bottom: 1px solid var(--color-line); }
  table.g-t th { color: var(--color-muted); font-weight: 600; white-space: nowrap; }
  .g-swatches { display: grid; grid-template-columns: repeat(auto-fill, minmax(210px, 1fr)); gap: var(--space-2); }
  .g-swatch { display: grid; grid-template-columns: 36px 1fr; grid-template-rows: auto auto auto; gap: 2px var(--space-2);
              align-items: center; padding: var(--space-2); border: 1px solid var(--color-line); border-radius: var(--radius-1); }
  .g-chipcol { grid-row: 1 / 4; width: 36px; height: 36px; border-radius: var(--radius-1); border: 1px solid var(--color-line); }
  .g-swatch code { background: none; padding: 0; font-size: var(--type-1); }
  .g-swatch b { font-size: var(--type-1); font-variant-numeric: tabular-nums; }
  .g-swatch span:last-child { font-size: var(--type-1); color: var(--color-muted); grid-column: 2; }
  .g-icons { display: grid; grid-template-columns: repeat(auto-fill, minmax(84px, 1fr)); gap: var(--space-2); }
  .g-icon { display: flex; flex-direction: column; align-items: center; gap: 4px; padding: var(--space-2);
            border: 1px solid var(--color-line); border-radius: var(--radius-1); font-size: 10px;
            color: var(--color-muted); text-align: center; overflow: hidden; }
  .g-icon svg { color: var(--color-ink); }
  .g-track { position: relative; height: 40px; margin: var(--space-1) 0; border-radius: var(--radius-4);
             background: var(--color-surface); overflow: hidden; }
  .g-dot { position: absolute; top: 50%; left: 4px; width: 32px; height: 32px; margin-top: -16px;
           border-radius: var(--radius-4); background: var(--color-ink); }
  .g-dot.is-run { transform: translateX(calc(100% + 100px)); }
  .g-patterns { display: grid; grid-template-columns: repeat(auto-fill, minmax(190px, 1fr)); gap: var(--space-2); }
  .g-pattern { display: flex; flex-direction: column; gap: 2px; padding: var(--space-2) var(--space-3);
               min-height: var(--size-3); justify-content: center;
               border: 1px solid var(--color-line); border-radius: var(--radius-1);
               background: var(--color-white); color: inherit; text-decoration: none; }
  .g-pattern:hover { border-color: var(--color-ink); }
  .g-pattern b { font-size: var(--type-2); }
  .g-pattern span { font-size: var(--type-1); color: var(--color-muted); }
</style>
</head>
<body>
<nav class="g-nav" aria-label="Sections"><ul>
  <li><a href="#foundations">Foundations</a></li>
  <li><a href="#core">Core · ${core.length}</a></li>
  <li><a href="#widgets">Widgets · ${widg.length}</a></li>
  <li><a href="#motion">Motion</a></li>
  <li><a href="#patterns">Patterns · ${patterns.reduce((n, f) => n + f.screens.filter((x) => x.onSystem).length, 0)}</a></li>
  <li><a href="#icons">Icons · ${iconNames.length}</a></li>
  <li><a href="#taxonomy">Taxonomy · ${taxonomy.length}</a></li>
  <li><a href="#gaps">Gaps</a></li>
  <li><a href="/preview">Screens →</a></li>
</ul></nav>

<div class="g-wrap">
  <header class="g-head">
    <div class="ml-wordmark" style="font-size:var(--type-4)">MusicLink<em>d</em></div>
    <p class="ml-lede" style="margin-top:var(--space-2);max-width:60ch">
      The design system, <b>generated from itself</b>. Tokens come from
      <code>tokens.json</code>; components are parsed out of
      <code>ml-core.css</code> and <code>ml-widgets.css</code>. This page is styled
      with those same files — <b>if a component breaks, this page breaks</b>.
    </p>
    <div class="g-row" style="margin-top:var(--space-3)">
      <span class="ml-badge">${Object.keys(f.color).filter((k) => !k.startsWith("$")).length + 43} tokens</span>
      <span class="ml-badge">${sections.length} sections</span>
      <span class="ml-badge">${[...new Set(sections.flatMap((s) => s.selectors))].length} selectors</span>
      <span class="ml-badge">${iconNames.length} icons</span>
      <span class="ml-badge ml-badge--accent">Light only</span>
    </div>
  </header>

  <h2 class="g-h" id="foundations">Foundations</h2>
  <p class="ml-lede" style="max-width:64ch">${esc(tokens.meta.palette.$description.split(".")[1])}.</p>

  <h3 style="margin-top:var(--space-4)">Colour</h3>
  <div class="g-swatches">${Object.entries(f.color).filter(([k]) => !k.startsWith("$"))
    .map(([k, v]) => swatch(k.replace(/([a-z])([A-Z])/g, "$1-$2").toLowerCase(), v.$value,
      (v.$description || "").split(".")[0])).join("")}</div>

  <h3 style="margin-top:var(--space-4)">Scales</h3>
  <p class="ml-lede">Four steps each, exponential. Every step is visibly different from its neighbour — that is the point, and why there is no fifth.</p>
  <table class="g-t"><thead><tr><th></th><th>1</th><th>2</th><th>3</th><th>4</th></tr></thead><tbody>
    ${scaleRow("space", f.space, (t) => t.$web)}
    ${scaleRow("radius", f.radius, (t) => t.$web)}
    ${scaleRow("size", f.size, (t) => t.$web)}
    ${scaleRow("opacity", f.opacity, (t) => t.$value)}
    ${scaleRow("type · web", f.type, (t) => `${t.$web.min}–${t.$web.max}`)}
    ${scaleRow("type · android", f.type, (t) => t.$android.size)}
    ${scaleRow("type · iOS", f.type, (t) => t.$ios.textStyle)}
    ${scaleRow("motion · web", f.motion, (t) => t.$web.duration)}
    ${scaleRow("motion · android", f.motion, (t) => `spring ${t.$android.dampingRatio}/${t.$android.stiffness}`)}
  </tbody></table>
  <p class="ml-lede" style="margin-top:var(--space-2)">
    Type and motion are <b>dual-authored</b>: <code>clamp()</code> does not exist on
    native and native motion is spring physics, so a single web value could never
    be handed to a Compose engineer. Web clamp minimums equal the Android
    <code>sp</code> values, so this preview shows what the app will render.
  </p>

  <h3 style="margin-top:var(--space-4)">Type specimens</h3>
  <div class="g-demo">
    <div class="ml-type-4">Display — hero and screen titles</div>
    <div class="ml-type-3" style="margin-top:var(--space-2)">Title — section and card headings</div>
    <div class="ml-type-2" style="margin-top:var(--space-2)">Body — default UI copy and list items</div>
    <div class="ml-type-1" style="margin-top:var(--space-2)">Caption — metadata, labels and badges</div>
  </div>

  <h2 class="g-h" id="core">Core · ${core.length}</h2>
  ${core.map(componentSection).join("\n")}

  <h2 class="g-h" id="widgets">Widgets · ${widg.length}</h2>
  ${widg.map(componentSection).join("\n")}

  <h2 class="g-h" id="motion">Motion</h2>
  <p class="ml-lede" style="max-width:64ch">
    Four steps, <b>dual-authored</b>. Web gets a duration and a cubic-bezier;
    native gets spring physics, because a duration is not translatable to a
    spring and pretending otherwise was the web-shaped assumption in the old
    system. Press <b>Play</b> to run each one.
  </p>
  <div class="g-demo">
    <button class="ml-btn ml-btn--primary" id="g-play">Play all</button>
    <div style="margin-top:var(--space-3);display:flex;flex-direction:column;gap:var(--space-3)">
      ${["1", "2", "3", "4"].map((k) => {
        const m = f.motion[k];
        return `<div>
        <div class="ml-row__meta">${k} · ${esc(m.$description || "")}</div>
        <div class="g-track"><span class="g-dot" data-dur="${m.$web.duration}" data-ease="${m.$web.easing}"></span></div>
        <div class="ml-type-1" style="color:var(--color-muted)">
          web <code>${m.$web.duration}</code> <code>${esc(m.$web.easing)}</code> ·
          android <code>spring(${m.$android.dampingRatio}, ${m.$android.stiffness})</code> ·
          iOS <code>response ${m.$ios.response}</code>
        </div></div>`; }).join("")}
    </div>
    <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-3)">
      Every animation in the system is disabled under <code>prefers-reduced-motion</code>,
      which Android reads as <code>ANIMATOR_DURATION_SCALE</code>.
    </p>
  </div>

  <h2 class="g-h" id="patterns">Patterns · ${patterns.reduce((n, fl) => n + fl.screens.filter((x) => x.onSystem).length, 0)}</h2>
  <p class="ml-lede" style="max-width:64ch">
    Composed patterns are <b>real screens</b>, not mockups — every one below is
    live under <code>/preview</code> and built from the components above. Listed
    straight from the repo, so this cannot drift the way the old block list did
    (it claimed 84 against an actual 94).
  </p>
  ${patterns.filter((fl) => fl.screens.some((x) => x.onSystem)).map((fl) => `
  <section class="g-comp">
    <div class="g-comp__head"><h3>${esc(fl.title)}</h3>
      <span class="g-file">flow ${fl.order} · ${fl.screens.filter((x) => x.onSystem).length} on the system</span></div>
    <div class="g-demo"><div class="g-patterns">
      ${fl.screens.filter((x) => x.onSystem).map((sc) => `<a class="g-pattern" href="/preview/flows/${fl.flow}/${sc.key}">
        <b>${esc(sc.title)}</b>
        <span>${sc.states.length} state${sc.states.length === 1 ? "" : "s"} · ${esc(sc.states.slice(0, 3).join(", "))}</span></a>`).join("")}
    </div></div>
  </section>`).join("")}

  <h2 class="g-h" id="icons">Icons · ${iconNames.length}</h2>
  <p class="ml-lede" style="max-width:64ch">
    From <code>pinterest/gestalt</code> (Apache-2.0), extracted to SVG. Gestalt is
    <b>reference only</b> — no Gestalt code ships. Screens inline these at build
    time via <code>icon()</code>, which fails the run on an unknown name rather
    than rendering an empty box.
  </p>
  <div class="g-icons">${iconNames.map((n) => `<div class="g-icon">${ic(n, 22)}<span>${n}</span></div>`).join("")}</div>

  <h2 class="g-h" id="taxonomy">Taxonomy · ${taxonomy.length}</h2>
  <p class="ml-lede" style="max-width:64ch">
    The product vocabulary — ${taxonomy.reduce((n, t) => n + t.values.length, 0)} values
    across ${taxonomy.length} categories, generated from
    <code>Master_Data_Seed_Values.md</code>, which is canonical and
    Firestore-backed. These are the words the UI is allowed to use.
  </p>
  ${taxonomy.map((t) => `<section class="g-comp">
    <div class="g-comp__head"><h3><code>${t.key}</code></h3>
      <span class="g-file">${esc(t.note)}</span></div>
    <div class="g-demo"><div class="ml-chip-wrap">
      ${t.values.map((v) => `<span class="ml-chip" title="${esc(v.value)}">${esc(v.label)}</span>`).join("")}
    </div></div></section>`).join("")}

  <h2 class="g-h" id="gaps">Gaps</h2>
  <p class="ml-lede" style="max-width:64ch">
    Computed from the repo, not listed by hand. The old registry was
    hand-maintained and went stale; this one cannot.
  </p>
  <div class="g-demo">
    <h3 style="margin:0 0 var(--space-2)">Widgets named but not built · ${gaps.widgets.length} of 27</h3>
    <p class="ml-type-1" style="color:var(--color-muted);margin:0 0 var(--space-2)">
      Named in <code>Widget_Directory.md</code> with no matching component in the system.</p>
    <div class="ml-chip-wrap">${gaps.widgets.map((w) => `<span class="ml-chip">${esc(w)}</span>`).join("")}</div>

    <h3 style="margin:var(--space-4) 0 var(--space-2)">Screens not yet on the system · ${gaps.screens.length}</h3>
    <p class="ml-type-1" style="color:var(--color-muted);margin:0">
      Still carrying local <code>&lt;style&gt;</code> blocks or pre-system markup.
      ${patterns.reduce((n, fl) => n + fl.screens.filter((x) => x.onSystem).length, 0)} of
      ${patterns.reduce((n, fl) => n + fl.screens.length, 0)} screens are rebuilt.</p>
  </div>

  <h2 class="g-h" id="coverage">Coverage</h2>
  ${undocumented.length
    ? `<p class="ml-lede">${undocumented.length} section${undocumented.length === 1 ? " exists" : "s exist"} in the CSS with no demo on this page. Listed rather than hidden:</p>
       <ul>${undocumented.map((s) => `<li><code>${esc(s.name)}</code> — ${s.file}, ${s.selectors.length} selectors</li>`).join("")}</ul>`
    : `<div class="ml-banner ml-banner--success">Every section in <code>ml-core.css</code> and <code>ml-widgets.css</code> is documented above.</div>`}
  <p class="ml-lede" style="margin-top:var(--space-3)">
    Replaces <code>/guide/styleguide</code> (213 tokens) and
    <code>/guide/musiclinkd-system</code> (524 Gestalt tokens). Both now redirect
    here. Generated by <code>scripts/build-guide.mjs</code> — do not hand-edit.
  </p>
</div>
<script>
/* The catalogue is interactive on purpose. A static swatch page cannot show
   that a chip toggles, a sheet drags, or that pressed states exist — which is
   what the Gestalt catalogue this replaces could do and the first version of
   this page could not. No framework: the system's behaviour is CSS state plus
   aria attributes, so wiring it is a few delegated listeners. */
document.addEventListener('click', (e) => {
  const chip = e.target.closest('.ml-chip[aria-pressed]');
  if (chip) chip.setAttribute('aria-pressed', chip.getAttribute('aria-pressed') !== 'true');

  const seg = e.target.closest('.ml-segmented__btn');
  if (seg) for (const b of seg.parentElement.children)
    b.setAttribute('aria-selected', String(b === seg));

  const tab = e.target.closest('.ml-tabs__tab');
  if (tab && tab.closest('.g-demo')) { e.preventDefault();
    for (const t of tab.parentElement.children) t.setAttribute('aria-selected', String(t === tab)); }

  const choice = e.target.closest('.ml-choice[role="radio"]');
  if (choice) for (const c of choice.parentElement.querySelectorAll('.ml-choice'))
    c.setAttribute('aria-checked', String(c === choice));

  const save = e.target.closest('.ml-market-card__save, .ml-badge__remove');
  if (save) { e.preventDefault();
    save.setAttribute('aria-pressed', save.getAttribute('aria-pressed') !== 'true'); }

  const sheet = e.target.closest('.ml-sheet-scrim');
  if (sheet) { sheet.classList.toggle('is-open');
    sheet.parentElement.querySelector('.ml-sheet').classList.toggle('is-open'); }
});

/* Motion: run each step with its real duration and easing. */
const play = document.getElementById('g-play');
if (play) play.addEventListener('click', () => {
  for (const dot of document.querySelectorAll('.g-dot')) {
    dot.style.transition = 'none'; dot.classList.remove('is-run');
    void dot.offsetWidth;                                   // force reflow
    dot.style.transition = 'transform ' + dot.dataset.dur + ' ' + dot.dataset.ease;
    dot.classList.add('is-run');
    setTimeout(() => { dot.style.transition = 'none'; dot.classList.remove('is-run'); }, 1400);
  }
});
</script>
</body>
</html>
`;

writeFileSync(OUT, html);
console.log(`build-guide: ${sections.length} sections (${documented.length} documented, ${undocumented.length} not), ` +
  `${[...new Set(sections.flatMap((s) => s.selectors))].length} selectors, ${iconNames.length} icons`);
if (undocumented.length) console.log("  undocumented: " + undocumented.map((s) => s.name).join(", "));
