package com.example.onboarding.ui.screens

import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import java.util.UUID
import com.example.onboarding.audio.AudioPlayerManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream
import coil.compose.AsyncImage

// --------------------------------------------------------------------------------------
// DESIGN TOKENS
// --------------------------------------------------------------------------------------
private val BrandPrimary = Color(0xFFEA4C89)   // Pink
private val BrandAccent = Color(0xFFEA4C89)    // Pink Accent
private val BrandDark = Color(0xFF0D0B26)      // Primary Dark
private val BrandBackground = Color(0xFFFCFCFD)
private val BrandBorder = Color(0xFFECECF1)
private val BrandSecondaryText = Color(0xFF7D7D8E)
private val BrandSuccess = Color(0xFF10B981)

enum class ProfileSubScreen {
    DASHBOARD,
    EDIT_PROFILE,
    ROLE_MANAGEMENT,
    AVAILABILITY,
    PROJECT_MANAGEMENT,
    SERVICES_PRICING,
    VERIFICATION,
    ANALYTICS,
    PRIVACY,
    NOTIFICATIONS,
    SUBSCRIPTION,
    BUSINESS_PROFILE,
    PROFILE_SHARE,
    PUBLIC_PREVIEW,
    BUSINESS_STOREFRONT_FLOW_11
}

// Mock structures
data class MusicianService(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val pricing: String,
    val rateType: String, // Hourly, Daily, Per Track, Negotiable
    val description: String
)

data class MusicianProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val type: String, // Audio, Video, Production
    val status: String, // Published, Draft, Archived
    val views: Int,
    val date: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileManagementMainFlowContainer(
    userName: String = "Vedanshu Srivastava",
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialProfilePath: String? = null,
    initialCoverPath: String? = null,
    onSavePictures: ((profilePath: String?, coverPath: String?) -> Unit)? = null,
    onAddPortfolioProject: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var currentSubScreen by remember { mutableStateOf(ProfileSubScreen.DASHBOARD) }

    // Core editable musician profile state
    var displayName by remember { mutableStateOf(userName) }
    var professionalName by remember { mutableStateOf("Vedanshu Beats") }
    var userHeadline by remember { mutableStateOf("Music Producer, Sound Designer & Dolby Atmos Mastering Engineer") }
    var userBio by remember { mutableStateOf("Based out of Mumbai/Chandigarh. Over 8 years working with national artists, labels, and film crews. Specialized in spatial sound processing and electronic fusion.") }
    var userLocation by remember { mutableStateOf("Mumbai, Maharashtra") }
    var userLanguages by remember { mutableStateOf("English, Hindi, Punjabi") }
    var userGenres by remember { mutableStateOf("Electronic, Sufi Rock, Cinematic Ambient") }
    
    var profilePicturePath by remember { mutableStateOf<String?>(initialProfilePath) }
    var coverPicturePath by remember { mutableStateOf<String?>(initialCoverPath) }
    
    // Social Links
    var linkWebsite by remember { mutableStateOf("https://musiclinkd.com/vedanshu") }
    var linkInstagram by remember { mutableStateOf("@vedanshu_beats") }
    var linkSpotify by remember { mutableStateOf("https://open.spotify.com/artist/vedanshu") }
    var linkYouTube by remember { mutableStateOf("https://youtube.com/c/vedanshumusic") }

    // Roles state
    val userRoles = remember { mutableStateListOf("Primary Music Producer", "Mixing & Mastering Engineer", "Session Keyboardist") }
    var availableRolePool = remember { listOf("Acoustic Guitarist", "Playback Singer", "Lyricist / Songwriter", "Voiceover Artist", "DJ / Turntablist") }

    // Availability state
    var currentAvailabilityStatus by remember { mutableStateOf("Available For Collaborations") }
    var availabilityExpiryDate by remember { mutableStateOf("2026-07-31") }

    // Dynamic stats state (Loading -> populated from state)
    var responseRate by remember { mutableStateOf<String?>(null) }
    var clientReviewsCount by remember { mutableStateOf<Int?>(null) }
    var verifiedCoCreds by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000)
        responseRate = "98%"
        clientReviewsCount = 32
        verifiedCoCreds = 24
    }

    // Business profile state (only visible to business accounts/studios)
    var businessName by remember { mutableStateOf("Octave Sound Labs Mumbai") }
    var businessDescription by remember { mutableStateOf("Elite commercial workspace equipped with certified Dolby Atmos 7.1.4 setups, massive live tracking room, and state of the art vintage preamps.") }
    var businessOperatingHours by remember { mutableStateOf("09:00 AM - 11:00 PM (Mon-Sat)") }
    var businessAddress by remember { mutableStateOf("Bandra West, Link Road, Mumbai 400050") }
    var businessGSTIn by remember { mutableStateOf("27AAAAA1111A1Z1") }
    var businessPolicies by remember { mutableStateOf("Min 2 hour booking. Cancellations require 24h notice.") }

    // Projects state
    val userProjects = remember {
        mutableStateListOf(
            MusicianProject(title = "Swar Sandhya EP", type = "EP Production", status = "Published", views = 1450, date = "2026-05-12"),
            MusicianProject(title = "Midnight Rain (Retro Remix)", type = "Remix", status = "Published", views = 890, date = "2026-06-02"),
            MusicianProject(title = "Lost In Time (Vocal Stems)", type = "Vocal Recording", status = "Draft", views = 0, date = "2026-06-25"),
            MusicianProject(title = "Delhi Diaries Documentary", type = "Film Score", status = "Archived", views = 2300, date = "2025-11-18")
        )
    }

    // Services state
    val userServices = remember {
        mutableStateListOf(
            MusicianService(name = "Premium Dolby Atmos Mixing & Mastering", pricing = "₹12,000", rateType = "Per Track", description = "Certified Atmos rendering of multitracks conforming strictly to commercial streaming standards."),
            MusicianService(name = "Studio Acoustic Session Tracking", pricing = "₹3,500", rateType = "Hourly", description = "Keyboard tracking on acoustic grand pianos and professional analog synths."),
            MusicianService(name = "Commercial Sound Design & Foley Scoring", pricing = "₹25,000", rateType = "Negotiable", description = "Complete audio post production and custom foley synthesis for documentaries & ads.")
        )
    }

    // Notification states
    var pushMessages by remember { mutableStateOf(true) }
    var pushBookings by remember { mutableStateOf(true) }
    var emailNewsletter by remember { mutableStateOf(false) }
    var smsAlerts by remember { mutableStateOf(true) }

    // Privacy states
    var isProfilePublic by remember { mutableStateOf(true) }
    var hideContactOptions by remember { mutableStateOf(false) }
    var hideFollowersList by remember { mutableStateOf(false) }

    // Subscriptions
    var userPlanType by remember { mutableStateOf("Pro") } // Free, Pro, Business

    // Verification variables
    var basicVerified by remember { mutableStateOf(true) }
    var proVerified by remember { mutableStateOf(false) }
    var businessVerified by remember { mutableStateOf(false) }
    var selectedIdType by remember { mutableStateOf("Aadhaar Card") }
    var idUploadFilename by remember { mutableStateOf<String?>(null) }
    var gstUploadFilename by remember { mutableStateOf<String?>(null) }

    // Dynamic success toast / inline banners helper
    var showSuccessBannerText by remember { mutableStateOf<String?>(null) }

    fun triggerSuccessBanner(text: String) {
        showSuccessBannerText = text
    }

    val coroutineScope = rememberCoroutineScope()
    var isSavingProfile by remember { mutableStateOf(false) }
    var savingMessage by remember { mutableStateOf("Saving changes...") }

    fun simulateSave(message: String, action: () -> Unit) {
        savingMessage = message
        isSavingProfile = true
        coroutineScope.launch {
            kotlinx.coroutines.delay(1200)
            action()
            isSavingProfile = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // DYNAMIC INLINE SUCCESS BANNER
            AnimatedVisibility(
                visible = showSuccessBannerText != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrandSuccess)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = showSuccessBannerText ?: "",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = { showSuccessBannerText = null },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            when (currentSubScreen) {
                // SCREEN 1: PROFILE DASHBOARD
                ProfileSubScreen.DASHBOARD -> {
                    val computedPercentage = run {
                        var percentage = 40
                        if (displayName.isNotEmpty()) percentage += 10
                        if (professionalName.isNotEmpty()) percentage += 10
                        if (userHeadline.isNotEmpty()) percentage += 10
                        if (userBio.isNotEmpty()) percentage += 10
                        if (userLocation.isNotEmpty()) percentage += 10
                        if (profilePicturePath != null) percentage += 10
                        percentage.coerceAtMost(100)
                    }
                    ProfileDashboardScreen(
                        userName = displayName,
                        userHeadline = userHeadline,
                        completionPercentage = computedPercentage,
                        planType = userPlanType,
                        onBack = onBackClick,
                        onNavigateTo = { screen -> currentSubScreen = screen },
                        profilePicturePath = profilePicturePath,
                        coverPicturePath = coverPicturePath
                    )
                }

                // SCREEN 2: EDIT PROFILE
                ProfileSubScreen.EDIT_PROFILE -> {
                    EditProfileScreen(
                        displayName = displayName,
                        onDisplayNameChange = { displayName = it },
                        professionalName = professionalName,
                        onProfessionalNameChange = { professionalName = it },
                        headline = userHeadline,
                        onHeadlineChange = { userHeadline = it },
                        bio = userBio,
                        onBioChange = { userBio = it },
                        location = userLocation,
                        onLocationChange = { userLocation = it },
                        languages = userLanguages,
                        onLanguagesChange = { userLanguages = it },
                        genres = userGenres,
                        onGenresChange = { userGenres = it },
                        website = linkWebsite,
                        onWebsiteChange = { linkWebsite = it },
                        instagram = linkInstagram,
                        onInstagramChange = { linkInstagram = it },
                        spotify = linkSpotify,
                        onSpotifyChange = { linkSpotify = it },
                        youtube = linkYouTube,
                        onYouTubeChange = { linkYouTube = it },
                        profilePicturePath = profilePicturePath,
                        onProfilePicturePathChange = { profilePicturePath = it },
                        coverPicturePath = coverPicturePath,
                        onCoverPicturePathChange = { coverPicturePath = it },
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD },
                        onSave = {
                            simulateSave("Publishing public identity to Firestore...") {
                                onSavePictures?.invoke(profilePicturePath, coverPicturePath)
                                try {
                                    val db = com.example.onboarding.database.FirebaseManager.getFirestore()
                                    if (db != null) {
                                        val roleList = userRoles.toList()
                                        val primary = roleList.firstOrNull() ?: "music_producer"
                                        val secondary = if (roleList.size > 1) roleList[1] else ""
                                        val langList = userLanguages.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                        val genreList = userGenres.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                        val profileMap = hashMapOf(
                                            "displayName" to displayName,
                                            "bio" to userBio,
                                            "location" to userLocation,
                                            "primaryRole" to primary,
                                            "secondaryRole" to secondary,
                                            "languages" to langList,
                                            "genres" to genreList,
                                            "instruments" to emptyList<String>(),
                                            "personaBadges" to emptyList<String>(),
                                            "updatedAt" to com.google.firebase.Timestamp.now()
                                        )
                                        val uid = com.example.onboarding.database.FirebaseManager.getAuth()?.currentUser?.uid ?: "local_user_uid"
                                        db.collection("users").document(uid)
                                            .set(profileMap, com.google.firebase.firestore.SetOptions.merge())
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                triggerSuccessBanner("Identity profile successfully saved & published to Firestore!")
                                currentSubScreen = ProfileSubScreen.DASHBOARD
                            }
                        }
                    )
                }

                // SCREEN 3: ROLE MANAGEMENT
                ProfileSubScreen.ROLE_MANAGEMENT -> {
                    RoleManagementScreen(
                        userRoles = userRoles,
                        availableRolePool = availableRolePool,
                        onAddRole = { role ->
                            if (!userRoles.contains(role)) {
                                userRoles.add(role)
                                triggerSuccessBanner("Added $role to identity portfolio")
                            }
                        },
                        onRemoveRole = { role ->
                            if (userRoles.size > 1) {
                                userRoles.remove(role)
                                triggerSuccessBanner("Removed role $role")
                            } else {
                                Toast.makeText(context, "Must keep at least 1 primary role!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD }
                    )
                }

                // SCREEN 4: AVAILABILITY
                ProfileSubScreen.AVAILABILITY -> {
                    AvailabilityManagementScreen(
                        currentStatus = currentAvailabilityStatus,
                        onStatusSelect = { currentAvailabilityStatus = it },
                        expiryDate = availabilityExpiryDate,
                        onExpiryDateChange = { availabilityExpiryDate = it },
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD },
                        onSave = {
                            simulateSave("Updating availability...") {
                                triggerSuccessBanner("Availability status set to '$currentAvailabilityStatus' until $availabilityExpiryDate")
                                currentSubScreen = ProfileSubScreen.DASHBOARD
                            }
                        }
                    )
                }

                // SCREEN 5: PROJECT MANAGEMENT
                ProfileSubScreen.PROJECT_MANAGEMENT -> {
                    ProjectManagementScreen(
                        projects = userProjects,
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD },
                        onAddProject = {
                            if (onAddPortfolioProject != null) {
                                onAddPortfolioProject()
                            } else {
                                userProjects.add(0, MusicianProject(title = "New Acoustic Project", type = "Audio Demo", status = "Draft", views = 0, date = "2026-06-27"))
                                triggerSuccessBanner("Created new project draft!")
                            }
                        },
                        onDeleteProject = { project ->
                            userProjects.remove(project)
                            triggerSuccessBanner("Project '${project.title}' deleted")
                        },
                        onDuplicateProject = { project ->
                            userProjects.add(userProjects.indexOf(project) + 1, project.copy(id = UUID.randomUUID().toString(), title = "${project.title} (Copy)", status = "Draft", views = 0))
                            triggerSuccessBanner("Duplicated '${project.title}' as Draft")
                        },
                        onToggleStatus = { project, newStatus ->
                            val index = userProjects.indexOf(project)
                            if (index != -1) {
                                userProjects[index] = project.copy(status = newStatus)
                                triggerSuccessBanner("Project '${project.title}' set to $newStatus")
                            }
                        }
                    )
                }

                // SCREEN 6: SERVICES & PRICING
                ProfileSubScreen.SERVICES_PRICING -> {
                    ServicesAndPricingScreen(
                        services = userServices,
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD },
                        onAddService = { name, rate, type, desc ->
                            userServices.add(MusicianService(name = name, pricing = rate, rateType = type, description = desc))
                            triggerSuccessBanner("New listing '$name' added to services")
                        },
                        onDeleteService = { service ->
                            userServices.remove(service)
                            triggerSuccessBanner("Removed listing '${service.name}'")
                        }
                    )
                }

                // SCREEN 7: VERIFICATION
                ProfileSubScreen.VERIFICATION -> {
                    VerificationScreen(
                        basicVerified = basicVerified,
                        proVerified = proVerified,
                        businessVerified = businessVerified,
                        selectedIdType = selectedIdType,
                        onIdTypeChange = { selectedIdType = it },
                        idUploadFilename = idUploadFilename,
                        onIdUpload = { filename ->
                            idUploadFilename = filename
                            basicVerified = true
                            triggerSuccessBanner("Government credentials submitted for review!")
                        },
                        gstUploadFilename = gstUploadFilename,
                        onGstUpload = { filename ->
                            gstUploadFilename = filename
                            businessVerified = true
                            triggerSuccessBanner("GST document attached. Processing business badge!")
                        },
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD }
                    )
                }

                // SCREEN 8: ANALYTICS
                ProfileSubScreen.ANALYTICS -> {
                    AnalyticsDashboardScreen(
                        userPlan = userPlanType,
                        onUpgradeClick = {
                            currentSubScreen = ProfileSubScreen.SUBSCRIPTION
                        },
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD }
                    )
                }

                // SCREEN 9: PRIVACY
                ProfileSubScreen.PRIVACY -> {
                    PrivacyScreen(
                        isPublic = isProfilePublic,
                        onPublicChange = { isProfilePublic = it },
                        hideContact = hideContactOptions,
                        onHideContactChange = { hideContactOptions = it },
                        hideFollowers = hideFollowersList,
                        onHideFollowersChange = { hideFollowersList = it },
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD },
                        onSave = {
                            simulateSave("Securing privacy credentials...") {
                                triggerSuccessBanner("Privacy settings saved successfully!")
                                currentSubScreen = ProfileSubScreen.DASHBOARD
                            }
                        }
                    )
                }

                // SCREEN 10: NOTIFICATIONS
                ProfileSubScreen.NOTIFICATIONS -> {
                    NotificationPreferencesScreen(
                        pushMessages = pushMessages,
                        onPushMessagesChange = { pushMessages = it },
                        pushBookings = pushBookings,
                        onPushBookingsChange = { pushBookings = it },
                        emailNewsletter = emailNewsletter,
                        onEmailNewsletterChange = { emailNewsletter = it },
                        smsAlerts = smsAlerts,
                        onSmsAlertsChange = { smsAlerts = it },
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD },
                        onSave = {
                            simulateSave("Saving alert preferences...") {
                                triggerSuccessBanner("Alert channels and preferences updated!")
                                currentSubScreen = ProfileSubScreen.DASHBOARD
                            }
                        }
                    )
                }

                // SCREEN 11: SUBSCRIPTION
                ProfileSubScreen.SUBSCRIPTION -> {
                    SubscriptionPlanScreen(
                        currentPlan = userPlanType,
                        onSelectPlan = { selectedPlan ->
                            userPlanType = selectedPlan
                            triggerSuccessBanner("Successfully subscribed to MusicLinkd $selectedPlan plan!")
                            currentSubScreen = ProfileSubScreen.DASHBOARD
                        },
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD }
                    )
                }

                // SCREEN 12: BUSINESS PROFILE
                ProfileSubScreen.BUSINESS_PROFILE -> {
                    BusinessProfileManagementScreen(
                        name = businessName,
                        onNameChange = { businessName = it },
                        description = businessDescription,
                        onDescriptionChange = { businessDescription = it },
                        operatingHours = businessOperatingHours,
                        onOperatingHoursChange = { businessOperatingHours = it },
                        address = businessAddress,
                        onAddressChange = { businessAddress = it },
                        gst = businessGSTIn,
                        onGstChange = { businessGSTIn = it },
                        policies = businessPolicies,
                        onPoliciesChange = { businessPolicies = it },
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD },
                        onSave = {
                            simulateSave("Encrypting business parameters...") {
                                triggerSuccessBanner("Business details locked for Dolby studio verification!")
                                currentSubScreen = ProfileSubScreen.DASHBOARD
                            }
                        }
                    )
                }

                ProfileSubScreen.BUSINESS_STOREFRONT_FLOW_11 -> {
                    BusinessProfileWorkspace(
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD }
                    )
                }

                // SCREEN 13: PROFILE SHARE
                ProfileSubScreen.PROFILE_SHARE -> {
                    ProfileShareScreen(
                        vanityUrl = linkWebsite,
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD },
                        onCopyLink = {
                            triggerSuccessBanner("Profile link copied to clipboard!")
                        }
                    )
                }

                // SCREEN 14: PUBLIC PROFILE PREVIEW
                ProfileSubScreen.PUBLIC_PREVIEW -> {
                    PublicProfilePreviewScreen(
                        displayName = displayName,
                        headline = userHeadline,
                        bio = userBio,
                        location = userLocation,
                        genres = userGenres,
                        status = currentAvailabilityStatus,
                        services = userServices,
                        projects = userProjects.filter { it.status == "Published" },
                        onBack = { currentSubScreen = ProfileSubScreen.DASHBOARD }
                    )
                }
            }
        }

        if (isSavingProfile) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) {}, // Scrim
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BrandBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .padding(32.dp)
                        .widthIn(max = 320.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = BrandPrimary,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = savingMessage,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B) // Slate Dark
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Please wait, syncing to cloud servers...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B) // Slate Light
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerSkeleton(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(4.dp)
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Box(
        modifier = modifier
            .background(Color.LightGray.copy(alpha = alpha), shape)
    )
}

