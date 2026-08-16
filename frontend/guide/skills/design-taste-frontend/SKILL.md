---
name: "design-taste-frontend"
description: "Anti-slop frontend skill for landing pages, portfolios, and redesigns. Reads the brief, infers the right design direction, and ships interfaces that do not look templated. Real design systems when applicable, audit-first on redesigns, strict pre-flight check."
---

# tasteskill: Anti-Slop Frontend Skill

> Landing pages, portfolios, and redesigns. Not dashboards, not data tables, not multi-step product UI.
> Every rule below is **contextual**. None of it fires automatically. First read the brief, then pull only what fits.

---

## 0. BRIEF INFERENCE (Read the Room Before Anything Else)

Before touching code or tweaking dials, **infer what the user actually wants**. Most LLM design output is bad because the model jumps to a default aesthetic instead of reading the room.

### 0.A Read these signals first
1. **Page kind** - landing (SaaS / consumer / agency / event), portfolio (dev / designer / creative studio), redesign (preserve vs overhaul), editorial / blog.
2. **Vibe words** the user used - "minimalist", "calm", "Linear-style", "Awwwards", "brutalist", "premium consumer", "Apple-y", "playful", "serious B2B", "editorial", "agency-y", "glassy", "dark tech".
3. **Reference signals** - URLs they linked, screenshots they pasted, products they named, brands they're competing with.
4. **Audience** - B2B procurement panel vs. design-conscious consumer vs. recruiter scanning a portfolio. The audience picks the aesthetic, not your taste.
5. **Brand assets that already exist** - logo, color, type, photography. For redesigns, these are starting material, not optional input (see Section 11).
6. **Quiet constraints** - accessibility-first audiences, public-sector, regulated industries, trust-first commerce, kids' products. These constraints OVERRIDE aesthetic preference.

### 0.B Output a one-line "Design Read" before generating
Before any code, state in one line: **"Reading this as: <page kind> for <audience>, with a <vibe> language, leaning toward <design system or aesthetic family>."**

Example reads:
- *"Reading this as: B2B SaaS landing for technical buyers, with a Linear-style minimalist language, leaning toward Tailwind utilities + Geist + restrained motion."*
- *"Reading this as: solo designer portfolio for hiring managers, with an editorial / kinetic-type language, leaning toward native CSS + scroll-driven animation + custom typography."*
- *"Reading this as: redesign of a public-sector service site, with a trust-first language, leaning toward GOV.UK Frontend or USWDS."*

### 0.C If the brief is ambiguous, ask one question, do not guess
Ask exactly **one** clarifying question - never a multi-question dump - and only when the design read genuinely diverges. Example: *"Should this feel closer to Linear-clean or Awwwards-experimental?"*

If you can confidently infer from context, **do not ask**. Just declare the design read and proceed.

### 0.D Anti-Default Discipline
Do not default to: AI-purple gradients, centered hero over dark mesh, three equal feature cards, generic glassmorphism on everything, infinite-loop micro-animations everywhere, Inter + slate-900. These are the LLM defaults. Reach past them deliberately based on the design read.

---

## 1. THE THREE DIALS (Core Configuration)

After the design read, set three dials. Every layout, motion, and density decision below is gated by these.

* **`DESIGN_VARIANCE: 8`** - 1 = Perfect Symmetry, 10 = Artsy Chaos
* **`MOTION_INTENSITY: 6`** - 1 = Static, 10 = Cinematic / Physics
* **`VISUAL_DENSITY: 4`** - 1 = Art Gallery / Airy, 10 = Cockpit / Packed Data

**Baseline:** `8 / 6 / 4`. Use these unless the design read overrides them. Do not ask the user to edit this file - overrides happen conversationally.

### 1.A Dial Inference (design read → dial values)
| Signal | VARIANCE | MOTION | DENSITY |
|---|---|---|---|
| "minimalist / clean / calm / editorial / Linear-style" | 5-6 | 3-4 | 2-3 |
| "premium consumer / Apple-y / luxury / brand" | 7-8 | 5-7 | 3-4 |
| "playful / wild / Dribbble / Awwwards / experimental / agency" | 9-10 | 8-10 | 3-4 |
| "landing page / portfolio / marketing site (default)" | 7-9 | 6-8 | 3-5 |
| "trust-first / public-sector / regulated / accessibility-critical" | 3-4 | 2-3 | 4-5 |
| "redesign - preserve" | match existing | +1 | match existing |
| "redesign - overhaul" | +2 | +2 | match existing |

### 1.B Use-Case Presets
| Use case | VARIANCE | MOTION | DENSITY |
|---|---|---|---|
| Landing (SaaS, mainstream) | 7 | 6 | 4 |
| Landing (Agency / creative) | 9 | 8 | 3 |
| Landing (Premium consumer) | 7 | 6 | 3 |
| Portfolio (Designer / studio) | 8 | 7 | 3 |
| Portfolio (Developer) | 6 | 5 | 4 |
| Editorial / Blog | 6 | 4 | 3 |
| Public-sector service | 3 | 2 | 5 |
| Redesign - preserve | match | match+1 | match |
| Redesign - overhaul | +2 | +2 | match |

### 1.C How the Dials Drive Output
Use these (or user-overridden values) as global variables. Cross-references throughout this document refer to these exact variable names - never invent aliases like `LAYOUT_VARIANCE` or `ANIM_LEVEL`.

---

## 2. BRIEF → DESIGN SYSTEM MAP

Once you have the design read (Section 0) and dials (Section 1), pick the right foundation. Do not invent CSS for things that have an official package. Do not pretend an aesthetic trend is an official system.

