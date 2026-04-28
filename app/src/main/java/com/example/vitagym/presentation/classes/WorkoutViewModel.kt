package com.example.vitagym.presentation.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vitagym.domain.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * ViewModel for managing workout data and UI state with real statistics.
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
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    /**
     * Real-time weekly statistics calculated from workouts
     */
    val weeklyStats: StateFlow<WeeklyStats> = workouts
        .map { workoutList -> calculateWeeklyStats(workoutList) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = WeeklyStats()
        )

    /**
     * Adds a custom workout entry (from LogWorkoutScreen).
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
     * Adds an AI Trainer workout with exercises and reps.
     * Call this when user completes an AI workout.
     */
    fun addAIWorkout(
        workoutType: WorkoutType,
        completedExercises: List<Exercise>,
        actualDuration: Int
    ) {
        val totalReps = AIWorkoutTemplates.getTotalReps(completedExercises)
        val title = when (workoutType) {
            WorkoutType.SQUATS -> "Squats"
            WorkoutType.PUSH_UPS -> "Push-ups"
            WorkoutType.PLANKS -> "Planks"
            WorkoutType.CUSTOM -> "Workout"
        }

        viewModelScope.launch {
            repository.addAIWorkout(
                title = title,
                duration = actualDuration,
                date = System.currentTimeMillis(),
                exercises = completedExercises,
                totalReps = totalReps,
                workoutType = workoutType
            )
            Timber.i("AI Workout added: $title with $totalReps total reps")
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

    /**
     * Get workouts for a specific date
     */
    fun getWorkoutsForDate(date: Long): List<Workout> {
        val calendar = Calendar.getInstance().apply { timeInMillis = date }
        val targetDay = calendar.get(Calendar.DAY_OF_YEAR)
        val targetYear = calendar.get(Calendar.YEAR)

        return workouts.value.filter {
            val workoutCal = Calendar.getInstance().apply { timeInMillis = it.date }
            workoutCal.get(Calendar.DAY_OF_YEAR) == targetDay &&
                    workoutCal.get(Calendar.YEAR) == targetYear
        }
    }

    /**
     * Calculate real statistics from workouts
     */
    private fun calculateWeeklyStats(workoutList: List<Workout>): WeeklyStats {
        val now = System.currentTimeMillis()
        val weekAgo = now - TimeUnit.DAYS.toMillis(7)

        val thisWeekWorkouts = workoutList.filter { it.date >= weekAgo }

        return WeeklyStats(
            totalWorkouts = thisWeekWorkouts.size,
            totalMinutes = thisWeekWorkouts.sumOf { it.duration },
            totalExercises = thisWeekWorkouts.sumOf { it.exercises.size },
            totalReps = thisWeekWorkouts.sumOf { it.totalReps },
            currentStreak = calculateStreak(workoutList),
            favoriteWorkout = findFavoriteWorkout(thisWeekWorkouts)
        )
    }

    /**
     * Calculate consecutive workout days
     */
    private fun calculateStreak(workoutList: List<Workout>): Int {
        if (workoutList.isEmpty()) return 0

        val sortedDates = workoutList
            .map { Calendar.getInstance().apply { timeInMillis = it.date } }
            .map { it.get(Calendar.DAY_OF_YEAR) to it.get(Calendar.YEAR) }
            .distinct()
            .sortedByDescending { (day, year) -> year * 1000 + day }

        if (sortedDates.isEmpty()) return 0

        var streak = 1
        val today = Calendar.getInstance()
        val todayDay = today.get(Calendar.DAY_OF_YEAR)
        val todayYear = today.get(Calendar.YEAR)

        // Check if there's a workout today or yesterday
        val (firstDay, firstYear) = sortedDates.first()
        val daysSinceFirst = (todayYear * 365 + todayDay) - (firstYear * 365 + firstDay)

        if (daysSinceFirst > 1) return 0  // Streak broken

        for (i in 0 until sortedDates.size - 1) {
            val (day1, year1) = sortedDates[i]
            val (day2, year2) = sortedDates[i + 1]

            val diff = (year1 * 365 + day1) - (year2 * 365 + day2)

            if (diff == 1) {
                streak++
            } else {
                break
            }
        }

        return streak
    }

    /**
     * Find most frequently done workout type
     */
    private fun findFavoriteWorkout(workouts: List<Workout>): WorkoutType? {
        if (workouts.isEmpty()) return null

        return workouts
            .filter { it.workoutType != WorkoutType.CUSTOM }
            .groupBy { it.workoutType }
            .maxByOrNull { it.value.size }
            ?.key
    }
}
