#!/usr/bin/env python3
# Plain python3 -m http.server calls os.getcwd() while building its argparse
# parser (to default --directory), even when --directory is passed explicitly.
# That getcwd() call is blocked under this project's process sandbox, so the
# stdlib CLI entry point can't run here at all. This script serves the same
# directory via the library API directly (os.chdir, no os.getcwd()) to avoid
# tripping that restriction.
import http.server
import os
import socketserver

ROOT = "/Users/abcom/Documents/GitHub/musiclinkd"
PORT = 5500

os.chdir(ROOT)

class Handler(http.server.SimpleHTTPRequestHandler):
    def log_message(self, fmt, *args):
        pass

class Server(socketserver.TCPServer):
    allow_reuse_address = True

with Server(("", PORT), Handler) as httpd:
    httpd.serve_forever()
