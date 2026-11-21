import json, os, subprocess

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

os.makedirs('/tmp/gutenberg_blocks', exist_ok=True)

# 1. Guides to Gutenberg Blocks
guide_id_map = {
    'profile-that-gets-booked': 20,
    'pricing-your-first-service': 21,
    'how-verification-works': 22,
    'booking-policies-explained': 23,
    'review-policy': 24,
    'community-guidelines': 25,
    'whats-new': 26,
}

for g in guides:
    slug = g['slug']
    post_id = guide_id_map.get(slug)
    if not post_id:
        continue
    title = g['title']
    cat = g['category'].replace('-', ' ').title()
    time = g.get('read_time') or '4 min read'
    summary = g.get('summary', '')

    out = []
    out.append('<!-- wp:group {"tagName":"main","className":"legal-body","layout":{"type":"constrained","contentSize":"820px"}} -->')
    out.append('<main class="wp-block-group legal-body" style="padding-top:calc(var(--nav-h) + 24px);padding-bottom:80px">')

    # Back button
    out.append('<!-- wp:buttons {"layout":{"type":"flex"}} -->')
    out.append('<div class="wp-block-buttons" style="margin-bottom:20px"><!-- wp:button {"className":"btn btn--ghost btn--sm"} -->')
    out.append('<div class="wp-block-button btn btn--ghost btn--sm"><a class="wp-block-button__link wp-element-button" href="/help/" style="font-size:13px;font-weight:600;color:var(--muted);text-decoration:none">← Back to Help Centre</a></div>')
    out.append('<!-- /wp:button --></div>')
    out.append('<!-- /wp:buttons -->')

    # Eyebrow
    out.append('<!-- wp:paragraph {"style":{"typography":{"textTransform":"uppercase","fontWeight":"700"}}} -->')
    out.append(f'<p style="font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.08em;color:var(--pink);margin-bottom:6px">{cat} · {time}</p>')
    out.append('<!-- /wp:paragraph -->')

    # Title
    out.append('<!-- wp:heading {"level":1,"className":"h2 wp-block-heading"} -->')
    out.append(f'<h1 class="wp-block-heading h2" style="font-size:clamp(26px, 4vw, 36px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:8px">{title}</h1>')
    out.append('<!-- /wp:heading -->')

    # Subtitle
    out.append('<!-- wp:paragraph {"className":"muted"} -->')
    out.append('<p class="muted" style="color:var(--muted);font-size:14px;margin-bottom:24px">Verified Guide · Last updated September 2026</p>')
    out.append('<!-- /wp:paragraph -->')

    # Short Summary Box
    out.append('<!-- wp:group {"className":"help-summary-box","layout":{"type":"constrained"}} -->')
    out.append('<div class="wp-block-group help-summary-box" style="background:var(--surface);border:1px solid var(--line);border-left:4px solid var(--pink);border-radius:14px;padding:18px 22px;margin-bottom:32px;box-shadow:var(--shadow-1)">')
    out.append('<!-- wp:paragraph -->')
    out.append('<p style="font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.06em;color:var(--pink);margin-bottom:6px"><strong>SHORT SUMMARY</strong></p>')
    out.append('<!-- /wp:paragraph -->')
    out.append('<!-- wp:paragraph -->')
    out.append(f'<p style="margin:0;font-size:15px;line-height:1.6;color:var(--ink);font-weight:500">{summary}</p>')
    out.append('<!-- /wp:paragraph -->')
    out.append('</div>')
    out.append('<!-- /wp:group -->')

    # Content body blocks
    out.append('<!-- wp:group {"className":"help-content-body","layout":{"type":"default"}} -->')
    out.append('<div class="wp-block-group help-content-body">')

    for b in g.get('blocks', []):
        t = b.get('type')
        if t == 'heading':
            out.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} -->')
            out.append(f'<h2 class="wp-block-heading" style="font-size:22px;font-weight:700;letter-spacing:-0.02em;margin-top:28px;margin-bottom:12px;color:var(--ink)">{b.get("text")}</h2>')
            out.append('<!-- /wp:heading -->')
        elif t == 'para':
            out.append('<!-- wp:paragraph -->')
            out.append(f'<p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">{b.get("text")}</p>')
            out.append('<!-- /wp:paragraph -->')
        elif t == 'list':
            out.append('<!-- wp:list {"className":"wp-block-list help-guide-list"} -->')
            out.append('<ul class="wp-block-list help-guide-list" style="margin:16px 0 24px 0;padding:0;list-style:none">')
            for it in b.get('items', []):
                out.append('<!-- wp:list-item -->')
                out.append(f'<li style="display:flex;align-items:flex-start;gap:12px;margin-bottom:10px;font-size:15px;line-height:1.55;color:var(--ink)"><span style="width:7px;height:7px;border-radius:50%;background:var(--pink);margin-top:8px;flex-shrink:0"></span><span>{it}</span></li>')
                out.append('<!-- /wp:list-item -->')
            out.append('</ul>')
            out.append('<!-- /wp:list -->')
        elif t == 'callout':
            out.append('<!-- wp:group {"className":"help-callout-box","layout":{"type":"constrained"}} -->')
            out.append(f'<div class="wp-block-group help-callout-box" style="background:var(--surface);border:1px solid var(--line);border-left:4px solid var(--pink);border-radius:12px;padding:16px 20px;margin:20px 0;font-size:14px;line-height:1.6;color:var(--navy);font-weight:500;box-shadow:var(--shadow-1)"><p style="margin:0">💡 {b.get("text")}</p></div>')
            out.append('<!-- /wp:group -->')
        elif t == 'cta':
            label = b.get('label')
            to = b.get('link', {}).get('to', '/register/')
            if to.startswith('/portfolio') or to.startswith('/profile') or to.startswith('/verification') or to.startswith('/reviews') or to.startswith('/identity'):
                to = '/register/'
            out.append('<!-- wp:buttons -->')
            out.append(f'<div class="wp-block-buttons" style="margin:28px 0"><!-- wp:button {{"className":"btn btn--primary"}} --><div class="wp-block-button btn btn--primary"><a class="wp-block-button__link wp-element-button" href="{to}" style="display:inline-flex;align-items:center;justify-content:center;min-height:46px;padding:0 24px;text-decoration:none;font-weight:600;border-radius:var(--r-pill)">{label} →</a></div><!-- /wp:button --></div>')
            out.append('<!-- /wp:buttons -->')

    out.append('</div>')
    out.append('<!-- /wp:group -->')

    # Helpful feedback
    out.append('<!-- wp:group {"className":"help-feedback-box","layout":{"type":"flex","flexWrap":"wrap","justifyContent":"space-between"}} -->')
    out.append('<div class="wp-block-group help-feedback-box" style="margin-top:48px;padding-top:24px;border-top:1px solid var(--line);display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:16px">')
    out.append('<!-- wp:paragraph -->')
    out.append('<p style="font-size:14px;font-weight:600;color:var(--ink);margin:0">Was this guide helpful?</p>')
    out.append('<!-- /wp:paragraph -->')
    out.append('<!-- wp:buttons -->')
    out.append('<div class="wp-block-buttons" style="display:flex;gap:8px"><!-- wp:button {"className":"btn btn--sm btn--ghost"} --><div class="wp-block-button btn btn--sm btn--ghost"><a class="wp-block-button__link wp-element-button" href="javascript:alert(\'Thank you for your feedback!\')" style="border:1px solid var(--line);padding:6px 16px;border-radius:20px;font-size:13px">👍 Yes</a></div><!-- /wp:button --><!-- wp:button {"className":"btn btn--sm btn--ghost"} --><div class="wp-block-button btn btn--sm btn--ghost"><a class="wp-block-button__link wp-element-button" href="javascript:alert(\'Thank you for your feedback!\')" style="border:1px solid var(--line);padding:6px 16px;border-radius:20px;font-size:13px">👎 No</a></div><!-- /wp:button --></div>')
    out.append('<!-- /wp:buttons -->')
    out.append('</div>')
    out.append('<!-- /wp:group -->')

    out.append('</main>')
    out.append('<!-- /wp:group -->')

    with open(f'/tmp/gutenberg_blocks/{post_id}.html', 'w') as out_f:
        out_f.write('\n'.join(out))

