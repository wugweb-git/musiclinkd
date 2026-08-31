#!/usr/bin/env node
/**
 * Flow 12 — Business Profiles. Seven screens, seven declared states, all seven
 * the raw scaffold.
 *
 * Five of the seven — overview, services, team, equipment, reviews/gallery —
 * are tabs of ONE business profile, not five destinations. They share a header
 * (cover, name, rating) and differ only below the tab strip, so they are
 * generated from one composer that takes the tab body. The strip marks its own
 * tab with aria-selected and does not link to itself.
 *
 * business_book_now is the booking handoff and business_owner_insights is the
 * owner-only view, whose declared state `visible_if_owner` is — like
 * profile_management's `visible_if_*` — a note that this is the owner variant.
 *
 * Services, reviews and booking are CONSUMED from flows 11, 17 and 08 rather
 * than rebuilt here, which is what the spec asks for.
 *
 * No new CSS.
 */
import { writeScreen, backArrow, icon, assertIcons, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/business_profiles/";
assertIcons(["arrow-forward", "check", "send"]);

const TABS = [
  ["business_overview", "Overview"],
  ["business_services_tab", "Services"],
  ["business_team_tab", "Team"],
  ["business_equipment_tab", "Equipment"],
  ["business_reviews_gallery_tab", "Reviews"],
];

/* One business profile. The tab strip is shared and the selected tab is a
   <span>, not a link back to the page you are on. */
const businessScreen = ({ screen, tab, body, state }) =>
  writeScreen({
    flow: "business_profiles", screen, title: `Business — ${TABS.find(([k]) => k === tab)?.[1] ?? "Insights"}`,
    body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow("/preview/flows/discover/discover_feed/index.html", "Back")}
      <h1 class="ml-type-2" style="margin:0">Yash Raj Sound Studios</h1>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:0 0 var(--space-4)">
      <img src="${media("stage", 1)}" alt="" loading="lazy"
           style="width:100%;aspect-ratio:var(--media-aspect-card);object-fit:cover" />
      <div style="padding:var(--space-3) var(--space-4) 0">
        <h2 class="ml-type-3" style="margin:0">Yash Raj Sound Studios</h2>
        <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-1) 0 0">Recording studio · Andheri West, Mumbai</p>
        <div class="ml-rating" style="margin-top:var(--space-2)">
          <span class="ml-rating__score">4.8</span>
          <span class="ml-rating__stars">★★★★★</span>
          <span class="ml-rating__count">· 62 reviews</span>
        </div>
      </div>

      <nav class="ml-tabs" role="tablist" aria-label="Business profile" style="margin:var(--space-3) var(--space-4) 0">
        ${TABS.map(([k, l]) => k === tab
          ? `<span class="ml-tabs__tab" role="tab" aria-selected="true" aria-current="page">${l}</span>`
          : `<a class="ml-tabs__tab" role="tab" aria-selected="false" href="${P}${k}/index.html">${l}</a>`).join("\n        ")}
      </nav>

      <div${state ? ` data-state="${state}"` : ""} style="padding:var(--space-3) var(--space-4) 0">
        ${body}
      </div>
    </main>
    <footer class="ml-app__footer" style="display:flex;gap:var(--space-2)">
      <a class="ml-btn ml-btn--secondary" style="flex:1" href="/preview/flows/communication/chat_conversation/index.html">Message</a>
      <a class="ml-btn ml-btn--primary" style="flex:1" href="${P}business_book_now/index.html">Book now</a>
    </footer>
  </div>`,
  });

businessScreen({
  screen: "business_overview", tab: "business_overview",
  body: `<p class="ml-type-2" style="color:var(--color-muted);margin:0">
          Two tracking rooms and a mix suite in Andheri West. Neve sidecar, maintained backline,
          and engineers who have worked on records you have heard of. Open 9am to midnight, seven days.
        </p>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>At a glance</h3></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Rooms</span><span>3</span></div>
          <div class="ml-summary__row"><span>From</span><span>₹700/hr</span></div>
          <div class="ml-summary__row"><span>Team</span><span>6 people</span></div>
          <div class="ml-summary__row"><span>Established</span><span>2014</span></div>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>The rooms</h3></div>
        <div class="ml-rail">
          ${[1, 7, 12].map((i) => `<a class="ml-media-card" href="/preview/flows/booking/studio_profile/index.html">
            <img src="${media("gear", i)}" alt="" loading="lazy" />
          </a>`).join("\n          ")}
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Owner</h3>
          <a class="ml-section__link" href="${P}business_owner_insights/index.html">Insights</a></div>
        <div class="ml-person-row">
          <img src="${media("faces", 5)}" alt="" loading="lazy" />
          <span class="meta"><b>Rhea Kapoor</b><p>Studio manager since 2014</p></span>
        </div>`,
});

businessScreen({
  screen: "business_services_tab", tab: "business_services_tab",
  body: `<!-- Services are OWNED by flow 11 and consumed here, not rebuilt. -->
        ${[["Tracking", "Live room, up to 8 inputs", "₹1,800/hr"],
           ["Mixing", "In the box or through the desk", "₹1,200/hr"],
           ["Mastering", "Per track, two revisions", "₹4,000/track"],
           ["Rehearsal", "Backline included", "₹700/hr"]]
          .map(([n, m, p]) => `<a class="ml-service" href="/preview/flows/booking/configure_session/index.html">
          <span class="ml-choice__body">
            <span class="ml-choice__title">${n}</span>
            <span class="ml-choice__meta">${m}</span>
          </span>
          <span class="ml-result__trail">${p}</span>
        </a>`).join("\n        ")}
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Rates are per room, before engineer time. Block bookings over three days are quoted separately.
        </p>`,
});

businessScreen({
  screen: "business_team_tab", tab: "business_team_tab",
  body: `${[["Rhea Kapoor", "Studio manager", 5], ["Rohan D'Souza", "Chief engineer", 6],
            ["Kabir Sheth", "Engineer", 8], ["Meera Nair", "Assistant engineer", 4]]
          .map(([n, r, i]) => `<a class="ml-person-row" href="/preview/flows/public_profile/public_profile_view/index.html">
          <img src="${media("faces", i)}" alt="" loading="lazy" />
          <span class="meta"><b>${n}</b><p>${r}</p></span>
          ${icon("arrow-forward", { size: 18 })}
        </a>`).join("\n        ")}`,
});

businessScreen({
  screen: "business_equipment_tab", tab: "business_equipment_tab",
  body: `${[["Microphones", ["Neumann U87", "Shure SM7B ×2", "Coles 4038 pair", "AKG C414 ×2"]],
            ["Outboard", ["Neve 1073 ×4", "LA-2A", "1176 pair", "Lexicon 480L"]],
            ["Monitoring", ["ATC SCM25A", "Yamaha NS-10M", "Auratone"]],
            ["Backline", ["Yamaha Maple Custom kit", "Fender Twin", "Ampeg SVT", "Nord Stage 3"]]]
          .map(([group, items]) => `<div class="ml-section" style="margin-top:var(--space-3)"><h3>${group}</h3></div>
        <div class="ml-summary">
          ${items.map((it) => `<div class="ml-summary__row"><span>${it}</span><span>Included</span></div>`).join("\n          ")}
        </div>`).join("\n        ")}`,
});

businessScreen({
  screen: "business_reviews_gallery_tab", tab: "business_reviews_gallery_tab",
  body: `<!-- Reviews are OWNED by flow 17 and consumed here. -->
        <div class="ml-rating">
          <span class="ml-rating__score">4.8</span>
          <span class="ml-rating__stars">★★★★★</span>
          <span class="ml-rating__count">· 62 reviews</span>
        </div>
        ${[["Ishita Rao", 2, 5, "Quiet live room and a patient engineer. Three vocal takes done inside a morning."],
           ["Kabir Sheth", 8, 4, "Great desk, and the backline is genuinely maintained. Parking is tight before 6pm."]]
          .map(([n, i, st, b]) => `<article class="ml-review-card">
          <div class="ml-review-card__head">
            <img class="ml-avatar ml-avatar--sm" src="${media("faces", i)}" alt="" loading="lazy" />
            <b>${n}</b>
            <span class="ml-review-card__stars">${"★".repeat(st)}${"☆".repeat(5 - st)}</span>
          </div>
          <p class="ml-review-card__body">${b}</p>
        </article>`).join("\n        ")}
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-2)" href="/preview/flows/reviews/review_list/index.html">All 62 reviews</a>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Gallery</h3></div>
        <div class="ml-masonry">
          ${[1, 7, 12, 3, 6, 9].map((i) => `<img class="ml-mosaic-tile" src="${media(i % 2 ? "gear" : "stage", i)}" alt="" loading="lazy" />`).join("\n          ")}
        </div>`,
});

/* ── business_book_now — the booking handoff ────────────────────────────── */
writeScreen({
  flow: "business_profiles", screen: "business_book_now", title: "Business — Book",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(`${P}business_overview/index.html`, "Back to profile")}
      <h1 class="ml-type-2" style="margin:0">Book</h1>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
        Booking is handled by the booking flow — pick a room, then a date and a session type.
      </p>
      ${[["Tracking Room 1", "42 m² live room · ₹1,800/hr", media("stage", 1)],
         ["Mix Suite", "18 m² treated · ₹1,200/hr", media("gear", 7)],
         ["Booth B", "6 m² vocal booth · ₹700/hr", media("gear", 12)]]
        .map(([n, m, img]) => `<a class="ml-result" href="/preview/flows/booking/availability_calendar/index.html">
        <img class="ml-result__thumb" src="${img}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">${n}</span>
          <span class="ml-result__meta">${m}</span>
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>`).join("\n      ")}
    </main>
  </div>`,
});

