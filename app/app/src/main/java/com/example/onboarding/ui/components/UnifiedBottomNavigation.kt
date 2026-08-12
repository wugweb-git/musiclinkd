package com.example.onboarding.ui.components

import androidx.compose.ui.text.style.TextAlign
import com.example.onboarding.database.FirebaseManager
import com.example.onboarding.util.DemoDataProvider
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
fun UnifiedBottomNavigation(
    activeTab: String,
    onTabClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    connectionsBadgeCount: Int? = DemoDataProvider.DEFAULT_CONNECTIONS_BADGE
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, BrandBorder)
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White,
            windowInsets = WindowInsets(0, 0, 0, 0),
            tonalElevation = 0.dp
        ) {
            val menuItems = listOf(
                Triple(stringResource(R.string.nav_home), Icons.Default.Home, Icons.Outlined.Home),
                Triple(stringResource(R.string.nav_discover), Icons.Default.Explore, Icons.Outlined.Explore),
                Triple(stringResource(R.string.nav_connections), Icons.Default.Group, Icons.Outlined.Group),
                Triple(stringResource(R.string.nav_market), Icons.Default.Storefront, Icons.Outlined.Storefront),
                Triple(stringResource(R.string.nav_profile), Icons.Default.AccountCircle, Icons.Outlined.AccountCircle)
            )

            menuItems.forEach { (label, filledIcon, outlinedIcon) ->
                val isActive = activeTab.equals(label, ignoreCase = true)
                val isConnections = label.equals(stringResource(R.string.nav_connections), ignoreCase = true)
                val tag = "tab_${label.lowercase()}"

                NavigationBarItem(
                    selected = isActive,
                    onClick = { onTabClick(label) },
                    modifier = Modifier
                        .testTag(tag)
                        .semantics {
                            contentDescription = stringResource(
                                if (isActive) R.string.nav_tab_selected else R.string.nav_tab_unselected,
                                label
                            )
                        },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (isConnections && connectionsBadgeCount != null && connectionsBadgeCount > 0) {
                                    Badge(
                                        containerColor = BrandPrimary,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            connectionsBadgeCount.toString(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            if (isConnections) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_musiclinkd_connections),
                                    contentDescription = label,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (isActive) BrandPrimary else BrandTextSecondary
                                )
                            } else {
                                Icon(
                                    imageVector = if (isActive) filledIcon else outlinedIcon,
                                    contentDescription = label,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (isActive) BrandPrimary else BrandTextSecondary
                                )
                            }
                        }
                    },
                    label = {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = if (isActive) BrandPrimary else BrandTextSecondary
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = BrandPrimary.copy(alpha = 0.12f),
                        selectedIconColor = BrandPrimary,
                        selectedTextColor = BrandPrimary,
                        unselectedIconColor = BrandTextSecondary,
                        unselectedTextColor = BrandTextSecondary
                    )
                )
            }
        }
    }
}

// ============================================================================
// 6. PROFILE HEADER
// ============================================================================
