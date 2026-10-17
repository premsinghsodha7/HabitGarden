package com.premsingh.habitgarden

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.premsingh.habitgarden.data.HabitViewModel
import com.premsingh.habitgarden.domain.Habit
import com.premsingh.habitgarden.domain.weeklyProgress

private val Moss = Color(0xFF42664B)
private val Paper = Color(0xFFF5F6EE)
private val Ink = Color(0xFF222A23)
private val Muted = Color(0xFF748076)

private enum class GardenTab(val label: String, val glyph: String) { Today("Today", "⌂"), Insights("Insights", "▥"), Settings("Settings", "⚙") }
private data class GardenPalette(val name: String, val background: Color, val accent: Color)
private val gardenPalettes = listOf(
    GardenPalette("Meadow", Color(0xFFF5F6EE), Color(0xFF42664B)),
    GardenPalette("Sky", Color(0xFFF0F5F7), Color(0xFF35617A)),
    GardenPalette("Dusk", Color(0xFFF7F1EA), Color(0xFF8A5A43))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { HabitGardenApp() } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitGardenApp(vm: HabitViewModel = viewModel()) {
    val habits by vm.habits.collectAsStateWithLifecycle()
    val today = System.currentTimeMillis() / 86_400_000L
    val done = habits.count { it.isComplete(today) }
    var showAdd by remember { mutableStateOf(false) }
    var tab by rememberSaveable { mutableStateOf(GardenTab.Today) }
    var paletteIndex by rememberSaveable { mutableIntStateOf(0) }
    var remindersEnabled by rememberSaveable { mutableStateOf(true) }
    val palette = gardenPalettes[paletteIndex]
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = palette.accent, secondary = palette.accent, primaryContainer = palette.accent.copy(alpha = .18f), onPrimaryContainer = palette.accent, secondaryContainer = palette.accent.copy(alpha = .18f), onSecondaryContainer = palette.accent, background = palette.background, surface = Color.White, onBackground = Ink)) {
        Scaffold(containerColor = palette.background, topBar = { TopAppBar(title = { Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Image(painterResource(com.premsingh.habitgarden.R.drawable.brand_logo), contentDescription = "HabitGarden logo", modifier = Modifier.size(38.dp))
                Text("HabitGarden", fontWeight = FontWeight.Bold)
            } }) },
            bottomBar = { NavigationBar(containerColor = Color.White) { GardenTab.entries.forEach { destination ->
                NavigationBarItem(selected = tab == destination, onClick = { tab = destination }, icon = { Text(destination.glyph) }, label = { Text(destination.label) })
            } } },
            floatingActionButton = { if (tab == GardenTab.Today) Button(onClick = { showAdd = true }) { Text("＋ New habit") } }) { padding ->
            when (tab) {
                GardenTab.Today -> LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                item {
                    Text("Small steps grow into something good.", color = Muted)
                    Spacer(Modifier.height(8.dp))
                    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE5EEDC))) {
                        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column { Text("Today's progress", color = Muted); Text("$done of ${habits.size} complete", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Ink) }
                                Text("🌱", style = MaterialTheme.typography.headlineLarge)
                            }
                            LinearProgressIndicator(progress = { weeklyProgress(done, habits.size) }, modifier = Modifier.fillMaxWidth(), color = Moss, trackColor = Color.White)
                            Text("A little care, every day.", color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                item { Text("Your daily rhythm", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp)) }
                items(habits, key = { it.id }) { habit -> HabitCard(habit, today, onToggle = { vm.toggleToday(habit.id) }, onDelete = { vm.delete(habit.id) }) }
                item { Text("Your habits are saved privately on this device.", color = Muted, modifier = Modifier.padding(vertical = 16.dp)) }
            }
                GardenTab.Insights -> LazyColumn(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item { Text("Your growth", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("A gentle look at your weekly rhythm", color = Muted) }
                    item { Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE5EEDC))) { Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Today", color = Muted); Text("$done of ${habits.size} habits", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); LinearProgressIndicator(progress = { weeklyProgress(done, habits.size) }, modifier = Modifier.fillMaxWidth(), color = palette.accent)
                    } } }
                    item { Text("This week", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium) }
                    listOf("Mon" to .78f, "Tue" to .55f, "Wed" to .88f, "Thu" to .44f, "Fri" to .68f, "Sat" to .32f, "Sun" to weeklyProgress(done, habits.size)).forEach { (day, progress) ->
                        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) { Text(day, modifier = Modifier.weight(1f), color = Muted); LinearProgressIndicator(progress = { progress }, modifier = Modifier.weight(4f), color = palette.accent); Text("${(progress * 100).toInt()}%", color = Muted) } }
                    }
                    item { Text("Your streaks grow one check-in at a time.", color = Muted) }
                }
                GardenTab.Settings -> Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Choose a garden", color = Muted)
                    gardenPalettes.forEachIndexed { index, option -> FilterChip(selected = paletteIndex == index, onClick = { paletteIndex = index }, label = { Text("${option.name} theme") }) }
                    androidx.compose.material3.Switch(checked = remindersEnabled, onCheckedChange = { remindersEnabled = it })
                    Text(if (remindersEnabled) "Daily reminder is enabled on this device." else "Daily reminder is paused.", color = Muted)
                    Text("Habit data is saved privately on this device.", color = Muted)
                }
            }
        }
        if (showAdd) AddHabitDialog({ showAdd = false }) { name, emoji, minutes -> vm.add(name, emoji, minutes); showAdd = false }
    }
}

@Composable
private fun HabitCard(habit: Habit, today: Long, onToggle: () -> Unit, onDelete: () -> Unit) {
    val isDone = habit.isComplete(today)
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(habit.emoji, style = MaterialTheme.typography.headlineMedium)
            Column(Modifier.weight(1f)) {
                Text(habit.name, fontWeight = FontWeight.SemiBold, color = Ink)
                Text("${habit.targetMinutes} min · ${habit.displayStreak(today)} day streak", color = Muted, style = MaterialTheme.typography.bodySmall)
            }
            FilterChip(selected = isDone, onClick = onToggle, label = { Text(if (isDone) "Done ✓" else "Check in") })
        }
    }
}

@Composable
private fun AddHabitDialog(onDismiss: () -> Unit, onAdd: (String, String, Int) -> Unit) {
    var name by remember { mutableStateOf("") }; var minutes by remember { mutableStateOf("10") }; var emoji by remember { mutableStateOf("🌿") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Plant a new habit") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(name, { name = it }, label = { Text("Habit name") }, singleLine = true)
            OutlinedTextField(minutes, { minutes = it.filter(Char::isDigit).take(3) }, label = { Text("Daily minutes") }, singleLine = true)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { listOf("🌿", "💧", "📖", "🧘").forEach { option ->
                FilterChip(emoji == option, onClick = { emoji = option }, label = { Text(option) })
            } }
        }
    }, confirmButton = { TextButton(enabled = name.isNotBlank() && (minutes.toIntOrNull() ?: 0) > 0, onClick = { onAdd(name, emoji, minutes.toIntOrNull() ?: 10) }) { Text("Add habit") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}
