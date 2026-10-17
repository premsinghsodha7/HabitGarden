package com.premsingh.habitgarden.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HabitRepository(application)
    val habits = repository.habits
    fun add(name: String, emoji: String, targetMinutes: Int) = repository.add(name, emoji, targetMinutes)
    fun toggleToday(id: String) = repository.toggleToday(id)
    fun delete(id: String) = repository.delete(id)
}
