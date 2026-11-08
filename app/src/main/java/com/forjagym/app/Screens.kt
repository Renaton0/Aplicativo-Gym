package com.forjagym.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.max

private val ptBr = Locale("pt", "BR")
private val fullDate = DateTimeFormatter.ofPattern("dd 'de' MMMM", ptBr)
private val weekdays = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")

private fun fmtKg(value: Double): String = if (value % 1.0 == 0.0) "${value.toInt()}" else
    String.format(ptBr, "%.1f", value)

@Composable
private fun PageHeading(eyebrow: String, title: String, subtitle: String? = null) {
    Text(eyebrow.uppercase(ptBr), color = Lime, style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
    Spacer(Modifier.height(8.dp))
    Text(title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black,
        color = White)
    if (subtitle != null) {
        Spacer(Modifier.height(7.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Muted)
    }
}

@Composable
private fun Panel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = modifier, color = SurfaceDark, shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.padding(18.dp), content = content)
    }
}

@Composable
private fun SolidButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier,
                        enabled: Boolean = true) {
    Button(
        onClick = onClick, modifier = modifier, enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Ink)
    ) { Text(text, fontWeight = FontWeight.Bold) }
}

@Composable
private fun EmptyCard(title: String, description: String, onClick: (() -> Unit)? = null,
                      action: String = "Começar") {
    Panel(Modifier.fillMaxWidth()) {
        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = Lime,
            modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(10.dp))
        Text(title, fontWeight = FontWeight.Bold, color = White)
        Spacer(Modifier.height(4.dp))
        Text(description, color = Muted, style = MaterialTheme.typography.bodyMedium)
        if (onClick != null) {
            Spacer(Modifier.height(12.dp))
            SolidButton(action, onClick)
        }
    }
}

