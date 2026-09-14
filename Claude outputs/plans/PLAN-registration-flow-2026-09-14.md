---
doc: PLAN-registration-flow-2026-09-14
version: 1.0
status: AWAITING ACTION
updated: 2026-09-14
verified_against: musiclinkd-9e6ca875@4d402b6
folder: plans
originally: new
---

> **AWAITING ACTION** · 14 Sep 2026. Audit of the 28 new-user registration screens against the
> design system, plus a UX work plan for Claude Code. Every line number below was read from the
> code at `4d402b6`, not inferred.
> Ground truth lives in `state/CURRENT.md`.

# New-user registration — audit and plan

## 0. The flow list is one step ahead of the code

The supplied flow has 29 screens including **25 `/onboarding/step/band` — "Are you in a band?"**

**That step does not exist.** `src/data/onboarding.ts` defines **19 steps**, ending at `business`.
There is no `band` step id, and `bandIntent` appears nowhere in `src/`. Band setup is not an account
type and never enters onboarding — correct as built.

**Action:** renumber the flow doc to 28 screens and delete step 25. No code change.

---

## 1. Design-system audit — the good news first

This is the cleanest part of the codebase. Measured across all 19 flow files:

| Check | Result |
|---|---|
| `text-[Npx]` arbitrary sizes | **1** — `auth.locked.tsx:43` `text-[13px]` |
| `rounded-[Npx]` | **0** |
| `bg-[...]` / raw hex in class names | **0** |
| `tracking-tight` | **0** |
| Emoji or text glyphs as icons | **0** |
| Navy / `#5B4BFF` | **0** |
| Named spacing (`p-md` etc.) | **0** |
| **All 12 auth-family screens use `AuthScreen`** | ✅ one shell, one brand lockup, one ground |

Compare this with `src/routes/index.tsx` (Discover), which carries six arbitrary sizes and two inline
styles in one file. The registration flow was built to the system. **Do not "sweep" it in Phase 8 —
it is already done.**

### 1.1 The five real off-token findings

| # | File:line | Finding | Fix |
|---|---|---|---|
| 1 | `auth.locked.tsx:43` | `text-[13px]` — the only arbitrary size in the flow | `text-1` |
| 2 | 16 sites across 7 onboarding files | `text-sm` — a Tailwind default, not a token. `index.tsx:74` · `step.$id.tsx:95,283` · `preview.tsx:62,75,121` · `complete.tsx:68` · `done.tsx:49,57,67,73,79` · `skip.tsx:42,48` · `resume.tsx:53,61` | `text-1` or `text-2`, whichever matches the rendered size today — check before swapping, do not assume |
| 3 | `auth.locked.tsx:33` | inline `style={{ background: "color-mix(in oklab, var(--destructive) 12%, transparent)" }}` | Add a `--destructive-container` token beside the existing `--pink-container` / `--success-container` / `--warn-container`, then `bg-destructive-container` |
| 4 | `auth.maintenance.tsx:28` | inline `style={{ background: "var(--pink-container)" }}` | `bg-pink-container` — the utility already exists |
| 5 | `welcome.tsx:136` | `<h2 className="text-3 font-bold">MusicLinkd</h2>` — a **hand-rolled wordmark**. Every other screen in the flow renders `Brand` from `ml/auth.tsx:35` | Use `Brand`, or `Brand compact` |

**Not defects, leave alone:**

- `welcome.tsx:52-64` — four raw hex fills (`#EA4335`, `#4285F4`, `#FBBC05`, `#34A853`) are the
  Google "G" mark. Google's brand guidelines require those exact values. Correct as-is; add a
  one-line comment so no future sweep "fixes" them.
- `complete.tsx:84` and `resume.tsx:74` — inline `style={{ width: '${pct}%' }}` on a progress bar.
  A computed width cannot be a Tailwind class. Correct as-is.
- `tracking-wide` on uppercase eyebrow labels — positive tracking on all-caps is a legibility
  convention, not Inter Tight residue.

---

## 2. The auth screens are not duplicated where it looks like it

Nine files for one pattern **is** a lot, but they already share `AuthScreen` (shell, max-width,
ground, brand) and `AuthHeading`. The shell is not the duplication.

```
auth.maintenance.tsx   47 lines
auth.offline.tsx       54
auth.success.tsx       56
auth.signout.tsx       58
auth.locked.tsx        69
auth.phone.tsx         74
auth.verify-email.tsx 104
auth.forgot.tsx       120
auth.otp.tsx          130
auth.index.tsx        238
```

**The three status screens are the real duplication.** `locked`, `maintenance` and `offline` are the
same object: an icon tile, a heading, a body, a bulleted list, one primary action. They differ only
in tone colour, icon and copy — and they are the three that break the token rules (findings 1, 3, 4
above are all in `locked` and `maintenance`).

**Action:** one `AuthStatus` component in `ml/auth.tsx` taking `{ tone, icon, title, body, actions }`.
Three files collapse to three thin call sites. `tone` maps to the container tokens, which is what
kills the inline styles by construction rather than by sweep.

**Do NOT merge** `index` / `phone` / `otp` / `verify-email` / `forgot`. They have different inputs,
different validation and different failure modes. They already share the parts worth sharing
(`LabeledInput`, `PasswordInput`, `AuthNotice`, `classifyAuthError`). Merging them behind one
"generic auth form" is how those files grow a `mode` prop with nine branches.