# 2. Help Centre Hub (ID 27)
hub = []
hub.append('<!-- wp:group {"tagName":"main","className":"help-hub-page","layout":{"type":"constrained","contentSize":"980px"}} -->')
hub.append('<main class="wp-block-group help-hub-page" style="padding-top:calc(var(--nav-h) + 32px);padding-bottom:96px">')

# Hero search header
hub.append('<!-- wp:group {"className":"help-hero","layout":{"type":"constrained"}} -->')
hub.append('<div class="wp-block-group help-hero" style="text-align:center;max-width:680px;margin:0 auto 48px auto">')
hub.append('<!-- wp:paragraph -->')
hub.append('<p style="color:var(--pink);font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.08em;margin-bottom:12px">Support & Knowledge Base</p>')
hub.append('<!-- /wp:paragraph -->')
hub.append('<!-- wp:heading {"level":1,"className":"display wp-block-heading"} -->')
hub.append('<h1 class="display wp-block-heading" style="font-size:clamp(32px, 5vw, 44px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:16px;line-height:1.15">How can we help you?</h1>')
hub.append('<!-- /wp:heading -->')
hub.append('<!-- wp:paragraph {"className":"lead"} -->')
hub.append('<p class="lead" style="font-size:16px;line-height:1.6;color:var(--muted);margin-bottom:28px">Search our guides, verified policies, booking workflows, and account verification rules.</p>')
hub.append('<!-- /wp:paragraph -->')

hub.append('<!-- wp:search {"label":"Search Guides","showLabel":false,"placeholder":"Search guides, verification, bookings...","buttonText":"Search","buttonPosition":"button-inside","buttonUseIcon":true,"className":"help-search-bar"} /-->')
hub.append('</div>')
hub.append('<!-- /wp:group -->')

