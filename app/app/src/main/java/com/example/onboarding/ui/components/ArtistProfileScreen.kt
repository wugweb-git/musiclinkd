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
fun ArtistProfileScreen(
    artistName: String = "Tara Sharma",
    artistRole: String = "Pop Vocalist & Songwriter",
    location: String = "Bandra, Mumbai",
    bioText: String = "Versatile pop & topline vocalist with 6+ years of playback and studio recording experience. Open for vocal features, backing arrangements, and acoustic sessions.",
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUserEmail = remember { FirebaseManager.getCurrentUserEmail() ?: "tara.sharma@musiclinkd.com" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        // Hero Header Base Container (24.dp radius)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BrandDark),
            shape = RoundedCornerShape(Dimens.r3)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(BrandPrimary)
                        ) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=300&q=80",
                                contentDescription = artistName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(artistName, style = Typography.bodyLarge, color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                UnifiedVerificationBadge(label = "Verified Artist")
                            }
                            Text(artistRole, fontSize = 12.5.sp, color = Color.White.copy(alpha = 0.8f))
                            Text(location, fontSize = 11.5.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                    }

                    UnifiedConnectButton(
                        targetUserId = "artist_tara",
                        targetUserName = artistName
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.sp2))

                Text(
                    text = bioText,
                    fontSize = 12.5.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auth Account: $currentUserEmail", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                    UnifiedStarRating(rating = 4.9, reviewCount = 38)
                }
            }
        }

        // Music Portfolio Base Container Card (24.dp radius)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(Dimens.r3),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Featured Portfolio Tracks", style = Typography.bodyLarge, color = BrandDark)
                Spacer(modifier = Modifier.height(Dimens.sp2))
                WaveformAudioPlayer(
                    title = "Tum Se Hi (Acoustic Reimagined)",
                    durationText = "3:42",
                    isPlaying = false,
                    onPlayToggle = {}
                )
                Spacer(modifier = Modifier.height(10.dp))
                WaveformAudioPlayer(
                    title = "Neon Nights (Topline Vocals)",
                    durationText = "2:50",
                    isPlaying = false,
                    onPlayToggle = {}
                )
            }
        }

        // Skills & Gear Base Container Card (24.dp radius)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(Dimens.r3),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Specializations & Studio Gear", style = Typography.bodyLarge, color = BrandDark)
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.sp1),
                    verticalArrangement = Arrangement.spacedBy(Dimens.sp1)
                ) {
                    val tags = listOf("Neo-Soul", "Vocal Tuning", "ProTools 2024", "Neumann TLM 103", "Apollo Twin X")
                    tags.forEach { tag ->
                        UnifiedChip(text = tag, selected = true, onClick = {})
                    }
                }
            }
        }

        // Media Attachments Base Container Card (Firebase Storage Integration - 24.dp radius)
        var mediaAttachments by remember {
            mutableStateOf(DemoDataProvider.defaultMediaAttachments)
        }
        var isUploadingMedia by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(Dimens.r3),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Media Attachments & STEMs", style = Typography.bodyLarge, color = BrandDark)
                        Text("Cloud Storage via Firebase", fontSize = 11.5.sp, color = BrandTextSecondary)
                    }
                    Button(
                        onClick = {
                            if (!isUploadingMedia) {
                                isUploadingMedia = true
                                FirebaseManager.uploadMediaAttachment(
                                    fileName = "New_Acoustic_Take_${System.currentTimeMillis().toString().takeLast(4)}.wav",
                                    mediaType = "AUDIO",
                                    onSuccess = { downloadUrl ->
                                        isUploadingMedia = false
                                        mediaAttachments = mediaAttachments + MediaAttachmentItem(
                                            id = "media_${System.currentTimeMillis()}",
                                            fileName = "New_Vocal_Take_Master.wav",
                                            type = "AUDIO",
                                            url = downloadUrl,
                                            sizeOrDuration = "2:45"
                                        )
                                    },
                                    onFailure = {
                                        isUploadingMedia = false
                                    }
                                )
                            }
                        },
                        shape = RoundedCornerShape(Dimens.r2),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary.copy(alpha = 0.12f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        if (isUploadingMedia) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = BrandPrimary, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.CloudUpload, contentDescription = "Upload", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload Media", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Audio Attachments
                mediaAttachments.filter { it.type == "AUDIO" }.forEach { audioItem ->
                    WaveformAudioPlayer(
                        title = audioItem.fileName,
                        durationText = audioItem.sizeOrDuration,
                        isPlaying = false,
                        onPlayToggle = {}
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Image Gallery Attachments
                val images = mediaAttachments.filter { it.type == "IMAGE" }
                if (images.isNotEmpty()) {
                    Text("Behind-the-scenes Gallery", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = BrandDark)
                    Spacer(modifier = Modifier.height(Dimens.sp1))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        images.forEach { imgItem ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(Dimens.r2))
                                    .background(BrandBorder)
                            ) {
                                AsyncImage(
                                    model = imgItem.url,
                                    contentDescription = imgItem.fileName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class MediaAttachmentItem(
    val id: String,
    val fileName: String,
    val type: String, // "AUDIO" or "IMAGE"
    val url: String,
    val sizeOrDuration: String
)

// ============================================================================
// CENTRALIZED ERROR HANDLING & ERROR BOUNDARY WRAPPER
// ============================================================================

/**
 * Reusable Base Container Error Card adhering to 24.dp radius and project brand styling.
 */
