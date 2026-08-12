package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Space

/** Semantic tone for [GestaltStatus] — maps to icons/workflow-status-*.svg on web. */
enum class StatusTone { Ok, InProgress, Warning, Problem, Unstarted }

/**
 * Starter Compose status indicator (dot + label) matching web's .g-status.
 * See frontend/guide/merge3.html#g-status for the web reference. Tone colors
 * below are functional placeholders (success/warning/error semantics), not a
 * final brand pass — that's tracked separately.
 */
@Composable
fun GestaltStatus(
  label: String,
  tone: StatusTone,
  modifier: Modifier = Modifier,
) {
  val dotColor = when (tone) {
    StatusTone.Ok -> Color(0xFF22C55E)
    StatusTone.InProgress -> MaterialTheme.colorScheme.primary
    StatusTone.Warning -> Color(0xFFF59E0B)
    StatusTone.Problem -> Color(0xFFDC2626)
    StatusTone.Unstarted -> MaterialTheme.colorScheme.onSurfaceVariant
  }
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(Space.xs),
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .background(dotColor, CircleShape),
    )
    Text(label, color = dotColor, style = MaterialTheme.typography.labelMedium)
  }
}
