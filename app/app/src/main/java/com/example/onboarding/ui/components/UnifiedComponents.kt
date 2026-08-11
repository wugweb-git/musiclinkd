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
fun UnifiedChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "chipScale"
    )

    Box(
        modifier = modifier
            .testTag(testTag)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(999.dp))
            .background(
                color = if (selected) BrandPrimary.copy(alpha = 0.08f) else Color.White
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) BrandPrimary else BrandBorder,
                shape = RoundedCornerShape(999.dp)
            )
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.foundation.LocalIndication.current
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (selected) {
                Text(
                    text = "✓ ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BrandPrimary
                )
            }
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) BrandPrimary else BrandTextPrimary
            )
        }
    }
}

// ============================================================================
// 2. INPUT SYSTEM (Clean Outlined text field)
// ============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    error: String? = null,
    testTag: String = ""
) {
    val focusManager = LocalFocusManager.current
    
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = BrandTextPrimary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { 
                Text(
                    text = placeholder,
                    color = BrandTextSecondary,
                    fontSize = 15.sp
                ) 
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
                .height(56.dp),
            shape = RoundedCornerShape(999.dp),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) },
                onDone = { focusManager.clearFocus() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BrandBorder,
                errorBorderColor = Color.Red,
                focusedTextColor = BrandTextPrimary,
                unfocusedTextColor = BrandTextPrimary
            )
        )
        
        if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

// ============================================================================
// 3. BUTTON SYSTEM (Primary, Secondary, Text Buttons)
// ============================================================================
@Composable
fun UnifiedPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    testTag: String = ""
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(50.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandPrimary,
            contentColor = Color.White,
            disabledContainerColor = BrandBorder,
            disabledContentColor = BrandTextSecondary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun UnifiedSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    testTag: String = ""
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(50.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, BrandBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = BrandTextPrimary,
            disabledContentColor = BrandTextSecondary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = BrandTextPrimary)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun UnifiedTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = ""
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.testTag(testTag)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = BrandPrimary
        )
    }
}

// ============================================================================
// 4. SEARCH BAR (Unified search field)
// ============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder, fontSize = 14.sp, color = BrandTextSecondary) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = BrandTextSecondary) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = BrandTextSecondary)
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = BrandPrimary,
            unfocusedBorderColor = BrandBorder,
            focusedTextColor = BrandTextPrimary,
            unfocusedTextColor = BrandTextPrimary
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag(testTag),
        singleLine = true
    )
}

