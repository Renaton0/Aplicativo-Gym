package com.forjagym.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardV2(workouts: List<Workout>, attendance: Set<LocalDate>, prefs: ForjaPrefs,
                db: GymDatabase, revision: Int, onMarkToday: () -> Unit, onRoutines: () -> Unit,
                onCalendar: () -> Unit, onExercise: (String) -> Unit) {
    val today = LocalDate.now()
    val isPresent = today in attendance
    val weekStart = today.minusDays((today.dayOfWeek.value - 1).toLong())
    val weekly = attendance.count { it >= weekStart && it <= today }
    val monthCount = attendance.count { YearMonth.from(it) == YearMonth.from(today) }
    val todayRoutines = workouts.filter { today.dayOfWeek.value in it.days }
    val fmt = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", Locale("pt", "BR"))
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Lime, shape = RoundedCornerShape(7.dp)) {
                    Text("F", modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
                        color = Ink, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(11.dp))
                Column {
                    Text("FORJA GYM", fontWeight = FontWeight.Bold, color = White,
                        style = MaterialTheme.typography.titleLarge)
                    Text("VERSÃO 2.0  /  TREINO & DISCIPLINA", color = Muted,
                        style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(Modifier.height(25.dp))
            ForjaHeader("Seu painel", "Treinar é construir constância.",
                today.format(fmt).replaceFirstChar { it.uppercase() })
        }
        item {
            ForjaPanel(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (isPresent) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                        null, tint = Lime)
                    Spacer(Modifier.width(9.dp))
                    Text("PRESENÇA DE HOJE", color = Lime,
                        style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                Text(if (isPresent) "Presença confirmada" else "Você foi à academia hoje?",
                    color = White, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(11.dp))
                ForjaButton(if (isPresent) "Desmarcar presença" else "Marcar que fui",
                    onMarkToday, Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("Uma marcação basta. Não precisa finalizar uma rotina.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Metric("Semana / meta", "$weekly / ${prefs.weeklyGoal}", Modifier.weight(1f), Lime)
                Metric("Mês / meta", "$monthCount / ${prefs.monthlyGoal}", Modifier.weight(1f))
                Metric("Sequência", "${currentStreak(attendance)}", Modifier.weight(1f))
            }
        }
        item {
            ForjaTitleRow("Treino programado", "${todayRoutines.size} para hoje")
        }
        if (todayRoutines.isEmpty()) item {
            EmptyHint("Nenhuma rotina para hoje", "Defina seus dias de treino na aba Rotinas.")
            Spacer(Modifier.height(9.dp))
            OutlinedButton(onClick = onRoutines, modifier = Modifier.fillMaxWidth()) {
                Text("Abrir minhas rotinas")
            }
        }
        todayRoutines.forEach { workout ->
            item(key = workout.id) {
                val entries = remember(workout.id, revision) { db.workoutExercises(workout.id) }
                ForjaPanel(Modifier.fillMaxWidth()) {
                    ForjaTitleRow(workout.name, "${entries.size} exercícios")
                    Spacer(Modifier.height(9.dp))
                    if (entries.isEmpty()) {
                        Text("Adicione exercícios à rotina para consultá-los aqui.", color = Muted,
                            style = MaterialTheme.typography.bodySmall)
                    }
                    entries.take(4).forEach { entry ->
                        Catalog.get(entry.exerciseId)?.let { e ->
                            TextButton(onClick = { onExercise(e.id) }, modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 4.dp)) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text(e.name, modifier = Modifier.weight(1f), color = White)
                                    Icon(Icons.Rounded.ChevronRight, null, tint = Muted)
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            ForjaPanel(Modifier.fillMaxWidth()) {
                ForjaTitleRow("Registro de disciplina", "Ver calendário")
                Spacer(Modifier.height(9.dp))
                Text("Seus dias treinados, sequências e metas num só lugar.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = onCalendar, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.CalendarMonth, null)
                    Spacer(Modifier.width(7.dp)); Text("Abrir calendário")
                }
            }
        }
    }
}
