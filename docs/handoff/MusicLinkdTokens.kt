/*
 * MusicLinkdTokens.kt — GENERATED. Do not edit.
 *
 * Source:     frontend/guide/tokens/tokens.json  (v9.0.0)
 * Regenerate: node scripts/build-handoff.mjs
 *
 * The web preview at /guide is generated from this same file, so the two
 * cannot disagree. If a value here looks wrong, change tokens.json and
 * regenerate both — do not patch this file.
 *
 * Palette rule: the chrome is monochrome, the media carries all the colour.
 * Light only — dark mode is deliberately not built. See meta.darkMode in the
 * source for the decision that has to be made first if that changes.
 */
package com.musiclinkd.design

import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* The three families. Unica One ships ONE weight (400); requesting anything
   heavier makes the platform synthesise a fake bold, which on strokes this
   fine reads as a rendering fault. */
val Inter = FontFamily.Default      // replace with the bundled Inter resource
val InterTight = FontFamily.Default // replace with the bundled Inter Tight resource
val UnicaOne = FontFamily.Default   // replace with the bundled Unica One resource

/** Colour. Product palette only — the brand* expression swatches are marketing
 *  assets and are deliberately absent from the app. */
object MLColor {
    /** Primary action AND primary text */
    val Ink = Color(0xFF0D0D0D)
    /** App canvas / page background */
    val Paper = Color(0xFFFCFCFD)
    /** Card and sheet fill */
    val White = Color(0xFFFFFFFF)
    /** Subtle raised/sunken fill — search bars, inactive segments, skeletons */
    val Surface = Color(0xFFF8F8FB)
    /** Hairline borders, dividers, list separators */
    val Line = Color(0xFFECECF1)
    /** Secondary text, metadata, placeholder */
    val Muted = Color(0xFF6E6E7D)
    /** Brand accent — focus ring, selection, active tab, brand mark, waveform progress */
    val Pink = Color(0xFFEA4C89)
    /** Two jobs, one token */
    val PinkPressed = Color(0xFFC7295F)
    /** The one permitted tinted surface — accent chips, progress tracks */
    val PinkContainer = Color(0xFFFCE7F3)
    /** Pressed state of a primary (black) control — lifts off pure black so the press is visible */
    val InkPressed = Color(0xFF2B2B2B)
    /** success */
    val Success = Color(0xFF22C55E)
    /** warning */
    val Warning = Color(0xFFF59E0B)
    /** error */
    val Error = Color(0xFFDC2626)
    /** Informational badge — 'On Tour', 'Remote available' */
    val Info = Color(0xFF2563EB)
    /** Inactive control borders and text */
    val Disabled = Color(0xFF9CA3AF)
    /** Backdrop behind drawers, sheets and modals */
    val Scrim = Color(0x520D0D0D)
    /** BADGES ONLY */
    val Purple = Color(0xFF8B5CF6)
}

/** Spacing — 4-step exponential (r=2). */
object MLSpace {
    val S1 = 4.dp
    val S2 = 8.dp
    val S3 = 16.dp
    val S4 = 32.dp
}

/** Corner radius. Radius4 is a pill and is expressed as a shape, not a Dp. */
object MLRadius {
    val R1 = 8.dp
    val R2 = 16.dp
    val R3 = 32.dp
    val Pill = RoundedCornerShape(percent = 50)
}

/** Control heights. Size3 IS the 48dp touch-target minimum and Size4 the 56dp
 *  comfortable target — there is no separate touch-target token because that
 *  would be the same number under a second name. */
object MLSize {
    val S1 = 32.dp
    val S2 = 40.dp
    val S3 = 48.dp
    val S4 = 56.dp
}

/** Icon sizes. 16 and 24 are the two grids the library is actually drawn on;
 *  the others scale. */
object MLIcon {
    val I1 = 16.dp
    val I2 = 20.dp
    val I3 = 24.dp
    val I4 = 32.dp
}

/** Avatar sizes. NOT MLSize — an avatar is not a control and must not inherit
 *  a touch-target floor. */
object MLAvatar {
    val A1 = 24.dp
    val A2 = 32.dp
    val A3 = 48.dp
    val A4 = 64.dp
}

/** Border widths. Zero needs no token. */
object MLBorder {
    val B1 = 1.dp
    val B2 = 2.dp
}

/** Elevation. Prefer a hairline MLColor.Line border before reaching for a
 *  shadow — the reference apps separate surfaces with borders, not stacks. */
object MLElevation {
    val E1 = 1.dp
    val E2 = 3.dp
    val E3 = 8.dp
    val E4 = 12.dp
}

