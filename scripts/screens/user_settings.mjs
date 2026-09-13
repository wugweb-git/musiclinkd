#!/usr/bin/env node
/**
 * Flow 21 — Settings & Support. Thirteen screens, 23 declared states, and all
 * thirteen were still the raw "Not designed yet" scaffold.
 *
 * Thirteen screens, but only THREE shapes — which is the whole point:
 *
 *   settingsScreen()  a list of labelled rows, each a switch, a value, or a
 *                     link onward. Serves account / appearance / notification
 *                     / privacy / security — five screens, one composer.
 *   formScreen()      a short form with default → submitting → success. Serves
 *                     feedback / raise_ticket / report_listing / report_user —
 *                     four screens, twelve states, one composer.
 *   docScreen()       long-form copy with an optional search. Serves
 *                     help_center / faq / legal_pages / app_version.
 *
 * Nothing here is new CSS. The rows are .ml-check-row + .ml-switch, values are
 * .ml-summary__row, links onward are .ml-action-row, forms are .ml-field, and
 * the submitting/success states reuse .ml-empty exactly as Booking's payment
 * screen and Opportunities' apply screen already do.
 */
import { writeScreen, backArrow, icon, assertIcons, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/user_settings/";
assertIcons(["arrow-forward", "search", "check"]);

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-4" style="margin:0">${title}</h1>
    </header>`;

/* A settings row is one of three things and nothing else. Keeping that closed
   is what stops thirteen screens drifting into thirteen private layouts. */
const rowSwitch = (label, meta, on) => `<div class="ml-check-row">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        <button class="ml-switch" type="button" role="switch" aria-checked="${on}" aria-label="${label}"></button>
      </div>`;
const rowValue = (label, value) => `<div class="ml-summary__row"><span>${label}</span><span>${value}</span></div>`;
const rowLink = (label, href, meta) => `<a class="ml-action-row" href="${href}">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>`;

/* A visibility control opened in place: heading, the options with their real
   consequences spelled out, and a caveat. Four of the FLOW-21 brief's numbered
   "screens" (profile visibility, contact privacy, online status, search
   visibility) are this one block with different options, so they are states of
   privacy_settings rather than four more screens. */
const radioBlock = (state, heading, options, caveat) => `<div data-state="${state}">
        <div class="ml-section"><h2>${heading}</h2></div>
        <div role="radiogroup" aria-label="${heading}">
          ${options.map(([t, m, on]) => `<div class="ml-choice" role="radio" aria-checked="${on}" tabindex="${on ? 0 : -1}">
            <span class="ml-choice__body">
              <span class="ml-choice__title">${t}</span>
              ${m ? `<span class="ml-choice__meta">${m}</span>` : ""}
            </span>
            <span class="ml-choice__mark"></span>
          </div>`).join("\n          ")}
        </div>
        ${caveat ? `<p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">${caveat}</p>` : ""}
      </div>`;

/* baseStates: which states the headed sections appear in.
   - privacy/appearance/security open a control IN PLACE, so the list is
     "default" only and the sub-state replaces it.
   - account_settings' saving/saved/error are BANNERS over the same list, so
     its sections stay visible in every state. */
const settingsScreen = ({ screen, title, backTo, sections, baseStates }) =>
  writeScreen({
    flow: "user_settings", screen, title: `Settings — ${title}`,
    body: `
  <div class="ml-app">
    ${bar(title, backTo)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${sections.map(([heading, rows]) => heading
        ? `<div${baseStates ? ` data-state="${baseStates}"` : ""}>
        <div class="ml-section"><h2>${heading}</h2></div>
        ${rows.join("\n        ")}
      </div>`
        : rows.join("\n      ")).join("\n\n      ")}
    </main>
  </div>`,
  });

/* default → submitting → success, the same three-state shape the payment and
   apply screens use. Four screens share it. */
const formScreen = ({ screen, title, backTo, intro, fields, cta, successTitle, successBody }) =>
  writeScreen({
    flow: "user_settings", screen, title: `Settings — ${title}`,
    body: `
  <div class="ml-app">
    ${bar(title, backTo)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        ${intro ? `<p class="ml-type-3" style="color:var(--color-muted);margin:0 0 var(--space-3)">${intro}</p>` : ""}
        ${fields.join("\n        ")}
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=submitting">${cta}</a>
      </div>

      <div class="ml-empty" data-state="submitting">
        <span class="ml-spinner" role="status" aria-label="Sending"></span>
        <p class="ml-empty__body" style="margin-top:var(--space-3)">Sending…</p>
      </div>

      <div class="ml-empty" data-state="success">
        <h2 class="ml-empty__title" style="color:var(--color-success)">${successTitle}</h2>
        <p class="ml-empty__body">${successBody}</p>
        <a class="ml-btn ml-btn--primary" href="${backTo}">Done</a>
      </div>
    </main>
  </div>`,
  });

const field = (id, label, type, value, ph) => `<div class="ml-field">
          <label for="${id}">${label}</label>
          ${type === "textarea"
            ? `<textarea id="${id}" rows="4"${ph ? ` placeholder="${ph}"` : ""}>${value || ""}</textarea>`
            : type === "select"
              ? `<select id="${id}">${value.map((o) => `<option>${o}</option>`).join("")}</select>`
              : `<input id="${id}" type="${type}" value="${value || ""}"${ph ? ` placeholder="${ph}"` : ""} />`}
        </div>`;

/* ══ the five settings lists ═══════════════════════════════════════════════ */

settingsScreen({
  screen: "account_settings", title: "Account", backTo: `${P}help_center/index.html`,
  sections: [
    ["Account", [
      rowValue("Email", "vedanshu@wugweb.com"),
      rowValue("Phone", "+91 98••• ••432"),
      rowValue("Member since", "March 2024"),
    ]],
    ["Account", [
      rowLink("Username", `${P}username/index.html`, "musiclinkd.com/ishitarao"),
      rowLink("Email and phone", `${P}change_email_phone/index.html`, "Changing either needs verifying"),
    ]],
    ["Manage", [
      rowLink("Notifications", `${P}notification_settings/index.html`, "What you're told about, and how"),
      rowLink("Data and privacy", `${P}data_privacy/index.html`, "Export your data, personalisation"),
      rowLink("Appearance", `${P}appearance_settings/index.html`, "Text size, motion, contrast, language"),
      rowLink("Change password", `${P}security_settings/index.html`),
      rowLink("Privacy", `${P}privacy_settings/index.html`, "Who can see your profile and contact you"),
      // Deleting an account is a handoff with real consequences, not a row you
      // can hit by accident — it opens the support ticket flow.
      rowLink("Deactivate or delete", `${P}delete_account/index.html`, "Take a break, or leave for good"),
    ]],
    ["", [
      // The brief's save contract: quiet on success, explicit on failure.
      `<div data-state="saving"><div class="ml-banner ml-banner--info"><span class="ml-banner__body">Saving…</span></div></div>`,
      `<div data-state="saved"><div class="ml-banner ml-banner--success"><span class="ml-banner__body">Saved.</span></div></div>`,
      `<div data-state="error"><div class="ml-banner ml-banner--error"><span class="ml-banner__body">We couldn't save your changes. <a class="ml-link" href="?state=editing">Retry</a></span></div></div>`,
      `<div data-state="editing"><div class="ml-banner ml-banner--warning"><span class="ml-banner__body">You have unsaved changes.</span></div></div>`,
    ]],
  ],
});

