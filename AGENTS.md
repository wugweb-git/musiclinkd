# AGENTS.md — MusicLinkd

Instructions for any Codex session (chat, Codex, Cowork) working in this repo.

## Standing rule: ask before acting

Ask first. Do not substitute your own judgment for the user's about what's
worth doing next, and do not go investigate, build, or "improve" something
adjacent to what was asked without checking first — even if it seems useful,
even if it's arguably in scope, even mid-task.

This means, concretely:

- If the user points at a specific file, screen, or URL and says look at it
  or fix it, look at THAT — don't pivot to a different file, a deeper
  investigation, or a new deliverable because it seemed more informative.
- Before running exploratory commands (shell, JS-in-page, network probes)
  that go beyond directly answering what was asked, say what you're about to
  do and why in one line, or ask.
- Before editing, committing, or building anything not explicitly requested
  in the current message, ask.
- If the task turns out to be broader or different from what the user's
  wording literally said, stop and confirm the real target before proceeding
  — don't guess and run with it.
- When genuinely unsure whether an action is in scope, treat that
  uncertainty as the answer: ask.

This rule exists because on 2026-08-16 the user asked to look at a specific
mobile UI render in `webapp`'s `index.html` and instead got a detour into
unrelated CSS-cascade/JS-console forensics across multiple pages — real
investigative work, but not what was asked, and not confirmed first. The
user's own words: "add a governance rule - ask for everything." Read that
literally, not as a one-off complaint.

## Standing rule: docs and skills update in parallel with code

Whenever a change is made to structure, tooling, or behavior in this repo,
update the relevant documentation and skills **in the same session**, not as
a follow-up. This includes:

- This file (`AGENTS.md`)
- `README.md` (repo map, public addresses, backend summary)
- `frontend/guide/INDEX.md` and `gestalt-library.md` (guide/library manifest)
- Any skill files under `frontend/guide/skills/` that describe affected workflows
- Build scripts' own inline comments, if their behavior changes

Do not let documentation drift from reality. `README.md` was found stale on
2026-08-14 — it described a repo structure (`app/`, `website/`, `styleguide/`,
`brand/` at root) that does not exist; the real structure is `frontend/website`,
`frontend/guide`, `webapp/`. That drift is the reason this rule exists. Verify
claims against the actual filesystem before writing them into any doc — don't
propagate what a previous doc says without checking.

## Known-accurate facts as of 2026-08-14

- **Hosting:** single Firebase Hosting site (`musiclinkd-7ae30`). No Vercel
  anywhere in this stack. `musiclinkd.com` and `admin.musiclinkd.com` serve
  **identical** deployed content (Firebase Hosting can't route by hostname
  within one site) — admin's real pages live at paths like `/dashboard`,
  `/login`, not at the admin domain's root. See `infrastructure/firebase-hosting.md`.
- **Build:** `admin/` (Next.js, `output: "export"`) + `website/` are merged
  into a build-only `dist/` via `./scripts/build-hosting.sh`. `dist/` is
  gitignored, never edited directly.
- **`frontend/guide/`:** contains the original `styleguide.html` (old, has
  known issues — duplicate token systems, unenforced emoji-vs-icon rule,
  duplicated sections — not yet remediated) plus a newer, separate Gestalt
  installation:
  - `gestalt/` — full vendored clone of `pinterest/gestalt` (Apache-2.0,
    v177.0.13, verified current against upstream on 2026-08-14).
  - `musiclinkd-system/` — a live, interactive React app (real Gestalt
    components/tokens/icons, not a mockup) rendered under MusicLinkd's own
    title. **This is not yet MusicLinkd's actual brand system** — it shows
    Gestalt's raw Pinterest tokens as-is. Building MusicLinkd's own brand
    layer (colors, type, spacing decisions) on top of this is future work,
    explicitly deferred until the Gestalt compile was confirmed complete.
  - `gestalt-library.md` / `INDEX.md` — manifest of what's vendored and why.
  - `admin/package.json` has `gestalt` installed; `webapp/` and
    `frontend/website/` do not (no React runtime — Gestalt can't install
    there as-is; tokens/icons would need to be ported to static CSS/SVG).
- **`musiclinkd-system` has no dark mode** — removed 2026-08-14 per explicit
  instruction. Don't re-add a theme toggle without being asked.
- **Deployment status:** none of the Gestalt/`musiclinkd-system` work has
  been deployed. `dist/guide/` still only contains the old styleguide.
  Getting it live requires the real build (`npm run build` in
  `musiclinkd-system/`, then `./scripts/build-hosting.sh` +
  `firebase deploy`) — run from an actual terminal, which no chat-based
  Codex session in this project has access to (file read/write only).
