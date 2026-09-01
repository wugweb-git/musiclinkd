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
    const params = new URLSearchParams(window.location.search);
    try {
      const res = await fetch(path, { cache: "no-store" });
      if (!res.ok) throw new Error(`screen.json ${res.status}`);
      const data = await res.json();
      MusicLinkd.screen = data;

      const state = params.get("state") ?? data.states?.[0] ?? "default";
      MusicLinkd.currentState = state;
      document.documentElement.dataset.flow = data.flowKey ?? "";
      document.documentElement.dataset.screen = data.screenKey ?? "";

      applyState(state);
      document.dispatchEvent(new CustomEvent("musiclinkd:screen-ready", { detail: data }));
    } catch (err) {
      // Fail SAFE, not silent. This used to console.warn and return, which left
      // whatever the markup shipped with on screen. Since every [data-state]
      // element for a non-default state now ships with `hidden` stamped in, a
      // give-up leaves the default state rendered rather than all of them at
      // once — but only if nothing has un-hidden them yet. Applying the
      // fallback explicitly makes that guaranteed rather than incidental, and
      // it is what a 404 on screen.json actually deserves: the screen still
      // works, one state, no error surface.
      console.warn("[MusicLinkd] screen.json unavailable, rendering default state:", err);
      applyState(params.get("state") ?? fallbackState());
    }
  }

  // The state to render when the contract could not be fetched. The codemod
  // stamps data-state-default onto the elements belonging to states[0], so
  // there is a declared answer in the markup itself rather than a guess.
  function fallbackState() {
    for (const sel of ["[data-state-default]", "[data-state]"]) {
      for (const el of document.querySelectorAll(sel)) {
        if (el === document.documentElement) continue;
        const list = (el.getAttribute("data-state") || "").split(/\s+/).filter(Boolean);
        if (list.length) return list[0];
      }
    }
    return "default";
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
      // Skip <html>: the current state is published there as data-state for
      // the workbench and CSS to read, which means this very selector matches
      // the root element. Setting hidden on it blanks the entire document.
      if (el === document.documentElement) continue;
      const states = el.getAttribute("data-state").split(/\s+/).filter(Boolean);
      el.hidden = !states.includes(state);
    }
    // A control carrying data-chip is a STATE SELECTOR — a category chip, a
    // tab. Its pressed state is derived here rather than by a per-screen
    // handler: marketplace_browse shipped one that queried ".chip" while its
    // markup wrote ".ml-chip", so it silently matched nothing and every
    // category looked the same.
    for (const el of document.querySelectorAll("[data-chip]")) {
      const owns = el.getAttribute("data-chip").split(/\s+/).filter(Boolean);
      const on = String(owns.includes(state));
      // Three cases, not two. A tab announces selection as aria-selected; a
      // chip or toggle button as aria-pressed; a bare link as aria-current.
      //
      // ROLE IS THE ONLY SIGNAL FOR A TAB. This previously also treated the
      // .ml-tabs__tab CLASS as a tab, which wrote aria-selected onto 78 plain
      // anchors across 10 screens. aria-selected is invalid outside
      // role="tab", so assistive tech announced nothing about which tab was
      // active — the attribute was there and inert. The codemod adds the real
      // role to the markup; this reads it rather than guessing from a class.
      //
      // And aria-pressed is not allowed on a bare <a> at all — axe flagged 12.
      // A link that selects the current view is aria-current, not a toggle.
      if (el.getAttribute("role") === "tab") {
        el.setAttribute("aria-selected", on);
        el.removeAttribute("aria-pressed"); el.removeAttribute("aria-current");
      } else if (el.tagName === "A" && !el.hasAttribute("role")) {
        if (on === "true") el.setAttribute("aria-current", "true");
        else el.removeAttribute("aria-current");
        el.removeAttribute("aria-pressed"); el.removeAttribute("aria-selected");
      } else {
        el.setAttribute("aria-pressed", on);
        el.removeAttribute("aria-selected"); el.removeAttribute("aria-current");
      }
    }
    document.documentElement.dataset.state = state;
    MusicLinkd.currentState = state;
  }

  // --- Toggle controls. 91 .ml-switch instances across 18 screens shipped with
  // no handler anywhere, so not one of them toggled — the control looked
  // interactive and was inert. One delegated listener covers all of them and
  // every future one, which is why it belongs here rather than on a screen.
  document.addEventListener("click", (e) => {
    const sw = e.target.closest('.ml-switch, [role="switch"]');
    if (!sw || sw.getAttribute("aria-disabled") === "true" || sw.disabled) return;
    const next = sw.getAttribute("aria-checked") !== "true";
    sw.setAttribute("aria-checked", String(next));
    sw.dispatchEvent(new CustomEvent("musiclinkd:toggle", {
      bubbles: true,
      detail: { name: sw.dataset.name ?? sw.id ?? null, checked: next },
    }));
  });

  // Space and Enter must work: a <div role="switch"> gets no default keyboard
  // behaviour, so a keyboard user could focus it and never operate it.
  document.addEventListener("keydown", (e) => {
    if (e.key !== " " && e.key !== "Enter") return;
    const sw = e.target.closest('.ml-switch, [role="switch"]');
    if (!sw || sw.tagName === "BUTTON" || sw.tagName === "INPUT") return;
    e.preventDefault();
    sw.click();
  });

  // Checkboxes and radios drawn as .ml-check-ctl have the same gap.
  document.addEventListener("click", (e) => {
    const box = e.target.closest('.ml-check-ctl, [role="checkbox"], [role="radio"]');
    if (!box || box.getAttribute("aria-disabled") === "true") return;
    const isRadio = box.getAttribute("role") === "radio" ||
                    box.classList.contains("ml-check-ctl--radio");
    if (isRadio) {
      const group = box.closest("[role='radiogroup'], .ml-choice-group, .ml-stack, form") ?? document;
      for (const peer of group.querySelectorAll('[role="radio"], .ml-check-ctl--radio')) {
        peer.setAttribute("aria-checked", "false");
      }
      box.setAttribute("aria-checked", "true");
    } else {
      box.setAttribute("aria-checked", String(box.getAttribute("aria-checked") !== "true"));
    }
    box.dispatchEvent(new CustomEvent("musiclinkd:toggle", {
      bubbles: true,
      detail: { name: box.dataset.name ?? null, checked: box.getAttribute("aria-checked") === "true" },
    }));
  });

  MusicLinkd.applyState = applyState;

  document.addEventListener("DOMContentLoaded", () => {
    bindKeyboardAwareness();
    loadScreenContract();
  });

  window.MusicLinkd = MusicLinkd;
})();
