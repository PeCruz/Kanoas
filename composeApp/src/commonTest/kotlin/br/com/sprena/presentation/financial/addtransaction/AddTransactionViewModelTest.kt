package br.com.sprena.presentation.financial.addtransaction

import app.cash.turbine.test
import br.com.sprena.presentation.financial.TransactionType
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
 * TDD Red — AddTransactionViewModel
 *
 * Campos:
 *  - Amount: obrigatório, > 0 (BRL format)
 *  - PersonName: obrigatório, máx 50
 *  - Description: opcional, máx 3000
 *  - Type: INCOME / EXPENSE (default EXPENSE)
 *  - InputDate: dia atual (auto)
 */
class AddTransactionViewModelTest {

    private val env = MainDispatcherEnv()
    private val today: Long = 20_000L

    @BeforeTest fun setUp() = env.install()
    @AfterTest fun tearDown() = env.uninstall()

    private fun vm() = AddTransactionViewModel(today = { today })

    // ── InputDate (auto = hoje) ──────────────────────────

    @Test
    fun `initial inputEpochDay is today`() = runTest {
        assertEquals(today, vm().state.first().inputEpochDay)
    }

    // ── Amount — Negative ────────────────────────────────

    @Test
    fun `amount empty sets amountError`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.AmountChanged(""))
        assertNotNull(vm.state.first().amountError)
    }

    @Test
    fun `amount with letters sets amountError`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.AmountChanged("abc"))
        assertNotNull(vm.state.first().amountError)
    }

    @Test
    fun `amount zero sets amountError`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.AmountChanged("0,00"))
        assertNotNull(vm.state.first().amountError)
    }

    // ── Amount — Positive ────────────────────────────────

    @Test
    fun `valid BRL string parses to cents and clears error`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.AmountChanged("R$ 1.234,56"))
        val s = vm.state.first()
        assertNull(s.amountError)
        assertEquals(123_456L, s.amountCents)
    }

    @Test
    fun `minimum non-zero amount is accepted`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.AmountChanged("0,01"))
        val s = vm.state.first()
        assertNull(s.amountError)
        assertEquals(1L, s.amountCents)
    }

    // ── PersonName — obrigatório, máx 50 ─────────────────

    @Test
    fun `personName empty sets personNameError`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.PersonNameChanged(""))
        assertNotNull(vm.state.first().personNameError)
    }

    @Test
    fun `personName blank sets personNameError`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.PersonNameChanged("   "))
        assertNotNull(vm.state.first().personNameError)
    }

    @Test
    fun `personName exceeding 50 chars sets personNameError`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.PersonNameChanged("a".repeat(51)))
        assertNotNull(vm.state.first().personNameError)
    }

    @Test
    fun `personName at boundary of 50 chars clears error`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.PersonNameChanged("a".repeat(50)))
        assertNull(vm.state.first().personNameError)
    }

    @Test
    fun `valid personName updates state and clears error`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.PersonNameChanged("Pedro Cruz"))
        val s = vm.state.first()
        assertEquals("Pedro Cruz", s.personName)
        assertNull(s.personNameError)
    }

    // ── Description — opcional, máx 3000 ─────────────────

    @Test
    fun `description empty is accepted`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.DescriptionChanged(""))
        assertNull(vm.state.first().descriptionError)
    }

    @Test
    fun `description at boundary of 3000 is accepted`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.DescriptionChanged("d".repeat(3000)))
        assertNull(vm.state.first().descriptionError)
    }

    @Test
    fun `description exceeding 3000 sets descriptionError`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.DescriptionChanged("d".repeat(3001)))
        assertNotNull(vm.state.first().descriptionError)
    }

    // ── Type ─────────────────────────────────────────────

    @Test
    fun `default type is EXPENSE`() = runTest {
        assertEquals(TransactionType.EXPENSE, vm().state.first().type)
    }

    @Test
    fun `TypeChanged to INCOME updates state`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.TypeChanged(TransactionType.INCOME))
        assertEquals(TransactionType.INCOME, vm.state.first().type)
    }

    @Test
    fun `TypeChanged to EXPENSE updates state`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.TypeChanged(TransactionType.INCOME))
        vm.handleIntent(AddTransactionIntent.TypeChanged(TransactionType.EXPENSE))
        assertEquals(TransactionType.EXPENSE, vm.state.first().type)
    }

    // ── Submit / canSubmit ───────────────────────────────

    @Test
    fun `canSubmit is false when amount is missing`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.PersonNameChanged("Pedro"))
        assertFalse(vm.state.first().canSubmit)
    }

    @Test
    fun `canSubmit is false when personName is missing`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.AmountChanged("R$ 10,00"))
        assertFalse(vm.state.first().canSubmit)
    }

    @Test
    fun `canSubmit is true when amount and personName are valid`() = runTest {
        val vm = vm()
        vm.handleIntent(AddTransactionIntent.AmountChanged("R$ 10,00"))
        vm.handleIntent(AddTransactionIntent.PersonNameChanged("Pedro"))
        assertTrue(vm.state.first().canSubmit)
    }

    @Test
    fun `submit with invalid form emits ShowError`() = runTest {
        val vm = vm()
        vm.effects.test {
            vm.handleIntent(AddTransactionIntent.Submit)
            val e = awaitItem()
            assertTrue(e is AddTransactionEffect.ShowError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `submit with valid form emits TransactionCreated`() = runTest {
        val vm = vm()
        vm.effects.test {
            vm.handleIntent(AddTransactionIntent.AmountChanged("R$ 25,00"))
            vm.handleIntent(AddTransactionIntent.PersonNameChanged("Pedro"))
            vm.handleIntent(AddTransactionIntent.Submit)
            assertEquals(AddTransactionEffect.TransactionCreated, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dismiss emits Dismissed`() = runTest {
        val vm = vm()
        vm.effects.test {
            vm.handleIntent(AddTransactionIntent.Dismiss)
            assertEquals(AddTransactionEffect.Dismissed, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
