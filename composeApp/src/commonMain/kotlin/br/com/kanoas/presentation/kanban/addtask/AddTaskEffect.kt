package br.com.kanoas.presentation.kanban.addtask

import br.com.kanoas.shared.core.mvi.UiEffect

sealed interface AddTaskEffect : UiEffect {
    data object TaskCreated : AddTaskEffect
    data object Dismissed : AddTaskEffect
    data class ShowError(val message: String) : AddTaskEffect
}
