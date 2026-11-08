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
    private lateinit var database: GymDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = GymDatabase(applicationContext)
        val prefs = ForjaPrefs(applicationContext)
        window.statusBarColor = android.graphics.Color.rgb(16, 22, 27)
        window.navigationBarColor = android.graphics.Color.rgb(16, 22, 27)
        setContent { ForjaTheme { GymAppV2(database, prefs) } }
    }
    override fun onDestroy() {
        database.close()
        super.onDestroy()
    }
}

private data class Tab(val title: String, val icon: ImageVector)
private val tabsV2 = listOf(
    Tab("Hoje", Icons.Rounded.Dashboard),
    Tab("Rotinas", Icons.Rounded.FitnessCenter),
    Tab("Aparelhos", Icons.Rounded.GridView),
    Tab("Disciplina", Icons.Rounded.CalendarMonth),
    Tab("Cargas", Icons.Rounded.ShowChart)
)

@Composable
private fun GymAppV2(db: GymDatabase, prefs: ForjaPrefs) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var exerciseId by rememberSaveable { mutableStateOf<String?>(null) }
    var routineId by rememberSaveable { mutableStateOf<Long?>(null) }
    var progressId by rememberSaveable { mutableStateOf(Catalog.all.first().id) }
    var revision by remember { mutableIntStateOf(0) }
    val refresh: () -> Unit = { revision++ }
    val workouts = remember(revision) { db.workouts() }
    val attendance = remember(revision) { db.attendance() }
    BackHandler(enabled = exerciseId != null || routineId != null) {
        if (exerciseId != null) exerciseId = null else routineId = null
    }
    Scaffold(containerColor = Ink, bottomBar = {
        if (exerciseId == null && routineId == null) {
            NavigationBar(containerColor = SurfaceDark) {
                tabsV2.forEachIndexed { index, t ->
                    NavigationBarItem(
                        selected = index == tab,
                        onClick = { tab = index },
                        icon = { Icon(t.icon, t.title) },
                        label = { Text(t.title, maxLines = 1) },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Ink, selectedTextColor = Lime,
                            indicatorColor = Lime, unselectedIconColor = Muted,
                            unselectedTextColor = Muted
                        )
                    )
                }
            }
        }
    }) { contentPadding ->
        Box(Modifier.fillMaxSize().padding(contentPadding)) {
            val e = exerciseId?.let { Catalog.get(it) }
            if (e != null) {
                ExerciseDetailV2(e, db, prefs, revision, refresh,
                    onBack = { exerciseId = null },
                    onShowProgress = { progressId = e.id; exerciseId = null; routineId = null; tab = 4 },
                    workouts = workouts)
            } else if (routineId != null) {
                val routine = workouts.firstOrNull { it.id == routineId }
                if (routine != null) {
                    RoutineEditorV2(routine, db, revision, refresh,
                        onBack = { routineId = null }, onExercise = { exerciseId = it })
                } else LaunchedEffect(routineId) { routineId = null }
            } else when (tab) {
                0 -> DashboardV2(workouts, attendance, prefs, db, revision,
                    onMarkToday = { db.toggleAttendance(java.time.LocalDate.now()); refresh() },
                    onRoutines = { tab = 1 }, onCalendar = { tab = 3 },
                    onExercise = { exerciseId = it })
                1 -> RoutinesTabV2(workouts, db, refresh, onOpen = { routineId = it })
                2 -> LibraryTabV2(prefs, revision, onOpen = { exerciseId = it })
                3 -> DisciplineTabV2(attendance, prefs,
                    onToggle = { db.toggleAttendance(it); refresh() }, onPrefsChange = refresh)
                else -> LoadsTabV2(db, progressId,
                    onSelect = { progressId = it }, onExplore = { exerciseId = it },
                    revision = revision, onChange = refresh)
            }
        }
    }
}
