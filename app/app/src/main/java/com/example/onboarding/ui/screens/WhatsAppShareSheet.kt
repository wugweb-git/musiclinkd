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
fun WhatsAppShareSheet(
    profile: DiscoverProfile,
    onDismiss: () -> Unit
) {
    var isCopied by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .widthIn(max = 480.dp),
        confirmButton = {},
        dismissButton = {},
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Share Portfolio Profile",
                    style = Typography.bodyLarge,
                    color = BrandText
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
            ) {
                Text(
                    text = "Promote this certified profile to external clients and partners via WhatsApp. Landing page previews are automatically generated.",
                    style = Typography.labelLarge,
                    color = BrandMuted
                )
                
                Text(
                    text = "WHATSAPP CHAT LINK PREVIEW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandSecondary,
                    letterSpacing = 1.sp
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(Dimens.r2)),
                    shape = RoundedCornerShape(Dimens.r2),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(profile.avatarColorHex).copy(alpha = 0.35f),
                                            Color(profile.avatarColorHex).copy(alpha = 0.08f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = profile.imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                                contentDescription = profile.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Verified status",
                                        tint = BrandSuccess,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("MusicLinkd Pro", fontSize = 8.sp, color = BrandText, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile.name,
                                    style = Typography.bodyLarge,
                                    color = BrandText
                                )
                                if (profile.isVerified) {
                                    Spacer(modifier = Modifier.width(Dimens.sp1))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified Artist",
                                        tint = BrandPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            
                            Text(
                                text = "${profile.subRole} • ${profile.location}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary
                            )
                            
                            Text(
                                text = profile.bio.ifEmpty { "Certified professional portfolio page with music streams, availability booking calendar, and client reviews." },
                                fontSize = 10.sp,
                                color = BrandMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            
                            Spacer(modifier = Modifier.height(Dimens.sp1))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = "Link",
                                    tint = BrandSuccess,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "musiclinkd.com/${profile.name.lowercase().replace(" ", "")}",
                                    fontSize = 10.sp,
                                    color = BrandSuccess,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(Dimens.sp1))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)
                ) {
                    Button(
                        onClick = {
                            isCopied = true
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(Dimens.r2)
                    ) {
                        Text("Send to WhatsApp", style = Typography.bodyLarge, color = Color.White)
                    }
                    
                    OutlinedButton(
                        onClick = {
                            isCopied = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(Dimens.r2),
                        border = BorderStroke(1.dp, BrandBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isCopied) {
                                MicroCheckmarkSuccessAnimation()
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Link",
                                    tint = BrandText,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = if (isCopied) "Copied!" else "Copy Link",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandText
                            )
                        }
                    }
                }
            }
        }
    )
}
