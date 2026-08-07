package com.example.onboarding.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

// --------------------------------------------------------------------------------------
// DESIGN SYSTEM DESIGN TOKENS (Pink #EA4C89, Dark #0D0B26)
// --------------------------------------------------------------------------------------
private val BrandPrimary = Color(0xFFEA4C89) // Brand Primary Pink
private val BrandAccent = Color(0xFFEA4C89)  // Accent/Highlights Pink
private val BrandDark = Color(0xFF0D0B26)    // Primary Dark Navy
private val BrandBackground = Color(0xFFFCFCFD)
private val BrandCard = Color(0xFFFFFFFF)
private val BrandBorder = Color(0xFFECECF1)
private val BrandSuccess = Color(0xFF10B981)
private val BrandTextPrimary = Color(0xFF0D0B26)
private val BrandTextSecondary = Color(0xFF7D7D8E)

enum class StudioBookingScreen {
    DISCOVERY,
    SEARCH_RESULTS,
    STUDIO_PROFILE,
    EQUIPMENT_DETAILS,
    CALENDAR_SLOTS,
    CONFIGURE_SESSION,
    BOOKING_SUMMARY,
    PAYMENT_GATEWAY,
    CONFIRMATION_SUCCESS,
    MY_BOOKINGS,
    OWNER_DASHBOARD
}

data class StudioSpace(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val location: String,
    val rating: Double,
    val reviewCount: Int,
    val pricePerHour: Int,
    val description: String,
    val amenities: List<String>,
    val equipmentList: List<StudioEquipment>,
    val isVerified: Boolean = true,
    val hasAtmos: Boolean = false,
    val coverEmoji: String = "🎙️",
    val imageUrl: String? = null,
    val availableDates: List<String> = listOf("2026-06-27", "2026-06-28", "2026-06-29", "2026-06-30"),
    val availableSlots: List<String> = listOf("10:00 AM - 02:00 PM", "02:00 PM - 06:00 PM", "06:00 PM - 10:00 PM")
)

data class StudioEquipment(
    val name: String,
    val type: String,
    val emoji: String,
    val quantity: Int,
    val condition: String = "Pristine",
    val isIncluded: Boolean = true
)

data class StudioBooking(
    val id: String = "BK-${(100000..999999).random()}",
    val studio: StudioSpace,
    val date: String,
    val timeSlot: String,
    val sessionType: String,
    val duration: String,
    val hasEngineer: Boolean,
    val extraAddons: List<String>,
    val totalPaid: Int,
    val status: String = "Confirmed" // Confirmed, Completed, Cancelled
)

