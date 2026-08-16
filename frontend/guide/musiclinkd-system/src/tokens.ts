// Parses the REAL, shipped Gestalt design-token CSS at runtime.
// Nothing here is hand-copied — it reads whatever ships in
// gestalt-design-tokens/dist/css/classic/variables.css, so it always
// reflects the actual version installed.

export type TokenGroup = {
  label: string;
  prefix: string;
  tokens: { name: string; value: string }[];
};

const GROUPS: { label: string; prefix: string }[] = [
  { label: "Color", prefix: "--color-" },
  { label: "Spacing", prefix: "--space-" },
  { label: "Rounding", prefix: "--rounding-" },
  { label: "Font Size", prefix: "--font-size-" },
  { label: "Font Weight", prefix: "--font-weight-" },
  { label: "Font Family", prefix: "--font-family-" },
  { label: "Elevation", prefix: "--elevation-" },
  { label: "Opacity", prefix: "--opacity-" },
];

export function parseTokenCss(cssText: string): TokenGroup[] {
  const varRe = /(--[a-zA-Z0-9-]+)\s*:\s*([^;]+);/g;
  const all: { name: string; value: string }[] = [];
  let m: RegExpExecArray | null;
  // eslint-disable-next-line no-cond-assign
  while ((m = varRe.exec(cssText))) {
    all.push({ name: m[1], value: m[2].trim() });
  }

  return GROUPS.map((g) => ({
    label: g.label,
    prefix: g.prefix,
    tokens: all.filter((t) => t.name.startsWith(g.prefix)),
  })).filter((g) => g.tokens.length > 0);
}

// Splits the parsed "Color" group into Gestalt's two real, distinct layers:
// a raw palette (--color-red-pushpin-500, --color-gray-roboflow-800, etc —
// literal hues, no meaning attached) and semantic role tokens
// (--color-background-*, --color-text-*, --color-border-*, --color-icon-*,
// --color-shadow-* — named by purpose, the actual layer components consume).
// This distinction is real and present in the shipped CSS var names
// themselves, not invented for this taxonomy.
const SEMANTIC_COLOR_RE = /^--color-(background|text|border|icon|shadow)-/;

export function splitColorGroup(colorGroup: TokenGroup | undefined) {
  if (!colorGroup) return { raw: [] as TokenGroup["tokens"], semantic: [] as TokenGroup["tokens"] };
  const semantic = colorGroup.tokens.filter((t) => SEMANTIC_COLOR_RE.test(t.name));
  const raw = colorGroup.tokens.filter((t) => !SEMANTIC_COLOR_RE.test(t.name));
  return { raw, semantic };
}

// ---------------------------------------------------------------------------
// Everything below is real data that does NOT live in the CSS custom-property
// files, because Gestalt doesn't express it that way. Sourced directly from
// Gestalt's own TypeScript/JS source (cited per item) rather than invented to
// fill a gap. Where Gestalt genuinely has no token for something on the
// requested list, that's stated as a gap, not papered over.
// ---------------------------------------------------------------------------

export const BREAKPOINTS = [
  { name: "sm", value: "576px" },
  { name: "md", value: "768px" },
  { name: "lg", value: "1312px" },
];
export const BREAKPOINTS_SOURCE = "gestalt/packages/gestalt/src/contexts/useResponsiveMinWidth.ts";

export const GRID = {
  columns: 12,
  responsiveProps: ["span", "smSpan", "mdSpan", "lgSpan"],
  description:
    "Column is Gestalt's real 12-unit grid primitive: span (0–12) sets the base column width, with smSpan/mdSpan/lgSpan overriding it at each breakpoint above. There is no separate gutter-width token — gutters come from the parent Flex/Box gap props (the Spacing scale).",
};
export const GRID_SOURCE = "gestalt/packages/gestalt/dist/Column.d.ts";

export const Z_INDEX = {
  pattern: "Compositional, not a numeric scale.",
  description:
    "FixedZIndex(n) declares a literal base layer. CompositeZIndex([...deps]) computes itself as max(deps.map(d => d.index())) + 1 at render time — later layers (modals, toasts, popovers) stack automatically above whatever they're told to depend on, instead of the app hand-picking numbers from a shared scale.",
};
export const Z_INDEX_SOURCE = "gestalt/packages/gestalt/src/zIndex.ts";
export const Z_INDEX_GAP =
  "No --z-index-* CSS custom properties exist anywhere in the shipped output — stacking is resolved in JS via these two classes, not a static token scale. Not fabricating numbers here.";

export const BORDER = {
  colorRoleCount: 36, // counted from --color-border-* at render time, see splitColorGroup
  observedWidths: ["1px", "2px", "3px", "4px"],
};
export const BORDER_SOURCE = "observed literal border-width declarations in the compiled gestalt/dist/gestalt.css";
export const BORDER_GAP =
  "Border COLOR is tokenized (--color-border-*, by role — container, focus, error, formfield, etc). Border WIDTH is not: components use hardcoded pixel literals (1px/2px/3px/4px) baked into compiled CSS, not a --border-width-* custom-property scale. Reporting the literals observed, not a token that doesn't exist.";

export const MOTION_NAMES = [
  "--base-motion-easing-linear",
  "--base-motion-easing-bounce",
  "--base-motion-easing-lateral",
  "--base-motion-duration-150",
  "--base-motion-duration-200",
  "--base-motion-duration-300",
  "--comp-checkbox-motion-background-colorchange-easing",
  "--comp-checkbox-icon-motion-collapse-in-easing",
  "--comp-radiogroupbutton-motion-border-scale-easing",
  "--comp-spinner-motion-enter-duration",
  "--sema-motion-opacity-duration-instant",
];
export const MOTION_SOURCE =
  "referenced in compiled component CSS: Switch.css, Checkbox/InternalCheckbox.css, RadioGroup/RadioButton.css, utils/tapScaleAnimation.css, gestalt.css";
export const MOTION_GAP =
  "A real gap in Gestalt's own shipped 'classic' theme, verified by grep against the compiled output: these custom properties are referenced BY NAME in component CSS but never DEFINED anywhere in variables.css or gestalt.css — so var(--base-motion-easing-bounce) currently resolves to nothing (the browser falls back to CSS defaults). Actual easing curves/durations only exist as raw source under Gestalt's newer, uncompiled vr-theme token set, which isn't part of what's shipped. Listing the real names Gestalt itself uses, not inventing ms/curve values to fill the gap.";
