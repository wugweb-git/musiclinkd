package com.example.features.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * GSAP-inspired fluid animation modifiers and spring physics specs for Jetpack Compose.
 */
object GsapComposeAnimations {

    // Centralized AnimationSpecs
    object AnimationSpecs {
        val SpringPower3 = spring<Float>(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        )

        val GentleEase = spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        )

        val ElasticBounce = spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        )

        val FastFade = tween<Float>(durationMillis = 200, easing = FastOutSlowInEasing)
    }

    // Centralized Transition Wrappers
    object Transition {
        val SlideEnter = androidx.compose.animation.fadeIn(AnimationSpecs.FastFade) +
            androidx.compose.animation.slideInHorizontally(initialOffsetX = { 40 })

        val SlideExit = androidx.compose.animation.fadeOut(AnimationSpecs.FastFade) +
            androidx.compose.animation.slideOutHorizontally(targetOffsetX = { -40 })
    }

    // Legacy aliases
    val GsapSpringSpec = AnimationSpecs.SpringPower3
    val GsapGentleSpring = AnimationSpecs.GentleEase
    val GsapFastBounce = AnimationSpecs.ElasticBounce
}

/**
 * Custom AnimatedTransition wrapper component applying GSAP-like fluid entrance/exit transitions.
 */
@Composable
fun AnimatedTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = GsapComposeAnimations.Transition.SlideEnter,
        exit = GsapComposeAnimations.Transition.SlideExit,
        modifier = modifier
    ) {
        content()
    }
}

/**
 * GSAP-like press scale feedback modifier down to target scale on press with spring release.
 */
fun Modifier.gsapPressScale(
    targetScaleOnPress: Float = 0.95f,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) targetScaleOnPress else 1.0f,
        animationSpec = GsapComposeAnimations.GsapFastBounce,
        label = "gsapPressScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = androidx.compose.foundation.LocalIndication.current,
                    onClick = onClick
                )
            } else Modifier
        )
}

/**
 * GSAP-like entrance fade and rise animation modifier.
 */
fun Modifier.gsapEntranceRise(
    visible: Boolean = true,
    initialOffsetY: Float = 30f
): Modifier = composed {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "gsapAlpha"
    )

    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else initialOffsetY,
        animationSpec = GsapComposeAnimations.GsapGentleSpring,
        label = "gsapOffsetY"
    )

    this.graphicsLayer {
        this.alpha = alpha
        this.translationY = offsetY
    }
}
