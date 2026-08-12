# Documentation / Diagramming Backlog — saved for later

Pasted by the user (2026-08-12), explicitly **not** to be acted on now —
"keep it safe someplace (for later)." This supersedes/extends whatever
shorter list exists under the plan file's "Part V — Documentation backlog"
section; treat this file as the canonical, up-to-date version of that
backlog going forward.

None of this is built. Nothing here should be inferred as done just
because a related *flow* is documented — flow/screen mapping (the
`flows`/`flows/*/screens` Firestore collections) is a different, narrower
thing than the diagram/journey/lifecycle documentation requested below.

## 1. Website flows — missing entirely
Landing page, Waitlist signup, Play Store redirect, Newsletter, Contact,
SEO pages, Blog, FAQ, Legal.

## 2. Admin flows — largely absent
Admin Login, Dashboard, User Management, Artist Verification, Studio
Verification, Reports, Moderation Queue, Marketplace Moderation, Booking
Management, CMS, Notification Broadcast, Analytics, Feature Flags, Remote
Config, Settings, Roles & Permissions, Audit Logs.

(Note: the *admin panel itself* implements most of these as real CRUD
modules already — see `admin/app/(admin)/`. What's missing here is a
documented *flow* for each, the same treatment given to the 22 app-side
product flows, not the underlying feature.)

## 3. Complete user journey map — missing
Guest → Website → Play Store → Install → Open App → Permissions → Signup →
Onboarding → Discovery → Connections → Messaging → Booking → Payment
(future) → Review → Retention → Notifications → Referral.

## 4. Error journeys — missing
Offline, No internet, Firebase unavailable, Upload failed, Login failed,
Token expired, Session expired, Permission denied, Booking conflict,
Storage full, Push notification denied.

## 5. State diagrams — missing
Every feature should have one. Example (Messaging):
Idle → Loading → Loaded → Sending → Delivered → Read → Failed → Retry.

## 6. Screen transition diagrams — missing
Not just navigation graphs — actual transition/animation intent. Example:
Splash → Login → Home → Discover → Profile → Chat → Booking → Settings.

## 7. Backend lifecycle — missing
Example: Android → Repository → Firestore → Cloud Function → FCM →
Receiver → UI Refresh.

## 8. Firestore lifecycle — missing
Example: User presses Connect → `connections` collection → Cloud Function
→ notification document → FCM → receiver → badge update.

## 9. Notification flows — missing
Types are documented; flow diagrams aren't. Example: Booking Confirmed →
Firestore → Cloud Function → FCM → Android → Notification → Open Booking.

## 10. Analytics flows — missing
User Action → Analytics Event → Firebase Analytics → GA4 → BigQuery
(future).

## 11. AI flows — missing
Gemini deserves its own flow. Example: Search → Repository → Gemini →
Similarity → Recommendations → Cards. (Cross-reference:
`recommendation_engine` flow — GeminiRecommendationEngine.kt is real code
but neutered by a placeholder-API-key guard-check bug; this diagram
backlog item is about documenting the *intended* flow, separate from that
known bug.)

## 12. Admin moderation lifecycle — missing
Report → Queue → Moderator → Review → Action → Ban → Notify User → Audit
Log.

## 13. Marketplace lifecycle — missing
Seller → Create Listing → Review → Approve → Publish → Search → Contact →
Archive. (Cross-reference: the `marketplace` flow audit, 2026-08-12,
confirmed no create-listing screen exists at all yet — this lifecycle is
aspirational/target-state, not a description of current behavior.)

## 14. Booking lifecycle — missing
Draft → Pending → Accepted → Confirmed → Completed → Reviewed → Archived.

## 15. Verification lifecycle — missing
Artist → Upload Documents → Pending → Manual Review → Approved → Verified
Badge. (Cross-reference: the `verifications` flow audit, 2026-08-12,
confirmed no status-tracking screen exists yet — same aspirational caveat
as #13.)

## 16. Role-based journeys — missing
Separate journeys needed for: Guest, User, Artist, Verified Artist,
Studio, Business, Admin, Super Admin.

## 17. Deep linking — missing
URL → App Link → Android → Navigation → Destination.

## 18. Firebase architecture flow — missing
Auth → Firestore → Storage → Functions → FCM → Analytics → Crashlytics →
Remote Config → App.

## 19. Repository interaction flow — missing
UI → ViewModel → Repository → Local → Remote → Mapper → StateFlow →
Compose.

## 20. Release flow — missing
GitHub → PR → Merge → Actions → Firebase Hosting → App Distribution →
Play Store.

---

**Next step, when resumed:** same grounding rule as the flow/screen audit
— pull each of these from real source (Kotlin nav graphs, Cloud Functions
source, CI config, `firestore.rules`) rather than writing plausible-looking
diagrams from general knowledge. Items 1, 2, 17, 18, 19, 20 are the most
tractable to ground immediately (they map to existing code/config); items
3–16 are journey/lifecycle *designs* that mix documented-current-state
with aspirational-target-state and need that distinction made explicit
per item, the same way #13 and #15 are flagged above.
