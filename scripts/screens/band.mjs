#!/usr/bin/env node
/**
 * Flow 15 — Band, against the deferred-flows brief. BETA.
 *
 * 18 numbered sections, 5 screens. The brief's central claim shapes the whole
 * flow: a band is NOT a group chat and NOT a community — it is a collective
 * professional entity that can hold portfolio, events, opportunities and
 * bookings. So the band profile is built from the same components as a person's
 * public profile, not from chat furniture.
 *
 * BAND-07 (invitation), BAND-08 (roles), BAND-15 (privacy), BAND-16 (leave),
 * BAND-17 (remove member) and BAND-18 (delete) are six sections that are states
 * of two screens. The three destructive ones each explain consequences before
 * confirming — never a one-tap row.
 *
 * No new CSS: .ml-facepile, .ml-person-row, .ml-booking-card, .ml-summary,
 * .ml-choice, .ml-media-card, .ml-rail and .ml-empty already exist.
 */
import { writeScreen, backArrow, icon, assertIcons, beta, finish, media } from "../write-screen.mjs";

const B = "/preview/flows/band/";
assertIcons(["arrow-forward", "check", "cancel", "add", "send"]);

const bar = (title, backTo, actions = "") => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-2" style="margin:0;flex:1">${title}</h1>
      ${beta}${actions}
    </header>`;

const rowLink = (label, href, meta) => `<a class="ml-action-row" href="${href}">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>`;

const field = (id, label, type, value, ph) => `<div class="ml-field">
        <label for="${id}">${label}</label>
        ${type === "textarea"
          ? `<textarea id="${id}" rows="3"${ph ? ` placeholder="${ph}"` : ""}>${value || ""}</textarea>`
          : type === "select"
            ? `<select id="${id}">${value.map((o) => `<option>${o}</option>`).join("")}</select>`
            : `<input id="${id}" type="${type}" value="${value || ""}"${ph ? ` placeholder="${ph}"` : ""} />`}
      </div>`;

const LINEUP = [
  ["Ishita Rao", "Vocals", "Owner", 2],
  ["Kabir Sheth", "Guitar", "Admin", 8],
  ["Meera Nair", "Bass", "Member", 4],
  ["Rohan D'Souza", "Drums", "Member", 6],
];

/* ── band_profile_layout — the public band, plus owner and empty states ─── */
writeScreen({
  flow: "band", screen: "band_profile_layout", title: "Band — Profile",
  body: `
  <div class="ml-app">
    ${bar("The Kolaba Sessions", "/preview/flows/discover/discover_feed/index.html",
      `<a class="ml-icon-btn ml-icon-btn--bare" href="${B}band_settings/index.html" aria-label="Band settings">${icon("check", { size: 20 })}</a>`)}
    <main class="ml-app__body app-content--scroll" style="padding:0 0 var(--space-4)">
      <img src="${media("stage", 6)}" alt="" loading="lazy"
           style="width:100%;aspect-ratio:var(--media-aspect-card);object-fit:cover" />
      <div style="padding:var(--space-3) var(--space-4)">
        <h2 class="ml-type-3" style="margin:0">The Kolaba Sessions</h2>
        <p class="ml-type-1" style="color:var(--color-muted);margin:var(--space-1) 0 0">
          Four-piece indie rock · Mumbai · formed 2022
        </p>
        <div class="ml-rating" style="margin-top:var(--space-2)">
          <span class="ml-rating__score">4.7</span>
          <span class="ml-rating__count">· 38 shows · 1,240 followers</span>
        </div>

        <div class="ml-banner ml-banner--info" data-state="private" style="margin-top:var(--space-3)">
          <span class="ml-banner__body">This band is private. Only members can see it.</span>
        </div>
        <div class="ml-banner ml-banner--info" data-state="members_only" style="margin-top:var(--space-3)">
          <span class="ml-banner__body">Visible to MusicLinkd members only — not to the open web.</span>
        </div>

        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          Guitar-led indie with a horn section when the room allows it. Playing together since 2022,
          mostly around Mumbai and Pune. Available for festivals, club nights and session work as a unit.
        </p>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Line-up</h3>
          <a class="ml-section__link" href="${B}ensemble_lineup/index.html">All members</a></div>
        <div class="ml-facepile">
          ${LINEUP.map(([, , , i]) => `<img src="${media("faces", i)}" alt="" loading="lazy" />`).join("\n          ")}
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Music</h3></div>
        <div class="ml-rail" data-state="default owner private members_only no_events no_followers">
          ${[3, 6, 11].map((i) => `<a class="ml-media-card" href="/preview/flows/portfolio/profile_project_view/index.html">
            <img src="${media("stage", i)}" alt="" loading="lazy" />
          </a>`).join("\n          ")}
        </div>
        <div class="ml-empty" data-state="no_portfolio">
          <h3 class="ml-empty__title">No music yet</h3>
          <p class="ml-empty__body">Releases and live recordings you add show up here, and on each member's profile.</p>
          <a class="ml-btn ml-btn--primary" href="/preview/flows/portfolio/portfolio_dashboard/index.html">Add a release</a>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Shows</h3>
          <a class="ml-section__link" href="${B}upcoming_shows/index.html">All dates</a></div>
        <div class="ml-summary" data-state="default owner private members_only no_portfolio no_followers">
          <div class="ml-summary__row"><span>18 Aug · Antisocial, Khar</span><span>₹500</span></div>
          <div class="ml-summary__row"><span>02 Sep · The Quarter</span><span>₹700</span></div>
        </div>
        <div class="ml-empty" data-state="no_events">
          <h3 class="ml-empty__title">No shows announced</h3>
          <p class="ml-empty__body">Add a date and it appears here, on each member's profile, and in Discover.</p>
          <a class="ml-btn ml-btn--primary" href="${B}upcoming_shows/index.html">Add a show</a>
        </div>

        <div class="ml-empty" data-state="no_followers">
          <h3 class="ml-empty__title">No followers yet</h3>
          <p class="ml-empty__body">Share the band's link — the fastest first followers are usually the members' own networks.</p>
          <a class="ml-btn ml-btn--secondary" href="/preview/flows/qr_share/qr_share_flow/index.html">Share the band</a>
        </div>

        <div class="ml-section" style="margin-top:var(--space-3)"><h3>Live documents</h3></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Tech rider</span><span>PDF</span></div>
          <div class="ml-summary__row"><span>Stage plot</span><span>PDF</span></div>
        </div>

        <!-- Owner view: management sits here rather than on a separate page. -->
        <div data-state="owner">
          <div class="ml-section" style="margin-top:var(--space-3)"><h3>Manage</h3></div>
          ${rowLink("Members and roles", `${B}ensemble_lineup/index.html?state=owner`, "4 members, 1 invitation pending")}
          ${rowLink("Shows", `${B}upcoming_shows/index.html`, "2 upcoming")}
          ${rowLink("Opportunities for the band", "/preview/flows/opportunities/opportunities_home/index.html", "Apply as a unit")}
          ${rowLink("Band settings", `${B}band_settings/index.html`)}
        </div>
      </div>
    </main>
    <footer class="ml-app__footer" data-state="default members_only no_portfolio no_events no_followers" style="display:flex;gap:var(--space-2)">
      <a class="ml-btn ml-btn--secondary" style="flex:1" href="/preview/flows/communication/chat_conversation/index.html">Message</a>
      <a class="ml-btn ml-btn--primary" style="flex:1" href="/preview/flows/booking/studio_discovery/index.html">Enquire about a date</a>
    </footer>
  </div>`,
});

/* ── ensemble_lineup — members, roles, invitations, removal ─────────────── */
writeScreen({
  flow: "band", screen: "ensemble_lineup", title: "Band — Line-up",
  body: `
  <div class="ml-app">
    ${bar("Line-up", `${B}band_profile_layout/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="default owner roles">
        ${LINEUP.map(([n, r, role, i]) => `<div class="ml-person-row">
          <img src="${media("faces", i)}" alt="" loading="lazy" />
          <span class="meta"><b>${n}</b><p>${r}${role !== "Member" ? ` · ${role}` : ""}</p></span>
          <span data-state="owner roles">
            <a class="ml-icon-btn ml-icon-btn--bare" href="?state=roles" aria-label="Change role for ${n}">${icon("arrow-forward", { size: 18 })}</a>
          </span>
        </div>`).join("\n        ")}
      </div>

      <div data-state="pending owner">
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Invited</h2></div>
        <div class="ml-person-row">
          <img src="${media("faces", 9)}" alt="" loading="lazy" />
          <span class="meta"><b>Priya Iyer</b><p>Keys · invited 2 days ago</p></span>
          <span class="ml-status ml-status--warning">Pending</span>
        </div>
      </div>

      <!-- What each role can actually do. Four levels, no more — the brief
           warns against unnecessary permission complexity. -->
      <div data-state="roles">
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>What roles can do</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Owner</span><span>Everything, including deleting the band</span></div>
          <div class="ml-summary__row"><span>Admin</span><span>Edit the profile, invite and remove members</span></div>
          <div class="ml-summary__row"><span>Manager</span><span>Shows, enquiries and opportunities</span></div>
          <div class="ml-summary__row"><span>Member</span><span>Appear on the profile, add music</span></div>
        </div>
      </div>

      <!-- The invitation as the invited person sees it. -->
      <div data-state="pending">
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>You've been invited</h2></div>
        <div class="ml-person-row">
          <img src="${media("faces", 2)}" alt="" loading="lazy" />
          <span class="meta"><b>Ishita Rao</b><p>invited you to The Kolaba Sessions</p></span>
        </div>
        <div class="ml-summary" style="margin-top:var(--space-2)">
          <div class="ml-summary__row"><span>Your role</span><span>Keys · Member</span></div>
          <div class="ml-summary__row"><span>Band</span><span>Four-piece indie rock, Mumbai</span></div>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-2)">
          Joining puts you on the band's public profile and lets the band apply for opportunities on your behalf.
          You can leave at any time.
        </p>
        <div style="display:flex;gap:var(--space-2);margin-top:var(--space-3)">
          <a class="ml-btn ml-btn--secondary" style="flex:1" href="?state=default">Decline</a>
          <a class="ml-btn ml-btn--primary" style="flex:1" href="?state=default">Accept</a>
        </div>
      </div>

      <div data-state="remove_member">
        <div class="ml-banner ml-banner--warning">
          <span class="ml-banner__body">Remove Meera Nair from the band?</span>
        </div>
        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>Their profile</span><span>Unaffected</span></div>
          <div class="ml-summary__row"><span>Music they added</span><span>Stays, credited to them</span></div>
          <div class="ml-summary__row"><span>Past shows</span><span>Keep their name</span></div>
          <div class="ml-summary__row"><span>They are told</span><span>Yes, by notification</span></div>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=owner">Remove from band</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=owner">Cancel</a>
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No members yet</h2>
        <p class="ml-empty__body">A band needs at least one more person. Invite them by name or MusicLinkd username.</p>
        <a class="ml-btn ml-btn--primary" href="${B}band_create/index.html?state=members">Invite a member</a>
      </div>
    </main>
    <footer class="ml-app__footer" data-state="owner roles">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${B}band_create/index.html?state=members">Invite a member</a>
    </footer>
  </div>`,
});

