package com.example.features.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.onboarding.models.OnboardingScreen
import com.example.onboarding.models.OnboardingState
import com.example.onboarding.models.PortfolioState
import com.example.onboarding.ui.screens.*
import com.example.onboarding.util.AnalyticsHelper
import com.example.onboarding.viewmodel.OnboardingViewModel

object MusicLinkdRoutes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val EMAIL_AUTH = "email_auth"
    const val MOBILE_AUTH = "mobile_auth"
    const val OTP_VERIFY = "otp_verify"
    const val ROLE_PRIMARY = "role_primary"
    const val ROLE_SECONDARY = "role_secondary"
    const val LANGUAGES = "languages"
    const val GENRES = "genres"
    const val INSTRUMENTS = "instruments"
    const val LOCATION = "location"
    const val PROFILE_CREATION = "profile_creation"
    const val SUCCESS = "success"
    const val DASHBOARD = "dashboard"
    const val METADATA_SYNC = "metadata_sync"
    const val ARTIST_DETAIL = "artist/{artistId}"
    const val STUDIO_DETAIL = "studio/{studioId}"
    const val CHAT_THREAD = "messages/{threadId}"

    fun buildArtistRoute(artistId: String) = "artist/$artistId"
    fun buildStudioRoute(studioId: String) = "studio/$studioId"
    fun buildChatRoute(threadId: String) = "messages/$threadId"
}

