package com.example.onboarding.util

import com.example.R

/**
 * Centralized Application Strings and Constants for maintainability and localization.
 */
object AppStrings {
    const val APP_NAME = "MusicLinkd"

    // User Roles
    const val ROLE_MUSICIAN = "Musician / Singer"
    const val ROLE_PRODUCER = "Music Producer"
    const val ROLE_ENGINEER = "Sound Engineer"
    const val ROLE_BAND = "Band / Group"
    const val ROLE_STUDIO = "Recording Studio"
    const val ROLE_VENUE = "Venue / Event Organizer"

    // Journey Steps
    val MUSICIAN_JOURNEY_STEPS = listOf(
        "Choose Primary Instrument & Vocal Range",
        "Select Genre Specializations (Bollywood, Classical, Pop, Rock)",
        "Upload Audio Demo Clips / Video Showreels",
        "Capture Professional Headshot",
        "Set Performance Session Rates & Availability"
    )

    val PRODUCER_JOURNEY_STEPS = listOf(
        "Select DAW & Software Suite (Logic, Ableton, Pro Tools)",
        "Specify Beatmaking & Production Genres",
        "Upload Track Samples & Published Discography Links",
        "Capture Studio / Professional Headshot",
        "Define Project Rates & Stems Delivery Terms"
    )

    val ENGINEER_JOURNEY_STEPS = listOf(
        "Specify Engineering Specialization (Tracking, Mixing, Mastering)",
        "List Outboard Gear, Plugins & Acoustics Suite",
        "Link Before & After Audio Portfolio Multi-tracks",
        "Capture Professional Studio Headshot",
        "Set Hourly Studio & Mixing Rates"
    )

    // Resource String IDs
    val RES_DISCOVER = R.string.nav_discover
    val RES_MESSAGES = R.string.nav_messages
    val RES_STUDIOS = R.string.nav_studios
    val RES_PORTFOLIO = R.string.nav_portfolio
    val RES_PROFILE = R.string.nav_profile
}
