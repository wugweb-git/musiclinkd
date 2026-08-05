package com.example.features.discover

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.onboarding.models.OnboardingState
import com.example.onboarding.ui.screens.DiscoverMainFlowContainer

/**
 * Discover & Collaboration Feed Feature Module.
 */
object DiscoverFeature {

    @Composable
    fun DiscoverFeedScreen(
        onBackClick: () -> Unit = {},
        modifier: Modifier = Modifier
    ) {
        DiscoverMainFlowContainer(
            onBackClick = onBackClick
        )
    }
}