# Category filter pills
hub.append('<!-- wp:buttons {"layout":{"type":"flex","justifyContent":"center"},"className":"help-pills"} -->')
hub.append('<div class="wp-block-buttons help-pills" style="margin-bottom:40px;display:flex;flex-wrap:wrap;justify-content:center;gap:10px">')
hub.append('<!-- wp:button {"className":"btn btn--sm is-style-fill"} --><div class="wp-block-button btn btn--sm is-style-fill"><a class="wp-block-button__link wp-element-button" href="#all">All Guides</a></div><!-- /wp:button -->')
hub.append('<!-- wp:button {"className":"btn btn--sm btn--ghost"} --><div class="wp-block-button btn btn--sm btn--ghost"><a class="wp-block-button__link wp-element-button" href="#getting-started">Getting Started</a></div><!-- /wp:button -->')
hub.append('<!-- wp:button {"className":"btn btn--sm btn--ghost"} --><div class="wp-block-button btn btn--sm btn--ghost"><a class="wp-block-button__link wp-element-button" href="#verification">Verification</a></div><!-- /wp:button -->')
hub.append('<!-- wp:button {"className":"btn btn--sm btn--ghost"} --><div class="wp-block-button btn btn--sm btn--ghost"><a class="wp-block-button__link wp-element-button" href="#trust-safety">Trust & Safety</a></div><!-- /wp:button -->')
hub.append('</div>')
hub.append('<!-- /wp:buttons -->')

# Guides Grid in Columns
hub.append('<!-- wp:group {"className":"help-grid-wrap","layout":{"type":"default"}} -->')
hub.append('<div class="wp-block-group help-grid-wrap">')

# Split guides into 2 per row
for i in range(0, len(guides), 2):
    pair = guides[i:i+2]
    hub.append('<!-- wp:columns {"className":"help-row"} -->')
    hub.append('<div class="wp-block-columns help-row" style="margin-bottom:20px">')
    for g in pair:
        slug = g['slug']
        title = g['title']
        cat = g['category'].replace('-', ' ').title()
        time = g.get('read_time') or '4 min read'
        summary = g.get('summary', '')
        hub.append('<!-- wp:column {"className":"help-col"} -->')
        hub.append('<div class="wp-block-column help-col">')
        hub.append('<!-- wp:group {"className":"help-card","layout":{"type":"constrained"}} -->')
        hub.append(f'<div class="wp-block-group help-card" style="background:var(--surface);border:1px solid var(--line);border-radius:16px;padding:24px;box-shadow:var(--shadow-1);height:100%;display:flex;flex-direction:column;justify-content:space-between">')
        hub.append('<div>')
        hub.append(f'<!-- wp:paragraph --><p style="font-size:11px;font-weight:700;text-transform:uppercase;letter-spacing:0.06em;color:var(--pink);margin-bottom:8px">{cat} · {time}</p><!-- /wp:paragraph -->')
        hub.append(f'<!-- wp:heading {{"level":3,"className":"wp-block-heading"}} --><h3 class="wp-block-heading" style="font-size:18px;font-weight:700;letter-spacing:-0.02em;margin-bottom:8px;color:var(--ink)"><a href="/{slug}/" style="text-decoration:none;color:inherit">{title}</a></h3><!-- /wp:heading -->')
        hub.append(f'<!-- wp:paragraph --><p style="font-size:14px;line-height:1.55;color:var(--muted);margin-bottom:16px">{summary}</p><!-- /wp:paragraph -->')
        hub.append('</div>')
        hub.append(f'<!-- wp:paragraph --><p style="margin:0"><a href="/{slug}/" style="color:var(--pink);font-weight:600;font-size:13px;text-decoration:none">Read Guide →</a></p><!-- /wp:paragraph -->')
        hub.append('</div>')
        hub.append('<!-- /wp:group -->')
        hub.append('</div>')
        hub.append('<!-- /wp:column -->')
    hub.append('</div>')
    hub.append('<!-- /wp:columns -->')

hub.append('</div>')
hub.append('<!-- /wp:group -->')

# Direct Support Card
hub.append('<!-- wp:group {"className":"help-support-card","layout":{"type":"constrained"}} -->')
hub.append('<div class="wp-block-group help-support-card" style="margin-top:64px;background:var(--pink-container);border:1px solid rgba(255,0,92,0.15);border-radius:20px;padding:36px;text-align:center">')
hub.append('<!-- wp:heading {"level":3,"className":"wp-block-heading"} -->')
hub.append('<h3 class="wp-block-heading" style="font-size:22px;font-weight:700;color:var(--navy);margin-bottom:8px">Cannot find what you are looking for?</h3>')
hub.append('<!-- /wp:heading -->')
hub.append('<!-- wp:paragraph -->')
hub.append('<p style="font-size:15px;color:var(--navy);opacity:0.85;margin-bottom:24px">Our creator operations team responds within 24 hours to all verification and booking questions.</p>')
hub.append('<!-- /wp:paragraph -->')
hub.append('<!-- wp:buttons {"layout":{"type":"flex","justifyContent":"center"}} -->')
hub.append('<div class="wp-block-buttons" style="display:flex;justify-content:center"><!-- wp:button {"className":"btn btn--primary"} --><div class="wp-block-button btn btn--primary"><a class="wp-block-button__link wp-element-button" href="/contact/" style="padding:12px 28px;text-decoration:none;font-weight:600">Contact Support Team →</a></div><!-- /wp:button --></div>')
hub.append('<!-- /wp:buttons -->')
hub.append('</div>')
hub.append('<!-- /wp:group -->')

hub.append('</main>')
hub.append('<!-- /wp:group -->')

with open('/tmp/gutenberg_blocks/27.html', 'w') as out_f:
    out_f.write('\n'.join(hub))

# 3. About Us (ID 28)
abt = []
abt.append('<!-- wp:group {"tagName":"main","className":"legal-body","layout":{"type":"constrained","contentSize":"860px"}} -->')
abt.append('<main class="wp-block-group legal-body" style="padding-top:calc(var(--nav-h) + 32px);padding-bottom:96px">')

