#!/usr/bin/env python3
"""
Updates Page 14 (/login/) and Page 13 (/register/) in WordPress
from obsolete broken shortcodes ([wppb-login], [wppb-register])
into native Gutenberg editorial block layouts matching the MusicLinkd design system.
"""

LOGIN_CONTENT = """<!-- wp:group {"style":{"spacing":{"padding":{"top":"var:preset|spacing|50","bottom":"var:preset|spacing|60","left":"var:preset|spacing|30","right":"var:preset|spacing|30"}}},"layout":{"type":"constrained","contentSize":"640px"}} -->
<div class="wp-block-group" style="padding-top:var(--wp--preset--spacing--50);padding-right:var(--wp--preset--spacing--30);padding-bottom:var(--wp--preset--spacing--60);padding-left:var(--wp--preset--spacing--30)">

  <!-- wp:group {"style":{"spacing":{"blockGap":"var:preset|spacing|20","margin":{"bottom":"var:preset|spacing|40"}}},"layout":{"type":"flex","orientation":"vertical","justifyContent":"center"}} -->
  <div class="wp-block-group" style="margin-bottom:var(--wp--preset--spacing--40);text-align:center">
    <!-- wp:paragraph {"style":{"typography":{"textTransform":"uppercase","letterSpacing":"0.1em","fontSize":"12px","fontWeight":"700"}},"textColor":"vivid-pink"} -->
    <p class="has-vivid-pink-color has-text-color" style="font-size:12px;font-weight:700;letter-spacing:0.1em;text-transform:uppercase">Single Sign-On &amp; Auth</p>
    <!-- /wp:paragraph -->

    <!-- wp:heading {"textAlign":"center","level":1,"style":{"typography":{"fontSize":"36px","letterSpacing":"-0.02em","lineHeight":"1.15"}}} -->
    <h1 class="wp-block-heading has-text-align-center" style="font-size:36px;line-height:1.15;letter-spacing:-0.02em">Sign In to MusicLinkd</h1>
    <!-- /wp:heading -->

    <!-- wp:paragraph {"textAlign":"center","style":{"typography":{"fontSize":"16px","lineHeight":"1.5"}},"textColor":"muted-foreground"} -->
    <p class="has-text-align-center has-muted-foreground-color has-text-color" style="font-size:16px;line-height:1.5">Access your creator portfolio, booking calendar, client inquiries, and verified network profile.</p>
    <!-- /wp:paragraph -->
  </div>
  <!-- /wp:group -->

  <!-- wp:group {"style":{"spacing":{"padding":{"top":"32px","bottom":"32px","left":"32px","right":"32px"}},"border":{"radius":"16px","width":"1px","color":"#E5E7EB"},"color":{"background":"#FFFFFF"}},"layout":{"type":"constrained"}} -->
  <div class="wp-block-group has-background" style="border-color:#E5E7EB;border-width:1px;border-radius:16px;background-color:#FFFFFF;padding-top:32px;padding-right:32px;padding-bottom:32px;padding-left:32px;box-shadow:0 4px 20px rgba(0,0,0,0.04)">
    
    <!-- wp:paragraph {"style":{"typography":{"fontSize":"15px","lineHeight":"1.5"}}} -->
    <p style="font-size:15px;line-height:1.5">MusicLinkd authenticates securely through our dedicated web and mobile application portal. Choose your preferred method to sign in:</p>
    <!-- /wp:paragraph -->

    <!-- wp:buttons {"layout":{"type":"flex","orientation":"vertical"},"style":{"spacing":{"blockGap":"14px","margin":{"top":"24px","bottom":"24px"}}}} -->
    <div class="wp-block-buttons" style="margin-top:24px;margin-bottom:24px">
      <!-- wp:button {"width":100,"style":{"color":{"background":"#FF005C","text":"#FFFFFF"},"border":{"radius":"9999px"},"typography":{"fontWeight":"700","fontSize":"15px"}}} -->
      <div class="wp-block-button has-custom-width wp-block-button__width-100"><a class="wp-block-button__link has-text-color has-background wp-element-button" href="https://app.musiclinkd.com/auth" style="border-radius:9999px;background-color:#FF005C;color:#FFFFFF;font-size:15px;font-weight:700;padding:12px 24px;text-align:center;display:block">Open App Sign In →</a></div>
      <!-- /wp:button -->

      <!-- wp:button {"width":100,"className":"is-style-outline","style":{"border":{"radius":"9999px","color":"#D1D5DB","width":"1px"},"color":{"text":"#111827"},"typography":{"fontWeight":"600","fontSize":"14px"}}} -->
      <div class="wp-block-button has-custom-width wp-block-button__width-100 is-style-outline"><a class="wp-block-button__link has-text-color wp-element-button" href="https://app.musiclinkd.com/auth/phone" style="border-color:#D1D5DB;border-width:1px;border-radius:9999px;color:#111827;font-size:14px;font-weight:600;padding:11px 24px;text-align:center;display:block">Sign in with Mobile OTP</a></div>
      <!-- /wp:button -->
    </div>
    <!-- /wp:buttons -->

    <!-- wp:separator {"style":{"spacing":{"margin":{"top":"20px","bottom":"20px"}}},"backgroundColor":"border-light"} -->
    <hr class="wp-block-separator has-text-color has-border-light-color has-alpha-channel-opacity has-border-light-background-color has-background" style="margin-top:20px;margin-bottom:20px"/>
    <!-- /wp:separator -->

    <!-- wp:paragraph {"textAlign":"center","style":{"typography":{"fontSize":"13px"}},"textColor":"muted-foreground"} -->
    <p class="has-text-align-center has-muted-foreground-color has-text-color" style="font-size:13px">Don't have an account yet? <a href="/register/" style="color:#FF005C;font-weight:600">Join Early Access</a> · <a href="/help/" style="color:#4B5563">Need help?</a></p>
    <!-- /wp:paragraph -->

  </div>
  <!-- /wp:group -->

</div>
<!-- /wp:group -->"""

