---
doc: lovable-prompt-wire-orphans
version: 1.0
status: AWAITING ACTION
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@47f7c7b
folder: specs
originally: lovable-prompt-wire-orphans.md
source: Claude Code session scratchpad
---

> **AWAITING ACTION** · imported 13 Sep 2026. Paste-ready Lovable prompt to link the ~20 finished screens nothing points at. Adds links only. Waiting on Lovable credits.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

Wire existing screens into navigation. **Add links only. Do not create new screens, do not redesign
anything, and do not change any screen's content or layout beyond the one entry point.**

These routes are finished and render correctly, but nothing in the app links to them (audit:
`docs/pending-screens-audit-2026-09-13.md`). For each one, add an entry point on the screen named,
reusing the controls and patterns that screen already uses (`Row`, `Btn`, sheet actions):

1. `/business/$id/enquiry`: an "Enquire" action on the business profile (`business.$id.index.tsx`),
   next to Message/Book.
2. `/services/$id/enquiry`: an "Ask a question / Request a quote" action on the service detail
   (`services.$id.index.tsx`).
3. `/services/$id/manage`: an owner-only "Manage" action on the same service detail.
4. `/services/conflicts`: a link from `/services/availability` when conflicts exist.
5. `/bands/$id/portfolio`, `/bands/$id/events`, `/bands/$id/contact`: from the band profile
   (`bands.$id.index.tsx`). Its Music/Shows tabs or a "Contact" action are the natural places.
6. `/reviews/$id/reply`, `/reviews/$id/edit`, `/reviews/$id/report`: from each review card on
   `/reviews`, as an overflow action (reply for the owner, edit for your own, report for others).
7. `/reviews/request`: an "Ask for a review" action on `/reviews`.
8. `/discover/music`, `/discover/projects`, `/discover/community`, `/discover/nearby`: section
   "See all" links or category entries on Discover home (`index.tsx`).
9. `/search/why/$id`: a "Why am I seeing this?" link on recommendation cards (`/recommendations`).
10. `/opportunities/applications/$aid`: make each row on `/opportunities/applications` open its
    detail.
11. Repoint the last `/talent/$id` links to `/profile/$username`: `discover.nearby.tsx:102`,
    `recently-viewed.tsx:48`, `src/data/identity.ts:90,125`.

Rules:
- Typed `<Link to=…>` only, no `as any`.
- Owner-only actions must check ownership the way the screen already does.
- No emoji or text glyphs; use Lucide icons.
- Keep 48dp touch targets.
- **Do not add a route file `x.y.tsx` beside an existing `x.tsx` that has no `<Outlet />`.**

When done, run `bunx tsc --noEmit` and `bunx eslint .`. List every link you added as
file:line → destination, and name any entry point you could not place.
