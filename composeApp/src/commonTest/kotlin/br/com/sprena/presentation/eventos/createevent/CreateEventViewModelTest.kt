package br.com.sprena.presentation.eventos.createevent

import app.cash.turbine.test
import br.com.sprena.presentation.eventos.EventCategory
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
 * TDD — Testes do form de criacao/edicao de evento.
 *
 * Campos:
 *  - Nome do Evento (obrigatorio)
 *  - Categoria (obrigatorio)
 *  - Data do Evento (obrigatorio)
 *  - Contato (opcional)
 *  - Descricao (opcional)
 */
class CreateEventViewModelTest {

    private val env = MainDispatcherEnv()

    @BeforeTest
    fun setUp() = env.install()

    @AfterTest
    fun tearDown() = env.uninstall()

    // =========================================================================
    // Estado Inicial
    // =========================================================================

    @Test
    fun `initial state has empty name`() = runTest {
        val vm = CreateEventViewModel()
        assertEquals("", vm.state.first().name)
    }

    @Test
    fun `initial state has no category selected`() = runTest {
        val vm = CreateEventViewModel()
        assertNull(vm.state.first().category)
    }

    @Test
    fun `initial state has no date selected`() = runTest {
        val vm = CreateEventViewModel()
        assertNull(vm.state.first().dateEpochDay)
    }

    @Test
    fun `initial state has empty contact`() = runTest {
        val vm = CreateEventViewModel()
        assertEquals("", vm.state.first().contact)
    }

    @Test
    fun `initial state has empty description`() = runTest {
        val vm = CreateEventViewModel()
        assertEquals("", vm.state.first().description)
    }

    @Test
    fun `initial state cannot submit`() = runTest {
        val vm = CreateEventViewModel()
        assertFalse(vm.state.first().canSubmit)
    }

    @Test
    fun `initial state has no unsaved changes`() = runTest {
        val vm = CreateEventViewModel()
        assertFalse(vm.state.first().hasUnsavedChanges)
    }

    @Test
    fun `initial state is not in edit mode`() = runTest {
        val vm = CreateEventViewModel()
        assertFalse(vm.state.first().isEditMode)
        assertNull(vm.state.first().editingEventId)
    }

    // =========================================================================
    // Preenchimento de campos
    // =========================================================================

