package br.com.kanoas.presentation.kanban.createtask

import br.com.kanoas.shared.core.mvi.UiEffect

sealed interface CreateTaskEffect : UiEffect {
    data object TaskCreated : CreateTaskEffect
    data object GoBack : CreateTaskEffect
    data class ShowError(val message: String) : CreateTaskEffect
}