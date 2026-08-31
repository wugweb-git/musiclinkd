#!/usr/bin/env node
/**
 * Flow 16 — Verification, against the deferred-flows brief. BETA.
 *
 * 17 numbered sections, 2 screens. The brief's VERIFY-09 through VERIFY-14 —
 * submitted, pending, verified, rejected, resubmit, expired — are six sections
 * describing ONE question: where is my verification up to? So they are states
 * of verification_status. The three levels (basic / professional / business)
 * differ only in what evidence they ask for, so they are states of the
 * submission screen rather than three screens.
 *
 * The brief's central rule shapes every line of copy here:
 *
 *   "Verification should increase trust. It must NOT become a vanity badge."
 *
 * So the screen says what was CHECKED, never that the person is endorsed or
 * recommended; a rejection gives a reason and a way back in without starting
 * over; and the privacy section states plainly that documents are never shown
 * publicly — only the status is.
 *
 * No new CSS: .ml-timeline, .ml-dropzone, .ml-choice, .ml-summary, .ml-status
 * and .ml-empty already exist.
 */
import { writeScreen, backArrow, icon, assertIcons, beta, finish, media } from "../write-screen.mjs";

const P = "/preview/flows/verifications/";
assertIcons(["arrow-forward", "check", "cancel"]);

