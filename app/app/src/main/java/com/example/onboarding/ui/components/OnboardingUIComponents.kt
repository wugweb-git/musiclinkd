package com.example.onboarding.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import kotlin.math.absoluteValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import com.example.onboarding.models.OnboardingScreen
import com.example.ui.theme.*

// 1. Onboarding Header with Progress Indicators
@Composable
fun OnboardingHeader(
    currentScreen: OnboardingScreen,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSkipClick: (() -> Unit)? = null
) {
    val totalSteps = 9
    val activeStep = when (currentScreen) {
        OnboardingScreen.SPLASH -> 0
        OnboardingScreen.WELCOME -> 0
        OnboardingScreen.MOBILE_AUTH -> 1
        OnboardingScreen.OTP_VERIFY -> 1
        OnboardingScreen.EMAIL_AUTH -> 1
        OnboardingScreen.ROLE_PRIMARY -> 2
        OnboardingScreen.ROLE_SECONDARY -> 3
        OnboardingScreen.LANGUAGES -> 4
        OnboardingScreen.GENRES -> 5
        OnboardingScreen.INSTRUMENTS -> 6
        OnboardingScreen.LOCATION -> 7
        OnboardingScreen.PROFILE_CREATION -> 8
        OnboardingScreen.SUCCESS -> 9
        OnboardingScreen.DASHBOARD -> 9
        OnboardingScreen.METADATA_SYNC -> 9
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            if (currentScreen != OnboardingScreen.WELCOME && currentScreen != OnboardingScreen.SUCCESS && currentScreen != OnboardingScreen.DASHBOARD) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("back_button")
                        .border(1.dp, BrandBorder, CircleShape)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = BrandTextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Top MusicLinkd Minimal Logo Text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandPrimary)
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_musiclinkd_logo),
                        contentDescription = "MusicLinkd Official Branded Logo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MusicLinkd",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                        color = BrandTextPrimary
                    )
                )
            }

            // Optional Skip / Fast Clickthrough Badge
            if (onSkipClick != null) {
                TextButton(
                    onClick = onSkipClick,
                    modifier = Modifier.testTag("skip_button")
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = BrandSecondary
                        )
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(44.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress segments
        if (currentScreen != OnboardingScreen.WELCOME && currentScreen != OnboardingScreen.SUCCESS && currentScreen != OnboardingScreen.DASHBOARD) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (step in 1..totalSteps) {
                    val isCompleted = step <= activeStep
                    val isCurrent = step == activeStep
                    
                    val animatedWidthWeight by animateFloatAsState(
                        targetValue = if (isCurrent) 1.8f else 1.0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                        label = "progressWidth"
                    )

                    Box(
                        modifier = Modifier
                            .weight(animatedWidthWeight)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                color = when {
                                    isCompleted -> BrandPrimary
                                    else -> BrandBorder
                                }
                            )
                    )
                }
            }
        }
    }
}

