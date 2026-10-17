package com.premsingh.habitgarden.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class HabitRulesTest {
    @Test fun checkInContinuesOnlyAConsecutiveStreak() {
        assertEquals(5, streakOnCheckIn(4, 99, 100))
        assertEquals(1, streakOnCheckIn(4, 98, 100))
        assertEquals(4, streakOnCheckIn(4, 100, 100))
    }

    @Test fun progressIsSafeForEmptyAndCompleteLists() {
        assertEquals(0f, weeklyProgress(0, 0))
        assertEquals(0.5f, weeklyProgress(2, 4))
        assertEquals(1f, weeklyProgress(4, 4))
    }
}
