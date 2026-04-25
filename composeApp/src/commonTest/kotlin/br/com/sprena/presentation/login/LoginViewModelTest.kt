package br.com.sprena.presentation.login

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
import kotlin.test.assertTrue

/**
 * Testes do LoginViewModel — User e Password inputs.
 * Cobre:
 *  - Digitação de username / password reflete no state
 *  - Validação bloqueia submit quando inválido (Negative)
 *  - Credenciais válidas emitem NavigateHome (Positive)
 *  - Credenciais inválidas emitem ShowError (Negative)
 *  - Toggle de visibilidade de senha
 */
class LoginViewModelTest {

    private val env = MainDispatcherEnv()

    @BeforeTest fun setUp() = env.install()
    @AfterTest fun tearDown() = env.uninstall()

    // ----------------- State wiring -----------------

    @Test
    fun `username changed updates state`() = runTest {
        val vm = LoginViewModel()
        vm.handleIntent(LoginIntent.UsernameChanged("pedro"))
        assertEquals("pedro", vm.state.first().username)
    }

    @Test
    fun `password changed updates state`() = runTest {
        val vm = LoginViewModel()
        vm.handleIntent(LoginIntent.PasswordChanged("Senha@123"))
        assertEquals("Senha@123", vm.state.first().password)
    }

    @Test
    fun `toggle password visibility flips flag`() = runTest {
        val vm = LoginViewModel()
        val before = vm.state.first().isPasswordVisible
        vm.handleIntent(LoginIntent.TogglePasswordVisibility)
        assertEquals(!before, vm.state.first().isPasswordVisible)
    }

    // ----------------- Validation (Negative) -----------------

    @Test
    fun `empty username sets usernameError and disables submit`() = runTest {
        val vm = LoginViewModel()
        vm.handleIntent(LoginIntent.UsernameChanged(""))
        vm.handleIntent(LoginIntent.PasswordChanged("Senha@123"))
        val s = vm.state.first()
        assertNotNull(s.usernameError)
        assertFalse(s.canSubmit)
    }

    @Test
    fun `short password sets passwordError and disables submit`() = runTest {
        val vm = LoginViewModel()
        vm.handleIntent(LoginIntent.UsernameChanged("pedro"))
        vm.handleIntent(LoginIntent.PasswordChanged("123"))
        val s = vm.state.first()
        assertNotNull(s.passwordError)
        assertFalse(s.canSubmit)
    }

    @Test
    fun `submit with invalid credentials emits ShowError`() = runTest {
        val vm = LoginViewModel()
        vm.effects.test {
            vm.handleIntent(LoginIntent.UsernameChanged(""))
            vm.handleIntent(LoginIntent.PasswordChanged(""))
            vm.handleIntent(LoginIntent.Submit)
            val effect = awaitItem()
            assertTrue(effect is LoginEffect.ShowError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ----------------- Validation (Positive) -----------------

    @Test
    fun `valid credentials enable submit`() = runTest {
        val vm = LoginViewModel()
        vm.handleIntent(LoginIntent.UsernameChanged("pedro"))
        vm.handleIntent(LoginIntent.PasswordChanged("Senha@123"))
        assertTrue(vm.state.first().canSubmit)
    }

    @Test
    fun `submit with valid credentials emits NavigateHome`() = runTest {
        val vm = LoginViewModel()
        vm.effects.test {
            vm.handleIntent(LoginIntent.UsernameChanged("pedro"))
            vm.handleIntent(LoginIntent.PasswordChanged("Senha@123"))
            vm.handleIntent(LoginIntent.Submit)
            assertEquals(LoginEffect.NavigateHome, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
