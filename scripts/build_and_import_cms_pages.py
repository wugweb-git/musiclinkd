import json, re, subprocess, sys

# 1. Guides data
with open('/tmp/help_guides.json') as f:
    guides = json.load(f)

if not any(g['slug'] == 'whats-new' for g in guides):
    guides.append({
        'slug': 'whats-new',
        'route': '/help/whats-new',
        'title': "What's new in MusicLinkd",
        'summary': 'Rentals, collaboration workspaces and QR identity shipped this quarter.',
        'category': 'getting-started',
        'kind': 'release',
        'read_time': '2 min read',
        'updated_at': '2026-09-10 09:00:00+00',
        'blocks': [
            {'type': 'list', 'items': [
                'Equipment rentals with calendar availability',
                'Collaboration workspaces with milestones and files',
                'QR digital identity and share cards',
                'Provider pricing and policy controls'
            ]},
            {'type': 'cta', 'label': 'Explore What is New', 'link': {'to': '/#platform'}}
        ]
    })

pages_to_create = []

# Helper to format blocks into HTML
def render_blocks(blocks):
    html = []
    for b in blocks:
        t = b.get('type')
        if t == 'heading':
            html.append(f'<h2 style="font-size:22px;font-weight:700;letter-spacing:-0.02em;margin-top:28px;margin-bottom:12px;color:var(--ink)">{b.get("text")}</h2>')
        elif t == 'para':
            html.append(f'<p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">{b.get("text")}</p>')
        elif t == 'list':
            items = ''.join([f'<li style="display:flex;align-items:flex-start;gap:12px;margin-bottom:10px;font-size:15px;line-height:1.55;color:var(--ink)"><span style="width:7px;height:7px;border-radius:50%;background:var(--pink);margin-top:8px;flex-shrink:0"></span><span>{it}</span></li>' for it in b.get('items', [])])
            html.append(f'<ul style="margin:16px 0 24px 0;padding:0;list-style:none">{items}</ul>')
        elif t == 'callout':
            html.append(f'<div style="background:var(--surface);border:1px solid var(--line);border-left:4px solid var(--pink);border-radius:12px;padding:16px 20px;margin:20px 0;font-size:14px;line-height:1.6;color:var(--navy);font-weight:500;box-shadow:var(--shadow-1)">💡 {b.get("text")}</div>')
        elif t == 'cta':
            label = b.get('label')
            to = b.get('link', {}).get('to', '/register/')
            if to.startswith('/portfolio') or to.startswith('/profile') or to.startswith('/verification') or to.startswith('/reviews') or to.startswith('/identity'):
                to = '/register/'
            html.append(f'<div style="margin:28px 0"><a href="{to}" class="btn btn--primary" style="display:inline-flex;align-items:center;justify-content:center;min-height:46px;padding:0 24px;text-decoration:none;font-weight:600;border-radius:var(--r-pill)">{label} →</a></div>')
    return '\n'.join(html)

# 2. Individual Guide Pages
for g in guides:
    slug = g['slug']
    title = g['title']
    cat = g['category'].replace('-', ' ').title()
    time = g.get('read_time') or '4 min read'
    summary = g.get('summary', '')
    blocks_html = render_blocks(g['blocks'])

    content = f'''<!-- wp:html -->
<main class="legal-body" role="main">
  <div class="container" style="max-width:820px;padding-top:calc(var(--nav-h) + 24px);padding-bottom:80px">
    <a href="/help/" class="btn btn--ghost btn--sm" style="margin-bottom:20px;display:inline-flex;align-items:center;gap:6px;font-size:13px;font-weight:600;color:var(--muted);text-decoration:none">← Back to Help Centre</a>
    <div class="legal-hero" style="margin-bottom:28px">
      <div style="font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.08em;color:var(--pink);margin-bottom:6px">{cat} · {time}</div>
      <h1 class="h2" style="font-size:clamp(26px, 4vw, 36px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:8px">{title}</h1>
      <p class="muted" style="color:var(--muted);font-size:14px;margin:0">Verified Guide · Last updated September 2026</p>
    </div>

    <div class="help-summary-box" style="background:var(--surface);border:1px solid var(--line);border-left:4px solid var(--pink);border-radius:14px;padding:18px 22px;margin-bottom:32px;box-shadow:var(--shadow-1)">
      <b style="display:block;font-size:12px;text-transform:uppercase;letter-spacing:0.06em;color:var(--pink);margin-bottom:6px">Short Summary</b>
      <p style="margin:0;font-size:15px;line-height:1.6;color:var(--ink);font-weight:500">{summary}</p>
    </div>

    <div class="help-content-body">
      {blocks_html}
    </div>

    <div style="margin-top:48px;padding-top:24px;border-top:1px solid var(--line);display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:16px">
      <span style="font-size:14px;font-weight:600;color:var(--ink)">Was this guide helpful?</span>
      <div style="display:flex;gap:8px">
        <button class="btn btn--sm btn--ghost" style="border:1px solid var(--line);padding:6px 16px;border-radius:20px;font-size:13px" onclick="alert('Thank you for your feedback!')">👍 Yes</button>
        <button class="btn btn--sm btn--ghost" style="border:1px solid var(--line);padding:6px 16px;border-radius:20px;font-size:13px" onclick="alert('Thank you for your feedback!')">👎 No</button>
      </div>
    </div>
  </div>
</main>
<!-- /wp:html -->'''
    pages_to_create.append({
        'title': title,
        'slug': slug,
        'content': content
    })

