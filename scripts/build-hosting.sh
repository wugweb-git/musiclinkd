#!/usr/bin/env bash
# Builds admin/ (Next.js static export) and merges it with frontend/website/
# into dist/ — the single directory Firebase Hosting actually deploys. One
# Hosting site, two custom domains (musiclinkd.com, admin.musiclinkd.com)
# both attached to it, both serving this same merged tree.
#
# frontend/website/ is the sole source of truth for the marketing site's
# pages and assets. merge.html/merge2.html live there still, but
# styleguide.html/merge3.html moved to frontend/guide/ (2026-08-12) — that's
# now the source-of-truth home for design-system material (tokens, icons,
# docs, and these two interactive references). They deploy to dist/guide/
# via the explicit copy step below; index.html/changelog link to
# /guide/styleguide.html accordingly. merge3.html's asset refs use
# site-root-absolute paths (/assets/img/...) so they resolve correctly from
# under /guide/ in the deployed tree.
#
# Why a merge instead of two Hosting sites: Firebase Hosting rewrites only
# match on URL path, not hostname — there's no way to serve different
# content per domain from one site. Two sites would solve that but the
# project wants exactly one. A flat merge works because admin's route names
# (dashboard, users, studios, ...) don't collide with website's page names
# (/, about, pricing, ...) — verified by hand, re-check if either side adds
# a new top-level route.
#
# Two real collisions DO exist: both sides have an index.html (root "/")
# and a 404.html. website/'s versions win both — it's the marketing
# homepage and the branded error page; admin's own root page (an
# auth-redirect shell) and generated 404 are dropped, since visitors reach
# the app directly at /login or /dashboard, not through admin's "/".
set -euo pipefail
cd "$(dirname "$0")/.."

echo "==> Building admin/"
(cd admin && npm run build)

# Fail the deploy if any generated artefact is stale against tokens.json.
# All three of these are generated from the same source and a deploy is the
# last moment they can be caught disagreeing — ml-tokens.css ships to the
# browser, MusicLinkdTokens.kt ships to the Android repo, and index.html is
# the page that documents both. Checking rather than regenerating is
# deliberate: a silent regenerate during deploy would ship a change nobody
# reviewed.
echo "==> Verifying generated artefacts are current"
node scripts/build-tokens.mjs --check
node scripts/build-handoff.mjs --check

echo "==> Generating changelog page from CHANGELOG.md"
node scripts/generate-changelog.mjs

echo "==> Merging frontend/website/ + admin/out/ into dist/"
rm -rf dist
mkdir -p dist
# assets/img/{1,4,5,6} are 448 Behance CDN captures, Spotify/Shazam/Tinder
# UI screenshots and app-store chrome kept as design reference. Nothing in the
# deployed tree references them and they were 279MB of a 326MB deploy, served
# publicly. assets/img.zip is a 300MB local archive of that same image folder,
# referenced by no page — it was being served as a web asset.
#
# The guide and the website reference photography from assets/img/{4,5,6,
# inspiration}. Those folders together are ~290MB and the pages use a small
# fraction, so the ship-list is DERIVED from the generated markup rather than
# maintained by hand — add a reference anywhere and it ships on the next build,
# remove one and it stops. This replaced a hand-kept list that had already
# drifted once.
# webapp/ screens are scanned too: they ship to dist/preview and reference the
# same library through /assets/img/... . Leaving them out meant a screen could
# pass every audit, look right in dev (where the server maps /assets straight
# at the source tree) and then 404 in production because the ship-list never
# saw the reference. Caught on marketplace_browse, where 3 of 9 photos and 6
# of the preview's 23 distinct images were being dropped from the deploy.
MEDIA_SRC=(frontend/guide/index.html frontend/guide/styleguide.html frontend/website/*.html
           webapp/index.html webapp/flows/*/*/index.html)
MEDIA_KEEP=$(grep -ohE 'assets/img/(1|4|5|6|inspiration)/[^"'"'"')? ]+\.(jpg|jpeg|png|webp|gif|mp4|webm)' \
  "${MEDIA_SRC[@]}" 2>/dev/null | sort -u)
MEDIA_RULES=()
for d in 1 4 5 6 inspiration; do MEDIA_RULES+=(--include="assets/img/$d/"); done
while IFS= read -r f; do [ -n "$f" ] && MEDIA_RULES+=(--include="$f"); done <<< "$MEDIA_KEEP"
for d in 1 4 5 6 inspiration; do MEDIA_RULES+=(--exclude="assets/img/$d/*"); done
KEEP_N=$(printf '%s\n' "$MEDIA_KEEP" | grep -c . || true)
KEEP_BYTES=$(printf '%s\n' "$MEDIA_KEEP" | while IFS= read -r f; do
  [ -n "$f" ] && [ -f "frontend/website/$f" ] && stat -f%z "frontend/website/$f" 2>/dev/null || true
done | awk '{t+=$1} END {printf "%.1f", t/1048576}')
echo "    keeping $KEEP_N referenced media files (${KEEP_BYTES}MB) of $(find frontend/website/assets/img/{1,4,5,6,inspiration} -type f 2>/dev/null | wc -l | tr -d ' ')"

rsync -a \
  "${MEDIA_RULES[@]}" \
  --exclude='*.zip' \
  --exclude='.DS_Store' \
  frontend/website/ dist/
