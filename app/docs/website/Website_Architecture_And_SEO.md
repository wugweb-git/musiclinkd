# Website Architecture, Landing Pages & SEO Specification — `musiclinkd.com`

## Product Target Host
- **Website Target:** `musiclinkd.com`
- **Application Target:** `app.musiclinkd.com`
- **Admin Portal Target:** `admin.musiclinkd.com`
- **Official Contact Email:** `hello@musiclinkd.com`

---

## 🏛 Information Architecture (`musiclinkd.com`)

```
musiclinkd.com
├── Home Landing Page (Hero, Value Prop, Interactive Synergy Demo, Download CTA)
├── Features Page (Artist Matching, Studio Booking, Audio STEM Preview, Gear Marketplace)
├── Pricing Page (Free Tier, Pro Verified Tier, Business/Studio Partner Tier)
├── Audio Directory / Catalog (Public Artist Previews, Top Studios, Sample STEMs)
├── Download Page (Android APK / Google Play Link, Mobile Web Access to app.musiclinkd.com)
├── Resources & Blog (Music Collaboration Guides, Studio Setup Guides, Indie Music Marketing)
├── Legal Pages (Terms of Service, Privacy Policy, Copyright & DMCA Policy)
└── Support Portal (Help Center, Contact Form forwarding to hello@musiclinkd.com)
```

---

## 🔍 SEO Strategy & Structured Schema Markup (JSON-LD)

To optimize indexation for artists, audio gear, and recording studio reservations, `musiclinkd.com` injects structured JSON-LD schemas:

### 1. `MusicGroup` / Artist Schema Example
```json
{
  "@context": "https://schema.org",
  "@type": "MusicGroup",
  "name": "Rohan Sharma",
  "genre": ["Indie Pop", "Acoustic", "Bollywood Classic"],
  "location": {
    "@type": "Place",
    "name": "Mumbai, MH, India"
  },
  "url": "https://app.musiclinkd.com/artist/artist_1",
  "sameAs": [
    "https://open.spotify.com/artist/sample",
    "https://instagram.com/rohansharmamusic"
  ]
}
```

### 2. `LocalBusiness` / Recording Studio Schema Example
```json
{
  "@context": "https://schema.org",
  "@type": "LocalBusiness",
  "name": "Octave Sound Labs",
  "image": "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04",
  "priceRange": "₹2,200/hr - ₹4,500/hr",
  "address": {
    "@type": "PostalAddress",
    "streetAddress": "Bandra West",
    "addressLocality": "Mumbai",
    "addressRegion": "Maharashtra",
    "addressCountry": "IN"
  },
  "url": "https://app.musiclinkd.com/studios/studio_1"
}
```

---

## 💳 Pricing Structure Specification

| Feature | Free Visitor | Pro Verified Member | Studio / Business Partner |
| :--- | :---: | :---: | :---: |
| **Monthly Rate** | ₹0 | ₹499 / mo | ₹1,999 / mo |
| **Profile Badge** | Standard | Blue "Verified Pro" | Gold "Studio Host" |
| **Connection Requests** | 5 / mo | Unlimited | Unlimited |
| **Audio STEM Uploads** | Up to 3 | Up to 25 | Unlimited |
| **Studio Slot Listing** | ❌ | ❌ | Unlimited Suites |
| **Scouting Analytics** | Basic | Advanced | Enterprise Suite |
