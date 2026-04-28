package com.example.vitagym.data.ml

import android.graphics.PointF
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlinx.coroutines.tasks.await

/**
 * Manages the ML Kit Pose Detection lifecycle and processing.
 * Configured for real-time stream processing using the Accurate Pose Detector model.
 */
class PoseDetectorManager {
    
    // Configure detector for stream mode to maintain state across frames
    private val options = AccuratePoseDetectorOptions.Builder()
        .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
        .build()
    
    private val poseDetector: PoseDetector = PoseDetection.getClient(options)
    
    /**
     * Processes an InputImage to detect human pose landmarks.
     * 
     * @param image The image frame from the camera.
     * @return A Pose object containing detected landmarks, or null if detection fails.
     */
    suspend fun detectPose(image: InputImage): Pose? {
        return try {
            poseDetector.process(image).await()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Releases the detector resources. Must be called when the feature is disposed.
     */
    fun close() {
        poseDetector.close()
    }
}

/**
 * Simplified data structure containing relevant joint positions for exercise analysis.
 */
data class PoseAnalysis(
    val leftShoulder: PointF?,
    val rightShoulder: PointF?,
    val leftElbow: PointF?,
    val rightElbow: PointF?,
    val leftWrist: PointF?,
    val rightWrist: PointF?,
    val leftHip: PointF?,
    val rightHip: PointF?,
    val leftKnee: PointF?,
    val rightKnee: PointF?,
    val leftAnkle: PointF?,
    val rightAnkle: PointF?,
    val allLandmarks: List<PoseLandmark>
)

/**
 * Extension function to map ML Kit's complex Pose object to our simpler PoseAnalysis structure.
 */
fun Pose.toPoseAnalysis(): PoseAnalysis {
    return PoseAnalysis(
        leftShoulder = getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position,
        rightShoulder = getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position,
        leftElbow = getPoseLandmark(PoseLandmark.LEFT_ELBOW)?.position,
        rightElbow = getPoseLandmark(PoseLandmark.RIGHT_ELBOW)?.position,
        leftWrist = getPoseLandmark(PoseLandmark.LEFT_WRIST)?.position,
        rightWrist = getPoseLandmark(PoseLandmark.RIGHT_WRIST)?.position,
        leftHip = getPoseLandmark(PoseLandmark.LEFT_HIP)?.position,
        rightHip = getPoseLandmark(PoseLandmark.RIGHT_HIP)?.position,
        leftKnee = getPoseLandmark(PoseLandmark.LEFT_KNEE)?.position,
        rightKnee = getPoseLandmark(PoseLandmark.RIGHT_KNEE)?.position,
        leftAnkle = getPoseLandmark(PoseLandmark.LEFT_ANKLE)?.position,
        rightAnkle = getPoseLandmark(PoseLandmark.RIGHT_ANKLE)?.position,
        allLandmarks = allPoseLandmarks
    )
}
