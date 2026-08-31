#!/usr/bin/env node
/**
 * Flow 7 — Market. Rebuilt to actually implement its declared states.
 *
 * marketplace_browse declared 8 states and implemented ZERO — ?state=rental
 * rendered identically to ?state=sale. Two dangling selectors were the cause:
 * its JS queried ".chip" and ".cat-tile" while the markup wrote .ml-chip and
 * .ml-cat-tile, so every category handler matched nothing and the whole
 * category mechanism was dead on arrival. It also carried a local <style>
 * block whose .item-card duplicated .ml-market-card almost line for line.
 *
 * This is the recycling case in its clearest form: six category "screens"
 * are six states of ONE screen, driven declaratively by data-state through
 * app-shell.js, with no per-screen JS at all. Selection state rides on
 * aria-pressed, which .ml-chip already styles — no inline background/colour
 * overrides, which is what the old handler was doing.
 */
import { writeScreen, icon, backArrow, assertIcons, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/marketplace/";
assertIcons(["search", "heart", "filter", "add", "arrow-forward"]);

/** Every listing carries its category so one grid serves all six states, and
    the keywords the demo search matches on — search_filtered used to show the
    full catalogue under the heading "Showing results for microphone", which
    made the state indistinguishable from default. */
const LISTINGS = [
  { cat: "sale",     price: "₹28,500",   title: "Shure SM7B Microphone",      meta: "Gear Sale · Mumbai",     img: media("gear", 0), q: true, status: ["warning", "Like new"] },
  { cat: "rental",   price: "₹800/day",  title: "Focusrite Scarlett 2i2",     meta: "Gear Rental · Mumbai",   img: media("gear", 3),  status: ["success", "Available"] },
  { cat: "beats",    price: "₹3,500",    title: "Lo-fi Beat Pack — 10 Beats", meta: "Beats Licensing",        img: media("stage", 4) },
  { cat: "mixing",   price: "₹6,000",    title: "Full Track Mix & Master",    meta: "Mixing & Mastering",     img: media("editorial", 2) },
  { cat: "sale",     price: "₹15,000",   title: "KRK Rokit 5 Monitors",       meta: "Gear Sale · Pune",       img: media("gear", 6) },
  { cat: "rental",   price: "₹450/day",  title: "Rode NT1 Condenser Mic",     meta: "Gear Rental · Bengaluru", img: media("gear", 9), q: true, status: ["success", "Available"] },
  { cat: "services", price: "₹2,500",    title: "Cover Art & Visualizer",     meta: "Creative Services",      img: media("editorial", 7) },
  { cat: "rental",   price: "₹1,200/day", title: "Yamaha Stage Piano",        meta: "Gear Rental · Delhi",    img: media("gear", 12) },
  { cat: "mixing",   price: "₹2,000",    title: "Vocal Tuning & Comping",     meta: "Mixing & Mastering",     img: media("stage", 11) },
];

const CATS = [
  ["default",  "All"],
  ["sale",     "Gear Sale"],
  ["rental",   "Gear Rental"],
  ["beats",    "Beats Licensing"],
  ["mixing",   "Mixing & Mastering"],
  ["services", "Creative Services"],
];

/* data-chip lists the states in which this chip reads as selected — "All"
   stays on while a search is filtered. app-shell.js turns that into
   aria-pressed, which .ml-chip already styles. An <a> rather than a
   <button> so it works with no JavaScript and is deep-linkable. */
const chip = ([key, label]) => {
  const owns = key === "default" ? "default search_filtered" : key;
  return `<a class="ml-chip" data-chip="${owns}" href="?state=${key}">${label}</a>`;
};

/* Each card is visible in "default", its own category, and — only if it
   matches the demo query — "search_filtered". One grid covers all eight
   states with no duplicated markup. */
const card = (l) => `<a class="ml-market-card" data-state="default ${l.q ? "search_filtered " : ""}${l.cat}"
          href="${P}marketplace_item_detail/index.html">
          <div class="ml-market-card__media">
            <img src="${l.img}" alt="" loading="lazy" />
            ${l.status ? `<span class="ml-status ml-status--${l.status[0]}" style="position:absolute;top:var(--space-2);left:var(--space-2)">${l.status[1]}</span>` : ""}
            <button class="ml-market-card__save" aria-pressed="false" aria-label="Save ${l.title}">${icon("heart", { size: 16 })}</button>
          </div>
          <div class="ml-market-card__body">
            <div class="ml-market-card__price">${l.price}</div>
            <div class="ml-market-card__title">${l.title}</div>
            <div class="ml-market-card__meta">${l.meta}</div>
          </div>
        </a>`;

const catTile = (key, label) => `<a class="ml-cat-tile" href="?state=${key}">
          <span class="ml-cat-tile__icon">${icon("search", { size: 20 })}</span>
          <span class="ml-cat-tile__label">${label}</span>
        </a>`;

writeScreen({
  flow: "marketplace",
  screen: "marketplace_browse",
  title: "Market — Browse",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Market</h1>
      <span class="ml-appbar__actions">
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}marketplace_saved_bookmarks/index.html" aria-label="Saved listings">${icon("heart", { size: 20 })}</a>
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}marketplace_search_filters/index.html" aria-label="Search and filters">${icon("search", { size: 20 })}</a>
      </span>
    </header>

    <!-- Category chips. Selection is aria-pressed, which .ml-chip already
         styles; the old handler set inline background/border/color instead
         and queried a class that did not exist. -->
    <nav class="ml-chip-wrap" style="flex-wrap:nowrap;overflow-x:auto;padding:0 var(--space-4) var(--space-2)" aria-label="Categories">
      ${CATS.map(chip).join("\n      ")}
    </nav>

    <main class="ml-app__body app-content--scroll" style="padding:var(--space-1) var(--space-4) var(--space-4)">

      <!-- Category tiles jump straight to a state via the URL, so they work
           with no JavaScript at all — the old ones were bound to a dead
           selector and did nothing. -->
      <a class="ml-action-row" data-state="default" href="${P}marketplace_nearby/index.html" style="margin-bottom:var(--space-2)">
        <span class="ml-choice__body">
          <span class="ml-choice__title">Near you</span>
          <span class="ml-choice__meta">Sorted by distance from your city</span>
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>

      <div class="ml-rail" data-state="default" style="margin-bottom:var(--space-3)">
        ${CATS.slice(1).map(([k, l]) => catTile(k, l)).join("\n        ")}
      </div>

      <div class="ml-market-grid" data-state="default search_filtered sale rental beats mixing services">
        ${LISTINGS.map(card).join("\n        ")}
      </div>

      <div data-state="search_filtered" class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-3)">
        Showing results for "microphone" · <a class="ml-link" href="?state=default">Clear</a>
      </div>

      <div class="ml-empty" data-state="empty" hidden>
        <h3 class="ml-empty__title">Nothing listed here yet</h3>
        <p class="ml-empty__body">Be the first to list gear, beats, or services in this category.</p>
        <a class="ml-btn ml-btn--primary" href="${P}marketplace_create_listing/index.html">Create a listing</a>
      </div>
    </main>
  </div>`,
});

/* ══ the remaining seven screens ═══════════════════════════════════════════
   Each declared states and implemented none, and each carried a local <style>
   block that redrew something the system already ships:

     .field / .photo-add        → .ml-field / .ml-upload-grid
     .type-toggle / .my-tabs
       / .view-toggle           → .ml-tabs__tab + data-chip
     .hero-img                  → .ml-market-card__media geometry
     .seller-row / .listing-mini
       / .item-row / .list-row  → .ml-person-row / .ml-result
     .status-banner             → .ml-banner--*
     .timeline / .t-row
       / .t-dot / .t-line       → .ml-timeline + .is-done/.is-current
     .item-card / .item-grid    → .ml-market-card / .ml-market-grid
     .chip-wrap                 → .ml-chip-wrap

   So, as with Booking, this adds no CSS. ══════════════════════════════════ */

const tab = (state, label) =>
  `<a class="ml-tabs__tab" role="tab" data-chip="${state}" href="?state=${state}">${label}</a>`;

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-2" style="margin:0">${title}</h1>
    </header>`;

