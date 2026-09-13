#!/usr/bin/env node
/**
 * Flow 13 — Notifications. BETA.
 *
 * Moved into its own file on 2026-08-31. It had lived in remaining.mjs, which
 * a bad removal pass gutted: the generated screens survived on disk but lost
 * their generator, so they could not be rebuilt. One screen, one generator —
 * and the generator lives with its own flow.
 *
 * The brief's SCREEN 06-16 are eleven notification TYPES. They are one list
 * filtered by category, so they are states. Priority is a state too: "Needs
 * attention" is the same list showing only what cannot wait. Grouping applies
 * to low-value repetition only — payments, bookings and security events each
 * carry independent consequences and are never collapsed.
 */
import { writeScreen, backArrow, icon, assertIcons, finish, media } from "../write-screen.mjs";

assertIcons(["arrow-forward", "search", "add", "check", "cancel", "bell", "send", "heart"]);

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-4" style="margin:0">${title}</h1>
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
      <h1 class="ml-type-4" style="margin:0">Notifications</h1>
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
      <p class="ml-type-3" style="color:var(--color-muted);margin:0 0 var(--space-3)">
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
      <p class="ml-type-3" style="color:var(--color-muted);margin:0 0 var(--space-3)">
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

/* ── notification_detail — only for notifications carrying a DECISION ──────
   Most notifications go straight to the flow that owns them. This screen
   exists for the handful where accepting or declining from a list row would
   be too easy to do by accident. It shows the context and hands off; it does
   NOT reproduce the booking or project page. */
const DECISIONS = [
  { st: "booking", who: "Ishita Rao", face: 2, kind: "Booking request",
    title: "Tracking Room 1 — Wed 12 August", meta: "10:00–14:00 · 4 hours · ₹7,200",
    rows: [["Room", "Tracking Room 1"], ["When", "Wed 12 Aug, 10:00–14:00"], ["Rate", "₹1,800/hr"], ["Deposit", "₹5,000 on confirmation"]],
    yes: ["Accept request", "/preview/flows/booking/my_bookings/index.html"],
    no: ["Decline", "/preview/flows/notifications/notifications_center/index.html"] },
  { st: "collaboration", who: "Kabir Sheth", face: 8, kind: "Collaboration invitation",
    title: "Neon Monsoon", meta: "Lead vocal · credited · delivery by 20 Sept",
    rows: [["Your role", "Lead vocal"], ["Credit", "Yes, on release"], ["Deadline", "20 September"], ["Fee", "₹15,000"]],
    yes: ["Accept", "/preview/flows/portfolio/portfolio_dashboard/index.html"],
    no: ["Decline", "/preview/flows/notifications/notifications_center/index.html"] },
  { st: "opportunity", who: "Octave Sound Labs", face: 5, kind: "Application shortlisted",
    title: "Session vocalist — Hindi pop EP", meta: "You're on the shortlist. They may message you next.",
    rows: [["Rate", "₹12,000/day"], ["Duration", "2 days"], ["Where", "Andheri West, Mumbai"]],
    yes: ["View opportunity", "/preview/flows/opportunities/opportunity_detail/index.html"],
    no: ["My applications", "/preview/flows/opportunities/my_applications/index.html"] },
  { st: "marketplace", who: "Aarav Menon", face: 8, kind: "Counter offer",
    title: "Shure SM7B Microphone", meta: "You offered ₹24,000. They countered at ₹26,000.",
    rows: [["Asking", "₹28,500"], ["Your offer", "₹24,000"], ["Their counter", "₹26,000"], ["Expires", "In 41 hours"]],
    yes: ["Accept ₹26,000", "/preview/flows/marketplace/marketplace_order_status/index.html"],
    no: ["Counter again", "/preview/flows/marketplace/marketplace_make_offer/index.html"] },
  { st: "payment", who: "MusicLinkd", face: 1, kind: "Payment failed",
    title: "₹5,000 deposit could not be taken", meta: "Your card was not charged. The booking is held for 24 hours.",
    rows: [["Booking", "Yash Raj, Wed 12 Aug"], ["Amount", "₹5,000"], ["Held until", "Tomorrow, 09:00"]],
    yes: ["Try another method", "/preview/flows/booking/payment_screen/index.html"],
    no: ["View booking", "/preview/flows/booking/my_bookings/index.html"] },
];

