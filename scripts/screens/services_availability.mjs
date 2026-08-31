#!/usr/bin/env node
/**
 * Flow 11 — Services & Availability. Five screens, nine declared states, all
 * five the raw scaffold.
 *
 * The spec is explicit that there is ONE canonical availability editor, not
 * one per service, and that is honoured here: availability_toggle owns the
 * five availability statuses as five states of a single control, and the other
 * four screens edit what is offered and what it costs.
 *
 * availability_toggle's five states — available_now, open_for_gigs,
 * booked_soon, on_tour, unavailable — are the clearest possible case for
 * states over screens: one status control, five positions, each with different
 * consequences spelled out beneath it.
 *
 * No new CSS.
 */
import { writeScreen, backArrow, icon, assertIcons, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/services_availability/";
assertIcons(["arrow-forward", "add", "check"]);

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-3" style="margin:0">${title}</h1>
    </header>`;

/* A row that leads somewhere. */
const rowLink = (label, href, meta) => `<a class="ml-action-row" href="${href}">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>`;

const field = (id, label, type, value, ph) => `<div class="ml-field">
        <label for="${id}">${label}</label>
        ${type === "textarea"
          ? `<textarea id="${id}" rows="3"${ph ? ` placeholder="${ph}"` : ""}>${value || ""}</textarea>`
          : type === "select"
            ? `<select id="${id}">${value.map((o) => `<option>${o}</option>`).join("")}</select>`
            : `<input id="${id}" type="${type}" value="${value || ""}"${ph ? ` placeholder="${ph}"` : ""} />`}
      </div>`;

/* ── availability_toggle — one control, five statuses ───────────────────── */
const STATUSES = [
  ["available_now", "Available now", "success", "You appear at the top of Discover and can be booked today."],
  ["open_for_gigs", "Open for gigs", "accent", "You appear in Discover and in opportunity matches, with normal priority."],
  ["booked_soon", "Booked soon", "warning", "You still appear, marked as filling up. Requests are allowed but flagged."],
  ["on_tour", "On tour", "neutral", "You appear with your tour dates. Booking requests are held until you return."],
  ["unavailable", "Unavailable", "error", "Hidden from Discover. People with your link can still see your profile."],
];

writeScreen({
  flow: "services_availability", screen: "availability_toggle", title: "Services — Availability",
  body: `
  <div class="ml-app">
    ${bar("Availability", `${P}service_offerings_editor/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
        One status, and it changes how you appear everywhere else. Pick the one that is true today.
      </p>

      <!-- Five statuses, one control. The chip that reads as selected is
           derived from the screen state, so each ?state= shows a real,
           different consequence rather than the same screen five times. -->
      <div class="ml-chip-wrap">
        ${STATUSES.map(([st, label]) =>
          `<a class="ml-chip" data-chip="${st}" href="?state=${st}">${label}</a>`).join("\n        ")}
      </div>

      ${STATUSES.map(([st, label, kind, what]) => `<div data-state="${st}" style="margin-top:var(--space-3)">
        <span class="ml-status ml-status--${kind}">${label}</span>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-2)">${what}</p>
      </div>`).join("\n      ")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Working week</h2></div>
      ${["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
        .map((d, i) => `<div class="ml-check-row">
        <span class="ml-choice__body"><span class="ml-choice__title">${d}</span>
        <span class="ml-choice__meta">${i > 4 ? "By arrangement" : "10:00 – 19:00"}</span></span>
        <button class="ml-switch" type="button" role="switch" aria-checked="${i <= 4}" aria-label="${d}"></button>
      </div>`).join("\n      ")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Blocked dates</h2>
        <a class="ml-section__link" href="/preview/flows/booking/availability_calendar/index.html">Calendar</a></div>
      <div class="ml-summary">
        <div class="ml-summary__row"><span>17–18 August</span><span>On tour</span></div>
        <div class="ml-summary__row"><span>24–25 August</span><span>Personal</span></div>
      </div>
    </main>
  </div>`,
});

