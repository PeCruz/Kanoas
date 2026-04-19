package br.com.kanoas.shared.financial.domain.validation

import br.com.kanoas.shared.core.validation.ValidationResult

/**
 * Formatador e validador de valores monetários em BRL (Real brasileiro).
 *
 * Convenções BR:
 *  - Separador de milhar: "." (ponto)
 *  - Separador decimal:   "," (vírgula)
 *  - Prefixo opcional:    "R$ "
 *
 * Internamente os valores trafegam em *centavos* (`Long`) para evitar
 * problemas de ponto flutuante (Double) em dinheiro.
 *
 * Exemplos:
 *  - "R$ 1.234,56" → 123456 centavos
 *  - "0,10"        → 10 centavos
 *  - "abc"         → inválido
 *  - ""            → inválido (obrigatório)
 *  - "0,00"        → inválido (deve ser > 0)
 */
object BrlMonetaryFormatter {

    /**
     * Converte o texto digitado pelo usuário em centavos.
     * Retorna `null` se o texto não representar um valor BRL válido.
     */
    fun parseToCents(raw: String): Long? {
        TODO("Day 3 TDD — implementar após Red")
    }

    /** Formata centavos em string BRL: 123456 → "R$ 1.234,56". */
    fun formatCents(cents: Long): String {
        TODO("Day 3 TDD — implementar após Red")
    }

    /** Valida um valor cru digitado pelo usuário (campo obrigatório, > 0). */
    fun validate(raw: String): ValidationResult {
        TODO("Day 3 TDD — implementar após Red")
    }
}
