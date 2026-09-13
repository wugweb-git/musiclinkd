---
doc: REFACTOR-REVIEW-URLMAP
version: 1.1
status: PARTLY STALE
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
folder: audits
originally: REFACTOR-REVIEW-URLMAP.md
---

> **PARTLY STALE** · v1.1 · 2026-09-13. Sections 1-4 (the /feed review) are live and unmigrated into any phase. Section 5 (the URL table) is superseded by the .index.tsx rename and should be script output.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

> **Corrections applied 13 Sep 2026.** Route counts raised 204 → 207, and all 12 parent routes
> renamed to their `.index.tsx` form. **Two findings this file recorded but did not flag:**
> (1) `/booking/:id` is listed twice in §5, mapped to both `booking.$id.index.tsx` and
> `booking.$id.tsx` — that is the parent-swallows-child bug appearing in the data three days before
> it was diagnosed, tabulated as two URLs instead of raised as a defect, which also means the
> original 204 count double-counted. (2) The file uses 204 for route *files*, *URLs* and *bodies*
> interchangeably; after the rename those are three different numbers.
>
> §§1–4 (the `/feed` review) are **live and exist in no phase spec** — the SSR skeleton, the three
> fabricated literals, and the Bell icon linking to `/messages` under a Notifications label. Do not
> archive this file until they are migrated.

# MusicLinkd — refactor review + URL map

Scope: the Lovable repo `musiclinkd-9e6ca875` at HEAD `315564c5`, and the live
`https://musiclinkd.lovable.app/feed`. Findings verified by reading files and fetching the live
page — not from comments or commit messages.

**207 route files → 204 URLs, 38 top-level groups, 113 dynamic.** Full table in §5.

I do not push code. This is the review; Claude Code executes.

---

## 1. The finding that matters most — `/feed` server-renders nothing

I fetched the live page. The SSR HTML contains **only the header and the bottom nav**. No greeting,
no figures, no sections, no activity. The body is blank until JavaScript runs.

The cause is in `src/routes/feed.tsx`:

```ts
const t = setTimeout(() => { setData(dashboardData); setStatus("ready"); }, 450);
```

`dashboardData` is a **static import** — it is already in the bundle. It is put behind a
client-only `useEffect` plus an artificial 450 ms timer, so on the server `status` is `"loading"`
and the route renders `<SkeletonBody />`, which is nothing but empty grey divs.

Consequences, all real:

- Every first paint shows a skeleton for at least 450 ms plus hydration, for data that was never
  fetched from anywhere.
- Crawlers and social unfurls see an empty page. The route's own `head()` advertises *"requests,
  sessions, matches and activity"* — none of it is in the HTML.
- This is a **TanStack Start SSR app**. Server rendering is the reason the stack was chosen; this
  route opts out of it for no gain.

**Fix:** render `dashboardData` directly. Keep the `loading`/`error`/`offline` states for when a
real query replaces it — the states are good and worth keeping — but the initial state should be
`ready` with the data already present. Delete the timer.

*(I expected a hydration mismatch from `greeting()` calling `new Date().getHours()` — server in UTC,
client in IST. There isn't one, because the body never renders server-side. It becomes a live bug
the moment the fix above lands, so handle it in the same change: compute the greeting after mount,
or pass a stable timestamp.)*

## 2. Fabricated figures on the dashboard

```jsx
<p className="text-[24px] font-bold leading-none">5</p>   → "Matching you"
<p className="text-[24px] font-bold leading-none">2</p>   → "Your applications"
<p className="...">1 shortlisted</p>
```

These are **literals in the JSX**, not even seed data from `src/data/`. The standing rule in the
project knowledge is *"never fabricate availability, counts, ratings or transaction state."* Three
invented numbers sit on the first screen a signed-in user sees. The same commit that removed the
invented `/welcome` stats left these.

Either derive them from `dashboardData` or render the tiles without a count.

