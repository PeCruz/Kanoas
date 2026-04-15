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

### Clean Architecture + MVVM

```
┌─────────────────────────────────────────────┐
│              composeApp/                     │
│  ┌────────────────────────────────────────┐  │
│  │  Presentation (UI + ViewModel)         │  │
│  │  commonMain/ → Compose Multiplatform   │  │
│  │  androidMain/ → Activity, Application  │  │
│  └──────────────┬─────────────────────────┘  │
│                 │ depends on                  │
├─────────────────┼─────────────────────────────┤
│              shared/                          │
│  ┌──────────────┼─────────────────────────┐  │
│  │  Domain (Use Cases, Models, Repos IF)  │  │
│  │  Data (Repos Impl, DataSources)        │  │
│  │  commonMain/ → Pure Kotlin             │  │
│  └────────────────────────────────────────┘  │
└─────────────────────────────────────────────┘
```

### Camadas e Responsabilidades

| Camada | Módulo | Conteúdo |
|--------|--------|----------|
| **Presentation** | `composeApp/commonMain` | Screens, ViewModels, Navigation, Theme |
| **Domain** | `shared/commonMain/domain` | Models, Use Cases, Repository interfaces |
| **Data** | `shared/commonMain/data` | Repository implementations, Data Sources, DTOs |
| **Platform** | `composeApp/androidMain` | Activity, Application, platform DI |

---

## 📐 Padrões Obrigatórios

### Linguagem e Frameworks
- **Kotlin** — única linguagem permitida
- **Coroutines + Flow** — para operações assíncronas
- **StateFlow** — para estado reativo no ViewModel
- **Koin** — injeção de dependência (NÃO usar Hilt/Dagger)
- **Material 3** — design system

### Convenções de Nomenclatura

| Tipo | Padrão | Exemplo |
|------|--------|---------|
| Screen (Composable) | `{Feature}Screen` | `HomeScreen` |
| ViewModel | `{Feature}ViewModel` | `HomeViewModel` |
| UI State | `{Feature}UiState` | `HomeUiState` |
| UI Event | `{Feature}UiEvent` | `HomeUiEvent` |
| Use Case | `{Ação}{Entidade}UseCase` | `GetUserProfileUseCase` |
| Repository Interface | `{Entidade}Repository` | `UserRepository` |
| Repository Impl | `{Entidade}RepositoryImpl` | `UserRepositoryImpl` |
| Koin Module | `{escopo}Module` | `appModule`, `dataModule` |

### Estrutura de Feature

Cada feature deve seguir este padrão:
```
presentation/{feature}/
├── {Feature}Screen.kt        # @Composable function
├── {Feature}ViewModel.kt     # ViewModel com StateFlow
├── {Feature}UiState.kt       # data class imutável (se complexo)
└── {Feature}UiEvent.kt       # sealed interface (se complexo)
```

---

## 🚫 Restrições

1. **NUNCA** colocar lógica de negócio em Composables
2. **NUNCA** usar `var` para estado — sempre `StateFlow` ou `MutableState`
3. **NUNCA** importar dependências Android no módulo `shared/`
4. **NUNCA** criar dependências cíclicas entre módulos
5. **NUNCA** fazer chamadas de rede diretamente no ViewModel — sempre via Use Case
6. **NUNCA** usar `GlobalScope` — sempre `viewModelScope` ou scope injetado
7. **NUNCA** hardcodar strings na UI — usar `stringResource()`
8. **NUNCA** usar Hilt/Dagger — o projeto usa Koin

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
