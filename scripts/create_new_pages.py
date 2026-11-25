#!/usr/bin/env python3
"""
Create Dedicated WordPress Pages for MusicLinkd
-----------------------------------------------
Creates 4 dedicated Gutenberg pages to replace monolithic home page sections:
1. /features/      - Platform & Core Engines
2. /how-it-works/  - Interactive User Journeys (Talent, Studios, Clients)
3. /faq/           - Comprehensive Categorized Platform FAQ
4. /safety/        - Trust, Safety, Verification & Creator Rights Manifesto
"""

import os
import json
import base64
import urllib.request
import urllib.error

WP_BASE_URL = os.environ.get("WP_BASE_URL", "https://musiclinkd.com")
INGEST_USER = os.environ.get("WP_INGEST_USER", "ingest")
INGEST_PASS = os.environ.get("WP_INGEST_PASS", "s7jE IM7W dPDg Q9H3 MW5y KtXR")

PAGES = [
    {
        "slug": "features",
        "title": "Platform Features",
        "excerpt": "Explore the complete MusicLinkd operating system: verified artist portfolios, stems player, direct booking calendar, collab board, and studio resource management.",
        "content": """<!-- wp:group {"tagName":"main","className":"legal-body","layout":{"type":"constrained","contentSize":"1000px"}} -->
<main class="wp-block-group legal-body" style="padding-top:calc(var(--nav-h) + 40px);padding-bottom:96px">

<!-- wp:group {"className":"page-hero","layout":{"type":"constrained"}} -->
<div class="wp-block-group page-hero" style="text-align:center;max-width:760px;margin:0 auto 56px auto">
<!-- wp:paragraph --><p style="color:#EA4C89;font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.1em;margin-bottom:12px">The Creative Operating System</p><!-- /wp:paragraph -->
<!-- wp:heading {"level":1,"style":{"typography":{"fontSize":"clamp(32px, 4vw, 48px)","fontWeight":"800","lineHeight":"1.15"}}} -->
<h1 style="font-size:clamp(32px, 4vw, 48px);font-weight:800;line-height:1.15;letter-spacing:-0.03em;color:#141414;margin-bottom:16px">Engineered for the modern music economy.</h1>
<!-- /wp:heading -->
<!-- wp:paragraph {"style":{"typography":{"fontSize":"18px","lineHeight":"1.6"}}} -->
<p style="font-size:18px;lineHeight:1.6;color:#6B7280;margin:0 auto">Everything artists, session players, audio engineers, and studios need to showcase work, agree on terms, and book without friction.</p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:group -->

<!-- wp:columns {"style":{"spacing":{"blockGap":"24px"}}} -->
<div class="wp-block-columns" style="gap:24px;margin-bottom:32px">
<!-- wp:column {"style":{"spacing":{"padding":{"top":"32px","right":"32px","bottom":"32px","left":"32px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-column has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:32px">
<span style="font-family:var(--font-mono);font-size:12px;font-weight:700;color:#EA4C89;letter-spacing:0.1em;display:block;margin-bottom:12px">MODULE 01</span>
<!-- wp:heading {"level":3,"style":{"typography":{"fontSize":"22px","fontWeight":"700"}}} -->
<h3 style="font-size:22px;font-weight:700;color:#141414;margin-bottom:10px">Dynamic Creative Portfolio & Stems</h3>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;line-height:1.6;font-size:15px">Replace messy Google Drive and Dropbox links with a clean, high-fidelity portfolio. Upload audio stems, link official Spotify and Apple Music discographies, and package your rates transparently.</p>
<!-- /wp:paragraph -->
<!-- wp:list {"style":{"typography":{"fontSize":"14px"}}} -->
<ul style="color:#374151;line-height:1.8;padding-left:20px;font-size:14px">
<li>Lossless audio player with waveform stem scrubbing</li>
<li>Verified release credits pulled directly from Spotify</li>
<li>Custom service menus (Hourly, Day Rate, Milestone, Royalty Split)</li>
</ul>
<!-- /wp:list -->
</div>
<!-- /wp:column -->

<!-- wp:column {"style":{"spacing":{"padding":{"top":"32px","right":"32px","bottom":"32px","left":"32px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-column has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:32px">
<span style="font-family:var(--font-mono);font-size:12px;font-weight:700;color:#EA4C89;letter-spacing:0.1em;display:block;margin-bottom:12px">MODULE 02</span>
<!-- wp:heading {"level":3,"style":{"typography":{"fontSize":"22px","fontWeight":"700"}}} -->
<h3 style="font-size:22px;font-weight:700;color:#141414;margin-bottom:10px">Direct Booking & Scheduling Desk</h3>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;line-height:1.6;font-size:15px">No endless back-and-forth WhatsApp negotiations. Clients view real-time availability, select services, and request dates directly on your calendar with clear deposit terms.</p>
<!-- /wp:paragraph -->
<!-- wp:list {"style":{"typography":{"fontSize":"14px"}}} -->
<ul style="color:#374151;line-height:1.8;padding-left:20px;font-size:14px">
<li>Real-time calendar slot reservations and buffer rules</li>
<li>Zero commission fees taken on creator booking earnings</li>
<li>Universal webcal sync for Apple Calendar, Google & Outlook</li>
</ul>
<!-- /wp:list -->
</div>
<!-- /wp:column -->
</div>
<!-- /wp:columns -->

<!-- wp:columns {"style":{"spacing":{"blockGap":"24px"}}} -->
<div class="wp-block-columns" style="gap:24px;margin-bottom:32px">
<!-- wp:column {"style":{"spacing":{"padding":{"top":"32px","right":"32px","bottom":"32px","left":"32px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-column has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:32px">
<span style="font-family:var(--font-mono);font-size:12px;font-weight:700;color:#EA4C89;letter-spacing:0.1em;display:block;margin-bottom:12px">MODULE 03</span>
<!-- wp:heading {"level":3,"style":{"typography":{"fontSize":"22px","fontWeight":"700"}}} -->
<h3 style="font-size:22px;font-weight:700;color:#141414;margin-bottom:10px">Collaborations & Casting Board</h3>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;line-height:1.6;font-size:15px">Discover open gig opportunities, session calls, songwriting camps, and music video castings posted by verified artists, producers, and labels across India.</p>
<!-- /wp:paragraph -->
<!-- wp:list {"style":{"typography":{"fontSize":"14px"}}} -->
<ul style="color:#374151;line-height:1.8;padding-left:20px;font-size:14px">
<li>Filtered by 37 musical disciplines, genres, and cities</li>
<li>Built-in split sheet agreements and project brief handoffs</li>
<li>Private audition stem submissions directly to casting owners</li>
</ul>
<!-- /wp:list -->
</div>
<!-- /wp:column -->

<!-- wp:column {"style":{"spacing":{"padding":{"top":"32px","right":"32px","bottom":"32px","left":"32px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-column has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:32px">
<span style="font-family:var(--font-mono);font-size:12px;font-weight:700;color:#EA4C89;letter-spacing:0.1em;display:block;margin-bottom:12px">MODULE 04</span>
<!-- wp:heading {"level":3,"style":{"typography":{"fontSize":"22px","fontWeight":"700"}}} -->
<h3 style="font-size:22px;font-weight:700;color:#141414;margin-bottom:10px">Studio Hub & Facilities Management</h3>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;line-height:1.6;font-size:15px">Commercial recording studios, rehearsal pads, and mastering rooms can list specific live rooms, analog consoles, mic lockers, and engineer add-ons with automated scheduling.</p>
<!-- /wp:paragraph -->
<!-- wp:list {"style":{"typography":{"fontSize":"14px"}}} -->
<ul style="color:#374151;line-height:1.8;padding-left:20px;font-size:14px">
<li>Room-specific hourly and shift booking presets</li>
<li>Verified hardware gear inventory and acoustics badge</li>
<li>Assistant engineer scheduling and stem export tracking</li>
</ul>
<!-- /wp:list -->
</div>
<!-- /wp:column -->
</div>
<!-- /wp:columns -->

<!-- wp:columns {"style":{"spacing":{"blockGap":"24px"}}} -->
<div class="wp-block-columns" style="gap:24px;margin-bottom:48px">
<!-- wp:column {"style":{"spacing":{"padding":{"top":"32px","right":"32px","bottom":"32px","left":"32px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-column has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:32px">
<span style="font-family:var(--font-mono);font-size:12px;font-weight:700;color:#EA4C89;letter-spacing:0.1em;display:block;margin-bottom:12px">MODULE 05</span>
<!-- wp:heading {"level":3,"style":{"typography":{"fontSize":"22px","fontWeight":"700"}}} -->
<h3 style="font-size:22px;font-weight:700;color:#141414;margin-bottom:10px">QR Identity & Digital Pass</h3>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;line-height:1.6;font-size:15px">Network effortlessly at gigs, jam rooms, and industry conferences. Every verified creator gets a persistent smart QR resolver that loads their audio portfolio instantly on any mobile phone.</p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:column -->

<!-- wp:column {"style":{"spacing":{"padding":{"top":"32px","right":"32px","bottom":"32px","left":"32px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-column has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:32px">
<span style="font-family:var(--font-mono);font-size:12px;font-weight:700;color:#EA4C89;letter-spacing:0.1em;display:block;margin-bottom:12px">MODULE 06</span>
<!-- wp:heading {"level":3,"style":{"typography":{"fontSize":"22px","fontWeight":"700"}}} -->
<h3 style="font-size:22px;font-weight:700;color:#141414;margin-bottom:10px">Direct Messaging & File Exchange</h3>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;line-height:1.6;font-size:15px">Structured communication tied to verified booking orders. Chat securely, exchange reference audio, track revisions, and approve project milestones in one unified thread.</p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:column -->
</div>
<!-- /wp:columns -->

<!-- wp:group {"style":{"spacing":{"padding":{"top":"48px","bottom":"48px","left":"32px","right":"32px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-group has-surface-background-color has-background" style="text-align:center;border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:48px 32px">
<!-- wp:heading {"level":2,"style":{"typography":{"fontSize":"28px","fontWeight":"800"}}} -->
<h2 style="font-size:28px;font-weight:800;color:#141414;margin-bottom:12px">Ready to build your professional presence?</h2>
<!-- /wp:heading -->
<!-- wp:paragraph {"style":{"typography":{"fontSize":"16px"}}} -->
<p style="color:#6B7280;max-width:540px;margin:0 auto 24px auto;font-size:16px">Join hundreds of verified artists, studios, and engineers already building their careers on MusicLinkd.</p>
<!-- /wp:paragraph -->
<!-- wp:buttons {"layout":{"type":"flex","justifyContent":"center"}} -->
<div class="wp-block-buttons" style="display:flex;justify-content:center;gap:12px">
<!-- wp:button {"style":{"border":{"radius":"9999px"}},"backgroundColor":"primary"} -->
<div class="wp-block-button"><a class="wp-block-button__link has-primary-background-color has-background" href="/#cta" style="border-radius:9999px;background-color:#EA4C89;color:#FFFFFF;font-weight:600;padding:12px 28px;text-decoration:none">Claim Your Profile</a></div>
<!-- /wp:button -->
<!-- wp:button {"style":{"border":{"radius":"9999px"}}} -->
<div class="wp-block-button is-style-outline"><a class="wp-block-button__link" href="/how-it-works/" style="border-radius:9999px;border:1px solid rgba(0,0,0,0.15);color:#141414;font-weight:600;padding:12px 28px;text-decoration:none">See How It Works &rarr;</a></div>
<!-- /wp:button -->
</div>
<!-- /wp:buttons -->
</div>
<!-- /wp:group -->

</main>
<!-- /wp:group -->"""
    },
    {
        "slug": "how-it-works",
        "title": "How It Works",
        "excerpt": "A transparent step-by-step walkthrough of how Talent, Studios, and Hiring Clients collaborate and book on MusicLinkd.",
        "content": """<!-- wp:group {"tagName":"main","className":"legal-body","layout":{"type":"constrained","contentSize":"960px"}} -->
<main class="wp-block-group legal-body" style="padding-top:calc(var(--nav-h) + 40px);padding-bottom:96px">

<!-- wp:group {"className":"page-hero","layout":{"type":"constrained"}} -->
<div class="wp-block-group page-hero" style="text-align:center;max-width:720px;margin:0 auto 56px auto">
<!-- wp:paragraph --><p style="color:#EA4C89;font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.1em;margin-bottom:12px">Simple & Transparent</p><!-- /wp:paragraph -->
<!-- wp:heading {"level":1,"style":{"typography":{"fontSize":"clamp(32px, 4vw, 48px)","fontWeight":"800","lineHeight":"1.15"}}} -->
<h1 style="font-size:clamp(32px, 4vw, 48px);font-weight:800;line-height:1.15;letter-spacing:-0.03em;color:#141414;margin-bottom:16px">How MusicLinkd Works</h1>
<!-- /wp:heading -->
<!-- wp:paragraph {"style":{"typography":{"fontSize":"18px","lineHeight":"1.6"}}} -->
<p style="font-size:18px;lineHeight:1.6;color:#6B7280;margin:0 auto">Whether you are offering vocal sessions, renting out a tracking room, or hiring an engineer, our structured workflows eliminate ambiguity.</p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:group -->

<!-- wp:group {"style":{"spacing":{"padding":{"top":"36px","bottom":"36px","left":"32px","right":"32px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-group has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:36px;margin-bottom:32px">
<span style="background:rgba(234,76,137,0.1);color:#EA4C89;padding:4px 12px;border-radius:9999px;font-size:12px;font-weight:700;display:inline-block;margin-bottom:16px">PATH A</span>
<!-- wp:heading {"level":2,"style":{"typography":{"fontSize":"24px","fontWeight":"800"}}} -->
<h2 style="font-size:24px;font-weight:800;color:#141414;margin-bottom:12px">For Artists, Session Musicians & Engineers</h2>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;font-size:15px;margin-bottom:24px">Turn your skills into booked sessions without giving away a percentage of your creative income.</p>
<!-- /wp:paragraph -->

<div style="display:flex;flex-direction:column;gap:20px">
  <div style="display:flex;gap:16px;align-items:flex-start">
    <div style="width:32px;height:32px;border-radius:50%;background:#EA4C89;color:#FFFFFF;display:grid;place-items:center;font-weight:700;font-size:14px;flex-shrink:0">1</div>
    <div>
      <strong style="color:#141414;font-size:16px;display:block;margin-bottom:4px">Claim your profile and link your discography</strong>
      <p style="color:#6B7280;font-size:14px;margin:0;line-height:1.6">Upload your stems, connect Spotify to showcase verified release credits, and display your verified ID badge.</p>
    </div>
  </div>
  <div style="display:flex;gap:16px;align-items:flex-start">
    <div style="width:32px;height:32px;border-radius:50%;background:#EA4C89;color:#FFFFFF;display:grid;place-items:center;font-weight:700;font-size:14px;flex-shrink:0">2</div>
    <div>
      <strong style="color:#141414;font-size:16px;display:block;margin-bottom:4px">Define your services & session rates</strong>
      <p style="color:#6B7280;font-size:14px;margin:0;line-height:1.6">Set transparent rates (e.g. ₹5,000/track or ₹1,200/hr), define deposit requirements, and configure your live booking calendar.</p>
    </div>
  </div>
  <div style="display:flex;gap:16px;align-items:flex-start">
    <div style="width:32px;height:32px;border-radius:50%;background:#EA4C89;color:#FFFFFF;display:grid;place-items:center;font-weight:700;font-size:14px;flex-shrink:0">3</div>
    <div>
      <strong style="color:#141414;font-size:16px;display:block;margin-bottom:4px">Accept bookings & keep 100% of your earnings</strong>
      <p style="color:#6B7280;font-size:14px;margin:0;line-height:1.6">Review booking inquiries, chat directly with clients, agree on deliverables, and get paid with zero platform commission deductions.</p>
    </div>
  </div>
</div>
</div>
<!-- /wp:group -->

<!-- wp:group {"style":{"spacing":{"padding":{"top":"36px","bottom":"36px","left":"32px","right":"32px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-group has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:36px;margin-bottom:32px">
<span style="background:rgba(234,76,137,0.1);color:#EA4C89;padding:4px 12px;border-radius:9999px;font-size:12px;font-weight:700;display:inline-block;margin-bottom:16px">PATH B</span>
<!-- wp:heading {"level":2,"style":{"typography":{"fontSize":"24px","fontWeight":"800"}}} -->
<h2 style="font-size:24px;font-weight:800;color:#141414;margin-bottom:12px">For Studios, Jam Pads & Rehearsal Spaces</h2>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;font-size:15px;margin-bottom:24px">Maximize your room utilization and eliminate empty studio downtime.</p>
<!-- /wp:paragraph -->

<div style="display:flex;flex-direction:column;gap:20px">
  <div style="display:flex;gap:16px;align-items:flex-start">
    <div style="width:32px;height:32px;border-radius:50%;background:#141414;color:#FFFFFF;display:grid;place-items:center;font-weight:700;font-size:14px;flex-shrink:0">1</div>
    <div>
      <strong style="color:#141414;font-size:16px;display:block;margin-bottom:4px">List your rooms, equipment & acoustic specs</strong>
      <p style="color:#6B7280;font-size:14px;margin:0;line-height:1.6">Showcase your tracking rooms, vocal booths, consoles, and microphone locker with high-resolution photos and technical specs.</p>
    </div>
  </div>
  <div style="display:flex;gap:16px;align-items:flex-start">
    <div style="width:32px;height:32px;border-radius:50%;background:#141414;color:#FFFFFF;display:grid;place-items:center;font-weight:700;font-size:14px;flex-shrink:0">2</div>
    <div>
      <strong style="color:#141414;font-size:16px;display:block;margin-bottom:4px">Sync your room availability</strong>
      <p style="color:#6B7280;font-size:14px;margin:0;line-height:1.6">Manage multiple rooms on a unified schedule. Block maintenance hours and set buffer times between sessions.</p>
    </div>
  </div>
  <div style="display:flex;gap:16px;align-items:flex-start">
    <div style="width:32px;height:32px;border-radius:50%;background:#141414;color:#FFFFFF;display:grid;place-items:center;font-weight:700;font-size:14px;flex-shrink:0">3</div>
    <div>
      <strong style="color:#141414;font-size:16px;display:block;margin-bottom:4px">Lock sessions with upfront deposits</strong>
      <p style="color:#6B7280;font-size:14px;margin:0;line-height:1.6">Reduce no-shows. Artists lock dates with verified booking requests and direct deposit agreements.</p>
    </div>
  </div>
</div>
</div>
<!-- /wp:group -->

<!-- wp:group {"style":{"spacing":{"padding":{"top":"36px","bottom":"36px","left":"32px","right":"32px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-group has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:36px;margin-bottom:48px">
<span style="background:rgba(234,76,137,0.1);color:#EA4C89;padding:4px 12px;border-radius:9999px;font-size:12px;font-weight:700;display:inline-block;margin-bottom:16px">PATH C</span>
<!-- wp:heading {"level":2,"style":{"typography":{"fontSize":"24px","fontWeight":"800"}}} -->
<h2 style="font-size:24px;font-weight:800;color:#141414;margin-bottom:12px">For Hiring Clients, Producers & Labels</h2>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;font-size:15px;margin-bottom:24px">Hire reliable, verified music talent with full confidence and clear expectations.</p>
<!-- /wp:paragraph -->

<div style="display:flex;flex-direction:column;gap:20px">
  <div style="display:flex;gap:16px;align-items:flex-start">
    <div style="width:32px;height:32px;border-radius:50%;background:#3B82F6;color:#FFFFFF;display:grid;place-items:center;font-weight:700;font-size:14px;flex-shrink:0">1</div>
    <div>
      <strong style="color:#141414;font-size:16px;display:block;margin-bottom:4px">Search by discipline, genre, or city</strong>
      <p style="color:#6B7280;font-size:14px;margin:0;line-height:1.6">Filter 140+ verified Indian musicians, mixing engineers, Foley artists, and session players by exact location and rate range.</p>
    </div>
  </div>
  <div style="display:flex;gap:16px;align-items:flex-start">
    <div style="width:32px;height:32px;border-radius:50%;background:#3B82F6;color:#FFFFFF;display:grid;place-items:center;font-weight:700;font-size:14px;flex-shrink:0">2</div>
    <div>
      <strong style="color:#141414;font-size:16px;display:block;margin-bottom:4px">Audit audio samples & verified reviews</strong>
      <p style="color:#6B7280;font-size:14px;margin:0;line-height:1.6">Listen to raw stems and master files. Read genuine double-blind reviews left exclusively by past confirmed booking clients.</p>
    </div>
  </div>
  <div style="display:flex;gap:16px;align-items:flex-start">
    <div style="width:32px;height:32px;border-radius:50%;background:#3B82F6;color:#FFFFFF;display:grid;place-items:center;font-weight:700;font-size:14px;flex-shrink:0">3</div>
    <div>
      <strong style="color:#141414;font-size:16px;display:block;margin-bottom:4px">Send booking requests with milestone agreements</strong>
      <p style="color:#6B7280;font-size:14px;margin:0;line-height:1.6">Lock dates, communicate directly in a dedicated thread, and release payments upon satisfactory completion of deliverables.</p>
    </div>
  </div>
</div>
</div>
<!-- /wp:group -->

</main>
<!-- /wp:group -->"""
    },
    {
        "slug": "faq",
        "title": "Frequently Asked Questions",
        "excerpt": "Get immediate answers to common questions about MusicLinkd profiles, verification, bookings, payments, and safety.",
        "content": """<!-- wp:group {"tagName":"main","className":"legal-body","layout":{"type":"constrained","contentSize":"900px"}} -->
<main class="wp-block-group legal-body" style="padding-top:calc(var(--nav-h) + 40px);padding-bottom:96px">

<!-- wp:group {"className":"page-hero","layout":{"type":"constrained"}} -->
<div class="wp-block-group page-hero" style="text-align:center;max-width:720px;margin:0 auto 56px auto">
<!-- wp:paragraph --><p style="color:#EA4C89;font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.1em;margin-bottom:12px">Support & Clarity</p><!-- /wp:paragraph -->
<!-- wp:heading {"level":1,"style":{"typography":{"fontSize":"clamp(32px, 4vw, 48px)","fontWeight":"800","lineHeight":"1.15"}}} -->
<h1 style="font-size:clamp(32px, 4vw, 48px);font-weight:800;line-height:1.15;letter-spacing:-0.03em;color:#141414;margin-bottom:16px">Frequently Asked Questions</h1>
<!-- /wp:heading -->
<!-- wp:paragraph {"style":{"typography":{"fontSize":"18px","lineHeight":"1.6"}}} -->
<p style="font-size:18px;lineHeight:1.6;color:#6B7280;margin:0 auto">Everything you need to know about joining, verifying your account, and booking on MusicLinkd.</p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:group -->

<!-- wp:heading {"level":2,"style":{"typography":{"fontSize":"22px","fontWeight":"700"}}} -->
<h2 style="font-size:22px;font-weight:700;color:#141414;margin-bottom:16px;border-bottom:2px solid #EA4C89;padding-bottom:8px;display:inline-block">1. Account & Verification</h2>
<!-- /wp:heading -->

<!-- wp:group {"style":{"spacing":{"padding":{"top":"20px","bottom":"20px","left":"24px","right":"24px"}},"border":{"radius":"12px"}},"backgroundColor":"surface"} -->
<div class="wp-block-group has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:12px;padding:20px;margin-bottom:16px">
<strong style="font-size:17px;color:#141414;display:block;margin-bottom:8px">Who can create an account on MusicLinkd?</strong>
<p style="color:#6B7280;font-size:15px;line-height:1.6;margin:0">MusicLinkd is open to any active music professional operating in India or working with the Indian music industry — including solo vocalists, session instrumentalists, mixing & mastering engineers, music producers, voiceover artists, commercial recording studios, and indie record labels.</p>
</div>
<!-- /wp:group -->

<!-- wp:group {"style":{"spacing":{"padding":{"top":"20px","bottom":"20px","left":"24px","right":"24px"}},"border":{"radius":"12px"}},"backgroundColor":"surface"} -->
<div class="wp-block-group has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:12px;padding:20px;margin-bottom:16px">
<strong style="font-size:17px;color:#141414;display:block;margin-bottom:8px">What does the Verified Creator badge signify?</strong>
<p style="color:#6B7280;font-size:15px;line-height:1.6;margin:0">The Verified badge confirms that the account holder's identity has been reviewed and authenticated by our Creator Desk. For talent, this includes matching official government ID and verifying linked Spotify/Apple Music release credits.</p>
</div>
<!-- /wp:group -->

<!-- wp:group {"style":{"spacing":{"padding":{"top":"20px","bottom":"20px","left":"24px","right":"24px"}},"border":{"radius":"12px"}},"backgroundColor":"surface"} -->
<div class="wp-block-group has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:12px;padding:20px;margin-bottom:36px">
<strong style="font-size:17px;color:#141414;display:block;margin-bottom:8px">Can I register both as an individual artist and as a studio?</strong>
<p style="color:#6B7280;font-size:15px;line-height:1.6;margin:0">Yes. You can manage a Talent User profile (for individual session work) and a Business User profile (for a studio, label, or venue) within the same account ecosystem, each with distinct rates, calendars, and reviews.</p>
</div>
<!-- /wp:group -->

<!-- wp:heading {"level":2,"style":{"typography":{"fontSize":"22px","fontWeight":"700"}}} -->
<h2 style="font-size:22px;font-weight:700;color:#141414;margin-bottom:16px;border-bottom:2px solid #EA4C89;padding-bottom:8px;display:inline-block">2. Bookings, Rates & Payments</h2>
<!-- /wp:heading -->

<!-- wp:group {"style":{"spacing":{"padding":{"top":"20px","bottom":"20px","left":"24px","right":"24px"}},"border":{"radius":"12px"}},"backgroundColor":"surface"} -->
<div class="wp-block-group has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:12px;padding:20px;margin-bottom:16px">
<strong style="font-size:17px;color:#141414;display:block;margin-bottom:8px">Does MusicLinkd take a cut of my session fees?</strong>
<p style="color:#6B7280;font-size:15px;line-height:1.6;margin:0"><strong>Zero percent.</strong> We charge zero commission on your peer-to-peer artist services or studio bookings. When you quote ₹10,000 for a mix or session, you keep the entire ₹10,000. Our platform operates on transparent membership tiers for advanced studio and marketing tools.</p>
</div>
<!-- /wp:group -->

<!-- wp:group {"style":{"spacing":{"padding":{"top":"20px","bottom":"20px","left":"24px","right":"24px"}},"border":{"radius":"12px"}},"backgroundColor":"surface"} -->
<div class="wp-block-group has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:12px;padding:20px;margin-bottom:16px">
<strong style="font-size:17px;color:#141414;display:block;margin-bottom:8px">How do booking deposits and cancellations work?</strong>
<p style="color:#6B7280;font-size:15px;line-height:1.6;margin:0">Creators specify their own cancellation policies (Flexible, Standard, or Strict). Booking deposits are held under milestone agreements and released once agreed deliverables or session dates are met.</p>
</div>
<!-- /wp:group -->

<!-- wp:group {"style":{"spacing":{"padding":{"top":"20px","bottom":"20px","left":"24px","right":"24px"}},"border":{"radius":"12px"}},"backgroundColor":"surface"} -->
<div class="wp-block-group has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:12px;padding:20px;margin-bottom:36px">
<strong style="font-size:17px;color:#141414;display:block;margin-bottom:8px">What happens if there is a dispute regarding audio stems or deliverables?</strong>
<p style="color:#6B7280;font-size:15px;line-height:1.6;margin:0">All booking communication and stem exchanges are logged within the booking order thread. Our Creator Mediation Desk steps in with audit trails to resolve disputes fairly based on the original project brief.</p>
</div>
<!-- /wp:group -->

<!-- wp:group {"style":{"spacing":{"padding":{"top":"36px","bottom":"36px","left":"32px","right":"32px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-group has-surface-background-color has-background" style="text-align:center;border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:36px">
<!-- wp:heading {"level":3,"style":{"typography":{"fontSize":"20px","fontWeight":"700"}}} -->
<h3 style="font-size:20px;font-weight:700;color:#141414;margin-bottom:8px">Have a question not answered here?</h3>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;font-size:15px;margin-bottom:20px">Our creator support desk is active 6 days a week to help artists and studios.</p>
<!-- /wp:paragraph -->
<!-- wp:buttons {"layout":{"type":"flex","justifyContent":"center"}} -->
<div class="wp-block-buttons" style="display:flex;justify-content:center">
<!-- wp:button {"style":{"border":{"radius":"9999px"}},"backgroundColor":"primary"} -->
<div class="wp-block-button"><a class="wp-block-button__link has-primary-background-color has-background" href="/contact/" style="border-radius:9999px;background-color:#EA4C89;color:#FFFFFF;font-weight:600;padding:10px 24px;text-decoration:none">Contact Creator Desk &rarr;</a></div>
<!-- /wp:button -->
</div>
<!-- /wp:buttons -->
</div>
<!-- /wp:group -->

</main>
<!-- /wp:group -->"""
    },
    {
        "slug": "safety",
        "title": "Trust, Safety & Verification",
        "excerpt": "Learn about our creator rights manifesto, ID verification standards, escrow booking protection, and community safety guidelines.",
        "content": """<!-- wp:group {"tagName":"main","className":"legal-body","layout":{"type":"constrained","contentSize":"900px"}} -->
<main class="wp-block-group legal-body" style="padding-top:calc(var(--nav-h) + 40px);padding-bottom:96px">

<!-- wp:group {"className":"page-hero","layout":{"type":"constrained"}} -->
<div class="wp-block-group page-hero" style="text-align:center;max-width:720px;margin:0 auto 56px auto">
<!-- wp:paragraph --><p style="color:#EA4C89;font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.1em;margin-bottom:12px">Our Promise to Creators</p><!-- /wp:paragraph -->
<!-- wp:heading {"level":1,"style":{"typography":{"fontSize":"clamp(32px, 4vw, 48px)","fontWeight":"800","lineHeight":"1.15"}}} -->
<h1 style="font-size:clamp(32px, 4vw, 48px);font-weight:800;line-height:1.15;letter-spacing:-0.03em;color:#141414;margin-bottom:16px">Built on Trust, Backed by Transparency</h1>
<!-- /wp:heading -->
<!-- wp:paragraph {"style":{"typography":{"fontSize":"18px","lineHeight":"1.6"}}} -->
<p style="font-size:18px;lineHeight:1.6;color:#6B7280;margin:0 auto">We designed MusicLinkd from the ground up to protect your creative rights, your audio assets, and your financial agreements.</p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:group -->

<!-- wp:group {"style":{"spacing":{"padding":{"top":"32px","bottom":"32px","left":"32px","right":"32px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-group has-surface-background-color has-background" style="border:1.5px solid #EA4C89;border-radius:16px;padding:32px;margin-bottom:32px;background:#FFF0F5">
<span style="font-size:12px;font-weight:700;color:#EA4C89;text-transform:uppercase;letter-spacing:0.1em;display:block;margin-bottom:8px">THE CREATOR GUARANTEE</span>
<!-- wp:heading {"level":2,"style":{"typography":{"fontSize":"24px","fontWeight":"800"}}} -->
<h2 style="font-size:24px;font-weight:800;color:#141414;margin-bottom:12px">Your Work Stays Yours. Always.</h2>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#374151;font-size:15px;line-height:1.7;margin:0">MusicLinkd claims <strong>zero intellectual property rights, publishing rights, or master ownership</strong> over any track, stem, arrangement, lyric, or composition uploaded to the platform. You retain 100% of your copyright. We are a software tool for your career, not a predatory middleman.</p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:group -->

<!-- wp:columns {"style":{"spacing":{"blockGap":"24px"}}} -->
<div class="wp-block-columns" style="gap:24px;margin-bottom:32px">
<!-- wp:column {"style":{"spacing":{"padding":{"top":"28px","right":"28px","bottom":"28px","left":"28px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-column has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:28px">
<!-- wp:heading {"level":3,"style":{"typography":{"fontSize":"20px","fontWeight":"700"}}} -->
<h3 style="font-size:20px;font-weight:700;color:#141414;margin-bottom:10px">Verified ID & Discography</h3>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;line-height:1.6;font-size:14px">Every verified profile undergoes manual confirmation of identity and official Spotify/Apple Music release credits to ensure clients are working with authentic, credited professionals.</p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:column -->

<!-- wp:column {"style":{"spacing":{"padding":{"top":"28px","right":"28px","bottom":"28px","left":"28px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-column has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:28px">
<!-- wp:heading {"level":3,"style":{"typography":{"fontSize":"20px","fontWeight":"700"}}} -->
<h3 style="font-size:20px;font-weight:700;color:#141414;margin-bottom:10px">Double-Blind Reviews</h3>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;line-height:1.6;font-size:14px">Reviews can only be left after a confirmed booking transaction has concluded. Both parties review concurrently, eliminating retaliatory ratings or fake spam testimonials.</p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:column -->
</div>
<!-- /wp:columns -->

<!-- wp:columns {"style":{"spacing":{"blockGap":"24px"}}} -->
<div class="wp-block-columns" style="gap:24px;margin-bottom:48px">
<!-- wp:column {"style":{"spacing":{"padding":{"top":"28px","right":"28px","bottom":"28px","left":"28px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-column has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:28px">
<!-- wp:heading {"level":3,"style":{"typography":{"fontSize":"20px","fontWeight":"700"}}} -->
<h3 style="font-size:20px;font-weight:700;color:#141414;margin-bottom:10px">Zero Spam Policy</h3>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;line-height:1.6;font-size:14px">In-app messaging requires legitimate booking inquiries or mutual collaboration interest. Cold outreach, unsolicited promo blasts, and abusive behavior result in instant suspension.</p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:column -->

<!-- wp:column {"style":{"spacing":{"padding":{"top":"28px","right":"28px","bottom":"28px","left":"28px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-column has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:28px">
<!-- wp:heading {"level":3,"style":{"typography":{"fontSize":"20px","fontWeight":"700"}}} -->
<h3 style="font-size:20px;font-weight:700;color:#141414;margin-bottom:10px">Privacy & Data Control</h3>
<!-- /wp:heading -->
<!-- wp:paragraph -->
<p style="color:#6B7280;line-height:1.6;font-size:14px">Your personal contact info, phone number, and private address remain strictly shielded. Clients only see your public artist name, verified city, and professional booking channels.</p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:column -->
</div>
<!-- /wp:columns -->

<!-- wp:group {"style":{"spacing":{"padding":{"top":"32px","bottom":"32px","left":"32px","right":"32px"}},"border":{"radius":"16px"}},"backgroundColor":"surface"} -->
<div class="wp-block-group has-surface-background-color has-background" style="border:1px solid rgba(0,0,0,0.08);border-radius:16px;padding:32px;display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:16px">
<div>
  <strong style="color:#141414;font-size:17px;display:block">Read our full Community Standards</strong>
  <p style="color:#6B7280;font-size:14px;margin:4px 0 0 0">Detailed policies on intellectual property, conduct, and safety.</p>
</div>
<a href="/community-guidelines/" class="btn btn--ghost" style="border:1px solid rgba(0,0,0,0.15);padding:10px 20px;border-radius:9999px;font-size:14px;font-weight:600;text-decoration:none;color:#141414">View Guidelines &rarr;</a>
</div>
<!-- /wp:group -->

</main>
<!-- /wp:group -->"""
    }
]

