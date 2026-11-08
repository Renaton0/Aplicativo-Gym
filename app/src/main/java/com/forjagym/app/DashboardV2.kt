package com.forjagym.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardV2(
    workouts: List<Workout>,
    attendance: Set<LocalDate>,
    prefs: ForjaPrefs,
    db: GymDatabase,
    revision: Int,
    onMarkToday: () -> Unit,
    onRoutines: () -> Unit,
    onCalendar: () -> Unit,
    onExercise: (String) -> Unit,
    onStartWorkout: (Long) -> Unit
) {
    val today = LocalDate.now()
    val isPresent = today in attendance
    val weekStart = today.minusDays((today.dayOfWeek.value - 1).toLong())
    val weekly = attendance.count { it >= weekStart && it <= today }
    val monthCount = attendance.count { YearMonth.from(it) == YearMonth.from(today) }
    val todayRoutines = workouts.filter { today.dayOfWeek.value in it.days }
    val fmt = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", Locale("pt", "BR"))

    LazyColumn(
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(color = SurfaceDark, shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(today.format(fmt).replaceFirstChar { it.uppercase() }, color = White,
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(5.dp))
                    Text("Resumo do seu treino, constância e próximas ações.", color = Muted,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                }
            }
        }
        item {
            ForjaPanel(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (isPresent) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                        null, tint = Lime)
                    Spacer(Modifier.width(8.dp))
                    Text("Presença de hoje", color = White, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(10.dp))
                Text(if (isPresent) "Dia registrado." else "Marque sua ida sem precisar finalizar um treino.",
                    color = Muted)
                Spacer(Modifier.height(10.dp))
                ForjaButton(if (isPresent) "Desmarcar presença" else "Marcar que fui",
                    onMarkToday, Modifier.fillMaxWidth())
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Metric("Semana / meta", "$weekly / ${prefs.weeklyGoal}", Modifier.weight(1f), Lime)
                Metric("Mês / meta", "$monthCount / ${prefs.monthlyGoal}", Modifier.weight(1f))
                Metric("Sequência", "${currentStreak(attendance)} dias", Modifier.weight(1f))
            }
        }

        item {
            ForjaTitleRow("Treinos de hoje", if (todayRoutines.isEmpty()) "Nenhum" else "${todayRoutines.size} disponível(is)")
        }
        if (todayRoutines.isEmpty()) item {
            EmptyHint("Nada programado para hoje", "Defina dias da semana nas suas rotinas.")
            OutlinedButton(onClick = onRoutines, modifier = Modifier.fillMaxWidth()) {
                Text("Abrir rotinas")
            }
        }
        todayRoutines.forEach { workout ->
            item(key = workout.id) {
                val entries = remember(workout.id, revision) { db.workoutExercises(workout.id) }
                ForjaPanel(Modifier.fillMaxWidth()) {
                    ForjaTitleRow(workout.name, "${entries.size} exercícios")
                    Spacer(Modifier.height(8.dp))
                    entries.take(3).forEach { entry ->
                        Catalog.get(entry.exerciseId)?.let { exercise ->
                            TextButton(onClick = { onExercise(exercise.id) }, modifier = Modifier.fillMaxWidth()) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text(exercise.name, modifier = Modifier.weight(1f), color = White)
                                    Text("${entry.sets}×${entry.reps}", color = Muted)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ForjaButton("Iniciar treino", { onStartWorkout(workout.id) }, Modifier.weight(1f))
                        OutlinedButton(onClick = { onRoutines() }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Rounded.FitnessCenter, null)
                            Spacer(Modifier.width(5.dp))
                            Text("Editar")
                        }
                    }
                }
            }
        }
        item {
            ForjaPanel(Modifier.fillMaxWidth()) {
                ForjaTitleRow("Disciplina", "Calendário anual e metas")
                Spacer(Modifier.height(8.dp))
                Text("Acompanhe presença, metas, sequência atual e visão anual.", color = Muted,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = onCalendar, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.CalendarMonth, null)
                    Spacer(Modifier.width(7.dp))
                    Text("Abrir disciplina")
                }
            }
        }
    }
}
