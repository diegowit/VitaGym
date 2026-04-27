package com.example.vitagym.data.checkin



import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

data class CheckIn(
    val userId: String = "",
    val gymId: String = "",
    val gymName: String = "",
    val timestamp: Date = Date(),
    val checkInId: String = ""
)

class CheckInRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Records a gym check-in for the current user.
     * Returns true if successful, false otherwise.
     */
    suspend fun checkIn(gymId: String, gymName: String): Result<CheckIn> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val checkIn = CheckIn(
                userId = userId,
                gymId = gymId,
                gymName = gymName,
                timestamp = Date(),
                checkInId = firestore.collection("checkins").document().id
            )

            firestore.collection("checkins")
                .document(checkIn.checkInId)
                .set(checkIn)
                .await()

            Result.success(checkIn)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets the user's check-in history, most recent first.
     */
    suspend fun getCheckInHistory(limit: Int = 20): Result<List<CheckIn>> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val snapshot = firestore.collection("checkins")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val checkIns = snapshot.documents.mapNotNull { it.toObject(CheckIn::class.java) }
            Result.success(checkIns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets today's check-ins for the current user.
     */
    suspend fun getTodayCheckIns(): Result<List<CheckIn>> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val todayStart = Date().apply {
                hours = 0
                minutes = 0
                seconds = 0
            }

            val snapshot = firestore.collection("checkins")
                .whereEqualTo("userId", userId)
                .whereGreaterThan("timestamp", todayStart)
                .get()
                .await()

            val checkIns = snapshot.documents.mapNotNull { it.toObject(CheckIn::class.java) }
            Result.success(checkIns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}