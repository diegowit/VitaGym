package com.example.vitagym.util

import android.graphics.PointF
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

object AngleCalculator {
    
    /**
     * Calculate angle between three points
     * @param firstPoint - First point (e.g., shoulder)
     * @param midPoint - Middle point (e.g., elbow) - vertex of angle
     * @param lastPoint - Last point (e.g., wrist)
     * @return Angle in degrees (0-180)
     */
    fun getAngle(firstPoint: PointF, midPoint: PointF, lastPoint: PointF): Double {
        var result = Math.toDegrees(
            (atan2(lastPoint.y - midPoint.y, lastPoint.x - midPoint.x) -
                    atan2(firstPoint.y - midPoint.y, firstPoint.x - midPoint.x)).toDouble()
        )
        
        result = Math.abs(result) // Get absolute value
        
        if (result > 180) {
            result = 360.0 - result // Normalize to 0-180
        }
        
        return result
    }
    
    /**
     * Calculate distance between two points
     */
    fun getDistance(p1: PointF, p2: PointF): Double {
        return sqrt((p2.x - p1.x).pow(2) + (p2.y - p1.y).pow(2)).toDouble()
    }
}