## 3. The Bell icon lies about where it goes

```jsx
<Link to="/messages" aria-label="Notifications">  <Bell size={18} />
```

`aria-label` says Notifications, destination is `/messages`, and **`/notifications` exists** as its
own route. A screen-reader user is told one thing and taken somewhere else. Pick one.

## 4. Refactor findings by smell

**Magic numbers / arbitrary type — `feed.tsx` alone has 20+.**
`text-[28px]` `text-[24px]` `text-[17px]` `text-[16px]` `text-[14px]` `text-[13px]` `text-[12px]`
`text-[11px]`. Phase 2 swept `src/components/ml/` only; all 207 route bodies are untouched. This
file is the densest example — and it is the dashboard.

**Inline `style` for static tokens — 6 in `feed.tsx`.**
`background: "var(--success-container)"`, `var(--pink-container)`, `var(--border)`, `var(--primary)`.
These are static token references and belong in Tailwind classes. The two `width: ${n}%` progress
bars are legitimately dynamic — leave those.

**Retired token still in use.** The empty-state CTA uses `bg-navy`. Navy is retired in
`tokens.json`; it still renders only because `styles.css` keeps a deliberate back-compat shim.

**Dead union member.** `QuietDestination` includes `"/verification"`; no `QuietAction` uses it.

**Dangling timer.** `load()` returns its `clearTimeout` closure, but
`window.addEventListener("online", load)` discards the return value. A timer started by an `online`
event is never cleared. Small, but it is the kind of thing that survives forever once shipped.

**Duplicated card shape.** The "Opportunities" tiles and the "For you" tiles are the same markup
with different content — `min-w-0 rounded-3xl border border-border bg-card p-4`, a big number, a
label, a hint. Two copies, one component's worth of behaviour. Extract one `StatTile`.

## 5. URL map

207 routes. Naming is flat dot-segments (`booking.$id.pay.tsx` → `/booking/:id/pay`), except
`marketplace/` and `onboarding/` which use real directories — **two conventions in one router**,
worth normalising.

Three names worth a decision:

- **`/band`** (singular, 1 route) alongside **`/bands`** (8 routes). Almost certainly a leftover.
- **`/talent/:id`** — scheduled for redirect to `/profile/:username` since 2 Sep. The redirect
  exists in `talent.$id.tsx`; the link-repointing half is unverified.
- **`/recommendations`** (2 routes) and **`/recommended`** (1 route) are different URLs for what
  reads like one idea. `feed.tsx` links to `/recommendations`; `index.tsx` links to both.

### Auth — `auth` · 10 routes

| URL | route file |
|---|---|
| `/auth` | `auth.index.tsx` |
| `/auth/forgot` | `auth.forgot.tsx` |
| `/auth/locked` | `auth.locked.tsx` |
| `/auth/maintenance` | `auth.maintenance.tsx` |
| `/auth/offline` | `auth.offline.tsx` |
| `/auth/otp` | `auth.otp.tsx` |
| `/auth/phone` | `auth.phone.tsx` |
| `/auth/signout` | `auth.signout.tsx` |
| `/auth/success` | `auth.success.tsx` |
| `/auth/verify-email` | `auth.verify-email.tsx` |

### Band (legacy singular) — `band` · 1 routes

| URL | route file |
|---|---|
| `/band` | `band.tsx` |

### Bands — `bands` · 8 routes

| URL | route file |
|---|---|
| `/bands` | `bands.index.tsx` |
| `/bands/:id` | `bands.$id.index.tsx` |
| `/bands/:id/contact` | `bands.$id.contact.tsx` |
| `/bands/:id/events` | `bands.$id.events.tsx` |
| `/bands/:id/invite` | `bands.$id.invite.tsx` |
| `/bands/:id/manage` | `bands.$id.manage.tsx` |
| `/bands/:id/portfolio` | `bands.$id.portfolio.tsx` |
| `/bands/new` | `bands.new.tsx` |

