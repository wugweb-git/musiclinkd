package com.example.onboarding.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onboarding.models.*
import com.example.onboarding.ui.components.MusicFilterChip
import com.example.onboarding.ui.components.EmptyState
import com.example.onboarding.ui.components.scale
import com.example.ui.theme.*

// SCREEN 4: ROLE SELECTION
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrimaryRoleScreen(
    selectedRole: String?,
    onRoleSelected: (String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredRoles = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            availableRoles
        } else {
            availableRoles.filter { 
                it.name.contains(searchQuery, ignoreCase = true) || 
                it.description.contains(searchQuery, ignoreCase = true) 
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "What best describes you?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandTextPrimary,
                letterSpacing = (-0.5).sp
            )
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Select your primary identity in the music industry",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = BrandTextSecondary,
                fontSize = 14.sp
            )
        )
        
        Spacer(modifier = Modifier.height(20.dp))

        // Search Field with Magnifying glass
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search roles (e.g. Producer, Label)", color = BrandTextSecondary, fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("role_search_bar"),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandTextSecondary) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BrandBorder
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grid Scrollable Area grouped by industry sections (Section 10)
        Box(modifier = Modifier.weight(1f)) {
            if (filteredRoles.isEmpty()) {
                EmptyState(
                    title = "No matching roles",
                    description = "We couldn't find any roles matching your search keyword. Try another term or scroll the grid below.",
                    emoji = "🔍"
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // pad for sticky bottom button
                ) {
                    val grouped = filteredRoles.groupBy { it.section }
                    grouped.forEach { (section, roles) ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Column(modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)) {
                                Text(
                                    text = section,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = BrandPrimary,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                HorizontalDivider(color = BrandBorder.copy(alpha = 0.5f))
                            }
                        }
                        
                        items(roles) { role ->
                            val isSelected = selectedRole == role.name
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(116.dp)
                                    .testTag("role_card_${role.id}")
                                    .scale(if (isSelected) 1.02f else 1f)
                                    .clickable { onRoleSelected(role.name) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) BrandPrimary.copy(alpha = 0.04f) else Color.White
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) BrandPrimary else BrandBorder
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (isSelected) 4.dp else 1.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) BrandPrimary else BrandBackground),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = role.icon,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else BrandPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(BrandPrimary),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = role.name,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) BrandPrimary else BrandTextPrimary,
                                            fontSize = 13.sp,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = role.description,
                                            color = BrandTextSecondary,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            lineHeight = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Sticky Bottom CTA Card overlay for premium fluid layout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, BrandBackground.copy(alpha = 0.95f), BrandBackground)
                        )
                    )
                    .padding(vertical = 16.dp)
            ) {
                Button(
                    onClick = onContinue,
                    enabled = selectedRole != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("role_primary_continue_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandPrimary,
                        disabledContainerColor = BrandBorder,
                        disabledContentColor = BrandTextSecondary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Continue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

// SCREEN 5: SECONDARY ROLE (OPTIONAL)
@Composable
fun SecondaryRoleScreen(
    primaryRole: String?,
    selectedRole: String?,
    onRoleSelected: (String) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Recommendations based on selected primary identity (Section 10)
    val recommendedSecondaryNames = remember(primaryRole) {
        primaryToSecondaryRolesMap[primaryRole] ?: emptyList()
    }

    // Exclude the already selected primary role
    val filteredRoles = remember(searchQuery, primaryRole) {
        val base = availableRoles.filter { it.name != primaryRole }
        if (searchQuery.isBlank()) {
            base
        } else {
            base.filter { 
                it.name.contains(searchQuery, ignoreCase = true) || 
                it.description.contains(searchQuery, ignoreCase = true) 
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Do you have another role?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandTextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Optional second area of expertise (Highly recommended for networking)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = BrandTextSecondary,
                        fontSize = 14.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search secondary role", color = BrandTextSecondary, fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("role_secondary_search_bar"),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandTextSecondary) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BrandBorder
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (filteredRoles.isEmpty()) {
                EmptyState(
                    title = "No matching roles",
                    description = "We couldn't find any secondary roles matching your search keyword. Try another term or scroll the grid below.",
                    emoji = "🔍"
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // 1. Recommended category list (Section 10)
                    if (searchQuery.isBlank() && recommendedSecondaryNames.isNotEmpty()) {
                        val recommendedRoles = filteredRoles.filter { recommendedSecondaryNames.contains(it.name) }
                        if (recommendedRoles.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                                    Text(
                                        text = "⚡ Recommended based on: $primaryRole",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = BrandSuccess,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    HorizontalDivider(color = BrandSuccess.copy(alpha = 0.25f))
                                }
                            }
                            items(recommendedRoles) { role ->
                                val isSelected = selectedRole == role.name
                                SecondaryRoleCard(role, isSelected) { onRoleSelected(role.name) }
                            }
                        }
                    }

                    // 2. All other remaining roles
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)) {
                            Text(
                                text = if (searchQuery.isBlank() && recommendedSecondaryNames.isNotEmpty()) "All Other Roles" else "Available Secondary Roles",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = BrandTextPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider(color = BrandBorder.copy(alpha = 0.5f))
                        }
                    }

                    val remainingRoles = if (searchQuery.isBlank() && recommendedSecondaryNames.isNotEmpty()) {
                        filteredRoles.filter { !recommendedSecondaryNames.contains(it.name) }
                    } else {
                        filteredRoles
                    }

                    items(remainingRoles) { role ->
                        val isSelected = selectedRole == role.name
                        SecondaryRoleCard(role, isSelected) { onRoleSelected(role.name) }
                    }
                }
            }

            // Bottom CTA Overlay with Skip possibility
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, BrandBackground.copy(alpha = 0.95f), BrandBackground)
                        )
                    )
                    .padding(vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onSkip,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("role_secondary_skip_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, BrandBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandTextPrimary)
                    ) {
                        Text("Skip", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Button(
                        onClick = onContinue,
                        enabled = selectedRole != null,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(56.dp)
                            .testTag("role_secondary_continue_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandPrimary,
                            disabledContainerColor = BrandBorder,
                            disabledContentColor = BrandTextSecondary
                        )
                    ) {
                        Text("Continue", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SecondaryRoleCard(role: RoleItem, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(116.dp)
            .testTag("role_sec_card_${role.id}")
            .scale(if (isSelected) 1.02f else 1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BrandPrimary.copy(alpha = 0.04f) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) BrandPrimary else BrandBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) BrandPrimary else BrandBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = role.icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else BrandPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BrandPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Column {
                Text(
                    text = role.name,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) BrandPrimary else BrandTextPrimary,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(
                    text = role.description,
                    color = BrandTextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    lineHeight = 12.sp
                )
            }
        }
    }
}


// SCREEN 6: LANGUAGES SCREEN
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LanguagesScreen(
    selectedLanguages: Set<String>,
    onLangToggled: (String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Languages You Work In",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandTextPrimary,
                    letterSpacing = (-0.5).sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Select up to 3 regional or international languages in which you vocalize, write, or direct projects. Written in native form. (Selected: ${selectedLanguages.size}/3)",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (selectedLanguages.size == 3) BrandPrimary else BrandTextSecondary,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Multi-select Flow chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                availableLanguages.forEach { lang ->
                    val isSelected = selectedLanguages.contains(lang)
                    MusicFilterChip(
                        text = lang,
                        selected = isSelected,
                        onClick = { onLangToggled(lang) },
                        testTag = "lang_chip_$lang"
                    )
                }
            }
        }

        Column {
            if (error != null) {
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )
            }

            // Action CTA
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("language_continue_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimary,
                    disabledContainerColor = BrandBorder,
                    disabledContentColor = BrandTextSecondary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
            }
        }
    }
}