/* ── business_owner_insights — visible_if_owner ─────────────────────────── */
writeScreen({
  flow: "business_profiles", screen: "business_owner_insights", title: "Business — Insights",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(`${P}business_overview/index.html`, "Back to profile")}
      <h1 class="ml-type-2" style="margin:0">Insights</h1>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <!-- "visible_if_owner" is a note that this is the owner variant of the
           profile, not a state the screen enters. Marked so the contract holds. -->
      <div data-state="visible_if_owner">
        <div class="ml-stat-grid">
          <div class="ml-kpi"><span class="ml-kpi__num">₹4.2L</span>Booked this month<span class="ml-kpi__delta ml-kpi__delta--up">+12%</span></div>
          <div class="ml-kpi"><span class="ml-kpi__num">68%</span>Room utilisation<span class="ml-kpi__delta ml-kpi__delta--up">+5%</span></div>
          <div class="ml-kpi"><span class="ml-kpi__num">3.1k</span>Profile views</div>
          <div class="ml-kpi"><span class="ml-kpi__num">9</span>Open requests</div>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Utilisation by room</h3></div>
        ${[["Tracking Room 1", 84], ["Mix Suite", 61], ["Booth B", 38]]
          .map(([r, pct]) => `<div class="ml-progress">
          <span class="ml-progress__title">${r}</span>
          <span class="ml-progress__meta">${pct}%</span>
          <span class="ml-progress__track"><span class="ml-progress__fill" style="width:${pct}%"></span></span>
        </div>`).join("\n        ")}

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Manage</h3></div>
        <a class="ml-action-row" href="${P}business_team_tab/index.html">
          <span class="ml-choice__body"><span class="ml-choice__title">Team</span>
          <span class="ml-choice__meta">6 people, 2 with booking rights</span></span>
          ${icon("arrow-forward", { size: 18 })}
        </a>
        <a class="ml-action-row" href="/preview/flows/services_availability/rate_pricing_editor/index.html">
          <span class="ml-choice__body"><span class="ml-choice__title">Rates and services</span></span>
          ${icon("arrow-forward", { size: 18 })}
        </a>
        <a class="ml-action-row" href="/preview/flows/reviews/reputation_dashboard/index.html">
          <span class="ml-choice__body"><span class="ml-choice__title">Reputation</span>
          <span class="ml-choice__meta">2 reviews awaiting a reply</span></span>
          ${icon("arrow-forward", { size: 18 })}
        </a>
      </div>
    </main>
  </div>`,
});

finish("business_profiles: ");
