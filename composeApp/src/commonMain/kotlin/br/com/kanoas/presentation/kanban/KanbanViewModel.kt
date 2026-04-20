package br.com.kanoas.presentation.kanban

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.kanoas.shared.core.mvi.MviViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KanbanViewModel : ViewModel(), MviViewModel<KanbanState, KanbanIntent, KanbanEffect> {

    private val _state = MutableStateFlow(KanbanState())
    override val state: StateFlow<KanbanState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<KanbanEffect>()
    override val effects: SharedFlow<KanbanEffect> = _effects.asSharedFlow()

    override fun handleIntent(intent: KanbanIntent) {
        when (intent) {
            is KanbanIntent.LoadBoard -> {
                val defaultColumns = listOf(
                    KanbanColumn(id = "col_todo", title = "A Fazer"),
                    KanbanColumn(id = "col_progress", title = "Em Progresso"),
                    KanbanColumn(id = "col_done", title = "Concluído"),
                )
                _state.value = _state.value.copy(
                    boardId = "board_default",
                    columns = defaultColumns,
                    isLoading = false,
                )
                recomputeFiltered()
            }

            is KanbanIntent.AddTaskClicked -> {
                _state.value = _state.value.copy(isAddTaskDialogVisible = true)
                viewModelScope.launch {
                    _effects.emit(KanbanEffect.OpenAddTaskDialog)
                }
            }

            is KanbanIntent.DismissAddTaskDialog -> {
                _state.value = _state.value.copy(isAddTaskDialogVisible = false)
            }

            is KanbanIntent.MoveTask -> {
                val tasks = _state.value.tasksByColumn.toMutableMap()
                var movedTask: KanbanTask? = null
                for ((colId, taskList) in tasks) {
                    val task = taskList.find { it.id == intent.taskId }
                    if (task != null) {
                        movedTask = task.copy(columnId = intent.targetColumnId)
                        tasks[colId] = taskList.filter { it.id != intent.taskId }
                        break
                    }
                }
                if (movedTask != null) {
                    val targetTasks = tasks[intent.targetColumnId].orEmpty()
                    tasks[intent.targetColumnId] = targetTasks + movedTask
                    _state.value = _state.value.copy(tasksByColumn = tasks)
                    recomputeFiltered()
                }
            }

            is KanbanIntent.SearchQueryChanged -> {
                _state.value = _state.value.copy(searchQuery = intent.query)
                recomputeFiltered()
            }

            is KanbanIntent.SettingsClicked -> {
                viewModelScope.launch {
                    _effects.emit(KanbanEffect.NavigateToSettings)
                }
            }

            is KanbanIntent.AddTestTasks -> {
                _state.value = _state.value.copy(tasksByColumn = intent.tasks)
                recomputeFiltered()
            }
        }
    }

    /**
     * Recomputa [KanbanState.filteredTasksByColumn] com base na searchQuery.
     * Busca case-insensitive, match em qualquer posição da string.
     */
    private fun recomputeFiltered() {
        val s = _state.value
        val query = s.searchQuery.trim()
        val filtered = if (query.isEmpty()) {
            s.tasksByColumn
        } else {
            s.tasksByColumn.mapValues { (_, tasks) ->
                tasks.filter { it.name.contains(query, ignoreCase = true) }
            }
        }
        _state.value = s.copy(filteredTasksByColumn = filtered)
    }
}
