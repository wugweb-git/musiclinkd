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

## Auth & registration — a deeper audit found this is worse than "falls back on failure"

A follow-up audit (dated 2026-08-11) traced the full sign-up sequence, not
just the Google Sign-In failure path already documented above. Net finding:
**most real registrations in this app today are not backed by any actual
Firebase Auth account.**

- **Email/password is real but broken, not fake.** `createFirebaseUser`/
  `loginFirebaseUser` (`FirebaseManager.kt:113,151`) call the genuine SDK.
  Because the project's `google-services.json` API key is fabricated (see
  the plan's Part E), these calls go out over the network and fail
  server-side with a real Firebase error — the one honest failure mode in
  the whole auth flow.
- **Mobile/OTP is 100% fake — no SMS is ever sent, no code is ever
  checked.** `sendOtp()` (`OnboardingViewModel.kt:59-78`) is `delay(1200)`
  and a screen transition. `verifyOtp()`'s check
  (`OnboardingViewModel.kt:116`) is
  `if (enteredOtp == "123456" || enteredOtp.length == 6)` — the `||` makes
  the first clause dead; **any 6-digit string passes**. The UI tells users
  this outright: `"💡 Tip: Enter '123456' to pass immediately"`
  (`WelcomeAndAuthScreens.kt:1049`). No `PhoneAuthProvider` exists anywhere
  in the codebase — this was never wired to real phone auth, not a
  regression.
- **Google Sign-In's fallback (documented above) is the common case, not
  an edge case**, confirmed by tracing why: the OAuth client ID is
  fabricated (`strings.xml:3`), so the real sign-in intent reliably fails
  with `DEVELOPER_ERROR`, routing every attempt to
  `signUpWithGoogle()` (`OnboardingViewModel.kt:813-828`) — zero network
  calls, hardcodes a specific developer's name/email/phone.
- **A second, independent fallback UID pattern exists at the point of
  final registration**, distinct from `google_uid_emulated`:
  ```kotlin
  val userId = FirebaseManager.getAuth()?.currentUser?.uid
    ?: "local_uid_${stateValue.email.hashCode()}"
  ```
  (`OnboardingViewModel.kt:673`). Given the three points above,
  `currentUser` is null far more often than not in real usage — so the
  `users/{uid}` document most real signups produce today is keyed by a
  deterministic hash of the typed email string, with **no corresponding
  Firebase Auth account behind it at all**. Same systemic pattern as the
  already-documented `sendConnectionRequest`/`uploadMediaAttachment`/
  `createFeedPost` fallbacks above, just not previously traced to its
  origin at registration time.
- **No auth gate exists before onboarding starts**, and **session
  "persistence" is local-Room-based, not Firebase-based** — cold start
  never checks `FirebaseAuth.getInstance().currentUser` anywhere; it
  resumes from the last-saved screen in Room (`OnboardingViewModel.kt:27-46`).
  A user with no real Firebase session (the common case) is still dropped
  straight back into the app as if logged in.
- **Two more onboarding fields silently dropped from the Firestore write**,
  beyond the four already known (`instrumentProficiency`, `roleExperience`,
  `networkingLinks`, `externalLinks`): `profilePicturePath` and
  `coverPicturePath` (collected via `updateProfile()`,
  `OnboardingViewModel.kt:446-467`) never appear in `confirmProfile()`'s
  write payload — a user's own profile/cover photo never reaches the cloud
  document.
- **Dead auth code, not to be confused with the live path**:
  `com.example.auth.FirebaseAuthManager` (a fully-built singleton) and
  `com.example.features.auth.AuthFeature` — both zero-reference; the real
  nav graph calls `onboarding.ui.screens.WelcomeScreen` directly.

**This needs a product decision before any further prompt to AI Studio**,
not a schema fix: should OTP become real (requires a real SMS
gateway/Firebase Phone Auth — a cost decision), should Google Sign-In get a
real OAuth client (blocked on the still-outstanding real
`google-services.json`/`applicationId` registration), and is the
`local_uid_${email.hashCode()}` fallback acceptable as a permanent
pseudo-identity or should registration hard-block without a real UID.

## Verification pattern — AI Studio's own changelogs are not evidence

Across five separate verification passes this session (2026-08-10 to
2026-08-11), every claim in AI Studio's changelogs and `BackendSchemas.kt`/
`BACKEND_SCHEMA_SPEC.md` revisions was checked directly against the live
source before being trusted — and the pattern held every time: **the
document gets revised; the implementing code frequently does not.** The
sharpest example: a `v1.9.1` changelog entry claiming 11 specific fixes
(studio-booking field-name dual-write, connections composite-ID, onboarding
payload persistence, settings/moderation Firestore wiring, QR code fix,
7 new `FirebaseManager` functions, portfolio publishing, Room migration to
v10, feed normalization, `MasterVocabularies`, `notifications`/
`brand_assets` collections) was checked item-by-item — **all 11 were
false**, none partially confirmed. The strongest independent signal:
`FirebaseManager.kt`, central to 4 of those 11 claims, had **zero changes —
committed or uncommitted — since the initial commit**, despite three
separate changelog entries across different dates claiming functions were
added to it. Three other files did show real, substantial uncommitted
editing activity in the same period — so work is genuinely happening, it
just isn't producing what the changelogs describe. Treat every future
changelog/schema-doc claim from AI Studio the same way: as a claim to
verify against the actual screen/viewmodel/repository file, never as
evidence on its own.

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
