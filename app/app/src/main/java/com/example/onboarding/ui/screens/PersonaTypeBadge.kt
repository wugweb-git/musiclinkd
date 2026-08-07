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
fun PersonaTypeBadge(type: ProfileType, modifier: Modifier = Modifier) {
    val bgColor = when (type) {
        ProfileType.TALENT -> Color(0xFFEA4C89).copy(alpha = 0.08f)
        ProfileType.BAND -> Color(0xFFD93C77).copy(alpha = 0.08f)
        ProfileType.BUSINESS -> Color(0xFF0D0B26).copy(alpha = 0.08f)
        ProfileType.VENUE_EVENT -> Color(0xFFF59E0B).copy(alpha = 0.08f)
    }
    val textColor = when (type) {
        ProfileType.TALENT -> Color(0xFFEA4C89)
        ProfileType.BAND -> Color(0xFFD93C77)
        ProfileType.BUSINESS -> Color(0xFF0D0B26)
        ProfileType.VENUE_EVENT -> Color(0xFFD97706)
    }
    val label = when (type) {
        ProfileType.TALENT -> "Solo Artist Persona"
        ProfileType.BAND -> "Band / Group Persona"
        ProfileType.BUSINESS -> "Studio / Business Persona"
        ProfileType.VENUE_EVENT -> "Venue / Event Persona"
    }
    val vectorIcon = when (type) {
        ProfileType.TALENT -> Icons.Default.Person
        ProfileType.BAND -> Icons.Default.Group
        ProfileType.BUSINESS -> Icons.Default.Business
        ProfileType.VENUE_EVENT -> Icons.Default.Place
    }
    
    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(Dimens.rFull))
            .border(1.dp, textColor.copy(alpha = 0.2f), RoundedCornerShape(Dimens.rFull))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(vectorIcon, contentDescription = null, tint = textColor, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(Dimens.sp1))
        Text(label, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

// =========================================================================
// SECTION 25: WHATSAPP LANDING PAGES & MICRO CHECKMARK SUCCESS ANIMATION
// =========================================================================