// --------------------------------------------------------------------------------------
// MAIN CONTAINER FLOW
// --------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioBookingMainFlowContainer(
    userName: String = "Vedanshu",
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Standard mock list
    val mockStudios = remember {
        listOf(
            StudioSpace(
                name = "Yash Raj Film Studios (YRF) - Sound Lab",
                location = "Andheri West, Mumbai",
                rating = 4.9,
                reviewCount = 188,
                pricePerHour = 8000,
                description = "The absolute gold standard of cinematic sound in India. Features Dolby Atmos Certified mixing suites and a massive sound-engineered 2,000 sq ft Live Room. Preferred by composers like A.R. Rahman.",
                amenities = listOf("Dolby Atmos", "Vocal Booth", "Live Room", "Valet Parking", "Resident Engineers", "Private Lounge", "24x7 Access"),
                equipmentList = listOf(
                    StudioEquipment("SSL Duality Delta Analog Console", "Console", "🎛️", 1),
                    StudioEquipment("Neumann U87 Ai Multi-pattern Mic", "Microphone", "🎙️", 4),
                    StudioEquipment("Genelec 8361A Smart Active Monitors", "Monitors", "🔊", 11),
                    StudioEquipment("Avid Pro Tools HDX Ultimate", "DAW", "💻", 1),
                    StudioEquipment("Rupert Neve Designs Preamp", "Preamp", "⚙️", 2)
                ),
                hasAtmos = true,
                coverEmoji = "🎛️",
                imageUrl = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=600&q=80"
            ),
            StudioSpace(
                name = "BeatRoute Spaces (Bandra)",
                location = "Bandra West, Mumbai",
                rating = 4.8,
                reviewCount = 94,
                pricePerHour = 3500,
                description = "Premium editorial rehearsal and tracking space preferred by top indie artists in Mumbai. Features high-end acoustic treatment, cozy vibes, and standard vocal and instrument recording gear.",
                amenities = listOf("Rehearsal Room", "Vocal Booth", "Chill Lounge", "Drums Included", "Parking Space", "Instruments Support"),
                equipmentList = listOf(
                    StudioEquipment("Universal Audio Apollo x16", "Audio Interface", "💻", 1),
                    StudioEquipment("Shure SM7B Vocal Microphone", "Microphone", "🎙️", 3),
                    StudioEquipment("Yamaha HS8 Active Studio Monitors", "Monitors", "🔊", 2),
                    StudioEquipment("Pearl Export 5-Piece Drum Kit", "Instruments", "🥁", 1),
                    StudioEquipment("Fender Twin Reverb Amp", "Amplifiers", "🎸", 2)
                ),
                hasAtmos = false,
                coverEmoji = "🥁",
                imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=600&q=80"
            ),
            StudioSpace(
                name = "Octave Sound Lab",
                location = "Indiranagar, Bengaluru",
                rating = 4.7,
                reviewCount = 72,
                pricePerHour = 4500,
                description = "State-of-the-art spatial production facility offering pristine recording, post-production mixing, and Dolby Atmos rendering. Perfect for podcasts, audiobooks, and electronic music tracking.",
                amenities = listOf("Dolby Atmos", "Vocal Booth", "Instruments Support", "24x7 Access", "Acoustics Optimized", "Fiber Internet"),
                equipmentList = listOf(
                    StudioEquipment("Rupert Neve Designs 5088 Analog Console", "Console", "🎛️", 1),
                    StudioEquipment("Neumann TLM 103 Condenser Mic", "Microphone", "🎙️", 2),
                    StudioEquipment("Focal Trio11 Be Monitors", "Monitors", "🔊", 2),
                    StudioEquipment("Ableton Suite & Logic Pro X", "DAW", "💻", 1)
                ),
                hasAtmos = true,
                coverEmoji = "🎙️",
                imageUrl = "https://images.unsplash.com/photo-1598653222000-6b7b7a552625?auto=format&fit=crop&w=600&q=80"
            ),
            StudioSpace(
                name = "The Humming Tree Live Room",
                location = "Vasant Kunj, Delhi",
                rating = 4.6,
                reviewCount = 51,
                pricePerHour = 2500,
                description = "A massive live performance space converted into a rehearsal recording room. Highly recommended for heavy bands, rock rehearsal, and multi-mic recording setups.",
                amenities = listOf("Live Room", "Drums Included", "Instruments Support", "24x7 Access", "Stage Monitors", "Bar & Diner Access"),
                equipmentList = listOf(
                    StudioEquipment("Yamaha TF5 Digital Mixing Console", "Console", "🎛️", 1),
                    StudioEquipment("Sennheiser e906 Dynamic Mic", "Microphone", "🎙️", 4),
                    StudioEquipment("Marshall JVM410H Guitar Amp Stack", "Amplifiers", "🎸", 2),
                    StudioEquipment("Mapex Armory Drum Kit", "Instruments", "🥁", 1)
                ),
                hasAtmos = false,
                coverEmoji = "🎸",
                imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=600&q=80"
            )
        )
    }

    var currentScreen by remember { mutableStateOf(StudioBookingScreen.DISCOVERY) }
    var selectedStudio by remember { mutableStateOf(mockStudios[0]) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Search filter state variables
    var filterAtmosOnly by remember { mutableStateOf(false) }
    var filterVocalBoothOnly by remember { mutableStateOf(false) }
    var filterLiveRoomOnly by remember { mutableStateOf(false) }
    var priceMaxSelected by remember { mutableStateOf(10000) }

    // Booking configuration state
    var selectedDate by remember { mutableStateOf("2026-06-27") }
    var selectedTimeSlot by remember { mutableStateOf("02:00 PM - 06:00 PM") }
    var sessionType by remember { mutableStateOf("Recording & Overdubs") }
    var durationSelection by remember { mutableStateOf("Half Day (4 Hours)") }
    var durationMultiplier by remember { mutableStateOf(4) }
    var withResidentEngineer by remember { mutableStateOf(true) }
    
    val selectedAddons = remember { mutableStateListOf<String>() }
    var appliedPromoCode by remember { mutableStateOf("") }
    var bookingFailedMessage by remember { mutableStateOf<String?>(null) }
    var showFiltersSheet by remember { mutableStateOf(false) }

    // Saved Bookings state
    val myBookings = remember {
        mutableStateListOf(
            StudioBooking(
                studio = mockStudios[1],
                date = "2026-06-20",
                timeSlot = "10:00 AM - 02:00 PM",
                sessionType = "Vocal Overdubs",
                duration = "Half Day (4 Hours)",
                hasEngineer = false,
                extraAddons = listOf("Parking Space"),
                totalPaid = 14000,
                status = "Completed"
            )
        )
    }

    var latestBookingId by remember { mutableStateOf("") }

    val filteredStudios = remember(searchQuery, filterAtmosOnly, filterVocalBoothOnly, filterLiveRoomOnly, priceMaxSelected) {
        mockStudios.filter { studio ->
            val matchesSearch = studio.name.contains(searchQuery, ignoreCase = true) || studio.location.contains(searchQuery, ignoreCase = true)
            val matchesAtmos = !filterAtmosOnly || studio.hasAtmos
            val matchesVocal = !filterVocalBoothOnly || studio.amenities.contains("Vocal Booth")
            val matchesLive = !filterLiveRoomOnly || studio.amenities.contains("Live Room")
            val matchesPrice = studio.pricePerHour <= priceMaxSelected
            matchesSearch && matchesAtmos && matchesVocal && matchesLive && matchesPrice
        }
    }

    // Success notifications / border flash banner triggers
    var showSuccessBanner by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // DYNAMIC INLINE SUCCESS BANNER
            AnimatedVisibility(
                visible = showSuccessBanner != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrandSuccess)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(Dimens.sp1))
                            Text(
                                text = showSuccessBanner ?: "",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = { showSuccessBanner = null },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            when (currentScreen) {
                StudioBookingScreen.DISCOVERY -> {
                    StudioDiscoveryScreen(
                        userName = userName,
                        studios = mockStudios,
                        onSearchClick = {
                            searchQuery = it
                            currentScreen = StudioBookingScreen.SEARCH_RESULTS
                        },
                        onSelectStudio = {
                            selectedStudio = it
                            currentScreen = StudioBookingScreen.STUDIO_PROFILE
                        },
                        onNavigateMyBookings = { currentScreen = StudioBookingScreen.MY_BOOKINGS },
                        onNavigateOwnerDashboard = { currentScreen = StudioBookingScreen.OWNER_DASHBOARD },
                        onBackClick = onBackToDashboard
                    )
                }

                StudioBookingScreen.SEARCH_RESULTS -> {
                    StudioSearchResultsScreen(
                        query = searchQuery,
                        studios = filteredStudios,
                        onBack = { currentScreen = StudioBookingScreen.DISCOVERY },
                        onQueryChange = { searchQuery = it },
                        onSelectStudio = {
                            selectedStudio = it
                            currentScreen = StudioBookingScreen.STUDIO_PROFILE
                        },
                        onOpenFilters = { showFiltersSheet = true }
                    )
                }

                StudioBookingScreen.STUDIO_PROFILE -> {
                    StudioProfileScreen(
                        studio = selectedStudio,
                        onBack = { currentScreen = StudioBookingScreen.SEARCH_RESULTS },
                        onBookClick = {
                            // Reset defaults for this booking
                            selectedDate = selectedStudio.availableDates[0]
                            selectedTimeSlot = selectedStudio.availableSlots[0]
                            selectedAddons.clear()
                            appliedPromoCode = ""
                            currentScreen = StudioBookingScreen.CALENDAR_SLOTS
                        },
                        onViewEquipment = { currentScreen = StudioBookingScreen.EQUIPMENT_DETAILS },
                        onShare = {
                            showSuccessBanner = "Studio link copied to clipboard!"
                        }
                    )
                }

                StudioBookingScreen.EQUIPMENT_DETAILS -> {
                    StudioEquipmentDetailsScreen(
                        studio = selectedStudio,
                        onBack = { currentScreen = StudioBookingScreen.STUDIO_PROFILE }
                    )
                }

                StudioBookingScreen.CALENDAR_SLOTS -> {
                    StudioAvailabilityCalendarScreen(
                        studio = selectedStudio,
                        selectedDate = selectedDate,
                        selectedSlot = selectedTimeSlot,
                        onDateSelect = { selectedDate = it },
                        onSlotSelect = { selectedTimeSlot = it },
                        onBack = { currentScreen = StudioBookingScreen.STUDIO_PROFILE },
                        onNext = { currentScreen = StudioBookingScreen.CONFIGURE_SESSION }
                    )
                }

                StudioBookingScreen.CONFIGURE_SESSION -> {
                    StudioConfigureSessionScreen(
                        sessionType = sessionType,
                        onSessionTypeChange = { sessionType = it },
                        duration = durationSelection,
                        onDurationChange = { label, hrs ->
                            durationSelection = label
                            durationMultiplier = hrs
                        },
                        hasEngineer = withResidentEngineer,
                        onEngineerChange = { withResidentEngineer = it },
                        selectedAddons = selectedAddons,
                        onBack = { currentScreen = StudioBookingScreen.CALENDAR_SLOTS },
                        onNext = { currentScreen = StudioBookingScreen.BOOKING_SUMMARY }
                    )
                }

                StudioBookingScreen.BOOKING_SUMMARY -> {
                    StudioBookingSummaryScreen(
                        studio = selectedStudio,
                        date = selectedDate,
                        timeSlot = selectedTimeSlot,
                        sessionType = sessionType,
                        duration = durationSelection,
                        durationMultiplier = durationMultiplier,
                        hasEngineer = withResidentEngineer,
                        selectedAddons = selectedAddons,
                        promoCode = appliedPromoCode,
                        onApplyPromo = { appliedPromoCode = it },
                        onBack = { currentScreen = StudioBookingScreen.CONFIGURE_SESSION },
                        onNext = { currentScreen = StudioBookingScreen.PAYMENT_GATEWAY }
                    )
                }

                StudioBookingScreen.PAYMENT_GATEWAY -> {
                    StudioPaymentScreen(
                        studioName = selectedStudio.name,
                        amount = calculateTotal(selectedStudio.pricePerHour, durationMultiplier, withResidentEngineer, selectedAddons, appliedPromoCode),
                        onBack = { currentScreen = StudioBookingScreen.BOOKING_SUMMARY },
                        onPaymentSuccess = {
                            val newBooking = StudioBooking(
                                studio = selectedStudio,
                                date = selectedDate,
                                timeSlot = selectedTimeSlot,
                                sessionType = sessionType,
                                duration = durationSelection,
                                hasEngineer = withResidentEngineer,
                                extraAddons = selectedAddons.toList(),
                                totalPaid = calculateTotal(selectedStudio.pricePerHour, durationMultiplier, withResidentEngineer, selectedAddons, appliedPromoCode),
                                status = "Confirmed"
                            )
                            myBookings.add(0, newBooking)
                            latestBookingId = newBooking.id
                            showSuccessBanner = "🎉 Payment Secured! Session Booked successfully!"
                            currentScreen = StudioBookingScreen.CONFIRMATION_SUCCESS
                        },
                        onPaymentFailed = {
                            bookingFailedMessage = "UPI Transaction Declined. Please check limit or use cards."
                            showSuccessBanner = "❌ Payment Failed. Please try another method."
                        }
                    )
                }

                StudioBookingScreen.CONFIRMATION_SUCCESS -> {
                    val currentBooking = myBookings.firstOrNull { it.id == latestBookingId } ?: myBookings[0]
                    StudioBookingSuccessScreen(
                        booking = currentBooking,
                        onExplore = { currentScreen = StudioBookingScreen.DISCOVERY },
                        onViewMyBookings = { currentScreen = StudioBookingScreen.MY_BOOKINGS }
                    )
                }

                StudioBookingScreen.MY_BOOKINGS -> {
                    StudioMyBookingsScreen(
                        bookings = myBookings,
                        onBack = { currentScreen = StudioBookingScreen.DISCOVERY },
                        onCancelBooking = { bookingId ->
                            val index = myBookings.indexOfFirst { it.id == bookingId }
                            if (index != -1) {
                                myBookings[index] = myBookings[index].copy(status = "Cancelled")
                                showSuccessBanner = "Booking $bookingId has been cancelled."
                            }
                        }
                    )
                }

                StudioBookingScreen.OWNER_DASHBOARD -> {
                    StudioOwnerDashboard(
                        onBack = { currentScreen = StudioBookingScreen.DISCOVERY }
                    )
                }
            }
        }

        // Custom Filters Bottom Sheet experience
        if (showFiltersSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFiltersSheet = false },
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter Spaces",
                            style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = BrandDark)
                        )
                        Text(
                            text = "Clear All",
                            color = BrandPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable {
                                filterAtmosOnly = false
                                filterVocalBoothOnly = false
                                filterLiveRoomOnly = false
                                priceMaxSelected = 10000
                            }
                        )
                    }

                    HorizontalDivider(color = BrandBorder)

                    Text("Capabilities & Amenities", style = Typography.bodyLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)
                    ) {
                        FilterPillToggle(
                            text = "Dolby Atmos",
                            isSelected = filterAtmosOnly,
                            onToggle = { filterAtmosOnly = !filterAtmosOnly }
                        )
                        FilterPillToggle(
                            text = "Vocal Booth",
                            isSelected = filterVocalBoothOnly,
                            onToggle = { filterVocalBoothOnly = !filterVocalBoothOnly }
                        )
                        FilterPillToggle(
                            text = "Live Room",
                            isSelected = filterLiveRoomOnly,
                            onToggle = { filterLiveRoomOnly = !filterLiveRoomOnly }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Max Price Per Hour: ₹$priceMaxSelected", style = Typography.bodyLarge)
                    Slider(
                        value = priceMaxSelected.toFloat(),
                        onValueChange = { priceMaxSelected = it.toInt() },
                        valueRange = 2000f..10000f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = BrandPrimary,
                            activeTrackColor = BrandPrimary,
                            inactiveTrackColor = BrandBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(Dimens.sp2))

                    Button(
                        onClick = { showFiltersSheet = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("apply_studio_filters_button"),
                        shape = RoundedCornerShape(99.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("Show Matching Studios (${filteredStudios.size})", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(Dimens.sp3))
                }
            }
        }
    }
}