// 2. High-fidelity Native Canvas-based Music Network Art
@Composable
fun MusicNetworkArt(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    // Wave pulse scale animations
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2500
                0.7f at 0
                1.2f at 1200
                0.7f at 2500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )
    
    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3000
                1.0f at 0
                0.6f at 1500
                1.0f at 3000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val baseRadius = size.minDimension * 0.22f

        // 1. Draw glowing background radial gradient fields
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(BrandPrimary.copy(alpha = 0.08f), Color.Transparent),
                center = Offset(centerX, centerY),
                radius = baseRadius * 3.5f
            ),
            radius = baseRadius * 3.5f,
            center = Offset(centerX, centerY)
        )

        // 2. Soundwaves circles wrapping nodes (pulsing)
        drawCircle(
            color = BrandPrimary.copy(alpha = 0.15f),
            radius = baseRadius * pulse1 * 2f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2f)
        )
        drawCircle(
            color = BrandSecondary.copy(alpha = 0.1f),
            radius = baseRadius * pulse2 * 2.8f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.5f)
        )

        // 3. Central "Core Node" - Sleek Vinyl stylized disk
        drawCircle(
            color = BrandPrimary.copy(alpha = 0.1f),
            radius = baseRadius * 1.3f,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = BrandTextPrimary,
            radius = baseRadius * 0.9f,
            center = Offset(centerX, centerY)
        )
        
        // Stylized grooves inside vinyl
        drawCircle(
            color = Color.White.copy(alpha = 0.15f),
            radius = baseRadius * 0.7f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.5f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.15f),
            radius = baseRadius * 0.5f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.5f)
        )
        drawCircle(
            color = BrandPrimary,
            radius = baseRadius * 0.22f,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = Color.White,
            radius = baseRadius * 0.06f,
            center = Offset(centerX, centerY)
        )

        // 4. Connective Orbitals and Outer Network Nodes (Singers, studios, producers)
        val outerNodeAngles = listOf(0f, 60f, 120f, 180f, 240f, 300f)
        val labels = listOf("VOC", "KEY", "PRD", "GTR", "LBL", "RAD")
        
        outerNodeAngles.forEachIndexed { index, angleDegrees ->
            val angleRadians = Math.toRadians((angleDegrees + (pulse1 * 12f)).toDouble())
            val orbitalRadius = baseRadius * 2.1f
            val nodeX = centerX + (orbitalRadius * Math.cos(angleRadians)).toFloat()
            val nodeY = centerY + (orbitalRadius * Math.sin(angleRadians)).toFloat()

            // Draw line connecting outer node to Vinyl Core
            drawLine(
                color = BrandPrimary.copy(alpha = 0.2f),
                start = Offset(centerX, centerY),
                end = Offset(nodeX, nodeY),
                strokeWidth = 2f
            )

            // Draw orbiting nodes
            drawCircle(
                color = Color.White,
                radius = 18.dp.toPx(),
                center = Offset(nodeX, nodeY)
            )
            drawCircle(
                color = if (index % 2 == 0) BrandPrimary else BrandSecondary,
                radius = 18.dp.toPx(),
                center = Offset(nodeX, nodeY),
                style = Stroke(width = 2.dp.toPx())
            )
            
            // Outer node glow pulse
            drawCircle(
                color = (if (index % 2 == 0) BrandPrimary else BrandSecondary).copy(alpha = 0.15f),
                radius = 24.dp.toPx() * (1f + (pulse1 * 0.15f)),
                center = Offset(nodeX, nodeY)
            )
        }
    }
}

// 3. Premium Rounded Text Input Field
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M3InputField(
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
    UnifiedInputField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        label = label,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardType = keyboardType,
        imeAction = imeAction,
        error = error,
        testTag = testTag
    )
}

// 4. Rounded Filter Chip with soft transitions
@Composable
fun MusicFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    UnifiedChip(
        text = text,
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        testTag = testTag
    )
}