### Booking wizard — `booking` · 9 routes

| URL | route file |
|---|---|
| `/booking/:id` | `booking.$id.index.tsx` |
| `/booking/:id` | `booking.$id.tsx` |
| `/booking/:id/addons` | `booking.$id.addons.tsx` |
| `/booking/:id/details` | `booking.$id.details.tsx` |
| `/booking/:id/pay` | `booking.$id.pay.tsx` |
| `/booking/:id/review` | `booking.$id.review.tsx` |
| `/booking/:id/schedule` | `booking.$id.schedule.tsx` |
| `/booking/:id/service` | `booking.$id.service.tsx` |
| `/booking/confirmed/:ref` | `booking.confirmed.$ref.tsx` |

### Bookings (mine) — `bookings` · 4 routes

| URL | route file |
|---|---|
| `/bookings` | `bookings.index.tsx` |
| `/bookings/:ref` | `bookings.$ref.index.tsx` |
| `/bookings/:ref/cancel` | `bookings.$ref.cancel.tsx` |
| `/bookings/:ref/reschedule` | `bookings.$ref.reschedule.tsx` |

### Business profile & console — `business` · 14 routes

| URL | route file |
|---|---|
| `/business` | `business.index.tsx` |
| `/business/:id` | `business.$id.index.tsx` |
| `/business/:id/analytics` | `business.$id.analytics.tsx` |
| `/business/:id/availability` | `business.$id.availability.tsx` |
| `/business/:id/dashboard` | `business.$id.dashboard.tsx` |
| `/business/:id/enquiry` | `business.$id.enquiry.tsx` |
| `/business/:id/leads` | `business.$id.leads.tsx` |
| `/business/:id/manage` | `business.$id.manage.tsx` |
| `/business/:id/portfolio` | `business.$id.portfolio.tsx` |
| `/business/:id/reviews` | `business.$id.reviews.tsx` |
| `/business/:id/services` | `business.$id.services.tsx` |
| `/business/:id/team` | `business.$id.team.tsx` |
| `/business/:id/verification` | `business.$id.verification.tsx` |
| `/business/new` | `business.new.tsx` |

### Categories — `categories` · 1 routes

| URL | route file |
|---|---|
| `/categories` | `categories.tsx` |

### Collaboration — `collab` · 30 routes

| URL | route file |
|---|---|
| `/collab` | `collab.index.tsx` |
| `/collab/:id` | `collab.$id.index.tsx` |
| `/collab/:id/activity` | `collab.$id.activity.tsx` |
| `/collab/:id/agreement` | `collab.$id.agreement.tsx` |
| `/collab/:id/discussion` | `collab.$id.discussion.tsx` |
| `/collab/:id/files` | `collab.$id.files.index.tsx` |
| `/collab/:id/files/:fileId` | `collab.$id.files.$fileId.tsx` |
| `/collab/:id/invite-link` | `collab.$id.invite-link.tsx` |
| `/collab/:id/lifecycle/:action` | `collab.$id.lifecycle.$action.tsx` |
| `/collab/:id/media` | `collab.$id.media.tsx` |
| `/collab/:id/milestones/:mid` | `collab.$id.milestones.$mid.tsx` |
| `/collab/:id/milestones/new` | `collab.$id.milestones.new.tsx` |
| `/collab/:id/notes` | `collab.$id.notes.tsx` |
| `/collab/:id/participants` | `collab.$id.participants.tsx` |
| `/collab/:id/remove/:pid` | `collab.$id.remove.$pid.tsx` |
| `/collab/:id/roles` | `collab.$id.roles.tsx` |
| `/collab/:id/settings` | `collab.$id.settings.tsx` |
| `/collab/:id/share` | `collab.$id.share.tsx` |
| `/collab/:id/timeline` | `collab.$id.timeline.tsx` |
| `/collab/:id/transfer` | `collab.$id.transfer.tsx` |
| `/collab/invites/:id` | `collab.invites.$id.tsx` |
| `/collab/invites/accepted/:id` | `collab.invites.accepted.$id.tsx` |
| `/collab/invites/declined/:id` | `collab.invites.declined.$id.tsx` |
| `/collab/new` | `collab.new.index.tsx` |
| `/collab/new/collaborators` | `collab.new.collaborators.tsx` |
| `/collab/proposals/:id` | `collab.proposals.$id.index.tsx` |
| `/collab/proposals/:id/counter` | `collab.proposals.$id.counter.tsx` |
| `/collab/proposals/accepted/:id` | `collab.proposals.accepted.$id.tsx` |
| `/collab/proposals/declined/:id` | `collab.proposals.declined.$id.tsx` |
| `/collab/proposals/new` | `collab.proposals.new.tsx` |