// Helper calculation
private fun calculateTotal(
    basePricePerHour: Int,
    hours: Int,
    engineer: Boolean,
    addons: List<String>,
    promo: String
): Int {
    var total = basePricePerHour * hours
    if (engineer) {
        total += 1500 * hours // ₹1,500/hr extra for elite resident engineer
    }
    addons.forEach { addon ->
        when (addon) {
            "Valet Parking Space" -> total += 400
            "Artisan Coffee & Catering" -> total += 800
            "Live-stream Rig Multi-cam Setup" -> total += 2500
        }
    }
    if (promo.trim().uppercase() == "CREATOR50") {
        total /= 2 // 50% discount
    } else if (promo.trim().uppercase() == "MUSICLINKD") {
        total = (total * 0.9).toInt() // 10% off
    }
    return total
}

// --------------------------------------------------------------------------------------
// SCREEN 1: STUDIO DISCOVERY SCREEN
// --------------------------------------------------------------------------------------
@Composable
fun StudioDiscoveryScreen(
    userName: String,
    studios: List<StudioSpace>,
    onSearchClick: (String) -> Unit,
    onSelectStudio: (StudioSpace) -> Unit,
    onNavigateMyBookings: () -> Unit,
    onNavigateOwnerDashboard: () -> Unit,
    onBackClick: () -> Unit
) {
    var inputQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // TOP RETRO HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors = listOf(BrandDark, Color(0xFF1B1936))))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "MusicLinkd Spaces",
                            color = Color.White,
                            style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Floating Profile Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Dimens.r2))
                            .background(BrandPrimary)
                            .clickable { onNavigateMyBookings() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("My Bookings", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Find, compare and book India's elite creative spaces.",
                    color = Color.White,
                    style = Typography.bodyLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 32.sp,
                        letterSpacing = (-0.5).sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(99.dp))
                            .background(Color.White)
                            .border(1.dp, BrandBorder, RoundedCornerShape(99.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(Dimens.sp1))
                            BasicInputPlaceholder(
                                text = inputQuery,
                                onValueChange = { inputQuery = it },
                                placeholder = "Search Bandra, Dolby Atmos, vocal..."
                            )
                        }
                    }
                    Button(
                        onClick = { onSearchClick(inputQuery) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(99.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp)
                    ) {
                        Text("Go", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // MONETIZATION QUICK BAR - OWNER DASHBOARD INLINE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(Dimens.r2))
                .background(BrandAccent.copy(alpha = 0.08f))
                .border(1.dp, BrandAccent.copy(alpha = 0.2f), RoundedCornerShape(Dimens.r2))
                .clickable { onNavigateOwnerDashboard() }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text("🏢", style = Typography.headlineLarge)
                    Spacer(modifier = Modifier.width(Dimens.sp2))
                    Column {
                        Text("List your rehearsal space or studio?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                        Text("Manage bookings, invoicing & residents.", fontSize = 11.sp, color = BrandTextSecondary)
                    }
                }
                Icon(Icons.Default.ArrowForward, null, tint = BrandAccent, modifier = Modifier.size(16.dp))
            }
        }

        // SECTION 1: FEATURED STUDIOS
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Featured Sound Labs", style = Typography.bodyLarge, color = BrandDark)
                Text("See All", color = BrandPrimary, style = Typography.bodyLarge)
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sp2)
            ) {
                items(studios) { studio ->
                    FeaturedStudioCard(studio = studio, onClick = { onSelectStudio(studio) })
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 2: NEARBY spaces
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text(
                text = "Nearby Rehearsal & Live Rooms",
                style = Typography.bodyLarge,
                color = BrandDark,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            studios.forEach { studio ->
                StudioListItemCard(studio = studio, onClick = { onSelectStudio(studio) })
                Spacer(modifier = Modifier.height(Dimens.sp2))
            }
        }
    }
}

