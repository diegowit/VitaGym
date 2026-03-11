package com.example.vitagym.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vitagym.presentation.auth.login.LoginScreen
import com.example.vitagym.presentation.auth.register.RegisterScreen
import com.example.vitagym.presentation.dashboard.DashboardScreen
import com.example.vitagym.presentation.dashboard.WelcomeScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Welcome) {

        // Welcome Screen
        composable<Welcome> {
            WelcomeScreen(
                navigateToLogin = { navController.navigate(Login) },
                navigateToRegister = { navController.navigate(Register) }
            )
        }

        // Login Screen
        composable<Login> {
            LoginScreen(
                navigateToHome = { 
                    navController.navigate(Dashboard) {
                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }

        // Register Screen
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

        // Dashboard Screen
        composable<Dashboard> {
            DashboardScreen(
                onLogout = {
                    navController.navigate(Welcome) {
                        popUpTo(Dashboard) { inclusive = true }
                    }
                }
            )
        }
    }
}