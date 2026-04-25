package br.com.sprena.presentation.financial.addtransaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import br.com.sprena.presentation.financial.TransactionType

/**
 * Diálogo para adicionar uma nova transação financeira.
 *
 * Campos: Amount (BRL), Person Name, Type (Income/Expense),
 * Category (dropdown), Description.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    viewModel: AddTransactionViewModel,
    onDismiss: () -> Unit,
    onTransactionCreated: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AddTransactionEffect.TransactionCreated -> onTransactionCreated()
                is AddTransactionEffect.Dismissed -> onDismiss()
                is AddTransactionEffect.ShowError -> { /* handled inline */ }
            }
        }
    }

    Dialog(onDismissRequest = { viewModel.handleIntent(AddTransactionIntent.Dismiss) }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = "Nova Transação",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Type chips (Income / Expense) ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = state.type == TransactionType.INCOME,
                        onClick = {
                            viewModel.handleIntent(
                                AddTransactionIntent.TypeChanged(TransactionType.INCOME),
                            )
                        },
                        label = { Text("Entrada") },
                    )
                    FilterChip(
                        selected = state.type == TransactionType.EXPENSE,
                        onClick = {
                            viewModel.handleIntent(
                                AddTransactionIntent.TypeChanged(TransactionType.EXPENSE),
                            )
                        },
                        label = { Text("Saída") },
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- Amount (BRL) ---
                OutlinedTextField(
                    value = state.amountRaw,
                    onValueChange = {
                        viewModel.handleIntent(AddTransactionIntent.AmountChanged(it))
                    },
                    label = { Text("Valor (R$) *") },
                    singleLine = true,
                    isError = state.amountError != null,
                    supportingText = state.amountError?.let { e -> { Text(e) } },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // --- Person Name ---
                OutlinedTextField(
                    value = state.personName,
                    onValueChange = {
                        viewModel.handleIntent(AddTransactionIntent.PersonNameChanged(it))
                    },
                    label = { Text("Responsável *") },
                    singleLine = true,
                    isError = state.personNameError != null,
                    supportingText = state.personNameError?.let { e -> { Text(e) } },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // --- Category Dropdown ---
                var categoryExpanded by remember { mutableStateOf(false) }
                val categories = listOf(
                    "Salário", "Vendas", "Mercado", "Transporte",
                    "Serviços", "Material", "Outros",
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it },
                ) {
                    OutlinedTextField(
                        value = state.category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    viewModel.handleIntent(
                                        AddTransactionIntent.CategoryChanged(cat),
                                    )
                                    categoryExpanded = false
                                },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- Description ---
                OutlinedTextField(
                    value = state.description,
                    onValueChange = {
                        viewModel.handleIntent(AddTransactionIntent.DescriptionChanged(it))
                    },
                    label = { Text("Descrição") },
                    minLines = 2,
                    maxLines = 4,
                    isError = state.descriptionError != null,
                    supportingText = state.descriptionError?.let { e -> { Text(e) } },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(20.dp))

                // --- Actions ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { viewModel.handleIntent(AddTransactionIntent.Dismiss) },
                    ) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.handleIntent(AddTransactionIntent.Submit) },
                        enabled = state.canSubmit,
                    ) {
                        Text("Adicionar")
                    }
                }
            }
        }
    }
}