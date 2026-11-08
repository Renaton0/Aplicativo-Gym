package com.forjagym.app

import android.content.Context
import android.net.Uri
import java.net.URLEncoder

/** Real photographs from Wikimedia Commons. Photos are references, not guaranteed to show the exact model. */
data class CommonsPhoto(val file: String, val author: String, val license: String) {
    val url: String get() = "https://commons.wikimedia.org/wiki/Special:FilePath/" +
        URLEncoder.encode(file.replace(' ', '_'), "UTF-8").replace("+", "%20") + "?width=700"
    val attributionUrl: String get() = "https://commons.wikimedia.org/wiki/File:" +
        URLEncoder.encode(file.replace(' ', '_'), "UTF-8").replace("+", "%20")
}

object MachineMedia {
    private val general = CommonsPhoto("Weight machine.webp", "Wikideas1", "CC0 1.0")
    private val press = CommonsPhoto("Girl doing chest press machine exercise.jpg", "Tyler Read / PTPioneer", "CC BY 2.0")
    private val pec = CommonsPhoto("Pec deck Fly.jpg", "Aliva Sahoo", "CC BY-SA 4.0")
    private val cable = CommonsPhoto("Multifunctional cable and row machines in a gym.jpg", "San Francisco Foghorn", "CC BY 2.0")
    private val lat = CommonsPhoto("Lat pulldown machine 20180112.jpg", "Santeri Viinamäki", "CC BY-SA 4.0")
    private val highRow = CommonsPhoto("Diverging Lat Pulldown Machine.jpg", "Teddyhtsai", "CC0 1.0")
    private val smith = CommonsPhoto("Smith machine.webp", "Wikideas1", "CC0 1.0")
    private val legPress = CommonsPhoto("Leg press machine.jpg", "Teemeah", "CC BY-SA 4.0")
    private val legExt = CommonsPhoto("LegExtensionMachineExercise.JPG", "GeorgeStepanek", "CC BY-SA 3.0")
    private val legCurl = CommonsPhoto("LyingLegCurlMachineExercise.JPG", "GeorgeStepanek", "CC BY-SA 3.0")
    private val squat = CommonsPhoto("Sissy squat machine.jpg", "Teemeah", "CC BY-SA 4.0")
    private val calf = CommonsPhoto("Seated calf machine.jpg", "Teemeah", "CC BY-SA 4.0")
    private val treadmill = CommonsPhoto("Treadmills.webp", "Wikideas1", "CC0 1.0")
    private val dumbbell = CommonsPhoto("Dumbells.jpg", "Keith McDuffee", "CC BY 2.0")
    private val bike = CommonsPhoto("Stationary bicycle.webp", "Wikideas1", "CC0 1.0")
    private val elliptical = CommonsPhoto("Elliptical trainer.jpg", "BrokenSphere", "CC BY-SA 3.0")
    private val stair = CommonsPhoto("Stair climber exercise machine.jpg", "Bait30", "CC BY-SA 4.0")
    private val rower = CommonsPhoto("Concept2 rowing machine.jpg", "B242", "CC BY-SA 3.0")
    private val skierg = CommonsPhoto("Concept2 SkiErg.jpg", "Mikael Häggström", "CC0 1.0")
    private val airBike = CommonsPhoto("Assault air bike.jpg", "Bait30", "CC BY-SA 4.0")
    private val preacher = CommonsPhoto("Preacher curl bench.jpg", "Raysonho", "CC0 1.0")
    private val dipMachine = CommonsPhoto("Seated dip machine.jpg", "Teemeah", "CC BY-SA 4.0")
    private val abduct = CommonsPhoto("Hip abduction machine.jpg", "Teemeah", "CC BY-SA 4.0")
    private val adduct = CommonsPhoto("Hip adduction machine.jpg", "Teemeah", "CC BY-SA 4.0")
    private val hipThrust = CommonsPhoto("Hip thrust machine.jpg", "Teemeah", "CC BY-SA 4.0")
    private val gluteKickback = CommonsPhoto("Glute kickback machine.jpg", "Teemeah", "CC BY-SA 4.0")
    private val crunch = CommonsPhoto("Abdominal crunch machine.jpg", "Teemeah", "CC BY-SA 4.0")
    private val romanChair = CommonsPhoto("Roman chair exercise equipment.jpg", "TobiasToMar", "CC BY-SA 4.0")
    private val battleRope = CommonsPhoto("Battle ropes exercise.jpg", "M.O. Stevens", "CC BY 3.0")
    private val kettlebell = CommonsPhoto("Kettlebell.jpg", "Christian Jansky", "CC BY-SA 2.5")
    private val sled = CommonsPhoto("Pushing sled exercise.jpg", "M.O. Stevens", "CC BY 3.0")
    private val trapBar = CommonsPhoto("Trap bar deadlift.jpg", "M.O. Stevens", "CC BY 3.0")
    private val bench = CommonsPhoto("Bench press gym.jpg", "Mikael Häggström", "CC0 1.0")
    private val pullup = CommonsPhoto("Pull-up bar gym.jpg", "M.O. Stevens", "CC BY 3.0")
    private val ropeJump = CommonsPhoto("Jump rope training.jpg", "N509FZ", "CC BY-SA 4.0")

