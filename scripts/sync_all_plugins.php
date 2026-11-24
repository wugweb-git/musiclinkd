<?php
/**
 * MusicLinkd Production All-Plugin Sync & Configuration Script
 * Configures FluentCRM, FluentSMTP, Fluent Forms, Rank Math SEO, and PWA for WP.
 */

if (!defined('ABSPATH')) {
    exit;
}

echo "=== 1. CONFIGURING FLUENTCRM ===\n";

$globalSettings = get_option('fluentcrm-global-settings', []);
if (!is_array($globalSettings)) {
    $globalSettings = [];
}

$globalSettings['business_settings'] = [
    'business_name'    => 'MusicLinkd',
    'admin_email'      => 'contact@musiclinkd.com',
    'business_address' => "MusicLinkd Creator Desk\nIndia\ncontact@musiclinkd.com",
    'logo_url'         => 'https://musiclinkd.com/wp-content/themes/musiclinkd/assets/img/brand/logo-badge-inverse.svg'
];

$globalSettings['campaign'] = [
    'from' => [
        'name'     => 'MusicLinkd Creator Desk',
        'email'    => 'contact@musiclinkd.com',
        'reply_to' => 'contact@musiclinkd.com'
    ],
    'footer_text' => "© " . date('Y') . " MusicLinkd. India's Verified Creative Network & Booking Marketplace."
];

$globalSettings['email'] = [
    'emails_per_second' => 4,
    'email_footer'      => "© " . date('Y') . " MusicLinkd · India's Verified Creative Network & Booking Marketplace\n##crm.unsubscribe_url##"
];

update_option('fluentcrm-global-settings', $globalSettings);
echo "FluentCRM global settings updated.\n";

// Ensure fc_meta options match
if (function_exists('fluentcrm_update_option')) {
    fluentcrm_update_option('business_settings', $globalSettings['business_settings']);
    fluentcrm_update_option('email_settings', $globalSettings['email']);
    echo "FluentCRM fc_meta synced.\n";
}

echo "\n=== 2. CONFIGURING FLUENT FORMS ===\n";
global $wpdb;

