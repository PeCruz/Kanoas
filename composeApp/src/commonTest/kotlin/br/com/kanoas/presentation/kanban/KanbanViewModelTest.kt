package br.com.kanoas.presentation.kanban

import app.cash.turbine.test
import br.com.kanoas.test.MainDispatcherEnv
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Testes da tela Kanban (Home) — board + botão "Add Task".
 */
class KanbanViewModelTest {

    private val env = MainDispatcherEnv()

    @BeforeTest fun setUp() = env.install()
    @AfterTest fun tearDown() = env.uninstall()

    // Negative — estado inicial NÃO deve mostrar diálogo
    @Test
    fun `initial state does not show add task dialog`() = runTest {
        val vm = KanbanViewModel()
        assertFalse(vm.state.first().isAddTaskDialogVisible)
    }

    // Negative — estado inicial vazio (sem tasks)
    @Test
    fun `initial state has no tasks`() = runTest {
        val vm = KanbanViewModel()
        assertTrue(vm.state.first().tasksByColumn.isEmpty())
    }

    // Positive — clicar Add Task emite efeito de abertura de diálogo
    @Test
    fun `AddTaskClicked emits OpenAddTaskDialog effect`() = runTest {
        val vm = KanbanViewModel()
        vm.effects.test {
            vm.handleIntent(KanbanIntent.AddTaskClicked)
            assertEquals(KanbanEffect.OpenAddTaskDialog, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Positive — clicar Add Task marca isAddTaskDialogVisible = true
    @Test
    fun `AddTaskClicked sets dialog visible`() = runTest {
        val vm = KanbanViewModel()
        vm.handleIntent(KanbanIntent.AddTaskClicked)
        assertTrue(vm.state.first().isAddTaskDialogVisible)
    }

    @Test
    fun `DismissAddTaskDialog hides dialog`() = runTest {
        val vm = KanbanViewModel()
        vm.handleIntent(KanbanIntent.AddTaskClicked)
        vm.handleIntent(KanbanIntent.DismissAddTaskDialog)
        assertFalse(vm.state.first().isAddTaskDialogVisible)
    }

    // Positive — LoadBoard preenche o state
    @Test
    fun `LoadBoard populates columns`() = runTest {
        val vm = KanbanViewModel()
        vm.handleIntent(KanbanIntent.LoadBoard)
        val s = vm.state.first()
        assertTrue(s.columns.isNotEmpty(), "LoadBoard deveria popular colunas padrão")
    }
}
