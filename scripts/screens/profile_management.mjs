#!/usr/bin/env node
/**
 * Flow 10 — Profile Management. Ten screens, 17 declared states, all ten still
 * the raw "Not designed yet" scaffold.
 *
 * This flow contains the single clearest recycling case in the app. Four
 * screens — talent_profile_editor, band_profile_editor,
 * business_profile_editor, venue_profile_editor — each declare exactly one
 * state, and that state is `visible_if_talent`, `visible_if_band`,
 * `visible_if_business`, `visible_if_venue`.
 *
 * A state named "visible_if_X" is not a state of a screen. It is a note saying
 * "this screen is the X variant of one editor". Four screens that differ only
 * in which fields apply are one editor, so they are generated from one
 * composer with a shared spine (name, tagline, location, links) and a
 * type-specific block. Same for public_profile_preview, whose four
 * *_template states are four renderings of one preview.
 *
 * No new CSS: .ml-field, .ml-choice, .ml-upload-grid, .ml-progress,
 * .ml-action-row, .ml-chip and .ml-summary already exist.
 *
 * Media rule, per the brand owner: images upload (avatar, cover), audio and
 * video are links only. external_links_editor is where the links live, and no
 * audio/video dropzone is drawn anywhere in this flow.
 */
import { writeScreen, backArrow, icon, assertIcons, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/profile_management/";
assertIcons(["arrow-forward", "check", "add"]);

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-2" style="margin:0">${title}</h1>
    </header>`;

const field = (id, label, type, value, ph) => `<div class="ml-field">
        <label for="${id}">${label}</label>
        ${type === "textarea"
          ? `<textarea id="${id}" rows="3"${ph ? ` placeholder="${ph}"` : ""}>${value || ""}</textarea>`
          : type === "select"
            ? `<select id="${id}">${value.map((o) => `<option>${o}</option>`).join("")}</select>`
            : `<input id="${id}" type="${type}" value="${value || ""}"${ph ? ` placeholder="${ph}"` : ""} />`}
      </div>`;

const chips = (items) => `<div class="ml-chip-wrap">
        ${items.map(([t, on]) => `<button type="button" class="ml-chip" aria-pressed="${on ? "true" : "false"}">${t}</button>`).join("\n        ")}
      </div>`;

const rowLink = (label, href, meta) => `<a class="ml-action-row" href="${href}">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>`;

/* ── ONE editor, four profile types ─────────────────────────────────────── */
/* The spine is identical for every type; only the second section differs. */
const editor = ({ screen, kind, state, heading, nameLabel, taglinePlaceholder, blocks }) =>
  writeScreen({
    flow: "profile_management", screen, title: `Profile — ${heading}`,
    body: `
  <div class="ml-app">
    ${bar(heading, `${P}profile_overview/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <!-- The declared state is "${state}", which is a note that this is the
           ${kind} variant of one editor rather than a state the screen enters.
           Marked here so the contract is honoured and the intent is visible. -->
      <div data-state="${state}">
        <div class="ml-section"><h2>Images</h2></div>
        <div class="ml-upload-grid">
          <div class="ml-upload-grid__item">
            <img src="${media("faces", 2)}" alt="" loading="lazy" />
            <button class="ml-upload-grid__remove" type="button" aria-label="Remove avatar"></button>
          </div>
          <div class="ml-upload-grid__item">
            <img src="${media("stage", 3)}" alt="" loading="lazy" />
            <button class="ml-upload-grid__remove" type="button" aria-label="Remove cover"></button>
          </div>
          <button class="ml-upload-grid__add" type="button" aria-label="Add an image"></button>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-1)">
          Avatar and cover only. Audio and video are added as links.
        </p>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Basics</h2></div>
        ${field("p-name", nameLabel, "text", "")}
        ${field("p-tag", "Tagline", "text", "", taglinePlaceholder)}
        ${field("p-loc", "City", "text", "Mumbai")}
        ${field("p-bio", "About", "textarea", "", "A few lines in your own voice.")}

        ${blocks}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Links</h2></div>
        ${rowLink("External links", `${P}external_links_editor/index.html`, "Spotify, YouTube, SoundCloud, Instagram")}
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}profile_overview/index.html">Save profile</a>
    </footer>
  </div>`,
  });

editor({
  screen: "talent_profile_editor", kind: "talent", state: "visible_if_talent",
  heading: "Edit talent profile", nameLabel: "Your name",
  taglinePlaceholder: "Session vocalist · Hindi & English pop",
  blocks: `<div class="ml-section" style="margin-top:var(--space-3)"><h2>Roles</h2></div>
        ${chips([["Vocalist", 1], ["Producer", 0], ["Composer", 0], ["Engineer", 0], ["DJ", 0], ["Instrumentalist", 0]])}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Genres</h2></div>
        ${chips([["Pop", 1], ["Indie", 1], ["Hip-hop", 0], ["Classical", 0], ["Electronic", 0], ["Jazz", 0]])}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Languages</h2></div>
        ${chips([["Hindi", 1], ["English", 1], ["Marathi", 0], ["Tamil", 0], ["Punjabi", 0]])}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Availability</h2></div>
        ${field("p-avail", "Status", "select", ["Open to work", "Selectively available", "Not available"])}
        ${field("p-rate", "Day rate (₹)", "text", "12000")}`,
});

editor({
  screen: "band_profile_editor", kind: "band", state: "visible_if_band",
  heading: "Edit band profile", nameLabel: "Band name",
  taglinePlaceholder: "Four-piece indie rock from Bandra",
  blocks: `<div class="ml-section" style="margin-top:var(--space-3)"><h2>Line-up</h2></div>
        ${["Ishita Rao — vocals", "Kabir Sheth — guitar", "Meera Nair — bass", "Rohan D'Souza — drums"]
          .map((m) => `<div class="ml-check-row">
          <span class="ml-choice__body"><span class="ml-choice__title">${m}</span></span>
          <button class="ml-icon-btn ml-icon-btn--muted" type="button" aria-label="Remove ${m}">${icon("check", { size: 18 })}</button>
        </div>`).join("\n        ")}
        <button class="ml-btn ml-btn--secondary ml-btn--block" type="button" style="margin-top:var(--space-2)">Add a member</button>

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Genres</h2></div>
        ${chips([["Indie", 1], ["Rock", 1], ["Alt", 0], ["Fusion", 0]])}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Live documents</h2></div>
        ${field("p-rider", "Tech rider (link)", "url", "", "https://…")}
        ${field("p-plot", "Stage plot (link)", "url", "", "https://…")}`,
});

editor({
  screen: "business_profile_editor", kind: "business", state: "visible_if_business",
  heading: "Edit business profile", nameLabel: "Business name",
  taglinePlaceholder: "Recording studio & label services",
  blocks: `<div class="ml-section" style="margin-top:var(--space-3)"><h2>Type</h2></div>
        ${chips([["Studio", 1], ["Label", 0], ["School", 0], ["Dealer", 0], ["Production house", 0], ["Booking agency", 0]])}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Services</h2></div>
        ${chips([["Tracking", 1], ["Mixing", 1], ["Mastering", 0], ["Rehearsal", 0], ["Rental", 0]])}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Details</h2></div>
        ${field("p-gst", "GST number", "text", "", "27ABCDE1234F1Z5")}
        ${field("p-team", "Team size", "select", ["1–5", "6–20", "21–50", "50+"])}`,
});

editor({
  screen: "venue_profile_editor", kind: "venue", state: "visible_if_venue",
  heading: "Edit venue profile", nameLabel: "Venue name",
  taglinePlaceholder: "220-capacity live room in Lower Parel",
  blocks: `<div class="ml-section" style="margin-top:var(--space-3)"><h2>The room</h2></div>
        ${field("p-cap", "Capacity", "text", "220")}
        ${field("p-stage", "Stage size", "text", "6m × 4m")}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Provided</h2></div>
        ${chips([["PA system", 1], ["Backline", 1], ["Lighting", 1], ["Sound engineer", 0], ["Green room", 1]])}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Bookings</h2></div>
        ${field("p-nights", "Typical nights", "select", ["Thu–Sat", "Every night", "Weekends only"])}
        ${field("p-fee", "Hire fee (₹)", "text", "25000")}`,
});

/* ── profile_type_selection ─────────────────────────────────────────────── */
const TYPES = [
  ["Talent", "You perform, write, produce or engineer", "talent_profile_editor", true],
  ["Band", "A group with a shared line-up and shows", "band_profile_editor", false],
  ["Business", "A studio, label, school or dealer", "business_profile_editor", false],
  ["Venue", "A room that hosts live music", "venue_profile_editor", false],
];

writeScreen({
  flow: "profile_management", screen: "profile_type_selection", title: "Profile — Type",
  body: `
  <div class="ml-app">
    ${bar("Profile type", `${P}profile_overview/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
        This decides which fields your profile asks for. You can change it later without losing anything.
      </p>
      <div role="radiogroup" aria-label="Profile type">
        ${TYPES.map(([t, m, , on]) => `<div class="ml-choice" role="radio" aria-checked="${on}" tabindex="${on ? 0 : -1}">
          <span class="ml-choice__body">
            <span class="ml-choice__title">${t}</span>
            <span class="ml-choice__meta">${m}</span>
          </span>
          <span class="ml-choice__mark"></span>
        </div>`).join("\n        ")}
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}talent_profile_editor/index.html">Continue</a>
    </footer>
  </div>`,
});

/* ── profile_overview ───────────────────────────────────────────────────── */
writeScreen({
  flow: "profile_management", screen: "profile_overview", title: "Profile — Overview",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Your profile</h1>
      <span class="ml-appbar__actions">
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}public_profile_preview/index.html" aria-label="Preview">${icon("arrow-forward", { size: 20 })}</a>
      </span>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-person-row">
        <img src="${media("faces", 2)}" alt="" loading="lazy" />
        <span class="meta"><b>Ishita Rao</b><p>Session vocalist · Mumbai</p></span>
      </div>

      <a class="ml-action-row" href="${P}profile_completion/index.html" style="margin-top:var(--space-2)">
        <span class="ml-choice__body">
          <span class="ml-choice__title">Profile 80% complete</span>
          <span class="ml-choice__meta">Two things left before you appear in Discover</span>
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Edit</h2></div>
      ${rowLink("Identity and contact", `${P}edit_identity/index.html`, "Name, email, phone")}
      ${rowLink("Talent profile", `${P}talent_profile_editor/index.html`, "Roles, genres, languages, rate")}
      ${rowLink("External links", `${P}external_links_editor/index.html`, "Spotify, YouTube, SoundCloud")}
      ${rowLink("Profile type", `${P}profile_type_selection/index.html`, "Currently: Talent")}

      <div class="ml-section" style="margin-top:var(--space-3)"><h2>Other profiles</h2></div>
      ${rowLink("Band profile", `${P}band_profile_editor/index.html`)}
      ${rowLink("Business profile", `${P}business_profile_editor/index.html`)}
      ${rowLink("Venue profile", `${P}venue_profile_editor/index.html`)}
    </main>
  </div>`,
});

