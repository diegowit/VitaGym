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

class WorkoutRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val workoutsCollection = firestore.collection("workouts")

    fun getWorkouts(): Flow<List<Workout>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: ""
        if (userId.isEmpty()) {
            trySend(emptyList())
            return@callbackFlow
        }

        val subscription = workoutsCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error fetching workouts")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val workouts = snapshot.toObjects(Workout::class.java)
                    trySend(workouts)
                }
            }

        awaitClose { subscription.remove() }
    }

    suspend fun addWorkout(title: String, duration: Int) {
        val userId = auth.currentUser?.uid ?: return
        val workout = Workout(
            id = workoutsCollection.document().id,
            userId = userId,
            title = title,
            duration = duration,
            date = System.currentTimeMillis()
        )

        try {
            workoutsCollection.document(workout.id).set(workout).await()
            Timber.i("Workout saved to Firestore: ${workout.title}")
        } catch (e: Exception) {
            Timber.e(e, "Error saving workout to Firestore")
        }
    }

    suspend fun updateWorkout(workout: Workout) {
        if (workout.id.isEmpty()) return
        try {
            workoutsCollection.document(workout.id).set(workout).await()
            Timber.i("Workout updated in Firestore: ${workout.title}")
        } catch (e: Exception) {
            Timber.e(e, "Error updating workout in Firestore")
        }
    }

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
