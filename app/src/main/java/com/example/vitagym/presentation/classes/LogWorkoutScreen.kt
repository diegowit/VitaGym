package com.example.vitagym.presentation.classes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

/**
 * dark-themed screen for logging new workouts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogWorkoutScreen(
    viewModel: WorkoutViewModel,
    onBackClick: () -> Unit,
    onWorkoutAdded: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = datePickerState.selectedDateMillis?.let {
        dateFormatter.format(Date(it))
    } ?: ""

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFF1A1A2E),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E)
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Text(
                        text = "Log Workout",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Instruction Text
            Text(
                text = "Add a new workout session",
                fontSize = 14.sp,
                color = Color(0xFF8E8E93),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Workout Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2E3548)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Workout Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Workout Title Input
                    Text(
                        text = "Workout Type",
                        fontSize = 13.sp,
                        color = Color(0xFF8E8E93),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "e.g., Chest Day, Morning Run",
                                color = Color(0xFF8E8E93)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF3D4459),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF00E5FF),
                            focusedLabelColor = Color(0xFF00E5FF),
                            unfocusedLabelColor = Color(0xFF8E8E93)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Duration Input
                    Text(
                        text = "Duration (minutes)",
                        fontSize = 13.sp,
                        color = Color(0xFF8E8E93),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "e.g., 30, 45, 60",
                                color = Color(0xFF8E8E93)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF3D4459),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF00E5FF),
                            focusedLabelColor = Color(0xFF00E5FF),
                            unfocusedLabelColor = Color(0xFF8E8E93)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Date Input
                    Text(
                        text = "Date",
                        fontSize = 13.sp,
                        color = Color(0xFF8E8E93),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = formattedDate,
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        placeholder = {
                            Text(
                                "Select date",
                                color = Color(0xFF8E8E93)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Date",
                                    tint = Color(0xFF00E5FF)
                                )
                            }
                        },
                        readOnly = true,
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color(0xFF3D4459),
                            disabledTextColor = Color.White,
                            disabledLeadingIconColor = Color(0xFF00E5FF),
                            disabledTrailingIconColor = Color(0xFF00E5FF),
                            disabledPlaceholderColor = Color(0xFF8E8E93)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (title.isNotBlank() && duration.isNotBlank()) {
                        viewModel.addWorkout(
                            title = title,
                            duration = duration,
                            date = datePickerState.selectedDateMillis
                                ?: System.currentTimeMillis()
                        )
                        onWorkoutAdded()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = title.isNotBlank() && duration.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF),
                    disabledContainerColor = Color(0xFF3D4459)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF1A1A2E),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Workout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A2E)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cancel Button
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF3D4459)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Date Picker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = { showDatePicker = false }
                    ) {
                        Text(
                            "OK",
                            color = Color(0xFF00E5FF)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDatePicker = false }
                    ) {
                        Text(
                            "Cancel",
                            color = Color(0xFF8E8E93)
                        )
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
    }
}