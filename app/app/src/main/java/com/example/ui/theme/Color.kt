package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// --- MUSICLINKD 4-TIER STRICT COLOR SYSTEM ---

// Neutral Scale
val Neutral0 = Color(0xFFFFFFFF)
val Neutral25 = Color(0xFFFCFCFD)
val Neutral100 = Color(0xFFF3F3F6)
val Neutral200 = Color(0xFFECECF1)
val Neutral500 = Color(0xFF8E91A1)
val Neutral950 = Color(0xFF0D0F14)

// Brand Scale
val Pink500 = Color(0xFFEA4C89)
val NavyDark = Color(0xFF0D0B26)

// Opacity Scale (4-Tier)
val Opacity1 = Color(0x0A000000) // op-1 (4%)
val Opacity2 = Color(0x14000000) // op-2 (8%)
val Opacity3 = Color(0x28000000) // op-3 (16%)
val Opacity4 = Color(0x52000000) // op-4 (32%)

// Semantic Tokens
val BrandPrimary = Pink500
val BrandSecondary = Color(0xFFD93C77)
val BrandDark = NavyDark

val BrandBackground = Neutral25
val BrandCard = Neutral0
val BrandTextPrimary = NavyDark
val BrandTextSecondary = Neutral500
val BrandBorder = Neutral200

// Legacy fallbacks (For strict Material3 overrides)
val Purple80 = BrandPrimary
val PurpleGrey80 = BrandSecondary
val Pink80 = Pink500
