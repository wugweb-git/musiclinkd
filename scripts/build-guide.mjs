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
import { readFileSync, writeFileSync, readdirSync, statSync } from "node:fs";
import { join, resolve, dirname } from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = resolve(dirname(fileURLToPath(import.meta.url)), "..");
/* Gestalt ships two optical grids: 398 icons drawn on 24 units and 25 "compact"
   ones drawn on 16. Rendering both at one size makes the compact set read
   heavier, because its strokes were cut for a smaller box. */
const nativeIs16 = (n) => {
  try { return /width="16"/.test(readFileSync(join(ROOT, "frontend/guide/musiclinkd-system/icons", `${n}.svg`), "utf8")); }
  catch { return false; }
};
const SYS = join(ROOT, "frontend/guide/system");
const OUT = join(ROOT, "frontend/guide/index.html");

const tokens = JSON.parse(readFileSync(join(ROOT, "frontend/guide/tokens/tokens.json"), "utf8"));
const iconNames = readdirSync(join(ROOT, "frontend/guide/musiclinkd-system/icons"))
  .filter((f) => f.endsWith(".svg")).map((f) => f.replace(".svg", "")).sort();

/** Real token count, counted from the emitted CSS rather than guessed from
 *  one JSON category plus a fudge factor. The header badge previously read
 *  "foundation.color keys + 43", a stale hardcode that undercounted by 32
 *  tokens (found 2026-08-30) — this recounts every build, so it can't drift
 *  again the same way. */
const cssTokenCount = new Set(
  [...readFileSync(join(SYS, "ml-tokens.css"), "utf8").matchAll(/^\s*(--[a-z0-9-]+):/gm)].map((m) => m[1])
).size;

/** brand.type.weights are the specimen the guide renders; foundation.weight is
 *  what the system can actually express. They were two hand-kept lists, and the
 *  brand one still advertised 700 after the scale dropped it. Resolving the
 *  specimen THROUGH the scale means the guide cannot show a weight no component
 *  can use — the build fails instead. */
const WEIGHT_TOKEN_BY_VALUE = new Map(
  Object.entries(tokens.foundation.weight)
    .filter(([k]) => /^\d+$/.test(k))
    .map(([k, t]) => [t.$value, `weight-${k}`])
);
function weightTokenName(value) {
  const tok = WEIGHT_TOKEN_BY_VALUE.get(value);
  if (!tok) {
    throw new Error(
      `brand.type advertises font-weight ${value}, which is not a step in ` +
        `foundation.weight (${[...WEIGHT_TOKEN_BY_VALUE.keys()].join(", ")}). ` +
        `Add the step or drop the weight — do not render an unnameable weight.`
    );
  }
  return tok;
}

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

/** Brand marks, read from the folder. Each is placed on the ground it was
 *  drawn for: a mark filled white or near-white needs ink behind it, one
 *  filled dark needs paper. Detected from the file rather than a hand-kept
 *  list, so dropping a new variant in the folder is enough to document it. */