/* ── upcoming_shows ─────────────────────────────────────────────────────── */
const SHOWS = [
  ["18 Aug 2026", "Antisocial, Khar", "Doors 8pm · ₹500", "success", "Confirmed"],
  ["02 Sep 2026", "The Quarter, Lower Parel", "Doors 9pm · ₹700", "success", "Confirmed"],
  ["21 Sep 2026", "Bandra Base", "Support slot · ₹400", "warning", "Holding"],
];

writeScreen({
  flow: "band", screen: "upcoming_shows", title: "Band — Shows",
  body: `
  <div class="ml-app">
    ${bar("Shows", `${B}band_profile_layout/index.html`)}
    <nav class="ml-tabs ml-tabs--fill" role="tablist" aria-label="Shows" style="margin:0 var(--space-4)">
      <a class="ml-tabs__tab" role="tab" data-chip="hardcoded" href="?state=hardcoded">Upcoming</a>
      <a class="ml-tabs__tab" role="tab" data-chip="past" href="?state=past">Past</a>
    </nav>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <!-- The declared state is "hardcoded": a registry note that the Android
           screen ships a fixed list rather than reading Firestore. Recorded
           here rather than silently renamed. -->
      <div data-state="hardcoded">
        ${SHOWS.map(([d, v, m, kind, label]) => `<div class="ml-booking-card">
          <div class="ml-booking-card__body">
            <span class="ml-booking-card__title">${v}</span>
            <span class="ml-booking-card__meta">${d} · ${m}</span>
            <span class="ml-status ml-status--${kind}">${label}</span>
          </div>
        </div>`).join("\n        ")}
      </div>

      <div data-state="past">
        <div class="ml-summary">
          ${[["12 Jul 2026", "Antisocial, Khar", "Sold out"], ["28 Jun 2026", "The Quarter", "180 in"],
             ["09 May 2026", "Bandra Base", "120 in"]]
            .map(([d, v, m]) => `<div class="ml-summary__row"><span>${d} · ${v}</span><span>${m}</span></div>`).join("\n          ")}
        </div>
      </div>

      <div class="ml-empty" data-state="empty">
        <h2 class="ml-empty__title">No shows yet</h2>
        <p class="ml-empty__body">Add a date and it appears on the band profile, on every member's profile, and in Discover.</p>
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="${B}band_profile_layout/index.html?state=owner">Add a show</a>
    </footer>
  </div>`,
});

