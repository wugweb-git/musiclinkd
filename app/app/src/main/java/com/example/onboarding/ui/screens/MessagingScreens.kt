package com.example.onboarding.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.onboarding.viewmodel.MessagingViewModel

// --------------------------------------------------------------------------------------
// COLOR TOKENS (SPECIFIED BY FLOW 5 DESIGN SYSTEM)
// --------------------------------------------------------------------------------------
private val BrandPrimary = Color(0xFFEA4C89)      // Primary Pink (#EA4C89)
private val BrandAccent = Color(0xFFEA4C89)       // Accent Pink (#EA4C89)
private val BrandBackground = Color(0xFFFFFFFF)   // Background (#FFFFFF)
private val BrandHeadline = Color(0xFF0D0B26)     // Headline Navy (#0D0B26)
private val BrandSecondary = Color(0xFF7D7D8E)    // Secondary Grey (#7D7D8E)
private val BrandBorder = Color(0xFFECECF1)       // Border Light (#ECECF1)
private val BrandSuccess = Color(0xFF10B981)      // Green Success
private val BrandCardBackground = Color(0xFFFFFFFF)

// --------------------------------------------------------------------------------------
// MOCK DATA STRUCTURES FOR FLOW 5
// --------------------------------------------------------------------------------------
enum class ChatContext {
    STUDIO,
    MARKETPLACE,
    OPPORTUNITY,
    PROJECT
}

data class ConvoItem(
    val id: String,
    val name: String,
    val role: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val isVerified: Boolean = true,
    val context: ChatContext,
    val contextStatus: String,
    val isPinned: Boolean = false,
    val avatarUrl: String = ""
)

data class MessageBubbleItem(
    val id: String,
    val sender: String,
    val text: String,
    val time: String,
    val isMe: Boolean,
    val type: String = "text", // text, portfolio, project, opportunity, marketplace, booking, quote, audio, image, document
    val metaTitle: String = "",
    val metaDesc: String = "",
    val metaPrice: String = "",
    val metaStatus: String = ""
)

data class CallLogItem(
    val callerName: String,
    val type: String, // Audio, Video
    val time: String,
    val status: String // Missed, Completed, Scheduled
)

data class SharedFileItem(
    val name: String,
    val size: String,
    val type: String, // PDF, WAV, JPG, PNG
    val date: String
)

// --------------------------------------------------------------------------------------
// MAIN FLOW ENTRY POINT
// --------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Flow5MessagingWorkspace(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MessagingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val convos by viewModel.conversations.collectAsStateWithLifecycle()
    val activeMessages by viewModel.activeMessages.collectAsStateWithLifecycle()
    val selectedConvoId by viewModel.selectedConvoId.collectAsStateWithLifecycle()

    var currentSubScreen by remember { mutableStateOf("HOME") }
    var filterActive by remember { mutableStateOf("All") }
    var searchText by remember { mutableStateOf("") }
    var showEmptyState by remember { mutableStateOf(false) }

    // Call Log data
    var callLogs by remember {
        mutableStateOf(
            listOf(
                CallLogItem("SoundOne Studios", "Audio", "Today, 10:05 AM", "Missed"),
                CallLogItem("Aria Vox", "Video", "Yesterday, 4:15 PM", "Completed"),
                CallLogItem("Netflix India Music", "Audio", "Scheduled for July 2, 3:00 PM", "Scheduled"),
                CallLogItem("Kabir (Band)", "Video", "June 25, 6:00 PM", "Completed")
            )
        )
    }

    // Shared Files list
    var sharedFiles by remember {
        mutableStateOf(
            listOf(
                SharedFileItem("Vocals_Stem_Dry_T4.wav", "128 MB", "WAV", "Jun 24, 2026"),
                SharedFileItem("Studio_Booking_Invoice_Y9.pdf", "1.2 MB", "PDF", "Jun 12, 2026"),
                SharedFileItem("Gibson_Fretboard_CloseUp.jpg", "4.6 MB", "JPG", "Yesterday"),
                SharedFileItem("NDA_Netflix_Originals.pdf", "890 KB", "PDF", "Jun 26, 2026"),
                SharedFileItem("Arrangement_Draft_Track1.mp3", "8.4 MB", "MP3", "Jun 20, 2026")
            )
        )
    }

    // Settings for notifications
    var isMutedMap by remember { mutableStateOf(mapOf<String, Boolean>()) }

    // Navigation and screen management
    Crossfade(targetState = currentSubScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            "HOME" -> {
                MessagesHomeScreen(
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    filterActive = filterActive,
                    onFilterSelect = { filterActive = it },
                    convos = convos.filter {
                        // Search filter
                        val matchesSearch = it.name.contains(searchText, ignoreCase = true) || 
                                              it.role.contains(searchText, ignoreCase = true) ||
                                              it.lastMessage.contains(searchText, ignoreCase = true)
                        
                        // Tab filters
                        val matchesFilter = when (filterActive) {
                            "All" -> true
                            "Unread" -> it.unreadCount > 0
                            "Bookings" -> it.context == ChatContext.STUDIO
                            "Marketplace" -> it.context == ChatContext.MARKETPLACE
                            "Projects" -> it.context == ChatContext.PROJECT
                            "Opportunities" -> it.context == ChatContext.OPPORTUNITY
                            "Pinned" -> it.isPinned
                            else -> true
                        }
                        matchesSearch && matchesFilter
                    },
                    isPinnedMap = convos.associate { it.id to it.isPinned },
                    isMutedMap = isMutedMap,
                    onConvoSelect = { id ->
                        viewModel.selectConversation(id)
                        currentSubScreen = "CHAT"
                    },
                    onToggleEmptyState = { showEmptyState = !showEmptyState },
                    showEmptyState = showEmptyState,
                    onViewCalls = { currentSubScreen = "CALL_HISTORY" },
                    onViewFiles = { currentSubScreen = "FILES" },
                    onBackClick = onBack
                )
            }
            "CHAT" -> {
                val activeConvo = convos.firstOrNull { it.id == selectedConvoId } ?: convos.firstOrNull() ?: ConvoItem(
                    id = "studio_1",
                    name = "SoundOne Studios",
                    role = "Premium Mixing Suite",
                    lastMessage = "",
                    timestamp = "10:24 AM",
                    unreadCount = 0,
                    context = ChatContext.STUDIO,
                    contextStatus = "Booking Confirmed"
                )
                
                ChatScreen(
                    convo = activeConvo,
                    messages = activeMessages,
                    isMuted = isMutedMap[activeConvo.id] ?: false,
                    isPinned = activeConvo.isPinned,
                    onBackClick = { 
                        viewModel.deselectConversation()
                        currentSubScreen = "HOME" 
                    },
                    onSendMessage = { text, type, title, desc, price, status ->
                        viewModel.sendMessage(text, type, title, desc, price, status)
                    },
                    onUpdateConvoStatus = { status ->
                        viewModel.updateConvoStatus(status)
                    },
                    onTogglePin = {
                        viewModel.togglePin(activeConvo.id)
                    },
                    onToggleMute = {
                        val current = isMutedMap[activeConvo.id] ?: false
                        isMutedMap = isMutedMap + (activeConvo.id to !current)
                    },
                    onDeleteChat = {
                        viewModel.deleteChat(activeConvo.id)
                        currentSubScreen = "HOME"
                    },
                    onViewCallScreen = { currentSubScreen = "CALL_HISTORY" },
                    onViewFilesScreen = { currentSubScreen = "FILES" }
                )
            }
            "CALL_HISTORY" -> {
                CallHistoryScreen(
                    callLogs = callLogs,
                    onBackClick = { currentSubScreen = "HOME" },
                    onJoinCall = { log ->
                        // Simulate call launch
                    },
                    onScheduleCall = { name, time, type ->
                        val newLog = CallLogItem(name, type, time, "Scheduled")
                        callLogs = listOf(newLog) + callLogs
                    }
                )
            }
            "FILES" -> {
                FilesAndMediaScreen(
                    files = sharedFiles,
                    onBackClick = { currentSubScreen = "HOME" },
                    onSearchFiles = { query ->
                        // Simulate search
                    },
                    onDownloadFile = {
                        // Simulate download
                    }
                )
            }
        }
    }
}

