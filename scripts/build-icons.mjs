#!/usr/bin/env node
/**
 * 12-build-icons.mjs — one icon source instead of 612 pasted copies.
 *
 *   node 12-build-icons.mjs <flows-dir> --out guide/system [--write]
 *
 * The product draws 37 distinct shapes across 612 inline <svg> elements, none of
 * which reference a shared source. Change one icon today and you change it in up
 * to 169 files. This emits a single sprite and rewrites every instance to a
 * <use>, which is also what makes the 381 unused glyphs in /guide deletable
 * without touching a screen.
 *
 * Names are derived from real usage context (aria-label, owning class, adjacent
 * text). Anything the heuristic cannot name confidently is emitted with a
 * TODO- prefix and listed, rather than guessed — a wrong icon name is worse than
 * an obviously unfinished one.
 */
import fs from "node:fs";
import path from "node:path";

const FLOWS = process.argv[2];
const arg = (k, d) => { const i = process.argv.indexOf(k); return i > -1 ? process.argv[i + 1] : d; };
const OUT = arg("--out", "guide/system");
const WRITE = process.argv.includes("--write");

/* context substring -> icon name, first match wins, most specific first */
const NAMES = [
  [/^Home$/i,                              "home"],
  [/Discover/i,                            "compass"],
  [/Connections/i,                         "message"],
  [/Market/i,                              "bag"],
  [/^Profile$/i,                           "person"],
  [/^Back$/i,                              "chevron-left"],
  [/Verified|ml-verified|ml-trust__mark/i, "verified"],
  [/ml-choice__mark|ml-check/i,            "check"],
  [/star/i,                                "star"],
  [/ml-search__icon|ml-search\b/i,         "search"],
  [/ml-suggest/i,                          "search-recent"],
  [/ml-market-card__save|Save to/i,        "heart"],
  [/Filters?/i,                            "filter"],
  [/ml-player__btn.*Pause|^Pause/i,        "pause"],
  [/ml-player__btn|^Play/i,                "play"],
  [/Notifications?/i,                      "bell"],
  [/Add |ml-upload/i,                      "plus"],
  [/Choose an account/i,                   "google"],
  [/bookings|calendar/i,                   "calendar"],
  [/Nearby|Mumbai|location/i,              "pin"],
  [/bookmark|saved/i,                      "bookmark"],
  [/Share/i,                               "share"],
  [/settings/i,                            "settings"],
  [/viewed|analytics/i,                    "chart"],
  [/Edit /i,                               "pencil"],
  [/ml-post__action/i,                     "comment"],
  [/ml-action-row|ml-choice__meta|ml-row/i, "chevron-right"],
  [/ml-cat-tile__icon/i,                   "search"],
  [/Ignore |Decline|Dismiss|close/i,       "close"],
  [/What you.?ve posted|grid/i,            "grid"],
  [/artists to meet|match/i,               "sparkle"],
  [/What are you looking for/i,            "explore"],
];

const files = [];
(function walk(d) { for (const e of fs.readdirSync(d, { withFileTypes: true })) {
  const p = path.join(d, e.name);
  if (e.isDirectory()) walk(p); else if (e.name === "index.html") files.push(p);
}})(FLOWS);

