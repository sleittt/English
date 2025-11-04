package com.example.bebeka.logik

object PointsCalculator {
    private var successStreak = 0

    fun calculatePoints(isCorrect: Boolean): Double {
        return if (isCorrect) {
            successStreak++
            val bonus = if (successStreak >= 2) 0.2 * successStreak else 0.0
            1.0 + bonus
        } else {
            successStreak = 0
            0.0
        }
    }

    fun resetStreak() {
        successStreak = 0
    }
}
