@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.onboarding.ui.screens

import com.example.onboarding.ai.GeminiRecommendationEngine
import com.example.onboarding.ai.TrendingMusicProject
import com.example.onboarding.util.DynamicLinkManager
import com.example.onboarding.util.ErrorReportingService
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onboarding.models.*
import com.example.onboarding.viewmodel.*
import com.example.onboarding.ui.components.*

// Cohesive Branding Colors aligned to high-fashion neutral and active creator pink

@Composable
fun DiscoverFlowRow(
    horizontalGap: androidx.compose.ui.unit.Dp = 4.dp,
    verticalGap: androidx.compose.ui.unit.Dp = 4.dp,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(horizontalGap),
        content = { content() }
    )
}

fun getInstrumentEmoji(subRole: String, category: String): String {
    val r = subRole.lowercase()
    val c = category.lowercase()
    return when {
        r.contains("guitar") || r.contains("acoustic") -> "🎸"
        r.contains("drum") || r.contains("percussion") -> "🥁"
        r.contains("singer") || r.contains("vocalist") || r.contains("vocal") || c.contains("singers") -> "🎤"
        r.contains("composer") || r.contains("arranger") -> "🎼"
        r.contains("keyboard") || r.contains("piano") || r.contains("synth") -> "🎹"
        r.contains("dj") || r.contains("remix") || r.contains("electronic") -> "🎧"
        r.contains("mix") || r.contains("master") || r.contains("engineer") || c.contains("engineers") -> "🎛️"
        r.contains("flute") -> "🪈"
        r.contains("violin") -> "🎻"
        r.contains("trumpet") || r.contains("brass") || r.contains("sax") -> "🎷"
        else -> "🎵"
    }
}
