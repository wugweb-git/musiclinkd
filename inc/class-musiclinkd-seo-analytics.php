<?php
/**
 * MusicLinkd — Rank Math SEO, Google Site Kit & GTM / GA4 Event Layer.
 *
 * 1. Rank Math Filters:
 *    - Custom page titles, meta descriptions, canonical URLs, and robots indexing.
 *    - Injects MusicOrganization JSON-LD structured data into Rank Math's @graph.
 *    - Injects focused keyphrases and focused keyword tags.
 * 2. Google Site Kit Integration:
 *    - Configures cross-domain tracking linker between musiclinkd.com and app.musiclinkd.com.
 * 3. GTM & GA4 Event Tracking:
 *    - Tracks app handoffs (clicks to https://app.musiclinkd.com).
 *    - Tracks CTA button clicks, Fluent Forms waitlist conversions, and social interactions.
 *    - Provides a standardized event bus (musiclinkd:track).
 * 4. AI Search & LLMs Discovery:
 *    - Dynamically serves /llms.txt for AI crawlers and LLM search agents.
 *
 * @package MusicLinkd
 */

if (!defined('ABSPATH')) {
    exit;
}

class MusicLinkd_SEO_Analytics {

    /**
     * Initialize all SEO, Rank Math, Site Kit, and Analytics hooks.
     */
    public static function init() {
        // Rank Math Metadata & Schema Filters
        add_filter('rank_math/frontend/title', array(__CLASS__, 'filter_rank_math_title'), 20);
        add_filter('rank_math/frontend/description', array(__CLASS__, 'filter_rank_math_description'), 20);
        add_filter('rank_math/frontend/canonical', array(__CLASS__, 'filter_rank_math_canonical'), 20);
        add_filter('rank_math/frontend/robots', array(__CLASS__, 'filter_rank_math_robots'), 20);
        add_filter('rank_math/json_ld', array(__CLASS__, 'enrich_rank_math_json_ld'), 20, 2);
        add_filter('rank_math/focus_keyword', array(__CLASS__, 'filter_rank_math_focus_keyword'), 20);

        // Google Site Kit Cross-Domain Linker
        add_filter('googlesitekit_gtag_opt', array(__CLASS__, 'filter_sitekit_gtag_options'), 20);

        // HTML Head: AI Discovery (llms.txt) & Focused Meta Keywords
        add_action('wp_head', array(__CLASS__, 'render_seo_and_ai_head'), 5);

        // Dynamic /llms.txt Virtual Route
        add_action('template_redirect', array(__CLASS__, 'maybe_serve_llms_txt'), 1);

        // Footer: Google Tag Manager & GA4 dataLayer event listeners
        add_action('wp_footer', array(__CLASS__, 'render_datalayer_events'), 99);
    }

    /**
     * Google Site Kit Tag Options: Link domains between marketing website and mobile web app.
     */
    public static function filter_sitekit_gtag_options($gtag_opts) {
        if (!is_array($gtag_opts)) {
            $gtag_opts = array();
        }
        $gtag_opts['linker'] = array(
            'domains'        => array('musiclinkd.com', 'app.musiclinkd.com'),
            'decorate_forms' => true,
        );
        return $gtag_opts;
    }

