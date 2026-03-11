package com.example.vitagym.presentation.classes

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.vitagym.domain.model.Workout

class WorkoutViewModel : ViewModel() {
    private val _workouts = mutableStateListOf<Workout>()
    val workouts: List<Workout> = _workouts

    fun addWorkout(title: String, duration: String) {
        val durationInt = duration.toIntOrNull() ?: 0
        if (title.isNotBlank() && durationInt > 0) {
            _workouts.add(
                Workout(
                    title = title,
                    duration = durationInt
                )
            )
        }
    }
}
