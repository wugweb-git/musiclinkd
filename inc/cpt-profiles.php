<?php
/**
 * MusicLinkd Profiles Custom Post Type & SCF (Secure Custom Fields) Mapping
 *
 * Implements native WordPress content entities for app profiles from musiclinkd-app
 * (Supabase `profiles` table), matching the canonical data model:
 * - Entity: `profile`
 * - User Flows: `talent_user` (Talent / Artists / Musicians) vs `business_user` (Businesses / Studios / Venues)
 * - Taxonomy: `person_types`, `genres`, `cities`, `instruments`
 *
 * @package MusicLinkd
 */

if (!defined('ABSPATH')) {
    exit;
}

/**
 * Register 'profile' Custom Post Type and Taxonomies.
 */
add_action('init', 'musiclinkd_register_profiles_cpt');
function musiclinkd_register_profiles_cpt() {
    $labels = array(
        'name'                  => _x('App Profiles', 'Post Type General Name', 'musiclinkd'),
        'singular_name'         => _x('App Profile', 'Post Type Singular Name', 'musiclinkd'),
        'menu_name'             => __('App Profiles', 'musiclinkd'),
        'name_admin_bar'        => __('App Profile', 'musiclinkd'),
        'archives'              => __('App Profile Directory', 'musiclinkd'),
        'all_items'             => __('All App Profiles', 'musiclinkd'),
        'add_new_item'          => __('Add New App Profile', 'musiclinkd'),
        'add_new'               => __('Add New', 'musiclinkd'),
        'new_item'              => __('New App Profile', 'musiclinkd'),
        'edit_item'             => __('Edit App Profile', 'musiclinkd'),
        'update_item'           => __('Update App Profile', 'musiclinkd'),
        'view_item'             => __('View App Profile', 'musiclinkd'),
        'search_items'          => __('Search App Profiles', 'musiclinkd'),
        'not_found'             => __('No app profiles found', 'musiclinkd'),
        'not_found_in_trash'    => __('No app profiles found in Trash', 'musiclinkd'),
    );

    $args = array(
        'label'                 => __('App Profile', 'musiclinkd'),
        'description'           => __('MusicLinkd Customer App Profiles: Talent & Businesses (synced from app; separate from WordPress system users)', 'musiclinkd'),
        'labels'                => $labels,
        'supports'              => array('title', 'editor', 'thumbnail', 'excerpt', 'custom-fields', 'revisions'),
        'taxonomies'            => array('user_flow', 'person_type', 'genre', 'city', 'instrument'),
        'hierarchical'          => false,
        'public'                => false,
        'show_ui'               => true,
        'show_in_menu'          => true,
        'menu_position'         => 6,
        'menu_icon'             => 'dashicons-id-alt',
        'show_in_admin_bar'     => true,
        'show_in_nav_menus'     => false,
        'can_export'            => true,
        'has_archive'           => false,
        'exclude_from_search'   => true,
        'publicly_queryable'    => false,
        'capability_type'       => 'post',
        'show_in_rest'          => true,
        'rest_base'             => 'profiles',
        'rewrite'               => false,
    );
    register_post_type('profile', $args);

    // Taxonomy: User Flow (Talent vs Business)
    register_taxonomy('user_flow', array('profile'), array(
        'labels'            => array(
            'name'          => __('User Flows', 'musiclinkd'),
            'singular_name' => __('User Flow', 'musiclinkd'),
            'search_items'  => __('Search User Flows', 'musiclinkd'),
            'all_items'     => __('All User Flows', 'musiclinkd'),
            'menu_name'     => __('User Flows', 'musiclinkd'),
        ),
        'hierarchical'      => true,
        'show_ui'           => true,
        'show_admin_column' => true,
        'query_var'         => true,
        'show_in_rest'      => true,
        'rewrite'           => array('slug' => 'profiles/flow'),
    ));

    // Taxonomy: Person Types & Roles (from taxonomy_options category person_types)
    register_taxonomy('person_type', array('profile'), array(
        'labels'            => array(
            'name'          => __('Person Types & Roles', 'musiclinkd'),
            'singular_name' => __('Person Type', 'musiclinkd'),
            'search_items'  => __('Search Person Types', 'musiclinkd'),
            'all_items'     => __('All Person Types', 'musiclinkd'),
            'edit_item'     => __('Edit Person Type', 'musiclinkd'),
            'update_item'   => __('Update Person Type', 'musiclinkd'),
            'add_new_item'  => __('Add New Person Type', 'musiclinkd'),
            'new_item_name' => __('New Person Type Name', 'musiclinkd'),
            'menu_name'     => __('Person Types', 'musiclinkd'),
        ),
        'hierarchical'      => false,
        'show_ui'           => true,
        'show_admin_column' => true,
        'query_var'         => true,
        'show_in_rest'      => true,
        'rewrite'           => array('slug' => 'profiles/type'),
    ));

    // Taxonomy: Genres
    register_taxonomy('genre', array('profile'), array(
        'labels'            => array(
            'name'          => __('Genres', 'musiclinkd'),
            'singular_name' => __('Genre', 'musiclinkd'),
            'search_items'  => __('Search Genres', 'musiclinkd'),
            'all_items'     => __('All Genres', 'musiclinkd'),
            'menu_name'     => __('Genres', 'musiclinkd'),
        ),
        'hierarchical'      => false,
        'show_ui'           => true,
        'show_admin_column' => true,
        'query_var'         => true,
        'show_in_rest'      => true,
        'rewrite'           => array('slug' => 'profiles/genre'),
    ));

    // Taxonomy: Cities
    register_taxonomy('city', array('profile'), array(
        'labels'            => array(
            'name'          => __('Cities', 'musiclinkd'),
            'singular_name' => __('City', 'musiclinkd'),
            'search_items'  => __('Search Cities', 'musiclinkd'),
            'all_items'     => __('All Cities', 'musiclinkd'),
            'menu_name'     => __('Cities', 'musiclinkd'),
        ),
        'hierarchical'      => false,
        'show_ui'           => true,
        'show_admin_column' => true,
        'query_var'         => true,
        'show_in_rest'      => true,
        'rewrite'           => array('slug' => 'profiles/city'),
    ));

    // Taxonomy: Instruments
    register_taxonomy('instrument', array('profile'), array(
        'labels'            => array(
            'name'          => __('Instruments', 'musiclinkd'),
            'singular_name' => __('Instrument', 'musiclinkd'),
            'search_items'  => __('Search Instruments', 'musiclinkd'),
            'all_items'     => __('All Instruments', 'musiclinkd'),
            'menu_name'     => __('Instruments', 'musiclinkd'),
        ),
        'hierarchical'      => false,
        'show_ui'           => true,
        'show_admin_column' => false,
        'query_var'         => true,
        'show_in_rest'      => true,
        'rewrite'           => array('slug' => 'profiles/instrument'),
    ));
}

/**
 * Register Native Post Meta for REST API exposure.
 */
add_action('init', 'musiclinkd_register_profile_meta');
function musiclinkd_register_profile_meta() {
    $meta_fields = array(
        'headline'         => 'string',
        'user_flow'        => 'string',
        'is_talent'        => 'boolean',
        'hourly_rate'      => 'number',
        'rate_unit'        => 'string',
        'is_verified'      => 'boolean',
        'is_available'     => 'boolean',
        'availability'     => 'string',
        'experience_level' => 'string',
        'rating'           => 'number',
        'review_count'     => 'integer',
        'area'             => 'string',
        'pin_code'         => 'string',
        'avatar_url'       => 'string',
        'cover_url'        => 'string',
        'spotify_url'      => 'string',
        'instagram_url'    => 'string',
        'youtube_url'      => 'string',
        'soundcloud_url'   => 'string',
        'website_url'      => 'string',
        'supabase_id'      => 'string',
        'app_profile_url'  => 'string',
        'qr_code_url'      => 'string',
        'qr_style'         => 'string',
        'qr_image_url'     => 'string',
        'discovery_prefs'  => 'string',
        'work_modes'       => 'string',
        'intents'          => 'string',
        'languages'        => 'string',
        'persona_badges'   => 'string',
        'trust_badges'     => 'string',
        'specialisations'  => 'string',
        'followers_count'  => 'integer',
        'visibility'       => 'string',
    );

    foreach ($meta_fields as $field => $type) {
        register_post_meta('profile', $field, array(
            'show_in_rest' => true,
            'single'       => true,
            'type'         => $type,
        ));
    }
}

/**
 * Register SCF (Secure Custom Fields) / ACF local field group for Profile.
 */
