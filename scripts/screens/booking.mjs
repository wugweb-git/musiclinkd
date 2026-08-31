#!/usr/bin/env node
/**
 * Flow 8 — Booking. The four screens that declare more than one state.
 *
 * All four already switched state, but each did it with its own JS toggling
 * `#panel-<name>.style.display`, plus a local <style> block. Every one of
 * those blocks turned out to be a re-draw of a component the system already
 * ships:
 *
 *   .method-row / .radio  → .ml-choice + aria-checked
 *   .my-tabs button       → .ml-tabs__tab + aria-selected
 *   .cal-grid .day        → .ml-calendar__day + .is-today/.is-selected/.is-blocked
 *   .slot-row             → .ml-slot inside .ml-slots
 *   .booking-card         → .ml-booking-card
 *   .result-card          → .ml-result
 *
 * So this rebuild adds no CSS at all. It moves the four screens onto
 * data-state, which deletes ~90 lines of per-screen JS and four <style>
 * blocks, and it makes the tab rows real tablists driven by the shared
 * data-chip mechanism instead of hand-managed aria.
 */
import { writeScreen, backArrow, icon, assertIcons, checkMark, finish, media } from "../write-screen.mjs";

assertIcons(["calendar", "filter", "arrow-forward"]);

const P = "/preview/flows/booking/";

/* backArrow is the shared one from write-screen.mjs — these screens each had
   their own hand-inlined chevron <svg> with slightly different sizes. */
const back = (href, label) => backArrow(href, label);

/* A titled bar. Four of these screens opened with a section <h3> and had no
   <h1> at all, which audit-flow's a11y check flags: a screen's first heading
   is its name, and a screen reader's heading list should start at the top. */
const titleBar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-3" style="margin:0">${title}</h1>
    </header>`;

/* A row that leads somewhere. booking.mjs did not have one, since its earlier
   screens are all cards and forms; the provider screens are lists of choices. */
const rowLink = (label, href, meta) => `<a class="ml-action-row" href="${href}">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>`;

/* A tab whose selected state is derived from the screen state. role=tab +
   data-chip is all the shell needs; see applyState() in app-shell.js. */
const tab = (state, label) =>
  `<a class="ml-tabs__tab" role="tab" data-chip="${state}" href="?state=${state}">${label}</a>`;

/* ── my_bookings — upcoming / past / cancelled ───────────────────────────── */
const BOOKINGS = [
  { state: "upcoming", img: media("stage", 2), title: "Yash Raj Sound Studios — Tracking Room 1",
    meta: "Tue, 12 Aug 2026 · 10:00 AM – 2:00 PM", status: ["success", "Confirmed"] },
  { state: "upcoming", img: media("gear", 5), title: "Soundbox Rehearsal — Room B",
    meta: "Sat, 16 Aug 2026 · 4:00 PM – 7:00 PM", status: ["warning", "Awaiting deposit"] },
  { state: "past", img: media("stage", 8), title: "Blue Note Recording Co.",
    meta: "Fri, 18 Jul 2026 · 6:00 PM – 9:00 PM", status: [null, "Completed"] },
  { state: "past", img: media("editorial", 4), title: "Mix review — Aarav Menon",
    meta: "Mon, 30 Jun 2026 · 11:00 AM – 12:00 PM", status: [null, "Completed"] },
];

const bookingCard = (b) => `<a class="ml-booking-card" data-state="${b.state}" href="${P}booking_success/index.html">
          <img class="ml-booking-card__thumb" src="${b.img}" alt="" loading="lazy" />
          <div class="ml-booking-card__body">
            <span class="ml-booking-card__title">${b.title}</span>
            <span class="ml-booking-card__meta">${b.meta}</span>
            <span class="ml-status${b.status[0] ? ` ml-status--${b.status[0]}` : ""}">${b.status[1]}</span>
          </div>
        </a>`;

writeScreen({
  flow: "booking",
  screen: "my_bookings",
  title: "Booking — My bookings",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">My bookings</h1>
    </header>
    <nav class="ml-tabs ml-tabs--fill" role="tablist" aria-label="Booking status" style="margin:0 var(--space-4)">
      ${tab("upcoming", "Upcoming")}
      ${tab("past", "Past")}
      ${tab("cancelled", "Cancelled")}
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${BOOKINGS.map(bookingCard).join("\n      ")}

      <div class="ml-empty" data-state="cancelled">
        <h3 class="ml-empty__title">No cancelled bookings</h3>
        <p class="ml-empty__body">Bookings you cancel will be listed here, with any refund status.</p>
        <a class="ml-btn ml-btn--secondary" href="${P}studio_discovery/index.html">Find a studio</a>
      </div>
    </main>
  </div>`,
});

