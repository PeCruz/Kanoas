package br.com.kanoas.presentation.financial.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.kanoas.presentation.financial.TransactionType
import br.com.kanoas.shared.core.mvi.MviViewModel
import br.com.kanoas.shared.financial.domain.validation.BrlMonetaryFormatter
import br.com.kanoas.shared.financial.domain.validation.TransactionValidator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddTransactionViewModel(
    private val today: () -> Long = { 0L },
) : ViewModel(), MviViewModel<AddTransactionState, AddTransactionIntent, AddTransactionEffect> {

    private val _state = MutableStateFlow(AddTransactionState(inputEpochDay = today()))
    override val state: StateFlow<AddTransactionState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AddTransactionEffect>()
    override val effects: SharedFlow<AddTransactionEffect> = _effects.asSharedFlow()

    override fun handleIntent(intent: AddTransactionIntent) {
        when (intent) {
            is AddTransactionIntent.AmountChanged -> {
                val validation = BrlMonetaryFormatter.validate(intent.raw)
                val cents = BrlMonetaryFormatter.parseToCents(intent.raw) ?: 0L
                updateState {
                    copy(
                        amountRaw = intent.raw,
                        amountCents = cents,
                        amountError = validation.errorMessage,
                    )
                }
            }

            is AddTransactionIntent.PersonNameChanged -> {
                val v = TransactionValidator.validatePersonName(intent.value)
                updateState {
                    copy(personName = intent.value, personNameError = v.errorMessage)
                }
            }

            is AddTransactionIntent.TypeChanged -> {
                updateState { copy(type = intent.value) }
            }

            is AddTransactionIntent.CategoryChanged -> {
                updateState { copy(category = intent.value) }
            }

            is AddTransactionIntent.DescriptionChanged -> {
                val v = TransactionValidator.validateDescription(intent.value)
                updateState {
                    copy(description = intent.value, descriptionError = v.errorMessage)
                }
            }

            is AddTransactionIntent.Submit -> {
                if (_state.value.canSubmit) {
                    viewModelScope.launch { _effects.emit(AddTransactionEffect.TransactionCreated) }
                } else {
                    viewModelScope.launch {
                        _effects.emit(AddTransactionEffect.ShowError("Preencha todos os campos obrigatórios"))
                    }
                }
            }

            is AddTransactionIntent.Dismiss -> {
                viewModelScope.launch { _effects.emit(AddTransactionEffect.Dismissed) }
            }
        }
    }

    private fun updateState(transform: AddTransactionState.() -> AddTransactionState) {
        val newState = _state.value.transform()
        _state.value = newState.copy(canSubmit = computeCanSubmit(newState))
    }

    private fun computeCanSubmit(s: AddTransactionState): Boolean =
        BrlMonetaryFormatter.validate(s.amountRaw).isValid &&
            TransactionValidator.validatePersonName(s.personName).isValid &&
            TransactionValidator.validateDescription(s.description).isValid
}