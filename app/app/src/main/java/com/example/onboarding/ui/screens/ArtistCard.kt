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
fun ArtistCard(
    profile: DiscoverProfile,
    onProfileClick: () -> Unit,
    onHireClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val springEasing = remember { CubicBezierEasing(0.22f, 1.0f, 0.36f, 1.0f) }

    val translationY by animateDpAsState(
        targetValue = if (isHovered || isPressed) (-4).dp else 0.dp,
        animationSpec = tween(durationMillis = 200, easing = springEasing),
        label = "translationY"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else if (isHovered) 1.01f else 1.0f,
        animationSpec = tween(durationMillis = 150, easing = springEasing),
        label = "scale"
    )

    var audioState by remember { mutableStateOf(0) } // 0 = Idle, 1 = Buffering, 2 = Playing

    LaunchedEffect(audioState) {
        if (audioState == 1) {
            kotlinx.coroutines.delay(800) // Simulating buffering
            audioState = 2
        }
    }
    val isAudioPlaying = audioState == 2
    val dominantColor = Color(profile.avatarColorHex)

    Card(
        modifier = modifier
            .graphicsLayer {
                this.translationY = translationY.toPx()
                this.scaleX = scale
                this.scaleY = scale
            }
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onProfileClick
            ),
        shape = RoundedCornerShape(Dimens.r3),
        colors = CardDefaults.cardColors(
            containerColor = if (isHovered) Color.White.copy(alpha = 0.96f) else Color.White
        ),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered || isPressed) 6.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isHovered) {
                        Brush.radialGradient(
                            colors = listOf(dominantColor.copy(alpha = 0.04f), Color.Transparent)
                        )
                    } else {
                        Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(profile.avatarColorHex).copy(alpha = 0.25f),
                                    Color(profile.avatarColorHex).copy(alpha = 0.05f)
                                )
                            )
                        )
                        .border(1.dp, Color(profile.avatarColorHex).copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = profile.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                        contentDescription = profile.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                val isAvailable = profile.availabilityStatus.contains("Avail", ignoreCase = true) || profile.availabilityStatus.contains("Open", ignoreCase = true)
                val tagBgColor = if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                val tagTextColor = if (isAvailable) Color(0xFF2E7D32) else Color(0xFFE65100)
                val dotColor = if (isAvailable) Color(0xFF4CAF50) else Color(0xFFFF9800)

                Row(
                    modifier = Modifier
                        .background(tagBgColor, RoundedCornerShape(Dimens.rFull))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    val pulseTransition = rememberInfiniteTransition(label = "pulse")
                    val alpha by pulseTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(800, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_alpha"
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .graphicsLayer { this.alpha = alpha }
                            .background(dotColor, CircleShape)
                    )
                    Text(
                        text = profile.availabilityStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = profile.name,
                    style = Typography.bodyLarge,
                    color = BrandText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (profile.isVerified) {
                    Spacer(modifier = Modifier.width(Dimens.sp1))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = Color(0xFFEA4C89),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp1))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val instEmoji = getInstrumentEmoji(profile.subRole, profile.category)
                Text(instEmoji, style = Typography.labelLarge)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = profile.subRole,
                    fontSize = 13.sp,
                    color = Color(0xFFEA4C89),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(Dimens.sp1))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = BrandMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.sp1))
                Text(
                    text = "${profile.area} • ${profile.location}",
                    style = Typography.labelLarge,
                    color = BrandMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                profile.genres.take(2).forEach { genre ->
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF3F3F6), RoundedCornerShape(Dimens.rFull))
                            .border(1.dp, Color(0xFFECECF1), RoundedCornerShape(Dimens.rFull))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = genre,
                            fontSize = 10.sp,
                            color = Color(0xFF4A4D5C),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            HorizontalDivider(color = Color(0xFFECECF1))

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val audioInteractionSource = remember { MutableInteractionSource() }
                val isAudioPressed by audioInteractionSource.collectIsPressedAsState()
                val audioScale by animateFloatAsState(
                    targetValue = if (isAudioPressed) 0.92f else 1.0f,
                    label = "audio_scale"
                )

                Row(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = audioScale
                            scaleY = audioScale
                        }
                        .background(
                            if (isAudioPlaying) Color(0xFFEA4C89).copy(alpha = 0.1f) else Color(0xFFF3F3F6),
                            RoundedCornerShape(Dimens.rFull)
                        )
                        .border(
                            1.dp,
                            if (isAudioPlaying) Color(0xFFEA4C89).copy(alpha = 0.3f) else Color(0xFFECECF1),
                            RoundedCornerShape(Dimens.rFull)
                        )
                        .clickable(
                            interactionSource = audioInteractionSource,
                            indication = androidx.compose.foundation.LocalIndication.current,
                            onClick = { 
                                audioState = if (audioState == 2) 0 else 1 
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (audioState == 1) {
                        CircularProgressIndicator(
                            color = Color(0xFFEA4C89),
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Quick Audio Preview",
                            tint = if (isAudioPlaying) Color(0xFFEA4C89) else Color(0xFF4A4D5C),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    if (isAudioPlaying) {
                        AnimatedWaveform(modifier = Modifier.padding(end = 4.dp))
                    } else if (audioState == 1) {
                        Text(
                            text = "Buffering",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEA4C89)
                        )
                    } else {
                        Text(
                            text = "Preview",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A4D5C)
                        )
                    }
                }

                val buttonInteractionSource = remember { MutableInteractionSource() }
                val isBtnPressed by buttonInteractionSource.collectIsPressedAsState()
                val btnScale by animateFloatAsState(
                    targetValue = if (isBtnPressed) 0.95f else 1.0f,
                    label = "btn_scale"
                )

                Button(
                    onClick = onHireClick,
                    interactionSource = buttonInteractionSource,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = btnScale
                            scaleY = btnScale
                        }
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0D0B26),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(Dimens.rFull),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Collab / Hire",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
}
