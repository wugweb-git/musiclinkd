package com.example.onboarding.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

// --------------------------------------------------------------------------------------
// COLOR TOKENS (MUSICLINKD FLOW 11 DESIGN SYSTEM)
// --------------------------------------------------------------------------------------
private val BrandPrimary = Color(0xFFEA4C89)      // Primary Pink
private val BrandAccent = Color(0xFFEA4C89)       // Accent/Highlights Pink
private val BrandDark = Color(0xFF0D0B26)         // Primary Dark Navy
private val BrandBackground = Color(0xFFFFFFFF)   // Background Surface
private val BrandSecondaryText = Color(0xFF7D7D8E)// Secondary Grey
private val BrandBorder = Color(0xFFECECF1)       // Subtle separation
private val BrandCard = Color(0xFFFFFFFF)
private val BrandSuccess = Color(0xFF10B981)
private val LightPurpleBg = Color(0xFFFFF0F5)
private val LightPinkBg = Color(0xFFFFF0F5)

// --------------------------------------------------------------------------------------
// MOCK MODELS FOR THE MUSIC BUSINESS ECOSYSTEM
// --------------------------------------------------------------------------------------
data class BusinessService(
    val title: String,
    val description: String,
    val price: String,
    val duration: String,
    val icon: ImageVector
)

data class TeamMember(
    val name: String,
    val role: String,
    val experience: String,
    val bio: String,
    val initial: String,
    val accentColor: Color
)

data class BusinessPortfolioProject(
    val title: String,
    val client: String,
    val year: String,
    val tags: List<String>,
    val type: String, // Album, Film Score, Video, Testimonial
    val waveformSeed: Int = 10
)

data class EquipmentItem(
    val name: String,
    val category: String, // Console, Microphones, Monitors, Software, Outboard
    val spec: String,
    val available: Boolean = true
)

data class StudioReview(
    val reviewer: String,
    val rating: Int,
    val project: String,
    val comment: String,
    val date: String,
    val reply: String? = null
)

data class RelatedBiz(
    val name: String,
    val category: String,
    val rating: Double,
    val location: String,
    val startingPrice: String,
    val bannerLetter: String,
    val bgColor: Color
)

