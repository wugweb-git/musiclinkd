package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// --- MUSICLINKD DESIGN SYSTEM COLOR PRIMITIVES (STABLE V4.0) ---

// Core Colors v4.0
val ColorWhite = Color(0xFFFFFFFF)
val ColorBg = Color(0xFFFCFCFD)
val ColorSurface50 = Color(0xFFF8F8FB)
val ColorSurface100 = Color(0xFFF3F3F6)
val ColorBorder = Color(0xFFECECF1)
val ColorMuted = Color(0xFF8E91A1)
val ColorText2 = Color(0xFF676B7B)
val ColorText = Color(0xFF181A22)
val ColorBlack = Color(0xFF0D0D0D)
val ColorPink = Color(0xFFEA4C89)
val ColorGreen = Color(0xFF16A34A)
val ColorError = Color(0xFFDC2626)

// Iconography Extended State Colors v4.0
val IconActive = ColorPink // #EA4C89
val IconInactive = Color(0xFF7D7D8E)
val IconDisabled = Color(0xFF9CA3AF)

// Neutral Scale
val Neutral0 = ColorWhite
val Neutral25 = ColorBg
val Neutral50 = ColorSurface50
val Neutral100 = ColorSurface100
val Neutral200 = ColorBorder
val Neutral300 = Color(0xFFDDDEE5)
val Neutral400 = Color(0xFFB8BAC7)
val Neutral500 = ColorMuted
val Neutral600 = ColorText2
val Neutral700 = Color(0xFF4A4D5C)
val Neutral800 = Color(0xFF2C2F3A)
val Neutral900 = ColorText
val Neutral950 = ColorBlack

// Brand Scale (Pink Accent and Dark Navy)
val Pink500 = ColorPink // MusicLinkd Accent Primary (#EA4C89)
val Pink600 = Color(0xFFD93C77)
val Pink700 = Color(0xFFC72F66)
val NavyDark = Color(0xFF0D0B26) // Primary Dark

// Success Scale
val Success500 = ColorGreen // #16A34A

// Error Scale
val Error500 = ColorError // #DC2626

// --- SEMANTIC COLOR TOKENS MAPPED TO THEMES ---
val BrandPrimary = ColorPink // #EA4C89
val BrandAccent = ColorPink
val BrandDark = ColorText // #181A22
val BrandSecondary = Pink600
val BrandBackground = ColorBg // #FCFCFD
val BrandCard = ColorWhite // #FFFFFF
val BrandTextPrimary = ColorText // #181A22
val BrandTextSecondary = ColorText2 // #676B7B
val BrandBorder = ColorBorder // #ECECF1
val BrandSuccess = ColorGreen // #16A34A
val BrandMuted = ColorMuted // #8E91A1

// Legacy tokens for backward compatibility
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = BrandPrimary
val PurpleGrey40 = BrandSecondary
val Pink40 = Color(0xFF7D5260)


