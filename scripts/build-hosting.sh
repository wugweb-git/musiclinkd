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
# inspiration/ is 185 files / 15MB of which styleguide.html references exactly
# FOUR, ~104KB, as gear thumbnails. Rather than ship 15MB for 104KB, the four
# are derived from the markup here so the list cannot drift: add a fifth
# reference to the styleguide and it ships automatically.
INSPIRATION_KEEP=$(grep -ohE 'assets/img/inspiration/[a-zA-Z0-9]+\.(jpg|jpeg|png|webp)' \
  frontend/guide/styleguide.html frontend/website/*.html 2>/dev/null | sort -u)
INSPIRATION_RULES=()
if [ -n "$INSPIRATION_KEEP" ]; then
  INSPIRATION_RULES+=(--include='assets/img/inspiration/')
  while IFS= read -r f; do INSPIRATION_RULES+=(--include="$f"); done <<< "$INSPIRATION_KEEP"
fi
INSPIRATION_RULES+=(--exclude='assets/img/inspiration/*')
echo "    keeping $(printf '%s\n' "$INSPIRATION_KEEP" | grep -c . ) of $(ls frontend/website/assets/img/inspiration | wc -l | tr -d ' ') inspiration images"

rsync -a \
  --exclude='assets/img/1/' --exclude='assets/img/4/' \
  --exclude='assets/img/5/' --exclude='assets/img/6/' \
  "${INSPIRATION_RULES[@]}" \
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
#                                    actually inlines from
#   testdir.tmp/, widgets/           empty
#   skills/, docs/, *.md             markdown; belongs in the repo, not on a CDN
#   styleguide.html                  660KB, and /guide/styleguide 301s away from
#                                    it, so it could never be fetched
#
# musiclinkd-system/ is excluded ENTIRELY as of 2026-08-29. It was the React +
# Gestalt catalogue — 4.6MB shipped, including a 2.6MB Gestalt bundle and 524
# Pinterest tokens — and /guide/index.html now replaces it. Its 423 icons are
# still the icon library, but they are inlined at BUILD time by build-guide.mjs
# and the screen generators, so nothing needs them at runtime. The source stays
# in the repo; it just stops being deployed. Its URL 301s to /guide.
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
  --exclude='*.map' \
  --exclude='.DS_Store' \
  frontend/guide/ dist/guide/

echo "==> Copying webapp/ PWA product-reference build into dist/preview/"
mkdir -p dist/preview
cp -r webapp/. dist/preview/

echo "==> dist/ ready ($(find dist -type f | wc -l | tr -d ' ') files)"
