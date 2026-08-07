@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.onboarding.ui.screens

import com.example.onboarding.ai.GeminiRecommendationEngine
import com.example.onboarding.ai.TrendingMusicProject
import com.example.onboarding.util.DynamicLinkManager
import com.example.onboarding.util.ErrorReportingService
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onboarding.models.*
import com.example.onboarding.viewmodel.*
import com.example.onboarding.ui.components.*

// Cohesive Branding Colors aligned to high-fashion neutral and active creator pink

@Composable
fun TrendingMusicProjectsSection(
    userGenres: List<String>
) {
    var projects by remember { mutableStateOf<List<TrendingMusicProject>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var playingProjectState by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(userGenres) {
        isLoading = true
        try {
            val genresString = if (userGenres.isNotEmpty()) userGenres.joinToString(", ") else "Indie Rock, Fusion, Bollywood"
            projects = GeminiRecommendationEngine.fetchTrendingMusicProjects(genresString)
        } catch (e: Throwable) {
            ErrorReportingService.logException(e, "TrendingProjectsSection", "Error fetching trending projects")
            projects = GeminiRecommendationEngine.getFallbackTrendingProjects(userGenres.joinToString())
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔥 Trending Music Projects",
                        style = Typography.bodyLarge,
                        color = BrandText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF7C3AED).copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("AI Recommended", fontSize = 9.sp, color = Color(0xFF7C3AED), fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "Matched to your genres: ${if (userGenres.isNotEmpty()) userGenres.take(2).joinToString(", ") else "Indie & Electronic"}",
                    style = Typography.labelLarge,
                    color = BrandMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.sp2))

        if (isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = BrandPrimary)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Analyzing Indian music grid trends...", fontSize = 13.sp, color = BrandMuted)
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(projects, key = { it.id }) { project ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.StartToEnd || dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                projects = projects.filter { it.id != project.id }
                                android.widget.Toast.makeText(context, "Project card dismissed from feed", android.widget.Toast.LENGTH_SHORT).show()
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(Dimens.r2))
                                    .background(Color(0xFFEF4444))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Delete, contentDescription = "Dismiss", tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Dismissed", color = Color.White, style = Typography.bodyLarge)
                                }
                            }
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .width(280.dp)
                                .testTag("trending_project_${project.id}"),
                            shape = RoundedCornerShape(Dimens.r2),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            border = BorderStroke(1.dp, BrandBorder)
                        ) {
                        Column {
                            // Cover Image with Match Badge
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            ) {
                                AsyncImage(
                                    model = project.coverImageUrl,
                                    contentDescription = project.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                            )
                                        )
                                )
                                // Match Score Badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(Dimens.r1))
                                        .background(BrandPrimary)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("${project.matchScore}% Match", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                                // Title and Listens
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = project.genre,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = project.title,
                                        color = Color.White,
                                        style = Typography.bodyLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Project Details Body
                            Column(modifier = Modifier.padding(Dimens.sp2)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = BrandMuted)
                                    Spacer(modifier = Modifier.width(Dimens.sp1))
                                    Text(
                                        text = project.creatorName,
                                        style = Typography.bodyLarge,
                                        color = BrandText
                                    )
                                    Spacer(modifier = Modifier.width(Dimens.sp1))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = BrandPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text("${project.listensCount} plays", fontSize = 10.sp, color = BrandMuted)
                                }

                                Text(
                                    text = project.description,
                                    fontSize = 11.sp,
                                    color = BrandMuted,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )

                                // Seeking Roles Chips
                                Text("Open Roles Needed:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandText)
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    items(project.seekingRoles) { role ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(BrandPrimary.copy(alpha = 0.08f))
                                                .border(0.8.dp, BrandPrimary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(role, fontSize = 9.sp, color = BrandPrimary, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Audio Snippet, Share & Collab Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isPlaying = playingProjectState == project.id
                                    OutlinedButton(
                                        onClick = {
                                            playingProjectState = if (isPlaying) null else project.id
                                            val msg = if (isPlaying) "Paused demo" else "Playing audio preview: ${project.audioSnippetTitle}"
                                            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                        border = BorderStroke(1.dp, if (isPlaying) BrandPrimary else BrandBorder)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Demo",
                                            tint = if (isPlaying) BrandPrimary else BrandText,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = if (isPlaying) "Playing" else "Demo",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPlaying) BrandPrimary else BrandText
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            DynamicLinkManager.shareProjectDynamicLink(
                                                context = context,
                                                projectId = project.id,
                                                projectTitle = project.title,
                                                creatorName = project.creatorName
                                            )
                                        },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .border(1.dp, BrandBorder, RoundedCornerShape(10.dp))
                                            .testTag("share_project_${project.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share Dynamic Link",
                                            tint = BrandPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            android.widget.Toast.makeText(
                                                context,
                                                "Collaboration request sent to ${project.creatorName}!",
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                    ) {
                                        Text("Join Collab", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}
