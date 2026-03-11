package com.example.vitagym.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Timer
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onLogout: () -> Unit = {}) {
    val authRepository = remember { AuthRepository() }
    val currentUser = authRepository.getCurrentUser()
    
    // Get user display name or email, fallback to "User"
    val userName = currentUser?.displayName ?: currentUser?.email ?: "User"

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                },
                title = {
                    Column {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Logged in as: $userName",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        authRepository.logout()
                        onLogout()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        AddWorkoutContent(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun AddWorkoutContent(modifier: Modifier = Modifier) {
    var title by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Workout Title Field
        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(id = R.string.text_titleHint)) },
            trailingIcon = {
                Icon(
                    Icons.Default.Edit, contentDescription = "",
                    tint = Color.Black
                )
            },
        )

        // Workout Duration Field
        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            value = duration,
            onValueChange = { duration = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(id = R.string.text_durationHint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                Icon(
                    Icons.Default.Timer, contentDescription = "",
                    tint = Color.Black
                )
            },
        )

        Button(
            onClick = { /* Handle add workout logic here */ },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            elevation = ButtonDefaults.buttonElevation(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
            Spacer(modifier = Modifier.width(width = 4.dp))
            Text(stringResource(id = R.string.button_addWorkout))
        }
    }
}
