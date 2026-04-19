package br.com.kanoas.shared.core.di

//import br.com.kanoas.shared.core.database.createDatabase
import org.koin.dsl.module

/**
 * Módulo Koin de persistência local (SQLDelight).
 *
 * Provê:
 *  - [br.com.kanoas.shared.core.database.KanoasDatabase] como singleton
 *
 * ⚠️ Requer que [br.com.kanoas.shared.core.database.DatabaseDriverFactory] esteja
 * registrado no Koin antes deste módulo (feito no `platformModule` Android).
 */
/*val databaseModule = module {
    single { createDatabase(driverFactory = get()) }
}*/
