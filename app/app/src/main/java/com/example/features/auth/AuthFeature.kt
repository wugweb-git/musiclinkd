package com.example.features.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.onboarding.models.OnboardingState
import com.example.onboarding.ui.screens.*

/**
 * Authentication & Onboarding Flow Feature Module.
 */
object AuthFeature {

    @Composable
    fun WelcomeScreen(
        state: OnboardingState,
        onGoogleClick: () -> Unit,
        onMobileClick: () -> Unit,
        onEmailClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        com.example.onboarding.ui.screens.WelcomeScreen(
            state = state,
            onGoogleClick = onGoogleClick,
            onMobileClick = onMobileClick,
            onEmailClick = onEmailClick
        )
    }

    @Composable
    fun SplashScreen(
        onDismiss: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        com.example.onboarding.ui.screens.SplashScreen(
            onDismiss = onDismiss
        )
    }
}
