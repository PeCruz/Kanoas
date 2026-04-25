package br.com.sprena.presentation.financial.addtransaction

import br.com.sprena.presentation.financial.TransactionType
import br.com.sprena.shared.core.mvi.UiIntent

sealed interface AddTransactionIntent : UiIntent {
    data class AmountChanged(val raw: String) : AddTransactionIntent
    data class PersonNameChanged(val value: String) : AddTransactionIntent
    data class TypeChanged(val value: TransactionType) : AddTransactionIntent
    data class CategoryChanged(val value: String) : AddTransactionIntent
    data class DescriptionChanged(val value: String) : AddTransactionIntent
    data object Submit : AddTransactionIntent
    data object Dismiss : AddTransactionIntent
}