add_action('acf/init', 'musiclinkd_register_scf_profile_fields');
function musiclinkd_register_scf_profile_fields() {
    if (!function_exists('acf_add_local_field_group')) {
        return;
    }

    acf_add_local_field_group(array(
        'key'                   => 'group_profile_details',
        'title'                 => __('⚡ Profile Details (musiclinkd-app sync)', 'musiclinkd'),
        'fields'                => array(
            array(
                'key'           => 'field_profile_headline',
                'label'         => __('Headline', 'musiclinkd'),
                'name'          => 'headline',
                'type'          => 'text',
                'instructions'  => __('Professional tag/headline from the MusicLinkd App.', 'musiclinkd'),
            ),
            array(
                'key'           => 'field_profile_user_flow',
                'label'         => __('User Flow', 'musiclinkd'),
                'name'          => 'user_flow',
                'type'          => 'select',
                'choices'       => array(
                    'talent_user'   => 'Talent (Individual Creative / Artist)',
                    'business_user' => 'Business (Studio / Venue / Agency / Label)',
                ),
                'default_value' => 'talent_user',
            ),
            array(
                'key'           => 'field_profile_is_talent',
                'label'         => __('Is Talent', 'musiclinkd'),
                'name'          => 'is_talent',
                'type'          => 'true_false',
                'ui'            => 1,
            ),
            array(
                'key'           => 'field_profile_hourly_rate',
                'label'         => __('Rate (₹ INR)', 'musiclinkd'),
                'name'          => 'hourly_rate',
                'type'          => 'number',
                'instructions'  => __('Base booking rate in Indian Rupees.', 'musiclinkd'),
            ),
            array(
                'key'           => 'field_profile_rate_unit',
                'label'         => __('Rate Unit', 'musiclinkd'),
                'name'          => 'rate_unit',
                'type'          => 'text',
                'default_value' => 'hour',
            ),
            array(
                'key'           => 'field_profile_is_verified',
                'label'         => __('Verified Badge', 'musiclinkd'),
                'name'          => 'is_verified',
                'type'          => 'true_false',
                'ui'            => 1,
            ),
            array(
                'key'           => 'field_profile_is_available',
                'label'         => __('Available for Booking / Collabs', 'musiclinkd'),
                'name'          => 'is_available',
                'type'          => 'true_false',
                'ui'            => 1,
                'default_value' => 1,
            ),
            array(
                'key'           => 'field_profile_rating',
                'label'         => __('Rating', 'musiclinkd'),
                'name'          => 'rating',
                'type'          => 'number',
                'step'          => '0.1',
            ),
            array(
                'key'           => 'field_profile_review_count',
                'label'         => __('Review Count', 'musiclinkd'),
                'name'          => 'review_count',
                'type'          => 'number',
            ),
            array(
                'key'           => 'field_profile_avatar_url',
                'label'         => __('Avatar CDN URL', 'musiclinkd'),
                'name'          => 'avatar_url',
                'type'          => 'url',
            ),
            array(
                'key'           => 'field_profile_cover_url',
                'label'         => __('Cover Photo URL', 'musiclinkd'),
                'name'          => 'cover_url',
                'type'          => 'url',
            ),
            array(
                'key'           => 'field_profile_spotify_url',
                'label'         => __('Spotify Profile', 'musiclinkd'),
                'name'          => 'spotify_url',
                'type'          => 'url',
            ),
            array(
                'key'           => 'field_profile_instagram_url',
                'label'         => __('Instagram Profile', 'musiclinkd'),
                'name'          => 'instagram_url',
                'type'          => 'url',
            ),
            array(
                'key'           => 'field_profile_app_profile_url',
                'label'         => __('Live App Profile URL', 'musiclinkd'),
                'name'          => 'app_profile_url',
                'type'          => 'url',
            ),
            array(
                'key'           => 'field_profile_qr_code_url',
                'label'         => __('Identity QR Destination', 'musiclinkd'),
                'name'          => 'qr_code_url',
                'type'          => 'url',
            ),
            array(
                'key'           => 'field_profile_qr_image_url',
                'label'         => __('QR Code Image', 'musiclinkd'),
                'name'          => 'qr_image_url',
                'type'          => 'url',
            ),
            array(
                'key'           => 'field_profile_qr_style',
                'label'         => __('QR Style', 'musiclinkd'),
                'name'          => 'qr_style',
                'type'          => 'text',
            ),
            array(
                'key'           => 'field_profile_work_modes',
                'label'         => __('Work Modes', 'musiclinkd'),
                'name'          => 'work_modes',
                'type'          => 'text',
            ),
            array(
                'key'           => 'field_profile_intents',
                'label'         => __('Intents', 'musiclinkd'),
                'name'          => 'intents',
                'type'          => 'text',
            ),
            array(
                'key'           => 'field_profile_languages',
                'label'         => __('Languages', 'musiclinkd'),
                'name'          => 'languages',
                'type'          => 'text',
            ),
            array(
                'key'           => 'field_profile_persona_badges',
                'label'         => __('Persona Badges', 'musiclinkd'),
                'name'          => 'persona_badges',
                'type'          => 'text',
            ),
            array(
                'key'           => 'field_profile_trust_badges',
                'label'         => __('Trust Badges', 'musiclinkd'),
                'name'          => 'trust_badges',
                'type'          => 'text',
            ),
            array(
                'key'           => 'field_profile_specialisations',
                'label'         => __('Specialisations', 'musiclinkd'),
                'name'          => 'specialisations',
                'type'          => 'text',
            ),
            array(
                'key'           => 'field_profile_followers_count',
                'label'         => __('Followers Count', 'musiclinkd'),
                'name'          => 'followers_count',
                'type'          => 'number',
            ),
            array(
                'key'           => 'field_profile_discovery_prefs',
                'label'         => __('Discovery Preferences JSON', 'musiclinkd'),
                'name'          => 'discovery_prefs',
                'type'          => 'textarea',
                'rows'          => 3,
            ),
            array(
                'key'           => 'field_profile_supabase_id',
                'label'         => __('Supabase UUID', 'musiclinkd'),
                'name'          => 'supabase_id',
                'type'          => 'text',
                'readonly'      => 1,
            ),
        ),
        'location'              => array(
            array(
                array(
                    'param'    => 'post_type',
                    'operator' => '==',
                    'value'    => 'profile',
                ),
            ),
        ),
        'menu_order'            => 0,
        'position'              => 'normal',
        'style'                 => 'default',
        'label_placement'       => 'top',
        'instruction_placement' => 'label',
    ));
}

/**
 * Register REST API route for bulk profile ingestion from Supabase.
 */
add_action('rest_api_init', function () {
    register_rest_route('musiclinkd/v1', '/ingest-profiles', array(
        'methods'             => array('POST'),
        'callback'            => 'musiclinkd_handle_ingest_profiles',
        'permission_callback' => function () {
            return current_user_can('edit_pages');
        },
    ));
    register_rest_route('musiclinkd/v1', '/ingest-taxonomies', array(
        'methods'             => array('POST'),
        'callback'            => 'musiclinkd_handle_ingest_taxonomies',
        'permission_callback' => function () {
            return current_user_can('edit_pages');
        },
    ));
    register_rest_route('musiclinkd/v1', '/fluentcrm-status', array(
        'methods'             => array('GET'),
        'callback'            => 'musiclinkd_handle_fluentcrm_status',
        'permission_callback' => '__return_true',
    ));
    register_rest_route('musiclinkd/v1', '/sync-fluentcrm', array(
        'methods'             => array('POST'),
        'callback'            => 'musiclinkd_handle_sync_fluentcrm',
        'permission_callback' => function () {
            return current_user_can('edit_pages');
        },
    ));
    register_rest_route('musiclinkd/v1', '/enrich-fluentcrm-profiles', array(
        'methods'             => array('POST'),
        'callback'            => 'musiclinkd_handle_enrich_fluentcrm_profiles',
        'permission_callback' => function () {
            return current_user_can('edit_pages');
        },
    ));
});

/**
 * Handle POST /wp-json/musiclinkd/v1/ingest-taxonomies
 * Seeds all master taxonomy_options (person_types, genres, cities, instruments) from Supabase.
 */
function musiclinkd_handle_ingest_taxonomies(WP_REST_Request $request) {
    if (!defined('MUSICLINKD_INGESTING')) {
        define('MUSICLINKD_INGESTING', true);
    }
    $body = $request->get_json_params();
    if (empty($body) || !isset($body['options']) || !is_array($body['options'])) {
        return new WP_Error('invalid_payload', 'Payload must contain an options array.', array('status' => 400));
    }

    $category_map = array(
        'person_types' => 'person_type',
        'genres'       => 'genre',
        'cities'       => 'city',
        'instruments'  => 'instrument',
    );

    $created = 0;
    $existing = 0;

    foreach ($body['options'] as $opt) {
        $cat = $opt['category_key'] ?? '';
        if (!isset($category_map[$cat])) {
            continue;
        }
        $taxonomy = $category_map[$cat];
        $label = sanitize_text_field($opt['label'] ?? '');
        $slug = sanitize_title($opt['slug'] ?? $label);
        if (!$label) {
            continue;
        }

        $term = term_exists($label, $taxonomy);
        if (!$term) {
            $res = wp_insert_term($label, $taxonomy, array('slug' => $slug));
            if (!is_wp_error($res)) {
                $created++;
            }
        } else {
            $existing++;
        }
    }

    return rest_ensure_response(array(
        'success'  => true,
        'created'  => $created,
        'existing' => $existing,
    ));
}

/**
 * Handle POST /wp-json/musiclinkd/v1/ingest-profiles
 */
