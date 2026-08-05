package com.example.features.marketplace

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.onboarding.ui.screens.SupportingModule
import com.example.onboarding.ui.screens.SupportingScreensHub

/**
 * Gear & Audio STEM Marketplace Feature Module.
 */
object MarketplaceFeature {

    @Composable
    fun MarketplaceWorkspace(
        onBack: () -> Unit = {},
        modifier: Modifier = Modifier
    ) {
        SupportingScreensHub(
            initialModule = SupportingModule.SAVED_FAVORITES,
            onBack = onBack,
            modifier = modifier
        )
    }
}
