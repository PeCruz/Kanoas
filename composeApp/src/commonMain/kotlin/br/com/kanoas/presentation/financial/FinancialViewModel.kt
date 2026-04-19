package br.com.kanoas.presentation.financial

import androidx.lifecycle.ViewModel
import br.com.kanoas.shared.core.mvi.MviViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class FinancialViewModel :
    ViewModel(),
    MviViewModel<FinancialState, FinancialIntent, FinancialEffect> {

    private val _state = MutableStateFlow(FinancialState())
    override val state: StateFlow<FinancialState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<FinancialEffect>()
    override val effects: SharedFlow<FinancialEffect> = _effects.asSharedFlow()

    override fun handleIntent(intent: FinancialIntent) {
        TODO("Day 3 TDD — implementar após Red")
    }
}