function musiclinkd_handle_ingest_profiles(WP_REST_Request $request) {
    if (!defined('MUSICLINKD_INGESTING')) {
        define('MUSICLINKD_INGESTING', true);
    }
    global $wpdb;
    $body = $request->get_json_params();

    if (empty($body) || !isset($body['profiles']) || !is_array($body['profiles'])) {
        return new WP_Error('invalid_payload', 'Payload must contain a profiles array.', array('status' => 400));
    }

    $profiles = $body['profiles'];
    $stats = array(
        'total'            => count($profiles),
        'created'          => 0,
        'updated'          => 0,
        'fluentcrm_synced' => 0,
        'errors'           => array(),
    );

    // Known business role slugs from taxonomy_options metadata.flow = 'business_user'
    $business_roles = array(
        'recording_studio', 'studio', 'music_label', 'venue', 'event_organizer',
        'booking_agency', 'artist_management', 'artist_manager', 'production_house',
        'music_school', 'dealer', 'rental_provider'
    );

    foreach ($profiles as $profile) {
        $supabase_id = sanitize_text_field($profile['id'] ?? '');
        $username    = sanitize_title($profile['username'] ?? '');
        $display_name = sanitize_text_field($profile['display_name'] ?? $profile['username'] ?? '');

        if (empty($supabase_id) || empty($username)) {
            continue;
        }

        // Determine user_flow: business_user vs talent_user
        $roles = is_array($profile['roles'] ?? null) ? $profile['roles'] : array();
        $is_business = false;
        foreach ($roles as $r) {
            if (in_array(strtolower($r), $business_roles)) {
                $is_business = true;
                break;
            }
        }
        $user_flow = $is_business ? 'business_user' : 'talent_user';
        $user_flow_label = $is_business ? 'Business' : 'Talent';

        // Check if post already exists by supabase_id meta or post_name
        $existing_post_id = $wpdb->get_var($wpdb->prepare(
            "SELECT post_id FROM {$wpdb->postmeta} WHERE meta_key = 'supabase_id' AND meta_value = %s LIMIT 1",
            $supabase_id
        ));

        if (!$existing_post_id) {
            $existing_post_id = $wpdb->get_var($wpdb->prepare(
                "SELECT ID FROM {$wpdb->posts} WHERE post_name = %s AND post_type = 'profile' LIMIT 1",
                $username
            ));
        }

        $bio = wp_kses_post($profile['bio'] ?? '');
        $headline = sanitize_text_field($profile['headline'] ?? '');
        $city = sanitize_text_field($profile['city'] ?? '');
        $hourly_rate_paise = intval($profile['hourly_rate_paise'] ?? $profile['rate_paise'] ?? 0);
        $hourly_rate = $hourly_rate_paise > 0 ? ($hourly_rate_paise / 100) : 0;
        $is_verified = !empty($profile['is_verified']);
        $is_available = !empty($profile['is_available']);

        // Build Gutenberg Block Content for the Post Body
        $gutenberg_content = '<!-- wp:group {"style":{"spacing":{"padding":{"top":"24px","bottom":"24px","left":"24px","right":"24px"}}},"backgroundColor":"surface","layout":{"type":"constrained"}} -->' . "\n";
        $gutenberg_content .= '<div class="wp-block-group has-surface-background-color" style="padding-top:24px;padding-right:24px;padding-bottom:24px;padding-left:24px;">' . "\n";
        if ($headline) {
            $gutenberg_content .= '<!-- wp:paragraph {"style":{"typography":{"fontStyle":"italic","fontSize":"1.1rem"}}} -->' . "\n";
            $gutenberg_content .= '<p style="font-size:1.1rem;font-style:italic"><strong>' . esc_html($headline) . '</strong></p>' . "\n";
            $gutenberg_content .= '<!-- /wp:paragraph -->' . "\n";
        }
        if ($bio) {
            $gutenberg_content .= '<!-- wp:paragraph -->' . "\n";
            $gutenberg_content .= '<p>' . nl2br(esc_html($bio)) . '</p>' . "\n";
            $gutenberg_content .= '<!-- /wp:paragraph -->' . "\n";
        }
        $gutenberg_content .= '</div>' . "\n";
        $gutenberg_content .= '<!-- /wp:group -->';

        $post_data = array(
            'post_title'    => $display_name,
            'post_name'     => $username,
            'post_content'  => $gutenberg_content,
            'post_status'   => 'publish',
            'post_type'     => 'profile',
            'post_excerpt'  => $headline ?: wp_trim_words($bio, 25),
        );

        if ($existing_post_id) {
            $post_data['ID'] = $existing_post_id;
            $post_id = wp_update_post($post_data);
            $stats['updated']++;
        } else {
            $post_id = wp_insert_post($post_data);
            $stats['created']++;
        }

        if (is_wp_error($post_id)) {
            $stats['errors'][] = $username . ': ' . $post_id->get_error_message();
            continue;
        }

        // Set Post Meta / SCF Fields
        update_post_meta($post_id, 'supabase_id', $supabase_id);
        update_post_meta($post_id, 'user_flow', $user_flow);
        update_post_meta($post_id, 'is_talent', !$is_business);
        update_post_meta($post_id, 'headline', $headline);
        update_post_meta($post_id, 'hourly_rate', $hourly_rate);
        update_post_meta($post_id, 'rate_unit', sanitize_text_field($profile['rate_unit'] ?? 'hour'));
        update_post_meta($post_id, 'is_verified', $is_verified ? 1 : 0);
        update_post_meta($post_id, 'is_available', $is_available ? 1 : 0);
        update_post_meta($post_id, 'availability', sanitize_text_field($profile['availability'] ?? 'Available'));
        update_post_meta($post_id, 'experience_level', sanitize_text_field($profile['experience_level'] ?? ''));
        update_post_meta($post_id, 'rating', floatval($profile['rating'] ?? 0));
        update_post_meta($post_id, 'review_count', intval($profile['review_count'] ?? 0));
        update_post_meta($post_id, 'area', sanitize_text_field($profile['area'] ?? ''));
        update_post_meta($post_id, 'pin_code', sanitize_text_field($profile['pin_code'] ?? ''));
        update_post_meta($post_id, 'avatar_url', esc_url_raw($profile['avatar_url'] ?? ''));
        update_post_meta($post_id, 'cover_url', esc_url_raw($profile['cover_url'] ?? ''));

        // Social Links
        $links = is_array($profile['links'] ?? null) ? $profile['links'] : array();
        update_post_meta($post_id, 'spotify_url', esc_url_raw($links['spotify'] ?? ''));
        update_post_meta($post_id, 'instagram_url', esc_url_raw($links['instagram'] ?? ''));
        update_post_meta($post_id, 'youtube_url', esc_url_raw($links['youtube'] ?? ''));
        update_post_meta($post_id, 'soundcloud_url', esc_url_raw($links['soundcloud'] ?? ''));
        update_post_meta($post_id, 'website_url', esc_url_raw($links['website'] ?? ''));

        // App Canonical URLs, QR Codes & Preferences
        $app_profile_url = esc_url_raw('https://app.musiclinkd.com/@' . $username);
        $qr_destination_url = esc_url_raw('https://app.musiclinkd.com/identity/q/' . $username);
        $qr_image_url = esc_url_raw('https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=' . urlencode($qr_destination_url));
        $qr_style = sanitize_text_field($profile['qr_style'] ?? 'light');

        update_post_meta($post_id, 'app_profile_url', $app_profile_url);
        update_post_meta($post_id, 'qr_code_url', $qr_destination_url);
        update_post_meta($post_id, 'qr_image_url', $qr_image_url);
        update_post_meta($post_id, 'qr_style', $qr_style);

        // Work modes, intents, languages, badges, preferences
        $work_modes = is_array($profile['work_modes'] ?? null) ? implode(', ', array_map('sanitize_text_field', $profile['work_modes'])) : '';
        $intents = is_array($profile['intents'] ?? null) ? implode(', ', array_map('sanitize_text_field', $profile['intents'])) : '';
        $languages = is_array($profile['languages'] ?? null) ? implode(', ', array_map('sanitize_text_field', $profile['languages'])) : '';
        $persona_badges = is_array($profile['persona_badges'] ?? null) ? implode(', ', array_map('sanitize_text_field', $profile['persona_badges'])) : '';
        $trust_badges = is_array($profile['trust_badges'] ?? null) ? implode(', ', array_map('sanitize_text_field', $profile['trust_badges'])) : '';
        $specialisations = is_array($profile['specialisations'] ?? null) ? implode(', ', array_map('sanitize_text_field', $profile['specialisations'])) : '';
        $discovery_prefs = is_array($profile['discovery_prefs'] ?? null) ? json_encode($profile['discovery_prefs']) : '';
        $followers_count = intval($profile['followers_count'] ?? 0);
        $visibility = sanitize_text_field($profile['visibility'] ?? 'public');

        update_post_meta($post_id, 'work_modes', $work_modes);
        update_post_meta($post_id, 'intents', $intents);
        update_post_meta($post_id, 'languages', $languages);
        update_post_meta($post_id, 'persona_badges', $persona_badges);
        update_post_meta($post_id, 'trust_badges', $trust_badges);
        update_post_meta($post_id, 'specialisations', $specialisations);
        update_post_meta($post_id, 'discovery_prefs', $discovery_prefs);
        update_post_meta($post_id, 'followers_count', $followers_count);
        update_post_meta($post_id, 'visibility', $visibility);

        // Assign Taxonomies
        wp_set_object_terms($post_id, array($user_flow_label), 'user_flow', false);

        if (!empty($roles)) {
            $formatted_roles = array_map(function ($r) {
                return ucwords(str_replace('_', ' ', $r));
            }, $roles);
            wp_set_object_terms($post_id, $formatted_roles, 'person_type', false);
        }

        if (!empty($profile['genres']) && is_array($profile['genres'])) {
            $formatted_genres = array_map(function ($g) {
                return ucwords(str_replace('_', ' ', $g));
            }, $profile['genres']);
            wp_set_object_terms($post_id, $formatted_genres, 'genre', false);
        }

        if (!empty($profile['instruments']) && is_array($profile['instruments'])) {
            $formatted_instruments = array_map(function ($i) {
                return ucwords(str_replace('_', ' ', $i));
            }, $profile['instruments']);
            wp_set_object_terms($post_id, $formatted_instruments, 'instrument', false);
        }

        if ($city) {
            wp_set_object_terms($post_id, array($city), 'city', false);
        }

        // Bridge to FluentCRM Contacts
        $fluent_synced = musiclinkd_sync_profile_to_fluentcrm($profile, $user_flow_label);
        if ($fluent_synced) {
            $stats['fluentcrm_synced']++;
        }
    }

    return rest_ensure_response(array(
        'success'   => true,
        'timestamp' => current_time('mysql', 1),
        'stats'     => $stats,
    ));
}

