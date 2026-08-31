#!/usr/bin/env node
/**
 * Flow 1 — User Auth (7 screens, registry numbers 1-7).
 *
 * Rebuilt on the ml-* system: no per-screen <style>, no inline colour, every
 * control at or above the 48dp touch minimum. Content and states preserved
 * from the previous hand-built screens and from each screen.json contract.
 *
 * Flow status is "partial" per flows/auth/_flow.json — OTP verification is
 * fake and Google Sign-In falls back to a hardcoded profile. That is a backend
 * truth and is deliberately NOT surfaced as UI here; these screens show the
 * intended design, and the honest status stays in _flow.json where the
 * workbench reads it.
 */
import { writeScreen, backArrow, checkMark, finish } from "../write-screen.mjs";

const W = "/preview/flows/auth/welcome/index.html";
const done = [];

/* 1 — Splash. Full-bleed ink; the one screen that is deliberately not paper. */
done.push(writeScreen({ flow: "auth", screen: "splash", body: `
  <div class="ml-app" style="background:var(--color-ink)">
    <main class="ml-app__body" style="display:flex;align-items:center;justify-content:center;text-align:center">
      <div>
        <h1 class="ml-wordmark ml-wordmark--on-media" style="font-size:var(--type-4);margin:0">MUSIC<em>LINKD</em></h1>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)" data-state="loading">Your professional music network</p>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-2)" data-state="resuming_local_session">Restoring your session…</p>
        <div class="ml-spinner" style="margin:var(--space-4) auto 0"></div>
      </div>
    </main>
  </div>` }));

/* 2 — Welcome. One primary action; everything else is secondary. */
done.push(writeScreen({ flow: "auth", screen: "welcome", body: `
  <div class="ml-app">
    <main class="ml-app__body" style="display:flex;flex-direction:column;justify-content:center;gap:var(--space-4)">
      <div style="text-align:center">
        <h1 class="ml-wordmark" style="font-size:var(--type-4);margin:0">MUSIC<em>LINKD</em></h1>
        <p class="ml-type-2" style="color:var(--color-muted);margin-top:var(--space-2)">Discover, collaborate, and get booked — India's network for musicians, studios, and bands.</p>
      </div>
      <div style="display:flex;flex-direction:column;gap:var(--space-2)">
        <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="/preview/flows/auth/email_auth/index.html?state=signup">Get started with email</a>
        <a class="ml-btn ml-btn--secondary ml-btn--block" href="/preview/flows/auth/mobile_auth/index.html">Continue with mobile</a>
        <div class="ml-divider ml-divider--labelled">or</div>
        <a class="ml-btn ml-btn--secondary ml-btn--block" href="/preview/flows/auth/google_signin/index.html">
          <svg width="18" height="18" viewBox="0 0 24 24" aria-hidden="true"><path fill="#4285F4" d="M23.52 12.27c0-.85-.08-1.67-.22-2.45H12v4.64h6.47c-.28 1.5-1.13 2.78-2.4 3.63v3.02h3.88c2.27-2.09 3.57-5.17 3.57-8.84z"/><path fill="#34A853" d="M12 24c3.24 0 5.96-1.07 7.95-2.9l-3.88-3.02c-1.08.72-2.45 1.15-4.07 1.15-3.13 0-5.78-2.12-6.73-4.96H1.27v3.12C3.25 21.3 7.31 24 12 24z"/><path fill="#FBBC05" d="M5.27 14.27A7.2 7.2 0 0 1 4.89 12c0-.79.14-1.56.38-2.27V6.61H1.27A12 12 0 0 0 0 12c0 1.94.46 3.77 1.27 5.39z"/><path fill="#EA4335" d="M12 4.77c1.77 0 3.35.61 4.6 1.8l3.44-3.44C17.96 1.19 15.24 0 12 0 7.31 0 3.25 2.7 1.27 6.61l4 3.12C6.22 6.89 8.87 4.77 12 4.77z"/></svg>
          Continue with Google
        </a>
      </div>
      <p class="ml-type-1" style="color:var(--color-muted);text-align:center">By continuing you agree to our Terms and Privacy Policy.</p>
    </main>
  </div>` }));