/* ── profile_completion — incomplete / complete ─────────────────────────── */
const STEPS = [
  ["Add a profile photo", true], ["Pick your roles", true], ["Add three genres", true],
  ["Add a portfolio piece", false], ["Verify your identity", false],
];
writeScreen({
  flow: "profile_management", screen: "profile_completion", title: "Profile — Completion",
  body: `
  <div class="ml-app">
    ${bar("Profile completion", `${P}profile_overview/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="incomplete">
        <div class="ml-progress">
          <span class="ml-progress__title">80% complete</span>
          <span class="ml-progress__meta">${STEPS.filter((s) => s[1]).length} of ${STEPS.length}</span>
          <span class="ml-progress__track"><span class="ml-progress__fill" style="width:80%"></span></span>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-2) 0 var(--space-3)">
          A complete profile appears in Discover and can be booked directly.
        </p>
        ${STEPS.map(([t, done]) => `<div class="ml-check-row">
          <span class="ml-check-ctl" role="checkbox" aria-checked="${done}" aria-disabled="true"></span>
          <span class="ml-choice__body"><span class="ml-choice__title">${t}</span></span>
        </div>`).join("\n        ")}
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="/preview/flows/portfolio/portfolio_dashboard/index.html">Add a portfolio piece</a>
      </div>

      <div class="ml-empty" data-state="complete">
        <h2 class="ml-empty__title" style="color:var(--color-success)">Profile complete</h2>
        <p class="ml-empty__body">You're visible in Discover and open to booking requests.</p>
        <a class="ml-btn ml-btn--primary" href="${P}public_profile_preview/index.html">See how it looks</a>
      </div>
    </main>
  </div>`,
});

/* ── edit_identity — editing / saving / saved / error ───────────────────── */
writeScreen({
  flow: "profile_management", screen: "edit_identity", title: "Profile — Identity",
  body: `
  <div class="ml-app">
    ${bar("Identity", `${P}profile_overview/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div data-state="editing">
        ${field("i-name", "Full name", "text", "Ishita Rao")}
        ${field("i-email", "Email", "email", "ishita@example.com")}
        ${field("i-phone", "Phone", "tel", "+91 98••• ••432")}
        ${field("i-pronouns", "Pronouns", "select", ["Prefer not to say", "she/her", "he/him", "they/them"])}
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Your email and phone are never shown publicly.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=saving">Save changes</a>
      </div>

      <div class="ml-empty" data-state="saving">
        <span class="ml-spinner" role="status" aria-label="Saving"></span>
        <p class="ml-empty__body" style="margin-top:var(--space-3)">Saving…</p>
      </div>

      <div class="ml-empty" data-state="saved">
        <h2 class="ml-empty__title" style="color:var(--color-success)">Saved</h2>
        <p class="ml-empty__body">Your details are up to date.</p>
        <a class="ml-btn ml-btn--primary" href="${P}profile_overview/index.html">Back to profile</a>
      </div>

      <div data-state="error">
        <div class="ml-banner ml-banner--error">
          <span class="ml-banner__body">Couldn't save — that email is already on another account.</span>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=editing">Back to the form</a>
      </div>
    </main>
  </div>`,
});

/* ── external_links_editor ──────────────────────────────────────────────── */
const LINKS = [
  ["Spotify", "https://open.spotify.com/artist/…"],
  ["Apple Music", ""],
  ["YouTube", "https://youtube.com/@ishitarao"],
  ["SoundCloud", ""],
  ["Instagram", "https://instagram.com/ishitarao"],
];
writeScreen({
  flow: "profile_management", screen: "external_links_editor", title: "Profile — Links",
  body: `
  <div class="ml-app">
    ${bar("External links", `${P}profile_overview/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
        Audio and video live on the platforms you already use — paste the link and it plays inline on your profile.
      </p>
      ${LINKS.map(([name, val], i) => field(`lk-${i}`, name, "url", val, "https://…")).join("\n      ")}
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}profile_overview/index.html">Save links</a>
    </footer>
  </div>`,
});

/* ── public_profile_preview — one preview, four templates ───────────────── */
const TEMPLATES = [
  ["talent_template", "Ishita Rao", "Session vocalist · Mumbai", "Open to work · ₹12,000/day", media("faces", 2)],
  ["band_template", "The Kolaba Sessions", "Four-piece indie rock · Mumbai", "Next show 18 Aug · Antisocial", media("stage", 3)],
  ["business_template", "Yash Raj Sound Studios", "Recording studio · Andheri West", "Tracking, mixing · from ₹1,800/hr", media("gear", 7)],
  ["venue_template", "The Quarter", "Live room · Lower Parel", "220 capacity · Thu–Sat", media("stage", 6)],
];

writeScreen({
  flow: "profile_management", screen: "public_profile_preview", title: "Profile — Preview",
  body: `
  <div class="ml-app">
    ${bar("Preview", `${P}profile_overview/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:0 0 var(--space-4)">
      <!-- Four templates, one preview. The template is the state, so switching
           type shows the real difference rather than opening another screen. -->
      ${TEMPLATES.map(([st, name, meta, trail, img]) => `<div data-state="${st}">
        <img src="${img}" alt="" loading="lazy"
             style="width:100%;aspect-ratio:var(--media-aspect-card);object-fit:cover" />
        <div style="padding:var(--space-3) var(--space-4)">
          <h2 class="ml-type-3" style="margin:0">${name}</h2>
          <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-1) 0 0">${meta}</p>
          <span class="ml-status ml-status--accent" style="margin-top:var(--space-2);display:inline-flex">${trail}</span>
          <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
            This is what someone sees when they open your profile from Discover or a shared link.
          </p>
        </div>
      </div>`).join("\n      ")}
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}profile_overview/index.html">Back to editing</a>
    </footer>
  </div>`,
});

finish("profile_management: ");
