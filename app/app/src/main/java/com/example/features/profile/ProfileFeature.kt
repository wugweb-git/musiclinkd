package com.example.features.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.onboarding.models.OnboardingState
import com.example.onboarding.models.PortfolioState
import com.example.onboarding.ui.screens.PortfolioMainFlowContainer

/**
 * Artist Portfolio & Profile Management Feature Module.
 */
object ProfileFeature {

    @Composable
    fun ArtistPortfolioScreen(
        portfolioState: PortfolioState = PortfolioState(),
        onBackClick: () -> Unit = {},
        modifier: Modifier = Modifier
    ) {
        PortfolioMainFlowContainer(
            portfolioState = portfolioState,
            onBackClick = onBackClick,
            onNavigatetoScreen = {},
            onNavigateBackInFlow = { true },
            onUpdateProjectType = {},
            onUpdateProjectDetails = { _, _, _, _ -> },
            onUpdateStreamingUrls = { _, _, _, _, _, _ -> },
            onToggleRole = {},
            onUpdateSearchQuery = {},
            onTagCollaborator = {},
            onUntagCollaborator = {},
            onUpdateCreditRole = { _, _ -> },
            onUpdateVisibility = {},
            onPublish = {},
            onPreloadQuickFill = {},
            onAddIncStat = { _, _ -> },
            onPreviewPublish = {}
        )
    }
}
