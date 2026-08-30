#!/usr/bin/env node
/**
 * Flow 0 — Home (1 screen).
 *
 * Genuinely missing before this pass. 12 screens' bottom-nav "Home" item
 * pointed at discover/trending_recommended_sections as a stand-in; that link
 * is repointed here in a separate mechanical sweep (see build-home-links.mjs
 * companion note in the commit), not by hand-editing 12 files.
 *
 * IA is the brand owner's own wireframe, not a generic feature-card grid:
 * greeting -> activity counts -> Continue -> For you -> Recent activity ->
 * secondary actions. The old five feature cards (Professional Profile Active,
 * Collab Availability, Premium Studio Spaces, Friends Already on MusicLinkd,
 * Add Track/EP) are demoted to the secondary-actions row rather than deleted —
 * the functionality stays reachable, it just stops competing with "what
 * happened and what's next" for the same visual weight.
 *
 * data-state carries five states declared in screen.json: default, loading,
 * empty, offline, error. Only default is authored as real content; the other
 * four reuse system-level patterns (.ml-skeleton, .ml-empty, .ml-banner--offline)
 * rather than one-off screen markup, per the "recycle, don't add" instruction.
 */
import { writeScreen, icon, assertIcons, finish } from "../write-screen.mjs";

const P = "/preview/flows/";
assertIcons(["bell", "arrow-forward", "calendar-check", "people", "music-on", "search"]);

const tabbar = `<nav class="ml-tabbar" aria-label="Primary">
      <span class="ml-tabbar__item" aria-current="page">
        <span class="ml-tabbar__icon"><svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true" fill="currentColor" xmlns="http://www.w3.org/2000/svg"><path d="M12 0 1 10v14h8v-7a3 3 0 1 1 6 0v7h8V10z"/></svg></span>
        Home
      </span>
      <a class="ml-tabbar__item" href="${P}discover/discover_feed/index.html">
        <span class="ml-tabbar__icon"><svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true" fill="currentColor" xmlns="http://www.w3.org/2000/svg"><path d="M12.87 12.9a1.25 1.25 0 1 0-.89.37q.52-.01.89-.37m2.27 1.13A2 2 0 0 1 14 15.16l-8.38 3.22L8.84 10a2 2 0 0 1 1.13-1.13l8.61-3.45zm5.35-10.52A12 12 0 1 0 3.52 20.48 12 12 0 0 0 20.49 3.52z"/></svg></span>
        Discover
      </a>
      <a class="ml-tabbar__item" href="${P}communication/networking_feed/index.html">
        <span class="ml-tabbar__icon"><svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true" fill="currentColor" xmlns="http://www.w3.org/2000/svg"><path d="M12 0a11 11 0 0 0-8.52 17.95l-1.46 5.43a.5.5 0 0 0 .73.55l5.08-2.75A10.98 10.98 0 0 0 23 11 11 11 0 0 0 12 0"/></svg></span>
        Connections
      </a>
      <a class="ml-tabbar__item" href="${P}marketplace/marketplace_browse/index.html">
        <span class="ml-tabbar__icon"><svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true" fill="currentColor" xmlns="http://www.w3.org/2000/svg"><path d="M12.25.25a6 6 0 0 0-6 6V7H2.5L1.01 17.13A6 6 0 0 0 6.95 24h10.1A6 6 0 0 0 23 17.13L21.5 7h-3.25v-.75a6 6 0 0 0-6-6M9.75 7v-.75a2.5 2.5 0 1 1 5 0V7z"/></svg></span>
        Market<span class="ml-tabbar__beta">Beta</span>
      </a>
      <a class="ml-tabbar__item" href="${P}profile_management/profile_overview/index.html">
        <span class="ml-tabbar__icon"><svg viewBox="0 0 24 24" width="22" height="22" aria-hidden="true" fill="currentColor" xmlns="http://www.w3.org/2000/svg"><path d="M17.5 5.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0M2 22a10 10 0 1 1 20 0v2H2z"/></svg></span>
        Profile
      </a>
    </nav>`;

