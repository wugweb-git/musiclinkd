# MusicLinkd domain map

| Hostname | Product | Deploy source | Status |
| --- | --- | --- | --- |
| `musiclinkd.com` | Marketing & Legal Hub | Hostinger WordPress | 🟢 **Live** (LiteSpeed / hCDN, FSE Theme, FluentCRM, Fluent Forms, Headless REST CMS) |
| `app.musiclinkd.com` | Customer app / Android App Links | Cloudflare / Lovable (`musiclinkd-9e6ca875`) | 🟢 **Live** (Cloudflare Edge, TanStack Start + React 19, Supabase backend) |
| `admin.musiclinkd.com` | Internal operations portal | WordPress Admin (`/wp-admin/`) | 🟢 **Live** (Authenticated via WordPress Hostinger instance) |
| `styleguide.musiclinkd.com` | Public design-system reference | `frontend/guide/` | Static reference / Storybook |
| `api.musiclinkd.com` | Future API / REST Endpoints | WordPress REST API (`/wp-json/wp/v2/`) | 🟢 **Live** (Active on `musiclinkd.com/wp-json/`) |

## Deployment rule

Each hostname should be deployed as an independent project or routed to its own service. DNS ownership, SSL issuance, access control, and custom-domain assignment must be configured in the selected hosting provider; creating source files alone does not publish a domain.
