package com.example.vitagym.presentation.classes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitagym.domain.model.Workout
import com.example.vitagym.domain.model.WorkoutType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modern workout history screen with dark theme.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkOutScreen(
    onBack: () -> Unit,
    viewModel: WorkoutViewModel
) {
    val workouts by viewModel.workouts.collectAsStateWithLifecycle()
    val weeklyStats by viewModel.weeklyStats.collectAsStateWithLifecycle()
    var editingWorkout by remember { mutableStateOf<Workout?>(null) }

    Scaffold(
        containerColor = Color(0xFF1A1A2E),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E)
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "Workout History",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${weeklyStats.totalWorkouts} workouts this week",
                            color = Color(0xFF8E8E93),
                            fontSize = 12.sp
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (workouts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = Color(0xFF3D4459),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "No workouts yet",
                        fontSize = 18.sp,
                        color = Color(0xFF8E8E93),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Start by logging your first workout!",
                        fontSize = 14.sp,
                        color = Color(0xFF8E8E93)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(workouts, key = { it.id }) { workout ->
                    WorkoutCard(
                        workout = workout,
                        onEdit = { editingWorkout = workout },
                        onDelete = { viewModel.deleteWorkout(workout.id) }
                    )
                }
            }
        }

        editingWorkout?.let { workout ->
            EditWorkoutDialog(
                workout = workout,
                onDismiss = { editingWorkout = null },
                onConfirm = { updatedWorkout ->
                    viewModel.updateWorkout(updatedWorkout)
                    editingWorkout = null
                }
            )
        }
    }
}

@Composable
fun WorkoutCard(
    workout: Workout,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E3548)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row - Title and Type Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Workout Icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when (workout.workoutType) {
                                    WorkoutType.SQUATS -> Color(0xFF00E5FF).copy(alpha = 0.2f)
                                    WorkoutType.PUSH_UPS -> Color(0xFFFF6B6B).copy(alpha = 0.2f)
                                    WorkoutType.PLANKS -> Color(0xFFFFA726).copy(alpha = 0.2f)
                                    WorkoutType.CUSTOM -> Color(0xFF8E8E93).copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (workout.workoutType) {
                                WorkoutType.SQUATS -> "🏋️"
                                WorkoutType.PUSH_UPS -> "💪"
                                WorkoutType.PLANKS -> "🧘"
                                WorkoutType.CUSTOM -> "🎯"
                            },
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = workout.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
                                .format(Date(workout.date)),
                            fontSize = 12.sp,
                            color = Color(0xFF8E8E93)
                        )
                    }
                }

                // Delete Icon
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Duration
                StatChip(
                    icon = "⏱️",
                    value = "${workout.duration}",
                    label = "min"
                )

                // Exercises (if AI workout)
                if (workout.exercises.isNotEmpty()) {
                    StatChip(
                        icon = "🏋️",
                        value = "${workout.exercises.size}",
                        label = "exercises"
                    )
                }

                // Total Reps (if AI workout)
                if (workout.totalReps > 0) {
                    StatChip(
                        icon = "💪",
                        value = "${workout.totalReps}",
                        label = "reps"
                    )
                }
            }

            // Exercise Details (if AI workout)
            if (workout.exercises.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1A1A2E))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Exercises",
                        fontSize = 11.sp,
                        color = Color(0xFF8E8E93),
                        fontWeight = FontWeight.Medium
                    )

                    workout.exercises.take(3).forEach { exercise ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = exercise.name,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${exercise.reps} × ${exercise.sets}",
                                fontSize = 12.sp,
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (workout.exercises.size > 3) {
                        Text(
                            text = "+${workout.exercises.size - 3} more",
                            fontSize = 11.sp,
                            color = Color(0xFF8E8E93),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color(0xFF2E3548),
            title = {
                Text(
                    text = "Delete Workout?",
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${workout.title}\"? This action cannot be undone.",
                    color = Color(0xFF8E8E93)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = Color(0xFFFF6B6B),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFF8E8E93)
                    )
                }
            }
        )
    }
}

@Composable
fun StatChip(
    icon: String,
    value: String,
    label: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF3D4459))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 14.sp
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF8E8E93)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWorkoutDialog(
    workout: Workout,
    onDismiss: () -> Unit,
    onConfirm: (Workout) -> Unit
) {
    var title by remember { mutableStateOf(workout.title) }
    var duration by remember { mutableStateOf(workout.duration.toString()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = workout.date)
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = datePickerState.selectedDateMillis?.let {
        dateFormatter.format(Date(it))
    } ?: ""

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF2E3548),
        title = {
            Text(
                text = "Edit Workout",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = Color(0xFF8E8E93)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF3D4459),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF00E5FF),
                        focusedLabelColor = Color(0xFF00E5FF),
                        unfocusedLabelColor = Color(0xFF8E8E93)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Duration
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (mins)", color = Color(0xFF8E8E93)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF3D4459),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF00E5FF),
                        focusedLabelColor = Color(0xFF00E5FF),
                        unfocusedLabelColor = Color(0xFF8E8E93)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Date
                OutlinedTextField(
                    value = formattedDate,
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    label = { Text("Date", color = Color(0xFF8E8E93)) },
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color(0xFF3D4459),
                        disabledTextColor = Color.White,
                        disabledLabelColor = Color(0xFF8E8E93),
                        disabledTrailingIconColor = Color(0xFF00E5FF)
                    ),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF)
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("OK", color = Color(0xFF00E5FF))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel", color = Color(0xFF8E8E93))
                        }
                    },
                    colors = DatePickerDefaults.colors(
                        containerColor = Color(0xFF2E3548)
                    )
                ) {
                    DatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors(
                            containerColor = Color(0xFF2E3548),
                            titleContentColor = Color.White,
                            headlineContentColor = Color.White,
                            weekdayContentColor = Color(0xFF8E8E93),
                            subheadContentColor = Color.White,
                            yearContentColor = Color.White,
                            currentYearContentColor = Color(0xFF00E5FF),
                            selectedYearContentColor = Color(0xFF1A1A2E),
                            selectedYearContainerColor = Color(0xFF00E5FF),
                            dayContentColor = Color.White,
                            selectedDayContentColor = Color(0xFF1A1A2E),
                            selectedDayContainerColor = Color(0xFF00E5FF),
                            todayContentColor = Color(0xFF00E5FF),
                            todayDateBorderColor = Color(0xFF00E5FF)
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val durationInt = duration.toIntOrNull() ?: workout.duration
                    onConfirm(
                        workout.copy(
                            title = title,
                            duration = durationInt,
                            date = datePickerState.selectedDateMillis ?: workout.date
                        )
                    )
                }
            ) {
                Text(
                    text = "Save",
                    color = Color(0xFF00E5FF),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = Color(0xFF8E8E93)
                )
            }
        }
    )
}