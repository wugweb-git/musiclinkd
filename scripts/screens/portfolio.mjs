#!/usr/bin/env node
/**
 * Flow 3 — Add Portfolio (11 screens, registry numbers 21-31).
 *
 * A 9-step create wizard (22-30) bracketed by the dashboard (21) and the
 * published public view (31). Steps carry .ml-steps like onboarding; the two
 * bookends do not, because they are destinations rather than wizard steps.
 *
 * media_preview is a THEME picker, not an upload: screen.json says plainly
 * that no real image-upload flow exists in the app yet and the field is a
 * keyword (artworkTheme). Kept as-is rather than designed into a false
 * affordance. MediaUploadGrid — the widget that would replace it — is
 * PRODUCT-SPEC Part E's highest-priority gap and is built in ml-widgets.css
 * but deliberately not wired here.
 */
import { writeScreen, backArrow, steps, checkMark, tick, finish } from "../write-screen.mjs";

const P = "/preview/flows/portfolio/";
const T = 9;
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

const chip = (label, sel = false) => `<button class="ml-chip" aria-pressed="${sel}">${label}</button>`;

const choice = (title, meta, selected = false) => `<button class="ml-choice" role="radio" aria-checked="${selected}">
          <span class="ml-choice__body">
            <span class="ml-choice__title">${title}</span>
            ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
          </span>
          <span class="ml-choice__mark">${tick}</span>
        </button>`;

/* 21 — Dashboard. states: empty / populated */
done.push(writeScreen({ flow: "portfolio", screen: "portfolio_dashboard", body: `
  <div class="ml-app">
    <header class="ml-appbar">
      <div class="ml-appbar__titles">
        <div class="ml-eyebrow">Portfolio</div>
        <h1 class="ml-appbar__title">Your work</h1>
      </div>
      <div class="ml-appbar__actions">
        <a class="ml-icon-btn" href="${P}choose_project_type/index.html" aria-label="Add project">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M12 5v14M5 12h14"/></svg>
        </a>
      </div>
    </header>
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">

      <a class="ml-card" href="${P}profile_project_view/index.html" data-state="populated">
        <div class="ml-media ml-media--wide ml-media--scrim">
          <img src="https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=600" alt="" />
          <span class="ml-media__tag"><span class="ml-status ml-status--success">Released</span></span>
        </div>
        <div style="padding:var(--space-3)">
          <div class="ml-type-3" style="font-weight:700">Tum Hi Toh (Unplugged)</div>
          <div class="ml-type-1" style="color:var(--color-muted)">Song · Released 14 Feb 2026</div>
          <p class="ml-type-2" style="margin:var(--space-2) 0 var(--space-3)">Acoustic unplugged reinterpretation — 4.2M plays on Spotify.</p>
          <div class="ml-stat-grid" style="grid-template-columns:repeat(3,1fr)">
            <div class="ml-stat"><span class="ml-stat__num">4,200</span><span class="ml-stat__label">Views</span></div>
            <div class="ml-stat"><span class="ml-stat__num">610</span><span class="ml-stat__label">Likes</span></div>
            <div class="ml-stat"><span class="ml-stat__num">88</span><span class="ml-stat__label">Shares</span></div>
          </div>
        </div>
      </a>

      <!-- state: empty -->
      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No projects yet</h2>
        <p class="ml-empty__body">Add a song, album, or performance to start building your public portfolio.</p>
        <a class="ml-btn ml-btn--primary" href="${P}choose_project_type/index.html">Add your first project</a>
      </div>
    </main>
    <!-- the empty state carries its own CTA, so the footer would otherwise be
         an empty bordered strip in that state -->
    <footer class="ml-app__footer" data-state="populated">
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="${P}choose_project_type/index.html">Add a project</a>
    </footer>
  </div>` }));

/* 22 · step 1 — Project type */
done.push(writeScreen({ flow: "portfolio", screen: "choose_project_type", body: `
  <div class="ml-app">
    ${nav(1, P + "portfolio_dashboard/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("What are you adding?", "Choose a project type from the platform's 10 project types.")}
      <div class="ml-chip-wrap">
        ${chip("Song", true)}${chip("Album")}${chip("EP")}${chip("Podcast")}${chip("DJ Mix")}
        ${chip("Film Score")}${chip("Commercial Jingle")}${chip("Live Performance")}
        ${chip("Music Video")}${chip("Custom Project")}
      </div>
    </main>
    ${cta(P + "project_information/index.html")}
  </div>` }));

