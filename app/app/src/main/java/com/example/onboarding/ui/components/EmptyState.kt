package com.example.onboarding.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    emoji: String? = null,
    icon: ImageVector? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryActionClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BrandBorder.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 48.dp, horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Visual indicator: Emoji or Icon with an elegant editorial container
            if (emoji != null || icon != null) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(Dimens.r3))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    BrandPrimary.copy(alpha = 0.08f),
                                    BrandSecondary.copy(alpha = 0.02f)
                                )
                            )
                        )
                        .border(1.dp, BrandPrimary.copy(alpha = 0.15f), RoundedCornerShape(Dimens.r3)),
                    contentAlignment = Alignment.Center
                ) {
                    if (emoji != null) {
                        Text(
                            text = emoji,
                            fontSize = 36.sp
                        )
                    } else if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "Empty State Icon",
                            tint = BrandPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            // Title with elegant typography scale
            Text(
                text = title,
                style = Typography.bodyLarge,
                color = BrandTextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(Dimens.sp2))

            // Description with massive whitespace and editorial style
            Text(
                text = description,
                style = Typography.bodyLarge,
                color = BrandTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (actionLabel != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(Dimens.sp3))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.sp2)
                ) {
                    Button(
                        onClick = onActionClick,
                        shape = RoundedCornerShape(Dimens.r3),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandTextPrimary,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = actionLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    if (secondaryActionLabel != null && onSecondaryActionClick != null) {
                        TextButton(
                            onClick = onSecondaryActionClick,
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text(
                                text = secondaryActionLabel,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
