---
doc: CURRENT
version: 1.0
status: CURRENT
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@47f7c7b
folder: state
originally: new
---

> **This is the ground truth.** Every other document in this folder is a snapshot pinned to a date
> or a commit. Where any of them disagrees with this file, **this file wins**. Update it first,
> then let the others go stale honestly rather than quietly.
>
> Nothing here is inferred. Every line was read from the repo, the Lovable API, or the live
> database on the date given.

# MusicLinkd — current state

## Identity

| | |
|---|---|
| Frontend repo | `wugweb-git/musiclinkd-9e6ca875` — TanStack Start · React 19 · Tailwind v4 · Supabase · bun |
| Design guide repo | `wugweb-git/musiclinkd` — reference only. **Not edited from this project**; pull from it only when Vedanshu points at something specific. |
| Host | **Lovable.** Published at `musiclinkd.lovable.app`. **Nothing ships to Firebase.** |
| Firebase | `musiclinkd-7ae30` — admin panel and Firestore backend only |
| Supabase (frontend) | live tables: `profiles`, `portfolio_items` |
| Supabase (taxonomy target) | `grufbwlmxhzpocryrhho` — a **different** project from the frontend's |

The Lovable app is the only MusicLinkd frontend. The Kotlin/Compose Android app it replaces is
retired; its Firebase backend is not.

## Frontend — verified 13 Sep 2026

| | |
|---|---|
| `origin/main` | `47f7c7b` (published build = `47f7c7b`, verified on the live site by Claude Code 13 Sep) |
| Lovable `latest_commit_sha` | tracks `main`, published, CI green |
| Routes | **207**, zero children under an Outlet-less parent |
| CI | GitHub Actions, bun 1.4.0. Blocking: typecheck, lint (correctness rules), build. Non-blocking: format check (`continue-on-error: true`) |

### Why format check is non-blocking

Lovable's agent commits straight to `main` without running Prettier. Gating on formatting would
fail every Lovable push, and a permanently red check stops being read. This is a deliberate
decision recorded in `.github/workflows/ci.yml` itself.

### The `.index.tsx` rule

A path with children must be a leaf named `.index.tsx`, never a component at the parent path. A
parent route that renders a component and has no `<Outlet />` swallows its children: the child URL
resolves and the parent's screen paints over it.

This shipped and broke **26 URLs** across `/bands`, `/reviews`, `/plans`, `/help` and `/search`.
Fixed 13 Sep by renaming 12 parents: `bands`, `bands.$id`, `collab.new`, `collab.proposals.$id`,
`help`, `identity.contact`, `plans`, `portfolio.$id`, `recommendations`, `reviews`, `search`,
`services.$id`.

## Design system — verified 13 Sep 2026

Everything below is the **app**, read from `src/styles.css` on 13 Sep. An earlier version of this
section mixed in guide values and got the type-step count and the eyebrow font wrong as a result.
No guide values appear here now.

| | |
|---|---|
| Type scale | **5 steps** — `--type-1..5`, `--type-5` is hero-only |
| Font tokens | `--font-sans` and `--font-display`, both Inter. **There is no `--font-eyebrow`** — a claim to the contrary in `lovable-project-knowledge.md` is wrong and should be corrected at the next knowledge update |
| Weights loaded | 400 / 500 / 600 / 700. 800 is never requested |
| Steps-per-scale rule | **DEAD.** Any scale carries as many steps as the design needs |
| Letter-spacing | None. Headings use Inter's normal tracking; the wordmark is set at 0 |
| Brand pink | `#EA4C89` fills · `#C7295F` text. `#5B4BFF` is superseded and always a bug |
| Navy | **Removed 13 Sep** — migrated to ink `#0D0D0D` (`81c8953`). Zero `navy` left in `src/`; `Btn variant="ink"`, `bg-ink` |
| Radius | **Material 3 shape scale, adopted 13 Sep:** xs 4 · sm 8 · md 12 · lg 16 · xl 20 · 2xl 28 · 3xl 32 · full. `--radius` (shadcn base) is 16. |

## Scope

**This project is the Lovable app, `musiclinkd-9e6ca875`.** The design-guide repo is not edited and
its divergence from the app is not a problem tracked here. The guide is a reference to pull from only
when Vedanshu points at a specific thing to take from it.

Removed from this document on that instruction: the guide-vs-app comparison (radius, display font,
weight 800, type steps, photography) and the "does the guide lead or follow" decision. They are in
git history at `0d2d6da` if ever wanted.

One item from that set stays, because it is inside the app and nothing to do with the guide:

### `AGENTS.md` and the code disagree about chips

`AGENTS.md:152` assigns `sm` 8px to *"chips/small controls"*. The shared `Chip` component
(`src/components/ml/ui.tsx:139`) is `rounded-full` — a pill — and the Material 3 commit says buttons
and chips stay pills. As written, Lovable's agent will build 8px chips on new screens. One line in
`AGENTS.md` and one in the Lovable knowledge.

### Radius — the detail

