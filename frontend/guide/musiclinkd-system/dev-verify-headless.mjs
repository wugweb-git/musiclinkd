// Headless functional check for musiclinkd-system, used because this build
// environment has no GUI browser. Renders the real production bundle.js in
// jsdom, clicks through every tab (+ the token taxonomy sub-nav), toggles
// dark mode, and reports console/window errors plus any literal "undefined"
// leaking into rendered output.
//
// Requires `npm install jsdom --no-save` first (jsdom isn't a project
// dependency — it's a dev-only verification tool, kept out of package.json
// on purpose so it doesn't ship with the app).
//
// Known limitation: jsdom doesn't implement requestAnimationFrame, which the
// real Masonry component (Blocks tab) calls internally. Polyfilled below so
// this script can still exercise that tab — this is a jsdom gap, not a bug
// in the app; every real browser implements requestAnimationFrame natively.
//
// Run: node dev-verify-headless.mjs   (after `npm run build`)

import { JSDOM } from "jsdom";
import fs from "fs";

const html = fs.readFileSync("index.html", "utf8");
const dom = new JSDOM(html, {
  runScripts: "dangerously",
  resources: "usable",
  url: "file://" + process.cwd() + "/index.html",
});
const { window } = dom;
window.requestAnimationFrame = (cb) => setTimeout(cb, 16);
window.cancelAnimationFrame = (id) => clearTimeout(id);

const errors = [];
window.console.error = (...args) => errors.push(args.join(" ").slice(0, 300));
window.addEventListener("error", (e) => errors.push("window error: " + (e.error?.message || e.message)));

const bundle = fs.readFileSync("bundle.js", "utf8");
const scriptEl = window.document.createElement("script");
scriptEl.textContent = bundle;
window.document.body.appendChild(scriptEl);
await new Promise((r) => setTimeout(r, 600));

function assert(label, cond) {
  console.log(`${cond ? "OK  " : "FAIL"} — ${label}`);
}

for (const label of ["Components", "Blocks", "Foundations", "Design Tokens", "Icons", "Brand Kit"]) {
  const btn = [...window.document.querySelectorAll("button.ml-tab-btn")].find((b) => b.textContent.includes(label));
  btn.click();
  await new Promise((r) => setTimeout(r, 150));
  const main = window.document.querySelector("main.ml-main");
  assert(`Tab "${label}" renders`, !!main && main.innerHTML.length > 0);
  assert(`Tab "${label}" has no literal <undefined> tag`, !/<undefined/.test(main.innerHTML));
}

// Token taxonomy: all 8 categories
const tokensBtn = [...window.document.querySelectorAll("button.ml-tab-btn")].find((b) => b.textContent.includes("Design Tokens"));
tokensBtn.click();
await new Promise((r) => setTimeout(r, 150));
for (const num of ["01", "02", "03", "04", "05", "06", "07", "08"]) {
  const btn = [...window.document.querySelectorAll("button.ml-token-cat-btn")].find((b) => b.textContent.trim().startsWith(num));
  assert(`Token category ${num} button exists`, !!btn);
  if (btn) {
    btn.click();
    await new Promise((r) => setTimeout(r, 60));
  }
}

// Blocks sub-nav: Composed patterns / Admin widget directory / Not built
const blocksBtn = [...window.document.querySelectorAll("button.ml-tab-btn")].find((b) => b.textContent.includes("Blocks"));
blocksBtn.click();
await new Promise((r) => setTimeout(r, 150));
for (const sub of ["Composed patterns", "Admin widget directory", "Not built"]) {
  const btn = [...window.document.querySelectorAll("button.ml-token-cat-btn")].find((b) => b.textContent.trim() === sub);
  assert(`Blocks sub-tab "${sub}" button exists`, !!btn);
  if (btn) {
    btn.click();
    await new Promise((r) => setTimeout(r, 100));
    const main = window.document.querySelector("main.ml-main");
    assert(`Blocks sub-tab "${sub}" renders`, !!main && main.innerHTML.length > 0);
  }
}
const notBuiltMain = window.document.querySelector("main.ml-main");
assert('Not built sub-tab lists "Breadcrumb"', notBuiltMain.innerHTML.includes("Breadcrumb"));
assert('Not built sub-tab lists "M3 Navigation Bar"', notBuiltMain.innerHTML.includes("M3 Navigation Bar"));

// Dark mode toggle + spot-check
const themeSwitch = window.document.querySelector("#theme-switch");
themeSwitch.click();
await new Promise((r) => setTimeout(r, 150));
assert("Dark mode toggles data-theme attribute", window.document.documentElement.getAttribute("data-theme") === "dark");

console.log(`\n${errors.length} console/window error(s) captured:`);
errors.forEach((e) => console.log(" -", e));
