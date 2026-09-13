---
doc: README
version: 1.0
status: CURRENT
updated: 2026-09-13
folder: root
---

# Claude outputs — document register

Working documents produced by Claude (Cowork) for MusicLinkd. Reorganised and versioned
**13 Sep 2026**; before that it was 19 files in one flat folder with no way to tell a live spec
from a superseded one.

**Read `state/CURRENT.md` first.** It holds the ground truth — commit SHA, route count, token
counts, phase status, open decisions. Every other file here is a snapshot pinned to a date or a
commit, and where any of them disagrees with `CURRENT.md`, **CURRENT wins**.

Every file carries a front-matter block: `doc`, `version`, `status`, `updated`,
`verified_against`, `folder`, `originally`.

## Status vocabulary

| Status | Meaning |
|---|---|
| **CURRENT** | Accurate as of `updated`. Safe to act on. |
| **PARTLY STALE** | Substance holds; specific claims have been overtaken. The banner names them. |
| **AWAITING DECISION** | Complete, but blocked on a call only Vedanshu can make. |
| **SUPERSEDED** | Replaced. Kept because later documents' corrections refer back to it. Do not act on. |

## The register

### `state/` — what is true now

| File | v | Status | What it is |
|---|---|---|---|
| `CURRENT.md` | 1.0 | CURRENT | **Ground truth.** Read first, update first. |
| `BUILD-STATE-2026-09-13.md` | 1.0 | CURRENT | Dated snapshot of pushed / merged / live, with the session's corrections. Point-in-time — it does not age into `CURRENT.md`. |
| `HANDOFF-2026-09-13.md` | 1.0 | CURRENT | Claude Code's consolidated handoff. **Rescued from a `/private/tmp` scratch directory** that is not version-controlled and would have been lost. |

### `plans/` — executable work

| File | v | Status | What it is |
|---|---|---|---|
| `CLAUDE-CODE-WORK-ORDER.md` | 1.1 | PARTLY STALE | Eight phased branch-per-phase instructions. Appendix A (a second copy of `AGENTS.md`) deleted. Phases 1, 2 and 7 are DONE. |
| `PHASE-SPECS-2b-to-9.md` | 1.1 | PARTLY STALE | Specs for 2b, 3, 4, 5, 6, 7, 9. Renamed from `-2b-to-7` because it always covered 9. **Phase 8 is referenced five times and specified nowhere** — the real gap. |

### `specs/` — designed, not yet built

| File | v | Status | What it is |
|---|---|---|---|
| `TAXONOMY-MIGRATION-SPEC.md` | 1.0 | CURRENT | 95 slugs across 7 broken categories, DDL, integrity options. The only document untouched by the route / token / typography changes. Never executed. |
| `lovable-prompt-wire-orphans.md` | 1.0 | AWAITING ACTION | Paste-ready Lovable prompt to link the ~20 finished screens nothing points at. Links only. Waiting on Lovable credits. |
| `WIDGET-ADOPTION-SPEC.md` | 1.0 | AWAITING DECISION | 116 widget families in `ml-widgets.css`, currently hand-rolled twice. Three honest options; the choice decides everything downstream. |
| `PR-SPEC-dead-spacing-and-band-route.md` | 1.0 | CURRENT | One small PR for Claude Code. Deletes 4 spacing tokens with zero call sites, and fixes a "Create band" CTA in onboarding that points at a read-only profile of a band absent from the data layer. |

### `audits/` — measurement

| File | v | Status | What it is |
|---|---|---|---|
| `MUSICLINKD-LOVABLE-AUDIT.md` | 2.1 | PARTLY STALE | The forensic account of how the app was built and where instructions were dropped. Diagnosis holds; counts and the type-scale claims do not. |
| `TAILWIND-DS-AUDIT.md` | 1.1 | PARTLY STALE | Both design systems. Section B is overtaken — the ceiling was raised and the scale is 7 steps. Section A still holds. |
| `FIRESTORE-SCHEMA-AUDIT.md` | 1.0 | CURRENT | 1063-line schema against live Firestore. Unaffected by frontend work. |
| `pending-screens-audit-2026-09-13.md` | 1.0 | CURRENT | The 7 Sep list checked item by item against the code: 24 done, 9 partial, 4 not done, plus which screens nothing links to. Also committed in the app repo at `docs/`. |
| `pending-screens-2026-09-07.md` | 1.0 | SUPERSEDED | The original list, written by Lovable's agent, from `.lovable/plan/`. Superseded by the audit above. |
| `REFACTOR-REVIEW-URLMAP.md` | 1.1 | PARTLY STALE | Sections 1–4 (the `/feed` review) are live and **exist in no phase spec** — losing this file loses three bugs. Section 5's URL table should be script output, not prose. |

