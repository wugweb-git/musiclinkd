# Edge Functions & Error Scenarios

**Audited:** August 2026. Dated snapshot of `/app` (Android) and this repo's
Firebase config — not synced automatically; the Android app is built and
published separately via AI Studio. Re-verify before relying on specifics.

**Update, in-repo:** Part C of the admin master-data/modules plan *wrote*
server-side logic — `toggleModule`, under `backend/functions/` — but it is
**not deployed and not called by the admin UI**. `musiclinkd-7ae30` is on
Firebase's free Spark plan; Cloud Functions (any generation) require the
paid Blaze plan to deploy at all, confirmed by a live `firebase deploy
--only functions` attempt this session, which failed before deploying
anything or incurring any cost. Rather than force a billing upgrade, v1
module enable/disable ships as a direct, `isAdmin()`-gated Firestore write
from the admin browser (`admin/app/(admin)/modules/page.tsx`) — no server
function in the loop. `toggleModule`'s code is kept as a ready-to-deploy
reference for later, along with a Supabase project (free tier, no billing
upgrade needed for Edge Functions) registered for exactly this purpose —
see "What Part C actually shipped" below for why it isn't wired up either.
Everything below the line marking "before Part C" describes the
pre-existing state (all client-side, zero server logic) that motivated
writing it in the first place.

## Server-side logic — before Part C

**None existed.** Confirmed by direct inspection:
- `backend/{functions,api,webhooks,auth,database,storage}/` each contained
  only a `.gitkeep` placeholder.
- `firebase.json` had no `functions` key — no Cloud Functions were even
  configured for deployment, let alone written.
- No HTTP calls from the Android app target anything other than
  `generativelanguage.googleapis.com` (Gemini, client-side) and Firebase
  SDKs (client-side).

This meant all business logic — including the "Authorize & Secure Payment"
button in studio booking (bare UI event, no gateway; see Service_Flow.md) —
ran client-side, with Firestore rules validating identity/ownership but not
business rules (amounts, booking conflicts, etc.). No server-side trust
boundary existed anywhere.

## What Part C wrote, and what it actually shipped

