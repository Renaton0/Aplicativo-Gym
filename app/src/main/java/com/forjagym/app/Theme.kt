package com.forjagym.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Ink = Color(0xFF101411)
val SurfaceDark = Color(0xFF1A211C)
val SurfaceLift = Color(0xFF263128)
val Lime = Color(0xFFB7F76B)
val PresenceGreen = Color(0xFF48C476)
val Muted = Color(0xFFA1ADA3)
val White = Color(0xFFF5F8F4)
val CalendarPaper = Color(0xFFEFF4ED)

private val darkPalette = darkColorScheme(
    primary = Lime,
    onPrimary = Ink,
    secondary = PresenceGreen,
    onSecondary = Ink,
    background = Ink,
    onBackground = White,
    surface = SurfaceDark,
    onSurface = White,
    surfaceVariant = SurfaceLift,
    onSurfaceVariant = Muted,
    outline = Muted
)

@Composable
fun ForjaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = darkPalette, typography = Typography(), content = content)
}