    /**
     * We prefer a representative photo for each exercise. When no close reference is available,
     * return null rather than showing a misleading machine.
     */
    fun forExercise(exercise: Exercise): CommonsPhoto? {
        val eq = exercise.equipment.lowercase()
        val name = exercise.name.lowercase()
        return when {
            "chest press" in eq || "supino" in name && ("máquina" in eq || "articulado" in eq || "press" in eq) -> press
            "peck deck" in eq || "voador" in eq || "crucifixo" in name && "polia" !in eq && "halter" !in eq -> pec
            "pulley alto" in eq || "pulldown" in name -> lat
            "high row" in eq || "remada alta" in name -> highRow
            "polia" in eq || "crossover" in name || "cabo" in eq || "corda" in eq -> cable
            "máquina smith" in eq -> smith
            "leg press" in eq -> legPress
            "extensora" in eq -> legExt
            "flexora" in eq -> legCurl
            "hack squat" in eq || "pendulum" in eq || "v-squat" in eq || "belt squat" in eq || "agachamento" in name && "máquina" in eq -> squat
            "panturrilha" in name || "sóleo" in eq -> calf
            "esteira" in eq -> treadmill
            "bike" in eq && "air" !in eq -> bike
            "elíptico" in eq || "transport" in eq -> elliptical
            "stair" in eq || "escada" in name -> stair
            "remo" in eq -> rower
            "skierg" in eq || "esqui" in eq -> skierg
            "air bike" in eq -> airBike
            "scott" in eq || "preacher" in eq -> preacher
            "dip" in eq || "mergulho" in name -> dipMachine
            "abdutora" in eq -> abduct
            "adutora" in eq -> adduct
            "hip thrust" in eq || "elevação pélvica" in name -> hipThrust
            "glúteo" in eq || "coice" in name || "extensão do quadril" in eq -> gluteKickback
            "crunch" in eq || "abdominal máquina" in name -> crunch
            "cadeira romana" in eq || "banco romano" in eq || "hiperextensão" in name -> romanChair
            "battle ropes" in eq || "corda naval" in name -> battleRope
            "kettlebell" in eq || "kettlebell" in name -> kettlebell
            "trenó" in eq -> sled
            "trap bar" in eq -> trapBar
            "barra fixa" in eq || "graviton" in eq -> pullup
            "barra" in eq || "banco" in eq -> bench
            "halter" in eq || "anilha" in eq -> dumbbell
            "corda" == eq.lowercase() || "pular corda" in name -> ropeJump
            "máquina" in eq || "articulado" in eq -> general
            else -> null
        }
    }
}

class ForjaPrefs(context: Context) {
    private val data = context.getSharedPreferences("forja_v21_preferences", Context.MODE_PRIVATE)
    var weeklyGoal: Int
        get() = data.getInt("goal_week", 4).coerceIn(1, 7)
        set(value) { data.edit().putInt("goal_week", value.coerceIn(1, 7)).apply() }
    var monthlyGoal: Int
        get() = data.getInt("goal_month", 16).coerceIn(1, 31)
        set(value) { data.edit().putInt("goal_month", value.coerceIn(1, 31)).apply() }

    fun customPhoto(exerciseId: String): String? = data.getString("custom_photo_$exerciseId", null)
    fun savePhoto(exerciseId: String, uri: Uri?) {
        data.edit().apply {
            if (uri == null) remove("custom_photo_$exerciseId") else putString("custom_photo_$exerciseId", uri.toString())
        }.apply()
    }

    fun machineSeat(exerciseId: String): String = data.getString("machine_seat_$exerciseId", "") ?: ""
    fun machineAdjust(exerciseId: String): String = data.getString("machine_adjust_$exerciseId", "") ?: ""
    fun machineNote(exerciseId: String): String = data.getString("machine_note_$exerciseId", "") ?: ""

    fun saveMachineProfile(exerciseId: String, seat: String, adjust: String, note: String) {
        data.edit()
            .putString("machine_seat_$exerciseId", seat.trim())
            .putString("machine_adjust_$exerciseId", adjust.trim())
            .putString("machine_note_$exerciseId", note.trim())
            .apply()
    }
}

