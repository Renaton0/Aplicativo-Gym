package com.forjagym.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
        setContent { ForjaTheme { GymAppV21(database, prefs) } }
    }

    override fun onDestroy() {
        database.close()
        super.onDestroy()
    }
}

private data class Tab(val title: String, val icon: ImageVector)
private val tabsV21 = listOf(
    Tab("Hoje", Icons.Rounded.Dashboard),
    Tab("Rotinas", Icons.Rounded.FitnessCenter),
    Tab("Aparelhos", Icons.Rounded.GridView),
    Tab("Disciplina", Icons.Rounded.CalendarMonth),
    Tab("Cargas", Icons.Rounded.ShowChart)
)

@Composable
private fun GymAppV21(db: GymDatabase, prefs: ForjaPrefs) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var exerciseId by rememberSaveable { mutableStateOf<String?>(null) }
    var routineId by rememberSaveable { mutableStateOf<Long?>(null) }
    var activeWorkoutId by rememberSaveable { mutableStateOf<Long?>(null) }
    var progressId by rememberSaveable { mutableStateOf(Catalog.all.first().id) }
    var revision by remember { mutableIntStateOf(0) }
    val refresh: () -> Unit = { revision++ }
    val workouts = remember(revision) { db.workouts() }
    val attendance = remember(revision) { db.attendance() }

    BackHandler(enabled = exerciseId != null || routineId != null || activeWorkoutId != null) {
        when {
            exerciseId != null -> exerciseId = null
            routineId != null -> routineId = null
            activeWorkoutId != null -> activeWorkoutId = null
        }
    }

    Scaffold(containerColor = Ink, bottomBar = {
        if (exerciseId == null && routineId == null && activeWorkoutId == null) {
            NavigationBar(containerColor = SurfaceDark) {
                tabsV21.forEachIndexed { index, t ->
                    NavigationBarItem(
                        selected = index == tab,
                        onClick = { tab = index },
                        icon = { Icon(t.icon, t.title) },
                        label = { Text(t.title, maxLines = 1) },
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
    }) { contentPadding ->
        Box(Modifier.fillMaxSize().padding(contentPadding)) {
            val selectedExercise = exerciseId?.let { Catalog.get(it) }
            when {
                activeWorkoutId != null -> {
                    val workout = workouts.firstOrNull { it.id == activeWorkoutId }
                    if (workout != null) {
                        ActiveWorkoutScreenV2(
                            workout = workout,
                            planned = remember(workout.id, revision) { db.workoutExercises(workout.id) },
                            db = db,
                            prefs = prefs,
                            onBack = { activeWorkoutId = null },
                            onOpenExercise = { exerciseId = it },
                            onOpenLoads = {
                                progressId = it
                                activeWorkoutId = null
                                tab = 4
                            }
                        )
                    } else {
                        LaunchedEffect(activeWorkoutId) { activeWorkoutId = null }
                    }
                }

                selectedExercise != null -> {
                    ExerciseDetailV2(
                        exercise = selectedExercise,
                        db = db,
                        prefs = prefs,
                        revision = revision,
                        onChange = refresh,
                        onBack = { exerciseId = null },
                        onShowProgress = {
                            progressId = selectedExercise.id
                            exerciseId = null
                            tab = 4
                        },
                        workouts = workouts,
                        onOpenAlternative = { exerciseId = it }
                    )
                }

                routineId != null -> {
                    val routine = workouts.firstOrNull { it.id == routineId }
                    if (routine != null) {
                        RoutineEditorV2(
                            workout = routine,
                            db = db,
                            prefs = prefs,
                            revision = revision,
                            onChange = refresh,
                            onBack = { routineId = null },
                            onExercise = { exerciseId = it }
                        )
                    } else LaunchedEffect(routineId) { routineId = null }
                }

                else -> when (tab) {
                    0 -> DashboardV2(
                        workouts = workouts,
                        attendance = attendance,
                        prefs = prefs,
                        db = db,
                        revision = revision,
                        onMarkToday = { db.toggleAttendance(java.time.LocalDate.now()); refresh() },
                        onRoutines = { tab = 1 },
                        onCalendar = { tab = 3 },
                        onExercise = { exerciseId = it },
                        onStartWorkout = { activeWorkoutId = it }
                    )

                    1 -> RoutinesTabV2(workouts, db, prefs, refresh, onOpen = { routineId = it })
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
}
