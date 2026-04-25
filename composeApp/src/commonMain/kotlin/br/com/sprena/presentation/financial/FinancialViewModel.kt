package br.com.sprena.presentation.financial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.sprena.shared.core.mvi.MviViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FinancialViewModel :
    ViewModel(),
    MviViewModel<FinancialState, FinancialIntent, FinancialEffect> {

    private val _state = MutableStateFlow(FinancialState())
    override val state: StateFlow<FinancialState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<FinancialEffect>()
    override val effects: SharedFlow<FinancialEffect> = _effects.asSharedFlow()

    override fun handleIntent(intent: FinancialIntent) {
        when (intent) {
            is FinancialIntent.Load -> {
                _state.value = _state.value.copy(isLoading = true)
                // TODO: integrar com Firebase Firestore para carregar transações reais
                _state.value = _state.value.copy(isLoading = false)
            }

            is FinancialIntent.AddTransactionClicked -> {
                _state.value = _state.value.copy(isAddDialogVisible = true)
                viewModelScope.launch {
                    _effects.emit(FinancialEffect.OpenAddTransactionDialog)
                }
            }

            is FinancialIntent.DismissAddDialog -> {
                _state.value = _state.value.copy(isAddDialogVisible = false)
            }
        }
    }
}