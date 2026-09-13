# Screen inventory — every route (13 Sep 2026, app commit a6dc3d0)

Seven read-only passes over all 209 route files. Format per route: default view, screens that replace it (with trigger), overlays, tabs, data-driven values, and anything unreachable. Every item cites file:line.

> Items fixed after these passes are marked in the main audit (`../route-audit-2026-09-13.md` §7.1). In particular, `onboarding/complete.tsx:129` now opens `/business/new?step=type`.

## Auth, welcome, onboarding, settings, plans, help

### / (src/routes/index.tsx)
default: splash showing the brand mark with role=status "Checking your session", then it sends the user to /feed if signed in or /welcome if not — index.tsx:31-40, 26-29
screens: none (the splash is the only view)
overlays: none
tabs: none
data-driven: none
hidden: none

### /welcome (src/routes/welcome.tsx)
default: MusicLinkd hero with three audience lines and three buttons (Google, Mobile, Email & Password), a Sign in link and the legal note — welcome.tsx:132-188
screens:
- redirect to /feed — trigger: !loading && signedIn — welcome.tsx:114-116
- Google error sends the user to /auth?mode=signup — trigger: result.error — welcome.tsx:123-126
overlays: none
tabs: none
data-driven: audience list — welcome.tsx:42-46
hidden: none

