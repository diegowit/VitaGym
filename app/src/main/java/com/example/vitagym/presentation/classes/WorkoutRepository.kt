package com.example.vitagym.presentation.classes

import com.example.vitagym.domain.model.Workout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
 * Repository for handling workout-related data operations with Firestore.
 */
class WorkoutRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val workoutsCollection = firestore.collection("workouts")

    /**
     * Streams the list of workouts for the current user from Firestore.
     * Uses [callbackFlow] to listen for real-time updates.
     */
    fun getWorkouts(): Flow<List<Workout>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrEmpty()) {
            trySend(emptyList())
            close() // Close the flow if no user is logged in
            return@callbackFlow
        }

        // Setting up a snapshot listener for real-time updates from Firestore
        val subscription = workoutsCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error fetching workouts")
                    // Note: We don't close the flow on error to allow for retry/recovery
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val workouts = snapshot.toObjects(Workout::class.java)
                    trySend(workouts)
                }
            }

        // Crucial: This block is executed when the flow is cancelled or closed.
        // It ensures the Firestore listener is removed to prevent memory leaks.
        awaitClose {
            subscription.remove()
            Timber.d("Firestore workout subscription removed")
        }
    }

    /**
     * Adds a new workout to the current user's collection in Firestore.
     */
    suspend fun addWorkout(title: String, duration: Int, date: Long) {
        val userId = auth.currentUser?.uid ?: return
        val workout = Workout(
            id = workoutsCollection.document().id,
            userId = userId,
            title = title,
            duration = duration,
            date = date
        )

        try {
            workoutsCollection.document(workout.id).set(workout).await()
            Timber.i("Workout saved to Firestore: ${workout.title}")
        } catch (e: Exception) {
            Timber.e(e, "Error saving workout to Firestore")
        }
    }

    /**
     * Updates an existing workout's details in Firestore.
     */
    suspend fun updateWorkout(workout: Workout) {
        if (workout.id.isEmpty()) return
        try {
            workoutsCollection.document(workout.id).set(workout).await()
            Timber.i("Workout updated in Firestore: ${workout.title}")
        } catch (e: Exception) {
            Timber.e(e, "Error updating workout in Firestore")
        }
    }

    /**
     * Deletes a specific workout from Firestore by its ID.
     */
    suspend fun deleteWorkout(workoutId: String) {
        if (workoutId.isEmpty()) return
        try {
            workoutsCollection.document(workoutId).delete().await()
            Timber.i("Workout deleted from Firestore: $workoutId")
        } catch (e: Exception) {
            Timber.e(e, "Error deleting workout from Firestore")
        }
    }
}
