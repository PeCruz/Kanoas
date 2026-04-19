package br.com.kanoas

import android.app.Application
import br.com.kanoas.di.appModule
import br.com.kanoas.di.platformModule
import br.com.kanoas.shared.core.di.sharedModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application class — ponto de entrada do app Android.
 *
 * Inicializa o Koin com todos os módulos de DI na ordem correta:
 *  1. [platformModule]   → credenciais Supabase + DatabaseDriverFactory (Android-specific)
 *  2. [sharedModules]    → network, database, kanban, financial
 *  3. [appModule]        → ViewModels da camada de apresentação
 */
class KanoasApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@KanoasApplication)
            modules(
                platformModule(),
                *sharedModules().toTypedArray(),
                appModule(),
            )
        }
    }
}
