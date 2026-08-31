#!/usr/bin/env node
/**
 * Flow 19 — Search, against the supplied brief.
 *
 * 40 numbered sections, 2 screens. The brief's SCREEN 06–12 are "PEOPLE
 * RESULTS", "BUSINESS RESULTS", "SERVICE RESULTS" and so on — seven sections
 * that are one result list filtered by entity type. The query has already run;
 * only what comes back differs. So they are states, and the entity tabs above
 * the results are the same data-chip mechanism every other flow now uses.
 *
 * SCREEN 14–19 are "PEOPLE FILTERS", "BUSINESS FILTERS", … — six sections that
 * are one filter sheet whose fields change with the entity being filtered. The
 * brief is explicit: "Do not show irrelevant filters." That is a state, not
 * six screens, so search_filters is ONE screen with six filter sets.
 *
 * Search never owns its destinations. Every result routes into the canonical
 * flow — a person to Public Profile, a listing to Market, an opportunity to
 * Opportunities — which is why there is no result-detail screen here at all.
 *
 * No new CSS: .ml-result, .ml-suggest, .ml-chip, .ml-tabs, .ml-empty,
 * .ml-section and .ml-status already exist.
 */
import { writeScreen, backArrow, icon, assertIcons, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/search/";
assertIcons(["search", "arrow-forward", "filter", "cancel", "heart"]);

/* chip may list several states (the All tab is selected in both "results" and
   "typo"); href must name exactly one. */
const tab = (chip, label, href = chip) =>
  `<a class="ml-tabs__tab" role="tab" data-chip="${chip}" href="?state=${href}">${label}</a>`;

/* Every result is the same row: thumbnail, name, what it is, and a trailing
   fact. What changes between entity types is the TRAILING fact and the
   destination, not the shape. */
const result = (r) => `<a class="ml-result" data-state="${r.states}" href="${r.href}">
        <img class="ml-result__thumb${r.square ? " ml-result__thumb--square" : ""}" src="${r.img}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">${r.name}</span>
          <span class="ml-result__meta">${r.meta}</span>
          ${r.status ? `<span class="ml-status ml-status--${r.status[0]}">${r.status[1]}</span>` : ""}
        </span>
        ${r.trail ? `<span class="ml-result__trail">${r.trail}</span>` : ""}
      </a>`;

/* R is the state LIST a result row belongs to — "typo" is the same result set
   with a did-you-mean offered above it, so rows carry both. RESULTS is the
   single state name to navigate to, because a href can only name one. */
const R = "results typo";
const RESULTS_STATE = "results";
const HITS = [
  // people
  { states: `${R} people partial`, name: "Ishita Rao", meta: "Session vocalist · Mumbai · Hindi, English",
    img: media("faces", 2), trail: "4.9 ★", status: ["success", "Available"],
    href: "/preview/flows/public_profile/public_profile_view/index.html" },
  { states: `${R} people partial`, name: "Meera Nair", meta: "Playback vocalist · Kochi · Malayalam, Tamil",
    img: media("faces", 4), trail: "5.0 ★",
    href: "/preview/flows/public_profile/public_profile_view_talent/index.html" },
  { states: `${R} people`, name: "Kabir Sheth", meta: "Producer · Pune · Indie, electronic",
    img: media("faces", 8), trail: "4.7 ★", status: ["warning", "Booked soon"],
    href: "/preview/flows/public_profile/public_profile_view/index.html" },
  // businesses
  { states: `${R} businesses`, name: "Yash Raj Sound Studios", meta: "Recording studio · Andheri West · 2.4 km",
    img: media("stage", 1), trail: "₹1,800/hr", status: ["accent", "Verified"],
    href: "/preview/flows/business_profiles/business_overview/index.html" },
  { states: `${R} businesses`, name: "Blue Note Recording Co.", meta: "Recording studio · Bandra · 4.1 km",
    img: media("gear", 7), trail: "₹2,400/hr",
    href: "/preview/flows/business_profiles/business_overview/index.html" },
  // services
  { states: `${R} services`, name: "Full Track Mix & Master", meta: "Blue Note Recording Co. · in person or remote",
    img: media("gear", 6), trail: "₹6,000",
    href: "/preview/flows/public_profile/talent_services_view/index.html" },
  { states: `${R} services`, name: "Vocal Tuning & Comping", meta: "Ishita Rao · remote · 3 day turnaround",
    img: media("stage", 11), trail: "₹2,000",
    href: "/preview/flows/public_profile/talent_services_view/index.html" },
  // marketplace
  { states: `${R} marketplace_results`, name: "Shure SM7B Microphone", meta: "Gear Sale · Mumbai · like new",
    img: media("gear", 0), trail: "₹28,500",
    href: "/preview/flows/marketplace/marketplace_item_detail/index.html" },
  { states: `${R} marketplace_results`, name: "Focusrite Scarlett 2i2", meta: "Gear Rental · Mumbai",
    img: media("gear", 3), trail: "₹800/day", status: ["success", "Available"],
    href: "/preview/flows/marketplace/marketplace_item_detail/index.html" },
  // opportunities
  { states: `${R} opportunities_results`, name: "Session vocalist — Hindi pop EP", meta: "Octave Sound Labs · Mumbai · closes in 3 days",
    img: media("stage", 4), trail: "₹12,000/day", status: ["warning", "Closing soon"],
    href: "/preview/flows/opportunities/opportunity_detail/index.html" },
  // portfolio
  { states: `${R} portfolio_results`, name: "Neon Monsoon", meta: "Single · 2026 · Ishita Rao · Hindi pop",
    img: media("stage", 3), square: true, trail: "2026",
    href: "/preview/flows/portfolio/profile_project_view/index.html" },
  // groups
  { states: `${R} groups`, name: "Mumbai Session Players", meta: "Group · Mumbai · 1,240 members",
    img: media("stage", 6), trail: "Join",
    href: "/preview/flows/communication/networking_feed/index.html" },
];

const COUNTS = [
  ["people", "People", 3], ["businesses", "Businesses", 2], ["services", "Services", 2],
  ["marketplace_results", "Market", 2], ["opportunities_results", "Opportunities", 1],
  ["portfolio_results", "Portfolio", 1], ["groups", "Groups", 1],
];

writeScreen({
  flow: "search", screen: "universal_search", title: "Search",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow("/preview/flows/discover/discover_feed/index.html", "Back")}
      <h1 class="ml-visually-hidden">Search</h1>
      <div class="ml-field" style="flex:1">
        <label for="u-q" class="ml-visually-hidden">Search MusicLinkd</label>
        <input id="u-q" type="search" value="vocalist" placeholder="People, studios, services, equipment…" />
      </div>
    </header>

    <!-- Entity tabs. Present once results exist, and each is a state of this
         same list rather than a separate results screen. -->
    <nav class="ml-tabs" role="tablist" aria-label="Result type"
         data-state="${R} people businesses services marketplace_results opportunities_results portfolio_results groups partial"
         style="margin:0 var(--space-4)">
      ${tab(R, "All", RESULTS_STATE)}${COUNTS.map(([st, l]) => tab(st, l)).join("")}
    </nav>

    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <!-- home: nothing typed yet. Recents and taxonomy-derived suggestions
           only — the brief says not to fill space with arbitrary content. -->
      <div data-state="home">
        <div class="ml-section"><h2>Recent</h2>
          <button class="ml-section__link" type="button">Clear all</button></div>
        ${["mixing engineer Mumbai", "recording studio Delhi", "tabla"]
          .map((q) => `<a class="ml-action-row" href="?state=${RESULTS_STATE}">
          <span class="ml-choice__body"><span class="ml-choice__title">${q}</span></span>
          ${icon("cancel", { size: 18 })}
        </a>`).join("\n        ")}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Browse</h2></div>
        <div class="ml-chip-wrap">
          ${["Vocalists", "Producers", "Engineers", "Studios", "Rehearsal rooms", "Gear", "Opportunities"]
            .map((t) => `<a class="ml-chip" href="?state=${RESULTS_STATE}">${t}</a>`).join("\n          ")}
        </div>
      </div>

      <!-- live_typing: suggestions come from the taxonomy, not invented. -->
      <div data-state="live_typing">
        ${["vocalist", "vocalist Mumbai", "vocalist Hindi", "vocal booth", "vocal tuning"]
          .map((sg) => `<a class="ml-suggest" href="?state=${RESULTS_STATE}">
          ${icon("search", { size: 18 })}<span>${sg}</span>
        </a>`).join("\n        ")}
      </div>

      <!-- Typo recovery is a state of the results, not a screen. -->
      <div data-state="typo">
        <p class="ml-type-2" style="margin:0 0 var(--space-2)">
          Showing results for <b>vocalist</b>. Search instead for
          <a class="ml-link" href="?state=empty">vocalest</a>.
        </p>
      </div>

      <div class="ml-chip-wrap" data-state="${R} partial typo" style="margin-bottom:var(--space-2)">
        ${[["Mumbai", 1], ["Available", 1], ["Hindi", 0]]
          .map(([t, on]) => `<button type="button" class="ml-chip" aria-pressed="${on ? "true" : "false"}">${t}${on ? " ×" : ""}</button>`).join("\n        ")}
        <a class="ml-chip" href="${P}search_filters/index.html?state=people">Filters</a>
      </div>

      <p class="ml-type-1" data-state="${R} typo" style="color:var(--color-muted);margin:0 0 var(--space-2)">
        ${HITS.length} results for "vocalist"
      </p>

      ${HITS.map(result).join("\n      ")}

      <!-- Partial: one category empty must not make the whole search look
           empty, which the brief states outright. -->
      <div data-state="partial">
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Businesses</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0">
          No businesses match "vocalist". People and services below still do.
        </p>
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No results found</h2>
        <p class="ml-empty__body">Try a different search, or remove some filters.</p>
        <a class="ml-btn ml-btn--primary" href="?state=${RESULTS_STATE}">Clear filters</a>
        <div class="ml-chip-wrap" style="margin-top:var(--space-3);justify-content:center">
          ${["vocalist", "session singer", "playback vocalist"].map((t) => `<a class="ml-chip" href="?state=${RESULTS_STATE}">${t}</a>`).join("")}
        </div>
      </div>

      <!-- The brief: show only what this person is allowed to discover. -->
      <div class="ml-empty" data-state="permission">
        <h2 class="ml-empty__title">Some results are hidden</h2>
        <p class="ml-empty__body">A few profiles matching this search aren't public. You're seeing everything you have access to.</p>
        <a class="ml-btn ml-btn--primary" href="?state=${RESULTS_STATE}">Show what I can see</a>
      </div>

      <div class="ml-empty" data-state="offline">
        <h2 class="ml-empty__title">You're offline</h2>
        <p class="ml-empty__body">These are the last results we loaded. They may be out of date, and availability isn't current.</p>
        <a class="ml-btn ml-btn--primary" href="?state=${RESULTS_STATE}">Retry</a>
      </div>

      <div class="ml-empty" data-state="error">
        <h2 class="ml-empty__title">We couldn't complete your search</h2>
        <p class="ml-empty__body">Something went wrong at our end, not yours.</p>
        <a class="ml-btn ml-btn--primary" href="?state=${RESULTS_STATE}">Retry</a>
      </div>
    </main>
  </div>`,
});

/* ── search_filters — one sheet, six filter sets ────────────────────────── */
/* The brief lists PEOPLE / BUSINESS / SERVICE / MARKETPLACE / OPPORTUNITY /
   PORTFOLIO FILTERS as six sections. They are one screen whose fields change
   with what is being filtered — "Do not show irrelevant filters." */
const FILTER_SETS = [
  ["people", "People", [
    ["Role", ["Vocalist", "Producer", "Engineer", "Composer", "DJ"]],
    ["Genre", ["Pop", "Indie", "Hip-hop", "Classical", "Electronic"]],
    ["Language", ["Hindi", "English", "Marathi", "Tamil"]],
    ["Availability", ["Available now", "Open for gigs", "Any"]],
  ]],
  ["businesses", "Businesses", [
    ["Type", ["Recording studio", "Rehearsal room", "Music school", "Label", "Venue"]],
    ["Service", ["Tracking", "Mixing", "Mastering", "Rental"]],
    ["Rating", ["4.5+", "4.0+", "Any"]],
  ]],
  ["services", "Services", [
    ["Category", ["Recording", "Mixing", "Mastering", "Production", "Lessons"]],
    ["Delivery", ["In person", "Remote", "Either"]],
    ["Duration", ["Per hour", "Half day", "Full day", "Per track"]],
  ]],
  ["marketplace_results", "Market", [
    ["Listing", ["Buy", "Sell", "Rent"]],
    ["Category", ["Microphones", "Monitors", "Interfaces", "Backline", "Keys"]],
    ["Condition", ["New", "Like new", "Used"]],
  ]],
  ["opportunities_results", "Opportunities", [
    ["Type", ["Session", "Tour", "Project", "Collaboration"]],
    ["Mode", ["In person", "Remote"]],
    ["Deadline", ["This week", "This month", "Any"]],
  ]],
  ["portfolio_results", "Portfolio", [
    ["Work type", ["Track", "Album", "Video", "Live"]],
    ["Genre", ["Pop", "Indie", "Film", "Classical"]],
    ["Year", ["2026", "2025", "Earlier"]],
  ]],
];

writeScreen({
  flow: "search", screen: "search_filters", title: "Search — Filters",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(`${P}universal_search/index.html?state=results`, "Back to results")}
      <h1 class="ml-type-3" style="margin:0;flex:1">Filters</h1>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="active">
        <div class="ml-section"><h2>Active</h2>
          <button class="ml-section__link" type="button">Clear all</button></div>
        <div class="ml-chip-wrap">
          ${["Mumbai ×", "Vocalist ×", "Available ×"].map((t) => `<button type="button" class="ml-chip" aria-pressed="true">${t}</button>`).join("\n          ")}
        </div>
      </div>

      <!-- Location and sort apply to every entity type, so they sit outside
           the per-entity block rather than being repeated six times. -->
      <div class="ml-section"><h2>Location</h2></div>
      <div class="ml-chip-wrap">
        ${[["Mumbai", 1], ["Pune", 0], ["Delhi", 0], ["Bengaluru", 0], ["Anywhere", 0]]
          .map(([t, on]) => `<button type="button" class="ml-chip" aria-pressed="${on ? "true" : "false"}">${t}</button>`).join("\n        ")}
      </div>
      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Within</h2>
        <span class="ml-section__link">8 km</span></div>
      <input class="ml-slider" type="range" min="1" max="50" value="8" aria-label="Maximum distance in kilometres" />

      ${FILTER_SETS.map(([st, , groups]) => `<div data-state="${st}">
        ${groups.map(([g, opts]) => `<div class="ml-section" style="margin-top:var(--space-3)"><h2>${g}</h2></div>
        <div class="ml-chip-wrap">
          ${opts.map((o, i) => `<button type="button" class="ml-chip" aria-pressed="${i === 0 && g === "Role" ? "true" : "false"}">${o}</button>`).join("\n          ")}
        </div>`).join("\n        ")}
      </div>`).join("\n      ")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Sort</h2></div>
      <div role="radiogroup" aria-label="Sort">
        ${[["Relevance", "How closely it matches what you typed.", true],
           ["Nearest", "Closest to your location first.", false],
           ["Newest", "Most recently added first.", false],
           ["Rating", "Highest rated first, where ratings exist.", false]]
          .map(([t, m, on]) => `<div class="ml-choice" role="radio" aria-checked="${on}" tabindex="${on ? 0 : -1}">
          <span class="ml-choice__body">
            <span class="ml-choice__title">${t}</span>
            <span class="ml-choice__meta">${m}</span>
          </span>
          <span class="ml-choice__mark"></span>
        </div>`).join("\n        ")}
      </div>
    </main>
    <footer class="ml-app__footer" style="display:flex;gap:var(--space-2)">
      <button class="ml-btn ml-btn--secondary" type="reset" style="flex:1">Reset</button>
      <a class="ml-btn ml-btn--primary" style="flex:1" href="${P}universal_search/index.html?state=results">Show results</a>
    </footer>
  </div>`,
});

finish("search: ");
