#!/usr/bin/env node
/**
 * Flow 06 — Collaboration, against its own brief. BETA.
 *
 * Registered inside the existing `communication` flow, which the registry
 * already names "Communication & Collaboration". Messaging keeps its seven
 * screens; these four are the work relationship.
 *
 *   Messaging answers  "can we talk?"
 *   Collaboration answers  "are we working together, on what, and what next?"
 *
 * 71 numbered sections, 4 screens. That ratio is the whole argument:
 *
 *   COLLAB-09/11/13/14/18/22/30  overview, participants, discussion, files,
 *                                timeline, notes, activity — SEVEN sections
 *                                that are areas of one workspace, so they are
 *                                states of collaboration_project.
 *   COLLAB-24 to COLLAB-28       propose, receive, counter, accept, decline —
 *                                one negotiation, so one screen.
 *   COLLAB-42 to COLLAB-48       on hold, resume, complete, cancel, archive —
 *                                lifecycle states of the project.
 *   COLLAB-49 to COLLAB-53       five empty states.
 *   COLLAB-67 to COLLAB-71       five HANDOFFS — review, portfolio, booking,
 *                                service, marketplace. Not screens at all.
 *
 * Two brief rules did real work here:
 *
 *   "Collaboration is not Jira."  Milestones exist. Tasks deliberately do not,
 *                                 even though COLLAB-21 offers them, because
 *                                 the brief itself says only where supported.
 *   "Audio is a first-class       So a track uses .ml-player with a real
 *    collaboration object."       waveform, contributor and version — not
 *                                 .ml-attachment, which is for documents.
 *
 * No new CSS: .ml-player, .ml-attachment, .ml-timeline, .ml-facepile,
 * .ml-credit, .ml-progress, .ml-dropzone and .ml-summary already exist.
 */
import { writeScreen, backArrow, icon, assertIcons, beta, finish, media } from "../write-screen.mjs";

const C = "/preview/flows/communication/";
assertIcons(["arrow-forward", "check", "cancel", "add", "send", "play", "pause"]);

const bar = (title, backTo, actions = "") => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-3" style="margin:0;flex:1">${title}</h1>
      ${beta}${actions}
    </header>`;

const tab = (state, label) =>
  `<a class="ml-tabs__tab" role="tab" data-chip="${state}" href="?state=${state}">${label}</a>`;

const rowLink = (label, href, meta) => `<a class="ml-action-row" href="${href}">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>`;

/* A waveform built from the component the system already has. Audio is not an
   attachment: it plays, it has a contributor and a version. */
const track = (name, meta, played = 40) => `<div class="ml-player">
          <button class="ml-player__btn" type="button" aria-label="Play ${name}">${icon("play", { size: 18 })}</button>
          <span class="ml-player__wave" aria-hidden="true">
            ${Array.from({ length: 32 }, (_, i) => {
              const h = 20 + ((i * 37) % 60);
              return `<span class="ml-player__bar${(i / 32) * 100 < played ? " is-played" : ""}" style="height:${h}%"></span>`;
            }).join("")}
          </span>
          <span class="ml-player__time">2:41</span>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-1) 0 var(--space-3)">${name} · ${meta}</p>`;

/* ── collaborations — four tabs, four states of one list ────────────────── */
const PROJECTS = [
  { st: "active", name: "Neon Monsoon", type: "Single · lead vocal", who: "with Kabir Sheth, Rohan D'Souza",
    status: ["success", "Active"], when: "Mix due 20 Sept", img: media("stage", 3) },
  { st: "active", name: "Kolaba Sessions EP", type: "EP · vocals and topline", who: "with 3 others",
    status: ["warning", "Awaiting stems"], when: "Last activity 2 days ago", img: media("stage", 6) },
  { st: "pending", name: "Aftertaste — remix", type: "Single · topline", who: "invited by Meera Nair",
    status: ["warning", "Invitation"], when: "Invited yesterday", img: media("editorial", 2) },
  { st: "completed", name: "Monsoon Sessions Vol. 1", type: "Album · backing vocals", who: "with Blue Note Recording Co.",
    status: [null, "Completed"], when: "Finished 14 Aug", img: media("gear", 7) },
  { st: "drafts", name: "Untitled film cue", type: "Film · composer", who: "not sent yet",
    status: [null, "Draft"], when: "Created 28 Aug", img: media("stage", 11) },
];

