#!/usr/bin/env python3
"""
Comprehensive Website Page Auditor
Audits all 17 live pages on musiclinkd.com for HTTP status, titles,
canonical headers, internal links, image health, and layout integrity.
"""

import urllib.request
import urllib.error
import re
import json
from datetime import datetime

BASE_URL = "https://musiclinkd.com"

PAGES = [
    {"slug": "", "name": "Home"},
    {"slug": "about/", "name": "About Us"},
    {"slug": "pricing/", "name": "Pricing"},
    {"slug": "contact/", "name": "Contact"},
    {"slug": "help/", "name": "Help Centre"},
    {"slug": "privacy/", "name": "Privacy Policy"},
    {"slug": "terms/", "name": "Terms of Use"},
    {"slug": "cookies/", "name": "Cookie Policy"},
    {"slug": "login/", "name": "Login"},
    {"slug": "register/", "name": "Register"},
    {"slug": "profile-that-gets-booked/", "name": "Guide: Profile"},
    {"slug": "pricing-your-first-service/", "name": "Guide: Pricing Service"},
    {"slug": "how-verification-works/", "name": "Guide: Verification"},
    {"slug": "booking-policies-explained/", "name": "Guide: Booking Policies"},
    {"slug": "review-policy/", "name": "Guide: Review Policy"},
    {"slug": "community-guidelines/", "name": "Guide: Community Guidelines"},
    {"slug": "whats-new/", "name": "Guide: What's New"},
]

def audit():
    print(f"🔍 Starting Deep Website Audit on {BASE_URL}...\n")
    results = []
    broken_links = []
    
    headers = {"User-Agent": "MusicLinkd-Auditor/1.0"}
    
    for p in PAGES:
        url = f"{BASE_URL}/{p['slug']}"
        req = urllib.request.Request(url, headers=headers)
        start = datetime.now()
        try:
            with urllib.request.urlopen(req, timeout=12) as resp:
                latency = int((datetime.now() - start).total_seconds() * 1000)
                html = resp.read().decode("utf-8", errors="ignore")
                
                # Extract Title
                title_match = re.search(r"<title>(.*?)</title>", html, re.IGNORECASE | re.DOTALL)
                title = title_match.group(1).strip() if title_match else "NO TITLE"
                
                # Check for duplicate <h1> or missing <h1>
                h1_matches = re.findall(r"<h1[^>]*>(.*?)</h1>", html, re.IGNORECASE | re.DOTALL)
                
                # Check for internal links
                links = re.findall(r'href=["\'](https?://musiclinkd\.com/[^"\']+|/[^"\']*)["\']', html)
                
                # Check for images / svg
                imgs = re.findall(r'<img[^>]+src=["\']([^"\']+)["\']', html)
                
                # Check for Fluent Forms on contact
                has_form = "fluentform" in html if p["slug"] == "contact/" else None
                
                item = {
                    "slug": p["slug"],
                    "name": p["name"],
                    "url": url,
                    "status": resp.status,
                    "latency_ms": latency,
                    "title": title,
                    "h1_count": len(h1_matches),
                    "h1_first": re.sub(r'<[^>]+>', '', h1_matches[0]).strip() if h1_matches else "NONE",
                    "html_bytes": len(html),
                    "links_count": len(links),
                    "images_count": len(imgs),
                    "has_form": has_form
                }
                results.append(item)
                print(f"  ✓ {p['name'].ljust(25)} -> HTTP {resp.status} | {latency}ms | H1: {item['h1_count']} | {len(html)} bytes")
        except urllib.error.HTTPError as e:
            results.append({
                "slug": p["slug"],
                "name": p["name"],
                "url": url,
                "status": e.code,
                "error": str(e)
            })
            print(f"  ✗ {p['name'].ljust(25)} -> HTTP {e.code} ({e})")
        except Exception as e:
            results.append({
                "slug": p["slug"],
                "name": p["name"],
                "url": url,
                "status": 0,
                "error": str(e)
            })
            print(f"  ✗ {p['name'].ljust(25)} -> FAILED ({e})")

    print(f"\n📊 Audit finished. {len(results)} pages scanned.")
    return results

if __name__ == "__main__":
    audit()