/* ── collect every distinct shape with its context ─────────────────────────── */
const shapes = new Map();
for (const f of files) {
  const h = fs.readFileSync(f, "utf8");
  for (const m of h.matchAll(/<svg([^>]*)>([\s\S]*?)<\/svg>/g)) {
    const [, attrs, inner] = m;
    const ds = [...inner.matchAll(/\bd="([^"]+)"/g)].map(x => x[1]).sort();
    if (!ds.length) continue;
    const key = ds.join("|");
    if (!shapes.has(key)) {
      /* A LINE DRAWING has no area to fill — emitting it as fill renders nothing.
         One shape here (a plus drawn as two strokes) was invisible for exactly
         that reason. But "no close command" alone is too loose: filled glyphs are
         drawn with curves and close implicitly. The test is no close command AND
         no curve command, which is what separates a stroked line from a filled
         silhouette. Checked by rendering the sheet — the looser version turned
         fifteen filled icons into outlines. */
      const openPath = ds.every(d => !/[zZ]/.test(d) && !/[cCsSqQtTaA]/.test(d));
      shapes.set(key, { key, inner: inner.trim(), n: 0, ctx: [], stroke: /stroke=/.test(attrs) || openPath });
    }
    const s = shapes.get(key); s.n++;
    const pre = h.slice(Math.max(0, m.index - 260), m.index);
    const post = h.slice(m.index + m[0].length, m.index + m[0].length + 70);
    const al = [...pre.matchAll(/aria-label="([^"]+)"/g)].pop();
    const cl = [...pre.matchAll(/class="(ml-[a-z0-9_-]+)/g)].pop();
    if (al) s.ctx.push(al[1].trim());
    if (cl) s.ctx.push(cl[1].trim());
    const t = post.replace(/<[^>]+>/g, " ").trim().slice(0, 24);
    if (t) s.ctx.push(t.replace(/\s+/g," ").trim());
  }
}

/* ── name them ───────────────────────────────────────────────────────────────
   VERIFIED names come first. They were established by rendering every symbol to
   a contact sheet and looking at it — which corrected eleven the context
   heuristic had got wrong: what it called "settings" is a check mark, "comment"
   is a heart, "chart" is a music note, "grid" is a briefcase and "sparkle" is
   two people. Context is a decent guess and no substitute for looking. */
const VERIFIED = JSON.parse(fs.readFileSync(new URL("./icon-names.json", import.meta.url), "utf8"));
const used = new Map(); const todo = [];
for (const s of [...shapes.values()].sort((a, b) => b.n - a.n)) {
  let name = VERIFIED[s.key.slice(0, 48)] || null;
  if (!name) for (const c of s.ctx) { for (const [re, n] of NAMES) if (re.test(c)) { name = n; break; } if (name) break; }
  if (!name) { name = `TODO-${todo.length + 1}`; todo.push(s); }
  /* the same concept drawn twice keeps both, suffixed, so nothing is silently merged */
  let final = name, i = 2;
  while (used.has(final)) final = `${name}-${i++}`;
  used.set(final, s); s.name = final;
}

/* ── sprite ────────────────────────────────────────────────────────────────── */
const symbols = [...used.entries()].map(([name, s]) =>
  `  <symbol id="i-${name}" viewBox="0 0 24 24"${s.stroke ? ' fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"' : ' fill="currentColor"'}>${s.inner}</symbol>`
).join("\n");
const sprite = `<svg xmlns="http://www.w3.org/2000/svg" style="display:none" aria-hidden="true">\n${symbols}\n</svg>\n`;

const spritePath = path.join(OUT, "ml-icons.svg");
if (WRITE) { fs.mkdirSync(OUT, { recursive: true }); fs.writeFileSync(spritePath, sprite); }

/* ── rewrite every instance to a reference ─────────────────────────────────── */
let rewritten = 0, bytesBefore = 0, bytesAfter = 0;
for (const f of files) {
  let h = fs.readFileSync(f, "utf8"); const before = h;
  h = h.replace(/<svg([^>]*)>([\s\S]*?)<\/svg>/g, (m, attrs, inner) => {
    const ds = [...inner.matchAll(/\bd="([^"]+)"/g)].map(x => x[1]).sort();
    if (!ds.length) return m;
    const s = shapes.get(ds.join("|")); if (!s) return m;
    const w = (attrs.match(/width="(\d+)"/) || [])[1];
    const hh = (attrs.match(/height="(\d+)"/) || [])[1];
    /* Snap to the icon scale. Sizes off the scale were measured at 18px x241,
       22px x71 and 14/28/30px — none of which are --icon-* values. */
    const px = Number(w || hh || 20);
    const step = px <= 16 ? 1 : px <= 20 ? 2 : px <= 24 ? 3 : 4;
    const label = (attrs.match(/aria-label="[^"]*"/) || [""])[0];
    const hidden = label ? "" : ' aria-hidden="true"';
    bytesBefore += m.length; rewritten++;
    const out = `<svg class="ml-icon ml-icon--${step}"${label ? " " + label : ""}${hidden}><use href="/guide/system/ml-icons.svg#i-${s.name}"/></svg>`;
    bytesAfter += out.length;
    return out;
  });
  if (h !== before && WRITE) fs.writeFileSync(f, h);
}

/* Idempotence guard. Unlike the other codemods this one CANNOT be re-run over its
   own output: once the screens reference the sprite there are no inline shapes
   left to extract, and a second run would emit an empty sprite over a good one.
   Re-running is a mistake, not a no-op, so it stops rather than doing damage. */
if (shapes.size === 0) {
  const already = files.some(f => /<use href="[^"]*ml-icons\.svg#/.test(fs.readFileSync(f, "utf8")));
  console.error(already
    ? "Already applied — the screens reference the sprite and there is nothing left to extract.\nRestore the screens from git before re-running, or skip this step."
    : "No inline <svg> with path data found. Check the --flows path.");
  process.exit(1);
}

console.log(`distinct shapes      ${shapes.size}`);
console.log(`instances rewritten  ${rewritten}`);
console.log(`sprite               ${sprite.length.toLocaleString()} bytes  ->  ${spritePath}`);
console.log(`inline svg markup    ${bytesBefore.toLocaleString()} -> ${bytesAfter.toLocaleString()} bytes  (${Math.round((1 - bytesAfter / bytesBefore) * 100)}% less in the screens)`);
console.log(`net across the build ${(bytesBefore - bytesAfter - sprite.length).toLocaleString()} bytes saved`);
if (todo.length) {
  console.log(`\n${todo.length} shapes the heuristic would not name — name these by hand:`);
  for (const s of todo) console.log(`   ${s.name.padEnd(9)} ${s.n} uses   context: ${[...new Set(s.ctx)].slice(0,3).join(" · ").slice(0,64)}`);
}
console.log(`\nCSS to add (also in 02-ml-fixes.css):
  .ml-icon        { flex: none; display: inline-block; vertical-align: middle; }
  .ml-icon--1     { width: var(--icon-1); height: var(--icon-1); }
  .ml-icon--2     { width: var(--icon-2); height: var(--icon-2); }
  .ml-icon--3     { width: var(--icon-3); height: var(--icon-3); }
  .ml-icon--4     { width: var(--icon-4); height: var(--icon-4); }`);