def create_or_update_page(page_def):
    slug = page_def["slug"]
    title = page_def["title"]
    
    # Check if page exists
    search_url = f"{WP_BASE_URL}/wp-json/wp/v2/pages?slug={slug}&status=any"
    req = urllib.request.Request(search_url, headers={"Accept": "application/json"})
    token = base64.b64encode(f"{INGEST_USER}:{INGEST_PASS}".encode()).decode()
    req.add_header("Authorization", f"Basic {token}")
    
    existing_id = None
    try:
        with urllib.request.urlopen(req) as r:
            data = json.loads(r.read().decode())
            if data:
                existing_id = data[0]["id"]
    except Exception as e:
        print(f"Error checking slug {slug}: {e}")
        return False
        
    payload = {
        "title": title,
        "slug": slug,
        "status": "publish",
        "content": page_def["content"],
        "excerpt": page_def["excerpt"],
    }
    
    data_bytes = json.dumps(payload).encode("utf-8")
    
    if existing_id:
        target_url = f"{WP_BASE_URL}/wp-json/wp/v2/pages/{existing_id}"
        print(f"Updating existing page '{slug}' (ID: {existing_id})...")
    else:
        target_url = f"{WP_BASE_URL}/wp-json/wp/v2/pages"
        print(f"Creating new page '{slug}'...")
        
    post_req = urllib.request.Request(
        target_url,
        data=data_bytes,
        headers={
            "Content-Type": "application/json",
            "Accept": "application/json",
            "Authorization": f"Basic {token}",
        },
        method="POST"
    )
    
    try:
        with urllib.request.urlopen(post_req) as resp:
            res = json.loads(resp.read().decode())
            print(f"✓ Page '{slug}' published successfully! ID: {res['id']} URL: {res['link']}")
            return True
    except urllib.error.HTTPError as he:
        body = he.read().decode()
        print(f"✗ Failed to create/update '{slug}': HTTP {he.code}: {body}")
        return False
    except Exception as e:
        print(f"✗ Error: {e}")
        return False

def main():
    print(f"Connecting to {WP_BASE_URL} as '{INGEST_USER}' to publish 4 dedicated pages...")
    success = True
    for p in PAGES:
        ok = create_or_update_page(p)
        if not ok:
            success = False
    if success:
        print("\nAll 4 dedicated pages published successfully!")
    else:
        print("\nSome pages failed to publish. Check errors above.")

if __name__ == "__main__":
    main()
