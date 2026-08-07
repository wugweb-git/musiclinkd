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
fun UnifiedReviewCard(
    reviewerName: String,
    reviewerRole: String,
    rating: Float,
    reviewText: String,
    date: String,
    helpfulCount: Int,
    hasClickedHelpful: Boolean,
    onHelpfulClick: () -> Unit,
    reviewerAvatarUrl: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(Dimens.r3),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r3))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6))
                ) {
                    AsyncImage(
                        model = reviewerAvatarUrl ?: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=150&q=80",
                        contentDescription = reviewerName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.sp2))
                Column(modifier = Modifier.weight(1f)) {
                    Text(reviewerName, style = Typography.bodyLarge, color = BrandTextPrimary)
                    Text(reviewerRole, style = Typography.labelLarge, color = BrandTextSecondary)
                }
                Text(date, style = Typography.labelLarge, color = BrandTextSecondary)
            }
            
            Spacer(modifier = Modifier.height(Dimens.sp2))
            
            // Stars Rating Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    val starColor = if (index < rating) Color(0xFFF59E0B) else Color(0xFFE5E7EB)
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Star",
                        tint = starColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.sp1))
                Text(
                    text = "${rating.toInt()}.0 / 5.0",
                    style = Typography.bodyLarge,
                    color = BrandTextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = reviewText,
                style = Typography.labelLarge,
                color = BrandTextPrimary,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Helpful button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.r2))
                        .background(if (hasClickedHelpful) BrandPrimary.copy(alpha = 0.08f) else Color(0xFFF3F4F6))
                        .clickable { onHelpfulClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Helpful",
                        tint = if (hasClickedHelpful) BrandPrimary else BrandTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Helpful ($helpfulCount)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasClickedHelpful) BrandPrimary else BrandTextSecondary
                    )
                }
            }
        }
    }
}

// ============================================================================
// 12. ENHANCED UI & INTERACTION EFFECTS COMPONENTS
// ============================================================================

/**
 * Animated 5-bar live equalizer waveform for playing audio tracks.
 */
