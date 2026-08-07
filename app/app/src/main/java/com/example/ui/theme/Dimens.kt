package com.example.ui.theme

import androidx.compose.ui.unit.dp

/**
 * MUSICLINKD 4-TIER EXPONENTIAL SCALES
 * 
 * Strict mapping of the CSS massive overhaul tokens to Jetpack Compose `Dp`.
 * We use a strict 4-tier system to force extreme visual hierarchy and massive whitespace.
 */
object Dimens {

    // --- SPACING SCALE (4-TIER) ---
    // sp-1: Tight grouping
    val sp1 = 8.dp
    // sp-2: Component padding
    val sp2 = 16.dp
    // sp-3: Layout sections
    val sp3 = 32.dp
    // sp-4: Massive whitespace between major blocks
    val sp4 = 64.dp
    // sp-5: Ultra massive whitespace (for hero sections)
    val sp5 = 120.dp

    // --- BORDER RADIUS SCALE (4-TIER) ---
    // r-1: Inner elements (tags, small buttons)
    val r1 = 8.dp
    // r-2: Standard inputs/buttons
    val r2 = 16.dp
    // r-3: Standard cards
    val r3 = 24.dp
    // r-4: Massive blocks/sections
    val r4 = 40.dp
    // r-full: Pills
    val rFull = 9999.dp
    
    // --- BORDER WIDTH ---
    val borderThin = 1.dp
    val borderThick = 2.dp
}
