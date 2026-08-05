package com.example.onboarding.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onboarding.ai.CollaboratorRecommendation
import com.example.onboarding.ai.GeminiRecommendationEngine
import com.example.onboarding.database.FirebaseManager
import com.example.onboarding.database.RepositoryProvider
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch

private val BrandPrimary = Color(0xFFEA4C89)
private val BrandDark = Color(0xFF1E293B)
private val BrandBackground = Color(0xFFFAFAFA)
private val BrandCardBg = Color(0xFFFFFFFF)
private val BrandBorder = Color(0xFFF1F5F9)
private val BrandSecondaryText = Color(0xFF64748B)

// Data Models for Connections
data class FollowRequestItem(
    val id: String,
    val name: String,
    val role: String,
    val location: String,
    val mutualCount: Int,
    val avatarEmoji: String,
    val tags: List<String>,
    val timestamp: String = "Today"
)

data class ActiveCollaboratorItem(
    val id: String,
    val name: String,
    val role: String,
    val location: String,
    val status: String, // "In Studio", "Online", "Touring"
    val avatarEmoji: String,
    val primaryInstrument: String,
    val verified: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ConnectionsFlow(
    showSnackbar: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Requests, 1: Collaborators, 2: AI Match
    var isLoadingAi by remember { mutableStateOf(false) }

    // Follow Requests State
    val followRequestsList = remember {
        mutableStateListOf(
            FollowRequestItem("req_1", "Karan Saxena", "Foley & Audio Engineer", "Mumbai, MH", 14, "🎙️", listOf("Foley", "Atmos", "ProTools")),
            FollowRequestItem("req_2", "Rhea Kapoor", "Sufi & Playback Singer", "Delhi, NCR", 8, "🎤", listOf("Sufi", "Classical", "Playback"))
        )
    }

    // Active Collaborators State
    val activeCollaboratorsList = remember {
        mutableStateListOf(
            ActiveCollaboratorItem("collab_1", "Amit Trivedi", "Music Director & Composer", "Juhu, Mumbai", "In Studio", "🎹", "Synthesizers"),
            ActiveCollaboratorItem("collab_2", "Pooja Singhal", "Lead Vocalist & Songwriter", "Andheri West", "Online", "🎙️", "Vocals"),
            ActiveCollaboratorItem("collab_3", "Rohan Sharma", "Session Guitarist & Arranger", "Bandra, Mumbai", "Touring", "🎸", "Electric Guitar"),
            ActiveCollaboratorItem("collab_4", "Maya Lin", "EDM Producer & Mix Engineer", "Pune, MH", "Online", "🎛️", "Logic Pro X")
        )
    }

    // Gemini Recommendations
    var aiRecommendations by remember { mutableStateOf<List<CollaboratorRecommendation>>(emptyList()) }

    LaunchedEffect(Unit) {
        isLoadingAi = true
        scope.launch {
            try {
                val dbProfile = RepositoryProvider.userProfileDao.getUserProfile()
                val userGenres = dbProfile?.genres ?: "Indie Rock, Bollywood, Synthwave"
                val userSkills = dbProfile?.skills ?: "Music Producer, Synthesizer"
                
                val recs = GeminiRecommendationEngine.fetchCollaboratorRecommendations(userGenres, userSkills)
                aiRecommendations = recs
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingAi = false
            }
        }
    }

    // Helper to persist connection state in Firestore
    fun updateFirestoreConnection(id: String, name: String, status: String) {
        try {
            val db = FirebaseManager.getFirestore()
            if (db != null) {
                val data = hashMapOf(
                    "connectionId" to id,
                    "targetName" to name,
                    "status" to status,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
                db.collection("connections").document(id)
                    .set(data, SetOptions.merge())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Hero Stats Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BrandPrimary.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = BrandPrimary,
                            shape = CircleShape,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.GroupAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Musician Network Registry",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BrandPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Manage follow requests, active studio collaborators & AI matches.",
                        fontSize = 11.5.sp,
                        color = BrandSecondaryText
                    )
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BrandBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("${activeCollaboratorsList.size}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandDark)
                        Text("Active", fontSize = 10.sp, color = BrandSecondaryText)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrandCardBg, RoundedCornerShape(14.dp))
                .border(1.dp, BrandBorder, RoundedCornerShape(14.dp))
                .padding(4.dp)
        ) {
            val tabs = listOf(
                "Requests (${followRequestsList.size})" to Icons.Default.PersonAdd,
                "Collaborators (${activeCollaboratorsList.size})" to Icons.Default.Groups,
                "Gemini AI" to Icons.Default.AutoAwesome
            )

            tabs.forEachIndexed { index, (label, icon) ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) BrandPrimary else Color.Transparent)
                        .clickable { selectedTab = index }
                        .padding(vertical = 10.dp)
                        .testTag("connections_tab_$index"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else BrandSecondaryText,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = label,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else BrandDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Content Display
        when (selectedTab) {
            0 -> {
                // TAB 0: PENDING FOLLOW REQUESTS
                if (followRequestsList.isEmpty()) {
                    com.example.onboarding.ui.components.IllustratedEmptyStateView(
                        badgeText = "Follow Requests",
                        title = "No Pending Follow Requests",
                        description = "You've answered all incoming connection invitations! Explore the global directory to follow new sound engineers, vocalists, or studio owners.",
                        icon = Icons.Default.CheckCircleOutline,
                        illustrationEmoji = "🤝",
                        primaryActionLabel = "Explore Global Registry",
                        onPrimaryAction = { showSnackbar("Opening Global Musician Registry...") }
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                text = "Pending Network Requests",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = BrandDark
                            )
                        }

                        items(followRequestsList, key = { it.id }) { req ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = BrandCardBg),
                                border = BorderStroke(1.dp, BrandBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("follow_request_card_${req.id}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(BrandPrimary.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(req.avatarEmoji, fontSize = 22.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(req.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
                                            Text(req.role, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = BrandPrimary)
                                            Text("${req.location} • ${req.mutualCount} mutual connections", fontSize = 11.sp, color = BrandSecondaryText)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        req.tags.forEach { tag ->
                                            Surface(
                                                color = BrandBorder.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = tag,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = BrandDark,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Action Buttons: ACCEPT & DENY
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                followRequestsList.remove(req)
                                                updateFirestoreConnection(req.id, req.name, "REJECTED")
                                                Toast.makeText(context, "Declined request from ${req.name}", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                                .testTag("deny_request_btn_${req.id}")
                                        ) {
                                            Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Decline", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                followRequestsList.remove(req)
                                                activeCollaboratorsList.add(
                                                    0,
                                                    ActiveCollaboratorItem(
                                                        id = req.id,
                                                        name = req.name,
                                                        role = req.role,
                                                        location = req.location,
                                                        status = "Online",
                                                        avatarEmoji = req.avatarEmoji,
                                                        primaryInstrument = req.tags.firstOrNull() ?: "Musician"
                                                    )
                                                )
                                                updateFirestoreConnection(req.id, req.name, "ACCEPTED")
                                                Toast.makeText(context, "Accepted ${req.name}! Added to Active Collaborators.", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                                .testTag("accept_request_btn_${req.id}")
                                        ) {
                                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Accept & Follow", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: ACTIVE COLLABORATORS LIST
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = "Connected Musician Partners (${activeCollaboratorsList.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BrandDark
                        )
                    }

                    items(activeCollaboratorsList, key = { it.id }) { collab ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BrandCardBg),
                            border = BorderStroke(1.dp, BrandBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(BrandPrimary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(collab.avatarEmoji, fontSize = 22.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(collab.name, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = BrandDark)
                                        if (collab.verified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.CheckCircle, null, tint = BrandPrimary, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Text(collab.role, fontSize = 11.5.sp, color = BrandPrimary, fontWeight = FontWeight.Medium)
                                    Text("${collab.location} • ${collab.primaryInstrument}", fontSize = 11.sp, color = BrandSecondaryText)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    // Status pill
                                    Surface(
                                        color = when (collab.status) {
                                            "In Studio" -> Color(0xFFDCFCE7)
                                            "Online" -> Color(0xFFDBEAFE)
                                            else -> Color(0xFFF3F4F6)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = collab.status,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (collab.status) {
                                                "In Studio" -> Color(0xFF15803D)
                                                "Online" -> Color(0xFF1D4ED8)
                                                else -> Color(0xFF4B5563)
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = { showSnackbar("Opening direct chat with ${collab.name}...") },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Icon(Icons.Default.Chat, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Direct Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // TAB 2: GEMINI AI COLLABORATOR MATCHMAKER
                if (isLoadingAi) {
                    com.example.onboarding.ui.components.PortfolioGridSkeletonLoader(count = 3)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F3FF)),
                                border = BorderStroke(1.dp, Color(0xFFDDD6FE)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFF7C3AED), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Gemini AI Matchmaker Active", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF5B21B6))
                                        Text("Analyzed your genre tags & instrument skills against the global musician database.", fontSize = 11.sp, color = Color(0xFF6B21A8))
                                    }
                                }
                            }
                        }

                        items(aiRecommendations) { rec ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = BrandCardBg),
                                border = BorderStroke(1.dp, BrandBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF7C3AED).copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(rec.avatarEmoji, fontSize = 22.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(rec.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandDark)
                                            Text(rec.role, fontSize = 12.sp, color = Color(0xFF7C3AED), fontWeight = FontWeight.Medium)
                                            Text(rec.location, fontSize = 11.sp, color = BrandSecondaryText)
                                        }

                                        // Match Score Pill
                                        Surface(
                                            color = Color(0xFF7C3AED),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("${rec.matchScore}% AI Match", color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Project Idea Highlight
                                    Surface(
                                        color = Color(0xFFF9FAFB),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("💡 AI Suggested Project:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                                            Text(rec.projectIdea, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = BrandPrimary)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(rec.synergyReason, fontSize = 10.5.sp, color = BrandSecondaryText, lineHeight = 15.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            updateFirestoreConnection("ai_collab_${rec.name.lowercase().replace(" ", "_")}", rec.name, "REQUEST_SENT")
                                            Toast.makeText(context, "Sent AI Collaboration invitation to ${rec.name}!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                        modifier = Modifier.fillMaxWidth().height(38.dp)
                                    ) {
                                        Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Send Collaboration Request", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