REGISTER_CONTENT = """<!-- wp:group {"style":{"spacing":{"padding":{"top":"var:preset|spacing|50","bottom":"var:preset|spacing|60","left":"var:preset|spacing|30","right":"var:preset|spacing|30"}}},"layout":{"type":"constrained","contentSize":"840px"}} -->
<div class="wp-block-group" style="padding-top:var(--wp--preset--spacing--50);padding-right:var(--wp--preset--spacing--30);padding-bottom:var(--wp--preset--spacing--60);padding-left:var(--wp--preset--spacing--30)">

  <!-- wp:group {"style":{"spacing":{"blockGap":"var:preset|spacing|20","margin":{"bottom":"var:preset|spacing|40"}}},"layout":{"type":"flex","orientation":"vertical","justifyContent":"center"}} -->
  <div class="wp-block-group" style="margin-bottom:var(--wp--preset--spacing--40);text-align:center">
    <!-- wp:paragraph {"style":{"typography":{"textTransform":"uppercase","letterSpacing":"0.1em","fontSize":"12px","fontWeight":"700"}},"textColor":"vivid-pink"} -->
    <p class="has-vivid-pink-color has-text-color" style="font-size:12px;font-weight:700;letter-spacing:0.1em;text-transform:uppercase">Early Access Network</p>
    <!-- /wp:paragraph -->

    <!-- wp:heading {"textAlign":"center","level":1,"style":{"typography":{"fontSize":"36px","letterSpacing":"-0.02em","lineHeight":"1.15"}}} -->
    <h1 class="wp-block-heading has-text-align-center" style="font-size:36px;line-height:1.15;letter-spacing:-0.02em">Join MusicLinkd</h1>
    <!-- /wp:heading -->

    <!-- wp:paragraph {"textAlign":"center","style":{"typography":{"fontSize":"16px","lineHeight":"1.5"}},"textColor":"muted-foreground"} -->
    <p class="has-text-align-center has-muted-foreground-color has-text-color" style="font-size:16px;line-height:1.5">Join India's dedicated network for artists, studios, engineers, and music businesses.</p>
    <!-- /wp:paragraph -->
  </div>
  <!-- /wp:group -->

  <!-- wp:columns {"style":{"spacing":{"blockGap":"24px","margin":{"bottom":"32px"}}}} -->
  <div class="wp-block-columns" style="margin-bottom:32px">

    <!-- wp:column {"style":{"spacing":{"padding":{"top":"28px","bottom":"28px","left":"28px","right":"28px"}},"border":{"radius":"16px","width":"1px","color":"#E5E7EB"},"color":{"background":"#FFFFFF"}}} -->
    <div class="wp-block-column has-background" style="border-color:#E5E7EB;border-width:1px;border-radius:16px;background-color:#FFFFFF;padding-top:28px;padding-right:28px;padding-bottom:28px;padding-left:28px">
      <!-- wp:paragraph {"style":{"typography":{"fontSize":"13px","fontWeight":"700","textTransform":"uppercase"}},"textColor":"vivid-pink"} -->
      <p class="has-vivid-pink-color has-text-color" style="font-size:13px;font-weight:700;text-transform:uppercase">For Individuals</p>
      <!-- /wp:paragraph -->

      <!-- wp:heading {"level":3,"style":{"typography":{"fontSize":"20px","fontWeight":"700"}}} -->
      <h3 class="wp-block-heading" style="font-size:20px;font-weight:700">Artists &amp; Talent</h3>
      <!-- /wp:heading -->

      <!-- wp:paragraph {"style":{"typography":{"fontSize":"14px","lineHeight":"1.5"}},"textColor":"muted-foreground"} -->
      <p class="has-muted-foreground-color has-text-color" style="font-size:14px;line-height:1.5">Musicians, producers, recording engineers, lyricists, mixing engineers, and session players.</p>
      <!-- /wp:paragraph -->

      <!-- wp:buttons {"style":{"spacing":{"margin":{"top":"20px"}}}} -->
      <div class="wp-block-buttons" style="margin-top:20px">
        <!-- wp:button {"width":100,"style":{"color":{"background":"#FF005C","text":"#FFFFFF"},"border":{"radius":"9999px"},"typography":{"fontWeight":"700","fontSize":"14px"}}} -->
        <div class="wp-block-button has-custom-width wp-block-button__width-100"><a class="wp-block-button__link has-text-color has-background wp-element-button" href="https://app.musiclinkd.com/onboarding" style="border-radius:9999px;background-color:#FF005C;color:#FFFFFF;font-size:14px;font-weight:700;padding:10px 20px;text-align:center;display:block">Get Started as Creator →</a></div>
        <!-- /wp:button -->
      </div>
      <!-- /wp:buttons -->
    </div>
    <!-- /wp:column -->

    <!-- wp:column {"style":{"spacing":{"padding":{"top":"28px","bottom":"28px","left":"28px","right":"28px"}},"border":{"radius":"16px","width":"1px","color":"#E5E7EB"},"color":{"background":"#FFFFFF"}}} -->
    <div class="wp-block-column has-background" style="border-color:#E5E7EB;border-width:1px;border-radius:16px;background-color:#FFFFFF;padding-top:28px;padding-right:28px;padding-bottom:28px;padding-left:28px">
      <!-- wp:paragraph {"style":{"typography":{"fontSize":"13px","fontWeight":"700","textTransform":"uppercase"}},"textColor":"vivid-pink"} -->
      <p class="has-vivid-pink-color has-text-color" style="font-size:13px;font-weight:700;text-transform:uppercase">For Businesses</p>
      <!-- /wp:paragraph -->

      <!-- wp:heading {"level":3,"style":{"typography":{"fontSize":"20px","fontWeight":"700"}}} -->
      <h3 class="wp-block-heading" style="font-size:20px;font-weight:700">Studios &amp; Companies</h3>
      <!-- /wp:heading -->

      <!-- wp:paragraph {"style":{"typography":{"fontSize":"14px","lineHeight":"1.5"}},"textColor":"muted-foreground"} -->
      <p class="has-muted-foreground-color has-text-color" style="font-size:14px;line-height:1.5">Recording facilities, rehearsal spaces, gear rental houses, labels, and event venues.</p>
      <!-- /wp:paragraph -->

      <!-- wp:buttons {"style":{"spacing":{"margin":{"top":"20px"}}}} -->
      <div class="wp-block-buttons" style="margin-top:20px">
        <!-- wp:button {"width":100,"className":"is-style-outline","style":{"border":{"radius":"9999px","color":"#111827","width":"1px"},"color":{"text":"#111827"},"typography":{"fontWeight":"700","fontSize":"14px"}}} -->
        <div class="wp-block-button has-custom-width wp-block-button__width-100 is-style-outline"><a class="wp-block-button__link has-text-color wp-element-button" href="https://app.musiclinkd.com/onboarding" style="border-color:#111827;border-width:1px;border-radius:9999px;color:#111827;font-size:14px;font-weight:700;padding:9px 20px;text-align:center;display:block">Register Studio Space →</a></div>
        <!-- /wp:button -->
      </div>
      <!-- /wp:buttons -->
    </div>
    <!-- /wp:column -->

  </div>
  <!-- /wp:columns -->

  <!-- wp:group {"style":{"spacing":{"padding":{"top":"32px","bottom":"32px","left":"32px","right":"32px"}},"border":{"radius":"16px","width":"1px","color":"#E5E7EB"},"color":{"background":"#FFFFFF"}},"layout":{"type":"constrained"}} -->
  <div class="wp-block-group has-background" style="border-color:#E5E7EB;border-width:1px;border-radius:16px;background-color:#FFFFFF;padding-top:32px;padding-right:32px;padding-bottom:32px;padding-left:32px">
    <!-- wp:heading {"textAlign":"center","level":3,"style":{"typography":{"fontSize":"18px","fontWeight":"700"}}} -->
    <h3 class="wp-block-heading has-text-align-center" style="font-size:18px;font-weight:700;margin-bottom:8px">Prefer Email Updates First?</h3>
    <!-- /wp:heading -->

    <!-- wp:paragraph {"textAlign":"center","style":{"typography":{"fontSize":"14px"}},"textColor":"muted-foreground"} -->
    <p class="has-text-align-center has-muted-foreground-color has-text-color" style="font-size:14px;margin-bottom:20px">Subscribe to Early Access updates, beta invites, and creator spotlights:</p>
    <!-- /wp:paragraph -->

    <!-- wp:fluentform/simple-form-block {"formId":"2"} /-->
  </div>
  <!-- /wp:group -->

  <!-- wp:paragraph {"textAlign":"center","style":{"typography":{"fontSize":"13px"},"spacing":{"margin":{"top":"24px"}}},"textColor":"muted-foreground"} -->
  <p class="has-text-align-center has-muted-foreground-color has-text-color" style="font-size:13px;margin-top:24px">Already have an account? <a href="/login/" style="color:#FF005C;font-weight:600">Sign in here</a></p>
  <!-- /wp:paragraph -->

</div>
<!-- /wp:group -->"""

