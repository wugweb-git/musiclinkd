<?php
/**
 * MusicLinkd Theme Functions and Definitions
 *
 * @package MusicLinkd
 */

if (!defined('ABSPATH')) {
    exit;
}

/**
 * Enqueue Google Tag Manager (GTM-KTXBFFMK) in head.
 */
add_action('wp_head', function () {
    ?>
<!-- Google Tag Manager (GTM-KTXBFFMK) -->
<script async src="https://www.googletagmanager.com/gtm.js?id=GTM-KTXBFFMK"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());
</script>
<!-- End Google Tag Manager -->
    <?php
}, 1);

/**
 * Enqueue Google Tag Manager (noscript) after body tag opens.
 */
add_action('wp_body_open', function () {
    ?>
<!-- Google Tag Manager (noscript) -->
<noscript><iframe src="https://www.googletagmanager.com/ns.html?id=GTM-KTXBFFMK"
height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<!-- End Google Tag Manager (noscript) -->
    <?php
}, 1);

/**
 * Enqueue Core Fonts, Stylesheets, and Scripts with 100% design fidelity.
 */
add_action('wp_enqueue_scripts', function () {
    // 1. Google Fonts
    wp_enqueue_style(
        'musiclinkd-fonts',
        'https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Inter+Tight:wght@600;700;800&family=Unica+One&display=swap',
        array(),
        null
    );

    // 2. Canonical Stylesheets
    wp_enqueue_style(
        'musiclinkd-tokens',
        get_theme_file_uri('/assets/css/tokens.css'),
        array(),
        '1.0'
    );

    wp_enqueue_style(
        'musiclinkd-main',
        get_theme_file_uri('/assets/css/main.css'),
        array('musiclinkd-tokens'),
        '1.0'
    );

    wp_enqueue_style(
        'musiclinkd-enhance',
        get_theme_file_uri('/assets/css/enhance.css'),
        array('musiclinkd-main'),
        '1.0'
    );

    wp_enqueue_style(
        'musiclinkd-css',
        get_theme_file_uri('/assets/css/musiclinkd.css'),
        array('musiclinkd-enhance'),
        '1.0'
    );

    // Dequeue conflicting/redundant third-party stylesheets on frontend
    wp_dequeue_style('wppb_stylesheet');
    wp_dequeue_style('wppb_block_themes_front_end_stylesheet');

    // 3. Animation & Icon Libraries
    wp_enqueue_script(
        'gsap',
        'https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/gsap.min.js',
        array(),
        '3.12.5',
        array('strategy' => 'defer', 'in_footer' => true)
    );

    wp_enqueue_script(
        'gsap-scrolltrigger',
        'https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/ScrollTrigger.min.js',
        array('gsap'),
        '3.12.5',
        array('strategy' => 'defer', 'in_footer' => true)
    );

    wp_enqueue_script(
        'lucide-icons',
        'https://unpkg.com/lucide@latest',
        array(),
        null,
        array('strategy' => 'defer', 'in_footer' => true)
    );

    // 4. Interactive Main JS
    wp_enqueue_script(
        'musiclinkd-main-js',
        get_theme_file_uri('/assets/js/main.js'),
        array('gsap', 'gsap-scrolltrigger', 'lucide-icons'),
        '1.0',
        array('strategy' => 'defer', 'in_footer' => true)
    );
});

/**
 * Theme Setup: Identity, Logos, Editor Styles, Thumbnails.
 */
add_action('after_setup_theme', function () {
    add_theme_support('title-tag');
    add_theme_support('post-thumbnails');
    add_theme_support('custom-logo', array(
        'height'      => 100,
        'width'       => 400,
        'flex-height' => true,
        'flex-width'  => true,
    ));
    add_theme_support('site-icon');
    add_theme_support('editor-styles');

    // Register editor stylesheets for Gutenberg visual parity
    add_editor_style(array(
        'https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Inter+Tight:wght@600;700;800&family=Unica+One&display=swap',
        'assets/css/tokens.css',
        'assets/css/main.css',
        'assets/css/unified.css',
        'assets/css/enhance.css',
        'assets/css/musiclinkd.css',
    ));
});

/**
 * Enqueue Block Editor Assets in WordPress Admin (Gutenberg).
 */
add_action('enqueue_block_editor_assets', function () {
    wp_enqueue_style(
        'musiclinkd-editor-fonts',
        'https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Inter+Tight:wght@600;700;800&family=Unica+One&display=swap',
        array(),
        null
    );
    wp_enqueue_style(
        'musiclinkd-editor-tokens',
        get_theme_file_uri('/assets/css/tokens.css'),
        array(),
        '1.0'
    );
    wp_enqueue_style(
        'musiclinkd-editor-main',
        get_theme_file_uri('/assets/css/main.css'),
        array('musiclinkd-editor-tokens'),
        '1.0'
    );
    wp_enqueue_style(
        'musiclinkd-editor-unified',
        get_theme_file_uri('/assets/css/unified.css'),
        array('musiclinkd-editor-main'),
        '1.0'
    );
    wp_enqueue_style(
        'musiclinkd-editor-enhance',
        get_theme_file_uri('/assets/css/enhance.css'),
        array('musiclinkd-editor-unified'),
        '1.0'
    );
    wp_enqueue_style(
        'musiclinkd-editor-css',
        get_theme_file_uri('/assets/css/musiclinkd.css'),
        array('musiclinkd-editor-enhance'),
        '1.0'
    );
});

/**
 * Enable SVG upload in WordPress Media Library.
 */
add_filter('upload_mimes', function ($mimes) {
    $mimes['svg'] = 'image/svg+xml';
    $mimes['svgz'] = 'image/svg+xml';
    return $mimes;
});

/**
 * Fix SVG display in WordPress Media Library.
 */
add_filter('wp_check_filetype_and_ext', function ($data, $file, $filename, $mimes) {
    $ext = strtolower(pathinfo($filename, PATHINFO_EXTENSION));
    if ($ext === 'svg') {
        $data['ext']  = 'svg';
        $data['type'] = 'image/svg+xml';
    }
    return $data;
}, 10, 4);

/**
 * MusicLinkd Engine & Ingestion Dashboard.
 */
if (file_exists(get_theme_file_path('/inc/engine-dashboard.php'))) {
    require_once get_theme_file_path('/inc/engine-dashboard.php');
}

/**
 * MusicLinkd Creators Custom Post Type & SCF (Secure Custom Fields) Mapping.
 */
if (file_exists(get_theme_file_path('/inc/cpt-creators.php'))) {
    require_once get_theme_file_path('/inc/cpt-creators.php');
}
