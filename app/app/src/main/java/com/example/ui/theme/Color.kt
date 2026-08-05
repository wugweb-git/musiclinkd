package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// --- MUSICLINKD DESIGN SYSTEM COLOR PRIMITIVES ---

// Neutral Scale
val Neutral0 = Color(0xFFFFFFFF)
val Neutral25 = Color(0xFFFCFCFD)
val Neutral50 = Color(0xFFF8F8FB)
val Neutral100 = Color(0xFFF3F3F6)
val Neutral200 = Color(0xFFECECF1)
val Neutral300 = Color(0xFFDDDEE5)
val Neutral400 = Color(0xFFB8BAC7)
val Neutral500 = Color(0xFF8E91A1)
val Neutral600 = Color(0xFF676B7B)
val Neutral700 = Color(0xFF4A4D5C)
val Neutral800 = Color(0xFF2C2F3A)
val Neutral900 = Color(0xFF181A22)
val Neutral950 = Color(0xFF0D0F14)

// Brand Scale (Pink Accent and Dark Navy)
val Pink500 = Color(0xFFEA4C89) // MusicLinkd Accent Primary
val Pink600 = Color(0xFFD93C77)
val Pink700 = Color(0xFFC72F66)
val NavyDark = Color(0xFF0D0B26) // Primary Dark

// Brand Scale fallback
val Brand50 = Color(0xFFFFF1F6)
val Brand100 = Color(0xFFFFE1ED)
val Brand200 = Color(0xFFFFC2DA)
val Brand300 = Color(0xFFFFA3C7)
val Brand400 = Color(0xFFF472A6)
val Brand500 = Pink500
val Brand600 = Pink600
val Brand700 = Pink700
val Brand800 = Color(0xFFB41E53)
val Brand900 = Color(0xFF8D0F3C)

// Success Scale
val Success50 = Color(0xFFF0FDF4)
val Success100 = Color(0xFFDCFCE7)
val Success500 = Color(0xFF22C55E)
val Success700 = Color(0xFF15803D)

// Warning Scale
val Warning50 = Color(0xFFFFFBEB)
val Warning100 = Color(0xFFFEF3C7)
val Warning500 = Color(0xFFF59E0B)
val Warning700 = Color(0xFFB45309)

// Error Scale
val Error50 = Color(0xFFFEF2F2)
val Error100 = Color(0xFFFEE2E2)
val Error500 = Color(0xFFEF4444)
val Error700 = Color(0xFFB91C1C)

// Info Scale
val Info50 = Color(0xFFEFF6FF)
val Info100 = Color(0xFFDBEAFE)
val Info500 = Color(0xFF3B82F6)
val Info700 = Color(0xFF1D4ED8)

// --- SEMANTIC COLOR TOKENS MAPPED TO THEMES ---
val BrandPrimary = Color(0xFFEA4C89) // #EA4C89 (MusicLinkd Accent)
val BrandAccent = Color(0xFFEA4C89)
val BrandDark = Color(0xFF0D0B26)
val BrandSecondary = Color(0xFFD93C77)
val BrandBackground = Neutral25 // #FCFCFD (Clean primary background-secondary/tertiary)
val BrandCard = Neutral0 // #FFFFFF
val BrandTextPrimary = Color(0xFF0D0B26) // #0D0B26 (Primary Dark / Navy)
val BrandTextSecondary = Color(0xFF7D7D8E) // #7D7D8E (Secondary Text Gray)
val BrandBorder = Color(0xFFECECF1) // #ECECF1 (Light Border)
val BrandSuccess = Success500 // #22C55E

// Legacy tokens for backward compatibility
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = BrandPrimary
val PurpleGrey40 = BrandSecondary
val Pink40 = Color(0xFF7D5260)

