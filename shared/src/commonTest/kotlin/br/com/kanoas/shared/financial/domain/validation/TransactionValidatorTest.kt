package br.com.kanoas.shared.financial.domain.validation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * TDD Red — TransactionValidator
 *
 * Regras:
 *  - PersonName: obrigatório, máx 50
 *  - Description: opcional, máx 3000
 */
class TransactionValidatorTest {

    // ── PersonName — obrigatório, máx 50 ─────────────────

    @Test
    fun `personName empty is invalid`() {
        val result = TransactionValidator.validatePersonName("")
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `personName blank spaces is invalid`() {
        assertFalse(TransactionValidator.validatePersonName("   ").isValid)
    }

    @Test
    fun `personName with 50 chars at boundary is valid`() {
        val atMax = "a".repeat(TransactionValidator.PERSON_NAME_MAX_LENGTH)
        assertTrue(TransactionValidator.validatePersonName(atMax).isValid)
        assertNull(TransactionValidator.validatePersonName(atMax).errorMessage)
    }

    @Test
    fun `personName with 51 chars above max is invalid`() {
        val overMax = "a".repeat(TransactionValidator.PERSON_NAME_MAX_LENGTH + 1)
        assertFalse(TransactionValidator.validatePersonName(overMax).isValid)
    }

    @Test
    fun `personName with valid content is valid`() {
        assertTrue(TransactionValidator.validatePersonName("Pedro Cruz").isValid)
    }

    // ── Description — opcional, máx 3000 ─────────────────

    @Test
    fun `description null is valid because optional`() {
        assertTrue(TransactionValidator.validateDescription(null).isValid)
    }

    @Test
    fun `description empty is valid because optional`() {
        assertTrue(TransactionValidator.validateDescription("").isValid)
    }

    @Test
    fun `description at boundary of 3000 chars is valid`() {
        assertTrue(TransactionValidator.validateDescription("d".repeat(3000)).isValid)
    }

    @Test
    fun `description exceeding 3000 chars is invalid`() {
        assertFalse(TransactionValidator.validateDescription("d".repeat(3001)).isValid)
    }

    @Test
    fun `description with valid text is valid`() {
        assertTrue(TransactionValidator.validateDescription("Pagamento fornecedor").isValid)
    }
}