writeScreen({
  flow: "home",
  screen: "home_dashboard",
  scroll: true,
  body: `
  <div class="ml-app">

    <!-- ══ compact header — identity + notifications, kept small per the brief ══ -->
    <header class="ml-appbar" style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4)">
      <span class="ml-wordmark" style="font-size:var(--type-2)">MUSIC<em>LINKD</em></span>
      <a class="ml-icon-btn ml-icon-btn--bare" href="${P}notifications/notifications_center/index.html" aria-label="Notifications, 3 unread" style="position:relative">
        ${icon("bell", { size: 20 })}
        <span class="ml-badge ml-badge--accent ml-badge--overlay">3</span>
      </a>
    </header>

    <!-- ══ default state — the real dashboard ══ -->
    <main class="ml-app__body" data-state="default" style="display:flex;flex-direction:column;gap:var(--space-4);padding-bottom:var(--space-4)">

      <!-- Welcome -->
      <div style="padding:0 var(--space-4)">
        <h1 class="ml-type-3" style="margin:0">Good afternoon, Vedanshu</h1>
        <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-1) 0 0">Your MusicLinkd today</p>
      </div>

      <!-- Your activity — counts, not a promo card. No heading, so this is a
           plain div rather than .ml-section, which is a HEADING row and
           would be empty flex space with nothing to justify-content against. -->
      <div style="padding:0 var(--space-4)">
        <div class="ml-stat-grid">
          <div class="ml-stat"><div class="ml-kpi"><span class="ml-kpi__num">3</span></div><div class="ml-type-1" style="color:var(--color-muted)">New opportunities</div></div>
          <div class="ml-stat"><div class="ml-kpi"><span class="ml-kpi__num">2</span></div><div class="ml-type-1" style="color:var(--color-muted)">Connection requests</div></div>
          <div class="ml-stat"><div class="ml-kpi"><span class="ml-kpi__num">8</span><span class="ml-kpi__delta ml-kpi__delta--up">+8</span></div><div class="ml-type-1" style="color:var(--color-muted)">Profile views this week</div></div>
          <div class="ml-stat"><div class="ml-kpi"><span class="ml-kpi__num">1</span></div><div class="ml-type-1" style="color:var(--color-muted)">Unread message</div></div>
        </div>
      </div>

      <!-- Continue — the single unfinished task surfaced prominently.
           .ml-section is a HEADING ROW (title + optional "See all"), not a
           content wrapper — confirmed against its own CSS (display:flex,
           justify-content:space-between) and the catalogue's own "section
           header + rail" demo, which puts the heading and its content in
           two siblings, not one container. My first draft nested content
           inside .ml-section and got a heading-beside-card layout instead
           of heading-above-content; caught by looking at the render. -->
      <div style="padding:0 var(--space-4)">
        <div class="ml-section"><h2>Continue</h2></div>
        <a class="ml-card" href="${P}booking/booking_summary/index.html" style="display:block;text-decoration:none;color:inherit">
          <div class="ml-booking-card">
            <img class="ml-booking-card__thumb" src="/assets/img/6/0112aa48922759.58a5af725b594.png" alt="" />
            <div class="ml-booking-card__body">
              <div class="ml-booking-card__title">Octave Sound Labs</div>
              <div class="ml-booking-card__meta">Studio booking · Today · 4:00 PM</div>
              <div class="ml-type-1" style="color:var(--color-pink-pressed);font-weight:700;margin-top:var(--space-1);display:flex;align-items:center;gap:var(--space-1)">Continue ${icon("arrow-forward", { size: 14 })}</div>
            </div>
          </div>
        </a>
      </div>

      <!-- For you — personalised, two lightweight rails -->
      <div>
        <div class="ml-section" style="padding:0 var(--space-4)"><h2>For you</h2></div>
        <div class="ml-rail" style="padding:0 var(--space-4)">
          <a class="ml-artist-card" style="width:150px" href="${P}discover/people_talent/index.html">
            <div class="ml-artist-card__media"><img src="/assets/img/6/01e26955706919.598fd42d42f34.png" alt="" /></div>
            <div class="ml-artist-card__body">
              <div class="ml-artist-card__name">${icon("people", { size: 14 })} 4 artists to meet</div>
              <div class="ml-artist-card__role">Based on your genres</div>
            </div>
          </a>
          <a class="ml-studio-card" style="width:200px" href="${P}discover/studios_spaces/index.html">
            <div class="ml-studio-card__media"><img src="/assets/img/6/029447155900185.635d48d12326c.png" alt="" /></div>
            <div class="ml-studio-card__body">
              <div class="ml-studio-card__name">2 studios nearby</div>
              <div class="ml-studio-card__loc">Andheri West, Mumbai</div>
            </div>
          </a>
        </div>
      </div>

      <!-- Activity — real events, not promotional cards -->
      <div style="padding:0 var(--space-4)">
        <div class="ml-section"><h2>Activity</h2></div>
        <div>
          <div class="ml-notification ml-notification--unread">
            <img class="ml-avatar ml-avatar--sm" src="/assets/img/editorials/c148e6ee64619796d9ef250e1b9f22d9.jpg" alt="" />
            <div><b>Aditi</b> accepted your connection request</div>
          </div>
          <div class="ml-notification">
            <img class="ml-avatar ml-avatar--sm" src="/assets/img/editorials/0b7348ff4eeab75b4ebf24e5e05bfef6.jpg" alt="" />
            <div>New message from <b>Rohan</b></div>
          </div>
          <div class="ml-notification">
            <span class="ml-icon-btn ml-icon-btn--bare" aria-hidden="true" style="pointer-events:none">${icon("music-on", { size: 18 })}</span>
            <div>Your profile was viewed <b>8 times</b> this week</div>
          </div>
        </div>
      </div>

      <!-- Secondary actions — demoted from the old feature-card grid. Same
           functionality, correct weight: a status control, a recommendation,
           and a primary action that belongs inside Portfolio, not the home feed. -->
      <div style="padding:0 var(--space-4)">
        <div class="ml-section"><h2>More</h2></div>
        <div style="display:flex;flex-direction:column;gap:var(--space-2)">
          <div class="ml-card ml-card--padded" style="display:flex;align-items:center;justify-content:space-between;gap:var(--space-3)">
            <div>
              <div class="ml-type-2" style="font-weight:700">Available for collaboration</div>
              <div class="ml-type-1" style="color:var(--color-muted)">Visible on your public profile</div>
            </div>
            <button class="ml-switch" role="switch" aria-checked="true" aria-label="Collaboration availability"></button>
          </div>
          <a class="ml-card ml-card--padded" href="${P}discover/studios_spaces/index.html" style="display:flex;align-items:center;justify-content:space-between;text-decoration:none;color:inherit">
            <span class="ml-type-2">Premium studio spaces near you</span>
            ${icon("arrow-forward", { size: 16 })}
          </a>
          <a class="ml-card ml-card--padded" href="${P}portfolio/portfolio_dashboard/index.html" style="display:flex;align-items:center;justify-content:space-between;text-decoration:none;color:inherit">
            <span class="ml-type-2">Add a track or EP to your portfolio</span>
            ${icon("arrow-forward", { size: 16 })}
          </a>
        </div>
      </div>

    </main>

    <!-- ══ loading — recycled skeleton, not a bespoke spinner ══ -->
    <main class="ml-app__body" data-state="loading" hidden style="padding:var(--space-4);display:flex;flex-direction:column;gap:var(--space-3)">
      <div class="ml-skeleton" style="height:28px;width:60%;border-radius:var(--radius-1)"></div>
      <div class="ml-skeleton" style="height:88px;border-radius:var(--radius-2)"></div>
      <div class="ml-skeleton" style="height:120px;border-radius:var(--radius-2)"></div>
    </main>

    <!-- ══ empty — first session, nothing has happened yet ══ -->
    <main class="ml-app__body" data-state="empty" hidden>
      <div class="ml-empty">
        <h3 class="ml-empty__title">Let's build your MusicLinkd</h3>
        <p class="ml-empty__body">Nothing to show yet — start by exploring people, studios and opportunities near you.</p>
        <a class="ml-btn ml-btn--primary" href="${P}discover/discover_feed/index.html">Explore MusicLinkd</a>
      </div>
    </main>

    <!-- ══ offline — recycled banner, cached content stays visible above it ══ -->
    <main class="ml-app__body" data-state="offline" hidden style="padding:var(--space-4)">
      <div class="ml-banner ml-banner--offline">You're offline — showing your last synced activity.</div>
    </main>

    <!-- ══ error ══ -->
    <main class="ml-app__body" data-state="error" hidden>
      <div class="ml-error">
        <span class="ml-error__mark">${icon("bell", { size: 24 })}</span>
        <h4 class="ml-error__title">Couldn't load your home</h4>
        <p class="ml-error__body">Your data is safe — try again in a moment.</p>
        <button class="ml-btn ml-btn--primary">Retry</button>
      </div>
    </main>

    ${tabbar}
  </div>`,
});

finish("home: ");
