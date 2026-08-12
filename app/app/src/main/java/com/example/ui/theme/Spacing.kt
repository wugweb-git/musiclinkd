package com.example.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Full-fidelity Gestalt (Pinterest) spacing scale, extracted verbatim from
 * github.com/pinterest/gestalt (Apache-2.0). Additive alongside the site's
 * own 4-token core (frontend/guide/tokens/tokens.json foundation.spacing:
 * 4/8/16/32dp) — this is the finer-grained scale for work that needs more
 * steps than that 4-token core provides. See frontend/guide/README.md
 * "Gestalt lineage" for the full rationale.
 */
object GestaltSpace {
  val space0: Dp = 0.dp
  val space100: Dp = 4.dp
  val space200: Dp = 8.dp
  val space300: Dp = 12.dp
  val space400: Dp = 16.dp
  val space500: Dp = 20.dp
  val space600: Dp = 24.dp
  val space700: Dp = 28.dp
  val space800: Dp = 32.dp
  val space900: Dp = 36.dp
  val space1000: Dp = 40.dp
  val space1100: Dp = 44.dp
  val space1200: Dp = 48.dp
  val space1300: Dp = 52.dp
  val space1400: Dp = 56.dp
  val space1500: Dp = 60.dp
  val space1600: Dp = 64.dp
}

/** MusicLinkd's own 4-token spacing core — unchanged, still the default for existing work. */
object Space {
  val xs: Dp = GestaltSpace.space100 // 4dp — badge internal, icon gaps
  val sm: Dp = GestaltSpace.space200 // 8dp — compact gaps, chip padding
  val md: Dp = GestaltSpace.space400 // 16dp — card padding, default gaps
  val lg: Dp = GestaltSpace.space800 // 32dp — section spacing, page margins, hero padding
}
