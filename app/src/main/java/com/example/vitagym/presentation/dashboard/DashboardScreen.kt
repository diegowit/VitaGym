package com.example.vitagym.presentation.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vitagym.domain.model.WeeklyStats
import com.example.vitagym.presentation.auth.AuthRepository
import com.example.vitagym.presentation.classes.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Main dashboard screen that provides a summary of user activity,
 * weekly statistics, and access to key features like the AI Trainer and QR Check-in.
 *
 * @param viewModel The shared workout view model for data observation.
 * @param onLogout Callback to handle user logout.
 * @param onNavigateToHistory Callback to navigate to workout history.
 * @param onNavigateToLocation Callback to navigate to gym locator.
 * @param onNavigateToAITrainer Callback to start the AI trainer session.
 * @param onNavigateToQRScanner Callback to open the QR check-in scanner.
 * @param onNavigateToCheckInHistory Callback to view gym check-in history.
 * @param onNavigateToLogWorkout Callback to manually log a workout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: WorkoutViewModel,
    onLogout: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToLocation: () -> Unit,
    onNavigateToAITrainer: () -> Unit,
    onNavigateToQRScanner: () -> Unit,
    onNavigateToCheckInHistory: () -> Unit,
    onNavigateToLogWorkout: () -> Unit
) {
    // Repository to access user profile and logout functionality
    val authRepository = remember { AuthRepository() }
    val currentUser = authRepository.getCurrentUser()
    val userName = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "User"

    // Observes real-time weekly performance stats from the ViewModel
    val weeklyStats by viewModel.weeklyStats.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()
    val currentDate = remember {
        SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()).format(Date())
    }

    Scaffold(
        containerColor = Color(0xFF1A1A2E),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E)
                ),
                title = { },
                actions = {
                    IconButton(onClick = {
                        authRepository.logout()
                        onLogout()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        }
        // Note: The Bottom Navigation Bar is managed globally in NavigationWrapper.kt
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            // --- HEADER SECTION ---
            // Displays the current date, a welcome message, and user avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = currentDate,
                        fontSize = 13.sp,
                        color = Color(0xFF8E8E93)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Daily Activity",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Initial-based User Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF00E5FF), Color(0xFF0099B3))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.first().uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- WEEKLY SCHEDULE SECTION ---
            // Horizontal row showing the next few days of tracking
            Text(
                text = "Weekly Schedule",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            WeeklyCalendar()

            Spacer(modifier = Modifier.height(24.dp))

            // --- QR CHECK-IN CARD ---
            // Direct access to gym check-in via QR code scanning
            QRCheckInCard(onQRCheckInClick = onNavigateToQRScanner)

            Spacer(modifier = Modifier.height(16.dp))

            // --- AI TRAINER CARD ---
            // Primary interactive feature for guided workouts using pose detection
            AITrainerCard(onAITrainerClick = onNavigateToAITrainer)

            Spacer(modifier = Modifier.height(24.dp))

            // --- WEEKLY PERFORMANCE SUMMARY ---
            // Visual report of the user's progress in the last 7 days
            Text(
                text = "Weekly Performance",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            GymStatsCard(weeklyStats = weeklyStats)

            // Spacing buffer to ensure scrollable content isn't obscured by the floating nav bar
            Spacer(modifier = Modifier.height(140.dp))
        }
    }
}

/**
 * Renders a small horizontal calendar view.
 */
@Composable
private fun WeeklyCalendar() {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEE\ndd", Locale.getDefault())
    val daysOfWeek = (0..3).map { offset ->
        calendar.add(Calendar.DAY_OF_MONTH, if (offset == 0) 0 else 1)
        dateFormat.format(calendar.time)
    }
    val selectedDay = 0 // Today is always the first index

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        daysOfWeek.forEachIndexed { index, day ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(70.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (index == selectedDay) Color(0xFF00E5FF) else Color(0xFF2E3548)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    fontSize = 14.sp,
                    fontWeight = if (index == selectedDay) FontWeight.Bold else FontWeight.Normal,
                    color = if (index == selectedDay) Color(0xFF1A1A2E) else Color(0xFF8E8E93),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

/**
 * Interactive card for launching the QR scanner.
 */
@Composable
private fun QRCheckInCard(onQRCheckInClick: () -> Unit) {
    Card(
        onClick = onQRCheckInClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFF0099B3))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "QR Check-In",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Scan to check into gym",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

/**
 * Redesigned AI Trainer card with visual cues for interactivity (glow, chevron, CTA).
 */
@Composable
private fun AITrainerCard(onAITrainerClick: () -> Unit) {
    Card(
        onClick = onAITrainerClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3548)),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF00E5FF).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "AI Trainer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF).copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Start guided workout with real-time pose detection",
                fontSize = 14.sp,
                color = Color(0xFF8E8E93),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Summary of AI capabilities
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(icon = "⏱️", value = "45", label = "Minutes", color = Color(0xFF00E5FF))
                StatItem(icon = "💪", value = "12", label = "Exercises", color = Color(0xFF00E5FF))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Call-to-Action Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF00E5FF).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "START GUIDED SESSION",
                        color = Color(0xFF00E5FF),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Reusable stat item for the AI Trainer card.
 */
@Composable
private fun StatItem(icon: String, value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 11.sp, color = Color(0xFF8E8E93))
    }
}

/**
 * Grid of statistics summarizing the current week's activity.
 */
@Composable
private fun GymStatsCard(weeklyStats: WeeklyStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3548)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "This Week",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "${weeklyStats.totalWorkouts} sessions",
                    fontSize = 12.sp,
                    color = Color(0xFF00E5FF)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Row 1: Time and Exercises
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStatCard(
                    icon = "⏱️",
                    title = "Total Time",
                    value = weeklyStats.totalMinutes.toString(),
                    label = "minutes"
                )
                MiniStatCard(
                    icon = "🏋️",
                    title = "Exercises",
                    value = weeklyStats.totalExercises.toString(),
                    label = "completed"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Reps and Streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStatCard(
                    icon = "💪",
                    title = "Total Reps",
                    value = weeklyStats.totalReps.toString(),
                    label = "repetitions"
                )
                MiniStatCard(
                    icon = "🔥",
                    title = "Day Streak",
                    value = weeklyStats.currentStreak.toString(),
                    label = "active days"
                )
            }
        }
    }
}

/**
 * Reusable card component for small numeric statistics.
 */
@Composable
private fun RowScope.MiniStatCard(icon: String, title: String, value: String, label: String) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF3D4459))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = title, fontSize = 11.sp, color = Color(0xFF8E8E93), fontWeight = FontWeight.Medium)
            }
            Column {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(text = label, fontSize = 10.sp, color = Color(0xFF8E8E93))
            }
        }
    }
}
