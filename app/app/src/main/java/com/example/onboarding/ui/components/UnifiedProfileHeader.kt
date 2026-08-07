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
fun UnifiedProfileHeader(
    name: String,
    displayName: String,
    avatarUrl: String?,
    bio: String,
    location: String,
    isVerified: Boolean = true,
    availabilityStatus: String = "Available",
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, BrandPrimary.copy(alpha = 0.2f), CircleShape)
            ) {
                AsyncImage(
                    model = avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80",
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = Typography.bodyLarge,
                        color = BrandTextPrimary
                    )
                    if (isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Professional",
                            tint = BrandPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                Text(
                    text = "@$displayName",
                    style = Typography.labelLarge,
                    color = BrandTextSecondary
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Availability Dot
                    val dotColor = when (availabilityStatus.lowercase()) {
                        "available" -> Color(0xFF10B981)
                        "in studio" -> Color(0xFFF59E0B)
                        "busy" -> Color(0xFFEF4444)
                        else -> Color.Gray
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(dotColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = availabilityStatus,
                        style = Typography.bodyLarge,
                        color = dotColor
                    )
                    Spacer(modifier = Modifier.width(Dimens.sp2))
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = BrandTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = location,
                        style = Typography.labelLarge,
                        color = BrandTextSecondary
                    )
                }
            }
            
            if (onEditClick != null) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = BrandTextSecondary)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(14.dp))
        
        Text(
            text = bio,
            style = Typography.labelLarge,
            color = BrandTextPrimary,
            lineHeight = 20.sp
        )
    }
}

// ============================================================================
// 7. ARTIST / TALENT CARD
// ============================================================================