writeScreen({
  flow: "notifications", screen: "notification_detail", title: "Notifications — Detail",
  body: `
  <div class="ml-app">
    ${bar("Notification", `${N}notifications_center/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${DECISIONS.map((d) => `<div data-state="${d.st}">
        <div class="ml-person-row">
          <img src="${media("faces", d.face)}" alt="" loading="lazy" />
          <span class="meta"><b>${d.who}</b><p>${d.kind}</p></span>
        </div>
        <h2 class="ml-type-4" style="margin:var(--space-3) 0 var(--space-1)">${d.title}</h2>
        <p class="ml-type-3" style="color:var(--color-muted);margin:0">${d.meta}</p>
        <div class="ml-summary" style="margin-top:var(--space-3)">
          ${d.rows.map(([k, v]) => `<div class="ml-summary__row"><span>${k}</span><span>${v}</span></div>`).join("\n          ")}
        </div>
        <div style="display:flex;gap:var(--space-2);margin-top:var(--space-3)">
          <a class="ml-btn ml-btn--secondary" style="flex:1" href="${d.no[1]}">${d.no[0]}</a>
          <a class="ml-btn ml-btn--primary" style="flex:1" href="${d.yes[1]}">${d.yes[0]}</a>
        </div>
      </div>`).join("\n      ")}

      <div class="ml-empty" data-state="expired">
        <h2 class="ml-empty__title">This is no longer available</h2>
        <p class="ml-empty__body">The opportunity closed, or the offer expired, before you got to it.</p>
        <a class="ml-btn ml-btn--primary" href="${N}notifications_center/index.html">Back to notifications</a>
      </div>

      <div class="ml-empty" data-state="entity_deleted">
        <h2 class="ml-empty__title">This content is no longer available</h2>
        <p class="ml-empty__body">It may have been deleted by whoever posted it.</p>
        <a class="ml-btn ml-btn--primary" href="${N}notifications_center/index.html">Back to notifications</a>
      </div>

      <div class="ml-empty" data-state="permission_denied">
        <h2 class="ml-empty__title">You don't have access to this</h2>
        <p class="ml-empty__body">Its visibility changed after you were notified.</p>
        <a class="ml-btn ml-btn--primary" href="${N}notifications_center/index.html">Back to notifications</a>
      </div>
    </main>
  </div>`,
});

/* ── notification_categories — the category being configured IS the state ── */
const CATEGORIES = [
  ["messages", "Messages", [
    ["A new message", "push", true], ["A message request", "push", true], ["Read receipts", "off", false],
  ]],
  ["bookings", "Bookings", [
    ["Request received", "both", true], ["Confirmed or declined", "both", true],
    ["Rescheduled or cancelled", "both", true], ["Reminder 24 hours before", "push", true],
    ["Payment problems", "both", true],
  ]],
  ["marketplace", "Market", [
    ["Someone offers on my listing", "push", true], ["My offer is answered", "push", true],
    ["Listing sold or rented", "email", true], ["Price drops on saved items", "off", false],
  ]],
  ["opportunities", "Opportunities", [
    ["An opportunity matches my roles", "push", true], ["My application is viewed", "off", false],
    ["Shortlisted or accepted", "both", true], ["Deadline approaching", "push", true],
  ]],
  ["system", "System", [
    ["Security and sign-ins", "both", true], ["Account and policy changes", "email", true],
    ["Product updates", "off", false], ["Offers and promotions", "off", false],
  ]],
];

writeScreen({
  flow: "notifications", screen: "notification_categories", title: "Notifications — Categories",
  body: `
  <div class="ml-app">
    ${bar("By category", `${N}notification_settings/index.html`)}
    <nav class="ml-tabs" role="tablist" aria-label="Category" style="margin:0 var(--space-4)">
      ${CATEGORIES.map(([st, l]) => tab(st, l)).join("")}
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        <p class="ml-type-3" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Choose what you're told about, and how. Anything switched off here still appears
          in your notification list — it just doesn't reach your phone or inbox.
        </p>
        ${CATEGORIES.map(([st, l, rows]) => rowLink(l, `?state=${st}`,
          `${rows.filter((r) => r[2]).length} of ${rows.length} on`)).join("\n        ")}
      </div>

      ${CATEGORIES.map(([st, label, rows]) => `<div data-state="${st}">
        <div class="ml-section"><h2>${label}</h2></div>
        ${rows.map(([t, ch, on]) => `<div class="ml-check-row">
          <span class="ml-choice__body">
            <span class="ml-choice__title">${t}</span>
            <span class="ml-choice__meta">${ch === "both" ? "Push and email" : ch === "push" ? "Push" : ch === "email" ? "Email" : "In the app only"}</span>
          </span>
          <button class="ml-switch" type="button" role="switch" aria-checked="${on}" aria-label="${t}"></button>
        </div>`).join("\n        ")}
        ${st === "system" ? `<p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Security notifications can't be turned off entirely — you'll always be told about a new sign-in.
        </p>` : ""}
      </div>`).join("\n      ")}
    </main>
  </div>`,
});

finish("notifications: ");