abt.append('<!-- wp:group {"className":"about-hero","layout":{"type":"constrained"}} -->')
abt.append('<div class="wp-block-group about-hero" style="text-align:center;margin-bottom:48px">')
abt.append('<!-- wp:paragraph --><p style="color:var(--pink);font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.08em;margin-bottom:12px">About MusicLinkd</p><!-- /wp:paragraph -->')
abt.append('<!-- wp:heading {"level":1,"className":"display wp-block-heading"} --><h1 class="display wp-block-heading" style="font-size:clamp(32px, 5vw, 48px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:16px;line-height:1.15">The Network Behind the Sound</h1><!-- /wp:heading -->')
abt.append('<!-- wp:paragraph {"className":"lead"} --><p class="lead" style="font-size:17px;line-height:1.6;color:var(--muted);max-width:680px;margin:0 auto">MusicLinkd is India\'s dedicated professional ecosystem and operating network for artists, sound engineers, producers, studios, session musicians, and music businesses.</p><!-- /wp:paragraph -->')
abt.append('</div>')
abt.append('<!-- /wp:group -->')

abt.append('<!-- wp:group {"className":"mission-card","layout":{"type":"constrained"}} -->')
abt.append('<div class="wp-block-group mission-card" style="background:var(--surface);border:1px solid var(--line);border-radius:20px;padding:32px;margin-bottom:40px;box-shadow:var(--shadow-1)">')
abt.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-bottom:12px">Our Mission</h2><!-- /wp:heading -->')
abt.append('<!-- wp:paragraph --><p style="font-size:16px;line-height:1.65;color:var(--muted);margin-bottom:16px">Independent music in India has historically operated through fragmented WhatsApp groups, unverified word-of-mouth recommendations, and friction-filled booking conversations with no payment guarantees.</p><!-- /wp:paragraph -->')
abt.append('<!-- wp:paragraph --><p style="font-size:16px;line-height:1.65;color:var(--muted);margin:0">MusicLinkd replaces chaos with clarity: providing one verified digital identity, seamless booking calendars, audio portfolio showcases, transparent pricing, and trusted collaboration spaces.</p><!-- /wp:paragraph -->')
abt.append('</div>')
abt.append('<!-- /wp:group -->')

# 3 Pillars Columns
abt.append('<!-- wp:columns {"className":"about-pillars"} -->')
abt.append('<div class="wp-block-columns about-pillars" style="gap:20px;margin-bottom:48px">')
abt.append('<!-- wp:column -->')
abt.append('<div class="wp-block-column" style="background:var(--surface);border:1px solid var(--line);border-radius:16px;padding:24px">')
abt.append('<!-- wp:paragraph --><p style="font-size:28px;font-weight:800;color:var(--pink);margin-bottom:8px">1</p><!-- /wp:paragraph -->')
abt.append('<!-- wp:heading {"level":3,"className":"wp-block-heading"} --><h3 class="wp-block-heading" style="font-size:18px;font-weight:700;margin-bottom:8px">Verified Identity</h3><!-- /wp:heading -->')
abt.append('<!-- wp:paragraph --><p style="font-size:14px;color:var(--muted);line-height:1.5;margin:0">Identity, studio, and credit badges reviewed by real professionals to eliminate impersonators.</p><!-- /wp:paragraph -->')
abt.append('</div>')
abt.append('<!-- /wp:column -->')

abt.append('<!-- wp:column -->')
abt.append('<div class="wp-block-column" style="background:var(--surface);border:1px solid var(--line);border-radius:16px;padding:24px">')
abt.append('<!-- wp:paragraph --><p style="font-size:28px;font-weight:800;color:var(--pink);margin-bottom:8px">2</p><!-- /wp:paragraph -->')
abt.append('<!-- wp:heading {"level":3,"className":"wp-block-heading"} --><h3 class="wp-block-heading" style="font-size:18px;font-weight:700;margin-bottom:8px">Smart Bookings</h3><!-- /wp:heading -->')
abt.append('<!-- wp:paragraph --><p style="font-size:14px;color:var(--muted);line-height:1.5;margin:0">Studio calendars, blackout dates, revision rules, and clear equipment amenities.</p><!-- /wp:paragraph -->')
abt.append('</div>')
abt.append('<!-- /wp:column -->')

abt.append('<!-- wp:column -->')
abt.append('<div class="wp-block-column" style="background:var(--surface);border:1px solid var(--line);border-radius:16px;padding:24px">')
abt.append('<!-- wp:paragraph --><p style="font-size:28px;font-weight:800;color:var(--pink);margin-bottom:8px">3</p><!-- /wp:paragraph -->')
abt.append('<!-- wp:heading {"level":3,"className":"wp-block-heading"} --><h3 class="wp-block-heading" style="font-size:18px;font-weight:700;margin-bottom:8px">Frictionless Network</h3><!-- /wp:heading -->')
abt.append('<!-- wp:paragraph --><p style="font-size:14px;color:var(--muted);line-height:1.5;margin:0">Discovery by genre, city, instrument, and verified credentials across India.</p><!-- /wp:paragraph -->')
abt.append('</div>')
abt.append('<!-- /wp:column -->')
abt.append('</div>')
abt.append('<!-- /wp:columns -->')

