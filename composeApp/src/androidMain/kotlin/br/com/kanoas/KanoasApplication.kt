package br.com.kanoas

import android.app.Application
import br.com.kanoas.di.appModule
import br.com.kanoas.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Application class — ponto de entrada do app Android.
 * Inicializa o Koin com os módulos de DI.
 */
class KanoasApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@KanoasApplication)
            modules(appModule(), platformModule())
        }
    }
}
