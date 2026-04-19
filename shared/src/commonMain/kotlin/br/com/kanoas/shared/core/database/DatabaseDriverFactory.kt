package br.com.kanoas.shared.core.database

import app.cash.sqldelight.db.SqlDriver

/**
 * Factory para criar o driver SQLite correto para cada plataforma.
 *
 * Segue o padrão `expect/actual` do KMP — cada plataforma fornece
 * sua própria implementação:
 *  - Android → [AndroidSqliteDriver]
 *  - iOS (futuro) → [NativeSqliteDriver]
 */
/*expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

/**
 * Cria a instância do [KanoasDatabase] a partir da factory de plataforma.
 *
 * Garante que o schema seja criado antes de retornar o banco.
 */
fun createDatabase(driverFactory: DatabaseDriverFactory): KanoasDatabase {
    val driver = driverFactory.createDriver()
    KanoasDatabase.Schema.create(driver)
    return KanoasDatabase(driver)
}*/
