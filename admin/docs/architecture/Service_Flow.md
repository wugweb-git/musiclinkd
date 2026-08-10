# Service Flow

**Audited:** August 2026, against `app/app/src/main/java/com/example/`.
Dated snapshot — not synced to the live Android app. Re-verify before
relying on specifics.

## Worked example: Studio Booking (fully mocked, traced end to end)

Picked because it's the most complete-looking flow in the app (11 sub-screens,
a full payment step) while being **entirely disconnected from Firestore** —
useful as the canonical "looks done, isn't" example.

1. **Entry point:** `com/example/features/studios/StudioBookingFeature.kt`
   → `StudioBookingMainFlowContainer` (`onboarding/ui/screens/StudioBookingScreens.kt:106`).
2. **Studio list:** `mockStudios` (lines 112–189) — 4 hardcoded `StudioSpace`
   objects (YRF Sound Lab, BeatRoute Spaces, Octave Sound Lab, The Humming
   Tree). No repository, no network call, no Room table.
3. **State:** all booking-configuration state (`selectedDate`,
   `selectedTimeSlot`, `sessionType`, `durationSelection`,
   `withResidentEngineer`, `selectedAddons`, `myBookings`) lives in plain
   Compose `remember { mutableStateOf/mutableStateListOf }` (lines 191–231)
   — not a ViewModel, not persisted to Room, not persisted to Firestore.
   Lost the moment the composable leaves composition.
4. **Payment step:** `StudioPaymentScreen` (~line 1703). "Authorize & Secure
   Payment" (`onClick = onPaymentSuccess`, ~line 1805) directly invokes the
   success callback — no payment SDK, no Razorpay/Stripe call, despite
   `RAZORPAY_KEY_ID`/`STRIPE_*` placeholders existing in `.env.example`.
   There's a second button, "Simulate Payment Failure Scenario"
   (`onClick = onPaymentFailed`) — the UI itself acknowledges this is
   simulated.
5. **On "success":** (lines 401–417) builds a `StudioBooking` object
   client-side and does `myBookings.add(0, newBooking)` — appends to the
   in-memory list, navigates to `CONFIRMATION_SUCCESS`. **No Firestore write
   occurs.** `grep -rn "studio_bookings"` across `com/example` returns zero
   results — the collection defined in `firestore.rules` is never written to
   or read from anywhere in the Android app.
6. **End state:** the booking exists only as a Compose list entry for the
   lifetime of that screen instance. Nothing reaches Room or Firestore, no
   notification fires to a studio owner, no side effect on `feed_posts`/
   `connections`. `StudioBooking` isn't even a `@Entity` (no Room
   annotation), so it couldn't be persisted locally without additional
   mapping code that doesn't exist.

Worth noting: `RepositoryProvider.kt`'s first-launch seed data independently
seeds a *fake* "Studio Booking Approved" notification and a fake "SoundOne
Studios" booking-confirmed chat thread into Room — cosmetic, unrelated to
anything a user does in the flow above, and reinforces the illusion that
bookings "work."

## Flows that actually reach Firestore

For contrast — the only three flows that are schema-consistent with what
the admin panel documents (see Modules_Map.md for the full completeness
table):

- **Onboarding → `users`:** `OnboardingViewModel.performFirebaseAuth()`
  (lines 754–806) and the profile-save flow (lines 670–721) use
  success/failure callback lambdas that update `_state` (StateFlow) with
  real error strings, which screens render. Writes land in Firestore's
  `users` collection with fields like `primaryRole`/`secondaryRole` as
  plain strings — no mapping to the admin schema's coarse
  `ARTIST/PRODUCER/VOCALIST/STUDIO/LABEL` enum exists (the app's own role
  picker offers 17 granular options; see Screens_Map.md §A).
- **Feed post create/fetch:** `NetworkingFeed.kt` calls
  `FirebaseManager.createFeedPost()`/`fetchNetworkingFeed()`, which write/read
  the `feed_posts` collection directly.
- **Connection request:** `ConnectionsScreen.kt:117-133`
  (`updateFirestoreConnection`) writes to `connections/{senderUid}_{targetUserId}`
  — but fire-and-forget, no `.addOnSuccessListener`/`.addOnFailureListener`,
  wrapped in a try/catch that only does `e.printStackTrace()` (see
  Edge_Functions_And_Errors.md).

All three of the above rely on `FirebaseManager.kt`, which itself
silently falls back to fake success if Firebase isn't actually reachable
(see Edge_Functions_And_Errors.md) — so even these "real" flows are only as
trustworthy as Firebase connectivity, which this session separately found to
be broken (see the plan's Part E, AI Studio handoff checklist).
