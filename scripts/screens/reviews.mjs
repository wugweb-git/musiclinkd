#!/usr/bin/env node
/**
 * Flow 17 — Reviews. Eleven screens, 19 declared states, all eleven still the
 * raw "Not designed yet" scaffold.
 *
 * Eleven screens, four shapes:
 *
 *   reviewForm()  star → subcategories → comment → submitted. Serves
 *                 write_review (5 states), completed_project_review,
 *                 marketplace_review_screen and studio_facility_review —
 *                 four screens, eleven states, ONE composer. The three
 *                 "review a booking / a listing / a studio" screens differ
 *                 only in what is being rated and which subcategories apply.
 *   reviewList()  a list of .ml-review-card. Serves review_list and
 *                 public_reputation.
 *   scoreboard()  a headline score plus a per-star breakdown. Serves
 *                 rating_breakdown and reputation_dashboard.
 *   detail        review_detail, review_business_response, review_report.
 *
 * write_review's five states are the real recycling case: star_selected,
 * subcategory_ratings and comment_entered are three moments in ONE form, not
 * three screens, so the form reveals more of itself as the state advances.
 *
 * No new CSS: .ml-rating, .ml-review-card, .ml-progress, .ml-kpi,
 * .ml-stat-grid, .ml-field, .ml-choice and .ml-empty already exist.
 */
