package br.com.kanoas.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.kanoas.presentation.core.navigation.BottomNavIntent
import br.com.kanoas.presentation.core.navigation.BottomNavViewModel
import br.com.kanoas.presentation.core.navigation.BottomTab
import br.com.kanoas.presentation.core.theme.ThemeViewModel
import br.com.kanoas.presentation.financial.FinancialScreen
import br.com.kanoas.presentation.financial.FinancialViewModel
import br.com.kanoas.presentation.financial.addtransaction.AddTransactionDialog
import br.com.kanoas.presentation.financial.addtransaction.AddTransactionViewModel
import br.com.kanoas.presentation.kanban.KanbanScreen
import br.com.kanoas.presentation.kanban.KanbanViewModel
import br.com.kanoas.presentation.kanban.addtask.AddTaskDialog
import br.com.kanoas.presentation.kanban.addtask.AddTaskViewModel
import br.com.kanoas.presentation.login.LoginScreen
import br.com.kanoas.presentation.login.LoginViewModel
import br.com.kanoas.presentation.settings.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel

/**
 * Rotas de navegação do app.
 */
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val SETTINGS = "settings"
}

/**
 * Grafo de navegação principal.
 * Login → Home (com BottomNav: Home / Financeiro) → Settings.
 *
 * O [ThemeViewModel] é compartilhado entre todas as telas para
 * que o botão de light/dark mode funcione em qualquer lugar.
 */
@Composable
fun NavGraph(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
    ) {
        composable(route = Routes.LOGIN) {
            val loginViewModel: LoginViewModel = koinViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                themeViewModel = themeViewModel,
                onNavigateHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }

        composable(route = Routes.HOME) {
            HomeWithBottomNav(
                navController = navController,
                themeViewModel = themeViewModel,
            )
        }

        composable(route = Routes.SETTINGS) {
            SettingsScreen(
                themeViewModel = themeViewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}

/**
 * Tela principal pós-login com BottomNav (Home / Financeiro).
 * Cada aba tem seu próprio conteúdo + dialogs.
 */
@Composable
private fun HomeWithBottomNav(
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
) {
    val bottomNavViewModel: BottomNavViewModel = koinViewModel()
    val bottomNavState by bottomNavViewModel.state.collectAsState()

    // Kanban
    val kanbanViewModel: KanbanViewModel = koinViewModel()
    val kanbanState by kanbanViewModel.state.collectAsState()

    // Financial
    val financialViewModel: FinancialViewModel = koinViewModel()
    val financialState by financialViewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = bottomNavState.current == BottomTab.KANBAN,
                    onClick = {
                        bottomNavViewModel.handleIntent(
                            BottomNavIntent.TabSelected(BottomTab.KANBAN),
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                        )
                    },
                    label = { Text("Home") },
                )
                NavigationBarItem(
                    selected = bottomNavState.current == BottomTab.FINANCIAL,
                    onClick = {
                        bottomNavViewModel.handleIntent(
                            BottomNavIntent.TabSelected(BottomTab.FINANCIAL),
                        )
                    },
                    icon = {
                        Text(
                            text = "R$",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                    label = { Text("Financeiro") },
                )
            }
        },
    ) { _ ->
        when (bottomNavState.current) {
            BottomTab.KANBAN -> {
                KanbanScreen(
                    viewModel = kanbanViewModel,
                    themeViewModel = themeViewModel,
                    onNavigateSettings = {
                        navController.navigate(Routes.SETTINGS)
                    },
                )
            }

            BottomTab.FINANCIAL -> {
                FinancialScreen(
                    viewModel = financialViewModel,
                    themeViewModel = themeViewModel,
                )
            }
        }
    }

    // --- Dialogs ---

    // AddTask Dialog
    if (kanbanState.isAddTaskDialogVisible) {
        val addTaskViewModel: AddTaskViewModel = koinViewModel()
        AddTaskDialog(
            viewModel = addTaskViewModel,
            onDismiss = {
                kanbanViewModel.handleIntent(
                    br.com.kanoas.presentation.kanban.KanbanIntent.DismissAddTaskDialog,
                )
            },
            onTaskCreated = {
                kanbanViewModel.handleIntent(
                    br.com.kanoas.presentation.kanban.KanbanIntent.DismissAddTaskDialog,
                )
                // TODO: reload tasks from Supabase
            },
        )
    }

    // AddTransaction Dialog
    if (financialState.isAddDialogVisible) {
        val addTransactionViewModel: AddTransactionViewModel = koinViewModel()
        AddTransactionDialog(
            viewModel = addTransactionViewModel,
            onDismiss = {
                financialViewModel.handleIntent(
                    br.com.kanoas.presentation.financial.FinancialIntent.DismissAddDialog,
                )
            },
            onTransactionCreated = {
                financialViewModel.handleIntent(
                    br.com.kanoas.presentation.financial.FinancialIntent.DismissAddDialog,
                )
                // TODO: reload transactions from Supabase
            },
        )
    }
}
