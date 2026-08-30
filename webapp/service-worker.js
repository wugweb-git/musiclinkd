/**
 * Minimal offline shell — keeps the PWA opening instantly and full-screen
 * with no connection, without ever serving a stale build.
 *
 * Rewritten 2026-08-30. The original was cache-first with no revalidation,
 * keyed to a hand-bumped CACHE_NAME whose own comment said "bump this when
 * shared/* changes". It was never bumped. The result: every browser that had
 * ever loaded the site kept serving the v1 shared/app-shell.js and
 * app-shell.css forever, so every change to the shared shell was invisible
 * to returning visitors no matter how many times the site was deployed —
 * deploys looked like no-ops, and a screen would render with months-old
 * behaviour (a removed dev badge reappearing, state filtering doing nothing)
 * while the freshly deployed markup sat right there in the same response.
 *
 * Two changes make that structurally impossible rather than a thing to
 * remember:
 *
 *   1. NETWORK-FIRST for the shell. The network copy wins whenever it can be
 *      reached and refreshes the cache; the cache is a fallback for offline,
 *      not the default source. A deploy takes effect on the next load.
 *   2. CACHE_NAME is stamped by build-hosting.sh from a hash of the shell
 *      files (see BUILD_ID below), so a changed shell lands in a new cache
 *      and every older one is deleted on activate. No manual step to forget.
 */

/* Replaced at build time with a hash of shared/*. The literal placeholder is
   what the dev server serves, which is correct there — dev wants one stable
   cache name and always-fresh files. */
const BUILD_ID = "__BUILD_ID__";
const CACHE_NAME = `musiclinkd-shell-${BUILD_ID}`;
const SHELL_FILES = [
  "./manifest.json",
  "./shared/app-shell.css",
  "./shared/app-shell.js",
  "./shared/icon.svg",
  "./offline.html",
];

self.addEventListener("install", (event) => {
  event.waitUntil(
    // cache: "reload" so installing a new worker never populates the new
    // cache from the HTTP cache's old copies.
    caches
      .open(CACHE_NAME)
      .then((cache) => cache.addAll(SHELL_FILES.map((f) => new Request(f, { cache: "reload" }))))
      .then(() => self.skipWaiting()),
  );
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches
      .keys()
      .then((names) => Promise.all(names.filter((n) => n !== CACHE_NAME).map((n) => caches.delete(n))))
      .then(() => self.clients.claim()),
  );
});

self.addEventListener("fetch", (event) => {
  if (event.request.method !== "GET") return;
  // Only same-origin: fonts and any other cross-origin asset go straight to
  // the network and are never cached here.
  if (new URL(event.request.url).origin !== self.location.origin) return;

  event.respondWith(
    fetch(event.request)
      .then((res) => {
        // Refresh the cached shell copy so the offline fallback is the most
        // recent build rather than whatever was current at install.
        if (res && res.ok && res.type === "basic") {
          const copy = res.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(event.request, copy)).catch(() => {});
        }
        return res;
      })
      .catch(async () => {
        const cached = await caches.match(event.request);
        if (cached) return cached;
        if (event.request.mode === "navigate") return caches.match("./offline.html");
        return new Response("", { status: 504, statusText: "Offline" });
      }),
  );
});
