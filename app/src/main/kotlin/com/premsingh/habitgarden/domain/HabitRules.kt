package com.premsingh.habitgarden.domain

data class Habit(
    val id: String,
    val name: String,
    val emoji: String,
    val targetMinutes: Int,
    val currentStreak: Int,
    val previousStreak: Int,
    val lastCompletedEpochDay: Long
) {
    fun isComplete(today: Long): Boolean = lastCompletedEpochDay == today
    fun displayStreak(today: Long): Int = if (lastCompletedEpochDay >= today - 1) currentStreak else 0
}

fun streakOnCheckIn(currentStreak: Int, lastCompletedEpochDay: Long, todayEpochDay: Long): Int =
    if (lastCompletedEpochDay == todayEpochDay - 1) currentStreak + 1 else if (lastCompletedEpochDay == todayEpochDay) currentStreak else 1

fun weeklyProgress(completedCount: Int, habitCount: Int): Float =
    if (habitCount <= 0) 0f else (completedCount.toFloat() / habitCount).coerceIn(0f, 1f)
