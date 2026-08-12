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
cp -r frontend/website/. dist/
rm -f dist/README.md "dist/index copy.html"
rsync -a --exclude='index.html' --exclude='index.txt' --exclude='404.html' admin/out/ dist/

echo "==> Copying frontend/guide/ design-system reference into dist/guide/"
mkdir -p dist/guide
cp -r frontend/guide/. dist/guide/

echo "==> Copying webapp/ PWA product-reference build into dist/preview/"
mkdir -p dist/preview
cp -r webapp/. dist/preview/

echo "==> dist/ ready ($(find dist -type f | wc -l | tr -d ' ') files)"