/* 23 · step 2 — Information */
done.push(writeScreen({ flow: "portfolio", screen: "project_information", body: `
  <div class="ml-app">
    ${nav(2, P + "choose_project_type/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Project information", "Title and description, as they appear on your public portfolio.")}
      <div class="ml-field">
        <label for="pname">Project name</label>
        <input id="pname" type="text" placeholder="Tum Hi Toh (Unplugged)" />
      </div>
      <div class="ml-field">
        <label for="pdesc">Description</label>
        <textarea id="pdesc" placeholder="Acoustic unplugged reinterpretation."></textarea>
      </div>
      <div class="ml-field">
        <label for="pdate">Release date</label>
        <input id="pdate" type="date" value="2026-02-14" />
      </div>
    </main>
    ${cta(P + "streaming_links/index.html")}
  </div>` }));

/* 24 · step 3 — Streaming links */
const linkField = (id, label, ph) => `<div class="ml-field">
        <label for="${id}">${label}</label>
        <input id="${id}" type="url" inputmode="url" placeholder="${ph}" />
      </div>`;

done.push(writeScreen({ flow: "portfolio", screen: "streaming_links", body: `
  <div class="ml-app">
    ${nav(3, P + "project_information/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Add streaming links", "Optional — link this project to where people can listen.")}
      ${linkField("sp", "Spotify", "open.spotify.com/track/…")}
      ${linkField("am", "Apple Music", "music.apple.com/…")}
      ${linkField("yt", "YouTube", "youtube.com/watch?v=…")}
      ${linkField("sc", "SoundCloud", "soundcloud.com/…")}
    </main>
    ${cta(P + "select_role/index.html", "Continue",
      `<a class="ml-btn ml-btn--ghost ml-btn--block" href="${P}select_role/index.html" style="margin-bottom:var(--space-2)">Skip for now</a>`)}
  </div>` }));

/* 25 · step 4 — Your role */
done.push(writeScreen({ flow: "portfolio", screen: "select_role", body: `
  <div class="ml-app">
    ${nav(4, P + "streaming_links/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Your role on this project", "From the 11 project roles — separate from your platform-wide primary role.")}
      <div class="ml-chip-wrap">
        ${chip("Singer", true)}${chip("Composer")}${chip("Music Director")}${chip("Producer")}
        ${chip("Lyricist")}${chip("Session Musician")}${chip("DJ")}${chip("Mix Engineer")}
        ${chip("Mastering Engineer")}${chip("Sound Designer")}${chip("Band Member")}
      </div>
    </main>
    ${cta(P + "add_collaborators/index.html")}
  </div>` }));

/* 26 · step 5 — Collaborators. states: searching / selected */
done.push(writeScreen({ flow: "portfolio", screen: "add_collaborators", body: `
  <div class="ml-app">
    ${nav(5, P + "select_role/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Add collaborators", "Optional — who else worked on this with you?")}

      <div class="ml-search">
        <svg class="ml-search__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/></svg>
        <input type="search" placeholder="Search musicians by name" aria-label="Search collaborators" />
      </div>

      <div class="ml-chip-wrap" data-state="selected">
        <span class="ml-badge ml-badge--accent ml-badge--removable">Arijit Sen<button class="ml-badge__remove" aria-label="Remove Arijit Sen">×</button></span>
      </div>
      <p class="ml-type-1" style="color:var(--color-muted)" data-state="searching">Start typing to find collaborators already on MusicLinkd.</p>

      <div>
        <button class="ml-choice" aria-checked="true" role="checkbox">
          <img class="ml-avatar" src="https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=120" alt="" />
          <span class="ml-choice__body">
            <span class="ml-choice__title">Arijit Sen</span>
            <span class="ml-choice__meta">Singer · Composer · Mumbai, MH</span>
          </span>
          <span class="ml-choice__mark">${tick}</span>
        </button>
        <button class="ml-choice" aria-checked="false" role="checkbox">
          <img class="ml-avatar" src="https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=120" alt="" />
          <span class="ml-choice__body">
            <span class="ml-choice__title">Mehek Kapoor</span>
            <span class="ml-choice__meta">Session Guitarist · Pune, MH</span>
          </span>
          <span class="ml-choice__mark">${tick}</span>
        </button>
      </div>
    </main>
    ${cta(P + "credit_attribution/index.html", "Continue",
      `<a class="ml-btn ml-btn--ghost ml-btn--block" href="${P}media_preview/index.html" style="margin-bottom:var(--space-2)">Skip — solo project</a>`)}
  </div>` }));

