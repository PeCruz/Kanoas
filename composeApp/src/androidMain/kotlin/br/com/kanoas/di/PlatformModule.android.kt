package br.com.kanoas.di

//import br.com.kanoas.shared.core.database.DatabaseDriverFactory
import br.com.kanoas.shared.core.network.SupabaseConfig
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Módulo Koin com dependências específicas da plataforma Android.
 *
 * Responsabilidades:
 *  - Fornecer [SupabaseConfig] com as credenciais do projeto
 *  - Fornecer [DatabaseDriverFactory] com o Context Android
 *
 * ⚠️ Credenciais do Supabase:
 *  Substitua os placeholders pelos valores reais do seu projeto.
 *  Em produção, leia de BuildConfig gerado a partir de `local.properties`:
 *
 *  ```
 *  // local.properties (gitignored)
 *  supabase.url=https://xxxx.supabase.co
 *  supabase.anonKey=eyJhbGci...
 *  ```
 *
 *  ```kotlin
 *  // build.gradle.kts (composeApp)
 *  buildConfigField("String", "SUPABASE_URL", "\"${localProperties["supabase.url"]}\"")
 *  buildConfigField("String", "SUPABASE_ANON_KEY", "\"${localProperties["supabase.anonKey"]}\"")
 *  ```
 */
fun platformModule() = module {
    // Supabase credentials
    // TODO: Substitua pelos valores do seu projeto Supabase
    single {
        SupabaseConfig(
            url = "https://YOUR_PROJECT_ID.supabase.co",
            anonKey = "YOUR_ANON_KEY",
        )
    }

    // SQLDelight driver Android (precisa de Context)
    //single { DatabaseDriverFactory(context = androidContext()) }
}
