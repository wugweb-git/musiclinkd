package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MusicLinkdShapes
import com.example.ui.theme.Space

/**
 * Starter Compose tag matching web's .g-tag — dismissible, distinct from a
 * plain chip (no selection toggle, has a remove affordance). See
 * frontend/guide/merge3.html#g-tag for the web reference. Color scheme comes
 * from MaterialTheme.colorScheme; brand color pass is tracked separately.
 */
@Composable
fun GestaltTag(
  text: String,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
  containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
  contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
  Row(
    modifier = modifier
      .background(containerColor, MusicLinkdShapes.extraSmall)
      .padding(start = Space.sm, top = 2.dp, bottom = 2.dp, end = 2.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(Space.xs),
  ) {
    Text(text, color = contentColor, style = MaterialTheme.typography.labelMedium)
    IconButton(onClick = onDismiss, modifier = Modifier.size(16.dp)) {
      Icon(
        imageVector = Icons.Filled.Close,
        contentDescription = "Remove $text",
        tint = contentColor,
        modifier = Modifier.size(12.dp),
      )
    }
  }
}
