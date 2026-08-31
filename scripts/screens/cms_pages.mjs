#!/usr/bin/env node
/**
 * Flow 22 — CMS / Content Pages, against the deferred-flows brief. BETA.
 *
 * The architectural point in the brief is the one that shapes this file:
 * Flow 22 is the READER's journey. Authoring belongs to Admin. So there is no
 * editor here — `admin_pages_editor` is a list of what exists and a link into
 * the admin surface, not a WYSIWYG.
 *
 * 22 numbered sections, 7 screens. CMS-04 (article), CMS-07 (FAQ), CMS-09
 * (legal), CMS-10 (policy) and CMS-11 (help article) are five sections that
 * differ in CONTENT, not in shape — one editorial reader serves all of them,
 * and the type is a state. CMS-16/17/18 (not found, archived, restricted) are
 * states of that same reader.
 *
 * "Do not turn articles into dense documentation pages." So the reader uses a
 * measure of ~64ch, generous spacing, and .ml-lede for the standfirst.
 *
 * No new CSS: .ml-article, .ml-articles, .ml-lede, .ml-feature, .ml-tier(s),
 * .ml-summary, .ml-action-row and .ml-empty already exist.
 */
import { writeScreen, backArrow, icon, assertIcons, beta, finish, media } from "../write-screen.mjs";

const C = "/preview/flows/cms_pages/";
assertIcons(["arrow-forward", "search", "check", "heart"]);

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-2" style="margin:0;flex:1">${title}</h1>
      ${beta}
    </header>`;

const rowLink = (label, href, meta) => `<a class="ml-action-row" href="${href}">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>`;

/* ── content_home — featured, latest, categories, search ────────────────── */
const CATEGORIES = [
  ["Getting started", "Your profile, your first booking", 6],
  ["Marketplace", "Buying, selling, renting, disputes", 9],
  ["Bookings", "Deposits, cancellations, no-shows", 7],
  ["Music career", "Rates, credits, negotiating", 11],
  ["Business", "Studios, teams, invoicing", 5],
  ["Safety", "Reporting, blocking, verification", 4],
];

const ARTICLES = [
  ["What a session rate should actually cover", "Music career", "28 Aug 2026", 3],
  ["Deposits, cancellations and the 48-hour rule", "Bookings", "21 Aug 2026", 1],
  ["Selling gear without getting stung", "Marketplace", "14 Aug 2026", 0],
];

writeScreen({
  flow: "cms_pages", screen: "content_home", title: "Guides & help",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Guides</h1>
      ${beta}
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-field">
        <label for="c-q">Search guides and help</label>
        <input id="c-q" type="search" placeholder="Deposits, rates, verification…" />
      </div>

      <div data-state="default">
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Featured</h2></div>
        <a class="ml-article" href="${C}content_article/index.html">
          <img src="${media("editorial", 2)}" alt="" loading="lazy" />
          <div>
            <b>What a session rate should actually cover</b>
            <span class="ml-type-1" style="color:var(--color-muted)">Music career · 28 Aug 2026</span>
            <p>Studio time, prep, revisions, and the two things people forget to charge for.</p>
          </div>
        </a>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Browse</h2></div>
        ${CATEGORIES.map(([t, m, n]) => rowLink(t, "?state=category", `${m} · ${n} articles`)).join("\n        ")}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Latest</h2></div>
        ${ARTICLES.map(([t, cat, d, i]) => `<a class="ml-article" href="${C}content_article/index.html">
          <img src="${media("editorial", i)}" alt="" loading="lazy" />
          <div>
            <b>${t}</b>
            <span class="ml-type-1" style="color:var(--color-muted)">${cat} · ${d}</span>
          </div>
        </a>`).join("\n        ")}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Still stuck?</h2></div>
        ${rowLink("Contact support", "/preview/flows/user_settings/raise_ticket/index.html", "Replies within one working day")}
      </div>

      <div data-state="category">
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Bookings</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Holding a room, paying a deposit, and what happens when plans change.
        </p>
        ${["Deposits, cancellations and the 48-hour rule",
           "What happens if a studio cancels on you",
           "Rescheduling without losing your deposit",
           "No-shows, both directions",
           "Booking a room you have not seen"]
          .map((t) => rowLink(t, `${C}content_article/index.html`)).join("\n        ")}
      </div>

      <div data-state="searching">
        <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-3) 0 var(--space-2)">
          4 results for "deposit"
        </p>
        ${["Deposits, cancellations and the 48-hour rule",
           "When is the deposit taken?",
           "Refunds and how long they take",
           "Marketplace deposits on rentals"]
          .map((t) => rowLink(t, `${C}content_article/index.html`, "Bookings")).join("\n        ")}
      </div>

      <div class="ml-empty" data-state="no_results">
        <h2 class="ml-empty__title">Nothing matches that</h2>
        <p class="ml-empty__body">Try "deposit", "cancellation" or "payout" — or just ask us.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/user_settings/raise_ticket/index.html">Contact support</a>
      </div>

      <div class="ml-empty" data-state="offline">
        <h2 class="ml-empty__title">You're offline</h2>
        <p class="ml-empty__body">Guides you've already opened are still readable. New ones need a connection.</p>
      </div>
    </main>
  </div>`,
});

