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
fun DiscoverFeedSkeletonLoader(
    count: Int = 3,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        repeat(count) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(Dimens.r3),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Shimmer Cover Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .shimmer()
                    )
                    
                    Column(modifier = Modifier.padding(Dimens.sp2)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SkeletonCircle(size = 44.dp)
                                Spacer(modifier = Modifier.width(Dimens.sp2))
                                Column {
                                    SkeletonItem(width = 120.dp, height = 14.dp, cornerRadius = 4.dp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    SkeletonItem(width = 80.dp, height = 10.dp, cornerRadius = 4.dp)
                                }
                            }
                            SkeletonItem(width = 60.dp, height = 24.dp, cornerRadius = 12.dp)
                        }

                        Spacer(modifier = Modifier.height(Dimens.sp2))
                        SkeletonItem(width = 240.dp, height = 12.dp, cornerRadius = 4.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        SkeletonItem(width = 180.dp, height = 12.dp, cornerRadius = 4.dp)

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)) {
                            repeat(3) {
                                SkeletonItem(width = 70.dp, height = 22.dp, cornerRadius = 11.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Skeleton loading screen for Profile & Business details pages.
 */