settingsScreen({
  screen: "appearance_settings", title: "Appearance", backTo: `${P}account_settings/index.html`,
  baseStates: "default",
  sections: [
    ["Display", [
      rowSwitch("Larger text", "Follows your device text size", true),
      rowSwitch("Reduce motion", "Fewer transitions and no autoplaying loops", false),
      rowSwitch("High contrast", "Stronger separators and heavier text", false),
    ]],
    ["Language", [
      rowValue("App language", "English (India)"),
      rowValue("Content languages", "Hindi, English"),
    ]],
    ["", [
      radioBlock("language", "App language", [
        ["English (India)", "", true],
        ["हिन्दी", "Partial — some screens stay in English.", false],
        ["मराठी", "Partial — some screens stay in English.", false],
      ], "Only languages we have actually translated are listed."),
      radioBlock("accessibility", "Accessibility", [
        ["Follow my device", "Text size, contrast and motion come from Android settings.", true],
        ["Larger text in the app only", "", false],
        ["Reduce motion in the app only", "", false],
      ], "MusicLinkd cannot change your device settings — these apply inside the app."),
    ]],
  ],
});

settingsScreen({
  screen: "notification_settings", title: "Notifications", backTo: `${P}account_settings/index.html`,
  sections: [
    ["Push", [
      rowSwitch("Messages", "Someone sends you a message", true),
      rowSwitch("Connection requests", null, true),
      rowSwitch("Opportunity updates", "Viewed, shortlisted, accepted", true),
      rowSwitch("Booking reminders", "24 hours before a session", true),
      rowSwitch("Marketplace offers", "Offers and counters on your listings", false),
    ]],
    ["Email", [
      rowSwitch("Weekly summary", null, false),
      rowSwitch("Product updates", null, false),
    ]],
  ],
});