### Discover — `discover` · 4 routes

| URL | route file |
|---|---|
| `/discover/community` | `discover.community.tsx` |
| `/discover/music` | `discover.music.tsx` |
| `/discover/nearby` | `discover.nearby.tsx` |
| `/discover/projects` | `discover.projects.tsx` |

### Dashboard — `feed` · 1 routes

| URL | route file |
|---|---|
| `/feed` | `feed.tsx` |

### Help centre — `help` · 3 routes

| URL | route file |
|---|---|
| `/help` | `help.index.tsx` |
| `/help/:slug` | `help.$slug.tsx` |
| `/help/category/:key` | `help.category.$key.tsx` |

### Identity / QR card — `identity` · 13 routes

| URL | route file |
|---|---|
| `/identity/analytics` | `identity.analytics.tsx` |
| `/identity/business` | `identity.business.tsx` |
| `/identity/card` | `identity.card.tsx` |
| `/identity/contact` | `identity.contact.index.tsx` |
| `/identity/contact/save` | `identity.contact.save.tsx` |
| `/identity/customize` | `identity.customize.tsx` |
| `/identity/full` | `identity.full.tsx` |
| `/identity/history` | `identity.history.tsx` |
| `/identity/print` | `identity.print.tsx` |
| `/identity/q/:code` | `identity.q.$code.tsx` |
| `/identity/qr` | `identity.qr.tsx` |
| `/identity/scan` | `identity.scan.tsx` |
| `/identity/settings` | `identity.settings.tsx` |

### Discover (home) — `index` · 1 routes

| URL | route file |
|---|---|
| `/` | `index.tsx` |

### Legal — `legal` · 2 routes

| URL | route file |
|---|---|
| `/legal/privacy` | `legal.privacy.tsx` |
| `/legal/terms` | `legal.terms.tsx` |

### Marketplace & rentals — `marketplace` · 21 routes

| URL | route file |
|---|---|
| `/marketplace` | `marketplace/index.tsx` |
| `/marketplace/c/:slug` | `marketplace/c.$slug.tsx` |
| `/marketplace/l/:id` | `marketplace/l.$id.tsx` |
| `/marketplace/mine` | `marketplace/mine.tsx` |
| `/marketplace/nearby` | `marketplace/nearby.tsx` |
| `/marketplace/rent` | `marketplace/rent/index.tsx` |
| `/marketplace/rent/active/:id` | `marketplace/rent/active.$id.tsx` |
| `/marketplace/rent/c/:slug` | `marketplace/rent/c.$slug.tsx` |
| `/marketplace/rent/e/:id` | `marketplace/rent/e.$id.tsx` |
| `/marketplace/rent/list` | `marketplace/rent/list.tsx` |
| `/marketplace/rent/listings` | `marketplace/rent/listings.tsx` |
| `/marketplace/rent/mine` | `marketplace/rent/mine.tsx` |
| `/marketplace/rent/nearby` | `marketplace/rent/nearby.tsx` |
| `/marketplace/rent/provider/:id` | `marketplace/rent/provider.$id.tsx` |
| `/marketplace/rent/request/:id` | `marketplace/rent/request.$id.tsx` |
| `/marketplace/rent/saved` | `marketplace/rent/saved.tsx` |
| `/marketplace/rent/search` | `marketplace/rent/search.index.tsx` |
| `/marketplace/saved` | `marketplace/saved.tsx` |
| `/marketplace/search` | `marketplace/search.index.tsx` |
| `/marketplace/sell` | `marketplace/sell.tsx` |
| `/marketplace/seller/:id` | `marketplace/seller.$id.tsx` |

