<?php
/**
 * Create/Update MusicLinkd Premium Editorial Email Template in FluentCRM.
 */

if (!defined('ABSPATH')) {
    exit;
}

$template_html = file_get_contents(__DIR__ . '/../templates/email/template.html');

$template_data = [
    'post_title'   => 'MusicLinkd Premium Editorial Onboarding',
    'post_content' => $template_html,
    'post_excerpt' => 'Really Good Emails-inspired premium onboarding with header, activation grid, pro tips, social handles, and canonical footer.',
    'post_type'    => function_exists('fluentcrmTemplateCPTSlug') ? fluentcrmTemplateCPTSlug() : 'fc_template',
    'post_status'  => 'publish'
];

$existing = get_posts([
    'post_type'   => $template_data['post_type'],
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

update_post_meta($template_id, '_email_subject', 'Welcome to MusicLinkd — Your Sound. Verified.');
update_post_meta($template_id, '_edit_type', 'block');
update_post_meta($template_id, '_design_template', 'simple');
update_post_meta($template_id, '_template_config', [
    'content_width'       => '640',
    'body_bg_color'       => '#0A0A0E',
    'content_bg_color'    => '#0F0F14',
    'content_font_family' => "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
    'text_color'          => '#D1D5DB',
    'link_color'          => '#FF005C'
]);

echo "Premium template ID {$template_id} successfully synced with FluentCRM.\n";
