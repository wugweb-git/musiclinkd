#!/usr/bin/env python3
"""
MusicLinkd App Profile Ingestion Worker
---------------------------------------
Pulls all verified profiles (Talent & Businesses) from musiclinkd-app
(Supabase `profiles` table) and ingests them into the WordPress Custom Post Type
(`profile`) with full SCF (Secure Custom Fields) and taxonomy mapping, plus
synchronization into FluentCRM.
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
INGEST_PASS = os.environ.get("WP_INGEST_PASS", "s7jE IM7W dPDg Q9H3 MW5y KtXR")

SUPABASE_URL = os.environ.get("SUPABASE_URL", "https://gsqeymhtyzepwcnorjun.supabase.co")
SUPABASE_KEY = os.environ.get("SUPABASE_KEY", "sb_publishable_ytTnZ4VL6Ms_QuwsSuQvWg_uMuLhvnr")

def fetch_supabase_profiles():
    """Fetches all profiles from Supabase REST API."""
    endpoint = f"{SUPABASE_URL}/rest/v1/profiles?select=*"
    print(f"📥 Querying Supabase profiles from {SUPABASE_URL}...")
    req = urllib.request.Request(
        endpoint,
        headers={
            "apikey": SUPABASE_KEY,
            "Authorization": f"Bearer {SUPABASE_KEY}",
            "User-Agent": "MusicLinkd-ProfileSync/1.0",
        }
    )
    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            profiles = json.loads(resp.read().decode("utf-8"))
            print(f"✓ Retrieved {len(profiles)} profiles from Supabase.")
            return profiles
    except Exception as e:
        print(f"❌ Failed to fetch Supabase profiles: {e}")
        return []

def send_batch_to_wordpress(batch):
    """Sends a batch of profiles to WordPress REST ingestion endpoint."""
    endpoint = f"{WP_BASE_URL}/wp-json/musiclinkd/v1/ingest-profiles"
    token = base64.b64encode(f"{INGEST_USER}:{INGEST_PASS}".encode()).decode()
    payload = json.dumps({"profiles": batch}).encode("utf-8")

    req = urllib.request.Request(
        endpoint,
        data=payload,
        headers={
            "Content-Type": "application/json",
            "Authorization": f"Basic {token}",
            "User-Agent": "MusicLinkd-ProfileSync/1.0",
        },
        method="POST"
    )

    try:
        with urllib.request.urlopen(req, timeout=30) as resp:
            data = json.loads(resp.read().decode("utf-8"))
            return data.get("stats", {})
    except urllib.error.HTTPError as e:
        err_msg = e.read().decode("utf-8")
        print(f"⚠️ WordPress HTTP {e.code}: {err_msg}")
        return {"error": str(e), "details": err_msg}
    except Exception as e:
        print(f"⚠️ Ingestion error: {e}")
        return {"error": str(e)}

def fetch_supabase_taxonomies():
    """Fetches master taxonomy_options (person_types, genres, cities, instruments) from Supabase."""
    endpoint = f"{SUPABASE_URL}/rest/v1/taxonomy_options?category_key=in.(person_types,genres,cities,instruments)&select=category_key,slug,label"
    req = urllib.request.Request(
        endpoint,
        headers={
            "apikey": SUPABASE_KEY,
            "Authorization": f"Bearer {SUPABASE_KEY}",
            "User-Agent": "MusicLinkd-ProfileSync/1.0",
        }
    )
    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            options = json.loads(resp.read().decode("utf-8"))
            print(f"✓ Retrieved {len(options)} master taxonomy options from Supabase.")
            return options
    except Exception as e:
        print(f"⚠️ Failed to fetch master taxonomies: {e}")
        return []

def sync_master_taxonomies():
    """Seeds all master taxonomy terms into WordPress."""
    options = fetch_supabase_taxonomies()
    if not options:
        return
    endpoint = f"{WP_BASE_URL}/wp-json/musiclinkd/v1/ingest-taxonomies"
    token = base64.b64encode(f"{INGEST_USER}:{INGEST_PASS}".encode()).decode()
    payload = json.dumps({"options": options}).encode("utf-8")
    req = urllib.request.Request(
        endpoint,
        data=payload,
        headers={
            "Content-Type": "application/json",
            "Authorization": f"Basic {token}",
            "User-Agent": "MusicLinkd-ProfileSync/1.0",
        },
        method="POST"
    )
    try:
        with urllib.request.urlopen(req, timeout=30) as resp:
            res = json.loads(resp.read().decode("utf-8"))
            print(f"✓ Master Taxonomies synced: +{res.get('created', 0)} created, {res.get('existing', 0)} existing.")
    except Exception as e:
        print(f"⚠️ Master taxonomy sync error: {e}")

def run_profile_sync():
    print("🚀 [Profile Ingestion Worker] Starting Supabase -> WordPress SCF sync...")
    start_time = datetime.now()
    sync_master_taxonomies()
    profiles = fetch_supabase_profiles()

    if not profiles:
        print("⚠️ No profiles found or failed to fetch. Aborting.")
        sys.exit(1)

    batch_size = 25
    totals = {
        "total": len(profiles),
        "created": 0,
        "updated": 0,
        "fluentcrm_synced": 0,
        "errors": [],
    }

    print(f"📦 Ingesting {len(profiles)} profiles in batches of {batch_size}...")

    for i in range(0, len(profiles), batch_size):
        batch = profiles[i:i + batch_size]
        batch_num = (i // batch_size) + 1
        total_batches = (len(profiles) + batch_size - 1) // batch_size
        print(f"  Uploading batch {batch_num}/{total_batches} ({len(batch)} profiles)...")

        stats = send_batch_to_wordpress(batch)
        if "error" not in stats:
            totals["created"] += stats.get("created", 0)
            totals["updated"] += stats.get("updated", 0)
            totals["fluentcrm_synced"] += stats.get("fluentcrm_synced", 0)
            if stats.get("errors"):
                totals["errors"].extend(stats.get("errors"))
            print(f"    ✓ Batch {batch_num} done: +{stats.get('created', 0)} created, ~{stats.get('updated', 0)} updated.")
        else:
            print(f"    ✗ Batch {batch_num} failed: {stats.get('error')}")

    elapsed = (datetime.now() - start_time).total_seconds()
    print("\n🎉 Profile Ingestion Complete!")
    print(f"⏱️ Time taken: {elapsed:.1f}s")
    print(f"📊 Summary:")
    print(f"   - Total Profiles Processed: {totals['total']}")
    print(f"   - New Profile Posts Created: {totals['created']}")
    print(f"   - Existing Posts Updated:   {totals['updated']}")
    print(f"   - FluentCRM Contacts Synced: {totals['fluentcrm_synced']}")
    if totals["errors"]:
        print(f"   - Errors encountered:       {len(totals['errors'])}")

    log_dir = os.path.join(os.path.dirname(os.path.dirname(__file__)), "docs", "logs")
    os.makedirs(log_dir, exist_ok=True)
    log_file = os.path.join(log_dir, "profiles-sync.json")

    log_data = {
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "elapsed_seconds": round(elapsed, 2),
        "target_url": WP_BASE_URL,
        "supabase_project": SUPABASE_URL,
        "stats": totals,
    }

    with open(log_file, "w", encoding="utf-8") as f:
        json.dump(log_data, f, indent=2)
    print(f"📄 Telemetry log saved to {log_file}")

if __name__ == "__main__":
    run_profile_sync()
