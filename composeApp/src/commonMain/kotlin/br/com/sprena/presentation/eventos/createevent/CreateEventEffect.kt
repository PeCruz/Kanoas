package br.com.sprena.presentation.eventos.createevent

import br.com.sprena.presentation.eventos.EventCategory
import br.com.sprena.shared.core.mvi.UiEffect

sealed interface CreateEventEffect : UiEffect {
    data class EventSaved(
        val eventId: String?,
        val name: String,
        val category: EventCategory,
        val dateEpochDay: Long,
        val contact: String?,
        val description: String?,
    ) : CreateEventEffect

    data object NavigateBack : CreateEventEffect
}
