#!/usr/bin/env node
/**
 * Flow 24 — Saved & Favorites, rebuilt against the supplied brief.
 *
 * The brief lists 50 numbered sections. Four of them are screens; the rest are
 * states, sheets, dialogs or handoffs, and reading them as screens is exactly
 * how a 200-screen app happens:
 *
 *   SCREEN 33 ITEM SOLD          → a state of a saved row
 *   SCREEN 34 OPPORTUNITY EXPIRED→ a state of a saved row
 *   SCREEN 47 DELETE COLLECTION  → a dialog
 *   SCREEN 20 MOVE TO COLLECTION → a bottom sheet
 *   SCREEN 26 SAVED & FAVORITES  → (in Settings) a handoff
 *   SCREEN 40 SAVED ITEM DETAIL  → a screen, and the brief says why: it must
 *                                  never become a duplicate detail page
 *
 * The distinction the brief insists on is carried through the markup, not just
 * the copy: SAVED is "I want to come back to this" and lives on every row;
 * FAVORITE is "this is important to me" and is a separate, filterable flag.
 * Favourites is therefore a STATE of the one list — a filter over it — and not
 * a second list with its own storage.
 *
 * No new CSS: .ml-result, .ml-market-card/.ml-market-grid, .ml-check-ctl,
 * .ml-status, .ml-empty, .ml-sheet, .ml-action-row and .ml-chip already exist.
 */