@Composable
private fun SectionHeading(title: String, trailing: String? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, color = White, style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold)
        if (trailing != null) Text(trailing, color = Muted,
            style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun HomeScreen(workouts: List<Workout>, attendance: Set<LocalDate>,
               onPresence: () -> Unit, onOpenRoutines: () -> Unit,
               onOpenCalendar: () -> Unit, onOpenLoads: () -> Unit, db: GymDatabase) {
    val today = LocalDate.now()
    val todayWorkouts = workouts.filter { today.dayOfWeek.value in it.days }
    val thisWeek = today.minusDays((today.dayOfWeek.value - 1).toLong())
    val weekCount = attendance.count { it >= thisWeek && it <= today }
    val attended = today in attendance
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 26.dp, end = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            PageHeading("FORJA / seu espaço", "A constância constrói.",
                "${today.format(fullDate).replaceFirstChar { it.uppercase() }} • seu treino, no seu ritmo")
        }
        item {
            Surface(color = Lime, shape = RoundedCornerShape(26.dp)) {
                Column(Modifier.fillMaxWidth().padding(22.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Whatshot, null, tint = Ink)
                        Spacer(Modifier.width(8.dp))
                        Text("PRESENÇA DE HOJE", color = Ink,
                            style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(if (attended) "Você apareceu. ✓" else "Mais um dia para evoluir.",
                        color = Ink, style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(15.dp))
                    Button(
                        onClick = onPresence,
                        colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = White),
                        shape = RoundedCornerShape(13.dp)
                    ) {
                        Icon(if (attended) Icons.Rounded.Undo else Icons.Rounded.CheckCircle,
                            null, Modifier.size(19.dp))
                        Spacer(Modifier.width(7.dp))
                        Text(if (attended) "Desmarcar presença" else "Marcar que fui", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Panel(Modifier.weight(1f)) {
                    Text("$weekCount / 7", color = Lime, fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.headlineMedium)
                    Text("dias nesta semana", color = Muted, style = MaterialTheme.typography.bodySmall)
                }
                Panel(Modifier.weight(1f)) {
                    Text("${workouts.size}", color = Lime, fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.headlineMedium)
                    Text("rotinas montadas", color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        item { SectionHeading("Treino programado para hoje") }
        if (todayWorkouts.isEmpty()) {
            item {
                EmptyCard("Hoje sem rotina agendada",
                    "Monte seus treinos e escolha os dias da semana para aparecerem aqui.",
                    onOpenRoutines, "Montar rotina")
            }
        } else {
            items(todayWorkouts, key = { it.id }) { workout ->
                Panel(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.FitnessCenter, null, tint = Lime)
                        Spacer(Modifier.width(9.dp))
                        Text(workout.name, color = White, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(7.dp))
                    val planned = remember(workout.id) { db.workoutExercises(workout.id) }
                    Text("${planned.size} exercícios • ${weekdays[today.dayOfWeek.value - 1]}", color = Muted)
                    if (planned.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(planned.take(3).mapNotNull { Catalog.get(it.exerciseId)?.name }
                            .joinToString("  •  "), color = Muted,
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        item {
            SectionHeading("Acesso rápido")
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onOpenCalendar, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Rounded.CalendarMonth, null, Modifier.size(17.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("Calendário")
                }
                OutlinedButton(onClick = onOpenLoads, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Rounded.TrendingUp, null, Modifier.size(17.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("Cargas")
                }
            }
        }
    }
}

@Composable
fun WorkoutsScreen(workouts: List<Workout>, onCreate: (String) -> Unit,
                   onOpen: (Long) -> Unit, onDelete: (Long) -> Unit, db: GymDatabase) {
    var creating by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<Workout?>(null) }
    LazyColumn(Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 26.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { PageHeading("Organização", "Minhas rotinas", "Divida seus exercícios por dia ou tipo de treino.") }
        item {
            SolidButton("+ Criar rotina", onClick = { creating = true },
                modifier = Modifier.fillMaxWidth())
        }
        if (workouts.isEmpty()) {
            item { EmptyCard("Sua primeira rotina começa aqui",
                "Crie um treino A, B, C ou como preferir. Depois escolha os dias e os aparelhos.") }
        }
        items(workouts, key = { it.id }) { workout ->
            val exercises = remember(workout.id) { db.workoutExercises(workout.id) }
            Panel(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(workout.name, color = White, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(5.dp))
                        Text("${exercises.size} exercícios", color = Muted)
                    }
                    IconButton(onClick = { deleting = workout }) {
                        Icon(Icons.Rounded.DeleteOutline, "Excluir rotina", tint = Muted)
                    }
                }
                Spacer(Modifier.height(9.dp))
                Text(if (workout.days.isEmpty()) "Sem dias selecionados"
                     else workout.days.sorted().joinToString(" • ") { weekdays[it - 1] },
                    color = Lime, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(11.dp))
                OutlinedButton(onClick = { onOpen(workout.id) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Editar exercícios e dias")
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Rounded.ArrowForward, null, Modifier.size(16.dp))
                }
            }
        }
    }
    if (creating) {
        NameDialog("Nova rotina", "Ex.: Treino A — Peito e tríceps",
            onDismiss = { creating = false }, onSave = {
                onCreate(it)
                creating = false
            })
    }
    deleting?.let { workout ->
        AlertDialog(onDismissRequest = { deleting = null }, title = { Text("Excluir rotina?") },
            text = { Text("A rotina '${workout.name}' e os exercícios dela serão apagados. O histórico de cargas será mantido.") },
            confirmButton = { TextButton(onClick = { onDelete(workout.id); deleting = null }) { Text("Excluir") } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancelar") } })
    }
}

@Composable
private fun NameDialog(title: String, hint: String, initial: String = "",
                       onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember(initial) { mutableStateOf(initial) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(title) },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") },
                placeholder = { Text(hint) }, singleLine = true)
        },
        confirmButton = { TextButton(onClick = { onSave(name.trim()) }, enabled = name.isNotBlank()) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}

@Composable
fun WorkoutEditorScreen(workout: Workout, exercises: List<WorkoutExercise>,
                        onBack: () -> Unit, onChange: () -> Unit, db: GymDatabase) {
    var rename by remember { mutableStateOf(false) }
    var picker by remember { mutableStateOf(false) }
    var editedExercise by remember { mutableStateOf<WorkoutExercise?>(null) }
    var deleteExercise by remember { mutableStateOf<WorkoutExercise?>(null) }
    LazyColumn(Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, "Voltar", tint = White) }
                Spacer(Modifier.width(4.dp))
                Text("EDITAR ROTINA", color = Lime, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.height(9.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(workout.name, modifier = Modifier.weight(1f), color = White,
                    style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                IconButton(onClick = { rename = true }) { Icon(Icons.Rounded.Edit, "Renomear", tint = Lime) }
            }
        }
        item {
            Panel(Modifier.fillMaxWidth()) {
                SectionHeading("Dias da semana")
                Spacer(Modifier.height(6.dp))
                Text("Toque para definir quando fará essa rotina.", color = Muted,
                    style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    items((1..7).toList()) { day ->
                        FilterChip(selected = day in workout.days,
                            onClick = {
                                val next = workout.days.toMutableSet()
                                if (day in next) next.remove(day) else next.add(day)
                                db.updateDays(workout.id, next)
                                onChange()
                            }, label = { Text(weekdays[day - 1]) })
                    }
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                SectionHeading("Exercícios", "${exercises.size} itens")
            }
            Spacer(Modifier.height(10.dp))
            SolidButton("+ Adicionar exercício", { picker = true }, Modifier.fillMaxWidth())
        }
        if (exercises.isEmpty()) {
            item { EmptyCard("Lista vazia", "Escolha aparelhos do catálogo para compor seu treino.") }
        }
        items(exercises, key = { it.id }) { entry ->
            val move = Catalog.get(entry.exerciseId)
            Panel(Modifier.fillMaxWidth()) {
                Text(move?.group ?: "Exercício", color = Lime,
                    style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text(move?.name ?: entry.exerciseId, color = White, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(move?.equipment ?: "Equipamento não identificado", color = Muted,
                    style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(11.dp))
                Text("${entry.sets} séries  •  ${entry.reps} repetições  •  ${entry.restSeconds}s de pausa",
                    color = White, style = MaterialTheme.typography.bodySmall)
                Row {
                    TextButton(onClick = { editedExercise = entry }) {
                        Icon(Icons.Rounded.Tune, null, Modifier.size(17.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("Ajustar")
                    }
                    TextButton(onClick = { deleteExercise = entry }) {
                        Icon(Icons.Rounded.Close, null, Modifier.size(17.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("Remover", color = Muted)
                    }
                }
            }
        }
    }
    if (rename) NameDialog("Renomear rotina", "Nome da rotina", workout.name,
        onDismiss = { rename = false }, onSave = {
            db.renameWorkout(workout.id, it)
            onChange()
            rename = false
        })
    if (picker) ExercisePickerDialog("Adicionar à ${workout.name}", { picker = false }) { exercise ->
        db.addExercise(workout.id, exercise.id)
        onChange()
        picker = false
    }
    editedExercise?.let { entry ->
        EditWorkoutExerciseDialog(entry, onDismiss = { editedExercise = null }) { sets, reps, rest ->
            db.updateExercise(entry.id, sets, reps, rest)
            editedExercise = null
            onChange()
        }
    }
    deleteExercise?.let { entry ->
        AlertDialog(onDismissRequest = { deleteExercise = null },
            title = { Text("Remover exercício?") },
            text = { Text("O exercício será removido desta rotina, sem apagar o histórico de cargas.") },
            confirmButton = { TextButton(onClick = {
                db.removeExercise(entry.id); deleteExercise = null; onChange()
            }) { Text("Remover") } },
            dismissButton = { TextButton(onClick = { deleteExercise = null }) { Text("Cancelar") } })
    }
}

@Composable
private fun EditWorkoutExerciseDialog(entry: WorkoutExercise, onDismiss: () -> Unit,
                                      onSave: (Int, Int, Int) -> Unit) {
    var sets by remember(entry.id) { mutableStateOf(entry.sets.toString()) }
    var reps by remember(entry.id) { mutableStateOf(entry.reps.toString()) }
    var rest by remember(entry.id) { mutableStateOf(entry.restSeconds.toString()) }
    val valid = (sets.toIntOrNull() ?: 0) in 1..100 &&
        (reps.toIntOrNull() ?: 0) in 1..1000 && (rest.toIntOrNull() ?: -1) in 0..3600
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Ajustar exercício") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                OutlinedTextField(sets, { sets = it }, label = { Text("Séries") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(reps, { reps = it }, label = { Text("Repetições") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(rest, { rest = it }, label = { Text("Descanso em segundos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            }
        }, confirmButton = {
            TextButton(onClick = { onSave(sets.toInt(), reps.toInt(), rest.toInt()) }, enabled = valid) {
                Text("Salvar")
            }
        }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}

@Composable
fun CatalogScreen(workouts: List<Workout>, onAdd: (String, Long) -> Unit) {
    var query by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("Todos") }
    var selectedForWorkout by remember { mutableStateOf<Exercise?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    val visible = remember(query, group) {
        Catalog.all.filter { exercise ->
            (group == "Todos" || exercise.group == group) &&
                (query.isBlank() || listOf(exercise.name, exercise.equipment, exercise.group)
                    .any { it.contains(query.trim(), ignoreCase = true) })
        }
    }
    LazyColumn(Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 26.dp, end = 20.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)) {
        item {
            PageHeading("Biblioteca", "Máquinas e exercícios",
                "${Catalog.all.size} exercícios por grupo muscular. Pesquise pelo nome ou aparelho.")
        }
        item {
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                placeholder = { Text("Ex.: leg press, polia, peitoral...") },
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                shape = RoundedCornerShape(16.dp)
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Todos") + Catalog.groups) { name ->
                    FilterChip(selected = group == name, onClick = { group = name },
                        label = { Text(name) })
                }
            }
        }
        item {
            Text("${visible.size} resultados", color = Muted,
                style = MaterialTheme.typography.labelMedium)
            message?.let {
                Spacer(Modifier.height(7.dp))
                Text(it, color = Lime, style = MaterialTheme.typography.bodySmall)
            }
        }
        if (visible.isEmpty()) item {
            EmptyCard("Nada encontrado", "Tente outro nome de máquina ou grupo muscular.")
        }
        items(visible, key = { it.id }) { exercise ->
            Panel(Modifier.fillMaxWidth()) {
                Text(exercise.group.uppercase(ptBr), color = Lime,
                    fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(4.dp))
                Text(exercise.name, color = White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(exercise.equipment, color = Muted,
                    style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(9.dp))
                TextButton(onClick = { selectedForWorkout = exercise },
                    contentPadding = PaddingValues(0.dp)) {
                    Icon(Icons.Rounded.AddCircleOutline, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Adicionar à rotina")
                }
            }
        }
    }
    selectedForWorkout?.let { selected ->
        AlertDialog(onDismissRequest = { selectedForWorkout = null },
            title = { Text("Em qual rotina?") },
            text = {
                if (workouts.isEmpty()) Text("Primeiro crie uma rotina na aba Rotinas.")
                else Column {
                    Text(selected.name, color = Muted,
                        style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    workouts.forEach { workout ->
                        TextButton(onClick = {
                            onAdd(selected.id, workout.id)
                            message = "${selected.name} adicionado a ${workout.name}."
                            selectedForWorkout = null
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text(workout.name, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
                            Icon(Icons.Rounded.Add, null)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { selectedForWorkout = null }) { Text("Fechar") } })
    }
}

/** Pesquisa e seleção reutilizável nas telas de rotina e de cargas. */
@Composable
fun ExercisePickerDialog(title: String, onDismiss: () -> Unit,
                         onSelect: (Exercise) -> Unit) {
    var query by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("Todos") }
    val items = remember(query, group) {
        Catalog.all.filter { item ->
            (group == "Todos" || item.group == group) &&
                (query.isBlank() || listOf(item.name, item.equipment, item.group)
                    .any { it.contains(query.trim(), ignoreCase = true) })
        }
    }
    Dialog(onDismissRequest = onDismiss) {
        Surface(Modifier.fillMaxWidth().fillMaxHeight(0.84f),
            color = SurfaceDark, shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, color = White, fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f), maxLines = 2)
                    IconButton(onClick = onDismiss) { Icon(Icons.Rounded.Close, "Fechar") }
                }
                OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true,
                    leadingIcon = { Icon(Icons.Rounded.Search, null) },
                    placeholder = { Text("Buscar exercício...") })
                Spacer(Modifier.height(9.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    items(listOf("Todos") + Catalog.groups) { name ->
                        FilterChip(selected = group == name, onClick = { group = name },
                            label = { Text(name) })
                    }
                }
                Spacer(Modifier.height(7.dp))
                LazyColumn(Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(items, key = { it.id }) { exercise ->
                        Column(Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(11.dp))
                            .clickable { onSelect(exercise) }
                            .padding(horizontal = 12.dp, vertical = 11.dp)) {
                            Text(exercise.name, color = White, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(2.dp))
                            Text("${exercise.group} • ${exercise.equipment}", color = Muted,
                                style = MaterialTheme.typography.bodySmall)
                        }
                        HorizontalDivider(color = SurfaceLift)
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceScreen(attendance: Set<LocalDate>, onToggle: (LocalDate) -> Unit) {
    val today = LocalDate.now()
    var month by remember { mutableStateOf(YearMonth.from(today)) }
    var mode by remember { mutableIntStateOf(0) } // 0: registrar; 1: mapa somente leitura
    val count = attendance.count { YearMonth.from(it) == month }
    val weekStart = today.minusDays((today.dayOfWeek.value - 1).toLong())
    val weekCount = attendance.count { it >= weekStart && it <= today }
    val currentStreak = remember(attendance, today) {
        var date = if (today in attendance) today else today.minusDays(1)
        var counter = 0
        while (date in attendance) { counter++; date = date.minusDays(1) }
        counter
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 26.dp)) {
        PageHeading("Disciplina visual", "Calendário de presença",
            "Uma bolinha verde para cada dia que você foi treinar.")
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Panel(Modifier.weight(1f)) {
                Text("$count", color = Lime, style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black)
                Text("dias no mês", color = Muted, style = MaterialTheme.typography.bodySmall)
            }
            Panel(Modifier.weight(1f)) {
                Text("$weekCount", color = Lime, style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black)
                Text("nesta semana", color = Muted, style = MaterialTheme.typography.bodySmall)
            }
            Panel(Modifier.weight(1f)) {
                Text("$currentStreak", color = Lime, style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black)
                Text("dias seguidos", color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(17.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = mode == 0, onClick = { mode = 0 },
                label = { Text("Marcar dias") })
            FilterChip(selected = mode == 1, onClick = { mode = 1 },
                label = { Text("Mapa de consistência") })
        }
        Spacer(Modifier.height(12.dp))
        Surface(color = CalendarPaper, shape = RoundedCornerShape(27.dp)) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 19.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { month = month.minusMonths(1) }) {
                        Icon(Icons.Rounded.ChevronLeft, "Mês anterior", tint = Ink)
                    }
                    Text(month.atDay(1).format(DateTimeFormatter.ofPattern("MMMM yyyy", ptBr))
                        .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black,
                        color = Ink)
                    IconButton(onClick = { month = month.plusMonths(1) },
                        enabled = month < YearMonth.from(today)) {
                        Icon(Icons.Rounded.ChevronRight, "Próximo mês", tint = Ink)
                    }
                }
                Spacer(Modifier.height(15.dp))
                Row {
                    weekdays.forEach { name ->
                        Text(name.first().toString(), modifier = Modifier.weight(1f),
                            color = Color(0xFF697769), textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(Modifier.height(11.dp))
                val offset = month.atDay(1).dayOfWeek.value - 1
                val rows = (offset + month.lengthOfMonth() + 6) / 7
                repeat(rows) { row ->
                    Row(Modifier.fillMaxWidth()) {
                        repeat(7) { col ->
                            val number = row * 7 + col - offset + 1
                            val valid = number in 1..month.lengthOfMonth()
                            val date = if (valid) month.atDay(number) else null
                            val marked = date != null && date in attendance
                            val enabled = mode == 0 && date != null && !date.isAfter(today)
                            Column(Modifier.weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(enabled = enabled) { if (date != null) onToggle(date) }
                                .padding(vertical = 9.dp),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                if (mode == 0) {
                                    Box(Modifier.size(27.dp)
                                        .then(if (date == today) Modifier.border(1.dp,
                                            Color(0xFF4B8450), CircleShape) else Modifier),
                                        contentAlignment = Alignment.Center) {
                                        Text(if (valid) number.toString() else "",
                                            color = if (enabled) Ink else Color(0xFF9AAA9C),
                                            fontWeight = if (date == today) FontWeight.Bold else FontWeight.Normal,
                                            style = MaterialTheme.typography.bodySmall)
                                    }
                                    Spacer(Modifier.height(7.dp))
                                } else {
                                    Text(if (valid) number.toString() else "", color = Ink,
                                        style = MaterialTheme.typography.labelSmall)
                                    Spacer(Modifier.height(10.dp))
                                }
                                Box(Modifier.size(if (mode == 0) 13.dp else 20.dp).clip(CircleShape)
                                    .background(if (!valid) Color.Transparent else
                                        if (marked) PresenceGreen else Ink))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(13.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(11.dp).clip(CircleShape).background(Ink))
                    Spacer(Modifier.width(6.dp))
                    Text("Não marcado", color = Ink, style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.width(19.dp))
                    Box(Modifier.size(11.dp).clip(CircleShape).background(PresenceGreen))
                    Spacer(Modifier.width(6.dp))
                    Text("Compareceu", color = Ink, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Panel(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.TouchApp, null, tint = Lime)
                Spacer(Modifier.width(8.dp))
                Text(if (mode == 0) "Toque nos dias para marcar ou desmarcar."
                     else "Seu mapa mostra os dias em que treinou.", color = White,
                    style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(7.dp))
            Text(if (mode == 0)
                "Você pode registrar treinos de datas passadas. Datas futuras ficam bloqueadas."
                else "Bolinhas pretas: sem registro. Verdes: presença confirmada. Volte à opção Marcar dias para editar.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
fun ProgressScreen(selectedId: String, onSelect: (String) -> Unit,
                   entries: List<WeightEntry>, loadedExerciseIds: Set<String>,
                   onAdd: (Double, Int) -> Unit, onDelete: (Long) -> Unit) {
    var selecting by remember { mutableStateOf(false) }
    var adding by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<WeightEntry?>(null) }
    val selected = Catalog.get(selectedId) ?: Catalog.all.first()
    val first = entries.firstOrNull()
    val last = entries.lastOrNull()
    val best = entries.maxOfOrNull { it.weight }
    val delta = if (first != null && last != null) last.weight - first.weight else 0.0
    LazyColumn(Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 26.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            PageHeading("Sua evolução", "Progressão de carga",
                "Registre o peso e as repetições de cada exercício.")
        }
        item {
            Panel(Modifier.fillMaxWidth()) {
                Text("EXERCÍCIO SELECIONADO", color = Lime,
                    style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(7.dp))
                Text(selected.name, color = White, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge)
                Text("${selected.group} • ${selected.equipment}", color = Muted,
                    style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = { selecting = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.SwapHoriz, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Trocar exercício")
                }
            }
        }
        if (loadedExerciseIds.isNotEmpty()) {
            item {
                Text("COM HISTÓRICO", color = Muted,
                    style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(7.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(loadedExerciseIds.sorted().toList()) { id ->
                        val name = Catalog.get(id)?.name ?: id
                        FilterChip(selected = id == selectedId, onClick = { onSelect(id) },
                            label = { Text(name, maxLines = 1) })
                    }
                }
            }
        }
        item { SolidButton("+ Registrar carga", { adding = true }, Modifier.fillMaxWidth()) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Panel(Modifier.weight(1f)) {
                    Text(if (last == null) "—" else "${fmtKg(last.weight)} kg",
                        color = Lime, fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge)
                    Text("última carga", color = Muted, style = MaterialTheme.typography.bodySmall)
                }
                Panel(Modifier.weight(1f)) {
                    Text(if (best == null) "—" else "${fmtKg(best)} kg",
                        color = Lime, fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge)
                    Text("recorde", color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        item {
            Panel(Modifier.fillMaxWidth()) {
                SectionHeading("Linha de evolução", "${entries.size} registros")
                Spacer(Modifier.height(9.dp))
                if (entries.isEmpty()) {
                    Text("Registre a primeira carga para iniciar o gráfico.", color = Muted,
                        style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(20.dp))
                } else {
                    Text("Variação: ${if (delta > 0) "+" else ""}${fmtKg(delta)} kg desde o primeiro registro",
                        color = if (delta >= 0) Lime else Muted,
                        style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(16.dp))
                    WeightChart(entries, Modifier.fillMaxWidth().height(166.dp))
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(first?.date?.format(DateTimeFormatter.ofPattern("dd/MM/yy")) ?: "",
                            color = Muted, style = MaterialTheme.typography.labelSmall)
                        Text(last?.date?.format(DateTimeFormatter.ofPattern("dd/MM/yy")) ?: "",
                            color = Muted, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
        item { SectionHeading("Registros recentes") }
        if (entries.isEmpty()) {
            item { EmptyCard("Seu histórico começa com um registro",
                "Adicione o peso utilizado e o número de repetições realizadas.") }
        } else {
            items(entries.asReversed(), key = { it.id }) { entry ->
                Panel(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${fmtKg(entry.weight)} kg  ×  ${entry.reps} reps",
                                color = White, fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium)
                            Text(entry.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { deleting = entry }) {
                            Icon(Icons.Rounded.DeleteOutline, "Excluir registro", tint = Muted)
                        }
                    }
                }
            }
        }
    }
    if (selecting) ExercisePickerDialog("Escolher exercício", { selecting = false }) { exercise ->
        onSelect(exercise.id)
        selecting = false
    }
    if (adding) AddLoadDialog(onDismiss = { adding = false }) { weight, reps ->
        onAdd(weight, reps)
        adding = false
    }
    deleting?.let { entry ->
        AlertDialog(onDismissRequest = { deleting = null },
            title = { Text("Excluir registro?") },
            text = { Text("Excluir ${fmtKg(entry.weight)} kg de ${entry.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}?") },
            confirmButton = { TextButton(onClick = { onDelete(entry.id); deleting = null }) { Text("Excluir") } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancelar") } })
    }
}

@Composable
private fun AddLoadDialog(onDismiss: () -> Unit, onSave: (Double, Int) -> Unit) {
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("12") }
    val kg = weight.replace(',', '.').toDoubleOrNull()
    val repetitions = reps.toIntOrNull()
    val valid = kg != null && kg.isFinite() && kg >= 0.0 && kg <= 10000.0 &&
        repetitions != null && repetitions in 1..1000
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Registrar treino de hoje") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Informe a carga total usada no equipamento. No peso corporal, use 0 kg.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(value = weight, onValueChange = { weight = it },
                    label = { Text("Carga (kg)") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(value = reps, onValueChange = { reps = it },
                    label = { Text("Repetições") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        }, confirmButton = {
            TextButton(onClick = { if (kg != null && repetitions != null) onSave(kg, repetitions) },
                enabled = valid) { Text("Salvar") }
        }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}

@Composable
private fun WeightChart(entries: List<WeightEntry>, modifier: Modifier = Modifier) {
    val values = entries.takeLast(30).map { it.weight.toFloat() }
    val lower = (values.minOrNull() ?: 0f).let { (it - 5f).coerceAtLeast(0f) }
    val upper = (values.maxOrNull() ?: 1f) + 5f
    Canvas(modifier) {
        val left = 8.dp.toPx()
        val right = size.width - 8.dp.toPx()
        val top = 8.dp.toPx()
        val bottom = size.height - 12.dp.toPx()
        repeat(4) { index ->
            val y = top + (bottom - top) * index / 3f
            drawLine(color = SurfaceLift, start = Offset(left, y),
                end = Offset(right, y), strokeWidth = 1.dp.toPx())
        }
        val positions = values.mapIndexed { index, weight ->
            val x = if (values.size == 1) (left + right) / 2f
                    else left + (right - left) * index / (values.size - 1).toFloat()
            val ratio = ((weight - lower) / max(upper - lower, 1f)).coerceIn(0f, 1f)
            Offset(x, bottom - ratio * (bottom - top))
        }
        positions.zipWithNext().forEach { (a, b) ->
            drawLine(Lime, a, b, strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
        }
        positions.forEach { point ->
            drawCircle(color = SurfaceDark, radius = 6.dp.toPx(), center = point)
            drawCircle(color = Lime, radius = 4.dp.toPx(), center = point)
        }
    }
}
