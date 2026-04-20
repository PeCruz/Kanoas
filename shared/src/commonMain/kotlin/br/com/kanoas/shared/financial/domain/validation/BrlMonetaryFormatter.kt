package br.com.kanoas.shared.financial.domain.validation

import br.com.kanoas.shared.core.validation.ValidationResult
import kotlin.math.roundToLong

/**
 * Formatador e validador de valores monetários em BRL (Real brasileiro).
 *
 * Convenções BR:
 *  - Separador de milhar: "." (ponto)
 *  - Separador decimal:   "," (vírgula)
 *  - Prefixo opcional:    "R$ "
 *
 * Internamente os valores trafegam em *centavos* (`Long`).
 */
object BrlMonetaryFormatter {

    /**
     * Converte texto BRL em centavos.
     * Retorna `null` se o texto não representar um valor válido.
     *
     * Exemplos:
     *  - "R$ 1.234,56" → 123456
     *  - "0,10"        → 10
     *  - "100"         → 10000
     *  - "abc"         → null
     */
    fun parseToCents(raw: String): Long? {
        val cleaned = raw
            .replace("R$", "")
            .trim()
            .replace(".", "")   // remove separador de milhar
            .replace(",", ".")  // vírgula decimal → ponto

        if (cleaned.isEmpty()) return null

        val value = cleaned.toDoubleOrNull() ?: return null
        return (value * 100).roundToLong()
    }

    /**
     * Formata centavos em string BRL.
     * Ex: 123456 → "R$ 1.234,56"
     */
    fun formatCents(cents: Long): String {
        val reais = cents / 100
        val centavos = (cents % 100).toString().padStart(2, '0')

        val reaisFormatted = if (reais == 0L) {
            "0"
        } else {
            reais.toString()
                .reversed()
                .chunked(3)
                .joinToString(".")
                .reversed()
        }

        return "R$ $reaisFormatted,$centavos"
    }

    /**
     * Valida um valor cru digitado pelo usuário (campo obrigatório, > 0).
     */
    fun validate(raw: String): ValidationResult {
        val cents = parseToCents(raw)
        return when {
            cents == null -> ValidationResult.invalid("Valor inválido")
            cents <= 0L -> ValidationResult.invalid("Valor deve ser maior que zero")
            else -> ValidationResult.Valid
        }
    }
}