/* ── content_article — ONE reader, every content type as a state ────────── */
/* The brief lists article, FAQ, policy and help as separate sections. They
   differ in content, not in shape, so one reader serves all four and the type
   is the state. Measure is capped so long-form stays readable. */
const READ = "max-width:64ch;margin-inline:auto";

writeScreen({
  flow: "cms_pages", screen: "content_article", title: "Article",
  body: `
  <div class="ml-app">
    ${bar("Guides", `${C}content_home/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:0 0 var(--space-4)">

      <!-- article -->
      <div data-state="default">
        <img src="${media("editorial", 2)}" alt="" loading="lazy"
             style="width:100%;aspect-ratio:var(--media-aspect-card);object-fit:cover" />
        <div style="padding:var(--space-3) var(--space-4)">
          <span class="ml-eyebrow">Music career</span>
          <h2 class="ml-type-4" style="margin:var(--space-1) 0;${READ}">What a session rate should actually cover</h2>
          <p class="ml-type-1" style="color:var(--color-muted);margin:0 0 var(--space-3)">
            Published 28 August 2026 · 6 min read
          </p>
          <p class="ml-lede" style="${READ}">
            Most people quote for the hours they will be in the room. That is the part
            everyone remembers and the smallest part of the work.
          </p>
          <div style="${READ}">
            <p class="ml-type-2" style="color:var(--color-muted)">
              A four-hour tracking session is rarely four hours. There is the listening you do
              beforehand, the charts or the lyric you prepare, the travel, and the revisions
              afterwards that nobody wrote down. Quoting only for the room means the rest is free.
            </p>
            <div class="ml-section" style="margin-top:var(--space-3)"><h3>What to include</h3></div>
            <div class="ml-summary">
              <div class="ml-summary__row"><span>Prep</span><span>Usually 1–2 hours</span></div>
              <div class="ml-summary__row"><span>The session</span><span>Booked hours</span></div>
              <div class="ml-summary__row"><span>Revisions</span><span>Say how many are included</span></div>
              <div class="ml-summary__row"><span>Travel</span><span>Outside your city, at cost</span></div>
            </div>
            <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
              The second thing people forget is the deposit. Half up front is normal here,
              and it is the difference between a booking and an intention.
            </p>
          </div>

          <div class="ml-section" style="margin-top:var(--space-4)"><h3>Do this next</h3></div>
          ${rowLink("Set your rates", "/preview/flows/services_availability/rate_pricing_editor/index.html", "Day rate, deposit, cancellation window")}

          <div class="ml-section" style="margin-top:var(--space-3)"><h3>Related</h3></div>
          ${["Deposits, cancellations and the 48-hour rule", "Negotiating without undercutting yourself"]
            .map((t) => rowLink(t, "?state=default")).join("\n          ")}

          <div class="ml-section" style="margin-top:var(--space-3)"><h3>Was this useful?</h3></div>
          <div style="display:flex;gap:var(--space-2)">
            <button class="ml-btn ml-btn--secondary" type="button" style="flex:1">Yes</button>
            <button class="ml-btn ml-btn--secondary" type="button" style="flex:1">No</button>
          </div>
        </div>
      </div>

      <!-- faq -->
      <div data-state="faq" style="padding:var(--space-3) var(--space-4)">
        <h2 class="ml-type-3" style="margin:0 0 var(--space-3)">Frequently asked</h2>
        ${[["When is the deposit taken?", "When the studio confirms your slot, not when you request it. If they decline, nothing is charged."],
           ["Can I cancel a booking?", "Free up to 48 hours before. Inside 48 hours the studio keeps the deposit."],
           ["When do I get paid?", "Within seven days of a completed session, to the account on your profile."],
           ["Why was my listing removed?", "Usually a prohibited item or someone else's photos. You always get an email saying which."]]
          .map(([q, a]) => `<div class="ml-section"><h3>${q}</h3></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3);${READ}">${a}</p>`).join("\n        ")}
      </div>

      <!-- policy / legal -->
      <div data-state="policy" style="padding:var(--space-3) var(--space-4)">
        <span class="ml-eyebrow">Policy</span>
        <h2 class="ml-type-3" style="margin:var(--space-1) 0">Marketplace rules</h2>
        <div class="ml-summary" style="margin-bottom:var(--space-3)">
          <div class="ml-summary__row"><span>Effective</span><span>14 August 2026</span></div>
          <div class="ml-summary__row"><span>Version</span><span>2.1</span></div>
        </div>
        <div style="${READ}">
          ${[["What you may list", "Only what you own and can hand over or ship. No counterfeits, no stolen gear, no listings for something you have not got yet."],
             ["Offers", "An offer is binding for 48 hours. A seller may counter once. Neither side is charged until an offer is accepted."],
             ["Deposits and disputes", "Rental deposits are held, not paid out, until the item comes back. If the two sides disagree we hold the deposit until both have been heard."],
             ["Removal", "We remove listings that break these rules and email you the reason. Repeated breaches end an account."]]
            .map(([h, b]) => `<div class="ml-section"><h3>${h}</h3></div>
          <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">${b}</p>`).join("\n          ")}
        </div>
      </div>

      <!-- help article: problem, explanation, steps -->
      <div data-state="help" style="padding:var(--space-3) var(--space-4)">
        <span class="ml-eyebrow">Help · Bookings</span>
        <h2 class="ml-type-3" style="margin:var(--space-1) 0">My deposit was taken but the booking isn't showing</h2>
        <p class="ml-lede" style="${READ}">
          Almost always a payment that succeeded at your bank but had not reached us when you last looked.
        </p>
        <div class="ml-section" style="margin-top:var(--space-3)"><h3>What to do</h3></div>
        <ol class="ml-timeline">
          <li class="ml-timeline__item is-done"><span class="ml-timeline__title">Check My bookings</span>
            <span class="ml-timeline__meta">Pull down to refresh</span></li>
          <li class="ml-timeline__item is-current"><span class="ml-timeline__title">Give it fifteen minutes</span>
            <span class="ml-timeline__meta">Most clear on their own</span></li>
          <li class="ml-timeline__item"><span class="ml-timeline__title">Still missing? Send us the reference</span></li>
        </ol>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="/preview/flows/user_settings/raise_ticket/index.html">Contact support</a>
      </div>

      <!-- announcement -->
      <div data-state="announcement" style="padding:var(--space-3) var(--space-4)">
        <span class="ml-eyebrow">Announcement</span>
        <h2 class="ml-type-3" style="margin:var(--space-1) 0">Offers now expire after 48 hours</h2>
        <p class="ml-type-1" style="color:var(--color-muted);margin:0 0 var(--space-3)">31 August 2026</p>
        <p class="ml-type-2" style="color:var(--color-muted);${READ}">
          Marketplace offers used to sit open indefinitely, which left sellers unsure whether an
          offer was still real. From today an offer expires after 48 hours unless it is answered.
          Nothing changes for offers already open.
        </p>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-3)" href="/preview/flows/marketplace/marketplace_browse/index.html">Go to Market</a>
      </div>

      <div class="ml-empty" data-state="archived">
        <h2 class="ml-empty__title">This guide is out of date</h2>
        <p class="ml-empty__body">It described how deposits worked before August 2026. There is a current version.</p>
        <a class="ml-btn ml-btn--primary" href="?state=default">Read the current version</a>
      </div>

      <div class="ml-empty" data-state="restricted">
        <h2 class="ml-empty__title">You don't have access to this</h2>
        <p class="ml-empty__body">Some guides are only for business accounts.</p>
        <a class="ml-btn ml-btn--primary" href="${C}content_home/index.html">Back to guides</a>
      </div>

      <div class="ml-empty" data-state="not_found">
        <h2 class="ml-empty__title">This content is unavailable</h2>
        <p class="ml-empty__body">It may have been moved or withdrawn.</p>
        <a class="ml-btn ml-btn--primary" href="${C}content_home/index.html">Back to guides</a>
      </div>

      <div class="ml-empty" data-state="offline">
        <h2 class="ml-empty__title">You're offline</h2>
        <p class="ml-empty__body">This is the copy we saved last time. It may have changed since.</p>
      </div>
    </main>
  </div>`,
});

/* ── the four site pages, unchanged in intent, now Beta-badged ──────────── */
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
          .map(([t, b]) => `<div class="ml-feature"><b>${t}</b><p>${b}</p></div>`).join("\n        ")}
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
      <div data-state="default">
        <div class="ml-section"><h2>About</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3);${READ}">
          MusicLinkd is built in Mumbai for the people who actually make music here — session players,
          engineers, studios and venues who have been finding each other through group chats and word of mouth.
        </p>
        ${rowLink("Pricing", "?state=pricing", "Free, Pro and Business")}
        ${rowLink("Careers", "?state=careers", "3 open roles")}
      </div>

      <div data-state="pricing">
        <div class="ml-section"><h2>Pricing</h2></div>
        <div class="ml-tiers">
          ${[["Free", "₹0", ["A public profile", "Apply to opportunities", "Message your connections"], false],
             ["Pro", "₹499/mo", ["Everything in Free", "Priority in Discover", "Analytics", "Direct booking"], true],
             ["Business", "₹1,499/mo", ["Everything in Pro", "Team accounts", "Room management", "Invoicing"], false]]
            .map(([name, price, feats, feat]) => `<div class="ml-tier${feat ? " ml-tier--featured" : ""}">
            <b>${name}</b>
            <span class="ml-value">${price}</span>
            ${feats.map((f) => `<p>${f}</p>`).join("\n            ")}
          </div>`).join("\n          ")}
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="/preview/flows/subscription/plans/index.html">Compare in the app</a>
      </div>

      <div data-state="careers">
        <div class="ml-section"><h2>Open roles</h2></div>
        <div class="ml-summary">
          ${[["Android engineer", "Mumbai · Full time"], ["Product designer", "Mumbai · Full time"],
             ["Community lead", "Mumbai · Contract"]]
            .map(([r, m]) => `<div class="ml-summary__row"><span>${r}</span><span>${m}</span></div>`).join("\n          ")}
        </div>
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
      <div class="ml-articles" data-state="default">
        ${[["Rates, in the open", "28 Aug 2026", "Why we made day rates a first-class field instead of something you negotiate in DMs.", 3],
           ["The marketplace is live", "14 Aug 2026", "Buy, sell and rent gear, with offers and a deposit held until both sides are happy.", 7],
           ["Verification, explained", "02 Aug 2026", "What the badge means, what we check, and what we deliberately do not.", 11]]
          .map(([t, d, b, i]) => `<a class="ml-article" href="${C}content_article/index.html">
          <img src="${media("stage", i)}" alt="" loading="lazy" />
          <div>
            <b>${t}</b>
            <span class="ml-type-1" style="color:var(--color-muted)">${d}</span>
            <p>${b}</p>
          </div>
        </a>`).join("\n        ")}
      </div>

      <div data-state="article">
        ${rowLink("Read this post", `${C}content_article/index.html`, "Opens in the reader")}
      </div>
      <div data-state="announcement">
        ${rowLink("Offers now expire after 48 hours", `${C}content_article/index.html?state=announcement`, "31 August 2026")}
      </div>
      <div class="ml-empty" data-state="archived">
        <h2 class="ml-empty__title">This post is archived</h2>
        <p class="ml-empty__body">It described how things worked before a change. Kept for the record.</p>
      </div>
      <div class="ml-empty" data-state="not_found">
        <h2 class="ml-empty__title">This post is unavailable</h2>
        <a class="ml-btn ml-btn--primary" href="?state=default">Back to the blog</a>
      </div>
    </main>
  </div>`,
});

