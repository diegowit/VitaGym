package com.example.vitagym.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vitagym.presentation.auth.login.LoginScreen
import com.example.vitagym.presentation.dashboard.DashboardScreen

@Composable
fun NavigationWrapper(){
    val navController = rememberNavController()
    NavHost(navController= navController, startDestination = Login) {

        composable<Login>{
            LoginScreen{navController.navigate(Dashboard)}

        }
composable<Dashboard>{
    DashboardScreen()
}
    }
}