package br.com.sprena.presentation.eventos

import br.com.sprena.shared.core.mvi.UiState

/**
 * State da tela Eventos.
 *
 * @property tabs lista ordenada das tabs disponiveis.
 * @property selectedTab tab atualmente selecionada.
 * @property events todos os eventos (fonte da verdade).
 * @property searchQuery texto digitado na busca.
 * @property isSearchActive true quando searchQuery nao esta vazia.
 * @property filteredEvents eventos filtrados (por tab ou por busca cross-tab), ordenados por data.
 * @property tabCounts contagem de eventos ativos por tab (exclui REALIZADOS).
 * @property todayEpochDay dia atual em epoch days (para comparar com event dates).
 * @property isLoading indica carregamento de dados.
 * @property error mensagem de erro.
 */
data class EventosState(
    val tabs: List<EventCategory> = listOf(
        EventCategory.EVENTOS,
        EventCategory.ALUGUEL,
        EventCategory.DAY_USE,
        EventCategory.REALIZADOS,
    ),
    val selectedTab: EventCategory = EventCategory.EVENTOS,
    val events: List<Event> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val filteredEvents: List<Event> = emptyList(),
    val tabCounts: Map<EventCategory, Int> = emptyMap(),
    val todayEpochDay: Long = 0L,
    val filterDateEpochDay: Long? = null,
    val filterMonth: Int = 0,
    val filterYear: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
) : UiState
