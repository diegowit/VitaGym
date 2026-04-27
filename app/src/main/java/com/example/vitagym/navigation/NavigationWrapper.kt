package com.example.vitagym.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vitagym.presentation.aitrainer.AITrainerScreen
import com.example.vitagym.presentation.auth.login.LoginScreen
import com.example.vitagym.presentation.auth.register.RegisterScreen
import com.example.vitagym.presentation.checkin.CheckInHistoryScreen
import com.example.vitagym.presentation.classes.WorkOutScreen
import com.example.vitagym.presentation.classes.WorkoutViewModel
import com.example.vitagym.presentation.dashboard.DashboardScreen
import com.example.vitagym.presentation.dashboard.WelcomeScreen
import com.example.vitagym.presentation.location.GymLocationScreen
import com.example.vitagym.presentation.qrcode.QRScannerScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
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
                },
                onNavigateToLocation = {
                    navController.navigate(GymLocation)
                },
                onNavigateToAITrainer = {
                    navController.navigate(AITrainer)
                },
                onNavigateToQRScanner = {
                    navController.navigate(QRScanner)
                },
                onNavigateToCheckInHistory = {
                    navController.navigate(CheckInHistory)
                }
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

        composable<AITrainer> {
            AITrainerScreen(
                onBack = { navController.popBackStack() }
            )
        }

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
    }
}