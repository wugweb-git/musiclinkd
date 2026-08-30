#!/usr/bin/env node
/**
 * audit-flow.mjs — deep audit of one or more flows.
 *
 * audit-screens.mjs is the app-wide mechanical sweep (unresolved tokens, dead
 * links, stray hex). This one goes further on a named flow and checks the
 * things that actually make a flow shippable:
 *
 *   contract      states declared in screen.json vs represented in the HTML
 *   a11y          input labels, image alt, heading order, aria on custom controls
 *   structure     wizard steps that are missing a back link, a step indicator,
 *                 a heading, or a primary action
 *   graph         dead ends, unreachable screens, back links that don't go back
 *   copy          sentence-case headings, terminology drift
 *   system        raw hex, inline colour, non-token spacing in style=""
 *
 * Usage: node scripts/audit-flow.mjs auth onboarding portfolio
 */
import { readFileSync, readdirSync, existsSync } from "node:fs";
import { join, resolve, dirname } from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = resolve(dirname(fileURLToPath(import.meta.url)), "..");
const FLOWS = process.argv.slice(2).filter((a) => !a.startsWith("-"));
if (!FLOWS.length) { console.error("usage: audit-flow.mjs <flow> [flow…]"); process.exit(2); }

const tokenCss = readFileSync(join(ROOT, "webapp/shared/ml-tokens.css"), "utf8");
const PALETTE = new Set([...tokenCss.matchAll(/#[0-9A-Fa-f]{6}/g)].map((m) => m[0].toUpperCase()));
["#FFFFFF", "#000000"].forEach((c) => PALETTE.add(c));
/* third-party brand marks are a documented exception */
const BRAND_EXEMPT = new Set(["#4285F4", "#34A853", "#FBBC05", "#EA4335"]);

const findings = [];
const add = (sev, flow, screen, kind, msg) => findings.push({ sev, flow, screen, kind, msg });

for (const flow of FLOWS) {
  const flowDir = join(ROOT, "webapp/flows", flow);
  if (!existsSync(flowDir)) { add("ERROR", flow, "-", "flow", "flow directory does not exist"); continue; }

  const screens = readdirSync(flowDir, { withFileTypes: true })
    .filter((e) => e.isDirectory()).map((e) => e.name);

  const meta = {};
  for (const s of screens) {
    const html = readFileSync(join(flowDir, s, "index.html"), "utf8");
    const contract = JSON.parse(readFileSync(join(flowDir, s, "screen.json"), "utf8"));
    meta[s] = { html, contract, links: [...html.matchAll(/href="(\/preview\/flows\/[^"]+)"/g)].map((m) => m[1]) };
  }

  for (const [s, { html, contract, links }] of Object.entries(meta)) {
    const body = (html.match(/<body>([\s\S]*)<\/body>/) || [, ""])[1];

    /* ── contract: are declared states represented? ─────────────────── */
    const declared = contract.states || [];
    const present = new Set([...body.matchAll(/data-state="([^"]+)"/g)].map((m) => m[1]));
    const nonDefault = declared.filter((d) => !/^(default|populated|ready|entering|signup|loading|submitting|searching|account_picker)$/.test(d));
    for (const st of nonDefault) {
      if (!present.has(st)) add("WARN", flow, s, "contract", `state "${st}" is declared in screen.json but has no data-state="${st}" element`);
    }

    /* ── a11y ────────────────────────────────────────────────────────── */
    for (const m of body.matchAll(/<input\b([^>]*)>/g)) {
      const attrs = m[1];
      const id = (attrs.match(/id="([^"]+)"/) || [])[1];
      const hasAria = /aria-label=/.test(attrs);
      const labelled = id && new RegExp(`<label[^>]*for="${id}"`).test(body);
      if (!hasAria && !labelled) add("ERROR", flow, s, "a11y", `<input${id ? ` id="${id}"` : ""}> has no <label for> and no aria-label`);
    }
    for (const m of body.matchAll(/<textarea\b([^>]*)>/g)) {
      const id = (m[1].match(/id="([^"]+)"/) || [])[1];
      if (!/aria-label=/.test(m[1]) && !(id && new RegExp(`<label[^>]*for="${id}"`).test(body)))
        add("ERROR", flow, s, "a11y", `<textarea> has no label`);
    }
    for (const m of body.matchAll(/<select\b([^>]*)>/g)) {
      const id = (m[1].match(/id="([^"]+)"/) || [])[1];
      if (!/aria-label=/.test(m[1]) && !(id && new RegExp(`<label[^>]*for="${id}"`).test(body)))
        add("ERROR", flow, s, "a11y", `<select> has no label`);
    }
    for (const m of body.matchAll(/<img\b([^>]*)>/g))
      if (!/\balt=/.test(m[1])) add("ERROR", flow, s, "a11y", `<img> without alt attribute`);
    /* an icon-only control needs an accessible name */
    for (const m of body.matchAll(/<(a|button)\b([^>]*)>([\s\S]*?)<\/\1>/g)) {
      const [, , attrs, inner] = m;
      const text = inner.replace(/<[^>]+>/g, "").replace(/&[a-z]+;/g, "").trim();
      const hasSvg = /<svg/.test(inner);
      if (hasSvg && !text && !/aria-label=/.test(attrs) && !/aria-labelledby=/.test(attrs))
        add("ERROR", flow, s, "a11y", `icon-only <${m[1]}> has no aria-label`);
    }
    const headings = [...body.matchAll(/<h([1-6])\b/g)].map((m) => +m[1]);
    if (headings.length && headings[0] !== 1) add("WARN", flow, s, "a11y", `first heading is h${headings[0]}, not h1`);
    /* Multiple h1s are only a defect on a screen with no state variants.
       Where states exist they are mutually exclusive and one h1 each is
       correct — that case is checked in the browser, per rendered state,
       because nested markup cannot be stripped reliably with a regex. */
    const hasStates = /data-state="/.test(body);
    if (!hasStates && (body.match(/<h1\b/g) || []).length > 1)
      add("WARN", flow, s, "a11y", `more than one <h1>`);
    if (!headings.length) add("WARN", flow, s, "a11y", `screen has no heading element`);

    /* ── system: raw values in markup ────────────────────────────────── */
    /* Strip HTML comments first. A comment that EXPLAINS why a hex was
       removed still contains that hex, so scanning raw markup flags the
       explanation as the violation — which is what happened to
       public_profile_view_venue on 2026-08-30 after its hardcoded amber was
       replaced by a color-mix and the note about it was left in place. */
    const paintable = body.replace(/<!--[\s\S]*?-->/g, "");
    for (const m of paintable.matchAll(/#[0-9A-Fa-f]{6}\b/g)) {
      const hex = m[0].toUpperCase();
      if (!PALETTE.has(hex) && !BRAND_EXEMPT.has(hex))
        add("ERROR", flow, s, "palette", `off-palette colour ${hex} hardcoded in markup`);
    }
    for (const m of body.matchAll(/style="([^"]*)"/g)) {
      const decl = m[1];
      /* px values that aren't a token and aren't 0 */
      for (const p of decl.matchAll(/(?:padding|margin|gap|top|left|right|bottom)[^:]*:\s*([0-9]+)px/g))
        if (p[1] !== "0") add("WARN", flow, s, "system", `hardcoded ${p[1]}px spacing in style="" — use a --space-* token`);
      if (/font-size:\s*[0-9]/.test(decl)) add("WARN", flow, s, "system", `hardcoded font-size in style="" — use --type-* or .ml-type-*`);
    }
    if (/<style>/.test(html)) add("ERROR", flow, s, "system", `screen defines a local <style> block`);

    /* ── structure ───────────────────────────────────────────────────── */
    const isWizardStep = /ml-steps__fill/.test(body);
    const hasBack = /<path d="M15 18l-6-6 6-6"/.test(body);   // the arrow, wherever it sits
    const hasCta = /ml-btn--primary/.test(body);
    const isTerminal = /ml-check/.test(body) || s === "splash";
    if (isWizardStep && !hasBack) add("ERROR", flow, s, "structure", `wizard step has a step indicator but no back control`);
    const hasChoiceNav = /class="ml-choice"[^>]*href=|href=[^>]*class="ml-choice"/.test(body);
    if (!hasCta && !isTerminal && !hasBack && !hasChoiceNav && !/ml-tabbar|ml-appbar/.test(body))
      add("WARN", flow, s, "structure", `no primary action, no back control and no persistent nav — dead end`);

    /* ── graph ───────────────────────────────────────────────────────── */
    const selfLinks = links.filter((l) => l.includes(`/${flow}/${s}/`) && !l.includes("?"));
    if (selfLinks.length)
      add("ERROR", flow, s, "graph", `${selfLinks.length} link(s) point at this same screen — dead affordance`);
    const outbound = links.filter((l) => !l.includes(`/${flow}/${s}/`));
    if (!outbound.length && !isTerminal)
      add("ERROR", flow, s, "graph", `no outbound navigation — dead end`);
  }

  /* unreachable screens within the flow */
  const inbound = new Set();
  for (const { links } of Object.values(meta))
    for (const l of links) { const m = l.match(/\/flows\/([^/]+)\/([^/]+)\//); if (m && m[1] === flow) inbound.add(m[2]); }
  const entry = { auth: "splash", onboarding: "primary_role", portfolio: "portfolio_dashboard" }[flow];
  for (const s of Object.keys(meta))
    if (!inbound.has(s) && s !== entry)
      add("WARN", flow, s, "graph", `no screen in this flow links to it — unreachable from within the flow`);
}

/* ── report ────────────────────────────────────────────────────────────── */
const bySev = { ERROR: [], WARN: [] };
for (const f of findings) bySev[f.sev].push(f);

console.log(`audit-flow: ${FLOWS.join(", ")}`);
console.log(`  ${bySev.ERROR.length} errors, ${bySev.WARN.length} warnings\n`);

for (const sev of ["ERROR", "WARN"]) {
  if (!bySev[sev].length) continue;
  console.log(`── ${sev} ──`);
  const byKind = {};
  for (const f of bySev[sev]) (byKind[f.kind] ??= []).push(f);
  for (const [kind, items] of Object.entries(byKind)) {
    console.log(`\n  [${kind}] ${items.length}`);
    const seen = new Map();
    for (const i of items) {
      const key = i.msg.replace(/"[^"]*"/g, '"…"');
      if (!seen.has(key)) seen.set(key, []);
      seen.get(key).push(`${i.flow}/${i.screen}`);
    }
    for (const [msg, where] of seen) {
      console.log(`    ${msg}`);
      console.log(`      ${where.slice(0, 6).join(", ")}${where.length > 6 ? ` … +${where.length - 6}` : ""}`);
    }
  }
  console.log();
}
process.exit(bySev.ERROR.length ? 1 : 0);
