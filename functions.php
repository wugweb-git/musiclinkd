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
