#!/usr/bin/env node
/**
 * Flow 2 — User Onboarding (13 screens, registry numbers 8-20).
 *
 * The longest flow in the app, so every screen shows its position: a
 * .ml-steps bar in the nav bar. Before this, .onb-progress and .onb-step were
 * redefined inside 12 separate screens and .step-dots inside 7.
 *
 * Emoji are kept on role/instrument choices. They are content, not chrome —
 * the same call the approved mobile reference makes on its category tiles —
 * so the monochrome-chrome rule is not violated. styleguide.html's
 * emoji-vs-icon rule remains unenforced repo-wide; that is unchanged here and
 * still open.
 */
import { writeScreen, backArrow, steps, checkMark, tick, finish } from "../write-screen.mjs";

const P = "/preview/flows/onboarding/";
const T = 13;
const done = [];

const nav = (n, back) =>
  `<header class="ml-navbar">${backArrow(back)}<span class="ml-navbar__spacer"></span>${steps(n, T)}</header>`;

const head = (title, sub) => `<div>
        <h1 class="ml-type-3" style="margin:0">${title}</h1>
        <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-1) 0 0">${sub}</p>
      </div>`;

const cta = (href, label = "Continue", extra = "") => `<footer class="ml-app__footer">
      ${extra}<a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="${href}">${label}</a>
    </footer>`;

/** A single-select role/instrument row. */
const choice = (emoji, title, meta, selected = false) => `<button class="ml-choice" role="radio" aria-checked="${selected}">
          <span class="ml-emoji" aria-hidden="true">${emoji}</span>
          <span class="ml-choice__body">
            <span class="ml-choice__title">${title}</span>
            ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
          </span>
          <span class="ml-choice__mark">${tick}</span>
        </button>`;

const chip = (label, sel = false) =>
  `<button class="ml-chip" aria-pressed="${sel}">${label}</button>`;

/* 8 · step 1 — Primary role */
done.push(writeScreen({ flow: "onboarding", screen: "primary_role", body: `
  <div class="ml-app">
    ${nav(1, "/preview/flows/auth/registration_write/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("What's your primary role?", "This is how you'll show up on MusicLinkd. You can add a secondary role next.")}
      <div role="radiogroup" aria-label="Primary role">
        ${choice("🎤", "Singer / Vocalist", "Lead or backing vocals", true)}
        ${choice("🎙️", "Rapper / MC", "Hip-hop, rap, spoken word")}
        ${choice("🧑‍🎤", "Folk Vocalist", "Traditional &amp; folk performance")}
        ${choice("🎸", "Guitarist", "Acoustic, electric, bass")}
        ${choice("🎹", "Keyboardist / Pianist", "Keys, piano, synthesis")}
      </div>
      <p class="ml-type-1" style="color:var(--color-muted)">Your role is sourced from the platform's 18 person types.</p>
    </main>
    ${cta(P + "secondary_role/index.html")}
  </div>` }));

/* 9 · step 2 — Secondary role (optional) */
done.push(writeScreen({ flow: "onboarding", screen: "secondary_role", body: `
  <div class="ml-app">
    ${nav(2, P + "primary_role/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Add a secondary role?", "Optional — helps others find you across disciplines.")}
      <div class="ml-banner ml-banner--info" data-state="skipped">Skipped — you can add a secondary role later in Profile settings.</div>
      <div role="radiogroup" aria-label="Secondary role" data-state="default">
        ${choice("🎼", "Composer", "Writing original music", true)}
        ${choice("🪘", "Percussionist", "Drums, tabla, dhol")}
        ${choice("🎛️", "Producer / Engineer", "Recording, mixing, mastering")}
        ${choice("🎧", "DJ", "Live sets, events, clubs")}
      </div>
    </main>
    ${cta(P + "genres/index.html", "Continue",
      `<a class="ml-btn ml-btn--ghost ml-btn--block" href="${P}genres/index.html" style="margin-bottom:var(--space-2)">Skip for now</a>`)}
  </div>` }));