// ============================================================================
// 5. BOTTOM NAVIGATION SYSTEM
// ============================================================================
@Composable
fun UnifiedBottomNavigation(
    activeTab: String,
    onTabClick: (String) -> Unit,
    modifier: Modifier = Modifier
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
                Triple("Home", Icons.Default.Home, Icons.Outlined.Home),
                Triple("Discover", Icons.Default.Explore, Icons.Outlined.Explore),
                Triple("Connections", Icons.Default.Group, Icons.Outlined.Group),
                Triple("Marketplace", Icons.Default.Storefront, Icons.Outlined.Storefront),
                Triple("Profile", Icons.Default.AccountCircle, Icons.Outlined.AccountCircle)
            )

            menuItems.forEach { (label, filledIcon, outlinedIcon) ->
                val isActive = activeTab.lowercase() == label.lowercase()
                val tag = "tab_${label.lowercase()}"
                
                NavigationBarItem(
                    selected = isActive,
                    onClick = { onTabClick(label) },
                    modifier = Modifier
                        .testTag(tag)
                        .semantics {
                            contentDescription = "$label navigation tab, ${if (isActive) "selected" else "unselected"}"
                        },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (label == "Connections") {
                                    Badge(
                                        containerColor = BrandPrimary,
                                        contentColor = Color.White
                                    ) {
                                        Text("3", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            if (label == "Connections") {
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
                            text = if (label == "Marketplace") "Market" else label,
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
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
                    fontSize = 14.sp,
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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = dotColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = BrandTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = location,
                        fontSize = 12.sp,
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
            fontSize = 14.sp,
            color = BrandTextPrimary,
            lineHeight = 20.sp
        )
    }
}

// ============================================================================
// 7. ARTIST / TALENT CARD
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
@Composable
fun UnifiedStudioCard(
    name: String,
    location: String,
    price: String,
    rating: String,
    imageUrl: String?,
    onBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3F4F6))
            ) {
                AsyncImage(
                    model = imageUrl ?: "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=500&q=80",
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BrandTextPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Filled.Verified, contentDescription = "Verified Business", tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                    }
                    Text(location, fontSize = 14.sp, color = BrandTextSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(rating, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandTextPrimary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("From $price", fontWeight = FontWeight.ExtraBold, color = BrandPrimary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            UnifiedPrimaryButton(
                text = "Book Studio",
                onClick = onBook,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ============================================================================
// 9. MARKETPLACE CARD
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = BrandTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (audioDemo) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFECFDF5), RoundedCornerShape(4.dp))
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
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("•", color = BrandTextSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(location, fontSize = 12.sp, color = BrandTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = price,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
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
                        fontSize = 12.sp,
                        color = BrandTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
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
@Composable
fun UnifiedOpportunityCard(
    title: String,
    role: String,
    budget: String,
    location: String,
    deadline: String,
    applicantCount: Int,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BrandTextPrimary)
                Text(budget, fontWeight = FontWeight.ExtraBold, color = Color(0xFF10B981), fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Looking for: $role", fontSize = 14.sp, color = BrandTextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(location, fontSize = 13.sp, color = BrandTextSecondary)
                Text("Due: $deadline", fontSize = 13.sp, color = BrandTextSecondary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            UnifiedPrimaryButton(
                text = "Apply Now ($applicantCount Applied)",
                onClick = onApply,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ============================================================================
// 11. CLIENT REVIEW CARD
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorder, RoundedCornerShape(24.dp))
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
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(reviewerName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandTextPrimary)
                    Text(reviewerRole, fontSize = 12.sp, color = BrandTextSecondary)
                }
                Text(date, fontSize = 12.sp, color = BrandTextSecondary)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
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
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${rating.toInt()}.0 / 5.0",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = BrandTextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = reviewText,
                fontSize = 14.sp,
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
                        .clip(RoundedCornerShape(12.dp))
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
@Composable
fun AnimatedEqualizerWave(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barColor: Color = BrandPrimary,
    barWidth: androidx.compose.ui.unit.Dp = 3.dp,
    maxHeight: androidx.compose.ui.unit.Dp = 20.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizerTransition")
    
    val height1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val height2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )
    val height3 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )
    val height4 by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(480, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar4"
    )
    val height5 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar5"
    )

    val factors = if (isPlaying) listOf(height1, height2, height3, height4, height5) else listOf(0.2f, 0.4f, 0.3f, 0.2f, 0.3f)

    Row(
        modifier = modifier.height(maxHeight),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        factors.forEach { factor ->
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .fillMaxHeight(factor)
                    .clip(CircleShape)
                    .background(barColor)
            )
        }
    }
}

/**
 * Pulsing glow badge with animated aura scale and alpha.
 */
@Composable
fun PulsingGlowBadge(
    text: String,
    badgeColor: Color = Color(0xFF10B981),
    textColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseGlow")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraScale"
    )
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraAlpha"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Outer aura
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = auraScale
                    scaleY = auraScale
                    alpha = auraAlpha
                }
                .matchParentSize()
                .clip(RoundedCornerShape(12.dp))
                .background(badgeColor)
        )

        // Core badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(badgeColor)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
        }
    }
}

/**
 * Animated spring tactile interactive button for likes, bookmarks & actions.
 */