### 2.A When to reach for a real design system (use official packages)
| Brief reads as… | Reach for | Why |
|---|---|---|
| Microsoft / enterprise SaaS / dashboards | `@fluentui/react-components` or `@fluentui/web-components` | Official Fluent UI, Microsoft tokens, accessibility done |
| Google-ish UI, Material-flavored product | `@material/web` + Material 3 tokens | Official, theme-able via Material Theming |
| IBM-style B2B / enterprise analytics | `@carbon/react` + `@carbon/styles` | Official Carbon, mature data-density patterns |
| Shopify app surfaces | `polaris.js` web components / Polaris React | Required for Shopify admin UI |
| Atlassian / Jira-style product | `@atlaskit/*` + `@atlaskit/tokens` | Official Atlassian DS |
| GitHub-style devtool / community page | `@primer/css` or `@primer/react-brand` | Official Primer; Brand variant for marketing |
| Public-sector UK service | `govuk-frontend` | Legally / regulatorily expected |
| US public-sector / trust-first | `uswds` | Same |
| Fast local-business / agency MVP | Bootstrap 5.3 | Boring, fast, works |
| Modern accessible React foundation | `@radix-ui/themes` | Primitives + polished theme |
| Modern SaaS where you own the components | shadcn/ui (`npx shadcn@latest add ...`) | You own the code, easy to customise; never ship default state |
| Tailwind-based modern SaaS / AI marketing | Tailwind v4 utilities + `dark:` variant | Default for indie + small team builds |

**Honesty rule:** if the brief reads as one of the systems above, install and use the **official** package. Do not recreate its CSS by hand. Do not import a system's tokens but then override 90% of them.

**One system per project.** Do not mix Fluent React with Carbon in the same tree. Do not import shadcn/ui components into a Material 3 app.

### 2.B When the brief is an aesthetic, not a system
For these directions, there is **no single official package**. Build with native CSS + Tailwind + a maintained component library. Be honest in code comments about what is borrowed inspiration vs. official material.

| Aesthetic | Honest implementation |
|---|---|
| Glassmorphism / "frosted glass" | `backdrop-filter`, layered borders, highlight overlays. Provide solid-fill fallback for `prefers-reduced-transparency`. |
| Bento (Apple-style tile grids) | CSS Grid with mixed cell sizes. No single library owns this. |
| Brutalism | Native CSS, monospace, raw borders. No library. |
| Editorial / magazine | Serif type, asymmetric grid, generous whitespace. No library. |
| Dark tech / hacker | Mono + accent neon, terminal motifs. No library. |
| Aurora / mesh gradients | SVG or layered radial gradients. No library. |
| Kinetic typography | Native CSS animations, scroll-driven animations, GSAP for hijacks. No library. |
| **Apple Liquid Glass** | Apple documents this for Apple platforms only. **There is no official `liquid-glass.css`.** Web implementations are approximations using `backdrop-filter` + layered borders + highlights. Label clearly as approximation. |

---

## 3. DEFAULT ARCHITECTURE & CONVENTIONS

Unless the design read picks a real design system (Section 2.A), these are the defaults:

### 3.A Stack
* **Framework:** React or Next.js. Default to Server Components (RSC).
  * **RSC SAFETY:** Global state works ONLY in Client Components. In Next.js, wrap providers in a `"use client"` component.
  * **INTERACTIVITY ISOLATION:** Any component using Motion, scroll listeners, or pointer physics MUST be an isolated leaf with `'use client'` at the top. Server Components render static layouts only.
* **Styling:** **Tailwind v4** (default). Tailwind v3 only if the existing project demands it.
  * For v4: do NOT use `tailwindcss` plugin in `postcss.config.js`. Use `@tailwindcss/postcss` or the Vite plugin.
* **Animation:** **Motion** (the library formerly known as Framer Motion). Import from `motion/react` (`import { motion } from "motion/react"`). The `framer-motion` package still works as a legacy alias - prefer `motion/react` in new code.
* **Fonts:** Always use `next/font` (Next.js) or self-host with `@font-face` + `font-display: swap`. Never link Google Fonts via `<link>` in production.

### 3.B State
* Local `useState` / `useReducer` for isolated UI.
* Global state ONLY for deep prop-drilling avoidance - Zustand, Jotai, or React context.
* **NEVER** use `useState` to track continuous values driven by user input (mouse position, scroll progress, pointer physics, magnetic hover). Use Motion's `useMotionValue` / `useTransform` / `useScroll`. `useState` re-renders the React tree on every change and collapses on mobile.

### 3.C Icons
* **Allowed libraries (priority order):** `@phosphor-icons/react`, `hugeicons-react`, `@radix-ui/react-icons`, `@tabler/icons-react`.
* **Discouraged:** `lucide-react`. Acceptable only when the user explicitly asks for it or the project already depends on it.
* **NEVER hand-roll SVG icons.** If a glyph is missing, install a second library or compose from primitives - do not draw icon paths from scratch.
* **One family per project.** Do not mix Phosphor with Lucide in the same component tree.
* **Standardize `strokeWidth` globally** (e.g. `1.5` or `2.0`).

### 3.D Emoji Policy
Discouraged by default in code, markup, and visible text. Replace symbols with icon-library glyphs. **Override:** allow emojis only when the user explicitly asks for a playful / chat-style / social-native vibe - and even then use them sparingly with intent.

### 3.E Responsiveness & Layout Mechanics
* Standardize breakpoints (`sm 640`, `md 768`, `lg 1024`, `xl 1280`, `2xl 1536`).
* Contain page layouts using `max-w-[1400px] mx-auto` or `max-w-7xl`.
* **Viewport Stability:** NEVER use `h-screen` for full-height Hero sections. ALWAYS use `min-h-[100dvh]` to prevent layout jumping on mobile (iOS Safari address bar).
* **Grid over Flex-Math:** NEVER use complex flexbox percentage math (`w-[calc(33%-1rem)]`). ALWAYS use CSS Grid (`grid grid-cols-1 md:grid-cols-3 gap-6`).

### 3.F Dependency Verification (mandatory)
Before importing ANY 3rd-party library, check `package.json`. If the package is missing, output the install command first. **Never** assume a library exists.

---

## 4. DESIGN ENGINEERING DIRECTIVES (Bias Correction)

LLMs default to clichés. Override these defaults proactively. Each rule has a context-aware override path.

### 4.1 Typography
* **Display / Headlines:** Default `text-4xl md:text-6xl tracking-tighter leading-none`.
* **Body / Paragraphs:** Default `text-base text-gray-600 leading-relaxed max-w-[65ch]`.
* **Sans font choice:**
  * **Discouraged as default:** `Inter`. Pick `Geist`, `Outfit`, `Cabinet Grotesk`, `Satoshi`, or a brand-appropriate serif first.
  * **Override:** Inter is acceptable when the user explicitly asks for a neutral / standard / Linear-style feel, or when the brief is a public-sector / accessibility-first site.
* **Pairings to know:** `Geist` + `Geist Mono`, `Satoshi` + `JetBrains Mono`, `Cabinet Grotesk` + `Inter Tight`, `GT America` + `IBM Plex Mono`.

