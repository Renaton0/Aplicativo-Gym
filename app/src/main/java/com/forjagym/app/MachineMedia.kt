package com.forjagym.app

import android.content.Context
import android.net.Uri
import java.net.URLEncoder

/** Real photographs from Wikimedia Commons. Photos are references, not guaranteed to show the exact model. */
data class CommonsPhoto(val file: String, val author: String, val license: String) {
    val url: String get() = "https://commons.wikimedia.org/wiki/Special:FilePath/" +
        URLEncoder.encode(file.replace(' ', '_'), "UTF-8").replace("+", "%20") + "?width=600"
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

    /** Only associate with a fitting piece of equipment. No arbitrary photo used as exact identification. */
    fun forExercise(exercise: Exercise): CommonsPhoto? {
        val eq = exercise.equipment.lowercase()
        val name = exercise.name.lowercase()
        return when {
            "esteira" in eq -> treadmill
            "leg press" in eq -> legPress
            "extensora" in eq -> legExt
            "flexora" in eq -> legCurl
            "sissy squat" in eq -> squat
            "máquina smith" in eq -> smith
            "sóleo" in eq || "panturrilha" in eq -> calf
            "peck deck" in eq || "voador" in eq -> pec
            "chest press" in eq || "supino" in eq && ("máquina" in eq || "articulado" in eq) -> press
            "high row" in eq || "pulley unilateral" in eq -> highRow
            "pulley alto" in eq || "pulldown" in name -> lat
            "polia" in eq || "cabo" in eq || "pulley" in eq -> cable
            "halter" in eq -> dumbbell
            "máquina" in eq || "articulado" in eq || "graviton" in eq -> general
            else -> null
        }
    }
}

class ForjaPrefs(context: Context) {
    private val data = context.getSharedPreferences("forja_v2_preferences", Context.MODE_PRIVATE)
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