/* ── marketplace_item_detail — default ─────────────────────────────────── */
writeScreen({
  flow: "marketplace", screen: "marketplace_item_detail", title: "Market — Item detail",
  body: `
  <div class="ml-app">
    ${bar("Listing", `${P}marketplace_browse/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:0 0 var(--space-4)">
      <img src="${media("gear", 0)}" alt="Shure SM7B microphone" loading="lazy"
           style="width:100%;aspect-ratio:var(--media-aspect-card);object-fit:cover" />
      <div style="padding:var(--space-3) var(--space-4)">
        <div class="ml-market-card__price" style="font-size:var(--type-4)">₹28,500</div>
        <h2 class="ml-type-3" style="margin:var(--space-1) 0">Shure SM7B Microphone</h2>
        <span class="ml-status ml-status--warning">Like new</span>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-2)">
          Cardioid dynamic, used on two album sessions. Original box, shock mount and cable included.
          Collection in Andheri or courier at cost.
        </p>
        <a class="ml-person-row" style="margin-top:var(--space-3)" href="${P}marketplace_seller_profile/index.html">
          <img src="${media("faces", 3)}" alt="" loading="lazy" />
          <span class="meta"><b>Aarav Menon</b><p>Seller · 12 listings · joined 2024</p></span>
          ${icon("arrow-forward", { size: 18 })}
        </a>
        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>Asking price</span><span>₹28,500</span></div>
          <div class="ml-summary__row"><span>Delivery</span><span>At cost</span></div>
          <div class="ml-summary__row ml-summary__row--total"><span>Total</span><span>₹28,500</span></div>
        </div>
      </div>
    </main>
    <footer class="ml-app__footer" style="display:flex;gap:var(--space-2)">
      <a class="ml-btn ml-btn--secondary" style="flex:1" href="${P}marketplace_make_offer/index.html">Make offer</a>
      <a class="ml-btn ml-btn--primary" style="flex:1" href="${P}marketplace_order_status/index.html">Buy now</a>
    </footer>
  </div>`,
});

/* ── marketplace_create_listing — default / publishing / error ─────────── */
const CATEGORY_OPTS = ["Gear Sale", "Gear Rental", "Beats Licensing", "Mixing & Mastering", "Creative Services"];
writeScreen({
  flow: "marketplace", screen: "marketplace_create_listing", title: "Market — Create listing",
  body: `
  <div class="ml-app">
    ${bar("New listing", `${P}marketplace_my_listings/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        <!-- Images upload; audio and video are links only. Per the media
             decision, so no audio/video dropzone is drawn here. -->
        <div class="ml-upload-grid">
          ${[0, 4, 8].map((i) => `<div class="ml-upload-grid__item">
            <img src="${media("gear", i)}" alt="" loading="lazy" />
            <button class="ml-upload-grid__remove" type="button" aria-label="Remove photo"></button>
          </div>`).join("\n          ")}
          <button class="ml-upload-grid__add" type="button" aria-label="Add photo"></button>
        </div>
        <div class="ml-field" style="margin-top:var(--space-3)">
          <label for="l-title">Title</label>
          <input id="l-title" type="text" value="Shure SM7B Microphone" />
        </div>
        <div class="ml-field">
          <label for="l-cat">Category</label>
          <select id="l-cat">${CATEGORY_OPTS.map((c) => `<option>${c}</option>`).join("")}</select>
        </div>
        <div class="ml-field">
          <label for="l-price">Price (₹)</label>
          <input id="l-price" type="text" inputmode="numeric" value="28500" />
        </div>
        <div class="ml-field">
          <label for="l-desc">Description</label>
          <textarea id="l-desc" rows="4">Cardioid dynamic, used on two album sessions.</textarea>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=publishing">Publish listing</a>
      </div>

      <div class="ml-empty" data-state="publishing">
        <span class="ml-spinner" role="status" aria-label="Publishing"></span>
        <p class="ml-empty__body" style="margin-top:var(--space-3)">Publishing your listing…</p>
      </div>

      <div data-state="error">
        <div class="ml-banner ml-banner--error">
          <span class="ml-banner__body">Couldn't publish — the price field must be a whole number.</span>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=default">Back to the form</a>
      </div>
    </main>
  </div>`,
});

/* ── marketplace_make_offer — default / countered / accepted / declined / expired */
const OFFER = [
  ["countered", "info",    "Seller countered at ₹26,000.", "Respond or accept"],
  ["accepted",  "success", "Offer accepted at ₹24,000.",   "Continue to payment"],
  ["declined",  "error",   "Offer declined.",              "Make another offer"],
  ["expired",   "warning", "Offer expired after 48 hours.", "Make another offer"],
];
writeScreen({
  flow: "marketplace", screen: "marketplace_make_offer", title: "Market — Make an offer",
  body: `
  <div class="ml-app">
    ${bar("Make an offer", `${P}marketplace_item_detail/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <a class="ml-result" href="${P}marketplace_item_detail/index.html">
        <img class="ml-result__thumb" src="${media("gear", 0)}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">Shure SM7B Microphone</span>
          <span class="ml-result__meta">Gear Sale · Mumbai</span>
        </span>
        <span class="ml-result__trail">₹28,500</span>
      </a>

      ${OFFER.map(([st, kind, msg, cta]) => `<div data-state="${st}" style="margin-top:var(--space-3)">
        <div class="ml-banner ml-banner--${kind}"><span class="ml-banner__body">${msg}</span></div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=default">${cta}</a>
      </div>`).join("\n      ")}

      <div data-state="default">
        <div class="ml-field" style="margin-top:var(--space-3)">
          <label for="o-amt">Your offer (₹)</label>
          <input id="o-amt" type="text" inputmode="numeric" value="24000" />
        </div>
        <div class="ml-field">
          <label for="o-note">Note to seller (optional)</label>
          <textarea id="o-note" rows="3" placeholder="Collection this weekend if that suits."></textarea>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted)">Offers expire after 48 hours.</p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-2)" href="?state=countered">Send offer</a>
      </div>
    </main>
  </div>`,
});

/* ── marketplace_my_listings — listings / purchases / rentals / empty ───── */
const MINE = [
  { st: "listings",  img: media("gear", 0),  name: "Shure SM7B Microphone",   meta: "Live · 14 views · 2 offers", trail: "₹28,500" },
  { st: "listings",  img: media("gear", 9),  name: "KRK Rokit 5 Monitors",    meta: "Live · 31 views",            trail: "₹15,000" },
  { st: "purchases", img: media("stage", 4), name: "Lo-fi Beat Pack — 10",    meta: "Bought 4 Aug 2026",          trail: "₹3,500" },
  { st: "rentals",   img: media("gear", 3),  name: "Focusrite Scarlett 2i2",  meta: "Returns 18 Aug 2026",        trail: "₹800/day" },
];
writeScreen({
  flow: "marketplace", screen: "marketplace_my_listings", title: "Market — My listings",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">My market</h1>
      <a class="ml-icon-btn ml-icon-btn--bare" href="${P}marketplace_create_listing/index.html" aria-label="Create listing">${icon("add", { size: 20 })}</a>
    </header>
    <nav class="ml-tabs ml-tabs--fill" role="tablist" aria-label="My market" style="margin:0 var(--space-4)">
      ${tab("listings", "Listings")}${tab("purchases", "Purchases")}${tab("rentals", "Rentals")}
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${MINE.map((m) => `<a class="ml-result" data-state="${m.st}" href="${P}marketplace_order_status/index.html">
        <img class="ml-result__thumb" src="${m.img}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">${m.name}</span>
          <span class="ml-result__meta">${m.meta}</span>
        </span>
        <span class="ml-result__trail">${m.trail}</span>
      </a>`).join("\n      ")}

      <div class="ml-empty" data-state="empty">
        <h3 class="ml-empty__title">Nothing here yet</h3>
        <p class="ml-empty__body">List gear you no longer use, or a service you offer.</p>
        <a class="ml-btn ml-btn--primary" href="${P}marketplace_create_listing/index.html">Create a listing</a>
      </div>
    </main>
  </div>`,
});