* **SERIF DISCIPLINE (VERY DISCOURAGED AS DEFAULT):**
  * Serif is **very discouraged as the default font for any project.** "It feels creative / premium / editorial" is NOT a reason to reach for serif.
  * **Serif is only acceptable when ONE of these is explicitly true:**
    - The brand brief literally names a serif font, OR
    - The aesthetic family is genuinely editorial / luxury / publication / manuscript / heritage / vintage AND you can articulate why this specific serif fits this specific brand
  * For everything else, **default sans-serif display** (Geist Display, ABC Diatype, Söhne Breit, Cabinet Grotesk Display, Migra Sans, GT Walsheim, Inter Display, PP Neue Montreal).
  * **EMPHASIS RULE (related):** When you want to emphasize a word within a headline, use **italic or bold of the SAME font**. Do NOT inject a random serif word into a sans headline just to add visual interest.
  * **Specifically BANNED as defaults:** `Fraunces` and `Instrument_Serif` (the two LLM-favorite display serifs).
  * **If a serif is justified** (rare), rotate from this pool, do NOT reuse the same serif across consecutive projects: PP Editorial New, GT Sectra Display, Cardinal Grotesque, Reckless Neue, Tiempos Headline, Recoleta, Cormorant Garamond, Playfair Display, EB Garamond, IvyPresto, Migra, Editorial Old, Saol Display, Söhne Breit Kursiv, Domaine Display, Canela, Schnyder, Tobias, NB Architekt, ITC Galliard.

* **ITALIC DESCENDER CLEARANCE (mandatory):** When italic is used in display type and the word contains a descender letter (`y g j p q`), `leading-[1]` or `leading-none` will clip the descender. Use `leading-[1.1]` minimum and add `pb-1` or `mb-1` reserve on the wrapping element.

### 4.2 Color Calibration
* Max 1 accent color. Saturation < 80% by default.
* **THE LILA RULE:** The "AI Purple / Blue glow" aesthetic is discouraged as a default. No automatic purple button glows, no random neon gradients. Use neutral bases (Zinc / Slate / Stone) with high-contrast singular accents (Emerald, Electric Blue, Deep Rose, Burnt Orange, etc.).
* **Override:** if the brand or brief explicitly asks for purple / violet / lila, embrace it, but execute with intent.
* **One palette per project.**
* **COLOR CONSISTENCY LOCK (mandatory):** Once an accent color is chosen for a page, it is used on the WHOLE page.

* **PREMIUM-CONSUMER PALETTE BAN (mandatory):**
  * For premium-consumer briefs (cookware, wellness, artisan, luxury, heritage craft, DTC home goods, etc.) the LLM default is **warm beige/cream + brass/clay/oxblood/ochre + espresso/ink dark text**. Concretely banned hex families as default backgrounds and accents:
    - Backgrounds: `#f5f1ea`, `#f7f5f1`, `#fbf8f1`, `#efeae0`, `#ece6db`, `#faf7f1`, `#e8dfcb`
    - Accents: `#b08947`, `#b6553a`, `#9a2436`, `#9c6e2a`, `#bc7c3a`, `#7d5621`
    - Text: `#1a1714`, `#1a1814`, `#1b1814`
  * **Default alternatives (rotate, do not reuse):** Cold Luxury (silver-grey + chrome + smoke), Forest (deep green + bone + amber), Black and Tan (true off-black + warm tan), Cobalt + Cream, Terracotta + Slate, Olive + Brick + Paper, Pure monochrome + single saturated pop.
  * **Override:** the beige+brass+espresso palette is acceptable ONLY when the brand brief explicitly names those colors, or the brand identity is genuinely vintage / artisan / warm-craft AND you can articulate why.

### 4.3 Layout Diversification
* **ANTI-CENTER BIAS:** Centered Hero / H1 sections are avoided when `DESIGN_VARIANCE > 4`. Force "Split Screen" (50/50), "Left-aligned content / right-aligned asset", "Asymmetric white-space", or scroll-pinned structures.
* **Override:** centered hero is OK for editorial / manifesto / launch-announcement briefs.

### 4.4 Materiality, Shadows, Cards
* Use cards ONLY when elevation communicates real hierarchy.
* When a shadow is used, tint it to the background hue. No pure-black drop shadows on light backgrounds.
* For `VISUAL_DENSITY > 7`: generic card containers are banned.
* **SHAPE CONSISTENCY LOCK (mandatory):** Pick ONE corner-radius scale for the page and stick to it.

### 4.5 Interactive UI States
LLMs default to "static successful state only." Always implement full cycles:
* **Loading:** Skeletal loaders matching the final layout's shape. Avoid generic circular spinners.
* **Empty States:** Beautifully composed; indicate how to populate.
* **Error States:** Clear, inline (forms), or contextual (toasts only for transient).
* **Tactile Feedback:** On `:active`, use `-translate-y-[1px]` or `scale-[0.98]` to simulate a physical push.
* **BUTTON CONTRAST CHECK (mandatory, a11y):** White button + white text, `bg-white` CTA with `text-white` label, transparent button against the page background with no border → all banned. WCAG AA min (4.5:1 body, 3:1 large text 18px+).
* **CTA BUTTON WRAP BAN (mandatory):** Button text MUST fit on one line at desktop.
* **NO DUPLICATE CTA INTENT (mandatory):** Two CTAs with the same intent on one page is a Pre-Flight Fail. One label per intent.
* **FORM CONTRAST CHECK (mandatory, a11y):** Form inputs, placeholder text, focus rings, helper text, and error text all pass WCAG AA contrast against the section background.

### 4.6 Data & Form Patterns
* Label ABOVE input. Helper text optional but present in markup. Error text BELOW input. Standard `gap-2` for input blocks.
* No placeholder-as-label. Ever.

### 4.7 Layout Discipline (Hard Rules. Failing any of these is shipping broken work)

