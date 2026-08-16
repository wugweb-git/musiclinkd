// Copies real assets from the vendored Gestalt clone (../gestalt) and from
// node_modules into this project so the build has everything it needs:
// icons as static files, and the two CSS files that actually style Gestalt
// components (tokens + component classes).

import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.join(__dirname, "..");
const vendoredIconsDir = path.join(root, "..", "gestalt", "packages", "gestalt", "src", "icons");
const publicIconsDir = path.join(root, "icons");
const dataDir = path.join(root, "src", "generated");

fs.mkdirSync(publicIconsDir, { recursive: true });
fs.mkdirSync(dataDir, { recursive: true });

function walkSvgs(dir, base = "") {
  const out = [];
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      out.push(...walkSvgs(full, path.join(base, entry.name)));
    } else if (entry.name.endsWith(".svg")) {
      out.push({ full, rel: path.join(base, entry.name) });
    }
  }
  return out;
}

const svgs = walkSvgs(vendoredIconsDir);
const iconNames = [];

for (const { full, rel } of svgs) {
  // Flatten into public/icons/<name>.svg — gestalt's <Icon icon="name" />
  // prop matches the top-level (non-compact) filename minus extension.
  const flatName = path.basename(rel, ".svg");
  const dest = path.join(publicIconsDir, `${flatName}${rel.includes("compact/") ? "-compact" : ""}.svg`);
  fs.copyFileSync(full, dest);
  iconNames.push(path.basename(dest, ".svg"));
}

iconNames.sort();
fs.writeFileSync(
  path.join(dataDir, "icons.json"),
  JSON.stringify(iconNames, null, 2)
);

// Copy the real, shipped CSS: design tokens (defines --color-*, --space-*
// etc. as CSS custom properties) and Gestalt's compiled component styles
// (the classes components actually use, referencing those custom
// properties via var()).
const cssFiles = [
  {
    from: path.join(root, "node_modules", "gestalt-design-tokens", "dist", "css", "classic", "variables.css"),
    to: path.join(root, "variables.css"),
  },
  {
    from: path.join(root, "node_modules", "gestalt", "dist", "gestalt.css"),
    to: path.join(root, "gestalt.css"),
  },
  {
    // BUG FIX: index.html links to "styles.css" at root, but this file was
    // never copied there — it stayed in src/ only, so the app shell (header,
    // nav, tab bar, grids) rendered unstyled. This is a straight file copy,
    // not a "real shipped asset" like the others above.
    from: path.join(root, "src", "styles.css"),
    to: path.join(root, "styles.css"),
  },
];

for (const { from, to } of cssFiles) {
  fs.copyFileSync(from, to);
}

console.log(`Prepared ${iconNames.length} icons and ${cssFiles.length} CSS files.`);
