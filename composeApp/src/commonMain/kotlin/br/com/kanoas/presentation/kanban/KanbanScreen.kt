package br.com.kanoas.presentation.kanban

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.kanoas.core.ui.components.ThemeToggleButton
import br.com.kanoas.presentation.core.theme.ThemeViewModel

/**
 * Tela principal do Kanban — header fixo com busca + add + theme + settings,
 * colunas horizontais com cards de tarefas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanbanScreen(
    viewModel: KanbanViewModel,
    themeViewModel: ThemeViewModel,
    onNavigateSettings: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(KanbanIntent.LoadBoard)
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is KanbanEffect.OpenAddTaskDialog -> { /* dialog controlled via state */ }
                is KanbanEffect.NavigateToSettings -> onNavigateSettings()
                is KanbanEffect.ShowError -> { /* TODO: snackbar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Search input com ícone de lupa
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = {
                            viewModel.handleIntent(KanbanIntent.SearchQueryChanged(it))
                        },
                        placeholder = { Text("Buscar tarefas...") },
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
                    // Add task button
                    IconButton(
                        onClick = { viewModel.handleIntent(KanbanIntent.AddTaskClicked) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adicionar tarefa",
                            tint = MaterialTheme.colorScheme.tertiary,
                        )
                    }

                    // Theme toggle
                    ThemeToggleButton(themeViewModel = themeViewModel)

                    // Settings icon
                    IconButton(
                        onClick = { viewModel.handleIntent(KanbanIntent.SettingsClicked) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
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
            val displayTasks = state.filteredTasksByColumn

            LazyRow(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.columns, key = { it.id }) { column ->
                    KanbanColumnCard(
                        column = column,
                        tasks = displayTasks[column.id].orEmpty(),
                    )
                }
            }
        }
    }
}

@Composable
private fun KanbanColumnCard(
    column: KanbanColumn,
    tasks: List<KanbanTask>,
) {
    Card(
        modifier = Modifier.width(240.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // --- Column header ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = column.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${tasks.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Tasks (scrollable within column) ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f, fill = false),
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(task = task)
                }
            }

            // Espaço inferior para evitar sobreposição
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Nenhuma tarefa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCard(task: KanbanTask) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            PriorityBadge(priority = task.priority)
        }
    }
}

@Composable
private fun PriorityBadge(priority: Int) {
    val (label, color) = when (priority) {
        1 -> "Muito Baixa" to MaterialTheme.colorScheme.outline
        2 -> "Baixa" to MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
        3 -> "Média" to MaterialTheme.colorScheme.tertiary
        4 -> "Alta" to MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
        5 -> "Urgente" to MaterialTheme.colorScheme.error
        else -> "—" to MaterialTheme.colorScheme.outline
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(4.dp),
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}