/* 27 · step 6 — Credit attribution */
done.push(writeScreen({ flow: "portfolio", screen: "credit_attribution", body: `
  <div class="ml-app">
    ${nav(6, P + "add_collaborators/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Credit each collaborator", "Assign a project role to everyone on this project.")}

      <div class="ml-card ml-card--padded">
        <div class="ml-row" style="padding-top:0">
          <img class="ml-avatar" src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=120" alt="" />
          <span class="ml-row__body"><span class="ml-row__title">You</span><span class="ml-row__meta">Arijit Sen</span></span>
        </div>
        <div class="ml-chip-wrap">
          ${chip("Singer", true)}${chip("Composer")}${chip("Music Director")}${chip("Producer")}
        </div>
      </div>

      <div class="ml-card ml-card--padded">
        <div class="ml-row" style="padding-top:0">
          <img class="ml-avatar" src="https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=120" alt="" />
          <span class="ml-row__body"><span class="ml-row__title">Arijit Sen</span><span class="ml-row__meta">Collaborator</span></span>
        </div>
        <div class="ml-chip-wrap">
          ${chip("Producer")}${chip("Mix Engineer", true)}${chip("Mastering Engineer")}${chip("Session Musician")}
        </div>
      </div>
    </main>
    ${cta(P + "media_preview/index.html")}
  </div>` }));

/* 28 · step 7 — Artwork theme (NOT an upload; see file header) */
done.push(writeScreen({ flow: "portfolio", screen: "media_preview", body: `
  <div class="ml-app">
    ${nav(7, P + "credit_attribution/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Choose an artwork theme", "This picks a palette for your project card.")}
      <div class="ml-banner ml-banner--info">
        Theme only — image upload isn't available in the app yet, so this saves a keyword rather than artwork.
      </div>
      <div class="ml-chip-wrap">
        ${chip("Acoustic", true)}${chip("Electronic")}${chip("Live")}
        ${chip("Cinematic")}${chip("Minimal")}${chip("Devotional")}
      </div>

      <div class="ml-section"><h2>Preview</h2></div>
      <div class="ml-media ml-media--wide ml-media--scrim" style="border-radius:var(--radius-2)">
        <img src="https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=600" alt="" />
        <div class="ml-media__overlay">
          <div class="ml-media-card__title">Tum Hi Toh (Unplugged)</div>
          <div class="ml-media-card__sub">Song · Arijit Sen</div>
        </div>
      </div>
    </main>
    ${cta(P + "publish_project/index.html")}
  </div>` }));

/* 29 · step 8 — Publish. states: ready / publishing / error */
const reviewRow = (label, value) => `<div class="ml-row">
          <span class="ml-row__body"><span class="ml-row__meta">${label}</span><span class="ml-row__title">${value}</span></span>
        </div>`;

done.push(writeScreen({ flow: "portfolio", screen: "publish_project", body: `
  <div class="ml-app">
    ${nav(8, P + "media_preview/index.html")}
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      ${head("Review &amp; publish", "Last look before this goes on your public portfolio.")}

      <div class="ml-card ml-card--padded">
        <div class="ml-type-3" style="font-weight:700">Tum Hi Toh (Unplugged)</div>
        ${reviewRow("Type", "Song")}
        ${reviewRow("Your role", "Singer")}
        ${reviewRow("Collaborators", "Arijit Sen — Mix Engineer")}
        ${reviewRow("Release date", "14 Feb 2026")}
        ${reviewRow("Visibility", "Public")}
      </div>

      <div class="ml-banner ml-banner--error" role="alert" data-state="error">
        Couldn't publish this project. Check your connection and try again.
      </div>
      <div class="ml-spinner" style="margin:0 auto" data-state="publishing"></div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="${P}success/index.html" data-state="ready">Publish project</a>
      <span class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" aria-disabled="true" data-state="publishing">Publishing…</span>
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="?state=publishing" data-state="error">Try again</a>
    </footer>
  </div>` }));