    /**
     * Canonical Route Metadata Map for MusicLinkd core slugs.
     *
     * @return array<string, array<string, string>>
     */
    public static function get_route_seo_catalog() {
        return array(
            'home' => array(
                'title'          => 'MusicLinkd — India’s Professional Music Network · Talent, Studios & Gigs',
                'description'    => 'India’s premier professional ecosystem for music talent, recording studios, session musicians, producers, and venues. 0% platform fee, verified identity pass, and escrow protection.',
                'canonical'      => 'https://musiclinkd.com/',
                'focus_keyword'  => 'music talent network India',
                'keywords'       => 'music talent network India, hire session musicians India, recording studios Mumbai, music producers Bangalore, artist booking India, music collaboration platform',
            ),
            'features' => array(
                'title'          => 'Platform Features — Multi-Track Stems, QR Identity & Studio Desk | MusicLinkd',
                'description'    => 'Explore MusicLinkd’s integrated creator suite: lossless multi-track stems player, dynamic QR identity passes, verified ratings, and double-blind escrow agreements.',
                'canonical'      => 'https://musiclinkd.com/features/',
                'focus_keyword'  => 'music collaboration tools India',
                'keywords'       => 'music collaboration tools, lossless stems player, audio stem sharing, artist QR identity pass, music contract escrow',
            ),
            'how-it-works' => array(
                'title'          => 'How It Works — Step-by-Step for Talent, Studios & Clients | MusicLinkd',
                'description'    => 'Learn how musicians get booked, studios rent rooms, and clients hire vetted audio talent in India with zero commission and instant payouts.',
                'canonical'      => 'https://musiclinkd.com/how-it-works/',
                'focus_keyword'  => 'hire session musicians India',
                'keywords'       => 'how to hire musicians India, book recording studio India, session artist workflow, music gigs India',
            ),
            'pricing' => array(
                'title'          => 'Fair Pricing & 0% Platform Fees — Your Work Stays Yours | MusicLinkd',
                'description'    => 'Transparent, creator-first pricing. 0% commission on peer-to-peer bookings. Studio desk subscriptions and talent tier breakdowns.',
                'canonical'      => 'https://musiclinkd.com/pricing/',
                'focus_keyword'  => 'music production rates India',
                'keywords'       => 'recording studio rates India, session musician pricing, music production fees, zero commission music platform',
            ),
            'faq' => array(
                'title'          => 'Frequently Asked Questions — Accounts, Verification & Bookings | MusicLinkd',
                'description'    => 'Answers to common questions about artist verification, booking agreements, direct UPI payouts, stem security, and studio management.',
                'canonical'      => 'https://musiclinkd.com/faq/',
                'focus_keyword'  => 'musiclinkd faq verification',
                'keywords'       => 'musiclinkd faq, artist verification India, studio booking cancellation, music escrow India',
            ),
            'safety' => array(
                'title'          => 'Trust, Safety & Creator Rights Guarantee — Your Work Stays Yours | MusicLinkd',
                'description'    => 'Our ironclad creator rights guarantee: you retain 100% of your copyright, master rights, and royalties. Double-blind verified reviews prevent retaliation.',
                'canonical'      => 'https://musiclinkd.com/safety/',
                'focus_keyword'  => 'music rights guarantee India',
                'keywords'       => 'music copyright protection India, artist rights guarantee, double-blind reviews music, safe music bookings',
            ),
            'about' => array(
                'title'          => 'About MusicLinkd — Building India’s Music Industry Infrastructure',
                'description'    => 'Our mission is to eliminate middlemen, unify fragmented music directories, and provide every music professional in India a verified digital identity.',
                'canonical'      => 'https://musiclinkd.com/about/',
                'focus_keyword'  => 'music industry platform India',
                'keywords'       => 'about MusicLinkd, Indian music industry startup, music tech India, creator economy music',
            ),
            'help' => array(
                'title'          => 'Help Centre & Creator Guides — Master Your Profile & Bookings | MusicLinkd',
                'description'    => 'Knowledge base, deep guides, and practical playbooks for setting rates, optimizing your stems portfolio, and booking clients on MusicLinkd.',
                'canonical'      => 'https://musiclinkd.com/help/',
                'focus_keyword'  => 'musiclinkd creator guides',
                'keywords'       => 'musiclinkd help center, musician profile guide, how to price music services, studio management guide',
            ),
            'contact' => array(
                'title'          => 'Contact Creator Desk & Studio Support | MusicLinkd',
                'description'    => 'Get in touch with the MusicLinkd team for studio onboarding, enterprise partnerships, or creator desk assistance. Fast 24-hour response.',
                'canonical'      => 'https://musiclinkd.com/contact/',
                'focus_keyword'  => 'contact musiclinkd',
                'keywords'       => 'contact MusicLinkd, music industry partnerships, studio support India, artist desk support',
            ),
            'privacy' => array(
                'title'          => 'Privacy Policy — Privacy First & Creator Data Ownership | MusicLinkd',
                'description'    => 'How MusicLinkd protects your personal information, contact credentials, and session files. We never sell your data.',
                'canonical'      => 'https://musiclinkd.com/privacy/',
                'focus_keyword'  => 'musiclinkd privacy policy',
                'keywords'       => 'MusicLinkd privacy policy, creator data privacy, session file security',
            ),
            'terms' => array(
                'title'          => 'Terms of Service — Creator Agreements & Escrow Protocols | MusicLinkd',
                'description'    => 'The terms governing the MusicLinkd platform, verified identity QR codes, peer-to-peer bookings, and community conduct.',
                'canonical'      => 'https://musiclinkd.com/terms/',
                'focus_keyword'  => 'musiclinkd terms of service',
                'keywords'       => 'MusicLinkd terms, music booking contract terms, platform terms India',
            ),
            'cookies' => array(
                'title'          => 'Cookie Policy — Essential Cookies & Transparency | MusicLinkd',
                'description'    => 'Details on our minimal use of essential authentication cookies and privacy-respecting analytics.',
                'canonical'      => 'https://musiclinkd.com/cookies/',
                'focus_keyword'  => 'musiclinkd cookie policy',
                'keywords'       => 'MusicLinkd cookies, session cookies, tracking policy',
            ),
        );
    }