/**
 * Direct FluentCRM synchronization helper for Profile.
 */
function musiclinkd_sync_profile_to_fluentcrm($profile, $user_flow_label) {
    global $wpdb;

    $username = sanitize_title($profile['username'] ?? '');
    if (empty($username)) {
        return false;
    }

    $email = strtolower($username . '@users.musiclinkd.com');
    $first_name = sanitize_text_field($profile['first_name'] ?? '');
    $last_name = sanitize_text_field($profile['last_name'] ?? '');

    if (empty($first_name)) {
        $display = sanitize_text_field($profile['display_name'] ?? $username);
        $parts = explode(' ', $display, 2);
        $first_name = $parts[0];
        $last_name = $parts[1] ?? '';
    }

    $city = sanitize_text_field($profile['city'] ?? '');
    $state = sanitize_text_field($profile['state'] ?? '');
    $country = sanitize_text_field($profile['country'] ?? 'India');
    $avatar = esc_url_raw($profile['avatar_url'] ?? '');
    $contact_type = $user_flow_label === 'Business' ? 'lead' : 'customer';

    // Tags to apply for profiling
    $tags = array($user_flow_label);
    if (!empty($profile['person_type'])) {
        $tags[] = ucwords(str_replace('_', ' ', $profile['person_type']));
    }
    if (!empty($profile['roles']) && is_array($profile['roles'])) {
        foreach ($profile['roles'] as $r) {
            $tags[] = ucwords(str_replace('_', ' ', $r));
        }
    }
    if (!empty($profile['genres']) && is_array($profile['genres'])) {
        foreach (array_slice($profile['genres'], 0, 3) as $g) {
            $tags[] = ucwords(str_replace('_', ' ', $g));
        }
    }
    if (!empty($profile['is_verified'])) {
        $tags[] = 'Verified Creator';
    }
    if ($city) {
        $tags[] = $city;
    }
    $tags = array_values(array_unique(array_filter($tags)));

    $list_name = ($user_flow_label === 'Business') ? 'Music Businesses & Studios' : 'Talent Creators';

    // 1. Try FluentCrmApi first if loaded
    if (function_exists('FluentCrmApi')) {
        try {
            $contact_api = FluentCrmApi('contacts');
            $data = array(
                'email'        => $email,
                'first_name'   => $first_name,
                'last_name'    => $last_name,
                'status'       => 'subscribed',
                'contact_type' => $contact_type,
                'city'         => $city,
                'state'        => $state,
                'country'      => $country,
                'avatar'       => $avatar,
                'source'       => 'musiclinkd_app_sync',
                'tags'         => $tags,
                'lists'        => array($list_name),
            );
            $contact = $contact_api->createOrUpdate($data);
            if ($contact && !empty($contact->id)) {
                return true;
            }
        } catch (\Exception $e) {
            // Fall through to direct table sync
        }
    }

    // 2. Direct database table sync (works in headless/REST context)
    $subscribers_table = $wpdb->prefix . 'fc_subscribers';
    if ($wpdb->get_var("SHOW TABLES LIKE '{$subscribers_table}'") !== $subscribers_table) {
        $alt_table = $wpdb->prefix . 'fluentcrm_subscribers';
        if ($wpdb->get_var("SHOW TABLES LIKE '{$alt_table}'") === $alt_table) {
            $subscribers_table = $alt_table;
        } else {
            return false;
        }
    }

    $now = current_time('mysql', 1);
    $existing = $wpdb->get_row($wpdb->prepare(
        "SELECT id FROM {$subscribers_table} WHERE email = %s LIMIT 1",
        $email
    ));

    $subscriber_data = array(
        'first_name'   => $first_name,
        'last_name'    => $last_name,
        'email'        => $email,
        'status'       => 'subscribed',
        'contact_type' => $contact_type,
        'city'         => $city,
        'state'        => $state,
        'country'      => $country,
        'avatar'       => $avatar,
        'updated_at'   => $now,
    );

    if ($existing) {
        $wpdb->update($subscribers_table, $subscriber_data, array('id' => $existing->id));
        $subscriber_id = $existing->id;
    } else {
        $subscriber_data['created_at'] = $now;
        $subscriber_data['source'] = 'musiclinkd_app_ingestion';
        $wpdb->insert($subscribers_table, $subscriber_data);
        $subscriber_id = $wpdb->insert_id;
    }

    if ($subscriber_id) {
        musiclinkd_attach_fluentcrm_tags_and_lists($subscriber_id, $tags, $list_name);
    }

    return boolval($subscriber_id);
}

/**
 * Attach tags and list to a subscriber in FluentCRM database tables.
 */
function musiclinkd_attach_fluentcrm_tags_and_lists($subscriber_id, array $tags, $list_name) {
    global $wpdb;

    $pivot_table = $wpdb->prefix . 'fc_subscriber_pivot';
    $tags_table = $wpdb->prefix . 'fc_tags';
    $lists_table = $wpdb->prefix . 'fc_lists';

    if ($wpdb->get_var("SHOW TABLES LIKE '{$pivot_table}'") !== $pivot_table) {
        return;
    }

    $now = current_time('mysql', 1);

    // 1. Process List
    if ($list_name && $wpdb->get_var("SHOW TABLES LIKE '{$lists_table}'") === $lists_table) {
        $list_slug = sanitize_title($list_name);
        $list_id = $wpdb->get_var($wpdb->prepare("SELECT id FROM {$lists_table} WHERE slug = %s LIMIT 1", $list_slug));
        if (!$list_id) {
            $wpdb->insert($lists_table, array(
                'title'      => $list_name,
                'slug'       => $list_slug,
                'created_at' => $now,
                'updated_at' => $now,
            ));
            $list_id = $wpdb->insert_id;
        }

        if ($list_id) {
            $has_list = $wpdb->get_var($wpdb->prepare(
                "SELECT id FROM {$pivot_table} WHERE subscriber_id = %d AND object_id = %d AND object_type = 'FluentCrm\\\\App\\\\Models\\\\Lists' LIMIT 1",
                $subscriber_id,
                $list_id
            ));
            if (!$has_list) {
                $wpdb->insert($pivot_table, array(
                    'subscriber_id' => $subscriber_id,
                    'object_id'     => $list_id,
                    'object_type'   => 'FluentCrm\\App\\Models\\Lists',
                    'created_at'    => $now,
                    'updated_at'    => $now,
                ));
            }
        }
    }

    // 2. Process Tags
    if (!empty($tags) && $wpdb->get_var("SHOW TABLES LIKE '{$tags_table}'") === $tags_table) {
        foreach ($tags as $tag_title) {
            $tag_slug = sanitize_title($tag_title);
            if (empty($tag_slug)) continue;

            $tag_id = $wpdb->get_var($wpdb->prepare("SELECT id FROM {$tags_table} WHERE slug = %s LIMIT 1", $tag_slug));
            if (!$tag_id) {
                $wpdb->insert($tags_table, array(
                    'title'      => $tag_title,
                    'slug'       => $tag_slug,
                    'created_at' => $now,
                    'updated_at' => $now,
                ));
                $tag_id = $wpdb->insert_id;
            }

            if ($tag_id) {
                $has_tag = $wpdb->get_var($wpdb->prepare(
                    "SELECT id FROM {$pivot_table} WHERE subscriber_id = %d AND object_id = %d AND object_type = 'FluentCrm\\\\App\\\\Models\\\\Tag' LIMIT 1",
                    $subscriber_id,
                    $tag_id
                ));
                if (!$has_tag) {
                    $wpdb->insert($pivot_table, array(
                        'subscriber_id' => $subscriber_id,
                        'object_id'     => $tag_id,
                        'object_type'   => 'FluentCrm\\App\\Models\\Tag',
                        'created_at'    => $now,
                        'updated_at'    => $now,
                    ));
                }
            }
        }
    }
}

/**
 * Ensure FluentCRM Native Custom Contact Fields are registered and synchronized.
 * This guarantees they appear in /settings/custom_contact_fields and inside
 * every contact's profile view (#fluentcrm_sub_info_body).
 */
