package br.com.kanoas.presentation.financial

import br.com.kanoas.shared.core.mvi.UiIntent

sealed interface FinancialIntent : UiIntent {
    data object Load : FinancialIntent
    data object AddTransactionClicked : FinancialIntent
    data object DismissAddDialog : FinancialIntent
}
