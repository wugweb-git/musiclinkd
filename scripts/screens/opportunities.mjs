#!/usr/bin/env node
/**
 * Flow 5 — Opportunities. Six screens, 18 declared states, 0 implemented.
 *
 * Same shape as Market and Booking before them: each screen carried a local
 * <style> block that redrew a component the system already ships, and
 * my_applications declared six lifecycle states while rendering one.
 *
 *   .opp-card      → .ml-opportunity-card
 *   .app-row       → .ml-application-card
 *   .status-pill   → .ml-status--*
 *   .field         → .ml-field
 *   .project-pick  → .ml-choice[aria-checked]
 *   .summary-row   → .ml-summary__row
 *   .row-actions   → .ml-icon-btn--primary / --muted
 *
 * So this adds no CSS. my_applications is the interesting one: five statuses
 * are five states of ONE list, not five screens, and the status a card shows
 * is the state it belongs to.
 */
import { writeScreen, icon, backArrow, assertIcons, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/opportunities/";
assertIcons(["add", "check", "cancel", "search", "briefcase"]);

const tab = (state, label) =>
  `<a class="ml-tabs__tab" role="tab" data-chip="${state}" href="?state=${state}">${label}</a>`;

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-4" style="margin:0">${title}</h1>
    </header>`;

/* ── opportunities_home — default / empty ───────────────────────────────── */
const OPPS = [
  { title: "Session vocalist — Hindi pop EP", by: "Octave Sound Labs", budget: "₹12,000/day",
    closes: "Closes in 3 days", soon: true, body: "Three tracks, two days at our Andheri room. Reference: soft-pop, light belt." },
  { title: "Live drummer — 6-city club tour", by: "Wavelength Artists", budget: "₹45,000/week",
    closes: "Closes in 11 days", body: "Six shows across Mumbai, Pune, Bengaluru, Hyderabad, Chennai, Kochi. Rehearsals paid." },
  { title: "Mixing engineer — indie rock album", by: "Aarav Menon", budget: "₹60,000 project",
    closes: "Closes in 6 days", body: "Nine tracks, stems ready. Looking for someone with a rough, roomy sensibility." },
  { title: "Cover art & motion loop", by: "Blue Note Recording Co.", budget: "₹18,000",
    closes: "Closes in 20 days", body: "Single artwork plus a 6-second visualiser loop for streaming platforms." },
];

const oppCard = (o) => `<a class="ml-opportunity-card" data-state="default" href="${P}opportunity_detail/index.html">
        <div class="ml-opportunity-card__head">
          <span class="ml-opportunity-card__title">${o.title}</span>
          <span class="ml-opportunity-card__by">${o.by}</span>
        </div>
        <p class="ml-opportunity-card__body">${o.body}</p>
        <div class="ml-opportunity-card__foot">
          <span class="ml-opportunity-card__budget">${o.budget}</span>
          <span class="ml-opportunity-card__closes${o.soon ? " is-soon" : ""}">${o.closes}</span>
        </div>
      </a>`;

writeScreen({
  flow: "opportunities", screen: "opportunities_home", title: "Opportunities",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-4" style="margin:0">Opportunities</h1>
      <span class="ml-appbar__actions">
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}my_applications/index.html" aria-label="My applications">${icon("check", { size: 20 })}</a>
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}my_opportunities/index.html" aria-label="What you've posted">${icon("briefcase", { size: 20 })}</a>
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}create_opportunity/index.html" aria-label="Post an opportunity">${icon("add", { size: 20 })}</a>
      </span>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${OPPS.map(oppCard).join("\n      ")}

      <div class="ml-empty" data-state="empty">
        <h3 class="ml-empty__title">No open opportunities</h3>
        <p class="ml-empty__body">Nothing matches your roles and location right now. Post one of your own, or widen your preferences.</p>
        <a class="ml-btn ml-btn--primary" href="${P}create_opportunity/index.html">Post an opportunity</a>
      </div>
    </main>
  </div>`,
});

