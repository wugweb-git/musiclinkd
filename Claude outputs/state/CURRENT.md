---
doc: CURRENT
version: 1.0
status: CURRENT
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
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
| Design guide repo | `wugweb-git/musiclinkd` — `tokens.json` → `ml-tokens.css` → `/guide` |
| Host | **Lovable.** Published at `musiclinkd.lovable.app`. **Nothing ships to Firebase.** |
| Firebase | `musiclinkd-7ae30` — admin panel and Firestore backend only |
| Supabase (frontend) | live tables: `profiles`, `portfolio_items` |
| Supabase (taxonomy target) | `grufbwlmxhzpocryrhho` — a **different** project from the frontend's |

The Lovable app is the only MusicLinkd frontend. The Kotlin/Compose Android app it replaces is
retired; its Firebase backend is not.

## Frontend — verified 13 Sep 2026

| | |
|---|---|
| `origin/main` | `0697a42` |
| Lovable `latest_commit_sha` | `0697a422` — same commit, published, `error: null` |
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

| | |
|---|---|
| Type scale | **7 steps.** `--type-1` … `--type-7` |
| Token count | **111 declarations** in the generated `ml-tokens.css` |
| Token ceiling | **Lifted.** The old 108 cap is gone |
| Steps-per-scale rule | **DEAD.** Any scale carries as many steps as the design needs |
| Fonts | **Inter alone** — body, display, eyebrow. Inter Tight, Unica One and JetBrains Mono all retired |
| Letter-spacing | None. Headings use Inter's normal tracking; the wordmark is set at 0 |
| Brand pink | `#EA4C89` fills · `#C7295F` text. `#5B4BFF` is superseded and always a bug |
| Navy | `#0D0B26` **retired** — survives only as a documented back-compat shim for 17 route files |
| Radius | 8 / 16 / 32 / pill |

**Correction on record:** an earlier claim that `scripts/build-handoff.mjs:95` would reject
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

- **`profiles.visibility` RLS is NOT applied.** The migration is merged; the live SELECT policy is
  still `USING (true)`. All 3 profiles are `visibility='public'`, so nothing is exposed *yet*. Do
  not write code that assumes the database hides a non-public profile.
- **Firestore `/users` is world-readable at document level.** `allow read: if true` publishes the
  whole document, and `email` is present on all 83 user docs. Firestore has no field-level read
  control, so the first real OTP signup publishes that person's phone number. Fixed by a split
  public-profile document, or by the move to Postgres.
- **The Lovable project `description` field still specifies the superseded system** — `#5B4BFF`
  primary, `#8B5CF6` secondary, `#FAFAFA` ground, Google Sans. It is read alongside project
  knowledge and contradicts it.

## Open decisions — owner, not engineering

1. **Lovable's agent: branch and PR, or direct-to-`main`?** Today it pushes straight to `main` as
   `gpt-engineer-app[bot]` with the message `Changes`. Unreviewed, and its commits carry
   `Co-authored-by: wugweb-git <…@users.noreply.github.com>`, which breaks the attribution rule
   permanently — those commits are published and cannot be rewritten.
2. **Capacitor path** — thin wrapper around the hosted site, or a static bundle in the APK.
   Determines whether `viewport-fit=cover`, `@capacitor/preferences` and a manifest splash are
   needed at all.
3. **Phase 8 prerequisites.**
4. **Route naming** — `/recommendations` vs `/recommended`, `/bands` vs `/band`. Both pairs exist.
5. **Widget adoption** — import `ml-widgets.css`, port 116 widgets to React, or share tokens only.
   See `specs/WIDGET-ADOPTION-SPEC.md`.

## Tooling

No GitHub connector exists in the Claude connector directory. GitHub's official remote MCP server
is the route: `https://api.githubcopilot.com/mcp/` (or `/mcp/readonly`), added via
claude.ai → Settings → Connectors → Add custom connector.

Until then repo state is read from a local clone plus the read-only Lovable connector. The clone's
sandbox has `git` but no credentials, so `git ls-remote` fails and the clone can go stale with no
signal. Two wrong claims on 13 Sep came from exactly that.
