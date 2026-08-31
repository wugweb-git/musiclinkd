#!/usr/bin/env node
/**
 * Flow 18 — Subscription / Monetization, against the deferred-flows brief.
 * BETA.
 *
 * This flow had NO screens before today, and no schema either: MASTER-REGISTRY
 * records subscription as "not modelled at all — no subscription/plan/tier
 * collection exists anywhere". Designing it does not change that, which is
 * exactly why every screen says Beta.
 *
 * 18 sections, 4 screens. MONETIZE-05/06/07 (processing, success, failure) are
 * states of checkout. MONETIZE-10/11/12 (upgrade, downgrade, cancel) are states
 * of manage_subscription. MONETIZE-03 (plan detail) is a state of plans.
 *
 * The brief's rule — "Do not use dark patterns" — is the design, not a note on
 * it. Concretely, in this file:
 *
 *   · Cancelling is a plain path. No interstitial discount, no "are you sure
 *     you want to lose X", no guilt copy. It says what you keep and when access
 *     ends, and the button says Cancel.
 *   · Downgrade shows what you LOSE before it will confirm, and says when.
 *   · The promotion state shows the real price alongside the offer price and a
 *     real end date. No countdown, no fake scarcity.
 *   · Feature limits explain which limit and what lifts it, and never block
 *     something unrelated.
 *   · Payment credentials are named, never entered — this is a design layer.
 *
 * Prices reuse the tiers already published on the marketing page rather than
 * inventing new ones.
 *
 * No new CSS: .ml-tier(s), .ml-value, .ml-summary, .ml-choice, .ml-banner,
 * .ml-empty and .ml-status already exist.
 */
import { writeScreen, backArrow, icon, assertIcons, beta, finish, media } from "../write-screen.mjs";