@Composable
fun BasicInputPlaceholder(
    text: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        if (text.isEmpty()) {
            Text(placeholder, color = BrandTextSecondary, style = Typography.labelLarge)
        }
        androidx.compose.foundation.text.BasicTextField(
            value = text,
            onValueChange = onValueChange,
            textStyle = Typography.bodyLarge.copy(color = BrandTextPrimary, style = Typography.labelLarge),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 3: SEARCH RESULTS
// --------------------------------------------------------------------------------------
@Composable
fun StudioSearchResultsScreen(
    query: String,
    studios: List<StudioSpace>,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSelectStudio: (StudioSpace) -> Unit,
    onOpenFilters: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // TOP SEARCH RESULTS BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BrandDark)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(99.dp))
                    .background(BrandBackground)
                    .border(1.dp, BrandBorder, RoundedCornerShape(99.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(Dimens.sp1))
                    BasicInputPlaceholder(
                        text = query,
                        onValueChange = onQueryChange,
                        placeholder = "Search spaces..."
                    )
                }
            }
            Spacer(modifier = Modifier.width(Dimens.sp1))
            IconButton(
                onClick = onOpenFilters,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary.copy(alpha = 0.08f))
            ) {
                Icon(Icons.Default.FilterList, "Filters", tint = BrandPrimary)
            }
        }

        if (studios.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏢", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(Dimens.sp2))
                    Text("No Studios Found Matching Selection", fontWeight = FontWeight.Bold, color = BrandDark)
                    Text("Try widening your price range or turning off amenities filters.", color = BrandTextSecondary, style = Typography.labelLarge, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Matching Studios (${studios.size})",
                        style = Typography.bodyLarge,
                        color = BrandDark
                    )
                }
                items(studios) { studio ->
                    StudioListItemCard(studio = studio, onClick = { onSelectStudio(studio) })
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 4: STUDIO PROFILE SCREEN
// --------------------------------------------------------------------------------------
@Composable
fun StudioProfileScreen(
    studio: StudioSpace,
    onBack: () -> Unit,
    onBookClick: () -> Unit,
    onViewEquipment: () -> Unit,
    onShare: () -> Unit
) {
    var activeTab by remember { mutableStateOf("About") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // COVER HERO WITH FLOATING ACTION BUTTONS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Brush.verticalGradient(colors = listOf(BrandDark, Color(0xFF2E2A4E))))
        ) {
            // Studio Cover Photo
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (studio.imageUrl != null) {
                    AsyncImage(
                        model = studio.imageUrl,
                        contentDescription = studio.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(studio.coverEmoji, fontSize = 84.sp)
                }
            }

            // Controls overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)) {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.Default.Share, "Share", tint = Color.White)
                    }
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.Default.FavoriteBorder, "Fav", tint = Color.White)
                    }
                }
            }

            // Dolby Atmos Tag overlay
            if (studio.hasAtmos) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(Dimens.r1))
                        .background(BrandPrimary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("DOLBY ATMOS 7.1.4 Certified", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // DETAILS BLOCK
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(studio.name, style = Typography.bodyLarge, color = BrandDark)
                        if (studio.isVerified) {
                            Spacer(modifier = Modifier.width(Dimens.sp1))
                            Icon(Icons.Default.Verified, "Verified", tint = BrandPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(studio.location, style = Typography.labelLarge, color = BrandTextSecondary)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("₹${studio.pricePerHour}/hr", style = Typography.bodyLarge, color = BrandPrimary)
                    Text("Base Booking Rate", fontSize = 10.sp, color = BrandTextSecondary)
                }
            }

            // Rating row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                Text(studio.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("(${studio.reviewCount} Reviews)", color = BrandTextSecondary, style = Typography.labelLarge)
            }

            // MAIN SECTIONS TABS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2))
                    .background(Color.White)
            ) {
                listOf("About", "Equipment & Gear", "Policies").forEach { tab ->
                    val isSelected = activeTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeTab = tab }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BrandPrimary else BrandTextSecondary
                        )
                    }
                }
            }

            when (activeTab) {
                "About" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
                        Text("About The Facility", style = Typography.bodyLarge)
                        Text(studio.description, style = Typography.labelLarge, color = BrandTextSecondary, lineHeight = 18.sp)
                        
                        Text("Elite Amenities Offered", style = Typography.bodyLarge)
                        FlowRowCustom(
                            items = studio.amenities,
                            color = BrandPrimary
                        )
                    }
                }

                "Equipment & Gear" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Resident Gear Showcase", style = Typography.bodyLarge)
                            Text(
                                text = "View Specifications",
                                color = BrandPrimary,
                                style = Typography.bodyLarge,
                                modifier = Modifier.clickable { onViewEquipment() }
                            )
                        }

                        studio.equipmentList.take(3).forEach { gear ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, BrandBorder, RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(gear.emoji, style = Typography.headlineLarge)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(gear.name, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                        Text("${gear.type} • Qty: ${gear.quantity}", fontSize = 10.sp, color = BrandTextSecondary)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .background(BrandSuccess.copy(alpha = 0.1f), RoundedCornerShape(Dimens.r1))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("INCLUDED", fontSize = 8.sp, color = BrandSuccess, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                "Policies" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Studio Code & Guidelines", style = Typography.bodyLarge)
                        listOf(
                            "❌ No smoking or alcoholic beverages allowed inside the live room or console bays.",
                            "🕒 Rescheduling must be initialized at least 24 hours in advance.",
                            "🎸 Outside session musicians are welcome, but gear checkout requires engineer authorization."
                        ).forEach { rule ->
                            Text(rule, fontSize = 11.5.sp, color = BrandTextSecondary, lineHeight = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            // PRIMARY RESERVE CTA
            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("reserve_studio_slot_button"),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Reserve Recording Session", style = Typography.bodyLarge)
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 5: EQUIPMENT DETAILS LISTING
// --------------------------------------------------------------------------------------
@Composable
fun StudioEquipmentDetailsScreen(
    studio: StudioSpace,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("Resident Equipment Room", style = Typography.bodyLarge, color = BrandDark)
                Text(studio.name, fontSize = 11.sp, color = BrandTextSecondary)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
        ) {
            items(studio.equipmentList) { gear ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BrandBorder),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(Dimens.sp2),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BrandPrimary.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(gear.emoji, style = Typography.headlineLarge)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(gear.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandDark)
                                Text("Category: ${gear.type} • Status: ${gear.condition}", fontSize = 11.sp, color = BrandTextSecondary)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .background(BrandSuccess.copy(alpha = 0.12f), RoundedCornerShape(Dimens.r1))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Included", color = BrandSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(Dimens.sp1))
                            Text("Qty: ${gear.quantity}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 6: AVAILABILITY CALENDAR & TIMELINE
// --------------------------------------------------------------------------------------
@Composable
fun StudioAvailabilityCalendarScreen(
    studio: StudioSpace,
    selectedDate: String,
    selectedSlot: String,
    onDateSelect: (String) -> Unit,
    onSlotSelect: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text("Select Date & Time Slot", style = Typography.bodyLarge)
        }

        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("Choose Reservation Date", style = Typography.bodyLarge)
            
            // Standard weekly calendar representation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)
            ) {
                listOf(
                    Triple("Sat", "27", "2026-06-27"),
                    Triple("Sun", "28", "2026-06-28"),
                    Triple("Mon", "29", "2026-06-29"),
                    Triple("Tue", "30", "2026-06-30")
                ).forEach { (day, num, rawDate) ->
                    val isSelected = selectedDate == rawDate
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(Dimens.r2))
                            .background(if (isSelected) BrandPrimary else Color.White)
                            .border(1.dp, if (isSelected) BrandPrimary else BrandBorder, RoundedCornerShape(Dimens.r2))
                            .clickable { onDateSelect(rawDate) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(day, fontSize = 11.sp, color = if (isSelected) Color.White else BrandTextSecondary)
                            Spacer(modifier = Modifier.height(Dimens.sp1))
                            Text(num, style = Typography.bodyLarge, color = if (isSelected) Color.White else BrandDark)
                        }
                    }
                }
            }

            Text("Select Time Block", style = Typography.bodyLarge)

            studio.availableSlots.forEach { slot ->
                val isSelected = selectedSlot == slot
                val isBooked = slot.contains("06:00 PM") // Simulate middle slot booked for testing
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) BrandPrimary.copy(alpha = 0.05f) else Color.White)
                        .border(
                            1.dp,
                            if (isSelected) BrandPrimary else BrandBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable(enabled = !isBooked) { onSlotSelect(slot) }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isSelected) BrandPrimary else BrandTextSecondary
                        )
                        Spacer(modifier = Modifier.width(Dimens.sp2))
                        Text(
                            text = slot,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = if (isBooked) BrandTextSecondary else BrandDark
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (isBooked) Color.Red.copy(alpha = 0.1f) else if (isSelected) BrandPrimary.copy(alpha = 0.12f) else BrandSuccess.copy(alpha = 0.1f),
                                RoundedCornerShape(Dimens.r1)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isBooked) "BOOKED" else if (isSelected) "SELECTED" else "AVAILABLE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBooked) Color.Red else if (isSelected) BrandPrimary else BrandSuccess
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp3))

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("next_studio_config_button"),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Configure Session Details", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 7: CONFIGURE SESSION DETAILS
// --------------------------------------------------------------------------------------
@Composable
fun StudioConfigureSessionScreen(
    sessionType: String,
    onSessionTypeChange: (String) -> Unit,
    duration: String,
    onDurationChange: (String, Int) -> Unit,
    hasEngineer: Boolean,
    onEngineerChange: (Boolean) -> Unit,
    selectedAddons: MutableList<String>,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text("Configure Your Booking", style = Typography.bodyLarge)
        }

        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("Choose Session Mode", style = Typography.bodyLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)
            ) {
                listOf("Recording & Overdubs", "Mixing & Mastering", "Podcast & Foley").forEach { type ->
                    val isSelected = sessionType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) BrandPrimary else Color.White)
                            .border(1.dp, if (isSelected) BrandPrimary else BrandBorder, RoundedCornerShape(10.dp))
                            .clickable { onSessionTypeChange(type) }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else BrandDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Text("Session Duration Selection", style = Typography.bodyLarge)
            listOf(
                Triple("Hourly Session", "₹ Base hourly rate applies.", 1),
                Triple("Half Day (4 Hours)", "₹ Save 10% off hourly fee.", 4),
                Triple("Full Day (8 Hours)", "🔥 Save 20% + VIP resident lounge.", 8)
            ).forEach { (label, desc, hrs) ->
                val isSelected = duration == label
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.r2))
                        .background(if (isSelected) BrandPrimary.copy(alpha = 0.05f) else Color.White)
                        .border(1.dp, if (isSelected) BrandPrimary else BrandBorder, RoundedCornerShape(Dimens.r2))
                        .clickable { onDurationChange(label, hrs) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(label, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        Text(desc, fontSize = 10.5.sp, color = BrandTextSecondary)
                    }
                    RadioButton(
                        selected = isSelected,
                        onClick = { onDurationChange(label, hrs) },
                        colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary)
                    )
                }
            }

            Text("Add Elite Resident Engineer?", style = Typography.bodyLarge)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.r2))
                    .background(Color.White)
                    .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Resident Senior Engineer (+₹1,500/hr)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Text("Guarantees state-of-the-art console routing, mic setups & live monitoring tracks.", fontSize = 10.5.sp, color = BrandTextSecondary)
                }
                Switch(
                    checked = hasEngineer,
                    onCheckedChange = onEngineerChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandPrimary)
                )
            }

            Text("Exclusive Add-ons (Optional)", style = Typography.bodyLarge)
            listOf(
                Triple("Valet Parking Space", "+₹400 per slot", "🚗"),
                Triple("Artisan Coffee & Catering", "+₹800 complete service", "☕"),
                Triple("Live-stream Rig Multi-cam Setup", "+₹2,500 flat fee", "📹")
            ).forEach { (name, fee, emoji) ->
                val isChecked = selectedAddons.contains(name)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.r2))
                        .background(Color.White)
                        .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2))
                        .clickable {
                            if (isChecked) selectedAddons.remove(name) else selectedAddons.add(name)
                        }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(emoji, style = Typography.bodyLarge)
                        Spacer(modifier = Modifier.width(Dimens.sp2))
                        Column {
                            Text(name, style = Typography.bodyLarge)
                            Text(fee, fontSize = 10.5.sp, color = BrandTextSecondary)
                        }
                    }
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            if (isChecked) selectedAddons.remove(name) else selectedAddons.add(name)
                        },
                        colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("reserve_config_next_button"),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Proceed to Review", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 8: BOOKING SUMMARY & REVIEW