### /reset-password (src/routes/reset-password.tsx)
default: "Set a new password" form with new and confirm password fields, requirements and a Save button — reset-password.tsx:116-140
screens:
- loading skeleton — trigger: ready=false — reset-password.tsx:76-84
- "Password updated" success — trigger: done=true after updateUser — reset-password.tsx:86-95
- "This reset link has expired" — trigger: valid=false (no type=recovery in the URL hash and no session) — reset-password.tsx:97-114
- inline AuthNotice errors (weak password, passwords don't match, server error), not full views — reset-password.tsx:50-71,122
overlays: none
tabs: none
data-driven: none
hidden: reachable only from the email link set at auth.forgot.tsx:53. No in-app link points here.

### /auth?mode= (src/routes/auth.index.tsx)
default: "Welcome back" sign-in: Google and phone buttons, email and password fields, a Forgot password link and a switch to sign-up — auth.index.tsx:129-237
screens:
- "Create your account" sign-up — trigger: mode=signup — auth.index.tsx:25,51,132-137,198
- redirect to /feed — trigger: signedIn — auth.index.tsx:58-60
- sign-up with no session goes to /auth/verify-email — auth.index.tsx:94-96; sign-up with a session goes to /auth/success — :98; sign-in goes to /feed — :104
- inline AuthNotice variants (not full views): account_exists with "Sign in instead" — :145; invalid_credentials with "Reset password" — :149; unconfirmed with "Resend confirmation" — :153; rate_limit, network, unknown and the Google failure — :118-122, auth.tsx:281-323
overlays: none
tabs: signin / signup, switched by the text button — auth.index.tsx:212-226
data-driven: mode ∈ {signin, signup} — auth.index.tsx:21,24-26
hidden: none. The "rate_limit" error never routes to /auth/locked (auth.tsx:303-308).

### /auth/forgot (src/routes/auth.forgot.tsx)
default: "Forgot your password?" email form — auth.forgot.tsx:93-117
screens:
- "Reset link sent" confirmation — trigger: sent=true — auth.forgot.tsx:66-70,76-92
- inline error notice — auth.forgot.tsx:74
overlays: none
tabs: none
data-driven: none
hidden: none

### /auth/locked (src/routes/auth.locked.tsx)
default: "Sign-in paused for 15 minutes" with Reset password, Contact support and Back to sign in — auth.locked.tsx:29-67
screens: none
overlays: none
tabs: none
data-driven: none
hidden: the whole route. Nothing in src navigates to /auth/locked, so you only get here by typing the URL — auth.locked.tsx:6

### /auth/maintenance (src/routes/auth.maintenance.tsx)
default: "Back in a few minutes" with a Check again button (reloads) and a status link to /help — auth.maintenance.tsx:24-46
screens: none
overlays: none
tabs: none
data-driven: none
hidden: the whole route. No inbound navigation exists — auth.maintenance.tsx:6

### /auth/offline (src/routes/auth.offline.tsx)
default: "No connection" with Try again, Browse cached Discover and Back to sign in — auth.offline.tsx:25-53
screens: none
overlays: none
tabs: none
data-driven: none
hidden: the whole route. No inbound navigation exists, and network errors show inline on /auth instead (auth.tsx:311-316) — auth.offline.tsx:6

### /auth/otp?phone= (src/routes/auth.otp.tsx)
default: "Enter your code" with six digit cells, Verify, a resend countdown, Change number and Use email — auth.otp.tsx:48-132
screens:
- subtitle without a number — trigger: phone="" — auth.otp.tsx:53-55
- "That code did not match" notice, always shown after Verify — trigger: tapping Verify with 6 digits — auth.otp.tsx:59,97-99
- "Resend code" link instead of the countdown — trigger: seconds<=0 — auth.otp.tsx:105-115
overlays: none
tabs: none
data-driven: none
hidden: a successful verification is impossible because Verify always sets the error — auth.otp.tsx:97-99. Resend only resets the timer — :110.

### /auth/phone (src/routes/auth.phone.tsx)
default: "Continue with phone" with +91 prefix, number field, "Preview mode" notice and Send code — auth.phone.tsx:29-73
screens: none
overlays: none
tabs: none
data-driven: none
hidden: none

### /auth/signout (src/routes/auth.signout.tsx)
default: "Sign out of MusicLinkd?" with Yes, sign out and Stay signed in — auth.signout.tsx:33-57
screens:
- busy label "Signing out…", then goes to /welcome — auth.signout.tsx:26-31,47
overlays: none
tabs: none
data-driven: none
hidden: the only entry point is the Sign out row on /settings (data/settings.ts:110), and /settings itself has no forward link (see /settings).

### /auth/success (src/routes/auth.success.tsx)
default: "You're in" with a next-steps list, Set up my profile and Skip for now — auth.success.tsx:25-58
screens: none
overlays: none
tabs: none
data-driven: none
hidden: only reachable when signUp returns a session immediately — auth.index.tsx:94-98

### /auth/verify-email?email= (src/routes/auth.verify-email.tsx)
default: "Check your email" with tips, Resend confirmation link, Use a different email and Sign in — auth.verify-email.tsx:52-106
screens:
- generic subtitle — trigger: email="" — auth.verify-email.tsx:62-66
- "Link sent again" success notice — trigger: sent=true — auth.verify-email.tsx:69-73
- Resend with no email goes to /auth?mode=signup — auth.verify-email.tsx:38-41
- resend error shows a toast — auth.verify-email.tsx:45-47
overlays: none
tabs: none
data-driven: none
hidden: none

### /help (src/routes/help.index.tsx)
default: search bar, "Popular right now" cards and a "Browse by topic" grid — help.index.tsx:63-112
screens:
- search results list — trigger: q non-empty with matches — help.index.tsx:43-55
- "No results" empty state — trigger: q non-empty with no matches — help.index.tsx:56-61
overlays: none
tabs: none
data-driven: popularSlugs profile-that-gets-booked, how-verification-works, booking-policies-explained — data/cms.ts:230-234. Categories getting-started, bookings, marketplace, trust, billing, legal — data/cms.ts:27-34.
hidden: none

### /help/category/$key (src/routes/help.category.$key.tsx)
default: list of guide rows for the category — help.category.$key.tsx:34-43
screens:
- "Nothing here yet" empty state — trigger: pagesInCategory(key) empty — help.category.$key.tsx:44-50. Reachable from the UI for marketplace and billing, which have no pages (data/cms.ts:30,32 vs page categories at :41,73,101,131,157,183,208).
- unknown key: title "Topic" plus the same empty state — help.category.$key.tsx:27,32
overlays: none
tabs: none
data-driven: key ∈ contentCategories — data/cms.ts:27-34. Pages: getting-started ×3, trust ×2, bookings ×1, legal ×1.
hidden: an unknown-key "Topic" view needs a typed URL — help.category.$key.tsx:32

### /help/$slug (src/routes/help.$slug.tsx)
default: article with kind pill, title, summary, content blocks (heading, para, list, callout, cta) and Related rows — help.$slug.tsx:48-125
screens:
- "This page has moved" not-found — trigger: getPage(slug) undefined — help.$slug.tsx:30-46
overlays: none
tabs: none
data-driven: slugs profile-that-gets-booked, pricing-your-first-service, how-verification-works, booking-policies-explained, review-policy, community-guidelines, whats-new — data/cms.ts:38,70,98,128,154,180,205
hidden: not-found needs a typed URL, since every related, popular and in-app slug exists (data/cms.ts:67-224). The null-return for a missing related page is also unreachable — help.$slug.tsx:109.

### /legal/privacy (src/routes/legal.privacy.tsx)
default: Privacy Policy summary with 5 section cards — legal.privacy.tsx:44-63
screens: none
overlays: none
tabs: none
data-driven: sections — legal.privacy.tsx:21-42
hidden: none

### /legal/terms (src/routes/legal.terms.tsx)
default: Terms of Use summary with 5 section cards — legal.terms.tsx:47-66
screens: none
overlays: none
tabs: none
data-driven: sections — legal.terms.tsx:24-45
hidden: none

### /notifications (src/routes/notifications.index.tsx)
default: timeline of notification cards with filter chips, "Mark all as read" and a settings icon — notifications.index.tsx:55-147
screens:
- "Nothing here" empty state — trigger: filtered list empty, reachable via Unread after Mark all as read — notifications.index.tsx:139-144
- subtitle "You are all caught up" — trigger: unreadCount=0 — notifications.index.tsx:59
overlays: none
tabs: chips All, Unread, Opportunities, Bookings, Messages, Account — notifications.index.tsx:30,72-78
data-driven: 8 seeded items, each with a `to` (for example /settings/security) — data/notifications.ts:45-120
hidden: none

### /notifications/settings (src/routes/notifications.settings.tsx)
default: channel explainer, a per-category push/email/SMS toggle card and a Quiet hours switch — notifications.settings.tsx:38-106
screens:
- quiet-hours off text — trigger: quiet=false — notifications.settings.tsx:99-101
overlays: none
tabs: none
data-driven: categories — data/notifications.ts:145-152
hidden: changes are local state only — notifications.settings.tsx:32-36

### /onboarding (src/routes/onboarding/index.tsx)
default: "Let's build your MusicLinkd profile." with 3 feature cards, a "Step 1 of N+2" badge, Let's start and Skip for now — onboarding/index.tsx:38-105
screens:
- "Continue setup" (links to /onboarding/resume) replaces Let's start — trigger: hydrated && completedSteps.length>0 — onboarding/index.tsx:86-89
overlays: none
tabs: none
data-driven: none
hidden: none

### /onboarding/step/$id (src/routes/onboarding/step.$id.tsx)
default: StepShell with header (back, group, "Step x of y", progress bar, Skip if optional), title, subtitle, "why" note, step body and a footer with Continue/"Review profile" and Finish later — step.$id.tsx:134-174, components/ml/onboarding.tsx:207-299
screens:
- loading skeleton — trigger: !hydrated — step.$id.tsx:83-89
- "This step isn't part of your setup" — trigger: unknown id or step not visible — step.$id.tsx:91-111
- "We couldn't save that" required-field banner (inline) — trigger: Continue while invalid (identity, role, location, visibility) — step.$id.tsx:145-149,158,177-190
- SyncBanner inline states: loading/saving (onboarding.tsx:695-700), offline (:702), error (:703-709), local (:711-717); idle and saved render nothing — set at onboarding.tsx:83,86,90,92,102,106,127,129,137
- after the last visible step goes to /onboarding/preview — step.$id.tsx:114-116,160
- 19 step bodies, one per id — step.$id.tsx:203-505
overlays: none
tabs: none
data-driven: steps from src/data/onboarding.ts
- identity "What should we call you?" — :514
- photo "Add a photo" — :529
- role "What do you do?" — :538
- secondary-roles "Anything else you do?" — :553
- instruments "What do you play?" — :568. CONDITIONAL visible() :574-577: primary or secondary role is musician, producer, composer or music-teacher (roles with instruments:true, :85-147).
- specialisation "Your specialisation" — :587. CONDITIONAL visible() :593: primaryRole ∈ singer, musician, producer, composer, sound-engineer, dj, music-teacher (:331-400).
- genres "What music do you work with?" — :603
- languages "Which languages do you work in?" — :612
- location "Where are you based?" — :627
- work-mode "How do you work?" — :642
- availability "What are you available for?" — :657
- intent "What brings you to MusicLinkd?" — :672
- experience "How experienced are you?" — :687
- bio "Tell people about yourself" — :702
- links "Where else can people hear you?" — :717
- contact "How can people reach you?" — :732
- discovery "What would you like to discover?" — :747
- visibility "Who can see your profile?" — :762
- business "Do you represent a business?" — :777
- band "Are you in a band or crew?" — :792
Required (optional:false): identity, role, location, visibility.
hidden:
- "Nothing extra needed for your role" body is unreachable, because visible() already hides specialisation when there is no spec — step.$id.tsx:282-283
- the StepBody default null can't render, because unknown ids are caught at :91 — step.$id.tsx:507
- the "not part of your setup" view needs a typed or stale URL — step.$id.tsx:91

### /onboarding/resume (src/routes/onboarding/resume.tsx)
default: "Welcome back" with "finished X of Y", a Next up card with progress, Continue setup and Start over — resume.tsx:43-101
screens:
- SyncBanner inline states (loading until hydrated, then sync) with a custom local note — resume.tsx:46-50
- fallback text "Review your profile" — trigger: resumeTarget id not found — resume.tsx:62-65. resumeTarget always returns a step id (onboarding.tsx:197-201).
overlays: none
tabs: none
data-driven: none
hidden: the fallback at resume.tsx:55,62,64 can't show, because resumeTarget returns "visibility" at worst (onboarding.tsx:200)

### /onboarding/preview (src/routes/onboarding/preview.tsx)
default: profile preview card plus 8 editable rows (Roles, Instruments, Genres, Languages, Available for, Work mode, Links, Visibility), Publish my profile and Finish later — preview.tsx:40-114
screens:
- per-row "Not added — this section stays hidden." — preview.tsx:141-145
- initial letter instead of photo — preview.tsx:52-58
overlays: none
tabs: none
data-driven: Edit targets role, instruments, genres, languages, availability, work-mode, links, visibility — preview.tsx:83-97
hidden: Edit on Instruments goes to a step that may not be visible, which lands on "not part of your setup" — preview.tsx:85, step.$id.tsx:91

### /onboarding/complete (src/routes/onboarding/complete.tsx)
default: "Profile published" hero with strength bar, 6 ReadinessCards, Next steps handoff cards and Finish — complete.tsx:58-166
screens:
- Business handoff card — trigger: d.businessIntent — complete.tsx:123-134
- Band handoff card — trigger: d.bandIntent — complete.tsx:135-146
- profile saved once if signed in; no UI change — complete.tsx:47-55
overlays: none
tabs: none
data-driven: readiness items — data/onboarding.ts:821-866
hidden: none

### /onboarding/done (src/routes/onboarding/done.tsx)
default: "Welcome aboard, {first}" with summary lines, 3 What next links and Go to my home — done.tsx:41-91
screens:
- summary list omitted — trigger: no role, city, genres or availability — done.tsx:54-62
overlays: none
tabs: none
data-driven: none
hidden: none

### /onboarding/skip (src/routes/onboarding/skip.tsx)
default: "We've saved your progress" with Still missing list, Continue setup and Explore MusicLinkd first — skip.tsx:37-71
screens: none
overlays: none
tabs: none
data-driven: readiness — data/onboarding.ts:821-866
hidden: none

### /plans (src/routes/plans.index.tsx)
default: current-plan card, Monthly/Yearly toggle, 3 selectable plan cards, CTA, and Earnings and help rows — plans.index.tsx:35-137
screens:
- "This is your current plan" button — trigger: selected === currentSubscription.planKey — plans.index.tsx:114-122
- "2 months free" line — trigger: Yearly and a paid plan — plans.index.tsx:85-87
overlays: none
tabs: Monthly / Yearly — plans.index.tsx:29,55
data-driven: plans free, pro, business — data/monetization.ts:21,37,54. Current plan is pro — :72.
hidden: "This is your current plan" Btn has no onClick — plans.index.tsx:119

### /plans/checkout?plan= (src/routes/plans.checkout.tsx)
default: plan card, billing-cycle radio, payment method, summary and a Confirm button — plans.checkout.tsx:35-110
screens:
- "Processing…" button label — trigger: state=processing — plans.checkout.tsx:101,105
overlays:
- "{Plan} is active" Dialog — opened by: Confirm {plan} (after 900ms) — plans.checkout.tsx:112-124
- "Payment could not be completed" Dialog — opened by: nothing — plans.checkout.tsx:126-132
tabs: monthly / yearly radio — plans.checkout.tsx:51-68
data-driven: plan ∈ free, pro, business (invalid values fall back to pro) — plans.checkout.tsx:8-12
hidden:
- failed Dialog: state "failed" is never set — plans.checkout.tsx:33,126
- "Change payment method" row onClick does nothing — plans.checkout.tsx:76
- plan=pro (the current plan) needs a typed URL, since /plans hides the CTA for the current plan — plans.index.tsx:114

### /plans/manage (src/routes/plans.manage.tsx)
default: plan status card, usage meters, boosts, invoices, Earnings row, Change plan and Cancel subscription — plans.manage.tsx:36-123
screens: none
overlays:
- "Why are you cancelling?" Sheet — opened by: Cancel subscription — plans.manage.tsx:119,125-141
- "Subscription cancelled" Dialog — opened by: picking any reason — plans.manage.tsx:131-133,143-150
tabs: none
data-driven: cancelReasons, entitlements, boosts, invoices — data/monetization.ts
hidden: the "Past due" pill never shows because status is fixed "active" — plans.manage.tsx:50, data/monetization.ts:73

### /plans/earnings (src/routes/plans.earnings.tsx)
default: net earnings card with lines, payout account card and link rows — plans.earnings.tsx:24-90
screens: none
overlays: none
tabs: none
data-driven: earnings and payoutAccount — data/monetization.ts:126
hidden:
- "Change payout account" Btn has no onClick — plans.earnings.tsx:70
- "Pending" and "Missing" pills never show because state is fixed "verified" — plans.earnings.tsx:57-64, data/monetization.ts:126

### /settings (src/routes/settings.index.tsx)
default: 4 groups of link rows (Account, Work, Privacy, Support) and version footer — settings.index.tsx:24-58
screens: none
overlays: none
tabs: none
data-driven: settingsGroups — data/settings.ts:12-112
hidden: no forward link to /settings anywhere in src. It's reachable only through the back button of /settings/account, /privacy or /security — settings.account.tsx:30, settings.privacy.tsx:31, settings.security.tsx:31

### /settings/account (src/routes/settings.account.tsx)
default: account field list (Email, Phone, Username, Language, Currency) with action buttons — settings.account.tsx:28-61
screens: none
overlays:
- "Change {field}" Sheet — opened by: each row's Change/Edit — settings.account.tsx:47-53,63-85
tabs: none
data-driven: accountFields — data/settings.ts:115-133
hidden: only linked from /settings (data/settings.ts:27). "Send confirmation" just closes the sheet — settings.account.tsx:81.

### /settings/privacy (src/routes/settings.privacy.tsx)
default: privacy control chip cards, Blocked people, Your data with Request a copy and Delete account — settings.privacy.tsx:29-74
screens: none
overlays: none
tabs: none
data-driven: privacyControls — data/settings.ts:135-164
hidden: "Request a copy" Btn has no onClick — settings.privacy.tsx:65

### /settings/security (src/routes/settings.security.tsx)
default: security link rows, signed-in sessions list and Sign out everywhere else — settings.security.tsx:29-82
screens:
- per-session Sign out removes the row (not a full view) — settings.security.tsx:65-73
overlays:
- "Sign out other devices?" Dialog — opened by: Sign out everywhere else — settings.security.tsx:76,84-104
tabs: none
data-driven: securityRows go to /auth/forgot, /auth/phone and /settings/delete-account — data/settings.ts:178-196. sessions — :166.
hidden: none

### /settings/delete-account (src/routes/settings.delete-account.tsx)
default: warning notice, Deleted/Kept lists, "Type DELETE" input, Delete my account and Keep my account — settings.delete-account.tsx:41-97
screens:
- Delete enabled — trigger: input is DELETE; tapping it shows a toast only — settings.delete-account.tsx:83-88
overlays: none
tabs: none
data-driven: removed/kept lists — settings.delete-account.tsx:25-35
hidden: there is no deletion or success view; the button only fires a toast — settings.delete-account.tsx:84-88

CROSS-CUTTING
- No screen-like component in the files read goes unrendered. OfflineBanner, SavingBanner and ErrorBanner render through SyncBanner (components/ml/onboarding.tsx:642-719). StepSkeleton, LocationHint, ReadinessCard and HandoffCard are all used.
- Three whole routes have no inbound navigation anywhere in src: /auth/locked (auth.locked.tsx:6), /auth/maintenance (auth.maintenance.tsx:6), /auth/offline (auth.offline.tsx:6).
- Exports never imported anywhere (grep shows only their definitions):
  - `supportTopics` — data/settings.ts:199
  - `screenContracts` — data/onboarding.ts:873
  - `stepIndex` — data/onboarding.ts:806
  - `countries` — data/onboarding.ts:302

## Discover, search, profile, feed

### /band (src/routes/band.tsx)
default: none. It redirects to /bands (band.tsx:8-10)
screens: none
overlays: none
tabs: none
data-driven: none
hidden: nothing in src links to /band; the only way in is typing the URL (band.tsx:7)

### /categories (src/routes/categories.tsx)
default: 2-column grid of category tiles
screens: none
overlays: none
tabs: none
data-driven: none. Tiles come from `categories` (data/discover.ts:199-209)
hidden: every tile except Singers goes to plain /search, not a category-specific result (categories.tsx:32)

### /feed (src/routes/feed.tsx)
default: dashboard with greeting, headline update, Continue, Opportunities, For you, Activity and Manage
screens:
- Offline — trigger: `navigator.onLine === false` or the window `offline` event — feed.tsx:65,70,103-110
- Loading skeleton — trigger: status "loading" — feed.tsx:102,431
- Error ("Couldn't load your dashboard") — trigger: status "error" — feed.tsx:111-118
- Empty network (quiet copy plus "No activity yet") — trigger: no headline, no Continue items and no activity — feed.tsx:166,177,286-297
- Signed-in vs signed-out header: the greeting shows a first name only when signed in (feed.tsx:174); the avatar links to /welcome when signed out and /me when signed in (viewer.tsx:78-98)
overlays: none
tabs: none
data-driven: none
hidden:
- status starts at "ready" and is only ever set to "ready" or "offline", so the loading and error screens can't be reached (feed.tsx:61,65,70)
- the empty-network screen can't be reached: data is always `dashboardData`, and `emptyDashboard` is defined but never used (feed.tsx:62; data/dashboard.ts:157)
- the Activity "Accept" button does nothing (feed.tsx:278)

### /me (src/routes/me.tsx)
default: `PersonProfileExperience` in owner mode (me.tsx:66)
screens:
- Loading skeleton — trigger: auth loading, or signed in with state idle/loading — me.tsx:50-62
- Signed out: shows the demo owner profile (`ownerProfile`, rahul-iyer) in owner mode — me.tsx:65; data/profile.ts:655
- Signed in: your own record, with empty states wherever it holds nothing — me.tsx:65
- Load error: no separate screen. It falls through to the signed-in view built from an empty row — me.tsx:47,65
- Owner variant, full breakdown:
  - hero has no back link and no ⋯ More button — person-profile.tsx:79,81
  - "Visibility: X" line when privacy isn't Public — profile.tsx:288
  - owner action bar: Edit, Share, Stats, Public — person-profile.tsx:163-190
  - OwnerPanel: completion card plus 11 links — person-profile.tsx:193-213
  - Stats expands an inline insights panel — person-profile.tsx:215-236
  - empty states show owner CTAs (Write a summary, Add audio, Add track, Add video, Add images, Add project, Add release, Add experience, Add a service) — person-profile.tsx:297-514
- Tab bodies: Overview (person-profile.tsx:264), Portfolio (370), Projects (433), Services (502), About (522)
overlays:
- Share profile sheet — opened by: hero share icon (profile.tsx:226) or owner "Share" (person-profile.tsx:173) — profile.tsx:635
- HireModal "Contact professional", then "Request sent" — opened by: service "Enquire" (profile.tsx:458), in Overview "Available to work" and the Services tab — person-profile.tsx:132; hire-modal.tsx:32-50
tabs: Overview, Portfolio, Projects, Services, About (person-profile.tsx:56)
data-driven: none
hidden:
- owner empty-state CTAs have no `onCta`, so tapping them does nothing (profile.tsx:64; person-profile.tsx:297 and following)
- "Public" always opens rahul-iyer, whoever is signed in (person-profile.tsx:183)
- "Enquire" opens the hire sheet on your own profile (person-profile.tsx:125,362)
- ShareSheet "Share card", "Save contact" and "Download QR" do nothing (profile.tsx:682-684)
- external links are disabled buttons (profile.tsx:586)

### /messages (src/routes/messages.tsx)
default: Collaborations and Share work links, then 4 conversation rows
screens: none
overlays: none
tabs: none
data-driven: none
hidden: conversation rows are plain divs and don't open a thread (messages.tsx:60)

### /recently-viewed (src/routes/recently-viewed.tsx)
default: People and Studios lists
screens:
- Empty ("Nothing here yet") — trigger: no people and no spaces — recently-viewed.tsx:26,33-39
overlays: none
tabs: none
data-driven: none
hidden:
- the empty state can't be reached because the lists are fixed non-empty slices (recently-viewed.tsx:24-26)
- "Clear history" does nothing (recently-viewed.tsx:95)
- studio cards aren't links (recently-viewed.tsx:77)
- the only link in is from /search/history, which is itself unreachable (see below)

### /recommended (src/routes/recommended.tsx)
default: none. It redirects to /recommendations/swipe (recommended.tsx:8-10)
screens: none
overlays: none
tabs: none
data-driven: none
hidden: nothing in src links to /recommended

### /saved (src/routes/saved.tsx)
default: shortlist cards with a note, tags and actions
screens: none. There's no empty state.
overlays: none
tabs: Talent, Bands, Studios, Businesses, Events (saved.tsx:25)
data-driven: none
hidden:
- only Studios shows different content; Talent, Bands, Businesses and Events all show the same talent list (saved.tsx:41,57)
- Message, Share and ✕ do nothing (saved.tsx:109-115)

### /singers (src/routes/singers.tsx)
default: singer list (TalentRow)
screens:
- Grid view — trigger: ▦ toggle sets `grid` to true — singers.tsx:51,87-92
overlays:
- Refine search FilterSheet — opened by: Vocal range / Language / City / Availability — singers.tsx:79,101; filter-sheet.tsx:32
tabs: List/Grid toggle (☰ / ▦) (singers.tsx:44-57)
data-driven: none
hidden:
- genre chips toggle but don't filter the list (singers.tsx:69, list at 89/95)
- there's no `onPreview`, so tapping a row's header does nothing (singers.tsx:96; cards.tsx:11)
- TalentRow "Message" and ♡ do nothing (cards.tsx:56,59)
- FilterSheet "Apply" only closes the sheet (filter-sheet.tsx:72)

### /studio (src/routes/studio.tsx)
default: hard-coded Yash Raj Sound Studios page
screens: none
overlays:
- HireModal (form, then "Request sent") — opened by: "Book studio" — studio.tsx:152,163; hire-modal.tsx:32
tabs: none
data-driven: none
hidden:
- "Message" and "Share" do nothing (studio.tsx:155,158)
- calendar days can't be tapped (studio.tsx:130)

### /discover/community (src/routes/discover.community.tsx)
default: Bands and crews list plus Studios and labels rows
screens:
- Empty bands ("No bands yet") — trigger: `bands.length === 0` — discover.community.tsx:57
overlays: none
tabs: none
data-driven: none
hidden:
- nothing in src links to this route (it only has its own createFileRoute, :7)
- the empty state can't be reached because the `bands` seed isn't empty (data/band.ts:76)

### /discover/music (src/routes/discover.music.tsx)
default: "Fresh this week" card, a work list and trending searches
screens:
- Empty ("Nothing published yet") — trigger: no audio/video items — discover.music.tsx:61
overlays: none
tabs: none
data-driven: none
hidden:
- nothing in src links to this route
- the empty state can't be reached because the portfolio has a "video" item (data/portfolio.ts:59); no item has kind "audio"
- trending search chips can't be tapped (discover.music.tsx:71)

### /discover/nearby (src/routes/discover.nearby.tsx)
default: radius chips, "Studios around you" and "People around you"
screens:
- Location permission gate ("Show people near you") — trigger: `allowed === false`, which is the initial state — discover.nearby.tsx:29,31-55
- Empty studios ("Nothing within this radius") — trigger: `studios.length === 0` — discover.nearby.tsx:92
overlays: none
tabs: none
data-driven: none
hidden:
- nothing in src links to this route
- "Use my location" just flips `allowed` and never asks for geolocation (discover.nearby.tsx:46)
- radius chips don't change results (discover.nearby.tsx:64)
- the empty state can't be reached because studios is a fixed non-empty seed
- studio cards aren't links (discover.nearby.tsx:75)

### /discover/projects (src/routes/discover.projects.tsx)
default: list of opportunities with kind Project or Collaborator
screens:
- Empty ("No open projects", CTA "Start your own") — trigger: no matching opportunities — discover.projects.tsx:52-57
overlays: none
tabs: none
data-driven: none
hidden:
- nothing in src links to this route
- the empty state can't be reached because a "Project" opportunity exists (data/opportunities.ts:274)

### /profile/$username (src/routes/profile.$username.tsx)
default: `PersonProfileExperience` in visitor mode (profile.$username.tsx:53)
screens:
- Redirect to /me — trigger: username "me" — profile.$username.tsx:12
- Not found (root 404, "Go home") — trigger: no real row and no seed profile — profile.$username.tsx:16; __root.tsx:17,118
- Root error ("This page didn't load") — trigger: the loader throws something other than the caught fetch error — __root.tsx:39,119
- Real profile with no seed: shows only its own data, with empty states elsewhere — profile.$username.tsx:46-48
- Visitor variant:
  - hero has a back link to /discover and a ⋯ More button — person-profile.tsx:79,81
  - action bar: Connect, Message, Hire, Follow — person-profile.tsx:141-160
  - no OwnerPanel, and empty states show no CTAs
- Tab bodies: same five as /me (person-profile.tsx:110-127)
overlays:
- Share profile sheet — opened by: hero share icon — profile.tsx:226,635
- More sheet (Copy profile link, Save contact, Add to shortlist, Restrict, Block, Report profile, Report content) — opened by: ⋯ — profile.tsx:235,711
- HireModal (form, then "Request sent") — opened by: "Hire" (person-profile.tsx:153) or "Enquire" (profile.tsx:458)
tabs: Overview, Portfolio, Projects, Services, About (person-profile.tsx:56)
data-driven:
- rahul-iyer (data/profile.ts:223)
- ananya-sharma (data/profile.ts:533)
- linked seeds: sunsetrecords, kabirmenon, thequarter, meeraraghav, northlightfilms, rheadsouza, arjunpillai, tarabose, kabir-nair, octave-sound-labs, sanya-rao (data/linked-profiles.ts:83-2036)
- talents: ananya-sharma, rahul-iyer, meher-kaur, dj-noor, arjun-menon, tanvi-rao (data/discover.ts:29-104)
- singers: zaid-qureshi, lisa-dsouza, harpreet-singh, nithya-balan (data/discover.ts:125-170)
- any real username from `getPublicProfile`
hidden:
- owner mode is never used here, even for your own username (profile.$username.tsx:53)
- Connect and Follow do nothing (person-profile.tsx:144,156)
- every More sheet item only closes the sheet (profile.tsx:719)
- ShareSheet secondary buttons do nothing (profile.tsx:682-684)
- external links are disabled (profile.tsx:586)

### /profile/edit (src/routes/profile.edit.tsx)
default: accordion editor with a sticky save bar
screens:
- Signed out ("Sign in to edit your profile") — trigger: state "signed-out" — profile.edit.tsx:121-123,176-193
- Loading skeleton — trigger: state "loading", which is the initial state — profile.edit.tsx:104,195-206
- Load error ("We couldn't load your profile", Retry) — trigger: state "error" — profile.edit.tsx:116,208-225
- Save bar messages, inline and not a full replacement: saving (:471), saved (:464), error (:456), offline (:460)
overlays: none
tabs: none. Accordion sections: Identity, Professional, Music, Location & availability, Links, Privacy (profile.edit.tsx:234-389)
data-driven: none
hidden: none

### /profile/insights (src/routes/profile.insights.tsx)
default: metric tiles, Engagement, audience cities and "Get seen more"
screens: none. There's no signed-out, loading or empty handling, and it always uses `ownerProfile` (profile.insights.tsx:28)
overlays: none
tabs: none
data-driven: none
hidden: nothing in src links to /profile/insights; the /me "Stats" button opens an inline panel instead (person-profile.tsx:177,215)

### /recommendations/settings (src/routes/recommendations.settings.tsx)
default: signal toggles, Reset card and links (no bottom nav)
screens: none
overlays: none
tabs: none
data-driven: none
hidden:
- "Clear recommendation feedback" does nothing (recommendations.settings.tsx:65)
- toggles are local state only (recommendations.settings.tsx:25)

### /recommendations/swipe (src/routes/recommendations.swipe.tsx)
default: 4-card deck with Skip, Save and Connect
screens: none. The deck loops, so there's no end-of-deck or empty state (recommendations.swipe.tsx:41)
overlays: none
tabs: none
data-driven: none
hidden:
- category pills are static spans; Singers is always active (recommendations.swipe.tsx:57-66)
- there's no drag gesture, only buttons
- Save and Connect both just advance the deck (recommendations.swipe.tsx:94,100)

### /search/history (src/routes/search.history.tsx)
default: list of recent searches
screens:
- Empty ("Nothing here yet") — trigger: every entry removed, or a tab with no entries — search.history.tsx:87-96
- Cleared ("History cleared") — trigger: `cleared` is true after confirming — search.history.tsx:89,114
overlays:
- Dialog "Clear search history?" — opened by: "Clear all" — search.history.tsx:48-54,106
tabs: All, Talent, Studios, Market, Opportunities (search.history.tsx:25)
data-driven: none
hidden:
- the only link in is from /search/saved, which is only linked from here, so neither page can be reached from the rest of the app
- history rows open /search without the query (search.history.tsx:70)

### /search/saved (src/routes/search.saved.tsx)
default: saved search cards with alert toggle, Run and Delete
screens:
- Empty ("No saved searches") — trigger: every card deleted — search.saved.tsx:103-112
overlays: none
tabs: none
data-driven: none
hidden:
- only linked from /search/history (search.history.tsx:98), which is unreachable
- "Run search" opens /search without the saved filters (search.saved.tsx:88)

### /talent/$id (src/routes/talent.$id.tsx)
default: none. It redirects to /profile/$username (talent.$id.tsx:8-14)
screens: none
overlays: none
tabs: none
data-driven: none (id passed straight through)
hidden: nothing in src links to /talent/$id

### /discover (src/routes/discover.index.tsx)
default: header with search, quick categories, Featured, Trending, Recommended and Nearby
screens:
- Signed-in vs signed-out avatar — trigger: `useViewer().signedIn` — viewer.tsx:78-98
overlays: none
tabs: none
data-driven: none
hidden:
- the 🔔 button does nothing (discover.index.tsx:39)
- trending cards aren't links (discover.index.tsx:83)
- every quick-category tile goes to /categories (discover.index.tsx:56)

### /recommendations (src/routes/recommendations.index.tsx)
default: recommendation sections with Open, Save and Hide
screens:
- Loading — trigger: `loading` is true — recommendations.index.tsx:79
- Empty ("Not enough to go on yet") — trigger: no visible items for the current filter, reached by hiding them — recommendations.index.tsx:81-89; data/recommendations.ts:136
overlays:
- Sheet "Tell us why" — opened by: ⋯ "Recommendation feedback" — recommendations.index.tsx:122-129,171
tabs: All, People, Spaces, Gear, Work, Reading (recommendations.index.tsx:32)
data-driven: none
hidden:
- `loading` is a constant `useState(false)` with no setter, so the loading screen can't be reached (recommendations.index.tsx:47)
- "Save" does nothing (recommendations.index.tsx:140)

### /search (src/routes/search.index.tsx)
default: sticky search header with chips, 248-results line and TalentRow list
screens: none. There's no no-results, loading or error state.
overlays:
- Refine search FilterSheet — opened by: "⚙ Filters" — search.index.tsx:67,74
- Profile preview Sheet — opened by: tapping a row's header — search.index.tsx:76; cards.tsx:9-11
- HireModal (form, then "Request sent") — opened by: preview "Hire" — search.index.tsx:171,88
tabs: none
data-driven: none
hidden:
- chips have no `onClick` (search.index.tsx:49-53)
- typing a query doesn't filter anything (search.index.tsx:47,62)
- preview "Message" and "♡ Save" do nothing (search.index.tsx:169-170)
- row "Message" and ♡ do nothing (cards.tsx:56,59)
- FilterSheet "Apply" only closes the sheet (filter-sheet.tsx:72)

### /search/why/$id (src/routes/search.why.$id.tsx)
default: explanation page with signals, never-used list, feedback chips and links
screens:
- Default explanation — trigger: any id other than "rec-1" — data/search-meta.ts:134,157
overlays:
- Dialog "Thanks — noted" — opened by: any feedback chip — search.why.$id.tsx:90,111
tabs: none
data-driven: "rec-1" (data/search-meta.ts:116); every other id falls back to `defaultWhy`, with no not-found state
hidden: nothing in src links to /search/why; the only way in is typing the URL

CROSS-CUTTING
- `ProfileSkeleton` loading screen is defined but never rendered anywhere (profile.tsx:72)
- `emptyDashboard` (the data behind the feed's empty state) is defined but never used (data/dashboard.ts:157)
- `StateBlock` error variant and `SkeletonBody` are rendered in code, but no status ever reaches them (feed.tsx:111,431)
- `ProfileEmpty` CTAs never get `onCta` from any caller (profile.tsx:50,64)
- Seven routes have no link in from anywhere in src: /discover/community, /discover/music, /discover/nearby, /discover/projects, /profile/insights, /search/why/$id, and the legacy redirects /band, /recommended and /talent/$id. /search/history and /search/saved only link to each other.

## Collaboration

### /collab (src/routes/collab.index.tsx)
default: Collaborations list with search, status chips, Filters/Sort buttons, ProjectCards, and a floating "Start a collaboration" button.
screens:
- Loading skeleton — trigger: sim="loading" (the "States" button cycles ok→loading→error→offline→empty) — collab.index.tsx:202-203
- Error — trigger: sim="error"; Retry sets it back to ok — collab.index.tsx:204-205
- Offline — trigger: sim="offline"; a banner above the normal list — collab.index.tsx:152
- Empty (simulated) — trigger: sim="empty" makes the list [] and shows EmptyForTab — collab.index.tsx:66,206-207
- Empty, Active tab — trigger: tab=active with 0 results (search or filters) — collab.index.tsx:283-297
- Empty, Completed tab — trigger: tab=completed with 0 results — collab.index.tsx:298-304
- Empty, Drafts tab — trigger: tab=drafts with 0 results — collab.index.tsx:305-312
- Pending tab — trigger: tab=pending; adds an "Invitations for you" and "Proposals" section above the list — collab.index.tsx:154-200
- Pending tab list fallback ("No collaborations yet") — trigger: tab=pending; always empty because tabFilter.pending=["proposed"] and no project has that status — collab.index.tsx:313-319; data/collab.ts:1206
overlays:
- Filter collaborations (Sheet) — opened by: "Filters" — collab.index.tsx:136,220-260
- Sort by (Sheet) — opened by: sort button (shows the current sort label) — collab.index.tsx:144,262-277
tabs: Active, Pending (· count), Completed, Drafts — collab.index.tsx:46-51
data-driven: tab→status map: active=[active,onHold], pending=[proposed], completed=[completed,archived], drafts=[draft] — data/collab.ts:1204-1209. Sort options — data/collab.ts:276
hidden:
- "No pending collaboration invitations" empty state can't show because invitations has 2 entries — collab.index.tsx:157-161; data/collab.ts:1014
- Cancelled projects (neon-bazaar) never appear in any tab — data/collab.ts:1204-1209, 952
- Only one proposal is linked, hardcoded to prop-mix — collab.index.tsx:188

### /collab/$id (src/routes/collab.$id.index.tsx)
default: Workspace Overview tab: artwork, tags, description, pinned note, progress, people, 3 milestones, 3 files, 4 activity items, and Messages/"Post an update" buttons.
screens:
- No access / not found (PermissionState) — trigger: unknown id — collab.$id.index.tsx:43-52
- On-hold banner (added on top of the default view) — trigger: status=onHold and holdReason (aftertaste-score) — :59-66
- Completed banner — trigger: status=completed and completedOn (sunday-drive) — :72-79
- Cancelled banner — trigger: status=cancelled and cancelledReason — :67-71
- "All milestones done" progress text — trigger: no milestone left that isn't done — :112-114
overlays: none
tabs: Workspace tabs: Overview, Discussion, Files, Timeline, People, Notes, Activity — collab-workspace.tsx:7-15
data-driven: project ids raat-ki-rani, monsoon-sessions, aftertaste-score, sunday-drive, untitled-collab, neon-bazaar, chai-tapes — data/collab.ts:361-1012
hidden:
- Cancelled banner: neon-bazaar is the only cancelled project and no list or link reaches it — :67-71; data/collab.ts:952
- MilestoneRow's check button: onToggle is never passed, so it does nothing — collab.tsx:346-348; collab.$id.index.tsx:133

### /collab/$id/activity (src/routes/collab.$id.activity.tsx)
default: Workspace Activity tab with the full activity log.
screens:
- No access (PermissionState) — trigger: unknown id — :33-42
- Empty "No activity yet" — trigger: activity.length===0 (untitled-collab) — :46-50
overlays: none
tabs: Workspace tabs — collab-workspace.tsx:7-15
data-driven: none
hidden: none

### /collab/$id/agreement (src/routes/collab.$id.agreement.tsx)
default: Working agreement card (scope, deliverables, timeline, compensation, ownership, credit), roles covered, and View roles / Project settings buttons. It renders under the Overview tab.
screens:
- Deleted/unavailable (DeletedState) — trigger: unknown id — :34-43
- "No agreement recorded yet" — trigger: !project.agreement (every project except raat-ki-rani) — :50-57; data/collab.ts:618
overlays: none
tabs: Workspace tabs, with Overview highlighted — :48
data-driven: none
hidden: none. Reached from Settings "Working agreement" and Roles "View agreement".

### /collab/$id/discussion (src/routes/collab.$id.discussion.tsx)
default: Workspace Discussion tab: filter chips, scope note, post cards with replies, and a composer.
screens:
- No access (PermissionState) — trigger: unknown id — :41-50
- Empty "Nothing here yet" — trigger: filtered entries.length===0 — :73-77
overlays: none
tabs: filter chips All / Decisions / Pinned / Files — :33,61-67 (plus Workspace tabs)
data-driven: none
hidden:
- "Attach file" has no handler — :135-137
- "Post update" only clears the draft — :138

### /collab/$id/invite-link (src/routes/collab.$id.invite-link.tsx)
default: "Invite collaborator": link card, role chips, active/approve switches, Disable link, and a link to People.
screens:
- Deleted (DeletedState) — trigger: unknown id — :40-49
- Link disabled (strikethrough link text) — trigger: active=false via switch or "Disable link" — :58-60,82,91
overlays: none
tabs: none
data-driven: role chips from collabRoles — data/collab.ts:59
hidden:
- "Copy link" and "Share" have no handlers — :65-68

### /collab/$id/media (src/routes/collab.$id.media.tsx)
default: Shared media grid of MediaTiles with filter chips. It renders under the Files tab.
screens:
- Deleted (DeletedState) — trigger: unknown id — :54-63
- Empty "Nothing shared here yet" — trigger: filtered list.length===0 — :76-85
- Unavailable tile overlay — trigger: file.available=false — collab-extra.tsx:38-42
overlays: none
tabs: All / Audio / Video / Images / Documents — :34-40 (plus Workspace tabs, Files highlighted)
data-driven: none
hidden: none. Reached only from Files "Media gallery" — collab.$id.files.index.tsx:68.

### /collab/$id/notes (src/routes/collab.$id.notes.tsx)
default: Workspace Notes tab: notes list with pinned notes first, and a new-note composer.
screens:
- No access (PermissionState) — trigger: unknown id — :38-47
- Empty "No notes yet" — trigger: notes.length===0 (aftertaste, sunday-drive, untitled and others) — :53-57
overlays: none
tabs: Workspace tabs
data-driven: none
hidden:
- "Save note" only clears the draft — :91

### /collab/$id/participants (src/routes/collab.$id.participants.tsx)
default: Workspace People tab: active list, invited list, declined/left list, roles card, and invite/share links.
screens:
- No access (PermissionState) — trigger: unknown id — :39-48
- "No collaborators yet" card — trigger: active.length===0 (untitled-collab) — :58-67
- Invited section — trigger: pending.length>0 (monsoon-sessions) — :84-101
- Declined or left section — trigger: past.length>0 — :103-112
- Manager view (Remove buttons) — trigger: myRole==="Composer" or owner==="You", and the row isn't the owner — :50; collab.tsx:173-181
overlays:
- MusicLinkd role taxonomy (Sheet) — opened by: "See the role taxonomy" — :120-126,158-168
tabs: Workspace tabs
data-driven: none
hidden:
- owner==="You" never matches any data (owners are full names) — :50
- Chips inside the taxonomy sheet have no onClick — :160-162

### /collab/$id/roles (src/routes/collab.$id.roles.tsx)
default: Roles on this project list, taxonomy note, and Manage people / View agreement buttons. It renders under the People tab.
screens:
- Deleted (DeletedState) — trigger: unknown id — :38-47
- "No roles assigned yet" — trigger: participants.length===0 (untitled-collab) — :52-58
overlays: none
tabs: Workspace tabs, with People highlighted — :50
data-driven: none
hidden: none

### /collab/$id/settings (src/routes/collab.$id.settings.tsx)
default: Project settings: visibility chips, notification switches, credits, project links (Roles, Agreement, Invite link, Transfer), and lifecycle links.
screens:
- No access (PermissionState) — trigger: unknown id — :44-53
- Agreement summary card — trigger: project.agreement exists — :128-161
overlays: none
tabs: none (the visibility chips only change the hint text — :60-73)
data-driven: lifecycle links pass action values hold, complete, archive, leave, cancel — :165-194
hidden: none. The same five lifecycle links show for every status.

### /collab/$id/share (src/routes/collab.$id.share.tsx)
default: Share project: link card, "Who can open it" chips, and three switches.
screens:
- No access (PermissionState) — trigger: unknown id — :37-46
overlays: none
tabs: none
data-driven: none
hidden:
- "Copy link" and "Share" have no handlers — :58-61
- The "Link is active" switch doesn't change anything else shown — :76

### /collab/$id/timeline (src/routes/collab.$id.timeline.tsx)
default: Workspace Timeline tab: project window card, Milestones/Tasks toggle, milestone list, and "Add milestone".
screens:
- No access (PermissionState) — trigger: unknown id — :35-44
- Tasks view — trigger: view="Tasks" — :84-130
- Empty "No milestones yet" — trigger: Milestones view with milestones.length===0 — :64-73
- Empty "No tasks yet" — trigger: Tasks view with tasks.length===0; the "0 tasks" label still renders below — :86-92
overlays: none
tabs: Milestones / Tasks — :56-61
data-driven: none
hidden:
- MilestoneRow check buttons do nothing (no onToggle) — :79; collab.tsx:346-348

### /collab/$id/transfer (src/routes/collab.$id.transfer.tsx)
default: Transfer ownership: current owner, new-owner chips, ConfirmPanel, and a Cancel/Transfer bar.
screens:
- Deleted (DeletedState) — trigger: unknown id — :40-49
- No eligible participant card — trigger: eligible.length===0 (untitled-collab) — :72-78
- Success "Ownership transferred" — trigger: done=true via "Transfer" — :55-63,112
overlays: none
tabs: none
data-driven: none
hidden:
- "Transfer" is still enabled with no eligible participant; the success text then has an empty name — :37,112

### /collab/invites/$id (src/routes/collab.invites.$id.tsx)
default: Invitation detail: artwork, tags, inviter card, expectations, question field, and a Decline / "Accept as …" bar.
screens:
- No access (PermissionState) — trigger: unknown invitation id — :45-54
overlays: none
tabs: none
data-driven: invitation ids inv-kalank and inv-podcast — data/collab.ts:1016,1032
hidden:
- The question field isn't used; "Send question in Connections" just links to /messages — :102-112

### /collab/new/collaborators (src/routes/collab.new.collaborators.tsx)
default: Step 2 of 2 "Add collaborators": search, facets, candidate rows with Invite/Invited, invite-by-link card, and a sticky bar.
screens:
- Empty "No one matches that search" — trigger: list.length===0 — :78-82
- Invited state per row ("Role: X · change", "Invited") — trigger: invited[c.id] is set — :98-122
overlays:
- Select role (Sheet) — opened by: "Invite" or "Role: … · change" — :101,118,160-182
tabs: facets All / People / Businesses / Verified — :29,70-74
data-driven: candidates list — data/collab.ts:1104
hidden:
- "Copy link" and "Disable link" have no handlers — :138-143
- "Create project" / "Create & invite N" always goes to /collab/raat-ki-rani — :154

### /collab/proposals/new (src/routes/collab.proposals.new.tsx)
default: New proposal form: project chips (first 5 projects), role, scope, deliverables, timeline, compensation, notes, and a Save draft / Send bar.
screens:
- Success "Proposal sent" — trigger: sent=true via "Send proposal" — :39-55,127
overlays: none
tabs: none
data-driven: project chips from projects.slice(0,5) — :63
hidden: none

### /collab/new (src/routes/collab.new.index.tsx)
default: Step 1 of 2 "New collaboration" form (name, type, description, role, location, mode, timeline, budget) with Save draft / Continue.
screens: none
overlays: none
tabs: none
data-driven: collabTypes and collabRoles — data/collab.ts:27,59
hidden:
- Nothing entered is carried to step 2; Continue is a plain link — :119

### /collab/$id/files/$fileId (src/routes/collab.$id.files.$fileId.tsx)
default: File detail: cover, tags, uploader card, version history, action rows, and "Back to files".
screens:
- No access (PermissionState) — trigger: unknown project or file — :33-42
- Unavailable-file banner — trigger: file.available=false — :52-59
- Audio player variant — trigger: file.kind==="audio" — :65-69
- Version history section — trigger: file.versions has entries — :84-109
overlays: none
tabs: none
data-driven: none
hidden:
- The unavailable banner is reachable only via a MediaTile, because FileRow doesn't link unavailable files — collab.tsx:274-276; collab-extra.tsx:25-28
- "Upload a new version", "Change access", "Add to portfolio" and "Remove file" rows have no to/onClick — :113-116
- "Compare with latest" has no handler — :101-103

### /collab/$id/lifecycle/$action (src/routes/collab.$id.lifecycle.$action.tsx)
default: Change status: title/body for the action, reason field, and a "Keep as is" / CTA bar.
screens:
- No access (PermissionState) — trigger: unknown id or an action not in copy — :79-88
- Done confirmation "{cta} — done" — trigger: confirmed=true via the CTA — :90-120
- Complete "What's next" card on the done screen — trigger: action==="complete" — :105-113
- Danger variant (ink button) — trigger: action leave or cancel — :60,67,156
overlays: none
tabs: none
data-driven: $action values hold, resume, complete, archive, leave, cancel — :31-70
hidden:
- action="resume": no link anywhere passes it — :38-43; settings only links hold/complete/archive/leave/cancel (collab.$id.settings.tsx:165-194)

### /collab/$id/milestones/$mid (src/routes/collab.$id.milestones.$mid.tsx)
default: Milestone detail card (name, status tag, owner, deadline, notes), Mark complete / Edit buttons, and "Back to timeline".
screens:
- Deleted "milestone has been removed" — trigger: unknown project or milestone — :38-47
- Done/Reopen state — trigger: local done toggled by "Mark complete"/"Reopen" — :36,58,80-82
overlays: none
tabs: none
data-driven: none
hidden:
- "Edit milestone" opens the blank new-milestone form, not an edit form — :83-89

### /collab/$id/milestones/new (src/routes/collab.$id.milestones.new.tsx)
default: Add milestone form (name, description, owner chips, due date) with a "Save milestone" bar.
screens:
- No access (PermissionState) — trigger: unknown id — :35-44
overlays: none
tabs: none
data-driven: owner chips from project.participants — :63-67
hidden:
- "Save milestone" is a plain link to the timeline; there is no saved or confirmation state — :75-77

### /collab/$id/remove/$pid (src/routes/collab.$id.remove.$pid.tsx)
default: Person card, ConfirmPanel "Remove X?", and a Cancel/Remove bar.
screens:
- Deleted "collaborator is no longer on the project" — trigger: unknown project or person — :37-46
- Owner can't be removed — trigger: person.status==="owner" — :48,66-80
- Removed outcome — trigger: removed=true via "Remove" — :81-90,115
overlays: none
tabs: none
data-driven: none
hidden:
- The owner-can't-be-removed screen is unreachable from the UI, because ParticipantRow hides Remove for owners — collab.tsx:173; remove.$pid.tsx:66-80

### /collab/invites/accepted/$id (src/routes/collab.invites.accepted.$id.tsx)
default: Accepted outcome, role/timeline/participants card, and Message / Open buttons.
screens:
- Deleted "invitation is no longer available" — trigger: unknown invitation id — :34-43
- Project-found variant (ParticipantStack, "Open project") — trigger: getProject(inv.projectId) finds a project — :61-67,77-80
- No-project variant ("You and {inviter}", "Open collaborations") — trigger: project undefined — :68-70,81-85
overlays: none
tabs: none
data-driven: none
hidden:
- The project-found variant can't show: both invitations point at kalank-remix and sound-of-city, which aren't in projects — :61-67,77-80; data/collab.ts:1017,1033

### /collab/invites/declined/$id (src/routes/collab.invites.declined.$id.tsx)
default: Declined outcome with "Message {first name}" and "Back to collaborations".
screens:
- Deleted — trigger: unknown invitation id — :32-41
overlays: none
tabs: none
data-driven: none
hidden: none

### /collab/proposals/$id/counter (src/routes/collab.proposals.$id.counter.tsx)
default: Counter proposal form prefilled from the proposal, with Cancel / Send counter.
screens:
- No access (PermissionState) — trigger: unknown proposal id — :35-44
overlays: none
tabs: none
data-driven: none
hidden:
- "Send counter" just links back to the proposal; there is no sent or confirmation state — :90-92

### /collab/proposals/accepted/$id (src/routes/collab.proposals.accepted.$id.tsx)
default: "Collaboration confirmed" outcome, terms grid, ProposalSummary, and Message / Open project.
screens:
- Deleted — trigger: unknown proposal id — :34-43
- No-project variant ("2 people", "Open collaborations") — trigger: getProject(projectId) is undefined — :71,90-94
overlays: none
tabs: none
data-driven: none
hidden:
- The no-project variant can't show: both proposals point at existing projects — :71,90-94; data/collab.ts:1051,1072

### /collab/proposals/declined/$id (src/routes/collab.proposals.declined.$id.tsx)
default: Declined outcome with Message / Back to collaborations.
screens:
- Deleted — trigger: unknown proposal id — :32-41
overlays: none
tabs: none
data-driven: none
hidden: none

### /collab/$id/files (src/routes/collab.$id.files.index.tsx)
default: Workspace Files tab: kind chips, Upload / Media gallery buttons, and FileRow list.
screens:
- No access (PermissionState) — trigger: unknown id — :39-48
- Empty "No files of this type yet" — trigger: filtered list.length===0 (for example Video, since no data file has kind video) — :77-81
overlays:
- Upload file (Sheet) — opened by: "Upload" — :64,93-115. Its inner states:
  - idle "Select a file to upload" — collab.tsx:570-583
  - uploading 46% — trigger: select the file, or "Retry upload" — collab.tsx:585-599
  - error "Upload failed" — trigger: "Simulate failure" — collab.tsx:601-617
  - done "uploaded to the project" — trigger: "Simulate success" — collab.tsx:619-626
tabs: All / Audio / Video / Image / Document / Lyrics / Contract / Reference — :30,56-60
data-driven: none
hidden: none

### /collab/proposals/$id (src/routes/collab.proposals.$id.index.tsx)
default: "Proposal received": ProposalSummary, disclaimer, and Decline / Accept terms / Counter.
screens:
- No access (PermissionState) — trigger: unknown proposal id — :35-44
- "Proposal you sent" variant (Awaiting response card, Withdraw / Edit proposal) — trigger: p.status==="sent" — :46,51,65-88
overlays: none
tabs: none
data-driven: proposal ids prop-mix (received) and prop-vocal (sent) — data/collab.ts:1050,1071
hidden:
- The sent variant is unreachable: nothing links to prop-vocal; the only link is hardcoded to prop-mix — collab.index.tsx:188
- "Withdraw" has no handler — :78-80
- Proposal statuses accepted, declined and countered exist in the type but no data uses them — data/collab.ts:235

CROSS-CUTTING
- No screen-like component in the files read is left unrendered. DeletedState, MediaTile, OutcomePanel and ConfirmPanel (collab-extra.tsx:8,23,55,77) and CollabSkeleton, ErrorState, OfflineBanner, PermissionState, UploadPanel and AudioPlayer (collab.tsx:448,492,515,534,559,306) all have a route that uses them.
- CollabSkeleton, ErrorState and OfflineBanner are only ever shown through the /collab "States" simulator (collab.index.tsx:94-112). No workspace route has a loading, error or offline state.
- Missing-id pages use two different views: PermissionState "You don't have access" (collab.tsx:534) in index, activity, discussion, notes, participants, settings, share, timeline, files, file detail, lifecycle, milestones/new, invites/$id, proposals/$id and counter; DeletedState (collab-extra.tsx:8) in agreement, invite-link, media, roles, transfer, milestone detail, remove, and all four accepted/declined outcome routes.
- Agreement, Media and Roles have no tab of their own. They borrow the overview, files and participants tab keys (agreement.tsx:48, media.tsx:66, roles.tsx:50).
- WorkspaceShell's back link always goes to /collab, not to the previous workspace tab (collab-workspace.tsx:33). The detail routes without the shell do the same.
- Cancelled project neon-bazaar (data/collab.ts:952) has no way in: no tab lists it and no link targets it, so its overview banner, empty lists and no-eligible-owner states are only reachable by typing the URL.
- Archived project chai-tapes has a completedOn date, but the Completed banner only checks status==="completed", so it never shows there (collab.$id.index.tsx:72; data/collab.ts:988,1003).

## Opportunities, reviews, verification, identity

### /identity/analytics (src/routes/identity.analytics.tsx)
default: Stat grid with 6 counts plus a "Where scans come from" bar list (identity.analytics.tsx:37-78)
screens: none
overlays: none
tabs: none
data-driven: none
hidden: none

### /identity/business (src/routes/identity.business.tsx)
default: Business QR card, copy-link row, Share/Download buttons, print link (identity.business.tsx:37-77)
screens: none
overlays:
- "Share business QR" Sheet — opened by: Share — identity.business.tsx:55,79-88
tabs: none
data-driven: none
hidden: none

### /identity/card (src/routes/identity.card.tsx)
default: Branded QrIdentityCard with Share card / Download (identity.card.tsx:61-107)
screens:
- Generating skeleton — trigger: phase="generating" (on mount, 500ms) — identity.card.tsx:37-44,50-51
- Export error "Couldn't build the card" — trigger: phase="error" — identity.card.tsx:52-60
overlays: none
tabs: none
data-driven: none
hidden:
- The error state can only be reached through a demo button, "Preview the export-failed state". Nothing else sets it — identity.card.tsx:100-106

### /identity/customize (src/routes/identity.customize.tsx)
default: Compact QR card preview, 4 style options, "Show role" checkbox, Save (identity.customize.tsx:35-96)
screens: none
overlays: none
tabs: none (the style option list restyles the preview in place)
data-driven: qrStyles light/dark/branded/minimal — src/data/identity.ts:287-322
hidden: none

### /identity/full?style= (src/routes/identity.full.tsx)
default: Full-bleed QR with no TopBar, close X, Share/Save (identity.full.tsx:39-88)
screens:
- One visual variant per ?style=light|dark|branded|minimal (defaults to light) — identity.full.tsx:12-16. Reached from /identity/qr with the chosen style — identity.qr.tsx:121-124
overlays: none
tabs: none
data-driven: style values — identity.full.tsx:13; src/data/identity.ts:287-322
hidden: none

### /identity/history (src/routes/identity.history.tsx)
default: List of scanned codes linking to /identity/q/$code (identity.history.tsx:60-70)
screens:
- Empty "No scans yet" — trigger: items.length===0 after tapping Clear history (trash) — identity.history.tsx:40,50-58
overlays: none
tabs: none
data-driven: scanHistory ananya, strat-62, octave-labs — src/data/identity.ts:375-379
hidden: none

### /identity/print (src/routes/identity.print.tsx)
default: Print layout preview, format list, "Download print file" (identity.print.tsx:37-114)
screens: none
overlays: none
tabs: Personal / Business subject toggle — identity.print.tsx:41-57
data-driven: printFormats business-card, poster, studio-sign, event-badge, portfolio-card — src/data/identity.ts:329-355
hidden: none

### /identity/qr (src/routes/identity.qr.tsx)
default: "Your MusicLinkd ID" card, copy link, Share/Fullscreen, style chips, "Use it everywhere" rows (identity.qr.tsx:101-204)
screens:
- Loading skeleton — trigger: phase="loading" (420ms) — identity.qr.tsx:58,87-92
- Offline "You're offline" — trigger: navigator.onLine false — identity.qr.tsx:64-65,93-99
overlays:
- "Share your MusicLinkd ID" Sheet — opened by: Share — identity.qr.tsx:118,208-223
tabs: none (the style chips restyle the card in place, identity.qr.tsx:131-147)
data-driven: none
hidden:
- The header comment lists an "error" state, but the phase type has no error value and no error view exists — identity.qr.tsx:4,58

### /identity/scan (src/routes/identity.scan.tsx)
default: Camera permission prompt, "Camera access needed" (identity.scan.tsx:59-70)
screens:
- Camera blocked — trigger: phase="denied" (Not now) — identity.scan.tsx:67,71-80
- Ready camera surface — trigger: phase="ready" (Allow camera / Retry) — identity.scan.tsx:64,88-108
- Scanning "Reading code…" — trigger: phase="scanning" (Simulate a scan / Choose from gallery). After 1400ms it auto-navigates to /identity/q/rahul-iyer — identity.scan.tsx:45-52,100,104
- Camera error "The camera couldn't start" — trigger: phase="error" — identity.scan.tsx:81-87
overlays: none
tabs: none
data-driven: destinations[0] is the scan target; recent list comes from scanHistory.slice(0,3) — src/data/identity.ts:78-85,375-379
hidden:
- The error phase is never set, so the error screen can't be reached — identity.scan.tsx:38,81-87
- "offline" is listed in the header comment, but no offline phase exists — identity.scan.tsx:3,38
- "Scan an image instead" only shows a toast — identity.scan.tsx:77

### /identity/settings (src/routes/identity.settings.tsx)
default: Privacy toggles plus Save / View analytics (identity.settings.tsx:66-115)
screens: none
overlays: none
tabs: none
data-driven: none
hidden: none

### /identity/contact (src/routes/identity.contact.index.tsx)
default: Digital card with shared contact details, Save contact / Share card (identity.contact.index.tsx:44-99)
screens: none
overlays: none
tabs: none
data-driven: contact fields filtered by .shared — src/data/identity.ts:56-61
hidden: none

### /identity/contact/save (src/routes/identity.contact.save.tsx)
default: "Will be saved" review list with Save to contacts / Cancel (identity.contact.save.tsx:52-99)
screens:
- Saving (button label only, not a separate view) — trigger: phase="saving" — identity.contact.save.tsx:89-93
- Success "Contact saved" — trigger: phase="done" (600ms after save) — identity.contact.save.tsx:36-44
- Error "Couldn't save the contact" — trigger: phase="error" — identity.contact.save.tsx:45-51
overlays: none
tabs: none
data-driven: none
hidden:
- The error phase is never set — identity.contact.save.tsx:29,45-51

### /identity/q/$code (src/routes/identity.q.$code.tsx)
default: Destination preview card with Open/Share, a trust note and "Scan another code" (identity.q.$code.tsx:75-124)
screens:
- Loading skeleton — trigger: phase="loading" (420ms) — identity.q.$code.tsx:42-49,60-64
- Offline — trigger: navigator.onLine false — identity.q.$code.tsx:65-74
- Profile preview, with Message/Connect — trigger: code rahul-iyer, ananya — identity.q.$code.tsx:92-109
- Business preview, with Message/Connect — trigger: code octave-labs — identity.q.$code.tsx:92-109
- Service preview — trigger: code live-room-a — src/data/identity.ts:205-220
- Listing preview — trigger: code strat-62 — src/data/identity.ts:221-235
- Portfolio work preview — trigger: code neon-monsoon — src/data/identity.ts:236-248
- Invalid "This isn't a valid MusicLinkd QR code" — trigger: unknown code, or an active code whose preview is null — identity.q.$code.tsx:125-137; identity.ts:151-161
- Expired — trigger: old-poster — identity.ts:121-127,262-266
- Private — trigger: private-band — identity.ts:128-134,267-271
- Deleted — trigger: gone — identity.ts:135-141,272-276
- Deactivated — trigger: paused — identity.ts:142-148,277-281
overlays: none
tabs: none
data-driven: codes rahul-iyer, ananya, octave-labs, live-room-a, strat-62, neon-monsoon, old-poster, private-band, gone, paused — src/data/identity.ts:78-149
hidden:
- These codes aren't linked anywhere in the app, so they're URL-only: live-room-a, neon-monsoon, old-poster, private-band, gone, paused, plus any invalid code. In-app links only reach rahul-iyer (identity.scan.tsx:48), ananya, strat-62 and octave-labs (identity.ts:375-379)
- The kinds "portfolio", "opportunity" and "band" have labels, but previewFor never returns them — qr.tsx:222-231; identity.ts:176-250

### /opportunities (src/routes/opportunities.index.tsx)
default: Search entry, My applications/Saved rows, category rail, opportunity list, "Need someone instead?" card (opportunities.index.tsx:67-141)
screens:
- Loading — trigger: state="loading" — opportunities.index.tsx:34,110-111
- Empty "Nothing here yet" — trigger: list.length===0 — opportunities.index.tsx:112-118
overlays:
- "Post an opportunity" Sheet — opened by: + (aria "Post an opportunity") — opportunities.index.tsx:56-63,143-152
tabs: For you / Recent / Nearby — opportunities.index.tsx:30,108
data-driven: none
hidden:
- `state` has no setter, so loading and empty can't be reached — opportunities.index.tsx:34
- "Change preferences" has no onClick — opportunities.index.tsx:117

### /opportunities/mine (src/routes/opportunities.mine.tsx)
default: Cards for posted opportunities, with Applicants or "Finish draft" plus Manage (opportunities.mine.tsx:54-99)
screens:
- Empty "You haven't posted yet" — trigger: myOpportunities.length===0 — opportunities.mine.tsx:44-52
overlays: none
tabs: none
data-driven: myOpportunities op-mine-1 (reviewing), op-mine-2 (draft) — src/data/opportunities.ts:414-450
hidden:
- The empty state can't be reached because the seed has 2 items — opportunities.mine.tsx:44

### /opportunities/new?step= (src/routes/opportunities.new.tsx)
default: Step "basics" (title, category, summary) (opportunities.new.tsx:130-158)
screens:
- Step basics — trigger: ?step=basics — opportunities.new.tsx:130
- Step location — trigger: ?step=location — :160
- Step availability — trigger: ?step=availability — :182
- Step compensation (Paid shows budget; other types show a notice) — trigger: ?step=compensation — :205-233
- Step requirements (with "No requirements yet" empty line) — trigger: ?step=requirements — :236-280
- Step preview — trigger: ?step=preview — :282-316
- Published success "Your opportunity is live" — trigger: ?step=published — :80-104
overlays: none
tabs: none
data-driven: step values — opportunities.new.tsx:16-24
hidden: none. "Finish draft" links straight to ?step=preview (opportunities.mine.tsx:73-78), and Manage → "Edit details" opens the default step (opportunities.$id.manage.tsx:82-87)

### /opportunities/saved (src/routes/opportunities.saved.tsx)
default: Saved OpportunityCards (opportunities.saved.tsx:39-46)
screens:
- Empty "No saved opportunities" — trigger: list.length===0 — opportunities.saved.tsx:29-37
overlays: none
tabs: none
data-driven: seeded saved:true on op-1 and op-4 — src/data/opportunities.ts:136,266
hidden:
- The empty state can't be reached. When no ids are stored, the seeded saved items fill the list — opportunities.saved.tsx:23

### /opportunities/search?q= (src/routes/opportunities.search.tsx)
default: Search bar, Filters button, sort segment, results count and list (opportunities.search.tsx:69-120)
screens:
- Empty "No matches" — trigger: filtered list.length===0 — opportunities.search.tsx:84-107
overlays:
- "Filters" Sheet — opened by: Filters — opportunities.search.tsx:72-78,123-180
tabs: Best fit / Newest / Highest pay (sort) — opportunities.search.tsx:34,80
data-driven: q prefilled from category links — opportunities.index.tsx:94-98
hidden:
- `Loading` is imported but never rendered — opportunities.search.tsx:5

### /opportunities/$id (src/routes/opportunities.$id.index.tsx)
default: Opportunity detail (pills, pay/where/when, fit, brief, requirements, poster) with a sticky Apply bar (opportunities.$id.index.tsx:77-212)
screens:
- Not found "This opportunity is gone" — trigger: getOpportunity(id) undefined, including op-mine-* ids — opportunities.$id.index.tsx:56-72
- Already-applied variant, "View your application · {status}" — trigger: myApplications match (op-1, op-3, op-5) — :189-196
- Closed variant, "Applications closed" — trigger: o.status==="closed" — :197-200
overlays:
- "Share opportunity" Sheet — opened by: Share icon — :84-91,214-226
- "Report opportunity" Sheet — opened by: Report this opportunity — :166-172,228-244
- "Report sent" Dialog — opened by: choosing a report reason — :235-238,246-253
tabs: none
data-driven: opportunities op-1…op-5 — src/data/opportunities.ts:96-311
hidden:
- The closed variant can't be reached because every seeded opportunity is "open" — opportunities.$id.index.tsx:197; data/opportunities.ts:102,150,192,233,275
- "Copy link" in the share sheet has no onClick — opportunities.$id.index.tsx:216-218

### /opportunities/$id/apply?step= (src/routes/opportunities.$id.apply.tsx)
default: Step "portfolio" (pick up to 3 works) (opportunities.$id.apply.tsx:102-145)
screens:
- Not found "Opportunity unavailable" — trigger: unknown id — :48-57
- Step portfolio — trigger: ?step=portfolio — :102
- Step message (pitch plus rate) — trigger: ?step=message — :147-175
- Step review — trigger: ?step=review — :177-205
- Success "Application sent" — trigger: ?step=done — :63-87
overlays: none
tabs: none
data-driven: step values — opportunities.$id.apply.tsx:9-10
hidden:
- For op-1, op-3 and op-5 (already applied), the detail page shows no Apply link, so their apply flow is URL-only — opportunities.$id.index.tsx:189-210

### /opportunities/$id/manage (src/routes/opportunities.$id.manage.tsx)
default: Status/stats card, action rows, "Hired someone?" card (opportunities.$id.manage.tsx:41-122)
screens:
- Not found "Opportunity not found" — trigger: id not in myOpportunities — :30-39
- Paused variant (pill and row label change in place) — trigger: paused=true — :48,90-101
overlays:
- "Why are you closing?" Sheet — opened by: Close opportunity — :103-109,124-140
- "Opportunity closed" Dialog — opened by: choosing a close reason — :131-134,142-151
tabs: none
data-driven: myOpportunities ids — src/data/opportunities.ts:414-450
hidden: none

### /opportunities/$id/analytics (src/routes/opportunities.$id.analytics.tsx)
default: 4 stat cards, 7-day bars, source meters, "Edit the post" card (opportunities.$id.analytics.tsx:36-86)
screens: none. An unknown id only changes the subtitle to "Your opportunity" — :33
overlays: none
tabs: none
data-driven: none
hidden: none

### /opportunities/applications (src/routes/opportunities.applications.index.tsx)
default: ApplicationCard list (opportunities.applications.index.tsx:64-69)
screens:
- Loading — trigger: loading=true — :33,52-53
- Empty "Nothing here" — trigger: list.length===0 — :54-62
overlays: none
tabs: Active / Shortlisted / Closed — :29,51
data-driven: myApplications ap-1 (shortlisted), ap-2 (viewed), ap-3 (rejected) — src/data/opportunities.ts:346-392
hidden:
- `loading` has no setter — :33
- The empty state can't be reached because every tab has at least one seeded item — :35-41

### /opportunities/applications/$aid (src/routes/opportunities.applications.$aid.tsx)
default: Status card, progress timeline, "What you sent", links, Withdraw (opportunities.applications.$aid.tsx:49-140)
screens:
- Not found "Application not found" — trigger: unknown aid — :38-47
- Shortlisted status copy — trigger: status shortlisted (ap-1) — :62-66
- Rejected status copy — trigger: status rejected (ap-3) — :67-70
- Default status copy — trigger: other statuses (ap-2) — :71-75
overlays:
- "Withdraw this application?" Dialog — opened by: Withdraw application — :135-139,142-159
- "Application withdrawn" Dialog — opened by: Withdraw in the confirm dialog — :148-153,161-170
tabs: none
data-driven: ap-1, ap-2, ap-3 — src/data/opportunities.ts:346-392
hidden: none

### /opportunities/$id/applicants (src/routes/opportunities.$id.applicants.index.tsx)
default: ApplicantCard list (opportunities.$id.applicants.index.tsx:69-71)
screens:
- Loading — trigger: loading=true — :33,53-54
- Empty "No applicants here yet" — trigger: list.length===0. Reachable for op-mine-2 through Manage → Applicants — :55-68
overlays: none
tabs: All / Shortlisted / New — :28,52
data-driven: applicants apl-1..3 on op-mine-1 — src/data/opportunities.ts:473-553
hidden:
- `loading` has no setter — :33

### /opportunities/$id/applicants/$aid (src/routes/opportunities.$id.applicants.$aid.tsx)
default: Applicant card, message, attached work, Message button, sticky "Not a fit" / Shortlist bar (opportunities.$id.applicants.$aid.tsx:50-133)
screens:
- Not found "Applicant not found" — trigger: unknown aid — :36-48
- Shortlisted variant (Hire replaces Shortlist) — trigger: a.status==="shortlisted" (apl-1) — :123-131
overlays:
- "Shortlist this applicant?" / "Tell them it's a no?" Dialog — opened by: Shortlist / Not a fit — :120,128,135-158
- Confirmation Dialog ("Shortlisted — conversation opened" / "Applicant declined") — opened by: the confirm button — :146-151,160-164
- "How do you want to work together?" Sheet — opened by: Hire — :124,166-180
tabs: none
data-driven: apl-1, apl-2, apl-3 — src/data/opportunities.ts:473-553
hidden: none

### /reviews (src/routes/reviews.index.tsx)
default: Rating summary card, tab content, "How reviews work" (reviews.index.tsx:40-162)
screens:
- Received empty "No reviews yet" — trigger: receivedReviews empty — :86-91
- To review empty "Nothing to review" — trigger: pendingReviews empty — :120-125
- Given empty "You have not reviewed anyone yet" — trigger: givenReviews empty — :133-138
- Inline response composer inside a ReviewCard (not full-view) — trigger: Respond — :194,232-247
overlays:
- "Report this review" Sheet — opened by: Report on a ReviewCard — :164-180,255-258
tabs: Received / To review / Given — :33,74
data-driven: rv-1, rv-2 received; rv-3 given; prv-1, prv-2 pending — src/data/reviews.ts:31-113
hidden:
- None of the three empty states can be reached with the seeded data — reviews.index.tsx:77,95,129
- Choosing a flag reason only closes the sheet, with no confirmation — :170
- "Publish response" only closes the composer — :240

### /reviews/request (src/routes/reviews.request.tsx)
default: Explainer card plus a target list with Ask / Send a reminder / Reminder sent states (reviews.request.tsx:43-99)
screens:
- Empty "Nobody to ask yet" — trigger: reviewRequestTargets empty — :93-98
overlays: none
tabs: none
data-driven: rq-1..rq-3 — src/data/reviews.ts:151-173
hidden:
- No in-app link points to /reviews/request (only its route definition matches) — reviews.request.tsx:7
- The empty state can't be reached with seeded data — :51

### /reviews/$id/edit (src/routes/reviews.$id.edit.tsx)
default: Locked rating plus editable text, Save / Delete (reviews.$id.edit.tsx:42-80)
screens:
- Not found "Review not found" — trigger: getReview(id) undefined — :31-40
overlays:
- "Delete your review?" Dialog — opened by: Delete this review — :77,82-92
tabs: none
data-driven: rv-1..rv-3 — src/data/reviews.ts:31-94,147
hidden:
- No in-app link points to this route — reviews.$id.edit.tsx:7

### /reviews/$id/reply (src/routes/reviews.$id.reply.tsx)
default: Review card, response textarea, house rules, Post response (reviews.$id.reply.tsx:44-99)
screens:
- Not found — trigger: unknown id — :30-42
overlays: none
tabs: none
data-driven: rv-1..rv-3 — src/data/reviews.ts:147
hidden:
- No in-app link points to this route. /reviews uses an inline composer instead — reviews.$id.reply.tsx:7; reviews.index.tsx:232

### /reviews/$id/report (src/routes/reviews.$id.report.tsx)
default: Quoted review, reason chips, detail textarea, Send report (reviews.$id.report.tsx:63-102)
screens:
- Not found — trigger: unknown id — :32-41
- Sent "Thanks — the team will look at it" — trigger: sent=true (Send report) — :43-61
overlays: none
tabs: none
data-driven: rv-1..rv-3 — src/data/reviews.ts:147
hidden:
- No in-app link points to this route. /reviews uses a sheet instead — reviews.$id.report.tsx:7; reviews.index.tsx:164

### /reviews/write/$id (src/routes/reviews.write.$id.tsx)
default: Item card, per-criterion star rating, text, policy, Submit (reviews.write.$id.tsx:53-120)
screens:
- Not found "This review window has closed" — trigger: id not in pendingReviews — :33-49
overlays:
- "Review submitted" Dialog — opened by: Submit review (once complete) — :117,122-131
tabs: none
data-driven: prv-1, prv-2 — src/data/reviews.ts:96-113
hidden: none

### /verification (src/routes/verification.index.tsx)
default: Overall status card, per-type status cards, trust signals, related rows (verification.index.tsx:63-161)
screens:
- Overall-state variants (card copy only) for unverified / in_review / verified / rejected / expired — trigger: overallStatus() — :36-61; src/data/verification.ts:222-228
overlays: none
tabs: none
data-driven: verificationTypes identity/professional/business/studio — src/data/verification.ts:33-113
hidden:
- overallStatus() always returns "rejected" with the seeded data, so the other 4 overall copies and the "Get verified" label can't be reached — verification.index.tsx:37-56,77; data/verification.ts:179-180,223
- The lock-icon branch can't be reached because identity is verified — verification.index.tsx:107-109; data/verification.ts:157-158,235

### /verification/choose (src/routes/verification.choose.tsx)
default: One card per type with verifies / people see / you need, linking to /verification/$type (verification.choose.tsx:37-128)
screens:
- Locked card variant (not a link) — trigger: !eligibility.allowed && unverified — :46,54-55,84-87,97
overlays: none
tabs: none
data-driven: verificationTypes — src/data/verification.ts:33-113
hidden:
- The locked variant can't be reached with seeded data (identity is verified) — verification.choose.tsx:46; data/verification.ts:235

### /verification/$type?step=&mode= (src/routes/verification.$type.tsx)
default: A status view picked from myVerifications. Only with mode=new does it show the 5-step wizard (verification.$type.tsx:83-275)
screens:
- Type not found "That verification type does not exist" — trigger: unknown $type (loader notFound) — :35-38,61,65-81
- Locked "Verify your identity first" — trigger: !gate.allowed && status unverified — :101-122
- Verified — trigger: status verified (identity) — :124-156
- In review — trigger: status in_review (professional) — :158-199
- Needs changes (rejected) — trigger: status rejected (business). Resubmit goes to ?step=2&mode=new — :201-243
- Expired — trigger: status expired (studio). Renew goes to ?step=0&mode=new — :245-272
- Wizard step 0 "Requirements" — trigger: mode=new&step=0 — :318-347
- Wizard step 1 "Your details" — trigger: step=1 — :349-400
- Wizard step 2 "Documents" — trigger: step=2 — :402-502
- Wizard step 3 "Review" (consent) — trigger: step=3 — :504-564
- Wizard step 4 "Submitted for review" (success) — trigger: step=4 — :566-600
overlays:
- "Document preview" Sheet — opened by: Preview on an attached document (step 2) — :425-431,603-610
tabs: none
data-driven: $type identity (verified), professional (in_review), business (rejected), studio (expired) — src/data/verification.ts:33-113,154-203. Steps are Requirements/Your details/Documents/Review/Submitted — src/data/verification.ts:316-322
hidden:
- The locked view can't be reached because identity is verified, so eligibility always allows — verification.$type.tsx:101-122; data/verification.ts:235-242
- For identity and professional, the wizard is URL-only (?mode=new). Every in-app link sends {step:0} without mode — verification.index.tsx:91-93; verification.choose.tsx:101-103
- Without mode=new, the ?step param is ignored for all 4 seeded types — verification.$type.tsx:92
- Business resubmit enters at step 2, so steps 0-1 are only reachable through Back — verification.$type.tsx:232

### /verification/badge (src/routes/verification.badge.tsx)
default: Badge explainer, placement rules, related rows (verification.badge.tsx:28-68)
screens: none
overlays: none
tabs: none
data-driven: badgePlacements — src/data/verification.ts:259
hidden: none

### /verification/privacy (src/routes/verification.privacy.tsx)
default: Privacy note, privacy points, FAQ, related rows (verification.privacy.tsx:29-81)
screens: none
overlays: none
tabs: none
data-driven: privacyPoints, verificationFaq — src/data/verification.ts:274,293
hidden: none

CROSS-CUTTING
- `Loading` (src/components/ml/kit.tsx:147) never actually shows. It sits only in branches that can't be reached (opportunities.index.tsx:111, opportunities.applications.index.tsx:53, opportunities.$id.applicants.index.tsx:54), and search imports it without rendering it (opportunities.search.tsx:5).
- The `DestinationPreviewCard` labels for portfolio, opportunity and band (src/components/ml/qr.tsx:225,229,230) never render, because previewFor never returns those kinds (src/data/identity.ts:176-250).
- `applyDraftKey` (src/data/opportunities.ts:586) is exported but never referenced. It's a data key, not a component.
- Among the files read, I found no screen-level component that is defined but never rendered: every export in opportunity.tsx, qr.tsx and kit.tsx is used by at least one of these routes. That excludes the Loading case above.
- Several screens here are URL-only, with no in-app link: /reviews/request, /reviews/$id/edit, /reviews/$id/reply and /reviews/$id/report.

## Marketplace, rent, bands

### /bands/new (src/routes/bands.new.tsx)
default: 4-step create-band wizard. Step 1 is "Identity".
screens:
- Step 1 Identity — trigger: step=0 ("Identity") — bands.new.tsx:67
- Step 2 Genre & city — trigger: step=1 — :82
- Step 3 Members (invite list, or an empty hint) — trigger: step=2 — :104, :123/:139
- Step 4 Preview — trigger: step=3 — :146
- Steps come from bandCreationSteps, src/data/band.ts:266
overlays:
- "Band created" Dialog — opened by: Publish band — :177 (:166)
tabs: none
data-driven: none
hidden: When the fields are invalid, the "Fill required fields" button does nothing — :170. Closing the Band created dialog leaves the user on Preview, and nothing is saved — :181.

### /bands (src/routes/bands.index.tsx)
default: Search bar, Discover/My bands segments and a list of band cards.
screens:
- Empty "No bands here yet" — trigger: filtered list is empty (search query) — :60
- Loading — trigger: loading=true — :58
overlays: none
tabs: Discover, My bands — :25
data-driven: none
hidden: Loading can never show because `loading` is fixed at false with no setter — :30.

### /bands/$id (src/routes/bands.$id.index.tsx)
default: Band profile (cover, action grid, segmented content).
screens:
- Not available — trigger: getBand(id) undefined — :32
- Owner view (Manage + Invite) — trigger: band.id===myBandId — :50, :89
- Visitor view (Follow + Message) — trigger: not the owner — :108
- Shows empty — trigger: events.length=0 — :246
- Music empty — trigger: releases.length=0 — :274
overlays: none
tabs: Overview, Members, Shows, Music — :25
data-driven: id ∈ {the-kite-notes, static-bloom} — src/data/band.ts:78, :195
hidden: Follow has no handler — :110. The "Band portfolio" row links to /portfolio, not /bands/$id/portfolio — :177. Message links to /messages, not /bands/$id/contact — :112.

### /bands/$id/contact (src/routes/bands.$id.contact.tsx)
default: Reason chips, message box and Send button.
screens:
- Band not found — trigger: no band — :34
- Message sent confirmation — trigger: sent=true — :48
overlays: none
tabs: none
data-driven: band ids as above
hidden: No link anywhere in src points to this route (grep), so the whole route is unreachable.

### /bands/$id/events (src/routes/bands.$id.events.tsx)
default: Shows list.
screens:
- Band not found — trigger: no band — :32
- Shows empty "No shows booked" — trigger: events.length=0 — :80
- Opportunities empty — trigger: gigs.length=0 — :102
overlays: none
tabs: Shows, Opportunities — :25
data-driven: band ids as above
hidden: No link points to this route (grep). "Add a show" has no handler — :75, :84.

### /bands/$id/invite (src/routes/bands.$id.invite.tsx)
default: People search with Invite toggles and an "Invite by link" card.
screens:
- No search matches — trigger: results.length=0 — :77
- Sticky "Send N invitations" bar — trigger: invited.length>0 — :85
overlays:
- "Invitations sent" Dialog — opened by: Send N invitations — :93
tabs: none
data-driven: band ids as above
hidden: There is no band-not-found state: an unknown id renders the full view with the subtitle "Band" — :35.

### /bands/$id/manage (src/routes/bands.$id.manage.tsx)
default: Members tab.
screens:
- Band not found — trigger: no band — :34
- Invitations empty — trigger: invitations.length=0 — :128
overlays:
- "Remove this member?" — opened by: Remove — :179 (:86)
- "Transfer ownership" — opened by: Transfer ownership — :190 (:168)
- "Delete this band?" — opened by: Delete band — :201 (:171)
tabs: Members, Invitations, Settings — :24
data-driven: band ids as above
hidden: The only links here are for the owner band (bands.$id.index.tsx:91). Invitations-empty needs static-bloom (band.ts:228), which only a typed URL reaches. "Make admin/Make member" has no handler — :80. Every dialog button only closes the dialog — :185-208.

### /bands/$id/portfolio (src/routes/bands.$id.portfolio.tsx)
default: Discography list.
screens:
- Band not found — trigger: no band — :32
- Discography empty — trigger: releases.length=0 — :79
- Live tab (a card plus an always-shown "Nothing live yet" empty state) — trigger: tab=Live — :86-101
overlays: none
tabs: Discography, Live — :25
data-driven: band ids as above
hidden: No link points to this route (grep). "Add a release" and "Add live work" have no handlers — :74, :83, :94.

### /marketplace (src/routes/marketplace/index.tsx)
default: Market home (header, chips, category rail, recommended/nearby/trending/recent rows, sell card).
screens: none
overlays:
- Filters sheet (MarketFilterSheet) — opened by: Filters icon — :145 (:59); market.tsx:344
tabs: none. The chips For You/Nearby/Rentals/Services/Studios/Saved at :76 are inert.
data-driven: none
hidden: The "Mumbai, Maharashtra · change" button has no handler — :48. Chips have no onClick — :77. "Recently viewed · See all" goes to /marketplace/saved — :119.

### /marketplace/search (src/routes/marketplace/search.tsx)
default: Starts with q="condenser microphone", so the results list shows first — :35.
screens:
- Recent/popular suggestions — trigger: q empty — :59
- No matches — trigger: results.length=0 — :96
overlays:
- Filters sheet — opened by: Filters chip — :108 (:88)
- "Sort by" SortSheet — opened by: sort chip — :113 (:87); market.tsx:425
tabs: none
data-driven: none
hidden: "Save this search" only clears the query — :101.

### /marketplace/nearby (src/routes/marketplace/nearby.tsx)
default: Chip row plus every listing as ListingRow.
screens: none
overlays: none
tabs: none. Chips at :25 are inert.
data-driven: none
hidden: Chips have no onClick — :26.

### /marketplace/saved (src/routes/marketplace/saved.tsx)
default: Listings tab.
screens:
- Studios empty — trigger: tab=Studios (always empty) — :70
overlays: none
tabs: Listings, Searches, Sellers, Studios — :22
data-driven: none
hidden: The "View" chip on saved searches has no handler — :50.

### /marketplace/mine (src/routes/marketplace/mine.tsx)
default: Active tab with 3 listing rows.
screens:
- Drafts empty — trigger: tab=Drafts (always empty) — :67
- Offers list — trigger: tab=Offers — :76
overlays: none
tabs: Active, Drafts, Offers — :25
data-driven: none
hidden: none

### /marketplace/sell (src/routes/marketplace/sell.tsx)
default: 4-step create-listing flow.
screens:
- Step 1 What are you offering (type picker) — trigger: step=0 — :63
- Step 2 Add photos — trigger: step=1 — :83
- Step 3 Listing details (Condition hidden for Service and Studio/space) — trigger: step=2 — :107, :126
- Step 4 Preview — trigger: step=3 — :153
overlays: none
tabs: none
data-driven: none
hidden: There is no success screen: Publish links to /marketplace/mine — :186. "Save draft" has no handler — :189. Photo tiles are not interactive — :86-97. The Description textarea is uncontrolled — :138.

### /marketplace/c/$slug (src/routes/marketplace/c.$slug.tsx)
default: Subcategory chips, sort/filter chips and a ListingCard list.
screens:
- Empty "Nothing here yet" — trigger: no listings match — :66
overlays:
- Filters sheet — opened by: Filters chip — :73 (:57)
- Sort by sheet — opened by: sort chip — :74 (:56)
tabs: none (subcategory chips filter the list)
data-driven: slug ∈ {instruments, audio-gear, studio, rentals, services, spaces} — src/data/market.ts:108-114. Subcategories come from market.ts:117.
hidden: No not-found state: an unknown slug renders title "Market" with 0 listings — :42. The "studio" slug has no listings, so it always shows the empty state (listing categories at market.ts:164-370).

### /marketplace/l/$id (src/routes/marketplace/l.$id.tsx)
default: Listing detail with a sticky 2-button CTA bar.
screens:
- Listing unavailable ("removed or sold") — trigger: byId undefined — :51
- CTA/heading variants by type — trigger: l.type service/rental/space/product — :63-70, :128-133, :144
overlays:
- "Make an offer" OfferSheet, which has an inner "Offer sent" state — opened by: cta[0] (Make offer / Enquire / Check availability) — :219 (:210); market.tsx:480
- "Report listing" sheet — opened by: Report this listing — :220 (:201)
tabs: none
data-driven: id ∈ fender-strat-62, neumann-tlm103, sitar-swaraj, mix-master-service, backline-rental, yrs-studio-a, focusrite-18i20, jam-room-bandra — market.ts:156-362
hidden: Share has no handler — :79. cta[0] opens the offer sheet for every type — :210. "Open conversation" in OfferSheet only closes — market.tsx:492. "Block this seller" only closes — market.tsx:576.

### /marketplace/seller/$id (src/routes/marketplace/seller.$id.tsx)
default: Seller card, listings and reviews.
screens:
- Seller unavailable (TopBar only, empty body) — trigger: sellerById undefined — :26
overlays: none
tabs: none
data-driven: id ∈ ravi-gear, yash-raj-sound, meher-kaur, swaraj-instruments — market.ts:51-93
hidden: Follow has no handler — :49.

### /marketplace/rent (src/routes/marketplace/rent/index.tsx)
default: Rent home (category rail, recommended, near you, popular, recent, saved, rent-out card).
screens:
- Loading skeletons in Recommended — trigger: loading for the first 450ms — :37-41, :121
overlays:
- Rental Filters sheet — opened by: Filters icon — :176 (:69); rental.tsx:532
tabs: none. Chips at :86 are inert.
data-driven: none
hidden: The location "change" button has no handler — :58. Chips have no onClick — :87.

### /marketplace/rent/search (src/routes/marketplace/rent/search.tsx)
default: With q empty, shows recent, popular, categories and available nearby.
screens:
- Results — trigger: q non-empty — :111
- No rentals matched — trigger: results.length=0 — :138
- Loading skeleton — trigger: loading=true — :133
overlays:
- Rental Filters sheet — opened by: Filter — :149 (:125)
- Sort by sheet — opened by: Sort — :154 (:119)
tabs: none
data-driven: none
hidden: Loading can never show because `loading` is fixed at false — :43. In the filter sheet, the "Pick dates" chip only toggles and never opens a date picker — rental.tsx:568.

### /marketplace/rent/nearby (src/routes/marketplace/rent/nearby.tsx)
default: Map placeholder, radius and category chips, equipment rows and providers.
screens: none. Filtering to the "other" category gives an empty list with no empty state — :32, :77.
overlays: none
tabs: none (radius and category chips filter the list)
data-driven: none
hidden: "Use my current location" has no handler — :52. The map placeholder is not interactive despite "Tap to open map view" — :43-50. No location-permission state exists.

### /marketplace/rent/saved (src/routes/marketplace/rent/saved.tsx)
default: Equipment tab.
screens:
- Spaces empty — trigger: tab=Spaces (always empty) — :68
overlays: none
tabs: Equipment, Providers, Searches, Spaces, Recently viewed — :26
data-driven: none
hidden: The "Remove" chip has no handler — :87.

### /marketplace/rent/mine (src/routes/marketplace/rent/mine.tsx)
default: Upcoming tab with BookingCards.
screens:
- Empty "No {tab} rentals" — trigger: bucket list empty — :92
overlays: none
tabs: Upcoming, Active, Completed, Cancelled — :23
data-driven: none
hidden: The empty state can't appear with the fixture data, because every bucket has a booking (rentals.ts:660, :675, :692, :706). Extend, Return and Review chips all just link to active/$id — :73-84.

### /marketplace/rent/listings (src/routes/marketplace/rent/listings.tsx)
default: Active tab, filtered to the Active status bucket.
screens:
- Status bucket empty "Nothing {bucket}" — trigger: no equipment with that listingStatus — :93
- Requests list — trigger: tab=Requests — :102
- Drafts empty — trigger: tab=Drafts (always empty) — :155
- Alerts list — trigger: tab=Alerts — :164
overlays: none
tabs: Active, Requests, Drafts, Alerts — :24. Sub-segments: Active, Reserved, Currently Rented, Unavailable, Completed, Archived — :25.
data-driven: none
hidden: Unavailable, Completed and Archived buckets are always empty (the data only has Active, Currently Rented and Reserved — rentals.ts:292-627). Accept, Counter offer and Decline chips have no handler — :142-144.

### /marketplace/rent/list (src/routes/marketplace/rent/list.tsx)
default: 5-step rent-out flow.
screens:
- Step 1 Category — trigger: step=0 — :90
- Step 2 Media — trigger: step=1 — :116
- Step 3 Details — trigger: step=2 — :149
- Step 4 Availability (calendar) — trigger: step=3 — :268
- Step 5 Review/preview — trigger: step=4 — :308
- Published success "Your rental is live" — trigger: published=true — :68
overlays: none
tabs: none
data-driven: none
hidden: "Save draft" has no handler — :353. The Add photo and Add video buttons have no handler — :139, :143. Sub-category chips have no onClick — :182. Min/max/notice chips have no onClick — :297. The Review step always shows a hardcoded Shure SM7B — :315-331.

### /marketplace/rent/c/$slug (src/routes/marketplace/rent/c.$slug.tsx)
default: Subcategory chips, sort/filter buttons and equipment rows.
screens:
- Empty "Nothing listed here yet" — trigger: shown.length=0 — :79
overlays:
- Rental Filters sheet — opened by: Filter — :87 (:68)
- Sort by sheet — opened by: Sort — :88 (:62)
tabs: none
data-driven: slug ∈ {instruments, audio, studio, dj, live, other} — src/data/rentals.ts:86-162
hidden: No not-found state: an unknown slug shows "Equipment" with the empty state — :45. The "other" slug has no equipment, so it always shows the empty state.

### /marketplace/rent/e/$id (src/routes/marketplace/rent/e.$id.tsx)
default: Equipment detail with a sticky "Check availability" bar.
screens:
- Rental unavailable — trigger: equipmentById undefined — :48
overlays:
- "Report or block" sheet — opened by: Report this listing — :279 (:255); rental.tsx:665
tabs: none
data-driven: id ∈ shure-sm7b, fender-twin, pearl-export-kit, pioneer-xdj-rx3, jbl-pa-2k, swaraj-tabla, focusrite-18i20-rent, nord-stage-3 — rentals.ts:242-583
hidden: Share has no handler — :69. Every option in the report sheet only closes it — rental.tsx:680.

### /marketplace/rent/provider/$id (src/routes/marketplace/rent/provider.$id.tsx)
default: Provider header, stats and Equipment tab.
screens:
- Provider unavailable — trigger: providerById undefined — :35
- Equipment empty — trigger: items.length=0 — :118
overlays:
- "Report or block" sheet — opened by: Report or block this provider (About tab) — :176 (:166)
tabs: Equipment, Reviews, About — :27
data-driven: id ∈ soundhouse, ravi-gear, swaraj-instruments, pulse-live — rentals.ts:172-222
hidden: Equipment-empty can't appear because every provider has equipment (providerIds at rentals.ts:290-627). Follow has no handler — :99.

### /marketplace/rent/request/$id (src/routes/marketplace/rent/request.$id.tsx)
default: 4-step request flow. Step 1 is "dates".
screens:
- Unavailable — trigger: no equipment — :49
- Step 1 Check availability (calendar, quantity, end-date badge) — trigger: step="dates" — :113
- Step 2 Rental summary (fulfilment + price breakdown) — trigger: step="summary" — :155
- Step 3 Rental request (provider, details, message) — trigger: step="request" — :170
- Step 4 Request sent, TopBar "Rental confirmed" — trigger: step="confirmed" — :215, :81
overlays: none
tabs: none
data-driven: equipment ids as in e/$id
hidden: "Save draft" goes back to the summary step — :209. The FulfilmentPicker "Pickup only" fallback can't appear because no equipment has both pickup and delivery false — rental.tsx:424. Continue works without an end date — :149.

### /marketplace/rent/active/$id (src/routes/marketplace/rent/active.$id.tsx)
default: Booking detail (card, details list, provider, actions).
screens:
- Rental not found — trigger: bookingById undefined — :39
- Active actions (Request extension / Return) — trigger: bucket=Active and not returned — :117
- Completed actions (Rate / Book again / Browse similar) — trigger: bucket=Completed or returned=true — :52, :127
- Upcoming/Cancelled: no action buttons — trigger: bucket Upcoming/Cancelled — :117-149
overlays:
- "Extend rental" Sheet — opened by: Request extension — :156 (:119)
- "Return equipment" Sheet — opened by: Return equipment — :181 (:122)
- "Rate this rental" Sheet — opened by: Rate provider & equipment, or automatically after Confirm return — :230 (:129, :223)
tabs: none
data-driven: id ∈ b1 (Upcoming), b2 (Active), b3 (Completed), b4 (Cancelled) — rentals.ts:656-706
hidden: "Report an issue with this rental" has no handler — :150. Return condition chips have no onClick — :202. "Add return photos" has no handler — :207.

CROSS-CUTTING
- ListingSkeleton is defined but no route renders it — src/components/ml/market.tsx:321
- The FulfilmentPicker empty-state branch can't be reached with current data — src/components/ml/rental.tsx:424
- The SellerSummary/ProviderSummary `compact` variant is never passed by any route — market.tsx:284, rental.tsx:209
- RolesCard is route-local and only appears inside the Manage Members tab — bands.$id.manage.tsx:216
- Three band routes are complete screens but nothing links to them: /bands/$id/contact, /bands/$id/events and /bands/$id/portfolio (grep of src finds no links)
- None of these routes has an offline state or a location-permission screen

## Business, services

### /business/new (src/routes/business.new.tsx)
default: 6-step "Create business" wizard, one Card per `?step=`, with a progress bar, Back/Continue buttons and a TopBar showing "Step N of 6" (:49-212)
screens:
- Type — trigger: step=type, also the fallback for a missing or invalid value — :12, :68-94
- Basics — trigger: step=basics — :96-115
- Location — trigger: step=location — :117-137
- Spaces — trigger: step=spaces — :139-153
- Team — trigger: step=team — :155-168
- Review — trigger: step=review — :170-194
overlays:
- "Business created" Dialog — opened by: "Create business" on the review step — :207, :214-228
tabs: none
data-driven: the type step lists businessTypes from src/data/business.ts:11-28 (studio, label, production_house, venue, agency, rental). The chosen type only changes the Review label (:174), not later steps. City chips are hardcoded (:121).
hidden: nothing is saved, so the created business never appears in /business (:46, :207). src/routes/onboarding/complete.tsx:129 links here without `search`, so it lands on the type step.

### /business (src/routes/business.index.tsx)
default: "Businesses" list of myBusiness cards, a "+" link to /business/new?step=type, and a staff info card (:26-103)
screens:
- Empty "No business yet" — trigger: myBusinesses.length === 0 — :82-89
overlays: none
tabs: none
data-driven: myBusinesses in src/data/business.ts:79 holds one entry, "biz-1" (:81)
hidden: the empty state can't show, because myBusinesses always has one entry (data/business.ts:79-81)

### /business/$id (src/routes/business.$id.index.tsx)
default: business profile with cover, logo, Message/Book/Follow buttons, a stats row and chip tabs (:46-240)
screens:
- Business not found — trigger: getBusiness(id) is undefined — :35-44
overlays: none
tabs: Overview / Spaces / Team / Credits (:28, :113-119, :121-238)
data-driven: $id is looked up by getBusiness (data/business.ts:166). The only valid id is biz-1. relatedBusinesses biz-2, biz-3, biz-4 (data/business.ts:283-305) have no matching profile.
hidden: there is no owner-vs-visitor branch; everyone sees the same view, including the Manage cog (:53-60). There is no branch on business type either; `b.type` is only used as a label (:50, :80). The "Follow" button has no onClick (:94-96). "Book" always goes to /booking/svc-1 (:91). No link to /business/$id/enquiry or /business/$id/dashboard.

### /business/$id/manage (src/routes/business.$id.manage.tsx)
default: owner "Manage" page with a summary card, 5 Rows and a delete card (:42-107)
screens:
- Business not found — trigger: !b — :31-40
overlays:
- "Delete {name}?" confirm Dialog — opened by: "Delete {b.name}" — :103, :109-129
- "Business deleted" Dialog — opened by: "Yes, delete" — :118-121, :131-140
tabs: none
data-driven: none (same getBusiness lookup)
hidden: the TopBar back is the literal "/business/$id" with no params (:44); TopBar's Link receives no params (src/components/ml/ui.tsx:81-82). The "Details" Row goes to /business/new, which is the create wizard (:65). "Photos & spaces" goes to the public profile (:71). No Row links to /business/$id/dashboard or /business/$id/verification. Verification points to /verification/choose instead (:91).

### /business/$id/dashboard (src/routes/business.$id.dashboard.tsx)
default: "Dashboard" with a new-enquiries card, 3 stat cards and 7 Rows (Leads, Analytics, Services, Availability, Portfolio, Reviews, Verification) (:48-133)
screens:
- Business not found — trigger: !b — :32-44
overlays: none
tabs: none
data-driven: stats come from businessAnalytics and businessLeads (data/business.ts:239, :198). They are not keyed by business.
hidden: the whole route is unreachable in the UI. Nothing in src links to it; only the back arrows on analytics (:49) and leads (:52) point here.

### /business/$id/analytics (src/routes/business.$id.analytics.tsx)
default: 4 stat cards, weekly bars, source and room-demand meters, and a responsiveness card (:44-111)
screens:
- Business not found — trigger: !b — :28-40
overlays: none
tabs: none
data-driven: businessAnalytics (data/business.ts:239)
hidden: reachable only from the unreachable dashboard (dashboard.tsx:92)

### /business/$id/availability (src/routes/business.$id.availability.tsx)
default: opening hours, notice chips, blackout dates and a Save button (:46-100)
screens:
- Business not found — trigger: !b — :32-44
overlays: none
tabs: none
data-driven: bookingWindows and timeOff (data/services.ts:191, :178)
hidden: "Add blackout dates" has no onClick (:90-92). "Save" only relabels itself (:95-97). Reachable only via dashboard.tsx:106 and business.$id.services.tsx:66, which itself is reachable only from the dashboard.

### /business/$id/enquiry (src/routes/business.$id.enquiry.tsx)
default: "Enquire" page with intent chips, a message textarea, "Send enquiry" and a "Book a room" card (:76-129)
screens:
- Business not found — trigger: !b — :38-50
- Enquiry sent confirmation — trigger: sent === true, set by "Send enquiry" (disabled under 8 characters) — :52-74, :109
overlays: none
tabs: none
data-driven: intents are hardcoded (:29)
hidden: the route is unreachable. No link to /business/$id/enquiry exists in src.

### /business/$id/leads (src/routes/business.$id.leads.tsx)
default: filter chips plus lead cards with Reply and "Mark closed" (:50-102)
screens:
- Business not found — trigger: !b — :32-44
- Empty "Nothing in this queue" — trigger: filtered list is empty — :94-99
overlays: none
tabs: All / New / Replied / Booked (:25, :55-61)
data-driven: businessLeads (data/business.ts:198). Statuses are new, replied and booked, one lead each.
hidden: the empty state can't show, since every filter matches one lead. The "closed" status exists (data/business.ts:196) but has no filter. "Mark closed" has no onClick (:87-89). Reachable only from the dashboard (dashboard.tsx:85).

### /business/$id/portfolio (src/routes/business.$id.portfolio.tsx)
default: Segmented Credits/Gallery with a credit list or photo grid (:45-111)
screens:
- Business not found — trigger: !b — :31-43
- Empty "No credits yet" — trigger: Credits tab and b.credits.length === 0 — :79-84
overlays: none
tabs: Credits / Gallery (:24, :50)
data-driven: none
hidden: the empty state can't show, because biz-1 has 3 credits (data/business.ts:155-159). Edit, Hide, "Add a credit" and "Add photos" have no onClick (:65-70, :74, :83, :104). Reachable only from the dashboard (dashboard.tsx:113).

### /business/$id/reviews (src/routes/business.$id.reviews.tsx)
default: review cards, each with the owner reply shown or an inline reply composer (:45-120)
screens:
- Business not found — trigger: !b — :31-43
- Empty "No reviews yet" — trigger: businessReviews.length === 0 — :112-117
overlays: none (the inline composer is controlled by replyTo, :79-103)
tabs: none
data-driven: businessReviews (data/business.ts:260). brv-1 has no response; brv-2 has one.
hidden: the empty state can't show (2 reviews). Reachable only from the dashboard (dashboard.tsx:120).

### /business/$id/services (src/routes/business.$id.services.tsx)
default: "Business availability" Row, service cards with Edit/Pause, and a spaces card; "+" goes to /services/new (:43-117)
screens:
- Business not found — trigger: !b — :29-41
overlays: none
tabs: none
data-driven: none
hidden: Edit and Pause have no onClick (:81-86). The "+" opens the personal /services/new wizard (:50-52). Reachable only from the dashboard (dashboard.tsx:99).

### /business/$id/team (src/routes/business.$id.team.tsx)
default: member list plus a role-permissions card (:51-108)
screens:
- Business not found — trigger: !b — :37-46
- Empty "No team yet" — trigger: every member has status "removed" — :95-97
overlays:
- "Invite to team" Sheet — opened by: the UserPlus button (aria "Invite member") — :58-65, :110-128
- Member role Sheet (title is the member's name) — opened by: tapping a member row — :76, :130-158
tabs: none
data-driven: businessRoleLabel and rolePermissions (data/business.ts:168, :175)
hidden: TopBar back is the literal "/business/$id" with no params (:56). "Send invite" only closes the sheet (:124).

### /business/$id/verification (src/routes/business.$id.verification.tsx)
default: status card, 4 check cards, a privacy card and "Submit for review" (:47-111)
screens:
- Business not found — trigger: !b — :31-43
overlays: none. Submitting only changes the pill and button label to In review / Submitted (:65-67, :106-108).
tabs: none
data-driven: businessVerificationChecks (data/business.ts:307-326)
hidden: "Upload" has no onClick (:85-87). Reachable only from the dashboard (dashboard.tsx:127); manage links to /verification/choose instead.

### /services (src/routes/services.index.tsx)
default: "Services" with 3 stat cards, Availability and Booking policies Rows, filter chips and service cards (:36-137)
screens:
- Empty "No {filter} services" — trigger: filtered list is empty — :125-134
overlays: none
tabs: All / Active / Paused / Draft (:26, :86-92)
data-driven: myServices (data/services.ts:64) with svc-1 active, svc-2 active, svc-3 paused (:66-140)
hidden: none. The empty state only shows under "Draft", since no service is draft.

### /services/new (src/routes/services.new.tsx)
default: 5-step "Add a service" wizard with progress bar and Back/Continue (:50-209)
screens:
- Category — trigger: step=category, also the fallback — :17, :70-85
- Basics — trigger: step=basics — :87-112
- Pricing — trigger: step=pricing — :114-134
- Delivery — trigger: step=delivery — :136-164
- Review — trigger: step=review — :166-191
overlays:
- "Service published" Dialog — opened by: "Publish service" — :204, :211-220
tabs: none
data-driven: serviceCategories (data/services.ts:53-62: vocals, mixing, mastering, production, instrument, songwriting, live, studio), pricingModelLabel (:45) and bookingWindows (:191). None of them branch later steps.
hidden: the published service is not persisted (:48, :204)

### /services/availability (src/routes/services.availability.tsx)
default: status chips, weekly hour toggles, notice chips, time-off list and a confirmed-bookings card (:42-151)
screens: none (the "No time off" EmptyState at :131-136 sits inside a card and does not replace the view)
overlays:
- "Add time off" Sheet — opened by: "+ Add" — :104-110, :153-173
tabs: none
data-driven: availabilityStates (data/services.ts:183-188), weeklyAvailability (:168), timeOff (:178) and bookingWindows (:191)
hidden: none

### /services/conflicts (src/routes/services.conflicts.tsx)
default: info card, 3 conflict cards and "Adjust availability" (:48-102)
screens:
- Empty "No conflicts" — trigger: conflicts.length === 0 — :92-94
overlays: none
tabs: none
data-driven: none (hardcoded list, :24-46)
hidden: the route is unreachable; no link to /services/conflicts exists. The empty state can't show because the list is a constant of 3. "Offer another time" and "Keep both" have no onClick (:82-87).

### /services/$id (src/routes/services.$id.index.tsx)
default: service detail with header card, deliverables, packages (when any), add-ons (when any), requirements, and Availability / Preview booking buttons (:45-157)
screens:
- Service not found — trigger: getService(id) is undefined — :34-43
overlays:
- "Manage service" Sheet — opened by: "Manage" in the TopBar — :51-57, :159-190
- "Service removed" Dialog — opened by: "Remove service" in the sheet — :179-188, :192-201
tabs: none
data-driven: $id is looked up by getService (data/services.ts:156) against svc-1, svc-2 and svc-3. The paused state for svc-3 changes only the pill (:31, :80-81).
hidden: "Edit details & pricing" opens the blank create wizard (:161-168). Nothing links to /services/$id/manage from here.

### /services/$id/manage (src/routes/services.$id.manage.tsx)
default: "Manage service" with a summary card, Pause/Live, Duplicate and Delete Rows, and a "Before you delete" card (:48-105)
screens:
- Service not found — trigger: !s — :34-46
overlays:
- "Delete "{title}"?" Dialog — opened by: the "Delete this service" Row — :94, :107-117. "Delete service" navigates to /services (:113).
tabs: none
data-driven: none. The inline paused notice depends on status === "paused" (:65-73).
hidden: the route is unreachable; no link to /services/$id/manage exists

### /services/$id/enquiry (src/routes/services.$id.enquiry.tsx)
default: "Enquire" with a summary card, mode chips and a mode-specific form (:80-152)
screens:
- Service not found — trigger: !s — :37-49
- "Question sent" confirmation — trigger: sent with mode Quick question — :51-73, :59-64
- "Quote request sent" confirmation — trigger: sent with mode Custom quote — :51-73, :59-64
overlays: none
tabs: Quick question / Custom quote (:26, :92-98, :100-145)
data-driven: none
hidden: the route is unreachable; no link to /services/$id/enquiry exists

None of the 21 routes has a loading, error or offline state.

BUSINESS OWNER ENTRY
- **After /business/new:** the "Business created" dialog's "Open business" goes to /business, the list, not to the new business (business.new.tsx:221). "Verify this business" goes to /verification/choose (:224). Closing the dialog leaves you on the review step (:218).
- **From /business:** the only way onward is a card link to /business/$id (business.index.tsx:47). Nothing links to the dashboard.
- **From /business/$id:** it links to /business/$id/manage (business.$id.index.tsx:54) and /business/$id/team (:220). Nothing links to the dashboard or its sub-pages.
- **From /business/$id/manage:** it links to /business/new (:65), /business/$id (:71), /business/$id/team (:78), /provider/bookings/calendar (:85) and /verification/choose (:91). Nothing links to the dashboard.
- **Result:** no link to /business/$id/dashboard exists anywhere in src. Only back arrows point to it, from analytics (:49) and leads (:52). So leads, analytics, portfolio, reviews and verification can only be reached by typing the URL, as can services and its availability page. /business/$id/verification's back arrow goes to manage (:49).

CROSS-CUTTING
- **Unreachable routes:** business.$id.dashboard.tsx:7, business.$id.enquiry.tsx:8, services.conflicts.tsx:6, services.$id.manage.tsx:8 and services.$id.enquiry.tsx:8 have no inbound link. Everything under the dashboard is reachable only through it.
- **No unrendered screen components:** the only local component in these files is Field (business.new.tsx:233, services.new.tsx:225), and both are used. No component in src/components/ml decides a full view for these routes; they use Screen, TopBar, Dialog, Sheet, EmptyState, Row and Segmented.
- **Unused data exports:** businessSteps (data/business.ts:184), businessDraftKey (:182), serviceDraftKey (data/services.ts:194) and relatedBusinesses (data/business.ts:283) are defined but never used.
- **Two paths for the same actions:** /services/$id has its own manage Sheet (services.$id.index.tsx:159), separate from the /services/$id/manage route.
- **Unused Loading state:** the Loading component (src/components/ml/kit.tsx:147) is used by other routes but by none of these 21.

## Booking, bookings, portfolio, provider

### /booking/$id (layout) (src/routes/booking.$id.tsx)
default: No UI of its own. Wraps child routes in BookingDraftProvider, which holds the draft in shared state (booking.$id.tsx:11-13).
screens:
- Space unavailable ("This space is unavailable") — trigger: $id not in `bookables`, or no resource — booking-context.tsx:75-86
overlays: none
tabs: none
data-driven: $id values: octave-sound-labs (booking.ts:212, deposit 25%), headroom-mastering (:329, 50%), basement-jam (:384, 0%), waveform-podcast (:449, 30%)
hidden: Every in-app link except one passes a services id, so it lands on the unavailable screen. Those links: `s.id` (services.$id.index.tsx:153), "svc-1" (business.$id.index.tsx:91, business.$id.enquiry.tsx:120, opportunities.$id.applicants.$aid.tsx:168). The only link with a real id is "Book again" (bookings.$ref.index.tsx:178). The `identity.ts` publicRoute strings (:97,:104,:146) are not rendered by any .tsx. `draft.acknowledged` is never set (booking-context.tsx:14,45).

### /booking/$id/ (src/routes/booking.$id.index.tsx)
default: Space entry page (step 0): hero, rooms, facilities, equipment, top 3 policies, gallery, sticky "Check availability" → service.
screens: none
overlays: none
tabs: none
data-driven: see layout
hidden: Bookmark and Share buttons do nothing (:56, :59). The "See all terms at review" link (:132) skips to review with no slot chosen, which shows review's "Pick a date and time first" state.

### /booking/$id/service (src/routes/booking.$id.service.tsx)
default: Funnel step 1 of 5: ResourceCard list to select a room; "Check dates" → schedule (:54).
screens: none
overlays: none
tabs: none
data-driven: resources per bookable (booking.ts:232-474)
hidden: none

### /booking/$id/schedule (src/routes/booking.$id.schedule.tsx)
default: Funnel step 2 of 5: availability calendar, then slot picker once a date is picked, then duration picker once a slot is picked. "Continue" → details (:214).
screens:
- Loading (CalendarSkeleton) — trigger: sim="loading" via "Loading state" button — :90-91, :195
- Error ("Availability didn't load") — trigger: sim="error" via "Error state" button — :92-97, :198
- Offline banner (calendar hidden) — trigger: sim="offline" via TopBar "Demo states" — :76, :87
- No availability on this date (EmptyState) — trigger: date picked and openSlots.length===0 — :122-146
overlays:
- "This time is no longer available" Sheet — opened by: tapping the 12:00 slot — :153-157, :217-244
tabs: none
data-driven: durations filtered by `r.durations` (booking.ts:237-473); durationOptions (booking.ts:582-589)
hidden: The "No availability" EmptyState (:122-146) cannot be reached. The calendar disables unavailable and closed days (booking.tsx:289), and every day you can select has at least 3 open slots (booking.ts:548-555). Its "Message provider" and "See similar spaces" links are unreachable with it. Funnel order: schedule goes to details (:214), not addons.

### /booking/$id/details (src/routes/booking.$id.details.tsx)
default: Funnel step 3 of 5: purpose select, project name, headcount stepper, requirements. "Continue" → addons (:108).
screens: none
overlays: none
tabs: none
data-driven: purpose options hard-coded (:49-56)
hidden: none

### /booking/$id/addons (src/routes/booking.$id.addons.tsx)
default: Funnel step 4 of 5: AddOnCard list plus running PriceBreakdownCard. "Review booking" → review (:59).
screens: none
overlays: none
tabs: none
data-driven: `b.addOns` per bookable (booking.ts:281-493). The "Unavailable" pill shows when `a.available` is false (booking.tsx:484-487).
hidden: none

### /booking/$id/review (src/routes/booking.$id.review.tsx)
default: Funnel step 5 of 5: summary, details (with "Edit details"), price, all policies, acknowledge checkbox. The sticky button reads "Confirm booking" (instant) or "Request booking" (request) (:126).
screens:
- Pick a date and time first — trigger: !draft.date || !draft.start — :47-56
- (Exit) deposit > 0 → /booking/$id/pay; deposit = 0 → /booking/confirmed/MLB-NEW-C (instant) or MLB-NEW-R (request) — :127-135
overlays: none
tabs: none
data-driven: resource `model` instant/request (booking.ts:238,254,269,355,409,424,474)
hidden: The deposit-0 "request" path to MLB-NEW-R (:133) never happens. The only 0% bookable is basement-jam (booking.ts:399), and both its rooms are instant (:409, :424).

### /booking/$id/pay (src/routes/booking.$id.pay.tsx)
default: Deposit payment form: PaymentSummaryCard, UPI/Card method rows, "Pay securely" (no StepDots).
screens:
- Processing (inline status; button disabled) — trigger: phase="processing" on "Pay securely" — :117-125, :141-143
- Payment successful (full Result view) — trigger: phase="success" 900 ms after processing — :35-54, :143
- Payment could not be completed (full Result view) — trigger: phase="failed" via "Preview failure state" — :56-85, :127-133
overlays: none
tabs: none
data-driven: none
hidden: The failed state is reachable only through the "Preview failure state" demo button (:129). Real payment always succeeds (:143). Success "View booking" always goes to MLB-NEW-C (:48).

### /booking/confirmed/$ref (src/routes/booking.confirmed.$ref.tsx)
default: Confirmation screen. Room, date, time and price are hard-coded from bookables[0] and are not tied to $ref (:31-35, :62-66).
screens:
- Booking confirmed (CheckCircle, "Deposit paid", Add to calendar/Share) — trigger: $ref not ending "R" — :30, :43-51, :71-80
- Booking request sent (Clock, "Nothing charged yet") — trigger: $ref ends with "R" — :30, :40-50
overlays: none
tabs: none
data-driven: $ref values linked: MLB-NEW-C (review.tsx:133, pay.tsx:48), MLB-NEW-R (review.tsx:133)
hidden: The "Booking request sent" variant is unreachable in the UI (see review). "Add to calendar" and "Share" do nothing (:73, :76).

### /bookings/ (src/routes/bookings.index.tsx)
default: My bookings: status chips plus BookingListCard list, a "Manage a space you run" link to /provider/bookings, and demo state buttons.
screens:
- Loading (BookingSkeleton) — trigger: sim="loading" via "Loading" — :76-77, :107
- Error — trigger: sim="error" via "Error" — :78-82, :110
- Offline banner above list — trigger: sim="offline" via "Offline" — :74, :113
- Empty ("No bookings yet") — trigger: sim="empty" via TopBar "States", or filtered list empty — :47, :57, :83-88
overlays: none
tabs: Upcoming / Pending / Completed / Cancelled (:34-39)
data-driven: `bookings` (booking.ts:660-743): MLB-4821 upcoming, MLB-4835 requested, MLB-4712 completed, MLB-4690 cancelled
hidden: With the seeded data no tab is empty on its own, so the empty state appears only through the "States" toggle.

### /bookings/$ref/ (src/routes/bookings.$ref.index.tsx)
default: Customer booking detail: status and payment pills, card, timeline, price, terms, actions.
screens:
- Booking unavailable — trigger: getBooking(ref) or bookable/resource missing — :50-63
- Today banner — trigger: status upcoming/confirmed and date = today (MLB-4821) — :74-84
- Upcoming actions (Add to calendar, Directions, Reschedule, Cancel) — trigger: status upcoming/confirmed — :149-174
- Completed actions (Book again, Leave a review) — trigger: status completed — :176-188
- Requested/cancelled: only "Message provider" — trigger: other statuses — :145-147
overlays: none
tabs: none
data-driven: refs from `getBooking`, which searches bookings, providerRequests and providerToday (booking.ts:852-853)
hidden: "Add to calendar", "Get directions" and "Leave a review" do nothing (:151, :154, :181). MLB-4901/4903/4905/4880 resolve here but nothing on the customer side links to them.

### /bookings/$ref/cancel (src/routes/bookings.$ref.cancel.tsx)
default: Cancel form: booking card, cancellation policy, reason list, optional message. "Cancel booking" is disabled until a reason is picked.
screens:
- Booking not found — trigger: missing booking — :42-51
- Booking cancelled (success) — trigger: done=true via "Cancel booking" — :55-77, :140
overlays: none
tabs: none
data-driven: reasons hard-coded (:25-32)
hidden: The message textarea is uncontrolled and its value is never read (:127-131).

### /bookings/$ref/reschedule (src/routes/bookings.$ref.reschedule.tsx)
default: Current slot note, AvailabilityCalendar, SlotPicker after a date is picked, "Request reschedule".
screens:
- Booking not found — trigger: missing booking — :47-56
- Reschedule request sent (success) — trigger: sent=true via "Request reschedule" — :60-84, :134
overlays: none
tabs: none
data-driven: none
hidden: SlotPicker renders nothing when there are zero slots (booking.tsx:356-358), but selectable days always have slots.

### /provider/bookings/ (src/routes/provider.bookings.index.tsx)
default: Provider workspace: 4 stat tiles, links (Requests, Calendar, Availability, Pricing, Policies), tab content.
screens:
- Loading (BookingSkeleton) — trigger: loading=true via "Preview loading state" — :108-109, :136
- No pending requests — trigger: providerRequests empty — :111-112
- "Nothing today/upcoming/past" — trigger: list empty — :145-147
overlays: none
tabs: Requests / Today / Upcoming / Past (:43)
data-driven: providerRequests (booking.ts:745), providerToday (:809), bookings (:660)
hidden: The empty states (:112, :146) cannot be reached with seeded data. Requests has 3, Today 2, Upcoming 1 (MLB-4821), Past 1 (MLB-4712).

### /provider/bookings/$ref (src/routes/provider.bookings.$ref.tsx)
default: Provider session detail: customer, timing, timeline, payout, message and availability links.
screens:
- Session not found — trigger: ref not in bookings + providerToday — :46, :50-63
overlays: none
tabs: none
data-driven: refs MLB-4821/4835/4712/4690 (booking.ts:660-743), MLB-4880 (:831)
hidden: Calendar BlockRows link request refs MLB-4901/4903/4905 here (provider.bookings.calendar.tsx:38, :112-114). This lookup leaves out providerRequests (:46), so those taps show "Session not found".

### /provider/bookings/calendar (src/routes/provider.bookings.calendar.tsx)
default: Day view with Prev/Next navigation and a list of BlockRows linking to /provider/bookings/$ref.
screens:
- Day: Nothing booked (Edit availability link) — trigger: no bookings on date — :140-152
- Week view — trigger: view="Week" — :69, :160-202 ("Open all day" per empty day, :193)
- Month view (current month only; tapping a day switches to Day) — trigger: view="Month" — :70-77, :204-243
overlays: none
tabs: Day / Week / Month (:36)
data-driven: providerToday + providerRequests + bookings (:38)
hidden: A day-header button in Week view (:174-177) sets the cursor but keeps the Week view, and the same week re-renders. Month view has no month navigation (:205-206).

### /provider/bookings/requests/ (src/routes/provider.bookings.requests.index.tsx)
default: Request queue of ProviderRequestCards.
screens:
- Loading (700 ms) — trigger: TopBar "Refresh" — :45-54, :67-68
- No requests here — trigger: filtered list empty — :69-73
overlays: none
tabs: All / New / In review (:28)
data-driven: providerRequests MLB-4901 requested, MLB-4903 reviewing, MLB-4905 requested (booking.ts:747-795)
hidden: The empty state (:69-73) cannot be reached with seeded data.

### /provider/bookings/requests/$ref (src/routes/provider.bookings.requests.$ref.tsx)
default: Request review: customer, session details, earnings, "Ask a question first", "Accept request".
screens:
- Request no longer available — trigger: ref not in providerRequests — :40, :45-58
- Declining reason panel (inline) — trigger: decision="declining" via "Propose a different time or decline" — :132-159
- Request accepted (result) — trigger: "Accept request" — :62-83, :166
- Request declined (result) — trigger: any reason chip — :62-83, :144
overlays: none
tabs: none
data-driven: declineReasons (booking.ts:893); refs as above
hidden: "Propose a different time" has no proposal UI. Every reason chip declines directly (:144).

### /provider/availability (src/routes/provider.availability.tsx)
default: Per-room opening days, block-date calendar with blocked list, instant-booking toggle, rates, cancellation card.
screens: none
overlays: none
tabs: room chips swap content (live-room-a / vocal-booth / mix-room) (:42-48)
data-driven: always bookables[0] (octave-sound-labs) (:31)
hidden: "Save availability" does nothing (:151).

### /provider/policies (src/routes/provider.policies.tsx)
default: Cancellation-window chips plus 7 policy textareas; "Save policies" shows a toast.
screens: none
overlays: none
tabs: none (the window chips select a value)
data-driven: octave-sound-labs policies (:70)
hidden: none

### /provider/pricing (src/routes/provider.pricing.tsx)
default: Per-room tier rate inputs, add-on prices, deposit slider; "Save pricing" shows a toast.
screens:
- Blank (returns null) — trigger: entity missing — :40
overlays: none
tabs: none
data-driven: octave-sound-labs (:27)
hidden: The null state (:40) cannot happen because the hard-coded id exists (booking.ts:212).

### /portfolio/ (src/routes/portfolio.index.tsx)
default: Signed in: stats header, action buttons (Add work, Reorder, Performance, Share work, Preview on profile), Featured (first 3), All work.
screens:
- Signed out: SignedOutCard plus demo work — trigger: !signedIn && !authLoading — :46, :87-91
- Loading skeleton — trigger: signedIn && (q.isLoading || authLoading) — :106
- Error ("We couldn't load your portfolio") — trigger: signedIn && q.isError — :108-115
- Empty ("Your work belongs here") — trigger: items.length===0 — :117-123
overlays: none
tabs: none
data-driven: demoPortfolio demo-1/2/3 (portfolio.ts:167-195)
hidden: none

### /portfolio/$id/ (src/routes/portfolio.$id.index.tsx)
default: Work detail: meta, media (AudioPlayer / VideoPlayer / link), credits, analytics, Share / Add another.
screens:
- Demo work (no Edit, no Delete) — trigger: id in demoPortfolio — :48, :166, :179
- Owned work (Edit plus "Delete this work") — trigger: fetched item — :166-187
- Loading — trigger: signedIn && !demo && q.isLoading — :81
- Error — trigger: signedIn && q.isError — :83-90 (the unavailable card also renders with it, :92)
- No longer available — trigger: !loading && !q.isLoading && !item (includes signed out with a non-demo id) — :92-98
- Audio not-uploaded card — trigger: audio kind with no media_url — portfolio.tsx:337-343
overlays: none
tabs: none
data-driven: demo-1/2/3 (portfolio.ts:169,182,195); media type per kind (portfolio.ts:45-112)
hidden: none

### /portfolio/$id/edit (src/routes/portfolio.$id.edit.tsx)
default: WorkForm pre-filled; button reads "Save changes".
screens:
- Loading — trigger: q.isLoading — :43
- You can't edit this work — trigger: !q.isLoading && !q.data (includes signed out, since the query is disabled) — :44-49
- Saving… — trigger: busy — work-form.tsx:173
overlays: none
tabs: none
data-driven: none
hidden: Demo ids have no Edit link (portfolio.$id.index.tsx:166), so /portfolio/demo-*/edit can only be reached by typing the URL.

### /portfolio/new/ (src/routes/portfolio.new.index.tsx)
default: "Step 1 of 3", list of work types linking to /portfolio/new/$kind.
screens: none
overlays: none
tabs: none
data-driven: workTypes (portfolio.ts:37-113)
hidden: No screen is labelled "Step 3 of 3". Saving goes to /portfolio/$id (work-form.tsx:73).

### /portfolio/new/$kind (src/routes/portfolio.new.$kind.tsx)
default: "Step 2 of 3", WorkForm for the kind; button reads "Publish work".
screens:
- Signed out: SignedOutCard above the form; submitting redirects to /welcome — trigger: !signedIn && !loading — :39-43, work-form.tsx:50-53
- Media field hidden — trigger: kind media "none" (case-study, writing) — work-form.tsx:137
- MediaUploader states not-uploaded / uploading / processing / ready / failed (inline) — portfolio.tsx:247-321
overlays: none
tabs: none
data-driven: $kind = track, album, video, performance, project, client, case-study, writing, image, link (portfolio.ts:45-112)
hidden: An unknown $kind falls back to the Track form (portfolio.ts:117) but still saves the raw kind (work-form.tsx:62). "Preview failure state" in MediaUploader is a demo-only trigger (portfolio.tsx:318-320).

### /portfolio/analytics (src/routes/portfolio.analytics.tsx)
default: Totals grid (WorkAnalytics) plus per-work rows linking to /portfolio/$id.
screens:
- Loading — trigger: signedIn && q.isLoading — :46
- Error — trigger: signedIn && q.isError — :48-55
- Nothing to measure yet — trigger: items.length===0 — :63-69
- Signed out: demo data, no signed-out card — :39
overlays: none
tabs: none
data-driven: demoPortfolio
hidden: none

### /portfolio/reorder (src/routes/portfolio.reorder.tsx)
default: ReorderList with up/down buttons and FeaturedToggle (max 3); "Save order" shows a toast.
screens:
- Loading — trigger: signedIn && q.isLoading — :76
- Nothing to reorder yet — trigger: order.length===0 — :78-84
overlays: none
tabs: none
data-driven: FEATURED_LIMIT=3 (portfolio.ts:341)
hidden: none

### /portfolio/select (src/routes/portfolio.select.tsx)
default: Selectable work list, Preview card, "Attach work" (toast). Back goes to /messages.
screens:
- Loading — trigger: signedIn && q.isLoading — :49
- No work to share yet — trigger: items.length===0 — :51-57
overlays: none
tabs: none
data-driven: demoPortfolio
hidden: "Attach" only shows a toast and does not return to a conversation (:109-115).

### /portfolio/settings (src/routes/portfolio.settings.tsx)
default: Visibility options, default ordering, audio autoplay switch, "Done" → /portfolio.
screens: none
overlays: none
tabs: none
data-driven: visibilityOptions (portfolio.ts:151-155)
hidden: The settings are local state only and nothing reads them (:25-27).

CROSS-CUTTING
- AudioPreview (portfolio.tsx:124-135) is defined and exported but not rendered anywhere in src.
- The PortfolioHeader `visibility` prop is never passed, so it always shows "Public" (portfolio.tsx:179; portfolio.index.tsx:64-69).
- The schedule EmptyState "No availability on this date" is defined in a route but unreachable (booking.$id.schedule.tsx:122-146).
- Confirmation variant "Booking request sent" (booking.confirmed.$ref.tsx:40-50) is unreachable through the funnel.
- Pay Result "failed" (booking.$id.pay.tsx:56-85) is reachable only through a demo button.
- Provider empty states (provider.bookings.index.tsx:112, :146; provider.bookings.requests.index.tsx:70-73) never show with seeded data.
- The overlay primitive Sheet (ui.tsx:221) appears only once in these routes (booking.$id.schedule.tsx:217). No Dialog, Modal or Drawer is used in any of these routes.
