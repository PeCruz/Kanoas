package br.com.kanoas.presentation.financial.addtransaction

import androidx.lifecycle.ViewModel
import br.com.kanoas.shared.core.mvi.MviViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel do diálogo de criação de Transação Financeira.
 *
 * A função [today] é injetada para permitir controle de "hoje" nos testes.
 */
class AddTransactionViewModel(
    private val today: () -> Long = { 0L },
) : ViewModel(), MviViewModel<AddTransactionState, AddTransactionIntent, AddTransactionEffect> {

    private val _state = MutableStateFlow(AddTransactionState(inputEpochDay = today()))
    override val state: StateFlow<AddTransactionState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AddTransactionEffect>()
    override val effects: SharedFlow<AddTransactionEffect> = _effects.asSharedFlow()

    override fun handleIntent(intent: AddTransactionIntent) {
        TODO("Day 3 TDD — implementar após Red")
    }
}
