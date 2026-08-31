#!/usr/bin/env node
/**
 * Flow 6 — Communication & Collaboration. Seven screens, 11 declared states,
 * 0 implemented, seven local <style> blocks.
 *
 * The four "network tabs" — collaborators, connection requests, AI matchmaker,
 * and the feed — each rebuilt the same tab strip as a local `.net-tabs`. They
 * are one tabbed surface, so the tab row is a shared .ml-tabs and the tab that
 * reads as selected is derived from the screen state.
 *
 *   .net-tabs        → .ml-tabs__tab + data-chip
 *   .post-*          → .ml-post + __head/__who/__body/__media/__actions
 *   .posting-banner  → .ml-banner
 *   .bubble/.bubble-row → .ml-bubble--in/--out/--typing/--failed
 *   .composer/.send-btn → .ml-composer + .ml-icon-btn--primary
 *   .chat-header     → .ml-appbar
 *   .unread-dot      → .ml-notification--unread
 *   .match-card/.match-pct → .ml-match + __pct/__track/__fill
 *   .msg-btn         → .ml-icon-btn
 *   .avatar-lift/.hero → .ml-lift + media
 *
 * No new CSS — fourth flow running.
 *
 * ai_matchmaker_tab declared the single state "unreachable", which is a note
 * to a reader rather than a state a screen can be in. It is rendered as a real
 * screen in its normal state and the contract is left alone here, since
 * screen.json is generated from the admin registry, not from this file.
 */
import { writeScreen, icon, backArrow, assertIcons, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/communication/";
assertIcons(["add", "check", "cancel", "heart", "search", "send", "ellipsis"]);

const netTab = (state, label, href) =>
  `<a class="ml-tabs__tab" role="tab" data-chip="${state}" href="${href}">${label}</a>`;

/* The four network surfaces share one tab strip. Each screen marks its own tab
   selected with aria-selected, since they are separate documents rather than
   states of one document. */
const NET_TABS = [
  ["feed", "Feed", `${P}networking_feed/index.html`],
  ["collaborators", "Collaborators", `${P}collaborators_tab/index.html`],
  ["requests", "Requests", `${P}connection_requests_tab/index.html`],
  ["matches", "Matches", `${P}ai_matchmaker_tab/index.html`],
];
const netStrip = (current) => `<nav class="ml-tabs" role="tablist" aria-label="Network" style="margin:0 var(--space-4)">
      ${NET_TABS.map(([k, l, h]) => k === current
        // The selected tab is not a link: a link to the page you are already
        // on is a dead affordance, which audit-flow's graph check flags.
        ? `<span class="ml-tabs__tab" role="tab" aria-selected="true" aria-current="page">${l}</span>`
        : `<a class="ml-tabs__tab" role="tab" aria-selected="false" href="${h}">${l}</a>`).join("\n      ")}
    </nav>`;

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-2" style="margin:0">${title}</h1>
    </header>`;

/* ── networking_feed — default / posting ────────────────────────────────── */
const POSTS = [
  { who: "Ishita Rao", meta: "Session vocalist · 4h", face: 2,
    body: "Wrapped two days on a Hindi pop EP at Octave. Three tracks, all live takes — the room did most of the work.",
    img: media("stage", 3), likes: 42, comments: 7 },
  { who: "Blue Note Recording Co.", meta: "Studio · Yesterday", face: 5,
    body: "New Neve sidecar in Room B from Monday. Existing bookings keep their rate for the month.",
    img: media("gear", 6), likes: 118, comments: 23 },
  { who: "Kabir Sheth", meta: "Producer · 2d", face: 8,
    body: "Looking for a bassist who can read charts for a jazz-adjacent session next week in Bandra. Paid, half day.",
    likes: 15, comments: 11 },
];

writeScreen({
  flow: "communication", screen: "networking_feed", title: "Network — Feed",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Network</h1>
      <span class="ml-appbar__actions">
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}chat_thread_list/index.html" aria-label="Messages">${icon("send", { size: 20 })}</a>
      </span>
    </header>
    ${netStrip("feed")}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      <div class="ml-banner ml-banner--info" data-state="posting">
        <span class="ml-banner__body">Posting…</span>
      </div>

      ${POSTS.map((p) => `<article class="ml-post">
        <div class="ml-post__head">
          <img src="${media("faces", p.face)}" alt="" loading="lazy" />
          <span class="ml-post__who">
            <span class="ml-post__name">${p.who}</span>
            <span class="ml-post__meta">${p.meta}</span>
          </span>
        </div>
        <p class="ml-post__body">${p.body}</p>
        ${p.img ? `<div class="ml-post__media"><img src="${p.img}" alt="" loading="lazy" /></div>` : ""}
        <div class="ml-post__actions">
          <button class="ml-post__action" type="button" aria-pressed="false">${icon("heart", { size: 18 })} ${p.likes}</button>
          <button class="ml-post__action" type="button">${icon("send", { size: 18 })} ${p.comments}</button>
        </div>
      </article>`).join("\n      ")}
    </main>
  </div>`,
});