// --------------------------------------------------------------------------------------
// WORKSPACE MAIN WRAPPER
// --------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileWorkspace(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine context views
    var isOwnerMode by remember { mutableStateOf(false) } // Toggle between client and owner views
    var selectedCategoryTab by remember { mutableStateOf("Overview") }
    
    // Interactive states
    var isFollowing by remember { mutableStateOf(false) }
    var followerCount by remember { mutableStateOf(1420) }
    var showBookingSuccess by remember { mutableStateOf(false) }
    var selectedGalleryImageIndex by remember { mutableStateOf<Int?>(null) }
    
    // Mock listings for Octave Sound Labs (Bandra West, Mumbai)
    val services = remember {
        listOf(
            BusinessService("Full Band Live Tracking", "Up to 32 channels simultaneous recording in our live room with vintage Neve preamps.", "₹4,500", "Hourly", Icons.Default.Mic),
            BusinessService("Certified Dolby Atmos Mixing", "Premium immersive mixing conformant to Dolby, Apple Music & Amazon standards.", "₹15,000", "Per Track", Icons.Default.Speaker),
            BusinessService("Acoustic Grand Piano Session", "Recording on our custom-tuned Yamaha C7 grand piano with Neumann stereo pair.", "₹3,500", "Hourly", Icons.Default.MusicNote),
            BusinessService("Commercial Film Stereo Dubbing", "Full visual-synchronized ADR, foley recording, and stereo surround mastering.", "₹25,000", "Day Rate", Icons.Default.Movie),
            BusinessService("Masterclass Audio Production", "One-on-one professional mentorship with our certified chief resident engineers.", "₹12,000", "5 Sessions", Icons.Default.School),
            BusinessService("Vintage Backline Gear Rental", "Rent rare analog synths, boutique tube amps and classic guitars for sessions.", "₹2,500", "Per Session", Icons.Default.SettingsInputComponent)
        )
    }

    val team = remember {
        listOf(
            TeamMember("Amit Trivedi", "Founder & Chief Composer", "18+ Years", "National Award winner composer. Built Octave Sound Labs to support local indies.", "AT", Color(0xFFEA4C89)),
            TeamMember("Karan Malhotra", "Senior Dolby Atmos Specialist", "8+ Years", "Certified by Dolby Laboratories. Expert in electronic and acoustic hybrid formats.", "KM", Color(0xFFEA4C89)),
            TeamMember("Pooja Singhal", "Lead Vocal tracking & ADR Engineer", "6+ Years", "Specialized in high-fidelity vocal processing, pop harmonies and classic Indian classical setups.", "PS", Color(0xFF16A34A)),
            TeamMember("Devraj Sen", "Analog Synthesizer Archivist", "11+ Years", "Maintains our vintage hardware rack. Passionate about retro-futuristic synthwave scores.", "DS", Color(0xFF8B5CF6))
        )
    }

    val portfolio = remember {
        listOf(
            BusinessPortfolioProject("Sufi Nights EP", "Kabir & The Band", "2026", listOf("Live Recording", "Acoustic Mix"), "Album"),
            BusinessPortfolioProject("Mumbai Monsoon Raga", "National Indie Project", "2026", listOf("Dolby Atmos Mix", "Stereo Master"), "Film Score"),
            BusinessPortfolioProject("Neon Dreams Soundtrack", "Netflix India Originals", "2025", listOf("Synthesizers", "Foley Post"), "Video", 25),
            BusinessPortfolioProject("Acoustic Sessions Vol 3", "Aria Vox", "2026", listOf("Vocal Session", "Yamaha C7"), "Testimonial")
        )
    }

    val equipment = remember {
        listOf(
            EquipmentItem("SSL Duality Delta 48-Channel Console", "Console", "Flagship analog workflow with absolute DAW integration."),
            EquipmentItem("Neve 1073 Mic Preamp & EQ (x8)", "Console", "Classic warm British vintage harmonic tone."),
            EquipmentItem("Neumann U87 Ai Large-Diaphragm (x2)", "Microphones", "Industry standard for rich, authoritative vocal tracks."),
            EquipmentItem("Shure SM7B Dynamic Cardioid (x4)", "Microphones", "Crisp vocal captures, pristine podcast recordings."),
            EquipmentItem("Royer R-121 Active Ribbon Mics (x2)", "Microphones", "Warm, highly detailed acoustic string recordings."),
            EquipmentItem("Genelec 8361A Coaxial Atmos Setup", "Monitors", "Certified 7.1.4 high-fidelity spatial calibration monitors."),
            EquipmentItem("Avid Pro Tools HDX Ultimate System", "Software", "Sub-millisecond tracking latency with 64-channel I/O."),
            EquipmentItem("Universal Audio Apollo x16 Heritage", "Interfaces", "Elite converters with real-time UAD processing."),
            EquipmentItem("Moog One 16-Voice Analog Synthesizer", "Backline", "Preeminent custom visualizer flagship synth.")
        )
    }

    val reviews = remember {
        listOf(
            StudioReview("Arjun Mehta", 5, "Monsoon Raga mixing", "Absolute state of the art workspace! Karan understood spatial dimensions perfectly. The Genelec Atmos system is jaw-dropping.", "June 12, 2026", "Thank you Arjun! Sourcing those sitar spatial channels was an absolute pleasure. Come back soon!"),
            StudioReview("Riya Sen", 5, "Vocal Stems Session", "Loved Pooja's precision during vocal tracking. She made me feel completely relaxed and the mic setup sounded angelic.", "May 28, 2026", "Riya, your vocals are outstanding! Can't wait to master the full EP next month."),
            StudioReview("The Kabir Trio", 4, "Acoustic EP Live", "Super spacious tracking room. Great isolation between drums and acoustic bass. Minimal leakage, brilliant acoustics.", "April 18, 2026")
        )
    }

    val related = remember {
        listOf(
            RelatedBiz("Echo Chamber Studios", "Mixing Studio", 4.9, "Andheri West, Mumbai", "₹3,500/hr", "EC", Color(0xFFEA4C89)),
            RelatedBiz("Guitars & Beyond", "Equipment Dealer", 4.8, "Juhu, Mumbai", "Retail / Delivery", "GB", Color(0xFFEA4C89)),
            RelatedBiz("Blue Lotus Arena", "Live Venue", 4.7, "Lower Parel, Mumbai", "Inquiry-based", "BL", Color(0xFF16A34A)),
            RelatedBiz("Vocal Harmony Academy", "Music School", 4.9, "Bandra, Mumbai", "₹1,200/lesson", "VH", Color(0xFF8B5CF6))
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandBackground)
                    .statusBarsPadding()
            ) {
                // Header row with back buttons and owner/client toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(BrandBorder.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BrandDark
                        )
                    }

                    // EXTREMELY COOL VISUAL TOGGLE BETWEEN USER ROLES
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(BrandBorder.copy(alpha = 0.6f))
                            .padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(if (!isOwnerMode) BrandPrimary else Color.Transparent)
                                .clickable { isOwnerMode = false }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                "Client View",
                                color = if (!isOwnerMode) Color.White else BrandDark,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(99.dp))
                                .background(if (isOwnerMode) BrandAccent else Color.Transparent)
                                .clickable { isOwnerMode = true }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = if (isOwnerMode) Color.White else BrandDark,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(Dimens.sp1))
                                Text(
                                    "Owner Mode",
                                    color = if (isOwnerMode) Color.White else BrandDark,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Divider(color = BrandBorder)
            }
        },
        containerColor = BrandBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isOwnerMode) {
                // SCREEN 12: OWNER BUSINESS INSIGHTS
                BusinessOwnerInsightsScreen(onUpgradeClick = { /* Launch Pro billing */ })
            } else {
                // CLIENT VIEW FLOW (SCREEN 1 - 11)
                Column(modifier = Modifier.fillMaxSize()) {
                    // CATEGORY TABS (Sticky Navigation)
                    TabNavigationRow(
                        selectedTab = selectedCategoryTab,
                        onTabSelect = { selectedCategoryTab = it }
                    )
                    Divider(color = BrandBorder)

                    // CHOOSE APPROPRIATE PAGE
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Crossfade(targetState = selectedCategoryTab, label = "BusinessSubPage") { tab ->
                            when (tab) {
                                "Overview" -> {
                                    OverviewSubPage(
                                        isFollowing = isFollowing,
                                        followerCount = followerCount,
                                        onFollowToggle = {
                                            isFollowing = !isFollowing
                                            if (isFollowing) followerCount++ else followerCount--
                                        },
                                        onBookClick = { selectedCategoryTab = "Book Now" },
                                        related = related
                                    )
                                }
                                "Services" -> {
                                    ServicesSubPage(
                                        services = services,
                                        onBookService = {
                                            selectedCategoryTab = "Book Now"
                                        }
                                    )
                                }
                                "Equipment" -> {
                                    EquipmentSubPage(equipment = equipment)
                                }
                                "Team & Works" -> {
                                    TeamAndWorksSubPage(team = team, portfolio = portfolio)
                                }
                                "Reviews & Gallery" -> {
                                    ReviewsAndGallerySubPage(
                                        reviews = reviews,
                                        onZoomImage = { index -> selectedGalleryImageIndex = index }
                                    )
                                }
                                "Book Now" -> {
                                    BookingSubPage(
                                        onBookingConfirm = { showBookingSuccess = true }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // MODAL OVERLAYS (IMAGE ZOOM-IN AND BOOKING CONFIRMATIONS)
            if (showBookingSuccess) {
                AlertDialog(
                    onDismissRequest = { showBookingSuccess = false },
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("🎉 Booking Confirmed!", style = Typography.bodyLarge, color = BrandDark)
                        }
                    },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(BrandSuccess.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(40.dp))
                            }
                            Spacer(modifier = Modifier.height(Dimens.sp2))
                            Text(
                                text = "Your session at Octave Sound Labs has been securely booked. Access the invoice and chat under the Messages tab.",
                                textAlign = TextAlign.Center,
                                fontSize = 13.5.sp,
                                color = BrandSecondaryText,
                                lineHeight = 19.sp
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showBookingSuccess = false },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(99.dp)
                        ) {
                            Text("Manage in Workspace", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    },
                    shape = RoundedCornerShape(Dimens.r3),
                    containerColor = Color.White
                )
            }

            selectedGalleryImageIndex?.let { index ->
                FullScreenImageOverlay(
                    imageIndex = index,
                    onClose = { selectedGalleryImageIndex = null }
                )
            }
        }
    }
}

