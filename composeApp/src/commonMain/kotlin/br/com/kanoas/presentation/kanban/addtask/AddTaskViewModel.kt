package br.com.kanoas.presentation.kanban.addtask

import androidx.lifecycle.ViewModel
import br.com.kanoas.shared.core.mvi.MviViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel do diálogo de criação de Task.
 *
 * A função [today] é injetada para permitir controle de "hoje" nos testes,
 * evitando dependência direta de um Clock de plataforma.
 */
class AddTaskViewModel(
    private val today: () -> Long = { 0L },
) : ViewModel(), MviViewModel<AddTaskState, AddTaskIntent, AddTaskEffect> {

    private val _state = MutableStateFlow(AddTaskState(startEpochDay = today()))
    override val state: StateFlow<AddTaskState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AddTaskEffect>()
    override val effects: SharedFlow<AddTaskEffect> = _effects.asSharedFlow()

    override fun handleIntent(intent: AddTaskIntent) {
        TODO("Day 3 TDD — implementar após Red")
    }
}
