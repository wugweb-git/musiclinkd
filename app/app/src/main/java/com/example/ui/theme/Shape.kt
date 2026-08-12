package com.example.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Full-fidelity Gestalt (Pinterest) rounding scale, extracted verbatim from
 * github.com/pinterest/gestalt (Apache-2.0). Additive alongside the site's
 * own 4-token core (8/16/32dp + pill) — confirmed to already land exactly
 * on rounding200/400/800/pill below, so the 4-token core is a validated
 * subsample, not a divergence. See frontend/guide/README.md "Gestalt lineage".
 */
object GestaltRounding {
  val rounding0: Dp = 0.dp
  val rounding100: Dp = 4.dp
  val rounding200: Dp = 8.dp
  val rounding300: Dp = 12.dp
  val rounding400: Dp = 16.dp
  val rounding500: Dp = 20.dp
  val rounding600: Dp = 24.dp
  val rounding700: Dp = 28.dp
  val rounding800: Dp = 32.dp
  val pill: Dp = 999.dp
}

/** MusicLinkd's own 4-token radius core — unchanged, still the default for existing work. */
object Radius {
  val sm: Dp = GestaltRounding.rounding200 // 8dp — chips, small controls
  val md: Dp = GestaltRounding.rounding400 // 16dp — media, toasts, inputs
  val lg: Dp = GestaltRounding.rounding800 // 32dp — cards, containers
  val pill: Dp = GestaltRounding.pill // full round — buttons, chips, avatars
  val circle: Shape = CircleShape
}

/** MaterialTheme(shapes = MusicLinkdShapes) — maps the radius core onto M3's 5-step Shapes contract. */
val MusicLinkdShapes = Shapes(
  extraSmall = RoundedCornerShape(GestaltRounding.rounding100),
  small = RoundedCornerShape(Radius.sm),
  medium = RoundedCornerShape(Radius.md),
  large = RoundedCornerShape(Radius.lg),
  extraLarge = RoundedCornerShape(Radius.pill),
)