// ======================================================================================
// TAB ROW SELECTION WRAPPER
// ======================================================================================
@Composable
fun TabNavigationRow(
    selectedTab: String,
    onTabSelect: (String) -> Unit
) {
    val tabs = listOf("Overview", "Services", "Equipment", "Team & Works", "Reviews & Gallery", "Book Now")
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBackground)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(Dimens.sp1),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        items(tabs) { tab ->
            val isSelected = selectedTab == tab
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .background(if (isSelected) BrandDark else Color.Transparent)
                    .border(1.dp, if (isSelected) BrandDark else BrandBorder, RoundedCornerShape(99.dp))
                    .clickable { onTabSelect(tab) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tab,
                    color = if (isSelected) Color.White else BrandDark,
                    style = Typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

// ======================================================================================
// SCREEN 1, 2, 10, 11: OVERVIEW SUB-PAGE (THE HERO LANDING CARD)
// ======================================================================================
@Composable
fun OverviewSubPage(
    isFollowing: Boolean,
    followerCount: Int,
    onFollowToggle: () -> Unit,
    onBookClick: () -> Unit,
    related: List<RelatedBiz>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        // HERO COVER PHOTO WITH LARGE ADAPTIVE BRAND GRADIENT
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(BrandPrimary, BrandAccent, Color(0xFFFFB2D2))
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                )

                // Verified Location / Category badge overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.VerifiedUser, null, tint = BrandAccent, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(Dimens.sp1))
                    Text("GST Verified Storefront", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Dynamic Audio Showcase watermark
                Text(
                    text = "OCTAVE",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 8.dp)
                )
            }
        }

        // IDENTITY METRICS & STATS BLOCK
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                
                // Studio logo avatar + verified badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(Dimens.r3))
                            .background(LightPurpleBg)
                            .border(1.5.dp, BrandPrimary.copy(alpha = 0.3f), RoundedCornerShape(Dimens.r3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎹", fontSize = 36.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)) {
                        // MESSAGE BUTTON
                        IconButton(
                            onClick = { /* Navigate to chat flow */ },
                            modifier = Modifier
                                .size(48.dp)
                                .background(BrandBorder.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Message, contentDescription = "Message Studio", tint = BrandPrimary)
                        }

                        // SHARE BUTTON
                        IconButton(
                            onClick = { /* Share dialog */ },
                            modifier = Modifier
                                .size(48.dp)
                                .background(BrandBorder.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share storefront", tint = BrandDark)
                        }

                        // FOLLOW TOGGLE BUTTON
                        Button(
                            onClick = onFollowToggle,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFollowing) BrandDark else BrandAccent
                            ),
                            shape = RoundedCornerShape(99.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp)
                        ) {
                            Text(
                                text = if (isFollowing) "Following" else "Follow",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.sp2))

                // Name & Metadata
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Octave Sound Labs Mumbai",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandDark,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Business checkmark",
                        tint = BrandPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("Recording & Dolby Atmos Suite", color = BrandPrimary, style = Typography.bodyLarge)
                    Spacer(modifier = Modifier.width(Dimens.sp1))
                    Text("•", color = BrandSecondaryText)
                    Spacer(modifier = Modifier.width(Dimens.sp1))
                    Icon(Icons.Default.Star, "Rating star", tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                    Text(" 4.9 (48 Reviews)", color = BrandDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "Bandra West, Link Road, Mumbai, India",
                    fontSize = 13.sp,
                    color = BrandSecondaryText,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(Dimens.sp3))

                // BUSINESS QUICK STATS COLUMN GRID (SCREEN 1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.r3))
                        .background(BrandBorder.copy(alpha = 0.30f))
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBox(value = "8+", label = "Years Active")
                    StatSeparator()
                    StatBox(value = "450+", label = "Projects Done")
                    StatSeparator()
                    StatBox(value = "120+", label = "HQ Clients")
                    StatSeparator()
                    StatBox(value = "$followerCount", label = "Followers")
                }

                Spacer(modifier = Modifier.height(28.dp))

                // SCREEN 2: ABOUT / STORY
                Text("Our Story", style = Typography.bodyLarge, color = BrandDark)
                Spacer(modifier = Modifier.height(Dimens.sp1))
                Text(
                    text = "Established in 2018, Octave Sound Labs has grown into Mumbai's premium creative haven for independent musicians and top commercial cinema directors. We blend an immaculate dual SSL 9000J tracking layout with cutting-edge 7.1.4 certified Dolby Atmos arrays. Our spaces are acoustically floated to ensure complete silence and high-end transient clarity.",
                    style = Typography.labelLarge,
                    color = BrandSecondaryText,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // OPERATING TIMELINE & SPECS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.r3),
                    colors = CardDefaults.cardColors(containerColor = LightPurpleBg.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(Dimens.sp2)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(Dimens.sp1))
                            Text("Operating hours & contact limits", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = BrandDark)
                        }
                        Spacer(modifier = Modifier.height(Dimens.sp1))
                        Text("• Monday - Saturday: 09:00 AM - 11:00 PM", fontSize = 12.5.sp, color = BrandSecondaryText)
                        Text("• Sundays: Sessional request only", fontSize = 12.5.sp, color = BrandSecondaryText)
                        Text("• GST Verification: GSTIN 27AAAAA1111A1Z1 (Fully Claimable)", fontSize = 12.5.sp, color = BrandSecondaryText)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // SCREEN 10: CONTACT DETAILS
                Text("Contact & Location", style = Typography.bodyLarge, color = BrandDark)
                Spacer(modifier = Modifier.height(Dimens.sp2))
                ContactInfoCard()

                Spacer(modifier = Modifier.height(Dimens.sp3))

                // SCREEN 11: RELATED BUSINESSES
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Related Organizations Near You", style = Typography.bodyLarge, color = BrandDark)
                    Text("View All", style = Typography.bodyLarge, color = BrandPrimary)
                }
                Spacer(modifier = Modifier.height(Dimens.sp2))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.sp2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(related) { item ->
                        RelatedBizCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = Typography.bodyLarge, color = BrandDark)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 11.sp, color = BrandSecondaryText, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun StatSeparator() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(36.dp)
            .background(BrandBorder)
    )
}

