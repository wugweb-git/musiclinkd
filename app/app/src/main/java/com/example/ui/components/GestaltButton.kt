package com.example.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Button as Material3Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GestaltElevation
import com.example.ui.theme.MusicLinkdShapes
import com.example.ui.theme.Space

private val MinTouchTarget = 48.dp

/**
 * Starter Compose button matching web's .btn-p (primary) / .btn-s (secondary)
 * — same shape/spacing/elevation tokens (frontend/guide/tokens/tokens.json,
 * MusicLinkdShapes). Color scheme comes from MaterialTheme.colorScheme, left
 * untouched here — see frontend/guide/README.md "Gestalt lineage"; brand
 * color pass is tracked separately.
 */
@Composable
fun GestaltButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  secondary: Boolean = false,
  enabled: Boolean = true,
) {
  val sizedModifier = modifier.defaultMinSize(minHeight = MinTouchTarget)
  val contentPadding = PaddingValues(horizontal = Space.md, vertical = Space.sm)

  if (secondary) {
    OutlinedButton(
      onClick = onClick,
      modifier = sizedModifier,
      enabled = enabled,
      shape = MusicLinkdShapes.extraLarge,
      contentPadding = contentPadding,
    ) {
      Text(text)
    }
  } else {
    Material3Button(
      onClick = onClick,
      modifier = sizedModifier,
      enabled = enabled,
      shape = MusicLinkdShapes.extraLarge,
      contentPadding = contentPadding,
      elevation = ButtonDefaults.buttonElevation(
        defaultElevation = GestaltElevation.flat,
        pressedElevation = GestaltElevation.flat,
      ),
    ) {
      Text(text)
    }
  }
}
