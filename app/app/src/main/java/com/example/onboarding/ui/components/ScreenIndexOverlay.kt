package com.example.onboarding.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.onboarding.models.OnboardingScreen
import com.example.onboarding.models.OnboardingState
import com.example.ui.theme.*

@Composable
fun ScreenIndexOverlay(
    state: OnboardingState,
    onJumpToScreen: (OnboardingScreen) -> Unit,
    onNavigateToRoute: (String) -> Unit,
    onSelectDashboardTab: (String) -> Unit,
    onPrefillDeveloperData: () -> Unit,
    onResetOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_index_overlay_container"),
        contentAlignment = Alignment.BottomEnd
    ) {
        // Floating action badge/button to trigger UI Index
        Surface(
            modifier = Modifier
                .padding(bottom = 80.dp, end = 16.dp) // Offset slightly from the bottom persistent bar
                .testTag("floating_ui_index_trigger"),
            shape = RoundedCornerShape(999.dp),
            color = BrandPrimary,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .clickable { isExpanded = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "UI Index Icon",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "UI INDEX",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
        }

        if (isExpanded) {
            Dialog(
                onDismissRequest = { isExpanded = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable { isExpanded = false },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .fillMaxHeight(0.85f)
                            .clickable(enabled = false) {} // prevent closing on inner card clicks
                            .testTag("ui_index_dialog_card"),
                        shape = RoundedCornerShape(24.dp), // Base Container Radius: 24.dp
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFCFD)), // Soft Off-White Background
                        border = BorderStroke(1.dp, BrandBorder), // 1.dp border stroke
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                        ) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "📱 MusicLinkd UI Catalog",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandTextPrimary
                                    )
                                    Text(
                                        text = "Direct emulator jump to any screen state or deep route",
                                        fontSize = 12.sp,
                                        color = BrandTextSecondary
                                    )
                                }
                                IconButton(
                                    onClick = { isExpanded = false },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(BrandBorder.copy(alpha = 0.4f), RoundedCornerShape(999.dp))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = BrandTextPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Main Actions Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BrandBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "⚡ DEVELOPER QUICK ACTIONS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandPrimary,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Start
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                onPrefillDeveloperData()
                                                isExpanded = false
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                                .testTag("prefill_dev_action"),
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                            shape = RoundedCornerShape(999.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.OfflineBolt,
                                                contentDescription = "Prefill",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Skip & Prefill Data", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                onResetOnboarding()
                                                isExpanded = false
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                                .testTag("reset_dev_action"),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandTextPrimary),
                                            border = BorderStroke(1.dp, BrandBorder),
                                            shape = RoundedCornerShape(999.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.RestartAlt,
                                                contentDescription = "Reset",
                                                tint = BrandTextPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Reset State", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            HorizontalDivider(color = BrandBorder)

                            Spacer(modifier = Modifier.height(12.dp))

                            // List of categories and screens
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Section: Onboarding screens
                                item {
                                    SectionHeader(title = "ONBOARDING USER FLOWS")
                                }

                                val onboardingScreens = listOf(
                                    CatalogScreenItem(OnboardingScreen.SPLASH, "Splash & Launch Screen", "The dynamic entering brand introduction view.", Icons.Default.Star),
                                    CatalogScreenItem(OnboardingScreen.WELCOME, "Welcome / Registration", "The provider signup entry card with emulated flows.", Icons.Default.Login),
                                    CatalogScreenItem(OnboardingScreen.EMAIL_AUTH, "Firebase Email Auth", "The custom login and registration credential view.", Icons.Default.Email),
                                    CatalogScreenItem(OnboardingScreen.MOBILE_AUTH, "SMS Mobile Auth", "The high-fidelity Indian phone gateway emulation screen.", Icons.Default.PhoneIphone),
                                    CatalogScreenItem(OnboardingScreen.OTP_VERIFY, "OTP SMS verification", "Verification of credentials with active countdown timer.", Icons.Default.LockClock),
                                    CatalogScreenItem(OnboardingScreen.ROLE_PRIMARY, "Primary Role Picker", "Selection of core specialty (Vocalist, Producer, etc.).", Icons.Default.MusicNote),
                                    CatalogScreenItem(OnboardingScreen.ROLE_SECONDARY, "Secondary Role Picker", "Secondary interest, with support for skip actions.", Icons.Default.Album),
                                    CatalogScreenItem(OnboardingScreen.LANGUAGES, "Language Selection", "Check-grid to specify musical languages used.", Icons.Default.Language),
                                    CatalogScreenItem(OnboardingScreen.GENRES, "Genre / Sonic styles", "Selection of up to 3 sound designs or acoustic genres.", Icons.Default.Tune),
                                    CatalogScreenItem(OnboardingScreen.INSTRUMENTS, "Instruments Proficiency", "Specific mastery and references for instruments.", Icons.Default.Piano),
                                    CatalogScreenItem(OnboardingScreen.LOCATION, "Location & Area Selector", "Localized city and sub-area selection screen.", Icons.Default.LocationOn),
                                    CatalogScreenItem(OnboardingScreen.PROFILE_CREATION, "Complete Profile Details", "Display names, custom visual avatars and bio updates.", Icons.Default.Face),
                                    CatalogScreenItem(OnboardingScreen.SUCCESS, "Completion / Success Screen", "Dynamic animation rendering the success layout.", Icons.Default.Celebration),
                                    CatalogScreenItem(OnboardingScreen.DASHBOARD, "Core Application Hub", "Main landing panel displaying explore, feeds, and tabs.", Icons.Default.Dashboard),
                                    CatalogScreenItem(OnboardingScreen.METADATA_SYNC, "Metadata Master & Sync", "CRUD and Firestore synchronization for masters & user mappings.", Icons.Default.Sync)
                                )

                                items(onboardingScreens) { screenItem ->
                                    CatalogScreenCard(
                                        item = screenItem,
                                        isActive = state.currentScreen == screenItem.screen,
                                        onSelect = {
                                            onJumpToScreen(screenItem.screen)
                                            isExpanded = false
                                        }
                                    )
                                }

                                // Section: Core sub-modules
                                item {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    SectionHeader(title = "CORE DEEP FEATURES (LIVE NAV ROUTES)")
                                }

                                val routeScreens = listOf(
                                    CatalogRouteItem("artist/current_user", "Artist Profile / Portfolio", "Deep view of high-fidelity public musician showcase.", Icons.Default.ContactPage),
                                    CatalogRouteItem("studio/studio_1", "Studio Booking Details", "Atmos recording studio booking panel & scheduler.", Icons.Default.GraphicEq),
                                    CatalogRouteItem("messages/thread_1", "Messaging Workspace", "Live audio player attachments and workspace threads.", Icons.Default.Forum)
                                )

                                items(routeScreens) { routeItem ->
                                    CatalogRouteCard(
                                        item = routeItem,
                                        onSelect = {
                                            onNavigateToRoute(routeItem.route)
                                            isExpanded = false
                                        }
                                    )
                                }

                                // Section: Dashboard tab modules
                                item {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    SectionHeader(title = "DASHBOARD TAB MODULES")
                                }

                                val dashboardTabs = listOf(
                                    CatalogDashboardTabItem("Home", "Home Feed & Newsletters", "View community posts, active newsletters, and professional highlights.", Icons.Default.Home),
                                    CatalogDashboardTabItem("Discover", "Discover Directory", "Explore playback singers, music producers, lyricists, and engineers.", Icons.Default.Search),
                                    CatalogDashboardTabItem("Marketplace", "Gigs & Opportunities", "Access live audio roles, contract openings, and audition lists.", Icons.Default.Work),
                                    CatalogDashboardTabItem("StudioSpaces", "Studio Spaces Registry", "Find premium recording facilities, mix rooms, and acoustic booths.", Icons.Default.Hearing),
                                    CatalogDashboardTabItem("Connections", "Network Connections", "Manage sent and received mutual artist link requests.", Icons.Default.People),
                                    CatalogDashboardTabItem("Notifications", "System Activity Alerts", "Track collaboration requests, booking approvals, and review updates.", Icons.Default.NotificationsActive),
                                    CatalogDashboardTabItem("QRShare", "Personal Identity QR", "On-device profile display with native QR generation.", Icons.Default.QrCode2),
                                    CatalogDashboardTabItem("Reputation", "Reputation & Reviews", "Verify credentials, showcase ratings, and client reviews.", Icons.Default.Star),
                                    CatalogDashboardTabItem("Settings", "Account Preferences", "Configure system options, notifications, and profile controls.", Icons.Default.Settings),
                                    CatalogDashboardTabItem("Help", "Concierge Help Center", "Contact support and browse curated developer guides.", Icons.Default.HelpOutline)
                                )

                                items(dashboardTabs) { tabItem ->
                                    CatalogDashboardTabCard(
                                        item = tabItem,
                                        isActive = state.currentScreen == OnboardingScreen.DASHBOARD && state.activeDashboardTab == tabItem.tabName,
                                        onSelect = {
                                            onSelectDashboardTab(tabItem.tabName)
                                            isExpanded = false
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = BrandBorder)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Footer details
                            Text(
                                text = "Current State: Screen = ${state.currentScreen.name} | Verified build by AI Coding Agent",
                                fontSize = 10.sp,
                                color = BrandTextSecondary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = BrandTextSecondary,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun CatalogScreenCard(
    item: CatalogScreenItem,
    isActive: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("catalog_screen_card_${item.screen.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) BrandPrimary.copy(alpha = 0.05f) else Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isActive) BrandPrimary.copy(alpha = 0.3f) else BrandBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isActive) BrandPrimary.copy(alpha = 0.12f) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = if (isActive) BrandPrimary else BrandTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandTextPrimary
                    )
                    if (isActive) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = BrandPrimary,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.bindWithPadding(), vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = item.desc,
                    fontSize = 11.sp,
                    color = BrandTextSecondary,
                    lineHeight = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = BrandTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun CatalogRouteCard(
    item: CatalogRouteItem,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("catalog_route_card_${item.route.replace("/", "_")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BrandBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = BrandTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandTextPrimary
                )
                Text(
                    text = "${item.desc} (Route: ${item.route})",
                    fontSize = 11.sp,
                    color = BrandTextSecondary,
                    lineHeight = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Default.Launch,
                contentDescription = "Launch deep route",
                tint = BrandPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Inline helper to prevent code complexity
private fun Int.bindWithPadding(): androidx.compose.ui.unit.Dp = this.dp

data class CatalogScreenItem(
    val screen: OnboardingScreen,
    val title: String,
    val desc: String,
    val icon: ImageVector
)

data class CatalogRouteItem(
    val route: String,
    val title: String,
    val desc: String,
    val icon: ImageVector
)

data class CatalogDashboardTabItem(
    val tabName: String,
    val title: String,
    val desc: String,
    val icon: ImageVector
)

@Composable
fun CatalogDashboardTabCard(
    item: CatalogDashboardTabItem,
    isActive: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("catalog_tab_card_${item.tabName.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) BrandPrimary.copy(alpha = 0.05f) else Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isActive) BrandPrimary.copy(alpha = 0.3f) else BrandBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isActive) BrandPrimary.copy(alpha = 0.12f) else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = if (isActive) BrandPrimary else BrandTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandTextPrimary
                    )
                    if (isActive) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = BrandPrimary,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = item.desc,
                    fontSize = 11.sp,
                    color = BrandTextSecondary,
                    lineHeight = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Default.Launch,
                contentDescription = "Jump to Tab",
                tint = BrandPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