@Composable
fun InteractiveLikeButton(
    isLiked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    activeIcon: ImageVector = Icons.Default.Favorite,
    inactiveIcon: ImageVector = Icons.Outlined.FavoriteBorder,
    activeColor: Color = Color(0xFFEF4444),
    inactiveColor: Color = BrandTextSecondary
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.75f else if (isLiked) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
        label = "likeButtonScale"
    )

    IconButton(
        onClick = {
            isPressed = true
            onToggle()
        },
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Icon(
            imageVector = if (isLiked) activeIcon else inactiveIcon,
            contentDescription = "Toggle Like",
            tint = if (isLiked) activeColor else inactiveColor,
            modifier = Modifier.size(22.dp)
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

// ============================================================================
// 13. ERROR BOUNDARY & FALLBACK UI WRAPPER COMPONENTS
// ============================================================================

/**
 * Local Composition for triggering error boundary fallback state.
 */
val LocalScreenErrorHandler = staticCompositionLocalOf<(Throwable) -> Unit> { {} }

/**
 * ScreenErrorBoundary wraps Compose screens or content blocks to manage error states
 * and present a graceful fallback UI state instead of crashing during failures.
 */
@Composable
fun ScreenErrorBoundary(
    modifier: Modifier = Modifier,
    screenName: String = "Screen",
    isError: Boolean = false,
    errorMessage: String? = null,
    onRetry: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var caughtError by remember { mutableStateOf<Throwable?>(null) }

    val errorHandler: (Throwable) -> Unit = remember {
        { throwable -> caughtError = throwable }
    }

    if (isError || caughtError != null) {
        FallbackErrorScreen(
            screenName = screenName,
            errorMessage = errorMessage ?: caughtError?.localizedMessage ?: "An unexpected network or system error occurred.",
            onRetry = {
                caughtError = null
                onRetry?.invoke()
            },
            modifier = modifier
        )
    } else {
        CompositionLocalProvider(LocalScreenErrorHandler provides errorHandler) {
            Box(modifier = modifier) {
                content()
            }
        }
    }
}

/**
 * Graceful Fallback UI state with retry CTA and network error feedback.
 */
@Composable
fun FallbackErrorScreen(
    screenName: String,
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BrandBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animated Warning Aura
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "Error icon",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Temporary Connection Issue",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BrandTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We couldn't load content for $screenName. Please check your internet connection and try again.",
                    fontSize = 13.sp,
                    color = BrandTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onRetry,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BrandPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retry", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = onRetry,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("Reload Page", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

// ============================================================================
// 15. PERSISTENT GLOBAL MEDIA PLAYER BAR
// ============================================================================

/**
 * Global persistent media player bar that floats across screens when audio is active/paused.
 */
@Composable
fun GlobalPersistentAudioPlayerBar(
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isPlaying by com.example.onboarding.audio.AudioPlayerManager.isPlayingState.collectAsState()
    val trackTitle by com.example.onboarding.audio.AudioPlayerManager.currentTrackTitle.collectAsState()

    if (trackTitle != "No track loaded") {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1F2937), // Dark slate
            tonalElevation = 8.dp,
            shadowElevation = 10.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.MusicNote else Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = trackTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isPlaying) "Now Playing • Portfolio High-Res Audio" else "Paused",
                            fontSize = 10.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                com.example.onboarding.audio.AudioPlayerManager.pause()
                            } else {
                                com.example.onboarding.audio.AudioPlayerManager.play(
                                    context,
                                    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                                    trackTitle
                                )
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            com.example.onboarding.audio.AudioPlayerManager.stop()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}


/**
 * Skeleton loading screen for Discover & Search feed lists.
 */
@Composable
fun DiscoverFeedSkeletonLoader(
    count: Int = 3,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(count) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
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
                    
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SkeletonCircle(size = 44.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    SkeletonItem(width = 120.dp, height = 14.dp, cornerRadius = 4.dp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    SkeletonItem(width = 80.dp, height = 10.dp, cornerRadius = 4.dp)
                                }
                            }
                            SkeletonItem(width = 60.dp, height = 24.dp, cornerRadius = 12.dp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        SkeletonItem(width = 240.dp, height = 12.dp, cornerRadius = 4.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        SkeletonItem(width = 180.dp, height = 12.dp, cornerRadius = 4.dp)

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
@Composable
fun ChatFeedSkeletonLoader(
    count: Int = 5,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        repeat(count) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                SkeletonCircle(size = 52.dp)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SkeletonItem(width = 130.dp, height = 14.dp, cornerRadius = 4.dp)
                        SkeletonItem(width = 40.dp, height = 10.dp, cornerRadius = 4.dp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    SkeletonItem(width = 200.dp, height = 12.dp, cornerRadius = 4.dp)
                }
            }
            HorizontalDivider(color = Color(0xFFF3F4F6))
        }
    }
}

/**
 * Skeleton loading screen for Portfolio Tracks & Media Grid.
 */
@Composable
fun PortfolioGridSkeletonLoader(
    count: Int = 4,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(count) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shimmer()
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        SkeletonItem(width = 140.dp, height = 14.dp, cornerRadius = 4.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        SkeletonItem(width = 90.dp, height = 10.dp, cornerRadius = 4.dp)
                    }
                    SkeletonCircle(size = 32.dp)
                }
            }
        }
    }
}