The app moved to Material 3 on 13 Sep (`df56e0a`, 124 files, 465 insertions) on the stated reason
*"follow the industry standards, esp. for mobile UX"* — Android is the target. Cards went 32px → 16px
across 372 call sites; small tiles are 12px; sheets and dialogs are 28px; buttons and chips stay pills.

~~`AGENTS.md` line 152 contradicts its own repo.~~ **Fixed in `47f7c7b`** — it now documents the
Material 3 roles (sm 8 chips · md 12 tiles/inputs · lg 16 cards · xl 20 · 2xl 28 sheets/dialogs · 3xl
32 hero · full).

**Correction on record (1):** `audits/TAILWIND-DS-AUDIT.md` §A1 called making the radius aliases
real steps *"the single cheapest win available — it restores expressiveness to ~450 existing call
sites without touching one of them."* That was wrong on cost. It took 124 files and 465 insertions,
and it changed what nearly every screen looks like. Zero *token* edits is not zero edits.

**Correction on record (2):** an earlier claim that `scripts/build-handoff.mjs:95` would reject
`--type-5` was **wrong**. That check validates a type step's *weight value* against the weight
scale and builds its map dynamically from `steps(f.weight)`. It never capped type steps. The word
"four" in its comment is stale prose, not a validator.

## Phase status

| Phase | State |
|---|---|
| 1 — rules and docs | DONE (`AGENTS.md`, `CHANGELOG.md`) |
| 2 — design tokens | DONE **for `src/components/ml/` only**. `src/routes/` was never swept |
| 2b — motion | not started |
| 3 — `exactOptionalPropertyTypes` convention | not started |
| 4 — emoji → Lucide | not started. Broader than it sounds: `→ ★ ✓` are absent from Inter's served Google subsets and already fall back to a system font |
| 5 — nav prefix, dead controls, touch targets | not started |
| 6 — de-duplication, Radix swap | not started |
| 7 — inventory | DONE, merged |
| 8 — the 204-route sweep | not started · **~97 hours / 16 working days / 8 PRs** |
| 9 — first-run / splash / onboarding entry | not started, and gated on 8 |

## Screens

| Verdict | Count |
|---|---|
| Done | 24 |
| Partial | 9 |
| Not done | 4 |

**Roughly 20 finished screens have no inbound link anywhere in the app.** This is the largest gap
between work paid for and value delivered in the project — bigger than Phase 8, and it is
navigation wiring rather than a sweep. It is the cheapest next step.

## Known-unsafe, not yet decided

- ~~`profiles.visibility` RLS is NOT applied.~~ **Applied 13 Sep** — see "Shipped 13 Sep" below.
- **Firestore `/users` is world-readable at document level.** `allow read: if true` publishes the
  whole document, and `email` is present on all 83 user docs. Firestore has no field-level read
  control, so the first real OTP signup publishes that person's phone number. Fixed by a split
  public-profile document, or by the move to Postgres.
- **The Lovable project `description` field still specifies the superseded system** — `#5B4BFF`
  primary, `#8B5CF6` secondary, `#FAFAFA` ground, Google Sans. It is read alongside project
  knowledge and contradicts it.

## Shipped 13 Sep — render-path batch

| Commit | What |
|---|---|
| `9427395` | **Profile sections render from the profile's own record, or an empty state.** Ananya, the 8 Discover talent profiles and real users without a seed profile all start from one empty base instead of spreading `...rahul`. Real users build from their own database row, `/me` included. |
| `9427395` | Header and share sheet: missing photo, cover, role, location or availability show a neutral placeholder or are hidden. **The Verified tick now appears only for verified tiers** — it rendered on every profile before. |
| `9427395` | Discover talent cards no longer show fabricated counts (640 connections, 410k plays, 12 reviews); unknown counts show "—". |
| `9427395` | Overview and About tabs gained empty states for summary, bio, details, music identity and links. Page title no longer ends in a dangling dash when a profile has no role. |
| `9427395` | The banner counts the same list the opportunity tiles count and links to `/opportunities`. Wording unchanged; still hidden at zero. |
| `2fc32d2` | Decision recorded: the 11 seeded profiles stay **unmarked, deliberately, pre-launch**. |

Verified live on `musiclinkd.lovable.app`: Meher Kaur's page shows "No audio yet" with none of
Rahul's content; the Home banner reads "5 new opportunities". None of 20 profiles shares a section
with Rahul, and his own page is unchanged.

**No content was authored.** `src/data/talent-profiles.ts` was deleted before it shipped. The three
items were closed as mechanics — a data-wiring fix and a render-path fix — which is why no bios,
tracks or reviews had to be invented.

The remaining `...rahul` spreads in `src/data/collab.ts:655` and `:871` are Rahul appearing as a
collaborator on a project record. That is a person in a list, not a profile section inheriting a
template. Correct as-is.

## Shipped 13 Sep — later batch