* **Hero MUST fit in the initial viewport.** Headline max 2 lines on desktop, subtext max **20 words** AND max 3-4 lines, CTAs visible without scroll.
* **Hero font-scale discipline.** Plan font size and image size together. Default sensible range: `text-4xl md:text-5xl lg:text-6xl` for most heroes; `text-6xl md:text-7xl` only when the headline is 3-5 words.
* **HERO TOP PADDING CAP (mandatory):** Hero top padding max `pt-24` (≈6rem) at desktop.
* **HERO STACK DISCIPLINE (max 4 text elements).** Allowed: eyebrow OR brand strip (pick zero or one), headline (max 2 lines), subtext (max 20 words, max 4 lines), CTAs (1 primary + max 1 secondary). BANNED in the hero: tiny tagline below CTAs, trust micro-strip, pricing teaser, feature bullet list, social-proof avatar row.
* **"Used by" / "Trusted by" logo wall belongs UNDER the hero, never inside it.**
* **Navigation MUST render on a single line on desktop.** Height cap: 80px max desktop, default 64-72px.
* **Bento grids MUST have rhythm, not one-sided repetition.**
* **BENTO CELL COUNT RULE (mandatory):** A bento grid has EXACTLY as many cells as you have content for.
* **Section-Layout-Repetition Ban.** Once you use a layout family for a section, that family can appear at most ONCE on the page. A landing page with 8 sections must use at least 4 different layout families.
* **ZIGZAG ALTERNATION CAP (mandatory).** Max 2 sections in a row with an image+text-split pattern.
* **EYEBROW RESTRAINT (mandatory, the #1 violated rule in production tests).** Maximum 1 eyebrow per 3 sections. Hero counts as 1.
* **SPLIT-HEADER BAN (mandatory).** "left big headline + right small explainer paragraph" as a section header is banned as default.
* **Bento Background Diversity (mandatory).** At least 2-3 cells in any multi-cell grid need real visual variation.
* **Mobile collapse must be explicit per section.**

### 4.8 Image & Visual Asset Strategy

Landing pages and portfolios are **visual products**. Text-only pages with fake-screenshot divs are slop.

**Priority order for visual assets:**
1. **Image-generation tool first.** If ANY image-gen tool is available in the environment, use it to create section-specific assets.
2. **Real web images second.** `https://picsum.photos/seed/{descriptive-seed}/{w}/{h}` for placeholder photography; actual stock or brand URLs when the brief provides them; open-license sources if explicitly allowed.
3. **Last resort: tell the user.** Leave clearly-labeled placeholder slots and say what images are needed.

**Real company logos for social proof.** Use real SVG logos from **Simple Icons** (`https://cdn.simpleicons.org/{slug}/ffffff`) or **devicon**. Make-up the brand name? Then make-up an SVG mark too. **LOGO-ONLY rule:** logo wall = logos and nothing else, no industry/category labels below each logo.

**Div-based fake screenshots are banned.**

**Hero needs a real visual.** Text + gradient blob is not a hero.

### 4.9 Content Density

Landing pages live on the **first impression**, not the full read. Cut ruthlessly.

* **Default content shape per section:** short headline (≤ 8 words) + short sub-paragraph (≤ 25 words) + one visual asset OR one CTA.
* **No data-dump sections.**
* **Long lists need a different UI component, not a longer list.** If > 5 items: 2-column split, card grid, tabs/accordion, horizontal scroll-snap pills, carousel, or marquee.
* **Spec sheets specifically.** A long spec table with `border-b` on every row is banned. Use 2-col card grid, scroll-snap pills, grouped chunks, or featured-vs-rest.
* **COPY SELF-AUDIT (mandatory before ship):** Re-read every visible string. Flag anything grammatically broken, with unclear referents, sounding like AI hallucination, or reading like performative-thoughtful copy. Rewrite it plainly.
* **Fake-precise numbers are flagged.** Numbers must come from real data, be explicitly labeled as mock, or are banned as AI-invented spec aesthetics.
* **One copy register per page.**

### 4.10 Quotes & Testimonials

* **Max 3 lines** of quote body.
* **No em-dashes inside the quote text.**
* Attribution: name + role + (optionally) company.
* Quote marks: real typographic quotes or none.

### 4.11 Page Theme Lock (Light / Dark Mode Consistency)

The page has ONE theme. Sections do not invert. If the page is dark mode, ALL sections are dark mode. The exception: a deliberate, once-per-page "Theme Switch on Scroll" device. Set the theme ONCE at the page root.

---

## 5. CONTEXT-AWARE PROACTIVITY

These are tools, not defaults. Use them when the design read calls for them. **None of these fire automatically.**

* **Liquid Glass / Glassmorphism:** Appropriate for premium consumer, Apple-adjacent, luxury brand, or media-overlay vibes. Inappropriate for dashboards, public-sector, or "boring B2B." Add a 1px inner border and subtle inner shadow for physical edge refraction; provide a solid-fill fallback under `prefers-reduced-transparency`.
* **Magnetic Micro-physics:** Use when `MOTION_INTENSITY > 5` AND the brief reads premium / playful / agency. Implement EXCLUSIVELY with Motion's `useMotionValue` / `useTransform`. Never `useState`.
* **Perpetual Micro-Interactions:** Use when `MOTION_INTENSITY > 5` AND the section actively benefits from motion. Not every card needs an infinite loop. Apply Spring Physics, no linear easing.
* **"Motion claimed, motion shown."** If `MOTION_INTENSITY > 4`, the page must actually move. Never half-build motion that breaks.
* **MOTION MUST BE MOTIVATED (mandatory).** Before adding any animation, ask what it communicates: hierarchy, storytelling, feedback, or state transition. "It looked cool" is invalid.
* **MARQUEE MAX-ONE-PER-PAGE (mandatory).**
* **GSAP Sticky-Stack Pattern:** must be a REAL sticky-stack (pin at `start: "top top"`), not a sequential reveal list.
* **GSAP Horizontal-Pan Pattern:** pin the wrapper, scrub the inner track, `start: "top top"`.

### 5.A Sticky-Stack - Canonical Skeleton

```tsx
"use client";
import { useRef, useEffect } from "react";
import { gsap } from "gsap";
import { ScrollTrigger } from "gsap/ScrollTrigger";
import { useReducedMotion } from "motion/react";

gsap.registerPlugin(ScrollTrigger);

export function StickyStack({ cards }: { cards: React.ReactNode[] }) {
  const ref = useRef<HTMLDivElement>(null);
  const reduce = useReducedMotion();

  useEffect(() => {
    if (reduce || !ref.current) return;
    const ctx = gsap.context(() => {
      const cardEls = gsap.utils.toArray<HTMLElement>(".stack-card");
      cardEls.forEach((card, i) => {
        if (i === cardEls.length - 1) return;
        ScrollTrigger.create({
          trigger: card,
          start: "top top",
          endTrigger: cardEls[cardEls.length - 1],
          end: "top top",
          pin: true,
          pinSpacing: false,
        });
        gsap.to(card, {
          scale: 0.92,
          opacity: 0.55,
          ease: "none",
          scrollTrigger: {
            trigger: cardEls[i + 1],
            start: "top bottom",
            end: "top top",
            scrub: true,
          },
        });
      });
    }, ref);
    return () => ctx.revert();
  }, [reduce]);

  return (
    <div ref={ref} className="relative">
      {cards.map((card, i) => (
        <div
          key={i}
          className="stack-card sticky top-0 min-h-[100dvh] flex items-center justify-center"
        >
          {card}
        </div>
      ))}
    </div>
  );
}
```

Critical points: `start: "top top"`, `pin: true`, every card except the last is pinned, the scale/opacity transform is driven by the NEXT card's scroll trigger.

### 5.B Horizontal-Pan - Canonical Skeleton

```tsx
"use client";
import { useRef, useEffect } from "react";
import { gsap } from "gsap";
import { ScrollTrigger } from "gsap/ScrollTrigger";
import { useReducedMotion } from "motion/react";

gsap.registerPlugin(ScrollTrigger);

export function HorizontalPan({ children }: { children: React.ReactNode }) {
  const wrap = useRef<HTMLDivElement>(null);
  const track = useRef<HTMLDivElement>(null);
  const reduce = useReducedMotion();

  useEffect(() => {
    if (reduce || !wrap.current || !track.current) return;
    const ctx = gsap.context(() => {
      const distance = track.current!.scrollWidth - window.innerWidth;
      gsap.to(track.current, {
        x: -distance,
        ease: "none",
        scrollTrigger: {
          trigger: wrap.current,
          start: "top top",
          end: () => `+=${distance}`,
          pin: true,
          scrub: 1,
          invalidateOnRefresh: true,
        },
      });
    }, wrap);
    return () => ctx.revert();
  }, [reduce]);

  return (
    <section ref={wrap} className="relative overflow-hidden">
      <div ref={track} className="flex h-[100dvh] items-center">
        {children}
      </div>
    </section>
  );
}
```

Critical points: `start: "top top"`, `pin: true`, `end: "+=${distance}"`, `scrub: 1`.

### 5.C Scroll-Reveal Stagger - Canonical Skeleton (lighter alternative)

For simple "items appear as they enter viewport" (no pinning), prefer Motion's `whileInView` over GSAP:

```tsx
"use client";
import { motion, useReducedMotion } from "motion/react";

export function RevealStagger({ items }: { items: string[] }) {
  const reduce = useReducedMotion();
  return (
    <ul className="grid gap-6">
      {items.map((item, i) => (
        <motion.li
          key={item}
          initial={reduce ? false : { opacity: 0, y: 24 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, amount: 0.3 }}
          transition={{
            duration: 0.6,
            delay: i * 0.06,
            ease: [0.16, 1, 0.3, 1],
          }}
        >
          {item}
        </motion.li>
      ))}
    </ul>
  );
}
```

Use this for feature lists, testimonial grids, logo walls. Save GSAP for actual pin/scrub work.

### 5.D Forbidden Animation Patterns

* **`window.addEventListener("scroll", ...)`** is banned. Use Motion's `useScroll()`, GSAP's `ScrollTrigger`, IntersectionObserver, or CSS `scroll-driven animations`.
* **Custom scroll progress calculations using `window.scrollY`** in React state - banned.
* **`requestAnimationFrame` loops that touch React state** - banned. Use motion values instead.
* **Layout Transitions:** Use Motion's `layout` and `layoutId` props for visible state changes only.
* **Staggered Orchestration:** Use `staggerChildren` (Motion) or CSS cascade.

---

## 6. PERFORMANCE & ACCESSIBILITY GUARDRAILS

### 6.A Hardware Acceleration
* Animate ONLY `transform` and `opacity`. Never animate `top`, `left`, `width`, `height`.
* Use `will-change: transform` sparingly.

### 6.B Reduced Motion (mandatory)
* **Any motion above `MOTION_INTENSITY > 3` MUST honor `prefers-reduced-motion`.** Non-negotiable.
* Infinite loops, parallax, scroll-hijack, and magnetic physics MUST collapse to static / instant under reduced motion.

### 6.C Dark Mode (mandatory for any consumer-facing page)
* Design for **both modes from the start**.
* Respect `prefers-color-scheme: dark`. Default to system preference unless the brand insists on one mode.

### 6.D Core Web Vitals Targets
* **LCP** < 2.5s. **INP** < 200ms. **CLS** < 0.1. Run Lighthouse before declaring a page done.

### 6.E DOM Cost
* Apply grain/noise filters EXCLUSIVELY to fixed, `pointer-events-none` pseudo-elements. NEVER on scrolling containers.
* Be aware of bundle size. Lazy-load anything not above-the-fold.

### 6.F Z-Index Restraint
NEVER spam arbitrary `z-50` or `z-10`. Use z-index strictly for systemic layer contexts.

---

## 7. DIAL DEFINITIONS (Technical Reference)

### DESIGN_VARIANCE (Level 1-10)
* **1-3 (Predictable):** Symmetrical CSS Grid, equal paddings, centered alignment.
* **4-7 (Offset):** Overlaps, varied image aspect ratios, left-aligned headers.
* **8-10 (Asymmetric):** Masonry layouts, fractional-unit grids, massive empty zones.
* **MOBILE OVERRIDE:** For levels 4-10, asymmetric layouts above `md:` MUST collapse to strict single-column on viewports `< 768px`.

### MOTION_INTENSITY (Level 1-10)
* **1-3 (Static):** No automatic animations. CSS `:hover`/`:active` only.
* **4-7 (Fluid CSS):** `transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1)`. Focus on `transform` and `opacity`.
* **8-10 (Advanced Choreography):** Complex scroll-triggered reveals, parallax, scroll-driven animation. **NEVER use `window.addEventListener('scroll')`.**

### VISUAL_DENSITY (Level 1-10)
* **1-3 (Art Gallery):** Huge section gaps (`py-32` to `py-48`).
* **4-7 (Daily App):** Standard spacing (`py-16` to `py-24`).
* **8-10 (Cockpit):** Tight paddings, no card boxes, `font-mono` for all numbers.

---

## 8. DARK MODE PROTOCOL

Dual-mode by default. Never assume light-only unless the brief is print-emulating editorial.

* **Token Strategy (pick one, stick to it):** Tailwind `dark:` variant, or CSS variables swapped under `[data-theme="dark"]`.
* **Do not prescribe specific dark-mode colors.** The brief decides. Enforce only: contrast (WCAG AA body, AAA hero copy), hierarchy parity, brand fidelity, no pure `#000000`/`#ffffff`.
* Respect `prefers-color-scheme` unless the brand insists. Add a manual toggle if either mode would lose key brand expression.
* Test in both modes before finishing.

---

## 9. AI TELLS (Forbidden Patterns)

Avoid these signatures unless the brief explicitly asks for them.

### 9.A Visual & CSS
* NO neon/outer glows by default. NO pure black (`#000000`). NO oversaturated accents. NO excessive gradient text for large headers. NO custom mouse cursors.

### 9.B Typography
* AVOID Inter as default. NO oversized H1s that just scream. Serif for editorial/luxury/publication only, not dashboards.

### 9.C Layout & Spacing
* Mathematically perfect padding/margins. NO 3-column equal feature cards (the generic "three identical cards" row is banned).

### 9.D Content & Data ("Jane Doe" Effect)
* NO generic names ("John Doe"). NO generic avatars (egg/Lucide user icons). NO fake-perfect numbers (`99.99%`) - use organic, messy data. NO startup-slop brand names ("Acme", "Nexus"). NO filler verbs ("Elevate", "Seamless", "Unleash").

### 9.E External Resources & Components
* NO hand-rolled SVG icons (use Phosphor/HugeIcons/Radix/Tabler). NO div-based fake screenshots. NO broken Unsplash links. shadcn/ui customization allowed but never in default state.

### 9.F Production-Test Tells (banned outright)

**Hero & top-of-page:** NO version labels in the hero (`V0.6`, `BETA`, `EARLY ACCESS`) unless the brief is a launch. NO "Brand · No. 01"-style sub-eyebrows.

**Section numbering & micro-labels:** NO section-number eyebrows (`00 / INDEX`, `001 · Capabilities`). NO `01/4`-style pagination on tiles. NO "Scroll · 001 Capabilities"-style scroll cues.

**Separators & dots:** Middle-dot (`·`) rationed to max 1 per line. NO decorative colored status dots by default.

**Em-dashes & typography flourishes:** See 9.G. NO `<br>`-broken-and-italicized headlines as default. NO vertical rotated text unless explicitly agency/Awwwards. NO crosshair/hairline grid lines as pure decoration.

**Fake product previews:** NO div-based fake product UI in the hero. NO fake version footers inside fake screenshots.

**Marketing-copy Tells:** NO "Quietly in use at" style social-proof headers - use plain language. NO "Field notes"/"On our desks"-style poetic labels - use plain functional labels. NO mock-humble industry references. NO weather/locale strips unless the brief is genuinely place/timezone-focused. NO micro-meta-sentences under eyebrows. NO generic step labels ("Stage 1/Stage 2/Stage 3") - use the actual verb-noun action.

**Pills, labels and version stamps:** NO pills/labels overlaid on images. NO photo-credit captions as decoration unless crediting a real photographer. NO version footers on marketing pages. NO "Reservation 412 of 800"-style fake live-stock counters.

**Decoration text strips:** NO decoration text strip at hero bottom (`BRAND. MOTION. SPATIAL.`) unless it's a real sticky nav or real status info. NO floating top-right sub-text in section headings.

**Lists, dividers and scoring:** NO `border-t` + `border-b` on every row of a long list. NO filled-background-track progress bars as comparison visuals on a landing page.

**Locale, time, scroll cues:** Locale/city/time/weather strips banned for 99% of briefs. Scroll cues (`Scroll`, `↓ scroll`) banned. Zero decorative status dots by default.

### 9.G EM-DASH BAN (the single most-violated Tell)

**Em-dash (`—`) is COMPLETELY banned.** No "limited use" allowance, no "natural language frequency" allowance. None. Banned in headlines, eyebrows, labels, pills, button text, image captions, nav items, body copy, quote attribution. En-dash (`–`) as a separator is also banned - use a regular hyphen for date/number ranges.

The ONLY permitted dash characters: regular hyphen `-`, minus sign in math.

If the output contains a single `—` or `–` anywhere visible to the user, it fails the Pre-Flight Check and must be rewritten. This rule is binary: zero em-dashes.

---

## 10. REFERENCE VOCABULARY (Pattern Names the Agent Should Know)

A vocabulary, not a library. Know these pattern names to design with them in mind and reach for them when the design read calls for them.

**Hero Paradigms:** Asymmetric Split Hero, Editorial Manifesto Hero, Video/Media Mask Hero, Kinetic-Type Hero, Curtain-Reveal Hero, Scroll-Pinned Hero.

**Navigation & Menus:** Mac OS Dock Magnification, Magnetic Button, Gooey Menu, Dynamic Island, Contextual Radial Menu, Floating Speed Dial, Mega Menu Reveal.

**Layout & Grids:** Bento Grid, Masonry Layout, Chroma Grid, Split-Screen Scroll, Sticky-Stack Sections.

**Cards & Containers:** Parallax Tilt Card, Spotlight Border Card, Glassmorphism Panel, Holographic Foil Card, Tinder Swipe Stack, Morphing Modal.

**Scroll Animations:** Sticky Scroll Stack, Horizontal Scroll Hijack, Locomotive/Sequence Scroll, Zoom Parallax, Scroll Progress Path, Liquid Swipe Transition.

**Galleries & Media:** Dome Gallery, Coverflow Carousel, Drag-to-Pan Grid, Accordion Image Slider, Hover Image Trail, Glitch Effect Image.

**Typography & Text:** Kinetic Marquee, Text Mask Reveal, Text Scramble Effect, Circular Text Path, Gradient Stroke Animation, Kinetic Typography Grid.

**Micro-Interactions & Effects:** Particle Explosion Button, Liquid Pull-to-Refresh, Skeleton Shimmer, Directional Hover-Aware Button, Ripple Click Effect, Animated SVG Line Drawing, Mesh Gradient Background, Lens Blur Depth.

**Animation Library Choice:** Motion (`motion/react`) for UI/Bento/state-change motion; GSAP + ScrollTrigger for full-page scrolltelling and scroll hijacks (isolate in dedicated leaf components); Three.js/WebGL for canvas backgrounds and 3D scenes. NEVER mix GSAP/Three.js with Motion in the same component tree.

---

## 11. REDESIGN PROTOCOL

This skill handles **greenfield builds AND redesigns**. Misclassifying the mode is the single biggest source of bad redesign output.

### 11.A Detect the Mode (first action)
* **Greenfield** - no existing site, or full overhaul approved. Dial baseline from Section 1.
* **Redesign - Preserve** - modernise without breaking the brand. Audit first, extract brand tokens, evolve gradually.
* **Redesign - Overhaul** - new visual language on top of existing content. Treat as greenfield for visuals; preserve content and IA.

If ambiguous, ask **once**: *"Should this redesign preserve the existing brand, or are we starting visually from scratch?"*

### 11.B Audit Before Touching
Document the current state before proposing changes: brand tokens, information architecture, content blocks, patterns to preserve, patterns to retire, dial reading of the existing site, SEO baseline. **SEO migration is the #1 redesign risk.**

### 11.C Preservation Rules
* Do not change information architecture unless asked. Keep page slugs, anchor IDs, primary nav labels stable.
* Extract brand colors before applying Section 4.2 - a brand that is already purple stays purple.
* Preserve copy voice unless asked for a rewrite.
* Honor existing accessibility wins.
* Respect existing analytics events - do not rename buttons, form fields, section IDs downstream tracking depends on.

### 11.D Modernisation Levers (priority order)
Apply in order, stop when the brief is satisfied: 1) Typography refresh, 2) Spacing & rhythm, 3) Color recalibration, 4) Motion layer, 5) Hero & key-section recomposition, 6) Full block replacement (last resort).

