package com.example.vitagym.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vitagym.presentation.aitrainer.AITrainerScreen
import com.example.vitagym.presentation.auth.login.LoginScreen
import com.example.vitagym.presentation.auth.register.RegisterScreen
import com.example.vitagym.presentation.checkin.CheckInHistoryScreen
import com.example.vitagym.presentation.classes.LogWorkoutScreen
import com.example.vitagym.presentation.classes.WorkOutScreen
import com.example.vitagym.presentation.classes.WorkoutViewModel
import com.example.vitagym.presentation.dashboard.DashboardScreen
import com.example.vitagym.presentation.dashboard.WelcomeScreen
import com.example.vitagym.presentation.location.GymLocationScreen
import com.example.vitagym.presentation.qrcode.QRScannerScreen

/**
 * The main navigation component of the application.
 * Manages the screen backstack, global UI elements like the Bottom Navigation Bar,
 * and standardizes transitions between destinations.
 */
@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    
    // Shared ViewModel instance available to all screens in the graph
    val workoutViewModel: WorkoutViewModel = viewModel()
    
    // Observe the current navigation state to update UI elements (like the Bottom Bar)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Logic to determine when to show the floating Bottom Navigation Bar
    val mainScreens = listOf(
        Dashboard::class,
        LogWorkout::class,
        WorkOutHistory::class,
        GymLocation::class,
        CheckInHistory::class
    )
    
    // Show bar only if the current route is one of the primary application hubs
    val showBottomBar = mainScreens.any { route -> currentDestination?.hasRoute(route) == true }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                // --- FLOATING BOTTOM NAVIGATION BAR ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 28.dp)
                        .navigationBarsPadding()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        shape = RoundedCornerShape(36.dp),
                        color = Color(0xFF25253D),
                        shadowElevation = 24.dp,
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            windowInsets = WindowInsets(0.dp)
                        ) {
                            // Home Tab
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(24.dp)) },
                                label = { Text("Home", fontSize = 10.sp) },
                                selected = currentDestination?.hasRoute<Dashboard>() == true,
                                onClick = {
                                    navController.navigate(Dashboard) {
                                        // Standard tab navigation behavior: avoid multiple copies of same destination
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = navigationItemColors()
                            )
                            // Log Workout Tab
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Add, contentDescription = "Log", modifier = Modifier.size(24.dp)) },
                                label = { Text("Log", fontSize = 10.sp) },
                                selected = currentDestination?.hasRoute<LogWorkout>() == true,
                                onClick = {
                                    navController.navigate(LogWorkout) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = navigationItemColors()
                            )
                            // Workout History Tab
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.History, contentDescription = "History", modifier = Modifier.size(24.dp)) },
                                label = { Text("History", fontSize = 10.sp) },
                                selected = currentDestination?.hasRoute<WorkOutHistory>() == true,
                                onClick = {
                                    navController.navigate(WorkOutHistory) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = navigationItemColors()
                            )
                            // Gym Locator Tab
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.LocationOn, contentDescription = "Gyms", modifier = Modifier.size(24.dp)) },
                                label = { Text("Gyms", fontSize = 10.sp) },
                                selected = currentDestination?.hasRoute<GymLocation>() == true,
                                onClick = {
                                    navController.navigate(GymLocation) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = navigationItemColors()
                            )
                            // Check-in History Tab
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Receipt, contentDescription = "Checks", modifier = Modifier.size(24.dp)) },
                                label = { Text("Checks", fontSize = 10.sp) },
                                selected = currentDestination?.hasRoute<CheckInHistory>() == true,
                                onClick = {
                                    navController.navigate(CheckInHistory) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = navigationItemColors()
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        // Main content area where screens are swapped
        NavHost(
            navController = navController, 
            startDestination = Welcome,
            // Applies consistent slide + fade animations to all screen changes
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(350)) + fadeIn(tween(350))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(350)) + fadeOut(tween(350))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(350)) + fadeIn(tween(350))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(350)) + fadeOut(tween(350))
            }
        ) {
            // Welcome/Auth Flow
            composable<Welcome> {
                WelcomeScreen(
                    navigateToLogin = { navController.navigate(Login) },
                    navigateToRegister = { navController.navigate(Register) }
                )
            }

            composable<Login> {
                LoginScreen(
                    navigateToHome = {
                        navController.navigate(Dashboard) {
                            popUpTo(Login) { inclusive = true }
                        }
                    }
                )
            }

            composable<Register> {
                RegisterScreen(
                    navigateToHome = {
                        navController.navigate(Dashboard) {
                            popUpTo(Register) { inclusive = true }
                        }
                    },
                    navigateToLogin = { navController.navigate(Login) }
                )
            }

            // Main Core Features
            composable<Dashboard> {
                DashboardScreen(
                    viewModel = workoutViewModel,
                    onLogout = {
                        navController.navigate(Welcome) {
                            popUpTo(Dashboard) { inclusive = true }
                        }
                    },
                    onNavigateToHistory = { navController.navigate(WorkOutHistory) },
                    onNavigateToLocation = { navController.navigate(GymLocation) },
                    onNavigateToAITrainer = { navController.navigate(AITrainer) },
                    onNavigateToQRScanner = { navController.navigate(QRScanner) },
                    onNavigateToCheckInHistory = { navController.navigate(CheckInHistory) },
                    onNavigateToLogWorkout = { navController.navigate(LogWorkout) }
                )
            }

            composable<WorkOutHistory> {
                WorkOutScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = workoutViewModel
                )
            }

            composable<GymLocation> {
                GymLocationScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // AI Features
            composable<AITrainer> {
                AITrainerScreen(
                    viewModel = workoutViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Utility Features
            composable<QRScanner> {
                QRScannerScreen(
                    onBackClick = { navController.navigateUp() },
                    onCheckInSuccess = {
                        navController.navigate(Dashboard) {
                            popUpTo(Dashboard) { inclusive = false }
                        }
                    }
                )
            }

            composable<CheckInHistory> {
                CheckInHistoryScreen(
                    onBackClick = { navController.navigateUp() }
                )
            }

            composable<LogWorkout> {
                LogWorkoutScreen(
                    viewModel = workoutViewModel,
                    onBackClick = { navController.navigateUp() },
                    onWorkoutAdded = {
                        navController.navigate(WorkOutHistory) {
                            popUpTo(Dashboard) { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}

/**
 * Consistent styling for navigation bar items.
 */
@Composable
private fun navigationItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Color(0xFF1A1A2E),
    selectedTextColor = Color(0xFF00E5FF),
    indicatorColor = Color(0xFF00E5FF),
    unselectedIconColor = Color.White.copy(alpha = 0.5f),
    unselectedTextColor = Color.White.copy(alpha = 0.5f)
)
