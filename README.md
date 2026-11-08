# FORJA Gym 1.0.0 — Android / Kotlin

Aplicativo de academia **nativo e offline**, com interface em português, desenvolvido com Kotlin, Jetpack Compose, Material 3 e SQLite (sem login, sem backend e sem anúncios).

## Funcionalidades

1. **Início:** treino programado para o dia, frequência da semana e botão para marcar/desmarcar a presença de hoje.
2. **Rotinas:** criar, renomear e excluir rotinas; selecionar dias da semana; adicionar/remover exercícios; configurar séries, repetições e descanso.
3. **Máquinas:** catálogo offline com **151 exercícios e variações**, agrupados em 13 grupos, incluindo aparelhos guiados, polias, livres e cardio. Busca por nome e equipamento; adicionar diretamente a uma rotina.
4. **Presença:** duas visualizações (Marcar dias e Mapa de consistência); calendário mês a mês com bolinhas **pretas** para datas não registradas e **verdes** para presença; tocar em datas de hoje ou passadas para marcar/desmarcar. Total mensal, semanal e sequência de dias.
5. **Cargas:** selecionar um exercício, registrar carga em kg e repetições, visualizar recorde, última carga, evolução (gráfico dos 30 registros mais recentes), histórico completo e excluir um registro.

Todos os dados permanecem no banco `forja_gym.db` no armazenamento privado do aplicativo. Uma reinstalação pode apagar os dados, conforme as configurações de backup do Android. O catálogo não garante cobrir todas as máquinas de todas as academias.

## Abrir no Android Studio

1. Instale uma versão atual do **Android Studio** e o **Android SDK 36**.
2. Extraia o ZIP deste projeto e abra a pasta `ForjaGym` em **File > Open**.
3. Conecte um dispositivo Android com depuração USB ou escolha um emulador (**Android 8.0/API 26 ou superior**).
4. Aguarde a sincronização Gradle, depois clique em **Run ▶**. Os scripts `gradlew` e `gradlew.bat` também funcionam e baixam o JAR oficial do Gradle Wrapper na primeira execução, conferindo seu SHA-256.
5. Para gerar um APK: **Build > Build App Bundle(s) / APK(s) > Build APK(s)**. O arquivo gerado normalmente ficará em `app/build/outputs/apk/debug/app-debug.apk`.

O Android Studio precisa de acesso à Internet para baixar Gradle, Android Gradle Plugin e dependências na primeira sincronização. **O ZIP contém o código-fonte; o APK não foi compilado neste ambiente**, pois não há Android SDK/Gradle instalados aqui. Use JDK 17 ou superior compatível com o Android Gradle Plugin.

## Estrutura

- `app/src/main/java/com/forjagym/app/Catalog.kt` — 151 opções de exercícios, grupos e equipamentos.
- `GymDatabase.kt` — persistência SQLite e operações CRUD.
- `Screens.kt` — todas as telas, diálogos, calendário e gráfico.
- `MainActivity.kt` — navegação e estado do aplicativo.
- `Theme.kt` — paleta verde/preto, estilo escuro.

## Limitações da versão 1.0.0

- Ainda não há sincronização entre celulares, exportação de backup, cronômetro, vídeos, fotos de máquinas ou ajustes para cargas diferentes por série.
- O registro de carga usa a data corrente; a presença permite datas anteriores.
- O catálogo é amplo, mas não exaustivo, e deve ser revisado conforme os aparelhos da academia frequentada.
- Não substitui orientação de profissional de educação física. O app organiza dados, não prescreve treinos.

## Dependências

- Android Gradle Plugin 8.11.1 / Gradle 8.14
- Kotlin 2.2.20 + plugin Compose 2.2.20
- Jetpack Compose BOM 2025.08.01
- AndroidX Activity Compose 1.10.1
- SQLiteOpenHelper do Android

Projeto de código-fonte para personalização e evolução.

## Gerar APK SEM Android Studio (pela nuvem)

Este projeto agora inclui `.github/workflows/gerar-apk.yml`, que permite compilar
um APK debug instalável usando GitHub Actions. Depois de enviar o projeto inteiro
para um repositório GitHub, abra **Actions > Gerar APK Android** e baixe o artefato
**ForjaGym-APK-instalavel**, extraia-o e instale `app-debug.apk` no celular.
Leia `COMO-GERAR-APK-SEM-ANDROID-STUDIO.txt` para o passo a passo completo.

**Não há um APK compilado neste ZIP:** o ambiente de criação não dispõe do SDK
Android, do Gradle nem de acesso para baixar essas ferramentas. A primeira execução
do workflow também pode revelar erros de compilação ainda não identificados.