### My profile — `me` · 1 routes

| URL | route file |
|---|---|
| `/me` | `me.tsx` |

### Messages — `messages` · 1 routes

| URL | route file |
|---|---|
| `/messages` | `messages.tsx` |

### Notifications — `notifications` · 2 routes

| URL | route file |
|---|---|
| `/notifications` | `notifications.index.tsx` |
| `/notifications/settings` | `notifications.settings.tsx` |

### Onboarding — `onboarding` · 7 routes

| URL | route file |
|---|---|
| `/onboarding` | `onboarding/index.tsx` |
| `/onboarding/complete` | `onboarding/complete.tsx` |
| `/onboarding/done` | `onboarding/done.tsx` |
| `/onboarding/preview` | `onboarding/preview.tsx` |
| `/onboarding/resume` | `onboarding/resume.tsx` |
| `/onboarding/skip` | `onboarding/skip.tsx` |
| `/onboarding/step/:id` | `onboarding/step.$id.tsx` |

### Opportunities — `opportunities` · 13 routes

| URL | route file |
|---|---|
| `/opportunities` | `opportunities.index.tsx` |
| `/opportunities/:id` | `opportunities.$id.index.tsx` |
| `/opportunities/:id/analytics` | `opportunities.$id.analytics.tsx` |
| `/opportunities/:id/applicants` | `opportunities.$id.applicants.index.tsx` |
| `/opportunities/:id/applicants/:aid` | `opportunities.$id.applicants.$aid.tsx` |
| `/opportunities/:id/apply` | `opportunities.$id.apply.tsx` |
| `/opportunities/:id/manage` | `opportunities.$id.manage.tsx` |
| `/opportunities/applications` | `opportunities.applications.index.tsx` |
| `/opportunities/applications/:aid` | `opportunities.applications.$aid.tsx` |
| `/opportunities/mine` | `opportunities.mine.tsx` |
| `/opportunities/new` | `opportunities.new.tsx` |
| `/opportunities/saved` | `opportunities.saved.tsx` |
| `/opportunities/search` | `opportunities.search.tsx` |

### Plans & billing — `plans` · 4 routes

| URL | route file |
|---|---|
| `/plans` | `plans.index.tsx` |
| `/plans/checkout` | `plans.checkout.tsx` |
| `/plans/earnings` | `plans.earnings.tsx` |
| `/plans/manage` | `plans.manage.tsx` |

### Portfolio — `portfolio` · 9 routes

| URL | route file |
|---|---|
| `/portfolio` | `portfolio.index.tsx` |
| `/portfolio/:id` | `portfolio.$id.index.tsx` |
| `/portfolio/:id/edit` | `portfolio.$id.edit.tsx` |
| `/portfolio/analytics` | `portfolio.analytics.tsx` |
| `/portfolio/new` | `portfolio.new.index.tsx` |
| `/portfolio/new/:kind` | `portfolio.new.$kind.tsx` |
| `/portfolio/reorder` | `portfolio.reorder.tsx` |
| `/portfolio/select` | `portfolio.select.tsx` |
| `/portfolio/settings` | `portfolio.settings.tsx` |

### Public profile — `profile` · 3 routes

