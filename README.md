# FORJA Gym 2.1.0 — Android nativo em Kotlin

Aplicativo pessoal de musculação com interface grafite/verde, sem conta e sem serviço de nuvem obrigatório. **Atualização do projeto FORJA Gym 1.0.0**, preservando o mesmo `applicationId = com.forjagym.app` e a mesma estrutura de banco SQLite.

## O que há na 2.1


## Novidades da 2.1

- **Modo treino ativo** com séries marcáveis, descanso e resumo.
- **Minha academia**: assento, ajuste, observação e foto personalizada por aparelho.
- **Substituições inteligentes** dentro da ficha do exercício.
- **Registro de cargas com recorde pessoal**, volume total e 1RM estimado.
- **Fotos também no seletor de exercícios das rotinas**.
- **Menos títulos fixos no topo**, interface mais limpa.

- **Hoje** — resumo do treino, presença em um toque, meta da semana/mês e sequência de presença.
- **Rotinas** — criar, renomear, remover, selecionar dias da semana, configurar séries, repetições e descansos e consultar exercícios.
- **Aparelhos** — 151 exercícios/variações em 13 grupos, busca, ficha com músculos trabalhados, equipamento e instruções básicas. Fotografias reais de referência do Wikimedia Commons em categorias compatíveis, com indicação de autoria/licença/fonte. Em exercícios sem imagem específica, exibe «Sem foto» em vez de mostrar aparelho errado. **Também é possível escolher uma foto tirada no próprio celular para qualquer exercício**, mantida com permissão de acesso persistente.
- **Disciplina** — bolinhas pretas e verdes no calendário mensal, registro de presença independente do treino, visão dos 12 meses, sequência atual, meta semanal de 1–7 dias e meta mensal de 1–31 dias, editáveis.
- **Cargas** — registro rápido de kg e repetições, preenche com a última carga, ajustes de ±2,5 kg, recorde, gráfico e histórico completo.
- **Visual** — paleta grafite, bordas discretas, tipografia mais contida, cartões utilitários e espaçamento consistente.

## Fotos e uso offline

Os **dados** de rotina, carga, presença e metas são locais e utilizáveis sem internet. As **fotos de referência** são carregadas pela internet a partir do Wikimedia Commons, podendo ser reutilizadas a partir do cache do Android/Coil; quando indisponíveis, há um placeholder. Fotos adicionadas pelo usuário vêm do armazenamento local do celular e dependem de o arquivo permanecer acessível.

Existem fotografias para **tipos representativos de aparelhos**, não uma foto única para cada um dos 151 exercícios. A seleção é uma **referência visual**, não garante a mesma marca/modelo da academia. Todos os arquivos licenciados e os créditos estão em [`CREDITOS-FOTOS.md`](CREDITOS-FOTOS.md); no aplicativo, toque nos créditos da ficha para visitar a fonte. Licenças livres exigem atribuição — consulte cada fonte antes de redistribuir ou modificar as imagens.

## Atualizar seu repositório no GitHub (sem Android Studio)

1. Extraia este ZIP. Abra a pasta `ForjaGym`.
2. No seu computador, abra a pasta local que já tem `.git` e está conectada ao repositório `Renaton0/Aplicativo-Gym`.
3. **Copie todo o conteúdo interno de `ForjaGym` para essa pasta existente**, substituindo os arquivos iguais. Inclua a pasta oculta `.github`. **Não apague sua pasta `.git`**.
4. Abra o Terminal nessa pasta e execute:

   ```powershell
   git add -A
   git commit -m "FORJA Gym 2.1.0"
   git push origin main
   ```

5. Acesse: https://github.com/Renaton0/Aplicativo-Gym/actions
6. Na execução **Gerar APK Android**, espere o sinal verde e abra **Artifacts → ForjaGym-APK-instalavel**. Extraia o ZIP do GitHub e instale `app-debug.apk` no celular.

O workflow inclui a correção `packages: platform-tools` para evitar o erro antigo de `sdkmanager tools`.

**Importante sobre atualização do APK:** `versionCode` mudou de 1 para 2; o app mantém o mesmo identificador/banco, mas APKs de teste gerados em execuções diferentes do GitHub Actions podem ter **assinaturas de depuração distintas**. Nessa situação o Android pode negar a instalação por cima, exigindo desinstalar a versão anterior, o que **apaga os dados locais**. Não desinstale a 1.0 se precisar manter dados e ainda não tiver um método de backup. Para atualização consistente daqui em diante, configure uma assinatura persistente/privada em um workflow futuro. Não publique um keystore de produção no repositório.

## Tecnologia / arquivos

- Android, Kotlin 2.2.20, Jetpack Compose / Material 3, SQLiteOpenHelper, Coil 2.7.0.
- `MainActivity.kt` — navegação 2.1.
- `LibraryV2.kt` / `MachineMedia.kt` — catálogo visual, imagens, instruções e foto pessoal.
- `DisciplineV2.kt` — calendário mensal, visão anual, metas e sequência.
- `LoadsV2.kt` — registro rápido, gráficos e histórico.
- `RoutinesV2.kt` — CRUD de rotinas e exercícios.
- `DashboardV2.kt`, `ProfessionalUI.kt`, `Theme.kt` — interface e painel inicial.
- `GymDatabase.kt`, `Catalog.kt` — banco e catálogo original preservados.

## Limitações

- Não há sincronização na nuvem nem backup/exportação de dados por enquanto.
- Não há vídeo demonstrativo, treinador virtual ou orientação individualizada.
- O registro rápido representa uma carga/repetição por lançamento; não há séries individuais.
- As instruções são gerais e devem ser ajustadas com profissional habilitado.
- A **compilação final da versão 2.1 não foi executada neste ambiente**. Faça a execução do GitHub Actions para verificar o APK.
