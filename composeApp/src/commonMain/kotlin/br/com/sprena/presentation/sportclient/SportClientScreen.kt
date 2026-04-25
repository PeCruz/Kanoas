package br.com.sprena.presentation.sportclient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.sprena.core.ui.components.ThemeToggleButton
import br.com.sprena.core.ui.mask.CpfMaskTransformation
import br.com.sprena.core.ui.mask.CurrencyMaskTransformation
import br.com.sprena.core.ui.mask.MonthYearMaskTransformation
import br.com.sprena.core.ui.mask.PhoneMaskTransformation
import br.com.sprena.core.ui.mask.centsToDigitString
import br.com.sprena.core.ui.mask.filterDigitsOnly
import br.com.sprena.core.ui.mask.formatMonthYearDigits
import br.com.sprena.core.ui.mask.parseCurrencyDigits
import br.com.sprena.presentation.core.theme.ThemeViewModel
import br.com.sprena.shared.sportclient.domain.validation.PaymentMethod
import br.com.sprena.shared.sportclient.domain.validation.SportClientValidator
import br.com.sprena.shared.sportclient.domain.validation.SportModality

/**
 * Tela Home — gestão de clientes de esportes (futevôlei, beach tennis, vôlei).
 *
 * Layout: tabela com busca + botão add, linhas clicáveis para editar/deletar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportClientScreen(
    viewModel: SportClientViewModel,
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Clientes") },
                actions = {
                    ThemeToggleButton(themeViewModel = themeViewModel)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            // --- Search Input + Add Button ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = {
                        viewModel.handleIntent(SportClientIntent.SearchQueryChanged(it))
                    },
                    placeholder = { Text("Buscar cliente...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilledIconButton(
                    onClick = { viewModel.handleIntent(SportClientIntent.AddClientClicked) },
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar cliente",
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Table Header ---
            SportClientTableHeader()

            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outline)

            // --- Table Body ---
            if (state.filteredClients.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (state.clients.isEmpty()) {
                            "Nenhum cliente registrado"
                        } else {
                            "Nenhum cliente encontrado"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                ) {
                    items(state.filteredClients, key = { it.id }) { client ->
                        SportClientTableRow(
                            client = client,
                            onClick = {
                                viewModel.handleIntent(SportClientIntent.ClientClicked(client))
                            },
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }

    // --- Add Dialog ---
    if (state.isAddDialogVisible) {
        AddSportClientDialog(
            onDismiss = { viewModel.handleIntent(SportClientIntent.DismissAddDialog) },
            onConfirm = { client ->
                viewModel.handleIntent(SportClientIntent.ClientAdded(client))
            },
        )
    }

    // --- Edit / Detail Dialog ---
    val selectedClient = state.selectedClient
    if (selectedClient != null) {
        EditSportClientDialog(
            client = selectedClient,
            onDismiss = { viewModel.handleIntent(SportClientIntent.DismissClientDetail) },
            onUpdate = { updated ->
                viewModel.handleIntent(SportClientIntent.ClientUpdated(updated))
            },
            onDelete = { clientId ->
                viewModel.handleIntent(SportClientIntent.ClientDeleted(clientId))
            },
        )
    }
}

@Composable
private fun SportClientTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Nome",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.2f),
        )
        Text(
            text = "Apelido",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "Modalidade",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.2f),
        )
        Text(
            text = "Freq.",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.6f),
        )
        Text(
            text = "Pagamento",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.1f),
        )
        Text(
            text = "Mês Pgto",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SportClientTableRow(
    client: SportClient,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = client.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.2f),
        )
        Text(
            text = client.apelido,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = modalityLabel(client.modality),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.2f),
        )
        Text(
            text = "${client.attendance}x",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f),
        )
        Text(
            text = paymentMethodLabel(client.paymentMethod),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.1f),
        )
        Text(
            text = client.lastPaymentMonth,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
}

// =========================================================================
// Labels
// =========================================================================

private fun paymentMethodLabel(method: PaymentMethod): String = when (method) {
    PaymentMethod.WELLHUB -> "Wellhub"
    PaymentMethod.TOTALPASS -> "TotalPass"
    PaymentMethod.CASH -> "Cash"
}

private fun modalityLabel(modality: SportModality): String = when (modality) {
    SportModality.FUTEVOLEI -> "Futevôlei"
    SportModality.BEACH_TENNIS -> "Beach Tennis"
    SportModality.VOLEI -> "Vôlei"
}

// =========================================================================
// Shared form fields composable
// =========================================================================

@Composable
private fun SportClientFormFields(
    name: String,
    onNameChange: (String) -> Unit,
    nameError: String?,
    apelido: String,
    onApelidoChange: (String) -> Unit,
    apelidoError: String?,
    cpfDigits: String,
    onCpfChange: (String) -> Unit,
    cpfError: String?,
    phoneDigits: String,
    onPhoneChange: (String) -> Unit,
    phoneError: String?,
    selectedModality: SportModality?,
    onModalityChange: (SportModality) -> Unit,
    modalityError: String?,
    selectedAttendance: Int?,
    onAttendanceChange: (Int) -> Unit,
    attendanceError: String?,
    selectedPayment: PaymentMethod?,
    onPaymentChange: (PaymentMethod) -> Unit,
    paymentError: String?,
    cashDigits: String,
    onCashChange: (String) -> Unit,
    cashError: String?,
    lastPaymentMonth: String,
    onLastPaymentMonthChange: (String) -> Unit,
    lastPaymentMonthError: String?,
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        // --- Nome ---
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nome *") },
            isError = nameError != null,
            supportingText = nameError?.let { e -> { Text(e) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- Apelido (opcional) ---
        OutlinedTextField(
            value = apelido,
            onValueChange = onApelidoChange,
            label = { Text("Apelido") },
            isError = apelidoError != null,
            supportingText = apelidoError?.let { e -> { Text(e) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- CPF ---
        OutlinedTextField(
            value = cpfDigits,
            onValueChange = onCpfChange,
            label = { Text("CPF *") },
            isError = cpfError != null,
            supportingText = cpfError?.let { e -> { Text(e) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = CpfMaskTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- Telefone ---
        OutlinedTextField(
            value = phoneDigits,
            onValueChange = onPhoneChange,
            label = { Text("Telefone *") },
            isError = phoneError != null,
            supportingText = phoneError?.let { e -> { Text(e) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            visualTransformation = PhoneMaskTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- Modalidade ---
        Text(
            text = "Modalidade *",
            style = MaterialTheme.typography.bodySmall,
            color = if (modalityError != null) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            SportModality.entries.forEach { modality ->
                FilterChip(
                    selected = selectedModality == modality,
                    onClick = { onModalityChange(modality) },
                    label = { Text(modalityLabel(modality)) },
                )
            }
        }
        if (modalityError != null) {
            Text(
                text = modalityError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Frequência (1~4) ---
        Text(
            text = "Frequência *",
            style = MaterialTheme.typography.bodySmall,
            color = if (attendanceError != null) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            (1..4).forEach { freq ->
                FilterChip(
                    selected = selectedAttendance == freq,
                    onClick = { onAttendanceChange(freq) },
                    label = { Text("${freq}x") },
                )
            }
        }
        if (attendanceError != null) {
            Text(
                text = attendanceError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Pagamento ---
        Text(
            text = "Pagamento *",
            style = MaterialTheme.typography.bodySmall,
            color = if (paymentError != null) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            PaymentMethod.entries.forEach { method ->
                FilterChip(
                    selected = selectedPayment == method,
                    onClick = { onPaymentChange(method) },
                    label = { Text(paymentMethodLabel(method)) },
                )
            }
        }
        if (paymentError != null) {
            Text(
                text = paymentError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        // --- Valor em dinheiro (aparece para TODOS os métodos) ---
        if (selectedPayment != null) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = cashDigits,
                onValueChange = onCashChange,
                label = { Text("Valor (R$) *") },
                isError = cashError != null,
                supportingText = cashError?.let { e -> { Text(e) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CurrencyMaskTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Mês Pagamento ---
        OutlinedTextField(
            value = lastPaymentMonth,
            onValueChange = onLastPaymentMonthChange,
            label = { Text("Mês Pagamento *") },
            placeholder = { Text("MM/AAAA") },
            isError = lastPaymentMonthError != null,
            supportingText = lastPaymentMonthError?.let { e -> { Text(e) } },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = MonthYearMaskTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// =========================================================================
// Add Dialog
// =========================================================================

@Composable
private fun AddSportClientDialog(
    onDismiss: () -> Unit,
    onConfirm: (SportClient) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var apelido by remember { mutableStateOf("") }
    var cpfDigits by remember { mutableStateOf("") }
    var phoneDigits by remember { mutableStateOf("") }
    var selectedModality by remember { mutableStateOf<SportModality?>(null) }
    var selectedAttendance by remember { mutableStateOf<Int?>(null) }
    var selectedPayment by remember { mutableStateOf<PaymentMethod?>(null) }
    var cashDigits by remember { mutableStateOf("0") }
    var lastPaymentMonthDigits by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var apelidoError by remember { mutableStateOf<String?>(null) }
    var cpfError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var modalityError by remember { mutableStateOf<String?>(null) }
    var attendanceError by remember { mutableStateOf<String?>(null) }
    var paymentError by remember { mutableStateOf<String?>(null) }
    var cashError by remember { mutableStateOf<String?>(null) }
    var lastPaymentMonthError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Cliente") },
        text = {
            SportClientFormFields(
                name = name,
                onNameChange = { name = it; nameError = null },
                nameError = nameError,
                apelido = apelido,
                onApelidoChange = { apelido = it; apelidoError = null },
                apelidoError = apelidoError,
                cpfDigits = cpfDigits,
                onCpfChange = { cpfDigits = filterDigitsOnly(it, 11); cpfError = null },
                cpfError = cpfError,
                phoneDigits = phoneDigits,
                onPhoneChange = { phoneDigits = filterDigitsOnly(it, 11); phoneError = null },
                phoneError = phoneError,
                selectedModality = selectedModality,
                onModalityChange = { selectedModality = it; modalityError = null },
                modalityError = modalityError,
                selectedAttendance = selectedAttendance,
                onAttendanceChange = { selectedAttendance = it; attendanceError = null },
                attendanceError = attendanceError,
                selectedPayment = selectedPayment,
                onPaymentChange = { selectedPayment = it; paymentError = null; cashError = null },
                paymentError = paymentError,
                cashDigits = cashDigits,
                onCashChange = { cashDigits = filterDigitsOnly(it, 10); cashError = null },
                cashError = cashError,
                lastPaymentMonth = lastPaymentMonthDigits,
                onLastPaymentMonthChange = {
                    lastPaymentMonthDigits = filterDigitsOnly(it, 6)
                    lastPaymentMonthError = null
                },
                lastPaymentMonthError = lastPaymentMonthError,
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val nameResult = SportClientValidator.validateName(name)
                val apelidoResult = SportClientValidator.validateApelido(apelido)
                val cpfResult = SportClientValidator.validateCpf(cpfDigits)
                val phoneResult = SportClientValidator.validatePhone(phoneDigits)
                val modalityResult = SportClientValidator.validateModalidade(selectedModality)
                val attendanceResult = SportClientValidator.validateAttendance(selectedAttendance)
                val paymentResult = SportClientValidator.validatePaymentMethod(selectedPayment)
                val formattedMonth = formatMonthYearDigits(lastPaymentMonthDigits)
                val lastMonthResult = SportClientValidator.validateLastPaymentMonth(formattedMonth)

                nameError = nameResult.errorMessage
                apelidoError = apelidoResult.errorMessage
                cpfError = cpfResult.errorMessage
                phoneError = phoneResult.errorMessage
                modalityError = modalityResult.errorMessage
                attendanceError = attendanceResult.errorMessage
                paymentError = paymentResult.errorMessage
                lastPaymentMonthError = lastMonthResult.errorMessage

                val cashAmountCents = parseCurrencyDigits(cashDigits)
                if (selectedPayment != null) {
                    val cashResult = SportClientValidator.validateCashAmount(
                        cashAmountCents,
                        selectedPayment!!,
                    )
                    cashError = cashResult.errorMessage
                    if (!cashResult.isValid) return@TextButton
                }

                if (!nameResult.isValid || !apelidoResult.isValid || !cpfResult.isValid ||
                    !phoneResult.isValid || !modalityResult.isValid || !attendanceResult.isValid ||
                    !paymentResult.isValid || !lastMonthResult.isValid
                ) return@TextButton

                val client = SportClient(
                    id = "sport_${System.currentTimeMillis()}",
                    name = name.trim(),
                    apelido = apelido.trim(),
                    cpf = cpfDigits,
                    phone = phoneDigits,
                    modality = selectedModality!!,
                    attendance = selectedAttendance!!,
                    paymentMethod = selectedPayment!!,
                    cashAmountCents = cashAmountCents ?: 0L,
                    lastPaymentMonth = formattedMonth,
                )
                onConfirm(client)
            }) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}

// =========================================================================
// Edit / Detail Dialog
// =========================================================================

@Composable
private fun EditSportClientDialog(
    client: SportClient,
    onDismiss: () -> Unit,
    onUpdate: (SportClient) -> Unit,
    onDelete: (String) -> Unit,
) {
    var name by remember(client.id) { mutableStateOf(client.name) }
    var apelido by remember(client.id) { mutableStateOf(client.apelido) }
    var cpfDigits by remember(client.id) { mutableStateOf(client.cpf) }
    var phoneDigits by remember(client.id) { mutableStateOf(client.phone) }
    var selectedModality by remember(client.id) { mutableStateOf<SportModality?>(client.modality) }
    var selectedAttendance by remember(client.id) { mutableStateOf<Int?>(client.attendance) }
    var selectedPayment by remember(client.id) { mutableStateOf<PaymentMethod?>(client.paymentMethod) }
    var cashDigits by remember(client.id) { mutableStateOf(centsToDigitString(client.cashAmountCents)) }
    var lastPaymentMonthDigits by remember(client.id) {
        mutableStateOf(client.lastPaymentMonth.filter { it.isDigit() })
    }

    var nameError by remember { mutableStateOf<String?>(null) }
    var apelidoError by remember { mutableStateOf<String?>(null) }
    var cpfError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var modalityError by remember { mutableStateOf<String?>(null) }
    var attendanceError by remember { mutableStateOf<String?>(null) }
    var paymentError by remember { mutableStateOf<String?>(null) }
    var cashError by remember { mutableStateOf<String?>(null) }
    var lastPaymentMonthError by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Excluir Cliente") },
            text = { Text("Deseja realmente excluir \"${client.name}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmation = false
                    onDelete(client.id)
                }) {
                    Text(
                        "Excluir",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancelar")
                }
            },
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Editar Cliente", modifier = Modifier.weight(1f))
                IconButton(onClick = { showDeleteConfirmation = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        text = {
            SportClientFormFields(
                name = name,
                onNameChange = { name = it; nameError = null },
                nameError = nameError,
                apelido = apelido,
                onApelidoChange = { apelido = it; apelidoError = null },
                apelidoError = apelidoError,
                cpfDigits = cpfDigits,
                onCpfChange = { cpfDigits = filterDigitsOnly(it, 11); cpfError = null },
                cpfError = cpfError,
                phoneDigits = phoneDigits,
                onPhoneChange = { phoneDigits = filterDigitsOnly(it, 11); phoneError = null },
                phoneError = phoneError,
                selectedModality = selectedModality,
                onModalityChange = { selectedModality = it; modalityError = null },
                modalityError = modalityError,
                selectedAttendance = selectedAttendance,
                onAttendanceChange = { selectedAttendance = it; attendanceError = null },
                attendanceError = attendanceError,
                selectedPayment = selectedPayment,
                onPaymentChange = { selectedPayment = it; paymentError = null; cashError = null },
                paymentError = paymentError,
                cashDigits = cashDigits,
                onCashChange = { cashDigits = filterDigitsOnly(it, 10); cashError = null },
                cashError = cashError,
                lastPaymentMonth = lastPaymentMonthDigits,
                onLastPaymentMonthChange = {
                    lastPaymentMonthDigits = filterDigitsOnly(it, 6)
                    lastPaymentMonthError = null
                },
                lastPaymentMonthError = lastPaymentMonthError,
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val nameResult = SportClientValidator.validateName(name)
                val apelidoResult = SportClientValidator.validateApelido(apelido)
                val cpfResult = SportClientValidator.validateCpf(cpfDigits)
                val phoneResult = SportClientValidator.validatePhone(phoneDigits)
                val modalityResult = SportClientValidator.validateModalidade(selectedModality)
                val attendanceResult = SportClientValidator.validateAttendance(selectedAttendance)
                val paymentResult = SportClientValidator.validatePaymentMethod(selectedPayment)
                val formattedMonth = formatMonthYearDigits(lastPaymentMonthDigits)
                val lastMonthResult = SportClientValidator.validateLastPaymentMonth(formattedMonth)

                nameError = nameResult.errorMessage
                apelidoError = apelidoResult.errorMessage
                cpfError = cpfResult.errorMessage
                phoneError = phoneResult.errorMessage
                modalityError = modalityResult.errorMessage
                attendanceError = attendanceResult.errorMessage
                paymentError = paymentResult.errorMessage
                lastPaymentMonthError = lastMonthResult.errorMessage

                val cashAmountCents = parseCurrencyDigits(cashDigits)
                if (selectedPayment != null) {
                    val cashResult = SportClientValidator.validateCashAmount(
                        cashAmountCents,
                        selectedPayment!!,
                    )
                    cashError = cashResult.errorMessage
                    if (!cashResult.isValid) return@TextButton
                }

                if (!nameResult.isValid || !apelidoResult.isValid || !cpfResult.isValid ||
                    !phoneResult.isValid || !modalityResult.isValid || !attendanceResult.isValid ||
                    !paymentResult.isValid || !lastMonthResult.isValid
                ) return@TextButton

                onUpdate(
                    client.copy(
                        name = name.trim(),
                        apelido = apelido.trim(),
                        cpf = cpfDigits,
                        phone = phoneDigits,
                        modality = selectedModality!!,
                        attendance = selectedAttendance!!,
                        paymentMethod = selectedPayment!!,
                        cashAmountCents = cashAmountCents ?: 0L,
                        lastPaymentMonth = formattedMonth,
                    ),
                )
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}