/** Short, cautious instructions. Not an individualized exercise prescription. */
object ExerciseGuide {
    fun muscles(e: Exercise): String = when(e.group) {
        "Peito" -> "Peitoral maior; tríceps e deltoide anterior como auxiliares"
        "Costas" -> "Dorsal, romboides e trapézio; bíceps como auxiliar"
        "Ombros" -> "Deltoides e estabilizadores escapulares"
        "Bíceps" -> "Bíceps braquial e braquial"
        "Tríceps" -> "Tríceps braquial"
        "Quadríceps" -> "Quadríceps; glúteos em movimentos compostos"
        "Posteriores" -> "Isquiotibiais; glúteos em alguns movimentos"
        "Glúteos" -> "Glúteo máximo e médio, conforme a execução"
        "Panturrilhas" -> "Gastrocnêmio e sóleo"
        "Abdômen" -> "Reto abdominal, oblíquos e estabilizadores do tronco"
        "Antebraços" -> "Flexores e extensores do antebraço"
        "Cardio" -> "Sistema cardiorrespiratório; membros inferiores ou superiores conforme aparelho"
        else -> "Múltiplos grupos musculares"
    }

    fun steps(e: Exercise): List<String> {
        val n = e.name.lowercase()
        return when {
            "leg press" in n -> listOf("Ajuste encosto e posição dos pés na plataforma.", "Flexione os joelhos em amplitude confortável, sem perder contato lombar.", "Empurre controladamente sem travar os joelhos ao final.")
            "extensora" in n -> listOf("Ajuste o eixo da máquina à altura dos joelhos.", "Posicione a almofada sobre a parte inferior das pernas.", "Estenda e retorne lentamente, sem impulso ou hiperextensão.")
            "flexora" in n -> listOf("Ajuste o aparelho para alinhar o joelho ao eixo.", "Mantenha quadril estabilizado no banco.", "Flexione os joelhos e retorne com controle.")
            "supino" in n || "chest press" in n -> listOf("Ajuste banco e pegada para manter os ombros estáveis.", "Empurre à frente ou para cima sem perder a postura.", "Retorne devagar, respeitando amplitude confortável.")
            "crucifixo" in n || "crossover" in n -> listOf("Regule altura de banco ou polias.", "Mantenha cotovelos levemente flexionados.", "Aproxime os braços controlando a volta, sem forçar os ombros.")
            "puxada" in n || "pulldown" in n || "barra fixa" in n -> listOf("Ajuste banco, apoio e pegada quando houver.", "Inicie o movimento estabilizando as escápulas.", "Puxe sem balançar o tronco e retorne com controle.")
            "remada" in n -> listOf("Posicione pés e coluna com estabilidade.", "Puxe os pegadores em direção ao tronco sem impulso.", "Retorne lentamente, controlando as escápulas.")
            "agachamento" in n || "hack squat" in n -> listOf("Ajuste a base e apoios para sua mobilidade.", "Desça controladamente mantendo joelhos alinhados aos pés.", "Suba com apoio estável, sem perder a postura.")
            "elevação lateral" in n -> listOf("Mantenha tronco estável e cotovelos suavemente flexionados.", "Eleve os braços lateralmente sem encolher os ombros.", "Desça lentamente, sem balanço.")
            "desenvolvimento" in n -> listOf("Ajuste banco/pegada e estabilize o tronco.", "Empurre para cima sem exagerar a extensão lombar.", "Retorne de forma controlada.")
            "rosca" in n -> listOf("Mantenha cotovelos estáveis próximos ao tronco.", "Flexione os braços sem movimentar o corpo para ajudar.", "Retorne lentamente até uma amplitude confortável.")
            "tríceps" in n -> listOf("Ajuste a pegada e estabilize os cotovelos.", "Estenda os braços sem usar o impulso do tronco.", "Volte devagar, mantendo controle.")
            "panturrilha" in n -> listOf("Posicione a parte anterior dos pés sobre o apoio.", "Eleve os calcanhares de modo controlado.", "Desça sem forçar a articulação, respeitando o alcance.")
            "esteira" in n || "bicicleta" in n || "elíptico" in n -> listOf("Ajuste o aparelho ao seu tamanho.", "Inicie em ritmo confortável e aumente gradualmente.", "Reduza a intensidade antes de encerrar a atividade.")
            else -> listOf("Ajuste o equipamento à sua altura e mobilidade.", "Faça o movimento com postura estável e sem compensações.", "Controle a carga e o retorno; interrompa se houver dor.")
        }
    }
}

object ExerciseSupport {
    fun alternatives(exercise: Exercise): List<Exercise> {
        val sameGroup = Catalog.all.filter { it.group == exercise.group && it.id != exercise.id }
        val ranked = sameGroup.sortedByDescending {
            similarityScore(exercise, it)
        }
        return ranked.take(4)
    }

    private fun similarityScore(a: Exercise, b: Exercise): Int {
        var score = 0
        if (a.group == b.group) score += 5
        val aName = a.name.lowercase()
        val bName = b.name.lowercase()
        val aEq = a.equipment.lowercase()
        val bEq = b.equipment.lowercase()
        if (aEq == bEq) score += 4
        listOf("supino", "puxada", "remada", "rosca", "tríceps", "agachamento", "panturrilha", "abdominal", "extensora", "flexora", "elevação", "terra", "afundo", "coice").forEach {
            if (it in aName && it in bName) score += 3
        }
        if (aEq.substringBefore(" ") == bEq.substringBefore(" ")) score += 1
        return score
    }
}
