package br.com.kanoas.presentation.core.navigation

import app.cash.turbine.test
import br.com.kanoas.test.MainDispatcherEnv
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * Testes da barra de navegação inferior — valida transição de abas.
 */
class BottomNavViewModelTest {

    private val env = MainDispatcherEnv()

    @BeforeTest fun setUp() = env.install()
    @AfterTest fun tearDown() = env.uninstall()

    // Negative — tab inicial é KANBAN, não FINANCIAL
    @Test
    fun `initial tab is KANBAN and not FINANCIAL`() = runTest {
        val vm = BottomNavViewModel()
        val current = vm.state.first().current
        assertEquals(BottomTab.KANBAN, current)
        assertNotEquals(BottomTab.FINANCIAL, current)
    }

    // Positive — selecionar FINANCIAL atualiza state
    @Test
    fun `selecting FINANCIAL tab updates state`() = runTest {
        val vm = BottomNavViewModel()
        vm.handleIntent(BottomNavIntent.TabSelected(BottomTab.FINANCIAL))
        assertEquals(BottomTab.FINANCIAL, vm.state.first().current)
    }

    // Positive — selecionar aba emite efeito de navegação
    @Test
    fun `selecting tab emits NavigateTo effect`() = runTest {
        val vm = BottomNavViewModel()
        vm.effects.test {
            vm.handleIntent(BottomNavIntent.TabSelected(BottomTab.FINANCIAL))
            assertEquals(BottomNavEffect.NavigateTo(BottomTab.FINANCIAL), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Positive — voltar para KANBAN
    @Test
    fun `selecting KANBAN after FINANCIAL reverts state`() = runTest {
        val vm = BottomNavViewModel()
        vm.handleIntent(BottomNavIntent.TabSelected(BottomTab.FINANCIAL))
        vm.handleIntent(BottomNavIntent.TabSelected(BottomTab.KANBAN))
        assertEquals(BottomTab.KANBAN, vm.state.first().current)
    }
}