/* 3 — Email auth. states: signup / signin / loading / error */
done.push(writeScreen({ flow: "auth", screen: "email_auth", body: `
  <div class="ml-app">
    <header class="ml-navbar">${backArrow(W)}</header>
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      <div>
        <h1 class="ml-type-3" style="margin:0">Welcome back</h1>
        <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-1) 0 0">Sign in or create your MusicLinkd account.</p>
      </div>

      <div class="ml-segmented" role="tablist">
        <a class="ml-segmented__btn" role="tab" href="?state=signup" aria-selected="true"  data-state="signup loading error">Sign up</a>
        <a class="ml-segmented__btn" role="tab" href="?state=signup" aria-selected="false" data-state="signin">Sign up</a>
        <a class="ml-segmented__btn" role="tab" href="?state=signin" aria-selected="false" data-state="signup loading error">Sign in</a>
        <a class="ml-segmented__btn" role="tab" href="?state=signin" aria-selected="true"  data-state="signin">Sign in</a>
      </div>

      <!-- state: error -->
      <div class="ml-banner ml-banner--error" role="alert" hidden data-state="error">
        We couldn't verify those details. Check your email and password and try again.
      </div>

      <div class="ml-field">
        <label for="email">Email address</label>
        <input id="email" type="email" inputmode="email" autocomplete="email" placeholder="you@example.com" />
      </div>
      <div class="ml-field">
        <label for="password">Password</label>
        <input id="password" type="password" autocomplete="new-password" placeholder="At least 8 characters" />
        <span class="ml-type-1" style="color:var(--color-muted)" data-state="signup loading error">Use 8+ characters with a number.</span>
        <a class="ml-type-1" style="color:var(--color-pink-pressed);font-weight:600;text-decoration:none;display:inline-flex;align-items:center;min-height:var(--size-3);align-self:flex-start" href="?state=signin" data-state="signin">Forgot your password?</a>
      </div>
      <!-- Signing up sends a verification email, so that is where signup goes. -->
      <a class="ml-btn ml-btn--ghost ml-btn--block" data-state="signup" href="/preview/flows/auth/email_verification/index.html">Continue</a>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="/preview/flows/auth/registration_write/index.html" data-state="signup error">Create account</a>
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="/preview/flows/auth/registration_write/index.html" data-state="signin">Sign in</a>
      <span class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" aria-disabled="true" data-state="loading"><span class="ml-spinner" style="width:20px;height:20px;border-width:2px"></span></span>
    </footer>
  </div>` }));

/* 4 — Mobile number. states: default / error */
done.push(writeScreen({ flow: "auth", screen: "mobile_auth", body: `
  <div class="ml-app">
    <header class="ml-navbar">${backArrow(W)}</header>
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      <div>
        <h1 class="ml-type-3" style="margin:0">Enter your mobile number</h1>
        <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-1) 0 0">We'll send a 6-digit code to verify it's you.</p>
      </div>
      <div class="ml-field">
        <label for="phone">Mobile number</label>
        <div class="ml-phone">
          <span class="ml-phone__prefix">🇮🇳 +91</span>
          <input id="phone" type="tel" inputmode="numeric" autocomplete="tel-national" placeholder="98765 43210" />
        </div>
      </div>
      <div class="ml-banner ml-banner--info" data-state="default">We never share your number publicly.</div>
      <div class="ml-banner ml-banner--error" role="alert" data-state="error">
        That doesn't look like a valid Indian mobile number.
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="/preview/flows/auth/otp_verification/index.html">Send code</a>
    </footer>
  </div>` }));