// 5. Native QR Code vector artist
@Composable
fun NativeQRCodeCanvas(
    contentUrl: String,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(140.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(6.dp)
    ) {
        val sizePx = size.width
        val blocks = 13 // 13x13 micro matrix
        val blockSize = sizePx / blocks
        
        // Simulating a real aesthetic QR Code pattern
        val filledIndices = setOf(
            // Top-left positional square
            0 to 0, 0 to 1, 0 to 2, 0 to 3, 1 to 0, 1 to 3, 2 to 0, 2 to 3, 3 to 0, 3 to 1, 3 to 2, 3 to 3,
            // Top-right positional square
            0 to 9, 0 to 10, 0 to 11, 0 to 12, 1 to 9, 1 to 12, 2 to 9, 2 to 12, 3 to 9, 3 to 10, 3 to 11, 3 to 12,
            // Bottom-left positional square
            9 to 0, 9 to 1, 9 to 2, 9 to 3, 10 to 0, 10 to 3, 11 to 0, 11 to 3, 12 to 0, 12 to 1, 12 to 2, 12 to 3,
            // Matrix details (random structured aesthetic connectors)
            1 to 6, 2 to 5, 2 to 6, 3 to 6, 4 to 1, 4 to 3, 4 to 5, 4 to 8, 4 to 10,
            5 to 2, 5 to 5, 5 to 7, 5 to 9, 5 to 11,
            6 to 0, 6 to 2, 6 to 3, 6 to 4, 6 to 6, 6 to 8, 6 to 10, 6 to 12,
            7 to 1, 7 to 5, 7 to 7, 7 to 9,
            8 to 3, 8 to 4, 8 to 6, 8 to 8, 8 to 11,
            9 to 6, 9 to 8, 9 to 10,
            10 to 5, 10 to 7, 10 to 9, 10 to 11,
            11 to 5, 11 to 6, 11 to 8, 11 to 12,
            12 to 5, 12 to 7, 12 to 9
        )

        for (r in 0 until blocks) {
            for (c in 0 until blocks) {
                if (filledIndices.contains(r to c)) {
                    // Draw rounded corner dots for modern design look (no hard squares)
                    drawRoundRect(
                        color = BrandPrimary,
                        topLeft = Offset(c * blockSize + 1.5f, r * blockSize + 1.5f),
                        size = Size(blockSize - 3f, blockSize - 3f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }
            }
        }
        
        // Draw decorative center MusicLinkd core dot
        val centerStart = (blocks / 2f) * blockSize - (blockSize / 2f)
        drawRoundRect(
            color = BrandSecondary,
            topLeft = Offset(centerStart, centerStart),
            size = Size(blockSize * 2f, blockSize * 2f),
            cornerRadius = CornerRadius(6f, 6f)
        )
    }
}

// Extension to scale elements on tap
fun Modifier.scale(scale: Float) = this.then(
    Modifier.graphicsLayer(
        scaleX = scale,
        scaleY = scale
    )
)

// Shake animation for error states (150ms)
@Composable
fun Modifier.errorShake(isError: Boolean): Modifier {
    val offset = remember { Animatable(0f) }
    
    LaunchedEffect(isError) {
        if (isError) {
            // Rapid shake pattern
            offset.animateTo(-20f, animationSpec = tween(30, easing = LinearEasing))
            offset.animateTo(20f, animationSpec = tween(30, easing = LinearEasing))
            offset.animateTo(-10f, animationSpec = tween(30, easing = LinearEasing))
            offset.animateTo(10f, animationSpec = tween(30, easing = LinearEasing))
            offset.animateTo(0f, animationSpec = tween(30, easing = LinearEasing))
        }
    }
    
    return this.then(Modifier.offset(x = offset.value.dp))
}

// Shimmer effect for Loading States
fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width,
        targetValue = 2 * size.width,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFECECF1),
                Color(0xFFF3F3F6),
                Color(0xFFECECF1)
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width, size.height)
        )
    ).onGloballyPositioned {
        size = androidx.compose.ui.geometry.Size(it.size.width.toFloat(), it.size.height.toFloat())
    }
}

// Offline Alert Banner
@Composable
fun OfflineAlertBanner(isOffline: Boolean) {
    AnimatedVisibility(
        visible = isOffline,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFDC2626))
                .padding(vertical = 8.dp, horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚠ Connection Lost. Reverting to Cached Offline Tokens and System Profiles.",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Extension to map Card States visually
@Composable
fun Modifier.cardState(state: String): Modifier {
    return when (state) {
        "Selected" -> this
            .border(2.dp, Color(0xFFEA4C89), RoundedCornerShape(24.dp))
            .background(Color(0xFFFFF0F5), RoundedCornerShape(24.dp))
        "Hired" -> this
            .background(Color(0xFFFCE7F3))
            .border(1.dp, Color(0xFFEA4C89), RoundedCornerShape(24.dp))
        "Unavailable" -> this
            .alpha(0.5f)
            .background(Color(0xFFF3F3F6))
        else -> this
    }
}

// Saved Badge Overlay component
@Composable
fun SavedBadgeOverlay() {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .size(32.dp)
            .background(Color(0xFFEA4C89), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Saved",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun BrandIcon(platform: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
    ) {
        when (platform) {
            "Spotify" -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(Color(0xFF1DB954))
                    val w = size.width
                    val h = size.height
                    val strokeWidth = w * 0.07f
                    
                    drawArc(
                        color = Color.White,
                        startAngle = -140f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = Offset(w * 0.14f, h * 0.22f),
                        size = Size(w * 0.72f, h * 0.72f),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = Color.White,
                        startAngle = -140f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = Offset(w * 0.22f, h * 0.34f),
                        size = Size(w * 0.56f, h * 0.56f),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = Color.White,
                        startAngle = -140f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = Offset(w * 0.3f, h * 0.46f),
                        size = Size(w * 0.4f, h * 0.4f),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }
            "YouTube" -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    drawRoundRect(
                        color = Color(0xFFFF0000),
                        cornerRadius = CornerRadius(w * 0.25f, h * 0.25f)
                    )
                    val path = Path().apply {
                        moveTo(w * 0.42f, h * 0.35f)
                        lineTo(w * 0.68f, h * 0.5f)
                        lineTo(w * 0.42f, h * 0.65f)
                        close()
                    }
                    drawPath(path, Color.White)
                }
            }
            "Instagram" -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFF833AB4), Color(0xFFF77737), Color(0xFFFCAF45))
                    )
                    drawRoundRect(
                        brush = gradient,
                        cornerRadius = CornerRadius(w * 0.25f, h * 0.25f)
                    )
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(w * 0.24f, h * 0.24f),
                        size = Size(w * 0.52f, h * 0.52f),
                        cornerRadius = CornerRadius(w * 0.12f, h * 0.12f),
                        style = Stroke(width = w * 0.06f)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.11f,
                        center = center,
                        style = Stroke(width = w * 0.06f)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = w * 0.035f,
                        center = Offset(w * 0.62f, h * 0.38f)
                    )
                }
            }
            "SoundCloud" -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    drawRoundRect(
                        color = Color(0xFFFF5500),
                        cornerRadius = CornerRadius(w * 0.25f, h * 0.25f)
                    )
                    val strokeWidth = w * 0.05f
                    drawLine(Color.White, Offset(w * 0.22f, h * 0.45f), Offset(w * 0.22f, h * 0.55f), strokeWidth, StrokeCap.Round)
                    drawLine(Color.White, Offset(w * 0.28f, h * 0.38f), Offset(w * 0.28f, h * 0.62f), strokeWidth, StrokeCap.Round)
                    drawLine(Color.White, Offset(w * 0.34f, h * 0.32f), Offset(w * 0.34f, h * 0.68f), strokeWidth, StrokeCap.Round)
                    drawLine(Color.White, Offset(w * 0.4f, h * 0.35f), Offset(w * 0.4f, h * 0.65f), strokeWidth, StrokeCap.Round)
                    
                    drawCircle(Color.White, radius = w * 0.15f, center = Offset(w * 0.58f, h * 0.5f))
                    drawCircle(Color.White, radius = w * 0.11f, center = Offset(w * 0.7f, h * 0.54f))
                    drawRect(Color.White, topLeft = Offset(w * 0.46f, h * 0.5f), size = Size(w * 0.24f, h * 0.15f))
                }
            }
            "LinkedIn" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0077B5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "in",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.offset(y = (-1).dp)
                    )
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF6B7280)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = platform.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