    /**
     * Resolve the current route key.
     */
    public static function get_current_route_key() {
        if (is_front_page() || is_home()) {
            return 'home';
        }
        $slug = get_post_field('post_name', get_queried_object_id());
        $catalog = self::get_route_seo_catalog();
        if (isset($catalog[$slug])) {
            return $slug;
        }
        return '';
    }

    public static function filter_rank_math_title($title) {
        $key = self::get_current_route_key();
        $catalog = self::get_route_seo_catalog();
        if ($key && !empty($catalog[$key]['title'])) {
            return $catalog[$key]['title'];
        }
        return $title;
    }

    public static function filter_rank_math_description($description) {
        $key = self::get_current_route_key();
        $catalog = self::get_route_seo_catalog();
        if ($key && !empty($catalog[$key]['description'])) {
            return $catalog[$key]['description'];
        }
        return $description;
    }

    public static function filter_rank_math_canonical($canonical) {
        $key = self::get_current_route_key();
        $catalog = self::get_route_seo_catalog();
        if ($key && !empty($catalog[$key]['canonical'])) {
            return $catalog[$key]['canonical'];
        }
        return $canonical;
    }

    public static function filter_rank_math_robots($robots) {
        if (is_array($robots)) {
            $robots['index']  = 'index';
            $robots['follow'] = 'follow';
            $robots['max-snippet'] = '-1';
            $robots['max-image-preview'] = 'large';
            $robots['max-video-preview'] = '-1';
        }
        return $robots;
    }

    public static function filter_rank_math_focus_keyword($keyword) {
        $key = self::get_current_route_key();
        $catalog = self::get_route_seo_catalog();
        if ($key && !empty($catalog[$key]['focus_keyword'])) {
            return $catalog[$key]['focus_keyword'];
        }
        return $keyword;
    }

    /**
     * Enrich Rank Math JSON-LD @graph with MusicOrganization schema.
     */
    public static function enrich_rank_math_json_ld($data, $jsonld) {
        if (!is_array($data)) {
            return $data;
        }

        $data['musiclinkdOrganization'] = array(
            '@type'            => 'MusicOrganization',
            '@id'              => 'https://musiclinkd.com/#organization',
            'name'             => 'MusicLinkd',
            'legalName'        => 'MusicLinkd Technologies',
            'url'              => 'https://musiclinkd.com/',
            'logo'             => 'https://musiclinkd.com/assets/img/brand/logo.svg',
            'email'            => 'contact@musiclinkd.com',
            'description'      => 'India’s professional music network connecting verified talent, session musicians, recording studios, sound engineers, producers, and venues with 0% platform fees.',
            'foundingLocation' => array(
                '@type'   => 'Place',
                'name'    => 'India',
                'address' => array(
                    '@type'          => 'PostalAddress',
                    'addressCountry' => 'IN',
                ),
            ),
            'sameAs'           => array(
                'https://app.musiclinkd.com/',
                'https://www.instagram.com/musiclinkd/',
                'https://open.spotify.com/user/musiclinkd',
                'https://www.linkedin.com/company/musiclinkd',
                'https://www.youtube.com/@musiclinkd',
                'https://twitter.com/musiclinkd',
            ),
            'potentialAction'  => array(
                '@type'       => 'SearchAction',
                'target'      => 'https://app.musiclinkd.com/search?q={search_term_string}',
                'query-input' => 'required name=search_term_string',
            ),
        );

        return $data;
    }

    /**
     * Render AI Context Discovery Link (/llms.txt) and Focused Meta Keywords in <head>.
     */
    public static function render_seo_and_ai_head() {
        echo '<link rel="alternate" type="text/plain" href="https://musiclinkd.com/llms.txt" title="AI Context Discovery" />' . "\n";
        
        $key = self::get_current_route_key();
        $catalog = self::get_route_seo_catalog();
        if ($key && !empty($catalog[$key]['keywords'])) {
            echo '<meta name="keywords" content="' . esc_attr($catalog[$key]['keywords']) . '" />' . "\n";
        }
    }

