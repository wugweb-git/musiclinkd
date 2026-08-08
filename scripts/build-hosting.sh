#!/usr/bin/env bash
# Builds admin/ (Next.js static export) and merges it with website/ into
# dist/ — the single directory Firebase Hosting actually deploys. One
# Hosting site, two custom domains (musiclinkd.com, admin.musiclinkd.com)
# both attached to it, both serving this same merged tree.
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

echo "==> Merging website/ + admin/out/ into dist/"
rm -rf dist
mkdir -p dist
cp -r website/. dist/
rm -f dist/README.md
rsync -a --exclude='index.html' --exclude='index.txt' --exclude='404.html' admin/out/ dist/

echo "==> Including guide/ into dist/guide/ + root copies"
mkdir -p dist/guide
cp -r guide/. dist/guide/
# Root-level copies for direct access
cp guide/merge3.html dist/merge3.html
cp guide/merge2.html dist/merge2.html
cp guide/merge.html dist/merge1.html
cp guide/styleguide.html dist/styleguide.html
echo "==> dist/ ready ($(find dist -type f | wc -l | tr -d ' ') files)"
