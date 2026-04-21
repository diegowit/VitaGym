package com.example.vitagym.presentation.classes

import com.example.vitagym.domain.model.Workout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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
     * Emits the current Firebase user whenever the auth state changes.
     */
    private fun authStateFlow(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /**
     * Streams the list of workouts for the current user.
     * Reacts to login/logout events automatically.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getWorkouts(): Flow<List<Workout>> = authStateFlow().flatMapLatest { userId ->
        if (userId == null) {
            Timber.w("getWorkouts: No user logged in.")
            return@flatMapLatest flowOf(emptyList())
        }

        callbackFlow {
            val query = workoutsCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)

            val subscription = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Firestore: Error fetching workouts for user $userId")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val workouts = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Workout::class.java)?.copy(id = doc.id)
                    }
                    Timber.d("Firestore: Synced ${workouts.size} workouts for user $userId")
                    trySend(workouts)
                }
            }

            awaitClose {
                subscription.remove()
                Timber.d("Firestore workout subscription removed for user $userId")
            }
        }
    }

    suspend fun addWorkout(title: String, duration: Int, date: Long) {
        val userId = auth.currentUser?.uid ?: run {
            Timber.e("Firestore: Cannot add workout - no user logged in")
            return
        }
        
        val docRef = workoutsCollection.document()
        val workout = Workout(
            id = docRef.id, 
            userId = userId,
            title = title,
            duration = duration,
            date = date
        )

        try {
            docRef.set(workout).await()
            Timber.i("Firestore: Workout saved: ${workout.title} for user $userId")
        } catch (e: Exception) {
            Timber.e(e, "Firestore: Error saving workout")
        }
    }

    suspend fun updateWorkout(workout: Workout) {
        if (workout.id.isBlank()) {
            Timber.e("Firestore: Cannot update workout - missing ID")
            return
        }

        val userId = auth.currentUser?.uid ?: run {
            Timber.e("Firestore: Cannot update workout - no user logged in")
            return
        }

        // Ensure the workout being updated still belongs to the current user
        val workoutToSave = workout.copy(userId = userId)

        try {
            workoutsCollection.document(workout.id).set(workoutToSave).await()
            Timber.i("Firestore: Workout updated: ${workout.title} (ID: ${workout.id})")
        } catch (e: Exception) {
            Timber.e(e, "Firestore: Error updating workout ${workout.id}")
        }
    }

    suspend fun deleteWorkout(workoutId: String) {
        if (workoutId.isBlank()) {
            Timber.e("Firestore: Cannot delete workout - missing ID")
            return
        }
        try {
            workoutsCollection.document(workoutId).delete().await()
            Timber.i("Firestore: Workout deleted: $workoutId")
        } catch (e: Exception) {
            Timber.e(e, "Firestore: Error deleting workout $workoutId")
        }
    }
}