    /**
     * Dynamically serve /llms.txt for AI Search Engines & LLMs.
     */
    public static function maybe_serve_llms_txt() {
        $path = trim(parse_url($_SERVER['REQUEST_URI'] ?? '', PHP_URL_PATH), '/');
        if ('llms.txt' !== $path) {
            return;
        }

        header('Content-Type: text/plain; charset=utf-8');
        header('Cache-Control: public, max-age=3600');
        
        echo "# MusicLinkd (musiclinkd.com / app.musiclinkd.com)\n\n";
        echo "> India's professional music industry ecosystem connecting verified talent, session musicians, recording studios, sound engineers, and venues with 0% platform fees.\n\n";
        
        echo "## Core Platform Modules\n";
        echo "- Verified Talent & Business Directory: https://app.musiclinkd.com/discover\n";
        echo "- Lossless Multi-track Stems Player: Integrated WebAudio waveform player supporting multi-track stem auditioning.\n";
        echo "- Dynamic QR Identity Pass: Instant profile and booking verification at https://app.musiclinkd.com/identity/q/:username\n";
        echo "- Studio Booking Desk & Room Rental: Direct room, engineer, and gear reservations across 22+ verified recording studios.\n";
        echo "- Industry Gigs & Opportunities: Open casting, vocal gigs, and production contracts at https://app.musiclinkd.com/opportunities\n";
        echo "- Help Centre & Guides: https://musiclinkd.com/help/\n\n";

        echo "## Ecosystem Personas\n";
        echo "- Talent Users: Singers, playback vocalists, session guitarists, keyboardists, drummers, mixing/mastering engineers, music directors, lyricists.\n";
        echo "- Business Users: Commercial recording studios, jam pads, indie record labels, live music venues, artist management agencies.\n\n";

        echo "## Guarantees & Terms\n";
        echo "- Commission: 0% Platform Fee on peer-to-peer artist transactions.\n";
        echo "- Copyright: 100% Creator Rights Retention ('Your work stays yours').\n";
        echo "- Reviews: Double-blind verified feedback preventing client or artist retaliation.\n";
        echo "- Payment Protection: Direct UPI/NEFT peer-to-peer agreements with milestone deliverables.\n\n";

        echo "## Sitemaps & Feeds\n";
        echo "- Website Sitemap: https://musiclinkd.com/sitemap_index.xml\n";
        echo "- Web App Sitemap: https://app.musiclinkd.com/sitemap.xml\n";
        echo "- Support & Contact: contact@musiclinkd.com\n";
        exit;
    }

    /**
     * Render GTM & GA4 DataLayer Event Handlers in <footer>.
     */
    public static function render_datalayer_events() {
        ?>
        <script>
        (function() {
            window.dataLayer = window.dataLayer || [];
            function emit(eventName, params) {
                var payload = Object.assign({ event: eventName }, params || {});
                window.dataLayer.push(payload);
                if (typeof window.gtag === 'function') {
                    window.gtag('event', eventName, params || {});
                }
            }

            // 1. App Handoff Tracking: Clicks to app.musiclinkd.com
            document.addEventListener('click', function(e) {
                var link = e.target && e.target.closest ? e.target.closest('a') : null;
                if (!link || !link.href) return;

                if (link.href.indexOf('https://app.musiclinkd.com') === 0) {
                    emit('app_handoff', {
                        destination_url: link.href.split('?')[0],
                        source_page: window.location.pathname,
                        link_text: (link.textContent || '').trim().substring(0, 50)
                    });
                } else if (link.href.indexOf('mailto:') === 0) {
                    emit('contact_email_click', { email: link.href.replace('mailto:', '') });
                } else if (link.href.indexOf('tel:') === 0) {
                    emit('contact_phone_click', { phone: link.href.replace('tel:', '') });
                } else if (link.classList.contains('social-link') || link.closest('.footer-social')) {
                    emit('social_profile_click', { platform_url: link.href });
                } else if (link.classList.contains('cta-pill') || link.classList.contains('button') || link.closest('.wp-block-button')) {
                    emit('cta_button_click', {
                        button_text: (link.textContent || '').trim().substring(0, 50),
                        target_url: link.href,
                        source_page: window.location.pathname
                    });
                }
            }, { passive: true });

            // 2. Fluent Forms Waitlist / Contact Submission Tracking
            if (window.jQuery) {
                window.jQuery(document).on('fluentform_submission_success', function(e, data) {
                    emit('waitlist_lead_submit', {
                        form_id: data ? data.form_id : 'unknown',
                        source_page: window.location.pathname
                    });
                });
            }

            // 3. Custom MusicLinkd Event Dispatcher
            document.addEventListener('musiclinkd:track', function(e) {
                if (e && e.detail && e.detail.name) {
                    emit(e.detail.name, e.detail.params || {});
                }
            });
        })();
        </script>
        <?php
    }
}

MusicLinkd_SEO_Analytics::init();
