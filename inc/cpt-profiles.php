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
        'name'                  => _x('Profiles', 'Post Type General Name', 'musiclinkd'),
        'singular_name'         => _x('Profile', 'Post Type Singular Name', 'musiclinkd'),
        'menu_name'             => __('Profiles', 'musiclinkd'),
        'name_admin_bar'        => __('Profile', 'musiclinkd'),
        'archives'              => __('Profile Directory', 'musiclinkd'),
        'all_items'             => __('All Profiles', 'musiclinkd'),
        'add_new_item'          => __('Add New Profile', 'musiclinkd'),
        'add_new'               => __('Add New', 'musiclinkd'),
        'new_item'              => __('New Profile', 'musiclinkd'),
        'edit_item'             => __('Edit Profile', 'musiclinkd'),
        'update_item'           => __('Update Profile', 'musiclinkd'),
        'view_item'             => __('View Profile', 'musiclinkd'),
        'search_items'          => __('Search Profiles', 'musiclinkd'),
        'not_found'             => __('No profiles found', 'musiclinkd'),
        'not_found_in_trash'    => __('No profiles found in Trash', 'musiclinkd'),
    );

    $args = array(
        'label'                 => __('Profile', 'musiclinkd'),
        'description'           => __('MusicLinkd App Profiles: Talent & Businesses', 'musiclinkd'),
        'labels'                => $labels,
        'supports'              => array('title', 'editor', 'thumbnail', 'excerpt', 'custom-fields', 'revisions'),
        'taxonomies'            => array('user_flow', 'person_type', 'genre', 'city', 'instrument'),
        'hierarchical'          => false,
        'public'                => true,
        'show_ui'               => true,
        'show_in_menu'          => true,
        'menu_position'         => 5,
        'menu_icon'             => 'dashicons-admin-users',
        'show_in_admin_bar'     => true,
        'show_in_nav_menus'     => true,
        'can_export'            => true,
        'has_archive'           => 'profiles',
        'exclude_from_search'   => false,
        'publicly_queryable'    => true,
        'capability_type'       => 'post',
        'show_in_rest'          => true,
        'rest_base'             => 'profiles',
        'rewrite'               => array('slug' => 'profiles', 'with_front' => false),
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
});

/**
 * Handle POST /wp-json/musiclinkd/v1/ingest-profiles
 */
function musiclinkd_handle_ingest_profiles(WP_REST_Request $request) {
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

    $subscribers_table = $wpdb->prefix . 'fluentcrm_subscribers';
    if ($wpdb->get_var("SHOW TABLES LIKE '{$subscribers_table}'") !== $subscribers_table) {
        return false;
    }

    $username = sanitize_title($profile['username'] ?? '');
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

    $now = current_time('mysql', 1);

    // Check if subscriber already exists
    $existing = $wpdb->get_row($wpdb->prepare(
        "SELECT id FROM {$subscribers_table} WHERE email = %s LIMIT 1",
        $email
    ));

    $subscriber_data = array(
        'first_name'   => $first_name,
        'last_name'    => $last_name,
        'email'        => $email,
        'status'       => 'subscribed',
        'contact_type' => $user_flow_label === 'Business' ? 'lead' : 'customer',
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

    return boolval($subscriber_id);
}