rm -f dist/README.md "dist/index copy.html"
rsync -a --exclude='index.html' --exclude='index.txt' --exclude='404.html' admin/out/ dist/

echo "==> Copying frontend/guide/ design-system reference into dist/guide/"
# rsync with excludes, not cp -r: a blanket copy shipped 180MB to Hosting.
#
# brandkit/ is excluded as of 2026-08-30: its content — the two brand colours,
# the Inter Tight / Inter weights, the logo inventory and the voice line — is
# now generated into /guide#brand from brand/README.md and brand/assets/, so
# the standalone page was a second place the brand was defined. Its URL 301s.
#
# Also excluded, all of it reachable by nobody:
#   _Music Genres…Apple Music_files  a saved Apple Music web page, 5.8MB of
#                                    someone else's JS and fonts, referenced by
#                                    nothing and served publicly
#   icons/                           401 files duplicating musiclinkd-system/
#                                    icons/ (423), which is the set the build
#                                    actually inlines from. NOTE this exclude
#                                    is the directory named exactly "icons" —
#                                    icons-lucide/ is NOT matched and DOES ship,
#                                    which is required: see below.
#   testdir.tmp/, widgets/           empty
#   skills/, docs/, *.md             markdown; belongs in the repo, not on a CDN
#   styleguide.html                  660KB, and /guide/styleguide 301s away from
#                                    it, so it could never be fetched
#
#   _to_delete/, *.pptx,             added 2026-08-31. The guide folder is copied
#   *.tgz, *.zip                     wholesale, so anything dropped into it is
#                                    published. A 20MB internal deck
#                                    (MusicLinkd.pptx) and _to_delete/
#                                    preview_bundle.tgz were both live and
#                                    publicly downloadable at musiclinkd.com
#                                    before this exclude. Source files stay in
#                                    the repo; they just stop being a CDN asset.
#
# musiclinkd-system/ is excluded ENTIRELY as of 2026-08-29. It was the React +
# Gestalt catalogue — 4.6MB shipped, including a 2.6MB Gestalt bundle and 524
# Pinterest tokens — and /guide/index.html now replaces it. Its 423 icons are
# still the icon library, but they are inlined at BUILD time by build-guide.mjs
# and the screen generators, so nothing needs them at runtime. The source stays
# in the repo; it just stops being deployed. Its URL 301s to /guide.
#
# icons-lucide/ IS deployed, and is the one icon directory that must be — added
# 2026-08-31 as the system's second icon set. Unlike the Gestalt glyphs it is
# NOT inlined: /guide renders 2006 of them through CSS mask urls, because
# inlining that many would add ~680KB to a page that is already ~930KB. So the
# .svg files are fetched at runtime and the deploy would 404 every one of them
# if this directory were excluded. Its LICENSE (ISC) ships with it deliberately;
# the README.md is dropped by the *.md exclude, like every other markdown here.
mkdir -p dist/guide
rsync -a \
  --exclude='node_modules/' \
  --exclude='gestalt/' \
  --exclude='musiclinkd-system/' \
  --exclude='brandkit/' \
  --exclude='_Music*' \
  --exclude='icons/' \
  --exclude='testdir.tmp/' \
  --exclude='widgets/' \
  --exclude='skills/' \
  --exclude='docs/' \
  --exclude='*.md' \
  --exclude='styleguide.html' \
  --exclude='_to_delete/' \
  --exclude='*.pptx' \
  --exclude='*.tgz' \
  --exclude='*.zip' \
  --exclude='*.map' \
  --exclude='.DS_Store' \
  frontend/guide/ dist/guide/

echo "==> Copying webapp/ PWA product-reference build into dist/preview/"
mkdir -p dist/preview
cp -r webapp/. dist/preview/

# Stamp the service worker's cache name with a hash of the shell it caches.
# It used to carry a hand-bumped "v1" whose own comment said to bump it when
# shared/* changed; nobody ever did, so returning visitors were served the
# original app-shell.js forever and every deploy looked like a no-op. Deriving
# it removes the step there was to forget: change the shell and the cache name
# changes with it, so old caches are dropped on activate.
# Hash EXACTLY the files service-worker.js lists in SHELL_FILES. It used to
# hash three of the five — shared/icon.svg and offline.html were cached but not
# hashed, so changing either alone left the cache name identical and the old
# copy in place. Network-first limits the damage to the offline fallback, but a
# cache key that does not cover what it caches is the same class of bug as the
# hand-bumped v1 this replaced.
SW_ID=$(cat dist/preview/manifest.json \
        dist/preview/shared/app-shell.css \
        dist/preview/shared/app-shell.js \
        dist/preview/shared/icon.svg \
        dist/preview/offline.html 2>/dev/null | shasum -a 256 | cut -c1-12)
sed -i '' "s/__BUILD_ID__/$SW_ID/" dist/preview/service-worker.js
grep -q "__BUILD_ID__" dist/preview/service-worker.js && \
  { echo "    ERROR: service-worker.js BUILD_ID not stamped"; exit 1; }
echo "    service worker cache: musiclinkd-shell-$SW_ID"

echo "==> dist/ ready ($(find dist -type f | wc -l | tr -d ' ') files)"
