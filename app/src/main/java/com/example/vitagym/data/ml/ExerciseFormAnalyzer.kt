package com.example.vitagym.data.ml

import com.example.vitagym.util.AngleCalculator

sealed class ExerciseType {
    object Squat : ExerciseType()
    object PushUp : ExerciseType()
    object Plank : ExerciseType()
    object Lunge : ExerciseType()
}

data class FormFeedback(
    val isCorrect: Boolean,
    val feedback: String,
    val repCount: Int = 0
)

class ExerciseFormAnalyzer {
    
    private var repCount = 0
    private var isDown = false  // Track exercise state for rep counting
    
    fun analyzeSquat(pose: PoseAnalysis): FormFeedback {
        val leftHip = pose.leftHip
        val leftKnee = pose.leftKnee
        val leftAnkle = pose.leftAnkle
        
        // Check if all required landmarks are detected
        if (leftHip == null || leftKnee == null || leftAnkle == null) {
            return FormFeedback(false, "Position yourself so your full body is visible")
        }
        
        // Calculate knee angle (hip-knee-ankle)
        val kneeAngle = AngleCalculator.getAngle(leftHip, leftKnee, leftAnkle)
        
        return when {
            kneeAngle > 160 -> {
                // Standing position
                if (isDown) {
                    repCount++
                    isDown = false
                }
                FormFeedback(true, "Good! Now squat down. Reps: $repCount", repCount)
            }
            kneeAngle in 70.0..110.0 -> {
                // Proper squat depth
                isDown = true
                FormFeedback(true, "Perfect depth! Keep your back straight. Reps: $repCount", repCount)
            }
            kneeAngle < 70 -> {
                // Too low
                FormFeedback(false, "Don't go too low! Risk of injury. Reps: $repCount", repCount)
            }
            else -> {
                // In between
                FormFeedback(false, "Go lower for proper squat depth. Reps: $repCount", repCount)
            }
        }
    }
    
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
        
        // Calculate elbow angle (shoulder-elbow-wrist)
        val elbowAngle = AngleCalculator.getAngle(leftShoulder, leftElbow, leftWrist)
        
        // Calculate body alignment angle (shoulder-hip-ankle)
        val bodyAngle = AngleCalculator.getAngle(leftShoulder, leftHip, leftAnkle)
        
        // Check if body is straight (should be close to 180 degrees)
        val isBodyStraight = bodyAngle > 160
        
        return when {
            elbowAngle > 160 -> {
                // Arms extended (up position)
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
                // Proper push-up depth
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
    
    fun analyzePlank(pose: PoseAnalysis): FormFeedback {
        val leftShoulder = pose.leftShoulder
        val leftHip = pose.leftHip
        val leftAnkle = pose.leftAnkle
        
        if (leftShoulder == null || leftHip == null || leftAnkle == null) {
            return FormFeedback(false, "Get into plank position")
        }
        
        // Calculate body alignment
        val bodyAngle = AngleCalculator.getAngle(leftShoulder, leftHip, leftAnkle)
        
        return when {
            bodyAngle > 170 -> {
                FormFeedback(true, "Perfect plank! Keep it up!", repCount)
            }
            bodyAngle < 160 -> {
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
    
    fun resetCounter() {
        repCount = 0
        isDown = false
    }
}
