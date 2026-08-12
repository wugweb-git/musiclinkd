/**
 * Native-app ENVIRONMENT behaviors — the JS half of app-shell.css. Include
 * this on every screen (`<script src="/shared/app-shell.js" defer></script>`
 * with a relative path adjusted to depth). Exposes `window.MusicLinkd` with
 * a small API; does nothing visual beyond the dev metadata badge.
 */
(function () {
  "use strict";

  // Single source of truth for where this build is mounted. Firebase Hosting
  // serves this app at musiclinkd.com/preview — and, critically, with
  // cleanUrls+trailingSlash:false, a page at .../flows/auth/splash has NO
  // trailing slash, so the browser treats "splash" as a file-position
  // segment: any *relative* "./x" or "../x" reference from that document
  // resolves against the WRONG base (one level too shallow). Absolute,
  // root-anchored paths built from this constant are the only fix that
  // isn't fragile to that redirect behavior. If this build is ever mounted
  // somewhere else, this is the one line to change.
  const MOUNT_BASE = "/preview";

  const MusicLinkd = {
    screen: null, // populated by loadScreenContract()
    mountBase: MOUNT_BASE,
    isStandalone: window.matchMedia("(display-mode: standalone)").matches || window.navigator.standalone === true,
  };

  // --- 100dvh fallback for browsers without dvh support ---
  function setVhFallback() {
    document.documentElement.style.setProperty("--app-vh", `${window.innerHeight}px`);
  }
  setVhFallback();
  window.addEventListener("resize", setVhFallback);

  // --- Service worker registration ---
  if ("serviceWorker" in navigator) {
    window.addEventListener("load", () => {
      navigator.serviceWorker.register(`${MOUNT_BASE}/service-worker.js`).catch(() => {
        // Offline shell is a nice-to-have for this reference build, not load-bearing — fail silently.
      });
    });
  }

  // --- Keyboard-aware viewport: toggle .is-keyboard-open when the visual
  // viewport shrinks (on-screen keyboard opening), so bottom bars/sheets
  // get out of the way instead of being covered. ---
  function bindKeyboardAwareness() {
    const root = document.querySelector(".app-root");
    if (!root || !window.visualViewport) return;
    const vv = window.visualViewport;
    let baseline = vv.height;
    vv.addEventListener("resize", () => {
      const shrunk = vv.height < baseline * 0.75;
      root.classList.toggle("is-keyboard-open", shrunk);
      if (!shrunk) baseline = Math.max(baseline, vv.height);
    });
  }

  // --- Bottom sheet primitive ---
  function openSheet(id) {
    document.getElementById(id)?.classList.add("is-open");
    document.querySelector(`[data-sheet-scrim-for="${id}"]`)?.classList.add("is-open");
    document.body.style.overflow = "hidden";
  }
  function closeSheet(id) {
    document.getElementById(id)?.classList.remove("is-open");
    document.querySelector(`[data-sheet-scrim-for="${id}"]`)?.classList.remove("is-open");
    document.body.style.overflow = "";
  }
  MusicLinkd.openSheet = openSheet;
  MusicLinkd.closeSheet = closeSheet;

  document.addEventListener("click", (e) => {
    const openTrigger = e.target.closest("[data-open-sheet]");
    if (openTrigger) openSheet(openTrigger.dataset.openSheet);
    const closeTrigger = e.target.closest("[data-close-sheet]");
    if (closeTrigger) closeSheet(closeTrigger.dataset.closeSheet);
    const scrim = e.target.closest("[data-sheet-scrim-for]");
    if (scrim && e.target === scrim) closeSheet(scrim.dataset.sheetScrimFor);
  });

  // --- screen.json loader: fetches this screen's screen.json (flow/screen/
  // state metadata + data contract), exposes it, and renders the dev badge.
  // `?state=` in the URL previews a specific state; defaults to states[0].
  // Path comes from a per-page <meta name="screen-json-path"> (baked in by
  // the generator, absolute — see MOUNT_BASE above for why) rather than a
  // relative fetch, for the same trailing-slash reason. Falls back to a
  // relative "./screen.json" for pages without that meta tag (e.g. the raw
  // template file, opened directly rather than as a generated screen). ---
  async function loadScreenContract() {
    const badge = document.getElementById("dev-meta-badge") ?? createBadge();
    const path = document.querySelector('meta[name="screen-json-path"]')?.content ?? "./screen.json";
    try {
      const res = await fetch(path, { cache: "no-store" });
      if (!res.ok) throw new Error(`screen.json ${res.status}`);
      const data = await res.json();
      MusicLinkd.screen = data;

      const params = new URLSearchParams(window.location.search);
      const state = params.get("state") ?? data.states?.[0] ?? "default";
      MusicLinkd.currentState = state;
      document.documentElement.dataset.flow = data.flowKey ?? "";
      document.documentElement.dataset.screen = data.screenKey ?? "";
      document.documentElement.dataset.state = state;

      badge.textContent = `${data.flowKey ?? "?"} / ${data.screenKey ?? "?"} · ${state}`;
      document.dispatchEvent(new CustomEvent("musiclinkd:screen-ready", { detail: data }));
    } catch (err) {
      badge.textContent = "screen.json not found";
      console.warn("[MusicLinkd] could not load screen.json for this screen:", err);
    }
  }

  function createBadge() {
    const el = document.createElement("div");
    el.id = "dev-meta-badge";
    el.className = "dev-meta-badge";
    document.body.appendChild(el);
    return el;
  }

  document.addEventListener("DOMContentLoaded", () => {
    bindKeyboardAwareness();
    loadScreenContract();
  });

  window.MusicLinkd = MusicLinkd;
})();