/* 30 · step 9 — Success */
done.push(writeScreen({ flow: "portfolio", screen: "success", body: `
  <div class="ml-app">
    <main class="ml-app__body" style="display:flex;flex-direction:column;align-items:center;justify-content:center;gap:var(--space-3);text-align:center">
      ${checkMark}
      <div>
        <h1 class="ml-type-3" style="margin:0">Project published</h1>
        <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-1) 0 0">"Tum Hi Toh (Unplugged)" is now live on your public portfolio.</p>
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="${P}profile_project_view/index.html">View project</a>
      <a class="ml-btn ml-btn--ghost ml-btn--block" href="${P}portfolio_dashboard/index.html" style="margin-top:var(--space-2)">Back to portfolio</a>
    </footer>
  </div>` }));

/* 31 — Published project, public view */
done.push(writeScreen({ flow: "portfolio", screen: "profile_project_view", body: `
  <div class="ml-app">
    <main class="ml-app__body" style="padding-inline:0">
      <div class="ml-hero" style="aspect-ratio:1/1">
        <img src="https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=800" alt="" />
        <div style="position:absolute;z-index:1;top:calc(var(--space-2) + var(--safe-top));left:var(--space-2);right:var(--space-2);display:flex;justify-content:space-between">
          ${backArrow(P + "portfolio_dashboard/index.html")}
          <button class="ml-icon-btn" aria-label="Share this project">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3v13M8 7l4-4 4 4"/><path d="M5 14v5a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-5"/></svg>
          </button>
        </div>
        <div class="ml-hero__body">
          <span class="ml-status ml-status--accent">Song · Released</span>
          <h1 class="ml-hero__name" style="margin-top:var(--space-2)">Tum Hi Toh (Unplugged)</h1>
          <div class="ml-hero__sub">Arijit Sen · Singer</div>
        </div>
      </div>

      <div style="padding:var(--space-3)">
        <p class="ml-type-2" style="margin:0 0 var(--space-3)">Acoustic unplugged reinterpretation — 4.2M plays on Spotify.</p>

        <div class="ml-player ml-card" style="padding:var(--space-2) var(--space-3)">
          <button class="ml-player__btn" aria-label="Play">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M8 5v14l11-7z"/></svg>
          </button>
          <div class="ml-player__wave" role="slider" aria-label="Seek" aria-valuemin="0" aria-valuemax="100" aria-valuenow="34" tabindex="0">
            ${Array.from({ length: 40 }, (_, i) =>
              `<span class="ml-player__bar${i < 14 ? " is-played" : ""}" style="height:${20 + ((i * 37) % 60)}%"></span>`).join("")}
          </div>
          <span class="ml-player__time">1:12</span>
        </div>

        <div class="ml-stat-grid" style="grid-template-columns:repeat(3,1fr);margin-top:var(--space-3)">
          <div class="ml-stat"><span class="ml-stat__num">4,200</span><span class="ml-stat__label">Views</span></div>
          <div class="ml-stat"><span class="ml-stat__num">610</span><span class="ml-stat__label">Likes</span></div>
          <div class="ml-stat"><span class="ml-stat__num">88</span><span class="ml-stat__label">Shares</span></div>
        </div>

        <div class="ml-section"><h2>Credits</h2></div>
        <div class="ml-card ml-card--padded">
          <div class="ml-row">
            <img class="ml-avatar" src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=120" alt="" />
            <span class="ml-row__body"><span class="ml-row__title">Arijit Sen</span><span class="ml-row__meta">Singer</span></span>
          </div>
          <div class="ml-row">
            <img class="ml-avatar" src="https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=120" alt="" />
            <span class="ml-row__body"><span class="ml-row__title">Arijit Sen</span><span class="ml-row__meta">Mix Engineer</span></span>
          </div>
        </div>

        <div class="ml-section"><h2>Listen</h2></div>
        <!-- These are <button>, not <a>: the streaming URLs come from the
             project document at runtime and there is no destination in the
             prototype. They were links pointing at this same screen, which is
             a dead affordance — a control that looks tappable and goes
             nowhere reads as a bug in review. -->
        <div class="ml-action-row" style="padding-top:0">
          <button class="ml-btn ml-btn--secondary">Spotify</button>
          <button class="ml-btn ml-btn--secondary">Apple Music</button>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted)">Released 14 Feb 2026</p>
      </div>
    </main>
  </div>` }));

const linkCount = finish("portfolio: ");
console.log("portfolio: wrote " + done.length + " screens, " + linkCount + " links verified");
