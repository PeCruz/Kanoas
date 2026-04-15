package br.com.kanoas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.kanoas.presentation.home.HomeScreen

/**
 * Rotas de navegação do app.
 */
object Routes {
    const val HOME = "home"
}

/**
 * Grafo de navegação principal.
 * Todas as telas são declaradas aqui.
 */
@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
    ) {
        composable(route = Routes.HOME) {
            HomeScreen()
        }
    }
}
