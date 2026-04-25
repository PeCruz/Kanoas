# CLAUDE.md — Sprena (AI Governance Document)

> **Este documento é a "coleira" da IA.** Ele define regras, padrões e restrições
> que devem ser seguidos em TODAS as interações de geração de código.
> Atualizar este documento sempre que um padrão novo for estabelecido ou um erro
> recorrente for identificado.

---

## 📋 Visão do Projeto

**Sprena** é um aplicativo mobile multi-plataforma (Android + iOS futuro)
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
│  │    ├── kanban/       → Kanban Screens + VMs │  │
│  │    ├── financial/    → Financial Screens+VMs│  │
│  │    ├── sportclient/  → SportClient Screens  │  │
│  │    └── core/ui/      → Theme, Nav, Comps    │  │
│  │  androidMain/ → Activity, Application, DI   │  │
│  └──────────────────┬──────────────────────────┘  │
│                     │ depends on                  │
├─────────────────────┼────────────────────────────-┤
│                  shared/                          │
│  ┌──────────────────┼──────────────────────────┐  │
│  │  sportclient/                               │  │
│  │    ├── domain/ → Models, Validation, Repo IF│  │
│  │    ├── data/   → DTO, Repo Impl (Firestore) │  │
│  │    └── di/     → Koin module                │  │
│  │  kanban/                                    │  │
│  │    ├── domain/ → Models, UseCases, Repo IF  │  │
│  │    └── data/   → Repo Impl, DataSources     │  │
│  │  financial/                                 │  │
│  │    ├── domain/                              │  │
│  │    └── data/                                │  │
│  │  core/                                      │  │
│  │    ├── mvi/      → UiState, UiIntent, etc.  │  │
│  │    ├── validation/→ ValidationResult        │  │
│  │    └── di/       → Koin modules             │  │
│  │  commonMain/ → Pure Kotlin (sem Android)    │  │
│  │  androidMain/→ Firebase Firestore impls     │  │
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
| **Domain** | `shared/{feature}/domain` | Models, Validation, Use Cases, Repository interfaces |
| **Data** | `shared/{feature}/data` | Repository implementations, DTOs |
| **Data (Android)** | `shared/androidMain/{feature}/data` | Firestore repository implementations |
| **Core** | `shared/core` | MVI contracts, ValidationResult, Koin modules |
| **Platform** | `composeApp/androidMain` | Activity, Application, Firebase DI, Repo bindings |

---

## 📐 Padrões Obrigatórios

### Linguagem e Frameworks
- **Kotlin** — única linguagem permitida
- **Coroutines + Flow** — para operações assíncronas
- **StateFlow** — para estado reativo (MVI State)
- **SharedFlow** — para efeitos colaterais one-shot (navigation, toasts)
- **Koin** — injeção de dependência (NÃO usar Hilt/Dagger)
- **Material 3** — design system
- **Firebase Firestore** — backend (database, real-time sync)
- **Kotlinx.serialization** — serialização JSON
- **Firebase BOM** — gerenciamento de versões Firebase

### Backend — Firebase Firestore

- **Dados dinâmicos** (clientes, transações, tarefas) → Firebase Firestore
- **Dados estáticos** (enums: SportModality, PaymentMethod, Frequency) → in-app
- **Repository pattern**: interface em `shared/commonMain`, implementação Firestore em `shared/androidMain`
- **Coleções Firestore**: `sport_clients` (SportClient), demais conforme features
- **applicationId**: `br.com.sprena`
- **google-services.json**: deve estar em `composeApp/`

### Convenções de Nomenclatura (MVI)

| Tipo | Padrão | Exemplo |
|------|--------|---------|
| Screen (Composable) | `{Feature}Screen` | `SportClientScreen` |
| ViewModel | `{Feature}ViewModel` | `SportClientViewModel` |
| MVI State | `{Feature}State` | `SportClientState` |
| MVI Intent | `{Feature}Intent` | `SportClientIntent` |
| MVI Effect | `{Feature}Effect` | `SportClientEffect` |
| Use Case | `{Ação}{Entidade}UseCase` | `GetBoardTasksUseCase` |
| Repository Interface | `{Entidade}Repository` | `SportClientRepository` |
| Repository Impl | `{Entidade}RepositoryImpl` | `SportClientRepositoryImpl` |
| DTO (Data Transfer Object) | `{Entidade}Dto` | `SportClientDto` |
| Domain Model | `{Entidade}Model` | `SportClientModel` |
| Koin Module | `{escopo}Module` | `sportClientModule`, `platformModule` |

### Estrutura de Feature (MVI)

Cada feature deve seguir este padrão:
```
{feature}/
├── presentation/                  (em composeApp/commonMain)
│   ├── {Feature}Screen.kt        # @Composable — só renderiza state, dispara intents
│   ├── {Feature}ViewModel.kt     # MviViewModel<State, Intent, Effect>
│   ├── {Feature}State.kt         # data class imutável — snapshot completo da UI
│   ├── {Feature}Intent.kt        # sealed interface — tudo que o usuário pode fazer
│   └── {Feature}Effect.kt        # sealed interface — efeitos one-shot (nav, toast)
├── domain/                        (em shared/commonMain)
│   ├── model/                    # entidades puras do domínio
│   ├── validation/               # validadores (pure functions)
│   ├── usecase/                  # um arquivo por use case
│   └── repository/               # interfaces (contratos)
├── data/                          (em shared/commonMain ou androidMain)
│   ├── repository/               # implementações Firestore (androidMain)
│   └── dto/                      # mapeamento de/para Firestore
└── di/                            (em shared/commonMain)
    └── {Feature}Module.kt        # Koin module
```

---

## 🚫 Restrições

1. **NUNCA** colocar lógica de negócio em Composables
2. **NUNCA** usar `var` para estado — sempre `StateFlow` (state) ou `SharedFlow` (effects)
3. **NUNCA** importar dependências Android no módulo `shared/commonMain`
4. **NUNCA** criar dependências cíclicas entre módulos
5. **NUNCA** fazer chamadas de rede diretamente no ViewModel — sempre via Use Case
6. **NUNCA** usar `GlobalScope` — sempre `viewModelScope` ou scope injetado
7. **NUNCA** hardcodar strings na UI — usar `stringResource()`
8. **NUNCA** usar Hilt/Dagger — o projeto usa Koin
9. **NUNCA** mutar o State diretamente — sempre emitir um novo estado via `copy()`
10. **NUNCA** colocar múltiplos concerns no mesmo Use Case — um Use Case, uma responsabilidade
11. **NUNCA** usar `UiEvent` ou `UiState` como nome — usar `Intent`, `State`, `Effect` (MVI)
12. **NUNCA** usar Supabase, Ktor, ou SQLDelight — o projeto usa Firebase Firestore
13. **NUNCA** importar Firebase no `shared/commonMain` — Firebase só em `shared/androidMain` ou `composeApp/androidMain`

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

### Stack atual
- Kotlin 2.1.10, AGP 8.7.3, Compose Multiplatform 1.7.3
- Koin 4.0.2
- Firebase BOM 33.7.0 (Firestore)
- Google Services Plugin 4.4.2

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