/* ── payment_screen — default / processing / error / success ─────────────── */
const METHODS = [
  ["UPI", "Pay from any UPI app", true],
  ["Credit / Debit card", "Visa, Mastercard, RuPay", false],
  ["Net banking", "All major banks", false],
];

writeScreen({
  flow: "booking",
  screen: "payment_screen",
  title: "Booking — Payment",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${back(`${P}booking_summary/index.html`, "Back to summary")}
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:0 var(--space-4) var(--space-4)">

      <div data-state="default">
        <h1 class="ml-type-3" style="margin:var(--space-2) 0 var(--space-1)">Pay deposit</h1>
        <p class="ml-type-1" style="color:var(--color-muted);margin-bottom:var(--space-3)">
          ₹5,000 due now · Yash Raj Sound Studios
        </p>
        <div role="radiogroup" aria-label="Payment method">
          ${METHODS.map(([t, m, on]) => `<div class="ml-choice" role="radio" aria-checked="${on}" tabindex="${on ? 0 : -1}">
            <span class="ml-choice__body">
              <span class="ml-choice__title">${t}</span>
              <span class="ml-choice__meta">${m}</span>
            </span>
            <span class="ml-choice__mark"></span>
          </div>`).join("\n          ")}
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-4)"
           href="?state=processing">Pay ₹5,000</a>
      </div>

      <div class="ml-empty" data-state="processing">
        <span class="ml-spinner" role="status" aria-label="Processing payment"></span>
        <p class="ml-empty__body" style="margin-top:var(--space-3)">Processing payment…</p>
      </div>

      <div class="ml-empty" data-state="error">
        <h3 class="ml-empty__title" style="color:var(--color-error)">Payment failed</h3>
        <p class="ml-empty__body">Your card wasn't charged. Try another method.</p>
        <a class="ml-btn ml-btn--primary" href="?state=default">Choose another method</a>
      </div>

      <div class="ml-empty" data-state="success">
        <h3 class="ml-empty__title" style="color:var(--color-success)">Payment successful</h3>
        <p class="ml-empty__body">₹5,000 paid. Your slot is confirmed.</p>
        <a class="ml-btn ml-btn--primary" href="${P}booking_success/index.html">See booking</a>
      </div>
    </main>
  </div>`,
});

/* ── availability_calendar — monthly / weekly / timeline ─────────────────── */
/* Three views of ONE dataset, not three screens: the month grid, the same
   week as bookable slots, and the day as a running order. */
const DAYS = Array.from({ length: 31 }, (_, i) => i + 1);
/* Leading blanks so day 1 lands in its real column. August 2026 opens on a
   Saturday; without the offset the grid started it under Monday, which any
   designer spots instantly. Monday-first, so Sunday is index 6. */
const MONTH_START = new Date(2026, 7, 1);          // 7 = August
const LEAD = (MONTH_START.getDay() + 6) % 7;
const BLOCKED = new Set([3, 4, 11, 17, 18, 24, 25]);
const TODAY = 12;

/* ONE day renderer, called by both calendars. They were duplicated verbatim
   and had already drifted: the customer calendar announced "3 August,
   unavailable" on a blocked day while the provider calendar announced just
   "3, dimmed button" — same data, same markup, different a11y contract. A
   shared helper is the only thing that keeps two copies of a grid in step. */
const calendarDay = (d) => {
  const cls = ["ml-calendar__day"];
  if (BLOCKED.has(d)) cls.push("is-blocked");
  if (d === TODAY) cls.push("is-today");
  /* A disabled button announces "dimmed" but not WHY, and not which month —
     the label carries the reason. */
  const label = BLOCKED.has(d) ? ` disabled aria-label="${d} August, unavailable"` : "";
  return `<button type="button" class="${cls.join(" ")}"${label}>${d}</button>`;
};

const SLOTS = [
  ["09:00 – 11:00", false], ["11:00 – 13:00", true], ["13:00 – 15:00", false],
  ["15:00 – 17:00", false], ["17:00 – 19:00", true], ["19:00 – 21:00", false],
];
const firstOpen = SLOTS.findIndex(([, taken]) => !taken);

writeScreen({
  flow: "booking",
  screen: "availability_calendar",
  title: "Booking — Availability",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${back(`${P}studio_profile/index.html`, "Back to studio")}
      <h1 class="ml-type-3" style="margin:0">Availability</h1>
    </header>
    <nav class="ml-tabs ml-tabs--fill" role="tablist" aria-label="Calendar view" style="margin:0 var(--space-4)">
      ${tab("monthly", "Month")}
      ${tab("weekly", "Week")}
      ${tab("timeline", "Day")}
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div class="ml-calendar" data-state="monthly">
        <div class="ml-calendar__head">
          <span class="ml-calendar__month">August 2026</span>
        </div>
        <div class="ml-calendar__dow">
          ${["M", "T", "W", "T", "F", "S", "S"].map((d) => `<span>${d}</span>`).join("")}
        </div>
        <div class="ml-calendar__grid">
          ${Array.from({ length: LEAD }, () => `<span></span>`).join("")}
          ${DAYS.map(calendarDay).join("\n          ")}
        </div>
        <div class="ml-calendar__legend">
          <span><i class="ml-calendar__key ml-calendar__key--sel"></i>Available</span>
          <span><i class="ml-calendar__key ml-calendar__key--blocked"></i>Booked</span>
        </div>
      </div>

      <div data-state="weekly">
        <h2 class="ml-type-2" style="margin:0 0 var(--space-2)">Wed 12 August</h2>
        <div class="ml-slots">
          ${SLOTS.map(([t, taken]) => `<button type="button" class="ml-slot"${taken ? " disabled" : ""}>${t}</button>`).join("\n          ")}
        </div>
      </div>

      <!-- .ml-timeline's contract is __title/__meta with .is-done/.is-current
           on the item; a booked slot is the "done" marker, the next open one
           is "current". -->
      <ol class="ml-timeline" data-state="timeline">
        ${SLOTS.map(([t, taken], i) => `<li class="ml-timeline__item${taken ? " is-done" : i === firstOpen ? " is-current" : ""}">
          <span class="ml-timeline__title">${taken ? "Booked — Tracking Room 1" : "Open"}</span>
          <span class="ml-timeline__meta">${t}</span>
        </li>`).join("\n        ")}
      </ol>
    </main>
  </div>`,
});

