package com.example.vitagym.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitagym.R
import com.example.vitagym.presentation.auth.AuthRepository
import com.example.vitagym.presentation.classes.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Main dashboard screen where users can log new workouts.
 * 
 * @param viewModel ViewModel handling workout data operations.
 * @param onLogout Callback triggered when the user logs out.
 * @param onNavigateToHistory Callback to navigate to the workout history screen.
 * @param onNavigateToLocation Callback to navigate to the location/map screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: WorkoutViewModel,
    onLogout: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToLocation: () -> Unit
) {
    val authRepository = remember { AuthRepository() }
    val currentUser = authRepository.getCurrentUser()
    val userName = currentUser?.displayName ?: currentUser?.email ?: "User"

    var title by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    
    // State for managing the date picker visibility and selection
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = datePickerState.selectedDateMillis?.let { dateFormatter.format(Date(it)) } ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                },
                title = {
                    Column {
                        Text(text = stringResource(id = R.string.app_name), color = Color.White, fontSize = 18.sp)
                        Text(text = "Logged in as: $userName", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToLocation) {
                        Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "Location", tint = Color.White)
                    }
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(imageVector = Icons.Filled.History, contentDescription = "History", tint = Color.White)
                    }
                    IconButton(onClick = {
                        authRepository.logout()
                        onLogout()
                    }) {
                        Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Add New Workout", style = MaterialTheme.typography.headlineSmall)

            // Input for workout title/type
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(id = R.string.text_titleHint)) },
                trailingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
            )

            // Input for workout duration in minutes
            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(id = R.string.text_durationHint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = { Icon(Icons.Default.Timer, contentDescription = null) }
            )

            // Read-only field that opens the Date Picker when clicked
            OutlinedTextField(
                value = formattedDate,
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                label = { Text("Workout Date") },
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                }
            )

            // Material 3 Date Picker Dialog
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // Submit button to save the workout
            Button(
                onClick = {
                    if (title.isNotBlank() && duration.isNotBlank()) {
                        viewModel.addWorkout(
                            title, 
                            duration, 
                            datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        )
                        title = ""
                        duration = ""
                        onNavigateToHistory() // Navigate to list after adding
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.button_addWorkout))
            }
        }
    }
}