settingsScreen({
  screen: "privacy_settings", title: "Privacy", backTo: `${P}account_settings/index.html`,
  baseStates: "default",
  sections: [
    ["Who can see you", [
      rowSwitch("Show profile in Discover", "Off means only people with your link can find you", true),
      rowSwitch("Show my location", "City only — never a precise address", true),
      rowSwitch("Show my rates publicly", null, false),
    ]],
    ["Who can reach you", [
      rowSwitch("Allow messages from anyone", "Off means connections only", false),
      rowSwitch("Allow booking requests", null, true),
    ]],
    ["Blocking", [
      rowLink("Blocked accounts", `${P}blocked_accounts/index.html`, "2 accounts"),
    ]],
    ["", [
      // Each visibility control opens IN PLACE as a state rather than as its
      // own screen — the options and their consequences are the whole content.
      radioBlock("profile_visibility", "Who can see your profile", [
        ["Public", "Anyone, including people not on MusicLinkd. Your profile can be found by search engines.", true],
        ["MusicLinkd members", "Only people signed in to MusicLinkd.", false],
        ["Private", "Only people you have connected with.", false],
      ], "Making your profile private does not remove links people have already shared."),
      radioBlock("contact_privacy", "Who can see your contact details", [
        ["Everyone", "Phone, email and website on your public profile.", false],
        ["Members", "Signed-in people only.", true],
        ["Connections", "People you have connected with.", false],
        ["Only me", "Nobody. People message you through MusicLinkd.", false],
      ]),
      radioBlock("online_status", "Online status", [
        ["Show when I'm active", "Others may see roughly when you were last here.", true],
        ["Hide it", "Nobody sees your activity. You also stop seeing theirs.", false],
      ], "Activity is approximate, not live to the second."),
      radioBlock("search_visibility", "Where you appear", [
        ["Search, Discover and matches", "The default. You can be found and suggested.", true],
        ["Search only", "Findable if someone looks for you, but not suggested.", false],
        ["Nowhere", "Only people with your link can reach you.", false],
      ], "Hiding from search does not delete a public profile URL you have already shared."),
    ]],
  ],
});

settingsScreen({
  screen: "security_settings", title: "Security", backTo: `${P}account_settings/index.html`,
  baseStates: "default two_factor_setup",
  sections: [
    ["Sign-in", [
      rowValue("Password", "Last changed 4 months ago"),
      rowSwitch("Two-factor authentication", "A code by SMS each time you sign in on a new device", false),
      rowLink("Signed-in devices", `${P}active_sessions/index.html`, "3 devices"),
    ]],
    ["", [
      `<div data-state="two_factor_on">
        <div class="ml-banner ml-banner--success"><span class="ml-banner__body">Two-factor is on. You'll be asked for a code on new devices.</span></div>
        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>Method</span><span>SMS to +91 98••• ••432</span></div>
          <div class="ml-summary__row"><span>Recovery codes</span><span>8 unused</span></div>
        </div>
      </div>`,
      `<div data-state="connected_accounts">
        <div class="ml-section"><h2>Connected accounts</h2></div>
        <div class="ml-check-row">
          <span class="ml-choice__body"><span class="ml-choice__title">Google</span>
          <span class="ml-choice__meta">ishita@example.com</span></span>
          <button class="ml-btn ml-btn--secondary" type="button">Disconnect</button>
        </div>
        <div class="ml-check-row">
          <span class="ml-choice__body"><span class="ml-choice__title">Apple</span>
          <span class="ml-choice__meta">Not connected</span></span>
          <button class="ml-btn ml-btn--secondary" type="button">Connect</button>
        </div>
      </div>`,
      `<div data-state="security_alert">
        <div class="ml-banner ml-banner--warning"><span class="ml-banner__body">New sign-in detected.</span></div>
        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>Device</span><span>Chrome on Windows</span></div>
          <div class="ml-summary__row"><span>When</span><span>Today, 09:14</span></div>
          <div class="ml-summary__row"><span>Where</span><span>Pune, approximately</span></div>
        </div>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-3)" href="${P}active_sessions/index.html">Review devices</a>
      </div>`,
    ]],
    ["Set up two-factor", [
      `<div data-state="two_factor_setup">
        <p class="ml-type-3" style="color:var(--color-muted);margin:0 0 var(--space-2)">
          We'll send a six-digit code to +91 98••• ••432 whenever you sign in on a device we don't recognise.
        </p>
        ${field("tf-phone", "Mobile number", "tel", "+91 98••• ••432")}
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=default">Send code</a>
      </div>`,
    ]],
  ],
});