/** Opacity. */
object MLOpacity {
    /** Hover overlay (web) / ripple start (native) */
    const val Opacity1 = 0.08f
    /** Pressed feedback */
    const val Opacity2 = 0.16f
    /** Disabled content */
    const val Opacity3 = 0.32f
    /** Modal scrim / media gradient */
    const val Opacity4 = 0.64f
}

/** Font weight — 400/500/600/800. There is deliberately no 700 step: the heavy
 *  UI weight was cut from 700 to 600 on 2026-08-31, and leaving a 700 here
 *  would let it creep back one Composable at a time. Reference these rather
 *  than writing FontWeight(600) inline. */
object MLWeight {
    /** Body copy, running text */
    val W1 = FontWeight(400)
    /** Medium — list titles, secondary labels */
    val W2 = FontWeight(500)
    /** Semibold — the system's emphasis weight */
    val W3 = FontWeight(600)
    /** Display only — hero headlines and the one big number on a screen */
    val W4 = FontWeight(800)
}

/** Type. Sizes are sp and scale with the user's OS font-size setting; that is
 *  the whole reason these are authored separately from the web clamp(). */
object MLType {
    /** Micro labels, captions, badges, metadata, timestamps — Material labelSmall */
    val Type1 = TextStyle(
        fontFamily = Inter,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = MLWeight.W3,
        letterSpacing = 0.12.sp,
    )
    /** Body copy, default UI text, list item titles — Material bodyLarge */
    val Type2 = TextStyle(
        fontFamily = Inter,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = MLWeight.W1,
        letterSpacing = 0.00.sp,
    )
    /** Section headings, card titles, screen titles — Material titleLarge */
    val Type3 = TextStyle(
        fontFamily = InterTight,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = MLWeight.W3,
        letterSpacing = -0.22.sp,
    )
    /** Display — hero headlines, now-playing track title, the one big number on a screen — Material displaySmall */
    val Type4 = TextStyle(
        fontFamily = InterTight,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        fontWeight = MLWeight.W4,
        letterSpacing = -0.96.sp,
    )
}

/** Motion — springs, not durations. A duration is not translatable to a spring
 *  and pretending otherwise was the web-shaped assumption this replaced. */
object MLMotion {
    /** Press, ripple, toggle, chip select — must feel instant — Spring.DampingRatioNoBouncy / Spring.StiffnessMedium */
    fun <T> spring1() = spring<T>(dampingRatio = 1f, stiffness = 1500f)
    /** Icon change, colour change, hover lift, dismissal — slightly damped, no visible overshoot */
    fun <T> spring2() = spring<T>(dampingRatio = 0.9f, stiffness = 700f)
    /** Sheet reveal, card lift, shared-element / container transform — Spring.StiffnessMediumLow */
    fun <T> spring3() = spring<T>(dampingRatio = 0.85f, stiffness = 400f)
    /** Entrance, modal, splash, expressive moments (the Shazam result reveal) — Spring.DampingRatioLowBouncy — the one step allowed to overshoot */
    fun <T> spring4() = spring<T>(dampingRatio = 0.75f, stiffness = 300f)
}

/** Gesture thresholds. */
object MLGesture {
    /** Distance past which a swipe commits — sheet dismiss, swipe deck, row actions */
    val SwipeThreshold = 80.dp
    /** Movement before a drag is recognised over a tap */
    val DragThreshold = 8.dp
    /** Long-press recognition delay */
    val LongPress = 500L
    /** Minimum pinch-zoom scale — artwork and waveform detail views */
    val PinchMin = 0.5f
    /** Maximum pinch-zoom scale */
    val PinchMax = 3f
}

/** Dimensions fixed by a platform contract rather than chosen by us. */
object MLComponent {
    /** Search app bar — the spec's 56dp variant */
    val AppbarSearch = 56.dp
    /** Center-aligned app bar, generic screens */
    val AppbarCenter = 64.dp
    /** Medium app bar, section headers */
    val AppbarMedium = 112.dp
    /** Large app bar, profile pages and dashboard */
    val AppbarLarge = 152.dp
    /** Modal navigation drawer — mobile hamburger menu */
    val DrawerModal = 360.dp
    /** Persistent drawer — desktop sidebar */
    val DrawerPersistent = 240.dp
    /** Navigation rail, tablet only, 3-7 destinations */
    val NavRail = 80.dp
    /** Material 3 switch track width */
    val SwitchTrack = 52.dp
}

/* Insets are NOT tokens on Android — read them from the framework:
 *   WindowInsets.statusBars      (top)
 *   WindowInsets.navigationBars  (bottom)
 * The px fallbacks in tokens.json exist only for browsers without env().
 *
 * Window size classes: Medium >= 600dp, Expanded >= 840dp.
 * Branch on WindowWidthSizeClass, not on a pixel breakpoint.
 */
