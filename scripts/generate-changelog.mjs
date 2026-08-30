#!/usr/bin/env node
/**
 * Generates frontend/website/changelog/index.html from the repo's own
 * CHANGELOG.md, so the public page and the engineering changelog can't
 * drift apart — there's one source of truth (CHANGELOG.md), regenerated
 * into HTML at build time, never hand-edited separately.
 *
 * Handles the specific markdown subset CHANGELOG.md actually uses: #/##/###
 * headings, "- " bullet lists, `code`, **bold**. Not a general markdown
 * parser — if CHANGELOG.md starts using other syntax, extend this.
 */
import { existsSync, mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const rootDir = join(dirname(fileURLToPath(import.meta.url)), "..");
const changelogPath = join(rootDir, "CHANGELOG.md");
const outDir = join(rootDir, "frontend/website/changelog");
const outPath = join(outDir, "index.html");

function escapeHtml(s) {
  return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

function inline(s) {
  let out = escapeHtml(s);
  out = out.replace(/`([^`]+)`/g, "<code>$1</code>");
  out = out.replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>");
  return out;
}

function mdToHtml(md) {
  const lines = md.split("\n");
  const html = [];
  let inList = false;
  let paraBuf = [];

  function flushPara() {
    if (paraBuf.length) {
      html.push(`<p>${inline(paraBuf.join(" "))}</p>`);
      paraBuf = [];
    }
  }
  function closeList() {
    if (inList) {
      html.push("</ul>");
      inList = false;
    }
  }

  for (const raw of lines) {
    const line = raw.trimEnd();
    if (line.trim() === "") {
      flushPara();
      continue;
    }
    const h3 = line.match(/^### (.+)/);
    const h2 = line.match(/^## (.+)/);
    const h1 = line.match(/^# (.+)/);
    const bullet = line.match(/^- (.+)/);

    if (h1) {
      flushPara();
      closeList();
      continue; // page's own <h1> is set separately from the title, skip the markdown one
    }
    if (h2) {
      flushPara();
      closeList();
      html.push(`<h2>${inline(h2[1])}</h2>`);
      continue;
    }
    if (h3) {
      flushPara();
      closeList();
      html.push(`<h3>${inline(h3[1])}</h3>`);
      continue;
    }
    if (bullet) {
      flushPara();
      if (!inList) {
        html.push("<ul>");
        inList = true;
      }
      html.push(`<li>${inline(bullet[1])}</li>`);
      continue;
    }
    closeList();
    paraBuf.push(line);
  }
  flushPara();
  closeList();
  return html.join("\n      ");
}

// Same nav + footer markup as frontend/website/index.html, kept manually in
// sync (extracted, not re-derived at build time — if the homepage's nav or
// footer changes, copy the new markup here too). Anchor links rewritten to
// `/#section` so they still work from this page instead of just scrolling
// nowhere on an id that doesn't exist here.
const NAV = `<header class="nav" id="nav" role="banner">
  <div class="container nav__inner">
    <a href="/" class="nav__logo" aria-label="MusicLinkd home">
      <svg class="nav__logo-mark" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true"><rect width="64" height="64" rx="16" fill="#141414"/><rect x="16" y="26" width="5" height="20" rx="2.5" fill="#FFFFFF"/><rect x="25.5" y="16" width="5" height="32" rx="2.5" fill="#EA4C89"/><rect x="35" y="22" width="5" height="24" rx="2.5" fill="#FFFFFF"/><rect x="44.5" y="30" width="5" height="16" rx="2.5" fill="#EA4C89"/></svg>
      <span style="font-family:'Unica One','Inter Tight',sans-serif;font-weight:500;letter-spacing:-0.02em;text-transform:uppercase;color:#141621">MUSIC<em style="font-style:normal;color:#FF005C">LINKD</em></span>
    </a>

    <nav id="navLinks" class="nav__links" aria-label="Primary">
      <a href="/#problem">Problem</a>
      <a href="/#platform">Platform</a>
      <a href="/#modules">Modules</a>
      <a href="/#journeys">How It Works</a>
      <a href="/#gallery">Screens</a>
      <a href="/#trust">Trust</a>
      <a href="/#ai">AI</a>
      <a href="/#faq">FAQ</a>
    </nav>

    <div class="nav__actions">
      <a href="/#cta" class="btn btn--primary btn--sm nav__cta-desktop">Join Early Access</a>
      <button id="navToggle" class="nav__toggle" aria-label="Toggle menu" aria-expanded="false" aria-controls="navLinks">
        <span></span><span></span><span></span>
      </button>
    </div>
  </div>
</header>`;

const FOOTER = `<footer class="footer" role="contentinfo">
  <div class="container">
    <div class="footer__grid">
      <div class="footer__brand">
        <a href="/" style="font-family:'Unica One','Inter Tight',sans-serif;font-weight:500;font-size:22px;letter-spacing:-0.02em;text-transform:uppercase;text-decoration:none;color:#141621;display:inline-flex;align-items:center;gap:10px">MUSIC<em style="color:#FF005C;font-style:normal">LINKD</em></a>
        <p>India's professional music ecosystem. Connecting artists, studios, engineers, labels, venues and music businesses through one unified platform.</p>
      </div>
      <div class="footer__col">
        <h4>Platform</h4>
        <ul>
          <li><a href="/#problem">Problem</a></li>
          <li><a href="/#modules">Modules</a></li>
          <li><a href="/#journeys">How It Works</a></li>
          <li><a href="/#gallery">Screens</a></li>
          <li><a href="/#trust">Trust</a></li>
          <li><a href="/#ai">AI Features</a></li>
        </ul>
      </div>
      <div class="footer__col">
        <h4>Company</h4>
        <ul>
          <li><a href="/#testimonials">Testimonials</a></li>
          <li><a href="/#roadmap">Roadmap</a></li>
          <li><a href="/#faq">FAQ</a></li>
          <li><a href="mailto:partner@musiclinkd.com">Partner With Us</a></li>
          <li><a href="/#cta">Early Access</a></li>
          <li><a href="/styleguide.html">Styleguide</a></li>
          <li><a href="/changelog/">Changelog</a></li>
        </ul>
      </div>
      <div class="footer__col">
        <h4>Legal</h4>
        <ul>
          <li><a href="/legal/privacy/">Privacy Policy</a></li>
          <li><a href="/legal/terms/">Terms of Service</a></li>
        </ul>
      </div>
    </div>
    <div class="footer__bottom">
      <span>© MusicLinkd. All rights reserved. · Powered by <a href="https://wugweb.com" target="_blank" rel="noopener" style="color:inherit;font-weight:600;text-decoration:none">Wugweb</a></span>
      <div class="footer__social">
        <a href="#" aria-label="Instagram"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="2" width="20" height="20" rx="5"/><path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"/><line x1="17.5" y1="6.5" x2="17.51" y2="6.5"/></svg></a>
        <a href="#" aria-label="X (Twitter)"><svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" stroke="none"><path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"/></svg></a>
        <a href="#" aria-label="YouTube"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22.54 6.42a2.78 2.78 0 0 0-1.94-2C18.88 4 12 4 12 4s-6.88 0-8.6.46a2.78 2.78 0 0 0-1.94 2A29 29 0 0 0 1 11.75a29 29 0 0 0 .46 5.33A2.78 2.78 0 0 0 3.4 19.1c1.72.46 8.6.46 8.6.46s6.88 0 8.6-.46a2.78 2.78 0 0 0 1.94-2 29 29 0 0 0 .46-5.25 29 29 0 0 0-.46-5.33z"/><polygon points="9.75 15.02 15.5 11.75 9.75 8.48 9.75 15.02"/></svg></a>
        <a href="#" aria-label="LinkedIn"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 8a6 6 0 0 1 6 6v7h-4v-7a2 2 0 0 0-2-2 2 2 0 0 0-2 2v7h-4v-7a6 6 0 0 1 6-6z"/><rect x="2" y="9" width="4" height="12"/><circle cx="4" cy="4" r="2"/></svg></a>
      </div>
    </div>
  </div>
</footer>`;

function main() {
  if (!existsSync(changelogPath)) {
    console.warn("[generate-changelog] CHANGELOG.md not found, skipping.");
    return;
  }
  const md = readFileSync(changelogPath, "utf8");
  const body = mdToHtml(md);

  const page = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Changelog — MusicLinkd</title>
  <meta name="description" content="What's new and what's changed across MusicLinkd — generated from the repository's own CHANGELOG.md.">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Inter+Tight:wght@600;700;800&family=Unica+One&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="/assets/css/musiclinkd.css">
  <link rel="icon" type="image/svg+xml" href="/assets/img/favicon.svg">
</head>
<body>
${NAV}

<main id="main">
  <section class="section" style="padding-top:calc(var(--nav-height, 72px) + 48px)">
    <div class="container" style="max-width:760px">
      <div class="eyebrow">Changelog</div>
      <h1 class="h2" style="margin-bottom:8px">What's new</h1>
      <p class="lead" style="margin-bottom:40px">Generated from CHANGELOG.md — always in sync with the repository, never hand-edited separately.</p>

      ${body}
    </div>
  </section>
</main>

${FOOTER}

<script src="/assets/js/main.js" defer></script>
</body>
</html>
`;

  mkdirSync(outDir, { recursive: true });
  writeFileSync(outPath, page);
  console.log("[generate-changelog] Wrote frontend/website/changelog/index.html");
}

main();