// --------------------------------------------------------------------------------------
// SCREEN 1: PROFILE DASHBOARD
// --------------------------------------------------------------------------------------
@Composable
fun ProfileDashboardScreen(
    userName: String,
    userHeadline: String,
    completionPercentage: Int,
    planType: String,
    onBack: () -> Unit,
    onNavigateTo: (ProfileSubScreen) -> Unit,
    profilePicturePath: String? = null,
    coverPicturePath: String? = null
) {
    var followersCountState by remember { mutableStateOf(1840) }
    var profileViewsState by remember { mutableStateOf<String?>("Loading...") }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1200)
        profileViewsState = "3,250"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFFCFCFD))
            .padding(bottom = 48.dp)
    ) {
        // RETRO HERO COVER HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Brush.verticalGradient(colors = listOf(BrandDark, Color(0xFF1F1A3E))))
        ) {
            if (coverPicturePath != null) {
                AsyncImage(
                    model = File(coverPicturePath),
                    contentDescription = "Cover Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=800&auto=format&fit=crop&q=80",
                    contentDescription = "Cover Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(BrandAccent)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${planType.uppercase()} WORKSPACE",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // PROFILE IDENTIFIER WITH OFFSET PHOTO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                // Avatar Offset circle
                Box(
                    modifier = Modifier
                        .offset(y = (-40).dp)
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary)
                        .border(4.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (profilePicturePath != null) {
                        AsyncImage(
                            model = File(profilePicturePath),
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandDark,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Verified, "Verified", tint = BrandAccent, modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = "Verified Music Professional",
                        fontSize = 11.sp,
                        color = BrandAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Text(
                text = userHeadline,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BrandSecondaryText,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // REPUTATION STATS ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandBorder.copy(alpha = 0.25f))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("$followersCountState", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
                    Text("Followers", fontSize = 11.sp, color = BrandSecondaryText)
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(BrandBorder).align(Alignment.CenterVertically))
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    if (profileViewsState == "Loading...") {
                        ShimmerSkeleton(
                            modifier = Modifier
                                .width(56.dp)
                                .height(18.dp)
                                .padding(vertical = 2.dp)
                        )
                    } else {
                        Text(profileViewsState ?: "---", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
                    }
                    Text("Profile Views", fontSize = 11.sp, color = BrandSecondaryText)
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(BrandBorder).align(Alignment.CenterVertically))
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("4.9★", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandAccent)
                    Text("32 Reviews", fontSize = 11.sp, color = BrandSecondaryText)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECTION 1: PERFORMANCE & SETUP
            Text(
                text = "SETUP & PERFORMANCE",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandPrimary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            /*
            // CREATOR COMPLETION CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Profile Completion Status", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                        Text("$completionPercentage% Done", fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { completionPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = BrandPrimary,
                        trackColor = BrandBorder.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Complete professional roles and list rates to achieve 100% visibility.",
                        fontSize = 11.sp,
                        color = BrandSecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            */

            // PRIMARY ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onNavigateTo(ProfileSubScreen.PUBLIC_PREVIEW) },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .testTag("dashboard_preview_profile_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDark)
                ) {
                    Icon(Icons.Default.Visibility, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Preview Public Page", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                
                Button(
                    onClick = { onNavigateTo(ProfileSubScreen.PROFILE_SHARE) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BrandBorder)
                ) {
                    Icon(Icons.Default.QrCode, null, modifier = Modifier.size(16.dp), tint = BrandDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share QR", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // MODULAR WORKSPACE DIRECTORY
            WorkspaceSectionGrid(onNavigateTo = onNavigateTo)
        }
    }
}

@Composable
fun DashboardStat(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 12.sp, color = BrandSecondaryText)
    }
}

@Composable
fun WorkspaceItemCard(
    icon: ImageVector,
    title: String,
    description: String,
    status: String,
    onClick: () -> Unit,
    isComingSoon: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isComingSoon) { onClick() }
            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon on the left
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandPrimary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text info in the center
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = BrandDark
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = BrandSecondaryText
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status Badge and Chevron on the right
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(
                            if (isComingSoon) Color(0xFFEA4C89).copy(alpha = 0.12f)
                            else if (status.contains("Verified") || status.contains("Live") || status.contains("Open") || status.contains("Active") || status.contains("Ready")) BrandSuccess.copy(alpha = 0.12f)
                            else BrandBorder.copy(alpha = 0.5f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isComingSoon) "COMING SOON" else status.uppercase(),
                        color = if (isComingSoon) Color(0xFFEA4C89)
                                else if (status.contains("Verified") || status.contains("Live") || status.contains("Open") || status.contains("Active") || status.contains("Ready")) BrandSuccess
                                else BrandDark.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }

                if (!isComingSoon) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = BrandSecondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WorkspaceSectionGrid(onNavigateTo: (ProfileSubScreen) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        
        // SECTION 1: IDENTITY & CREATIVE SHOWCASE
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "PORTFOLIO & IDENTITY SHOWCASE",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandDark.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            WorkspaceItemCard(
                icon = Icons.Outlined.Person,
                title = "Edit Portfolio Info",
                description = "Configure identity bio, photo, location, links",
                status = "Live",
                onClick = { onNavigateTo(ProfileSubScreen.EDIT_PROFILE) }
            )
            WorkspaceItemCard(
                icon = Icons.Outlined.MusicNote,
                title = "Professional Roles",
                description = "Define instruments, specialties and display order",
                status = "2 Active",
                onClick = { onNavigateTo(ProfileSubScreen.ROLE_MANAGEMENT) }
            )
            WorkspaceItemCard(
                icon = Icons.Outlined.Album,
                title = "Track & EP Projects",
                description = "Add high-fidelity audio singles or live projects",
                status = "3 Tracks",
                onClick = { onNavigateTo(ProfileSubScreen.PROJECT_MANAGEMENT) }
            )
        }

        // SECTION 2: BOOKINGS & AVAILABILITY
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "BOOKINGS & SERVICES",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandDark.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            WorkspaceItemCard(
                icon = Icons.Outlined.Schedule,
                title = "Availability Status",
                description = "Configure live work readiness and response time",
                status = "Open",
                onClick = { onNavigateTo(ProfileSubScreen.AVAILABILITY) }
            )
            WorkspaceItemCard(
                icon = Icons.Outlined.Payments,
                title = "Services & Pricing",
                description = "List service rates, billing rules, and packages",
                status = "3 Packages",
                onClick = { onNavigateTo(ProfileSubScreen.SERVICES_PRICING) }
            )
        }

        // SECTION 3: TRUST & SHARING
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "TRUST & CREDIBILITY",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandDark.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            WorkspaceItemCard(
                icon = Icons.Outlined.Verified,
                title = "Verify Professional Identity",
                description = "Verify via GST, IPRS, or registration credentials",
                status = "Verified",
                onClick = { onNavigateTo(ProfileSubScreen.VERIFICATION) }
            )
            WorkspaceItemCard(
                icon = Icons.Outlined.Star,
                title = "Client Reviews",
                description = "Approve and reply to verified client feedback",
                status = "4.9 ★",
                onClick = { onNavigateTo(ProfileSubScreen.DASHBOARD) } // Navigates to dashboard as mock or handle
            )
        }

        // SECTION 4: GROWTH & OPPORTUNITIES
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "GROWTH & GIG OPPORTUNITIES",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandDark.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            WorkspaceItemCard(
                icon = Icons.Outlined.Work,
                title = "Live Opportunities Feed",
                description = "Apply to nearby gigs, session briefs, and projects",
                status = "4 Active",
                onClick = { onNavigateTo(ProfileSubScreen.BUSINESS_STOREFRONT_FLOW_11) }
            )
            WorkspaceItemCard(
                icon = Icons.Outlined.BarChart,
                title = "Audience Analytics",
                description = "Track impressions, clicks, leads, and bookings",
                status = "Locked",
                onClick = { onNavigateTo(ProfileSubScreen.ANALYTICS) },
                isComingSoon = true
            )
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 2: EDIT PROFILE
// --------------------------------------------------------------------------------------
@Composable
fun EditProfileScreen(
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    professionalName: String,
    onProfessionalNameChange: (String) -> Unit,
    headline: String,
    onHeadlineChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    languages: String,
    onLanguagesChange: (String) -> Unit,
    genres: String,
    onGenresChange: (String) -> Unit,
    website: String,
    onWebsiteChange: (String) -> Unit,
    instagram: String,
    onInstagramChange: (String) -> Unit,
    spotify: String,
    onSpotifyChange: (String) -> Unit,
    youtube: String,
    onYouTubeChange: (String) -> Unit,
    profilePicturePath: String?,
    onProfilePicturePathChange: (String?) -> Unit,
    coverPicturePath: String?,
    onCoverPicturePathChange: (String?) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    var showHeadshotOptions by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            try {
                val headshotFile = File(context.cacheDir, "headshot_${System.currentTimeMillis()}.jpg")
                FileOutputStream(headshotFile).use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, out)
                }
                onProfilePicturePathChange(headshotFile.absolutePath)
                Toast.makeText(context, "Headshot captured and set!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to save captured photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission is required to capture headshot", Toast.LENGTH_SHORT).show()
        }
    }

    val profilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val copiedPath = copyUriToLocalFile(context, uri, "profile")
            if (copiedPath != null) {
                onProfilePicturePathChange(copiedPath)
            }
        }
    }

    val coverPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val copiedPath = copyUriToLocalFile(context, uri, "cover")
            if (copiedPath != null) {
                onCoverPicturePathChange(copiedPath)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // TOP BACK BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BrandDark)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit Public Identity", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
            }
            TextButton(onClick = onSave, modifier = Modifier.testTag("save_edit_profile_button")) {
                Text("Save", color = BrandPrimary, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Visual Assets", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)
            
            // Cover Photo editor box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandDark)
                    .clickable { coverPicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (coverPicturePath != null) {
                    AsyncImage(
                        model = File(coverPicturePath),
                        contentDescription = "Cover Banner",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PhotoCamera, null, tint = Color.White)
                        Text("Change Cover Banner", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary)
                        .clickable { showHeadshotOptions = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (profilePicturePath != null) {
                        AsyncImage(
                            model = File(profilePicturePath),
                            contentDescription = "Public Avatar",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.AddAPhoto, null, tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Public Avatar / Headshot", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                    Text("Tap to capture headshot with camera or pick from gallery.", fontSize = 10.sp, color = BrandSecondaryText)
                }
            }

            if (showHeadshotOptions) {
                AlertDialog(
                    onDismissRequest = { showHeadshotOptions = false },
                    title = { Text("Update Profile Headshot", fontWeight = FontWeight.Bold) },
                    text = { Text("Choose how you would like to set your professional avatar headshot:") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showHeadshotOptions = false
                                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.size(18.dp))
                                Text("Take Photo", fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showHeadshotOptions = false
                                profilePicker.launch("image/*")
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(18.dp))
                                Text("Pick Gallery", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                )
            }

            HorizontalDivider(color = BrandBorder)

            Text("Public Profile Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)

            LabeledInputField(label = "Display Name", value = displayName, onValueChange = onDisplayNameChange)
            LabeledInputField(label = "Professional Handle (e.g. Beats Name)", value = professionalName, onValueChange = onProfessionalNameChange)
            LabeledInputField(label = "Elevator Headline", value = headline, onValueChange = onHeadlineChange)
            LabeledInputField(label = "Detailed Professional Bio", value = bio, onValueChange = onBioChange, isMultiline = true)
            LabeledInputField(label = "Location City", value = location, onValueChange = onLocationChange)
            LabeledInputField(label = "Fluent Languages", value = languages, onValueChange = onLanguagesChange)
            LabeledInputField(label = "Acoustic / Music Genres (comma separated)", value = genres, onValueChange = onGenresChange)

            // Genre & Skill Tags Live Preview Chips
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Genre & Skill Tag Highlights", fontSize = 11.sp, color = BrandSecondaryText, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val genreList = genres.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    if (genreList.isEmpty()) {
                        Text("No genre tags added yet", fontSize = 11.sp, color = BrandSecondaryText)
                    } else {
                        genreList.forEach { tag ->
                            Surface(
                                color = BrandPrimary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "🎵 $tag",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = BrandBorder)

            Text("External Sound & Social Links", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)

            LabeledInputField(label = "🌐 Personal Website Portfolio Link", value = website, onValueChange = onWebsiteChange)
            LabeledInputField(label = "📸 Instagram Username", value = instagram, onValueChange = onInstagramChange)
            LabeledInputField(label = "🎵 Spotify Artist URL", value = spotify, onValueChange = onSpotifyChange)
            LabeledInputField(label = "📺 YouTube Channel URL", value = youtube, onValueChange = onYouTubeChange)

            // Firestore Database Sync Indicator
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Firestore Network Sync Enabled", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        Text("Saving will update your musician profile in the Firestore database.", fontSize = 10.sp, color = Color(0xFF166534))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_edit_profile_main_btn"),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Publish to Firestore", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LabeledInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isMultiline: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 11.sp, color = BrandSecondaryText, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(1.dp, BrandBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            BasicInputPlaceholder(text = value, onValueChange = onValueChange, placeholder = "Enter $label...")
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 3: ROLE MANAGEMENT
// --------------------------------------------------------------------------------------
@Composable
fun RoleManagementScreen(
    userRoles: List<String>,
    availableRolePool: List<String>,
    onAddRole: (String) -> Unit,
    onRemoveRole: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BrandDark)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("Role & Talent Settings", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Active Roles (Drag or re-arrange to sort primary priority)", fontSize = 12.sp, color = BrandSecondaryText)
            
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                userRoles.forEachIndexed { index, role ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (index == 0) BrandPrimary else BrandBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (index == 0) BrandPrimary else BrandBorder)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (index == 0) "Primary" else "Role ${index + 1}",
                                        color = if (index == 0) Color.White else BrandDark,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(role, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                            }
                            IconButton(onClick = { onRemoveRole(role) }) {
                                Icon(Icons.Default.DeleteOutline, "Remove", tint = BrandAccent)
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = BrandBorder)

            Text("Discover and Add Creative Talent Categories", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)
            
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(availableRolePool) { poolRole ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(BrandPrimary.copy(alpha = 0.08f))
                            .border(1.dp, BrandPrimary.copy(alpha = 0.2f), RoundedCornerShape(99.dp))
                            .clickable { onAddRole(poolRole) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, null, tint = BrandPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(poolRole, fontSize = 11.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💡 Pro Visibility Tip", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Your first primary role represents how you show up in Discovery stacks, studio matching searches and marketplace listings. Order them carefully!", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, lineHeight = 16.sp)
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 4: AVAILABILITY
// --------------------------------------------------------------------------------------
@Composable
fun AvailabilityManagementScreen(
    currentStatus: String,
    onStatusSelect: (String) -> Unit,
    expiryDate: String,
    onExpiryDateChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val statuses = listOf(
        "Available For Collaborations" to "🟢",
        "Accepting Remote Mix Projects" to "💻",
        "Available For Gigs & Festivals" to "🎸",
        "Busy/Fully Booked in SoundLab" to "🟡",
        "Touring Globally / Out of Station" to "✈️"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BrandDark)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Manage Studio Presence", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
            }
            TextButton(onClick = onSave) {
                Text("Apply", color = BrandPrimary, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Current Booking Availability Status", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                statuses.forEach { (statusName, emoji) ->
                    val isSelected = currentStatus == statusName
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStatusSelect(statusName) }
                            .border(1.dp, if (isSelected) BrandAccent else BrandBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(emoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(statusName, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = BrandDark)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.Check, null, tint = BrandAccent)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("Set Expiration for Auto Expiry Date", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.dp, BrandBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicInputPlaceholder(text = expiryDate, onValueChange = onExpiryDateChange, placeholder = "YYYY-MM-DD")
                    Icon(Icons.Default.DateRange, null, tint = BrandSecondaryText)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Commit Live Presence Status", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 5: PROJECT MANAGEMENT
// --------------------------------------------------------------------------------------
@Composable
fun ProjectManagementScreen(
    projects: List<MusicianProject>,
    onBack: () -> Unit,
    onAddProject: () -> Unit,
    onDeleteProject: (MusicianProject) -> Unit,
    onDuplicateProject: (MusicianProject) -> Unit,
    onToggleStatus: (MusicianProject, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BrandDark)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Track & Portfolio Manager", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
            }
            IconButton(onClick = onAddProject) {
                Icon(Icons.Default.Add, null, tint = BrandPrimary)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Your Uploaded EP & Stems (${projects.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    TextButton(onClick = onAddProject) {
                        Text("+ Add New Track", fontSize = 12.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (projects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎵", fontSize = 36.sp)
                            Text("No projects linked yet. Paste media URL links above!", color = BrandSecondaryText, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(projects) { project ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(project.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    when (project.status) {
                                                        "Published" -> BrandSuccess.copy(alpha = 0.1f)
                                                        "Draft" -> BrandPrimary.copy(alpha = 0.1f)
                                                        else -> BrandSecondaryText.copy(alpha = 0.1f)
                                                    }
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = project.status.uppercase(),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (project.status) {
                                                    "Published" -> BrandSuccess
                                                    "Draft" -> BrandPrimary
                                                    else -> BrandSecondaryText
                                                }
                                            )
                                        }
                                    }
                                    Text("${project.type} • Uploaded ${project.date}", fontSize = 11.sp, color = BrandSecondaryText)
                                }
                                
                                Text("👁️ ${project.views} views", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextButton(
                                    onClick = {
                                        val nextState = when (project.status) {
                                            "Published" -> "Draft"
                                            "Draft" -> "Published"
                                            else -> "Published"
                                        }
                                        onToggleStatus(project, nextState)
                                    }
                                ) {
                                    Text(if (project.status == "Published") "Unpublish" else "Publish", fontSize = 12.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                                }
                                TextButton(onClick = { onDuplicateProject(project) }) {
                                    Text("Duplicate", fontSize = 12.sp, color = BrandSecondaryText)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = { onDeleteProject(project) }) {
                                    Icon(Icons.Default.DeleteOutline, "Delete", tint = BrandAccent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 6: SERVICES & PRICING
// --------------------------------------------------------------------------------------
@Composable
fun ServicesAndPricingScreen(
    services: List<MusicianService>,
    onBack: () -> Unit,
    onAddService: (String, String, String, String) -> Unit,
    onDeleteService: (MusicianService) -> Unit
) {
    var newServiceName by remember { mutableStateOf("") }
    var newServicePricing by remember { mutableStateOf("₹5,000") }
    var selectedRateType by remember { mutableStateOf("Hourly") }
    var newServiceDescription by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BrandDark)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("Services Directory & Pricing", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("List Services Offered", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            items(services) { service ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(service.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                            IconButton(onClick = { onDeleteService(service) }) {
                                Icon(Icons.Default.DeleteOutline, null, tint = BrandAccent)
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("${service.pricing} (${service.rateType})", fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 12.sp)
                            ComingSoonPill()
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(service.description, fontSize = 11.sp, color = BrandSecondaryText)
                    }
                }
            }

            item {
                HorizontalDivider(color = BrandBorder)
                Text("Add New Creative Listing", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LabeledInputField(label = "Service Name", value = newServiceName, onValueChange = { newServiceName = it })
                        LabeledInputField(label = "Base Pricing (INR)", value = newServicePricing, onValueChange = { newServicePricing = it })
                        
                        // Custom rate type row
                        Text("Rate Type", fontSize = 11.sp, color = BrandSecondaryText, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Hourly", "Daily", "Per Track", "Negotiable").forEach { rate ->
                                val isSelected = selectedRateType == rate
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(99.dp))
                                        .background(if (isSelected) BrandPrimary else BrandPrimary.copy(alpha = 0.05f))
                                        .clickable { selectedRateType = rate }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(rate, color = if (isSelected) Color.White else BrandPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        LabeledInputField(label = "Brief Scope / Deliverables Description", value = newServiceDescription, onValueChange = { newServiceDescription = it })

                        Button(
                            onClick = {
                                if (newServiceName.isNotEmpty() && newServiceDescription.isNotEmpty()) {
                                    onAddService(newServiceName, newServicePricing, selectedRateType, newServiceDescription)
                                    newServiceName = ""
                                    newServiceDescription = ""
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(99.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Text("Launch Service Listing", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 7: VERIFICATION
// --------------------------------------------------------------------------------------
@Composable
fun VerificationScreen(
    basicVerified: Boolean,
    proVerified: Boolean,
    businessVerified: Boolean,
    selectedIdType: String,
    onIdTypeChange: (String) -> Unit,
    idUploadFilename: String?,
    onIdUpload: (String) -> Unit,
    gstUploadFilename: String?,
    onGstUpload: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BrandDark)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("Verify Professional Status", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Professional Verification Timeline", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            
            // TIMELINE MOCK COMPONENT
            TimelineMockItem(
                title = "1. Personal Account Authentication",
                subtitle = "Ensures verified real identity under basic rules.",
                isComplete = basicVerified
            )
            TimelineMockItem(
                title = "2. Professional Creator Badge",
                subtitle = "Upload Govt ID (Aadhaar/PAN) or IPRS membership card.",
                isComplete = proVerified,
                isActive = basicVerified
            )
            TimelineMockItem(
                title = "3. Verified Dolby Studio / Agency Badge",
                subtitle = "Provide Business Registration (GST) Certificate.",
                isComplete = businessVerified,
                isActive = proVerified
            )

            HorizontalDivider(color = BrandBorder)

            Text("Verify Identity Under Aadhaar / PAN", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            
            // ID Type Selector Row
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Aadhaar Card", "PAN Card", "Passport / Voter ID").forEach { idType ->
                    val isSelected = selectedIdType == idType
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BrandPrimary else BrandBorder)
                            .clickable { onIdTypeChange(idType) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(idType, color = if (isSelected) Color.White else BrandDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Drag and drop Govt ID mock
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(2.dp, BrandBorder, RoundedCornerShape(12.dp))
                    .clickable { onIdUpload("Aadhaar_Front_SwarSandhya.pdf") }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CloudUpload, null, tint = BrandPrimary, modifier = Modifier.size(36.dp))
                    Text(
                        text = idUploadFilename ?: "Upload Govt ID Document / Photo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = BrandDark,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text("Supported formats: PDF, PNG, JPG (max 4MB)", fontSize = 10.sp, color = BrandSecondaryText)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("Verify Dolby SoundLab GST Registry", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(2.dp, BrandBorder, RoundedCornerShape(12.dp))
                    .clickable { onGstUpload("GST_Verification_VedanshuMusic.pdf") }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FilePresent, null, tint = BrandAccent, modifier = Modifier.size(36.dp))
                    Text(
                        text = gstUploadFilename ?: "Attach GST Certificate (Form GST REG-06)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = BrandDark,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text("Mandatory for professional studio validation.", fontSize = 10.sp, color = BrandSecondaryText)
                }
            }
        }
    }
}

@Composable
fun TimelineMockItem(title: String, subtitle: String, isComplete: Boolean, isActive: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (isComplete) BrandSuccess
                    else if (isActive) BrandPrimary
                    else BrandBorder
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isComplete) Icons.Default.Check else Icons.Default.Circle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isActive || isComplete) BrandDark else BrandSecondaryText)
            Text(subtitle, fontSize = 10.sp, color = BrandSecondaryText)
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 8: ANALYTICS (CUSTOM CANVAS-DRAWN CHARTS & PREMIUM BLUR)
// --------------------------------------------------------------------------------------
@Composable
fun AnalyticsDashboardScreen(
    userPlan: String,
    onUpgradeClick: () -> Unit,
    onBack: () -> Unit
) {
    var activeGraphTab by remember { mutableStateOf("Weekly") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BrandDark)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("Audience Analytics Dashboard", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Editorial Traffic Insights", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnalyticsScorecard(label = "Search Appearances", value = "1,120", trend = "+12%")
                AnalyticsScorecard(label = "EP Playback Plays", value = "5,490", trend = "+28%")
            }

            // Tabs for charts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandBorder, RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                listOf("Weekly", "Monthly", "Yearly").forEach { tab ->
                    val isSelected = activeGraphTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeGraphTab = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(tab, color = if (isSelected) BrandPrimary else BrandSecondaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // CUSTOM CANVAS-DRAWN GRAPH OR BLURRED OVERLAY IF FREE
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Custom Draw Waveform Chart on canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .blur(if (userPlan == "Free") 12.dp else 0.dp)
                    ) {
                        val width = size.width
                        val height = size.height
                        
                        // Background grid lines
                        for (i in 1..4) {
                            val y = height * (i / 5f)
                            drawLine(
                                color = BrandBorder,
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1f
                            )
                        }

                        // Draw graph path
                        val points = listOf(
                            Offset(0f, height * 0.8f),
                            Offset(width * 0.2f, height * 0.5f),
                            Offset(width * 0.4f, height * 0.65f),
                            Offset(width * 0.6f, height * 0.25f),
                            Offset(width * 0.8f, height * 0.35f),
                            Offset(width, height * 0.1f)
                        )

                        val path = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                cubicTo(
                                    (points[i - 1].x + points[i].x) / 2, points[i - 1].y,
                                    (points[i - 1].x + points[i].x) / 2, points[i].y,
                                    points[i].x, points[i].y
                                )
                            }
                        }

                        drawPath(
                            path = path,
                            color = BrandPrimary,
                            style = Stroke(width = 4f)
                        )
                    }

                    if (userPlan == "Free") {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.75f))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔒 Premium Creators Only", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)
                                Text("Upgrade to Pro to unlock comprehensive daily search impressions & booking charts.", fontSize = 11.sp, color = BrandSecondaryText, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 12.dp))
                                Button(
                                    onClick = onUpgradeClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                    shape = RoundedCornerShape(99.dp)
                                ) {
                                    Text("Upgrade Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("Session Booking Leads", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Direct Inbound Leads: 42 leads", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                    Text("Average response time: 2.4 hours", fontSize = 11.sp, color = BrandSecondaryText)
                }
            }
        }
    }
}

@Composable
fun AnalyticsScorecard(label: String, value: String, trend: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, fontSize = 11.sp, color = BrandSecondaryText)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BrandDark)
                Text(trend, color = BrandSuccess, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 9: PRIVACY
// --------------------------------------------------------------------------------------
@Composable
fun PrivacyScreen(
    isPublic: Boolean,
    onPublicChange: (Boolean) -> Unit,
    hideContact: Boolean,
    onHideContactChange: (Boolean) -> Unit,
    hideFollowers: Boolean,
    onHideFollowersChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BrandDark)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Privacy & Visibility", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
            }
            TextButton(onClick = onSave) {
                Text("Save", color = BrandPrimary, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Configure Public Profile Limits", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            PrivacyToggleRow(
                title = "Public Search Indexing",
                subtitle = "Allow Google & external scouts to discover your public portfolio.",
                checked = isPublic,
                onCheckedChange = onPublicChange
            )
            PrivacyToggleRow(
                title = "Hide Instant Call / Chat CTAs",
                subtitle = "Only approved booking clients will see your contact options.",
                checked = hideContact,
                onCheckedChange = onHideContactChange
            )
            PrivacyToggleRow(
                title = "Hide Live Followers Count",
                subtitle = "Keep your followers metrics private on display headers.",
                checked = hideFollowers,
                onCheckedChange = onHideFollowersChange
            )

            HorizontalDivider(color = BrandBorder)

            Text("Account Actions & Data Archiving", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(1.dp, BrandBorder, RoundedCornerShape(99.dp)),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Export Profile Data (JSON)", color = BrandDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            TextButton(
                onClick = {},
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Deactivate Portfolio Account", color = BrandAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PrivacyToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
            Text(subtitle, fontSize = 11.sp, color = BrandSecondaryText)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BrandPrimary,
                uncheckedThumbColor = BrandBorder,
                uncheckedTrackColor = Color.White
            )
        )
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 10: NOTIFICATIONS
// --------------------------------------------------------------------------------------
@Composable
fun NotificationPreferencesScreen(
    pushMessages: Boolean,
    onPushMessagesChange: (Boolean) -> Unit,
    pushBookings: Boolean,
    onPushBookingsChange: (Boolean) -> Unit,
    emailNewsletter: Boolean,
    onEmailNewsletterChange: (Boolean) -> Unit,
    smsAlerts: Boolean,
    onSmsAlertsChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BrandDark)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Notification Alerts Channel", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
            }
            TextButton(onClick = onSave) {
                Text("Lock", color = BrandPrimary, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // FCM Connected Status Banner
            val fcmToken by com.example.onboarding.database.FirebaseManager.fcmTokenState.collectAsState()
            val context = androidx.compose.ui.platform.LocalContext.current
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Active",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "FCM Service Active & Registered",
                                color = Color(0xFF1B5E20),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Device connected to Firebase Cloud Messaging gateway for real-time alerts.",
                                color = Color(0xFF2E7D32),
                                fontSize = 9.5.sp,
                                lineHeight = 13.sp
                            )
                        }
                    }
                    if (fcmToken != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFC8E6C9).copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "FCM Token: ${fcmToken?.take(36)}...",
                                fontSize = 8.5.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = Color(0xFF1B5E20),
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                            val channelId = "musiclinkd_fcm_channel"
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                val channel = android.app.NotificationChannel(
                                    channelId,
                                    "FCM Push Alerts",
                                    android.app.NotificationManager.IMPORTANCE_HIGH
                                )
                                notificationManager.createNotificationChannel(channel)
                            }
                            val builder = androidx.core.app.NotificationCompat.Builder(context, channelId)
                                .setSmallIcon(android.R.drawable.ic_dialog_info)
                                .setContentTitle("FCM Test Alert • MusicLinkd")
                                .setContentText("You have 1 new studio booking request from SoundLab Mumbai!")
                                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                                .setAutoCancel(true)
                            notificationManager.notify(8891, builder.build())
                            android.widget.Toast.makeText(context, "Simulated FCM Push Notification dispatched!", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1B5E20)),
                        border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Send, null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Trigger Test FCM Push Alert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text("Direct Sound Scout Notifications", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            PrivacyToggleRow(
                title = "Push: Instant Booking requests",
                subtitle = "Notify me as soon as a client triggers studio dates.",
                checked = pushBookings,
                onCheckedChange = onPushBookingsChange
            )
            PrivacyToggleRow(
                title = "Push: Chat Inbound Messages",
                subtitle = "Get alerted when scouts or label contacts trigger messages.",
                checked = pushMessages,
                onCheckedChange = onPushMessagesChange
            )
            PrivacyToggleRow(
                title = "SMS: Urgent Booking Changes",
                subtitle = "Receive direct text alerts for calendar rescheduling.",
                checked = smsAlerts,
                onCheckedChange = onSmsAlertsChange
            )
            PrivacyToggleRow(
                title = "Email: Weekly Analytics Recap",
                subtitle = "Deliver a traffic overview report directly to inbox.",
                checked = emailNewsletter,
                onCheckedChange = onEmailNewsletterChange
            )
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 11: SUBSCRIPTION
// --------------------------------------------------------------------------------------
@Composable
fun SubscriptionPlanScreen(
    currentPlan: String,
    onSelectPlan: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BrandDark)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("MusicLinkd Subscriptions", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Select Your Professional Tier", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            // FREE TIER CARD
            PlanCard(
                title = "Free Basic",
                price = "₹0 / mo",
                features = listOf("3 Portfolio Projects", "Standard Search indexing", "Direct Messaging"),
                isSelected = currentPlan == "Free",
                onSelect = { onSelectPlan("Free") }
            )

            // PRO TIER CARD (RECOMENDED)
            PlanCard(
                title = "Creator Pro",
                price = "₹1,499 / mo",
                features = listOf("Unlimited Portfolio Tracks", "Advanced Search Boost Priority", "Access Daily Analytics (Unblurred)", "Verified Pro Badge"),
                isSelected = currentPlan == "Pro",
                isRecommended = true,
                onSelect = { onSelectPlan("Pro") }
            )

            // BUSINESS STUDIO TIER CARD
            PlanCard(
                title = "Enterprise Dolby Space",
                price = "₹5,499 / mo",
                features = listOf("List Multiple Rehearsal Rooms & Studios", "0% Booking commissions", "Dedicated Resident Engineers profiles", "Certified Dolby Space Badge"),
                isSelected = currentPlan == "Business",
                onSelect = { onSelectPlan("Business") }
            )
        }
    }
}

@Composable
fun ComingSoonPill(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFFFECEE))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "COMING SOON",
            color = Color(0xFFEA4C89),
            fontWeight = FontWeight.Bold,
            fontSize = 8.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun PlanCard(
    title: String,
    price: String,
    features: List<String>,
    isSelected: Boolean,
    isRecommended: Boolean = false,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, if (isSelected) BrandPrimary else if (isRecommended) BrandAccent else BrandBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
                    if (title != "Free Basic") {
                        ComingSoonPill()
                    }
                }
                if (isRecommended) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BrandAccent)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("POPULAR", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text(price, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandPrimary, modifier = Modifier.padding(vertical = 4.dp))
            
            HorizontalDivider(color = BrandBorder, modifier = Modifier.padding(vertical = 8.dp))

            features.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                    Icon(Icons.Default.Check, null, tint = BrandSuccess, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(feature, fontSize = 11.sp, color = BrandSecondaryText)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSelect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) BrandPrimary else Color.White),
                border = if (isSelected) null else BorderStroke(1.dp, BrandBorder)
            ) {
                Text(
                    text = if (isSelected) "Current Active Plan" else "Select Plan",
                    color = if (isSelected) Color.White else BrandDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 12: BUSINESS PROFILE
// --------------------------------------------------------------------------------------
@Composable
fun BusinessProfileManagementScreen(
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    operatingHours: String,
    onOperatingHoursChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    gst: String,
    onGstChange: (String) -> Unit,
    policies: String,
    onPoliciesChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BrandDark)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Business Profile Settings", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
            }
            TextButton(onClick = onSave) {
                Text("Lock", color = BrandPrimary, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Registered Dolby Studio & Label Info", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            LabeledInputField(label = "Business Studio Name", value = name, onValueChange = onNameChange)
            LabeledInputField(label = "Acoustic Spaces & Gear Description", value = description, onValueChange = onDescriptionChange, isMultiline = true)
            LabeledInputField(label = "Operating Calendar Hours", value = operatingHours, onValueChange = onOperatingHoursChange)
            LabeledInputField(label = "Physical Studio Address", value = address, onValueChange = onAddressChange)
            LabeledInputField(label = "GSTIN Business Identification", value = gst, onValueChange = onGstChange)
            LabeledInputField(label = "Rescheduling & Booking Policies", value = policies, onValueChange = onPoliciesChange)

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Lock Space Registry Details", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 13: PROFILE SHARE
// --------------------------------------------------------------------------------------
@Composable
fun ProfileShareScreen(
    vanityUrl: String,
    onBack: () -> Unit,
    onCopyLink: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BrandDark)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("Share Sound Professional Card", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Large beautiful custom draw vector QR card on Canvas
        Card(
            modifier = Modifier
                .size(280.dp)
                .border(1.dp, BrandBorder, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Drawing aesthetic tech QR design
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val qrSize = size.width
                    // Outer square frame
                    drawRoundRect(
                        color = BrandPrimary,
                        topLeft = Offset(0f, 0f),
                        size = Size(qrSize, qrSize),
                        cornerRadius = CornerRadius(16f),
                        style = Stroke(width = 8f)
                    )

                    // Draw stylized matrix blocks representing high-fidelity QR
                    for (x in 2..8 step 2) {
                        for (y in 2..8 step 2) {
                            if ((x + y) % 3 != 0) {
                                drawRoundRect(
                                    color = if ((x + y) % 2 == 0) BrandAccent else BrandDark,
                                    topLeft = Offset(qrSize * (x / 10f), qrSize * (y / 10f)),
                                    size = Size(qrSize * 0.12f, qrSize * 0.12f),
                                    cornerRadius = CornerRadius(4f)
                                )
                            }
                        }
                    }

                    // Top Left target block
                    drawRoundRect(
                        color = BrandDark,
                        topLeft = Offset(qrSize * 0.1f, qrSize * 0.1f),
                        size = Size(qrSize * 0.25f, qrSize * 0.25f),
                        cornerRadius = CornerRadius(6f)
                    )
                    // Bottom Right target block
                    drawRoundRect(
                        color = BrandDark,
                        topLeft = Offset(qrSize * 0.65f, qrSize * 0.65f),
                        size = Size(qrSize * 0.25f, qrSize * 0.25f),
                        cornerRadius = CornerRadius(6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Copy & Share Profile URL",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = BrandDark
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .border(1.dp, BrandBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = vanityUrl,
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimary,
                    fontSize = 12.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onCopyLink) {
                    Icon(Icons.Default.ContentCopy, null, tint = BrandPrimary)
                }
            }
        }

        Button(
            onClick = onCopyLink,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp),
            shape = RoundedCornerShape(99.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
        ) {
            Text("Copy Profile Link", fontWeight = FontWeight.Bold)
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 14: PUBLIC PROFILE PREVIEW (FLOW 15 - ULTRA PREMIUM CONVERSION ENGINE)
// --------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PublicProfilePreviewScreen(
    displayName: String,
    headline: String,
    bio: String,
    location: String,
    genres: String,
    status: String,
    services: List<MusicianService>,
    projects: List<MusicianProject>,
    onBack: () -> Unit
) {
    val selectedTemplate = remember(headline) {
        when {
            headline.contains("Studio", ignoreCase = true) || headline.contains("Facility", ignoreCase = true) -> "Business"
            headline.contains("Organizer", ignoreCase = true) || headline.contains("Venue", ignoreCase = true) || headline.contains("Event", ignoreCase = true) -> "Venue"
            headline.contains("Label", ignoreCase = true) || headline.contains("Band", ignoreCase = true) || headline.contains("Manager", ignoreCase = true) -> "Band"
            else -> "Talent"
        }
    }

    val context = LocalContext.current
    
    // Core interactivity states
    var isTrackPlaying by remember { mutableStateOf(false) }
    var trackProgress by remember { mutableFloatStateOf(0.35f) }
    var isMasteredChannel by remember { mutableStateOf(true) } // A/B audio comparison slider
    var selectedDate by remember { mutableIntStateOf(15) } // July date selection
    var selectedTimeSlot by remember { mutableStateOf("02:00 PM - 04:00 PM") }
    var userFollowed by remember { mutableStateOf(false) }
    var followerCount by remember { mutableIntStateOf(2480) }
    var selectedPhotoIndex by remember { mutableIntStateOf(0) }
    var activeStagePin by remember { mutableStateOf("Vocal Mic") } // Stage plot selection
    
    // Auto simulated playback timer
    LaunchedEffect(isTrackPlaying) {
        if (isTrackPlaying) {
            while (isTrackPlaying) {
                kotlinx.coroutines.delay(200)
                trackProgress = (trackProgress + 0.01f)
                if (trackProgress >= 1f) {
                    trackProgress = 0f
                    isTrackPlaying = false
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // 1. TOP NAV OVERLAY WITH VISITOR MODE ALERT & SHARING INFO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, BrandBorder)
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = BrandDark)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "PUBLIC VISITOR PREVIEW",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandAccent,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "musiclinkd.co/${displayName.lowercase().replace(" ", "")}",
                                fontSize = 11.sp,
                                color = BrandSecondaryText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(BrandSuccess.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(BrandSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("LIVE CONVERSION", color = BrandSuccess, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // 2. MAIN SCROLLABLE EDITORIAL PREVIEW CONTAINER WITH DEVICE SIMULATION FRAME
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9)) // Beautiful slate backdrop for the devices
            ) {
                val deviceModifier = Modifier
                    .width(360.dp)
                    .fillMaxHeight()
                    .align(Alignment.Center)
                    .padding(vertical = 12.dp)
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(4.dp, BrandDark, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(20.dp))

                Box(
                    modifier = deviceModifier
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 84.dp) // Room for sticky floating button at bottom
                    ) {
                        when (selectedTemplate) {
                            "Talent" -> PremiumTalentProfileLayout(
                                displayName = displayName,
                                headline = headline,
                                bio = bio,
                                location = location,
                                genres = genres,
                                status = status,
                                services = services,
                                projects = projects,
                                userFollowed = userFollowed,
                                followerCount = followerCount,
                                onFollowToggle = {
                                    userFollowed = !userFollowed
                                    followerCount += if (userFollowed) 1 else -1
                                },
                                isTrackPlaying = isTrackPlaying,
                                onPlayToggle = { isTrackPlaying = !isTrackPlaying },
                                trackProgress = trackProgress,
                                isMasteredChannel = isMasteredChannel,
                                onChannelToggle = { isMasteredChannel = it },
                                onInstantBook = {
                                    android.widget.Toast.makeText(context, "Directing to secure checkout payment gateway...", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            )
                            "Business" -> BusinessProfileLayout(
                                displayName = displayName,
                                location = location,
                                status = status,
                                services = services,
                                selectedDate = selectedDate,
                                onDateSelect = { selectedDate = it },
                                selectedTimeSlot = selectedTimeSlot,
                                onTimeSlotSelect = { selectedTimeSlot = it },
                                onInstantBook = {
                                    android.widget.Toast.makeText(context, "Directing to secure checkout payment gateway...", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            )
                            "Band" -> BandProfileLayout(
                                displayName = displayName,
                                location = location,
                                genres = genres,
                                activeStagePin = activeStagePin,
                                onStagePinSelect = { activeStagePin = it },
                                onInstantBook = {
                                    android.widget.Toast.makeText(context, "Directing to secure checkout payment gateway...", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            )
                            "Venue" -> VenueProfileLayout(
                                displayName = displayName,
                                location = location,
                                selectedPhotoIndex = selectedPhotoIndex,
                                onPhotoSelect = { selectedPhotoIndex = it },
                                onInstantBook = {
                                    android.widget.Toast.makeText(context, "Directing to secure checkout payment gateway...", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        // BOTTOM EDITORIAL FOOTER (Section 16)
                        Spacer(modifier = Modifier.height(48.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BrandDark)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "MusicLinkd",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "CREATOR PORTFOLIO NETWORK",
                                fontSize = 9.sp,
                                color = BrandAccent,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Generate your own gorgeous, audio-first public microsite in under 60 seconds with MusicLinkd Pro.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onBack,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandAccent),
                                shape = RoundedCornerShape(99.dp)
                            ) {
                                Text("Back to Portfolio Workspace", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                text = "Privacy · Terms · Custom CSS · NFC Card Integration",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    }

                    // STICKY FLOATING HIRE BUTTON (Linear/Apple Music Style)
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(14.dp)
                            .shadow(16.dp, RoundedCornerShape(99.dp)),
                        colors = CardDefaults.cardColors(containerColor = BrandDark),
                        shape = RoundedCornerShape(99.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    android.widget.Toast.makeText(context, "Directing to secure checkout payment gateway...", android.widget.Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(BrandSuccess)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Hire $displayName",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = "Secure Booking",
                                color = BrandAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ======================================================================================
// 🧑‍🎤 PREMIUM EDITORIAL CREATIVE PORTFOLIO (Linear & Apple Music Styled)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PremiumTalentProfileLayout(
    displayName: String,
    headline: String,
    bio: String,
    location: String,
    genres: String,
    status: String,
    services: List<MusicianService>,
    projects: List<MusicianProject>,
    userFollowed: Boolean,
    followerCount: Int,
    onFollowToggle: () -> Unit,
    isTrackPlaying: Boolean,
    onPlayToggle: () -> Unit,
    trackProgress: Float,
    isMasteredChannel: Boolean,
    onChannelToggle: (Boolean) -> Unit,
    onInstantBook: () -> Unit
) {
    val context = LocalContext.current
    val isEnginePlaying by AudioPlayerManager.isPlayingState.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            AudioPlayerManager.stop()
        }
    }

    // Local state variables for portfolio sections
    var isBioExpanded by remember { mutableStateOf(false) }
    var selectedGalleryCategory by remember { mutableStateOf("Studio") }
    var approvedCreditIndex by remember { mutableStateOf<Int?>(null) }
    var selectedServiceTier by remember { mutableStateOf("Standard") }
    var activeTrackIndex by remember { mutableIntStateOf(0) }
    var contactFeedbackMsg by remember { mutableStateOf("") }

    val trackList = listOf(
        "Midnight Synthwave Echoes" to "3:45",
        "Acoustic Nostalgia (Unplugged Live)" to "4:12",
        "Cyberpunk Basslines & Dry stems" to "2:50"
    )

    // Curated Fallbacks for professional visuals
    val coverImageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800&auto=format&fit=crop&q=80"

    val displayProjects = if (projects.isEmpty()) {
        listOf(
            MusicianProject(title = "Midnight Synthwave LP", type = "Audio", status = "Published", views = 1840, date = "May 2026"),
            MusicianProject(title = "Abbey Road Live Sessions", type = "Production", status = "Published", views = 920, date = "Apr 2026"),
            MusicianProject(title = "Cyberpunk Echoes (Official Video)", type = "Video", status = "Published", views = 3120, date = "Jan 2026")
        )
    } else {
        projects
    }

    val displayServices = if (services.isEmpty()) {
        listOf(
            MusicianService(name = "Vocal Session Tracking", pricing = "$150", rateType = "Hourly", description = "High-fidelity professional lead vocal tracking using Neumann U87 Ai. Includes 2 revision runs."),
            MusicianService(name = "Pitch Correction & Tuning", pricing = "$250", rateType = "Per Track", description = "Surgical pitch correction, timing alignment, and harmony layer generation. Delivered in 48kHz WAV."),
            MusicianService(name = "Full Vocal Arrangement", pricing = "$600", rateType = "Per Track", description = "Complete custom vocal stacking, backing harmonies, ad-libs, and pre-mixed stems.")
        )
    } else {
        services
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        
        // ================= SECTION 1: HERO & IDENTITY =================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            // High-quality scenic background
            AsyncImage(
                model = coverImageUrl,
                contentDescription = "Cover Hero Backdrop",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Premium dark subtle overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
            )

            // Live Booking Pill Badge overlay
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(BrandSuccess.copy(alpha = 0.2f))
                    .border(1.dp, BrandSuccess.copy(alpha = 0.5f), RoundedCornerShape(99.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(BrandSuccess)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AVAILABLE FOR HIRE",
                        color = BrandSuccess,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Pro Membership Pill Badge overlay
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(BrandAccent.copy(alpha = 0.25f))
                    .border(1.dp, BrandAccent, RoundedCornerShape(99.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "PRO ARTIST",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            // Text Info aligned inside Hero bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = displayName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Profile Checkmark",
                        tint = BrandAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = headline,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // ================= SECTION 2: IDENTITY DETAILS =================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location & Genre
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location Pin Icon",
                            tint = BrandPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandDark
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = genres,
                        fontSize = 11.sp,
                        color = BrandSecondaryText,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Follow Action
                Button(
                    onClick = onFollowToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (userFollowed) BrandBorder else BrandPrimary
                    ),
                    shape = RoundedCornerShape(99.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = if (userFollowed) "Following" else "+ Follow",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (userFollowed) BrandDark else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BIO STATEMENT (COLLAPSIBLE)
            Text(
                text = bio,
                fontSize = 13.sp,
                color = BrandDark.copy(alpha = 0.85f),
                lineHeight = 20.sp,
                maxLines = if (isBioExpanded) 10 else 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isBioExpanded) "Read Less" else "Read Full Bio",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BrandPrimary,
                modifier = Modifier.clickable { isBioExpanded = !isBioExpanded }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ================= SECTION 3: DARK AUDIO SHOWCASE =================
            Text(
                text = "FEATURED AUDIO MASTER",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandDark.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Sleek Dark Audio Player Card (Apple Music Style)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = BrandDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play/Pause button
                        IconButton(
                            onClick = {
                                if (isEnginePlaying) {
                                    AudioPlayerManager.pause()
                                } else {
                                    onPlayToggle()
                                    AudioPlayerManager.play(context, "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", trackList[activeTrackIndex].first)
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(BrandPrimary)
                        ) {
                            Icon(
                                imageVector = if (isTrackPlaying || isEnginePlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isTrackPlaying || isEnginePlaying) "Pause" else "Play Track",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = trackList[activeTrackIndex].first,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Producer Mix · ${trackList[activeTrackIndex].second}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Progress Slider
                    LinearProgressIndicator(
                        progress = { trackProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape),
                        color = BrandAccent,
                        trackColor = Color.White.copy(alpha = 0.15f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Alternating Track Queue Selector
                    Text(
                        text = "PLAYLIST QUEUE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White.copy(alpha = 0.4f),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    trackList.forEachIndexed { idx, (trackName, duration) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (activeTrackIndex == idx) Color.White.copy(alpha = 0.08f)
                                    else Color.Transparent
                                )
                                .clickable { activeTrackIndex = idx }
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (activeTrackIndex == idx && (isTrackPlaying || isEnginePlaying)) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (activeTrackIndex == idx) BrandAccent else Color.White.copy(alpha = 0.4f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = trackName,
                                    color = if (activeTrackIndex == idx) Color.White else Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    fontWeight = if (activeTrackIndex == idx) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.widthIn(max = 200.dp)
                                )
                            }
                            Text(
                                text = duration,
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ================= SECTION 4: PROJECTS =================
            Text(
                text = "PORTFOLIO PROJECTS",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandDark.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                displayProjects.forEach { project ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrandBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BrandPrimary.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (project.type) {
                                        "Audio" -> Icons.Default.Album
                                        "Video" -> Icons.Default.VideoCall
                                        else -> Icons.Default.MusicNote
                                    },
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = project.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = BrandDark
                                )
                                Text(
                                    text = "${project.type} · Released ${project.date}",
                                    fontSize = 11.sp,
                                    color = BrandSecondaryText
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BrandBorder.copy(alpha = 0.5f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${project.views} VIEWS",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandDark.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ================= SECTION 5: CREDITS TIMELINE =================
            Text(
                text = "VERIFIED DISCOGRAPHY CREDITS",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandDark.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Discography credits timeline
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "Lead Vocalist on 'Cyber Horizon' with Neon Valley (2026)",
                    "Co-Producer on 'Acoustic Soul' with Tara Vance (2025)",
                    "Vocal Synthesizer Designer for 'Dark Nebula' OST (2025)"
                ).forEachIndexed { idx, credit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { approvedCreditIndex = idx }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (approvedCreditIndex == idx) Icons.Default.CheckCircle else Icons.Default.Verified,
                            contentDescription = "Verified Credit",
                            tint = if (approvedCreditIndex == idx) BrandSuccess else BrandAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = credit,
                            fontSize = 12.5.sp,
                            color = BrandDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ================= SECTION 6: SERVICES & SECURED BOOKING =================
            Text(
                text = "SERVICES & SECURED BOOKING RATES",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandDark.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Tier Tab Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Standard", "Premium", "VIP Ultimate").forEach { tier ->
                    val isSelected = selectedServiceTier == tier
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BrandDark else BrandBorder.copy(alpha = 0.3f))
                            .clickable { selectedServiceTier = tier }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tier.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) Color.White else BrandDark.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display Active Service details
            val activeService = when (selectedServiceTier) {
                "Premium" -> displayServices.getOrElse(1) { displayServices[0] }
                "VIP Ultimate" -> displayServices.getOrElse(2) { displayServices[0] }
                else -> displayServices[0]
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeService.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BrandDark
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(BrandPrimary.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${activeService.pricing} / ${activeService.rateType}",
                                color = BrandPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = activeService.description,
                        fontSize = 12.sp,
                        color = BrandSecondaryText,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onInstantBook,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Book Service with Secured Payment", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ================= SECTION 7: CREATIVE GALLERY =================
            Text(
                text = "STUDIO & SESSION GALLERY",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandDark.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Studio", "Concerts", "Gear setup", "Press").forEach { cat ->
                    val isSelected = selectedGalleryCategory == cat
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(99.dp))
                            .background(
                                if (isSelected) BrandPrimary.copy(alpha = 0.1f)
                                else Color.Transparent
                            )
                            .clickable { selectedGalleryCategory = cat }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) BrandPrimary else BrandDark.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selected Gallery Photo Card
            val activeGalleryUrl = when (selectedGalleryCategory) {
                "Studio" -> "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=500&auto=format&fit=crop&q=80"
                "Concerts" -> "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&auto=format&fit=crop&q=80"
                "Gear setup" -> "https://images.unsplash.com/photo-1598653222000-6b7b7a552625?w=500&auto=format&fit=crop&q=80"
                else -> "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=500&auto=format&fit=crop&q=80"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .border(1.dp, BrandBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            ) {
                AsyncImage(
                    model = activeGalleryUrl,
                    contentDescription = "$selectedGalleryCategory Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ================= SECTION 8: CLIENT REVIEWS =================
            Text(
                text = "VERIFIED CLIENT REVIEWS",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandDark.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            listOf(
                "Rahul M., SoundStage Labs" to "Absolute vocal powerhouse! Perfect delivery times and flawless backing vocal arrangements. Highly recommended.",
                "Sophie L., Pulse Sync Media" to "Vedanshu's studio recordings require zero surgical tuning. Deliverables are pristine and perfectly cataloged. Will hire again!"
            ).forEach { (client, reviewText) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, BrandBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = client,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BrandDark
                            )
                            Text(
                                text = "★★★★★",
                                color = BrandAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "\"$reviewText\"",
                            fontSize = 12.sp,
                            color = BrandSecondaryText,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ================= SECTION 9: SECURE CONTACT CHANNELS =================
            Text(
                text = "SECURE CHANNELS & IDENTITY",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandDark.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Your booking communications, session agreements, and payments are protected inside secure, verified booking agreements.",
                        fontSize = 12.sp,
                        color = BrandSecondaryText,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE8F5E9))
                                .clickable { contactFeedbackMsg = "Inquiry encrypted over direct channel!" }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Direct Chat", color = Color(0xFF2E7D32), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFECEFF1))
                                .clickable { contactFeedbackMsg = "Email address copied to clipboard!" }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Copy Email", color = BrandDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    if (contactFeedbackMsg.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = contactFeedbackMsg,
                            color = BrandSuccess,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ======================================================================================
// TEMPLATE COMPOSABLES (REUSABLE LAYOUT PRESET// 🧑‍🎤 TEMPLATE 1: SOLO TALENT LAYOUT
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TalentProfileLayout(
    displayName: String,
    headline: String,
    bio: String,
    location: String,
    genres: String,
    status: String,
    services: List<MusicianService>,
    projects: List<MusicianProject>,
    userFollowed: Boolean,
    followerCount: Int,
    onFollowToggle: () -> Unit,
    isTrackPlaying: Boolean,
    onPlayToggle: () -> Unit,
    trackProgress: Float,
    isMasteredChannel: Boolean,
    onChannelToggle: (Boolean) -> Unit,
    onInstantBook: () -> Unit
) {
    val context = LocalContext.current
    val isEnginePlaying by AudioPlayerManager.isPlayingState.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            AudioPlayerManager.stop()
        }
    }

    // Local section-specific interactivity states
    var isBioExpanded by remember { mutableStateOf(false) }
    var selectedGalleryCategory by remember { mutableStateOf("Studio") }
    var approvedCreditIndex by remember { mutableStateOf<Int?>(null) }
    var selectedServiceTier by remember { mutableStateOf("Standard") }
    var activeTrackIndex by remember { mutableIntStateOf(0) }
    var userRatingHovered by remember { mutableIntStateOf(0) }
    var contactFeedbackMsg by remember { mutableStateOf("") }

    val trackList = listOf(
        "Midnight Synthwave Echoes" to "3:45",
        "Acoustic Nostalgia (Unplugged Live)" to "4:12",
        "Cyberpunk Basslines & Dry stems" to "2:50"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        
        // ================= SECTION 1: HERO =================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2E1A47),
                            Color(0xFF0F081D),
                            BrandDark
                        )
                    )
                )
        ) {
            // Hero Decorative Vinyl Art
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .offset(x = 180.dp, y = (-20).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Availability Badge & Status Overlay
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(BrandSuccess.copy(alpha = 0.15f))
                            .border(1.dp, BrandSuccess.copy(alpha = 0.4f), RoundedCornerShape(99.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(BrandSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AVAILABLE FOR SESSION BOOKINGS", color = BrandSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }

                    // Pro Tier tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(BrandAccent.copy(alpha = 0.2f))
                            .border(1.dp, BrandAccent, RoundedCornerShape(99.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("PRO MEMBER", color = BrandAccent, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary)
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(displayName.take(1), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = displayName,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Artist",
                                tint = BrandAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "Vocal Architect & Music Producer",
                            color = BrandAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Mumbai, India · Speaks English, Hindi, Punjabi",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // ================= STATS & PRIMARY ACTIONS =================
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandBorder.copy(alpha = 0.25f))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$followerCount", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = BrandDark)
                    Text("Followers", fontSize = 10.sp, color = BrandSecondaryText)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("4.9★", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = BrandAccent)
                    Text("32 Client Reviews", fontSize = 10.sp, color = BrandSecondaryText)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("98%", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = BrandSuccess)
                    Text("Response Rate", fontSize = 10.sp, color = BrandSecondaryText)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("24", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = BrandPrimary)
                    Text("Co-Creds Verified", fontSize = 10.sp, color = BrandSecondaryText)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary CTAs (Hire / Follow / Message)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onInstantBook,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(99.dp),
                    modifier = Modifier.weight(1.5f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hire Artist Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Button(
                    onClick = onFollowToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (userFollowed) BrandBorder else BrandDark
                    ),
                    shape = RoundedCornerShape(99.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (userFollowed) "✓ Following" else "+ Follow",
                        fontWeight = FontWeight.Bold,
                        color = if (userFollowed) BrandDark else Color.White,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = { contactFeedbackMsg = "Message channel established via WhatsApp Secured API" },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BrandBorder.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Mail, null, tint = BrandDark, modifier = Modifier.size(20.dp))
                }
            }

            if (contactFeedbackMsg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandAccent.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(contactFeedbackMsg, color = BrandAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ================= SECTION 2: BIOGRAPHY & GENRES =================
            Spacer(modifier = Modifier.height(28.dp))
            Text("Editorial Bio & Creative Vibe", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
            
            Text(
                text = if (isBioExpanded) {
                    bio + " Armed with Neve analog desks, tube microphones, and soundscape synthesizers, they specialize in composing organic orchestral themes matched with contemporary electronic drums. Known across Bollywood and independent circles for precision tuning, modern sound architecture, and an impeccable ear for dynamic spatial audio mastering."
                } else {
                    bio.take(120) + "..."
                },
                fontSize = 13.sp,
                color = BrandSecondaryText,
                lineHeight = 19.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = if (isBioExpanded) "Show Less" else "Read full backstory",
                color = BrandPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { isBioExpanded = !isBioExpanded }
            )

            Spacer(modifier = Modifier.height(12.dp))
            // Genres list
            Text("Featured Spheres:", fontSize = 11.sp, color = BrandDark.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Cinematic", "Synthwave", "Neo-Classical", "Indie Pop", "Dolby Atmos Mixing", "EDM Stem Prep").forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BrandAccent.copy(alpha = 0.08f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(tag, color = BrandAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ================= SECTION 3: AUDIO SHOWCASE & RECENT TRACKS =================
            Spacer(modifier = Modifier.height(28.dp))
            Text("Audio Discovery Player", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = BrandDark),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // spinning disc animation representation
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .border(2.dp, BrandAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = trackList[activeTrackIndex].first,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Text(
                                text = "High-Fidelity Stereo · ${trackList[activeTrackIndex].second}",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 10.5.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                if (isEnginePlaying) {
                                    AudioPlayerManager.pause()
                                } else {
                                    val trackUrl = when (activeTrackIndex) {
                                        0 -> "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
                                        1 -> "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
                                        else -> "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"
                                    }
                                    AudioPlayerManager.play(context, trackUrl, trackList[activeTrackIndex].first)
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BrandAccent)
                        ) {
                            Icon(
                                imageVector = if (isEnginePlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Waveform seeker
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        for (i in 1..28) {
                            val active = (i / 28f) <= trackProgress
                            val height = if (isEnginePlaying) {
                                remember(isEnginePlaying, i) { (8..26).random() }.dp
                            } else {
                                ((i % 6) * 3 + 8).dp
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(height)
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(if (active) BrandAccent else Color.White.copy(alpha = 0.25f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Playlist Tracks toggle list
                    Text("Playlist Queue:", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    trackList.forEachIndexed { idx, (title, dur) ->
                        val isCurrent = idx == activeTrackIndex
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) Color.White.copy(alpha = 0.08f) else Color.Transparent)
                                .clickable {
                                    activeTrackIndex = idx
                                    val trackUrl = when (idx) {
                                        0 -> "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
                                        1 -> "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
                                        else -> "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"
                                    }
                                    AudioPlayerManager.play(context, trackUrl, title)
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isCurrent && isEnginePlaying) "▶" else "${idx + 1}.",
                                    color = if (isCurrent) BrandAccent else Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(18.dp)
                                )
                                Text(
                                    text = title,
                                    color = if (isCurrent) Color.White else Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            Text(dur, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }
                    }
                }
            }

            // ================= SECTION 4: RELEASES & PROJECTS =================
            Spacer(modifier = Modifier.height(28.dp))
            Text("Featured Studio Projects", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
            Spacer(modifier = Modifier.height(10.dp))

            projects.forEach { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💿", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(project.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                            Text("Released: ${project.date} · Indie Electro Label", fontSize = 11.sp, color = BrandSecondaryText)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(BrandPrimary.copy(alpha = 0.08f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Play Single", color = BrandPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ================= SECTION 5: PROFESSIONAL CREDITS =================
            Spacer(modifier = Modifier.height(28.dp))
            Text("Professional Work Timeline", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
            Text("Collaborator credits linked and verified on MusicLinkd", fontSize = 11.sp, color = BrandSecondaryText)
            Spacer(modifier = Modifier.height(12.dp))

            listOf(
                "Bollywood Synth Scrape Theme" to "Lead Vocal Producer · 2026",
                "Indie Rock Album - Sunset" to "Co-Arranger & Dolby Atmos Mastering · 2025",
                "Netflix Original Sci-Fi Sequence" to "Acoustic SFX & Stem Mixer · 2024"
            ).forEachIndexed { idx, (title, credits) ->
                val approved = approvedCreditIndex == idx
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(1.dp, if (approved) BrandSuccess else BrandBorder, RoundedCornerShape(12.dp))
                        .clickable { approvedCreditIndex = idx }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                        Text(credits, fontSize = 11.sp, color = BrandSecondaryText)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(if (approved) BrandSuccess.copy(alpha = 0.15f) else BrandBorder.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (approved) "✓ Approved" else "Verify",
                            color = if (approved) BrandSuccess else BrandDark,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ================= SECTION 6: SERVICE TIERS =================
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Standard Offerings & Rates", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
                // Tier selector
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(BrandBorder.copy(alpha = 0.4f))
                        .padding(2.dp)
                ) {
                    listOf("Standard", "Premium", "Pro VIP").forEach { tier ->
                        val isSel = selectedServiceTier == tier
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(if (isSel) BrandPrimary else Color.Transparent)
                                .clickable { selectedServiceTier = tier }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tier,
                                color = if (isSel) Color.White else BrandDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val displayedService = when (selectedServiceTier) {
                "Premium" -> MusicianService(name = "Deluxe Custom Production", pricing = "₹14,999", rateType = "per Track", description = "Includes 3 acoustic track layers, custom synth design, drum programming, and 3 rounds of review.")
                "Pro VIP" -> MusicianService(name = "Full Album Architecture", pricing = "₹49,999", rateType = "flat rate", description = "Premium end-to-end production of 5 tracks with Dolby Atmos spatial mastering, direct phone consultation, and priority schedule reservation.")
                else -> services.firstOrNull() ?: MusicianService(name = "Session Vocal Recording", pricing = "₹4,999", rateType = "per Track", description = "Dry vocal stems recorded on Neumann U87 rig with premium vocal compression.")
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, BrandPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayedService.name,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = BrandDark
                        )
                        Text(
                            text = displayedService.pricing,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandAccent,
                            fontSize = 15.sp
                        )
                    }
                    Text(
                        text = "Rate Type: ${displayedService.rateType}",
                        fontSize = 10.sp,
                        color = BrandPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = displayedService.description,
                        fontSize = 12.sp,
                        color = BrandSecondaryText,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Button(
                        onClick = onInstantBook,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandDark),
                        shape = RoundedCornerShape(99.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Order ${displayedService.name} (Secured Pay)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ================= SECTION 7: PORTFOLIO GALLERY =================
            Spacer(modifier = Modifier.height(28.dp))
            Text("Visual Studio Gallery", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
            
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Studio", "Live Concerts", "Gear setup", "Press/Awards").forEach { cat ->
                    val isSel = selectedGalleryCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(if (isSel) BrandAccent else BrandBorder.copy(alpha = 0.4f))
                            .clickable { selectedGalleryCategory = cat }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(cat, color = if (isSel) Color.White else BrandDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Display grid based on selected category
            val galleryMockText = when (selectedGalleryCategory) {
                "Live Concerts" -> "🎤 Stage setup at NH7 Weekender Mumbai with 20K audience\n🎸 Intimate acoustic unplugged session at Hard Rock Cafe"
                "Gear setup" -> "🎹 Moog Sub 37 analog synthesizer core workstation\n🎛️ SSL stereo bus compressor & Neumann recording rig"
                "Press/Awards" -> "🏆 Nominated for Artist of the Year at India Music Awards\n📰 Featured inside Rolling Stone India June Editorial cover"
                else -> "🎙️ Vocal session recording using vintage Neumann U87 tube preamp\n🎚️ Arranging multi-tracks inside Dolby Atmos audio production suite"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = BrandBackground),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Selected Category: $selectedGalleryCategory", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = galleryMockText,
                        fontSize = 12.sp,
                        color = BrandDark,
                        lineHeight = 18.sp
                    )
                }
            }

            // ================= SECTION 8: VIDEOS & CLIPS =================
            Spacer(modifier = Modifier.height(28.dp))
            Text("Featured Reels & Videos", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    "Studio Session BTS Vlog" to "🎬 YouTube · 45K views",
                    "Live Vocal Improv in Mumbai" to "📱 Instagram Reel · 120K views",
                    "Dolby Atmos Mix Breakdown" to "🖥️ Masterclass · 12K views"
                ).forEach { (title, stat) ->
                    item {
                        Card(
                            modifier = Modifier
                                .width(220.dp)
                                .border(1.dp, BrandBorder, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Brush.radialGradient(colors = listOf(BrandAccent, BrandDark))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(32.dp))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = BrandDark)
                                Text(stat, fontSize = 9.5.sp, color = BrandSecondaryText)
                            }
                        }
                    }
                }
            }

            // ================= SECTION 9: TECHNICAL GEAR & DAW =================
            Spacer(modifier = Modifier.height(28.dp))
            Text("Technical Rig & Audio Setup", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    listOf(
                        "DAW Workstation" to "Logic Pro X, Pro Tools Ultimate, Ableton Live 11 Suite",
                        "Microphone Locker" to "Vintage Neumann U87 Ai, Shure SM7B, AKG C414 XLS Pair",
                        "Preamp / Interface" to "Universal Audio Apollo x8p, Neve 1073 Dual Channel preamp",
                        "Monitoring Suite" to "Genelec 8040B Studio Monitors, Sennheiser HD600 Reference cans"
                    ).forEach { (cat, detail) ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(text = "• $cat: ", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = BrandPrimary)
                            Text(text = detail, fontSize = 11.5.sp, color = BrandSecondaryText)
                        }
                    }
                }
            }

            // ================= SECTION 10: REVIEWS & REPUTATION =================
            Spacer(modifier = Modifier.height(28.dp))
            Text("Verified Client Reviews", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
            
            // Interaction to rate
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tap stars to rate artist:", fontSize = 10.sp, color = BrandSecondaryText)
                Spacer(modifier = Modifier.width(6.dp))
                Row {
                    for (i in 1..5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (i <= userRatingHovered) BrandAccent else BrandBorder,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { userRatingHovered = i }
                        )
                    }
                }
                if (userRatingHovered > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("You gave ${userRatingHovered}★", color = BrandAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            listOf(
                "James Miller (Universal Records)" to "The raw vocals Vedanshu recorded are exceptionally clean. The Kemper profile work was masterfully done. Saved us days of EQ.",
                "Sarah Connor (Synergy Games)" to "Incredible cinematic arrangements on tight deadlines. The contract release process was flawless. Highly recommended!"
            ).forEach { (client, feedback) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, BrandBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = BrandBackground),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(BrandPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(client.take(1), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(client, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandDark)
                            }
                            Row {
                                repeat(5) {
                                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB000), modifier = Modifier.size(11.dp))
                                }
                            }
                        }
                        Text(
                            "\"$feedback\"",
                            fontSize = 11.sp,
                            color = BrandSecondaryText,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }

            // ================= SECTION 11: VERIFIED COLLABORATORS =================
            Spacer(modifier = Modifier.height(28.dp))
            Text("Verified Collaborators Network", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Kabir Sen" to "Acoustic Guitarist",
                    "Aanya Rao" to "Synthesizer Lead",
                    "Rohan Mehra" to "Recording Engineer",
                    "Pooja Nair" to "Bollywood Composer"
                ).forEach { (name, role) ->
                    item {
                        Card(
                            modifier = Modifier
                                .width(130.dp)
                                .border(1.dp, BrandBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(BrandPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(name.take(1), fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandDark, maxLines = 1)
                                Text(role, fontSize = 9.sp, color = BrandSecondaryText, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // ================= SECTION 12: ACHIEVEMENTS & MILESTONES =================
            Spacer(modifier = Modifier.height(28.dp))
            Text("Achievements & Digital Stats", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    "🎧 1.5M Streams" to "Spotify / Apple Music",
                    "🏆 Award Nominee" to "India Music Awards",
                    "🌍 Top 1% Creator" to "MusicLinkd Premium"
                ).forEach { (title, subt) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandBorder.copy(alpha = 0.2f))
                            .padding(10.dp)
                    ) {
                        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 11.5.sp, color = BrandDark)
                        Text(subt, fontSize = 9.sp, color = BrandSecondaryText)
                    }
                }
            }

            // ================= SECTION 13: AVAILABILITY & TOUR STATUS =================
            Spacer(modifier = Modifier.height(28.dp))
            Text("Availability Status & Schedule", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BrandSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Fully Open for Bookings", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandSuccess)
                        }
                        Text("Response Time: ~18 mins", fontSize = 10.sp, color = BrandSecondaryText)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Accepting remote stem work, live gig inquiries, and sound engineering sessions in Mumbai and Pune. Booking calendar is secure with integrated milestone releases.",
                        fontSize = 11.5.sp,
                        color = BrandSecondaryText,
                        lineHeight = 16.sp
                    )
                }
            }

            // ================= SECTION 14: SECURED CONTACT ANCHOR =================
            Spacer(modifier = Modifier.height(28.dp))
            Text("Secure Direct Connect Channels", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
            Text("Select a channel to query the artist directly", fontSize = 11.sp, color = BrandSecondaryText)
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "🟢 WhatsApp secured" to "Inquire via official chat integration",
                    "📩 Official Email" to "Send dynamic business brief",
                    "📞 Verified phone line" to "Direct call access via token"
                ).forEach { (title, description) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, BrandBorder, RoundedCornerShape(12.dp))
                            .clickable { contactFeedbackMsg = "Connected to $title channel securely." }
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = BrandDark)
                            Text(description, fontSize = 9.sp, color = BrandSecondaryText)
                        }
                    }
                }
            }

            // ================= SECTION 15: RELATED ARTISTS FOR DISCOVERY =================
            Spacer(modifier = Modifier.height(28.dp))
            Text("You might also like", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
            Text("Other highly-rated creators nearby", fontSize = 11.sp, color = BrandSecondaryText)
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    "Vikram Roy" to "Violinist · Mumbai · 5.0★",
                    "Nisha Sen" to "Sitarist · Pune · 4.9★",
                    "Dhruv Kapoor" to "Synth Bassist · Mumbai · 4.8★"
                ).forEach { (name, info) ->
                    item {
                        Card(
                            modifier = Modifier
                                .width(180.dp)
                                .border(1.dp, BrandBorder, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = BrandBackground)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandDark)
                                Text(info, fontSize = 10.sp, color = BrandSecondaryText)
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(99.dp))
                                        .background(BrandPrimary)
                                        .clickable { contactFeedbackMsg = "Navigating to related profile: $name" }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("View Profile", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 🏢 TEMPLATE 2: BUSINESS / STUDIO LAYOUT
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BusinessProfileLayout(
    displayName: String,
    location: String,
    status: String,
    services: List<MusicianService>,
    selectedDate: Int,
    onDateSelect: (Int) -> Unit,
    selectedTimeSlot: String,
    onTimeSlotSelect: (String) -> Unit,
    onInstantBook: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF0F172A), BrandDark)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏢", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Abbey Road Atmos Room", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, null, tint = BrandAccent, modifier = Modifier.size(16.dp))
                        }
                        Text("Established facility · 4.9 ★ (110 reviews)", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            // About Studio
            Text("Premium Recording Facility & Outboard Racks", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDark)
            Text(
                "Fully soundproofed professional suites featuring direct Neumann recording arrays, Dolby Atmos 7.1.4 configurations, and premium analog SSL mixing decks.",
                fontSize = 11.5.sp,
                color = BrandSecondaryText,
                lineHeight = 16.sp,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            // DOLBY ATMOS ROOM SPEC CONFIGS
            Spacer(modifier = Modifier.height(16.dp))
            Text("Studio Room Specifications", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
            Spacer(modifier = Modifier.height(8.dp))

            listOf(
                "Studio A: Dolby Atmos Mastering" to "Equipped with Dolby 7.1.4, SSL Solid State Logic board, 96-channel direct preamps.",
                "Studio B: Production Suite" to "Featuring Neve 1073 Outboard Rack, Kemper Guitar Profile, and vintage analog keyboards."
            ).forEach { (room, desc) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, BrandBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔊", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(room, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandDark)
                        }
                        Text(desc, fontSize = 10.5.sp, color = BrandSecondaryText, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            // EQUIPMENT LEDGER
            Spacer(modifier = Modifier.height(16.dp))
            Text("Equipment Inventory Available", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
            
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("SSL 4000E Console", "Neumann U87 Ai", "Neve 1073 Pre", "Dolby Atmos 7.1.4", "Kemper Profiler", "Pro Tools HDX").forEach { gear ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BrandBorder.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(gear, fontSize = 9.5.sp, color = BrandDark, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // INTERACTIVE BOOKING CALENDAR PREVIEW
            Spacer(modifier = Modifier.height(20.dp))
            Text("Select Date & Book Session Slot", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
            Spacer(modifier = Modifier.height(8.dp))

            // Custom Calendar row (12-18 July 2026)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (date in 12..18) {
                    val isSelected = selectedDate == date
                    Box(
                        modifier = Modifier
                            .size(width = 42.dp, height = 50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) BrandAccent else BrandBorder.copy(alpha = 0.3f))
                            .clickable { onDateSelect(date) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = when (date) {
                                    12 -> "S"
                                    13 -> "M"
                                    14 -> "T"
                                    15 -> "W"
                                    16 -> "T"
                                    17 -> "F"
                                    18 -> "S"
                                    else -> "W"
                                },
                                fontSize = 9.sp,
                                color = if (isSelected) Color.White else BrandSecondaryText,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$date",
                                fontSize = 13.sp,
                                color = if (isSelected) Color.White else BrandDark,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time Slots
            listOf("10:00 AM - 02:00 PM", "02:00 PM - 06:00 PM", "06:00 PM - 10:00 PM").forEach { slot ->
                val isSelected = selectedTimeSlot == slot
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, if (isSelected) BrandPrimary else BrandBorder, RoundedCornerShape(8.dp))
                        .clickable { onTimeSlotSelect(slot) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) BrandPrimary.copy(alpha = 0.05f) else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                null,
                                tint = if (isSelected) BrandPrimary else BrandSecondaryText,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(slot, fontSize = 11.5.sp, color = BrandDark, fontWeight = FontWeight.Bold)
                        }
                        Text("$100 / hr", fontSize = 11.sp, color = BrandAccent, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onInstantBook,
                colors = ButtonDefaults.buttonColors(containerColor = BrandDark),
                shape = RoundedCornerShape(99.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lock Slot for July $selectedDate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 🎸 TEMPLATE 3: BAND / CREW LAYOUT
@Composable
fun BandProfileLayout(
    displayName: String,
    location: String,
    genres: String,
    activeStagePin: String,
    onStagePinSelect: (String) -> Unit,
    onInstantBook: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF3B0764), BrandDark)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎸", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("The Midnight Decibels", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, null, tint = BrandAccent, modifier = Modifier.size(16.dp))
                        }
                        Text("Synth Rock Ensemble · London, UK", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            // ENSEMBLE LINEUP CAROUSEL
            Text("Band Members & Performing Crew", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Sarah (Lead Vocals)" to "🎤",
                    "Vedanshu (Keytar / Synth)" to "🎹",
                    "Alex (Lead Guitar)" to "🎸",
                    "Marcus (Drums / Beats)" to "🥁"
                ).forEach { (member, emoji) ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, BrandBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                member.substringBefore(" "),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                member.substringAfter("(").replace(")", ""),
                                fontSize = 8.sp,
                                color = BrandSecondaryText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // TECHNICAL RIDER & STAGE PLOT
            Spacer(modifier = Modifier.height(20.dp))
            Text("Technical Stage Plot Setup Map", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
            Text("Our stage rig specification setup grid mapping vocal mics, direct boxes, monitors, and guitar feeds.", fontSize = 11.sp, color = BrandSecondaryText)
            Spacer(modifier = Modifier.height(8.dp))

            // Graphical Stage Plot map
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandDark)
                    .padding(10.dp)
            ) {
                // Renders stage outline grid
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Stage border line
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.15f),
                        topLeft = Offset(5.dp.toPx(), 5.dp.toPx()),
                        size = Size(w - 10.dp.toPx(), h - 10.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("BACKSTAGE / AMPLIFIERS (WALL)", color = Color.White.copy(alpha = 0.3f), fontSize = 8.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Guitar Amp", "Drum Kit (DI)", "Synth Mix").forEach { pin ->
                            val isSelected = activeStagePin == pin
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) BrandAccent else Color.White.copy(alpha = 0.12f))
                                    .clickable { onStagePinSelect(pin) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(pin, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val isSelected = activeStagePin == "Vocal Mic"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) BrandAccent else Color.White.copy(alpha = 0.12f))
                                .clickable { onStagePinSelect("Vocal Mic") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("🎤 Center Vocal Monitor", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text("FRONT OF HOUSE (STAGE LIP)", color = Color.White.copy(alpha = 0.3f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandBorder, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = BrandBackground)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Technical Rider Spec: center vocal monitor requires high-pass filter. Guitar amp utilizes direct cabinet box bypass feed.",
                        fontSize = 9.5.sp,
                        color = BrandSecondaryText,
                        lineHeight = 13.sp
                    )
                }
            }

            // UPCOMING CONCERTS
            Spacer(modifier = Modifier.height(20.dp))
            Text("Upcoming Concerts & Gigs Schedule", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
            Spacer(modifier = Modifier.height(8.dp))

            listOf(
                "Aug 15" to "O2 Academy Islington (London)",
                "Sep 02" to "The Electric Ballroom (Camden)",
                "Oct 24" to "Supersonic Paris (France)"
            ).forEach { (date, venue) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, BrandBorder, RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(date, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = BrandAccent)
                            Text(venue, fontSize = 10.5.sp, color = BrandDark, fontWeight = FontWeight.Medium)
                        }
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Tickets", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// 🏟️ TEMPLATE 4: VENUE / EVENT LAYOUT
@Composable
fun VenueProfileLayout(
    displayName: String,
    location: String,
    selectedPhotoIndex: Int,
    onPhotoSelect: (Int) -> Unit,
    onInstantBook: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF042F1A), BrandDark)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏟️", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("The Echo Concert Arena", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, null, tint = BrandAccent, modifier = Modifier.size(16.dp))
                        }
                        Text("Capacity: 500 Standing · California, US", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            // Venue Features
            Text("Premium Live Acoustics & Lighting Spec", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
            Text("The primary concert hall is acoustically treated with premium wall traps and integrated L-Acoustics surround audio arrays.", fontSize = 11.5.sp, color = BrandSecondaryText, lineHeight = 16.sp)

            Spacer(modifier = Modifier.height(12.dp))

            // Capacity ledgers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    "Standing Limit" to "500 Fans",
                    "Acoustics" to "L-Acoustics K2",
                    "Lighting Grid" to "GrandMA3 DMX"
                ).forEach { (label, value) ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, BrandBorder, RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = BrandBackground),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(label, fontSize = 9.sp, color = BrandSecondaryText)
                            Text(value, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = BrandDark, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }

            // PHOTO GALLERY SLIDER
            Spacer(modifier = Modifier.height(20.dp))
            Text("Venue Gallery & Interior Shots", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray)
            ) {
                // Renders mock visual photo corresponding to selection
                val backgroundBrush = when (selectedPhotoIndex) {
                    0 -> Brush.sweepGradient(colors = listOf(Color(0xFF1E1B4B), Color(0xFF4C1D95)))
                    1 -> Brush.radialGradient(colors = listOf(Color(0xFF311042), Color(0xFF0F172A)))
                    else -> Brush.verticalGradient(colors = listOf(Color(0xFF042F1A), Color(0xFF181A22)))
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (selectedPhotoIndex) {
                            0 -> "🎤 Active Concert Stage Deck [Render A]"
                            1 -> "🍸 Premium VIP Lounge & Bar Barricade [Render B]"
                            else -> "⚡ Backstage Dressing Room & Technical Power Box"
                        },
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Stage Rig" to 0,
                    "VIP Lounge" to 1,
                    "Green Room" to 2
                ).forEach { (label, idx) ->
                    val isSelected = selectedPhotoIndex == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) BrandAccent else BrandBorder.copy(alpha = 0.3f))
                            .border(1.dp, if (isSelected) BrandAccent else BrandBorder, RoundedCornerShape(6.dp))
                            .clickable { onPhotoSelect(idx) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            color = if (isSelected) Color.White else BrandDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // DYNAMIC EVENTS FEED
            Spacer(modifier = Modifier.height(20.dp))
            Text("Upcoming Concert Bookings", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
            Spacer(modifier = Modifier.height(8.dp))

            listOf(
                "Electronic Midnight Blast" to "14 Tickets Remaining",
                "Indie Showcase Acoustic Night" to "Sold Out (200 Attended)"
            ).forEach { (event, tickets) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, BrandBorder, RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(event, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandDark)
                            Text(tickets, fontSize = 10.sp, color = if (tickets.contains("Remaining")) BrandAccent else BrandSecondaryText)
                        }
                        Icon(Icons.Default.ArrowForward, null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