import { writeScreen, backArrow, icon, assertIcons, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/saved_favorites/";
assertIcons(["heart", "search", "add", "check", "cancel", "arrow-forward", "filter", "bell"]);

const bar = (title, backTo, actions = "") => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-3" style="margin:0;flex:1">${title}</h1>
      ${actions}
    </header>`;

const tab = (state, label) =>
  `<a class="ml-tabs__tab" role="tab" data-chip="${state}" href="?state=${state}">${label}</a>`;

const rowLink = (label, href, meta, trail = "") => `<a class="ml-action-row" href="${href}">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        ${trail || icon("arrow-forward", { size: 18 })}
      </a>`;

/* ── the saved set ──────────────────────────────────────────────────────────
   Each row declares: its entity tab, whether it is a favourite, and — if the
   underlying entity has gone away — which resolution state it belongs to. One
   row, many states, which is the whole argument. */
const SAVED = [
  { cat: "artists",     fav: true,  name: "Ishita Rao",              meta: "Session vocalist · Mumbai · 4.9 ★",
    img: media("faces", 2),  when: "Saved 3 days ago", href: "/preview/flows/public_profile/public_profile_view/index.html" },
  { cat: "artists",     fav: false, name: "Meera Nair",              meta: "Playback vocalist · Kochi",
    img: media("faces", 4),  when: "Saved 2 weeks ago", href: "/preview/flows/public_profile/public_profile_view_talent/index.html" },
  { cat: "artists",     fav: false, name: "Kabir Sheth",             meta: "Producer · Pune",
    img: media("faces", 8),  when: "Saved 1 month ago", href: "/preview/flows/public_profile/public_profile_view/index.html",
    gone: "item_private", goneLabel: "No longer available to you" },
  { cat: "bands",       fav: true,  name: "The Kolaba Sessions",     meta: "Indie rock · Mumbai",
    img: media("stage", 6),  when: "Saved 5 days ago", href: "/preview/flows/band/band_profile_layout/index.html" },
  { cat: "studios",     fav: true,  name: "Yash Raj Sound Studios",  meta: "Andheri West · from ₹1,800/hr",
    img: media("stage", 1),  when: "Saved 1 week ago", href: "/preview/flows/booking/studio_profile/index.html" },
  { cat: "studios",     fav: false, name: "Blue Note Recording Co.", meta: "Bandra · from ₹2,400/hr",
    img: media("gear", 7),   when: "Saved 3 weeks ago", href: "/preview/flows/booking/studio_profile/index.html",
    gone: "item_unavailable", goneLabel: "This business is no longer available" },
  { cat: "marketplace", fav: false, name: "Shure SM7B Microphone",   meta: "Gear Sale · ₹28,500",
    img: media("gear", 0),   when: "Saved 2 days ago", href: "/preview/flows/marketplace/marketplace_item_detail/index.html" },
  { cat: "marketplace", fav: false, name: "KRK Rokit 5 Monitors",    meta: "Gear Sale · ₹15,000",
    img: media("gear", 6),   when: "Saved 4 days ago", href: "/preview/flows/marketplace/marketplace_item_detail/index.html",
    gone: "item_sold", goneLabel: "Sold" },
  { cat: "marketplace", fav: false, name: "Session vocalist — Hindi pop EP", meta: "Opportunity · ₹12,000/day",
    img: media("stage", 4),  when: "Saved 6 days ago", href: "/preview/flows/opportunities/opportunity_detail/index.html",
    gone: "item_expired", goneLabel: "This opportunity has closed" },
];

/* A row belongs to: its own tab, "favorites" if flagged, the two view modes,
   bulk-select, and its resolution state if the entity has gone. */
const rowStates = (s) => [
  s.cat, s.fav ? "favorites" : null, "list_view", "bulk_select",
  s.gone || null,
].filter(Boolean).join(" ");

const savedRow = (s) => `<a class="ml-result" data-state="${rowStates(s)}" href="${s.gone ? `${P}saved_item_detail/index.html?state=unavailable` : s.href}">
        <img class="ml-result__thumb" src="${s.img}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">${s.name}</span>
          <span class="ml-result__meta">${s.gone ? s.goneLabel : s.meta}</span>
          <span class="ml-result__meta">${s.when}</span>
        </span>
        <!-- Favourite is a distinct flag from saved, and it is not signalled by
             colour alone: the label says which it is. -->
        <span class="ml-status ml-status--${s.gone ? "neutral" : s.fav ? "accent" : "neutral"}">${s.gone ? "Unavailable" : s.fav ? "Favourite" : "Saved"}</span>
      </a>`;

/* ── saved_favorites_hub ────────────────────────────────────────────────── */
writeScreen({
  flow: "saved_favorites", screen: "saved_favorites_hub", title: "Saved",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Saved</h1>
      <span class="ml-appbar__actions">
        <a class="ml-icon-btn ml-icon-btn--bare" data-chip="favorites" href="?state=favorites" aria-label="Favourites only">${icon("heart", { size: 20 })}</a>
        <a class="ml-icon-btn ml-icon-btn--bare" href="?state=bulk_select" aria-label="Select items">${icon("check", { size: 20 })}</a>
        <button class="ml-icon-btn ml-icon-btn--bare" type="button" data-open-sheet="sf-filter" aria-label="Sort and filter">${icon("filter", { size: 20 })}</button>
      </span>
    </header>

    <p class="ml-type-1" style="color:var(--color-muted);margin:0 var(--space-4) var(--space-2)">
      Things you want to come back to.
    </p>

    <nav class="ml-tabs" role="tablist" aria-label="Saved" style="margin:0 var(--space-4)">
      ${tab("artists", "People")}${tab("bands", "Bands")}${tab("studios", "Studios")}${tab("marketplace", "Market")}${tab("collections", "Collections")}
    </nav>

    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <!-- Bulk selection is a MODE, not a screen: the same rows gain a
           checkbox and the footer gains actions. -->
      <div class="ml-banner ml-banner--info" data-state="bulk_select">
        <span class="ml-banner__body">2 selected. Removing from Saved does not delete the item itself.</span>
      </div>

      <div class="ml-banner ml-banner--warning" data-state="item_sold item_expired item_private item_unavailable">
        <span class="ml-banner__body">Some saved items are no longer available. You can remove them, or open what's left.</span>
      </div>

      ${SAVED.map(savedRow).join("\n      ")}

      <!-- Grid is the same set, a second view mode. -->
      <div class="ml-market-grid" data-state="grid_view">
        ${SAVED.filter((s) => !s.gone).map((s) => `<a class="ml-market-card" href="${s.href}">
          <div class="ml-market-card__media"><img src="${s.img}" alt="" loading="lazy" /></div>
          <div class="ml-market-card__body">
            <div class="ml-market-card__title">${s.name}</div>
            <div class="ml-market-card__meta">${s.meta}</div>
          </div>
        </a>`).join("\n        ")}
      </div>

      <!-- Collections and saved searches are routes of their own; the tab
           lists them rather than reimplementing either. -->
      <div data-state="collections">
        ${[["Mumbai studios", "4 items · Private"], ["Potential collaborators", "7 items · Private"],
           ["Gear wishlist", "3 items · Shared"]]
          .map(([n, m]) => rowLink(n, `${P}collection_detail/index.html`, m)).join("\n        ")}
        <button class="ml-btn ml-btn--secondary ml-btn--block" type="button" style="margin-top:var(--space-2)"
                data-open-sheet="sf-collection">Create a collection</button>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Saved searches</h2></div>
        ${rowLink("Saved searches", `${P}saved_searches/index.html`, "3 searches · 1 with alerts on")}
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">Nothing saved yet</h2>
        <p class="ml-empty__body">Save people, businesses, services, listings and opportunities to find them later.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/discover/discover_feed/index.html">Discover</a>
      </div>
    </main>

    <!-- Bulk actions live in the footer only while selecting. -->
    <footer class="ml-app__footer" data-state="bulk_select" style="display:flex;gap:var(--space-2)">
      <button class="ml-btn ml-btn--secondary" type="button" style="flex:1" data-open-sheet="sf-move">Move</button>
      <button class="ml-btn ml-btn--primary" type="button" style="flex:1">Remove</button>
    </footer>

    <!-- Sheets: sort + filter, move to collection, create collection. -->
    <div class="ml-sheet-scrim" data-sheet-scrim-for="sf-filter"></div>
    <div class="ml-sheet" id="sf-filter" role="dialog" aria-label="Sort and filter">
      <span class="ml-sheet__handle"></span>
      <div class="ml-sheet__body">
        <div class="ml-section"><h2>Sort</h2></div>
        <div class="ml-chip-wrap">
          ${[["Recently saved", 1], ["Recently updated", 0], ["Alphabetical", 0], ["Entity type", 0]]
            .map(([t, on]) => `<button type="button" class="ml-chip" aria-pressed="${on ? "true" : "false"}">${t}</button>`).join("\n          ")}
        </div>
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Show</h2></div>
        <div class="ml-chip-wrap">
          ${["Favourites", "People", "Bands", "Studios", "Market", "Opportunities", "Collections"]
            .map((t) => `<button type="button" class="ml-chip" aria-pressed="false">${t}</button>`).join("\n          ")}
        </div>
        <button class="ml-btn ml-btn--primary ml-btn--block" type="button" style="margin-top:var(--space-3)" data-close-sheet="sf-filter">Apply</button>
      </div>
    </div>

    <div class="ml-sheet-scrim" data-sheet-scrim-for="sf-move"></div>
    <div class="ml-sheet" id="sf-move" role="dialog" aria-label="Move to collection">
      <span class="ml-sheet__handle"></span>
      <div class="ml-sheet__body">
        <div class="ml-section"><h2>Move to</h2></div>
        ${["Mumbai studios", "Potential collaborators", "Gear wishlist"]
          .map((c) => `<button class="ml-action-row" type="button">
          <span class="ml-choice__body"><span class="ml-choice__title">${c}</span></span>
          ${icon("arrow-forward", { size: 18 })}
        </button>`).join("\n        ")}
        <button class="ml-btn ml-btn--secondary ml-btn--block" type="button" style="margin-top:var(--space-2)" data-close-sheet="sf-move">Cancel</button>
      </div>
    </div>

    <div class="ml-sheet-scrim" data-sheet-scrim-for="sf-collection"></div>
    <div class="ml-sheet" id="sf-collection" role="dialog" aria-label="Create a collection">
      <span class="ml-sheet__handle"></span>
      <div class="ml-sheet__body">
        <div class="ml-field">
          <label for="c-name">Collection name</label>
          <input id="c-name" type="text" placeholder="Mumbai studios" />
        </div>
        <div class="ml-field">
          <label for="c-desc">Description (optional)</label>
          <input id="c-desc" type="text" placeholder="Rooms I'd book again" />
        </div>
        <div class="ml-field">
          <label for="c-priv">Privacy</label>
          <select id="c-priv"><option>Private</option><option>Shared with a link</option><option>Public</option></select>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-1)">
          Private by default. A shared collection never exposes items you can't already see.
        </p>
        <button class="ml-btn ml-btn--primary ml-btn--block" type="button" style="margin-top:var(--space-3)" data-close-sheet="sf-collection">Create</button>
      </div>
    </div>
  </div>`,
});