/* ══ the four forms ════════════════════════════════════════════════════════ */

formScreen({
  screen: "feedback", title: "Send feedback", backTo: `${P}help_center/index.html`,
  intro: "Tell us what's working and what isn't. This goes straight to the product team.",
  fields: [
    field("fb-kind", "About", "select", ["Something is broken", "An idea", "General feedback"]),
    field("fb-body", "Your feedback", "textarea", "", "As much detail as you can give us."),
  ],
  cta: "Send feedback",
  successTitle: "Thanks — that's with us",
  successBody: "We read everything. If we need more detail we'll reply by email.",
});

formScreen({
  screen: "raise_ticket", title: "Contact support", backTo: `${P}help_center/index.html`,
  intro: "Support replies within one working day.",
  fields: [
    field("t-kind", "Topic", "select", ["Bookings", "Payments", "My account", "Marketplace", "Something else"]),
    field("t-subject", "Subject", "text", ""),
    field("t-body", "What happened?", "textarea", "", "Include a booking reference or listing link if you have one."),
  ],
  cta: "Raise ticket",
  successTitle: "Ticket raised",
  successBody: "Reference SUP-2291. We'll email you at vedanshu@wugweb.com.",
});

formScreen({
  screen: "report_listing", title: "Report listing", backTo: "/preview/flows/marketplace/marketplace_item_detail/index.html",
  intro: "Reporting is anonymous. The seller is not told who reported the listing.",
  fields: [
    field("rl-reason", "Reason", "select", ["Counterfeit or misrepresented", "Prohibited item", "Wrong price or spam", "Someone else's photos", "Something else"]),
    field("rl-body", "Anything else we should know?", "textarea", "", "Optional."),
  ],
  cta: "Report listing",
  successTitle: "Report received",
  successBody: "We'll review this listing. You won't see it in your feed while we do.",
});

formScreen({
  screen: "report_user", title: "Report account", backTo: "/preview/flows/communication/networking_feed_artist_profile/index.html",
  intro: "Reporting is anonymous. Blocking is immediate and separate from this report.",
  fields: [
    field("ru-reason", "Reason", "select", ["Harassment", "Impersonation", "Spam", "Payment dispute", "Something else"]),
    field("ru-body", "What happened?", "textarea", "", "Optional, but it helps us act faster."),
  ],
  cta: "Report account",
  successTitle: "Report received",
  successBody: "We'll review this account. You can block them from their profile at any time.",
});

/* ══ the four document screens ═════════════════════════════════════════════ */

const HELP_TOPICS = [
  ["Bookings", "Holds, deposits, cancellations", `${P}faq/index.html`],
  ["Payments and payouts", "When you're paid, and how", `${P}faq/index.html`],
  ["Marketplace", "Selling, renting, and offers", `${P}faq/index.html`],
  ["Your account", "Profile, privacy, verification", `${P}account_settings/index.html`],
];

