package com.example.vitagym.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Workout(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val duration: Int = 0,  // Duration in minutes
    val date: Long = System.currentTimeMillis(),
    val exercises: List<Exercise> = emptyList(),  // NEW: Track exercises
    val totalReps: Int = 0,  // NEW: Total reps for this workout
    val workoutType: WorkoutType = WorkoutType.CUSTOM  // NEW: Type of workout
)

@Serializable
data class Exercise(
    val name: String = "",
    val reps: Int = 0,
    val sets: Int = 1
)

@Serializable
enum class WorkoutType {
    SQUATS,
    PUSH_UPS,
    PLANKS,
    CUSTOM
}

/**
 * AI Trainer workout templates with predefined exercises
 */
object AIWorkoutTemplates {

    val SQUATS = listOf(
        Exercise(name = "Bodyweight Squats", reps = 15, sets = 3),
        Exercise(name = "Jump Squats", reps = 10, sets = 3),
        Exercise(name = "Sumo Squats", reps = 12, sets = 3),
        Exercise(name = "Pistol Squats", reps = 5, sets = 3)
    )

    val PUSH_UPS = listOf(
        Exercise(name = "Standard Push-ups", reps = 12, sets = 3),
        Exercise(name = "Wide Push-ups", reps = 10, sets = 3),
        Exercise(name = "Diamond Push-ups", reps = 8, sets = 3),
        Exercise(name = "Decline Push-ups", reps = 10, sets = 3)
    )

    val PLANKS = listOf(
        Exercise(name = "Standard Plank", reps = 60, sets = 3),  // reps = seconds
        Exercise(name = "Side Plank Left", reps = 45, sets = 3),
        Exercise(name = "Side Plank Right", reps = 45, sets = 3),
        Exercise(name = "Plank to Push-up", reps = 10, sets = 3)
    )

    fun getWorkoutByType(type: WorkoutType): List<Exercise> {
        return when (type) {
            WorkoutType.SQUATS -> SQUATS
            WorkoutType.PUSH_UPS -> PUSH_UPS
            WorkoutType.PLANKS -> PLANKS
            WorkoutType.CUSTOM -> emptyList()
        }
    }

    fun getTotalReps(exercises: List<Exercise>): Int {
        return exercises.sumOf { it.reps * it.sets }
    }

    fun getEstimatedDuration(exercises: List<Exercise>): Int {
        // Estimate: ~2 seconds per rep + 30 seconds rest between sets
        val totalReps = exercises.sumOf { it.reps * it.sets }
        val totalSets = exercises.sumOf { it.sets }
        val workTime = (totalReps * 2) / 60  // Convert to minutes
        val restTime = (totalSets * 30) / 60  // Convert to minutes
        return workTime + restTime + 5  // Add 5 min warmup/cooldown
    }
}

/**
 * Weekly workout statistics
 */
@Serializable
data class WeeklyStats(
    val totalWorkouts: Int = 0,
    val totalMinutes: Int = 0,
    val totalExercises: Int = 0,
    val totalReps: Int = 0,
    val currentStreak: Int = 0,
    val favoriteWorkout: WorkoutType? = null
)