/* ── collection_detail ──────────────────────────────────────────────────── */
const IN_COLLECTION = SAVED.filter((s) => !s.gone && (s.cat === "studios" || s.cat === "artists")).slice(0, 4);

writeScreen({
  flow: "saved_favorites", screen: "collection_detail", title: "Saved — Collection",
  body: `
  <div class="ml-app">
    ${bar("Mumbai studios", `${P}saved_favorites_hub/index.html?state=collections`,
      `<button class="ml-icon-btn ml-icon-btn--bare" type="button" data-open-sheet="cd-settings" aria-label="Collection settings">${icon("filter", { size: 20 })}</button>`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default shared private editing">
        <p class="ml-type-2" style="color:var(--color-muted);margin:0">Rooms I'd book again.</p>
        <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-1) 0 var(--space-3)">
          ${IN_COLLECTION.length} items
        </p>
      </div>

      <span class="ml-status ml-status--neutral" data-state="private">Private — only you can see this</span>
      <span class="ml-status ml-status--accent" data-state="shared">Shared with a link</span>

      <div class="ml-banner ml-banner--info" data-state="editing">
        <span class="ml-banner__body">Editing. Remove or move items, then done.</span>
      </div>

      ${IN_COLLECTION.map((s) => `<a class="ml-result" data-state="default shared private editing" href="${s.href}">
        <img class="ml-result__thumb" src="${s.img}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">${s.name}</span>
          <span class="ml-result__meta">${s.meta}</span>
        </span>
      </a>`).join("\n      ")}

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">This collection is empty</h2>
        <p class="ml-empty__body">Add saved items to it from the Saved list.</p>
        <a class="ml-btn ml-btn--primary" href="${P}saved_favorites_hub/index.html">Add to collection</a>
      </div>
    </main>

    <div class="ml-sheet-scrim" data-sheet-scrim-for="cd-settings"></div>
    <div class="ml-sheet" id="cd-settings" role="dialog" aria-label="Collection settings">
      <span class="ml-sheet__handle"></span>
      <div class="ml-sheet__body">
        <div class="ml-field">
          <label for="cd-name">Name</label>
          <input id="cd-name" type="text" value="Mumbai studios" />
        </div>
        <div class="ml-field">
          <label for="cd-priv">Privacy</label>
          <select id="cd-priv"><option>Private</option><option>Shared with a link</option><option>Public</option></select>
        </div>
        <a class="ml-action-row" href="?state=editing"><span class="ml-choice__body"><span class="ml-choice__title">Edit items</span></span>${icon("arrow-forward", { size: 18 })}</a>
        <a class="ml-action-row" href="${P}saved_favorites_hub/index.html?state=collections">
          <span class="ml-choice__body">
            <span class="ml-choice__title" style="color:var(--color-error)">Delete collection</span>
            <span class="ml-choice__meta">The saved items themselves are not deleted</span>
          </span>
          ${icon("arrow-forward", { size: 18 })}
        </a>
        <button class="ml-btn ml-btn--secondary ml-btn--block" type="button" style="margin-top:var(--space-2)" data-close-sheet="cd-settings">Close</button>
      </div>
    </div>
  </div>`,
});