# CTA Box
abt.append('<!-- wp:group {"className":"about-cta-box","layout":{"type":"constrained"}} -->')
abt.append('<div class="wp-block-group about-cta-box" style="text-align:center;padding:32px;background:var(--pink-container);border-radius:20px">')
abt.append('<!-- wp:heading {"level":3,"className":"wp-block-heading"} --><h3 class="wp-block-heading" style="font-size:20px;font-weight:700;color:var(--navy);margin-bottom:8px">Ready to join India\'s music network?</h3><!-- /wp:heading -->')
abt.append('<!-- wp:paragraph --><p style="font-size:14px;color:var(--navy);opacity:0.8;margin-bottom:20px">Early access is now live for creators and studios.</p><!-- /wp:paragraph -->')
abt.append('<!-- wp:buttons {"layout":{"type":"flex","justifyContent":"center"}} --><div class="wp-block-buttons" style="display:flex;justify-content:center"><!-- wp:button {"className":"btn btn--primary"} --><div class="wp-block-button btn btn--primary"><a class="wp-block-button__link wp-element-button" href="/register/" style="display:inline-flex;padding:12px 28px;text-decoration:none;font-weight:600;border-radius:var(--r-pill)">Join Early Access →</a></div><!-- /wp:button --></div><!-- /wp:buttons -->')
abt.append('</div>')
abt.append('<!-- /wp:group -->')

abt.append('</main>')
abt.append('<!-- /wp:group -->')

with open('/tmp/gutenberg_blocks/28.html', 'w') as out_f:
    out_f.write('\n'.join(abt))

# 4. Cookie Policy (ID 29)
ck = []
ck.append('<!-- wp:group {"tagName":"main","className":"legal-body","layout":{"type":"constrained","contentSize":"820px"}} -->')
ck.append('<main class="wp-block-group legal-body" style="padding-top:calc(var(--nav-h) + 24px);padding-bottom:80px">')

ck.append('<!-- wp:heading {"level":1,"className":"h2 wp-block-heading"} --><h1 class="wp-block-heading h2" style="font-size:clamp(26px, 4vw, 36px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:8px">Cookie Policy</h1><!-- /wp:heading -->')
ck.append('<!-- wp:paragraph {"className":"muted"} --><p class="muted" style="color:var(--muted);font-size:14px;margin-bottom:24px">Last updated: September 2026</p><!-- /wp:paragraph -->')

ck.append('<!-- wp:group {"className":"help-summary-box","layout":{"type":"constrained"}} -->')
ck.append('<div class="wp-block-group help-summary-box" style="background:var(--surface);border:1px solid var(--line);border-left:4px solid var(--pink);border-radius:14px;padding:18px 22px;margin-bottom:32px;box-shadow:var(--shadow-1)">')
ck.append('<!-- wp:paragraph --><p style="font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.06em;color:var(--pink);margin-bottom:6px"><strong>SHORT SUMMARY</strong></p><!-- /wp:paragraph -->')
ck.append('<!-- wp:paragraph --><p style="margin:0;font-size:15px;line-height:1.6;color:var(--ink);font-weight:500">We use cookies strictly for authentication session tokens, persistent login, and aggregate analytics. We do not sell tracking profiles to data brokers.</p><!-- /wp:paragraph -->')
ck.append('</div>')
ck.append('<!-- /wp:group -->')

ck.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">1. What are Cookies?</h2><!-- /wp:heading -->')
ck.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">Cookies and local storage are small data tokens stored on your device that allow MusicLinkd to recognize your session and maintain your preferences between visits.</p><!-- /wp:paragraph -->')

ck.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">2. Types of Cookies We Use</h2><!-- /wp:heading -->')
ck.append('<!-- wp:list {"className":"wp-block-list"} -->')
ck.append('<ul class="wp-block-list" style="margin:16px 0 24px 0;padding:0;list-style:none">')
ck.append('<!-- wp:list-item --><li style="display:flex;align-items:flex-start;gap:12px;margin-bottom:12px;font-size:15px;line-height:1.55;color:var(--ink)"><span style="width:7px;height:7px;border-radius:50%;background:var(--pink);margin-top:8px;flex-shrink:0"></span><span><strong>Strictly Necessary:</strong> Authentication tokens, session state, and security tokens to prevent cross-site forgery.</span></li><!-- /wp:list-item -->')
ck.append('<!-- wp:list-item --><li style="display:flex;align-items:flex-start;gap:12px;margin-bottom:12px;font-size:15px;line-height:1.55;color:var(--ink)"><span style="width:7px;height:7px;border-radius:50%;background:var(--pink);margin-top:8px;flex-shrink:0"></span><span><strong>Functional:</strong> Preferences such as theme, language, and workspace filters.</span></li><!-- /wp:list-item -->')
ck.append('<!-- wp:list-item --><li style="display:flex;align-items:flex-start;gap:12px;margin-bottom:12px;font-size:15px;line-height:1.55;color:var(--ink)"><span style="width:7px;height:7px;border-radius:50%;background:var(--pink);margin-top:8px;flex-shrink:0"></span><span><strong>Analytics (GA4/GTM):</strong> Anonymous pageview telemetry to understand app performance without collecting personally identifying data.</span></li><!-- /wp:list-item -->')
ck.append('</ul>')
ck.append('<!-- /wp:list -->')

