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
/* extra: markup appended after the tab body — used by business_overview for
   the lifecycle and empty states, which are the SAME profile rendered
   differently rather than four more screens. */
const businessScreen = ({ screen, tab, body, state, bodyStates, extra = "" }) =>
  writeScreen({
    flow: "business_profiles", screen, title: `Business — ${TABS.find(([k]) => k === tab)?.[1] ?? "Insights"}`,
    body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow("/preview/flows/discover/discover_feed/index.html", "Back")}
      <h1 class="ml-type-3" style="margin:0">Yash Raj Sound Studios</h1>
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

      <div${bodyStates ? ` data-state="${bodyStates}"` : state ? ` data-state="${state}"` : ""} style="padding:var(--space-3) var(--space-4) 0">
        ${body}
      </div>
      ${extra ? `<div style="padding:0 var(--space-4)">${extra}</div>` : ""}
    </main>
    <footer class="ml-app__footer" data-state="default owner draft no_services no_portfolio no_team no_reviews no_availability visible_if_owner leads analytics invite" style="display:flex;gap:var(--space-2)">
      <a class="ml-btn ml-btn--secondary" style="flex:1" href="/preview/flows/communication/chat_conversation/index.html">Message</a>
      <a class="ml-btn ml-btn--primary" style="flex:1" href="${P}business_book_now/index.html">Book now</a>
    </footer>
  </div>`,
  });

businessScreen({
  screen: "business_overview", tab: "business_overview",
  bodyStates: "default no_services no_portfolio no_team no_reviews no_availability",
  /* Lifecycle and empty states. The brief asks for different copy for an
     OWNER and a VISITOR on an empty section — an owner gets a job to do, a
     visitor gets an honest absence — so both are written out. */
  extra: `
        <div class="ml-banner ml-banner--warning" data-state="draft">
          <span class="ml-banner__body">This profile is a draft. Only you can see it.</span>
        </div>
        <div class="ml-banner ml-banner--info" data-state="unpublished">
          <span class="ml-banner__body">Unpublished. It won't appear in Discover, search or booking until you publish it.</span>
        </div>

        <div class="ml-empty" data-state="suspended">
          <h2 class="ml-empty__title">This business profile is temporarily unavailable</h2>
          <p class="ml-empty__body">It's under review. If it's yours, support can tell you more.</p>
          <a class="ml-btn ml-btn--primary" href="/preview/flows/user_settings/raise_ticket/index.html">Contact support</a>
        </div>

        <div class="ml-empty" data-state="not_found">
          <h2 class="ml-empty__title">This business is no longer available</h2>
          <p class="ml-empty__body">It may have closed or been removed.</p>
          <a class="ml-btn ml-btn--primary" href="/preview/flows/discover/discover_feed/index.html">Discover other businesses</a>
        </div>

        <div class="ml-empty" data-state="no_services">
          <h2 class="ml-empty__title">No services listed yet</h2>
          <p class="ml-empty__body">Customers can't book what isn't listed. Add what you offer and what it costs.</p>
          <a class="ml-btn ml-btn--primary" href="/preview/flows/services_availability/service_offerings_editor/index.html">Add a service</a>
        </div>
        <div class="ml-empty" data-state="no_portfolio">
          <h2 class="ml-empty__title">No work shown yet</h2>
          <p class="ml-empty__body">Show what you've made — it's the thing people actually judge you on.</p>
          <a class="ml-btn ml-btn--primary" href="/preview/flows/portfolio/portfolio_dashboard/index.html">Add work</a>
        </div>
        <div class="ml-empty" data-state="no_team">
          <h2 class="ml-empty__title">No team members listed</h2>
          <p class="ml-empty__body">People book rooms, but they trust engineers.</p>
          <a class="ml-btn ml-btn--primary" href="${P}business_team_tab/index.html?state=invite">Invite someone</a>
        </div>
        <div class="ml-empty" data-state="no_reviews">
          <h2 class="ml-empty__title">No reviews yet</h2>
          <p class="ml-empty__body">Reviews appear after a completed session. Nothing is shown until then.</p>
        </div>
        <div class="ml-empty" data-state="no_availability">
          <h2 class="ml-empty__title">Availability isn't published</h2>
          <p class="ml-empty__body">People can still message you to ask about a date.</p>
          <a class="ml-btn ml-btn--primary" href="/preview/flows/services_availability/availability_toggle/index.html">Publish availability</a>
        </div>`,
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
          <a class="ml-section__link" href="${P}business_dashboard/index.html">Dashboard</a></div>
        <a class="ml-action-row" href="${P}create_business/index.html">
          <span class="ml-choice__body">
            <span class="ml-choice__title">Add another business</span>
            <span class="ml-choice__meta">A second studio, a label, a venue</span>
          </span>
          ${icon("arrow-forward", { size: 18 })}
        </a>
        <div class="ml-person-row">
          <img src="${media("faces", 5)}" alt="" loading="lazy" />
          <span class="meta"><b>Rhea Kapoor</b><p>Studio manager since 2014</p></span>
        </div>`,
});