# 3. Help Centre Hub (/help/)
help_cards = []
for g in guides:
    cat = g['category'].replace('-', ' ').title()
    time = g.get('read_time') or '4 min read'
    help_cards.append(f'''
    <a href="/{g['slug']}/" style="display:flex;flex-direction:column;background:var(--surface);border:1px solid var(--line);border-radius:16px;padding:24px;text-decoration:none;transition:transform 200ms ease, box-shadow 200ms ease;box-shadow:var(--shadow-1)" onmouseover="this.style.transform='translateY(-3px)';this.style.boxShadow='var(--shadow-3)'" onmouseout="this.style.transform='none';this.style.boxShadow='var(--shadow-1)'">
      <div style="font-size:11px;font-weight:700;text-transform:uppercase;letter-spacing:0.08em;color:var(--pink);margin-bottom:8px">{cat} · {time}</div>
      <h3 style="font-size:18px;font-weight:700;letter-spacing:-0.02em;color:var(--ink);margin-bottom:8px;line-height:1.3">{g['title']}</h3>
      <p style="font-size:14px;line-height:1.55;color:var(--muted);margin:0;flex-grow:1">{g['summary']}</p>
      <div style="margin-top:16px;font-size:13px;font-weight:600;color:var(--pink)">Read guide →</div>
    </a>''')

help_cards_html = '\n'.join(help_cards)

help_hub_content = f'''<!-- wp:html -->
<main class="legal-body" role="main">
  <div class="container" style="max-width:1080px;padding-top:calc(var(--nav-h) + 32px);padding-bottom:96px">
    <div style="text-align:center;max-width:680px;margin:0 auto 48px auto">
      <div class="eyebrow" style="color:var(--pink);font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.08em;margin-bottom:12px">MusicLinkd Help Centre</div>
      <h1 class="display" style="font-size:clamp(32px, 5vw, 48px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:16px;line-height:1.15">Guides, Policies & Best Practices</h1>
      <p class="lead" style="font-size:16px;line-height:1.6;color:var(--muted);margin:0">Everything you need to set up a profile that gets booked, manage studio schedules, verify your credentials, and build your music network.</p>
    </div>

    <!-- Category Pills -->
    <div style="display:flex;justify-content:center;flex-wrap:wrap;gap:10px;margin-bottom:48px">
      <span style="background:var(--pink-container);color:var(--pink);padding:8px 18px;border-radius:20px;font-size:13px;font-weight:700">All Topics</span>
      <span style="background:var(--surface);border:1px solid var(--line);color:var(--ink);padding:8px 18px;border-radius:20px;font-size:13px;font-weight:600">Getting Started</span>
      <span style="background:var(--surface);border:1px solid var(--line);color:var(--ink);padding:8px 18px;border-radius:20px;font-size:13px;font-weight:600">Trust & Safety</span>
      <span style="background:var(--surface);border:1px solid var(--line);color:var(--ink);padding:8px 18px;border-radius:20px;font-size:13px;font-weight:600">Bookings</span>
      <span style="background:var(--surface);border:1px solid var(--line);color:var(--ink);padding:8px 18px;border-radius:20px;font-size:13px;font-weight:600">Legal & Conduct</span>
    </div>

    <!-- Guides Grid -->
    <div style="display:grid;grid-template-columns:repeat(auto-fill, minmax(320px, 1fr));gap:20px">
      {help_cards_html}
    </div>

    <!-- Contact Support Card -->
    <div style="margin-top:64px;background:var(--surface);border:1px solid var(--line);border-radius:20px;padding:36px;text-align:center;box-shadow:var(--shadow-2)">
      <h3 style="font-size:22px;font-weight:700;letter-spacing:-0.02em;margin-bottom:8px">Have a specific inquiry or need direct support?</h3>
      <p style="font-size:15px;color:var(--muted);max-width:540px;margin:0 auto 24px auto;line-height:1.55">Our team is available for creator onboarding questions, studio partner verification, and dispute resolution.</p>
      <a href="/contact/" class="btn btn--primary" style="display:inline-flex;padding:12px 28px;text-decoration:none;font-weight:600;border-radius:var(--r-pill)">Contact MusicLinkd Team →</a>
    </div>
  </div>
</main>
<!-- /wp:html -->'''