writeScreen({
  flow: "user_settings", screen: "help_center", title: "Settings — Help",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-4" style="margin:0">Help &amp; support</h1>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-field">
        <label for="h-q">Search help</label>
        <input id="h-q" type="search" placeholder="Deposits, cancellations, payouts…" />
      </div>

      <div data-state="searching">
        <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-2) 0">3 results for "deposit"</p>
        ${[["When is the deposit taken?", "Bookings"], ["Can I cancel a booking?", "Bookings"], ["When do I get paid?", "Payments"]]
          .map(([q, c]) => rowLink(q, `${P}faq/index.html?state=searching`, c)).join("\n        ")}
      </div>

      <div class="ml-empty" data-state="no_results">
        <h2 class="ml-empty__title">No help articles found</h2>
        <p class="ml-empty__body">Try "deposit", "cancellation" or "payout" — or ask us directly.</p>
        <a class="ml-btn ml-btn--primary" href="${P}raise_ticket/index.html">Contact support</a>
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)" data-state="default"><h2>Topics</h2></div>
      ${HELP_TOPICS.map(([t, m, h]) => rowLink(t, h, m)).join("\n      ")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Still stuck?</h2></div>
      ${rowLink("Contact support", `${P}raise_ticket/index.html`, "Replies within one working day")}
      ${rowLink("Send feedback", `${P}feedback/index.html`, "Ideas and bug reports")}
      ${rowLink("Report a listing", `${P}report_listing/index.html`, "Counterfeit, prohibited, or someone else's photos")}
      ${rowLink("Report an account", `${P}report_user/index.html`)}
      ${rowLink("Settings", `${P}account_settings/index.html`, "Account, privacy, notifications")}
      ${rowLink("Terms and privacy", `${P}legal_pages/index.html`)}
      ${rowLink("About this app", `${P}app_version/index.html`)}
    </main>
  </div>`,
});

const FAQS = [
  ["When is the deposit taken?", "The deposit is charged when the studio confirms your slot, not when you request it. If they decline, nothing is charged."],
  ["Can I cancel a booking?", "Free up to 48 hours before the session. Inside 48 hours the deposit is retained by the studio."],
  ["When do I get paid?", "Within seven days of a completed session or a delivered order, to the account on your profile."],
  ["Why was my listing removed?", "Usually a prohibited item or someone else's photos. You'll always get an email explaining which."],
  ["How does verification work?", "Upload one government ID and one proof of work. Most are reviewed within two working days."],
];

writeScreen({
  flow: "user_settings", screen: "faq", title: "Settings — FAQ",
  body: `
  <div class="ml-app">
    ${bar("FAQ", `${P}help_center/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-field">
        <label for="f-q">Search questions</label>
        <input id="f-q" type="search" value="" placeholder="deposit" />
      </div>

      <div data-state="searching" style="margin-top:var(--space-3)">
        <p class="ml-type-1" style="color:var(--color-muted);margin:0 0 var(--space-2)">
          2 results for "deposit"
        </p>
        ${FAQS.slice(0, 2).map(([q, a]) => `<div class="ml-section"><h2>${q}</h2></div>
        <p class="ml-type-3" style="color:var(--color-muted);margin:0 0 var(--space-3)">${a}</p>`).join("\n        ")}
      </div>

      <div data-state="default" style="margin-top:var(--space-3)">
        ${FAQS.map(([q, a]) => `<div class="ml-section"><h2>${q}</h2></div>
        <p class="ml-type-3" style="color:var(--color-muted);margin:0 0 var(--space-3)">${a}</p>`).join("\n        ")}
      </div>
    </main>
  </div>`,
});

writeScreen({
  flow: "user_settings", screen: "legal_pages", title: "Settings — Legal",
  body: `
  <div class="ml-app">
    ${bar("Terms & privacy", `${P}help_center/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <!-- The documents are sections OF this page, not four rows linking back
           to the page you are already on. -->
      ${[["Terms of service", "Updated 14 June 2026",
          "Using MusicLinkd means agreeing to these terms. Bookings and marketplace orders are contracts between you and the other party; we hold deposits and release them under the rules below."],
         ["Privacy policy", "Updated 14 June 2026",
          "We don't sell your personal data, and we don't use your uploads to train anything. Your location is shown as a city, never a precise address."],
         ["Community guidelines", "",
          "Represent your own work. Don't post someone else's recordings, photographs or credits as your own. Harassment ends an account."],
         ["Marketplace rules", "Prohibited items, offers, disputes",
          "List only what you own and can ship or hand over. Offers expire after 48 hours. Disputes are held against the deposit until both sides are heard."]]
        .map(([t, m, body]) => `<div class="ml-section"><h2>${t}</h2>${m ? `<span class="ml-section__link">${m}</span>` : ""}</div>
      <p class="ml-type-3" style="color:var(--color-muted);margin:0 0 var(--space-3)">${body}</p>`).join("\n      ")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>In short</h2></div>
      <p class="ml-type-3" style="color:var(--color-muted);margin:0">
        You own the work you upload. We show it to the people you choose to show it to.
        We don't sell your personal data, and we don't use your uploads to train anything.
        Bookings and marketplace orders are contracts between you and the other party;
        we hold deposits and release them under the rules above.
      </p>
    </main>
  </div>`,
});

writeScreen({
  flow: "user_settings", screen: "app_version", title: "Settings — About",
  body: `
  <div class="ml-app">
    ${bar("About", `${P}help_center/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-empty" style="padding-block:var(--space-4)">
        <img class="ml-avatar ml-avatar--lg" src="${media("object", 0)}" alt="" loading="lazy" />
        <h2 class="ml-empty__title"><span class="ml-wordmark">MUSIC<em>LINKD</em></span></h2>
        <p class="ml-empty__body">Version 1.4.2 (build 2291)</p>
      </div>

      <div class="ml-summary">
        ${rowValue("Version", "1.4.2 (2291)")}
        ${rowValue("Released", "28 August 2026")}
        ${rowValue("Signed in as", "vedanshu@wugweb.com")}
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>More</h2></div>
      ${rowLink("Terms and privacy", `${P}legal_pages/index.html`)}
      ${rowLink("Send feedback", `${P}feedback/index.html`)}
    </main>
  </div>`,
});

/* ══ FLOW-21 brief, 2026-08-31 ═════════════════════════════════════════════
   58 numbered sections. Six become screens — each owns a route: username,
   change email/phone, signed-in devices, blocked accounts, data & privacy,
   and the deactivate/delete danger screen. The rest are states, sheets,
   dialogs, or handoffs to the flow that already owns them:

     SCREEN 19 NOTIFICATION SETTINGS -> handoff to FLOW 13
     SCREEN 21 MARKETPLACE SETTINGS  -> handoff to FLOW 07
     SCREEN 26 SAVED & FAVORITES     -> handoff to FLOW 24
     SCREEN 43 DEACTIVATE CONFIRM    -> a state of the danger screen
     SCREEN 51 UNSAVED CHANGES       -> a dialog
     SCREEN 52 SAVE SUCCESS          -> a state, deliberately quiet

   The brief's rule that a destructive action must never be one tap is the
   reason deactivate/delete is one screen with five states rather than four
   screens: consequences are shown BEFORE the confirmation, and you cannot
   reach the confirmation without passing through them. ═══════════════════ */

/* ── username ───────────────────────────────────────────────────────────── */
writeScreen({
  flow: "user_settings", screen: "username", title: "Settings — Username",
  body: `
  <div class="ml-app">
    ${bar("Username", `${P}account_settings/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${field("u-name", "Username", "text", "ishitarao")}
      <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-1) 0 0">
        Your profile will be at musiclinkd.com/ishitarao
      </p>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Rules</h2></div>
      <div class="ml-summary">
        <div class="ml-summary__row"><span>Length</span><span>3–30 characters</span></div>
        <div class="ml-summary__row"><span>Allowed</span><span>letters, numbers, full stops</span></div>
        <div class="ml-summary__row"><span>Changes</span><span>once every 30 days</span></div>
      </div>

      <div data-state="checking" style="margin-top:var(--space-3)">
        <div class="ml-banner ml-banner--info"><span class="ml-banner__body">Checking availability…</span></div>
      </div>
      <div data-state="taken" style="margin-top:var(--space-3)">
        <div class="ml-banner ml-banner--error"><span class="ml-banner__body">That username is taken. Try ishita.rao or ishitaraomusic.</span></div>
      </div>
      <div data-state="saved" style="margin-top:var(--space-3)">
        <div class="ml-banner ml-banner--success"><span class="ml-banner__body">Saved. Your old link redirects for 30 days.</span></div>
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="?state=saved">Save username</a>
    </footer>
  </div>`,
});

/* ── change_email_phone — email / phone are two states of one screen ────── */
writeScreen({
  flow: "user_settings", screen: "change_email_phone", title: "Settings — Change email or phone",
  body: `
  <div class="ml-app">
    ${bar("Email & phone", `${P}account_settings/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <nav class="ml-tabs ml-tabs--fill" role="tablist" aria-label="What to change">
        <a class="ml-tabs__tab" role="tab" data-chip="email" href="?state=email">Email</a>
        <a class="ml-tabs__tab" role="tab" data-chip="phone" href="?state=phone">Phone</a>
      </nav>

      <div data-state="email" style="margin-top:var(--space-3)">
        ${field("e-cur", "Current email", "email", "ishita@example.com")}
        ${field("e-new", "New email", "email", "", "you@example.com")}
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-1)">
          We'll send a link to the new address. Your email only changes once you follow it.
        </p>
      </div>

      <div data-state="phone" style="margin-top:var(--space-3)">
        ${field("p-cur", "Current phone", "tel", "+91 98••• ••432")}
        ${field("p-new", "New phone", "tel", "", "+91 …")}
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-1)">
          We'll send a six-digit code. Your number only changes once you enter it.
        </p>
      </div>

      <div class="ml-empty" data-state="verifying">
        <span class="ml-spinner" role="status" aria-label="Waiting for verification"></span>
        <p class="ml-empty__body" style="margin-top:var(--space-3)">Waiting for you to confirm. Nothing has changed yet.</p>
      </div>

      <div class="ml-empty" data-state="verified">
        <h2 class="ml-empty__title" style="color:var(--color-success)">Verified and updated</h2>
        <p class="ml-empty__body">You'll use the new details from your next sign-in.</p>
        <a class="ml-btn ml-btn--primary" href="${P}account_settings/index.html">Back to account</a>
      </div>

      <div data-state="error">
        <div class="ml-banner ml-banner--error"><span class="ml-banner__body">That address is already on another account.</span></div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=email">Try another</a>
      </div>
    </main>
    <footer class="ml-app__footer" data-state="email phone">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="?state=verifying">Continue</a>
    </footer>
  </div>`,
});

/* ── active_sessions ────────────────────────────────────────────────────── */
const DEVICES = [
  ["Pixel 8", "Android · Mumbai", "Active now", true],
  ["MacBook Pro", "Chrome · Mumbai", "2 hours ago", false],
  ["iPad", "Safari · Pune", "6 days ago", false],
];
writeScreen({
  flow: "user_settings", screen: "active_sessions", title: "Settings — Devices",
  body: `
  <div class="ml-app">
    ${bar("Signed-in devices", `${P}security_settings/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        ${DEVICES.map(([d, m, t, cur]) => `<div class="ml-check-row">
          <span class="ml-choice__body">
            <span class="ml-choice__title">${d}${cur ? " · this device" : ""}</span>
            <span class="ml-choice__meta">${m} · ${t}</span>
          </span>
          ${cur ? `<span class="ml-status ml-status--success">Current</span>`
                : `<button class="ml-btn ml-btn--secondary" type="button">Sign out</button>`}
        </div>`).join("\n        ")}
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Locations are approximate, from the network the device used.
        </p>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-2)" href="?state=current_only">Sign out all other devices</a>
      </div>

      <div data-state="current_only">
        <div class="ml-banner ml-banner--success"><span class="ml-banner__body">Signed out everywhere else. Only this device stays signed in.</span></div>
        <div class="ml-check-row" style="margin-top:var(--space-3)">
          <span class="ml-choice__body">
            <span class="ml-choice__title">Pixel 8 · this device</span>
            <span class="ml-choice__meta">Android · Mumbai · Active now</span>
          </span>
          <span class="ml-status ml-status--success">Current</span>
        </div>
      </div>
    </main>
  </div>`,
});

/* ── blocked_accounts ───────────────────────────────────────────────────── */
writeScreen({
  flow: "user_settings", screen: "blocked_accounts", title: "Settings — Blocked",
  body: `
  <div class="ml-app">
    ${bar("Blocked accounts", `${P}privacy_settings/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default">
        <p class="ml-type-3" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Blocked accounts cannot message you or see your profile. Blocking is immediate, and separate from reporting.
        </p>
        ${[["Anonymous account", "Blocked 12 Aug", 9], ["Loud Gear Rentals", "Business · blocked 2 Aug", 12]]
          .map(([n, m, i]) => `<div class="ml-person-row">
          <img src="${media("faces", i)}" alt="" loading="lazy" />
          <span class="meta"><b>${n}</b><p>${m}</p></span>
          <button class="ml-btn ml-btn--secondary" type="button">Unblock</button>
        </div>`).join("\n        ")}
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No blocked accounts</h2>
        <p class="ml-empty__body">You can block anyone from their profile, or from a conversation.</p>
      </div>
    </main>
  </div>`,
});

/* ── data_privacy — the export lifecycle as states ──────────────────────── */
writeScreen({
  flow: "user_settings", screen: "data_privacy", title: "Settings — Data & privacy",
  body: `
  <div class="ml-app">
    ${bar("Data & privacy", `${P}account_settings/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-section"><h2>Your data</h2></div>

      <div data-state="default">
        <p class="ml-type-3" style="color:var(--color-muted);margin:0 0 var(--space-2)">
          Your export may contain your profile, portfolio, messages, bookings and account information.
          It takes a little while to prepare.
        </p>
        <a class="ml-btn ml-btn--secondary ml-btn--block" href="?state=export_requested">Request data export</a>
      </div>

      <div data-state="export_requested">
        <div class="ml-banner ml-banner--info"><span class="ml-banner__body">Export requested. We'll email you when it's ready.</span></div>
      </div>
      <div data-state="export_processing">
        <div class="ml-banner ml-banner--info"><span class="ml-banner__body">Preparing your export. This usually takes a few hours.</span></div>
      </div>
      <div data-state="export_ready">
        <div class="ml-banner ml-banner--success"><span class="ml-banner__body">Your data export is ready.</span></div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=default">Download</a>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">The link expires in 7 days.</p>
      </div>
      <div data-state="export_expired">
        <div class="ml-banner ml-banner--warning"><span class="ml-banner__body">That export has expired. You can request a new one.</span></div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=export_requested">Request again</a>
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Personalisation</h2></div>
      <div data-state="default personalisation">
        <div role="radiogroup" aria-label="Personalisation">
          ${[["Personalised", "Discover, opportunity matches and market suggestions use what you do here.", true],
             ["Limited", "Only your roles, genres and city are used.", false],
             ["Off", "Nothing personal is used to order what you see.", false]]
            .map(([t, m, on]) => `<div class="ml-choice" role="radio" aria-checked="${on}" tabindex="${on ? 0 : -1}">
            <span class="ml-choice__body">
              <span class="ml-choice__title">${t}</span>
              <span class="ml-choice__meta">${m}</span>
            </span>
            <span class="ml-choice__mark"></span>
          </div>`).join("\n          ")}
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Turning personalisation off does not turn off search — you can still find anything, it is just ordered plainly.
        </p>
      </div>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>More</h2></div>
      ${rowLink("Privacy settings", `${P}privacy_settings/index.html`, "Who can see you and reach you")}
      ${rowLink("Terms and privacy", `${P}legal_pages/index.html`)}
    </main>
  </div>`,
});

/* ── delete_account — consequences BEFORE confirmation ──────────────────── */
writeScreen({
  flow: "user_settings", screen: "delete_account", title: "Settings — Deactivate or delete",
  body: `
  <div class="ml-app">
    ${bar("Deactivate or delete", `${P}account_settings/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="choose">
        <div class="ml-section"><h2>Take a break</h2></div>
        <a class="ml-action-row" href="?state=deactivate_confirm">
          <span class="ml-choice__body">
            <span class="ml-choice__title">Deactivate</span>
            <span class="ml-choice__meta">Temporarily hides your profile. Sign in again to bring it back.</span>
          </span>
          ${icon("arrow-forward", { size: 18 })}
        </a>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Leave for good</h2></div>
        <a class="ml-action-row" href="?state=delete_consequences">
          <span class="ml-choice__body">
            <span class="ml-choice__title" style="color:var(--color-error)">Delete account</span>
            <span class="ml-choice__meta">Permanent. Read what happens before you decide.</span>
          </span>
          ${icon("arrow-forward", { size: 18 })}
        </a>
      </div>

      <div data-state="deactivate_confirm">
        <div class="ml-section"><h2>Deactivate your account?</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Your profile</span><span>Hidden</span></div>
          <div class="ml-summary__row"><span>Your listings</span><span>Paused</span></div>
          <div class="ml-summary__row"><span>Confirmed bookings</span><span>Still stand</span></div>
          <div class="ml-summary__row"><span>Messages</span><span>Kept</span></div>
          <div class="ml-summary__row"><span>Coming back</span><span>Just sign in</span></div>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=processing">Deactivate</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=choose">Cancel</a>
      </div>

      <!-- Consequences first. You cannot reach the confirmation without them. -->
      <div data-state="delete_consequences">
        <div class="ml-banner ml-banner--error">
          <span class="ml-banner__body">Deleting is permanent. This cannot be undone.</span>
        </div>
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>What happens</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Profile and portfolio</span><span>Deleted</span></div>
          <div class="ml-summary__row"><span>Listings and offers</span><span>Removed</span></div>
          <div class="ml-summary__row"><span>Upcoming bookings</span><span>Cancelled</span></div>
          <div class="ml-summary__row"><span>Reviews you wrote</span><span>Kept, shown as removed</span></div>
          <div class="ml-summary__row"><span>Businesses you own</span><span>Must be transferred first</span></div>
          <div class="ml-summary__row"><span>Invoices</span><span>Kept, as the law requires</span></div>
        </div>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-3)" href="${P}data_privacy/index.html">Download my data first</a>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-2)" href="?state=delete_confirm">Continue</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=choose">Cancel</a>
      </div>

      <div data-state="delete_confirm">
        <div class="ml-section"><h2>Confirm</h2></div>
        ${field("d-phrase", "Type DELETE to confirm", "text", "", "DELETE")}
        ${field("d-pass", "Your password", "password", "")}
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=processing">Delete account</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=choose">Cancel</a>
      </div>

      <div class="ml-empty" data-state="processing">
        <h2 class="ml-empty__title">Request received</h2>
        <p class="ml-empty__body">
          Your account is being closed. It can take up to 30 days to remove everything,
          and you can cancel by signing in before then.
        </p>
        <a class="ml-btn ml-btn--primary" href="${P}help_center/index.html">Contact support</a>
      </div>
    </main>
  </div>`,
});

finish("user_settings: ");
