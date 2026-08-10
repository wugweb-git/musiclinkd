# Deployment Plan

**This is a planning document — nothing here is implemented.** No
`.github/workflows/` directory exists in this repo; the description below
of a GitHub Actions pipeline is a proposal to evaluate, not a running
system. Written August 2026.

## Current state (fully manual)

Three deployables share one Firebase project (`musiclinkd-7ae30`) and one
Firebase Hosting site (two custom domains — `musiclinkd.com`,
`admin.musiclinkd.com` — both pointed at the same merged output):

1. **Admin panel** (`admin/`, Next.js 15 static export) and **marketing
   website** (`frontend/website/`, static HTML) are merged by
   `scripts/build-hosting.sh` into `dist/`, then deployed via `firebase
   deploy` (hosting target). Entirely manual — run from a developer's
   machine.
2. **Firestore rules/indexes, Storage rules, Remote Config template** —
   deployed via `firebase deploy --only firestore,storage,remoteconfig`,
   also manual.
3. **Cloud Functions** (`backend/functions/`, added in Part C of the
   master-data/modules plan) — `cd backend/functions && npm run build &&
   firebase deploy --only functions`, also manual, no build step folded
   into `build-hosting.sh` (deliberately — see the plan file's Part C
   rationale: hosting and functions are orthogonal deploy targets).
4. **Android app** (`/app`) — built and published entirely through Google
   AI Studio's own Build mode, outside this repo's deploy story. Any GitHub
   pipeline for this repo should explicitly exclude `/app` — there is no
   connector or export path that would let a GitHub Action publish an AI
   Studio-managed app (confirmed this session: AI Studio supports ZIP
   export and a Secrets Manager for API keys, no external CI/API access).

No CI runs on push or PR today — no lint, typecheck, or build verification
happens automatically anywhere in this repo.

## Proposed GitHub Actions pipeline (not built)

Scope: admin + website + Firestore/Storage/Remote Config rules + Cloud
Functions. Explicitly **not** the Android app.

- **On pull request:** `cd admin && npm run build` (catches TypeScript/build
  errors before merge — this is the same build `build-hosting.sh` already
  runs, just gated earlier) and `cd backend/functions && npm run build`.
  No deploy on PR.
- **On push to `main`:** run `scripts/build-hosting.sh`, then `firebase
  deploy --only hosting,firestore,storage,remoteconfig,functions` using a
  Firebase service-account key stored as a GitHub Actions secret
  (`FIREBASE_SERVICE_ACCOUNT`, generated via `firebase projects:addaccount`
  or the Firebase console's service-account key export — the exact
  provisioning step needs a decision, not covered by this doc).
- **Remote Config caveat, carried over from the plan's Part C:** Part C's
  `toggleModule` Cloud Function calls `publishTemplate()` directly against
  the *live* Remote Config template — bypassing `remoteconfig.template.json`
  entirely. If this pipeline's `remoteconfig` deploy target runs blindly on
  every push to `main`, it would silently overwrite any live admin-driven
  module toggles with whatever's checked into
  `remoteconfig.template.json` at that commit. **Before automating the
  `remoteconfig` deploy target, this needs either (a) a manual reconciliation
  step in the pipeline (`firebase remoteconfig:get` → diff → confirm) or
  (b) dropping `remoteconfig` from the automated targets entirely and
  keeping it a manual, deliberate deploy** — not resolved here, flagging it
  because automating past it silently would be a real regression, not a
  hypothetical one.

## Explicitly out of scope for this doc

- Staging/preview environments (this repo has no staging Firebase project
  today — one project, `musiclinkd-7ae30`, serves both `dev` and `prod`
  implicitly by however developers already work; introducing a second
  project is a bigger decision than a CI pipeline change).
- Secret rotation policy for the service-account key.
- Any Android/AI Studio automation — confirmed not currently possible.
