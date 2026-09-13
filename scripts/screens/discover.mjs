#!/usr/bin/env node
/**
 * Flow 4 — Discover (21 screens).
 *
 * The governing distinction, from the brief and enforced throughout:
 *
 *     Market  = I want to buy, sell or rent something.
 *     Discover = I want to FIND someone or something inside the ecosystem.
 *
 * So nothing here leads with a price. A service card's subject is the person
 * offering it; the rate is a qualifier on the second line. The moment a card
 * leads with "₹12,000" it has become a Market listing.
 *
 * Five destinations in the bottom nav — Home, Discover, Connections, Market
 * (Beta), Profile. Messaging is NOT a destination: it lives inside
 * Connections, per the approved navigation architecture.
 *
 * Imagery is Unsplash, as every other screen in the app uses. The 680 files
 * under frontend/website/assets/img are NOT used: 441 of them sit in folders
 * named behance / shazam / spotify / tinder — competitor UI captures kept as
 * design reference — and 673 of the 680 are untracked local files. Putting
 * those into product screens as content would be wrong on both counts.
 */
import { writeScreen, backArrow, finish, icon, assertIcons } from "../write-screen.mjs";

/* Fail the run on a typo rather than render an empty box. */
assertIcons(["search","microphone","bell","person","home","compass","speech","shopping-bag",
  "filter","location","sparkle","play","pause","heart","heart-outline","share","add",
  "arrow-forward","clock","star","globe","music-on"]);

const D = "/preview/flows/discover/";
const done = [];

/* ── shared chrome ─────────────────────────────────────────────────────── */

const TABS = [
  ["Home", "/preview/flows/discover/trending_recommended_sections/index.html",
   "home"],
  ["Discover", D + "discover_feed/index.html",
   "compass"],
  ["Connections", "/preview/flows/communication/networking_feed/index.html",
   "speech"],
  ["Market", "/preview/flows/marketplace/marketplace_browse/index.html",
   "shopping-bag"],
  ["Profile", "/preview/flows/profile_management/profile_overview/index.html",
   "person"],
];

const tabbar = (active) => `<nav class="ml-tabbar" aria-label="Primary">
      ${TABS.map(([label, href, path]) => {
        const on = label === active;
        // the active destination renders as a span, not a link
        const tag = on ? "span" : "a";
        const attrs = on ? ' aria-current="page"' : ` href="${href}"`;
        return `<${tag} class="ml-tabbar__item"${attrs}>
        <span class="ml-tabbar__icon">${icon(path, { size: 22 })}</span>
        ${label}${label === "Market" ? `<span class="ml-tabbar__beta">Beta</span>` : ""}
      </${tag}>`;
      }).join("\n      ")}
    </nav>`;

const search = (placeholder, href = D + "global_search/index.html") =>
  `<a class="ml-search" href="${href}" style="text-decoration:none;color:inherit">
        <span class="ml-search__icon">${icon("search", { size: 18 })}</span>
        <span style="flex:1;color:var(--color-muted);font-size:var(--type-3)">${placeholder}</span>
        <span class="ml-search__divider"></span>
        <span class="ml-icon-btn ml-icon-btn--bare" aria-hidden="true" style="min-width:24px;min-height:24px;border:0">
          ${icon("microphone", { size: 20 })}
        </span>
      </a>`;

/* the six discovery dimensions, as a tab strip on every results screen */
const DIMENSIONS = [
  ["People", "people_talent"], ["Businesses", "businesses"], ["Spaces", "studios_spaces"],
  ["Music", "music_audio"], ["Services", "services"], ["Projects", "projects"],
  ["Communities", "communities"], ["Opportunities", "discover_opportunities"],
];
const dimTabs = (active) => `<nav class="ml-tabs" aria-label="Discovery type">
        ${DIMENSIONS.map(([l, k]) => l === active
          ? `<span class="ml-tabs__tab" aria-selected="true" aria-current="page">${l}</span>`
          : `<a class="ml-tabs__tab" href="${D}${k}/index.html" aria-selected="false">${l}</a>`).join("\n        ")}
      </nav>`;

const navbar = (title, back = D + "discover_feed/index.html", action = "") =>
  `<header class="ml-navbar">
      ${backArrow(back)}
      <h1 class="ml-navbar__title">${title}</h1>
      <span class="ml-navbar__spacer"></span>
      ${action}
    </header>`;

const filterBtn = `<a class="ml-icon-btn" href="${D}advanced_filters/index.html" aria-label="Filters">
        ${icon("filter", { size: 20 })}
      </a>`;

const verified = `<span class="ml-verified" aria-label="Verified">${icon("check", { size: 10 })}</span>`;

/* Real photography from frontend/website/assets/img, not stock.
 * editorials/ (29) is Indian performance photography; speakers/ (23) is
 * design-object and gear art; inspiration/ (184) is studio and gear detail.
 * The other four folders (441 files) are deliberately NOT used: their
 * filenames give them away as Behance CDN captures and app-store chrome
 * screenshots kept as design reference, not photographs.
 * Picks are fixed, not random, so a screen shows the same photo every run. */
/* Real photography from frontend/website/assets/img.
 * editorials/ (29) — Indian musicians and live performance. The only true
 *   photography set in the repo, and the right register for this product.
 * speakers/ (23) — gear and design objects, used for rooms and services.
 * inspiration/ (184) is NOT used: it looked usable by filename but is
 *   scraped gig posters and album art, which rendered a purple poster as a
 *   'Spaces' tile. The remaining four folders (441) are Behance CDN captures
 *   and app-store chrome screenshots — design reference, never content.
 * Picks are fixed so a screen shows the same photo on every run. */
const IMG = {
  ananya: "/assets/img/editorials/0b7348ff4eeab75b4ebf24e5e05bfef6.jpg",
  rahul: "/assets/img/editorials/324eae250c23aafe52ea0ef9caab5d5f.jpg",
  mehek: "/assets/img/editorials/4dcf33e3aa19ee077f4e95c2b66ae43a.jpg",
  arjun: "/assets/img/editorials/8f517fcabb27d88bec943d192068c756.jpg",
  priya: "/assets/img/editorials/a023f0e015a6ddc1ca4135942479a715.jpg",
  studio1: "/assets/img/speakers/0159435ccca405aa6d2c59bb4f11947b.jpg",
  studio2: "/assets/img/speakers/39fdf43f680420b44f547c6cb535900b.jpg",
  studio3: "/assets/img/speakers/5e4c5a1598e10dfe9c213609ce2ab5fb.jpg",
  mic: "/assets/img/editorials/c148e6ee64619796d9ef250e1b9f22d9.jpg",
  crowd: "/assets/img/editorials/0b7348ff4eeab75b4ebf24e5e05bfef6.jpg",
  guitar: "/assets/img/editorials/f651ac8b73ceb8d2b7ca4967a18179f2.jpg",
  piano: "/assets/img/editorials/41f63164364f0757ddb2a20693f4b144.jpg",
  desk: "/assets/img/speakers/20400c2337df4ff17c3b0e72deefa4ad.jpg",
  band: "/assets/img/editorials/f9c72888832942ada70666cc17bac058.png",
};

const result = ({ img, name, meta, trail = "", square = false, href = D + "discover_profile_detail/index.html", ver = false }) =>
  `<a class="ml-result" href="${href}">
          <img class="ml-result__thumb${square ? " ml-result__thumb--square" : ""}" src="${img}" alt="" />
          <span class="ml-result__body">
            <span class="ml-result__name">${name}${ver ? verified : ""}</span>
            <span class="ml-result__meta">${meta}</span>
          </span>
          ${trail ? `<span class="ml-result__trail">${trail}</span>` : ""}
        </a>`;

const match = (pct) => `<span class="ml-match">
            <span class="ml-match__pct">${pct}%</span>
            <span class="ml-match__track"><span class="ml-match__fill" style="width:${pct}%"></span></span>
            <span class="ml-match__label">match</span>
          </span>`;

