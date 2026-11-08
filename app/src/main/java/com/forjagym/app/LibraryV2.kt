package com.forjagym.app

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent

@Composable
fun MachinePhotoV2(exercise: Exercise, prefs: ForjaPrefs, modifier: Modifier = Modifier) {
    val custom = prefs.customPhoto(exercise.id)
    val photo = MachineMedia.forExercise(exercise)
    val source = custom ?: photo?.url
    val shape = RoundedCornerShape(9.dp)
    Box(modifier.clip(shape).background(SurfaceLift).border(1.dp, Hairline, shape),
        contentAlignment = Alignment.Center) {
        if (source != null) {
            SubcomposeAsyncImage(model = source, contentDescription = "Imagem de referência: ${exercise.equipment}",
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) {
                when (painter.state) {
                    is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
                    is AsyncImagePainter.State.Loading -> CircularProgressIndicator(
                        Modifier.size(20.dp), strokeWidth = 2.dp, color = Lime)
                    else -> PhotoNotAvailable()
                }
            }
        } else PhotoNotAvailable()
    }
}

@Composable
private fun PhotoNotAvailable() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Rounded.PhotoCamera, null, tint = Muted, modifier = Modifier.size(24.dp))
        Text("Sem foto", style = MaterialTheme.typography.labelSmall, color = Muted)
    }
}

@Composable
fun LibraryTabV2(prefs: ForjaPrefs, revision: Int, onOpen: (String) -> Unit) {
    var search by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("Todos") }
    val filtered = remember(search, group) {
        Catalog.all.filter { e ->
            (group == "Todos" || e.group == group) &&
                (search.isBlank() || listOf(e.name, e.equipment, e.group).any {
                    it.contains(search.trim(), ignoreCase = true)
                })
        }
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            ForjaHeader("Biblioteca técnica", "Aparelhos e exercícios",
                "151 variações • identificação, execução e registro")
            Spacer(Modifier.height(18.dp))
            OutlinedTextField(value = search, onValueChange = { search = it },
                placeholder = { Text("Buscar aparelho ou exercício") }, singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Todos") + Catalog.groups) { g ->
                    FilterChip(selected = group == g, onClick = { group = g },
                        label = { Text(g) }, shape = RoundedCornerShape(8.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            ForjaTitleRow("Catálogo", "${filtered.size} resultados")
        }
        if (filtered.isEmpty()) item { EmptyHint("Nenhum resultado", "Tente outro nome ou grupo muscular.") }
        items(filtered, key = { it.id }) { exercise ->
            Surface(shape = RoundedCornerShape(11.dp), color = SurfaceDark,
                modifier = Modifier.fillMaxWidth()
                    .border(1.dp, Hairline, RoundedCornerShape(11.dp))
                    .clickable { onOpen(exercise.id) }) {
                Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    MachinePhotoV2(exercise, prefs, Modifier.size(88.dp))
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(exercise.group.uppercase(), color = Lime,
                            style = MaterialTheme.typography.labelSmall)
                        Spacer(Modifier.height(4.dp))
                        Text(exercise.name, color = White, fontWeight = FontWeight.SemiBold,
                            maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(exercise.equipment, style = MaterialTheme.typography.bodySmall,
                            color = Muted, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    Icon(Icons.Rounded.ChevronRight, null, tint = Muted)
                }
            }
        }
    }
}

@Composable
fun ExerciseDetailV2(exercise: Exercise, db: GymDatabase, prefs: ForjaPrefs,
                     revision: Int, onChange: () -> Unit, onBack: () -> Unit,
                     onShowProgress: () -> Unit, workouts: List<Workout>) {
    val context = LocalContext.current
    var showRoutine by remember { mutableStateOf(false) }
    var notice by remember { mutableStateOf("") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try { context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: SecurityException) { }
            prefs.savePhoto(exercise.id, uri)
            onChange()
        }
    }
    val photo = MachineMedia.forExercise(exercise)
    val custom = prefs.customPhoto(exercise.id)
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            TextButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, "Voltar")
                Spacer(Modifier.width(6.dp)); Text("Voltar para aparelhos")
            }
            Spacer(Modifier.height(5.dp))
            ForjaHeader(exercise.group, exercise.name, exercise.equipment)
        }
        item {
            MachinePhotoV2(exercise, prefs, Modifier.fillMaxWidth().height(224.dp))
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(if (custom == null) "Imagem de referência do equipamento" else "Sua foto do aparelho",
                    modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = Muted)
                TextButton(onClick = { picker.launch(arrayOf("image/*")) }) {
                    Icon(Icons.Rounded.AddAPhoto, null, Modifier.size(18.dp));
                    Spacer(Modifier.width(5.dp)); Text("Minha foto")
                }
            }
            if (custom != null) {
                TextButton(onClick = { prefs.savePhoto(exercise.id, null); onChange() }) {
                    Text("Remover foto personalizada")
                }
            } else if (photo != null) {
                TextButton(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(photo.attributionUrl))) },
                    contentPadding = PaddingValues(0.dp)) {
                    Text("Foto: ${photo.author} • ${photo.license} • Ver fonte",
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        item {
            ForjaPanel(Modifier.fillMaxWidth()) {
                ForjaTitleRow("Músculos envolvidos")
                Spacer(Modifier.height(7.dp))
                Text(ExerciseGuide.muscles(exercise), color = Muted,
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            ForjaPanel(Modifier.fillMaxWidth()) {
                ForjaTitleRow("Execução — referência básica")
                Spacer(Modifier.height(11.dp))
                ExerciseGuide.steps(exercise).forEachIndexed { index, step ->
                    Row(Modifier.padding(bottom = 11.dp)) {
                        Text("0${index+1}", color = Lime, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(13.dp))
                        Text(step, color = White, modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Text("Ajuste a orientação com um profissional. Não treine com dor.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
        item {
            val entries = remember(revision, exercise.id) { db.loadEntries(exercise.id) }
            QuickLoadV2(exercise, entries, onSave = { weight, reps ->
                db.addLoad(exercise.id, weight, reps); onChange()
            })
        }
        item {
            ForjaButton("Ver evolução de carga", onShowProgress, Modifier.fillMaxWidth())
            Spacer(Modifier.height(7.dp))
            OutlinedButton(onClick = { showRoutine = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.PlaylistAdd, null)
                Spacer(Modifier.width(6.dp)); Text("Adicionar a uma rotina")
            }
            if (notice.isNotEmpty()) {
                Spacer(Modifier.height(6.dp)); Text(notice, color = Lime)
            }
        }
    }
    if (showRoutine) AlertDialog(onDismissRequest = { showRoutine = false },
        title = { Text("Adicionar exercício") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                if (workouts.isEmpty()) Text("Crie uma rotina na aba Rotinas antes de adicionar.")
                workouts.forEach { w ->
                    TextButton(onClick = {
                        db.addExercise(w.id, exercise.id); onChange()
                        notice = "Adicionado a ${w.name}"; showRoutine = false
                    }) { Text(w.name) }
                }
            }
        }, confirmButton = { TextButton(onClick = { showRoutine = false }) { Text("Fechar") } })
}
