package com.example.vitagym.presentation.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vitagym.domain.model.Workout
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for managing workout data and UI state.
 */
class WorkoutViewModel : ViewModel() {
    private val repository = WorkoutRepository()

    /**
     * StateFlow representing the list of workouts.
     * It automatically updates whenever the repository's data changes.
     */
    val workouts: StateFlow<List<Workout>> = repository.getWorkouts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly, // Change to Eagerly to ensure data is loaded immediately
            initialValue = emptyList()
        )

    /**
     * Adds a new workout entry.
     */
    fun addWorkout(title: String, duration: String, date: Long) {
        val durationInt = duration.toIntOrNull() ?: 0
        if (title.isNotBlank() && durationInt > 0) {
            viewModelScope.launch {
                repository.addWorkout(title, durationInt, date)
                Timber.i("Workout add request sent for: $title")
            }
        } else {
            Timber.w("Failed to add workout: invalid input")
        }
    }

    /**
     * Updates an existing workout entry.
     */
    fun updateWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.updateWorkout(workout)
        }
    }

    /**
     * Deletes a workout entry by its ID.
     */
    fun deleteWorkout(workoutId: String) {
        viewModelScope.launch {
            repository.deleteWorkout(workoutId)
        }
    }
}
