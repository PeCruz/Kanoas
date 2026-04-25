package br.com.sprena.presentation.kanban.addtask

import br.com.sprena.shared.core.mvi.UiState

/**
 * State do diálogo "Adicionar Task".
 *
 * Campos:
 *  - [name]               — Título da task (obrigatório, máx 50)
 *  - [priority]           — 1..5 (obrigatório, dropdown)
 *  - [description]        — opcional, máx 3000
 *  - [comment]            — opcional, máx 2000
 *  - [startEpochDay]      — data-início = hoje (não editável)
 *  - [endEpochDay]        — data-fim, obrigatória, >= hoje
 *  - [attachmentName]     — nome do anexo escolhido, se houver
 *  - [attachmentSizeBytes]— tamanho em bytes (limite 100 MB)
 *  - [*Error]             — mensagens de validação por campo
 *  - [isSubmitting]       — indicador de envio em progresso
 *  - [canSubmit]          — derivado: todos os campos obrigatórios válidos
 */
data class AddTaskState(
    val name: String = "",
    val priority: Int? = null,
    val description: String = "",
    val comment: String = "",
    val startEpochDay: Long = 0L,
    val endEpochDay: Long? = null,
    val attachmentName: String? = null,
    val attachmentSizeBytes: Long = 0L,
    val nameError: String? = null,
    val priorityError: String? = null,
    val descriptionError: String? = null,
    val commentError: String? = null,
    val endDateError: String? = null,
    val attachmentError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
) : UiState {
    companion object {
        /** Opções fixas de prioridade expostas ao dropdown. */
        val PRIORITY_OPTIONS: List<Int> = listOf(1, 2, 3, 4, 5)
    }
}
