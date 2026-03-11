package com.example.vitagym.presentation.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vitagym.domain.model.Workout
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class WorkoutViewModel : ViewModel() {
    private val repository = WorkoutRepository()

    val workouts: StateFlow<List<Workout>> = repository.getWorkouts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addWorkout(title: String, duration: String) {
        val durationInt = duration.toIntOrNull() ?: 0
        if (title.isNotBlank() && durationInt > 0) {
            viewModelScope.launch {
                repository.addWorkout(title, durationInt)
                Timber.i("Workout add request sent for: $title")
            }
        } else {
            Timber.w("Failed to add workout: invalid input")
        }
    }
}