| Commit | What |
|---|---|
| `8783ea2` | `/feed`: the fake 450ms timer removed so the server renders real content; the three fabricated figures now computed from seed data; Bell links to `/notifications`. `ui/skeleton` default tint neutral. Four QR items ticked in `roadmap.md`. |
| `78dca36` | **The 11 linked profiles are real rows in the live database.** Opportunity posters, applicants and collab participants now have their own `profiles` rows with content in `src/data/linked-profiles.ts`; their `auth.users` rows carry no email or password, so nobody can sign in as them. Migration `20260913090949_seed_linked_profiles.sql`. |
| `78dca36` | `/profile/$username` returns a real **404** for a username with neither a database row nor a seed profile — it used to render Rahul Iyer's page. `/profile/me` redirects to `/me`. |
| `e2e3b18` | Seed migration recorded in `schema_migrations`. |

Published: `musiclinkd.lovable.app` is on this build. Verified live — unknown username 404s;
`/profile/sunsetrecords` renders its own content.

### Still open, recorded in `roadmap.md`

- Ananya's profile and every Discover talent profile still copy Rahul's tracks, projects and reviews.
- A real user with no seed profile still falls back to Rahul's sections.
- The banner's *"9 new opportunities"* comes from a different seed stat than the 5 real ones.

## Shipped 13 Sep, earlier batch

| Commit | What |
|---|---|
| `df057a0` | `viewport-fit=cover`; `--text-secondary` now points at `--muted-foreground`; five-step comment fixed |
| `2ea034a` | Heading letter-spacing removed — `-0.02em` off `h1..h4`, `tracking-tight` off auth / welcome / error headings |
| `b17b510` | `roadmap.md` synced; pending-screens audit committed as `docs/pending-screens-audit-2026-09-13.md` |
| `d526f17` | One spacing system; `text-text2` renamed; `/talent/$id` links removed |
| `abe3c66` | `/band` redirects to `/bands`; swipe deck moved under `/recommendations/swipe` |
| `9993a6c` | Retired navy migrated to ink `#0D0D0D` (ΔE2000 14.1 — a visible change, not a rename) |
| `df56e0a` | Material 3 shape scale |
| `47f7c7b` | Docs: `AGENTS.md` radius roles / ink / numeric spacing / `text-muted-foreground`; "never Unsplash" line removed on the owner's chat statement; CHANGELOG + roadmap. Lovable knowledge updated to match |

Branch SHAs above; the merge commits on `main` are `d526f17`+`e2471e7` (one merge), `81c8953`, `90a4676`, `47f7c7b`.

**`specs/PR-SPEC-dead-spacing-and-band-route.md` is already implemented** (before the spec was written), with deviations recorded in the spec.

**`profiles.visibility` RLS is now applied to the live database.** Private profiles are hidden from
everyone but their owner; the old `USING (true)` policy is gone; the migration is in history. All 3
profiles are public, so nothing visible changed. **This supersedes the warning elsewhere in the
older documents that the policy is unenforced.**

## The photography rule

Removed from `AGENTS.md` and the Lovable project knowledge on Vedanshu's statement that no such rule
exists. **Settled for this project; not reopened.**

Earlier versions of this document argued the point at length and got the reason for the deletion
wrong. Both are in git history. 14 files in `src/` still carry Unsplash URLs; whether they are
replaced is a normal content task, not a rule violation.

## Open decisions — owner, not engineering

1. **Lovable's agent: branch and PR, or direct-to-`main`?** Today it pushes straight to `main` as
   `gpt-engineer-app[bot]` with the message `Changes`. Unreviewed, and its commits carry
   `Co-authored-by: wugweb-git <…@users.noreply.github.com>`, which breaks the attribution rule
   permanently — those commits are published and cannot be rewritten.
2. **Capacitor path** — thin wrapper around the hosted site, or a static bundle in the APK.
   Determines whether `viewport-fit=cover`, `@capacitor/preferences` and a manifest splash are
   needed at all.
3. **Phase 8 prerequisites — settled except photography.** Radius → Material 3 (`90a4676`), navy →
   ink (`81c8953`). Photography is the open item above (the `cards.md` rule vs the owner's chat
   statement). The sweep itself (~97h) is unstarted.
4. ~~Route naming~~ — **resolved 13 Sep** (`e2471e7`): `/band` redirects to `/bands`; swipe deck is
   `/recommendations/swipe`, `/recommended` redirects.
5. **Widget adoption** — import `ml-widgets.css`, port 116 widgets to React, or share tokens only.
   See `specs/WIDGET-ADOPTION-SPEC.md`.

## Tooling

No GitHub connector exists in the Claude connector directory. GitHub's official remote MCP server
is the route: `https://api.githubcopilot.com/mcp/` (or `/mcp/readonly`), added via
claude.ai → Settings → Connectors → Add custom connector.

Until then repo state is read from a local clone plus the read-only Lovable connector. The clone's
sandbox has `git` but no credentials, so `git ls-remote` fails and the clone can go stale with no
signal. Two wrong claims on 13 Sep came from exactly that.