/* ── search_results — default / empty ────────────────────────────────────── */
const RESULTS = [
  { img: media("stage", 1), name: "Yash Raj Sound Studios", meta: "Andheri West · 2.4 km", trail: "₹1,800/hr" },
  { img: media("gear", 7), name: "Blue Note Recording Co.", meta: "Bandra · 4.1 km", trail: "₹2,400/hr" },
  { img: media("stage", 6), name: "Soundbox Rehearsal Rooms", meta: "Lower Parel · 6.8 km", trail: "₹900/hr" },
  { img: media("editorial", 9), name: "Third Wave Audio", meta: "Powai · 11.2 km", trail: "₹1,500/hr" },
];

writeScreen({
  flow: "booking",
  screen: "search_results",
  title: "Booking — Search results",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${back(`${P}studio_discovery/index.html`, "Back to discovery")}
      <h1 class="ml-type-3" style="margin:0">Studios near you</h1>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <p class="ml-type-1" data-state="default" style="color:var(--color-muted);margin:0 0 var(--space-2)">
        ${RESULTS.length} studios · sorted by distance
      </p>
      ${RESULTS.map((r) => `<a class="ml-result" data-state="default" href="${P}studio_profile/index.html">
        <img class="ml-result__thumb" src="${r.img}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">${r.name}</span>
          <span class="ml-result__meta">${r.meta}</span>
        </span>
        <span class="ml-result__trail">${r.trail}</span>
      </a>`).join("\n      ")}

      <div class="ml-empty" data-state="empty">
        <h3 class="ml-empty__title">No studios match those filters</h3>
        <p class="ml-empty__body">Try widening the distance, or clearing the price ceiling.</p>
        <a class="ml-btn ml-btn--primary" href="${P}filters/index.html">Adjust filters</a>
      </div>
    </main>
  </div>`,
});

/* ══ the seven single-state screens ════════════════════════════════════════
   These declare only "default", so they owed no data-state — but each still
   carried a local <style> block redrawing something the system ships:

     .studio-card / .room-card  → .ml-studio-card
     .review-card               → .ml-review-card
     .check-circle              → .ml-check
     .detail-row / .cost-row    → .ml-summary__row
     .chip-wrap                 → .ml-chip-wrap
     .switch / .toggle-row      → .ml-switch
     .eq-row / .addon-row       → .ml-check-row + .ml-check-ctl
     .range-row                 → .ml-slider
     .sheet-handle              → .ml-sheet__handle
     .studio-rail               → .ml-rail
     .gold-badge / .included-pill → .ml-status--*
     .amenity-row               → .ml-summary__row
     .mini-card                 → .ml-result
     .search-row / .promo-row / .field → .ml-field

   Rebuilding them here takes the flow to zero local CSS. ═════════════════ */

/* Rooms WITHIN this studio — not other studios. The rail was reusing the
   STUDIOS list, so "Yash Raj Sound Studios" appeared as a room inside itself. */
const ROOMS = [
  { name: "Tracking Room 1", loc: "42 m² · live room",      meta: "₹1,800/hr · Neve 1073", img: media("stage", 1), gold: true },
  { name: "Mix Suite",       loc: "18 m² · treated",        meta: "₹1,200/hr · ATC SCM25", img: media("gear", 7) },
  { name: "Booth B",         loc: "6 m² · vocal booth",     meta: "₹700/hr · U87",         img: media("gear", 12) },
];

const STUDIOS = [
  { name: "Yash Raj Sound Studios", loc: "Andheri West · 2.4 km", meta: "₹1,800/hr · Tracking, Mixing", img: media("stage", 1), gold: true },
  { name: "Blue Note Recording Co.", loc: "Bandra · 4.1 km",      meta: "₹2,400/hr · Tracking",          img: media("gear", 7) },
  { name: "Soundbox Rehearsal Rooms", loc: "Lower Parel · 6.8 km", meta: "₹900/hr · Rehearsal",          img: media("stage", 6) },
];

const studioCard = (st, href = `${P}studio_profile/index.html`) => `<a class="ml-studio-card" href="${href}">
        <div class="ml-studio-card__media"><img src="${st.img}" alt="" loading="lazy" /></div>
        <div class="ml-studio-card__body">
          <span class="ml-studio-card__name">${st.name}</span>
          <span class="ml-studio-card__loc">${st.loc}</span>
          <span class="ml-studio-card__meta">${st.meta}</span>
          ${st.gold ? `<span class="ml-status ml-status--accent">Verified</span>` : ""}
        </div>
      </a>`;

/* ── studio_discovery ───────────────────────────────────────────────────── */
writeScreen({
  flow: "booking", screen: "studio_discovery", title: "Booking — Studios",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Studios</h1>
      <span class="ml-appbar__actions">
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}my_bookings/index.html" aria-label="My bookings">${icon("calendar", { size: 20 })}</a>
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}filters/index.html" aria-label="Filters">${icon("filter", { size: 20 })}</a>
      </span>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-field">
        <label for="s-q">Find a studio</label>
        <input id="s-q" type="search" placeholder="Room, area, or service" />
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Near you</h2>
        <a class="ml-section__link" href="${P}search_results/index.html">See all</a></div>
      <div class="ml-rail">
        ${STUDIOS.map(studioCard).join("\n        ")}
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Open this week</h2></div>
      ${STUDIOS.slice(0, 2).map((st) => `<a class="ml-result" href="${P}studio_profile/index.html">
        <img class="ml-result__thumb" src="${st.img}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">${st.name}</span>
          <span class="ml-result__meta">${st.loc}</span>
        </span>
        <span class="ml-result__trail">${st.meta.split(" · ")[0]}</span>
      </a>`).join("\n      ")}
    </main>
  </div>`,
});

