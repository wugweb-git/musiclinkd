package com.example.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card as Material3Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ui.theme.GestaltElevation
import com.example.ui.theme.MusicLinkdShapes
import com.example.ui.theme.Space

/**
 * Starter Compose card matching web's .card-s / .g-card-hover — flat resting
 * state, lifts to GestaltElevation.raisedTop while pressed (only meaningful
 * when [onClick] is provided). See frontend/guide/merge3.html (.card-s.hl)
 * for the web reference.
 */
@Composable
fun GestaltCard(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  if (onClick != null) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevation = if (isPressed) GestaltElevation.raisedTop else GestaltElevation.flat

    Material3Card(
      onClick = onClick,
      modifier = modifier,
      shape = MusicLinkdShapes.large,
      elevation = CardDefaults.cardElevation(defaultElevation = elevation),
      interactionSource = interactionSource,
    ) {
      Column(modifier = Modifier.padding(Space.md), content = content)
    }
  } else {
    Material3Card(
      modifier = modifier,
      shape = MusicLinkdShapes.large,
      elevation = CardDefaults.cardElevation(defaultElevation = GestaltElevation.flat),
    ) {
      Column(modifier = Modifier.padding(Space.md), content = content)
    }
  }
}
