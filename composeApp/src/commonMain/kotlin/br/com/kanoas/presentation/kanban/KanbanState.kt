package br.com.kanoas.presentation.kanban

import br.com.kanoas.shared.core.mvi.UiState

/**
 * Representação leve de uma coluna exibida na UI do Kanban.
 * Domain models completos virão em shared/kanban/domain/model.
 */
data class KanbanColumn(
    val id: String,
    val title: String,
)

data class KanbanTask(
    val id: String,
    val columnId: String,
    val name: String,
    val priority: Int,
)

/**
 * State da tela de Kanban (= Home).
 *
 * Campos:
 *  - [boardId] — id do quadro ativo (null enquanto carrega)
 *  - [columns] — colunas visíveis (ex: "A Fazer", "Em Progresso", "Concluído")
 *  - [tasksByColumn] — tasks agrupadas por columnId (fonte da verdade)
 *  - [searchQuery] — texto de busca do usuário
 *  - [filteredTasksByColumn] — tasks filtradas pela searchQuery (derivado)
 *  - [isLoading] — carregando do Supabase / SQLDelight
 *  - [error] — mensagem de erro a exibir
 *  - [isAddTaskDialogVisible] — controla o diálogo de criação
 */
data class KanbanState(
    val boardId: String? = null,
    val columns: List<KanbanColumn> = emptyList(),
    val tasksByColumn: Map<String, List<KanbanTask>> = emptyMap(),
    val searchQuery: String = "",
    val filteredTasksByColumn: Map<String, List<KanbanTask>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAddTaskDialogVisible: Boolean = false,
) : UiState
