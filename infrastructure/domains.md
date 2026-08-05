# MusicLinkd domain map

| Hostname | Product | Deploy source | Status |
| --- | --- | --- | --- |
| `musiclinkd.com` | Root redirect | `website/` | Redirect to `www.musiclinkd.com` |
| `www.musiclinkd.com` | Marketing website | `website/` | Ready for deployment configuration |
| `app.musiclinkd.com` | Customer app / Android App Links | `app/` | Android manifest already declares this host; web association/deployment still required |
| `admin.musiclinkd.com` | Internal operations portal | `admin/` | Static access-gated shell scaffolded |
| `styleguide.musiclinkd.com` | Public design-system reference | `styleguide/` | Static render scaffolded |
| `api.musiclinkd.com` | Future API | `backend/` | Reserved |

## Deployment rule

Each hostname should be deployed as an independent project or routed to its own service. DNS ownership, SSL issuance, access control, and custom-domain assignment must be configured in the selected hosting provider; creating source files alone does not publish a domain.