### 11.E Decision Tree: Targeted Evolution vs Full Redesign
* IA, content, and SEO sound → targeted evolution (Levers 1-4). ~70% of value at ~40% of risk.
* Visual debt is structural → full redesign with strict content preservation.
* Brand itself is changing → greenfield.

### 11.F What Never Changes Silently
Never modify without explicit user approval: URL structure/route slugs, primary nav labels, form field names/order, brand logo/wordmark, existing legal/consent/cookie copy.

---

## 12. THE BLOCK LIBRARY

The Reference Vocabulary (Section 10) names patterns; block implementations (real props, motion specs, code sketches) are added iteratively per-project as needed. Required frontmatter per block: `name`, `category`, `dial_compatibility` (variance/motion/density ranges), `when_to_use`, `not_for`, `stack`. Required body sections: visual sketch, props API, code sketch, mobile fallback, motion variants per intensity band, dark-mode notes, anti-patterns, references.

---

## 13. OUT OF SCOPE

This skill is NOT for: dashboards/dense product UI/admin panels (use Fluent, Carbon, Atlassian, or Polaris from Section 2.A), data tables (use TanStack Table or AG Grid), multi-step forms/wizards, code editors (use Monaco/CodeMirror with official skinning), native mobile (use Apple HIG/Material directly), realtime collab UIs.

