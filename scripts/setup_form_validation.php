<?php
/**
 * MusicLinkd Form Validation, Fallback & Error Handling Configuration
 */

if (!defined('ABSPATH')) {
    exit;
}

echo "=== CONFIGURING ADVANCED VALIDATION & ERROR HANDLING FOR FLUENT FORMS ===\n";
global $wpdb;

// 1. Form 1: Creator Application & Inquiries
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
                    'attributes' => ['type' => 'text', 'name' => 'first_name', 'placeholder' => 'First Name *'],
                    'settings' => [
                        'container_class' => '',
                        'label' => 'First Name',
                        'visible' => true,
                        'validation_rules' => [
                            'required' => ['value' => true, 'message' => 'Please enter your first name.']
                        ]
                    ]
                ],
                'last_name' => [
                    'element' => 'input_text',
                    'attributes' => ['type' => 'text', 'name' => 'last_name', 'placeholder' => 'Last Name *'],
                    'settings' => [
                        'container_class' => '',
                        'label' => 'Last Name',
                        'visible' => true,
                        'validation_rules' => [
                            'required' => ['value' => true, 'message' => 'Please enter your last name.']
                        ]
                    ]
                ]
            ],
            'editor_options' => ['title' => 'Name Fields', 'element' => 'name-fields', 'template' => 'nameFields'],
            'uniqElKey' => 'el_name'
        ],
        [
            'index' => 1,
            'element' => 'input_email',
            'attributes' => ['type' => 'email', 'name' => 'email', 'placeholder' => 'name@domain.com *'],
            'settings' => [
                'container_class' => '',
                'label' => 'Email Address',
                'validation_rules' => [
                    'required' => ['value' => true, 'message' => 'Please enter your email address.'],
                    'email'    => ['value' => true, 'message' => 'Please enter a valid email address (e.g. name@domain.com).']
                ]
            ],
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
                'validation_rules' => [
                    'required' => ['value' => true, 'message' => 'Please select your role from the list.']
                ]
            ],
            'editor_options' => ['title' => 'Dropdown', 'template' => 'selectField'],
            'uniqElKey' => 'el_category'
        ],
        [
            'index' => 3,
            'element' => 'input_text',
            'attributes' => ['type' => 'url', 'name' => 'portfolio_url', 'placeholder' => 'https://spotify.com, soundcloud.com, instagram.com, or portfolio'],
            'settings' => [
                'container_class' => '',
                'label' => 'Portfolio / Audio Sample Link',
                'validation_rules' => [
                    'url' => ['value' => true, 'message' => 'Please enter a valid URL starting with http:// or https://.']
                ]
            ],
            'editor_options' => ['title' => 'Simple Text', 'template' => 'inputText'],
            'uniqElKey' => 'el_portfolio'
        ],
        [
            'index' => 4,
            'element' => 'textarea',
            'attributes' => ['name' => 'message', 'placeholder' => 'Tell us about your work, services, or inquiry...', 'rows' => 4],
            'settings' => [
                'container_class' => '',
                'label' => 'Message / Project Details',
                'validation_rules' => [
                    'required'  => ['value' => true, 'message' => 'Please provide a message or inquiry details.'],
                    'min'       => ['value' => 10, 'message' => 'Message must be at least 10 characters long.']
                ]
            ],
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

// Form 1 Settings (Success message, empty check, errors)
$form1Settings = [
    'confirmation' => [
        'redirectTo' => 'samePage',
        'messageToShow' => '<div class="ml-form-success"><strong>Application Received!</strong><p>Thank you for submitting your details. Our Creator Desk will review your application and audio portfolio within 24–48 hours.</p></div>',
        'samePageFormBehavior' => 'hide_form',
        'customPage' => null,
        'customUrl' => null
    ],
    'restrictions' => [
        'limitNumberOfEntries' => ['enabled' => false],
        'scheduleForm' => ['enabled' => false],
        'requireLogin' => ['enabled' => false],
        'denyEmptySubmission' => [
            'enabled' => true,
            'message' => 'Please fill in all required fields before submitting.'
        ]
    ],
    'layout' => [
        'labelPlacement' => 'top',
        'helpMessagePlacement' => 'with_label',
        'errorMessagePlacement' => 'inline',
        'cssClassName' => 'musiclinkd-fluent-form',
        'asteriskPlacement' => 'asterisk-right'
    ],
    'delete_entry_on_submission' => 'no'
];

$wpdb->replace(
    $wpdb->prefix . 'fluentform_form_meta',
    [
        'form_id' => 1,
        'meta_key' => 'formSettings',
        'value' => json_encode($form1Settings)
    ]
);

echo "Form 1 validation, error rules & success messages configured.\n";

// 2. Form 2: Early Access & Newsletter
$form2Fields = [
    'fields' => [
        [
            'index' => 0,
            'element' => 'input_text',
            'attributes' => ['type' => 'text', 'name' => 'first_name', 'placeholder' => 'Your First Name'],
            'settings' => [
                'container_class' => '',
                'label' => 'First Name',
                'validation_rules' => [
                    'required' => ['value' => false, 'message' => '']
                ]
            ],
            'editor_options' => ['title' => 'First Name', 'template' => 'inputText'],
            'uniqElKey' => 'el_n_name'
        ],
        [
            'index' => 1,
            'element' => 'input_email',
            'attributes' => ['type' => 'email', 'name' => 'email', 'placeholder' => 'Enter your email address *'],
            'settings' => [
                'container_class' => '',
                'label' => 'Email Address',
                'validation_rules' => [
                    'required' => ['value' => true, 'message' => 'Please enter your email address to join.'],
                    'email'    => ['value' => true, 'message' => 'Please enter a valid email address.']
                ]
            ],
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

$form2Settings = [
    'confirmation' => [
        'redirectTo' => 'samePage',
        'messageToShow' => '<div class="ml-form-success"><strong>Welcome to MusicLinkd!</strong><p>You have been added to our early access priority list. Watch your inbox for updates.</p></div>',
        'samePageFormBehavior' => 'hide_form',
        'customPage' => null,
        'customUrl' => null
    ],
    'restrictions' => [
        'limitNumberOfEntries' => ['enabled' => false],
        'scheduleForm' => ['enabled' => false],
        'requireLogin' => ['enabled' => false],
        'denyEmptySubmission' => [
            'enabled' => true,
            'message' => 'Please enter your email address.'
        ]
    ],
    'layout' => [
        'labelPlacement' => 'top',
        'helpMessagePlacement' => 'with_label',
        'errorMessagePlacement' => 'inline',
        'cssClassName' => 'musiclinkd-newsletter-form',
        'asteriskPlacement' => 'asterisk-right'
    ],
    'delete_entry_on_submission' => 'no'
];

$wpdb->replace(
    $wpdb->prefix . 'fluentform_form_meta',
    [
        'form_id' => 2,
        'meta_key' => 'formSettings',
        'value' => json_encode($form2Settings)
    ]
);

echo "Form 2 validation, error rules & success messages configured.\n";

// 3. Polish Page 15 (Contact Page) with native design system blocks
$contactContent = '<!-- wp:group {"className":"editorial-guide","layout":{"type":"constrained","contentSize":"780px"}} -->
<div class="wp-block-group editorial-guide">

<!-- wp:group {"className":"editorial-guide__hero","layout":{"type":"flex","orientation":"vertical"}} -->
<div class="wp-block-group editorial-guide__hero">
<!-- wp:paragraph {"className":"category-pill"} -->
<p class="category-pill">Support &amp; Desk</p>
<!-- /wp:paragraph -->
<!-- wp:heading {"level":1,"className":"display--lg"} -->
<h1 class="wp-block-heading display--lg">Contact MusicLinkd</h1>
<!-- /wp:heading -->
<!-- wp:paragraph {"className":"editorial-guide__subhead"} -->
<p class="editorial-guide__subhead">Have a question about verified creator badges, booking policies, or dispute resolution? Reach out directly to our creator operations desk.</p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:group -->

<!-- wp:columns {"className":"contact-meta-row","style":{"spacing":{"margin":{"bottom":"32px"}}}} -->
<div class="wp-block-columns contact-meta-row" style="margin-bottom:32px">
<!-- wp:column -->
<div class="wp-block-column">
<!-- wp:paragraph {"className":"text-secondary"} -->
<p class="text-secondary">Direct Operations Desk</p>
<!-- /wp:paragraph -->
<!-- wp:paragraph -->
<p><strong><a href="mailto:contact@musiclinkd.com">contact@musiclinkd.com</a></strong></p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:column -->
<!-- wp:column -->
<div class="wp-block-column">
<!-- wp:paragraph {"className":"text-secondary"} -->
<p class="text-secondary">Response Time</p>
<!-- /wp:paragraph -->
<!-- wp:paragraph -->
<p><strong>Within 24–48 Hours</strong></p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:column -->
<!-- wp:column -->
<div class="wp-block-column">
<!-- wp:paragraph {"className":"text-secondary"} -->
<p class="text-secondary">Self-Service Help</p>
<!-- /wp:paragraph -->
<!-- wp:paragraph -->
<p><strong><a href="/help/">Visit Help Centre →</a></strong></p>
<!-- /wp:paragraph -->
</div>
<!-- /wp:column -->
</div>
<!-- /wp:columns -->

<!-- wp:shortcode -->
[fluentform id="1"]
<!-- /wp:shortcode -->

</div>
<!-- /wp:group -->';

wp_update_post([
    'ID' => 15,
    'post_content' => $contactContent,
    'post_title' => 'Contact MusicLinkd'
]);

echo "Page ID 15 (Contact) polished with editorial hierarchy and Form 1.\n";
echo "=== VALIDATION SETUP COMPLETE ===\n";