/* 5 — OTP. states: entering / verifying / error / success */
done.push(writeScreen({ flow: "auth", screen: "otp_verification", body: `
  <div class="ml-app">
    <header class="ml-navbar">${backArrow("/preview/flows/auth/mobile_auth/index.html")}</header>
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3);text-align:center">
      <div>
        <h1 class="ml-type-3" style="margin:0">Enter verification code</h1>
        <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-1) 0 0">Sent to +91 98765 43210</p>
      </div>

      <div class="ml-otp" role="group" aria-label="6-digit verification code" data-state="entering verifying">
        <span class="ml-otp__box is-filled">4</span>
        <span class="ml-otp__box is-filled">8</span>
        <span class="ml-otp__box is-filled">2</span>
        <span class="ml-otp__box"></span>
        <span class="ml-otp__box"></span>
        <span class="ml-otp__box"></span>
      </div>
      <div class="ml-otp" role="group" aria-label="6-digit verification code" data-state="error">
        ${"4826".split("").map(d => `<span class="ml-otp__box is-invalid">${d}</span>`).join("")}
        <span class="ml-otp__box is-invalid">1</span>
        <span class="ml-otp__box is-invalid">9</span>
      </div>

      <div data-state="success">
        ${checkMark}
        <p class="ml-type-2" style="margin-top:var(--space-2)">Number verified</p>
      </div>

      <div class="ml-banner ml-banner--error" role="alert" data-state="error">
        That code isn't right. You have 2 attempts left.
      </div>

      <p class="ml-type-1" style="color:var(--color-muted)" data-state="entering error">Didn't get it? Resend in 0:24</p>
      <div class="ml-spinner" style="margin:0 auto" data-state="verifying"></div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="/preview/flows/auth/registration_write/index.html" data-state="entering error success">Verify</a>
      <span class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" aria-disabled="true" data-state="verifying">Verifying…</span>
    </footer>
  </div>` }));

/* 6 — Google sign-in. states: account_picker / success / fallback_fake */
done.push(writeScreen({ flow: "auth", screen: "google_signin", body: `
  <div class="ml-app">
    <header class="ml-navbar">${backArrow(W)}</header>
    <main class="ml-app__body" style="display:flex;flex-direction:column;gap:var(--space-3)">
      <div style="text-align:center">
        <svg width="30" height="30" viewBox="0 0 24 24" style="margin:0 auto" aria-hidden="true"><path fill="#4285F4" d="M23.52 12.27c0-.85-.08-1.67-.22-2.45H12v4.64h6.47c-.28 1.5-1.13 2.78-2.4 3.63v3.02h3.88c2.27-2.09 3.57-5.17 3.57-8.84z"/><path fill="#34A853" d="M12 24c3.24 0 5.96-1.07 7.95-2.9l-3.88-3.02c-1.08.72-2.45 1.15-4.07 1.15-3.13 0-5.78-2.12-6.73-4.96H1.27v3.12C3.25 21.3 7.31 24 12 24z"/><path fill="#FBBC05" d="M5.27 14.27A7.2 7.2 0 0 1 4.89 12c0-.79.14-1.56.38-2.27V6.61H1.27A12 12 0 0 0 0 12c0 1.94.46 3.77 1.27 5.39z"/><path fill="#EA4335" d="M12 4.77c1.77 0 3.35.61 4.6 1.8l3.44-3.44C17.96 1.19 15.24 0 12 0 7.31 0 3.25 2.7 1.27 6.61l4 3.12C6.22 6.89 8.87 4.77 12 4.77z"/></svg>
        <h1 class="ml-type-3" style="margin:var(--space-2) 0 0">Choose an account</h1>
        <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-1) 0 0">to continue to MusicLinkd</p>
      </div>

      <div class="ml-banner ml-banner--error" role="alert" data-state="fallback_fake">
        Google sign-in is unavailable right now. Continue with email instead.
      </div>

      <div>
        <a class="ml-choice" href="/preview/flows/auth/registration_write/index.html">
          <img class="ml-avatar" src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=120" alt="" />
          <span class="ml-choice__body">
            <span class="ml-choice__title">Ananya Sharma</span>
            <span class="ml-choice__meta">ananya.sharma@gmail.com</span>
          </span>
        </a>
        <a class="ml-choice" href="/preview/flows/auth/email_auth/index.html?state=signup">
          <span class="ml-avatar" style="display:flex;align-items:center;justify-content:center;background:var(--color-surface);color:var(--color-muted)" aria-hidden="true">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M12 5v14M5 12h14"/></svg>
          </span>
          <span class="ml-choice__body"><span class="ml-choice__title">Use another account</span></span>
        </a>
      </div>

      <p class="ml-type-1" style="color:var(--color-muted)" data-state="account_picker">MusicLinkd will receive your name, email address and profile picture.</p>
      <div data-state="success" style="text-align:center">
        ${checkMark}
        <p class="ml-type-2" style="margin-top:var(--space-2)">Signed in as ananya.sharma@gmail.com</p>
      </div>
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="/preview/flows/auth/email_auth/index.html?state=signup" data-state="fallback_fake">Continue with email</a>
    </main>
  </div>` }));

