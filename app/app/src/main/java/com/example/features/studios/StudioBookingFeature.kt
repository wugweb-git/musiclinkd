package com.example.features.studios

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.onboarding.ui.screens.StudioBookingMainFlowContainer

/**
 * Recording Studio Directory & Hourly Slot Booking Feature Module.
 */
object StudioBookingFeature {

    @Composable
    fun StudioBookingWorkspace(
        onBackToDashboard: () -> Unit = {},
        modifier: Modifier = Modifier
    ) {
        StudioBookingMainFlowContainer(
            onBackToDashboard = onBackToDashboard,
            modifier = modifier
        )
    }
}