const chip = (l, sel = false) => `<button class="ml-chip" aria-pressed="${sel}">${l}</button>`;

/* ── 1 · Discover Home ─────────────────────────────────────────────────── */
/* Restructured per the approved hierarchy: search first, then the six
   dimensions, then For You / Trending / Collaborators / Studios / Rising.
   The Swipe'n'Match banner is gone — it competed with search for the top of
   the screen and swiping is not the primary Discover action. */

done.push(writeScreen({ flow: "discover", screen: "discover_feed", title: "Discover Home", body: `
  <div class="ml-app">
    <header class="ml-appbar">
      <div class="ml-appbar__titles">
        <h1 class="ml-appbar__title">Discover</h1>
        <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-1) 0 0">Connect with India's music grid</p>
      </div>
      <div class="ml-appbar__actions">
        <a class="ml-icon-btn" href="${D}saved_profiles/index.html" aria-label="Saved">
          ${icon("pin", { size: 20 })}
        </a>
        <a class="ml-icon-btn" href="${D}nearby/index.html" aria-label="Nearby">
          ${icon("location", { size: 20 })}
        </a>
      </div>
    </header>

    <main class="ml-app__body">
      ${search("Search talent, studios, projects…")}

      <div class="ml-section"><h2>What are you looking for?</h2></div>
      <div class="ml-dimension-grid">
        <a class="ml-dimension" href="${D}people_talent/index.html">
          <img src="${IMG.mic}" alt="" />
          <span class="ml-dimension__label">People<span class="ml-dimension__count">12,400 artists</span></span>
        </a>
        <a class="ml-dimension" href="${D}studios_spaces/index.html">
          <img src="${IMG.studio1}" alt="" />
          <span class="ml-dimension__label">Spaces<span class="ml-dimension__count">860 studios</span></span>
        </a>
        <a class="ml-dimension" href="${D}projects/index.html">
          <img src="${IMG.guitar}" alt="" />
          <span class="ml-dimension__label">Projects<span class="ml-dimension__count">3,100 live</span></span>
        </a>
        <a class="ml-dimension" href="${D}services/index.html">
          <img src="${IMG.desk}" alt="" />
          <span class="ml-dimension__label">Services<span class="ml-dimension__count">Mix · Master · Session</span></span>
        </a>
        <a class="ml-dimension" href="${D}businesses/index.html">
          <img src="${IMG.studio3}" alt="" />
          <span class="ml-dimension__label">Businesses<span class="ml-dimension__count">Labels · Agencies</span></span>
        </a>
        <a class="ml-dimension" href="${D}communities/index.html">
          <img src="${IMG.crowd}" alt="" />
          <span class="ml-dimension__label">Communities<span class="ml-dimension__count">By genre &amp; city</span></span>
        </a>
      </div>

      <div class="ml-section">
        <h2>For you</h2>
        <a class="ml-section__link" href="${D}people_talent/index.html">See all</a>
      </div>
      <p class="ml-type-1" style="color:var(--color-muted);margin:0 0 var(--space-2)">Matched to your role, genres and city.</p>
      <div>
        ${result({ img: IMG.rahul, name: "Rahul Iyer", ver: true, meta: "Producer · Mix Engineer · Mumbai", trail: match(94) })}
        ${result({ img: IMG.priya, name: "Priya Nair", meta: "Session Guitarist · Pune", trail: match(88) })}
        ${result({ img: IMG.mehek, name: "Mehek Kapoor", meta: "Composer · Delhi NCR", trail: match(81) })}
      </div>

      <div class="ml-section">
        <h2>Trending projects</h2>
        <a class="ml-section__link" href="${D}projects/index.html">See all</a>
      </div>
      <div class="ml-rail">
        <a class="ml-media-card" href="${D}project_detail/index.html">
          <img src="${IMG.crowd}" alt="" />
          <span class="ml-media-card__tag"><span class="ml-status ml-status--accent">Open roles</span></span>
          <div class="ml-media-card__body">
            <div class="ml-media-card__title">Monsoon Sessions</div>
            <div class="ml-media-card__sub">Indie EP · needs a bassist</div>
          </div>
        </a>
        <a class="ml-media-card" href="${D}project_detail/index.html">
          <img src="${IMG.band}" alt="" />
          <span class="ml-media-card__tag"><span class="ml-status ml-status--accent">Live band</span></span>
          <div class="ml-media-card__body">
            <div class="ml-media-card__title">The Local Train</div>
            <div class="ml-media-card__sub">Indie Rock · Delhi NCR</div>
          </div>
        </a>
      </div>

      <div class="ml-section">
        <h2>Studios near you</h2>
        <a class="ml-section__link" href="${D}studios_spaces/index.html">See all</a>
      </div>
      <div class="ml-rail">
        <a class="ml-studio-card" href="${D}studio_profile_discover/index.html">
          <div class="ml-studio-card__media"><img src="${IMG.studio1}" alt="" /></div>
          <div class="ml-studio-card__body">
            <div class="ml-studio-card__name">Andheri Live Room</div>
            <div class="ml-studio-card__loc">Andheri East · 1.4 km</div>
            <div class="ml-studio-card__meta"><span>★ 4.4 (52)</span><span class="ml-studio-card__price">Jam room</span></div>
          </div>
        </a>
        <a class="ml-studio-card" href="${D}studio_profile_discover/index.html">
          <div class="ml-studio-card__media"><img src="${IMG.studio2}" alt="" /></div>
          <div class="ml-studio-card__body">
            <div class="ml-studio-card__name">Blue Note Recording Co.</div>
            <div class="ml-studio-card__loc">Bandra · 3.2 km</div>
            <div class="ml-studio-card__meta"><span>★ 4.6 (94)</span><span class="ml-studio-card__price">Recording</span></div>
          </div>
        </a>
      </div>

      <div class="ml-section">
        <h2>New &amp; rising</h2>
        <a class="ml-section__link" href="${D}trending_recommended_sections/index.html">See all</a>
      </div>
      <div>
        ${result({ img: IMG.ananya, name: "Ananya Sharma", ver: true, meta: "Playback Vocalist · joined 3 weeks ago", trail: `<span class="ml-status ml-status--success">Available</span>` })}
        ${result({ img: IMG.arjun, name: "Arjun Menon", meta: "Tabla · Fusion · Kochi", trail: `<span class="ml-status ml-status--success">Available</span>` })}
      </div>
    </main>
    ${tabbar("Discover")}
  </div>` }));

/* ── 2 · Global Search ─────────────────────────────────────────────────── */