/* 10 · step 3 — Genres (max 3) */
done.push(writeScreen({ flow: "onboarding", screen: "genres", body: `
  <div class="ml-app">
    ${nav(3, P + "secondary_role/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("What genres do you play?", "Pick up to 3 that best describe your sound.")}
      <div class="ml-chip-wrap">
        ${chip("Bollywood / Filmi", true)}${chip("Indie Pop")}${chip("Hindustani Classical")}
        ${chip("Carnatic Classical")}${chip("Punjabi Folk / Bhangra")}${chip("Sufi Devotional", true)}
        ${chip("Acoustic / Unplugged")}${chip("Electronic / EDM")}
      </div>
      <div class="ml-banner ml-banner--info">2 of 3 selected</div>
    </main>
    ${cta(P + "languages/index.html")}
  </div>` }));

/* 11 · step 4 — Languages */
done.push(writeScreen({ flow: "onboarding", screen: "languages", body: `
  <div class="ml-app">
    ${nav(4, P + "genres/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Which languages do you work in?", "Select the languages you sing, write, or communicate in.")}
      <div class="ml-chip-wrap">
        ${chip("English", true)}${chip("Hindi (हिंदी)", true)}${chip("Punjabi (ਪੰਜਾਬੀ)")}
        ${chip("Tamil (தமிழ்)")}${chip("Telugu (తెలుగు)")}${chip("Bengali (বাংলা)")}
        ${chip("Marathi (मराठी)")}${chip("Kannada (ಕನ್ನಡ)")}
      </div>
      <p class="ml-type-1" style="color:var(--color-muted)">Select all that apply.</p>
    </main>
    ${cta(P + "instruments/index.html")}
  </div>` }));

/* 12 · step 5 — Instruments */
done.push(writeScreen({ flow: "onboarding", screen: "instruments", body: `
  <div class="ml-app">
    ${nav(5, P + "languages/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Which instruments do you play?", "Select all that apply. Skilled musicians can be discovered for more work.")}
      <div class="ml-banner ml-banner--info" data-state="skipped_for_non_musician">Skipped — instruments and proficiency won't appear on your profile.</div>
      <div data-state="default">
        ${choice("🎸", "Acoustic Guitar", "", true)}
        ${choice("⚡", "Electric Guitar", "")}
        ${choice("🎸", "Bass Guitar", "", true)}
        ${choice("🎹", "Keyboard / Synthesizer", "")}
        ${choice("🎹", "Piano", "")}
        ${choice("🥁", "Drums", "")}
      </div>
    </main>
    ${cta(P + "instrument_proficiency/index.html", "Continue",
      `<a class="ml-btn ml-btn--ghost ml-btn--block" href="${P}location/index.html" style="margin-bottom:var(--space-2)">Not a musician — skip instruments</a>`)}
  </div>` }));

