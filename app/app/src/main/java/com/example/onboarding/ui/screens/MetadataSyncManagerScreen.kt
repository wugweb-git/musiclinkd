package com.example.onboarding.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.onboarding.database.CustomMasterEntity
import com.example.onboarding.database.FirebaseManager
import com.example.onboarding.database.RepositoryProvider
import com.example.onboarding.database.UserProfileEntity
import com.example.onboarding.models.*
import com.example.onboarding.ui.components.UnifiedChip
import com.example.onboarding.viewmodel.OnboardingViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class MetadataCategory(
    val categoryKey: String,
    val displayName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    GENRES("genres", "Genres & Styles", Icons.Default.Tune),
    PERSON_TYPES("person_types", "User Roles", Icons.Default.Face),
    LANGUAGES("languages", "Languages", Icons.Default.Language),
    INSTRUMENTS("instruments", "Instruments & Gear", Icons.Default.Piano),
    PERSONA_BADGES("persona_badges", "Persona Badges", Icons.Default.Face),
    PROJECT_TYPES("project_types", "Project Types", Icons.Default.MusicNote),
    PROJECT_ROLES("project_roles", "Project Roles", Icons.Default.AssignmentInd)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetadataSyncManagerScreen(
    onBack: () -> Unit,
    onboardingViewModel: OnboardingViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val onboardingState by onboardingViewModel.state.collectAsStateWithLifecycle()

    // Retrieve database DAOs
    val customMasterDao = remember { RepositoryProvider.customMasterDao }
    val userProfileDao = remember { RepositoryProvider.userProfileDao }

    // State for local database custom master items
    val customMastersList by customMasterDao.getAllCustomMastersFlow()
        .collectAsStateWithLifecycle(initialValue = emptyList())

    // Active category tab state
    var activeCategory by remember { mutableStateOf(MetadataCategory.GENRES) }

    // Text inputs for adding custom masters
    var newMasterLabel by remember { mutableStateOf("") }
    var newMasterExtra by remember { mutableStateOf("") }
    var newMasterSortOrder by remember { mutableStateOf("") }

    // Sync status states
    var isSyncing by remember { mutableStateOf(false) }
    var lastSyncedTimestamp by remember { mutableStateOf<Long?>(null) }

    // Pull combined master items for selected category
    val currentCategoryMasters = remember(customMastersList, activeCategory) {
        val staticList: List<Pair<String, String>> = when (activeCategory) {
            MetadataCategory.GENRES -> availableGenres.map { it.id to it.name }
            MetadataCategory.PERSON_TYPES -> availableRoles.map { it.id to it.name }
            MetadataCategory.LANGUAGES -> availableLanguages.map { it.id to it.name }
            MetadataCategory.INSTRUMENTS -> availableInstruments.map { it.id to it.name }
            MetadataCategory.PERSONA_BADGES -> availableAvatars.map { it.name to it.name }
            else -> emptyList()
        }

        val dbForCat = customMastersList
            .filter { it.category == activeCategory.categoryKey }
            .sortedBy { it.sortOrder }

        if (dbForCat.isNotEmpty()) {
            dbForCat.map { it.itemKey to it.displayLabel }
        } else {
            staticList.distinctBy { it.first }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Metadata Sync & CRUD",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandDark
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = BrandDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandBackground
                )
            )
        },
        containerColor = BrandBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Core Synchronization Dashboard Panel
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrandBorder, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Sync",
                                tint = BrandPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = "Firestore Sync Hub",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BrandDark
                                    )
                                )
                                Text(
                                    text = "Masters & Mapping Persistence Controller",
                                    fontSize = 12.sp,
                                    color = BrandTextSecondary
                                )
                            }
                        }

                        Divider(color = BrandBorder)

                        // Real-time statuses
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Local Cache", fontSize = 11.sp, color = BrandTextSecondary)
                                Text(
                                    text = "Room SQLite (${customMastersList.size} Custom Items)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandDark
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Firebase Status", fontSize = 11.sp, color = BrandTextSecondary)
                                val isConnected = FirebaseManager.isFullyConnected()
                                Text(
                                    text = if (isConnected) "Fully Connected" else "Offline Emulator Mode",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isConnected) Color(0xFF10B981) else Color(0xFFF59E0B)
                                )
                            }
                        }

                        if (lastSyncedTimestamp != null) {
                            Text(
                                text = "Last Synced: ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(lastSyncedTimestamp!!))}",
                                fontSize = 11.sp,
                                color = BrandTextSecondary,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // PULL FROM FIRESTORE
                            Button(
                                onClick = {
                                    isSyncing = true
                                    coroutineScope.launch {
                                        val firestore = FirebaseManager.getFirestore()
                                        if (firestore != null) {
                                            val categoriesToSync = listOf(
                                                "person_types", "languages", "genres", "instruments",
                                                "persona_badges", "project_types", "project_roles",
                                                "booking_statuses", "studio_amenities", "collaboration_types", "trust_badges"
                                            )
                                            var pendingCats = categoriesToSync.size
                                            var hadError = false
                                            for (catId in categoriesToSync) {
                                                firestore.collection("taxonomy_categories").document(catId).collection("options")
                                                    .get()
                                                    .addOnSuccessListener { snapshot ->
                                                        coroutineScope.launch(Dispatchers.IO) {
                                                            try {
                                                                customMasterDao.deleteByCategory(catId)
                                                                for (doc in snapshot.documents) {
                                                                    val label = doc.getString("label") ?: ""
                                                                    val value = doc.getString("value") ?: ""
                                                                    val order = doc.getLong("sortOrder")?.toInt() ?: 0
                                                                    val extra = doc.getString("emoji") ?: doc.getString("extraData") ?: ""
                                                                    val active = doc.getBoolean("isActive") ?: true
                                                                    if (active && label.isNotEmpty() && value.isNotEmpty()) {
                                                                        customMasterDao.insertCustomMaster(
                                                                            CustomMasterEntity(
                                                                                category = catId,
                                                                                itemKey = value,
                                                                                displayLabel = label,
                                                                                extraData = extra,
                                                                                sortOrder = order
                                                                            )
                                                                        )
                                                                    }
                                                                }
                                                            } catch (e: Exception) {
                                                                hadError = true
                                                            } finally {
                                                                pendingCats--
                                                                if (pendingCats == 0) {
                                                                    lastSyncedTimestamp = System.currentTimeMillis()
                                                                    isSyncing = false
                                                                    withContext(Dispatchers.Main) {
                                                                        if (hadError) {
                                                                            Toast.makeText(context, "Completed taxonomy pull with errors", Toast.LENGTH_SHORT).show()
                                                                        } else {
                                                                            Toast.makeText(context, "Successfully pulled and merged all 11 taxonomy categories from Firestore!", Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    .addOnFailureListener { e ->
                                                        hadError = true
                                                        pendingCats--
                                                        if (pendingCats == 0) {
                                                            isSyncing = false
                                                            coroutineScope.launch(Dispatchers.Main) {
                                                                Toast.makeText(context, "Error fetching taxonomy from Firestore: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    }
                                            }
                                        } else {
                                            // Simulated sync pull offline
                                            delaySimulator(1000)
                                            isSyncing = false
                                            lastSyncedTimestamp = System.currentTimeMillis()
                                            Toast.makeText(context, "Offline Emulator: Refreshed local metadata lists", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                if (isSyncing) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                                } else {
                                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = "Pull", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pull Masters", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 2. Category Selector Tabs Row
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MetadataCategory.values()) { category ->
                        val isSelected = activeCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (isSelected) BrandPrimary else Color.White)
                                .border(1.dp, if (isSelected) BrandPrimary else BrandBorder, RoundedCornerShape(999.dp))
                                .clickable {
                                    activeCategory = category
                                    newMasterLabel = ""
                                    newMasterExtra = ""
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = category.displayName,
                                    tint = if (isSelected) Color.White else BrandDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = category.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else BrandDark
                                )
                            }
                        }
                    }
                }
            }

            // 3. Info Card on Taxonomy Read-Only status
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrandBorder, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FB))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Taxonomy Collection: Read-Only",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandDark
                        )
                        Text(
                            text = "Under the public-read, admin-write ruleset, new option additions and modifications for all 7 taxonomy categories must be made through the official admin panel at admin.musiclinkd.com. Regular users and this debug overlay are restricted to Pull/Read access to prevent data contamination.",
                            fontSize = 12.sp,
                            color = Color(0xFF5E5C73),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // 4. View Available Masters & Handle Deletion (Master CRUD - Read & Delete)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrandBorder, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Manage ${activeCategory.displayName} Master Options",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandDark
                        )

                        Divider(color = BrandBorder)

                        if (currentCategoryMasters.isEmpty()) {
                            Text(
                                "No options defined yet.",
                                fontSize = 12.sp,
                                color = BrandTextSecondary,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                currentCategoryMasters.forEach { (value, label) ->
                                    val customEntity = customMastersList.find {
                                        it.category == activeCategory.categoryKey && (it.itemKey == value || it.displayLabel == label)
                                    }
                                    val isCustom = customEntity != null

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(BrandBackground, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isCustom) Icons.Default.AddCircle else Icons.Default.VerifiedUser,
                                                contentDescription = "Indicator",
                                                tint = if (isCustom) BrandPrimary else Color(0xFF10B981),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = label,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = BrandDark
                                            )
                                        }

                                        if (isCustom) {
                                            IconButton(
                                                onClick = {
                                                    coroutineScope.launch(Dispatchers.IO) {
                                                        customMasterDao.deleteCustomMaster(
                                                            category = activeCategory.categoryKey,
                                                            displayLabel = label
                                                        )
                                                        withContext(Dispatchers.Main) {
                                                            Toast.makeText(context, "Custom master removed!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = Color.Red.copy(alpha = 0.7f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = "System",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. User Profile Mappings Configuration (User Mapping CRUD - Update)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrandBorder, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AssignmentInd,
                                contentDescription = "Mapping",
                                tint = BrandPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Profile Selections Mapping",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandDark
                            )
                        }

                        Text(
                            text = "Map custom or default metadata directly to your active collaborator identity card.",
                            fontSize = 11.sp,
                            color = BrandTextSecondary
                        )

                        Divider(color = BrandBorder)

                        // Render respective mappings editor
                        when (activeCategory) {
                            MetadataCategory.PERSON_TYPES -> {
                                Text("Primary Role Mapping Selection:", fontSize = 12.sp, color = BrandTextSecondary)
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    mainAxisSpacing = 8.dp,
                                    crossAxisSpacing = 8.dp
                                ) {
                                    currentCategoryMasters.forEach { (value, label) ->
                                        val isSelected = onboardingState.primaryRole == value || onboardingState.primaryRole == label
                                        UnifiedChip(
                                            text = label,
                                            selected = isSelected,
                                            onClick = {
                                                onboardingViewModel.selectPrimaryRole(value)
                                                Toast.makeText(context, "Primary role mapped to $label", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text("Secondary Role Mapping Selection:", fontSize = 12.sp, color = BrandTextSecondary)
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    mainAxisSpacing = 8.dp,
                                    crossAxisSpacing = 8.dp
                                ) {
                                    currentCategoryMasters.forEach { (value, label) ->
                                        val isSelected = onboardingState.secondaryRole == value || onboardingState.secondaryRole == label
                                        UnifiedChip(
                                            text = label,
                                            selected = isSelected,
                                            onClick = {
                                                onboardingViewModel.selectSecondaryRole(value)
                                                Toast.makeText(context, "Secondary role mapped to $label", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                            }

                            MetadataCategory.LANGUAGES -> {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    mainAxisSpacing = 8.dp,
                                    crossAxisSpacing = 8.dp
                                ) {
                                    currentCategoryMasters.forEach { (value, label) ->
                                        val isSelected = onboardingState.selectedLanguages.contains(value)
                                        UnifiedChip(
                                            text = label,
                                            selected = isSelected,
                                            onClick = {
                                                onboardingViewModel.toggleLanguage(value)
                                                // Sync back immediately to local profiles database
                                                coroutineScope.launch(Dispatchers.IO) {
                                                    val updatedLangs = if (isSelected) {
                                                        onboardingState.selectedLanguages - value
                                                     } else {
                                                        onboardingState.selectedLanguages + value
                                                     }
                                                     userProfileDao.getUserProfile()?.let { p ->
                                                         userProfileDao.saveUserProfile(
                                                             p.copy(languages = updatedLangs.joinToString(", "))
                                                         )
                                                     }
                                                 }
                                             }
                                         )
                                     }
                                }
                            }

                            MetadataCategory.GENRES -> {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    mainAxisSpacing = 8.dp,
                                    crossAxisSpacing = 8.dp
                                ) {
                                    currentCategoryMasters.forEach { (value, label) ->
                                        val isSelected = onboardingState.selectedGenres.contains(value)
                                        UnifiedChip(
                                            text = label,
                                            selected = isSelected,
                                            onClick = {
                                                onboardingViewModel.toggleGenre(value)
                                                coroutineScope.launch(Dispatchers.IO) {
                                                    val updatedGenres = if (isSelected) {
                                                        onboardingState.selectedGenres - value
                                                    } else {
                                                        onboardingState.selectedGenres + value
                                                    }
                                                    userProfileDao.getUserProfile()?.let { p ->
                                                        userProfileDao.saveUserProfile(
                                                            p.copy(genres = updatedGenres.joinToString(", "))
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            MetadataCategory.INSTRUMENTS -> {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    mainAxisSpacing = 8.dp,
                                    crossAxisSpacing = 8.dp
                                ) {
                                    currentCategoryMasters.forEach { (value, label) ->
                                        val isSelected = onboardingState.selectedInstruments.contains(value)
                                        UnifiedChip(
                                            text = label,
                                            selected = isSelected,
                                            onClick = {
                                                onboardingViewModel.toggleInstrument(value)
                                                coroutineScope.launch(Dispatchers.IO) {
                                                    val updatedInstruments = if (isSelected) {
                                                        onboardingState.selectedInstruments - value
                                                    } else {
                                                        onboardingState.selectedInstruments + value
                                                    }
                                                    userProfileDao.getUserProfile()?.let { p ->
                                                        userProfileDao.saveUserProfile(
                                                            p.copy(skills = (listOf(onboardingState.primaryRole, onboardingState.secondaryRole).filterNotNull() + updatedInstruments).joinToString(", "))
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            MetadataCategory.PERSONA_BADGES -> {
                                Text("Map Current Persona Avatar Badge:", fontSize = 12.sp, color = BrandTextSecondary)
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    itemsIndexed(availableAvatars) { idx, avatar ->
                                        val isSelected = onboardingState.selectedAvatarIndex == idx
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .clickable {
                                                    onboardingViewModel.updateProfile(
                                                        fullName = onboardingState.fullName,
                                                        displayName = onboardingState.displayName,
                                                        bioText = onboardingState.bio,
                                                        avatarIndex = idx,
                                                        profilePath = onboardingState.profilePicturePath,
                                                        coverPath = onboardingState.coverPicturePath
                                                    )
                                                    Toast.makeText(context, "Persona Badge updated to ${avatar.name}", Toast.LENGTH_SHORT).show()
                                                }
                                                .padding(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(avatar.colorHex))
                                                    .border(
                                                        width = if (isSelected) 3.dp else 1.dp,
                                                        color = if (isSelected) BrandPrimary else Color.Transparent,
                                                        shape = CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AsyncImage(
                                                    model = avatar.imageUrl,
                                                    contentDescription = avatar.name,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = avatar.name.substringBefore(" "),
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) BrandPrimary else BrandTextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .border(BorderStroke(1.dp, BrandBorder), RoundedCornerShape(24.dp))
                                        .background(Color.White)
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Mapping for ${activeCategory.displayName} is integrated globally.",
                                        fontSize = 12.sp,
                                        color = BrandTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }

        val rows = mutableListOf<MutableList<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentWidth = 0
        val mainAxisSpacingPx = mainAxisSpacing.roundToPx()
        val crossAxisSpacingPx = crossAxisSpacing.roundToPx()

        placeables.forEach { placeable ->
            val spacing = if (currentRow.isEmpty()) 0 else mainAxisSpacingPx
            if (currentWidth + spacing + placeable.width > constraints.maxWidth) {
                rows.add(currentRow)
                currentRow = mutableListOf(placeable)
                currentWidth = placeable.width
            } else {
                currentRow.add(placeable)
                currentWidth += spacing + placeable.width
            }
        }
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
        }

        var totalHeight = 0
        rows.forEachIndexed { index, row ->
            val rowHeight = row.maxOf { it.height }
            totalHeight += rowHeight
            if (index < rows.size - 1) {
                totalHeight += crossAxisSpacingPx
            }
        }

        layout(constraints.maxWidth, totalHeight) {
            var y = 0
            rows.forEach { row ->
                var x = 0
                val rowHeight = row.maxOf { it.height }
                row.forEach { placeable ->
                    placeable.placeRelative(x, y + (rowHeight - placeable.height) / 2)
                    x += placeable.width + mainAxisSpacingPx
                }
                y += rowHeight + crossAxisSpacingPx
            }
        }
    }
}

private suspend fun delaySimulator(timeMs: Long) {
    kotlinx.coroutines.delay(timeMs)
}

// Helper method to resolve dynamic custom additions for role lists in Onboarding selection screen
suspend fun queryCustomMastersForCategory(category: String): List<String> {
    return withContext(Dispatchers.IO) {
        RepositoryProvider.customMasterDao.getAllCustomMasters()
            .filter { it.category == category }
            .map { it.displayLabel }
    }
}
