package br.com.sprena.presentation.eventos.createevent

import br.com.sprena.presentation.eventos.EventCategory
import br.com.sprena.shared.core.mvi.UiState

/**
 * State do formulario de criacao/edicao de evento.
 *
 * Campos obrigatorios: name, category, dateEpochDay.
 * Campos opcionais: contact, description.
 */
data class CreateEventState(
    val editingEventId: String? = null,
    val isEditMode: Boolean = false,
    val name: String = "",
    val category: EventCategory? = null,
    val dateEpochDay: Long? = null,
    val contact: String = "",
    val description: String = "",
    val nameError: String? = null,
    val categoryError: String? = null,
    val dateError: String? = null,
    val canSubmit: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
) : UiState