/* ── marketplace_order_status — the 6 lifecycle states ──────────────────── */
/* One timeline, six positions along it — the screen used to carry bespoke JS
   toggling six near-identical panels. */
const STEPS = ["Ordered", "Paid", "In progress", "Returned", "Completed"];
const ORDER = [
  ["pending",   0, "warning", "Awaiting payment"],
  ["paid",      1, "success", "Payment received"],
  ["active",    2, "info",    "With the buyer"],
  ["returned",  3, "info",    "Returned, awaiting check"],
  ["completed", 4, "success", "Completed"],
  ["cancelled", 0, "error",   "Cancelled — refunded in full"],
];
writeScreen({
  flow: "marketplace", screen: "marketplace_order_status", title: "Market — Order status",
  body: `
  <div class="ml-app">
    ${bar("Order", `${P}marketplace_my_listings/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <a class="ml-result" href="${P}marketplace_item_detail/index.html">
        <img class="ml-result__thumb" src="${media("gear", 3)}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">Focusrite Scarlett 2i2</span>
          <span class="ml-result__meta">Order #ML-4471 · Gear Rental</span>
        </span>
        <span class="ml-result__trail">₹800/day</span>
      </a>

      ${ORDER.map(([st, at, kind, msg]) => `<div data-state="${st}" style="margin-top:var(--space-3)">
        <div class="ml-banner ml-banner--${kind}"><span class="ml-banner__body">${msg}</span></div>
        <ol class="ml-timeline" style="margin-top:var(--space-3)">
          ${STEPS.map((s, i) => `<li class="ml-timeline__item${st === "cancelled" ? "" : i < at ? " is-done" : i === at ? " is-current" : ""}">
            <span class="ml-timeline__title">${s}</span>
          </li>`).join("\n          ")}
        </ol>
      </div>`).join("\n      ")}
    </main>
  </div>`,
});

/* ── marketplace_saved_bookmarks — grid / list / empty ──────────────────── */
const SAVED = LISTINGS.slice(0, 4);
writeScreen({
  flow: "marketplace", screen: "marketplace_saved_bookmarks", title: "Market — Saved",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Saved</h1>
    </header>
    <nav class="ml-tabs ml-tabs--fill" role="tablist" aria-label="View" style="margin:0 var(--space-4)">
      ${tab("grid", "Grid")}${tab("list", "List")}
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-market-grid" data-state="grid">
        ${SAVED.map((l) => `<a class="ml-market-card" href="${P}marketplace_item_detail/index.html">
          <div class="ml-market-card__media"><img src="${l.img}" alt="" loading="lazy" /></div>
          <div class="ml-market-card__body">
            <div class="ml-market-card__price">${l.price}</div>
            <div class="ml-market-card__title">${l.title}</div>
          </div>
        </a>`).join("\n        ")}
      </div>

      ${SAVED.map((l) => `<a class="ml-result" data-state="list" href="${P}marketplace_item_detail/index.html">
        <img class="ml-result__thumb" src="${l.img}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">${l.title}</span>
          <span class="ml-result__meta">${l.meta}</span>
        </span>
        <span class="ml-result__trail">${l.price}</span>
      </a>`).join("\n      ")}

      <div class="ml-empty" data-state="empty">
        <h3 class="ml-empty__title">Nothing saved yet</h3>
        <p class="ml-empty__body">Tap the heart on any listing to keep it here.</p>
        <a class="ml-btn ml-btn--primary" href="${P}marketplace_browse/index.html">Browse the market</a>
      </div>
    </main>
  </div>`,
});

