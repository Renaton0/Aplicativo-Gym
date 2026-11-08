package com.forjagym.app

/** Catálogo offline. Cada variação é um exercício; equipamentos podem se repetir. */
data class Exercise(val id: String, val name: String, val group: String, val equipment: String)

object Catalog {
    val groups = listOf(
        "Peito", "Costas", "Ombros", "Bíceps", "Tríceps", "Quadríceps",
        "Posteriores", "Glúteos", "Panturrilhas", "Abdômen", "Antebraços",
        "Cardio", "Corpo inteiro"
    )

    private fun group(group: String, vararg moves: Pair<String, String>): List<Exercise> =
        moves.map { (name, equipment) -> Exercise("$group::$name", name, group, equipment) }

    val all: List<Exercise> = buildList {
        addAll(group("Peito",
            "Supino horizontal convergente" to "Chest press articulado",
            "Supino inclinado articulado" to "Máquina de supino inclinado",
            "Supino declinado articulado" to "Máquina de supino declinado",
            "Supino vertical sentado" to "Chest press vertical",
            "Supino horizontal seletorizado" to "Chest press com placas",
            "Crucifixo sentado" to "Peck deck / voador",
            "Crossover alto" to "Polia dupla (cross over)",
            "Crossover médio" to "Polia dupla (cross over)",
            "Crossover baixo" to "Polia dupla (cross over)",
            "Crucifixo unilateral na polia" to "Polia ajustável",
            "Supino reto no Smith" to "Máquina Smith",
            "Supino inclinado no Smith" to "Máquina Smith",
            "Supino reto com barra" to "Banco reto + barra",
            "Supino inclinado com barra" to "Banco inclinado + barra",
            "Supino reto com halteres" to "Banco + halteres",
            "Crucifixo com halteres" to "Banco + halteres",
            "Flexão de braços" to "Peso corporal"
        ))
        addAll(group("Costas",
            "Puxada frontal aberta" to "Pulley alto",
            "Puxada frontal neutra" to "Pulley alto + triângulo",
            "Puxada frontal supinada" to "Pulley alto",
            "Puxada unilateral" to "Pulley unilateral articulado",
            "Puxada articulada convergente" to "High row articulado",
            "Remada baixa fechada" to "Polia baixa + triângulo",
            "Remada baixa aberta" to "Polia baixa + barra",
            "Remada sentada articulada" to "Máquina de remada articulada",
            "Remada unilateral máquina" to "Máquina de remada unilateral",
            "Remada cavalinho apoiada" to "Máquina T-bar row",
            "Remada alta para dorsais" to "High row",
            "Remada baixa articulada" to "Low row",
            "Pullover sentado" to "Máquina pullover",
            "Pulldown com braços estendidos" to "Polia alta",
            "Barra fixa assistida" to "Graviton",
            "Barra fixa livre" to "Barra fixa",
            "Remada curvada com barra" to "Barra livre",
            "Remada unilateral com halter" to "Banco + halter",
            "Levantamento terra" to "Barra e anilhas",
            "Hiperextensão lombar" to "Banco romano"
        ))
        addAll(group("Ombros",
            "Desenvolvimento máquina" to "Shoulder press",
            "Desenvolvimento articulado" to "Máquina de ombro articulada",
            "Desenvolvimento no Smith" to "Máquina Smith",
            "Desenvolvimento com halteres" to "Banco + halteres",
            "Elevação lateral máquina" to "Máquina de elevação lateral",
            "Elevação lateral na polia" to "Polia baixa",
            "Elevação lateral com halteres" to "Halteres",
            "Elevação frontal na polia" to "Polia baixa",
            "Elevação frontal com anilha" to "Anilha",
            "Crucifixo inverso máquina" to "Peck deck inverso",
            "Face pull" to "Polia alta + corda",
            "Remada alta no cabo" to "Polia baixa",
            "Encolhimento de ombros" to "Halteres / barra"
        ))
        addAll(group("Bíceps",
            "Rosca Scott máquina" to "Máquina de bíceps Scott",
            "Rosca Scott barra" to "Banco Scott + barra W",
            "Rosca direta na polia" to "Polia baixa + barra",
            "Rosca alternada no cabo" to "Polia dupla",
            "Rosca direta com barra" to "Barra W ou reta",
            "Rosca alternada" to "Halteres",
            "Rosca martelo" to "Halteres",
            "Rosca concentrada" to "Halter",
            "Rosca inclinada" to "Banco inclinado + halteres",
            "Rosca bayesian" to "Polia baixa"
        ))
        addAll(group("Tríceps",
            "Tríceps pulley barra" to "Polia alta + barra",
            "Tríceps pulley corda" to "Polia alta + corda",
            "Tríceps unilateral na polia" to "Polia alta",
            "Tríceps francês na polia" to "Polia alta",
            "Tríceps testa na polia" to "Polia baixa",
            "Mergulho assistido" to "Graviton",
            "Mergulho na máquina" to "Máquina dip sentado",
            "Paralelas livres" to "Barras paralelas",
            "Tríceps testa barra W" to "Banco + barra W",
            "Tríceps francês halter" to "Halter",
            "Supino fechado" to "Barra e banco"
        ))
        addAll(group("Quadríceps",
            "Cadeira extensora bilateral" to "Cadeira extensora",
            "Cadeira extensora unilateral" to "Cadeira extensora",
            "Leg press 45°" to "Leg press 45°",
            "Leg press horizontal" to "Leg press horizontal",
            "Leg press vertical" to "Leg press vertical",
            "Hack squat" to "Máquina hack squat",
            "Agachamento pendular" to "Máquina pendulum squat",
            "Agachamento no Smith" to "Máquina Smith",
            "Agachamento na máquina V" to "V-squat",
            "Agachamento no belt squat" to "Belt squat",
            "Agachamento livre" to "Rack + barra",
            "Agachamento goblet" to "Halter / kettlebell",
            "Afundo no Smith" to "Máquina Smith",
            "Passada com halteres" to "Halteres"
        ))
        addAll(group("Posteriores",
            "Mesa flexora" to "Mesa flexora deitada",
            "Cadeira flexora" to "Cadeira flexora sentada",
            "Flexora em pé" to "Flexora vertical",
            "Flexora unilateral" to "Flexora unilateral",
            "Stiff com barra" to "Barra + anilhas",
            "Stiff com halteres" to "Halteres",
            "Terra romeno" to "Barra + anilhas",
            "Glute ham raise" to "Máquina GHD",
            "Nordic curl assistido" to "Apoio para pés",
            "Bom dia com barra" to "Barra livre"
        ))
        addAll(group("Glúteos",
            "Cadeira abdutora" to "Máquina abdutora",
            "Cadeira adutora" to "Máquina adutora",
            "Glúteo no coice máquina" to "Máquina glúteo 4 apoios",
            "Coice no cabo" to "Polia baixa + tornozeleira",
            "Abdução em pé na polia" to "Polia baixa + tornozeleira",
            "Elevação pélvica máquina" to "Hip thrust machine",
            "Elevação pélvica com barra" to "Banco + barra",
            "Ponte de glúteos" to "Colchonete",
            "Passada búlgara" to "Banco + halteres",
            "Step up" to "Caixa / banco",
            "Agachamento sumô" to "Halter / barra",
            "Extensão de quadril máquina" to "Máquina de extensão do quadril"
        ))
        addAll(group("Panturrilhas",
            "Panturrilha sentada" to "Máquina sóleo sentado",
            "Panturrilha em pé máquina" to "Máquina panturrilha em pé",
            "Panturrilha no leg press 45°" to "Leg press 45°",
            "Panturrilha no leg horizontal" to "Leg press horizontal",
            "Panturrilha no Smith" to "Máquina Smith",
            "Panturrilha em degrau" to "Degrau",
            "Panturrilha unilateral" to "Degrau + halter"
        ))
        addAll(group("Abdômen",
            "Abdominal máquina sentado" to "Máquina de crunch",
            "Abdominal na polia alta" to "Polia alta + corda",
            "Elevação de pernas" to "Barra fixa",
            "Elevação de joelhos assistida" to "Cadeira romana",
            "Abdominal no banco declinado" to "Banco declinado",
            "Abdominal na bola" to "Bola suíça",
            "Prancha isométrica" to "Colchonete",
            "Prancha lateral" to "Colchonete",
            "Rotação de tronco máquina" to "Máquina rotatória de tronco",
            "Woodchopper" to "Polia ajustável",
            "Ab wheel" to "Roda abdominal",
            "Abdominal bicicleta" to "Colchonete"
        ))
        addAll(group("Antebraços",
            "Flexão de punho" to "Barra ou halter",
            "Extensão de punho" to "Barra ou halter",
            "Rosca inversa" to "Barra W / polia baixa",
            "Farmer walk" to "Halteres / trap bar",
            "Hand grip" to "Fortalecedor de pegada",
            "Suspensão na barra" to "Barra fixa"
        ))
        addAll(group("Cardio",
            "Caminhada na esteira" to "Esteira elétrica",
            "Corrida na esteira" to "Esteira elétrica",
            "Bicicleta ergométrica vertical" to "Bike vertical",
            "Bicicleta ergométrica horizontal" to "Bike reclinada",
            "Bicicleta de spinning" to "Bike spinning",
            "Elíptico" to "Transport / elíptico",
            "Escada ergométrica" to "Stair climber",
            "Remo ergométrico" to "Remo indoor",
            "SkiErg" to "Simulador de esqui",
            "Air bike" to "Bicicleta de resistência a ar",
            "Simulador de caminhada" to "Air walker",
            "Corda naval" to "Battle ropes",
            "Pular corda" to "Corda"
        ))
        addAll(group("Corpo inteiro",
            "Burpee" to "Peso corporal",
            "Kettlebell swing" to "Kettlebell",
            "Agachamento com desenvolvimento" to "Halteres / barra",
            "Levantamento terra com trap bar" to "Trap bar",
            "Puxada de trenó" to "Trenó de treino",
            "Empurrar trenó" to "Trenó de treino"
        ))
    }

    private val index: Map<String, Exercise> = all.associateBy { it.id }
    fun get(id: String): Exercise? = index[id]
}
