#!/usr/bin/env node
/**
 * Flow 10 — Person Profile. Real content for the canonical talent template,
 * which existed only as the raw "not designed yet" scaffold before this pass
 * despite carrying a full dataContract (Arijit Sen, singer/composer, Mumbai —
 * see screen.json). 18 of the 19 profile_management/public_profile screens
 * were on that same scaffold; this is the one the spec's Person Profile
 * targets, built first because every other flow links into it.
 *
 * ONE screen, not two: public and owner views are `data-state="default"` /
 * `data-state="owner"` on the same markup, per the spec's own instruction —
 * "It is the same profile with owner controls," not a different visual
 * profile. Sections are progressive-disclosure blocks on one scroll, not
 * ten tabs, per "Do not create ten visible tabs."
 *
 * Content is the screen's own seeded dataContract, not invented: singer,
 * Mumbai, Bollywood/Sufi/Acoustic, Hindi/Bengali/English, one released
 * project (Tum Hi Toh Unplugged, 4.2M plays), one five-star review from a
 * T-Series A&R credit.
 */
import { writeScreen, icon, assertIcons, finish } from "../write-screen.mjs";

const P = "/preview/flows/";
assertIcons(["arrow-back", "share", "check-circle-fill", "heart", "star", "ellipsis", "edit", "briefcase"]);

const backNav = `<header class="ml-navbar">
      <a class="ml-icon-btn ml-icon-btn--bare" href="${P}discover/discover_profile_detail/index.html" aria-label="Back">${icon("arrow-back", { size: 20 })}</a>
      <span class="ml-navbar__spacer"></span>
      <a class="ml-icon-btn ml-icon-btn--bare" href="#" aria-label="More"><span data-state="default">${icon("share", { size: 20 })}</span><span data-state="owner" hidden>${icon("ellipsis", { size: 20 })}</span></a>
    </header>`;

