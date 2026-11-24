<?php
/**
 * MusicLinkd Creators Custom Post Type & SCF (Secure Custom Fields) Mapping
 *
 * Implements native WordPress content entities for app creators, musicians,
 * studios, and service providers from musiclinkd-app (Supabase `profiles`).
 * Also synchronizes contacts into FluentCRM for email and marketing automation.
 *
 * @package MusicLinkd
 */

if (!defined('ABSPATH')) {
    exit;
}

/**
 * Register 'creator' Custom Post Type and Taxonomies.
 */
add_action('init', 'musiclinkd_register_creators_cpt');
function musiclinkd_register_creators_cpt() {
    $labels = array(
        'name'                  => _x('Creators', 'Post Type General Name', 'musiclinkd'),
        'singular_name'         => _x('Creator', 'Post Type Singular Name', 'musiclinkd'),
        'menu_name'             => __('Creators', 'musiclinkd'),
        'name_admin_bar'        => __('Creator', 'musiclinkd'),
        'archives'              => __('Creator Directory', 'musiclinkd'),
        'all_items'             => __('All Creators', 'musiclinkd'),
        'add_new_item'          => __('Add New Creator', 'musiclinkd'),
        'add_new'               => __('Add New', 'musiclinkd'),
        'new_item'              => __('New Creator', 'musiclinkd'),
        'edit_item'             => __('Edit Creator', 'musiclinkd'),
        'update_item'           => __('Update Creator', 'musiclinkd'),
        'view_item'             => __('View Creator', 'musiclinkd'),
        'search_items'          => __('Search Creators', 'musiclinkd'),
        'not_found'             => __('No creators found', 'musiclinkd'),
        'not_found_in_trash'    => __('No creators found in Trash', 'musiclinkd'),
    );

    $args = array(
        'label'                 => __('Creator', 'musiclinkd'),
        'description'           => __('Verified Creators and Music Service Providers from MusicLinkd App', 'musiclinkd'),
        'labels'                => $labels,
        'supports'              => array('title', 'editor', 'thumbnail', 'excerpt', 'custom-fields', 'revisions'),
        'taxonomies'            => array('creator_role', 'creator_genre', 'creator_city', 'creator_instrument'),
        'hierarchical'          => false,
        'public'                => true,
        'show_ui'               => true,
        'show_in_menu'          => true,
        'menu_position'         => 5,
        'menu_icon'             => 'dashicons-groups',
        'show_in_admin_bar'     => true,
        'show_in_nav_menus'     => true,
        'can_export'            => true,
        'has_archive'           => 'creators',
        'exclude_from_search'   => false,
        'publicly_queryable'    => true,
        'capability_type'       => 'post',
        'show_in_rest'          => true,
        'rest_base'             => 'creators',
        'rewrite'               => array('slug' => 'creators', 'with_front' => false),
    );
    register_post_type('creator', $args);

    // Taxonomy: Disciplines & Roles
    register_taxonomy('creator_role', array('creator'), array(
        'labels'            => array(
            'name'          => __('Disciplines & Roles', 'musiclinkd'),
            'singular_name' => __('Discipline', 'musiclinkd'),
            'search_items'  => __('Search Disciplines', 'musiclinkd'),
            'all_items'     => __('All Disciplines', 'musiclinkd'),
            'edit_item'     => __('Edit Discipline', 'musiclinkd'),
            'update_item'   => __('Update Discipline', 'musiclinkd'),
            'add_new_item'  => __('Add New Discipline', 'musiclinkd'),
            'new_item_name' => __('New Discipline Name', 'musiclinkd'),
            'menu_name'     => __('Disciplines', 'musiclinkd'),
        ),
        'hierarchical'      => false,
        'show_ui'           => true,
        'show_admin_column' => true,
        'query_var'         => true,
        'show_in_rest'      => true,
        'rewrite'           => array('slug' => 'creators/discipline'),
    ));

    // Taxonomy: Genres
    register_taxonomy('creator_genre', array('creator'), array(
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
        'rewrite'           => array('slug' => 'creators/genre'),
    ));

    // Taxonomy: Cities
    register_taxonomy('creator_city', array('creator'), array(
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
        'rewrite'           => array('slug' => 'creators/city'),
    ));

    // Taxonomy: Instruments
    register_taxonomy('creator_instrument', array('creator'), array(
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
        'rewrite'           => array('slug' => 'creators/instrument'),
    ));
}

/**
 * Register Native Post Meta for REST API exposure.
 */
add_action('init', 'musiclinkd_register_creator_meta');
function musiclinkd_register_creator_meta() {
    $meta_fields = array(
        'headline'         => 'string',
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
        register_post_meta('creator', $field, array(
            'show_in_rest' => true,
            'single'       => true,
            'type'         => $type,
        ));
    }
}

/**
 * Register SCF (Secure Custom Fields) / ACF local field group.
 */
