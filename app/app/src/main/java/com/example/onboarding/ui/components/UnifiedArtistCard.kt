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
fun UnifiedArtistCard(
    name: String,
    role: String,
    location: String,
    genres: String,
    availability: String,
    creditsCount: Int,
    followersCount: String,
    imageUrl: String?,
    onViewProfile: () -> Unit,
    onMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6))
                ) {
                    AsyncImage(
                        model = imageUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80",
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = BrandTextPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Filled.Verified, contentDescription = "Verified", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = role,
                        fontSize = 14.sp,
                        color = BrandTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Availability Pill
                    val dotColor = when (availability.lowercase()) {
                        "available" -> Color(0xFF10B981)
                        "in studio" -> Color(0xFFF59E0B)
                        "busy" -> Color(0xFFEF4444)
                        else -> Color.Gray
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(dotColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).background(dotColor, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(availability, fontSize = 11.sp, color = dotColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Location", fontSize = 11.sp, color = BrandTextSecondary)
                    Text(location, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandTextPrimary)
                }
                Column {
                    Text("Credits", fontSize = 11.sp, color = BrandTextSecondary)
                    Text("$creditsCount Verified", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandTextPrimary)
                }
                Column {
                    Text("Followers", fontSize = 11.sp, color = BrandTextSecondary)
                    Text(followersCount, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandTextPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                UnifiedSecondaryButton(
                    text = "Message",
                    onClick = onMessage,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                UnifiedPrimaryButton(
                    text = "View Profile",
                    onClick = onViewProfile,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ============================================================================
// 8. STUDIO CARD
// ============================================================================