/* ── studio_profile ─────────────────────────────────────────────────────── */
const AMENITIES = [["Live room", "42 m²"], ["Control room", "Neve sidecar"], ["Backline", "Included"],
                   ["Parking", "2 cars"], ["Engineer", "₹800/hr extra"]];
writeScreen({
  flow: "booking", screen: "studio_profile", title: "Booking — Studio",
  body: `
  <div class="ml-app">
    ${titleBar("Studio", `${P}studio_discovery/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:0 0 var(--space-4)">
      <img src="${media("stage", 1)}" alt="" loading="lazy"
           style="width:100%;aspect-ratio:var(--media-aspect-card);object-fit:cover" />
      <div style="padding:var(--space-3) var(--space-4)">
        <h2 class="ml-type-3" style="margin:0">Yash Raj Sound Studios</h2>
        <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-1) 0 0">Andheri West, Mumbai</p>
        <div class="ml-rating" style="margin-top:var(--space-2)">
          <span class="ml-rating__score">4.8</span>
          <span class="ml-rating__count">· 62 sessions</span>
        </div>

        <!-- A room links to ITS availability, not back to this page: three
             cards all pointing at the screen you are on is a dead affordance. -->
        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Rooms</h3></div>
        <div class="ml-rail">
          ${ROOMS.map((r) => studioCard(r, `${P}availability_calendar/index.html`)).join("\n          ")}
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>The room</h3></div>
        <div class="ml-summary">
          ${AMENITIES.map(([k, v]) => `<div class="ml-summary__row"><span>${k}</span><span>${v}</span></div>`).join("\n          ")}
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Reviews</h3></div>
        <div class="ml-review-card">
          <div class="ml-review-card__head">
            <img class="ml-avatar ml-avatar--sm" src="${media("faces", 2)}" alt="" loading="lazy" />
            <b>Ishita Rao</b>
            <span class="ml-review-card__stars">★★★★★</span>
          </div>
          <p class="ml-review-card__body">Quiet live room and a patient engineer. Got three vocal takes done inside a morning.</p>
        </div>
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}availability_calendar/index.html">Check availability</a>
    </footer>
  </div>`,
});

/* ── filters ────────────────────────────────────────────────────────────── */
const FILTER_CHIPS = [["Tracking", 1], ["Mixing", 0], ["Mastering", 0], ["Rehearsal", 0],
                      ["Andheri", 1], ["Bandra", 0], ["Lower Parel", 0]];
writeScreen({
  flow: "booking", screen: "filters", title: "Booking — Filters",
  body: `
  <div class="ml-app">
    ${titleBar("Filters", `${P}studio_discovery/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-section"><h3>Service</h3></div>
      <div class="ml-chip-wrap">
        ${FILTER_CHIPS.map(([f, on]) => `<button type="button" class="ml-chip" aria-pressed="${on ? "true" : "false"}">${f}</button>`).join("\n        ")}
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h3>Hourly rate</h3>
        <span class="ml-section__link">up to ₹2,500</span></div>
      <input class="ml-slider" type="range" min="500" max="5000" step="100" value="2500" aria-label="Maximum hourly rate" />

      <div class="ml-section" style="margin-top:var(--space-3)"><h3>Distance</h3>
        <span class="ml-section__link">within 8 km</span></div>
      <input class="ml-slider" type="range" min="1" max="25" value="8" aria-label="Maximum distance in kilometres" />
    </main>
    <footer class="ml-app__footer" style="display:flex;gap:var(--space-2)">
      <button class="ml-btn ml-btn--secondary" type="reset" style="flex:1">Reset</button>
      <a class="ml-btn ml-btn--primary" style="flex:1" href="${P}search_results/index.html">Show studios</a>
    </footer>
  </div>`,
});

