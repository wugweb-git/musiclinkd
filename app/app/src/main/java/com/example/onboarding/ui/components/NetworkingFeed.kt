package com.example.onboarding.ui.components

import androidx.compose.ui.text.style.TextAlign
import com.example.onboarding.database.FirebaseManager
import com.example.onboarding.util.DemoDataProvider
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*

// ============================================================================
// 1. CHIP SYSTEM (Pill-shaped selection indicators)
// ============================================================================

@Composable
fun NetworkingFeed(
    modifier: Modifier = Modifier,
    onOpenProfile: (String) -> Unit = {}
) {
    var postsList by remember {
        mutableStateOf(
            listOf(
                FeedPostItem(
                    id = "feed_1",
                    authorName = "Rhea Kapoor",
                    authorRole = "Playback Vocalist & Topliner",
                    authorAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=300&q=80",
                    isVerified = true,
                    content = "🔥 Just published fresh acoustic STEMs for 'Sufi Echoes'! Seeking a synthwave or chillhop producer to record a remix track.",
                    postType = "COLLAB_CALL",
                    timestampText = "2 hrs ago",
                    likesCount = 24,
                    audioTitle = "Sufi Echoes - Dry Vocal Stem (WAV 24-bit)",
                    audioDuration = "3:15"
                ),
                FeedPostItem(
                    id = "feed_2",
                    authorName = "Acoustic Vault Studios",
                    authorRole = "Verified Recording Studio",
                    authorAvatar = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=300&q=80",
                    isVerified = true,
                    content = "🎙️ Live Room A upgrade complete! Installed Neumann U87 Ai and Solid State Logic (SSL) console. Booking slots open for weekend mixing sessions.",
                    postType = "STUDIO_UPDATE",
                    timestampText = "5 hrs ago",
                    likesCount = 42
                ),
                FeedPostItem(
                    id = "feed_3",
                    authorName = "Amit Trivedi",
                    authorRole = "Music Director & Composer",
                    authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=300&q=80",
                    isVerified = true,
                    content = "🎹 Finished final master bounce for 'Neon Dreamscape'. Big thanks to our session guitarist Rohan Sharma and mixing engineer Rahul!",
                    postType = "RELEASE",
                    timestampText = "1 day ago",
                    likesCount = 89,
                    audioTitle = "Neon Dreamscape - Official Master Bounce",
                    audioDuration = "4:02"
                )
            )
        )
    }

    var selectedFilter by remember { mutableStateOf("ALL") }
    var newPostText by remember { mutableStateOf("") }
    var isPosting by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        FirebaseManager.fetchNetworkingFeed(
            onSuccess = { fetched ->
                if (fetched.isNotEmpty()) {
                    val mapped = fetched.mapIndexed { idx, map ->
                        FeedPostItem(
                            id = "fs_post_$idx",
                            authorName = map["authorName"] as? String ?: "Musician Member",
                            authorRole = map["authorRole"] as? String ?: "Artist",
                            authorAvatar = map["mediaUrl"] as? String,
                            isVerified = true,
                            content = map["content"] as? String ?: "",
                            postType = map["postType"] as? String ?: "UPDATE",
                            timestampText = "Just now",
                            likesCount = (map["likesCount"] as? Number)?.toInt() ?: 0
                        )
                    }
                    postsList = mapped + postsList
                }
            },
            onFailure = { /* Fallback to initial seed list */ }
        )
    }

    val filteredPosts = remember(postsList, selectedFilter) {
        if (selectedFilter == "ALL") postsList
        else postsList.filter { it.postType == selectedFilter }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        // Create Post Base Card (24.dp radius)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(Dimens.r3),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(modifier = Modifier.padding(Dimens.sp2)) {
                Text(
                    text = "Share Professional Update",
                    style = Typography.bodyLarge,
                    color = BrandDark
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = newPostText,
                    onValueChange = { newPostText = it },
                    placeholder = { Text("Seeking collaborators, sharing new STEMs, or studio availability...", fontSize = 12.5.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.r2),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BrandBorder
                    )
                )
                Spacer(modifier = Modifier.height(Dimens.sp2))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)) {
                        UnifiedChip(text = "🎵 STEMs", selected = false, onClick = {})
                        UnifiedChip(text = "🎙️ Studio", selected = false, onClick = {})
                    }
                    Button(
                        onClick = {
                            if (newPostText.isNotBlank() && !isPosting) {
                                isPosting = true
                                val userEmail = FirebaseManager.getCurrentUserEmail() ?: "artist@musiclinkd.com"
                                val userName = userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
                                FirebaseManager.createFeedPost(
                                    authorName = userName,
                                    authorRole = "Music Producer & Creator",
                                    content = newPostText,
                                    postType = "COLLAB_CALL",
                                    onSuccess = {
                                        postsList = listOf(
                                            FeedPostItem(
                                                id = "post_${System.currentTimeMillis()}",
                                                authorName = userName,
                                                authorRole = "Music Producer & Creator",
                                                authorAvatar = null,
                                                isVerified = true,
                                                content = newPostText,
                                                postType = "COLLAB_CALL",
                                                timestampText = "Just now",
                                                likesCount = 1
                                            )
                                        ) + postsList
                                        newPostText = ""
                                        isPosting = false
                                    },
                                    onFailure = {
                                        isPosting = false
                                    }
                                )
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        enabled = newPostText.isNotBlank() && !isPosting
                    ) {
                        Text("Post Update", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }
                }
            }
        }

        // Filter Bar Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)
        ) {
            val filters = listOf("ALL" to "All Feed", "COLLAB_CALL" to "Calls", "RELEASE" to "Releases", "STUDIO_UPDATE" to "Studios")
            filters.forEach { (type, label) ->
                UnifiedChip(
                    text = label,
                    selected = selectedFilter == type,
                    onClick = { selectedFilter = type }
                )
            }
        }

        // Feed Items List
        filteredPosts.forEach { post ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenProfile(post.authorName) },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(Dimens.r3),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Author Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary.copy(alpha = 0.1f))
                            ) {
                                if (post.authorAvatar != null) {
                                    AsyncImage(
                                        model = post.authorAvatar,
                                        contentDescription = post.authorName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = BrandPrimary,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(Dimens.sp2))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = post.authorName,
                                        style = Typography.bodyLarge,
                                        color = BrandDark
                                    )
                                    if (post.isVerified) {
                                        Spacer(modifier = Modifier.width(Dimens.sp1))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified",
                                            tint = BrandPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${post.authorRole} • ${post.timestampText}",
                                    fontSize = 11.5.sp,
                                    color = BrandTextSecondary
                                )
                            }
                        }

                        // Connect Button Component
                        UnifiedConnectButton(
                            targetUserId = post.id,
                            targetUserName = post.authorName
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.sp2))

                    Text(
                        text = post.content,
                        fontSize = 13.5.sp,
                        color = BrandDark,
                        lineHeight = 19.sp
                    )

                    if (post.audioTitle != null && post.audioDuration != null) {
                        Spacer(modifier = Modifier.height(Dimens.sp2))
                        WaveformAudioPlayer(
                            title = post.audioTitle,
                            durationText = post.audioDuration,
                            isPlaying = false,
                            onPlayToggle = {}
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BrandBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Like", tint = BrandTextSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(Dimens.sp1))
                            Text("${post.likesCount}", style = Typography.labelLarge, color = BrandTextSecondary)
                        }
                        Text("Reply in Session →", style = Typography.bodyLarge, color = BrandPrimary)
                    }
                }
            }
        }
    }
}

// ============================================================================
// GLOBAL SEARCH FEATURE TOP-BAR & SEARCH FLOW
// ============================================================================
