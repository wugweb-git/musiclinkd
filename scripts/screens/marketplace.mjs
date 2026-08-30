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
import { writeScreen, icon, assertIcons, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/marketplace/";
assertIcons(["search", "heart", "filter"]);

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
      <a class="ml-icon-btn ml-icon-btn--bare" href="${P}marketplace_search_filters/index.html" aria-label="Search and filters">${icon("search", { size: 20 })}</a>
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

finish("marketplace: ");
