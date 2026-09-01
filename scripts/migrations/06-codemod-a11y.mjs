#!/usr/bin/env node
/**
 * 06-codemod-a11y.mjs — second sweep, for the violations that need markup.
 * Run after 04. Idempotent.
 */
import fs from "node:fs";
import path from "node:path";
const root = process.argv[2];
const DRY = process.argv.includes("--dry");
const map = JSON.parse(fs.readFileSync(process.argv[3] || "flows-map.json", "utf8"));
const title = {};
for (const s of map.screens) title[`${s.flowKey}/${s.screenKey}`] = s.screenTitle;

const files = [];
(function walk(d){ for (const e of fs.readdirSync(d,{withFileTypes:true})) {
  const p = path.join(d,e.name);
  if (e.isDirectory()) walk(p); else if (e.name==="index.html") files.push(p);
}})(root);

const n = Object.create(null); const bump = (k,c=1)=>{n[k]=(n[k]||0)+c;};

for (const f of files) {
  let s = fs.readFileSync(f,"utf8"); const before = s;

  // A — <a> wrapping only images: give it a name from the destination screen.
  s = s.replace(/<a([^>]*?)href="\/preview\/flows\/([^"?#]+?)(?:\/index\.html)?"([^>]*)>([\s\S]*?)<\/a>/g,
    (m, pre, href, post, inner) => {
      if (/aria-label=/.test(pre + post)) return m;
      const text = inner.replace(/<[^>]+>/g, "").trim();
      if (text) return m;                       // has a visible name already
      if (!/<img|<svg/.test(inner)) return m;   // nothing to name
      const t = title[href.replace(/\/$/, "")];
      if (!t) return m;
      bump("link-name");
      return `<a${pre}href="/preview/flows/${href}${/\/index\.html/.test(m)?"/index.html":""}"${post} aria-label="${t}">${inner}</a>`;
    });

  // B — role=checkbox / radio with no accessible name: label it from the row title.
  s = s.replace(/<(?:div|label|span)([^>]*class="[^"]*ml-(?:check-row|choice)[^"]*"[^>]*)>([\s\S]*?)(<span class="ml-check-ctl[^"]*"[^>]*role="(?:checkbox|radio)"[^>]*>)/g,
    (m, rowAttrs, mid, ctl) => {
      if (/aria-label|aria-labelledby/.test(ctl)) return m;
      const t = (mid.match(/class="ml-(?:choice|check-row|row)__title"[^>]*>([^<]+)</) || [])[1]
             || (mid.replace(/<[^>]+>/g," ").trim().split(/\s{2,}|\n/)[0] || "").trim();
      if (!t) return m;
      bump("toggle-name");
      return m.replace(ctl, ctl.replace(/(role="(?:checkbox|radio)")/, `$1 aria-label="${t.replace(/"/g,"&quot;").slice(0,80)}"`));
    });

  // B2 — aria-pressed is invalid on a bare <a>. The shell now sets aria-current
  //      at runtime; strip the baked-in attribute so the two cannot disagree.
  s = s.replace(/(<a\b(?:(?!role=)[^>])*?)\s+aria-pressed="(?:true|false)"/g,
    (m, a) => { bump("strip-aria-pressed"); return a; });

  // C — aria-label on a div needs a role.
  s = s.replace(/<div((?:(?!role=)[^>])*?aria-label="[^"]*"(?:(?!role=)[^>])*?)>/g,
    (m, a) => { bump("qr-role"); return `<div${a} role="img">`; });

  // D — scroll containers with no focusable content.
  s = s.replace(/<(main|div)([^>]*class="[^"]*(?:app-content--scroll|ml-rail)[^"]*"(?:(?!tabindex)[^>])*)>([\s\S]*?)<\/\1>/g,
    (m, tag, attrs, inner) => {
      if (/<a\b|<button\b|<input\b|<select\b|<textarea\b|tabindex=/.test(inner)) return m;
      bump("scroll-focus");
      return `<${tag}${attrs} tabindex="0">${inner}</${tag}>`;
    });

  if (s !== before) { bump("files-changed"); if (!DRY) fs.writeFileSync(f, s); }
}
console.log(DRY ? "DRY RUN\n" : "APPLIED\n");
console.log(`files scanned ${files.length}`);
for (const k of Object.keys(n).sort()) console.log(`  ${k.padEnd(16)} ${n[k]}`);
