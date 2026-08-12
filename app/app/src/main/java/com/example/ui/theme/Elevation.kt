package com.example.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Gestalt (Pinterest) elevation, extracted verbatim from
 * github.com/pinterest/gestalt (Apache-2.0). Compose's `Modifier.shadow(elevation)`
 * takes a single Dp (simulated ambient+spot light), not CSS's independent
 * offsetX/offsetY/blur/spread — these Dp values are chosen to read the same
 * as Gestalt's blur radius; the original CSS-equivalent spec is documented
 * per constant for anyone building a custom `drawBehind` shadow instead.
 * See frontend/guide/tokens/tokens.json (foundation.gestalt.elevation).
 */
object GestaltElevation {
  /** CSS-equivalent: 0px 0px 8px rgba(0,0,0,0.1) — popover, menu, tooltip. */
  val floating: Dp = 8.dp

  /** CSS-equivalent: 0px 2px 8px rgba(0,0,0,0.12) — card/sheet top edge. */
  val raisedTop: Dp = 6.dp

  /** CSS-equivalent: 0px -2px 8px rgba(0,0,0,0.12) — sticky bottom bar. Compose
   * shadows always draw downward; a bottom-edge shadow needs a rotated/flipped
   * custom drawBehind modifier if the visual direction matters. */
  val raisedBottom: Dp = 6.dp

  /** No shadow — flat/resting surface. */
  val flat: Dp = 0.dp
}