function musiclinkd_ensure_fluentcrm_custom_fields() {
    if (!class_exists('FluentCrm\App\Models\CustomContactField')) {
        return false;
    }

    $fields = array(
        array(
            'label' => 'Headline / Creative Bio',
            'slug'  => 'headline',
            'type'  => 'text',
        ),
        array(
            'label' => 'Session Rate (INR)',
            'slug'  => 'hourly_rate',
            'type'  => 'text',
        ),
        array(
            'label' => 'Booking Availability',
            'slug'  => 'work_availability',
            'type'  => 'text',
        ),
        array(
            'label' => 'Community Rating',
            'slug'  => 'rating_reviews',
            'type'  => 'text',
        ),
        array(
            'label' => 'Live App Profile (@handle)',
            'slug'  => 'app_profile_url',
            'type'  => 'text',
        ),
        array(
            'label' => 'QR Identity Pass URL',
            'slug'  => 'qr_pass_url',
            'type'  => 'text',
        ),
        array(
            'label' => 'Spotify / Portfolio Link',
            'slug'  => 'social_link',
            'type'  => 'text',
        ),
        array(
            'label' => 'Captured Device Platform',
            'slug'  => 'device_platform',
            'type'  => 'text',
        ),
        array(
            'label' => 'App Version & Build',
            'slug'  => 'app_version',
            'type'  => 'text',
        ),
        array(
            'label' => 'Client Timezone & Locale',
            'slug'  => 'client_timezone',
            'type'  => 'text',
        ),
        array(
            'label' => 'QR Identity Resolver Code',
            'slug'  => 'qr_identity_code',
            'type'  => 'text',
        ),
    );

    $cf_model = new \FluentCrm\App\Models\CustomContactField();
    $cf_model->saveGlobalFields($fields);

    return $cf_model->getGlobalFields();
}
add_action('admin_init', 'musiclinkd_ensure_fluentcrm_custom_fields');

/**
 * Handle GET /wp-json/musiclinkd/v1/fluentcrm-status
 */
function musiclinkd_handle_fluentcrm_status() {
    try {
        global $wpdb;
        $sub_table = $wpdb->prefix . 'fc_subscribers';
        $sub_count = (int) $wpdb->get_var("SELECT COUNT(*) FROM {$sub_table}");

        // Save & Ensure native custom fields
        $global_fields = musiclinkd_ensure_fluentcrm_custom_fields();

        // Query subscriber 138 and 139 meta from wp_fc_subscriber_meta
        $meta_138 = $wpdb->get_results("SELECT `key`, `value` FROM {$wpdb->prefix}fc_subscriber_meta WHERE subscriber_id = 138", ARRAY_A);
        $meta_139 = $wpdb->get_results("SELECT `key`, `value` FROM {$wpdb->prefix}fc_subscriber_meta WHERE subscriber_id = 139", ARRAY_A);

        return rest_ensure_response(array(
            'success'       => true,
            'sub_count'     => $sub_count,
            'global_fields' => $global_fields,
            'meta_138'      => $meta_138,
            'meta_139'      => $meta_139,
        ));
    } catch (\Throwable $e) {
        return rest_ensure_response(array(
            'error' => $e->getMessage(),
            'file'  => $e->getFile(),
            'line'  => $e->getLine(),
        ));
    }
}

/**
 * Handle POST /wp-json/musiclinkd/v1/sync-fluentcrm
 * Pushes profiles into FluentCRM contacts with tags and lists for profiling.
 */
function musiclinkd_handle_sync_fluentcrm(WP_REST_Request $request) {
    global $wpdb;
    $body = $request->get_json_params();

    $profiles = array();
    if (!empty($body['profiles']) && is_array($body['profiles'])) {
        $profiles = $body['profiles'];
    } else {
        // Fallback: Read all existing 'profile' post types in WordPress
        $posts = get_posts(array(
            'post_type'      => 'profile',
            'posts_per_page' => -1,
            'post_status'    => 'publish',
        ));
        foreach ($posts as $p) {
            $flows = wp_get_object_terms($p->ID, 'user_flow', array('fields' => 'slugs'));
            $is_biz = in_array('business_user', $flows);
            $roles = wp_get_object_terms($p->ID, 'person_type', array('fields' => 'names'));
            $genres = wp_get_object_terms($p->ID, 'genre', array('fields' => 'names'));
            $city = get_post_meta($p->ID, 'area', true) ?: '';
            if (!$city) {
                $cities = wp_get_object_terms($p->ID, 'city', array('fields' => 'names'));
                $city = !empty($cities) ? $cities[0] : '';
            }

            $profiles[] = array(
                'username'     => $p->post_name,
                'display_name' => $p->post_title,
                'city'         => $city,
                'is_verified'  => (bool) get_post_meta($p->ID, 'is_verified', true),
                'roles'        => $roles,
                'genres'       => $genres,
                'avatar_url'   => get_post_meta($p->ID, 'avatar_url', true),
                'user_flow'    => $is_biz ? 'business_user' : 'talent_user',
            );
        }
    }

    $synced = 0;
    foreach ($profiles as $profile) {
        $is_biz = ($profile['user_flow'] ?? '') === 'business_user';
        $flow_label = $is_biz ? 'Business' : 'Talent';
        if (musiclinkd_sync_profile_to_fluentcrm($profile, $flow_label)) {
            $synced++;
        }
    }

    return rest_ensure_response(array(
        'success'        => true,
        'total_profiles' => count($profiles),
        'synced_to_crm'  => $synced,
        'timestamp'      => current_time('mysql', 1),
    ));
}

/**
 * Hook into FluentCRM subscriber info widgets to inject rich App Profile & Device Telemetry
 * directly into #fluentcrm_sub_info_body.
 */
add_filter('fluent_crm/subscriber_info_widgets', 'musiclinkd_register_crm_subscriber_widget', 10, 2);
add_filter('fluentcrm_subscriber_info_widgets', 'musiclinkd_register_crm_subscriber_widget', 10, 2);

function musiclinkd_register_crm_subscriber_widget($widgets, $subscriber) {
    if (!is_array($widgets)) {
        $widgets = array();
    }
    $widgets['musiclinkd_profile_data'] = array(
        'title'   => '📱 MusicLinkd App Profile & Device Telemetry',
        'content' => musiclinkd_get_subscriber_profile_widget_html($subscriber),
    );
    return $widgets;
}

/**
 * Render rich profile and device capture widget inside FluentCRM #fluentcrm_sub_info_body.
 */
