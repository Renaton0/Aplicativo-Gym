package com.forjagym.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.max

private data class ActiveSetState(
    val entry: WorkoutExercise,
    val done: SnapshotStateList<Boolean>
)

@Composable
fun ActiveWorkoutScreenV2(
    workout: Workout,
    planned: List<WorkoutExercise>,
    db: GymDatabase,
    prefs: ForjaPrefs,
    onBack: () -> Unit,
    onOpenExercise: (String) -> Unit,
    onOpenLoads: (String) -> Unit
) {
    val startMs = remember { System.currentTimeMillis() }
    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    var restRemaining by remember { mutableIntStateOf(0) }
    var showingSummary by remember { mutableStateOf(false) }
    val states = remember(planned) {
        planned.map { entry ->
ActiveSetState(entry, androidx.compose.runtime.mutableStateListOf<Boolean>().apply { repeat(entry.sets) { add(false) } })
        }
    }

    LaunchedEffect(startMs, showingSummary) {
        while (!showingSummary) {
            kotlinx.coroutines.delay(1000)
            elapsedSeconds = ((System.currentTimeMillis() - startMs) / 1000L)
        }
    }
    LaunchedEffect(restRemaining) {
        while (restRemaining > 0) {
            kotlinx.coroutines.delay(1000)
            restRemaining = max(0, restRemaining - 1)
        }
    }

    val totalSets = states.sumOf { it.done.size }
    val completedSets = states.sumOf { state -> state.done.count { it } }
    val touchedExercises = states.count { state -> state.done.any { it } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, "Voltar")
                    Spacer(Modifier.size(6.dp))
                    Text("Encerrar")
                }
                Spacer(Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(8.dp), color = SurfaceLift) {
                    Row(Modifier.padding(horizontal = 10.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Timer, null, tint = Lime)
                        Spacer(Modifier.size(6.dp))
                        Text(formatDuration(elapsedSeconds), color = White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        item {
            ForjaPanel(Modifier.fillMaxWidth()) {
                Text(workout.name, color = White, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(6.dp))
                Text("Modo treino ativo", color = Muted, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Metric("Séries", "$completedSets / $totalSets", Modifier.weight(1f), Lime)
                    Metric("Exercícios", "$touchedExercises / ${states.size}", Modifier.weight(1f))
                }
                if (restRemaining > 0) {
                    Spacer(Modifier.height(12.dp))
                    Surface(color = SurfaceLift, shape = RoundedCornerShape(10.dp)) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Timer, null, tint = Lime)
                            Spacer(Modifier.size(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Descanso em andamento", color = White, fontWeight = FontWeight.SemiBold)
                                Text("$restRemaining s restantes", color = Muted, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = { restRemaining = 0 }) { Text("Pular") }
                        }
                    }
                }
            }
        }

        if (states.isEmpty()) {
            item { EmptyHint("Rotina vazia", "Adicione exercícios à rotina antes de iniciar um treino ativo.") }
        }

        items(states, key = { it.entry.id }) { state ->
            val exercise = Catalog.get(state.entry.exerciseId)
            if (exercise != null) {
                val history = remember(exercise.id) { db.loadEntries(exercise.id) }
                val last = history.lastOrNull()
                ForjaPanel(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MachinePhotoV2(exercise, prefs, Modifier.size(72.dp))
                        Spacer(Modifier.size(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(exercise.name, color = White, fontWeight = FontWeight.SemiBold)
                            Text(exercise.equipment, color = Muted, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                            if (last != null) {
                                Spacer(Modifier.height(3.dp))
                                Text("Última carga: ${displayKg(last.weight)} kg × ${last.reps}", color = Lime,
                                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("${state.entry.sets} séries  ·  ${state.entry.reps} reps  ·  ${state.entry.restSeconds}s descanso",
                        color = Muted, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.done.forEachIndexed { index, checked ->
                            val label = "${index + 1}"
                            Surface(
                                modifier = Modifier.clickable {
                                    state.done[index] = !checked
                                    if (!checked) restRemaining = state.entry.restSeconds
                                },
                                shape = CircleShape,
                                color = if (checked) PresenceGreen else SurfaceLift
                            ) {
                                Box(Modifier.size(34.dp), contentAlignment = Alignment.Center) {
                                    if (checked) {
                                        Icon(Icons.Rounded.CheckCircle, null, tint = Ink, modifier = Modifier.size(19.dp))
                                    } else {
                                        Text(label, color = White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { onOpenLoads(exercise.id) }, modifier = Modifier.weight(1f)) {
                            Text("Registrar carga")
                        }
                        OutlinedButton(onClick = { onOpenExercise(exercise.id) }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Rounded.OpenInNew, null)
                            Spacer(Modifier.size(5.dp))
                            Text("Ficha")
                        }
                    }
                }
            }
        }

        item {
            androidx.compose.material3.Button(
                onClick = { showingSummary = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(9.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Ink)
            ) {
                Icon(Icons.Rounded.PlayArrow, null)
                Spacer(Modifier.size(6.dp))
                Text("Finalizar treino", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showingSummary) {
        AlertDialog(
            onDismissRequest = { showingSummary = false },
            title = { Text("Resumo do treino") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(workout.name, color = White, fontWeight = FontWeight.SemiBold)
                    Text("Duração: ${formatDuration(elapsedSeconds)}", color = Muted)
                    Text("Séries concluídas: $completedSets / $totalSets", color = Muted)
                    Text("Exercícios com progresso: $touchedExercises / ${states.size}", color = Muted)
                    Text(if (completedSets == totalSets && totalSets > 0)
                        "Treino completo. Boa!" else "Você pode encerrar agora ou continuar depois.", color = Lime)
                }
            },
            confirmButton = {
                TextButton(onClick = onBack) { Text("Fechar treino") }
            },
            dismissButton = {
                TextButton(onClick = { showingSummary = false }) { Text("Continuar") }
            }
        )
    }
}

private fun formatDuration(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