ck.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">3. Your Controls</h2><!-- /wp:heading -->')
ck.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">You can control or clear cookies at any time through your browser settings or directly in Settings → Privacy inside the application.</p><!-- /wp:paragraph -->')

ck.append('<!-- wp:buttons --><div class="wp-block-buttons" style="margin-top:36px"><!-- wp:button {"className":"btn btn--ghost"} --><div class="wp-block-button btn btn--ghost"><a class="wp-block-button__link wp-element-button" href="/" style="text-decoration:none;font-weight:600">← Back to MusicLinkd</a></div><!-- /wp:button --></div><!-- /wp:buttons -->')

ck.append('</main>')
ck.append('<!-- /wp:group -->')

with open('/tmp/gutenberg_blocks/29.html', 'w') as out_f:
    out_f.write('\n'.join(ck))

# 5. Pricing (ID 30)
pr = []
pr.append('<!-- wp:group {"tagName":"main","className":"legal-body","layout":{"type":"constrained","contentSize":"960px"}} -->')
pr.append('<main class="wp-block-group legal-body" style="padding-top:calc(var(--nav-h) + 32px);padding-bottom:96px">')

pr.append('<!-- wp:group {"className":"pricing-hero","layout":{"type":"constrained"}} -->')
pr.append('<div class="wp-block-group pricing-hero" style="text-align:center;max-width:680px;margin:0 auto 48px auto">')
pr.append('<!-- wp:paragraph --><p style="color:var(--pink);font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.08em;margin-bottom:12px">Simple, Transparent Pricing</p><!-- /wp:paragraph -->')
pr.append('<!-- wp:heading {"level":1,"className":"display wp-block-heading"} --><h1 class="display wp-block-heading" style="font-size:clamp(32px, 5vw, 48px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:16px;line-height:1.15">Free to join. Powerful tools to grow.</h1><!-- /wp:heading -->')
pr.append('<!-- wp:paragraph {"className":"lead"} --><p class="lead" style="font-size:16px;line-height:1.6;color:var(--muted);margin:0">Create a profile, list your skills, and get discovered by collaborators completely free. Studios and music businesses can unlock advanced calendar booking tools.</p><!-- /wp:paragraph -->')
pr.append('</div>')
pr.append('<!-- /wp:group -->')

# 2 Pricing Columns
pr.append('<!-- wp:columns {"className":"pricing-grid"} -->')
pr.append('<div class="wp-block-columns pricing-grid" style="gap:24px;margin-bottom:64px">')

# Column 1
pr.append('<!-- wp:column -->')
pr.append('<div class="wp-block-column" style="background:var(--surface);border:1px solid var(--line);border-radius:20px;padding:32px;display:flex;flex-direction:column;justify-content:space-between">')
pr.append('<div>')
pr.append('<!-- wp:heading {"level":3,"className":"wp-block-heading"} --><h3 class="wp-block-heading" style="font-size:20px;font-weight:700;margin-bottom:6px">Creator & Talent</h3><!-- /wp:heading -->')
pr.append('<!-- wp:paragraph --><p style="font-size:14px;color:var(--muted);margin-bottom:20px">For artists, vocalists, producers, sound engineers & session musicians.</p><!-- /wp:paragraph -->')
pr.append('<!-- wp:paragraph --><p style="font-size:36px;font-weight:800;color:var(--ink);margin-bottom:24px">₹0 <span style="font-size:15px;font-weight:500;color:var(--muted)">/ forever</span></p><!-- /wp:paragraph -->')
pr.append('<!-- wp:list {"className":"wp-block-list"} -->')
pr.append('<ul class="wp-block-list" style="margin:0 0 32px 0;padding:0;list-style:none">')
for feat in ["Verified identity & talent profile", "Audio portfolio & streaming showcases", "Direct messaging & collaboration", "Skill & genre tagging for discovery", "Client reviews and verified credits"]:
    pr.append(f'<!-- wp:list-item --><li style="display:flex;align-items:center;gap:10px;margin-bottom:12px;font-size:14px;color:var(--ink)"><span style="color:var(--pink)">✓</span> {feat}</li><!-- /wp:list-item -->')
pr.append('</ul>')
pr.append('<!-- /wp:list -->')
pr.append('</div>')
pr.append('<!-- wp:buttons --><div class="wp-block-buttons"><!-- wp:button {"className":"btn btn--primary"} --><div class="wp-block-button btn btn--primary"><a class="wp-block-button__link wp-element-button" href="/register/" style="width:100%;justify-content:center;padding:12px 20px;text-decoration:none;font-weight:600;border-radius:var(--r-pill)">Join Free Forever →</a></div><!-- /wp:button --></div><!-- /wp:buttons -->')
pr.append('</div>')
pr.append('<!-- /wp:column -->')

