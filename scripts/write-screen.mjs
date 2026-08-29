#!/usr/bin/env node
/**
 * write-screen.mjs — emit a webapp screen with the correct boilerplate.
 *
 * NOT a template engine and NOT run at build time. It is a one-shot authoring
 * helper: the <head> of a screen is 20 lines of meta/link boilerplate that must
 * be byte-identical across screens (absolute /preview paths, viewport-fit=cover,
 * the three system stylesheets in the right order). Hand-copying that 139 times
 * is how the theme-color drifted to a retired navy on every screen and how
 * five dead links reached a hand-written screen.
 *
 * The BODY of every screen is hand-authored and passed in. Output is plain
 * HTML with no generated marker — once written, a screen is edited directly.
 *
 * Import and call writeScreen(); see scripts/screens/*.mjs for callers.
 */
import { writeFileSync, existsSync, readFileSync } from "node:fs";
import { join, resolve, dirname } from "node:path";
import { fileURLToPath } from "node:url";

export const ROOT = resolve(dirname(fileURLToPath(import.meta.url)), "..");

/** Fail loudly rather than emit a dead link — the audit catches these too,
 *  but catching it at authoring time is cheaper.
 *
 *  Deferred, not immediate: a flow's screens link to each other, so screen 1
 *  legitimately references screen 12 before screen 12 has been written. The
 *  check therefore records links as it goes and verifies them once the whole
 *  run has finished — call finish() at the end of a generator. */
const pending = [];

export function checkLinks(html, label) {
  for (const m of html.matchAll(/(?:href|src)="(\/preview\/[^"#?]+)"/g)) pending.push([label, m[1]]);
}

/** Verify every link recorded during this run. Throws listing all dead ones. */
export function finish(tag = "") {
  const dead = new Map();
  for (const [label, href] of pending) {
    if (existsSync(join(ROOT, "webapp", href.replace(/^\/preview\//, "")))) continue;
    if (!dead.has(href)) dead.set(href, new Set());
    dead.get(href).add(label);
  }
  if (dead.size) {
    const lines = [...dead].map(([h, from]) => `  ${h}\n      linked from: ${[...from].join(", ")}`);
    throw new Error(`${tag}dead links (${dead.size}):\n${lines.join("\n")}`);
  }
  return pending.length;
}

export function writeScreen({ flow, screen, title, body, scroll = true }) {
  const dir = join(ROOT, "webapp/flows", flow, screen);
  if (!existsSync(join(dir, "screen.json"))) {
    throw new Error(`${flow}/${screen}: no screen.json — this is not a registered screen`);
  }
  const contract = JSON.parse(readFileSync(join(dir, "screen.json"), "utf8"));
  const heading = title ?? contract.screenTitle ?? screen;

  const html = `<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, viewport-fit=cover" />
<meta name="theme-color" content="#0D0D0D" />
<meta name="mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" />
<meta name="screen-json-path" content="/preview/flows/${flow}/${screen}/screen.json" />
<link rel="manifest" href="/preview/manifest.json" />
<link rel="apple-touch-icon" href="/preview/shared/icon.svg" />
<title>MusicLinkd — ${heading}</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Inter+Tight:wght@600;700;800&display=swap" rel="stylesheet">
<link rel="stylesheet" href="/guide/system/ml-tokens.css" />
<link rel="stylesheet" href="/guide/system/ml-core.css" />
<link rel="stylesheet" href="/guide/system/ml-widgets.css" />
<link rel="stylesheet" href="/preview/shared/app-shell.css" />
</head>
<body>
${body.trimEnd()}
  <script src="/preview/shared/app-shell.js" defer></script>
</body>
</html>
`;
  checkLinks(html, `${flow}/${screen}`);
  writeFileSync(join(dir, "index.html"), html);
  return `${flow}/${screen}`;
}

/* ── icons ─────────────────────────────────────────────────────────────────
 * Inline from the real 423-icon library at
 * frontend/guide/musiclinkd-system/icons/ rather than hand-drawing paths.
 * Those icons are Gestalt's (Apache-2.0), already extracted, and hand-drawing
 * substitutes is how a system ends up with six slightly different microphones.
 *
 * They ship as solid 24x24 with an implicit black fill, so the wrapper strips
 * the fixed size and sets fill:currentColor — that is what lets one icon sit
 * on paper, on ink and on a scrim without a second copy.
 */
const ICON_DIR = resolve(ROOT, "frontend/guide/musiclinkd-system/icons");
const iconCache = new Map();

export function icon(name, { size = 20, cls = "" } = {}) {
  if (!iconCache.has(name)) {
    const file = join(ICON_DIR, `${name}.svg`);
    if (!existsSync(file)) {
      throw new Error(`icon "${name}" is not in the library — check frontend/guide/musiclinkd-system/icons/`);
    }
    iconCache.set(name, readFileSync(file, "utf8")
      .replace(/\s*width="\d+"|\s*height="\d+"/g, "")
      .replace(/<svg /, '<svg fill="currentColor" ')
      .trim());
  }
  return iconCache.get(name)
    .replace(/<svg /, `<svg width="${size}" height="${size}" aria-hidden="true"${cls ? ` class="${cls}"` : ""} `);
}

/** Every icon name this project uses, checked once so a typo fails the run
 *  instead of rendering an empty box. */
export function assertIcons(names) {
  const missing = names.filter((n) => !existsSync(join(ICON_DIR, `${n}.svg`)));
  if (missing.length) throw new Error(`missing icons: ${missing.join(", ")}`);
  return names.length;
}

/* Small shared fragments so 31 screens cannot disagree about a back arrow. */
export const backArrow = (href, label = "Back") =>
  `<a class="ml-icon-btn ml-icon-btn--bare" href="${href}" aria-label="${label}">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M15 18l-6-6 6-6"/></svg>
        </a>`;

export const steps = (n, total) =>
  `<div class="ml-steps">
        <span class="ml-steps__label">${n}/${total}</span>
        <span class="ml-steps__track"><span class="ml-steps__fill" style="width:${Math.round((n / total) * 100)}%"></span></span>
      </div>`;

export const checkMark = `<div class="ml-check">
        <svg width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6L9 17l-5-5"/></svg>
      </div>`;

export const tick = `<svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6L9 17l-5-5"/></svg>`;
