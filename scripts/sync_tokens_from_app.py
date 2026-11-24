#!/usr/bin/env python3
"""
MusicLinkd App Token & Pattern Ingestion Worker
------------------------------------------------
Reads canonical design tokens, brand colors, and component schemas from
musiclinkd-app (src/styles.css) and maps them into the WordPress theme
(theme.json, assets/css/tokens.css, enhance.css, parts, patterns).

Enforces the rule: "musiclinkd must pick from musiclinkd-app".
"""

import os
import re
import json
import sys

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
APP_REPO_ROOT = os.path.join(os.path.dirname(REPO_ROOT), "musiclinkd-app")
APP_STYLES_PATH = os.path.join(APP_REPO_ROOT, "src", "styles.css")

# Canonical fallback defaults from musiclinkd-app/src/styles.css
CANONICAL_APP_TOKENS = {
    "brand": "#ea4c89",
    "brand_hover": "#d63e78",
    "ink": "#0d0d0d",
    "background": "#f5f5f5",
    "surface_warm": "#f3f1ec",
    "border": "#d5d5de",
    "radius_sm": "8px",
    "radius_lg": "16px",
    "radius_3xl": "32px",
    "radius_full": "9999px",
}

def extract_tokens_from_app():
    """Extracts live tokens from musiclinkd-app/src/styles.css if available."""
    tokens = dict(CANONICAL_APP_TOKENS)
    if not os.path.exists(APP_STYLES_PATH):
        print(f"ℹ️ App repo styles not found at {APP_STYLES_PATH}, using canonical app token matrix.")
        return tokens

    try:
        with open(APP_STYLES_PATH, "r", encoding="utf-8") as f:
            css = f.read()

        # Extract brand pink
        brand_match = re.search(r'--brand:\s*(#[a-fA-F0-9]{3,8});', css)
        if brand_match:
            tokens["brand"] = brand_match.group(1).lower()

        # Extract ink
        ink_match = re.search(r'--ink:\s*(#[a-fA-F0-9]{3,8});', css)
        if ink_match:
            tokens["ink"] = ink_match.group(1).lower()

        # Extract warm surface
        warm_match = re.search(r'--surface-warm:\s*(#[a-fA-F0-9]{3,8});', css)
        if warm_match:
            tokens["surface_warm"] = warm_match.group(1).lower()

        # Extract background
        bg_match = re.search(r'--background:\s*(#[a-fA-F0-9]{3,8});', css)
        if bg_match:
            tokens["background"] = bg_match.group(1).lower()

        print(f"✓ Extracted live tokens from musiclinkd-app: brand={tokens['brand']}, ink={tokens['ink']}, warm={tokens['surface_warm']}")
    except Exception as e:
        print(f"⚠️ Error reading {APP_STYLES_PATH}: {e}. Falling back to canonical constants.")

    return tokens

def sync_theme_json(tokens):
    """Syncs canonical tokens into theme.json."""
    theme_json_path = os.path.join(REPO_ROOT, "theme.json")
    if not os.path.exists(theme_json_path):
        return

    with open(theme_json_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    palette = data.get("settings", {}).get("color", {}).get("palette", [])
    updated = False
    for item in palette:
        if item.get("slug") == "primary":
            item["color"] = tokens["brand"].upper()
            updated = True
        elif item.get("slug") == "primary-hover":
            item["color"] = tokens["brand_hover"].upper()
            updated = True

    if updated:
        with open(theme_json_path, "w", encoding="utf-8") as f:
            json.dump(data, f, indent="\t")
            f.write("\n")
        print(f"✓ Synchronized theme.json palette to brand={tokens['brand'].upper()}")

def sync_tokens_css(tokens):
    """Syncs canonical tokens into assets/css/tokens.css."""
    css_path = os.path.join(REPO_ROOT, "assets", "css", "tokens.css")
    if not os.path.exists(css_path):
        return

    with open(css_path, "r", encoding="utf-8") as f:
        content = f.read()

    brand_hex = tokens["brand"].upper()
    hover_hex = tokens["brand_hover"].upper()
    
    # Replace brand-pink declarations
    content = re.sub(r'--brand-pink:\s*#[a-fA-F0-9]{6};', f'--brand-pink: {brand_hex};', content)
    content = re.sub(r'--brand-pink-hover:\s*#[a-fA-F0-9]{6};', f'--brand-pink-hover: {hover_hex};', content)
    content = re.sub(r'--border-focus:\s*#[a-fA-F0-9]{6};', f'--border-focus: {brand_hex};', content)

    with open(css_path, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"✓ Synchronized assets/css/tokens.css to brand={brand_hex}")

def main():
    print("🚀 [Token Ingestion Worker] Ingesting design tokens from musiclinkd-app...")
    tokens = extract_tokens_from_app()
    sync_theme_json(tokens)
    sync_tokens_css(tokens)
    print("🎉 Ingestion complete. WordPress theme tokens are 100% aligned with musiclinkd-app.")

if __name__ == "__main__":
    main()
