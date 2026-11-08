package com.forjagym.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.max

/** One-tap repeat of previous load. Weight and reps also editable without leaving the exercise. */
@Composable
fun QuickLoadV2(exercise: Exercise, entries: List<WeightEntry>, onSave: (Double, Int) -> Unit) {
    val latest = entries.lastOrNull()
    var kgText by remember(exercise.id, latest?.id) {
        mutableStateOf(latest?.weight?.let(::displayKg) ?: "")
    }
    var repsText by remember(exercise.id, latest?.id) {
        mutableStateOf(latest?.reps?.toString() ?: "12")
    }
    var feedback by remember(exercise.id) { mutableStateOf("") }
    val kg = kgText.trim().replace(',', '.').toDoubleOrNull()
    val reps = repsText.toIntOrNull()
    val valid = kg != null && kg.isFinite() && kg in 0.0..10000.0 && reps != null && reps in 1..1000
    ForjaPanel(Modifier.fillMaxWidth()) {
        ForjaTitleRow("Registro rápido", "kg × repetições")
        Spacer(Modifier.height(6.dp))
        Text(if (latest == null) "Primeiro registro deste exercício" else
            "Último: ${displayKg(latest.weight)} kg × ${latest.reps} reps",
            color = Muted, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(15.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = kgText, onValueChange = { kgText = it },
                label = { Text("Carga (kg)") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1.35f), shape = RoundedCornerShape(9.dp))
            OutlinedTextField(value = repsText, onValueChange = { repsText = it },
                label = { Text("Reps") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.9f), shape = RoundedCornerShape(9.dp))
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                val value = (kg ?: 0.0) - 2.5
                kgText = displayKg(value.coerceAtLeast(0.0))
            }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) {
                Text("− 2,5 kg")
            }
            OutlinedButton(onClick = {
                kgText = displayKg(((kg ?: 0.0) + 2.5).coerceAtMost(10000.0))
            }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) {
                Text("+ 2,5 kg")
            }
        }
        Spacer(Modifier.height(9.dp))
        ForjaButton(if (latest != null && kg == latest.weight && reps == latest.reps)
            "Repetir carga anterior" else "Salvar carga de hoje", onClick = {
            if (valid) {
                onSave(kg!!, reps!!)
                feedback = "Carga registrada com sucesso"
            }
        }, Modifier.fillMaxWidth(), enabled = valid)
        if (feedback.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(feedback, color = Lime, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun LoadsTabV2(db: GymDatabase, selectedId: String, onSelect: (String) -> Unit,
               onExplore: (String) -> Unit, revision: Int, onChange: () -> Unit) {
    val e = Catalog.get(selectedId) ?: Catalog.all.first()
    val history = remember(selectedId, revision) { db.loadEntries(e.id) }
    val withHistory = remember(revision) { db.exercisesWithLoads().toList().sorted() }
    var selecting by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<WeightEntry?>(null) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { ForjaHeader("Performance", "Progressão de carga", "Última carga, histórico e evolução real") }
        item {
            ForjaPanel(Modifier.fillMaxWidth()) {
                Text("EXERCÍCIO ATUAL", color = Lime,
                    style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(7.dp))
                Text(e.name, color = White, style = MaterialTheme.typography.titleLarge)
                Text(e.equipment, color = Muted, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    OutlinedButton(onClick = { selecting = true }, modifier = Modifier.weight(1f)) {
                        Text("Trocar exercício")
                    }
                    OutlinedButton(onClick = { onExplore(e.id) }) {
                        Icon(Icons.Rounded.Info, "Ficha do exercício")
                    }
                }
            }
        }
        if (withHistory.isNotEmpty()) item {
            Text("REGISTRADOS", color = Muted, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(7.dp))
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                items(withHistory) { id ->
                    FilterChip(selected = e.id == id, onClick = { onSelect(id) },
                        label = { Text(Catalog.get(id)?.name ?: id) },
                        shape = RoundedCornerShape(8.dp))
                }
            }
        }
        item { QuickLoadV2(e, history, onSave = { kg, reps -> db.addLoad(e.id, kg, reps); onChange() }) }
        item {
            val last = history.lastOrNull()
            val best = history.maxOfOrNull { it.weight }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Metric("Última carga", last?.let { "${displayKg(it.weight)} kg" } ?: "—",
                    Modifier.weight(1f), Lime)
                Metric("Recorde", best?.let { "${displayKg(it)} kg" } ?: "—",
                    Modifier.weight(1f), White)
            }
        }
        item {
            ForjaPanel(Modifier.fillMaxWidth()) {
                ForjaTitleRow("Evolução", "${history.size} registros")
                Spacer(Modifier.height(8.dp))
                if (history.isEmpty()) {
                    Text("Seu gráfico aparecerá após registrar uma carga.", color = Muted)
                } else {
                    val change = history.last().weight - history.first().weight
                    Text("Desde o início: ${if (change > 0) "+" else ""}${displayKg(change)} kg",
                        color = Lime, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(14.dp))
                    LoadLineV2(history.takeLast(30), Modifier.fillMaxWidth().height(155.dp))
                }
            }
        }
        item { ForjaTitleRow("Histórico", "Mais recentes primeiro") }
        if (history.isEmpty()) item { EmptyHint("Sem registros", "Adicione seu primeiro levantamento acima.") }
        items(history.asReversed(), key = { it.id }) { item ->
            ForjaPanel(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("${displayKg(item.weight)} kg × ${item.reps} reps", color = White,
                            fontWeight = FontWeight.SemiBold)
                        Text(item.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { deleting = item }) {
                        Icon(Icons.Rounded.DeleteOutline, "Excluir registro", tint = Muted)
                    }
                }
            }
        }
    }
    if (selecting) ExerciseSelectorV2(onDismiss = { selecting = false }, onChoose = {
        onSelect(it.id); selecting = false
    })
    deleting?.let { entry ->
        AlertDialog(onDismissRequest = { deleting = null }, title = { Text("Excluir registro?") },
            text = { Text("Essa carga será removida do histórico.") },
            confirmButton = { TextButton(onClick = {
                db.removeLoad(entry.id); deleting = null; onChange()
            }) { Text("Excluir") } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancelar") } })
    }
}

@Composable
fun ExerciseSelectorV2(onDismiss: () -> Unit, onChoose: (Exercise) -> Unit) {
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
                LazyColumn(Modifier.heightIn(max = 360.dp)) {
                    items(filtered, key = { it.id }) { exercise ->
                        TextButton(onClick = { onChoose(exercise) },
                            modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.fillMaxWidth()) {
                                Text(exercise.name, color = White)
                                Text(exercise.group, color = Muted,
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = onDismiss) { Text("Fechar") } })
}

@Composable
private fun LoadLineV2(entries: List<WeightEntry>, modifier: Modifier = Modifier) {
    val values = entries.map { it.weight.toFloat() }
    Canvas(modifier) {
        if (values.isEmpty()) return@Canvas
        val hi = max(values.maxOrNull() ?: 1f, 1f)
        val lo = values.minOrNull() ?: 0f
        val range = max(hi - lo, 1f)
        val left = 9.dp.toPx(); val right = size.width - 9.dp.toPx()
        val top = 8.dp.toPx(); val bottom = size.height - 11.dp.toPx()
        for (i in 0..3) {
            val y = top + (bottom - top) * i / 3f
            drawLine(Hairline, Offset(left, y), Offset(right, y), strokeWidth = 1.dp.toPx())
        }
        val points = values.mapIndexed { index, value ->
            val x = if (values.size == 1) (left + right) / 2f
                else left + index.toFloat() / (values.size - 1) * (right - left)
            val y = bottom - (value - lo) / range * (bottom - top)
            Offset(x, y)
        }
        points.zipWithNext { a, b -> drawLine(Lime, a, b,
            strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round) }
        points.forEach { p -> drawCircle(Lime, radius = 3.5.dp.toPx(), center = p) }
    }
}