// --------------------------------------------------------------------------------------
@Composable
fun StudioBookingSummaryScreen(
    studio: StudioSpace,
    date: String,
    timeSlot: String,
    sessionType: String,
    duration: String,
    durationMultiplier: Int,
    hasEngineer: Boolean,
    selectedAddons: List<String>,
    promoCode: String,
    onApplyPromo: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    var promoInput by remember { mutableStateOf(promoCode) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text("Review Studio Session", style = Typography.bodyLarge)
        }

        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            // CONFIRMATION SUMMARY BOX
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BrandBorder),
                shape = RoundedCornerShape(Dimens.r2)
            ) {
                Column(modifier = Modifier.padding(Dimens.sp2), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Studio Reservation", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandPrimary)
                    Text(studio.name, style = Typography.bodyLarge, color = BrandDark)
                    Text("Location: ${studio.location}", style = Typography.labelLarge, color = BrandTextSecondary)
                    
                    HorizontalDivider(color = BrandBorder)

                    Text("Date: $date", style = Typography.bodyLarge)
                    Text("Selected slot: $timeSlot", style = Typography.bodyLarge)
                    Text("Mode: $sessionType", style = Typography.bodyLarge)
                    Text("Block Type: $duration", style = Typography.bodyLarge)
                }
            }

            Text("Apply Promo Code", style = Typography.bodyLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sp1),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color.White)
                        .border(1.dp, BrandBorder, RoundedCornerShape(99.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    BasicInputPlaceholder(
                        text = promoInput,
                        onValueChange = { promoInput = it },
                        placeholder = "Enter CREATOR50 or MUSICLINKD"
                    )
                }
                Button(
                    onClick = { onApplyPromo(promoInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Text("Apply", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            if (promoCode.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrandSuccess.copy(alpha = 0.1f), RoundedCornerShape(Dimens.r1))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Promo applied successfully! Discount verified.",
                        color = BrandSuccess,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text("Session Fee Breakdown", style = Typography.bodyLarge)
            
            // Detailed breakdown
            val baseCost = studio.pricePerHour * durationMultiplier
            val engineerCost = if (hasEngineer) 1500 * durationMultiplier else 0
            val addonCosts = selectedAddons.sumOf { addon ->
                when (addon) {
                    "Valet Parking Space" -> 400
                    "Artisan Coffee & Catering" -> 800
                    "Live-stream Rig Multi-cam Setup" -> 2500
                    else -> 0
                }
            }
            val subTotal = baseCost + engineerCost + addonCosts
            val discount = if (promoCode.uppercase() == "CREATOR50") subTotal / 2 else if (promoCode.uppercase() == "MUSICLINKD") (subTotal * 0.1).toInt() else 0
            val totalPaid = subTotal - discount

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.r2))
                    .background(Color.White)
                    .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Studio Hire Rate (₹${studio.pricePerHour} × $durationMultiplier hrs)", style = Typography.labelLarge, color = BrandTextSecondary)
                    Text("₹$baseCost", style = Typography.bodyLarge)
                }
                if (hasEngineer) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Senior Resident Engineer (₹1,500 × $durationMultiplier hrs)", style = Typography.labelLarge, color = BrandTextSecondary)
                        Text("₹$engineerCost", style = Typography.bodyLarge)
                    }
                }
                selectedAddons.forEach { addon ->
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(addon, style = Typography.labelLarge, color = BrandTextSecondary)
                        Text(
                            text = when (addon) {
                                "Valet Parking Space" -> "₹400"
                                "Artisan Coffee & Catering" -> "₹800"
                                "Live-stream Rig Multi-cam Setup" -> "₹2500"
                                else -> "₹0"
                            },
                            style = Typography.bodyLarge
                        )
                    }
                }

                if (discount > 0) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Promo Discount applied ($promoCode)", style = Typography.labelLarge, color = BrandSuccess, fontWeight = FontWeight.Bold)
                        Text("-₹$discount", style = Typography.labelLarge, color = BrandSuccess, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = BrandBorder)

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Total Session Quote", style = Typography.bodyLarge)
                    Text("₹$totalPaid", style = Typography.bodyLarge, color = BrandPrimary)
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("summary_proceed_payment_button"),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Proceed to Secure Payment", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 9: SIMULATED PAYMENT GATEWAY
// --------------------------------------------------------------------------------------
@Composable
fun StudioPaymentScreen(
    studioName: String,
    amount: Int,
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    onPaymentFailed: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("UPI") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)
            ) {
                Text("Secure Checkout Gateway", style = Typography.bodyLarge)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFECEE))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "COMING SOON",
                        color = Color(0xFFEA4C89),
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            // Price card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandDark),
                shape = RoundedCornerShape(Dimens.r2)
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.sp3),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Securing Studio Slot at", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Text(studioName, color = Color.White, style = Typography.bodyLarge, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(Dimens.sp2))
                    Text("₹$amount", color = BrandAccent, fontWeight = FontWeight.Black, fontSize = 32.sp)
                    Text("Guaranteed 100% Booking Refund Policy", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                }
            }

            Text("Choose Payment Method", style = Typography.bodyLarge)

            listOf(
                Triple("UPI (Google Pay / PhonePe)", "Instant routing secure session lock.", "UPI"),
                Triple("Credit or Debit Card", "Secure checkout with credit protection.", "Card"),
                Triple("MusicLinkd Wallet Balance", "Use creator credits or vouchers.", "Wallet")
            ).forEach { (label, desc, code) ->
                val isSelected = selectedMethod == code
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.r2))
                        .background(if (isSelected) BrandPrimary.copy(alpha = 0.05f) else Color.White)
                        .border(1.dp, if (isSelected) BrandPrimary else BrandBorder, RoundedCornerShape(Dimens.r2))
                        .clickable { selectedMethod = code }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(label, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = BrandDark)
                        Text(desc, fontSize = 10.5.sp, color = BrandTextSecondary)
                    }
                    RadioButton(
                        selected = isSelected,
                        onClick = { selectedMethod = code },
                        colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.sp2))

            Button(
                onClick = onPaymentSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("securely_pay_cta_button"),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Authorize & Secure Payment", style = Typography.bodyLarge)
            }

            TextButton(
                onClick = onPaymentFailed,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Simulate Payment Failure Scenario", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 10: CONFIRMATION SUCCESS WITH GORGEOUS RECEIPT
// --------------------------------------------------------------------------------------
@Composable
fun StudioBookingSuccessScreen(
    booking: StudioBooking,
    onExplore: () -> Unit,
    onViewMyBookings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dimens.sp4))

        // Large high-fidelity illustration checkmark
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(BrandSuccess.copy(alpha = 0.12f))
                .border(2.dp, BrandSuccess, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = BrandSuccess, modifier = Modifier.size(52.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Booking Secured!",
            style = Typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold, color = BrandDark)
        )
        Text(
            text = "Session confirmation sent to your email & registered SMS.",
            style = Typography.labelLarge,
            color = BrandTextSecondary,
            modifier = Modifier.padding(horizontal = 24.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.sp3))

        // Visual Receipt Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BrandBorder),
            shape = RoundedCornerShape(Dimens.r2)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("STUDIO SESSION PASS", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = BrandPrimary)
                    Text("ID: ${booking.id}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandTextSecondary)
                }

                HorizontalDivider(color = BrandBorder)

                Text(booking.studio.name, style = Typography.bodyLarge)
                Text("Location: ${booking.studio.location}", fontSize = 11.5.sp, color = BrandTextSecondary)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("RESERVATION DATE", fontSize = 9.sp, color = BrandTextSecondary)
                        Text(booking.date, style = Typography.bodyLarge)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("TIME SLOT BLOCK", fontSize = 9.sp, color = BrandTextSecondary)
                        Text(booking.timeSlot, style = Typography.bodyLarge)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("ENGINEER SUPPORT", fontSize = 9.sp, color = BrandTextSecondary)
                        Text(if (booking.hasEngineer) "Yes, Resident" else "No (Self Tracked)", style = Typography.bodyLarge)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("TOTAL PAID SECURE", fontSize = 9.sp, color = BrandTextSecondary)
                        Text("₹${booking.totalPaid}", style = Typography.bodyLarge, color = BrandSuccess)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.sp3))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(Dimens.sp2)
        ) {
            OutlinedButton(
                onClick = onViewMyBookings,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(99.dp),
                border = BorderStroke(1.dp, BrandPrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandPrimary)
            ) {
                Text("Manage Bookings", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onExplore,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(99.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Explore Studios", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// SCREEN 11: MY BOOKINGS SCREEN
// --------------------------------------------------------------------------------------
@Composable
fun StudioMyBookingsScreen(
    bookings: List<StudioBooking>,
    onBack: () -> Unit,
    onCancelBooking: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Upcoming") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text("My Bookings Feed", style = Typography.bodyLarge)
        }

        // TABS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
        ) {
            listOf("Upcoming", "Past & Cancelled").forEach { tab ->
                val isSelected = (tab == "Upcoming" && selectedTab == "Upcoming") || (tab == "Past & Cancelled" && selectedTab == "Past")
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = if (tab == "Upcoming") "Upcoming" else "Past" }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = tab,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BrandPrimary else BrandTextSecondary,
                            fontSize = 12.5.sp
                        )
                        Spacer(modifier = Modifier.height(Dimens.sp1))
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(if (isSelected) 40.dp else 0.dp)
                                .background(BrandPrimary)
                        )
                    }
                }
            }
        }

        val filteredList = remember(bookings, selectedTab) {
            bookings.filter { booking ->
                if (selectedTab == "Upcoming") booking.status == "Confirmed"
                else booking.status == "Completed" || booking.status == "Cancelled"
            }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = BrandTextSecondary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(Dimens.sp2))
                    Text("No bookings matching selection.", fontWeight = FontWeight.Bold, color = BrandDark)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredList) { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BrandBorder),
                        shape = RoundedCornerShape(Dimens.r2)
                    ) {
                        Column(
                            modifier = Modifier.padding(Dimens.sp2),
                            verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(Dimens.r1)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (booking.studio.imageUrl != null) {
                                            AsyncImage(
                                                model = booking.studio.imageUrl,
                                                contentDescription = booking.studio.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Text(booking.studio.coverEmoji, style = Typography.headlineLarge)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(Dimens.sp1))
                                    Text(booking.studio.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (booking.status == "Confirmed") BrandSuccess.copy(alpha = 0.1f) else if (booking.status == "Completed") BrandPrimary.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f),
                                            RoundedCornerShape(Dimens.r1)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = booking.status.uppercase(),
                                        fontSize = 8.sp,
                                        color = if (booking.status == "Confirmed") BrandSuccess else if (booking.status == "Completed") BrandPrimary else Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            HorizontalDivider(color = BrandBorder)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("DATE & TIME SLOT", fontSize = 9.sp, color = BrandTextSecondary)
                                    Text("${booking.date} • ${booking.timeSlot}", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("PAID SECURE", fontSize = 9.sp, color = BrandTextSecondary)
                                    Text("₹${booking.totalPaid}", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                }
                            }

                            if (booking.status == "Confirmed") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Dimens.sp1)
                                ) {
                                    OutlinedButton(
                                        onClick = { onCancelBooking(booking.id) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(Dimens.r1),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                        border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                                    ) {
                                        Text("Cancel Slot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(Dimens.r1),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                                    ) {
                                        Text("Directions", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

// --------------------------------------------------------------------------------------
// BUSINESS DASHBOARD (STUDIO OWNER HUB)
// --------------------------------------------------------------------------------------
@Composable
fun StudioOwnerDashboard(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, BrandBorder)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("Studio Owner Workspace", style = Typography.bodyLarge, color = BrandDark)
                Text("Managing Yash Raj Film Studios (YRF)", fontSize = 11.sp, color = BrandTextSecondary)
            }
        }

        Column(modifier = Modifier.padding(Dimens.sp2), verticalArrangement = Arrangement.spacedBy(Dimens.sp2)) {
            // REVENUE METRIC HERO
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BrandDark),
                shape = RoundedCornerShape(Dimens.r3)
            ) {
                Column(modifier = Modifier.padding(Dimens.sp3)) {
                    Text("TODAY'S ACCUMULATED REVENUE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("₹42,000", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(Dimens.sp2))
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sp3)) {
                        Column {
                            Text("HOURS BOOKED", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp)
                            Text("18 Hrs Today", color = Color.White, style = Typography.bodyLarge)
                        }
                        Column {
                            Text("ACTIVE ENGINES", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp)
                            Text("2 Rooms Live", color = Color.White, style = Typography.bodyLarge)
                        }
                    }
                }
            }

            Text("Current Live Sessions (Today)", style = Typography.bodyLarge)
            
            // List of today's live sessions
            listOf(
                Triple("Vocal Tracking session - Live Room A", "Aditi Sen • 10 AM - 02 PM", "LIVE"),
                Triple("Podcast Recording & Foley Room C", "Karan Saxena • 02 PM - 06 PM", "PENDING"),
                Triple("Spatial Atmos Mixing Room B", "A.R. Rahman • 06 PM - 10 PM", "CONFIRMED")
            ).forEach { (title, subtitle, status) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.r2))
                        .background(Color.White)
                        .border(1.dp, BrandBorder, RoundedCornerShape(Dimens.r2))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(title, style = Typography.bodyLarge, color = BrandDark)
                        Text(subtitle, fontSize = 10.5.sp, color = BrandTextSecondary)
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                if (status == "LIVE") BrandAccent.copy(alpha = 0.12f) else if (status == "CONFIRMED") BrandSuccess.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f),
                                RoundedCornerShape(Dimens.r1)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = status,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (status == "LIVE") BrandAccent else if (status == "CONFIRMED") BrandSuccess else Color.Red
                        )
                    }
                }
            }

            Text("Equipment Status Monitor", style = Typography.bodyLarge)
            
            // Equipment health checks
            listOf(
                "SSL Duality Delta Console - Active",
                "Neumann U87 Ai Microphone - Sanitized & In Booth",
                "Genelec Active Monitors - Self-calibrated OK"
            ).forEach { checklist ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = BrandSuccess, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(checklist, fontSize = 11.5.sp, color = BrandTextSecondary)
                }
            }
        }
    }
}