done.push(writeScreen({ flow: "discover", screen: "global_search", body: `
  <div class="ml-app">
    <header class="ml-navbar">
      ${backArrow(D + "discover_feed/index.html")}
      <div class="ml-search" style="flex:1">
        <span class="ml-search__icon">${icon("search", { size: 18 })}</span>
        <input type="search" placeholder="Search talent, studios, projects…" aria-label="Search MusicLinkd" autofocus />
      </div>
    </header>

    <main class="ml-app__body">
      <!-- The search field is the page's subject, so the heading is visually
           redundant — but a screen still needs one h1 for structure. -->
      <h1 class="ml-visually-hidden">Search MusicLinkd</h1>

      <!-- idle: recent + suggested -->
      <div data-state="idle">
        <div class="ml-section"><h2>Recent</h2></div>
        <a class="ml-suggest" href="${D}search_results/index.html">
          ${icon("clock", { size: 18 })}
          <span class="ml-suggest__body">mix engineer mumbai</span>
        </a>
        <a class="ml-suggest" href="${D}search_results/index.html">
          ${icon("clock", { size: 18 })}
          <span class="ml-suggest__body">jam room andheri</span>
        </a>

        <div class="ml-section"><h2>Try searching for</h2></div>
        <div class="ml-chip-wrap">
          ${chip("Playback vocalists")}${chip("Tabla players")}${chip("Dolby Atmos studios")}
          ${chip("Punjabi lyricists")}${chip("Mastering")}${chip("Bands in Pune")}
        </div>
      </div>

      <!-- typing: live suggestions across dimensions -->
      <div data-state="typing">
        <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-2) 0">Results for "mix"</p>
        <a class="ml-suggest" href="${D}search_results/index.html">
          ${icon("search", { size: 18 })}
          <span class="ml-suggest__body">mix engineer<span class="ml-suggest__kind">in People</span></span>
        </a>
        <a class="ml-suggest" href="${D}services/index.html">
          ${icon("search", { size: 18 })}
          <span class="ml-suggest__body">mixing &amp; mastering<span class="ml-suggest__kind">in Services</span></span>
        </a>
        <a class="ml-suggest" href="${D}discover_profile_detail/index.html">
          <img class="ml-avatar ml-avatar--sm" src="${IMG.rahul}" alt="" />
          <span class="ml-suggest__body">Rahul Iyer<span class="ml-suggest__kind">Producer · Mix Engineer</span></span>
        </a>
      </div>

      <!-- no_results -->
      <div class="ml-empty" data-state="no_results">
        <h2 class="ml-empty__title">Nothing found</h2>
        <p class="ml-empty__body">Try a different spelling, or browse by category instead.</p>
        <a class="ml-btn ml-btn--secondary" href="${D}discover_feed/index.html">Browse Discover</a>
      </div>
    </main>
  </div>` }));

/* ── 3 · Search Results ────────────────────────────────────────────────── */

done.push(writeScreen({ flow: "discover", screen: "search_results", body: `
  <div class="ml-app">
    ${navbar("mix engineer mumbai", D + "global_search/index.html", filterBtn)}
    <main class="ml-app__body">
      ${dimTabs("People")}

      <div data-state="default">
        <div class="ml-section">
          <h2>28 results</h2>
          <a class="ml-section__link" href="${D}advanced_filters/index.html">Filters</a>
        </div>
        <div class="ml-chip-wrap" style="margin-bottom:var(--space-3)">
          ${chip("Available now", true)}${chip("Verified")}${chip("Within 10 km")}${chip("★ 4.5+")}
        </div>
        <div>
          ${result({ img: IMG.rahul, name: "Rahul Iyer", ver: true, meta: "Producer · Mix Engineer · Andheri West", trail: match(94) })}
          ${result({ img: IMG.arjun, name: "Arjun Menon", meta: "Mix &amp; Master · Bandra", trail: match(87) })}
          ${result({ img: IMG.priya, name: "Priya Nair", meta: "Mix Engineer · Powai", trail: match(79) })}
          ${result({ img: IMG.mehek, name: "Mehek Kapoor", meta: "Producer · Lower Parel", trail: match(72) })}
        </div>
      </div>

      <div class="ml-empty" data-state="no_results">
        <h2 class="ml-empty__title">No matches</h2>
        <p class="ml-empty__body">No one matches all your filters. Try removing "Within 10 km".</p>
        <a class="ml-btn ml-btn--secondary" href="${D}advanced_filters/index.html">Edit filters</a>
      </div>
    </main>
    ${tabbar("Discover")}
  </div>` }));

/* ── 4 · People / Talent ───────────────────────────────────────────────── */

done.push(writeScreen({ flow: "discover", screen: "people_talent", body: `
  <div class="ml-app">
    ${navbar("People", D + "discover_feed/index.html", filterBtn)}
    <main class="ml-app__body">
      ${dimTabs("People")}
      <div class="ml-chip-wrap" style="margin:var(--space-3) 0">
        ${chip("All", true)}${chip("Singers")}${chip("Musicians")}${chip("Producers")}
        ${chip("Composers")}${chip("Engineers")}${chip("DJs")}
      </div>
      <div>
        ${result({ img: IMG.ananya, name: "Ananya Sharma", ver: true, meta: "Playback Vocalist · Mumbai · Hindi, Punjabi", trail: match(96) })}
        ${result({ img: IMG.rahul, name: "Rahul Iyer", ver: true, meta: "Producer · Mix Engineer · Mumbai", trail: match(94) })}
        ${result({ img: IMG.priya, name: "Priya Nair", meta: "Session Guitarist · Pune", trail: match(88) })}
        ${result({ img: IMG.mehek, name: "Mehek Kapoor", meta: "Composer · Delhi NCR", trail: match(81) })}
        ${result({ img: IMG.arjun, name: "Arjun Menon", meta: "Tabla · Fusion · Kochi", trail: match(74) })}
      </div>
    </main>
    ${tabbar("Discover")}
  </div>` }));

/* ── 5 · Businesses ────────────────────────────────────────────────────── */

done.push(writeScreen({ flow: "discover", screen: "businesses", body: `
  <div class="ml-app">
    ${navbar("Businesses", D + "discover_feed/index.html", filterBtn)}
    <main class="ml-app__body">
      ${dimTabs("Businesses")}
      <div class="ml-chip-wrap" style="margin:var(--space-3) 0">
        ${chip("All", true)}${chip("Studios")}${chip("Labels")}${chip("Dealers")}${chip("Agencies")}${chip("Schools")}
      </div>
      <div>
        ${result({ img: IMG.studio2, square: true, name: "Yash Raj Sound Studios", ver: true, meta: "Studio · Andheri West · Dolby Atmos", href: D + "business_profile/index.html", trail: `<span class="ml-type-1" style="color:var(--color-muted)">★ 4.9</span>` })}
        ${result({ img: IMG.desk, square: true, name: "Blue Ridge Records", meta: "Label · Bandra · Indie &amp; Fusion", href: D + "business_profile/index.html", trail: `<span class="ml-type-1" style="color:var(--color-muted)">★ 4.6</span>` })}
        ${result({ img: IMG.studio3, square: true, name: "Furtados Music", meta: "Dealer · 12 stores · Instruments &amp; gear", href: D + "business_profile/index.html", trail: `<span class="ml-type-1" style="color:var(--color-muted)">★ 4.4</span>` })}
        ${result({ img: IMG.mic, square: true, name: "True School of Music", meta: "School · Lower Parel · Production &amp; performance", href: D + "business_profile/index.html", trail: `<span class="ml-type-1" style="color:var(--color-muted)">★ 4.7</span>` })}
      </div>
    </main>
    ${tabbar("Discover")}
  </div>` }));

/* ── 6 · Studios & Spaces ──────────────────────────────────────────────── */

done.push(writeScreen({ flow: "discover", screen: "studios_spaces", body: `
  <div class="ml-app">
    ${navbar("Spaces", D + "discover_feed/index.html", filterBtn)}
    <main class="ml-app__body">
      ${dimTabs("Spaces")}
      <div class="ml-chip-wrap" style="margin:var(--space-3) 0">
        ${chip("All", true)}${chip("Recording studios")}${chip("Jam rooms")}${chip("Venues")}
      </div>
      <div style="display:flex;flex-direction:column;gap:var(--space-3)">
        <a class="ml-card" href="${D}studio_profile_discover/index.html">
          <div class="ml-media ml-media--wide"><img src="${IMG.studio1}" alt="" /></div>
          <div style="padding:var(--space-3)">
            <div class="ml-type-3" style="font-weight:700">Andheri Live Room</div>
            <div class="ml-type-1" style="color:var(--color-muted)">Jam room · Andheri East · 1.4 km away</div>
            <div style="display:flex;gap:var(--space-2);margin-top:var(--space-2)">
              <span class="ml-badge">★ 4.4 (52)</span><span class="ml-badge">Drum kit</span><span class="ml-badge">PA</span>
            </div>
          </div>
        </a>
        <a class="ml-card" href="${D}studio_profile_discover/index.html">
          <div class="ml-media ml-media--wide"><img src="${IMG.studio2}" alt="" /></div>
          <div style="padding:var(--space-3)">
            <div class="ml-type-3" style="font-weight:700">Blue Note Recording Co.</div>
            <div class="ml-type-1" style="color:var(--color-muted)">Recording studio · Bandra · 3.2 km away</div>
            <div style="display:flex;gap:var(--space-2);margin-top:var(--space-2)">
              <span class="ml-badge">★ 4.6 (94)</span><span class="ml-badge">Neve desk</span><span class="ml-badge">Vocal booth</span>
            </div>
          </div>
        </a>
      </div>
    </main>
    ${tabbar("Discover")}
  </div>` }));