function readBrand() {
  const dir = join(ROOT, "frontend/guide/brand/assets");
  let files = [];
  try { files = readdirSync(dir).filter((f) => f.endsWith(".svg")).sort(); } catch { return []; }
  return files.map((f) => {
    const svg = readFileSync(join(dir, f), "utf8");
    const fills = [...svg.matchAll(/fill="(#[0-9A-Fa-f]{3,8}|white|black)"/g)].map((m) => m[1].toLowerCase());
    const light = fills.filter((c) => c === "white" || /^#(f|e)/.test(c)).length;
    const dark  = fills.filter((c) => c === "black" || /^#(0|1|2|3)/.test(c)).length;
    return { file: f, name: f.replace(".svg", "").replace(/-/g, " "),
             onDark: light > dark, bytes: svg.length };
  });
}

const brand = readBrand();
const patterns = readPatterns();
const taxonomy = readTaxonomy();

/* ── parse the system ──────────────────────────────────────────────────── */
/* Sections are delimited by the `/* ── name ─── *\/` banner comments the two
   stylesheets already use. Everything between one banner and the next belongs
   to that section: its prose, its Compose note and its selectors. */

/* ── state mapping ──────────────────────────────────────────────────────
   #foundations defines nine states once, in a table. Until now that table was
   the ONLY place states appeared: no component said which of the nine it
   actually implements. That omission is the direct cause of the flows growing
   a separate screen per state — if the catalogue never says "a chip has
   selected and disabled", a screen author has nowhere to look and builds a
   second screen instead.

   These are DERIVED from each section's own CSS, not listed by hand, for the
   same reason patterns and gaps are: a hand-kept state list is a list that
   goes stale. If a selector for a state is not in the stylesheet, the system
   does not have that state, and the guide must not claim it does.

   `key` matches the state names in the #foundations table verbatim, so the two
   cannot describe different vocabularies.

   A state reaches a component three different ways, and collapsing them into
   a yes/no is how a map like this starts lying. The first version of this did
   exactly that and reported "Focused: 5 of 119" — untrue, because ml-core.css
   line 45 gives EVERY focusable element a ring via :where(a, button, input,
   textarea, select, [tabindex]):focus-visible. It also reported "Loading: 1",
   untrue for the same reason in reverse: .ml-skeleton is a component you
   compose, not a rule each card restates.

     own       the section's own rules implement it
     global    a base rule in ml-core.css already covers it
     composed  another component supplies it (.ml-skeleton, .ml-empty)
     absent    genuinely not available — the only one that is a gap */
const STATE_PROBES = [
  { key: "hover",    label: "Hover",    re: /:hover\b/ },
  { key: "focused",  label: "Focused",  re: /:focus(-visible|-within)?\b/, global: true },
  { key: "pressed",  label: "Pressed",  re: /:active\b/ },
  { key: "selected", label: "Selected", re: /\[aria-(pressed|selected|checked|current)[^\]]*\]|\.is-(selected|active)\b/ },
  { key: "disabled", label: "Disabled", re: /:disabled\b|\[aria-disabled|\.is-disabled\b|\.ml-[\w-]*--disabled\b/ },
  { key: "loading",  label: "Loading",  re: /\.is-loading\b|\[aria-busy/, composedBy: ".ml-skeleton" },
  { key: "error",    label: "Error",    re: /\[aria-invalid|\.is-(error|invalid)\b|--error\b|\.ml-[\w-]*--error\b/ },
  { key: "empty",    label: "Empty",    re: /\.is-empty\b/, composedBy: ".ml-empty" },
  /* Open/expanded is a tenth state the system genuinely has (sheets, drawers,
     accordions, popovers). It was missing from the foundations table, which
     listed nine — so sheets had no documented state at all. */
  { key: "open",     label: "Open",     re: /\.is-open\b|\[aria-expanded[^\]]*\]|\[open\]|::backdrop/ },
];

/* The base layer, read once. ml-core.css's global rules are not in a preamble
   above the first banner — they are INSIDE its first section, which is itself
   called "base". Slicing at the first banner therefore captured 804 bytes of
   file header and found no :focus-visible at all, which is what made the first
   run of this map report focus as absent on 114 of 119 components. The base
   layer is the "base" section plus anything above it. */
const CORE_BASE = (() => {
  const css = readFileSync(join(SYS, "ml-core.css"), "utf8");
  const banners = [...css.matchAll(/\/\*\s*──\s*([^─]+?)\s*─+\s*\*\//g)];
  const base = banners.find((b) => b[1].trim() === "base");
  if (!base) return css.slice(0, banners[0]?.index ?? css.length);
  const next = banners[banners.indexOf(base) + 1];
  return css.slice(0, next ? next.index : css.length);
})();
const GLOBAL_STATES = new Set(
  STATE_PROBES.filter((p) => p.global && p.re.test(CORE_BASE)).map((p) => p.key)
);

/** Which of the ten states this section's own CSS actually implements. */
function statesIn(body) {
  return STATE_PROBES.filter((p) => p.re.test(body)).map((p) => p.key);
}

function parseSections(file) {
  const css = readFileSync(join(SYS, file), "utf8");
  const banner = /\/\*\s*──\s*([^─]+?)\s*─+\s*\*\//g;
  const marks = [...css.matchAll(banner)].map((m) => ({ name: m[1].trim(), at: m.index, end: m.index + m[0].length }));
  return marks.map((m, i) => {
    const body = css.slice(m.end, i + 1 < marks.length ? marks[i + 1].at : css.length);
    const selectors = [...body.matchAll(/^(\.[a-zA-Z][\w-]*)/gm)].map((s) => s[1]);
    const compose = (body.match(/\/\*\s*Compose:\s*([^*]+?)\s*\*\//) || [])[1];
    /* The first comment block after the banner is the section's rationale —
       EXCEPT a heavy "══" divider (FLOW N —, MIGRATION BACKFILL, etc.), which
       introduces the NEXT group, not this one. A section with no prose of its
       own before its rules would otherwise inherit that divider as its own
       description: "empty state" showed "══ FLOW 4 — DISCOVER ══…" as its
       prose on 2026-08-30, with the unbroken ═ line overflowing the page. */
    const comments = [...body.matchAll(/\/\*([\s\S]*?)\*\//g)].map((c) => c[1]);
    const proseSrc = comments.find((c) => !/^\s*═/.test(c)) || "";
    const prose = proseSrc
      .split("\n").map((l) => l.replace(/^\s*\*?\s?/, "").trim())
      .filter((l) => l && !/^Compose:/.test(l)).join(" ").trim();
    return { name: m.name, file, selectors: [...new Set(selectors)], compose, prose,
             states: statesIn(body) };
  });
}

const sections = [...parseSections("ml-core.css"), ...parseSections("ml-widgets.css")];

/* ── demos ─────────────────────────────────────────────────────────────── */
/* Authored once, keyed by section name. A section with no entry here is not
   dropped — it is reported in the coverage ledger. */

/* The icon files carry width/height but NO viewBox, and they are not all the
   same size: 398 are 24x24 and 25 are 16x16. Swapping width/height for a
   smaller number without a viewBox therefore CROPS the path to the top-left
   corner instead of scaling it — which is what every ic() call at a size other
   than the icon's native one was silently doing. Found 2026-08-30 because the
   pricing tick marks rendered as slivers. The viewBox has to come from the
   file's own dimensions, not a constant. */
const ic = (n, s = 18) => {
  const raw = readFileSync(join(ROOT, "frontend/guide/musiclinkd-system/icons", `${n}.svg`), "utf8");
  const nw = (raw.match(/width="(\d+)"/) || [, "24"])[1];
  const nh = (raw.match(/height="(\d+)"/) || [, nw])[1];
  const vb = /viewBox=/.test(raw) ? "" : ` viewBox="0 0 ${nw} ${nh}"`;
  return raw.replace(/\s*width="\d+"|\s*height="\d+"/g, "")
    .replace(/<svg /, `<svg width="${s}" height="${s}"${vb} fill="currentColor" aria-hidden="true" `);
};
/* ── media ─────────────────────────────────────────────────────────────
   Photography is READ FROM DISK at build time, never hardcoded. Six pools,
   each with a different job, because the folders genuinely differ in kind:

     stage      img/6   live performance — artists, crowds, stage light
     editorial  img/4   lifestyle editorial — people, headphones, desks
     object     img/5   3D object renders on transparent ground
     pins       img/inspiration  portrait crops, mixed aspect — mosaic fodder
     faces      img/editorials   portraits already in use across the system
     gear       img/speakers     monitors, hardware, design objects
     video      img/4 *.mp4      real motion, for the video component
     motion     img/6 *.gif      short loops

   Sorted, so a build is reproducible; size-filtered, so thumbnails and 1x1
   trackers never surface. Adding a file to a folder makes it available with
   no edit here, and build-hosting derives its ship-list from the markup this
   produces, so the two cannot drift. */
const IMGROOT = join(ROOT, "frontend/website/assets/img");
const poolOf = (dir, minBytes, re = /\.(jpe?g|png|webp)$/i, maxBytes = Infinity) => {
  let files = [];
  try { files = readdirSync(join(IMGROOT, dir)); } catch { return []; }
  return files.filter((f) => re.test(f))
    .filter((f) => { try { const b = statSync(join(IMGROOT, dir, f)).size; return b >= minBytes && b <= maxBytes; } catch { return false; } })
    .sort().map((f) => `/assets/img/${dir}/${f}`);
};
const MEDIA = {
  stage:     poolOf("6", 150_000),
  editorial: poolOf("4", 120_000),
  object:    poolOf("5", 60_000),
  pins:      poolOf("inspiration", 14_000),
  faces:     poolOf("editorials", 0),
  gear:      poolOf("speakers", 0),
  video:     poolOf("4", 200_000, /\.mp4$/i, 7_000_000),
  motion:    poolOf("6", 200_000, /\.gif$/i),
};
for (const [k, v] of Object.entries(MEDIA)) {
  if (!v.length) throw new Error(`media pool "${k}" is empty — the guide would render placeholder-less`);
}
/* Deterministic pick. Index wraps, so a demo asking for 12 tiles from an
   8-file pool repeats rather than crashing. */
const m = (pool, i = 0) => MEDIA[pool][i % MEDIA[pool].length];
/* Videos are chosen by NAME, not index: the two used here are the songwriting
   session and the banner loop, and an index would silently point at a trainer
   advert the day someone adds a file. Falls back to the first clip. */
const vid = (needle) => MEDIA.video.find((v) => v.toLowerCase().includes(needle.toLowerCase())) || MEDIA.video[0];

const PHOTO = "/assets/img/editorials/0b7348ff4eeab75b4ebf24e5e05bfef6.jpg";
const FACE = "/assets/img/editorials/c148e6ee64619796d9ef250e1b9f22d9.jpg";
const GEAR = "/assets/img/speakers/0159435ccca405aa6d2c59bb4f11947b.jpg";
const GEAR2 = "/assets/img/speakers/39fdf43f680420b44f547c6cb535900b.jpg";

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
  "chip": `<div class="ml-chip-wrap">
    <button class="ml-chip" aria-pressed="true">${ic("check", 16)}Selected</button>
    <button class="ml-chip">${ic("music-on", 16)}Bollywood</button>
    <button class="ml-chip">Sufi</button>
    <button class="ml-chip">Mumbai<span class="ml-chip__dismiss" aria-hidden="true">${ic("cancel", 14)}</span></button>
    <button class="ml-chip">${ic("filter", 16)}Filters</button></div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Press one — pressed is pink (intent), selected is ink (commitment).</p>`,
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
  "brand": `<div class="g-row" style="align-items:center;gap:var(--space-4)">
    <span class="ml-logo ml-logo--lg ml-logo--clear"><img src="/guide/brand/assets/mark-grey.svg" alt="MusicLinkd" /></span>
    <span class="ml-logo ml-logo--lg ml-logo--clear" style="background:var(--color-ink);border-radius:var(--radius-2)">
      <img src="/guide/brand/assets/logo-lockup-white.svg" alt="MusicLinkd" /></span>
    <span class="ml-logo ml-logo--lg ml-logo--clear" style="background:var(--color-ink);border-radius:var(--radius-2)">
      <img src="/guide/brand/assets/logo-lockup-wide-caps.svg" alt="MusicLinkd" /></span>
  </div>
  <p class="ml-type-1 ml-measure" style="color:var(--color-muted);margin-top:var(--space-2)">
    Eight of the nine files are white artwork and only <code>mark-grey</code> is drawn dark, so
    paper gets the mark and ink gets the lockups. Putting a white lockup on paper is not a
    styling slip — the wordmark simply disappears, which is what this demo used to do.</p>
  <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-3) 0 var(--space-1)">Type fallback — never beside the real mark.</p>
  <div class="ml-wordmark">MUSIC<em>LINKD</em></div>
  <div class="ml-eyebrow" style="margin-top:var(--space-2)">Eyebrow label</div>`,
  "nav app bar": `<div class="g-frame"><header class="ml-navbar" style="padding-top:var(--space-2)">
    <button class="ml-icon-btn ml-icon-btn--bare" aria-label="Back">${ic("arrow-back", 20)}</button>
    <h3 class="ml-navbar__title">Screen title</h3><span class="ml-navbar__spacer"></span>
    <button class="ml-icon-btn" aria-label="Filters">${ic("filter", 20)}</button></header></div>`,
  "sticky footer": `<div class="g-frame" style="height:220px;overflow-y:auto;display:flex;flex-direction:column">
    <div style="flex:1;padding:var(--space-3)">
      <p class="ml-type-2" style="margin:0 0 var(--space-3)">Scroll this panel. The footer stays put and the content passes beneath it.</p>
      ${Array.from({length:6},(_,i)=>`<p class="ml-type-1" style="color:var(--color-muted);margin:0 0 var(--space-3)">Paragraph ${i+1} of the scrolling body copy.</p>`).join("")}
    </div>
    <footer class="ml-app__footer" style="padding-bottom:var(--space-3)">
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
    <div class="ml-bubble ml-bubble--in">Would you take ₹26,000?<span class="ml-bubble__meta">11:07</span></div>
    <div class="ml-bubble ml-bubble--in ml-bubble--typing"><span></span><span></span><span></span></div>
    <div class="ml-bubble ml-bubble--out ml-bubble--failed">Sending my card for the Monsoon Sessions gig<span class="ml-bubble__error">${ic("alert", 14)} Not delivered · Tap to retry</span></div></div>
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
      <div class="ml-timeline__meta">Review unlocks after handover</div></li></ol>
  <p class="ml-lede ml-measure" style="margin-top:var(--space-3)">A stage can also fail — added 2026-08-30, the first two states couldn't represent a cancelled or failed step.</p>
  <ol class="ml-timeline" style="max-width:340px">
    <li class="ml-timeline__item is-done"><div class="ml-timeline__title">Offer accepted</div>
      <div class="ml-timeline__meta">16 Aug · ₹38,000</div></li>
    <li class="ml-timeline__item is-failed"><div class="ml-timeline__title">Payment failed</div>
      <div class="ml-timeline__meta">Card declined — buyer notified</div></li></ol>`,
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
  "opportunity card": `<a class="ml-opportunity-card" href="#c-opportunity-card" style="max-width:340px">
    <span class="ml-opportunity-card__head"><span><span class="ml-opportunity-card__title">Bassist for a 5-track indie EP</span>
    <span class="ml-opportunity-card__by">Monsoon Sessions · Mumbai</span></span>
    <span class="ml-status ml-status--success">Paid</span></span>
    <p class="ml-opportunity-card__body">Studio dates in March. Fretless preferred, not required.</p>
    <span class="ml-opportunity-card__foot"><span class="ml-opportunity-card__closes is-soon">Closes in 3 days</span>
    <span class="ml-opportunity-card__budget">₹40,000</span></span></a>`,
  "application card": `<a class="ml-application-card" href="#c-application-card" style="max-width:340px">
    <img class="ml-avatar" src="${FACE}" alt="" />
    <span class="ml-application-card__body"><span class="ml-application-card__title">Bassist — Monsoon Sessions</span>
    <span class="ml-application-card__meta">Applied 12 Aug · portfolio sent</span>
    <span class="ml-application-card__foot"><span class="ml-status ml-status--warning">Shortlisted</span>
    <span class="ml-row__meta">2 of 14</span></span></span></a>`,
  "band card": `<a class="ml-band-card" href="#c-band-card"><div class="ml-band-card__media"><img src="${PHOTO}" alt="" /></div>
    <div class="ml-band-card__body"><div class="ml-band-card__name">The Local Train</div>
    <div class="ml-band-card__meta">Indie Rock · Delhi NCR · 4 members</div>
    <div class="ml-facepile"><img src="${FACE}" alt="" /><img src="${FACE}" alt="" /><img src="${FACE}" alt="" /></div></div></a>`,
  "rating": `<div class="g-row"><span class="ml-rating"><span class="ml-rating__score">4.9</span>
    <span class="ml-rating__stars">★★★★★</span><span class="ml-rating__count">(128)</span></span>
    <span class="ml-rating"><span class="ml-rating__score">4.4</span>
    <span class="ml-rating__stars">★★★★☆</span><span class="ml-rating__count">(52)</span></span></div>`,
  "credit row": `<div style="max-width:340px"><div class="ml-credit"><img class="ml-avatar ml-avatar--sm" src="${FACE}" alt="" />
    <span class="ml-credit__body"><span class="ml-credit__who">Rahul Iyer</span>
    <span class="ml-credit__role">Mix Engineer</span></span>
    <span class="ml-status ml-status--success">Verified</span></div>
    <div class="ml-credit"><img class="ml-avatar ml-avatar--sm" src="${FACE}" alt="" />
    <span class="ml-credit__body"><span class="ml-credit__who">Mehek Kapoor</span>
    <span class="ml-credit__role">Composer</span></span>
    <span class="ml-status ml-status--neutral">Pending</span></div></div>`,
  "order summary": `<div class="ml-summary" style="max-width:340px">
    <div class="ml-summary__row"><span>Studio A · 3 hrs</span><span>₹9,600</span></div>
    <div class="ml-summary__row"><span>Engineer</span><span>₹3,000</span></div>
    <div class="ml-summary__row"><span>Platform fee</span><span>₹640</span></div>
    <div class="ml-summary__row ml-summary__row--total"><span>Total</span><span>₹13,240</span></div>
    <p class="ml-summary__note">Estimate — payment is not collected in this preview.</p></div>`,
  "swipe deck": `<div><div class="ml-deck">
    <div class="ml-deck__card"><img src="${PHOTO}" alt="" /></div>
    <div class="ml-deck__card"><img src="${FACE}" alt="" />
      <div class="ml-deck__body"><div class="ml-deck__name">Ananya Sharma</div>
      <div class="ml-deck__meta">Playback Vocalist · Mumbai</div></div></div></div>
    <div class="ml-deck__actions">
      <button class="ml-deck__btn" aria-label="Skip">${ic("cancel", 20)}</button>
      <button class="ml-deck__btn ml-deck__btn--yes" aria-label="Connect">${ic("check", 20)}</button></div></div>`,
  "time picker": `<div class="ml-slots" style="max-width:340px">
    ${["09:00", "10:00", "11:00", "12:00", "14:00", "15:00", "16:00", "17:00"]
      .map((t, i) => `<button class="ml-slot"${i === 2 ? ' aria-pressed="true"' : ""}${i === 4 ? " disabled" : ""}>${t}</button>`).join("")}</div>`,
  "drawer": `<div class="g-frame" style="position:relative;height:220px;overflow:hidden">
    <div class="ml-drawer is-open" style="position:absolute;padding-top:0">
      <div class="ml-drawer__body"><div class="ml-eyebrow">MusicLinkd</div>
      <a class="ml-row" href="#c-drawer"><span class="ml-row__body"><span class="ml-row__title">Your profile</span></span></a>
      <a class="ml-row" href="#c-drawer"><span class="ml-row__body"><span class="ml-row__title">Settings</span></span></a>
      <a class="ml-row" href="#c-drawer"><span class="ml-row__body"><span class="ml-row__title">Help</span></span></a></div></div></div>`,
  "video": `<div class="g-row" style="align-items:flex-start">
    <div>
      <div class="ml-video" style="max-width:300px">
        <video src="${vid("Songwriting")}" poster="${m("stage",11)}" controls playsinline preload="metadata"
               style="width:100%;display:block" aria-label="Session reel"></video></div>
      <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">With controls — the default. ${MEDIA.video.length} clips in the pool.</p>
    </div>
    <div>
      <div class="ml-video" style="max-width:300px">
        <video src="${vid("banner")}" muted loop playsinline autoplay
               style="width:100%;display:block" aria-label="Ambient loop"></video>
        <span class="ml-video__time">0:08</span></div>
      <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Muted autoplay loop — decorative only, never for content that carries meaning.</p>
    </div>
  </div>`,
  "stem mixer": `<div class="ml-mixer" style="max-width:340px">
    ${[["Vocals", 78], ["Guitar", 62], ["Bass", 55], ["Drums", 70]].map(([n, v], i) => `<div class="ml-mixer__track">
      <span class="ml-mixer__name">${n}</span>
      <input class="ml-slider ml-mixer__level" type="range" min="0" max="100" value="${v}" aria-label="${n} level" />
      <button class="ml-mixer__mute" aria-label="Mute ${n}"${i === 2 ? ' aria-pressed="true"' : ""}>${ic("sound-slash", 14)}</button>
    </div>`).join("")}</div>`,
  "editorial layouts": `<div style="max-width:360px">
    <div class="ml-row__meta">bento</div>
    <div class="ml-bento" style="margin-bottom:var(--space-3)">
      <div class="ml-card ml-card--padded"><b>Featured</b></div>
      <div class="ml-card ml-card--padded"><b>Trending</b></div>
      <div class="ml-card ml-card--padded"><b>Nearby</b></div></div>
    <div class="ml-row__meta">masonry</div>
    <div class="ml-masonry">
      <div class="ml-card"><img src="${PHOTO}" alt="" style="width:100%;display:block" /></div>
      <div class="ml-card ml-card--padded"><b>A note</b></div>
      <div class="ml-card"><img src="${FACE}" alt="" style="width:100%;display:block" /></div>
      <div class="ml-card ml-card--padded"><b>Another</b></div></div></div>`,
  "surface effects": `<div style="max-width:360px">
    <div class="ml-media ml-media--wide" style="border-radius:var(--radius-2);position:relative">
      <img src="${PHOTO}" alt="" />
      <div class="ml-glass" style="position:absolute;left:var(--space-2);right:var(--space-2);bottom:var(--space-2);padding:var(--space-2);border-radius:var(--radius-1)">
        <b>.ml-glass</b> — frosted panel over media</div></div>
    <div class="ml-card ml-card--padded ml-grain ml-lift" style="margin-top:var(--space-2)">
      <b>.ml-grain + .ml-lift</b>
      <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-1) 0 0">Surface only — no coloured glows, because the chrome stays monochrome.</p></div></div>`,
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
  "verified mark": `<span style="display:inline-flex;align-items:center;gap:var(--space-1);font-weight:var(--weight-3)">Ananya Sharma
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
  /* ── migration backfill: present in styleguide.html / Gestalt, absent here
        until 2026-08-30. ─────────────────────────────────────────────── */
  "tooltip": `<div class="g-row">
    <span class="ml-tip" data-tip="Verified by a label or a release credit"><button class="ml-btn ml-btn--secondary">Hover me</button></span>
    <span class="ml-tip ml-tip--below" data-tip="Opens below instead"><button class="ml-btn ml-btn--ghost">Below</button></span>
  </div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Desktop only — a touch screen has no hover. On a phone use Help or a sheet.</p>`,
  "popover": `<div class="ml-pop-anchor">
    <button class="ml-icon-btn" aria-label="More" aria-expanded="true">${ic("ellipsis", 20)}</button>
    <div class="ml-pop" role="menu">
      <button class="ml-pop__item" role="menuitem" aria-checked="true">${ic("check", 16)} Share profile</button>
      <button class="ml-pop__item" role="menuitem">Copy link</button>
      <div class="ml-pop__sep"></div>
      <button class="ml-pop__item ml-pop__item--danger" role="menuitem">Report</button>
    </div></div>
  <div style="height:150px"></div>`,
  "help button": `<div class="g-row" style="align-items:center">
    <span class="ml-type-2">Industry verified</span>
    <span class="ml-pop-anchor"><button class="ml-help" aria-label="What does industry verified mean?" style="position:relative">?</button></span>
  </div>`,
  "combobox": `<div class="ml-combo" style="max-width:340px">
    <div class="ml-field"><label for="d-cb">Primary instrument</label>
      <input id="d-cb" role="combobox" aria-expanded="true" aria-controls="d-cbl"
        aria-autocomplete="list" aria-activedescendant="d-cbo1" value="tab" /></div>
    <span class="ml-visually-hidden" role="status">3 results</span>
    <div class="ml-combo__list" id="d-cbl" role="listbox">
      <div class="ml-combo__opt" role="option" id="d-cbo1" aria-selected="true"><span><mark>Tab</mark>la</span></div>
      <div class="ml-combo__opt" role="option" id="d-cbo2" aria-selected="false"><span><mark>Tab</mark>or</span></div>
      <div class="ml-combo__opt" role="option" id="d-cbo3" aria-selected="false"><span>Guitar (<mark>tab</mark>bed)</span></div>
    </div></div>
  <div style="height:120px"></div>`,
  "breadcrumb": `<nav class="ml-crumbs" aria-label="Breadcrumb">
    <a class="ml-crumbs__item" href="#c-breadcrumb">Market</a>
    <a class="ml-crumbs__item" href="#c-breadcrumb">Audio gear</a>
    <a class="ml-crumbs__item" href="#c-breadcrumb" aria-current="page">Microphones</a></nav>`,
  "pricing tiers": `<div class="ml-tiers" style="max-width:760px">
    <div class="ml-tier"><div class="ml-tier__name">Free</div>
      <div><span class="ml-tier__price">₹0</span> <span class="ml-tier__unit">/month</span></div>
      <ul class="ml-tier__feats">
        <li class="ml-tier__feat">${ic("check", 14)} Profile and portfolio</li>
        <li class="ml-tier__feat">${ic("check", 14)} 3 applications a month</li>
        <li class="ml-tier__feat ml-tier__feat--off">${ic("cancel", 14)} Verified badge</li></ul>
      <button class="ml-btn ml-btn--secondary ml-btn--block">Current plan</button></div>
    <div class="ml-tier ml-tier--featured"><span class="ml-tier__flag">Recommended</span>
      <div class="ml-tier__name">Pro</div>
      <div><span class="ml-tier__price">₹499</span> <span class="ml-tier__unit">/month</span></div>
      <ul class="ml-tier__feats">
        <li class="ml-tier__feat">${ic("check", 14)} Everything in Free</li>
        <li class="ml-tier__feat">${ic("check", 14)} Unlimited applications</li>
        <li class="ml-tier__feat">${ic("check", 14)} Verified badge</li></ul>
      <button class="ml-btn ml-btn--primary ml-btn--block">Upgrade</button></div>
    <div class="ml-tier"><div class="ml-tier__name">Studio</div>
      <div><span class="ml-tier__price">₹1,999</span> <span class="ml-tier__unit">/month</span></div>
      <ul class="ml-tier__feats">
        <li class="ml-tier__feat">${ic("check", 14)} Everything in Pro</li>
        <li class="ml-tier__feat">${ic("check", 14)} Team seats and booking</li></ul>
      <button class="ml-btn ml-btn--secondary ml-btn--block">Talk to us</button></div></div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">The recommended tier is marked by border and label, not by colour alone.</p>`,
  "trends table": `<div class="ml-trends" style="max-width:420px">
    <div class="ml-trends__head"><span>#</span><span>Top searches this week</span><span>Change</span></div>
    <a class="ml-trends__row" href="#c-trends-table"><span class="ml-trends__rank">1</span>
      <span class="ml-trends__term"><img class="ml-trends__thumb" src="${PHOTO}" alt="" />
        <span><span class="ml-trends__label">Session tabla players</span><br><span class="ml-trends__sub">Mumbai · 2,410 searches</span></span></span>
      <span class="ml-trends__delta ml-trends__delta--up">${ic("arrow-up", 12)} +18%</span></a>
    <a class="ml-trends__row" href="#c-trends-table"><span class="ml-trends__rank">2</span>
      <span class="ml-trends__term"><img class="ml-trends__thumb" src="${GEAR}" alt="" />
        <span><span class="ml-trends__label">Neumann U87</span><br><span class="ml-trends__sub">Gear · 1,905 searches</span></span></span>
      <span class="ml-trends__delta ml-trends__delta--down">${ic("arrow-down", 12)} −4%</span></a>
    <a class="ml-trends__row" href="#c-trends-table"><span class="ml-trends__rank">3</span>
      <span class="ml-trends__term"><img class="ml-trends__thumb" src="${PHOTO}" alt="" />
        <span><span class="ml-trends__label">Mixing &amp; mastering</span><br><span class="ml-trends__sub">Services · 1,740 searches</span></span></span>
      <span class="ml-trends__delta ml-trends__delta--flat">±0%</span></a></div>`,
  "collage": `<div class="ml-collage" style="max-width:400px">
    <div class="ml-collage__hero"><img src="${PHOTO}" alt="" /></div>
    <img src="${GEAR}" alt="" /><img src="${FACE}" alt="" />
    <img src="${GEAR2}" alt="" /><div class="ml-collage__more">+7</div></div>`,
  "social sign-in button": `<div style="max-width:340px;display:grid;gap:var(--space-2)">
    <button class="ml-btn ml-btn--social ml-btn--block">
      <svg viewBox="0 0 18 18" aria-hidden="true"><path fill="#4285F4" d="M17.6 9.2c0-.6-.1-1.2-.2-1.8H9v3.4h4.8a4.1 4.1 0 0 1-1.8 2.7v2.2h2.9c1.7-1.6 2.7-3.9 2.7-6.5z"/><path fill="#34A853" d="M9 18c2.4 0 4.5-.8 6-2.2l-2.9-2.2c-.8.5-1.8.9-3.1.9-2.4 0-4.4-1.6-5.1-3.8H.9v2.3A9 9 0 0 0 9 18z"/><path fill="#FBBC05" d="M3.9 10.7a5.4 5.4 0 0 1 0-3.4V5H.9a9 9 0 0 0 0 8l3-2.3z"/><path fill="#EA4335" d="M9 3.6c1.3 0 2.5.5 3.4 1.3l2.6-2.6A9 9 0 0 0 .9 5l3 2.3C4.6 5.2 6.6 3.6 9 3.6z"/></svg>
      Continue with Google</button>
    <button class="ml-btn ml-btn--social ml-btn--block">
      <svg viewBox="0 0 18 18" aria-hidden="true"><path fill="#000" d="M12.5 9.6c0-2 1.6-2.9 1.7-3-.9-1.4-2.4-1.5-2.9-1.6-1.2-.1-2.4.7-3 .7-.6 0-1.6-.7-2.6-.7-1.3 0-2.6.8-3.3 2-1.4 2.4-.4 6 1 8 .7 1 1.5 2 2.5 2 1 0 1.4-.6 2.6-.6s1.5.6 2.6.6c1.1 0 1.8-1 2.4-1.9.8-1.1 1.1-2.2 1.1-2.2s-2.1-.8-2.1-3.3zM10.8 3.7c.5-.7.9-1.6.8-2.6-.8 0-1.8.5-2.4 1.2-.5.6-1 1.6-.8 2.5.9.1 1.8-.4 2.4-1.1z"/></svg>
      Continue with Apple</button></div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Provider marks are the one documented palette exception — their brand terms fix the colours.</p>`,
  "native keyboard & IME": `<div class="g-frame" style="max-width:300px">
    <div style="padding:var(--space-3)"><div class="ml-field"><label for="d-ime">Message</label>
      <input id="d-ime" inputmode="text" enterkeyhint="send" placeholder="Type a message…" /></div></div>
    <div class="ml-ime-bar"><span class="ml-type-1" style="color:var(--color-muted)">enterkeyhint="send"</span>
      <button class="ml-btn ml-btn--primary ml-btn--sm ml-ime-bar__done">Send</button></div>
    <div style="background:var(--color-surface);border-top:1px solid var(--color-line);padding:var(--space-4) var(--space-2);text-align:center">
      <span class="ml-type-1" style="color:var(--color-muted)">System keyboard — 45% of the screen</span></div></div>`,
  "masonry": '<div class="ml-masonry">' +
    Array.from({length:9},(_,i)=>
      `<figure class="ml-mosaic-tile" style="margin:0 0 var(--space-2)">
         <img src="${m("pins", i*7)}" alt="" loading="lazy" />
         <button class="ml-mosaic-tile__pin" aria-label="Save">${ic("heart", 16)}</button>
         ${i%3===0?`<figcaption class="ml-mosaic-tile__cap">Saved to Moodboard</figcaption>`:""}
       </figure>`).join("") + '</div>' +
    `<p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-3)">Nine tiles of unequal height, ${MEDIA.pins.length} in the pool. Columns: 2 / 3 at 600dp / 4 at 840dp.</p>`,

  "bento": `<div class="ml-bento">
    <article class="ml-bento__card"><img class="ml-bento__img" src="${m("stage",3)}" alt="" loading="lazy" />
      <div><div class="ml-bento__title">Live sessions</div>
        <p class="ml-bento__body" style="margin:var(--space-1) 0 0">The wide tile leads. Everything after it is a beat in the same bar.</p></div></article>
    <article class="ml-bento__card"><img class="ml-bento__img" src="${m("object",1)}" alt="" loading="lazy" />
      <div class="ml-bento__title">Gear</div></article>
    <article class="ml-bento__card"><img class="ml-bento__img" src="${m("gear",2)}" alt="" loading="lazy" />
      <div class="ml-bento__title">Studios</div></article>
    <article class="ml-bento__card"><img class="ml-bento__img" src="${m("stage",17)}" alt="" style="height:auto;flex:1" loading="lazy" />
      <div class="ml-bento__title">Tall tile</div>
      <p class="ml-bento__body">Spans two rows at 600dp and up.</p></article>
    <article class="ml-bento__card"><img class="ml-bento__img" src="${m("editorial",4)}" alt="" loading="lazy" />
      <div class="ml-bento__title">Collaborators</div></article>
  </div>
  <div class="ml-bento ml-bento--dark" style="background:var(--color-ink);padding:var(--space-3);border-radius:var(--radius-3);margin-top:var(--space-4)">
    <article class="ml-bento__card"><img class="ml-bento__img" src="${m("stage",63)}" alt="" loading="lazy" />
      <div><div class="ml-bento__title">Dark bento</div>
      <p class="ml-bento__body" style="margin:var(--space-1) 0 0">Tiles are translucent white, not a second black — so the same markup works over ink or over media.</p></div></article>
    <article class="ml-bento__card"><img class="ml-bento__img" src="${m("stage",88)}" alt="" loading="lazy" />
      <div class="ml-bento__title">Live rooms</div>
      <p class="ml-bento__body" style="margin:0">5% white fill</p></article>
    <article class="ml-bento__card"><img class="ml-bento__img" src="${m("motion",0)}" alt="" loading="lazy" />
      <div class="ml-bento__title">Motion</div>
      <p class="ml-bento__body" style="margin:0">12% white border</p></article>
  </div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Both variants carry media in every tile — a bento with empty cells reads as a broken grid, not a considered one.</p>`,

  "split editorial": `<div class="ml-split">
    <div class="ml-split__media" style="aspect-ratio:4/3"><img src="${m("editorial",0)}" alt="" loading="lazy" /></div>
    <div><span class="ml-article__tag">The brief</span>
      <blockquote class="ml-quote" style="margin-top:var(--space-2)">Find the room, not just the engineer.</blockquote>
      <cite class="ml-quote__cite">Ananya Sharma · Playback vocalist, Mumbai</cite></div>
  </div>
  <div class="ml-split ml-split--flip" style="margin-top:var(--space-4)">
    <div class="ml-split__media" style="aspect-ratio:4/3"><video src="${m("video",2)}" muted loop playsinline autoplay aria-label="Studio session"></video></div>
    <div><span class="ml-article__tag">Motion</span>
      <blockquote class="ml-quote" style="margin-top:var(--space-2)">The flipped band puts the media second.</blockquote>
      <cite class="ml-quote__cite">Alternating sides is what stops a run of these reading as a slideshow</cite></div>
  </div>`,

  "article grid": '<div class="ml-articles">' +
    [["Recording","Ten rooms worth booking in Mumbai","6 min read"],
     ["Gear","What a U87 actually costs to run","4 min read"],
     ["Craft","Session players on getting the first call","8 min read"]]
    .map(([tag,title,meta],i)=>
      `<a class="ml-article" href="#c-article-grid">
         <div class="ml-article__media"><img src="${m("stage", i*29+5)}" alt="" loading="lazy" /></div>
         <div><span class="ml-article__tag">${tag}</span>
           <h4 class="ml-article__title" style="margin:var(--space-1) 0">${title}</h4>
           <span class="ml-article__meta">${meta}</span></div></a>`).join("") + '</div>',

  "feature grid": `<div class="ml-features">
    ${[["sparkle","Matched, not searched","Briefs go to the twelve people who actually fit, not the twelve hundred who might."],
       ["globe-checked","Credits that hold up","Every credit is confirmed by a second party before it counts."],
       ["briefcase","Paid on delivery","Escrow releases when the stems land, not when someone remembers."]]
      .map(([i,t,b])=>`<article class="ml-feature">
        <span class="ml-feature__icon">${ic(i,20)}</span>
        <h4 class="ml-feature__title">${t}</h4>
        <p class="ml-feature__body" style="margin:0">${b}</p></article>`).join("")}
  </div>
  <div class="ml-features" style="margin-top:var(--space-4)">
    ${[["lock","Unverified credits","Anyone can claim a session. Nothing proves it."],
       ["people","Cold outreach","The same fifty DMs to the same fifty inboxes."]]
      .map(([i,t,b])=>`<article class="ml-feature ml-feature--problem">
        <span class="ml-feature__icon">${ic(i,20)}</span>
        <h4 class="ml-feature__title">${t}</h4>
        <p class="ml-feature__body" style="margin:0">${b}</p></article>`).join("")}
  </div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Accent disc for capability, neutral disc for problem — a pain point is not an accent.</p>`,

  "testimonial": `<div class="ml-testimonial" style="max-width:420px">
    <blockquote class="ml-testimonial__quote">Three confirmed credits in a fortnight, and every one of them from someone who found me rather than the other way round.</blockquote>
    <div class="ml-testimonial__user">
      <img class="ml-avatar ml-avatar--sm" src="${m("faces",4)}" alt="" />
      <div><div class="ml-testimonial__name">Ananya Sharma</div>
        <div class="ml-testimonial__role">Playback vocalist · Mumbai</div></div></div>
    <div class="ml-testimonial__stats">
      <div class="ml-testimonial__stat"><b>3</b><span>Credits</span></div>
      <div class="ml-testimonial__stat"><b>14</b><span>Days</span></div>
      <div class="ml-testimonial__stat"><b>4.9</b><span>Rating</span></div></div>
  </div>`,

  "ambient album card": `<div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:var(--space-3);max-width:640px">
    ${[["Dil Ki Raah","Arijit Sen · 2026"],["Limelight","Shubh · Single"],["Night Bus","The Local Train · EP"]]
      .map(([t,mt],i)=>`<a class="ml-album" href="#c-ambient-album-card">
        <div class="ml-album__frame"><img src="${m("pins", i*13+2)}" alt="" loading="lazy" />
          <button class="ml-album__play" aria-label="Play ${t}">${ic("play",20)}</button></div>
        <div><div class="ml-album__title">${t}</div><div class="ml-album__meta">${mt}</div></div></a>`).join("")}
  </div>`,

  "trust & verification": `<div class="ml-trust" style="max-width:420px">
    ${[["Identity","Verified by Aadhaar eKYC · Mar 2026"],
       ["Credit · Dil Ki Raah","Confirmed by Yash Raj Sound Studios"],
       ["Payout account","Verified by penny-drop · Feb 2026"]]
      .map(([w,who])=>`<div class="ml-trust__row"><span class="ml-trust__mark">${ic("check",14)}</span>
        <div><div class="ml-trust__what">${w}</div><div class="ml-trust__who">${who}</div></div></div>`).join("")}
    <div class="ml-trust__stats">
      <div class="ml-testimonial__stat"><b>98%</b><span>Response</span></div>
      <div class="ml-testimonial__stat"><b>6 yrs</b><span>On MusicLinkd</span></div></div>
  </div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Every tick names its source. A tick with no source is decoration.</p>`,

  "sticker": `<div style="position:relative;max-width:300px;border-radius:var(--radius-2);overflow:hidden">
    <img src="${m("stage",41)}" alt="" style="width:100%;display:block" loading="lazy" />
    <span class="ml-sticker">Now booking</span></div>`,

  "ambient mosaic panel": `<div class="ml-wall">
    <div class="ml-wall__bg" aria-hidden="true">
      ${[0,1,2,3].map(c=>`<div class="ml-wall__col ${c%2?"ml-wall__col--drift":""}">
        ${[0,1,2,3].map(r=>`<img src="${m("pins", c*17+r*5)}" alt="" loading="lazy" />`).join("")}</div>`).join("")}
    </div>
    <div class="ml-wall__card">
      <div class="ml-wordmark" style="font-size:var(--type-2)">MUSIC<em>LINKD</em></div>
      <p class="ml-type-2" style="margin:var(--space-2) 0 var(--space-3)">72,000 verified music professionals.</p>
      <button class="ml-btn ml-btn--primary ml-btn--block">Create your profile</button></div>
  </div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">The wall is decorative and aria-hidden; the card carries the meaning. Drift respects prefers-reduced-motion.</p>`,

  "permission dialog": `<div class="ml-permission" style="max-width:320px">
    <span class="ml-permission__icon">${ic("microphone",24)}</span>
    <h4 class="ml-permission__title">Allow microphone access?</h4>
    <p class="ml-permission__why">Needed to record scratch takes for a brief. Nothing is recorded until you tap Record.</p>
    <div class="ml-permission__acts">
      <button class="ml-btn ml-btn--primary ml-btn--block">Continue</button>
      <button class="ml-btn ml-btn--secondary ml-btn--block">Not now</button></div>
  </div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Earns the OS prompt before firing it — Android remembers "don't ask again" after two refusals.</p>`,

  "camera picker": `<div class="ml-capture" style="max-width:300px;border:1px solid var(--color-line);border-radius:var(--radius-2)">
    <button class="ml-capture__opt">${ic("camera",20)}Take photo</button>
    <div style="height:1px;background:var(--color-line)"></div>
    <button class="ml-capture__opt">${ic("camera-roll",20)}Choose from library</button>
    <div style="height:1px;background:var(--color-line)"></div>
    <button class="ml-capture__opt">${ic("folder",20)}Browse files</button>
  </div>`,

  "audio recorder": `<div class="ml-rec is-live" style="max-width:320px">
    <div class="ml-rec__head"><span class="ml-rec__dot"></span>
      <span class="ml-type-1" style="font-weight:var(--weight-3)">Recording</span>
      <span class="ml-rec__time">00:14</span></div>
    <div class="ml-rec__meter">${Array.from({length:24},(_,i)=>`<span class="ml-rec__bar" style="height:${8+((i*37)%28)}px"></span>`).join("")}</div>
    <div class="ml-rec__acts">
      <button class="ml-btn ml-btn--secondary ml-btn--block">Cancel</button>
      <button class="ml-btn ml-btn--primary ml-btn--block">Stop</button></div>
  </div>`,

  "stem dropzone": `<div class="g-row" style="align-items:flex-start">
    <div class="ml-dropzone" style="width:220px">
      ${ic("upload-feed",28)}
      <span class="ml-dropzone__hint">Drop stems here</span>
      <span class="ml-dropzone__formats">WAV, AIFF up to 200MB</span></div>
    <div class="ml-dropzone is-over" style="width:220px">
      ${ic("upload-feed",28)}
      <span class="ml-dropzone__hint">Release to upload</span></div>
  </div>`,

  "inline editor": `<div style="max-width:320px">
    <button class="ml-inline"><span>Playback Vocalist · Mumbai</span>
      <span class="ml-inline__pencil">${ic("compact-chevron-right-compact",14)}</span></button>
    <div class="ml-inline is-editing" style="margin-top:var(--space-2)">
      <input value="Playback Vocalist · Mumbai" style="border:0;outline:0;background:none;font:inherit;width:100%;padding:0;min-height:var(--size-3)" />
    </div>
    <div class="ml-inline__acts"><button class="ml-btn ml-btn--primary ml-btn--sm">Save</button><button class="ml-btn ml-btn--secondary ml-btn--sm">Cancel</button></div>
  </div>`,

  "error boundary": `<div class="ml-error" style="max-width:320px">
    <span class="ml-error__mark">${ic("cancel",24)}</span>
    <h4 class="ml-error__title">Something broke on our end</h4>
    <p class="ml-error__body">The page didn't load. Your data is safe — try again in a moment.</p>
    <button class="ml-btn ml-btn--primary">Retry</button>
    <span class="ml-error__ref">Ref · 8F2C-91A</span>
  </div>`,

  "offline & adaptive banners": `<div style="display:grid;gap:var(--space-2);max-width:400px">
    <div class="ml-banner ml-banner--offline"><span>${ic("wifi-no",18)}</span>
      <span class="ml-banner__body">You're offline — changes will sync when you're back.</span></div>
    <div class="ml-banner ml-banner--promo"><span>${ic("download",18)}</span>
      <span class="ml-banner__body">Install MusicLinkd for faster access and offline briefs.</span>
      <button class="ml-banner__dismiss" aria-label="Dismiss">${ic("cancel",16)}</button></div>
  </div>`,

  "QR share": `<div class="ml-qr" style="max-width:260px">
    <div class="ml-qr__code">${ic("qr-code",120)}</div>
    <div class="ml-qr__handle">@arijitsen</div>
    <div class="ml-qr__hint">Scan to view profile</div>
  </div>`,

  "animated success": `<div class="ml-success" style="max-width:280px">
    <span class="ml-success__mark"><span class="ml-success__ring"></span><span class="ml-success__tick">${ic("check",28)}</span></span>
    <h4 style="margin:0;font-family:var(--font-display);font-size:var(--type-3);font-weight:var(--weight-3)">Booking confirmed</h4>
    <p class="ml-type-2" style="color:var(--color-muted);margin:0">Yash Raj Sound Studios · Sat, 4 PM</p>
  </div>`,

  "footer CTA": `<div class="ml-footer-cta" style="max-width:480px">
    <h3 class="ml-footer-cta__title">Ready to collaborate?</h3>
    <p class="ml-footer-cta__sub">Join 72,000+ verified music professionals.</p>
    <button class="ml-btn ml-btn--primary ml-btn--lg" style="margin:0 auto">Create your profile</button>
    <div class="ml-footer-cta__links">
      <a href="#c-footer-cta">About</a><a href="#c-footer-cta">Careers</a><a href="#c-footer-cta">Privacy</a><a href="#c-footer-cta">Terms</a>
    </div>
  </div>`,

  "what's new": `<div class="ml-whatsnew" style="max-width:380px">
    <span class="ml-whatsnew__ver">Version 4.2 · Aug 2026</span>
    ${[["New","Rental marketplace","Rent gear and studio time from verified owners."],
       ["Fixed","Booking calendar","Blocked dates now sync correctly across time zones."],
       ["Improved","Credit verification","Second-party confirmation now takes under 48 hours."]]
      .map(([k,t,d])=>`<div class="ml-whatsnew__item"><span class="ml-whatsnew__kind">${k}</span>
        <div><div class="ml-whatsnew__what">${t}</div><div class="ml-whatsnew__why">${d}</div></div></div>`).join("")}
  </div>`,

  "android app grid": `<div class="ml-appgrid">
    ${[["circle","Circle mask"],["squircle","Squircle mask"],["rounded","Rounded square"]]
      .map(([v,l])=>`<div class="ml-appgrid__cell">
        <div class="ml-appgrid__canvas ml-appgrid__canvas--${v}">
          <img src="/guide/brand/assets/logo-badge-inverse.svg" alt="" style="width:56%;height:56%" />
          <div class="ml-appgrid__safe"></div></div>
        <span class="ml-appgrid__label">${l}</span></div>`).join("")}
  </div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Dashed line marks the 66.6% safe zone — OEM masks vary and only the centre is guaranteed visible.</p>`,

  "tabular numbers": `<div class="g-row" style="gap:var(--space-4)">
    <div><div class="ml-eyebrow">Proportional</div>
      <div style="font-size:var(--type-3);font-weight:var(--weight-3);line-height:1.6">
        <div>1,204</div><div>8,891</div><div>11,073</div></div></div>
    <div><div class="ml-eyebrow">Tabular</div>
      <div class="ml-tabular" style="font-size:var(--type-3);font-weight:var(--weight-3);line-height:1.6">
        <div>1,204</div><div>8,891</div><div>11,073</div></div></div>
  </div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Digits share one advance width, so columns align and a changing number does not shift the text beside it.</p>`,

  "readable measure": `<p class="ml-measure ml-type-2" style="margin:0">A line of prose past roughly seventy-five characters starts to lose its return sweep — the eye has to hunt for the start of the next line. This paragraph is capped at <code>--layout-measure</code>, which is 64ch, and the cap is expressed in ch so it tracks the font rather than the viewport.</p>`,

  "person row": `<div style="max-width:360px">
    ${[["Vedanshu Beats","Producer · connected 12 May 2026","2h"],
       ["Karan Saxena","Guitarist · connected 3 Apr 2026","1d"],
       ["Siddharth Swar","Mixing Engineer · applied 2 days ago","3d"]]
      .map(([n,sub,t],i)=>`<a class="ml-person-row" href="#c-person-row">
        <img src="${m("faces", i*3+1)}" alt="" />
        <div class="meta"><b>${n}</b><span>${sub}</span></div>
        <span class="time">${t}</span></a>`).join("")}
  </div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">One component, four former names — .thread-row, .collab-row, .req-row and .applicant-row.</p>`,

  /* ── Widget Directory backfill ──────────────────────────────────────────
     Demos for the fourteen widgets the directory named and the system had
     never built. Photography comes from the existing pools via m(); no new
     imagery is introduced, because a widget demo is not a reason to bind a
     new asset to the system. */

  "recommended collaborator card": `<div style="max-width:340px"><a class="ml-collab-card" href="#c-recommended-collaborator-card">
    <div class="ml-collab-card__head"><img src="${m("faces", 4)}" alt="" />
      <div class="ml-collab-card__id"><div class="ml-collab-card__name">Rhea Kapoor</div>
        <span class="ml-collab-card__role">Mixing Engineer · Mumbai</span></div>
      <span class="ml-match"><span class="ml-match__pct">92%</span></span></div>
    <ul class="ml-collab-card__why"><li>Both worked on <b>Dil Ki Raah</b></li>
      <li>Overlapping genres — Sufi, Indie</li></ul>
    <div class="ml-collab-card__actions"><span class="ml-btn">Connect</span>
      <span class="ml-btn ml-btn--ghost">View</span></div></a></div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">The match figure reuses .ml-match rather than a second percentage treatment.</p>`,

  "collaborator match rail": `<div class="ml-collab-rail" style="max-width:360px">
    ${[["Rhea Kapoor", "Mixing Engineer", 92, 4], ["Imtiaz Khan", "Session Guitarist", 87, 7],
       ["Nikhil Rao", "Composer", 81, 10]]
      .map(([n, r, p, i]) => `<a class="ml-collab-card" href="#c-collaborator-match-rail">
        <div class="ml-collab-card__head"><img src="${m("faces", i)}" alt="" />
          <div class="ml-collab-card__id"><div class="ml-collab-card__name">${n}</div>
            <span class="ml-collab-card__role">${r}</span></div>
          <span class="ml-match"><span class="ml-match__pct">${p}%</span></span></div></a>`).join("")}
  </div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Layout only — it does not restyle the cards it holds, so the two cannot drift.</p>`,

  "discovery search result card": `<div style="max-width:360px">
    ${[["Ananya Sharma", "Playback Vocalist · Mumbai", ["Sufi", "Bollywood"], 1],
       ["Yash Raj Sound", "Recording Studio · Andheri", ["Dolby Atmos", "Live room"], 5]]
      .map(([n, meta, tags, i]) => `<a class="ml-search-result" href="#c-discovery-search-result-card">
        <img src="${m("faces", i)}" alt="" />
        <div class="ml-search-result__body"><div class="ml-search-result__name">${n}</div>
          <span class="ml-search-result__meta">${meta}</span>
          <div class="ml-search-result__tags">${tags.map((t) => `<span class="ml-chip">${t}</span>`).join("")}</div></div>
        <span class="ml-match"><span class="ml-match__pct">88%</span></span></a>`).join("")}
  </div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">The richer sibling of .ml-result — a row has no room for genres or match data.</p>`,

  "networking feed": `<div class="ml-feed" style="max-width:360px">
    <div class="ml-post"><div class="ml-post__head"><img src="${m("faces", 2)}" alt="" />
      <div><b>Karan Saxena</b><span>Guitarist · 2h</span></div></div>
      <p class="ml-post__body">Tracked three takes of the bridge today. The room finally sounds right.</p></div>
    <div class="ml-post"><div class="ml-post__head"><img src="${m("faces", 6)}" alt="" />
      <div><b>Priya Sharma</b><span>Producer · 5h</span></div></div>
      <p class="ml-post__body">Looking for a tabla player for a session in Bandra next week.</p></div>
    <p class="ml-feed__end">You're all caught up</p></div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">The post is .ml-post. This is the feed around it — it owns the two states a post has no concept of, loading-more and empty.</p>`,

  "band members": `<div class="ml-roster" style="max-width:340px">
    ${[["Raman Negi", "Vocals, rhythm guitar", true, 0], ["Paras Thakur", "Lead guitar", false, 3],
       ["Sahil Sarin", "Drums", false, 8], ["Ramit Mehra", "Bass", false, 11]]
      .map(([n, inst, lead, i]) => `<div class="ml-roster__member" data-lead="${lead}">
        <img src="${m("faces", i)}" alt="" />
        <div class="ml-roster__id"><div class="ml-roster__name">${n}</div>
          <span class="ml-roster__instrument">${inst}</span></div></div>`).join("")}
  </div>`,

  "studio venue list item": `<div style="max-width:360px">
    ${[["Andheri Live Room", "Andheri East · 1.4 km", "★ 4.4", 2],
       ["Yash Raj Sound Studios", "Andheri West · 3.2 km", "★ 4.9", 5]]
      .map(([n, loc, r, i]) => `<a class="ml-studio-card ml-studio-card--compact" href="#c-studio-venue-list-item">
        <div class="ml-studio-card__media"><img src="${m("gear", i)}" alt="" /></div>
        <div class="ml-studio-card__body"><div class="ml-studio-card__name">${n}</div>
          <div class="ml-studio-card__loc">${loc}</div></div>
        <span class="ml-type-1" style="color:var(--color-muted)">${r}</span></a>`).join("")}
  </div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">A modifier on .ml-studio-card, not a second block — same data, one-line layout.</p>`,

  "featured studio card": `<a class="ml-featured-studio" href="#c-featured-studio-card" style="max-width:360px">
    <div class="ml-media ml-media--scrim"><img src="${m("gear", 4)}" alt="" /></div>
    <div class="ml-featured-studio__body"><div class="ml-featured-studio__name">Yash Raj Sound Studios</div>
      <span class="ml-featured-studio__meta">Dolby Atmos · SSL 9000J · Andheri West</span>
      <div class="ml-featured-studio__rate">₹4,500 / hour</div></div></a>`,

  "studio spec": `<dl class="ml-spec" style="max-width:360px">
    ${[["Console", "SSL 9000J, 72 channels"], ["Monitors", "PMC IB2S · Yamaha NS-10M"],
       ["Microphones", "Neumann U87 Ai · AKG C414 · Shure SM7B"],
       ["Outboard", "Neve 1073 ×2 · Urei 1176 · Lexicon 480L"],
       ["Room", "Live room 620 sq ft · booth 90 sq ft"]]
      .map(([k, v]) => `<div class="ml-spec__row"><dt class="ml-spec__key">${k}</dt>
        <dd class="ml-spec__val" style="margin:0">${v}</dd></div>`).join("")}
  </dl>`,

  "project card": `<a class="ml-project-card" href="#c-project-card" style="max-width:240px">
    <div class="ml-media"><img src="${m("stage", 3)}" alt="" /></div>
    <div class="ml-project-card__body"><div class="ml-project-card__title">Dil Ki Raah</div>
      <div class="ml-project-card__meta">Single · Released 14 Mar 2026</div>
      <div class="ml-project-card__credits"><span class="ml-status ml-status--success">Released</span>
        <span class="ml-type-1" style="color:var(--color-muted)">4 credits</span></div></div></a>`,

  "recent project card": `<div style="max-width:360px">
    ${[["Neon Horizon", "Mix engineer · 2026", 6], ["Tu Hi Bata", "Composer · 2025", 9]]
      .map(([t, meta, i]) => `<a class="ml-project-card ml-project-card--compact" href="#c-recent-project-card">
        <div class="ml-media"><img src="${m("stage", i)}" alt="" /></div>
        <div class="ml-project-card__body"><div class="ml-project-card__title">${t}</div>
          <div class="ml-project-card__meta">${meta}</div></div></a>`).join("")}
  </div>`,

  "track card": `<div style="max-width:360px">
    ${[["Dil Ki Raah", "3:42", "128k", 3], ["Neon Horizon", "4:05", "86k", 6],
       ["Tu Hi Bata", "3:18", "54k", 9]]
      .map(([t, dur, plays, i]) => `<a class="ml-track" href="#c-track-card">
        <div class="ml-media"><img src="${m("stage", i)}" alt="" /></div>
        <div class="ml-track__body"><div class="ml-track__title">${t}</div>
          <div class="ml-track__meta"><span>${dur}</span>
            <span class="ml-track__platforms"><span class="ml-badge">Spotify</span><span class="ml-badge">YouTube</span></span></div></div>
        <span class="ml-track__plays">${plays}</span></a>`).join("")}
  </div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Platforms are named in the existing badge — the system ships no third-party logo artwork.</p>`,

  "audio podcast demo": `<div class="ml-player ml-player--inline" style="max-width:340px">
    <div class="ml-player__title">Demo — session reel</div>
    <div class="ml-player__transport"><button class="ml-player__btn" aria-label="Play">${ic("play", 18)}</button>
      <div class="ml-player__wave" role="slider" aria-label="Seek" aria-valuemin="0" aria-valuemax="100" aria-valuenow="42" tabindex="0">
      ${Array.from({ length: 28 }, (_, i) => `<span class="ml-player__bar${i < 12 ? " is-played" : ""}" style="height:${20 + ((i * 37) % 60)}%"></span>`).join("")}
      </div><span class="ml-player__time">1:24</span></div></div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">A modifier on .ml-player, not a second player — the transport and waveform are the same component.</p>`,

  "hire contact modal": `<div class="g-frame" style="padding:var(--space-3)">
    <form class="ml-hire">
      <div class="ml-hire__row"><span class="ml-hire__label">Scope</span>
        <div class="ml-hire__scope"><button type="button" class="ml-chip" aria-pressed="true">Session</button>
          <button type="button" class="ml-chip">Full track</button>
          <button type="button" class="ml-chip">Live show</button></div></div>
      <div class="ml-hire__row"><span class="ml-hire__label">Proposed date</span>
        <label class="ml-field"><input type="date" value="2026-09-14" /></label></div>
      <div class="ml-hire__row"><span class="ml-hire__label">Budget</span>
        <div class="ml-hire__budget"><label class="ml-field"><input type="text" value="₹25,000" /></label>
          <span class="ml-type-1" style="color:var(--color-muted)">per day</span></div></div>
      <button type="button" class="ml-btn">Send inquiry</button>
    </form></div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Form body only. The shell is .ml-sheet or .ml-dialog, so this cannot fork their open/close behaviour.</p>`,

  "share sheet": `<div class="g-frame" style="padding:var(--space-3)"><div class="ml-share">
    <div class="ml-share__preview"><img src="${m("faces", 1)}" alt="" />
      <div><b>Ananya Sharma</b><div class="ml-type-1" style="color:var(--color-muted)">Playback Vocalist · Mumbai</div></div></div>
    <p class="ml-share__msg">Check out Ananya on MusicLinkd — musiclinkd.com/ananya</p>
    <div class="ml-share__channels"><span class="ml-btn">WhatsApp</span>
      <span class="ml-btn ml-btn--ghost">Copy link</span>
      <span class="ml-btn ml-btn--ghost">More</span></div></div></div>
  <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">Named for the job, not the destination — a .ml-whatsapp-sheet guarantees a near-identical .ml-telegram-sheet later.</p>`,

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

/* Truncate prose for the card blurb. Two things the previous one-liner got
   wrong: it sliced at exactly 320 chars, which cut words in half ("the type
   is only the fallbac"), and it sliced AFTER esc(), so a cut landing inside
   "&amp;" emitted a broken entity. Cut on a word boundary, before escaping. */
function clamp(text, max) {
  if (text.length <= max) return text;
  const cut = text.slice(0, max);
  const at = cut.lastIndexOf(" ");
  return (at > max * 0.6 ? cut.slice(0, at) : cut).replace(/[,;:.\s]+$/, "") + "…";
}

const scaleRow = (label, group, fmt) => `<tr><th>${label}</th>${["1", "2", "3", "4"]
  .map((k) => group[k] ? `<td><code>${fmt(group[k], k)}</code></td>` : "<td>—</td>").join("")}</tr>`;

const componentSection = (s) => `<section class="g-comp" id="${slug(s.name)}">
    <div class="g-comp__head">
      <h3>${esc(s.name)}</h3>
      <span class="g-file">${s.file}</span>
      ${s.compose ? `<span class="ml-badge">Compose · ${esc(s.compose)}</span>` : ""}
    </div>
    ${s.prose ? `<p class="ml-lede ml-measure">${esc(clamp(s.prose, 320))}</p>` : ""}
    <div class="g-demo">${DEMOS[s.name]}</div>
    ${stateMap(s)}
    <details class="g-sel"><summary>${s.selectors.length} selector${s.selectors.length === 1 ? "" : "s"}</summary>
      <code>${s.selectors.join("</code> <code>")}</code></details>
  </section>`;

/* Every state the component implements, plus every one it does NOT — the
   absences are the useful half. A screen author who can see that a card has no
   "loading" is told to reach for .ml-skeleton rather than invent a second
   screen, which is exactly the decision that went wrong across the flows. */
const stateMap = (s) => {
  const has = new Set(s.states);
  /* Focus is only inherited by something focusable. The demo markup is the
     honest test — a card that is an <a> gets the ring, a <div> does not. */
  const focusable = /<(a|button|input|textarea|select)\b|tabindex=/.test(DEMOS[s.name] || "");
  const cell = (p) => {
    if (has.has(p.key)) return `<span class="ml-badge" title="${s.file}">${p.label}</span>`;
    if (p.global && GLOBAL_STATES.has(p.key) && focusable)
      return `<span class="ml-badge ml-badge--inherit" title="Base rule in ml-core.css — every focusable element">${p.label}</span>`;
    if (p.composedBy)
      return `<span class="ml-badge ml-badge--compose" title="Compose ${p.composedBy}">${p.label}</span>`;
    return `<span class="ml-badge ml-badge--off" title="Not available — building it belongs in ${s.file}">${p.label}</span>`;
  };
  return `<div class="g-states">
    <span class="g-states__lbl">States</span>${STATE_PROBES.map(cell).join("")}
  </div>`;
};

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
<!-- Axes requested here must match foundation.weight in tokens.json exactly.
     700 was dropped on 2026-08-31: fetching a weight the system cannot name
     is how a stray 700 gets to render correctly and go unnoticed. -->
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Inter+Tight:wght@600;800&family=Unica+One&display=swap" rel="stylesheet">
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
             color: var(--color-muted); font-size: var(--type-2); font-weight:var(--weight-3); text-decoration: none; }
  .g-nav a:hover { color: var(--color-ink); }
  h2.g-h { font-family: var(--font-display); font-size: var(--type-4); font-weight:var(--weight-4);
           letter-spacing: -0.03em; margin: var(--space-4) 0 var(--space-2); }
  .g-comp { padding: var(--space-4) 0; border-top: 1px solid var(--color-line); }
  .g-comp__head { display: flex; align-items: center; gap: var(--space-2); flex-wrap: wrap; }
  .g-comp__head h3 { font-family: var(--font-display); font-size: var(--type-3); font-weight:var(--weight-3);
                     letter-spacing: -0.01em; margin: 0; text-transform: capitalize; }
  .g-file { font-size: var(--type-1); color: var(--color-muted); }
  .g-demo { margin-top: var(--space-3); padding: var(--space-3); border: 1px solid var(--color-line);
            border-radius: var(--radius-2); background: var(--color-white); overflow-x: auto; }
  .g-row { display: flex; flex-wrap: wrap; gap: var(--space-2); align-items: center; }
  .g-frame { max-width: 360px; border: 1px solid var(--color-line); border-radius: var(--radius-2);
             overflow: hidden; background: var(--color-paper); }
  /* State map. An implemented state is a normal badge; an absent one is
     outlined and struck, because "this component has no loading state" is the
     half a screen author needs to read fastest — it is the fact that sends
     them to the stylesheet instead of to a new screen file. */
  .g-states { display: flex; flex-wrap: wrap; gap: var(--space-1); align-items: center;
              margin-top: var(--space-2); }
  .g-states__lbl { font-size: var(--type-1); color: var(--color-muted); margin-right: var(--space-1); }
  .g-states .ml-badge--off { background: none; border: 1px dashed var(--color-line);
                             color: var(--color-muted); text-decoration: line-through; }
  /* Inherited and composed are real coverage, so neither is struck — but both
     read quieter than a state the component owns outright. */
  .g-states .ml-badge--inherit { background: none; border: 1px solid var(--color-line);
                                 color: var(--color-muted); }
  .g-states .ml-badge--compose { background: none; border: 1px solid var(--color-line);
                                 color: var(--color-muted); font-style: italic; }
  .g-legend { font-size: var(--type-1); color: var(--color-muted); max-width: var(--layout-measure);
              margin-top: var(--space-2); }
  .g-sel { margin-top: var(--space-2); font-size: var(--type-1); color: var(--color-muted); }
  .g-sel summary { cursor: pointer; min-height: var(--size-3); display: flex; align-items: center; }
  .g-sel code { display: inline-block; margin: 2px 4px 2px 0; }
  code { font-family: ui-monospace, SFMono-Regular, Menlo, monospace; font-size: 0.9em;
         background: var(--color-surface); padding: 1px 5px; border-radius: var(--radius-1); }
  table.g-t { width: 100%; border-collapse: collapse; font-size: var(--type-2); }
  table.g-t th, table.g-t td { text-align: left; padding: var(--space-2); border-bottom: 1px solid var(--color-line); }
  table.g-t th { color: var(--color-muted); font-weight:var(--weight-3); white-space: nowrap; }
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
  .g-brand { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: var(--space-2); }
  .g-mark { margin: 0; border: 1px solid var(--color-line); border-radius: var(--radius-2); overflow: hidden; }
  .g-mark > img { display: block; width: 100%; height: 132px; object-fit: contain;
                  padding: var(--space-3); background: var(--color-paper); box-sizing: border-box; }
  .g-mark--dark > img { background: var(--color-ink); }
  .g-mark figcaption { padding: var(--space-2); border-top: 1px solid var(--color-line);
                       font-size: var(--type-1); }
  .g-mark figcaption code { display: block; background: none; padding: 0; }
  .g-mark figcaption span { color: var(--color-muted); }
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
  <li><a href="#brand">Brand</a></li>
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
    <div class="ml-wordmark" style="font-size:var(--type-4)">MUSIC<em>LINKD</em></div>
    <p class="ml-lede" style="margin-top:var(--space-2);max-width:60ch">
      The design system, <b>generated from itself</b>. Tokens come from
      <code>tokens.json</code>; components are parsed out of
      <code>ml-core.css</code> and <code>ml-widgets.css</code>. This page is styled
      with those same files — <b>if a component breaks, this page breaks</b>.
    </p>
    <div class="g-row" style="margin-top:var(--space-3)">
      <span class="ml-badge">${cssTokenCount} tokens</span>
      <span class="ml-badge">${sections.length} sections</span>
      <span class="ml-badge">${[...new Set(sections.flatMap((s) => s.selectors))].length} selectors</span>
      <span class="ml-badge">${iconNames.length} icons</span>
      <span class="ml-badge ml-badge--accent">Light only</span>
    </div>
  </header>

  <h2 class="g-h" id="brand">Brand · ${brand.length}</h2>
  <p class="ml-lede" style="max-width:64ch">
    Every mark in <code>frontend/guide/brand/assets/</code>, each shown on the
    ground it was drawn for — detected from the file's own fills, so dropping a
    new variant into the folder documents it. The wordmark set in Unica One
    (<code>.ml-wordmark</code>) is the type treatment; these are the artwork.
  </p>
  <div class="g-brand">
    ${brand.map((b) => `<figure class="g-mark${b.onDark ? " g-mark--dark" : ""}">
      <img src="/guide/brand/assets/${b.file}" alt="${esc(b.name)}" />
      <figcaption><code>${b.file}</code><span>${b.onDark ? "on ink" : "on paper"} · ${Math.round(b.bytes / 1024)}KB</span></figcaption>
    </figure>`).join("")}
  </div>

  <h3 style="margin-top:var(--space-4)">Positioning</h3>
  <p class="ml-type-3" style="max-width:var(--layout-measure);font-family:var(--font-display);font-weight:var(--weight-3)">
    ${esc(tokens.brand.positioning)}</p>

  <h3 style="margin-top:var(--space-4)">Logo usage</h3>
  <ul class="ml-lede" style="max-width:64ch">
    ${tokens.brand.logoRules.map((r) => `<li>${esc(r)}</li>`).join("")}
  </ul>

  <h3 style="margin-top:var(--space-4)">Voice</h3>
  <p class="ml-lede" style="max-width:64ch"><b>${esc(tokens.brand.voice)}</b></p>

  <h3 style="margin-top:var(--space-4)">Colour roles</h3>
  <p class="ml-lede ml-measure">Which colour means what. A value alone does not
    say whether it is allowed on a primary button — this does, and it is the
    rule the old catalogue carried that the rebuild had not.</p>
  <table class="g-t"><thead><tr><th>Role</th><th>Token</th><th>Use</th></tr></thead><tbody>
    ${tokens.brand.colourRoles.map((r) => `<tr>
      <th><span class="g-chipcol" style="background:var(${r.token});vertical-align:middle"></span> ${esc(r.role)}</th>
      <td><code>${r.token}</code></td><td>${esc(r.use)}</td></tr>`).join("")}
  </tbody></table>
  <p class="ml-type-1" style="color:var(--color-muted);max-width:var(--layout-measure);margin-top:var(--space-2)">
    The v4 rule also read <b>“Purple = badges only.”</b> There is no purple in
    the palette and no component uses one, so that rule named a colour the
    system does not have; badges use the status colours or
    <code>--color-ink</code>. Recorded rather than dropped silently — a rule
    that vanishes with no reason is how two colour books happened.</p>

  <h3 style="margin-top:var(--space-4)">Typeface &amp; weights</h3>
  <div class="g-demo">
    ${Object.entries(tokens.brand.type).map(([role, t]) => `<div style="margin-bottom:var(--space-4)">
      <div class="ml-row__meta"><code>--font-${role}</code> · ${esc(t.family)} · ${esc(t.use)}</div>
      <div style="font-family:'${t.family}',sans-serif;font-size:28px;line-height:1.15;margin-top:var(--space-2)">
        ${t.weights.map((w) => `<div style="font-weight:var(--${weightTokenName(w)})">${w} <span class="ml-row__meta">--${weightTokenName(w)}</span> — MusicLinkd, the professional network for music</div>`).join("")}
      </div></div>`).join("")}
  </div>

  <p class="ml-lede" style="max-width:64ch">
    Colour moved into one book on 2026-08-30 — see Foundations → Colour below.
    The <code>brand-*</code> tokens there are marketing-only, never a screen
    colour; everything else is UI. Merged 2026-08-30: previously these lived
    in a separate section up here, which is exactly the "two colour books"
    problem the merge fixes.
  </p>

  <h2 class="g-h" id="foundations">Foundations</h2>
  <p class="ml-lede" style="max-width:64ch">${esc(tokens.meta.palette.$description.split(".")[1])}.</p>

  <h3 style="margin-top:var(--space-4)">Colour</h3>
  <p class="ml-lede" style="max-width:64ch">
    One book. <code>brand-*</code> entries are <b>marketing only, not the UI
    palette</b> — a screen never reaches for a <code>brand*</code> colour and a
    poster never reaches for the UI palette; the separation is carried by the
    name, not by a second group.
  </p>
  <div class="g-swatches">${Object.entries(f.color).filter(([k]) => !k.startsWith("$"))
    .map(([k, v]) => swatch(k.replace(/([a-z])([A-Z])/g, "$1-$2").toLowerCase(), v.$value,
      (v.$description || "").split(".")[0])).join("")}</div>

  <h3 style="margin-top:var(--space-4)">Scales</h3>
  <p class="ml-lede">Four steps each, exponential. Every step is visibly different from its neighbour — that is the point, and why there is no fifth.</p>
  <table class="g-t"><thead><tr><th></th><th>1</th><th>2</th><th>3</th><th>4</th></tr></thead><tbody>
    ${scaleRow("space", f.space, (t) => t.$web)}
    ${scaleRow("radius", f.radius, (t) => t.$web)}
    ${scaleRow("size", f.size, (t) => t.$web)}
    ${scaleRow("icon", f.icon, (t) => t.$web)}
    ${scaleRow("avatar", f.avatar, (t) => t.$web)}
    ${scaleRow("elevation", f.elevation, (t) => t.$android)}
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

  <h3 style="margin-top:var(--space-4)">Scale audit</h3>
  <p class="ml-lede ml-measure">Measured, not asserted. A step passes if each neighbour differs by
  at least 1.5&times; — roughly where a difference stops being arguable at a glance.</p>
  <table class="g-t"><thead><tr><th>Scale</th><th>Steps</th><th>Ratios</th><th>Verdict</th></tr></thead><tbody>
    ${Object.entries(tokens.meta.scaleAudit.pass).map(([k, v]) => {
      const [steps, rest] = v.split(" — ");
      return `<tr><th>${k}</th><td class="ml-tabular">${esc(steps)}</td><td class="ml-tabular">${esc(rest || "")}</td><td><span class="ml-status ml-status--success">Pass</span></td></tr>`;
    }).join("")}
    ${Object.entries(tokens.meta.scaleAudit.fail).map(([k, v]) => {
      const [steps] = v.split(" — ");
      return `<tr><th>${k}</th><td class="ml-tabular">${esc(steps)}</td><td colspan="2">${esc(v.slice(steps.length + 3))}</td></tr>`;
    }).join("")}
  </tbody></table>

  <h3 style="margin-top:var(--space-4)">Typography spec</h3>
  <p class="ml-lede ml-measure">Every axis of every step, and the Compose <code>TextStyle</code> it
  becomes. Generated from the same source the CSS is, so the app and this page cannot disagree.</p>
  <div style="overflow-x:auto">
  <table class="g-t"><thead><tr><th>Token</th><th>Fluid value (web)</th><th>Weight</th><th>Compose TextStyle</th><th>Usage</th></tr></thead><tbody>
    ${["1","2","3","4"].map((k) => {
      const t = f.type[k], w = t.$web, a = t.$android;
      return `<tr><th>--type-${k}</th>
        <td class="ml-tabular">${w.min} → ${w.max}</td>
        <td class="ml-tabular">${w.weight}</td>
        <td><code>TextStyle(fontFamily = ${a.family}, fontSize = ${a.size}, lineHeight = ${a.lineHeight}, fontWeight = FontWeight.W${a.weight})</code></td>
        <td>${esc(t.$description)}</td></tr>`;
    }).join("")}
  </tbody></table></div>
  <table class="g-t" style="margin-top:var(--space-3)"><thead><tr><th>Property</th><th>Value</th><th>Usage</th></tr></thead><tbody>
    <tr><th>Paragraph spacing</th><td class="ml-tabular">var(--space-3) · 16px</td><td>Between paragraphs</td></tr>
    <tr><th>Max line width</th><td class="ml-tabular">var(--layout-measure) · 64ch</td><td>Readable prose width — <code>.ml-measure</code></td></tr>
    <tr><th>Font fallback</th><td><code>-apple-system, BlinkMacSystemFont, sans-serif</code></td><td>Declared inside each --font-* value</td></tr>
    <tr><th>Number style</th><td>Tabular</td><td>Stats, tables, timers — <code>.ml-tabular</code></td></tr>
  </tbody></table>

  <h3 style="margin-top:var(--space-4)">Sizing map</h3>
  <p class="ml-lede ml-measure">Component dimensions from the spec. Where a value already lands on a
  scale it is <b>mapped, not duplicated</b> — emitting it again would be a second name for the same
  number, which is how the previous system reached 248 tokens.</p>
  <table class="g-t"><thead><tr><th>Component</th><th>Resolves to</th><th>Value</th></tr></thead><tbody>
    ${Object.entries(tokens.meta.tokenMap).filter(([k]) => !k.startsWith("$"))
      .map(([k, v]) => `<tr><th><code>${esc(k)}</code></th><td colspan="2">${esc(v)}</td></tr>`).join("")}
  </tbody></table>

  <h3 style="margin-top:var(--space-4)">Elevation &amp; borders</h3>
  <table class="g-t"><thead><tr><th>Token</th><th>Shadow</th><th>Usage</th></tr></thead><tbody>
    ${["1","2","3","4"].map((k) => `<tr><th>--elevation-${k}</th>
      <td><span style="display:inline-block;width:56px;height:28px;border-radius:var(--radius-1);background:var(--color-white);box-shadow:var(--elevation-${k})"></span>
      <code style="margin-left:var(--space-2)">${esc(f.elevation[k].$value)}</code></td>
      <td>${esc(f.elevation[k].$description)}</td></tr>`).join("")}
    <tr><th>--border-1</th><td class="ml-tabular">1px solid var(--color-line)</td><td>Divider, card border, input rest</td></tr>
    <tr><th>--border-2</th><td class="ml-tabular">2px solid var(--color-pink)</td><td>Focus ring, selected card or chip</td></tr>
    <tr><th>disabled border</th><td class="ml-tabular">1px solid var(--color-disabled)</td><td>Inactive controls</td></tr>
  </tbody></table>

  <h3 style="margin-top:var(--space-4)">Component states</h3>
  <table class="g-t"><thead><tr><th>State</th><th>Trigger</th><th>Visual</th></tr></thead><tbody>
    <tr><th>Default</th><td>Rest</td><td>Base style</td></tr>
    <tr><th>Hover</th><td>Pointer enter</td><td>--elevation-2, translateY(-2px). Pointer only — no hover on touch.</td></tr>
    <tr><th>Focused</th><td>Keyboard / Tab</td><td>2px var(--color-pink) outline at 2px offset</td></tr>
    <tr><th>Pressed</th><td>Pointer down</td><td>scale(0.96–0.97), var(--duration-1)</td></tr>
    <tr><th>Selected</th><td>Tap / click</td><td>var(--color-ink) fill for commitment; var(--color-pink-container) for intent</td></tr>
    <tr><th>Disabled</th><td>Inactive</td><td>opacity var(--opacity-3) · 0.32, pointer-events none</td></tr>
    <tr><th>Loading</th><td>Async</td><td><code>.ml-skeleton</code> shimmer</td></tr>
    <tr><th>Error</th><td>Validation fail</td><td>var(--color-error) border and message</td></tr>
    <tr><th>Empty</th><td>No data</td><td><code>.ml-empty</code> — icon, message, CTA</td></tr>
  </tbody></table>

  <h3 style="margin-top:var(--space-4)">Semantic roles</h3>
  <p class="ml-lede ml-measure">Which foundation colour fills which role. These emit <b>no</b> CSS of
  their own — on web you use the foundation token directly, because a role alias would be a second
  name for the same value. The role names exist for the Compose and SwiftUI exports.</p>
  <table class="g-t"><thead><tr><th>Role</th><th>Token</th><th>Usage</th></tr></thead><tbody>
    <tr><th>Primary</th><td><code>--color-ink</code></td><td>CTAs. Black, not pink — set by the brand owner.</td></tr>
    <tr><th>Accent</th><td><code>--color-pink</code></td><td>Focus, selection, active tab, brand mark. Never a CTA fill.</td></tr>
    <tr><th>Primary container</th><td><code>--color-pink-container</code></td><td>Badge and tag ground</td></tr>
    <tr><th>Surface</th><td><code>--color-white</code></td><td>Card and sheet fill</td></tr>
    <tr><th>Surface variant</th><td><code>--color-surface</code></td><td>Search bars, inactive segments, skeletons</td></tr>
    <tr><th>Background</th><td><code>--color-paper</code></td><td>App canvas</td></tr>
    <tr><th>Outline</th><td><code>--color-line</code></td><td>Hairlines and dividers</td></tr>
    <tr><th>Success</th><td><code>--color-success</code></td><td>Confirmed, available, verified</td></tr>
    <tr><th>Warning</th><td><code>--color-warning</code></td><td>Pending, caution</td></tr>
    <tr><th>Error</th><td><code>--color-error</code></td><td>Rejected, critical, destructive</td></tr>
    <tr><th>Info</th><td><code>--color-info</code></td><td>On Tour, remote available</td></tr>
    <tr><th>Disabled</th><td><code>--color-disabled</code></td><td>Inactive elements</td></tr>
    <tr><th>Scrim</th><td><code>--color-scrim</code></td><td>Backdrop behind drawers, sheets, modals</td></tr>
  </tbody></table>

  <h3 style="margin-top:var(--space-4)">Icon sizes</h3>
  <p class="ml-lede">The four real icon tokens. 16 and 24 are the two drawn grids;
  20 and 32 are scaled, which is why every icon carries a <code>viewBox</code> — without one,
  resizing crops the path instead of scaling it. (Intermediate sizes like 14/18/22 showed here
  until 2026-08-30 — they weren't tokens, just components that hadn't been snapped to the scale yet.)</p>
  <div class="g-demo"><div class="g-row" style="align-items:flex-end;gap:var(--space-4)">
    ${[16, 20, 24, 32].map((n) => `<div style="text-align:center">
      ${ic("search", n)}<div class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-1)">${n}</div></div>`).join("")}
  </div></div>

  <h3 style="margin-top:var(--space-4)">App icon set</h3>
  <p class="ml-lede">The launcher mark at the sizes Android and iOS ask for, each on the ground it
  is drawn for. Picking the variant by ground rather than by taste is the whole rule.</p>
  <div class="g-demo"><div class="g-row" style="align-items:center;gap:var(--space-4)">
    ${[["favicon-mark", "paper", 32], ["favicon-mark", "paper", 48], ["logo-badge-inverse", "ink", 48], ["logo-badge-inverse", "ink", 64]]
      .map(([f, ground, n]) => `<div style="text-align:center">
        <span style="display:inline-flex;padding:var(--space-2);border-radius:var(--radius-2);background:var(${ground === "ink" ? "--color-ink" : "--color-white"});border:1px solid var(--color-line)">
          <img src="/guide/brand/assets/${f}.svg" alt="" style="width:${n}px;height:${n}px;display:block" /></span>
        <div class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-1)">${n}px · ${ground}</div></div>`).join("")}
  </div></div>

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
  <p class="ml-type-1" style="color:var(--color-muted);margin:0 0 var(--space-3)">
    Two optical grids. ${iconNames.filter(nativeIs16).length} "compact" icons are drawn on a 16-unit
    box and ${iconNames.length - iconNames.filter(nativeIs16).length} standard icons on 24. Rendering a
    16-unit glyph at 22px inflates its strokes about 1.4x against a 24-unit one beside it — which is
    what made the set look unstable. Each is now shown at its own native size.</p>
  <div class="g-icons">${iconNames.map((n) => {
    const s = nativeIs16(n) ? 16 : 22;
    return `<div class="g-icon">${ic(n, s)}<span>${n}</span></div>`;
  }).join("")}</div>

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

    <h3 style="margin:var(--space-4) 0 var(--space-2)">Token-level gaps</h3>
    <p class="ml-type-1" style="color:var(--color-muted);margin:0 0 var(--space-2)">
      One table, merged 2026-08-30 — this used to be a second "Gaps" section
      under Foundations. Each row is a spec item that was considered and either
      resolved, deferred with a reason, or moved out of CSS entirely — not
      silently dropped.</p>
    <table class="g-t"><thead><tr><th>Item</th><th>Why</th></tr></thead><tbody>
      ${Object.entries(tokens.meta.knownGaps).filter(([k]) => !k.startsWith("$"))
        .map(([k, v]) => `<tr><th>${esc(k)}</th><td>${esc(v)}</td></tr>`).join("")}
    </tbody></table>
  </div>

  <h2 class="g-h" id="coverage">Coverage</h2>
  ${undocumented.length
    ? `<p class="ml-lede">${undocumented.length} section${undocumented.length === 1 ? " exists" : "s exist"} in the CSS with no demo on this page. Listed rather than hidden:</p>
       <ul>${undocumented.map((s) => `<li><code>${esc(s.name)}</code> — ${s.file}, ${s.selectors.length} selectors</li>`).join("")}</ul>`
    : `<div class="ml-banner ml-banner--success">Every section in <code>ml-core.css</code> and <code>ml-widgets.css</code> is documented above.</div>`}
  <p class="ml-lede" style="margin-top:var(--space-3)">
    Replaces <code>/guide/styleguide</code> (213 tokens) and
    <code>/guide/musiclinkd-system</code> (524 Gestalt tokens). Both 301-redirect
    here via <code>firebase.json</code> once deployed. Generated by
    <code>scripts/build-guide.mjs</code> — do not hand-edit.
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

/* Every ml-* class the page uses must be defined by the stylesheets or by the
   page's own <style> block. A demo that names a class nobody wrote renders
   unstyled, and the guide is the one place where that failure is invisible —
   it looks like a component that simply has no styling yet.
   Introduced 2026-08-31 after this caught four: three widget demos written
   against a guessed `.ml-audio` (the real class is `.ml-player`), and
   `.ml-btn--sm`, which two demos had been asking for since before it existed.
   assertIcons() already fails the build on an unknown icon; this is the same
   guarantee for classes. */
{
  const styleBlocks = (html.match(/<style>[\s\S]*?<\/style>/g) || []).join("\n");
  const sheets = ["ml-tokens.css", "ml-core.css", "ml-widgets.css"]
    .map((f) => readFileSync(join(SYS, f), "utf8")).join("\n");
  const defined = new Set(
    [...(sheets + styleBlocks).matchAll(/\.(ml-[a-zA-Z0-9_-]+)/g)].map((m) => m[1])
  );
  const undefinedClasses = new Set();
  for (const attr of html.matchAll(/class="([^"]+)"/g))
    for (const c of attr[1].split(/\s+/))
      if (c.startsWith("ml-") && !defined.has(c)) undefinedClasses.add(c);
  if (undefinedClasses.size) {
    throw new Error(
      `build-guide: ${undefinedClasses.size} undefined ml-* class(es) in the ` +
        `generated page — ${[...undefinedClasses].sort().join(", ")}. ` +
        `Either the demo has a typo or the component was never written.`
    );
  }
}

writeFileSync(OUT, html);
console.log(`build-guide: ${sections.length} sections (${documented.length} documented, ${undocumented.length} not), ` +
  `${[...new Set(sections.flatMap((s) => s.selectors))].length} selectors, ${iconNames.length} icons`);
if (undocumented.length) console.log("  undocumented: " + undocumented.map((s) => s.name).join(", "));