    @Test
    fun `NameChanged updates name in state`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.NameChanged("Festa Junina"))
        assertEquals("Festa Junina", vm.state.first().name)
    }

    @Test
    fun `CategoryChanged updates category in state`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.CategoryChanged(EventCategory.ALUGUEL))
        assertEquals(EventCategory.ALUGUEL, vm.state.first().category)
    }

    @Test
    fun `DateChanged updates dateEpochDay in state`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.DateChanged(20000L))
        assertEquals(20000L, vm.state.first().dateEpochDay)
    }

    @Test
    fun `ContactChanged updates contact in state`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.ContactChanged("11999998888"))
        assertEquals("11999998888", vm.state.first().contact)
    }

    @Test
    fun `DescriptionChanged updates description in state`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.DescriptionChanged("Descricao do evento"))
        assertEquals("Descricao do evento", vm.state.first().description)
    }

    @Test
    fun `filling any field marks hasUnsavedChanges`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.NameChanged("A"))
        assertTrue(vm.state.first().hasUnsavedChanges)
    }

    // =========================================================================
    // Validacao — campos obrigatorios
    // =========================================================================

    @Test
    fun `canSubmit is true when all required fields are filled`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.NameChanged("Festa"))
        vm.handleIntent(CreateEventIntent.CategoryChanged(EventCategory.EVENTOS))
        vm.handleIntent(CreateEventIntent.DateChanged(20000L))
        assertTrue(vm.state.first().canSubmit)
    }

    @Test
    fun `canSubmit is false when name is empty`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.CategoryChanged(EventCategory.EVENTOS))
        vm.handleIntent(CreateEventIntent.DateChanged(20000L))
        assertFalse(vm.state.first().canSubmit)
    }

    @Test
    fun `canSubmit is false when category is null`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.NameChanged("Festa"))
        vm.handleIntent(CreateEventIntent.DateChanged(20000L))
        assertFalse(vm.state.first().canSubmit)
    }

    @Test
    fun `canSubmit is false when date is null`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.NameChanged("Festa"))
        vm.handleIntent(CreateEventIntent.CategoryChanged(EventCategory.EVENTOS))
        assertFalse(vm.state.first().canSubmit)
    }

    @Test
    fun `canSubmit remains true with optional fields empty`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.NameChanged("Festa"))
        vm.handleIntent(CreateEventIntent.CategoryChanged(EventCategory.EVENTOS))
        vm.handleIntent(CreateEventIntent.DateChanged(20000L))
        assertTrue(vm.state.first().canSubmit)
    }

    @Test
    fun `canSubmit is false when name is only whitespace`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.NameChanged("   "))
        vm.handleIntent(CreateEventIntent.CategoryChanged(EventCategory.EVENTOS))
        vm.handleIntent(CreateEventIntent.DateChanged(20000L))
        assertFalse(vm.state.first().canSubmit)
    }

    // =========================================================================
    // Validacao — erros de campo
    // =========================================================================

    @Test
    fun `Submit with empty name sets nameError`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.CategoryChanged(EventCategory.EVENTOS))
        vm.handleIntent(CreateEventIntent.DateChanged(20000L))
        vm.handleIntent(CreateEventIntent.Submit)
        assertNotNull(vm.state.first().nameError)
    }

    @Test
    fun `Submit with no category sets categoryError`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.NameChanged("Festa"))
        vm.handleIntent(CreateEventIntent.DateChanged(20000L))
        vm.handleIntent(CreateEventIntent.Submit)
        assertNotNull(vm.state.first().categoryError)
    }

    @Test
    fun `Submit with no date sets dateError`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.NameChanged("Festa"))
        vm.handleIntent(CreateEventIntent.CategoryChanged(EventCategory.EVENTOS))
        vm.handleIntent(CreateEventIntent.Submit)
        assertNotNull(vm.state.first().dateError)
    }

    @Test
    fun `valid submit clears all errors`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.Submit)
        vm.handleIntent(CreateEventIntent.NameChanged("Festa"))
        vm.handleIntent(CreateEventIntent.CategoryChanged(EventCategory.EVENTOS))
        vm.handleIntent(CreateEventIntent.DateChanged(20000L))
        vm.handleIntent(CreateEventIntent.Submit)
        val state = vm.state.first()
        assertNull(state.nameError)
        assertNull(state.categoryError)
        assertNull(state.dateError)
    }

    // =========================================================================
    // Submit — efeitos (criacao)
    // =========================================================================

    @Test
    fun `valid Submit emits EventSaved effect with null eventId for creation`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.NameChanged("Festa"))
        vm.handleIntent(CreateEventIntent.CategoryChanged(EventCategory.EVENTOS))
        vm.handleIntent(CreateEventIntent.DateChanged(20000L))
        vm.effects.test {
            vm.handleIntent(CreateEventIntent.Submit)
            val effect = awaitItem()
            assertTrue(effect is CreateEventEffect.EventSaved)
            val saved = effect as CreateEventEffect.EventSaved
            assertNull(saved.eventId)
            assertEquals("Festa", saved.name)
            assertEquals(EventCategory.EVENTOS, saved.category)
            assertEquals(20000L, saved.dateEpochDay)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Submit with optional fields includes them in effect`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(CreateEventIntent.NameChanged("Aluguel"))
        vm.handleIntent(CreateEventIntent.CategoryChanged(EventCategory.ALUGUEL))
        vm.handleIntent(CreateEventIntent.DateChanged(20000L))
        vm.handleIntent(CreateEventIntent.ContactChanged("11999998888"))
        vm.handleIntent(CreateEventIntent.DescriptionChanged("Espaco coberto"))
        vm.effects.test {
            vm.handleIntent(CreateEventIntent.Submit)
            val effect = awaitItem() as CreateEventEffect.EventSaved
            assertEquals("(11) 99999-8888", effect.contact)
            assertEquals("Espaco coberto", effect.description)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invalid Submit does NOT emit EventSaved`() = runTest {
        val vm = CreateEventViewModel()
        vm.effects.test {
            vm.handleIntent(CreateEventIntent.Submit)
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // =========================================================================
    // Modo Edicao — LoadForEdit
    // =========================================================================

    @Test
    fun `LoadForEdit sets edit mode and populates fields`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(
            CreateEventIntent.LoadForEdit(
                eventId = "e1",
                name = "Festa Original",
                category = EventCategory.EVENTOS,
                dateEpochDay = 20010L,
                contact = "11999998888",
                description = "Descricao original",
            ),
        )
        val state = vm.state.first()
        assertTrue(state.isEditMode)
        assertEquals("e1", state.editingEventId)
        assertEquals("Festa Original", state.name)
        assertEquals(EventCategory.EVENTOS, state.category)
        assertEquals(20010L, state.dateEpochDay)
        assertEquals("11999998888", state.contact)
        assertEquals("Descricao original", state.description)
        assertTrue(state.canSubmit)
    }

    @Test
    fun `LoadForEdit with null optional fields sets empty strings`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(
            CreateEventIntent.LoadForEdit(
                eventId = "e2",
                name = "Sem Contato",
                category = EventCategory.ALUGUEL,
                dateEpochDay = 20010L,
                contact = null,
                description = null,
            ),
        )
        val state = vm.state.first()
        assertEquals("", state.contact)
        assertEquals("", state.description)
    }

    @Test
    fun `Submit in edit mode emits EventSaved with eventId`() = runTest {
        val vm = CreateEventViewModel()
        vm.handleIntent(
            CreateEventIntent.LoadForEdit(
                eventId = "e1",
                name = "Festa",
                category = EventCategory.EVENTOS,
                dateEpochDay = 20010L,
                contact = null,
                description = null,
            ),
        )
        vm.handleIntent(CreateEventIntent.NameChanged("Festa Atualizada"))
        vm.effects.test {
            vm.handleIntent(CreateEventIntent.Submit)
            val effect = awaitItem() as CreateEventEffect.EventSaved
            assertEquals("e1", effect.eventId)
            assertEquals("Festa Atualizada", effect.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // =========================================================================
    // Navegacao — voltar
    // =========================================================================

    @Test
    fun `BackClicked emits NavigateBack effect`() = runTest {
        val vm = CreateEventViewModel()
        vm.effects.test {
            vm.handleIntent(CreateEventIntent.BackClicked)
            assertEquals(CreateEventEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