/* ── 7 · Services ──────────────────────────────────────────────────────── */
/* The provider is the subject; the rate is a qualifier. Inverting those two
   turns a Discover card into a Market listing. */

const service = (title, by, img, rate, turnaround) => `<a class="ml-service" href="${D}service_detail/index.html">
          <span class="ml-service__head">
            <img class="ml-avatar ml-avatar--sm" src="${img}" alt="" />
            <span>
              <span class="ml-service__title">${title}</span>
              <span class="ml-service__by">by ${by}</span>
            </span>
          </span>
          <span class="ml-service__foot">
            <span style="color:var(--color-muted)">${turnaround}</span>
            <span class="ml-service__rate">${rate}</span>
          </span>
        </a>`;

done.push(writeScreen({ flow: "discover", screen: "services", body: `
  <div class="ml-app">
    ${navbar("Services", D + "discover_feed/index.html", filterBtn)}
    <main class="ml-app__body">
      ${dimTabs("Services")}
      <div class="ml-chip-wrap" style="margin:var(--space-3) 0">
        ${chip("All", true)}${chip("Production")}${chip("Mixing")}${chip("Mastering")}${chip("Session work")}
      </div>
      <div style="display:flex;flex-direction:column;gap:var(--space-2)">
        ${service("Mixing &amp; mastering for indie releases", "Rahul Iyer", IMG.rahul, "from ₹12,000", "5–7 days")}
        ${service("Session guitar — acoustic &amp; electric", "Priya Nair", IMG.priya, "₹4,000/track", "2–3 days")}
        ${service("Full production &amp; arrangement", "Mehek Kapoor", IMG.mehek, "from ₹35,000", "3–4 weeks")}
        ${service("Playback &amp; backing vocals", "Ananya Sharma", IMG.ananya, "₹6,000/song", "1 week")}
      </div>
    </main>
    ${tabbar("Discover")}
  </div>` }));

/* ── 8 · Projects / Portfolio ──────────────────────────────────────────── */

done.push(writeScreen({ flow: "discover", screen: "projects", body: `
  <div class="ml-app">
    ${navbar("Projects", D + "discover_feed/index.html", filterBtn)}
    <main class="ml-app__body">
      ${dimTabs("Projects")}
      <div class="ml-chip-wrap" style="margin:var(--space-3) 0">
        ${chip("All", true)}${chip("Open roles")}${chip("Songs")}${chip("Albums")}${chip("Film scores")}${chip("Live")}
      </div>
      <div style="display:flex;flex-direction:column;gap:var(--space-3)">
        <a class="ml-card" href="${D}project_detail/index.html">
          <div class="ml-media ml-media--wide ml-media--scrim">
            <img src="${IMG.guitar}" alt="" />
            <span class="ml-media__tag"><span class="ml-status ml-status--accent">2 open roles</span></span>
          </div>
          <div style="padding:var(--space-3)">
            <div class="ml-type-3" style="font-weight:700">Monsoon Sessions</div>
            <div class="ml-type-1" style="color:var(--color-muted)">Indie EP · Mumbai · led by Mehek Kapoor</div>
            <p class="ml-type-3" style="margin:var(--space-2) 0 0">Five-track acoustic EP. Looking for a bassist and a mix engineer.</p>
            <div class="ml-facepile">
              <img src="${IMG.mehek}" alt="" /><img src="${IMG.ananya}" alt="" /><img src="${IMG.arjun}" alt="" />
            </div>
          </div>
        </a>
        <a class="ml-card" href="${D}project_detail/index.html">
          <div class="ml-media ml-media--wide"><img src="${IMG.crowd}" alt="" /></div>
          <div style="padding:var(--space-3)">
            <div class="ml-type-3" style="font-weight:700">Tum Hi Toh (Unplugged)</div>
            <div class="ml-type-1" style="color:var(--color-muted)">Song · Released 14 Feb 2026 · 4.2M plays</div>
          </div>
        </a>
      </div>
    </main>
    ${tabbar("Discover")}
  </div>` }));

/* ── 9 · Music / Audio ─────────────────────────────────────────────────── */

const track = (title, artist, art, dur, playing = false) => `<div class="ml-result">
          <img class="ml-result__thumb ml-result__thumb--square" src="${art}" alt="" />
          <span class="ml-result__body">
            <span class="ml-result__name">${title}</span>
            <span class="ml-result__meta">${artist}</span>
          </span>
          <span class="ml-result__trail" style="display:flex;align-items:center;gap:var(--space-2)">
            <span class="ml-type-1" style="color:var(--color-muted)">${dur}</span>
            <button class="ml-player__btn" aria-label="${playing ? "Pause" : "Play"} ${title}" style="width:var(--size-3);height:var(--size-3)">
              ${playing ? icon("pause", { size: 16 }) : icon("play", { size: 16 })}
            </button>
          </span>
        </div>`;

done.push(writeScreen({ flow: "discover", screen: "music_audio", body: `
  <div class="ml-app">
    ${navbar("Music", D + "discover_feed/index.html", filterBtn)}
    <main class="ml-app__body">
      ${dimTabs("Music")}
      <div class="ml-chip-wrap" style="margin:var(--space-3) 0">
        ${chip("Tracks", true)}${chip("Videos")}${chip("Projects")}
      </div>
      <div>
        ${track("Tum Hi Toh (Unplugged)", "Ananya Sharma · Playback", IMG.guitar, "3:42", true)}
        ${track("Monsoon (demo)", "Mehek Kapoor · Indie", IMG.piano, "4:10")}
        ${track("Raag Bhairav — Fusion", "Arjun Menon · Fusion", IMG.mic, "6:28")}
        ${track("Bandra Nights", "Rahul Iyer · Electronic", IMG.desk, "3:05")}
      </div>
    </main>

    <!-- playing: the persistent mini-player sits above the tab bar -->
    <div class="ml-miniplayer" data-state="playing">
      <img class="ml-miniplayer__art" src="${IMG.guitar}" alt="" />
      <div class="ml-miniplayer__body">
        <div class="ml-miniplayer__title">Tum Hi Toh (Unplugged)</div>
        <div class="ml-miniplayer__sub">Ananya Sharma</div>
      </div>
      <button class="ml-icon-btn ml-icon-btn--bare" aria-label="Pause">
        ${icon("pause", { size: 20 })}
      </button>
    </div>
    ${tabbar("Discover")}
  </div>` }));

/* ── 10 · Communities ──────────────────────────────────────────────────── */

const community = (name, meta, img, faces) => `<a class="ml-community" href="${D}community_detail/index.html">
          <div class="ml-community__media"><img src="${img}" alt="" /></div>
          <div class="ml-community__body">
            <div class="ml-community__name">${name}</div>
            <div class="ml-community__meta">${meta}</div>
            <div class="ml-facepile">${faces.map((f) => `<img src="${f}" alt="" />`).join("")}</div>
          </div>
        </a>`;