- **WordPress Block Theme (2026-11-22):** The root repository now includes a
  standard Full Site Editing (FSE) block theme (`style.css`, `theme.json`,
  `index.php`, `templates/`, `parts/`, `patterns/`) mapped for Hostinger
  WordPress deployment (`musiclinkd.com`). Allows tenant visual editing via
  the WordPress Site Editor with canonical tokens. Includes the imported
  production design system (`assets/css/musiclinkd.css`, `main.css`, `tokens.css`),
  interactions (`assets/js/main.js` with GSAP & ScrollTrigger), complete 17-section
  landing pattern (`patterns/landing.php`), native nav header and footer parts,
  and branded 404 template.
- **WordPress CMS, Help Centre & Legal System (2026-11-22):** All 11 CMS and
  Help Centre guides from the mobile app repository have been mapped and imported
  with full design system fidelity into WordPress:
  - Help Centre Hub (`/help/`) with searchable category pills and direct support CTA.
  - 7 Deep Guides (`/profile-that-gets-booked/`, `/pricing-your-first-service/`,
    `/how-verification-works/`, `/booking-policies-explained/`, `/review-policy/`,
    `/community-guidelines/`, `/whats-new/`).
  - Ecosystem & Commercial Pages (`/about/`, `/pricing/`, `/cookies/`).
  - Legal Sync: `/privacy/` and `/terms/` updated with "Short Version" executive
    summary boxes and creator ownership guarantee ("Your work stays yours").
  - Mobile UX polish in `assets/css/enhance.css` (zero horizontal overflow,
    scaled orbit displays, touch-friendly navigation drawer, and micro-feeds).
- **Gutenberg Blocks, Media Gallery & Site Identity (2026-11-22):**
  - Converted all 13 pages from raw HTML chunks into native Gutenberg block markup
    (`<!-- wp:group -->`, `<!-- wp:heading -->`, `<!-- wp:paragraph -->`, `<!-- wp:list -->`,
    `<!-- wp:buttons -->`, `<!-- wp:columns -->`), enabling native visual drag-and-drop
    editing in WordPress Admin (`/wp-admin/post.php`).
  - 55 Media Library assets imported into WordPress (`wp_posts` attachments), including
    official vector brand assets (`assets/img/brand/`) and editorial/speaker portraits
    (`assets/img/gallery/`).
  - Set `site_icon` (Favicon mark, ID 37) and `custom_logo` (White lockup, ID 36) in WordPress.
  - Added SVG upload support and full Gutenberg Block Editor visual stylesheet parity
    via `add_theme_support('editor-styles')` and `enqueue_block_editor_assets` in `functions.php`.
- **FluentCRM & FluentSMTP Email Architecture (2026-11-22):**
  - Native FluentCRM block-based email template (`fc_template`, ID 106) configured with clean editorial styling (centered brand header, Inter typography, callout pro-tips, pink pill CTA, and canonical unsubscribe footer).
  - Responsive cross-client HTML email signature created in `templates/email/signature.html` for user-generated correspondence via FluentSMTP (`contact@musiclinkd.com`), styled with ultra-minimal Linear/Stripe typographic hierarchy (no emojis, subtle monochrome vector mark, clean single-line contact, and subtle social glyphs without link clutter).
  - Streamlined `templates/page.html` to eliminate duplicate generic post titles and synced all ecosystem links in `parts/footer.html`.
- **Ecosystem Plugin Synchronization & Form Design Parity (2026-11-22):**
  - FluentCRM initialized with official company profile (`MusicLinkd`), sender (`MusicLinkd Creator Desk <contact@musiclinkd.com>`), logo asset, and address.
  - Fluent Forms synchronized with branded Creator Application (Form ID 1) and Early Access Newsletter (Form ID 2), direct database lead capture (external email notifications disabled, leaving outbound mail to FluentSMTP).
  - Native Fluent Forms CSS styling injected into `assets/css/enhance.css` with canonical `#FF005C` pill buttons, rounded inputs, and focus rings.
  - Rank Math SEO populated with canonical organization metadata, title separator (`·`), and social card defaults.
  - PWA for WP configured with brand tokens (`#FF005C` theme, `#0F0F14` background, vector favicon mark).

## Environment note

Chat-based Codex sessions in this repo (this one) have file read/write
access via a local Filesystem connector, but **no terminal/shell execution**.
Don't attempt to hand-copy large generated assets (bundles, compiled CSS,
icon sets) through chat — that's real build-script work; ask the user to run
the actual command instead of manually reconstructing its output.