// Form 1: Creator Application & Inquiries
$form1Fields = [
    'fields' => [
        [
            'index' => 0,
            'element' => 'input_name',
            'attributes' => ['name' => 'names', 'data-type' => 'name-element'],
            'settings' => ['container_class' => '', 'admin_field_label' => 'Name', 'conditional_logics' => []],
            'fields' => [
                'first_name' => [
                    'element' => 'input_text',
                    'attributes' => ['type' => 'text', 'name' => 'first_name', 'placeholder' => 'First Name'],
                    'settings' => ['container_class' => '', 'label' => 'First Name', 'visible' => true, 'validation_rules' => ['required' => ['value' => true, 'message' => 'First name is required']]]
                ],
                'last_name' => [
                    'element' => 'input_text',
                    'attributes' => ['type' => 'text', 'name' => 'last_name', 'placeholder' => 'Last Name'],
                    'settings' => ['container_class' => '', 'label' => 'Last Name', 'visible' => true, 'validation_rules' => ['required' => ['value' => false, 'message' => '']]]
                ]
            ],
            'editor_options' => ['title' => 'Name Fields', 'element' => 'name-fields', 'template' => 'nameFields'],
            'uniqElKey' => 'el_name'
        ],
        [
            'index' => 1,
            'element' => 'input_email',
            'attributes' => ['type' => 'email', 'name' => 'email', 'placeholder' => 'your@email.com'],
            'settings' => ['container_class' => '', 'label' => 'Email Address', 'validation_rules' => ['required' => ['value' => true, 'message' => 'Email is required'], 'email' => ['value' => true, 'message' => 'Please enter a valid email']]],
            'editor_options' => ['title' => 'Email Address', 'template' => 'inputText'],
            'uniqElKey' => 'el_email'
        ],
        [
            'index' => 2,
            'element' => 'select',
            'attributes' => ['name' => 'creator_category'],
            'settings' => [
                'container_class' => '',
                'label' => 'Primary Discipline / Role',
                'placeholder' => 'Select your discipline...',
                'advanced_options' => [
                    ['label' => 'Music Producer / Beatmaker', 'value' => 'Music Producer'],
                    ['label' => 'Vocalist / Singer-Songwriter', 'value' => 'Vocalist'],
                    ['label' => 'Mixing & Mastering Engineer', 'value' => 'Audio Engineer'],
                    ['label' => 'Sound Designer / Composer', 'value' => 'Sound Designer'],
                    ['label' => 'Session Instrumentalist', 'value' => 'Instrumentalist'],
                    ['label' => 'Label / Brand / Client Booking', 'value' => 'Client Booking']
                ],
                'validation_rules' => ['required' => ['value' => true, 'message' => 'Please select your role']]
            ],
            'editor_options' => ['title' => 'Dropdown', 'template' => 'selectField'],
            'uniqElKey' => 'el_category'
        ],
        [
            'index' => 3,
            'element' => 'input_text',
            'attributes' => ['type' => 'url', 'name' => 'portfolio_url', 'placeholder' => 'https://spotify.com, soundcloud.com, or portfolio'],
            'settings' => ['container_class' => '', 'label' => 'Portfolio / Audio Sample Link', 'validation_rules' => ['required' => ['value' => false, 'message' => '']]],
            'editor_options' => ['title' => 'Simple Text', 'template' => 'inputText'],
            'uniqElKey' => 'el_portfolio'
        ],
        [
            'index' => 4,
            'element' => 'textarea',
            'attributes' => ['name' => 'message', 'placeholder' => 'Tell us about your work or inquiry...', 'rows' => 4],
            'settings' => ['container_class' => '', 'label' => 'Message / Project Details', 'validation_rules' => ['required' => ['value' => true, 'message' => 'Please include a message']]],
            'editor_options' => ['title' => 'Text Area', 'template' => 'inputTextarea'],
            'uniqElKey' => 'el_message'
        ]
    ],
    'submitButton' => [
        'uniqElKey' => 'el_submit',
        'element' => 'button',
        'attributes' => ['type' => 'submit', 'class' => 'ml-btn ml-btn--primary'],
        'settings' => [
            'align' => 'left',
            'button_style' => 'default',
            'background_color' => '#EA4C89',
            'button_size' => 'md',
            'color' => '#ffffff',
            'button_ui' => ['type' => 'default', 'text' => 'Submit Creator Inquiry', 'img_url' => '']
        ]
    ]
];

$wpdb->update(
    $wpdb->prefix . 'fluentform_forms',
    [
        'title' => 'MusicLinkd Creator Application & Inquiries',
        'form_fields' => json_encode($form1Fields),
        'status' => 'published',
        'updated_at' => current_time('mysql')
    ],
    ['id' => 1]
);
echo "FluentForm ID 1 updated to 'MusicLinkd Creator Application & Inquiries'.\n";

// Form 2: Newsletter & Early Access
$form2Fields = [
    'fields' => [
        [
            'index' => 0,
            'element' => 'input_text',
            'attributes' => ['type' => 'text', 'name' => 'first_name', 'placeholder' => 'First Name'],
            'settings' => ['container_class' => '', 'label' => 'First Name', 'validation_rules' => ['required' => ['value' => false, 'message' => '']]],
            'editor_options' => ['title' => 'First Name', 'template' => 'inputText'],
            'uniqElKey' => 'el_n_name'
        ],
        [
            'index' => 1,
            'element' => 'input_email',
            'attributes' => ['type' => 'email', 'name' => 'email', 'placeholder' => 'Enter your email'],
            'settings' => ['container_class' => '', 'label' => 'Email', 'validation_rules' => ['required' => ['value' => true, 'message' => 'Email is required'], 'email' => ['value' => true, 'message' => 'Valid email required']]],
            'editor_options' => ['title' => 'Email', 'template' => 'inputText'],
            'uniqElKey' => 'el_n_email'
        ]
    ],
    'submitButton' => [
        'uniqElKey' => 'el_n_submit',
        'element' => 'button',
        'attributes' => ['type' => 'submit', 'class' => 'ml-btn ml-btn--primary'],
        'settings' => [
            'align' => 'left',
            'button_style' => 'default',
            'background_color' => '#EA4C89',
            'button_size' => 'md',
            'color' => '#ffffff',
            'button_ui' => ['type' => 'default', 'text' => 'Join Early Access', 'img_url' => '']
        ]
    ]
];