pages_to_create.append({
    'title': 'Help Centre',
    'slug': 'help',
    'content': help_hub_content
})

# 4. About Us Page (/about/)
about_content = '''<!-- wp:html -->
<main class="legal-body" role="main">
  <div class="container" style="max-width:860px;padding-top:calc(var(--nav-h) + 32px);padding-bottom:96px">
    <div style="text-align:center;margin-bottom:48px">
      <div class="eyebrow" style="color:var(--pink);font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.08em;margin-bottom:12px">About MusicLinkd</div>
      <h1 class="display" style="font-size:clamp(32px, 5vw, 48px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:16px;line-height:1.15">The Network Behind the Sound</h1>
      <p class="lead" style="font-size:17px;line-height:1.6;color:var(--muted);max-width:680px;margin:0 auto">MusicLinkd is India's dedicated professional ecosystem and operating network for artists, sound engineers, producers, studios, session musicians, and music businesses.</p>
    </div>

    <div style="background:var(--surface);border:1px solid var(--line);border-radius:20px;padding:32px;margin-bottom:40px;box-shadow:var(--shadow-1)">
      <h2 style="font-size:22px;font-weight:700;margin-bottom:12px">Our Mission</h2>
      <p style="font-size:16px;line-height:1.65;color:var(--muted);margin-bottom:16px">Independent music in India has historically operated through fragmented WhatsApp groups, unverified word-of-mouth recommendations, and friction-filled booking conversations with no payment guarantees.</p>
      <p style="font-size:16px;line-height:1.65;color:var(--muted);margin:0">MusicLinkd replaces chaos with clarity: providing one verified digital identity, seamless booking calendars, audio portfolio showcases, transparent pricing, and trusted collaboration spaces.</p>
    </div>

    <div style="display:grid;grid-template-columns:repeat(auto-fit, minmax(250px, 1fr));gap:20px;margin-bottom:48px">
      <div style="background:var(--surface);border:1px solid var(--line);border-radius:16px;padding:24px">
        <div style="font-size:28px;font-weight:800;color:var(--pink);margin-bottom:8px">1</div>
        <h3 style="font-size:18px;font-weight:700;margin-bottom:8px">Verified Identity</h3>
        <p style="font-size:14px;color:var(--muted);line-height:1.5;margin:0">Identity, studio, and credit badges reviewed by real professionals to eliminate impersonators.</p>
      </div>
      <div style="background:var(--surface);border:1px solid var(--line);border-radius:16px;padding:24px">
        <div style="font-size:28px;font-weight:800;color:var(--pink);margin-bottom:8px">2</div>
        <h3 style="font-size:18px;font-weight:700;margin-bottom:8px">Smart Bookings</h3>
        <p style="font-size:14px;color:var(--muted);line-height:1.5;margin:0">Studio calendars, blackout dates, revision rules, and clear equipment amenities.</p>
      </div>
      <div style="background:var(--surface);border:1px solid var(--line);border-radius:16px;padding:24px">
        <div style="font-size:28px;font-weight:800;color:var(--pink);margin-bottom:8px">3</div>
        <h3 style="font-size:18px;font-weight:700;margin-bottom:8px">Frictionless Network</h3>
        <p style="font-size:14px;color:var(--muted);line-height:1.5;margin:0">Discovery by genre, city, instrument, and verified credentials across India.</p>
      </div>
    </div>

    <div style="text-align:center;padding:32px;background:var(--pink-container);border-radius:20px">
      <h3 style="font-size:20px;font-weight:700;color:var(--navy);margin-bottom:8px">Ready to join India's music network?</h3>
      <p style="font-size:14px;color:var(--navy);opacity:0.8;margin-bottom:20px">Early access is now live for creators and studios.</p>
      <a href="/register/" class="btn btn--primary" style="display:inline-flex;padding:12px 28px;text-decoration:none;font-weight:600;border-radius:var(--r-pill)">Join Early Access →</a>
    </div>
  </div>
</main>
<!-- /wp:html -->'''

