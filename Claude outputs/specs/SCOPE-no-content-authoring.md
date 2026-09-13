---
doc: SCOPE-no-content-authoring
version: 1.0
status: CURRENT
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@e2e3b18
folder: specs
originally: new
---

> **CURRENT** · 13 Sep 2026. A scope boundary and a re-spec of the three open items so they can be
> finished without authoring any content. Supersedes the in-flight plan to create
> `src/data/talent-profiles.ts`.
> Ground truth lives in `state/CURRENT.md`.

# Scope: no content authoring

**Vedanshu's instruction, 13 Sep:** do not author content. Focus is UX and UI mechanics, routes,
mapping, components and blocks.

Content means bios, headlines, track names, project titles, credits, reviews, quotes, ratings,
locations attributed to a person, and anything else that reads as a fact about a human or a business.
Seed data is not an exemption — it renders on a public site and is indistinguishable from real
content to anyone looking at it.

**`src/data/talent-profiles.ts` should not be created.** The plan to write full profiles for 8
Discover talent members is content authoring, whatever module it lands in.

---

## Already shipped, and it needs a decision

`src/data/linked-profiles.ts` is **64.7 KB with 111 authored prose fields** — headlines, bios,
project descriptions, review text — for 11 people and businesses. Samples:

> *"Independent label for indie-pop and alternative releases out of Mumbai."*
> *"Singer-guitarist making loud, patient alt-rock out of Bengaluru."*
> *"Producer and pianist recording jazz and fusion bands live in Pune."*

Those rows are **in the live database and published**. They are invented people with invented
careers, presented on a public site as profiles. That is a bigger version of the `talents[1].photo`
bug from 10 Sep — a seed record's identity shown as if real — and of the fabricated `5 / 2 /
1 shortlisted` figures fixed this morning.

Three honest options. **This is Vedanshu's call, not an engineering one:**

1. **Keep them, clearly marked.** Add a visible demo/sample marker to seeded profiles so no visitor
   reads them as real people.
2. **Keep them unmarked** as a deliberate pre-launch decision, and record that decision in
   `roadmap.md` so nobody re-raises it.
3. **Pull them** from the live database and go back to a 404 for those usernames, accepting the dead
   links until real content exists.

Doing nothing is option 2 by default, which is the one that should be chosen deliberately rather
than arrived at.

---

## The three open items, re-specified as mechanics

All three can be closed without writing a word of content.

### 1. The banner says "9 new opportunities"; there are 5

A hard-coded stat, the same defect class as the `/feed` figures already fixed. This is data wiring,
not content.

- Count the source list. Do not type a number.
- Link the banner to the list it counts.
- If the count is zero, the banner does not render. Decide that in the component, not in the data.

**No authored text.** The label is already written; only the number and the destination change.

### 2. A real user with no seed profile falls back to Rahul's sections

This is a render-path bug, not missing content. The profile page currently merges a real Supabase row
with seed sections, so a real user inherits somebody else's tracks, projects and reviews.

The fix is per-section empty states, which the app is supposed to ship anyway:

- Each section renders from the viewed profile's own data or not at all. Never from a template row.
- A section with no data shows an empty state, or is omitted.
- Nothing on a profile may come from a different profile. That is the rule; the Rahul fallback
  violates it and so does any replacement default.

`AGENTS.md` already requires every screen to ship loading, empty, error and offline states. This is
that requirement applied to one screen, and it removes the need for seeded content entirely: a
profile with no tracks shows "no tracks yet", which is honest and costs nothing.

### 3. Ananya and the Discover talent profiles copy Rahul's content

Same root cause as (2), same fix. Once sections render only from their own record, the copying stops
by construction. **Do not solve this by authoring 8 more profiles** — that hides the bug behind
content and leaves the fallback in place for the next record that lacks data.

---

## What is in scope, and worth doing

- Empty, loading, error and offline states for the profile sections — currently none exist, and the
  kit has no `Skeleton` or `OfflineBanner` component at all.
- The verified tick reflecting actual verification status rather than always rendering.
- Missing photo and cover handling — a layout question, not a content one.
- Anything on the `~20 unreachable screens` list: routes, links, navigation mapping.
- `plans/PHASE-SPECS-2b-to-9.md` phases 2b, 3, 4, 5, 6 — motion, prop conventions, icons, nav
  prefix matching, de-duplication. All mechanics.

## What is out of scope until Vedanshu says otherwise

- Writing or editing any bio, headline, track, project, credit, review, rating or quote.
- Creating seed records that describe people or businesses.
- Choosing photographs for a person.
- Naming things that appear in the product as content rather than as structure.
