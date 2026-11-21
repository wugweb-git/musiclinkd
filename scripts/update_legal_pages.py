import subprocess, tempfile

privacy_content = """<!-- wp:html -->
<main class="legal-body" role="main">
  <div class="container" style="max-width:820px;padding-top:calc(var(--nav-h) + 24px);padding-bottom:80px">
    <div class="legal-hero" style="margin-bottom:28px">
      <a href="/" style="font-family:var(--font-display);font-weight:800;font-size:22px;letter-spacing:-0.05em;text-decoration:none;color:var(--text-primary);display:inline-flex;align-items:center;gap:10px;margin-bottom:24px">MusicLink<em style="color:var(--brand-pink);font-style:normal">d</em></a>
      <h1 class="h2" style="font-size:clamp(26px, 4vw, 36px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:8px">Privacy Policy</h1>
      <p class="muted" style="color:var(--muted);font-size:14px;margin:0">Last updated: September 2026</p>
    </div>

    <!-- Short Version Summary Box -->
    <div class="help-summary-box" style="background:var(--surface);border:1px solid var(--line);border-left:4px solid var(--pink);border-radius:14px;padding:20px 24px;margin-bottom:36px;box-shadow:var(--shadow-1)">
      <b style="display:block;font-size:12px;text-transform:uppercase;letter-spacing:0.06em;color:var(--pink);margin-bottom:6px">Short Version</b>
      <p style="margin:0;font-size:16px;line-height:1.6;color:var(--ink);font-weight:600">Your public profile is public, everything else is not, and you can leave with your data at any time.</p>
    </div>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">1. What We Collect</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">Account credentials (email, phone, name), published profile and portfolio media, and basic operational activity required to manage studio bookings, messages, and discovery search.</p>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">2. What Stays Private by Default</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">Your email address, phone number, physical address, identity verification documents, and draft portfolio work are strictly private. Nothing in these fields is ever published to your public profile.</p>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">3. Why We Use It</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">To connect your skills with relevant opportunities, operate recommendation and discovery signals, keep studio bookings synchronized, and prevent fraud and impersonation.</p>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">4. Verification Documents</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">Documents submitted for verification (government IDs, studio registration, release credits) are used exclusively by human reviewers to confirm identity. They are permanently removed once a verification badge is granted.</p>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">5. Your Controls & Data Rights</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">You can edit or unpublish anything at any time, toggle profile visibility, export your portfolio data, or permanently delete your account directly from Settings.</p>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">6. Privacy Inquiries</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">For any data questions or deletion requests, reach our team at <a href="mailto:privacy@musiclinkd.com" style="color:var(--pink);font-weight:600">privacy@musiclinkd.com</a>.</p>

    <p style="margin-top:40px"><a href="/" class="btn btn--ghost" style="font-weight:600;text-decoration:none">← Back to MusicLinkd</a></p>
  </div>
</main>
<!-- /wp:html -->"""

terms_content = """<!-- wp:html -->
<main class="legal-body" role="main">
  <div class="container" style="max-width:820px;padding-top:calc(var(--nav-h) + 24px);padding-bottom:80px">
    <div class="legal-hero" style="margin-bottom:28px">
      <a href="/" style="font-family:var(--font-display);font-weight:800;font-size:22px;letter-spacing:-0.05em;text-decoration:none;color:var(--text-primary);display:inline-flex;align-items:center;gap:10px;margin-bottom:24px">MusicLink<em style="color:var(--brand-pink);font-style:normal">d</em></a>
      <h1 class="h2" style="font-size:clamp(26px, 4vw, 36px);font-weight:800;letter-spacing:-0.03em;color:var(--ink);margin-bottom:8px">Terms of Use</h1>
      <p class="muted" style="color:var(--muted);font-size:14px;margin:0">Last updated: September 2026</p>
    </div>

    <!-- Short Version Summary Box -->
    <div class="help-summary-box" style="background:var(--surface);border:1px solid var(--line);border-left:4px solid var(--pink);border-radius:14px;padding:20px 24px;margin-bottom:36px;box-shadow:var(--shadow-1)">
      <b style="display:block;font-size:12px;text-transform:uppercase;letter-spacing:0.06em;color:var(--pink);margin-bottom:6px">Short Version</b>
      <p style="margin:0;font-size:16px;line-height:1.6;color:var(--ink);font-weight:600">Be honest about your work, treat other professionals well, and you keep 100% of the rights to everything you create.</p>
    </div>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">1. Your Account & Eligibility</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">You must be 16 or older and provide accurate information. You are responsible for maintaining the confidentiality of your account credentials.</p>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">2. Your Work Stays Yours (Ownership Guarantee)</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px"><b>You retain full, exclusive ownership of every track, audio clip, image, and credit you upload.</b> You grant MusicLinkd only a non-exclusive license to display and stream that content on the surfaces you choose (your public profile, search, discovery, and shared links).</p>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">3. Professional Conduct</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">No impersonation, no claiming production credits you did not earn, no off-platform payment pressure, and zero tolerance for harassment. Verification badges protect creators who collaborate in good faith.</p>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">4. Bookings & Marketplace Transactions</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">Commercial agreements for recording sessions, mixing/mastering, studio time, equipment rentals, and hiring are directly between the contracting parties. MusicLinkd provides the booking schedule, availability tools, and records.</p>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">5. Ending Your Account</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">You can close your account at any time from Settings. Your public presence and portfolio disappear immediately upon deletion.</p>

    <h2 style="font-size:22px;font-weight:700;margin-top:28px;margin-bottom:12px">6. Legal Contact</h2>
    <p style="font-size:15px;line-height:1.65;color:var(--muted);margin-bottom:16px">For inquiries regarding these terms, reach our legal counsel at <a href="mailto:legal@musiclinkd.com" style="color:var(--pink);font-weight:600">legal@musiclinkd.com</a>.</p>

    <p style="margin-top:40px"><a href="/" class="btn btn--ghost" style="font-weight:600;text-decoration:none">← Back to MusicLinkd</a></p>
  </div>
</main>
<!-- /wp:html -->"""

with tempfile.NamedTemporaryFile('w', delete=False) as tf1:
    tf1.write(privacy_content)
    subprocess.call(['wp', 'post', 'update', '3', tf1.name, '--post_title=Privacy Policy', '--post_status=publish'])

with tempfile.NamedTemporaryFile('w', delete=False) as tf2:
    tf2.write(terms_content)
    subprocess.call(['wp', 'post', 'update', '18', tf2.name, '--post_title=Terms of Use', '--post_status=publish'])

print('Privacy Policy and Terms of Use updated successfully.')
