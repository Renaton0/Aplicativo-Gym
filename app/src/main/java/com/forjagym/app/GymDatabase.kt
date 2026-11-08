package com.forjagym.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate

/** Banco de dados local: nenhuma conta, servidor ou permissão de Internet. */
data class Workout(val id: Long, val name: String, val days: Set<Int>)
data class WorkoutExercise(
    val id: Long, val workoutId: Long, val exerciseId: String,
    val sort: Int, val sets: Int, val reps: Int, val restSeconds: Int
)
data class WeightEntry(
    val id: Long, val exerciseId: String, val date: LocalDate,
    val weight: Double, val reps: Int
)

class GymDatabase(context: Context) : SQLiteOpenHelper(context, "forja_gym.db", null, 1) {
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE workouts (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT NOT NULL,
              days TEXT NOT NULL DEFAULT ''
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE workout_exercises (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              workout_id INTEGER NOT NULL REFERENCES workouts(id) ON DELETE CASCADE,
              exercise_id TEXT NOT NULL,
              sort INTEGER NOT NULL,
              sets INTEGER NOT NULL DEFAULT 3,
              reps INTEGER NOT NULL DEFAULT 12,
              rest_seconds INTEGER NOT NULL DEFAULT 60
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX idx_workout_exercises_workout ON workout_exercises(workout_id, sort)")
        db.execSQL("CREATE TABLE attendance (day TEXT PRIMARY KEY NOT NULL)")
        db.execSQL("""
            CREATE TABLE weight_entries (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              exercise_id TEXT NOT NULL,
              day TEXT NOT NULL,
              weight REAL NOT NULL CHECK(weight >= 0),
              reps INTEGER NOT NULL CHECK(reps > 0)
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX idx_weight_entries_exercise ON weight_entries(exercise_id, day, id)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Na versão inicial ainda não há migrações; não remover tabelas/dados.
    }

    fun workouts(): List<Workout> = readableDatabase.rawQuery(
        "SELECT id, name, days FROM workouts ORDER BY id", null
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) {
                add(Workout(cursor.getLong(0), cursor.getString(1),
                    cursor.getString(2).split(",").mapNotNull { it.toIntOrNull() }
                        .filter { it in 1..7 }.toSet()))
            }
        }
    }

    fun addWorkout(name: String): Long {
        val values = ContentValues().apply { put("name", name.trim()); put("days", "") }
        return writableDatabase.insertOrThrow("workouts", null, values)
    }

    fun renameWorkout(id: Long, name: String) {
        writableDatabase.update("workouts", ContentValues().apply { put("name", name.trim()) },
            "id=?", arrayOf(id.toString()))
    }

    fun updateDays(id: Long, days: Set<Int>) {
        val csv = days.filter { it in 1..7 }.sorted().joinToString(",")
        writableDatabase.update("workouts", ContentValues().apply { put("days", csv) },
            "id=?", arrayOf(id.toString()))
    }

    fun removeWorkout(id: Long) {
        writableDatabase.delete("workouts", "id=?", arrayOf(id.toString()))
    }

    fun workoutExercises(workoutId: Long): List<WorkoutExercise> = readableDatabase.rawQuery(
        """SELECT id, workout_id, exercise_id, sort, sets, reps, rest_seconds
           FROM workout_exercises WHERE workout_id=? ORDER BY sort, id""",
        arrayOf(workoutId.toString())
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) {
                add(WorkoutExercise(cursor.getLong(0), cursor.getLong(1), cursor.getString(2),
                    cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6)))
            }
        }
    }

    fun addExercise(workoutId: Long, exerciseId: String) {
        val order = workoutExercises(workoutId).maxOfOrNull { it.sort }?.plus(1) ?: 0
        val values = ContentValues().apply {
            put("workout_id", workoutId)
            put("exercise_id", exerciseId)
            put("sort", order)
            put("sets", 3)
            put("reps", 12)
            put("rest_seconds", 60)
        }
        writableDatabase.insertOrThrow("workout_exercises", null, values)
    }

    fun updateExercise(id: Long, sets: Int, reps: Int, restSeconds: Int) {
        val values = ContentValues().apply {
            put("sets", sets.coerceAtLeast(1))
            put("reps", reps.coerceAtLeast(1))
            put("rest_seconds", restSeconds.coerceAtLeast(0))
        }
        writableDatabase.update("workout_exercises", values, "id=?", arrayOf(id.toString()))
    }

    fun removeExercise(id: Long) {
        writableDatabase.delete("workout_exercises", "id=?", arrayOf(id.toString()))
    }

    fun attendance(): Set<LocalDate> = readableDatabase.rawQuery(
        "SELECT day FROM attendance", null
    ).use { cursor ->
        buildSet {
            while (cursor.moveToNext()) add(LocalDate.parse(cursor.getString(0)))
        }
    }

    /** true ao marcar; false ao desmarcar. */
    fun toggleAttendance(date: LocalDate): Boolean {
        val key = date.toString()
        return writableDatabase.run {
            val exists = rawQuery("SELECT 1 FROM attendance WHERE day=?", arrayOf(key))
                .use { it.moveToFirst() }
            if (exists) {
                delete("attendance", "day=?", arrayOf(key))
                false
            } else {
                insertOrThrow("attendance", null, ContentValues().apply { put("day", key) })
                true
            }
        }
    }

    fun loadEntries(exerciseId: String): List<WeightEntry> = readableDatabase.rawQuery(
        """SELECT id, exercise_id, day, weight, reps FROM weight_entries
           WHERE exercise_id=? ORDER BY day, id""", arrayOf(exerciseId)
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) add(WeightEntry(
                cursor.getLong(0), cursor.getString(1), LocalDate.parse(cursor.getString(2)),
                cursor.getDouble(3), cursor.getInt(4)
            ))
        }
    }

    fun exercisesWithLoads(): Set<String> = readableDatabase.rawQuery(
        "SELECT DISTINCT exercise_id FROM weight_entries", null
    ).use { cursor ->
        buildSet { while (cursor.moveToNext()) add(cursor.getString(0)) }
    }

    fun addLoad(exerciseId: String, weight: Double, reps: Int, day: LocalDate = LocalDate.now()) {
        require(weight >= 0 && weight.isFinite() && reps > 0)
        writableDatabase.insertOrThrow("weight_entries", null, ContentValues().apply {
            put("exercise_id", exerciseId)
            put("day", day.toString())
            put("weight", weight)
            put("reps", reps)
        })
    }

    fun removeLoad(id: Long) {
        writableDatabase.delete("weight_entries", "id=?", arrayOf(id.toString()))
    }
}