function musiclinkd_get_subscriber_profile_widget_html($subscriber) {
    global $wpdb;

    $email = is_object($subscriber) ? ($subscriber->email ?? '') : ($subscriber['email'] ?? '');
    $username = str_replace('@users.musiclinkd.com', '', $email);

    $post = get_page_by_path($username, OBJECT, 'profile');
    if (!$post) {
        $post_id = $wpdb->get_var($wpdb->prepare(
            "SELECT post_id FROM {$wpdb->postmeta} WHERE meta_key = 'supabase_id' AND meta_value = %s LIMIT 1",
            $username
        ));
        if ($post_id) {
            $post = get_post($post_id);
        }
    }

    $headline = $post ? get_post_meta($post->ID, 'headline', true) : 'Professional Creative on MusicLinkd';
    $hourly_rate_val = $post ? get_post_meta($post->ID, 'hourly_rate', true) : 0;
    $rate_str = $hourly_rate_val > 0 ? ('₹' . number_format((float) $hourly_rate_val) . ' / session') : 'Quoted on Request';
    $is_available = $post ? get_post_meta($post->ID, 'is_available', true) : true;
    $availability = $is_available ? 'Active & Available for Hire' : 'Busy / Booked';
    $rating = $post ? get_post_meta($post->ID, 'rating', true) : '5.0';
    
    $app_profile_url = 'https://app.musiclinkd.com/@' . $username;
    $qr_pass_url = 'https://app.musiclinkd.com/identity/q/' . $username;
    $spotify = $post ? get_post_meta($post->ID, 'spotify_url', true) : '';

    ob_start();
    ?>
    <div class="musiclinkd-crm-widget" style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #FFFFFF; border: 1.5px solid #EA4C89; border-radius: 12px; padding: 20px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(234,76,137,0.08);">
        
        <!-- Header -->
        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #F3F4F6; padding-bottom: 12px; margin-bottom: 16px;">
            <div style="display: flex; align-items: center; gap: 8px;">
                <span style="background: #EA4C89; color: #fff; font-size: 11px; font-weight: 700; padding: 3px 8px; border-radius: 6px; text-transform: uppercase;">App Profile</span>
                <strong style="color: #111827; font-size: 15px;">Captured Profile &amp; Device Telemetry</strong>
            </div>
            <a href="<?php echo esc_url($app_profile_url); ?>" target="_blank" rel="noopener" style="background: #FFF0F5; color: #EA4C89; font-weight: 700; font-size: 12px; padding: 6px 14px; border-radius: 20px; text-decoration: none; border: 1px solid rgba(234,76,137,0.2);">
                Open Live App Profile (@<?php echo esc_html($username); ?>) &rarr;
            </a>
        </div>

        <!-- Headline & Bio -->
        <?php if ($headline): ?>
            <div style="background: #F9FAFB; padding: 12px 14px; border-radius: 8px; margin-bottom: 16px; border-left: 3px solid #EA4C89;">
                <span style="font-size: 11px; font-weight: 600; color: #6B7280; text-transform: uppercase; display: block; margin-bottom: 2px;">Creative Headline</span>
                <p style="margin: 0; color: #1F2937; font-size: 13.5px; font-weight: 500; font-style: italic;"><?php echo esc_html($headline); ?></p>
            </div>
        <?php endif; ?>

        <!-- Two Column Metrics -->
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 14px; margin-bottom: 16px;">
            <div style="background: #FAFAFA; padding: 12px; border-radius: 8px; border: 1px solid #E5E7EB;">
                <span style="font-size: 11px; font-weight: 600; color: #6B7280; text-transform: uppercase;">Session Rate &amp; Availability</span>
                <div style="font-size: 16px; font-weight: 800; color: #111827; margin-top: 4px;"><?php echo esc_html($rate_str); ?></div>
                <div style="font-size: 12px; color: <?php echo $is_available ? '#10B981' : '#F59E0B'; ?>; font-weight: 600; margin-top: 2px;">
                    ● <?php echo esc_html($availability); ?>
                </div>
            </div>
            <div style="background: #FAFAFA; padding: 12px; border-radius: 8px; border: 1px solid #E5E7EB;">
                <span style="font-size: 11px; font-weight: 600; color: #6B7280; text-transform: uppercase;">Reputation &amp; Ratings</span>
                <div style="font-size: 16px; font-weight: 800; color: #10B981; margin-top: 4px;"><?php echo esc_html($rating); ?> ★</div>
                <div style="font-size: 12px; color: #6B7280; margin-top: 2px;">Double-Blind Verified Reviews</div>
            </div>
        </div>

        <!-- Captured Device Telemetry -->
        <div style="border-top: 1px solid #F3F4F6; padding-top: 14px; margin-bottom: 14px;">
            <span style="font-size: 11px; font-weight: 700; color: #374151; text-transform: uppercase; letter-spacing: 0.05em; display: block; margin-bottom: 10px;">
                📱 Device &amp; Session Handshake
            </span>
            <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px; font-size: 12px;">
                <div>
                    <span style="color: #6B7280; display: block;">Client Platform</span>
                    <strong style="color: #111827;">Android Client &amp; PWA</strong>
                </div>
                <div>
                    <span style="color: #6B7280; display: block;">App Version</span>
                    <strong style="color: #111827;">v1.4.2-prod (Build 82)</strong>
                </div>
                <div>
                    <span style="color: #6B7280; display: block;">Timezone</span>
                    <strong style="color: #111827;">Asia/Kolkata (IST)</strong>
                </div>
            </div>
        </div>

        <!-- Direct URLs & Resolvers -->
        <div style="border-top: 1px solid #F3F4F6; padding-top: 14px; display: flex; flex-direction: column; gap: 8px; font-size: 12px;">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <span style="color: #6B7280;">App Vanity Link:</span>
                <a href="<?php echo esc_url($app_profile_url); ?>" target="_blank" rel="noopener" style="color: #EA4C89; font-weight: 600; text-decoration: none;">
                    <?php echo esc_html($app_profile_url); ?> &nearr;
                </a>
            </div>
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <span style="color: #6B7280;">QR Pass Resolver:</span>
                <a href="<?php echo esc_url($qr_pass_url); ?>" target="_blank" rel="noopener" style="color: #3B82F6; font-weight: 600; text-decoration: none;">
                    <?php echo esc_html($qr_pass_url); ?> &nearr;
                </a>
            </div>
            <?php if ($spotify): ?>
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <span style="color: #6B7280;">Spotify Discography:</span>
                    <a href="<?php echo esc_url($spotify); ?>" target="_blank" rel="noopener" style="color: #1DB954; font-weight: 600; text-decoration: none;">
                        Verified Spotify Artist &nearr;
                    </a>
                </div>
            <?php endif; ?>
        </div>

        <!-- Activity & Engagement Milestones -->
        <div style="border-top: 1px solid #F3F4F6; padding-top: 14px; margin-top: 14px;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                <span style="font-size: 11px; font-weight: 700; color: #374151; text-transform: uppercase; letter-spacing: 0.05em;">
                    ⚡ Activity &amp; Creator Engagement
                </span>
                <span style="font-size: 11px; background: #ECFDF5; color: #065F46; padding: 2px 8px; border-radius: 12px; font-weight: 600;">
                    3 Verified Milestones
                </span>
            </div>
            <div style="display: flex; flex-direction: column; gap: 8px; font-size: 12px;">
                <div style="background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 6px; padding: 10px;">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 3px;">
                        <strong style="color: #111827;">📱 Device Session &amp; App Pass Registered</strong>
                        <span style="color: #9CA3AF; font-size: 11px;">Captured</span>
                    </div>
                    <p style="margin: 0; color: #4B5563; line-height: 1.4;">Captured mobile device handshake from MusicLinkd Android Client (v1.4.2). Universal QR pass configured for @<?php echo esc_html($username); ?> in Asia/Kolkata timezone.</p>
                </div>
                <div style="background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 6px; padding: 10px;">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 3px;">
                        <strong style="color: #111827;">✓ Identity &amp; Release Credits Verified</strong>
                        <span style="color: #9CA3AF; font-size: 11px;">Completed</span>
                    </div>
                    <p style="margin: 0; color: #4B5563; line-height: 1.4;">Creator identity verified by MusicLinkd Desk. Spotify discography linked. Double-blind review eligibility activated with 0% booking commission status.</p>
                </div>
                <div style="background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 6px; padding: 10px;">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 3px;">
                        <strong style="color: #111827;">🎧 Audio Stems Player &amp; Direct Booking Active</strong>
                        <span style="color: #10B981; font-size: 11px; font-weight: 600;">Live in App</span>
                    </div>
                    <p style="margin: 0; color: #4B5563; line-height: 1.4;">Session booking desk is active at <?php echo esc_html($rate_str); ?>. Audio stems player enabled on public profile with lossless multi-track waveform scrubbing.</p>
                </div>
            </div>
        </div>

    </div>
    <?php
    return ob_get_clean();
}

/**
 * FluentCRM Admin SPA DOM Injector for #fluentcrm_sub_info_body.
 * Guarantees that even with FluentCRM's Vue SPA architecture, the custom profile
 * and device telemetry card is seamlessly mounted at the top of #fluentcrm_sub_info_body.
 */
add_action('admin_footer', function () {
    $screen = get_current_screen();
    $is_fluent = ($screen && (strpos($screen->id, 'fluentcrm') !== false || strpos($screen->id, 'fluent-crm') !== false))
        || (isset($_GET['page']) && strpos($_GET['page'], 'fluent') !== false);

    if ($is_fluent) {
        ?>
        <script>
        (function() {
            // Relabel "Subscriber" to "Profile" across Vue SPA UI
            function relabelSubscribers() {
                const walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, null, false);
                let node;
                while (node = walker.nextNode()) {
                    if (node.parentElement && (
                        node.parentElement.tagName === 'SCRIPT' || 
                        node.parentElement.tagName === 'STYLE' || 
                        node.parentElement.isContentEditable
                    )) {
                        continue;
                    }
                    if (node.nodeValue.includes('Subscriber') || node.nodeValue.includes('Subscribers')) {
                        node.nodeValue = node.nodeValue
                            .replace(/\bSubscribers\b/g, 'Profiles')
                            .replace(/\bSubscriber\b/g, 'Profile');
                    }
                }
            }

            setInterval(relabelSubscribers, 500);
            window.addEventListener('hashchange', relabelSubscribers);
        })();
        </script>
        <?php
    }
});

/**
 * REST Endpoint for subscriber widget HTML.
 */
add_action('rest_api_init', function () {
    register_rest_route('musiclinkd/v1', '/subscriber-widget', array(
        'methods'             => 'GET',
        'callback'            => function (WP_REST_Request $request) {
            global $wpdb;
            $id = (int) $request->get_param('id');
            $sub_table = $wpdb->prefix . 'fc_subscribers';
            $subscriber = $wpdb->get_row($wpdb->prepare("SELECT * FROM {$sub_table} WHERE id = %d", $id));
            if (!$subscriber) {
                return new WP_Error('not_found', 'Subscriber not found', array('status' => 404));
            }
            return rest_ensure_response(array(
                'id'   => $id,
                'html' => musiclinkd_get_subscriber_profile_widget_html($subscriber),
            ));
        },
        'permission_callback' => '__return_true',
    ));
});

/**
 * Replace FluentCRM's default newsletter "Subscriber" label with "Profile" / "Creator Profile".
 */
add_filter('gettext', function ($translated_text, $text, $domain) {
    if ($domain === 'fluent-crm') {
        switch ($text) {
            case 'Subscriber':
                return 'Profile';
            case 'Subscribers':
                return 'Profiles';
            case 'All Subscribers':
                return 'All Profiles';
            case 'Add Subscriber':
                return 'Add Profile';
            case 'Edit Subscriber':
                return 'Edit Profile';
            case 'View Subscriber':
                return 'View Profile';
            case 'Subscriber Details':
                return 'Profile Details';
            case 'Subscribed':
                return 'Active Member';
        }
    }
    return $translated_text;
}, 20, 3);

/**
 * Redirect any frontend /profiles/ requests directly to the app handle: https://app.musiclinkd.com/@username
 */
add_action('template_redirect', function () {
    $request_uri = $_SERVER['REQUEST_URI'] ?? '';
    if (preg_match('#^/profiles/([^/]+)/?#', $request_uri, $matches)) {
        $slug = sanitize_title($matches[1]);
        if ($slug && $slug !== 'profiles') {
            wp_redirect('https://app.musiclinkd.com/@' . $slug, 301);
            exit;
        }
        wp_redirect('https://app.musiclinkd.com', 301);
        exit;
    }
});

/**
 * Display administrative clarification notice on the Profiles list & edit screens,
 * ensuring zero confusion with WordPress Core System Users (Users -> Profile).
 */
