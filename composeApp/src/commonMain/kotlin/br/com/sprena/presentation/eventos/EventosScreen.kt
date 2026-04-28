package br.com.sprena.presentation.eventos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.sprena.core.ui.components.ThemeToggleButton
import br.com.sprena.presentation.core.theme.ThemeViewModel
import kotlinx.coroutines.launch

/**
 * Tela principal de Eventos — search input no topo, MD3 scrollable tabs
 * (Eventos, Aluguel, Day Use, Realizados), lista flat de cards ordenada por data,
 * FAB para criar, cards com cores (verde=ativo, vermelho/transparente=expirado).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(
    viewModel: EventosViewModel,
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier,
    onNavigateCreateEvent: () -> Unit = {},
    onNavigateEditEvent: (Event) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is EventosEffect.NavigateToCreateEvent -> onNavigateCreateEvent()
                is EventosEffect.NavigateToEditEvent -> onNavigateEditEvent(effect.event)
                is EventosEffect.ShowError -> {
                    scope.launch { snackbarHostState.showSnackbar(effect.message) }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = {
                            viewModel.handleIntent(EventosIntent.SearchQueryChanged(it))
                        },
                        placeholder = { Text("Buscar eventos...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
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
                onClick = {
                    viewModel.handleIntent(EventosIntent.AddEventClicked)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Criar evento",
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // --- Date Filters: DatePicker + Month Navigator ---
            DateFilterRow(
                filterDateEpochDay = state.filterDateEpochDay,
                filterMonth = state.filterMonth,
                filterYear = state.filterYear,
                onDatePickerSelected = { epochDay ->
                    viewModel.handleIntent(EventosIntent.DatePickerFilterChanged(epochDay))
                },
                onClearDatePicker = {
                    viewModel.handleIntent(EventosIntent.ClearDatePickerFilter)
                },
                onMonthBack = {
                    viewModel.handleIntent(EventosIntent.MonthNavigatedBack)
                },
                onMonthForward = {
                    viewModel.handleIntent(EventosIntent.MonthNavigatedForward)
                },
            )

            // --- MD3 Scrollable Tabs with badge counts ---
            val tabIndex = state.tabs.indexOf(state.selectedTab).coerceAtLeast(0)

            ScrollableTabRow(
                selectedTabIndex = tabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp,
            ) {
                state.tabs.forEachIndexed { index, category ->
                    val count = state.tabCounts[category] ?: 0
                    Tab(
                        selected = tabIndex == index,
                        onClick = {
                            viewModel.handleIntent(EventosIntent.TabSelected(category))
                        },
                        text = {
                            if (count > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge {
                                            Text(count.toString())
                                        }
                                    },
                                ) {
                                    Text(category.label)
                                }
                            } else {
                                Text(category.label)
                            }
                        },
                    )
                }
            }

            // --- Event List ---
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.filteredEvents.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (state.isSearchActive) {
                            "Nenhum evento encontrado"
                        } else {
                            "Nenhum evento"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.filteredEvents, key = { it.id }) { event ->
                        EventCard(
                            event = event,
                            todayEpochDay = state.todayEpochDay,
                            onClick = {
                                viewModel.handleIntent(EventosIntent.EventClicked(event))
                            },
                        )
                    }
                }
            }
        }
    }
}

/**
 * Linha de filtros de data: date picker (esquerda) + navegador de mes (direita).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateFilterRow(
    filterDateEpochDay: Long?,
    filterMonth: Int,
    filterYear: Int,
    onDatePickerSelected: (Long) -> Unit,
    onClearDatePicker: () -> Unit,
    onMonthBack: () -> Unit,
    onMonthForward: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // --- Date Picker Field (left) ---
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (filterDateEpochDay != null) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            },
            modifier = Modifier
                .weight(1f)
                .clickable { showDatePicker = true },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Filtrar por data",
                    modifier = Modifier.size(18.dp),
                    tint = if (filterDateEpochDay != null) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = filterDateEpochDay?.let { formatEpochDay(it) } ?: "Filtrar data",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (filterDateEpochDay != null) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
                if (filterDateEpochDay != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onClearDatePicker,
                        modifier = Modifier.size(18.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpar filtro de data",
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // --- Month Navigator (right) — ◀ Mes/Ano ▶ ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onMonthBack, modifier = Modifier.size(32.dp)) {
                Text("◀", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "${monthName(filterMonth)}/$filterYear",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(90.dp),
            )
            IconButton(onClick = onMonthForward, modifier = Modifier.size(32.dp)) {
                Text("▶", style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    // --- DatePickerDialog ---
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = filterDateEpochDay?.let { it * 86_400_000L },
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val epochDay = millis / 86_400_000L
                            onDatePickerSelected(epochDay)
                        }
                        showDatePicker = false
                    },
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * Retorna nome abreviado do mes em portugues.
 */
