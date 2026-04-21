package br.com.kanoas.presentation.kanban.taskdetail

import br.com.kanoas.presentation.kanban.KanbanColumn
import br.com.kanoas.shared.core.mvi.UiState

/**
 * State da tela de detalhes/edição de tarefa.
 *
 * Exibida como bottom sheet sobre o Kanban. Campos editáveis:
 * name, priority, description, comment, endEpochDay, columnId (mover).
 *
 * [hasChanges] — true se qualquer campo difere do valor original.
 * [isDeleteConfirmVisible] — controla o diálogo de confirmação de exclusão.
 */
data class TaskDetailState(
    val taskId: String = "",
    val name: String = "",
    val priority: Int? = null,
    val description: String = "",
    val comment: String = "",
    val endEpochDay: Long? = null,
    val columnId: String = "",
    val availableColumns: List<KanbanColumn> = emptyList(),
    val nameError: String? = null,
    val priorityError: String? = null,
    val descriptionError: String? = null,
    val commentError: String? = null,
    val endDateError: String? = null,
    val hasChanges: Boolean = false,
    val isDeleteConfirmVisible: Boolean = false,
    // Snapshot dos valores originais para detectar hasChanges
    val originalName: String = "",
    val originalPriority: Int? = null,
    val originalDescription: String = "",
    val originalComment: String = "",
    val originalEndEpochDay: Long? = null,
    val originalColumnId: String = "",
) : UiState