# Column 2
pr.append('<!-- wp:column -->')
pr.append('<div class="wp-block-column" style="background:var(--surface);border:2px solid var(--pink);border-radius:20px;padding:32px;display:flex;flex-direction:column;justify-content:space-between;position:relative">')
pr.append('<div>')
pr.append('<!-- wp:paragraph --><p style="font-size:11px;font-weight:700;text-transform:uppercase;background:var(--pink);color:#fff;padding:4px 12px;border-radius:20px;display:inline-block;margin-bottom:12px">Early Access</p><!-- /wp:paragraph -->')
pr.append('<!-- wp:heading {"level":3,"className":"wp-block-heading"} --><h3 class="wp-block-heading" style="font-size:20px;font-weight:700;margin-bottom:6px">Studio & Business Pro</h3><!-- /wp:heading -->')
pr.append('<!-- wp:paragraph --><p style="font-size:14px;color:var(--muted);margin-bottom:20px">For commercial recording studios, rehearsal spaces & audio equipment providers.</p><!-- /wp:paragraph -->')
pr.append('<!-- wp:paragraph --><p style="font-size:36px;font-weight:800;color:var(--ink);margin-bottom:24px">Free <span style="font-size:15px;font-weight:500;color:var(--muted)">during early access</span></p><!-- /wp:paragraph -->')
pr.append('<!-- wp:list {"className":"wp-block-list"} -->')
pr.append('<ul class="wp-block-list" style="margin:0 0 32px 0;padding:0;list-style:none">')
for feat in ["Everything in Creator, plus:", "Studio room availability & booking calendar", "Hourly rates, blackout dates & deposit rules", "Equipment inventory & amenity listings", "Priority placement in local studio search"]:
    pr.append(f'<!-- wp:list-item --><li style="display:flex;align-items:center;gap:10px;margin-bottom:12px;font-size:14px;color:var(--ink)"><span style="color:var(--pink)">✓</span> {feat}</li><!-- /wp:list-item -->')
pr.append('</ul>')
pr.append('<!-- /wp:list -->')
pr.append('</div>')
pr.append('<!-- wp:buttons --><div class="wp-block-buttons"><!-- wp:button {"className":"btn btn--primary"} --><div class="wp-block-button btn btn--primary"><a class="wp-block-button__link wp-element-button" href="/register/" style="width:100%;justify-content:center;padding:12px 20px;text-decoration:none;font-weight:600;border-radius:var(--r-pill)">Claim Studio Early Access →</a></div><!-- /wp:button --></div><!-- /wp:buttons -->')
pr.append('</div>')
pr.append('<!-- /wp:column -->')

pr.append('</div>')
pr.append('<!-- /wp:columns -->')

pr.append('</main>')
pr.append('<!-- /wp:group -->')

with open('/tmp/gutenberg_blocks/30.html', 'w') as out_f:
    out_f.write('\n'.join(pr))

# 6. Privacy Policy (ID 3)
pv = []
pv.append('<!-- wp:group {"tagName":"main","className":"legal-body","layout":{"type":"constrained","contentSize":"820px"}} -->')
pv.append('<main class="wp-block-group legal-body" style="padding-top:calc(var(--nav-h) + 24px);padding-bottom:80px">')

pv.append('<!-- wp:heading {"level":1,"className":"h2 wp-block-heading"} --><h1 class="wp-block-heading h2" style="font-size:clamp(26px, 4vw, 36px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:8px">Privacy Policy</h1><!-- /wp:heading -->')
pv.append('<!-- wp:paragraph {"className":"muted"} --><p class="muted" style="color:var(--muted);font-size:14px;margin-bottom:24px">Last updated: September 2026</p><!-- /wp:paragraph -->')

# Short Version Summary Box
pv.append('<!-- wp:group {"className":"help-summary-box","layout":{"type":"constrained"}} -->')
pv.append('<div class="wp-block-group help-summary-box" style="background:var(--surface);border:1px solid var(--line);border-left:4px solid var(--pink);border-radius:14px;padding:20px 24px;margin-bottom:36px;box-shadow:var(--shadow-1)">')
pv.append('<!-- wp:paragraph --><p style="font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.06em;color:var(--pink);margin-bottom:6px"><strong>SHORT VERSION</strong></p><!-- /wp:paragraph -->')
pv.append('<!-- wp:paragraph --><p style="margin:0;font-size:16px;line-height:1.6;color:var(--ink);font-weight:600">Your public profile is public, everything else is not, and you can leave with your data at any time.</p><!-- /wp:paragraph -->')
pv.append('</div>')
pv.append('<!-- /wp:group -->')

pv.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">1. What We Collect</h2><!-- /wp:heading -->')
pv.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">Account credentials (email, phone, name), published profile and portfolio media, and basic operational activity required to manage studio bookings, messages, and discovery search.</p><!-- /wp:paragraph -->')

pv.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">2. What Stays Private by Default</h2><!-- /wp:heading -->')
pv.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">Your email address, phone number, physical address, identity verification documents, and draft portfolio work are strictly private. Nothing in these fields is ever published to your public profile.</p><!-- /wp:paragraph -->')

pv.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">3. Why We Use It</h2><!-- /wp:heading -->')
pv.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">To connect your skills with relevant opportunities, operate recommendation and discovery signals, keep studio bookings synchronized, and prevent fraud and impersonation.</p><!-- /wp:paragraph -->')

pv.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">4. Verification Documents</h2><!-- /wp:heading -->')
pv.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">Documents submitted for verification (government IDs, studio registration, release credits) are used exclusively by human reviewers to confirm identity. They are permanently removed once a verification badge is granted.</p><!-- /wp:paragraph -->')

pv.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">5. Your Controls & Data Rights</h2><!-- /wp:heading -->')
pv.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">You can edit or unpublish anything at any time, toggle profile visibility, export your portfolio data, or permanently delete your account directly from Settings.</p><!-- /wp:paragraph -->')

