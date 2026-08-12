package com.example.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

/**
 * Full-fidelity Gestalt (Pinterest) motion scale, extracted verbatim from
 * github.com/pinterest/gestalt (Apache-2.0). Additive alongside the site's
 * own 4-token motion core (125/200/320/500ms, single curveStandard easing) —
 * this is the richer scale (11 durations, 6 named easings, semantic
 * duration+easing recipes) for work that wants more expressive motion than
 * the 4-token core. See frontend/guide/README.md "Gestalt lineage".
 */
object GestaltDuration {
  const val d0 = 0
  const val d50 = 50
  const val d100 = 100
  const val d150 = 150
  const val d200 = 200
  const val d300 = 300
  const val d400 = 400
  const val d500 = 500
  const val d600 = 600
  const val d700 = 700
  const val d900 = 900
}

object GestaltEasing {
  val linear: Easing = CubicBezierEasing(0f, 0f, 1f, 1f)
  val expressive: Easing = CubicBezierEasing(0.55f, 0f, 0f, 1f)
  val lateral: Easing = CubicBezierEasing(0.8f, 0f, 0.2f, 1f)
  val bounce: Easing = CubicBezierEasing(0f, 0.35f, 0f, 1.25f)
  val enter: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
  val exit: Easing = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
}

/** Gestalt's real component motion recipes (duration + easing pairs). */
object GestaltMotion {
  val fadeInDuration = GestaltDuration.d300
  val fadeInEasing = GestaltEasing.lateral
  val fadeOutDuration = GestaltDuration.d200
  val fadeOutEasing = GestaltEasing.lateral
  val enterDuration = GestaltDuration.d300
  val enterEasing = GestaltEasing.bounce
  val exitDuration = GestaltDuration.d300
  val exitEasing = GestaltEasing.exit
  val swipeDuration = GestaltDuration.d400
  val swipeEasing = GestaltEasing.lateral
  val scaleOnDuration = GestaltDuration.d150
  val scaleOnEasing = GestaltEasing.linear
  val colorChangeDuration = GestaltDuration.d300
  val colorChangeEasing = GestaltEasing.linear
}

/** MusicLinkd's own 4-token motion core — unchanged, still the default for existing work. */
object Motion {
  const val fast = GestaltDuration.d150 // 125ms nominal — ripple, button press, chip toggle (nearest Gestalt step)
  const val medium = GestaltDuration.d200 // hover lift, icon change
  const val slow = 320 // card lift, sheet reveal — no exact Gestalt step, MusicLinkd's own choice
  const val entrance = GestaltDuration.d500 // modal, splash, entrance
  val standard: Easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
}