/**
 * Skeleton loading screen for Notifications Center list.
 */
@Composable
fun NotificationListSkeletonLoader(
    count: Int = 4,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(count) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BrandBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SkeletonCircle(size = 38.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                SkeletonItem(width = 160.dp, height = 14.dp, cornerRadius = 4.dp)
                                Spacer(modifier = Modifier.height(6.dp))
                                SkeletonItem(width = 110.dp, height = 10.dp, cornerRadius = 4.dp)
                            }
                        }
                        SkeletonCircle(size = 12.dp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    SkeletonItem(width = 280.dp, height = 12.dp, cornerRadius = 4.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    SkeletonItem(width = 200.dp, height = 12.dp, cornerRadius = 4.dp)
                }
            }
        }
    }
}

/**
 * Reusable professional empty state component with custom vector illustration,
 * badge pill, descriptive typography, and action triggers.
 */
@Composable
fun IllustratedEmptyStateView(
    badgeText: String,
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    illustrationEmoji: String? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, BrandBorder),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Decorative Illustration Container with Ring Aura
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(110.dp)
            ) {
                // Outer ring
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary.copy(alpha = 0.06f))
                        .border(2.dp, BrandPrimary.copy(alpha = 0.15f), CircleShape)
                )
                // Inner ring
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (illustrationEmoji != null) {
                        Text(text = illustrationEmoji, fontSize = 38.sp)
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Badge Pill
            Surface(
                color = BrandPrimary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(99.dp)
            ) {
                Text(
                    text = badgeText.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrandTextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = description,
                fontSize = 13.sp,
                color = BrandTextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            if (primaryActionLabel != null && onPrimaryAction != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onPrimaryAction,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = primaryActionLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }

            if (secondaryActionLabel != null && onSecondaryAction != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onSecondaryAction) {
                    Text(
                        text = secondaryActionLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandTextSecondary
                    )
                }
            }
        }
    }
}

// ============================================================================
// ADDITIONAL UNIFIED COMPONENTS (PHASE 3 CONSOLIDATION)
// ============================================================================

@Composable
fun UnifiedVerificationBadge(
    modifier: Modifier = Modifier,
    label: String = "Verified Pro"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(BrandPrimary.copy(alpha = 0.08f), RoundedCornerShape(999.dp))
            .border(1.dp, BrandPrimary.copy(alpha = 0.2f), RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Verified",
            tint = BrandPrimary,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = BrandPrimary
        )
    }
}

