package com.forjagym.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val pt = Locale("pt", "BR")
private val calendarBlack = Color(0xFF1E2C33)
private val calendarBg = Color(0xFFE6EEEB)
private val inactiveDot = Color(0xFF1C292D)

fun currentStreak(attendance: Set<LocalDate>, today: LocalDate = LocalDate.now()): Int {
    var cursor = if (today in attendance) today else today.minusDays(1)
    var count = 0
    while (cursor in attendance) {
        count++
        cursor = cursor.minusDays(1)
    }
    return count
}

@Composable
fun DisciplineTabV2(attendance: Set<LocalDate>, prefs: ForjaPrefs,
                    onToggle: (LocalDate) -> Unit, onPrefsChange: () -> Unit) {
    val today = LocalDate.now()
    var selectedMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var year by remember { mutableIntStateOf(today.year) }
    var annual by remember { mutableStateOf(false) }
    var goalsOpen by remember { mutableStateOf(false) }
    val weekStart = today.minusDays((today.dayOfWeek.value - 1).toLong())
    val weekCount = attendance.count { it >= weekStart && it <= today }
    val monthCount = attendance.count { YearMonth.from(it) == YearMonth.from(today) }
    val daysYear = attendance.count { it.year == year }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)) {
        ForjaHeader("Compromisso", "Sua disciplina", "Cada presença conta. Sem precisar registrar um treino.")
        ForjaPanel(Modifier.fillMaxWidth()) {
            ForjaTitleRow("Meta semanal", "$weekCount / ${prefs.weeklyGoal} dias")
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { (weekCount.toFloat() / prefs.weeklyGoal).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = PresenceGreen, trackColor = Hairline
            )
            Spacer(Modifier.height(9.dp))
            ForjaTitleRow("Meta mensal", "$monthCount / ${prefs.monthlyGoal} dias")
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { (monthCount.toFloat() / prefs.monthlyGoal).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = PresenceGreen, trackColor = Hairline
            )
            Spacer(Modifier.height(7.dp))
            TextButton(onClick = { goalsOpen = true }) {
                Icon(Icons.Rounded.Tune, null, Modifier.size(17.dp))
                Spacer(Modifier.width(6.dp)); Text("Editar minhas metas")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Metric("Sequência atual", "${currentStreak(attendance)} dias", Modifier.weight(1f), Lime)
            Metric("No ano de $year", "$daysYear dias", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !annual, onClick = { annual = false },
                label = { Text("Calendário mensal") }, shape = RoundedCornerShape(8.dp))
            FilterChip(selected = annual, onClick = { annual = true },
                label = { Text("Visão anual") }, shape = RoundedCornerShape(8.dp))
        }
        if (annual) {
            Surface(color = calendarBg, shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        IconButton(onClick = { year-- }) {
                            Icon(Icons.Rounded.ChevronLeft, "Ano anterior", tint = calendarBlack)
                        }
                        Text("$year • $daysYear dias marcados", color = calendarBlack,
                            fontWeight = FontWeight.Bold)
                        IconButton(onClick = { year++ }, enabled = year < today.year) {
                            Icon(Icons.Rounded.ChevronRight, "Próximo ano",
                                tint = if (year < today.year) calendarBlack else Quiet)
                        }
                    }
                    Spacer(Modifier.height(9.dp))
                    repeat(4) { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            repeat(3) { col ->
                                val month = YearMonth.of(year, row * 3 + col + 1)
                                SmallYearMonth(month, attendance, modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(Modifier.height(13.dp))
                    }
                    CalendarLegend()
                    Spacer(Modifier.height(6.dp))
                    Text("Visão anual somente para acompanhamento; marque datas no calendário mensal.",
                        color = calendarBlack, style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            Surface(color = calendarBg, shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(horizontal = 14.dp, vertical = 16.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        IconButton(onClick = { selectedMonth = selectedMonth.minusMonths(1) }) {
                            Icon(Icons.Rounded.ChevronLeft, "Mês anterior", tint = calendarBlack)
                        }
                        Text(selectedMonth.atDay(1).format(
                            DateTimeFormatter.ofPattern("MMMM yyyy", pt)).replaceFirstChar { it.uppercase() },
                            color = calendarBlack, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { selectedMonth = selectedMonth.plusMonths(1) },
                            enabled = selectedMonth < YearMonth.from(today)) {
                            Icon(Icons.Rounded.ChevronRight, "Próximo mês", tint = calendarBlack)
                        }
                    }
                    Row(Modifier.fillMaxWidth()) {
                        listOf("S", "T", "Q", "Q", "S", "S", "D").forEach { day ->
                            Text(day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                                color = Quiet, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    val offset = selectedMonth.atDay(1).dayOfWeek.value - 1
                    val rows = (offset + selectedMonth.lengthOfMonth() + 6) / 7
                    repeat(rows) { row ->
                        Row(Modifier.fillMaxWidth()) {
                            repeat(7) { col ->
                                val day = row * 7 + col - offset + 1
                                val date = if (day in 1..selectedMonth.lengthOfMonth())
                                    selectedMonth.atDay(day) else null
                                val marked = date != null && date in attendance
                                val active = date != null && !date.isAfter(today)
                                Column(Modifier.weight(1f)
                                    .clickable(enabled = active) { if (date != null) onToggle(date) }
                                    .padding(vertical = 9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(if (date != null) "$day" else " ",
                                        color = if (date == today) PresenceGreen else calendarBlack,
                                        fontWeight = if (date == today) FontWeight.Bold else FontWeight.Normal,
                                        style = MaterialTheme.typography.bodySmall)
                                    Spacer(Modifier.height(7.dp))
                                    Box(Modifier.size(17.dp).background(
                                        if (date == null) Color.Transparent
                                        else if (marked) PresenceGreen else inactiveDot, CircleShape))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    CalendarLegend()
                }
            }
            Text("Toque num dia de hoje ou do passado para marcar ou desmarcar sua presença.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))
    }
    if (goalsOpen) GoalsDialog(prefs, onDismiss = { goalsOpen = false }, onSave = { week, month ->
        prefs.weeklyGoal = week; prefs.monthlyGoal = month; onPrefsChange(); goalsOpen = false
    })
}

@Composable
private fun CalendarLegend() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        Box(Modifier.size(11.dp).background(inactiveDot, CircleShape))
        Text("Não marcado", color = calendarBlack, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.width(8.dp))
        Box(Modifier.size(11.dp).background(PresenceGreen, CircleShape))
        Text("Fui treinar", color = calendarBlack, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun SmallYearMonth(month: YearMonth, attendance: Set<LocalDate>, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(month.atDay(1).format(DateTimeFormatter.ofPattern("MMM", pt)).uppercase(pt),
            color = calendarBlack, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        val offset = month.atDay(1).dayOfWeek.value - 1
        repeat(6) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(7) { col ->
                    val n = row * 7 + col - offset + 1
                    val date = if (n in 1..month.lengthOfMonth()) month.atDay(n) else null
                    Box(Modifier.size(9.dp).padding(1.dp).background(
                        if (date == null) Color.Transparent else if (date in attendance) PresenceGreen
                        else inactiveDot, CircleShape))
                }
            }
        }
    }
}

@Composable
private fun GoalsDialog(prefs: ForjaPrefs, onDismiss: () -> Unit, onSave: (Int, Int) -> Unit) {
    var week by remember { mutableIntStateOf(prefs.weeklyGoal) }
    var month by remember { mutableIntStateOf(prefs.monthlyGoal) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Metas pessoais") }, text = {
        Column {
            Text("Dias por semana", color = White)
            Spacer(Modifier.height(6.dp))
            GoalStepper(week, 1, 7, onChange = { week = it })
            Spacer(Modifier.height(14.dp))
            Text("Dias por mês", color = White)
            Spacer(Modifier.height(6.dp))
            GoalStepper(month, 1, 31, onChange = { month = it })
        }
    }, confirmButton = { TextButton(onClick = { onSave(week, month) }) { Text("Salvar") } },
       dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}

@Composable
private fun GoalStepper(value: Int, min: Int, max: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onChange((value - 1).coerceAtLeast(min)) }) {
            Icon(Icons.Rounded.RemoveCircleOutline, "Diminuir meta")
        }
        Text("$value", modifier = Modifier.width(48.dp), textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge)
        IconButton(onClick = { onChange((value + 1).coerceAtMost(max)) }) {
            Icon(Icons.Rounded.AddCircleOutline, "Aumentar meta")
        }
    }
}
