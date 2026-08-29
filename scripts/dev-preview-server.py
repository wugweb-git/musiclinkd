#!/usr/bin/env python3
"""
dev-preview-server.py — serve the webapp preview and the guide at the SAME
paths Firebase Hosting serves them, without running a build.

Why this exists: every screen under webapp/ links its CSS and its next screen
with absolute /preview/... paths. That is deliberate and documented in
webapp/template/index.html — Firebase Hosting runs with cleanUrls:true and
trailingSlash:false, so a relative "bundle.js" on /preview/flows/x/y resolves
to /preview/flows/x/bundle.js and 404s. The cost of that fix is that the
screens cannot be opened over file:// or from a plain repo-root static server.

So this maps, in memory, with no copying:

    /preview/...  ->  webapp/...
    /guide/...    ->  frontend/guide/...
    /assets/...   ->  frontend/website/assets/...
    /...          ->  repo root

Same os.chdir-instead-of-getcwd trick as scripts/dev-static-server.py; see the
comment there for why the stdlib http.server CLI cannot run in this sandbox.

Run via the Browser pane ("preview" config in .claude/launch.json), not by
hand. Port 5501 so it can run alongside dev-static-server.py on 5500.
"""
import http.server
import os
import posixpath
import socketserver
from urllib.parse import unquote, urlparse

ROOT = "/Users/abcom/Documents/GitHub/musiclinkd"
PORT = 5501

# Longest prefix first so /preview wins before /.
MOUNTS = (
    ("/preview", "webapp"),
    ("/guide", "frontend/guide"),
    # the real photo library — deployed at the site root, so screens reference
    # /assets/img/... and it resolves in both dev and production
    ("/assets", "frontend/website/assets"),
)

os.chdir(ROOT)


class Handler(http.server.SimpleHTTPRequestHandler):
    def translate_path(self, path):
        path = unquote(urlparse(path).path)
        # normalise and refuse to escape ROOT
        parts = [p for p in posixpath.normpath(path).split("/") if p not in ("", ".", "..")]
        rel = "/".join(parts)

        for prefix, target in MOUNTS:
            p = prefix.lstrip("/")
            if rel == p or rel.startswith(p + "/"):
                rel = posixpath.join(target, rel[len(p):].lstrip("/"))
                break

        full = os.path.join(ROOT, rel)
        # directory -> index.html, matching Firebase's behaviour
        if os.path.isdir(full):
            full = os.path.join(full, "index.html")
        return full

    def end_headers(self):
        # never cache during design review — a stale token file would make a
        # visual diff meaningless
        self.send_header("Cache-Control", "no-store, must-revalidate")
        super().end_headers()

    def log_message(self, fmt, *args):
        pass


class Server(socketserver.TCPServer):
    allow_reuse_address = True
    daemon_threads = True


with Server(("", PORT), Handler) as httpd:
    print(f"preview server on http://localhost:{PORT}  (/preview -> webapp, /guide -> frontend/guide)")
    httpd.serve_forever()
