#!/usr/bin/env node
/**
 * The last sixteen scaffolds, across six flows.
 *
 * Grouped into one generator because they share composers rather than because
 * they share a flow — which is the same argument the rest of this work has
 * been making. The four singletons (saved_favorites, qr_share, verifications,
 * search) are each ONE screen carrying four to seven declared states, so they
 * are the densest state work left in the app and the least screen-shaped.
 *
 *   notifications_center      loading / empty / populated / search_filtered /
 *                             category_filtered / muted_banner — six states of
 *                             one list.
 *   saved_favorites_hub       artists / bands / studios / marketplace / empty /
 *                             grid_view / list_view — a tabbed hub whose view
 *                             mode is also a state.
 *   verification_screen       a four-step wizard as four states, not four
 *                             screens.
 *   qr_share_flow             home / options / preview.
 *   universal_search          home / live_typing / results / empty.
 *
 * No new CSS.
 */
import { writeScreen, backArrow, icon, assertIcons, finish, media } from "../write-screen.mjs";

assertIcons(["arrow-forward", "search", "add", "check", "cancel", "bell", "send", "heart"]);

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-2" style="margin:0">${title}</h1>
    </header>`;

const tab = (state, label) =>
  `<a class="ml-tabs__tab" role="tab" data-chip="${state}" href="?state=${state}">${label}</a>`;

const rowLink = (label, href, meta) => `<a class="ml-action-row" href="${href}">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>`;

const rowSwitch = (label, meta, on) => `<div class="ml-check-row">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        <button class="ml-switch" type="button" role="switch" aria-checked="${on}" aria-label="${label}"></button>
      </div>`;

/* ══ FLOW 13 — Notifications ══════════════════════════════════════════════ */
const N = "/preview/flows/notifications/";
/* The FLOW-13 brief's SCREEN 06-16 are eleven notification TYPES. They are one
   list: each row declares which category filters show it, whether it is high
   priority, and where it goes. Priority is deliberately carried by an explicit
   label and by ordering, never by colour alone — the brief's accessibility
   rule and its "do not use excessive red" rule both apply.

   Grouping applies to low-value repetition only. A payment, a booking and a
   security event each have independent consequences and are never collapsed,
   which is stated in the brief and enforced by the `group` flag below. */
const P_HIGH = "populated high_priority";
const NOTES = [
  { st: `${P_HIGH} category_filtered`, pri: "high", text: "Your payment of ₹5,000 failed",
    meta: "Payments · 8m", face: 1, unread: true,
    href: "/preview/flows/booking/payment_screen/index.html?state=error" },
  { st: `${P_HIGH}`, pri: "high", text: "Yash Raj cancelled your session on Tue 12 Aug",
    meta: "Bookings · 40m", face: 5, unread: true,
    href: "/preview/flows/booking/my_bookings/index.html?state=cancelled" },
  { st: `${P_HIGH} search_filtered`, pri: "high", text: "New sign-in from Chrome on Windows",
    meta: "Security · 2h", face: 12, unread: true,
    href: "/preview/flows/user_settings/security_settings/index.html?state=security_alert" },
  { st: "populated search_filtered", pri: "medium", text: "Ishita Rao sent you a message",
    meta: "Messages · 20m", face: 2, unread: true,
    href: "/preview/flows/communication/chat_conversation/index.html" },
  { st: "populated category_filtered", pri: "medium", text: "Octave Sound Labs shortlisted your application",
    meta: "Opportunities · 2h", face: 5, unread: true,
    href: "/preview/flows/opportunities/my_applications/index.html?state=shortlisted" },
  { st: "populated", pri: "medium", text: "You've been invited to collaborate on Neon Monsoon",
    meta: "Projects · 4h", face: 8, unread: false,
    href: "/preview/flows/notifications/notification_detail/index.html?state=collaboration" },
  { st: "populated search_filtered", pri: "medium", text: "Aarav Menon countered your offer at ₹26,000",
    meta: "Market · Yesterday", face: 8, unread: false,
    href: "/preview/flows/marketplace/marketplace_make_offer/index.html?state=countered" },
  { st: "populated", pri: "medium", text: "New enquiry about Full Track Mix & Master",
    meta: "Services · Yesterday", face: 4, unread: false,
    href: "/preview/flows/communication/chat_conversation/index.html" },
  { st: "populated", pri: "low", text: "Kabir Sheth left you a 5-star review",
    meta: "Reviews · 2d", face: 6, unread: false,
    href: "/preview/flows/reviews/review_detail/index.html" },
  { st: "populated", pri: "low", text: "Your work Neon Monsoon was shared 4 times",
    meta: "Portfolio · 3d", face: 3, unread: false,
    href: "/preview/flows/portfolio/profile_project_view/index.html" },
  { st: "populated", pri: "low", text: "MusicLinkd 1.4.2 — offers now expire after 48 hours",
    meta: "System · 4d", face: 0, unread: false,
    href: "/preview/flows/user_settings/app_version/index.html" },
];

writeScreen({
  flow: "notifications", screen: "notifications_center", title: "Notifications",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Notifications</h1>
      <span class="ml-appbar__actions">
        <a class="ml-icon-btn ml-icon-btn--bare" href="?state=populated" aria-label="Mark all as read">${icon("check", { size: 20 })}</a>
        <a class="ml-icon-btn ml-icon-btn--bare" href="${N}notification_settings/index.html" aria-label="Notification settings">${icon("bell", { size: 20 })}</a>
      </span>
    </header>
    <nav class="ml-tabs" role="tablist" aria-label="Filter" style="margin:0 var(--space-4)">
      ${tab("populated", "All")}${tab("high_priority", "Needs attention")}${tab("search_filtered", "Unread")}${tab("category_filtered", "Opportunities")}${tab("grouped", "Grouped")}
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-banner ml-banner--warning" data-state="muted_banner">
        <span class="ml-banner__body">Notifications are muted until 9am. <a class="ml-link" href="${N}notification_settings/index.html">Change</a></span>
      </div>

      <div class="ml-empty" data-state="loading">
        <span class="ml-spinner" role="status" aria-label="Loading"></span>
        <p class="ml-empty__body" style="margin-top:var(--space-3)">Loading…</p>
      </div>

      ${NOTES.map((n) => `<a class="ml-notification${n.unread ? " ml-notification--unread" : ""}" data-state="${n.st} muted_banner grouped" href="${n.href}">
        <div class="ml-person-row" style="border:0;padding:0">
          <img src="${media("faces", n.face)}" alt="" loading="lazy" />
          <span class="meta">
            <b>${n.text}</b>
            <p>${n.meta}${n.pri === "high" ? " · Needs attention" : ""}</p>
          </span>
          ${n.unread ? `<span class="ml-status ml-status--accent">Unread</span>` : ""}
        </div>
      </a>`).join("\n      ")}

      <!-- Grouping: low-value repetition collapses to one row. Payments,
           bookings and security never do. -->
      <a class="ml-notification" data-state="grouped" href="/preview/flows/communication/collaborators_tab/index.html">
        <div class="ml-person-row" style="border:0;padding:0">
          <span class="ml-facepile">
            ${[2, 4, 6, 8].map((i) => `<img src="${media("faces", i)}" alt="" loading="lazy" />`).join("\n            ")}
          </span>
          <span class="meta"><b>12 people followed you</b><p>Network · today</p></span>
        </div>
      </a>

      <div class="ml-empty" data-state="push_denied">
        <h2 class="ml-empty__title">Notifications are turned off</h2>
        <p class="ml-empty__body">
          You'll still see everything here, but nothing will reach your phone. Turn them on
          in your device settings, under Apps → MusicLinkd → Notifications. We can't change it from here.
        </p>
        <a class="ml-btn ml-btn--primary" href="${N}notification_settings/index.html">Notification settings</a>
      </div>

      <div class="ml-empty" data-state="offline">
        <h2 class="ml-empty__title">You're offline</h2>
        <p class="ml-empty__body">These are the notifications we had already loaded. Anything new will arrive when you reconnect.</p>
      </div>

      <div class="ml-empty" data-state="error">
        <h2 class="ml-empty__title">We couldn't load your notifications</h2>
        <p class="ml-empty__body">Nothing has been lost — try again in a moment.</p>
        <a class="ml-btn ml-btn--primary" href="?state=populated">Retry</a>
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">Nothing new</h2>
        <p class="ml-empty__body">Messages, booking updates and opportunity news land here.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/discover/discover_feed/index.html">Go to Discover</a>
      </div>
    </main>
  </div>`,
});