`backend/functions/src/index.ts` — `toggleModule`, an HTTPS Callable that
verifies the caller is `ROLE_ADMIN` (via `/admin_roles/{uid}`, mirroring
`firestore.rules`' `isAdmin()`), flips `modules/{key}.enabled` via the Admin
SDK, and — if the module has a `remoteConfigKey` — writes the matching
Remote Config parameter via `getRemoteConfig().getTemplate()`/
`publishTemplate()` (the Admin SDK's write path; the client SDK used in
`admin/services/remote-config.ts` can only read). Builds cleanly
(`cd backend/functions && npm run build`) but **is not deployed** — blocked
by the Blaze-plan requirement above.

What actually ships and is live: `firestore.rules`' `/modules/{moduleKey}`
match block lets `isAdmin()` write `enabled` directly, and
`admin/app/(admin)/modules/page.tsx` does exactly that — no function call.
This means v1 has no Remote Config mirror and no `audit_logs` entry for
toggles (both were the function's job) — a real, accepted gap, not an
oversight. `modules/{key}.remoteConfigKey` is still stored on each registry
row so the mirror can be wired up later without a schema change.

**Deferred path — Supabase**: a free-tier Supabase project (`wugweb` org,
credentials in `admin/.env.local`) was registered as an alternative host for
this function, since Supabase Edge Functions don't require a paid plan.
Not yet built: doing so still requires a Firebase service-account credential
(to call Firestore/Remote Config Admin APIs from outside Firebase's own
infra — Cloud Functions get this for free via their default service
account; Supabase Edge Functions don't), which is a real secret someone has
to generate via the Firebase console and provision as a Supabase secret —
deliberately not done in this pass to avoid handling that credential for a
minor admin-toggle feature. When that's worth doing: generate the key
(Firebase console → Project Settings → Service Accounts), store it as a
Supabase secret, port `backend/functions/src/index.ts`'s logic to a Deno
Edge Function (Firestore/Remote Config REST calls instead of
`firebase-admin`, since that SDK is Node-only), and switch
`modules/page.tsx`'s toggle handler to call it instead of `modulesRepo.update()` directly.

## Error-handling findings (Android app)

- **`onboarding/ui/components/ScreenErrorBoundary.kt`** wraps the whole app
  (`MainActivity.kt:131`) and looks like a React-style error boundary, but
  isn't one — it only renders its fallback if `caughtError` is set via
  `LocalScreenErrorHandler.current(throwable)`, and
  **`LocalScreenErrorHandler` is never actually invoked anywhere**, only
  defined and provided. Compose has no mechanism to catch exceptions thrown
  by child composables this way — an uncaught exception in any screen still
  crashes the app. This "boundary" currently protects nothing.
- **`ProfileManagementScreens.kt:212-220` (`simulateSave`)** — a literal
  fake loading spinner (`delay(1200)`, then run the action) with no
  relationship to whether the underlying Firestore write actually happened.
  The "successfully saved & published to Firestore!" banner (line 349)
  fires **unconditionally**, regardless of whether the write succeeded —
  the `catch (e: Exception) { e.printStackTrace() }` at lines 346-348
  silently swallows any failure with no user-facing signal.
- **`ConnectionsScreen.kt:117-133`** — same pattern: fire-and-forget
  `.set()` with no `.addOnSuccessListener`/`.addOnFailureListener`, wrapped
  in a try/catch that only logs to stdout.
- **`onboarding/viewmodel/OnboardingViewModel.kt`** is the one place with
  genuine, well-structured error handling — `performFirebaseAuth()`
  (754-806) and the profile-save flow (670-721) use success/failure
  callbacks that update a `StateFlow` with real error strings screens can
  render. This is the pattern the rest of the app should have used but
  mostly doesn't.
- **`MainActivity.kt:55-85`** — every Google Sign-In failure branch (null
  token, `ApiException`, canceled result, launch exception) converges on
  `viewModel.signUpWithGoogle()`, which hardcodes a specific developer
  identity into onboarding state and proceeds as if sign-in succeeded, with
  only a passing Toast ("Using high-fidelity profile emulation") as the
  "error surface." Since `default_web_client_id` is a mock value (see the
  plan's Part E), this is the path that actually executes for anyone
  tapping "Sign in with Google" today.
- **`FirebaseManager.kt`** — the central Firestore/Auth access point —
  silently falls back to fake success on failure throughout: fake FCM
  tokens, a hardcoded fake sign-in identity (`google_uid_emulated`) when
  Firebase Auth isn't reachable, `sendConnectionRequest()`/
  `createFeedPost()`/`fetchNetworkingFeed()` all calling `onSuccess()`
  even when the underlying Firestore client is null.
  `uploadMediaAttachment()` never touches Firebase Storage at all — it
  returns a hardcoded stock URL regardless of the file passed in.
- **`RepositoryProvider.kt`** — on `initialize()`, unconditionally seeds a
  full fake dataset into local Room (fake onboarding profile, notifications,
  reviews, conversations) on every fresh install. This is why the app
  "looks used" even though none of it is Firestore data — see Modules_Map.md.
- **`onboarding/util/ErrorReportingService.kt`** — forwards to Crashlytics
  if Firebase is initialized, else silently degrades to an in-memory buffer
  capped at 30-50 entries — which is **never read anywhere in the UI**, so
  even on-device diagnostics are invisible to the user and lost on process
  death.
- **`FirestoreRepository.kt`** is the one exception that fails honestly —
  every method returns `false`/`null`/empty and logs via `Log.e`, no fake
  success. It's just dead code, never called from anywhere (see
  Screens_Map.md headline finding).

## Gemini AI — real but neutered, plus a guard-check bug

`GeminiRecommendationEngine.kt` and `GeminiImageGenerator.kt` make genuine
OkHttp calls to `generativelanguage.googleapis.com`, but both read
`BuildConfig.GEMINI_API_KEY`, which resolves to the literal placeholder
`"MY_GEMINI_API_KEY"` in this repo's `.env`. `GeminiImageGenerator.kt`
correctly checks for that exact string and short-circuits.
`GeminiRecommendationEngine.kt` instead checks for `"YOUR_GEMINI_API_KEY"` —
a different string — so its guard never trips; it makes a live, doomed HTTP
call to Google on every use before falling through to the same hardcoded
fallback data. Functionally both end up on fallback content, but one via an
extra live failed network round-trip caused by a copy-paste mismatch. Flagged
as a small independent fix in the plan's Part E (AI Studio handoff).