/* 7 — Registration write. states: success / fallback_uid */
done.push(writeScreen({ flow: "auth", screen: "registration_write", body: `
  <div class="ml-app">
    <main class="ml-app__body" style="display:flex;flex-direction:column;align-items:center;justify-content:center;gap:var(--space-3);text-align:center">
      ${checkMark}
      <div>
        <h1 class="ml-type-3" style="margin:0">Account created</h1>
        <p class="ml-type-2" style="color:var(--color-muted);margin:var(--space-1) 0 0">Welcome to MusicLinkd — let's set up your profile next.</p>
      </div>
      <span class="ml-status ml-status--success" data-state="success">Verified email</span>
      <div class="ml-banner ml-banner--warning ml-banner--info" data-state="fallback_uid">
        Created with a local account ID — verify your email to unlock booking and payouts.
      </div>
    </main>
    <footer class="ml-app__footer">
      <a class="ml-btn ml-btn--primary ml-btn--block ml-btn--lg" href="/preview/flows/onboarding/primary_role/index.html">Continue to onboarding</a>
    </footer>
  </div>` }));

const linkCount = finish("auth: ");
console.log("auth: wrote " + done.length + " screens, " + linkCount + " links verified\n  " + done.join("\n  "));

/* ══ the three screens the approved plan listed and that were never built ══
   email_verification, forgot_password and reset_password. Each owns a route:
   two are reached from an emailed link, and a person arriving at one has no
   session to carry them anywhere else.

   The copy rule that shapes all three: never claim a change has happened
   before it has. An email is not verified until the link is followed, and a
   reset request says the same thing whether or not the address exists — an
   honest "if that address is registered" is the difference between a reset
   form and an account-discovery oracle. ══════════════════════════════════ */

const A = "/preview/flows/auth/";

const authBar = (title, backTo) => `<header class="ml-appbar" style="display:flex;align-items:center;gap:var(--space-2);padding:var(--space-2) var(--space-3)">
      ${backArrow(backTo, "Back")}
      <h1 class="ml-type-2" style="margin:0">${title}</h1>
    </header>`;