const bar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-2" style="margin:0;flex:1">${title}</h1>
      ${beta}
    </header>`;

const rowLink = (label, href, meta) => `<a class="ml-action-row" href="${href}">
        <span class="ml-choice__body">
          <span class="ml-choice__title">${label}</span>
          ${meta ? `<span class="ml-choice__meta">${meta}</span>` : ""}
        </span>
        ${icon("arrow-forward", { size: 18 })}
      </a>`;

/* ── verification_status — the whole lifecycle as five states ───────────── */
/* What each level actually checks. Stated as a fact about the CHECK, never as
   a claim about the person — that is the difference between trust and a
   vanity badge, and the brief is explicit about it. */
const LEVELS = [
  ["Basic", "That you are a real person, using one government ID.",
   "A Basic badge on your profile", "One government ID"],
  ["Professional", "Basic, plus that the work on your profile is genuinely yours.",
   "A Professional badge, and priority in Discover", "One government ID, and two pieces of released work you are credited on"],
  ["Business", "That the business exists and that you are authorised to act for it.",
   "A Business badge on the business profile", "GST certificate and one document showing your authority"],
];

writeScreen({
  flow: "verifications", screen: "verification_status", title: "Verification",
  body: `
  <div class="ml-app">
    ${bar("Verification", "/preview/flows/profile_management/profile_overview/index.html")}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="not_started">
        <div class="ml-person-row">
          <img src="${media("faces", 2)}" alt="" loading="lazy" />
          <span class="meta"><b>Ishita Rao</b><p>Not verified</p></span>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-3) 0">
          Verification tells people that we have checked something specific about you.
          It is not a recommendation, and it does not affect how good anyone thinks your work is.
        </p>

        <div class="ml-section"><h2>What you can verify</h2></div>
        ${LEVELS.map(([name, what, gives, needs]) => `<div class="ml-choice">
          <span class="ml-choice__body">
            <span class="ml-choice__title">${name}</span>
            <span class="ml-choice__meta">Checks: ${what}</span>
            <span class="ml-choice__meta">You'll need: ${needs}</span>
            <span class="ml-choice__meta">You get: ${gives}</span>
          </span>
        </div>`).join("\n        ")}

        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="${P}verification_screen/index.html">Get verified</a>
        ${rowLink("How verification works", "?state=help", "What we check, and what we do with your documents")}
      </div>

      <div data-state="pending">
        <span class="ml-status ml-status--warning">Under review</span>
        <h2 class="ml-type-3" style="margin:var(--space-2) 0 var(--space-1)">We're checking your documents</h2>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0">
          Submitted 29 August. Most checks finish within two working days, but we don't promise a time —
          if it needs a second look it takes longer.
        </p>
        <ol class="ml-timeline" style="margin-top:var(--space-3)">
          <li class="ml-timeline__item is-done"><span class="ml-timeline__title">Documents received</span>
            <span class="ml-timeline__meta">29 Aug, 14:02</span></li>
          <li class="ml-timeline__item is-current"><span class="ml-timeline__title">Under review</span></li>
          <li class="ml-timeline__item"><span class="ml-timeline__title">Decision</span>
            <span class="ml-timeline__meta">You'll be notified</span></li>
        </ol>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-3)">
          You can keep using MusicLinkd normally while this is happening.
        </p>
      </div>

      <div data-state="verified">
        <div class="ml-person-row">
          <img src="${media("faces", 2)}" alt="" loading="lazy" />
          <span class="meta"><b>Ishita Rao</b><p>Verified · Professional</p></span>
          <span class="ml-status ml-status--success">Verified</span>
        </div>
        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>Level</span><span>Professional</span></div>
          <div class="ml-summary__row"><span>Verified on</span><span>14 August 2026</span></div>
          <div class="ml-summary__row"><span>We checked</span><span>Identity and credited work</span></div>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          Your badge appears on your profile, in search, in Discover, on listings and beside your reviews.
          It means we checked your identity and your credits — nothing more than that.
        </p>
        ${rowLink("How verification works", "?state=help")}
      </div>

      <!-- Rejection: a reason where policy allows, and a way back in that does
           not make you start from nothing. -->
      <div data-state="rejected">
        <span class="ml-status ml-status--error">Not completed</span>
        <h2 class="ml-type-3" style="margin:var(--space-2) 0 var(--space-1)">We couldn't verify you this time</h2>
        <div class="ml-banner ml-banner--error" style="margin-top:var(--space-2)">
          <span class="ml-banner__body">The name on your ID didn't match the name on your profile.</span>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          This is usually a small mismatch — a middle name, or a stage name on the profile.
          Correcting one of the two is normally enough. Your other documents are still on file.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="${P}verification_screen/index.html?state=govt_id_upload">Send a different ID</a>
        <a class="ml-btn ml-btn--secondary ml-btn--block" style="margin-top:var(--space-2)" href="/preview/flows/profile_management/edit_identity/index.html">Change the name on my profile</a>
      </div>

      <div data-state="expired">
        <span class="ml-status ml-status--warning">Expired</span>
        <h2 class="ml-type-3" style="margin:var(--space-2) 0 var(--space-1)">Your verification has expired</h2>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0">
          Business verification lasts a year, because company details change. Your badge is hidden until you renew.
          Nothing else about your account is affected.
        </p>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="${P}verification_screen/index.html?state=level_business">Renew verification</a>
      </div>

      <div data-state="help">
        <div class="ml-section"><h2>Why verify?</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          People book strangers on this platform. A badge tells them we checked something concrete —
          not that we recommend you, and not that your work is good.
        </p>
        <div class="ml-section"><h2>What happens to my documents?</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          They are encrypted, seen only by the person reviewing them, and deleted once the check is done.
          They are never shown on your profile. Only the status — verified or not — is public.
        </p>
        <div class="ml-section"><h2>How long does it take?</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Usually two working days. We won't promise faster than we can deliver.
        </p>
        <div class="ml-section"><h2>What if I'm rejected?</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0">
          You get a reason, and you fix only the thing that was wrong — you don't start again.
          There's no limit on how many times you can try.
        </p>
      </div>
    </main>
  </div>`,
});

/* ── verification_screen — submission, the three levels as states ───────── */
writeScreen({
  flow: "verifications", screen: "verification_screen", title: "Verification — Submit",
  body: `
  <div class="ml-app">
    ${bar("Get verified", `${P}verification_status/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="id_type_selector">
        <div class="ml-section"><h2>What are you verifying?</h2></div>
        <div role="radiogroup" aria-label="Verification level">
          ${LEVELS.map(([name, what, , needs], i) => `<a class="ml-choice" role="radio" aria-checked="${i === 1}" href="?state=level_${name.toLowerCase()}">
            <span class="ml-choice__body">
              <span class="ml-choice__title">${name}</span>
              <span class="ml-choice__meta">${what}</span>
              <span class="ml-choice__meta">Needs: ${needs}</span>
            </span>
            <span class="ml-choice__mark"></span>
          </a>`).join("\n          ")}
        </div>
      </div>

      <div data-state="level_basic">
        <div class="ml-section"><h2>Basic — one document</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          One government ID with your name and photograph. We check the name against your profile.
        </p>
        ${rowLink("Continue to upload", "?state=govt_id_upload")}
      </div>

      <div data-state="level_professional">
        <div class="ml-section"><h2>Professional — ID and two credits</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          One government ID, plus two pieces of released work you're credited on. A link to each is enough —
          a streaming page, a label page, or a release you've already added to your portfolio.
        </p>
        <div class="ml-field">
          <label for="v-c1">First credit (link)</label>
          <input id="v-c1" type="url" placeholder="https://open.spotify.com/track/…" />
        </div>
        <div class="ml-field">
          <label for="v-c2">Second credit (link)</label>
          <input id="v-c2" type="url" placeholder="https://…" />
        </div>
        ${rowLink("Continue to upload", "?state=govt_id_upload")}
      </div>

      <div data-state="level_business">
        <div class="ml-section"><h2>Business — company documents</h2></div>
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          We check that the business exists and that you can act for it. We do not ask for bank details,
          and we never will as part of verification.
        </p>
        ${rowLink("Continue", "?state=gst_upload")}
      </div>

      <div data-state="govt_id_upload">
        <div class="ml-section"><h2>Your ID</h2></div>
        <div class="ml-dropzone">
          <span class="ml-dropzone__hint">Photograph or scan, both sides</span>
          <span class="ml-dropzone__formats">JPG, PNG or PDF · up to 10MB</span>
        </div>
        <div class="ml-field" style="margin-top:var(--space-3)">
          <label for="v-type">Document type</label>
          <select id="v-type"><option>Aadhaar</option><option>Passport</option><option>Driving licence</option><option>Voter ID</option></select>
        </div>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)">
          Cover any number you don't want us to see. We only need the name and photograph.
        </p>
        ${rowLink("Continue", "?state=review")}
      </div>

      <div data-state="gst_upload">
        <div class="ml-section"><h2>Business documents</h2></div>
        <div class="ml-field">
          <label for="v-gst">GST number</label>
          <input id="v-gst" type="text" placeholder="27ABCDE1234F1Z5" />
        </div>
        <div class="ml-dropzone" style="margin-top:var(--space-2)">
          <span class="ml-dropzone__hint">GST certificate</span>
          <span class="ml-dropzone__formats">PDF · up to 10MB</span>
        </div>
        <div class="ml-dropzone" style="margin-top:var(--space-2)">
          <span class="ml-dropzone__hint">Proof you can act for the business</span>
          <span class="ml-dropzone__formats">Letterhead, board resolution or incorporation document</span>
        </div>
        ${rowLink("Continue", "?state=review")}
      </div>

      <div data-state="review">
        <div class="ml-section"><h2>Before you send it</h2></div>
        <div class="ml-summary">
          <div class="ml-summary__row"><span>Level</span><span>Professional</span></div>
          <div class="ml-summary__row"><span>ID</span><span>Aadhaar · 2 files</span></div>
          <div class="ml-summary__row"><span>Credits</span><span>2 links</span></div>
          <div class="ml-summary__row"><span>Name on profile</span><span>Ishita Rao</span></div>
        </div>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-3)">
          Check the name on your profile matches the name on your ID — a mismatch is the single
          most common reason a check fails.
        </p>
        ${rowLink("Consent and submit", "?state=consent")}
      </div>

      <div data-state="consent">
        <div class="ml-section"><h2>Your consent</h2></div>
        <label class="ml-check-row">
          <span class="ml-check-ctl" role="checkbox" aria-checked="true"></span>
          <span class="ml-choice__body"><span class="ml-choice__title">
            I agree to MusicLinkd checking these documents to verify my identity.
          </span></span>
        </label>
        <label class="ml-check-row">
          <span class="ml-check-ctl" role="checkbox" aria-checked="true"></span>
          <span class="ml-choice__body"><span class="ml-choice__title">
            I understand my documents are deleted once the check is complete, and are never shown publicly.
          </span></span>
        </label>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=badge_timeline">Submit for review</a>
      </div>

      <div data-state="badge_timeline">
        <div class="ml-empty">
          <h2 class="ml-empty__title">Sent for review</h2>
          <p class="ml-empty__body">
            We'll notify you when it's decided. Most checks finish within two working days.
          </p>
          <a class="ml-btn ml-btn--primary" href="${P}verification_status/index.html?state=pending">See status</a>
        </div>
      </div>
    </main>
  </div>`,
});

finish("verifications: ");
