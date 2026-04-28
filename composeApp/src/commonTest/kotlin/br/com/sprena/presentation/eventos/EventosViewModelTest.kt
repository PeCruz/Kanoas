package br.com.sprena.presentation.eventos

import app.cash.turbine.test
import br.com.sprena.test.MainDispatcherEnv
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * TDD — Testes da tela Eventos.
 *
 * A tela possui 4 tabs: Eventos, Aluguel, Day Use, Realizados.
 * Busca cross-tab com indicacao de categoria.
 * FAB para criar evento em qualquer tab.
 * Eventos expirados movem automaticamente para Realizados.
 * Contagem de eventos ativos por tab (badge).
 * Eventos ordenados por data (mais proximo primeiro).
 */
class EventosViewModelTest {

    private val env = MainDispatcherEnv()

    @BeforeTest
    fun setUp() = env.install()

    @AfterTest
    fun tearDown() = env.uninstall()

    // =========================================================================
    // Estado Inicial
    // =========================================================================

    @Test
    fun `initial state has Eventos as selected tab`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        assertEquals(EventCategory.EVENTOS, vm.state.first().selectedTab)
    }

    @Test
    fun `initial state has no events`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        assertTrue(vm.state.first().events.isEmpty())
    }

    @Test
    fun `initial state has empty search query`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        assertEquals("", vm.state.first().searchQuery)
    }

    @Test
    fun `initial state is not loading`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        assertFalse(vm.state.first().isLoading)
    }

    @Test
    fun `initial state has all four tabs available`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        val tabs = vm.state.first().tabs
        assertEquals(4, tabs.size)
        assertEquals(EventCategory.EVENTOS, tabs[0])
        assertEquals(EventCategory.ALUGUEL, tabs[1])
        assertEquals(EventCategory.DAY_USE, tabs[2])
        assertEquals(EventCategory.REALIZADOS, tabs[3])
    }

    @Test
    fun `initial state stores todayEpochDay`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        assertEquals(20000L, vm.state.first().todayEpochDay)
    }

    // =========================================================================
    // Selecao de Tab
    // =========================================================================

    @Test
    fun `TabSelected changes selected tab`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.handleIntent(EventosIntent.TabSelected(EventCategory.ALUGUEL))
        assertEquals(EventCategory.ALUGUEL, vm.state.first().selectedTab)
    }

    @Test
    fun `TabSelected to DayUse works`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.handleIntent(EventosIntent.TabSelected(EventCategory.DAY_USE))
        assertEquals(EventCategory.DAY_USE, vm.state.first().selectedTab)
    }

    @Test
    fun `TabSelected to Realizados works`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.handleIntent(EventosIntent.TabSelected(EventCategory.REALIZADOS))
        assertEquals(EventCategory.REALIZADOS, vm.state.first().selectedTab)
    }

    @Test
    fun `TabSelected back to Eventos works`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.handleIntent(EventosIntent.TabSelected(EventCategory.ALUGUEL))
        vm.handleIntent(EventosIntent.TabSelected(EventCategory.EVENTOS))
        assertEquals(EventCategory.EVENTOS, vm.state.first().selectedTab)
    }

    // =========================================================================
    // Exibicao de eventos por tab (sem busca)
    // =========================================================================

    @Test
    fun `filteredEvents shows only events of selected tab when no search`() = runTest {
        val vm = createVmWithActiveEvents()
        val filtered = vm.state.first().filteredEvents
        assertTrue(filtered.all { it.category == EventCategory.EVENTOS })
        assertTrue(filtered.isNotEmpty())
    }

    @Test
    fun `switching tab shows events of that tab`() = runTest {
        val vm = createVmWithActiveEvents()
        vm.handleIntent(EventosIntent.TabSelected(EventCategory.ALUGUEL))
        val filtered = vm.state.first().filteredEvents
        assertTrue(filtered.all { it.category == EventCategory.ALUGUEL })
        assertTrue(filtered.isNotEmpty())
    }

    @Test
    fun `DayUse tab shows only DayUse events`() = runTest {
        val vm = createVmWithActiveEvents()
        vm.handleIntent(EventosIntent.TabSelected(EventCategory.DAY_USE))
        val filtered = vm.state.first().filteredEvents
        assertTrue(filtered.all { it.category == EventCategory.DAY_USE })
        assertTrue(filtered.isNotEmpty())
    }

    // =========================================================================
    // Ordenacao — eventos mais proximos do hoje primeiro
    // =========================================================================

    @Test
    fun `events are sorted by date ascending`() = runTest {
        val vm = createVmWithActiveEvents()
        val filtered = vm.state.first().filteredEvents
        val dates = filtered.map { it.dateEpochDay }
        assertEquals(dates.sorted(), dates)
    }

    @Test
    fun `Aluguel events are sorted by date ascending`() = runTest {
        val vm = createVmWithActiveEvents()
        vm.handleIntent(EventosIntent.TabSelected(EventCategory.ALUGUEL))
        val filtered = vm.state.first().filteredEvents
        val dates = filtered.map { it.dateEpochDay }
        assertEquals(dates.sorted(), dates)
    }

    // =========================================================================
    // Busca cross-tab
    // =========================================================================

    @Test
    fun `SearchQueryChanged updates searchQuery`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.handleIntent(EventosIntent.SearchQueryChanged("festa"))
        assertEquals("festa", vm.state.first().searchQuery)
    }

    @Test
    fun `search filters events across all tabs`() = runTest {
        val vm = createVmWithActiveEvents()
        vm.handleIntent(EventosIntent.SearchQueryChanged("Pedro"))
        val state = vm.state.first()
        assertTrue(state.isSearchActive)
        val results = state.filteredEvents
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.name.contains("Pedro", ignoreCase = true) })
    }

    @Test
    fun `search is case insensitive`() = runTest {
        val vm = createVmWithActiveEvents()
        vm.handleIntent(EventosIntent.SearchQueryChanged("pedro"))
        val results = vm.state.first().filteredEvents
        assertTrue(results.any { it.name.contains("Pedro") })
    }

    @Test
    fun `search results include events from different categories`() = runTest {
        val vm = createVmWithActiveEvents()
        vm.handleIntent(EventosIntent.SearchQueryChanged("Pedro"))
        val results = vm.state.first().filteredEvents
        val categories = results.map { it.category }.toSet()
        assertTrue(categories.size > 1, "Search should find results across multiple tabs")
    }

    @Test
    fun `search with no match returns empty`() = runTest {
        val vm = createVmWithActiveEvents()
        vm.handleIntent(EventosIntent.SearchQueryChanged("xyz_nao_existe"))
        val results = vm.state.first().filteredEvents
        assertTrue(results.isEmpty())
    }

    @Test
    fun `clearing search returns to tab-filtered view`() = runTest {
        val vm = createVmWithActiveEvents()
        vm.handleIntent(EventosIntent.SearchQueryChanged("Pedro"))
        vm.handleIntent(EventosIntent.SearchQueryChanged(""))
        val state = vm.state.first()
        assertFalse(state.isSearchActive)
        assertTrue(state.filteredEvents.all { it.category == state.selectedTab })
    }

    // =========================================================================
    // Criacao de Evento
    // =========================================================================

    @Test
    fun `EventCreated adds event to the list`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.handleIntent(
            EventosIntent.EventCreated(
                name = "Festa de Natal",
                category = EventCategory.EVENTOS,
                dateEpochDay = 20010L,
                contact = null,
                description = null,
            ),
        )
        val events = vm.state.first().events
        assertEquals(1, events.size)
        assertEquals("Festa de Natal", events[0].name)
    }

    @Test
    fun `EventCreated with optional fields stores them`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.handleIntent(
            EventosIntent.EventCreated(
                name = "Aluguel Quadra",
                category = EventCategory.ALUGUEL,
                dateEpochDay = 20010L,
                contact = "11999998888",
                description = "Quadra poliesportiva",
            ),
        )
        val event = vm.state.first().events.first()
        assertEquals("11999998888", event.contact)
        assertEquals("Quadra poliesportiva", event.description)
    }

    @Test
    fun `EventCreated adds event to correct category tab`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.handleIntent(
            EventosIntent.EventCreated(
                name = "Day Use Piscina",
                category = EventCategory.DAY_USE,
                dateEpochDay = 20010L,
                contact = null,
                description = null,
            ),
        )
        vm.handleIntent(EventosIntent.TabSelected(EventCategory.DAY_USE))
        val filtered = vm.state.first().filteredEvents
        assertEquals(1, filtered.size)
        assertEquals("Day Use Piscina", filtered[0].name)
    }

    @Test
    fun `new event does not appear in other tabs`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.handleIntent(
            EventosIntent.EventCreated(
                name = "Festa Julina",
                category = EventCategory.EVENTOS,
                dateEpochDay = 20010L,
                contact = null,
                description = null,
            ),
        )
        vm.handleIntent(EventosIntent.TabSelected(EventCategory.ALUGUEL))
        val filtered = vm.state.first().filteredEvents
        assertTrue(filtered.isEmpty())
    }

    // =========================================================================
    // Edicao de Evento
    // =========================================================================

    @Test
    fun `EventUpdated modifies existing event`() = runTest {
        val vm = createVmWithActiveEvents()
        val eventToUpdate = vm.state.first().events.first()
        vm.handleIntent(
            EventosIntent.EventUpdated(
                eventId = eventToUpdate.id,
                name = "Nome Atualizado",
                category = eventToUpdate.category,
                dateEpochDay = eventToUpdate.dateEpochDay,
                contact = "11000001111",
                description = "Descricao atualizada",
            ),
        )
        val updated = vm.state.first().events.find { it.id == eventToUpdate.id }
        assertNotNull(updated)
        assertEquals("Nome Atualizado", updated.name)
        assertEquals("11000001111", updated.contact)
        assertEquals("Descricao atualizada", updated.description)
    }

    @Test
    fun `EventUpdated can change category`() = runTest {
        val vm = createVmWithActiveEvents()
        val eventToUpdate = vm.state.first().events.first { it.category == EventCategory.EVENTOS }
        vm.handleIntent(
            EventosIntent.EventUpdated(
                eventId = eventToUpdate.id,
                name = eventToUpdate.name,
                category = EventCategory.ALUGUEL,
                dateEpochDay = eventToUpdate.dateEpochDay,
                contact = eventToUpdate.contact,
                description = eventToUpdate.description,
            ),
        )
        val updated = vm.state.first().events.find { it.id == eventToUpdate.id }
        assertNotNull(updated)
        assertEquals(EventCategory.ALUGUEL, updated.category)
    }

    // =========================================================================
    // Exclusao de Evento
    // =========================================================================

    @Test
    fun `EventDeleted removes event`() = runTest {
        val vm = createVmWithActiveEvents()
        val eventsBefore = vm.state.first().events
        val eventToDelete = eventsBefore.first()
        vm.handleIntent(EventosIntent.EventDeleted(eventToDelete.id))
        val eventsAfter = vm.state.first().events
        assertFalse(eventsAfter.any { it.id == eventToDelete.id })
    }

    // =========================================================================
    // Realizados — eventos expirados auto-movidos
    // =========================================================================

    @Test
    fun `expired events are moved to REALIZADOS tab`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20010L)
        vm.handleIntent(
            EventosIntent.AddTestEvents(
                listOf(
                    Event("e1", "Evento Passado", EventCategory.EVENTOS, 20005L),
                    Event("e2", "Evento Futuro", EventCategory.EVENTOS, 20020L),
                ),
            ),
        )
        val events = vm.state.first().events
        val passado = events.find { it.id == "e1" }!!
        val futuro = events.find { it.id == "e2" }!!
        assertEquals(EventCategory.REALIZADOS, passado.category)
        assertEquals(EventCategory.EVENTOS, futuro.category)
    }

    @Test
    fun `expired events preserve originalCategory`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20010L)
        vm.handleIntent(
            EventosIntent.AddTestEvents(
                listOf(
                    Event("e1", "Aluguel Passado", EventCategory.ALUGUEL, 20005L),
                ),
            ),
        )
        val event = vm.state.first().events.first()
        assertEquals(EventCategory.REALIZADOS, event.category)
        assertEquals(EventCategory.ALUGUEL, event.originalCategory)
    }

    @Test
    fun `expired events appear in Realizados tab`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20010L)
        vm.handleIntent(
            EventosIntent.AddTestEvents(
                listOf(
                    Event("e1", "Passado 1", EventCategory.EVENTOS, 20005L),
                    Event("e2", "Passado 2", EventCategory.ALUGUEL, 20003L),
                    Event("e3", "Futuro", EventCategory.EVENTOS, 20020L),
                ),
            ),
        )
        vm.handleIntent(EventosIntent.TabSelected(EventCategory.REALIZADOS))
        val realizados = vm.state.first().filteredEvents
        assertEquals(2, realizados.size)
        assertTrue(realizados.all { it.category == EventCategory.REALIZADOS })
    }

    @Test
    fun `newly created expired event goes to Realizados`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20010L)
        vm.handleIntent(
            EventosIntent.EventCreated(
                name = "Evento Passado",
                category = EventCategory.EVENTOS,
                dateEpochDay = 20005L,
                contact = null,
                description = null,
            ),
        )
        val event = vm.state.first().events.first()
        assertEquals(EventCategory.REALIZADOS, event.category)
        assertEquals(EventCategory.EVENTOS, event.originalCategory)
    }

    @Test
    fun `SetTodayEpochDay moves events that become expired`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.handleIntent(
            EventosIntent.AddTestEvents(
                listOf(
                    Event("e1", "Soon", EventCategory.EVENTOS, 20005L),
                ),
            ),
        )
        // Event is still active
        assertEquals(EventCategory.EVENTOS, vm.state.first().events.first().category)

        // Time advances past the event date
        vm.handleIntent(EventosIntent.SetTodayEpochDay(20010L))
        assertEquals(EventCategory.REALIZADOS, vm.state.first().events.first().category)
    }

    // =========================================================================
    // Tab Counts — contagem de eventos ativos por tab
    // =========================================================================

    @Test
    fun `tabCounts counts only active events per category`() = runTest {
        val vm = createVmWithActiveEvents()
        val counts = vm.state.first().tabCounts
        assertEquals(2, counts[EventCategory.EVENTOS])
        assertEquals(2, counts[EventCategory.ALUGUEL])
        assertEquals(2, counts[EventCategory.DAY_USE])
    }

    @Test
    fun `tabCounts excludes expired events`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20010L)
        vm.handleIntent(
            EventosIntent.AddTestEvents(
                listOf(
                    Event("e1", "Passado", EventCategory.EVENTOS, 20005L),
                    Event("e2", "Futuro", EventCategory.EVENTOS, 20020L),
                ),
            ),
        )
        val counts = vm.state.first().tabCounts
        // Only the future event counts
        assertEquals(1, counts[EventCategory.EVENTOS])
    }

    @Test
    fun `tabCounts includes REALIZADOS key for expired events`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20010L)
        vm.handleIntent(
            EventosIntent.AddTestEvents(
                listOf(
                    Event("e1", "Passado", EventCategory.EVENTOS, 20005L),
                ),
            ),
        )
        val counts = vm.state.first().tabCounts
        assertEquals(1, counts[EventCategory.REALIZADOS])
    }

    // =========================================================================
    // Efeitos
    // =========================================================================

    @Test
    fun `AddEventClicked emits NavigateToCreateEvent effect`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.effects.test {
            vm.handleIntent(EventosIntent.AddEventClicked)
            assertEquals(EventosEffect.NavigateToCreateEvent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `EventClicked emits NavigateToEditEvent effect`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        val event = Event("e1", "Test", EventCategory.EVENTOS, 20010L)
        vm.effects.test {
            vm.handleIntent(EventosIntent.EventClicked(event))
            val effect = awaitItem()
            assertTrue(effect is EventosEffect.NavigateToEditEvent)
            assertEquals(event, (effect as EventosEffect.NavigateToEditEvent).event)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ShowError effect is emitted on error`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.effects.test {
            vm.handleIntent(EventosIntent.ErrorOccurred("Erro de rede"))
            val effect = awaitItem()
            assertTrue(effect is EventosEffect.ShowError)
            assertEquals("Erro de rede", (effect as EventosEffect.ShowError).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // =========================================================================
    // Filtro por Data — DatePickerFilter
    // =========================================================================

    @Test
    fun `initial state has no date picker filter`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        val state = vm.state.first()
        assertNull(state.filterDateEpochDay)
    }

    @Test
    fun `DatePickerFilterChanged sets filter date`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.handleIntent(EventosIntent.DatePickerFilterChanged(20010L))
        assertEquals(20010L, vm.state.first().filterDateEpochDay)
    }

    @Test
    fun `date picker filter shows only events on that exact date across all tabs`() = runTest {
        val vm = createVmWithActiveEvents()
        // e1 has date 20010L (EVENTOS), e3 has date 20015L (ALUGUEL)
        vm.handleIntent(EventosIntent.DatePickerFilterChanged(20010L))
        val filtered = vm.state.first().filteredEvents
        assertEquals(1, filtered.size)
        assertEquals("e1", filtered[0].id)
    }

    @Test
    fun `ClearDatePickerFilter removes date filter`() = runTest {
        val vm = createVmWithActiveEvents()
        vm.handleIntent(EventosIntent.DatePickerFilterChanged(20010L))
        vm.handleIntent(EventosIntent.ClearDatePickerFilter)
        assertNull(vm.state.first().filterDateEpochDay)
    }

    // =========================================================================
    // Filtro por Mes — MonthNavigator
    // =========================================================================

    @Test
    fun `initial state has current month and year from todayEpochDay`() = runTest {
        // 20000 epoch days = 2024-10-04
        val vm = EventosViewModel(todayEpochDay = 20000L)
        val state = vm.state.first()
        assertEquals(10, state.filterMonth)
        assertEquals(2024, state.filterYear)
    }

    @Test
    fun `MonthNavigatedForward advances month`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L) // Oct 2024
        vm.handleIntent(EventosIntent.MonthNavigatedForward)
        val state = vm.state.first()
        assertEquals(11, state.filterMonth)
        assertEquals(2024, state.filterYear)
    }

    @Test
    fun `MonthNavigatedForward from December wraps to January next year`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L) // Oct 2024
        // Go to Nov, Dec, then Jan
        vm.handleIntent(EventosIntent.MonthNavigatedForward)
        vm.handleIntent(EventosIntent.MonthNavigatedForward)
        vm.handleIntent(EventosIntent.MonthNavigatedForward)
        val state = vm.state.first()
        assertEquals(1, state.filterMonth)
        assertEquals(2025, state.filterYear)
    }

    @Test
    fun `MonthNavigatedBack goes to previous month`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L) // Oct 2024
        vm.handleIntent(EventosIntent.MonthNavigatedBack)
        val state = vm.state.first()
        assertEquals(9, state.filterMonth)
        assertEquals(2024, state.filterYear)
    }

    @Test
    fun `MonthNavigatedBack from January wraps to December previous year`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L) // Oct 2024
        // Go back 10 months to reach January, then one more
        repeat(10) { vm.handleIntent(EventosIntent.MonthNavigatedBack) }
        val state = vm.state.first()
        assertEquals(12, state.filterMonth)
        assertEquals(2023, state.filterYear)
    }

    @Test
    fun `month filter shows only events in that month across all tabs`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L) // Oct 2024
        // All test events are around epoch day 20000-20025 (Oct 2024)
        vm.handleIntent(
            EventosIntent.AddTestEvents(
                listOf(
                    Event("e1", "Oct Event", EventCategory.EVENTOS, 20000L), // Oct 4, 2024
                    Event("e2", "Nov Event", EventCategory.EVENTOS, 20040L), // Nov 13, 2024
                ),
            ),
        )
        // Default month = Oct 2024 — should show only e1
        val filtered = vm.state.first().filteredEvents
        assertEquals(1, filtered.size)
        assertEquals("e1", filtered[0].id)
    }

    // =========================================================================
    // Filtros combinados — DatePicker + Month (intersecao)
    // =========================================================================

    @Test
    fun `date picker and month filter work as intersection`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L) // Oct 2024
        vm.handleIntent(
            EventosIntent.AddTestEvents(
                listOf(
                    Event("e1", "Oct 4", EventCategory.EVENTOS, 20000L), // Oct 4
                    Event("e2", "Oct 10", EventCategory.EVENTOS, 20006L), // Oct 10
                ),
            ),
        )
        // Set date picker to Oct 4 — both month (Oct) and date (20000) match only e1
        vm.handleIntent(EventosIntent.DatePickerFilterChanged(20000L))
        val filtered = vm.state.first().filteredEvents
        assertEquals(1, filtered.size)
        assertEquals("e1", filtered[0].id)
    }

    @Test
    fun `date picker filter on different month than selected shows nothing`() = runTest {
        val vm = EventosViewModel(todayEpochDay = 20000L) // Oct 2024
        vm.handleIntent(
            EventosIntent.AddTestEvents(
                listOf(
                    Event("e1", "Oct Event", EventCategory.EVENTOS, 20000L), // Oct 4
                    Event("e2", "Nov Event", EventCategory.EVENTOS, 20040L), // Nov 13
                ),
            ),
        )
        // Month = Oct, but date picker = Nov 13 → intersection is empty
        vm.handleIntent(EventosIntent.DatePickerFilterChanged(20040L))
        val filtered = vm.state.first().filteredEvents
        assertTrue(filtered.isEmpty())
    }

    // =========================================================================
    // Helper — cria VM com eventos ATIVOS (todos no futuro)
    // =========================================================================

    private fun createVmWithActiveEvents(): EventosViewModel {
        val vm = EventosViewModel(todayEpochDay = 20000L)
        vm.handleIntent(
            EventosIntent.AddTestEvents(
                listOf(
                    Event(
                        id = "e1",
                        name = "Aniversario do Pedro",
                        category = EventCategory.EVENTOS,
                        dateEpochDay = 20010L,
                        contact = "11999998888",
                        description = "Festa surpresa",
                    ),
                    Event(
                        id = "e2",
                        name = "Casamento Maria e Joao",
                        category = EventCategory.EVENTOS,
                        dateEpochDay = 20005L,
                        contact = null,
                        description = null,
                    ),
                    Event(
                        id = "e3",
                        name = "Aluguel Quadra Pedro",
                        category = EventCategory.ALUGUEL,
                        dateEpochDay = 20015L,
                        contact = "11888887777",
                        description = "Quadra esportiva",
                    ),
                    Event(
                        id = "e4",
                        name = "Aluguel Salao de Festas",
                        category = EventCategory.ALUGUEL,
                        dateEpochDay = 20020L,
                        contact = null,
                        description = null,
                    ),
                    Event(
                        id = "e5",
                        name = "Day Use Piscina Pedro",
                        category = EventCategory.DAY_USE,
                        dateEpochDay = 20003L,
                        contact = "11777776666",
                        description = null,
                    ),
                    Event(
                        id = "e6",
                        name = "Day Use Churrasqueira",
                        category = EventCategory.DAY_USE,
                        dateEpochDay = 20025L,
                        contact = null,
                        description = "Inclui area coberta",
                    ),
                ),
            ),
        )
        return vm
    }
}
