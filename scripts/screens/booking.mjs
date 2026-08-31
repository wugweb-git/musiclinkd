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
import { writeScreen, backArrow, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/booking/";

/* backArrow is the shared one from write-screen.mjs — these screens each had
   their own hand-inlined chevron <svg> with slightly different sizes. */
const back = (href, label) => backArrow(href, label);

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
const BLOCKED = new Set([3, 4, 11, 17, 18, 24, 25]);
const TODAY = 12;

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
      <h1 class="ml-type-2" style="margin:0">Availability</h1>
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
          ${DAYS.map((d) => {
            const cls = ["ml-calendar__day"];
            if (BLOCKED.has(d)) cls.push("is-blocked");
            if (d === TODAY) cls.push("is-today");
            return `<button type="button" class="${cls.join(" ")}"${BLOCKED.has(d) ? " disabled aria-label=\"" + d + " August, unavailable\"" : ""}>${d}</button>`;
          }).join("\n          ")}
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
      <h1 class="ml-type-2" style="margin:0">Studios near you</h1>
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

finish("booking: ");
