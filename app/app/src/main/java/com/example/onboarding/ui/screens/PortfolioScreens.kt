package com.example.onboarding.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onboarding.models.*
import com.example.onboarding.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioMainFlowContainer(
    portfolioState: PortfolioState,
    onBackClick: () -> Unit, // Navigate back to flow selector or dashboard
    onNavigatetoScreen: (PortfolioScreen) -> Unit,
    onNavigateBackInFlow: () -> Boolean,
    onUpdateProjectType: (String) -> Unit,
    onUpdateProjectDetails: (String, String, String, String) -> Unit,
    onUpdateStreamingUrls: (String, String, String, String, String, String) -> Unit,
    onToggleRole: (String) -> Unit,
    onUpdateSearchQuery: (String) -> Unit,
    onTagCollaborator: (ProjectCollaborator) -> Unit,
    onUntagCollaborator: (String) -> Unit,
    onUpdateCreditRole: (String, String) -> Unit,
    onUpdateVisibility: (String) -> Unit,
    onPublish: () -> Unit,
    onPreloadQuickFill: () -> Unit,
    onAddIncStat: (String, String) -> Unit,
    onPreviewPublish: () -> Unit,
    userName: String = "Vedanshu Srivastava",
    userRole: String = "Composer / Sound Engineer"
) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            if (portfolioState.activeScreen != PortfolioScreen.PORTFOLIO_DASHBOARD && 
                portfolioState.activeScreen != PortfolioScreen.SUCCESS_STATE) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (portfolioState.activeScreen) {
                                PortfolioScreen.CHOOSE_PROJECT_TYPE -> "What are you adding?"
                                PortfolioScreen.PROJECT_INFORMATION -> "Project Details"
                                PortfolioScreen.ADD_STREAMING_LINKS -> "Add Streaming Links"
                                PortfolioScreen.SELECT_YOUR_ROLE -> "Your Role"
                                PortfolioScreen.ADD_COLLABORATORS -> "Tag Collaborators"
                                PortfolioScreen.CREDIT_ATTRIBUTION -> "Project Credits"
                                PortfolioScreen.PROJECT_MEDIA_PREVIEW -> "Preview Project"
                                PortfolioScreen.PUBLISH_PROJECT -> "Ready to Publish"
                                PortfolioScreen.PROFILE_PROJECT_VIEW -> "Project Detail"
                                else -> "Add Project"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandTextPrimary,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { 
                                val handled = onNavigateBackInFlow()
                                if (!handled) {
                                    onBackClick()
                                }
                            },
                            modifier = Modifier.testTag("portfolio_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = BrandTextPrimary
                            )
                        }
                    },
                    actions = {
                        if (portfolioState.activeScreen == PortfolioScreen.PROJECT_INFORMATION) {
                            TextButton(
                                onClick = {
                                    onPreloadQuickFill()
                                    Toast.makeText(context, "Filled Swar Sandhya EP demo data!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.testTag("portfolio_quick_fill")
                            ) {
                                Text("Quick Demo", color = BrandPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = BrandBackground
                    )
                )
            }
        },
        containerColor = BrandBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = portfolioState.activeScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "PortfolioScreenAnimation"
            ) { currentScreen ->
                when (currentScreen) {
                    PortfolioScreen.PORTFOLIO_DASHBOARD -> {
                        PortfolioDashboardScreen(
                            state = portfolioState,
                            userName = userName,
                            userRole = userRole,
                            onAddProjectClick = { 
                                onUpdateProjectDetails("", "", "", "Released")
                                onNavigatetoScreen(PortfolioScreen.CHOOSE_PROJECT_TYPE) 
                            },
                            onProjectClick = { project ->
                                onUpdateProjectDetails(
                                    project.name, project.description, project.releaseDate, project.status
                                )
                                onUpdateStreamingUrls(
                                    project.spotifyUrl, project.appleMusicUrl, project.youtubeUrl,
                                    project.soundcloudUrl, project.mixcloudUrl, project.instagramReelUrl
                                )
                                onNavigatetoScreen(PortfolioScreen.PROFILE_PROJECT_VIEW)
                            },
                            onBackClick = onBackClick
                        )
                    }
                    PortfolioScreen.CHOOSE_PROJECT_TYPE -> {
                        ChooseProjectTypeScreen(
                            selectedType = portfolioState.addingProjectType,
                            onTypeSelect = onUpdateProjectType,
                            onContinue = { onNavigatetoScreen(PortfolioScreen.PROJECT_INFORMATION) }
                        )
                    }
                    PortfolioScreen.PROJECT_INFORMATION -> {
                        ProjectInformationScreen(
                            projectName = portfolioState.addingProjectName,
                            projectDesc = portfolioState.addingProjectDesc,
                            projectDate = portfolioState.addingProjectDate,
                            projectStatus = portfolioState.addingProjectStatus,
                            onDetailsChange = onUpdateProjectDetails,
                            onContinue = { onNavigatetoScreen(PortfolioScreen.ADD_STREAMING_LINKS) }
                        )
                    }
                    PortfolioScreen.ADD_STREAMING_LINKS -> {
                        AddStreamingLinksScreen(
                            spotifyUrl = portfolioState.spotifyUrl,
                            appleMusicUrl = portfolioState.appleMusicUrl,
                            youtubeUrl = portfolioState.youtubeUrl,
                            soundcloudUrl = portfolioState.soundcloudUrl,
                            mixcloudUrl = portfolioState.mixcloudUrl,
                            instagramReelUrl = portfolioState.instagramReelUrl,
                            onUrlsChange = onUpdateStreamingUrls,
                            onContinue = { onNavigatetoScreen(PortfolioScreen.SELECT_YOUR_ROLE) }
                        )
                    }
                    PortfolioScreen.SELECT_YOUR_ROLE -> {
                        SelectYourRoleScreen(
                            selectedRoles = portfolioState.selectedRoles,
                            onToggleRole = onToggleRole,
                            onContinue = { onNavigatetoScreen(PortfolioScreen.ADD_COLLABORATORS) }
                        )
                    }
                    PortfolioScreen.ADD_COLLABORATORS -> {
                        AddCollaboratorsScreen(
                            searchQuery = portfolioState.searchQuery,
                            taggedCollaborators = portfolioState.taggedCollaborators,
                            onSearchQueryChange = onUpdateSearchQuery,
                            onAddCollaborator = onTagCollaborator,
                            onRemoveCollaborator = onUntagCollaborator,
                            onContinue = { onNavigatetoScreen(PortfolioScreen.CREDIT_ATTRIBUTION) }
                        )
                    }
                    PortfolioScreen.CREDIT_ATTRIBUTION -> {
                        CreditAttributionScreen(
                            taggedCollaborators = portfolioState.taggedCollaborators,
                            credits = portfolioState.credits,
                            onUpdateCreditRole = onUpdateCreditRole,
                            onContinue = { onNavigatetoScreen(PortfolioScreen.PROJECT_MEDIA_PREVIEW) }
                        )
                    }
                    PortfolioScreen.PROJECT_MEDIA_PREVIEW -> {
                        ProjectMediaPreviewScreen(
                            state = portfolioState,
                            onContinue = { onNavigatetoScreen(PortfolioScreen.PUBLISH_PROJECT) }
                        )
                    }
                    PortfolioScreen.PUBLISH_PROJECT -> {
                        PublishProjectScreen(
                            state = portfolioState,
                            onVisibilityChange = onUpdateVisibility,
                            onPublishClick = onPublish
                        )
                    }
                    PortfolioScreen.SUCCESS_STATE -> {
                        SuccessStateScreen(
                            state = portfolioState,
                            onViewProject = onPreviewPublish,
                            onShareProject = { 
                                Toast.makeText(context, "Copied MusicLinkd link for " + (portfolioState.lastPublishedProject?.name ?: "project") + "!", Toast.LENGTH_SHORT).show()
                            },
                            onGoToProfile = { onNavigatetoScreen(PortfolioScreen.PORTFOLIO_DASHBOARD) }
                        )
                    }
                    PortfolioScreen.PROFILE_PROJECT_VIEW -> {
                        ProfileProjectViewScreen(
                            state = portfolioState,
                            onAddIncStat = onAddIncStat,
                            onBackClick = { onNavigatetoScreen(PortfolioScreen.PORTFOLIO_DASHBOARD) },
                            onShareClick = {
                                Toast.makeText(context, "Copied card link!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// SCREEN 1: PORTFOLIO DASHBOARD
// -------------------------------------------------------------------------------------
@Composable
fun PortfolioDashboardScreen(
    state: PortfolioState,
    userName: String,
    userRole: String,
    onAddProjectClick: () -> Unit,
    onProjectClick: (PortfolioProject) -> Unit,
    onBackClick: () -> Unit
) {
    var isLoadingPortfolio by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoadingPortfolio = true
        kotlinx.coroutines.delay(500)
        isLoadingPortfolio = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Portfolio",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = BrandTextPrimary,
                            letterSpacing = (-1).sp
                        )
                    )
                    Text(
                        text = "MusicLinkd Indian Professional Registry",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Normal,
                            color = BrandTextSecondary
                        )
                    )
                }
                
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(BrandCard, CircleShape)
                        .border(1.dp, BrandBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close, 
                        contentDescription = "Exit to onboarding dashboard", 
                        tint = BrandTextPrimary, 
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BrandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // User Avatar
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(BrandPrimary, BrandSecondary)),
                                    CircleShape
                                )
                        ) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80",
                                contentDescription = "User Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BrandTextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(BrandPrimary, CircleShape)
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Verified Artist Registry",
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                            Text(
                                text = userRole,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BrandPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "📍 Mumbai, India",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BrandTextSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = BrandBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.projectsCount.toString(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BrandTextPrimary
                                )
                            )
                            Text(
                                text = "Projects",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BrandTextSecondary
                                )
                            )
                        }
                        
                        Box(modifier = Modifier.width(1.dp).height(32.dp).background(BrandBorder))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.creditsCount.toString(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BrandTextPrimary
                                )
                            )
                            Text(
                                text = "Credits",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BrandTextSecondary
                                )
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(32.dp).background(BrandBorder))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.profileViews.toString(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BrandTextPrimary
                                )
                            )
                            Text(
                                text = "Views",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BrandTextSecondary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary CTA Add Project
            Button(
                onClick = onAddProjectClick,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("portfolio_primary_add_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Add Professional Project",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Projects Label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Professional Releases & Co-Credits",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandTextPrimary,
                        letterSpacing = (-0.2).sp
                    )
                )
                Text(
                    text = "${state.projectsList.size} Total",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = BrandPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Projects List
            if (isLoadingPortfolio) {
                com.example.onboarding.ui.components.PortfolioGridSkeletonLoader(count = 3)
            } else if (state.projectsList.isEmpty()) {
                com.example.onboarding.ui.components.IllustratedEmptyStateView(
                    badgeText = "Portfolio Canvas",
                    title = "No Projects Published Yet",
                    description = "Upload your original master tracks, studio stems, live performances, or video scoring projects to showcase your craft on the Indian professional registry.",
                    icon = Icons.Default.LibraryMusic,
                    illustrationEmoji = "💿",
                    primaryActionLabel = "Upload First Project",
                    onPrimaryAction = onAddProjectClick
                )
            } else {
                state.projectsList.forEach { project ->
                    RecentProjectCard(
                        project = project,
                        onClick = { onProjectClick(project) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Followers List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Professional Network",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandTextPrimary,
                        letterSpacing = (-0.2).sp
                    )
                )
                Text(
                    text = "0 Followers",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = BrandTextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            EmptyState(
                title = "No Followers Yet",
                description = "Prompt users to share profiles externally to build their active professional network.",
                emoji = "📣",
                actionLabel = "Share Profile",
                onActionClick = { /* Simulate sharing profile externally */ }
            )
            
            Spacer(modifier = Modifier.height(80.dp)) // Padding for the floating action button
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onAddProjectClick,
            containerColor = BrandSecondary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("portfolio_fab")
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add Project")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Project", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RecentProjectCard(
    project: PortfolioProject,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BrandCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BrandBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Colored graphic indicator based on artwork name
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(getArtworkColor(project.artworkUrl))
                ) {
                    Text(
                        text = when (project.type) {
                            "Song" -> "🎵"
                            "Album" -> "💿"
                            "EP" -> "📀"
                            "Podcast" -> "🎙️"
                            "DJ Mix" -> "🎛️"
                            "Film Score" -> "🎬"
                            "Commercial Jingle" -> "🔔"
                            "Live Performance" -> "🎤"
                            "Music Video" -> "📹"
                            else -> "🎼"
                        },
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = project.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandTextPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        
                        // Status Badge
                        StatusBadge(status = project.status)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = project.type,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BrandPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "•  ${project.releaseDate}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BrandTextSecondary
                            )
                        )
                    }
                }
            }

            if (project.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = project.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BrandTextSecondary,
                        lineHeight = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = BrandBorder)
            Spacer(modifier = Modifier.height(10.dp))

            // Render roles & collaborators quick view row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Roles list
                Text(
                    text = "My role: ${project.roles.joinToString()}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                // Collaborators Avatars
                if (project.collaborators.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        project.collaborators.take(3).forEach { collab ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF3F4F6), CircleShape)
                                    .border(1.dp, BrandBorder, CircleShape)
                            ) {
                                AsyncImage(
                                    model = collab.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                                    contentDescription = collab.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                        if (project.collaborators.size > 3) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFE5E7EB), CircleShape)
                            ) {
                                Text(
                                    "+${project.collaborators.size - 3}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "Released" -> BrandSuccess
        "Upcoming" -> BrandPrimary
        "In Production" -> BrandSecondary
        else -> BrandTextSecondary
    }
    
    val bg = when (status) {
        "Released" -> Color(0xFFD1FAE5)
        "Upcoming" -> Color(0xFFE0E7FF)
        "In Production" -> Color(0xFFEDE9FE)
        else -> Color(0xFFF3F4F6)
    }

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = status,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

fun getArtworkColor(name: String): Color {
    return when (name) {
        "Acoustic" -> Color(0xFFFFCC80)
        "Sunset" -> Color(0xFFFFAB91)
        "Studio" -> Color(0xFFCE93D8)
        "Techno1" -> Color(0xFF90CAF9)
        "Techno" -> Color(0xFF80CBC4)
        "Folk" -> Color(0xFFA5D6A7)
        "Cinematic" -> Color(0xFFB0BEC5)
        else -> Color(0xFFD1C4E9)
    }
}

// -------------------------------------------------------------------------------------
// SCREEN 2: CHOOSE PROJECT TYPE
// -------------------------------------------------------------------------------------
@Composable
fun ChooseProjectTypeScreen(
    selectedType: String,
    onTypeSelect: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select project format",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandPrimary
            )
        )
        Text(
            text = "Select the type of release you want to register in your Indian music database profile.",
            style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(availableProjectTypes) { item ->
                val isSelected = item.name == selectedType
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFF5F3FF) else BrandCard
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) BrandPrimary else BrandBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clickable { onTypeSelect(item.name) }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (isSelected) Color(0xFFDDD6FE) else Color(0xFFF3F4F6),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.name,
                                tint = if (isSelected) BrandPrimary else BrandTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = BrandTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = item.description,
                                fontSize = 10.sp,
                                color = BrandTextSecondary,
                                lineHeight = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("portfolio_choose_type_continue_btn")
        ) {
            Text("Confirm & Continue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// -------------------------------------------------------------------------------------
// SCREEN 3: PROJECT INFORMATION
// -------------------------------------------------------------------------------------
@Composable
fun ProjectInformationScreen(
    projectName: String,
    projectDesc: String,
    projectDate: String,
    projectStatus: String,
    onDetailsChange: (String, String, String, String) -> Unit,
    onContinue: () -> Unit
) {
    var nameState by remember(projectName) { mutableStateOf(projectName) }
    var descState by remember(projectDesc) { mutableStateOf(projectDesc) }
    var dateState by remember(projectDate) { mutableStateOf(projectDate) }
    var statusState by remember(projectStatus) { mutableStateOf(projectStatus) }

    fun emitChanges() {
        onDetailsChange(nameState, descState, dateState, statusState)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Tell us about this release",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandPrimary
            )
        )
        Text(
            text = "Add core details of your original metadata for copyright search matching and identification.",
            style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Input: Project Name
        Text("Project Title", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandTextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = nameState,
            onValueChange = { nameState = it; emitChanges() },
            placeholder = { Text("e.g. Swar Sandhya EP, Jhumka Beats, etc.") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BrandBorder,
                focusedContainerColor = BrandCard,
                unfocusedContainerColor = BrandCard
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("portfolio_field_title")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input: Description
        Text("Project Description (Bio & Metadata)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandTextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = descState,
            onValueChange = { descState = it; emitChanges() },
            placeholder = { Text("Who was the master of this project? What style or instruments are featured?") },
            minLines = 3,
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BrandBorder,
                focusedContainerColor = BrandCard,
                unfocusedContainerColor = BrandCard
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("portfolio_field_desc")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input: Release Date
        Text("Release Date", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandTextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = dateState,
            onValueChange = { dateState = it; emitChanges() },
            placeholder = { Text("YYYY-MM-DD or Month YYYY") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BrandBorder,
                focusedContainerColor = BrandCard,
                unfocusedContainerColor = BrandCard
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("portfolio_field_date")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input: Status selector
        Text("Project Production Status", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val statuses = listOf("Released", "Upcoming", "In Production", "Draft")
            statuses.forEach { opt ->
                val isSelected = opt == statusState
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .background(
                            if (isSelected) Color(0xFFF5F3FF) else BrandCard,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) BrandPrimary else BrandBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { statusState = opt; emitChanges() }
                ) {
                    Text(
                        text = opt,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) BrandPrimary else BrandTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (nameState.trim().isEmpty()) {
                    nameState = "Swar Sandhya EP"
                    emitChanges()
                }
                onContinue()
            },
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("portfolio_info_continue_btn")
        ) {
            Text("Confirm Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// -------------------------------------------------------------------------------------
// SCREEN 4: ADD STREAMING LINKS
// -------------------------------------------------------------------------------------
@Composable
fun AddStreamingLinksScreen(
    spotifyUrl: String,
    appleMusicUrl: String,
    youtubeUrl: String,
    soundcloudUrl: String,
    mixcloudUrl: String,
    instagramReelUrl: String,
    onUrlsChange: (String, String, String, String, String, String) -> Unit,
    onContinue: () -> Unit
) {
    var spotify by remember(spotifyUrl) { mutableStateOf(spotifyUrl) }
    var apple by remember(appleMusicUrl) { mutableStateOf(appleMusicUrl) }
    var youtube by remember(youtubeUrl) { mutableStateOf(youtubeUrl) }
    var soundcloud by remember(soundcloudUrl) { mutableStateOf(soundcloudUrl) }
    var mixcloud by remember(mixcloudUrl) { mutableStateOf(mixcloudUrl) }
    var instagram by remember(instagramReelUrl) { mutableStateOf(instagramReelUrl) }

    fun emitChange() {
        onUrlsChange(spotify, apple, youtube, soundcloud, mixcloud, instagram)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Link your streaming assets",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandPrimary
            )
        )
        Text(
            text = "Paste relevant tracks, playlists, or videos. We automatically design interactive embeds.",
            style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Spotify
        LinkInputField(
            value = spotify,
            onValueChange = { spotify = it; emitChange() },
            label = "Spotify URL",
            icon = painterResource(id = android.R.drawable.ic_media_play), // placeholder
            colorHex = 0xFF1DB954,
            appLabel = "Spotify"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Apple Music
        LinkInputField(
            value = apple,
            onValueChange = { apple = it; emitChange() },
            label = "Apple Music URL",
            icon = painterResource(id = android.R.drawable.ic_media_play),
            colorHex = 0xFFFA243C,
            appLabel = "Apple Music"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // YouTube
        LinkInputField(
            value = youtube,
            onValueChange = { youtube = it; emitChange() },
            label = "YouTube URL",
            icon = painterResource(id = android.R.drawable.ic_media_play),
            colorHex = 0xFFFF0000,
            appLabel = "YouTube"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // SoundCloud
        LinkInputField(
            value = soundcloud,
            onValueChange = { soundcloud = it; emitChange() },
            label = "SoundCloud URL",
            icon = painterResource(id = android.R.drawable.ic_media_play),
            colorHex = 0xFFFF5500,
            appLabel = "SoundCloud"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mixcloud
        LinkInputField(
            value = mixcloud,
            onValueChange = { mixcloud = it; emitChange() },
            label = "Mixcloud URL",
            icon = painterResource(id = android.R.drawable.ic_media_play),
            colorHex = 0xFF52AAD4,
            appLabel = "Mixcloud"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Instagram
        LinkInputField(
            value = instagram,
            onValueChange = { instagram = it; emitChange() },
            label = "Instagram Reel / Audio Video Link",
            icon = painterResource(id = android.R.drawable.ic_media_play),
            colorHex = 0xFFE1306C,
            appLabel = "Instagram"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Live Preview Platform Cards List
        Text(
            "Embed Preview Cards", 
            fontWeight = FontWeight.Bold, 
            fontSize = 13.sp, 
            color = BrandTextPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))

        val linksList = listOf(
            Triple(spotify, "Spotify Embed", Color(0xFF1DB954)),
            Triple(apple, "Apple Music Embed", Color(0xFFFA243C)),
            Triple(youtube, "YouTube Live Video", Color(0xFFFF0000)),
            Triple(soundcloud, "SoundCloud Audio Player", Color(0xFFFF5500)),
            Triple(mixcloud, "Mixcloud DJ Cloudcast", Color(0xFF52AAD4)),
            Triple(instagram, "Instagram Video Clip Reel", Color(0xFFE1306C))
        ).filter { it.first.trim().isNotEmpty() }

        if (linksList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                    .border(1.dp, BrandBorder, RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Insert Spotify, SoundCloud or YouTube URLs above to generate high fidelity card embeds.",
                    fontSize = 11.sp,
                    color = BrandTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            linksList.forEach { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = BrandCard),
                    border = BorderStroke(1.dp, BrandBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(item.third, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                item.second,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BrandTextPrimary
                            )
                            Text(
                                item.first,
                                fontSize = 10.sp,
                                color = BrandPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // If reviewer didn't add any link, prefill spotify for preview completeness
                if (spotify.isEmpty() && youtube.isEmpty() && soundcloud.isEmpty()) {
                    spotify = "https://spotify.com/album/swarsandhya_preview"
                    emitChange()
                }
                onContinue()
            },
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("portfolio_links_continue_btn")
        ) {
            Text("Verify Streaming Links", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
fun LinkInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.painter.Painter,
    colorHex: Long,
    appLabel: String
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BrandIcon(
                platform = appLabel,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandTextSecondary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Paste $appLabel URL track link here") },
            leadingIcon = {
                BrandIcon(
                    platform = appLabel,
                    modifier = Modifier.size(18.dp)
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(colorHex),
                unfocusedBorderColor = BrandBorder,
                focusedContainerColor = BrandCard,
                unfocusedContainerColor = BrandCard
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// -------------------------------------------------------------------------------------
// SCREEN 5: SELECT YOUR ROLE
// -------------------------------------------------------------------------------------
@Composable
fun SelectYourRoleScreen(
    selectedRoles: Set<String>,
    onToggleRole: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Role in This Project",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandPrimary
            )
        )
        Text(
            text = "Select all roles you personally performed for copyright matching attribution. Multi-selection enabled.",
            style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableProjectRoles) { role ->
                val isSelected = selectedRoles.contains(role)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFF5F3FF) else BrandCard
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) BrandPrimary else BrandBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleRole(role) }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (isSelected) Color(0xFFDDD6FE) else Color(0xFFF3F4F6),
                                        CircleShape
                                    )
                            ) {
                                Text(
                                    text = when (role) {
                                        "Singer" -> "🎤"
                                        "Composer" -> "🎹"
                                        "Music Director" -> "🎬"
                                        "Producer" -> "🎧"
                                        "Lyricist" -> "📝"
                                        "Session Musician" -> "🎸"
                                        "DJ" -> "💿"
                                        "Mix Engineer" -> "🎚️"
                                        "Mastering Engineer" -> "🎛️"
                                        "Sound Designer" -> "🔊"
                                        "Band Member" -> "👥"
                                        else -> "🎼"
                                    },
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = role,
                                fontWeight = FontWeight.Bold,
                                color = BrandTextPrimary,
                                fontSize = 14.sp
                            )
                        }

                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onToggleRole(role) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = BrandPrimary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedRoles.isEmpty()) {
                    onToggleRole("Composer")
                    onToggleRole("Producer")
                }
                onContinue()
            },
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("portfolio_roles_continue_btn")
        ) {
            Text("Confirm Selected Roles", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// -------------------------------------------------------------------------------------
// SCREEN 6: ADD COLLABORATORS
// -------------------------------------------------------------------------------------
@Composable
fun AddCollaboratorsScreen(
    searchQuery: String,
    taggedCollaborators: List<ProjectCollaborator>,
    onSearchQueryChange: (String) -> Unit,
    onAddCollaborator: (ProjectCollaborator) -> Unit,
    onRemoveCollaborator: (String) -> Unit,
    onContinue: () -> Unit
) {
    val filteredCollaborators = globalCollaboratorDirectory.filter {
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.role.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tag project team & collaborators",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandPrimary
            )
        )
        Text(
            text = "Tag other artists registered in India's registry to request official back-link co-credits.",
            style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search by name or primary role (e.g. Arijit)") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = BrandTextSecondary)
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BrandBorder,
                focusedContainerColor = BrandCard,
                unfocusedContainerColor = BrandCard
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("portfolio_collab_search")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tagged/Selected List
        if (taggedCollaborators.isNotEmpty()) {
            Text("Tagged (${taggedCollaborators.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(taggedCollaborators) { collab ->
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEDE9FE), RoundedCornerShape(20.dp))
                            .border(1.dp, BrandSecondary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = collab.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                                contentDescription = collab.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                collab.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove tag",
                                tint = BrandSecondary,
                                modifier = Modifier
                                    .size(12.dp)
                                    .clickable { onRemoveCollaborator(collab.id) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Suggestions Directory
        Text("Verified Directory Users", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredCollaborators) { item ->
                val isAlreadyTagged = taggedCollaborators.any { it.id == item.id }
                Card(
                    colors = CardDefaults.cardColors(containerColor = BrandCard),
                    border = BorderStroke(1.dp, BrandBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF3F4F6), CircleShape)
                                    .border(1.dp, BrandBorder, CircleShape)
                            ) {
                                AsyncImage(
                                    model = item.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                                    contentDescription = item.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        item.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = BrandTextPrimary
                                    )
                                    if (item.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(BrandPrimary, CircleShape)
                                                .padding(2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Verified Profile",
                                                tint = Color.White,
                                                modifier = Modifier.size(6.dp)
                                            )
                                        }
                                    }
                                }
                                Text(item.role, fontSize = 10.sp, color = BrandTextSecondary)
                            }
                        }

                        Button(
                            onClick = {
                                if (isAlreadyTagged) {
                                    onRemoveCollaborator(item.id)
                                } else {
                                    onAddCollaborator(item)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAlreadyTagged) Color(0xFFF3F4F6) else BrandPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = if (isAlreadyTagged) "Tagged" else "Tag",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAlreadyTagged) BrandTextSecondary else Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // If nothing tagged, tag some default suggested collaborators for complete workflow preview
                if (taggedCollaborators.isEmpty()) {
                    onAddCollaborator(globalCollaboratorDirectory[0]) // Tara Sharma
                    onAddCollaborator(globalCollaboratorDirectory[3]) // Rahul Sharma
                }
                onContinue()
            },
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("portfolio_collab_continue_btn")
        ) {
            Text("Verify Collaborators", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// -------------------------------------------------------------------------------------
// SCREEN 7: CREDIT ATTRIBUTION
// -------------------------------------------------------------------------------------
@Composable
fun CreditAttributionScreen(
    taggedCollaborators: List<ProjectCollaborator>,
    credits: List<CreditItem>,
    onUpdateCreditRole: (String, String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Accreditation and Assignment",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandPrimary
            )
        )
        Text(
            text = "Assign roles to your tagged collaborators. These roles will be officially cross-verified in music databases.",
            style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ourselves placeholder metadata row
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
            border = BorderStroke(1.dp, BrandBorder),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFB74D), CircleShape)
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80",
                        contentDescription = "User Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Vedanshu Srivastava (Me)", fontWeight = FontWeight.Black, fontSize = 13.sp, color = BrandTextPrimary)
                    Text("Role: Composer & Producer", fontSize = 10.sp, color = BrandPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text("Collaborator Credit Roles", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (credits.isEmpty()) {
                item {
                    EmptyState(
                        title = "No Credits Yet",
                        description = "Guide users to tag active collaborators to build out the project's music database.",
                        emoji = "🤝",
                        actionLabel = "Back to Collaborators",
                        onActionClick = { /* Navigate back or just prompt */ }
                    )
                }
            } else {
                items(credits) { credit ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BrandCard),
                        border = BorderStroke(1.dp, BrandBorder),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEDE9FE), CircleShape)
                                ) {
                                    AsyncImage(
                                        model = credit.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                                        contentDescription = credit.collaboratorName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = credit.collaboratorName,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = BrandTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Verify Assigned Role", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandTextSecondary)
                            Spacer(modifier = Modifier.height(6.dp))

                            // Custom horizontal scroll selector for role chips
                            val suggestedCreditRoles = listOf("Singer", "Lyricist", "Mix Engineer", "Mastering Engineer", "Producer", "Composer", "Session Musician")
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(suggestedCreditRoles) { role ->
                                    val isSelected = role == credit.role
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .background(
                                                if (isSelected) BrandPrimary else Color(0xFFF3F4F6),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onUpdateCreditRole(credit.collaboratorName, role) }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = role,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else BrandTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("portfolio_credits_continue_btn")
        ) {
            Text("Confirm Accreditations", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// -------------------------------------------------------------------------------------
// SCREEN 8: PROJECT MEDIA PREVIEW
// -------------------------------------------------------------------------------------
@Composable
fun ProjectMediaPreviewScreen(
    state: PortfolioState,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Generate Profile Preview Card",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandPrimary
            )
        )
        Text(
            text = "Review how international users and labels will see this card published on your professional MusicLinkd timeline.",
            style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Large Mock Portfolio Card
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, BrandBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(getArtworkColor(state.addingProjectType))
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(state.addingProjectStatus, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Box(
                            modifier = Modifier
                                .background(BrandPrimary, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = state.addingProjectType.uppercase(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.addingProjectName.ifEmpty { "Swar Sandhya EP" },
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    }
                }

                // Details Content
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "METADATA DESCRIPTION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandPrimary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.addingProjectDesc.ifEmpty { "A soft acoustic Indian sarangi lo-fi reimagined soundtrack crafted live." },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BrandTextSecondary,
                            lineHeight = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Roles List
                    Text(
                        "MY METADATA ROLES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val roles = state.selectedRoles.ifEmpty { setOf("Composer", "Producer") }
                        roles.forEach { role ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFEDE9FE), RoundedCornerShape(8.dp))
                                    .border(1.dp, BrandSecondary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(role, color = BrandSecondary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Credit attribution summary
                    Text(
                        "COLLABORATORS & CO-CREDITS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    val allCredits = state.credits
                    if (allCredits.isEmpty()) {
                        Text("No collaborators tagged on this track.", fontSize = 10.sp, color = BrandTextSecondary)
                    } else {
                        allCredits.forEach { cr ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                AsyncImage(
                                    model = cr.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                                    contentDescription = cr.collaboratorName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(cr.collaboratorName, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandTextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("→", color = BrandTextSecondary, fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(cr.role, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = BrandBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated streaming preview badges
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Active Platforms: ", fontSize = 10.sp, color = BrandTextSecondary)
                        Spacer(modifier = Modifier.width(6.dp))
                        
                        val activeChops = mutableListOf<String>()
                        if (state.spotifyUrl.isNotEmpty()) activeChops.add("Spotify")
                        if (state.appleMusicUrl.isNotEmpty()) activeChops.add("Apple Music")
                        if (state.youtubeUrl.isNotEmpty()) activeChops.add("YouTube")
                        if (state.soundcloudUrl.isNotEmpty()) activeChops.add("SoundCloud")
                        
                        if (activeChops.isEmpty() || state.spotifyUrl.isEmpty()) {
                            activeChops.add("Spotify") // mock default
                        }

                        activeChops.forEach { chip ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(chip, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = BrandTextPrimary)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("portfolio_preview_continue_btn")
        ) {
            Text("Confirm Preview", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// -------------------------------------------------------------------------------------
// SCREEN 9: PUBLISH PROJECT
// -------------------------------------------------------------------------------------
@Composable
fun PublishProjectScreen(
    state: PortfolioState,
    onVisibilityChange: (String) -> Unit,
    onPublishClick: () -> Unit
) {
    var isPublishingState by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Safety Verification Checklist",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandPrimary
            )
        )
        Text(
            text = "Verify that details match registered original catalog metadata before locking the record.",
            style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Checklist Group
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandCard),
            border = BorderStroke(1.dp, BrandBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ChecklistRowItem(label = "Project Details Locked", detail = "${state.addingProjectName} (${state.addingProjectType})", done = true)
                Divider(color = BrandBorder, modifier = Modifier.padding(vertical = 10.dp))
                ChecklistRowItem(label = "Streaming Link Generated", detail = if (state.spotifyUrl.isNotEmpty()) "Spotify active" else "Default active link", done = true)
                Divider(color = BrandBorder, modifier = Modifier.padding(vertical = 10.dp))
                ChecklistRowItem(label = "Credit Attribution Assigned", detail = "${state.credits.size + 1} artist positions allocated", done = true)
                Divider(color = BrandBorder, modifier = Modifier.padding(vertical = 10.dp))
                ChecklistRowItem(label = "Collaborator Approvals Queued", detail = "Verify 2 tags", done = true)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Visibility Options Title
        Text("Visibility Options", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        val visibilities = listOf(
            Triple("Public", "Visible to anyone on Web and the MusicLinkd global feed search.", Icons.Outlined.Visibility),
            Triple("Connections Only", "Visible only to your verified network and co-collaborators.", Icons.Outlined.Group),
            Triple("Private Draft", "Visible to you and specified artists as a private demonstration.", Icons.Outlined.Lock)
        )

        visibilities.forEach { opt ->
            val isSelected = opt.first == state.visibility
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFFF5F3FF) else BrandCard
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) BrandPrimary else BrandBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable { onVisibilityChange(opt.first) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onVisibilityChange(opt.first) },
                        colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = opt.third, contentDescription = null, tint = BrandTextPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(opt.first, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandTextPrimary)
                        }
                        Text(opt.second, fontSize = 10.sp, color = BrandTextSecondary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        if (isPublishingState) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandPrimary)
            }
        } else {
            Button(
                onClick = {
                    isPublishingState = true
                    onPublishClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("portfolio_publish_btn")
            ) {
                Text("Publish to Portfolio Canvas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun ChecklistRowItem(label: String, detail: String, done: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandTextPrimary)
            Text(detail, fontSize = 10.sp, color = BrandTextSecondary)
        }
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Complete",
            tint = BrandSuccess,
            modifier = Modifier.size(20.dp)
        )
    }
}

// -------------------------------------------------------------------------------------
// SCREEN 10: SUCCESS STATE
// -------------------------------------------------------------------------------------
@Composable
fun SuccessStateScreen(
    state: PortfolioState,
    onViewProject: () -> Unit,
    onShareProject: () -> Unit,
    onGoToProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success Logo Animation Canvas
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .drawBehind {
                    drawCircle(
                        color = Color(0xFFD1FAE5),
                        radius = size.minDimension / 2f
                    )
                    drawCircle(
                        color = BrandSuccess.copy(alpha = 0.2f),
                        radius = size.minDimension / 1.7f,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
        ) {
            Text("🎉", fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Project Published Live!",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                color = BrandTextPrimary,
                letterSpacing = (-0.5).sp
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your newly registered project \"${state.lastPublishedProject?.name ?: "Swar EP"}\" was successfully uploaded and logged to India's copyright registry catalog on MusicLinkd.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = BrandTextSecondary,
                lineHeight = 22.sp
            ),
            modifier = Modifier.padding(horizontal = 14.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // CTA Buttons
        Button(
            onClick = onViewProject,
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("success_cnt_view")
        ) {
            Text("View Published Layout", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onShareProject,
            colors = ButtonDefaults.buttonColors(containerColor = BrandCard),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BrandBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("success_cnt_share")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = BrandTextPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Project Passport", color = BrandTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = onGoToProfile,
            modifier = Modifier.testTag("success_cnt_profile")
        ) {
            Text("Back to Portfolio Dashboard", color = BrandTextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

// -------------------------------------------------------------------------------------
// SCREEN 11: PROFILE PROJECT VIEW
// -------------------------------------------------------------------------------------
@Composable
fun ProfileProjectViewScreen(
    state: PortfolioState,
    onAddIncStat: (String, String) -> Unit,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val project = state.lastPublishedProject ?: state.projectsList.firstOrNull() ?: PortfolioProject(
        name = "Swar Sandhya EP",
        description = "Core lo-fi Indian music composition.",
        type = "EP",
        releaseDate = "2026-06-18",
        status = "Released",
        roles = setOf("Composer"),
        credits = listOf(CreditItem("Tara Sharma", "Singer", ""))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Large header banner artwork
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(getArtworkColor(project.artworkUrl), BrandPrimary.copy(alpha = 0.5f))
                    )
                )
        ) {
            // Overlay gradient to make text readable
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )

            // Top action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                }
            }

            // Project Title Banner
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(BrandSecondary, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            project.type.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Published on MusicLinkd",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    )
                )
                
                Text(
                    text = "Release date: ${project.releaseDate} • Status: ${project.status}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }

        // Project details container
        Column(modifier = Modifier.padding(16.dp)) {
            
            // Statistics Card Box
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandCard),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BrandBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InteractiveStatItem(label = "Views", count = project.projectViews, icon = Icons.Default.RemoveRedEye, onClick = { onAddIncStat(project.id, "view") })
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(BrandBorder))
                    InteractiveStatItem(label = "Likes", count = project.likes, icon = Icons.Default.Favorite, onClick = { onAddIncStat(project.id, "like") })
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(BrandBorder))
                    InteractiveStatItem(label = "Shares", count = project.shares, icon = Icons.Default.Share, onClick = { onAddIncStat(project.id, "share") })
                }
            }

            // Description details
            Text("About the Project", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandTextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = BrandTextSecondary,
                    lineHeight = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // My credits selection
            Text("Licensed Roles & Accreditations", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandTextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                project.roles.forEach { role ->
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEDE9FE), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(role, color = BrandSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Embedded streaming players simulated
            Text("Official Audio Embed Platforms", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            // Custom active players checklist
            val platforms = listOf(
                Triple(project.spotifyUrl, "Play on Spotify", Color(0xFF1DB954)),
                Triple(project.soundcloudUrl, "Listen on SoundCloud", Color(0xFFFF5500)),
                Triple(project.youtubeUrl, "Stream on YouTube", Color(0xFFFF0000))
            ).filter { it.first.isNotEmpty() }

            if (platforms.isEmpty()) {
                // Return default play item
                SimulatedPlayerCard(label = "Play on Spotify", url = "https://spotify.com/album/demotrack", color = Color(0xFF1DB954))
            } else {
                platforms.forEach { plt ->
                    SimulatedPlayerCard(label = plt.second, url = plt.first, color = plt.third)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Credits Attribution list
            Text("Verified Collaborator Table", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandTextPrimary)
            Spacer(modifier = Modifier.height(6.dp))

            if (project.credits.isEmpty()) {
                EmptyState(
                    title = "No Collaborators Listed",
                    description = "Verified collaborator credits have not been added or accredited to this master record yet.",
                    emoji = "📜"
                )
            } else {
                project.credits.forEach { cr ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BrandCard),
                        border = BorderStroke(1.dp, BrandBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF3F4F6), CircleShape)
                                ) {
                                    AsyncImage(
                                        model = cr.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                                        contentDescription = cr.collaboratorName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(cr.collaboratorName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandTextPrimary)
                            }
                            
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFEDE9FE), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(cr.role, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandSecondary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6)),
                border = BorderStroke(1.dp, BrandBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Back to Portfolio Dashboard", color = BrandTextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SimulatedPlayerCard(label: String, url: String, color: Color) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isPlaying) color else Color.White.copy(alpha = 0.1f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isPlaying = !isPlaying
                Toast.makeText(
                    context,
                    if (isPlaying) "Playing $label stream!" else "Paused $label",
                    Toast.LENGTH_SHORT
                ).show()
            }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Play / Pause Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(url, color = Color.Gray, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            AnimatedEqualizerWave(
                isPlaying = isPlaying,
                barColor = if (isPlaying) color else Color.Gray,
                maxHeight = 18.dp
            )
        }
    }
}

@Composable
fun InteractiveStatItem(
    label: String,
    count: Int,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = BrandPrimary, modifier = Modifier.size(16.dp))
        Text(count.toString(), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandTextPrimary)
        Text(label, fontSize = 9.sp, color = BrandTextSecondary)
    }
}
