package br.com.sprena.presentation.financial

import br.com.sprena.shared.core.mvi.UiState

data class FinancialTransactionSummary(
    val id: String,
    val description: String,
    val cents: Long,
    val type: TransactionType,
)

enum class TransactionType { INCOME, EXPENSE }

/**
 * Placeholder — tela financeira será detalhada mais adiante.
 * Campos sugeridos:
 *  - [transactions] — lista para o dashboard
 *  - [balanceCents] / [incomeCents] / [expenseCents] — totais
 *  - [isAddDialogVisible] — diálogo de nova transação
 */
data class FinancialState(
    val transactions: List<FinancialTransactionSummary> = emptyList(),
    val balanceCents: Long = 0L,
    val incomeCents: Long = 0L,
    val expenseCents: Long = 0L,
    val isLoading: Boolean = false,
    val isAddDialogVisible: Boolean = false,
) : UiState