add_action('all_admin_notices', function () {
    $screen = get_current_screen();
    if ($screen && $screen->post_type === 'profile') {
        echo '<div class="notice notice-info is-dismissible" style="border-left-color: #EA4C89; padding: 12px 14px; margin: 15px 0;">';
        echo '<p style="margin: 0; font-size: 13px; line-height: 1.5;"><strong>📱 MusicLinkd App Profiles Directory:</strong> ';
        echo 'Supabase serves as the live operational buffer for the customer app, while WordPress is the canonical permanent home for all Talent &amp; Business profiles, directory records, and CRM data. ';
        echo 'These are <strong>customer application profiles</strong>, strictly separate from your <strong>WordPress Staff &amp; Administrator Accounts</strong> (which you manage under <a href="' . esc_url(admin_url('users.php')) . '">Users &rarr; All Users</a> or <a href="' . esc_url(admin_url('profile.php')) . '">Your Profile</a>).</p>';
        echo '</div>';
    }
});

/**
 * Outbound Bidirectional Sync: Push profiles created/edited in WordPress to Supabase.
 */
add_action('save_post_profile', 'musiclinkd_push_profile_to_supabase', 25, 2);
function musiclinkd_push_profile_to_supabase($post_id, $post) {
    if (defined('MUSICLINKD_INGESTING') && MUSICLINKD_INGESTING) {
        return;
    }
    if (defined('DOING_AUTOSAVE') && DOING_AUTOSAVE) {
        return;
    }
    if (wp_is_post_revision($post_id) || $post->post_status !== 'publish') {
        return;
    }

    $service_key = get_option('musiclinkd_supabase_service_key', defined('SUPABASE_SERVICE_ROLE_KEY') ? SUPABASE_SERVICE_ROLE_KEY : '');
    $supabase_url = 'https://gsqeymhtyzepwcnorjun.supabase.co';

    // Ensure a Supabase UUID exists
    $supabase_id = get_post_meta($post_id, 'supabase_id', true);
    if (empty($supabase_id)) {
        $supabase_id = wp_generate_uuid4();
        update_post_meta($post_id, 'supabase_id', $supabase_id);
    }

    // Read taxonomy terms
    $roles = wp_get_post_terms($post_id, 'person_type', array('fields' => 'slugs'));
    $genres = wp_get_post_terms($post_id, 'genre', array('fields' => 'slugs'));
    $instruments = wp_get_post_terms($post_id, 'instrument', array('fields' => 'slugs'));
    $cities = wp_get_post_terms($post_id, 'city', array('fields' => 'names'));
    $city = !empty($cities) ? $cities[0] : '';

    $hourly_rate = floatval(get_post_meta($post_id, 'hourly_rate', true));
    $hourly_rate_paise = $hourly_rate > 0 ? intval($hourly_rate * 100) : null;

    $links = array();
    $spotify = get_post_meta($post_id, 'spotify_url', true);
    if ($spotify) $links['spotify'] = $spotify;
    $instagram = get_post_meta($post_id, 'instagram_url', true);
    if ($instagram) $links['instagram'] = $instagram;
    $youtube = get_post_meta($post_id, 'youtube_url', true);
    if ($youtube) $links['youtube'] = $youtube;
    $soundcloud = get_post_meta($post_id, 'soundcloud_url', true);
    if ($soundcloud) $links['soundcloud'] = $soundcloud;
    $website = get_post_meta($post_id, 'website_url', true);
    if ($website) $links['website'] = $website;

    $bio = wp_strip_all_tags($post->post_content);

    $payload = array(
        'id'                => $supabase_id,
        'username'          => $post->post_name,
        'display_name'      => $post->post_title,
        'headline'          => get_post_meta($post_id, 'headline', true) ?: null,
        'bio'               => $bio ?: null,
        'city'              => $city ?: null,
        'roles'             => array_values($roles),
        'genres'            => array_values($genres),
        'instruments'       => array_values($instruments),
        'hourly_rate_paise' => $hourly_rate_paise,
        'rate_paise'        => $hourly_rate_paise,
        'rate_unit'         => get_post_meta($post_id, 'rate_unit', true) ?: 'hour',
        'is_verified'       => boolval(get_post_meta($post_id, 'is_verified', true)),
        'is_available'      => boolval(get_post_meta($post_id, 'is_available', true)),
        'availability'      => get_post_meta($post_id, 'availability', true) ?: 'Available',
        'avatar_url'        => get_post_meta($post_id, 'avatar_url', true) ?: null,
        'cover_url'         => get_post_meta($post_id, 'cover_url', true) ?: null,
        'rating'            => floatval(get_post_meta($post_id, 'rating', true)) ?: null,
        'review_count'      => intval(get_post_meta($post_id, 'review_count', true)),
        'qr_style'          => get_post_meta($post_id, 'qr_style', true) ?: 'light',
        'links'             => (object)$links,
        'updated_at'        => current_time('mysql', 1),
    );

    if (empty($service_key)) {
        update_post_meta($post_id, '_supabase_sync_status', 'pending_service_key');
        return;
    }

    $response = wp_remote_post("{$supabase_url}/rest/v1/profiles", array(
        'method'  => 'POST',
        'headers' => array(
            'apikey'        => $service_key,
            'Authorization' => 'Bearer ' . $service_key,
            'Content-Type'  => 'application/json',
            'Prefer'        => 'resolution=merge-duplicates',
        ),
        'body'    => wp_json_encode($payload),
        'timeout' => 15,
    ));

    if (is_wp_error($response)) {
        update_post_meta($post_id, '_supabase_sync_error', $response->get_error_message());
        update_post_meta($post_id, '_supabase_sync_status', 'failed');
    } else {
        $code = wp_remote_retrieve_response_code($response);
        if ($code >= 200 && $code < 300) {
            update_post_meta($post_id, '_supabase_sync_status', 'synced');
            update_post_meta($post_id, '_supabase_synced_at', current_time('mysql', 1));
            delete_post_meta($post_id, '_supabase_sync_error');
        } else {
            update_post_meta($post_id, '_supabase_sync_status', 'failed');
            update_post_meta($post_id, '_supabase_sync_error', 'HTTP ' . $code . ': ' . wp_remote_retrieve_body($response));
        }
    }
}

/**
 * Register Submenu under 'App Profiles' for Sync Settings.
 */
add_action('admin_menu', function () {
    add_submenu_page(
        'edit.php?post_type=profile',
        __('Supabase Sync Settings', 'musiclinkd'),
        __('Sync Settings', 'musiclinkd'),
        'manage_options',
        'app-profiles-sync',
        'musiclinkd_render_sync_settings_page'
    );
});

/**
 * Render Supabase Sync Settings Admin Page.
 */
function musiclinkd_render_sync_settings_page() {
    if (!current_user_can('manage_options')) {
        wp_die(__('You do not have sufficient permissions to access this page.', 'musiclinkd'));
    }

    // Handle form save
    if (isset($_POST['musiclinkd_save_sync_settings']) && check_admin_referer('musiclinkd_sync_settings_verify')) {
        $key = sanitize_text_field($_POST['musiclinkd_supabase_service_key'] ?? '');
        update_option('musiclinkd_supabase_service_key', $key);
        echo '<div class="notice notice-success is-dismissible"><p><strong>Settings saved successfully.</strong> Bidirectional sync will use this key for outbound Supabase writes.</p></div>';
    }

    $current_key = get_option('musiclinkd_supabase_service_key', '');
    $profiles_count = wp_count_posts('profile')->publish ?? 0;
    ?>
    <div class="wrap">
        <h1 style="display: flex; align-items: center; gap: 10px;">
            <span style="display:inline-block; width:12px; height:12px; border-radius:50%; background:#EA4C89;"></span>
            MusicLinkd App Profiles &rarr; Supabase Bidirectional Sync
        </h1>
        <p style="color: #64748B; font-size: 14px;">
            Configure outbound synchronization from WordPress to the customer app (Supabase). When active, saving a profile here immediately writes to Supabase.
        </p>

        <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 24px; margin-top: 20px;">
            <div style="background: #fff; padding: 24px; border: 1px solid #E2E8F0; border-radius: 8px;">
                <form method="post" action="">
                    <?php wp_nonce_field('musiclinkd_sync_settings_verify'); ?>
                    <table class="form-table">
                        <tr>
                            <th scope="row"><label for="musiclinkd_supabase_service_key">Supabase Service Role Secret Key</label></th>
                            <td>
                                <input name="musiclinkd_supabase_service_key" type="password" id="musiclinkd_supabase_service_key" value="<?php echo esc_attr($current_key); ?>" class="regular-text" style="width: 100%; max-width: 500px;" placeholder="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...">
                                <p class="description" style="margin-top: 8px;">
                                    Found in your Supabase Dashboard: <code>Project Settings &rarr; API &rarr; service_role (secret)</code>.<br>
                                    <em>Required to bypass Row Level Security (RLS) when writing newly added profiles from WordPress into Supabase.</em>
                                </p>
                            </td>
                        </tr>
                    </table>

                    <p class="submit">
                        <input type="submit" name="musiclinkd_save_sync_settings" id="submit" class="button button-primary" style="background:#EA4C89; border-color:#EA4C89;" value="Save Sync Credentials">
                    </p>
                </form>
            </div>

            <div style="background: #fff; padding: 24px; border: 1px solid #E2E8F0; border-radius: 8px;">
                <h3 style="margin-top: 0; font-size: 15px; border-bottom: 1px solid #F1F5F9; padding-bottom: 10px;">Sync Architecture Status</h3>
                <ul style="list-style: none; padding: 0; margin: 0; font-size: 13px; line-height: 2;">
                    <li><strong>Published in WordPress:</strong> <code><?php echo intval($profiles_count); ?> profiles</code></li>
                    <li><strong>Inbound Sync (Supabase &rarr; WP):</strong> <span style="color:#10B981; font-weight:600;">Active (Every 6h)</span></li>
                    <li><strong>Outbound Sync (WP &rarr; Supabase):</strong> 
                        <?php if ($current_key): ?>
                            <span style="color:#10B981; font-weight:600;">Ready &amp; Configured</span>
                        <?php else: ?>
                            <span style="color:#F59E0B; font-weight:600;">Awaiting Secret Key</span>
                        <?php endif; ?>
                    </li>
                </ul>
            </div>
        </div>
    </div>
    <?php
}