add_action('acf/init', 'musiclinkd_register_scf_creator_fields');
function musiclinkd_register_scf_creator_fields() {
    if (!function_exists('acf_add_local_field_group')) {
        return;
    }

    acf_add_local_field_group(array(
        'key'                   => 'group_creator_profile',
        'title'                 => __('⚡ Creator Profile Details (App Sync)', 'musiclinkd'),
        'fields'                => array(
            array(
                'key'           => 'field_creator_headline',
                'label'         => __('Headline', 'musiclinkd'),
                'name'          => 'headline',
                'type'          => 'text',
                'instructions'  => __('Professional tag/headline from the MusicLinkd App.', 'musiclinkd'),
            ),
            array(
                'key'           => 'field_creator_hourly_rate',
                'label'         => __('Hourly Rate (₹ INR)', 'musiclinkd'),
                'name'          => 'hourly_rate',
                'type'          => 'number',
                'instructions'  => __('Base booking rate in Indian Rupees.', 'musiclinkd'),
            ),
            array(
                'key'           => 'field_creator_rate_unit',
                'label'         => __('Rate Unit', 'musiclinkd'),
                'name'          => 'rate_unit',
                'type'          => 'text',
                'default_value' => 'hour',
            ),
            array(
                'key'           => 'field_creator_is_verified',
                'label'         => __('Verified Artist Badge', 'musiclinkd'),
                'name'          => 'is_verified',
                'type'          => 'true_false',
                'ui'            => 1,
            ),
            array(
                'key'           => 'field_creator_is_available',
                'label'         => __('Available for Booking / Collabs', 'musiclinkd'),
                'name'          => 'is_available',
                'type'          => 'true_false',
                'ui'            => 1,
                'default_value' => 1,
            ),
            array(
                'key'           => 'field_creator_rating',
                'label'         => __('Rating', 'musiclinkd'),
                'name'          => 'rating',
                'type'          => 'number',
                'step'          => '0.1',
            ),
            array(
                'key'           => 'field_creator_review_count',
                'label'         => __('Review Count', 'musiclinkd'),
                'name'          => 'review_count',
                'type'          => 'number',
            ),
            array(
                'key'           => 'field_creator_avatar_url',
                'label'         => __('Avatar CDN URL', 'musiclinkd'),
                'name'          => 'avatar_url',
                'type'          => 'url',
            ),
            array(
                'key'           => 'field_creator_cover_url',
                'label'         => __('Cover Photo URL', 'musiclinkd'),
                'name'          => 'cover_url',
                'type'          => 'url',
            ),
            array(
                'key'           => 'field_creator_spotify_url',
                'label'         => __('Spotify Profile', 'musiclinkd'),
                'name'          => 'spotify_url',
                'type'          => 'url',
            ),
            array(
                'key'           => 'field_creator_instagram_url',
                'label'         => __('Instagram Profile', 'musiclinkd'),
                'name'          => 'instagram_url',
                'type'          => 'url',
            ),
            array(
                'key'           => 'field_creator_supabase_id',
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
                    'value'    => 'creator',
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
 * Register REST API route for bulk creator ingestion from Supabase.
 */
add_action('rest_api_init', function () {
    register_rest_route('musiclinkd/v1', '/ingest-creators', array(
        'methods'             => array('POST'),
        'callback'            => 'musiclinkd_handle_ingest_creators',
        'permission_callback' => function () {
            return current_user_can('edit_pages');
        },
    ));
});

/**
 * Handle POST /wp-json/musiclinkd/v1/ingest-creators
 */
function musiclinkd_handle_ingest_creators(WP_REST_Request $request) {
    global $wpdb;
    $body = $request->get_json_params();

    if (empty($body) || !isset($body['profiles']) || !is_array($body['profiles'])) {
        return new WP_Error('invalid_payload', 'Payload must contain a profiles array.', array('status' => 400));
    }

    $profiles = $body['profiles'];
    $stats = array(
        'total'         => count($profiles),
        'created'       => 0,
        'updated'       => 0,
        'fluentcrm_synced' => 0,
        'errors'        => array(),
    );

    foreach ($profiles as $profile) {
        $supabase_id = sanitize_text_field($profile['id'] ?? '');
        $username    = sanitize_title($profile['username'] ?? '');
        $display_name = sanitize_text_field($profile['display_name'] ?? $profile['username'] ?? '');

        if (empty($supabase_id) || empty($username)) {
            continue;
        }

        // Check if post already exists by supabase_id meta or post_name
        $existing_post_id = $wpdb->get_var($wpdb->prepare(
            "SELECT post_id FROM {$wpdb->postmeta} WHERE meta_key = 'supabase_id' AND meta_value = %s LIMIT 1",
            $supabase_id
        ));

        if (!$existing_post_id) {
            $existing_post_id = $wpdb->get_var($wpdb->prepare(
                "SELECT ID FROM {$wpdb->posts} WHERE post_name = %s AND post_type = 'creator' LIMIT 1",
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
            'post_type'     => 'creator',
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
        if (!empty($profile['roles']) && is_array($profile['roles'])) {
            $formatted_roles = array_map(function ($r) {
                return ucwords(str_replace('_', ' ', $r));
            }, $profile['roles']);
            wp_set_object_terms($post_id, $formatted_roles, 'creator_role', false);
        }

        if (!empty($profile['genres']) && is_array($profile['genres'])) {
            $formatted_genres = array_map(function ($g) {
                return ucwords(str_replace('_', ' ', $g));
            }, $profile['genres']);
            wp_set_object_terms($post_id, $formatted_genres, 'creator_genre', false);
        }

        if (!empty($profile['instruments']) && is_array($profile['instruments'])) {
            $formatted_instruments = array_map(function ($i) {
                return ucwords(str_replace('_', ' ', $i));
            }, $profile['instruments']);
            wp_set_object_terms($post_id, $formatted_instruments, 'creator_instrument', false);
        }

        if ($city) {
            wp_set_object_terms($post_id, array($city), 'creator_city', false);
        }

        // Bridge to FluentCRM: Upsert contact directly into FluentCRM
        $fluent_synced = musiclinkd_sync_profile_to_fluentcrm($profile, $hourly_rate);
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
 * Direct FluentCRM synchronization helper.
 */
function musiclinkd_sync_profile_to_fluentcrm($profile, $hourly_rate) {
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
        'contact_type' => 'lead',
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