// ======================================================================================
// SCREEN 3: SERVICES SUB-PAGE
// ======================================================================================
@Composable
fun ServicesSubPage(
    services: List<BusinessService>,
    onBookService: (BusinessService) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        item {
            Text("Our Professional Storefront Services", style = Typography.bodyLarge, color = BrandDark)
            Text(
                "Book verified commercial services conforming strictly to professional guidelines.",
                fontSize = 13.sp,
                color = BrandSecondaryText,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }

        items(services) { service ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandCard),
                shape = RoundedCornerShape(Dimens.r3),
                border = BorderStroke(1.dp, BrandBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(Dimens.r2))
                                    .background(LightPurpleBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(service.icon, null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(Dimens.sp2))
                            Column {
                                Text(service.title, style = Typography.bodyLarge, color = BrandDark)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Duration: ${service.duration}", style = Typography.labelLarge, color = BrandSecondaryText)
                            }
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text(service.price, style = Typography.bodyLarge, color = BrandPrimary)
                            Text("Starting rate", fontSize = 10.sp, color = BrandSecondaryText)
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.sp2))
                    Text(service.description, fontSize = 13.sp, color = BrandSecondaryText, lineHeight = 19.sp)
                    Spacer(modifier = Modifier.height(Dimens.sp2))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, null, tint = BrandSuccess, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(Dimens.sp1))
                            Text("Secure Payment Active", color = BrandSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onBookService(service) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(99.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Text("Book Suite", style = Typography.bodyLarge, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ======================================================================================
// SCREEN 7: EQUIPMENT LIST SUB-PAGE
// ======================================================================================
@Composable
fun EquipmentSubPage(equipment: List<EquipmentItem>) {
    val categories = listOf("All", "Console", "Microphones", "Monitors", "Software", "Backline")
    var activeFilter by remember { mutableStateOf("All") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Premium Studio Equipment & Gear", style = Typography.bodyLarge, color = BrandDark)
            Text(
                "Browse verified technical spec sheets and backline inventory details.",
                fontSize = 13.sp,
                color = BrandSecondaryText,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            // Dynamic filter pills
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sp1),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = activeFilter == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(if (isSelected) BrandAccent else BrandBorder.copy(alpha = 0.5f))
                            .clickable { activeFilter = cat }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else BrandDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        val filteredItems = if (activeFilter == "All") equipment else equipment.filter { it.category == activeFilter }

        if (filteredItems.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📦", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(Dimens.sp2))
                    Text("No equipment listed in this category.", style = Typography.labelLarge, color = BrandSecondaryText, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            items(filteredItems) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BrandCard),
                    border = BorderStroke(1.dp, BrandBorder),
                    shape = RoundedCornerShape(Dimens.r2)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(Dimens.r1))
                                        .background(LightPurpleBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(item.category.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                                }
                                Spacer(modifier = Modifier.width(Dimens.sp1))
                                if (item.available) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(Dimens.r1))
                                            .background(Color(0xFFF0FDF4))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("IN HOUSE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = BrandSuccess)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(Dimens.sp1))
                            Text(item.name, style = Typography.bodyLarge, color = BrandDark)
                            Text(item.spec, style = Typography.labelLarge, color = BrandSecondaryText, modifier = Modifier.padding(top = 2.dp))
                        }
                        
                        Icon(Icons.Default.SettingsInputComponent, null, tint = BrandSecondaryText.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

// ======================================================================================
// SCREEN 4 & 5: TEAM & PORTFOLIO SUB-PAGE
// ======================================================================================
@Composable
fun TeamAndWorksSubPage(
    team: List<TeamMember>,
    portfolio: List<BusinessPortfolioProject>
) {
    var playingProjectIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        // --- SECTION 1: THE ELITE TEAM (SCREEN 4) ---
        item {
            Text("Meet Our Professional Engineers", style = Typography.bodyLarge, color = BrandDark)
            Spacer(modifier = Modifier.height(Dimens.sp2))
        }

        items(team) { member ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandCard),
                border = BorderStroke(1.dp, BrandBorder),
                shape = RoundedCornerShape(Dimens.r3)
            ) {
                Row(
                    modifier = Modifier.padding(Dimens.sp2),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(member.accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(member.initial, fontWeight = FontWeight.Bold, color = member.accentColor, style = Typography.bodyLarge)
                    }

                    Spacer(modifier = Modifier.width(Dimens.sp2))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(member.name, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = BrandDark)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(BrandBorder.copy(alpha = 0.5f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(member.experience, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                            }
                        }
                        Text(member.role, style = Typography.labelLarge, color = BrandPrimary, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(Dimens.sp1))
                        Text(member.bio, fontSize = 12.5.sp, color = BrandSecondaryText, lineHeight = 18.sp)
                    }
                }
            }
        }

        // --- SECTION 2: CLIENT PORTFOLIO (SCREEN 5) ---
        item {
            Spacer(modifier = Modifier.height(Dimens.sp2))
            Text("Featured Studio Releases", style = Typography.bodyLarge, color = BrandDark)
            Spacer(modifier = Modifier.height(Dimens.sp1))
            Text("Listen to recent mixes and masters generated live in our Bandra suites.", fontSize = 13.sp, color = BrandSecondaryText)
            Spacer(modifier = Modifier.height(Dimens.sp1))
        }

        items(portfolio.size) { index ->
            val proj = portfolio[index]
            val isPlaying = playingProjectIndex == index

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandCard),
                border = BorderStroke(1.dp, BrandBorder),
                shape = RoundedCornerShape(Dimens.r3)
            ) {
                Column(modifier = Modifier.padding(Dimens.sp2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { playingProjectIndex = if (isPlaying) null else index },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(if (isPlaying) BrandAccent else BrandPrimary, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play Audio",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(Dimens.sp2))
                            Column {
                                Text(proj.title, style = Typography.bodyLarge, color = BrandDark)
                                Text("Client: ${proj.client} • ${proj.year}", style = Typography.labelLarge, color = BrandSecondaryText)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Dimens.r1))
                                .background(BrandBorder.copy(alpha = 0.5f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(proj.type.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.sp2))

                    // Waveform visualizer simulation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(36) { barIndex ->
                            // Custom calculation to simulate active waveform
                            val amplitude = if (isPlaying) {
                                (3..26).random()
                            } else {
                                (barIndex % 7) * 3 + 4
                            }
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(amplitude.dp)
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(
                                        if (isPlaying && barIndex < 18) BrandAccent 
                                        else if (isPlaying) BrandPrimary.copy(alpha = 0.4f)
                                        else BrandSecondaryText.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.sp2))

                    // Tags
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(proj.tags) { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(99.dp))
                                    .background(LightPurpleBg)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(tag, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ======================================================================================
// SCREEN 6 & 8: REVIEWS & GALLERY SUB-PAGE
// ======================================================================================
@Composable
fun ReviewsAndGallerySubPage(
    reviews: List<StudioReview>,
    onZoomImage: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        // --- SECTION 1: STUDIO PHOTO GALLERY (SCREEN 6 - EDITORIAL MASONRY) ---
        item {
            Text("Visual Studio Gallery", style = Typography.bodyLarge, color = BrandDark)
            Text("Pristine recording lounges, live acoustics floated tracking rooms & master suites.", fontSize = 12.5.sp, color = BrandSecondaryText)
            Spacer(modifier = Modifier.height(Dimens.sp1))
            
            // Editorial Masonry Mock layout using simple layouts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sp2)
            ) {
                // Large left photo
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(Dimens.r3))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(BrandPrimary, BrandPrimary.copy(alpha = 0.6f))
                            )
                        )
                        .clickable { onZoomImage(0) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎛️", fontSize = 48.sp)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(Dimens.r1))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("SSL Control Suite A", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Vertical stack on the right
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Dimens.r3))
                            .background(Brush.linearGradient(colors = listOf(Color(0xFF10B981), BrandPrimary)))
                            .clickable { onZoomImage(1) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎹", fontSize = 28.sp)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Yamaha C7", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Dimens.r3))
                            .background(Brush.linearGradient(colors = listOf(Color(0xFFEA4C89), Color(0xFFFFB2D2))))
                            .clickable { onZoomImage(2) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎙️", fontSize = 28.sp)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Neumann U87", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- SECTION 2: TESTIMONIAL REVIEWS (SCREEN 8) ---
        item {
            Spacer(modifier = Modifier.height(Dimens.sp2))
            Text("Client Reviews & Verifications", style = Typography.bodyLarge, color = BrandDark)
            Spacer(modifier = Modifier.height(Dimens.sp2))
        }

        items(reviews) { r ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandCard),
                border = BorderStroke(1.dp, BrandBorder),
                shape = RoundedCornerShape(Dimens.r3)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(r.reviewer, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = BrandDark)
                            Text("Verified Booking: ${r.project}", style = Typography.labelLarge, color = BrandPrimary, fontWeight = FontWeight.SemiBold)
                        }

                        // Star ratings
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            repeat(r.rating) {
                                Icon(Icons.Default.Star, null, tint = Color(0xFFFBBF24), modifier = Modifier.size(15.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.sp1))
                    Text(
                        text = "\"${r.comment}\"",
                        fontSize = 13.5.sp,
                        color = BrandSecondaryText,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    r.reply?.let { reply ->
                        Spacer(modifier = Modifier.height(Dimens.sp2))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Dimens.r2))
                                .background(BrandBorder.copy(alpha = 0.35f))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Reply, null, tint = BrandPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Business Reply", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandDark)
                                }
                                Spacer(modifier = Modifier.height(Dimens.sp1))
                                Text(reply, style = Typography.labelLarge, color = BrandSecondaryText, lineHeight = 17.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ======================================================================================
// SCREEN 9: BOOKING SUB-PAGE
// ======================================================================================
@Composable
fun BookingSubPage(
    onBookingConfirm: () -> Unit
) {
    var selectedDayIndex by remember { mutableStateOf(0) }
    var selectedTimeIndex by remember { mutableStateOf(0) }

    val days = listOf("Mon\nJuly 12", "Tue\nJuly 13", "Wed\nJuly 14", "Thu\nJuly 15", "Fri\nJuly 16", "Sat\nJuly 17")
    val timeSlots = listOf("09:00 AM - 01:00 PM", "02:00 PM - 06:00 PM", "07:00 PM - 11:00 PM")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        item {
            Text("Secure Calendar Booking Setup", style = Typography.bodyLarge, color = BrandDark)
            Text("Choose date and tracking block. Our system ensures payments stay secure until delivery.", fontSize = 13.sp, color = BrandSecondaryText)
            Spacer(modifier = Modifier.height(Dimens.sp1))
        }

        // Horizontal visual calendar selector
        item {
            Text("Select Sessional Date", style = Typography.bodyLarge, color = BrandDark)
            Spacer(modifier = Modifier.height(Dimens.sp1))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(days.size) { index ->
                    val isSelected = selectedDayIndex == index
                    Card(
                        modifier = Modifier
                            .width(88.dp)
                            .clickable { selectedDayIndex = index },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BrandPrimary else Color.White
                        ),
                        border = BorderStroke(1.dp, if (isSelected) BrandPrimary else BrandBorder),
                        shape = RoundedCornerShape(Dimens.r2)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = days[index].split("\n")[0],
                                color = if (isSelected) Color.White else BrandSecondaryText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(Dimens.sp1))
                            Text(
                                text = days[index].split("\n")[1],
                                color = if (isSelected) Color.White else BrandDark,
                                style = Typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }

        // Time slot picker
        item {
            Spacer(modifier = Modifier.height(Dimens.sp1))
            Text("Select Available Block Slot", style = Typography.bodyLarge, color = BrandDark)
            Spacer(modifier = Modifier.height(Dimens.sp1))
            
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                timeSlots.forEachIndexed { index, slot ->
                    val isSelected = selectedTimeIndex == index
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTimeIndex = index },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) LightPurpleBg else Color.White
                        ),
                        border = BorderStroke(1.dp, if (isSelected) BrandPrimary else BrandBorder),
                        shape = RoundedCornerShape(Dimens.r2)
                    ) {
                        Row(
                            modifier = Modifier.padding(Dimens.sp2),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = if (isSelected) BrandPrimary else BrandSecondaryText,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(Dimens.sp2))
                                Text(slot, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = BrandDark)
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(BrandPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Billing Summary & Payment Protection Notice
        item {
            Spacer(modifier = Modifier.height(Dimens.sp2))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.r3),
                colors = CardDefaults.cardColors(containerColor = BrandBorder.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Payment Security Parameters", style = Typography.bodyLarge, color = BrandDark)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Session Room Rate (4 Hours)", fontSize = 13.sp, color = BrandSecondaryText)
                        Text("₹12,000", fontSize = 13.sp, color = BrandDark, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Booking Service Fee", fontSize = 13.sp, color = BrandSecondaryText)
                        Text("₹450", fontSize = 13.sp, color = BrandDark, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(Dimens.sp1))
                    Divider(color = BrandBorder)
                    Spacer(modifier = Modifier.height(Dimens.sp1))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Secure Deposit", style = Typography.bodyLarge, color = BrandDark)
                        Text("₹12,450", style = Typography.bodyLarge, color = BrandPrimary)
                    }

                    Spacer(modifier = Modifier.height(Dimens.sp2))
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.VerifiedUser, null, tint = BrandSuccess, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(Dimens.sp1))
                        Text(
                            text = "MusicLinkd Assurance: Payments remain held until the mixing studio delivers final sessional wav/stems. Complete refund guaranteed if cancel parameters match guidelines.",
                            fontSize = 11.sp,
                            color = BrandSecondaryText,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Confirm Book CTA
        item {
            Spacer(modifier = Modifier.height(Dimens.sp1))
            Button(
                onClick = onBookingConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(99.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text("Lock Calendar & Pay Deposit", style = Typography.bodyLarge, color = Color.White)
            }
        }
    }
}

// ======================================================================================
// SCREEN 10: CONTACT DETAILS LAYOUT
// ======================================================================================
@Composable
fun ContactInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.r3),
        colors = CardDefaults.cardColors(containerColor = BrandCard),
        border = BorderStroke(1.dp, BrandBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // MOCK INTERACTIVE MAP GRAPHIC
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(Dimens.r2))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LocationOn, null, tint = BrandAccent, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(Dimens.sp1))
                    Text("Bandra West, Link Road, Mumbai", style = Typography.bodyLarge, color = BrandDark)
                    Text("Interactive G-Map Mockup Active", fontSize = 10.sp, color = BrandSecondaryText)
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            // Contact items
            ContactRow(icon = Icons.Default.Phone, label = "Phone Support", value = "+91 98765 43210")
            ContactRow(icon = Icons.Default.Chat, label = "Official WhatsApp", value = "Secure Client Desk Verified")
            ContactRow(icon = Icons.Default.Email, label = "Inquiries Email", value = "booking@octavesoundlabs.com")
            ContactRow(icon = Icons.Default.Language, label = "Website Domain", value = "octavesoundlabs.com")

            Spacer(modifier = Modifier.height(Dimens.sp2))
            Button(
                onClick = { /* Launch Maps intent */ },
                colors = ButtonDefaults.buttonColors(containerColor = BrandDark),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(99.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Directions, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(Dimens.sp1))
                    Text("Get Directions", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun ContactRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(Dimens.sp2))
        Column {
            Text(label, fontSize = 11.sp, color = BrandSecondaryText)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandDark)
        }
    }
}

// ======================================================================================
// SCREEN 11: RELATED BIZ CARD COMPONENT
// ======================================================================================
@Composable
fun RelatedBizCard(item: RelatedBiz) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r3)),
        shape = RoundedCornerShape(Dimens.r3),
        colors = CardDefaults.cardColors(containerColor = BrandCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .clip(RoundedCornerShape(Dimens.r2))
                    .background(item.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(item.bannerLetter, style = Typography.bodyLarge, color = Color.White)
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = BrandDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFBBF24), modifier = Modifier.size(12.dp))
                Text(" ${item.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                Spacer(modifier = Modifier.width(6.dp))
                Text("• ${item.category}", fontSize = 11.sp, color = BrandSecondaryText)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(item.location, fontSize = 11.sp, color = BrandSecondaryText)
            Spacer(modifier = Modifier.height(Dimens.sp1))
            Text(item.startingPrice, style = Typography.bodyLarge, color = BrandPrimary)
        }
    }
}

