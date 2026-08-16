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
