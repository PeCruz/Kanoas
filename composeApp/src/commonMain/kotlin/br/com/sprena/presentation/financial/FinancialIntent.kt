package br.com.sprena.presentation.financial

import br.com.sprena.shared.core.mvi.UiIntent

sealed interface FinancialIntent : UiIntent {
    data object Load : FinancialIntent
    data object AddTransactionClicked : FinancialIntent
    data object DismissAddDialog : FinancialIntent
}