// ======================================================================================
// SCREEN 12: OWNER BUSINESS INSIGHTS (MONETIZATION & CHARTS)
// ======================================================================================
@Composable
fun BusinessOwnerInsightsScreen(onUpgradeClick: () -> Unit) {
    var insightPeriod by remember { mutableStateOf("This Month") }
    var showUpgradeDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
    ) {
        // Stats header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Storefront Insights", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = BrandDark)
                    Text("Octave Sound Labs Mumbai", fontSize = 13.sp, color = BrandSecondaryText)
                }

                // Time selector
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(BrandBorder.copy(alpha = 0.5f))
                        .clickable {
                            insightPeriod = if (insightPeriod == "This Month") "This Week" else "This Month"
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(insightPeriod, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandDark)
                }
            }
        }

        // LEADS, VIEW COUNTERS & REVENUE (SCREEN 12)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
                    InsightMetricCard(
                        title = "Views",
                        value = if (insightPeriod == "This Month") "4,890" else "1,120",
                        growth = "+14.8%",
                        icon = Icons.Default.Visibility,
                        modifier = Modifier.weight(1f)
                    )
                    InsightMetricCard(
                        title = "Leads Sourced",
                        value = if (insightPeriod == "This Month") "124" else "32",
                        growth = "+28.2%",
                        icon = Icons.Default.Chat,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
                    InsightMetricCard(
                        title = "Confirmed Bookings",
                        value = if (insightPeriod == "This Month") "42" else "11",
                        growth = "+8.4%",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                    InsightMetricCard(
                        title = "Estimated Revenue",
                        value = if (insightPeriod == "This Month") "₹5,04,000" else "₹1,32,000",
                        growth = "+19.5%",
                        icon = Icons.Default.CurrencyRupee,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // REVENUE GROWTH CHARTS (SCREEN 12)
        item {
            Spacer(modifier = Modifier.height(Dimens.sp1))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.r3),
                border = BorderStroke(1.dp, BrandBorder),
                colors = CardDefaults.cardColors(containerColor = BrandCard)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Revenue Trends & Inquiries Tracker", style = Typography.bodyLarge, color = BrandDark)
                    Text("Visualizing payment booking milestones across fiscal cycles.", fontSize = 11.sp, color = BrandSecondaryText)
                    
                    Spacer(modifier = Modifier.height(Dimens.sp3))

                    // Premium Chart Simulation using simple layout bars with labels
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
                        val values = listOf(40, 60, 50, 85, 75, 110)

                        months.forEachIndexed { i, m ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(values[i].dp)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(BrandPrimary, BrandAccent)
                                            )
                                        )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(m, fontSize = 10.sp, color = BrandSecondaryText, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // UPGRADE MONETIZATION TOUCHPOINTS CTA (SCREEN 12)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.r3),
                colors = CardDefaults.cardColors(containerColor = BrandDark)
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.sp3),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Dimens.r1))
                            .background(BrandAccent)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("PRO BUSINESS ACTIVE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(Dimens.sp2))
                    Text(
                        text = "Unlock Elite Lead Routing",
                        style = Typography.bodyLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(Dimens.sp1))
                    Text(
                        text = "MusicLinkd Pro Business prioritizes your studio booking slots in search algorithms, matches verified client budgets directly to your board, and waives sessional booking fees.",
                        color = Color.White.copy(alpha = 0.7f),
                        style = Typography.labelLarge,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { showUpgradeDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(99.dp)
                    ) {
                        Text("Explore Premium Tier Perks", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    if (showUpgradeDialog) {
        AlertDialog(
            onDismissRequest = { showUpgradeDialog = false },
            title = {
                Text("MusicLinkd Elite Tier Benefits", style = Typography.bodyLarge, color = BrandDark)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.sp1)) {
                    BenefitRow(title = "Top Ranking", desc = "Get featured under Bandra/Mumbai recording nodes.")
                    BenefitRow(title = "Zero Commission", desc = "Pay 0% transaction fees on standard booking deposits.")
                    BenefitRow(title = "Dolby Badges", desc = "Showcase certified Dolby Atmos status verified on network.")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showUpgradeDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            shape = RoundedCornerShape(Dimens.r3),
            containerColor = Color.White
        )
    }
}

@Composable
fun BenefitRow(title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(Icons.Default.Check, null, tint = BrandSuccess, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(Dimens.sp1))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = BrandDark)
            Text(desc, fontSize = 11.5.sp, color = BrandSecondaryText)
        }
    }
}