pv.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">6. Privacy Inquiries</h2><!-- /wp:heading -->')
pv.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">For any data questions or deletion requests, reach our team at <a href="mailto:privacy@musiclinkd.com" style="color:var(--pink);font-weight:600">privacy@musiclinkd.com</a>.</p><!-- /wp:paragraph -->')

pv.append('<!-- wp:buttons --><div class="wp-block-buttons" style="margin-top:40px"><!-- wp:button {"className":"btn btn--ghost"} --><div class="wp-block-button btn btn--ghost"><a class="wp-block-button__link wp-element-button" href="/" style="font-weight:600;text-decoration:none">← Back to MusicLinkd</a></div><!-- /wp:button --></div><!-- /wp:buttons -->')

pv.append('</main>')
pv.append('<!-- /wp:group -->')

with open('/tmp/gutenberg_blocks/3.html', 'w') as out_f:
    out_f.write('\n'.join(pv))

# 7. Terms of Use (ID 18)
tm = []
tm.append('<!-- wp:group {"tagName":"main","className":"legal-body","layout":{"type":"constrained","contentSize":"820px"}} -->')
tm.append('<main class="wp-block-group legal-body" style="padding-top:calc(var(--nav-h) + 24px);padding-bottom:80px">')

tm.append('<!-- wp:heading {"level":1,"className":"h2 wp-block-heading"} --><h1 class="wp-block-heading h2" style="font-size:clamp(26px, 4vw, 36px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:8px">Terms of Use</h1><!-- /wp:heading -->')
tm.append('<!-- wp:paragraph {"className":"muted"} --><p class="muted" style="color:var(--muted);font-size:14px;margin-bottom:24px">Last updated: September 2026</p><!-- /wp:paragraph -->')

# Short Version Summary Box
tm.append('<!-- wp:group {"className":"help-summary-box","layout":{"type":"constrained"}} -->')
tm.append('<div class="wp-block-group help-summary-box" style="background:var(--surface);border:1px solid var(--line);border-left:4px solid var(--pink);border-radius:14px;padding:20px 24px;margin-bottom:36px;box-shadow:var(--shadow-1)">')
tm.append('<!-- wp:paragraph --><p style="font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.06em;color:var(--pink);margin-bottom:6px"><strong>SHORT VERSION</strong></p><!-- /wp:paragraph -->')
tm.append('<!-- wp:paragraph --><p style="margin:0;font-size:16px;line-height:1.6;color:var(--ink);font-weight:600">Be honest about your work, treat other professionals well, and you keep 100% of the rights to everything you create.</p><!-- /wp:paragraph -->')
tm.append('</div>')
tm.append('<!-- /wp:group -->')

tm.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">1. Your Account & Eligibility</h2><!-- /wp:heading -->')
tm.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">You must be 16 or older and provide accurate information. You are responsible for maintaining the confidentiality of your account credentials.</p><!-- /wp:paragraph -->')

tm.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">2. Your Work Stays Yours (Ownership Guarantee)</h2><!-- /wp:heading -->')
tm.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px"><strong>You retain full, exclusive ownership of every track, audio clip, image, and credit you upload.</strong> You grant MusicLinkd only a non-exclusive license to display and stream that content on the surfaces you choose (your public profile, search, discovery, and shared links).</p><!-- /wp:paragraph -->')

tm.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">3. Professional Conduct</h2><!-- /wp:heading -->')
tm.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">No impersonation, no claiming production credits you did not earn, no off-platform payment pressure, and zero tolerance for harassment. Verification badges protect creators who collaborate in good faith.</p><!-- /wp:paragraph -->')

tm.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">4. Bookings & Marketplace Transactions</h2><!-- /wp:heading -->')
tm.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">Commercial agreements for recording sessions, mixing/mastering, studio time, equipment rentals, and hiring are directly between the contracting parties. MusicLinkd provides the booking schedule, availability tools, and records.</p><!-- /wp:paragraph -->')

tm.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">5. Ending Your Account</h2><!-- /wp:heading -->')
tm.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">You can close your account at any time from Settings. Your public presence and portfolio disappear immediately upon deletion.</p><!-- /wp:paragraph -->')

tm.append('<!-- wp:heading {"level":2,"className":"wp-block-heading"} --><h2 class="wp-block-heading" style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">6. Legal Contact</h2><!-- /wp:heading -->')
tm.append('<!-- wp:paragraph --><p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">For inquiries regarding these terms, reach our legal counsel at <a href="mailto:legal@musiclinkd.com" style="color:var(--pink);font-weight:600">legal@musiclinkd.com</a>.</p><!-- /wp:paragraph -->')

tm.append('<!-- wp:buttons --><div class="wp-block-buttons" style="margin-top:40px"><!-- wp:button {"className":"btn btn--ghost"} --><div class="wp-block-button btn btn--ghost"><a class="wp-block-button__link wp-element-button" href="/" style="font-weight:600;text-decoration:none">← Back to MusicLinkd</a></div><!-- /wp:button --></div><!-- /wp:buttons -->')

tm.append('</main>')
tm.append('<!-- /wp:group -->')

with open('/tmp/gutenberg_blocks/18.html', 'w') as out_f:
    out_f.write('\n'.join(tm))

print("All 13 pages converted to Gutenberg blocks in /tmp/gutenberg_blocks/")
