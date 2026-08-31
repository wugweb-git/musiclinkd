#!/usr/bin/env node
/**
 * Flow 20 — Recommendations, against the deferred-flows brief. BETA.
 *
 * This flow had NO screens at all before today.
 *
 * 15 sections, 2 screens. RECO-03 through RECO-08 are six recommendation
 * TYPES — people, businesses, services, opportunities, marketplace,
 * collaborators — which are one list filtered by what is being recommended.
 * States, not screens.
 *
 * The brief's principles are the design here, not decoration on top of it:
 *
 *   "Transparency over mystery."   Every card carries its reason IN WORDS.
 *   "No fabricated reasons."       A card whose reason would have to be
 *                                  invented is not shown — which is why the
 *                                  empty state exists and says we're still
 *                                  learning, rather than filling the screen.
 *   "Control over manipulation."   Not Interested is one tap, costs nothing,
 *                                  and the copy does not argue back.
 *   "Quality over endless scroll." Each section is short and ends.
 *
 * Recommendations are not Search and not Discovery: this never owns a
 * destination, every card routes to the canonical flow.
 *
 * No new CSS: .ml-match, .ml-result, .ml-person-row, .ml-market-card,
 * .ml-chip, .ml-empty and .ml-choice already exist.
 */
import { writeScreen, backArrow, icon, assertIcons, beta, finish, media } from "../write-screen.mjs";

const R = "/preview/flows/recommendation_engine/";
assertIcons(["arrow-forward", "heart", "cancel", "check", "search"]);

const tab = (state, label) =>
  `<a class="ml-tabs__tab" role="tab" data-chip="${state}" href="?state=${state}">${label}</a>`;

/* Every recommendation carries the reason it was made. If a reason cannot be
   stated truthfully in one line, the recommendation does not belong here. */
const RECS = [
  { st: "people collaborators", name: "Meera Nair", meta: "Playback vocalist · Kochi",
    why: "Sings in two of the three languages on your last release", pct: 94,
    img: media("faces", 4), href: "/preview/flows/public_profile/public_profile_view_talent/index.html",
    act: ["Follow", "Message"] },
  { st: "people", name: "Rohan D'Souza", meta: "Mixing engineer · Bengaluru",
    why: "Mixed two records in your primary genre this year", pct: 88,
    img: media("faces", 6), href: "/preview/flows/public_profile/public_profile_view/index.html",
    act: ["Follow", "Message"] },
  { st: "collaborators", name: "Kabir Sheth", meta: "Producer · Pune",
    why: "You've both worked with Aarav Menon, and your free weeks overlap", pct: 81,
    img: media("faces", 8), href: "/preview/flows/public_profile/public_profile_view/index.html",
    act: ["Message", "Save"] },
  { st: "businesses", name: "Blue Note Recording Co.", meta: "Recording studio · Bandra · 4.1 km",
    why: "Nearest room with a vocal booth free this week", pct: null,
    img: media("gear", 7), href: "/preview/flows/business_profiles/business_overview/index.html",
    act: ["Book", "Save"] },
  { st: "services", name: "Vocal Tuning & Comping", meta: "Ishita Rao · remote · ₹2,000",
    why: "You've bought this service twice before", pct: null,
    img: media("stage", 11), href: "/preview/flows/public_profile/talent_services_view/index.html",
    act: ["Book", "Save"] },
  { st: "opportunities", name: "Session vocalist — Hindi pop EP", meta: "Octave Sound Labs · ₹12,000/day",
    why: "Matches your role, your languages and your city", pct: null,
    img: media("stage", 4), href: "/preview/flows/opportunities/opportunity_detail/index.html",
    act: ["Apply", "Save"] },
  { st: "marketplace", name: "Rode NT1 Condenser Mic", meta: "Gear Rental · Bengaluru · ₹450/day",
    why: "You saved two condenser mics last month", pct: null,
    img: media("gear", 9), href: "/preview/flows/marketplace/marketplace_item_detail/index.html",
    act: ["View", "Save"] },
  { st: "nearby", name: "Yash Raj Sound Studios", meta: "Recording studio · Andheri West · 2.4 km",
    why: "2.4 km from the area on your profile", pct: null,
    img: media("stage", 1), href: "/preview/flows/booking/studio_profile/index.html",
    act: ["Book", "Save"] },
];

const card = (r) => `<div class="ml-match" data-state="${r.st} default why">
        <a class="ml-person-row" style="border:0;padding:0" href="${r.href}">
          <img src="${r.img}" alt="" loading="lazy" />
          <span class="meta"><b>${r.name}</b><p>${r.meta}</p></span>
          ${r.pct ? `<span class="ml-match__pct">${r.pct}%</span>` : ""}
        </a>
        <!-- The reason, always. This is the brief's transparency rule. -->
        <span class="ml-match__label">${r.why}</span>
        ${r.pct ? `<span class="ml-match__track"><span class="ml-match__fill" style="width:${r.pct}%"></span></span>` : ""}
        <div style="display:flex;gap:var(--space-2);margin-top:var(--space-2)">
          <a class="ml-btn ml-btn--secondary" style="flex:1" href="?state=dismissed">Not interested</a>
          <a class="ml-btn ml-btn--primary" style="flex:1" href="${r.href}">${r.act[0]}</a>
        </div>
      </div>`;