| URL | route file |
|---|---|
| `/profile/:username` | `profile.$username.tsx` |
| `/profile/edit` | `profile.edit.tsx` |
| `/profile/insights` | `profile.insights.tsx` |

### Provider console — `provider` · 8 routes

| URL | route file |
|---|---|
| `/provider/availability` | `provider.availability.tsx` |
| `/provider/bookings` | `provider.bookings.index.tsx` |
| `/provider/bookings/:ref` | `provider.bookings.$ref.tsx` |
| `/provider/bookings/calendar` | `provider.bookings.calendar.tsx` |
| `/provider/bookings/requests` | `provider.bookings.requests.index.tsx` |
| `/provider/bookings/requests/:ref` | `provider.bookings.requests.$ref.tsx` |
| `/provider/policies` | `provider.policies.tsx` |
| `/provider/pricing` | `provider.pricing.tsx` |

### Recently viewed — `recently-viewed` · 1 routes

| URL | route file |
|---|---|
| `/recently-viewed` | `recently-viewed.tsx` |

### Recommendations — `recommendations` · 2 routes

| URL | route file |
|---|---|
| `/recommendations` | `recommendations.index.tsx` |
| `/recommendations/settings` | `recommendations.settings.tsx` |

### Recommended — `recommended` · 1 routes

| URL | route file |
|---|---|
| `/recommended` | `recommended.tsx` |

### Auth — `reset-password` · 1 routes

| URL | route file |
|---|---|
| `/reset-password` | `reset-password.tsx` |

### Reviews — `reviews` · 6 routes

| URL | route file |
|---|---|
| `/reviews` | `reviews.index.tsx` |
| `/reviews/:id/edit` | `reviews.$id.edit.tsx` |
| `/reviews/:id/reply` | `reviews.$id.reply.tsx` |
| `/reviews/:id/report` | `reviews.$id.report.tsx` |
| `/reviews/request` | `reviews.request.tsx` |
| `/reviews/write/:id` | `reviews.write.$id.tsx` |

### Saved — `saved` · 1 routes

| URL | route file |
|---|---|
| `/saved` | `saved.tsx` |

### Search — `search` · 1 routes

| URL | route file |
|---|---|
| `/search` | `search.index.tsx` |

### Services — `services` · 7 routes

| URL | route file |
|---|---|
| `/services` | `services.index.tsx` |
| `/services/:id` | `services.$id.index.tsx` |
| `/services/:id/enquiry` | `services.$id.enquiry.tsx` |
| `/services/:id/manage` | `services.$id.manage.tsx` |
| `/services/availability` | `services.availability.tsx` |
| `/services/conflicts` | `services.conflicts.tsx` |
| `/services/new` | `services.new.tsx` |

### Settings — `settings` · 5 routes

| URL | route file |
|---|---|
| `/settings` | `settings.index.tsx` |
| `/settings/account` | `settings.account.tsx` |
| `/settings/delete-account` | `settings.delete-account.tsx` |
| `/settings/privacy` | `settings.privacy.tsx` |
| `/settings/security` | `settings.security.tsx` |

### Singers directory — `singers` · 1 routes

| URL | route file |
|---|---|
| `/singers` | `singers.tsx` |

### Studio — `studio` · 1 routes

| URL | route file |
|---|---|
| `/studio` | `studio.tsx` |

### Talent (deprecated) — `talent` · 1 routes

| URL | route file |
|---|---|
| `/talent/:id` | `talent.$id.tsx` |

### Verification — `verification` · 5 routes

| URL | route file |
|---|---|
| `/verification` | `verification.index.tsx` |
| `/verification/:type` | `verification.$type.tsx` |
| `/verification/badge` | `verification.badge.tsx` |
| `/verification/choose` | `verification.choose.tsx` |
| `/verification/privacy` | `verification.privacy.tsx` |

### First run — `welcome` · 1 routes

| URL | route file |
|---|---|
| `/welcome` | `welcome.tsx` |