---

## 3. UX findings — mechanics, not visual design

These are measured properties of the flow, not taste. Each is a defect or a missing state.

### 3.1 A 19-step form with no way to see the end from the start

`StepShell` (`ml/onboarding.tsx:207`) does show `{group} · Step {step} of {total}` and a percentage
bar — that part is right. What it does not do is tell a user at `/onboarding` **what they are about
to be asked** or **how long it takes**, and only 4 of the 19 steps are actually required (identity,
role, location, visibility).

The mechanic worth adding is not a new screen: **mark the optional steps as optional in the step
data**, and let `StepShell` render that. A user who knows step 9 is optional finishes. A user who
thinks all 19 are mandatory abandons at step 6.

### 3.2 There is no motion anywhere in the flow

`grep 'animate-\|transition-'` across `step.$id.tsx` returns **nothing**. Nineteen steps, and each
one hard-cuts to the next. On Android this reads as a crash or a failed tap, because there is no
signal that the app received the input.

This is Phase 2b, and the registration flow is the highest-value place to land it first: it is the
one flow every single user sees, and it is 19 transitions deep. Honour `prefers-reduced-motion`, use
the existing `--duration-*` and `--ease-*` tokens, exit at 60-70% of enter.

### 3.3 Android keyboard handling is unspecified

`__root.tsx:81` sets `viewport-fit=cover` — good, and it is why safe-area insets resolve at all. But
there is no `interactive-widget` setting, so when the Android IME opens, the browser's default
resize behaviour decides what happens to a sticky footer button. In a 19-step form where nearly
every step has an input and a footer CTA, that is the single most-hit interaction in the product.

Specify it deliberately (`interactive-widget=resizes-content` is the usual choice for a form), then
verify on a real Android device at 412px with the keyboard open. This is a Capacitor prerequisite
too.

### 3.4 Google and email-link sign-up skip onboarding

Recorded in the route audit and still open. A user who signs up with Google lands on `/feed` with an
empty profile and no prompt. That is not a missing screen — it is a missing redirect on an existing
one. **Verify this claim on a run before acting**; it has been asserted from a grep, not observed.

### 3.5 `/auth/otp` — verify before repeating the claim

`auth.otp.tsx:59` renders "That code did not match" **conditionally on `error`**. The earlier report
that phone sign-in "can never succeed" was inferred from that string, not from a run. Confirm by
attempting a real OTP before anyone writes a fix.

---

## 4. The help screens — mechanics only

Vedanshu designs the screens. What follows is the wiring they need, and the constraints they must
respect.

- **Routes:** `/help/getting-started/$step` with 3 steps, or three flat routes. Prefer the
  parameterised route — one file, one set of states, and the step count can change without a new
  route. `/help` already exists and currently shows "Nothing here yet" for two topics.
- **Placement:** the flow has exactly two natural attachment points that do not add a step to the
  19 — `/auth/success` ("You're in", which already offers *Set up profile* / *Skip to Home*) and
  `/onboarding/complete` (the next-step cards). A third entry from `/help` covers the user who skips.
- **Dismissal must persist.** Android users reinstall and switch devices. Store "seen" in the
  profile row, not `localStorage` — `localStorage` is per-browser and will re-show the help on the
  Capacitor wrap. There is no column for this today; it needs one.
- **Hard constraints:** no dead controls, no fabricated counts or names, every screen ships loading,
  empty, error and offline states, 48dp touch floor, `:focus-visible` ring, Lucide icons only, no
  emoji. Copy is Vedanshu's.
- **Do not** make these an interstitial that blocks reaching the app. The flow already has 28
  screens before Home.

---

## 5. Order of work

Each is one branch, one PR, exit gate on each. Do not batch.

| # | Branch | Scope | Size |
|---|---|---|---|
| 1 | `fix/booking-link-targets` | **Not in this flow, but it goes first.** Three `params={{ id: "svc-1" }}` sites still break every Book button. See `state/CURRENT.md`. | tiny |
| 2 | `fix/auth-status-component` | `AuthStatus` in `ml/auth.tsx`; collapse `locked`/`maintenance`/`offline`; add `--destructive-container`; kills findings 1, 3, 4 | small |
| 3 | `fix/welcome-brand-lockup` | `welcome.tsx:136` uses `Brand`; comment the Google hex | tiny |
| 4 | `fix/onboarding-text-tokens` | 16 `text-sm` → the matching token. **Measure each rendered size first.** | small |
| 5 | `feat/onboarding-optional-steps` | Mark the 15 optional steps in the step data; `StepShell` renders it | small |
| 6 | `feat/onboarding-motion` | Phase 2b applied to the 19 step transitions and the sheets in this flow | medium |
| 7 | `chore/android-ime` | `interactive-widget`, verified on a device with the keyboard open | small |
| 8 | — | Verify 3.4 and 3.5 by running them, then spec the fix | investigation |

Items 2-4 are mechanical and independent. 5 and 6 change what a user experiences and should be
looked at on a device before merge. 8 is not a fix, it is the check that stops a fix being written
against a claim nobody confirmed.

## 6. What this plan does not contain

No screen designs, no flows, no copy, no seed content. Section 4 is placement and constraints for
help screens Vedanshu will design.
