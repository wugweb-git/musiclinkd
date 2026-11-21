<?php
/**
 * Create MusicLinkd Official Clean Editorial Email Template in FluentCRM.
 */

if (!defined('ABSPATH')) {
    exit;
}

$template_content = <<<'HTML'
<!-- wp:paragraph {"align":"center"} -->
<p class="has-text-align-center" style="margin:24px 0 16px 0;"><a href="https://musiclinkd.com" target="_blank" rel="noopener"><img src="https://musiclinkd.com/wp-content/themes/musiclinkd/assets/img/brand/logo-lockup-black.svg" alt="MusicLinkd" width="180" style="display:inline-block;max-width:180px;height:auto;" /></a></p>
<!-- /wp:paragraph -->

<!-- wp:separator {"className":"is-style-wide"} -->
<hr class="wp-block-separator has-alpha-channel-opacity is-style-wide" style="margin:20px 0 28px 0;border-top:1px solid #E5E7EB;" />
<!-- /wp:separator -->

<!-- wp:heading {"level":2} -->
<h2 class="wp-block-heading" style="font-size:22px;font-weight:700;color:#111827;letter-spacing:-0.02em;margin-top:0;margin-bottom:12px;">Hello ##contact.first_name##,</h2>
<!-- /wp:heading -->

<!-- wp:paragraph -->
<p style="font-size:15px;line-height:1.65;color:#374151;margin-bottom:16px;">Welcome to <strong>MusicLinkd</strong> — India’s verified professional ecosystem and marketplace connecting artists, audio engineers, music producers, recording studios, session talent, and music businesses.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p style="font-size:15px;line-height:1.65;color:#374151;margin-bottom:20px;">We built MusicLinkd to replace fragmented messaging groups with one verified digital identity, seamless booking schedules, transparent rates, and trusted collaboration spaces.</p>
<!-- /wp:paragraph -->

<!-- wp:group {"style":{"border":{"left":{"color":"#FF005C","width":"4px"}},"spacing":{"padding":{"top":"16px","right":"20px","bottom":"16px","left":"20px"}}},"backgroundColor":"background"} -->
<div class="wp-block-group" style="border-left:4px solid #FF005C;background:#F9FAFB;padding:16px 20px;border-radius:10px;margin:24px 0;">
<p style="font-size:14px;line-height:1.6;color:#1F2937;margin:0;">💡 <strong>Pro Tip for Getting Booked:</strong> Creators who upload at least two verified audio samples and define their rate range receive three times more direct inquiry messages.</p>
</div>
<!-- /wp:group -->

<!-- wp:buttons {"layout":{"type":"flex","justifyContent":"center"}} -->
<div class="wp-block-buttons" style="margin:36px 0;display:flex;justify-content:center;">
<!-- wp:button {"style":{"color":{"background":"#FF005C","text":"#ffffff"},"border":{"radius":"28px"}}} -->
<div class="wp-block-button"><a class="wp-block-button__link wp-element-button" href="https://musiclinkd.com/register/" style="background-color:#FF005C;color:#ffffff;border-radius:28px;padding:14px 32px;text-decoration:none;font-weight:600;font-size:15px;display:inline-block;">Access Your Profile & Dashboard →</a></div>
<!-- /wp:button -->
</div>
<!-- /wp:buttons -->

<!-- wp:paragraph -->
<p style="font-size:14px;line-height:1.6;color:#6B7280;margin-top:28px;margin-bottom:8px;">Need assistance setting up your portfolio or verifying your credentials?</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p style="font-size:14px;line-height:1.6;color:#6B7280;margin-top:0;">Browse our step-by-step guides in the <a href="https://musiclinkd.com/help/" style="color:#FF005C;font-weight:600;text-decoration:none;">Help Centre</a> or reply directly to this email to reach our creator operations desk.</p>
<!-- /wp:paragraph -->

<!-- wp:separator {"className":"is-style-wide"} -->
<hr class="wp-block-separator has-alpha-channel-opacity is-style-wide" style="margin:32px 0 24px 0;border-top:1px solid #E5E7EB;" />
<!-- /wp:separator -->

<!-- wp:paragraph {"align":"center","fontSize":"small"} -->
<p class="has-text-align-center has-small-font-size" style="font-size:12px;line-height:1.6;color:#9CA3AF;margin:0;">
<strong>MusicLinkd</strong> · The verified creative network and marketplace for the music industry<br />
<a href="https://musiclinkd.com/help/" style="color:#9CA3AF;text-decoration:underline;">Help Centre</a> · 
<a href="https://musiclinkd.com/pricing/" style="color:#9CA3AF;text-decoration:underline;">Pricing</a> · 
<a href="https://musiclinkd.com/terms/" style="color:#9CA3AF;text-decoration:underline;">Terms</a> · 
<a href="https://musiclinkd.com/privacy/" style="color:#9CA3AF;text-decoration:underline;">Privacy Policy</a><br /><br />
##crm.business_name## · ##crm.business_address##<br />
You received this because you are a registered member of MusicLinkd. <a href="##crm.unsubscribe_url##" style="color:#FF005C;text-decoration:underline;">Unsubscribe</a>
</p>
<!-- /wp:paragraph -->
HTML;

$template_data = [
    'post_title'   => 'MusicLinkd Clean Editorial Template',
    'post_content' => $template_content,
    'post_excerpt' => 'Canonical, clean editorial email template for welcome emails, notifications, and updates.',
    'post_type'    => function_exists('fluentcrmTemplateCPTSlug') ? fluentcrmTemplateCPTSlug() : 'fc_template',
    'post_status'  => 'publish'
];

$existing = get_posts([
    'post_type'   => $template_data['post_type'],
    'title'       => $template_data['post_title'],
    'post_status' => 'any',
    'numberposts' => 1
]);

if (!empty($existing)) {
    $template_id = $existing[0]->ID;
    $template_data['ID'] = $template_id;
    wp_update_post($template_data);
    echo "Updated existing FluentCRM Template ID: " . $template_id . "\n";
} else {
    $template_id = wp_insert_post($template_data);
    echo "Created new FluentCRM Template ID: " . $template_id . "\n";
}

update_post_meta($template_id, '_email_subject', 'Welcome to MusicLinkd — Your Verified Creative Network');
update_post_meta($template_id, '_edit_type', 'block');
update_post_meta($template_id, '_design_template', 'simple');
update_post_meta($template_id, '_template_config', [
    'content_width'       => '640',
    'body_bg_color'       => '#F4F5F7',
    'content_bg_color'    => '#FFFFFF',
    'content_font_family' => "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
    'text_color'          => '#374151',
    'link_color'          => '#FF005C'
]);

echo "Template configured successfully for FluentCRM.\n";