/* ── equipment_screen ───────────────────────────────────────────────────── */
const GEAR = [["Neumann U87", "Included", true], ["Neve 1073 preamp", "Included", true],
              ["Drum kit (Yamaha)", "₹1,200/session", false], ["Guitar amp (Fender)", "₹600/session", false],
              ["Grand piano", "₹2,000/session", false]];
writeScreen({
  flow: "booking", screen: "equipment_screen", title: "Booking — Equipment",
  body: `
  <div class="ml-app">
    ${titleBar("Equipment", `${P}configure_session/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-section"><h3>Included with the room</h3></div>
      ${GEAR.filter((g) => g[2]).map(([n, p]) => `<label class="ml-check-row">
        <span class="ml-check-ctl" role="checkbox" aria-checked="true" aria-disabled="true"></span>
        <span class="ml-choice__body"><span class="ml-choice__title">${n}</span><span class="ml-choice__meta">${p}</span></span>
      </label>`).join("\n      ")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h3>Add to the session</h3></div>
      ${GEAR.filter((g) => !g[2]).map(([n, p]) => `<label class="ml-check-row">
        <span class="ml-check-ctl" role="checkbox" aria-checked="false"></span>
        <span class="ml-choice__body"><span class="ml-choice__title">${n}</span><span class="ml-choice__meta">${p}</span></span>
      </label>`).join("\n      ")}
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}booking_summary/index.html">Continue</a>
    </footer>
  </div>`,
});

/* ── configure_session ──────────────────────────────────────────────────── */
const ADDONS = [["Engineer", "₹800/hr", true], ["Assistant", "₹400/hr", false], ["Recorded video", "₹3,500", false]];
writeScreen({
  flow: "booking", screen: "configure_session", title: "Booking — Configure",
  body: `
  <div class="ml-app">
    ${titleBar("Session", `${P}availability_calendar/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-section"><h3>Session type</h3></div>
      <div class="ml-chip-wrap">
        ${[["Tracking", 1], ["Mixing", 0], ["Rehearsal", 0]].map(([t, on]) =>
          `<button type="button" class="ml-chip" aria-pressed="${on ? "true" : "false"}">${t}</button>`).join("\n        ")}
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h3>Add-ons</h3></div>
      ${ADDONS.map(([n, p, on]) => `<div class="ml-check-row">
        <span class="ml-choice__body"><span class="ml-choice__title">${n}</span><span class="ml-choice__meta">${p}</span></span>
        <button class="ml-switch" type="button" role="switch" aria-checked="${on}" aria-label="${n}"></button>
      </div>`).join("\n      ")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h3>Equipment</h3>
        <a class="ml-section__link" href="${P}equipment_screen/index.html">Choose</a></div>
      <p class="ml-type-1" style="color:var(--color-muted);margin:0">Neumann U87 and Neve 1073 are included.</p>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}booking_summary/index.html">Review booking</a>
    </footer>
  </div>`,
});

/* ── booking_summary ────────────────────────────────────────────────────── */
writeScreen({
  flow: "booking", screen: "booking_summary", title: "Booking — Summary",
  body: `
  <div class="ml-app">
    ${titleBar("Review booking", `${P}configure_session/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <a class="ml-result" href="${P}studio_profile/index.html">
        <img class="ml-result__thumb" src="${media("stage", 1)}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">Yash Raj Sound Studios</span>
          <span class="ml-result__meta">Tracking Room 1 · Wed 12 Aug, 10:00–14:00</span>
        </span>
      </a>

      <div class="ml-summary" style="margin-top:var(--space-3)">
        <div class="ml-summary__row"><span>Room · 4 hrs</span><span>₹7,200</span></div>
        <div class="ml-summary__row"><span>Engineer · 4 hrs</span><span>₹3,200</span></div>
        <div class="ml-summary__row"><span>Drum kit</span><span>₹1,200</span></div>
        <div class="ml-summary__row"><span>Promo code MONSOON</span><span>−₹1,600</span></div>
        <div class="ml-summary__row ml-summary__row--total"><span>Total</span><span>₹10,000</span></div>
        <p class="ml-summary__note">₹5,000 deposit due now, the balance on the day.</p>
      </div>

      <div class="ml-field" style="margin-top:var(--space-3)">
        <label for="promo">Promo code</label>
        <input id="promo" type="text" value="MONSOON" />
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}payment_screen/index.html">Pay deposit</a>
    </footer>
  </div>`,
});

/* ── booking_success ────────────────────────────────────────────────────── */
writeScreen({
  flow: "booking", screen: "booking_success", title: "Booking — Confirmed",
  body: `
  <div class="ml-app">
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-4)">
      <div class="ml-empty">
        ${checkMark}
        <h1 class="ml-empty__title">Booking confirmed</h1>
        <p class="ml-empty__body">Yash Raj Sound Studios, Tracking Room 1 — Wednesday 12 August, 10:00–14:00.</p>
      </div>

      <div class="ml-summary" style="margin-top:var(--space-3)">
        <div class="ml-summary__row"><span>Reference</span><span>ML-4471</span></div>
        <div class="ml-summary__row"><span>Deposit paid</span><span>₹5,000</span></div>
        <div class="ml-summary__row"><span>Balance on the day</span><span>₹5,000</span></div>
      </div>
    </main>
    <footer class="ml-app__footer" style="display:flex;gap:var(--space-2)">
      <a class="ml-btn ml-btn--secondary" style="flex:1" href="${P}studio_discovery/index.html">Book another</a>
      <a class="ml-btn ml-btn--primary" style="flex:1" href="${P}my_bookings/index.html">My bookings</a>
    </footer>
  </div>`,
});

/* ══ the approved plan's Booking +3 — the provider side ════════════════════
   The eleven screens above are all customer-side. A studio owner answering a
   request had nowhere to stand. These three are that place, and each owns a
   route. Lifecycle stays as states, as it already is on the customer side. ══ */

writeScreen({
  flow: "booking", screen: "provider_dashboard", title: "Booking — Provider",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Your rooms</h1>
      <span class="ml-appbar__actions">
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}provider_availability/index.html" aria-label="Manage availability">${icon("calendar", { size: 20 })}</a>
      </span>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="default">
        <div class="ml-section"><h2>Needs an answer</h2></div>
        <a class="ml-action-row" href="${P}provider_requests/index.html">
          <span class="ml-choice__body">
            <span class="ml-choice__title">3 booking requests</span>
            <span class="ml-choice__meta">Oldest waiting 14 hours — they can't plan until you reply</span>
          </span>
          <span class="ml-status ml-status--warning">Answer</span>
        </a>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Today</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>10:00 · Tracking Room 1</span><span>Ishita Rao</span></div>
          <div class="ml-summary__row"><span>15:00 · Mix Suite</span><span>Kabir Sheth</span></div>
          <div class="ml-summary__row"><span>19:00 · Booth B</span><span>Free</span></div>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>This week</h2></div>
        <div class="ml-stat-grid">
          <div class="ml-kpi"><span class="ml-kpi__num">14</span>Sessions</div>
          <div class="ml-kpi"><span class="ml-kpi__num">68%</span>Rooms used</div>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Manage</h2></div>
        ${rowLink("Availability and blocked dates", `${P}provider_availability/index.html`)}
        ${rowLink("Rates and services", "/preview/flows/services_availability/rate_pricing_editor/index.html", "Owned by Services")}
      </div>

      <div class="ml-empty" data-state="quiet">
        <h2 class="ml-empty__title">Nothing booked today</h2>
        <p class="ml-empty__body">No requests waiting either. A good day to put the rooms in front of people.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/business_profiles/business_overview/index.html">Check your public profile</a>
      </div>
    </main>
  </div>`,
});