@Composable
fun UnifiedStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "ACTIVE", "APPROVED", "CONFIRMED", "RELEASED", "AVAILABLE" -> Pair(BrandSuccess.copy(alpha = 0.1f), BrandSuccess)
        "PENDING", "IN REVIEW", "PROCESSING" -> Pair(Color(0xFFF59E0B).copy(alpha = 0.1f), Color(0xFFD97706))
        "LIVE", "URGENT" -> Pair(BrandPrimary.copy(alpha = 0.1f), BrandPrimary)
        else -> Pair(BrandBorder, BrandTextSecondary)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun UnifiedStarRating(
    rating: Double,
    reviewCount: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = Color(0xFFFFB300),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = String.format("%.1f", rating),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BrandTextPrimary
        )
        if (reviewCount != null) {
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "($reviewCount)",
                fontSize = 11.sp,
                color = BrandTextSecondary
            )
        }
    }
}

@Composable
fun UnifiedTrackCard(
    title: String,
    artistName: String,
    genre: String,
    duration: String,
    coverUrl: String?,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, BrandBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandDark)
                ) {
                    if (coverUrl != null) {
                        AsyncImage(
                            model = coverUrl,
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BrandDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$artistName • $genre",
                        fontSize = 12.sp,
                        color = BrandTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = duration,
                    fontSize = 12.sp,
                    color = BrandTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onPlayToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) BrandPrimary else BrandPrimary.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = if (isPlaying) Color.White else BrandPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UnifiedProjectCard(
    title: String,
    creatorName: String,
    genre: String,
    seekingRole: String,
    description: String,
    coverUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, BrandBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BrandDark)
                    ) {
                        if (coverUrl != null) {
                            AsyncImage(
                                model = coverUrl,
                                contentDescription = creatorName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
                        Text("By $creatorName • $genre", fontSize = 12.sp, color = BrandTextSecondary)
                    }
                }
                UnifiedStatusBadge(status = "Open Session")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                fontSize = 12.5.sp,
                color = BrandTextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                UnifiedChip(
                    text = "Seeking: $seekingRole",
                    selected = true,
                    onClick = {}
                )
                Text(
                    text = "Apply Now →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimary
                )
            }
        }
    }
}

@Composable
fun UnifiedBookingCard(
    title: String,
    subtitle: String,
    dateText: String,
    statusText: String,
    priceText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, BrandBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
                UnifiedStatusBadge(status = statusText)
            }

            Text(subtitle, fontSize = 12.sp, color = BrandTextSecondary)

            HorizontalDivider(color = BrandBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(dateText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BrandDark)
                Text(priceText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
            }
        }
    }
}

