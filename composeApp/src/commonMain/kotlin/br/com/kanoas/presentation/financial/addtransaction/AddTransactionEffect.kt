package br.com.kanoas.presentation.financial.addtransaction

import br.com.kanoas.shared.core.mvi.UiEffect

sealed interface AddTransactionEffect : UiEffect {
    data object TransactionCreated : AddTransactionEffect
    data object Dismissed : AddTransactionEffect
    data class ShowError(val message: String) : AddTransactionEffect
}
