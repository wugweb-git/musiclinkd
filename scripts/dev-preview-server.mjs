#!/usr/bin/env node
/**
 * dev-preview-server.mjs — serve the webapp preview and the guide at the SAME
 * paths Firebase Hosting serves them, without running a build.
 *
 * A Node port of dev-preview-server.py, which still exists and is still
 * correct. The port is not a rewrite for its own sake: the Python one cannot
 * run under the Browser pane's launcher on this machine. macOS denies that
 * launcher access to ~/Documents, and CPython trips on it immediately —
 * `os.getcwd()` itself raises PermissionError, so every request 500s before
 * any file is touched. Node does not call getcwd() to serve a file, so it can
 * work where CPython cannot. If Node is denied too, this fails the same way
 * and the answer is a Files-and-Folders grant for the Claude app, or moving
 * the repo out of ~/Documents.
 *
 * Same mapping, in memory, with no copying:
 *
 *     /preview/...  ->  webapp/...
 *     /guide/...    ->  frontend/guide/...
 *     /assets/...   ->  frontend/website/assets/...
 *     /...          ->  repo root
 *
 * Every screen links its CSS and its next screen with absolute /preview/...
 * paths. That is deliberate: Firebase Hosting runs cleanUrls:true and
 * trailingSlash:false, so a relative "bundle.js" on /preview/flows/x/y
 * resolves to /preview/flows/x/bundle.js and 404s. The cost is that screens
 * cannot be opened over file:// or from a plain static server rooted anywhere
 * else — hence this.
 *
 * cleanUrls is emulated too, so /preview/flows/a/b serves .../b/index.html
 * exactly as production does. Without that the local preview would disagree
 * with the deployed site about what a URL means.
 *
 * Run via the Browser pane ("preview" in .claude/launch.json), not by hand.
 * Port 5501, so it can sit alongside the website server on 5500.
 */
import { createServer } from "node:http";
import { createReadStream, statSync } from "node:fs";
import { join, resolve, extname, dirname } from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = resolve(dirname(fileURLToPath(import.meta.url)), "..");
/* The launcher assigns a port via PORT and expects the server to take it.
   Hardcoding 5501 meant a stale instance from an earlier session held the
   port and every later start failed; nothing here needs a fixed port —
   there are no OAuth callbacks, webhooks or CORS origins pointing at it. */
const PORT = Number(process.env.PORT) || 5501;

/* Longest prefix first, so /preview wins before /. */
const MOUNTS = [
  ["/preview", "webapp"],
  ["/guide", "frontend/guide"],
  ["/assets", "frontend/website/assets"],
];

const TYPES = {
  ".html": "text/html; charset=utf-8",
  ".css": "text/css; charset=utf-8",
  ".js": "text/javascript; charset=utf-8",
  ".mjs": "text/javascript; charset=utf-8",
  ".json": "application/json; charset=utf-8",
  ".svg": "image/svg+xml",
  ".png": "image/png",
  ".jpg": "image/jpeg",
  ".jpeg": "image/jpeg",
  ".webp": "image/webp",
  ".avif": "image/avif",
  ".gif": "image/gif",
  ".mp4": "video/mp4",
  ".webm": "video/webm",
  ".woff": "font/woff",
  ".woff2": "font/woff2",
  ".ico": "image/x-icon",
  ".txt": "text/plain; charset=utf-8",
  ".webmanifest": "application/manifest+json",
};

/** Map a request path onto a real file, refusing to escape ROOT. */
function translate(urlPath) {
  const clean = decodeURIComponent(urlPath.split("?")[0].split("#")[0]);
  let rel = clean;
  for (const [prefix, dir] of MOUNTS) {
    if (clean === prefix || clean.startsWith(prefix + "/")) {
      rel = join(dir, clean.slice(prefix.length));
      break;
    }
  }
  const abs = resolve(ROOT, "." + (rel.startsWith("/") ? rel : "/" + rel));
  // A path that resolves outside ROOT is refused rather than served.
  if (abs !== ROOT && !abs.startsWith(ROOT + "/")) return null;
  return abs;
}

/** A directory serves its index.html — and so does an extensionless path,
    which is what Firebase's cleanUrls does in production. */
function fileFor(abs) {
  try {
    const st = statSync(abs);
    if (st.isDirectory()) return fileFor(join(abs, "index.html"));
    return abs;
  } catch {
    if (!extname(abs)) {
      try {
        const asIndex = join(abs, "index.html");
        statSync(asIndex);
        return asIndex;
      } catch { /* fall through */ }
      try {
        statSync(abs + ".html");
        return abs + ".html";
      } catch { /* fall through */ }
    }
    return null;
  }
}

createServer((req, res) => {
  const abs = translate(req.url || "/");
  const file = abs && fileFor(abs);
  if (!file) {
    res.writeHead(404, { "content-type": "text/plain; charset=utf-8" });
    res.end(`404 — no file for ${req.url}\nServing ${ROOT}\n`);
    return;
  }
  res.writeHead(200, {
    "content-type": TYPES[extname(file).toLowerCase()] || "application/octet-stream",
    // Never cache in dev. The deployed site had a genuine bug where an
    // immutable header pinned a stale build for a year; a dev server that
    // caches would hide exactly that class of problem.
    "cache-control": "no-store",
  });
  createReadStream(file).pipe(res);
}).listen(PORT, () => {
  console.log(`dev-preview-server: http://localhost:${PORT}/preview/index.html  (root ${ROOT})`);
});
