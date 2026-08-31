#!/usr/bin/env node
/**
 * Flow 23 — QR / Digital Identity & Sharing, against the supplied brief.
 *
 * 35 numbered sections. Three become screens, because each owns a route and,
 * in the scanner's case, a camera:
 *
 *   qr_scanner              the camera, and every scan outcome as a state
 *   digital_business_card   a distinct artefact from the QR itself
 *   qr_settings             enable/destination/analytics
 *
 * The other 32 are states of those three or of qr_share_flow. In particular
 * the brief's SCREEN 08–12 (result: profile / business / service / marketplace
 * / portfolio) are five states of the scanner, not five screens — the scan has
 * already happened, only the preview differs.
 *
 * Two rules from the brief are load-bearing and are honoured in the copy:
 *
 *   · "Do not imply MusicLinkd can override OS permissions." The camera-denied
 *     state tells you to change it in device settings and offers the gallery
 *     fallback; it does not offer a button that pretends to grant permission.
 *   · "Do not require users to reprint QR codes for ordinary profile edits."
 *     qr_settings therefore has no regenerate action for a profile change —
 *     the code resolves through the canonical route.
 *
 * Scannability first: the QR is drawn on plain white with a full quiet zone
 * and nothing overlaid, which is why the "branded" style option changes the
 * card around the code rather than the code itself.
 *
 * No new CSS: .ml-qr, .ml-dropzone, .ml-empty, .ml-summary, .ml-action-row,
 * .ml-status and .ml-sheet already exist.
 */
