/**
 * Minimal offline shell — caches the environment files (manifest, shared
 * shell, template) so the PWA opens instantly and full-screen even with no
 * connection. Deliberately does NOT cache per-screen HTML/JSON — those are
 * still being designed and will change constantly; caching them here would
 * mean stale content served silently. Bump CACHE_NAME when shared/* changes
 * so old clients pick up the new shell instead of an outdated cached copy.
 */
const CACHE_NAME = "musiclinkd-shell-v1";
const SHELL_FILES = [
  "./manifest.json",
  "./shared/app-shell.css",
  "./shared/app-shell.js",
  "./shared/icon.svg",
  "./offline.html",
];

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(SHELL_FILES)).then(() => self.skipWaiting()),
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
  event.respondWith(
    caches.match(event.request).then((cached) => {
      if (cached) return cached;
      return fetch(event.request).catch(() => {
        if (event.request.mode === "navigate") return caches.match("./offline.html");
        return new Response("", { status: 504, statusText: "Offline" });
      });
    }),
  );
});