/* ── saved_searches ─────────────────────────────────────────────────────── */
const SEARCHES = [
  ["Recording studios in Mumbai", "Studios · within 8 km · under ₹2,500/hr", true],
  ["Session vocalist, Hindi", "People · Mumbai · available", false],
  ["Neve 1073", "Market · gear sale · any condition", false],
];

writeScreen({
  flow: "saved_favorites", screen: "saved_searches", title: "Saved — Searches",
  body: `
  <div class="ml-app">
    ${bar("Saved searches", `${P}saved_favorites_hub/index.html?state=collections`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-banner ml-banner--info" data-state="alerts_on">
        <span class="ml-banner__body">New results for "Recording studios in Mumbai".
          <a class="ml-link" href="/preview/flows/search/universal_search/index.html?state=results">View results</a></span>
      </div>

      <div class="ml-banner ml-banner--info" data-state="editing">
        <span class="ml-banner__body">Editing. Turn alerts on or off, or delete a search.</span>
      </div>

      <div data-state="default alerts_on editing">
        ${SEARCHES.map(([q, f, alerts]) => `<div class="ml-check-row">
          <a class="ml-row ml-choice__body" href="/preview/flows/search/universal_search/index.html?state=results">
            <span class="ml-choice__title">${q}</span>
            <span class="ml-choice__meta">${f}</span>
          </a>
          <button class="ml-switch" type="button" role="switch" aria-checked="${alerts}" aria-label="Alerts for ${q}"></button>
        </div>`).join("\n        ")}
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Alerts are delivered as notifications. You can turn them all off in Notification settings.
        </p>
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No saved searches</h2>
        <p class="ml-empty__body">Run a search, apply the filters you want, then save it to be told when something new matches.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/search/universal_search/index.html">Search MusicLinkd</a>
      </div>
    </main>
  </div>`,
});