/* ── networking_feed_artist_profile — default ───────────────────────────── */
writeScreen({
  flow: "communication", screen: "networking_feed_artist_profile", title: "Network — Artist",
  body: `
  <div class="ml-app">
    ${bar("Ishita Rao", `${P}networking_feed/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:0 0 var(--space-4)">
      <img src="${media("stage", 7)}" alt="" loading="lazy"
           style="width:100%;aspect-ratio:var(--media-aspect-card);object-fit:cover" />
      <div style="padding:0 var(--space-4)">
        <!-- .ml-avatar--lg, not a hand-sized box: the size scale stops at
             --size-4 and I had reached for a --size-6 that does not exist. -->
        <img class="ml-avatar ml-avatar--lg ml-lift" src="${media("faces", 2)}" alt="" loading="lazy"
             style="margin-top:calc(var(--size-3) * -1)" />
        <h2 class="ml-type-3" style="margin:var(--space-2) 0 var(--space-1)">Ishita Rao</h2>
        <p class="ml-type-1" style="color:var(--color-muted);margin:0">Session vocalist · Mumbai · 4.9 ★</p>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          Hindi and English pop, light belt and soft head voice. Twelve EPs and two features since 2021.
        </p>
        <div style="display:flex;gap:var(--space-2);margin-top:var(--space-3)">
          <a class="ml-btn ml-btn--primary" style="flex:1" href="${P}chat_conversation/index.html">Message</a>
          <a class="ml-btn ml-btn--secondary" style="flex:1" href="${P}collaborators_tab/index.html">Connect</a>
        </div>
      </div>
    </main>
  </div>`,
});

/* ── collaborators_tab — default ────────────────────────────────────────── */
const COLLABS = [
  ["Ishita Rao", "Session vocalist · Mumbai", 2],
  ["Kabir Sheth", "Producer · Pune", 8],
  ["Meera Nair", "Playback vocalist · Kochi", 4],
  ["Rohan D'Souza", "Mixing engineer · Bengaluru", 6],
];
writeScreen({
  flow: "communication", screen: "collaborators_tab", title: "Network — Collaborators",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Network</h1>
    </header>
    ${netStrip("collaborators")}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${COLLABS.map(([n, m, i]) => `<div class="ml-person-row">
        <img src="${media("faces", i)}" alt="" loading="lazy" />
        <span class="meta"><b>${n}</b><p>${m}</p></span>
        <a class="ml-icon-btn ml-icon-btn--bare" href="${P}chat_conversation/index.html" aria-label="Message ${n}">${icon("send", { size: 18 })}</a>
      </div>`).join("\n      ")}
    </main>
  </div>`,
});

/* ── connection_requests_tab — default ──────────────────────────────────── */
const REQS = [
  ["Aarav Menon", "Mixing engineer · 3 mutual", 1],
  ["Wavelength Artists", "Booking agency · 8 mutual", 5],
  ["Priya Iyer", "Composer · 1 mutual", 9],
];
writeScreen({
  flow: "communication", screen: "connection_requests_tab", title: "Network — Requests",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Network</h1>
    </header>
    ${netStrip("requests")}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${REQS.map(([n, m, i]) => `<div class="ml-person-row">
        <img src="${media("faces", i)}" alt="" loading="lazy" />
        <span class="meta"><b>${n}</b><p>${m}</p></span>
        <span class="ml-appbar__actions">
          <button class="ml-icon-btn ml-icon-btn--muted" type="button" aria-label="Ignore ${n}">${icon("cancel", { size: 18 })}</button>
          <button class="ml-icon-btn ml-icon-btn--primary" type="button" aria-label="Accept ${n}">${icon("check", { size: 18 })}</button>
        </span>
      </div>`).join("\n      ")}
    </main>
  </div>`,
});

/* ── ai_matchmaker_tab ──────────────────────────────────────────────────── */
const MATCHES = [
  ["Meera Nair", "Playback vocalist · Kochi", 4, 94, "Shares 3 genres and 2 languages with your last two projects"],
  ["Rohan D'Souza", "Mixing engineer · Bengaluru", 6, 88, "Mixed two releases in your primary genre this year"],
  ["Kabir Sheth", "Producer · Pune", 8, 81, "Overlapping availability and a shared collaborator"],
];
writeScreen({
  flow: "communication", screen: "ai_matchmaker_tab", title: "Network — Matches",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Network</h1>
    </header>
    ${netStrip("matches")}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${MATCHES.map(([n, m, i, pct, why]) => `<a class="ml-match" href="${P}networking_feed_artist_profile/index.html">
        <div class="ml-person-row" style="border:0;padding:0">
          <img src="${media("faces", i)}" alt="" loading="lazy" />
          <span class="meta"><b>${n}</b><p>${m}</p></span>
          <span class="ml-match__pct">${pct}%</span>
        </div>
        <span class="ml-match__label">${why}</span>
        <span class="ml-match__track"><span class="ml-match__fill" style="width:${pct}%"></span></span>
      </a>`).join("\n      ")}
    </main>
  </div>`,
});