import { writeScreen, backArrow, icon, assertIcons, beta, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/reviews/";
assertIcons(["star", "arrow-forward"]);

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-2" style="margin:0;flex:1">${title}</h1>
      ${beta}
    </header>`;

/* A star row that is an INPUT, not a readout: five buttons carrying
   aria-pressed, which is the same mechanism the chips use. */
const starInput = (score) => `<div role="group" aria-label="Overall rating" style="display:flex;gap:var(--space-1)">
          ${[1, 2, 3, 4, 5].map((n) => `<button class="ml-icon-btn ml-icon-btn--bare" type="button"
            aria-pressed="${n <= score}" aria-label="${n} star${n > 1 ? "s" : ""}"
            style="color:${n <= score ? "var(--color-ink)" : "var(--color-line)"}">${icon("star", { size: 28 })}</button>`).join("\n          ")}
        </div>`;

/* ── one review form, four screens ──────────────────────────────────────── */
const reviewForm = ({ screen, title, backTo, subject, subjectMeta, img, subcats, states }) => {
  const has = (s) => states.includes(s);
  return writeScreen({
    flow: "reviews", screen, title: `Reviews — ${title}`,
    body: `
  <div class="ml-app">
    ${bar(title, backTo)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <!-- What is being reviewed, always visible except once it's sent. -->
      <div data-state="${states.filter((s) => s !== "submitted").join(" ")}">
        <div class="ml-result" style="border:0;padding:0">
          <img class="ml-result__thumb" src="${img}" alt="" loading="lazy" />
          <span class="ml-result__body">
            <span class="ml-result__name">${subject}</span>
            <span class="ml-result__meta">${subjectMeta}</span>
          </span>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Overall</h2></div>
        ${starInput(4)}

        ${has("subcategory_ratings") || has("comment_entered") || !has("star_selected") ? `
        <div data-state="${["subcategory_ratings", "comment_entered", "default"].filter(has).join(" ")}">
          <div class="ml-section" style="margin-top:var(--space-3)"><h2>The detail</h2></div>
          ${subcats.map((c) => `<div class="ml-check-row">
            <span class="ml-choice__body"><span class="ml-choice__title">${c}</span></span>
            <span class="ml-review-card__stars">★★★★☆</span>
          </div>`).join("\n          ")}
        </div>` : ""}

        <div data-state="${["comment_entered", "default"].filter(has).join(" ")}">
          <div class="ml-field" style="margin-top:var(--space-3)">
            <label for="r-body">Your review</label>
            <textarea id="r-body" rows="4" placeholder="What went well, and what would you tell the next person?">Quiet room and a patient engineer. Three vocal takes done inside a morning.</textarea>
          </div>
        </div>

        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=submitted">Post review</a>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Reviews are public and show your name. You can edit yours for 48 hours.
        </p>
      </div>

      <div class="ml-empty" data-state="submitted">
        <h2 class="ml-empty__title" style="color:var(--color-success)">Review posted</h2>
        <p class="ml-empty__body">Thanks — it's live on ${subject}'s profile now.</p>
        <a class="ml-btn ml-btn--primary" href="${P}review_list/index.html">See all reviews</a>
      </div>
    </main>
  </div>`,
  });
};

reviewForm({
  screen: "write_review", title: "Write a review", backTo: `${P}review_list/index.html`,
  subject: "Ishita Rao", subjectMeta: "Session vocalist · Worked together Aug 2026",
  img: media("faces", 2),
  subcats: ["Communication", "Punctuality", "Musicianship", "Would work with again"],
  states: ["default", "star_selected", "subcategory_ratings", "comment_entered", "submitted"],
});

reviewForm({
  screen: "completed_project_review", title: "Review the project", backTo: "/preview/flows/opportunities/my_applications/index.html",
  subject: "Octave Sound Labs", subjectMeta: "Session vocalist — Hindi pop EP · Completed 14 Aug",
  img: media("stage", 1),
  subcats: ["Brief was clear", "Paid on time", "Studio conditions", "Would work with again"],
  states: ["default", "submitted"],
});

reviewForm({
  screen: "marketplace_review_screen", title: "Review the seller", backTo: "/preview/flows/marketplace/marketplace_order_status/index.html",
  subject: "Aarav Menon", subjectMeta: "Shure SM7B Microphone · Order ML-4471",
  img: media("gear", 0),
  subcats: ["Item as described", "Packaging", "Communication", "Dispatch speed"],
  states: ["default", "submitted"],
});

reviewForm({
  screen: "studio_facility_review", title: "Review the studio", backTo: "/preview/flows/booking/my_bookings/index.html",
  subject: "Yash Raj Sound Studios", subjectMeta: "Tracking Room 1 · Session 12 Aug",
  img: media("stage", 1),
  subcats: ["Room and acoustics", "Equipment condition", "Engineer", "Value for money"],
  states: ["default", "submitted"],
});

/* ── review lists ───────────────────────────────────────────────────────── */
const REVIEWS = [
  { who: "Ishita Rao", face: 2, stars: 5, when: "2 weeks ago",
    body: "Quiet live room and a patient engineer. Got three vocal takes done inside a morning." },
  { who: "Kabir Sheth", face: 8, stars: 4, when: "1 month ago",
    body: "Great desk, and the backline is genuinely maintained. Parking is tight before 6pm." },
  { who: "Meera Nair", face: 4, stars: 5, when: "2 months ago",
    body: "Booked at short notice and they moved things around to fit us in. Would go back." },
];

const reviewCard = (r) => `<article class="ml-review-card">
        <div class="ml-review-card__head">
          <img class="ml-avatar ml-avatar--sm" src="${media("faces", r.face)}" alt="" loading="lazy" />
          <b>${r.who}</b>
          <span class="ml-review-card__stars">${"★".repeat(r.stars)}${"☆".repeat(5 - r.stars)}</span>
        </div>
        <p class="ml-review-card__body">${r.body}</p>
      </article>`;

writeScreen({
  flow: "reviews", screen: "review_list", title: "Reviews — All reviews",
  body: `
  <div class="ml-app">
    ${bar("Reviews", `${P}public_reputation/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        <div class="ml-rating">
          <span class="ml-rating__score">4.8</span>
          <span class="ml-rating__stars">★★★★★</span>
          <span class="ml-rating__count">· 62 reviews</span>
        </div>
        ${REVIEWS.map(reviewCard).join("\n        ")}
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-3)" href="${P}rating_breakdown/index.html">See the breakdown</a>
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No reviews yet</h2>
        <p class="ml-empty__body">Reviews appear here once you've finished a session, a project or an order together.</p>
        <a class="ml-btn ml-btn--primary" href="${P}write_review/index.html">Write a review</a>
      </div>

      <!-- A review is tied to a real interaction, so the REQUEST is tied to one
           too: it names the session it is about. The brief forbids a generic
           social rating system, and this is where that starts. -->
      <div data-state="request">
        <div class="ml-inset ml-inset--raised" style="padding:var(--space-3)">
          <h2 class="ml-type-3" style="margin:0 0 var(--space-1)">How was your session?</h2>
          <p class="ml-type-2" style="color:var(--color-muted);margin:0">
            Yash Raj Sound Studios, Tracking Room 1 — Wednesday 12 August.
          </p>
          <div style="display:flex;gap:var(--space-2);margin-top:var(--space-3)">
            <a class="ml-btn ml-btn--secondary" style="flex:1" href="?state=default">Not now</a>
            <a class="ml-btn ml-btn--primary" style="flex:1" href="${P}studio_facility_review/index.html">Leave a review</a>
          </div>
        </div>
      </div>

      <div data-state="reminder">
        <div class="ml-banner ml-banner--info">
          <span class="ml-banner__body">
            One review still to leave, from your session on 12 August.
            <a class="ml-link" href="${P}studio_facility_review/index.html">Write it</a>
          </span>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          We ask once, and once more a week later. After that we stop.
        </p>
      </div>

      <div data-state="pending_moderation">
        <div class="ml-banner ml-banner--warning">
          <span class="ml-banner__body">Your review is being checked before it appears.</span>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          This happens when a review is reported, or when it mentions a payment dispute.
          It usually takes a day. Nobody else can see it in the meantime.
        </p>
      </div>
    </main>
  </div>`,
});

writeScreen({
  flow: "reviews", screen: "public_reputation", title: "Reviews — Reputation",
  body: `
  <div class="ml-app">
    ${bar("Reputation", `${P}reputation_dashboard/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-person-row">
        <img src="${media("faces", 2)}" alt="" loading="lazy" />
        <span class="meta"><b>Ishita Rao</b><p>Session vocalist · Mumbai</p></span>
      </div>
      <div class="ml-rating" style="margin-top:var(--space-2)">
        <span class="ml-rating__score">4.8</span>
        <span class="ml-rating__stars">★★★★★</span>
        <span class="ml-rating__count">· 62 reviews</span>
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Recent</h2>
        <a class="ml-section__link" href="${P}review_list/index.html">See all</a></div>
      ${REVIEWS.slice(0, 2).map(reviewCard).join("\n      ")}
    </main>
  </div>`,
});

/* ── scoreboards ────────────────────────────────────────────────────────── */
const BREAKDOWN = [[5, 48], [4, 10], [3, 3], [2, 1], [1, 0]];
const total = BREAKDOWN.reduce((a, [, n]) => a + n, 0);

writeScreen({
  flow: "reviews", screen: "rating_breakdown", title: "Reviews — Breakdown",
  body: `
  <div class="ml-app">
    ${bar("Rating breakdown", `${P}review_list/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-rating">
        <span class="ml-rating__score">4.8</span>
        <span class="ml-rating__stars">★★★★★</span>
        <span class="ml-rating__count">· ${total} reviews</span>
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>By star</h2></div>
      ${BREAKDOWN.map(([star, n]) => `<div class="ml-progress">
        <span class="ml-progress__title">${star} star${star > 1 ? "s" : ""}</span>
        <span class="ml-progress__meta">${n}</span>
        <span class="ml-progress__track"><span class="ml-progress__fill" style="width:${Math.round((n / total) * 100)}%"></span></span>
      </div>`).join("\n      ")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>By category</h2></div>
      ${[["Communication", 4.9], ["Punctuality", 4.9], ["Musicianship", 4.8], ["Value", 4.6]]
        .map(([c, v]) => `<div class="ml-summary__row"><span>${c}</span><span>${v}</span></div>`).join("\n      ")}
    </main>
  </div>`,
});

writeScreen({
  flow: "reviews", screen: "reputation_dashboard", title: "Reviews — Dashboard",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Reputation</h1>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-stat-grid">
        <div class="ml-kpi"><span class="ml-kpi__num">4.8</span>Average rating<span class="ml-kpi__delta ml-kpi__delta--up">+0.1</span></div>
        <div class="ml-kpi"><span class="ml-kpi__num">62</span>Reviews<span class="ml-kpi__delta ml-kpi__delta--up">+6</span></div>
        <div class="ml-kpi"><span class="ml-kpi__num">96%</span>Would rebook</div>
        <div class="ml-kpi"><span class="ml-kpi__num">2</span>Awaiting reply</div>
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Needs a reply</h2>
        <a class="ml-section__link" href="${P}review_business_response/index.html">Reply</a></div>
      ${reviewCard(REVIEWS[1])}

      <!-- The three "review a booking / an order / a project" forms are entered
           from Booking, Market and Opportunities. Listing what you owe here
           gives them a route inside this flow too, which is also how a person
           actually finds them. -->
      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Reviews you can leave</h2></div>
      <a class="ml-action-row" href="${P}studio_facility_review/index.html">
        <span class="ml-choice__body"><span class="ml-choice__title">Yash Raj Sound Studios</span>
        <span class="ml-choice__meta">Session on 12 August</span></span>
        ${icon("arrow-forward", { size: 18 })}
      </a>
      <a class="ml-action-row" href="${P}marketplace_review_screen/index.html">
        <span class="ml-choice__body"><span class="ml-choice__title">Aarav Menon</span>
        <span class="ml-choice__meta">Order ML-4471</span></span>
        ${icon("arrow-forward", { size: 18 })}
      </a>
      <a class="ml-action-row" href="${P}completed_project_review/index.html">
        <span class="ml-choice__body"><span class="ml-choice__title">Octave Sound Labs</span>
        <span class="ml-choice__meta">Hindi pop EP, completed 14 August</span></span>
        ${icon("arrow-forward", { size: 18 })}
      </a>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>More</h2></div>
      <a class="ml-action-row" href="${P}rating_breakdown/index.html">
        <span class="ml-choice__body"><span class="ml-choice__title">Rating breakdown</span></span>
        ${icon("arrow-forward", { size: 18 })}
      </a>
      <a class="ml-action-row" href="${P}public_reputation/index.html">
        <span class="ml-choice__body"><span class="ml-choice__title">How your profile looks</span></span>
        ${icon("arrow-forward", { size: 18 })}
      </a>
    </main>
  </div>`,
});

/* ── detail, response, report ───────────────────────────────────────────── */
writeScreen({
  flow: "reviews", screen: "review_detail", title: "Reviews — Review",
  body: `
  <div class="ml-app">
    ${bar("Review", `${P}review_list/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${reviewCard(REVIEWS[0])}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Ratings</h2></div>
      ${[["Communication", 5], ["Punctuality", 5], ["Musicianship", 5], ["Value", 4]]
        .map(([c, v]) => `<div class="ml-summary__row"><span>${c}</span><span>${"★".repeat(v)}${"☆".repeat(5 - v)}</span></div>`).join("\n      ")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Actions</h2></div>
      <a class="ml-action-row" href="${P}review_business_response/index.html">
        <span class="ml-choice__body"><span class="ml-choice__title">Reply publicly</span>
        <span class="ml-choice__meta">Your reply appears under the review</span></span>
        ${icon("arrow-forward", { size: 18 })}
      </a>
      <a class="ml-action-row" href="${P}review_report/index.html">
        <span class="ml-choice__body"><span class="ml-choice__title">Report this review</span>
        <span class="ml-choice__meta">If it breaks the guidelines</span></span>
        ${icon("arrow-forward", { size: 18 })}
      </a>
      <a class="ml-action-row" href="?state=edit">
        <span class="ml-choice__body"><span class="ml-choice__title">Edit your review</span>
        <span class="ml-choice__meta">Within 48 hours of posting</span></span>
        ${icon("arrow-forward", { size: 18 })}
      </a>

      <!-- Editing is time-boxed and the edit is disclosed, so a review cannot
           be quietly rewritten after the fact. -->
      <div data-state="edit">
        <div class="ml-section"><h2>Edit your review</h2></div>
        <div class="ml-field">
          <label for="e-body">Your review</label>
          <textarea id="e-body" rows="4">Quiet live room and a patient engineer. Got three vocal takes done inside a morning.</textarea>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Edited reviews are marked "edited" with the date. You can edit for 48 hours after posting.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=default">Save changes</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=delete">Delete this review instead</a>
      </div>

      <div data-state="delete">
        <div class="ml-banner ml-banner--error">
          <span class="ml-banner__body">Delete your review?</span>
        </div>
        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>The studio's rating</span><span>Recalculated without it</span></div>
          <div class="ml-summary__row"><span>Their reply</span><span>Deleted with it</span></div>
          <div class="ml-summary__row"><span>Writing another</span><span>Not possible for this session</span></div>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="${P}review_list/index.html">Delete review</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=default">Keep it</a>
      </div>

      <div data-state="reported">
        <div class="ml-banner ml-banner--warning">
          <span class="ml-banner__body">This review has been reported and is being checked.</span>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          It stays visible while we look at it. A review is only removed if it breaks the
          guidelines — not because it is critical.
        </p>
      </div>
    </main>
  </div>`,
});

writeScreen({
  flow: "reviews", screen: "review_business_response", title: "Reviews — Reply",
  body: `
  <div class="ml-app">
    ${bar("Reply publicly", `${P}review_detail/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${reviewCard(REVIEWS[1])}
      <div class="ml-field" style="margin-top:var(--space-3)">
        <label for="resp">Your reply</label>
        <textarea id="resp" rows="4" placeholder="Answer the point, not the tone.">Thanks Kabir — you're right about parking. We've since taken two more bays on the same street.</textarea>
      </div>
      <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
        One reply per review, shown publicly under it. It can't be edited once posted.
      </p>
      <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=posted">Post reply</a>

      <div class="ml-empty" data-state="posted">
        <h2 class="ml-empty__title" style="color:var(--color-success)">Reply posted</h2>
        <p class="ml-empty__body">It appears under the review, on your public profile. It can't be edited.</p>
        <a class="ml-btn ml-btn--primary" href="${P}reputation_dashboard/index.html">Back to reputation</a>
      </div>
    </main>
  </div>`,
});

writeScreen({
  flow: "reviews", screen: "review_report", title: "Reviews — Report",
  body: `
  <div class="ml-app">
    ${bar("Report review", `${P}review_detail/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
        Reporting is anonymous. A review is only removed if it breaks the guidelines — not because it's critical.
      </p>
      <div class="ml-field">
        <label for="rr-reason">Reason</label>
        <select id="rr-reason">
          <option>Not about a real session or order</option>
          <option>Abusive or discriminatory</option>
          <option>Reveals private information</option>
          <option>Conflict of interest</option>
        </select>
      </div>
      <div class="ml-field">
        <label for="rr-body">Anything else?</label>
        <textarea id="rr-body" rows="4" placeholder="Optional."></textarea>
      </div>
      <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=submitted">Report review</a>

      <div class="ml-empty" data-state="submitted">
        <h2 class="ml-empty__title">Report received</h2>
        <p class="ml-empty__body">
          We'll check it against the guidelines. The review stays visible while we do,
          and the reviewer is not told who reported it.
        </p>
        <a class="ml-btn ml-btn--primary" href="${P}review_detail/index.html">Back to the review</a>
      </div>
    </main>
  </div>`,
});

finish("reviews: ");