done.push(writeScreen({ flow: "discover", screen: "communities", body: `
  <div class="ml-app">
    ${navbar("Communities", D + "discover_feed/index.html", filterBtn)}
    <main class="ml-app__body">
      ${dimTabs("Communities")}
      <div class="ml-chip-wrap" style="margin:var(--space-3) 0">
        ${chip("All", true)}${chip("By genre")}${chip("By language")}${chip("By region")}
      </div>

      <div class="ml-section"><h2>By genre</h2></div>
      <div class="ml-rail">
        ${community("Indie India", "4,200 members · Indie &amp; alt", IMG.band, [IMG.mehek, IMG.priya, IMG.arjun])}
        ${community("Hindustani Classical", "1,860 members", IMG.mic, [IMG.arjun, IMG.ananya])}
      </div>

      <div class="ml-section"><h2>By language</h2></div>
      <div class="ml-rail">
        ${community("Punjabi Music Collective", "2,740 members", IMG.crowd, [IMG.rahul, IMG.priya])}
        ${community("Tamil Independent", "1,120 members", IMG.piano, [IMG.mehek, IMG.arjun])}
      </div>

      <div class="ml-section"><h2>By region</h2></div>
      <div class="ml-rail">
        ${community("Mumbai Session Players", "3,410 members", IMG.studio1, [IMG.rahul, IMG.ananya, IMG.priya])}
        ${community("Delhi NCR Live Circuit", "2,050 members", IMG.studio3, [IMG.mehek, IMG.arjun])}
      </div>
    </main>
    ${tabbar("Discover")}
  </div>` }));

/* ── 11 · Opportunities (Discover view) ────────────────────────────────── */
/* Distinct from flow 8's Opportunities, which is the user's own applications
   and postings. This is the browse surface. */

done.push(writeScreen({ flow: "discover", screen: "discover_opportunities", body: `
  <div class="ml-app">
    ${navbar("Opportunities", D + "discover_feed/index.html", filterBtn)}
    <main class="ml-app__body">
      ${dimTabs("Opportunities")}
      <div class="ml-chip-wrap" style="margin:var(--space-3) 0">
        ${chip("All", true)}${chip("Paid")}${chip("Collab")}${chip("Auditions")}${chip("Remote")}
      </div>
      <div style="display:flex;flex-direction:column;gap:var(--space-2)">
        <a class="ml-card ml-card--padded" href="/preview/flows/opportunities/opportunity_detail/index.html">
          <div style="display:flex;justify-content:space-between;gap:var(--space-2)">
            <div class="ml-type-3" style="font-weight:700">Bassist for a 5-track indie EP</div>
            <span class="ml-status ml-status--success">Paid</span>
          </div>
          <div class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-1)">Monsoon Sessions · Mumbai · closes in 6 days</div>
          <p class="ml-type-3" style="margin:var(--space-2) 0 0">Studio dates in March. Fretless preferred, not required.</p>
        </a>
        <a class="ml-card ml-card--padded" href="/preview/flows/opportunities/opportunity_detail/index.html">
          <div style="display:flex;justify-content:space-between;gap:var(--space-2)">
            <div class="ml-type-3" style="font-weight:700">Playback vocalist — Punjabi film</div>
            <span class="ml-status ml-status--warning">Audition</span>
          </div>
          <div class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-1)">Blue Ridge Records · Mumbai · closes in 12 days</div>
        </a>
        <a class="ml-card ml-card--padded" href="/preview/flows/opportunities/opportunity_detail/index.html">
          <div style="display:flex;justify-content:space-between;gap:var(--space-2)">
            <div class="ml-type-3" style="font-weight:700">Mix engineer for a fusion album</div>
            <span class="ml-status ml-status--neutral">Collab</span>
          </div>
          <div class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-1)">Arjun Menon · Remote · closes in 3 days</div>
        </a>
      </div>
    </main>
    ${tabbar("Discover")}
  </div>` }));

/* ── 12 · Nearby ───────────────────────────────────────────────────────── */

done.push(writeScreen({ flow: "discover", screen: "nearby", body: `
  <div class="ml-app">
    ${navbar("Nearby", D + "discover_feed/index.html", filterBtn)}
    <main class="ml-app__body">
      <div data-state="default">
        <div class="ml-banner ml-banner--info" style="margin:var(--space-3) 0">
          ${icon("location", { size: 16 })}
          Andheri West, Mumbai · within 10 km
        </div>
        <div class="ml-chip-wrap" style="margin-bottom:var(--space-3)">
          ${chip("People", true)}${chip("Studios")}${chip("Jam rooms")}${chip("Venues")}
        </div>
        <div>
          ${result({ img: IMG.rahul, name: "Rahul Iyer", ver: true, meta: "Producer · Mix Engineer", trail: `<span class="ml-type-1" style="color:var(--color-muted)">0.8 km</span>` })}
          ${result({ img: IMG.studio1, square: true, name: "Andheri Live Room", meta: "Jam room · ★ 4.4", href: D + "studio_profile_discover/index.html", trail: `<span class="ml-type-1" style="color:var(--color-muted)">1.4 km</span>` })}
          ${result({ img: IMG.priya, name: "Priya Nair", meta: "Session Guitarist", trail: `<span class="ml-type-1" style="color:var(--color-muted)">2.1 km</span>` })}
          ${result({ img: IMG.studio2, square: true, name: "Blue Note Recording Co.", meta: "Recording studio · ★ 4.6", href: D + "studio_profile_discover/index.html", trail: `<span class="ml-type-1" style="color:var(--color-muted)">3.2 km</span>` })}
        </div>
      </div>

      <div class="ml-empty" data-state="permission_denied">
        <h2 class="ml-empty__title">Location is off</h2>
        <p class="ml-empty__body">Turn on location to find musicians, studios and jam rooms around you. You can also set a city manually.</p>
        <a class="ml-btn ml-btn--primary" href="${D}nearby/index.html?state=default">Enable location</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" href="${D}discover_feed/index.html" style="margin-top:var(--space-2)">Choose a city instead</a>
      </div>
    </main>
    ${tabbar("Discover")}
  </div>` }));

/* ── 13 · Filters ──────────────────────────────────────────────────────── */

const filterGroup = (label, chips) => `<div>
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>${label}</h2></div>
        <div class="ml-chip-wrap">${chips}</div>
      </div>`;

done.push(writeScreen({ flow: "discover", screen: "advanced_filters", title: "Filters", body: `
  <div class="ml-app">
    <header class="ml-navbar">
      ${backArrow(D + "search_results/index.html", "Close")}
      <h1 class="ml-navbar__title">Filters</h1>
      <span class="ml-navbar__spacer"></span>
      <a class="ml-btn ml-btn--ghost" href="${D}advanced_filters/index.html?state=default">Reset</a>
    </header>
    <main class="ml-app__body">
      ${filterGroup("Discovery type", `${chip("People", true)}${chip("Businesses")}${chip("Spaces")}${chip("Music")}${chip("Services")}${chip("Communities")}`)}
      ${filterGroup("Role", `${chip("Singer")}${chip("Musician")}${chip("Producer", true)}${chip("Composer")}${chip("Engineer", true)}${chip("DJ")}`)}
      ${filterGroup("Genre", `${chip("Bollywood / Filmi", true)}${chip("Indie Pop")}${chip("Hindustani Classical")}${chip("Carnatic")}${chip("Punjabi Folk")}${chip("Sufi")}${chip("Electronic")}`)}
      ${filterGroup("Language", `${chip("Hindi", true)}${chip("English", true)}${chip("Punjabi")}${chip("Tamil")}${chip("Telugu")}${chip("Bengali")}${chip("Marathi")}`)}
      ${filterGroup("Availability", `${chip("Available now", true)}${chip("Selective")}${chip("Open to collab")}${chip("Remote OK")}`)}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Distance</h2></div>
      <div class="ml-field">
        <label for="dist">Within 10 km of Andheri West</label>
        <input id="dist" type="range" min="1" max="50" value="10" style="min-height:var(--size-3);padding:0" />
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Rating</h2></div>
      <div class="ml-chip-wrap">${chip("Any", true)}${chip("★ 4.0+")}${chip("★ 4.5+")}${chip("Verified only")}</div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="${D}search_results/index.html">Show 28 results</a>
    </footer>
  </div>` }));

/* ── 14 · Person Profile ───────────────────────────────────────────────── */
/* The end of the discovery journey: listen, then connect. */

