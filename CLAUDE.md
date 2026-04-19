# CLAUDE.md — Kanoas (AI Governance Document)

> **Este documento é a "coleira" da IA.** Ele define regras, padrões e restrições
> que devem ser seguidos em TODAS as interações de geração de código.
> Atualizar este documento sempre que um padrão novo for estabelecido ou um erro
> recorrente for identificado.

---

## 📋 Visão do Projeto

**Kanoas** é um aplicativo mobile multi-plataforma (Android + iOS futuro)
construído com **Kotlin Multiplatform** e **Compose Multiplatform**.

O projeto segue a filosofia do **Desafio de 7 Dias do Akita Dev**:
- AI como par de *pair programming*, nunca como gerador autônomo
- TDD obrigatório
- Refactoring contínuo
- Commits pequenos e granulares

---

## 🏗️ Arquitetura

### Clean Architecture + MVI (Model-View-Intent)

```
┌──────────────────────────────────────────────────┐
│                  composeApp/                      │
│  ┌─────────────────────────────────────────────┐  │
│  │  Presentation (UI + ViewModel)              │  │
│  │  commonMain/                                │  │
│  │    ├── kanban/    → Kanban Screens + VMs    │  │
│  │    ├── financial/ → Financial Screens + VMs │  │
│  │    └── core/ui/   → Theme, Nav, Components  │  │
│  │  androidMain/ → Activity, Application       │  │
│  └──────────────────┬──────────────────────────┘  │
│                     │ depends on                  │
├─────────────────────┼────────────────────────────-┤
│                  shared/                          │
│  ┌──────────────────┼──────────────────────────┐  │
│  │  kanban/                                    │  │
│  │    ├── domain/ → Models, UseCases, Repo IF  │  │
│  │    └── data/   → Repo Impl, DataSources     │  │
│  │  financial/                                 │  │
│  │    ├── domain/                              │  │
│  │    └── data/                                │  │
│  │  core/                                      │  │
│  │    ├── network/  → Ktor + Supabase Client   │  │
│  │    ├── database/ → SQLDelight               │  │
│  │    └── di/       → Koin modules             │  │
│  │  commonMain/ → Pure Kotlin (sem Android)    │  │
│  └─────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────┘
```

### MVI — Contrato Base

Cada ViewModel deve implementar este contrato:

```kotlin
// shared/core/mvi/MviViewModel.kt
interface MviViewModel<STATE : UiState, INTENT : UiIntent, EFFECT : UiEffect> {
    val state: StateFlow<STATE>
    val effects: SharedFlow<EFFECT>
    fun handleIntent(intent: INTENT)
}
```

O fluxo é sempre unidirecional:
```
User Action → Intent → ViewModel → State (UI re-renders)
                                 ↘ Effect (navigation, toast, etc.)
```

### Camadas e Responsabilidades

| Camada | Módulo | Conteúdo |
|--------|--------|----------|
| **Presentation** | `composeApp/commonMain/{feature}` | Screens, ViewModels, State, Intent, Effect |
| **Domain** | `shared/{feature}/domain` | Models, Use Cases, Repository interfaces |
| **Data** | `shared/{feature}/data` | Repository implementations, Data Sources, DTOs |
| **Core** | `shared/core` | Ktor, SQLDelight, Koin modules, MVI base contracts |
| **Platform** | `composeApp/androidMain` | Activity, Application, platform DI |

---

## 📐 Padrões Obrigatórios

### Linguagem e Frameworks
- **Kotlin** — única linguagem permitida
- **Coroutines + Flow** — para operações assíncronas
- **StateFlow** — para estado reativo (MVI State)
- **SharedFlow** — para efeitos colaterais one-shot (navigation, toasts)
- **Koin** — injeção de dependência (NÃO usar Hilt/Dagger)
- **Material 3** — design system
- **Ktor Client** — chamadas HTTP e WebSocket (KMP-native)
- **Supabase Kotlin SDK** — backend (auth, database, real-time, storage)
- **SQLDelight** — persistência local e cache offline
- **Kotlinx.serialization** — serialização JSON

### Convenções de Nomenclatura (MVI)