pages_to_create.append({
    'title': 'About Us',
    'slug': 'about',
    'content': about_content
})

# 5. Cookie Policy Page (/cookies/)
cookie_content = '''<!-- wp:html -->
<main class="legal-body" role="main">
  <div class="container" style="max-width:820px;padding-top:calc(var(--nav-h) + 24px);padding-bottom:80px">
    <div class="legal-hero" style="margin-bottom:28px">
      <a href="/" style="font-family:var(--font-display);font-weight:800;font-size:22px;letter-spacing:-0.05em;text-decoration:none;color:var(--text-primary);display:inline-flex;align-items:center;gap:10px;margin-bottom:24px">MusicLink<em style="color:var(--brand-pink);font-style:normal">d</em></a>
      <h1 class="h2" style="font-size:clamp(26px, 4vw, 36px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:8px">Cookie Policy</h1>
      <p class="muted" style="color:var(--muted);font-size:14px;margin:0">Last updated: September 2026</p>
    </div>

    <div class="help-summary-box" style="background:var(--surface);border:1px solid var(--line);border-left:4px solid var(--pink);border-radius:14px;padding:18px 22px;margin-bottom:32px;box-shadow:var(--shadow-1)">
      <b style="display:block;font-size:12px;text-transform:uppercase;letter-spacing:0.06em;color:var(--pink);margin-bottom:6px">Short Summary</b>
      <p style="margin:0;font-size:15px;line-height:1.6;color:var(--ink);font-weight:500">We use cookies strictly for authentication session tokens, persistent login, and aggregate analytics. We do not sell tracking profiles to data brokers.</p>
    </div>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">1. What are Cookies?</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">Cookies and local storage are small data tokens stored on your device that allow MusicLinkd to recognize your session and maintain your preferences between visits.</p>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">2. Types of Cookies We Use</h2>
    <ul style="margin:16px 0 24px 0;padding:0;list-style:none">
      <li style="display:flex;align-items:flex-start;gap:12px;margin-bottom:12px;font-size:15px;line-height:1.55;color:var(--ink)"><span style="width:7px;height:7px;border-radius:50%;background:var(--pink);margin-top:8px;flex-shrink:0"></span><span><b>Strictly Necessary:</b> Authentication tokens, session state, and security tokens to prevent cross-site forgery.</span></li>
      <li style="display:flex;align-items:flex-start;gap:12px;margin-bottom:12px;font-size:15px;line-height:1.55;color:var(--ink)"><span style="width:7px;height:7px;border-radius:50%;background:var(--pink);margin-top:8px;flex-shrink:0"></span><span><b>Functional:</b> Preferences such as theme, language, and workspace filters.</span></li>
      <li style="display:flex;align-items:flex-start;gap:12px;margin-bottom:12px;font-size:15px;line-height:1.55;color:var(--ink)"><span style="width:7px;height:7px;border-radius:50%;background:var(--pink);margin-top:8px;flex-shrink:0"></span><span><b>Analytics (GA4/GTM):</b> Anonymous pageview telemetry to understand app performance without collecting personally identifying data.</span></li>
    </ul>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">3. Your Controls</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">You can control or clear cookies at any time through your browser settings or directly in Settings → Privacy inside the application.</p>

    <div style="margin-top:36px"><a href="/" class="btn btn--ghost" style="text-decoration:none;font-weight:600">← Back to MusicLinkd</a></div>
  </div>
</main>
<!-- /wp:html -->'''

pages_to_create.append({
    'title': 'Cookie Policy',
    'slug': 'cookies',
    'content': cookie_content
})