/* 13 · step 6 — Proficiency */
done.push(writeScreen({ flow: "onboarding", screen: "instrument_proficiency", body: `
  <div class="ml-app">
    ${nav(6, P + "instruments/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Tell us your skill level", "For each instrument, roughly how many years and your experience level.")}

      <div class="ml-card ml-card--padded">
        <div class="ml-row" style="padding-top:0">
          <span class="ml-emoji" aria-hidden="true">🎸</span>
          <span class="ml-row__body"><span class="ml-row__title">Acoustic Guitar</span></span>
        </div>
        <div class="ml-segmented" role="tablist" style="margin-bottom:var(--space-3)">
          <button class="ml-segmented__btn" role="tab" aria-selected="false">Beginner</button>
          <button class="ml-segmented__btn" role="tab" aria-selected="true">Intermediate</button>
          <button class="ml-segmented__btn" role="tab" aria-selected="false">Pro</button>
        </div>
        <div style="display:flex;align-items:center;gap:var(--space-3)">
          <button class="ml-icon-btn" aria-label="Fewer years">−</button>
          <span class="ml-type-3" style="flex:1;text-align:center">6 yrs</span>
          <button class="ml-icon-btn" aria-label="More years">+</button>
        </div>
      </div>

      <div class="ml-card ml-card--padded">
        <div class="ml-row" style="padding-top:0">
          <span class="ml-emoji" aria-hidden="true">🎸</span>
          <span class="ml-row__body"><span class="ml-row__title">Bass Guitar</span></span>
        </div>
        <div class="ml-segmented" role="tablist" style="margin-bottom:var(--space-3)">
          <button class="ml-segmented__btn" role="tab" aria-selected="true">Beginner</button>
          <button class="ml-segmented__btn" role="tab" aria-selected="false">Intermediate</button>
          <button class="ml-segmented__btn" role="tab" aria-selected="false">Pro</button>
        </div>
        <div style="display:flex;align-items:center;gap:var(--space-3)">
          <button class="ml-icon-btn" aria-label="Fewer years">−</button>
          <span class="ml-type-3" style="flex:1;text-align:center">2 yrs</span>
          <button class="ml-icon-btn" aria-label="More years">+</button>
        </div>
      </div>

      <p class="ml-type-1" style="color:var(--color-muted)">You can always update this later in Profile settings.</p>
    </main>
    ${cta(P + "location/index.html")}
  </div>` }));

/* 14 · step 7 — Location */
done.push(writeScreen({ flow: "onboarding", screen: "location", body: `
  <div class="ml-app">
    ${nav(7, P + "instrument_proficiency/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Where are you based?", "Helps local discovery and booking. You can hide this on your public profile.")}
      <div class="ml-field">
        <label for="country">Country</label>
        <select id="country"><option>India</option><option>UAE</option><option>United Kingdom</option><option>United States</option></select>
      </div>
      <div class="ml-field">
        <label for="state">State / Region</label>
        <select id="state"><option>Maharashtra</option><option>Delhi</option><option>Karnataka</option><option>Punjab</option><option>Tamil Nadu</option><option>Telangana</option><option>West Bengal</option></select>
      </div>
      <div class="ml-field">
        <label for="city">City</label>
        <select id="city"><option>Mumbai</option><option>Pune</option><option>Navi Mumbai</option><option>Thane</option></select>
      </div>
      <div class="ml-field">
        <label for="area">Area / Locality</label>
        <input id="area" type="text" placeholder="Andheri West" autocomplete="address-level3" />
      </div>
    </main>
    ${cta(P + "profile_creation/index.html")}
  </div>` }));

/* 15 · step 8 — Profile creation */
done.push(writeScreen({ flow: "onboarding", screen: "profile_creation", body: `
  <div class="ml-app">
    ${nav(8, P + "location/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Build your profile", "A complete profile gets discovered and booked more often.")}

      <div style="display:flex;align-items:center;gap:var(--space-3)">
        <span class="ml-avatar ml-avatar--lg" aria-hidden="true"></span>
        <button class="ml-btn ml-btn--secondary">Add photo</button>
      </div>
      <button class="ml-upload-grid__add" style="aspect-ratio:16/9">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M12 5v14M5 12h14"/></svg>
        Add cover photo
      </button>
      <p class="ml-type-1" style="color:var(--color-muted)">Real photos only — no placeholder avatars.</p>

      <div class="ml-field">
        <label for="name">Display name</label>
        <input id="name" type="text" placeholder="Arijit Sen" autocomplete="name" />
      </div>
      <div class="ml-field">
        <label for="bio">Bio</label>
        <textarea id="bio" placeholder="Playback vocalist based in Mumbai. 6 years of session work across Hindi and Punjabi film music."></textarea>
        <span class="ml-type-1" style="color:var(--color-muted)">Up to 300 characters.</span>
      </div>
    </main>
    ${cta(P + "networking_links/index.html")}
  </div>` }));

