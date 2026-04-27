package br.com.sprena.presentation.financial

import java.util.Calendar

actual fun currentYearMonth(): YearMonth {
    val cal = Calendar.getInstance()
    return YearMonth(
        year = cal.get(Calendar.YEAR),
        month = cal.get(Calendar.MONTH) + 1,
    )
}