# 6. Pricing Page (/pricing/)
pricing_content = '''<!-- wp:html -->
<main class="legal-body" role="main">
  <div class="container" style="max-width:960px;padding-top:calc(var(--nav-h) + 32px);padding-bottom:96px">
    <div style="text-align:center;max-width:680px;margin:0 auto 48px auto">
      <div class="eyebrow" style="color:var(--pink);font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.08em;margin-bottom:12px">Simple, Transparent Pricing</div>
      <h1 class="display" style="font-size:clamp(32px, 5vw, 48px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:16px;line-height:1.15">Free to join. Powerful tools to grow.</h1>
      <p class="lead" style="font-size:16px;line-height:1.6;color:var(--muted);margin:0">Create a profile, list your skills, and get discovered by collaborators completely free. Studios and music businesses can unlock advanced calendar booking tools.</p>
    </div>

    <!-- Pricing Grid -->
    <div style="display:grid;grid-template-columns:repeat(auto-fit, minmax(300px, 1fr));gap:24px;margin-bottom:64px">
      <!-- Creator Plan -->
      <div style="background:var(--surface);border:1px solid var(--line);border-radius:20px;padding:36px;display:flex;flex-direction:column;box-shadow:var(--shadow-1)">
        <h3 style="font-size:20px;font-weight:700;margin-bottom:4px">Creator & Talent</h3>
        <p style="font-size:14px;color:var(--muted);margin-bottom:20px">For musicians, vocalists, producers, and audio engineers.</p>
        <div style="font-size:36px;font-weight:800;color:var(--ink);margin-bottom:24px">₹0 <span style="font-size:15px;color:var(--muted);font-weight:500">/ forever</span></div>
        <ul style="margin:0 0 32px 0;padding:0;list-style:none;flex-grow:1">
          <li style="display:flex;align-items:center;gap:10px;margin-bottom:12px;font-size:14px">✓ Verified public portfolio & profile</li>
          <li style="display:flex;align-items:center;gap:10px;margin-bottom:12px;font-size:14px">✓ Unlimited direct inquiries & messaging</li>
          <li style="display:flex;align-items:center;gap:10px;margin-bottom:12px;font-size:14px">✓ Audio showcase playback</li>
          <li style="display:flex;align-items:center;gap:10px;margin-bottom:12px;font-size:14px">✓ Digital QR identity share card</li>
        </ul>
        <a href="/register/" class="btn btn--ghost" style="width:100%;text-align:center;justify-content:center;border:1px solid var(--line);border-radius:var(--r-pill);min-height:46px;text-decoration:none;font-weight:600">Get Started Free</a>
      </div>

      <!-- Studio Pro Plan -->
      <div style="background:var(--navy);color:#fff;border-radius:20px;padding:36px;display:flex;flex-direction:column;box-shadow:var(--shadow-4);position:relative">
        <div style="position:absolute;top:16px;right:16px;background:var(--pink);color:#fff;font-size:11px;font-weight:700;padding:4px 10px;border-radius:12px;text-transform:uppercase;letter-spacing:0.06em">Early Access</div>
        <h3 style="font-size:20px;font-weight:700;margin-bottom:4px;color:#fff">Studio & Business Pro</h3>
        <p style="font-size:14px;color:var(--text-on-dark-muted);margin-bottom:20px">For commercial recording studios, rental hubs, and labels.</p>
        <div style="font-size:36px;font-weight:800;color:#fff;margin-bottom:24px">Free <span style="font-size:15px;color:var(--text-on-dark-muted);font-weight:500">in Early Access</span></div>
        <ul style="margin:0 0 32px 0;padding:0;list-style:none;flex-grow:1">
          <li style="display:flex;align-items:center;gap:10px;margin-bottom:12px;font-size:14px">✓ Multi-room studio booking calendar</li>
          <li style="display:flex;align-items:center;gap:10px;margin-bottom:12px;font-size:14px">✓ Blackout dates & deposit policy controls</li>
          <li style="display:flex;align-items:center;gap:10px;margin-bottom:12px;font-size:14px">✓ Equipment rental catalog management</li>
          <li style="display:flex;align-items:center;gap:10px;margin-bottom:12px;font-size:14px">✓ Verified Studio Business badge</li>
        </ul>
        <a href="/register/" class="btn btn--primary" style="width:100%;text-align:center;justify-content:center;border-radius:var(--r-pill);min-height:46px;text-decoration:none;font-weight:600">Claim Early Access</a>
      </div>
    </div>
  </div>
</main>
<!-- /wp:html -->'''

pages_to_create.append({
    'title': 'Pricing',
    'slug': 'pricing',
    'content': pricing_content
})

print(f"Total pages generated: {len(pages_to_create)}")
with open('/tmp/all_pages_payload.json', 'w') as out:
    json.dump(pages_to_create, out, indent=2)
