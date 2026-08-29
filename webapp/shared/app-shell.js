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
  // the generator, absolute — see MOUNT_BASE above for why).
  //
  // Bug fixed 2026-08-16, then refined on re-audit the same day: this used to
  // fall back to a relative "./screen.json" fetch on ANY page with no
  // screen-json-path meta tag. That fallback was meant to catch one real
  // authoring mistake — a screen copied from template/index.html that forgot
  // to add its own meta tag — by showing "screen.json not found" as a hint.
  // But app-shell.js is also included on /preview/index.html (the Flow/
  // Screen Index shell page, for its own service-worker registration and
  // keyboard-awareness features), which was never meant to have a
  // screen.json at all — the same fallback fired there too and stamped a
  // permanent, misleading "screen.json not found" badge onto a page that
  // isn't a screen and never will be.
  //
  // First pass just skipped the loader whenever the meta tag was missing —
  // that silently broke the original "forgot the meta tag" signal for real
  // screens. Fixed properly here by keying off the URL instead: only pages
  // under /flows/ are actual generated screens. A /flows/ page with no meta
  // tag is a real authoring mistake and still gets the fallback fetch +
  // error badge; anything outside /flows/ (the shell page, or this file
  // opened from somewhere else entirely) is skipped outright — no fetch and
  // no console warning.
  //
  // The floating "flow / screen · state" chip this used to paint over the
  // top-right of every screen was removed 2026-08-29: it sat on top of real
  // UI (the app bar actions on discover_feed) and made every screenshot
  // unusable for design review. The same information is still on <html> as
  // data-flow / data-screen / data-state, which is where the workbench and
  // any automated check read it from anyway. The raw template/index.html itself sits
  // outside /flows/ too, so opening it directly is now silent rather than
  // showing a "not found" badge — that page's own header comment already
  // says plainly it's a template, not a screen, so the badge wasn't adding
  // information there either. ---
  async function loadScreenContract() {
    const metaPath = document.querySelector('meta[name="screen-json-path"]')?.content;
    const isScreenPage = metaPath || window.location.pathname.includes("/flows/");
    if (!isScreenPage) return; // shell page / template file opened directly — nothing to load
    const path = metaPath ?? "./screen.json";
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

      applyState(state);
      document.dispatchEvent(new CustomEvent("musiclinkd:screen-ready", { detail: data }));
    } catch (err) {
      console.warn("[MusicLinkd] could not load screen.json for this screen:", err);
    }
  }

  // --- State switching. Every screen.json declares the states that screen can
  // be in (e.g. otp_verification: entering / verifying / error / success) and
  // the workbench deep-links them with ?state=. Until 2026-08-29 nothing
  // consumed that: the attribute landed on <html> and no markup responded, so
  // ?state=error rendered exactly the same screen as ?state=success and the
  // states contract was decorative.
  //
  // The rule is deliberately simple so screens stay readable: an element
  // carrying data-state is shown ONLY in the states it lists (space-separated),
  // and hidden otherwise. Elements without data-state are always shown, so a
  // screen only has to mark up what actually varies.
  function applyState(state) {
    for (const el of document.querySelectorAll("[data-state]")) {
      const states = el.getAttribute("data-state").split(/\s+/).filter(Boolean);
      el.hidden = !states.includes(state);
    }
  }
  MusicLinkd.applyState = applyState;

  document.addEventListener("DOMContentLoaded", () => {
    bindKeyboardAwareness();
    loadScreenContract();
  });

  window.MusicLinkd = MusicLinkd;
})();
