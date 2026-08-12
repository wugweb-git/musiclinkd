package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)

private val LightColorScheme =
  lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    secondary = BrandSecondary,
    onSecondary = Color.White,
    background = BrandBackground,
    onBackground = BrandTextPrimary,
    surface = BrandCard,
    onSurface = BrandTextPrimary,
    surfaceVariant = BrandBorder,
    onSurfaceVariant = BrandTextSecondary
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Force false as requested
  dynamicColor: Boolean = false, // Force brand-specific aesthetic consistency
  content: @Composable () -> Unit,
) {
  val colorScheme = LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    shapes = MusicLinkdShapes,
    content = content,
  )
}