@Composable
fun InsightMetricCard(
    title: String,
    value: String,
    growth: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.r3),
        colors = CardDefaults.cardColors(containerColor = BrandCard),
        border = BorderStroke(1.dp, BrandBorder)
    ) {
        Column(modifier = Modifier.padding(Dimens.sp2)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = Typography.labelLarge, color = BrandSecondaryText, fontWeight = FontWeight.Bold)
                Icon(icon, null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, style = Typography.bodyLarge, color = BrandDark)
            Spacer(modifier = Modifier.height(2.dp))
            Text(growth, fontSize = 11.sp, color = BrandSuccess, fontWeight = FontWeight.Bold)
        }
    }
}

// ======================================================================================
// FULL SCREEN GALLERY ZOOM MODAL OVERLAY
// ======================================================================================
@Composable
fun FullScreenImageOverlay(
    imageIndex: Int,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .clickable { onClose() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(300.dp)
                    .clip(RoundedCornerShape(Dimens.r3))
                    .background(
                        when (imageIndex) {
                            0 -> Brush.linearGradient(colors = listOf(BrandPrimary, BrandPrimary.copy(alpha = 0.6f)))
                            1 -> Brush.linearGradient(colors = listOf(Color(0xFF10B981), BrandPrimary))
                            else -> Brush.linearGradient(colors = listOf(BrandPrimary, Color(0xFFFFB2D2)))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (imageIndex) {
                        0 -> "🎛️"
                        1 -> "🎹"
                        else -> "🎙️"
                    },
                    fontSize = 72.sp
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.sp2))
            Text(
                text = when (imageIndex) {
                    0 -> "SSL Control Suite A - Flawless acoustic calibration."
                    1 -> "Yamaha C7 custom grand piano stereo capture setup."
                    else -> "Premium Neumann vocal microphones configuration."
                },
                color = Color.White,
                style = Typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(Dimens.sp3))
            Text("Tap anywhere to close zoom preview", color = Color.White.copy(alpha = 0.5f), style = Typography.labelLarge)
        }
    }
}