If the brief is one of the above, **say so explicitly**, point to the right tool, and only apply this skill's marketing-page/about-page/landing-page parts to the surfaces where they apply.

---

## 14. FINAL PRE-FLIGHT CHECK

Run this matrix before outputting code. **THIS IS NOT OPTIONAL.** If any box fails, the output is not done.

- Brief inference declared (one-liner)? Dial values explicit and reasoned from the brief?
- Design system chosen from Section 2 if applicable, or aesthetic labeled honestly?
- Redesign mode detected and audit performed (if applicable)?
- **ZERO em-dashes anywhere on the page** (headlines, eyebrows, pills, body, quotes, attribution, captions, buttons, alt text)?
- Page Theme Lock: ONE theme for the whole page, no mid-page inversion?
- Color Consistency Lock: one accent color used identically across all sections?
- Shape Consistency Lock: one corner-radius system applied consistently?
- Button Contrast Check: every CTA text readable against its background (WCAG AA 4.5:1)?
- CTA Button Wrap: no CTA label wraps to 2+ lines at desktop?
- Form Contrast Check: inputs, placeholders, focus rings, labels all pass WCAG AA?
- Serif discipline: not Fraunces/Instrument_Serif unless explicitly justified, different from previous project?
- Premium-consumer palette check: not the AI-default beige+brass+oxblood+espresso family?
- Italic descender clearance handled?
- Hero fits the viewport (headline ≤2 lines, subtext ≤20 words/≤4 lines, CTA visible without scroll)?
- Hero top padding max `pt-24`?
- Hero stack discipline: max 4 text elements, no tiny tagline/trust strip in hero?
- Eyebrow count ≤ ceil(sectionCount/3)?
- No Split-Header pattern? No 3+ consecutive zigzag sections?
- No Duplicate CTA Intent?
- Logo wall = logo only, lives under the hero, uses real SVG logos?
- Bento Background Diversity: 2-3 cells have real visual variation?
- Copy Self-Audit done: no grammatically-broken or AI-hallucinated phrases shipped?
- Motion motivated (hierarchy/storytelling/feedback/state transition), not GSAP-for-show?
- Marquee max-one-per-page?
- Navigation on ONE line at desktop, height ≤80px?
- Section-Layout-Repetition check: at least 4 different layout families across 8 sections?
- Bento has rhythm and exact cell count, no empty cells?
- Long lists use the right UI component (not default `<ul>` with `divide-y` for >5 items)?
- Real images used (gen-tool first, then Picsum-seed, then explicit placeholder slots) - no div-based fake screenshots, no hand-rolled decorative SVGs?
- No pills/labels overlaid on images, no photo-credit-as-decoration, no version footers, no micro-meta-sentences, no decoration text strip at hero bottom, no floating top-right sub-text?
- No filled-track progress bars as comparison visuals?
- No locale/time/weather strips unless genuinely relevant? No scroll cues? No version labels in hero unless a launch? No section-numbering eyebrows? No decorative dots?
- No `border-t`+`border-b` on every row of long lists?
- Content density sane: no 20-row data tables, ≤25-word sub-paragraphs by default?
- Quotes ≤3 lines, clean attribution, no em-dash?
- Motion claimed = motion shown?
- GSAP sticky-stack/horizontal-pan use the canonical `start: "top top"` skeleton?
- No `window.addEventListener('scroll')` anywhere?
- Reduced motion wrapped for everything `MOTION_INTENSITY > 3`? Dark mode tested in both modes?
- Mobile collapse explicit? Viewport stability (`min-h-[100dvh]`, never `h-screen`)?
- `useEffect` animations have cleanup functions? Empty/loading/error states provided?
- Cards omitted in favor of spacing where possible? Icons from an allowed library only?
- Motion isolated in client-leaf components? No AI Tells from Section 9 present?
- Core Web Vitals plausibly hit (LCP<2.5s, INP<200ms, CLS<0.1)? One design system per project?

