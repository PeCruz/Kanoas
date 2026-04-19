package br.com.kanoas.presentation.kanban

import br.com.kanoas.shared.core.mvi.UiIntent

sealed interface KanbanIntent : UiIntent {
    data object LoadBoard : KanbanIntent
    data object AddTaskClicked : KanbanIntent
    data object DismissAddTaskDialog : KanbanIntent
    data class MoveTask(val taskId: String, val targetColumnId: String) : KanbanIntent
}