businessScreen({
  screen: "business_services_tab", tab: "business_services_tab",
  bodyStates: "default",
  extra: `
        <!-- Owner view: the same services, plus their publication state. The
             brief says services are OWNED by flow 11, so every edit hands off. -->
        <div data-state="owner">
          <div class="ml-section"><h2>Your services</h2>
            <a class="ml-section__link" href="/preview/flows/services_availability/service_offerings_editor/index.html">Manage</a></div>
          ${[["Tracking", "Live", "success"], ["Mixing", "Live", "success"],
             ["Mastering", "Draft", "neutral"], ["Rental", "Paused", "warning"]]
            .map(([n, st, kind]) => `<div class="ml-check-row">
            <span class="ml-choice__body"><span class="ml-choice__title">${n}</span></span>
            <span class="ml-status ml-status--${kind}">${st}</span>
          </div>`).join("\n          ")}
          <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-2)" href="/preview/flows/services_availability/session_configuration/index.html">Add a service</a>
        </div>`,
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
  bodyStates: "default",
  extra: `
        <div data-state="owner">
          <div class="ml-section"><h2>Team and roles</h2></div>
          ${[["Rhea Kapoor", "Owner", 5], ["Rohan D'Souza", "Admin · can accept bookings", 6],
             ["Kabir Sheth", "Staff", 8], ["Meera Nair", "Staff", 4]]
            .map(([n, r, i]) => `<div class="ml-person-row">
            <img src="${media("faces", i)}" alt="" loading="lazy" />
            <span class="meta"><b>${n}</b><p>${r}</p></span>
            <button class="ml-icon-btn ml-icon-btn--muted" type="button" aria-label="Change role for ${n}">${icon("arrow-forward", { size: 18 })}</button>
          </div>`).join("\n          ")}
          <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-2)" href="?state=invite">Invite a member</a>
        </div>

        <div data-state="invite">
          <div class="ml-section"><h2>Invite someone</h2></div>
          <div class="ml-field">
            <label for="t-who">Email or MusicLinkd username</label>
            <input id="t-who" type="text" placeholder="rohan@example.com" />
          </div>
          <div class="ml-field">
            <label for="t-role">Role</label>
            <select id="t-role"><option>Staff</option><option>Manager</option><option>Admin</option><option>Owner</option></select>
          </div>
          <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-1)">
            Admins can accept bookings and edit services. Staff appear on the profile but change nothing.
          </p>
          <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=owner">Send invitation</a>
        </div>`,
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
        </div>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-2)" href="${P}business_gallery/index.html">All 18 photographs</a>
`,
});

/* ── business_book_now — the booking handoff ────────────────────────────── */
writeScreen({
  flow: "business_profiles", screen: "business_book_now", title: "Business — Book",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(`${P}business_overview/index.html`, "Back to profile")}
      <h1 class="ml-type-3" style="margin:0">Book</h1>
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
      <h1 class="ml-type-3" style="margin:0">Insights</h1>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <!-- "visible_if_owner" is a note that this is the owner variant of the
           profile, not a state the screen enters. Marked so the contract holds. -->
      <!-- Leads. The brief: surface them, but do NOT build a second CRM —
           every row hands off to the flow that already owns that conversation. -->
      <div data-state="leads">
        <div class="ml-section"><h2>New enquiries</h2></div>
        ${[["Ishita Rao", "Asking about a Saturday in September", "Messages", "/preview/flows/communication/chat_conversation/index.html", 2],
           ["Kabir Sheth", "Mixing, 9 tracks, wants a quote", "Messages", "/preview/flows/communication/chat_conversation/index.html", 8],
           ["Meera Nair", "Booking request · Tracking Room 1, Wed", "Bookings", "/preview/flows/booking/my_bookings/index.html", 4],
           ["Wavelength Artists", "Applied to your opportunity", "Opportunities", "/preview/flows/opportunities/manage_applicants/index.html", 5]]
          .map(([n, what, owner, href, i]) => `<a class="ml-person-row" href="${href}">
          <img src="${media("faces", i)}" alt="" loading="lazy" />
          <span class="meta"><b>${n}</b><p>${what}</p></span>
          <span class="ml-status ml-status--neutral">${owner}</span>
        </a>`).join("\n        ")}
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Each of these lives in the flow that owns it — this is a list of what's waiting, not a second inbox.
        </p>
      </div>

      <div data-state="visible_if_owner analytics">
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

/* ── create_business — six steps as STATES of one screen ───────────────────
   The brief asks for lightweight creation and warns against a huge form. Six
   steps as states means you can see how far along you are and go back without
   spending a back-stack entry per field. Identity, logo, cover, description,
   location and hours are those steps — not six screens. */
const STEPS = ["basics", "identity", "location", "hours", "preview"];
const stepBar = (current) => `<nav class="ml-tabs ml-tabs--fill" role="tablist" aria-label="Step" style="margin:0 var(--space-4)">
      ${STEPS.map((st, i) => `<a class="ml-tabs__tab" role="tab" data-chip="${st}" href="?state=${st}">${i + 1}</a>`).join("")}
    </nav>`;

const bizField = (id, label, type, value, ph) => `<div class="ml-field">
        <label for="${id}">${label}</label>
        ${type === "textarea"
          ? `<textarea id="${id}" rows="3"${ph ? ` placeholder="${ph}"` : ""}>${value || ""}</textarea>`
          : type === "select"
            ? `<select id="${id}">${value.map((o) => `<option>${o}</option>`).join("")}</select>`
            : `<input id="${id}" type="${type}" value="${value || ""}"${ph ? ` placeholder="${ph}"` : ""} />`}
      </div>`;

writeScreen({
  flow: "business_profiles", screen: "create_business", title: "Business — Create",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(`${P}business_overview/index.html`, "Back")}
      <h1 class="ml-type-3" style="margin:0">Create a business</h1>
    </header>
    ${stepBar()}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="basics">
        <div class="ml-section"><h2>The basics</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Four things now, the rest whenever you like. Nothing is public until you publish.
        </p>
        ${bizField("b-name", "Business name", "text", "", "Yash Raj Sound Studios")}
        ${bizField("b-type", "Type", "select", ["Recording studio", "Mixing studio", "Rehearsal room", "Music school", "Label", "Venue", "Dealer", "Rental provider"])}
        ${bizField("b-city", "City", "text", "Mumbai")}
        ${bizField("b-short", "One line about it", "text", "", "Two tracking rooms and a mix suite")}
      </div>

      <div data-state="identity">
        <div class="ml-section"><h2>Logo and cover</h2></div>
        <div class="ml-upload-grid">
          <div class="ml-upload-grid__item">
            <img src="${media("gear", 7)}" alt="" loading="lazy" />
            <button class="ml-upload-grid__remove" type="button" aria-label="Remove logo"></button>
          </div>
          <div class="ml-upload-grid__item">
            <img src="${media("stage", 1)}" alt="" loading="lazy" />
            <button class="ml-upload-grid__remove" type="button" aria-label="Remove cover"></button>
          </div>
          <button class="ml-upload-grid__add" type="button" aria-label="Add an image"></button>
        </div>
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>About</h2></div>
        ${bizField("b-about", "Description", "textarea", "", "What you do, who for, and what makes the room worth booking.")}
        ${bizField("b-year", "Established", "text", "", "2014")}
        ${bizField("b-site", "Website", "url", "", "https://…")}
      </div>

      <div data-state="location">
        <div class="ml-section"><h2>Where you are</h2></div>
        ${bizField("b-addr", "Address", "text", "", "Unit 4, Link Road")}
        ${bizField("b-area", "Area", "text", "Andheri West")}
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>What to show publicly</h2></div>
        <div role="radiogroup" aria-label="Address visibility">
          ${[["Full address", "Anyone can find the door. Right for a studio or a venue.", true],
             ["Area only", "People see Andheri West, and you send the address when you accept.", false],
             ["Service area", "You travel to them. No address shown at all.", false]]
            .map(([t, m, on]) => `<div class="ml-choice" role="radio" aria-checked="${on}" tabindex="${on ? 0 : -1}">
            <span class="ml-choice__body">
              <span class="ml-choice__title">${t}</span>
              <span class="ml-choice__meta">${m}</span>
            </span>
            <span class="ml-choice__mark"></span>
          </div>`).join("\n          ")}
        </div>
      </div>

      <div data-state="hours">
        <div class="ml-section"><h2>Opening hours</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          When the doors are open. This is separate from when a specific room is free to book.
        </p>
        ${["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
          .map((d, i) => `<div class="ml-check-row">
          <span class="ml-choice__body">
            <span class="ml-choice__title">${d}</span>
            <span class="ml-choice__meta">${i === 6 ? "Closed" : "09:00 – 00:00"}</span>
          </span>
          <button class="ml-switch" type="button" role="switch" aria-checked="${i !== 6}" aria-label="${d}"></button>
        </div>`).join("\n        ")}
      </div>

      <div data-state="preview">
        <div class="ml-section"><h2>How it will look</h2></div>
        <a class="ml-result" href="${P}business_overview/index.html">
          <img class="ml-result__thumb" src="${media("stage", 1)}" alt="" loading="lazy" />
          <span class="ml-result__body">
            <span class="ml-result__name">Yash Raj Sound Studios</span>
            <span class="ml-result__meta">Recording studio · Andheri West, Mumbai</span>
          </span>
        </a>
        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>Services</span><span>Add after publishing</span></div>
          <div class="ml-summary__row"><span>Team</span><span>Just you for now</span></div>
          <div class="ml-summary__row"><span>Availability</span><span>Not published yet</span></div>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          You can publish now and add services, team and availability afterwards.
        </p>
      </div>

      <div class="ml-empty" data-state="published">
        <h2 class="ml-empty__title" style="color:var(--color-success)">Business created</h2>
        <p class="ml-empty__body">Add a service so people can actually book you — that's the one thing left that matters.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/services_availability/session_configuration/index.html">Add a service</a>
        <a class="ml-btn ml-btn--secondary" style="margin-top:var(--space-2)" href="${P}business_dashboard/index.html">Go to dashboard</a>
      </div>
    </main>
    <footer class="ml-app__footer" data-state="basics identity location hours preview">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="?state=published">Continue</a>
    </footer>
  </div>`,
});

