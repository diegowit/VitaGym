package com.example.vitagym.presentation.classes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vitagym.domain.model.Workout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen that displays a list of past workouts (Workout History).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkOutScreen(
    onBack: () -> Unit,
    viewModel: WorkoutViewModel
) {
    // Observe the workouts from the shared ViewModel
    val workouts by viewModel.workouts.collectAsStateWithLifecycle()
    
    var editingWorkout by remember { mutableStateOf<Workout?>(null) }
    val lightGreen = Color(0xFF8BC34A)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout History", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = lightGreen),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (workouts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No workouts added yet.", fontSize = 18.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Using 'id' as a key ensures Compose correctly handles deletions/updates
                items(workouts, key = { it.id }) { workout ->
                    WorkoutItem(
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
fun WorkoutItem(workout: Workout, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEdit() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = workout.title, fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(workout.date)),
                    fontSize = 12.sp, color = Color.Gray
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFF8BC34A), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${workout.duration} mins", fontSize = 16.sp, color = Color(0xFF8BC34A))
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWorkoutDialog(workout: Workout, onDismiss: () -> Unit, onConfirm: (Workout) -> Unit) {
    var title by remember { mutableStateOf(workout.title) }
    var duration by remember { mutableStateOf(workout.duration.toString()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = workout.date)
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = datePickerState.selectedDateMillis?.let { dateFormatter.format(Date(it)) } ?: ""

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Workout") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (mins)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = formattedDate, onValueChange = { },
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                    label = { Text("Date") }, readOnly = true, enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange, contentDescription = null) } }
                )
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } }
                    ) { DatePicker(state = datePickerState) }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val durationInt = duration.toIntOrNull() ?: workout.duration
                onConfirm(workout.copy(title = title, duration = durationInt, date = datePickerState.selectedDateMillis ?: workout.date))
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