const S = "/preview/flows/subscription/";
assertIcons(["arrow-forward", "check", "cancel"]);

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-3" style="margin:0;flex:1">${title}</h1>
      ${beta}
    </header>`;

const rowLink = (label, href, meta) => `<a class="ml-action-row" href="${href}">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>`;

const PLANS = [
  ["free", "Free", "₹0", "Always", [
    "A public profile", "Apply to opportunities", "Message your connections",
    "3 portfolio pieces", "Marketplace buying",
  ]],
  ["pro", "Pro", "₹499", "per month", [
    "Everything in Free", "Unlimited portfolio", "Priority in Discover",
    "Profile analytics", "Direct booking with deposits", "Saved searches with alerts",
  ]],
  ["business", "Business", "₹1,499", "per month", [
    "Everything in Pro", "Team accounts and roles", "Room and availability management",
    "Invoicing", "Business analytics", "Multiple businesses",
  ]],
];

/* ── plans — comparison, and each plan's detail as a state ──────────────── */
writeScreen({
  flow: "subscription", screen: "plans", title: "Plans",
  body: `
  <div class="ml-app">
    ${bar("Plans", "/preview/flows/user_settings/account_settings/index.html")}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div class="ml-banner ml-banner--info" data-state="default free pro business">
        <span class="ml-banner__body">You're on Free. Nothing here charges you until you choose it.</span>
      </div>

      <!-- Comparison. Every row is a real capability, not a checkmark grid. -->
      <div class="ml-tiers" data-state="default">
        ${PLANS.map(([key, name, price, cycle, feats]) => `<a class="ml-tier${key === "pro" ? " ml-tier--featured" : ""}" href="?state=${key}">
          <b>${name}</b>
          <span class="ml-value">${price}</span>
          <p class="ml-type-1" style="color:var(--color-muted)">${cycle}</p>
          ${feats.slice(0, 3).map((f) => `<p>${f}</p>`).join("\n          ")}
        </a>`).join("\n        ")}
      </div>

      <p class="ml-type-1" data-state="default" style="color:var(--color-muted);margin-top:var(--space-2)">
        Prices include GST. You can change or cancel at any time, and cancelling takes one tap
        from Manage subscription.
      </p>

      ${PLANS.map(([key, name, price, cycle, feats]) => `<div data-state="${key}">
        <div class="ml-section"><h2>${name}</h2></div>
        <p class="ml-type-4" style="margin:0">${price} <span class="ml-type-1" style="color:var(--color-muted)">${cycle}</span></p>
        <div class="ml-section" style="margin-top:var(--space-3)"><h3>What you get</h3></div>
        <div class="ml-summary">
          ${feats.map((f) => `<div class="ml-summary__row"><span>${f}</span><span>Included</span></div>`).join("\n          ")}
        </div>
        ${key === "free" ? `<p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Free is a real plan, not a trial. It does not expire.
        </p>` : `<a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="${S}checkout/index.html">Choose ${name}</a>`}
      </div>`).join("\n      ")}

      <!-- Trial, only described where one genuinely exists. -->
      <div data-state="trial">
        <div class="ml-banner ml-banner--info">
          <span class="ml-banner__body">You're on a Pro trial. It ends on 14 September.</span>
        </div>
        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>Trial ends</span><span>14 September 2026</span></div>
          <div class="ml-summary__row"><span>Then</span><span>₹499/month, unless you cancel</span></div>
          <div class="ml-summary__row"><span>If you cancel</span><span>You keep Pro until the 14th, then go to Free</span></div>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          We'll email you three days before it ends. Cancelling during a trial costs nothing.
        </p>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-3)" href="${S}manage_subscription/index.html?state=cancel">Cancel the trial</a>
      </div>

      <!-- A promotion states the real price beside the offer price and a real
           end date. No countdown, no invented scarcity. -->
      <div data-state="promotion">
        <div class="ml-section"><h2>Launch pricing</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Pro, normally</span><span>₹499/month</span></div>
          <div class="ml-summary__row"><span>Until 31 October 2026</span><span>₹299/month</span></div>
          <div class="ml-summary__row"><span>After that</span><span>₹499/month</span></div>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          The lower price holds for twelve months from the day you start, even after the offer closes.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="${S}checkout/index.html">Choose Pro at ₹299</a>
      </div>

      <!-- Reached a limit. Say WHICH limit, what lifts it, and nothing else
           stops working. -->
      <div data-state="feature_limit">
        <div class="ml-banner ml-banner--warning">
          <span class="ml-banner__body">You've used all 3 portfolio pieces on Free.</span>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          Your existing work stays published and everything else on your account is unaffected —
          you just can't add a fourth piece on Free. Pro removes the limit.
        </p>
        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>Free</span><span>3 pieces</span></div>
          <div class="ml-summary__row"><span>Pro</span><span>Unlimited</span></div>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=pro">See Pro</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="/preview/flows/portfolio/portfolio_dashboard/index.html">Replace an existing piece instead</a>
      </div>
    </main>
  </div>`,
});

/* ── checkout — review / processing / success / failure ─────────────────── */
writeScreen({
  flow: "subscription", screen: "checkout", title: "Checkout",
  body: `
  <div class="ml-app">
    ${bar("Checkout", `${S}plans/index.html?state=pro`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="review">
        <div class="ml-section"><h2>What you're paying for</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Pro</span><span>₹423.73</span></div>
          <div class="ml-summary__row"><span>GST at 18%</span><span>₹76.27</span></div>
          <div class="ml-summary__row ml-summary__row--total"><span>Today</span><span>₹499.00</span></div>
          <p class="ml-summary__note">Then ₹499 on the 31st of each month, until you cancel.</p>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Pay with</h2></div>
        <div role="radiogroup" aria-label="Payment method">
          ${[["UPI", "Pay from any UPI app", true],
             ["Card ending 6411", "Visa · expires 09/28", false],
             ["Net banking", "All major banks", false]]
            .map(([t, m, on]) => `<div class="ml-choice" role="radio" aria-checked="${on}" tabindex="${on ? 0 : -1}">
            <span class="ml-choice__body">
              <span class="ml-choice__title">${t}</span>
              <span class="ml-choice__meta">${m}</span>
            </span>
            <span class="ml-choice__mark"></span>
          </div>`).join("\n          ")}
        </div>

        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-3)">
          By subscribing you agree to the
          <a class="ml-link" href="/preview/flows/user_settings/legal_pages/index.html">terms</a>.
          You can cancel any time and keep access until the period you've paid for ends.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=processing">Subscribe · ₹499</a>
      </div>

      <!-- Processing hides the button entirely, so it cannot be pressed twice. -->
      <div class="ml-empty" data-state="processing">
        <span class="ml-spinner" role="status" aria-label="Taking payment"></span>
        <p class="ml-empty__body" style="margin-top:var(--space-3)">
          Taking payment. Don't close this — it usually takes a few seconds.
        </p>
      </div>

      <div class="ml-empty" data-state="success">
        <h2 class="ml-empty__title" style="color:var(--color-success)">You're on Pro</h2>
        <p class="ml-empty__body">
          ₹499 paid. Renews on 30 September 2026. A receipt is on its way to your email.
        </p>
        <a class="ml-btn ml-btn--primary" href="${S}manage_subscription/index.html">Manage subscription</a>
      </div>

      <div data-state="failure">
        <div class="ml-banner ml-banner--error">
          <span class="ml-banner__body">Payment couldn't be completed. You have not been charged.</span>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          Your bank declined it. That is usually a limit on the card rather than anything wrong
          with the account.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=review">Try another method</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="${S}plans/index.html">Back to plans</a>
      </div>
    </main>
  </div>`,
});

/* ── manage_subscription — upgrade / downgrade / cancel as states ───────── */
writeScreen({
  flow: "subscription", screen: "manage_subscription", title: "Subscription",
  body: `
  <div class="ml-app">
    ${bar("Subscription", "/preview/flows/user_settings/account_settings/index.html")}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="active">
        <div class="ml-section"><h2>Your plan</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Plan</span><span>Pro</span></div>
          <div class="ml-summary__row"><span>Status</span><span>Active</span></div>
          <div class="ml-summary__row"><span>Renews</span><span>30 September 2026</span></div>
          <div class="ml-summary__row"><span>Amount</span><span>₹499/month</span></div>
          <div class="ml-summary__row"><span>Paid with</span><span>UPI</span></div>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Change</h2></div>
        ${rowLink("Upgrade to Business", "?state=upgrade", "Team accounts, invoicing, multiple businesses")}
        ${rowLink("Move down to Free", "?state=downgrade", "See what you'd lose first")}
        ${rowLink("Change payment method", `${S}checkout/index.html?state=review`)}
        ${rowLink("Invoices", `${S}invoices/index.html`, "3 invoices")}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Cancel</h2></div>
        ${rowLink("Cancel subscription", "?state=cancel")}
      </div>

      <div data-state="upgrade">
        <div class="ml-section"><h2>Pro → Business</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>You pay now</span><span>₹1,000 for the rest of this month</span></div>
          <div class="ml-summary__row"><span>From 30 September</span><span>₹1,499/month</span></div>
          <div class="ml-summary__row"><span>Takes effect</span><span>Immediately</span></div>
        </div>
        <div class="ml-section" style="margin-top:var(--space-3)"><h3>What you gain</h3></div>
        <div class="ml-summary">
          ${["Team accounts and roles", "Room and availability management", "Invoicing", "Multiple businesses"]
            .map((f) => `<div class="ml-summary__row"><span>${f}</span><span>New</span></div>`).join("\n          ")}
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="${S}checkout/index.html">Confirm upgrade</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=active">Cancel</a>
      </div>

      <!-- Downgrade shows the LOSS before it will confirm. -->
      <div data-state="downgrade">
        <div class="ml-section"><h2>Pro → Free</h2></div>
        <div class="ml-banner ml-banner--warning">
          <span class="ml-banner__body">You keep Pro until 30 September. Free starts the day after.</span>
        </div>
        <div class="ml-section" style="margin-top:var(--space-3)"><h3>What changes on 1 October</h3></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Portfolio</span><span>Limited to 3 pieces</span></div>
          <div class="ml-summary__row"><span>Extra pieces</span><span>Hidden, not deleted</span></div>
          <div class="ml-summary__row"><span>Priority in Discover</span><span>Ends</span></div>
          <div class="ml-summary__row"><span>Analytics</span><span>Ends; past data kept</span></div>
          <div class="ml-summary__row"><span>Direct booking</span><span>Ends; existing bookings stand</span></div>
          <div class="ml-summary__row"><span>Saved search alerts</span><span>Stop</span></div>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=active">Move to Free</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=active">Stay on Pro</a>
      </div>

      <!-- Cancelling is a plain path. No retention offer, no guilt. -->
      <div data-state="cancel">
        <div class="ml-section"><h2>Cancel your subscription</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Access until</span><span>30 September 2026</span></div>
          <div class="ml-summary__row"><span>Then</span><span>Your account moves to Free</span></div>
          <div class="ml-summary__row"><span>Your work</span><span>Stays; extra pieces are hidden, not deleted</span></div>
          <div class="ml-summary__row"><span>Bookings already made</span><span>Unaffected</span></div>
          <div class="ml-summary__row"><span>Coming back</span><span>Subscribe again any time</span></div>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=cancelled">Cancel subscription</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=active">Keep it</a>
      </div>

      <div class="ml-empty" data-state="cancelled">
        <h2 class="ml-empty__title">Subscription cancelled</h2>
        <p class="ml-empty__body">
          You keep Pro until 30 September 2026. Nothing more will be charged.
        </p>
        <a class="ml-btn ml-btn--primary" href="${S}plans/index.html">See plans</a>
      </div>

      <div data-state="past_due">
        <div class="ml-banner ml-banner--error">
          <span class="ml-banner__body">We couldn't take September's payment.</span>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          Pro stays on for another seven days while you sort it out. After that the account
          moves to Free — nothing is deleted.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="${S}checkout/index.html?state=review">Update payment method</a>
      </div>
    </main>
  </div>`,
});

/* ── invoices ───────────────────────────────────────────────────────────── */
writeScreen({
  flow: "subscription", screen: "invoices", title: "Invoices",
  body: `
  <div class="ml-app">
    ${bar("Invoices", `${S}manage_subscription/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        ${[["31 Aug 2026", "₹499.00", "Paid", "success"],
           ["31 Jul 2026", "₹499.00", "Paid", "success"],
           ["30 Jun 2026", "₹499.00", "Paid", "success"]]
          .map(([d, a, st, kind]) => `<a class="ml-action-row" href="?state=receipt">
          <span class="ml-choice__body">
            <span class="ml-choice__title">${d}</span>
            <span class="ml-choice__meta">Pro · monthly</span>
          </span>
          <span class="ml-result__trail">${a}</span>
          <span class="ml-status ml-status--${kind}">${st}</span>
        </a>`).join("\n        ")}
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Invoices are kept for seven years, as the law requires — including after you cancel.
        </p>
      </div>

      <div data-state="receipt">
        <div class="ml-section"><h2>Receipt · 31 August 2026</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Plan</span><span>Pro, monthly</span></div>
          <div class="ml-summary__row"><span>Period</span><span>31 Aug – 29 Sep 2026</span></div>
          <div class="ml-summary__row"><span>Subtotal</span><span>₹423.73</span></div>
          <div class="ml-summary__row"><span>GST at 18%</span><span>₹76.27</span></div>
          <div class="ml-summary__row ml-summary__row--total"><span>Paid</span><span>₹499.00</span></div>
          <div class="ml-summary__row"><span>Method</span><span>UPI</span></div>
          <div class="ml-summary__row"><span>Reference</span><span>ML-INV-20826</span></div>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=default">Download PDF</a>
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No invoices yet</h2>
        <p class="ml-empty__body">You're on Free, so there's nothing to bill.</p>
        <a class="ml-btn ml-btn--primary" href="${S}plans/index.html">See plans</a>
      </div>
    </main>
  </div>`,
});

finish("subscription: ");