If a single item cannot be honestly confirmed, the page is not done. Fix it before delivering.

---

## Appendix A - Install Commands per Design System

```bash
npm install @material/web                          # Material Web (Material 3)
npm install @fluentui/react-components              # Fluent UI React (v9)
npm install @fluentui/web-components @fluentui/tokens  # Fluent UI Web Components
npm install @carbon/react @carbon/styles             # IBM Carbon
npm install @radix-ui/themes                         # Radix Themes
npx shadcn@latest init && npx shadcn@latest add button card badge separator input  # shadcn/ui
npm install --save @primer/css                       # Primer CSS (GitHub devtool UI)
npm install @primer/react-brand                      # Primer Brand (GitHub marketing UI)
npm install govuk-frontend                           # GOV.UK Frontend
npm install uswds                                     # USWDS
yarn add @atlaskit/css-reset @atlaskit/tokens @atlaskit/button @atlaskit/badge @atlaskit/section-message @atlaskit/card  # Atlassian
npm install bootstrap                                 # Bootstrap 5.3
# Shopify Polaris Web Components (Shopify apps only) - add to app HTML head:
#   <meta name="shopify-api-key" content="%SHOPIFY_API_KEY%" />
#   <script src="https://cdn.shopify.com/shopifycloud/polaris.js"></script>
```

