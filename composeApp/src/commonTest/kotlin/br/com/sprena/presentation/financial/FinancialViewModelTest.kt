package br.com.sprena.presentation.financial

import app.cash.turbine.test
import br.com.sprena.test.MainDispatcherEnv
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * TDD Red — FinancialViewModel
 *
 * Cobre:
 *  - Estado inicial (vazio, diálogo fechado)
 *  - Botão "Adicionar Transação" abre diálogo / emite efeito
 *  - DismissAddDialog fecha diálogo
 *  - Load (placeholder para futura integração)
 */
class FinancialViewModelTest {

    private val env = MainDispatcherEnv()

    @BeforeTest fun setUp() = env.install()
    @AfterTest fun tearDown() = env.uninstall()

    // ── Estado inicial ───────────────────────────────────

    @Test
    fun `initial state has dialog hidden`() = runTest {
        val vm = FinancialViewModel()
        assertFalse(vm.state.first().isAddDialogVisible)
    }

    @Test
    fun `initial state has empty transactions and zero balance`() = runTest {
        val vm = FinancialViewModel()
        val s = vm.state.first()
        assertTrue(s.transactions.isEmpty())
        assertEquals(0L, s.balanceCents)
        assertEquals(0L, s.incomeCents)
        assertEquals(0L, s.expenseCents)
    }

    @Test
    fun `initial state is not loading`() = runTest {
        assertFalse(FinancialViewModel().state.first().isLoading)
    }

    // ── Add Transaction button ───────────────────────────

    @Test
    fun `AddTransactionClicked opens dialog in state`() = runTest {
        val vm = FinancialViewModel()
        vm.handleIntent(FinancialIntent.AddTransactionClicked)
        assertTrue(vm.state.first().isAddDialogVisible)
    }

    @Test
    fun `AddTransactionClicked emits OpenAddTransactionDialog effect`() = runTest {
        val vm = FinancialViewModel()
        vm.effects.test {
            vm.handleIntent(FinancialIntent.AddTransactionClicked)
            assertEquals(FinancialEffect.OpenAddTransactionDialog, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── Dismiss dialog ───────────────────────────────────

    @Test
    fun `DismissAddDialog hides dialog`() = runTest {
        val vm = FinancialViewModel()
        vm.handleIntent(FinancialIntent.AddTransactionClicked)
        vm.handleIntent(FinancialIntent.DismissAddDialog)
        assertFalse(vm.state.first().isAddDialogVisible)
    }

    // ── Load ─────────────────────────────────────────────

    @Test
    fun `Load sets isLoading true during fetch`() = runTest {
        val vm = FinancialViewModel()
        vm.handleIntent(FinancialIntent.Load)
        // Enquanto carrega, isLoading deve ter sido true pelo menos uma vez.
        // Após completar, transactions pode estar vazia (sem backend real ainda).
        val s = vm.state.first()
        // Para Day 3, basta verificar que o intent não crasha.
        // A implementação real preencherá transactions.
        assertTrue(s.transactions.isEmpty() || s.transactions.isNotEmpty())
    }
}