done.push(writeScreen({ flow: "discover", screen: "discover_profile_detail", title: "Person Profile", body: `
  <div class="ml-app">
    <main class="ml-app__body" style="padding-inline:0">
      <div class="ml-hero">
        <img src="${IMG.mic}" alt="" />
        <div style="position:absolute;z-index:1;top:calc(var(--space-2) + var(--safe-top));left:var(--space-2);right:var(--space-2);display:flex;justify-content:space-between">
          ${backArrow(D + "people_talent/index.html")}
          <button class="ml-icon-btn" aria-label="Save to your list">
            ${icon("pin", { size: 20 })}
          </button>
        </div>
        <div class="ml-hero__body">
          <span class="ml-status ml-status--success">Available for work</span>
          <h1 class="ml-hero__name" style="margin-top:var(--space-2)">Rahul Iyer</h1>
          <div class="ml-hero__sub">Producer · Mix Engineer · Andheri West, Mumbai</div>
          <div class="ml-hero__stats">
            <span><b>8.1k</b> followers</span><span><b>★ 4.8</b> (126)</span><span><b>94%</b> match</span>
          </div>
        </div>
      </div>

      <div style="padding:var(--space-3)">
        <div class="ml-action-row" style="padding-top:0">
          <a class="ml-btn ml-btn--primary" href="/preview/flows/communication/chat_conversation/index.html">Message</a>
          <button class="ml-btn ml-btn--secondary">Follow</button>
          <a class="ml-btn ml-btn--secondary" href="${D}service_detail/index.html">Hire</a>
        </div>

        <p class="ml-type-3" style="margin:0 0 var(--space-3)">Twelve years mixing Hindi and Punjabi film music. Neve desk, Dolby Atmos certified. Happiest on a fast turnaround.</p>

        <div class="ml-chip-wrap">
          ${chip("Bollywood / Filmi", true)}${chip("Sufi", true)}${chip("Hindi", true)}${chip("Punjabi", true)}
        </div>

        <div class="ml-section"><h2>Listen</h2></div>
        <div class="ml-player ml-card" style="padding:var(--space-2) var(--space-3)">
          <button class="ml-player__btn" aria-label="Play showreel">
            ${icon("play", { size: 18 })}
          </button>
          <div class="ml-player__wave" role="slider" aria-label="Seek" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0" tabindex="0">
            ${Array.from({ length: 40 }, (_, i) => `<span class="ml-player__bar${i < 9 ? " is-played" : ""}" style="height:${18 + ((i * 31) % 64)}%"></span>`).join("")}
          </div>
          <span class="ml-player__time">2:04</span>
        </div>

        <div class="ml-section">
          <h2>Work</h2>
          <a class="ml-section__link" href="${D}projects/index.html">See all</a>
        </div>
        <div class="ml-rail">
          <a class="ml-media-card" href="${D}project_detail/index.html">
            <img src="${IMG.guitar}" alt="" />
            <div class="ml-media-card__body">
              <div class="ml-media-card__title">Tum Hi Toh</div>
              <div class="ml-media-card__sub">Mix · 2026</div>
            </div>
          </a>
          <a class="ml-media-card" href="${D}project_detail/index.html">
            <img src="${IMG.piano}" alt="" />
            <div class="ml-media-card__body">
              <div class="ml-media-card__title">Monsoon Sessions</div>
              <div class="ml-media-card__sub">Producer · 2025</div>
            </div>
          </a>
        </div>

        <div class="ml-section"><h2>Services</h2></div>
        <div style="display:flex;flex-direction:column;gap:var(--space-2)">
          ${service("Mixing &amp; mastering for indie releases", "Rahul Iyer", IMG.rahul, "from ₹12,000", "5–7 days")}
        </div>
      </div>
    </main>
  </div>` }));

/* ── 15 · Business Profile ─────────────────────────────────────────────── */

done.push(writeScreen({ flow: "discover", screen: "business_profile", body: `
  <div class="ml-app">
    <main class="ml-app__body" style="padding-inline:0">
      <div class="ml-hero">
        <img src="${IMG.studio2}" alt="" />
        <div style="position:absolute;z-index:1;top:calc(var(--space-2) + var(--safe-top));left:var(--space-2)">
          ${backArrow(D + "businesses/index.html")}
        </div>
        <div class="ml-hero__body">
          <span class="ml-status ml-status--accent">Studio · Verified</span>
          <h1 class="ml-hero__name" style="margin-top:var(--space-2)">Yash Raj Sound Studios</h1>
          <div class="ml-hero__sub">Andheri West, Mumbai</div>
          <div class="ml-hero__stats"><span><b>★ 4.9</b> (188)</span><span><b>Est.</b> 2004</span></div>
        </div>
      </div>

      <div style="padding:var(--space-3)">
        <div class="ml-action-row" style="padding-top:0">
          <a class="ml-btn ml-btn--primary" href="/preview/flows/communication/chat_conversation/index.html">Enquire</a>
          <button class="ml-btn ml-btn--secondary">Follow</button>
        </div>

        <p class="ml-type-3" style="margin:0 0 var(--space-3)">Four rooms including a Dolby Atmos mix stage. In-house engineers, Steinway D, full backline.</p>

        <div class="ml-section"><h2>Rooms</h2></div>
        <div class="ml-rail">
          <a class="ml-studio-card" href="${D}studio_profile_discover/index.html">
            <div class="ml-studio-card__media"><img src="${IMG.studio1}" alt="" /></div>
            <div class="ml-studio-card__body">
              <div class="ml-studio-card__name">Studio A — Atmos</div>
              <div class="ml-studio-card__loc">Mix stage · 7.1.4</div>
            </div>
          </a>
          <a class="ml-studio-card" href="${D}studio_profile_discover/index.html">
            <div class="ml-studio-card__media"><img src="${IMG.studio3}" alt="" /></div>
            <div class="ml-studio-card__body">
              <div class="ml-studio-card__name">Studio B — Live</div>
              <div class="ml-studio-card__loc">Tracking · 12 inputs</div>
            </div>
          </a>
        </div>

        <div class="ml-section"><h2>Team</h2></div>
        <div>
          ${result({ img: IMG.rahul, name: "Rahul Iyer", ver: true, meta: "Chief Mix Engineer" })}
          ${result({ img: IMG.priya, name: "Priya Nair", meta: "Studio Manager" })}
        </div>

        <div class="ml-section"><h2>Services</h2></div>
        <div style="display:flex;flex-direction:column;gap:var(--space-2)">
          ${service("Dolby Atmos mixing", "Yash Raj Sound", IMG.studio2, "from ₹45,000", "per track")}
        </div>
      </div>
    </main>
  </div>` }));

/* ── 16 · Studio Profile ───────────────────────────────────────────────── */
/* Discover's view of a space. Booking itself is flow 5 — this hands off. */