## Appendix B - Canonical Sources (read these before reinventing)

Material Web: https://github.com/material-components/material-web · https://material-web.dev/theming/material-theming/
Fluent UI: https://fluent2.microsoft.design/get-started/develop · https://github.com/microsoft/fluentui
Carbon: https://carbondesignsystem.com/ · https://github.com/carbon-design-system/carbon
Shopify Polaris: https://shopify.dev/docs/api/app-home/web-components · https://github.com/Shopify/polaris-react
Atlassian: https://atlassian.design/get-started/develop · https://atlassian.design/tokens/design-tokens
Primer: https://primer.style/ · https://github.com/primer/css
GOV.UK: https://design-system.service.gov.uk/components/button/ · https://github.com/alphagov/govuk-frontend
USWDS: https://designsystem.digital.gov/ · https://github.com/uswds/uswds
Bootstrap: https://getbootstrap.com/docs/5.3/layout/grid/
Tailwind: https://tailwindcss.com/docs/dark-mode
Radix: https://www.radix-ui.com/themes/docs/components/theme
shadcn/ui: https://ui.shadcn.com/docs
Native CSS/W3C: MDN backdrop-filter, prefers-color-scheme, prefers-reduced-motion, CSS Grid, Scroll-driven animations
Apple Liquid Glass (Apple platforms only): Apple HIG → Materials, Apple Developer Docs → Liquid Glass, SwiftUI → Material

## Appendix C - Apple Liquid Glass: Honest Web Approximation

Do **not** treat random CSS snippets as official Apple Liquid Glass. Apple documents Liquid Glass for Apple platforms only - it is not a public web CSS package. There is no `liquid-glass.css` from Apple. A web approximation can use `backdrop-filter`, transparent backgrounds, layered borders, highlight overlays, gradients, and motion - but that is web glassmorphism / frosted-glass approximation, not official Apple Liquid Glass. Label it as such in comments.

```css
.liquid-glass-web-approx {
  position: relative;
  isolation: isolate;
  overflow: hidden;
  border-radius: 999px;
  border: 1px solid rgb(255 255 255 / .32);
  background:
    linear-gradient(135deg, rgb(255 255 255 / .30), rgb(255 255 255 / .08)),
    rgb(255 255 255 / .12);
  backdrop-filter: blur(24px) saturate(180%) contrast(1.05);
  -webkit-backdrop-filter: blur(24px) saturate(180%) contrast(1.05);
  box-shadow:
    inset 0 1px 0 rgb(255 255 255 / .48),
    inset 0 -1px 0 rgb(255 255 255 / .12),
    0 18px 60px rgb(0 0 0 / .18);
}

.liquid-glass-web-approx::before {
  content: "";
  position: absolute;
  inset: 0;
  z-index: -1;
  border-radius: inherit;
  background:
    radial-gradient(circle at 20% 0%, rgb(255 255 255 / .55), transparent 34%),
    linear-gradient(90deg, rgb(255 255 255 / .18), transparent 42%, rgb(255 255 255 / .14));
  pointer-events: none;
}

.liquid-glass-web-approx::after {
  content: "";
  position: absolute;
  inset: 1px;
  border-radius: inherit;
  border: 1px solid rgb(255 255 255 / .14);
  pointer-events: none;
}

@media (prefers-color-scheme: dark) {
  .liquid-glass-web-approx {
    border-color: rgb(255 255 255 / .18);
    background:
      linear-gradient(135deg, rgb(255 255 255 / .16), rgb(255 255 255 / .04)),
      rgb(15 23 42 / .42);
    box-shadow:
      inset 0 1px 0 rgb(255 255 255 / .22),
      0 18px 60px rgb(0 0 0 / .42);
  }
}

@media (prefers-reduced-transparency: reduce) {
  .liquid-glass-web-approx {
    background: rgb(255 255 255 / .96);
    backdrop-filter: none;
    -webkit-backdrop-filter: none;
  }
}
```

`prefers-reduced-transparency` has uneven browser support; test it. Always provide enough contrast even without blur.

---
Source: Leonxlnx/taste-skill v2 (design-taste-frontend), MIT licensed, 64.9k GitHub stars, sponsored by the Vercel Open Source Program and others.