const REQUESTS = [
  ["Ishita Rao", "Tracking Room 1 · Wed 12 Aug, 10:00–14:00", "₹7,200", 2, "14 hours ago"],
  ["Kabir Sheth", "Mix Suite · Fri 14 Aug, 15:00–19:00", "₹4,800", 8, "5 hours ago"],
  ["Meera Nair", "Booth B · Sat 15 Aug, 11:00–13:00", "₹1,400", 4, "1 hour ago"],
];

writeScreen({
  flow: "booking", screen: "provider_requests", title: "Booking — Requests",
  body: `
  <div class="ml-app">
    ${titleBar("Requests", `${P}provider_dashboard/index.html`)}
    <nav class="ml-tabs ml-tabs--fill" role="tablist" aria-label="Requests" style="margin:0 var(--space-4)">
      <a class="ml-tabs__tab" role="tab" data-chip="pending" href="?state=pending">Waiting</a>
      <a class="ml-tabs__tab" role="tab" data-chip="accepted" href="?state=accepted">Accepted</a>
      <a class="ml-tabs__tab" role="tab" data-chip="declined" href="?state=declined">Declined</a>
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="pending">
        ${REQUESTS.map(([n, what, amt, i, ago]) => `<div class="ml-booking-card">
          <img class="ml-booking-card__thumb" src="${media("faces", i)}" alt="" loading="lazy" />
          <div class="ml-booking-card__body">
            <span class="ml-booking-card__title">${n}</span>
            <span class="ml-booking-card__meta">${what}</span>
            <span class="ml-booking-card__meta">${amt} · asked ${ago}</span>
            <div style="display:flex;gap:var(--space-2);margin-top:var(--space-2)">
              <a class="ml-btn ml-btn--secondary" style="flex:1" href="?state=declined">Decline</a>
              <a class="ml-btn ml-btn--primary" style="flex:1" href="?state=accepted">Accept</a>
            </div>
          </div>
        </div>`).join("\n        ")}
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Requests expire after 48 hours if nobody answers, and the person is told why.
        </p>
      </div>

      <div data-state="accepted">
        <div class="ml-banner ml-banner--success">
          <span class="ml-banner__body">Accepted. The room is held and the deposit has been taken.</span>
        </div>
        ${REQUESTS.slice(0, 2).map(([n, what, amt, i]) => `<div class="ml-booking-card">
          <img class="ml-booking-card__thumb" src="${media("faces", i)}" alt="" loading="lazy" />
          <div class="ml-booking-card__body">
            <span class="ml-booking-card__title">${n}</span>
            <span class="ml-booking-card__meta">${what}</span>
            <span class="ml-status ml-status--success">Confirmed · ${amt}</span>
          </div>
        </div>`).join("\n        ")}
      </div>

      <div data-state="declined">
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Declined requests keep their reason, so you can see whether you are turning away the
          same kind of work repeatedly.
        </p>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Meera Nair · 15 Aug</span><span>Room already booked</span></div>
          <div class="ml-summary__row"><span>Rohan D'Souza · 9 Aug</span><span>Outside opening hours</span></div>
        </div>
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No requests waiting</h2>
        <p class="ml-empty__body">Everything has been answered.</p>
        <a class="ml-btn ml-btn--primary" href="${P}provider_dashboard/index.html">Back to your rooms</a>
      </div>
    </main>
  </div>`,
});

