#!/usr/bin/env python3
"""
MusicLinkd Content & Ingestion Sync Worker
-----------------------------------------
Automated scheduled worker to audit, sync, and log the state of published
WordPress CMS pages, Help Centre guides, and legal pages against the REST API.

Supports both unauthenticated public REST inspection and authenticated
REST operations using the 'ingest' service user.
"""

import os
import sys
import json
import base64
import urllib.request
import urllib.error
from datetime import datetime, timezone

WP_BASE_URL = os.environ.get("WP_BASE_URL", "https://musiclinkd.com")
INGEST_USER = os.environ.get("WP_INGEST_USER", "ingest")
INGEST_PASS = os.environ.get("WP_INGEST_PASS", "")

CANONICAL_SLUGS = [
    "help",
    "contact",
    "about",
    "pricing",
    "privacy",
    "terms",
    "cookies",
    "community-guidelines",
    "review-policy",
    "booking-policies-explained",
    "how-verification-works",
    "pricing-your-first-service",
    "profile-that-gets-booked",
    "whats-new",
]

def make_request(url: str, auth_user: str = "", auth_pass: str = ""):
    req = urllib.request.Request(
        url,
        headers={
            "User-Agent": "MusicLinkd-SyncWorker/1.0 (+https://musiclinkd.com)",
            "Accept": "application/json",
        }
    )
    if auth_user and auth_pass:
        token = base64.b64encode(f"{auth_user}:{auth_pass}".encode()).decode()
        req.add_header("Authorization", f"Basic {token}")
    
    start_time = datetime.now()
    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            elapsed_ms = int((datetime.now() - start_time).total_seconds() * 1000)
            data = json.loads(resp.read().decode("utf-8"))
            return {
                "status": resp.status,
                "elapsed_ms": elapsed_ms,
                "data": data,
                "error": None
            }
    except urllib.error.HTTPError as e:
        elapsed_ms = int((datetime.now() - start_time).total_seconds() * 1000)
        return {
            "status": e.code,
            "elapsed_ms": elapsed_ms,
            "data": None,
            "error": str(e)
        }
    except Exception as e:
        elapsed_ms = int((datetime.now() - start_time).total_seconds() * 1000)
        return {
            "status": 0,
            "elapsed_ms": elapsed_ms,
            "data": None,
            "error": str(e)
        }

def run_sync_audit():
    print(f"🚀 [MusicLinkd Worker] Starting content sync audit on {WP_BASE_URL}...")
    timestamp = datetime.now(timezone.utc).isoformat()
    
    results = {
        "timestamp": timestamp,
        "base_url": WP_BASE_URL,
        "authenticated_as": INGEST_USER if INGEST_PASS else "public_guest",
        "total_checked": len(CANONICAL_SLUGS),
        "successful": 0,
        "failed": 0,
        "pages": {}
    }
    
    for slug in CANONICAL_SLUGS:
        endpoint = f"{WP_BASE_URL}/wp-json/wp/v2/pages?slug={slug}"
        resp = make_request(endpoint, INGEST_USER, INGEST_PASS)
        
        status_code = resp["status"]
        elapsed_ms = resp["elapsed_ms"]
        
        if status_code == 200 and resp["data"] and len(resp["data"]) > 0:
            page_obj = resp["data"][0]
            title = page_obj.get("title", {}).get("rendered", "")
            raw_content = page_obj.get("content", {}).get("rendered", "")
            page_id = page_obj.get("id")
            
            results["successful"] += 1
            results["pages"][slug] = {
                "id": page_id,
                "title": title,
                "status": "OK",
                "http_status": 200,
                "latency_ms": elapsed_ms,
                "content_bytes": len(raw_content),
                "modified_gmt": page_obj.get("modified_gmt", ""),
                "has_gutenberg_blocks": "wp-block" in raw_content or "<!-- wp:" in raw_content or len(raw_content) > 500
            }
            print(f"  ✓ {slug.ljust(30)} -> ID {page_id} | {elapsed_ms}ms | {len(raw_content)} bytes")
        else:
            results["failed"] += 1
            results["pages"][slug] = {
                "status": "FAILED",
                "http_status": status_code,
                "latency_ms": elapsed_ms,
                "error": resp["error"] or "Page not found in REST response"
            }
            print(f"  ✗ {slug.ljust(30)} -> HTTP {status_code} ({resp['error'] or 'Missing'})")
            
    # Persist log to docs/logs/sync-health.json
    log_dir = os.path.join(os.path.dirname(os.path.dirname(__file__)), "docs", "logs")
    os.makedirs(log_dir, exist_ok=True)
    log_file = os.path.join(log_dir, "sync-health.json")
    
    with open(log_file, "w", encoding="utf-8") as f:
        json.dump(results, f, indent=2)
    print(f"\n📊 Audit log written to {log_file}")
    
    # Write to GitHub Step Summary if running in Actions
    summary_path = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary_path:
        with open(summary_path, "a", encoding="utf-8") as f:
            f.write("## 🔄 MusicLinkd Content & Ingestion Worker Summary\n\n")
            f.write(f"- **Timestamp:** `{timestamp}`\n")
            f.write(f"- **Target:** `{WP_BASE_URL}`\n")
            f.write(f"- **Verified Pages:** {results['successful']} / {results['total_checked']}\n")
            f.write(f"- **Failed:** {results['failed']}\n\n")
            f.write("| Slug | Page ID | Latency | Status |\n")
            f.write("| :--- | :--- | :--- | :--- |\n")
            for slug, info in results["pages"].items():
                icon = "🟢" if info.get("status") == "OK" else "🔴"
                page_id = info.get("id", "—")
                lat = f"{info.get('latency_ms', 0)}ms"
                f.write(f"| `{slug}` | {page_id} | {lat} | {icon} {info.get('status')} |\n")
                
    if results["failed"] > 0:
        print(f"⚠️ {results['failed']} pages failed health audit.")
        sys.exit(1)
    else:
        print("🎉 All canonical pages verified healthy and responsive!")

if __name__ == "__main__":
    run_sync_audit()