import os
import subprocess

def deploy_pages():
    login_tmp = "/tmp/ml_page_14.txt"
    register_tmp = "/tmp/ml_page_13.txt"
    
    with open(login_tmp, "w", encoding="utf-8") as f:
        f.write(LOGIN_CONTENT)
    with open(register_tmp, "w", encoding="utf-8") as f:
        f.write(REGISTER_CONTENT)
        
    print("Uploading updated pages to Hostinger...")
    subprocess.run(["scp", "-i", os.path.expanduser("~/.ssh/hostinger_aw"), "-P", "65002", login_tmp, "u544169507@46.28.45.135:/tmp/ml_page_14.txt"], check=True)
    subprocess.run(["scp", "-i", os.path.expanduser("~/.ssh/hostinger_aw"), "-P", "65002", register_tmp, "u544169507@46.28.45.135:/tmp/ml_page_13.txt"], check=True)
    
    cmd = (
        "wp post update 14 /tmp/ml_page_14.txt --post_title='Sign In to MusicLinkd' --path=/home/u544169507/domains/musiclinkd.com/public_html && "
        "wp post update 13 /tmp/ml_page_13.txt --post_title='Join MusicLinkd Early Access' --path=/home/u544169507/domains/musiclinkd.com/public_html && "
        "wp cache flush --path=/home/u544169507/domains/musiclinkd.com/public_html"
    )
    res = subprocess.run(["ssh", "-i", os.path.expanduser("~/.ssh/hostinger_aw"), "-p", "65002", "-o", "StrictHostKeyChecking=no", "u544169507@46.28.45.135", cmd], capture_output=True, text=True)
    print("Hostinger WP-CLI Output:\n", res.stdout)
    if res.stderr:
        print("Stderr:\n", res.stderr)

if __name__ == "__main__":
    deploy_pages()