writeScreen({
  flow: "booking", screen: "provider_availability", title: "Booking — Availability",
  body: `
  <div class="ml-app">
    ${titleBar("Availability", `${P}provider_dashboard/index.html`)}
    <nav class="ml-tabs ml-tabs--fill" role="tablist" aria-label="Availability" style="margin:0 var(--space-4)">
      <a class="ml-tabs__tab" role="tab" data-chip="default" href="?state=default">Calendar</a>
      <a class="ml-tabs__tab" role="tab" data-chip="hours" href="?state=hours">Hours</a>
      <a class="ml-tabs__tab" role="tab" data-chip="blocked" href="?state=blocked">Blocked</a>
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div class="ml-calendar" data-state="default">
        <div class="ml-calendar__head"><span class="ml-calendar__month">August 2026</span></div>
        <div class="ml-calendar__dow">${["M", "T", "W", "T", "F", "S", "S"].map((d) => `<span>${d}</span>`).join("")}</div>
        <div class="ml-calendar__grid">
          ${Array.from({ length: LEAD }, () => `<span></span>`).join("")}
          ${DAYS.map(calendarDay).join("\n          ")}
        </div>
        <div class="ml-calendar__legend">
          <span><i class="ml-calendar__key ml-calendar__key--sel"></i>Bookable</span>
          <span><i class="ml-calendar__key ml-calendar__key--blocked"></i>Booked or blocked</span>
        </div>
      </div>

      <div data-state="hours">
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          When the rooms can be booked. Separate from when the building is open.
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

      <div data-state="blocked">
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Dates nobody can book, whatever the hours say. Maintenance, holidays, a session you took offline.
        </p>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>3–4 August</span><span>Maintenance</span></div>
          <div class="ml-summary__row"><span>17–18 August</span><span>Closed</span></div>
          <div class="ml-summary__row"><span>24–25 August</span><span>Private session</span></div>
        </div>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-3)" href="?state=default">Block more dates</a>
      </div>

      <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-3)">
        Rates live in Services, which owns them —
        <a class="ml-link" href="/preview/flows/services_availability/rate_pricing_editor/index.html">edit rates</a>.
      </p>
    </main>
  </div>`,
});

finish("booking: ");
