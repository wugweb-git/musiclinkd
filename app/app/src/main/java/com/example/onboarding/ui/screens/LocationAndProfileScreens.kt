package com.example.onboarding.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onboarding.models.*
import com.example.onboarding.ui.components.M3InputField
import com.example.onboarding.ui.components.scale
import com.example.onboarding.ui.components.BrandIcon
import com.example.ui.theme.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.alpha
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

// SCREEN 8: LOCATION SCREEN
@Composable
fun LocationScreen(
    country: String,
    state: String,
    city: String,
    area: String,
    onLocationChange: (state: String, city: String, area: String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    stateError: String? = null,
    cityError: String? = null
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var isLocating by remember { mutableStateOf(false) }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            isLocating = true
            onLocationChange("Maharashtra", "Mumbai", "Bandra West")
            Toast.makeText(context, "Location permission granted. Auto-detected Mumbai, Bandra West!", Toast.LENGTH_LONG).show()
            isLocating = false
        } else {
            Toast.makeText(context, "Location permission denied. Please enter your location details manually.", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Where are you based?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandTextPrimary,
                    letterSpacing = (-0.5).sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Connect with local session artists, studios, and hiring opportunities near you.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = BrandTextSecondary,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 1. Country (Pre-filled Locked)
            Text(
                text = "Country",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandTextPrimary
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, BrandBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🇮🇳", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "India",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = BrandTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Auto-detect button with editorial styling
            OutlinedButton(
                onClick = {
                    locationLauncher.launch(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BrandPrimary
                ),
                border = BorderStroke(1.5.dp, BrandPrimary.copy(alpha = 0.5f))
            ) {
                if (isLocating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = BrandPrimary, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Auto-detecting GPS...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Auto-detect My Location", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. State Input
            M3InputField(
                value = state,
                onValueChange = { onLocationChange(it, city, area) },
                placeholder = "e.g. Maharashtra",
                label = "State",
                leadingIcon = { Icon(Icons.Outlined.Explore, contentDescription = null, tint = BrandPrimary) },
                error = stateError,
                testTag = "location_state_input"
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 3. City Input
            M3InputField(
                value = city,
                onValueChange = { onLocationChange(state, it, area) },
                placeholder = "e.g. Mumbai",
                label = "City",
                leadingIcon = { Icon(Icons.Outlined.MusicNote, contentDescription = null, tint = BrandPrimary) },
                error = cityError,
                testTag = "location_city_input"
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Area Input (Optional)
            M3InputField(
                value = area,
                onValueChange = { onLocationChange(state, city, it) },
                placeholder = "e.g. Andheri West",
                label = "Area / Locality (Optional)",
                leadingIcon = { Icon(Icons.Outlined.Face, contentDescription = null, tint = BrandPrimary) },
                imeAction = ImeAction.Done,
                testTag = "location_area_input"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "* Leaving fields blank will auto-populate with Mumbai, MH for test review.",
                fontSize = 11.sp,
                color = BrandTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Continue CTA
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("location_continue_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPrimary,
                contentColor = Color.White
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

// SCREEN 9: PROFILE CREATION SCREEN
@Composable
fun ProfileCreationScreen(
    fullName: String,
    displayName: String,
    bio: String,
    selectedAvatarIndex: Int,
    roleExperience: SkillProficiency,
    networkingLinks: Map<String, String>,
    onProfileChange: (fullName: String, displayName: String, bio: String, avatarIndex: Int, profilePath: String?, coverPath: String?) -> Unit,
    onUpdateRoleExperience: (yearsOfExperience: String, proficiencyLevel: String, proofUrl: String) -> Unit,
    onUpdateNetworkingLink: (platform: String, url: String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    nameError: String? = null,
    usernameError: String? = null,
    bioError: String? = null,
    proofError: String? = null,
    linksErrors: Map<String, String> = emptyMap(),
    profilePicturePath: String? = null,
    coverPicturePath: String? = null,
    isSaving: Boolean = false
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val profilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val copiedPath = copyUriToLocalFile(context, uri, "profile")
            if (copiedPath != null) {
                onProfileChange(fullName, displayName, bio, selectedAvatarIndex, copiedPath, coverPicturePath)
            }
        }
    }

    val coverPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val copiedPath = copyUriToLocalFile(context, uri, "cover")
            if (copiedPath != null) {
                onProfileChange(fullName, displayName, bio, selectedAvatarIndex, profilePicturePath, copiedPath)
            }
        }
    }

    var pendingPickerType by remember { mutableStateOf<String?>(null) }
    var showHeadshotOptions by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            try {
                val headshotFile = java.io.File(context.cacheDir, "headshot_${System.currentTimeMillis()}.jpg")
                java.io.FileOutputStream(headshotFile).use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, out)
                }
                onProfileChange(fullName, displayName, bio, selectedAvatarIndex, headshotFile.absolutePath, coverPicturePath)
                Toast.makeText(context, "Headshot captured!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to save photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission is required to capture headshot", Toast.LENGTH_SHORT).show()
        }
    }

    val mediaPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Media permission granted!", Toast.LENGTH_SHORT).show()
            if (pendingPickerType == "profile") {
                profilePicker.launch("image/*")
            } else if (pendingPickerType == "cover") {
                coverPicker.launch("image/*")
            }
        } else {
            Toast.makeText(context, "Using system photo library picker fallback.", Toast.LENGTH_SHORT).show()
            if (pendingPickerType == "profile") {
                profilePicker.launch("image/*")
            } else if (pendingPickerType == "cover") {
                coverPicker.launch("image/*")
            }
        }
        pendingPickerType = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(BrandBackground)
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Describe Yourself",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandTextPrimary,
                    letterSpacing = (-0.5).sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Introduce yourself to studios, directors, and bands.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = BrandTextSecondary,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 1. Sleek Avatar Selector Ring
            Text(
                text = "Choose Your Persona Badge",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandTextPrimary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(availableAvatars) { index, avatar ->
                    val isSelected = selectedAvatarIndex == index
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .testTag("avatar_node_$index")
                            .scale(if (isSelected) 1.08f else 1f)
                            .clickable {
                                onProfileChange(fullName, displayName, bio, index, profilePicturePath, coverPicturePath)
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
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
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text(
                            text = avatar.name.substringBefore(" "),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) BrandPrimary else BrandTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Or Upload Custom Media",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandTextPrimary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Photo
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, BrandBorder, RoundedCornerShape(16.dp))
                        .clickable {
                            showHeadshotOptions = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (profilePicturePath != null) {
                        AsyncImage(
                            model = File(profilePicturePath),
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, null, tint = BrandPrimary, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Upload Headshot", fontSize = 11.sp, color = BrandTextSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (showHeadshotOptions) {
                    AlertDialog(
                        onDismissRequest = { showHeadshotOptions = false },
                        title = { Text("Profile Headshot", fontWeight = FontWeight.Bold) },
                        text = { Text("Capture a new headshot with your camera or pick an image from gallery:") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showHeadshotOptions = false
                                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                                }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.size(18.dp))
                                    Text("Camera", fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showHeadshotOptions = false
                                    pendingPickerType = "profile"
                                    val perm = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                        android.Manifest.permission.READ_MEDIA_IMAGES
                                    } else {
                                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                                    }
                                    mediaPermissionLauncher.launch(perm)
                                }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(18.dp))
                                    Text("Gallery", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    )
                }

                // Cover Banner
                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .height(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, BrandBorder, RoundedCornerShape(16.dp))
                        .clickable {
                            pendingPickerType = "cover"
                            val perm = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                android.Manifest.permission.READ_MEDIA_IMAGES
                            } else {
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                            }
                            mediaPermissionLauncher.launch(perm)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (coverPicturePath != null) {
                        AsyncImage(
                            model = File(coverPicturePath),
                            contentDescription = "Cover Banner",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PhotoCamera, null, tint = BrandPrimary, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Upload Cover Banner", fontSize = 11.sp, color = BrandTextSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Full Name Field
            M3InputField(
                value = fullName,
                onValueChange = { onProfileChange(it, displayName, bio, selectedAvatarIndex, profilePicturePath, coverPicturePath) },
                placeholder = "e.g. Vedanshu Srivastava",
                label = "Full Name",
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = BrandPrimary) },
                error = nameError,
                testTag = "profile_name_input"
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Unique Display Name (Used in URL!)
            M3InputField(
                value = displayName,
                onValueChange = { onProfileChange(fullName, it.filter { c -> c.isLetterOrDigit() || c == '_' }.lowercase(), bio, selectedAvatarIndex, profilePicturePath, coverPicturePath) },
                placeholder = "e.g. vedanshu",
                label = "Display Handle",
                leadingIcon = { Icon(Icons.Outlined.Face, contentDescription = null, tint = BrandPrimary) },
                error = usernameError,
                testTag = "profile_username_input"
            )
            
            // Dynamic URL Handle Preview
            Text(
                text = "Your public web card: musiclinkd.com/${if (displayName.isEmpty()) "username" else displayName}",
                fontSize = 12.sp,
                color = BrandPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp, bottom = 18.dp, start = 4.dp)
            )

            // 4. Short Bio with live counter
            Text(
                text = "Short Bio",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BrandTextPrimary
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            
            OutlinedTextField(
                value = bio,
                onValueChange = { 
                    if (it.length <= 150) {
                        onProfileChange(fullName, displayName, it, selectedAvatarIndex, profilePicturePath, coverPicturePath)
                    }
                },
                isError = bioError != null,
                placeholder = { 
                    Text(
                        text = "Briefly share your musical background, instruments, or past records...",
                        color = BrandTextSecondary,
                        fontSize = 14.sp
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .testTag("profile_bio_input"),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = BrandBorder,
                    errorBorderColor = Color.Red
                )
            )
            
            // Character counter and error row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (bioError != null) {
                    Text(
                        text = bioError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                Text(
                    text = "${bio.length}/150",
                    color = if (bio.length >= 135) BrandPrimary else BrandTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = if (bio.length >= 135) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Professional Experience & Proof Mapping (Section 10)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💼 Experience & Verification",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BrandTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Map your proficiency for your primary role to unlock premium listings.",
                        style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                            val isSel = roleExperience.yearsOfExperience == opt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) BrandPrimary else BrandBackground)
                                    .clickable {
                                        onUpdateRoleExperience(
                                            opt,
                                            roleExperience.proficiencyLevel,
                                            roleExperience.portfolioProofUrl
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
                            val isSel = roleExperience.proficiencyLevel == opt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) BrandPrimary else BrandBackground)
                                    .clickable {
                                        onUpdateRoleExperience(
                                            roleExperience.yearsOfExperience,
                                            opt,
                                            roleExperience.portfolioProofUrl
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

                    OutlinedTextField(
                        value = roleExperience.portfolioProofUrl,
                        onValueChange = { link ->
                            onUpdateRoleExperience(
                                roleExperience.yearsOfExperience,
                                roleExperience.proficiencyLevel,
                                link
                            )
                        },
                        isError = proofError != null,
                        label = { Text("Primary Role Proof URL (Optional)", fontSize = 11.sp) },
                        placeholder = { Text("e.g. Spotify, YouTube, SoundCloud, LinkedIn", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("role_proof_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BrandBackground,
                            unfocusedContainerColor = BrandBackground,
                            focusedBorderColor = BrandPrimary,
                            unfocusedBorderColor = BrandBorder,
                            errorBorderColor = Color.Red
                        )
                    )
                    if (proofError != null) {
                        Text(
                            text = proofError,
                            color = Color.Red,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Networking & Social Links (Section 10)
            Text(
                text = "🌐 Networking & Social Links",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = BrandTextPrimary,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
            )
            Text(
                text = "Add links to external platforms to showcase your portfolio, music releases, and socials.",
                style = MaterialTheme.typography.bodySmall.copy(color = BrandTextSecondary),
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )

            val platforms = listOf("Spotify", "YouTube", "SoundCloud", "LinkedIn", "Instagram")
            platforms.forEach { platform ->
                val linkVal = networkingLinks[platform] ?: ""
                val platErr = linksErrors[platform]
                OutlinedTextField(
                    value = linkVal,
                    onValueChange = { url ->
                        onUpdateNetworkingLink(platform, url)
                    },
                    isError = platErr != null,
                    label = { Text("$platform Profile URL", fontSize = 11.sp) },
                    placeholder = { Text("e.g. https://${platform.lowercase()}.com/your-username", fontSize = 12.sp) },
                    leadingIcon = {
                        BrandIcon(
                            platform = platform,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(24.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("network_link_${platform.lowercase()}"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BrandBorder,
                        errorBorderColor = Color.Red
                    )
                )
                if (platErr != null) {
                    Text(
                        text = platErr,
                        color = Color.Red,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Continue CTA
        Button(
            onClick = onContinue,
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("profile_continue_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPrimary,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "Create Profile",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
            }
        }
    }
    }

    if (isSaving) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) {}, // Scrim input absorber
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .padding(32.dp)
                    .widthIn(max = 320.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = BrandPrimary,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Publishing Identity...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B) // Slate Dark
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Syncing your secure professional profile, assets, and portfolio links.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B) // Slate Light
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun copyUriToLocalFile(context: android.content.Context, uri: android.net.Uri, fileNamePrefix: String): String? {
    return try {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val file = java.io.File(context.filesDir, "${fileNamePrefix}_${System.currentTimeMillis()}.jpg")
        val outputStream = java.io.FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