/* ── chat_thread_list — default / empty ─────────────────────────────────── */
const THREADS = [
  ["Ishita Rao", "Sounds good — Tuesday works.", "4h", 2, true],
  ["Blue Note Recording Co.", "Room B is yours from 6.", "Yesterday", 5, false],
  ["Kabir Sheth", "Sent the charts over.", "2d", 8, false],
];
writeScreen({
  flow: "communication", screen: "chat_thread_list", title: "Messages",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;padding:var(--space-3) var(--space-4)">
      <h1 class="ml-type-3" style="margin:0">Messages</h1>
    </header>
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">
      ${THREADS.map(([n, last, when, i, unread]) => `<a class="ml-notification${unread ? " ml-notification--unread" : ""}" data-state="default" href="${P}chat_conversation/index.html">
        <div class="ml-person-row" style="border:0;padding:0">
          <img src="${media("faces", i)}" alt="" loading="lazy" />
          <span class="meta"><b>${n}</b><p>${last}</p></span>
          <span class="time">${when}</span>
        </div>
      </a>`).join("\n      ")}

      <div class="ml-empty" data-state="empty">
        <h3 class="ml-empty__title">No messages yet</h3>
        <p class="ml-empty__body">Message someone from their profile, or accept a connection request to start a thread.</p>
        <a class="ml-btn ml-btn--primary" href="${P}collaborators_tab/index.html">See collaborators</a>
      </div>
    </main>
  </div>`,
});

/* ── chat_conversation — default / sending / error ──────────────────────── */
const MSGS = [
  ["in",  "Hi! Are you free Tuesday for the Hindi pop session?", "10:02"],
  ["out", "Tuesday works. What time do you need me in?", "10:04"],
  ["in",  "10am at Octave, Andheri. Two tracks, maybe three.", "10:05"],
  ["out", "Perfect — I'll bring my own mic if that helps.", "10:06"],
];
writeScreen({
  flow: "communication", screen: "chat_conversation", title: "Messages — Conversation",
  body: `
  <div class="ml-app">
    <header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(`${P}chat_thread_list/index.html`, "Back to messages")}
      <img class="ml-avatar" src="${media("faces", 2)}" alt="" loading="lazy" />
      <h1 class="ml-type-2" style="margin:0">Ishita Rao</h1>
    </header>
    <main class="ml-app__body app-content--scroll ml-chat" style="padding:var(--space-3) var(--space-4)">
      <div class="ml-chat__day">Today</div>
      ${MSGS.map(([dir, t, when]) => `<div class="ml-bubble ml-bubble--${dir}">
        ${t}<span class="ml-bubble__meta">${when}</span>
      </div>`).join("\n      ")}

      <div class="ml-bubble ml-bubble--out" data-state="sending" style="opacity:var(--opacity-3)">
        See you Tuesday.<span class="ml-bubble__meta">Sending…</span>
      </div>

      <div class="ml-bubble ml-bubble--out ml-bubble--failed" data-state="error">
        See you Tuesday.
        <span class="ml-bubble__error">Not delivered — tap to retry</span>
      </div>
    </main>
    <footer class="ml-composer">
      <textarea rows="1" aria-label="Message" placeholder="Message Ishita…"></textarea>
      <button class="ml-icon-btn ml-icon-btn--primary" type="button" aria-label="Send">${icon("send", { size: 18 })}</button>
    </footer>
  </div>`,
});

finish("communication: ");