/* ── marketplace_search_filters — default / empty ───────────────────────── */
const FILTERS = [["Under ₹5,000", 0], ["₹5,000–20,000", 1], ["Over ₹20,000", 0],
                 ["Mumbai", 1], ["Delhi", 0], ["Bengaluru", 0], ["Like new", 1], ["Used", 0]];
writeScreen({
  flow: "marketplace", screen: "marketplace_search_filters", title: "Market — Search & filters",
  body: `
  <div class="ml-app">
    ${bar("Search", `${P}marketplace_browse/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-field">
        <label for="q">Search the market</label>
        <input id="q" type="search" value="microphone" />
      </div>
      <div class="ml-chip-wrap" style="margin-top:var(--space-2)">
        ${FILTERS.map(([f, on]) => `<button type="button" class="ml-chip" aria-pressed="${on ? "true" : "false"}">${f}</button>`).join("\n        ")}
      </div>

      <div data-state="default" style="margin-top:var(--space-3)">
        ${LISTINGS.filter((l) => l.q).map((l) => `<a class="ml-result" href="${P}marketplace_item_detail/index.html">
          <img class="ml-result__thumb" src="${l.img}" alt="" loading="lazy" />
          <span class="ml-result__body">
            <span class="ml-result__name">${l.title}</span>
            <span class="ml-result__meta">${l.meta}</span>
          </span>
          <span class="ml-result__trail">${l.price}</span>
        </a>`).join("\n        ")}
      </div>

      <div class="ml-empty" data-state="empty">
        <h3 class="ml-empty__title">No matches</h3>
        <p class="ml-empty__body">Nothing matches those filters. Try removing one.</p>
        <a class="ml-btn ml-btn--secondary" href="?state=default">Clear filters</a>
      </div>
    </main>
  </div>`,
});

/* ══ the approved plan's Market +4 ═════════════════════════════════════════
   nearby, availability, rental_summary and seller_profile. Each owns a route.
   Everything else in that column was already delivered as states on the eight
   screens above. ═══════════════════════════════════════════════════════════ */

writeScreen({
  flow: "marketplace", screen: "marketplace_nearby", title: "Market — Nearby",
  body: `
  <div class="ml-app">
    ${bar("Nearby", `${P}marketplace_browse/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        <p class="ml-type-1" style="color:var(--color-muted);margin:0 0 var(--space-2)">
          Sorted by distance from Andheri West, the city on your profile.
        </p>
        ${/* Only things with a physical pickup belong on a distance-sorted screen.
             Slicing LISTINGS unfiltered stamped a distance on the beat pack and the
             mixing service — "Lo-fi Beat Pack · Beats Licensing · 4.1 km away", directly
             under a line that says results are sorted by distance. Both are delivered
             as files; neither has anywhere to be. The category already encodes this:
             sale and rental carry a city in their meta, beats/mixing/services do not. */
          LISTINGS.filter((l) => l.cat === "sale" || l.cat === "rental")
            .slice(0, 5).map((l, i) => `<a class="ml-result" href="${P}marketplace_item_detail/index.html">
          <img class="ml-result__thumb" src="${l.img}" alt="" loading="lazy" />
          <span class="ml-result__body">
            <span class="ml-result__name">${l.title}</span>
            <span class="ml-result__meta">${l.meta}</span>
            <span class="ml-result__meta">${[1.2, 2.4, 4.1, 6.8, 11.2][i]} km away</span>
          </span>
          <span class="ml-result__trail">${l.price}</span>
        </a>`).join("\n        ")}
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">Nothing listed near you</h2>
        <p class="ml-empty__body">Nobody within 25 km has listed anything yet. Browsing everywhere usually works better in a smaller city.</p>
        <a class="ml-btn ml-btn--primary" href="${P}marketplace_browse/index.html">Browse everywhere</a>
      </div>

      <!-- Distance comes from the city on the profile, not live location. The
           copy says so, and does not pretend the app can grant itself the
           permission it does not have. -->
      <div class="ml-empty" data-state="permission_denied">
        <h2 class="ml-empty__title">We're using the city on your profile</h2>
        <p class="ml-empty__body">
          Location access is off, so distances are measured from Andheri West rather than from
          where you are. You can turn it on in your device settings — we can't change it from here.
        </p>
        <a class="ml-btn ml-btn--primary" href="?state=default">Use my profile city</a>
        <a class="ml-btn ml-btn--secondary" style="margin-top:var(--space-2)" href="/preview/flows/profile_management/edit_identity/index.html">Change my city</a>
      </div>
    </main>
  </div>`,
});

/* Rental availability reuses .ml-calendar — the same component booking uses,
   which is exactly why it lives in the system and not in either flow. */
const R_DAYS = Array.from({ length: 30 }, (_, i) => i + 1);
const R_TAKEN = new Set([5, 6, 7, 14, 15, 22, 23]);

writeScreen({
  flow: "marketplace", screen: "marketplace_availability", title: "Market — Availability",
  body: `
  <div class="ml-app">
    ${bar("Availability", `${P}marketplace_item_detail/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <a class="ml-result" href="${P}marketplace_item_detail/index.html">
        <img class="ml-result__thumb" src="${media("gear", 3)}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">Focusrite Scarlett 2i2</span>
          <span class="ml-result__meta">Gear Rental · Mumbai</span>
        </span>
        <span class="ml-result__trail">₹800/day</span>
      </a>

      <div class="ml-calendar" data-state="default" style="margin-top:var(--space-3)">
        <div class="ml-calendar__head"><span class="ml-calendar__month">September 2026</span></div>
        <div class="ml-calendar__dow">${["M", "T", "W", "T", "F", "S", "S"].map((d) => `<span>${d}</span>`).join("")}</div>
        <div class="ml-calendar__grid">
          ${Array.from({ length: 1 }, () => `<span></span>`).join("")}
          ${R_DAYS.map((d) => `<button type="button" class="ml-calendar__day${R_TAKEN.has(d) ? " is-blocked" : ""}"${R_TAKEN.has(d) ? ` disabled aria-label="${d} September, already rented"` : ""}>${d}</button>`).join("\n          ")}
        </div>
        <div class="ml-calendar__legend">
          <span><i class="ml-calendar__key ml-calendar__key--sel"></i>Free</span>
          <span><i class="ml-calendar__key ml-calendar__key--blocked"></i>Already out</span>
        </div>
      </div>

      <div class="ml-empty" data-state="unavailable">
        <h2 class="ml-empty__title">This item isn't being rented right now</h2>
        <p class="ml-empty__body">The owner has paused it. You can still message them to ask.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/communication/chat_conversation/index.html">Message the owner</a>
      </div>
    </main>
    <footer class="ml-app__footer" data-state="default">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}marketplace_rental_summary/index.html">Request these dates</a>
    </footer>
  </div>`,
});

writeScreen({
  flow: "marketplace", screen: "marketplace_rental_summary", title: "Market — Rental summary",
  body: `
  <div class="ml-app">
    ${bar("Your rental", `${P}marketplace_availability/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <a class="ml-result" href="${P}marketplace_item_detail/index.html">
        <img class="ml-result__thumb" src="${media("gear", 3)}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">Focusrite Scarlett 2i2</span>
          <span class="ml-result__meta">From Aarav Menon · Mumbai</span>
        </span>
      </a>

      <div class="ml-summary" style="margin-top:var(--space-3)" data-state="default">
        <div class="ml-summary__row"><span>Out</span><span>Mon 8 Sept</span></div>
        <div class="ml-summary__row"><span>Back</span><span>Thu 11 Sept</span></div>
        <div class="ml-summary__row"><span>3 days at ₹800</span><span>₹2,400</span></div>
        <div class="ml-summary__row"><span>Deposit, refundable</span><span>₹5,000</span></div>
        <div class="ml-summary__row ml-summary__row--total"><span>Now</span><span>₹7,400</span></div>
        <p class="ml-summary__note">
          The deposit is held, not paid to the owner, and comes back within three days of return
          if the item is as it went out.
        </p>
      </div>

      <div data-state="returned">
        <div class="ml-banner ml-banner--success"><span class="ml-banner__body">Returned on 11 September. Deposit released.</span></div>
        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>Rental</span><span>₹2,400</span></div>
          <div class="ml-summary__row"><span>Deposit returned</span><span>₹5,000</span></div>
          <div class="ml-summary__row"><span>Deductions</span><span>None</span></div>
        </div>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-3)" href="/preview/flows/reviews/marketplace_review_screen/index.html">Review the owner</a>
      </div>

      <div data-state="overdue">
        <div class="ml-banner ml-banner--warning"><span class="ml-banner__body">Due back yesterday.</span></div>
        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>Was due</span><span>Thu 11 Sept</span></div>
          <div class="ml-summary__row"><span>Late fee</span><span>₹800/day from today</span></div>
          <div class="ml-summary__row"><span>Taken from</span><span>The deposit</span></div>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          If you need it longer, message the owner — extending is usually cheaper than the late fee.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="/preview/flows/communication/chat_conversation/index.html">Message the owner</a>
      </div>
    </main>
    <footer class="ml-app__footer" data-state="default">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}marketplace_order_status/index.html">Confirm rental</a>
    </footer>
  </div>`,
});

/* The TRADING identity, deliberately distinct from FLOW 14's public profile,
   which is the person. What matters here is whether they ship. */
writeScreen({
  flow: "marketplace", screen: "marketplace_seller_profile", title: "Market — Seller",
  body: `
  <div class="ml-app">
    ${bar("Seller", `${P}marketplace_item_detail/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-person-row">
        <img src="${media("faces", 8)}" alt="" loading="lazy" />
        <span class="meta"><b data-state="default">Aarav Menon</b><b data-state="business">Blue Note Recording Co.</b>
        <p data-state="default">Selling since 2024 · Mumbai</p><p data-state="business">Business seller · Bandra</p></span>
        <span class="ml-status ml-status--accent" data-state="business">Verified business</span>
      </div>

      <div class="ml-stat-grid" style="margin-top:var(--space-3)">
        <div class="ml-kpi"><span class="ml-kpi__num">4.8</span>Rating</div>
        <div class="ml-kpi"><span class="ml-kpi__num">31</span>Sold</div>
        <div class="ml-kpi"><span class="ml-kpi__num">2h</span>Usually replies in</div>
        <div class="ml-kpi"><span class="ml-kpi__num">0</span>Disputes</div>
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h3>Listed now</h3></div>
      <div class="ml-market-grid">
        ${LISTINGS.slice(0, 4).map((l) => `<a class="ml-market-card" href="${P}marketplace_item_detail/index.html">
          <div class="ml-market-card__media"><img src="${l.img}" alt="" loading="lazy" /></div>
          <div class="ml-market-card__body">
            <div class="ml-market-card__price">${l.price}</div>
            <div class="ml-market-card__title">${l.title}</div>
          </div>
        </a>`).join("\n        ")}
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h3>What buyers said</h3></div>
      <article class="ml-review-card">
        <div class="ml-review-card__head">
          <img class="ml-avatar ml-avatar--sm" src="${media("faces", 2)}" alt="" loading="lazy" />
          <b>Ishita Rao</b>
          <span class="ml-review-card__stars">★★★★★</span>
        </div>
        <p class="ml-review-card__body">Exactly as described, packed properly, posted the same day.</p>
      </article>

      <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-3)">
        This is their trading record. Their
        <a class="ml-link" href="/preview/flows/public_profile/public_profile_view/index.html">musician profile</a>
        is separate.
      </p>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="/preview/flows/communication/chat_conversation/index.html">Message seller</a>
    </footer>
  </div>`,
});

finish("marketplace: ");
