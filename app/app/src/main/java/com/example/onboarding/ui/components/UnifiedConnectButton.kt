package com.example.onboarding.ui.components

import androidx.compose.ui.text.style.TextAlign
import com.example.onboarding.database.FirebaseManager
import com.example.onboarding.util.DemoDataProvider
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*

// ============================================================================
// 1. CHIP SYSTEM (Pill-shaped selection indicators)
// ============================================================================

@Composable
fun UnifiedConnectButton(
    targetUserId: String,
    targetUserName: String,
    modifier: Modifier = Modifier,
    initialStatus: String = "IDLE", // "IDLE", "PENDING", "CONNECTED"
    onStatusChange: (String) -> Unit = {},
    testTag: String = "connect_btn"
) {
    var status by remember(initialStatus) { mutableStateOf(initialStatus) }
    var isLoading by remember { mutableStateOf(false) }

    val (btnBg, contentColor, text, icon) = when (status) {
        "CONNECTED" -> Tuple4(BrandSuccess.copy(alpha = 0.12f), BrandSuccess, "Connected", Icons.Default.Check)
        "PENDING" -> Tuple4(BrandPrimary.copy(alpha = 0.12f), BrandPrimary, "Request Sent", Icons.Default.CheckCircle)
        else -> Tuple4(BrandPrimary, Color.White, "Connect", Icons.Default.PersonAdd)
    }

    Button(
        onClick = {
            if (status == "IDLE" && !isLoading) {
                isLoading = true
                FirebaseManager.sendConnectionRequest(
                    targetUserId = targetUserId,
                    targetUserName = targetUserName,
                    message = "Hi $targetUserName, let's collaborate on MusicLinkd!",
                    onSuccess = {
                        isLoading = false
                        status = "PENDING"
                        onStatusChange("PENDING")
                    },
                    onFailure = {
                        isLoading = false
                        // Still update UI gracefully
                        status = "PENDING"
                        onStatusChange("PENDING")
                    }
                )
            }
        },
        enabled = !isLoading && status == "IDLE",
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = btnBg,
            contentColor = contentColor,
            disabledContainerColor = btnBg,
            disabledContentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (status == "IDLE") 2.dp else 0.dp),
        modifier = modifier.testTag(testTag)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Sending...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        } else {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

// ============================================================================
// NETWORKING FEED UI COMPONENT (FIRESTORE DATA)
// ============================================================================
data class FeedPostItem(
    val id: String,
    val authorName: String,
    val authorRole: String,
    val authorAvatar: String?,
    val isVerified: Boolean,
    val content: String,
    val postType: String, // "RELEASE", "COLLAB_CALL", "STUDIO_UPDATE", "UPDATE"
    val timestampText: String,
    val likesCount: Int,
    val audioTitle: String? = null,
    val audioDuration: String? = null
)
