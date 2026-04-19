package br.com.kanoas.shared.core.network

/**
 * Configuração de conexão com o projeto Supabase.
 *
 * ⚠️  NUNCA commitar valores reais neste arquivo.
 *
 * Como configurar:
 *  1. Acesse https://supabase.com → seu projeto → Settings → API
 *  2. Copie "Project URL" e "anon public" key
 *  3. Adicione em `local.properties` (gitignored):
 *       supabase.url=https://xxxx.supabase.co
 *       supabase.anonKey=eyJhbGci...
 *  4. Leia via BuildConfig no `platformModule` Android (veja PlatformModule.android.kt)
 */
data class SupabaseConfig(
    val url: String,
    val anonKey: String,
)