/* 16 · step 9 — Networking links */
done.push(writeScreen({ flow: "onboarding", screen: "networking_links", body: `
  <div class="ml-app">
    ${nav(9, P + "profile_creation/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Add your links", "Optional — share your music and socials so collaborators can hear your work.")}
      <div class="ml-field"><label for="spotify">Spotify</label><input id="spotify" type="url" inputmode="url" placeholder="open.spotify.com/artist/…" /></div>
      <div class="ml-field"><label for="youtube">YouTube</label><input id="youtube" type="url" inputmode="url" placeholder="youtube.com/@yourchannel" /></div>
      <div class="ml-field"><label for="soundcloud">SoundCloud</label><input id="soundcloud" type="url" inputmode="url" placeholder="soundcloud.com/you" /></div>
      <div class="ml-field"><label for="linkedin">LinkedIn</label><input id="linkedin" type="url" inputmode="url" placeholder="linkedin.com/in/you" /></div>
    </main>
    ${cta(P + "persona_badges/index.html", "Continue",
      `<a class="ml-btn ml-btn--ghost ml-btn--block" href="${P}persona_badges/index.html" style="margin-bottom:var(--space-2)">Skip for now</a>`)}
  </div>` }));

/* 17 · step 10 — Persona badges */
done.push(writeScreen({ flow: "onboarding", screen: "persona_badges", body: `
  <div class="ml-app">
    ${nav(10, P + "networking_links/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("How should you be discovered?", "Pick a few badges that describe how you work.")}
      <div class="ml-chip-wrap">
        ${chip("Available for Collab", true)}${chip("Touring")}${chip("Open to Session Work", true)}
        ${chip("Teaches")}${chip("Recording", true)}${chip("Remixing / Production")}
      </div>
      <p class="ml-type-1" style="color:var(--color-muted)">Badges appear on your public profile and in search filters.</p>
    </main>
    ${cta(P + "review_confirm/index.html")}
  </div>` }));

/* 18 · step 11 — Review */
const reviewRow = (label, value) => `<div class="ml-row">
          <span class="ml-row__body">
            <span class="ml-row__meta">${label}</span>
            <span class="ml-row__title">${value}</span>
          </span>
        </div>`;

done.push(writeScreen({ flow: "onboarding", screen: "review_confirm", body: `
  <div class="ml-app">
    ${nav(11, P + "persona_badges/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Review your profile", "Make sure everything looks right before you publish.")}

      <div>
        <div class="ml-section"><h2>Profile</h2><a class="ml-section__link" href="${P}profile_creation/index.html">Edit</a></div>
        <div class="ml-card ml-card--padded">
          ${reviewRow("Name", "Arijit Sen")}
          ${reviewRow("Based in", "Mumbai, MH · Andheri West")}
        </div>
      </div>

      <div>
        <div class="ml-section"><h2>Role</h2><a class="ml-section__link" href="${P}primary_role/index.html">Edit</a></div>
        <div class="ml-card ml-card--padded">
          ${reviewRow("Primary", "Singer / Vocalist")}
          ${reviewRow("Secondary", "Composer")}
        </div>
      </div>

      <div>
        <div class="ml-section"><h2>Skills</h2><a class="ml-section__link" href="${P}genres/index.html">Edit</a></div>
        <div class="ml-card ml-card--padded" style="display:flex;flex-direction:column;gap:var(--space-3)">
          <div><div class="ml-row__meta">Genres</div><div class="ml-chip-wrap" style="margin-top:var(--space-2)">${chip("Bollywood / Filmi", true)}${chip("Sufi Devotional", true)}</div></div>
          <div><div class="ml-row__meta">Languages</div><div class="ml-chip-wrap" style="margin-top:var(--space-2)">${chip("Hindi", true)}${chip("English", true)}</div></div>
          <div><div class="ml-row__meta">Instruments</div><div class="ml-chip-wrap" style="margin-top:var(--space-2)">${chip("Acoustic Guitar · 6 yrs", true)}${chip("Bass Guitar · 2 yrs", true)}</div></div>
        </div>
      </div>

      <div>
        <div class="ml-section"><h2>Badges</h2><a class="ml-section__link" href="${P}persona_badges/index.html">Edit</a></div>
        <div class="ml-chip-wrap">${chip("Available for Collab", true)}${chip("Open to Session Work", true)}${chip("Recording", true)}</div>
      </div>
    </main>
    ${cta(P + "submit_publish/index.html", "Looks good — publish")}
  </div>` }));

