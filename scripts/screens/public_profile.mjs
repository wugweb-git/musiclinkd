#!/usr/bin/env node
/**
 * Flow 14 — Public Profile. Seven screens, 13 declared states, all seven the
 * raw scaffold.
 *
 * public_profile_view, public_profile_view_band and public_profile_view_business
 * are one profile rendered for three subject types — the same recycling as
 * profile_management's four editors. One composer, three subjects.
 *
 * The other four (media_gallery, collaborators_graph, profile_analytics,
 * talent_services_view) each declare default + empty, which is the shape the
 * system already has an answer for: content, or .ml-empty.
 *
 * No new CSS.
 */
import { writeScreen, backArrow, icon, assertIcons, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/public_profile/";
assertIcons(["arrow-forward", "send", "heart", "star", "check-circle-fill", "edit", "graph-bar"]);

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-3" style="margin:0">${title}</h1>
    </header>`;

/* ── one public profile, five subjects ──────────────────────────────────
   Everything past `sections` is optional, and only the talent template asks
   for all of it. That template carries the flow's one real dataContract
   (Arijit Sen — users, portfolio_projects, reviews), and it used to be
   written twice: a second generator authored a richer version of this exact
   screen and lost the race to this one, so its verified mark, trust block,
   music identity and owner view never reached the site. Folded in here as
   parameters, so the richer content is served BY the composer rather than
   against it. */
const profileView = ({ screen, name, tagline, cover, face, stat, about, sections, cta,
                       verified = false, availability, trust, identity, work, owner = false }) =>
  writeScreen({
    flow: "public_profile", screen, title: `Profile — ${name}`,
    body: `
  <div class="ml-app">
    ${bar(name, "/preview/flows/discover/discover_feed/index.html")}
    <main class="ml-app__body app-content--scroll" style="padding:0 0 var(--space-4)">
      <img src="${cover}" alt="" loading="lazy"
           style="width:100%;aspect-ratio:var(--media-aspect-card);object-fit:cover" />
      <div style="padding:0 var(--space-4)">
        <img class="ml-avatar ml-avatar--lg ml-lift" src="${face}" alt="" loading="lazy"
             style="margin-top:calc(var(--size-3) * -1)" />
        <h2 class="ml-type-3" style="margin:var(--space-2) 0 var(--space-1)">${name}${verified ? ` ${icon("check-circle-fill", { size: 18 })}` : ""}</h2>
        <p class="ml-type-1" style="color:var(--color-muted);margin:0">${tagline}</p>
        <div class="ml-rating" style="margin-top:var(--space-2)">
          <span class="ml-rating__score">${stat[0]}</span>
          <span class="ml-rating__count">· ${stat[1]}</span>
        </div>
        ${[
          availability && `<p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-2) 0 0;display:flex;align-items:center;gap:var(--space-1)"><span style="width:6px;height:6px;border-radius:50%;background:currentColor;flex:none"></span>${availability}</p>`,
          owner && `<!-- Public and owner are one screen with two states, not two
             profiles: "It is the same profile with owner controls." The owner
             actions replace the public ones in the same slot rather than
             sitting beside them. -->
        <div class="ml-action-row" data-state="owner">
          <a class="ml-btn ml-btn--primary ml-btn--block" href="/preview/flows/profile_management/edit_identity/index.html">${icon("edit", { size: 16 })} Edit profile</a>
          <a class="ml-btn ml-btn--secondary" href="${P}profile_analytics/index.html" aria-label="Analytics">${icon("graph-bar", { size: 16 })}</a>
        </div>
        <a class="ml-link" data-state="owner" href="?state=default" style="display:block;text-align:center;font-size:var(--type-1);margin-top:var(--space-2)">View as public</a>`,
        ].filter(Boolean).join("\n        ")}
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">${about}</p>
        ${[
          trust && `<!-- Trust as numbers with their source, not a wall of badges. -->
        <div class="ml-trust" style="margin-top:var(--space-3)">
          <div class="ml-trust__row">
            <span class="ml-trust__mark">${icon("check-circle-fill", { size: 14 })}</span>
            <div><div class="ml-trust__what">${trust.what}</div><div class="ml-trust__who">${trust.who}</div></div>
          </div>
          <div class="ml-trust__stats">
            ${trust.stats.map(([n, l]) => `<div class="ml-testimonial__stat"><b>${n}</b><span>${l}</span></div>`).join("\n            ")}
          </div>
        </div>`,
          identity && `<div class="ml-section" style="margin-top:var(--space-3)"><h3>Music identity</h3></div>
        ${identity.map(([label, chips]) => `<div class="ml-type-1" style="color:var(--color-muted);margin-bottom:var(--space-1)">${label}</div>
        <div class="ml-chip-wrap" style="margin-bottom:var(--space-3)">
          ${chips.map((c) => `<span class="ml-chip">${c}</span>`).join("")}
        </div>`).join("\n        ")}`,
          work && `<div class="ml-section" style="margin-top:var(--space-3)"><h3>Featured work</h3></div>
        <div class="ml-rail">
          ${work.map(([img, t, sub, href]) => `<a class="ml-media-card" href="${href}">
            <img src="${img}" alt="" loading="lazy" />
            <div class="ml-media-card__body">
              <div class="ml-media-card__title">${t}</div>
              <div class="ml-media-card__sub">${sub}</div>
            </div>
          </a>`).join("\n          ")}
        </div>`,
        ].filter(Boolean).join("\n        ")}
        ${sections}

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Reviews</h3>
          <a class="ml-section__link" href="/preview/flows/reviews/review_list/index.html">See all</a></div>
        <article class="ml-review-card">
          <div class="ml-review-card__head">
            <img class="ml-avatar ml-avatar--sm" src="${media("faces", 8)}" alt="" loading="lazy" />
            <b>Kabir Sheth</b>
            <span class="ml-review-card__stars">★★★★★</span>
          </div>
          <p class="ml-review-card__body">Turned up prepared, worked fast, and took direction without ego. Booked again the same week.</p>
        </article>
      </div>
    </main>
    <footer class="ml-app__footer"${owner ? ` data-state="default"` : ""} style="display:flex;gap:var(--space-2)">
      <a class="ml-btn ml-btn--secondary" style="flex:1" href="/preview/flows/communication/chat_conversation/index.html">Message</a>
      <a class="ml-btn ml-btn--primary" style="flex:1" href="${cta[1]}">${cta[0]}</a>
    </footer>
  </div>`,
  });

profileView({
  screen: "public_profile_view", name: "Ishita Rao", tagline: "Session vocalist · Mumbai",
  cover: media("stage", 3), face: media("faces", 2), stat: ["4.8", "62 sessions"],
  about: "Hindi and English pop, light belt and soft head voice. Twelve EPs and two features since 2021. Comfortable tracking to a scratch guide or writing the topline in the room.",
  cta: ["Book a session", "/preview/flows/booking/studio_discovery/index.html"],
  sections: `<div class="ml-section" style="margin-top:var(--space-3)"><h3>Works with</h3></div>
        <div class="ml-chip-wrap">
          ${["Pop", "Indie", "Film", "Hindi", "English"].map((g) => `<span class="ml-chip">${g}</span>`).join("\n          ")}
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Selected work</h3>
          <a class="ml-section__link" href="${P}media_gallery/index.html">Gallery</a></div>
        <div class="ml-rail">
          ${[3, 6, 11].map((i) => `<a class="ml-media-card" href="${P}media_gallery/index.html">
            <img src="${media("stage", i)}" alt="" loading="lazy" />
          </a>`).join("\n          ")}
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Services</h3>
          <a class="ml-section__link" href="${P}talent_services_view/index.html">See rates</a></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Studio session</span><span>₹12,000/day</span></div>
          <div class="ml-summary__row"><span>Remote vocal</span><span>₹6,000/track</span></div>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>More on this profile</h3></div>
        <a class="ml-action-row" href="${P}collaborators_graph/index.html">
          <span class="ml-choice__body"><span class="ml-choice__title">Collaborators</span></span>
          ${icon("arrow-forward", { size: 18 })}
        </a>
        <a class="ml-action-row" href="${P}profile_analytics/index.html">
          <span class="ml-choice__body"><span class="ml-choice__title">Analytics</span>
          <span class="ml-choice__meta">Only you can see this</span></span>
          ${icon("arrow-forward", { size: 18 })}
        </a>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Other profile types</h3></div>
        ${[["Band", "public_profile_view_band"], ["Business", "public_profile_view_business"],
           ["Venue", "public_profile_view_venue"], ["Playback vocalist", "public_profile_view_talent"]]
          .map(([l, sc]) => `<a class="ml-action-row" href="${P}${sc}/index.html">
          <span class="ml-choice__body"><span class="ml-choice__title">${l}</span></span>
          ${icon("arrow-forward", { size: 18 })}
        </a>`).join("\n        ")}`,
});

profileView({
  screen: "public_profile_view_band", name: "The Kolaba Sessions", tagline: "Four-piece indie rock · Mumbai",
  cover: media("stage", 6), face: media("faces", 8), stat: ["4.7", "38 shows"],
  about: "Guitar-led indie with a horn section when the room allows it. Playing together since 2022, mostly around Mumbai and Pune.",
  cta: ["Book the band", "/preview/flows/booking/studio_discovery/index.html"],
  sections: `<div class="ml-section" style="margin-top:var(--space-3)"><h3>Line-up</h3></div>
        ${[["Ishita Rao", "Vocals", 2], ["Kabir Sheth", "Guitar", 8], ["Meera Nair", "Bass", 4], ["Rohan D'Souza", "Drums", 6]]
          .map(([n, r, i]) => `<div class="ml-person-row">
          <img src="${media("faces", i)}" alt="" loading="lazy" />
          <span class="meta"><b>${n}</b><p>${r}</p></span>
        </div>`).join("\n        ")}

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Upcoming shows</h3></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>18 Aug · Antisocial, Khar</span><span>₹500</span></div>
          <div class="ml-summary__row"><span>02 Sep · The Quarter</span><span>₹700</span></div>
        </div>`,
});

profileView({
  screen: "public_profile_view_business", name: "Yash Raj Sound Studios", tagline: "Recording studio · Andheri West",
  cover: media("stage", 1), face: media("gear", 7), stat: ["4.8", "62 reviews"],
  about: "Two tracking rooms and a mix suite in Andheri West. Neve sidecar, maintained backline, and engineers who have worked on the records you have heard of.",
  cta: ["Check availability", "/preview/flows/booking/availability_calendar/index.html"],
  sections: `<div class="ml-section" style="margin-top:var(--space-3)"><h3>Services</h3></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Tracking</span><span>from ₹1,800/hr</span></div>
          <div class="ml-summary__row"><span>Mixing</span><span>from ₹1,200/hr</span></div>
          <div class="ml-summary__row"><span>Rehearsal</span><span>from ₹700/hr</span></div>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>The rooms</h3></div>
        <div class="ml-rail">
          ${[1, 7, 12].map((i) => `<a class="ml-media-card" href="/preview/flows/booking/studio_profile/index.html">
            <img src="${media("gear", i)}" alt="" loading="lazy" />
          </a>`).join("\n          ")}
        </div>`,
});


/* public_profile_view_talent and _venue already existed but were authored
   separately: venue had no heading at all, no way out, and two hardcoded 2px
   values. Running them through the same composer is the point of having one.

   Talent is the flow's one screen with a real seeded dataContract, so its
   content is that contract rather than invented: Arijit Sen, singer and
   composer, Mumbai; the three genres, three languages and one instrument
   from users; the released project from portfolio_projects; the five-star
   T-Series review from reviews. The avatarUrl in the seed is a stock URL and
   is deliberately not used — brand photography only. */
profileView({
  screen: "public_profile_view_talent", name: "Arijit Sen", tagline: "Singer · Composer · Mumbai, MH",
  cover: media("stage", 11), face: media("faces", 4), stat: ["5.0", "24 sessions"],
  verified: true, owner: true,
  availability: "Available for collaboration · Touring",
  about: "Playback & classical singer known for Bollywood, Sufi, and acoustic reinterpretations. 10+ years touring and session work.",
  trust: { what: "Verified Artist", who: "Identity confirmed on MusicLinkd",
           stats: [["5.0", "Rating"], ["10+ yrs", "Touring &amp; session work"]] },
  identity: [["Genres", ["Bollywood / Filmi", "Sufi Devotional", "Acoustic / Unplugged"]],
             ["Languages", ["Hindi", "Bengali", "English"]],
             ["Instruments", ["Acoustic Guitar"]]],
  work: [[media("stage", 3), "Tum Hi Toh (Unplugged)", "4.2M plays · Released Feb 2026",
          "/preview/flows/portfolio/profile_project_view/index.html"]],
  cta: ["Book a session", "/preview/flows/booking/studio_discovery/index.html"],
  sections: `<div class="ml-section" style="margin-top:var(--space-3)"><h3>Services</h3>
          <a class="ml-section__link" href="${P}talent_services_view/index.html">See rates</a></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Playback session</span><span>₹18,000/day</span></div>
          <div class="ml-summary__row"><span>Remote vocal</span><span>₹8,000/track</span></div>
        </div>`,
});

profileView({
  screen: "public_profile_view_venue", name: "The Quarter", tagline: "Live room · Lower Parel",
  cover: media("stage", 6), face: media("gear", 12), stat: ["4.6", "112 shows"],
  about: "A 220-capacity room with a proper stage and a house engineer. Thursday to Saturday, doors at eight.",
  cta: ["Enquire about a date", "/preview/flows/booking/availability_calendar/index.html"],
  sections: `<div class="ml-section" style="margin-top:var(--space-3)"><h3>The room</h3></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Capacity</span><span>220</span></div>
          <div class="ml-summary__row"><span>Stage</span><span>6m × 4m</span></div>
          <div class="ml-summary__row"><span>Hire fee</span><span>₹25,000/night</span></div>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Provided</h3></div>
        <div class="ml-chip-wrap">
          ${["PA system", "Backline", "Lighting", "Green room"].map((g) => `<span class="ml-chip">${g}</span>`).join("\n          ")}
        </div>`,
});

/* ── media_gallery — default / empty ────────────────────────────────────── */
writeScreen({
  flow: "public_profile", screen: "media_gallery", title: "Profile — Gallery",
  body: `
  <div class="ml-app">
    ${bar("Gallery", `${P}public_profile_view/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-masonry" data-state="default">
        ${[3, 6, 11, 1, 7, 4, 12, 9].map((i) => `<img class="ml-mosaic-tile" src="${media(i % 2 ? "stage" : "gear", i)}" alt="" loading="lazy" />`).join("\n        ")}
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">Nothing in the gallery yet</h2>
        <p class="ml-empty__body">Images added to a profile or a portfolio piece show up here.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/portfolio/portfolio_dashboard/index.html">Add work</a>
      </div>
    </main>
  </div>`,
});

/* ── talent_services_view — default / empty ─────────────────────────────── */
const SERVICES = [
  ["Studio session", "Full day, in person, anywhere in Mumbai", "₹12,000/day"],
  ["Remote vocal", "Delivered as stems within 5 days", "₹6,000/track"],
  ["Topline writing", "Melody and lyric, one track", "₹15,000"],
  ["Live performance", "Set of up to 60 minutes", "₹25,000"],
];
writeScreen({
  flow: "public_profile", screen: "talent_services_view", title: "Profile — Services",
  body: `
  <div class="ml-app">
    ${bar("Services & rates", `${P}public_profile_view/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        ${SERVICES.map(([n, m, p]) => `<a class="ml-service" href="/preview/flows/booking/configure_session/index.html">
          <span class="ml-choice__body">
            <span class="ml-choice__title">${n}</span>
            <span class="ml-choice__meta">${m}</span>
          </span>
          <span class="ml-result__trail">${p}</span>
        </a>`).join("\n        ")}
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Rates are a starting point. Anything unusual, send a message first.
        </p>
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No services listed</h2>
        <p class="ml-empty__body">This person hasn't published rates. You can still message them.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/communication/chat_conversation/index.html">Send a message</a>
      </div>
    </main>
  </div>`,
});

/* ── collaborators_graph — default / empty ──────────────────────────────── */
writeScreen({
  flow: "public_profile", screen: "collaborators_graph", title: "Profile — Collaborators",
  body: `
  <div class="ml-app">
    ${bar("Collaborators", `${P}public_profile_view/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          People Ishita has credited, or been credited by, on released work.
        </p>
        ${[["Kabir Sheth", "Producer · 6 tracks together", 8], ["Rohan D'Souza", "Mixing engineer · 4 tracks", 6],
           ["Octave Sound Labs", "Studio · 3 sessions", 5], ["Meera Nair", "Backing vocals · 2 tracks", 4]]
          .map(([n, m, i]) => `<a class="ml-person-row" href="${P}public_profile_view/index.html">
          <img src="${media("faces", i)}" alt="" loading="lazy" />
          <span class="meta"><b>${n}</b><p>${m}</p></span>
          ${icon("arrow-forward", { size: 18 })}
        </a>`).join("\n        ")}
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No credited collaborators yet</h2>
        <p class="ml-empty__body">Credits appear here once both people confirm them on a portfolio piece.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/portfolio/credit_attribution/index.html">Add a credit</a>
      </div>
    </main>
  </div>`,
});

/* ── profile_analytics — default / empty ────────────────────────────────── */
writeScreen({
  flow: "public_profile", screen: "profile_analytics", title: "Profile — Analytics",
  body: `
  <div class="ml-app">
    ${bar("Profile analytics", `${P}public_profile_view/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        <div class="ml-stat-grid">
          <div class="ml-kpi"><span class="ml-kpi__num">1,284</span>Profile views<span class="ml-kpi__delta ml-kpi__delta--up">+18%</span></div>
          <div class="ml-kpi"><span class="ml-kpi__num">96</span>Saves<span class="ml-kpi__delta ml-kpi__delta--up">+7</span></div>
          <div class="ml-kpi"><span class="ml-kpi__num">31</span>Messages<span class="ml-kpi__delta ml-kpi__delta--down">−4</span></div>
          <div class="ml-kpi"><span class="ml-kpi__num">12</span>Booking requests</div>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Where views come from</h3></div>
        ${[["Discover", 62], ["Search", 21], ["Shared links", 11], ["Opportunities", 6]]
          .map(([src, pct]) => `<div class="ml-progress">
          <span class="ml-progress__title">${src}</span>
          <span class="ml-progress__meta">${pct}%</span>
          <span class="ml-progress__track"><span class="ml-progress__fill" style="width:${pct}%"></span></span>
        </div>`).join("\n        ")}
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">Not enough data yet</h2>
        <p class="ml-empty__body">Analytics appear once your profile has been seen by a few people. Completing your profile is the fastest way there.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/profile_management/profile_completion/index.html">Finish your profile</a>
      </div>
    </main>
  </div>`,
});

finish("public_profile: ");