private fun monthName(month: Int): String = when (month) {
    1 -> "Jan"
    2 -> "Fev"
    3 -> "Mar"
    4 -> "Abr"
    5 -> "Mai"
    6 -> "Jun"
    7 -> "Jul"
    8 -> "Ago"
    9 -> "Set"
    10 -> "Out"
    11 -> "Nov"
    12 -> "Dez"
    else -> "---"
}

/**
 * Card de evento — exibe nome, data e badge de categoria.
 * Verde para eventos ativos, vermelho/transparente para expirados.
 */
@Composable
private fun EventCard(
    event: Event,
    todayEpochDay: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isExpired = event.dateEpochDay < todayEpochDay
    val cardAlpha = if (isExpired) 0.55f else 1f
    val borderColor = if (isExpired) {
        MaterialTheme.colorScheme.error
    } else {
        Color(0xFF2E7D32) // green
    }
    val containerColor = if (isExpired) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
    } else {
        Color(0xFF2E7D32).copy(alpha = 0.08f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = borderColor.copy(alpha = cardAlpha),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpired) 0.dp else 2.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .then(if (isExpired) Modifier else Modifier),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    color = if (isExpired) {
                        MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                Spacer(modifier = Modifier.width(8.dp))
                CategoryBadge(
                    category = event.originalCategory ?: event.category,
                    isExpired = isExpired,
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = formatEpochDay(event.dateEpochDay),
                style = MaterialTheme.typography.bodySmall,
                color = if (isExpired) {
                    MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                } else {
                    Color(0xFF2E7D32).copy(alpha = 0.8f)
                },
            )

            if (event.contact != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = event.contact,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = if (isExpired) 0.5f else 0.8f,
                    ),
                )
            }
        }
    }
}

/**
 * Badge com o nome da categoria (tab) do evento.
 */
@Composable
private fun CategoryBadge(
    category: EventCategory,
    isExpired: Boolean = false,
) {
    val color = when (category) {
        EventCategory.EVENTOS -> MaterialTheme.colorScheme.primary
        EventCategory.ALUGUEL -> MaterialTheme.colorScheme.tertiary
        EventCategory.DAY_USE -> MaterialTheme.colorScheme.secondary
        EventCategory.REALIZADOS -> MaterialTheme.colorScheme.outline
    }
    val alpha = if (isExpired) 0.5f else 1f
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = alpha),
    ) {
        Text(
            text = category.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = alpha),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

/**
 * Formata epoch day (dias desde 1970-01-01) para "dd/MM/yyyy".
 * Usa algoritmo civil-from-days para evitar dependencia de kotlinx-datetime.
 */
internal fun formatEpochDay(epochDay: Long): String {
    val z = epochDay + 719468
    val era = (if (z >= 0) z else z - 146096) / 146097
    val doe = (z - era * 146097)
    val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
    val y = yoe + era * 400
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val d = doy - (153 * mp + 2) / 5 + 1
    val m = mp + (if (mp < 10) 3 else -9)
    val year = y + (if (m <= 2) 1 else 0)

    val day = d.toString().padStart(2, '0')
    val month = m.toString().padStart(2, '0')
    return "$day/$month/$year"
}

/**
 * Retorna (year, month) de um epoch day usando o mesmo algoritmo civil-from-days.
 */
internal fun yearMonthFromEpochDay(epochDay: Long): Pair<Int, Int> {
    val z = epochDay + 719468
    val era = (if (z >= 0) z else z - 146096) / 146097
    val doe = (z - era * 146097)
    val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
    val y = yoe + era * 400
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val m = mp + (if (mp < 10) 3 else -9)
    val year = (y + (if (m <= 2) 1 else 0)).toInt()
    return year to m.toInt()
}