$wpdb->update(
    $wpdb->prefix . 'fluentform_forms',
    [
        'title' => 'MusicLinkd Early Access & Newsletter',
        'form_fields' => json_encode($form2Fields),
        'status' => 'published',
        'updated_at' => current_time('mysql')
    ],
    ['id' => 2]
);
echo "FluentForm ID 2 updated to 'MusicLinkd Early Access & Newsletter'.\n";

// Disable external form email notifications (as requested by user)
$notificationsSetting = [
    'email_notification' => [
        'name' => 'Admin Notification',
        'sendTo' => ['type' => 'admin_email'],
        'status' => false // Disabled: let CRM handle contacts
    ]
];
$wpdb->replace(
    $wpdb->prefix . 'fluentform_form_meta',
    [
        'form_id' => 1,
        'meta_key' => 'notifications',
        'value' => serialize($notificationsSetting)
    ]
);
$wpdb->replace(
    $wpdb->prefix . 'fluentform_form_meta',
    [
        'form_id' => 2,
        'meta_key' => 'notifications',
        'value' => serialize($notificationsSetting)
    ]
);
echo "FluentForm direct notification emails disabled (leads captured directly in DB/CRM).\n";

echo "\n=== 3. CONFIGURING RANK MATH SEO ===\n";
$rmTitles = get_option('rank-math-options-titles', []);
if (!is_array($rmTitles)) {
    $rmTitles = [];
}

$rmTitles['title_separator'] = '·';
$rmTitles['website_name'] = 'MusicLinkd';
$rmTitles['knowledgegraph_name'] = 'MusicLinkd';
$rmTitles['knowledgegraph_type'] = 'company';
$rmTitles['homepage_title'] = 'MusicLinkd %sep% Verified Creative Network & Booking Marketplace';
$rmTitles['homepage_description'] = "India's verified booking network and marketplace for musicians, sound engineers, producers, and audio creators.";
$rmTitles['twitter_card_type'] = 'summary_large_image';

update_option('rank-math-options-titles', $rmTitles);
echo "Rank Math SEO title & description tokens updated.\n";

echo "\n=== 4. CONFIGURING PWA FOR WP ===\n";
if (function_exists('pwaforwp_defaultSettings')) {
    $pwaSettings = pwaforwp_defaultSettings();
} else {
    $pwaSettings = [];
}

$pwaSettings['app_blog_name'] = 'MusicLinkd';
$pwaSettings['app_blog_short_name'] = 'MusicLinkd';
$pwaSettings['description'] = "India's Verified Creative Network & Booking Marketplace";
$pwaSettings['theme_color'] = '#EA4C89';
$pwaSettings['background_color'] = '#0F0F14';
$pwaSettings['icon'] = 'https://musiclinkd.com/wp-content/themes/musiclinkd/assets/img/brand/favicon-mark.svg';
$pwaSettings['splash_icon'] = 'https://musiclinkd.com/wp-content/themes/musiclinkd/assets/img/brand/favicon-mark.svg';
$pwaSettings['custom_banner_background_color'] = '#0F0F14';
$pwaSettings['custom_banner_title_color'] = '#FFFFFF';
$pwaSettings['custom_banner_btn_color'] = '#EA4C89';
$pwaSettings['custom_banner_btn_text_color'] = '#FFFFFF';
$pwaSettings['normal_enable'] = 1;
$pwaSettings['amp_enable'] = 0;

update_option('pwaforwp_settings', $pwaSettings);
echo "PWA for WP manifest & branding tokens updated.\n";

echo "\n=== ALL PLUGINS SYNC COMPLETE ===\n";