@Composable
fun AppNavigationGraph(
    navController: NavHostController = rememberNavController(),
    state: OnboardingState,
    viewModel: OnboardingViewModel,
    onGoogleSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val startDest = when (state.currentScreen) {
        OnboardingScreen.SPLASH -> MusicLinkdRoutes.SPLASH
        OnboardingScreen.WELCOME -> MusicLinkdRoutes.WELCOME
        OnboardingScreen.EMAIL_AUTH -> MusicLinkdRoutes.EMAIL_AUTH
        OnboardingScreen.MOBILE_AUTH -> MusicLinkdRoutes.MOBILE_AUTH
        OnboardingScreen.OTP_VERIFY -> MusicLinkdRoutes.OTP_VERIFY
        OnboardingScreen.ROLE_PRIMARY -> MusicLinkdRoutes.ROLE_PRIMARY
        OnboardingScreen.ROLE_SECONDARY -> MusicLinkdRoutes.ROLE_SECONDARY
        OnboardingScreen.LANGUAGES -> MusicLinkdRoutes.LANGUAGES
        OnboardingScreen.GENRES -> MusicLinkdRoutes.GENRES
        OnboardingScreen.INSTRUMENTS -> MusicLinkdRoutes.INSTRUMENTS
        OnboardingScreen.LOCATION -> MusicLinkdRoutes.LOCATION
        OnboardingScreen.PROFILE_CREATION -> MusicLinkdRoutes.PROFILE_CREATION
        OnboardingScreen.SUCCESS -> MusicLinkdRoutes.SUCCESS
        OnboardingScreen.DASHBOARD -> MusicLinkdRoutes.DASHBOARD
        OnboardingScreen.METADATA_SYNC -> MusicLinkdRoutes.METADATA_SYNC
    }

    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentNavBackStackEntry) {
        currentNavBackStackEntry?.destination?.route?.let { route ->
            AnalyticsHelper.trackScreenView(screenName = route, screenClass = "MusicLinkdScreen_$route")
        }
    }

    LaunchedEffect(state.currentScreen) {
        AnalyticsHelper.trackUserInteraction(
            eventName = "onboarding_screen_changed",
            params = mapOf(
                "screen" to state.currentScreen.name,
                "project_id" to AnalyticsHelper.PRIMARY_PROJECT_ID
            )
        )
    }

    NavHost(
        navController = navController,
        startDestination = startDest,
        modifier = modifier,
        enterTransition = { fadeIn(tween(250)) + slideInHorizontally(initialOffsetX = { 40 }) },
        exitTransition = { fadeOut(tween(200)) + slideOutHorizontally(targetOffsetX = { -40 }) },
        popEnterTransition = { fadeIn(tween(250)) + slideInHorizontally(initialOffsetX = { -40 }) },
        popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(targetOffsetX = { 40 }) }
    ) {
        composable(MusicLinkdRoutes.SPLASH) {
            SplashScreen(onDismiss = { viewModel.dismissSplash() })
        }

        composable(MusicLinkdRoutes.WELCOME) {
            WelcomeScreen(
                state = state,
                onGoogleClick = onGoogleSignInClick,
                onMobileClick = { viewModel.startMobileAuth() },
                onEmailClick = { viewModel.startEmailAuth() }
            )
        }

        composable(MusicLinkdRoutes.EMAIL_AUTH) {
            EmailAuthScreen(
                state = state,
                emailValue = state.firebaseEmailInput,
                onEmailChange = { viewModel.updateFirebaseEmail(it) },
                passwordValue = state.firebasePasswordInput,
                onPasswordChange = { viewModel.updateFirebasePassword(it) },
                isLoginMode = state.isFirebaseAuthLoginMode,
                onToggleMode = { viewModel.toggleFirebaseAuthMode() },
                onSubmit = { viewModel.performFirebaseAuth {} },
                isLoading = state.isFirebaseAuthLoading,
                error = state.firebaseAuthError
            )
        }

        composable(MusicLinkdRoutes.MOBILE_AUTH) {
            MobileAuthScreen(
                mobileNumber = state.mobileNumber,
                onNumberChange = { viewModel.updateMobileNumber(it) },
                onSubmit = { viewModel.sendOtp() },
                isLoading = state.isOtpSending,
                error = state.mobileError
            )
        }

        composable(MusicLinkdRoutes.OTP_VERIFY) {
            OtpVerificationScreen(
                mobileNumber = state.mobileNumber,
                otpValue = state.otp,
                onOtpChange = { viewModel.updateOtp(it) },
                timerCount = 30,
                onResendClick = { viewModel.resendOtp() },
                onSubmit = { viewModel.verifyOtp() },
                isLoading = state.isOtpVerifying,
                error = state.otpError
            )
        }

        composable(MusicLinkdRoutes.ROLE_PRIMARY) {
            PrimaryRoleScreen(
                selectedRole = state.primaryRole,
                onRoleSelected = { viewModel.selectPrimaryRole(it) },
                onContinue = { viewModel.confirmPrimaryRole() }
            )
        }

        composable(MusicLinkdRoutes.ROLE_SECONDARY) {
            SecondaryRoleScreen(
                primaryRole = state.primaryRole,
                selectedRole = state.secondaryRole,
                onRoleSelected = { viewModel.selectSecondaryRole(it) },
                onContinue = { viewModel.confirmSecondaryRole() },
                onSkip = { viewModel.skipSecondaryRole() }
            )
        }

        composable(MusicLinkdRoutes.LANGUAGES) {
            LanguagesScreen(
                selectedLanguages = state.selectedLanguages,
                onLangToggled = { viewModel.toggleLanguage(it) },
                onContinue = { viewModel.confirmLanguages() },
                error = state.languagesError
            )
        }

        composable(MusicLinkdRoutes.GENRES) {
            GenresScreen(
                selectedGenres = state.selectedGenres,
                onGenreToggled = { viewModel.toggleGenre(it) },
                onContinue = { viewModel.confirmGenres() },
                error = state.genresError
            )
        }

        composable(MusicLinkdRoutes.INSTRUMENTS) {
            InstrumentsScreen(
                selectedInstruments = state.selectedInstruments,
                instrumentProficiency = state.instrumentProficiency,
                onInstrumentToggled = { viewModel.toggleInstrument(it) },
                onUpdateProficiency = { name, exp, lvl, url -> viewModel.updateInstrumentProficiency(name, exp, lvl, url) },
                onContinue = { viewModel.confirmInstruments() }
            )
        }

        composable(MusicLinkdRoutes.LOCATION) {
            LocationScreen(
                country = state.country,
                state = state.state,
                city = state.city,
                area = state.area,
                onLocationChange = { st, ct, ar -> viewModel.updateLocation(st, ct, ar) },
                onContinue = { viewModel.confirmLocation() },
                stateError = state.stateError,
                cityError = state.cityError
            )
        }

        composable(MusicLinkdRoutes.PROFILE_CREATION) {
            ProfileCreationScreen(
                fullName = state.fullName,
                displayName = state.displayName,
                bio = state.bio,
                selectedAvatarIndex = state.selectedAvatarIndex,
                roleExperience = state.roleExperience,
                networkingLinks = state.networkingLinks,
                onProfileChange = { fn, dn, bi, av, pf, cv -> viewModel.updateProfile(fn, dn, bi, av, pf, cv) },
                onUpdateRoleExperience = { exp, lvl, proof -> viewModel.updateRoleExperience(exp, lvl, proof) },
                onUpdateNetworkingLink = { platform, url -> viewModel.updateNetworkingLink(platform, url) },
                onContinue = { viewModel.confirmProfile() },
                nameError = state.nameError,
                usernameError = state.usernameError,
                bioError = state.bioError,
                proofError = state.proofError,
                linksErrors = state.linksErrors,
                profilePicturePath = state.profilePicturePath,
                coverPicturePath = state.coverPicturePath,
                isSaving = state.isProfileSaving
            )
        }

        composable(MusicLinkdRoutes.SUCCESS) {
            SuccessOnboardingScreen(
                state = state,
                onNavigateDashboard = { viewModel.finishOnboarding() },
                onShareProfile = {}
            )
        }

        composable(MusicLinkdRoutes.DASHBOARD) {
            DashboardPreviewScreen(
                state = state,
                onResetClick = { viewModel.resetToWelcome() },
                onSavePictures = { profilePath, coverPath ->
                    viewModel.updateProfile(
                        fullName = state.fullName,
                        displayName = state.displayName,
                        bioText = state.bio,
                        avatarIndex = state.selectedAvatarIndex,
                        profilePath = profilePath,
                        coverPath = coverPath
                    )
                }
            )
        }

        composable(MusicLinkdRoutes.ARTIST_DETAIL) {
            PortfolioMainFlowContainer(
                portfolioState = PortfolioState(),
                onBackClick = { navController.popBackStack() },
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

        composable(MusicLinkdRoutes.STUDIO_DETAIL) {
            StudioBookingMainFlowContainer(
                onBackToDashboard = { navController.popBackStack() }
            )
        }

        composable(MusicLinkdRoutes.CHAT_THREAD) {
            Flow5MessagingWorkspace(
                onBack = { navController.popBackStack() }
            )
        }

        composable(MusicLinkdRoutes.METADATA_SYNC) {
            MetadataSyncManagerScreen(
                onBack = { viewModel.jumpToScreen(OnboardingScreen.DASHBOARD) }
            )
        }
    }
}
