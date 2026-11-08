package com.forjagym.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private val weekNames = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")

@Composable
fun RoutinesTabV2(
    workouts: List<Workout>,
    db: GymDatabase,
    prefs: ForjaPrefs,
    onChange: () -> Unit,
    onOpen: (Long) -> Unit
) {
    var create by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<Workout?>(null) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item {
            ForjaButton("+ Nova rotina", { create = true }, Modifier.fillMaxWidth())
        }
        if (workouts.isEmpty()) item {
            EmptyHint("Nenhuma rotina criada", "Crie seu Treino A, B, C ou sua própria divisão.")
        }
        items(workouts, key = { it.id }) { workout ->
            ForjaPanel(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(workout.name, style = androidx.compose.material3.MaterialTheme.typography.titleLarge, color = White)
                        Spacer(Modifier.height(4.dp))
                        Text("${db.workoutExercises(workout.id).size} exercícios • " +
                            if (workout.days.isEmpty()) "sem dias definidos"
                            else workout.days.sorted().joinToString(" · ") { weekNames[it - 1] },
                            color = Muted, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { toDelete = workout }) {
                        Icon(Icons.Rounded.DeleteOutline, "Excluir rotina", tint = Muted)
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { onOpen(workout.id) }, modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)) { Text("Editar rotina") }
            }
        }
    }
    if (create) RoutineNameDialogV2("Criar rotina", "Treino A — Peito", onDismiss = { create = false }) {
        val id = db.addWorkout(it)
        onChange(); create = false; onOpen(id)
    }
    toDelete?.let { workout ->
        AlertDialog(onDismissRequest = { toDelete = null }, title = { Text("Excluir rotina?") },
            text = { Text("A rotina e sua lista de exercícios serão apagadas. O histórico de cargas continuará salvo.") },
            confirmButton = { TextButton(onClick = {
                db.removeWorkout(workout.id); onChange(); toDelete = null
            }) { Text("Excluir") } },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Cancelar") } })
    }
}

@Composable
private fun RoutineNameDialogV2(title: String, placeholder: String, initial: String = "",
                                 onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var input by remember(initial) { mutableStateOf(initial) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(title) }, text = {
        OutlinedTextField(value = input, onValueChange = { input = it },
            placeholder = { Text(placeholder) }, label = { Text("Nome") }, singleLine = true)
    }, confirmButton = { TextButton(onClick = { onSave(input.trim()) },
        enabled = input.isNotBlank()) { Text("Salvar") } },
       dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}

