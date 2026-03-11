package com.example.vitagym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.vitagym.navigation.NavigationWrapper
import com.example.vitagym.ui.theme.VitaGymTheme
import timber.log.Timber
import timber.log.Timber.i


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Timber.plant(Timber.DebugTree())
        i("VitaGym MainActivity started...")
        setContent {
            VitaGymTheme {
                NavigationWrapper()
            }        
        }    
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello User $name!",
        modifier = modifier
    )
}


@Composable
fun AddWorkout(modifier: Modifier = Modifier) {
    var title by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        Button(
            onClick = { },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            elevation = ButtonDefaults.buttonElevation(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
            Spacer(modifier = Modifier.width(width = 4.dp))
            Text(stringResource(id = R.string.button_addWorkout))
        }
    }}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWorkout() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            Icon(imageVector = Icons.Filled.Menu,
                contentDescription = "Menu",
                tint = Color.White)
        },
        title = {
            Text(text = stringResource(id = R.string.app_name),
                color = Color.White
            )
        },
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AddWorkoutPreview() {
    VitaGymTheme {
        Scaffold(
            topBar = { TopBarWorkout() },
            modifier = Modifier.fillMaxSize()) { innerPadding ->
            AddWorkout(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VitaGymTheme {
        Greeting("Android")
    }
}
