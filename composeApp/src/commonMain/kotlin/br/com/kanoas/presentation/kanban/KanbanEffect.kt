package br.com.kanoas.presentation.kanban

import br.com.kanoas.shared.core.mvi.UiEffect

sealed interface KanbanEffect : UiEffect {
    data object OpenAddTaskDialog : KanbanEffect
    data class ShowError(val message: String) : KanbanEffect
}