/* ── saved_item_detail ──────────────────────────────────────────────────── */
/* The brief: "Saved should never become a duplicate detail page." So this
   shows a PREVIEW of the original plus the saved-specific facts, and one
   action that leaves for the canonical flow. */
writeScreen({
  flow: "saved_favorites", screen: "saved_item_detail", title: "Saved — Item",
  body: `
  <div class="ml-app">
    ${bar("Saved item", `${P}saved_favorites_hub/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <a class="ml-result" data-state="default" href="/preview/flows/booking/studio_profile/index.html">
        <img class="ml-result__thumb" src="${media("stage", 1)}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">Yash Raj Sound Studios</span>
          <span class="ml-result__meta">Andheri West · from ₹1,800/hr</span>
        </span>
      </a>

      <div class="ml-summary" style="margin-top:var(--space-3)" data-state="default">
        <div class="ml-summary__row"><span>Saved</span><span>1 week ago</span></div>
        <div class="ml-summary__row"><span>Favourite</span><span>Yes</span></div>
        <div class="ml-summary__row"><span>Collection</span><span>Mumbai studios</span></div>
        <div class="ml-summary__row"><span>Type</span><span>Studio</span></div>
      </div>

      <div class="ml-empty" data-state="unavailable">
        <h2 class="ml-empty__title">This item is no longer available</h2>
        <p class="ml-empty__body">It may have been sold, closed, made private or removed. You can take it out of your saved list.</p>
        <a class="ml-btn ml-btn--primary" href="${P}saved_favorites_hub/index.html">Remove from saved</a>
      </div>
    </main>
    <footer class="ml-app__footer" data-state="default" style="display:flex;gap:var(--space-2)">
      <button class="ml-btn ml-btn--secondary" type="button" style="flex:1">Remove</button>
      <a class="ml-btn ml-btn--primary" style="flex:1" href="/preview/flows/booking/studio_profile/index.html">Open original</a>
    </footer>
  </div>`,
});

finish("saved_favorites: ");