/* ── business_dashboard — few numbers, each linking to what it counts ─────
   The brief warns against a generic analytics dashboard, so this is the
   owner's home: what needs doing today, then the quick actions. */
writeScreen({
  flow: "business_profiles", screen: "business_dashboard", title: "Business — Dashboard",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Yash Raj Sound Studios</h1>
      <span class="ml-appbar__actions">
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}business_owner_insights/index.html?state=analytics" aria-label="Insights">${icon("arrow-forward", { size: 20 })}</a>
      </span>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="default">
        <div class="ml-section"><h2>Needs you today</h2></div>
        <a class="ml-action-row" href="/preview/flows/booking/my_bookings/index.html">
          <span class="ml-choice__body">
            <span class="ml-choice__title">3 booking requests</span>
            <span class="ml-choice__meta">Oldest waiting 14 hours</span>
          </span>
          <span class="ml-status ml-status--warning">Answer</span>
        </a>
        <a class="ml-action-row" href="/preview/flows/communication/chat_thread_list/index.html">
          <span class="ml-choice__body">
            <span class="ml-choice__title">2 unread enquiries</span>
            <span class="ml-choice__meta">About mixing and about a Saturday</span>
          </span>
          <span class="ml-status ml-status--warning">Reply</span>
        </a>
        <a class="ml-action-row" href="/preview/flows/reviews/reputation_dashboard/index.html">
          <span class="ml-choice__body">
            <span class="ml-choice__title">2 reviews awaiting a reply</span>
          </span>
          ${icon("arrow-forward", { size: 18 })}
        </a>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Today</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>10:00 · Tracking Room 1</span><span>Ishita Rao</span></div>
          <div class="ml-summary__row"><span>15:00 · Mix Suite</span><span>Kabir Sheth</span></div>
          <div class="ml-summary__row"><span>19:00 · Booth B</span><span>Free</span></div>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>This month</h2></div>
        <div class="ml-stat-grid">
          <div class="ml-kpi"><span class="ml-kpi__num">₹4.2L</span>Booked<span class="ml-kpi__delta ml-kpi__delta--up">+12%</span></div>
          <div class="ml-kpi"><span class="ml-kpi__num">68%</span>Rooms used</div>
        </div>
      </div>

      <div class="ml-empty" data-state="quiet">
        <h2 class="ml-empty__title">Nothing needs you today</h2>
        <p class="ml-empty__body">No requests, no unread enquiries, and nothing booked. A good day to add work to your gallery.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/portfolio/portfolio_dashboard/index.html">Add work</a>
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Manage</h2></div>
      <a class="ml-action-row" href="${P}business_services_tab/index.html?state=owner">
        <span class="ml-choice__body"><span class="ml-choice__title">Services and rates</span></span>
        ${icon("arrow-forward", { size: 18 })}
      </a>
      <a class="ml-action-row" href="${P}business_team_tab/index.html?state=owner">
        <span class="ml-choice__body"><span class="ml-choice__title">Team</span>
        <span class="ml-choice__meta">6 people, 2 who can accept bookings</span></span>
        ${icon("arrow-forward", { size: 18 })}
      </a>
      <a class="ml-action-row" href="/preview/flows/services_availability/availability_toggle/index.html">
        <span class="ml-choice__body"><span class="ml-choice__title">Availability</span></span>
        ${icon("arrow-forward", { size: 18 })}
      </a>
      <a class="ml-action-row" href="${P}business_overview/index.html">
        <span class="ml-choice__body"><span class="ml-choice__title">Public profile</span>
        <span class="ml-choice__meta">See what customers see</span></span>
        ${icon("arrow-forward", { size: 18 })}
      </a>
    </main>
  </div>`,
});

/* ══ the approved plan's Business +3, part built ═══════════════════════════
   business_gallery is the last of that column. business_dashboard and
   team_management were already delivered — the latter as an owner STATE on
   business_team_tab rather than a screen, since it is the same list with
   different affordances.

   The gallery earns a route because it is linkable and shareable: a studio
   sends someone the photographs, not the profile. The tab shows a sample. ═══ */

writeScreen({
  flow: "business_profiles", screen: "business_gallery", title: "Business — Gallery",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(`${P}business_reviews_gallery_tab/index.html`, "Back")}
      <h1 class="ml-type-3" style="margin:0;flex:1">Gallery</h1>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        <p class="ml-type-1" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Yash Raj Sound Studios · 18 photographs
        </p>

        <div class="ml-section"><h3>The rooms</h3></div>
        <div class="ml-masonry">
          ${[1, 7, 12, 3].map((i) => `<img class="ml-mosaic-tile" src="${media(i % 2 ? "gear" : "stage", i)}" alt="" loading="lazy" />`).join("\n          ")}
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Equipment</h3></div>
        <div class="ml-masonry">
          ${[0, 6, 9].map((i) => `<img class="ml-mosaic-tile" src="${media("gear", i)}" alt="" loading="lazy" />`).join("\n          ")}
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Sessions here</h3></div>
        <div class="ml-masonry">
          ${[4, 6, 11].map((i) => `<img class="ml-mosaic-tile" src="${media("stage", i)}" alt="" loading="lazy" />`).join("\n          ")}
        </div>

        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-3)">
          Photographs of sessions are published with the artist's agreement.
        </p>
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No photographs yet</h2>
        <p class="ml-empty__body">
          People book a room they can picture. Three or four honest photographs of the live room
          and the desk do more than a description.
        </p>
        <a class="ml-btn ml-btn--primary" href="${P}business_dashboard/index.html">Add photographs</a>
      </div>
    </main>
    <footer class="ml-app__footer" data-state="default" style="display:flex;gap:var(--space-2)">
      <a class="ml-btn ml-btn--secondary" style="flex:1" href="/preview/flows/qr_share/qr_share_flow/index.html?state=options">Share</a>
      <a class="ml-btn ml-btn--primary" style="flex:1" href="${P}business_book_now/index.html">Book a room</a>
    </footer>
  </div>`,
});

finish("business_profiles: ");