// ======================================================================================
// SCREEN 1: MESSAGES HOME (INBOX LIST VIEW)
// ======================================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesHomeScreen(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    filterActive: String,
    onFilterSelect: (String) -> Unit,
    convos: List<ConvoItem>,
    isPinnedMap: Map<String, Boolean>,
    isMutedMap: Map<String, Boolean>,
    onConvoSelect: (String) -> Unit,
    onToggleEmptyState: () -> Unit,
    showEmptyState: Boolean,
    onViewCalls: () -> Unit,
    onViewFiles: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandBackground)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(40.dp).background(BrandBorder.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BrandHeadline)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onViewCalls,
                            modifier = Modifier.size(40.dp).background(BrandBorder.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call History", tint = BrandPrimary)
                        }
                        IconButton(
                            onClick = onViewFiles,
                            modifier = Modifier.size(40.dp).background(BrandBorder.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = "Files Library", tint = BrandPrimary)
                        }
                        IconButton(
                            onClick = onToggleEmptyState,
                            modifier = Modifier.size(40.dp).background(
                                if (showEmptyState) BrandAccent.copy(alpha = 0.15f) else BrandBorder.copy(alpha = 0.5f),
                                CircleShape
                            )
                        ) {
                            Icon(
                                imageVector = if (showEmptyState) Icons.Default.Inbox else Icons.Outlined.Inbox,
                                contentDescription = "Toggle Empty State View",
                                tint = if (showEmptyState) BrandAccent else BrandSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // LARGE EDITORIAL DISPLAY HEADER
                Text(
                    text = "Messages",
                    fontSize = 38.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandHeadline,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "Your conversations, bookings and collaborations.",
                    fontSize = 14.sp,
                    color = BrandSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // SEARCH BAR - PILL DESIGN
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchTextChange,
                    placeholder = { Text("Search people, studios, projects...", color = BrandSecondary, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(99.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = BrandBorder.copy(alpha = 0.3f),
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BrandBorder,
                        focusedTextColor = BrandHeadline,
                        unfocusedTextColor = BrandHeadline
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // FILTER CHIPS (SLIDABLE ROW)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    val filters = listOf("All", "Unread", "Bookings", "Marketplace", "Projects", "Opportunities", "Pinned")
                    items(filters) { item ->
                        val isSelected = filterActive == item
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(if (isSelected) BrandPrimary else Color.Transparent)
                                .border(1.dp, if (isSelected) BrandPrimary else BrandBorder, RoundedCornerShape(99.dp))
                                .clickable { onFilterSelect(item) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = item,
                                color = if (isSelected) Color.White else BrandHeadline,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        containerColor = BrandBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showEmptyState || convos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    com.example.onboarding.ui.components.IllustratedEmptyStateView(
                        badgeText = "Direct Messaging",
                        title = "No Active Conversations",
                        description = "Start discovering elite musicians, bands, recording studios, and live gig opportunities on the network to start collaborating.",
                        icon = Icons.Default.ChatBubbleOutline,
                        illustrationEmoji = "💬",
                        primaryActionLabel = "Discover Network Talent",
                        onPrimaryAction = onBackClick
                    )
                }
            } else {
                // CONVERSATION CARD LIST
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp, start = 20.dp, end = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(convos) { item ->
                        val isPinned = isPinnedMap[item.id] == true
                        val isMuted = isMutedMap[item.id] == true
                        
                        Card(
                            onClick = { onConvoSelect(item.id) },
                            colors = CardDefaults.cardColors(containerColor = BrandCardBackground),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, if (item.unreadCount > 0) BrandPrimary.copy(alpha = 0.4f) else BrandBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(if (item.unreadCount > 0) 4.dp else 0.dp, RoundedCornerShape(24.dp))
                                .testTag("convo_card_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // AVATAR WITH ONLINE AND STATUS BADGE
                                Box(modifier = Modifier.size(54.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(
                                                when (item.context) {
                                                    ChatContext.STUDIO -> Color(0xFFEFF6FF)
                                                    ChatContext.MARKETPLACE -> Color(0xFFFDF2F8)
                                                    ChatContext.OPPORTUNITY -> Color(0xFFF0FDF4)
                                                    ChatContext.PROJECT -> Color(0xFFFAF5FF)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = item.name.take(2).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = when (item.context) {
                                                ChatContext.STUDIO -> BrandPrimary
                                                ChatContext.MARKETPLACE -> BrandAccent
                                                ChatContext.OPPORTUNITY -> BrandSuccess
                                                ChatContext.PROJECT -> Color(0xFF8B5CF6)
                                            }
                                        )
                                    }
                                    
                                    // ONLINE INDICATOR DOT
                                    Box(
                                        modifier = Modifier
                                            .size(13.dp)
                                            .background(Color.White, CircleShape)
                                            .align(Alignment.BottomEnd)
                                            .padding(1.5.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(BrandSuccess, CircleShape)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // CONVERSATION INFO
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = item.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = BrandHeadline
                                            )
                                            if (item.isVerified) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Verified,
                                                    contentDescription = "Verified Profile",
                                                    tint = BrandPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            if (isPinned) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(
                                                    imageVector = Icons.Default.PushPin,
                                                    contentDescription = "Pinned",
                                                    tint = BrandAccent,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                            if (isMuted) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(
                                                    imageVector = Icons.Default.VolumeMute,
                                                    contentDescription = "Muted",
                                                    tint = BrandSecondary,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = item.timestamp,
                                            fontSize = 11.sp,
                                            color = if (item.unreadCount > 0) BrandPrimary else BrandSecondary,
                                            fontWeight = if (item.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.role,
                                            fontSize = 12.sp,
                                            color = BrandPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        // CONTEXT TAG LABEL (E.g. Studio, Marketplace)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    when (item.context) {
                                                        ChatContext.STUDIO -> Color(0xFFEFF6FF)
                                                        ChatContext.MARKETPLACE -> Color(0xFFFDF2F8)
                                                        ChatContext.OPPORTUNITY -> Color(0xFFF0FDF4)
                                                        ChatContext.PROJECT -> Color(0xFFFAF5FF)
                                                    }
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = item.contextStatus.uppercase(),
                                                color = when (item.context) {
                                                    ChatContext.STUDIO -> BrandPrimary
                                                    ChatContext.MARKETPLACE -> BrandAccent
                                                    ChatContext.OPPORTUNITY -> BrandSuccess
                                                    ChatContext.PROJECT -> Color(0xFF8B5CF6)
                                                },
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    Text(
                                        text = item.lastMessage,
                                        fontSize = 13.sp,
                                        color = if (item.unreadCount > 0) BrandHeadline else BrandSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = if (item.unreadCount > 0) FontWeight.Medium else FontWeight.Normal
                                    )
                                }

                                if (item.unreadCount > 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(BrandPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = item.unreadCount.toString(),
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
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
}

// ======================================================================================
// SCREEN 2: CHAT SCREEN WITH ADAPTIVE HEADER
// ======================================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    convo: ConvoItem,
    messages: List<MessageBubbleItem>,
    isMuted: Boolean,
    isPinned: Boolean,
    onBackClick: () -> Unit,
    onSendMessage: (String, String, String, String, String, String) -> Unit,
    onUpdateConvoStatus: (String) -> Unit,
    onTogglePin: () -> Unit,
    onToggleMute: () -> Unit,
    onDeleteChat: () -> Unit,
    onViewCallScreen: () -> Unit,
    onViewFilesScreen: () -> Unit
) {
    var textMessage by remember { mutableStateOf("") }
    var activeQuotePrice by remember { mutableStateOf("₹15,000") }
    var showQuoteCreator by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var activeOfferValue by remember { mutableStateOf("₹1,15,000") }
    
    // Auto keyboard focus
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(BrandBackground)) {
                // 1. HEADER BAR with profile details
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BrandHeadline)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // AVATAR
                    Box(modifier = Modifier.size(44.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(BrandPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(convo.name.take(2).uppercase(), color = BrandPrimary, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .background(Color.White, CircleShape)
                                .align(Alignment.BottomEnd)
                                .padding(1.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize().background(BrandSuccess, CircleShape))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(convo.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandHeadline)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, "Verified", tint = BrandPrimary, modifier = Modifier.size(15.dp))
                        }
                        Text(convo.role, fontSize = 11.5.sp, color = BrandSecondary)
                    }

                    // CALL & VIDEO & SETTINGS ACTION TRIGGERS
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = onViewCallScreen) {
                            Icon(Icons.Default.Call, contentDescription = "Audio Call", tint = BrandPrimary)
                        }
                        IconButton(onClick = onViewCallScreen) {
                            Icon(Icons.Default.VideoCall, contentDescription = "Video Call", tint = BrandPrimary)
                        }
                        IconButton(onClick = { showSettingsSheet = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Settings", tint = BrandHeadline)
                        }
                    }
                }

                // 2. ADAPTIVE HEADER COMPONENT (Differentiated based on ChatContext)
                // Instantly changes the workspace layout and header to fit workflow
                Divider(color = BrandBorder, modifier = Modifier.fillMaxWidth())
                AdaptiveContextBanner(
                    context = convo.context,
                    convoStatus = convo.contextStatus,
                    offerValue = activeOfferValue,
                    onAcceptOffer = {
                        onUpdateConvoStatus("Completed Deal")
                        activeOfferValue = "₹1,15,000 [SOLD]"
                        onSendMessage("⚡ Deal Finalized! Shipping contract and warranty locked.", "text", "", "", "", "")
                    },
                    onDeclineOffer = {
                        onUpdateConvoStatus("Decline Offer")
                        onSendMessage("❌ Counter offer declined. Please re-adjust quote pricing.", "text", "", "", "", "")
                    },
                    onRescheduleBooking = {
                        onUpdateConvoStatus("Booking Adjusted")
                        onSendMessage("🗓️ Rescheduled Studio Booking requested for July 14, 2:00 PM.", "text", "", "", "", "")
                    },
                    onAttachPortfolio = {
                        showShareSheet = true
                    }
                )
                Divider(color = BrandBorder, modifier = Modifier.fillMaxWidth())
                
                // 3. PINNED MESSAGES BANNER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrandBorder.copy(alpha = 0.25f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PushPin, contentDescription = "Pinned Message", tint = BrandAccent, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pinned: Dynamic Booking agreement finalized on June 26, 2026. Review workspace parameters.",
                        fontSize = 11.5.sp,
                        color = BrandHeadline,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                Divider(color = BrandBorder, modifier = Modifier.fillMaxWidth())
            }
        },
        containerColor = BrandBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // MESSAGE LIST CHAT AREA
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Date Separator
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TODAY, JUNE 28",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    items(messages) { msg ->
                        MessageBubble(
                            msg = msg,
                            onPlayAudio = {
                                // Interactive audio toggle
                            }
                        )
                    }

                    // Typing Indicator (Simulated visual feedback)
                    item {
                        Row(
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary.copy(alpha = 0.6f))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary.copy(alpha = 0.3f))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Typing...", fontSize = 11.sp, color = BrandSecondary)
                        }
                    }
                }

                // 4. SMART QUICK ACTIONS ROW
                Divider(color = BrandBorder)
                SmartQuickActionsRow(
                    context = convo.context,
                    onAction = { actionName ->
                        when (actionName) {
                            "Book Studio" -> {
                                onSendMessage("🗓️ Booking request sent for Studio tracking session.", "booking", "Recording Suite A", "July 12, 2:00 PM (4 hours)", "₹12,000", "PENDING")
                            }
                            "Pay Deposit" -> {
                                onSendMessage("💰 Paid booking security deposit of ₹3,000 for SoundOne Studios.", "text", "", "", "", "")
                            }
                            "View Calendar" -> {
                                onUpdateConvoStatus("Reviewing Dates")
                            }
                            "Make Offer" -> {
                                onSendMessage("🤝 Sent counter-negotiation offer of ₹1,10,000.", "text", "", "", "", "")
                            }
                            "Buy Now" -> {
                                onSendMessage("⚡ Direct payment transaction executed. Total paid ₹1,15,000.", "text", "", "", "", "")
                            }
                            "Hire" -> {
                                onSendMessage("🎉 Job Offer finalized for vocal arrangement.", "text", "", "", "", "")
                            }
                            "Invite to Project" -> {
                                onSendMessage("🎸 Invited Aria to collaborate on 'Midnight Indie' EP.", "project", "Midnight Indie", "Indie arrangements", "July 30", "ACTIVE")
                            }
                            "View Portfolio" -> {
                                onSendMessage("📂 Shared client-facing professional portfolio.", "portfolio", "Cabir's Ambient Showcase", "12 audio stems, 2 Grammy credentials", "", "VIEW")
                            }
                            "Submit Portfolio" -> {
                                onSendMessage("📂 Submitted professional PDF resume & high-fidelity audio tracks.", "portfolio", "Soundtrack Reels", "3 heavy ambient tracks", "", "SUBMITTED")
                            }
                            "Update Availability" -> {
                                onSendMessage("⏰ Availability status updated: Free weekdays 2 PM - 6 PM.", "text", "", "", "", "")
                            }
                        }
                    }
                )
                Divider(color = BrandBorder)

                // 5. PREMIUM AI ASSIST FEATURES (Suggested Replies, Recap, Summarize)
                AIAssistTray(
                    onSelectReply = { reply ->
                        textMessage = reply
                    },
                    onSummarize = {
                        onSendMessage("🤖 AI Workspace Recap:\n- SoundOne Studio hold secured for July 12, 2 PM.\n- Deposit invoice INV-2026 is UNPAID.\n- Next action: Submit secure payment via header.", "text", "", "", "", "")
                    },
                    onGenerateQuote = {
                        showQuoteCreator = true
                    }
                )
                Divider(color = BrandBorder)

                // 6. BOTTOM COMPOSER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ATTACH BUTTON (+) -> Triggers Screen 3 Portfolio Sheet
                    IconButton(
                        onClick = { showShareSheet = true },
                        modifier = Modifier.size(40.dp).background(BrandBorder.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Share Portfolio / Assets", tint = BrandPrimary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { onViewFilesScreen() },
                        modifier = Modifier.size(40.dp).background(BrandBorder.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = BrandSecondary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // TEXT COMPOSER INPUT
                    OutlinedTextField(
                        value = textMessage,
                        onValueChange = { textMessage = it },
                        placeholder = { Text("Write your message...", color = BrandSecondary, fontSize = 13.5.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(max = 120.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = BrandBorder.copy(alpha = 0.3f),
                            focusedBorderColor = BrandPrimary,
                            unfocusedBorderColor = BrandBorder,
                            focusedTextColor = BrandHeadline,
                            unfocusedTextColor = BrandHeadline
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // SEND BUTTON
                    IconButton(
                        onClick = {
                            if (textMessage.isNotBlank()) {
                                onSendMessage(textMessage, "text", "", "", "", "")
                                textMessage = ""
                                focusManager.clearFocus()
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    // SCREEN 3: SHARE PORTFOLIO BOTTOM SHEET
    if (showShareSheet) {
        SharePortfolioBottomSheet(
            onDismiss = { showShareSheet = false },
            onShareAction = { assetType ->
                showShareSheet = false
                when (assetType) {
                    "Profile" -> {
                        onSendMessage("📂 Shared Profile Card: Premium Mixing Engineer & Vocalist", "portfolio", "Kabir's Verified Profile", "5-star rating · Verified Identity · Mumbai based", "", "VISIT")
                    }
                    "Project" -> {
                        onSendMessage("🎸 Shared Project Board: 'Midnight Rain' Collaboration", "project", "Midnight Rain EP", "Milestone 1/3: Vocals tracker holding", "₹45,000", "ACTIVE")
                    }
                    "Track" -> {
                        onSendMessage("🎵 Shared high-fidelity audio track preview.", "audio", "Indie_Acoustic_Dry_Demo.wav", "24-bit PCM stem audio", "45 MB", "PLAYABLE")
                    }
                    "Service" -> {
                        onSendMessage("⚡ Shared Booking Service Proposal", "booking", "Vocal Stem Design", "Complete vocal harmony tracking & master tuning", "₹15,000 / Session", "OPEN")
                    }
                    "QR" -> {
                        onSendMessage("📲 Scanned & shared professional NFC QR Link Card.", "document", "MusicLinkd QR Profile Card", "Scan to pay or view live credentials", "", "SECURE")
                    }
                    "Copy" -> {
                        // Copy link
                    }
                }
            }
        )
    }

    // SCREEN 8: QUOTE REQUEST CREATOR
    if (showQuoteCreator) {
        QuoteCreatorDialog(
            activePrice = activeQuotePrice,
            onPriceChange = { activeQuotePrice = it },
            onDismiss = { showQuoteCreator = false },
            onSendQuote = { price, timeline, deliverables, notes ->
                showQuoteCreator = false
                onSendMessage(
                    "📝 Sent Professional Quote Proposal:\nDeliverables: $deliverables\nTimeline: $timeline\nNotes: $notes",
                    "quote",
                    "Professional Quote: Vocal Session",
                    deliverables,
                    price,
                    "PROPOSED"
                )
            }
        )
    }

    // SCREEN 11: CHAT SETTINGS / OPTIONS
    if (showSettingsSheet) {
        ChatSettingsDialog(
            convoName = convo.name,
            isMuted = isMuted,
            isPinned = isPinned,
            onToggleMute = onToggleMute,
            onTogglePin = onTogglePin,
            onDelete = onDeleteChat,
            onDismiss = { showSettingsSheet = false }
        )
    }
}

// ======================================================================================
// ADAPTIVE CONTEXT HEADER COMPONENT
// ======================================================================================
@Composable
fun AdaptiveContextBanner(
    context: ChatContext,
    convoStatus: String,
    offerValue: String,
    onAcceptOffer: () -> Unit,
    onDeclineOffer: () -> Unit,
    onRescheduleBooking: () -> Unit,
    onAttachPortfolio: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFCFD)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual accent based on context
            val accentIcon = when (context) {
                ChatContext.STUDIO -> Icons.Default.RoomService
                ChatContext.MARKETPLACE -> Icons.Default.ShoppingBag
                ChatContext.OPPORTUNITY -> Icons.Default.WorkOutline
                ChatContext.PROJECT -> Icons.Default.GroupWork
            }
            val accentBg = when (context) {
                ChatContext.STUDIO -> Color(0xFFEFF6FF)
                ChatContext.MARKETPLACE -> Color(0xFFFDF2F8)
                ChatContext.OPPORTUNITY -> Color(0xFFF0FDF4)
                ChatContext.PROJECT -> Color(0xFFFAF5FF)
            }
            val accentColor = when (context) {
                ChatContext.STUDIO -> BrandPrimary
                ChatContext.MARKETPLACE -> BrandAccent
                ChatContext.OPPORTUNITY -> BrandSuccess
                ChatContext.PROJECT -> Color(0xFF8B5CF6)
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(accentIcon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                val bannerTitle = when (context) {
                    ChatContext.STUDIO -> "STUDIO BOOKING"
                    ChatContext.MARKETPLACE -> "MARKETPLACE TRANSACTION"
                    ChatContext.OPPORTUNITY -> "PROJECT DISPATCH"
                    ChatContext.PROJECT -> "COLLABORATION BOARD"
                }
                val bannerDetail = when (context) {
                    ChatContext.STUDIO -> "Hold confirmed for July 12 · 4 hours Suite A"
                    ChatContext.MARKETPLACE -> "Vintage Gibson Les Paul · Offer: $offerValue"
                    ChatContext.OPPORTUNITY -> "Licensing Web Series · Status: $convoStatus"
                    ChatContext.PROJECT -> "Milestone Vocals Mixing · Status: $convoStatus"
                }

                Text(bannerTitle, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = accentColor, letterSpacing = 1.sp)
                Text(bannerDetail, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandHeadline)
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action CTAs based on context
            when (context) {
                ChatContext.STUDIO -> {
                    Button(
                        onClick = onRescheduleBooking,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(99.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Reschedule", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                ChatContext.MARKETPLACE -> {
                    if (!offerValue.contains("SOLD")) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(
                                onClick = onAcceptOffer,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess),
                                shape = RoundedCornerShape(99.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Accept", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onDeclineOffer,
                                border = BorderStroke(1.dp, BrandBorder),
                                shape = RoundedCornerShape(99.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Decline", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(BrandSuccess.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("SOLD", color = BrandSuccess, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
                ChatContext.OPPORTUNITY -> {
                    Button(
                        onClick = onAttachPortfolio,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAccent),
                        shape = RoundedCornerShape(99.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Attach Portfolio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                ChatContext.PROJECT -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("60% DONE", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ======================================================================================
// SMART QUICK ACTIONS COMPONENT
// ======================================================================================
@Composable
fun SmartQuickActionsRow(
    context: ChatContext,
    onAction: (String) -> Unit
) {
    val actions = when (context) {
        ChatContext.STUDIO -> listOf("Book Studio", "View Calendar", "Pay Deposit")
        ChatContext.MARKETPLACE -> listOf("Make Offer", "Buy Now", "View Listing")
        ChatContext.OPPORTUNITY -> listOf("Submit Portfolio", "Update Availability", "View Opportunity")
        ChatContext.PROJECT -> listOf("Invite to Project", "View Portfolio", "Hire")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFCFCFD))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Quick:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandSecondary)
        
        actions.forEach { action ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White)
                    .border(1.dp, BrandBorder, RoundedCornerShape(99.dp))
                    .clickable { onAction(action) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = action,
                    color = BrandHeadline,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ======================================================================================
// PREMIUM AI ASSIST FEATURES COMPONENT
// ======================================================================================
@Composable
fun AIAssistTray(
    onSelectReply: (String) -> Unit,
    onSummarize: () -> Unit,
    onGenerateQuote: () -> Unit
) {
    val suggestions = listOf(
        "Sounds great, let's lock it in!",
        "Can you send the track stems?",
        "What's the booking timeline?"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFCFCFD))
            .padding(vertical = 10.dp)
    ) {
        // AI Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🤖", fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("AI ASSIST FEATURE", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = BrandPrimary, letterSpacing = 1.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Summarize Chat",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimary,
                    modifier = Modifier
                        .clickable { onSummarize() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Text(
                    text = "Generate Quote",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandAccent,
                    modifier = Modifier
                        .clickable { onGenerateQuote() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Suggested Replies
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestions.forEach { suggestion ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(BrandPrimary.copy(alpha = 0.08f))
                        .clickable { onSelectReply(suggestion) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "“$suggestion”",
                        color = BrandPrimary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ======================================================================================
// MASSIVE REUSABLE CUSTOM CHAT BUBBLES
// ======================================================================================
@Composable
fun MessageBubble(
    msg: MessageBubbleItem,
    onPlayAudio: () -> Unit
) {
    val isSystem = msg.sender == "System"
    
    if (isSystem) {
        // SYSTEM DECORATIVE BOARD CARDS (Screen 4, 5, 6, 7 details)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .border(1.dp, BrandBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (msg.type) {
                                        "booking" -> Color(0xFFEFF6FF)
                                        "marketplace" -> Color(0xFFFDF2F8)
                                        "opportunity" -> Color(0xFFF0FDF4)
                                        "project" -> Color(0xFFFAF5FF)
                                        "quote" -> Color(0xFFFFFBEB)
                                        else -> BrandBorder
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = msg.type.uppercase(),
                                color = when (msg.type) {
                                    "booking" -> BrandPrimary
                                    "marketplace" -> BrandAccent
                                    "opportunity" -> BrandSuccess
                                    "project" -> Color(0xFF8B5CF6)
                                    "quote" -> Color(0xFFD97706)
                                    else -> BrandHeadline
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Text(msg.metaPrice, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandHeadline)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(msg.metaTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrandHeadline)
                    Text(msg.metaDesc, fontSize = 12.5.sp, color = BrandSecondary, modifier = Modifier.padding(top = 2.dp))

                    Spacer(modifier = Modifier.height(14.dp))

                    Divider(color = BrandBorder)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (msg.metaStatus) {
                                            "PENDING", "PROPOSED" -> Color(0xFFF59E0B)
                                            "ACTIVE", "PLAYABLE" -> BrandSuccess
                                            "UNPAID" -> Color(0xFFEF4444)
                                            else -> BrandSecondary
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(msg.metaStatus, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandHeadline)
                        }

                        // ACTION BUTTONS INSIDE CARDS (Screen 5, 6, 7 actions)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (msg.type == "booking") {
                                OutlinedButton(
                                    onClick = {},
                                    border = BorderStroke(1.dp, BrandBorder),
                                    shape = RoundedCornerShape(99.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text("Invoice", fontSize = 11.sp, color = BrandHeadline)
                                }
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess),
                                    shape = RoundedCornerShape(99.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text("Confirm", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (msg.type == "marketplace") {
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandAccent),
                                    shape = RoundedCornerShape(99.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text("Make Offer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (msg.type == "audio") {
                                Button(
                                    onClick = onPlayAudio,
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                    shape = RoundedCornerShape(99.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Play", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else if (msg.type == "opportunity") {
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                    shape = RoundedCornerShape(99.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text("Attach Portfolio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // STANDARD BUBBLE RENDER
        val alignMe = msg.isMe
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (alignMe) Arrangement.End else Arrangement.Start
        ) {
            Column(horizontalAlignment = if (alignMe) Alignment.End else Alignment.Start) {
                // Name handle
                Text(
                    text = if (alignMe) "Me" else msg.sender,
                    fontSize = 11.sp,
                    color = BrandSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )

                Card(
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomStart = if (alignMe) 24.dp else 4.dp,
                        bottomEnd = if (alignMe) 4.dp else 24.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (alignMe) BrandPrimary else Color(0xFFFCFCFD)
                    ),
                    border = if (alignMe) null else BorderStroke(1.dp, BrandBorder),
                    modifier = Modifier
                        .widthIn(max = 290.dp)
                        .shadow(if (alignMe) 3.dp else 0.dp, RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = msg.text,
                            color = if (alignMe) Color.White else BrandHeadline,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.align(Alignment.End),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = msg.time,
                                color = if (alignMe) Color.White.copy(alpha = 0.7f) else BrandSecondary,
                                fontSize = 10.sp
                            )
                            if (alignMe) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Read Status",
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ======================================================================================
// SCREEN 3: SHARE PORTFOLIO BOTTOM SHEET
// ======================================================================================
@Composable
fun SharePortfolioBottomSheet(
    onDismiss: () -> Unit,
    onShareAction: (String) -> Unit
) {
    // Dynamic simulated sheet with high fidelity overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {}
                .shadow(24.dp, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Notch Bar
                Box(
                    modifier = Modifier
                        .size(40.dp, 5.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(BrandBorder)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Share Professional Assets",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandHeadline
                )
                Text(
                    text = "Instantly post verified credentials, media stems, or invoices.",
                    fontSize = 12.5.sp,
                    color = BrandSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // Grid Options
                val options = listOf(
                    Triple("Share Profile", Icons.Default.Person, "Profile"),
                    Triple("Share Project", Icons.Default.GroupWork, "Project"),
                    Triple("Share Track", Icons.Default.Audiotrack, "Track"),
                    Triple("Share Service", Icons.Default.RoomService, "Service"),
                    Triple("Share QR Code", Icons.Default.QrCodeScanner, "QR"),
                    Triple("Copy Link", Icons.Default.ContentCopy, "Copy")
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(options) { (title, icon, action) ->
                        Card(
                            onClick = { onShareAction(action) },
                            colors = CardDefaults.cardColors(containerColor = BrandBorder.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, contentDescription = title, tint = BrandPrimary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = title,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandHeadline,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Preview Before Sending Pane
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = BrandBorder.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Asset Sandbox Preview", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandHeadline)
                            Text("Selecting an asset builds a secure deep-linked contract card in the conversation stream.", fontSize = 11.sp, color = BrandSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    border = BorderStroke(1.dp, BrandBorder),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold, color = BrandHeadline)
                }
            }
        }
    }
}

// ======================================================================================
// SCREEN 8: QUOTE REQUEST CREATOR DIALOG
// ======================================================================================
@Composable
fun QuoteCreatorDialog(
    activePrice: String,
    onPriceChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSendQuote: (String, String, String, String) -> Unit
) {
    var serviceName by remember { mutableStateOf("Vocal Session Harmony Design") }
    var timelineTarget by remember { mutableStateOf("July 12 (Express)") }
    var deliverablesText by remember { mutableStateOf("3 Mixed WAV tracks, tuned stem parameters, commercial master usage license.") }
    var remarksText by remember { mutableStateOf("Calculated under standard 20% network booking assurance guidelines.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Professional Quote Proposal", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = BrandHeadline)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Service / Role Title", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandSecondary)
                OutlinedTextField(
                    value = serviceName,
                    onValueChange = { serviceName = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Verified Proposal Pricing", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandSecondary)
                OutlinedTextField(
                    value = activePrice,
                    onValueChange = onPriceChange,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Timeline Target Deadline", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandSecondary)
                OutlinedTextField(
                    value = timelineTarget,
                    onValueChange = { timelineTarget = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Deliverables Breakdown", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandSecondary)
                OutlinedTextField(
                    value = deliverablesText,
                    onValueChange = { deliverablesText = it },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Text("Notes / Invoice remarks", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandSecondary)
                OutlinedTextField(
                    value = remarksText,
                    onValueChange = { remarksText = it },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSendQuote(activePrice, timelineTarget, deliverablesText, remarksText) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Send Quote", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = BrandSecondary)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

// ======================================================================================
// SCREEN 10: CALL HISTORY SCREEN
// ======================================================================================
@Composable
fun CallHistoryScreen(
    callLogs: List<CallLogItem>,
    onBackClick: () -> Unit,
    onJoinCall: (CallLogItem) -> Unit,
    onScheduleCall: (String, String, String) -> Unit
) {
    var showScheduleDialog by remember { mutableStateOf(false) }
    var scheduleName by remember { mutableStateOf("Netflix Original Sync") }
    var scheduleTime by remember { mutableStateOf("July 3, 2:00 PM") }
    var scheduleType by remember { mutableStateOf("Video") }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BrandHeadline)
                }
                Text("Collaboration Calls", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = BrandHeadline)
                IconButton(
                    onClick = { showScheduleDialog = true },
                    modifier = Modifier.size(40.dp).background(BrandPrimary.copy(alpha = 0.12f), CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Schedule Call", tint = BrandPrimary)
                }
            }
        },
        containerColor = BrandBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            // Call control panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .border(1.dp, BrandBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = BrandBorder.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("⚡ Active Meeting Studio", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = BrandHeadline)
                    Text("Secure audio & screen-share rooms utilizing lossless Dolby codec engine.", fontSize = 12.sp, color = BrandSecondary)
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(99.dp)
                        ) {
                            Text("Join Room", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { showScheduleDialog = true },
                            border = BorderStroke(1.dp, BrandBorder),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(99.dp)
                        ) {
                            Text("Schedule Sync", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandHeadline)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Call History Logs", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = BrandHeadline)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(callLogs) { log ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (log.status == "Missed") Color(0xFFFEF2F2) else BrandBorder.copy(alpha = 0.5f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (log.type == "Video") Icons.Default.Videocam else Icons.Default.Call,
                                        contentDescription = null,
                                        tint = if (log.status == "Missed") Color.Red else BrandPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(log.callerName, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = BrandHeadline)
                                    Text("${log.type} Call · ${log.time}", fontSize = 11.5.sp, color = BrandSecondary)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(
                                        when (log.status) {
                                            "Missed" -> Color(0xFFFEF2F2)
                                            "Scheduled" -> Color(0xFFEFF6FF)
                                            else -> BrandBorder.copy(alpha = 0.4f)
                                        }
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = log.status,
                                    color = when (log.status) {
                                        "Missed" -> Color.Red
                                        "Scheduled" -> BrandPrimary
                                        else -> BrandHeadline
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showScheduleDialog) {
        AlertDialog(
            onDismissRequest = { showScheduleDialog = false },
            title = { Text("Schedule Collaboration Call", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Invitee / Team Name", fontSize = 11.sp, color = BrandSecondary)
                    OutlinedTextField(value = scheduleName, onValueChange = { scheduleName = it }, modifier = Modifier.fillMaxWidth())
                    
                    Text("Target Date & Time", fontSize = 11.sp, color = BrandSecondary)
                    OutlinedTextField(value = scheduleTime, onValueChange = { scheduleTime = it }, modifier = Modifier.fillMaxWidth())
                    
                    Text("Call Type", fontSize = 11.sp, color = BrandSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("Audio", "Video").forEach { item ->
                            val active = scheduleType == item
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(if (active) BrandPrimary else BrandBorder)
                                    .clickable { scheduleType = item }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(item, color = if (active) Color.White else BrandHeadline, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showScheduleDialog = false
                        onScheduleCall(scheduleName, scheduleTime, scheduleType)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("ScheduleSync", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showScheduleDialog = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}

// ======================================================================================
// SCREEN 9: FILES & MEDIA SCREEN
// ======================================================================================
@Composable
fun FilesAndMediaScreen(
    files: List<SharedFileItem>,
    onBackClick: () -> Unit,
    onSearchFiles: (String) -> Unit,
    onDownloadFile: (SharedFileItem) -> Unit
) {
    var queryText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BrandHeadline)
                    }
                    Text("Files & Shared Media", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = BrandHeadline)
                    Box(modifier = Modifier.size(40.dp)) // spacing
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = queryText,
                    onValueChange = {
                        queryText = it
                        onSearchFiles(it)
                    },
                    placeholder = { Text("Search shared media, STEMs, PDFs...", color = BrandSecondary, fontSize = 13.5.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(99.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = BrandBorder.copy(alpha = 0.3f),
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BrandBorder
                    ),
                    singleLine = true
                )
            }
        },
        containerColor = BrandBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Text("Contracts, Invoices & Audio STEMs", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = BrandHeadline)
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(files.filter { it.name.contains(queryText, ignoreCase = true) }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrandBorder, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (item.type == "PDF") Color(0xFFFEF2F2) else Color(0xFFEFF6FF)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (item.type == "PDF") "📄" else "🎵",
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandHeadline)
                                    Text("${item.size} · ${item.type} file · Shared ${item.date}", fontSize = 11.sp, color = BrandSecondary)
                                }
                            }

                            IconButton(onClick = { onDownloadFile(item) }) {
                                Icon(Icons.Default.Download, contentDescription = "Download File", tint = BrandPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ======================================================================================
// SCREEN 11: CHAT OPTIONS / SETTINGS DIALOG
// ======================================================================================
@Composable
fun ChatSettingsDialog(
    convoName: String,
    isMuted: Boolean,
    isPinned: Boolean,
    onToggleMute: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("$convoName Parameters", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = BrandHeadline)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // PIN
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTogglePin() }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PushPin, contentDescription = null, tint = BrandPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Pin Conversation Block", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = BrandHeadline)
                    }
                    Switch(
                        checked = isPinned,
                        onCheckedChange = { onTogglePin() },
                        colors = SwitchDefaults.colors(checkedTrackColor = BrandPrimary)
                    )
                }

                // MUTE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleMute() }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeMute, contentDescription = null, tint = BrandPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Mute Notifications", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = BrandHeadline)
                    }
                    Switch(
                        checked = isMuted,
                        onCheckedChange = { onToggleMute() },
                        colors = SwitchDefaults.colors(checkedTrackColor = BrandPrimary)
                    )
                }

                Divider(color = BrandBorder)

                // EXPORT
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {}
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.IosShare, contentDescription = null, tint = BrandSecondary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Export Chat Transaction Ledgers", fontSize = 13.5.sp, color = BrandHeadline)
                }

                // DELETE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDelete() }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Delete Chat History", fontSize = 13.5.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close Parameters", fontWeight = FontWeight.Bold, color = BrandPrimary)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}
