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
fun ProfileSkeletonLoader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BrandBackground)
    ) {
        // Banner Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .shimmer()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Avatar Circle
            Box(
                modifier = Modifier
                    .offset(y = (-40).dp)
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color.White, CircleShape)
                    .shimmer()
            )

            Spacer(modifier = Modifier.height((-30).dp))

            SkeletonItem(width = 160.dp, height = 20.dp, cornerRadius = 4.dp)
            Spacer(modifier = Modifier.height(8.dp))
            SkeletonItem(width = 110.dp, height = 12.dp, cornerRadius = 4.dp)

            Spacer(modifier = Modifier.height(16.dp))

            // Stat Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(3) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(105.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SkeletonItem(width = 40.dp, height = 16.dp, cornerRadius = 4.dp)
                            Spacer(modifier = Modifier.height(6.dp))
                            SkeletonItem(width = 60.dp, height = 10.dp, cornerRadius = 4.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Content Bio Skeleton Lines
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SkeletonItem(width = 100.dp, height = 14.dp, cornerRadius = 4.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    SkeletonItem(width = 280.dp, height = 12.dp, cornerRadius = 4.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    SkeletonItem(width = 220.dp, height = 12.dp, cornerRadius = 4.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    SkeletonItem(width = 150.dp, height = 12.dp, cornerRadius = 4.dp)
                }
            }
        }
    }
}

/**
 * Skeleton loading screen for Chat / Messaging conversations.
 */