import { writeScreen, backArrow, icon, assertIcons, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/qr_share/";
assertIcons(["arrow-forward", "search", "add", "check", "cancel", "send", "heart"]);

const bar = (title, backTo, actions = "") => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-2" style="margin:0;flex:1">${title}</h1>
      ${actions}
    </header>`;

const rowLink = (label, href, meta) => `<a class="ml-action-row" href="${href}">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>`;

/* The identity block that sits above or below the code. Deliberately separate
   from the code itself: the brief says identity must not be communicated by
   the QR alone, and a screen reader gets the name, not a picture of a code. */
const identity = (name, role, verified) => `<h2 class="ml-type-3" style="margin:var(--space-2) 0 0">${name}</h2>
        <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-1) 0 0">${role}</p>
        ${verified ? `<span class="ml-status ml-status--accent" style="margin-top:var(--space-2);display:inline-flex">Verified</span>` : ""}`;

/* ── qr_share_flow — the identity card, its share options, and print previews */
writeScreen({
  flow: "qr_share", screen: "qr_share_flow", title: "Your MusicLinkd ID",
  body: `
  <div class="ml-app">
    ${bar("MusicLinkd ID", "/preview/flows/profile_management/profile_overview/index.html",
      `<a class="ml-icon-btn ml-icon-btn--bare" href="${P}qr_settings/index.html" aria-label="QR settings">${icon("check", { size: 20 })}</a>`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <!-- Personal card. The code sits on plain white with its quiet zone
           intact and nothing drawn inside it. -->
      <div class="ml-empty" data-state="home preview share_card print_card print_poster" style="padding-block:var(--space-3)">
        <div class="ml-qr" aria-label="QR code linking to Ishita Rao's MusicLinkd profile"></div>
        ${identity("Ishita Rao", "Session vocalist · Mumbai", true)}
        <p class="ml-empty__body">musiclinkd.com/ishitarao</p>
      </div>

      <!-- Business card is the SAME card with a different subject. -->
      <div class="ml-empty" data-state="business" style="padding-block:var(--space-3)">
        <div class="ml-qr" aria-label="QR code linking to Yash Raj Sound Studios on MusicLinkd"></div>
        ${identity("Yash Raj Sound Studios", "Recording studio · Andheri West", true)}
        <p class="ml-empty__body">musiclinkd.com/yashrajsound</p>
      </div>

      <div data-state="home business">
        <a class="ml-btn ml-btn--primary ml-btn--block" href="?state=options">Share or export</a>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-2)" href="${P}qr_scanner/index.html">Scan a code</a>
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Also available as</h2></div>
        ${rowLink("Business QR", "?state=business", "For Yash Raj Sound Studios")}
        ${rowLink("Digital business card", `${P}digital_business_card/index.html`, "Name, role, and the contact details you've made public")}
      </div>

      <div data-state="options">
        <div class="ml-section"><h2>Share</h2></div>
        ${rowLink("Copy link", "?state=home", "musiclinkd.com/ishitarao")}
        ${rowLink("Send in a message", "/preview/flows/communication/chat_thread_list/index.html")}
        ${rowLink("Share elsewhere", "?state=home", "Uses your device's share sheet")}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Export</h2></div>
        ${rowLink("Share card", "?state=share_card", "Photo, name, role and code — for a post or a bio")}
        ${rowLink("Business card", "?state=print_card", "85 × 55 mm, print-ready")}
        ${rowLink("Poster", "?state=print_poster", "A3, for a studio wall or a green room")}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Style</h2></div>
        <div class="ml-chip-wrap">
          ${[["Light", 1], ["Dark", 0], ["MusicLinkd", 0], ["Minimal", 0]]
            .map(([t, on]) => `<button type="button" class="ml-chip" aria-pressed="${on ? "true" : "false"}">${t}</button>`).join("\n          ")}
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Style changes the card around the code. The code itself stays plain black on white so it always scans.
        </p>
      </div>

      <div data-state="preview share_card print_card print_poster">
        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>Includes</span><span>Photo, name, role, code</span></div>
          <div class="ml-summary__row" data-state="print_card"><span>Size</span><span>85 × 55 mm</span></div>
          <div class="ml-summary__row" data-state="print_poster"><span>Size</span><span>A3, 297 × 420 mm</span></div>
          <div class="ml-summary__row" data-state="share_card"><span>Size</span><span>1080 × 1350 px</span></div>
          <div class="ml-summary__row"><span>Contact details</span><span>Only what's public</span></div>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=home">Download</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=options">Back to options</a>
      </div>

      <div data-state="share_error">
        <div class="ml-banner ml-banner--error"><span class="ml-banner__body">We couldn't create the share card.</span></div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=options">Retry</a>
      </div>
    </main>
  </div>`,
});

/* ── qr_scanner — one camera, every outcome as a state ──────────────────── */
const RESULTS = [
  ["resolved_profile", "Ishita Rao", "Session vocalist · Mumbai", media("faces", 2),
   "View profile", "/preview/flows/public_profile/public_profile_view/index.html", "Message", "/preview/flows/communication/chat_conversation/index.html"],
  ["resolved_business", "Yash Raj Sound Studios", "Recording studio · Andheri West", media("stage", 1),
   "View business", "/preview/flows/business_profiles/business_overview/index.html", "Book", "/preview/flows/booking/availability_calendar/index.html"],
  ["resolved_service", "Full Track Mix & Master", "Blue Note Recording Co. · ₹6,000", media("gear", 7),
   "View service", "/preview/flows/public_profile/talent_services_view/index.html", "Enquire", "/preview/flows/communication/chat_conversation/index.html"],
  ["resolved_listing", "Shure SM7B Microphone", "Gear Sale · ₹28,500 · Like new", media("gear", 0),
   "View listing", "/preview/flows/marketplace/marketplace_item_detail/index.html", "Message seller", "/preview/flows/communication/chat_conversation/index.html"],
  ["resolved_portfolio", "Neon Monsoon", "Single · 2026 · Ishita Rao", media("stage", 3),
   "View work", "/preview/flows/portfolio/profile_project_view/index.html", "Save", "/preview/flows/saved_favorites/saved_favorites_hub/index.html"],
];

/* Failure copy that neither leaks nor overclaims — both are brief rules. */
const FAILURES = [
  ["invalid", "That isn't a MusicLinkd code", "It may be a code for something else entirely. Point the camera at a MusicLinkd QR.", "Scan again", "?state=scanning"],
  ["expired", "This link is no longer available", "The page it pointed to has moved on.", "Go to MusicLinkd", "/preview/flows/discover/discover_feed/index.html"],
  ["private", "This content isn't public", "Whoever shared it has restricted who can see it.", "Scan again", "?state=scanning"],
  ["deleted", "This profile is no longer available", "It may have been deleted or closed.", "Discover on MusicLinkd", "/preview/flows/discover/discover_feed/index.html"],
  ["deactivated", "This code is no longer active", "Its owner has turned it off.", "Scan again", "?state=scanning"],
  ["offline", "You're offline", "Connect to the internet to open this MusicLinkd profile. The code was read, but we can't look it up yet.", "Retry", "?state=scanning"],
];

writeScreen({
  flow: "qr_share", screen: "qr_scanner", title: "Scan a MusicLinkd code", scroll: false,
  body: `
  <div class="ml-app">
    ${bar("Scan", `${P}qr_share_flow/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <!-- The viewfinder. A dark plate with a cut-out frame stands in for the
           camera; the instruction is text, not an icon, so it is readable. -->
      <div class="ml-inset ml-inset--dark" data-state="scanning" style="aspect-ratio:1;display:flex;align-items:center;justify-content:center;position:relative">
        <span style="width:60%;aspect-ratio:1;border:2px solid var(--color-white);border-radius:var(--radius-2)"></span>
      </div>
      <p class="ml-type-2" data-state="scanning" style="color:var(--color-muted);text-align:center;margin-top:var(--space-3)">
        Point your camera at a MusicLinkd QR code.
      </p>
      <div data-state="scanning" style="display:flex;gap:var(--space-2);margin-top:var(--space-3)">
        <a class="ml-btn ml-btn--secondary" style="flex:1" href="?state=image_scan">Choose from gallery</a>
        <button class="ml-btn ml-btn--secondary" type="button" style="flex:1">Flash</button>
      </div>

      ${RESULTS.map(([st, name, meta, img, cta, href, alt, altHref]) => `<div data-state="${st}">
        <div class="ml-result" style="border:0;padding:0">
          <img class="ml-result__thumb" src="${img}" alt="" loading="lazy" />
          <span class="ml-result__body">
            <span class="ml-result__name">${name}</span>
            <span class="ml-result__meta">${meta}</span>
          </span>
        </div>
        <div style="display:flex;gap:var(--space-2);margin-top:var(--space-3)">
          <a class="ml-btn ml-btn--secondary" style="flex:1" href="${altHref}">${alt}</a>
          <a class="ml-btn ml-btn--primary" style="flex:1" href="${href}">${cta}</a>
        </div>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=scanning">Scan another</a>
      </div>`).join("\n      ")}

      ${FAILURES.map(([st, title, body, cta, href]) => `<div class="ml-empty" data-state="${st}">
        <h2 class="ml-empty__title">${title}</h2>
        <p class="ml-empty__body">${body}</p>
        <a class="ml-btn ml-btn--primary" href="${href}">${cta}</a>
      </div>`).join("\n      ")}

      <div class="ml-empty" data-state="no_camera">
        <h2 class="ml-empty__title">Camera access is needed to scan</h2>
        <p class="ml-empty__body">We only use the camera while this screen is open, and nothing is recorded.</p>
        <a class="ml-btn ml-btn--primary" href="?state=scanning">Allow camera</a>
        <a class="ml-btn ml-btn--secondary" style="margin-top:var(--space-2)" href="?state=image_scan">Use a photo instead</a>
      </div>

      <!-- Denied: the app cannot grant this, and the copy must not pretend it
           can. It points at device settings and offers the real fallback. -->
      <div class="ml-empty" data-state="camera_denied">
        <h2 class="ml-empty__title">Camera access is turned off</h2>
        <p class="ml-empty__body">
          You can turn it back on in your device settings, under Apps → MusicLinkd → Permissions.
          We can't change it from here.
        </p>
        <a class="ml-btn ml-btn--primary" href="?state=image_scan">Use a photo instead</a>
        <a class="ml-btn ml-btn--ghost" style="margin-top:var(--space-2)" href="${P}qr_share_flow/index.html">Cancel</a>
      </div>

      <div data-state="image_scan">
        <div class="ml-dropzone">
          <span class="ml-dropzone__hint">Choose a photo containing a QR code</span>
          <span class="ml-dropzone__formats">JPG or PNG</span>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=resolved_profile">Scan the photo</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=scanning">Use the camera</a>
      </div>
    </main>
  </div>`,
});

/* ── digital_business_card ──────────────────────────────────────────────── */
writeScreen({
  flow: "qr_share", screen: "digital_business_card", title: "Digital business card",
  body: `
  <div class="ml-app">
    ${bar("Business card", `${P}qr_share_flow/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-inset ml-inset--raised" style="padding:var(--space-4);text-align:center">
        <img class="ml-avatar ml-avatar--lg" src="${media("faces", 2)}" alt="" loading="lazy" style="margin:0 auto" />
        ${identity("Ishita Rao", "Session vocalist · Mumbai", true)}
        <div class="ml-qr" style="margin:var(--space-3) auto 0" aria-label="QR code linking to Ishita Rao's MusicLinkd profile"></div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">musiclinkd.com/ishitarao</p>
      </div>

      <!-- What the card carries is decided by the owner's privacy settings.
           The "restricted" state is what the same card looks like when phone
           and email are private — the fields are absent, not blanked out. -->
      <div class="ml-summary" style="margin-top:var(--space-3)" data-state="default save_contact">
        <div class="ml-summary__row"><span>Name</span><span>Ishita Rao</span></div>
        <div class="ml-summary__row"><span>Role</span><span>Session vocalist</span></div>
        <div class="ml-summary__row"><span>Phone</span><span>+91 98••• ••432</span></div>
        <div class="ml-summary__row"><span>Email</span><span>ishita@example.com</span></div>
        <div class="ml-summary__row"><span>Website</span><span>ishitarao.in</span></div>
        <div class="ml-summary__row"><span>MusicLinkd</span><span>musiclinkd.com/ishitarao</span></div>
      </div>

      <div data-state="restricted">
        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>Name</span><span>Ishita Rao</span></div>
          <div class="ml-summary__row"><span>Role</span><span>Session vocalist</span></div>
          <div class="ml-summary__row"><span>MusicLinkd</span><span>musiclinkd.com/ishitarao</span></div>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          This person keeps their phone and email private. Message them through MusicLinkd instead.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-2)" href="/preview/flows/communication/chat_conversation/index.html">Send a message</a>
      </div>

      <div class="ml-empty" data-state="save_contact">
        <h2 class="ml-empty__title">Save to contacts</h2>
        <p class="ml-empty__body">Only the fields above will be saved. Nothing this person keeps private is included.</p>
        <a class="ml-btn ml-btn--primary" href="?state=default">Save contact</a>
      </div>
    </main>
    <footer class="ml-app__footer" data-state="default" style="display:flex;gap:var(--space-2)">
      <a class="ml-btn ml-btn--secondary" style="flex:1" href="?state=save_contact">Save contact</a>
      <a class="ml-btn ml-btn--primary" style="flex:1" href="${P}qr_share_flow/index.html?state=options">Share</a>
    </footer>
  </div>`,
});

/* ── qr_settings ────────────────────────────────────────────────────────── */
writeScreen({
  flow: "qr_share", screen: "qr_settings", title: "QR settings",
  body: `
  <div class="ml-app">
    ${bar("QR settings", `${P}qr_share_flow/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="default analytics">
        <div class="ml-check-row">
          <span class="ml-choice__body">
            <span class="ml-choice__title">My QR code is active</span>
            <span class="ml-choice__meta">Turn this off and existing codes stop resolving</span>
          </span>
          <button class="ml-switch" type="button" role="switch" aria-checked="true" aria-label="QR code active"></button>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Where it goes</h2></div>
        <div role="radiogroup" aria-label="QR destination">
          ${[["My profile", "musiclinkd.com/ishitarao", true],
             ["My services and rates", "Straight to what you charge", false],
             ["My portfolio", "Straight to your work", false]]
            .map(([t, m, on]) => `<div class="ml-choice" role="radio" aria-checked="${on}" tabindex="${on ? 0 : -1}">
            <span class="ml-choice__body">
              <span class="ml-choice__title">${t}</span>
              <span class="ml-choice__meta">${m}</span>
            </span>
            <span class="ml-choice__mark"></span>
          </div>`).join("\n          ")}
        </div>
        <!-- The brief: ordinary profile edits must never require a reprint. -->
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Editing your profile never changes the code. A printed card keeps working —
          the code points at your MusicLinkd address, not at a snapshot of your page.
        </p>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>More</h2></div>
        ${rowLink("Scan activity", "?state=analytics", "Who's opened your code")}
        ${rowLink("Privacy settings", "/preview/flows/user_settings/privacy_settings/index.html", "What the card is allowed to show")}
      </div>

      <div data-state="analytics">
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Scan activity</h2></div>
        <div class="ml-stat-grid">
          <div class="ml-kpi"><span class="ml-kpi__num">148</span>Scans<span class="ml-kpi__delta ml-kpi__delta--up">+22</span></div>
          <div class="ml-kpi"><span class="ml-kpi__num">96</span>People</div>
          <div class="ml-kpi"><span class="ml-kpi__num">31</span>Opened your profile</div>
          <div class="ml-kpi"><span class="ml-kpi__num">7</span>Messaged you</div>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Counts only. We don't record who scanned your code or where they were.
        </p>
      </div>

      <div class="ml-empty" data-state="disabled">
        <h2 class="ml-empty__title">Your QR code is off</h2>
        <p class="ml-empty__body">Anyone scanning a printed card will be told the code isn't active. Your profile is still reachable by its link.</p>
        <a class="ml-btn ml-btn--primary" href="?state=default">Turn it back on</a>
      </div>
    </main>
  </div>`,
});

finish("qr_share: ");
