package br.com.kanoas.presentation.kanban

import androidx.lifecycle.ViewModel
import br.com.kanoas.shared.core.mvi.MviViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class KanbanViewModel : ViewModel(), MviViewModel<KanbanState, KanbanIntent, KanbanEffect> {

    private val _state = MutableStateFlow(KanbanState())
    override val state: StateFlow<KanbanState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<KanbanEffect>()
    override val effects: SharedFlow<KanbanEffect> = _effects.asSharedFlow()

    override fun handleIntent(intent: KanbanIntent) {
        TODO("Day 3 TDD — implementar após Red")
    }
}