// PREMIUM SKELETON LOADER FRAMEWORK
fun Modifier.shimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslation"
    )

    val shimmerColors = listOf(
        Color(0xFFE2E8F0),
        Color(0xFFF1F5F9),
        Color(0xFFE2E8F0)
    )

    this.background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim.value - 300f, translateAnim.value - 300f),
            end = Offset(translateAnim.value, translateAnim.value)
        )
    )
}

@Composable
fun SkeletonItem(
    width: Dp,
    height: Dp,
    cornerRadius: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width, height)
            .clip(RoundedCornerShape(cornerRadius))
            .shimmer()
    )
}

@Composable
fun SkeletonCircle(
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .shimmer()
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchAutocompleteOverlay(
    query: String,
    onSuggestionSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (query.trim().isEmpty()) return

    var suggestions by remember { mutableStateOf<List<com.example.onboarding.database.SearchHistoryEntity>>(emptyList()) }

    LaunchedEffect(query) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val dao = com.example.onboarding.database.RepositoryProvider.searchHistoryDao
                val matches = dao.getAutocompleteSuggestions(query.trim())
                suggestions = matches
            } catch (e: Throwable) {
                com.example.onboarding.util.ErrorReportingService.logWarning("Autocomplete", "Failed fetching search history: ${e.message}")
            }
        }
    }

    if (suggestions.isNotEmpty()) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .semantics {
                    contentDescription = "Offline search autocomplete suggestions list"
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BrandBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Offline Room Autocomplete Suggestions",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    suggestions.forEach { item ->
                        AssistChip(
                            onClick = {
                                onSuggestionSelect(item.query)
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                    try {
                                        com.example.onboarding.database.RepositoryProvider.searchHistoryDao.insertSearchQuery(
                                            com.example.onboarding.database.SearchHistoryEntity(query = item.query, category = item.category)
                                        )
                                    } catch (_: Throwable) {}
                                }
                            },
                            label = { Text("🔍 ${item.query}", fontSize = 11.sp) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.semantics {
                                contentDescription = "Search suggestion for ${item.query} in category ${item.category}"
                            }
                        )
                    }
                }
            }
        }
    }
}

