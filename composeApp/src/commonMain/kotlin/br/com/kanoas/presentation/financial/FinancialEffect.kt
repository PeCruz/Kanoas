package br.com.kanoas.presentation.financial

import br.com.kanoas.shared.core.mvi.UiEffect

sealed interface FinancialEffect : UiEffect {
    data object OpenAddTransactionDialog : FinancialEffect
    data class ShowError(val message: String) : FinancialEffect
}
