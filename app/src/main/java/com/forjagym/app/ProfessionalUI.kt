package com.forjagym.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ForjaHeader(kicker: String, title: String, detail: String? = null) {
    Text(kicker.uppercase(), fontSize = 10.sp, color = Lime,
        fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
    Spacer(Modifier.height(7.dp))
    Text(title, style = MaterialTheme.typography.headlineLarge, color = White)
    if (detail != null) {
        Spacer(Modifier.height(5.dp))
        Text(detail, style = MaterialTheme.typography.bodyMedium, color = Muted)
    }
}

@Composable
fun ForjaPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = modifier.border(1.dp, Hairline, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp), color = SurfaceDark) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun ForjaButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier,
                enabled: Boolean = true) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled,
        shape = RoundedCornerShape(9.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Ink)) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ForjaTitleRow(title: String, info: String? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, color = White, style = MaterialTheme.typography.titleMedium)
        if (info != null) Text(info, color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun Metric(label: String, value: String, modifier: Modifier = Modifier, accent: Color = White) {
    ForjaPanel(modifier) {
        Text(value, color = accent, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(3.dp))
        Text(label, color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ListAction(title: String, subtitle: String, onClick: () -> Unit,
               modifier: Modifier = Modifier, trailing: String? = null) {
    Surface(modifier = modifier.fillMaxWidth()
        .border(1.dp, Hairline, RoundedCornerShape(11.dp))
        .clickable(onClick = onClick), color = SurfaceDark, shape = RoundedCornerShape(11.dp)) {
        Row(Modifier.padding(horizontal = 15.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = White, fontWeight = FontWeight.SemiBold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            if (trailing != null) {
                Text(trailing, color = Lime, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.width(7.dp))
            }
            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Muted)
        }
    }
}

fun displayKg(v: Double): String = if (v % 1.0 == 0.0) v.toInt().toString() else
    String.format(java.util.Locale("pt", "BR"), "%.1f", v)

@Composable
fun EmptyHint(title: String, detail: String) {
    ForjaPanel(Modifier.fillMaxWidth()) {
        Text(title, color = White, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Text(detail, color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}
