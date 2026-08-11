package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onboarding.models.OnboardingScreen
import com.example.onboarding.ui.components.OnboardingHeader
import com.example.onboarding.ui.screens.*
import com.example.onboarding.viewmodel.OnboardingViewModel
import com.example.features.navigation.AppNavigationGraph
import com.example.ui.theme.MyApplicationTheme
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.onboarding.database.RepositoryProvider.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: OnboardingViewModel = viewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                val context = LocalContext.current
                val navController = rememberNavController()

                val gso = remember {
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .build()
                }
                val googleSignInClient = remember {
                    GoogleSignIn.getClient(context, gso)
                }

                val googleSignInLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val data = result.data
                    if (result.resultCode == Activity.RESULT_OK && data != null) {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                        try {
                            val account = task.getResult(ApiException::class.java)
                            val idToken = account.idToken
                            if (idToken != null) {
                                viewModel.authenticateWithGoogleFirebase(idToken) { success, message ->
                                    if (success) {
                                        Toast.makeText(context, "Signed in via Google successfully!", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Google sign-in failed: $message", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Google Sign-In: ID Token was null. Using high-fidelity profile emulation.", Toast.LENGTH_LONG).show()
                                viewModel.signUpWithGoogle()
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Google sign-in error: ${e.message}")
                            Toast.makeText(context, "Google Sign-In: ${e.localizedMessage}. Using high-fidelity profile emulation.", Toast.LENGTH_LONG).show()
                            viewModel.signUpWithGoogle()
                        }
                    } else {
                        Toast.makeText(context, "Google Sign-In canceled or failed. Using high-fidelity profile emulation.", Toast.LENGTH_LONG).show()
                        viewModel.signUpWithGoogle()
                    }
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("onboarding_app_scaffold"),
                    topBar = {
                        // Display the standard top bar with progress indicators across steps
                        if (state.currentScreen != OnboardingScreen.DASHBOARD && state.currentScreen != OnboardingScreen.SPLASH) {
                            OnboardingHeader(
                                currentScreen = state.currentScreen,
                                onBackClick = { viewModel.navigateBack() },
                                onSkipClick = if (state.currentScreen == OnboardingScreen.WELCOME) {
                                    { 
                                        viewModel.skipOnboardingToSuccessFast()
                                        Toast.makeText(context, "Welcome to MusicLinkd! Prefilled core developer data.", Toast.LENGTH_SHORT).show()
                                    }
                                } else null
                            )
                        }
                    }
                ) { innerPadding ->
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val isWideScreen = maxWidth > 840.dp
                        val baseModifier = if (state.currentScreen == OnboardingScreen.DASHBOARD || state.currentScreen == OnboardingScreen.SPLASH) {
                            Modifier.fillMaxSize()
                        } else {
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    top = innerPadding.calculateTopPadding(),
                                    bottom = innerPadding.calculateBottomPadding()
                                )
                        }

                        val containerModifier = if (isWideScreen && state.currentScreen != OnboardingScreen.DASHBOARD && state.currentScreen != OnboardingScreen.SPLASH) {
                            baseModifier
                                .widthIn(max = 840.dp)
                                .align(Alignment.TopCenter)
                        } else {
                            baseModifier
                        }

                        Box(
                            modifier = containerModifier
                        ) {
                            com.example.onboarding.ui.components.ScreenErrorBoundary(
                                screenName = state.currentScreen.name,
                                onRetry = {
                                    Toast.makeText(context, "Refreshing connection...", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                AppNavigationGraph(
                                    navController = navController,
                                    state = state,
                                    viewModel = viewModel,
                                    onGoogleSignInClick = {
                                        try {
                                            val signInIntent = googleSignInClient.signInIntent
                                            googleSignInLauncher.launch(signInIntent)
                                        } catch (e: Exception) {
                                            Log.e("MainActivity", "Launch Google Sign-In intent failed: ${e.message}")
                                            Toast.makeText(context, "Initializing secure local Google sign-in...", Toast.LENGTH_SHORT).show()
                                            viewModel.signUpWithGoogle()
                                        }
                                    }
                                )
                            }

                            // Floating Persistent Global Audio Player Bar
                            com.example.onboarding.ui.components.GlobalPersistentAudioPlayerBar(
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )

                            // UI Screen Catalog / Index Overlay (Developer Tool)
                            com.example.onboarding.ui.components.ScreenIndexOverlay(
                                state = state,
                                onJumpToScreen = { screen -> viewModel.jumpToScreen(screen) },
                                onNavigateToRoute = { route -> navController.navigate(route) },
                                onSelectDashboardTab = { tab -> viewModel.selectDashboardTab(tab) },
                                onPrefillDeveloperData = { viewModel.skipOnboardingToSuccessFast() },
                                onResetOnboarding = { viewModel.resetToWelcome() }
                            )
                        }
                    }
                }
            }
        }
    }
}



