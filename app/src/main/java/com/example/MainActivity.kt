package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppNavDestination
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ContadorJuridicoProApp()
            }
        }
    }
}

@Composable
fun ContadorJuridicoProApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    val userSettings by viewModel.userSettings.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showAiAssistantDialog by remember { mutableStateOf(false) }

    val startDestination = when {
        !userSettings.hasCompletedOnboarding -> "onboarding"
        !userSettings.isLoggedIn -> "auth"
        else -> AppNavDestination.HOME.name
    }

    // Determine current active bottom nav destination
    val currentNavDestination = when {
        currentRoute?.startsWith(AppNavDestination.HOME.name) == true -> AppNavDestination.HOME
        currentRoute?.startsWith(AppNavDestination.CASES.name) == true -> AppNavDestination.CASES
        currentRoute?.startsWith(AppNavDestination.GUIDES.name) == true -> AppNavDestination.GUIDES
        currentRoute?.startsWith(AppNavDestination.CALENDAR.name) == true -> AppNavDestination.CALENDAR
        currentRoute?.startsWith(AppNavDestination.SETTINGS.name) == true -> AppNavDestination.SETTINGS
        else -> AppNavDestination.HOME
    }

    val showBottomBar = currentRoute in listOf(
        AppNavDestination.HOME.name,
        AppNavDestination.CASES.name,
        AppNavDestination.GUIDES.name,
        AppNavDestination.CALENDAR.name,
        AppNavDestination.SETTINGS.name
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavBar(
                    currentDestination = currentNavDestination,
                    onNavigate = { destination ->
                        navController.navigate(destination.name) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Onboarding Route
            composable("onboarding") {
                OnboardingScreen(
                    viewModel = viewModel,
                    onComplete = {
                        navController.navigate("auth") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            // Auth Route
            composable("auth") {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        navController.navigate(AppNavDestination.HOME.name) {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                )
            }

            // Top Level Bottom Nav Destinations
            composable(AppNavDestination.HOME.name) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToCalculation = { caseId ->
                        navController.navigate("calculation_details/$caseId")
                    },
                    onNavigateToPetition = { caseId ->
                        navController.navigate("structured_petition/$caseId")
                    },
                    onNavigateToGuides = {
                        navController.navigate(AppNavDestination.GUIDES.name)
                    },
                    onOpenAiAssistant = { showAiAssistantDialog = true }
                )
            }

            composable(AppNavDestination.CASES.name) {
                MyCasesScreen(
                    viewModel = viewModel,
                    onNavigateToCalculation = { caseId ->
                        navController.navigate("calculation_details/$caseId")
                    },
                    onNavigateToPetition = { caseId ->
                        navController.navigate("structured_petition/$caseId")
                    }
                )
            }

            composable(AppNavDestination.GUIDES.name) {
                LegalGuidesScreen(
                    viewModel = viewModel,
                    onOpenAiAssistant = { showAiAssistantDialog = true }
                )
            }

            composable(AppNavDestination.CALENDAR.name) {
                CalendarDeadlinesScreen(
                    viewModel = viewModel
                )
            }

            composable(AppNavDestination.SETTINGS.name) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateToTerms = {
                        navController.navigate("legal_documents")
                    },
                    onLogout = {
                        viewModel.setLoggedIn(false)
                        navController.navigate("auth") {
                            popUpTo(0)
                        }
                    }
                )
            }

            // Detail Sub-Screens
            composable(
                route = "calculation_details/{caseId}",
                arguments = listOf(navArgument("caseId") { type = NavType.LongType })
            ) { backStackEntry ->
                val caseId = backStackEntry.arguments?.getLong("caseId") ?: 1L
                CalculationDetailsScreen(
                    viewModel = viewModel,
                    caseId = caseId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPetition = { id ->
                        navController.navigate("structured_petition/$id")
                    }
                )
            }

            composable(
                route = "structured_petition/{caseId}",
                arguments = listOf(navArgument("caseId") { type = NavType.LongType })
            ) { backStackEntry ->
                val caseId = backStackEntry.arguments?.getLong("caseId") ?: 1L
                StructuredPetitionScreen(
                    viewModel = viewModel,
                    caseId = caseId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("legal_documents") {
                LegalDocumentsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        if (showAiAssistantDialog) {
            GeminiAssistantDialog(
                viewModel = viewModel,
                onDismiss = { showAiAssistantDialog = false }
            )
        }
    }
}