/* ── opportunity_detail — default ───────────────────────────────────────── */
writeScreen({
  flow: "opportunities", screen: "opportunity_detail", title: "Opportunities — Detail",
  body: `
  <div class="ml-app">
    ${bar("Opportunity", `${P}opportunities_home/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <h2 class="ml-type-4" style="margin:0">Session vocalist — Hindi pop EP</h2>
      <div class="ml-person-row" style="margin-top:var(--space-2)">
        <img src="${media("faces", 1)}" alt="" loading="lazy" />
        <span class="meta"><b>Octave Sound Labs</b><p>Studio · Andheri West, Mumbai</p></span>
      </div>
      <span class="ml-status ml-status--warning" style="margin-top:var(--space-2);display:inline-flex">Closes in 3 days</span>

      <p class="ml-type-3" style="color:var(--color-muted);margin-top:var(--space-3)">
        Three tracks, two days at our Andheri room. Reference points are soft-pop with a light belt —
        think restrained verses and an open chorus. Stems and a scratch guide will be provided a week ahead.
      </p>

      <div class="ml-summary" style="margin-top:var(--space-3)">
        <div class="ml-summary__row"><span>Rate</span><span>₹12,000/day</span></div>
        <div class="ml-summary__row"><span>Duration</span><span>2 days</span></div>
        <div class="ml-summary__row"><span>Location</span><span>Andheri West, Mumbai</span></div>
        <div class="ml-summary__row"><span>Languages</span><span>Hindi, English</span></div>
        <div class="ml-summary__row ml-summary__row--total"><span>Total</span><span>₹24,000</span></div>
        <p class="ml-summary__note">Paid within 7 days of the final session.</p>
      </div>
    </main>
    <!-- Both sides of the same posting: the poster manages applicants, the
         applicant applies. Two roles on one screen rather than two screens. -->
    <footer class="ml-app__footer" style="display:flex;gap:var(--space-2)">
      <a class="ml-btn ml-btn--secondary" style="flex:1" href="${P}manage_applicants/index.html">Applicants</a>
      <a class="ml-btn ml-btn--primary" style="flex:1" href="${P}opportunity_apply/index.html">Apply</a>
    </footer>
  </div>`,
});

/* ── create_opportunity — default / publishing / error ──────────────────── */
const ROLES = ["Session vocalist", "Session musician", "Producer", "Mixing engineer", "Composer", "DJ"];
writeScreen({
  flow: "opportunities", screen: "create_opportunity", title: "Opportunities — Post",
  body: `
  <div class="ml-app">
    ${bar("Post an opportunity", `${P}opportunities_home/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        <div class="ml-field">
          <label for="o-title">Title</label>
          <input id="o-title" type="text" value="Session vocalist — Hindi pop EP" />
        </div>
        <div class="ml-field">
          <label for="o-role">Role</label>
          <select id="o-role">${ROLES.map((r) => `<option>${r}</option>`).join("")}</select>
        </div>
        <div class="ml-field">
          <label for="o-budget">Budget (₹)</label>
          <input id="o-budget" type="text" inputmode="numeric" value="12000" />
        </div>
        <div class="ml-field">
          <label for="o-loc">Location</label>
          <input id="o-loc" type="text" value="Andheri West, Mumbai" />
        </div>
        <div class="ml-field">
          <label for="o-brief">Brief</label>
          <textarea id="o-brief" rows="4">Three tracks, two days at our Andheri room.</textarea>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=publishing">Post opportunity</a>
      </div>

      <div class="ml-empty" data-state="publishing">
        <span class="ml-spinner" role="status" aria-label="Publishing"></span>
        <p class="ml-empty__body" style="margin-top:var(--space-3)">Posting your opportunity…</p>
      </div>

      <div data-state="error">
        <div class="ml-banner ml-banner--error">
          <span class="ml-banner__body">Couldn't post — a budget is required before this can go live.</span>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=default">Back to the form</a>
      </div>
    </main>
  </div>`,
});

/* ── opportunity_apply — default / submitting / submitted / error ───────── */
/* The portfolio picker is .ml-choice, the same control the payment screen
   uses for methods — a pick-one list is a pick-one list. */
const WORKS = [
  ["Neon Monsoon — lead vocal", "Single · 2026 · Hindi pop", true],
  ["Kolaba Sessions — backing vocals", "EP · 2025 · Indie", false],
  ["Aftertaste — topline & vocal", "Single · 2025 · Electronic", false],
];
writeScreen({
  flow: "opportunities", screen: "opportunity_apply", title: "Opportunities — Apply",
  body: `
  <div class="ml-app">
    ${bar("Apply", `${P}opportunity_detail/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        <div class="ml-section"><h2>Session vocalist — Hindi pop EP</h2></div>
        <p class="ml-type-1" style="color:var(--color-muted);margin:0 0 var(--space-3)">Octave Sound Labs · ₹12,000/day</p>

        <div class="ml-section"><h3>Attach relevant work</h3></div>
        <div role="radiogroup" aria-label="Portfolio piece">
          ${WORKS.map(([t, m, on]) => `<div class="ml-choice" role="radio" aria-checked="${on}" tabindex="${on ? 0 : -1}">
            <span class="ml-choice__body">
              <span class="ml-choice__title">${t}</span>
              <span class="ml-choice__meta">${m}</span>
            </span>
            <span class="ml-choice__mark"></span>
          </div>`).join("\n          ")}
        </div>

        <div class="ml-field" style="margin-top:var(--space-3)">
          <label for="a-note">Message</label>
          <textarea id="a-note" rows="4" placeholder="A line on why this one suits you.">Available both days, and I've sung this exact register on two recent EPs.</textarea>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=submitting">Send application</a>
      </div>

      <div class="ml-empty" data-state="submitting">
        <span class="ml-spinner" role="status" aria-label="Sending"></span>
        <p class="ml-empty__body" style="margin-top:var(--space-3)">Sending your application…</p>
      </div>

      <div class="ml-empty" data-state="submitted">
        <h3 class="ml-empty__title" style="color:var(--color-success)">Application sent</h3>
        <p class="ml-empty__body">Octave Sound Labs will see it straight away. You'll be notified when they respond.</p>
        <a class="ml-btn ml-btn--primary" href="${P}my_applications/index.html">See my applications</a>
      </div>

      <div data-state="error">
        <div class="ml-banner ml-banner--error">
          <span class="ml-banner__body">Couldn't send — attach at least one piece of work first.</span>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=default">Back to the form</a>
      </div>
    </main>
  </div>`,
});

/* ── manage_applicants — default / empty ────────────────────────────────── */
const APPLICANTS = [
  ["Ishita Rao", "Session vocalist · Mumbai · 4.9 ★", 2],
  ["Kabir Sheth", "Vocalist & topliner · Pune · 4.7 ★", 5],
  ["Meera Nair", "Playback vocalist · Kochi · 5.0 ★", 9],
];
writeScreen({
  flow: "opportunities", screen: "manage_applicants", title: "Opportunities — Applicants",
  body: `
  <div class="ml-app">
    ${bar("Applicants", `${P}opportunities_home/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        <div class="ml-section"><h2>Session vocalist — Hindi pop EP</h2><span class="ml-section__link">${APPLICANTS.length} applicants</span></div>
        ${APPLICANTS.map(([n, m, i]) => `<div class="ml-person-row">
          <img src="${media("faces", i)}" alt="" loading="lazy" />
          <span class="meta"><b>${n}</b><p>${m}</p></span>
          <span class="ml-appbar__actions">
            <button class="ml-icon-btn ml-icon-btn--muted" type="button" aria-label="Decline ${n}">${icon("cancel", { size: 18 })}</button>
            <button class="ml-icon-btn ml-icon-btn--primary" type="button" aria-label="Shortlist ${n}">${icon("check", { size: 18 })}</button>
          </span>
        </div>`).join("\n        ")}
      </div>

      <div class="ml-empty" data-state="empty">
        <h3 class="ml-empty__title">No applicants yet</h3>
        <p class="ml-empty__body">This opportunity is live. Applications will appear here as they arrive.</p>
        <a class="ml-btn ml-btn--secondary" href="${P}opportunity_detail/index.html">View the posting</a>
      </div>
    </main>
  </div>`,
});

/* ── my_applications — five statuses + empty ────────────────────────────── */
/* Five states of ONE list. Each card declares the status it belongs to, so a
   status filter needs no JS and no duplicated markup. */
const APPS = [
  { st: "submitted",   title: "Session vocalist — Hindi pop EP", by: "Octave Sound Labs",     when: "Applied 2 days ago",  kind: "neutral", label: "Submitted" },
  { st: "viewed",      title: "Live drummer — 6-city club tour", by: "Wavelength Artists",    when: "Viewed yesterday",    kind: "info",    label: "Viewed" },
  { st: "shortlisted", title: "Mixing engineer — indie rock",    by: "Aarav Menon",           when: "Shortlisted 3 Aug",   kind: "warning", label: "Shortlisted" },
  { st: "accepted",    title: "Cover art & motion loop",         by: "Blue Note Recording Co.", when: "Accepted 1 Aug",    kind: "success", label: "Accepted" },
  { st: "rejected",    title: "Keys session — jazz trio",        by: "Third Wave Audio",      when: "Closed 28 Jul",       kind: "error",   label: "Not selected" },
];

writeScreen({
  flow: "opportunities", screen: "my_applications", title: "Opportunities — My applications",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-4" style="margin:0">My applications</h1>
    </header>
    <nav class="ml-tabs" role="tablist" aria-label="Application status" style="margin:0 var(--space-4)">
      ${tab("submitted", "Submitted")}${tab("viewed", "Viewed")}${tab("shortlisted", "Shortlisted")}${tab("accepted", "Accepted")}${tab("rejected", "Closed")}
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${APPS.map((a) => `<a class="ml-application-card" data-state="${a.st}" href="${P}opportunity_detail/index.html">
        <div class="ml-application-card__body">
          <span class="ml-application-card__title">${a.title}</span>
          <span class="ml-application-card__meta">${a.by} · ${a.when}</span>
        </div>
        <div class="ml-application-card__foot">
          <span class="ml-status ml-status--${a.kind}">${a.label}</span>
        </div>
      </a>`).join("\n      ")}

      <div class="ml-empty" data-state="empty">
        <h3 class="ml-empty__title">No applications yet</h3>
        <p class="ml-empty__body">When you apply for an opportunity it will show up here, with its status.</p>
        <a class="ml-btn ml-btn--primary" href="${P}opportunities_home/index.html">Browse opportunities</a>
      </div>
    </main>
  </div>`,
});

/* ══ the approved plan's Opportunities +2, part built ══════════════════════
   my_opportunities is the POSTER's side. my_applications, already built, is
   the applicant's. Two different people looking at the same objects from
   opposite ends, which is why they are two screens and not two states.

   applicant_profile from that column was NOT built: the canonical person
   profile already serves it, and manage_applicants links straight there. That
   is the recycling rule applied to the plan's own column. ═════════════════ */

const MINE = [
  { st: "posted", title: "Session vocalist — Hindi pop EP", meta: "3 applicants · closes in 3 days",
    status: ["success", "Live"], href: `${P}manage_applicants/index.html` },
  { st: "posted", title: "Live drummer — 6-city club tour", meta: "11 applicants · closes in 11 days",
    status: ["success", "Live"], href: `${P}manage_applicants/index.html` },
  { st: "drafts", title: "Mixing engineer — indie rock album", meta: "Not posted · saved 28 Aug",
    status: [null, "Draft"], href: `${P}create_opportunity/index.html` },
  { st: "closed", title: "Cover art & visualiser", meta: "Filled by Priya Iyer · closed 20 Aug",
    status: [null, "Filled"], href: `${P}opportunity_detail/index.html` },
  { st: "closed", title: "Keys session — jazz trio", meta: "Expired with no hire · 28 Jul",
    status: ["error", "Expired"], href: `${P}opportunity_detail/index.html` },
];

writeScreen({
  flow: "opportunities", screen: "my_opportunities", title: "Opportunities — Mine",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-4" style="margin:0">What you've posted</h1>
      <span class="ml-appbar__actions">
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}create_opportunity/index.html" aria-label="Post an opportunity">${icon("add", { size: 20 })}</a>
      </span>
    </header>
    <nav class="ml-tabs ml-tabs--fill" role="tablist" aria-label="Your opportunities" style="margin:0 var(--space-4)">
      ${tab("posted", "Live")}${tab("drafts", "Drafts")}${tab("closed", "Closed")}
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${MINE.map((m) => `<a class="ml-application-card" data-state="${m.st}" href="${m.href}">
        <div class="ml-application-card__body">
          <span class="ml-application-card__title">${m.title}</span>
          <span class="ml-application-card__meta">${m.meta}</span>
        </div>
        <div class="ml-application-card__foot">
          <span class="ml-status${m.status[0] ? ` ml-status--${m.status[0]}` : ""}">${m.status[1]}</span>
        </div>
      </a>`).join("\n      ")}

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">You haven't posted anything</h2>
        <p class="ml-empty__body">Posting takes a couple of minutes, and you can save a draft first.</p>
        <a class="ml-btn ml-btn--primary" href="${P}create_opportunity/index.html">Post an opportunity</a>
      </div>
    </main>
  </div>`,
});

finish("opportunities: ");
