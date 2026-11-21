<?php
/**
 * Title: MusicLinkd Starter Hero
 * Slug: musiclinkd/hero
 * Categories: featured, banner
 * Description: Clean starter hero block with headline, subtext, and CTA buttons.
 */
?>
<!-- wp:group {"align":"full","style":{"spacing":{"padding":{"top":"var:preset|spacing|70","bottom":"var:preset|spacing|70","left":"var:preset|spacing|30","right":"var:preset|spacing|30"}}},"backgroundColor":"background","layout":{"type":"constrained"}} -->
<div class="wp-block-group alignfull has-background-background-color has-background" style="padding-top:var(--wp--preset--spacing--70);padding-right:var(--wp--preset--spacing--30);padding-bottom:var(--wp--preset--spacing--70);padding-left:var(--wp--preset--spacing--30)">
	<!-- wp:heading {"level":1,"align":"wide","style":{"typography":{"lineHeight":"1.1"}}} -->
	<h1 class="wp-block-heading alignwide">Welcome to MusicLinkd</h1>
	<!-- /wp:heading -->

	<!-- wp:paragraph {"style":{"typography":{"fontSize":"var:preset|font-size|large"}},"textColor":"foreground-muted"} -->
	<p class="has-foreground-muted-color has-text-color" style="font-size:var(--wp--preset--font-size--large)">A modern block theme built with the full block editor. Customize your brand, layouts, and typography directly in the Site Editor.</p>
	<!-- /wp:paragraph -->

	<!-- wp:buttons -->
	<div class="wp-block-buttons">
		<!-- wp:button -->
		<div class="wp-block-button"><a class="wp-block-button__link wp-element-button" href="#">Get Started</a></div>
		<!-- /wp:button -->
		<!-- wp:button {"className":"is-style-outline"} -->
		<div class="wp-block-button is-style-outline"><a class="wp-block-button__link wp-element-button" href="#">Learn More</a></div>
		<!-- /wp:button -->
	</div>
	<!-- /wp:buttons -->
</div>
<!-- /wp:group -->
