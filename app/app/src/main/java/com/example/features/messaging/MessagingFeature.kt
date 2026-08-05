package com.example.features.messaging

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.onboarding.ui.screens.Flow5MessagingWorkspace

/**
 * Direct Messaging & Collaboration Threads Feature Module.
 */
object MessagingFeature {

    @Composable
    fun MessagingWorkspace(
        onBack: () -> Unit = {},
        modifier: Modifier = Modifier
    ) {
        Flow5MessagingWorkspace(
            onBack = onBack,
            modifier = modifier
        )
    }
}
