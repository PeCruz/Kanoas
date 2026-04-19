package br.com.kanoas.shared.auth.domain.validation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * TDD Red — LoginValidator
 *
 * Regras:
 *  - Username: obrigatório, mín 3, máx 50
 *  - Password: obrigatório, mín 6
 */
class LoginValidatorTest {

    // ── Username ─────────────────────────────────────────

    @Test
    fun `username empty is invalid`() {
        val result = LoginValidator.validateUsername("")
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `username blank spaces is invalid`() {
        assertFalse(LoginValidator.validateUsername("   ").isValid)
    }

    @Test
    fun `username with 2 chars below min is invalid`() {
        val short = "a".repeat(LoginValidator.USERNAME_MIN_LENGTH - 1)
        assertFalse(LoginValidator.validateUsername(short).isValid)
    }

    @Test
    fun `username with 3 chars at min boundary is valid`() {
        val atMin = "a".repeat(LoginValidator.USERNAME_MIN_LENGTH)
        assertTrue(LoginValidator.validateUsername(atMin).isValid)
        assertNull(LoginValidator.validateUsername(atMin).errorMessage)
    }

    @Test
    fun `username with 50 chars at max boundary is valid`() {
        val atMax = "a".repeat(LoginValidator.USERNAME_MAX_LENGTH)
        assertTrue(LoginValidator.validateUsername(atMax).isValid)
    }

    @Test
    fun `username with 51 chars above max is invalid`() {
        val overMax = "a".repeat(LoginValidator.USERNAME_MAX_LENGTH + 1)
        assertFalse(LoginValidator.validateUsername(overMax).isValid)
    }

    @Test
    fun `username with valid content is valid`() {
        assertTrue(LoginValidator.validateUsername("pedro.kanoas").isValid)
    }

    // ── Password ─────────────────────────────────────────

    @Test
    fun `password empty is invalid`() {
        val result = LoginValidator.validatePassword("")
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `password blank spaces is invalid`() {
        assertFalse(LoginValidator.validatePassword("     ").isValid)
    }

    @Test
    fun `password with 5 chars below min is invalid`() {
        val short = "a".repeat(LoginValidator.PASSWORD_MIN_LENGTH - 1)
        assertFalse(LoginValidator.validatePassword(short).isValid)
    }

    @Test
    fun `password with 6 chars at min boundary is valid`() {
        val atMin = "a".repeat(LoginValidator.PASSWORD_MIN_LENGTH)
        assertTrue(LoginValidator.validatePassword(atMin).isValid)
        assertNull(LoginValidator.validatePassword(atMin).errorMessage)
    }

    @Test
    fun `password long is valid`() {
        assertTrue(LoginValidator.validatePassword("minha_senha_super_segura_2026!").isValid)
    }
}
