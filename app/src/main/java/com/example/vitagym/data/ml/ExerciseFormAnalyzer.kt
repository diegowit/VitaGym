package com.example.vitagym.data.ml

import com.example.vitagym.util.AngleCalculator

/**
 * Supported exercise types for AI analysis.
 */
sealed class ExerciseType {
    object Squat : ExerciseType()
    object PushUp : ExerciseType()
    object Plank : ExerciseType()
    object Lunge : ExerciseType()
}

/**
 * Data model for providing real-time feedback and counting repetitions.
 * 
 * @property isCorrect True if the current form is within acceptable parameters.
 * @property feedback Human-readable instruction for the user.
 * @property repCount The number of successful repetitions completed.
 */
data class FormFeedback(
    val isCorrect: Boolean,
    val feedback: String,
    val repCount: Int = 0
)

/**
 * Core logic for analyzing human poses to detect specific exercises and provide form correction.
 * Uses trigonometry to calculate joint angles and state machines for rep counting.
 */
class ExerciseFormAnalyzer {
    
    private var repCount = 0
    private var isDown = false  // State tracker for rep counting (up vs down position)
    
    /**
     * Analyzes squat form using hip, knee, and ankle landmarks.
     */
    fun analyzeSquat(pose: PoseAnalysis): FormFeedback {
        val leftHip = pose.leftHip
        val leftKnee = pose.leftKnee
        val leftAnkle = pose.leftAnkle
        
        // Ensure user is properly positioned in frame
        if (leftHip == null || leftKnee == null || leftAnkle == null) {
            return FormFeedback(false, "Position yourself so your full body is visible")
        }
        
        // Calculate the interior angle at the knee
        val kneeAngle = AngleCalculator.getAngle(leftHip, leftKnee, leftAnkle)
        
        return when {
            kneeAngle > 160 -> {
                // Standing / Up position
                if (isDown) {
                    repCount++ // Successful rep completed
                    isDown = false
                }
                FormFeedback(true, "Good! Now squat down. Reps: $repCount", repCount)
            }
            kneeAngle in 70.0..110.0 -> {
                // Deep Squat / Down position
                isDown = true
                FormFeedback(true, "Perfect depth! Keep your back straight. Reps: $repCount", repCount)
            }
            kneeAngle < 70 -> {
                // Warning: Squatting too low can stress the knees
                FormFeedback(false, "Don't go too low! Risk of injury. Reps: $repCount", repCount)
            }
            else -> {
                // Transitional state
                FormFeedback(false, "Go lower for proper squat depth. Reps: $repCount", repCount)
            }
        }
    }
    
    /**
     * Analyzes push-up form using shoulder, elbow, wrist, hip, and ankle landmarks.
     * Validates both arm depth and overall body straightness (core engagement).
     */
    fun analyzePushUp(pose: PoseAnalysis): FormFeedback {
        val leftShoulder = pose.leftShoulder
        val leftElbow = pose.leftElbow
        val leftWrist = pose.leftWrist
        val leftHip = pose.leftHip
        val leftAnkle = pose.leftAnkle
        
        if (leftShoulder == null || leftElbow == null || leftWrist == null || 
            leftHip == null || leftAnkle == null) {
            return FormFeedback(false, "Position yourself in push-up stance")
        }
        
        // Calculate arm depth and core alignment
        val elbowAngle = AngleCalculator.getAngle(leftShoulder, leftElbow, leftWrist)
        val bodyAngle = AngleCalculator.getAngle(leftShoulder, leftHip, leftAnkle)
        
        // Body should be close to a straight line (180 deg)
        val isBodyStraight = bodyAngle > 160
        
        return when {
            elbowAngle > 160 -> {
                // Arms locked out (Up position)
                if (isDown && isBodyStraight) {
                    repCount++
                    isDown = false
                }
                if (!isBodyStraight) {
                    FormFeedback(false, "Keep your body straight! Don't sag hips. Reps: $repCount", repCount)
                } else {
                    FormFeedback(true, "Good! Now lower down. Reps: $repCount", repCount)
                }
            }
            elbowAngle in 70.0..100.0 -> {
                // Chest near ground (Down position)
                isDown = true
                if (!isBodyStraight) {
                    FormFeedback(false, "Good depth but keep body straight! Reps: $repCount", repCount)
                } else {
                    FormFeedback(true, "Perfect form! Push back up. Reps: $repCount", repCount)
                }
            }
            else -> {
                FormFeedback(false, "Lower your chest more. Reps: $repCount", repCount)
            }
        }
    }
    
    /**
     * Analyzes static plank position, focusing on core alignment and hip height.
     */
    fun analyzePlank(pose: PoseAnalysis): FormFeedback {
        val leftShoulder = pose.leftShoulder
        val leftHip = pose.leftHip
        val leftAnkle = pose.leftAnkle
        
        if (leftShoulder == null || leftHip == null || leftAnkle == null) {
            return FormFeedback(false, "Get into plank position")
        }
        
        // Static core alignment check
        val bodyAngle = AngleCalculator.getAngle(leftShoulder, leftHip, leftAnkle)
        
        return when {
            bodyAngle > 170 -> {
                FormFeedback(true, "Perfect plank! Keep it up!", repCount)
            }
            bodyAngle < 160 -> {
                // Differentiate between "piking" (hips high) and "sagging" (hips low)
                if (leftHip.y > leftShoulder.y + 50) {
                    FormFeedback(false, "Hips too high! Lower them.", repCount)
                } else {
                    FormFeedback(false, "Hips sagging! Engage your core.", repCount)
                }
            }
            else -> {
                FormFeedback(true, "Good! Try to straighten your body more.", repCount)
            }
        }
    }
    
    /**
     * Resets rep counting logic. Use this when switching exercise types.
     */
    fun resetCounter() {
        repCount = 0
        isDown = false
    }
}
