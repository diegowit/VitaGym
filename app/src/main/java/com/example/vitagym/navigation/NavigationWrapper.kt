package com.example.vitagym.navigation


import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vitagym.presentation.auth.login.LoginScreen
import com.example.vitagym.presentation.auth.register.RegisterScreen
import com.example.vitagym.presentation.classes.WorkOutScreen
import com.example.vitagym.presentation.classes.WorkoutViewModel
import com.example.vitagym.presentation.dashboard.DashboardScreen
import com.example.vitagym.presentation.dashboard.WelcomeScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    // Shared ViewModel to persist workouts during the app session
    val workoutViewModel: WorkoutViewModel = viewModel()

    NavHost(navController = navController, startDestination = Welcome) {

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

        composable<Dashboard> {
            DashboardScreen(
                viewModel = workoutViewModel,
                onLogout = {
                    navController.navigate(Welcome) {
                        popUpTo(Dashboard) { inclusive = true }
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(WorkOutHistory)
                }
            )
        }

        composable<WorkOutHistory> {
            WorkOutScreen(
                workouts = workoutViewModel.workouts,
                onBack = { navController.popBackStack() }
            )
        }
    }
}