/* ── service_offerings_editor ───────────────────────────────────────────── */
const OFFERS = [
  ["Studio session", "Full day, in person", "₹12,000/day", true],
  ["Remote vocal", "Stems within 5 days", "₹6,000/track", true],
  ["Topline writing", "Melody and lyric", "₹15,000", true],
  ["Live performance", "Up to 60 minutes", "₹25,000", false],
];

writeScreen({
  flow: "services_availability", screen: "service_offerings_editor", title: "Services — Offerings",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Services</h1>
      <span class="ml-appbar__actions">
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}session_configuration/index.html" aria-label="Add a service">${icon("add", { size: 20 })}</a>
      </span>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <a class="ml-action-row" href="${P}availability_toggle/index.html">
        <span class="ml-choice__body">
          <span class="ml-choice__title">Availability</span>
          <span class="ml-choice__meta">Open for gigs · Mon–Fri</span>
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>What you offer</h2>
        <a class="ml-section__link" href="${P}my_services/index.html">All services</a></div>
      ${OFFERS.map(([n, m, p, live]) => `<div class="ml-check-row">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${n}</span>
          <span class="ml-choice__meta">${m} · ${p}</span>
        </span>
        <button class="ml-switch" type="button" role="switch" aria-checked="${live}" aria-label="${n}"></button>
      </div>`).join("\n      ")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Your room</h2></div>
      <a class="ml-action-row" href="${P}studio_services_amenities/index.html">
        <span class="ml-choice__body">
          <span class="ml-choice__title">Amenities</span>
          <span class="ml-choice__meta">What the room provides without being asked</span>
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Pricing</h2>
        <a class="ml-section__link" href="${P}rate_pricing_editor/index.html">Edit rates</a></div>
      <p class="ml-type-1" style="color:var(--color-muted);margin:0">
        Rates shown publicly can be turned off in Privacy without unpublishing the service.
      </p>
    </main>
  </div>`,
});

/* ── rate_pricing_editor ────────────────────────────────────────────────── */
writeScreen({
  flow: "services_availability", screen: "rate_pricing_editor", title: "Services — Rates",
  body: `
  <div class="ml-app">
    ${bar("Rates", `${P}service_offerings_editor/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-section"><h2>Base rates</h2></div>
      ${field("r-day", "Day rate (₹)", "text", "12000")}
      ${field("r-half", "Half day (₹)", "text", "7000")}
      ${field("r-track", "Per track (₹)", "text", "6000")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Adjustments</h2></div>
      <div class="ml-check-row">
        <span class="ml-choice__body"><span class="ml-choice__title">Travel outside Mumbai</span>
        <span class="ml-choice__meta">Charged at cost, agreed up front</span></span>
        <button class="ml-switch" type="button" role="switch" aria-checked="true" aria-label="Travel outside Mumbai"></button>
      </div>
      <div class="ml-check-row">
        <span class="ml-choice__body"><span class="ml-choice__title">Rush turnaround</span>
        <span class="ml-choice__meta">+25% for delivery under 48 hours</span></span>
        <button class="ml-switch" type="button" role="switch" aria-checked="true" aria-label="Rush turnaround"></button>
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Deposit</h2></div>
      ${field("r-dep", "Deposit taken on booking", "select", ["50% of the total", "25% of the total", "A flat ₹5,000", "No deposit"])}
      ${field("r-cancel", "Free cancellation window", "select", ["48 hours", "24 hours", "7 days", "No free cancellation"])}
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}service_offerings_editor/index.html">Save rates</a>
    </footer>
  </div>`,
});

/* ── session_configuration ──────────────────────────────────────────────── */
writeScreen({
  flow: "services_availability", screen: "session_configuration", title: "Services — New service",
  body: `
  <div class="ml-app">
    ${bar("New service", `${P}service_offerings_editor/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${field("s-name", "Service name", "text", "", "Studio session")}
      ${field("s-kind", "Type", "select", ["In person", "Remote", "Either"])}
      ${field("s-len", "Typical length", "select", ["Full day", "Half day", "Per track", "Per hour"])}
      ${field("s-price", "Price (₹)", "text", "", "12000")}
      ${field("s-desc", "What's included", "textarea", "", "What the person booking actually gets.")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Add-ons</h2></div>
      ${[["Engineer", "₹800/hr"], ["Extra revision", "₹2,000"], ["Stems delivery", "Free"]]
        .map(([n, p]) => `<div class="ml-check-row">
        <span class="ml-choice__body"><span class="ml-choice__title">${n}</span>
        <span class="ml-choice__meta">${p}</span></span>
        <span class="ml-check-ctl" role="checkbox" aria-checked="false"></span>
      </div>`).join("\n      ")}
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}service_offerings_editor/index.html">Publish service</a>
    </footer>
  </div>`,
});

/* ── studio_services_amenities ──────────────────────────────────────────── */
writeScreen({
  flow: "services_availability", screen: "studio_services_amenities", title: "Services — Amenities",
  body: `
  <div class="ml-app">
    ${bar("Amenities", `${P}service_offerings_editor/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
        What a room provides without being asked. These show on your public profile and in search filters.
      </p>
      ${[["The room", ["Air conditioning", "Natural light", "Step-free access", "Green room"]],
         ["Technical", ["Engineer on site", "Backline provided", "Headphone mixes", "Talkback"]],
         ["Practical", ["Parking", "Loading bay", "Kitchen", "Open past midnight"]]]
        .map(([group, items]) => `<div class="ml-section" style="margin-top:var(--space-3)"><h2>${group}</h2></div>
      ${items.map((it, i) => `<label class="ml-check-row">
        <span class="ml-check-ctl" role="checkbox" aria-checked="${i < 2}"></span>
        <span class="ml-choice__body"><span class="ml-choice__title">${it}</span></span>
      </label>`).join("\n      ")}`).join("\n      ")}
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}service_offerings_editor/index.html">Save amenities</a>
    </footer>
  </div>`,
});

/* ══ the approved plan's Services +3, part built ═══════════════════════════
   my_services lists what a provider offers, with publication state.
   service_offerings_editor edits the set inline and session_configuration adds
   one — those two already existed, so this is the third and last. ══════════ */

writeScreen({
  flow: "services_availability", screen: "my_services", title: "Services — Mine",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Your services</h1>
      <span class="ml-appbar__actions">
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}session_configuration/index.html" aria-label="Add a service">${icon("add", { size: 20 })}</a>
      </span>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="default">
        ${[["Studio session", "Full day, in person", "₹12,000/day", "success", "Live", "9 bookings"],
           ["Remote vocal", "Stems within 5 days", "₹6,000/track", "success", "Live", "22 bookings"],
           ["Topline writing", "Melody and lyric", "₹15,000", "success", "Live", "3 bookings"],
           ["Live performance", "Up to 60 minutes", "₹25,000", "warning", "Paused", "1 booking"]]
          .map(([n, m, p, kind, st, count]) => `<a class="ml-service" href="${P}session_configuration/index.html">
          <span class="ml-choice__body">
            <span class="ml-choice__title">${n}</span>
            <span class="ml-choice__meta">${m} · ${count}</span>
          </span>
          <span class="ml-result__trail">${p}</span>
          <span class="ml-status ml-status--${kind}">${st}</span>
        </a>`).join("\n        ")}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Also</h2></div>
        ${rowLink("Availability", `${P}availability_toggle/index.html`, "Open for gigs · Mon–Fri")}
        ${rowLink("Rates and deposits", `${P}rate_pricing_editor/index.html`)}
      </div>

      <div data-state="paused">
        <div class="ml-banner ml-banner--warning">
          <span class="ml-banner__body">All your services are paused. Nobody can book you.</span>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          Your profile stays visible and people can still message you. Pausing is not the same as
          being unavailable — that lives in Availability.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=default">Unpause everything</a>
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No services yet</h2>
        <p class="ml-empty__body">People can't book what isn't listed. One service with an honest price is enough to start.</p>
        <a class="ml-btn ml-btn--primary" href="${P}session_configuration/index.html">Add a service</a>
      </div>
    </main>
  </div>`,
});

finish("services_availability: ");
