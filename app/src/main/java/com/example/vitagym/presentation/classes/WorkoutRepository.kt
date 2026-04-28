package com.example.vitagym.presentation.classes

import com.example.vitagym.domain.model.Exercise
import com.example.vitagym.domain.model.Workout
import com.example.vitagym.domain.model.WorkoutType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
 * Repository for managing workout data with Firebase Firestore.
 */
class WorkoutRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Get current user's workouts as a Flow that reacts to auth changes.
     */
    fun getWorkouts(): Flow<List<Workout>> = callbackFlow {
        var listenerRegistration: ListenerRegistration? = null

        // Listen for Auth changes to handle login/logout/re-login
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val userId = firebaseAuth.currentUser?.uid
            
            // Remove previous listener if user changed
            listenerRegistration?.remove()

            if (userId == null) {
                Timber.w("No authenticated user, providing empty list")
                trySend(emptyList())
            } else {
                Timber.d("User authenticated: $userId, attaching Firestore listener")
                listenerRegistration = db.collection("workouts")
                    .whereEqualTo("userId", userId)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Timber.e(error, "Error fetching workouts from Firestore")
                            return@addSnapshotListener
                        }

                        val workouts = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                doc.toObject(Workout::class.java)?.copy(id = doc.id)
                            } catch (e: Exception) {
                                Timber.e(e, "Error parsing workout document: ${doc.id}")
                                null
                            }
                        } ?: emptyList()

                        Timber.d("Successfully fetched ${workouts.size} workouts for user: $userId")
                        trySend(workouts)
                    }
            }
        }

        auth.addAuthStateListener(authListener)

        // Clean up listeners when the flow is closed
        awaitClose {
            auth.removeAuthStateListener(authListener)
            listenerRegistration?.remove()
            Timber.d("Workout Flow closed, listeners removed")
        }
    }

    suspend fun addWorkout(title: String, duration: Int, date: Long) {
        val userId = auth.currentUser?.uid ?: return
        val workout = Workout(
            userId = userId,
            title = title,
            duration = duration,
            date = date,
            workoutType = WorkoutType.CUSTOM
        )
        try {
            db.collection("workouts").add(workout).await()
            Timber.i("Custom workout saved: $title")
        } catch (e: Exception) {
            Timber.e(e, "Error saving custom workout")
        }
    }

    suspend fun addAIWorkout(
        title: String,
        duration: Int,
        date: Long,
        exercises: List<Exercise>,
        totalReps: Int,
        workoutType: WorkoutType
    ) {
        val userId = auth.currentUser?.uid ?: return
        val workout = Workout(
            userId = userId,
            title = title,
            duration = duration,
            date = date,
            exercises = exercises,
            totalReps = totalReps,
            workoutType = workoutType
        )
        try {
            db.collection("workouts").add(workout).await()
            Timber.i("AI workout saved: $title")
        } catch (e: Exception) {
            Timber.e(e, "Error saving AI workout")
        }
    }

    suspend fun updateWorkout(workout: Workout) {
        if (workout.id.isEmpty()) return
        try {
            db.collection("workouts").document(workout.id).set(workout).await()
        } catch (e: Exception) {
            Timber.e(e, "Error updating workout")
        }
    }

    suspend fun deleteWorkout(workoutId: String) {
        try {
            db.collection("workouts").document(workoutId).delete().await()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting workout")
        }
    }
}