writeScreen({
  flow: "auth", screen: "email_verification", title: "Auth — Verify your email",
  body: `
  <div class="ml-app">
    ${authBar("Verify your email", `${A}email_auth/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div class="ml-empty" data-state="sent">
        <h2 class="ml-empty__title">Check your email</h2>
        <p class="ml-empty__body">
          We've sent a link to <b>ishita@example.com</b>. Follow it and you're in.
          Your address isn't verified until you do.
        </p>
        <a class="ml-btn ml-btn--primary" href="?state=verifying">I've followed the link</a>
        <a class="ml-btn ml-btn--ghost" style="margin-top:var(--space-2)" href="?state=sent">Send it again</a>
        <p class="ml-type-1" style="color:var(--color-muted);margin-top:var(--space-3)">
          Check spam before asking for another — a second email replaces the first, and only
          the newest link works.
        </p>
      </div>

      <div class="ml-empty" data-state="verifying">
        <span class="ml-spinner" role="status" aria-label="Checking"></span>
        <p class="ml-empty__body" style="margin-top:var(--space-3)">Checking the link…</p>
      </div>

      <div class="ml-empty" data-state="verified">
        <h2 class="ml-empty__title" style="color:var(--color-success)">Email verified</h2>
        <p class="ml-empty__body">That's your address confirmed. Let's set up your profile.</p>
        <a class="ml-btn ml-btn--primary" href="/preview/flows/onboarding/primary_role/index.html">Continue</a>
      </div>

      <div class="ml-empty" data-state="expired">
        <h2 class="ml-empty__title">That link has expired</h2>
        <p class="ml-empty__body">Verification links last 24 hours. We can send a fresh one.</p>
        <a class="ml-btn ml-btn--primary" href="?state=sent">Send a new link</a>
      </div>

      <div data-state="error">
        <div class="ml-banner ml-banner--error">
          <span class="ml-banner__body">We couldn't verify that link. It may already have been used.</span>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=sent">Send a new link</a>
      </div>
    </main>
  </div>`,
});

writeScreen({
  flow: "auth", screen: "forgot_password", title: "Auth — Forgot password",
  body: `
  <div class="ml-app">
    ${authBar("Forgot password", `${A}email_auth/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="default">
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Give us the address on your account and we'll send a link to set a new password.
        </p>
        <div class="ml-field">
          <label for="fp-email">Email</label>
          <input id="fp-email" type="email" placeholder="you@example.com" />
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=sent">Send reset link</a>
        <a class="ml-btn ml-btn--ghost ml-btn--block" style="margin-top:var(--space-2)" href="${A}email_auth/index.html">Back to sign in</a>
      </div>

      <!-- Deliberately identical whether or not the address is registered. A
           form that says "no such account" tells anyone who asks which email
           addresses have accounts here. -->
      <div class="ml-empty" data-state="sent">
        <h2 class="ml-empty__title">Check your email</h2>
        <p class="ml-empty__body">
          If that address is registered, a reset link is on its way. It's valid for one hour.
        </p>
        <a class="ml-btn ml-btn--primary" href="${A}email_auth/index.html">Back to sign in</a>
        <!-- In the real app you arrive at the reset screen from the email. In
             this preview the flow has to be walkable, so the link is here. -->
        <a class="ml-btn ml-btn--ghost" style="margin-top:var(--space-2)" href="${A}reset_password/index.html">Preview: open the reset screen</a>
      </div>

      <div data-state="error">
        <div class="ml-banner ml-banner--error">
          <span class="ml-banner__body">We couldn't send that. Try again in a moment.</span>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=default">Retry</a>
      </div>
    </main>
  </div>`,
});

writeScreen({
  flow: "auth", screen: "reset_password", title: "Auth — Set a new password",
  body: `
  <div class="ml-app">
    ${authBar("New password", `${A}forgot_password/index.html`)}
    <main class="ml-app__body app-content--scroll" style="padding:var(--space-3) var(--space-4) var(--space-4)">

      <div data-state="default weak">
        <p class="ml-type-2" style="color:var(--color-muted);margin:0 0 var(--space-3)">
          Setting a new password for <b>ishita@example.com</b>. You'll be signed out everywhere else.
        </p>
        <div class="ml-field">
          <label for="rp-new">New password</label>
          <input id="rp-new" type="password" />
        </div>
        <div class="ml-field">
          <label for="rp-confirm">Confirm it</label>
          <input id="rp-confirm" type="password" />
        </div>

        <div data-state="weak">
          <div class="ml-banner ml-banner--warning" style="margin-top:var(--space-2)">
            <span class="ml-banner__body">That's a common password. Pick something harder to guess.</span>
          </div>
        </div>

        <div class="ml-summary" style="margin-top:var(--space-3)">
          <div class="ml-summary__row"><span>At least 10 characters</span><span>Required</span></div>
          <div class="ml-summary__row"><span>Not a password you've used here</span><span>Required</span></div>
          <div class="ml-summary__row"><span>Not one of the 10,000 most common</span><span>Required</span></div>
        </div>
        <a class="ml-btn ml-btn--primary ml-btn--block" style="margin-top:var(--space-3)" href="?state=saved">Set password</a>
      </div>

      <div class="ml-empty" data-state="saved">
        <h2 class="ml-empty__title" style="color:var(--color-success)">Password changed</h2>
        <p class="ml-empty__body">You've been signed out on every other device. Sign in with the new one.</p>
        <a class="ml-btn ml-btn--primary" href="${A}email_auth/index.html">Sign in</a>
      </div>

      <div class="ml-empty" data-state="expired">
        <h2 class="ml-empty__title">That link has expired</h2>
        <p class="ml-empty__body">Reset links last an hour, so a stale one can't be used later.</p>
        <a class="ml-btn ml-btn--primary" href="${A}forgot_password/index.html">Ask for a new link</a>
      </div>
    </main>
  </div>`,
});