writeScreen({
  flow: "cms_pages", screen: "legal_pages_site", title: "CMS — Legal",
  body: `
  <div class="ml-app">
    ${bar("Legal pages", `${C}admin_pages_editor/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          The public versions of what the app shows in Settings. Edited in Admin, published to the website.
        </p>
        <div class="ml-summary">
          ${[["Terms of service", "14 June 2026"], ["Privacy policy", "14 June 2026"],
             ["Community guidelines", "2 August 2026"], ["Marketplace rules", "14 August 2026"]]
            .map(([t, m]) => `<div class="ml-summary__row"><span>${t}</span><span>${m}</span></div>`).join("\n          ")}
        </div>
        ${rowLink("Read the marketplace rules", `${C}content_article/index.html?state=policy`)}
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-3)" href="/preview/flows/user_settings/legal_pages/index.html">See them in the app</a>
      </div>

      <div data-state="policy">
        ${rowLink("Marketplace rules", `${C}content_article/index.html?state=policy`, "Version 2.1 · 14 August 2026")}
      </div>

      <div class="ml-empty" data-state="restricted">
        <h2 class="ml-empty__title">You don't have access to this</h2>
        <p class="ml-empty__body">Some legal documents are only shown to business accounts.</p>
      </div>
    </main>
  </div>`,
});

/* ── admin_pages_editor — a LIST, not an editor ─────────────────────────── */
/* The brief is explicit: Flow 22 is the reader's journey and authoring belongs
   to Admin. So this shows what content exists and hands off; it is deliberately
   not a WYSIWYG. */
writeScreen({
  flow: "cms_pages", screen: "admin_pages_editor", title: "CMS — Pages",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Pages</h1>
      ${beta}
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
        What's published on musiclinkd.com. Writing and editing happen in Admin — this is the reader's
        view of what exists.
      </p>
      ${rowLink("Guides & help", `${C}content_home/index.html`, "42 articles across 6 categories")}
      ${rowLink("Homepage", `${C}marketing_homepage/index.html`, "Hero, features, call to action")}
      ${rowLink("About, pricing & careers", `${C}about_pricing_careers/index.html`, "3 open roles")}
      ${rowLink("Blog & changelog", `${C}blog_changelog/index.html`, "3 posts")}
      ${rowLink("Legal pages", `${C}legal_pages_site/index.html`, "4 documents")}
    </main>
  </div>`,
});

finish("cms_pages: ");
