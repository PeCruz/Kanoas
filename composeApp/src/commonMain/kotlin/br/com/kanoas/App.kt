package br.com.kanoas

import androidx.compose.runtime.Composable
import br.com.kanoas.core.ui.theme.KanoasTheme
import br.com.kanoas.navigation.NavGraph
import org.koin.compose.KoinContext

/**
 * Composable raiz da aplicação.
 * Aplica o tema e injeta o contexto do Koin.
 */
@Composable
fun App() {
    KoinContext {
        KanoasTheme {
            NavGraph()
        }
    }
}