### `reference/` — standing rules

| File | v | Status | What it is |
|---|---|---|---|
| `AGENTS.md` | 2.0 | CURRENT | **Proposed replacement** for the live `AGENTS.md` at the root of `musiclinkd-9e6ca875`. The copy here is not what agents read — apply it to that repo. |
| `lovable-knowledge-2026-09-13.md` | 1.0 | CURRENT | **The text now live in Lovable.** Its agent reads this on every prompt. The photography rule was removed from it — see `state/CURRENT.md` before treating that as settled. |
| `lovable-project-knowledge.md` | 2.0 | SUPERSEDED | Earlier mirror, pushed via `set_project_knowledge`. Replaced by the file above. |
| `lovable-workspace-knowledge.md` | 1.0 | CURRENT | Wugweb-wide standing rules. Deliberately abstract, so it has not rotted. **Unverified: whether it was ever installed as workspace knowledge.** |

### `archive/` — superseded, kept for the trail

| File | Superseded by |
|---|---|
| `MUSICLINKD-AUDIT.md` | `audits/MUSICLINKD-LOVABLE-AUDIT.md`, whose §21 retracts this document's central frame. Do not delete — those corrections are unreadable without it. |
| `PHASE-2-TOKEN-SPEC.md` | Phase 2 shipped. Its own banner disowns its type scale. §§4–5 should be lifted into the Phase 8 spec before this is forgotten. |
| `DESIGN-WORK-STATE.md` | `state/CURRENT.md` |
| `NOT-LANDED-REGISTER.md` | `state/BUILD-STATE-2026-09-13.md`, same day |
| `audit-part2-governance-and-code.md` | Code-review half fully promoted into `plans/PHASE-SPECS-2b-to-9.md` |
| `audit-part3-chat-history.md` | The only forensic record of 110 Lovable messages and 46 commits. **Never delete.** |
| `audit-part3-addendum.md` | Corrections to part 3. Unreadable standalone. |
| `lovable-knowledge-PREVIOUS.md` | The Lovable knowledge as it stood before the 13 Sep replacement. Kept so the change is reversible. |

## What changed on 13 Sep 2026

- **Created `state/CURRENT.md`.** The set previously had no current-state document: every file was
  pinned to `315564c5`, `384af26f` or `f1419f2`, and the newest facts appeared in none of them.
- **Fixed the route count** in four files — 204/205 → 207 — and renamed the 12 parent routes to
  `.index.tsx` wherever they were listed.
- **Killed the "four steps per scale" rule** where it was still asserted as law, most importantly
  in `AGENTS.md`, which is the only one agents actually obey.
- **Deleted the second copy of the rules.** Work Order Appendix A was a condensed duplicate of
  `AGENTS.md` and had already drifted. Two copies of the governing rules is how `#5B4BFF` drifted
  in the first place.
- **Upgraded `AGENTS.md`** with the `.index.tsx` routing rule, Inter-only typography, navy as a
  shim, and an explicit warning that `profiles.visibility` is not enforced.
- **Sorted 19 files into six folders** with a status on each.

## Known gaps, not fixed here

- **Phase 8 has no spec.** It is the largest remaining piece of work (~97 hours) and it is
  referenced as a dependency five times with nothing behind it.
- **`REFACTOR-REVIEW-URLMAP.md` §5 is hand-maintained.** A 207-row table transcribed by hand will
  be wrong again within the week. It should be generated from the route tree.
- **`audits/MUSICLINKD-LOVABLE-AUDIT.md` has internal contradictions** independent of staleness:
  §6's flow table has 23 rows against prose citing 20 and 24, and §2.1's causal claim rests on a
  quoted timestamp three minutes earlier than the message it supposedly answers. Left as-is
  because the narrative is the only account of the chat history; flagged so the next reader knows.
- **Whether `lovable-workspace-knowledge.md` was ever installed** is unrecorded. Cheapest open item
  in the folder.
