package com.example.vitagym.presentation.classes

import androidx.lifecycle.ViewModel
import com.example.vitagym.domain.model.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

class WorkoutViewModel : ViewModel() {
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts.asStateFlow()

    fun addWorkout(title: String, duration: String) {
        val durationInt = duration.toIntOrNull() ?: 0
        if (title.isNotBlank() && durationInt > 0) {
            val newWorkout = Workout(
                title = title,
                duration = durationInt
            )
            _workouts.update { currentList ->
                currentList + newWorkout
            }
            Timber.i("Added workout: $newWorkout. Total workouts: ${_workouts.value.size}")
        } else {
            Timber.w("Failed to add workout: title='$title', duration='$duration'")
        }
    }
}
