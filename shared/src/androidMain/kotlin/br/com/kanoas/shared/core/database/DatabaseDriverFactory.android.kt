package br.com.kanoas.shared.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/**
 * Implementação Android do [DatabaseDriverFactory].
 *
 * Usa [AndroidSqliteDriver] que persiste os dados no armazenamento
 * interno do dispositivo no arquivo "kanoas.db".
 *
 * @param context Android [Context] necessário para acessar o sistema de arquivos.
 */
/*actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = KanoasDatabase.Schema,
        context = context,
        name = "kanoas.db",
    )
}*/
