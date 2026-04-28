package com.example.vitagym.presentation.dashboard

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
    val authRepository = remember { AuthRepository() }
    val currentUser = authRepository.getCurrentUser()
    val userName = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "User"

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
        },
        bottomBar = {
            // Floating Pill-Shaped Navigation Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .navigationBarsPadding()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    shape = RoundedCornerShape(36.dp),
                    color = Color(0xFF2E3548),
                    shadowElevation = 20.dp,
                    tonalElevation = 12.dp
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        windowInsets = WindowInsets(0.dp)
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Add, contentDescription = "Log", modifier = Modifier.size(26.dp)) },
                            label = { Text("Log", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            selected = false,
                            onClick = onNavigateToLogWorkout,
                            colors = NavigationBarItemDefaults.colors(
                                unselectedIconColor = Color(0xFF00E5FF),
                                unselectedTextColor = Color.White.copy(alpha = 0.8f),
                                indicatorColor = Color(0xFF00E5FF).copy(alpha = 0.12f)
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.History, contentDescription = "History", modifier = Modifier.size(26.dp)) },
                            label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            selected = false,
                            onClick = onNavigateToHistory,
                            colors = NavigationBarItemDefaults.colors(
                                unselectedIconColor = Color(0xFF00E5FF),
                                unselectedTextColor = Color.White.copy(alpha = 0.8f),
                                indicatorColor = Color(0xFF00E5FF).copy(alpha = 0.12f)
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Gyms", modifier = Modifier.size(26.dp)) },
                            label = { Text("Gyms", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            selected = false,
                            onClick = onNavigateToLocation,
                            colors = NavigationBarItemDefaults.colors(
                                unselectedIconColor = Color(0xFF00E5FF),
                                unselectedTextColor = Color.White.copy(alpha = 0.8f),
                                indicatorColor = Color(0xFF00E5FF).copy(alpha = 0.12f)
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Receipt, contentDescription = "Check-ins", modifier = Modifier.size(26.dp)) },
                            label = { Text("Checks", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                            selected = false,
                            onClick = onNavigateToCheckInHistory,
                            colors = NavigationBarItemDefaults.colors(
                                unselectedIconColor = Color(0xFF00E5FF),
                                unselectedTextColor = Color.White.copy(alpha = 0.8f),
                                indicatorColor = Color(0xFF00E5FF).copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            // Header Section
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

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00E5FF),
                                    Color(0xFF0099B3)
                                )
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

            Text(
                text = "Weekly Schedule",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            WeeklyCalendar()

            Spacer(modifier = Modifier.height(24.dp))

            QRCheckInCard(onQRCheckInClick = onNavigateToQRScanner)

            Spacer(modifier = Modifier.height(16.dp))

            AITrainerCard(onAITrainerClick = onNavigateToAITrainer)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Weekly Performance",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            GymStatsCard(weeklyStats = weeklyStats)

            // Buffer spacing to prevent bottom bar from covering content
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
private fun WeeklyCalendar() {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEE\ndd", Locale.getDefault())
    val daysOfWeek = (0..3).map { offset ->
        calendar.add(Calendar.DAY_OF_MONTH, if (offset == 0) 0 else 1)
        dateFormat.format(calendar.time)
    }
    val selectedDay = 0

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
                        if (index == selectedDay) {
                            Color(0xFF00E5FF)
                        } else {
                            Color(0xFF2E3548)
                        }
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

@Composable
private fun QRCheckInCard(onQRCheckInClick: () -> Unit) {
    Card(
        onClick = onQRCheckInClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF00E5FF),
                            Color(0xFF0099B3)
                        )
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

@Composable
private fun AITrainerCard(onAITrainerClick: () -> Unit) {
    Card(
        onClick = onAITrainerClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E3548)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Trainer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Start guided workout with real-time pose detection",
                fontSize = 14.sp,
                color = Color(0xFF8E8E93),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(
                    icon = "⏱️",
                    value = "45",
                    label = "Minutes",
                    color = Color(0xFF00E5FF)
                )
                StatItem(
                    icon = "💪",
                    value = "12",
                    label = "Exercises",
                    color = Color(0xFF00E5FF)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: String,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF8E8E93)
        )
    }
}

@Composable
private fun GymStatsCard(weeklyStats: WeeklyStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E3548)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
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

@Composable
private fun RowScope.MiniStatCard(
    icon: String,
    title: String,
    value: String,
    label: String
) {
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
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color(0xFF8E8E93)
                )
            }
        }
    }
}
