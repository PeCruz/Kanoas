package br.com.kanoas.presentation.financial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.kanoas.core.ui.components.ThemeToggleButton
import br.com.kanoas.presentation.core.theme.ThemeViewModel
import br.com.kanoas.shared.financial.domain.validation.BrlMonetaryFormatter

/**
 * Tela Financeira — exibe dashboard com saldo, entradas, saídas
 * e lista de transações. FAB "+" abre o diálogo de nova transação.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialScreen(
    viewModel: FinancialViewModel,
    themeViewModel: ThemeViewModel,
    onAddTransactionDialog: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(FinancialIntent.Load)
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FinancialEffect.OpenAddTransactionDialog -> onAddTransactionDialog()
                is FinancialEffect.ShowError -> { /* TODO: snackbar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financeiro") },
                actions = {
                    ThemeToggleButton(themeViewModel = themeViewModel)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(FinancialIntent.AddTransactionClicked) },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
            ) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        },
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            ) {
                // --- Summary Cards ---
                FinancialSummaryRow(
                    balanceCents = state.balanceCents,
                    incomeCents = state.incomeCents,
                    expenseCents = state.expenseCents,
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Transações",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (state.transactions.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Nenhuma transação registrada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.transactions, key = { it.id }) { tx ->
                            TransactionCard(tx)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FinancialSummaryRow(
    balanceCents: Long,
    incomeCents: Long,
    expenseCents: Long,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SummaryCard(
            label = "Saldo",
            valueCents = balanceCents,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        SummaryCard(
            label = "Entradas",
            valueCents = incomeCents,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.weight(1f),
        )
        SummaryCard(
            label = "Saídas",
            valueCents = expenseCents,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SummaryCard(
    label: String,
    valueCents: Long,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f),
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = BrlMonetaryFormatter.formatCents(valueCents),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
    }
}

@Composable
private fun TransactionCard(tx: FinancialTransactionSummary) {
    val isIncome = tx.type == TransactionType.INCOME
    val valueColor = if (isIncome) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.error
    }
    val prefix = if (isIncome) "+" else "-"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = tx.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "$prefix ${BrlMonetaryFormatter.formatCents(tx.cents)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor,
            )
        }
    }
}