writeScreen({
  flow: "notifications", screen: "notification_settings", title: "Notifications — Settings",
  body: `
  <div class="ml-app">
    ${bar("Notification settings", `${N}notifications_center/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-section"><h2>Quiet hours</h2></div>
      ${rowSwitch("Mute overnight", "No push between 10pm and 9am", true)}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>What you're told about</h2></div>
      ${rowSwitch("Messages", null, true)}
      ${rowSwitch("Connection requests", null, true)}
      ${rowSwitch("Opportunity updates", "Viewed, shortlisted, accepted", true)}
      ${rowSwitch("Booking reminders", "24 hours before a session", true)}
      ${rowSwitch("Marketplace offers", null, false)}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>More</h2></div>
      ${rowLink("By category", `${N}notification_categories/index.html`, "Which events, and on which channel")}\n      ${rowLink("Per-feed settings", `${N}notification_feeds_settings/index.html`, "A summary of the above")}
      ${rowLink("Redo the setup questions", `${N}notification_preferences_onboarding/index.html`)}
      ${rowLink("All app settings", "/preview/flows/user_settings/notification_settings/index.html")}
    </main>
  </div>`,
});

writeScreen({
  flow: "notifications", screen: "notification_feeds_settings", title: "Notifications — Feeds",
  body: `
  <div class="ml-app">
    ${bar("Per-feed settings", `${N}notification_settings/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
        Push, email, or nothing — set per category. Anything switched off here still appears in the notification centre.
      </p>
      ${[["Messages", "Push"], ["Network", "Push"], ["Opportunities", "Push + email"],
         ["Bookings", "Push + email"], ["Market", "Email only"], ["Reviews", "Nothing"]]
        .map(([cat, how]) => `<div class="ml-summary__row"><span>${cat}</span><span>${how}</span></div>`).join("\n      ")}
    </main>
  </div>`,
});

writeScreen({
  flow: "notifications", screen: "notification_preferences_onboarding", title: "Notifications — Set up",
  body: `
  <div class="ml-app">
    ${bar("Stay in the loop", "/preview/flows/onboarding/dashboard_handoff/index.html")}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
      <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
        Pick what matters now. You can change any of it later in Settings, and nothing is on by default that you did not choose.
      </p>
      ${rowSwitch("Someone messages me", null, true)}
      ${rowSwitch("An opportunity matches my roles", null, true)}
      ${rowSwitch("A booking is confirmed or changed", null, true)}
      ${rowSwitch("Offers on my listings", null, false)}
      ${rowSwitch("Weekly summary by email", null, false)}
      </div>

      <!-- The OS permission prompt is a moment, not a screen. The brief: explain
           the value BEFORE asking, and never imply the app can override a
           denial — so "denied" points at device settings and says so plainly. -->
      <div class="ml-empty" data-state="asking">
        <h2 class="ml-empty__title">Allow notifications?</h2>
        <p class="ml-empty__body">
          So you hear when someone messages you, books your service, or answers an opportunity.
          You can change any of this later.
        </p>
        <a class="ml-btn ml-btn--primary" href="?state=default">Allow notifications</a>
        <a class="ml-btn ml-btn--ghost" style="margin-top:var(--space-2)" href="${N}notifications_center/index.html">Not now</a>
      </div>

      <div class="ml-empty" data-state="denied">
        <h2 class="ml-empty__title">Notifications are turned off</h2>
        <p class="ml-empty__body">
          You'll still see everything inside MusicLinkd. To get them on your phone, turn them
          on in your device settings under Apps → MusicLinkd → Notifications. We can't change it from here.
        </p>
        <a class="ml-btn ml-btn--primary" href="${N}notifications_center/index.html">Continue without them</a>
      </div>
    </main>
    <footer class="ml-app__footer" data-state="default">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${N}notifications_center/index.html">Done</a>
    </footer>
  </div>`,
});

/* ══ FLOW 22 — CMS pages ══════════════════════════════════════════════════ */
const C = "/preview/flows/cms_pages/";

writeScreen({
  flow: "cms_pages", screen: "marketing_homepage", title: "CMS — Homepage",
  body: `
  <div class="ml-app">
    ${bar("Homepage", `${C}admin_pages_editor/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:0 0 var(--space-4)">
      <img src="${media("stage", 3)}" alt="" loading="lazy"
           style="width:100%;aspect-ratio:var(--media-aspect-card);object-fit:cover" />
      <div style="padding:var(--space-3) var(--space-4)">
        <span class="ml-eyebrow">For the industry</span>
        <h2 class="ml-type-4" style="margin:var(--space-1) 0">Find the people who make the record.</h2>
        <p class="ml-type-2" style="color:var(--color-muted)">
          Vocalists, producers, engineers, studios and venues — with rates, availability and work you can actually hear.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="/preview/flows/auth/welcome/index.html">Join MusicLinkd</a>

        <div class="ml-section" style="margin-top:var(--space-4)"><h3>What it's for</h3></div>
        ${[["Get booked", "Publish rates and availability, take deposits."],
           ["Find people", "Search by role, genre, language and city."],
           ["Buy and rent gear", "A marketplace that knows what a preamp is."]]
          .map(([t, b]) => `<div class="ml-feature">
          <b>${t}</b>
          <p>${b}</p>
        </div>`).join("\n        ")}
      </div>
    </main>
  </div>`,
});

writeScreen({
  flow: "cms_pages", screen: "about_pricing_careers", title: "CMS — About",
  body: `
  <div class="ml-app">
    ${bar("About & pricing", `${C}admin_pages_editor/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-section"><h2>About</h2></div>
      <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
        MusicLinkd is built in Mumbai for the people who actually make music here — session players,
        engineers, studios and venues who have been finding each other through group chats and word of mouth.
      </p>

      <div class="ml-section"><h2>Pricing</h2></div>
      <div class="ml-tiers">
        ${[["Free", "₹0", ["A public profile", "Apply to opportunities", "Message your connections"], false],
           ["Pro", "₹499/mo", ["Everything in Free", "Priority in Discover", "Analytics", "Direct booking"], true],
           ["Business", "₹1,499/mo", ["Everything in Pro", "Team accounts", "Room management", "Invoicing"], false]]
          .map(([name, price, feats, feat]) => `<div class="ml-tier${feat ? " ml-tier--featured" : ""}">
          <b>${name}</b>
          <span class="ml-value">${price}</span>
          ${feats.map((f) => `<p>${f}</p>`).join("\n          ")}
        </div>`).join("\n        ")}
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Careers</h2></div>
      <div class="ml-summary">
        ${[["Android engineer", "Mumbai · Full time"], ["Product designer", "Mumbai · Full time"],
           ["Community lead", "Mumbai · Contract"]]
          .map(([r, m]) => `<div class="ml-summary__row"><span>${r}</span><span>${m}</span></div>`).join("\n        ")}
      </div>
    </main>
  </div>`,
});

writeScreen({
  flow: "cms_pages", screen: "blog_changelog", title: "CMS — Blog",
  body: `
  <div class="ml-app">
    ${bar("Blog & changelog", `${C}admin_pages_editor/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${[["Rates, in the open", "28 Aug 2026", "Why we made day rates a first-class field instead of something you negotiate in DMs.", 3],
         ["The marketplace is live", "14 Aug 2026", "Buy, sell and rent gear, with offers and a deposit held until both sides are happy.", 7],
         ["Verification, explained", "02 Aug 2026", "What the badge means, what we check, and what we deliberately do not.", 11]]
        .map(([t, d, b, i]) => `<article class="ml-article">
        <img src="${media("stage", i)}" alt="" loading="lazy" />
        <div>
          <b>${t}</b>
          <span class="ml-type-1" style="color:var(--color-muted)">${d}</span>
          <p>${b}</p>
        </div>
      </article>`).join("\n      ")}
    </main>
  </div>`,
});

writeScreen({
  flow: "cms_pages", screen: "legal_pages_site", title: "CMS — Legal",
  body: `
  <div class="ml-app">
    ${bar("Legal pages", `${C}admin_pages_editor/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
        The public versions of what the app shows in Settings. Edited here, published to the website.
      </p>
      ${[["Terms of service", "Updated 14 June 2026"], ["Privacy policy", "Updated 14 June 2026"],
         ["Community guidelines", "Updated 2 August 2026"], ["Marketplace rules", "Updated 14 August 2026"]]
        .map(([t, m]) => `<div class="ml-summary__row"><span>${t}</span><span>${m}</span></div>`).join("\n      ")}
      <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-3)" href="/preview/flows/user_settings/legal_pages/index.html">See them in the app</a>
    </main>
  </div>`,
});

writeScreen({
  flow: "cms_pages", screen: "admin_pages_editor", title: "CMS — Pages",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Pages</h1>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
        Website content, edited here and published to musiclinkd.com.
      </p>
      ${rowLink("Homepage", `${C}marketing_homepage/index.html`, "Hero, features, call to action")}
      ${rowLink("About, pricing & careers", `${C}about_pricing_careers/index.html`, "3 open roles")}
      ${rowLink("Blog & changelog", `${C}blog_changelog/index.html`, "3 posts")}
      ${rowLink("Legal pages", `${C}legal_pages_site/index.html`, "4 documents")}
    </main>
  </div>`,
});

/* ══ FLOW 15 — Band ═══════════════════════════════════════════════════════ */
const B = "/preview/flows/band/";
const LINEUP = [["Ishita Rao", "Vocals", 2], ["Kabir Sheth", "Guitar", 8],
                ["Meera Nair", "Bass", 4], ["Rohan D'Souza", "Drums", 6]];

writeScreen({
  flow: "band", screen: "band_profile_layout", title: "Band — Profile",
  body: `
  <div class="ml-app">
    ${bar("The Kolaba Sessions", "/preview/flows/discover/discover_feed/index.html")}
    <main class="ml-app__body app-content--scroll" style="padding:0 0 var(--space-4)">
      <img src="${media("stage", 6)}" alt="" loading="lazy"
           style="width:100%;aspect-ratio:var(--media-aspect-card);object-fit:cover" />
      <div style="padding:var(--space-3) var(--space-4)">
        <h2 class="ml-type-3" style="margin:0">The Kolaba Sessions</h2>
        <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-1) 0 0">Four-piece indie rock · Mumbai</p>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          Guitar-led indie with a horn section when the room allows it. Playing together since 2022.
        </p>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Line-up</h3>
          <a class="ml-section__link" href="${B}ensemble_lineup/index.html">Manage</a></div>
        <div class="ml-facepile">
          ${LINEUP.map(([, , i]) => `<img src="${media("faces", i)}" alt="" loading="lazy" />`).join("\n          ")}
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Shows</h3>
          <a class="ml-section__link" href="${B}upcoming_shows/index.html">All dates</a></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>18 Aug · Antisocial, Khar</span><span>₹500</span></div>
          <div class="ml-summary__row"><span>02 Sep · The Quarter</span><span>₹700</span></div>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Live documents</h3></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Tech rider</span><span>PDF</span></div>
          <div class="ml-summary__row"><span>Stage plot</span><span>PDF</span></div>
        </div>
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="/preview/flows/communication/chat_conversation/index.html">Enquire about a date</a>
    </footer>
  </div>`,
});

writeScreen({
  flow: "band", screen: "ensemble_lineup", title: "Band — Line-up",
  body: `
  <div class="ml-app">
    ${bar("Line-up", `${B}band_profile_layout/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${LINEUP.map(([n, r, i]) => `<div class="ml-person-row">
        <img src="${media("faces", i)}" alt="" loading="lazy" />
        <span class="meta"><b>${n}</b><p>${r}</p></span>
        <button class="ml-icon-btn ml-icon-btn--muted" type="button" aria-label="Remove ${n}">${icon("cancel", { size: 18 })}</button>
      </div>`).join("\n      ")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Depping</h2></div>
      <div class="ml-person-row">
        <img src="${media("faces", 9)}" alt="" loading="lazy" />
        <span class="meta"><b>Priya Iyer</b><p>Keys · covers 18 Aug</p></span>
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="/preview/flows/communication/collaborators_tab/index.html">Add a member</a>
    </footer>
  </div>`,
});

writeScreen({
  flow: "band", screen: "upcoming_shows", title: "Band — Shows",
  body: `
  <div class="ml-app">
    ${bar("Shows", `${B}band_profile_layout/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <!-- The declared state is "hardcoded", which is a note in the registry
           that the Android screen ships a fixed list rather than reading
           Firestore. It is not a state this screen enters, so it is recorded
           here rather than rendered as one. -->
      <div data-state="hardcoded">
      <div class="ml-section"><h2>Upcoming</h2></div>
      ${[["18 Aug 2026", "Antisocial, Khar", "Doors 8pm · ₹500", "success", "Confirmed"],
         ["02 Sep 2026", "The Quarter, Lower Parel", "Doors 9pm · ₹700", "success", "Confirmed"],
         ["21 Sep 2026", "Bandra Base", "Support slot · ₹400", "warning", "Holding"]]
        .map(([d, v, m, kind, label]) => `<div class="ml-booking-card">
        <div class="ml-booking-card__body">
          <span class="ml-booking-card__title">${v}</span>
          <span class="ml-booking-card__meta">${d} · ${m}</span>
          <span class="ml-status ml-status--${kind}">${label}</span>
        </div>
      </div>`).join("\n      ")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Past</h2></div>
      ${[["12 Jul 2026", "Antisocial, Khar", "Sold out"], ["28 Jun 2026", "The Quarter", "180 in"]]
        .map(([d, v, m]) => `<div class="ml-summary__row"><span>${d} · ${v}</span><span>${m}</span></div>`).join("\n      ")}
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${B}band_profile_layout/index.html">Add a show</a>
    </footer>
  </div>`,
});

/* ══ the four singletons ══════════════════════════════════════════════════ */

/* verification_screen — a four-step wizard as four states. */
const V = "/preview/flows/verifications/";
writeScreen({
  flow: "verifications", screen: "verification_screen", title: "Verification",
  body: `
  <div class="ml-app">
    ${bar("Get verified", "/preview/flows/profile_management/profile_overview/index.html")}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <!-- Four steps, four states, one screen. A wizard whose steps are
           screens gives you a back-stack you have to manage; states give you
           one that the URL already describes. -->
      <div data-state="id_type_selector">
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Verification adds a badge to your profile and unlocks direct booking. We check that you are who you say you are — nothing else.
        </p>
        <div class="ml-section"><h2>What are you verifying with?</h2></div>
        <div role="radiogroup" aria-label="Document type">
          ${[["Aadhaar", "Most common, fastest to check", true], ["Passport", "For non-residents", false],
             ["Driving licence", "", false], ["GST certificate", "For businesses", false]]
            .map(([t, m, on]) => `<div class="ml-choice" role="radio" aria-checked="${on}" tabindex="${on ? 0 : -1}">
            <span class="ml-choice__body">
              <span class="ml-choice__title">${t}</span>
              ${m ? `<span class="ml-choice__meta">${m}</span>` : ""}
            </span>
            <span class="ml-choice__mark"></span>
          </div>`).join("\n          ")}
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=govt_id_upload">Continue</a>
      </div>

      <div data-state="govt_id_upload">
        <div class="ml-section"><h2>Upload your Aadhaar</h2></div>
        <div class="ml-dropzone">
          <span class="ml-dropzone__hint">Photograph or PDF, both sides</span>
          <span class="ml-dropzone__formats">JPG, PNG or PDF · up to 10MB</span>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Stored encrypted, seen only by the reviewer, and deleted once the check completes.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=gst_upload">Continue</a>
      </div>

      <div data-state="gst_upload">
        <div class="ml-section"><h2>Business details</h2></div>
        <div class="ml-field">
          <label for="v-gst">GST number</label>
          <input id="v-gst" type="text" placeholder="27ABCDE1234F1Z5" />
        </div>
        <div class="ml-dropzone" style="margin-top:var(--space-2)">
          <span class="ml-dropzone__hint">GST certificate</span>
          <span class="ml-dropzone__formats">PDF · up to 10MB</span>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Skip this if you are verifying as an individual.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=badge_timeline">Submit for review</a>
      </div>

      <div data-state="badge_timeline">
        <div class="ml-section"><h2>Where it's up to</h2></div>
        <ol class="ml-timeline">
          <li class="ml-timeline__item is-done"><span class="ml-timeline__title">Documents received</span>
            <span class="ml-timeline__meta">29 Aug, 14:02</span></li>
          <li class="ml-timeline__item is-current"><span class="ml-timeline__title">Under review</span>
            <span class="ml-timeline__meta">Usually two working days</span></li>
          <li class="ml-timeline__item"><span class="ml-timeline__title">Badge issued</span></li>
        </ol>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-3)" href="/preview/flows/profile_management/profile_overview/index.html">Back to profile</a>
      </div>
    </main>
  </div>`,
});

/* NOTE: saved_favorites_hub, qr_share_flow and universal_search were generated
   here originally and are now owned by scripts/screens/saved_favorites.mjs,
   qr_share.mjs and search.mjs, which build them against their full briefs.
   They were removed from this file on 2026-08-31 after running this generator
   for Notifications silently reverted all three to the earlier, simpler
   versions — two generators writing one screen means whichever ran last wins.
   One screen, one generator. */

finish("remaining: ");
