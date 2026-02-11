package com.example.vitagym.presentation.dashboard


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    navigateToLogin: () -> Unit,
    navigateToRegister: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F23),  // Dark navy
                        Color(0xFF1A1A2E)   // Slightly lighter
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo / App Name
            Text(
                text = "VitaGym",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E5FF)  // Cyan accent
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline
            Text(
                text = "Your Smart Fitness Companion",
                fontSize = 18.sp,
                color = Color(0xFFB0B0B0),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1.5f))

            // Get Started Button
            Button(
                onClick = navigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF),
                    contentColor = Color(0xFF0F0F23)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign In Button (Outlined)
            OutlinedButton(
                onClick = navigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF00E5FF)
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFF00E5FF))
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}