done.push(writeScreen({ flow: "discover", screen: "studio_profile_discover", title: "Studio Profile", body: `
  <div class="ml-app">
    <main class="ml-app__body" style="padding-inline:0">
      <div class="ml-hero">
        <img src="${IMG.studio1}" alt="" />
        <div style="position:absolute;z-index:1;top:calc(var(--space-2) + var(--safe-top));left:var(--space-2)">
          ${backArrow(D + "studios_spaces/index.html")}
        </div>
        <div class="ml-hero__body">
          <span class="ml-status ml-status--success">Available today</span>
          <h1 class="ml-hero__name" style="margin-top:var(--space-2)">Andheri Live Room</h1>
          <div class="ml-hero__sub">Jam room · Andheri East · 1.4 km away</div>
          <div class="ml-hero__stats"><span><b>★ 4.4</b> (52)</span><span><b>₹3,200</b>/hr</span></div>
        </div>
      </div>

      <div style="padding:var(--space-3)">
        <div class="ml-action-row" style="padding-top:0">
          <a class="ml-btn ml-btn--primary" href="/preview/flows/booking/availability_calendar/index.html">Check availability</a>
          <button class="ml-btn ml-btn--secondary">Save</button>
        </div>

        <p class="ml-type-3" style="margin:0 0 var(--space-3)">Acoustically treated 320 sq ft room. Walk-ins welcome off-peak.</p>

        <div class="ml-section"><h2>Gear</h2></div>
        <div class="ml-chip-wrap">
          ${chip("Pearl drum kit")}${chip("Ampeg bass rig")}${chip("2× Marshall")}${chip("PA + 4 monitors")}${chip("Shure SM58 ×4")}
        </div>

        <div class="ml-section"><h2>Photos</h2></div>
        <div class="ml-rail">
          <img class="ml-card" src="${IMG.studio1}" alt="Live room" style="width:220px;height:140px;object-fit:cover" />
          <img class="ml-card" src="${IMG.studio3}" alt="Control desk" style="width:220px;height:140px;object-fit:cover" />
          <img class="ml-card" src="${IMG.desk}" alt="Booth" style="width:220px;height:140px;object-fit:cover" />
        </div>

        <div class="ml-section"><h2>Reviews</h2></div>
        <div class="ml-card ml-card--padded">
          <div class="ml-review-card__head">
            <img class="ml-avatar ml-avatar--sm" src="${IMG.mehek}" alt="" />
            <span><span class="ml-row__title">Mehek Kapoor</span><span class="ml-review-card__stars">★★★★★</span></span>
          </div>
          <p class="ml-review-card__body" style="margin-bottom:0">Great room for a five-piece. Booked it twice in a month.</p>
        </div>
      </div>
    </main>
  </div>` }));

/* ── 17 · Project Detail ───────────────────────────────────────────────── */

done.push(writeScreen({ flow: "discover", screen: "project_detail", body: `
  <div class="ml-app">
    <main class="ml-app__body" style="padding-inline:0">
      <div class="ml-hero">
        <img src="${IMG.guitar}" alt="" />
        <div style="position:absolute;z-index:1;top:calc(var(--space-2) + var(--safe-top));left:var(--space-2)">
          ${backArrow(D + "projects/index.html")}
        </div>
        <div class="ml-hero__body">
          <span class="ml-status ml-status--accent">2 open roles</span>
          <h1 class="ml-hero__name" style="margin-top:var(--space-2)">Monsoon Sessions</h1>
          <div class="ml-hero__sub">Indie EP · Mumbai · led by Mehek Kapoor</div>
        </div>
      </div>

      <div style="padding:var(--space-3)">
        <div class="ml-action-row" style="padding-top:0">
          <a class="ml-btn ml-btn--primary" href="/preview/flows/opportunities/opportunity_apply/index.html">Join collaboration</a>
          <button class="ml-btn ml-btn--secondary">Follow</button>
        </div>

        <p class="ml-type-3" style="margin:0 0 var(--space-3)">A five-track acoustic EP recorded across three Mumbai rooms this monsoon. Two roles still open.</p>

        <div class="ml-section"><h2>Open roles</h2></div>
        <div style="display:flex;flex-direction:column;gap:var(--space-2)">
          <a class="ml-card ml-card--padded" href="/preview/flows/opportunities/opportunity_detail/index.html">
            <div style="display:flex;justify-content:space-between;gap:var(--space-2)">
              <span class="ml-row__title">Bassist</span><span class="ml-status ml-status--success">Paid</span>
            </div>
            <span class="ml-row__meta">Studio dates in March · fretless preferred</span>
          </a>
          <a class="ml-card ml-card--padded" href="/preview/flows/opportunities/opportunity_detail/index.html">
            <div style="display:flex;justify-content:space-between;gap:var(--space-2)">
              <span class="ml-row__title">Mix engineer</span><span class="ml-status ml-status--neutral">Collab</span>
            </div>
            <span class="ml-row__meta">Remote OK · 5 tracks</span>
          </a>
        </div>

        <div class="ml-section"><h2>Team</h2></div>
        <div>
          ${result({ img: IMG.mehek, name: "Mehek Kapoor", meta: "Composer · project lead" })}
          ${result({ img: IMG.ananya, name: "Ananya Sharma", ver: true, meta: "Playback Vocalist" })}
          ${result({ img: IMG.arjun, name: "Arjun Menon", meta: "Percussion" })}
        </div>

        <div class="ml-section"><h2>Demo</h2></div>
        <div class="ml-player ml-card" style="padding:var(--space-2) var(--space-3)">
          <button class="ml-player__btn" aria-label="Play demo">
            ${icon("play", { size: 18 })}
          </button>
          <div class="ml-player__wave" role="slider" aria-label="Seek" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0" tabindex="0">
            ${Array.from({ length: 40 }, (_, i) => `<span class="ml-player__bar${i < 6 ? " is-played" : ""}" style="height:${22 + ((i * 43) % 58)}%"></span>`).join("")}
          </div>
          <span class="ml-player__time">1:48</span>
        </div>
      </div>
    </main>
  </div>` }));

/* ── 18 · Service Detail ───────────────────────────────────────────────── */

done.push(writeScreen({ flow: "discover", screen: "service_detail", body: `
  <div class="ml-app">
    <header class="ml-navbar">${backArrow(D + "services/index.html")}</header>
    <main class="ml-app__body">
      <div class="ml-card ml-card--padded" style="margin-top:var(--space-3)">
        <div style="display:flex;align-items:center;gap:var(--space-3)">
          <img class="ml-avatar ml-avatar--lg" src="${IMG.rahul}" alt="" />
          <div>
            <h1 class="ml-type-4" style="margin:0">Mixing &amp; mastering</h1>
            <a class="ml-row__meta" href="${D}discover_profile_detail/index.html" style="color:var(--color-pink-pressed);font-weight:600;text-decoration:none">by Rahul Iyer</a>
          </div>
        </div>
      </div>

      <div class="ml-stat-grid" style="grid-template-columns:repeat(3,1fr);margin-top:var(--space-3)">
        <div class="ml-stat"><span class="ml-stat__num">₹12k</span><span class="ml-stat__label">From</span></div>
        <div class="ml-stat"><span class="ml-stat__num">5–7</span><span class="ml-stat__label">Days</span></div>
        <div class="ml-stat"><span class="ml-stat__num">★ 4.8</span><span class="ml-stat__label">126 reviews</span></div>
      </div>

      <div class="ml-section"><h2>What's included</h2></div>
      <div>
        <div class="ml-row"><span class="ml-row__body"><span class="ml-row__title">Stem mixing</span><span class="ml-row__meta">Up to 48 tracks</span></span></div>
        <div class="ml-row"><span class="ml-row__body"><span class="ml-row__title">Two revision rounds</span><span class="ml-row__meta">Within 14 days of delivery</span></span></div>
        <div class="ml-row"><span class="ml-row__body"><span class="ml-row__title">Master for streaming</span><span class="ml-row__meta">-14 LUFS, plus a CD master</span></span></div>
      </div>

      <div class="ml-section"><h2>Before / after</h2></div>
      <div class="ml-player ml-card" style="padding:var(--space-2) var(--space-3)">
        <button class="ml-player__btn" aria-label="Play sample">
          ${icon("play", { size: 18 })}
        </button>
        <div class="ml-player__wave" role="slider" aria-label="Seek" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0" tabindex="0">
          ${Array.from({ length: 40 }, (_, i) => `<span class="ml-player__bar${i < 20 ? " is-played" : ""}" style="height:${20 + ((i * 29) % 62)}%"></span>`).join("")}
        </div>
        <span class="ml-player__time">0:48</span>
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="/preview/flows/communication/chat_conversation/index.html">Enquire about this service</a>
    </footer>
  </div>` }));

/* ── 19 · Community Detail ─────────────────────────────────────────────── */

