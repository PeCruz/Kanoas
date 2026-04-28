package br.com.sprena.presentation.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.sprena.shared.core.mvi.MviViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventosViewModel(
    todayEpochDay: Long = System.currentTimeMillis() / 86_400_000L,
) : ViewModel(), MviViewModel<EventosState, EventosIntent, EventosEffect> {

    private var eventIdCounter = 0L

    private val _state: MutableStateFlow<EventosState>

    init {
        val (year, month) = yearMonthFromEpochDay(todayEpochDay)
        _state = MutableStateFlow(
            EventosState(
                todayEpochDay = todayEpochDay,
                filterMonth = month,
                filterYear = year,
            ),
        )
    }
    override val state: StateFlow<EventosState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<EventosEffect>()
    override val effects: SharedFlow<EventosEffect> = _effects.asSharedFlow()

    override fun handleIntent(intent: EventosIntent) {
        when (intent) {
            is EventosIntent.TabSelected -> {
                _state.value = _state.value.copy(selectedTab = intent.tab)
                recomputeFiltered()
            }

            is EventosIntent.SearchQueryChanged -> {
                _state.value = _state.value.copy(searchQuery = intent.query)
                recomputeFiltered()
            }

            is EventosIntent.AddEventClicked -> {
                viewModelScope.launch {
                    _effects.emit(EventosEffect.NavigateToCreateEvent)
                }
            }

            is EventosIntent.EventClicked -> {
                viewModelScope.launch {
                    _effects.emit(EventosEffect.NavigateToEditEvent(intent.event))
                }
            }

            is EventosIntent.EventCreated -> {
                val newEvent = Event(
                    id = "event_${++eventIdCounter}",
                    name = intent.name,
                    category = intent.category,
                    dateEpochDay = intent.dateEpochDay,
                    contact = intent.contact,
                    description = intent.description,
                )
                _state.value = _state.value.copy(
                    events = _state.value.events + newEvent,
                )
                moveExpiredToRealizados()
                recomputeFiltered()
            }

            is EventosIntent.EventUpdated -> {
                val events = _state.value.events.map { event ->
                    if (event.id == intent.eventId) {
                        event.copy(
                            name = intent.name,
                            category = intent.category,
                            dateEpochDay = intent.dateEpochDay,
                            contact = intent.contact,
                            description = intent.description,
                            originalCategory = null,
                        )
                    } else {
                        event
                    }
                }
                _state.value = _state.value.copy(events = events)
                moveExpiredToRealizados()
                recomputeFiltered()
            }

            is EventosIntent.EventDeleted -> {
                _state.value = _state.value.copy(
                    events = _state.value.events.filter { it.id != intent.eventId },
                )
                recomputeFiltered()
            }

            is EventosIntent.SetTodayEpochDay -> {
                _state.value = _state.value.copy(todayEpochDay = intent.todayEpochDay)
                moveExpiredToRealizados()
                recomputeFiltered()
            }

            is EventosIntent.ErrorOccurred -> {
                viewModelScope.launch {
                    _effects.emit(EventosEffect.ShowError(intent.message))
                }
            }

            is EventosIntent.DatePickerFilterChanged -> {
                _state.value = _state.value.copy(filterDateEpochDay = intent.dateEpochDay)
                recomputeFiltered()
            }

            is EventosIntent.ClearDatePickerFilter -> {
                _state.value = _state.value.copy(filterDateEpochDay = null)
                recomputeFiltered()
            }

            is EventosIntent.MonthNavigatedForward -> {
                val s = _state.value
                val newMonth = if (s.filterMonth == 12) 1 else s.filterMonth + 1
                val newYear = if (s.filterMonth == 12) s.filterYear + 1 else s.filterYear
                _state.value = s.copy(filterMonth = newMonth, filterYear = newYear)
                recomputeFiltered()
            }

            is EventosIntent.MonthNavigatedBack -> {
                val s = _state.value
                val newMonth = if (s.filterMonth == 1) 12 else s.filterMonth - 1
                val newYear = if (s.filterMonth == 1) s.filterYear - 1 else s.filterYear
                _state.value = s.copy(filterMonth = newMonth, filterYear = newYear)
                recomputeFiltered()
            }

            is EventosIntent.AddTestEvents -> {
                _state.value = _state.value.copy(events = intent.events)
                moveExpiredToRealizados()
                recomputeFiltered()
            }
        }
    }

    /**
     * Move eventos expirados (dateEpochDay < todayEpochDay) para REALIZADOS.
     * Preserva a categoria original em [Event.originalCategory].
     */
    private fun moveExpiredToRealizados() {
        val s = _state.value
        val today = s.todayEpochDay
        if (today == 0L) return // today not set yet

        val updated = s.events.map { event ->
            if (event.category != EventCategory.REALIZADOS && event.dateEpochDay < today) {
                event.copy(
                    originalCategory = event.originalCategory ?: event.category,
                    category = EventCategory.REALIZADOS,
                )
            } else {
                event
            }
        }
        _state.value = s.copy(events = updated)
    }

    /**
     * Recomputa [EventosState.filteredEvents] e [EventosState.tabCounts].
     *
     * Filtros aplicados em cadeia (intersecao):
     * 1. Tab ou busca cross-tab
     * 2. Filtro de mes (month navigator) — aplica globalmente
     * 3. Filtro de data especifica (date picker) — aplica globalmente
     */
    private fun recomputeFiltered() {
        val s = _state.value
        val query = s.searchQuery.trim()
        val isSearchActive = query.isNotEmpty()

        val hasDateFilter = s.filterDateEpochDay != null

        // Step 1: tab or search filter
        // Search bypasses all date filters — only matches by name.
        // Date picker overrides tab to show all tabs.
        var filtered = if (isSearchActive) {
            s.events.filter { it.name.contains(query, ignoreCase = true) }
        } else if (hasDateFilter) {
            s.events // date picker searches across all tabs
        } else {
            s.events.filter { it.category == s.selectedTab }
        }

        // Step 2 & 3: date filters only apply when NOT searching
        if (!isSearchActive) {
            // Month filter (always active when not searching)
            if (s.filterMonth != 0 && s.filterYear != 0) {
                filtered = filtered.filter { event ->
                    val (year, month) = yearMonthFromEpochDay(event.dateEpochDay)
                    year == s.filterYear && month == s.filterMonth
                }
            }

            // Date picker filter (when active)
            if (hasDateFilter) {
                filtered = filtered.filter { it.dateEpochDay == s.filterDateEpochDay }
            }
        }

        // Ordena por data (mais proximo do hoje primeiro)
        val sorted = filtered.sortedBy { it.dateEpochDay }

        // Conta eventos por tab (incluindo REALIZADOS)
        val counts = s.events
            .groupBy { it.category }
            .mapValues { (_, events) -> events.size }

        _state.value = s.copy(
            isSearchActive = isSearchActive,
            filteredEvents = sorted,
            tabCounts = counts,
        )
    }
}