@Composable
fun UnifiedNotificationCard(
    title: String,
    description: String,
    timeAgo: String,
    isUnread: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (isUnread) BrandPrimary.copy(alpha = 0.04f) else Color.White),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isUnread) BrandPrimary.copy(alpha = 0.3f) else BrandBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isUnread) BrandPrimary else Color.Transparent)
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = BrandDark)
                    Text(timeAgo, fontSize = 11.sp, color = BrandTextSecondary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, fontSize = 12.sp, color = BrandTextSecondary, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
fun UnifiedBusinessHeader(
    title: String,
    subtitle: String,
    location: String,
    imageUrl: String?,
    rating: Double,
    reviewCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BrandDark),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    if (imageUrl != null) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Default.Business, contentDescription = null, tint = Color.White, modifier = Modifier.padding(12.dp))
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        UnifiedVerificationBadge(label = "Verified Studio")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(subtitle, fontSize = 12.5.sp, color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(location, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                UnifiedStarRating(rating = rating, reviewCount = reviewCount)
                Text("Instant Booking Available", fontSize = 11.sp, color = BrandAccent, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun UnifiedTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, BrandBorder)
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = selectedIndex == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = tab,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) BrandPrimary else BrandTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(2.5.dp)
                            .width(if (isSelected) 36.dp else 0.dp)
                            .background(BrandPrimary, RoundedCornerShape(99.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun WaveformAudioPlayer(
    title: String,
    durationText: String,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BrandBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onPlayToggle,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val barHeights = listOf(12, 24, 18, 30, 10, 22, 28, 14, 20, 32, 16, 26, 8, 18, 24, 12, 28, 16)
                    barHeights.forEachIndexed { i, h ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(h.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (i < 7) BrandPrimary else BrandBorder)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(durationText, fontSize = 11.sp, color = BrandTextSecondary, fontWeight = FontWeight.Bold)
        }
    }
}

// ============================================================================
// CONNECT BUTTON COMPONENT (FIRESTORE INTEGRATION)
// ============================================================================
@Composable
fun UnifiedConnectButton(
    targetUserId: String,
    targetUserName: String,
    modifier: Modifier = Modifier,
    initialStatus: String = "IDLE", // "IDLE", "PENDING", "CONNECTED"
    onStatusChange: (String) -> Unit = {},
    testTag: String = "connect_btn"
) {
    var status by remember(initialStatus) { mutableStateOf(initialStatus) }
    var isLoading by remember { mutableStateOf(false) }

    val (btnBg, contentColor, text, icon) = when (status) {
        "CONNECTED" -> Tuple4(BrandSuccess.copy(alpha = 0.12f), BrandSuccess, "Connected", Icons.Default.Check)
        "PENDING" -> Tuple4(BrandPrimary.copy(alpha = 0.12f), BrandPrimary, "Request Sent", Icons.Default.CheckCircle)
        else -> Tuple4(BrandPrimary, Color.White, "Connect", Icons.Default.PersonAdd)
    }

    Button(
        onClick = {
            if (status == "IDLE" && !isLoading) {
                isLoading = true
                FirebaseManager.sendConnectionRequest(
                    targetUserId = targetUserId,
                    targetUserName = targetUserName,
                    message = "Hi $targetUserName, let's collaborate on MusicLinkd!",
                    onSuccess = {
                        isLoading = false
                        status = "PENDING"
                        onStatusChange("PENDING")
                    },
                    onFailure = {
                        isLoading = false
                        // Still update UI gracefully
                        status = "PENDING"
                        onStatusChange("PENDING")
                    }
                )
            }
        },
        enabled = !isLoading && status == "IDLE",
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = btnBg,
            contentColor = contentColor,
            disabledContainerColor = btnBg,
            disabledContentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (status == "IDLE") 2.dp else 0.dp),
        modifier = modifier.testTag(testTag)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Sending...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        } else {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

// ============================================================================
// NETWORKING FEED UI COMPONENT (FIRESTORE DATA)
// ============================================================================
data class FeedPostItem(
    val id: String,
    val authorName: String,
    val authorRole: String,
    val authorAvatar: String?,
    val isVerified: Boolean,
    val content: String,
    val postType: String, // "RELEASE", "COLLAB_CALL", "STUDIO_UPDATE", "UPDATE"
    val timestampText: String,
    val likesCount: Int,
    val audioTitle: String? = null,
    val audioDuration: String? = null
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NetworkingFeed(
    modifier: Modifier = Modifier,
    onOpenProfile: (String) -> Unit = {}
) {
    var postsList by remember {
        mutableStateOf(
            listOf(
                FeedPostItem(
                    id = "feed_1",
                    authorName = "Rhea Kapoor",
                    authorRole = "Playback Vocalist & Topliner",
                    authorAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=300&q=80",
                    isVerified = true,
                    content = "🔥 Just published fresh acoustic STEMs for 'Sufi Echoes'! Seeking a synthwave or chillhop producer to record a remix track.",
                    postType = "COLLAB_CALL",
                    timestampText = "2 hrs ago",
                    likesCount = 24,
                    audioTitle = "Sufi Echoes - Dry Vocal Stem (WAV 24-bit)",
                    audioDuration = "3:15"
                ),
                FeedPostItem(
                    id = "feed_2",
                    authorName = "Acoustic Vault Studios",
                    authorRole = "Verified Recording Studio",
                    authorAvatar = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=300&q=80",
                    isVerified = true,
                    content = "🎙️ Live Room A upgrade complete! Installed Neumann U87 Ai and Solid State Logic (SSL) console. Booking slots open for weekend mixing sessions.",
                    postType = "STUDIO_UPDATE",
                    timestampText = "5 hrs ago",
                    likesCount = 42
                ),
                FeedPostItem(
                    id = "feed_3",
                    authorName = "Amit Trivedi",
                    authorRole = "Music Director & Composer",
                    authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=300&q=80",
                    isVerified = true,
                    content = "🎹 Finished final master bounce for 'Neon Dreamscape'. Big thanks to our session guitarist Rohan Sharma and mixing engineer Rahul!",
                    postType = "RELEASE",
                    timestampText = "1 day ago",
                    likesCount = 89,
                    audioTitle = "Neon Dreamscape - Official Master Bounce",
                    audioDuration = "4:02"
                )
            )
        )
    }

    var selectedFilter by remember { mutableStateOf("ALL") }
    var newPostText by remember { mutableStateOf("") }
    var isPosting by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        FirebaseManager.fetchNetworkingFeed(
            onSuccess = { fetched ->
                if (fetched.isNotEmpty()) {
                    val mapped = fetched.mapIndexed { idx, map ->
                        FeedPostItem(
                            id = "fs_post_$idx",
                            authorName = map["authorName"] as? String ?: "Musician Member",
                            authorRole = map["authorRole"] as? String ?: "Artist",
                            authorAvatar = map["mediaUrl"] as? String,
                            isVerified = true,
                            content = map["content"] as? String ?: "",
                            postType = map["postType"] as? String ?: "UPDATE",
                            timestampText = "Just now",
                            likesCount = (map["likesCount"] as? Number)?.toInt() ?: 0
                        )
                    }
                    postsList = mapped + postsList
                }
            },
            onFailure = { /* Fallback to initial seed list */ }
        )
    }

    val filteredPosts = remember(postsList, selectedFilter) {
        if (selectedFilter == "ALL") postsList
        else postsList.filter { it.postType == selectedFilter }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Create Post Base Card (24.dp radius)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Share Professional Update",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = BrandDark
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = newPostText,
                    onValueChange = { newPostText = it },
                    placeholder = { Text("Seeking collaborators, sharing new STEMs, or studio availability...", fontSize = 12.5.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BrandBorder
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        UnifiedChip(text = "🎵 STEMs", selected = false, onClick = {})
                        UnifiedChip(text = "🎙️ Studio", selected = false, onClick = {})
                    }
                    Button(
                        onClick = {
                            if (newPostText.isNotBlank() && !isPosting) {
                                isPosting = true
                                val userEmail = FirebaseManager.getCurrentUserEmail() ?: "artist@musiclinkd.com"
                                val userName = userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
                                FirebaseManager.createFeedPost(
                                    authorName = userName,
                                    authorRole = "Music Producer & Creator",
                                    content = newPostText,
                                    postType = "COLLAB_CALL",
                                    onSuccess = {
                                        postsList = listOf(
                                            FeedPostItem(
                                                id = "post_${System.currentTimeMillis()}",
                                                authorName = userName,
                                                authorRole = "Music Producer & Creator",
                                                authorAvatar = null,
                                                isVerified = true,
                                                content = newPostText,
                                                postType = "COLLAB_CALL",
                                                timestampText = "Just now",
                                                likesCount = 1
                                            )
                                        ) + postsList
                                        newPostText = ""
                                        isPosting = false
                                    },
                                    onFailure = {
                                        isPosting = false
                                    }
                                )
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        enabled = newPostText.isNotBlank() && !isPosting
                    ) {
                        Text("Post Update", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }
                }
            }
        }

        // Filter Bar Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("ALL" to "All Feed", "COLLAB_CALL" to "Calls", "RELEASE" to "Releases", "STUDIO_UPDATE" to "Studios")
            filters.forEach { (type, label) ->
                UnifiedChip(
                    text = label,
                    selected = selectedFilter == type,
                    onClick = { selectedFilter = type }
                )
            }
        }

        // Feed Items List
        filteredPosts.forEach { post ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenProfile(post.authorName) },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Author Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary.copy(alpha = 0.1f))
                            ) {
                                if (post.authorAvatar != null) {
                                    AsyncImage(
                                        model = post.authorAvatar,
                                        contentDescription = post.authorName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = BrandPrimary,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = post.authorName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = BrandDark
                                    )
                                    if (post.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified",
                                            tint = BrandPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${post.authorRole} • ${post.timestampText}",
                                    fontSize = 11.5.sp,
                                    color = BrandTextSecondary
                                )
                            }
                        }

                        // Connect Button Component
                        UnifiedConnectButton(
                            targetUserId = post.id,
                            targetUserName = post.authorName
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = post.content,
                        fontSize = 13.5.sp,
                        color = BrandDark,
                        lineHeight = 19.sp
                    )

                    if (post.audioTitle != null && post.audioDuration != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        WaveformAudioPlayer(
                            title = post.audioTitle,
                            durationText = post.audioDuration,
                            isPlaying = false,
                            onPlayToggle = {}
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BrandBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Like", tint = BrandTextSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${post.likesCount}", fontSize = 12.sp, color = BrandTextSecondary)
                        }
                        Text("Reply in Session →", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                    }
                }
            }
        }
    }
}