// --------------------------------------------------------------------------------------
// HELPER COMPOSABLES
// --------------------------------------------------------------------------------------

@Composable
fun FeaturedStudioCard(
    studio: StudioSpace,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BrandBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
            ) {
                if (studio.imageUrl != null) {
                    AsyncImage(
                        model = studio.imageUrl,
                        contentDescription = studio.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(studio.coverEmoji, fontSize = 40.sp, modifier = Modifier.align(Alignment.Center))
                }
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = studio.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = BrandDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = studio.location.split(",")[0],
                        fontSize = 9.sp,
                        color = BrandTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 4.dp)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("₹${if (studio.pricePerHour >= 1000) "${studio.pricePerHour/1000}k" else studio.pricePerHour.toString()}", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = BrandDark)
                        Text("/hr", fontSize = 8.sp, color = BrandTextSecondary)
                    }
                    Text("${studio.rating} ★", fontSize = 8.sp, color = Color(0xFFF59E0B))
                }
            }
        }
    }
}

@Composable
fun StudioListItemCard(
    studio: StudioSpace,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BrandBorder),
        shape = RoundedCornerShape(Dimens.r2)
    ) {
        Row(
            modifier = Modifier.padding(Dimens.sp2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(Dimens.r2))
                    .background(BrandPrimary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                if (studio.imageUrl != null) {
                    AsyncImage(
                        model = studio.imageUrl,
                        contentDescription = studio.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(studio.coverEmoji, fontSize = 32.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = studio.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BrandDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = studio.location,
                    fontSize = 11.sp,
                    color = BrandTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                    Text(studio.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("(${studio.reviewCount} reviews)", color = BrandTextSecondary, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.width(Dimens.sp1))

            Column(horizontalAlignment = Alignment.End) {
                Text("₹${studio.pricePerHour}", style = Typography.bodyLarge, color = BrandPrimary)
                Text("per hour", fontSize = 9.sp, color = BrandTextSecondary)
            }
        }
    }
}

@Composable
fun FilterPillToggle(
    text: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(if (isSelected) BrandPrimary else Color.White)
            .border(1.dp, if (isSelected) BrandPrimary else BrandBorder, RoundedCornerShape(99.dp))
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else BrandTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FlowRowCustom(
    items: List<String>,
    color: Color
) {
    // Basic inline wrap simulation using multiple smaller rows or simple layout
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        val chunked = items.chunked(3)
        chunked.forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .background(color.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(item, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