| Tipo | Padrão | Exemplo |
|------|--------|---------|
| Screen (Composable) | `{Feature}Screen` | `KanbanScreen` |
| ViewModel | `{Feature}ViewModel` | `KanbanViewModel` |
| MVI State | `{Feature}State` | `KanbanState` |
| MVI Intent | `{Feature}Intent` | `KanbanIntent` |
| MVI Effect | `{Feature}Effect` | `KanbanEffect` |
| Use Case | `{Ação}{Entidade}UseCase` | `GetBoardTasksUseCase` |
| Repository Interface | `{Entidade}Repository` | `TaskRepository` |
| Repository Impl | `{Entidade}RepositoryImpl` | `TaskRepositoryImpl` |
| DTO (Data Transfer Object) | `{Entidade}Dto` | `TaskDto` |
| Koin Module | `{escopo}Module` | `kanbanModule`, `networkModule` |

### Estrutura de Feature (MVI)

Cada feature deve seguir este padrão:
```
{feature}/
├── presentation/
│   ├── {Feature}Screen.kt        # @Composable — só renderiza state, dispara intents
│   ├── {Feature}ViewModel.kt     # MviViewModel<State, Intent, Effect>
│   ├── {Feature}State.kt         # data class imutável — snapshot completo da UI
│   ├── {Feature}Intent.kt        # sealed interface — tudo que o usuário pode fazer
│   └── {Feature}Effect.kt        # sealed interface — efeitos one-shot (nav, toast)
├── domain/
│   ├── model/                    # entidades puras do domínio
│   ├── usecase/                  # um arquivo por use case
│   └── repository/               # interfaces (contratos)
└── data/
    ├── repository/               # implementações dos contratos
    ├── datasource/               # Supabase remote, SQLDelight local
    └── dto/                      # mapeamento de/para API
```

---

## 🚫 Restrições

1. **NUNCA** colocar lógica de negócio em Composables
2. **NUNCA** usar `var` para estado — sempre `StateFlow` (state) ou `SharedFlow` (effects)
3. **NUNCA** importar dependências Android no módulo `shared/`
4. **NUNCA** criar dependências cíclicas entre módulos
5. **NUNCA** fazer chamadas de rede diretamente no ViewModel — sempre via Use Case
6. **NUNCA** usar `GlobalScope` — sempre `viewModelScope` ou scope injetado
7. **NUNCA** hardcodar strings na UI — usar `stringResource()`
8. **NUNCA** usar Hilt/Dagger — o projeto usa Koin
9. **NUNCA** mutар o State diretamente — sempre emitir um novo estado via `copy()`
10. **NUNCA** colocar múltiplos concerns no mesmo Use Case — um Use Case, uma responsabilidade
11. **NUNCA** usar `UiEvent` ou `UiState` como nome — usar `Intent`, `State`, `Effect` (MVI)
12. **NUNCA** usar Firebase — o projeto usa Supabase (PostgreSQL)

---

## 🧪 TDD (Obrigatório)

### Regra de Ouro
> **Escreva o teste ANTES do código funcional.**

### Estrutura de Testes
- Testes unitários em `commonTest/` (ou `test/` para Android-specific)
- Use `kotlin-test` para assertions
- Use `Turbine` para testar Flows
- Use `MockK` para mocks (ou expect/actual pattern em KMP)

### Comandos
```bash
# Testes unitários
./gradlew :composeApp:testDebugUnitTest

# Testes do módulo shared
./gradlew :shared:testDebugUnitTest

# Lint
./gradlew :composeApp:lint
```

---

## 🐳 Docker (Isolamento)

### Comandos de Build
```bash
# Build dentro do container
docker compose run android-build

# Testes dentro do container
docker compose run android-test

# Lint dentro do container
docker compose run android-lint
```

---

## 📦 Dependências

Todas as versões são gerenciadas via `gradle/libs.versions.toml`.
**NUNCA** declarar versões inline nos `build.gradle.kts`.

---

## 📝 Commits

### Formato
```
tipo(escopo): descrição curta

corpo opcional com mais detalhes
```

### Tipos permitidos
- `feat` — nova funcionalidade
- `fix` — correção de bug
- `test` — adição/modificação de testes
- `refactor` — refatoração sem alterar comportamento
- `docs` — documentação
- `chore` — tarefas de build/config
- `style` — formatação (sem mudança de lógica)