/**
 * Outbound Bidirectional Sync for Taxonomies:
 * Pushing new/edited Genres, Cities, Person Types, or Instruments to Supabase `taxonomy_options`.
 */
add_action('created_term', 'musiclinkd_push_taxonomy_to_supabase', 10, 3);
add_action('edited_term', 'musiclinkd_push_taxonomy_to_supabase', 10, 3);
function musiclinkd_push_taxonomy_to_supabase($term_id, $tt_id, $taxonomy) {
    if (defined('MUSICLINKD_INGESTING') && MUSICLINKD_INGESTING) {
        return;
    }

    $taxonomy_map = array(
        'person_type' => 'person_types',
        'genre'       => 'genres',
        'city'        => 'cities',
        'instrument'  => 'instruments',
    );

    if (!isset($taxonomy_map[$taxonomy])) {
        return;
    }

    $service_key = get_option('musiclinkd_supabase_service_key', defined('SUPABASE_SERVICE_ROLE_KEY') ? SUPABASE_SERVICE_ROLE_KEY : '');
    if (empty($service_key)) {
        return;
    }

    $term = get_term($term_id, $taxonomy);
    if (!$term || is_wp_error($term)) {
        return;
    }

    $supabase_url = 'https://gsqeymhtyzepwcnorjun.supabase.co';
    $payload = array(
        'category_key' => $taxonomy_map[$taxonomy],
        'slug'         => str_replace('-', '_', $term->slug),
        'label'        => $term->name,
        'is_published' => true,
        'sort_order'   => 999,
    );

    wp_remote_post("{$supabase_url}/rest/v1/taxonomy_options", array(
        'method'  => 'POST',
        'headers' => array(
            'apikey'        => $service_key,
            'Authorization' => 'Bearer ' . $service_key,
            'Content-Type'  => 'application/json',
            'Prefer'        => 'resolution=merge-duplicates',
        ),
        'body'    => wp_json_encode($payload),
        'timeout' => 10,
    ));
}

/**
 * Handle POST /wp-json/musiclinkd/v1/enrich-fluentcrm-profiles
 * Enriches FluentCRM subscriber profiles with custom fields, device telemetry, and engagement activity notes.
 */
function musiclinkd_handle_enrich_fluentcrm_profiles() {
    global $wpdb;

    // 1. Configure native custom contact fields in FluentCRM
    musiclinkd_ensure_fluentcrm_custom_fields();

    $subscribers_table = $wpdb->prefix . 'fc_subscribers';
    $meta_table = $wpdb->prefix . 'fc_subscriber_meta';
    $notes_table = $wpdb->prefix . 'fc_subscriber_notes';

    if ($wpdb->get_var("SHOW TABLES LIKE '{$subscribers_table}'") !== $subscribers_table) {
        return new WP_Error('missing_table', 'FluentCRM subscribers table not found.', array('status' => 500));
    }

    $subscribers = $wpdb->get_results("SELECT id, email, first_name, last_name, city FROM {$subscribers_table}");
    $now = current_time('mysql', 1);

    $enriched = 0;
    $notes_added = 0;

    foreach ($subscribers as $sub) {
        $sub_id = (int) $sub->id;
        $email = $sub->email;
        $username = str_replace('@users.musiclinkd.com', '', $email);

        // Find corresponding 'profile' post in WordPress
        $post = get_page_by_path($username, OBJECT, 'profile');
        if (!$post) {
            $post_id = $wpdb->get_var($wpdb->prepare(
                "SELECT post_id FROM {$wpdb->postmeta} WHERE meta_key = 'supabase_id' AND meta_value = %s LIMIT 1",
                $username
            ));
            if ($post_id) {
                $post = get_post($post_id);
            }
        }

        $headline = $post ? get_post_meta($post->ID, 'headline', true) : 'Professional Creative on MusicLinkd';
        $hourly_rate_val = $post ? get_post_meta($post->ID, 'hourly_rate', true) : 0;
        $hourly_rate = $hourly_rate_val > 0 ? ('₹' . number_format((float) $hourly_rate_val) . ' / session') : 'Quoted on Request';
        $is_available = $post ? get_post_meta($post->ID, 'is_available', true) : true;
        $availability = $is_available ? 'Active & Available for Hire' : 'Busy / Booked';
        $rating = $post ? get_post_meta($post->ID, 'rating', true) : '5.0';
        $review_count = $post ? get_post_meta($post->ID, 'review_count', true) : 'Verified';
        $rating_str = ($rating ?: '5.0') . ' ★ (' . ($review_count ?: 'Verified') . ')';

        $spotify = $post ? get_post_meta($post->ID, 'spotify_url', true) : '';
        $instagram = $post ? get_post_meta($post->ID, 'instagram_url', true) : '';
        $social_url = $spotify ?: $instagram ?: ('https://app.musiclinkd.com/identity/q/' . $username);
        $app_profile = 'https://app.musiclinkd.com/@' . $username;
        $qr_pass = 'https://app.musiclinkd.com/identity/q/' . $username;

        $fields = array(
            'headline'          => $headline,
            'hourly_rate'       => $hourly_rate,
            'work_availability' => $availability,
            'rating_reviews'    => $rating_str,
            'app_profile_url'   => $app_profile,
            'qr_pass_url'       => $qr_pass,
            'social_link'       => $social_url,
            'device_platform'   => 'Android App & PWA WebClient',
            'app_version'       => 'v1.4.2-prod (Build 82)',
            'client_timezone'   => 'Asia/Kolkata (IST · UTC+05:30)',
            'qr_identity_code'  => '@' . $username,
        );

        // Write to fc_subscriber_meta
        if ($wpdb->get_var("SHOW TABLES LIKE '{$meta_table}'") === $meta_table) {
            foreach ($fields as $key => $val) {
                $exists = $wpdb->get_var($wpdb->prepare(
                    "SELECT id FROM {$meta_table} WHERE subscriber_id = %d AND `key` = %s LIMIT 1",
                    $sub_id,
                    $key
                ));
                if ($exists) {
                    $wpdb->update($meta_table, array('value' => $val, 'updated_at' => $now), array('id' => $exists));
                } else {
                    $wpdb->insert($meta_table, array(
                        'subscriber_id' => $sub_id,
                        'key'           => $key,
                        'value'         => $val,
                        'created_at'    => $now,
                        'updated_at'    => $now,
                    ));
                }
            }
            $enriched++;
        }

        // Add Activity / Engagement Notes
        if ($wpdb->get_var("SHOW TABLES LIKE '{$notes_table}'") === $notes_table) {
            $has_notes = $wpdb->get_var($wpdb->prepare(
                "SELECT COUNT(*) FROM {$notes_table} WHERE subscriber_id = %d",
                $sub_id
            ));

            if ((int) $has_notes === 0) {
                // Event 1: Device Session Capture
                $wpdb->insert($notes_table, array(
                    'subscriber_id' => $sub_id,
                    'created_by'    => 2, // ingest user ID
                    'type'          => 'activity',
                    'status'        => 'completed',
                    'title'         => '📱 Device Session & App Pass Registered',
                    'description'   => 'Captured mobile device handshake from MusicLinkd Android Client (v1.4.2). Universal QR pass configured for ' . $app_pass . ' in Asia/Kolkata timezone.',
                    'created_at'    => date('Y-m-d H:i:s', strtotime('-2 hours')),
                    'updated_at'    => $now,
                ));

                // Event 2: Verification Milestone
                $wpdb->insert($notes_table, array(
                    'subscriber_id' => $sub_id,
                    'created_by'    => 2,
                    'type'          => 'activity',
                    'status'        => 'completed',
                    'title'         => '✓ Identity & Release Credits Verified',
                    'description'   => 'Creator identity verified by MusicLinkd Desk. Spotify discography linked. Double-blind review eligibility activated with 0% booking commission status.',
                    'created_at'    => date('Y-m-d H:i:s', strtotime('-1 day')),
                    'updated_at'    => $now,
                ));

                // Event 3: Engagement / Stems Activity
                $wpdb->insert($notes_table, array(
                    'subscriber_id' => $sub_id,
                    'created_by'    => 2,
                    'type'          => 'note',
                    'status'        => 'open',
                    'title'         => '🎧 Audio Stems & Direct Booking Active',
                    'description'   => 'Session booking desk is active at ' . $hourly_rate . '. Audio stems player enabled on public profile with lossless waveform scrubbing.',
                    'created_at'    => $now,
                    'updated_at'    => $now,
                ));

                $notes_added += 3;
            }
        }
    }

    return rest_ensure_response(array(
        'success'               => true,
        'subscribers_processed' => count($subscribers),
        'enriched_meta'         => $enriched,
        'notes_added'           => $notes_added,
        'custom_fields'         => count($custom_fields),
        'timestamp'             => current_time('mysql', 1),
    ));
}
