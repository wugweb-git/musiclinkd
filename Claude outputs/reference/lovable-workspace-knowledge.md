---
doc: lovable-workspace-knowledge
version: 1.0
status: CURRENT
updated: 2026-09-13
verified_against: musiclinkd-9e6ca875@0697a42
folder: reference
originally: lovable-workspace-knowledge.md
---

> **CURRENT** · v1.0 · 2026-09-13. Wugweb-wide standing rules. Deliberately abstract, so it has not aged. UNVERIFIED: whether this was ever installed as Lovable workspace knowledge.
> Ground truth lives in `state/CURRENT.md`; where this file disagrees with it, CURRENT wins.

# Wugweb — Workspace Knowledge

Applies to every project in this workspace. Project Knowledge overrides this where they conflict.

## Who we are

Wugweb is a design and technology agency. Client work spans fintech, govtech, edtech, EV and
healthcare. Output is shipped product, not concept work — treat every build as production-bound.

## Non-negotiables

**Never use emoji as icons.** Not as glyphs in JSX, not as fields in data files, not as
placeholders. They render as empty boxes on real devices and read as unserious. Use a real icon
library — `lucide-react` unless the project specifies otherwise.

**Never ship a control that does nothing.** A button or link with no handler and no destination is
a bug. If the action is not built, disable the control and label why.

**Never fabricate.** No invented statistics, fake availability, placeholder review counts, or copy
implying a payment, booking or upload succeeded when the system cannot do it. If a state is not
supported, do not design a screen that claims it.

**Never use stock photography** unless the project explicitly allows it, and never initials,
cartoon avatars or AI-looking imagery for people-facing content.

**Accessibility is a requirement, not a pass.** Visible `:focus-visible` ring on every interactive
control. 48dp minimum touch target — a smaller-looking control keeps an invisible 48dp hit area.
4.5:1 contrast for body text. Accessible label on every icon-only button. Colour is never the only
signal of state.

## Design systems

If the project has a design system, it is the authority — over any prompt, brief or older
document, including a stale `README.md`. Read the token source before writing UI.

Use its scales exactly: spacing, radius, type, motion, opacity. Never write arbitrary values
(`text-[13px]`, `rounded-[24px]`) when a token exists. If a value you need is genuinely not on the
scale, say so and ask — do not silently invent a step.

Do not create a second component when one already exists. Check the component directory first.
Prefer the installed accessible primitives (Radix, shadcn) over hand-rolled dialogs, sheets,
drawers and skeletons — hand-rolled versions routinely miss focus trap, Escape and scroll lock.

One styling mechanism per project. Semantic tokens over raw hex. No inline `style` objects
carrying design values.

## Code

Write for the strict TypeScript settings the project actually has. Where
`exactOptionalPropertyTypes` is on, declare optional props as `prop?: T | undefined` at the
component definition — never patch it at the call site, or the same error returns with the next
batch of code.

Do not cast away type safety (`as any`) to make a router, form or query compile. A cast that
silences a typed router defeats the reason the library was chosen.

Never key styling or logic off user-visible copy. Use a union type or enum; keep the label a
separate display concern.

Separate UI copy, seed/demo content and runtime data into different modules. Runtime data must be
shaped so a real query can replace it without editing the screen. Never store a relative time as a
string — store a timestamp and format at render.

## Database

All schema changes go through migration files. Never apply DDL directly to a live database.

Do not invent tables for a domain that is already modelled elsewhere in the client's stack. Ask
first — a forked data model is more expensive than a delay.

## How to report work

**Verify before claiming done.** Typecheck, build, and a render pass at the project's target
viewport. If you did not run them, say so in plain words. Never write "fixed all the bugs" without
evidence.

**Commit messages must describe the commit.** A message that does not match the diff makes the
history worthless for everyone after you.

**Say when you cannot read something.** If a link, connector, attachment or file is unavailable,
stop and say so in the reply. A silent no-op turn costs a day.

**Surface conflicts instead of resolving them silently.** If two source documents disagree, put
the conflict and your choice in the reply — not only in your reasoning.

**Never force-push or rewrite published history.**

## Tone in product copy

Plain, specific, professional. No exclamation marks, no hype, no "Oops!". Error messages say what
happened and what to do next. Empty states explain why the space is empty and offer one useful
action. Write for a working professional, not a consumer app onboarding.
