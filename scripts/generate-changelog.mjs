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
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Inter+Tight:wght@600;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="/assets/css/tokens.css">
  <link rel="stylesheet" href="/assets/css/main.css">
  <link rel="icon" type="image/svg+xml" href="/assets/img/favicon.svg">
</head>
<body>
  <main class="legal-body" role="main">
    <div class="container">
      <div class="legal-hero">
        <a href="/" style="font-family:var(--font-display);font-weight:800;font-size:22px;letter-spacing:-0.05em;text-decoration:none;color:var(--text-primary);display:inline-flex;align-items:center;gap:10px;margin-bottom:24px">MusicLink<em style="color:var(--brand-pink);font-style:normal">d</em></a>
        <h1 class="h2">Changelog</h1>
        <p class="muted">Generated from CHANGELOG.md — always in sync with the repository.</p>
      </div>

      ${body}

      <p style="margin-top:32px"><a href="/" class="btn btn--ghost">← Back to MusicLinkd</a></p>
    </div>
  </main>
</body>
</html>
`;

  mkdirSync(outDir, { recursive: true });
  writeFileSync(outPath, page);
  console.log("[generate-changelog] Wrote frontend/website/changelog/index.html");
}

main();