writeScreen({
  flow: "public_profile",
  screen: "public_profile_view_talent",
  title: "Public Profile — Talent template",
  body: `
  <div class="ml-app">
    ${backNav}
    <main class="ml-app__body app-content--scroll" style="padding-bottom:var(--space-4)">

      <!-- Hero — photo, name, primary role, location. One .ml-hero, not a
           separate cover + avatar stack: the seed data has one avatarUrl. -->
      <div class="ml-hero" style="aspect-ratio:1/1">
        <img src="https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800" alt="" />
        <div class="ml-hero__body">
          <h1 class="ml-hero__name">Arijit Sen ${icon("check-circle-fill", { size: 18 })}</h1>
          <p class="ml-hero__sub">Singer · Composer · Mumbai, MH</p>
          <!-- .ml-status is a filled pill for a surface with its own background,
               not text sitting directly on a media scrim — reused it correctly
               instead of overriding its background/color to fake a fit. A
               plain dot + text follows .ml-hero__stats's own pattern instead. -->
          <div class="ml-hero__stats"><span style="display:flex;align-items:center;gap:var(--space-1)"><span style="width:6px;height:6px;border-radius:50%;background:currentColor;flex:none"></span>Available for collaboration</span></div>
        </div>
      </div>

      <!-- Actions — public vs owner, same slot, swapped by data-state.
           Public: Connect + Message, the two the spec names for a musician.
           Owner: Edit Profile + View as Public + Analytics, replacing the
           public actions rather than sitting alongside them. -->
      <div style="padding:0 var(--space-3)">
        <div class="ml-action-row" data-state="default">
          <a class="ml-btn ml-btn--primary ml-btn--block" href="#">Connect</a>
          <a class="ml-btn ml-btn--secondary ml-btn--block" href="${P}communication/chat_conversation/index.html">Message</a>
        </div>
        <div class="ml-action-row" data-state="owner" hidden>
          <a class="ml-btn ml-btn--primary ml-btn--block" href="${P}profile_management/edit_identity/index.html">${icon("edit", { size: 16 })} Edit Profile</a>
          <a class="ml-btn ml-btn--secondary" href="${P}public_profile/profile_analytics/index.html" aria-label="Analytics">${icon("briefcase", { size: 16 })}</a>
        </div>
        <a class="ml-link" href="#" data-state="owner" hidden style="display:block;text-align:center;font-size:var(--type-1);margin-top:var(--space-2)">View as public</a>
      </div>

      <!-- Trust — verification + rating as numbers, not a badge wall. -->
      <div class="ml-trust" style="margin:var(--space-3) var(--space-3) 0">
        <div class="ml-trust__row">
          <span class="ml-trust__mark">${icon("check-circle-fill", { size: 14 })}</span>
          <div><div class="ml-trust__what">Verified Artist</div><div class="ml-trust__who">Identity confirmed on MusicLinkd</div></div>
        </div>
        <div class="ml-trust__stats">
          <div class="ml-testimonial__stat"><b>5.0</b><span>Rating</span></div>
          <div class="ml-testimonial__stat"><b>10+ yrs</b><span>Touring &amp; session work</span></div>
        </div>
      </div>

      <!-- About — short bio, progressive disclosure. -->
      <section style="padding:0 var(--space-3);margin-top:var(--space-4)">
        <h2 class="ml-type-3" style="margin:0 0 var(--space-2)">About</h2>
        <p class="ml-type-2" style="margin:0;color:var(--color-muted)">Playback &amp; classical singer known for Bollywood, Sufi, and acoustic reinterpretations. 10+ years touring and session work.</p>
      </section>

      <!-- Music identity — genres, languages, instruments as chips, per the
           spec's "compact visual chips/cards, not a generic skill-tag cloud". -->
      <section style="padding:0 var(--space-3);margin-top:var(--space-4)">
        <h2 class="ml-type-3" style="margin:0 0 var(--space-2)">Music identity</h2>
        <div class="ml-type-1" style="color:var(--color-muted);margin-bottom:var(--space-1)">Genres</div>
        <div class="ml-chip-wrap" style="margin-bottom:var(--space-3)">
          <span class="ml-chip">Bollywood / Filmi</span><span class="ml-chip">Sufi Devotional</span><span class="ml-chip">Acoustic / Unplugged</span>
        </div>
        <div class="ml-type-1" style="color:var(--color-muted);margin-bottom:var(--space-1)">Languages</div>
        <div class="ml-chip-wrap" style="margin-bottom:var(--space-3)">
          <span class="ml-chip">Hindi</span><span class="ml-chip">Bengali</span><span class="ml-chip">English</span>
        </div>
        <div class="ml-type-1" style="color:var(--color-muted);margin-bottom:var(--space-1)">Instruments</div>
        <div class="ml-chip-wrap"><span class="ml-chip">Acoustic Guitar</span></div>
      </section>

      <!-- Featured work — the released project from the seed data. Uses
           .ml-media-card, already built for exactly this: full-bleed
           artwork with the title on the scrim. -->
      <section style="margin-top:var(--space-4)">
        <div class="ml-section" style="padding:0 var(--space-3)"><h2>Featured work</h2></div>
        <div class="ml-rail" style="padding:0 var(--space-3)">
          <a class="ml-media-card" href="${P}portfolio/publish_project/index.html">
            <img src="https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=500" alt="" />
            <div class="ml-media-card__body">
              <div class="ml-media-card__title">Tum Hi Toh (Unplugged)</div>
              <div class="ml-media-card__sub">4.2M plays · Released Feb 2026</div>
            </div>
          </a>
        </div>
      </section>

      <!-- Reviews — the one seeded review, real reviewer context. -->
      <section style="padding:0 var(--space-3);margin-top:var(--space-4)">
        <h2 class="ml-type-3" style="margin:0 0 var(--space-2)">Reviews</h2>
        <div class="ml-card ml-card--padded">
          <div class="ml-review-card">
            <div class="ml-review-card__head">
              <span class="ml-review-card__stars" aria-hidden="true">${"★".repeat(5)}</span>
              <span class="ml-type-1" style="color:var(--color-muted)">T-Series A&amp;R Team</span>
            </div>
            <p class="ml-review-card__body" style="margin:var(--space-2) 0 0">"Exceptional range and control across genres."</p>
          </div>
        </div>
      </section>

    </main>
  </div>`,
});

finish("profile: ");
