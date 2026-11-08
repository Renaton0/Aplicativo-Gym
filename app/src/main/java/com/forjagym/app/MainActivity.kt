package com.forjagym.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

class MainActivity : ComponentActivity() {
    private lateinit var gymDb: GymDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gymDb = GymDatabase(applicationContext)
        window.statusBarColor = android.graphics.Color.rgb(16, 20, 17)
        window.navigationBarColor = android.graphics.Color.rgb(16, 20, 17)
        setContent { ForjaTheme { GymApp(gymDb) } }
    }

    override fun onDestroy() {
        gymDb.close()
        super.onDestroy()
    }
}

private data class NavTab(val text: String, val icon: ImageVector)
private val tabs = listOf(
    NavTab("Início", Icons.Rounded.Home),
    NavTab("Rotinas", Icons.Rounded.FitnessCenter),
    NavTab("Máquinas", Icons.Rounded.GridView),
    NavTab("Presença", Icons.Rounded.EventAvailable),
    NavTab("Cargas", Icons.Rounded.TrendingUp)
)

@Composable
private fun GymApp(db: GymDatabase) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var editorId by rememberSaveable { mutableStateOf<Long?>(null) }
    var progressExercise by rememberSaveable { mutableStateOf(Catalog.all.first().id) }
    var revision by remember { mutableIntStateOf(0) }
    val refresh: () -> Unit = { revision++ }
    val workouts = remember(revision) { db.workouts() }
    val present = remember(revision) { db.attendance() }

    BackHandler(enabled = editorId != null) { editorId = null }
    Scaffold(
        containerColor = Ink,
        bottomBar = {
            if (editorId == null) {
                NavigationBar(containerColor = SurfaceDark) {
                    tabs.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = tab == index,
                            onClick = { tab = index },
                            icon = { Icon(item.icon, item.text) },
                            label = { Text(item.text, maxLines = 1) },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Ink,
                                selectedTextColor = Lime,
                                indicatorColor = Lime,
                                unselectedIconColor = Muted,
                                unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (editorId != null) {
                val current = workouts.firstOrNull { it.id == editorId }
                if (current != null) {
                    WorkoutEditorScreen(
                        workout = current,
                        exercises = remember(revision, current.id) { db.workoutExercises(current.id) },
                        onBack = { editorId = null },
                        onChange = refresh,
                        db = db
                    )
                } else {
                    LaunchedEffect(editorId) { editorId = null }
                }
            } else when (tab) {
                0 -> HomeScreen(workouts, present,
                    onPresence = { db.toggleAttendance(java.time.LocalDate.now()); refresh() },
                    onOpenRoutines = { tab = 1 },
                    onOpenCalendar = { tab = 3 },
                    onOpenLoads = { tab = 4 },
                    db = db)
                1 -> WorkoutsScreen(workouts,
                    onCreate = { name ->
                        val id = db.addWorkout(name)
                        refresh()
                        editorId = id
                    },
                    onOpen = { editorId = it },
                    onDelete = { db.removeWorkout(it); refresh() },
                    db = db)
                2 -> CatalogScreen(workouts, onAdd = { id, workout ->
                    db.addExercise(workout, id)
                    refresh()
                })
                3 -> AttendanceScreen(present) { date -> db.toggleAttendance(date); refresh() }
                else -> ProgressScreen(
                    selectedId = progressExercise,
                    onSelect = { progressExercise = it },
                    entries = remember(revision, progressExercise) { db.loadEntries(progressExercise) },
                    loadedExerciseIds = remember(revision) { db.exercisesWithLoads() },
                    onAdd = { weight, reps ->
                        db.addLoad(progressExercise, weight, reps)
                        refresh()
                    },
                    onDelete = { db.removeLoad(it); refresh() }
                )
            }
        }
    }
}