writeScreen({
  flow: "communication", screen: "collaborations", title: "Collaborations",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Collaborations</h1>
      <span class="ml-appbar__actions">
        ${beta}
        <a class="ml-icon-btn ml-icon-btn--bare" href="${C}collaboration_proposal/index.html?state=draft" aria-label="Propose a collaboration">${icon("add", { size: 20 })}</a>
      </span>
    </header>
    <nav class="ml-tabs ml-tabs--fill" role="tablist" aria-label="Collaborations" style="margin:0 var(--space-4)">
      ${tab("active", "Active")}${tab("pending", "Pending")}${tab("completed", "Done")}${tab("drafts", "Drafts")}
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${PROJECTS.map((p) => `<a class="ml-result" data-state="${p.st}" href="${C}collaboration_project/index.html">
        <img class="ml-result__thumb ml-result__thumb--square" src="${p.img}" alt="" loading="lazy" />
        <span class="ml-result__body">
          <span class="ml-result__name">${p.name}</span>
          <span class="ml-result__meta">${p.type} · ${p.who}</span>
          <span class="ml-result__meta">${p.when}</span>
          <span class="ml-status${p.status[0] ? ` ml-status--${p.status[0]}` : ""}">${p.status[1]}</span>
        </span>
      </a>`).join("\n      ")}

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No collaborations yet</h2>
        <p class="ml-empty__body">
          A collaboration turns a conversation into a project — with roles, a timeline and
          somewhere the files live.
        </p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/discover/discover_feed/index.html">Find someone to work with</a>
      </div>

      <div class="ml-empty" data-state="no_invitations">
        <h2 class="ml-empty__title">No pending invitations</h2>
        <p class="ml-empty__body">Invitations to join a project land here.</p>
      </div>
    </main>
  </div>`,
});

/* ── collaboration_project — the workspace, its areas as states ─────────── */
const MILESTONES = [
  ["Lyrics approved", "Kabir Sheth", "14 Aug", "is-done"],
  ["Vocals recorded", "Ishita Rao", "22 Aug", "is-done"],
  ["Rough mix", "Rohan D'Souza", "5 Sept", "is-current"],
  ["Final master", "Rohan D'Souza", "20 Sept", ""],
];

const PEOPLE = [
  ["Ishita Rao", "Lead vocal · you", 2], ["Kabir Sheth", "Producer · owner", 8],
  ["Rohan D'Souza", "Mixing engineer", 6], ["Meera Nair", "Backing vocals", 4],
];

writeScreen({
  flow: "communication", screen: "collaboration_project", title: "Collaboration — Project",
  body: `
  <div class="ml-app">
    ${bar("Neon Monsoon", `${C}collaborations/index.html`)}
    <nav class="ml-tabs" role="tablist" aria-label="Project" style="margin:0 var(--space-4)"
         data-state="overview participants files timeline activity notes no_files no_milestones no_activity">
      ${tab("overview", "Overview")}${tab("participants", "People")}${tab("timeline", "Timeline")}${tab("activity", "Activity")}${tab("notes", "Notes")}
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="overview">
        <span class="ml-status ml-status--success">Active</span>
        <h2 class="ml-type-3" style="margin:var(--space-2) 0 var(--space-1)">Neon Monsoon</h2>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0">
          Single · Hindi pop. Lead vocal and topline, tracked at Yash Raj, mixed remotely.
        </p>
        <div class="ml-facepile" style="margin-top:var(--space-3)">
          ${PEOPLE.map(([, , i]) => `<img src="${media("faces", i)}" alt="" loading="lazy" />`).join("\n          ")}
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Latest</h3>
          <a class="ml-section__link" href="${C}collaboration_files/index.html">All files</a></div>
        ${track("Neon Monsoon — rough mix v3", "Rohan D'Souza · 2 days ago")}

        <div class="ml-section"><h3>Where it's up to</h3>
          <a class="ml-section__link" href="?state=timeline">Timeline</a></div>
        <div class="ml-progress">
          <span class="ml-progress__title">2 of 4 milestones</span>
          <span class="ml-progress__meta">50%</span>
          <span class="ml-progress__track"><span class="ml-progress__fill" style="width:50%"></span></span>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Agreed</h3></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Your role</span><span>Lead vocal</span></div>
          <div class="ml-summary__row"><span>Credit</span><span>Yes, on release</span></div>
          <div class="ml-summary__row"><span>Fee</span><span>₹15,000</span></div>
          <div class="ml-summary__row"><span>Delivery</span><span>20 September</span></div>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          What everyone agreed when the proposal was accepted. It is a record, not a contract.
        </p>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>The project needs</h3></div>
        ${rowLink("Book a room", "/preview/flows/booking/studio_discovery/index.html", "Studio or rehearsal")}
        ${rowLink("Find a service", "/preview/flows/public_profile/talent_services_view/index.html", "Mixing, mastering, art")}
        ${rowLink("Rent equipment", "/preview/flows/marketplace/marketplace_browse/index.html?state=rental")}
      </div>

      <div data-state="participants">
        ${PEOPLE.map(([n, r, i]) => `<div class="ml-person-row">
          <img src="${media("faces", i)}" alt="" loading="lazy" />
          <span class="meta"><b>${n}</b><p>${r}</p></span>
          <a class="ml-icon-btn ml-icon-btn--bare" href="${C}chat_conversation/index.html" aria-label="Message ${n}">${icon("send", { size: 18 })}</a>
        </div>`).join("\n        ")}
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-2)" href="${C}collaboration_proposal/index.html?state=invitation">Invite someone</a>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Conversations happen in Messages — this lists who is on the project, not what was said.
        </p>
      </div>

      <div data-state="timeline">
        <ol class="ml-timeline">
          ${MILESTONES.map(([t, who, when, cls]) => `<li class="ml-timeline__item ${cls}">
            <span class="ml-timeline__title">${t}</span>
            <span class="ml-timeline__meta">${who} · ${when}</span>
          </li>`).join("\n          ")}
        </ol>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-3)" href="?state=overview">Add a milestone</a>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Milestones, not tasks. Four dates people agree on beats forty someone has to tick off.
        </p>
      </div>

      <div data-state="activity">
        <ol class="ml-timeline">
          ${[["Rohan uploaded rough mix v3", "2 days ago", "is-done"],
             ["Ishita completed Vocals recorded", "22 Aug", "is-done"],
             ["Meera joined the project", "18 Aug", "is-done"],
             ["Kabir accepted the proposal", "14 Aug", "is-done"],
             ["Kabir created the project", "12 Aug", "is-done"]]
            .map(([t, w, cls]) => `<li class="ml-timeline__item ${cls}">
            <span class="ml-timeline__title">${t}</span>
            <span class="ml-timeline__meta">${w}</span>
          </li>`).join("\n          ")}
        </ol>
      </div>

      <div data-state="notes">
        <div class="ml-section"><h3>Decisions</h3></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Key</span><span>D minor, capo 2</span></div>
          <div class="ml-summary__row"><span>Reference</span><span>Restrained verse, open chorus</span></div>
          <div class="ml-summary__row"><span>Vocal</span><span>Double-track the chorus only</span></div>
        </div>
        <div class="ml-field" style="margin-top:var(--space-3)">
          <label for="p-note">Add a note</label>
          <textarea id="p-note" rows="3" placeholder="Something everyone should remember."></textarea>
        </div>
      </div>

      <!-- Lifecycle. Same project, different state of the world. -->
      <div data-state="on_hold">
        <div class="ml-banner ml-banner--warning">
          <span class="ml-banner__body">On hold since 26 August — waiting on stems from Kabir.</span>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          Nothing is lost while a project is on hold. Deadlines stop counting and nobody is chased.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=overview">Resume project</a>
      </div>

      <div class="ml-empty" data-state="completed">
        <h2 class="ml-empty__title">Completed 14 August</h2>
        <p class="ml-empty__body">Four people, four milestones, eleven files. Two things left worth doing.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/portfolio/portfolio_dashboard/index.html">Add to your portfolio</a>
        <a class="ml-btn ml-btn--secondary" style="margin-top:var(--space-2)" href="/preview/flows/reviews/completed_project_review/index.html">Review the collaboration</a>
      </div>

      <div class="ml-empty" data-state="cancelled">
        <h2 class="ml-empty__title">This project was cancelled</h2>
        <p class="ml-empty__body">Files and messages are kept for everyone who was on it. Nothing was deleted.</p>
        <a class="ml-btn ml-btn--primary" href="${C}collaborations/index.html">Back to collaborations</a>
      </div>

      <div class="ml-empty" data-state="archived">
        <h2 class="ml-empty__title">Archived</h2>
        <p class="ml-empty__body">Out of your active list, still readable, and restorable.</p>
        <a class="ml-btn ml-btn--primary" href="?state=overview">Restore project</a>
      </div>

      <div class="ml-empty" data-state="no_files">
        <h2 class="ml-empty__title">No files yet</h2>
        <p class="ml-empty__body">Stems, references, lyrics, a rough phone recording — whatever gets it started.</p>
        <a class="ml-btn ml-btn--primary" href="${C}collaboration_files/index.html?state=uploading">Upload the first file</a>
      </div>
      <div class="ml-empty" data-state="no_milestones">
        <h2 class="ml-empty__title">No milestones</h2>
        <p class="ml-empty__body">Three or four dates everyone agrees on is usually enough.</p>
        <a class="ml-btn ml-btn--primary" href="?state=timeline">Add a milestone</a>
      </div>
      <div class="ml-empty" data-state="no_activity">
        <h2 class="ml-empty__title">Nothing has happened yet</h2>
        <p class="ml-empty__body">Joins, uploads and completed milestones show up here.</p>
      </div>

      <div class="ml-empty" data-state="permission_denied">
        <h2 class="ml-empty__title">You don't have access to this project</h2>
        <p class="ml-empty__body">Only its participants can open it.</p>
        <a class="ml-btn ml-btn--primary" href="${C}collaborations/index.html">Back</a>
      </div>
      <div class="ml-empty" data-state="deleted">
        <h2 class="ml-empty__title">This collaboration is no longer available</h2>
        <a class="ml-btn ml-btn--primary" href="${C}collaborations/index.html">Back to collaborations</a>
      </div>
    </main>
    <footer class="ml-app__footer" data-state="overview participants timeline activity notes" style="display:flex;gap:var(--space-2)">
      <a class="ml-btn ml-btn--secondary" style="flex:1" href="${C}chat_conversation/index.html">Discuss</a>
      <a class="ml-btn ml-btn--primary" style="flex:1" href="${C}collaboration_files/index.html">Files</a>
    </footer>
  </div>`,
});

/* ── collaboration_proposal — one negotiation, five states ──────────────── */
const TERMS = [
  ["Your role", "Lead vocal"],
  ["Scope", "Two tracks, lead and stacked chorus"],
  ["Deliverables", "Dry stems, 48k/24-bit"],
  ["Revisions", "Two included"],
  ["Timeline", "Vocals by 5 Sept, revisions by 12 Sept"],
  ["Fee", "₹15,000, half on start"],
  ["Credit", "Lead vocal, on release"],
  ["Ownership", "Kabir Sheth retains the master"],
];

writeScreen({
  flow: "communication", screen: "collaboration_proposal", title: "Collaboration — Proposal",
  body: `
  <div class="ml-app">
    ${bar("Proposal", `${C}collaborations/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="draft">
        <div class="ml-section"><h2>Propose a collaboration</h2></div>
        <div class="ml-field"><label for="pr-name">Project name</label>
          <input id="pr-name" type="text" value="Neon Monsoon" /></div>
        <div class="ml-field"><label for="pr-type">Type</label>
          <select id="pr-type">${["Single", "EP", "Album", "Live performance", "Recording session", "Production", "Composition", "Film / media", "Podcast"].map((o) => `<option>${o}</option>`).join("")}</select></div>
        <div class="ml-field"><label for="pr-role">Their role</label>
          <select id="pr-role">${["Lead vocal", "Backing vocal", "Producer", "Composer", "Lyricist", "Musician", "Mixing engineer", "Mastering engineer"].map((o) => `<option>${o}</option>`).join("")}</select></div>
        <div class="ml-field"><label for="pr-scope">Scope</label>
          <textarea id="pr-scope" rows="3" placeholder="What you're asking them to do, plainly.">Two tracks, lead and stacked chorus.</textarea></div>
        <div class="ml-field"><label for="pr-fee">Fee (₹)</label>
          <input id="pr-fee" type="text" value="15000" /></div>
        <div class="ml-field"><label for="pr-when">Deliver by</label>
          <input id="pr-when" type="text" value="5 September 2026" /></div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          This records what you agree. It is not a contract, and MusicLinkd is not a party to it.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=received">Send proposal</a>
      </div>

      <div data-state="received">
        <div class="ml-person-row">
          <img src="${media("faces", 8)}" alt="" loading="lazy" />
          <span class="meta"><b>Kabir Sheth</b><p>proposed a collaboration</p></span>
        </div>
        <h2 class="ml-type-3" style="margin:var(--space-3) 0 var(--space-1)">Neon Monsoon</h2>
        <p class="ml-type-1" style="color:var(--color-muted);margin:0 0 var(--space-3)">Single · Hindi pop</p>
        <div class="ml-summary">
          ${TERMS.map(([k, v]) => `<div class="ml-summary__row"><span>${k}</span><span>${v}</span></div>`).join("\n          ")}
        </div>
        <div style="display:flex;gap:var(--space-2);margin-top:var(--space-3)">
          <a class="ml-btn ml-btn--secondary" style="flex:1" href="?state=declined">Decline</a>
          <a class="ml-btn ml-btn--primary" style="flex:1" href="?state=accepted">Accept</a>
        </div>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=counter">Counter</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="${C}chat_conversation/index.html">Ask a question first</a>
      </div>

      <div data-state="counter">
        <div class="ml-section"><h2>Your counter</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Change only what you want to change. Everything else stays as proposed.
        </p>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Fee · proposed</span><span>₹15,000</span></div>
          <div class="ml-summary__row"><span>Fee · yours</span><span>₹18,000</span></div>
          <div class="ml-summary__row"><span>Deliver by · proposed</span><span>5 September</span></div>
          <div class="ml-summary__row"><span>Deliver by · yours</span><span>12 September</span></div>
        </div>
        <div class="ml-field" style="margin-top:var(--space-3)">
          <label for="pr-why">Why (optional)</label>
          <textarea id="pr-why" rows="3">Three sessions rather than two, and I'm away the first week of September.</textarea>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=received">Send counter</a>
      </div>

      <div class="ml-empty" data-state="accepted">
        <h2 class="ml-empty__title" style="color:var(--color-success)">You're collaborating</h2>
        <p class="ml-empty__body">
          Neon Monsoon, lead vocal, ₹15,000, delivered by 5 September. Everyone on it can see those terms.
        </p>
        <a class="ml-btn ml-btn--primary" href="${C}collaboration_project/index.html">Open the project</a>
      </div>

      <div class="ml-empty" data-state="declined">
        <h2 class="ml-empty__title">Proposal declined</h2>
        <p class="ml-empty__body">Kabir has been told. You can still message each other.</p>
        <a class="ml-btn ml-btn--primary" href="${C}chat_conversation/index.html">Send a message</a>
      </div>

      <div data-state="invitation">
        <div class="ml-section"><h2>Invite someone to Neon Monsoon</h2></div>
        <div class="ml-field">
          <label for="pr-who">Find them on MusicLinkd</label>
          <input id="pr-who" type="search" placeholder="Name, username or instrument" />
        </div>
        ${[["Meera Nair", "Playback vocalist · Kochi", 4], ["Rohan D'Souza", "Mixing engineer · Bengaluru", 6]]
          .map(([n, m, i]) => `<div class="ml-person-row">
          <img src="${media("faces", i)}" alt="" loading="lazy" />
          <span class="meta"><b>${n}</b><p>${m}</p></span>
          <button class="ml-btn ml-btn--secondary" type="button">Invite</button>
        </div>`).join("\n        ")}
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Inviting someone links their existing profile. It never creates a second account for them.
        </p>
      </div>
    </main>
  </div>`,
});

