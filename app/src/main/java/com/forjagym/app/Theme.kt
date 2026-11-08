package com.forjagym.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// FORJA 2.0 — graphite / mineral green, no oversized saturated cards.
val Ink = Color(0xFF10161B)
val SurfaceDark = Color(0xFF192228)
val SurfaceLift = Color(0xFF222D34)
val Lime = Color(0xFF7ED4B0)
val PresenceGreen = Color(0xFF36AF7C)
val Muted = Color(0xFFA2B1B8)
val White = Color(0xFFF0F4F5)
val CalendarPaper = Color(0xFF162027)
val Hairline = Color(0xFF304049)
val Quiet = Color(0xFF72848D)

private val palette = darkColorScheme(
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
    outline = Hairline
)
private val forjaType = Typography(
    headlineLarge = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 34.sp),
    headlineMedium = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 25.sp),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, lineHeight = 21.sp),
    bodyLarge = TextStyle(fontSize = 15.sp, lineHeight = 23.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 17.sp),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
)
@Composable
fun ForjaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = palette, typography = forjaType, content = content)
}