@Composable
fun RoutineEditorV2(
    workout: Workout,
    db: GymDatabase,
    prefs: ForjaPrefs,
    revision: Int,
    onChange: () -> Unit,
    onBack: () -> Unit,
    onExercise: (String) -> Unit
) {
    var rename by remember { mutableStateOf(false) }
    var add by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<WorkoutExercise?>(null) }
    var deleting by remember { mutableStateOf<WorkoutExercise?>(null) }
    val planned = remember(workout.id, revision) { db.workoutExercises(workout.id) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            TextButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, "Voltar")
                Spacer(Modifier.size(6.dp))
                Text("Minhas rotinas")
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(workout.name, color = White, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                    Text("Dias e exercícios planejados", color = Muted, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = { rename = true }) {
                    Icon(Icons.Rounded.Edit, "Renomear", tint = Lime)
                }
            }
        }
        item {
            ForjaPanel(Modifier.fillMaxWidth()) {
                ForjaTitleRow("Dias da semana")
                Spacer(Modifier.height(7.dp))
                Text("Escolha os dias em que esta rotina deve aparecer na tela inicial.",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall, color = Muted)
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    items((1..7).toList()) { day ->
                        FilterChip(selected = day in workout.days, onClick = {
                            db.updateDays(workout.id, if (day in workout.days) workout.days - day else workout.days + day)
                            onChange()
                        }, label = { Text(weekNames[day - 1]) }, shape = RoundedCornerShape(7.dp))
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Exercícios • ${planned.size}", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = White)
                TextButton(onClick = { add = true }) {
                    Icon(Icons.Rounded.Add, null, Modifier.size(18.dp))
                    Text("Adicionar")
                }
            }
        }
        if (planned.isEmpty()) item {
            EmptyHint("Lista vazia", "Adicione exercícios a partir do catálogo.")
        }
        items(planned, key = { it.id }) { entry ->
            val exercise = Catalog.get(entry.exerciseId)
            if (exercise != null) ForjaPanel(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MachinePhotoV2(exercise, prefs, Modifier.size(72.dp))
                    Spacer(Modifier.size(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text(exercise.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = White)
                        Text(exercise.equipment, style = androidx.compose.material3.MaterialTheme.typography.bodySmall, color = Muted)
                        Spacer(Modifier.height(7.dp))
                        Text("${entry.sets} séries  ·  ${entry.reps} reps  ·  ${entry.restSeconds}s intervalo",
                            color = Lime, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(onClick = { editing = entry }, modifier = Modifier.weight(1f)) {
                        Text("Editar")
                    }
                    OutlinedButton(onClick = { onExercise(exercise.id) }, modifier = Modifier.weight(1f)) {
                        Text("Ficha")
                    }
                    IconButton(onClick = { deleting = entry }) {
                        Icon(Icons.Rounded.DeleteOutline, "Remover exercício", tint = Muted)
                    }
                }
            }
        }
    }
    if (rename) RoutineNameDialogV2("Renomear rotina", "Nome", workout.name,
        onDismiss = { rename = false }) {
        db.renameWorkout(workout.id, it); rename = false; onChange()
    }
    if (add) ExerciseSelectorV2(prefs, onDismiss = { add = false }) {
        db.addExercise(workout.id, it.id); onChange(); add = false
    }
    editing?.let { e ->
        EditPlannedExerciseV2(e, onDismiss = { editing = null }) { sets, reps, rest ->
            db.updateExercise(e.id, sets, reps, rest); onChange(); editing = null
        }
    }
    deleting?.let { e ->
        AlertDialog(onDismissRequest = { deleting = null }, title = { Text("Remover exercício?") },
            confirmButton = { TextButton(onClick = {
                db.removeExercise(e.id); onChange(); deleting = null
            }) { Text("Remover") } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancelar") } })
    }
}

@Composable
private fun EditPlannedExerciseV2(entry: WorkoutExercise, onDismiss: () -> Unit,
                                  onSave: (Int, Int, Int) -> Unit) {
    var sets by remember { mutableStateOf(entry.sets.toString()) }
    var reps by remember { mutableStateOf(entry.reps.toString()) }
    var rest by remember { mutableStateOf(entry.restSeconds.toString()) }
    val a = sets.toIntOrNull(); val b = reps.toIntOrNull(); val c = rest.toIntOrNull()
    val valid = a != null && a in 1..100 && b != null && b in 1..1000 && c != null && c in 0..3600
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Editar exercício") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(sets, { sets = it }, label = { Text("Séries") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(reps, { reps = it }, label = { Text("Repetições") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(rest, { rest = it }, label = { Text("Intervalo (segundos)") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
    }, confirmButton = { TextButton(onClick = { onSave(a!!, b!!, c!!) }, enabled = valid) { Text("Salvar") } },
       dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}

@Composable
fun ExerciseSelectorV2(
    prefs: ForjaPrefs,
    onDismiss: () -> Unit,
    onChoose: (Exercise) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) { Catalog.all.filter {
        query.isBlank() || it.name.contains(query, true) || it.equipment.contains(query, true)
    } }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Selecionar exercício") }, text = {
            Column {
                OutlinedTextField(query, onValueChange = { query = it },
                    placeholder = { Text("Buscar") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.heightIn(max = 360.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtered, key = { it.id }) { exercise ->
                        ForjaPanel(Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MachinePhotoV2(exercise, prefs, Modifier.size(58.dp))
                                Spacer(Modifier.size(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(exercise.name, color = White)
                                    Text(exercise.group + " • " + exercise.equipment, color = Muted,
                                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                                }
                                TextButton(onClick = { onChoose(exercise) }) { Text("Adicionar") }
                            }
                        }
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = onDismiss) { Text("Fechar") } })
}