// ============================================================================
// GLOBAL SEARCH FEATURE TOP-BAR & SEARCH FLOW
// ============================================================================
@Composable
fun GlobalSearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        UnifiedSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            placeholder = "Search artists, tracks, or studios...",
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categories = listOf("All" to "All Network", "Artists" to "👨‍🎤 Artists", "Tracks" to "🎵 Tracks", "Studios" to "🎙️ Studios")
            categories.forEach { (key, label) ->
                UnifiedChip(
                    text = label,
                    selected = selectedCategory == key,
                    onClick = { onCategorySelected(key) }
                )
            }
        }
    }
}

// ============================================================================
// ARTIST PROFILE SCREEN (BASE CONTAINER CARDS & FIREBASE AUTH LINKING)
// ============================================================================
@OptIn(ExperimentalLayoutApi::class)
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Base Container (24.dp radius)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BrandDark),
            shape = RoundedCornerShape(24.dp)
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
                                Text(artistName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
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

                Spacer(modifier = Modifier.height(16.dp))

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
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Featured Portfolio Tracks", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
                Spacer(modifier = Modifier.height(12.dp))
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
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Specializations & Studio Gear", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BrandBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Media Attachments & STEMs", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
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
                        shape = RoundedCornerShape(12.dp),
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        images.forEach { imgItem ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(16.dp))
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
@Composable
fun UnifiedErrorCard(
    title: String = "Unable to load content",
    errorMessage: String = "A network error or connection timeout occurred while fetching updates.",
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    testTag: String = "unified_error_card"
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFEF2F2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error Alert",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = BrandDark,
                textAlign = TextAlign.Center
            )

            Text(
                text = errorMessage,
                fontSize = 12.5.sp,
                color = BrandTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            if (onRetry != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Retry Request", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            }
        }
    }
}

/**
 * Custom ErrorBoundary wrapper composable to catch UI exception states or network failures.
 */
@Composable
fun ErrorBoundary(
    hasError: Boolean,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (hasError) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            UnifiedErrorCard(
                title = "Something went wrong",
                errorMessage = errorMessage ?: "An unexpected issue occurred. Please check your network connection and try again.",
                onRetry = onRetry
            )
        }
    } else {
        content()
    }
}