// SCREEN 7: GENRES SCREEN
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenresScreen(
    selectedGenres: Set<String>,
    onGenreToggled: (String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Genres & Styles",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandTextPrimary,
                    letterSpacing = (-0.5).sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Choose up to 3 sonic styles. This determines opportunity recommendations in your feed. (Selected: ${selectedGenres.size}/3)",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (selectedGenres.size == 3) BrandPrimary else BrandTextSecondary,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Flow Row of multi-select filter chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                availableGenres.forEach { genre ->
                    val isSelected = selectedGenres.contains(genre)
                    MusicFilterChip(
                        text = genre,
                        selected = isSelected,
                        onClick = { onGenreToggled(genre) },
                        testTag = "genre_chip_$genre"
                    )
                }
            }
        }

        Column {
            if (error != null) {
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )
            }

            // Action CTA
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("genre_continue_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimary,
                    disabledContainerColor = BrandBorder,
                    disabledContentColor = BrandTextSecondary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
            }
        }
    }
}

// NEW SCREEN: INSTRUMENTS AND PROFICIENCY SCREEN
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InstrumentsScreen(
    selectedInstruments: Set<String>,
    instrumentProficiency: Map<String, SkillProficiency>,
    onInstrumentToggled: (String) -> Unit,
    onUpdateProficiency: (String, String, String, String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Instruments & Gear",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandTextPrimary,
                    letterSpacing = (-0.5).sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Select up to 2 instruments you perform with, and specify your experience or provide links to verify your credentials. (Selected: ${selectedInstruments.size}/2)",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (selectedInstruments.size == 2) BrandPrimary else BrandTextSecondary,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Instruments multi-select flow row
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                availableInstruments.forEach { item ->
                    val isSelected = selectedInstruments.contains(item.name)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onInstrumentToggled(item.name) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.emoji, modifier = Modifier.padding(end = 4.dp))
                                Text(item.name)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary.copy(alpha = 0.08f),
                            selectedLabelColor = BrandPrimary,
                            selectedLeadingIconColor = BrandPrimary,
                            containerColor = Color.White,
                            labelColor = BrandTextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            selectedBorderColor = BrandPrimary,
                            borderColor = BrandBorder,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 2.dp
                        ),
                        shape = RoundedCornerShape(99.dp),
                        modifier = Modifier.testTag("instrument_chip_${item.name.replace(" ", "_")}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Proficiency Mapping for each selected instrument
            if (selectedInstruments.isNotEmpty()) {
                Text(
                    text = "Map Experience & Proof",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Providing links builds instant trust with prospective hirers.",
                    style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
                )
                Spacer(modifier = Modifier.height(12.dp))

                selectedInstruments.forEach { instrumentName ->
                    val prof = instrumentProficiency[instrumentName] ?: SkillProficiency()

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BrandBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Title with instrument emoji if matched
                            val emoji = availableInstruments.find { it.name == instrumentName }?.emoji ?: "🎸"
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(emoji, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                                Text(
                                    text = instrumentName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = BrandTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Experience Years Selection Row
                            Text(
                                text = "Years of Experience",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = BrandTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val expOptions = listOf("1-2 Years", "3-5 Years", "5-10 Years", "10+ Years")
                                expOptions.forEach { opt ->
                                    val isSel = prof.yearsOfExperience == opt
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) BrandPrimary else BrandBackground)
                                            .clickable {
                                                onUpdateProficiency(
                                                    instrumentName,
                                                    opt,
                                                    prof.proficiencyLevel,
                                                    prof.portfolioProofUrl
                                                )
                                            }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = opt.replace(" Years", ""),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isSel) Color.White else BrandTextPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Proficiency Level Selection Row
                            Text(
                                text = "Proficiency Level",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = BrandTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val levelOptions = listOf("Beginner", "Intermediate", "Advanced", "Expert")
                                levelOptions.forEach { opt ->
                                    val isSel = prof.proficiencyLevel == opt
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) BrandPrimary else BrandBackground)
                                            .clickable {
                                                onUpdateProficiency(
                                                    instrumentName,
                                                    prof.yearsOfExperience,
                                                    opt,
                                                    prof.portfolioProofUrl
                                                )
                                            }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = opt,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = if (isSel) Color.White else BrandTextPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Verification / Portfoilio link input field
                            OutlinedTextField(
                                value = prof.portfolioProofUrl,
                                onValueChange = { link ->
                                    onUpdateProficiency(
                                        instrumentName,
                                        prof.yearsOfExperience,
                                        prof.proficiencyLevel,
                                        link
                                    )
                                },
                                label = { Text("Proof / Portfolio URL (Optional)", fontSize = 11.sp) },
                                placeholder = { Text("e.g. Spotify, YouTube, SoundCloud link", fontSize = 12.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("instrument_proof_${instrumentName.replace(" ", "_")}"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = BrandBackground,
                                    unfocusedContainerColor = BrandBackground,
                                    focusedBorderColor = BrandPrimary,
                                    unfocusedBorderColor = BrandBorder
                                )
                            )
                        }
                    }
                }
            } else {
                EmptyState(
                    title = "No instruments selected",
                    description = "Choose an instrument from above if you perform on stage or record tracks. Alternatively, you can skip to continue.",
                    emoji = "🎹"
                )
            }
            Spacer(modifier = Modifier.height(100.dp)) // padding for bottom button
        }

        // Sticky Bottom button container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, BrandBackground.copy(alpha = 0.95f), BrandBackground)
                    )
                )
                .padding(vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // If they don't play instruments, they can skip this step entirely!
                OutlinedButton(
                    onClick = onContinue, // skips or confirms empty instrument set
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("instruments_skip_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BrandBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandTextPrimary)
                ) {
                    Text(
                        text = if (selectedInstruments.isEmpty()) "Skip" else "Skip Experience",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = onContinue,
                    enabled = selectedInstruments.isNotEmpty(),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(56.dp)
                        .testTag("instruments_continue_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandPrimary,
                        disabledContainerColor = BrandBorder,
                        disabledContentColor = BrandTextSecondary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Confirm Instruments",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