/* ── collaboration_files — audio first-class, filters as states ─────────── */
writeScreen({
  flow: "communication", screen: "collaboration_files", title: "Collaboration — Files",
  body: `
  <div class="ml-app">
    ${bar("Files", `${C}collaboration_project/index.html`,
      `<a class="ml-icon-btn ml-icon-btn--bare" href="?state=uploading" aria-label="Upload a file">${icon("add", { size: 20 })}</a>`)}
    <nav class="ml-tabs" role="tablist" aria-label="File type" style="margin:0 var(--space-4)"
         data-state="all audio video images documents versions">
      ${tab("all", "All")}${tab("audio", "Audio")}${tab("video", "Video")}${tab("images", "Images")}${tab("documents", "Docs")}
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <!-- Audio is not an attachment: it plays, and it says who made it and
           which version it is. -->
      <div data-state="all audio">
        <div class="ml-section"><h3>Rough mix v3</h3>
          <a class="ml-section__link" href="?state=versions">3 versions</a></div>
        ${track("Neon Monsoon — rough mix v3", "Rohan D'Souza · 2 days ago · current")}
        <div class="ml-section"><h3>Vocal stems</h3></div>
        ${track("Neon Monsoon — lead vocal, comped", "Ishita Rao · 22 Aug", 100)}
        ${track("Neon Monsoon — chorus stack", "Ishita Rao · 22 Aug", 100)}
      </div>

      <div data-state="all images">
        <div class="ml-section"><h3>References and artwork</h3></div>
        <div class="ml-upload-grid">
          ${[3, 6, 11].map((i) => `<div class="ml-upload-grid__item">
            <img src="${media("stage", i)}" alt="" loading="lazy" />
          </div>`).join("\n          ")}
          <button class="ml-upload-grid__add" type="button" aria-label="Add an image"></button>
        </div>
      </div>

      <div data-state="all video">
        <div class="ml-section"><h3>Video</h3></div>
        <div class="ml-attachment">
          <span class="ml-attachment__icon">${icon("play", { size: 18 })}</span>
          <span class="ml-attachment__body">
            <span class="ml-attachment__name">Live room take — 22 Aug.mp4</span>
            <span class="ml-attachment__meta">Kabir Sheth · 84 MB</span>
          </span>
        </div>
      </div>

      <!-- Documents DO use .ml-attachment. That is the distinction the brief
           asks for: audio is a first-class object, a lyric sheet is a file. -->
      <div data-state="all documents">
        <div class="ml-section"><h3>Documents</h3></div>
        ${[["Neon Monsoon — lyrics v2.pdf", "Kabir Sheth · 84 KB"],
           ["Split sheet — draft.pdf", "Kabir Sheth · 120 KB"],
           ["Chord chart.pdf", "Ishita Rao · 61 KB"]]
          .map(([n, m]) => `<div class="ml-attachment">
          <span class="ml-attachment__icon">${icon("check", { size: 18 })}</span>
          <span class="ml-attachment__body">
            <span class="ml-attachment__name">${n}</span>
            <span class="ml-attachment__meta">${m}</span>
          </span>
        </div>`).join("\n        ")}
      </div>

      <!-- Versions are kept, never overwritten. -->
      <div data-state="versions">
        <div class="ml-section"><h3>Rough mix — all versions</h3></div>
        <ol class="ml-timeline">
          ${[["v3 — current", "Rohan D'Souza · 2 days ago", "Brought the vocal up 1.5dB, tamed 3k", "is-current"],
             ["v2", "Rohan D'Souza · 28 Aug", "First pass with the stacked chorus", "is-done"],
             ["v1", "Rohan D'Souza · 24 Aug", "Rough balance from the tracking session", "is-done"]]
            .map(([t, who, note, cls]) => `<li class="ml-timeline__item ${cls}">
            <span class="ml-timeline__title">${t}</span>
            <span class="ml-timeline__meta">${who}</span>
            <span class="ml-timeline__meta">${note}</span>
          </li>`).join("\n          ")}
        </ol>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Older versions are kept. Nothing is overwritten.
        </p>
      </div>

      <div data-state="uploading">
        <div class="ml-dropzone">
          <span class="ml-dropzone__hint">Drop a file, or choose one</span>
          <span class="ml-dropzone__formats">WAV, MP3, MP4, JPG, PNG, PDF</span>
        </div>
        <div class="ml-progress" style="margin-top:var(--space-3)">
          <span class="ml-progress__title">Neon Monsoon — rough mix v4.wav</span>
          <span class="ml-progress__meta">62%</span>
          <span class="ml-progress__track"><span class="ml-progress__fill" style="width:62%"></span></span>
        </div>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=all">Cancel upload</a>
      </div>

      <div data-state="upload_error">
        <div class="ml-banner ml-banner--error">
          <span class="ml-banner__body">Upload failed at 62%. Nothing was saved.</span>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=uploading">Retry</a>
      </div>

      <div data-state="access">
        <div class="ml-section"><h3>Who can open this file?</h3></div>
        <div role="radiogroup" aria-label="File access">
          ${[["Everyone on the project", "The default. Four people.", true],
             ["Just the owner and me", "For a rough idea you're not ready to share.", false]]
            .map(([t, m, on]) => `<div class="ml-choice" role="radio" aria-checked="${on}" tabindex="${on ? 0 : -1}">
            <span class="ml-choice__body">
              <span class="ml-choice__title">${t}</span>
              <span class="ml-choice__meta">${m}</span>
            </span>
            <span class="ml-choice__mark"></span>
          </div>`).join("\n          ")}
        </div>
      </div>

      <div class="ml-empty" data-state="unavailable">
        <h2 class="ml-empty__title">This file is no longer available</h2>
        <p class="ml-empty__body">Whoever uploaded it removed it. Other versions may still be there.</p>
        <a class="ml-btn ml-btn--primary" href="?state=versions">See versions</a>
      </div>
    </main>
  </div>`,
});

finish("collaboration: ");