/* ── band_create — identity, members, the invitation, published ─────────── */
writeScreen({
  flow: "band", screen: "band_create", title: "Band — Create",
  body: `
  <div class="ml-app">
    ${bar("Create a band", `${B}band_profile_layout/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="identity">
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          A band is a professional entity of its own — it can hold music, shows, opportunities and bookings,
          separately from any member's profile.
        </p>
        ${field("b-name", "Band name", "text", "", "The Kolaba Sessions")}
        ${field("b-handle", "Username", "text", "", "kolabasessions")}
        ${field("b-kind", "Type", "select", ["Band", "Duo", "Trio", "Collective", "Ensemble", "Orchestra", "Music project"])}
        ${field("b-genre", "Primary genre", "select", ["Indie", "Rock", "Pop", "Jazz", "Classical", "Electronic", "Hip-hop", "Folk"])}
        ${field("b-city", "Where you're based", "text", "Mumbai")}
        ${field("b-year", "Formed", "text", "", "2022")}
        ${field("b-langs", "Languages", "text", "", "Hindi, English")}
        ${field("b-about", "About", "textarea", "", "What you sound like, and who you play to.")}
      </div>

      <div data-state="members">
        <div class="ml-section"><h2>Who's in it?</h2></div>
        <div class="ml-field">
          <label for="b-search">Find someone on MusicLinkd</label>
          <input id="b-search" type="search" placeholder="Name, username or instrument" />
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-1)">
          Members are existing MusicLinkd people — inviting someone links their profile, it doesn't make a copy of it.
        </p>
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Suggested</h2></div>
        ${[["Kabir Sheth", "Producer · Pune", 8], ["Meera Nair", "Playback vocalist · Kochi", 4],
           ["Rohan D'Souza", "Mixing engineer · Bengaluru", 6]]
          .map(([n, m, i]) => `<div class="ml-person-row">
          <img src="${media("faces", i)}" alt="" loading="lazy" />
          <span class="meta"><b>${n}</b><p>${m}</p></span>
          <button class="ml-btn ml-btn--secondary" type="button">Invite</button>
        </div>`).join("\n        ")}
      </div>

      <div data-state="invitation">
        <div class="ml-section"><h2>What they'll see</h2></div>
        <div class="ml-person-row">
          <img src="${media("faces", 2)}" alt="" loading="lazy" />
          <span class="meta"><b>Ishita Rao</b><p>invited you to The Kolaba Sessions</p></span>
        </div>
        ${field("b-msg", "Add a message (optional)", "textarea", "", "We'd love you on keys for the Antisocial run.")}
        ${field("b-role", "Their role", "select", ["Member", "Manager", "Admin"])}
      </div>

      <div class="ml-empty" data-state="published">
        <h2 class="ml-empty__title" style="color:var(--color-success)">Band created</h2>
        <p class="ml-empty__body">
          Invitations are out. The band appears publicly once at least one other member accepts.
        </p>
        <a class="ml-btn ml-btn--primary" href="${B}band_profile_layout/index.html?state=owner">Go to the band</a>
      </div>
    </main>
    <footer class="ml-app__footer" data-state="identity members invitation">
      <a class="ml-btn ml-btn--primary ml-btn--block" href="?state=published">Continue</a>
    </footer>
  </div>`,
});

/* ── band_settings — privacy, and the three destructive actions ─────────── */
writeScreen({
  flow: "band", screen: "band_settings", title: "Band — Settings",
  body: `
  <div class="ml-app">
    ${bar("Band settings", `${B}band_profile_layout/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="default">
        <div class="ml-section"><h2>Band</h2></div>
        ${rowLink("Identity", `${B}band_create/index.html?state=identity`, "Name, genre, city, about")}
        ${rowLink("Members and roles", `${B}ensemble_lineup/index.html?state=roles`, "4 members")}
        ${rowLink("Privacy", "?state=privacy", "Public")}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Leaving</h2></div>
        ${rowLink("Leave this band", "?state=leave", "You stay on MusicLinkd; the band continues")}

        <div class="ml-section" style="margin-top:var(--space-3)"><h2>Danger zone</h2></div>
        <a class="ml-action-row" href="?state=delete">
          <span class="ml-choice__body">
            <span class="ml-choice__title" style="color:var(--color-error)">Delete this band</span>
            <span class="ml-choice__meta">Only the owner can do this. Read what happens first.</span>
          </span>
          ${icon("arrow-forward", { size: 18 })}
        </a>
      </div>

      <div data-state="privacy">
        <div class="ml-section"><h2>Who can see this band?</h2></div>
        <div role="radiogroup" aria-label="Band visibility">
          ${[["Public", "Anyone, including people not on MusicLinkd. Appears in Discover and search.", true],
             ["MusicLinkd members", "Signed-in people only.", false],
             ["Private", "Only members. Useful while you're still forming.", false]]
            .map(([t, m, on]) => `<div class="ml-choice" role="radio" aria-checked="${on}" tabindex="${on ? 0 : -1}">
            <span class="ml-choice__body">
              <span class="ml-choice__title">${t}</span>
              <span class="ml-choice__meta">${m}</span>
            </span>
            <span class="ml-choice__mark"></span>
          </div>`).join("\n          ")}
        </div>
      </div>

      <div data-state="leave">
        <div class="ml-section"><h2>Leave The Kolaba Sessions?</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Your profile</span><span>Unaffected</span></div>
          <div class="ml-summary__row"><span>Music you added</span><span>Stays with the band, credited to you</span></div>
          <div class="ml-summary__row"><span>Past shows</span><span>Keep your name</span></div>
          <div class="ml-summary__row"><span>Rejoining</span><span>Needs a new invitation</span></div>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="${B}band_profile_layout/index.html">Leave band</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=default">Cancel</a>
      </div>

      <div data-state="remove_member">
        <div class="ml-section"><h2>Remove a member</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Removing someone is done from the line-up, so you can see who you're removing.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" href="${B}ensemble_lineup/index.html?state=remove_member">Go to line-up</a>
      </div>

      <div data-state="delete">
        <div class="ml-banner ml-banner--error">
          <span class="ml-banner__body">Deleting a band is permanent. It cannot be undone.</span>
        </div>
        <div class="ml-section" style="margin-top:var(--space-3)"><h2>What happens</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Band profile</span><span>Deleted</span></div>
          <div class="ml-summary__row"><span>Members</span><span>Keep their own profiles</span></div>
          <div class="ml-summary__row"><span>Music</span><span>Returns to whoever uploaded it</span></div>
          <div class="ml-summary__row"><span>Upcoming shows</span><span>Removed from every profile</span></div>
          <div class="ml-summary__row"><span>Past shows</span><span>Kept on members' profiles</span></div>
          <div class="ml-summary__row"><span>Members are told</span><span>Yes, by notification</span></div>
        </div>
        <div class="ml-field" style="margin-top:var(--space-3)">
          <label for="b-confirm">Type the band's name to confirm</label>
          <input id="b-confirm" type="text" placeholder="The Kolaba Sessions" />
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="${B}band_profile_layout/index.html">Delete band</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="?state=default">Cancel</a>
      </div>
    </main>
  </div>`,
});

finish("band: ");
