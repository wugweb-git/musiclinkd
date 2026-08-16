import { execSync } from "node:child_process";
import * as esbuild from "esbuild";

console.log("→ preparing assets (icons + real CSS)…");
execSync("node scripts/prepare-assets.mjs", { stdio: "inherit" });

console.log("→ bundling…");
await esbuild.build({
  entryPoints: ["src/main.tsx"],
  bundle: true,
  outfile: "bundle.js",
  format: "iife",
  platform: "browser",
  jsx: "automatic",
  loader: { ".json": "json", ".css": "text", ".svg": "text" },
  minify: false,
  sourcemap: true,
  logLevel: "info",
});

console.log("✓ Build complete. Open index.html in a browser.");
