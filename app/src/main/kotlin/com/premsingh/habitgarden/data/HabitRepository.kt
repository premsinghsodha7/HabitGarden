package com.premsingh.habitgarden.data

import android.content.Context
import com.premsingh.habitgarden.domain.Habit
import com.premsingh.habitgarden.domain.streakOnCheckIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class HabitRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("habit-garden", Context.MODE_PRIVATE)
    private val today get() = System.currentTimeMillis() / 86_400_000L
    private val state = MutableStateFlow(readHabits())
    val habits = state.asStateFlow()

    fun add(name: String, emoji: String, targetMinutes: Int) {
        if (name.isBlank()) return
        state.value = state.value + Habit(UUID.randomUUID().toString(), name.trim(), emoji, targetMinutes.coerceIn(1, 180), 0, 0, -1)
        persist()
    }

    fun toggleToday(id: String) {
        val day = today
        state.value = state.value.map { habit ->
            if (habit.id != id) habit else if (habit.isComplete(day)) {
                habit.copy(currentStreak = habit.previousStreak, lastCompletedEpochDay = if (habit.previousStreak > 0) day - 1 else -1)
            } else {
                val previous = if (habit.lastCompletedEpochDay == day - 1) habit.currentStreak else 0
                habit.copy(currentStreak = streakOnCheckIn(habit.currentStreak, habit.lastCompletedEpochDay, day), previousStreak = previous, lastCompletedEpochDay = day)
            }
        }
        persist()
    }

    fun delete(id: String) { state.value = state.value.filterNot { it.id == id }; persist() }

    private fun readHabits(): List<Habit> {
        val saved = preferences.getString("habits", null)
        if (saved != null) runCatching {
            val array = JSONArray(saved)
            return (0 until array.length()).map { i -> array.getJSONObject(i).toHabit() }
        }
        val day = today
        return listOf(
            Habit("water", "Drink water", "💧", 5, 3, 2, day),
            Habit("read", "Read a little", "📖", 15, 6, 5, day - 1),
            Habit("walk", "Take a walk", "🌿", 20, 0, 0, -1)
        )
    }

    private fun persist() {
        val array = JSONArray()
        state.value.forEach { habit -> array.put(JSONObject().put("id", habit.id).put("name", habit.name).put("emoji", habit.emoji)
            .put("target", habit.targetMinutes).put("streak", habit.currentStreak).put("previous", habit.previousStreak).put("lastDay", habit.lastCompletedEpochDay)) }
        preferences.edit().putString("habits", array.toString()).apply()
    }

    private fun JSONObject.toHabit() = Habit(getString("id"), getString("name"), getString("emoji"), getInt("target"), getInt("streak"), getInt("previous"), getLong("lastDay"))
}