/* 19 · step 12 — Submit. states: submitting / success / error */
done.push(writeScreen({ flow: "onboarding", screen: "submit_publish", body: `
  <div class="ml-app">
    ${nav(12, P + "review_confirm/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;align-items:center;justify-content:center;gap:var(--space-3);text-align:center">
      <div data-state="submitting">
        <div class="ml-spinner" style="margin:0 auto"></div>
        <h1 class="ml-type-3" style="margin:var(--space-3) 0 0">Creating your profile…</h1>
        <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-1) 0 0">This takes a few seconds.</p>
      </div>

      <div data-state="success">
        ${checkMark}
        <h1 class="ml-type-3" style="margin:var(--space-3) 0 0">Profile published</h1>
        <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-1) 0 0">You're now discoverable on MusicLinkd.</p>
        <span class="ml-status ml-status--success" style="margin-top:var(--space-3)">Verified email</span>
      </div>

      <div data-state="error">
        <h1 class="ml-type-3" style="margin:0">Something went wrong</h1>
        <div class="ml-banner ml-banner--error" role="alert" style="margin-top:var(--space-3)">
          We couldn't publish your profile right now. Check your connection and try again.
        </div>
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="${P}dashboard_handoff/index.html" data-state="success">Continue</a>
      <span class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" aria-disabled="true" data-state="submitting">Publishing…</span>
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="?state=submitting" data-state="error">Try again</a>
    </footer>
  </div>` }));

/* 20 · step 13 — Handoff */
const nextStep = (emoji, title, meta, href) => `<a class="ml-choice" href="${href}">
          <span class="ml-emoji" aria-hidden="true">${emoji}</span>
          <span class="ml-choice__body">
            <span class="ml-choice__title">${title}</span>
            <span class="ml-choice__meta">${meta}</span>
          </span>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" style="color:var(--color-muted);flex:none"><path d="m9 6 6 6-6 6"/></svg>
        </a>`;

done.push(writeScreen({ flow: "onboarding", screen: "dashboard_handoff", body: `
  <div class="ml-app">
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3);padding-top:var(--space-4)">
      <div style="text-align:center">
        <h1 class="ml-wordmark" style="font-size:var(--type-4);margin:0">Welcome to MusicLink<em>d</em></h1>
        <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-2) 0 0">Your profile is live. Here's where to go from here.</p>
      </div>
      <div>
        ${nextStep("🎤", "Complete your portfolio", "Add projects, credits and audio", "/preview/flows/portfolio/portfolio_dashboard/index.html")}
        ${nextStep("🔍", "Explore talent &amp; studios", "Find collaborators near you", "/preview/flows/discover/discover_feed/index.html")}
        ${nextStep("🤝", "Connect &amp; collaborate", "Message and follow other artists", "/preview/flows/communication/networking_feed/index.html")}
        ${nextStep("🔐", "Get verified", "Build trust with a verified badge", "/preview/flows/verifications/verification_screen/index.html")}
      </div>
      <div class="ml-banner ml-banner--success">Onboarding complete — 13 of 13 steps done.</div>
    </main>
    ${cta("/preview/flows/discover/discover_feed/index.html", "Go to Discover")}
  </div>` }));

const linkCount = finish("onboarding: ");
console.log("onboarding: wrote " + done.length + " screens, " + linkCount + " links verified");
