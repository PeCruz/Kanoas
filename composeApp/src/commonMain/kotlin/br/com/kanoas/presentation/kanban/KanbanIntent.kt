package br.com.kanoas.presentation.kanban

import br.com.kanoas.shared.core.mvi.UiIntent

sealed interface KanbanIntent : UiIntent {
    data object LoadBoard : KanbanIntent
    data object AddTaskClicked : KanbanIntent
    data object DismissAddTaskDialog : KanbanIntent
    data class MoveTask(val taskId: String, val targetColumnId: String) : KanbanIntent
    data class SearchQueryChanged(val query: String) : KanbanIntent
    data object SettingsClicked : KanbanIntent
    data class TaskCreated(val name: String, val priority: Int) : KanbanIntent

    /** Intent auxiliar para injetar tasks em testes. Não usar em produção. */
    data class AddTestTasks(val tasks: Map<String, List<KanbanTask>>) : KanbanIntent
}