done.push(writeScreen({ flow: "discover", screen: "community_detail", body: `
  <div class="ml-app">
    <main class="ml-app__body" style="padding-inline:0">
      <div class="ml-hero">
        <img src="${IMG.band}" alt="" />
        <div style="position:absolute;z-index:1;top:calc(var(--space-2) + var(--safe-top));left:var(--space-2)">
          ${backArrow(D + "communities/index.html")}
        </div>
        <div class="ml-hero__body">
          <h1 class="ml-hero__name">Indie India</h1>
          <div class="ml-hero__sub">4,200 members · Indie &amp; alternative</div>
        </div>
      </div>

      <div style="padding:var(--space-3)">
        <div class="ml-action-row" style="padding-top:0">
          <button class="ml-btn ml-btn--primary" data-state="default">Join community</button>
          <span class="ml-btn ml-btn--secondary" data-state="joined">Joined ✓</span>
          <button class="ml-btn ml-btn--secondary">Share</button>
        </div>

        <div class="ml-banner ml-banner--success" data-state="joined" style="margin-bottom:var(--space-3)">
          You're in. New posts from Indie India will appear in Connections.
        </div>

        <p class="ml-type-3" style="margin:0 0 var(--space-3)">For independent artists across India — releases, gig swaps, and honest feedback on works in progress.</p>

        <div class="ml-section"><h2>Members</h2></div>
        <div>
          ${result({ img: IMG.mehek, name: "Mehek Kapoor", meta: "Composer · Delhi NCR" })}
          ${result({ img: IMG.priya, name: "Priya Nair", meta: "Session Guitarist · Pune" })}
          ${result({ img: IMG.arjun, name: "Arjun Menon", meta: "Tabla · Fusion · Kochi" })}
        </div>

        <div class="ml-section">
          <h2>Projects from this community</h2>
          <a class="ml-section__link" href="${D}projects/index.html">See all</a>
        </div>
        <div class="ml-rail">
          <a class="ml-media-card" href="${D}project_detail/index.html">
            <img src="${IMG.guitar}" alt="" />
            <div class="ml-media-card__body">
              <div class="ml-media-card__title">Monsoon Sessions</div>
              <div class="ml-media-card__sub">Indie EP · 2 open roles</div>
            </div>
          </a>
        </div>
      </div>
    </main>
  </div>` }));

/* ── 20 · Saved / Following ────────────────────────────────────────────── */

done.push(writeScreen({ flow: "discover", screen: "saved_profiles", title: "Saved / Following", body: `
  <div class="ml-app">
    ${navbar("Saved", D + "discover_feed/index.html")}
    <main class="ml-app__body">
      <div data-state="populated">
        <nav class="ml-tabs" aria-label="Saved type" style="margin-top:0">
          <button class="ml-tabs__tab" aria-selected="true">People</button>
          <button class="ml-tabs__tab" aria-selected="false">Spaces</button>
          <button class="ml-tabs__tab" aria-selected="false">Projects</button>
          <button class="ml-tabs__tab" aria-selected="false">Communities</button>
        </nav>
        <div style="margin-top:var(--space-3)">
          ${result({ img: IMG.rahul, name: "Rahul Iyer", ver: true, meta: "Producer · Mix Engineer · Mumbai", trail: `<span class="ml-status ml-status--success">Available</span>` })}
          ${result({ img: IMG.ananya, name: "Ananya Sharma", ver: true, meta: "Playback Vocalist · Mumbai", trail: `<span class="ml-status ml-status--warning">Selective</span>` })}
          ${result({ img: IMG.priya, name: "Priya Nair", meta: "Session Guitarist · Pune", trail: `<span class="ml-status ml-status--success">Available</span>` })}
        </div>
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">Nothing saved yet</h2>
        <p class="ml-empty__body">Tap the bookmark on any profile, studio or project to keep it here.</p>
        <a class="ml-btn ml-btn--primary" href="${D}discover_feed/index.html">Start discovering</a>
      </div>
    </main>
    ${tabbar("Discover")}
  </div>` }));

/* ── 21 · Trending & Recommended (also the Home destination) ───────────── */

/* The declared state is "hidden": a registry note that these composables still
   exist in the Android source but their call sites are commented out —
   confirmed hidden, not deleted. It is a fact about the app, not a state this
   screen enters, so it is marked on the content rather than renamed away.
   Same treatment as band/upcoming_shows' "hardcoded". */
done.push(writeScreen({ flow: "discover", screen: "trending_recommended_sections", title: "Trending & Recommended", body: `
  <div class="ml-app" data-state="hidden">
    <header class="ml-appbar">
      <div class="ml-appbar__titles">
        <div class="ml-eyebrow">MusicLinkd</div>
        <h1 class="ml-appbar__title">Home</h1>
      </div>
      <div class="ml-appbar__actions">
        <a class="ml-icon-btn" href="/preview/flows/notifications/notifications_center/index.html" aria-label="Notifications">
          ${icon("bell", { size: 20 })}
        </a>
        <a class="ml-avatar-btn" href="/preview/flows/profile_management/profile_overview/index.html" aria-label="Your profile">
          <img class="ml-avatar" src="${IMG.arjun}" alt="" />
        </a>
      </div>
    </header>
    <main class="ml-app__body">
      <p class="ml-type-3" style="color:var(--color-muted);margin:0 0 var(--space-3)">What's happening in your world.</p>
      <div class="ml-banner ml-banner--info" data-state="empty">Follow a few artists and studios and this feed fills up.</div>

      <div class="ml-progress">
        <div class="ml-progress__title">Your profile is 80% complete</div>
        <div class="ml-progress__meta">Add streaming links to finish</div>
        <div class="ml-progress__track"><span class="ml-progress__fill" style="width:80%"></span></div>
        <a class="ml-section__link" href="/preview/flows/onboarding/networking_links/index.html" style="margin:0">Finish setup</a>
      </div>

      <div class="ml-section">
        <h2>Trending this week</h2>
        <a class="ml-section__link" href="${D}projects/index.html">See all</a>
      </div>
      <div class="ml-rail">
        <a class="ml-media-card" href="${D}project_detail/index.html">
          <img src="${IMG.crowd}" alt="" />
          <span class="ml-media-card__tag"><span class="ml-status ml-status--accent">Live band</span></span>
          <div class="ml-media-card__body">
            <div class="ml-media-card__title">The Local Train</div>
            <div class="ml-media-card__sub">Indie Rock · Delhi NCR</div>
          </div>
        </a>
        <a class="ml-media-card" href="${D}studio_profile_discover/index.html">
          <img src="${IMG.mic}" alt="" />
          <span class="ml-media-card__tag"><span class="ml-status ml-status--accent">Studio</span></span>
          <div class="ml-media-card__body">
            <div class="ml-media-card__title">Yash Raj Sound</div>
            <div class="ml-media-card__sub">Dolby Atmos · Mumbai</div>
          </div>
        </a>
      </div>

      <div class="ml-section">
        <h2>Recommended for you</h2>
        <a class="ml-section__link" href="${D}people_talent/index.html">See all</a>
      </div>
      <div>
        ${result({ img: IMG.rahul, name: "Rahul Iyer", ver: true, meta: "Producer · Mix Engineer · Mumbai", trail: match(94) })}
        ${result({ img: IMG.priya, name: "Priya Nair", meta: "Session Guitarist · Pune", trail: match(88) })}
      </div>

      <div class="ml-section">
        <h2>Opportunities for you</h2>
        <a class="ml-section__link" href="${D}discover_opportunities/index.html">See all</a>
      </div>
      <a class="ml-card ml-card--padded" href="/preview/flows/opportunities/opportunity_detail/index.html">
        <div style="display:flex;justify-content:space-between;gap:var(--space-2)">
          <span class="ml-row__title">Bassist for a 5-track indie EP</span>
          <span class="ml-status ml-status--success">Paid</span>
        </div>
        <span class="ml-row__meta">Monsoon Sessions · Mumbai · closes in 6 days</span>
      </a>
    </main>
    ${tabbar("Home")}
  </div>` }));

const linkCount = finish("discover: ");
console.log("discover: wrote " + done.length + " screens, " + linkCount + " links verified");