writeScreen({
  flow: "recommendation_engine", screen: "for_you", title: "For you",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">For you</h1>
      <span class="ml-appbar__actions">
        ${beta}
        <a class="ml-icon-btn ml-icon-btn--bare" href="${R}recommendation_preferences/index.html" aria-label="Recommendation settings">${icon("check", { size: 20 })}</a>
      </span>
    </header>
    <nav class="ml-tabs" role="tablist" aria-label="What to show" style="margin:0 var(--space-4)">
      ${tab("default", "All")}${tab("people", "People")}${tab("collaborators", "Collaborate")}${tab("businesses", "Studios")}${tab("services", "Services")}${tab("opportunities", "Work")}${tab("marketplace", "Gear")}${tab("nearby", "Nearby")}
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <p class="ml-type-1" data-state="default" style="color:var(--color-muted);margin:0 0 var(--space-2)">
        Suggestions based on your roles, your work and where you are. Every one says why.
      </p>

      <div class="ml-banner ml-banner--info" data-state="nearby">
        <span class="ml-banner__body">
          Nearby uses the city on your profile, not your live location.
          <a class="ml-link" href="/preview/flows/user_settings/privacy_settings/index.html?state=search_visibility">Change</a>
        </span>
      </div>

      ${RECS.map(card).join("\n      ")}

      <!-- "Why am I seeing this?" expands the signals actually used. Listing a
           signal we do not use would be a fabricated reason. -->
      <div data-state="why">
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Why you're seeing these</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Your roles and genres</span><span>Used</span></div>
          <div class="ml-summary__row"><span>Languages on your profile</span><span>Used</span></div>
          <div class="ml-summary__row"><span>City on your profile</span><span>Used</span></div>
          <div class="ml-summary__row"><span>What you've saved</span><span>Used</span></div>
          <div class="ml-summary__row"><span>Who you've worked with</span><span>Used</span></div>
          <div class="ml-summary__row"><span>Your messages</span><span>Not used</span></div>
          <div class="ml-summary__row"><span>Your live location</span><span>Not used</span></div>
        </div>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-3)" href="${R}recommendation_preferences/index.html">Change what's used</a>
      </div>

      <div data-state="dismissed">
        <div class="ml-banner ml-banner--info">
          <span class="ml-banner__body">Hidden. You'll see fewer like it.</span>
        </div>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-3)" href="?state=default">Back to suggestions</a>
      </div>

      <!-- No fabricated recommendations: when there is nothing honest to say,
           say that, and offer the thing that would actually help. -->
      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">We're still learning what's useful to you</h2>
        <p class="ml-empty__body">
          We'd rather show you nothing than guess. Adding your roles and a piece of work
          gives us something real to go on.
        </p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/profile_management/profile_completion/index.html">Finish your profile</a>
        <a class="ml-btn ml-btn--secondary" style="margin-top:var(--space-2)" href="/preview/flows/discover/discover_feed/index.html">Explore instead</a>
      </div>

      <p class="ml-type-1" data-state="default" style="color:var(--color-muted);margin-top:var(--space-3)">
        That's everything worth suggesting today. <a class="ml-link" href="?state=why">Why these?</a>
      </p>
    </main>
  </div>`,
});

writeScreen({
  flow: "recommendation_engine", screen: "recommendation_preferences", title: "Recommendation settings",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(`${R}for_you/index.html`, "Back")}
      <h1 class="ml-type-2" style="margin:0;flex:1">Recommendation settings</h1>
      ${beta}
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Choose what we may use. Turning something off makes suggestions less useful, not worse-behaved.
        </p>
        ${[["Your roles and genres", "The strongest signal, and the one you control directly", true],
           ["Your languages", null, true],
           ["Your city", "Used for nearby studios and in-person work", true],
           ["What you've saved", null, true],
           ["Who you've worked with", "Shared collaborators, not your message contents", true],
           ["Your availability", "So we don't suggest work for weeks you're away", false]]
          .map(([t, m, on]) => `<div class="ml-check-row">
          <span class="ml-choice__body">
            <span class="ml-choice__title">${t}</span>
            ${m ? `<span class="ml-choice__meta">${m}</span>` : ""}
          </span>
          <button class="ml-switch" type="button" role="switch" aria-checked="${on}" aria-label="${t}"></button>
        </div>`).join("\n        ")}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>All suggestions</h2></div>
        <a class="ml-action-row" href="?state=off">
          <span class="ml-choice__body">
            <span class="ml-choice__title">Turn off recommendations</span>
            <span class="ml-choice__meta">Search and Discover keep working exactly as they do now</span>
          </span>
          ${icon("arrow-forward", { size: 18 })}
        </a>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          This mirrors the Personalisation control in Settings — changing it in either place changes both.
        </p>
      </div>

      <div class="ml-empty" data-state="off">
        <h2 class="ml-empty__title">Recommendations are off</h2>
        <p class="ml-empty__body">
          You'll see no suggestions anywhere. Search and Discover are unaffected —
          you can still find everything, it's just ordered plainly rather than for you.
        </p>
        <a class="ml-btn ml-btn--primary" href="?state=default">Turn them back on</a>
      </div>
    </main>
  </div>`,
});

finish("recommendation_engine: ");
