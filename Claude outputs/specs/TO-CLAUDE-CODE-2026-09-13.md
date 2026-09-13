---
doc: TO-CLAUDE-CODE-2026-09-13
version: 1.0
status: AWAITING ACTION
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@47f7c7b
folder: specs
originally: new
---

> **AWAITING ACTION** · 13 Sep 2026. Four items that survived a cleanup of the outputs folder —
> everything else in the deleted documents had either shipped or been promoted elsewhere. These four
> are live, measured in the code today, and written down nowhere else.
> Ground truth lives in `state/CURRENT.md`.

# Four items pulled out of deleted documents

Eight files were deleted from `Claude outputs/` because their contents had shipped or moved. These
four items were the only things in them still open. They are listed here so nothing is lost with the
files. All counts were measured at `47f7c7b`.

---

## 1. `AGENTS.md` and the code disagree about chips — **fix both, one line each**

`AGENTS.md:152` assigns the Material 3 `sm` step, 8px, to *"chips/small controls"*.
`src/components/ml/ui.tsx:139` renders `Chip` as `rounded-full` — a pill. The M3 commit message
(`df56e0a`) agrees with the code: *"Buttons and chips stay pills."*

So the rule is wrong, not the component. Left as-is, **Lovable's agent will build 8px chips on every
new screen**, because `AGENTS.md` is what it reads first.

Two edits:

- `AGENTS.md:152` — drop "chips" from the `sm` role. `sm` 8px is small controls; chips are `full`.
- The live Lovable project knowledge — same correction. Mirror in
  `reference/lovable-knowledge-2026-09-13.md`.

---

## 2. The live Lovable knowledge claims a font token that does not exist

`reference/lovable-knowledge-2026-09-13.md:112` — and therefore the text Lovable's agent reads on
every prompt — says:

> `--font-sans`, `--font-display`, `--font-eyebrow` all resolve to Inter.

**There is no `--font-eyebrow` in the app.** `src/styles.css` declares exactly two font tokens:

```css
--font-sans:    "Inter", ui-sans-serif, system-ui, sans-serif;
--font-display: "Inter", ui-sans-serif, system-ui, sans-serif;
```

I wrote that line. It asserts a token that has never existed in this repo, which invites the agent
to reference it. Correct it to name the two real tokens. Same edit in `AGENTS.md` if it repeats the
claim.

---

## 3. Dark mode — DEFERRED, do not touch

**Vedanshu's decision, 13 Sep: leave it alone. Deferred to a future session.**
Do not delete `@custom-variant dark`, do not remove the variant line, do not "tidy" it.

Recorded here only so the next reader knows the state and does not rediscover it as a defect:

```css
/* src/styles.css:8 */
@custom-variant dark (&:is(.dark *));
```

- No `.dark { }` block anywhere in `src/styles.css`.
- Nothing in `src/` ever adds the `.dark` class.
- No `prefers-color-scheme` outside the reduced-motion block.
- Exactly **one** `dark:` utility in the whole app — `dark:border-destructive` in
  `src/components/ui/alert.tsx`, which is vendored shadcn and out of bounds anyway.

**Correction on record:** an earlier version of this document said there were 10 `dark:` utilities.
There is one. The grep was matching `dark: s.dark` in `ml/qr.tsx` and `"dark"` string keys in
`data/identity.ts` — JavaScript object properties, not Tailwind classes.

It is inert either way. That is the reason it can wait, not a reason to remove it.

## 4. Phase 8 has no spec, and here is its real size

Phase 8 is referenced as a dependency in `plans/PHASE-SPECS-2b-to-9.md` five times and specified
nowhere. Measured at `47f7c7b`, in `src/routes/` only:

| | Count |
|---|---|
| Arbitrary `text-[Npx]` | **1,057** |
| Inline `style={{ … }}` | **64** |

Phase 2 swept `src/components/ml/`. The route bodies were never touched, which is where nearly all
of the off-token values live. That is the ~97-hour estimate in one table.

Two things worth settling before anyone starts, because they change the shape of the sweep:

- **The type-step gap.** The app has five steps. The sizes actually used in routes cluster in the
  gap between `--type-3` and `--type-4`. Either a step is added, or ~1,057 call sites get rounded to
  a neighbour and some screens change size. That is a design call, not a sweep decision.
- **Whether `src/data` splits** in the same pass or a separate one.

---

## What was deleted, and where its content went

| Deleted | Content now lives in |
|---|---|
| `audits/REFACTOR-REVIEW-URLMAP.md` | The `/feed` bugs (fake 450ms timer, fabricated `5`/`2`/`1 shortlisted`, Bell labelled Notifications linking to `/messages`) are in `state/HANDOFF-2026-09-13.md` §E. The §5 URL table was hand-maintained prose, stale since the `.index.tsx` rename; it should be generated from the route tree, not kept. |
| `audits/TAILWIND-DS-AUDIT.md` | Radius and spacing both shipped. Dark mode and the type gap are items 3 and 4 above. Its §B and §C were guide-repo work, now out of scope. |
| `archive/MUSICLINKD-AUDIT.md` | Revision 1. `audits/MUSICLINKD-LOVABLE-AUDIT.md` §21 restates each correction in full, so the original is no longer needed to read them. |
| `archive/audit-part2-governance-and-code.md` | Its code-review half is in `plans/PHASE-SPECS-2b-to-9.md`; the three-way content split is in `AGENTS.md:196`. |
| `archive/DESIGN-WORK-STATE.md` | `state/CURRENT.md`. |
| `audits/pending-screens-2026-09-07.md` | `audits/pending-screens-audit-2026-09-13.md`, which checks every item against the code. |
| `reference/lovable-project-knowledge.md` | Superseded by `reference/lovable-knowledge-2026-09-13.md`, the text actually applied. |
| `.DS_Store` | Nothing. |

`archive/lovable-knowledge-PREVIOUS.md` was **kept** — it is the only way to roll back the 13 Sep
knowledge change.

All deleted files remain in git history.
