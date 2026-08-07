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
fun UnifiedMarketplaceCard(
    name: String,
    price: String,
    condition: String?,
    deposit: String?,
    location: String,
    owner: String,
    ownerType: String,
    imageUrl: String?,
    category: String, // "sale" or "rental"
    audioDemo: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.r3),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r3))
    ) {
        Column(modifier = Modifier.padding(Dimens.sp2)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFAFAFA)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUrl ?: "https://images.unsplash.com/photo-1598653222000-6b7b7a552625?auto=format&fit=crop&w=300&q=80",
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(Dimens.sp2))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = name,
                            style = Typography.bodyLarge,
                            color = BrandTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (audioDemo) {
                            Spacer(modifier = Modifier.width(Dimens.sp1))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFECFDF5), RoundedCornerShape(Dimens.r1))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("AUDIO", color = Color(0xFF059669), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (category.lowercase() == "sale") "Sale" else "Rental",
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary,
                            style = Typography.labelLarge
                        )
                        Spacer(modifier = Modifier.width(Dimens.sp1))
                        Text("•", color = BrandTextSecondary)
                        Spacer(modifier = Modifier.width(Dimens.sp1))
                        Text(location, style = Typography.labelLarge, color = BrandTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = price,
                        style = Typography.bodyLarge,
                        color = BrandTextPrimary
                    )
                    if (condition != null) {
                        Text(condition, fontSize = 11.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                    } else if (deposit != null) {
                        Text("Deposit: $deposit", fontSize = 11.sp, color = BrandTextSecondary)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "By $owner",
                        style = Typography.labelLarge,
                        color = BrandTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(Dimens.sp1))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Owner",
                        tint = if (ownerType.lowercase() == "business") Color(0xFFF59E0B) else BrandPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// 10. OPPORTUNITY CARD
// ============================================================================
